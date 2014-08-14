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

import org.ofbiz.accounting.tax.TaxAuthorityServices;
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
		decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
		rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

		// set zero to the proper scale
		if (decimals != -1) ZERO = ZERO.setScale(decimals);
	}

	public static List<GenericValue> getByProductProducts(DispatchContext dctx, Map<String, ? extends Object> context) {
		Timestamp salesDate = UtilDateTime.nowTimestamp();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		if (!UtilValidate.isEmpty(context.get("salesDate"))) {
			salesDate = (Timestamp) context.get("salesDate");
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(salesDate);
		String productCategoryId = (String) context.get("productCategoryId");
		if (UtilValidate.isEmpty(productCategoryId)) {
			productCategoryId = "MILK_MILKPRODUCTS";
		}
		List<GenericValue> productList = FastList.newInstance();
		/*List condList =FastList.newInstance();
		  condList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS, "MILK_MILKPRODUCTS"));
		  condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
		  EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN, dayBegin))); 
		 EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND); 
		 try{
		 productList =delegator.findList("ProductAndCategoryMember",discontinuationDateCondition,null, null, null, false);
		 }catch(GenericEntityException e) { 
		 	// TODO: handle exception
		  Debug.logError(e, module); 
		 } */
		productList = ProductWorker.getProductsByCategory(delegator,productCategoryId, dayBegin);
		return productList;
	}
	/*
	  * Helper that returns the   all products which are configured as prodcuts not yet closed
	  * 
	  * 
	  */
	public static List<GenericValue> getAllProducts(DispatchContext dctx, Map<String, ? extends Object> context) {
		Timestamp salesDate = UtilDateTime.nowTimestamp();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		if (!UtilValidate.isEmpty(context.get("salesDate"))) {
			salesDate = (Timestamp) context.get("salesDate");
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(salesDate);
		List<GenericValue> productList = FastList.newInstance();
		List condList = FastList.newInstance();
		condList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, "_NA_"));
		condList.add(EntityCondition.makeCondition("isVirtual",EntityOperator.NOT_EQUAL, "Y"));
		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS, null), EntityOperator.OR,
				EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN, dayBegin)));
		EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
		try {
			productList = delegator.findList("Product",	discontinuationDateCondition, null, null, null, false);
		} catch (GenericEntityException e) {
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
	/* public static Map getProductCategoryMap(Delegator delegator, String categoryId)  {
		 Map productCatMap = FastMap.newInstance();
		 
   	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
   	 Timestamp dayBegin =UtilDateTime.getDayEnd(salesDate);
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
	 }*/

	public static Map getProductCategoryMap(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map productCatMap = FastMap.newInstance();
		Timestamp salesDate = UtilDateTime.nowTimestamp();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String productCategoryId = (String) context.get("productCategoryId");
		if (!UtilValidate.isEmpty(context.get("salesDate"))) {
			salesDate = (Timestamp) context.get("salesDate");
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(salesDate);
		List<GenericValue> productList = FastList.newInstance();

		if (UtilValidate.isNotEmpty(productCategoryId)) {
			List condList = FastList.newInstance();
			productList = ProductWorker.getProductsByCategory(delegator,productCategoryId, dayBegin);
			for (int i = 0; i < productList.size(); ++i) {
				productCatMap.put(productList.get(i).get("productId"),productList.get(i));
			}
		}

		return productCatMap;
	}

	public static Map getByProductFacilityCategoryAndClassification(Delegator delegator, Timestamp effectiveDate) {

		Map facilityDetail = FastMap.newInstance();
		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		List<GenericValue> partyClassification = FastList.newInstance();
		List<GenericValue> facilityList = FastList.newInstance();
		try {
			// facilityList = delegator.findList("Facility",EntityCondition.makeCondition("byProdRouteId", EntityOperator.NOT_EQUAL, null), null, null, null, false);
			facilityList = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"BOOTH"), null, null, null, false);
			if (UtilValidate.isNotEmpty(facilityList)) {
				for (int i = 0; i < facilityList.size(); i++) {
					Map facilityTemp = FastMap.newInstance();
					GenericValue facility = facilityList.get(i);
					String facilityId = facility.getString("facilityId");
					String category = facility.getString("categoryTypeEnum");
					String ownerPartyId = facility.getString("ownerPartyId");
					if (UtilValidate.isNotEmpty(ownerPartyId)) {
						List condList = FastList.newInstance();
						condList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, ownerPartyId));
						EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
						partyClassification = delegator.findList("PartyClassification", condition,UtilMisc.toSet("partyClassificationGroupId"),	null, null, false);
						partyClassification = EntityUtil.filterByDate(partyClassification, effectiveDate);
						if (UtilValidate.isNotEmpty(partyClassification)) {
							facilityTemp.put("ownerPartyId", ownerPartyId);
							facilityTemp.put("categoryTypeEnum", category);
							String classificationId = EntityUtil.getFirst(partyClassification).getString("partyClassificationGroupId");
							facilityTemp.put("partyClassification",classificationId);
							facilityDetail.put(facilityId, facilityTemp);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		return facilityDetail;
	}

	public static Map getByProductSales(DispatchContext dctx,Timestamp fromDate, Timestamp thruDate, String salesChannel,String categoryTypeEnum, List productList, List facilityList) {

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
		int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1);
		List orderedItems = FastList.newInstance();
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.NOT_IN,UtilMisc.toList("ORDER_CANCELLED", "ORDER_REJECTED")));
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipmentList));
		if (UtilValidate.isNotEmpty(categoryTypeEnum)) {
			conditionList.add(EntityCondition.makeCondition("categoryTypeEnum",	EntityOperator.EQUALS, categoryTypeEnum));
		}
		if (UtilValidate.isNotEmpty(productList)) {
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN,productList)));
		}
		if (UtilValidate.isNotEmpty(facilityList)) {
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.IN,facilityList)));
		}
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			orderedItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition,UtilMisc.toSet("estimatedDeliveryDate", "quantity","productId", "originFacilityId", "shipmentId","categoryTypeEnum"), null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		Map totalProductQuant = FastMap.newInstance();
		List daywiseSales = FastList.newInstance();
		Map daySale = FastMap.newInstance();
		for (int k = 0; k < intervalDays; k++) {

			List condList = FastList.newInstance();
			Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
			Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
			Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
			List dayWiseShipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, dayStart, dayEnd);

			List<GenericValue> daywiseParlourOrders = EntityUtil.filterByCondition(orderedItems, EntityCondition.makeCondition("shipmentId", EntityOperator.IN,dayWiseShipmentList));
			List tempDayPartyList = FastList.newInstance();
			tempDayPartyList = EntityUtil.getFieldListFromEntityList(daywiseParlourOrders, "originFacilityId", true);
			List dayPartyList = FastList.newInstance();
			for (int j = 0; j < tempDayPartyList.size(); j++) {
				String booth = (String) tempDayPartyList.get(j);
				booth = booth.toUpperCase();
				if (!dayPartyList.contains(booth)) {
					dayPartyList.add(booth);
				}
			}
			Map partySale = FastMap.newInstance();
			String boothId = "";
			if (UtilValidate.isNotEmpty(dayPartyList)) {
				for (int i = 0; i < dayPartyList.size(); i++) {
					boothId = (String) dayPartyList.get(i);
					Map productQuant = FastMap.newInstance();
					List daywiseBoothwiseSale = EntityUtil.filterByCondition(daywiseParlourOrders, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("originFacilityId"),EntityOperator.EQUALS,EntityFunction.UPPER(((String) boothId).toUpperCase())));
					for (int j = 0; j < daywiseBoothwiseSale.size(); j++) {
						GenericValue eachOrderItem = (GenericValue) daywiseBoothwiseSale.get(j);
						String productId = eachOrderItem.getString("productId");
						BigDecimal quantity = eachOrderItem.getBigDecimal("quantity");
						if (productQuant.containsKey(productId)) {
							BigDecimal tempQty = (BigDecimal) productQuant.get(productId);
							BigDecimal totalQty = tempQty.add(quantity);
							productQuant.put(productId, totalQty);
						} else {
							productQuant.put(productId, quantity);
						}
						if (totalProductQuant.containsKey(productId)) {
							BigDecimal tempTotQty = (BigDecimal) totalProductQuant.get(productId);
							BigDecimal totalQuant = tempTotQty.add(quantity);
							totalProductQuant.put(productId, totalQuant);
						} 
						else {
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

	public static Map getDayWiseTotalSales(DispatchContext dctx,Timestamp fromDate, Timestamp thruDate, String salesChannel,List productList) {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		Map result = FastMap.newInstance();
		int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1);
		List orderedItems = FastList.newInstance();
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
		conditionList.add(EntityCondition.makeCondition("salesChannelEnumId",EntityOperator.EQUALS, "BYPROD_SALES_CHANNEL"));
		if (UtilValidate.isNotEmpty(productList)) {
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, productList));
		}
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO,	thruDate));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			orderedItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, UtilMisc.toSet("estimatedDeliveryDate", "quantity","productId", "originFacilityId", "ownerPartyId"),null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		List shipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
		// List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator,fromDate, thruDate, "BYPRODUCTS");
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipmentList));
		conditionList.add(EntityCondition.makeCondition("isCancelled",EntityOperator.EQUALS, null));
		if (UtilValidate.isNotEmpty(productList)) {
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, productList));
		}
		conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS,"CASH_BYPROD"));
		EntityCondition shipReceiptCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<GenericValue> shipReceiptList = FastList.newInstance();
		try {
			shipReceiptList = delegator.findList("ShipmentReceiptAndItem",shipReceiptCond, UtilMisc.toSet("facilityId", "productId","datetimeReceived", "quantityAccepted"), UtilMisc.toList("productId"), null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}

		/* ==============GET PRICES BASED ON PARTY CLASSIFICATION============= */
		Map classificationMap = FastMap.newInstance();

		List<GenericValue> partyClassificationList = null;
		try {
			partyClassificationList = delegator.findList("PartyClassificationGroup", EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS,"PM_RC"), UtilMisc.toSet("partyClassificationGroupId"), null, null,false);
		} catch (GenericEntityException e) {
			Debug.logError("Unable to get records from PartyClassificationGroup" + e,module);
			return ServiceUtil.returnError("Unable to get records from PartyClassificationGroup");
		}
		List partyClassifications = EntityUtil.getFieldListFromEntityList(partyClassificationList, "partyClassificationGroupId", true);
		for (int i = 0; i < partyClassifications.size(); i++) {
			Map productsPrice = (Map) ByProductReportServices.getByProductPricesForPartyClassification(dctx,UtilMisc.toMap("partyClassificationId",partyClassifications.get(i))).get("productsPrice");
			classificationMap.put(partyClassifications.get(i), productsPrice);
		}
		/* ==============END============= */
		Map dayWiseSale = FastMap.newInstance();

		Map totalSalesMap = FastMap.newInstance();

		BigDecimal totalBasicPrice = BigDecimal.ZERO;
		BigDecimal grandTotalPrice = BigDecimal.ZERO;
		BigDecimal totalVatAmt = BigDecimal.ZERO;
		BigDecimal totalExDuty = BigDecimal.ZERO;
		BigDecimal totalEdCess = BigDecimal.ZERO;
		BigDecimal totalHigherSecCess = BigDecimal.ZERO;
		BigDecimal grandTotalExcise = BigDecimal.ZERO;

		for (int k = 0; k < intervalDays; k++) {

			Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
			Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
			Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);

			List<GenericValue> daywiseParlourOrders = EntityUtil.filterByCondition(orderedItems, EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate",EntityOperator.GREATER_THAN_EQUAL_TO,dayStart), EntityOperator.AND,
									EntityCondition.makeCondition("estimatedDeliveryDate",EntityOperator.LESS_THAN_EQUAL_TO,dayEnd)));

			List<GenericValue> daywiseShipmentReceipts = EntityUtil.filterByCondition(shipReceiptList, EntityCondition.makeCondition(EntityCondition.makeCondition("datetimeReceived",EntityOperator.GREATER_THAN_EQUAL_TO,dayStart), EntityOperator.AND,
									EntityCondition.makeCondition("datetimeReceived",EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd)));

			Map salesMap = FastMap.newInstance();

			BigDecimal basicPrice = BigDecimal.ZERO;
			BigDecimal totalPrice = BigDecimal.ZERO;

			BigDecimal exDutyPrecent = BigDecimal.ZERO;
			BigDecimal edCessPrecent = BigDecimal.ZERO;
			BigDecimal higherSecCessPercent = BigDecimal.ZERO;

			for (int i = 0; i < daywiseShipmentReceipts.size(); i++) {

				GenericValue eachReceiptItem = (GenericValue) daywiseShipmentReceipts.get(i);

				String productId = eachReceiptItem.getString("productId");
				BigDecimal quantity = eachReceiptItem.getBigDecimal("quantityAccepted");
				// String ownerPartyId = eachOrderItem.getString("ownerPartyId");

				Map priceMap = (Map) classificationMap.get("PM_RC_P");
				Map prodPriceMap = (Map) priceMap.get(productId);

				BigDecimal basicValue = ((BigDecimal) prodPriceMap.get("basicPrice")).multiply(quantity);
				BigDecimal totalValue = ((BigDecimal) prodPriceMap.get("totalAmount")).multiply(quantity);

				exDutyPrecent = (BigDecimal) prodPriceMap.get("bedPercentage");
				edCessPrecent = (BigDecimal) prodPriceMap.get("bedCessPercent");
				higherSecCessPercent = (BigDecimal) prodPriceMap.get("bedsecPercent");

				basicPrice = basicPrice.add(basicValue);
				totalPrice = totalPrice.add(totalValue);

				totalBasicPrice = totalBasicPrice.add(basicValue);
				grandTotalPrice = grandTotalPrice.add(totalValue);

			}

			for (int i = 0; i < daywiseParlourOrders.size(); i++) {

				GenericValue eachOrderItem = (GenericValue) daywiseParlourOrders.get(i);

				String productId = eachOrderItem.getString("productId");
				BigDecimal quantity = eachOrderItem.getBigDecimal("quantity");
				String ownerPartyId = eachOrderItem.getString("ownerPartyId");

				String partyClassificationTypeId = null;
				List<GenericValue> partyClassification = null;
				try {
					List<GenericValue> partyClassificationGroup = delegator.findList("PartyClassification",EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,ownerPartyId), null, null,	null, false);
					if (UtilValidate.isNotEmpty(partyClassificationGroup)) {
						partyClassificationTypeId = (String) EntityUtil.getFirst(partyClassificationGroup).get("partyClassificationGroupId");
					}
				} catch (GenericEntityException e) {
					Debug.logError("No partyRole found for given partyId:"+ ownerPartyId, module);
					return ServiceUtil.returnError("No partyRole found for given partyId");
				}

				Map priceMap = (Map) classificationMap.get(partyClassificationTypeId);
				Map prodPriceMap = (Map) priceMap.get(productId);

				BigDecimal basicValue = ((BigDecimal) prodPriceMap.get("basicPrice")).multiply(quantity);
				BigDecimal totalValue = ((BigDecimal) prodPriceMap.get("totalAmount")).multiply(quantity);

				exDutyPrecent = (BigDecimal) prodPriceMap.get("bedPercentage");
				edCessPrecent = (BigDecimal) prodPriceMap.get("bedCessPercent");
				higherSecCessPercent = (BigDecimal) prodPriceMap.get("bedsecPercent");

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

	 /*public static Map getByProductSubscriptionId(Delegator delegator, String facilityId)  {
	 
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
	}*/

	public static List getByProdShipmentIds(Delegator delegator,Timestamp fromDate, Timestamp thruDate) {
		List shipments = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate, null);
		return shipments;
	}

	public static List getByProdShipmentIds(Delegator delegator,Timestamp fromDate, Timestamp thruDate, List routeIds) {

		List conditionList = FastList.newInstance();
		List shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		Timestamp dayBegin = UtilDateTime.nowTimestamp();
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		try {
			List<GenericValue> shipmentTypeIds = delegator.findList("ShipmentType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("LMS_SHIPMENT", "LMS_SHIPMENT_SUPPL")),null, null, null, false);
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN, EntityUtil.getFieldListFromEntityList(shipmentTypeIds, "shipmentTypeId", true)));
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}

		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "GENERATED"));
		if (UtilValidate.isNotEmpty(routeIds)) {
			conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.IN, routeIds));
		}
		if (!UtilValidate.isEmpty(fromDate)) {
			dayBegin = UtilDateTime.getDayStart(fromDate);
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		}

		conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}
		if (!UtilValidate.isEmpty(shipmentList)) {
			shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		}

		return shipments;
	}

	public static List getAllShipmentIds(Delegator delegator,Timestamp fromDate, Timestamp thruDate) {
		List conditionList = FastList.newInstance();
		List shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		Timestamp dayBegin = UtilDateTime.nowTimestamp();
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		try {
			List<GenericValue> shipmentTypeIds = delegator.findList("ShipmentType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("LMS_SHIPMENT", "LMS_SHIPMENT_SUPPL","DIRECT_SHIPMENT")), null, null, null,false);
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN, EntityUtil.getFieldListFromEntityList(shipmentTypeIds, "shipmentTypeId", true)));
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}

		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "GENERATED"));

		if (!UtilValidate.isEmpty(fromDate)) {
			dayBegin = UtilDateTime.getDayStart(fromDate);
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		}

		conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}
		if (!UtilValidate.isEmpty(shipmentList)) {
			shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		}

		return shipments;
	}

	public static List getDayShipmentIds(Delegator delegator,Timestamp fromDate, Timestamp thruDate, String shipmentTypeId,List routeIds) {
		// TO DO:for now getting one shipment id we need to get pm and am shipment id irrespective of Shipment type Id
		List conditionList = FastList.newInstance();
		List shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		if (UtilValidate.isNotEmpty(shipmentTypeId)	&& ("AM".equals(shipmentTypeId))) {
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN, UtilMisc.toList("AM_SHIPMENT")));
			if (UtilValidate.isNotEmpty(routeIds)) {
				conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN, routeIds));
			}
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Unable to get shipment list: ", module);
			}
			if (!UtilValidate.isEmpty(shipmentList)) {
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}
		} 
		else if (UtilValidate.isNotEmpty(shipmentTypeId) && ("PM".equals(shipmentTypeId))) {
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN, UtilMisc.toList("PM_SHIPMENT")));
			if (UtilValidate.isNotEmpty(routeIds)) {
				conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.IN, routeIds));
			}
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.addDaysToTimestamp(dayBegin, -1)));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	UtilDateTime.addDaysToTimestamp(dayEnd, -1)));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Unable to get shipment list: ", module);
			}
			if (!UtilValidate.isEmpty(shipmentList)) {
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}
		} else {
			conditionList.clear();
			// conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS , shipmentTypeId));
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Unable to get shipment list: ", module);
			}
			shipments = EntityUtil.getFieldListFromEntityList(shipmentList,	"shipmentId", false);
		}
		return shipments;
	}

	/*public static Map<String, Object> getByProductActiveFacilities(Delegator delegator, Timestamp effectiveDate){
    
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
	}*/

	public static List getByProdShipmentIdsByType(Delegator delegator,Timestamp fromDate, Timestamp thruDate, String shipmentTypeId) {
		List shipments = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, fromDate, thruDate, shipmentTypeId, null);
		return shipments;
	}

	public static List getByProdShipmentIdsByType(Delegator delegator,Timestamp fromDate, Timestamp thruDate, String shipmentTypeId, List routeIds) {

		List conditionList = FastList.newInstance();
		List shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		Timestamp dayBegin = UtilDateTime.nowTimestamp();
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS, shipmentTypeId));
		conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
		if (UtilValidate.isNotEmpty(routeIds)) {
			conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.IN, routeIds));
		}
		if (!UtilValidate.isEmpty(fromDate)) {
			dayBegin = UtilDateTime.getDayStart(fromDate);
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		}

		conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}
		if (!UtilValidate.isEmpty(shipmentList)) {
			shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		}

		return shipments;
	}

	public static Map<String, Object> getFacilityFieldStaff(DispatchContext dctx, Map<String, ? extends Object> context) {

		Map<String, Object> result = FastMap.newInstance();
		List conditionList = FastList.newInstance();
		List<String> boothIds = FastList.newInstance();
		List<GenericValue> facilityPartyList = null;
		Timestamp saleDate = UtilDateTime.nowTimestamp();
		Delegator delegator = dctx.getDelegator();
		Map<String, String> facilityFieldStaffMap = FastMap.newInstance();
		Map<String, List> fieldStaffAndFacility = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(context.get("saleDate"))) {
			saleDate = (Timestamp) context.get("saleDate");
		}

		conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, "FIELD_STAFF"));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			facilityPartyList = delegator.findList("FacilityFacilityPartyAndPerson", condition, null, null,null, false);
			facilityPartyList = EntityUtil.filterByDate(facilityPartyList,saleDate);
			for (GenericValue facilityParty : facilityPartyList) {
				List tempFacilityList = FastList.newInstance();
				if (facilityParty.getString("facilityTypeId").equals("ROUTE")) {
					tempFacilityList = getBoothList(delegator,facilityParty.getString("facilityId"));
				}
				tempFacilityList.add(facilityParty.getString("facilityId"));
				for (int i = 0; i < tempFacilityList.size(); i++) {
					String tempFacilityId = (String) tempFacilityList.get(i);
					facilityFieldStaffMap.put(tempFacilityId,facilityParty.getString("partyId"));
					if (UtilValidate.isNotEmpty(fieldStaffAndFacility.get(facilityParty.getString("partyId")))) {
						List facilityList = fieldStaffAndFacility.get(facilityParty.getString("partyId"));
						facilityList.add(tempFacilityId);
						fieldStaffAndFacility.put(facilityParty.getString("partyId"),facilityList);
					} else {
						fieldStaffAndFacility.put(facilityParty.getString("partyId"),UtilMisc.toList(tempFacilityId));
					}
				}

			}

		} catch (Exception e) {
			Debug.logError(e, "Exception while getting FaclityParty ", module);
		}
		result.put("facilityFieldStaffMap", facilityFieldStaffMap);
		result.put("fieldStaffAndFacility", fieldStaffAndFacility);
		return result;
	}

	public static Map<String, Object> getProductPricesByDate(Delegator delegator, LocalDispatcher dispatcher,Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		DispatchContext dctx = dispatcher.getDispatchContext();
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
			currencyDefaultUomId = UtilProperties.getPropertyValue("general","currency.uom.id.default", "INR");
		}
		List<GenericValue> indentProductList = FastList.newInstance();
		if (context.get("productsList") != null) {
			indentProductList = (List<GenericValue>) context.get("productsList");
		} else {
			indentProductList = ProductWorker.getProductsByCategory(delegator,"INDENT", null);
		}

		List productIdsList = EntityUtil.getFieldListFromEntityList(indentProductList, "productId", false);

		/*Timestamp dayBegin =UtilDateTime.getDayStart(fromDate);
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
       productIdsList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);*/
		if (UtilValidate.isNotEmpty(productIdsList)) {
			for (int i = 0; i < productIdsList.size(); i++) {
				String eachProd = (String) productIdsList.get(i);
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
					Debug.logError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult),module);
					return ServiceUtil.returnError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult));
				}
				prodPriceMap.put(eachProd, priceResult.get("totalPrice"));
			}
		}
		result.put("priceMap", prodPriceMap);
		return result;
	}
	
	public static Map<String, Object> getStoreProductPricesByDate(Delegator delegator, LocalDispatcher dispatcher,Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		DispatchContext dctx = dispatcher.getDispatchContext();
		Map<String, Object> priceByDateMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productStoreId = (String) context.get("productStoreId");
		String partyId = (String) context.get("partyId");
		String productCategoryId = (String) context.get("productCategoryId");
		String geoTax = (String) context.get("geoTax");
		Timestamp priceDate = (Timestamp) context.get("priceDate");
		Map prodPriceMap = FastMap.newInstance();
		if (UtilValidate.isEmpty(priceDate)) {
			priceDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		String productPriceTypeId = (String) context.get("productPriceTypeId");
		List conditionList = FastList.newInstance();
		if (UtilValidate.isEmpty(productPriceTypeId)) {
	    	try{
	    		conditionList.clear();
	    		conditionList.add(EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "PRICE_TYPE"));
	    		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	    		EntityCondition expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		
	    		List<GenericValue> partyClassifications = delegator.findList("PartyClassificationAndGroupAndType", expr, null, null, null, false);
	    		partyClassifications = EntityUtil.filterByDate(partyClassifications, priceDate);
	    		if(UtilValidate.isNotEmpty(partyClassifications)){
	    			productPriceTypeId = (EntityUtil.getFirst(partyClassifications)).getString("partyClassificationGroupId");
	    		}
	    		else{
	    			productPriceTypeId = "DEFAULT_PRICE";
	    		}
	    	}
	    	catch (GenericEntityException e) {
				Debug.logError(e, e.toString(), module);
		        return ServiceUtil.returnError(e.toString());
			}
		}
		if(UtilValidate.isEmpty(geoTax)){
			geoTax="VAT";
		}
		String currencyDefaultUomId = (String) context.get("currencyUomId");
		if (UtilValidate.isEmpty(currencyDefaultUomId)) {
			currencyDefaultUomId = UtilProperties.getPropertyValue("general","currency.uom.id.default", "INR");
		}
		
		List<GenericValue> indentProductList = ProductWorker.getProductsByCategory(delegator, productCategoryId, null);

		List productIdsList = EntityUtil.getFieldListFromEntityList(indentProductList, "productId", false);

		if (UtilValidate.isNotEmpty(productIdsList)) {
			for (int i = 0; i < productIdsList.size(); i++) {
				String eachProd = (String) productIdsList.get(i);
				Map<String, Object> priceContext = FastMap.newInstance();
				priceContext.put("userLogin", userLogin);
				priceContext.put("productStoreId", productStoreId);
				priceContext.put("productId", eachProd);
				priceContext.put("priceDate", priceDate);
				priceContext.put("productPriceTypeId", productPriceTypeId);
				priceContext.put("partyId", partyId);
				priceContext.put("geoTax", geoTax);
				Map priceResult = calculateStoreProductPrices(delegator, dispatcher, priceContext);
				if (ServiceUtil.isError(priceResult)) {
					Debug.logError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult),module);
					return ServiceUtil.returnError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult));
				}
				prodPriceMap.put(eachProd, priceResult.get("totalPrice"));
			}
		}
		result.put("priceMap", prodPriceMap);
		return result;
	}

	public static Map<String, Object> getBoothChandentIndent(DispatchContext dctx, Map<String, ? extends Object> context) {
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
		if (UtilValidate.isNotEmpty(context.get("isEnableProductSubscription"))) {
			isEnableProductSubscription = (Boolean) context.get("isEnableProductSubscription");
		}
		Map result = ServiceUtil.returnSuccess();
		List changeIndentProductList = FastList.newInstance();
		List changeQtyList = FastList.newInstance();
		Map<String, BigDecimal> prevQuantityMap = FastMap.newInstance();
		/*List<GenericValue> contIndentProducts = FastList.newInstance();
        List<GenericValue> dayIndentProducts = FastList.newInstance();
        List<String> crateIndentProductList = FastList.newInstance();
        List<String> packetIndentProductList = FastList.newInstance();
        */
		Map prodIndentQtyCat = FastMap.newInstance();
		Map qtyInPieces = FastMap.newInstance();
		List productList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		Map prodPriceMap = FastMap.newInstance();
		String productStoreId = (String) (ByProductServices.getByprodFactoryStore(delegator)).get("factoryStoreId");
		String partyId = "";
		String facilityCategory = "";
		Timestamp supplyDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
		if (UtilValidate.isNotEmpty(supplyDateStr)) {
			try {
				supplyDate = new java.sql.Timestamp(sdf.parse(supplyDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + supplyDateStr,	module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + supplyDateStr,	module);
			}
		} 
		else {
			supplyDate = UtilDateTime.nowTimestamp();
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(supplyDate);
		Timestamp dayEnd = UtilDateTime.getDayStart(supplyDate);
		BigDecimal totalAmount = BigDecimal.ZERO;
		try {

			if (UtilValidate.isNotEmpty(routeId)) {
				GenericValue facilityRoute = delegator.findOne("Facility",UtilMisc.toMap("facilityId", routeId), true);
				if (UtilValidate.isEmpty(facilityRoute)) {
					Debug.logError("Route doesn't exists with Id: " + routeId,module);
					return ServiceUtil.returnError("Route doesn't exists with Id: "+ routeId);
				}

				Map resultCtx = getRoutesByAMPM(dctx, UtilMisc.toMap("supplyType", subscriptionTypeId, "userLogin",	userLogin));
				List routeIds = (List) resultCtx.get("routeIdsList");

				if (UtilValidate.isEmpty(routeIds) || !routeIds.contains(routeId)) {
					Debug.logError("Route doesn't exists in "+ subscriptionTypeId + " shipping", module);
					return ServiceUtil.returnError("Route doesn't exists in "+ subscriptionTypeId + " shipping");
				}

			}
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), false);
			if (UtilValidate.isEmpty(facility)) {
				Debug.logError("Invalid  Booth Id", module);
				return ServiceUtil.returnError("Invalid  Booth Id");
			}

			boolean isActive = EntityUtil.isValueActive(facility, dayBegin,	"openedDate", "closedDate");
			if (!isActive) {
				Debug.logError("is not active facility " + boothId, module);
				return ServiceUtil.returnError("The  facility ' " + boothId	+ "' is not Active.");
			}
			if (UtilValidate.isNotEmpty(routeId)) {
				Map permanentBoothCtxMap = FastMap.newInstance();
				permanentBoothCtxMap.putAll(context);
				permanentBoothCtxMap.put("supplyDate", supplyDate);
				Map boothDetails = (Map) (getBoothRoute(dctx,permanentBoothCtxMap)).get("boothDetails");
				String perRouteId = (String) boothDetails.get("routeId");
				if (UtilValidate.isNotEmpty(perRouteId)	&& !(perRouteId.equals(routeId))) {
					Debug.logError(routeId+ " is not permanent route of retailer "+ boothId	+ " . Use Route Shift to temporarly shift the route",module);
					return ServiceUtil.returnError(routeId+ " is not permanent route of retailer "+ boothId	+ " . Use Route Shift to temporarly shift the route");
				}
			}
			partyId = facility.getString("ownerPartyId");
			facilityCategory = facility.getString("categoryTypeEnum");
			// lets override productSubscriptionTypeId based on facility category

			if (UtilValidate.isEmpty(routeId)) {
				Map boothCtxMap = FastMap.newInstance();
				boothCtxMap.putAll(context);
				boothCtxMap.put("supplyDate", supplyDate);
				Map boothDetails = (Map) (getBoothRoute(dctx, boothCtxMap)).get("boothDetails");

				if (UtilValidate.isEmpty(boothDetails) || UtilValidate.isEmpty(boothDetails.get("routeId"))) {
					Debug.logError("No Permanent Route exists for : "+ subscriptionTypeId, module);
					return ServiceUtil.returnError("No Permanent Route exists for : "+ subscriptionTypeId);
				}
				routeId = (String) boothDetails.get("routeId");
			}
			/*List prevshipmentIds = getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(supplyDate, "yyyy-MM-dd HH:mm:ss"),subscriptionTypeId, routeId);
    		if(UtilValidate.isNotEmpty(prevshipmentIds)){
    			return ServiceUtil.returnError("Trucksheet already generated for the route :"+ routeId); 
    		}*/
			List genRouteIds = ByProductNetworkServices.getShipedRouteIdsByAMPM(delegator, UtilDateTime.toDateString(supplyDate, "yyyy-MM-dd HH:mm:ss"),subscriptionTypeId, null);
			if (!isEnableProductSubscription) {
				productSubscriptionTypeId = "CASH";
				if (facility.getString("categoryTypeEnum").equals("SO_INST")) {
					productSubscriptionTypeId = "SPECIAL_ORDER";
				}
				if (facility.getString("categoryTypeEnum").equals("CR_INST")) {
					productSubscriptionTypeId = "CREDIT";
				}
			}
			BigDecimal securityDeposit = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(facility.get("securityDeposit"))) {
				securityDeposit = facility.getBigDecimal("securityDeposit");
			}
			result.put("securityDeposit", securityDeposit);
			result.put("boothName", facility.getString("facilityName"));
			Map inputCtx = FastMap.newInstance();
			inputCtx.put("userLogin", userLogin);
			inputCtx.put("supplyDate", dayBegin);
			inputCtx.put("facilityId", boothId);
			Boolean priceCalcFalg = Boolean.TRUE;

			if (UtilValidate.isNotEmpty(context.get("priceCalcFalg"))) {
				priceCalcFalg = (Boolean) context.get("priceCalcFalg");
			}
			if (priceCalcFalg) {
				Map qtyResultMap = getFacilityIndentQtyCategories(dctx,	inputCtx);
				prodIndentQtyCat = (Map) qtyResultMap.get("indentQtyCategory");
				qtyInPieces = (Map) qtyResultMap.get("qtyInPieces");
				result.put("prodIndentQtyCat", prodIndentQtyCat);
				result.put("qtyInPieces", qtyInPieces);

				Map inputProductRate = FastMap.newInstance();
				inputProductRate.put("productStoreId", productStoreId);
				inputProductRate.put("fromDate", dayBegin);
				inputProductRate.put("facilityId", boothId);
				inputProductRate.put("partyId", partyId);
				inputProductRate.put("facilityCategory", facilityCategory);
				inputProductRate.put("userLogin", userLogin);
				Map priceResultMap = getProductPricesByDate(delegator,dctx.getDispatcher(), inputProductRate);
				prodPriceMap = (Map) priceResultMap.get("priceMap");
				result.put("productPrice", prodPriceMap);

			}
			result.put("supplyDate", supplyDate);
			/*contIndentProducts = ProductWorker.getProductsByCategory(delegator ,"CONTINUES_INDENT" ,UtilDateTime.getDayStart(supplyDate));
    		dayIndentProducts = ProductWorker.getProductsByCategory(delegator ,"DAILY_INDENT" ,UtilDateTime.getDayStart(supplyDate));
    		
    		crateIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"CRATE_INDENT" ,UtilDateTime.getDayStart(supplyDate)), "productId", true);
    		packetIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"PACKET_INDENT" ,UtilDateTime.getDayStart(supplyDate)), "productId", true);
    		*/
			conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, boothId));
			if (subscriptionTypeId.equals("AM")) {
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId",	EntityOperator.EQUALS, subscriptionTypeId),EntityOperator.OR, EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS,null)));
			} else {
				conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS,subscriptionTypeId));
			}
			if (UtilValidate.isNotEmpty(tripId)) {
				result.put("tripId", tripId);
				conditionList.add(EntityCondition.makeCondition("tripNum",EntityOperator.EQUALS, tripId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> subscriptionList = EntityUtil.filterByDate(delegator.findList("Subscription", condition, null, null,	null, false), UtilDateTime.addDaysToTimestamp(dayBegin, -1));
			if (UtilValidate.isEmpty(subscriptionList)) {
				Debug.logError("No Active Data For given Booth", module);
				return ServiceUtil.returnError("No Active Data For given Booth");

			}
			result.put("routeId", routeId);
			result.put("tempRouteId", routeId);
			String subscriptionId = (EntityUtil.getFirst(subscriptionList)).getString("subscriptionId");

			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("subscriptionId",EntityOperator.EQUALS, subscriptionId));
			conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS,productSubscriptionTypeId));
			if (UtilValidate.isNotEmpty(screenFlag)	&& !screenFlag.equals("indentAlt")) {
				conditionList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS, routeId));
			} else {
				if (UtilValidate.isNotEmpty(genRouteIds) && UtilValidate.isEmpty(context.get("fetchForSms"))) {
					conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.NOT_IN, genRouteIds));
				}

			}
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN,UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayBegin, -1))),EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
			EntityCondition cond = EntityCondition.makeCondition(conditionList,	EntityOperator.AND);
			List<GenericValue> tempSubProdList = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
			tempSubProdList = EntityUtil.filterByDate(tempSubProdList, dayBegin);
			List<GenericValue> subProdList = FastList.newInstance();

			if (UtilValidate.isNotEmpty(tempSubProdList)) {
				productList.addAll(EntityUtil.getFieldListFromEntityList(tempSubProdList, "productId", true));
			}
			List<String> orderBy = UtilMisc.toList("-sequenceNum");
			productList = delegator.findList("Product",	EntityCondition.makeCondition("productId",EntityOperator.IN, productList), null, orderBy,null, true);
			List productListIds = EntityUtil.getFieldListFromEntityList(productList, "productId", true);

			for (int j = 0; j < productListIds.size(); j++) {
				String productId = (String) productListIds.get(j);
				List<GenericValue> prodSubList = EntityUtil.filterByCondition(tempSubProdList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				subProdList.addAll(prodSubList);
			}
			subProdList = EntityUtil.orderBy(subProdList,UtilMisc.toList("-sequenceNum"));
			String qtyCategory = "";
			for (GenericValue product : subProdList) {
				Map changeQuantityMap = FastMap.newInstance();
				// result.put("tempRouteId", product.getString("sequenceNum"));
				String productId = product.getString("productId");
				if (UtilValidate.isNotEmpty(prodIndentQtyCat)) {
					qtyCategory = (String) prodIndentQtyCat.get(productId);
				}
				if (UtilValidate.isNotEmpty(screenFlag) && screenFlag.equals("indentAlt")) {
					Map tempMap = FastMap.newInstance();
					tempMap.put("quantity", product.getBigDecimal("quantity"));
					tempMap.put("seqRouteId", product.getString("sequenceNum"));
					changeQuantityMap.put(productId, tempMap);
				} 
				else {
					changeQuantityMap.put(productId,product.getBigDecimal("quantity"));
				}
				changeQtyList.add(changeQuantityMap);

			}

			// here fetch employee subsidy quantity and add amount to total amount
			Map empSubIndent = FastMap.newInstance();
			if (UtilValidate.isNotEmpty(context.get("fetchForSms"))	&& ((Boolean) context.get("fetchForSms"))) {
				BigDecimal subPercent = new BigDecimal("50");
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS,subscriptionId));
				conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS,"EMP_SUBSIDY"));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN,UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayBegin, -1))),EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
				EntityCondition condEmp = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> tempEmpSubProdList = delegator.findList("SubscriptionProduct", condEmp, null, null,null, false);
				tempEmpSubProdList = EntityUtil.filterByDate(tempEmpSubProdList, dayBegin);
				for (GenericValue product : tempEmpSubProdList) {
					String productId = product.getString("productId");
					BigDecimal quantity = product.getBigDecimal("quantity");
					/*if(UtilValidate.isNotEmpty(empSubIndent.get("productId"))){
    				quantity = quantity.add((BigDecimal)empSubIndent.get("productId"));
            		}
    				empSubIndent.put("productId",quantity);*/

					// lets popultate total indent qty Map
					Map<String, Object> priceContext = FastMap.newInstance();
					priceContext.put("userLogin", userLogin);
					priceContext.put("productStoreId", productStoreId);
					priceContext.put("productId", productId);
					priceContext.put("facilityId", boothId);
					priceContext.put("priceDate", dayEnd);
					priceContext.put("productSubscriptionTypeId", "EMP_SUBSIDY");
					Map priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher,priceContext);
					if (ServiceUtil.isError(priceResult)) {
						Debug.logError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult),module);
						return ServiceUtil.returnError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult));
					}
					BigDecimal defaultPrice = (BigDecimal) priceResult.get("totalPrice");
					BigDecimal subAmount = BigDecimal.ZERO;
					BigDecimal marginAmount = (defaultPrice).subtract((BigDecimal) prodPriceMap.get(productId));
					BigDecimal itemSubAmt = ((defaultPrice).multiply(subPercent.divide(new BigDecimal("100")))).subtract(marginAmount);
					subAmount = (subAmount.add(itemSubAmt)).multiply(quantity);
					totalAmount = totalAmount.add(subAmount);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.toString() + "context ====" + context, module);
			return ServiceUtil.returnError(e.toString());
		}
		Map totalIndentQtyMap = FastMap.newInstance();

		for (int i = (changeQtyList.size() - 1); i >= 0; i--) {
			Map tempChangeProdMap = FastMap.newInstance();
			Map changeQuantityMap = (Map) changeQtyList.get(i);
			Iterator tempIter = changeQuantityMap.entrySet().iterator();
			String productId = "";
			while (tempIter.hasNext()) {
				Map.Entry tempEntry = (Entry) tempIter.next();
				productId = (String) tempEntry.getKey();
			}
			GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(productList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS,productId)));
			/*if(UtilValidate.isNotEmpty(dayIndentProducts) &&  (dayIndentProducts.size() > i)){
    		dayIndentProduct = dayIndentProducts.remove(i);        	
    		}
    		if(UtilValidate.isNotEmpty(contIndentProducts) && (contIndentProducts.size() >i)){
    		conIndentProduct = contIndentProducts.remove(i);
    		
    		}*/

			/* if(UtilValidate.isNotEmpty(conIndentProduct)){ */

			tempChangeProdMap.put("id", productId);
			tempChangeProdMap.put("cProductId", productId);
			tempChangeProdMap.put("cProductName",product.getString("brandName") + " [ "+ product.getString("description") + "]");
			tempChangeProdMap.put("cQuantity", "");
			if (UtilValidate.isNotEmpty(screenFlag)	&& screenFlag.equals("indentAlt")) {
				tempChangeProdMap.put("seqRouteId", "");
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
			if (UtilValidate.isNotEmpty(changeQuantityMap.get(productId))) {
				BigDecimal quantity = BigDecimal.ZERO;
				if (UtilValidate.isNotEmpty(screenFlag) && screenFlag.equals("indentAlt")) {
					quantity = (BigDecimal) (((Map) changeQuantityMap.get(productId)).get("quantity"));
					tempChangeProdMap.put("cQuantity", quantity);
					tempChangeProdMap.put("seqRouteId",	(String) ((Map) changeQuantityMap.get(productId)).get("seqRouteId"));
				} 
				else {
					quantity = (BigDecimal) (changeQuantityMap.get(productId));
					tempChangeProdMap.put("cQuantity", quantity);
				}

				// lets popultate total indent qty Map
				if (UtilValidate.isNotEmpty(prodPriceMap.get(productId))) {
					totalAmount = totalAmount.add(((BigDecimal) prodPriceMap.get(productId)).multiply(quantity));
				}
				if (UtilValidate.isEmpty(totalIndentQtyMap.get(productId))) {
					totalIndentQtyMap.put(productId, quantity);

				} else {
					totalIndentQtyMap.put(productId, quantity.add((BigDecimal) totalIndentQtyMap.get(productId)));
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
		// Collections.reverse(changeIndentProductList);
		// Collections.sort(changeIndentProductList);
		String tempRouteId = routeId;
		if (UtilValidate.isNotEmpty(screenFlag)	&& screenFlag.equals("indentAlt")) {
			tempRouteId = "";
		}
		/*Map boothDetailResult = getRouteCrateDetails(dctx, UtilMisc.toMap("userLogin", userLogin, "facilityId", boothId,"supplyDate", dayBegin, "subscriptionTypeId", subscriptionTypeId, "tripId", tripId, "routeId", tempRouteId));
    	List routeTotalList = (List)boothDetailResult.get("routeTotalList");
    	result.put("routeTotalsList", routeTotalList);*/

		result.put("totalAmount", totalAmount);

		// lets populate route totals
		try {
			/*conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
    		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayBegin, -1))) , EntityOperator.OR ,EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null) ));
    		if(UtilValidate.isNotEmpty(tripId)){
    			conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.LESS_THAN_EQUAL_TO, tripId));
    		}
    		EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		  
    		List<GenericValue> subProdList =delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condExpr, null,null, null, false);
    		
    		subProdList = EntityUtil.filterByDate(subProdList , dayBegin);
    		 Hard coded the categories ... get the few categories by type for indent totals
    		
    		result.put("routeCapacity", BigDecimal.ZERO);
    		*/
    		GenericValue route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
			result.put("routeName", route.getString("facilityName"));
			result.put("routeCapacity", route.getBigDecimal("facilitySize"));
    		
			/*List condProdCatList = UtilMisc.toList("MILK","CURD", "FMILK_CATEGORY");
    		
    		conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, condProdCatList));
    		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(subProdList, "productId", true)));
    		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		List<GenericValue> productCategoryList = delegator.findList("ProductAndCategoryMember", cond, null, null, null, true);
    		*/
    		
    		/*BigDecimal routeCrateTotal = BigDecimal.ZERO;
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
    		*/
    	
    		
    		/*BigDecimal totalMilkInLtrs = BigDecimal.ZERO;
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
    		result.put("categoryTotals", categoryList);*/
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
		}
		//changeIndentProductList = UtilMisc.sortMaps(changeIndentProductList ,UtilMisc.toList("cPrevQuantity","cProductName" ,"dProductName"));
		result.put("totalIndentQtyMap", totalIndentQtyMap);
		result.put("changeIndentProductList", changeIndentProductList);
		return result;
	}

	public static Map<String, Object> getRetailerIndent(DispatchContext dctx,Map<String, ? extends Object> context) {
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
		boolean isCreditInstitution = Boolean.FALSE;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		if (UtilValidate.isNotEmpty(context.get("isEnableProductSubscription"))) {
			isEnableProductSubscription = (Boolean) context.get("isEnableProductSubscription");
		}
		Map result = ServiceUtil.returnSuccess();
		String PONumber = "";
		Timestamp supplyDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
		if (UtilValidate.isNotEmpty(supplyDateStr)) {
			try {
				supplyDate = new java.sql.Timestamp(sdf.parse(supplyDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + supplyDateStr,	module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + supplyDateStr,	module);
			}
		} 
		else {
			supplyDate = UtilDateTime.nowTimestamp();
		}
		GenericValue facility = null;
		try {
			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), false);
			if (UtilValidate.isNotEmpty(facility) && (facility.get("categoryTypeEnum")).equals("CR_INST")) {
				isCreditInstitution = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error in fetching facility " + boothId, module);
			return ServiceUtil.returnError(e.toString());
		}
		if (isCreditInstitution) {
			String subscriptionId = "";
			List subCondList = FastList.newInstance();
			subCondList.add(EntityCondition.makeCondition("subscriptionTypeId",	EntityOperator.EQUALS, subscriptionTypeId));
			subCondList.add(EntityCondition.makeCondition("facilityId",	EntityOperator.EQUALS, boothId));
			EntityCondition subCond = EntityCondition.makeCondition(subCondList, EntityOperator.AND);
			try {
				List<GenericValue> subscriptions = delegator.findList("Subscription", subCond,UtilMisc.toSet("subscriptionId"), null, null, false);
				if (UtilValidate.isNotEmpty(subscriptions)) {
					GenericValue subscription = EntityUtil.getFirst(subscriptions);
					subscriptionId = subscription.getString("subscriptionId");
				}
				GenericValue subscriptionAttr = delegator.findOne("SubscriptionAttribute", UtilMisc.toMap("subscriptionId", subscriptionId, "attrName",	"PO_NUMBER"), false);
				if (UtilValidate.isNotEmpty(subscriptionAttr)) {
					PONumber = subscriptionAttr.getString("attrValue");
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, "Error in fetching PO Number" + boothId,module);
				return ServiceUtil.returnError(e.toString());
			}

		}
		result.put("isCreditInstitution", isCreditInstitution);
		result.put("PONumber", PONumber);
		List changeIndentProductList = FastList.newInstance();

		try {
			result = getBoothChandentIndent(dctx, context);

			changeIndentProductList = (List) result.get("changeIndentProductList");

			if (UtilValidate.isEmpty(changeIndentProductList)) {
				EntityFindOptions opts = new EntityFindOptions();
				opts.setMaxRows(1);
				opts.setFetchSize(1);
				// GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", boothId), false);

				productSubscriptionTypeId = "CASH";
				if (facility.getString("categoryTypeEnum").equals("SO_INST")) {
					productSubscriptionTypeId = "SPECIAL_ORDER";
				}
				if (facility.getString("categoryTypeEnum").equals("CR_INST")) {
					productSubscriptionTypeId = "CREDIT";
				}
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS,subscriptionTypeId));
				conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, boothId));
				conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS,	productSubscriptionTypeId));
				conditionList.add(EntityCondition.makeCondition("thruDate",	EntityOperator.LESS_THAN, supplyDate));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> previousSubscriptionProductList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct",condition, UtilMisc.toSet("thruDate"),	UtilMisc.toList("-thruDate"), opts, false);
				GenericValue prevSubscriptionProduct = (GenericValue) EntityUtil.getFirst(previousSubscriptionProductList);
				String prevDate = "";
				if (UtilValidate.isNotEmpty(prevSubscriptionProduct)) {
					List changedIndentProducts = FastList.newInstance();
					Timestamp previousDate = prevSubscriptionProduct.getTimestamp("thruDate");
					prevDate = UtilDateTime.toDateString(previousDate,"dd MMMMM, yyyy");
					Map newContext = FastMap.newInstance();
					newContext.put("boothId", boothId);
					newContext.put("routeId", routeId);
					newContext.put("screenFlag", screenFlag);
					newContext.put("tripId", tripId);
					newContext.put("productSubscriptionTypeId",productSubscriptionTypeId);
					newContext.put("subscriptionTypeId", subscriptionTypeId);
					newContext.put("userLogin", userLogin);
					newContext.put("isEnableProductSubscription",(Boolean) context.get("isEnableProductSubscription"));
					newContext.put("supplyDate", prevDate);
					newContext.put("priceCalcFalg", Boolean.FALSE);
					result.put("changeIndentProductList", changedIndentProducts);
					result.put("isCreditInstitution", isCreditInstitution);
					result.put("PONumber", PONumber);
					Map resultCtx = getBoothChandentIndent(dctx, newContext);
					if(ServiceUtil.isError(resultCtx)){
						Debug.logError(ServiceUtil.getErrorMessage(resultCtx), module);
						return result;
					}
					List tempProdQtyList = (List) resultCtx.get("changeIndentProductList");
					for (int i = 0; i < tempProdQtyList.size(); i++) {
						Map tempMap = (Map) tempProdQtyList.get(i);
						tempMap.put("cQuantity", BigDecimal.ZERO);
						changedIndentProducts.add(tempMap);
					}
					result.put("changeIndentProductList", changedIndentProducts);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Error in fetching indent for the date"+ supplyDateStr, module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("isCreditInstitution", isCreditInstitution);
		result.put("PONumber", PONumber);
		return result;
	}

	public static Map<String, Object> getRouteCrateDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String subscriptionTypeId = (String) context.get("subscriptionTypeId");
		String tripId = (String) context.get("tripId");
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		String facilityId = (String) context.get("facilityId");
		String routeId = (String) context.get("routeId");
		List conditionList = FastList.newInstance();
		if (UtilValidate.isEmpty(supplyDate)) {
			supplyDate = UtilDateTime.nowTimestamp();
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(supplyDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
		List routeTotalList = FastList.newInstance();
		try {
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
			conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS,subscriptionTypeId));
			if (UtilValidate.isNotEmpty(tripId)) {
				conditionList.add(EntityCondition.makeCondition("tripNum",EntityOperator.EQUALS, tripId));
			}
			EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> subscriptionList = delegator.findList("Subscription", condExpr, UtilMisc.toSet("subscriptionId"),null, null, false);

			if (UtilValidate.isEmpty(subscriptionList)) {
				Debug.logError("No Active subscription found for the retailer: "+ facilityId, module);
				return ServiceUtil.returnError("No Active subscription found for the retailer: "+ facilityId);
			}
			String subscriptionId = ((GenericValue) EntityUtil.getFirst(subscriptionList)).getString("subscriptionId");

			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("subscriptionId",EntityOperator.EQUALS, subscriptionId));
			if (UtilValidate.isNotEmpty(routeId)) {
				conditionList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS, routeId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> subProdList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition,null, null, null, false);

			subProdList = EntityUtil.filterByDate(subProdList, dayBegin);

			List<String> routesListIds = EntityUtil.getFieldListFromEntityList(subProdList, "sequenceNum", true);

			List<GenericValue> routes = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",EntityOperator.IN, routesListIds), null, null,	null, false);

			List<GenericValue> crateProductsList = ProductWorker.getProductsByCategory(delegator, "CRATE", null);
			// List<GenericValue> canProductsList=ProductWorker.getProductsByCategory(delegator ,"CAN" ,null);

			List crateProductsIdsList = EntityUtil.getFieldListFromEntityList(crateProductsList, "productId", false);
			// List canProductsIdsList=EntityUtil.getFieldListFromEntityList(canProductsList, "productId", false);

			Map resultQtyMap = (Map) getProductCratesAndCans(dctx,UtilMisc.toMap("userLogin", userLogin));
			Map cratesMap = (Map) resultQtyMap.get("piecesPerCrate");
			Map cansMap = (Map) resultQtyMap.get("piecesPerCan");

			for (String routeListId : routesListIds) {
				List<GenericValue> routeProducts = EntityUtil.filterByCondition(subProdList, EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS, routeListId));
				BigDecimal crateTotal = BigDecimal.ZERO;
				// BigDecimal canTotal = BigDecimal.ZERO;

				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS, routeListId));
				conditionList.add(EntityCondition.makeCondition("fromDate",	EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd),	EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
				EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> routeTotalIndents = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", cond,UtilMisc.toSet("crateQuantity"), null, null, false);
				BigDecimal routeLoad = BigDecimal.ZERO;
				for (GenericValue routeIndent : routeTotalIndents) {
					BigDecimal crateQty = routeIndent.getBigDecimal("crateQuantity");
					if (UtilValidate.isNotEmpty(crateQty)) {
						routeLoad = routeLoad.add(crateQty);
					}

				}

				Map routeTotalMap = FastMap.newInstance();
				List<GenericValue> route = EntityUtil.filterByCondition(routes,EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, routeListId));
				BigDecimal routeCapacity = BigDecimal.ZERO;
				if (UtilValidate.isNotEmpty(((GenericValue) EntityUtil.getFirst(route)).getBigDecimal("facilitySize"))) {
					routeCapacity = ((GenericValue) EntityUtil.getFirst(route)).getBigDecimal("facilitySize");
				}
				for (GenericValue routeProduct : routeProducts) {
					String productId = routeProduct.getString("productId");
					BigDecimal qty = routeProduct.getBigDecimal("quantity");
					if (crateProductsIdsList.contains(productId) && UtilValidate.isNotEmpty(cratesMap) && UtilValidate.isNotEmpty(cratesMap.get(productId))) {
						BigDecimal diviserCrateValue = (BigDecimal) cratesMap.get(productId);
						BigDecimal crateQuantity = qty.divide(diviserCrateValue, 2, rounding);
						crateTotal = crateTotal.add(crateQuantity);
					}

				}
				routeTotalMap.put("routeId", routeListId);
				routeTotalMap.put("retailerIndentCrate", crateTotal);
				routeTotalMap.put("routeLoad", routeLoad);
				// routeTotalMap.put("retailerIndentCan", canTotal);
				routeTotalMap.put("routeCapacity", routeCapacity);
				routeTotalList.add(routeTotalMap);
			}
		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
		}
		result.put("routeTotalList", routeTotalList);
		return result;
	}

	public static Map getOpeningBalanceForBooth(DispatchContext dctx,Map<String, ? extends Object> context) {
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
		shipmentIds = new HashSet(getShipmentIds(delegator, null, saleDate));
		Timestamp dayBegin = UtilDateTime.getDayStart(saleDate);
		List categoryTypeEnumList = UtilMisc.toList("SO_INST", "CR_INST");
		boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}

		boolean enableSoCrPmntTrack = Boolean.FALSE;
		List prevPmInvIdList = FastList.newInstance();
		try {
			GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName","enableSoCrPmntTrack"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
				enableSoCrPmntTrack = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		if (enableSoCrPmntTrack) {
			// exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
			exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId",EntityOperator.EQUALS, "CASH"), EntityOperator.OR,EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.IN, categoryTypeEnumList)));
		} else {
			// exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH") , EntityOperator.OR , EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, categoryTypeEnumList)));
			exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,UtilMisc.toList("CASH", "EMP_SUBSIDY")));		
		}
		exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_EQUAL, "CARD"));

		if (facilityId != null) {
			try {
				GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
				if (facilityDetail == null	|| (!facilityDetail.getString("facilityTypeId").equals("BOOTH"))) {
					Debug.logInfo("facilityId '" + facilityId+ "'is not a Booth or Zone ", "");
					return ServiceUtil.returnError("facilityId '" + facilityId+ "'is not a Booth or Zone ");
				}
				facilityIds = FastList.newInstance();
				facilityIds.add(facilityId);
				// exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}
		}
		List ownerPartyIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(facilityIds) && isByParty) {
			try {
				List<GenericValue> ownerPartyList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(ownerPartyList, "ownerPartyId", true);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		}
		if (isByParty) {
			exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, ownerPartyIds));
		} else {
			exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
		}

		// lets check the tenant configuration for enableSameDayPmEntry
					// if not same day entry exclude prev day PM Sales invoices from opening balance
		Boolean enableSameDayPmEntry = Boolean.FALSE;
		try {
			GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName", "enableSameDayPmEntry"),false);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry)&& (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
				enableSameDayPmEntry = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		// get all invoices for this facility that either haven't been paid or have been paid after the opening balance date
		/* if(!enableSameDayPmEntry){			 
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
		 
	 }else{*/
		exprListForParameters.add(EntityCondition.makeCondition("shipmentId",EntityOperator.NOT_EQUAL, null));
		// }

		List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");

		exprListForParameters.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, dayBegin));
		exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, invoiceStatusList));
		exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR, EntityCondition.makeCondition("paidDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try {
			boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond,UtilMisc.toSet("invoiceId"), null, findOptions, false);
			// get Opening invoices before given sale date and added to boothOrderList
			String duesByParty = "N";
			if (isByParty) {
				duesByParty = "Y";
			}
			List<GenericValue> obInvoiceList = (List) getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityId", facilityId, "thruDate",UtilDateTime.addDaysToTimestamp(dayBegin, -1),	"isForCalOB", "Y", "duesByParty", duesByParty)).get("invoiceList");
			boothOrdersList.addAll(obInvoiceList);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		BigDecimal openingBalance = BigDecimal.ZERO;
		BigDecimal invoicePendingAmount = BigDecimal.ZERO;
		BigDecimal advancePaymentAmount = BigDecimal.ZERO;
		if (!UtilValidate.isEmpty(boothOrdersList)) {
			Set invoiceIdSet = new HashSet(EntityUtil.getFieldListFromEntityList(boothOrdersList,"invoiceId", false));
			List invoiceIds = new ArrayList(invoiceIdSet);
			// First compute the total invoice outstanding amount as of opening balance date.
			for (int i = 0; i < invoiceIds.size(); i++) {
				String invoiceId = (String) invoiceIds.get(i);
				List<GenericValue> pendingInvoiceList = FastList.newInstance();
				List exprList = FastList.newInstance();
				exprList.add(EntityCondition.makeCondition("invoiceId",	EntityOperator.EQUALS, invoiceId));
				exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate",EntityOperator.EQUALS, null),EntityOperator.OR, EntityCondition.makeCondition("paidDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
				exprList.add(EntityCondition.makeCondition("pmPaymentDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
				EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
				try {
					pendingInvoiceList = delegator.findList("InvoiceAndApplAndPayment", cond, null, null, null,	false);
					// no payment applications then add invoice total amount to OB or unapplied amount.
					
					Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("userLogin", userLogin, "invoiceId",invoiceId));
					if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
						Debug.logError(getInvoicePaymentInfoListResult.toString(),module);
						return ServiceUtil.returnError(null, null, null,getInvoicePaymentInfoListResult);
					}
					Map invoicePaymentInfo = (Map) ((List) getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
					BigDecimal outstandingAmount = (BigDecimal) invoicePaymentInfo.get("outstandingAmount");
					invoicePendingAmount = invoicePendingAmount.add(outstandingAmount);
					for (GenericValue pendingInvoice : pendingInvoiceList) {
						invoicePendingAmount = invoicePendingAmount.add(pendingInvoice.getBigDecimal("amountApplied"));
					}
				} catch (Exception e) {
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
		if (isByParty) {
			exprList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, ownerPartyIds));
		} else {
			exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds));
		}
		if (UtilValidate.isNotEmpty(prevPmInvIdList)) {
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),EntityOperator.OR, EntityCondition.makeCondition("invoiceId", EntityOperator.IN, prevPmInvIdList)));
		} else {
			exprList.add(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		}

		//exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("paidDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate))));
		exprList.add(EntityCondition.makeCondition("pmPaymentDate",EntityOperator.LESS_THAN, dayBegin));
		EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			pendingPaymentsList = delegator.findList("InvoiceAndApplAndPayment", cond, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		Set paymentSet = new HashSet(EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", false));
		for (GenericValue pendingPayments : pendingPaymentsList) {
			if (UtilValidate.isEmpty(pendingPayments.getTimestamp("paidDate")) || (UtilDateTime.getDayStart(pendingPayments.getTimestamp("paidDate"))).equals(UtilDateTime.getDayStart(pendingPayments.getTimestamp("invoiceDate")))
					|| (pendingPayments.getTimestamp("paidDate")).compareTo(dayBegin) >= 0) {
				advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amountApplied"));
			}

		}

		List paymentList = new ArrayList(paymentSet);
		for (int i = 0; i < paymentList.size(); i++) {
			try {
				Map result = dispatcher.runSync("getPaymentNotApplied",UtilMisc.toMap("userLogin", userLogin, "paymentId",(String) paymentList.get(i)));
				advancePaymentAmount = advancePaymentAmount.add((BigDecimal) result.get("unAppliedAmountTotal"));
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}

		}
		// here get the all the zero application paymentId's
		List<String> zeroAppPaymentIds = EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", true);
		// Next get payments that were made before opening balance date and have zero applications
		exprList.clear();
		if (isByParty) {
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN, ownerPartyIds));
		} else {
			exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds));
		}
		exprList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.NOT_EQUAL, "SECURITYDEPSIT_PAYIN"));
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentApplicationId", EntityOperator.EQUALS,null), EntityOperator.OR, EntityCondition.makeCondition(EntityCondition.makeCondition("isFullyApplied",EntityOperator.EQUALS, null), EntityOperator.OR,EntityCondition.makeCondition("isFullyApplied",EntityOperator.EQUALS, "N"))));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID", "PMNT_CANCELLED", "PMNT_NOT_PAID")));
		exprList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, dayBegin));
		// exculde all the zero payment application payments
		if (UtilValidate.isNotEmpty(zeroAppPaymentIds)) {

			exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.NOT_IN, zeroAppPaymentIds));
		}

		EntityCondition paymentCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			EntityFindOptions findOption = new EntityFindOptions();
			findOption.setDistinct(true);
			pendingPaymentsList = delegator.findList("PaymentAndApplicationLftJoin", paymentCond,UtilMisc.toSet("paymentId"), null, findOption, false);
			for (GenericValue pendingPayments : pendingPaymentsList) {
				Map result = dispatcher.runSync("getPaymentNotApplied",UtilMisc.toMap("userLogin", userLogin, "paymentId",pendingPayments.getString("paymentId")));
				advancePaymentAmount = advancePaymentAmount.add((BigDecimal) result.get("unAppliedAmountTotal"));
				// advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amount"));
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		// here get the cheque bounce amount till cancel of payments
		exprList.clear();
		if (isByParty) {
			exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN, ownerPartyIds));
		} else {
			exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds));
		}
		exprList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, "CHEQUE_PAYIN"));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "PMNT_VOID"));
		exprList.add(EntityCondition.makeCondition("chequeReturns",EntityOperator.EQUALS, "Y"));
		exprList.add(EntityCondition.makeCondition("cancelDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, dayBegin));

		EntityCondition payReturnCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			pendingPaymentsList = delegator.findList("Payment", payReturnCond,UtilMisc.toSet("amount"), null, null, false);
			for (GenericValue pendingPayments : pendingPaymentsList) {
				advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amount"));
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		openingBalance = invoicePendingAmount.subtract(advancePaymentAmount);
		openingBalanceMap.put("openingBalance", openingBalance);
		return openingBalanceMap;
	}

	public static Map populatePartyAccountingHistory(DispatchContext dctx,Map<String, ? extends Object> context) {
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
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
			if (UtilValidate.isNotEmpty(customTimePeriod)) {
				periodTypeId = customTimePeriod.getString("periodTypeId");
				organizationPartyId = customTimePeriod.getString("organizationPartyId");
				fromDateTime = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
				thruDateTime = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
			} 
			else {
				Debug.logError("Custom time period doesnot exists", module);
				return ServiceUtil.returnError("Custom time period doesnot exists");
			}
			checkDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(fromDateTime, -1));
			result = dispatcher.runSync("findLastClosedDate", UtilMisc.toMap("organizationPartyId", organizationPartyId,"onlyFiscalPeriods", Boolean.FALSE, "periodTypeId",	periodTypeId, "userLogin", userLogin));
			if (ServiceUtil.isError(result)) {
				Debug.logError("Error in service findLastClosedDate ", module);
				return ServiceUtil.returnError("Error in service findLastClosedDate");
			}
			previousCustomTimePeriodId = ((GenericValue) result.get("lastClosedTimePeriod")).getString("customTimePeriodId");
			if (UtilValidate.isNotEmpty(previousCustomTimePeriodId)) {
				GenericValue previousCustomPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",previousCustomTimePeriodId), false);
				prevFromDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(previousCustomPeriod.getDate("fromDate")));
				prevThruDate = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(previousCustomPeriod.getDate("thruDate")));
				if (!(checkDate.compareTo(prevThruDate) == 0)) {
					Debug.logError("Previous custom time period is not yet closed ",module);
					return ServiceUtil.returnError("Previous custom time period is not yet closed ");
				}
			}

		} catch (GenericEntityException e) {
			Debug.logError("Error while fetching custom time period Id"+ e.getMessage(), module);
			return ServiceUtil.returnError("Error while fetching custom time period Id");
		} catch (GenericServiceException e) {
			Debug.logError("Error calling service findLastClosedDate" + e.getMessage(),module);
			return ServiceUtil.returnError("Error in service findLastClosedDate");
		}

		List<GenericValue> facilityDetails = (List<GenericValue>) getAllBooths(delegator, null).get("boothsDetailsList");

		try {
			String boothId = "";
			String ownerPartyId = "";
			for (GenericValue facility : facilityDetails) {
				boothId = facility.getString("facilityId");
				ownerPartyId = facility.getString("ownerPartyId");
				BigDecimal OB = BigDecimal.ZERO;
				GenericValue partyHistory = delegator.findOne("PartyAccountingHistory", UtilMisc.toMap("customTimePeriodId",previousCustomTimePeriodId, "partyId",ownerPartyId, "facilityId", boothId), false);
				if (UtilValidate.isNotEmpty(partyHistory)) {
					OB = partyHistory.getBigDecimal("endingBalance");
				} 
				else 
				{
					result = (Map) getOpeningBalanceForBooth(dctx,UtilMisc.toMap("userLogin", userLogin, "saleDate",fromDateTime, "facilityId", boothId));
					if (ServiceUtil.isError(result)) {
						Debug.logError("Error in service getOpeningBalanceForBooth ",module);
						return ServiceUtil.returnError("Error in service getOpeningBalanceForBooth");
					}
					OB = (BigDecimal) result.get("openingBalance");
				}
				List boothIds = FastList.newInstance();
				boothIds.add(boothId);
				result = (Map) getPeriodTotals(dctx, UtilMisc.toMap("userLogin", userLogin, "facilityIds", boothIds,"fromDate", fromDateTime, "thruDate", thruDateTime));
				if (ServiceUtil.isError(result)) {
					Debug.logError("Error in service getPeriodTotals ", module);
					return ServiceUtil.returnError("Error in service getPeriodTotals");
				}
				BigDecimal totalSale = (BigDecimal) result.get("totalRevenue");
				result = (Map) getBoothPaidPayments(dctx, UtilMisc.toMap("fromDate", fromDateTime, "thruDate", thruDateTime,"facilityId", boothId));

				if (ServiceUtil.isError(result)) {
					Debug.logError("Error in service getBoothPaidPayments ",module);
					return ServiceUtil.returnError("Error in service getBoothPaidPayments");
				}

				BigDecimal paymentTotal = (BigDecimal) result.get("invoicesTotalAmount");
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
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		result = ServiceUtil.returnSuccess("Successfully populated parties accouting history for the period");
		return result;
	}
	public static Map getOpeningBalanceForParty(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");
		Timestamp saleDate = (Timestamp) context.get("saleDate");
		List exprListForParameters = FastList.newInstance();
		List boothPaymentsList = FastList.newInstance();
		List partyInvoicesList = FastList.newInstance();
		Map openingBalanceMap = FastMap.newInstance();
		BigDecimal invoicesTotalAmount = BigDecimal.ZERO;
		BigDecimal invoicesTotalDueAmount = BigDecimal.ZERO;
		Timestamp dayBegin = UtilDateTime.getDayStart(saleDate);
		try {
			GenericValue partyDetail = delegator.findOne("Party",UtilMisc.toMap("partyId", partyId), false);
			if (UtilValidate.isEmpty(partyDetail)) {
				Debug.logInfo(partyId+ "'is not a valid party", "");
				return ServiceUtil.returnError(partyId+ "'is not a valid party");
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		List invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
		exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
		exprListForParameters.add(EntityCondition.makeCondition("parentInvoiceTypeId",EntityOperator.EQUALS, "SALES_INVOICE"));
		exprListForParameters.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN, dayBegin));
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, invoiceStatusList));
		exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR, EntityCondition.makeCondition("paidDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try {
			partyInvoicesList = delegator.findList("InvoiceAndItemType", paramCond,UtilMisc.toSet("invoiceId"), null, findOptions, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		BigDecimal openingBalance = BigDecimal.ZERO;
		BigDecimal invoicePendingAmount = BigDecimal.ZERO;
		BigDecimal advancePaymentAmount = BigDecimal.ZERO;
		if (!UtilValidate.isEmpty(partyInvoicesList)) {
			Set invoiceIdSet = new HashSet(EntityUtil.getFieldListFromEntityList(partyInvoicesList,"invoiceId", false));
			List invoiceIds = new ArrayList(invoiceIdSet);
			// First compute the total invoice outstanding amount as of opening balance date.
			for (int i = 0; i < invoiceIds.size(); i++) {
				String invoiceId = (String) invoiceIds.get(i);
				List<GenericValue> pendingInvoiceList = FastList.newInstance();
				List exprList = FastList.newInstance();
				exprList.add(EntityCondition.makeCondition("invoiceId",	EntityOperator.EQUALS, invoiceId));
				exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate",EntityOperator.EQUALS, null),EntityOperator.OR, EntityCondition.makeCondition("paidDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
				exprList.add(EntityCondition.makeCondition("pmPaymentDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
				EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
				try {
					pendingInvoiceList = delegator.findList("InvoiceAndApplAndPayment", cond, null, null, null,	false);
					// no payment applications then add invoice total amount to OB or unapplied amount.
					
					Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("userLogin", userLogin, "invoiceId",invoiceId));
					if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
						Debug.logError(getInvoicePaymentInfoListResult.toString(),module);
						return ServiceUtil.returnError(null, null, null,getInvoicePaymentInfoListResult);
					}
					Map invoicePaymentInfo = (Map) ((List) getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
					BigDecimal outstandingAmount = (BigDecimal) invoicePaymentInfo.get("outstandingAmount");
					invoicePendingAmount = invoicePendingAmount.add(outstandingAmount);
					for (GenericValue pendingInvoice : pendingInvoiceList) {
						invoicePendingAmount = invoicePendingAmount.add(pendingInvoice.getBigDecimal("amountApplied"));
					}
				} catch (Exception e) {
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
		exprList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
		exprList.add(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("pmPaymentDate",EntityOperator.LESS_THAN, dayBegin));
		EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			pendingPaymentsList = delegator.findList("InvoiceAndApplAndPayment", cond, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		Set paymentSet = new HashSet(EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", false));
		for (GenericValue pendingPayments : pendingPaymentsList) {
			if (UtilValidate.isEmpty(pendingPayments.getTimestamp("paidDate")) || (UtilDateTime.getDayStart(pendingPayments.getTimestamp("paidDate"))).equals(UtilDateTime.getDayStart(pendingPayments.getTimestamp("invoiceDate")))
					|| (pendingPayments.getTimestamp("paidDate")).compareTo(dayBegin) >= 0) {
				advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amountApplied"));
			}

		}

		List paymentList = new ArrayList(paymentSet);
		for (int i = 0; i < paymentList.size(); i++) {
			try {
				Map result = dispatcher.runSync("getPaymentNotApplied",UtilMisc.toMap("userLogin", userLogin, "paymentId",(String) paymentList.get(i)));
				advancePaymentAmount = advancePaymentAmount.add((BigDecimal) result.get("unAppliedAmountTotal"));
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}

		}
		// here get the all the zero application paymentId's
		List<String> zeroAppPaymentIds = EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", true);
		// Next get payments that were made before opening balance date and have zero applications
		exprList.clear();
		exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
		exprList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.NOT_EQUAL, "SECURITYDEPSIT_PAYIN"));
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentApplicationId", EntityOperator.EQUALS,null), EntityOperator.OR, EntityCondition.makeCondition(EntityCondition.makeCondition("isFullyApplied",EntityOperator.EQUALS, null), EntityOperator.OR,EntityCondition.makeCondition("isFullyApplied",EntityOperator.EQUALS, "N"))));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID", "PMNT_CANCELLED", "PMNT_NOT_PAID")));
		exprList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, dayBegin));
		// exculde all the zero payment application payments
		if (UtilValidate.isNotEmpty(zeroAppPaymentIds)) {
			exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.NOT_IN, zeroAppPaymentIds));
		}
		EntityCondition paymentCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			EntityFindOptions findOption = new EntityFindOptions();
			findOption.setDistinct(true);
			pendingPaymentsList = delegator.findList("PaymentAndApplicationLftJoin", paymentCond,UtilMisc.toSet("paymentId"), null, findOption, false);
			for (GenericValue pendingPayments : pendingPaymentsList) {
				Map result = dispatcher.runSync("getPaymentNotApplied",UtilMisc.toMap("userLogin", userLogin, "paymentId",pendingPayments.getString("paymentId")));
				advancePaymentAmount = advancePaymentAmount.add((BigDecimal) result.get("unAppliedAmountTotal"));
				// advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amount"));
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
		// here get the cheque bounce amount till cancel of payments
		exprList.clear();
		exprList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, partyId));
		exprList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, "CHEQUE_PAYIN"));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "PMNT_VOID"));
		exprList.add(EntityCondition.makeCondition("chequeReturns",EntityOperator.EQUALS, "Y"));
		exprList.add(EntityCondition.makeCondition("cancelDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN, dayBegin));

		EntityCondition payReturnCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			pendingPaymentsList = delegator.findList("Payment", payReturnCond,UtilMisc.toSet("amount"), null, null, false);
			for (GenericValue pendingPayments : pendingPaymentsList) {
				advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amount"));
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		openingBalance = invoicePendingAmount.subtract(advancePaymentAmount);
		openingBalanceMap.put("openingBalance", openingBalance);
		return openingBalanceMap;
	}
	
	public static Map<String, Object> getBoothOrderDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String supDate = (String) context.get("supplyDate");
		String boothId = (String) context.get("boothId");
		String routeId = (String) context.get("routeId");
		String tripId = (String) context.get("tripId");
		String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
		String subscriptionTypeId = (String) context.get("subscriptionTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Boolean isNoShipment = (Boolean) context.get("isNoShipment");
		Map result = ServiceUtil.returnSuccess();
		Timestamp supplyDate = null;
		Timestamp dayBegin = null;
		Timestamp dayEnd = null;
		List changeIndentProductList = FastList.newInstance();

		try {
			if (UtilValidate.isNotEmpty(supDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
				try {
					supplyDate = new java.sql.Timestamp(sdf.parse(supDate).getTime());
				} catch (ParseException e) {
					Debug.logError("Cannot parse date string: " + supDate,module);
					return ServiceUtil.returnError("Cannot parse date string");
				} catch (NullPointerException e) {
					Debug.logError("Cannot parse date string: " + supDate,	module);
					return ServiceUtil.returnError("Cannot parse date string");
				}
			}
			dayBegin = UtilDateTime.getDayStart(supplyDate);
			dayEnd = UtilDateTime.getDayEnd(supplyDate);
		} 
		catch (Exception e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}

		Map<String, BigDecimal> changeQuantityMap = FastMap.newInstance();
		Map<String, BigDecimal> prevQuantityMap = FastMap.newInstance();
		Map prodIndentQtyCat = FastMap.newInstance();
		Map qtyInPieces = FastMap.newInstance();
		List productList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		String productStoreId = (String) (ByProductServices.getByprodFactoryStore(delegator)).get("factoryStoreId");
		String partyId = "";
		String facilityCategory = "";
		try {
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), true);

			if (UtilValidate.isEmpty(facility)) {
				Debug.logError("Invalid  Booth Id", module);
				return ServiceUtil.returnError("No dealer exists with ["+ boothId + "]");
			}
			boolean isActive = EntityUtil.isValueActive(facility, dayBegin,	"openedDate", "closedDate");
			if (!isActive) {
				Debug.logError("Is not active facility " + boothId, module);
				return ServiceUtil.returnError("The  dealer ' "	+ facility.getString("facilityName")+ "' is not Active.");
			}
			partyId = facility.getString("ownerPartyId");
			facilityCategory = facility.getString("categoryTypeEnum");
			// lets override productSubscriptionTypeId based on facility category
			if (facility.getString("categoryTypeEnum").equals("SO_INST")) {
				productSubscriptionTypeId = "SPECIAL_ORDER";
			} else if (facility.getString("categoryTypeEnum").equals("CR_INST")) {
				productSubscriptionTypeId = "CREDIT";
			} else {
				productSubscriptionTypeId = "CASH";
			}

			BigDecimal securityDeposit = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(facility.get("securityDeposit"))) {
				securityDeposit = facility.getBigDecimal("securityDeposit");
			}
			result.put("securityDeposit", securityDeposit);
			result.put("boothName", facility.getString("facilityName"));

			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS, subscriptionTypeId + "_SHIPMENT"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.EQUALS, dayBegin));
			conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, routeId));
			if (UtilValidate.isNotEmpty(tripId)) {
				conditionList.add(EntityCondition.makeCondition("tripNum",EntityOperator.EQUALS, tripId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> shipments = delegator.findList("Shipment",condition, UtilMisc.toSet("shipmentId"), null, null, false);
			if (UtilValidate.isEmpty(shipments)) {
				Debug.logError("No orders found for the given shipment details "+ boothId, module);
				return ServiceUtil.returnError("No orders found for the given shipment details "+ boothId);
			}
			String shipmentId = (EntityUtil.getFirst(shipments)).getString("shipmentId");
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.EQUALS, "ORDER_APPROVED"));
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("originFacilityId",	EntityOperator.EQUALS, boothId));
			conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS,	productSubscriptionTypeId));
			EntityCondition cond = EntityCondition.makeCondition(conditionList,	EntityOperator.AND);
			List<GenericValue> orderItems = delegator.findList(	"OrderHeaderItemProductShipmentAndFacility", cond, null,null, null, false);
			if (UtilValidate.isEmpty(orderItems)) {
				Debug.logError("No orders found for the dealer " + boothId+ "===isNoShipment ====" + isNoShipment, module);
				if (!(UtilValidate.isNotEmpty(isNoShipment) && isNoShipment)) {
					return ServiceUtil.returnError("No orders found for the dealer "+ boothId);
				}

			}
			List productIds = EntityUtil.getFieldListFromEntityList(orderItems,"productId", true);
			List<GenericValue> products = delegator.findList("Product",	EntityCondition.makeCondition("productId",EntityOperator.IN, productIds), null, null, null,	false);
			if (UtilValidate.isEmpty(routeId)) {
				Map boothDetails = (Map) (getBoothRoute(dctx, context)).get("boothDetails");
				routeId = (String) boothDetails.get("routeId");
			}

			result.put("routeId", routeId);
			for (GenericValue orderItem : orderItems) {
				Map tempChangeProdMap = FastMap.newInstance();
				String productId = orderItem.getString("productId");
				String prodName = (EntityUtil.getFirst(EntityUtil.filterByAnd(products, UtilMisc.toMap("productId", productId)))).getString("description");
				BigDecimal quantity = orderItem.getBigDecimal("quantity");
				tempChangeProdMap.put("id", productId);
				tempChangeProdMap.put("cProductId", productId);
				tempChangeProdMap.put("cProductName", prodName);
				tempChangeProdMap.put("cQuantity", quantity);
				changeIndentProductList.add(tempChangeProdMap);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}

		// lets populate route totals
		try {

			Map prodPriceMap = FastMap.newInstance();
			Map inputProductRate = FastMap.newInstance();
			inputProductRate.put("productStoreId", productStoreId);
			inputProductRate.put("fromDate", dayBegin);
			inputProductRate.put("facilityId", boothId);
			inputProductRate.put("partyId", partyId);
			inputProductRate.put("facilityCategory", facilityCategory);
			inputProductRate.put("userLogin", userLogin);
			Map priceResultMap = getProductPricesByDate(delegator,dctx.getDispatcher(), inputProductRate);
			prodPriceMap = (Map) priceResultMap.get("priceMap");
			result.put("productPrice", prodPriceMap);

			Map inputCtx = FastMap.newInstance();
			inputCtx.put("userLogin", userLogin);
			inputCtx.put("supplyDate", dayBegin);
			inputCtx.put("facilityId", boothId);
			Map qtyResultMap = getFacilityIndentQtyCategories(dctx, inputCtx);
			prodIndentQtyCat = (Map) qtyResultMap.get("indentQtyCategory");
			qtyInPieces = (Map) qtyResultMap.get("qtyInPieces");
			result.put("prodIndentQtyCat", prodIndentQtyCat);
			result.put("qtyInPieces", qtyInPieces);

		} catch (Exception e) {
			Debug.logError(e, e.getMessage());
		}
		result.put("changeIndentProductList", changeIndentProductList);
		return result;
	}

	public static Map<String, Object> getRouteIssuanceDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String supplyDate = (String) context.get("supplyDate");
		String routeId = (String) context.get("routeId");
		String tripId = (String) context.get("tripId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List routeProductList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		String productStoreId = (String) (ByProductServices.getByprodFactoryStore(delegator)).get("factoryStoreId");
		Timestamp issueDate = null;
		try {

			if (UtilValidate.isNotEmpty(supplyDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
				try {
					issueDate = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
				} catch (ParseException e) {
					Debug.logError("Cannot parse date string: " + supplyDate,module);
					return ServiceUtil.returnError("Cannot parse date string");
				} catch (NullPointerException e) {
					Debug.logError("Cannot parse date string: " + supplyDate,module);
					return ServiceUtil.returnError("Cannot parse date string");
				}
			}
			issueDate = UtilDateTime.getDayStart(issueDate);
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", routeId), true);
			if (UtilValidate.isEmpty(facility)) {
				Debug.logError("Invalid  Route Id", module);
				return ServiceUtil.returnError("Invalid  Route Id");
			}
			boolean isActive = EntityUtil.isValueActive(facility, issueDate,"openedDate", "closedDate");
			if (!isActive) {
				Debug.logError("is not active facility " + routeId, module);
				return ServiceUtil.returnError("The  facility ' " + routeId+ "' is not Active.");
			}
			List<GenericValue> products = delegator.findList("Product", null,null, null, null, false);
			result.put("routeName", facility.getString("description"));
			result.put("routeId", routeId);
			conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, routeId));
			if (UtilValidate.isNotEmpty(tripId)) {
				conditionList.add(EntityCondition.makeCondition("tripNum",EntityOperator.EQUALS, tripId));
			}
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,issueDate));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	UtilDateTime.getDayEnd(issueDate)));
			EntityCondition shipCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> shipment = delegator.findList("Shipment",shipCond, null, null, null, false);
			String shipmentId = "";
			if (UtilValidate.isNotEmpty(shipment)) {
				shipmentId = (String) ((GenericValue) EntityUtil.getFirst(shipment)).get("shipmentId");
			} 
			else {
				Debug.logError("No shipment found for the Route" + routeId+ " for the given date" + issueDate, module);
				return ServiceUtil.returnError("No shipment found for the Route and Trip ["+ routeId+ " - "+ tripId	+ "] for the given date" + issueDate);
			}
			List<GenericValue> itemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId), null, null,	null, false);
			for (GenericValue itemIssue : itemIssuance) {
				Map tempProdMap = FastMap.newInstance();
				String productId = itemIssue.getString("productId");
				GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId)));
				BigDecimal quantity = itemIssue.getBigDecimal("quantity");
				if (quantity.compareTo(BigDecimal.ZERO) > 0) {
					tempProdMap.put("id", productId);
					tempProdMap.put("cProductId", productId);
					tempProdMap.put("cProductName",	product.getString("description"));
					tempProdMap.put("cQuantity", quantity);
					routeProductList.add(tempProdMap);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}

		result.put("routeProductList", routeProductList);
		return result;
	}

	public static Map<String, Object> getOrderReturnItems(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String supplyDateStr = (String) context.get("supplyDate");
		String boothId = (String) context.get("boothId");
		String routeId = (String) context.get("routeId");
		String shipmentId = (String) context.get("shipmentId");
		/* String tripId = (String) context.get("tripId"); */
		String returnHeaderTypeId = (String) context.get("returnHeaderTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List conditionList = FastList.newInstance();
		Timestamp supplyDate = null;
		List orderReturnList = FastList.newInstance();
		Timestamp dayStart = null;
		Timestamp dayEnd = null;

		if (UtilValidate.isEmpty(shipmentId)) {
			if (UtilValidate.isNotEmpty(supplyDateStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
				try {
					supplyDate = new java.sql.Timestamp(sdf.parse(supplyDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError("Cannot parse date string: " + supplyDateStr,module);
					return ServiceUtil.returnError("Cannot parse date string");
				} catch (NullPointerException e) {
					Debug.logError("Cannot parse date string: " + supplyDateStr,module);
					return ServiceUtil.returnError("Cannot parse date string");
				}
			}
			if (UtilValidate.isEmpty(supplyDate)) {
				Debug.logError("supply date is empty", module);
				return ServiceUtil.returnError("supply date is empty");
			}
			dayStart = UtilDateTime.getDayStart(supplyDate);
			dayEnd = UtilDateTime.getDayEnd(supplyDate);
			try {
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "GENERATED"));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
				conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, routeId));
				/*conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));*/
				EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> shipment = delegator.findList("Shipment",cond, UtilMisc.toSet("shipmentId"), null, null, false);
				if (UtilValidate.isNotEmpty(shipment)) {
					shipmentId = ((GenericValue) EntityUtil.getFirst(shipment)).getString("shipmentId");
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}

		} 
		else {
			try {
				GenericValue shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
				supplyDate = shipment.getTimestamp("estimatedShipDate");
				dayStart = UtilDateTime.getDayStart(supplyDate);
				dayEnd = UtilDateTime.getDayEnd(supplyDate);

			} 
			catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}

		try {
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), false);
			if (UtilValidate.isEmpty(facility)) {
				Debug.logError("Invalid  Booth Id", module);
				return ServiceUtil.returnError("Invalid  Booth Id");
			}

			boolean isActive = EntityUtil.isValueActive(facility, dayStart,"openedDate", "closedDate");
			if (!isActive) {
				Debug.logError("is not active facility " + boothId, module);
				return ServiceUtil.returnError("The  facility ' " + boothId	+ "' is not Active.");
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}

		if (UtilValidate.isEmpty(returnHeaderTypeId)) {
			Debug.logError("Return type is empty", module);
			return ServiceUtil.returnError("Return type is empty");
		}
		Map returnItemsMap = FastMap.newInstance();
		Map returnItemsReasonMap = FastMap.newInstance();

		try {

			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("originFacilityId",	EntityOperator.EQUALS, boothId));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.NOT_IN,UtilMisc.toList("RETURN_CANCELLED")));
			EntityCondition expr = EntityCondition.makeCondition(conditionList,	EntityOperator.AND);
			List<GenericValue> returnHeader = delegator.findList("ReturnHeader", expr, null, null, null, false);

			if (UtilValidate.isNotEmpty(returnHeader)) {
				String returnId = ((GenericValue) EntityUtil.getFirst(returnHeader)).getString("returnId");
				List<GenericValue> returnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition("returnId",	EntityOperator.EQUALS, returnId), UtilMisc.toSet("productId", "returnQuantity",	"returnReasonId"), null, null, false);
				for (GenericValue returnItem : returnItems) {
					returnItemsMap.put(returnItem.getString("productId"),returnItem.getBigDecimal("returnQuantity"));
					returnItemsReasonMap.put(returnItem.getString("productId"),returnItem.getString("returnReasonId"));
				}
			}
			/*if(UtilValidate.isNotEmpty(returnHeader)){
			String returnId = ((GenericValue)EntityUtil.getFirst(returnHeader)).getString("returnId");
			Debug.logError("One return allowed for one shipment, Cancel return :"+returnId+" and re-enter the return" , module);
			return ServiceUtil.returnError("One return allowed for one shipment, Cancel return :"+returnId+" and re-enter the return");
			}*/

			Map orderedQty = FastMap.newInstance();
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("originFacilityId",	EntityOperator.EQUALS, boothId));
			conditionList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.NOT_IN,UtilMisc.toList("ORDER_CANCELLED", "ORDER_REJECTED")));
			EntityCondition orderCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCond,UtilMisc.toSet("productId", "quantity", "productName"),null, null, false);

			Map productNames = FastMap.newInstance();
			String productId = "";
			BigDecimal quantity = BigDecimal.ZERO;
			for (GenericValue orderItem : orderItems) {
				productId = orderItem.getString("productId");
				quantity = orderItem.getBigDecimal("quantity");
				if (orderedQty.containsKey(productId)) {
					BigDecimal tempQty = (BigDecimal) orderedQty.get(productId);
					tempQty = tempQty.add(quantity);
					orderedQty.put(productId, tempQty);
				} 
				else {
					orderedQty.put(productId, quantity);
				}
				productNames.put(productId, orderItem.getString("productName"));
			}

			Iterator orderItemIter = orderedQty.entrySet().iterator();
			while (orderItemIter.hasNext()) {
				Map.Entry orderItemEntry = (Entry) orderItemIter.next();
				Map tempMap = FastMap.newInstance();
				productId = (String) orderItemEntry.getKey();
				BigDecimal qty = (BigDecimal) orderItemEntry.getValue();
				tempMap.put("cProductId", productId);
				tempMap.put("cProductName", productNames.get(productId));
				tempMap.put("cQuantity", qty);
				tempMap.put("returnQuantity", "");
				if (UtilValidate.isNotEmpty(returnItemsMap)	&& UtilValidate.isNotEmpty(returnItemsMap.get(productId))) {
					tempMap.put("returnQuantity",(BigDecimal) returnItemsMap.get(productId));

				}
				if (UtilValidate.isNotEmpty(returnItemsReasonMap) && UtilValidate.isNotEmpty(returnItemsReasonMap.get(productId))) {
					GenericValue returnReason = delegator.findOne("ReturnReason", UtilMisc.toMap("returnReasonId",returnItemsReasonMap.get(productId)), false);
					tempMap.put("reasonId",	(String) returnReason.getString("description"));
					tempMap.put("returnReasonId",(String) returnReason.getString("returnReasonId"));

				}
				orderReturnList.add(tempMap);
			}
			result.put("orderReturnList", orderReturnList);
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}

	public static Map<String, Object> finalizeOrders(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String estimatedShipDateStr = (String) context.get("estimatedShipDate");
		String shipmentId = (String) context.get("shipmentId");
		String statusId = (String) context.get("statusId");
		
		String shipmentTypeId = (String) context.get("shipmentTypeId");

		String routeId = (String) context.get("routeId");
		String tripId = (String) context.get("tripId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp estimatedShipDate = null;
		String vehicleId = "";
		if (UtilValidate.isNotEmpty(estimatedShipDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
			try {
				estimatedShipDate = new java.sql.Timestamp(sdf.parse(estimatedShipDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError("Cannot parse date string: "	+ estimatedShipDateStr, module);
				return ServiceUtil.returnError("Cannot parse date string");
			} catch (NullPointerException e) {
				Debug.logError("Cannot parse date string: "	+ estimatedShipDateStr, module);
				return ServiceUtil.returnError("Cannot parse date string");
			}
		}

		// estimatedShipDate=Timestamp.valueOf(estimatedShipDateStr);

		Map resultMap = FastMap.newInstance();
		List<String> shipmentIds = FastList.newInstance();
		if(UtilValidate.isNotEmpty( context.get("shipmentIds"))){
	    	   shipmentIds=(List<String>) context.get("shipmentIds");
	    }
		List<String> routeIdsList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		Map productTotals = FastMap.newInstance();
		Map issuanceProductTotals = FastMap.newInstance();
		List<String> boothOrderIdsList = FastList.newInstance();
		Map resultCompareMap = FastMap.newInstance();
		boolean isComparsionFaild = false;
		try {
			if (UtilValidate.isNotEmpty(shipmentId)) {
				GenericValue shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
				shipmentIds.add((String) shipment.get("shipmentId"));
				estimatedShipDate = shipment.getTimestamp("estimatedShipDate");
				if (!UtilValidate.isEmpty(statusId)) {
					if(!(statusId.equals("VEHICLE_CRATE_RTN"))){
			    		// isComparsionFaild=true;
						  Debug.logError("Failed to Update Status..RETRUN CRATES NOT ENTERED for Route:"+routeId,	module);
			    		  return ServiceUtil.returnError("Failed to Update Status..RETRUN CRATES NOT ENTERED for Route:"+routeId);
			    	}
			}
			}
			if (UtilValidate.isNotEmpty(shipmentIds)) {
				Timestamp dayBegin = UtilDateTime.getDayStart(estimatedShipDate);
				Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedShipDate);
				Map inputMap = FastMap.newInstance();
				inputMap.put("shipmentIds", shipmentIds);
				inputMap.put("fromDate", dayBegin);
				inputMap.put("thruDate", dayEnd);
				// if success method will return false otherwise true
				/*resultCompareMap =compareOrdersAndItemIssuence(dctx,inputMap);
		    	 String isFailedStr=(String) resultCompareMap.get("isFailed");
		    		Debug.log("==isComparsionFaild==:"+isComparsionFaild+"==for ShipmentId===="+shipmentIds+"==resultcomapreMap=="+resultCompareMap);
		    	if(isFailedStr.equals("Y")){
		    		 isComparsionFaild=true;
		    		  return ServiceUtil.returnError("Failed to create invoice..!"+resultCompareMap.get("failedProductItemsMap"));
		    	}*/
				if (!isComparsionFaild) {
					for(String shipId:shipmentIds){
		    			 GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId",shipId) , false);
			        		estimatedShipDate=shipment.getTimestamp("estimatedShipDate");
			        		routeId=shipment.getString("routeId");
					Map vehicleTripInMap = FastMap.newInstance();
					vehicleTripInMap.put("shipmentId", shipId);
					vehicleTripInMap.put("routeId", routeId);
					Map vehcileResultMap = getVehicleTrip(dctx,vehicleTripInMap);
					GenericValue vehicleTrip = (GenericValue) vehcileResultMap.get("vehicleTrip");
					if (UtilValidate.isNotEmpty(vehicleTrip)) {
						vehicleId = vehicleTrip.getString("vehicleId");
						Map<String, Object> vehicleTripStatusMap = FastMap.newInstance();
						vehicleTripStatusMap.put("vehicleId",vehicleTrip.getString("vehicleId"));
						vehicleTripStatusMap.put("facilityId",vehicleTrip.getString("originFacilityId"));
						vehicleTripStatusMap.put("sequenceNum",	vehicleTrip.getString("sequenceNum"));
						vehicleTripStatusMap.put("userLogin", userLogin);
						vehicleTripStatusMap.put("statusId", "VEHICLE_RETURNED");
						vehicleTripStatusMap.put("lastModifiedDate",UtilDateTime.nowTimestamp());
						vehicleTripStatusMap.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
						try {
							Map vehicleTripStatusResult = dispatcher.runSync("createVehicleTripStatus",vehicleTripStatusMap);
							if (ServiceUtil.isError(vehicleTripStatusResult)) {
								String errMsg = ServiceUtil.getErrorMessage(vehicleTripStatusResult);
								Debug.logError(errMsg, module);
								return ServiceUtil.returnError("Error while Updating Status To VEHICLE_RETURNED"+ vehicleId);
							}
						} catch (GenericServiceException e) {
							Debug.logError(e,"Error while Updating Status To VEHICLE_RETURNED",	module);
							return ServiceUtil.returnError(e.getMessage());
						}
					}
				 }//for close
				}// if close
			}
		} catch (Exception e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		// result.put("routeProductList", routeProductList);
		Map result = ServiceUtil.returnSuccess("Finalization Done successfully !");
		return result;
	}

	public static Map<String, Object> compareOrdersAndItemIssuence(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List<String> shipmentIds = (List<String>) context.get("shipmentIds");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		Map result = ServiceUtil.returnSuccess();
		Map resultMap = FastMap.newInstance();
		List conditionList = FastList.newInstance();
		Map productTotals = FastMap.newInstance();
		Map issuanceProductTotals = FastMap.newInstance();
		Map failedProductItemsMap = FastMap.newInstance();
		boolean isComparisonFailed = false;
		try {
			List boothsList =null; //= NetworkServices.getRouteBooths(delegator , (String)shipmentObj.get("routeId"));
			Map boothWiseMap = FastMap.newInstance();
			Map dayTotals = getPeriodTotals(dctx, UtilMisc.toMap("shipmentIds",	shipmentIds, "facilityIds", boothsList, "fromDate",	fromDate, "thruDate", thruDate));
			if (UtilValidate.isNotEmpty(dayTotals)) {
				productTotals = (Map) dayTotals.get("productTotals");
			}
			// get Issueance Details
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipmentIds));
			EntityCondition itemIssueanceCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", itemIssueanceCond, null, null, null, false);
			// List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", itemIssueanceCond, null, UtilMisc.toList("productId"), false);
			for (GenericValue itemIssueance : itemIssuanceList) {
				BigDecimal quantity = itemIssueance.getBigDecimal("quantity");
				String issueProductId = itemIssueance.getString("productId");
				if (quantity.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal tempQuantity = BigDecimal.ZERO;
					if (UtilValidate.isEmpty(issuanceProductTotals.get(issueProductId))) {
						// issuanceProductTotals.put(issueProductId,quantity);
						tempQuantity = quantity;
					} else {
						tempQuantity = tempQuantity.add((BigDecimal) issuanceProductTotals.get(issueProductId));

					}
					issuanceProductTotals.put(issueProductId, tempQuantity);
				}
			}// for close
				// comparison of issuance and ProdTotals

			Iterator prodIter = productTotals.entrySet().iterator();
			while (prodIter.hasNext()) {
				Map.Entry entry = (Map.Entry) prodIter.next();
				String productId = (String) entry.getKey();
				Map prodTotalMap = (Map) productTotals.get(productId);
				BigDecimal prodQty = (BigDecimal) prodTotalMap.get("packetQuantity");
				/*GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId",productId) , false);
				  Debug.log("==productId=="+productId+"==quantityIncluded=="+(BigDecimal)product.get("quantityIncluded"));
				  BigDecimal pktTotal=(new BigDecimal(1.00)).divide((BigDecimal)product.get("quantityIncluded"),2,rounding);
				  BigDecimal actQtyTotal= prodQty.multiply(pktTotal);*/
				BigDecimal itemIssuQty = (BigDecimal) issuanceProductTotals.get(productId);
				if (UtilValidate.isNotEmpty(itemIssuQty)) {
					if (prodQty.compareTo(itemIssuQty) != 0) {
						Map itemInnerMap = FastMap.newInstance();
						itemInnerMap.put("ordrQty", prodQty);
						itemInnerMap.put("issuanceQty", itemIssuQty);
						failedProductItemsMap.put(productId, itemInnerMap);
						isComparisonFailed = true;
					}
				}
			}// end of while
		} catch (Exception e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());

		}
		resultMap.put("failedProductItemsMap", failedProductItemsMap);
		if (isComparisonFailed) {// true for failure
			resultMap.put("isFailed", "Y");
		} else {
			resultMap.put("isFailed", "N");
		}
		return resultMap;
	}

	// NetworkServices Methods
	/**
	 * Helper method to get booth details for given boothId
	 * The foll. details will be returned in a map: boothId, boothName, vendorName, routeName, zoneName, distributorName
	 * @param ctx the dispatch context
	 * @param context
	 * @return boothDetail map
	 */
	public static List getBoothList(Delegator delegator, String facilityId) {
		List boothList = FastList.newInstance();
		try {
			GenericValue facilityDetail = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), true);
			if (facilityDetail == null	|| (!facilityDetail.getString("facilityTypeId").equals("ZONE")&& !facilityDetail.getString("facilityTypeId").equals("ROUTE") && !facilityDetail.getString("facilityTypeId").equals("BOOTH"))) {
				Debug.logInfo("facilityId '" + facilityId+ "'is not a Booth or Zone ", "");
				boothList = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("Facility",UtilMisc.toMap("facilityTypeId", "BOOTH")),"facilityId", true);
				return boothList;
			}
			if (facilityDetail.getString("facilityTypeId").equals("ZONE")) {
				boothList = getZoneBooths(delegator, facilityId);
			} else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")) {
				boothList = getRouteBooths(delegator, facilityId);
			} else {
				boothList.add(facilityId);
			}

		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		return boothList;
	}

	public static Map<String, Object> getBoothDetails(DispatchContext ctx,Map<String, ? extends Object> context) {
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
			boothFacility = delegator.findOne("Facility", true,UtilMisc.toMap("facilityId", boothId));
			if (boothFacility == null) {
				Debug.logError("Invalid boothId " + boothId, module);
				return ServiceUtil.returnError("Invalid boothId " + boothId);
			}
			vendorName = PartyHelper.getPartyName(delegator,boothFacility.getString("ownerPartyId"), false);
			Map<String, Object> getTelParams = FastMap.newInstance();
			getTelParams.put("partyId", boothFacility.getString("ownerPartyId"));
			getTelParams.put("userLogin", userLogin);
			Map<String, Object> serviceResult = dispatcher.runSync(	"getPartyTelephone", getTelParams);
			if (ServiceUtil.isSuccess(serviceResult)) {
				vendorPhone = (String) serviceResult.get("contactNumber");
			}
			String contactNumberTo = (String) serviceResult.get("countryCode")+ (String) serviceResult.get("contactNumber");

			// routeFacility = boothFacility.getRelatedOneCache("ParentFacility");
			// zoneFacility = routeFacility.getRelatedOneCache("ParentFacility");
			// distributorFacility = zoneFacility.getRelatedOneCache("ParentFacility");
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
		boothDetails.put("categoryTypeEnum",boothFacility.getString("categoryTypeEnum"));
		boothDetails.put("vendorName", vendorName);
		boothDetails.put("vendorPhone", vendorPhone);

//        boothDetails.put("routeName", routeFacility.getString("facilityName"));
//        boothDetails.put("routeId", routeFacility.getString("facilityId"));
//        boothDetails.put("zoneName", zoneFacility.getString("facilityName"));
//        boothDetails.put("zoneId", zoneFacility.getString("facilityId"));
//        boothDetails.put("isUpcountry", zoneFacility.getString("isUpcountry"));
//        boothDetails.put("distributorName", distributorFacility.getString("facilityName"));
//        boothDetails.put("distributorId", distributorFacility.getString("facilityId"));
		result.put("boothDetails", boothDetails);
		return result;
	}

	public static Map<String, Object> getBoothRoute(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String boothId = (String) context.get("boothId");
		String supplyTime = (String) context.get("subscriptionTypeId");
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		if (UtilValidate.isEmpty(supplyDate)) {
			supplyDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		Map<String, Object> result = FastMap.newInstance();
		GenericValue boothFacility;
		GenericValue routeFacility;
		GenericValue zoneFacility;
		GenericValue distributorFacility;
		String vendorName;
		try {
			boothFacility = delegator.findOne("Facility", true,UtilMisc.toMap("facilityId", boothId));
			if (boothFacility == null) {
				Debug.logError("Invalid boothId " + boothId, module);
				return ServiceUtil.returnError("Invalid boothId " + boothId);
			}
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("facilityGroupTypeId",EntityOperator.EQUALS, "RT_BOOTH_GROUP"));
			condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, boothId));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> rtGroupMember = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null,null, true);
			rtGroupMember = EntityUtil.filterByDate(rtGroupMember, supplyDate);
			condList.clear();
			String supplyTimeFacilityGroupSuffix = "_RT_GROUP";
			String supplyTimeFacilityGroup = "AM_RT_GROUP";
			if (UtilValidate.isNotEmpty(supplyTime)) {
				supplyTimeFacilityGroup = supplyTime+ supplyTimeFacilityGroupSuffix;
			}
			condList.add(EntityCondition.makeCondition("facilityGroupId",EntityOperator.EQUALS, supplyTimeFacilityGroup));
			condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, EntityUtil.getFieldListFromEntityList(rtGroupMember, "ownerFacilityId", true)));

			EntityCondition condGroup = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> rtGroup = delegator.findList("FacilityGroupAndMemberAndFacility", condGroup, null, null,	null, true);
			rtGroup = EntityUtil.filterByDate(rtGroup, supplyDate);
			routeFacility = EntityUtil.getFirst(rtGroup);
		} 
		catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}

		Map<String, Object> boothDetails = FastMap.newInstance();
		if (UtilValidate.isEmpty(routeFacility)) {
			Debug.logError("permanent route  not configured for:" + boothId,module);
			return ServiceUtil.returnError("permanent route  not configured for:"+ boothId);
		}
		boothDetails.put("routeId", routeFacility.getString("facilityId"));
		boothDetails.put("boothId", boothId);
		result.put("boothDetails", boothDetails);
		return result;
	}

	// This will return the list of boothIds for the given zone and (optional) booth category type
	public static List getZoneRoutes(Delegator delegator, String zoneId) {
		List<String> routeIds = FastList.newInstance();
		try {
			List<GenericValue> routes = delegator.findList("Facility",EntityCondition.makeCondition("parentFacilityId",	EntityOperator.EQUALS, zoneId), null, UtilMisc.toList("facilityId"), null, false);
			routeIds = EntityUtil.getFieldListFromEntityList(routes,"facilityId", false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		return routeIds;
	}

	// This will return the list of boothIds for the given zone
	public static List getZoneBooths(Delegator delegator, String zoneId) {
		return getZoneBooths(delegator, zoneId, null);
	}

	// This will return All boothsList
	public static Map<String, Object> getAllBooths(Delegator delegator,String categoryTypeEnum) {
		Map<String, Object> result = FastMap.newInstance();
		List boothsList = FastList.newInstance();
		List boothsDetailsList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		try {
			conditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS, "BOOTH"));
			if (UtilValidate.isNotEmpty(categoryTypeEnum)) {
				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,categoryTypeEnum));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> booths = delegator.findList("Facility", condition, null,	UtilMisc.toList("facilityId"), null, false);
			Iterator<GenericValue> boothIter = booths.iterator();
			while (boothIter.hasNext()) {
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

	// This will return all booths as a list of Maps with all the relevant booth details
	public static Map<String, Object> getAllBoothsDetails(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String categoryTypeEnum = (String) context.get("categoryTypeEnum");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean activeOnly = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("activeOnly"))) {
			activeOnly = (Boolean) context.get("activeOnly");
		}		
		Map<String, Object> result = FastMap.newInstance();
		List boothsDetailsList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		// get fieldstaff map
		Map<String, String> facilityFieldStaffMap = FastMap.newInstance();
		Map<String, Object> facilityFieldStaffResult = getFacilityFieldStaff(dctx, context);
		if (facilityFieldStaffResult != null && facilityFieldStaffResult.get("facilityFieldStaffMap") != null) {
			facilityFieldStaffMap = (Map<String, String>) facilityFieldStaffResult.get("facilityFieldStaffMap");
		}
		try {
			Map FDRDetails = (Map)getFacilityFixedDeposit( dctx , UtilMisc.toMap("userLogin", userLogin, "effectiveDate", UtilDateTime.nowTimestamp())).get("FacilityFDRDetail");
			conditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS, "BOOTH"));
			if (UtilValidate.isNotEmpty(categoryTypeEnum)) {
				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,categoryTypeEnum));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> booths = delegator.findList("Facility", condition, null,UtilMisc.toList("facilityId"), null, false);
			if (activeOnly) {
				booths = EntityUtil.filterByDate(booths, UtilDateTime.nowTimestamp(), "openedDate", "closedDate",Boolean.FALSE);
			}
			Iterator<GenericValue> boothIter = booths.iterator();
			while (boothIter.hasNext()) {
				BigDecimal fixedDeposit = BigDecimal.ZERO;
				BigDecimal securityDeposit = BigDecimal.ZERO;
				GenericValue booth = boothIter.next();
				String boothId = booth.getString("facilityId");
				if(UtilValidate.isNotEmpty(FDRDetails) && UtilValidate.isNotEmpty(FDRDetails.get(boothId))){
					Map facilityFDRDetail = (Map)FDRDetails.get(boothId);
					fixedDeposit = (BigDecimal)facilityFDRDetail.get("totalAmount");
				}
				String vendorPhone = "";
				String latitude = "";
				String longitude = "";
				String vendorName = PartyHelper.getPartyName(delegator,	booth.getString("ownerPartyId"), false);
				Map<String, Object> getTelParams = FastMap.newInstance();
				getTelParams.put("partyId", booth.getString("ownerPartyId"));
				getTelParams.put("userLogin", userLogin);
				Map<String, Object> serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
				if (ServiceUtil.isSuccess(serviceResult)) {
					vendorPhone = (String) serviceResult.get("contactNumber");
				}
				String amRouteId = "";
				String pmRouteId = "";
				String salesRep = "";
				if (facilityFieldStaffMap.get(boothId) != null) {
					salesRep = PartyHelper.getPartyName(delegator,(String) facilityFieldStaffMap.get(boothId), false);
				}
				Map amDetails = (Map) (getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "subscriptionTypeId", "AM","userLogin", userLogin))).get("boothDetails");
				if (amDetails != null) {
					amRouteId = (String) amDetails.get("routeId");
				}
				Map pmDetails = (Map) (getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "subscriptionTypeId", "PM","userLogin", userLogin))).get("boothDetails");
				if (pmDetails != null) {
					pmRouteId = (String) pmDetails.get("routeId");
				}
				GenericValue pt = booth.getRelatedOne("GeoPoint");
				if (pt != null) {
					latitude = pt.getString("latitude");
					longitude = pt.getString("longitude");
				}
				if(UtilValidate.isNotEmpty(booth.get("securityDeposit"))){
					securityDeposit = booth.getBigDecimal("securityDeposit");
				}
				Map<String, Object> boothDetails = FastMap.newInstance();
				boothDetails.put("facilityId", boothId);
				boothDetails.put("facilityName",booth.getString("facilityName"));
				boothDetails.put("category",booth.getString("categoryTypeEnum"));
				boothDetails.put("ownerName", vendorName);
				boothDetails.put("securityDeposit", securityDeposit);
				boothDetails.put("fixedDeposit", fixedDeposit);
				boothDetails.put("ownerPhone", vendorPhone);
				boothDetails.put("salesRep", salesRep);
				boothDetails.put("amRouteId", amRouteId);
				boothDetails.put("pmRouteId", pmRouteId);
				boothDetails.put("latitude", latitude);
				boothDetails.put("longitude", longitude);

				boothsDetailsList.add(boothDetails);
			}
			result.put("boothsDetailsList", boothsDetailsList);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		} 
		catch (Exception e) {
			Debug.logError(e, "Problem getting booth details", module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> getFacilityFixedDeposit(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = FastMap.newInstance();
		List facilityList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		Map facilityFDMap = FastMap.newInstance();
		if (UtilValidate.isEmpty(facilityId)) {
			facilityList = (List) getAllBooths(delegator, null).get("boothsList");
		}
		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		try {
			if (UtilValidate.isNotEmpty(facilityId)) {
				conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
			}
			if (UtilValidate.isNotEmpty(facilityList)) {
				conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityList));
			}
			conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> facilityFDs = delegator.findList("FacilityFixedDeposit", condition, null, null, null, false);
			List<String> boothIds = EntityUtil.getFieldListFromEntityList(facilityFDs, "facilityId", true);

			for (String boothId : boothIds) {
				List<GenericValue> eachBoothFDRs = EntityUtil.filterByCondition(facilityFDs, EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, boothId));
				for (GenericValue eachBoothData : eachBoothFDRs) {
					if (facilityFDMap.containsKey(boothId)) {
						Map tempFDRDetail = FastMap.newInstance();
						tempFDRDetail = (Map) facilityFDMap.get(boothId);
						List FDREntries = (List) tempFDRDetail.get("FDRDetail");
						BigDecimal totalAmt = BigDecimal.ZERO;
						BigDecimal extTotAmt = (BigDecimal) tempFDRDetail.get("totalAmount");
						Map tempMap = FastMap.newInstance();
						tempMap.put("facilityId",eachBoothData.getString("facilityId"));
						tempMap.put("fdrNumber",eachBoothData.getString("fdrNumber"));
						tempMap.put("bankName",	eachBoothData.getString("bankName"));
						tempMap.put("branchName",eachBoothData.getString("branchName"));
						tempMap.put("amount",eachBoothData.getBigDecimal("amount"));
						tempMap.put("fromDate",	eachBoothData.getTimestamp("fromDate"));
						tempMap.put("thruDate",	eachBoothData.getTimestamp("thruDate"));
						FDREntries.add(tempMap);
						totalAmt = extTotAmt.add(eachBoothData.getBigDecimal("amount"));
						tempFDRDetail.put("FDRDetail", FDREntries);
						tempFDRDetail.put("totalAmount", totalAmt);
						facilityFDMap.put(boothId, tempFDRDetail);

					} else {
						Map FDRDetail = FastMap.newInstance();
						List tempList = FastList.newInstance();
						Map tempMap = FastMap.newInstance();
						tempMap.put("facilityId",eachBoothData.getString("facilityId"));
						tempMap.put("fdrNumber",eachBoothData.getString("fdrNumber"));
						tempMap.put("bankName",eachBoothData.getString("bankName"));
						tempMap.put("branchName",eachBoothData.getString("branchName"));
						tempMap.put("amount",eachBoothData.getBigDecimal("amount"));
						tempMap.put("fromDate",	eachBoothData.getTimestamp("fromDate"));
						tempMap.put("thruDate",	eachBoothData.getTimestamp("thruDate"));
						tempList.add(tempMap);
						FDRDetail.put("FDRDetail", tempList);
						FDRDetail.put("totalAmount",eachBoothData.getBigDecimal("amount"));
						facilityFDMap.put(boothId, FDRDetail);
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Problem fetching facility fixed deposits",module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("FacilityFDRDetail", facilityFDMap);
		return result;
	}

	public static Map<String, Object> getAllActiveOrInactiveBooths(Delegator delegator, String categoryTypeEnum, Timestamp dateMoment) {
		Map<String, Object> result = FastMap.newInstance();
		List<GenericValue> boothsList = (List<GenericValue>) getAllBooths(delegator, categoryTypeEnum).get("boothsDetailsList");
		List<GenericValue> boothActiveList = EntityUtil.filterByDate(boothsList, dateMoment, "openedDate", "closedDate",Boolean.FALSE);
		boothsList.removeAll(boothActiveList);
		// List<GenericValue> boothInActiveList =
		result.put("boothActiveList", boothActiveList);
		result.put("boothInActiveList", boothsList);
		return result;
	}

	// This will return the list of boothIds for the given zone and (optional) booth category type
	public static List getZoneBooths(Delegator delegator, String zoneId,String boothCategory) {
		List<String> boothIds = FastList.newInstance();

		try {
			List<GenericValue> routes = delegator.findList("Facility",EntityCondition.makeCondition("parentFacilityId",	EntityOperator.EQUALS, zoneId), null, UtilMisc.toList("facilityId"), null, false);
			List routeIds = EntityUtil.getFieldListFromEntityList(routes,"facilityId", false);
			if (!routeIds.isEmpty()) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN, routeIds));
				if (!UtilValidate.isEmpty(boothCategory)) {
					conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,boothCategory));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> booths = delegator.findList("Facility",condition, null, UtilMisc.toList("facilityId"), null,	false);
				boothIds = EntityUtil.getFieldListFromEntityList(booths,"facilityId", false);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		return boothIds;
	}

	// This will return the list of boothIds for the given zone
	public static List getRouteBooths(Delegator delegator, String routeId) {
		return getRouteBooths(delegator, routeId, null);
	}

	// This will return the list of boothIds for the given route and (optional) booth category type
	public static List getRouteBooths(Delegator delegator, String routeId,String boothCategory) {
		List<String> boothIds = FastList.newInstance();
		boothIds = (List) (getRouteBooths(delegator, UtilMisc.toMap("routeId",routeId, "categoryTypeEnum", boothCategory))).get("boothIdsList");
		return boothIds;
	}

	// This will return the list of boothIds for the given route and (optional) booth category type
	public static Map getRouteBooths(Delegator delegator,Map<String, ? extends Object> context) {
		List<String> boothIds = FastList.newInstance();
		List<GenericValue> booths = FastList.newInstance();
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		String boothCategory = (String) context.get("boothCategory");
		String routeId = (String) context.get("routeId");
		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.nowTimestamp();
		}

		try {
			List condList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(boothCategory)) {
				condList.add(EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.EQUALS, boothCategory));
			}

			condList.add(EntityCondition.makeCondition("facilityGroupTypeId",EntityOperator.EQUALS, "RT_BOOTH_GROUP"));
			condList.add(EntityCondition.makeCondition("ownerFacilityId",EntityOperator.EQUALS, routeId));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			booths = delegator.findList("FacilityGroupAndMemberAndFacility",cond, null, null, null, true);
			booths = EntityUtil.filterByDate(booths, effectiveDate);
			boothIds = EntityUtil.getFieldListFromEntityList(booths,"facilityId", true);

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("boothsList", booths);
		result.put("boothIdsList", boothIds);
		return result;
	}

	public static Map getBoothsRouteByShipment(Delegator delegator,Map<String, ? extends Object> context) {
		List<String> boothIds = FastList.newInstance();
		List<GenericValue> booths = FastList.newInstance();
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		String boothCategory = (String) context.get("boothCategory");
		String facilityId = (String) context.get("facilityId");
		List shipmentIds=FastList.newInstance();
		if (!UtilValidate.isEmpty(context.get("shipmentIds"))) {
			shipmentIds =(List) context.get("shipmentIds");
		}
		Map boothRouteIdsMap = FastMap.newInstance();

		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		Timestamp dayStart = UtilDateTime.getDayStart(effectiveDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate);
		if (UtilValidate.isEmpty(shipmentIds)) {
			 shipmentIds = getShipmentIds(delegator, dayStart, dayEnd);
		}

		String facilityTypeId = "";
		String ownerPartyId = "";
		try {
			List condList = FastList.newInstance();
			// get type of Facility
			GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
			if (UtilValidate.isNotEmpty(facilityDetail)) {
				facilityTypeId = facilityDetail.getString("facilityTypeId");
				ownerPartyId = facilityDetail.getString("ownerPartyId");
			}

			if (UtilValidate.isNotEmpty(facilityTypeId)	&& facilityTypeId.equals("ROUTE")) {
				condList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, facilityId));
			}
			if (UtilValidate.isNotEmpty(facilityTypeId)	&& facilityTypeId.equals("BOOTH")) {
				condList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS, ownerPartyId));
				// condList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS ,facilityId));
			}
			if (UtilValidate.isNotEmpty(boothCategory)) {
				condList.add(EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.EQUALS, boothCategory));
			}
			condList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.EQUALS, "ORDER_APPROVED"));
			condList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipmentIds));
			/*condList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			 * condList.add(EntityCondition.makeCondition("estimatedDeliveryDate",
			 * EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			 */
			
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> orderBooths = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null,null, null, true);
			boothIds = EntityUtil.getFieldListFromEntityList(orderBooths,"originFacilityId", true);
			if (UtilValidate.isNotEmpty(boothIds)) {
				booths = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN,boothIds), null, null, null, false);
			}
			for (String eachBooth : boothIds) {
				List<GenericValue> boothOrderData = EntityUtil.filterByCondition(orderBooths, EntityCondition.makeCondition("originFacilityId",EntityOperator.EQUALS, eachBooth));
				GenericValue boothData = EntityUtil.getFirst(boothOrderData);
				boothRouteIdsMap.put(eachBooth, boothData.getString("routeId"));
			}
			// booths = EntityUtil.filterByDate(booths, effectiveDate);

			/*for(GenericValue facilityBooth: booths){
   		 		boothRouteIdsMap.put(facilityBooth.getString("facilityId"),facilityBooth.getString("ownerFacilityId"));
   	 		}*/
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("boothsList", booths);
		result.put("boothIdsList", boothIds);
		result.put("boothRouteIdsMap", boothRouteIdsMap);
		return result;
	}
	
	public static Map getBoothsByAMPM(Delegator delegator,Map<String, ? extends Object> context) {
		List<String> boothIds = FastList.newInstance();
		List<GenericValue> booths = FastList.newInstance();
		List<GenericValue> facilityGroups = FastList.newInstance();
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.nowTimestamp();
		}

		try {
			facilityGroups = delegator.findList("FacilityGroupAndMemberAndFacility", EntityCondition.makeCondition("facilityGroupTypeId",EntityOperator.EQUALS, "RT_BOOTH_GROUP"), null, null, null, true);
			facilityGroups = EntityUtil.filterByDate(facilityGroups, effectiveDate);
			
			List<GenericValue> amFacilityGroups  = EntityUtil.filterByCondition(facilityGroups, EntityCondition.makeCondition("primaryParentGroupId",EntityOperator.EQUALS, "AM_RT_GROUP"));
			List<GenericValue> pmFacilityGroups  = EntityUtil.filterByCondition(facilityGroups, EntityCondition.makeCondition("primaryParentGroupId",EntityOperator.EQUALS, "PM_RT_GROUP"));
			
			List amRoutes = EntityUtil.getFieldListFromEntityList(amFacilityGroups, "facilityGroupId", true);
			List pmRoutes = EntityUtil.getFieldListFromEntityList(pmFacilityGroups, "facilityGroupId", true);
			List amBooths = EntityUtil.getFieldListFromEntityList(amFacilityGroups, "facilityId", true);
			List pmBooths = EntityUtil.getFieldListFromEntityList(pmFacilityGroups, "facilityId", true);
			List amPartyBooths = EntityUtil.getFieldListFromEntityList(amFacilityGroups, "ownerPartyId", true);
			List pmPartyBooths = EntityUtil.getFieldListFromEntityList(pmFacilityGroups, "ownerPartyId", true);

			result.put("amRoutes", amRoutes);
			result.put("pmRoutes", pmRoutes);
			result.put("amBooths", amBooths);
			result.put("pmBooths", pmBooths);
			result.put("amPartyBooths", amPartyBooths);
			result.put("pmPartyBooths", pmPartyBooths);

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		return result;
	}
	
	public static Map getBoothsRouteMap(Delegator delegator,Map<String, ? extends Object> context) {
		List<String> boothIds = FastList.newInstance();
		List<GenericValue> booths = FastList.newInstance();
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		String boothCategory = (String) context.get("boothCategory");
		String facilityId = (String) context.get("facilityId");
		List<String> facilityIdsList = FastList.newInstance();// tat means all booths or Routes

		Map boothRouteIdsMap = FastMap.newInstance();

		if (UtilValidate.isEmpty(effectiveDate)) {
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		String facilityTypeId = "";

		try {
			List condList = FastList.newInstance();
			// get type of Facility
			GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
			if (UtilValidate.isNotEmpty(facilityDetail)) {
				facilityTypeId = facilityDetail.getString("facilityTypeId");
			}
			if (UtilValidate.isNotEmpty(context.get("facilityIdsList"))) {
				facilityIdsList = (List) context.get("facilityIdsList");// tat means all booths or Routes
			}
			if (UtilValidate.isNotEmpty(facilityId)) {
				facilityIdsList.add(facilityId);
			}
			if (UtilValidate.isNotEmpty(boothCategory)) {
				condList.add(EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.EQUALS, boothCategory));
			}

			if (UtilValidate.isNotEmpty((String) context.get("facilityTypeId"))) {// if context type is Not empty consider tat only
				facilityTypeId = (String) context.get("facilityTypeId");
			}
			condList.add(EntityCondition.makeCondition("facilityGroupTypeId",EntityOperator.EQUALS, "RT_BOOTH_GROUP"));
			if ((UtilValidate.isNotEmpty(facilityTypeId)) && facilityTypeId.equals("BOOTH")) {
				condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIdsList));
			}
			if ((UtilValidate.isNotEmpty(facilityTypeId)) && facilityTypeId.equals("ROUTE")) {
				condList.add(EntityCondition.makeCondition("ownerFacilityId",EntityOperator.IN, facilityIdsList));
			}

			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			booths = delegator.findList("FacilityGroupAndMemberAndFacility",cond, null, null, null, true);
			booths = EntityUtil.filterByDate(booths, effectiveDate);
			for (GenericValue facilityBooth : booths) {
				boothRouteIdsMap.put(facilityBooth.getString("facilityId"),facilityBooth.getString("ownerFacilityId"));
			}
			boothIds = EntityUtil.getFieldListFromEntityList(booths,"facilityId", true);

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
	public static Map<String, Object> getAllZonesBoothsMap(Delegator delegator) {
		Map<String, Object> result = FastMap.newInstance();
		try {
			List<GenericValue> zones = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS, "ZONE"), null, UtilMisc.toList("facilityId"), null, false);
			Iterator<GenericValue> zoneIter = zones.iterator();
			while (zoneIter.hasNext()) {
				GenericValue zone = zoneIter.next();
				List boothIds = getZoneBooths(delegator,zone.getString("facilityId"));
				Map<String, Object> zoneMap = FastMap.newInstance();
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
	public static Map<String, Object> getAllBoothsZonesMap(Delegator delegator) {
		Map<String, Object> result = new TreeMap<String, Object>();
		Map<String, Object> zonesMap = getAllZonesBoothsMap(delegator);
		for (Map.Entry<String, Object> entry : zonesMap.entrySet()) {
			Map<String, Object> zoneValue = (Map<String, Object>) entry.getValue();
			List boothIds = (List) zoneValue.get("boothIds");
			Iterator<String> boothIter = boothIds.iterator();
			while (boothIter.hasNext()) {
				Map<String, Object> boothMap = FastMap.newInstance();
				boothMap.put("name", zoneValue.get("name"));
				boothMap.put("distributorId", zoneValue.get("distributorId"));
				boothMap.put("zoneId", entry.getKey());
				result.put(boothIter.next(), boothMap);
			}
		}
		return result;
	}

	public static Map<String, Object> getAllBoothsRegionsMap(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new TreeMap<String, Object>();

		List<GenericValue> regions = null;
		try {
			regions = delegator.findList("FacilityGroup", EntityCondition.makeCondition("facilityGroupTypeId",EntityOperator.EQUALS, "REGION_TYPE"), null, null,null, false);
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		Map boothsRegionsMap = FastMap.newInstance();
		Map groupMemberCtx = FastMap.newInstance();
		Map<String, List> regionBoothMap = FastMap.newInstance();
		for (GenericValue region : regions) {
			try {
				List<GenericValue> regionMembers = delegator.findList("FacilityGroupMemberAndFacility", EntityCondition.makeCondition("facilityGroupId",EntityOperator.EQUALS,region.getString("facilityGroupId")),	null, null, null, false);
				regionBoothMap.put(region.getString("facilityGroupId"),	EntityUtil.getFieldListFromEntityList(regionMembers,"facilityId", true));
			} catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}
		}
		List<GenericValue> boothsList = (List<GenericValue>) getAllBooths(delegator, null).get("boothsDetailsList");
		for (GenericValue booth : boothsList) {
			Map<String, Object> boothMap = FastMap.newInstance();
			String boothId = booth.getString("facilityId");
			String zoneId = booth.getString("zoneId");
			boothMap.put("name", booth.getString("facilityName"));
			boothMap.put("zoneId", zoneId);
			boothMap.put("regionId", "");
			// lets populate regionId here
			if (UtilValidate.isNotEmpty(regionBoothMap)) {
				for (Map.Entry<String, List> regionEntry : regionBoothMap.entrySet()) {
					List regionBooths = regionEntry.getValue();
					if (regionBooths.contains(zoneId)) {
						boothMap.put("regionId", regionEntry.getKey());
					}
				}
			}
			result.put(boothId, boothMap);
		}
		return result;
	}

	public static List getShipmentIdsByAMPM(Delegator delegator,String estimatedDeliveryDateString, String subscriptionType) {
		return getShipmentIdsByAMPM(delegator, estimatedDeliveryDateString,subscriptionType, null);
	}

	public static List getShipmentIdsByAMPM(Delegator delegator,String estimatedDeliveryDateString, String subscriptionType,String routeId) {

		List shipmentIds = FastList.newInstance();
		if(!subscriptionType.equals("AM") && !subscriptionType.equals("PM") && !subscriptionType.equals("DIRECT")){			
			return shipmentIds;			
		}
		if (subscriptionType.equals("AM")) {
			shipmentIds = getShipmentIds(delegator,estimatedDeliveryDateString, "AM_SHIPMENT", routeId);
			shipmentIds.addAll(getShipmentIds(delegator,estimatedDeliveryDateString, "AM_SHIPMENT_SUPPL"));
		}else if(subscriptionType.equals("DIRECT")){
			shipmentIds.addAll(getShipmentIds(delegator , estimatedDeliveryDateString,"RM_DIRECT_SHIPMENT"));	
		}else {
			shipmentIds = getShipmentIds(delegator,estimatedDeliveryDateString, "PM_SHIPMENT", routeId);
			shipmentIds.addAll(getShipmentIds(delegator,estimatedDeliveryDateString, "PM_SHIPMENT_SUPPL"));
		}
		return shipmentIds;
	}

	public static List getShipedRouteIdsByAMPM(Delegator delegator,String estimatedDeliveryDateString, String subscriptionType,String routeId) {

		List routeIds = FastList.newInstance();

		List shipmentIds = getShipmentIdsByAMPM(delegator,estimatedDeliveryDateString, subscriptionType, routeId);
		if (UtilValidate.isNotEmpty(shipmentIds)) {
			try {
				routeIds = EntityUtil.getFieldListFromEntityList(delegator.findList("Shipment", EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds),null, null, null, false), "routeId", false);
			} catch (Exception e) {
				Debug.logError(e, "error getting shipment Id's: " + routeId,module);
			}

		}
		return routeIds;
	}

	public static List getShipmentIds(Delegator delegator,String estimatedDeliveryDateString, String shipmentTypeId) {
		//TO DO:for now getting one shipment id  we need to get pm and am shipment id irrespective of Shipment type Id
		return getShipmentIds(delegator, estimatedDeliveryDateString,shipmentTypeId, null);

	}

	// This will return the list of ShipmentIds for the selected
	public static List getShipmentIds(Delegator delegator,String estimatedDeliveryDateString, String shipmentTypeId,String routeId) {
		//TO DO:for now getting one shipment id  we need to get pm and am shipment id irrespective of Shipment type Id
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();
		String estimatedDeliveryDateStr = estimatedDeliveryDateString;
		List conditionList = FastList.newInstance();
		List shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		try {
			estimatedDeliveryDate = new java.sql.Timestamp(sdf.parse(estimatedDeliveryDateStr).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: "+ estimatedDeliveryDateStr, module);
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDate);
		if (shipmentTypeId == null) {
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,UtilMisc.toList("AM_SHIPMENT", "AM_SHIPMENT_SUPPL")));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			if (UtilValidate.isNotEmpty(routeId)) {
				conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, routeId));
			}
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Cannot parse date string: "+ estimatedDeliveryDateStr, module);
			}
			if (!UtilValidate.isEmpty(shipmentList)) {
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}

			conditionList.clear();
			condition.reset();

			// lets check the tenant configuration for enableSameDayPmEntry
			Boolean enableSameDayPmEntry = Boolean.FALSE;
			try {
				GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName","enableSameDayPmEntry"), false);
				if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
					enableSameDayPmEntry = Boolean.TRUE;
				}
			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}

			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,	UtilMisc.toList("PM_SHIPMENT", "PM_SHIPMENT_SUPPL")));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			if (!enableSameDayPmEntry) {
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.addDaysToTimestamp(dayBegin, -1)));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	UtilDateTime.addDaysToTimestamp(dayEnd, -1)));
			} else {
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			}
			if (UtilValidate.isNotEmpty(routeId)) {
				conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, routeId));
			}
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,	null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Cannot parse date string: "	+ estimatedDeliveryDateStr, module);
			}
			if (!UtilValidate.isEmpty(shipmentList)) {
				shipments.addAll(EntityUtil.getFieldListFromEntityList(	shipmentList, "shipmentId", false));
			}

		} else {
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS, shipmentTypeId));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
			if (UtilValidate.isNotEmpty(routeId)) {
				conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, routeId));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Cannot parse date string: "+ estimatedDeliveryDateStr, module);
			}
			shipments = EntityUtil.getFieldListFromEntityList(shipmentList,"shipmentId", false);
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
	public static List getShipmentIds(Delegator delegator, Timestamp fromDate,Timestamp thruDate) {

		List conditionList = FastList.newInstance();
		List shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		Timestamp dayBegin = UtilDateTime.nowTimestamp();
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,UtilMisc.toList("AM_SHIPMENT", "AM_SHIPMENT_SUPPL")));
		conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
		if (!UtilValidate.isEmpty(fromDate)) {
			dayBegin = UtilDateTime.getDayStart(fromDate);
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		}

		conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}
		if (!UtilValidate.isEmpty(shipmentList)) {
			shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		}

		conditionList.clear();
		condition.reset();
		// Enable LMS PM Sales entry for sameday
		//Enable LMS PM Sales(if the property set to 'Y' then ,Day  NetSales  = 'AM Sales+Prev.Day PM Sales'   otherwise NetSales = 'AM Sales+ PM Sales')
		Boolean enableSameDayPmEntry = Boolean.FALSE;
		try {
			GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName", "enableSameDayPmEntry"),false);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
				enableSameDayPmEntry = Boolean.TRUE;			
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}

		conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,UtilMisc.toList("PM_SHIPMENT", "PM_SHIPMENT_SUPPL")));
		conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
		if (!UtilValidate.isEmpty(fromDate)) {
			if (!enableSameDayPmEntry) {
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.GREATER_THAN_EQUAL_TO,	UtilDateTime.addDaysToTimestamp(dayBegin, -1)));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	UtilDateTime.addDaysToTimestamp(dayEnd, -1)));
			} else {
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
				conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			}

		}

		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		try {
			shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}
		if (!UtilValidate.isEmpty(shipmentList)) {
			shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		}

		return shipments;
	}

	public static List getShipmentIdsSupplyType(Delegator delegator,Timestamp fromDate, Timestamp thruDate, String supplyTypeId) {
		// TO DO:for now getting one shipment id we need to get pm and am shipment id irrespective of Shipment type Id
		List conditionList = FastList.newInstance();
		List shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		if (UtilValidate.isNotEmpty(supplyTypeId) && ("AM".equals(supplyTypeId))) {
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,	UtilMisc.toList("AM_SHIPMENT", "AM_SHIPMENT_SUPPL")));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,	null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Unable to get shipment list: ", module);
			}
			if (!UtilValidate.isEmpty(shipmentList)) {
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}
		} 
		else if (UtilValidate.isNotEmpty(supplyTypeId) && ("PM".equals(supplyTypeId))) {
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,	UtilMisc.toList("PM_SHIPMENT", "PM_SHIPMENT_SUPPL")));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Unable to get shipment list: ", module);
			}
			if (!UtilValidate.isEmpty(shipmentList)) {
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}
		} else if(UtilValidate.isNotEmpty(supplyTypeId)&&("DIRECT".equals(supplyTypeId))){
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,	UtilMisc.toList("RM_DIRECT_SHIPMENT")));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Unable to get shipment list: ", module);
			}
			if (!UtilValidate.isEmpty(shipmentList)) {
				shipments.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
			}
		}else {// combine both shipments AM nd PM
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN, UtilMisc.toList("AM_SHIPMENT","AM_SHIPMENT_SUPPL", "PM_SHIPMENT","PM_SHIPMENT_SUPPL")));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			try {
				shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
			} catch (Exception e) {
				Debug.logError(e, "Unable to get shipment list: ", module);
			}
			shipments = EntityUtil.getFieldListFromEntityList(shipmentList,	"shipmentId", false);
		}
		return shipments;
	}

	public static List getShipmentIdsByType(Delegator delegator,Timestamp fromDate, Timestamp thruDate, String shipmentTypeId) {

		List conditionList = FastList.newInstance();
		List<GenericValue> shipmentList = FastList.newInstance();
		List shipments = FastList.newInstance();
		Timestamp dayBegin = null;
		Timestamp dayEnd = null;
		dayBegin = fromDate;
		dayEnd = thruDate;
		if (UtilValidate.isEmpty(fromDate)) {
			dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		if (UtilValidate.isEmpty(thruDate)) {
			dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		}
		if (UtilValidate.isNotEmpty(shipmentTypeId)) {
			conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS, shipmentTypeId));
		}
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "GENERATED"));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(dayBegin)));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(dayEnd)));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting shipment ids ", module);
		}
		if (UtilValidate.isNotEmpty(shipmentList)) {
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
	public static Map<String, Object> getRoutesByAMPM(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String supplyType = (String) context.get("supplyType");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<String> routesIdsList = FastList.newInstance();
		try {
			List conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(supplyType)) {
				String type = supplyType + "_RT_GROUP";
				conditionList.add(EntityCondition.makeCondition("primaryParentGroupId", EntityOperator.EQUALS, type));
			}
			conditionList.add(EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS,"RT_BOOTH_GROUP"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> routesList = delegator.findList("FacilityGroup",	condition, null, null, null, false);
			if (UtilValidate.isNotEmpty(routesList)) {
				routesIdsList = (List) EntityUtil.getFieldListFromEntityList(routesList, "facilityGroupId", true);
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("routeIdsList", routesIdsList);
		return result;
	}

	public static Map<String, Object> getRoutes(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<String> routes = FastList.newInstance();
		try {
			List<GenericValue> facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS, "ROUTE"), null, UtilMisc.toList("facilityId"), null, false);
			routes = EntityUtil.getFieldListFromEntityList(facilities,"facilityId", false);
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

		List<String> zones = FastList.newInstance();
		try {
			List<GenericValue> facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS, "ZONE"), null, UtilMisc.toList("facilityId"), null, false);
			zones = EntityUtil.getFieldListFromEntityList(facilities,"facilityId", false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("zonesList", zones);
		return result;
	}

	public static Map<String, Object> getZonesComissionRates(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map zonesComissionRates = FastMap.newInstance();
		Map<String, Object> zonesMap = getZones(delegator);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List zonesList = (List) zonesMap.get("zonesList");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
		inputRateAmt.put("periodTypeId", "RATE_HOUR");
		inputRateAmt.put("rateCurrencyUomId", "INR");
		try {
			for (int i = 0; i < zonesList.size(); i++) {
				inputRateAmt.put("rateTypeId", zonesList.get(i) + "_ZN_MRGN");
				result = dispatcher.runSync("getRateAmount", inputRateAmt);
				zonesComissionRates.put(zonesList.get(i),result.get("rateAmount"));
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result = ServiceUtil.returnSuccess();
		result.put("zonesComissionRates", zonesComissionRates);
		return result;
	}

	public static Map getBoothPaidPayments(DispatchContext dctx,Map<String, ? extends Object> context) {
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
		List facilityIdsList = (List) context.get("facilityIdsList");
		Map boothRouteIdsMap = FastMap.newInstance();
		boolean isByParty = Boolean.FALSE;
		String ownerPartyId = "";
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = Boolean.TRUE;
		}
		boolean enableCRInst = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("enableCRInst"))) {
			enableCRInst = (Boolean) context.get("enableCRInst");
		}
		boolean excludeCreditNote = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("excludeCreditNote"))) {
			excludeCreditNote = (Boolean) context.get("excludeCreditNote");
		}
		boolean orderByBankName = Boolean.FALSE;
		if (context.get("orderByBankName") != null) {
			orderByBankName = (Boolean) context.get("orderByBankName");
		}
		boolean findByInstrumentDate = Boolean.FALSE;
		if (context.get("findByInstrumentDate") != null) {
			findByInstrumentDate = (Boolean) context.get("findByInstrumentDate");
		}
		boolean findByCreatedDate = Boolean.FALSE;
		if (context.get("findByCreatedDate") != null) {
			findByCreatedDate = (Boolean) context.get("findByCreatedDate");
		}

		boolean unDepositedChequesOnly = Boolean.FALSE;
		if (context.get("unDepositedChequesOnly") != null) {
			unDepositedChequesOnly = (Boolean) context.get("unDepositedChequesOnly");
		}

		boolean excludeAdhocPayments = Boolean.TRUE;// always excluding if externally not set
		if (context.get("excludeAdhocPayments") != null) {
			excludeAdhocPayments = (Boolean) context.get("excludeAdhocPayments");
		}
	        boolean isForCalOB = Boolean.FALSE;
			if(UtilValidate.isNotEmpty(context.get("isForCalOB")) && ((String)context.get("isForCalOB")).equals("Y")){
				isForCalOB = Boolean.TRUE;
			}
		String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
		List paymentIds = (List) context.get("paymentIds");
		boolean onlyCurrentDues = Boolean.FALSE;
		if (context.get("onlyCurrentDues") != null) {
			onlyCurrentDues = (Boolean) context.get("onlyCurrentDues");
		}
		if (onlyCurrentDues) {
			boothsPaymentsDetail = getCurrentDuesBoothPaidPayments(dctx,context);
			return boothsPaymentsDetail;
		}
		if (!UtilValidate.isEmpty(context.get("fromDate"))) {
			fromDate = (Timestamp) context.get("fromDate");
		}
		Map boothRtInMap = UtilMisc.toMap("facilityId", facilityId);
		boothRtInMap.put("facilityIdsList", facilityIdsList);
		if (UtilValidate.isNotEmpty(context.get("facilityIdsList"))) {// if not Empty Consider them only
			boothRtInMap.put("facilityTypeId", "BOOTH");
		}
		boothRtInMap.put("effectiveDate", fromDate);

		// Map boothRouteResultMap =getBoothsRouteMap(delegator,boothRtInMap);
		Map boothRouteResultMap = getBoothsRouteByShipment(delegator,boothRtInMap);
		facilityIdsList = (List) boothRouteResultMap.get("boothIdsList");
		// get All booths RouteIds

		if (UtilValidate.isNotEmpty(boothRouteResultMap)) {
			boothRouteIdsMap = (Map) boothRouteResultMap.get("boothRouteIdsMap");// to get routeIds
		}
		if (UtilValidate.isNotEmpty(context.get("facilityIdsList"))) {// if not Empty Consider tehm only
			facilityIdsList = (List) context.get("facilityIdsList");
		}
		List facilityPartyIds = FastList.newInstance();
		if (isByParty && UtilValidate.isNotEmpty(facilityIdsList)) {
			try {
				List<GenericValue> ownerPartyFacilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIdsList), UtilMisc.toSet("ownerPartyId"), null, null, false);
				facilityPartyIds = EntityUtil.getFieldListFromEntityList(ownerPartyFacilities, "ownerPartyId", true);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error in fetching facility data");
			}
		}
		Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
		if (paymentDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + paymentDate,module);

			}
		}
		Locale locale = (Locale) context.get("locale");
		Timestamp dayBegin = UtilDateTime.getDayStart(paymentTimestamp,TimeZone.getDefault(), locale);
		Timestamp dayEnd = UtilDateTime.getDayEnd(paymentTimestamp,TimeZone.getDefault(), locale);
		List exprList = FastList.newInstance();
		// get Payments for period if fromDate and thruDate available in params
		if (!UtilValidate.isEmpty(context.get("fromDate"))) {
			fromDate = (Timestamp) context.get("fromDate");
			dayBegin = UtilDateTime.getDayStart(fromDate);
		}
		if (!UtilValidate.isEmpty(context.get("thruDate"))) {
			thruDate = (Timestamp) context.get("thruDate");
			dayEnd = UtilDateTime.getDayEnd(thruDate);
		}

		if (facilityId != null) {
			try {
				GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
				if (facilityDetail == null	|| (!facilityDetail.getString("facilityTypeId").equals("ZONE") && (!facilityDetail.getString("facilityTypeId").equals("ROUTE")) && !facilityDetail.getString("facilityTypeId").equals("BOOTH"))) {
					Debug.logInfo("facilityId '" + facilityId+ "'is not a Booth or Zone ", "");
					return ServiceUtil.returnError("facilityId '" + facilityId+ "'is not a Booth or Zone ");
				}
				ownerPartyId = facilityDetail.getString("ownerPartyId");
				if (isByParty) {
					if (facilityDetail.getString("facilityTypeId").equals(
							"ZONE")) {
						exprList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN,facilityPartyIds));
					} else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")) {
						exprList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN,facilityPartyIds));
					} else {
						exprList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,ownerPartyId));
					}

				} else {
					if (facilityDetail.getString("facilityTypeId").equals("ZONE")) {
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,facilityIdsList));
					} else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")) {
						exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,facilityIdsList));
					} else {
						exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));					
					}
				}

			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("facilityId '" + facilityId+ "' error");
			}
		}

		if (findByInstrumentDate) {// findByInstrumentDate filtering used for cheQue paymentChecklist
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("instrumentDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),EntityOperator.AND, 
					EntityCondition.makeCondition("instrumentDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
		} else if (findByCreatedDate) {// findByCreatedDate filtering used for cheQue paymentChecklist
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),	EntityOperator.AND, EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd)));
		} else {
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd)));
		}
			if(isForCalOB){//for Party Ledger we have to consider Cheque Voided payments also
				exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"),EntityOperator.OR, 
				    	EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_VOID"),EntityOperator.AND, EntityCondition.makeCondition("chequeReturns", EntityOperator.EQUALS, "Y"))));
			}else{
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID", "PMNT_CANCELLED")));
			}
		if (!UtilValidate.isEmpty(userLoginId)) {
			exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS,userLoginId));
		}
		if (!UtilValidate.isEmpty(paymentMethodTypeId)) {
			exprList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, paymentMethodTypeId));
		}
		if (excludeCreditNote) {// consider only Flag is True
			exprList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.NOT_EQUAL, "CREDITNOTE_PAYIN"));
		}
		
		if (!UtilValidate.isEmpty(paymentIds)) {
			exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.IN, paymentIds));
		}
		if (unDepositedChequesOnly) {// consider If Undeposited is true
			exprList.add(EntityCondition.makeCondition("finAccountTransId",EntityOperator.EQUALS, null));
		}
		if (excludeAdhocPayments) {
			// get AdhocSale Payments to exclude them
			Map adhocSaleDetails = getAdhocSalePayments(dctx,UtilMisc.toMap("estimatedShipDate", dayBegin));
			if (UtilValidate.isNotEmpty(adhocSaleDetails.get("paymentIds"))) {
				List adhocPaymentIds = (List) adhocSaleDetails.get("paymentIds");
				exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.NOT_IN, adhocPaymentIds));
			}
		}

		EntityCondition condition = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		List paymentsList = FastList.newInstance();
		// order by condition will change basing on requirement;
		List<String> orderBy = UtilMisc.toList("facilityId","-lastModifiedDate");
		if (orderByBankName) {
			orderBy = UtilMisc.toList("issuingAuthority", "facilityId","-lastModifiedDate");
		}
		try {
			paymentsList = delegator.findList("PaymentAndFacility", condition,null, orderBy, null, false);
			String tempFacilityId = "";
			String tempPartyId = "";
			Map tempPayment = FastMap.newInstance();
			for (int i = 0; i < paymentsList.size(); i++) {
				GenericValue boothPayment = (GenericValue) paymentsList.get(i);

				if (isByParty) {

					if (tempPartyId == "") {
						tempPartyId = boothPayment.getString("partyIdFrom");
						tempPayment = FastMap.newInstance();
						tempPayment.put("facilityId", boothPayment.getString("facilityId"));
						tempPayment.put("partyIdFrom", tempPartyId);
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempPartyId))) {
							tempPayment.put("routeId",boothRouteIdsMap.get(tempPartyId));
						}
						tempPayment.put("paymentDate",boothPayment.getTimestamp("paymentDate"));
						tempPayment.put("paymentId",boothPayment.getString("paymentId"));
						tempPayment.put("paymentLocation",boothPayment.getString("paymentLocation"));
						tempPayment.put("paymentMethodTypeId",boothPayment.getString("paymentMethodTypeId"));
						tempPayment.put("amount", BigDecimal.ZERO);
						tempPayment.put("userId",boothPayment.getString("createdByUserLogin"));
					}
					if (!(tempPartyId.equals(boothPayment.getString("partyIdFrom")))) {
						// populating paymentMethodTypeId for paid invoices
						boothPaymentsList.add(tempPayment);
						tempPartyId = boothPayment.getString("partyIdFrom");
						tempPayment = FastMap.newInstance();
						tempPayment.put("facilityId",boothPayment.getString("facilityId"));
						tempPayment.put("partyIdFrom",boothPayment.getString("partyIdFrom"));
						tempPayment.put("routeId",boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempPartyId))) {
							tempPayment.put("routeId",boothRouteIdsMap.get(tempPartyId));
						}
						tempPayment.put("paymentDate",boothPayment.getTimestamp("paymentDate"));
						tempPayment.put("paymentId",boothPayment.getString("paymentId"));
						tempPayment.put("paymentMethodTypeId",boothPayment.getString("paymentMethodTypeId"));
						tempPayment.put("amount",boothPayment.getBigDecimal("amount"));
						tempPayment.put("userId",boothPayment.getString("createdByUserLogin"));
					} else {
						tempPayment.put("amount", (boothPayment.getBigDecimal("amount")).add((BigDecimal) tempPayment.get("amount")));
					}
				} else {
					if (tempFacilityId == "") {
						tempFacilityId = boothPayment.getString("facilityId");
						tempPayment.put("facilityId",boothPayment.getString("facilityId"));
						// tempPayment.put("routeId",
						// boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
							tempPayment.put("routeId",boothRouteIdsMap.get(tempFacilityId));
						}
						tempPayment.put("paymentDate",boothPayment.getTimestamp("paymentDate"));
						tempPayment.put("paymentId",boothPayment.getString("paymentId"));
						tempPayment.put("paymentLocation",boothPayment.getString("paymentLocation"));
						tempPayment.put("paymentMethodTypeId",boothPayment.getString("paymentMethodTypeId"));
						tempPayment.put("amount", BigDecimal.ZERO);
						tempPayment.put("userId",boothPayment.getString("createdByUserLogin"));
					}
					if (!(tempFacilityId.equals(boothPayment.getString("facilityId")))) {
						// populating paymentMethodTypeId for paid invoices
						boothPaymentsList.add(tempPayment);
						tempFacilityId = boothPayment.getString("facilityId");
						tempPayment = FastMap.newInstance();
						tempPayment.put("facilityId",boothPayment.getString("facilityId"));
						tempPayment.put("routeId",boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
							tempPayment.put("routeId",boothRouteIdsMap.get(tempFacilityId));
						}
						tempPayment.put("paymentDate",boothPayment.getTimestamp("paymentDate"));
						tempPayment.put("paymentId",boothPayment.getString("paymentId"));
						tempPayment.put("paymentMethodTypeId",boothPayment.getString("paymentMethodTypeId"));
						tempPayment.put("amount",boothPayment.getBigDecimal("amount"));
						tempPayment.put("userId",boothPayment.getString("createdByUserLogin"));

					} else {
						tempPayment.put("amount", (boothPayment.getBigDecimal("amount")).add((BigDecimal) tempPayment.get("amount")));
					}
				}

				if ((i == paymentsList.size() - 1)) {
					boothPaymentsList.add(tempPayment);
				}
			}
		} 
		catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		// rounding off booth amounts
		List tempPaymentsList = FastList.newInstance();
		for (int i = 0; i < boothPaymentsList.size(); i++) {
			Map entry = FastMap.newInstance();
			entry.putAll((Map) boothPaymentsList.get(i));
			BigDecimal roundingAmount = ((BigDecimal) entry.get("amount")).setScale(0, rounding);
			entry.put("amount", roundingAmount);
			invoicesTotalAmount = invoicesTotalAmount.add(roundingAmount);
			tempPaymentsList.add(entry);
		}
		boothsPaymentsDetail.put("invoicesTotalAmount", invoicesTotalAmount);
		boothsPaymentsDetail.put("boothPaymentsList", tempPaymentsList);
		boothsPaymentsDetail.put("paymentsList", paymentsList);
		boothsPaymentsDetail.put("boothRouteIdsMap", boothRouteIdsMap);// for reporting purpose (boothId,routeId) for which route tat booth belongsto
		return boothsPaymentsDetail;
	}

	public static Map getBoothPaidDepositedPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
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
		List facilityIdsList = (List) context.get("facilityIdsList");
		Map boothRouteIdsMap = FastMap.newInstance();

		boolean orderByBankName = Boolean.FALSE;
		if (context.get("orderByBankName") != null) {
			orderByBankName = (Boolean) context.get("orderByBankName");
		}
		boolean excludeAdhocPayments = Boolean.TRUE;// always excluding if externally not set
		if (context.get("excludeAdhocPayments") != null) {
			excludeAdhocPayments = (Boolean) context.get("excludeAdhocPayments");
		}

		String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");

		Map boothRtInMap = UtilMisc.toMap("facilityId", facilityId);
		boothRtInMap.put("facilityIdsList", facilityIdsList);
		if (UtilValidate.isNotEmpty(context.get("facilityIdsList"))) {// if not Empty Consider them only
			boothRtInMap.put("facilityTypeId", "BOOTH");
		}
		boothRtInMap.put("effectiveDate", fromDate);
		Map boothRouteResultMap = getBoothsRouteByShipment(delegator, boothRtInMap);
		facilityIdsList = (List) boothRouteResultMap.get("boothIdsList");
		// get All booths RouteIds
		if (UtilValidate.isNotEmpty(boothRouteResultMap)) {
			boothRouteIdsMap = (Map) boothRouteResultMap.get("boothRouteIdsMap");// to get routeIds
		}
		if (UtilValidate.isNotEmpty(context.get("facilityIdsList"))) {// if not Empty Consider tehm only
			facilityIdsList = (List) context.get("facilityIdsList");
		}
		Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
		if (paymentDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + paymentDate,module);

			}
		}
		Locale locale = (Locale) context.get("locale");
		Timestamp dayBegin = UtilDateTime.getDayStart(paymentTimestamp,TimeZone.getDefault(), locale);
		Timestamp dayEnd = UtilDateTime.getDayEnd(paymentTimestamp,	TimeZone.getDefault(), locale);
		List exprList = FastList.newInstance();
		// get Payments for period if fromDate and thruDate available in params
		if (!UtilValidate.isEmpty(context.get("fromDate"))) {
			fromDate = (Timestamp) context.get("fromDate");
			dayBegin = UtilDateTime.getDayStart(fromDate);
		}
		if (!UtilValidate.isEmpty(context.get("thruDate"))) {
			thruDate = (Timestamp) context.get("thruDate");
			dayEnd = UtilDateTime.getDayEnd(thruDate);
		}

		if (UtilValidate.isNotEmpty(context.get("facilityIdsList"))) {
			exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIdsList));
		}
		/*if(findByInstrumentDate){//findByInstrumentDate  filtering used for  cheQue paymentChecklist
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("instrumentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin), EntityOperator.AND, EntityCondition.makeCondition("instrumentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
		}else{
			exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin), EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
		}*/

		exprList.add(EntityCondition.makeCondition("finAccountTransactionDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
		exprList.add(EntityCondition.makeCondition("finAccountTransactionDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID", "PMNT_CANCELLED")));
		if (!UtilValidate.isEmpty(userLoginId)) {
			exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS,userLoginId));
		}
		if (!UtilValidate.isEmpty(paymentMethodTypeId)) {
			exprList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, paymentMethodTypeId));
		}
		exprList.add(EntityCondition.makeCondition("finAccountTransId",EntityOperator.NOT_EQUAL, null));
		exprList.add(EntityCondition.makeCondition("finAccountTransStatusId",EntityOperator.IN,UtilMisc.toList("FINACT_TRNS_APPROVED", "FINACT_TRNS_CREATED")));
		// TO DO : Need to revisit ExcludeAdhoc Payments logic
		if (excludeAdhocPayments) {
			// get AdhocSale Payments to exclude them
			Map adhocSaleDetails = getAdhocSalePayments(dctx,UtilMisc.toMap("estimatedShipDate", dayBegin));
			if (UtilValidate.isNotEmpty(adhocSaleDetails.get("paymentIds"))) {
				List adhocPaymentIds = (List) adhocSaleDetails.get("paymentIds");
				exprList.add(EntityCondition.makeCondition("paymentId",	EntityOperator.NOT_IN, adhocPaymentIds));
			}
		}
		EntityCondition condition = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		List paymentsList = FastList.newInstance();
		// order by condition will change basing on requirement;
		List<String> orderBy = UtilMisc.toList("-lastModifiedDate");
		if (orderByBankName) {
			orderBy = UtilMisc.toList("issuingAuthority", "-lastModifiedDate");
		}
		try {
			paymentsList = delegator.findList("PaymentAndFacilityAndFinAcctTrans", condition, null,	orderBy, null, false);
			String tempFacilityId = "";
			Map tempPayment = FastMap.newInstance();
			for (int i = 0; i < paymentsList.size(); i++) {
				GenericValue boothPayment = (GenericValue) paymentsList.get(i);
				if (tempFacilityId == "") {
					tempFacilityId = boothPayment.getString("facilityId");
					tempPayment.put("facilityId",boothPayment.getString("facilityId"));
					if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
						tempPayment.put("routeId",boothRouteIdsMap.get(tempFacilityId));
					}
					tempPayment.put("paymentDate",	boothPayment.getTimestamp("paymentDate"));
					tempPayment.put("paymentId",boothPayment.getString("paymentId"));
					tempPayment.put("paymentLocation",	boothPayment.getString("paymentLocation"));
					tempPayment.put("paymentMethodTypeId",boothPayment.getString("paymentMethodTypeId"));
					tempPayment.put("amount", BigDecimal.ZERO);
					tempPayment.put("userId",boothPayment.getString("createdByUserLogin"));
				}
				if (!(tempFacilityId.equals(boothPayment.getString("facilityId")))) {
					// populating paymentMethodTypeId for paid invoices
					boothPaymentsList.add(tempPayment);
					tempFacilityId = boothPayment.getString("facilityId");
					tempPayment = FastMap.newInstance();
					tempPayment.put("facilityId",boothPayment.getString("facilityId"));
					tempPayment.put("routeId",	boothPayment.getString("parentFacilityId"));
					if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
						tempPayment.put("routeId",boothRouteIdsMap.get(tempFacilityId));
					}
					tempPayment.put("paymentDate",boothPayment.getTimestamp("paymentDate"));
					tempPayment.put("paymentId",boothPayment.getString("paymentId"));
					tempPayment.put("paymentMethodTypeId",boothPayment.getString("paymentMethodTypeId"));
					tempPayment.put("amount",boothPayment.getBigDecimal("amount"));
					tempPayment.put("userId",boothPayment.getString("createdByUserLogin"));

				} else {
					tempPayment.put("amount", (boothPayment.getBigDecimal("amount")).add((BigDecimal) tempPayment.get("amount")));
				}
				if ((i == paymentsList.size() - 1)) {
					boothPaymentsList.add(tempPayment);
				}
			}
		} 
		catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		// rounding off booth amounts
		List tempPaymentsList = FastList.newInstance();
		for (int i = 0; i < boothPaymentsList.size(); i++) {
			Map entry = FastMap.newInstance();
			entry.putAll((Map) boothPaymentsList.get(i));
			BigDecimal roundingAmount = ((BigDecimal) entry.get("amount")).setScale(0, rounding);
			entry.put("amount", roundingAmount);
			invoicesTotalAmount = invoicesTotalAmount.add(roundingAmount);
			tempPaymentsList.add(entry);
		}
		boothsPaymentsDetail.put("invoicesTotalAmount", invoicesTotalAmount);
		boothsPaymentsDetail.put("boothPaymentsList", tempPaymentsList);
		boothsPaymentsDetail.put("paymentsList", paymentsList);
		boothsPaymentsDetail.put("boothRouteIdsMap", boothRouteIdsMap);// for reporting purpose (boothId,routeId) for which route tat booth belongsto 
		return boothsPaymentsDetail;
	}

	public static Map<String, Object> absenteeOverrideForBooth(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		Locale locale = (Locale) context.get("locale");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		BigDecimal overrideAmount = (BigDecimal) context.get("overrideAmount");
		Map<String, Object> result = new HashMap<String, Object>();
		Map boothsPaymentsDetail = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boothsPaymentsDetail = getBoothPayments(delegator, dispatcher,userLogin, UtilDateTime.nowDateString("yyyy-MM-dd"), null,facilityId, null, Boolean.FALSE);
		List boothPaymentsList = (List) boothsPaymentsDetail.get("boothPaymentsList");
		if (UtilValidate.isEmpty(boothPaymentsList)) {
			Debug.logError("No payment dues found for booth; " + facilityId,module);
			return ServiceUtil.returnError("No payment dues found for Booth "+ facilityId);
		}
		GenericValue newEntity = delegator.makeValue("AbsenteeOverride");
		newEntity.set("boothId", facilityId);
		newEntity.set("supplyDate", new java.sql.Date((new Date().getTime())+ (1000 * 60 * 60 * 24)));
		newEntity.set("amount", overrideAmount);
		newEntity.set("createdDate", UtilDateTime.nowTimestamp());
		newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		try {
			delegator.create(newEntity);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> removeAbsenteeOverride(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		Locale locale = (Locale) context.get("locale");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		Map<String, Object> result = new HashMap<String, Object>();
		Map boothsPaymentsDetail = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		GenericValue newEntity = delegator.makeValue("AbsenteeOverride");
		newEntity.set("boothId", facilityId);
		try {
			newEntity.set("supplyDate",	((new DateTimeConverters.TimestampToSqlDate()).convert(supplyDate)));
		} catch (ConversionException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}

		try {
			delegator.removeValue(newEntity);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map getCurrentDuesBoothPaidPayments(DispatchContext dctx,Map<String, ? extends Object> context) {
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
		if (paymentDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + paymentDate,module);

			}
		}

		Timestamp dayBegin = UtilDateTime.getDayStart(paymentTimestamp,TimeZone.getDefault(), locale);
		Timestamp dayEnd = UtilDateTime.getDayEnd(paymentTimestamp,	TimeZone.getDefault(), locale);
		List exprList = FastList.newInstance();
		List shipmentIds = getShipmentIds(delegator, UtilDateTime.toDateString(	paymentTimestamp, "yyyy-MM-dd HH:mm:ss"), null);
		List invoiceIds = FastList.newInstance();
		boolean enableSoCrPmntTrack = Boolean.FALSE;
		try {
			GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName",	"enableSoCrPmntTrack"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack)&& (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
				enableSoCrPmntTrack = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		if (enableSoCrPmntTrack) {
			exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,UtilMisc.toList("CASH", "SPECIAL_ORDER", "CREDIT")));
		} else {
			exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"));
		}
		exprList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, new ArrayList(shipmentIds)));
		EntityCondition paramCond = EntityCondition.makeCondition(exprList,EntityOperator.AND);

		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try {
			List boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null,UtilMisc.toList("parentFacilityId", "originFacilityId","-estimatedDeliveryDate"), findOptions, false);
			invoiceIds = EntityUtil.getFieldListFromEntityList(boothOrdersList,	"invoiceId", false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		exprList.clear();
		exprList.add(EntityCondition.makeCondition("invoiceId",	EntityOperator.IN, invoiceIds));
		if (facilityId != null) {
			try {
				GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
				if (facilityDetail == null	|| (!facilityDetail.getString("facilityTypeId").equals("ZONE") && (!facilityDetail.getString("facilityTypeId").equals("ROUTE")) && !facilityDetail.getString("facilityTypeId").equals("BOOTH"))) {
					Debug.logInfo("facilityId '" + facilityId+ "'is not a Booth or Zone ", "");
					return ServiceUtil.returnError("facilityId '" + facilityId	+ "'is not a Booth or Zone ");
				}
				if (facilityDetail.getString("facilityTypeId").equals("ZONE")) {
					exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,getZoneBooths(delegator, facilityId)));		
				} else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")) {
					exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,getRouteBooths(delegator, facilityId)));
				} else {
					exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
				}

			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("facilityId '" + facilityId+ "' error");
			}
		}
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate",	EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd)));
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID", "PMNT_CANCELLED")));
		if (!UtilValidate.isEmpty(userLoginId)) {
			exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS,userLoginId));
		}
		if (!UtilValidate.isEmpty(paymentMethodTypeId)) {
			exprList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, paymentMethodTypeId));
		}
		if (!UtilValidate.isEmpty(paymentIds)) {
			exprList.add(EntityCondition.makeCondition("paymentId",EntityOperator.IN, paymentIds));
		}
		EntityCondition condition = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		List paymentsList = FastList.newInstance();
		try {
			paymentsList = delegator.findList("PaymentFacilityAndApplication",condition, null, UtilMisc.toList("-lastModifiedDate"),null, false);
			for (int i = 0; i < paymentsList.size(); i++) {
				GenericValue boothPayment = (GenericValue) paymentsList.get(i);
				Map tempPayment = FastMap.newInstance();
				tempPayment.put("facilityId",boothPayment.getString("facilityId"));
				tempPayment.put("routeId",boothPayment.getString("parentFacilityId"));
				tempPayment.put("paymentDate",boothPayment.getTimestamp("paymentDate"));
				tempPayment.put("paymentId",boothPayment.getString("paymentId"));
				tempPayment.put("paymentMethodTypeId",boothPayment.getString("paymentMethodTypeId"));
				tempPayment.put("amount",boothPayment.getBigDecimal("amountApplied"));
				tempPayment.put("userId",boothPayment.getString("createdByUserLogin"));
				boothPaymentsList.add(tempPayment);
			}
		} 
		catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		// rounding off booth amounts
		List tempPaymentsList = FastList.newInstance();
		for (int i = 0; i < boothPaymentsList.size(); i++) {
			Map entry = FastMap.newInstance();
			entry.putAll((Map) boothPaymentsList.get(i));
			BigDecimal roundingAmount = ((BigDecimal) entry.get("amount")).setScale(0, rounding);
			entry.put("amount", roundingAmount);
			invoicesTotalAmount = invoicesTotalAmount.add(roundingAmount);
			tempPaymentsList.add(entry);
		}
		boothsPaymentsDetail.put("invoicesTotalAmount", invoicesTotalAmount);
		boothsPaymentsDetail.put("boothPaymentsList", tempPaymentsList);
		return boothsPaymentsDetail;
	}

	// This method will give only the Pending Payments
	public static Map getBoothPayments(Delegator delegator,	LocalDispatcher dispatcher, GenericValue userLogin,String paymentDate, String invoiceStatusId, String facilityId,String paymentMethodTypeId, boolean onlyCurrentDues,boolean duesByParty, boolean enableCRInst) {
		Map boothsPaymentsDetail = getBoothReceivablePayments(delegator,dispatcher, userLogin, paymentDate, invoiceStatusId,facilityId, paymentMethodTypeId, onlyCurrentDues, Boolean.TRUE,	duesByParty, enableCRInst);
		return boothsPaymentsDetail;
	}

	public static Map getBoothPayments(Delegator delegator,	LocalDispatcher dispatcher, GenericValue userLogin,String paymentDate, String invoiceStatusId, String facilityId,String paymentMethodTypeId, boolean onlyCurrentDues) {
		Map boothsPaymentsDetail = getBoothReceivablePayments(delegator,dispatcher, userLogin, paymentDate, invoiceStatusId,facilityId, paymentMethodTypeId, onlyCurrentDues, Boolean.TRUE,	Boolean.FALSE, Boolean.FALSE);
		return boothsPaymentsDetail;
	}
	public static Map getBoothReceivablePayments(Delegator delegator,LocalDispatcher dispatcher, GenericValue userLogin,String paymentDate, String invoiceStatusId, String facilityId,String paymentMethodTypeId, boolean onlyCurrentDues,	boolean isPendingDues, boolean duesByParty, boolean enableCRInst) {
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("invoiceStatusId", invoiceStatusId);
		context.put("facilityId", facilityId);
		context.put("onlyCurrentDues", onlyCurrentDues);
		context.put("isPendingDues", isPendingDues);
		context.put("duesByParty", duesByParty);
		context.put("enableCRInst", enableCRInst);
		Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
		if (paymentDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(paymentDate));
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + paymentDate,module);

			}

		}
		if (onlyCurrentDues) {
			context.put("fromDate", UtilDateTime.getDayStart(paymentTimestamp));
		} else {
			context.put("fromDate", null);
		}
		context.put("thruDate", UtilDateTime.getDayEnd(paymentTimestamp));
		Map boothsPaymentsDetail = getBoothReceivablePaymentsForPeriod(dispatcher.getDispatchContext(), context);
		return boothsPaymentsDetail;
	}

	public static Map<String, Object> createFacilityInvoice(DispatchContext dctx, Map<String, ? extends Object> context) {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> serviceResults;
		String facilityId = (String) context.get("facilityId");
		BigDecimal invoiceAmount = (BigDecimal) context.get("invoiceAmount");
		String invoiceItemTypeId = (String) context.get("invoiceItemTypeId");
		String invoiceTypeId = (String) context.get("invoiceTypeId");
		Timestamp invoiceDateParameter = (Timestamp) context.get("invoiceDate");
		String description = (String) context.get("description");

		String referenceNumber = (String) context.get("referenceNumber");

		Timestamp invoiceDate = UtilDateTime.getDayStart(invoiceDateParameter);
		String partyIdFrom = "Company";
		String partyId = "";
		GenericValue facility;
		try {
			facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), false);
			if (UtilValidate.isEmpty(facility)) {
				Debug.logError(facilityId + "====is not a booth", module);
				return ServiceUtil.returnError(facilityId+ "====is not a booth");
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		if (UtilValidate.isNotEmpty(facility)) {
			partyId = facility.getString("ownerPartyId");
		}
		Map<String, Object> createInvoiceContext = FastMap.newInstance();
		createInvoiceContext.put("partyId", partyId);
		createInvoiceContext.put("partyIdFrom", partyIdFrom);
		createInvoiceContext.put("invoiceDate", invoiceDate);
		createInvoiceContext.put("dueDate", invoiceDate);
		createInvoiceContext.put("facilityId", facilityId);
		createInvoiceContext.put("invoiceTypeId", invoiceTypeId);
		createInvoiceContext.put("referenceNumber", referenceNumber);
		createInvoiceContext.put("description", description);
		createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
		createInvoiceContext.put("userLogin", userLogin);

		String invoiceId = null;
		try {
			serviceResults = dispatcher.runSync("createInvoice",createInvoiceContext);
			if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError("There was an error while creating Invoice"+ ServiceUtil.getErrorMessage(serviceResults));
			}
			invoiceId = (String) serviceResults.get("invoiceId");
		} catch (GenericServiceException e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		// createInvoiceItem
		Map input = UtilMisc.toMap("userLogin", userLogin, "invoiceId",invoiceId);
		input.put("invoiceItemTypeId", invoiceItemTypeId);
		input.put("quantity", BigDecimal.ONE);
		input.put("amount", invoiceAmount);
		try {
			serviceResults = dispatcher.runSync("createInvoiceItem", input);
			if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError("Unable to create Invoice Item",null, null, serviceResults);
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}

		// set invoice status
		try {
			serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId",	"INVOICE_APPROVED", "userLogin", userLogin));
			if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError("Unable to set Invoice Status",null, null, serviceResults);
			}
			serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId","INVOICE_READY", "userLogin", userLogin));
			if (ServiceUtil.isError(serviceResults)) {
				return ServiceUtil.returnError("Unable to set Invoice Status",null, null, serviceResults);
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}

		Map<String, Object> result = ServiceUtil.returnSuccess("Payment Cancelled and Penalty Invoice ("+ invoiceId + ") Created Sucessfully" + " for Party : "	+ facilityId);
		return result;
	}

	public static Map<String, Object> getPaymentMethodTypeForBooth(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		String facilityId = (String) context.get("facilityId");
		List facilityIds = FastList.newInstance();
		Map facilityPaymentMethod = FastMap.newInstance();
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		try {
			Map facilityOwner = FastMap.newInstance();
			if (UtilValidate.isEmpty(facilityId)) {
				// return ServiceUtil.returnSuccess();
				facilityIds = (List) getAllBooths(delegator, null).get("boothsList");
			} else {
				GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), false);
				if (facility.getString("facilityTypeId").equals("ROUTE")) {
					facilityIds = getRouteBooths(delegator, facilityId);
				} else {
					facilityIds.add(facilityId);
				}
			}
			List<GenericValue> facilityOwnerParty = delegator.findList("Facility", EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds), null, UtilMisc.toList("facilityId"), null, false);
			List ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilityOwnerParty, "ownerPartyId", false);
			for (GenericValue eachBooth : facilityOwnerParty) {
				facilityOwner.put(eachBooth.getString("ownerPartyId"),eachBooth.getString("facilityId"));
			}
			// PartyProfileDefaultAndFacilityAndMethodType

			List paymentTypeConditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId",EntityOperator.IN, ownerPartyIds));
			paymentTypeConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
			paymentTypeConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS, null), EntityOperator.OR,EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayEnd(fromDate))));
			EntityCondition paymentTypeCondition = EntityCondition.makeCondition(paymentTypeConditionList, EntityOperator.AND);
			List<GenericValue> paymentTypeList = delegator.findList("PartyProfileDefault", paymentTypeCondition, null, null,null, false);
			paymentTypeList = EntityUtil.filterByDate(paymentTypeList,UtilDateTime.getDayStart(fromDate));
			for (GenericValue eachBoothMeth : paymentTypeList) {
				String owner = eachBoothMeth.getString("partyId");
				facilityPaymentMethod.put(facilityOwner.get(owner),eachBoothMeth.getString("defaultPayMeth"));
				// facilityPaymentDescrption.put(facilityOwner.get(owner), eachBoothMeth.getString("defaultPayMeth"));
			}
			result.put("partyPaymentMethod", facilityPaymentMethod);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		return result;
	}

	public static Map<String, Object> getDaywiseProductReturnTotal(DispatchContext dctx, Map context) {
		//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
		//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String facilityId = (String) context.get("facilityId");
		List facilityIds = FastList.newInstance();
		Boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}
		List ownerPartyIds = FastList.newInstance();
		if (UtilValidate.isEmpty(facilityId)) {
			facilityIds = (List) getAllBooths(delegator, null)
					.get("boothsList");
		} else {
			try {
				GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), false);
				if (facility.getString("facilityTypeId").equals("ROUTE")) {
					facilityIds = getRouteBooths(delegator, facilityId);
				} else {
					facilityIds.add(facilityId);
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}

		}

		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		if (UtilValidate.isEmpty(thruDate)) {
			thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		}
		int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate)) + 1;
		if (isByParty) {
			try {
				List facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",	EntityOperator.IN, facilityIds), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilities, "ownerPartyId", true);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		}
		List<GenericValue> payments = FastList.newInstance();
		List conditionList = FastList.newInstance();
		/*
		 * if(isByParty){
		 * conditionList.add(EntityCondition.makeCondition("partyIdFrom",
		 * EntityOperator.IN, ownerPartyIds)); }else{
		 * conditionList.add(EntityCondition.makeCondition("facilityId",
		 * EntityOperator.IN, facilityIds)); }
		 */
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "PMNT_RECEIVED"));
		conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, "CREDITNOTE_PAYIN"));
		conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<String> boothIds = FastList.newInstance();
		try {
			payments = delegator.findList("Payment", condition, null, null,	null, false);
			if (isByParty) {
				boothIds = EntityUtil.getFieldListFromEntityList(payments,"partyIdFrom", true);
			} else {
				boothIds = EntityUtil.getFieldListFromEntityList(payments,"facilityId", true);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		Map boothReturnDetail = FastMap.newInstance();
		for (String boothId : boothIds) {
			List<GenericValue> boothCreditNoteDetails = FastList.newInstance();
			if (isByParty) {
				boothCreditNoteDetails = EntityUtil.filterByCondition(payments,EntityCondition.makeCondition("partyIdFrom",	EntityOperator.EQUALS, boothId));
			} else {
				boothCreditNoteDetails = EntityUtil.filterByCondition(payments,EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, boothId));
			}
			if (UtilValidate.isNotEmpty(boothCreditNoteDetails)) {
				Map returnDetails = FastMap.newInstance();
				Map dayWiseMap = FastMap.newInstance();
				BigDecimal totalAmount = BigDecimal.ZERO;
				for (GenericValue boothCreditNote : boothCreditNoteDetails) {
					Timestamp paymentDate = boothCreditNote.getTimestamp("paymentDate");
					BigDecimal amount = boothCreditNote.getBigDecimal("amount");
					totalAmount = totalAmount.add(amount);
					String payDate = UtilDateTime.toDateString(paymentDate,	"yyyy-MM-dd");
					if (dayWiseMap.containsKey(payDate)) {
						BigDecimal extAmt = (BigDecimal) dayWiseMap.get(payDate);
						BigDecimal tempTotalAmt = extAmt.add(amount);
						dayWiseMap.put(payDate, tempTotalAmt);
					} else {
						dayWiseMap.put(payDate, amount);
					}
				}
				returnDetails.put("daywiseReturnAmt", dayWiseMap);
				returnDetails.put("totalAmount", totalAmount);
				boothReturnDetail.put(boothId, returnDetails);
			}

		}
		result.put("productReturnTotals", boothReturnDetail);
		return result;
	}
	
	
	public static Map getBoothReceivablePaymentsForPeriod(DispatchContext dctx,Map<String, ? extends Object> context) {
		// Delegator delegator ,LocalDispatcher dispatcher ,GenericValue
		// userLogin,String paymentDate,String invoiceStatusId ,String
		// facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues
		// ,boolean isPendingDues){
		// TO DO:for now getting one shipment id we need to get pmand am
		// shipment id irrespective of Shipment type Id
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String invoiceStatusId = (String) context.get("invoiceStatusId");
		String facilityId = (String) context.get("facilityId");
		Boolean onlyCurrentDues = (Boolean) context.get("onlyCurrentDues");
		Boolean isPendingDues = (Boolean) context.get("isPendingDues");
		Boolean duesByParty = (Boolean) context.get("duesByParty");
		Boolean enableCRInst = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("enableCRInst"))) {
			enableCRInst = (Boolean) context.get("enableCRInst");
		}

		Timestamp fromDate = null;
		if (!UtilValidate.isEmpty(context.get("fromDate"))) {
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
		String ownerPartyId = "";
		if (thruDate != null) {
			shipmentIds = new HashSet(getShipmentIds(delegator, fromDate,thruDate));
		}
		// boothRouteResultMap
		// =getBoothsRouteMap(delegator,UtilMisc.toMap("facilityId",facilityId,"effectiveDate",fromDate));
		boothRouteResultMap = getBoothsRouteByShipment(delegator,UtilMisc.toMap("facilityId", facilityId, "effectiveDate",fromDate));
		facilityIdsList = (List) boothRouteResultMap.get("boothIdsList");
		if (UtilValidate.isNotEmpty(boothRouteResultMap)) {
			boothRouteIdsMap = (Map) boothRouteResultMap.get("boothRouteIdsMap");// to
			// get
			// routeIds
		}
		List facilityPartyIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(duesByParty) && duesByParty	&& UtilValidate.isNotEmpty(facilityIdsList)) {
			try {
				List<GenericValue> ownerPartyFacilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIdsList), UtilMisc.toSet("ownerPartyId"), null, null, false);
				facilityPartyIds = EntityUtil.getFieldListFromEntityList(ownerPartyFacilities, "ownerPartyId", true);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error in fetching facility data");
			}
		}

		Map resultCtx = (Map) getPartyProfileDafult(dispatcher.getDispatchContext(), UtilMisc.toMap("boothIds",	UtilMisc.toList(facilityIdsList), "supplyDate",	fromDate));
		Map partyPaymentMethodDesc = (Map) resultCtx.get("partyPaymentMethodDesc");
		Map partyProfileFacilityMap = (Map) resultCtx.get("partyProfileFacilityMap");
		// Map paymentMethod = (Map)getPaymentMethodTypeForBooth(dctx, UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin,"fromDate",fromDate)).get("partyPaymentMethod");
		Set currentDayShipments = new HashSet(getShipmentIds(delegator,UtilDateTime.toDateString(thruDate, "yyyy-MM-dd HH:mm:ss"),null));
		boolean enableSoCrPmntTrack = Boolean.FALSE;

		try {
			GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName",	"enableSoCrPmntTrack"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack) && (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
				enableSoCrPmntTrack = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		if (enableCRInst) {
			exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,	UtilMisc.toList("EMP_SUBSIDY", "CASH", "CREDIT")));
		} else {
			if (enableSoCrPmntTrack) {
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,	UtilMisc.toList("CASH", "SPECIAL_ORDER", "CREDIT")));
			} else {
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,	UtilMisc.toList("EMP_SUBSIDY", "CASH")));
			}
		}

		// exprListForParameters.add(EntityCondition.makeCondition("estimatedDeliveryDate",
		// EntityOperator.LESS_THAN_EQUAL_TO,
		// UtilDateTime.getDayEnd(paymentTimestamp)));
		if (facilityId != null) {
			try {
				GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
				if (facilityDetail == null	|| (!facilityDetail.getString("facilityTypeId").equals("ZONE")
								&& !facilityDetail.getString("facilityTypeId").equals("ROUTE") && !facilityDetail.getString("facilityTypeId").equals("BOOTH"))) {
					Debug.logInfo("facilityId '" + facilityId+ "'is not a Booth or Zone ", "");
					return ServiceUtil.returnError("facilityId '" + facilityId	+ "'is not a Booth or Zone ");
				}
				ownerPartyId = facilityDetail.getString("ownerPartyId");
				if (UtilValidate.isNotEmpty(duesByParty) && duesByParty) {
					if (facilityDetail.getString("facilityTypeId").equals("ZONE")) {
						exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,facilityPartyIds));
					} else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")) {
						exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,facilityPartyIds));
					} else {
						exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, ownerPartyId));
					}

				} else {
					if (facilityDetail.getString("facilityTypeId").equals("ZONE")) {
						exprListForParameters.add(EntityCondition.makeCondition("originFacilityId",	EntityOperator.IN, facilityIdsList));
					} else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")) {
						exprListForParameters.add(EntityCondition.makeCondition("originFacilityId",	EntityOperator.IN, facilityIdsList));
					} else {
						exprListForParameters.add(EntityCondition.makeCondition("originFacilityId",EntityOperator.EQUALS, facilityId));
					}
				}

			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}
		}

		if (invoiceStatusId != null) {
			exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.EQUALS, invoiceStatusId));
		} else {
			List invoiceStatusList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(isPendingDues) && isPendingDues) {
				invoiceStatusList = UtilMisc.toList("INVOICE_PAID",	"INVOICE_CANCELLED", "INVOICE_WRITEOFF");

			} else {
				invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
			}
			exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN,invoiceStatusList));
		}

		exprListForParameters.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, new ArrayList(shipmentIds)));

		// filter out booths owned by the APDDCF
		exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL, "Company"));
		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try {
			boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null,UtilMisc.toList("originFacilityId",	"-estimatedDeliveryDate"), findOptions, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		List<GenericValue> obInvoiceList = FastList.newInstance();
		if (UtilValidate.isNotEmpty(duesByParty) && duesByParty) {
			obInvoiceList = (List) getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityId", facilityId, "fromDate",fromDate, "thruDate", thruDate, "duesByParty", "Y")).get("invoiceList");
		} else {
			obInvoiceList = (List) getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityId", facilityId, "fromDate",fromDate, "thruDate", thruDate)).get("invoiceList");
		}
		boothOrdersList.addAll(obInvoiceList);
		boothOrdersList = EntityUtil.orderBy(boothOrdersList,UtilMisc.toList("originFacilityId", "-estimatedDeliveryDate"));
		Map<String, Object> totalAmount = FastMap.newInstance();
		List splInvoiceTypes = UtilMisc.toList(obInvoiceType, "SHOPEE_RENT", "MIS_INCOME_IN");
		if (!UtilValidate.isEmpty(boothOrdersList)) {
			List invoiceIds = EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", false);
			String tempFacilityId = "";
			String tempPartyId = "";
			Map tempPayment = FastMap.newInstance();
			for (int i = 0; i < boothOrdersList.size(); i++) {
				GenericValue boothPayment = (GenericValue) boothOrdersList.get(i);
				List paymentApplicationList = FastList.newInstance();
				Map invoicePaymentInfoMap = FastMap.newInstance();
				BigDecimal outstandingAmount = BigDecimal.ZERO;
				String invoiceTypeId = "";
				try {
					invoiceTypeId = boothPayment.getRelatedOne("Invoice").getString("invoiceTypeId");
				} catch (Exception e) {
					// TODO: handle exception
				}
				invoicePaymentInfoMap.put("invoiceId",boothPayment.getString("invoiceId"));
				invoicePaymentInfoMap.put("userLogin", userLogin);
				try {
					Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList",invoicePaymentInfoMap);
					if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
						Debug.logError(getInvoicePaymentInfoListResult.toString(),module);
						return ServiceUtil.returnError(null, null, null,getInvoicePaymentInfoListResult);
					}
					Map invoicePaymentInfo = (Map) ((List) getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
					outstandingAmount = (BigDecimal) invoicePaymentInfo.get("outstandingAmount");
					if (UtilValidate.isNotEmpty(isPendingDues)	&& !isPendingDues) {
						outstandingAmount = (BigDecimal) invoicePaymentInfo.get("amount");
					}

				} catch (GenericServiceException e) {
					// TODO: handle exception
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.toString());
				}
				if (UtilValidate.isNotEmpty(duesByParty) && duesByParty) {

					if (tempPartyId == "") {
						tempPartyId = boothPayment.getString("partyId");
						tempPayment = FastMap.newInstance();
						tempPayment.put("facilityId", boothPayment.getString("originFacilityId"));
						tempPayment.put("partyId", tempPartyId);
						tempPayment.put("routeId",boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempPartyId))) {
							tempPayment.put("routeId",boothRouteIdsMap.get(tempPartyId));
						}
						tempPayment.put("supplyDate", boothPayment.getTimestamp("estimatedDeliveryDate"));
						if (UtilValidate.isNotEmpty(partyPaymentMethodDesc.get(boothPayment.getString("partyId")))) {
							tempPayment.put("paymentMethodTypeDesc",partyPaymentMethodDesc.get(boothPayment.getString("partyId")));
						}
						if (UtilValidate.isNotEmpty(partyProfileFacilityMap.get(boothPayment.getString("partyId")))) {
							tempPayment.put("paymentMethodTypeId",partyProfileFacilityMap.get(boothPayment.getString("partyId")));
						}
						tempPayment.put("grandTotal", BigDecimal.ZERO);
						tempPayment.put("totalDue", BigDecimal.ZERO);
					}
					if (!(tempPartyId.equals(boothPayment.getString("partyId")))) {
						// populating paymentMethodTypeId for paid invoices
						boothPaymentsList.add(tempPayment);
						tempPartyId = boothPayment.getString("partyId");
						tempPayment = FastMap.newInstance();
						tempPayment.put("facilityId", boothPayment.getString("originFacilityId"));
						tempPayment.put("partyId",boothPayment.getString("partyId"));
						tempPayment.put("routeId",boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
							tempPayment.put("routeId",boothRouteIdsMap.get(tempFacilityId));
						}
						if (UtilValidate.isNotEmpty(partyProfileFacilityMap.get(boothPayment.getString("partyId")))) {
							tempPayment.put("paymentMethodTypeId",partyProfileFacilityMap.get(boothPayment.getString("partyId")));
						}
						if (UtilValidate.isNotEmpty(partyPaymentMethodDesc.get(boothPayment.getString("partyId")))) {
							tempPayment.put("paymentMethodTypeDesc",partyPaymentMethodDesc.get(boothPayment.getString("partyId")));
						}
						tempPayment.put("grandTotal", BigDecimal.ZERO);
						tempPayment.put("totalDue", BigDecimal.ZERO);
						if (currentDayShipments.contains(boothPayment.getString("shipmentId")) || ((thruDate.compareTo(UtilDateTime.getDayEnd(boothPayment.getTimestamp("estimatedDeliveryDate"))) == 0) && splInvoiceTypes.contains(invoiceTypeId))) {
							tempPayment.put("grandTotal", outstandingAmount);
							tempPayment.put("totalDue", outstandingAmount);
						} else {
							tempPayment.put("totalDue", outstandingAmount);
						}

						tempPayment.put("supplyDate", boothPayment.getTimestamp("estimatedDeliveryDate"));

					} else {
						if (currentDayShipments.contains(boothPayment.getString("shipmentId"))	|| ((thruDate.compareTo(UtilDateTime.getDayEnd(boothPayment.getTimestamp("estimatedDeliveryDate"))) == 0) && splInvoiceTypes.contains(invoiceTypeId))) {
							tempPayment.put("grandTotal", outstandingAmount.add((BigDecimal) tempPayment.get("grandTotal")));
							tempPayment.put("totalDue", outstandingAmount.add((BigDecimal) tempPayment.get("totalDue")));
						} else {
							tempPayment.put("totalDue", outstandingAmount.add((BigDecimal) tempPayment.get("totalDue")));
						}

					}

				} else {
					if (tempFacilityId == "") {
						tempFacilityId = boothPayment.getString("originFacilityId");
						tempPayment = FastMap.newInstance();
						tempPayment.put("facilityId", tempFacilityId);
						tempPayment.put("routeId",boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
							tempPayment.put("routeId",	boothRouteIdsMap.get(tempFacilityId));
						}
						tempPayment.put("supplyDate", boothPayment.getTimestamp("estimatedDeliveryDate"));
						if (UtilValidate.isNotEmpty(partyPaymentMethodDesc.get(tempFacilityId))) {
							tempPayment.put("paymentMethodTypeDesc",partyPaymentMethodDesc.get(tempFacilityId));
						}
						if (UtilValidate.isNotEmpty(partyProfileFacilityMap.get(tempFacilityId))) {
							tempPayment.put("paymentMethodTypeId",partyProfileFacilityMap.get(tempFacilityId));
						}
						tempPayment.put("grandTotal", BigDecimal.ZERO);
						tempPayment.put("totalDue", BigDecimal.ZERO);
					}
					if (!(tempFacilityId.equals(boothPayment.getString("originFacilityId")))) {
						// populating paymentMethodTypeId for paid invoices
						boothPaymentsList.add(tempPayment);
						tempFacilityId = boothPayment.getString("originFacilityId");
						tempPayment = FastMap.newInstance();
						tempPayment.put("facilityId",boothPayment.getString("originFacilityId"));
						tempPayment.put("routeId",boothPayment.getString("parentFacilityId"));
						if (UtilValidate.isNotEmpty(boothRouteIdsMap.get(tempFacilityId))) {
							tempPayment.put("routeId",boothRouteIdsMap.get(tempFacilityId));
						}
						if (UtilValidate.isNotEmpty(partyProfileFacilityMap.get(tempFacilityId))) {
							tempPayment.put("paymentMethodTypeId",partyProfileFacilityMap.get(tempFacilityId));
						}
						if (UtilValidate.isNotEmpty(partyPaymentMethodDesc.get(tempFacilityId))) {
							tempPayment.put("paymentMethodTypeDesc",partyPaymentMethodDesc.get(tempFacilityId));
						}
						tempPayment.put("grandTotal", BigDecimal.ZERO);
						tempPayment.put("totalDue", BigDecimal.ZERO);
						if (currentDayShipments.contains(boothPayment.getString("shipmentId"))|| ((thruDate.compareTo(UtilDateTime.getDayEnd(boothPayment.getTimestamp("estimatedDeliveryDate"))) == 0) && splInvoiceTypes.contains(invoiceTypeId))) {
							tempPayment.put("grandTotal", outstandingAmount);
							tempPayment.put("totalDue", outstandingAmount);
						} else {
							tempPayment.put("totalDue", outstandingAmount);
						}

						tempPayment.put("supplyDate", boothPayment.getTimestamp("estimatedDeliveryDate"));

					} else {
						if (currentDayShipments.contains(boothPayment.getString("shipmentId"))|| ((thruDate.compareTo(UtilDateTime.getDayEnd(boothPayment.getTimestamp("estimatedDeliveryDate"))) == 0) && splInvoiceTypes.contains(invoiceTypeId))) {
							tempPayment.put("grandTotal", outstandingAmount.add((BigDecimal) tempPayment.get("grandTotal")));
							tempPayment.put("totalDue", outstandingAmount.add((BigDecimal) tempPayment.get("totalDue")));
						} else {
							tempPayment.put("totalDue", outstandingAmount.add((BigDecimal) tempPayment.get("totalDue")));
						}

					}
				}

				if ((i == boothOrdersList.size() - 1)) {
					boothPaymentsList.add(tempPayment);
				}
			}

		}
		// here rounding the booth amounts
		List tempPaymentsList = FastList.newInstance();
		// tempPaymentsList.addAll(boothPaymentsList);
		for (int i = 0; i < boothPaymentsList.size(); i++) {
			Map entry = FastMap.newInstance();
			entry.putAll((Map) boothPaymentsList.get(i));
			BigDecimal roundingAmount = ((BigDecimal) entry.get("grandTotal")).setScale(0, rounding);
			BigDecimal roundingTotalDueAmount = ((BigDecimal) entry.get("totalDue")).setScale(0, rounding);
			entry.put("grandTotal", roundingAmount);
			entry.put("totalDue", roundingTotalDueAmount);
			invoicesTotalAmount = invoicesTotalAmount.add(roundingAmount);
			invoicesTotalDueAmount = invoicesTotalDueAmount.add(roundingTotalDueAmount);
			tempPaymentsList.add(entry);
		}
		boothsPaymentsDetail.put("invoicesTotalAmount", invoicesTotalAmount);
		boothsPaymentsDetail.put("invoicesTotalDueAmount",invoicesTotalDueAmount);
		boothsPaymentsDetail.put("boothPaymentsUnRoundedList",boothPaymentsList);
		boothsPaymentsDetail.put("boothPaymentsList", tempPaymentsList);
		return boothsPaymentsDetail;
	}

	/**
	 * Get all payment pending booths
	 * 
	 * @param ctx
	 *            the dispatch context
	 * @param context
	 * @return a List Stop Ship booths List
	 */
	public static Map<String, Object> getStopShipList(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		String facilityId = (String) context.get("facilityId");
		List boothList = FastList.newInstance();
		List excludeStopShipBooths = FastList.newInstance();
		Map<String, Object> boothPayments = FastMap.newInstance();
		if (supplyDate == null) {
			supplyDate = UtilDateTime.getNextDayStart(UtilDateTime.nowTimestamp());
		}
		if (facilityId != null) {
			try {
				GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
				if (facilityDetail == null) {
					Debug.logError("Booth " + facilityId + " does not exist! ",	module);
					return ServiceUtil.returnError("Booth " + facilityId+ " does not exist! ");
				}

			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		boothPayments = getBoothPayments(delegator, ctx.getDispatcher(),userLogin, UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"),null, facilityId, null, Boolean.FALSE);
		List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
		Map absenteeOverrideMap = getAbsenteeOverrideBooths(ctx,UtilMisc.toMap("overrideSupplyDate", supplyDate));
		List absenteeOverrideList = (List) absenteeOverrideMap.get("boothList");
		try {
			excludeStopShipBooths = EntityUtil.getFieldListFromEntityList(delegator.findList("Facility", EntityCondition.makeCondition("excludeStopShipCheck",EntityOperator.EQUALS, "Y"), null, null,null, true), "facilityId", true);
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		excludeStopShipBooths.addAll(absenteeOverrideList);
		Set excludeStopBoothSet = new HashSet(excludeStopShipBooths);
		List tempPaymentsList = FastList.newInstance();
		for (int i = 0; i < boothPaymentsList.size(); i++) {
			Map<String, Object> boothMap = FastMap.newInstance();
			String tempBoothId = (String) ((Map) boothPaymentsList.get(i)).get("facilityId");
			if (!excludeStopBoothSet.contains(tempBoothId)) {
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
	 * 
	 * @param ctx
	 *            the dispatch context
	 * @param context
	 * @return a List of payment pending booths
	 */
	public static Map<String, Object> getDaywiseBoothDues(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		List boothDuesList = FastList.newInstance();
		String ownerPartyId = "";
		List exprListForParameters = FastList.newInstance();
		Map<String, Object> boothDuesDetail = FastMap.newInstance();
		List boothOrdersList = FastList.newInstance();
		BigDecimal totalAmount = BigDecimal.ZERO;

		boolean isByParty = Boolean.FALSE;
		boolean enableCRInst = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = Boolean.TRUE;
		}
		if (UtilValidate.isNotEmpty(context.get("enableCRInst"))) {
			enableCRInst = Boolean.TRUE;
		}
		boolean enableSoCrPmntTrack = Boolean.FALSE;
		try {
			GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName",	"enableSoCrPmntTrack"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack)&& (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
				enableSoCrPmntTrack = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		if (enableCRInst) {
			exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,	UtilMisc.toList("CASH", "EMP_SUBSIDY", "CREDIT")));
		} else {
			if (enableSoCrPmntTrack) {
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,	UtilMisc.toList("CASH", "SPECIAL_ORDER", "CREDIT")));
			} else {
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,UtilMisc.toList("CASH", "EMP_SUBSIDY")));
			}
		}

		if (UtilValidate.isEmpty(facilityId)) {
			Debug.logError("Facility Id cannot be empty", module);
			return ServiceUtil.returnError("Facility Id cannot be empty");
		}

		try {
			GenericValue facilityDetail = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), true);
			if (facilityDetail == null || !facilityDetail.getString("facilityTypeId").equals("BOOTH")) {
				Debug.logInfo("facilityId '" + facilityId + "'is not a Booth ",	"");
				return ServiceUtil.returnError("facilityId '" + facilityId	+ "'is not a Booth");
			}
			ownerPartyId = facilityDetail.getString("ownerPartyId");
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		if (isByParty) {
			exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, ownerPartyId));
		} else {
			exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		}

		exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF")));

		// filter out booths owned by the APDDCF
		exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL, "Company"));
		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try {
			boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null,UtilMisc.toList("estimatedDeliveryDate"), findOptions,false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		String duesByParty = "N";
		if (isByParty) {
			duesByParty = "Y";
		}

		List<GenericValue> obInvoiceList = (List) getOpeningBalanceInvoices(ctx,UtilMisc.toMap("facilityId", facilityId, "duesByParty",	duesByParty)).get("invoiceList");
		boothOrdersList.addAll(obInvoiceList);
		boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("parentFacilityId", "originFacilityId","estimatedDeliveryDate"));
		if (!UtilValidate.isEmpty(boothOrdersList)) {
			Timestamp firstDate = ((GenericValue) boothOrdersList.get(0)).getTimestamp("estimatedDeliveryDate");
			firstDate = UtilDateTime.getDayStart(firstDate);
			Timestamp lastDate = ((GenericValue) boothOrdersList.get(boothOrdersList.size() - 1)).getTimestamp("estimatedDeliveryDate");
			lastDate = UtilDateTime.getDayEnd(lastDate);
			Timestamp iterDate = firstDate;
			while (iterDate.compareTo(lastDate) < 0) {
				Map<String, Object> boothPayments = FastMap.newInstance();
				boothPayments = getBoothPayments(delegator,	ctx.getDispatcher(), userLogin,	UtilDateTime.toDateString(iterDate,	"yyyy-MM-dd HH:mm:ss"), null, facilityId, null,	Boolean.TRUE, isByParty, enableCRInst);
				List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
				if (!UtilValidate.isEmpty(boothPaymentsList)) {
					Map tempDetail = (Map) boothPaymentsList.get(0);
					Map boothDue = FastMap.newInstance();
					boothDue.put("supplyDate", iterDate);
					boothDue.put("amount", tempDetail.get("grandTotal"));
					boothDuesList.add(boothDue);
					totalAmount = totalAmount.add((BigDecimal) tempDetail.get("grandTotal"));
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
	 * 
	 * @param ctx
	 *            the dispatch context
	 * @param context
	 * @return a List of Override booths
	 * @throws ConversionException
	 */
	public static Map<String, Object> getAbsenteeOverrideBooths(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List exprListForParameters = FastList.newInstance();
		List boothList = FastList.newInstance();
		List<GenericValue> overrideList = FastList.newInstance();;
		Timestamp supplyDate = (Timestamp) context.get("overrideSupplyDate");
		if (supplyDate == null) {
			supplyDate = UtilDateTime.nowTimestamp();
		}
		try {
			exprListForParameters.add(EntityCondition.makeCondition("supplyDate", EntityOperator.EQUALS,((new DateTimeConverters.TimestampToSqlDate()).convert(supplyDate))));
		} catch (ConversionException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

		try {
			overrideList = delegator.findList("AbsenteeOverrideAndFacility",paramCond, null, UtilMisc.toList("boothId"), null, false);
			if (!UtilValidate.isEmpty(overrideList)) {
				boothList = EntityUtil.getFieldListFromEntityList(overrideList,"boothId", true);
			}
		} catch (GenericEntityException e) {
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
	 * 
	 * @param ctx
	 *            the dispatch context
	 * @param context
	 * @return a List of booths that have payments due for the given route
	 */
	public static Map<String, Object> getBoothDues(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String boothId = (String) context.get("boothId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// Debug.logInfo("userLogin= " + userLogin, module);

		if (UtilValidate.isEmpty(boothId)) {
			Debug.logError("Booth Id cannot be empty", module);
			return ServiceUtil.returnError("Booth Id cannot be empty");
		}

		Map<String, Object> result = getBoothDetails(ctx, context);
		if (ServiceUtil.isError(result)) {
			Debug.logError("Error fetching details for Booth Id " + boothId,module);
			return ServiceUtil.returnError("Error fetching details for Booth Id "+ boothId);
		}
		Map<String, Object> boothDues = (Map) result.get("boothDetails");
		Map<String, Object> boothTotalDues = FastMap.newInstance();
		boothTotalDues.putAll((Map) result.get("boothDetails"));
		BigDecimal unRoundedAmount = ZERO;
		BigDecimal unRoundedtotalDueAmount = ZERO;
		BigDecimal roundedAmount = ZERO;
		BigDecimal roundedtotalDueAmount = ZERO;
		Map<String, Object> boothPayments = FastMap.newInstance();
		boothPayments = getBoothPayments(delegator, ctx.getDispatcher(),
				userLogin, UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"),null, boothId, null, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
		List boothPaymentsList = (List) boothPayments.get("boothPaymentsList");
		List boothPaymentsUnRoundedList = (List) boothPayments.get("boothPaymentsUnRoundedList");
		if (boothPaymentsList.size() != 0) {
			Map boothPayment = (Map) boothPaymentsList.get(0);
			roundedAmount = (BigDecimal) boothPayment.get("grandTotal");
			roundedtotalDueAmount = (BigDecimal) boothPayment.get("totalDue");
		}
		if (boothPaymentsUnRoundedList.size() != 0) {
			Map boothPayment = (Map) boothPaymentsUnRoundedList.get(0);
			unRoundedAmount = (BigDecimal) boothPayment.get("grandTotal");
			unRoundedtotalDueAmount = (BigDecimal) boothPayment.get("totalDue");
		}
		boothDues.put("amount", roundedAmount.doubleValue());
		boothTotalDues.put("amount", unRoundedAmount.doubleValue());
		boothTotalDues.put("totalDueAmount",unRoundedtotalDueAmount.doubleValue());
		result = ServiceUtil.returnSuccess();
		result.put("boothDues", boothDues);
		result.put("boothTotalDues", boothTotalDues);
		Debug.logInfo("result= " + result, module);
		return result;
	}

	/**
	 * Get booth dues running Total
	 * 
	 * @param ctx
	 *            the dispatch context
	 * @param context
	 * @return a List of booths that have payments due for the given route
	 */
	public static Map<String, Object> getBoothDuesRunningTotal(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List boothIds = (List) context.get("boothIds");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		BigDecimal boothRunningTotal = BigDecimal.ZERO;
		Map<String, Object> result = ServiceUtil.returnSuccess();

		if (UtilValidate.isEmpty(boothIds)) {
			Debug.logError("Booth Id's cannot be empty", module);
			return ServiceUtil.returnError("Booth Id cannot be empty");
		}

		result = ServiceUtil.returnSuccess();
		if (!UtilValidate.isEmpty(boothIds)) {
			for (int i = 0; i < boothIds.size(); i++) {
				Map boothResult = getBoothDues(ctx,UtilMisc.<String, Object> toMap("boothId",boothIds.get(i), "userLogin", userLogin));
				if (!ServiceUtil.isError(boothResult)) {
					Map boothDues = (Map) boothResult.get("boothDues");
					if ((Double) boothDues.get("amount") != 0) {
						boothRunningTotal = boothRunningTotal.add(new BigDecimal((Double) boothDues.get("amount")));
					}
				}
			}
			result.put("boothRunningTotal", boothRunningTotal);
		}
		return result;
	}

	/**
	 * Get route dues
	 * 
	 * @param ctx
	 *            the dispatch context
	 * @param context
	 * @return a List of booths that have payments due for the given route
	 */
	public static Map<String, Object> getRouteDues(DispatchContext ctx,	Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String routeId = (String) context.get("routeId");
		if (UtilValidate.isEmpty(routeId)) {
			Debug.logError("Route Id cannot be empty", module);
			return ServiceUtil.returnError("Route Id cannot be empty");
		}
		List<Map<String, Object>> booths = FastList.newInstance();
		try {
			List<GenericValue> facilities = delegator.findList("Facility",EntityCondition.makeCondition("parentFacilityId",	EntityOperator.EQUALS, routeId), null, UtilMisc.toList("facilityId"), null, false);

			for (GenericValue facility : facilities) {
				Map boothResult = getBoothDues(ctx,	UtilMisc.<String, Object> toMap("boothId",facility.getString("facilityId"), "userLogin",userLogin));
				if (!ServiceUtil.isError(boothResult)) {
					Map boothDues = (Map) boothResult.get("boothDues");
					if ((Double) boothDues.get("amount") != 0) {
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
	 * Get the sales order totals for the given period. The totals are also
	 * segmented into products and zones for reporting purposes
	* Get the sales order totals for the given period.  The totals are also segmented into products and zones for
	     * reporting purposes
	     * @param ctx the dispatch context
	     * @param context context map
	 * @return totals map
	 * 
	* ::TODO:: consolidate DayTotals, PeriodTotals and DaywiseTotals functions
	 */
	public static Map<String, Object> getPeriodTotals(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<String> facilityIds = (List<String>) context.get("facilityIds");
		List<String> shipmentIds = (List<String>) context.get("shipmentIds");
		boolean isByParty = Boolean.FALSE;
		if(UtilValidate.isNotEmpty(context.get("isByParty"))){
			isByParty = (Boolean)context.get("isByParty");
		}
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
		List ownerPartyIds = FastList.newInstance();
		if(UtilValidate.isNotEmpty(facilityIds) && isByParty){
			try{
				List<GenericValue> ownerPartyList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), UtilMisc.toSet("ownerPartyId"), null, null ,false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(ownerPartyList, "ownerPartyId", true);
			}catch(GenericEntityException e){
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		boolean includeReturnOrders = Boolean.FALSE;// always excluding if externally not set
		if (context.get("includeReturnOrders") != null) {
			includeReturnOrders = (Boolean) context.get("includeReturnOrders");
		}
		List<GenericValue> orderItems = FastList.newInstance();
		Map productAttributes = new TreeMap<String, Object>();
		List productSubscriptionTypeList = FastList.newInstance();
		Map<String, String> dayShipmentMap = FastMap.newInstance();
		List adjustmentOrderList = FastList.newInstance();
		try {
			List exprListForParameters = FastList.newInstance();
			exprListForParameters.add(EntityCondition.makeCondition("attrName",	EntityOperator.EQUALS, "FAT"));
			exprListForParameters.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS, "SNF"));
			EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.OR);
			List<GenericValue> productAttribtutesList = delegator.findList("ProductAttribute", paramCond, null, null, null, false);
			Iterator<GenericValue> productAttrIter = productAttribtutesList.iterator();
			while (productAttrIter.hasNext()) {
				GenericValue productAttrItem = productAttrIter.next();
				if (!productAttributes.containsKey(productAttrItem.getString("productId"))) {
					productAttributes.put(productAttrItem.getString("productId"),new TreeMap<String, Object>());
				}
				Map value = (Map) productAttributes.get(productAttrItem.getString("productId"));
				value.put(productAttrItem.getString("attrName"),productAttrItem.getString("attrValue"));
			}

			productSubscriptionTypeList = delegator.findList("Enumeration",	EntityCondition.makeCondition("enumTypeId",	EntityOperator.EQUALS, "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"),null, false);

			// Debug.logInfo("productAttributes=" + productAttributes, module);
			if (UtilValidate.isEmpty(shipmentIds)) {
				shipmentIds = getByProdShipmentIds(delegator, fromDate,	thruDate);// this will give Today AM+PM as SALE 
				//shipmentIds = getShipmentIds(delegator, fromDate, thruDate);
			}

			// lets populate sales date shipmentId Map
			int intervalDays = (UtilDateTime.getIntervalInDays(fromDate,thruDate)) + 1;
			for (int i = 0; i < intervalDays; i++) {
				Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate,i);
				// List dayShipments = getShipmentIds(delegator, saleDate, saleDate);
				List dayShipments = getByProdShipmentIds(delegator, saleDate,saleDate);// this will give Today AM+PM as SALE
				for (int j = 0; j < dayShipments.size(); j++) {
					dayShipmentMap.put((String) dayShipments.get(j),UtilDateTime.toDateString(saleDate, "yyyy-MM-dd"));
				}
			}
			// Debug.logInfo("salesDate=" + salesDate + "shipmentIds=" +shipmentIds, module);
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipmentIds));
			conditionList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
			if (!UtilValidate.isEmpty(onlyVendorAndPTCBooths)) {
				if (onlyVendorAndPTCBooths.booleanValue()) {
					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("categoryTypeEnum", "VENDOR"),EntityCondition.makeCondition("categoryTypeEnum","PTC")));
				}
			}
			if(isByParty){
				if (UtilValidate.isNotEmpty(ownerPartyIds)) {
					conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, ownerPartyIds));
				}
			}else{
				if (UtilValidate.isNotEmpty(facilityIds)) {
					conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
				}
			}
			
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			// Debug.logInfo("condition=" + condition, module);
			if (!UtilValidate.isEmpty(shipmentIds)) {
				orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition,	null, null, null, false);
			}
			// Returns Logic Starts here
			if (includeReturnOrders) {
				List returnConditionList = FastList.newInstance();
				returnConditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
				if(UtilValidate.isNotEmpty(ownerPartyIds)){
					returnConditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,ownerPartyIds));
				}
				else{
					if (UtilValidate.isNotEmpty(facilityIds)) {
						returnConditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN,facilityIds));
					}
				}
				
				returnConditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS,"RETURN_ACCEPTED"));
				EntityCondition returnCondition = EntityCondition.makeCondition(returnConditionList, EntityOperator.AND);
				List<GenericValue> returnItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility",returnCondition, null, null, null, false);
				List<GenericValue> newReturnItemList = FastList.newInstance();
				for (GenericValue returnItem : returnItemsList) {
					String boothId = "";
					if(isByParty){
						boothId = returnItem.getString("ownerPartyId");
					}else{
						boothId = returnItem.getString("originFacilityId");
					}
					
					String productSubscriptionTypeId = "CASH";
					String shipId = returnItem.getString("shipmentId");
					String productId = returnItem.getString("productId");
					GenericValue facility = null;
					if(isByParty){
						List<GenericValue> facilityList = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, boothId), null, null, null, false);
						if(UtilValidate.isNotEmpty(facilityList)){
							facility = EntityUtil.getFirst(facilityList);
						}
					}else{
						facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), false);
					}
					
					if (UtilValidate.isNotEmpty(facility)) {
						// lets override productSubscriptionTypeId based on facility category
						if (facility.getString("categoryTypeEnum").equals("SO_INST")) {
							productSubscriptionTypeId = "SPECIAL_ORDER";
						} else if (facility.getString("categoryTypeEnum").equals("CR_INST")) {
							productSubscriptionTypeId = "CREDIT";
						}
					}
					// to Get related Order for return
					GenericValue returnHeaderOrderItem = EntityUtil.getFirst(EntityUtil.filterByAnd(orderItems,UtilMisc.toMap("productId", productId,"originFacilityId", boothId,"shipmentId", shipId,"productSubscriptionTypeId",productSubscriptionTypeId)));
					// making Same record with minus Quantity
					if (UtilValidate.isNotEmpty(returnHeaderOrderItem)) {
						GenericValue newOrderReturnItem = delegator.makeValue("OrderHeaderItemProductShipmentAndFacility");
						newOrderReturnItem.putAll(returnHeaderOrderItem);
						newOrderReturnItem.set("quantity", (returnItem.getBigDecimal("returnQuantity").negate()));
						newReturnItemList.add(newOrderReturnItem);
					}
					if (UtilValidate.isEmpty(returnHeaderOrderItem)) {
						Debug.logImportant("==InConsitentREcord=####==boothId==" + boothId+ "==shipId==" + shipId+ "==productSubscriptionTypeId="+ productSubscriptionTypeId+ "==productId=" + productId, "");
					}
				}
				orderItems.addAll(newReturnItemList);
			}// end of returns check

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		BigDecimal totalQuantity = ZERO;
		BigDecimal totalRevenue = ZERO;
		BigDecimal totalPacket = ZERO;
		BigDecimal totalFat = ZERO;
		BigDecimal totalSnf = ZERO;
		BigDecimal totalVatRevenue = ZERO;

		Map<String, Object> boothZoneMap = FastMap.newInstance();
		boothZoneMap = getAllBoothsZonesMap(delegator);
		// Debug.logInfo("boothZoneMap=" + boothZoneMap, module);
		Map<String, Object> boothTotals = new TreeMap<String, Object>();
		// Map<String, Object> zoneTotals = new TreeMap<String, Object>();
		//Map<String, Object> distributorTotals = new TreeMap<String, Object>();
		Map<String, Object> productTotals = new TreeMap<String, Object>();
		Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
		Map<String, Object> dayWiseTotals = new TreeMap<String, Object>();
		Iterator<GenericValue> itemIter = orderItems.iterator();
		while (itemIter.hasNext()) {
			GenericValue orderItem = itemIter.next();
			String prodSubscriptionTypeId = orderItem.getString("productSubscriptionTypeId");
			BigDecimal quantity = orderItem.getBigDecimal("quantity");
			BigDecimal packetQuantity = orderItem.getBigDecimal("quantity");
			BigDecimal price = orderItem.getBigDecimal("unitListPrice");
			BigDecimal revenue = price.multiply(quantity);
			if (!(adjustmentOrderList.contains(orderItem.getString("orderId")))	&& (prodSubscriptionTypeId.equals("EMP_SUBSIDY"))) {
				try {
					List<GenericValue> adjustemntsList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderItem.getString("orderId")), null,null, null, false);
					for (GenericValue adjustemnt : adjustemntsList) {
						revenue = revenue.add(adjustemnt.getBigDecimal("amount"));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				adjustmentOrderList.add(orderItem.getString("orderId"));
			}
			totalRevenue = totalRevenue.add(revenue);
			totalPacket = totalPacket.add(packetQuantity);
			BigDecimal vatAmount = ZERO;
			if (UtilValidate.isNotEmpty(orderItem.getBigDecimal("vatAmount"))) {
				vatAmount = orderItem.getBigDecimal("vatAmount");
			}
			BigDecimal vatRevenue = vatAmount.multiply(quantity);
			totalVatRevenue = totalVatRevenue.add(vatRevenue);
			quantity = quantity.multiply(orderItem.getBigDecimal("quantityIncluded"));
			totalQuantity = totalQuantity.add(quantity);
			BigDecimal fat = ZERO;
			BigDecimal snf = ZERO;
			String productName = orderItem.getString("productName");
			String productId = orderItem.getString("productId");
			Map prodAttrMap = (Map) productAttributes.get(orderItem.getString("productId"));
			// Debug.logInfo("orderItem=" + orderItem, module);
			if (prodAttrMap != null) {
				double fatPercent = Double.parseDouble((String) prodAttrMap.get("FAT"));
				fat = quantity.multiply(BigDecimal.valueOf(fatPercent));
				fat = fat.multiply(BigDecimal.valueOf(1.03));
				fat = fat.divide(BigDecimal.valueOf(100));
				double snfPercent = Double.parseDouble((String) prodAttrMap.get("SNF"));
				snf = quantity.multiply(BigDecimal.valueOf(snfPercent));
				snf = snf.multiply(BigDecimal.valueOf(1.03));
				snf = snf.divide(BigDecimal.valueOf(100));
			}
			totalFat = totalFat.add(fat);
			totalSnf = totalSnf.add(snf);
			
			Map zone = (Map) boothZoneMap.get(orderItem.getString("originFacilityId"));
			// Handle booth totals
			
			String boothId = "";
			if(isByParty){
				boothId = orderItem.getString("ownerPartyId");
			}
			else{
				boothId = orderItem.getString("originFacilityId");
			}
			
			if (boothTotals.get(boothId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();

				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("excludeIncentive",orderItem.getString("excludeIncentive"));
				newMap.put("categoryTypeEnum",orderItem.getString("categoryTypeEnum"));
				Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
				Map<String, Object> iteratorMap = FastMap.newInstance();
				while (typeIter.hasNext()) {
					// initialize type maps
					GenericValue type = typeIter.next();
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name", type.getString("enumId"));
					supplyTypeDetailsMap.put("total", ZERO);
					supplyTypeDetailsMap.put("packetQuantity", ZERO);
					supplyTypeDetailsMap.put("totalRevenue", ZERO);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					iteratorMap.put(type.getString("enumId"),supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", iteratorMap);
				}
				Map supplyTypeMap = (Map) newMap.get("supplyTypeTotals");
				Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(prodSubscriptionTypeId);
				supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
				supplyTypeDetailsMap.put("total", quantity);
				supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
				supplyTypeDetailsMap.put("totalRevenue", revenue);
				supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
				supplyTypeMap.put(prodSubscriptionTypeId, supplyTypeDetailsMap);
				newMap.put("supplyTypeTotals", supplyTypeMap);

				Map<String, Object> productItemMap = FastMap.newInstance();
				Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
				Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();

				productItemMap.put("name", productName);
				productSupplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
				productSupplyTypeDetailsMap.put("total", quantity);
				productSupplyTypeDetailsMap.put("packetQuantity",packetQuantity);
				productSupplyTypeDetailsMap.put("totalRevenue", revenue);
				productSupplyTypeDetailsMap.put("vatRevenue", vatRevenue);
				productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),productSupplyTypeDetailsMap);
				productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
				productItemMap.put("total", quantity);
				productItemMap.put("packetQuantity", packetQuantity);
				productItemMap.put("totalRevenue", revenue);
				productItemMap.put("vatRevenue", vatRevenue);

				Map<String, Object> productMap = FastMap.newInstance();
				productMap.put(productId, productItemMap);
				newMap.put("productTotals", productMap);
				boothTotals.put(boothId, newMap);
			} 
			else {
				Map boothMap = (Map) boothTotals.get(boothId);
				BigDecimal runningTotal = (BigDecimal) boothMap.get("total");
				runningTotal = runningTotal.add(quantity);
				boothMap.put("total", runningTotal);

				BigDecimal runningPacketTotal = (BigDecimal) boothMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				boothMap.put("packetQuantity", runningPacketTotal);

				BigDecimal runningTotalRevenue = (BigDecimal) boothMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				boothMap.put("totalRevenue", runningTotalRevenue);

				BigDecimal runningVatRevenue = (BigDecimal) boothMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				boothMap.put("vatRevenue", runningVatRevenue);

				// next handle type totals
				Map tempMap = (Map) boothMap.get("supplyTypeTotals");
				Map typeMap = (Map) tempMap.get(prodSubscriptionTypeId);
				BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
				typeRunningTotal = typeRunningTotal.add(quantity);
				BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
				typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);

				BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
				typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);

				BigDecimal typeRunningVatRevenue = (BigDecimal) typeMap.get("vatRevenue");
				typeRunningVatRevenue = typeRunningVatRevenue.add(vatRevenue);

				typeMap.put("name", prodSubscriptionTypeId);
				typeMap.put("total", typeRunningTotal);
				typeMap.put("packetQuantity", typeRunningPacketTotal);
				typeMap.put("totalRevenue", typeRunningTotalRevenue);
				typeMap.put("vatRevenue", typeRunningVatRevenue);
				// next handle product totals
				Map boothProductTotals = (Map) boothMap.get("productTotals");
				Map productMap = (Map) boothProductTotals.get(productId);
				if (UtilValidate.isEmpty(productMap)) {
					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> supplyTypeMap = FastMap.newInstance();
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
					productItemMap.put("name", productName);
					productItemMap.put("supplyTypeTotals", supplyTypeMap);
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);
					boothProductTotals.put(productId, productItemMap);

				} else {
					BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
					productRunningTotal = productRunningTotal.add(quantity);
					productMap.put("total", productRunningTotal);
					BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
					productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
					productMap.put("totalRevenue", productRunningTotalRevenue);

					BigDecimal productRunningPacketTotals = (BigDecimal) productMap.get("packetQuantity");
					productRunningPacketTotals = productRunningPacketTotals.add(packetQuantity);
					productMap.put("packetQuantity", productRunningPacketTotals);

					BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
					productRunningVatRevenue = productRunningVatRevenue.add(productRunningVatRevenue);
					productMap.put("vatRevenue", productRunningVatRevenue);

					Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
					if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
						Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
						BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
						runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
						BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
						runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);
						BigDecimal runningPacketTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
						runningPacketTotalproductSubscriptionType = runningPacketTotalproductSubscriptionType.add(packetQuantity);
						BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
						runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);

						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
						supplyTypeDetailsMap.put("packetQuantity",runningPacketTotalproductSubscriptionType);
						supplyTypeDetailsMap.put("totalRevenue",runningTotalRevenueproductSubscriptionType);
						supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
						productMap.put("supplyTypeTotals", supplyTypeMap);
						boothProductTotals.put(productId, productMap);

					} else {
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity",packetQuantity);			
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
						productMap.put("supplyTypeTotals", supplyTypeMap);
						boothProductTotals.put(productId, productMap);
					}
				}
			}
			// handle dayWise Totals if empty ignore this type of totals
			if (UtilValidate.isNotEmpty(dayShipmentMap)) {
				String currentSaleDate = dayShipmentMap.get(orderItem.getString("shipmentId"));
				if (UtilValidate.isEmpty(currentSaleDate)) {
					try {
						GenericValue shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId",orderItem.getString("shipmentId")),false);
						currentSaleDate = UtilDateTime.toDateString(shipment.getTimestamp("estimatedShipDate"),"yyyy-MM-dd");
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
					}
				}
				if (dayWiseTotals.get(currentSaleDate) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue);
					newMap.put("vatRevenue", vatRevenue);
					newMap.put("excludeIncentive",orderItem.getString("excludeIncentive"));
					newMap.put("categoryTypeEnum",orderItem.getString("categoryTypeEnum"));
					Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
					Map<String, Object> iteratorMap = FastMap.newInstance();
					while (typeIter.hasNext()) {
						// initialize type maps
						GenericValue type = typeIter.next();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name",type.getString("enumId"));
						supplyTypeDetailsMap.put("total", ZERO);
						supplyTypeDetailsMap.put("packetQuantity", ZERO);
						supplyTypeDetailsMap.put("totalRevenue", ZERO);
						supplyTypeDetailsMap.put("vatRevenue", ZERO);
						iteratorMap.put(type.getString("enumId"),supplyTypeDetailsMap);
						newMap.put("supplyTypeTotals", iteratorMap);
					}
					Map supplyTypeMap = (Map) newMap.get("supplyTypeTotals");
					Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(prodSubscriptionTypeId,supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);

					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();
					productItemMap.put("name", productName);
					productSupplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					productSupplyTypeDetailsMap.put("total", quantity);
					productSupplyTypeDetailsMap.put("packetQuantity",packetQuantity);
					productSupplyTypeDetailsMap.put("totalRevenue", revenue);
					productSupplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),productSupplyTypeDetailsMap);
					productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);

					Map<String, Object> productMap = FastMap.newInstance();
					productMap.put(productId, productItemMap);
					newMap.put("productTotals", productMap);
					dayWiseTotals.put(currentSaleDate, newMap);
				} 
				else {
					Map dayWiseMap = (Map) dayWiseTotals.get(currentSaleDate);
					BigDecimal runningTotal = (BigDecimal) dayWiseMap.get("total");
					runningTotal = runningTotal.add(quantity);
					dayWiseMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal) dayWiseMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					dayWiseMap.put("totalRevenue", runningTotalRevenue);
					BigDecimal runningPacketTotal = (BigDecimal) dayWiseMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					dayWiseMap.put("packetQuantity", runningPacketTotal);
					BigDecimal runningVatRevenue = (BigDecimal) dayWiseMap.get("vatRevenue");
					runningVatRevenue = runningVatRevenue.add(vatRevenue);
					dayWiseMap.put("vatRevenue", runningVatRevenue);
					// next handle type totals
					Map tempMap = (Map) dayWiseMap.get("supplyTypeTotals");
					Map typeMap = (Map) tempMap.get(prodSubscriptionTypeId);
					BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
					typeRunningTotal = typeRunningTotal.add(quantity);
					BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);
					BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
					typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);
					BigDecimal typeRunningVatRevenue = (BigDecimal) typeMap.get("vatRevenue");
					typeRunningVatRevenue = typeRunningVatRevenue.add(vatRevenue);

					typeMap.put("name", prodSubscriptionTypeId);
					typeMap.put("total", typeRunningTotal);
					typeMap.put("totalRevenue", typeRunningTotalRevenue);
					typeMap.put("packetQuantity", typeRunningPacketTotal);
					typeMap.put("vatRevenue", typeRunningVatRevenue);

					// next handle product totals
					Map dayWiseProductTotals = (Map) dayWiseMap.get("productTotals");
					Map productMap = (Map) dayWiseProductTotals.get(productId);

					if (UtilValidate.isEmpty(productMap)) {
						Map<String, Object> productItemMap = FastMap.newInstance();
						Map<String, Object> supplyTypeMap = FastMap.newInstance();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity",packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
						productItemMap.put("name", productName);
						productItemMap.put("supplyTypeTotals", supplyTypeMap);
						productItemMap.put("total", quantity);
						productItemMap.put("packetQuantity", packetQuantity);
						productItemMap.put("totalRevenue", revenue);
						productItemMap.put("vatRevenue", vatRevenue);
						dayWiseProductTotals.put(productId, productItemMap);

					} else {
						BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
						productRunningTotal = productRunningTotal.add(quantity);
						productMap.put("total", productRunningTotal);
						BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
						productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
						productMap.put("totalRevenue",productRunningTotalRevenue);
						BigDecimal productRunningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
						productRunningPacketTotal = productRunningPacketTotal.add(packetQuantity);
						productMap.put("packetQuantity",productRunningPacketTotal);
						BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
						productRunningVatRevenue = productRunningVatRevenue.add(vatRevenue);
						productMap.put("vatRevenue", productRunningVatRevenue);

						Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
						if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
							Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
							BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
							runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);

							BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
							runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);

							BigDecimal runningTotalPacketSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
							runningTotalPacketSubscriptionType = runningTotalPacketSubscriptionType.add(packetQuantity);
							BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
							runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);

							supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
							supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
							supplyTypeDetailsMap.put("packetQuantity",runningTotalPacketSubscriptionType);
							supplyTypeDetailsMap.put("totalRevenue",runningTotalRevenueproductSubscriptionType);
							supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);
							supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
							productMap.put("supplyTypeTotals", supplyTypeMap);
							dayWiseProductTotals.put(productId, productMap);

						} else {
							Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
							supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
							supplyTypeDetailsMap.put("total", quantity);
							supplyTypeDetailsMap.put("packetQuantity",packetQuantity);
							supplyTypeDetailsMap.put("totalRevenue", revenue);
							supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
							supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
							productMap.put("supplyTypeTotals", supplyTypeMap);
							dayWiseProductTotals.put(productId, productMap);
						}
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
				supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
				supplyTypeDetailsMap.put("total", quantity);
				supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
				supplyTypeDetailsMap.put("totalRevenue", revenue);
				supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
				newMap.put("supplyTypeTotals", supplyTypeMap);
				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("totalFat", fat);
				newMap.put("totalSnf", snf);
				productTotals.put(productId, newMap);
			} 
			else {
				Map productMap = (Map) productTotals.get(productId);
				BigDecimal runningTotal = (BigDecimal) productMap.get("total");
				runningTotal = runningTotal.add(quantity);
				productMap.put("total", runningTotal);
				BigDecimal runningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				productMap.put("totalRevenue", runningTotalRevenue);
				BigDecimal runningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				productMap.put("packetQuantity", runningPacketTotal);
				BigDecimal runningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				productMap.put("vatRevenue", runningVatRevenue);

				BigDecimal runningTotalFat = (BigDecimal) productMap.get("totalFat");
				runningTotalFat = runningTotalFat.add(fat);
				productMap.put("totalFat", runningTotalFat);
				BigDecimal runningTotalSnf = (BigDecimal) productMap.get("totalSnf");
				runningTotalSnf = runningTotalSnf.add(snf);
				productMap.put("totalSnf", runningTotalSnf);
				Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
				if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap = (Map<String, Object>) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
					BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
					BigDecimal runningPacketTotalSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
					BigDecimal runningRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
					BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
					runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
					runningRevenueproductSubscriptionType = runningRevenueproductSubscriptionType.add(revenue);
					runningPacketTotalSubscriptionType = runningPacketTotalSubscriptionType.add(packetQuantity);
					runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);
					supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
					supplyTypeDetailsMap.put("packetQuantity",runningPacketTotalSubscriptionType);
					supplyTypeDetailsMap.put("totalRevenue",runningRevenueproductSubscriptionType);
					supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);

					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
					productMap.put("supplyTypeTotals", supplyTypeMap);
				} else {
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
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
				newMap.put("vatRevenue", vatRevenue);
				supplyTypeTotals.put(prodSubscriptionTypeId, newMap);
			} 
			else {
				Map supplyTypeMap = (Map) supplyTypeTotals.get(prodSubscriptionTypeId);
				BigDecimal runningTotal = (BigDecimal) supplyTypeMap.get("total");
				runningTotal = runningTotal.add(quantity);
				supplyTypeMap.put("total", runningTotal);
				BigDecimal runningTotalRevenue = (BigDecimal) supplyTypeMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				supplyTypeMap.put("totalRevenue", runningTotalRevenue);
				BigDecimal runningPacketTotal = (BigDecimal) supplyTypeMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				supplyTypeMap.put("packetQuantity", runningPacketTotal);
				BigDecimal runningVatRevenue = (BigDecimal) supplyTypeMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				supplyTypeMap.put("vatRevenue", runningVatRevenue);
				supplyTypeTotals.put(prodSubscriptionTypeId, supplyTypeMap);
			}
			// Debug.log("===INENDDDDDDD==boothId=="+boothId+"===productId=="+productId+"====OrderId=="+orderItem.getString("orderId")+"==qty=="+orderItem.getString("quantity"));
		}
		totalQuantity = totalQuantity.setScale(decimals, rounding);
		totalRevenue = totalRevenue.setScale(decimals, rounding);
		totalPacket = totalPacket.setScale(decimals, rounding);
		totalFat = totalFat.setScale(decimals, rounding);
		totalSnf = totalSnf.setScale(decimals, rounding);
		totalVatRevenue = totalVatRevenue.setScale(decimals, rounding);
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
		for (Map.Entry<String, Object> entry : productTotals.entrySet()) {
			Map<String, Object> productValue = (Map<String, Object>) entry.getValue();
			BigDecimal tempVal = (BigDecimal) productValue.get("total");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("total", tempVal);

			tempVal = (BigDecimal) productValue.get("packetQuantity");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("packetQuantity", tempVal);

			tempVal = (BigDecimal) productValue.get("totalRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("totalRevenue", tempVal);
			tempVal = (BigDecimal) productValue.get("totalFat");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("totalFat", tempVal);
			tempVal = (BigDecimal) productValue.get("totalSnf");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("totalSnf", tempVal);
			tempVal = (BigDecimal) productValue.get("vatRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("vatRevenue", tempVal);
		}

		for (Map.Entry<String, Object> entry : supplyTypeTotals.entrySet()) {
			Map<String, Object> supplyTypeValue = (Map<String, Object>) entry.getValue();
			BigDecimal tempVal = (BigDecimal) supplyTypeValue.get("total");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("total", tempVal);

			tempVal = (BigDecimal) supplyTypeValue.get("packetQuantity");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("packetQuantity", tempVal);

			tempVal = (BigDecimal) supplyTypeValue.get("totalRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("totalRevenue", tempVal);
			tempVal = (BigDecimal) supplyTypeValue.get("vatRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("vatRevenue", tempVal);

		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("totalQuantity", totalQuantity);
		result.put("totalRevenue", totalRevenue);
		result.put("totalVatRevenue", totalVatRevenue);
		result.put("totalPacket", totalPacket);
		result.put("totalFat", totalFat);
		result.put("totalSnf", totalSnf);
		// result.put("zoneTotals", zoneTotals);
		result.put("boothTotals", boothTotals);
		result.put("dayWiseTotals", dayWiseTotals);
		// result.put("distributorTotals", distributorTotals);
		result.put("productTotals", productTotals);
		result.put("supplyTypeTotals", supplyTypeTotals);
		return result;
	}

	public static Map<String, Object> getFacilityGroupMemberList(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String facilityGroupId = (String) context.get("facilityGroupId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
		}
		Map<String, Object> result = FastMap.newInstance();
		List facilityIds = FastList.newInstance();
		List<GenericValue> groupFacilityList = FastList.newInstance();
		try {
			groupFacilityList = delegator.findList("FacilityGroupMemberAndFacility", EntityCondition.makeCondition("facilityGroupId",EntityOperator.EQUALS, facilityGroupId),null, null, null, false);
			groupFacilityList = EntityUtil.filterByDate(groupFacilityList,fromDate);
			if (UtilValidate.isEmpty(groupFacilityList)) {
				result.put("facilityIds", facilityIds);
				return result;
			}
			for (GenericValue facility : groupFacilityList) {
				if (facility.getString("facilityTypeId").equals("ZONE")) {
					facilityIds.addAll(getZoneBooths(delegator,facility.getString("facilityId")));
				}
				if (facility.getString("facilityTypeId").equals("ROUTE")) {
					facilityIds.addAll(getRouteBooths(delegator,facility.getString("facilityId")));
				}
				if (facility.getString("facilityTypeId").equals("BOOTH")) {
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

	public static Map<String, Object> createCreditNoteForReturns(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		Map result = ServiceUtil.returnSuccess();
		List shipments = getByProdShipmentIds(delegator, fromDate, thruDate);
		List conditionList = FastList.newInstance();
		try {
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipments));
			conditionList.add(EntityCondition.makeCondition("returnStatusId",EntityOperator.EQUALS, "RETURN_ACCEPTED"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

			List<GenericValue> returnHeaderAndItems = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", condition, null,	null, null, false);

			List<String> returnIds = EntityUtil.getFieldListFromEntityList(returnHeaderAndItems, "returnId", true);
			for (String returnId : returnIds) {
				List<GenericValue> returnItems = EntityUtil.filterByCondition(returnHeaderAndItems, EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId));
				GenericValue payItem = EntityUtil.getFirst(returnItems);
				boolean createNote = Boolean.TRUE;
				String boothId = payItem.getString("originFacilityId");
				Timestamp payDate = payItem.getTimestamp("estimatedShipDate");
				if (UtilValidate.isNotEmpty(payItem)&& UtilValidate.isNotEmpty(payItem.getString("paymentId"))) {
					String paidId = payItem.getString("paymentId");
					GenericValue payment = delegator.findOne("Payment",	UtilMisc.toMap("paymentId", paidId), false);
					String payType = payment.getString("paymentMethodTypeId");
					String status = payment.getString("statusId");
					BigDecimal amt = payment.getBigDecimal("amount");
					if (payType.equals("CREDITNOTE_PAYIN") && !status.equals("PMNT_VOID")&& (amt.compareTo(BigDecimal.ZERO) > 0)) {
						createNote = Boolean.FALSE;
					}
				}
				if (createNote) {
					BigDecimal creditAmount = BigDecimal.ZERO;
					for (GenericValue eachItem : returnItems) {
						BigDecimal unitPrice = eachItem.getBigDecimal("returnPrice");
						BigDecimal retQty = eachItem.getBigDecimal("returnQuantity");
						if (UtilValidate.isNotEmpty(unitPrice)) {
							BigDecimal tempAmount = unitPrice.multiply(retQty);
							creditAmount = creditAmount.add(tempAmount);
						}
					}
					if (creditAmount.compareTo(BigDecimal.ZERO) > 0) {
						Map paymentInputMap = FastMap.newInstance();
						paymentInputMap.put("userLogin", userLogin);
						paymentInputMap.put("facilityId", boothId);
						paymentInputMap.put("supplyDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(),"yyyy-MM-dd"));
						paymentInputMap.put("paymentDate", payDate);
						paymentInputMap.put("paymentMethodTypeId","CREDITNOTE_PAYIN");
						paymentInputMap.put("paymentPurposeType", "ROUTE_MKTG");
						paymentInputMap.put("amount", creditAmount.toString());
						paymentInputMap.put("useFifo", true);
						paymentInputMap.put("sendSMS", false);

						Map paymentResult = createPaymentForBooth(ctx,paymentInputMap);
						if (ServiceUtil.isError(paymentResult)) {
							Debug.logError(paymentResult.toString(), module);
							return ServiceUtil.returnError(null, null, null,paymentResult);
						}

						String paymentId = (String) paymentResult.get("paymentId");
						GenericValue returnHeaders = delegator.findOne("ReturnHeader",UtilMisc.toMap("returnId", returnId), false);
						returnHeaders.set("paymentId", paymentId);
						returnHeaders.store();
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}

		return result;
	}
	
	public static Map<String, Object> calculateStoreProductPrices(Delegator delegator, LocalDispatcher dispatcher, Map<String, ? extends Object> context) {
	    Map<String, Object> result = FastMap.newInstance();
	   
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String productId = (String) context.get("productId"); 
	    String partyId = (String) context.get("partyId");
	    String geoTax = (String) context.get("geoTax");
	    String productStoreId = (String) context.get("productStoreId");
	    String shipmentTypeId = (String) context.get("shipmentTypeId");
	    String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
	    Timestamp priceDate = (Timestamp) context.get("priceDate");
	    String productStoreGroupId = "_NA_";
	    String productPriceTypeId = (String) context.get("productPriceTypeId");
	    GenericValue product;
	    String currencyDefaultUomId = (String) context.get("currencyUomId");
	    BigDecimal discountAmount = BigDecimal.ZERO;
	    String productCategory = "";
	    List lmsProductIdsList = FastList.newInstance();
	    List byprodProductIdsList = FastList.newInstance();
	    if (UtilValidate.isEmpty(currencyDefaultUomId)) {
	        currencyDefaultUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "INR");
	    }
		GenericValue productStore;
		try {
			productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
		} catch (GenericEntityException e) {
			Debug.logError(e, e.toString(), module);
	        return ServiceUtil.returnError(e.toString());
		}
		try {
			product = delegator.findOne("Product", UtilMisc.toMap("productId", productId),false);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.toString(), module);
	        return ServiceUtil.returnError(e.toString());
		}
		
	    if (UtilValidate.isEmpty(priceDate)) {
	    	priceDate = UtilDateTime.nowTimestamp();
	    }
	    List conditionList = FastList.newInstance();
	    
	    if(UtilValidate.isEmpty(productPriceTypeId)){
	    	try{
	    		conditionList.clear();
	    		conditionList.add(EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "PRICE_TYPE"));
	    		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	    		EntityCondition expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		
	    		List<GenericValue> partyClassifications = delegator.findList("PartyClassificationAndGroupAndType", expr, null, null, null, false);
	    		partyClassifications = EntityUtil.filterByDate(partyClassifications, priceDate);
	    		if(UtilValidate.isNotEmpty(partyClassifications)){
	    			productPriceTypeId = (EntityUtil.getFirst(partyClassifications)).getString("partyClassificationGroupId");
	    		}
	    		else{
	    			productPriceTypeId = "DEFAULT_PRICE";
	    		}
	    	}
	    	catch (GenericEntityException e) {
				Debug.logError(e, e.toString(), module);
		        return ServiceUtil.returnError(e.toString());
			}
	    }
	    
	    try {
    		Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin,"partyId", partyId);
    		inputRateAmt.put("rateCurrencyUomId", currencyDefaultUomId);  
    		inputRateAmt.put("rateTypeId", "VENDOR_DEDUCTION");
    		inputRateAmt.put("periodTypeId", "RATE_HOUR");
    		inputRateAmt.put("partyId", partyId);
    		inputRateAmt.put("fromDate", priceDate);
    		inputRateAmt.put("productId", productId);
    		Map<String, Object> serviceResults = dispatcher.runSync("getPartyDiscountAmount", inputRateAmt);
    		
    		if (ServiceUtil.isError(serviceResults)) {
    			Debug.logError( "Unable to determine discount for [" + partyId +"]=========", module);
    			//return ServiceUtil.returnError("Unable to determine discount for " + facilityCategory, null, null, serviceResults);
    		}else if(UtilValidate.isNotEmpty(serviceResults.get("rateAmount"))){
				discountAmount = (BigDecimal)serviceResults.get("rateAmount");
				//rateAmountEntry = (GenericValue) serviceResults.get("rateAmountEntry");
    		}
	        		
        }catch (GenericServiceException e) {
			Debug.logError(e, "Unable to get margin/discount: " + e.getMessage(), module);
	        return ServiceUtil.returnError("Unable to get margin/discount: " + e.getMessage());
        }

	    boolean taxInPrice = false;
	    List<GenericValue> productPricesComponents = FastList.newInstance();
	    conditionList.clear();
    	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    	EntityCondition productPriceCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    	try{
    		productPricesComponents = delegator.findList("ProductPrice", productPriceCond, null, null, null, false);
    	}catch(GenericEntityException e){
    		Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
    	}
    	
    	productPricesComponents = EntityUtil.filterByDate(productPricesComponents, priceDate);
    	BigDecimal basicPrice = BigDecimal.ZERO;
    	List taxDetailList = FastList.newInstance();
		BigDecimal totalExciseDuty = BigDecimal.ZERO;
		BigDecimal totalTaxAmt = BigDecimal.ZERO;
		BigDecimal MRPPrice = BigDecimal.ZERO;
		
    	List<GenericValue> productComponentPrices = EntityUtil.filterByCondition(productPricesComponents, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, productPriceTypeId));
    	if(UtilValidate.isNotEmpty(productComponentPrices)){
    		String taxFlag = (EntityUtil.getFirst(productComponentPrices)).getString("taxInPrice");
    		basicPrice = (BigDecimal)(EntityUtil.getFirst(productComponentPrices)).getBigDecimal("price");
    		if(UtilValidate.isNotEmpty(taxFlag) && taxFlag.equalsIgnoreCase("Y")){
    			taxInPrice = true;
    		}
    	}
	    if(!taxInPrice){
	    	
	    	List<GenericValue> applicableTaxTypes = null;
			try {
				applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive ProductPriceType ", module);
				return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
			}

			List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
			
			List<GenericValue> prodPriceType = null;
			String MRPPriceType = "";

			if(UtilValidate.isNotEmpty(geoTax) && geoTax.equals("CST")){
					MRPPriceType = "MRP_OS";
					applicableTaxTypeList.remove("VAT_SALE");
			}
			else{
				MRPPriceType = "MRP_IS";
				applicableTaxTypeList.remove("CST_SALE");
			}
			//Calculate MRP price for excise duty amount
			
			List<GenericValue> MRPPriceList = EntityUtil.filterByCondition(productPricesComponents, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, MRPPriceType));
			
			List<GenericValue> prodPriceTypes = EntityUtil.filterByCondition(productPricesComponents, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.IN, applicableTaxTypeList));
			
			if(UtilValidate.isNotEmpty(MRPPriceList)){
				MRPPrice = (BigDecimal)(EntityUtil.getFirst(MRPPriceList)).get("price");
			}
			BigDecimal tempPrice = BigDecimal.ZERO;
			tempPrice = tempPrice.add(basicPrice);
			boolean exciseInvolvedFlag = false;
			List<GenericValue> productTaxTypes = FastList.newInstance();
			for(GenericValue priceType : prodPriceTypes){
				String taxType = priceType.getString("productPriceTypeId"); 
				if(taxType.equals("BED_SALE")){
					BigDecimal taxPercent = priceType.getBigDecimal("taxPercentage");
					if(UtilValidate.isNotEmpty(taxPercent) && taxPercent.compareTo(BigDecimal.ZERO)>0){
						BigDecimal excisableAmount = (MRPPrice.multiply(new BigDecimal(65))).divide(new BigDecimal(100), 2, rounding);
						BigDecimal amount = (excisableAmount.multiply(taxPercent)).divide(new BigDecimal(100), 2, rounding);
						priceType.set("price", amount);
						exciseInvolvedFlag = true;
						tempPrice = tempPrice.add(amount);
					}
					productTaxTypes.add(priceType);
				}
			}
			if(discountAmount.compareTo(BigDecimal.ZERO)>0){
				tempPrice = tempPrice.subtract(discountAmount);
			}
			for(GenericValue priceType : prodPriceTypes){
				String taxType = priceType.getString("productPriceTypeId"); 
				if(taxType.equals("VAT_SALE") || taxType.equals("CST_SALE")){
					BigDecimal taxPercent = priceType.getBigDecimal("taxPercentage");
					if(UtilValidate.isNotEmpty(taxPercent) && taxPercent.compareTo(BigDecimal.ZERO)>0 && exciseInvolvedFlag){
						BigDecimal amount = (tempPrice.multiply(taxPercent)).divide(new BigDecimal(100), 2, rounding);
						priceType.set("price", amount);
					}
				}
				if(!taxType.equals("BED_SALE")){
					productTaxTypes.add(priceType);
				}
			}
			// basicPrice = basicPrice.setScale( decimals,rounding);
			List<GenericValue> taxList = TaxAuthorityServices.getTaxAdjustmentByType(delegator, product, productStore, null, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, null, productTaxTypes);
			for (GenericValue taxItem : taxList) {
				String taxType = (String) taxItem.get("orderAdjustmentTypeId");
				BigDecimal amount = BigDecimal.ZERO;
				
				BigDecimal percentage = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(taxItem.get("amount"))){
					amount = (BigDecimal) taxItem.get("amount");
				}
				if(UtilValidate.isNotEmpty(taxItem.get("sourcePercentage"))){
					percentage = (BigDecimal) taxItem.get("sourcePercentage");
				}
				if(UtilValidate.isNotEmpty(taxItem.get("sourcePercentage")) && amount.compareTo(BigDecimal.ZERO)== 0){
					percentage = (BigDecimal) taxItem.get("sourcePercentage");
					if(UtilValidate.isNotEmpty(percentage) && UtilValidate.isNotEmpty(basicPrice)){
						amount = (basicPrice.multiply(percentage)).divide(new BigDecimal(100), 2, rounding);
					}
				}

				if(taxType.equals("BED_SALE")){
					totalExciseDuty = totalExciseDuty.add(amount);
				}
				else{
					totalTaxAmt = totalTaxAmt.add(amount);
				}
				
				Map taxDetailMap = FastMap.newInstance();
				
				taxDetailMap.put("taxType", taxType);
				taxDetailMap.put("amount", amount);
				taxDetailMap.put("percentage", percentage);
			
				Map tempDetailMap = FastMap.newInstance();
				tempDetailMap.putAll(taxDetailMap);
				
				taxDetailList.add(tempDetailMap);
			}
	    }
    
	    BigDecimal price = basicPrice.add(totalExciseDuty);
	    price = price.subtract(discountAmount);
	    BigDecimal totalPrice = price.add(totalTaxAmt);

	    result.put("basicPrice", basicPrice);
	    result.put("mrpPrice", MRPPrice);	    
	    result.put("price", price);
	    result.put("totalPrice", totalPrice);
	    result.put("taxList", taxDetailList);
	    return result;
    }
	
	public static Map<String, Object> getFacilityByCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String categoryTypeEnum = (String) context.get("categoryTypeEnum");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
		}
		List conditionList = FastList.newInstance();
		Map result = ServiceUtil.returnSuccess();
		try {
			if (UtilValidate.isNotEmpty(categoryTypeEnum)) {
				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,categoryTypeEnum));
			}
			conditionList.add(EntityCondition.makeCondition("openedDate",EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),	EntityOperator.OR, EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null)));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

			List<GenericValue> facilities = delegator.findList("Facility",condition, null, null, null, false);

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
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
		}
		Map<String, Object> result = FastMap.newInstance();
		List facilityIds = FastList.newInstance();
		try {
			facilityIds = (List) getFacilityGroupMemberList(ctx,UtilMisc.toMap("facilityGroupId", facilityGroupId,"fromDate", fromDate)).get("facilityIds");
			if (UtilValidate.isEmpty(facilityIds)) {
				result.put("facilityIds", facilityIds);
				result.put("routeList", FastList.newInstance());
				result.put("zoneList", FastList.newInstance());
				return result;
			}
			List<GenericValue> facilityList = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",	EntityOperator.IN, facilityIds), null, UtilMisc.toList("parentFacilityId"), null, false);
			List<GenericValue> routesfacilityList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId",EntityOperator.IN, EntityUtil.getFieldListFromEntityList(facilityList,"parentFacilityId", true)), null,UtilMisc.toList("parentFacilityId"), null, false);
			List<GenericValue> zonefacilityList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId",EntityOperator.IN, EntityUtil.getFieldListFromEntityList(routesfacilityList,"parentFacilityId", true)), null,UtilMisc.toList("parentFacilityId"), null, false);

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
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
		}
		Map<String, Object> result = FastMap.newInstance();
		List facilityIds = FastList.newInstance();

		try {

			GenericValue facilityGroup = EntityUtil.getFirst(delegator.findByAnd("FacilityGroup", UtilMisc.toMap("facilityGroupTypeId", "DAIRY_LMD_TYPE","ownerFacilityId", facilityId)));
			if (UtilValidate.isEmpty(facilityGroup)) {
				return result;
			}
			Map<String, Object> groupDetail = getFacilityGroupDetail(ctx,UtilMisc.toMap("facilityGroupId",facilityGroup.getString("facilityGroupId"),"fromDate", fromDate));
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
		Timestamp dayBegin = UtilDateTime.getDayStart(supplyDate);
		Map<String, String> indentQtyCategory = FastMap.newInstance();
		List<GenericValue> productList = FastList.newInstance();
		List<GenericValue> productCategory = FastList.newInstance();
		List productCategoryIds = FastList.newInstance();
		Map qtyInPiecesMap = FastMap.newInstance();
		BigDecimal crateLtrQty = BigDecimal.ZERO;
		try {
			productCategory = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS, "PACKING_PRODUCT"), UtilMisc.toSet("productCategoryId"), null, null, false);
			productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategory, "productCategoryId", true);

			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.IN, productCategoryIds));
			/*condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
			 */EntityCondition productsListCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("sequenceNum");

			productList = delegator.findList("ProductAndCategoryMember",productsListCondition,UtilMisc.toSet("productId", "productCategoryId"), orderBy,null, false);

			Map resultQtyMap = (Map) getProductCratesAndCans(ctx,UtilMisc.toMap("userLogin", userLogin));
			Map cratesMap = (Map) resultQtyMap.get("piecesPerCrate");
			Map cansMap = (Map) resultQtyMap.get("piecesPerCan");

			for (GenericValue productCat : productList) {
				indentQtyCategory.put(productCat.getString("productId"),productCat.getString("productCategoryId"));
				if (UtilValidate.isNotEmpty(cratesMap)	&& UtilValidate.isNotEmpty(cratesMap.get(productCat.getString("productId")))) {
					qtyInPiecesMap.put(productCat.getString("productId"),(BigDecimal) cratesMap.get(productCat.getString("productId")));
				}
				if (UtilValidate.isNotEmpty(cansMap)&& UtilValidate.isNotEmpty(cansMap.get(productCat.getString("productId")))) {
					qtyInPiecesMap.put(productCat.getString("productId"),(BigDecimal) cansMap.get(productCat.getString("productId")));
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
		} catch (Exception e) {
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
	public static BigDecimal convertPacketsToCrates(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productId = (String) context.get("productId");
		BigDecimal packetQuantity = (BigDecimal) context.get("packetQuantity");
		BigDecimal crateQuantity = BigDecimal.ZERO;
		List productIds = FastList.newInstance();
		productIds.add(productId);
		result = (Map) getProductCratesAndCans(dctx, UtilMisc.toMap("userLogin", userLogin, "productIds", productIds));
		Map piecesPerCrate = (Map) result.get("piecesPerCrate");
		Map piecesPerCan = (Map) result.get("piecesPerCan");
		if (UtilValidate.isNotEmpty(piecesPerCrate)	&& UtilValidate.isNotEmpty(piecesPerCrate.get(productId))) {
			BigDecimal packetPerCrate = (BigDecimal) piecesPerCrate.get(productId);
			crateQuantity = packetQuantity.divide(packetPerCrate, 2, rounding);
		}
		if (UtilValidate.isNotEmpty(piecesPerCan)&& UtilValidate.isNotEmpty(piecesPerCan.get(productId))) {
			BigDecimal packetPerCan = (BigDecimal) piecesPerCan.get(productId);
			crateQuantity = packetQuantity.divide(packetPerCan, 2, rounding);
		}
		return crateQuantity;
	}

	public static BigDecimal convertCratesToPackets(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productId = (String) context.get("productId");
		BigDecimal crateQuantity = (BigDecimal) context.get("crateQuantity");
		BigDecimal packetQuantity = BigDecimal.ZERO;

		List productIds = FastList.newInstance();
		productIds.add(productId);
		result = (Map) getProductCratesAndCans(dctx, UtilMisc.toMap("userLogin", userLogin, "productIds", productIds));
		Map piecesPerCrate = (Map) result.get("piecesPerCrate");
		Map piecesPerCan = (Map) result.get("piecesPerCan");
		if (UtilValidate.isNotEmpty(piecesPerCrate)	&& UtilValidate.isNotEmpty(piecesPerCrate.get(productId))) {
			BigDecimal packetPerCrate = (BigDecimal) piecesPerCrate.get(productId);
			packetQuantity = (packetPerCrate.multiply(crateQuantity)).setScale(0, BigDecimal.ROUND_HALF_UP);
		}
		if (UtilValidate.isNotEmpty(piecesPerCan)&& UtilValidate.isNotEmpty(piecesPerCan.get(productId))) {
			BigDecimal packetPerCan = (BigDecimal) piecesPerCan.get(productId);
			packetQuantity = (packetPerCan.multiply(crateQuantity)).setScale(0,	BigDecimal.ROUND_HALF_UP);
		}
		return packetQuantity;
	}

	public static Map getOpeningBalanceInvoices(DispatchContext dctx,Map<String, ? extends Object> context) {
		//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
		//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		boolean duesByParty = Boolean.FALSE;
		String getDuesByParty = (String) context.get("duesByParty");
		if (UtilValidate.isNotEmpty(getDuesByParty)	&& getDuesByParty.equalsIgnoreCase("Y")) {
			duesByParty = Boolean.TRUE;
		}
		List<GenericValue> pendingOBInvoiceList = FastList.newInstance();
		List exprList = FastList.newInstance();
		List categoryTypeEnumList = UtilMisc.toList("SO_INST", "CR_INST");
		// this flag enables to get all opening balance invoices if it is paid or not to calculate opening balance for facility 
		boolean isForCalOB = Boolean.FALSE;
		if(UtilValidate.isNotEmpty(context.get("isForCalOB")) && ((String)context.get("isForCalOB")).equals("Y")){
			isForCalOB = Boolean.TRUE;
		}
		List ownerPartyIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(duesByParty) && duesByParty) {
			List facilityIdsList = FastList.newInstance();
			try {
				GenericValue facilityDetail = delegator.findOne("Facility",	UtilMisc.toMap("facilityId", facilityId), true);
				if (facilityDetail == null	|| (!facilityDetail.getString("facilityTypeId").equals("ZONE") && (!facilityDetail.getString("facilityTypeId").equals("ROUTE")) && !facilityDetail.getString("facilityTypeId").equals("BOOTH"))) {
					Debug.logInfo("facilityId '" + facilityId+ "'is not a Booth or Zone ", "");
					return ServiceUtil.returnError("facilityId '" + facilityId	+ "'is not a Booth or Zone ");
				}
				if (facilityDetail.getString("facilityTypeId").equals("ZONE")) {
					facilityIdsList = getZoneBooths(delegator, facilityId);
				} else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")) {
					facilityIdsList = getRouteBooths(delegator, facilityId);
				} else {
					facilityIdsList.add(facilityId);
				}
				List facilityOwnerList = delegator.findList("Facility",	EntityCondition.makeCondition("facilityId",	EntityOperator.IN, facilityIdsList), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilityOwnerList, "ownerPartyId", true);
			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}

		} else {
			duesByParty = Boolean.FALSE;
		}
		boolean enableSoCrPmntTrack = Boolean.FALSE;
		try {
			GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap(	"propertyTypeEnumId", "LMS", "propertyName","enableSoCrPmntTrack"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack)&& (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
				enableSoCrPmntTrack = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		if (!enableSoCrPmntTrack) {
			// exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId",
			// EntityOperator.IN,
			// UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
			//exprList.add(EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.NOT_IN, categoryTypeEnumList));
		}
		if (UtilValidate.isNotEmpty(ownerPartyIds)) {
			exprList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, ownerPartyIds));
		}
		if (UtilValidate.isNotEmpty(facilityId) && !duesByParty) {
			exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, getBoothList(delegator, facilityId)));
		}

		if (UtilValidate.isNotEmpty(fromDate)) {
			exprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
		}
		if (UtilValidate.isNotEmpty(thruDate)) {
			exprList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,	UtilDateTime.getDayEnd(thruDate)));
		}
		exprList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.IN,UtilMisc.toList(obInvoiceType, "SHOPEE_RENT", "MIS_INCOME_IN")));
		List invoiceStatusList = UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF");
		if(isForCalOB){//for get Opening Balance it should Invoke
			invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF");
		}
		exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN, invoiceStatusList));

		EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		try {
			pendingOBInvoiceList = delegator.findList("InvoiceAndFacility",	cond, null, null, null, false);
		} catch (Exception e) {
			// TODO: handle exception

		}
		List<GenericValue> tempObInvoiceList = FastList.newInstance();

		for (GenericValue obInvoice : pendingOBInvoiceList) {
			GenericValue tempObInvoice = delegator.makeValue("OrderHeaderFacAndItemBillingInv");
			// tempObInvoice.putAll(obInvoice);
			tempObInvoice.put("parentFacilityId",obInvoice.getString("parentFacilityId"));
			tempObInvoice.put("invoiceId", obInvoice.getString("invoiceId"));
			tempObInvoice.put("originFacilityId",obInvoice.getString("facilityId"));
			tempObInvoice.put("partyId", obInvoice.getString("partyId"));
			tempObInvoice.put("facilityName",obInvoice.getString("facilityName"));
			tempObInvoice.put("estimatedDeliveryDate", UtilDateTime.getDayStart(obInvoice.getTimestamp("invoiceDate")));
			tempObInvoiceList.add(tempObInvoice);
		}
		result.put("invoiceList", tempObInvoiceList);
		result.put("invoiceIds", EntityUtil.getFieldListFromEntityList(pendingOBInvoiceList, "invoiceId", true));
		return result;

	}

	public static List<GenericValue> getAllLmsAndByProdProducts(DispatchContext dctx, Map<String, ? extends Object> context) {
		Timestamp salesDate = UtilDateTime.nowTimestamp();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		if (!UtilValidate.isEmpty(context.get("salesDate"))) {
			salesDate = (Timestamp) context.get("salesDate");
		}
		Timestamp dayBegin = UtilDateTime.getDayStart(salesDate);
		List<GenericValue> productList = FastList.newInstance();
		List condList = FastList.newInstance();
		condList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.IN, UtilMisc.toList("LMS", "BYPROD")));
		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS, null), EntityOperator.OR,
				EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN, dayBegin)));
		EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("sequenceNum");
		try {
			productList = delegator.findList("ProductAndCategoryMember",discontinuationDateCondition, null, orderBy, null, false);
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		return productList;
	}

	public static Map calculateCratesForShipment(DispatchContext dctx,Map<String, ? extends Object> context) {
		//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
		//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String shipmentId = (String) context.get("shipmentId");
		List exprList = FastList.newInstance();
		List productQtyList = FastList.newInstance();
		Timestamp shipDate = null;
		try {
			GenericValue shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
			if (UtilValidate.isEmpty(shipment)) {
				Debug.logError("Valid shipmentId should be given", module);
				return ServiceUtil.returnError("Valid shipmentId should be given");
			}
			shipDate = shipment.getTimestamp("estimatedShipDate");
		} catch (GenericEntityException e) {
			Debug.logError("Error fetching shipment details", module);
			return ServiceUtil.returnError("Error fetching shipment details");
		}
		Map resultCrates = (Map) getProductCratesAndCans(dctx,UtilMisc.toMap("userLogin", userLogin, "saleDate", shipDate));

		Map piecesPerCrate = (Map) resultCrates.get("piecesPerCrate");
		Map piecesPerCan = (Map) resultCrates.get("piecesPerCan");

		List facilityList = FastList.newInstance();
		exprList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.EQUALS, "ORDER_APPROVED"));
		exprList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId));
		EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
		BigDecimal totalCrateQty = BigDecimal.ZERO;
		BigDecimal totalCanQty = BigDecimal.ZERO;
		try {
			List<GenericValue> shippedOrderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null,null, null, false);

			List<GenericValue> excludeSubsidy = EntityUtil.filterByCondition(shippedOrderItems, EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS,"EMP_SUBSIDY"));
			BigDecimal empSubsidyCrate = BigDecimal.ZERO;
			BigDecimal empSubsidyQty = BigDecimal.ZERO;
			String empProdId = "";
			for (GenericValue subsidy : excludeSubsidy) {
				empProdId = subsidy.getString("productId");
				empSubsidyQty = empSubsidyQty.add(subsidy.getBigDecimal("quantity"));
			}
			if (UtilValidate.isNotEmpty(piecesPerCrate)	&& UtilValidate.isNotEmpty(empProdId)&& piecesPerCrate.containsKey(empProdId)) {
				empSubsidyCrate = (empSubsidyQty.divide((BigDecimal) piecesPerCrate.get(empProdId), 0,BigDecimal.ROUND_CEILING));
			}
			List<GenericValue> exluEmpShippedOrderItem = EntityUtil.filterByCondition(shippedOrderItems, EntityCondition.makeCondition("productSubscriptionTypeId",EntityOperator.NOT_EQUAL, "EMP_SUBSIDY"));
			List<String> productList = EntityUtil.getFieldListFromEntityList(shippedOrderItems, "productId", true);

			for (String productId : productList) {
				List<GenericValue> productOrderItems = EntityUtil.filterByCondition(exluEmpShippedOrderItem,EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
				BigDecimal prodTotQty = BigDecimal.ZERO;
				BigDecimal prodTotCrate = BigDecimal.ZERO;
				BigDecimal prodTotCan = BigDecimal.ZERO;
				for (GenericValue orderItem : productOrderItems) {
					BigDecimal qty = orderItem.getBigDecimal("quantity");
					prodTotQty = prodTotQty.add(qty);
				}
				if (UtilValidate.isNotEmpty(piecesPerCrate)	&& piecesPerCrate.containsKey(productId)) {
					prodTotCrate = (prodTotQty.divide((BigDecimal) piecesPerCrate.get(productId), 0,BigDecimal.ROUND_CEILING));
					totalCrateQty = totalCrateQty.add(prodTotCrate);
				}

				// calculate cans
				if (UtilValidate.isNotEmpty(piecesPerCan)&& piecesPerCan.containsKey(productId)) {
					prodTotCan = (prodTotQty.divide((BigDecimal) piecesPerCan.get(productId), 0,BigDecimal.ROUND_CEILING));
					totalCanQty = totalCanQty.add(prodTotCan);
				}
			}
			// caliculating all prod Totals
			productList = EntityUtil.getFieldListFromEntityList(shippedOrderItems, "productId", true);
			for (String productId : productList) {
				List<GenericValue> productOrderItems = EntityUtil.filterByCondition(exluEmpShippedOrderItem,EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
				BigDecimal prodTotQty = BigDecimal.ZERO;
				for (GenericValue orderItem : productOrderItems) {
					BigDecimal qty = orderItem.getBigDecimal("quantity");
					prodTotQty = prodTotQty.add(qty);
				}
				Map tempMap = FastMap.newInstance();
				tempMap.put("productId", productId);
				tempMap.put("quantity", prodTotQty);
				productQtyList.add(tempMap);
			}

			BigDecimal totalFinalCrate = totalCrateQty.add(empSubsidyCrate);
			result.put("totalCans", totalCanQty);
			result.put("totalCrates", totalFinalCrate);
			result.put("productQtyList", productQtyList);
		} catch (Exception e) {
			Debug.logError("Error calculating crates for the shipment", module);
			return ServiceUtil.returnError("Error calculating crates for the shipment");
		}
		return result;
	}

	public static Map<String, Object> getFacilityRateAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		String rateTypeId = (String) context.get("rateTypeId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String rateCurrencyUomId = "INR";
		if (UtilValidate.isNotEmpty(context.get("rateCurrencyUomId"))) {
			rateCurrencyUomId = (String) context.get("rateCurrencyUomId");
		}
		// if from date is null then lets take now timestamp as default
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
		}
		Map result = ServiceUtil.returnSuccess();
		BigDecimal rateAmount = BigDecimal.ZERO;
		String uomId = "";
		// lets get the active rateAmount
		List facilityRates = FastList.newInstance();
		List exprList = FastList.newInstance();
		// facility level
		exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
		exprList.add(EntityCondition.makeCondition("rateTypeId",EntityOperator.EQUALS, rateTypeId));
		exprList.add(EntityCondition.makeCondition("rateCurrencyUomId",	EntityOperator.EQUALS, rateCurrencyUomId));

		EntityCondition paramCond = EntityCondition.makeCondition(exprList,	EntityOperator.AND);
		try {
			facilityRates = delegator.findList("FacilityRate", paramCond, null,	null, null, false);

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		try {
			facilityRates = EntityUtil.filterByDate(facilityRates, fromDate);
			// if no rates at facility level then, lets check for default rate
			if (UtilValidate.isEmpty(facilityRates)) {
				exprList.clear();
				// Default level
				exprList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, "_NA_"));
				exprList.add(EntityCondition.makeCondition("rateTypeId",EntityOperator.EQUALS, rateTypeId));
				exprList.add(EntityCondition.makeCondition("rateCurrencyUomId",	EntityOperator.EQUALS, rateCurrencyUomId));

				EntityCondition cond = EntityCondition.makeCondition(exprList,EntityOperator.AND);
				try {
					facilityRates = delegator.findList("FacilityRate",paramCond, null, null, null, false);

				} catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.toString());
				}
			}

			GenericValue validFacilityRate = EntityUtil.getFirst(facilityRates);
			if (UtilValidate.isNotEmpty(validFacilityRate)) {
				if (UtilValidate.isNotEmpty(validFacilityRate.getString("acctgFormulaId"))) {
					String acctgFormulaId = validFacilityRate.getString("acctgFormulaId");
					BigDecimal slabAmount = (BigDecimal) context.get("slabAmount");
					uomId = validFacilityRate.getString("uomId");
					if (UtilValidate.isEmpty(slabAmount)) {
						slabAmount = BigDecimal.ZERO;
						Debug.logWarning("no slab amount found for acctgFormulaId taking zero as default ",	module);
					}
					Map<String, Object> input = UtilMisc.toMap("userLogin",	userLogin, "acctgFormulaId", acctgFormulaId,"variableValues", "QUANTITY=" + "1", "slabAmount",slabAmount);
					Map<String, Object> incentivesResult = dispatcher.runSync("evaluateAccountFormula", input);
					if (ServiceUtil.isError(incentivesResult)) {
						Debug.logError("unable to evaluate AccountFormula"+ acctgFormulaId, module);
						return ServiceUtil.returnError("unable to evaluate AccountFormula"+ acctgFormulaId);
					}
					double formulaValue = (Double) incentivesResult.get("formulaResult");
					rateAmount = new BigDecimal(formulaValue);

				} else {
					rateAmount = validFacilityRate.getBigDecimal("rateAmount");
					uomId = validFacilityRate.getString("uomId");
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("rateAmount", rateAmount);
		result.put("uomId", uomId);

		return result;
	}

	
	public static Map<String, Object> getRouteDistance(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = ServiceUtil.returnSuccess();
		  Map<String , Object> inputRateAmt = FastMap.newInstance();
		  inputRateAmt.putAll(context);
			BigDecimal facilitySize = BigDecimal.ZERO;
		  try{
			   Map<String, Object> facilitySizeResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
			   if(UtilValidate.isNotEmpty(facilitySizeResult)){
				   BigDecimal tempFacilitySize=(BigDecimal) facilitySizeResult.get("rateAmount");
				   if (tempFacilitySize.compareTo(BigDecimal.ZERO) > 0)
		   			facilitySize = (BigDecimal) facilitySizeResult.get("rateAmount");
		   		}	
				if (ServiceUtil.isError(facilitySizeResult)) {
	   			Debug.logWarning("There was an error while getting Facility Size: " + ServiceUtil.getErrorMessage(result), module);
	       		return ServiceUtil.returnError("There was an error while getting Facility Size: " + ServiceUtil.getErrorMessage(result));          	            
	           }
			} catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
		   result.put("facilitySize", facilitySize);
		return result;
	}
	// Payment services
	public static Map<String, Object> createPaymentForBooth(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		String supplyDate = (String) context.get("supplyDate");
		BigDecimal paymentAmount = ProductEvents.parseBigDecimalForEntity((String) context.get("amount"));
		Locale locale = (Locale) context.get("locale");
		String paymentMethodType = (String) context.get("paymentMethodTypeId");
		Timestamp paymentDate = (Timestamp) context.get("paymentDate");
		String paymentLocationId = (String) context.get("paymentLocationId");
		String paymentRefNum = (String) context.get("paymentRefNum");
		String paymentPurposeType = (String) context.get("paymentPurposeType");
		String issuingAuthority = (String) context.get("issuingAuthority");
		String issuingAuthorityBranch = (String) context.get("issuingAuthorityBranch");
		String instrumentDateStr = (String) context.get("instrumentDate");
		String isEnableAcctg = (String) context.get("isEnableAcctg");
		String ownerPartyId = "";
		boolean useFifo = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("useFifo"))) {
			useFifo = (Boolean) context.get("useFifo");
		}
		boolean sendSMS = Boolean.TRUE;
		if (UtilValidate.isNotEmpty(context.get("sendSMS"))) {
			sendSMS = (Boolean) context.get("sendSMS");
		}
		facilityId = facilityId.toUpperCase();
		String paymentType = "SALES_PAYIN";
		String partyIdTo = "Company";
		String paymentId = "";
		if (UtilValidate.isEmpty(paymentDate)) {
			paymentDate = UtilDateTime.nowTimestamp();
		}
		boolean roundingAdjustmentFlag = Boolean.TRUE;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List exprListForParameters = FastList.newInstance();
		List boothOrdersList = FastList.newInstance();
		Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp instrumentDate = null;
		try {
			paymentTimestamp = UtilDateTime.toTimestamp(dateFormat.parse(supplyDate));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + supplyDate, module);
			return ServiceUtil.returnError(e.toString());
		}
		boolean enableOBInvoiceTrack = Boolean.TRUE;//For OB invoices Consideration
		try{
			 GenericValue tenantConfigEnableOBTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableOBInvoiceForDueAndPayment"), true);
			 if (UtilValidate.isNotEmpty(tenantConfigEnableOBTrack) && (tenantConfigEnableOBTrack.getString("propertyValue")).equals("N")) {
				 enableOBInvoiceTrack = Boolean.FALSE;
			 	} 
		}catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
		 }
		if (UtilValidate.isNotEmpty(instrumentDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
			try {
				instrumentDate = new java.sql.Timestamp(sdf.parse(instrumentDateStr).getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ instrumentDateStr, module);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "	+ instrumentDateStr, module);
			}
		}
		// getting default payment methodtype if empty
		if (UtilValidate.isEmpty(paymentMethodType)) {
			Map partyProfileFacilityMap = (Map) getPartyProfileDafult(dispatcher.getDispatchContext(),UtilMisc.toMap("boothIds", UtilMisc.toList(facilityId),"supplyDate", paymentTimestamp)).get("partyProfileFacilityMap");
			if (UtilValidate.isNotEmpty(partyProfileFacilityMap.get(facilityId))) {
				paymentMethodType = (String) partyProfileFacilityMap.get(facilityId);
			}
		}
		if (UtilValidate.isEmpty(paymentMethodType)) {
			Debug.logError("paymentMethod Configuration not Done=========== ",module);
			Debug.logError("paymentMethod Configuration not Done======For=====  "+ facilityId, module);
			return ServiceUtil.returnError("paymentMethod Configuration not Done======For====="+ facilityId);
		}
		// get all invoices for single party and multiple facility (Ex: chai point case)
		try {
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), false);
			if (UtilValidate.isNotEmpty(facility)) {
				ownerPartyId = facility.getString("ownerPartyId");
			}

		} catch (GenericEntityException e) {
			Debug.log("Cannot fetch entity Facility");
		}

		// Do check for only past dues payments
		if (useFifo) {
			try {
				List exprListForParameters1 = FastList.newInstance();
				exprListForParameters1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,userLogin.getString("partyId")));
				exprListForParameters1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"FACILITY_CASHIER"));
				EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters1, EntityOperator.AND);
				List<GenericValue> faclityPartyList = delegator.findList("FacilityParty", paramCond, null, null, null, false);
				faclityPartyList = EntityUtil.filterByDate(faclityPartyList);
				if (UtilValidate.isEmpty(faclityPartyList)) {
					Debug.logError("you Don't have permission to create payment, Facility Cashier role missing",module);
					return ServiceUtil.returnError("you Don't have permission to create payment, Facility Cashier role missing");
				}
				paymentLocationId = (EntityUtil.getFirst(faclityPartyList)).getString("facilityId");
			} catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
		}
		// lets exclude future day shipment orders
		// this is to exclude the pm sales invoices on that same day
		List feaShipmentIds = getShipmentIds(delegator,UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(paymentTimestamp, 1),"yyyy-MM-dd HH:mm:ss"), null);
		if (UtilValidate.isNotEmpty(feaShipmentIds) && !useFifo) {
			exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_IN, feaShipmentIds));
		}

		exprListForParameters.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, ownerPartyId));
		/*exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));	*/

		exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID", "INVOICE_CANCELLED","INVOICE_WRITEOFF")));
		// checking tenant config to find invoices only for cash or all
		boolean enableSoCrPmntTrack = Boolean.FALSE;
		try {
			GenericValue tenantConfigEnableSoCrPmntTrack = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "LMS", "propertyName",	"enableSoCrPmntTrack"), true);
			if (UtilValidate.isNotEmpty(tenantConfigEnableSoCrPmntTrack)&& (tenantConfigEnableSoCrPmntTrack.getString("propertyValue")).equals("Y")) {
				enableSoCrPmntTrack = Boolean.TRUE;
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e, module);
		}
		List categoryTypeEnumList = UtilMisc.toList("SO_INST", "CR_INST");
		if (enableSoCrPmntTrack) {
			// exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
			exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId",EntityOperator.EQUALS, "CASH"), EntityOperator.OR,EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.IN, categoryTypeEnumList)));
		} else {
			exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN,UtilMisc.toList("EMP_SUBSIDY", "CASH")));
		}

		/*exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));*/

		EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		List<String> orderBy = UtilMisc.toList("-estimatedDeliveryDate");
		try {
			// Here we are trying change the invoice order to apply (LIFO OR FIFO)
			if (useFifo) {
				orderBy = UtilMisc.toList("estimatedDeliveryDate");
			}
			boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null,orderBy, findOptions, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}

		try {
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), false);
			String partyIdFrom = (String) facility.getString("ownerPartyId");

			Map<String, Object> paymentCtx = UtilMisc.<String, Object> toMap("paymentTypeId", paymentType);
			// lets get the opening balance invoices if any	
			if(enableOBInvoiceTrack){
			List<GenericValue> obInvoiceList = (List) getOpeningBalanceInvoices(dctx, UtilMisc.toMap("facilityId", facilityId)).get("invoiceList");
			boothOrdersList.addAll(obInvoiceList);
			}
			List invoiceIds = FastList.newInstance();
			if (UtilValidate.isNotEmpty(boothOrdersList)) {
				boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("parentFacilityId", "originFacilityId","-estimatedDeliveryDate"));
				if (useFifo) {
					boothOrdersList = EntityUtil.orderBy(boothOrdersList, UtilMisc.toList("parentFacilityId", "originFacilityId","estimatedDeliveryDate"));
				}
				invoiceIds = EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", true);

			}
			// Here we are trying change the invoice order to apply (LIFO OR FIFO)
			Map<String, Object> totalAmount = FastMap.newInstance();
			Map boothResult = getBoothDues(dctx,UtilMisc.<String, Object> toMap("boothId",
					facility.getString("facilityId"), "userLogin",userLogin));
			if (!ServiceUtil.isError(boothResult)) {
				Map boothTotalDues = (Map) boothResult.get("boothTotalDues");
				BigDecimal amount = new BigDecimal(boothTotalDues.get("amount").toString());
				//BigDecimal totalDueAmount = new BigDecimal(boothTotalDues.get("totalDueAmount").toString());        			
				/*if(roundingAdjustmentFlag){
					if((amount.subtract(paymentAmount)).compareTo(BigDecimal.ONE) < 0 && (amount.subtract(paymentAmount)).compareTo(BigDecimal.ZERO) > 0){
						paymentAmount = amount;
					}
					if((totalDueAmount.subtract(paymentAmount)).compareTo(BigDecimal.ONE) < 0 && (totalDueAmount.subtract(paymentAmount)).compareTo(BigDecimal.ZERO) > 0){
						paymentAmount = totalDueAmount;
					}    										
				}*/	
			}
			paymentCtx.put("paymentMethodTypeId", paymentMethodType);
			paymentCtx.put("organizationPartyId", partyIdTo);
			paymentCtx.put("partyId", partyIdFrom);
			paymentCtx.put("facilityId", facilityId);
			if (!UtilValidate.isEmpty(paymentLocationId)) {
				paymentCtx.put("paymentLocationId", paymentLocationId);
			}
			if (!UtilValidate.isEmpty(paymentRefNum)) {
				paymentCtx.put("paymentRefNum", paymentRefNum);
			}
			paymentCtx.put("paymentDate", paymentDate);
			paymentCtx.put("issuingAuthority", issuingAuthority);
			paymentCtx.put("issuingAuthorityBranch", issuingAuthorityBranch);
			if (UtilValidate.isNotEmpty(instrumentDate)) {
				paymentCtx.put("instrumentDate", instrumentDate);
			}
			paymentCtx.put("paymentPurposeType", paymentPurposeType);
			paymentCtx.put("statusId", "PMNT_RECEIVED");
			if (UtilValidate.isNotEmpty(isEnableAcctg)) {
				paymentCtx.put("isEnableAcctg", isEnableAcctg);
			}
			paymentCtx.put("amount", paymentAmount);
			paymentCtx.put("userLogin", userLogin);
			paymentCtx.put("invoices", invoiceIds);
			Debug.log("=====paymentAmount===="+paymentAmount+"=====invoiceIds=="+invoiceIds);
			Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
			if (ServiceUtil.isError(paymentResult)) {
				Debug.logError(paymentResult.toString(), module);
				return ServiceUtil.returnError(null, null, null, paymentResult);
			}
			paymentId = (String) paymentResult.get("paymentId");
		} catch (Exception e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		Map result = ServiceUtil.returnSuccess("Payment successfully done.");
		boolean enablePaymentSms = Boolean.FALSE;
		if (sendSMS) {
			try {
				GenericValue tenantConfigEnablePaymentSms = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId", "SMS", "propertyName","enablePaymentSms"), true);
				if (UtilValidate.isNotEmpty(tenantConfigEnablePaymentSms)&& (tenantConfigEnablePaymentSms.getString("propertyValue")).equals("Y")) {
					enablePaymentSms = Boolean.TRUE;
				}
			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}
		}

		result.put("enablePaymentSms", enablePaymentSms);
		result.put("paymentId", paymentId);
		result.put("paymentMethodTypeId", paymentMethodType);
		result.put("statusFlag", "PAID");
		result.put("hideSearch", "N");
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
	public static Map<String, Object> makeBoothPayments(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String paymentChannel = (String) context.get("paymentChannel");
		String transactionId = (String) context.get("transactionId");
		String paymentLocationId = (String) context.get("paymentLocationId");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<Map<String, Object>> boothPayments = (List<Map<String, Object>>) context.get("boothPayments");
		String infoString = "makeBoothPayments:: " + "paymentChannel=" + paymentChannel 
				+";transactionId=" + transactionId + ";paymentLocationId=" + paymentLocationId 
				+ " " + boothPayments;
		Debug.logInfo(infoString, module);
		if (boothPayments.isEmpty()) {
			Debug.logError("No payment amounts found; " + infoString, module);
			return ServiceUtil.returnError("No payment amounts found; "	+ infoString);
		}
		for (Map boothPayment : boothPayments) {
			Map boothResult = getBoothDues(ctx,UtilMisc.<String, Object>toMap("boothId", 
					(String)boothPayment.get("boothId"), "userLogin", userLogin));
			Map boothDues = (Map) boothResult.get("boothDues");
			BigDecimal amount = new BigDecimal(boothDues.get("amount").toString());
			if (amount.compareTo(new BigDecimal(((Double) boothPayment.get("amount")).toString())) > 0) {
				Debug.logError(	"received partial payment or no dues for booth :"+ (String) boothPayment.get("boothId"), module);
				return ServiceUtil.returnError("received partial payment or no dues for booth :"+ (String) boothPayment.get("boothId"));
			}
			Map<String, Object> paymentCtx = UtilMisc.<String, Object> toMap("paymentMethodTypeId", paymentChannel);
			paymentCtx.put("userLogin", context.get("userLogin"));
			paymentCtx.put("facilityId",((String) boothPayment.get("boothId")).toUpperCase());
			paymentCtx.put("supplyDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"));
			paymentCtx.put("paymentLocationId", paymentLocationId);
			paymentCtx.put("paymentRefNum", transactionId);
			paymentCtx.put("amount",((Double) boothPayment.get("amount")).toString());

			Map<String, Object> paidPaymentCtx = UtilMisc.<String, Object> toMap("paymentMethodTypeId",paymentChannel);
			paidPaymentCtx.put("paymentDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd"));
			paidPaymentCtx.put("facilityId",(String) boothPayment.get("boothId"));

			Map boothsPaymentsDetail = getBoothPaidPayments(ctx, paidPaymentCtx);
			List boothPaymentsList = (List) boothsPaymentsDetail.get("boothPaymentsList");
			if (boothPaymentsList.size() > 0) {
				Debug.logError("Already received payment for booth "+ (String) boothPayment.get("boothId") + " from ,"+ paymentChannel+ "hence skipping... Existing payment details:"+ boothPaymentsList.get(0)	+ "; Current payment details:" + paymentCtx, module);
				return ServiceUtil.returnError("Already received payment for booth "+ (String) boothPayment.get("boothId")+ " from ," + paymentChannel
								+ "hence skipping... Existing payment details:"+ boothPaymentsList.get(0)+ "; Current payment details:" + paymentCtx);
			}
			try {
				Map<String, Object> paymentResult = dispatcher.runSync("createPaymentForBooth", paymentCtx);
				if (ServiceUtil.isError(paymentResult)) {
					Debug.logError("Payment failed for: " + infoString + "["+ paymentResult + "]", module);
					return paymentResult;
				}
				Debug.logInfo("Made following payment:" + paymentCtx, module);
			} catch (GenericServiceException e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createSequenceForVATInvoice(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List facilityIds = (List) context.get("facilityIds");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}
		List ownerPartyIds = FastList.newInstance();
		
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		if (UtilValidate.isEmpty(thruDate)) {
			thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		}
		if (isByParty && UtilValidate.isNotEmpty(facilityIds)) {
			try {
				List facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",	EntityOperator.IN, facilityIds), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilities, "ownerPartyId", true);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		Map finYearContext = FastMap.newInstance();
		finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
		finYearContext.put("organizationPartyId", "Company");
		finYearContext.put("userLogin", userLogin);
		finYearContext.put("findDate", fromDate);
		finYearContext.put("excludeNoOrganizationPeriods", "Y");
		List customTimePeriodList = FastList.newInstance();
		Map resultCtx = FastMap.newInstance();
		try{
			resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
			if(ServiceUtil.isError(resultCtx)){
				Debug.logError("Problem in fetching financial year ", module);
				return ServiceUtil.returnError("Problem in fetching financial year ");
			}
		}catch(GenericServiceException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
		String finYearId = "";
		if(UtilValidate.isNotEmpty(customTimePeriodList)){
			GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
			finYearId = (String)customTimePeriod.get("customTimePeriodId");
		}
		List conditionList = FastList.newInstance();
		
		if(UtilValidate.isNotEmpty(ownerPartyIds)){
			conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, ownerPartyIds));
		}
		else{
			if(UtilValidate.isNotEmpty(facilityIds)){
				conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds));
			}
		}
		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		conditionList.add(EntityCondition.makeCondition("dueDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS, "VAT_SALE"));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<String> invoiceIds = FastList.newInstance();
		List<GenericValue> extInvoices = FastList.newInstance();
		try {
			extInvoices = delegator.findList("InvoiceItemInvoiceItemTypeInvoice", condition, UtilMisc.toSet("invoiceId"), UtilMisc.toList("dueDate", "facilityId"), null, false);
			invoiceIds = EntityUtil.getFieldListFromEntityList(extInvoices, "invoiceId", true);
			
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId",EntityOperator.IN, invoiceIds));
		conditionList.add(EntityCondition.makeCondition("billOfSaleTypeId",EntityOperator.EQUALS, "VAT_INV"));
		conditionList.add(EntityCondition.makeCondition("finYearId",EntityOperator.EQUALS, finYearId));
		EntityCondition billOfSaleCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List excludeInvoiceIds = FastList.newInstance();
		try {
			List<GenericValue> extBillOfSaleInvoices = delegator.findList("BillOfSaleInvoiceSequence", billOfSaleCond, UtilMisc.toSet("invoiceId"), null, null, false);
			excludeInvoiceIds = EntityUtil.getFieldListFromEntityList(extBillOfSaleInvoices, "invoiceId", true); 
			int i = 0;
			invoiceIds.removeAll(excludeInvoiceIds);		
			for(String vatInvId : invoiceIds){
				i++;
				GenericValue billOfSale = delegator.makeValue("BillOfSaleInvoiceSequence");
				billOfSale.put("billOfSaleTypeId", "VAT_INV");
				billOfSale.put("invoiceId", vatInvId);
				billOfSale.put("finYearId", finYearId);
				delegator.setNextSubSeqId(billOfSale, "sequenceId", 10, 1);
	            delegator.create(billOfSale);
	            if(i%500 == 0){
	            	Debug.log("Records processed ###########"+i);
	            }
	            
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		return result;
	}
	public static Map<String, Object> getPartyByRoleType(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String roleTypeId = (String) context.get("roleTypeId");
		//List partyRoleTypes = UtilMisc.toList("MIS_CUSTOMER", "IC_WHOLESALE", "Retailer");
		
		List conditionList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(roleTypeId)){
			conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, roleTypeId));
		}
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<String> partyIds = FastList.newInstance();
		List<GenericValue> partyDetails = FastList.newInstance();
		try {
			partyDetails = delegator.findList("PartyRoleNameDetail", condition, UtilMisc.toSet("partyId", "roleTypeId", "firstName", "lastName", "groupName"), UtilMisc.toList("partyId"), null, false);
			partyIds = EntityUtil.getFieldListFromEntityList(partyDetails, "partyId", true);
			
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyDetails", partyDetails);
		result.put("partyIds", partyIds);
		return result;
	}
	
	
	public static Map<String, Object> getFacilityOwnerMap(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List ownerPartyIds = (List) context.get("ownerPartyIds");
		List facilityIds = (List) context.get("facilityIds");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List conditionList = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(ownerPartyIds)) {
			conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, ownerPartyIds));
		}
		if (UtilValidate.isNotEmpty(facilityIds)) {
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds));
		}
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List facilities = FastList.newInstance();
		try{
			facilities = delegator.findList("Facility", condition, UtilMisc.toSet("facilityId", "ownerPartyId"), null, null, false);
		}catch(GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		List<String> existFacilityIds = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", true);
		List<String> existOwnerPartyIds = EntityUtil.getFieldListFromEntityList(facilities, "ownerPartyId", true);
		Map partyFacilityMap = FastMap.newInstance();
		Map facilityPartyMap = FastMap.newInstance();
		for(String facId : existFacilityIds){
			List<GenericValue> ownerPartyList = EntityUtil.filterByCondition(facilities, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facId));
			List facOwnerPartyIds = EntityUtil.getFieldListFromEntityList(ownerPartyList, "ownerPartyId", true);
			facilityPartyMap.put(facId, facOwnerPartyIds);
		}
		for(String partyId : existOwnerPartyIds){
			List<GenericValue> facilityPartyList = EntityUtil.filterByCondition(facilities, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
			List partyFacilityIds = EntityUtil.getFieldListFromEntityList(facilityPartyList, "facilityId", true);
			partyFacilityMap.put(partyId, partyFacilityIds);
		}
		result.put("facilityPartyMap", facilityPartyMap);
		result.put("partyFacilityMap", partyFacilityMap);
		return result;
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
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String paymentLocationId = "";
		List paymentIds = FastList.newInstance();
		if (UtilValidate.isEmpty(boothIds)) {
			Debug.logError("No payment amounts found; ", module);
			return ServiceUtil.returnError("No payment amounts found; ");
		}
		String paymentMethodType = "";
		Map partyProfileFacilityMap = (Map) getPartyProfileDafult(dispatcher.getDispatchContext(),UtilMisc.toMap("boothIds", boothIds, "supplyDate",UtilDateTime.nowTimestamp())).get("partyProfileFacilityMap");

		for (int i = 0; i < boothIds.size(); i++) {
			String boothId = (String) boothIds.get(i);
			Map boothResult = getBoothDues(ctx,UtilMisc.<String, Object> toMap("boothId",
					boothIds.get(i),"userLogin", userLogin));
			if (ServiceUtil.isError(boothResult)) {
				Debug.logError("No payment amounts found; " + boothResult,	module);
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(boothResult));
			}
			try {
				List exprListForParameters = FastList.newInstance();
				exprListForParameters.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,userLogin.getString("partyId")));
				exprListForParameters.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"FACILITY_CASHIER"));
				EntityCondition paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
				List<GenericValue> faclityPartyList = delegator.findList("FacilityParty", paramCond, null, null, null, false);
				if (UtilValidate.isEmpty(faclityPartyList)) {
					Debug.logError("you Don't have permission to create payment, Facility Cashier role missing",module);
					return ServiceUtil.returnError("you Don't have permission to create payment, Facility Cashier role missing");
				}
				faclityPartyList = EntityUtil.filterByDate(faclityPartyList);
				paymentLocationId = (EntityUtil.getFirst(faclityPartyList)).getString("facilityId");
			} catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.toString());
			}
			Map boothDues = (Map) boothResult.get("boothDues");
			Map<String, Object> paymentCtx = UtilMisc.<String, Object> toMap("userLogin", userLogin);
			// paymentCtx.put("paymentMethodTypeId", "CASH_PAYIN");

			if (UtilValidate.isNotEmpty(partyProfileFacilityMap.get(boothId))) {
				paymentMethodType = (String) partyProfileFacilityMap.get(boothId);
				paymentCtx.put("paymentMethodTypeId", paymentMethodType);
			}

			paymentCtx.put("facilityId", boothId);
			paymentCtx.put("supplyDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"));
			paymentCtx.put("paymentLocationId", paymentLocationId);
			paymentCtx.put("amount",((Double) boothDues.get("amount")).toString());
			try {
				Map<String, Object> paymentResult = dispatcher.runSync("createPaymentForBooth", paymentCtx);
				if (ServiceUtil.isError(paymentResult)) {
					Debug.logError("Payment failed for:" + paymentResult + "]",	module);
					return paymentResult;
				}
				paymentIds.add(paymentResult.get("paymentId"));
			} catch (GenericServiceException e) {
				// TODO: handle exception
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}

		}
		if (UtilValidate.isNotEmpty(paymentIds) && paymentIds.size() > 1) {
			try {
				Map resultCtx = dispatcher.runSync("createPaymentGroupAndMember", UtilMisc.toMap("paymentIds", paymentIds, "paymentGroupTypeId","ROUTE_BATCH_PAYMENT", "userLogin", userLogin));
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("Error while creating payment group: "+ ServiceUtil.getErrorMessage(resultCtx), module);
					return ServiceUtil.returnError("Error while creating payment group: "+ ServiceUtil.getErrorMessage(resultCtx));
				}
			} 
			catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}

		}
		Map result = ServiceUtil.returnSuccess("Payment successfully done.");
		result.put("paymentIds", paymentIds);
		return result;
	}

	public static Map<String, Object> getPartyProfileDafult(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List boothIds = (List) context.get("boothIds");
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		if (UtilValidate.isEmpty(supplyDate)) {
			supplyDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		}
		Map<String, Object> result = FastMap.newInstance();
		GenericValue boothFacility;
		Map partyProfileFacilityMap = FastMap.newInstance();
		Map partyPaymentMethodDesc = FastMap.newInstance();
		Map paymentTypeFacilityMap = FastMap.newInstance();
		try {
			List partyCondList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(boothIds)) {
				List<GenericValue> facilityOwners = delegator.findList("Facility", EntityCondition.makeCondition("facilityId",EntityOperator.IN, boothIds), UtilMisc.toSet("ownerPartyId"), null, null, false);
				List ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilityOwners, "ownerPartyId", true);
				// partyCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN ,boothIds));
				partyCondList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, ownerPartyIds));
			}
			partyCondList.add(EntityCondition.makeCondition("fromDate",	EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayStart(supplyDate)));
			partyCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
					EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayEnd(supplyDate))));

			EntityCondition partyCondition = EntityCondition.makeCondition(partyCondList, EntityOperator.AND);
			/* List<GenericValue> facilityList = delegator.findList("Facility", facCond, null, null, null, true); 
			 * List partyIdsList = EntityUtil.getFieldListFromEntityList(facilityList, "ownerPartyId", true);
			 */
			List<GenericValue> partyProfileDefaultList = delegator.findList("PartyProfileDefaultAndFacilityAndMethodType",partyCondition, null, null, null, true);
			partyProfileDefaultList = EntityUtil.filterByDate(partyProfileDefaultList, supplyDate);
			for (GenericValue partyProfileDafault : partyProfileDefaultList) {
				partyProfileFacilityMap.put(partyProfileDafault.getString("facilityId"),partyProfileDafault.getString("defaultPayMeth"));
				partyPaymentMethodDesc.put(partyProfileDafault.getString("facilityId"),	partyProfileDafault.getString("description"));
			}
			List<String> paymentTypes = EntityUtil.getFieldListFromEntityList(partyProfileDefaultList, "defaultPayMeth", true);
			for (String paymentType : paymentTypes) {
				List<GenericValue> paymentTypeEntry = EntityUtil.filterByCondition(partyProfileDefaultList,EntityCondition.makeCondition("defaultPayMeth",EntityOperator.EQUALS, paymentType));
				List paymentTypeFacility = EntityUtil.getFieldListFromEntityList(paymentTypeEntry,"facilityId", true);
				paymentTypeFacilityMap.put(paymentType, paymentTypeFacility);
			}

			result.put("partyProfileFacilityMap", partyProfileFacilityMap);
			result.put("partyPaymentMethodDesc", partyPaymentMethodDesc);
			result.put("paymentTypeFacilityMap", paymentTypeFacilityMap);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}

	public static Map<String, Object> getVehicleRole(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String) context.get("facilityId");
		Timestamp supplyDate = (Timestamp) context.get("supplyDate");
		if (UtilValidate.isEmpty(supplyDate)) {
			supplyDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		}
		Map<String, Object> result = FastMap.newInstance();
		GenericValue routeFacility;
		GenericValue vehicleRole = null;
		try {
			routeFacility = delegator.findOne("Facility", true,UtilMisc.toMap("facilityId", facilityId));
			if (routeFacility == null) {
				Debug.logError("Invalid routeId " + facilityId, module);
				return ServiceUtil.returnError("Invalid routeId " + facilityId);
			}
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, "ROUTE_VEHICLE"));
			condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,routeFacility.getString("facilityId")));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> vehicleRoleList = delegator.findList("VehicleRole", cond, null, null, null, true);
			vehicleRoleList = EntityUtil.filterByDate(vehicleRoleList,supplyDate);
			if (UtilValidate.isNotEmpty(vehicleRoleList)) {
				vehicleRole = EntityUtil.getFirst(vehicleRoleList);
			}
			result.put("vehicleRole", vehicleRole);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}

	public static Map<String, Object> getVehicleTrip(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String routeId = (String) context.get("routeId");
		String shipmentId = (String) context.get("shipmentId");
		Map<String, Object> result = FastMap.newInstance();
		GenericValue routeFacility;
		GenericValue vehicleTrip = null;
		try {
			routeFacility = delegator.findOne("Facility", true,	UtilMisc.toMap("facilityId", routeId));
			if (routeFacility == null) {
				Debug.logError("Invalid routeId " + routeId, module);
				return ServiceUtil.returnError("Invalid routeId " + routeId);
			}
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("originFacilityId",EntityOperator.EQUALS, routeId));
			condList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> vehicleTripList = delegator.findList("VehicleTrip", cond, null, null, null, true);
			if (UtilValidate.isNotEmpty(vehicleTripList)) {
				vehicleTrip = EntityUtil.getFirst(vehicleTripList);
			}
			result.put("vehicleTrip", vehicleTrip);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
	
	public static Map<String, Object> getProductQtyConversions(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productCategoryId = (String) context.get("productCategoryId");
		List productList = (List) context.get("productList");
		Map result = ServiceUtil.returnSuccess();
		List<String> productIds = FastList.newInstance();
		Map productConversionDetail = FastMap.newInstance();
		if(UtilValidate.isEmpty(productList) && UtilValidate.isNotEmpty(productCategoryId)){
			productList = ProductWorker.getProductsByCategory(delegator , productCategoryId, null);
		}
		
		productIds = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
		
		List<GenericValue> products = null;
		List<GenericValue> productAttributes = null;
		
		try{
			products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
			productAttributes = delegator.findList("ProductAttribute", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
			
		}catch(GenericEntityException e){
			Debug.logError(e, module);
		}
		
		for(String prodId : productIds){
			GenericValue productDetail = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId)));
			List<GenericValue> productAttribute = (List)EntityUtil.filterByCondition(productAttributes, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
			Map uomMapDetail = FastMap.newInstance();
			for(GenericValue prodAttr : productAttribute){
				uomMapDetail.put(prodAttr.getString("attrName"), new BigDecimal(prodAttr.getString("attrValue")));
			}
			uomMapDetail.put("LtrKg", productDetail.getBigDecimal("quantityIncluded"));
			productConversionDetail.put(prodId, uomMapDetail);
		}
		result.put("productConversionDetails", productConversionDetail);
		return result;
	}
	
	public static Map<String, Object> getProductCratesAndCans(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp saleDate = (Timestamp) context.get("saleDate");
		List productIds = (List) context.get("productIds");
		Map result = ServiceUtil.returnSuccess();
		if (UtilValidate.isEmpty(saleDate)) {
			saleDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		List conditionList = FastList.newInstance();
		Map<String, Object> cratesMap = FastMap.newInstance();
		Map cansMap = FastMap.newInstance();
		try {

			List attrNameList = UtilMisc.toList("CRATE", "CAN");
			conditionList.add(EntityCondition.makeCondition("attrName",EntityOperator.IN, attrNameList));
			if (UtilValidate.isNotEmpty(productIds)) {
				conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, productIds));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> productCratesCans = delegator.findList("ProductAttribute", condition, null, null, null, false);

			String attrName = "";
			for (GenericValue productAttr : productCratesCans) {
				attrName = productAttr.getString("attrName");
				BigDecimal attrValue = new BigDecimal(productAttr.getString("attrValue"));

				if (attrName.equals("CRATE")) {
					cratesMap.put(productAttr.getString("productId"), attrValue);
				} 
				else {
					cansMap.put(productAttr.getString("productId"), attrValue);
				}
			}

		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("piecesPerCrate", cratesMap);
		result.put("piecesPerCan", cansMap);
		return result;
	}

	public static Map<String, Object> getRouteVehicleForShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String supplyDateStr = (String) context.get("supplyDate");
		String routeId = (String) context.get("routeId");
		String tripId = (String) context.get("tripId");
		String returnHeaderTypeId = (String) context.get("returnHeaderTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String subscriptionTypeId = (String) context.get("subscriptionTypeId");
		Map result = ServiceUtil.returnSuccess();
		List conditionList = FastList.newInstance();
		Timestamp supplyDate = null;
		try {
			if (UtilValidate.isNotEmpty(supplyDateStr)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
				try {
					supplyDate = new java.sql.Timestamp(sdf.parse(supplyDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError("Cannot parse date string: " + supplyDateStr,module);
					return ServiceUtil.returnError("Cannot parse date string");
				} catch (NullPointerException e) {
					Debug.logError("Cannot parse date string: " + supplyDateStr,module);
					return ServiceUtil.returnError("Cannot parse date string");
				}
			}
			if (UtilValidate.isEmpty(returnHeaderTypeId)) {
				Debug.logError("Return type is empty", module);
				return ServiceUtil.returnError("Return type is empty");
			}
			if (UtilValidate.isEmpty(supplyDate)) {
				Debug.logError("supply date is empty", module);
				return ServiceUtil.returnError("supply date is empty");
			}
			Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
			Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,	dayEnd));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
			conditionList.add(EntityCondition.makeCondition("routeId",EntityOperator.EQUALS, routeId));
			if (UtilValidate.isNotEmpty(subscriptionTypeId)) {
				if ("AM".equals(subscriptionTypeId)) {
					conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS,"AM_SHIPMENT"));
				} else if ("PM".equals(subscriptionTypeId)) {
					conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS,"PM_SHIPMENT"));
				}
			}
			/*conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));*/
			EntityCondition cond = EntityCondition.makeCondition(conditionList,	EntityOperator.AND);
			List<GenericValue> shipment = delegator.findList("Shipment", cond,null, null, null, false);
			String shipmentId = "";
			String vehicleId = "";
			String sequenceNum = "";
			if (UtilValidate.isNotEmpty(shipment)) {
				shipmentId = ((GenericValue) EntityUtil.getFirst(shipment)).getString("shipmentId");
				vehicleId = ((GenericValue) EntityUtil.getFirst(shipment)).getString("vehicleId");
			}
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("originFacilityId",	EntityOperator.EQUALS, routeId));
			// conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("RETURN_CANCELLED")));
			EntityCondition vhCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> vehicleTrp = delegator.findList("VehicleTrip",vhCondition, null, null, null, false);
			if (UtilValidate.isNotEmpty(vehicleTrp)) {
				vehicleId = ((GenericValue) EntityUtil.getFirst(vehicleTrp)).getString("vehicleId");
				sequenceNum = ((GenericValue) EntityUtil.getFirst(vehicleTrp)).getString("sequenceNum");
			} else {
				Debug.logError("Truck not Loaded yet for Route " + routeId+ " on Selected Date !", module);
				return ServiceUtil.returnError("Truck not Loaded yet for Route "+ routeId + " on Selected Date !");
			}

			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS, vehicleId));
			conditionList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS, sequenceNum));
			conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, routeId));
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.IN, UtilMisc.toList("VEHICLE_RETURNED")));
			EntityCondition vhTripCondi = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> vehicleTripStatus = delegator.findList("VehicleTripStatus", vhTripCondi, null, null, null, false);
			if (UtilValidate.isNotEmpty(vehicleTripStatus)) {
				vehicleId = ((GenericValue) EntityUtil.getFirst(vehicleTripStatus)).getString("vehicleId");
				Debug.logError("Vehicle " + vehicleId+ " is Finalized and in Return State", module);
				return ServiceUtil.returnError("Vehicle " + vehicleId+ " is Finalized and in Return State");
			}
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,UtilMisc.toList("RETURN_CANCELLED")));
			EntityCondition expr = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List<GenericValue> returnHeader = delegator.findList("ReturnHeader", expr, null, null, null, false);
			if (UtilValidate.isNotEmpty(returnHeader)) {
				String returnId = ((GenericValue) EntityUtil.getFirst(returnHeader)).getString("returnId");
				Debug.logError(	"One return allowed for one shipment, Cancel return :"+ returnId + " and re-enter the return", module);
				return ServiceUtil.returnError("One return allowed for one shipment, Cancel return :"+ returnId + " and re-enter the return");
			}
			result.put("vehicleId", vehicleId);
			result.put("routeId", routeId);
			result.put("shipmentId", shipmentId);

		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}

	public static Map getByProductPaymentDetails(DispatchContext dctx,Map<String, ? extends Object> context) {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = FastMap.newInstance();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List facilityList = (List) context.get("facilityList");
		boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}
		int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1);
		List payments = FastList.newInstance();
		List conditionList = FastList.newInstance();

		if (UtilValidate.isEmpty(facilityList)) {
			facilityList = (List) getAllBooths(delegator, null).get("boothsList");
		}
		List ownerPartyIds = FastList.newInstance();
		if (isByParty) {
			try {
				List<GenericValue> facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityList), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilities, "ownerPartyId", true);
			} catch (Exception e) {
				Debug.logError(e, e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}

		}

		// conditionList.add(EntityCondition.makeCondition("paymentPurposeType",EntityOperator.EQUALS, "BYPROD_PAYMENT"));
		if (isByParty) {
			if (UtilValidate.isNotEmpty(ownerPartyIds)) {
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN,	ownerPartyIds)));
			}
		} else {
			if (UtilValidate.isNotEmpty(facilityList)) {
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.IN,facilityList)));
			}
		}

		conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, "Company"));
		conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.NOT_EQUAL, "SECURITYDEPSIT_PAYIN"));
		conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		conditionList.add(EntityCondition.makeCondition("paymentDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.NOT_EQUAL,"CREDITNOTE_PAYIN"));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"PMNT_RECEIVED"), EntityOperator.OR, EntityCondition.makeCondition(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "PMNT_VOID"),EntityOperator.AND, EntityCondition.makeCondition("chequeReturns", EntityOperator.EQUALS, "Y"))));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			payments = delegator.findList("Payment", condition, null, UtilMisc.toList("facilityId", "paymentDate", "partyIdFrom"), null,false);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		List paymentParties = FastList.newInstance();
		if (isByParty) {
			paymentParties = EntityUtil.getFieldListFromEntityList(payments,"partyIdFrom", true);
		} else {
			paymentParties = EntityUtil.getFieldListFromEntityList(payments,"facilityId", true);
		}

		Map paymentDetailsMap = FastMap.newInstance();
		Map facilityPaidMap = FastMap.newInstance();

		if (UtilValidate.isNotEmpty(paymentParties)) {
			for (int i = 0; i < paymentParties.size(); i++) {
				Map dayPaymentDetail = FastMap.newInstance();
				String facilityId = ((String) paymentParties.get(i)).toUpperCase();
				BigDecimal totalPaidAmnt=BigDecimal.ZERO;
				List<GenericValue> facilityPayments = FastList.newInstance();
				if (isByParty) {
					facilityPayments = EntityUtil.filterByCondition(payments,EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyIdFrom"),EntityOperator.EQUALS, EntityFunction.UPPER(((String) facilityId).toUpperCase())));
				} else {
					facilityPayments = EntityUtil.filterByCondition(payments,EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"),EntityOperator.EQUALS, EntityFunction.UPPER(((String) facilityId).toUpperCase())));
				}
				if (UtilValidate.isNotEmpty(facilityPayments)) {
					for (int j = 0; j < facilityPayments.size(); j++) {
						GenericValue facilityPayment = (GenericValue) facilityPayments.get(j);
						Map dataMap = FastMap.newInstance();
						Map tempDayPayments = FastMap.newInstance();
						Timestamp paymentDate = UtilDateTime.getDayStart((Timestamp) facilityPayment.get("paymentDate"));
						Timestamp chequeDate = UtilDateTime.getDayStart((Timestamp) facilityPayment.get("effectiveDate"));
						String paymentRefNum = facilityPayment.getString("paymentRefNum");
						String issuingAuthority = facilityPayment.getString("issuingAuthority");
						String issuingAuthorityBranch = facilityPayment.getString("issuingAuthorityBranch");
						String statusId = facilityPayment.getString("statusId");
						BigDecimal amount = (BigDecimal) facilityPayment.get("amount");
						totalPaidAmnt=totalPaidAmnt.add(amount);
						String paymentMethodTypeId = facilityPayment.getString("paymentMethodTypeId");
						dataMap.put("paymentRefNum", paymentRefNum);
						dataMap.put("issuingAuthority", issuingAuthority);
						dataMap.put("paymentMethodTypeId", paymentMethodTypeId);
						dataMap.put("issuingAuthorityBranch",issuingAuthorityBranch);
						if (paymentMethodTypeId.equalsIgnoreCase("CHEQUE_PAYIN")) {
							dataMap.put("chequeDate", chequeDate);
						} else {
							dataMap.put("chequeDate", "");
						}
						dataMap.put("amount", amount);
						if (dayPaymentDetail.containsKey(paymentDate)) {
							Map tempDayData = FastMap.newInstance();
							tempDayData = (Map) dayPaymentDetail.get(paymentDate);
							List tempList = FastList.newInstance();
							Map tempMap = FastMap.newInstance();
							if (statusId.equals("PMNT_VOID")) {
								if (tempDayData.containsKey("chequeReturn")) {
									List tempChequeReturn = (List) tempDayData.get("chequeReturn");
									tempChequeReturn.add(dataMap);
									tempDayData.put("chequeReturn",	tempChequeReturn);
								} else {
									tempList.add(dataMap);
									tempMap.put("chequeReturn", tempList);
									tempDayData.putAll(tempMap);
								}
							} else {
								if (tempDayData.containsKey("payment")) {
									List tempPayment = (List) tempDayData.get("payment");
									tempPayment.add(dataMap);
									tempDayData.put("payment", tempPayment);
								} else {
									tempList.add(dataMap);
									tempMap.put("payment", tempList);
									tempDayData.putAll(tempMap);
								}
							}
							dayPaymentDetail.put(paymentDate, tempDayData);
						} 
						else {
							List tempPayment = FastList.newInstance();
							tempPayment.add(dataMap);
							Map initMap = FastMap.newInstance();
							if (statusId.equals("PMNT_VOID")) {
								initMap.put("chequeReturn", tempPayment);
							} else {
								initMap.put("payment", tempPayment);
							}
							dayPaymentDetail.put(paymentDate, initMap);
						}
					}
				}
				paymentDetailsMap.put(facilityId, dayPaymentDetail);
				facilityPaidMap.put(facilityId,totalPaidAmnt);
				
			}
		}
		result.put("paymentDetails", paymentDetailsMap);
		result.put("facilityPaidMap", facilityPaidMap);
		return result;
	}

	public static Map getByProductDayWiseInvoiceTotals(DispatchContext dctx,Map<String, ? extends Object> context) {
		// Timestamp fromDate, Timestamp thruDate, List facilityList
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List facilityList = (List) context.get("facilityList");
		boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}
		Map result = FastMap.newInstance();
		List boothsList = FastList.newInstance();
		if (UtilValidate.isEmpty(facilityList)) {
			facilityList = (List) getAllBooths(delegator, null).get("boothsList");
		}

		List ownerPartyIds = FastList.newInstance();
		if (isByParty) {
			try {
				List<GenericValue> facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityList), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilities, "ownerPartyId", true);
			} catch (Exception e) {
				Debug.logError(e, e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}

		}
		Map dayWisePartyInvoiceDetail = new TreeMap();
		Map boothInvTotalMap = FastMap.newInstance();
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.NOT_IN,UtilMisc.toList("STATUTORY_OUT")));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.NOT_IN,UtilMisc.toList("INCO_FINEPENALTY_CHQ")));
			conditionList.add(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conditionList.add(EntityCondition.makeCondition("dueDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			if (isByParty) {
				conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN, ownerPartyIds));
			} else {
				conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityList));
			}

			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.NOT_IN,UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> invoiceItems = delegator.findList("InvoiceAndItemType", condition, UtilMisc.toSet("invoiceId"), null, null, false);
			List tempInvIds = EntityUtil.getFieldListFromEntityList(invoiceItems, "invoiceId", true);
			
			List<GenericValue> invoices = delegator.findList("Invoice", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, tempInvIds), UtilMisc.toSet("invoiceId", "dueDate", "facilityId", "partyId"), null, null, false);
			
			
			int intervalDays = (UtilDateTime.getIntervalInDays(fromDate,thruDate) + 1);
			for (int k = 0; k < intervalDays; k++) {

				List condList = FastList.newInstance();
				Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
				Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
				Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
				List<GenericValue> daywiseInvoices = EntityUtil.filterByCondition(invoices,EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO,dayStart),EntityOperator.AND,EntityCondition.makeCondition("dueDate",EntityOperator.LESS_THAN_EQUAL_TO,dayEnd)));
				List dayPartyInvoices = FastList.newInstance();
				if (isByParty) {
					dayPartyInvoices = (List) EntityUtil.getFieldListFromEntityList(daywiseInvoices,"partyId", true);
				} else {
					dayPartyInvoices = (List) EntityUtil.getFieldListFromEntityList(daywiseInvoices,"facilityId", true);
				}
				List dayPartyList = FastList.newInstance();
				for (int j = 0; j < dayPartyInvoices.size(); j++) {
					String booth = (String) dayPartyInvoices.get(j);
					booth = booth.toUpperCase();
					if (!dayPartyList.contains(booth)) {
						dayPartyList.add(booth);
					}
				}
				Map partySale = FastMap.newInstance();
				String boothId = "";
				Map invDetail = FastMap.newInstance();
				for (int i = 0; i < dayPartyList.size(); i++) {
					boothId = (String) dayPartyList.get(i);
					BigDecimal boothInvTotal = BigDecimal.ZERO;
					List<GenericValue> daywiseBoothwiseSale = FastList.newInstance();
					if (isByParty) {
						daywiseBoothwiseSale = EntityUtil.filterByCondition(daywiseInvoices, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"),EntityOperator.EQUALS, EntityFunction.UPPER(((String) boothId).toUpperCase())));
					} else {
						daywiseBoothwiseSale = EntityUtil.filterByCondition(daywiseInvoices, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("facilityId"),EntityOperator.EQUALS, EntityFunction.UPPER(((String) boothId).toUpperCase())));
					}

					for (int j = 0; j < daywiseBoothwiseSale.size(); j++) {
						GenericValue dayBoothSale = (GenericValue) daywiseBoothwiseSale.get(j);
						String invoiceId = dayBoothSale.getString("invoiceId");
						Timestamp dueDate = (Timestamp) dayBoothSale.get("dueDate");
						BigDecimal amount = (BigDecimal) InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
						boothInvTotal = boothInvTotal.add(amount);
						if (invDetail.containsKey(boothId)) {
							Map tempDetail = (Map) invDetail.get(boothId);
							BigDecimal tempAmount = (BigDecimal) tempDetail.get("amount");
							String tempInvoiceId = (String) tempDetail.get("invoiceId");
							tempDetail.put("invoiceId", tempInvoiceId + ","+ invoiceId);
							tempDetail.put("amount", amount.add(tempAmount));
							invDetail.put(boothId, tempDetail);
						} 
						else {
							Map detailMap = FastMap.newInstance();
							detailMap.put("invoiceId", invoiceId);
							detailMap.put("amount", amount);
							invDetail.put(boothId, detailMap);
						}
					}
					// boothInvoice total map prapred Here
					if (boothInvTotalMap.containsKey(boothId)) {
						BigDecimal tempTotAmount = (BigDecimal) boothInvTotalMap.get(boothId);
						tempTotAmount = tempTotAmount.add(boothInvTotal);
						boothInvTotalMap.put(boothId, tempTotAmount);
					} else {
						boothInvTotalMap.put(boothId, boothInvTotal);
					}
				}
				dayWisePartyInvoiceDetail.put(dayStart, invDetail);
			}
		} catch (Exception e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("dayWisePartyInvoiceDetail", dayWisePartyInvoiceDetail);
		result.put("boothInvoiceTotalMap", boothInvTotalMap);
		return result;
	}

	public static Map getByProductDaywisePenaltyTotals(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List facilityList = (List) context.get("facilityList");
		boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}
		Map result = FastMap.newInstance();
		List boothsList = FastList.newInstance();
		if (UtilValidate.isEmpty(facilityList)) {
			boothsList = (List) getAllBooths(delegator, null).get("boothsList");
		} 
		else {
			boothsList.addAll(facilityList);
		}
		Map daywiseBoothTotals = FastMap.newInstance();
		int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1);
		List<GenericValue> invoices = null;
		Map facilityPenalty = FastMap.newInstance();
		Map penaltyPaymentReferences = FastMap.newInstance();
		try {
			List returnChequeExpr = FastList.newInstance();
			returnChequeExpr.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "PMNT_VOID"));
			returnChequeExpr.add(EntityCondition.makeCondition("chequeReturns",	EntityOperator.EQUALS, "Y"));
			returnChequeExpr.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CHEQUE_PAYIN"));
			EntityCondition cond = EntityCondition.makeCondition(returnChequeExpr, EntityOperator.AND);
			List<GenericValue> returnPayments = delegator.findList("Payment",cond,UtilMisc.toSet("paymentId", "paymentRefNum", "amount"),null, null, false);
			if (UtilValidate.isNotEmpty(returnPayments)) {
				for (GenericValue returns : returnPayments) {
					Map tempMap = FastMap.newInstance();
					String paymentId = returns.getString("paymentId");
					String paymentRefNum = returns.getString("paymentRefNum");
					BigDecimal amount = (BigDecimal) returns.get("amount");
					tempMap.put("referenceNum", paymentRefNum);
					tempMap.put("amount", amount);
					penaltyPaymentReferences.put(paymentId, tempMap);
				}
			}

			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS, "MIS_INCOME_IN"));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityOperator.EQUALS, "INCO_FINEPENALTY_CHQ"));
			conditionList.add(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conditionList.add(EntityCondition.makeCondition("dueDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			invoices = delegator.findList("InvoiceAndItemType", condition, UtilMisc.toSet("invoiceId", "dueDate", "facilityId","referenceNumber", "partyId"), null, null, false);
			
			if (UtilValidate.isNotEmpty(invoices)) {
				List facilities = FastList.newInstance();
				if (isByParty) {
					facilities = (List) EntityUtil.getFieldListFromEntityList(invoices, "partyId", true);
				} else {
					facilities = (List) EntityUtil.getFieldListFromEntityList(invoices, "facilityId", true);
				}
				if (UtilValidate.isNotEmpty(facilities)) {
					for (int i = 0; i < facilities.size(); i++) {
						String facilityId = (String) facilities.get(i);
						Map dayWisePenalty = FastMap.newInstance();
						List<GenericValue> facilityInvoices = FastList.newInstance();
						if (isByParty) {
							facilityInvoices = EntityUtil.filterByCondition(invoices, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,facilityId));
						} else {
							facilityInvoices = EntityUtil.filterByCondition(invoices, EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
						}
						Map dayPenalty = FastMap.newInstance();
						if (UtilValidate.isNotEmpty(facilityInvoices)) {
							for (int k = 0; k < intervalDays; k++) {
								Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
								Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
								Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
								List dayCond = FastList.newInstance();
								dayCond.add(EntityCondition.makeCondition("dueDate",EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
								dayCond.add(EntityCondition.makeCondition("dueDate",EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
								EntityCondition custExpr = EntityCondition.makeCondition(dayCond, EntityOperator.AND);
								List<GenericValue> dayPartyPenalty = (List) EntityUtil.filterByCondition(facilityInvoices, custExpr);
								List invoiceDetail = FastList.newInstance();
								if (UtilValidate.isNotEmpty(dayPartyPenalty)) {
									for (int j = 0; j < dayPartyPenalty.size(); j++) {
										Map invoiceMap = FastMap.newInstance();
										GenericValue eachPartyPenalty = dayPartyPenalty.get(j);
										String invoiceId = eachPartyPenalty.getString("invoiceId");
										String paymentRefNum = eachPartyPenalty.getString("referenceNumber");
										BigDecimal amount = InvoiceWorker.getInvoiceTotal(delegator,invoiceId);
										invoiceMap.put("amount", amount);
										invoiceMap.put("paymentId",	paymentRefNum);
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
		} catch (Exception e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("returnPaymentReferences", penaltyPaymentReferences);
		result.put("facilityPenalty", facilityPenalty);
		return result;
	}

	public static Map getChequePenaltyTotals(DispatchContext dctx,Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map result = FastMap.newInstance();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List facilityList = (List) context.get("facilityList");
		String dateType = (String) context.get("dateType");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List boothsList = FastList.newInstance();
		if (UtilValidate.isEmpty(facilityList)) {
			boothsList = (List) getAllBooths(delegator, null).get("boothsList");
		} 
		else {
			boothsList.addAll(facilityList);
		}
		Map daywiseBoothTotals = FastMap.newInstance();
		int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate) + 1);
		List<GenericValue> invoices = null;
		Map penaltyPaymentReferences = FastMap.newInstance();
		Map facilityPenalty = FastMap.newInstance();
		Map facilityPenaltyDayWise = FastMap.newInstance();
		List chequeReturnDetails = FastList.newInstance();
		Map facilityPenaltyPaymentIdsMap = FastMap.newInstance();
		try {
			List returnChequeExpr = FastList.newInstance();
			returnChequeExpr.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "PMNT_VOID"));
			returnChequeExpr.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CHEQUE_PAYIN"));
			returnChequeExpr.add(EntityCondition.makeCondition("chequeReturns",	EntityOperator.EQUALS, "Y"));
			EntityCondition cond = EntityCondition.makeCondition(returnChequeExpr, EntityOperator.AND);
			List<GenericValue> returnPayments = delegator.findList("Payment",cond, null, UtilMisc.toList("facilityId"), null, false);
			List canceldPaymentIds = (List) EntityUtil.getFieldListFromEntityList(returnPayments, "paymentId",true);

			List customCondList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(dateType)&& dateType.equals("BOUNCE_DATE")) {
				customCondList.add(EntityCondition.makeCondition("cancelDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				customCondList.add(EntityCondition.makeCondition("cancelDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));

			} else {
				customCondList.add(EntityCondition.makeCondition("paymentDate",	EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				customCondList.add(EntityCondition.makeCondition("paymentDate",	EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			}
			EntityCondition retCond = EntityCondition.makeCondition(customCondList, EntityOperator.AND);
			List<GenericValue> returnChequeList = EntityUtil.filterByCondition(returnPayments, retCond);

			if (UtilValidate.isNotEmpty(returnChequeList)) {
				for (GenericValue returns : returnChequeList) {
					Map detailMap = FastMap.newInstance();
					detailMap.put("referenceNum",returns.getString("paymentRefNum"));
					detailMap.put("facilityId", returns.getString("facilityId"));
					detailMap.put("paymentId", returns.getString("paymentId"));
					detailMap.put("paymentDate",returns.getTimestamp("paymentDate"));
					detailMap.put("comments", returns.getString("comments"));
					detailMap.put("issuingAuthority",returns.getString("issuingAuthority"));
					detailMap.put("cancelDate",	returns.getTimestamp("cancelDate"));
					detailMap.put("amount", returns.getBigDecimal("amount"));
					chequeReturnDetails.add(detailMap);
				}
			}

			if (UtilValidate.isNotEmpty(returnPayments)) {
				for (GenericValue returns : returnPayments) {
					Map tempMap = FastMap.newInstance();
					String paymentId = returns.getString("paymentId");
					String paymentRefNum = returns.getString("paymentRefNum");
					BigDecimal amount = (BigDecimal) returns.get("amount");
					tempMap.put("referenceNum", paymentRefNum);
					tempMap.put("amount", amount);
					penaltyPaymentReferences.put(paymentId, tempMap);
				}
			}

			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS, "MIS_INCOME_IN"));
			conditionList.add(EntityCondition.makeCondition("referenceNumber",EntityOperator.IN, canceldPaymentIds));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditionList.add(EntityCondition.makeCondition("statusId",	EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			invoices = delegator.findList("Invoice", condition, UtilMisc.toSet("invoiceId", "invoiceDate", "facilityId","referenceNumber"), null, null, false);

			if (UtilValidate.isNotEmpty(invoices)) {
				List facilities = (List) EntityUtil.getFieldListFromEntityList(invoices, "facilityId", true);
				if (UtilValidate.isNotEmpty(facilities)) {
					for (int i = 0; i < facilities.size(); i++) {
						String facilityId = (String) facilities.get(i);
						BigDecimal totalAmount = BigDecimal.ZERO;
						List facilityPenaltyPaymentIds = FastList.newInstance();
						Map dayWisePenalty = FastMap.newInstance();
						List<GenericValue> facilityInvoices = EntityUtil.filterByCondition(invoices, EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
						Map dayPenalty = FastMap.newInstance();
						if (UtilValidate.isNotEmpty(facilityInvoices)) {
							for (int k = 0; k < intervalDays; k++) {
								Timestamp supplyDate = UtilDateTime.addDaysToTimestamp(fromDate, k);
								Timestamp dayStart = UtilDateTime.getDayStart(supplyDate);
								Timestamp dayEnd = UtilDateTime.getDayEnd(supplyDate);
								List dayCond = FastList.newInstance();
								dayCond.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
								dayCond.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
								List<GenericValue> dayPartyPenalty = (List) EntityUtil.filterByCondition(facilityInvoices,EntityCondition.makeCondition(dayCond));
								List invoiceDetail = FastList.newInstance();
								
								if (UtilValidate.isNotEmpty(dayPartyPenalty)) {
									for (int j = 0; j < dayPartyPenalty.size(); j++) {
										Map invoiceMap = FastMap.newInstance();
										GenericValue eachPartyPenalty = dayPartyPenalty.get(j);
										String invoiceId = eachPartyPenalty.getString("invoiceId");
										String paymentRefNum = eachPartyPenalty.getString("referenceNumber");
										BigDecimal amount = InvoiceWorker.getInvoiceTotal(delegator,invoiceId);
										totalAmount = totalAmount.add(amount);
										invoiceMap.put("amount", amount);
										invoiceMap.put("paymentId",	paymentRefNum);
										invoiceDetail.add(invoiceMap);
										facilityPenaltyPaymentIds.add(paymentRefNum);
									}
									dayPenalty.put(dayStart, invoiceDetail);
								}
							}
						}
						facilityPenaltyDayWise.put(facilityId, dayPenalty);
						facilityPenalty.put(facilityId, totalAmount);
						facilityPenaltyPaymentIdsMap.put(facilityId,facilityPenaltyPaymentIds);
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("chequeReturnDetails", chequeReturnDetails);
		result.put("returnPaymentReferences", penaltyPaymentReferences);
		result.put("facilityPenaltyDayWise", facilityPenaltyDayWise);
		result.put("facilityPenalty", facilityPenalty);
		result.put("facilityPenaltyPaymentIdsMap", facilityPenaltyPaymentIdsMap);
		return result;
	}

	public static Map<String, Object> getItemIssuenceForShipments(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List<String> shipmentIds = (List<String>) context.get("shipmentIds");
		Map result = ServiceUtil.returnSuccess();
		Map resultMap = FastMap.newInstance();
		List conditionList = FastList.newInstance();
		Map issuanceProductTotals = FastMap.newInstance();
		Map failedProductItemsMap = FastMap.newInstance();
		try {
			// get Issueance Details
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipmentIds));
			EntityCondition itemIssueanceCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", itemIssueanceCond, null, null, null, false);
			// List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", itemIssueanceCond, null, UtilMisc.toList("productId"), false);
			for (GenericValue itemIssueance : itemIssuanceList) {
				BigDecimal quantity = itemIssueance.getBigDecimal("quantity");
				String issueProductId = itemIssueance.getString("productId");
				if (quantity.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal tempQuantity = BigDecimal.ZERO;
					if (UtilValidate.isEmpty(issuanceProductTotals.get(issueProductId))) {
						// issuanceProductTotals.put(issueProductId,quantity);
						tempQuantity = quantity;
					} else {
						tempQuantity = tempQuantity.add((BigDecimal) issuanceProductTotals.get(issueProductId));

					}
					issuanceProductTotals.put(issueProductId, tempQuantity);
				}
			}// for close
		} catch (Exception e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		resultMap.put("issuanceProductTotals", issuanceProductTotals);
		resultMap.put("crateTotal", issuanceProductTotals.get("CRATE"));
		resultMap.put("canTotal", issuanceProductTotals.get("CAN"));
		return resultMap;
	}

	public static Map<String, Object> getFacilityFinAccountInfo(DispatchContext dctx, Map context) {

		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		String finAccountName = (String) context.get("finAccountName");
		String finAccountCode = (String) context.get("finAccountCode");
		Map accountInfo = FastMap.newInstance();
		Map accountNameMap = FastMap.newInstance();
		try {

			List<GenericValue> facilityFinAccounts = null;
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("finAccountTypeId",EntityOperator.EQUALS, "BANK_ACCOUNT"));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"FNACT_ACTIVE"), EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));
			if (UtilValidate.isNotEmpty(facilityId)) {
				conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
			}
			if (UtilValidate.isNotEmpty(finAccountName)) {
				conditionList.add(EntityCondition.makeCondition("finAccountName", EntityOperator.EQUALS,finAccountName));
			}
			if (UtilValidate.isNotEmpty(finAccountCode)) {
				conditionList.add(EntityCondition.makeCondition("finAccountCode", EntityOperator.EQUALS,finAccountCode));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			facilityFinAccounts = delegator.findList("FacilityAndFinAccount",condition, null, null, null, false);
			facilityFinAccounts = EntityUtil.filterByDate(facilityFinAccounts);
			if (UtilValidate.isEmpty(facilityFinAccounts)) {
				Debug.logError("No Financial Accounts available for the Facility:"+ facilityId, module);
				accountInfo.put("facilityId", facilityId);
				result.put("accountInfo", accountInfo);
				return ServiceUtil.returnSuccess();
			}

			GenericValue finAccountDetail = EntityUtil.getFirst(facilityFinAccounts);
			accountInfo.put("finAccountName", "");
			accountInfo.put("finAccountBranch", "");
			accountInfo.put("facilityId", facilityId);
			if (UtilValidate.isNotEmpty(finAccountDetail.getString("finAccountName"))) {
				accountInfo.put("finAccountName",finAccountDetail.getString("finAccountName"));
			}
			if (UtilValidate.isNotEmpty(finAccountDetail.getString("finAccountBranch"))) {
				accountInfo.put("finAccountBranch",finAccountDetail.getString("finAccountBranch"));
			}
			accountInfo.put("finAccountId",	finAccountDetail.getString("finAccountId"));
			accountInfo.put("finAccountCode",finAccountDetail.getString("finAccountCode"));

			List facilityIds = EntityUtil.getFieldListFromEntityList(facilityFinAccounts, "facilityId", true);
			List accountNames = EntityUtil.getFieldListFromEntityList(facilityFinAccounts, "finAccountName", true);
			List accountNameList = FastList.newInstance();
			for (int i = 0; i < accountNames.size(); i++) {
				accountNameList.add(UtilMisc.toMap("finAccountName",accountNames.get(i)));
			}
			result.put("facilityIds", facilityIds);
			result.put("accountNameList", accountNameList);
		} catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("accountInfo", accountInfo);

		return result;
	}

	public static Map<String, Object> getShopeeRentAmount(DispatchContext dctx,Map<String, ? extends Object> context) {

		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp estimatedShipDate = (Timestamp) context.get("estimatedDeliveryDate");
		List conditionList = FastList.newInstance();
		List<GenericValue> custTimePeriodList = FastList.newInstance();
		Timestamp pMonthStart = null;
		Timestamp pMonthEnd = null;
		String customTimePeriodId = "";
		List<String> periodBillingIds = FastList.newInstance();
		try {
			Timestamp newDate = UtilDateTime.getMonthStart(estimatedShipDate);
			pMonthStart = UtilDateTime.getMonthStart(UtilDateTime.addDaysToTimestamp(newDate, -1));
			pMonthEnd = UtilDateTime.getMonthEnd(UtilDateTime.addDaysToTimestamp(newDate, -1),TimeZone.getDefault(), Locale.getDefault());
			Map<String, Object> resultMap = dispatcher.runSync("getCustomTimePeriodId", UtilMisc.toMap("periodTypeId","SALES_MONTH", "fromDate", pMonthStart, "thruDate",pMonthEnd, "userLogin", userLogin));
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError("Error getting Custom Time Period", module);
				return ServiceUtil.returnError("Error getting Custom Time Period");
			}
			customTimePeriodId = (String) resultMap.get("customTimePeriodId");
			Map<String, Object> resultMaplst = dispatcher.runSync("getPeriodBillingList", UtilMisc.toMap("billingTypeId","SHOPEE_RENT", "customTimePeriodId",customTimePeriodId, "statusId", "COM_CANCELLED","userLogin", userLogin));
			List<GenericValue> periodBillingList = (List<GenericValue>) resultMaplst.get("periodBillingList");
			if (UtilValidate.isEmpty(periodBillingList)) {
				Debug.logError("Error getting PeriodBilling", module);
				return ServiceUtil.returnError("Error getting Period Billing");
			}
			periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
			List<GenericValue> invoiceList;
			List<GenericValue> InvoiceItemList;
			List<GenericValue> InvoiceItemAmount;
			String facilityId;
			String invoiceId;
			List<GenericValue> rateAmounts;
			Map<String, Map> amountMap = FastMap.newInstance();
			Map<String, Object> rateAmount = FastMap.newInstance();
			try {
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("periodBillingId",EntityOperator.IN, periodBillingIds));
				EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				invoiceList = delegator.findList("Invoice", cond, null, null,null, false);
				for (GenericValue invoice : invoiceList) {
					facilityId = invoice.getString("facilityId");
					invoiceId = invoice.getString("invoiceId");
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					try {
						InvoiceItemList = delegator.findList("InvoiceItem",	condition, null, null, null, false);
						for (GenericValue InvoiceItemAmountval : InvoiceItemList) {
							InvoiceItemAmountval.getString("invoiceItemTypeId");
							if (InvoiceItemAmountval.getString("invoiceItemTypeId").equals("SHOPEE_RENT")) {
								rateAmount.put("rentAmount",InvoiceItemAmountval.getBigDecimal("amount"));
							}
							rateAmount.put("tax", InvoiceItemAmountval.getBigDecimal("amount"));
						}
					} catch (GenericEntityException e) {
						Debug.logError(e, "Error in getting Invoice Item",module);
						return ServiceUtil.returnError("Error in getting Invoice Item");
					}
					amountMap.put(facilityId, rateAmount);

				}
				result.put("amountMap", amountMap);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Error getting Invoice Ids", module);
				return ServiceUtil.returnError("Error getting Invoice Ids ");
			}

		} catch (Exception e) {
			Debug.logError(e, "Error in getShopeeRentAmount");
			return ServiceUtil.returnError("Error in getShopeeRentAmount " + e);
		}
		return result;
	}

	public static Map<String, Object> createAdhocSalePayment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		String invoiceId = (String) context.get("invoiceId");
		String orderId = (String) context.get("orderId");
		BigDecimal paymentAmount = ProductEvents.parseBigDecimalForEntity((String) context.get("amount"));
		Locale locale = (Locale) context.get("locale");
		String paymentMethodType = (String) context.get("paymentMethodTypeId");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		boolean useFifo = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("useFifo"))) {
			useFifo = (Boolean) context.get("useFifo");
		}
		String paymentType = "SALES_PAYIN";
		String partyIdTo = "Company";
		String partyIdFrom = "";
		String paymentId = "";
		boolean roundingAdjustmentFlag = Boolean.TRUE;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List exprListForParameters = FastList.newInstance();
		List boothOrdersList = FastList.newInstance();
		Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();

		Timestamp instrumentDate = UtilDateTime.nowTimestamp();

		try {
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId), true);
			if (UtilValidate.isEmpty(facility)) {
				Debug.logError("Booth doesn't exists with Id: " + facilityId,module);
				return ServiceUtil.returnError("Booth doesn't exists with Id: "	+ facilityId);
			}
			if (UtilValidate.isNotEmpty(facility)) {
				partyIdFrom = facility.getString("ownerPartyId");
			}
			Map<String, Object> paymentCtx = UtilMisc.<String, Object> toMap("paymentTypeId", paymentType);

			paymentCtx.put("paymentMethodTypeId", "CASH_PAYIN");
			paymentCtx.put("organizationPartyId", partyIdTo);
			paymentCtx.put("partyId", partyIdFrom);
			paymentCtx.put("facilityId", facilityId);
			paymentCtx.put("paymentPurposeType", "ROUTE_MKTG");
			/*if (!UtilValidate.isEmpty(paymentLocationId) ) {
            paymentCtx.put("paymentLocationId", paymentLocationId);                        	
        }   */         
      /*  if (!UtilValidate.isEmpty(paymentRefNum) ) {
            paymentCtx.put("paymentRefNum", paymentRefNum);                        	
        }*/
			// paymentCtx.put("issuingAuthority", issuingAuthority);
			// paymentCtx.put("issuingAuthorityBranch", issuingAuthorityBranch);
			paymentCtx.put("instrumentDate", instrumentDate);

			paymentCtx.put("statusId", "PMNT_RECEIVED");
			paymentCtx.put("isEnableAcctg", "N");
			paymentCtx.put("amount", paymentAmount);
			paymentCtx.put("userLogin", userLogin);
			paymentCtx.put("invoices", UtilMisc.toList(invoiceId));

			Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
			if (ServiceUtil.isError(paymentResult)) {
				Debug.logError(paymentResult.toString(), module);
				return ServiceUtil.returnError(null, null, null, paymentResult);
			}
			paymentId = (String) paymentResult.get("paymentId");
		} catch (Exception e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result = ServiceUtil.returnSuccess("Payment successfully done for Party "+ facilityId + " ..!");
		return result;
	}

	public static Map<String, Object> getAdhocSalePayments(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Timestamp estimatedShipDate = (Timestamp) context.get("estimatedShipDate");

		List adhocShipmentIds = getShipmentIds(delegator,UtilDateTime.toDateString(estimatedShipDate,"yyyy-MM-dd HH:mm:ss"), "RM_DIRECT_SHIPMENT", null);
		Map adhocBoothPaymentMap = FastMap.newInstance();
		BigDecimal totalPaidAmount = BigDecimal.ZERO;
		List paymentIds = FastList.newInstance();
		try {
			List<GenericValue> orderHeaderFacInvList = delegator.findList("OrderHeaderFacAndItemBillingInv",EntityCondition.makeCondition("shipmentId",EntityOperator.IN, adhocShipmentIds),UtilMisc.toSet("originFacilityId", "invoiceId"),null, null, false);
			if (!UtilValidate.isEmpty(orderHeaderFacInvList)) {
				List invoiceIds = EntityUtil.getFieldListFromEntityList(orderHeaderFacInvList, "invoiceId", false);
				List facilityIds = EntityUtil.getFieldListFromEntityList(orderHeaderFacInvList, "originFacilityId", false);
				List<GenericValue> paymentAppList = delegator.findList("PaymentAndApplication", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds),UtilMisc.toSet("paymentId", "facilityId", "amount"),null, null, false);
				paymentIds = EntityUtil.getFieldListFromEntityList(paymentAppList, "paymentId", false);

				//List<GenericValue> paymentsList = delegator.findList("PaymentAndFacility", EntityCondition.makeCondition("paymentId", EntityOperator.IN, paymentIds), null, null, null, false);
				for (int i = 0; i < paymentAppList.size(); i++) {
					GenericValue boothPayment = (GenericValue) paymentAppList.get(i);
					String facilityId = boothPayment.getString("facilityId");
					if (facilityId != null) {
						if (adhocBoothPaymentMap.get(facilityId) == null) {
							adhocBoothPaymentMap.put(facilityId,boothPayment.getBigDecimal("amount"));
							totalPaidAmount = totalPaidAmount.add(boothPayment.getBigDecimal("amount"));
						} else {
							BigDecimal amount = (BigDecimal) adhocBoothPaymentMap.get(facilityId);
							adhocBoothPaymentMap.put(facilityId, amount.add(boothPayment.getBigDecimal("amount")));
							totalPaidAmount = totalPaidAmount.add(boothPayment.getBigDecimal("amount"));
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("paymentIds", paymentIds);
		result.put("adhocBoothPaidMap", adhocBoothPaymentMap);
		result.put("totalPaidAmount", totalPaidAmount);
		return result;
	}
	
	
	public static Map<String, Object> updateReturnsForPeriod(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		Locale locale = (Locale) context.get("locale");
		List<String> shipmentIds = FastList.newInstance();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<String> returnIdList = FastList.newInstance();
		String productStoreId = "";
		try {
			productStoreId = (String) ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
			shipmentIds = ByProductNetworkServices.getShipmentIds(delegator,fromDate, thruDate);
			if (UtilValidate.isNotEmpty(shipmentIds)) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, shipmentIds));
				conditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS,"RETURN_ACCEPTED"));
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> returnHeaderItemList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", condition,null, null, null, false);
				if (UtilValidate.isNotEmpty(returnHeaderItemList)) {
					returnIdList = EntityUtil.getFieldListFromEntityList(returnHeaderItemList, "returnId", true);
				}
				for (int i = 0; i < returnIdList.size(); i++) {
					String returnId = (String) returnIdList.get(i);
					List<GenericValue> returnItemList = EntityUtil.filterByCondition(returnHeaderItemList,EntityCondition.makeCondition("returnId",EntityOperator.EQUALS, returnId));
					for (GenericValue eachReturnItem : returnItemList) {
						Map<String, Object> priceContext = FastMap.newInstance();
						priceContext.put("userLogin", userLogin);
						priceContext.put("productStoreId", productStoreId);
						priceContext.put("productId",eachReturnItem.get("productId"));
						priceContext.put("partyId",	eachReturnItem.get("ownerPartyId"));
						priceContext.put("facilityId",eachReturnItem.get("originFacilityId"));
						priceContext.put("priceDate", eachReturnItem.getTimestamp("estimatedShipDate"));
						Map priceResult = ByProductServices.calculateByProductsPrice(delegator,dispatcher, priceContext);
						if (ServiceUtil.isError(priceResult)) {
							Debug.logError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult),module);
							return ServiceUtil.returnError("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult));
						}
						BigDecimal totalPrice = (BigDecimal) priceResult.get("totalPrice");
						BigDecimal basicPrice = (BigDecimal) priceResult.get("basicPrice");
						List taxList = (List) priceResult.get("taxList");
						BigDecimal vatPercent = BigDecimal.ZERO;
						BigDecimal vatAmount = BigDecimal.ZERO;
						for (int m = 0; m < taxList.size(); m++) {
							Map taxComp = (Map) taxList.get(m);
							String taxType = (String) taxComp.get("taxType");
							BigDecimal percentage = (BigDecimal) taxComp.get("percentage");
							BigDecimal amount = (BigDecimal) taxComp.get("amount");
							if (taxType.startsWith("VAT_")) {
								vatPercent = percentage;
								vatAmount = amount;
							}
						}
						GenericValue returnItem = delegator.findOne("ReturnItem", UtilMisc.toMap("returnId",eachReturnItem.getString("returnId"),"returnItemSeqId", eachReturnItem.getString("returnItemSeqId")),false);
						returnItem.set("returnPrice", totalPrice);
						returnItem.set("returnBasicPrice", basicPrice);
						returnItem.set("vatPercent", vatPercent);
						returnItem.set("vatAmount", vatAmount);
						returnItem.store();
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			Debug.logError(e, "Problem in updating ReturnItem", module);
			return ServiceUtil.returnError("Problem in updating ReturnItem");
		}
		return result;
	}

	public static Map<String, Object> getFacilityPartyContractor(DispatchContext dctx, Map<String, ? extends Object> context) {

		Map<String, Object> result = FastMap.newInstance();
		List conditionList = FastList.newInstance();
		List<String> boothIds = FastList.newInstance();
		List<GenericValue> facilityPartyList = null;
		Timestamp saleDate = UtilDateTime.nowTimestamp();
		Delegator delegator = dctx.getDelegator();
		Map<String, String> facilityPartyMap = FastMap.newInstance();
		Map<String, List> partyAndFacilityList = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(context.get("saleDate"))) {
			saleDate = (Timestamp) context.get("saleDate");
		}
		String roleTypeId = (String) context.get("roleTypeId");
		String facilityId = (String) context.get("facilityId");
		if (UtilValidate.isEmpty(roleTypeId)) {
			conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, "Contractor"));
		}
		if (UtilValidate.isNotEmpty(roleTypeId)) {
			conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS, roleTypeId));
		}
		if (UtilValidate.isNotEmpty(facilityId)) {
			conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
		}

		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try {
			facilityPartyList = delegator.findList("FacilityFacilityPartyAndPerson", condition, null, null,null, false);
			facilityPartyList = EntityUtil.filterByDate(facilityPartyList,saleDate);
			for (GenericValue facilityParty : facilityPartyList) {
				List tempFacilityList = FastList.newInstance();
				String routeId = (String) facilityParty.getString("facilityId");
				if (facilityParty.getString("facilityTypeId").equals("ROUTE")) {
					facilityPartyMap.put(facilityParty.getString("facilityId"),	facilityParty.getString("partyId"));
				}
				if (UtilValidate.isNotEmpty(partyAndFacilityList.get(facilityParty.getString("partyId")))) {
					List facilityList = partyAndFacilityList.get(facilityParty.getString("partyId"));
					facilityList.add(routeId);
					partyAndFacilityList.put(facilityParty.getString("partyId"), facilityList);
				} else {
					partyAndFacilityList.put(facilityParty.getString("partyId"),UtilMisc.toList(routeId));
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception while getting FaclityParty ", module);
		}
		result.put("facilityPartyMap", facilityPartyMap);
		result.put("partyAndFacilityList", partyAndFacilityList);
		return result;
	}

	/**
	 * This is a batch service that settles cash orders by creating a payment and 
		     * applying it to the card order invoice.  
		     * NOTE:: This is a temporary service until payments integration is completed
		     * @param ctx the dispatch context
	 * @param context
	 * @return result map
	 */
	public static Map<String, Object> batchSettleLMSCashOrders(DispatchContext dctx, Map<String, Object> context) {
		context.put("paymentMethodType", "VBIZ_PAYIN");
		return batchSettleLMSOrders(dctx, context);
	}

	/**
	 * This is a batch service that settles card orders by creating a payment and 
		     * applying it to the card order invoice.  Cards orders are actually advance pay orders 
		     * but for efficiency/simplicity we auto create payments after order generation
		     * @param ctx the dispatch context
	 * @param context
	 * @return result map
	 *//*
	 public static Map<String, Object> batchSettleLMSCardOrders(DispatchContext dctx, Map<String, Object> context){
		    	context.put("productSubscriptionTypeId", "CARD");
		    	return batchSettleLMSOrders(dctx, context);
		    }

	 *//**
	  * This is a batch service that settles card orders by creating a payment and 
		     * applying it to the card order invoice.  Cards orders are actually advance pay orders 
		     * but for efficiency/simplicity we auto create payments after order generation
		     * @param ctx the dispatch context
	 * @param context
	 * @return result map
	 *//*
	public static Map<String, Object> batchSettleLMSSpecialOrders(DispatchContext dctx, Map<String, Object> context){
		    	context.put("productSubscriptionTypeId", "SPECIAL_ORDER");
		    	return batchSettleLMSOrders(dctx, context);
		    }
	 *//**
	 * This is a batch service that settles orders by creating a payment and
	  * applying it to the order invoice. 
		     * Note: Only VENDOR and PTC booths will be handled in this method
		     * @param ctx the dispatch context
	 * @param context
	 * @return result map
	 */

	/*public static Map<String, Object> batchSettleLMSOrders(DispatchContext dctx, Map<String, Object> context){
    Delegator delegator = dctx.getDelegator();
    LocalDispatcher dispatcher = dctx.getDispatcher();
    String shipmentTypeId= (String) context.get("shipmentTypeId");
    String paymentType = "SALES_PAYIN";
    String paymentMethodType = (String) context.get("paymentMethodType");
    if (UtilValidate.isEmpty(paymentMethodType)) {
        paymentMethodType = "CASH_PAYIN";           	
    }
    Timestamp startDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date)context.get("startDate")).getTime()));
    Timestamp endDate = UtilDateTime.getDayEnd(UtilDateTime.getTimestamp(((java.sql.Date)context.get("endDate")).getTime()));
    String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
	 if (UtilValidate.isEmpty(productSubscriptionTypeId)) {
		productSubscriptionTypeId= "productSubscriptionTypeId";         	
        }
   List shipmentIds=getShipmentIdsByType(delegator,startDate,endDate,shipmentTypeId);
   
   Debug.log("====shipmentIds===="+shipmentIds);
    String partyIdTo ="Company";        
  
    Debug.log("startDate=" + startDate + "; =endDate=" + endDate+"==shipmentTypeId=="+shipmentTypeId+"===productSubscriptionTypeId=="+productSubscriptionTypeId);
    if (UtilValidate.isEmpty(productSubscriptionTypeId)) {
		Debug.logError("Product Subscription Type Id cannot be null", module);	
        return ServiceUtil.returnError("Product Subscription Type Id cannot be null");	        	
    }
    Map<String, Object> result = new HashMap<String, Object>();
    int orderCounter = 0;        
    Timestamp startTimestamp = UtilDateTime.nowTimestamp();
    double elapsedSeconds;        
    GenericValue userLogin = null;
    try {
        userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
    } catch (GenericEntityException e) {
		Debug.logError(e, module);	
        return ServiceUtil.returnError(e.toString());	
    }        
    List exprListForParameters = FastList.newInstance();
    Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
	List invoiceStatusList=FastList.newInstance();
	invoiceStatusList = UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF");  
	
	
	exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_IN, invoiceStatusList));
	//exprListForParameters.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, UtilMisc.toList("VENDOR","PTC")));
	exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
	if (!UtilValidate.isEmpty(startDate)) {
		exprListForParameters.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));			
	}
	if (!UtilValidate.isEmpty(endDate)) {
		exprListForParameters.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));			
	}		
	EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
	EntityFindOptions findOptions = new EntityFindOptions();
	findOptions.setDistinct(true);
    List boothOrdersList = FastList.newInstance();		
	try {
		boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, null,UtilMisc.toList("estimatedDeliveryDate"), findOptions,false);
		Set invoiceIdSet = new HashSet(EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId",true));
		List invoiceIdList = new ArrayList(invoiceIdSet);
		// Settle each of the card invoices
		
		for (int i = 0; i < invoiceIdList.size(); i++) {
    		orderCounter++;
    		if ((orderCounter % 200) == 0) {
    			elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
    			Debug.logImportant("Completed " + orderCounter + " orders [ in " + elapsedSeconds + " seconds]", module);
    		}				
			String invoiceId = (String) invoiceIdList.get(i);
			List paymentApplicationList =FastList.newInstance();
			Map invoicePaymentInfoMap =FastMap.newInstance();
			BigDecimal outstandingAmount =BigDecimal.ZERO;				
			invoicePaymentInfoMap.put("invoiceId", invoiceId);
			invoicePaymentInfoMap.put("userLogin",userLogin);
			Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", invoicePaymentInfoMap);
			if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
				Debug.logError(getInvoicePaymentInfoListResult.toString(), module);    			
		        return ServiceUtil.returnError(null, null, null, getInvoicePaymentInfoListResult);
		    }
			Map invoicePaymentInfo = (Map)((List)getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
			outstandingAmount = (BigDecimal)invoicePaymentInfo.get("outstandingAmount");				
			Map<String, Object> paymentCtx = UtilMisc.<String, Object> toMap("paymentTypeId", paymentType);
			paymentCtx.put("paymentMethodTypeId", paymentMethodType);
			GenericValue facility = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			String partyIdFrom = (String) facility.getString("partyId");
			paymentCtx.put("partyIdTo", partyIdTo);
			paymentCtx.put("partyIdFrom", partyIdFrom);
			paymentCtx.put("statusId", "PMNT_RECEIVED");
			paymentCtx.put("amount", outstandingAmount);
			paymentCtx.put("userLogin", userLogin);
			paymentCtx.put("isEnableAcctg", "N");
			Map<String, Object> paymentResult = dispatcher.runSync("createPayment", paymentCtx);
			if (ServiceUtil.isError(paymentResult)) {
				return ServiceUtil.returnError(null, null, null,
						paymentResult);
			}
			String paymentId = (String) paymentResult.get("paymentId");
			Map<String, Object> invoiceCtx = UtilMisc.<String, Object> toMap("invoiceId", invoiceId);
			invoiceCtx.put("userLogin", userLogin);
			invoiceCtx.put("statusId", "INVOICE_READY");
			try {
				Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus", invoiceCtx);
				if (ServiceUtil.isError(invoiceResult)) {
					return ServiceUtil.returnError(null, null, null,
							invoiceResult);
				}

			} catch (GenericServiceException e) {
				Debug.logError(e, e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}

			Map<String, Object> invoiceApplCtx = UtilMisc.<String, Object> toMap("invoiceId", invoiceId);
			invoiceApplCtx.put("userLogin", userLogin);
			invoiceApplCtx.put("paymentId", paymentId);
			invoiceApplCtx.put("amountApplied", outstandingAmount);
			Map<String, Object> invoiceApplResult = dispatcher.runSync("createPaymentApplication", invoiceApplCtx);
			if (ServiceUtil.isError(invoiceApplResult)) {
				return ServiceUtil.returnError(null, null, null,
						invoiceApplResult);
			}
		}
	} catch (GenericServiceException e) {
		Debug.logError(e, e.toString(), module);
		return ServiceUtil.returnError(e.toString());
	} catch (GenericEntityException e) {
		Debug.logError(e, module);
		return ServiceUtil.returnError(e.toString());
	}
	elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
	Debug.logImportant("Completed " + orderCounter + " orders [ in " + elapsedSeconds + " seconds]", module);		
	return ServiceUtil.returnSuccess();
    
}*/
	public static Map<String, Object> batchSettleLMSOrders(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String paymentType = "SALES_PAYIN";
		String paymentMethodType = (String) context.get("paymentMethodType");
		Timestamp startDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("startDate")).getTime()));
		Timestamp endDate = UtilDateTime.getDayEnd(UtilDateTime.getTimestamp(((java.sql.Date) context.get("endDate")).getTime()));
		String productSubscriptionTypeId = (String) context.get("productSubscriptionTypeId");
		Map<String, Object> result = ServiceUtil.returnSuccess("Service runs Succesfully");
		if (UtilValidate.isEmpty(productSubscriptionTypeId)) {
			productSubscriptionTypeId = "productSubscriptionTypeId";
		}
		String partyIdTo = "Company";

		Debug.log("startDate=" + startDate + "; =endDate=" + endDate+ "===productSubscriptionTypeId==" + productSubscriptionTypeId);

		List facilityIds = (List) getAllBooths(delegator, null).get("boothsList");

		// Map<String, Object> result = new HashMap<String, Object>();
		int orderCounter = 0;
		Timestamp startTimestamp = UtilDateTime.nowTimestamp();
		double elapsedSeconds;
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findByPrimaryKey("UserLogin",UtilMisc.toMap("userLoginId", "system"));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		List exprListForParameters = FastList.newInstance();
		Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
		int totalDays = UtilDateTime.getIntervalInDays(startDate, endDate);
		Debug.log("=====totalDays===" + totalDays);
		try {
			for (int i = 0; i < facilityIds.size(); i++) {
				orderCounter++;
				if ((orderCounter % 200) == 0) {
					elapsedSeconds = UtilDateTime.getInterval(startTimestamp,UtilDateTime.nowTimestamp()) / 1000;
					Debug.logImportant("Completed " + orderCounter+ " orders [ in " + elapsedSeconds + " seconds]",	module);
				}
				String facilityId = (String) facilityIds.get(i);
				List paymentApplicationList = FastList.newInstance();
				Map invoicePaymentInfoMap = FastMap.newInstance();
				BigDecimal outstandingAmount = BigDecimal.ZERO;

				Timestamp paymentDate = startDate;
				for (int k = 0; k <= (totalDays); k++) {
					Map routeMarginMap = FastMap.newInstance();
					paymentDate = UtilDateTime.addDaysToTimestamp(startDate, k);
					Map boothsPaymentsDetail = FastMap.newInstance();
					boothsPaymentsDetail = getBoothPayments(delegator,dispatcher, userLogin, UtilDateTime.toDateString(	paymentDate, "yyyy-MM-dd"), null,facilityId, null, Boolean.TRUE);
					// public static Map getBoothPayments(Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues){
					if (ServiceUtil.isError(boothsPaymentsDetail)) {
						Debug.logError(boothsPaymentsDetail.toString(), module);
						return ServiceUtil.returnError(null, null, null,boothsPaymentsDetail);
					}
					List boothPaymentsList = (List) boothsPaymentsDetail.get("boothPaymentsList");
					if (UtilValidate.isNotEmpty(boothPaymentsList)) {
						Map tempBoothPayment = (Map) boothPaymentsList.get(0);
						outstandingAmount = (BigDecimal) tempBoothPayment.get("totalDue");
					}
					Debug.log("=====outstandingAmount==="+outstandingAmount+"===BoothId==:"+facilityId+"====PaymentDate==:"+paymentDate);
					if (outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					Map<String, Object> paymentCtx = UtilMisc.<String, Object> toMap("paymentMethodTypeId",paymentMethodType);
					paymentCtx.put("userLogin", userLogin);
					paymentCtx.put("facilityId", facilityId);
					paymentCtx.put("supplyDate", UtilDateTime.toDateString(paymentDate, "yyyy-MM-dd HH:mm:ss"));
					paymentCtx.put("amount", outstandingAmount.toString());
					paymentCtx.put("useFifo", true);
					paymentCtx.put("isEnableAcctg", "N");
					try {
						result = dispatcher.runSync("createPaymentForBooth",paymentCtx);
						if (ServiceUtil.isError(result)) {
							return ServiceUtil.returnError(null, null, null,result);
						}
					} catch (GenericServiceException e) {
						// TODO: handle exception
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		elapsedSeconds = UtilDateTime.getInterval(startTimestamp,UtilDateTime.nowTimestamp()) / 1000;
		Debug.logImportant("Completed " + orderCounter + " orders [ in "+ elapsedSeconds + " seconds]", module);
		return ServiceUtil.returnSuccess();

	}

	public static Map<String, Object> getPeriodReturnTotals(DispatchContext ctx, Map<String, ? extends Object> context) {
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
		boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}
		List ownerPartyIds = FastList.newInstance();
		if (isByParty && UtilValidate.isNotEmpty(facilityIds)) {
			try {
				List<GenericValue> facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilities, "ownerPartyId", true);
			} catch (Exception e) {
				Debug.logError(e, e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}

		}
		String subscriptionType = (String) context.get("subscriptionType");
		Boolean onlyVendorAndPTCBooths = (Boolean) context.get("onlyVendorAndPTCBooths");

		List<GenericValue> orderItems = FastList.newInstance();
		Map productAttributes = new TreeMap<String, Object>();
		List productSubscriptionTypeList = FastList.newInstance();
		Map<String, String> dayShipmentMap = FastMap.newInstance();
		List adjustmentOrderList = FastList.newInstance();
		try {

			productSubscriptionTypeList = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS, "SUB_PROD_TYPE"), UtilMisc.toSet("enumId"), UtilMisc.toList("sequenceId"),	null, false);

			if (UtilValidate.isEmpty(shipmentIds)) {
				shipmentIds = getShipmentIds(delegator, fromDate, thruDate);
			}

			// lets populate sales date shipmentId Map
			int intervalDays = (UtilDateTime.getIntervalInDays(fromDate,thruDate)) + 1;
			for (int i = 0; i < intervalDays; i++) {
				Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate,i);
				List dayShipments = getAllShipmentIds(delegator, saleDate,saleDate);
				for (int j = 0; j < dayShipments.size(); j++) {
					dayShipmentMap.put((String) dayShipments.get(j),UtilDateTime.toDateString(saleDate, "yyyy-MM-dd"));
				}
			}
			List returnConditionList = FastList.newInstance();
			returnConditionList.add(EntityCondition.makeCondition("shipmentId",	EntityOperator.IN, shipmentIds));
			if (isByParty) {
				if (UtilValidate.isEmpty(ownerPartyIds)) {
					returnConditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, ownerPartyIds));
				}
			} else {
				if (UtilValidate.isEmpty(facilityIds)) {
					returnConditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN,facilityIds));
				}
			}

			returnConditionList.add(EntityCondition.makeCondition("returnStatusId",EntityOperator.EQUALS, "RETURN_ACCEPTED"));
			EntityCondition returnCondition = EntityCondition.makeCondition(returnConditionList, EntityOperator.AND);
			List<GenericValue> returnItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", returnCondition,null, null, null, false);

			List returnBoothIds = FastList.newInstance();
			if (isByParty) {
				returnBoothIds = EntityUtil.getFieldListFromEntityList(returnItemsList, "ownerPartyId", true);
			} else {
				returnBoothIds = EntityUtil.getFieldListFromEntityList(returnItemsList, "originFacilityId", true);
			}
			List returnShipmentIds = EntityUtil.getFieldListFromEntityList(returnItemsList, "shipmentId", true);
			List returnProductIds = EntityUtil.getFieldListFromEntityList(returnItemsList, "productId", true);

			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("shipmentId",EntityOperator.IN, returnShipmentIds));
			if (isByParty) {
				condList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN, returnBoothIds));
			} else {
				condList.add(EntityCondition.makeCondition("originFacilityId",EntityOperator.IN, returnBoothIds));
			}

			condList.add(EntityCondition.makeCondition("productId",	EntityOperator.IN, returnProductIds));
			condList.add(EntityCondition.makeCondition("orderStatusId",EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
			EntityCondition orderCondition = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> orderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility",orderCondition, null, null, null, false);

			List<GenericValue> newReturnItemList = FastList.newInstance();
			for (GenericValue returnItem : returnItemsList) {
				String boothId = "";
				if (isByParty) {
					boothId = returnItem.getString("ownerPartyId");
				} else {
					boothId = returnItem.getString("originFacilityId");
				}
				String productSubscriptionTypeId = "CASH";
				String shipId = returnItem.getString("shipmentId");
				String productId = returnItem.getString("productId");
				GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", boothId), false);
				if (UtilValidate.isNotEmpty(facility)) {
					// lets override productSubscriptionTypeId based on facility category
					if (facility.getString("categoryTypeEnum").equals("SO_INST")) {
						productSubscriptionTypeId = "SPECIAL_ORDER";
					} else if (facility.getString("categoryTypeEnum").equals("CR_INST")) {
						productSubscriptionTypeId = "CREDIT";
					}
				}
				// to Get related Order for return
				GenericValue returnHeaderOrderItem = null;
				if (isByParty) {
					returnHeaderOrderItem = EntityUtil.getFirst(EntityUtil.filterByAnd(orderItemsList, UtilMisc.toMap("productId", productId, "ownerPartyId",boothId, "shipmentId", shipId,	"productSubscriptionTypeId",productSubscriptionTypeId)));
				} else {
					returnHeaderOrderItem = EntityUtil.getFirst(EntityUtil.filterByAnd(orderItemsList, UtilMisc.toMap("productId", productId, "originFacilityId",boothId, "shipmentId", shipId,	"productSubscriptionTypeId",productSubscriptionTypeId)));
				}

				// making Same record with minus Quantity
				if (UtilValidate.isNotEmpty(returnHeaderOrderItem)) {
					GenericValue newOrderReturnItem = delegator.makeValue("OrderHeaderItemProductShipmentAndFacility");
					newOrderReturnItem.putAll(returnHeaderOrderItem);
					newOrderReturnItem.set("quantity",(returnItem.getBigDecimal("returnQuantity")));
					newReturnItemList.add(newOrderReturnItem);
				}
				if (UtilValidate.isEmpty(returnHeaderOrderItem)) {
					Debug.logImportant("==InConsitentREcord=####==boothId=="+ boothId + "==shipId==" + shipId+ "==productSubscriptionTypeId="+ productSubscriptionTypeId + "==productId="+ productId, "");
				}
			}
			orderItems.addAll(newReturnItemList);
			// }//end of returns check

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		BigDecimal totalQuantity = ZERO;
		BigDecimal totalRevenue = ZERO;
		BigDecimal totalPacket = ZERO;
		BigDecimal totalFat = ZERO;
		BigDecimal totalSnf = ZERO;
		BigDecimal totalVatRevenue = ZERO;

		Map<String, Object> boothZoneMap = FastMap.newInstance();
		boothZoneMap = getAllBoothsZonesMap(delegator);
		Map<String, Object> boothTotals = new TreeMap<String, Object>();
		Map<String, Object> productTotals = new TreeMap<String, Object>();
		Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
		Map<String, Object> dayWiseTotals = new TreeMap<String, Object>();
		Map<String, Object> dayWiseBoothWiseTotals = new TreeMap<String, Object>();
		Iterator<GenericValue> itemIter = orderItems.iterator();
		while (itemIter.hasNext()) {
			GenericValue orderItem = itemIter.next();
			String prodSubscriptionTypeId = orderItem.getString("productSubscriptionTypeId");
			BigDecimal quantity = orderItem.getBigDecimal("quantity");
			BigDecimal packetQuantity = orderItem.getBigDecimal("quantity");
			BigDecimal price = orderItem.getBigDecimal("unitListPrice");
			BigDecimal revenue = price.multiply(quantity);
			if (!(adjustmentOrderList.contains(orderItem.getString("orderId")))	&& (prodSubscriptionTypeId.equals("EMP_SUBSIDY"))) {
				try {
					List<GenericValue> adjustemntsList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderItem.getString("orderId")), null,null, null, false);
					for (GenericValue adjustemnt : adjustemntsList) {
						revenue = revenue.add(adjustemnt.getBigDecimal("amount"));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				adjustmentOrderList.add(orderItem.getString("orderId"));
			}
			totalRevenue = totalRevenue.add(revenue);
			totalPacket = totalPacket.add(packetQuantity);
			BigDecimal vatAmount = ZERO;
			if (UtilValidate.isNotEmpty(orderItem.getBigDecimal("vatAmount"))) {
				vatAmount = orderItem.getBigDecimal("vatAmount");
			}
			BigDecimal vatRevenue = vatAmount.multiply(quantity);
			totalVatRevenue = totalVatRevenue.add(vatRevenue);
			quantity = quantity.multiply(orderItem.getBigDecimal("quantityIncluded"));
			totalQuantity = totalQuantity.add(quantity);
			BigDecimal fat = ZERO;
			BigDecimal snf = ZERO;
			String productName = orderItem.getString("productName");
			String productId = orderItem.getString("productId");
			Map prodAttrMap = (Map) productAttributes.get(orderItem.getString("productId"));
			if (prodAttrMap != null) {
				double fatPercent = Double.parseDouble((String) prodAttrMap.get("FAT"));
				fat = quantity.multiply(BigDecimal.valueOf(fatPercent));
				fat = fat.multiply(BigDecimal.valueOf(1.03));
				fat = fat.divide(BigDecimal.valueOf(100));
				double snfPercent = Double.parseDouble((String) prodAttrMap.get("SNF"));
				snf = quantity.multiply(BigDecimal.valueOf(snfPercent));
				snf = snf.multiply(BigDecimal.valueOf(1.03));
				snf = snf.divide(BigDecimal.valueOf(100));
			}
			totalFat = totalFat.add(fat);
			totalSnf = totalSnf.add(snf);
			Map zone = FastMap.newInstance();
			String boothId = "";
			if (isByParty) {
				zone = (Map) boothZoneMap.get(orderItem.getString("ownerPartyId"));
				boothId = orderItem.getString("ownerPartyId");
			} else {
				zone = (Map) boothZoneMap.get(orderItem.getString("originFacilityId"));
				boothId = orderItem.getString("originFacilityId");
			}

			// Handle booth totals
			if (boothTotals.get(boothId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();

				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("excludeIncentive",orderItem.getString("excludeIncentive"));
				newMap.put("categoryTypeEnum",orderItem.getString("categoryTypeEnum"));
				Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
				Map<String, Object> iteratorMap = FastMap.newInstance();
				while (typeIter.hasNext()) {
					// initialize type maps
					GenericValue type = typeIter.next();
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name", type.getString("enumId"));
					supplyTypeDetailsMap.put("total", ZERO);
					supplyTypeDetailsMap.put("packetQuantity", ZERO);
					supplyTypeDetailsMap.put("totalRevenue", ZERO);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					iteratorMap.put(type.getString("enumId"),supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", iteratorMap);
				}
				Map supplyTypeMap = (Map) newMap.get("supplyTypeTotals");
				Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(prodSubscriptionTypeId);
				supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
				supplyTypeDetailsMap.put("total", quantity);
				supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
				supplyTypeDetailsMap.put("totalRevenue", revenue);
				supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
				supplyTypeMap.put(prodSubscriptionTypeId, supplyTypeDetailsMap);
				newMap.put("supplyTypeTotals", supplyTypeMap);

				Map<String, Object> productItemMap = FastMap.newInstance();
				Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
				Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();

				productItemMap.put("name", productName);
				productSupplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
				productSupplyTypeDetailsMap.put("total", quantity);
				productSupplyTypeDetailsMap.put("packetQuantity",packetQuantity);
				productSupplyTypeDetailsMap.put("totalRevenue", revenue);
				productSupplyTypeDetailsMap.put("vatRevenue", vatRevenue);
				productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),productSupplyTypeDetailsMap);
				productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
				productItemMap.put("total", quantity);
				productItemMap.put("packetQuantity", packetQuantity);
				productItemMap.put("totalRevenue", revenue);
				productItemMap.put("vatRevenue", vatRevenue);

				Map<String, Object> productMap = FastMap.newInstance();
				productMap.put(productId, productItemMap);
				newMap.put("productTotals", productMap);
				boothTotals.put(boothId, newMap);
			} 
			else {
				Map boothMap = (Map) boothTotals.get(boothId);
				BigDecimal runningTotal = (BigDecimal) boothMap.get("total");
				runningTotal = runningTotal.add(quantity);
				boothMap.put("total", runningTotal);

				BigDecimal runningPacketTotal = (BigDecimal) boothMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				boothMap.put("packetQuantity", runningPacketTotal);

				BigDecimal runningTotalRevenue = (BigDecimal) boothMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				boothMap.put("totalRevenue", runningTotalRevenue);

				BigDecimal runningVatRevenue = (BigDecimal) boothMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				boothMap.put("vatRevenue", runningVatRevenue);

				// next handle type totals
				Map tempMap = (Map) boothMap.get("supplyTypeTotals");
				Map typeMap = (Map) tempMap.get(prodSubscriptionTypeId);
				BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
				typeRunningTotal = typeRunningTotal.add(quantity);
				BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
				typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);

				BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
				typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);

				BigDecimal typeRunningVatRevenue = (BigDecimal) typeMap.get("vatRevenue");
				typeRunningVatRevenue = typeRunningVatRevenue.add(vatRevenue);

				typeMap.put("name", prodSubscriptionTypeId);
				typeMap.put("total", typeRunningTotal);
				typeMap.put("packetQuantity", typeRunningPacketTotal);
				typeMap.put("totalRevenue", typeRunningTotalRevenue);
				typeMap.put("vatRevenue", typeRunningVatRevenue);
				// next handle product totals
				Map boothProductTotals = (Map) boothMap.get("productTotals");
				Map productMap = (Map) boothProductTotals.get(productId);
				if (UtilValidate.isEmpty(productMap)) {
					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> supplyTypeMap = FastMap.newInstance();
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
					productItemMap.put("name", productName);
					productItemMap.put("supplyTypeTotals", supplyTypeMap);
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);
					boothProductTotals.put(productId, productItemMap);

				} else {
					BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
					productRunningTotal = productRunningTotal.add(quantity);
					productMap.put("total", productRunningTotal);
					BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
					productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
					productMap.put("totalRevenue", productRunningTotalRevenue);

					BigDecimal productRunningPacketTotals = (BigDecimal) productMap.get("packetQuantity");
					productRunningPacketTotals = productRunningPacketTotals.add(packetQuantity);
					productMap.put("packetQuantity", productRunningPacketTotals);

					BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
					productRunningVatRevenue = productRunningVatRevenue.add(productRunningVatRevenue);
					productMap.put("vatRevenue", productRunningVatRevenue);

					Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
					if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
						Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
						BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
						runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
						BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
						runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);
						BigDecimal runningPacketTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
						runningPacketTotalproductSubscriptionType = runningPacketTotalproductSubscriptionType.add(packetQuantity);
						BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
						runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);

						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
						supplyTypeDetailsMap.put("packetQuantity",runningPacketTotalproductSubscriptionType);
						supplyTypeDetailsMap.put("totalRevenue",runningTotalRevenueproductSubscriptionType);
						supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
						productMap.put("supplyTypeTotals", supplyTypeMap);
						boothProductTotals.put(productId, productMap);

					} else {
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity",packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
						productMap.put("supplyTypeTotals", supplyTypeMap);
						boothProductTotals.put(productId, productMap);
					}
				}
			}
			// handle dayWise Totals if empty ignore this type of totals
			if (UtilValidate.isNotEmpty(dayShipmentMap)) {
				String currentSaleDate = dayShipmentMap.get(orderItem.getString("shipmentId"));
				if (UtilValidate.isEmpty(currentSaleDate)) {
					try {
						GenericValue shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId",orderItem.getString("shipmentId")),false);
						currentSaleDate = UtilDateTime.toDateString(shipment.getTimestamp("estimatedShipDate"),	"yyyy-MM-dd");
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
					}
				}
				// handle dayWise boothWise totals
				if (dayWiseBoothWiseTotals.get(currentSaleDate) == null) {
					Map boothReturnTotal = FastMap.newInstance();
					boothReturnTotal.put(boothId, revenue);
					dayWiseBoothWiseTotals.put(currentSaleDate,	boothReturnTotal);
				} else {
					Map tempDayReturnMap = FastMap.newInstance();
					tempDayReturnMap = (Map) dayWiseBoothWiseTotals.get(currentSaleDate);
					if (tempDayReturnMap.get(boothId) == null) {
						tempDayReturnMap.put(boothId, revenue);
						dayWiseBoothWiseTotals.put(currentSaleDate,	tempDayReturnMap);
					} else {
						BigDecimal boothTot = (BigDecimal) tempDayReturnMap.get(boothId);
						BigDecimal finalTot = boothTot.add(revenue);
						tempDayReturnMap.put(boothId, finalTot);
						dayWiseBoothWiseTotals.put(currentSaleDate,tempDayReturnMap);
					}
				}
				// handle daywise totals
				if (dayWiseTotals.get(currentSaleDate) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("totalRevenue", revenue);
					newMap.put("vatRevenue", vatRevenue);
					newMap.put("excludeIncentive",orderItem.getString("excludeIncentive"));
					newMap.put("categoryTypeEnum",orderItem.getString("categoryTypeEnum"));
					Iterator<GenericValue> typeIter = productSubscriptionTypeList.iterator();
					Map<String, Object> iteratorMap = FastMap.newInstance();
					while (typeIter.hasNext()) {
						// initialize type maps
						GenericValue type = typeIter.next();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name",type.getString("enumId"));
						supplyTypeDetailsMap.put("total", ZERO);
						supplyTypeDetailsMap.put("packetQuantity", ZERO);
						supplyTypeDetailsMap.put("totalRevenue", ZERO);
						supplyTypeDetailsMap.put("vatRevenue", ZERO);
						iteratorMap.put(type.getString("enumId"),supplyTypeDetailsMap);
						newMap.put("supplyTypeTotals", iteratorMap);
					}
					Map supplyTypeMap = (Map) newMap.get("supplyTypeTotals");
					Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("name", prodSubscriptionTypeId);
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(prodSubscriptionTypeId,supplyTypeDetailsMap);
					newMap.put("supplyTypeTotals", supplyTypeMap);

					Map<String, Object> productItemMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeMap = FastMap.newInstance();
					Map<String, Object> productSupplyTypeDetailsMap = FastMap.newInstance();
					productItemMap.put("name", productName);
					productSupplyTypeDetailsMap.put("name",	orderItem.getString("productSubscriptionTypeId"));
					productSupplyTypeDetailsMap.put("total", quantity);
					productSupplyTypeDetailsMap.put("packetQuantity",packetQuantity);
					productSupplyTypeDetailsMap.put("totalRevenue", revenue);
					productSupplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					productSupplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),productSupplyTypeDetailsMap);
					productItemMap.put("supplyTypeTotals", productSupplyTypeMap);
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);

					Map<String, Object> productMap = FastMap.newInstance();
					productMap.put(productId, productItemMap);
					newMap.put("productTotals", productMap);
					dayWiseTotals.put(currentSaleDate, newMap);
				} 
				else {
					Map dayWiseMap = (Map) dayWiseTotals.get(currentSaleDate);
					BigDecimal runningTotal = (BigDecimal) dayWiseMap.get("total");
					runningTotal = runningTotal.add(quantity);
					dayWiseMap.put("total", runningTotal);
					BigDecimal runningTotalRevenue = (BigDecimal) dayWiseMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					dayWiseMap.put("totalRevenue", runningTotalRevenue);
					BigDecimal runningPacketTotal = (BigDecimal) dayWiseMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					dayWiseMap.put("packetQuantity", runningPacketTotal);
					BigDecimal runningVatRevenue = (BigDecimal) dayWiseMap.get("vatRevenue");
					runningVatRevenue = runningVatRevenue.add(vatRevenue);
					dayWiseMap.put("vatRevenue", runningVatRevenue);
					// next handle type totals
					Map tempMap = (Map) dayWiseMap.get("supplyTypeTotals");
					Map typeMap = (Map) tempMap.get(prodSubscriptionTypeId);
					BigDecimal typeRunningTotal = (BigDecimal) typeMap.get("total");
					typeRunningTotal = typeRunningTotal.add(quantity);
					BigDecimal typeRunningTotalRevenue = (BigDecimal) typeMap.get("totalRevenue");
					typeRunningTotalRevenue = typeRunningTotalRevenue.add(revenue);
					BigDecimal typeRunningPacketTotal = (BigDecimal) typeMap.get("packetQuantity");
					typeRunningPacketTotal = typeRunningPacketTotal.add(packetQuantity);
					BigDecimal typeRunningVatRevenue = (BigDecimal) typeMap.get("vatRevenue");
					typeRunningVatRevenue = typeRunningVatRevenue.add(vatRevenue);

					typeMap.put("name", prodSubscriptionTypeId);
					typeMap.put("total", typeRunningTotal);
					typeMap.put("totalRevenue", typeRunningTotalRevenue);
					typeMap.put("packetQuantity", typeRunningPacketTotal);
					typeMap.put("vatRevenue", typeRunningVatRevenue);

					// next handle product totals
					Map dayWiseProductTotals = (Map) dayWiseMap.get("productTotals");
					Map productMap = (Map) dayWiseProductTotals.get(productId);

					if (UtilValidate.isEmpty(productMap)) {
						Map<String, Object> productItemMap = FastMap.newInstance();
						Map<String, Object> supplyTypeMap = FastMap.newInstance();
						Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
						supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
						supplyTypeDetailsMap.put("total", quantity);
						supplyTypeDetailsMap.put("packetQuantity",packetQuantity);
						supplyTypeDetailsMap.put("totalRevenue", revenue);
						supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
						supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
						productItemMap.put("name", productName);
						productItemMap.put("supplyTypeTotals", supplyTypeMap);
						productItemMap.put("total", quantity);
						productItemMap.put("packetQuantity", packetQuantity);
						productItemMap.put("totalRevenue", revenue);
						productItemMap.put("vatRevenue", vatRevenue);
						dayWiseProductTotals.put(productId, productItemMap);

					} else {
						BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
						productRunningTotal = productRunningTotal.add(quantity);
						productMap.put("total", productRunningTotal);
						BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
						productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
						productMap.put("totalRevenue",productRunningTotalRevenue);
						BigDecimal productRunningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
						productRunningPacketTotal = productRunningPacketTotal.add(packetQuantity);
						productMap.put("packetQuantity",productRunningPacketTotal);
						BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
						productRunningVatRevenue = productRunningVatRevenue.add(vatRevenue);
						productMap.put("vatRevenue", productRunningVatRevenue);

						Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
						if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
							Map supplyTypeDetailsMap = (Map) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
							BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
							runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);

							BigDecimal runningTotalRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
							runningTotalRevenueproductSubscriptionType = runningTotalRevenueproductSubscriptionType.add(revenue);

							BigDecimal runningTotalPacketSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
							runningTotalPacketSubscriptionType = runningTotalPacketSubscriptionType.add(packetQuantity);
							BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
							runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);

							supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
							supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
							supplyTypeDetailsMap.put("packetQuantity",runningTotalPacketSubscriptionType);
							supplyTypeDetailsMap.put("totalRevenue",runningTotalRevenueproductSubscriptionType);
							supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);
							supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
							productMap.put("supplyTypeTotals", supplyTypeMap);
							dayWiseProductTotals.put(productId, productMap);

						} else {
							Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
							supplyTypeDetailsMap.put("name", orderItem.getString("productSubscriptionTypeId"));
							supplyTypeDetailsMap.put("total", quantity);
							supplyTypeDetailsMap.put("packetQuantity",packetQuantity);
							supplyTypeDetailsMap.put("totalRevenue", revenue);
							supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
							supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
							productMap.put("supplyTypeTotals", supplyTypeMap);
							dayWiseProductTotals.put(productId, productMap);
						}
					}
				}
			}

			// Handle product totals
			if (productTotals.get(productId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();
				newMap.put("name", productName);
				Map<String, Object> supplyTypeMap = FastMap.newInstance();
				Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
				supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
				supplyTypeDetailsMap.put("total", quantity);
				supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
				supplyTypeDetailsMap.put("totalRevenue", revenue);
				supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
				supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
				newMap.put("supplyTypeTotals", supplyTypeMap);
				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("totalFat", fat);
				newMap.put("totalSnf", snf);
				productTotals.put(productId, newMap);
			} 
			else {
				Map productMap = (Map) productTotals.get(productId);
				BigDecimal runningTotal = (BigDecimal) productMap.get("total");
				runningTotal = runningTotal.add(quantity);
				productMap.put("total", runningTotal);
				BigDecimal runningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				productMap.put("totalRevenue", runningTotalRevenue);
				BigDecimal runningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				productMap.put("packetQuantity", runningPacketTotal);
				BigDecimal runningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				productMap.put("vatRevenue", runningVatRevenue);

				BigDecimal runningTotalFat = (BigDecimal) productMap.get("totalFat");
				runningTotalFat = runningTotalFat.add(fat);
				productMap.put("totalFat", runningTotalFat);
				BigDecimal runningTotalSnf = (BigDecimal) productMap.get("totalSnf");
				runningTotalSnf = runningTotalSnf.add(snf);
				productMap.put("totalSnf", runningTotalSnf);
				Map supplyTypeMap = (Map) productMap.get("supplyTypeTotals");
				if (supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId")) != null) {
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap = (Map<String, Object>) supplyTypeMap.get(orderItem.getString("productSubscriptionTypeId"));
					BigDecimal runningTotalproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("total");
					BigDecimal runningPacketTotalSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("packetQuantity");
					BigDecimal runningRevenueproductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("totalRevenue");
					BigDecimal runningVatRevenueProductSubscriptionType = (BigDecimal) supplyTypeDetailsMap.get("vatRevenue");
					runningTotalproductSubscriptionType = runningTotalproductSubscriptionType.add(quantity);
					runningRevenueproductSubscriptionType = runningRevenueproductSubscriptionType.add(revenue);
					runningPacketTotalSubscriptionType = runningPacketTotalSubscriptionType.add(packetQuantity);
					runningVatRevenueProductSubscriptionType = runningVatRevenueProductSubscriptionType.add(vatRevenue);
					supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total",runningTotalproductSubscriptionType);
					supplyTypeDetailsMap.put("packetQuantity",runningPacketTotalSubscriptionType);
					supplyTypeDetailsMap.put("totalRevenue",runningRevenueproductSubscriptionType);
					supplyTypeDetailsMap.put("vatRevenue",runningVatRevenueProductSubscriptionType);

					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),	supplyTypeDetailsMap);
					productMap.put("supplyTypeTotals", supplyTypeMap);
				} else {
					Map<String, Object> supplyTypeDetailsMap = FastMap.newInstance();
					supplyTypeDetailsMap.put("name",orderItem.getString("productSubscriptionTypeId"));
					supplyTypeDetailsMap.put("total", quantity);
					supplyTypeDetailsMap.put("packetQuantity", packetQuantity);
					supplyTypeDetailsMap.put("totalRevenue", revenue);
					supplyTypeDetailsMap.put("vatRevenue", vatRevenue);
					supplyTypeMap.put(orderItem.getString("productSubscriptionTypeId"),supplyTypeDetailsMap);
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
				newMap.put("vatRevenue", vatRevenue);
				supplyTypeTotals.put(prodSubscriptionTypeId, newMap);
			} 
			else {
				Map supplyTypeMap = (Map) supplyTypeTotals.get(prodSubscriptionTypeId);
				BigDecimal runningTotal = (BigDecimal) supplyTypeMap.get("total");
				runningTotal = runningTotal.add(quantity);
				supplyTypeMap.put("total", runningTotal);
				BigDecimal runningTotalRevenue = (BigDecimal) supplyTypeMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				supplyTypeMap.put("totalRevenue", runningTotalRevenue);
				BigDecimal runningPacketTotal = (BigDecimal) supplyTypeMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				supplyTypeMap.put("packetQuantity", runningPacketTotal);
				BigDecimal runningVatRevenue = (BigDecimal) supplyTypeMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				supplyTypeMap.put("vatRevenue", runningVatRevenue);
				supplyTypeTotals.put(prodSubscriptionTypeId, supplyTypeMap);
			}
		}
		totalQuantity = totalQuantity.setScale(decimals, rounding);
		totalRevenue = totalRevenue.setScale(decimals, rounding);
		totalPacket = totalPacket.setScale(decimals, rounding);
		totalFat = totalFat.setScale(decimals, rounding);
		totalSnf = totalSnf.setScale(decimals, rounding);
		totalVatRevenue = totalVatRevenue.setScale(decimals, rounding);
		for (Map.Entry<String, Object> entry : productTotals.entrySet()) {
			Map<String, Object> productValue = (Map<String, Object>) entry.getValue();
			BigDecimal tempVal = (BigDecimal) productValue.get("total");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("total", tempVal);

			tempVal = (BigDecimal) productValue.get("packetQuantity");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("packetQuantity", tempVal);

			tempVal = (BigDecimal) productValue.get("totalRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("totalRevenue", tempVal);
			tempVal = (BigDecimal) productValue.get("totalFat");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("totalFat", tempVal);
			tempVal = (BigDecimal) productValue.get("totalSnf");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("totalSnf", tempVal);
			tempVal = (BigDecimal) productValue.get("vatRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("vatRevenue", tempVal);
		}
		for (Map.Entry<String, Object> entry : supplyTypeTotals.entrySet()) {
			Map<String, Object> supplyTypeValue = (Map<String, Object>) entry.getValue();
			BigDecimal tempVal = (BigDecimal) supplyTypeValue.get("total");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("total", tempVal);

			tempVal = (BigDecimal) supplyTypeValue.get("packetQuantity");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("packetQuantity", tempVal);

			tempVal = (BigDecimal) supplyTypeValue.get("totalRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("totalRevenue", tempVal);
			tempVal = (BigDecimal) supplyTypeValue.get("vatRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			supplyTypeValue.put("vatRevenue", tempVal);

		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("totalQuantity", totalQuantity);
		result.put("totalRevenue", totalRevenue);
		result.put("totalVatRevenue", totalVatRevenue);
		result.put("totalPacket", totalPacket);
		result.put("totalFat", totalFat);
		result.put("totalSnf", totalSnf);
		// result.put("zoneTotals", zoneTotals);

		result.put("dayWiseBoothWiseTotals", dayWiseBoothWiseTotals);
		result.put("boothTotals", boothTotals);
		result.put("dayWiseTotals", dayWiseTotals);
		// result.put("distributorTotals", distributorTotals);
		result.put("productTotals", productTotals);
		result.put("supplyTypeTotals", supplyTypeTotals);
		return result;
	}
	
	 /**
     * Get the sales order totals for the given date.  The totals are also segmented into products and zones for
     * reporting purposes
     * @param ctx the dispatch context
     * @param salesDate
     * @param onlySummary
     * @param onlyVendorAndPTCBooths
     * @param context 
     * @return totals map
     */
    public static Map<String, Object> getDayTotals(DispatchContext ctx, Timestamp salesDate, boolean onlySummary, boolean onlyVendorAndPTCBooths) {
    	return getDayTotals(ctx, salesDate, null, onlySummary, onlyVendorAndPTCBooths, null);
    } 
    
    
    /**
     * Get the sales order totals for the given date.  The totals are also segmented into products and zones for
     * reporting purposes
     * @param ctx the dispatch context
     * @param salesDate
     * @param onlySummary
     * @param onlyVendorAndPTCBooths
     * @param boothId limit totals related to this booth alone
     * @return totals map
     */
    public static Map<String, Object> getDayTotals(DispatchContext ctx, Timestamp salesDate, String subscriptionType, boolean onlySummary, boolean onlyVendorAndPTCBooths, List facilityIds) {
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> orderItems= FastList.newInstance();
    	Map productAttributes = new TreeMap<String, Object>();    
    	List productSubscriptionTypeList = FastList.newInstance();
    	List shipmentIds =FastList.newInstance();
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
        	
        	if(UtilValidate.isEmpty(subscriptionType)){
        		//shipmentIds = getByProductShipmentIds(delegator , UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"),null);
        		shipmentIds =getByProdShipmentIds(delegator,salesDate,salesDate);
        	}else{
        			//shipmentIds = getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"),subscriptionType);
        			shipmentIds=getShipmentIdsSupplyType(delegator, salesDate,salesDate,subscriptionType);
        	}
           //Debug.logInfo("salesDate=" + salesDate + "shipmentIds=" + shipmentIds, module);
            List conditionList= FastList.newInstance(); 
        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
        	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));    		
            if (onlyVendorAndPTCBooths) {
            	conditionList.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("categoryTypeEnum", "VENDOR"), EntityCondition.makeCondition("categoryTypeEnum", "PTC")));
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
    	/*if (!onlySummary) {
    		boothZoneMap = getAllBoothsZonesMap(delegator); 
    		Debug.log("===boothZoneMap="+boothZoneMap);
    	}*/
//Debug.logInfo("boothZoneMap=" + boothZoneMap, module);
    	Map<String, Object> boothTotals = new TreeMap<String, Object>();
    	Map<String, Object> zoneTotals = new TreeMap<String, Object>();
    	Map<String, Object> distributorTotals = new TreeMap<String, Object>();
    	Map<String, Object> productTotals = new TreeMap<String, Object>();
    	Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
    	
        Iterator<GenericValue> itemIter = orderItems.iterator();
    	while(itemIter.hasNext()) {
            GenericValue orderItem = itemIter.next();
            String prodSubscriptionTypeId = orderItem.getString("productSubscriptionTypeId");
            BigDecimal quantity  = orderItem.getBigDecimal("quantity");
            BigDecimal price  = orderItem.getBigDecimal("unitPrice"); 
            BigDecimal revenue = price.multiply(quantity);
            totalRevenue = totalRevenue.add(revenue);
          //  quantity = quantity.multiply(orderItem.getBigDecimal("quantityIncluded"));
            quantity = orderItem.getBigDecimal("quantity");
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
    		if (!onlySummary) {
    			
    			//Map zone = (Map)boothZoneMap.get(orderItem.getString("originFacilityId"));
    			
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
    			// Handle zone totals
    			/*String zoneName = (String)zone.get("name");
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
    			}*/
    			// Handle distributor totals
    			//distributorTotals
    			/*String distributorId = (String)zone.get("distributorId");    		
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
    			}
    			// Handle product totals
    			
 */   			if (productTotals.get(productId) == null) {
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
    	}    	
		totalQuantity = totalQuantity.setScale(decimals, rounding);  
		totalRevenue = totalRevenue.setScale(decimals, rounding);    
		totalFat = totalFat.setScale(decimals, rounding);    
		totalSnf = totalSnf.setScale(decimals, rounding);    
		
		// set scale
		if (!onlySummary) {
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
	        }	        
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
		}
		// check and update sales history summary entity\
		
		if(UtilValidate.isEmpty(subscriptionType)) {
			try {
				Date summaryDate = new Date(salesDate.getTime());
				
//Debug.logInfo("salesDate=" + salesDate + "shipmentIds=" + shipmentIds, module);
				GenericValue salesSummary = delegator.findOne("LMSSalesHistorySummary", UtilMisc.toMap("salesDate", summaryDate), false);
				if (salesSummary == null) {
					// add to summary table
					salesSummary = delegator.makeValue("LMSSalesHistorySummary");
					salesSummary.put("salesDate", summaryDate);
					salesSummary.put("totalQuantity", totalQuantity);
					salesSummary.put("totalRevenue", totalRevenue);                
					salesSummary.create();  
					SalesHistoryServices.LMSSalesHistorySummaryDetail(ctx,  UtilMisc.toMap("salesDate", summaryDate));
				}
				else {
					// check and see if we need to update for whatever reason
					BigDecimal summaryQuantity  = salesSummary.getBigDecimal("totalQuantity");
					BigDecimal summaryRevenue  = salesSummary.getBigDecimal("totalRevenue");     
					if (summaryQuantity.compareTo(totalQuantity) != 0 || summaryRevenue.compareTo(totalRevenue) != 0) {
						salesSummary.put("totalQuantity", totalQuantity);
						salesSummary.put("totalRevenue", totalRevenue);  
						salesSummary.store();
						SalesHistoryServices.LMSSalesHistorySummaryDetail(ctx,  UtilMisc.toMap("salesDate", summaryDate));
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}	
		}
		
		Map<String, Object> result = FastMap.newInstance();        
        result.put("totalQuantity", totalQuantity);
        result.put("totalRevenue", totalRevenue);
        result.put("totalFat", totalFat);   
        result.put("totalSnf", totalSnf);                
        result.put("zoneTotals", zoneTotals);
        result.put("boothTotals", boothTotals);
        result.put("distributorTotals", distributorTotals);
        result.put("productTotals", productTotals);      
        result.put("supplyTypeTotals", supplyTypeTotals);  
        result.put("shipmentIds", shipmentIds); //used to get ShipmentBooths
        
        return result;
    }  
    public static Map<String, Object> getDayReturnTotals(DispatchContext ctx, Map<String, ? extends Object> context) {
   // public static Map<String, Object> getDayReturnTotals(DispatchContext ctx, Timestamp salesDate, String subscriptionType, boolean onlySummary, boolean onlyVendorAndPTCBooths, List facilityIds) {
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> returnItemsList= FastList.newInstance();
    	List<String> facilityIds = (List<String>) context.get("facilityIds");
		List<String> shipmentIds = (List<String>) context.get("shipmentIds");
		Timestamp salesDate = UtilDateTime.nowTimestamp();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (!UtilValidate.isEmpty(context.get("salesDate"))) {
			 salesDate = (Timestamp) context.get("salesDate");
		}
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate=salesDate;
		}
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		if (UtilValidate.isEmpty(thruDate)) {
			thruDate=salesDate;
		}
		String subscriptionType = (String) context.get("subscriptionType");
    	Map productAttributes = new TreeMap<String, Object>();    
    	List productSubscriptionTypeList = FastList.newInstance();
    	//List shipmentIds =FastList.newInstance();
    	boolean isByParty = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("isByParty"))) {
			isByParty = (Boolean) context.get("isByParty");
		}
		List ownerPartyIds = FastList.newInstance();
		if (isByParty && UtilValidate.isNotEmpty(facilityIds)) {
			try {
				List<GenericValue> facilities = delegator.findList("Facility",EntityCondition.makeCondition("facilityId",EntityOperator.IN, facilityIds), UtilMisc.toSet("ownerPartyId"), null, null, false);
				ownerPartyIds = EntityUtil.getFieldListFromEntityList(facilities, "ownerPartyId", true);
			} catch (Exception e) {
				Debug.logError(e, e.toString(), module);
				return ServiceUtil.returnError(e.toString());
			}

		}
    	try {
        	if(UtilValidate.isEmpty(subscriptionType)){
        		//shipmentIds = getByProductShipmentIds(delegator , UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"),null);
        		shipmentIds =getByProdShipmentIds(delegator,salesDate,salesDate);
        	}else{
        			//shipmentIds = getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"),subscriptionType);
        			shipmentIds=getShipmentIdsSupplyType(delegator, salesDate,salesDate,subscriptionType);
        	}
        	Debug.log("salesDate===INRETURNS==" + salesDate + "shipmentIds=" + shipmentIds+"===SUBSCRIPTION==="+subscriptionType, module);	
        	List returnConditionList = FastList.newInstance();
			returnConditionList.add(EntityCondition.makeCondition("shipmentId",	EntityOperator.IN, shipmentIds));
			if (isByParty) {
				if (UtilValidate.isNotEmpty(ownerPartyIds)) {
					returnConditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, ownerPartyIds));
				}
			} else {
				if (UtilValidate.isNotEmpty(facilityIds)) {
					returnConditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN,facilityIds));
				}
			}
			returnConditionList.add(EntityCondition.makeCondition("returnStatusId",EntityOperator.EQUALS, "RETURN_ACCEPTED"));
			EntityCondition returnCondition = EntityCondition.makeCondition(returnConditionList, EntityOperator.AND);
			if(!UtilValidate.isEmpty(shipmentIds)){        		
				returnItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", returnCondition,null, null, null, false);
        	}

    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
    	BigDecimal totalQuantity = ZERO;
    	BigDecimal totalRevenue = ZERO;
    	BigDecimal totalFat = ZERO;
    	BigDecimal totalSnf = ZERO;
    	
    	Map<String, Object> boothZoneMap = FastMap.newInstance();
    	
    	Map<String, Object> boothTotals = new TreeMap<String, Object>();
    	Map<String, Object> zoneTotals = new TreeMap<String, Object>();
    	Map<String, Object> distributorTotals = new TreeMap<String, Object>();
    	Map<String, Object> productTotals = new TreeMap<String, Object>();
    	Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
    	
        Iterator<GenericValue> itemIter = returnItemsList.iterator();
    	while(itemIter.hasNext()) {
            GenericValue orderItem = itemIter.next();
            String prodSubscriptionTypeId = "";//orderItem.getString("productSubscriptionTypeId");
            BigDecimal quantity  = orderItem.getBigDecimal("returnQuantity");
            BigDecimal price  = orderItem.getBigDecimal("returnPrice"); 
            BigDecimal revenue = price.multiply(quantity);
            totalRevenue = totalRevenue.add(revenue);
          //  quantity = quantity.multiply(orderItem.getBigDecimal("quantityIncluded"));
            quantity = orderItem.getBigDecimal("returnQuantity");
    		totalQuantity = totalQuantity.add(quantity);   
    		BigDecimal fat = ZERO;
    		BigDecimal snf = ZERO;
    		String productName = "";
			String productId = orderItem.getString("productId");
		    		
    			//Map zone = (Map)boothZoneMap.get(orderItem.getString("originFacilityId"));
    			
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
    				Map<String, Object> productItemMap = FastMap.newInstance();
    				productItemMap.put("name", productName);
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
					// next handle product totals
					Map boothProductTotals = (Map)boothMap.get("productTotals");
					Map productMap = (Map)boothProductTotals.get(productId);
					
					if(UtilValidate.isEmpty(productMap)){
						Map<String, Object> productItemMap = FastMap.newInstance();
	    				productItemMap.put("name", productName);
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
	    			}
				}
    			
    			// Handle product totals
     			if (productTotals.get(productId) == null) {
    				Map<String, Object> newMap = FastMap.newInstance();
    				newMap.put("name", productName);
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
    			}
    		
    	}    	
		totalQuantity = totalQuantity.setScale(decimals, rounding);  
		totalRevenue = totalRevenue.setScale(decimals, rounding);    
		totalFat = totalFat.setScale(decimals, rounding);    
		totalSnf = totalSnf.setScale(decimals, rounding);    
		
		// set scale
		
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
		
		
		Map<String, Object> result = FastMap.newInstance();        
        result.put("totalQuantity", totalQuantity);
        result.put("totalRevenue", totalRevenue);
        result.put("totalFat", totalFat);   
        result.put("totalSnf", totalSnf);                
        result.put("boothTotals", boothTotals);
        result.put("productTotals", productTotals);      
        result.put("supplyTypeTotals", supplyTypeTotals);  
        result.put("shipmentIds", shipmentIds); //used to get ShipmentBooths
        
        return result;
    }  

}
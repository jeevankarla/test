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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;

public class SalesInvoiceServices {
	
    public static final String module = SalesInvoiceServices.class.getName();
    private static BigDecimal ZERO = BigDecimal.ZERO;
	private static int decimals;
	private static int rounding;
	public static final String resource_error = "OrderErrorUiLabels";
	static {
		decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
		rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
		// set zero to the proper scale
		if (decimals != -1) ZERO = ZERO.setScale(decimals);
	}

	    
	/**
	 * Get the sales order totals for the given period. The totals are also
	 * segmented into products and zones for reporting purposes
	* Get the sales order totals for the given period.  The totals are also segmented into products  for
	     * reporting purposes
	     * @param ctx the dispatch context
	     * @param context context map
	 * @return totals map
	 * 
	* ::TODO:: consolidate DayTotals, PeriodTotals and DaywiseTotals functions
	 */
	public static Map<String, Object> getPeriodSalesTotals(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
	    List<String> partyIds = (List<String>) context.get("partyIds");
		//List<String> shipmentIds = (List<String>) context.get("shipmentIds");
	    boolean isQuantityLtrs = Boolean.FALSE;
		if(UtilValidate.isNotEmpty(context.get("isQuantityLtrs"))){
			isQuantityLtrs = (Boolean)context.get("isQuantityLtrs");
		}
		 boolean isPurchaseInvoice = Boolean.FALSE;
			if(UtilValidate.isNotEmpty(context.get("isPurchaseInvoice"))){
				isPurchaseInvoice = (Boolean)context.get("isPurchaseInvoice");
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
		Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		
		//List<GenericValue> orderItems = FastList.newInstance();
		EntityListIterator invoiceItemsIter = null;
		Map productAttributes = new TreeMap<String, Object>();
		List productSubscriptionTypeList = FastList.newInstance();
		Map<String, String> dayWiseSaleMap = FastMap.newInstance();
		List adjustmentOrderList = FastList.newInstance();
		try {
			// lets populate sales date  Map
			int intervalDays = (UtilDateTime.getIntervalInDays(fromDate,thruDate)) + 1;
			/*for (int i = 0; i < intervalDays; i++) {
				Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate,i);
				dayWiseSaleMap.put(UtilDateTime.toDateString(saleDate, "yyyy-MM-dd"),null);
			}
			Debug.log("====>dayWiseSaleMap ===!"+dayWiseSaleMap);*/
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			if(isPurchaseInvoice){
				conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
				if (UtilValidate.isNotEmpty(partyIds)) {
					conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIds));
				}/*else{
					conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, null));
					Debug.log("====>Given partyIds list is empty and then Result returned from Service is Empty  ===!");
					Debug.logError("partyIds cannot be empty", module);
					return ServiceUtil.returnError("partyIds cannot be empty");
				}*/
			}else{//no need to send Purchase Invoice
				conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
				conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,"Company"));
				if (UtilValidate.isNotEmpty(partyIds)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
				}/*else{
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null));
					Debug.log("====>Given partyIds list is empty and then Result returned from Service is Empty  ===!");
					Debug.logError("partyIds cannot be empty", module);
					return ServiceUtil.returnError("partyIds cannot be empty");
				}*/
			}
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		BigDecimal totalQuantity = ZERO;
		BigDecimal totalRevenue = ZERO;
		BigDecimal totalBasicRevenue = ZERO;
		BigDecimal totalPacket = ZERO;
		BigDecimal totalFat = ZERO;
		BigDecimal totalSnf = ZERO;
		BigDecimal totalVatRevenue = ZERO;
		BigDecimal totalBedRevenue = ZERO;
		BigDecimal totalCstRevenue = ZERO;

		Map<String, Object> partyTotals = new TreeMap<String, Object>();
		Map<String, Object> productTotals = new TreeMap<String, Object>();
		Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
		Map<String, Object> dayWiseTotals = new TreeMap<String, Object>();
	   GenericValue invoiceItem;   
	   while( invoiceItemsIter != null && (invoiceItem = invoiceItemsIter.next()) != null) {
			String prodSubscriptionTypeId = invoiceItem.getString("productSubscriptionTypeId");
			BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
			BigDecimal packetQuantity = invoiceItem.getBigDecimal("quantity");
			BigDecimal price = invoiceItem.getBigDecimal("unitListPrice");
			
			BigDecimal basicPrice = invoiceItem.getBigDecimal("unitPrice");
			BigDecimal basicRevenue = basicPrice.multiply(quantity);
			totalBasicRevenue=totalBasicRevenue.add(basicRevenue);
			
			BigDecimal revenue = price.multiply(quantity);
			
		/*	if (!(adjustmentOrderList.contains(invoiceItem.getString("orderId")))	&& (prodSubscriptionTypeId.equals("EMP_SUBSIDY"))) {
				try {
					List<GenericValue> adjustemntsList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,invoiceItem.getString("orderId")), null,null, null, false);
					for (GenericValue adjustemnt : adjustemntsList) {
						revenue = revenue.add(adjustemnt.getBigDecimal("amount"));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				adjustmentOrderList.add(invoiceItem.getString("orderId"));
			}*/
			totalRevenue = totalRevenue.add(revenue);
			totalPacket = totalPacket.add(packetQuantity);
			
			BigDecimal vatAmount = ZERO;
			if (UtilValidate.isNotEmpty(invoiceItem.getBigDecimal("vatAmount"))) {
				vatAmount = invoiceItem.getBigDecimal("vatAmount");
			}
			BigDecimal vatRevenue = vatAmount.multiply(quantity);
			totalVatRevenue = totalVatRevenue.add(vatRevenue);
			
			BigDecimal bedAmount = ZERO;
			if (UtilValidate.isNotEmpty(invoiceItem.getBigDecimal("bedAmount"))) {
				bedAmount = invoiceItem.getBigDecimal("bedAmount");
			}
			BigDecimal bedRevenue = bedAmount.multiply(quantity);
			totalBedRevenue = totalBedRevenue.add(bedRevenue);
			
			BigDecimal cstAmount = ZERO;
			if (UtilValidate.isNotEmpty(invoiceItem.getBigDecimal("cstAmount"))) {
				cstAmount = invoiceItem.getBigDecimal("cstAmount");
			}
			BigDecimal cstRevenue = cstAmount.multiply(quantity);
			totalCstRevenue = totalCstRevenue.add(cstRevenue);
			
			//String productName = invoiceItem.getString("productName");
			String productId = invoiceItem.getString("productId");
			if(isQuantityLtrs){
			   try{
				    GenericValue productDetails = delegator.findOne("Product",UtilMisc.toMap("productId", productId), true);
					if(UtilValidate.isNotEmpty(productDetails.getBigDecimal("quantityIncluded"))) {
					quantity = quantity.multiply(productDetails.getBigDecimal("quantityIncluded"));
					}
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
			}
			totalQuantity = totalQuantity.add(quantity);
			BigDecimal fat = ZERO;
			BigDecimal snf = ZERO;
		
			String partyId = "";
			partyId = invoiceItem.getString("partyId");
			
			if (partyTotals.get(partyId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();

				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("basicRevenue", basicRevenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("bedRevenue", bedRevenue);
				newMap.put("cstRevenue", cstRevenue);
				
				Map<String, Object> productItemMap = FastMap.newInstance();
				productItemMap.put("total", quantity);
				productItemMap.put("packetQuantity", packetQuantity);
				productItemMap.put("basicRevenue", basicRevenue);
				productItemMap.put("totalRevenue", revenue);
				productItemMap.put("vatRevenue", vatRevenue);
				productItemMap.put("bedRevenue", bedRevenue);
				productItemMap.put("cstRevenue", cstRevenue);
				Map<String, Object> productMap = FastMap.newInstance();
				productMap.put(productId, productItemMap);
				
				newMap.put("productTotals", productMap);
				partyTotals.put(partyId, newMap);
			}else {
				Map partyMap = (Map) partyTotals.get(partyId);
				BigDecimal runningTotal = (BigDecimal) partyMap.get("total");
				runningTotal = runningTotal.add(quantity);
				partyMap.put("total", runningTotal);

				BigDecimal runningPacketTotal = (BigDecimal) partyMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				partyMap.put("packetQuantity", runningPacketTotal);

				
				BigDecimal runningTotalBasicRevenue = (BigDecimal) partyMap.get("basicRevenue");
				runningTotalBasicRevenue = runningTotalBasicRevenue.add(basicRevenue);
				partyMap.put("basicRevenue", runningTotalBasicRevenue);
				
				BigDecimal runningTotalRevenue = (BigDecimal) partyMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				partyMap.put("totalRevenue", runningTotalRevenue);

				BigDecimal runningVatRevenue = (BigDecimal) partyMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				partyMap.put("vatRevenue", runningVatRevenue);
				BigDecimal runningBedRevenue = (BigDecimal) partyMap.get("bedRevenue");
				runningBedRevenue = runningBedRevenue.add(bedRevenue);
				partyMap.put("bedRevenue", runningBedRevenue);
				BigDecimal runningCstRevenue = (BigDecimal) partyMap.get("cstRevenue");
				runningCstRevenue = runningCstRevenue.add(cstRevenue);
				partyMap.put("cstRevenue", runningCstRevenue);
				
				// next handle product totals
				Map partyProductTotals = (Map) partyMap.get("productTotals");
				Map productMap = (Map) partyProductTotals.get(productId);
				if (UtilValidate.isEmpty(productMap)) {
					Map<String, Object> productItemMap = FastMap.newInstance();
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("basicRevenue", basicRevenue);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);
					productItemMap.put("bedRevenue", bedRevenue);
					productItemMap.put("cstRevenue", cstRevenue);
					partyProductTotals.put(productId, productItemMap);

				} else {
					BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
					productRunningTotal = productRunningTotal.add(quantity);
					productMap.put("total", productRunningTotal);
					
					BigDecimal productRunningBasicRevenue = (BigDecimal) productMap.get("basicRevenue");
					productRunningBasicRevenue = productRunningBasicRevenue.add(basicRevenue);
					productMap.put("basicRevenue", productRunningBasicRevenue);
					
					BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
					productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
					productMap.put("totalRevenue", productRunningTotalRevenue);

					BigDecimal productRunningPacketTotals = (BigDecimal) productMap.get("packetQuantity");
					productRunningPacketTotals = productRunningPacketTotals.add(packetQuantity);
					productMap.put("packetQuantity", productRunningPacketTotals);

					BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
					productRunningVatRevenue = productRunningVatRevenue.add(productRunningVatRevenue);
					productMap.put("vatRevenue", productRunningVatRevenue);
					
					BigDecimal prodRunningBedRevenue = (BigDecimal) productMap.get("bedRevenue");
					prodRunningBedRevenue = prodRunningBedRevenue.add(bedRevenue);
					productMap.put("bedRevenue", prodRunningBedRevenue);
					
					BigDecimal prodRunningCstRevenue = (BigDecimal) productMap.get("cstRevenue");
					prodRunningCstRevenue = prodRunningCstRevenue.add(cstRevenue);
					productMap.put("cstRevenue", prodRunningCstRevenue);
					
					partyProductTotals.put(productId, productMap);
				}
			}
			// handle dayWise Totals For Daywise not PartyWise
			if (UtilValidate.isNotEmpty(dayWiseSaleMap)) {
				String currentSaleDate=UtilDateTime.toDateString(invoiceItem.getTimestamp("invoiceDate"), "yyyy-MM-dd");
				Debug.log("====>currentSaleDate ===!"+currentSaleDate);
				if (dayWiseTotals.get(currentSaleDate) == null) {
					Map<String, Object> newMap = FastMap.newInstance();
					newMap.put("total", quantity);
					newMap.put("packetQuantity", packetQuantity);
					newMap.put("basicRevenue", basicRevenue);
					newMap.put("totalRevenue", revenue);
					newMap.put("vatRevenue", vatRevenue);
					newMap.put("bedRevenue", bedRevenue);
					newMap.put("cstRevenue", cstRevenue);
					
					Map<String, Object> productItemMap = FastMap.newInstance();
					
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("basicRevenue", basicRevenue);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);
					productItemMap.put("bedRevenue", bedRevenue);
					productItemMap.put("cstRevenue", cstRevenue);

					Map<String, Object> productMap = FastMap.newInstance();
					productMap.put(productId, productItemMap);
					newMap.put("productTotals", productMap);
					
					Map<String, Object> partyMap = FastMap.newInstance();
					partyMap.put(partyId, newMap);
					newMap.put("dayPartyTotals", partyMap);
					
					dayWiseTotals.put(currentSaleDate, newMap);
				} 
				else {
					Map dayWiseMap = (Map) dayWiseTotals.get(currentSaleDate);
					BigDecimal runningTotal = (BigDecimal) dayWiseMap.get("total");
					runningTotal = runningTotal.add(quantity);
					dayWiseMap.put("total", runningTotal);
					
					BigDecimal runningTotalBasicRevenue = (BigDecimal) dayWiseMap.get("basicRevenue");
					runningTotalBasicRevenue = runningTotalBasicRevenue.add(basicRevenue);
					dayWiseMap.put("basicRevenue", runningTotalBasicRevenue);
					
					BigDecimal runningTotalRevenue = (BigDecimal) dayWiseMap.get("totalRevenue");
					runningTotalRevenue = runningTotalRevenue.add(revenue);
					dayWiseMap.put("totalRevenue", runningTotalRevenue);
					BigDecimal runningPacketTotal = (BigDecimal) dayWiseMap.get("packetQuantity");
					runningPacketTotal = runningPacketTotal.add(packetQuantity);
					dayWiseMap.put("packetQuantity", runningPacketTotal);
					BigDecimal runningVatRevenue = (BigDecimal) dayWiseMap.get("vatRevenue");
					runningVatRevenue = runningVatRevenue.add(vatRevenue);
					dayWiseMap.put("vatRevenue", runningVatRevenue);
					
					BigDecimal runningBedRevenue = (BigDecimal) dayWiseMap.get("bedRevenue");
					runningBedRevenue = runningBedRevenue.add(bedRevenue);
					dayWiseMap.put("bedRevenue", runningBedRevenue);
					
					BigDecimal runningCstRevenue = (BigDecimal) dayWiseMap.get("cstRevenue");
					runningCstRevenue = runningCstRevenue.add(cstRevenue);
					dayWiseMap.put("cstRevenue", runningCstRevenue);

					// next handle product totals
					Map dayWiseProductTotals = (Map) dayWiseMap.get("productTotals");
					Map productMap = (Map) dayWiseProductTotals.get(productId);

					if (UtilValidate.isEmpty(productMap)) {
						Map<String, Object> productItemMap = FastMap.newInstance();
						
						productItemMap.put("total", quantity);
						productItemMap.put("packetQuantity", packetQuantity);
						productItemMap.put("totalRevenue", revenue);
						productItemMap.put("basicRevenue", basicRevenue);
						productItemMap.put("vatRevenue", vatRevenue);
						productItemMap.put("bedRevenue", bedRevenue);
						productItemMap.put("cstRevenue", cstRevenue);
						dayWiseProductTotals.put(productId, productItemMap);

					} else {
						BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
						productRunningTotal = productRunningTotal.add(quantity);
						productMap.put("total", productRunningTotal);
						
						BigDecimal productRunningTotalBasicRevenue = (BigDecimal)productMap.get("basicRevenue");
						productRunningTotalBasicRevenue = productRunningTotalBasicRevenue.add(basicRevenue);
						productMap.put("basicRevenue", productRunningTotalBasicRevenue);
						
						BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
						productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
						productMap.put("totalRevenue",productRunningTotalRevenue);
						
						BigDecimal productRunningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
						productRunningPacketTotal = productRunningPacketTotal.add(packetQuantity);
						productMap.put("packetQuantity",productRunningPacketTotal);
						
						BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
						productRunningVatRevenue = productRunningVatRevenue.add(vatRevenue);
						productMap.put("vatRevenue", productRunningVatRevenue);
						
						BigDecimal prodRunningBedRevenue = (BigDecimal) productMap.get("bedRevenue");
						prodRunningBedRevenue = prodRunningBedRevenue.add(bedRevenue);
						productMap.put("bedRevenue", prodRunningBedRevenue);
						
						BigDecimal prodRunningCstRevenue = (BigDecimal) productMap.get("cstRevenue");
						prodRunningCstRevenue = prodRunningCstRevenue.add(cstRevenue);
						productMap.put("cstRevenue", prodRunningCstRevenue);
						
						dayWiseProductTotals.put(productId, productMap);
					}
					//Day wise Party Totals
				/*	Map dayPartyTotals=dayWiseMap.get("dayPartyTotals");
					Map partyMap =(Map) dayPartyTotals.get(partyId);
					if (partyMap.get(partyId) == null) {
						Map<String, Object> newMap = FastMap.newInstance();

						newMap.put("total", quantity);
						newMap.put("packetQuantity", packetQuantity);
						newMap.put("totalRevenue", revenue);
						newMap.put("vatRevenue", vatRevenue);
						newMap.put("bedRevenue", bedRevenue);
						newMap.put("cstRevenue", cstRevenue);
						
						Map<String, Object> productItemMap = FastMap.newInstance();
						productItemMap.put("total", quantity);
						productItemMap.put("packetQuantity", packetQuantity);
						productItemMap.put("totalRevenue", revenue);
						productItemMap.put("vatRevenue", vatRevenue);
						productItemMap.put("bedRevenue", bedRevenue);
						productItemMap.put("cstRevenue", cstRevenue);
						Map<String, Object> productMap = FastMap.newInstance();
						productMap.put(productId, productItemMap);
						
						newMap.put("productTotals", productMap);
						partyMap.put(partyId, newMap);
					}else{
						BigDecimal runningTotal = (BigDecimal) partyMap.get("total");
						runningTotal = runningTotal.add(quantity);
						partyMap.put("total", runningTotal);

						BigDecimal runningPacketTotal = (BigDecimal) partyMap.get("packetQuantity");
						runningPacketTotal = runningPacketTotal.add(packetQuantity);
						partyMap.put("packetQuantity", runningPacketTotal);

						BigDecimal runningTotalRevenue = (BigDecimal) partyMap.get("totalRevenue");
						runningTotalRevenue = runningTotalRevenue.add(revenue);
						partyMap.put("totalRevenue", runningTotalRevenue);

						BigDecimal runningVatRevenue = (BigDecimal) partyMap.get("vatRevenue");
						runningVatRevenue = runningVatRevenue.add(vatRevenue);
						partyMap.put("vatRevenue", runningVatRevenue);
						BigDecimal runningBedRevenue = (BigDecimal) partyMap.get("bedRevenue");
						runningBedRevenue = runningBedRevenue.add(bedRevenue);
						partyMap.put("bedRevenue", runningVatRevenue);
						BigDecimal runningCstRevenue = (BigDecimal) partyMap.get("cstRevenue");
						runningCstRevenue = runningCstRevenue.add(cstRevenue);
						partyMap.put("cstRevenue", runningCstRevenue);
						
						// next handle product totals
						Map partyProductTotals = (Map) partyMap.get("productTotals");
						Map productMap = (Map) partyProductTotals.get(productId);
						if (UtilValidate.isEmpty(productMap)) {
							Map<String, Object> productItemMap = FastMap.newInstance();
							productItemMap.put("name", productName);
							productItemMap.put("total", quantity);
							productItemMap.put("packetQuantity", packetQuantity);
							productItemMap.put("totalRevenue", revenue);
							productItemMap.put("vatRevenue", vatRevenue);
							productItemMap.put("bedRevenue", bedRevenue);
							productItemMap.put("cstRevenue", cstRevenue);
							partyProductTotals.put(productId, productItemMap);

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
							
							BigDecimal runningBedRevenue = (BigDecimal) productMap.get("bedRevenue");
							runningBedRevenue = runningBedRevenue.add(bedRevenue);
							productMap.put("bedRevenue", runningVatRevenue);
							
							BigDecimal runningCstRevenue = (BigDecimal) productMap.get("cstRevenue");
							runningCstRevenue = runningCstRevenue.add(cstRevenue);
							productMap.put("cstRevenue", runningCstRevenue);
							
						}
						partyProductTotals.put(productId, productMap);
						
						partyMap("productTotals",partyProductTotals);
					}
					dayWiseMap
					dayWiseMap.put(partyId,)*/
				}
			}
			
			Debug.log("====>productId ===!"+productId+"====partyId=="+partyId);
			// Handle product totals
			if (productTotals.get(productId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();
				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("basicRevenue", basicRevenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("bedRevenue", bedRevenue);
				newMap.put("cstRevenue", cstRevenue);
				productTotals.put(productId, newMap);
			} 
			else {
				Map productMap = (Map) productTotals.get(productId);
				BigDecimal runningTotal = (BigDecimal) productMap.get("total");
				runningTotal = runningTotal.add(quantity);
				productMap.put("total", runningTotal);
				
				BigDecimal runningTotalBasicRevenue = (BigDecimal) productMap.get("basicRevenue");
				runningTotalBasicRevenue = runningTotalBasicRevenue.add(basicRevenue);
				productMap.put("basicRevenue", runningTotalBasicRevenue);
				
				BigDecimal runningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				productMap.put("totalRevenue", runningTotalRevenue);
				BigDecimal runningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				productMap.put("packetQuantity", runningPacketTotal);
				BigDecimal runningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				productMap.put("vatRevenue", runningVatRevenue);
				
				BigDecimal runningBedRevenue = (BigDecimal) productMap.get("bedRevenue");
				runningBedRevenue = runningBedRevenue.add(bedRevenue);
				productMap.put("bedRevenue", runningBedRevenue);
				
				BigDecimal runningCstRevenue = (BigDecimal) productMap.get("cstRevenue");
				runningCstRevenue = runningCstRevenue.add(cstRevenue);
				productMap.put("cstRevenue", runningCstRevenue);

			}
		}
	   if (invoiceItemsIter != null) {
           try {
        	   invoiceItemsIter.close();
           } catch (GenericEntityException e) {
               Debug.logWarning(e, module);
           }
       }
		totalQuantity = totalQuantity.setScale(decimals, rounding);
		totalRevenue = totalRevenue.setScale(decimals, rounding);
		totalBasicRevenue=totalBasicRevenue.setScale(decimals, rounding);
		totalPacket = totalPacket.setScale(decimals, rounding);
		totalVatRevenue = totalVatRevenue.setScale(decimals, rounding);
	    totalBedRevenue = totalBedRevenue.setScale(decimals, rounding);
		totalCstRevenue = totalCstRevenue.setScale(decimals, rounding);
	
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
			
			tempVal = (BigDecimal) productValue.get("basicRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("basicRevenue", tempVal);
			
			tempVal = (BigDecimal) productValue.get("vatRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("vatRevenue", tempVal);
			
			tempVal = (BigDecimal) productValue.get("bedRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("bedRevenue", tempVal);
			
			tempVal = (BigDecimal) productValue.get("cstRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("cstRevenue", tempVal);
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("totalQuantity", totalQuantity);
		result.put("totalRevenue", totalRevenue);
		result.put("totalBasicRevenue", totalBasicRevenue);
		result.put("totalVatRevenue", totalVatRevenue);
		result.put("totalBedRevenue", totalBedRevenue);
		result.put("totalCstRevenue", totalCstRevenue);
		result.put("totalPacket", totalPacket);
		result.put("partyTotals", partyTotals);
		result.put("dayWiseTotals", dayWiseTotals);
		result.put("productTotals", productTotals);
		
		return result;
	}
	/**
	 * Get the sales order totals for the given period. The totals are also
	 * segmented into products and zones for reporting purposes
	* Get the sales order totals for the given period.  The totals are also segmented into products  for
	     * reporting purposes
	     * @param ctx the dispatch context
	     * @param context context map
	 * @return totals map
	 * 
	* ::TODO:: consolidate  getPeriodSalesInvoiceTotals functions
	 */
	public static Map<String, Object> getPeriodSalesInvoiceTotals(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
	    List<String> partyIds = (List<String>) context.get("partyIds");
		//List<String> shipmentIds = (List<String>) context.get("shipmentIds");
	    boolean isQuantityLtrs = Boolean.FALSE;
		if(UtilValidate.isNotEmpty(context.get("isQuantityLtrs"))){
			isQuantityLtrs = (Boolean)context.get("isQuantityLtrs");
		}
		 boolean isPurchaseInvoice = Boolean.FALSE;
			if(UtilValidate.isNotEmpty(context.get("isPurchaseInvoice"))){
				isPurchaseInvoice = (Boolean)context.get("isPurchaseInvoice");
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
		Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		
		//List<GenericValue> orderItems = FastList.newInstance();
		EntityListIterator invoiceItemsIter = null;
		Map productAttributes = new TreeMap<String, Object>();
		List productSubscriptionTypeList = FastList.newInstance();
		Map<String, String> dayWiseSaleMap = FastMap.newInstance();
		List adjustmentOrderList = FastList.newInstance();
		try {
			// lets populate sales date  Map
			int intervalDays = (UtilDateTime.getIntervalInDays(fromDate,thruDate)) + 1;
			/*for (int i = 0; i < intervalDays; i++) {
				Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDate,i);
				dayWiseSaleMap.put(UtilDateTime.toDateString(saleDate, "yyyy-MM-dd"),null);
			}
			Debug.log("====>dayWiseSaleMap ===!"+dayWiseSaleMap);*/
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			if(isPurchaseInvoice){
				conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
				if (UtilValidate.isNotEmpty(partyIds)) {
					conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIds));
				}/*else{
					conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, null));
					Debug.log("====>Given partyIds list is empty and then Result returned from Service is Empty  ===!");
					Debug.logError("partyIds cannot be empty", module);
					return ServiceUtil.returnError("partyIds cannot be empty");
				}*/
			}else{//no need to send Purchase Invoice
				conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"SALES_INVOICE"));
				conditionList.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,"Company"));
				if (UtilValidate.isNotEmpty(partyIds)) {
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
				}/*else{
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null));
					Debug.log("====>Given partyIds list is empty and then Result returned from Service is Empty  ===!");
					Debug.logError("partyIds cannot be empty", module);
					return ServiceUtil.returnError("partyIds cannot be empty");
				}*/
			}
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			List<String> orderBy = UtilMisc.toList("invoiceDate","partyId");
			/*if (orderByBankName) {
				orderBy = UtilMisc.toList("issuingAuthority", "facilityId","-lastModifiedDate");
			}*/
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null,null,orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		BigDecimal totalQuantity = ZERO;
		BigDecimal totalRevenue = ZERO;
		BigDecimal totalBasicRevenue = ZERO;
		BigDecimal totalPacket = ZERO;
		BigDecimal totalFat = ZERO;
		BigDecimal totalSnf = ZERO;
		BigDecimal totalVatRevenue = ZERO;
		BigDecimal totalBedRevenue = ZERO;
		BigDecimal totalCstRevenue = ZERO;

		Map<String, Object> partyTotals = new TreeMap<String, Object>();
		Map<String, Object> invoiceIdTotals = new TreeMap<String, Object>();
		Map<String, Object> productTotals = new TreeMap<String, Object>();
		Map<String, Object> supplyTypeTotals = new TreeMap<String, Object>();
		Map<String, Object> dayWiseTotals = new TreeMap<String, Object>();
	   GenericValue invoiceItem;   
	   while( invoiceItemsIter != null && (invoiceItem = invoiceItemsIter.next()) != null) {
			String prodSubscriptionTypeId = invoiceItem.getString("productSubscriptionTypeId");
			BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
			BigDecimal packetQuantity = invoiceItem.getBigDecimal("quantity");
			BigDecimal price = invoiceItem.getBigDecimal("unitListPrice");
			
			BigDecimal basicPrice = invoiceItem.getBigDecimal("unitPrice");
			BigDecimal basicRevenue = basicPrice.multiply(quantity);
			totalBasicRevenue=totalBasicRevenue.add(basicRevenue);
			
			BigDecimal revenue = price.multiply(quantity);
			Debug.log("==FIRSTTTTTTT=>revenue ===!"+revenue+"====partyId=="+invoiceItem.getString("partyId")+"==invoiceId=="+invoiceItem.getString("invoiceId"));
		/*	if (!(adjustmentOrderList.contains(invoiceItem.getString("orderId")))	&& (prodSubscriptionTypeId.equals("EMP_SUBSIDY"))) {
				try {
					List<GenericValue> adjustemntsList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,invoiceItem.getString("orderId")), null,null, null, false);
					for (GenericValue adjustemnt : adjustemntsList) {
						revenue = revenue.add(adjustemnt.getBigDecimal("amount"));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				adjustmentOrderList.add(invoiceItem.getString("orderId"));
			}*/
			totalRevenue = totalRevenue.add(revenue);
			totalPacket = totalPacket.add(packetQuantity);
			
			BigDecimal vatAmount = ZERO;
			if (UtilValidate.isNotEmpty(invoiceItem.getBigDecimal("vatAmount"))) {
				vatAmount = invoiceItem.getBigDecimal("vatAmount");
			}
			BigDecimal vatRevenue = vatAmount.multiply(quantity);
			totalVatRevenue = totalVatRevenue.add(vatRevenue);
			
			BigDecimal bedAmount = ZERO;
			if (UtilValidate.isNotEmpty(invoiceItem.getBigDecimal("bedAmount"))) {
				bedAmount = invoiceItem.getBigDecimal("bedAmount");
			}
			BigDecimal bedRevenue = bedAmount.multiply(quantity);
			totalBedRevenue = totalBedRevenue.add(bedRevenue);
			
			BigDecimal cstAmount = ZERO;
			if (UtilValidate.isNotEmpty(invoiceItem.getBigDecimal("cstAmount"))) {
				cstAmount = invoiceItem.getBigDecimal("cstAmount");
			}
			BigDecimal cstRevenue = cstAmount.multiply(quantity);
			totalCstRevenue = totalCstRevenue.add(cstRevenue);
			
			//String productName = invoiceItem.getString("productName");
			String productId = invoiceItem.getString("productId");
			if(isQuantityLtrs){
			   try{
				    GenericValue productDetails = delegator.findOne("Product",UtilMisc.toMap("productId", productId), true);
					if(UtilValidate.isNotEmpty(productDetails.getBigDecimal("quantityIncluded"))) {
					quantity = quantity.multiply(productDetails.getBigDecimal("quantityIncluded"));
					}
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
				}
			}
			totalQuantity = totalQuantity.add(quantity);
			BigDecimal fat = ZERO;
			BigDecimal snf = ZERO;
		
			String partyId = "";
			partyId = invoiceItem.getString("partyId");
			
			if (partyTotals.get(partyId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();

				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("basicRevenue", basicRevenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("bedRevenue", bedRevenue);
				newMap.put("cstRevenue", cstRevenue);
				
				Map<String, Object> productItemMap = FastMap.newInstance();
				productItemMap.put("total", quantity);
				productItemMap.put("packetQuantity", packetQuantity);
				productItemMap.put("basicRevenue", basicRevenue);
				productItemMap.put("totalRevenue", revenue);
				productItemMap.put("vatRevenue", vatRevenue);
				productItemMap.put("bedRevenue", bedRevenue);
				productItemMap.put("cstRevenue", cstRevenue);
				Map<String, Object> productMap = FastMap.newInstance();
				productMap.put(productId, productItemMap);
				
				newMap.put("productTotals", productMap);
				partyTotals.put(partyId, newMap);
				Debug.log("====>productId=IN-IFFFF!"+productId+"====partyId=="+partyId);
			}else {
				Map partyMap = (Map) partyTotals.get(partyId);
				BigDecimal runningTotal = (BigDecimal) partyMap.get("total");
				runningTotal = runningTotal.add(quantity);
				partyMap.put("total", runningTotal);

				BigDecimal runningPacketTotal = (BigDecimal) partyMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				partyMap.put("packetQuantity", runningPacketTotal);

				
				BigDecimal runningTotalBasicRevenue = (BigDecimal) partyMap.get("basicRevenue");
				runningTotalBasicRevenue = runningTotalBasicRevenue.add(basicRevenue);
				partyMap.put("basicRevenue", runningTotalBasicRevenue);
				
				BigDecimal runningTotalRevenue = (BigDecimal) partyMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				partyMap.put("totalRevenue", runningTotalRevenue);

				BigDecimal runningVatRevenue = (BigDecimal) partyMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				partyMap.put("vatRevenue", runningVatRevenue);
				BigDecimal runningBedRevenue = (BigDecimal) partyMap.get("bedRevenue");
				runningBedRevenue = runningBedRevenue.add(bedRevenue);
				partyMap.put("bedRevenue", runningBedRevenue);
				BigDecimal runningCstRevenue = (BigDecimal) partyMap.get("cstRevenue");
				runningCstRevenue = runningCstRevenue.add(cstRevenue);
				partyMap.put("cstRevenue", runningCstRevenue);
				
				// next handle product totals
				Map partyProductTotals = (Map) partyMap.get("productTotals");
				Map productMap = (Map) partyProductTotals.get(productId);
				if (UtilValidate.isEmpty(productMap)) {
					Map<String, Object> productItemMap = FastMap.newInstance();
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("basicRevenue", basicRevenue);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);
					productItemMap.put("bedRevenue", bedRevenue);
					productItemMap.put("cstRevenue", cstRevenue);
					partyProductTotals.put(productId, productItemMap);

				} else {
					BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
					productRunningTotal = productRunningTotal.add(quantity);
					productMap.put("total", productRunningTotal);
					
					BigDecimal productRunningBasicRevenue = (BigDecimal) productMap.get("basicRevenue");
					productRunningBasicRevenue = productRunningBasicRevenue.add(basicRevenue);
					productMap.put("basicRevenue", productRunningBasicRevenue);
					
					BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
					productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
					productMap.put("totalRevenue", productRunningTotalRevenue);

					BigDecimal productRunningPacketTotals = (BigDecimal) productMap.get("packetQuantity");
					productRunningPacketTotals = productRunningPacketTotals.add(packetQuantity);
					productMap.put("packetQuantity", productRunningPacketTotals);

					BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
					productRunningVatRevenue = productRunningVatRevenue.add(productRunningVatRevenue);
					productMap.put("vatRevenue", productRunningVatRevenue);
					
					BigDecimal prodRunningBedRevenue = (BigDecimal) productMap.get("bedRevenue");
					prodRunningBedRevenue = prodRunningBedRevenue.add(bedRevenue);
					productMap.put("bedRevenue", prodRunningBedRevenue);
					
					BigDecimal prodRunningCstRevenue = (BigDecimal) productMap.get("cstRevenue");
					prodRunningCstRevenue = prodRunningCstRevenue.add(cstRevenue);
					productMap.put("cstRevenue", prodRunningCstRevenue);
					
					partyProductTotals.put(productId, productMap);
				}
				Debug.log("====>productId=ELSE!"+productId+"====partyId=="+partyId);
			}
			
			
			//invoiceId Wise Totals
			String invoiceId = "";
			invoiceId = invoiceItem.getString("invoiceId");
			
			//String currentSaleDate=UtilDateTime.toDateString(invoiceItem.getTimestamp("invoiceDate"), "yyyy-MM-dd");
			
			if (invoiceIdTotals.get(invoiceId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();
				newMap.put("invoiceDate", invoiceItem.getTimestamp("invoiceDate"));
				newMap.put("invoiceDateStr", UtilDateTime.toDateString(invoiceItem.getTimestamp("invoiceDate"), "dd-MMM-yyyy"));
				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("basicRevenue", basicRevenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("bedRevenue", bedRevenue);
				newMap.put("cstRevenue", cstRevenue);
				
				Map<String, Object> productItemMap = FastMap.newInstance();
				productItemMap.put("total", quantity);
				productItemMap.put("packetQuantity", packetQuantity);
				productItemMap.put("basicRevenue", basicRevenue);
				productItemMap.put("totalRevenue", revenue);
				productItemMap.put("vatRevenue", vatRevenue);
				productItemMap.put("bedRevenue", bedRevenue);
				productItemMap.put("cstRevenue", cstRevenue);
				Map<String, Object> productMap = FastMap.newInstance();
				productMap.put(productId, productItemMap);
				
				newMap.put("productTotals", productMap);
				invoiceIdTotals.put(invoiceId, newMap);
			}else {
				Map invoiceMap = (Map) invoiceIdTotals.get(invoiceId);
				BigDecimal runningTotal = (BigDecimal) invoiceMap.get("total");
				runningTotal = runningTotal.add(quantity);
				invoiceMap.put("total", runningTotal);

				BigDecimal runningPacketTotal = (BigDecimal) invoiceMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				invoiceMap.put("packetQuantity", runningPacketTotal);

				
				BigDecimal runningTotalBasicRevenue = (BigDecimal) invoiceMap.get("basicRevenue");
				runningTotalBasicRevenue = runningTotalBasicRevenue.add(basicRevenue);
				invoiceMap.put("basicRevenue", runningTotalBasicRevenue);
				
				BigDecimal runningTotalRevenue = (BigDecimal) invoiceMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				invoiceMap.put("totalRevenue", runningTotalRevenue);

				BigDecimal runningVatRevenue = (BigDecimal) invoiceMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				invoiceMap.put("vatRevenue", runningVatRevenue);
				BigDecimal runningBedRevenue = (BigDecimal) invoiceMap.get("bedRevenue");
				runningBedRevenue = runningBedRevenue.add(bedRevenue);
				invoiceMap.put("bedRevenue", runningBedRevenue);
				BigDecimal runningCstRevenue = (BigDecimal) invoiceMap.get("cstRevenue");
				runningCstRevenue = runningCstRevenue.add(cstRevenue);
				invoiceMap.put("cstRevenue", runningCstRevenue);
				
				// next handle product totals
				Map invProductTotals = (Map) invoiceMap.get("productTotals");
				Map productMap = (Map) invProductTotals.get(productId);
				if (UtilValidate.isEmpty(productMap)) {
					Map<String, Object> productItemMap = FastMap.newInstance();
					productItemMap.put("total", quantity);
					productItemMap.put("packetQuantity", packetQuantity);
					productItemMap.put("basicRevenue", basicRevenue);
					productItemMap.put("totalRevenue", revenue);
					productItemMap.put("vatRevenue", vatRevenue);
					productItemMap.put("bedRevenue", bedRevenue);
					productItemMap.put("cstRevenue", cstRevenue);
					invProductTotals.put(productId, productItemMap);

				} else {
					BigDecimal productRunningTotal = (BigDecimal) productMap.get("total");
					productRunningTotal = productRunningTotal.add(quantity);
					productMap.put("total", productRunningTotal);
					
					BigDecimal productRunningBasicRevenue = (BigDecimal) productMap.get("basicRevenue");
					productRunningBasicRevenue = productRunningBasicRevenue.add(basicRevenue);
					productMap.put("basicRevenue", productRunningBasicRevenue);
					
					BigDecimal productRunningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
					productRunningTotalRevenue = productRunningTotalRevenue.add(revenue);
					productMap.put("totalRevenue", productRunningTotalRevenue);

					BigDecimal productRunningPacketTotals = (BigDecimal) productMap.get("packetQuantity");
					productRunningPacketTotals = productRunningPacketTotals.add(packetQuantity);
					productMap.put("packetQuantity", productRunningPacketTotals);

					BigDecimal productRunningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
					productRunningVatRevenue = productRunningVatRevenue.add(productRunningVatRevenue);
					productMap.put("vatRevenue", productRunningVatRevenue);
					
					BigDecimal prodRunningBedRevenue = (BigDecimal) productMap.get("bedRevenue");
					prodRunningBedRevenue = prodRunningBedRevenue.add(bedRevenue);
					productMap.put("bedRevenue", prodRunningBedRevenue);
					
					BigDecimal prodRunningCstRevenue = (BigDecimal) productMap.get("cstRevenue");
					prodRunningCstRevenue = prodRunningCstRevenue.add(cstRevenue);
					productMap.put("cstRevenue", prodRunningCstRevenue);
					
					invProductTotals.put(productId, productMap);
				}
			}
			
			// Handle product totals
			if (productTotals.get(productId) == null) {
				Map<String, Object> newMap = FastMap.newInstance();
				newMap.put("total", quantity);
				newMap.put("packetQuantity", packetQuantity);
				newMap.put("totalRevenue", revenue);
				newMap.put("basicRevenue", basicRevenue);
				newMap.put("vatRevenue", vatRevenue);
				newMap.put("bedRevenue", bedRevenue);
				newMap.put("cstRevenue", cstRevenue);
				productTotals.put(productId, newMap);
			} 
			else {
				Map productMap = (Map) productTotals.get(productId);
				BigDecimal runningTotal = (BigDecimal) productMap.get("total");
				runningTotal = runningTotal.add(quantity);
				productMap.put("total", runningTotal);
				
				BigDecimal runningTotalBasicRevenue = (BigDecimal) productMap.get("basicRevenue");
				runningTotalBasicRevenue = runningTotalBasicRevenue.add(basicRevenue);
				productMap.put("basicRevenue", runningTotalBasicRevenue);
				
				BigDecimal runningTotalRevenue = (BigDecimal) productMap.get("totalRevenue");
				runningTotalRevenue = runningTotalRevenue.add(revenue);
				productMap.put("totalRevenue", runningTotalRevenue);
				BigDecimal runningPacketTotal = (BigDecimal) productMap.get("packetQuantity");
				runningPacketTotal = runningPacketTotal.add(packetQuantity);
				productMap.put("packetQuantity", runningPacketTotal);
				BigDecimal runningVatRevenue = (BigDecimal) productMap.get("vatRevenue");
				runningVatRevenue = runningVatRevenue.add(vatRevenue);
				productMap.put("vatRevenue", runningVatRevenue);
				
				BigDecimal runningBedRevenue = (BigDecimal) productMap.get("bedRevenue");
				runningBedRevenue = runningBedRevenue.add(bedRevenue);
				productMap.put("bedRevenue", runningBedRevenue);
				
				BigDecimal runningCstRevenue = (BigDecimal) productMap.get("cstRevenue");
				runningCstRevenue = runningCstRevenue.add(cstRevenue);
				productMap.put("cstRevenue", runningCstRevenue);

			}
		}
	   if (invoiceItemsIter != null) {
           try {
        	   invoiceItemsIter.close();
           } catch (GenericEntityException e) {
               Debug.logWarning(e, module);
           }
       }
		totalQuantity = totalQuantity.setScale(decimals, rounding);
		totalRevenue = totalRevenue.setScale(decimals, rounding);
		totalBasicRevenue=totalBasicRevenue.setScale(decimals, rounding);
		totalPacket = totalPacket.setScale(decimals, rounding);
		totalVatRevenue = totalVatRevenue.setScale(decimals, rounding);
	    totalBedRevenue = totalBedRevenue.setScale(decimals, rounding);
		totalCstRevenue = totalCstRevenue.setScale(decimals, rounding);
	
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
			
			tempVal = (BigDecimal) productValue.get("basicRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("basicRevenue", tempVal);
			
			tempVal = (BigDecimal) productValue.get("vatRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("vatRevenue", tempVal);
			
			tempVal = (BigDecimal) productValue.get("bedRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("bedRevenue", tempVal);
			
			tempVal = (BigDecimal) productValue.get("cstRevenue");
			tempVal = tempVal.setScale(decimals, rounding);
			productValue.put("cstRevenue", tempVal);
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("totalQuantity", totalQuantity);
		result.put("totalRevenue", totalRevenue);
		result.put("totalBasicRevenue", totalBasicRevenue);
		result.put("totalVatRevenue", totalVatRevenue);
		result.put("totalBedRevenue", totalBedRevenue);
		result.put("totalCstRevenue", totalCstRevenue);
		result.put("totalPacket", totalPacket);
		result.put("invoiceIdTotals", invoiceIdTotals);
		result.put("partyTotals", partyTotals);
		result.put("dayWiseTotals", dayWiseTotals);
		result.put("productTotals", productTotals);
		
		return result;
	}
}
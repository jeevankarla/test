package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.regex.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.io.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javolution.util.FastMap;
import javolution.util.FastList;
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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
public class ByProductReportServices {
	
	public static final String module = ByProductReportServices.class.getName();
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
	
    public static Map<String, Object> getPeriodSales(DispatchContext dctx, Map<String, ? extends Object> context ) {
    	
    	Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Map<String, Object> result = FastMap.newInstance();
	    Locale locale = (Locale) context.get("locale");
	    
        List<String> facilityList = (List<String>) context.get("facilityList");
        List<String> productList = (List<String>) context.get("productList");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp fromDate = UtilDateTime.getDayStart( (Timestamp)(context.get("fromDate")), TimeZone.getDefault(), locale); 
        Timestamp thruDate = UtilDateTime.getDayEnd( (Timestamp)(context.get("thruDate")), TimeZone.getDefault(), locale); 
    	
        if (UtilValidate.isEmpty(fromDate)) {
        	fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), locale);  	
        }        
        if (UtilValidate.isEmpty(thruDate)) {
        	thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), locale);          	
        } 
        List shipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
        
        String productStoreId = (String) ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
        if(UtilValidate.isEmpty(productList)){
        	productList = (List) ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
        }

        List conditionList= FastList.newInstance(); 
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
		if(UtilValidate.isNotEmpty(facilityList)){
			conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityList));
		}
    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
    	List <GenericValue> boothOrderItemsList = null;
    	try{
    		boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition,  null, null, null, false);
    	}catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError("Unable to get records from OrderHeaderItemProductShipmentAndFacility"+e, module);
    		return ServiceUtil.returnError("Unable to get records from OrderHeaderItemProductShipmentAndFacility "); 
		}
    	
    	Map prodSalesMap = FastMap.newInstance();
    	for(int i=0; i<productList.size(); i++){
    		
    		String productId = (String) productList.get(i);
			List prodOrderItemsList = EntityUtil.filterByAnd(boothOrderItemsList, UtilMisc.toMap("productId", productId)); 
			BigDecimal totalQty = BigDecimal.ZERO;
			BigDecimal totalValue = BigDecimal.ZERO;
			BigDecimal totalQtyInc = BigDecimal.ZERO;
			
			for(int j = 0; j < prodOrderItemsList.size(); j++){
				
				BigDecimal quantity =(BigDecimal) ((GenericValue)prodOrderItemsList.get(j)).get("quantity");
	    		String facilityId = (String) ((GenericValue)prodOrderItemsList.get(j)).get("originFacilityId");
	    		BigDecimal quantityIncluded = (BigDecimal) ((GenericValue)prodOrderItemsList.get(j)).get("quantityIncluded");
	    		
	    		Map<String, Object> salePriceResult;
                Map<String, Object> priceContext = FastMap.newInstance();
                priceContext.put("userLogin", userLogin);   
                priceContext.put("productStoreId", productStoreId);                    
                priceContext.put("productId", productId);
                priceContext.put("priceDate", fromDate);
                priceContext.put("facilityId", facilityId);
                
        		salePriceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);    
                if (ServiceUtil.isError(salePriceResult)) {
                    Debug.logWarning("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(salePriceResult), module);
            		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(salePriceResult));          	            
                }  
                BigDecimal price = BigDecimal.ZERO;
                BigDecimal vatAmt = BigDecimal.ZERO;
                
                if (UtilValidate.isNotEmpty(salePriceResult)) {
                	price = (BigDecimal) salePriceResult.get("totalPrice");
				}
                BigDecimal eachProductValue = price.multiply(quantity);
                BigDecimal prodQtyInc = quantityIncluded.multiply(quantity);
                
                totalQty = totalQty.add(quantity);
                totalValue = totalValue.add(eachProductValue);
                totalQtyInc = totalQtyInc.add(prodQtyInc);
              
			}
			
			Map salesDetailsMap = FastMap.newInstance();
			salesDetailsMap.put("productId", productId);
			salesDetailsMap.put("totalSaleQty", totalQty);
			salesDetailsMap.put("totalSaleValue", totalValue);
			salesDetailsMap.put("totalQtyInc", totalQtyInc);
			
			Map tempProdMap = FastMap.newInstance();
			tempProdMap.putAll(salesDetailsMap);
			
			prodSalesMap.put(productId, tempProdMap);
    	}
    	result.put("prodSalesMap", prodSalesMap);
        
        return result;
    }
    
	/* public static Map<String, Object> getByProductPeriodTotals(DispatchContext dctx, Map<String, ? extends Object> context ) {
		 	Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
	       
	        String categoryId = (String) context.get("categoryId");
	        String productCategoryTypeId = (String) context.get("productCategoryTypeId");
	        List<String> facilityList = (List<String>) context.get("facilityList");
	        List<String> productList = (List<String>) context.get("productList");
	        
	        if (UtilValidate.isEmpty(productCategoryTypeId)) {
	        	 productCategoryTypeId = "BYPROD_REPORTS";       	
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
	        
	        Map<String, Object> result = FastMap.newInstance();
	        List categoryIdsList = FastList.newInstance();
	        Map productWiseTotalsMap = FastMap.newInstance();
	        Map productWiseGrTotalsMap = FastMap.newInstance();
	        productWiseGrTotalsMap.put("quantity", BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("qtyInc" , BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("vatPercentage", BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("basicValue", BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("totalValue", BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("vatValue", BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("MRPBasicValue", BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("MRPTotalValue", BigDecimal.ZERO);
	        productWiseGrTotalsMap.put("MRPVatValue", BigDecimal.ZERO);
	        
	        List shipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
	    	String productStoreId = (String) ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	    	
	    	Map allProductsMap = (Map) ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId));
	    	Map categoryProductsMap = (Map) allProductsMap.get("categoryProduct");
	    	//List unionProductList = (List)categoryProductsMap.get("UNION_PRODUCTS");
	    	List conditionList= FastList.newInstance(); 
	    	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	    	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	    	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
	    	List catTypeList = UtilMisc.toList("REPLACEMENT_BYPROD","BYPROD_GIFT");
	    	conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_IN, catTypeList));
	    	if(UtilValidate.isNotEmpty(context.get("categoryTypeEnum"))){
	    		conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,context.get("categoryTypeEnum")));
	    	}
	    	if(UtilValidate.isNotEmpty(facilityList)){
				conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityList));
			}
	    	if(UtilValidate.isNotEmpty(productList)){
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
				categoryIdsList.add("prodListCat");
			}else{
	    		if(UtilValidate.isEmpty(categoryId)){
		    		categoryIdsList = (List) getByProdReportCategories(delegator, UtilMisc.toMap("productCategoryTypeId", productCategoryTypeId)).get("reportProductCategories");
		    	}else{
		    		categoryIdsList.add(categoryId);
		    	}
	    	}
	    	
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	
	    	List <GenericValue> boothOrderItemsList = null;
	    	try{
	    		Set fieldsToSelect = UtilMisc.toSet("productId", "quantity", "categoryTypeEnum", "originFacilityId", "ownerPartyId", "quantityIncluded");
	    		boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition,  fieldsToSelect, null, null, false);
	       	}catch (GenericEntityException e) {
	    		Debug.logError("Unable to get records from OrderHeaderItemProductShipmentAndFacility"+e, module);
	    		return ServiceUtil.returnError("Unable to get records from OrderHeaderItemProductShipmentAndFacility "); 
			}
	    	List activeProdList = EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "productId", true);
	    	
	    	==============GET PRICES BASED ON PARTY CLASSIFICATION=============
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
	    		Map productsPrice = (Map) ByProductReportServices.getByProductPricesForPartyClassification(dctx, UtilMisc.toMap("userLogin",userLogin,"partyClassificationId", partyClassifications.get(i))).get("productsPrice");
    	        classificationMap.put(partyClassifications.get(i), productsPrice);
	    	}
	    	==============END=============
	    	
	    	Map productCategoryMap = FastMap.newInstance();
	    	for(int i = 0; i < categoryIdsList.size(); i++){
	    		String category = (String) categoryIdsList.get(i);
	    		
	    		BigDecimal catSaleQty = BigDecimal.ZERO;
	    		BigDecimal catSaleQtyInc = BigDecimal.ZERO;
    			BigDecimal catSalebasicValue = BigDecimal.ZERO;
    			BigDecimal catSaleTotalValue = BigDecimal.ZERO;
    			BigDecimal catSaleVatValue = BigDecimal.ZERO;
    			
    			BigDecimal catMRPBasicValue = BigDecimal.ZERO;
    			BigDecimal catMRPTotalValue = BigDecimal.ZERO;
    			BigDecimal catMRPVatValue = BigDecimal.ZERO;
    			
    			BigDecimal catPurchasebasicValue = BigDecimal.ZERO;
    			BigDecimal catPurchaseVatValue = BigDecimal.ZERO;
    			
    			Map facCategoryTotalsMap = FastMap.newInstance();
    			Map facCategoryPurTotMap = FastMap.newInstance();
    			
    			List categoryProductsList = null;
    			if(category == "prodListCat"){
    				categoryProductsList = productList;
    			}else{
    				categoryProductsList = (List) categoryProductsMap.get(category);
    			}
	    		
	    		if(UtilValidate.isEmpty(categoryProductsList)){
	    			continue;
	    		}
	    		Map productMap = FastMap.newInstance();
	    		for(int j = 0; j < categoryProductsList.size(); j++ ){
	    			String productId = (String) categoryProductsList.get(j);
	    			if(!activeProdList.contains(productId)){
	    				continue;
	    			}
	    			
	    			List prodOrderItemsList = EntityUtil.filterByAnd(boothOrderItemsList, UtilMisc.toMap("productId", productId));
	    			BigDecimal totalQty = BigDecimal.ZERO;
	    			BigDecimal subTotalQtyInc = BigDecimal.ZERO;
	    			BigDecimal totalbasicValue = BigDecimal.ZERO;
	    			BigDecimal totalTotalValue = BigDecimal.ZERO;
	    			BigDecimal totalVatValue = BigDecimal.ZERO;
	    			BigDecimal vatPercentage = BigDecimal.ZERO;
	    			
	    			BigDecimal totalMRPBasicValue = BigDecimal.ZERO;
	    			BigDecimal totalMRPTotalValue = BigDecimal.ZERO;
	    			BigDecimal totalMRPVatValue = BigDecimal.ZERO;
	    			
	    			BigDecimal totalPurchaseBasicValue = BigDecimal.ZERO;
	    			BigDecimal totalPurchaseVatValue = BigDecimal.ZERO;
	    	    	
	    			Map saleAndPurchaseMap = FastMap.newInstance();
	    			Map saleCategoryMap = FastMap.newInstance();
	    			Map purchaseCategoryMap = FastMap.newInstance();
	    			if(UtilValidate.isEmpty(productWiseTotalsMap.get(productId))){
	    				Map productWiseMap = FastMap.newInstance();
	    				productWiseMap.put("quantity", BigDecimal.ZERO);
	    				productWiseMap.put("qtyInc", BigDecimal.ZERO);
	    				productWiseMap.put("vatPercentage", BigDecimal.ZERO);
	    				productWiseMap.put("basicValue", BigDecimal.ZERO);
	    				productWiseMap.put("totalValue", BigDecimal.ZERO);
	    				productWiseMap.put("vatValue", BigDecimal.ZERO);
	    				productWiseMap.put("MRPBasicValue", BigDecimal.ZERO);
	    				productWiseMap.put("MRPTotalValue", BigDecimal.ZERO);
	    				productWiseMap.put("MRPVatValue", BigDecimal.ZERO);
	    				productWiseTotalsMap.put(productId , productWiseMap);
	    			}
	    	    	for(int k = 0; k < prodOrderItemsList.size(); k++){
	    	    		BigDecimal quantity =(BigDecimal) ((GenericValue)prodOrderItemsList.get(k)).get("quantity");
	    	    		BigDecimal quantityIncluded = (BigDecimal) ((GenericValue)prodOrderItemsList.get(k)).get("quantityIncluded");
	    	    		BigDecimal totalQtyInc = quantity.multiply(quantityIncluded);
	    	    		
	    	    		
	    	    		String facilityCategory = (String) ((GenericValue)prodOrderItemsList.get(k)).get("categoryTypeEnum");
	    	    		if( (quantity.compareTo(BigDecimal.ZERO) <= 0) ||  UtilValidate.isEmpty(facilityCategory)){
	    	    			continue;
	    	    		}
	    	    		if(facilityCategory.equals("BYPROD_GIFT") || (facilityCategory.equals("BYPROD_SO")) || (facilityCategory.equals("SP_SALES"))){
	    	    			facilityCategory = "SOrderAndOthers";
	    	    		}
	    	    		
	    	    		String facilityId = (String) ((GenericValue)prodOrderItemsList.get(k)).get("originFacilityId");
	    	    		String ownerPartyId = (String) ((GenericValue)prodOrderItemsList.get(k)).get("ownerPartyId");
	    	    		String partyClassificationTypeId = null;
	    	    		
			     		List<GenericValue> partyClassification = null;
			     		try{
			     			List<GenericValue> partyClassificationGroup = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId), null, null, null, false);
			     			//partyClassificationGroup = EntityUtil.filterByDate(partyClassificationGroup, fromDate);
		    				if(UtilValidate.isNotEmpty(partyClassificationGroup)){
		    					partyClassificationTypeId = (String) EntityUtil.getFirst(partyClassificationGroup).get("partyClassificationGroupId");
		    				}
			     		}catch(GenericEntityException e){
			     			Debug.logError("No partyRole found for given partyId:"+ ownerPartyId, module);
			     			return ServiceUtil.returnError("No partyRole found for given partyId");
			     		}
	    	    		
	    	    		========================GET PRODUCT SALE TOTALS AND PRICES================================
	    	    		
			     		
			     			=========GET MRP PRICES========
		     		    Map MRPPriceMap = (Map) classificationMap.get("PM_RC_MRP");
			     		Map MRPProdPriceMap = (Map)MRPPriceMap.get(productId);
			     		BigDecimal MRPBasicPrice = (BigDecimal)MRPProdPriceMap.get("basicPrice");
			     		BigDecimal MRPTotalPrice = (BigDecimal)MRPProdPriceMap.get("totalAmount");
			     		BigDecimal MRPVatAmt = (BigDecimal)MRPProdPriceMap.get("VAT");
			     		
			     		BigDecimal MRPBasicValue = MRPBasicPrice.multiply(quantity);
	                    BigDecimal MRPTotalValue = MRPTotalPrice.multiply(quantity);
	                    BigDecimal MRPVatValue = MRPVatAmt.multiply(quantity);
	                    
	                    totalMRPBasicValue = totalMRPBasicValue.add(MRPBasicValue);
	                    totalMRPTotalValue = totalMRPTotalValue.add(MRPTotalValue);
	                    totalMRPVatValue = totalMRPVatValue.add(MRPVatValue);
	                    
	                    catMRPBasicValue = catMRPBasicValue.add(MRPBasicValue);
	        			catMRPTotalValue = catMRPTotalValue.add(MRPTotalValue);
	        			catMRPVatValue = catMRPVatValue.add(MRPVatValue);
			     		
	        			    =========GET BASIC PRICES========
			     		Map priceMap = (Map) classificationMap.get(partyClassificationTypeId);
			     		Map prodPriceMap = (Map)priceMap.get(productId);
			     		BigDecimal basicPrice = (BigDecimal)prodPriceMap.get("basicPrice");
			     		BigDecimal totalPrice = (BigDecimal)prodPriceMap.get("totalAmount");
			     		BigDecimal vatAmt = (BigDecimal)prodPriceMap.get("VAT");
			     		vatPercentage = (BigDecimal)prodPriceMap.get("vatPercentage");
			     		
			     		BigDecimal basicValue = basicPrice.multiply(quantity);
	                    BigDecimal totalValue = totalPrice.multiply(quantity);
	                    BigDecimal vatValue = vatAmt.multiply(quantity);	
	                    
	                    totalQty = totalQty.add(quantity);
	                    subTotalQtyInc = subTotalQtyInc.add(totalQtyInc);
	                    totalbasicValue = totalbasicValue.add(basicValue);
	                    totalTotalValue = totalTotalValue.add(totalValue);
	                    totalVatValue = totalVatValue.add(vatValue);
	                    
	                    catSaleQty = catSaleQty.add(quantity);
	                    catSaleQtyInc = catSaleQtyInc.add(totalQtyInc);
	                    catSalebasicValue = catSalebasicValue.add(basicValue);
	                    catSaleTotalValue = catSaleTotalValue.add(totalValue);
	        			catSaleVatValue = catSaleVatValue.add(vatValue);
	                    
	                    Map detailMap = FastMap.newInstance();
	                    detailMap.put("productId", productId);
	                    detailMap.put("quantity", quantity);
	                    detailMap.put("qtyInc", totalQtyInc);
	                    detailMap.put("vatPercentage", vatPercentage);
	                    detailMap.put("basicValue", basicValue);
	                    detailMap.put("totalValue", totalValue);
	                    detailMap.put("vatValue", vatValue);
	                    detailMap.put("MRPBasicValue", MRPBasicValue);
	                    detailMap.put("MRPTotalValue", MRPTotalValue);
	                    detailMap.put("MRPVatValue", MRPVatValue);
	                   
	                    Map tempDetailMap = FastMap.newInstance();
	    				tempDetailMap.putAll(detailMap);
	    				
	    				Map tempCatTotMap = FastMap.newInstance();
	    				tempCatTotMap.putAll(detailMap);
	    				// let' update productWiseTotalsMap here
	                    Map tempProductWiseMap = FastMap.newInstance();
	                    tempProductWiseMap = (Map)productWiseTotalsMap.get(productId);
	                    tempProductWiseMap.put("quantity" , quantity.add((BigDecimal)tempProductWiseMap.get("quantity")));
	                    tempProductWiseMap.put("qtyInc" , totalQtyInc.add((BigDecimal)tempProductWiseMap.get("qtyInc")));
	                    tempProductWiseMap.put("vatPercentage" , vatPercentage);
	                    tempProductWiseMap.put("basicValue" , basicValue.add((BigDecimal)tempProductWiseMap.get("basicValue")));
	                    tempProductWiseMap.put("totalValue" , totalValue.add((BigDecimal)tempProductWiseMap.get("totalValue")));
	                    tempProductWiseMap.put("vatValue" , vatValue.add((BigDecimal)tempProductWiseMap.get("vatValue")));
	                    tempProductWiseMap.put("MRPBasicValue" , MRPBasicValue.add((BigDecimal)tempProductWiseMap.get("MRPBasicValue")));
	                    tempProductWiseMap.put("MRPTotalValue" , MRPTotalValue.add((BigDecimal)tempProductWiseMap.get("MRPTotalValue")));
	                    tempProductWiseMap.put("MRPVatValue" , MRPVatValue.add((BigDecimal)tempProductWiseMap.get("MRPVatValue")));
	                    productWiseTotalsMap.put(productId, tempProductWiseMap);
	                    
	                    // let's update product wise grand total map
	                    productWiseGrTotalsMap.put("quantity" , quantity.add((BigDecimal)productWiseGrTotalsMap.get("quantity")));
	                    productWiseGrTotalsMap.put("qtyInc" , totalQtyInc.add((BigDecimal)productWiseGrTotalsMap.get("qtyInc")));
	                    productWiseGrTotalsMap.put("vatPercentage" , vatPercentage);
	                    productWiseGrTotalsMap.put("basicValue" , basicValue.add((BigDecimal)productWiseGrTotalsMap.get("basicValue")));
	                    productWiseGrTotalsMap.put("totalValue" , totalValue.add((BigDecimal)productWiseGrTotalsMap.get("totalValue")));
	                    productWiseGrTotalsMap.put("vatValue" , vatValue.add((BigDecimal)productWiseGrTotalsMap.get("vatValue")));
	                    productWiseGrTotalsMap.put("MRPBasicValue" , MRPBasicValue.add((BigDecimal)productWiseGrTotalsMap.get("MRPBasicValue")));
	                    productWiseGrTotalsMap.put("MRPTotalValue" , MRPTotalValue.add((BigDecimal)productWiseGrTotalsMap.get("MRPTotalValue")));
	                    productWiseGrTotalsMap.put("MRPVatValue" , MRPVatValue.add((BigDecimal)productWiseGrTotalsMap.get("MRPVatValue")));
	                   
	    				if(UtilValidate.isEmpty(saleCategoryMap.get(facilityCategory))){
	    					saleCategoryMap.put(facilityCategory, tempDetailMap);
	    				}else{
	    					Map updateDetailMap = (Map) saleCategoryMap.get(facilityCategory);
	    					BigDecimal updateQty = (BigDecimal) updateDetailMap.get("quantity");
	    					updateQty = updateQty.add(quantity);
	    					
	    					BigDecimal updateQtyInc = (BigDecimal) updateDetailMap.get("qtyInc");
	    					updateQtyInc = updateQtyInc.add(totalQtyInc);
	    					
	    					BigDecimal updateBasicValue = (BigDecimal) updateDetailMap.get("basicValue");
	    					updateBasicValue = updateBasicValue.add(basicValue);
	    					
	    					BigDecimal updateTotalValue = (BigDecimal) updateDetailMap.get("totalValue");
	    					updateTotalValue = updateTotalValue.add(totalValue);
	    					
	    					BigDecimal updateVatValue = (BigDecimal) updateDetailMap.get("vatValue");
	    					updateVatValue = updateVatValue.add(vatValue);
	    					
	    					BigDecimal updateMRPValue = (BigDecimal) updateDetailMap.get("MRPBasicValue");
	    					updateMRPValue = updateMRPValue.add(basicValue);
	    					
	    					BigDecimal updateTotalMRPValue = (BigDecimal) updateDetailMap.get("MRPTotalValue");
	    					updateTotalMRPValue = updateTotalMRPValue.add(totalValue);
	    					
	    					BigDecimal updateMRPVatValue = (BigDecimal) updateDetailMap.get("MRPVatValue");
	    					updateMRPVatValue = updateMRPVatValue.add(vatValue);
	    					
	    					updateDetailMap.put("quantity", updateQty);
	    					updateDetailMap.put("qtyInc", updateQtyInc);
	    					updateDetailMap.put("basicValue", updateBasicValue);
	    					updateDetailMap.put("totalValue", updateTotalValue);
	    					updateDetailMap.put("vatValue", updateVatValue);
	    					updateDetailMap.put("MRPBasicValue", updateMRPValue);
	    					updateDetailMap.put("MRPTotalValue", updateTotalMRPValue);
	    					updateDetailMap.put("MRPVatValue", updateMRPVatValue);
	    					
	    					Map tempUpdateMap = FastMap.newInstance();
	    					tempUpdateMap.putAll(updateDetailMap);
	    					saleCategoryMap.put(facilityCategory, tempUpdateMap);
	    				}
	    				
	    				if(UtilValidate.isEmpty(facCategoryTotalsMap.get(facilityCategory))){
	    					facCategoryTotalsMap.put(facilityCategory, tempCatTotMap);
	    				}else{
	    					Map updateDetailMap = (Map) facCategoryTotalsMap.get(facilityCategory);
	    					BigDecimal updateQty = (BigDecimal) updateDetailMap.get("quantity");
	    					updateQty = updateQty.add(quantity);
	    					
	    					BigDecimal updateQtyInc = (BigDecimal) updateDetailMap.get("qtyInc");
	    					updateQtyInc = updateQtyInc.add(totalQtyInc);
	    					
	    					BigDecimal updateBasicValue = (BigDecimal) updateDetailMap.get("basicValue");
	    					updateBasicValue = updateBasicValue.add(basicValue);
	    					
	    					BigDecimal updateTotalValue = (BigDecimal) updateDetailMap.get("totalValue");
	    					updateTotalValue = updateTotalValue.add(totalValue);
	    					
	    					BigDecimal updateVatValue = (BigDecimal) updateDetailMap.get("vatValue");
	    					updateVatValue = updateVatValue.add(vatValue);
	    					
	    					BigDecimal updateMRPValue = (BigDecimal) updateDetailMap.get("MRPBasicValue");
	    					updateMRPValue = updateMRPValue.add(basicValue);
	    					
	    					BigDecimal updateTotalMRPValue = (BigDecimal) updateDetailMap.get("MRPTotalValue");
	    					updateTotalMRPValue = updateTotalMRPValue.add(totalValue);
	    					
	    					BigDecimal updateMRPVatValue = (BigDecimal) updateDetailMap.get("MRPVatValue");
	    					updateMRPVatValue = updateMRPVatValue.add(vatValue);
	    					
	    					updateDetailMap.put("quantity", updateQty);
	    					updateDetailMap.put("qtyInc", updateQtyInc);
	    					updateDetailMap.put("basicValue", updateBasicValue);
	    					updateDetailMap.put("totalValue", updateTotalValue);
	    					updateDetailMap.put("vatValue", updateVatValue);
	    					updateDetailMap.put("MRPBasicValue", updateMRPValue);
	    					updateDetailMap.put("MRPTotalValue", updateTotalMRPValue);
	    					updateDetailMap.put("MRPVatValue", updateMRPVatValue);
	    					
	    					Map tempUpdateMap = FastMap.newInstance();
	    					tempUpdateMap.putAll(updateDetailMap);
	    					facCategoryTotalsMap.put(facilityCategory, tempUpdateMap);
	    				}
	    				========================GET PURCHASE TOTALS AND PRICES================================
	    				if(!unionProductList.contains(productId)){
	    					quantity = BigDecimal.ZERO;
	    					continue;
	    				}
	    				
	    				Map UTPPriceMap = (Map) classificationMap.get("PM_RC_U");
			     		Map UTPProdPriceMap = (Map)UTPPriceMap.get(productId);
			     		BigDecimal purchaseBasicPrice = (BigDecimal)UTPProdPriceMap.get("basicPrice");
			     		BigDecimal purchaseVatAmt = (BigDecimal)UTPProdPriceMap.get("VAT");
			     		
	                    BigDecimal purchaseBasicValue = purchaseBasicPrice.multiply(quantity);
	                    BigDecimal purchaseVatValue = purchaseVatAmt.multiply(quantity);
	                    
	                    totalPurchaseBasicValue = totalPurchaseBasicValue.add(purchaseBasicValue);
	                    totalPurchaseVatValue = totalPurchaseVatValue.add(purchaseVatValue);
	                    
	                    catPurchasebasicValue = catPurchasebasicValue.add(purchaseBasicValue);
	        		    catPurchaseVatValue = catPurchaseVatValue.add(purchaseVatValue);
	                    
	                    Map purchaseDetailMap = FastMap.newInstance();
	                    purchaseDetailMap.put("productId", productId);
	                    purchaseDetailMap.put("quantity", quantity);
	                    purchaseDetailMap.put("vatPercentage", vatPercentage);
	                    purchaseDetailMap.put("basicValue", purchaseBasicValue);
	                    purchaseDetailMap.put("vatValue", purchaseVatValue);
	                    
	                    Map tempPurchaseDetailMap = FastMap.newInstance();
	                    tempPurchaseDetailMap.putAll(purchaseDetailMap);
	                    
	                    Map tempCatPurchaseTotMap = FastMap.newInstance();
	                    tempCatPurchaseTotMap.putAll(purchaseDetailMap);
	                    
	    				if(UtilValidate.isEmpty(purchaseCategoryMap.get(facilityCategory))){
	    					purchaseCategoryMap.put(facilityCategory, tempPurchaseDetailMap);
	    				}else{
	    					Map updatePurchaseDetailMap = (Map) purchaseCategoryMap.get(facilityCategory);
	    					BigDecimal updateQty = (BigDecimal) updatePurchaseDetailMap.get("quantity");
	    					updateQty = updateQty.add(quantity);
	    					
	    					BigDecimal updateBasicValue = (BigDecimal) updatePurchaseDetailMap.get("basicValue");
	    					updateBasicValue = updateBasicValue.add(purchaseBasicValue);
	    					
	    					BigDecimal updateVatValue = (BigDecimal) updatePurchaseDetailMap.get("vatValue");
	    					updateVatValue = updateVatValue.add(purchaseVatValue);
	    					
	    					updatePurchaseDetailMap.put("quantity", updateQty);
	    					updatePurchaseDetailMap.put("basicValue", updateBasicValue);
	    					updatePurchaseDetailMap.put("vatValue", updateVatValue);
	    					
	    					Map tempPurchaseUpdateMap = FastMap.newInstance();
	    					tempPurchaseUpdateMap.putAll(updatePurchaseDetailMap);
	    					purchaseCategoryMap.put(facilityCategory, tempPurchaseUpdateMap);
	    				}
	    				
	    				if(UtilValidate.isEmpty(facCategoryPurTotMap.get(facilityCategory))){
	    					facCategoryPurTotMap.put(facilityCategory, tempCatPurchaseTotMap);
	    				}else{
	    					Map updateDetailMap = (Map) facCategoryPurTotMap.get(facilityCategory);
	    					BigDecimal updateQty = (BigDecimal) updateDetailMap.get("quantity");
	    					updateQty = updateQty.add(quantity);
	    					
	    					BigDecimal updateBasicValue = (BigDecimal) updateDetailMap.get("basicValue");
	    					updateBasicValue = updateBasicValue.add(purchaseBasicValue);
	    					
	    					BigDecimal updateVatValue = (BigDecimal) updateDetailMap.get("vatValue");
	    					updateVatValue = updateVatValue.add(purchaseVatValue);
	    					
	    					updateDetailMap.put("quantity", updateQty);
	    					updateDetailMap.put("basicValue", updateBasicValue);
	    					updateDetailMap.put("vatValue", updateVatValue);
	    					
	    					Map tempUpdateMap = FastMap.newInstance();
	    					tempUpdateMap.putAll(updateDetailMap);
	    					facCategoryPurTotMap.put(facilityCategory, tempUpdateMap);
	    				}
	                    
	    	    	}
	    	    	if( totalQty.compareTo(BigDecimal.ZERO) <= 0 ){
    	    			continue;
    	    		}
	    	    	========================POPULATE SALES TOTALS================================
	    	    	Map productTotalsMap = FastMap.newInstance();
	    	    	productTotalsMap.put("quantity", totalQty);
	    	    	productTotalsMap.put("qtyInc", subTotalQtyInc);
	    	    	productTotalsMap.put("basicValue", totalbasicValue);
	    	    	productTotalsMap.put("totalValue", totalTotalValue);
	    	    	productTotalsMap.put("vatValue", totalVatValue);
	    	    	productTotalsMap.put("vatPercentage", vatPercentage);
	    	    	productTotalsMap.put("MRPBasicValue", totalMRPBasicValue);
	    	    	productTotalsMap.put("MRPTotalValue", totalMRPTotalValue);
	    	    	productTotalsMap.put("MRPVatValue", totalMRPVatValue);
	    	    	
	    	    	Map tempProductTotalsMap = FastMap.newInstance();
	    	    	tempProductTotalsMap.putAll(productTotalsMap);
	    	    	
	    	    	Map tempSaleCategoryTotals = FastMap.newInstance();
	    	    	tempSaleCategoryTotals.putAll(saleCategoryMap);
	    	    	
	    	    	Map supplyMap = FastMap.newInstance();
	    	    	supplyMap.put("totals", tempProductTotalsMap);
	    	    	supplyMap.put("CategoryWiseTotals", tempSaleCategoryTotals);
	    	    	
	    	    	Map tempSupplyMap = FastMap.newInstance();
	    	    	tempSupplyMap.putAll(supplyMap);
	    	    	
	    	    	saleAndPurchaseMap.put("sale", tempSupplyMap); 
	    	    	
	    	    	========================POPULATE PURCHASE TOTALS================================
	    	    	Map productPurchaseTotalsMap = FastMap.newInstance();
	    	    	
	    	    	productPurchaseTotalsMap.put("quantity", totalQty);
	    	    	productPurchaseTotalsMap.put("qtyInc", subTotalQtyInc);
	    	    	productPurchaseTotalsMap.put("basicValue", totalPurchaseBasicValue);
	    	    	productPurchaseTotalsMap.put("vatPercentage", vatPercentage);
	    	    	productPurchaseTotalsMap.put("vatValue", totalPurchaseVatValue);
	    	    	
	    	    	if(!unionProductList.contains(productId)){
	    	    		productPurchaseTotalsMap.put("quantity", BigDecimal.ZERO);
	    	    		productPurchaseTotalsMap.put("qtyInc", BigDecimal.ZERO);
		    	    	productPurchaseTotalsMap.put("basicValue", BigDecimal.ZERO);
		    	    	productPurchaseTotalsMap.put("vatPercentage", BigDecimal.ZERO);
		    	    	productPurchaseTotalsMap.put("vatValue", BigDecimal.ZERO);
    				}
	    	    	
	    	    	Map tempproductPurchaseTotalsMap = FastMap.newInstance();
	    	    	tempproductPurchaseTotalsMap.putAll(productPurchaseTotalsMap);
	    	    	
	    	    	Map tempPurchaseCategoryTotals = FastMap.newInstance();
	    	    	tempPurchaseCategoryTotals.putAll(purchaseCategoryMap);
	    	    	
	    	    	Map purchaseSupplyMap = FastMap.newInstance();
	    	    	purchaseSupplyMap.put("totals", tempproductPurchaseTotalsMap);
	    	    	purchaseSupplyMap.put("CategoryWiseTotals", tempPurchaseCategoryTotals);
	    	    	
	    	    	Map tempPurchaseSupplyMap = FastMap.newInstance();
	    	    	tempPurchaseSupplyMap.putAll(purchaseSupplyMap);
	    	    	
	    	    	saleAndPurchaseMap.put("purchase", tempPurchaseSupplyMap);
	    	    	
	    	    	Map tempProductMap = FastMap.newInstance();
	    	    	tempProductMap.putAll(saleAndPurchaseMap);
	    	    	
	    	    	productMap.put(productId, tempProductMap);
	    		}
	    		if(UtilValidate.isEmpty(productMap)){
	    			continue;
	    		}
	    		
	    		================POPULATE CATEGORY-WISE SALE AND PURCHASE TOTALS================
    	    	Map prodCategoryTotalsMap = FastMap.newInstance();
    	    	prodCategoryTotalsMap.put("catSaleQty", catSaleQty);
    	    	prodCategoryTotalsMap.put("catSaleQtyInc", catSaleQtyInc);
    	    	prodCategoryTotalsMap.put("catSalebasicValue", catSalebasicValue);
    	    	prodCategoryTotalsMap.put("catSaleTotalValue", catSaleTotalValue);
    	    	prodCategoryTotalsMap.put("catSaleVatValue", catSaleVatValue);
    	    	prodCategoryTotalsMap.put("catMRPBasicValue", catMRPBasicValue);
    	    	prodCategoryTotalsMap.put("catMRPTotalValue", catMRPTotalValue);
    	    	prodCategoryTotalsMap.put("catMRPVatValue", catMRPVatValue);
    	    	
    	    	Map tempProdCategoryTotalsMap = FastMap.newInstance();
    	    	tempProdCategoryTotalsMap.putAll(prodCategoryTotalsMap);
    	    	
    	    	Map tempFacCategoryTotals = FastMap.newInstance();
    	    	tempFacCategoryTotals.putAll(facCategoryTotalsMap);
    	    	
    	    	Map tempFacCatAndProdCatMap = FastMap.newInstance();
    	    	tempFacCatAndProdCatMap.put("totals", tempProdCategoryTotalsMap);
    	    	tempFacCatAndProdCatMap.put("CategoryWiseTotals", tempFacCategoryTotals);
    	    	
    	    	Map tempProdFacCatMap = FastMap.newInstance();
    	    	tempProdFacCatMap.putAll(tempFacCatAndProdCatMap);
    	    	
    	    	Map prodCatPurchaseTotalsMap = FastMap.newInstance();
    	    	prodCatPurchaseTotalsMap.put("catPurchasebasicValue", catPurchasebasicValue);
    	    	prodCatPurchaseTotalsMap.put("catPurchaseVatValue", catPurchaseVatValue);
    	    	
    	    	Map tempProdPurTotalsMap = FastMap.newInstance();
    	    	tempProdPurTotalsMap.putAll(prodCatPurchaseTotalsMap);
    	    	
    	    	Map tempFacCategoryPurTotMap = FastMap.newInstance();
    	    	tempFacCategoryPurTotMap.putAll(facCategoryPurTotMap);
    	    	
    	    	Map tempFacCatPurchasesMap = FastMap.newInstance();
    	    	tempFacCatPurchasesMap.put("totals", tempProdPurTotalsMap);
    	    	tempFacCatPurchasesMap.put("CategoryWiseTotals", tempFacCategoryPurTotMap);
    	    	
    	    	Map tempProdFacPurCatMap = FastMap.newInstance();
    	    	tempProdFacPurCatMap.putAll(tempFacCatPurchasesMap);
    	    	
    	    	Map facCatTotalsMap = FastMap.newInstance();
    	    	facCatTotalsMap.put("sale", tempProdFacCatMap);
    	    	facCatTotalsMap.put("purchase", tempProdFacPurCatMap);
    	    	
    	    	Map tempProdMap = FastMap.newInstance();
    	    	tempProdMap.putAll(facCatTotalsMap);
	    		
	    		productMap.put("prodCategoryTotals", tempProdMap);
	    		
	    		Map tempProductCategoryMap = FastMap.newInstance();
	    		tempProductCategoryMap.putAll(productMap);
	    		
	    		productCategoryMap.put(category, tempProductCategoryMap);
	    	}
	    	productWiseTotalsMap.put("Total", productWiseGrTotalsMap);
	    	result.put("periodTotalsMap", productCategoryMap);
	    	result.put("productWiseTotalsMap", productWiseTotalsMap);	    	
	        return result;
	 }
*/
	 public static Map<String, Object> getCategoryProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Locale locale = (Locale) context.get("locale");
			String productCategoryId = (String)context.get("productCategoryId");
	        if (UtilValidate.isEmpty(productCategoryId)) {
	            Debug.logError("productCategoryId cannot be empty", module);
	            return ServiceUtil.returnError("productCategoryId cannot be empty");        	
	        }  			
	        Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
	        if(UtilValidate.isEmpty(effectiveDate)){
	        	effectiveDate = UtilDateTime.nowTimestamp();
	        }
	        Timestamp dayBegin = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
	        			
			List conditionList = FastList.newInstance();
	        
			conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	   			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
			EntityCondition prodCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List<GenericValue> categoryProducts = null;
			try {
				categoryProducts = delegator.findList("ProductAndCategoryMember", prodCondition , UtilMisc.toSet("productId", "productName", "primaryProductCategoryId", "productCategoryId"), null, null, false );
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching categories");
			}
			categoryProducts = EntityUtil.filterByDate(categoryProducts, dayBegin);
			
			List productIdsList = EntityUtil.getFieldListFromEntityList(categoryProducts, "productId", true);
			Map<String, Object> result = FastMap.newInstance(); 
			result.put("productIdsList", productIdsList);
	        return result;        
	  }
	 
	 public static Map<String, Object> getByProdReportCategories(Delegator delegator, Map<String, ? extends Object> context) {
	    	List<String> reportProductCategories = FastList.newInstance();
	    	List<GenericValue> productCategories = null;
	    	
	    	String productCategoryTypeId = (String) context.get("productCategoryTypeId");
	    	try {
	    		List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, productCategoryTypeId));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		
	    		productCategories = delegator.findList("ProductCategory", condition, null, UtilMisc.toList("sequenceNum", "productCategoryId"), null, false);
	    		reportProductCategories = EntityUtil.getFieldListFromEntityList(productCategories, "productCategoryId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("reportProductCategories", reportProductCategories);
	        return result;
	  }
	 
	 public static Map<String, Object> getReportCategoryProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Locale locale = (Locale) context.get("locale");
	        
	        Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
	        if(UtilValidate.isEmpty(effectiveDate)){
	        	effectiveDate = UtilDateTime.nowTimestamp();
	        }
	        Timestamp dayBegin = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
	        
			List categoryIds = (List) getByProdReportCategories(delegator, UtilMisc.toMap("productCategoryTypeId", "BYPROD_REPORTS")).get("reportProductCategories");
			
			List conditionList = FastList.newInstance();
	        
			conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIds));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	   			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
			EntityCondition prodCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List<GenericValue> categoryProducts = null;
			try {
				categoryProducts = delegator.findList("ProductAndCategoryMember", prodCondition , UtilMisc.toSet("productId", "productName", "primaryProductCategoryId", "productCategoryId"), null, null, false );
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching categories");
			}
			categoryProducts = EntityUtil.filterByDate(categoryProducts, dayBegin);
			Map prodMap = FastMap.newInstance();
			List categoryProdList = FastList.newInstance();
			String categoryId = null;
			String productId = null;
			for (GenericValue categoryProd : categoryProducts) {
				productId = categoryProd.getString("productId");
				categoryId = categoryProd.getString("productCategoryId");
				if(prodMap.containsKey(categoryId)){
					categoryProdList = (List)prodMap.get(categoryId);
					List tempList = FastList.newInstance();
					tempList.addAll(categoryProdList);
					tempList.add(productId);
					prodMap.put(categoryId, tempList);
					categoryProdList.clear();
				}else{
					
					List prodList = FastList.newInstance();
					prodList.add(productId);
					
					List tempList = FastList.newInstance();
					tempList.addAll(prodList);
					
					prodMap.put(categoryId, tempList);
					
					prodList.clear();
				}
			}
			
			List productIdsList = EntityUtil.getFieldListFromEntityList(categoryProducts, "productId", true);
			Map<String, Object> result = FastMap.newInstance(); 
			result.put("productList", categoryProducts);
			result.put("categoryProduct", prodMap);
			result.put("productIdsList", productIdsList);
	        return result;        
	  }
	 
	 public static Map<String, Object> getBoothSaleAndPaymentTotals(DispatchContext dctx, Map<String, ? extends Object> context ,Boolean collectionForNextDay) {
		 	Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
	        List<String> facilityIds = (List<String>) context.get("facilityIds");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
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
	        Map<String, Object> boothTotals = FastMap.newInstance();
	        try {
	        	List shipmentIds = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
	        	 
	        	for(String facilityId : facilityIds){
	        		Map<String, Object> boothTotalsMap = FastMap.newInstance();
		        	List<GenericValue> orderItems= FastList.newInstance();
		        	BigDecimal totalRevenue = BigDecimal.ZERO;
		        	List conditionList= FastList.newInstance(); 
		        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
		        	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		        	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
		        	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
		        	conditionList.add(EntityCondition.makeCondition("originFacilityId",EntityOperator.EQUALS, facilityId));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        	if(!UtilValidate.isEmpty(shipmentIds)){        		
		        		orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
		        		Iterator<GenericValue> itemIter = orderItems.iterator();
			        	while(itemIter.hasNext()) {
			                GenericValue orderItem = itemIter.next();
			                BigDecimal totalAmount = BigDecimal.ZERO;
			                BigDecimal quantity  = orderItem.getBigDecimal("quantity");
			                //to calculate the price inculiding all taxes
			                 Map<String, Object> priceResult;
			                 Map<String, Object> priceContext = FastMap.newInstance();
			                 priceContext.put("userLogin", userLogin);   
			                 priceContext.put("productStoreId", orderItem.get("productStoreId"));                    	
			                 priceContext.put("productId", orderItem.get("productId"));
			                 priceContext.put("facilityId", facilityId); 
			                 priceContext.put("priceDate", orderItem.getTimestamp("estimatedDeliveryDate"));
			         		 priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext); 
			                 if (ServiceUtil.isError(priceResult)) {
			                     Debug.logWarning("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
			             		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
			                 }
			                 if (UtilValidate.isNotEmpty(priceResult)) {
									totalAmount = (BigDecimal)priceResult.get("totalPrice");
			                 }
			                BigDecimal price  = totalAmount; 
			                BigDecimal revenue = price.multiply(quantity);
			                totalRevenue = totalRevenue.add(revenue);
			        	}	
		        	}
		        	BigDecimal reciepts = BigDecimal.ZERO;
		        	if(collectionForNextDay){
		        		Map<String, Object> paidPaymentCtx = UtilMisc.<String, Object>toMap("fromDate", UtilDateTime.getNextDayStart(fromDate));    
			        	paidPaymentCtx.put("thruDate", UtilDateTime.getNextDayStart(thruDate));
						paidPaymentCtx.put("facilityId", facilityId);
						Map boothsPaymentsDetail =  ByProductNetworkServices.getBoothPaidPayments( dctx , paidPaymentCtx);
			 			if(UtilValidate.isNotEmpty(boothsPaymentsDetail)){
			 				reciepts = (BigDecimal)boothsPaymentsDetail.get("invoicesUnRoundedTotalAmount");
			 			}
			 			
		        	}else{
		        		Map<String, Object> paidPaymentCtx = UtilMisc.<String, Object>toMap("fromDate", fromDate);    
			        	paidPaymentCtx.put("thruDate", thruDate);
						paidPaymentCtx.put("facilityId", facilityId);
						Map boothsPaymentsDetail =  ByProductNetworkServices.getBoothPaidPayments( dctx , paidPaymentCtx);
			 			if(UtilValidate.isNotEmpty(boothsPaymentsDetail)){
			 				reciepts = (BigDecimal)boothsPaymentsDetail.get("invoicesUnRoundedTotalAmount");
			 			}
		        	}
		 			
		 			BigDecimal chequeAmount =BigDecimal.ZERO;
		 			if(UtilValidate.isNotEmpty(reciepts)){
		 				 chequeAmount = reciepts;	
		 			}
		 		
		 			BigDecimal invoiceAmount = totalRevenue;
		 			BigDecimal netAmount =(BigDecimal) invoiceAmount.subtract(chequeAmount);
		 			
		 			boothTotalsMap.put("facilityId", facilityId);
					boothTotalsMap.put("invoiceAmount", invoiceAmount);
					boothTotalsMap.put("chequeAmount", chequeAmount);
					boothTotalsMap.put("netAmount", netAmount);
					boothTotals.put(facilityId,boothTotalsMap);
		        }
	        }catch (Exception e) {
	        	Debug.logError(e, module);
			}
	        
	    	Map<String, Object> result = FastMap.newInstance();    
	    	result.put("boothTotalsMap", boothTotals);
	        return result;
	 }
	 public static Map<String, Object> getByProductPricesForPartyClassification(DispatchContext dctx, Map<String, ? extends Object> context) {
		 Map result = FastMap.newInstance();
	     GenericValue userLogin = (GenericValue) context.get("userLogin");
	     Locale locale = (Locale) context.get("locale");
		 String partyClassificationId = (String)context.get("partyClassificationId");
		 Map priceResult = getByProductPricesForFacility(dctx, UtilMisc.toMap("userLogin", userLogin,"partyClassificationId", partyClassificationId));
		 if(!ServiceUtil.isError(priceResult)){
			 result.put("productsPrice", (Map)priceResult.get("productsPrice"));
		 }
		 return result;
	 }
	 
	 public static Map<String, Object> getByProductPricesForFacility(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	
		  	Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Locale locale = (Locale) context.get("locale");
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String facilityId = (String) context.get("facilityId");
	        String partyClassificationId = (String)context.get("partyClassificationId");
	        Timestamp priceDate = (Timestamp) context.get("priceDate");
	        Map result = FastMap.newInstance();
	        
	        if(UtilValidate.isEmpty(priceDate)){
	        	priceDate = UtilDateTime.nowTimestamp();
	        }
	        String productStoreId = (String) ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	        String productCategoryId = (String)context.get("productCategoryId");
    		Map<String, Object> productsContext = FastMap.newInstance();
    		if (UtilValidate.isNotEmpty(productCategoryId)) {
    			productsContext.put("productCategoryId", productCategoryId);
    		}
	        List<GenericValue> products = ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), productsContext);
	        List<String> productList = EntityUtil.getFieldListFromEntityList(products, "productId", true);
	        Map<String, Map> productsPrice = FastMap.newInstance();
	        if(UtilValidate.isNotEmpty(productList)){
	        	for(int j = 0; j < productList.size(); ++j){
	        		String eachProd = productList.get(j);
	        		Map<String, Object> priceResult;
	        		Map<String, Object> priceContext = FastMap.newInstance();
	        		priceContext.put("userLogin", userLogin);   
	        		priceContext.put("productStoreId", productStoreId);                    
	        		priceContext.put("productId", eachProd);
	        		priceContext.put("priceDate", priceDate);
	        		if(UtilValidate.isEmpty(facilityId)){
	        			priceContext.put("productPriceTypeId", partyClassificationId);
	        		}else{
	        			priceContext.put("facilityId", facilityId);
	        		}
	        		priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext); 
	        		if (ServiceUtil.isError(priceResult)) {
	        			Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
	        			return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
	        		}
	        		if (UtilValidate.isNotEmpty(priceResult)) {
						BigDecimal totalAmount = (BigDecimal)priceResult.get("totalPrice");
						BigDecimal basicPrice = (BigDecimal)priceResult.get("basicPrice");
						List<GenericValue> taxList = (List)priceResult.get("taxList");
						BigDecimal BED = BigDecimal.ZERO;
						BigDecimal VAT = BigDecimal.ZERO;
						BigDecimal BEDCESS = BigDecimal.ZERO;
						BigDecimal BEDSECCESS = BigDecimal.ZERO;
						BigDecimal vatPercentage = BigDecimal.ZERO;
						BigDecimal bedPercentage = BigDecimal.ZERO;
						BigDecimal bedCessPercent = BigDecimal.ZERO;
						BigDecimal bedsecPercent = BigDecimal.ZERO;
						
						if(UtilValidate.isNotEmpty(taxList)){
							for(int i=0;i<taxList.size();i++){
								Map tempMap = taxList.get(i);
								String taxType = (String)tempMap.get("taxType");
								BigDecimal amount = (BigDecimal)tempMap.get("amount");
								BigDecimal percentage = (BigDecimal)tempMap.get("percentage");
								if(taxType.equalsIgnoreCase("VAT_SALE")){
									VAT = amount;
									vatPercentage = percentage;
									if(UtilValidate.isNotEmpty(vatPercentage) && amount.compareTo(BigDecimal.ZERO) == 0){
										VAT = basicPrice.multiply((vatPercentage.divide(new BigDecimal(100))));
										totalAmount = totalAmount.add(VAT);
									}
								}
								else if(taxType.equalsIgnoreCase("BEDCESS_SALE")){
									BEDCESS = amount;
									bedPercentage = percentage;
								}
								else if(taxType.equalsIgnoreCase("BED_SALE")){
									BED = amount;
									bedCessPercent = percentage;
								}
								else if(taxType.equalsIgnoreCase("BEDSECCESS_SALE")){
									BEDSECCESS = amount;
									bedsecPercent = percentage;
								}
								else{
								}
							}
						}
						Map priceCatMap = FastMap.newInstance();
						priceCatMap.put("VAT", VAT);
						priceCatMap.put("BEDCESS", BEDCESS);
						priceCatMap.put("BEDSECCESS", BEDSECCESS);
						priceCatMap.put("BED", BED);
						priceCatMap.put("basicPrice", basicPrice);
						priceCatMap.put("totalAmount", totalAmount);
						priceCatMap.put("vatPercentage", vatPercentage);
						priceCatMap.put("bedPercentage", bedCessPercent);
						priceCatMap.put("bedCessPercent", bedPercentage);
						priceCatMap.put("bedsecPercent", bedsecPercent);
						priceCatMap.put("name", products.get(j).get("brandName"));
						priceCatMap.put("description", products.get(j).get("description"));		
						priceCatMap.put("sequenceNum", products.get(j).get("sequenceNum"));							
						productsPrice.put(eachProd, priceCatMap);
	        		}
	        	}
	        }
	        result.put("productsPrice", productsPrice);
	        return result;
	 }
	 
	 public static Map<String, Object> getDayDespatchDetails(DispatchContext dctx, Map<String, ? extends Object> context ) {
	    	
	    	Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    Map<String, Object> result = FastMap.newInstance();
		    Locale locale = (Locale) context.get("locale");
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    List shipmentIds = (List) context.get("shipmentIds");
	        Timestamp salesDate = (Timestamp) context.get("salesDate");
	        String subscriptionTypeId = (String) context.get("subscriptionTypeId");
	        List<String> facilityIds = (List<String>) context.get("facilityIds");
	        Timestamp fromDate = UtilDateTime.getDayStart(salesDate); 
	        Timestamp thruDate = UtilDateTime.getDayEnd(salesDate); 
	        Map categoryTotals = FastMap.newInstance(); 
	        if(UtilValidate.isEmpty(shipmentIds)){
	        	if (UtilValidate.isEmpty(fromDate)) {
		        	fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), locale);  	
		        }        
		        if (UtilValidate.isEmpty(thruDate)) {
		        	thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), locale);          	
		        }
		        
		        String dateStr = UtilDateTime.toDateString(fromDate, "yyyy-MM-dd HH:mm:ss");
		        shipmentIds =  ByProductNetworkServices.getShipmentIdsByAMPM(delegator,dateStr,subscriptionTypeId);
		        
	        }
	         
	        List<GenericValue> indentProducts = ProductWorker.getProductsByCategory(delegator ,"INDENT" ,null);
	        List indentProductIds = EntityUtil.getFieldListFromEntityList(indentProducts, "productId", true); 
	        
			Map periodTotalsCtx = FastMap.newInstance();
			periodTotalsCtx.put("userLogin", userLogin);
			periodTotalsCtx.put("shipmentIds", shipmentIds);
			periodTotalsCtx.put("fromDate", fromDate);	
			periodTotalsCtx.put( "thruDate", thruDate);	
	        if(!UtilValidate.isEmpty(facilityIds)){
				periodTotalsCtx.put("facilityIds", facilityIds);	        
	        }

	        Map resultCtx = (Map)ByProductNetworkServices.getPeriodTotals(dctx, periodTotalsCtx);
 	    	if(ServiceUtil.isError(resultCtx)){
 	    		Debug.logError("Error in service getPeriodTotals ", module);    			
	            return ServiceUtil.returnError("Error in service getPeriodTotals");
 	    	}
 	    	BigDecimal totalSale = (BigDecimal)resultCtx.get("totalRevenue");
 	    	Map productTotals = (Map)resultCtx.get("productTotals");
 	    	List<GenericValue> products = FastList.newInstance();
 	    	try{
 	    		
 	    		products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, indentProductIds), null, null, null, false);
 	    	}catch(GenericEntityException e){
 	    		Debug.logError("Error in fetching Products", module);    			
	            return ServiceUtil.returnError("Error in fetching Products");
 	    	}
 	    	Map productCategoryMap = FastMap.newInstance();
 	    	String productId = "";
 	    	String primaryProductCategoryId = "";
 	    	for(GenericValue product : products){
 	    		productId = product.getString("productId");
 	    		primaryProductCategoryId = product.getString("primaryProductCategoryId");
 	    		
 	    		if(UtilValidate.isNotEmpty(productCategoryMap.get(primaryProductCategoryId))){
 	    			List getProdList = (List)productCategoryMap.get(primaryProductCategoryId);
	    			getProdList.add(productId);
	    			productCategoryMap.put(primaryProductCategoryId, getProdList);
 	    		}
 	    		else{
 	    			List tempList = FastList.newInstance();
	    			tempList.add(productId);
	    			productCategoryMap.put(primaryProductCategoryId, tempList);
 	    		}
 	    	}
 	    	
 	    	List milkProducts = (List)productCategoryMap.get("Milk");
 	    	List curdProducts = (List)productCategoryMap.get("Curd");
 	    	List otherProducts = (List)productCategoryMap.get("OTHER");
 	    	BigDecimal milkTotalLtr = BigDecimal.ZERO;
 	    	BigDecimal curdTotalLtr = BigDecimal.ZERO;
 	    	BigDecimal otherTotals = BigDecimal.ZERO;
 	    	Iterator prodIter = productTotals.entrySet().iterator();
        	while(prodIter.hasNext()) {
        		Map.Entry entry = (Entry)prodIter.next();
                String prodId = (String)entry.getKey();
                GenericValue prodCategory = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId)));
                
                String categoryId = prodCategory.getString("primaryProductCategoryId");
                
                Map prodTotals = (Map)entry.getValue();
                BigDecimal totalQty = BigDecimal.ZERO;
                if(UtilValidate.isNotEmpty(prodTotals)){
                	totalQty = (BigDecimal)prodTotals.get("total");
                	if(UtilValidate.isNotEmpty(categoryTotals.get(categoryId))){
                		BigDecimal extQty = (BigDecimal)categoryTotals.get(categoryId);
                		BigDecimal tempQty = extQty.add(totalQty);
                		categoryTotals.put(categoryId, tempQty);
                	}
                	else{
                		categoryTotals.put(categoryId, totalQty);
                	}
                }
        	}
        	result.put("categoryTotals", categoryTotals);
        	result.put("totalRevenue", totalSale);
 	    	return result;
	    }
	 
	  	public static Map<String, Object>  sendSMSNotification(DispatchContext dctx, Map<String, Object> context)  {
	        LocalDispatcher dispatcher = dctx.getDispatcher();	
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String subscriptionTypeId = (String) context.get("subscriptionTypeId");
	        String supplyDate = (String) context.get("supplyDate");
	        Timestamp smsDate = null;
	        if(UtilValidate.isEmpty(supplyDate)){
	        	smsDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	        }
	        if (UtilValidate.isNotEmpty(supplyDate)) { 
    			SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
    			try {
    				smsDate = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
    			} catch (ParseException e) {
    				Debug.logError("Cannot parse date string: "+supplyDate, module);
	    			return ServiceUtil.returnError("Cannot parse date string"); 
    			} catch (NullPointerException e) {
    				Debug.logError("Cannot parse date string: "+supplyDate, module);
	    			return ServiceUtil.returnError("Cannot parse date string"); 
    			}
    		}
	        Timestamp smsStartDate = UtilDateTime.getDayStart(smsDate);
	        Timestamp smsEndDate = UtilDateTime.getDayEnd(smsDate);
	        List shipmentIds = FastList.newInstance();
	        String dateStr = UtilDateTime.toDateString(smsDate, "yyyy-MM-dd HH:mm:ss");
	        if(UtilValidate.isNotEmpty(subscriptionTypeId) && subscriptionTypeId.equalsIgnoreCase("PM")){
	        	shipmentIds = (List)ByProductNetworkServices.getShipmentIdsSupplyType(delegator, smsStartDate, smsEndDate, null);	
	        }
	        else{
	        	shipmentIds =  ByProductNetworkServices.getShipmentIdsSupplyType(delegator, smsStartDate, smsEndDate, subscriptionTypeId);
	        }
	        
	        String displayDate = UtilDateTime.toDateString(smsDate, "dd MMM, yyyy");
	        if(UtilValidate.isEmpty(shipmentIds)){
	        	Debug.logError("No shipments found", module);
	    		return ServiceUtil.returnError("No shipments found");
	        }
			Map<String, Object> todaysSalesTotals = getDayDespatchDetails(dctx, UtilMisc.toMap("userLogin", userLogin, "shipmentIds", shipmentIds, "salesDate", smsDate, "subscriptionTypeId", null));
			
			Map catTotals = (Map)todaysSalesTotals.get("categoryTotals");
			
			BigDecimal milkTotal = BigDecimal.ZERO;
			BigDecimal curdTotal = BigDecimal.ZERO;
			BigDecimal butterTotal = BigDecimal.ZERO;
			BigDecimal gheeTotal = BigDecimal.ZERO;
			BigDecimal butterMilkTotal = BigDecimal.ZERO;
			BigDecimal paneerTotal = BigDecimal.ZERO;
			BigDecimal otherTotal = BigDecimal.ZERO;
			
			Iterator categoryIter = catTotals.entrySet().iterator();
        	while(categoryIter.hasNext()) {
        		Map.Entry entry = (Entry)categoryIter.next();
                String categoryId = (String)entry.getKey();
                if(categoryId.equalsIgnoreCase("Milk")){
                	milkTotal = (BigDecimal)catTotals.get(categoryId);
                }
                else if(categoryId.equalsIgnoreCase("Curd")){
                	curdTotal = (BigDecimal)catTotals.get(categoryId);
                }
                else if(categoryId.equalsIgnoreCase("Butter")){
                	butterTotal = (BigDecimal)catTotals.get(categoryId);
                }
                else if(categoryId.equalsIgnoreCase("Ghee")){
                	gheeTotal = (BigDecimal)catTotals.get(categoryId);
                }
                else if(categoryId.equalsIgnoreCase("ButterMilk")){
                	butterMilkTotal = (BigDecimal)catTotals.get(categoryId);
                }
                else if(categoryId.equalsIgnoreCase("Paneer")){
                	paneerTotal = (BigDecimal)catTotals.get(categoryId);
                }
                else{
                	otherTotal = otherTotal.add((BigDecimal)catTotals.get(categoryId));
                }
        	}
			
			milkTotal = milkTotal.setScale(0, rounding);
			curdTotal = curdTotal.setScale(0, rounding);
			gheeTotal = gheeTotal.setScale(0, rounding);
			butterTotal = butterTotal.setScale(0, rounding);
			paneerTotal = paneerTotal.setScale(0, rounding);
			butterMilkTotal = butterMilkTotal.setScale(0, rounding);
			otherTotal = otherTotal.setScale(0, rounding);
			String subTypeText = "AM";
			if(subscriptionTypeId.equals("PM")){
				subTypeText = "AM+PM";
			}
			try {
				// Send SMS notification to list
				String text = displayDate +" ("+subTypeText+") Dispatch Totals -- MILK: "+milkTotal+" Ltrs; CURD: "+curdTotal+" Ltrs; GHEE: "+gheeTotal+" Kgs; BTR: "+butterTotal+" Kgs; PNR: "+paneerTotal+" Kgs; MBM: "+butterMilkTotal+" Ltrs; OTH: " +otherTotal+" Kgs. From Milkosoft.";
				Debug.logInfo("Sms text: " + text, module);
				Map<String,  Object> sendSmsContext = UtilMisc.<String, Object>toMap("contactListId", "SALES_NOTIFY_LST", 
					"text", text, "userLogin", userLogin);
				dispatcher.runAsync("sendSmsToContactListNoCommEvent", sendSmsContext);
			}
			catch (GenericServiceException e) {
				Debug.logError(e, "Error calling sendSmsToContactListNoCommEvent service", module);
				return ServiceUtil.returnError(e.getMessage());			
			} 
	        return ServiceUtil.returnSuccess("Sms successfully sent!");		
		} 	
	 
	 public static Map<String, Object> getTotalSales(DispatchContext dctx, Map<String, ? extends Object> context ) {
	    	
	    	Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();
		    Map<String, Object> result = FastMap.newInstance();
		    Locale locale = (Locale) context.get("locale");
		    
	        List<String> facilityList = (List<String>) context.get("facilityList");
	        List<String> productList = (List<String>) context.get("productList");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Timestamp fromDate = UtilDateTime.getDayStart( (Timestamp)(context.get("fromDate")), TimeZone.getDefault(), locale); 
	        Timestamp thruDate = UtilDateTime.getDayEnd( (Timestamp)(context.get("thruDate")), TimeZone.getDefault(), locale); 
	        if (UtilValidate.isEmpty(fromDate)) {
	        	fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), locale);  	
	        }        
	        if (UtilValidate.isEmpty(thruDate)) {
	        	thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), locale);          	
	        } 
	        List shipmentList = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, thruDate);
	        
	        String productStoreId = (String) ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	        if(UtilValidate.isEmpty(productList)){
	        	productList = (List) ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	        }
	        
	        List conditionList= FastList.newInstance(); 
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
			conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
			if(UtilValidate.isNotEmpty(facilityList)){
				conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityList));
			}
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			
	    	List <GenericValue> boothOrdersList = null;
	    	try{
	    		boothOrdersList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition,  null, null, null, false);
	    	}catch (GenericEntityException e) {
				// TODO: handle exception
	    		Debug.logError("Unable to get records from OrderHeaderItemProductShipmentAndFacility"+e, module);
	    		return ServiceUtil.returnError("Unable to get records from OrderHeaderItemProductShipmentAndFacility "); 
			}
	    	
	    	List<GenericValue> nonParlourSale = EntityUtil.filterByCondition(boothOrdersList, EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "BYPROD_SALES_CHANNEL"));
	    	nonParlourSale = EntityUtil.filterByCondition(nonParlourSale, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL, "PARLOUR"));
	    	List<GenericValue> parlourSale = EntityUtil.filterByCondition(boothOrdersList, EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "PARLOR_SALES_CHANNEL"));
	    	List<GenericValue> boothOrderItemsList = FastList.newInstance();
	    	boothOrderItemsList.addAll(nonParlourSale);
	    	boothOrderItemsList.addAll(parlourSale);
	    	List boothList = EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "originFacilityId", true);
	    	Map facilityPriceMap = FastMap.newInstance();
	    	Map classificationMap = FastMap.newInstance();
	    	if(UtilValidate.isNotEmpty(boothList)){
	    		
	    		for(int i=0; i<boothList.size(); i++){
	    			
	    			String boothId = (String) boothList.get(i);
	    			String classifyGroupId = "";
	    			GenericValue facilityParty;
					try {
						facilityParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", boothId), false);
						
						if(UtilValidate.isNotEmpty(facilityParty)){
		    				String partyId = facilityParty.getString("ownerPartyId");
		    				List<GenericValue> partyClassificationGroup = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false);
		    				partyClassificationGroup = EntityUtil.filterByDate(partyClassificationGroup, fromDate);
		    				if(UtilValidate.isNotEmpty(partyClassificationGroup)){
		    					classifyGroupId = (String) EntityUtil.getFirst(partyClassificationGroup).get("partyClassificationGroupId");
		    				}
		    			}
					} catch (GenericEntityException e) {
						Debug.logError("Unable to get records from Facility"+e, module);
			    		return ServiceUtil.returnError("Unable to get records from Facility"); 
					}
	    			
	    			if(UtilValidate.isNotEmpty(classificationMap.get(classifyGroupId))){
	    				Map tempMap = FastMap.newInstance();
	    				tempMap = (Map) classificationMap.get(classifyGroupId);
	    				facilityPriceMap.put(boothId, tempMap);
	    			}else{
	    				Map productsPrice = (Map) ByProductReportServices.getByProductPricesForFacility(dctx, UtilMisc.toMap( "userLogin" ,userLogin,"facilityId", boothId, "priceDate", fromDate)).get("productsPrice");
	    				facilityPriceMap.put(boothId, productsPrice);
	    				classificationMap.put(classifyGroupId, productsPrice);
	    			}
	    		}
	    	}
	    	BigDecimal totalQty = BigDecimal.ZERO;
			BigDecimal totalValue = BigDecimal.ZERO;
			BigDecimal totalQtyInc = BigDecimal.ZERO;
	    	for(int i=0; i<boothOrderItemsList.size(); i++){
	    		GenericValue boothOrderItem = (GenericValue) boothOrderItemsList.get(i);
	    		String productId = (String) boothOrderItem.get("productId");
	    		BigDecimal quantity =(BigDecimal) boothOrderItem.get("quantity");
	    		String facilityId = (String) boothOrderItem.get("originFacilityId");
	    		BigDecimal quantityIncluded = (BigDecimal) boothOrderItem.get("quantityIncluded");
	    		Map priceMap = (Map) facilityPriceMap.get(facilityId);
				Map productPrice = (Map)priceMap.get(productId);
				BigDecimal price = (BigDecimal)productPrice.get("totalAmount");
                BigDecimal eachProductValue = price.multiply(quantity);
                BigDecimal prodQtyInc = quantityIncluded.multiply(quantity);
                totalQty = totalQty.add(quantity);
                totalValue = totalValue.add(eachProductValue);
                totalQtyInc = totalQtyInc.add(prodQtyInc);
	    	}
	    	result.put("totalQty", totalQty);
	    	result.put("totalValue", totalValue);
	    	result.put("totalQtyInc", totalQtyInc);
	        return result;
	    }
	 
}

package in.vasista.vbiz.procurement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

public class PriceServices {
	
	 public static final String module = PriceServices.class.getName();
	 private static BigDecimal ZERO = BigDecimal.ZERO;
	 private static int decimals;
	 private static int rounding;
	 static {
	        decimals = 1;//UtilNumber.getBigDecimalScale("order.decimals");
	        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

	        // set zero to the proper scale
	        if (decimals != -1) ZERO = ZERO.setScale(decimals);
	    }    
	private static GenericValue fetchPriceChart(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();		
		GenericValue priceChart = null;
        Timestamp priceDate = (Timestamp) context.get("priceDate");
        String categoryTypeEnum = (String) context.get("categoryTypeEnum");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
        String isPremiumChart = (String) context.get("isPremiumChart");
        List ancestorNodeList =FastList.newInstance();
        List ancestorCatTypeList =FastList.newInstance();
        List<EntityCondition> condList = FastList.newInstance();
        if (UtilValidate.isEmpty(priceDate)) {
        	priceDate = UtilDateTime.nowTimestamp();
        } 		
        String facilityId = (String) context.get("facilityId");
        ancestorNodeList.add(facilityId);
    	try {
    		// ::TODO:: Need to handle region-specific rate charts    	
    		// Fist get the appropriate Procurement Chart
    		Map ancestorNodeMap =fetchAncestorNodesForFacility(dctx, UtilMisc.toMap("facilityId", facilityId ,"ancestorNodeList", ancestorNodeList, "ancestorCatTypeList",ancestorCatTypeList));    		
    		ancestorNodeList = (List)ancestorNodeMap.get("ancestorNodeList");
    		ancestorCatTypeList = (List)ancestorNodeMap.get("ancestorCatTypeList");    		
    		ancestorNodeList.add("_NA_");
    		Debug.logInfo("ancestorNodeList =========="+ancestorNodeList, module);
    		Debug.logInfo("ancestorCatTypeList =========="+ancestorCatTypeList, module);
    		List orderBy = UtilMisc.toList("-categoryTypeEnum","-supplyTypeEnumId");
    		condList.add(EntityCondition.makeCondition("regionId" ,EntityOperator.IN , ancestorNodeList));
    		
    		if(UtilValidate.isNotEmpty(isPremiumChart) && isPremiumChart.equals("Y")){
    			condList.add(EntityCondition.makeCondition("isPremiumChart" ,EntityOperator.EQUALS , "Y"));
    		}else{
    			condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isPremiumChart" ,EntityOperator.EQUALS , null) ,EntityOperator.OR,EntityCondition.makeCondition("isPremiumChart" ,EntityOperator.EQUALS , "N")));
    		}
    		List<GenericValue> chartList = delegator.findList("ProcurementPriceChart", EntityCondition.makeCondition(condList , EntityOperator.AND ), null, orderBy, null, true);
    		List<GenericValue> selChartList = EntityUtil.filterByDate(chartList, priceDate);
   		 	if(UtilValidate.isEmpty(selChartList)){
   		 		Debug.logInfo("No valid price chart found!", module);
   		 		return priceChart;
   		 	}	
   		 	if (selChartList.size() == 1 && "_NA_".equals(selChartList.get(0).getString("regionId"))) {
   		 		priceChart = selChartList.get(0); 
   		 		return priceChart;
   		 	}   		
   		 	Map<String, List> priceChartMap = FastMap.newInstance();
   		 	for (int i = 0; i < selChartList.size(); ++ i) {
   		 		GenericValue tempChart = selChartList.get(i);
   		 		String regionId = tempChart.getString("regionId");
   		 		List<GenericValue> tempChartList = FastList.newInstance();   		 		
   		 		if(UtilValidate.isEmpty(priceChartMap.get(regionId))){
   		 			tempChartList.add(tempChart);
   		 			priceChartMap.put(regionId,tempChartList);
   		 			continue;
   		 		}
   		 		tempChartList.addAll(priceChartMap.get(regionId));
   		 		tempChartList.add(tempChart);
   		 		priceChartMap.put(regionId,tempChartList);  
   		 	}   	      
   		 	HashSet regions = new HashSet(EntityUtil.getFieldListFromEntityList(selChartList, "regionId", false));
   		 	// if the facility has specific price chart then return that 
   		 	if(regions.contains(facilityId)){
   		 		List<GenericValue> tempChartList = priceChartMap.get(facilityId);
   		 		 if(tempChartList.size() == 1 && (UtilValidate.isEmpty(tempChartList.get(0).getString("categoryTypeEnum"))|| (UtilValidate.isNotEmpty(categoryTypeEnum)&& categoryTypeEnum.equals(tempChartList.get(0).getString("categoryTypeEnum")))) && (UtilValidate.isEmpty(tempChartList.get(0).getString("supplyTypeEnumId")) || (UtilValidate.isNotEmpty(supplyTypeEnumId) && supplyTypeEnumId.equals(tempChartList.get(0).getString("supplyTypeEnumId"))))){
   		 			 return tempChartList.get(0); 
   		 		 }
   		 		 
   		 	}
   		 		//lets iterate through each node list to find appropriate price chart
	 		for(int i=0;i<ancestorNodeList.size();i++){
	 			String tempNodeId = (String)ancestorNodeList.get(i);
	 			//ancestorNode has only one price chart  and  supplyTypeEnumId,categoryTypeEnum are  null or  equals 
	 			//then return that price chart item	 			
	 			if(UtilValidate.isNotEmpty(priceChartMap.get(tempNodeId))){   		 				
   		 			List<GenericValue> tempChartList = priceChartMap.get(tempNodeId);   		 			
   		 		if(tempChartList.size() == 1 && (UtilValidate.isEmpty(tempChartList.get(0).getString("categoryTypeEnum"))|| (UtilValidate.isNotEmpty(categoryTypeEnum)&& categoryTypeEnum.equals(tempChartList.get(0).getString("categoryTypeEnum")))) && (UtilValidate.isEmpty(tempChartList.get(0).getString("supplyTypeEnumId")) || (UtilValidate.isNotEmpty(supplyTypeEnumId) && supplyTypeEnumId.equals(tempChartList.get(0).getString("supplyTypeEnumId"))))){
      		 			return tempChartList.get(0); 
      		 		 }else{
      		 			 //Prepare the temp chart list based on the priority
      		 			 /* priority  will be like following
      		 			  * 
      		 			  * 1. categoryTypeEnum,supplyTypeEnumId both should match
      		 			  * 2. categoryTypeEnum is equal and supplyTypeEnumId is null
      		 			  * 3. categoryTypeEnum is null and  supplyTypeEnumId is equal
      		 			  * 4. categoryTypeEnum,supplyTypeEnumId both are null     		 			   
      		 			  * */
      		 			
      		 			 for( GenericValue tempChart : tempChartList){      		 				
      		 				 if(UtilValidate.isNotEmpty(tempChart.getString("categoryTypeEnum")) && UtilValidate.isNotEmpty(tempChart.getString("supplyTypeEnumId")) && ancestorCatTypeList.contains(tempChart.getString("categoryTypeEnum")) && tempChart.getString("supplyTypeEnumId").equals(supplyTypeEnumId)){	 					
      		 					 return tempChart;	      		 					      		 						      		 					 
      		 				 }else if(UtilValidate.isNotEmpty(tempChart.getString("categoryTypeEnum")) &&  ancestorCatTypeList.contains(tempChart.getString("categoryTypeEnum")) && UtilValidate.isEmpty(tempChart.getString("supplyTypeEnumId"))){	      		 				
      		 					return tempChart;
      		 				 }else if( UtilValidate.isNotEmpty(tempChart.getString("supplyTypeEnumId")) && UtilValidate.isEmpty(tempChart.getString("categoryTypeEnum")) && tempChart.getString("supplyTypeEnumId").equals(supplyTypeEnumId)){
      		 					return tempChart;
      		 				 }else if(UtilValidate.isEmpty(tempChart.getString("categoryTypeEnum")) && UtilValidate.isEmpty(tempChart.getString("supplyTypeEnumId"))){	      		 					
      		 					return tempChart;
      		 				 }      		 				 
      		 			 }//end of for each
      		 		 }
	 			}
	 		}	 
   		 	
   		 	 
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
     	}   		 	
		return priceChart;
	}
	private static Map<String, ? extends Object> fetchAncestorNodesForFacility(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();	 		
        String facilityId = (String) context.get("facilityId");
        List ancestorNodeList =(List) context.get("ancestorNodeList");
        List ancestorCatTypeList =(List) context.get("ancestorCatTypeList");       
    	try {  	   
	 	   GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" ,facilityId), true);
	 	   
	 	   if(UtilValidate.isEmpty(facility)){
	 		   return context;	 		   
	 	   }
	 	   facilityId = facility.getString("parentFacilityId");
	 	   ancestorCatTypeList.add(facility.getString("categoryTypeEnum"));
	 	   if(UtilValidate.isNotEmpty(facilityId)){
	 		   ancestorNodeList.add(facilityId);
	 		   return fetchAncestorNodesForFacility(dctx, UtilMisc.toMap("facilityId", facilityId ,"ancestorNodeList", ancestorNodeList, "ancestorCatTypeList",ancestorCatTypeList)); 
	 	   }else{ 		
   		 	  return context;
	 	   }   		 	   
   		 	
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
     	}   		 	
		return context;
	}
	
	/*
	 * Helper Method Computes the procurement price based on the input parameters. 
	 * The premium/deduction amount applied is also returned.  All values are per Kg.
	 * Default Rate is also returned by this function on per Kg fat (or per Kg solids).
	 * 
	 */
	
    private static Map<String, Object> calculateProcurementProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String priceChartId = (String) context.get("priceChartId");
       /* String productId = (String) context.get("productId");    
        String facilityId = (String) context.get("facilityId");
        String categoryTypeEnum = (String) context.get("categoryTypeEnum");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
        BigDecimal fatPercent = (BigDecimal) context.get("fatPercent");
        BigDecimal snfPercent = (BigDecimal) context.get("snfPercent");
        BigDecimal fatMinQualityToPremum = BigDecimal.ZERO;*/
        String customPriceCalcService = null;
		//lets check this price chart using baseSnf or not
		try{
			GenericValue priceChartIdDetails = delegator.findOne("ProcurementPriceChart",UtilMisc.toMap("procPriceChartId",priceChartId), false);
			if(UtilValidate.isNotEmpty(priceChartIdDetails) ){
				customPriceCalcService = priceChartIdDetails.getString("customPriceCalcService");
			}
			Debug.logInfo("customPriceCalcService=====>"+customPriceCalcService, module);
			// any custom method for price calculation use that
			if(UtilValidate.isNotEmpty(customPriceCalcService)){
				result = dispatcher.runSync(customPriceCalcService,context);
			}else{
				result = calculateProcurementProductPriceDefault(dctx, context);
			}
			if(ServiceUtil.isError(result)){
				Debug.logError("Error while calculating procurement product price=======>"+result,module);
				return result;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error while calculating procurement product price=======>"+e.getMessage(),module);
			return ServiceUtil.returnError("Error while calculating procurement product price.["+e.getMessage()+"]");
		}	
       
        return result;
    }
	/*
	 * Default Helper Method Computes the procurement price based on the input parameters. 
	 * The premium/deduction amount applied is also returned.  All values are per Kg.
	 * Default Rate is also returned by this function on per Kg fat (or per Kg solids).
	 * 
	 */
	
    private static Map<String, Object> calculateProcurementProductPriceDefault(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String priceChartId = (String) context.get("priceChartId");
        String productId = (String) context.get("productId");    
        String facilityId = (String) context.get("facilityId");
        String categoryTypeEnum = (String) context.get("categoryTypeEnum");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
        BigDecimal fatPercent = (BigDecimal) context.get("fatPercent");
        BigDecimal snfPercent = (BigDecimal) context.get("snfPercent");
        BigDecimal fatMinQualityToPremum = BigDecimal.ZERO;
        List<EntityCondition> procPriceEcList = FastList.newInstance();
        String useBaseSnf = "Y";
        if (UtilValidate.isEmpty(fatPercent)) {
        	fatPercent = BigDecimal.ZERO;
        }else{
        	//lets check this price chart using baseSnf or not
        	try{
        		GenericValue priceChartIdDetails = delegator.findOne("ProcurementPriceChart",UtilMisc.toMap("procPriceChartId",priceChartId), false);
        		if(UtilValidate.isNotEmpty(priceChartIdDetails)){
        			useBaseSnf = (String)priceChartIdDetails.get("useBaseSnf");
        		}
        	}catch (GenericEntityException e) {
        		Debug.logError("Error while getting useBaseSnf Value=======>"+e.getMessage(),module);
        		ServiceUtil.returnError("Error while getting useBaseSnf Value:");
        	}
        	
        	// lets override high fat with max value fat 
        	procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS , "PROC_PRICE_MAX_QLTY" ));
	        EntityCondition procPriceSlab = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        try{
	        	 List<GenericValue> productPricesMax = delegator.findList("ProcurementPrice", procPriceSlab, null, UtilMisc.toList("-snfPercent","-fatPercent"), null, false);	         	
	        	 if(UtilValidate.isNotEmpty(productPricesMax)){
	         		GenericValue productPriceMax = EntityUtil.getFirst(productPricesMax);	         		
	         		if(fatPercent.compareTo(productPriceMax.getBigDecimal("fatPercent")) > 0){
	         			fatPercent = productPriceMax.getBigDecimal("fatPercent");
	         		}
	         	}
	        }catch (Exception e) {
				// TODO: handle exception
	        	 Debug.logError(e, module);
	             return ServiceUtil.returnError(e.getMessage());
			}
	       
        }
       
        if (UtilValidate.isEmpty(snfPercent)) {
        	snfPercent = BigDecimal.ZERO;
        }
         //Don't merge the below Line, this is specific to APDairy	
          snfPercent = snfPercent.setScale(1, rounding);
        	
        Timestamp priceDate = (Timestamp) context.get("priceDate");
        if (UtilValidate.isEmpty(priceDate)) {
        	priceDate = UtilDateTime.nowTimestamp();
        } 
		if (Debug.infoOn()) {
			Debug.logInfo("fatPercent=" + fatPercent + "; snfPercent=" + snfPercent, module);
		} 
        BigDecimal defaultRate = BigDecimal.ZERO;
        BigDecimal sourRate = BigDecimal.ZERO;
        //BigDecimal sourPrice = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal premium = BigDecimal.ZERO;  
        result.put("defaultRate", defaultRate);
        result.put("sourRate", sourRate);
        //result.put("sourPrice", sourPrice);
        result.put("price", price);  
        result.put("premium", premium); 
        result.put("useTotalSolids", "N");
    	try {
    		
    		procPriceEcList.clear();
   	        //lets get the slab bucket for the given fat and snf
   	        String procurementPriceTypeId = null;
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.LIKE, "PROC_PRICE_SLAB"+"%"));
	        EntityCondition procPriceSlab = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        
	        List<GenericValue> productPriceSlabs = delegator.findList("ProcurementPrice", procPriceSlab, null, UtilMisc.toList("-snfPercent","-fatPercent"), null, false);
	        Debug.logInfo("productPriceSlabs =========="+productPriceSlabs, module);
	        for( GenericValue productPriceSlab  : productPriceSlabs ){
   	        	if(snfPercent.compareTo(productPriceSlab.getBigDecimal("snfPercent")) >= 0 && fatPercent.compareTo(productPriceSlab.getBigDecimal("fatPercent")) >= 0 ){
   	        		procurementPriceTypeId = productPriceSlab.getString("procurementPriceTypeId");
   	        		break; 	        		
   	        		
   	        	}
   	        	
   	        }
	        // lets get the minimum fat percent to calculate premum(add)
	        GenericValue productPriceDefaultSlab = EntityUtil.getFirst(EntityUtil.filterByAnd(productPriceSlabs, UtilMisc.toMap("procurementPriceTypeId","PROC_PRICE_SLAB1")));
	        fatMinQualityToPremum = productPriceDefaultSlab.getBigDecimal("fatPercent");
	        Debug.logInfo("procurementPriceTypeId =========="+procurementPriceTypeId, module);
	        //lets get the sour Rate
	        procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SOUR"));
   	        EntityCondition procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        List<GenericValue> productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logInfo("No sour Price configuration found (" + procPriceEc + ")", module);
   		 	}
   		 	GenericValue productSourPrice = EntityUtil.getFirst(productPrices);
   		 	result.put("sourRate", productSourPrice.getBigDecimal("price"));   		 	
	        // Check for minimum quality  		 	
   		 	procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_MIN_QLTY"));
   	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logInfo("No minimum quality configuration found (" + procPriceEc + ")", module);
   		 	}
   		 	else {
   		 		if (fatPercent.compareTo(productPrices.get(0).getBigDecimal("fatPercent")) <= 0 ||
   		 			snfPercent.compareTo(productPrices.get(0).getBigDecimal("snfPercent")) <= 0) {
   	   		 		Debug.logInfo("Did not meet minimum milk quality threshold (" + productPrices.get(0) + ")", module);
   	   		 		// lets populate default price in result map
   	   		 		procPriceEcList.clear();
	   		        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	   		        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	   		        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SLAB1"));
	   		        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	   		        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
	   			 	if(UtilValidate.isEmpty(productPrices)){
	   			 		Debug.logInfo("No Default Price set (" + procPriceEc + ")", module);
	   			 	}
	   			 	GenericValue productDefaultPrice = EntityUtil.getFirst(productPrices);
	   			 	result.put("defaultRate", productDefaultPrice.getBigDecimal("price"));
	   			 	String useTotalSolids ="N";
	   			 	if(UtilValidate.isNotEmpty( productDefaultPrice.getString("useTotalSolids"))){
	   			 		useTotalSolids = productDefaultPrice.getString("useTotalSolids");
	   			 	}
	   			 	result.put("useTotalSolids",useTotalSolids);
   	   		 		return result;
   		 		}
   		 	} 	        
	        
	        procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, procurementPriceTypeId));
   	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logError("No valid product price found (" + procPriceEc + ")", module);
          		return ServiceUtil.returnError("No valid product price found (" + procPriceEc + ")");  
   		 	}
   		 	GenericValue productPrice = EntityUtil.getFirst(productPrices);
    		if (Debug.infoOn()) {
    			Debug.logInfo("ProcurementPrice =" + productPrice, module);
    		}
   		 	boolean useTotalSolids = false;
   		 	if (productPrice.getBoolean("useTotalSolids") != null) {
   		 		useTotalSolids = productPrice.getBoolean("useTotalSolids").booleanValue();
   		 	}
	   		defaultRate = productPrice.getBigDecimal("price");
	        result.put("defaultRate", defaultRate);
	        result.put("useTotalSolids", productPrice.getString("useTotalSolids"));
   		 	// lets get the base Snf percent from price chart 
   		 	procPriceEcList.clear();
	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_DED"));
	        procPriceEcList.add(EntityCondition.makeCondition("price", EntityOperator.EQUALS, BigDecimal.ZERO));
	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        List<GenericValue> baseSnfPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
	        
		 	if(UtilValidate.isEmpty(baseSnfPrices)){
		 		Debug.logError("No valid base snf set for the  product (" + procPriceEc + ")", module);
		 		return ServiceUtil.returnError("No valid product price found (" + procPriceEc + ")");  
		 	}
		 	GenericValue baseSnfPrice = EntityUtil.getFirst(baseSnfPrices);
   	        BigDecimal baseSnfPercent = baseSnfPrice.getBigDecimal("snfPercent");
   	        
   	        if("N".equalsIgnoreCase(useBaseSnf)){
   	        	baseSnfPercent = snfPercent;
   	        }
   	     
   		 	// get the rate based on fat percent  or total solids
   		 	BigDecimal fatSolidsDecimal = fatPercent.divide(new BigDecimal(100));
   		 	if (useTotalSolids) {
   		 		// Use total solids instead. 
   		 		// Note: for total solids we need to take the base snf. If snf is below the default, then
   		 		// the deductions will be applied below
   		 		fatSolidsDecimal = (fatPercent.add(baseSnfPercent)).divide(new BigDecimal(100));
   		 	}
    		price = fatSolidsDecimal.multiply(defaultRate);  
   		 	procPriceEcList.clear();    		
    		if (snfPercent.compareTo(baseSnfPercent) < 0) {
    			//Handle any deduction logic (slab-based)   
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_DED"));
       	        procPriceEcList.add(EntityCondition.makeCondition("snfPercent", EntityOperator.LESS_THAN_EQUAL_TO, snfPercent));
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        	productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, UtilMisc.toList("-snfPercent"), null, true);
   		 		if(!UtilValidate.isEmpty(productPrices)){
   		 			premium = productPrices.get(0).getBigDecimal("price");
   		 			premium = premium.multiply(new BigDecimal(-1));
   		 			price = price.add(premium);
   		 		}
   		 		else {
   		 			// should not come here, throw warning
   		           Debug.logWarning("Did not find SNF Deduction record for snfPercent=" + snfPercent, module);   		 			
   		 		}
    		} else if ((snfPercent.compareTo(baseSnfPercent)) > 0 && (fatPercent.compareTo(fatMinQualityToPremum) >=0)) {
    			//Handle any premium logic (not slab-based)      
    			// premium applicable when fatPercent is greater than MaxQualityFat and snfPercent is greater than 
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_PRM"));    				
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
    			productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 		if(!UtilValidate.isEmpty(productPrices)){
   		    		BigDecimal tempDecimal = (snfPercent.subtract(baseSnfPercent)).divide(productPrices.get(0).getBigDecimal("snfPercent"));    		   		 			
   		 			premium = tempDecimal.multiply(productPrices.get(0).getBigDecimal("price"));
   		 			price = price.add(premium);
   		 		} 		 	
        	}
    		//::TODO:: need to handle any incentives??
    	} catch (GenericEntityException e) {
           Debug.logError(e, module);
           return ServiceUtil.returnError(e.getMessage());
    	}	     
    	
        result.put("price", price);
        result.put("premium", premium);        
		if (Debug.infoOn()) {
			Debug.logInfo("result =" + result, module);
		}        
        return result;
    }
    
    /*
	 * SADF Helper Method Computes the procurement price based on the input parameters. 
	 * The premium/deduction amount applied is also returned.  All values are per Kg.
	 * Default Rate is also returned by this function on per Kg fat (or per Kg solids).
	 * 
	 */
	
    public static Map<String, Object> calculateProcurementProductPriceSADF(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String priceChartId = (String) context.get("priceChartId");
        String productId = (String) context.get("productId");    
        String facilityId = (String) context.get("facilityId");
        String categoryTypeEnum = (String) context.get("categoryTypeEnum");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
        BigDecimal fatPercent = (BigDecimal) context.get("fatPercent");
        BigDecimal snfPercent = (BigDecimal) context.get("snfPercent");
        BigDecimal fatMinQualityToPremum = BigDecimal.ZERO;
        List<EntityCondition> procPriceEcList = FastList.newInstance();
        String useBaseSnf = "Y";
        if (UtilValidate.isEmpty(fatPercent)) {
        	fatPercent = BigDecimal.ZERO;
        }else{
        	//lets check this price chart using baseSnf or not
        	try{
        		GenericValue priceChartIdDetails = delegator.findOne("ProcurementPriceChart",UtilMisc.toMap("procPriceChartId",priceChartId), false);
        		/*if(UtilValidate.isNotEmpty(priceChartIdDetails)){
        			useBaseSnf = (String)priceChartIdDetails.get("useBaseSnf");
        		}
*/        	}catch (GenericEntityException e) {
        		Debug.logError("Error while getting useBaseSnf Value=======>"+e.getMessage(),module);
        		ServiceUtil.returnError("Error while getting useBaseSnf Value:");
        	}
        	// lets override high fat with max value fat 
        	/*procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS , "PROC_PRICE_MAX_QLTY" ));
	        EntityCondition procPriceSlab = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        try{
	        	 List<GenericValue> productPricesMax = delegator.findList("ProcurementPrice", procPriceSlab, null, UtilMisc.toList("-snfPercent","-fatPercent"), null, false);	         	
	        	 if(UtilValidate.isNotEmpty(productPricesMax)){
	         		GenericValue productPriceMax = EntityUtil.getFirst(productPricesMax);	         		
	         		if(fatPercent.compareTo(productPriceMax.getBigDecimal("fatPercent")) > 0){
	         			fatPercent = productPriceMax.getBigDecimal("fatPercent");
	         		}
	         	}
	        }catch (Exception e) {
				// TODO: handle exception
	        	 Debug.logError(e, module);
	             return ServiceUtil.returnError(e.getMessage());
			}*/
	       
        }
       
        if (UtilValidate.isEmpty(snfPercent)) {
        	snfPercent = BigDecimal.ZERO;
        } 
        	snfPercent = snfPercent.setScale(2, rounding);
        
        Timestamp priceDate = (Timestamp) context.get("priceDate");
        if (UtilValidate.isEmpty(priceDate)) {
        	priceDate = UtilDateTime.nowTimestamp();
        } 
		if (Debug.infoOn()) {
			Debug.logInfo("fatPercent=" + fatPercent + "; snfPercent=" + snfPercent, module);
		} 
        BigDecimal defaultRate = BigDecimal.ZERO;
        BigDecimal sourRate = BigDecimal.ZERO;
        //BigDecimal sourPrice = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal premium = BigDecimal.ZERO;  
        result.put("defaultRate", defaultRate);
        result.put("sourRate", sourRate);
        //result.put("sourPrice", sourPrice);
        result.put("price", price);  
        result.put("premium", premium); 
        result.put("useTotalSolids", "N");
    	try {
    		
    		procPriceEcList.clear();
   	        //lets get the slab bucket for the given fat and snf
   	        String procurementPriceTypeId = null;
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.LIKE, "PROC_PRICE_SLAB"+"%"));
	        EntityCondition procPriceSlab = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        
	        List<GenericValue> productPriceSlabs = delegator.findList("ProcurementPrice", procPriceSlab, null, UtilMisc.toList("-snfPercent","-fatPercent"), null, false);
	        Debug.logInfo("productPriceSlabs =========="+productPriceSlabs, module);
	        for( GenericValue productPriceSlab  : productPriceSlabs ){
   	        	if(snfPercent.compareTo(productPriceSlab.getBigDecimal("snfPercent")) >= 0 && fatPercent.compareTo(productPriceSlab.getBigDecimal("fatPercent")) >= 0 ){
   	        		procurementPriceTypeId = productPriceSlab.getString("procurementPriceTypeId");
   	        		break; 	        		
   	        		
   	        	}
   	        	
   	        }
	        // lets get the minimum fat percent to calculate premum(add)
	        GenericValue productPriceDefaultSlab = EntityUtil.getFirst(EntityUtil.filterByAnd(productPriceSlabs, UtilMisc.toMap("procurementPriceTypeId","PROC_PRICE_SLAB1")));
	        fatMinQualityToPremum = productPriceDefaultSlab.getBigDecimal("fatPercent");
	        Debug.logInfo("procurementPriceTypeId =========="+procurementPriceTypeId, module);
	        EntityCondition procPriceEc = null;
	        List<GenericValue> productPrices  = FastList.newInstance();
	        //lets get the sour Rate
	     /*   procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SOUR"));
   	        EntityCondition procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        List<GenericValue> productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logInfo("No sour Price configuration found (" + procPriceEc + ")", module);
   		 	}
   		 	GenericValue productSourPrice = EntityUtil.getFirst(productPrices);
   		 	result.put("sourRate", productSourPrice.getBigDecimal("price"));   		 	*/
	        // Check for minimum quality  		 	
   		 	procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_MIN_QLTY"));
   	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logInfo("No minimum quality configuration found (" + procPriceEc + ")", module);
   		 	}
   		 	else {
   		 		if (fatPercent.compareTo(productPrices.get(0).getBigDecimal("fatPercent")) <= 0 ||
   		 			snfPercent.compareTo(productPrices.get(0).getBigDecimal("snfPercent")) <= 0) {
   	   		 		Debug.logInfo("Did not meet minimum milk quality threshold (" + productPrices.get(0) + ")", module);
   	   		 		// lets populate default price in result map
   	   		 		procPriceEcList.clear();
	   		        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	   		        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	   		        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SLAB1"));
	   		        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	   		        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
	   			 	if(UtilValidate.isEmpty(productPrices)){
	   			 		Debug.logInfo("No Default Price set (" + procPriceEc + ")", module);
	   			 	}
	   			 	GenericValue productDefaultPrice = EntityUtil.getFirst(productPrices);
	   			 	
	   			 	result.put("defaultRate", productDefaultPrice.getBigDecimal("tsValue"));
	   			 	String useTotalSolids ="N";
	   			 	if(UtilValidate.isNotEmpty( productDefaultPrice.getString("useTotalSolids"))){
	   			 		useTotalSolids = productDefaultPrice.getString("useTotalSolids");
	   			 	}
	   			 	result.put("useTotalSolids",useTotalSolids);
   	   		 		return result;
   		 		}
   		 	} 	        
	        
	        procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, procurementPriceTypeId));
   	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logError("No valid product price found (" + procPriceEc + ")", module);
          		return ServiceUtil.returnError("No valid product price found (" + procPriceEc + ")");  
   		 	}
   		 	GenericValue productPrice = EntityUtil.getFirst(productPrices);
    		if (Debug.infoOn()) {
    			Debug.logInfo("ProcurementPrice =" + productPrice, module);
    		}
   		 	boolean useTotalSolids = false;
   		 	if (productPrice.getBoolean("useTotalSolids") != null) {
   		 		useTotalSolids = productPrice.getBoolean("useTotalSolids").booleanValue();
   		 	}
	   		defaultRate = productPrice.getBigDecimal("tsValue");
	        result.put("defaultRate", defaultRate);
	        result.put("useTotalSolids", productPrice.getString("useTotalSolids"));
	        BigDecimal baseSnfPercent = snfPercent;
   		 	// lets get the base Snf percent from price chart 
   		 	/*procPriceEcList.clear();
	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_DED"));
	        procPriceEcList.add(EntityCondition.makeCondition("price", EntityOperator.EQUALS, BigDecimal.ZERO));
	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        List<GenericValue> baseSnfPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
	        
		 	if(UtilValidate.isEmpty(baseSnfPrices)){
		 		Debug.logError("No valid base snf set for the  product (" + procPriceEc + ")", module);
		 		return ServiceUtil.returnError("No valid product price found (" + procPriceEc + ")");  
		 	}
		 	GenericValue baseSnfPrice = EntityUtil.getFirst(baseSnfPrices);
   	        BigDecimal baseSnfPercent = baseSnfPrice.getBigDecimal("snfPercent");
   	        
   	        if("N".equalsIgnoreCase(useBaseSnf)){
   	        	baseSnfPercent = snfPercent;
   	        }*/
   	     
   		 	/*// get the rate based on fat percent  or total solids
   		 	BigDecimal fatSolidsDecimal = fatPercent.divide(new BigDecimal(100));
   		 	if (useTotalSolids) {
   		 		// Use total solids instead. 
   		 		// Note: for total solids we need to take the base snf. If snf is below the default, then
   		 		// the deductions will be applied below
   		 		fatSolidsDecimal = (fatPercent.add(baseSnfPercent)).divide(new BigDecimal(100));
   		 	}*/
    		price = (fatPercent.add(baseSnfPercent)).multiply(defaultRate);  
   		/* 	procPriceEcList.clear();    		
    		if (snfPercent.compareTo(baseSnfPercent) < 0) {
    			//Handle any deduction logic (slab-based)   
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_DED"));
       	        procPriceEcList.add(EntityCondition.makeCondition("snfPercent", EntityOperator.LESS_THAN_EQUAL_TO, snfPercent));
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        	productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, UtilMisc.toList("-snfPercent"), null, true);
   		 		if(!UtilValidate.isEmpty(productPrices)){
   		 			premium = productPrices.get(0).getBigDecimal("price");
   		 			premium = premium.multiply(new BigDecimal(-1));
   		 			price = price.add(premium);
   		 		}
   		 		else {
   		 			// should not come here, throw warning
   		           Debug.logWarning("Did not find SNF Deduction record for snfPercent=" + snfPercent, module);   		 			
   		 		}
    		} else if ((snfPercent.compareTo(baseSnfPercent)) > 0 && (fatPercent.compareTo(fatMinQualityToPremum) >=0)) {
    			//Handle any premium logic (not slab-based)      
    			// premium applicable when fatPercent is greater than MaxQualityFat and snfPercent is greater than 
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_PRM"));    				
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
    			productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 		if(!UtilValidate.isEmpty(productPrices)){
   		    		BigDecimal tempDecimal = (snfPercent.subtract(baseSnfPercent)).divide(productPrices.get(0).getBigDecimal("snfPercent"));    		   		 			
   		 			premium = tempDecimal.multiply(productPrices.get(0).getBigDecimal("price"));
   		 			price = price.add(premium);
   		 		} 		 	
        	}*/
    		//::TODO:: need to handle any incentives??
    	} catch (GenericEntityException e) {
           Debug.logError(e, module);
           return ServiceUtil.returnError(e.getMessage());
    	}	     
    	
        result.put("price", price);
        result.put("premium", premium);        
		if (Debug.infoOn()) {
			Debug.logInfo("result =" + result, module);
		}        
        return result;
    }
 
    
    
    
    /*
	 *  Computes the procurement price based on the input parameters. 
	 * The premium/deduction amount applied is also returned.  All values are per Kg.
	 * Default Rate is also returned by this function on per Kg fat (or per Kg solids).
	 * 
	 */
    
    public static Map<String, Object> getProcurementProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productId = (String) context.get("productId");    
        String facilityId = (String) context.get("facilityId");
        String categoryTypeEnum = (String) context.get("categoryTypeEnum");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");           
        Timestamp priceDate = (Timestamp) context.get("priceDate");
        String uomId = null;
        String lrFormulaId=null;
        // pass in this flag to, get special  premium price
        String isPremiumChart = (String) context.get("isPremiumChart");
        if (UtilValidate.isEmpty(priceDate)) {
        	priceDate = UtilDateTime.nowTimestamp();
        }
        BigDecimal fatPercent = (BigDecimal) context.get("fatPercent");
        BigDecimal snfPercent = (BigDecimal) context.get("snfPercent");
        List<BigDecimal> fatPercentList = FastList.newInstance();
        List<BigDecimal> snfPercentList = FastList.newInstance();
        if (UtilValidate.isEmpty(fatPercent) && UtilValidate.isEmpty(snfPercent)) {
        	BigDecimal minSnf = new BigDecimal(7.5);
            BigDecimal maxSnf = new BigDecimal(12.0);
            
            while(minSnf.compareTo(maxSnf) < 0){	
            	snfPercentList.add(minSnf.setScale(1,0));
            	minSnf = minSnf.add(new BigDecimal(0.1));
            }
            
            BigDecimal minFat = new BigDecimal(2.5);
            BigDecimal  maxFat= new BigDecimal(9);
            while(minFat.compareTo(maxFat) < 0){	
            	fatPercentList.add(minFat.setScale(1,0));
            	minFat = minFat.add(new BigDecimal(0.1));
            }
            
        }  
        BigDecimal defaultRate = BigDecimal.ZERO;		
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal premium = BigDecimal.ZERO; 
        String useTotalSolids ="N";
        result.put("defaultRate", defaultRate);          
        result.put("price", price);  
        result.put("premium", premium);
        result.put("useTotalSolids", useTotalSolids);
        Map priceChartMap = FastMap.newInstance();
    	try {
    		// Fist get the appropriate Procurement Chart 
   		 	GenericValue priceChart = fetchPriceChart(dctx, UtilMisc.toMap("priceDate", priceDate, "facilityId", facilityId ,"categoryTypeEnum", categoryTypeEnum ,"supplyTypeEnumId" , supplyTypeEnumId ,"isPremiumChart" ,isPremiumChart));  
   		 	if(UtilValidate.isEmpty(priceChart)){
   		 		Debug.logInfo("No valid price chart found!", module);
          		return ServiceUtil.returnError("No valid price chart found!");  
   		 	} 
   		 	if(UtilValidate.isNotEmpty(priceChart.get("uomId"))){
		 		uomId = priceChart.getString("uomId");
		 	}
   		 	if(UtilValidate.isNotEmpty(priceChart.get("lrFormulaId"))){
   		 		lrFormulaId = priceChart.getString("lrFormulaId");
   		 	}
   		 	// Compute price from the chart based on the input parameters
   		 	
   	        BigDecimal tempFatPercent =BigDecimal.ZERO;
    		BigDecimal tempSnfPercent =BigDecimal.ZERO;
    		Map priceInMap =FastMap.newInstance();
    		priceInMap.putAll(context);
    		priceInMap.put("priceChartId",  priceChart.getString("procPriceChartId"));
    		if(UtilValidate.isEmpty(fatPercent) && UtilValidate.isEmpty(snfPercent)){
    			for(int j=0; j<fatPercentList.size(); j++){
    				tempFatPercent = fatPercentList.get(j);
    				priceInMap.put("fatPercent", tempFatPercent);
		 			Map tempSnfPriceMap = FastMap.newInstance();
		   		 	for(int i=0; i<snfPercentList.size();i++){
		   		 		tempSnfPercent = snfPercentList.get(i);
		   		 		price = BigDecimal.ZERO;
		   		 		priceInMap.put("snfPercent", tempSnfPercent);
		   		 		
		   		 		result = calculateProcurementProductPrice(dctx ,priceInMap);
		   		 		
		   		 		price = (BigDecimal)result.get("price");
		   		 		useTotalSolids = (String)result.get("useTotalSolids");
	   		 			tempSnfPriceMap.put(tempSnfPercent, price.setScale(2,BigDecimal.ROUND_HALF_UP));
	   		 		} // end of FatList for loop  		 		
	   		 		Map tempSnfPrice = FastMap.newInstance();
	   		 		tempSnfPrice.putAll(tempSnfPriceMap);
	   		 		priceChartMap.put(tempFatPercent,tempSnfPrice);
   		 		}// end of SnfList for loop
    		}else{    			
    			priceInMap.put("snfPercent", snfPercent);
    			priceInMap.put("fatPercent", fatPercent);
   		 		result = calculateProcurementProductPrice(dctx ,priceInMap);
   		 		if(UtilValidate.isNotEmpty(result.get("uomId"))){
   		 			uomId = (String)result.get("uomId"); 
   		 		}
   		 		
   		 		price = (BigDecimal)result.get("price");
   		 		premium = (BigDecimal)result.get("premium");
   		 		useTotalSolids = (String)result.get("useTotalSolids");   		 		
    		}
    		
   		 	
    	} catch (Exception e) {
           Debug.logError(e, module);
           return ServiceUtil.returnError(e.getMessage());
    	}	     
    	result.put("priceChartMap", priceChartMap);
    	result.put("snfPercentList", snfPercentList);
        result.put("price", price);
        result.put("uomId", uomId);
        result.put("premium", premium);  
        result.put("lrFormulaId", lrFormulaId);
        result.put("useTotalSolids", useTotalSolids);
		if (Debug.infoOn()) {
			Debug.logInfo("result =" + result, module);
		}        
        return result;
    }
    /**
     * updates prices if Price chart exits otherwise creates new price Chart 
     * @param request
     * @param response
     * @return
     */
    
    public static String createPriceChart(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx = dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		String facilityId = (String)paramMap.get("facilityId"+UtilHttp.MULTI_ROW_DELIMITER+0);
		String supplyTypeEnumId = (String)paramMap.get("supplyTypeEnumId"+UtilHttp.MULTI_ROW_DELIMITER+0);
		String categoryTypeEnum = (String)paramMap.get("categoryTypeEnum"+UtilHttp.MULTI_ROW_DELIMITER+0); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp timestamp = UtilDateTime.nowTimestamp();
		Timestamp currentPriceTillDate = UtilDateTime.nowTimestamp();
		Timestamp priceDate = UtilDateTime.nowTimestamp();
		Debug.log("parameters :: "+paramMap);
		Debug.log("facilityID : "+UtilHttp.MULTI_ROW_DELIMITER );
		if(paramMap.get("priceDate"+UtilHttp.MULTI_ROW_DELIMITER+0)!=null){
			try {
				java.util.Date parsedDate = sdf.parse((String)paramMap.get("priceDate"+UtilHttp.MULTI_ROW_DELIMITER+0));
				priceDate = new java.sql.Timestamp(parsedDate.getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+priceDate, "");
		   
			}
		}
		currentPriceTillDate = UtilDateTime.getDayEnd(priceDate);
		Timestamp priceApplicableDate = UtilDateTime.nowTimestamp();
		priceApplicableDate = UtilDateTime.getNextDayStart(priceDate);
		Map<String, Object> inMap = FastMap.newInstance();
		inMap.put("userLogin",userLogin);
		inMap.put("facilityId",facilityId);
		inMap.put("priceDate",priceApplicableDate);
		inMap.put("supplyTypeEnumId",supplyTypeEnumId);
		inMap.put("categoryTypeEnum",categoryTypeEnum);
		GenericValue priceChart = fetchPriceChart(dctx, inMap);  
		String procPriceChartId = null;
		if(priceChart != null){
			procPriceChartId = (String)priceChart.get("procPriceChartId");
		}
		String regionId = null;
		GenericValue	priceChartDetails = null;
		try{
			priceChartDetails = delegator.findOne("ProcurementPriceChart",UtilMisc.toMap("procPriceChartId",procPriceChartId),false);
		} catch(GenericEntityException e){
			Debug.logError("price chart details not found : "+e.getMessage(),module);
		}
		regionId = (String)priceChartDetails.get("regionId");
		String categoryType = (String)priceChartDetails.get("categoryTypeEnum");
		if(categoryType == null){
			categoryType="";
		}
		String supplyType = (String)priceChartDetails.get("supplyTypeEnumId");
		if(supplyType == null){
			supplyType="";
		}
		// If regionId and facilityId are same then update thruDate of price Chart		
		Timestamp fromDate = (Timestamp)priceChartDetails.get("fromDate");		
		try{
			TransactionUtil.begin();
			int days =0;
			// Existing price chart fromDate is same as priceApplicableDate then remove existing price chart and create new one with paramMap data.
			//Other wise populate thruDate and create new price chart.
			if((facilityId.equals(regionId)) && (supplyTypeEnumId.equals(supplyType)) && (categoryTypeEnum.equals(categoryType))){
					 days =UtilDateTime.getIntervalInDays(fromDate, priceApplicableDate);					
					 if(days ==0){
						List<GenericValue> procPriceList = delegator.findList("ProcurementPrice",EntityCondition.makeCondition("procPriceChartId",EntityOperator.EQUALS,procPriceChartId),null,null,null,false );
						for(GenericValue procurementPrice : procPriceList ){
							 GenericPK procurementPricePK = delegator.makePK("ProcurementPrice");
							 procurementPricePK.setPKFields(procurementPrice);
				             delegator.removeByPrimaryKey(procurementPricePK);
						}
						for(int i=0;i<rowCount;i++){
							 	BigDecimal fatPercent = new BigDecimal((String)paramMap.get("fatPercent"+UtilHttp.MULTI_ROW_DELIMITER+i));
				        	   	BigDecimal snfPercent = new BigDecimal((String)paramMap.get("snfPercent"+UtilHttp.MULTI_ROW_DELIMITER+i));
				        	   	BigDecimal price = new BigDecimal((String)paramMap.get("price"+UtilHttp.MULTI_ROW_DELIMITER+i));
				        	   	GenericValue procurementPriceValue = delegator.makeValue("ProcurementPrice");
				        	   	procurementPriceValue.put("productId", (String)paramMap.get("productId"+UtilHttp.MULTI_ROW_DELIMITER+i));
				        	   	procurementPriceValue.put("procPriceChartId", procPriceChartId);
				        	   	procurementPriceValue.put("procurementPriceTypeId", (String)paramMap.get("procurementPriceTypeId"+UtilHttp.MULTI_ROW_DELIMITER+i));
				        	   	procurementPriceValue.put("currencyUomId", "INR");
				        	   	procurementPriceValue.put("fatPercent", fatPercent);
				        	   	procurementPriceValue.put("snfPercent", snfPercent);
				        	   	procurementPriceValue.put("price", price);
				        	   	procurementPriceValue.put("lastModifiedDate",timestamp);
				        	   	procurementPriceValue.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				        	   	procurementPriceValue.put("createdByUserLogin", userLogin.get("userLoginId"));
				        	   	procurementPriceValue.put("createdDate",timestamp);
				        	   	delegator.create(procurementPriceValue);
					        }
				           TransactionUtil.commit();
				           return "success";
					}else{
						priceChartDetails.set("thruDate",currentPriceTillDate);
						priceChartDetails.set("lastModifiedDate",timestamp);
						priceChartDetails.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						priceChartDetails.store();
					}
			}
			//create new procPriceChartId 
			   String chartId = null;
	           GenericValue newPriceChart = delegator.makeValue("ProcurementPriceChart");
	           newPriceChart.put("regionId",facilityId);
	           newPriceChart.put("categoryTypeEnum",categoryTypeEnum);
	           newPriceChart.put("supplyTypeEnumId",supplyTypeEnumId);
	           newPriceChart.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	           newPriceChart.put("createdByUserLogin", userLogin.get("userLoginId"));
	           newPriceChart.put("fromDate",priceApplicableDate);
	           newPriceChart.put("lastModifiedDate",timestamp);
	           delegator.createSetNextSeqId(newPriceChart);
	           chartId = (String)newPriceChart.get("procPriceChartId"); 
	        //inserting prices
	           for(int i=0;i<rowCount;i++){
	        	   	BigDecimal fatPercent = new BigDecimal((String)paramMap.get("fatPercent"+UtilHttp.MULTI_ROW_DELIMITER+i));
	        	   	BigDecimal snfPercent = new BigDecimal((String)paramMap.get("snfPercent"+UtilHttp.MULTI_ROW_DELIMITER+i));
	        	   	BigDecimal price = new BigDecimal((String)paramMap.get("price"+UtilHttp.MULTI_ROW_DELIMITER+i));
	        	   	GenericValue procurementPrice = delegator.makeValue("ProcurementPrice");
	        	   	procurementPrice.put("productId", (String)paramMap.get("productId"+UtilHttp.MULTI_ROW_DELIMITER+i));
	        	   	procurementPrice.put("procPriceChartId", chartId);
					procurementPrice.put("procurementPriceTypeId", (String)paramMap.get("procurementPriceTypeId"+UtilHttp.MULTI_ROW_DELIMITER+i));
					procurementPrice.put("currencyUomId", "INR");
					procurementPrice.put("fatPercent", fatPercent);
					procurementPrice.put("snfPercent", snfPercent);
					procurementPrice.put("price", price);
					procurementPrice.put("lastModifiedDate",timestamp);
					procurementPrice.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					procurementPrice.put("createdByUserLogin", userLogin.get("userLoginId"));
					procurementPrice.put("createdDate",timestamp);
					delegator.create(procurementPrice);
		        }
	           TransactionUtil.commit();				
			}catch(GenericEntityException e){
				try{
					TransactionUtil.rollback();
				}catch(GenericTransactionException e1){
					Debug.logError(e1.getMessage(),module);
					return "error";
				}
				Debug.logError(e.getMessage(),module);
				return "error";
			}
		request.setAttribute("facilityId",facilityId);
		request.setAttribute("supplyTypeEnumId",supplyTypeEnumId);
		request.setAttribute("priceDate",priceDate);
		return "success";
    }// End of the service
    /**
     * 
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> calculateMilkReceiptProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String priceChartId = (String) context.get("priceChartId");
        String productId = (String) context.get("productId");    
        String facilityId = (String) context.get("facilityId");
        String categoryTypeEnum = (String) context.get("categoryTypeEnum");
        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
        BigDecimal fatPercent = (BigDecimal) context.get("fatPercent");
        BigDecimal snfPercent = (BigDecimal) context.get("snfPercent");
        BigDecimal fatMinQualityToPremum = BigDecimal.ZERO;
        BigDecimal snfMaxQuality = BigDecimal.ZERO;
        result.put("price", BigDecimal.ZERO);
        result.put("fatPremium", BigDecimal.ZERO);
        result.put("snfPremium", BigDecimal.ZERO);
        List<EntityCondition> procPriceEcList = FastList.newInstance();
        String billQuantity= null;
        String useBaseSnf = "Y";
        String useBaseFat = "Y";
        String uomId = "VLIQ_KGFAT";
        if (UtilValidate.isEmpty(fatPercent)) {
        	fatPercent = BigDecimal.ZERO;
        }else{
        	//lets check this price chart using baseSnf or not
        	try{
        		GenericValue priceChartIdDetails = delegator.findOne("ProcurementPriceChart",UtilMisc.toMap("procPriceChartId",priceChartId), false);
        		if(UtilValidate.isNotEmpty(priceChartIdDetails)){
        			useBaseSnf = (String)priceChartIdDetails.get("useBaseSnf");
        			useBaseFat= (String)priceChartIdDetails.get("useBaseFat");
        			uomId = (String)priceChartIdDetails.get("uomId");
        			billQuantity = (String) priceChartIdDetails.get("billQuantity");
        		}
        	}catch (GenericEntityException e) {
        		Debug.logError("Error while getting useBaseSnf Value=======>"+e.getMessage(),module);
        		ServiceUtil.returnError("Error while getting useBaseSnf Value:");
        	}
        	
        	// lets override high fat with max value fat 
        	procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS , "PROC_PRICE_MAX_QLTY" ));
	        EntityCondition procPriceSlab = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        try{
	        	 List<GenericValue> productPricesMax = delegator.findList("ProcurementPrice", procPriceSlab, null, UtilMisc.toList("-snfPercent","-fatPercent"), null, false);	         	
	        	 if(UtilValidate.isNotEmpty(productPricesMax)){
	         		GenericValue productPriceMax = EntityUtil.getFirst(productPricesMax);	         		
	         		if(fatPercent.compareTo(productPriceMax.getBigDecimal("fatPercent")) > 0){
	         			fatPercent = productPriceMax.getBigDecimal("fatPercent");
	         		}
	         		if(UtilValidate.isNotEmpty(productPriceMax.getBigDecimal("snfPercent"))){
	         			snfMaxQuality = productPriceMax.getBigDecimal("snfPercent");
	         		}
	         	}else{
	         		return result;
	         	}
	        }catch (Exception e) {
				// TODO: handle exception
	        	 Debug.logError(e, module);
	             return ServiceUtil.returnError(e.getMessage());
			}
	       
        }
       
        if (UtilValidate.isEmpty(snfPercent)) {
        	snfPercent = BigDecimal.ZERO;
        }
         //Don't merge the below Line, this is specific to APDairy	
          snfPercent = snfPercent.setScale(1, rounding);
        	
        Timestamp priceDate = (Timestamp) context.get("priceDate");
        if (UtilValidate.isEmpty(priceDate)) {
        	priceDate = UtilDateTime.nowTimestamp();
        } 
		if (Debug.infoOn()) {
			Debug.logInfo("fatPercent=" + fatPercent + "; snfPercent=" + snfPercent, module);
		} 
        BigDecimal defaultRate = BigDecimal.ZERO;
        BigDecimal sourRate = BigDecimal.ZERO;
        //BigDecimal sourPrice = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal snfPremium = BigDecimal.ZERO;
        BigDecimal fatPremium = BigDecimal.ZERO;
        result.put("defaultRate", defaultRate);
        result.put("sourRate", sourRate);
        //result.put("sourPrice", sourPrice);
        result.put("price", price);  
        result.put("snfPremium", snfPremium);
        result.put("fatPremium", fatPremium);
        if(UtilValidate.isEmpty(billQuantity)){
        	billQuantity ="ACK_QTY";
        }
        result.put("billQuantity", billQuantity);
        result.put("useTotalSolids", "N");
    	try {
    		
    		procPriceEcList.clear();
   	        //lets get the slab bucket for the given fat and snf
   	        String procurementPriceTypeId = null;
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.LIKE, "PROC_PRICE_SLAB"+"%"));
	        EntityCondition procPriceSlab = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        List<GenericValue> productPriceSlabs = delegator.findList("ProcurementPrice", procPriceSlab, null, UtilMisc.toList("-snfPercent","-fatPercent"), null, false);
	        for( GenericValue productPriceSlab  : productPriceSlabs ){
   	        	if(UtilValidate.isNotEmpty(productPriceSlab.get("procurementPriceTypeId")) ){
   	        		procurementPriceTypeId = productPriceSlab.getString("procurementPriceTypeId");
   	        		break; 	        		
   	        	}
   	        	
   	        }
	        // lets get the minimum fat percent to calculate premum(add)
	        GenericValue productPriceDefaultSlab = EntityUtil.getFirst(EntityUtil.filterByAnd(productPriceSlabs, UtilMisc.toMap("procurementPriceTypeId","PROC_PRICE_SLAB1")));
	        fatMinQualityToPremum = productPriceDefaultSlab.getBigDecimal("fatPercent");
	        result.put("uomId",uomId);
	        if(UtilValidate.isNotEmpty(productPriceDefaultSlab.get("uomId"))){
	   	    	result.put("uomId",productPriceDefaultSlab.get("uomId"));
	   	    }else{
	   	    	result.put("uomId","VLIQ_KGFAT");
	   	    }
	        if(fatPercent.compareTo(BigDecimal.ZERO)==0 && snfPercent.compareTo(BigDecimal.ZERO)==0){
	        	return result;
	        }
	        //lets get the sour Rate
	        procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SOUR"));
   	        EntityCondition procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        List<GenericValue> productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logInfo("No sour Price configuration found (" + procPriceEc + ")", module);
   		 	}
   		 	GenericValue productSourPrice = EntityUtil.getFirst(productPrices);
   		 	result.put("sourRate", productSourPrice.getBigDecimal("price"));   		 	
	        // Check for minimum quality  		 	
   		 	procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_MIN_QLTY"));
   	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logInfo("No minimum quality configuration found (" + procPriceEc + ")", module);
   		 	}
   		 	else {
   		 		if (fatPercent.compareTo(productPrices.get(0).getBigDecimal("fatPercent")) <= 0 ||
   		 			snfPercent.compareTo(productPrices.get(0).getBigDecimal("snfPercent")) <= 0) {
   	   		 		Debug.logInfo("Did not meet minimum milk quality threshold (" + productPrices.get(0) + ")", module);
   	   		 		// lets populate default price in result map
   	   		 		procPriceEcList.clear();
	   		        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	   		        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	   		        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SLAB1"));
	   		        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	   		        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
	   			 	if(UtilValidate.isEmpty(productPrices)){
	   			 		Debug.logInfo("No Default Price set (" + procPriceEc + ")", module);
	   			 	}
	   			 	GenericValue productDefaultPrice = EntityUtil.getFirst(productPrices);
	   			 	result.put("defaultRate", productDefaultPrice.getBigDecimal("price"));
	   			 	String useTotalSolids ="N";
	   			 	if(UtilValidate.isNotEmpty( productDefaultPrice.getString("useTotalSolids"))){
	   			 		useTotalSolids = productDefaultPrice.getString("useTotalSolids");
	   			 	}
	   			 	result.put("useTotalSolids",useTotalSolids);
   	   		 		return result;
   		 		}
   		 	} 	        
	        procPriceEcList.clear();
   	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
   	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, procurementPriceTypeId));
   	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 	if(UtilValidate.isEmpty(productPrices)){
   		 		Debug.logError("No valid product price found (" + procPriceEc + ")", module);
          		return ServiceUtil.returnError("No valid product price found (" + procPriceEc + ")");  
   		 	}
   		 	GenericValue productPrice = EntityUtil.getFirst(productPrices);
    		if (Debug.infoOn()) {
    			Debug.logInfo("ProcurementPrice =" + productPrice, module);
    		}
   		 	boolean useTotalSolids = false;
   		 	if (productPrice.getBoolean("useTotalSolids") != null) {
   		 		useTotalSolids = productPrice.getBoolean("useTotalSolids").booleanValue();
   		 	}
	   		defaultRate = productPrice.getBigDecimal("price");
	        result.put("defaultRate", defaultRate);
	        result.put("useTotalSolids", productPrice.getString("useTotalSolids"));
   		 	// lets get the base Snf percent from price chart 
   		 	procPriceEcList.clear();
	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_DED"));
	        procPriceEcList.add(EntityCondition.makeCondition("price", EntityOperator.EQUALS, BigDecimal.ZERO));
	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        List<GenericValue> baseSnfPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
		 	if(UtilValidate.isEmpty(baseSnfPrices)){
		 		Debug.logError("No valid base snf set for the  product (" + procPriceEc + ")", module);
		 		return ServiceUtil.returnError("No valid product price found (" + procPriceEc + ")");  
		 	}
		 	GenericValue baseSnfPrice = EntityUtil.getFirst(baseSnfPrices);
   	        BigDecimal baseSnfPercent = baseSnfPrice.getBigDecimal("snfPercent");
   	        
   	        if("N".equalsIgnoreCase(useBaseSnf)){
   	        	baseSnfPercent = snfPercent;
   	        }
   	        
   	  // lets get the base Fat percent from price chart 
   		 	procPriceEcList.clear();
	        procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
	        procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_FAT_DED"));
	        procPriceEcList.add(EntityCondition.makeCondition("price", EntityOperator.EQUALS, BigDecimal.ZERO));
	        procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
	        List<GenericValue> baseFatPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
	        
	        BigDecimal baseFatPercent = fatPercent;
		 	if(UtilValidate.isNotEmpty(baseFatPrices)){
		 		GenericValue baseFatPrice = EntityUtil.getFirst(baseFatPrices);
		 		baseFatPercent = baseFatPrice.getBigDecimal("fatPercent");
		 	}
   	        if("N".equalsIgnoreCase(useBaseFat)){
   	        	baseFatPercent = fatPercent;
   	        }
   	        
   		 	// get the rate based on fat percent  or total solids
   		 	BigDecimal fatSolidsDecimal = fatPercent.divide(new BigDecimal(100));
   		 	if (useTotalSolids) {
   		 		// Use total solids instead. 
   		 		// Note: for total solids we need to take the base snf. If snf is below the default, then
   		 		// the deductions will be applied below
   		 		fatSolidsDecimal = (fatPercent.add(baseSnfPercent)).divide(new BigDecimal(100));
   		 	}
   		 	if(UtilValidate.isNotEmpty(uomId)&&  uomId.equalsIgnoreCase("VLIQ_KG")){
   		 		price= defaultRate;
   		 	}else{
   		 		price = fatSolidsDecimal.multiply(defaultRate);
   		 	}
    		  
   		 	procPriceEcList.clear();    		
    		if (snfPercent.compareTo(baseSnfPercent) < 0) {
    			//Handle any deduction logic (slab-based)   
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_DED"));
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        	productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   	        	if(!UtilValidate.isEmpty(productPrices)){
	   		    		BigDecimal tempDecimal = (baseSnfPercent.subtract(snfPercent)).divide(productPrices.get(0).getBigDecimal("snfPercent"),2,BigDecimal.ROUND_HALF_UP);    		   		 			
	   		 			snfPremium = tempDecimal.multiply(productPrices.get(0).getBigDecimal("price"));
	   		 			snfPremium = snfPremium.multiply(new BigDecimal(-1));
	   		 			price = price.add(snfPremium);
   		 		}
   		 		else {
   		 			// should not come here, throw warning
   		           Debug.logWarning("Did not find SNF Deduction record for snfPercent=" + snfPercent, module);   		 			
   		 		}
    		} else if ((snfPercent.compareTo(baseSnfPercent)) > 0 && (fatPercent.compareTo(fatMinQualityToPremum) >=0)) {
    			//Handle any snfPremium logic (not slab-based)      
    			// snfPremium applicable when fatPercent is greater than MaxQualityFat and snfPercent is greater than 10 
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_SNF_PRM"));    				
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
    			productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 		if(!UtilValidate.isEmpty(productPrices)){
   		 			BigDecimal tempDecimal = BigDecimal.ZERO;
   		 			if(snfPercent.compareTo(snfMaxQuality)>0){
   		 			tempDecimal = (snfMaxQuality.subtract(baseSnfPercent)).divide(productPrices.get(0).getBigDecimal("snfPercent"));
   		 			}else{
   		 				tempDecimal = (snfPercent.subtract(baseSnfPercent)).divide(productPrices.get(0).getBigDecimal("snfPercent"));
   		 			}
   		    		snfPremium = tempDecimal.multiply(productPrices.get(0).getBigDecimal("price"));
   		 			price = price.add(snfPremium);
   		 		} 		 	
        	}
    		procPriceEcList.clear();    		
    		if (fatPercent.compareTo(baseFatPercent) < 0) {
    			//Handle any deduction logic (slab-based)   
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_FAT_DED"));
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
   	        	productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 		if(!UtilValidate.isEmpty(productPrices)){
   		 			BigDecimal tempDecimal = (baseFatPercent.subtract(fatPercent)).divide(productPrices.get(0).getBigDecimal("fatPercent"));    		   		 			
		 			fatPremium = tempDecimal.multiply(productPrices.get(0).getBigDecimal("price"));
   		 			fatPremium = fatPremium.multiply(new BigDecimal(-1));
   		 			price = price.add(fatPremium);
   		 		}
   		 		else {
   		 			// should not come here, throw warning
   		           Debug.logWarning("Did not find FAT Deduction record for fatPercent=" + fatPercent, module);   		 			
   		 		}
    		} else if ((fatPercent.compareTo(baseFatPercent)) > 0 && (fatPercent.compareTo(fatMinQualityToPremum) >=0)) {
    			//Handle any FAT Premium logic (not slab-based)      
    			// fatPremium applicable when fatPercent is greater than MaxQualityFat and fatPercent is greater than 10 
    			procPriceEcList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, priceChartId));   	        
    			procPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    			procPriceEcList.add(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_FAT_PRM"));    				
    			procPriceEc = EntityCondition.makeCondition(procPriceEcList, EntityOperator.AND);
    			productPrices = delegator.findList("ProcurementPrice", procPriceEc, null, null, null, true);
   		 		if(!UtilValidate.isEmpty(productPrices)){
   		    		BigDecimal tempDecimal = (fatPercent.subtract(baseFatPercent)).divide(productPrices.get(0).getBigDecimal("fatPercent"));
   		 			fatPremium = tempDecimal.multiply(productPrices.get(0).getBigDecimal("price"));
   		 			price = price.add(fatPremium);
   		 		} 		 	
        	}
    		
    		//::TODO:: need to handle any incentives??
    	} catch (GenericEntityException e) {
           Debug.logError(e, module);
           return ServiceUtil.returnError(e.getMessage());
    	}	     
    	
        result.put("price", price);
        result.put("snfPremium", snfPremium);
        result.put("fatPremium", fatPremium);
		if (Debug.infoOn()) {
			Debug.logInfo("result =" + result, module);
		}        
        return result;
    }
   
}

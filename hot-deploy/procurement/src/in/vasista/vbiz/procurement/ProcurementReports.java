package in.vasista.vbiz.procurement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;




import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

public class ProcurementReports {
 	public static final String module = ProcurementReports.class.getName();
    private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals;
    private static int rounding;
    public static final String resource_error = "OrderErrorUiLabels";
    static {
        decimals = 3;//UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO = ZERO.setScale(decimals); 
    }	

    private static Map<String, Object> initFieldsMap() {
		Map<String, Object> fieldsMap = FastMap.newInstance();
		fieldsMap.put("qtyLtrs", ZERO);
		fieldsMap.put("qtyKgs", ZERO);
		fieldsMap.put("kgFat", ZERO);
		fieldsMap.put("zeroKgFat", ZERO);
		fieldsMap.put("zeroKgSnf", ZERO);
		fieldsMap.put("kgSnf", ZERO);
		fieldsMap.put("fat", ZERO);		
		fieldsMap.put("snf", ZERO);		
		fieldsMap.put("price", ZERO);
		fieldsMap.put("sQtyLtrs", ZERO);
		fieldsMap.put("sFat", ZERO);
		fieldsMap.put("sKgFat", ZERO);
		fieldsMap.put("sPrice", ZERO);
		fieldsMap.put("cQtyLtrs", ZERO);
		fieldsMap.put("ptcQtyKgs", ZERO);
		fieldsMap.put("totPrem", ZERO);
		fieldsMap.put("ptcMilkType", "");
		fieldsMap.put("gKgFat", ZERO);
		return fieldsMap;
    }
    
    private static Map<String, Object> initDayMap(List<String> productNames) {		
        Map<String, Object> result = FastMap.newInstance();
        result.put("AM",FastMap.newInstance());
        result.put("PM",FastMap.newInstance());
        result.put("TOT",FastMap.newInstance());  
        List<String> allProductNames = FastList.newInstance();
        allProductNames.addAll(productNames);
        allProductNames.add("TOT");
        for (int i = 0; i < allProductNames.size(); ++i) {	
			Map amMap = (Map)result.get("AM");
			amMap.put(allProductNames.get(i), initFieldsMap());
			Map pmMap = (Map)result.get("PM");
			pmMap.put(allProductNames.get(i), initFieldsMap());
			Map totMap = (Map)result.get("TOT");
			totMap.put(allProductNames.get(i), initFieldsMap());			
        }
        return result;   
    }

    private static void populateProductMap(BigDecimal qtyKgs,BigDecimal qtyLtrs, BigDecimal price, BigDecimal kgFat,BigDecimal gKgFat, BigDecimal zeroKgFat, 
    		BigDecimal kgSnf, BigDecimal zeroKgSnf,BigDecimal sQtyLtrs, BigDecimal sFat,BigDecimal sKgFat, BigDecimal sPrice, BigDecimal cQtyLtrs,BigDecimal ptcQtyKgs,BigDecimal totPrem,String ptcMilkType, Map<String, Object> productMap) {
    	BigDecimal totQtyLts = qtyLtrs.add((BigDecimal)productMap.get("qtyLtrs"));
    	BigDecimal totQtyKgs = qtyKgs.add((BigDecimal)productMap.get("qtyKgs"));
    	BigDecimal totPrice = (price.add((BigDecimal)productMap.get("price"))).setScale(2, rounding);
    	BigDecimal totKgFat = kgFat.add((BigDecimal)productMap.get("kgFat")); 
    	BigDecimal totGKgFat= gKgFat.add((BigDecimal)productMap.get("gKgFat"));
    	BigDecimal totzeroKgFat = zeroKgFat.add((BigDecimal)productMap.get("zeroKgFat")); 
    	BigDecimal totzeroKgSnf = zeroKgSnf.add((BigDecimal)productMap.get("zeroKgSnf")); 
    	BigDecimal totKgSnf = kgSnf.add((BigDecimal)productMap.get("kgSnf")); 
    	BigDecimal totFat = ZERO;
    	BigDecimal totSnf = ZERO;
    	if(!(totQtyKgs.equals(ZERO))){
    		totFat = (((totKgFat.divide(totQtyKgs, 5, rounding)).multiply(new BigDecimal(100)))).setScale(5, rounding);
        	totSnf = (((totKgSnf.divide(totQtyKgs, 5, rounding)).multiply(new BigDecimal(100)))).setScale(5, rounding);        
    	}
    	BigDecimal sTotQtyLtrs = sQtyLtrs.add((BigDecimal)productMap.get("sQtyLtrs"));
    	BigDecimal sTotKgFat = sKgFat.add((BigDecimal)productMap.get("sKgFat"));
    	BigDecimal sTotFat = ZERO;
    	if(!(sTotQtyLtrs.equals(ZERO))){
    		BigDecimal sTotQtyKgs = ProcurementNetworkServices.convertLitresToKG(sTotQtyLtrs);
    		if(!(sTotKgFat.equals(ZERO))){
        		sTotFat = ProcurementNetworkServices.calculateFatOrSnf(sTotKgFat, sTotQtyKgs);
        	}
    	}
    	BigDecimal sTotPrice = sPrice.add((BigDecimal)productMap.get("sPrice"));
    	BigDecimal cTotQtyLtrs = cQtyLtrs.add((BigDecimal)productMap.get("cQtyLtrs"));
    	BigDecimal ptcTotQtyKgs = ptcQtyKgs.add((BigDecimal)productMap.get("ptcQtyKgs"));
    	BigDecimal unitPremPrice = ((totPrem).multiply(qtyKgs)).add((BigDecimal)productMap.get("totPrem"));
    	String ptcQtyMilkType = ptcMilkType;
    	productMap.put("qtyLtrs", totQtyLts);
    	productMap.put("qtyKgs", totQtyKgs);
        productMap.put("price", totPrice);  
        productMap.put("kgFat", totKgFat); 
        productMap.put("zeroKgFat", totzeroKgFat);
        productMap.put("zeroKgSnf", totzeroKgSnf);
        productMap.put("kgSnf", totKgSnf);  
        productMap.put("fat", totFat);
        productMap.put("snf", totSnf);
        productMap.put("sQtyLtrs", sTotQtyLtrs);
        productMap.put("sFat", sTotFat);
        productMap.put("sKgFat", sTotKgFat);
        productMap.put("sPrice", sTotPrice);
        productMap.put("cQtyLtrs", cTotQtyLtrs);
        productMap.put("ptcQtyKgs", ptcTotQtyKgs);
        productMap.put("totPrem", unitPremPrice);
        productMap.put("ptcMilkType", ptcQtyMilkType);
        // here we are getting good kgFat
        productMap.put("gKgFat", totGKgFat);
    }
    
    private static void populateDayTotalsMap(GenericValue orderItem, Map<String, Object> dayTotalsMap) {
        String supplyType = orderItem.getString("supplyTypeEnumId"); 
        String productName = orderItem.getString("productName");    
        BigDecimal quantity =BigDecimal.ZERO;
        BigDecimal qtyKgs = BigDecimal.ZERO;
        BigDecimal qtyLtrs = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("quantityKgs"))){
        	 qtyKgs = orderItem.getBigDecimal("quantityKgs").setScale(decimals, rounding);
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("quantityLtrs"))){
        	 qtyLtrs = orderItem.getBigDecimal("quantityLtrs").setScale(decimals, rounding);
        }
        if(qtyKgs.compareTo(BigDecimal.ZERO)==0){
        	qtyKgs = orderItem.getBigDecimal("quantity").setScale(decimals, rounding);
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("quantity"))){
        	quantity = orderItem.getBigDecimal("quantity").setScale(decimals, rounding);
        }
        if(qtyLtrs.compareTo(BigDecimal.ZERO)==0){
        	qtyLtrs = ProcurementNetworkServices.convertKGToLitre(qtyKgs);
        }
        BigDecimal unitPrice = orderItem.getBigDecimal("unitPrice");
        BigDecimal price = (quantity.multiply(unitPrice)).setScale(2, rounding);
        BigDecimal snf = orderItem.getBigDecimal("snf");
        BigDecimal fat = orderItem.getBigDecimal("fat"); 
        BigDecimal kgFat = (qtyKgs.multiply(fat.divide(new BigDecimal(100)))).setScale(decimals, rounding);
        BigDecimal kgSnf = (qtyKgs.multiply(snf.divide(new BigDecimal(100)))).setScale(decimals, rounding);
        BigDecimal gKgFat = kgFat;
        BigDecimal zeroKgFat =BigDecimal.ZERO;
        BigDecimal zeroKgSnf =BigDecimal.ZERO;
        if(price.compareTo(BigDecimal.ZERO)==0){
        	zeroKgFat = kgFat;
        	zeroKgSnf = kgSnf;
        }
        BigDecimal sQtyLtrs = BigDecimal.ZERO;
        BigDecimal sQtyKgs = BigDecimal.ZERO;
        BigDecimal sFat = BigDecimal.ZERO;
        BigDecimal sKgFat =BigDecimal.ZERO;
        BigDecimal sPrice = BigDecimal.ZERO;
        BigDecimal sUnitPrice = BigDecimal.ZERO;
        BigDecimal cQtyLtrs = BigDecimal.ZERO;
        BigDecimal ptcQtyKgs = BigDecimal.ZERO;
        BigDecimal totPrem = BigDecimal.ZERO;
        String ptcMilkType = orderItem.getString("ptcMilkType"); 
        if(orderItem.getBigDecimal("sQuantityLtrs") !=null){
        	sQtyLtrs = orderItem.getBigDecimal("sQuantityLtrs").setScale(decimals, rounding);
        	sQtyKgs = sQtyLtrs.multiply(new BigDecimal(1.03));
        }
        if(orderItem.getBigDecimal("sFat") !=null){
        	sFat = orderItem.getBigDecimal("sFat").setScale(decimals, rounding);
        }
        if(orderItem.getBigDecimal("sUnitPrice") !=null){
        	sKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQtyKgs,sFat);
        	sUnitPrice = orderItem.getBigDecimal("sUnitPrice");
        	sPrice = (sKgFat.multiply(sUnitPrice)).setScale(decimals, rounding);
        	kgFat =kgFat.add(sKgFat);
        }
        if(orderItem.getBigDecimal("cQuantityLtrs") !=null){
        	cQtyLtrs = orderItem.getBigDecimal("cQuantityLtrs").setScale(decimals, rounding);
        }
        if(orderItem.getBigDecimal("ptcQuantity") !=null){
        	ptcQtyKgs = orderItem.getBigDecimal("ptcQuantity").setScale(decimals, rounding);
        }
        if(orderItem.getBigDecimal("unitPremiumPrice") !=null){
        	totPrem = orderItem.getBigDecimal("unitPremiumPrice").setScale(decimals, rounding);
        }
        Timestamp estimatedDeliveryDate = orderItem.getTimestamp("estimatedDeliveryDate");   
        String dateKey = UtilDateTime.toDateString(estimatedDeliveryDate, "yyyy/MM/dd");
        Map dateMap = (Map)dayTotalsMap.get(dateKey);
        Map supplyTypeMap = (Map)dateMap.get(supplyType);
        Map productMap = (Map)supplyTypeMap.get(productName);
        populateProductMap(qtyKgs,qtyLtrs, price, kgFat,gKgFat, zeroKgFat,kgSnf, zeroKgSnf,sQtyLtrs, sFat,sKgFat, sPrice, cQtyLtrs, ptcQtyKgs, totPrem, ptcMilkType, productMap);

        productMap = (Map)supplyTypeMap.get("TOT");  
        populateProductMap(qtyKgs,qtyLtrs, price, kgFat,gKgFat, zeroKgFat,kgSnf, zeroKgSnf,sQtyLtrs, sFat,sKgFat, sPrice, cQtyLtrs, ptcQtyKgs, totPrem, ptcMilkType, productMap);   
        
        supplyTypeMap = (Map)dateMap.get("TOT");
        productMap = (Map)supplyTypeMap.get(productName);  
        populateProductMap(qtyKgs,qtyLtrs, price, kgFat,gKgFat, zeroKgFat,kgSnf, zeroKgSnf,sQtyLtrs, sFat,sKgFat, sPrice, cQtyLtrs, ptcQtyKgs, totPrem, ptcMilkType, productMap);         
        
        productMap = (Map)supplyTypeMap.get("TOT");  
        populateProductMap(qtyKgs,qtyLtrs, price, kgFat,gKgFat, zeroKgFat,kgSnf, zeroKgSnf,sQtyLtrs, sFat,sKgFat, sPrice, cQtyLtrs, ptcQtyKgs, totPrem, ptcMilkType, productMap);        
        
        // update dayTotals TOT
        Map dayTotalsTOTMap = (Map)dayTotalsMap.get("TOT");
        supplyTypeMap = (Map)dayTotalsTOTMap.get(supplyType);
        productMap = (Map)supplyTypeMap.get(productName);  
        populateProductMap(qtyKgs,qtyLtrs, price, kgFat,gKgFat, zeroKgFat,kgSnf, zeroKgSnf,sQtyLtrs, sFat,sKgFat, sPrice, cQtyLtrs, ptcQtyKgs, totPrem, ptcMilkType, productMap);    
        
        supplyTypeMap = (Map)dayTotalsTOTMap.get("TOT");
        productMap = (Map)supplyTypeMap.get(productName);  
        populateProductMap(qtyKgs,qtyLtrs, price, kgFat,gKgFat, zeroKgFat,kgSnf, zeroKgSnf,sQtyLtrs, sFat,sKgFat, sPrice, cQtyLtrs, ptcQtyKgs, totPrem, ptcMilkType, productMap);    
        
        productMap = (Map)supplyTypeMap.get("TOT");  
        populateProductMap(qtyKgs,qtyLtrs, price, kgFat,gKgFat, zeroKgFat,kgSnf, zeroKgSnf,sQtyLtrs, sFat,sKgFat, sPrice, cQtyLtrs, ptcQtyKgs, totPrem, ptcMilkType, productMap); 
    }
    
    /**
     * Get the procurement totals for the requested period. Totals are returned for the requested facility Id
     * as well as the breakup center-wise if the given facility Id is a unit, route, plant, etc  
     * @return totals map for the requested facility Id (including center-wise totals breakup) which look like this:
     * totalsMap : {
     *  facilityId : {
     *  	dayTotals : {date: {AM  :  {BM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								CM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								TOT: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx}},
     *  				  		 PM  : {BM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								CM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								TOT: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx}},
     *  						 TOT : {BM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								CM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								TOT: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx}}},..,
     *  				 TOT:  {AM  :  {BM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								CM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								TOT: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx}},
     *  			  			 PM  : {BM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								CM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								TOT: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx}},
     *  					 	TOT : {BM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								CM : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx},
     *  								TOT: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, price:xx}}}
     *  		}
     *  	}
     *  centerWiseTotals : {
     *  	same as above per center facilityId
     *  } 
     * }
     *  
     */
    public static Map<String, Object> getPeriodTotals(DispatchContext ctx,  Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Timestamp fromDate = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
		if(UtilValidate.isEmpty(fromDate)){	
			Debug.logError("fromDate cannot be empty", module);
			return ServiceUtil.returnError("fromDate cannot be empty");							
		}	    	
    	Timestamp thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
		if(UtilValidate.isEmpty(thruDate)){	
			Debug.logError("thruDate cannot be empty", module);
			return ServiceUtil.returnError("thruDate cannot be empty");							
		}	  
    	String facilityId = (String) context.get("facilityId");
		if(UtilValidate.isEmpty(facilityId)){	
			Debug.logError("facilityId cannot be empty", module);
			return ServiceUtil.returnError("facilityId cannot be empty");							
		}  
        Boolean includeCenterTotals = (Boolean) context.get("includeCenterTotals");
        if (includeCenterTotals == null) {
        	includeCenterTotals = Boolean.FALSE;
        }		
    	List<String> facilityIds= FastList.newInstance();    	
    	List<GenericValue> orderItems= FastList.newInstance();
        Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> centerWiseTotals = FastMap.newInstance();
        if (includeCenterTotals.booleanValue()) {
        	result.put("centerWiseTotals", centerWiseTotals);
        }
        Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(ctx, UtilMisc.toMap("facilityId", facilityId));
        if(UtilValidate.isNotEmpty(facilityAgents)){
        	facilityIds= (List) facilityAgents.get("facilityIds");
        }
        try{
            List<GenericValue> productCatMembers = ProcurementNetworkServices.getProcurementProducts(ctx, context);
            List<String> productNames = EntityUtil.getFieldListFromEntityList(productCatMembers, "productName", false);
//Debug.logInfo("productNames=" + productNames, module);         		
            
            List conditionList= FastList.newInstance(); 
        	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));    		
        	conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));    		
        	conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));    		
			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
			//::TODO:: need to handle non-center facilities as well (route, unit, etc)
			conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));			 
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
Debug.logInfo("condition=" + condition, module);         		
        	orderItems = delegator.findList("OrderHeaderItemProductAndFacility", condition, null, UtilMisc.toList("estimatedDeliveryDate","supplyTypeEnumId"), null, false);
Debug.logInfo("orderItems.size()=" + orderItems.size(), module);  
        	Iterator<GenericValue> itemIter = orderItems.iterator();
        	while(itemIter.hasNext()) {
                GenericValue orderItem = itemIter.next();
                Timestamp estimatedDeliveryDate = orderItem.getTimestamp("estimatedDeliveryDate");
                String facilityKey = facilityId; //orderItem.getString("originFacilityId");
                String dateKey = UtilDateTime.toDateString(estimatedDeliveryDate, "yyyy/MM/dd");
                Map<String, Object> dayTotalsMap;
                if( result.get(facilityKey) == null) {
    				Map<String, Object> dateMap = initDayMap(productNames);
    				dayTotalsMap = new TreeMap<String, Object>();
    				dayTotalsMap.put(dateKey, dateMap);
    				Map<String, Object> dayTotalsTOTMap = initDayMap(productNames);
    				dayTotalsMap.put("TOT", dayTotalsTOTMap);
    				Map<String, Object> newFacilityMap = FastMap.newInstance();
    				newFacilityMap.put("dayTotals", dayTotalsMap);
    				result.put(facilityKey, newFacilityMap);                	
                }
                else {
                    Map facilityMap = (Map)result.get(facilityKey); 
                    dayTotalsMap = (Map)facilityMap.get("dayTotals");
                    if (dayTotalsMap.get(dateKey) == null) {
        				Map<String, Object> dateMap = initDayMap(productNames);
        				dayTotalsMap.put(dateKey, dateMap);                    	
                    }
                }
                populateDayTotalsMap(orderItem, dayTotalsMap);
                if (includeCenterTotals.booleanValue()) {
	                // next populate centerwise totals 
	                facilityKey = orderItem.getString("originFacilityId");
	                Map<String, Object> centerDayTotalsMap;
	                if( centerWiseTotals.get(facilityKey) == null) {
	    				Map<String, Object> dateMap = initDayMap(productNames);
	    				centerDayTotalsMap = new TreeMap<String, Object>();
	    				centerDayTotalsMap.put(dateKey, dateMap);
	    				Map<String, Object> dayTotalsTOTMap = initDayMap(productNames);
	    				centerDayTotalsMap.put("TOT", dayTotalsTOTMap);
	    				Map<String, Object> newFacilityMap = FastMap.newInstance();
	    				newFacilityMap.put("dayTotals", centerDayTotalsMap);
	    				centerWiseTotals.put(facilityKey, newFacilityMap);  
	    				result.put("centerWiseTotals", centerWiseTotals);                	
	                }
	                else {
	                    Map facilityMap = (Map)centerWiseTotals.get(facilityKey); 
	                    centerDayTotalsMap = (Map)facilityMap.get("dayTotals");
	                    if (centerDayTotalsMap.get(dateKey) == null) {
	        				Map<String, Object> dateMap = initDayMap(productNames);
	        				centerDayTotalsMap.put(dateKey, dateMap);                    	
	                    }
	                }
	                populateDayTotalsMap(orderItem, centerDayTotalsMap);   
                }
        	}
    	}
        catch (GenericEntityException e) {
            Debug.logError(e, module);
        }        
        return result;	        
    }
   /**
    * Service for Getting Transfer period Totals and shortages
    * @return periodTransferTotalsMap which look like this this:
    * periodTransferTotalsMap :{	input:{dayTotals :{ date:{qtyKgs,kgFat,kgSnf... }
    * 												TOT:{ qtyKgs,kgFat,kgSnf...} 	 }}
    * 								output{dayTotals :{ date: {qtyKgs,kgFat,kgSnf...}       
    * 												TOT:{qtyKgs,kgFat,kgSnf... }     }}
    * 							
    * 								procurementPeriodTotals{dayTotals:{date:{qtyKgs,kgFat,kgSnf...}
    * 												TOT:{qtyKgs,kgFat,kgSnf...}	     }}	
    * 								shortages{  qtyKgs,kgFat,kgSnf,kgFatAmt,kgSnfAmt... }
    *                          
    *                          		amounts={grossAmt, cartage, addnAmt, commAmt, tipAmt}
    *                          		outputEntries={{outputType , qtyKgs, qtyLts,kgFat,kgSnf},{outputType , qtyKgs,qtyLts, kgFat,kgSnf} }
    *                          }
    * @param ctx
    * @param context
    * 
    */
    public static Map<String, Object> getPeriodTransferTotals(DispatchContext ctx,  Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> result = FastMap.newInstance();
    	Timestamp fromDate = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
    	if(UtilValidate.isEmpty(fromDate)){	
			Debug.logError("fromDate cannot be empty", module);
			return ServiceUtil.returnError("fromDate cannot be empty");							
		}	    	
    	Timestamp thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
		if(UtilValidate.isEmpty(thruDate)){	
			Debug.logError("thruDate cannot be empty", module);
			return ServiceUtil.returnError("thruDate cannot be empty");							
		}	  
    	String facilityId = (String) context.get("facilityId");
		if(UtilValidate.isEmpty(facilityId)){	
			Debug.logError("facilityId cannot be empty", module);
			return ServiceUtil.returnError("facilityId cannot be empty");							
		}
		Map<String, Object> inputTotals = FastMap.newInstance();
		Map<String, Object> outputTotals = FastMap.newInstance();
		Map<String, Object> facilityMap = FastMap.newInstance();
		// condition for getting Input
		List inputConditionList= FastList.newInstance(); 
		inputConditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId));    		
		inputConditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
		inputConditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		inputConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"MXF_RECD"));
		EntityCondition inputCondition = EntityCondition.makeCondition(inputConditionList,EntityOperator.AND);
		// condition for getting output
		List outputConditionList= FastList.newInstance(); 
		outputConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));    		
		outputConditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
		outputConditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		outputConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS ,"MXF_RECD"));
		EntityCondition outputCondition = EntityCondition.makeCondition(outputConditionList,EntityOperator.AND);
		Map<String,Object> procurementPeriodTotals = FastMap.newInstance();
		// getting period Totals excluding transfers
		procurementPeriodTotals = getPeriodTotals(ctx , UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "facilityId", facilityId,"userLogin",userLogin));
		Map<String, Object> amtMap = FastMap.newInstance();
		Map<String,Object> periodDayTotalsMap = FastMap.newInstance();
		Map<String,Object> tempDayTotalsMap = FastMap.newInstance();
		// populating Period Day Totals
		if(!procurementPeriodTotals.isEmpty()){
			tempDayTotalsMap = (Map)((Map)procurementPeriodTotals.get(facilityId)).get("dayTotals");
			for(String key : tempDayTotalsMap.keySet()){
				periodDayTotalsMap.put(key, ((Map)((Map)tempDayTotalsMap.get(key)).get("TOT")).get("TOT"));
			}
		}
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal kgFatPrice = BigDecimal.ZERO;
		BigDecimal kgSnfPrice = BigDecimal.ZERO;
		//getting applied price
		//for calculating gross amount we need commission,cart amount, additional amount
		// gross amount = totalPrice+commission + cart amount +  additional amount
		amtMap.put("grossAmt",  BigDecimal.ZERO);
		amtMap.put("cartage",  BigDecimal.ZERO);
		amtMap.put("addnAmt",  BigDecimal.ZERO);
		amtMap.put("commAmt",  BigDecimal.ZERO);
		amtMap.put("tipAmt", BigDecimal.ZERO);
		amtMap.put("opCost",BigDecimal.ZERO);
		String custTimePeriodId = null;
		BigDecimal kgFatTot = BigDecimal.ZERO;
		BigDecimal kgSnfTot = BigDecimal.ZERO;
		try{
			
			// for calculating tip Amount
			BigDecimal totAmt = BigDecimal.ZERO;
			Map<String, Object> getRateInMap = FastMap.newInstance();
			getRateInMap.put("userLogin", userLogin);
			getRateInMap.put("rateCurrencyUomId", "INR");
			getRateInMap.put("facilityId", facilityId);
			getRateInMap.put("fromDate",fromDate);
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
			List<GenericValue> productsList = ProcurementNetworkServices.getProcurementProducts(ctx,UtilMisc.toMap());
			//here we are calculating tip amt 
			BigDecimal tipAmt = BigDecimal.ZERO;
			Map<String, Object> productTotalsMap = FastMap.newInstance();
			BigDecimal opCost = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(tempDayTotalsMap)){
				productTotalsMap = (((Map)((Map)tempDayTotalsMap.get("TOT")).get("TOT")));
				Map<String, Object> suplyTypeWiseMap = (Map)tempDayTotalsMap.get("TOT");
				getRateInMap.put("rateTypeId", "PROC_OP_COST");
				for(String key : suplyTypeWiseMap.keySet()){
					if(!key.equalsIgnoreCase("TOT")){
						getRateInMap.put("supplyTypeEnumId", key);
						BigDecimal qtyLtrs = BigDecimal.ZERO;
						Map<String, Object> tempMap = (Map)(suplyTypeWiseMap.get(key));
						if(UtilValidate.isNotEmpty(tempMap)){
							for(String productKey : tempMap.keySet()){
								if(!productKey.equalsIgnoreCase("TOT")){
									qtyLtrs = qtyLtrs.add((BigDecimal) ((Map)tempMap.get(productKey)).get("qtyLtrs"));
								}
							}
						}
						getRateInMap.put("slabAmount",facility.getBigDecimal("facilitySize"));
						Map<String, Object> opCostAmtMap = dispatcher.runSync("getProcurementFacilityRateAmount", getRateInMap);
						BigDecimal opCostRate = BigDecimal.ZERO;
						if(ServiceUtil.isSuccess(opCostAmtMap)){
							opCostRate = (BigDecimal)opCostAmtMap.get("rateAmount");
						}
						opCost = opCost.add(opCostRate.multiply(qtyLtrs));
					}	
				}
				amtMap.put("opCost",opCost);
				
				getRateInMap.remove("slabAmount");
				getRateInMap.remove("supplyTypeEnumId");
				
				getRateInMap.put("rateTypeId", "PROC_TIP_AMOUNT");
				for(GenericValue product : productsList){
					getRateInMap.put("productId",product.get("productId"));
					Map<String, Object> defaultPriceMap = PriceServices.getProcurementProductPrice(ctx,UtilMisc.toMap("userLogin",userLogin,"facilityId",facilityId,"productId",product.get("productId"),"fatPercent",BigDecimal.ZERO,"snfPercent",BigDecimal.ZERO));
					Map<String, Object> tipAmtMap = dispatcher.runSync("getProcurementFacilityRateAmount", getRateInMap);
					BigDecimal tipRate = BigDecimal.ZERO;
					if(ServiceUtil.isSuccess(tipAmtMap)){
						tipRate = (BigDecimal)tipAmtMap.get("rateAmount");
					}
					BigDecimal qty = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(defaultPriceMap)){
						String productName = (String)product.get("productName");
						BigDecimal tipKgFat = (BigDecimal)(((Map)productTotalsMap.get(productName)).get("kgFat"));
						BigDecimal tipKgSnf = (BigDecimal)(((Map)productTotalsMap.get(productName)).get("kgSnf"));
						BigDecimal tipZeroKgFat = (BigDecimal)(((Map)productTotalsMap.get(productName)).get("zeroKgFat"));
						BigDecimal tipZeroKgSnf = (BigDecimal)(((Map)productTotalsMap.get(productName)).get("zeroKgSnf"));
						if(((String)defaultPriceMap.get("useTotalSolids")).equals("N"))
						{
							qty = tipKgFat.subtract(tipZeroKgFat);
						}else{
							qty = (tipKgFat.subtract(tipZeroKgFat)).add(tipKgSnf.subtract(tipZeroKgSnf));
						}		
					}
					tipAmt = tipAmt.add(qty.multiply(tipRate));
				}
				amtMap.put("tipAmt",tipAmt);
			}
			//  trying to use the same map and removing the unnecessary keys from Map
			getRateInMap.remove("rateTypeId");
			getRateInMap.remove("rateCurrencyUomId");
			//for getting commission and Cartage we need custom Time period
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(fromDate.getTime())));
			conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(thruDate.getTime())));
			conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"PROC_BILL_MONTH"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List<GenericValue> customTimePeriod = delegator.findList("CustomTimePeriod",condition,null,null, null,false);
			//here we are getting commission and cartage amounts
			if(UtilValidate.isNotEmpty(customTimePeriod)){ 
				custTimePeriodId = (String)(((Map)customTimePeriod.get(0)).get("customTimePeriodId"));
				getRateInMap.put("customTimePeriodId", custTimePeriodId);
				Map<String, Object> commAndCartMap = getProcurementBillingValues(ctx, getRateInMap);
				// In this commandcartMAp we also have total qtyKgs
				if(UtilValidate.isNotEmpty(commAndCartMap.get("FacilityBillingMap"))){
					kgFatTot = (BigDecimal)((Map)((Map)((Map)commAndCartMap.get("FacilityBillingMap")).get(facilityId)).get("tot")).get("kgFat");
					kgSnfTot = (BigDecimal)((Map)((Map)((Map)commAndCartMap.get("FacilityBillingMap")).get(facilityId)).get("tot")).get("kgSnf");
					amtMap.put("commAmt",((Map)((Map)((Map)commAndCartMap.get("FacilityBillingMap")).get(facilityId)).get("tot")).get("commAmt"));
					amtMap.put("cartage",((Map)((Map)((Map)commAndCartMap.get("FacilityBillingMap")).get(facilityId)).get("tot")).get("cartage"));
					totAmt =  ((BigDecimal)((Map)((Map)((Map)commAndCartMap.get("FacilityBillingMap")).get(facilityId)).get("tot")).get("totAmt"));
				}
			 }
			// for getting additon amount
			Map<String, Object> adjustments = ProcurementServices.getPeriodAdjustmentsForAgent(ctx, UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin, "fromDate", fromDate, "thruDate", thruDate));
			if(UtilValidate.isNotEmpty(adjustments)){
				Map<String, Object> adjustmentsTypeValues = FastMap.newInstance();
				adjustmentsTypeValues = (Map) adjustments.get("adjustmentsTypeMap");
				if(UtilValidate.isNotEmpty(adjustmentsTypeValues)){
					Map<String, Object> additionsMap = FastMap.newInstance();
					additionsMap = (Map)adjustmentsTypeValues.get("MILKPROC_ADDITIONS");
					if(UtilValidate.isNotEmpty(additionsMap)){
						for(String key: additionsMap.keySet()){
							amtMap.put("addnAmt", ((BigDecimal)amtMap.get("addnAmt")).add(((BigDecimal)additionsMap.get(key))));
						}
					}
				}
			}
			amtMap.put("grossAmt",totAmt.add((BigDecimal)amtMap.get("addnAmt")).add((BigDecimal)amtMap.get("opCost")).add((BigDecimal)amtMap.get("cartage")));
			/*BigDecimal fatMultiple = (new BigDecimal(55)).divide(new BigDecimal(100));
			if(kgFat.compareTo(BigDecimal.ZERO)!=0){
				kgFatPrice = (((BigDecimal)amtMap.get("grossAmt")).multiply(fatMultiple)).divide(kgFat,4, rounding).setScale(decimals,rounding);
			}
			BigDecimal snfMultiple = (new BigDecimal(45)).divide(new BigDecimal(100));
			if(kgSnf.compareTo(BigDecimal.ZERO)!=0){
				kgSnfPrice = (((BigDecimal)amtMap.get("grossAmt")).multiply(snfMultiple)).divide(kgSnf,4, rounding).setScale(decimals,rounding);
			}*/
		}catch (GenericEntityException e) {
			// TODO: handle exception
				Debug.logError("Error while getting custom Time Period Id: "+e,module);
		}catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError("Error while getting adjustments or commissions : "+e,module);
	}
		
		if(UtilValidate.isNotEmpty(periodDayTotalsMap)){
			BigDecimal totPrice = (BigDecimal)((Map)periodDayTotalsMap.get("TOT")).get("price");
			BigDecimal totKgs = (BigDecimal)((Map)periodDayTotalsMap.get("TOT")).get("qtyKgs");
			BigDecimal totKgFat = (BigDecimal)((Map)periodDayTotalsMap.get("TOT")).get("kgFat");
			BigDecimal totKgSnf = (BigDecimal)((Map)periodDayTotalsMap.get("TOT")).get("kgSnf");
			if((totKgs.compareTo((BigDecimal.ZERO)))!=0)
			{
				price = totPrice.divide(totKgs,4, rounding).setScale(decimals,rounding);
			}
		}
		
		Map<String,Object> inMap = FastMap.newInstance();
		inMap.put("userLogin", userLogin);
		inMap.put("condition", inputCondition);
		inMap.put("price", price);
		inMap.put("facilityId",facilityId);
		inMap.put("conditionType", "in");
		inputTotals = (Map)populateIOTotals(ctx,inMap);
		inMap.put("conditionType", "out");
		inMap.put("condition", outputCondition);
		outputTotals =(Map) populateIOTotals(ctx,inMap);
		// output Entries means  Local sale,mm preparation,tm prep ,Closing Balance.. what ever, it is considered as output
		Map<String, Object> outputEntriesMap = FastMap.newInstance();
		outputEntriesMap.put("kgFat",BigDecimal.ZERO);
		outputEntriesMap.put("kgSnf",BigDecimal.ZERO);
		outputEntriesMap.put("qtyKgs",BigDecimal.ZERO);
		outputEntriesMap.put("sQtyLts",BigDecimal.ZERO);
		outputEntriesMap.put("sKgFat",BigDecimal.ZERO);
		outputEntriesMap.put("sKgSnf",BigDecimal.ZERO);
		List outputEntriesConditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		outputEntriesConditionList.add(EntityCondition.makeCondition("outputTypeId",EntityOperator.NOT_EQUAL,"TM_PREPARATION_OB"));
		outputEntriesConditionList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,custTimePeriodId));
		EntityCondition outputEntriesCondition = EntityCondition.makeCondition(outputEntriesConditionList,EntityOperator.AND);
		List<GenericValue> outputEntriesList = FastList.newInstance();;
		try{
			outputEntriesList = delegator.findList("ProcFacilityOutput",outputEntriesCondition,null,null, null,false);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error while getting other outputs :"+e,module);
		}
		List tempOutputEntriesList =FastList.newInstance();
		for(GenericValue outputEntries : outputEntriesList){
			Map<String, Object> tempOutputEntriesMap = FastMap.newInstance();
			tempOutputEntriesMap.put("outputType",outputEntries.get("outputTypeId"));
			tempOutputEntriesMap.put("qtyKgs",((BigDecimal)outputEntries.get("qty")).setScale(2, rounding));
			tempOutputEntriesMap.put("qtyLts",((BigDecimal)outputEntries.get("quantityLtrs")).setScale(2, rounding));;
			tempOutputEntriesMap.put("kgFat",((BigDecimal)outputEntries.get("kgFat")).setScale(2, rounding));;
			tempOutputEntriesMap.put("kgSnf",((BigDecimal)outputEntries.get("kgSnf")).setScale(2, rounding));;
			tempOutputEntriesList.add(tempOutputEntriesMap);
			if(((String)outputEntries.get("outputTypeId")).equalsIgnoreCase("sour")){
				outputEntriesMap.put("qtyKgs",(((BigDecimal)outputEntriesMap.get("qtyKgs")).add(((BigDecimal)outputEntries.get("qty")))));
				outputEntriesMap.put("sQtyLts",(((BigDecimal)outputEntriesMap.get("sQtyLts")).add(((BigDecimal)outputEntries.get("quantityLtrs")))));
				outputEntriesMap.put("sKgFat",(((BigDecimal)outputEntriesMap.get("sKgFat")).add(((BigDecimal)outputEntries.get("kgFat")))));
				outputEntriesMap.put("sKgSnf",(((BigDecimal)outputEntriesMap.get("sKgSnf")).add(((BigDecimal)outputEntries.get("kgSnf")))));	
			}else{
				outputEntriesMap.put("qtyKgs",((BigDecimal)outputEntriesMap.get("qtyKgs")).add(((BigDecimal)outputEntries.get("qty"))));
				outputEntriesMap.put("kgFat",((BigDecimal)outputEntriesMap.get("kgFat")).add(((BigDecimal)outputEntries.get("kgFat"))));
				outputEntriesMap.put("kgSnf",((BigDecimal)outputEntriesMap.get("kgSnf")).add(((BigDecimal)outputEntries.get("kgSnf"))));
			}
		}
		Map<String, Object> inputEntriesMap = FastMap.newInstance();
		inputEntriesMap.put("kgFat",BigDecimal.ZERO);
		inputEntriesMap.put("kgSnf",BigDecimal.ZERO);
		inputEntriesMap.put("qtyKgs",BigDecimal.ZERO);
		inputEntriesMap.put("sQtyLts",BigDecimal.ZERO);
		inputEntriesMap.put("sKgFat",BigDecimal.ZERO);
		inputEntriesMap.put("sKgSnf",BigDecimal.ZERO);
		
		List inputEntriesConditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		inputEntriesConditionList.add(EntityCondition.makeCondition("outputTypeId",EntityOperator.EQUALS,"TM_PREPARATION_OB"));
		inputEntriesConditionList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,custTimePeriodId));
		EntityCondition inputEntriesCondition = EntityCondition.makeCondition(inputEntriesConditionList,EntityOperator.AND);
		List<GenericValue> inputEntriesList = FastList.newInstance();
		try{
			inputEntriesList = delegator.findList("ProcFacilityOutput",inputEntriesCondition,null,null, null,false);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error while getting other outputs :"+e,module);
		}
		for(GenericValue inputEntries : inputEntriesList){
				inputEntriesMap.put("qtyKgs",((BigDecimal)inputEntriesMap.get("qtyKgs")).add(((BigDecimal)inputEntries.get("qty"))));
				inputEntriesMap.put("kgFat",((BigDecimal)inputEntriesMap.get("kgFat")).add(((BigDecimal)inputEntries.get("kgFat"))));
				inputEntriesMap.put("kgSnf",((BigDecimal)inputEntriesMap.get("kgSnf")).add(((BigDecimal)inputEntries.get("kgSnf"))));
		}
		// here we are getting opening balance for the current period
		Map  openingBalMap = FastMap.newInstance();
		Map openingBalInMap = FastMap.newInstance();
		openingBalInMap.put("customTimePeriodId",custTimePeriodId);
		openingBalInMap.put("periodTypeId","PROC_BILL_MONTH");
		openingBalInMap.put("facilityId",facilityId);
		openingBalInMap.put("userLogin", userLogin);
		openingBalMap  = getOpeningBalance(ctx ,openingBalInMap);
		Debug.logInfo("openingBalMap==============="+openingBalMap,module);
		Map<String, Object> shortages = FastMap.newInstance();
		shortages.put("qtyKgs", BigDecimal.ZERO);
		shortages.put("kgFat", BigDecimal.ZERO);
		shortages.put("kgSnf", BigDecimal.ZERO);
		shortages.put("kgFatAmt", BigDecimal.ZERO);
		shortages.put("kgSnfAmt", BigDecimal.ZERO);
		shortages.put("gheeYield", BigDecimal.ZERO);
		shortages.put("sQtyLtrsTrans", BigDecimal.ZERO);
		
		// Here first add Output Totals and outputEntries   then subtract period totals and Input Totals to get Shortages
		//formula is shortage = (outputTotals+outputEntries)-(inputTransferTotals+periodTotals)
		if(!outputTotals.isEmpty()){
			shortages.put("qtyKgs", ((BigDecimal)((Map)((Map)outputTotals.get("dayTotals")).get("TOT")).get("qtyKgs")) .add(((BigDecimal)shortages.get("qtyKgs"))));
			shortages.put("kgFat",  ((BigDecimal)((Map)((Map)outputTotals.get("dayTotals")).get("TOT")).get("kgFat")) .add(((BigDecimal)shortages.get("kgFat"))));
			shortages.put("kgSnf",  ((BigDecimal)((Map)((Map)outputTotals.get("dayTotals")).get("TOT")).get("kgSnf")) .add(((BigDecimal)shortages.get("kgSnf"))));
		}
		if(!outputEntriesMap.isEmpty()){
			shortages.put("qtyKgs", ((BigDecimal)outputEntriesMap.get("qtyKgs")) .add(((BigDecimal)shortages.get("qtyKgs"))));
			shortages.put("kgFat",  ((BigDecimal)outputEntriesMap.get("kgFat")) .add(((BigDecimal)shortages.get("kgFat"))));
			shortages.put("kgSnf",  ((BigDecimal)outputEntriesMap.get("kgSnf")) .add(((BigDecimal)shortages.get("kgSnf"))));
		}
		
		if(UtilValidate.isNotEmpty(openingBalMap) && UtilValidate.isNotEmpty(openingBalMap.get("openingBalance"))){
			Map openingBalancce = (Map)openingBalMap.get("openingBalance");
			shortages.put("qtyKgs", (((BigDecimal)shortages.get("qtyKgs"))) .subtract((BigDecimal)openingBalancce.get("qtyKgs")));
			shortages.put("kgFat", (((BigDecimal)shortages.get("kgFat"))) .subtract((BigDecimal)openingBalancce.get("kgFat")));
			shortages.put("kgSnf", (((BigDecimal)shortages.get("kgSnf"))) .subtract((BigDecimal)openingBalancce.get("kgSnf")));
		}
		if(UtilValidate.isNotEmpty(inputEntriesMap)){
			shortages.put("qtyKgs", (((BigDecimal)shortages.get("qtyKgs"))) .subtract((BigDecimal)inputEntriesMap.get("qtyKgs")));
			shortages.put("kgFat", (((BigDecimal)shortages.get("kgFat"))) .subtract((BigDecimal)inputEntriesMap.get("kgFat")));
			shortages.put("kgSnf", (((BigDecimal)shortages.get("kgSnf"))) .subtract((BigDecimal)inputEntriesMap.get("kgSnf")));
		}
		if(!periodDayTotalsMap.isEmpty()){
			shortages.put("qtyKgs",((BigDecimal)shortages.get("qtyKgs")).subtract((BigDecimal)((Map)periodDayTotalsMap.get("TOT")).get("qtyKgs")));
			shortages.put("kgFat",((BigDecimal)shortages.get("kgFat")).subtract((BigDecimal)((Map)periodDayTotalsMap.get("TOT")).get("kgFat")));
			shortages.put("kgSnf",((BigDecimal)shortages.get("kgSnf")).subtract((BigDecimal)((Map)periodDayTotalsMap.get("TOT")).get("kgSnf")));
		}
		if(!inputTotals.isEmpty()){
			Map<String, Object> totMap = (Map)(((Map)inputTotals.get("dayTotals")).get("TOT"));
			shortages.put("qtyKgs", ((BigDecimal)shortages.get("qtyKgs")).subtract(((BigDecimal)totMap.get("qtyKgs"))));
			shortages.put("kgFat",  ((BigDecimal)shortages.get("kgFat")).subtract(((BigDecimal)totMap.get("kgFat"))));
			shortages.put("kgSnf",  ((BigDecimal)shortages.get("kgSnf")).subtract(((BigDecimal)totMap.get("kgSnf"))));
			if(UtilValidate.isNotEmpty(totMap.get("gheeYield"))){
				shortages.put("gheeYield",((BigDecimal)shortages.get("gheeYield")).add((BigDecimal)totMap.get("gheeYield")));
			}
			if(UtilValidate.isNotEmpty(totMap.get("sQtyLtrsTrans"))){
				shortages.put("sQtyLtrsTrans",((BigDecimal)shortages.get("sQtyLtrsTrans")).add((BigDecimal)totMap.get("sQtyLtrsTrans")));
			}
		}
		
		shortages.put("qtyLts",ProcurementNetworkServices.convertKGToLitre(((BigDecimal)shortages.get("qtyKgs"))));
		if((((BigDecimal)shortages.get("kgFat")).compareTo(BigDecimal.ZERO))<0){
			BigDecimal shrtKgFatAmt = BigDecimal.ZERO;
			// simplified formula for calculating shrtkgFat amt =(grossAmt*55*shortKgFat)/(100*kgFat)
			if(kgFatTot.compareTo(BigDecimal.ZERO)!=0){	
				BigDecimal amtFatMultiple= ((((BigDecimal)amtMap.get("grossAmt")).multiply(new BigDecimal(55))).divide(new BigDecimal(100))).setScale(decimals, rounding);
				BigDecimal avgKgFatRate = (amtFatMultiple.divide(kgFatTot ,3 , RoundingMode.HALF_UP));
				shrtKgFatAmt = (avgKgFatRate.multiply((BigDecimal)shortages.get("kgFat"))).setScale(decimals,rounding) ;
			}
			shortages.put("kgFatAmt", shrtKgFatAmt);
			//shortages.put("kgFatAmt", ((BigDecimal)shortages.get("kgFat")).multiply(kgFatPrice).setScale(decimals, rounding));
			
		}
		
		Debug.logInfo("grossAmount========>"+amtMap.get("grossAmt"),module);
		Debug.logInfo("Total KgFat========>"+kgFatTot,module);
		Debug.logInfo("Total KgSnf========>"+kgSnfTot,module);
		Debug.logInfo("shortages=====> kgFat :"+shortages.get("kgFat")+" :kgSnf=====>"+shortages.get("kgSnf"),module);
		if((((BigDecimal)shortages.get("kgSnf")).compareTo(BigDecimal.ZERO))<0){
			BigDecimal shrtKgSnfAmt = BigDecimal.ZERO;
			// simplified formula for calculating shrtkgSnf amt =(grossAmt*45*shortKgSnf)/(100*kgSnf)
			if(kgSnfTot.compareTo(BigDecimal.ZERO)!=0){	
				BigDecimal amtSnfMultiple= ((((BigDecimal)amtMap.get("grossAmt")).multiply(new BigDecimal(45))).divide(new BigDecimal(100))).setScale(decimals, rounding);
				BigDecimal avgKgSnfRate = (amtSnfMultiple.divide(kgSnfTot ,3 , RoundingMode.HALF_UP));
				shrtKgSnfAmt = (avgKgSnfRate.multiply((BigDecimal)shortages.get("kgSnf"))).setScale(decimals,rounding) ;
			}
			shortages.put("kgSnfAmt", shrtKgSnfAmt);
			//shortages.put("kgSnfAmt", ((BigDecimal)shortages.get("kgSnf")).multiply(kgSnfPrice).setScale(decimals, rounding));
		}
		Debug.logInfo("kgFatAmt========>"+shortages.get("kgFatAmt"),module);
		Debug.logInfo("kgSnfAmt========>"+shortages.get("kgSnfAmt"),module);
		//populating 
		inputEntriesMap.put("qtyLtrs", ProcurementNetworkServices.convertKGToLitre((BigDecimal)inputEntriesMap.get("qtyKgs")));
		
		
		//This is to put periodDayTotalsMap into dayTotals map
		Map<String, Object> finalPeriodTotalsMap = FastMap.newInstance();
		finalPeriodTotalsMap.put("dayTotals", periodDayTotalsMap);
		Map<String,Object> tempTransfersMap = FastMap.newInstance();
		tempTransfersMap.put("input",inputTotals);
		tempTransfersMap.put("output",outputTotals);
		tempTransfersMap.put("procurementPeriodTotals",finalPeriodTotalsMap);
		tempTransfersMap.put("shortages", shortages);
		tempTransfersMap.put("amounts", amtMap);
		tempTransfersMap.put("outputEntries", tempOutputEntriesList);
		tempTransfersMap.put("tmPreparationOB", inputEntriesMap);
		facilityMap.put("transfers", tempTransfersMap);
		Map<String, Object> periodTransferTotalsMap = FastMap.newInstance();
		periodTransferTotalsMap.put(facilityId, facilityMap);
		result.put("periodTransferTotalsMap", periodTransferTotalsMap);
		return result;    
    }     
    public static Map<String, Object> getProcurementBillingValues(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String facilityId = (String) context.get("facilityId");
        String customTimePeriodId = (String) context.get("customTimePeriodId");		
        List<GenericValue> periodBillingList =FastList.newInstance();      
        List<GenericValue> facilityCommisionList = FastList.newInstance();
       
        try{
        	List<String> billingFacilityIds= FastList.newInstance();
        	GenericValue shedDetails = (GenericValue)(ProcurementNetworkServices.getShedDetailsForFacility(dctx , UtilMisc.toMap("facilityId",facilityId))).get("facility");
        	Map facilityAgents = ProcurementNetworkServices.getFacilityChildernTree(dctx, UtilMisc.toMap("facilityId", shedDetails.getString("facilityId")));
            if(UtilValidate.isNotEmpty(facilityAgents)){
            	billingFacilityIds= (List) facilityAgents.get("facilityIds");
            }       	
        	List condList = UtilMisc.toList(EntityCondition.makeCondition("facilityId",EntityOperator.IN,billingFacilityIds));
        	condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,customTimePeriodId));
        	condList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"GENERATED"));
        	periodBillingList= delegator.findList("PeriodBilling", EntityCondition.makeCondition(condList,EntityOperator.AND),null,null,null,false);       	
        }catch(GenericEntityException ge){
        	ge.printStackTrace();
        }
        GenericValue billingId =null;
        List periodBillingIdList = null;
        if(!UtilValidate.isEmpty(periodBillingList)){
        	periodBillingIdList =  EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);				    			
		}else{
			return ServiceUtil.returnError("No Period Billing Ids for the given periodId==>"+customTimePeriodId);
		}  
        Map resultMap = ServiceUtil.returnSuccess();
        List<String> facilityIds= FastList.newInstance();
        Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facilityId));
        if(UtilValidate.isNotEmpty(facilityAgents)){
        	facilityIds= (List) facilityAgents.get("facilityIds");
        }  
        	
		 try{    	   
			 List conditionList =FastList.newInstance();	       
			 conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN, periodBillingIdList));	      
	       if(!UtilValidate.isEmpty(facilityId)){
	    	   conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds));
	       }	   
		   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   facilityCommisionList =delegator.findList("FacilityCommissionProc", orderCondition, null, null, null,false);	  		   
		 }catch (GenericEntityException e) {
			// TODO: handle exception
	 		 Debug.logError(e.getMessage(), module);
	 		 return ServiceUtil.returnError(e.getMessage());
		}
    	 
    	 if(UtilValidate.isEmpty(facilityCommisionList)){
    		  return ServiceUtil.returnSuccess("no values to process for the time period");
    	 }   	
    	 Map FacilityBillingMap = FastMap.newInstance();   
    	 Map productTypeMap = FastMap.newInstance();    	 
    	 Map tempMap = FastMap.newInstance();
    	 tempMap.put("totQty", BigDecimal.ZERO);
         tempMap.put("kgFat", BigDecimal.ZERO);
         tempMap.put("kgSnf", BigDecimal.ZERO);
         tempMap.put("totAmt", BigDecimal.ZERO);
         tempMap.put("commAmt", BigDecimal.ZERO);
         tempMap.put("cartage", BigDecimal.ZERO);
         productTypeMap.put("tot", tempMap);
    	 for(GenericValue facilityDetails : facilityCommisionList){	           
	           String productId = facilityDetails.getString("productId");
	           Map tempTotals = FastMap.newInstance();
	           tempTotals.putAll((Map)productTypeMap.get("tot"));
	           if(UtilValidate.isEmpty(productTypeMap.get(productId))){
	        	   productTypeMap.put(productId, tempMap);	        	   
	           }
	           Map tempProductWiseMap =FastMap.newInstance();
	          tempProductWiseMap.putAll((Map)productTypeMap.get(productId));	          
	          tempProductWiseMap.put("totQty", ((BigDecimal)tempProductWiseMap.get("totQty")).add((BigDecimal)facilityDetails.get("totalQty")));	         
	          tempProductWiseMap.put("kgFat", ((BigDecimal)tempProductWiseMap.get("kgFat")).add((BigDecimal)facilityDetails.get("kgFat")));
	          tempProductWiseMap.put("kgSnf", ((BigDecimal)tempProductWiseMap.get("kgSnf")).add((BigDecimal)facilityDetails.get("kgSnf")));
	          tempProductWiseMap.put("totAmt", ((BigDecimal)tempProductWiseMap.get("totAmt")).add((BigDecimal)facilityDetails.get("totalAmount")));
	          tempProductWiseMap.put("commAmt", ((BigDecimal)tempProductWiseMap.get("commAmt")).add((BigDecimal)facilityDetails.get("commissionAmount")));
	          tempProductWiseMap.put("cartage", ((BigDecimal)tempProductWiseMap.get("cartage")).add((BigDecimal)facilityDetails.get("cartage")));
	          
	          //let update totals map
	          tempTotals.put("totQty", ((BigDecimal)tempTotals.get("totQty")).add((BigDecimal)facilityDetails.get("totalQty")));
	          tempTotals.put("kgFat", ((BigDecimal)tempTotals.get("kgFat")).add((BigDecimal)facilityDetails.get("kgFat")));
	          tempTotals.put("kgSnf", ((BigDecimal)tempTotals.get("kgSnf")).add((BigDecimal)facilityDetails.get("kgSnf")));
	          tempTotals.put("totAmt", ((BigDecimal)tempTotals.get("totAmt")).add((BigDecimal)facilityDetails.get("totalAmount")));
	          tempTotals.put("commAmt", ((BigDecimal)tempTotals.get("commAmt")).add((BigDecimal)facilityDetails.get("commissionAmount")));
	          tempTotals.put("cartage", ((BigDecimal)tempTotals.get("cartage")).add((BigDecimal)facilityDetails.get("cartage")));
	          productTypeMap.put(productId,tempProductWiseMap);         
	          productTypeMap.put("tot", tempTotals);	           	          
		   }   	
    	 FacilityBillingMap.put(facilityId, productTypeMap);    	 
    	 resultMap.put("FacilityBillingMap",FacilityBillingMap);
        return resultMap;
	}
   /**
    * This populates Input or Output Totals based on the Entity Condition 
    * @param 
    * @return
    */
    private static Map<String, Object> populateIOTotals(DispatchContext ctx,Map<String,Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	EntityCondition condition = (EntityCondition)context.get("condition"); 
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	BigDecimal priceCal =(BigDecimal) context.get("price");
    	String facilityId = (String)context.get("facilityId");
    	Map<String, Object> result = FastMap.newInstance();
    	List<GenericValue> transfers = null;
    	Map<String , Object> mpfRecieptsMap = FastMap.newInstance();
    	try{
    		transfers = delegator.findList("MilkTransfer", condition, null,null,null,false);
    	}catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError("Error while getting Transfers "+e.getMessage(),module);
    		return ServiceUtil.returnError("Error while getting Transfers");
    	}
    	if(!transfers.isEmpty()){
    		Map<String,Object> totalsMap = FastMap.newInstance();
    		totalsMap.put("qtyKgs", BigDecimal.ZERO);
    		totalsMap.put("qtyLts", BigDecimal.ZERO);
			totalsMap.put("snf",BigDecimal.ZERO);
			totalsMap.put("fat", BigDecimal.ZERO );
			totalsMap.put("kgFat", BigDecimal.ZERO);
			totalsMap.put("kgSnf", BigDecimal.ZERO );
			totalsMap.put("sKgFat",BigDecimal.ZERO );
			totalsMap.put("sKgSnf", BigDecimal.ZERO );
			totalsMap.put("sQuantityLtrs",BigDecimal.ZERO) ;
			totalsMap.put("cQuantityLtrs", BigDecimal.ZERO);
			totalsMap.put("price", BigDecimal.ZERO);
			totalsMap.put("sPrice", BigDecimal.ZERO);
			
			mpfRecieptsMap.putAll(totalsMap);
			mpfRecieptsMap.put("qtyLts",BigDecimal.ZERO);
			mpfRecieptsMap.put("sQtyLtrsTrans",BigDecimal.ZERO);
			mpfRecieptsMap.remove("sQuantityLtrs");
			mpfRecieptsMap.remove("price");
			mpfRecieptsMap.remove("sPrice");
			
			if(((String)context.get("conditionType")).equals("in")){
				totalsMap.put("gheeYield",BigDecimal.ZERO);
				totalsMap.put("sQtyLtrsTrans",BigDecimal.ZERO);
			}
			Map<String,Object> dayMap = FastMap.newInstance();
			String dateType = null;
			if(((String)context.get("conditionType")).equalsIgnoreCase("in")){
				dateType = "receiveDate";
			}else{
				dateType = "sendDate";
			}
			for(GenericValue transfer : transfers){
				Map<String, Object> dayWiseMap = FastMap.newInstance();
				BigDecimal fat = transfer.getBigDecimal("receivedFat");
				BigDecimal snf = transfer.getBigDecimal("receivedSnf");
				Map<String, Object> dayTotalsMap = FastMap.newInstance();
				String dateKey = UtilDateTime.toDateString(transfer.getTimestamp(dateType), "yyyy/MM/dd");
				BigDecimal qtyKgs = transfer.getBigDecimal("receivedQuantity");
				BigDecimal qtyLts = ProcurementNetworkServices.convertKGToLitre(qtyKgs);
				BigDecimal recLr = transfer.getBigDecimal("receivedLR");
				BigDecimal gheeYield = BigDecimal.ZERO;
				BigDecimal sQtyLtrsTrans = BigDecimal.ZERO;
				String facilityIdTo = (String)transfer.get("facilityIdTo");
				if((facilityIdTo.equalsIgnoreCase("MAIN_PLANT"))&&UtilValidate.isNotEmpty(recLr)&& recLr.compareTo(BigDecimal.ZERO)!=0  ){
					// liters formula for apdairy mpf Reciepts  kgs/(1+Lr/1000)
					qtyLts = (qtyKgs.multiply(new BigDecimal(1000))).divide(((new BigDecimal(1000)).add(recLr)),2,rounding);
				
					BigDecimal decimalValue = BigDecimal.ZERO;
			    	 decimalValue = qtyLts.subtract(new BigDecimal((qtyLts.intValue())));
			    	 /* decimal value calculation
			    	  * 	decimal value between 0.25 and 0.75 = 0.5
			    	  * 	greater than or equal to 0.75 = 1.0
			    	  * 	less than 0.25 = 0
			    	  */
			    	 if((decimalValue.compareTo(new BigDecimal(0.75))<0)&&(decimalValue.compareTo(new BigDecimal(0.25))>=0)){
			    		 decimalValue = new BigDecimal(0.5);
			    	 }else if(decimalValue.compareTo(new BigDecimal(0.75))>=0){
			    		 decimalValue = new BigDecimal(1.0);
			         }else{
			    		 decimalValue = BigDecimal.ZERO;
			         }         
			    	 BigDecimal intQtyLts = new BigDecimal(qtyLts.intValue());
			    	 qtyLts = intQtyLts.add(decimalValue);
				}
				
				
				if(UtilValidate.isNotEmpty(transfer.get("gheeYield"))){
					gheeYield = (BigDecimal)transfer.get("gheeYield");
				}
				if(UtilValidate.isNotEmpty(transfer.get("sQuantityLtrs"))){
					sQtyLtrsTrans = (BigDecimal)transfer.get("sQuantityLtrs");
				}
				dayWiseMap.put("qtyKgs", qtyKgs);
				dayWiseMap.put("qtyLts",qtyLts);
				dayWiseMap.put("cQuantityLtrs" ,BigDecimal.ZERO);
				dayWiseMap.put("sQuantityLtrs", BigDecimal.ZERO);	
				dayWiseMap.put("sKgFat", BigDecimal.ZERO);
				dayWiseMap.put("sKgSnf",  BigDecimal.ZERO);
				dayWiseMap.put("sPrice",BigDecimal.ZERO);
				dayWiseMap.put("price", BigDecimal.ZERO);
				dayWiseMap.put("kgFat", BigDecimal.ZERO);
				dayWiseMap.put("kgSnf",BigDecimal.ZERO);
				if(((String)context.get("conditionType")).equals("in")){
					dayWiseMap.put("gheeYield",gheeYield);
					dayWiseMap.put("sQtyLtrsTrans",sQtyLtrsTrans);
					if(UtilValidate.isNotEmpty(gheeYield)){
					  totalsMap.put("gheeYield",((BigDecimal)totalsMap.get("gheeYield")).add(gheeYield));
					}
					if(UtilValidate.isNotEmpty(sQtyLtrsTrans)){
					   totalsMap.put("sQtyLtrsTrans",((BigDecimal)totalsMap.get("sQtyLtrsTrans")).add(sQtyLtrsTrans));
					}
				}
				totalsMap.put("qtyKgs",((BigDecimal)totalsMap.get("qtyKgs")).add(qtyKgs));
				totalsMap.put("qtyLts",((BigDecimal)totalsMap.get("qtyLts")).add(qtyLts));
				String milkType = transfer.getString("milkType");
				BigDecimal tempKgFat = BigDecimal.ZERO;
				BigDecimal tempKgSnf = BigDecimal.ZERO;
				tempKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,fat);
				tempKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,snf);
				if(!(UtilValidate.isEmpty(milkType))){
					if(milkType.equals("C")){
						dayWiseMap.put("cQuantityLtrs",qtyLts);
						totalsMap.put("cQuantityLtrs",((BigDecimal)totalsMap.get("cQuantityLtrs")).add((BigDecimal)dayWiseMap.get((BigDecimal)dayWiseMap.get("cQuantityLtrs")))); 
					}else if (milkType.equals("S")) {
						dayWiseMap.put("sQuantityLtrs",qtyLts);
				    	dayWiseMap.put("sKgFat", tempKgFat);
				    	dayWiseMap.put("sKgSnf",tempKgSnf);
					    totalsMap.put("sKgFat",((BigDecimal)totalsMap.get("sKgFat")).add(tempKgFat));
					    totalsMap.put("sKgSnf",((BigDecimal)totalsMap.get("sKgSnf")).add(tempKgSnf));
					    totalsMap.put("sQuantityLtrs",((BigDecimal)totalsMap.get("sQuantityLtrs")).add((BigDecimal)dayWiseMap.get("sQuantityLtrs")));
					}
				}else{
						dayWiseMap.put("kgFat",tempKgFat);
						dayWiseMap.put("kgSnf",tempKgSnf);
						totalsMap.put("kgFat", ((BigDecimal)totalsMap.get("kgFat")).add(tempKgFat));
						totalsMap.put("kgSnf",((BigDecimal)totalsMap.get("kgSnf")).add(tempKgSnf));
				}
				// checking the key if already exist or not
				if(!(UtilValidate.isEmpty(dayMap.get(dateKey)))){
					Map<String,Object> temp =(Map<String, Object>) dayMap.get(dateKey);
					for(String key : temp.keySet() ){
						if(!(UtilValidate.isEmpty(temp.get(key)))){
								dayWiseMap.put(key, ((BigDecimal)dayWiseMap.get(key)).add((BigDecimal)temp.get(key)));
						}
					}
				}
				BigDecimal sQtyKgs = ((BigDecimal)dayWiseMap.get("sQuantityLtrs")).multiply(new BigDecimal("1.03"));
				dayWiseMap.put("sPrice", ((sQtyKgs).multiply(priceCal).setScale(decimals, rounding)));
				dayWiseMap.put("price", (qtyKgs).multiply(priceCal).setScale(decimals, rounding));
				dayMap.put(dateKey, dayWiseMap);
				if(((String)context.get("conditionType")).equalsIgnoreCase("out")){
					if(facilityIdTo.equalsIgnoreCase("MAIN_PLANT")){
						for(String key : mpfRecieptsMap.keySet()){
							if(UtilValidate.isNotEmpty(dayWiseMap.get(key))){
								mpfRecieptsMap.put(key,((BigDecimal) mpfRecieptsMap.get(key)).add((BigDecimal)dayWiseMap.get(key)));
							}
						}
						BigDecimal mpfQtyKgs = (BigDecimal) mpfRecieptsMap.get("qtyKgs");
						BigDecimal mpfKgFat = (BigDecimal)mpfRecieptsMap.get("kgFat");
						BigDecimal mpfKgSnf = (BigDecimal)mpfRecieptsMap.get("kgSnf");
						if(((BigDecimal)mpfRecieptsMap.get("qtyKgs")).compareTo(BigDecimal.ZERO)>0){
							mpfRecieptsMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(mpfKgFat, mpfQtyKgs));
						}
						if(((BigDecimal)mpfRecieptsMap.get("qtyKgs")).compareTo(BigDecimal.ZERO)>0){
							mpfRecieptsMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(mpfKgSnf, mpfQtyKgs));
						}
					}
				}
				
				
			}
			if((((BigDecimal)totalsMap.get("qtyKgs")).compareTo((BigDecimal.ZERO)))!=0){
				totalsMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf((BigDecimal)totalsMap.get("kgFat"), (BigDecimal)totalsMap.get("qtyKgs")));
				totalsMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf((BigDecimal)totalsMap.get("kgSnf"), (BigDecimal)totalsMap.get("qtyKgs")));
			}
			if(!(UtilValidate.isEmpty(totalsMap.get("qtyKgs")))){
				totalsMap.put("price", ((BigDecimal)totalsMap.get("qtyKgs")).multiply(priceCal).setScale(decimals, rounding) );
			}
			if(!(UtilValidate.isEmpty(totalsMap.get("sQuantityLtrs")))){
				BigDecimal sQtyKgs = ((BigDecimal)totalsMap.get("sQuantityLtrs")).multiply(new BigDecimal("1.03"));
				totalsMap.put("sPrice", ((sQtyKgs).multiply(priceCal).setScale(decimals, rounding)));
			}
			// populating totals into result Map
			dayMap.put("TOT", totalsMap);
			if(((String)context.get("conditionType")).equalsIgnoreCase("out")){
				result.put("mpfReciepts", mpfRecieptsMap);
			}
			result.put("dayTotals",dayMap);
		}
    	return result;
    }
    /**
     * returns Opening Balance map for the given faciliyId ,customTimeperiod,periodTypeId
     */
    public static Map<String, ? extends Object> getOpeningBalance(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();   	 	
    	Delegator delegator = dctx.getDelegator();
	    Map<String, Object> result = FastMap.newInstance();
	    String facilityId = (String)context.get("facilityId");
	    String timePeriodId = (String) context.get("customTimePeriodId");
	    String periodTypeId = (String) context.get("periodTypeId");	
	    GenericValue facility = null;
	    GenericValue customTimePeriod = null;
	    Timestamp fromDate = UtilDateTime.nowTimestamp();
	    try{
	    	facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
	    	if(UtilValidate.isEmpty(facility)){
	    		result = ServiceUtil.returnError(" facility not found with the facility Id ========> "+facilityId);
				return result;
	    	}
	    	customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", timePeriodId), false);
	    }catch (GenericEntityException e) {
			Debug.logError("Error while getting OpeningBalance for : ========>"+e,module); 
			result = ServiceUtil.returnError(" Error while getting OpeningBalance for facilityId: "+facilityId+"  For Period :"+timePeriodId);
			return result;
	    }
	    if(UtilValidate.isEmpty(customTimePeriod)){
		  	 result = ServiceUtil.returnError("Time Period Not existed for given customTimePeriodId=====>"+timePeriodId);
		   }else{
	    List conditionList = FastList.newInstance();
	    String inputDateStr = customTimePeriod.get("fromDate").toString();
	    Timestamp sqlTimestamp = null;
	    java.sql.Date previousDate = null;
	  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	  	try {
	  		sqlTimestamp = new java.sql.Timestamp(sdf.parse(inputDateStr).getTime());
	  		} catch (ParseException e) {
			  Debug.logError("Can not parse the Date String===>"+inputDateStr,module);
	  		}
		previousDate = new java.sql.Date((UtilDateTime.addDaysToTimestamp(sqlTimestamp, -1)).getTime());
	    //Debug.log("previous Time Period Thru Date===========>"+ previousDate);
	    conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,previousDate)));
	    conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,periodTypeId)));
	    EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    List<GenericValue> prevPeriodIdsList = null;
	    try{
	    	Set<String> fields = UtilMisc.toSet("customTimePeriodId");
	    	prevPeriodIdsList = delegator.findList("CustomTimePeriod", condition ,fields, null, null, false);
	      }catch (GenericEntityException e) {
	    	Debug.logError("Error while getting Previous Periods List===>"+e,module);  
		  }
	    if(UtilValidate.isNotEmpty(prevPeriodIdsList)){
	    	  String preCustomTimePeriodId = (String)(prevPeriodIdsList.get(0).get("customTimePeriodId"));
	    	  Debug.logInfo("previous Time Period Id ==============> "+preCustomTimePeriodId,module);
	    	  try{
	    		  GenericValue preClosingBalanceRec = null; 
	    		  preClosingBalanceRec = delegator.findOne("ProcFacilityOutput",UtilMisc.toMap("customTimePeriodId",preCustomTimePeriodId ,"outputTypeId","CLOSING_BALANCE","facilityId",facilityId ) , false);
	    		  Map<String, Object> openingBalMap = FastMap.newInstance();
	    		  if(UtilValidate.isEmpty(preClosingBalanceRec)){
	    			  openingBalMap.put("kgFat",BigDecimal.ZERO);
	    			  openingBalMap.put("kgSnf",BigDecimal.ZERO);
	    			  openingBalMap.put("qtyKgs",BigDecimal.ZERO);
	    			  openingBalMap.put("qtyLtrs",BigDecimal.ZERO);
	    			  openingBalMap.put("fat",BigDecimal.ZERO);
	    			  openingBalMap.put("snf",BigDecimal.ZERO);
	    		  }else{
	    			  openingBalMap.put("qtyKgs",preClosingBalanceRec.get("qty"));
	    			  openingBalMap.put("qtyLtrs",preClosingBalanceRec.get("quantityLtrs"));
	    			  openingBalMap.put("kgFat",preClosingBalanceRec.get("kgFat"));
	    			  openingBalMap.put("kgSnf",preClosingBalanceRec.get("kgSnf"));
	    		      openingBalMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf((BigDecimal)preClosingBalanceRec.get("kgFat"),(BigDecimal)preClosingBalanceRec.get("qty")));
	    		      openingBalMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf((BigDecimal)preClosingBalanceRec.get("kgSnf"),(BigDecimal)preClosingBalanceRec.get("qty")));
	    		  }
	    		  result.put("openingBalance",openingBalMap);
	    	  }catch (GenericEntityException e) {
				// TODO: handle exception
	    		Debug.logError("Error while getting Previous Period Details ========>"+e, module);
			  }
	      }
	    }
	  return result;
   }
   public static Map<String, Object> getShedDDAccount(DispatchContext dctx, Map<String, ? extends Object> context){
	   Map resultMap = FastMap.newInstance();
	   LocalDispatcher dispatcher = dctx.getDispatcher();   	 	
	   Delegator delegator = dctx.getDelegator();
	   String facilityId = (String) context.get("facilityId");
	   String customTimePeriodId = (String) context.get("customTimePeriodId");
	   List<GenericValue> ddAccFacilityPartyList = FastList.newInstance();
	   Map ddAccDetailsMap = FastMap.newInstance();
	   
	   ddAccDetailsMap.put("nameOfTheBank"," ");
	   ddAccDetailsMap.put("nameOfTheBrch"," ");
	   ddAccDetailsMap.put("ifscCode"," ");
	   ddAccDetailsMap.put("bankAccNo"," ");
	   ddAccDetailsMap.put("accountHolder", " ");
	   ddAccDetailsMap.put("partyId", " ");
	   ddAccDetailsMap.put("nameOfUnit","DD OFFICE ACCOUNT");
	   try{
		   GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		   String inputDateStr = customTimePeriod.get("fromDate").toString();
		   Timestamp fromDate;
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		   try{
		   fromDate= new java.sql.Timestamp(sdf.parse(inputDateStr).getTime());
		   }catch (ParseException e) {
				// TODO: handle exception
				Debug.logError("parsing Exception======>"+e,module);
		    	return ServiceUtil.returnError("parsing Exception ======>"+e);
			}
		   
		   List conditionList = FastList.newInstance();
		   conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
		   conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"DD_ROLE"));
		   //conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
		   EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		   ddAccFacilityPartyList = delegator.findList("FacilityParty",condition,null,null,null,false);
		   if(UtilValidate.isEmpty(ddAccFacilityPartyList)){
			   Debug.logError("DD Account Not yet Configured",module);
			   return ServiceUtil.returnError("DD Account Not yet Configured");
		   }
		   List<GenericValue>	activeDDsList = EntityUtil.filterByDate(ddAccFacilityPartyList,fromDate);
		   if(UtilValidate.isNotEmpty(activeDDsList)){
			   GenericValue activeDD = EntityUtil.getFirst(activeDDsList);
			   String partyId = (String)activeDD.get("partyId");
			   // getting fin Account details
			   List finAccConditionList = FastList.newInstance();
			   finAccConditionList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyId));
			   finAccConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"FNACT_ACTIVE"));
			   EntityCondition finAccCondition  = EntityCondition.makeCondition(finAccConditionList,EntityOperator.AND);
			   List<GenericValue> ddFinAccList = delegator.findList("FinAccount",finAccCondition,null,null,null,false);
			   if(UtilValidate.isEmpty(ddFinAccList)){
				   Debug.logError("Financial Account not configured for DD ",module);
				   return ServiceUtil.returnError("Financial Account not configured for DD ");
			   }
			   List<GenericValue> activeDDFinAccList = EntityUtil.filterByDate(ddFinAccList,fromDate);
			   if(UtilValidate.isEmpty(activeDDFinAccList)){
				   Debug.logError("No Active  DD Account is Found for this period========>"+customTimePeriod.get("fromDate")+" - "+customTimePeriod.get("thruDate"),module);
				   return ServiceUtil.returnError("No Active  DD Account is Found for this period========>"+customTimePeriod.get("fromDate")+" - "+customTimePeriod.get("thruDate"));
			   }
			   GenericValue ddFinAcc =  EntityUtil.getFirst(activeDDFinAccList);
			   ddAccDetailsMap.put("partyId", partyId);
			   GenericValue ddPartyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId",partyId),false);
			   ddAccDetailsMap.put("accountHolder", (String)ddPartyGroup.get("groupName"));
			   ddAccDetailsMap.put("nameOfTheBank",ddFinAcc.get("finAccountName"));
			   ddAccDetailsMap.put("nameOfTheBrch",ddFinAcc.get("finAccountBranch"));
			   ddAccDetailsMap.put("ifscCode",ddFinAcc.get("ifscCode"));
			   ddAccDetailsMap.put("bankAccNo",ddFinAcc.get("finAccountCode"));
		   }
		   resultMap = ServiceUtil.returnSuccess();
	   	}catch (GenericEntityException e) {
			// TODO: handle exception
	    	Debug.logError("Eror while getting DDAccountDetails======>"+e,module);
	    	return ServiceUtil.returnError("Eror while getting DDAccountDetails======>"+e);
		}
		resultMap.put("ddAccDetailsMap", ddAccDetailsMap);
	   return resultMap;
   } 
    
   
   public static Map<String, Object> getMilkosoftCharges(DispatchContext dctx, Map<String, ? extends Object> context){
	    Map resultMap = FastMap.newInstance();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    BigDecimal quantity = (BigDecimal)context.get("quantity");
	    String facilityId = (String)context.get("facilityId");
	    String productId = (String)context.get("productId");
	    BigDecimal amount = BigDecimal.ZERO;
	    Map<String,Object> procurementPeriodTotals = FastMap.newInstance();
	    Timestamp fromDate = UtilDateTime.nowTimestamp();
	    Timestamp thruDate = UtilDateTime.nowTimestamp();
	    String fromDateStr = "2013-03-16";
	    String thruDateStr = "2013-04-30";
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	  	try {
	  		 fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
	  		 thruDate = new java.sql.Timestamp(sdf.parse(thruDateStr).getTime());
	  		} catch (ParseException e) {
			  Debug.logError("Can not parse the Date String===>"+fromDateStr,module);
	  		}
		procurementPeriodTotals = getPeriodTotals(dctx , UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "facilityId", facilityId,"userLogin",userLogin));
	    
		if(!procurementPeriodTotals.isEmpty()){
			Map tempDayTotalsMap = (Map)((Map)procurementPeriodTotals.get(facilityId)).get("dayTotals");
			quantity = (BigDecimal)((Map)((Map)((Map)tempDayTotalsMap.get("TOT")).get("TOT")).get("TOT")).get("qtyLtrs");
			if(UtilValidate.isNotEmpty(productId)){
				quantity = (BigDecimal)((Map)((Map)((Map)tempDayTotalsMap.get("TOT")).get("TOT")).get(productId)).get("qtyLtrs");
			}
			
		}
		
		BigDecimal tempAmount = quantity.multiply(new BigDecimal(0.025));
	    BigDecimal serviceTax = tempAmount.multiply(new BigDecimal(12.36)).divide(new BigDecimal(100));
	    amount = amount.add(tempAmount.add(serviceTax));
	    resultMap.put("amount", amount);
	   return resultMap;
   }  
   
}

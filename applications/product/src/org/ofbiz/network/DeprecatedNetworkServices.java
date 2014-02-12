package org.ofbiz.network;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.xml.rpc.ServiceException;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.net.ntp.TimeStamp;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.product.ProductEvents;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * 
 * @deprecated Do not use this class methods!
 */

@Deprecated
public class DeprecatedNetworkServices {

    public static final String module = DeprecatedNetworkServices.class.getName();
    
    private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals;
    private static int rounding;
    private static String obInvoiceType = "OBINVOICE_IN";
    
    public static final String resource_error = "OrderErrorUiLabels";
    static {
        decimals = 1;//UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }    

	public static Map<String, Object>  sendChangeIndentSms(DispatchContext dctx, Map<String, Object> context)  {
        String facilityId = (String) context.get("facilityId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");      
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();  
		Timestamp estimatedDeliveryDate = UtilDateTime.nowTimestamp();        
        Map<String, Object> serviceResult;
		List conditionList= FastList.newInstance();
		// ::TODO:: currently only CASH changes
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));
		conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS , "CASH"));
		EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, estimatedDeliveryDate);
        conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
        		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, estimatedDeliveryDate)));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);	
		try {
			List<GenericValue> indentList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null , null, null, false);			
		
			if (indentList == null) {
				Debug.logWarning("No indents for " + facilityId + " for " + estimatedDeliveryDate, module);
				return ServiceUtil.returnError("No indents for " + facilityId + " for " + estimatedDeliveryDate);            	
			}
    		String destinationPartyId = "";
    		String text = "INDENT: ";
            Iterator<GenericValue> indentIter = indentList.iterator();
        	while(indentIter.hasNext()) {
                GenericValue indent = indentIter.next();
                String productId = indent.getString("productId");
            	GenericValue product = delegator.findOne("Product",true, UtilMisc.toMap("productId", indent.getString("productId")));
            	if (product == null) {
                    return ServiceUtil.returnError("Invalid productId " + indent.getString("productId"));         		
            	}
                if (indent.getBigDecimal("quantity").compareTo(BigDecimal.ZERO) == 0) {
                	continue;
                }            	
                text += product.getString("brandName");
                text += "=";
                text += indent.getBigDecimal("quantity").intValue();
                text += ";";
                destinationPartyId = indent.getString("ownerPartyId");
        	}
            if (UtilValidate.isEmpty(destinationPartyId)) {
            	Debug.logError("Invalid destination party id for booth " + facilityId, module);
            	return ServiceUtil.returnError("Invalid destination party id for booth  " + facilityId);             
            }
            Map<String, Object> getTelParams = FastMap.newInstance();
            getTelParams.put("partyId", destinationPartyId);
            getTelParams.put("userLogin", userLogin);                    	
            serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
            if (ServiceUtil.isError(serviceResult)) {
            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
            } 
            String contactNumberTo = (String) serviceResult.get("countryCode") + (String) serviceResult.get("contactNumber");            
            Map<String, Object> sendSmsParams = FastMap.newInstance();      
            sendSmsParams.put("contactNumberTo", contactNumberTo);          
            sendSmsParams.put("text", text);            
            serviceResult  = dispatcher.runSync("sendSms", sendSmsParams);       
            if (ServiceUtil.isError(serviceResult)) {
            	Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
            	return serviceResult;
            }    
		}
		catch (Exception e) {
			Debug.logError(e, "Problem getting Invoice", module);
			return ServiceUtil.returnError(e.getMessage());
		}        
        return ServiceUtil.returnSuccess();
    }
	
    /**
     * Service to update eseva and aponline collection for the given sales date
     * @param ctx the dispatch context
     * @param context 
     * @return a List of routes
     */
    public static Map<String, Object> updateEsevaAponlineCollection(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		Timestamp salesDate = (Timestamp) context.get("salesDate");      	
    	BigDecimal esevaCollection = (BigDecimal) context.get("esevaCollection");
    	BigDecimal aponlineCollection = (BigDecimal) context.get("aponlineCollection");
    	BigDecimal esevaCharges = (BigDecimal) context.get("esevaCharges");
    	BigDecimal aponlineCharges = (BigDecimal) context.get("aponlineCharges");
        Map<String, Object> result = ServiceUtil.returnSuccess();        
        Debug.logInfo("context " + context, module);
   	
    	
    	try {
        	GenericValue collectionRecord = delegator.findOne("LMSEsevaAponlineCollection",false, UtilMisc.toMap("salesDate", UtilDateTime.toSqlDate(salesDate)));
        	if (collectionRecord == null) {
        		// insert new record
        		collectionRecord = delegator.makeValue("LMSEsevaAponlineCollection");
        		collectionRecord.put("salesDate", UtilDateTime.toSqlDate(salesDate));
        		if (esevaCollection != null) {
        			collectionRecord.put("esevaCollection", esevaCollection);
        		}
        		if (aponlineCollection != null) {
        			collectionRecord.put("aponlineCollection", aponlineCollection);
        		}   
        		if (esevaCharges!= null) {
        			collectionRecord.put("esevaCharges", esevaCharges);
        		}
        		if (aponlineCharges != null) {
        			collectionRecord.put("aponlineCharges", aponlineCharges);
        		}   
        		collectionRecord.create();         		
        	}
        	else {
        		// update existing record
        		if (esevaCollection != null) {
        			collectionRecord.put("esevaCollection", esevaCollection);
        		}
        		if (aponlineCollection != null) {
        			collectionRecord.put("aponlineCollection", aponlineCollection);
        		}
        		if (esevaCharges!= null) {
        			collectionRecord.put("esevaCharges", esevaCharges);
        		}
        		if (aponlineCharges != null) {
        			collectionRecord.put("aponlineCharges", aponlineCharges);
        		}  
        		collectionRecord.store();
        	}
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }  
    
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

//        	routeFacility = boothFacility.getRelatedOneCache("ParentFacility");
//        	zoneFacility = routeFacility.getRelatedOneCache("ParentFacility");
//        	distributorFacility = zoneFacility.getRelatedOneCache("ParentFacility");
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
        List shipmentIds = getShipmentIds(delegator , UtilDateTime.toDateString(paymentTimestamp, "yyyy-MM-dd HH:mm:ss"),null);
		
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
		 if(enableSoCrPmntTrack){
				exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.IN, UtilMisc.toList("CASH","SPECIAL_ORDER","CREDIT")));
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
        return result;
    }  
    
    /**
     * Get all payment pending booths
     * @param ctx the dispatch context
     * @param context 
     * @return a List of payment pending booths
     */
    public static Map<String, Object> getPaymentPendingBooths(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		List exprListForParameters = FastList.newInstance();
		List boothList = FastList.newInstance();
		List shipmentIds = getShipmentIds(delegator , UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"),null);		
		exprListForParameters.add(EntityCondition.makeCondition("invoiceStatusId",  EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF")));
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
		
		exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try{
			List<GenericValue> orderList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, UtilMisc.toSet("originFacilityId", "facilityName") , UtilMisc.toList("originFacilityId"), findOptions, false);
			// lets get the opening balance invoices if any
			List<GenericValue> obInvoiceList = (List)getOpeningBalanceInvoices(ctx,UtilMisc.toMap()).get("invoiceList");
	    	orderList.addAll(obInvoiceList);
	    	
			for (GenericValue order: orderList) {
                Map<String, Object> boothMap = FastMap.newInstance();  
            	boothMap.put("boothId", order.getString("originFacilityId"));
            	boothMap.put("boothName", order.getString("facilityName")); 
            	boothList.add(boothMap);
            }
		} catch(GenericEntityException e){
            Debug.logError(e, module);			
            return ServiceUtil.returnError(e.getMessage());        	
		}    	

        Map<String, Object> result = ServiceUtil.returnSuccess();        
        result.put("boothList", boothList);

        return result;
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
	   Map absenteeOverrideMap = DeprecatedNetworkServices.getAbsenteeOverrideBooths(ctx , UtilMisc.toMap("overrideSupplyDate",supplyDate));
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
    		
        	Map<String, Object> paidPaymentCtx = UtilMisc.<String, Object>toMap("paymentMethodTypeId", "ESEVA_PAYIN");    
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
    		Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentMethodTypeId", "CASH_"+paymentLocationId+"_PAYIN");    		
    		paymentCtx.put("userLogin", userLogin);
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
        	List shipmentIds =FastList.newInstance();
        	if(UtilValidate.isEmpty(subscriptionType)){
        		shipmentIds = getShipmentIds(delegator , UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"),null);
        	}else{
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
        		if(subscriptionType.equals("PM") && !enableSameDayPmEntry){
        			shipmentIds = getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp( salesDate, -1), "yyyy-MM-dd HH:mm:ss"),subscriptionType);
        		}else{
        			shipmentIds = getShipmentIdsByAMPM(delegator , UtilDateTime.toDateString(salesDate, "yyyy-MM-dd HH:mm:ss"),subscriptionType);
        			
        		}
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
    	if (!onlySummary) {
    		boothZoneMap = getAllBoothsZonesMap(delegator); 
    	}
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
    		if (!onlySummary) {
    			
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
    			// Handle zone totals
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
    			}
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
					DeprecatedLMSSalesHistoryServices.LMSSalesHistorySummaryDetail(ctx,  UtilMisc.toMap("salesDate", summaryDate));
				}
				else {
					// check and see if we need to update for whatever reason
					BigDecimal summaryQuantity  = salesSummary.getBigDecimal("totalQuantity");
					BigDecimal summaryRevenue  = salesSummary.getBigDecimal("totalRevenue");     
					if (summaryQuantity.compareTo(totalQuantity) != 0 || summaryRevenue.compareTo(totalRevenue) != 0) {
						salesSummary.put("totalQuantity", totalQuantity);
						salesSummary.put("totalRevenue", totalRevenue);  
						salesSummary.store();
						DeprecatedLMSSalesHistoryServices.LMSSalesHistorySummaryDetail(ctx,  UtilMisc.toMap("salesDate", summaryDate));
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
    
    public static List<GenericValue> getLmsProducts(DispatchContext dctx, Map<String, ? extends  Object> context){
    	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         if(!UtilValidate.isEmpty(context.get("salesDate"))){
        	salesDate =  (Timestamp) context.get("salesDate");  
         }
        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
    	List<GenericValue> productList =FastList.newInstance();
    	List condList =FastList.newInstance();
    	condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "LMS"));
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
    
    public static Map getOpeningBalanceForBooth(DispatchContext dctx, Map<String, ? extends Object> context){
    	//Delegator delegator ,LocalDispatcher dispatcher ,GenericValue userLogin,String paymentDate,String invoiceStatusId ,String facilityId ,String paymentMethodTypeId ,boolean onlyCurrentDues ,boolean isPendingDues){
		//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
	    Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String facilityId = (String) context.get("facilityId");
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
			exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "CASH"), EntityOperator.OR ,EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, categoryTypeEnumList)));
		}
		
		if(facilityId != null){			
			try{
				GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
				if(facilityDetail == null || (!facilityDetail.getString("facilityTypeId").equals("BOOTH")) ){
					Debug.logInfo("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
					return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth or Zone ");
				}
				
				exprListForParameters.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
				
			}catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}			
		}
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
		exprListForParameters.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("paidDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate))));
		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try{			
			boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", paramCond, UtilMisc.toSet("invoiceId") ,null, findOptions, false);
			List<GenericValue> obInvoiceList = (List)getOpeningBalanceInvoices(dctx,UtilMisc.toMap("facilityId",facilityId,"isForCalOB","Y")).get("invoiceList");
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
				exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("paidDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate))));
				exprList.add(EntityCondition.makeCondition("pmPaymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate)));
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
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		exprList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate)));
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paidDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("paidDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate))));
		exprList.add(EntityCondition.makeCondition("pmPaymentDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(saleDate)));
		EntityCondition	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);				
		try{			
			pendingPaymentsList = delegator.findList("InvoiceAndApplAndPayment", cond, null ,null, null, false);
		}catch(GenericEntityException e){
			Debug.logError(e, module);	
			return ServiceUtil.returnError(e.toString());
		}		
		Set paymentSet = new HashSet(EntityUtil.getFieldListFromEntityList(pendingPaymentsList, "paymentId", false));
		for( GenericValue pendingPayments : pendingPaymentsList){
			 advancePaymentAmount = advancePaymentAmount.add(pendingPayments.getBigDecimal("amountApplied"));
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
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		//exprList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(saleDate)));
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentApplicationId", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition(EntityCondition.makeCondition("isFullyApplied", EntityOperator.EQUALS, null),EntityOperator.OR ,EntityCondition.makeCondition("isFullyApplied", EntityOperator.EQUALS, "N"))));
		exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
		exprList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN, UtilDateTime.getDayStart(saleDate)));
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
	
    public static Map isFacilityAcitve(DispatchContext ctx, Map<String, ? extends Object> context) {
    	
    	Delegator delegator = ctx.getDelegator();		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String facilityId = (String) context.get("facilityId");
		Timestamp filterByDate = (Timestamp) context.get("fromDate");
		if(UtilValidate.isEmpty(filterByDate)){
			filterByDate = UtilDateTime.nowTimestamp();
		}
    	try{
    		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
    		boolean isActive = EntityUtil.isValueActive(facility , filterByDate, "openedDate", "closedDate");
    		if(!isActive){
    			Debug.logError("is not active facility"+facilityId, module);    			
    			return ServiceUtil.returnError("The  facility ' "+ facilityId+"' is not Active."); 
    		}
    	}catch (GenericEntityException e) {
			// TODO: handle exception
    		Debug.logError(e, module);    			
			return ServiceUtil.returnError(e.getMessage());    		
		}
		
    	
        return ServiceUtil.returnSuccess();
    }
    public static Map<String, Object> createFacilityRate(DispatchContext dctx, Map context) {
    	
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess("Booth discount updated successfully.");	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String)context.get("facilityId");
		String productId = (String)context.get("productId");
		BigDecimal amount = (BigDecimal)context.get("amount");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		if(UtilValidate.isNotEmpty(context.get("fromDate"))){
			 fromDate = (Timestamp) context.get("fromDate");
		}		
		Timestamp dayStart = UtilDateTime.getDayStart(fromDate);
		Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(dayStart, -1));
		String rateTypeId ="";
		if(UtilValidate.isEmpty(productId)){
			productId = "_NA_";
		}
		try{			
			GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
			if(!(facility.getString("facilityTypeId")).equals("BOOTH")){
				Debug.logError(facilityId+"====is not a booth", module);    			
                return ServiceUtil.returnError(facilityId+"====is not a booth");
			}
			List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "RATE_HOUR"));
			condList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, "INR"));
			condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facility.getString("ownerPartyId")));	    	
			condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, "_NA_"));
			condList.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, "_NA_"));	    	
			if(UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum"))){
				if((facility.getString("categoryTypeEnum")).equals("VENDOR") ){
					rateTypeId = "VENDOR_DEDUCTION";				
				}else{
					rateTypeId = facility.getString("categoryTypeEnum")+"_MRGN";
				}
			}					
			condList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
			condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> rateAmounts = delegator.findList("RateAmount", condition, null, null, null, false);
			rateAmounts =EntityUtil.filterByDate(rateAmounts ,dayStart);
			GenericValue rateAmount = EntityUtil.getFirst(rateAmounts);
			if(UtilValidate.isNotEmpty(rateAmount)){
				rateAmount.put("thruDate", previousDayEnd);
				delegator.store(rateAmount);
				// lets create new rate amount record for the Booth
				rateAmount.put("thruDate", null);
				rateAmount.put("fromDate", dayStart);
				rateAmount.put("productId", productId);
				rateAmount.put("rateAmount", amount);
				delegator.createOrStore(rateAmount);
			}else{
				GenericValue newRateAmount = delegator.makeValue("RateAmount");				
				newRateAmount.put("periodTypeId", "RATE_HOUR");
				newRateAmount.put("rateCurrencyUomId", "INR");
				newRateAmount.put("partyId", facility.getString("ownerPartyId"));
				newRateAmount.put("workEffortId", "_NA_");
				newRateAmount.put("emplPositionTypeId", "_NA_");				
				newRateAmount.put("rateTypeId", rateTypeId);
				newRateAmount.put("fromDate", dayStart);
				newRateAmount.put("productId", productId);
				newRateAmount.put("rateAmount", amount);				
				delegator.create(newRateAmount);				
			}	
			condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, dayStart));
			EntityCondition condition1 = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> futureDaysRateAmounts = delegator.findList("RateAmount", condition1, null, null, null, false);
			delegator.removeAll(futureDaysRateAmounts);
			
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError( e.toString(), module);
			return ServiceUtil.returnError(e.toString());
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
        	facilityIds = (List)DeprecatedNetworkServices.getFacilityGroupMemberList(ctx, UtilMisc.toMap("facilityGroupId",facilityGroupId ,"fromDate" , fromDate)).get("facilityIds");;
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
        	Map<String, Object> groupDetail = DeprecatedNetworkServices.getFacilityGroupDetail(ctx, UtilMisc.toMap("facilityGroupId", facilityGroup.getString("facilityGroupId"),"fromDate" , fromDate));   	
        	result.putAll(groupDetail);
        }
        catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());          	
        }
        
        return result;
    }
    public static String getLmsFacilityGroupByFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
		String facilityId = (String) context.get("facilityId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String facilityGroupId = null;
		if(UtilValidate.isEmpty(fromDate)){
			fromDate = UtilDateTime.nowTimestamp();
		}
        Map<String, Object> result = FastMap.newInstance(); 
       
    	List<GenericValue> getLmdGroupList = (List)getLmdGroups(delegator).get("lmdGroupList");
    	for(GenericValue lmdGroup : getLmdGroupList){
    		
    		String tempFacilityGroupId = lmdGroup.getString("facilityGroupId");
    		Map<String, Object> tempGroupDetail = getFacilityGroupDetail(ctx , UtilMisc.toMap("facilityGroupId" ,tempFacilityGroupId));      		
    		Set tempGroupFacility = new HashSet();
    		List tempGroupBoothList = (List)tempGroupDetail.get("facilityIds");
    		tempGroupFacility.addAll(tempGroupBoothList);
    		tempGroupFacility.addAll(EntityUtil.getFieldListFromEntityList((List)tempGroupDetail.get("routeList"), "facilityId", true));
    		tempGroupFacility.addAll(EntityUtil.getFieldListFromEntityList((List)tempGroupDetail.get("zoneList"), "facilityId", true));
    		if(tempGroupFacility.contains(facilityId)){
    			return tempFacilityGroupId;
    		}
    		
    	}
   
        return facilityGroupId;
    }
    public static Map<String, Object> getLmdGroups(Delegator delegator){
        Map<String, Object> result = FastMap.newInstance(); 
    	try {
    		List<GenericValue> lmdGroupList = delegator.findList("FacilityGroup", EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS, "DAIRY_LMD_TYPE"), null, null, null, false);
            result.put("lmdGroupList", lmdGroupList);
        	
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
    	return result;
	}
    public static String getLmsFacilityGroupProductStore(Delegator delegator ,String facilityGroupId){		
   	 String productStoreId = null;
   	 try{
        	if(UtilValidate.isNotEmpty(facilityGroupId)){
        		GenericValue facilityGroup = delegator.findOne("FacilityGroup",UtilMisc.toMap("facilityGroupId",facilityGroupId) , false);
            	productStoreId = facilityGroup.getString("ownerFacilityId");
            	
            }
        }catch (GenericEntityException e) {
   		 	Debug.logError(e, module);             
   		 	return productStoreId;
        } 
        
        return productStoreId;
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

    public static Map<String, Object> getDateWiseBoothDiscounts(DispatchContext ctx, Map<String, ? extends Object> context) {
		 Delegator delegator = ctx.getDelegator();
		 LocalDispatcher dispatcher = ctx.getDispatcher(); 
		 Map<String, Object> result = FastMap.newInstance();
	     Map<String, Object> discountsByDateMap = FastMap.newInstance();
	     GenericValue userLogin = (GenericValue) context.get("userLogin");          
	     String facilityId = (String) context.get("facilityId");
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
	    Timestamp dayBegin =UtilDateTime.getDayStart(fromDate);
	 	List<GenericValue> productList =FastList.newInstance();
	 	
	 	productList=getAllLmsAndByProdProducts(ctx, UtilMisc.toMap("salesDate",fromDate));
	    List 	productIdsList=FastList.newInstance();
	    List<Map> productRatesList=FastList.newInstance();
	    productIdsList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
	
	    int intervalDays = (UtilDateTime.getIntervalInDays(fromDate, thruDate))+1;
	    GenericValue  facility;
			try{
		      facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			}catch (GenericEntityException e) {
	         Debug.logError(e, e.getMessage());
	         return ServiceUtil.returnError("Error While getting facility======!");          	
	     }
		Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
		String categoryType=facility.getString("categoryTypeEnum");
		String rateTypeId =categoryType+"_MRGN";
		if("VENDOR".equals(categoryType)){
			rateTypeId = "VENDOR_DEDUCTION";
		}
		inputRateAmt.put("rateTypeId", rateTypeId);
		inputRateAmt.put("periodTypeId", "RATE_HOUR");
		inputRateAmt.put("partyId", facility.getString("ownerPartyId"));
		inputRateAmt.put("rateCurrencyUomId","INR");
		
		 BigDecimal tempProdMargin =BigDecimal.ZERO;
		 int cnt=0;
		
		 	for (Object  productId:productIdsList){		  
		       BigDecimal productMargin=BigDecimal.ZERO;
		       Timestamp rateAmtThruDate = thruDate;
		       for(int i=0 ; i< intervalDays ; i++){
		    		Timestamp marginDate = UtilDateTime.addDaysToTimestamp(fromDate, i);
		    		String marginDateStr=UtilDateTime.toDateString(marginDate ,"yyyy-MM-dd");
		    		
		    		Map productsMarginMap=FastMap.newInstance();
		    		Map tempDateMap =FastMap.newInstance();
			    	if(UtilValidate.isNotEmpty(discountsByDateMap.get(marginDateStr)))	{			    		
			    		tempDateMap=(Map)(discountsByDateMap.get(marginDateStr));
				    }
					inputRateAmt.put("fromDate",marginDate);
		    		inputRateAmt.put("productId", productId);//setting each product for forSpecial Discount
					try {
				        Map rateAmount =null;
				        if(marginDate.compareTo(fromDate)==0){
				        	++cnt;
				        	rateAmount = dispatcher.runSync("getPartyDiscountAmount", inputRateAmt);//Run Serivice for each product
				        	productMargin = (BigDecimal) rateAmount.get("rateAmount");
				        	if(productMargin.compareTo(BigDecimal.ZERO) > 0){
				        	  	GenericValue productMarginEntry = (GenericValue)rateAmount.get("rateAmountEntry");
					        	Map tempMap = FastMap.newInstance();
					        	tempMap.put("rateAmount", (BigDecimal)productMarginEntry.get("rateAmount"));
					        	tempMap.put("fromDate", productMarginEntry.get("fromDate"));
					        	tempMap.put("thruDate", productMarginEntry.get("thruDate"));
					        	tempMap.put("productId", productMarginEntry.get("productId"));
					        	tempMap.put("facilityId", facility.get("facilityId"));
					        	tempMap.put("facilityName", facility.get("facilityName"));
					        	tempMap.put("categoryTypeEnum", facility.get("categoryTypeEnum"));
					        	productRatesList.add(tempMap);
				        	}
							tempProdMargin=(BigDecimal) rateAmount.get("rateAmount");
							rateAmtThruDate= (Timestamp)rateAmount.get("thruDate");
							
				        }				
						if(UtilValidate.isNotEmpty(rateAmtThruDate)&& (rateAmtThruDate.compareTo(thruDate)<=0)){
							++cnt;
							rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
							productMargin = (BigDecimal) rateAmount.get("rateAmount");
							tempProdMargin=(BigDecimal) rateAmount.get("rateAmount");
						}else{
							productMargin=tempProdMargin;
						}
							
					}catch (GenericServiceException e) {
						Debug.logError(e, e.toString(), module);
				        return ServiceUtil.returnError(e.toString());
					}
					if(productMargin.compareTo(BigDecimal.ZERO) !=0){
				      tempDateMap.put((String)productId, productMargin);
				      productsMarginMap.putAll(tempDateMap);
				      discountsByDateMap.put(marginDateStr, productsMarginMap);
					}
		       }
		  }
		 	Map input = FastMap.newInstance();
		 	input.put("userLogin", userLogin);
		 	input.put("rateTypeId", rateTypeId);
		 	input.put("periodTypeId", "RATE_HOUR");
		 	input.put("partyId", "_NA_");
		 	input.put("rateCurrencyUomId","INR");
		 	input.put("fromDate",fromDate);
		 	input.put("productId", "_NA_");
		 	try{
		 		Map resultProductMap = dispatcher.runSync("getPartyDiscountAmount", input);//Run Serivice for each product
		 		GenericValue allProductMarginEntry = (GenericValue)resultProductMap.get("rateAmountEntry");
	        	Map tempMap = FastMap.newInstance();
	        	tempMap.put("rateAmount", (BigDecimal)allProductMarginEntry.get("rateAmount"));
	        	tempMap.put("fromDate", allProductMarginEntry.get("fromDate"));
	        	tempMap.put("thruDate", allProductMarginEntry.get("thruDate"));
	        	tempMap.put("productId", allProductMarginEntry.get("productId"));
	        	tempMap.put("facilityId", facility.get("facilityId"));
	        	tempMap.put("facilityName", facility.get("facilityName"));
	        	tempMap.put("categoryTypeEnum", facility.get("categoryTypeEnum"));
	        	productRatesList.add(tempMap);
		 	}
		 	catch(GenericServiceException e){
		 		Debug.logError(e, e.toString(), module);
		        return ServiceUtil.returnError(e.toString());
		 	}
	     result.put("discountsByDate", discountsByDateMap);
	     result.put("productRatesList", productRatesList);
	     return result;
	 }  
}

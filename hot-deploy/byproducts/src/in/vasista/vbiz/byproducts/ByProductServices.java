package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.ofbiz.accounting.util.UtilAccounting;
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
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
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


public class ByProductServices {
	
	public static final String module = ByProductServices.class.getName();
	
	
	public static Map<String, Object> getProdStoreProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");	
        
        Timestamp salesDate = UtilDateTime.nowTimestamp();
        if(!UtilValidate.isEmpty(context.get("salesDate"))){
        	salesDate =  (Timestamp) context.get("salesDate");  
         }
        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
        
        String productStoreId = (String) context.get("productStoreId"); 
        String isVariant = (String) context.get("isVariant");
        if(UtilValidate.isEmpty(isVariant)){
        	isVariant = "Y";
        }
        
        GenericValue productStore = null;	
		if (UtilValidate.isNotEmpty(productStoreId)) { // 2011-12-25 18:09:45
			try {
				productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details for productStoreId " + productStoreId);
			}
		}
        
		List<GenericValue> catalogIds = null;
		if (UtilValidate.isNotEmpty(productStore)){
	        try {
	        	catalogIds = productStore.getRelated("ProductStoreCatalog");
	        } catch (GenericEntityException e) {
	        	Debug.logError(e, module);
				return ServiceUtil.returnError("Error fetching details for ProductStoreCatalog with productStoreId" + productStoreId);
	        }
		}
		catalogIds = EntityUtil.filterByDate(catalogIds, dayBegin);
		List catalogIdsFieldList = EntityUtil.getFieldListFromEntityList(catalogIds, "prodCatalogId", false);
		
		List conditionList = FastList.newInstance();
		conditionList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, catalogIdsFieldList));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<GenericValue> catalogCategoryIds;
		try {
			catalogCategoryIds = delegator.findList("ProdCatalogCategory", condition , UtilMisc.toSet("productCategoryId"), null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error fetching details for ProdCatalogCategory with productStoreId " + productStoreId);
		}
		List categoryIdsFieldList = EntityUtil.getFieldListFromEntityList(catalogCategoryIds, "productCategoryId", false);
        
        conditionList.clear();
        
		conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIdsFieldList));
		/*conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL, "BYPROD"));*/
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
   			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, dayBegin)));
		conditionList.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, isVariant));
		EntityCondition prodCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		/*EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setDistinct(true);*/
		List<GenericValue> categoryProducts = null;
		try {
			categoryProducts = delegator.findList("ProductAndCategoryMember", prodCondition , UtilMisc.toSet("productId", "productName", "primaryProductCategoryId", "productCategoryId", "quantityIncluded"), null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error fetching details for ProdCatalogCategory with productStoreId " + productStoreId);
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
		
	public static Map<String, Object> createByProdShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> vehicleTripResult = new HashMap<String, Object>();
        Map<String, Object> vehicleTripStatusResult = new HashMap<String, Object>();
        Map<String, Object> vehicleTripEntity = FastMap.newInstance(); 
        Map<String, Object> vehicleTripStatusEntity = FastMap.newInstance(); 
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        String estimatedDeliveryDateString = (String) context.get("estimatedDeliveryDate");
        String routeId = (String) context.get("routeId");
        String tripId = (String) context.get("tripId");
        List routesList = FastList.newInstance();
        Timestamp estimatedDeliveryDate = null;
        String vehicleId="";
        String shipmentTypeId = (String) context.get("shipmentTypeId");
        String subscriptionTypeId = "AM";
        if(shipmentTypeId.equals("PM_SHIPMENT")){
        	subscriptionTypeId = "PM";       	
        }
        String facilityGroupId = (String) context.get("facilityGroupId");
        String shipmentId = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        String  seqId="";
        List exclInvoiceFacilityIdsList = FastList.newInstance();
		try {
			estimatedDeliveryDate = new java.sql.Timestamp(sdf.parse(estimatedDeliveryDateString).getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: "+ estimatedDeliveryDateString, module);			
			return ServiceUtil.returnError("Failed to Generate TruckSheet ,Cannot parse date string:" + e);
			// effectiveDate = UtilDateTime.nowTimestamp();
		}
		List creditInstList= (List)ByProductNetworkServices.getAllBooths(delegator, "CR_INST").get("boothsList");
		List<GenericValue> facilityCustomTimePeriod = FastList.newInstance();
		List dailyBillingFacility = FastList.newInstance();
		try{
			facilityCustomTimePeriod = delegator.findList("FacilityCustomBilling", EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "INST_DAILY_BILL"), null, null, null, false);
			facilityCustomTimePeriod = EntityUtil.filterByDate(facilityCustomTimePeriod, estimatedDeliveryDate);
			dailyBillingFacility = EntityUtil.getFieldListFromEntityList(facilityCustomTimePeriod, "facilityId", true);
		}catch(GenericEntityException e){
			Debug.logError(e, "Error fetching daily billing credit institutions", module);			
			return ServiceUtil.returnError("Error fetching daily billing credit institutions" + e);
		}
		if(UtilValidate.isNotEmpty(dailyBillingFacility)){
			for(int i=0;i<creditInstList.size();i++){
				String facId = (String)creditInstList.get(i);
				if(!dailyBillingFacility.contains(facId)){
					exclInvoiceFacilityIdsList.add(facId);
				}
			}
		}
		else{
			exclInvoiceFacilityIdsList.addAll(creditInstList);
		}
		//checking for one route  or all routes
		if(UtilValidate.isNotEmpty(routeId) && (routeId.equals("AllRoutes"))){
			routesList = (List) getByproductRoutes(delegator).get("routeIdsList");
        }else{
        	routesList.add(routeId);
        }
        //checking for indents if indents are not there for facility(route) then throw an error
		
		List<GenericValue> subscriptionProductsList = FastList.newInstance();         
        try {
        	
        	List conditionList = UtilMisc.toList(
    			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, estimatedDeliveryDate));
            conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
            		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, estimatedDeliveryDate)));
            conditionList.add(EntityCondition.makeCondition("quantity", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
            conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
            conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.IN, routesList));
            if(UtilValidate.isNotEmpty(tripId)){
            	conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
            }
           	
            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
            List<String> orderBy = UtilMisc.toList("subscriptionId", "productSubscriptionTypeId","-productId"); 
        	subscriptionProductsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, orderBy, null, false);
        	
        	if(UtilValidate.isEmpty(subscriptionProductsList)){
        		Debug.logError("No indents found for route :'"+routeId+"'", module);
                return ServiceUtil.returnError("No Indents found for route :"+routeId);        		
            }
        	routesList = EntityUtil.getFieldListFromEntityList(subscriptionProductsList, "sequenceNum", true);
        }catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Subscription Products", module);
            return ServiceUtil.returnError("Failed to Generate TruckSheet ,Cannot parse date string:" + e);
            //::TODO:: set shipment status
        }
		
        // checking if shipment exists for the specified day then return without creating orders.
		estimatedDeliveryDate = UtilDateTime.getDayStart(estimatedDeliveryDate);
        Timestamp dayBegin = UtilDateTime.getDayStart(estimatedDeliveryDate);
        Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedDeliveryDate);
        List conditionList = FastList.newInstance();
        List shipmentList = FastList.newInstance();
        GenericValue facilityGroup = null;
        
        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN , UtilMisc.toList("SHIPMENT_CANCELLED", "GENERATION_FAIL")));
        conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS ,shipmentTypeId));
    	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
    	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
      	conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN, routesList));
      	if(UtilValidate.isNotEmpty(tripId)){
      		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
      	}
   		List existingRoutesList = FastList.newInstance();
    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    	try {
    		shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
    		existingRoutesList = EntityUtil.getFieldListFromEntityList(shipmentList, "routeId", true);
    		if(UtilValidate.isNotEmpty(existingRoutesList)){
        		routesList.removeAll(existingRoutesList);
        	}
        	if(UtilValidate.isEmpty(routesList)){
        		 Debug.logError("Failed to generate a trucksheet ,allready generated  for:"+routeId, module);             
                 return ServiceUtil.returnError("Failed to generate a trucksheet ,allready generated  for:"+routeId);
        	}
    	}catch (GenericEntityException e) {
    		 Debug.logError(e, module);             
             return ServiceUtil.returnError("Failed to find ShipmentList " + e);
		}      
    	
    	List shipmentIdsList = FastList.newInstance();
		for(int i = 0; i < routesList.size(); i++){
			
			GenericValue newEntity = delegator.makeValue("Shipment");
	        if(UtilValidate.isNotEmpty(facilityGroup)){
	        	newEntity.set("originFacilityId", facilityGroup.getString("ownerFacilityId"));
	        }
	        newEntity.set("estimatedShipDate", estimatedDeliveryDate);
	        newEntity.set("shipmentTypeId", shipmentTypeId);
	        newEntity.set("routeId", routesList.get(i));
	        if(UtilValidate.isNotEmpty(tripId)){
	        	newEntity.set("tripNum", tripId);
	        }
	        newEntity.set("statusId", "IN_PROCESS");
	        newEntity.set("createdDate", nowTimeStamp);
	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	        try {
	            delegator.createSetNextSeqId(newEntity);            
	            shipmentId = (String) newEntity.get("shipmentId");
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError("Failed to create a new shipment " + e);            
	        }  	   
	              	       	
	        shipmentIdsList.add(shipmentId);
	        Map vehicleCtx = UtilMisc.toMap("facilityId",routesList.get(i));
	        vehicleCtx.put("supplyDate", estimatedDeliveryDate);
	      
	        Map vehicleRoleResult =  (Map) ByProductNetworkServices.getVehicleRole(dctx,vehicleCtx);
	        if(UtilValidate.isNotEmpty(vehicleRoleResult.get("vehicleRole"))){
	        	 GenericValue vehicleRole= (GenericValue) vehicleRoleResult.get("vehicleRole");
				 vehicleId=vehicleRole.getString("vehicleId");
		        if(UtilValidate.isNotEmpty(vehicleId)){
		        	vehicleTripEntity.put("vehicleId", vehicleId);
		        	vehicleTripStatusEntity.put("vehicleId", vehicleId);
		        }
		        vehicleTripEntity.put("originFacilityId", routesList.get(i));
		        vehicleTripEntity.put("userLogin", userLogin);
		        vehicleTripEntity.put("shipmentId", shipmentId);
		        vehicleTripEntity.put("createdDate", nowTimeStamp);
		        vehicleTripEntity.put("createdByUserLogin", userLogin.get("userLoginId"));
		        vehicleTripEntity.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        try {
		        	vehicleTripResult=dispatcher.runSync("createVehicleTrip", vehicleTripEntity);
		            seqId =(String) vehicleTripResult.get("sequenceNum");
		            if (ServiceUtil.isError(vehicleTripResult)){
		  		  		String errMsg =  ServiceUtil.getErrorMessage(vehicleTripResult);
		  		  		Debug.logError(errMsg , module);
		  		  	    return ServiceUtil.returnError("createVehicleTrip service" + vehicleId);    
		  		  	}
		          
		        } catch (GenericServiceException e) {
		            Debug.logError(e, "Error calling createVehicleTrip service", module);
		            return ServiceUtil.returnError(e.getMessage());
		        } 
		        
		        vehicleTripStatusEntity.put("facilityId",routesList.get(i));
		        vehicleTripStatusEntity.put("sequenceNum", seqId);
		        vehicleTripStatusEntity.put("userLogin", userLogin);
		        vehicleTripStatusEntity.put("statusId", "VEHICLE_RETURNED");
		        vehicleTripStatusEntity.put("createdDate", nowTimeStamp);
		        vehicleTripStatusEntity.put("createdByUserLogin", userLogin.get("userLoginId"));
		        vehicleTripStatusEntity.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        try {
		        	vehicleTripStatusResult= dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusEntity);
		            if (ServiceUtil.isError(vehicleTripStatusResult)) {
		  		  		String errMsg =  ServiceUtil.getErrorMessage(vehicleTripStatusResult);
		  		  		Debug.logError(errMsg , module);
		  		  	    return ServiceUtil.returnError("createVehicleTripStatus service" + vehicleId);   
		  		  	}
		        }catch (GenericServiceException e) {
		            Debug.logError(e, "Error calling createVehicleTripStatus service", module);
		            return ServiceUtil.returnError(e.getMessage());
		        }
		      }
		}
		 try {
	        	Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("shipmentIds", shipmentIdsList, 
	        			"estimatedDeliveryDate", estimatedDeliveryDate,"userLogin", userLogin);
	        	runSACOContext.put("facilityGroupId", facilityGroupId);
	        	runSACOContext.put("subscriptionTypeId", subscriptionTypeId);
	        	runSACOContext.put("excludeInvoiceForFacilityIds", exclInvoiceFacilityIdsList);
	            dispatcher.runAsync("runSubscriptionAutoCreateByprodOrders", runSACOContext);
	        } catch (GenericServiceException e) {
	            Debug.logError(e, "Error calling runSubscriptionAutoCreateOrders service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        }  
		result.put("shipmentIdsList", shipmentIdsList);
        // attempt to create a Shipment entity
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
	
	public static Map<String, Object> runSubscriptionAutoCreateByprodOrders(DispatchContext dctx, Map<String,  Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<GenericValue> subscriptionList=FastList.newInstance();
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("estimatedDeliveryDate");
        List shipmentIds = (List) context.get("shipmentIds");
        List excludeInvoiceForFacilityIds = (List) context.get("excludeInvoiceForFacilityIds");
        //String facilityGroupId = (String) context.get("facilityGroupId");
       // String routeId = (String) context.get("routeId");
       String productStoreId = (String) (getByprodFactoryStore(delegator)).get("factoryStoreId");
       int orderCounter = 0;
       double elapsedSeconds;
       Timestamp startTimestamp = UtilDateTime.nowTimestamp();
       boolean beganTransaction = false;
       boolean generationFailed = false;
    
       for(int shipNo=0 ; shipNo <shipmentIds.size() ;  shipNo++){
    	   String shipmentId = (String)shipmentIds.get(shipNo);
    	   GenericValue shipment;
           try {
        	    beganTransaction = TransactionUtil.begin(72000);
	           	shipment=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
	           	if (shipment == null) {
	           		Debug.logError("Shipment does not exist " + shipmentId, module);
	           		return ServiceUtil.returnError("Shipment does not exist " + shipmentId);                    	
	           	}
           }catch (GenericEntityException e) {
	       		Debug.logError(e, "Error getting shipment " + shipmentId, module);
	       		return ServiceUtil.returnError("Error getting shipment " + shipmentId + ": " + e);         	
           }
           String routeId = shipment.getString("routeId");
           String tripId = shipment.getString("tripNum");
           String shipmentTypeId = shipment.getString("shipmentTypeId");
           String subscriptionTypeId = (String) context.get("subscriptionTypeId");
           
           List<GenericValue> subscriptionProductsList = FastList.newInstance();         
           try {        
           		List conditionList = UtilMisc.toList(
       			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, estimatedDeliveryDate));
               conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, 
               		EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, estimatedDeliveryDate)));
               conditionList.add(EntityCondition.makeCondition("quantity", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
               conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
               if(UtilValidate.isNotEmpty(routeId)){
               		conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
               }
               if(UtilValidate.isNotEmpty(tripId)){
              		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
               }
              
              	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
               List<String> orderBy = UtilMisc.toList("subscriptionId", "productSubscriptionTypeId","-productId"); 
           	subscriptionProductsList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, orderBy, null, false);
           }catch (GenericEntityException e) {
               Debug.logError(e, "Problem getting Subscription Products", module);
               shipment.set("statusId", "GENERATION_FAIL");
    		  	try{
    		  		shipment.store();
    		  	}catch (Exception ex) {
					// TODO: handle exception
				}
               return ServiceUtil.returnError(e.toString());
               //::TODO:: set shipment status
           }
           
           
           List indentedProduct = EntityUtil.getFieldListFromEntityList(subscriptionProductsList,"productId", true);
           List prodQtyList = FastList.newInstance();
           for(int i=0;i< indentedProduct.size();i++){
        	   String indProd = (String)indentedProduct.get(i);
        	   BigDecimal prodTotalQty = BigDecimal.ZERO;
        	   List<GenericValue> prodSubscriptions = EntityUtil.filterByCondition(subscriptionProductsList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, indProd));
        	   Map prodQty = FastMap.newInstance();
        	   for(GenericValue prodSubscription: prodSubscriptions){
        		   BigDecimal qty = prodSubscription.getBigDecimal("quantity");
        		   prodTotalQty = prodTotalQty.add(qty);
        	   }
        	   prodQty.put("productId", indProd);
        	   prodQty.put("quantity", prodTotalQty);
        	   prodQtyList.add(prodQty);
           }
           Map processDispatchReconcilHelperCtx = UtilMisc.toMap("userLogin",userLogin);
     	  	processDispatchReconcilHelperCtx.put("shipmentId", shipmentId);
     	  	processDispatchReconcilHelperCtx.put("routeId", routeId);
     	  	processDispatchReconcilHelperCtx.put("effectiveDate", estimatedDeliveryDate);
     	  	processDispatchReconcilHelperCtx.put("productQtyList", prodQtyList);
     	  	try{
     		  result = dispatcher.runSync("processDispatchReconcilHelper",processDispatchReconcilHelperCtx);
     		
     		  if (ServiceUtil.isError(result)) {
     			  String errMsg =  ServiceUtil.getErrorMessage(result);
     			  Debug.logError(errMsg , module);
     			 shipment.store();
     			 shipment.set("statusId", "GENERATION_FAIL");
                 return result;
     		  }
     		  
     	  }catch (Exception e) {
     		  	Debug.logError(e, "Problem updating ItemIssuance for Route " + routeId, module); 
     		  	shipment.set("statusId", "GENERATION_FAIL");
     		  	try{
     		  		shipment.store();
     		  	}catch (Exception ex) {
					// TODO: handle exception
				}
                return ServiceUtil.returnError(e.toString());
     	  }	
           
           String tempSubId = "";
           String tempTypeId = "";
           String subId;
           String typeId;
           
           generationFailed = false;
          // BigDecimal totalQuantity = BigDecimal.ZERO;
           List<GenericValue> orderSubProdsList = FastList.newInstance();  
           
           for (int j = 0; j < subscriptionProductsList.size(); j++) {
           	subId = subscriptionProductsList.get(j).getString("subscriptionId");
           	typeId = subscriptionProductsList.get(j).getString("productSubscriptionTypeId");
               	if (tempSubId == "") {
               		tempSubId = subId;
               		tempTypeId = typeId;        		
               	}
               	
                   /*condition: "!(typeId.startsWith(tempTypeId))" is to generate same order for CASH_FS and CASH*/
               	if (!tempSubId.equals(subId) || (!tempTypeId.equals(typeId))) {

               		context.put("subscriptionProductsList", orderSubProdsList);
   					context.put("shipmentId" , shipmentId);
   					context.put("excludeInvoiceForFacilityIds", excludeInvoiceForFacilityIds);	
   					result = createSalesOrderSubscriptionProductType(dctx, context);
   					if (ServiceUtil.isError(result)) {
                   		Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
                   		generationFailed = true;
                   		break;
                   	}
               		
               		BigDecimal quantity = (BigDecimal)result.get("quantity");                		
               		/*if (quantity != null) {
               			totalQuantity = totalQuantity.add(quantity);
               		}*/
               		orderCounter++;
               		if ((orderCounter % 10) == 0) {
               			elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
               			Debug.logImportant("Completed " + orderCounter + " orders [ in " + elapsedSeconds + " seconds]", module);
               		}
               		orderSubProdsList.clear();
               		tempSubId = subId;
               		tempTypeId = typeId;
               	}
               	orderSubProdsList.add(subscriptionProductsList.get(j));
           }
           
           if (orderSubProdsList.size() > 0 && !generationFailed) {
   			
   				context.put("subscriptionProductsList", orderSubProdsList);
   				context.put("shipmentId" , shipmentId);
   				context.put("excludeInvoiceForFacilityIds", excludeInvoiceForFacilityIds);
   				result = createSalesOrderSubscriptionProductType(dctx, context); 
   				if (ServiceUtil.isError(result)) {
   	    			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
   	    			generationFailed = true;
   	    		}  

   			/*BigDecimal quantity = (BigDecimal)result.get("quantity");
       		if (quantity != null) {
       			totalQuantity = totalQuantity.add(quantity);
       		}   */ 		
       		orderCounter++;
           }
           
   		
   		if (generationFailed) {
   			/*List<GenericValue> shipmentReceipts;
   	        try {
   	        	shipmentReceipts = shipment.getRelated("ShipmentReceipt");
   	        } catch (GenericEntityException e) {
   	        	Debug.logError("Unable to get ShipmentReceipt"+e, module);
   	    		return ServiceUtil.returnError("Unable to get ShipmentReceipt");
   	        }
   	        for (GenericValue shipmentReceipt : shipmentReceipts) {
   	        	List<GenericValue> inventoryItemDetails;
   		        try {
   		        	String inventoryItemId = shipmentReceipt.getString("inventoryItemId");
   		        	inventoryItemDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS,inventoryItemId),null, null, null, false);
   		        	delegator.removeAll(inventoryItemDetails);
   		        } catch (GenericEntityException e) {
   		        	Debug.logError("Unable to remove inventoryItemDetails"+e, module);
   		    		return ServiceUtil.returnError("Unable to remove inventoryItemDetails");
   		        }
   	        }
   	        
   	        GenericValue inventoryItem = null;
   	        
   	        for (GenericValue shipmentReceipt : shipmentReceipts) {
   				try {
   					inventoryItem = shipmentReceipt.getRelatedOne("InventoryItem");
   					delegator.removeValue(shipmentReceipt);
   					delegator.removeValue(inventoryItem);
   				} catch (GenericEntityException e) {
   					Debug.logError("Unable to remove inventoryItem"+e, module);
   		    		return ServiceUtil.returnError("Unable to remove inventoryItem");
   				}
   	        }*/
   			try {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback();
            } catch (Exception e2) {
                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
            }
   			shipment.set("statusId", "GENERATION_FAIL");
   		}else {
   			shipment.set("statusId", "GENERATED");	
   		}
        try {
           	shipment.store();
        }catch (GenericEntityException e) {
       		Debug.logError(e, "Failed to update shipment status " + shipmentId, module);
       		return ServiceUtil.returnError("Failed to update shipment status " + shipmentId + ": " + e);          	
        }
        if(!generationFailed){
        	try{
            	result = dispatcher.runSync("calculateCratesForShipment", UtilMisc.toMap("userLogin", userLogin, "shipmentId", shipmentId));
      		  	if (ServiceUtil.isError(result)) {
      		  		String errMsg =  ServiceUtil.getErrorMessage(result);
      		  		Debug.logError(errMsg , module);
	      		  	shipment.set("statusId", "GENERATION_FAIL");
	     		  	shipment.store();
	                return result;
      		  	}
      		  	BigDecimal crateQty = (BigDecimal)result.get("totalCrates");
      		  	BigDecimal canQty = (BigDecimal)result.get("totalCans");
      		  	
      		  	List prodQty = FastList.newInstance();
      		  	if(crateQty.compareTo(BigDecimal.ZERO)>0){
    	  		  	Map tempMap = FastMap.newInstance();
    	  		  	tempMap.put("productId", "CRATE");
    	  		  	tempMap.put("quantity", crateQty);
    	  		  	prodQty.add(tempMap);
      		  	}
      		  	if(canQty.compareTo(BigDecimal.ZERO)>0){
	  	  		  	Map tempMap = FastMap.newInstance();
	  	  		  	tempMap.put("productId", "CAN");
	  	  		  	tempMap.put("quantity", canQty);
	  	  		  	prodQty.add(tempMap);
	    		}
      		  	if(UtilValidate.isNotEmpty(prodQty)){
	      		  	Map processItemIssuenceCtx = UtilMisc.toMap("userLogin",userLogin);
		  		  	processItemIssuenceCtx.put("shipmentId", shipmentId);
		  		  	processItemIssuenceCtx.put("routeId", routeId);
		  		  	processItemIssuenceCtx.put("effectiveDate", estimatedDeliveryDate);
		  		  	processItemIssuenceCtx.put("productQtyList", prodQty);
		
		  		  	result = dispatcher.runSync("processDispatchReconcilHelper",processItemIssuenceCtx);
			 		
		  		  	if (ServiceUtil.isError(result)) {
		  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
		  		  		Debug.logError(errMsg , module);
		  		  	}
      		  	}
    	  		
      		  	GenericValue cratesCansAccnt = delegator.findOne("CrateCanAccount", UtilMisc.toMap("shipmentId", shipmentId), false);
      		  	
      		  	if(UtilValidate.isNotEmpty(cratesCansAccnt)){
      		  		delegator.removeValue(cratesCansAccnt);
      		  	}
      		  	
      		  	GenericValue crateCanAcct = null;
      		  	crateCanAcct = delegator.makeValue("CrateCanAccount");
      		  	crateCanAcct.put("shipmentId", shipmentId);
      		  	crateCanAcct.put("cratesSent", crateQty);
      		  	crateCanAcct.put("cratesReceived", BigDecimal.ZERO);
      		  	crateCanAcct.put("cansSent", canQty);
      		  	crateCanAcct.put("cansReceived", BigDecimal.ZERO);
      		  	crateCanAcct.put("createdDate", UtilDateTime.nowTimestamp());
      		  	crateCanAcct.put("createdByUserLogin", userLogin.get("userLoginId"));
      		  	crateCanAcct.create();
    	  		  	
    	  		  	/*List invExpr = FastList.newInstance();
    	  		  	List<GenericValue> productStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, "9004"), null, null, null, false);
    	  		  	productStoreFacility = EntityUtil.filterByDate(productStoreFacility, estimatedDeliveryDate);
    	  		  	String invFacility = "";
    	  		  	if(UtilValidate.isNotEmpty(productStoreFacility)){
    	  		  		invFacility = ((GenericValue)EntityUtil.getFirst(productStoreFacility)).getString("facilityId");
    	  		  	}
    	  		  	invExpr.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "CRATE"));
    	  		  	invExpr.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, invFacility));
    	  		  	EntityCondition cond = EntityCondition.makeCondition(invExpr, EntityOperator.OR);
    	  		  	List<GenericValue> invItemDetail = delegator.findList("InventoryItem", cond, null, null, null, true);
    	  		  	String invItemId = "";
    	  		  	if(UtilValidate.isNotEmpty(invItemDetail)){
    	  		  		invItemId = ((GenericValue)EntityUtil.getFirst(invItemDetail)).getString("inventoryItemId");
    	  		  	}*/
    	  		  	
    	  		  	/*invExpr.clear();
    	  		  	invExpr.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
    	  		  	invExpr.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "CRATE"));
    	  		  	EntityCondition condition1 = EntityCondition.makeCondition(invExpr, EntityOperator.AND);
    	  		  	List<GenericValue> itemIssuance = delegator.findList("ItemIssuance", condition1, UtilMisc.toSet("itemIssuanceId"), null, null ,false);
    	  		  	String itemIssueId = "";
    	  		  	if(UtilValidate.isNotEmpty(itemIssuance)){
    	  		  		itemIssueId = ((GenericValue)EntityUtil.getFirst(itemIssuance)).getString("itemIssuanceId");
    	  		  	}*/
    	  		  	/*Map inventoryItemDetailCtx = FastMap.newInstance();
    	  		  	inventoryItemDetailCtx.put("inventoryItemId", invItemId);
    	  		  	inventoryItemDetailCtx.put("quantityOnHandDiff", crateQty.negate());
    	  		  	inventoryItemDetailCtx.put("availableToPromiseDiff", crateQty.negate());
    	  		  	inventoryItemDetailCtx.put("shipmentId", shipmentId);
    	  		  	inventoryItemDetailCtx.put("itemIssuanceId", itemIssueId);
    	  		  	inventoryItemDetailCtx.put("userLogin", userLogin);
    	  		  	
    	  		  	result = dispatcher.runSync("createInventoryItemDetail", inventoryItemDetailCtx);
    	  		  	
    	  		  	if (ServiceUtil.isError(result)) {
    	  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
    	  		  		Debug.logError(errMsg , module);
    	  		  	}*/
            }catch(Exception e){
         	   	Debug.logError(e, "Failed to calculate Crates For Shipment" + shipmentId, module);
         		shipment.set("statusId", "GENERATION_FAIL");
     		  	try{
     		  		shipment.store();
     		  	}catch (Exception ex) {
					// TODO: handle exception
				}
           		return ServiceUtil.returnError("Failed to calculate Crates For Shipment" + shipmentId + ": " + e);
            }
        }
        
        
       } // shipment list
       
       if(!generationFailed){
    	   elapsedSeconds = UtilDateTime.getInterval(startTimestamp, UtilDateTime.nowTimestamp())/1000;
     	   Debug.logImportant("Completed " + orderCounter + " orders [ in " + elapsedSeconds + " seconds]", module);
       }
      
       return ServiceUtil.returnSuccess();
    }
	
	public static Map<String, Object> createSalesOrderSubscriptionProductType(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String currencyUomId = (String) context.get("defaultOrganizationPartyCurrencyUomId");
        if(UtilValidate.isEmpty(currencyUomId)){
        	currencyUomId ="INR";
        }
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("estimatedDeliveryDate");
        String shipmentId = (String) context.get("shipmentId");
        List excludeInvoiceForFacilityIds = (List) context.get("excludeInvoiceForFacilityIds");
        List<GenericValue> subscriptionProductsList = UtilGenerics.checkList(context.get("subscriptionProductsList"));
        Map<String, Object> resultMap = FastMap.newInstance();
		//BigDecimal quantity = BigDecimal.ZERO;        
        String productStoreId = (String) (getByprodFactoryStore(delegator)).get("factoryStoreId");
        
        String salesChannel = (String) context.get("salesChannel");       
        if (UtilValidate.isEmpty(salesChannel)) {
        	salesChannel = "BYPROD_SALES_CHANNEL";
        }      

        if (UtilValidate.isEmpty(subscriptionProductsList)) {
            //Debug.logInfo("No subscription to create orders, finished", module);
            return resultMap;
        }   
        String productSubscriptionTypeId = subscriptionProductsList.get(0).getString("productSubscriptionTypeId");
        
        String partyId = subscriptionProductsList.get(0).getString("ownerPartyId");
        String facilityId = subscriptionProductsList.get(0).getString("facilityId");
        
       List conditionList = FastList.newInstance();
        
         conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.LIKE, "PM_RC_%"));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		List<GenericValue> partyClassificationList = null;
		GenericValue shipment = null;
		try{
			partyClassificationList = delegator.findList("PartyClassification", condition, null, null, null, false);
			shipment=delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
			
		}catch(GenericEntityException e){
			Debug.logError("No partyRole found for given partyId:"+ partyId, module);
			return ServiceUtil.returnError("No partyRole found for given partyId");
		}
		
		String productPriceTypeId = null;
		
		if (UtilValidate.isNotEmpty(partyClassificationList)) {
			GenericValue partyClassification = partyClassificationList.get(0);
			productPriceTypeId = (String) partyClassification.get("partyClassificationGroupId");
			//productPriceTypeId = productPriceTypeId+"_PRICE";
		}
		conditionList.clear();
		
        ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
        cart.setOrderType("SALES_ORDER");
        cart.setIsEnableAcctg("N");
        cart.setChannelType(salesChannel);
        cart.setProductStoreId(productStoreId);
        cart.setBillToCustomerPartyId(partyId);
        cart.setPlacingCustomerPartyId(partyId);
        cart.setShipToCustomerPartyId(partyId);
        cart.setEndUserCustomerPartyId(partyId);
        cart.setFacilityId(facilityId);
        cart.setEstimatedDeliveryDate(estimatedDeliveryDate);
        cart.setProductSubscriptionTypeId(productSubscriptionTypeId);
        cart.setShipmentId(shipmentId);
        try {
            cart.setUserLogin(userLogin, dispatcher);
        } catch (Exception exc) {
            Debug.logError("Error setting userLogin in the cart: " + exc.getMessage(), module);
            try{
            	 shipment.set("statusId", "GENERATION_FAIL");
      		  	shipment.store();
            }catch (Exception e) {
				// TODO: handle exception
			}
    		return ServiceUtil.returnError("Error setting userLogin in the cart: " + exc.getMessage());          	            
        }      
        Iterator<GenericValue> i = subscriptionProductsList.iterator();
        while (i.hasNext()) {
            GenericValue subscriptionProduct = i.next();
            if (subscriptionProduct != null) {
            	try { 
            		Map<String, Object> priceResult;
                    Map<String, Object> priceContext = FastMap.newInstance();
                    priceContext.put("userLogin", userLogin);   
                    priceContext.put("productStoreId", productStoreId);                    
                    priceContext.put("productId", subscriptionProduct.getString("productId"));
                    priceContext.put("partyId", partyId);
                    priceContext.put("facilityId", subscriptionProduct.getString("facilityId")); 
                    priceContext.put("priceDate", estimatedDeliveryDate);
                    priceContext.put("facilityCategory", subscriptionProduct.getString("categoryTypeEnum"));
                    
            		priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);    
                    if (ServiceUtil.isError(priceResult)) {
                        Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
                		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
                    }
                    BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
                    List taxList = (List)priceResult.get("taxList");
            		List<ShoppingCartItem> tempCartItems =cart.findAllCartItems(subscriptionProduct.getString("productId"));
            		ShoppingCartItem item = null;
            		if(tempCartItems.size() >0){
            			 item = tempCartItems.get(0);
            			 item.setQuantity((item.getQuantity()).add(new BigDecimal(subscriptionProduct.getString("quantity"))), dispatcher, cart, true, false ,false);
                         item.setBasePrice((BigDecimal)priceResult.get("basicPrice"));
                         
                       
                        
            			
            		}else{
            			int itemIndx = cart.addItem(0, ShoppingCartItem.makeItem(Integer.valueOf(0), subscriptionProduct.getString("productId"), null, 
                				new BigDecimal(subscriptionProduct.getString("quantity")), (BigDecimal)priceResult.get("basicPrice"),
                                null, null, null, null, null, null, null,
                                null, null, null, null, null, null, dispatcher,
                                cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE)); 
            			
            			item = cart.findCartItem(itemIndx);
            			
            			
            		}
            		 item.setListPrice(totalPrice);
            		item.setTaxDetails(taxList);
            		// populate tax amount percent here 
            	/*	item.setVatPercent();
                    item.setVatAmount();
                    item.setBedPercent();
                    item.setBedAmount();
                    item.setBedcessPercent();
                    item.setBedcessAmount();
                    item.setBedseccessPercent();
                    item.setBedseccessPercent();*/
					/*GenericValue productDetail = delegator.findOne("Product", UtilMisc.toMap("productId", subscriptionProduct.getString("productId")), false);
					if (productDetail != null) {
						BigDecimal tempQuantity  = subscriptionProduct.getBigDecimal("quantity").multiply(productDetail.getBigDecimal("quantityIncluded"));
						quantity = quantity.add(tempQuantity);
					}*/
            		
            		
            		
            		
            		
                } catch (Exception exc) {
                    Debug.logError("Error adding product with id " + subscriptionProduct.getString("productId") + " to the cart: " + exc.getMessage(), module);
                    try{
                   	 shipment.set("statusId", "GENERATION_FAIL");
             		  	shipment.store();
                   }catch (Exception e) {
       				// TODO: handle exception
       			  }
                    return ServiceUtil.returnError("Error adding product with id " + subscriptionProduct.getString("productId") + " to the cart: " + exc.getMessage());          	            
                }
               
            }  
            // for now comment out the  inventory logic
            
        }      
        cart.setDefaultCheckoutOptions(dispatcher);
        ProductPromoWorker.doPromotions(cart, dispatcher);
        CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart);
        
        
		List<GenericValue> applicableTaxTypes = null;
		try {
        	applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Failed to retrive InventoryItem ", module);
			try{
           	 shipment.set("statusId", "GENERATION_FAIL");
     		  	shipment.store();
           }catch (Exception ex) {
				// TODO: handle exception
			}
    		return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
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
			Debug.logError(e, "Failed to retrive InventoryItem ", module);
			try{
           	 shipment.set("statusId", "GENERATION_FAIL");
     		  	shipment.store();
           }catch (Exception ex) {
				// TODO: handle exception
			}
    		return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
		}
       
        try {
			checkout.calcAndAddTax(prodPriceType);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			Debug.logError(e, "Failed to add tax amount ", module);
			try{
           	 shipment.set("statusId", "GENERATION_FAIL");
     		  	shipment.store();
           }catch (Exception ex) {
        	   return ServiceUtil.returnError("Failed to add tax amount  " + e);
			}
    		return ServiceUtil.returnError("Failed to add tax amount  " + e);
		}
        Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
        String orderId = (String) orderCreateResult.get("orderId");
        // handle employee subsidies here 
        if(productSubscriptionTypeId.equals("EMP_SUBSIDY")){
        	Map empSubdCtx = UtilMisc.toMap("userLogin",userLogin);	  	
        	empSubdCtx.put("orderId", orderId);
   	  	 	  	 
   	  	 	try{
   	  	 		Map result = dispatcher.runSync("adjustEmployeeSubsidyForOrder",empSubdCtx);  		  		 
   	  	 		if (ServiceUtil.isError(result)) {
   	  	 			String errMsg =  ServiceUtil.getErrorMessage(result);
   	  	 			Debug.logError(errMsg , module);
   	  	 		try{
               	 shipment.set("statusId", "GENERATION_FAIL");
         		  	shipment.store();
                }catch (Exception e) {
                	Debug.logError(e , module);
	                return ServiceUtil.returnError(e+" Error While Creating Employee Subsidy Milk  !");
   			    }
   	  	 			return result;
   	  	 		}

   	  	 	}catch (Exception e) {
   	  			  Debug.logError(e, "Error While Creating Employee Subsidy Milk ", module);
   	  			try{
               	 shipment.set("statusId", "GENERATION_FAIL");
         		  	shipment.store();
               }catch (Exception ex) {
            		Debug.logError(ex , module);
	                return ServiceUtil.returnError(ex+" Error while Creating Order !");
   			}
   	  			  return resultMap;			  
   	  	 	}
        }
        // let's handle order rounding here
        try{   
        	    Map roundAdjCtx = UtilMisc.toMap("userLogin",userLogin);	  	
        	    roundAdjCtx.put("orderId", orderId);
	  	 		Map result = dispatcher.runSync("adjustRoundingDiffForOrder",roundAdjCtx);  		  		 
	  	 		if (ServiceUtil.isError(result)) {
	  	 			String errMsg =  ServiceUtil.getErrorMessage(result);
	  	 			Debug.logError(errMsg , module);
	  	 			shipment.set("statusId", "GENERATION_FAIL");
  	      		  	shipment.store();
  	      		  	return ServiceUtil.returnError(errMsg+"==Error While  Rounding Order !");
	  	 			
	  	 		}

	  	 	}catch (Exception e) {
	  			  Debug.logError(e, "Error while Creating Order", module);
	  			 try{
	            	 shipment.set("statusId", "GENERATION_FAIL");
	      		  	shipment.store();
	              }catch (Exception ex) {
	            	  Debug.logError(ex, module);        
	                   return ServiceUtil.returnError(ex.toString());
				  }
	              return ServiceUtil.returnError(e+"==Error While  Rounding Order !");
	  			  //return resultMap;			  
	  	 	}
        // approve the order
        if (UtilValidate.isNotEmpty(orderId)) {
            boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
            
            	try{
            		if(UtilValidate.isEmpty(excludeInvoiceForFacilityIds) || (UtilValidate.isNotEmpty(excludeInvoiceForFacilityIds) && !excludeInvoiceForFacilityIds.contains(facilityId))){
            			resultMap = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"userLogin", userLogin));
                		if (ServiceUtil.isError(resultMap)) {
                            Debug.logError("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(resultMap), module);
                    		return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(resultMap));          	            
                        }
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
    	             }       */ 		
            		// apply invoice if any adavance payments from this  party
    				  			            
    				/*Map<String, Object> resultPaymentApp = dispatcher.runSync("settleInvoiceAndPayments", UtilMisc.<String, Object>toMap("invoiceId", (String)resultMap.get("invoiceId"),"userLogin", userLogin));
    				if (ServiceUtil.isError(resultPaymentApp)) {						  
    	        	   Debug.logError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp), module);			             
    	               return ServiceUtil.returnError("There was an error while  adjusting advance payment" + ServiceUtil.getErrorMessage(resultPaymentApp));  
    		        }	*/			           
    		          
                }catch (Exception e) {
                    Debug.logError(e, module);
                    try{
                   	    shipment.set("statusId", "GENERATION_FAIL");
             		  	shipment.store();
                   }catch (Exception ex) {
                	   Debug.logError(e, module);        
                       return ServiceUtil.returnError(e.toString());
       			}
                 return ServiceUtil.returnError(e.toString()); 
                   
                }
        
            resultMap.put("orderId", orderId);
        }        
        return resultMap;   
    }
	
		
	public static Map<String, Object> cancelByProdShipment(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        Date nowDate=UtilDateTime.nowDate(); 
        String shipmentId = (String) context.get("shipmentId");
        String shipmentTypeId = (String) context.get("shipmentTypeId");
        List conditionList= FastList.newInstance(); 
        List boothOrdersList = FastList.newInstance();
        List boothOrderIdsList = FastList.newInstance();
        List boothInvoiceIdsList = FastList.newInstance();
        GenericValue shipment = null;
       // Date shipDate = nowDate;
        String routeId = (String)context.get("routeId");
        String tripId = (String)context.get("tripId");
        String vehicleId ="";
        List routesList = FastList.newInstance();
        if((UtilValidate.isNotEmpty(routeId)) && (routeId.equalsIgnoreCase("AllRoutes")) ){
        	routesList = (List) getByproductRoutes(delegator).get("routeIdsList");
        }else if(UtilValidate.isNotEmpty(routeId)){
        	routesList.add(routeId);
        }
        
        Timestamp estimatedShipDate = (Timestamp)context.get("estimatedDeliveryDate");
        Timestamp dayBegin = UtilDateTime.getDayStart(estimatedShipDate);
        Timestamp dayEnd = UtilDateTime.getDayEnd(estimatedShipDate);
        List shipmentList = FastList.newInstance();
        if(UtilValidate.isEmpty(shipmentId)  && (UtilValidate.isNotEmpty(routesList))){
        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
        	if(UtilValidate.isNotEmpty(tripId)){
        		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS , tripId));
        	}
        	conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS , shipmentTypeId));
  	        conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.IN ,routesList));
  	        conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
  	        conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
  	        EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
  	     
  	      
  	        try {
	    	    shipmentList = delegator.findList("Shipment", condition, null,null, null, false);
	        }catch (GenericEntityException e) {
	            Debug.logError("Unable to get Shipment record from DataBase"+e, module);
	    	    return ServiceUtil.returnError("Unable to get Shipment record from DataBase "); 
		    } 
        }else{
        	conditionList.clear();
        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS , shipmentId));
        	EntityCondition shipCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	try {
        		shipmentList = delegator.findList("Shipment", shipCondition, null,null, null, false);
	        }catch (GenericEntityException e) {
	            Debug.logError("Unable to get Shipment record from DataBase"+e, module);
	    	    return ServiceUtil.returnError("Unable to get Shipment record from DataBase "); 
	         
		    }
        }
        if(UtilValidate.isEmpty(shipmentList)){
        	Debug.logError("ShipmentId or routeId should be given", module);
     		return ServiceUtil.returnError("ShipmentId or routeId should be given"); 
	    }
        
        boolean enableCancelAfterShipDate = Boolean.FALSE;
		try{
			 GenericValue tenantConfigEnableCancelAfterShipDate = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableCancelAfterShipDate"), false);
			 if (UtilValidate.isNotEmpty(tenantConfigEnableCancelAfterShipDate) && (tenantConfigEnableCancelAfterShipDate.getString("propertyValue")).equals("Y")) {
				 enableCancelAfterShipDate = Boolean.TRUE;
			 }
		} catch (GenericEntityException e) {
			 Debug.logError(e, module);             
		}		
		
        if(!enableCancelAfterShipDate && nowDate.after(dayBegin)){        	
        	Debug.logError("Truck sheet cancel not allowed after shipment date", module);
    		return ServiceUtil.returnError("Truck sheet cancel not allowed after Shipment date"); 
        }
        /*
		 * start removing the itemIssuance
		 */
        List shipListId = FastList.newInstance();
        if(UtilValidate.isNotEmpty(shipmentList)){
        	shipListId = (List)EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", true);
        }
        try{
        	List<GenericValue> itemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipListId), null, null, null, false);
        	if (UtilValidate.isNotEmpty(itemIssuance)) {
				delegator.removeAll(itemIssuance); 
			}
		} catch (GenericEntityException e) {
			Debug.logError("Error cancelling item issuance for shipment", module);
    		return ServiceUtil.returnError("Error cancelling item issuance for shipment");             
		}
		/*
		 * removing the itemIssuance
		 */ 
		/*List invExpr = FastList.newInstance();
		String invFacility = "";
		String invItemId = "";
		try{
			
		  	List<GenericValue> productStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, "9004"), null, null, null, false);
		  	productStoreFacility = EntityUtil.filterByDate(productStoreFacility, dayBegin);
		  	
		  	if(UtilValidate.isNotEmpty(productStoreFacility)){
		  		invFacility = ((GenericValue)EntityUtil.getFirst(productStoreFacility)).getString("facilityId");
		  	}
		  	invExpr.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "CRATE"));
		  	invExpr.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, invFacility));
		  	EntityCondition cond = EntityCondition.makeCondition(invExpr, EntityOperator.OR);
		  	List<GenericValue> invItemDetail = delegator.findList("InventoryItem", cond, null, null, null, true);
		  	
		  	if(UtilValidate.isNotEmpty(invItemDetail)){
		  		invItemId = ((GenericValue)EntityUtil.getFirst(invItemDetail)).getString("inventoryItemId");
		  	}
		}catch(Exception e){
			Debug.logError("Error fetching inventory items for crate"+e, module);
    		return ServiceUtil.returnError("Error fetching inventory items for crate"); 
		}*/
		
	  	
        for(int i = 0; i < shipmentList.size(); i++){
        	
        	shipment = (GenericValue) shipmentList.get(i);
        	shipmentId = (String) shipment.get("shipmentId");
        	vehicleId = (String) shipment.get("vehicleId");
        	 try {
             	List VehicleTripStatus = FastList.newInstance();
     			 if (UtilValidate.isNotEmpty(vehicleId) ) {
     				 VehicleTripStatus.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS, vehicleId));
     				 VehicleTripStatus.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "VEHICLE_RETURNED"));
     				 EntityCondition cond = EntityCondition.makeCondition(VehicleTripStatus, EntityOperator.AND);
     				 List<GenericValue> vehicleStatus = delegator.findList("VehicleTripStatus", cond, null, null, null, true);
     				 if(UtilValidate.isNotEmpty(vehicleStatus)){        	
     			        	Debug.logError("Truck sheet cannot be cancel once Vehicle returns back", module);
     			    		return ServiceUtil.returnError("Truck sheet cannot be cancel once Vehicle returns back"); 
     			     }	
     			 }
     		} catch (GenericEntityException e) {
     			 Debug.logError(e, module);      
     		}
     		try{
     			GenericValue crateCanAcct = delegator.findOne("CrateCanAccount", UtilMisc.toMap("shipmentId", shipmentId), false);
         		if(UtilValidate.isNotEmpty(crateCanAcct)){
         			delegator.removeValue(crateCanAcct);
         		}
     		}catch(GenericEntityException e){
     			Debug.logError(e, module);
     			return ServiceUtil.returnError(e.getMessage()); 
     		}
     		
        	//shipment.set("statusId", "CANCEL_INPROCESS");
        	/*invExpr.clear();
        	invExpr.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, invItemId));
        	invExpr.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
        	
        	try{
        		
        		EntityCondition condExp = EntityCondition.makeCondition(invExpr, EntityOperator.AND);
        		List<GenericValue> inventoryItemDetail = delegator.findList("InventoryItemDetail", condExp, null, null, null, false);
        		
        		BigDecimal qty = BigDecimal.ZERO;
        		if(UtilValidate.isNotEmpty(inventoryItemDetail)){
        			qty = ((GenericValue)EntityUtil.getFirst(inventoryItemDetail)).getBigDecimal("quantityOnHandDiff");
        			
        			Map inventoryItemDetailCtx = FastMap.newInstance();
    	  		  	inventoryItemDetailCtx.put("inventoryItemId", invItemId);
    	  		  	inventoryItemDetailCtx.put("quantityOnHandDiff", qty.negate());
    	  		  	inventoryItemDetailCtx.put("availableToPromiseDiff", qty.negate());
    	  		  	inventoryItemDetailCtx.put("shipmentId", shipmentId);
    	  		  	inventoryItemDetailCtx.put("itemIssuanceId", itemIssueId);
    	  		  	inventoryItemDetailCtx.put("userLogin", userLogin);
    	  		  	
    	  		  	result = dispatcher.runSync("createInventoryItemDetail", inventoryItemDetailCtx);
    	  		  	
    	  		  	if (ServiceUtil.isError(result)) {
    	  		  		String errMsg =  ServiceUtil.getErrorMessage(result);
    	  		  		Debug.logError(errMsg , module);
    	  		  	}
        		}
        		shipment.store();
        	}catch (Exception e) {
    			// TODO: handle exception
        		Debug.logError("Unable to Store Shipment Status"+e, module);
        		return ServiceUtil.returnError("Unable to Store Shipment Status"); 
    		}*/
        	
        	
        }
        
        try{
    		dispatcher.runAsync("cancelByProdShipmentInternal", UtilMisc.toMap("shipmentIds", EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", true),"userLogin", userLogin));    		
        }catch (GenericServiceException e) {
			// TODO: handle exception
    		Debug.logError("Unable to get records from DataBase"+e, module);
    		return ServiceUtil.returnError(e.getMessage()); 
		}  
        
        return ServiceUtil.returnSuccess();
    }  
    public static Map<String, Object> cancelByProdShipmentInternal(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
        Date nowDate=UtilDateTime.nowDate(); 
        List shipmentIds = (List) context.get("shipmentIds");
       
        List boothOrdersList = FastList.newInstance();
        List parlourInvList = FastList.newInstance();
        List boothOrderIdsList = FastList.newInstance();
        List boothInvoiceIdsList = FastList.newInstance();
        List parlourInvoiceIdsList = FastList.newInstance();
        GenericValue shipment = null;
        Date shipDate = nowDate;
        for(int k=0; k<shipmentIds.size(); k++){
        	String shipmentId = (String)shipmentIds.get(k);
        	try{
            	// To change shipment status here we are getting shipment Generic Value
            	shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId", shipmentId), false);
            }catch (GenericEntityException e) {
    			// TODO: handle exception
        		Debug.logError("Unable to get Shipment record from DataBase"+e, module);
        		return ServiceUtil.returnError("Unable to get Shipment record from DataBase "); 
    		}   
            List conditionList= FastList.newInstance(); 
        	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
        	
        	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
        	EntityFindOptions findOptions = new EntityFindOptions();
    		findOptions.setDistinct(true);
    		
    		try{			
        		List boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition,  UtilMisc.toSet("orderId", "productId", "quantity", "estimatedDeliveryDate") , null, null, false);
        		
        		Set orderIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "orderId", true));
        		boothOrderIdsList = new ArrayList(orderIdsSet);
        		//to get all invoices for the shipment 
        		boothOrdersList = delegator.findList("OrderHeaderFacAndItemBillingInv", condition, UtilMisc.toSet("orderId","originFacilityId","invoiceId") , UtilMisc.toList("originFacilityId"), findOptions , false);   
        		Set invoiceIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(boothOrdersList, "invoiceId", true));    		
        	    boothInvoiceIdsList = new ArrayList(invoiceIdsSet);

        	    parlourInvList = delegator.findList("Invoice", condition, null , null, null, false);
        	    Set parlourInvSet = new HashSet(EntityUtil.getFieldListFromEntityList(parlourInvList, "invoiceId", true));
        	    parlourInvoiceIdsList = new ArrayList(parlourInvSet);
        	   
        	    boothInvoiceIdsList.addAll(parlourInvoiceIdsList);
        	    
        	}catch (GenericEntityException e) {
    			// TODO: handle exception
        		Debug.logError("Unable to get records from DataBase"+e, module);
        		return ServiceUtil.returnError("Unable to get records from DataBase "); 
    		} 
        	
        	try{
        		result = dispatcher.runSync("massCancelOrders", UtilMisc.<String, Object>toMap("orderIdList", boothOrderIdsList,"userLogin", userLogin));
    			if (ServiceUtil.isError(result)) {
    	        	   Debug.logError("There was an error while Cancel  the Orders: " + ServiceUtil.getErrorMessage(result), module);	               
    	               return ServiceUtil.returnError("There was an error while Cancel  the Orders: ");  
    	         } 			
        		 result = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", boothInvoiceIdsList, "statusId","INVOICE_CANCELLED","userLogin", userLogin));
        		 
        		 if (ServiceUtil.isError(result)) {
        			 Debug.logError("There was an error while Cancel  the invoices: " + ServiceUtil.getErrorMessage(result), module);	               
    	             return ServiceUtil.returnError("There was an error while Cancel  the invoices: ");   			 
        		 }	        	  
    	        	    		
        	}catch (GenericServiceException e) {
    			// TODO: handle exception
        		Debug.logError("Unable to Cancel bulk orders"+e, module);
        		return ServiceUtil.returnError("Unable to Cancel bulk order");  
    		} 
        	
        	shipment.set("statusId", "SHIPMENT_CANCELLED");
        	try{
        		shipment.store();    		
        	}catch (Exception e) {
    			// TODO: handle exception
        		Debug.logError("Unable to Store Shipment Status"+e, module);
        		return ServiceUtil.returnError("Unable to Store Shipment Status"); 
    		}
        }
        
        return result;
    }
    
    public static Map<String, Object> getByprodFactoryStore(Delegator delegator){
        
    	Map<String, Object> result = FastMap.newInstance(); 
    	String productStoreGroupId = "BYPRODUCTS";
        List<GenericValue> byProdStores =FastList.newInstance();
            try{
           	if(UtilValidate.isNotEmpty(productStoreGroupId)){
           		byProdStores = delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(EntityOperator.AND, "productStoreGroupId", productStoreGroupId), null, null, null, false);
            }
         }catch (GenericEntityException e) {
            	Debug.logError(e, module);
            }
         for (GenericValue byProdStore : byProdStores) {
        	 GenericValue productStore = null;
     		try {
     			productStore = byProdStore.getRelatedOne("ProductStore");
     		} catch (GenericEntityException e) {
     			Debug.logError(e, module); 
     		}
     		if(UtilValidate.isNotEmpty(productStore.getString("isFactoryStore")) && (productStore.getString("isFactoryStore").equals("Y")  )){
     			String productStoreId = productStore.getString("productStoreId");
     			result.put("factoryStore", productStore);
     			result.put("factoryStoreId", productStoreId);
     			continue;
     		}
         }
         
    	return result;
	}
       
    public static Map<String, Object> getByproductRoutes(Delegator delegator) {
    	List<String> routes= FastList.newInstance();
    	List<GenericValue> facilities = null;
    	try {
    		facilities = delegator.findList("Facility", EntityCondition.makeCondition(EntityOperator.AND, "facilityTypeId", "ROUTE"), null, UtilMisc.toList("sequenceNum"), null, false);
            routes = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();        
        result.put("routesList", facilities);
        result.put("routeIdsList", routes);

        return result;
    }
    public static Map<String, Object> getByProdRouteBooths(Delegator delegator,String routeId){
    	return getByProdRouteBooths(delegator, routeId, null, null);
	}
    public static Map<String, Object> getByProdRouteBooths(Delegator delegator,String routeId, Timestamp effectiveDate){
    	return getByProdRouteBooths(delegator, routeId, null, effectiveDate);
	}

    public static Map<String, Object> getByProdRouteBooths(Delegator delegator,String routeId, String boothCategory, Timestamp effectiveDate){
    	List<String> boothIds = FastList.newInstance(); 
    	List<GenericValue> booths = null;
    	
    	if(UtilValidate.isEmpty(effectiveDate)){
    		effectiveDate = UtilDateTime.nowTimestamp();
    	}
    	
    	try {
    		List conditionList= FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("byProdRouteId", EntityOperator.EQUALS , routeId));
			conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
			conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate)));
			if (!UtilValidate.isEmpty(boothCategory)) {
				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS , boothCategory));
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    		booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
    		//booths = EntityUtil.filterByDate(booths, effectiveDate);
    		
            boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);          	
       
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
    	result.put("boothList", booths);
        result.put("boothIdsList", boothIds);
    	
    	return result;
	}
    
     
    
    public static Map<String, Object> getBoothOwnerContactInfo(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		Map contactInfo = FastMap.newInstance();
		try{
			
			GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
			if(UtilValidate.isEmpty(facility)){
				Debug.logError("No Facility Found with id :"+facilityId, module);
				return result;
			}
			String partyId = facility.getString("ownerPartyId");
			contactInfo.put("byProdRouteId",facility.getString("byProdRouteId"));
			String ownerName = PartyHelper.getPartyName(delegator, partyId, false);
			contactInfo.put("name",ownerName); 
			Map<String, Object> getTelParams = FastMap.newInstance();
	        	getTelParams.put("partyId", partyId);
	            getTelParams.put("userLogin", userLogin);                    	
	            Map<String, Object> serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                return ServiceUtil.returnSuccess();
	            } 	            
	            if(!UtilValidate.isEmpty(serviceResult.get("contactNumber"))){
	            	contactInfo.put("contactNumber",(String) serviceResult.get("contactNumber"));        	
	            	
	            }
	            serviceResult = dispatcher.runSync("getPartyPostalAddress", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	 Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
	                return ServiceUtil.returnSuccess();
	            } 	            
	            if(!UtilValidate.isEmpty(serviceResult.get("address1"))){
	            	contactInfo.put("address1",(String) serviceResult.get("address1"));        	
	            	
	            }
	            if(!UtilValidate.isEmpty(serviceResult.get("address2"))){
	            	contactInfo.put("address2",(String) serviceResult.get("address2"));      	
	            	
	            }
	            if(!UtilValidate.isEmpty(serviceResult.get("postalCode"))){
	            	contactInfo.put("postalCode",(String) serviceResult.get("postalCode"));      	
	            	
	            }
			
			
		}catch (Exception e) {
			// TODO: handle exception
			 Debug.logError(e.toString(), module);
			 return ServiceUtil.returnError(e.toString());
		}
		result.put("contactInfo", contactInfo);
		return result;
    }	
    public static Map<String, Object> createByProduct(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        String productId = ((String) context.get("productId")).toUpperCase();
        Timestamp pricesEffectiveDate = UtilDateTime.getDayStart((Timestamp)context.get("pricesEffectiveDate"));
        
        Map<String, Object> createProductMap = FastMap.newInstance();
        createProductMap.put("productId", productId);
        createProductMap.put("productTypeId", ((String) context.get("productTypeId")).toUpperCase());
        createProductMap.put("productName", ((String) context.get("productName")).toUpperCase());
        createProductMap.put("internalName", ((String) context.get("internalName")).toUpperCase());
        createProductMap.put("brandName", ((String)context.get("brandName")).toUpperCase());
        createProductMap.put("description", context.get("description"));
        createProductMap.put("quantityUomId", context.get("quantityUomId"));
        createProductMap.put("quantityIncluded", context.get("quantityIncluded"));
        createProductMap.put("piecesIncluded", context.get("piecesIncluded"));
        createProductMap.put("taxable", context.get("taxable"));
        createProductMap.put("chargeShipping", context.get("chargeShipping"));
        createProductMap.put("autoCreateKeywords", context.get("autoCreateKeywords"));
        createProductMap.put("isVirtual", context.get("isVirtual"));
        createProductMap.put("isVariant", context.get("isVariant"));
        createProductMap.put("userLogin", userLogin);
        Map<String, Object> resultMap = null;
        try {
        	resultMap = dispatcher.runSync("createProduct", createProductMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(resultMap)) {
        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
            return resultMap;
        }
        
        /*****  CREATING BYPROD CATEGORY  *****/
        GenericValue productCategoryMember = null;
        productCategoryMember = delegator.makeValue("ProductCategoryMember");
        productCategoryMember.put("productId", productId);
        productCategoryMember.put("productCategoryId", "BYPROD");
        productCategoryMember.put("fromDate", pricesEffectiveDate);
		try {
			productCategoryMember.create();
        } catch (GenericEntityException e) {
        	Debug.logError(e, module);
			return ServiceUtil.returnError("Trouble in creating 'BYPROD' category member for product: "+productId);
        }
        
        /*****  GETTING ALL EXISTING PRICE TYPES  *****/
        List priceTypeCondList = FastList.newInstance();
        priceTypeCondList.add(EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "PM_RC"));
		EntityCondition priceTypeCond = EntityCondition.makeCondition(priceTypeCondList,EntityOperator.AND);
		
		List<GenericValue> priceTypes;
		try {
			priceTypes = delegator.findList("PartyClassificationGroup", priceTypeCond , UtilMisc.toSet("partyClassificationGroupId"), null, null, false );
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Trouble in getting priceTypes");
		}
        
		List priceTypeList = FastList.newInstance();
		priceTypeList = EntityUtil.getFieldListFromEntityList(priceTypes, "partyClassificationGroupId", true);
        
		SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
		String newFormatDate = newFormat.format(pricesEffectiveDate);
		
		/*****  CREATING PRICES FOR ALL EXISTING PRICE TYPES *****/
        for(int i=0; i<priceTypeList.size(); i++){
        	
        	String priceType = (String) priceTypeList.get(i);
        	BigDecimal price = BigDecimal.ZERO;
        	BigDecimal taxAmount = BigDecimal.ZERO;
        	BigDecimal taxPercentage = BigDecimal.ZERO;
        	
        	
        	Map<String, Object> createPriceMap = FastMap.newInstance();
        	createPriceMap.put("productId", productId);
        	createPriceMap.put("priceType", priceType);
        	createPriceMap.put("currencyUomId", "INR");
        	createPriceMap.put("productStoreGroupId", "_NA_");
        	createPriceMap.put("fromDate", newFormatDate);
        	createPriceMap.put("userLogin", userLogin);
            Map<String, Object> priceResultMap = null;
            try {
            	priceResultMap = dispatcher.runSync("createNewPrice", createPriceMap);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        	
            if (ServiceUtil.isError(resultMap)) {
            	Debug.logError(ServiceUtil.getErrorMessage(priceResultMap), module);
                return resultMap;
            }
        }
        result.put("productId", productId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    public static Map<String, Object> updateByProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
    	
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        String productId = (String) context.get("productId");
        Timestamp fromDate = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
        Timestamp thruDate = null;
        if(UtilValidate.isNotEmpty(context.get("thruDate"))){
        	thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
        }
        	
        String uom = "INR";
        if(UtilValidate.isNotEmpty(context.get("currencyUomId"))){
        	uom = (String) context.get("currencyUomId");
        }
        String productStoreGroupId = "_NA_";
        if(UtilValidate.isNotEmpty(context.get("productStoreGroupId"))){
        	productStoreGroupId = (String) context.get("productStoreGroupId");
        }
        
        String priceType = (String) context.get("productPriceTypeId");
        BigDecimal vatPercentage = (BigDecimal)context.get("VAT_SALE_Rate");
        BigDecimal componentPrice = (BigDecimal)context.get("price");
        
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        try{
        	List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, priceType),EntityOperator.OR,
    				EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, "SALE")));
    		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
    		conditionList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
    		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		List<GenericValue> productPrices = delegator.findList("ProductPrice", cond, null, null, null, false);
    		boolean priceFlag = false;
    		boolean taxFlag = false;
    		for(GenericValue productPrice : productPrices){
    			String purposeId = productPrice.getString("productPricePurposeId");
    			String prodPriceTypeId = productPrice.getString("productPriceTypeId");
    			BigDecimal price = productPrice.getBigDecimal("price");
    			BigDecimal taxPercentage = productPrice.getBigDecimal("taxPercentage");
    			if(prodPriceTypeId.contains("SALE")){
    				if(!(vatPercentage.compareTo(taxPercentage)==0)){
    					taxFlag = true;
    				}
    			}
    			else{
    				if(!(componentPrice.compareTo(price)==0)){
    					priceFlag = true;
    				}
    			}
    		}
    		if(priceFlag || taxFlag){
    			if(fromDate.compareTo(UtilDateTime.getDayStart(nowTimestamp)) == 0){
    				for(GenericValue productPrice : productPrices){
    					BigDecimal taxPercent = productPrice.getBigDecimal("taxPercentage");
    					BigDecimal prodPrice = productPrice.getBigDecimal("price");
    					if(taxFlag && UtilValidate.isEmpty(prodPrice)){
    						productPrice.put("taxPercentage", vatPercentage);
    	    			}
    	    			if(priceFlag && UtilValidate.isEmpty(taxPercent)){
    	    				productPrice.put("price", componentPrice);
    	    			}
    	    			
    	    			productPrice.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
    	    			productPrice.put("lastModifiedDate", UtilDateTime.nowTimestamp());
    	    			productPrice.store();
    	    		}
    			}
    			else{
    				for(GenericValue productPrice : productPrices){
    	    			productPrice.put("thruDate", UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayEnd(nowTimestamp), -1));
    	    			productPrice.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
    	    			productPrice.put("lastModifiedDate", UtilDateTime.nowTimestamp());
    	    			productPrice.store();
    	    		}
    				for(GenericValue createProductPrice : productPrices){
    	    			createProductPrice.put("fromDate", UtilDateTime.getDayStart(nowTimestamp));
    	    			createProductPrice.put("thruDate", null);
    	    			BigDecimal tempTaxPercent = createProductPrice.getBigDecimal("taxPercentage");
    	    			BigDecimal tempPrice = createProductPrice.getBigDecimal("price");
    	    			if(UtilValidate.isNotEmpty(tempTaxPercent)){
    	    				createProductPrice.put("taxPercentage", vatPercentage);
    	    			}
    	    			if(UtilValidate.isNotEmpty(tempPrice)){
    	    				createProductPrice.put("price", componentPrice);
    	    			}
    	    			createProductPrice.put("createdByUserLogin", userLogin.get("userLoginId"));
    	    			createProductPrice.put("createdDate", nowTimestamp);
    	    			createProductPrice.create();
    	    		}
    			}
    		}
        }catch(Exception e){
        	Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        result.put("productId", productId);
        result.put("fromDate", fromDate);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
	public static Map<String, Object> deleteByProductPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();       
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    Map<String, Object> result = new HashMap<String, Object>();
	    Timestamp nowTimeStamp=UtilDateTime.nowTimestamp();
	    String productId = (String) context.get("productId");
	    Timestamp fromDate = (Timestamp) context.get("fromDate");
	    String priceType = (String) context.get("productPriceTypeId");
	    String productStoreGroupId = (String) context.get("productStoreGroupId");
	    String currencyUomId = (String) context.get("currencyUomId");
		
		if(UtilValidate.isNotEmpty(currencyUomId)){
			currencyUomId = "INR";
		}
		if(UtilValidate.isNotEmpty(productStoreGroupId)){
			productStoreGroupId = "_NA_";
		}
		
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, priceType),EntityOperator.OR,
				EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, "SALE")));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
		conditionList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		try{
			List<GenericValue> productPrice = delegator.findList("ProductPrice", cond, null, null, null, false);
			if(UtilValidate.isNotEmpty(productPrice)){
				int rows_deleted = delegator.removeAll(productPrice);
			}
		}catch(GenericEntityException e){
			Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
		}
		result.put("productId", productId);
	    result.put("productPriceTypeId", priceType);
	    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	    return result;
	}


	public static Map<String, Object> getByproductParlours(Delegator delegator) {
	    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
	    	result = getByproductParlours(delegator, null);
	    	return result;
	    }
	    public static Map<String, Object> getByproductParlours(Delegator delegator, Timestamp effectiveDate) {
	    	List<String> parlours= FastList.newInstance();
	    	List<GenericValue> facilities = null;
	    	
	    	if(UtilValidate.isEmpty(effectiveDate)){
	    		effectiveDate = UtilDateTime.nowTimestamp();
	    	}
	    	
	    	try {
	    		List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "PARLOUR"));
				conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null),EntityOperator.OR,
		    			 EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, effectiveDate)));
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    		facilities = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
	            parlours = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("parloursList", facilities);
	        result.put("parlourIdsList", parlours);
	
	        return result;
    }
    
    // This will return All boothsList 
    public static Map<String, Object> getAllByproductBooths(Delegator delegator) {
    	Map<String, Object> result = ServiceUtil.returnSuccess(); 
    	result = getAllByproductBooths(delegator, null);
    	return result;
    }    
	public static Map<String, Object> getAllByproductBooths(Delegator delegator, Timestamp effectiveDate){
	    Map<String, Object> result = FastMap.newInstance(); 
	    List<String> boothIds = FastList.newInstance(); 
    	List<GenericValue> booths = null;
    	
    	if(UtilValidate.isEmpty(effectiveDate)){
    		effectiveDate = UtilDateTime.nowTimestamp();
    	}
    	
		try {
			List conditionList= FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
			conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL , null));
			conditionList.add(EntityCondition.makeCondition("openedDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null),EntityOperator.OR,
		    			 EntityCondition.makeCondition("closedDate", EntityOperator.GREATER_THAN, effectiveDate)));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    		
			booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
            boothIds = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);
	    	
            result.put("boothsList", booths);
	    	result.put("boothsIdsList", boothIds);
		
		} catch (GenericEntityException e) {
	        Debug.logError(e, module);
	    }
		return result;
	}
    

    
    public static Map<String, Object> getPartyFinAccountInfo(DispatchContext dctx, Map context) {
    	
    	Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");
		Map accountInfo = FastMap.newInstance();
		try{
			
			GenericValue party = delegator.findOne("Party",UtilMisc.toMap("partyId",partyId),false);
			if(UtilValidate.isEmpty(party)){
				Debug.logError("No Party Found with id :"+partyId, module);
				return ServiceUtil.returnError("No Party found with Id:"+party.getString("partyId"));
			}
			List<GenericValue> partyFinAccounts = null;
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
			conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    		partyFinAccounts = delegator.findList("FinAccount", condition, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
			if(UtilValidate.isEmpty(partyFinAccounts)){
				Debug.logError("No Financial Accounts available for the Party:"+partyId, module);
				return ServiceUtil.returnSuccess();
			}
			else{
				GenericValue finAccountDetail = EntityUtil.getFirst(partyFinAccounts);
				if(UtilValidate.isNotEmpty(finAccountDetail.getString("finAccountName"))){
					accountInfo.put("finAccountName", finAccountDetail.getString("finAccountName"));        	
	            }
				else{
					accountInfo.put("finAccountName", "");
				}
				if(UtilValidate.isNotEmpty(finAccountDetail.getString("finAccountBranch"))){
					accountInfo.put("finAccountBranch", finAccountDetail.getString("finAccountBranch"));        	
	            }
				else{
					accountInfo.put("finAccountBranch", "");
				}
				accountInfo.put("finAccountId", finAccountDetail.getString("finAccountId"));
				
			}
		}catch (Exception e) {
			// TODO: handle exception
			 Debug.logError(e.toString(), module);
			 return ServiceUtil.returnError(e.toString());
		}
		result.put("accountInfo", accountInfo);
		return result;
    }
		
	public static Map<String, Object> calculateByProductsPrice(Delegator delegator, LocalDispatcher dispatcher, Map<String, ? extends Object> context) {
		    Map<String, Object> result = FastMap.newInstance();
		   
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    String facilityId = (String) context.get("facilityId");
		    String productId = (String) context.get("productId"); 
		    String partyId = (String) context.get("partyId");            
		    String productStoreId = (String) context.get("productStoreId");
		    String shipmentTypeId = (String) context.get("shipmentTypeId");
		    Timestamp priceDate = (Timestamp) context.get("priceDate");
		    String productStoreGroupId = "_NA_";
		    String productPriceTypeId = (String) context.get("productPriceTypeId");
		    String facilityCategory = (String) context.get("facilityCategory");
		    GenericValue product;
		    GenericValue facility;
			try {
				product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
				if(UtilValidate.isEmpty(facilityCategory)){
					facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);
					facilityCategory = facility.getString("categoryTypeEnum");
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, e.toString(), module);
		        return ServiceUtil.returnError(e.toString());
			}
			
			GenericValue productStore;
			try {
				productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
			} catch (GenericEntityException e) {
				Debug.logError(e, e.toString(), module);
		        return ServiceUtil.returnError(e.toString());
			}
			
		    if (UtilValidate.isEmpty(priceDate)) {
		    	priceDate = UtilDateTime.nowTimestamp();
		    }
		    List conditionList = FastList.newInstance();
		    
		    /*if((UtilValidate.isEmpty(partyId)) && (UtilValidate.isEmpty(facilityId)) && (UtilValidate.isEmpty(productPriceTypeId))){
				Debug.logWarning("No 'partyId' or 'facilityId' Found", module);
				return result;
			}*/
		    if(UtilValidate.isEmpty(productPriceTypeId)){
		    	 // lets take DEFAULT_PRICE as default priceType any special priceType for facility Or party, this will override with the special type
		    	  productPriceTypeId = "DEFAULT_PRICE";
		    	  
		    }
		    if (productPriceTypeId.contains("_PRICE")) {
		    	String[] prodPriceSplit = productPriceTypeId.split("_PRICE");
		    	productPriceTypeId = prodPriceSplit[0];
		    }
		    
		    List<GenericValue> applicableTaxTypes = null;
			try {
		    	applicableTaxTypes = delegator.findList("ProductPriceType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"TAX"), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive ProductPriceType ", module);
				return ServiceUtil.returnError("Failed to retrive ProductPriceType " + e);
			}
			List applicableTaxTypeList = EntityUtil.getFieldListFromEntityList(applicableTaxTypes, "productPriceTypeId", true);
			
			List<GenericValue> prodPriceType = null;
		    
		    conditionList.clear();
		    conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"%"));
			conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, applicableTaxTypeList));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, priceDate)));
			
			EntityCondition priceCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		    
		    try {
		    	prodPriceType = delegator.findList("ProductPriceAndType", priceCondition, null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, "Failed to retrive InventoryItem ", module);
				return ServiceUtil.returnError("Failed to retrive InventoryItem " + e);
			}
			String currencyDefaultUomId = (String) context.get("currencyUomId");
		    if (UtilValidate.isEmpty(currencyDefaultUomId)) {
		        currencyDefaultUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "INR");
		    }
		
		    String productPricePurposeId = (String) context.get("productPricePurposeId");
		    if (UtilValidate.isEmpty(productPricePurposeId)) {
		        productPricePurposeId = "COMPONENT_PRICE";
		    }       
		     
		    List<EntityCondition> productPriceEcList = FastList.newInstance();
		    productPriceEcList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		    productPriceEcList.add(EntityCondition.makeCondition("productPricePurposeId", EntityOperator.EQUALS, productPricePurposeId));
		    productPriceEcList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyDefaultUomId));
		    productPriceEcList.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.EQUALS, productStoreGroupId));
		    productPriceEcList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,
	    			 EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, priceDate)));
			
		    EntityCondition productPriceEc = EntityCondition.makeCondition(productPriceEcList, EntityOperator.AND);
		    // for prices, get all ProductPrice entities for this productId and currencyUomId
		    List<GenericValue> productPrices = null;
		    try {
		        productPrices = delegator.findList("ProductPrice", productPriceEc, null, UtilMisc.toList("-fromDate"), null, true);
		    } catch (GenericEntityException e) {
		        Debug.logError(e, "An error occurred while getting the product prices", module);
		    }
		    productPrices = EntityUtil.filterByDate(productPrices, priceDate);
			List<GenericValue> prodPrices = EntityUtil.filterByCondition(productPrices, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, productPriceTypeId+"_%"));
		    if (UtilValidate.isEmpty(prodPrices)) {
		    	return ServiceUtil.returnError("Missing price for '" + productId + "' [" + productPriceTypeId + "]");            	
		    }
		    
		    GenericValue resultPrice;
		    
			resultPrice = EntityUtil.getFirst(prodPrices);
			if (prodPrices != null && prodPrices.size() > 1) {
				if (Debug.infoOn()) Debug.logInfo("There is more than one price with the currencyUomId " + currencyDefaultUomId + " and productId " + productId + ", using the latest found with price: " + resultPrice.getBigDecimal("price"), module);
			}
			
			BigDecimal discountAmount = BigDecimal.ZERO;
		    // lets look for party specific discount rates if any and adjust the discount
		    try {
	    		//GenericValue product = resultPrice.getRelatedOne("Product");
	    		BigDecimal quantityIncluded = product.getBigDecimal("quantityIncluded");        	
	    		Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin,"partyId", partyId);
	    		String rateTypeId;
	    		inputRateAmt.put("rateCurrencyUomId", currencyDefaultUomId);    		
	        	if (UtilValidate.isEmpty(facilityCategory)) {        		
	        		rateTypeId = "VENDOR_DEDUCTION";
	        			
	        	}else {
	        		rateTypeId = facilityCategory + "_MRGN";
	        	}
	        		inputRateAmt.put("rateCurrencyUomId", currencyDefaultUomId);  
	        		inputRateAmt.put("rateTypeId", rateTypeId);
	        		inputRateAmt.put("periodTypeId", "RATE_HOUR");
	        		inputRateAmt.put("fromDate", priceDate);
	        		inputRateAmt.put("productId", productId);
	        		Map<String, Object> serviceResults = dispatcher.runSync("getPartyDiscountAmount", inputRateAmt);
	        		
	        		if (ServiceUtil.isError(serviceResults)) {
	        			Debug.logError( "Unable to determine discount for [" + partyId +"]========="+facilityCategory, module);
	        			//return ServiceUtil.returnError("Unable to determine discount for " + facilityCategory, null, null, serviceResults);
	        		}else if(UtilValidate.isNotEmpty(serviceResults.get("rateAmount"))){
	        				discountAmount = (BigDecimal)serviceResults.get("rateAmount");
	        			      			
	        		}
	        		Debug.logInfo( "PartyId ==========["+partyId+"]=========discountAmount   :"+discountAmount, module);
	        		// since the discounts are per litre, adjust proportionally
	        		//discountAmount = discountAmount.multiply(quantityIncluded);
	        	/*
	        	// Sometimes Vendors are also given discounts i.e. deduction at source. Check for 
	        	// VENDOR_DEDUCTION rate type (NOTE: VENDOR_DEDUCTION will override any other eligible
	        	// discounts from above)
	        	if (facilityCategory.equals("VENDOR")) {
					rateTypeId = "VENDOR_DEDUCTION"; 
					inputRateAmt.put("rateTypeId", rateTypeId);
					inputRateAmt.put("fromDate", priceDate);
					inputRateAmt.put("productId", productId);
	        		Map<String, Object> serviceResults = dispatcher.runSync("getRateAmount", inputRateAmt);
	        		if (ServiceUtil.isError(serviceResults)) {
	        			Debug.logError( "Unable to determine deduction for [" + partyId +"]========="+facilityCategory, module);
	        			return ServiceUtil.returnError("Unable to determine deduction for " + facilityCategory, null, null, serviceResults);
	        		}     
	        		discountAmount = (BigDecimal)serviceResults.get("rateAmount");
	        		// since the discounts are per litre, adjust proportionally
	        		discountAmount = discountAmount.multiply(quantityIncluded);				
	        	}*/
	        }catch (GenericServiceException e) {
    			Debug.logError(e, "Unable to get margin/discount: " + e.getMessage(), module);
    	        return ServiceUtil.returnError("Unable to get margin/discount: " + e.getMessage());
	        }
	        BigDecimal basicPrice = resultPrice.getBigDecimal("price").subtract(discountAmount);
			List<GenericValue> taxList = TaxAuthorityServices.getTaxAdjustmentByType(delegator, product, productStore, null, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, null, prodPriceType);
			List taxDetailList = FastList.newInstance();
			BigDecimal totalExciseDuty = BigDecimal.ZERO;
			BigDecimal totalTaxAmt = BigDecimal.ZERO;
			
			for (GenericValue taxItem : taxList) {
				
				String taxType = (String) taxItem.get("orderAdjustmentTypeId");
				BigDecimal amount = BigDecimal.ZERO;
				BigDecimal percentage = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(taxItem.get("amount"))){
					amount = (BigDecimal) taxItem.get("amount");
				}
				if(UtilValidate.isNotEmpty(taxItem.get("sourcePercentage")) && amount.compareTo(BigDecimal.ZERO)== 0){
					percentage = (BigDecimal) taxItem.get("sourcePercentage");
					if(UtilValidate.isNotEmpty(percentage) && UtilValidate.isNotEmpty(basicPrice)){
						amount = (basicPrice.multiply(percentage)).divide(new BigDecimal(100));
					}
				}
				
				if(!taxType.equals("VAT_SALE")){
					totalExciseDuty = totalExciseDuty.add(amount);
				}
				
				totalTaxAmt = totalTaxAmt.add(amount);
				
				Map taxDetailMap = FastMap.newInstance();
				
				taxDetailMap.put("taxType", taxType);
				taxDetailMap.put("amount", amount);
				taxDetailMap.put("percentage", percentage);
			
				Map tempDetailMap = FastMap.newInstance();
				tempDetailMap.putAll(taxDetailMap);
				
				taxDetailList.add(tempDetailMap);
			}
		    
		    BigDecimal price = basicPrice.add(totalExciseDuty);
		    BigDecimal totalPrice = basicPrice.add(totalTaxAmt);
		    
		    result.put("basicPrice", basicPrice);
		    result.put("price", price);
		    result.put("totalPrice", totalPrice);
		    result.put("taxList", taxDetailList);
		    return result;
	}
	
	public static Map<String, Object> getPartyDiscountAmount(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		if (UtilValidate.isEmpty(fromDate)) {
			fromDate = UtilDateTime.nowTimestamp();
	    }
		
		Timestamp dayStart = UtilDateTime.getDayStart(fromDate, TimeZone.getDefault(), locale);
		
		String partyId = (String) context.get("partyId");
		String productId = (String) context.get("productId");
		String periodTypeId = (String) context.get("periodTypeId");
		String rateTypeId = (String) context.get("rateTypeId");
		String rateCurrencyUomId = (String) context.get("rateCurrencyUomId");
		GenericValue rateAmountEntry =null;
		
		List<GenericValue> amountList = FastList.newInstance();
		BigDecimal rateAmount = BigDecimal.ZERO;
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
			conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
			conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List<GenericValue> allParamRateAmount = delegator.findList("RateAmount", condition , null, null, null, false);
			amountList = EntityUtil.filterByDate(allParamRateAmount, dayStart);
			if(UtilValidate.isEmpty(amountList)){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
				conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
				conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
				EntityCondition partyCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> partyRateAmount = delegator.findList("RateAmount", partyCond, null, null, null, false);
				amountList = EntityUtil.filterByDate(partyRateAmount, dayStart);
				if(UtilValidate.isEmpty(amountList)){
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "_NA_"));
					conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
					conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
					conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
					EntityCondition productCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					List<GenericValue> prodcuRateAmount = delegator.findList("RateAmount", productCond, null, null, null, false);
					amountList = EntityUtil.filterByDate(prodcuRateAmount, dayStart);
					if(UtilValidate.isEmpty(amountList)){
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "_NA_"));
						conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
						conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
						conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
						EntityCondition rateCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
						List<GenericValue> rateTypeRateAmount = delegator.findList("RateAmount", rateCond, null, null, null, true);
						amountList = EntityUtil.filterByDate(rateTypeRateAmount, dayStart);
					}
				}
			}
			
			if(UtilValidate.isNotEmpty(amountList)){
				rateAmountEntry = EntityUtil.getFirst(amountList);
				rateAmount = rateAmountEntry.getBigDecimal("rateAmount");
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("rateAmount", rateAmount);
		result.put("rateAmountEntry", rateAmountEntry);
		return result;
	}
	
	public static Map<String, Object> cancelReturn(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String returnId = (String) context.get("returnId");
		String shipmentId = "";
		Timestamp shipDate = null;
		BigDecimal returnQuantity = BigDecimal.ZERO;
		String facilityId = "";
		try{
			GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
			returnHeader.set("statusId", "RETURN_CANCELLED");
			returnHeader.store();
			shipmentId = returnHeader.getString("shipmentId");
			facilityId = returnHeader.getString("originFacilityId");
			List<GenericValue> returnItems = delegator.findByAnd("ReturnItem", UtilMisc.toMap("returnId", returnId));
			returnQuantity = ((GenericValue)EntityUtil.getFirst(returnItems)).getBigDecimal("returnQuantity");
		}catch(GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error fetching return header" + e);
		}
		try{
			Map inputMap = FastMap.newInstance();
			inputMap.put("userLogin", userLogin);
			inputMap.put("returnId", returnId);
			result = dispatcher.runSync("cancelReturnItems", inputMap);
			if(ServiceUtil.isError(result)){
				Debug.logError("Error in service cancelReturnItem", module);
				return ServiceUtil.returnError("Error while cancelling cancelReturnItem");
			}
			GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
			if(UtilValidate.isNotEmpty(shipment)){
				shipDate = shipment.getTimestamp("estimatedShipDate");
				Timestamp dayBegin = UtilDateTime.getDayStart(shipDate);
    			Timestamp dayEnd = UtilDateTime.getDayEnd(shipDate);
				List condList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    			condList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "CREDITNOTE_PAYIN"));
    			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID")));
    			condList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
    			condList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
    			EntityCondition exprCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
    			List<GenericValue> payments = delegator.findList("Payment", exprCond, null, null, null, false);
    			if(UtilValidate.isNotEmpty(payments)){
    				GenericValue payment = EntityUtil.getFirst(payments);
    				Map resultPayMap = dispatcher.runSync("voidPayment", UtilMisc.toMap("paymentId", payment.getString("paymentId"), "userLogin", userLogin));
    				if (ServiceUtil.isError(resultPayMap)) {
    					Debug.logError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap), module);
	                    return ServiceUtil.returnError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap));          	            
	                }
    			}
			}
			
		}catch(Exception e){
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error while calling serivce cancelReturnItem" + e);
		}
		
		result = ServiceUtil.returnSuccess("Successfully cancelled returned items with Id:"+returnId);
		return result;
	}
	
	public static String makeTransporterPayment(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	      Timestamp todayDayStart = UtilDateTime.getDayStart(nowTimeStamp);
	  	  List custTimePeriodList = FastList.newInstance();  	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String paidAmountStr = (String) request.getParameter("paidAmt");
	  	  String shortAmountStr = (String) request.getParameter("shortAmt");
	  	  BigDecimal paidAmount = BigDecimal.ZERO;
	  	  BigDecimal shortAmount = BigDecimal.ZERO;
	  	  if(UtilValidate.isNotEmpty(paidAmountStr)){
			  paidAmount = new BigDecimal(paidAmountStr);
		  }
	  	  if(UtilValidate.isNotEmpty(shortAmountStr)){
			  shortAmount = new BigDecimal(shortAmountStr);
		  } 
	  	  
	  	  String partyCode = (String) request.getParameter("partyCode");
	  	  boolean beganTransaction = false;
	  	  try{
	  		  beganTransaction = TransactionUtil.begin();
	  		  BigDecimal dealerGrandPayment = BigDecimal.ZERO;
		  	  for (int i = 0; i < rowCount; i++){
		  		  
		  		  String facilityId = "";
		  		  String paymentMethodTypeId = "";
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  BigDecimal amount = BigDecimal.ZERO;
		  		  String amountStr = "";
		  		  BigDecimal pastAmount = BigDecimal.ZERO;
		  		  String pastAmountStr = "";
		  		  if (paramMap.containsKey("facilityId" + thisSuffix)) {
		  			  facilityId = (String) paramMap.get("facilityId" + thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("paymentMethodTypeId" + thisSuffix)) {
		  			paymentMethodTypeId = (String) paramMap.get("paymentMethodTypeId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("amount" + thisSuffix)) {
		  			amountStr = (String) paramMap.get("amount"+thisSuffix);
		  		  }
		  		  if(UtilValidate.isNotEmpty(amountStr)){
		  			  try {
			  			  amount = new BigDecimal(amountStr);
			  		  } catch (Exception e) {
			  			  Debug.logError(e, "Problems parsing amount string: " + amountStr, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing amount string: " + amountStr);
			  			  TransactionUtil.rollback();
			  			  return "error";
			  		  }
		  		  }
		  		  if (paramMap.containsKey("pastAmount" + thisSuffix)) {
		  			pastAmountStr = (String) paramMap.get("pastAmount"+thisSuffix);
		  		  }
		  		  if(UtilValidate.isNotEmpty(pastAmountStr)){
		  				pastAmount = new BigDecimal(pastAmountStr);
			  		  
		  		  }
		  		  
		  		  BigDecimal totalAmount = amount.add(pastAmount);
		  		  dealerGrandPayment = dealerGrandPayment.add(totalAmount);
		  		  if(totalAmount.compareTo(BigDecimal.ZERO)>0){
		  			  
		  			  List invoiceIdList = FastList.newInstance();
		  			  String statusId = "PMNT_RECEIVED";            
		  			  String paymentTypeId = "SALES_PAYIN";
		  			  String partyIdFrom = "";
		  			  if(UtilValidate.isEmpty(partyIdFrom)){
		  				  GenericValue partyFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false );
		  				  if(UtilValidate.isNotEmpty(partyFacility)){
		  					partyIdFrom = partyFacility.getString("ownerPartyId");
		  				  }
		  			  }
		  		   
		  			  Map resultMap = FastMap.newInstance();
		  			  List statusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_PAID","INVOICE_WRITEOFF");
		  			  List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom));
		  			  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, statusList));
		  			  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		  			  List invoices = delegator.findList("Invoice", condition, UtilMisc.toSet("invoiceId"), UtilMisc.toList("-dueDate"), null, false);
		  	          invoices = EntityUtil.getFieldListFromEntityList(invoices,"invoiceId",true);
		  	          Map input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyIdFrom, "facilityId", facilityId, "paymentPurposeType", null, "organizationPartyId", "Company", "effectiveDate", todayDayStart, "paymentDate", nowTimeStamp, "paymentTypeId", paymentTypeId, "paymentMethodTypeId", paymentMethodTypeId, "statusId", statusId, "paymentRefNum", null, "issuingAuthority", null, "issuingAuthorityBranch", null, "amount", totalAmount, "invoices", invoices);
		  	          String paymentId = null;
		  	          resultMap = dispatcher.runSync("createPaymentAndApplicationForInvoices", input);
		  	          if (ServiceUtil.isError(resultMap)) {
		  	        	  Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Payment Unsuccessful for Dealer: " +facilityId);
			  			  TransactionUtil.rollback();
			  			  return "error";
		  	          }
		  	          paymentId = (String) resultMap.get("paymentId");
		  	          Debug.log("payment created for facility =="+facilityId+"-->"+paymentId);
		  		  }
		  	 }//end of loop
		  	 GenericValue routeFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",partyCode),false);
		  	 if(UtilValidate.isNotEmpty(routeFacility)){
		  		 String boothType = routeFacility.getString("facilityTypeId");
		  		 if(boothType.equals("ROUTE")){
		  			 if(shortAmount.compareTo(BigDecimal.ZERO) > 0){
		  				
		  				Map inputCtx = UtilMisc.toMap("userLogin", userLogin, "supplyDate", todayDayStart, "facilityId", partyCode, "amount", shortAmount);
			  	        String invoiceId = null;
			  	        result = dispatcher.runSync("CreateTransporterDue", inputCtx);
			  	        if (ServiceUtil.isError(result)) {
			  	          Debug.logError(ServiceUtil.getErrorMessage(result), module);
				  		  request.setAttribute("_ERROR_MESSAGE_", "Transporter short payment invoice unsuccessful: "+partyCode);
				  		  TransactionUtil.rollback();
				  		  return "error";
			  	        }
			  	        invoiceId = (String) result.get("invoiceId");
			  	        Debug.log("short payment invoice =="+partyCode+"-->"+invoiceId);
		  			 }
		  			 
		  			 if(paidAmount.compareTo(dealerGrandPayment)>0){
		  				
		  				Map inputMap = UtilMisc.toMap("userLogin", userLogin, "facilityId", partyCode, "amount", paidAmount.subtract(dealerGrandPayment));
			  	        String paymentId = null;
			  	        result = dispatcher.runSync("createTransporterDuePayment", inputMap);
			  	        if (ServiceUtil.isError(result)) {
			  	          Debug.logError(ServiceUtil.getErrorMessage(result), module);
				  		  request.setAttribute("_ERROR_MESSAGE_", "Transporter payment unsuccessful: "+partyCode);
				  		  TransactionUtil.rollback();
				  		  return "error";
			  	        }
			  	        paymentId = (String) result.get("paymentId");
			  	        Debug.log("transporter payment =="+partyCode+"-->"+paymentId);
			  	        
		  			 }
		  		 }
		  	 }
	  	  } catch (GenericEntityException e) {
	  		  try {
	  			  // only rollback the transaction if we started one...
	  			  TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
	  		  } catch (GenericEntityException e2) {
	  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		  }
	  		  Debug.logError("An entity engine error occurred while fetching data", module);
	  	  }
	  	  catch (GenericServiceException e) {
	  		  try {
	  			  // only rollback the transaction if we started one...
	  			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
	  		  } catch (GenericEntityException e2) {
	  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  		  }
	  		  Debug.logError("An entity engine error occurred while calling services", module);
	  	  }
	  	  finally {
	  		  // only commit the transaction if we started one... this will throw an exception if it fails
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  		  }
	  	  }
	  	  request.setAttribute("_EVENT_MESSAGE_", "Successfully made entry from party: "+partyCode);
	  	  return "success";     
	}
	
	public static String processDSCorrectionMIS(HttpServletRequest request, HttpServletResponse response) {
  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  	  Locale locale = UtilHttp.getLocale(request);
  	  String boothId = (String) request.getParameter("boothId");
  	  String tripId = (String) request.getParameter("tripId");
  	  String routeId = (String) request.getParameter("routeId");
  	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
  	  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
  	  String subscriptionTypeId = (String) request.getParameter("subscriptionTypeId");
  	  String shipmentTypeId = "AM_SHIPMENT";
  	  if(UtilValidate.isNotEmpty(request.getParameter("shipmentTypeId"))){
  		  shipmentTypeId = (String) request.getParameter("shipmentTypeId");
  	  }
  	  String productId = null;
  	  String quantityStr = null;
  	  String sequenceNum = null;	  
  	  Timestamp effectiveDate=null;
  	  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  	  BigDecimal quantity = BigDecimal.ZERO;
  	  List<GenericValue> subscriptionList=FastList.newInstance();
  	  Map<String, Object> result = ServiceUtil.returnSuccess();
  	  HttpSession session = request.getSession();
  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
  	  GenericValue subscription = null;
  	  GenericValue facility = null;
  	  List custTimePeriodList = FastList.newInstance(); 
  	  List conditionList =FastList.newInstance();
  	  String shipmentId = "";
  	  String orderId = "";
  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
  		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
  		  try {
  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
  		  } catch (ParseException e) {
  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
              // effectiveDate = UtilDateTime.nowTimestamp();
  		  } catch (NullPointerException e) {
  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
               //effectiveDate = UtilDateTime.nowTimestamp();
  		  }
  	  }
  	  if (boothId == "") {
  			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
  			return "error";
  		}
      
      // Get the parameters as a MAP, remove the productId and quantity params.
  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
  	  if (rowCount < 1) {
  		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
  		  return "success";
  	  }
  	  try{
  		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
  		  if(UtilValidate.isEmpty(facility)){
  			  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
  			  return "error";
  		  }
  		  //lets override productSubscriptionTypeId based on facility category
  		  if(facility.getString("categoryTypeEnum").equals("SO_INST")){
  			  productSubscriptionTypeId = "SPECIAL_ORDER";
  		  }else if(facility.getString("categoryTypeEnum").equals("CR_INST")){
  			 productSubscriptionTypeId = "CREDIT";
 		  }
  	  }catch (GenericEntityException e) {
  		  Debug.logError(e, "Booth does not exist", module);
  		  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
  		  return "error";
  	  }
  	  try {
  		  
  		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
  		  conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, subscriptionTypeId+"_SHIPMENT"));
  		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.EQUALS, effectiveDate));
  		  conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
  		  if(UtilValidate.isNotEmpty(tripId)){
  			  conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
  		  }
  		  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
  		  List<GenericValue> shipments = delegator.findList("Shipment", condition, UtilMisc.toSet("shipmentId"), null, null, false);
  		  if(UtilValidate.isEmpty(shipments)){
  			  Debug.logError("No shipment found for the dealer "+boothId, module);
      		  request.setAttribute("_ERROR_MESSAGE_", "No shipment found for the dealer "+boothId);
      		  return "error";
  		  }
  		  shipmentId = (EntityUtil.getFirst(shipments)).getString("shipmentId");
	    		
  		  conditionList.clear();
  		  conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
  		  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
  		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
  		  EntityCondition orderCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
  		  List<GenericValue> orderHeader = delegator.findList("OrderHeader", orderCond, null, null, null, false);
  		  if(UtilValidate.isEmpty(orderHeader)){
  			  Debug.logError("No Order found for dealer  "+boothId, module);
  			  request.setAttribute("_ERROR_MESSAGE_", "No Order found for dealer  "+boothId);
  			  return "error";     		
  		  }
  		  orderId = (EntityUtil.getFirst(orderHeader)).getString("orderId");
  	  }  catch (GenericEntityException e) {
  		  Debug.logError(e, "Problem fetching data from Entity", module);
  		  request.setAttribute("_ERROR_MESSAGE_", "Problem fetching data from Entity");
  		  return "error";	
  	  }
  	
  	  List<Map>productQtyList =FastList.newInstance();
  	  for (int i = 0; i < rowCount; i++) {
  		  Map<String  ,Object> productQtyMap = FastMap.newInstance();
  		  
  		  List<GenericValue> subscriptionProductsList = FastList.newInstance();
  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
  		  if (paramMap.containsKey("productId" + thisSuffix)) {
  			  productId = (String) paramMap.get("productId" + thisSuffix);
  		  }
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
  			  return "error";			  
  		  }
  		  if (paramMap.containsKey("sequenceNum" + thisSuffix)) {
  			  sequenceNum = (String) paramMap.get("sequenceNum" + thisSuffix);
  		  }	
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing sequence number");
  			  return "error";			  
  		  }		  
  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
  			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
  		  }
  		  else {
  			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
  			  return "error";			  
  		  }		  
  		  if (quantityStr.equals("")) {
  			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
  			  return "error";	
  		  }
  		  try {
  			  quantity = new BigDecimal(quantityStr);
  		  } catch (Exception e) {
  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
  			  return "error";
  		  } 
  		  
  		  productQtyMap.put("productId", productId);
  		  productQtyMap.put("quantity", quantity);
  		  productQtyMap.put("sequenceNum", sequenceNum);
  		  productQtyList.add(productQtyMap);
  	 }//end row count for loop
  
  	 Map processDSCorrectionHelperCtx = UtilMisc.toMap("userLogin",userLogin);
  	 processDSCorrectionHelperCtx.put("orderId", orderId);
  	 processDSCorrectionHelperCtx.put("shipmentId", shipmentId);
  	 processDSCorrectionHelperCtx.put("boothId", boothId);
  	 processDSCorrectionHelperCtx.put("shipmentTypeId", shipmentTypeId);
  	 processDSCorrectionHelperCtx.put("effectiveDate", effectiveDate);
  	 processDSCorrectionHelperCtx.put("productQtyList", productQtyList);
  	 processDSCorrectionHelperCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
  	 String indentChanged = "";
  	 try{
  		 result = dispatcher.runSync("processDSCorrectionHelper",processDSCorrectionHelperCtx);
  		 
  		 if (ServiceUtil.isError(result)) {
  			 String errMsg =  ServiceUtil.getErrorMessage(result);
  			 Debug.logError(errMsg , module);
  			 request.setAttribute("_ERROR_MESSAGE_",errMsg);
  			 return "error";
  		 }
  		 indentChanged = (String)result.get("indentChangeFlag");
		 
  	 }catch (Exception e) {
  		 Debug.logError(e, "Problem updating order for dealer " + boothId, module);     
  		 request.setAttribute("_ERROR_MESSAGE_", "Problem updating order for dealer " + boothId);
  		 return "error";			  
  	 }	 
  	 request.setAttribute("indentChangeFlag", indentChanged);
  	 return "success";     
	}
	
	public static String processReturnItemsMIS(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  String routeId = (String) request.getParameter("routeId");
	  	  String tripId = (String) request.getParameter("tripId");
	  	  String boothId = (String) request.getParameter("boothId");
	  	  String returnHeaderTypeId = (String) request.getParameter("returnHeaderTypeId");
	  	  String receiveInventory = (String) request.getParameter("receiveInventory");
	  	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
	  	  String productId = null;
	  	  String quantityStr = null;
	  	  String sequenceNum = null;	  
	  	  Timestamp effectiveDate=null;
	  	  BigDecimal quantity = BigDecimal.ZERO;
	  	  List conditionList =FastList.newInstance();
	  	  
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	  	  GenericValue route = null;
	  	  GenericValue dealer = null;
	  	  List custTimePeriodList = FastList.newInstance();
	  	  String shipmentId = "";
	  	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
	  		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");             
	  		  try {
	  			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	  		  }
	  	  }
	  	  if (UtilValidate.isEmpty(routeId)) {
	  		request.setAttribute("_ERROR_MESSAGE_","Route Id is empty");
	  		return "error";
	  	  }
	      
	  	  
	      // Get the parameters as a MAP, remove the productId and quantity params.
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	  		  return "success";
	  	  }
	  	  try{
	  		  route=delegator.findOne("Facility", UtilMisc.toMap("facilityId",routeId), false);
	  		  if(UtilValidate.isEmpty(route)){
	  			  request.setAttribute("_ERROR_MESSAGE_", "Route"+" '"+routeId+"'"+" does not exist");
	  			  return "error";
	  		  }
	  		  dealer = delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
  			  if(UtilValidate.isEmpty(dealer)){
  				  request.setAttribute("_ERROR_MESSAGE_", "Dealer"+" '"+boothId+"'"+" does not exist");
  				  return "error";
  			  }
			  
	  	  }catch (GenericEntityException e) {
	  		  Debug.logError(e, module);
	  		  request.setAttribute("_ERROR_MESSAGE_", "Error while fetching facility details");
	  		  return "error";
	  	  }
	  	  
	  	  try {
	  		  conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
	  		  if(UtilValidate.isNotEmpty(tripId)){
	  			  conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	  		  }
	  		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	  		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(effectiveDate)));
	  		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(effectiveDate)));
			  EntityCondition shipCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
			  List<GenericValue> shipmentList = delegator.findList("Shipment", shipCond, UtilMisc.toSet("shipmentId"), null, null, false);
	  		  if(UtilValidate.isEmpty(shipmentList)){
	  			  request.setAttribute("_ERROR_MESSAGE_", "This Route has no shipments for the day");
	  			  return "error";     		
	  		  }
	  		  shipmentId = (String)((GenericValue)EntityUtil.getFirst(shipmentList)).get("shipmentId");
	  	  }catch (GenericEntityException e) {
	  		  Debug.logError(e, module);
	  		  request.setAttribute("_ERROR_MESSAGE_", "Error fetching shipment details");
	  		  return "error";
	  	  }
	  	
	  	  List<Map>productQtyList =FastList.newInstance();
	  	  for (int i = 0; i < rowCount; i++) {
	  		  Map<String  ,Object> productQtyMap = FastMap.newInstance();
	  		  
	  		  List<GenericValue> subscriptionProductsList = FastList.newInstance();
	  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	  		  if (paramMap.containsKey("productId" + thisSuffix)) {
	  			  productId = (String) paramMap.get("productId" + thisSuffix);
	  		  }
	  		  else {
	  			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
	  			  return "error";			  
	  		  }
	  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
	  			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
	  		  }
	  		  else {
	  			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
	  			  return "error";			  
	  		  }		  
	  		  if (quantityStr.equals("")) {
	  			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
	  			  return "error";	
	  		  }
	  		  try {
	  			  quantity = new BigDecimal(quantityStr);
	  		  } catch (Exception e) {
	  			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
	  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
	  			  return "error";
	  		  } 
	  		  
	  		  productQtyMap.put("productId", productId);
	  		  productQtyMap.put("quantity", quantity);
	  		  productQtyList.add(productQtyMap);
	  	  }//end row count for loop
	  	  
	  	  Map processReturnHelperCtx = UtilMisc.toMap("userLogin",userLogin);
	  	  processReturnHelperCtx.put("shipmentId", shipmentId);
	  	  processReturnHelperCtx.put("routeId", routeId);
	  	  processReturnHelperCtx.put("effectiveDate", effectiveDate);
	  	  processReturnHelperCtx.put("productQtyList", productQtyList);
	  	  processReturnHelperCtx.put("boothId", boothId);
		  processReturnHelperCtx.put("returnHeaderTypeId", returnHeaderTypeId);
		  processReturnHelperCtx.put("receiveInventory", receiveInventory);
	  	  try{
	  		  result = dispatcher.runSync("processReturnItemsHelper",processReturnHelperCtx);
	  		  if (ServiceUtil.isError(result)) {
	  			  String errMsg =  ServiceUtil.getErrorMessage(result);
	  			  Debug.logError(errMsg , module);
	  			  request.setAttribute("_ERROR_MESSAGE_",errMsg);
	  			  return "error";
	  		  }
	  	  }catch (Exception e) {
	  		  	Debug.logError(e, "Problem calling processReturnItemsHelper for " + routeId+" and "+boothId, module);     
	  	  		request.setAttribute("_ERROR_MESSAGE_", "Problem calling processReturnItemsHelper for " + routeId+" and "+boothId);
	  	  		return "error";			  
	  	  }	 
	  	  return "success";     
	    }
	    
	    public static Map<String ,Object> processReturnItemsHelper(DispatchContext dctx, Map<String, ? extends Object> context){
			  Delegator delegator = dctx.getDelegator();
		      LocalDispatcher dispatcher = dctx.getDispatcher();       
		      GenericValue userLogin = (GenericValue) context.get("userLogin");
		      Map<String, Object> result = ServiceUtil.returnSuccess();
		      String shipmentId = (String)context.get("shipmentId");
		      String routeId = (String)context.get("routeId");
		      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
		      List<Map> productQtyList = (List)context.get("productQtyList");
		      Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		      String boothId = (String) context.get("boothId");
		  	  String returnHeaderTypeId = (String) context.get("returnHeaderTypeId");
		  	  String receiveInventory = (String) context.get("receiveInventory");
		  	  GenericValue dealer = null;
		  	  GenericValue route = null;
		  	  String ownerPartyId = "";
		  	  String returnId = "";
		  	  String toPartyId = "";
		  	  Map prodQtyMap = FastMap.newInstance();
		  	  BigDecimal crateQty = BigDecimal.ZERO;
		  	  String productStoreId = "";
		      try{
		    	  productStoreId = (String)ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
		    	  
		    	  route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
	    		  if(UtilValidate.isEmpty(route)){
	    			  Debug.logError("Route does not exists with Id: " + routeId, module);		  
	    			  return ServiceUtil.returnError("route does not exists with Id: " + routeId);
	    		  }
		    		  
	    		  dealer = delegator.findOne("Facility", UtilMisc.toMap("facilityId", boothId), false);
	    		  if(UtilValidate.isEmpty(dealer)){
	    			  Debug.logError("dealer does not exists with Id: " + boothId, module);		  
	    			  return ServiceUtil.returnError("dealer does not exists with Id: " + boothId);
	    		  }
	    		  ownerPartyId = dealer.getString("ownerPartyId");
		    	  
		    	  
		    	  
		    	  List conditionList = FastList.newInstance();
		    	  conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
		    	  conditionList.add(EntityCondition.makeCondition("returnHeaderTypeId", EntityOperator.EQUALS, returnHeaderTypeId));
		    	  conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, ownerPartyId));
		    	  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("RETURN_CANCELLED")));
		    	  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		    	  EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    	  List<GenericValue> returnHeader = delegator.findList("ReturnHeader", cond, null, null, null, false);
		    	  if(UtilValidate.isNotEmpty(returnHeader)){
		    		  returnId = ((GenericValue)EntityUtil.getFirst(returnHeader)). getString("returnId");
		    	  }
		    	  
		    	  if(UtilValidate.isEmpty(returnId)){
		    		  
		    		  Map returnHeaderCtx = FastMap.newInstance();
			    	  returnHeaderCtx.put("originFacilityId", boothId);
			    	  returnHeaderCtx.put("returnHeaderTypeId", returnHeaderTypeId);
			    	  returnHeaderCtx.put("fromPartyId", ownerPartyId);
			    	  returnHeaderCtx.put("toPartyId", "Company");
			    	  returnHeaderCtx.put("entryDate", nowTimestamp);
			    	  returnHeaderCtx.put("needsInventoryReceive", receiveInventory);
			    	  returnHeaderCtx.put("statusId", "RETURN_ACCEPTED");
			    	  returnHeaderCtx.put("shipmentId", shipmentId);
			    	  returnHeaderCtx.put("userLogin", userLogin);
			    	  
			    	  result = dispatcher.runSync("createReturnHeader", returnHeaderCtx);
			    	  
			    	  if (ServiceUtil.isError(result)) {
						  Debug.logError("Error creating ReturnHeader: " + ServiceUtil.getErrorMessage(result), module);
						  return ServiceUtil.returnError("Error creating ReturnHeader: " + ServiceUtil.getErrorMessage(result));          	            
			          } 
			    	  
			    	  returnId = (String)result.get("returnId");
	    			  BigDecimal totalReturnAmount = BigDecimal.ZERO;
			    	  for(int i=0; i< productQtyList.size() ; i++){
			    		  Map productQtyMap = productQtyList.get(i);
			    		  String productId = (String)productQtyMap.get("productId");
			    		  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");
			    		  
			    		  GenericValue returnItem = delegator.makeValue("ReturnItem");
			    		  returnItem.put("returnReasonId", "RTN_DEFECTIVE_ITEM");
			    		  returnItem.put("statusId", "RETURN_ACCEPTED");
			    		  returnItem.put("returnId", returnId);
			    		  returnItem.put("productId", productId);
			    		  returnItem.put("returnQuantity", quantity);
			    		  returnItem.put("returnTypeId", "RTN_REFUND");
			    		  returnItem.put("returnItemTypeId", "RET_FPROD_ITEM");
			    		  delegator.setNextSubSeqId(returnItem, "returnItemSeqId", 5, 1);
			    		  delegator.create(returnItem);
			    		  
			    			  /* calculate price for return items and create payment(Credit note)*/
		    			  	Map<String, Object> priceContext = FastMap.newInstance();
		                    priceContext.put("userLogin", userLogin);   
		                    priceContext.put("productStoreId", productStoreId);                    
		                    priceContext.put("productId", productId);
		                    priceContext.put("partyId", ownerPartyId);
		                    priceContext.put("facilityId", boothId); 
		                    priceContext.put("priceDate", effectiveDate);
		                    
		                    
		                    Map priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);            			
		                    if (ServiceUtil.isError(priceResult)) {
		                        Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
		                		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
		                    }  
		                    BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
		                    totalPrice = totalPrice.multiply(quantity);
		                    totalReturnAmount = totalReturnAmount.add(totalPrice);
			    			 
				  	  }
			    	  if(totalReturnAmount.compareTo(BigDecimal.ZERO)>0){
			    		  Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", "SALES_PAYIN");        	
			              paymentCtx.put("paymentMethodTypeId", "CREDITNOTE_PAYIN");
			              paymentCtx.put("partyIdFrom", "Company");
			              paymentCtx.put("partyIdTo", ownerPartyId);
			              paymentCtx.put("effectiveDate", effectiveDate);
			              paymentCtx.put("facilityId", boothId);            
			              paymentCtx.put("statusId", "PMNT_RECEIVED");            
			              paymentCtx.put("amount", totalReturnAmount);
			              paymentCtx.put("paymentDate", effectiveDate);
			              paymentCtx.put("userLogin", userLogin);
			              paymentCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
			              paymentCtx.put("lastModifiedByUserLogin",  userLogin.getString("userLoginId"));
			              paymentCtx.put("createdDate", UtilDateTime.nowTimestamp());
			              paymentCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
			              Map<String, Object> paymentResult = dispatcher.runSync("createPayment", paymentCtx);
			              if (ServiceUtil.isError(paymentResult)) {
			              	Debug.logError(paymentResult.toString(), module);    			
			                  return ServiceUtil.returnError(null, null, null, paymentResult);
			              }
			              String paymentId = (String)paymentResult.get("paymentId");
			    	  }
		    	  }else{
		    		  List<GenericValue> returnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId), null, null, null , false);
		    		  
		    		  if(UtilValidate.isNotEmpty(returnItems)){
		    			  int removeReturnItems = delegator.removeAll(returnItems);
		    		  }
		    		  for(int i=0; i< productQtyList.size() ; i++){
			    		  Map productQtyMap = productQtyList.get(i);
			    		  String productId = (String)productQtyMap.get("productId");
			    		  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");
			    		  
			    		  GenericValue returnItem = delegator.makeValue("ReturnItem");
			    		  returnItem.put("returnReasonId", "RTN_DEFECTIVE_ITEM");
			    		  returnItem.put("statusId", "RETURN_ACCEPTED");
			    		  returnItem.put("returnId", returnId);
			    		  returnItem.put("productId", productId);
			    		  returnItem.put("returnQuantity", quantity);
			    		  returnItem.put("returnTypeId", "RTN_REFUND");
			    		  returnItem.put("returnItemTypeId", "RET_FPROD_ITEM");
			    		  delegator.setNextSubSeqId(returnItem, "returnItemSeqId", 5, 1);
			    		  delegator.create(returnItem);
		    		  }
		    			  
	    			  BigDecimal totalReturnAmount = BigDecimal.ZERO;
	    			  for(int i=0; i< productQtyList.size() ; i++){
			    		  Map productQtyMap = productQtyList.get(i);
			    		  String prodId = (String)productQtyMap.get("productId");
			    		  BigDecimal qty = (BigDecimal)productQtyMap.get("quantity");
			    		  
			    		  Debug.log("calculating return items prices for product : "+prodId);
	    				  Map<String, Object> priceContext = FastMap.newInstance();
		                  priceContext.put("userLogin", userLogin);   
		                  priceContext.put("productStoreId", productStoreId);                    
		                  priceContext.put("productId", prodId);
		                  priceContext.put("partyId", ownerPartyId);
		                  priceContext.put("facilityId", boothId); 
		                  priceContext.put("priceDate", effectiveDate);
		                  Map priceResult = calculateByProductsPrice(delegator, dispatcher, priceContext);            			
		                  if (ServiceUtil.isError(priceResult)) {
		                       Debug.logError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
		                       return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
		                  }  
		                  BigDecimal totalPrice = (BigDecimal)priceResult.get("totalPrice");
		                  Debug.log("calculated return items prices for product : "+totalPrice.intValue());
		                  totalPrice = totalPrice.multiply(qty);
		                  totalReturnAmount = totalReturnAmount.add(totalPrice);
		                  Debug.log("calculated return items prices for product : "+totalReturnAmount.intValue());
	    			  }
	    			  Timestamp dayBegin = UtilDateTime.getDayStart(effectiveDate);
	    			  Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate);
	    			  Debug.log("calculated return items prices : "+totalReturnAmount);
	    			  List condList = FastList.newInstance();
	    			  condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
	    			  condList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "CREDITNOTE_PAYIN"));
	    			  condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID")));
	    			  condList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	    			  condList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	    			  EntityCondition exprCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	    			  List<GenericValue> payments = delegator.findList("Payment", exprCond, null, null, null, false);
	    			  if(UtilValidate.isNotEmpty(payments)){
	    				  GenericValue payment = EntityUtil.getFirst(payments);
	    				  Map resultPayMap = dispatcher.runSync("voidPayment", UtilMisc.toMap("paymentId", payment.getString("paymentId"), "userLogin", userLogin));
	    				  if (ServiceUtil.isError(resultPayMap)) {
		                       Debug.logError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap), module);
		                       return ServiceUtil.returnError("There was an error in cancelling credit note: " + ServiceUtil.getErrorMessage(resultPayMap));          	            
		                  }
	    			  }
	    			  Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", "SALES_PAYIN");        	
		              paymentCtx.put("paymentMethodTypeId", "CREDITNOTE_PAYIN");
		              paymentCtx.put("partyIdFrom", "Company");
		              paymentCtx.put("partyIdTo", ownerPartyId);
		              paymentCtx.put("effectiveDate", effectiveDate);
		              paymentCtx.put("facilityId", boothId);            
		              paymentCtx.put("statusId", "PMNT_RECEIVED");            
		              paymentCtx.put("amount", totalReturnAmount);
		              paymentCtx.put("paymentDate", effectiveDate);
		              paymentCtx.put("userLogin", userLogin);
		              paymentCtx.put("createdByUserLogin", userLogin.getString("userLoginId"));
		              paymentCtx.put("lastModifiedByUserLogin",  userLogin.getString("userLoginId"));
		              paymentCtx.put("createdDate", UtilDateTime.nowTimestamp());
		              paymentCtx.put("lastModifiedDate", UtilDateTime.nowTimestamp());
		              Map<String, Object> paymentResult = dispatcher.runSync("createPayment", paymentCtx);
		              if (ServiceUtil.isError(paymentResult)) {
		              	Debug.logError(paymentResult.toString(), module);    			
		                  return ServiceUtil.returnError(null, null, null, paymentResult);
		              }
		              String paymentId = (String)paymentResult.get("paymentId");
		    	  }
			  }catch (Exception e) {
				  Debug.logError(e, "Error creating Returns", module);		  
				  return ServiceUtil.returnError("Error creating Returns");			  
			  }
			  return result;  
	    }
	
	    public static Map<String ,Object> processInstitutionBilling(DispatchContext dctx, Map<String, ? extends Object> context){
			  Delegator delegator = dctx.getDelegator();
		      LocalDispatcher dispatcher = dctx.getDispatcher();       
		      GenericValue userLogin = (GenericValue) context.get("userLogin");
		      Map<String, Object> result = ServiceUtil.returnSuccess();
		      String customTimePeriodId = (String)context.get("customTimePeriodId");
		      String periodTypeId = (String)context.get("periodTypeId");
		      String facilityId = (String)context.get("facilityId");	      
		      List conditionList = FastList.newInstance();
		      List<String> facilityIds = FastList.newInstance();
		      try{
		    	  
		    	  GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		    	  Timestamp fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		    	  Timestamp thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		    	  Timestamp endThruDate = UtilDateTime.getDayEnd(thruDate);
		    	  Timestamp dayStartThruDate = UtilDateTime.getDayStart(thruDate);
		    	  Timestamp dayBeginFromDate = UtilDateTime.getDayStart(fromDate);
		    	  
		    	  
		    	  if(!facilityId.equals("AllInstitutions")){
		    		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		    		  conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		    		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		    		  EntityCondition checkExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    		  List<GenericValue> periodBilling = delegator.findList("PeriodBilling", checkExpr, null, null, null, false);
		    		  if(UtilValidate.isNotEmpty(periodBilling)){
		    			  Debug.logError("Billing is already generated for the period for the institution "+facilityId, module);
	                      return ServiceUtil.returnError("Billing is already generated for the period for the institution "+facilityId);
		    		  }
		    	  }
		    	  
		    	  conditionList.clear();
		    	  if(!facilityId.equals("AllInstitutions")){
		    		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		    	  }
		    	  conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
		    	  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    	  List<GenericValue> customBilling = delegator.findList("FacilityCustomBilling", condition, null, null, null, false);
		    	  customBilling = EntityUtil.filterByDate(customBilling, dayBeginFromDate);
		    	  if(facilityId.equals("AllInstitutions")){
		    		  List tempList = (List)EntityUtil.getFieldListFromEntityList(customBilling, "facilityId", true);
		    		  facilityIds.addAll(tempList);
		    	  }
		    	  else{
		    		  facilityIds.add(facilityId);
		    	  }
		    	  
		    	  for(String facId : facilityIds){
		    		  
		    		  conditionList.clear();
		    		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facId));
			    	  conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
			    	  EntityCondition expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			    	  List<GenericValue> facCustomBilling = delegator.findList("FacilityCustomBilling", expr, null, null, null, false);
			    	  facCustomBilling = EntityUtil.filterByDate(facCustomBilling, endThruDate);
			    	  
			    	  if(UtilValidate.isEmpty(facCustomBilling)){
			    		  Debug.logError("Institution is not configured to any billing period type", module);
	                      //return ServiceUtil.returnError("Institution is not configured to any billing period  type");
			    		  continue;
			    	  }
		    		  
		    		  conditionList.clear();
			    	  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facId));
			    	  conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
			    	  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
			    	  EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			    	  List<GenericValue> periodBilling = delegator.findList("PeriodBilling", condExpr, null, null, null, false);
			    	  if(UtilValidate.isNotEmpty(periodBilling)){
			    		  Debug.logError("Billing has been generated for the period to the dealer :"+facId, module);
	                      //return ServiceUtil.returnError("Billing has been generated for the period to the dealer :"+facId);
			    		  continue;
			    	  }
			    	  
			    	  List shipmentIds = ByProductNetworkServices.getByProdShipmentIds(delegator, fromDate, endThruDate);
			    	  
			    	  conditionList.clear();
			    	  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
			    	  conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facId));
			    	  conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED", "ORDER_REJECTED")));
			    	  EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			    	  List<GenericValue> orderItems = delegator.findList("OrderHeaderItemProductShipmentAndFacility", cond, null, UtilMisc.toList("-estimatedDeliveryDate"), null, false);
			    	  List orderIds = EntityUtil.getFieldListFromEntityList(orderItems, "orderId", true);
			    	  /*List productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
			    	  
			    	  GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facId), false);
			    	  String partyId = facility.getString("ownerPartyId");
			    	  
			    	  String productId ="";
			    	  List<Map> productItemsList = FastList.newInstance();
			    	  for(int i=0;i<productIds.size();i++){
			    		  List<GenericValue> orderedProducts = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, (String)productIds.get(i)));
			    		  BigDecimal totalProductPrice = BigDecimal.ZERO;
			    		  BigDecimal qty = BigDecimal.ZERO;
			    		  Map prodQty = FastMap.newInstance();
				    		  productId = orderedProduct.getString("productId");
				    		  BigDecimal tempQty = orderedProduct.getBigDecimal("quantity");
				    		  BigDecimal unitPrice = orderedProduct.getBigDecimal("unitPrice");
				    		  BigDecimal itemPrice = tempQty.multiply(unitPrice);
				    		  totalProductPrice = totalProductPrice.add(itemPrice);
				    		  qty = qty.add(tempQty);
				    	  }
			    		  prodQty.put("productId", (String)productIds.get(i));
			    		  prodQty.put("quantity", qty);
			    		  prodQty.put("amount", totalProductPrice);
			    		  productItemsList.add(prodQty);
			    	  }
			    	  
			    	  Debug.log("productItemsList ##################################"+productItemsList);
			    	  
			    	  List<GenericValue> orderAdj = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds), null, null, null, false);
			    	  List orderAdjustmentTypeIds = EntityUtil.getFieldListFromEntityList(orderAdj, "orderAdjustmentTypeIds", true);
			    	  List<Map> taxAdjList = FastList.newInstance();
			    	  for(int i=0;i<orderAdjustmentTypeIds.size();i++){
			    		  Map prodTaxQty = FastMap.newInstance();
			    		  List<GenericValue> orderAdjTypeItems = EntityUtil.filterByCondition(orderAdj, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, orderAdjustmentTypeIds.get(i)));
			    		  BigDecimal amount = BigDecimal.ZERO;
			    		  String adjType = (String)orderAdjustmentTypeIds.get(i);
			    		  for(GenericValue orderAdjTypeItem : orderAdjTypeItems){
			    			  amount = amount.add(orderAdjTypeItem.getBigDecimal("amount"));
			    		  }
			    		  prodTaxQty.put("orderAdjustmentTypeId", adjType);
			    		  prodTaxQty.put("amount", amount);
			    		  taxAdjList.add(prodTaxQty);
			    	  }*/
			    	  
			    	  Map createInvoiceCtx = FastMap.newInstance();
			    	  createInvoiceCtx.put("userLogin", userLogin);
			    	  createInvoiceCtx.put("orderIds", orderIds);
			    	  createInvoiceCtx.put("invoiceDate", dayStartThruDate);
			    	  Map resultCtx = dispatcher.runSync("createInvoiceForAllOrders", createInvoiceCtx);
			    	  
			    	  if(ServiceUtil.isError(resultCtx)){
			    		  Debug.logError("Error in creating invoice for dealer:"+facId, module);
	                      return ServiceUtil.returnError("Error in creating invoice for dealer:"+facId);
			    	  }
			    	  
			    	  String invoiceId = (String)resultCtx.get("invoiceId");
			    	  
			    	  GenericValue newEntity = delegator.makeValue("PeriodBilling");
			    	  newEntity.set("billingTypeId", "CR_INST_BILLING");
			    	  newEntity.set("customTimePeriodId", customTimePeriodId);
			    	  newEntity.set("statusId", "GENERATED");
			    	  newEntity.set("facilityId", facId);
			    	  delegator.createSetNextSeqId(newEntity);
			    	  
			    	  String periodBillingId = newEntity.getString("periodBillingId");
			    	  String refNum = "CR_PB_"+periodBillingId;
			    	  GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			    	  invoice.set("referenceNumber",refNum);
			    	  invoice.store();
			    	  
		    	  }
		    	  
			  }catch (Exception e) {
				  Debug.logError(e, "Error creating Invoice for credit institution", module);		  
				  return ServiceUtil.returnError("Error creating Invoice for credit institution");			  
			  }
			  return result;  
	    }
	    
	    public static Map<String ,Object> processCancelInstitutionBilling(DispatchContext dctx, Map<String, ? extends Object> context){
			  Delegator delegator = dctx.getDelegator();
		      LocalDispatcher dispatcher = dctx.getDispatcher();       
		      GenericValue userLogin = (GenericValue) context.get("userLogin");
		      Map<String, Object> result = ServiceUtil.returnSuccess();
		      String customTimePeriodId = (String)context.get("customTimePeriodId");
		      String periodBillingId = (String)context.get("periodBillingId");	      
		      List conditionList = FastList.newInstance();
		      List<String> facilityIds = FastList.newInstance();
		      try{
		    	  
		    	  GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		    	  Timestamp fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		    	  Timestamp thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		    	  Timestamp endThruDate = UtilDateTime.getDayEnd(thruDate);
		    	  Timestamp dayBeginFromDate = UtilDateTime.getDayStart(fromDate);
		    	  
		    	  if(!periodBillingId.equals("AllInstitutions")){
		    		  conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
		    	  }
	    		  conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
	    		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	    		  EntityCondition checkExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    		  List<GenericValue> periodBillingList = delegator.findList("PeriodBilling", checkExpr, null, null, null, false);
	    		  if(UtilValidate.isEmpty(periodBillingList)){
	    			  Debug.logError("No valid period billing exists to cancel for the billingId "+periodBillingId, module);
                      return ServiceUtil.returnError("No valid period billing exists to cancel for the billingId "+periodBillingId);
	    		  }
		    	  
	    		  List<String> facilityIdsList = EntityUtil.getFieldListFromEntityList(periodBillingList, "facilityId", true);
	    		  List cancelInvoiceIdsList = FastList.newInstance();
	    		  for(String facId: facilityIdsList){
	    			  List<GenericValue> periodBillList = EntityUtil.filterByCondition(periodBillingList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facId));
	    			  String billingId = ((GenericValue)EntityUtil.getFirst(periodBillList)).getString("periodBillingId");
	    			  String invoiceReferenceNum = "CR_PB_"+billingId;
	    			  
	    			  conditionList.clear();
	    			  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facId));
		    		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		    		  conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS, invoiceReferenceNum));
		    		  EntityCondition invCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    		  
		    		  List<GenericValue> invoice = delegator.findList("Invoice", invCond, UtilMisc.toSet("invoiceId"), null, null, false);
		    		  if(UtilValidate.isNotEmpty(invoice)){
		    			  cancelInvoiceIdsList.add(((GenericValue)EntityUtil.getFirst(invoice)).getString("invoiceId"));
		    		  }
		    		  GenericValue periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", billingId), false);
		    		  periodBilling.set("statusId", "COM_CANCELLED");
		    		  periodBilling.store();
		    		  
	    		  }
	    		  Map resultCtx = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", cancelInvoiceIdsList, "statusId","INVOICE_CANCELLED","userLogin", userLogin));
	        		 
	        		 if (ServiceUtil.isError(resultCtx)) {
	        			 Debug.logError("There was an error while Cancel  the invoices: " + ServiceUtil.getErrorMessage(result), module);	               
	    	             return ServiceUtil.returnError("There was an error while Cancel  the invoices: ");   			 
	        		 }
		    	  
		      }catch (Exception e) {
				  Debug.logError(e, "Error cancelling Invoice for period billing", module);		  
				  return ServiceUtil.returnError("Error cancelling Invoice for period billing");			  
			  }
		      result = ServiceUtil.returnSuccess("Billing generated successfully !!");
		      return result;
	    }
	    
	    public static Map<String, Object> createInvoiceForAllOrders(DispatchContext dctx, Map<String, Object> context) {
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        List<String> orderIds = (List)context.get("orderIds");
	        Timestamp invoiceDate = (Timestamp)context.get("invoiceDate");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Locale locale = (Locale) context.get("locale");
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        try {
                if(UtilValidate.isEmpty(orderIds)){
                	Debug.logError("order List of empty", module);
                    return ServiceUtil.returnError("order list is empty");
                }
                
                String tempOrderId = (String)orderIds.get(0);
                List<GenericValue> billItems = FastList.newInstance();
                
	        	for(String orderId : orderIds){
	        		List<GenericValue> orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
	        		billItems.addAll(orderItems);
	        	}
	        	Map invoiceCtx = FastMap.newInstance();
	        	invoiceCtx.put("userLogin", userLogin);
	        	invoiceCtx.put("orderId", tempOrderId);
	        	invoiceCtx.put("billItems", billItems);
	        	if(UtilValidate.isNotEmpty(invoiceDate)){
	        		invoiceCtx.put("eventDate", invoiceDate);
	        	}
		        Map<String, Object> serviceResult = dispatcher.runSync("createInvoiceForOrder", invoiceCtx);
	        	if(ServiceUtil.isError(serviceResult)){
	        		Debug.logWarning("There was an error while creating  invoice: " + ServiceUtil.getErrorMessage(serviceResult), module);
	         		return ServiceUtil.returnError("There was an error while creating invoice: " + ServiceUtil.getErrorMessage(serviceResult));  
	        	}
	        	String invoiceId = (String)serviceResult.get("invoiceId");
	        	result.put("invoiceId", invoiceId);
	            
	        }
	        catch (GenericServiceException e) {
	        	Debug.logError(e, module);
		        return ServiceUtil.returnError(e.getMessage());
	        	
	        } catch (GenericEntityException e) {
	        	Debug.logError(e, module);
		        return ServiceUtil.returnError(e.getMessage());
	        }
	        return result;
	    }
	    
	    
	    /*public static Map<String ,Object> createInvoiceForItems(DispatchContext dctx, Map<String, ? extends Object> context){
			  Delegator delegator = dctx.getDelegator();
		      LocalDispatcher dispatcher = dctx.getDispatcher();       
		      GenericValue userLogin = (GenericValue) context.get("userLogin");
		      Map<String, Object> result = ServiceUtil.returnSuccess();
		      String partyId = (String)context.get("partyId");
		      String facilityId = (String)context.get("facilityId");
		      String partyIdFrom = (String)context.get("partyIdFrom");
		      String invoiceTypeId = (String)context.get("invoiceTypeId");
		      List<Map> productItems = (List) context.get("productItemsList");
		      List<Map> taxAdjList = (List) context.get("taxAdjList");
		      String statusId = (String) context.get("statusId");
		      Timestamp dueDate = (Timestamp) context.get("dueDate");
		      Timestamp now = UtilDateTime.nowTimestamp();
		      
		      List conditionList = FastList.newInstance();
		      String invoiceId ="";
		      try{
		    	  	if(UtilValidate.isEmpty(dueDate)){
		    	  		dueDate = UtilDateTime.getDayStart(now);
		    	  	}
		    	  	Map<String, Object> createInvoiceMap = FastMap.newInstance();
		            createInvoiceMap.put("partyId", partyId);
		            createInvoiceMap.put("partyIdFrom", partyIdFrom);
		            createInvoiceMap.put("invoiceDate", now);
	                createInvoiceMap.put("dueDate", dueDate);
	                createInvoiceMap.put("facilityId", facilityId);
		            createInvoiceMap.put("invoiceTypeId", invoiceTypeId);
		            createInvoiceMap.put("statusId", "INVOICE_IN_PROCESS");
		            createInvoiceMap.put("userLogin", userLogin);
		            Debug.log("invoice context #############################"+createInvoiceMap);
		            Map<String, Object> createInvoiceResult = null;
		            
		            try {
		                createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceMap);
		                if(ServiceUtil.isError(createInvoiceResult)){
		                	Debug.logError("Error in creating invoice for dealer:"+facilityId, module);
		                    return ServiceUtil.returnError("Error in creating invoice for dealer:"+facilityId);
		                }
		                invoiceId = (String)createInvoiceResult.get("invoiceId");
		                Debug.log("invoiceId ################################## in internal "+invoiceId);
		            } catch (GenericServiceException e) {
		                return ServiceUtil.returnError("Error creating invoice");
		            }
		            Debug.log("productItems ######################"+productItems);
		            Map<String, Object> resMap = FastMap.newInstance();
		            for (Map entries : productItems) {
		            	String productId = (String)entries.get("productId");
		            	BigDecimal qty = (BigDecimal)entries.get("quantity");
		            	BigDecimal amount = (BigDecimal)entries.get("amount");
		            	Debug.log("########## entries ####"+entries);
		            	resMap = dispatcher.runSync("createInvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "productId", productId, "invoiceItemTypeId", "INV_FPROD_ITEM",
		                            "quantity",qty, "amount", amount, "userLogin", userLogin));
		                 
		                if (ServiceUtil.isError(resMap)) {
		                    return ServiceUtil.returnError("Error creating invoice item for product "+productId);
		                }
		            }
		            Debug.log("taxAdjList ######################"+taxAdjList);
		            for (Map entry : taxAdjList) {
		            	String invoiceItemTypeId = (String)entry.get("orderAdjustmentTypeId");
		            	BigDecimal qty = BigDecimal.ONE;
		            	BigDecimal amount = (BigDecimal)entry.get("amount");

		                resMap = dispatcher.runSync("createInvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "productId", "", "invoiceItemTypeId", invoiceItemTypeId,
		                            "quantity",qty, "amount", amount, "userLogin", userLogin));
		                
		                if (ServiceUtil.isError(resMap)) {
		                    return ServiceUtil.returnError("Error creating invoice item for Tax "+invoiceItemTypeId);
		                }
		            }
		            
			  }catch (Exception e) {
				  Debug.logError(e, "Error creating Returns", module);		  
				  return ServiceUtil.returnError("Error creating Returns");			  
			  }
			  result.put("invoiceId", invoiceId);
			  return result;  
	    }*/
	    
	public static Map<String, Object> updateCrateQtyConfig(DispatchContext dctx, Map<String, ? extends Object> context){
	    Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Double liters = (Double) context.get("liters");
        Locale locale = (Locale) context.get("locale");     
        String uomId = (String) context.get("uomId");
        String uomIdTo = (String) context.get("uomIdTo");
        Map result = ServiceUtil.returnSuccess();
        if(UtilValidate.isEmpty(liters)){
        	Debug.logError("Liters field is Empty, nothing to update", module);
            return ServiceUtil.returnError("Liters field is Empty, nothing to update");
        }
        if(UtilValidate.isEmpty(uomId) || UtilValidate.isEmpty(uomIdTo)){
   			Debug.logError("uomId is Empty", module);
   			return ServiceUtil.returnError("uomId is Empty");
   		}
        GenericValue uomConversion = null;
		try {
			uomConversion = delegator.findOne("UomConversion", UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo), false);
			if(UtilValidate.isEmpty(uomConversion)){
				GenericValue newEntity = delegator.makeValue("UomConversion");
				newEntity.set("uomId", uomId);
				newEntity.set("uomIdTo", uomIdTo);
				newEntity.set("conversionFactor", liters);
				newEntity.create();
			}
			else{
				uomConversion.set("conversionFactor", liters);
				uomConversion.store();
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
        result = ServiceUtil.returnSuccess("Successfully Updated!!");
        return result;
    }
	public static Map<String, Object> createReturnCrate(DispatchContext ctx, Map<String, ? extends Object> context){
		
      	Delegator delegator = ctx.getDelegator();
      	Locale locale = (Locale) context.get("locale");
      	GenericValue userLogin = (GenericValue) context.get("userLogin");
      	LocalDispatcher dispatcher = ctx.getDispatcher();
      	String productId = (String)context.get("productId");
      	BigDecimal returnQuantity = (BigDecimal)context.get("returnQuantity");
      	String productStoreId = (String)context.get("productStoreId");
      	Timestamp shipmentDate = (Timestamp)context.get("shipmentDate");
      	String tripId = (String)context.get("tripId");
      	String routeId = (String)context.get("routeId");
      	Map<String, Object> result = ServiceUtil.returnSuccess();
      	List conditionList = FastList.newInstance();
      	Timestamp startDate = UtilDateTime.getDayStart(shipmentDate);
      	Timestamp endDate = UtilDateTime.getDayEnd(shipmentDate);
		try {
			GenericValue route = delegator.findOne("Facility", UtilMisc.toMap("facilityId", routeId), false);
			if(UtilValidate.isEmpty(route)){
				Debug.logError("Given route does not exists : "+routeId,module);
 				return ServiceUtil.returnError("Given route does not exists : "+routeId);
			}
			
			conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
			if(UtilValidate.isNotEmpty(tripId)){
				conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
			}
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			List<GenericValue> shipments = delegator.findList("Shipment", condition, UtilMisc.toSet("shipmentId"), null, null, false);
			if(UtilValidate.isEmpty(shipments)){
				Debug.logError("No Shipment found for the given details",module);
 				return ServiceUtil.returnError("No Shipment found for the given details");
			}
			
			String shipmentId = ((GenericValue)EntityUtil.getFirst(shipments)).getString("shipmentId");
			
			/*conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			*/
			List<GenericValue> productStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId), null, null, null, false);
	  		productStoreFacility = EntityUtil.filterByDate(productStoreFacility, startDate);
	  		String invFacility = "";
	  		String invItemId = "";
	  		
	  		List invExpr = FastList.newInstance();
	  		if(UtilValidate.isNotEmpty(productStoreFacility)){
	  		  	invFacility = ((GenericValue)EntityUtil.getFirst(productStoreFacility)).getString("facilityId");
	  		}
	  		  
	  		invExpr.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "CRATE"));
	  		invExpr.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, invFacility));
	  		EntityCondition cond1 = EntityCondition.makeCondition(invExpr, EntityOperator.OR);
	  		List<GenericValue> invItemDetail = delegator.findList("InventoryItem", cond1, null, null, null, true);
	  		  	
	  		if(UtilValidate.isNotEmpty(invItemDetail)){
	  		  	invItemId = ((GenericValue)EntityUtil.getFirst(invItemDetail)).getString("inventoryItemId");
	  		}
			
			
			Map receiptCtx = FastMap.newInstance();
			receiptCtx.put("datetimeReceived", startDate);
			receiptCtx.put("userLogin", userLogin);
			receiptCtx.put("productId", productId);
			receiptCtx.put("quantityAccepted", returnQuantity);
			receiptCtx.put("quantityRejected", BigDecimal.ZERO);
			receiptCtx.put("shipmentId", shipmentId);
			receiptCtx.put("inventoryItemId", invItemId);
			
			Map receiptMap = dispatcher.runSync("createShipmentReceipt", receiptCtx);
			if(ServiceUtil.isError(receiptMap)){
				Debug.logError("There was an error while creating  receipt of crate from transporter" + ServiceUtil.getErrorMessage(result), module);
         		return ServiceUtil.returnError("There was an error while creating  receipt of crate from transporter" + ServiceUtil.getErrorMessage(result));
			}
			String receiptId = (String)receiptMap.get("receiptId");
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
		
        result = ServiceUtil.returnSuccess("Successfully received crates from transporter!!");
        return result;
    }
	
	public static Map<String, Object> adjustEmployeeSubsidyForOrder(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");     
        Map result = ServiceUtil.returnSuccess();
        BigDecimal subPercent = new BigDecimal("50");
        BigDecimal subAmount = BigDecimal.ZERO;
		try {
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			List<GenericValue> orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
			for(GenericValue orederItem : orderItems){
				BigDecimal itemSubAmt = orederItem.getBigDecimal("unitPrice").multiply(subPercent.divide(new BigDecimal("100")));
				subAmount = (subAmount.add(itemSubAmt)).multiply(orederItem.getBigDecimal("quantity"));
			}
			// add employee subsidy adjustment "EMPSUBSID_ADJUSTMENT"
			String orderAdjustmentTypeId = "EMPSUBSID_ADJUSTMENT";
			 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
	    	 createOrderAdjustmentCtx.put("orderId", orderId);
	    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);    	
	    	 createOrderAdjustmentCtx.put("amount", subAmount.negate());
	    	 result = dispatcher.runSync("createOrderAdjustment", createOrderAdjustmentCtx);
	     	 if (ServiceUtil.isError(result)) {
	                Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(result), module);
	         		return ServiceUtil.returnError("There was an error while creating the adjustment: " + ServiceUtil.getErrorMessage(result));          	            
	         } 
		
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.toString());
		}
        result = ServiceUtil.returnSuccess("Successfully added the adjustment!!");
        result.put("orderId", orderId);
        return result;
    }
	/**
	 * Employee subsidy 
	 */
	public static Map<String, Object> createOrUpdateEmployeeSubsidy(DispatchContext ctx,Map<String, Object> context) {
        Map<String, Object> finalResult = FastMap.newInstance();
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        String partyId = (String) context.get("partyId");
        String facilityId = (String) context.get("facilityId");
        String productId = (String) context.get("productId");
        String shipmentTypeId = (String) context.get("shipmentTypeId");
        String tripId = (String) context.get("tripId");
        BigDecimal quantity =BigDecimal.ZERO;
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String subscriptionId = "";
        String indentChanged = "";
        String tempFacId = "";
        String tempPartyId = "";
        String roleTypeId="EMPSUBISDY_ROLE";
        List<GenericValue> subscriptionList=FastList.newInstance();
        Map<String  ,Object> productQtyMap = FastMap.newInstance();
        List<Map> subscriptionProductsList = FastList.newInstance();
        Map<String, Object> resultMap = ServiceUtil.returnSuccess();;
        String subscriptionTypeId = "";
        if(UtilValidate.isNotEmpty(shipmentTypeId)){
        	if(shipmentTypeId.equals("AM_SHIPMENT")){
        		subscriptionTypeId = "AM";
        	}else{
        		subscriptionTypeId = "PM";
        	}
        }
        if(fromDate==null){
        	fromDate= UtilDateTime.nowTimestamp();
        }
        if(UtilValidate.isNotEmpty(thruDate)){
        	thruDate=UtilDateTime.getDayEnd(thruDate);
        }
        fromDate=UtilDateTime.getDayStart(fromDate);
        
        if(fromDate.before(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()))){
        	Debug.logError("From date  shoud be greater than current date : "+fromDate+ "\t",module);
			return ServiceUtil.returnError("From date  shoud be greater than current date  : "+fromDate);
        }
        List conditionList = UtilMisc.toList(
        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        conditionList.add(
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
    	try{
    		/**getting the facility party based on the party Id and role type and from date,
    		 if facility party exist getting old facility Id from the list and populating the thru date for old facility 
    		 and creating new facility,if facility party not exists creating new party role and facility party*/
    	   List<GenericValue> subsidyEmpList = delegator.findList("FacilityParty", condition, null, null, null, false); 
           List<GenericValue> facilityParties = EntityUtil.filterByDate(subsidyEmpList,fromDate);
	           if(UtilValidate.isNotEmpty(facilityParties)){
			        conditionList.clear();
			       	GenericValue facilityParty = facilityParties.get(0);
			       	tempFacId = facilityParty.getString("facilityId");
			       	tempPartyId = facilityParty.getString("partyId");
			       	if(tempFacId.equals(facilityId) && tempPartyId.equals(partyId) ){
					   	Debug.logError("Error in create subsidy for employee : "+partyId+ "\t",module);
						return ServiceUtil.returnError("Error in create subsidy for employee  : "+partyId);
				    }
			       	if(!tempFacId.equals(facilityId)){
			       		facilityParty.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1), TimeZone.getDefault(), locale));
			           	facilityParty.store();
			       	}
			        
	           }else{
	        	   GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
	        	   if(UtilValidate.isEmpty(partyRole)){
	        		   resultMap = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", userLogin));
	        		   if(ServiceUtil.isError(resultMap)){
		    			   	Debug.logError("Error in create party role for employee : "+partyId+ "\t",module);
		   					return ServiceUtil.returnError("Error in create party role for employee : "+partyId);
	        		   }
	        	   }
	           }
		   	GenericValue newEntity = delegator.makeValue("FacilityParty");
	        newEntity.set("partyId", partyId);
	        newEntity.set("facilityId", facilityId);
	        newEntity.set("fromDate", fromDate);
	        newEntity.set("thruDate", thruDate);
	        newEntity.set("roleTypeId", roleTypeId);
		        try {
					delegator.create(newEntity);
				}catch (GenericEntityException e) {
					Debug.logError("Error in create Employee : "+partyId+ "\t"+e.toString(),module);
					return ServiceUtil.returnError(e.getMessage());
				}
			String fromDateStr = UtilDateTime.toDateString(fromDate, "dd MMMMM, yyyy");
			Map indentHelperCtx = UtilMisc.toMap("userLogin",userLogin);
			indentHelperCtx.put("subscriptionTypeId", subscriptionTypeId);
			indentHelperCtx.put("productId", productId);
			indentHelperCtx.put("productSubscriptionTypeId","EMP_SUBSIDY");
			indentHelperCtx.put("supplyDate",  fromDateStr);
			indentHelperCtx.put("isEnableProductSubscriptionType",  Boolean.TRUE);
			  	/**if facility party not equals to old facility party  getting the quantity and subtracting the quantity and
			   creating a new record  with new quantity  in subscription product and doing the end subscription for the old facility party  **/
			    if(UtilValidate.isNotEmpty(tempFacId) && !tempFacId.equals(facilityId)){
			  		 result = (Map)ByProductNetworkServices.getBoothRoute(ctx, UtilMisc.toMap("boothId", tempFacId, "subscriptionTypeId", subscriptionTypeId, "supplyDate", fromDate, "userLogin", userLogin));
	        		 String routeId = (String)((Map) result.get("boothDetails")).get("routeId");
	        	     indentHelperCtx.put("boothId", tempFacId);
	        	     indentHelperCtx.put("routeId",  routeId);
	        		 result=ByProductNetworkServices.getBoothChandentIndent(ctx,indentHelperCtx);
	        		 routeId=(String)result.get("routeId");
		   	         List changeIndentProductList=(List)result.get("changeIndentProductList");
		   	         List<Map> prodQtyList = FastList.newInstance();
		   	         BigDecimal prvQty = BigDecimal.ZERO;
		   	         BigDecimal tempPrvQty = BigDecimal.ZERO;
			   	        if(UtilValidate.isNotEmpty(changeIndentProductList)){
				   	         for(int j=0;j<changeIndentProductList.size();j++){
				   	        	 Map prodQty = FastMap.newInstance();
				   	        	 Map tempMap = (Map)changeIndentProductList.get(j);
				   	        	 String tempProd = (String)tempMap.get("cProductId");
				   	        	 if(tempProd.equals(productId)){
			                         prvQty = (BigDecimal)tempMap.get("cQuantity");
				   	        	 }
				   	         }
				   	     }
			   	         Debug.log("temp facId changeIndentProductList"+changeIndentProductList);
				   	     if(prvQty.intValue()>0){
				   	    	
		   		        	 Map<String, Object> input = FastMap.newInstance();
			   		         input.put("userLogin", userLogin);
			   		         input.put("facilityId", tempFacId);
			   		         input.put("productSubscriptionTypeId", "EMP_SUBSIDY");
			   		         input.put("productId", productId);
			   		         input.put("closeDate", fromDate);
			   		         try {
			   		         	resultMap = dispatcher.runSync("endSubscriptionIndent", input);
			   		         } catch (GenericServiceException e) {
			   		             Debug.logError(e, module);
			   		             return ServiceUtil.returnError(e.getMessage());
			   		         }
				   		         Map tempMap = FastMap.newInstance();
				   		         tempMap.put("productId", productId);
				   		         tempMap.put("sequenceNum", routeId);
				   		         tempPrvQty=prvQty.subtract(BigDecimal.ONE);
				   		         tempMap.put("quantity", tempPrvQty);
				   		         prodQtyList.add(tempMap);
				   		        
				   		         
		   		         }
				   	  if (tempPrvQty.intValue()!=0) { 
				   	     List condList = FastList.newInstance();
				   	     condList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
				   	     condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, tempFacId));
				   	     condList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
				   	     EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				   	     List<GenericValue> subscription = delegator.findList("Subscription", cond, UtilMisc.toSet("subscriptionId"), null, null ,false);
				   	     subscription = EntityUtil.filterByDate(subscription,fromDate);
				   	     if(UtilValidate.isNotEmpty(subscription)){
				   	    	 subscriptionId = (EntityUtil.getFirst(subscription)).getString("subscriptionId");
				   	  	  }else{
				   	      	Debug.logError("Error in getting subscriptionId : "+tempFacId+ "\t",module);
							return ServiceUtil.returnError("Error in getting subscriptionId  : "+tempFacId);
				 
				   	     }
				   	     Debug.log("temp facId end subscriptionId"+subscriptionId);
		   		         
		   	             Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);
					  	 processChangeIndentHelperCtx.put("subscriptionId", subscriptionId);
					  	 processChangeIndentHelperCtx.put("boothId", tempFacId);
					  	 processChangeIndentHelperCtx.put("shipmentTypeId", shipmentTypeId);
					  	 processChangeIndentHelperCtx.put("effectiveDate", fromDate);
					  	 processChangeIndentHelperCtx.put("productQtyList", prodQtyList);
					  	 processChangeIndentHelperCtx.put("productSubscriptionTypeId",  "EMP_SUBSIDY");
					  	 try{
						  		 result = dispatcher.runSync("processChangeIndentHelper",processChangeIndentHelperCtx);
								 if (ServiceUtil.isError(result)) {
									String errMsg =  ServiceUtil.getErrorMessage(result);
									Debug.logError(errMsg , module);
									return ServiceUtil.returnError("Error in processChangeIndentHelper"); 
								 }
						  	 }catch (Exception e) {
						  			  Debug.logError(e, "Problem updating subscription for booth " + tempFacId, module);     
						  	 }
							  	 
				  	 }//ending old facility block
			    }
			/**if facility party equals new facility id or if no record found for old facility  getting the quantity for facilitry id and incrementing with one and
			creating a new record  with new quantity  in subscription product and end subscription for the old record with same facility **/
				
    	     List<Map> prodQtyList = FastList.newInstance();
    	     BigDecimal createQty = BigDecimal.ZERO;
    	     result = (Map)ByProductNetworkServices.getBoothRoute(ctx, UtilMisc.toMap("boothId", facilityId, "subscriptionTypeId", subscriptionTypeId, "supplyDate", fromDate, "userLogin", userLogin));
    	     String routeId = (String)((Map) result.get("boothDetails")).get("routeId");
		  	 indentHelperCtx.put("boothId", facilityId);
		  	 indentHelperCtx.put("routeId",  routeId);
    		 result=ByProductNetworkServices.getBoothChandentIndent(ctx,indentHelperCtx);
	         List changeIndentProductList=(List)result.get("changeIndentProductList");
		         for(int j=0;j<changeIndentProductList.size();j++){
		        	 Map prodQty = FastMap.newInstance();
		        	 Map tempMap = (Map)changeIndentProductList.get(j);
		        	 String tempProd = (String)tempMap.get("cProductId");
		        	 if(tempProd.equals(productId)){
		        		 createQty= (BigDecimal)tempMap.get("cQuantity");
		        	 }
		         }
		         if(createQty.intValue()>0){
			         Map<String, Object> input = FastMap.newInstance();
	 		         input.put("userLogin", userLogin);
	 		         input.put("facilityId", facilityId);
	 		         input.put("productSubscriptionTypeId", "EMP_SUBSIDY");
	 		         input.put("productId", productId);
	 		         input.put("closeDate", fromDate);
	 		         try {
	 		         	resultMap = dispatcher.runSync("endSubscriptionIndent", input);
	 		         } catch (GenericServiceException e) {
	 		             Debug.logError(e, module);
	 		             return ServiceUtil.returnError(e.getMessage());
	 		         }
	 		     }
        	 Map prodQty = FastMap.newInstance();
        	 prodQtyList = FastList.newInstance();
        	 prodQty.put("productId", productId);
        	 prodQty.put("quantity",createQty.add(BigDecimal.ONE));
        	 prodQty.put("sequenceNum", routeId);
        	 prodQtyList.add(prodQty);
        	 
        	 List condList = FastList.newInstance();
	   	     condList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
	   	     condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	   	     condList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	   	     EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	   	     List<GenericValue> subscription = delegator.findList("Subscription", cond, UtilMisc.toSet("subscriptionId"), null, null ,false);
		    	subscription = EntityUtil.filterByDate(subscription, fromDate); 
		    	Debug.log("subscription"+subscription);
	   	     if(UtilValidate.isNotEmpty(subscription)){
		   	    	 subscriptionId = (EntityUtil.getFirst(subscription)).getString("subscriptionId");
		   	     }else{
		   	    	Debug.logError("Error in getting subscriptionId",module);
					return ServiceUtil.returnError("Error in getting subscriptionId  : "+tempFacId); 
		   	     }
			 Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);
		  	 processChangeIndentHelperCtx.put("subscriptionId", subscriptionId);
		  	 processChangeIndentHelperCtx.put("boothId", facilityId);
		  	 processChangeIndentHelperCtx.put("shipmentTypeId", shipmentTypeId);
		  	 processChangeIndentHelperCtx.put("effectiveDate", fromDate);
		  	 processChangeIndentHelperCtx.put("productQtyList", prodQtyList);
		  	 processChangeIndentHelperCtx.put("productSubscriptionTypeId",  "EMP_SUBSIDY");
			  	 try{
			  		 result = dispatcher.runSync("processChangeIndentHelper",processChangeIndentHelperCtx);
					 if (ServiceUtil.isError(result)) {
						String errMsg =  ServiceUtil.getErrorMessage(result);
						Debug.logError(errMsg , module);
						return ServiceUtil.returnError("Error in processChangeIndentHelper"); 
					 }
			  	 }catch (Exception e) {
			  			  Debug.logError(e, "Problem updating subscription for booth " + facilityId, module);     
			  	 }
    	}catch(Exception e){
    		  Debug.logError(e, module);
              return ServiceUtil.returnError(e.getMessage());
    	}
    	        
    	finalResult.put("partyId", partyId);
    	finalResult.put("facilityId", facilityId);
    	
    	finalResult.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return finalResult;
    }
	public static String processAdhocSale(HttpServletRequest request, HttpServletResponse response) {
		  Delegator delegator = (Delegator) request.getAttribute("delegator");
		  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	 	  DispatchContext dctx =  dispatcher.getDispatchContext();
	 	  Locale locale = UtilHttp.getLocale(request);
	 	  String boothId = (String) request.getParameter("boothId");
	 	 // String bankName = (String) request.getParameter("bankName");
	 	 // String chequeNo = (String) request.getParameter("chequeNo");
	 	  String amountStr = (String) request.getParameter("amount");
	 	  String totalAmount = (String) request.getParameter("totAmt");
	 	  BigDecimal amount = BigDecimal.ZERO;
	 	  BigDecimal totAmt = BigDecimal.ZERO;
	 	  if(UtilValidate.isNotEmpty(amountStr)){
	 		  amount = new BigDecimal(amountStr);
	 	  }
	 	  if(UtilValidate.isNotEmpty(totalAmount)){
	 		  totAmt = new BigDecimal(totalAmount);
	 	  }
	 	  Map resultMap = FastMap.newInstance();
	 	  List invoices = FastList.newInstance(); 
	 	  //String chequeDate = (String) request.getParameter("chequeDate");
	 	  String routeId = (String) request.getParameter("routeId");
	 	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
	 	  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
	 	  String shipmentTypeId = (String) request.getParameter("shipmentTypeId");
	 	  String subscriptionTypeId = "AM";
	 	  String partyIdFrom = "";
	 	 String shipmentId = "";
	 	  if(UtilValidate.isEmpty(shipmentTypeId)){
	 		  routeId = (String) request.getAttribute("routeId");
		  	   productSubscriptionTypeId = (String) request.getAttribute("productSubscriptionTypeId");
		  	  shipmentTypeId = (String) request.getAttribute("shipmentTypeId");
		  	shipmentId=(String)request.getAttribute("shipmentId");
			boothId=(String)request.getAttribute("boothId");
	 	  }
	 	  if(shipmentTypeId.equals("BYPROD_PM_SUPPL")){
	       	subscriptionTypeId = "PM";       	
	     }
	 	  String salesChannel = "";
	 	  
	 	  if(shipmentTypeId.equals("RM_DIRECT_SHIPMENT")){
	 		 salesChannel = "RM_DIRECT_CHANNEL";      	
	     }
	 	  //String shipmentTypeId = "BYPRODUCTS_SUPPL"; 
	 	  // String salesChannel = "BYPROD_SALES_CHANNEL";
	     List<String> leakProductList =UtilMisc.toList("1");//for BMP 200 ml leaks to be added
	 	  String productId = null;
	 	  String quantityStr = null;
	 	  Timestamp effectiveDate=null;
	 	  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
	 	  BigDecimal quantity = BigDecimal.ZERO;
	 	  BigDecimal butterLeakQty = BigDecimal.ZERO;
	 	  List<GenericValue> subscriptionList=FastList.newInstance();
	 	  Map<String, Object> result = ServiceUtil.returnSuccess();
	 	  HttpSession session = request.getSession();
	 	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	 	  GenericValue subscription = null;
	 	  GenericValue facility = null;
	 	  List custTimePeriodList = FastList.newInstance();  
	 	  Timestamp instrumentDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	 	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
	 		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
	 		  try {
	 			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
	 			/*if(UtilValidate.isNotEmpty(chequeDate)){
	 				instrumentDate = new java.sql.Timestamp(sdf.parse(chequeDate).getTime());
	 			  }*/
	 				  
	 		  } catch (ParseException e) {
	 			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	             // effectiveDate = UtilDateTime.nowTimestamp();
	 		  } catch (NullPointerException e) {
	 			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	              //effectiveDate = UtilDateTime.nowTimestamp();
	 		  }
	 	  }
	 	  if (boothId == "") {
	 			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
	 			return "error";
	 	  }
	 	  
	 	 if (UtilValidate.isNotEmpty(request.getAttribute("estimatedDeliveryDate"))) {
	 		effectiveDate = (Timestamp) request.getAttribute("estimatedDeliveryDate");
	 	 }
	     // Get the parameters as a MAP, remove the productId and quantity params.
	 	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	 	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	 	  if (rowCount < 1) {
	 		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
	 		  return "success";
	 	  }
	 	  try{
	 		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
	 		routeId=facility.getString("parentFacilityId");
	 		  if(UtilValidate.isEmpty(facility)){
	 			  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
	 			  return "error";
	 		  }
	 		  
	 		  //here is the temporary list for grouping of non restricted categories in facility 
	 		  
	 		  List categoryList = FastList.newInstance(); 
	 		  categoryList.add("BYPROD_SO");
	 		  //payment restriction not required....
	 		  /*if(categoryList.contains(facility.getString("categoryTypeEnum"))){
	 		   	 if(amount.compareTo(totAmt) != 0){
		  			  Debug.logError("Need to pay Sale Amount of ("+totAmt+"/-).", module);
			  		  request.setAttribute("_ERROR_MESSAGE_", "Need to pay Sale Amount of ("+totAmt+"/-).");
			  		  return "error";
	 			  }
	 		  }*/
	 			  
	 	  }catch (GenericEntityException e) {
	 		  Debug.logError(e, "Booth does not exist", module);
	 		  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+ boothId +"'"+" does not exist");
	 		  return "error";
	 	  }
	 	  try {
	 		  List conditionList =FastList.newInstance();
	 		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));			 
	         conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId),EntityOperator.OR,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
	         
			EntityCondition subCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
	 		  subscriptionList=delegator.findList("SubscriptionAndFacility", subCond, null, null, null, false);
	 		  subscriptionList = EntityUtil.filterByDate(subscriptionList ,effectiveDate);
	 		  if(UtilValidate.isEmpty(subscriptionList)){
	 			  request.setAttribute("_ERROR_MESSAGE_", "Booth subscription does not exist");
	 			  return "error";     		
	 		  }
	 		  subscription = EntityUtil.getFirst(subscriptionList);
	 	  }  catch (GenericEntityException e) {
	 		  Debug.logError(e, "Problem getting Booth subscription", module);
	 		  request.setAttribute("_ERROR_MESSAGE_", "Problem getting Booth subscription");
	 		  return "error";
	 	  }
	 	// attempt to create a Shipment entity       
	     
	      List shipmentList=FastList.newInstance();
	      Timestamp dayBegin = UtilDateTime.getDayStart(effectiveDate, TimeZone.getDefault(), locale);
	      Timestamp dayEnd = UtilDateTime.getDayEnd(effectiveDate, TimeZone.getDefault(), locale);
	      List conditionList = FastList.newInstance();
	      
	      if(shipmentTypeId.equals("RM_DIRECT_SHIPMENT")){//for Direct sale Each Time we will create Shipment ...
	   	  if(UtilValidate.isEmpty(shipmentId)){
	   	   GenericValue newDirShip = delegator.makeValue("Shipment");        	 
	   	   newDirShip.set("estimatedShipDate", effectiveDate);
	   	   newDirShip.set("shipmentTypeId", shipmentTypeId);
	   	   newDirShip.set("statusId", "GENERATED");
	   	   newDirShip.set("originFacilityId", boothId);
	   	   newDirShip.set("routeId", routeId);
	   	   newDirShip.set("createdDate", nowTimeStamp);
	   	   newDirShip.set("createdByUserLogin", userLogin.get("userLoginId"));
	   	   newDirShip.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            try {
	                delegator.createSetNextSeqId(newDirShip);            
	               shipmentId = (String) newDirShip.get("shipmentId");
	            } catch (GenericEntityException e) {
	                Debug.logError(e, module);
	                request.setAttribute("_ERROR_MESSAGE_", "un able to create shipment id for DirectOrder.");
	    			return "error";                 
	            }  
	   	  }
	      }else{//for Gatepass Type same shipment need to be considered
	   	   // lets get the shipment if already exits else create new one
		  	shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, UtilDateTime.getDayStart(effectiveDate), UtilDateTime.getDayEnd(effectiveDate), shipmentTypeId);
		  	if(UtilValidate.isEmpty(shipmentList)){
	       	 GenericValue newEntity = delegator.makeValue("Shipment");        	 
	            newEntity.set("estimatedShipDate", effectiveDate);
	            newEntity.set("shipmentTypeId", shipmentTypeId);
	            newEntity.set("statusId", "GENERATED");
	            newEntity.set("routeId", routeId);
	            newEntity.set("createdDate", nowTimeStamp);
	            newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	            newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            
	            try {
	                delegator.createSetNextSeqId(newEntity);            
	               shipmentId = (String) newEntity.get("shipmentId");
	               
	            } catch (GenericEntityException e) {
	                Debug.logError(e, module);
	                request.setAttribute("_ERROR_MESSAGE_", "un able to create shipment id.");
	                
	    			return "error";                 
	            }  
	       	
	       }else{
	       	shipmentId = (String)shipmentList.get(0);
	       }
	      }
	       request.setAttribute("shipmentId",shipmentId);
	       if(!shipmentTypeId.equals("RM_DIRECT_SHIPMENT")){//other than this shipment this need to invoke...
	       	List orderList=null;
	       conditionList.clear();
			  conditionList = UtilMisc.toList(
					  EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
			  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
			  conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
			  conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
			  EntityCondition ohCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);	
			try {
				orderList = delegator.findList("OrderHeaderFacAndItemBillingInv", ohCondition, null, UtilMisc.toList("-orderId"), null, false);
				if (UtilValidate.isNotEmpty(orderList)) {
					request.setAttribute("_ERROR_MESSAGE_", "Only One GatePass allowed Each GatePass Type........");
					TransactionUtil.rollback();
					return "error";
				}
			}catch (GenericEntityException e) {
	                Debug.logError(e, module);
	                request.setAttribute("_ERROR_MESSAGE_", "Problem while getting order.");
	    			return "error";                 
	            }  
	       }
			  conditionList.clear();
	 	  List<GenericValue> subscriptionProductsList =FastList.newInstance();
	 	  
	 	  for (int i = 0; i < rowCount; i++) {
	 		  GenericValue subscriptionFacilityProduct = delegator.makeValue("SubscriptionFacilityAndSubscriptionProduct");
	 		  subscriptionFacilityProduct.set("facilityId", subscription.get("facilityId"));
	 		  subscriptionFacilityProduct.set("subscriptionId", subscription.get("subscriptionId"));
	 		  subscriptionFacilityProduct.set("categoryTypeEnum", subscription.get("categoryTypeEnum"));
	 		  subscriptionFacilityProduct.set("ownerPartyId", subscription.get("ownerPartyId"));
	 		  subscriptionFacilityProduct.set("productSubscriptionTypeId", productSubscriptionTypeId);
	 		  
	 		  Map<String  ,Object> productQtyMap = FastMap.newInstance();	  		  
	 		 
	 		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	 		  if (paramMap.containsKey("productId" + thisSuffix)) {
	 			  productId = (String) paramMap.get("productId" + thisSuffix);
	 		  }
	 		  else {
	 			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
	 			  return "error";			  
	 		  }
	 		  	  
	 		  
	 		  if (paramMap.containsKey("quantity" + thisSuffix)) {
	 			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
	 		  }
	 		  else {
	 			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
	 			  return "error";			  
	 		  }		  
	 		  if (quantityStr.equals("")) {
	 			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
	 			  return "error";	
	 		  }
	 		  try {
	 			  quantity = new BigDecimal(quantityStr);
	 		  } catch (Exception e) {
	 			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
	 			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
	 			  return "error";
	 		  } 
	 		
	 /*		try {//to Caliculate Leaks  for BMP 200 ml packet
				GenericValue boothDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), true);
				String categoryTypeEnum=boothDetail.getString("categoryTypeEnum");
				String schemeTypeId=boothDetail.getString("schemeTypeId");
				if(UtilValidate.isNotEmpty(schemeTypeId)){
					
					if(categoryTypeEnum.equals("FRANCHISE") && schemeTypeId.equals("LEAK")){
						if((quantity.compareTo(new BigDecimal(100)) >= 0)&&(leakProductList.contains(productId))){
							butterLeakQty=new BigDecimal(quantity.multiply(new BigDecimal(0.01)).intValue());
							//adding leak ProductSubscription for Each for Extra Quantity
							  GenericValue leakSubscriptionProduct = delegator.makeValue("SubscriptionFacilityAndSubscriptionProduct");
							  leakSubscriptionProduct.set("facilityId", subscription.get("facilityId"));
							  leakSubscriptionProduct.set("subscriptionId", subscription.get("subscriptionId"));
							  leakSubscriptionProduct.set("categoryTypeEnum", subscription.get("categoryTypeEnum"));
							  leakSubscriptionProduct.set("ownerPartyId", subscription.get("ownerPartyId"));
							  leakSubscriptionProduct.set("productSubscriptionTypeId", "LEAK");
							  leakSubscriptionProduct.set("productId", productId);
							  leakSubscriptionProduct.set("quantity", butterLeakQty);
						  		subscriptionProductsList.add(leakSubscriptionProduct);
							//quantity=quantity.add(butterLeakQty);
						}
					}	
				}
			}catch (Exception e) {
				Debug.logError(e, "unable to get the route details ", module);
				request.setAttribute("_ERROR_MESSAGE_","unable to get the Franchise Booth details");
				return "error";
			}*/
	 		subscriptionFacilityProduct.set("productId", productId);
	 		subscriptionFacilityProduct.set("quantity", quantity);
	 		subscriptionProductsList.add(subscriptionFacilityProduct);
	 	  }//end row count for loop
	 	  
	 	 if( UtilValidate.isEmpty(subscriptionProductsList)){
	 		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	 		 request.setAttribute("_ERROR_MESSAGE_", "No rows to process, as rowCount =  :" + rowCount);
	 		  return "success";
	 	 }
	 	 List<String> orderBy = UtilMisc.toList("subscriptionId", "productSubscriptionTypeId","-productId"); 
	 	subscriptionProductsList=EntityUtil.orderBy(subscriptionProductsList,orderBy);
	 	
	    List<GenericValue> orderSubProdsList = FastList.newInstance();  
		 Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);	  	
	 	 processChangeIndentHelperCtx.put("shipmentId", shipmentId);
	 	 processChangeIndentHelperCtx.put("estimatedDeliveryDate", effectiveDate);
	 	 processChangeIndentHelperCtx.put("salesChannel", salesChannel);
	 	 String orderId = null;
	 	 String invoiceId = null;
		   String tempSubId = "";
	      String tempTypeId = "";
	      String subId;
	      String typeId;
	    for (int j = 0; j < subscriptionProductsList.size(); j++) {
	    	subId = subscriptionProductsList.get(j).getString("subscriptionId");
	    	typeId = subscriptionProductsList.get(j).getString("productSubscriptionTypeId");
	        	if (tempSubId == "") {
	        		tempSubId = subId;
	        		tempTypeId = typeId;        		
	        	}
	            /*condition: "!(typeId.startsWith(tempTypeId))" is to generate same order for CASH_FS and CASH*/
	        	if (!tempSubId.equals(subId) || (!tempTypeId.equals(typeId))) {
					
						processChangeIndentHelperCtx.put("subscriptionProductsList", orderSubProdsList);
						processChangeIndentHelperCtx.put("shipmentId" , shipmentId);
						result = createSalesOrderSubscriptionProductType(dctx, processChangeIndentHelperCtx);
						if (ServiceUtil.isError(result)) {
	            			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
	            			  request.setAttribute("_ERROR_MESSAGE_", "Unable to generate order  For :" + boothId);
	            			return "error";
	            			//break;
	            		}
						orderId = (String) result.get("orderId");
			  			 invoiceId = (String) result.get("invoiceId");
			  			 partyIdFrom = (String) result.get("partyId");
			  			invoices.add(invoiceId);
						
	        		// quantity = (BigDecimal)result.get("quantity");                		
	        		orderSubProdsList.clear();
	        		tempSubId = subId;
	        		tempTypeId = typeId;
	        	}
	        	orderSubProdsList.add(subscriptionProductsList.get(j));
	    }
	    if (orderSubProdsList.size() > 0) {
			
				processChangeIndentHelperCtx.put("subscriptionProductsList", orderSubProdsList);
				processChangeIndentHelperCtx.put("shipmentId" , shipmentId);
				result = createSalesOrderSubscriptionProductType(dctx, processChangeIndentHelperCtx); 
				if (ServiceUtil.isError(result)) {
	    			Debug.logError("Unable to generate order: " + ServiceUtil.getErrorMessage(result), module);
	    		    request.setAttribute("_ERROR_MESSAGE_", "Problem creating Indent For AdhocSale :" + boothId);
	    		    return "error";
				} 
				orderId = (String) result.get("orderId");
	 			 invoiceId = (String) result.get("invoiceId");
	 			 partyIdFrom = (String) result.get("partyId");
	 			invoices.add(invoiceId);
				 
	    }
	 	 
	 	 /*if(amount.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(amount)){
	 		//payment Application
		  	Map input = UtilMisc.toMap("userLogin", userLogin, "partyId", partyIdFrom, "facilityId", boothId, "organizationPartyId", "Company", "effectiveDate", UtilDateTime.getDayStart(effectiveDate), "paymentDate", effectiveDate, "paymentTypeId", "SALES_PAYIN", "paymentMethodTypeId", "CASH_NELLORE_PAYIN", "statusId", "PMNT_RECEIVED", "amount", amount, "invoices", invoices);
	        String paymentId = null;
	        try {
	        	resultMap = dispatcher.runSync("createPaymentAndApplicationForInvoices", input);
	        	if (ServiceUtil.isError(resultMap)) {
	        		Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	        		request.setAttribute("_ERROR_MESSAGE_","ERROR While Making Payment...!");
	        		return "error";
	        	}
	        	paymentId = (String) resultMap.get("paymentId");	
	        	result.put("paymentId", paymentId);
	        }catch (GenericServiceException e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
	        }
	        result = ServiceUtil.returnSuccess("Payment successfully done For Party: "+boothId);
		 }*/
	   
		request.setAttribute("supplyDate", effectiveDate);
	 	request.setAttribute("_EVENT_MESSAGE_", "Successfully made entry from party: "+boothId);	  	 
	 	return "success";
	 	}	
	 public static Map<String, Object> cancelAdhocSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();       
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map<String, Object> result = new HashMap<String, Object>();
	        String orderId = (String) context.get("orderId");
	        String statusId = (String) context.get("statusId");
	        String shipmentId = (String) context.get("shipmentId");
	        String originFacilityId = (String) context.get("originFacilityId");
	        if(UtilValidate.isEmpty(orderId)){
				return ServiceUtil.returnError("orderId is null" + orderId);
	        }
	        else{
	        	List<GenericValue> orderItems = null;
		  		try{
		  		  List conditionList = UtilMisc.toList(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
				  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
				  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		  			orderItems = delegator.findList("OrderHeader",condition, null, null, null, false);
	     		} catch (GenericEntityException e) {
	     			Debug.logError(e, module);
	        		return ServiceUtil.returnError("Error in fetching Order :" + orderId);
			    }
	     		if(UtilValidate.isEmpty(orderItems)){
	     			Debug.logError("Error ", module);
	        		return ServiceUtil.returnError("No Order Items available to Cacnel Shipment:" + shipmentId+" for BoothId:"+originFacilityId);
	     		}else{ 	
	     			try{
		        		dispatcher.runSync("cancelLMSShipmentInternal", UtilMisc.toMap("shipmentId", shipmentId,"userLogin", userLogin));    		
		        	}catch (GenericServiceException e) {
		    			// TODO: handle exception
		        		Debug.logError("Unable to get records from DataBase To cancel Shipment "+e, module);
		        		return ServiceUtil.returnError(e.getMessage()); 
		    		}   
		        	
	     			/*for (GenericValue orderItem : orderItems) {
	     				orderId=orderItem.getString("orderId");
	     				boolean cancelled = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);//order Cancelation helper
	     				List<GenericValue> OrderItemBillingList = null;
	     		        String invoiceId = null;
	     		  		try{
	     		  			OrderItemBillingList = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	     		 		} catch (GenericEntityException e) {
	     		 			Debug.logError(e, module);
	     		    		return ServiceUtil.returnError("Error in fetching Order Item billing :" + orderId);
	     			    }
	     		 		if(UtilValidate.isEmpty(OrderItemBillingList)){
	     		 			Debug.logError("OrderItemBillingList is empty", module);
	     		    		return ServiceUtil.returnError("No invoice found for order :" + orderId);
	     		 		}
	     		 		else{
	     		 			invoiceId = OrderItemBillingList.get(0).getString("invoiceId");
	     		 			
	     		 			Map<String, Object> cancelInvoiceInput = FastMap.newInstance();
	     		 	       	cancelInvoiceInput.put("invoiceId", invoiceId);
	     		 	        cancelInvoiceInput.put("statusId", "INVOICE_CANCELLED");
	     		 	       	cancelInvoiceInput.put("userLogin", userLogin);
	     		 	        Map<String, Object> resultMap = null;
	     		 	        try {
	     		 	        	resultMap = dispatcher.runSync("setInvoiceStatus", cancelInvoiceInput);
	     		 	        	if(ServiceUtil.isError(resultMap)){
	     		 	        		Debug.logError("Error in service setInvoiceStatus while cancelling invoice", module);
	     		 	 	        	return ServiceUtil.returnError("Error in service setInvoiceStatus while cancelling invoice :" + invoiceId);
	     		 	        	}
	     		 	        } catch (GenericServiceException e) {
	     		 	        	Debug.logError(e, module);
	     		 	        	return ServiceUtil.returnError("Error in cancelling invoice :" + invoiceId);
	     		 	        }
	     		 	        Debug.log("invoiceId cancelled is : "+invoiceId);
	     		 		}
	     			 }*/
	     		}
			}
	        return ServiceUtil.returnSuccess("Order Canceled Successfully for Booth:" +originFacilityId+" !");
	  
	    }
	
	/*   migrated services from product and order */
     
	    public static String processDispatchReconcilMIS(HttpServletRequest request, HttpServletResponse response) {
	    	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	    	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    	  Locale locale = UtilHttp.getLocale(request);
	    	  String routeId = (String) request.getParameter("routeId");
	    	  String tripId = (String) request.getParameter("tripId");
	    	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
	    	  String productId = null;
	    	  String quantityStr = null;
	    	  String sequenceNum = null;	  
	    	  Timestamp effectiveDate=null;
	    	  BigDecimal quantity = BigDecimal.ZERO;
	    	  List conditionList =FastList.newInstance();
	    	  
	    	  Map<String, Object> result = ServiceUtil.returnSuccess();
	    	  HttpSession session = request.getSession();
	    	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    	  GenericValue facility = null;
	    	  List custTimePeriodList = FastList.newInstance();
	    	  String shipmentId = "";
	    	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
	    		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");             
	    		  try {
	    			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
	    		  } catch (ParseException e) {
	    			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	    		  } catch (NullPointerException e) {
	    			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
	    		  }
	    	  }
	    	  if (routeId == "") {
	    		request.setAttribute("_ERROR_MESSAGE_","Route Id is empty");
	    		return "error";
	    	  }
	        
	        // Get the parameters as a MAP, remove the productId and quantity params.
	    	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	    	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	    	  if (rowCount < 1) {
	    		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	    		  return "success";
	    	  }
	    	  try{
	    		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",routeId), false);
	    		  if(UtilValidate.isEmpty(facility)){
	    			  request.setAttribute("_ERROR_MESSAGE_", "Route"+" '"+routeId+"'"+" does not exist");
	    			  return "error";
	    		  }
	    	  }catch (GenericEntityException e) {
	    		  Debug.logError(e, "Route does not exist", module);
	    		  request.setAttribute("_ERROR_MESSAGE_", "Route"+" '"+routeId+"'"+" does not exist");
	    		  return "error";
	    	  }
	    	  
	    	  try {
	    		  conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
	    		  if(UtilValidate.isNotEmpty(tripId)){
	    			  conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	    		  }
	    		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	    		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(effectiveDate)));
	    		  conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(effectiveDate)));
	  		  	  EntityCondition shipCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
	  		  	  List<GenericValue> shipmentList = delegator.findList("Shipment", shipCond, UtilMisc.toSet("shipmentId"), null, null, false);
	    		  if(UtilValidate.isEmpty(shipmentList)){
	    			  request.setAttribute("_ERROR_MESSAGE_", "This Route has no shipments for the day");
	    			  return "error";     		
	    		  }
	    		  shipmentId = (String)((GenericValue)EntityUtil.getFirst(shipmentList)).get("shipmentId");
	    	  }catch (GenericEntityException e) {
	    		  Debug.logError(e, "Problem getting Booth subscription", module);
	    		  request.setAttribute("_ERROR_MESSAGE_", "Problem getting Booth subscription");
	    		  return "error";
	    	  }
	    	
	    	  List<Map>productQtyList =FastList.newInstance();
	    	  for (int i = 0; i < rowCount; i++) {
	    		  Map<String  ,Object> productQtyMap = FastMap.newInstance();
	    		  
	    		  List<GenericValue> subscriptionProductsList = FastList.newInstance();
	    		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
	    		  if (paramMap.containsKey("productId" + thisSuffix)) {
	    			  productId = (String) paramMap.get("productId" + thisSuffix);
	    		  }
	    		  else {
	    			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
	    			  return "error";			  
	    		  }
	    		  if (paramMap.containsKey("quantity" + thisSuffix)) {
	    			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
	    		  }
	    		  else {
	    			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
	    			  return "error";			  
	    		  }		  
	    		  if (quantityStr.equals("")) {
	    			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
	    			  return "error";	
	    		  }
	    		  try {
	    			  quantity = new BigDecimal(quantityStr);
	    		  } catch (Exception e) {
	    			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
	    			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
	    			  return "error";
	    		  } 
	    		  
	    		  productQtyMap.put("productId", productId);
	    		  productQtyMap.put("quantity", quantity);
	    		  productQtyList.add(productQtyMap);
	    	  }//end row count for loop
	    	  
	    	  Map processDispatchReconcilHelperCtx = UtilMisc.toMap("userLogin",userLogin);
	    	  processDispatchReconcilHelperCtx.put("shipmentId", shipmentId);
	    	  processDispatchReconcilHelperCtx.put("routeId", routeId);
	    	  processDispatchReconcilHelperCtx.put("effectiveDate", effectiveDate);
	    	  processDispatchReconcilHelperCtx.put("productQtyList", productQtyList);
	    	  try{
	    		  result = dispatcher.runSync("processDispatchReconcilHelper",processDispatchReconcilHelperCtx);
	    		
	    		  if (ServiceUtil.isError(result)) {
	    			  String errMsg =  ServiceUtil.getErrorMessage(result);
	    			  Debug.logError(errMsg , module);
	    			  request.setAttribute("_ERROR_MESSAGE_",errMsg);
	    			  return "error";
	    		  }
	    	  }catch (Exception e) {
	    		  	Debug.logError(e, "Problem updating ItemIssuance for Route " + routeId, module);     
	    	  		request.setAttribute("_ERROR_MESSAGE_", "Problem updating ItemIssuance for Route " + routeId);
	    	  		return "error";			  
	    	  }	 
	    	  return "success";     
	    }
	    public static Map<String ,Object>  processDispatchReconcilHelper(DispatchContext dctx, Map<String, ? extends Object> context){
			  Delegator delegator = dctx.getDelegator();
		      LocalDispatcher dispatcher = dctx.getDispatcher();       
		      GenericValue userLogin = (GenericValue) context.get("userLogin");
		      Map<String, Object> result = ServiceUtil.returnSuccess();
		      String shipmentId = (String)context.get("shipmentId");
		      String routeId = (String)context.get("routeId");
		      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
		      List<Map> productQtyList = (List)context.get("productQtyList");
		      Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		      
		      try{
		    	  List conditionList = FastList.newInstance();
		    	  conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		    	  conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(effectiveDate)));
		    	  conditionList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(effectiveDate)));
		    	  EntityCondition issueCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    	  List<GenericValue> itemIssuance = delegator.findList("ItemIssuance", issueCond, null, null, null, false);
		    	  for(int i=0; i< productQtyList.size() ; i++){
		    		  Map productQtyMap = productQtyList.get(i);
		    		  String productId = (String)productQtyMap.get("productId");
		    		  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");

		    		  List<GenericValue> prodItemIssue = (List)EntityUtil.filterByCondition(itemIssuance, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		    		  GenericValue prodItem = EntityUtil.getFirst(prodItemIssue);
		    		  if(UtilValidate.isEmpty(prodItem)){
		    			  GenericValue newEntryIssuance = delegator.makeValue("ItemIssuance");
		    			  newEntryIssuance.set("shipmentId", shipmentId);
		    			  newEntryIssuance.set("productId", productId);
		    			  newEntryIssuance.set("quantity", quantity);
		    			  newEntryIssuance.set("issuedDateTime", UtilDateTime.getDayStart(effectiveDate));
		    			  newEntryIssuance.set("issuedByUserLoginId", userLogin.get("userLoginId"));
		    			  newEntryIssuance.set("modifiedByUserLoginId", userLogin.get("userLoginId"));
		    			  newEntryIssuance.set("modifiedDateTime", nowTimestamp);
		    			  delegator.createSetNextSeqId(newEntryIssuance);
		    		  }
		    		  else{
		    			  prodItem.put("quantity", quantity);
		    			  prodItem.put("modifiedByUserLoginId", userLogin.get("userLoginId"));
		    			  prodItem.put("modifiedDateTime", nowTimestamp);
		    			  prodItem.store();
		    		  }
			  	  }
			  }catch (Exception e) {
				  Debug.logError(e, "Problem updating itemIssuance for Route " + routeId, module);		  
				  return ServiceUtil.returnError("Problem updating Dispatch Reconciliation for Route " + routeId);			  
			  }
			  return result;  
	    }
	    public static Map<String, Object> createOrUpdateInstBillingPeriod(DispatchContext ctx,Map<String, Object> context) {
	        Map<String, Object> finalResult = FastMap.newInstance();
	        Map<String, Object> result = FastMap.newInstance();
	        Delegator delegator = ctx.getDelegator();
	        LocalDispatcher dispatcher = ctx.getDispatcher();
	        Locale locale = (Locale) context.get("locale");
	        String facilityId = (String) context.get("facilityId");
	        String periodTypeId = (String) context.get("periodTypeId");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        Timestamp thruDate = (Timestamp) context.get("thruDate");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        List<GenericValue> FacilityCustBillingList = null;
	        if(fromDate==null){
	        	fromDate= UtilDateTime.nowTimestamp();
	        }
	        if(UtilValidate.isNotEmpty(thruDate)){
	        	thruDate=UtilDateTime.getDayEnd(thruDate);
	        }
	        fromDate=UtilDateTime.getDayStart(fromDate);
	        
	        if(fromDate.before(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()))){
	        	Debug.logError("From date  shoud be greater than current date : "+fromDate+ "\t",module);
				return ServiceUtil.returnError("From date  shoud be greater than current date  : "+fromDate);
	        }
	        List conditionList = UtilMisc.toList(
	                EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	                conditionList.add( EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
	                EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  
	                
				try {
					FacilityCustBillingList = delegator.findList("FacilityCustomBilling", condition, null, null, null, false);
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
		            return ServiceUtil.returnError(e.getMessage());
				} 
                List<GenericValue> facilityCustBill= EntityUtil.filterByDate(FacilityCustBillingList,fromDate);
 	            if(UtilValidate.isNotEmpty(facilityCustBill)){
 			       try {
 			    	    conditionList.clear();
 	 			       	GenericValue facilityList = facilityCustBill.get(0);
 	 			        facilityList.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1), TimeZone.getDefault(), locale));
						facilityList.store();
					}catch (GenericEntityException e) {
						Debug.logError("Error in Facility Custom Billing : "+facilityId+ "\t"+e.toString(),module);
						return ServiceUtil.returnError(e.getMessage());
					}
 	            }
     		   	GenericValue newEntity = delegator.makeValue("FacilityCustomBilling");
     	        newEntity.set("facilityId", facilityId);
     	        newEntity.set("periodTypeId", periodTypeId);
     	        newEntity.set("fromDate", fromDate);
     	        newEntity.set("thruDate", thruDate);
     	        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
   	            newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
   	            newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
   	            newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
 		        try {
 					delegator.create(newEntity);
 				}catch (GenericEntityException e) {
 					Debug.logError("Error in creating Facility Custom Billing: "+facilityId+ "\t"+e.toString(),module);
 					return ServiceUtil.returnError(e.getMessage());
 				}
			return result;
	    }
	    public static Map<String, Object> getFacilityRates(DispatchContext dctx, Map<String, ? extends Object> context){
		    Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String facilityId = (String) context.get("facilityId");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        String rateTypeId = (String) context.get("rateTypeId");
	        List facilityRatesList=null;
	        BigDecimal rateAmount=BigDecimal.ZERO;
	        Map result = ServiceUtil.returnSuccess();
	        if(fromDate==null){
	        	fromDate= UtilDateTime.nowTimestamp();
	        }
	        fromDate=UtilDateTime.getDayStart(fromDate);
	        List conditionList = UtilMisc.toList(
	        		EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
            		conditionList.add( EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
	                EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				try {
					facilityRatesList = delegator.findList("FacilityRate", condition, null, null, null, false);
					List<GenericValue> facilityRates = EntityUtil.filterByDate(facilityRatesList,fromDate);
					 if(UtilValidate.isNotEmpty(facilityRates) && facilityRates.size()>0){
						 GenericValue facilityRateDesc = delegator.findOne("RateType", UtilMisc.toMap("rateTypeId", rateTypeId), false);	
				         return ServiceUtil.returnError("Error in getting "+facilityRateDesc.get("description"));
			         }
						GenericValue facilityRate = facilityRates.get(0);
				       	rateAmount = facilityRate.getBigDecimal("rateAmount");
				       	rateTypeId = facilityRate.getString("rateTypeId");
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
		            return ServiceUtil.returnError(e.getMessage());
				} 
	        return result;
	    }
	    
	    public static Map<String, Object> createOrUpdateFacilityRate(DispatchContext ctx,Map<String, Object> context) {
	        Map<String, Object> finalResult = FastMap.newInstance();
	        Map<String, Object> result = FastMap.newInstance();
	        Delegator delegator = ctx.getDelegator();
	        LocalDispatcher dispatcher = ctx.getDispatcher();
	        Locale locale = (Locale) context.get("locale");
	        String facilityId = (String) context.get("facilityId");
	        String productId = (String) context.get("productId");
	        String rateTypeId = (String) context.get("rateTypeId");
	        BigDecimal rateAmount = (BigDecimal) context.get("rateAmount");
	        String rateCurrencyUomId = (String) context.get("rateCurrencyUomId");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        Timestamp thruDate = (Timestamp) context.get("thruDate");
	        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
	        List<GenericValue> facilityRateList = FastList.newInstance();
	        List<GenericValue> activeFacilityRate = FastList.newInstance();
	        List<GenericValue> futureFacilityRate = FastList.newInstance();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Timestamp tempfromDate=null;
	        boolean isNewFacility = true;
	        if(fromDate==null){
	        	fromDate= UtilDateTime.nowTimestamp();
	        }
	        if(UtilValidate.isNotEmpty(thruDate)){
	        	thruDate=UtilDateTime.getDayEnd(thruDate);
	        }
	        fromDate=UtilDateTime.getDayStart(fromDate);
	        if(fromDate.before(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()))){
	        	Debug.logError("From date  shoud be greater than current date : "+fromDate+ "\t",module);
				return ServiceUtil.returnError("From date  shoud be greater than current date  : "+fromDate);
	        }
	        List conditionList = UtilMisc.toList(
	                EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	                conditionList.add( EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
	                conditionList.add( EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	                conditionList.add( EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS, supplyTypeEnumId));
	                conditionList.add( EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
	                EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  
	               
				try {
					 facilityRateList = delegator.findList("FacilityRate", condition, null, UtilMisc.toList("-fromDate"), null, false);
					 activeFacilityRate = EntityUtil.filterByDate(facilityRateList, fromDate);
					 futureFacilityRate = EntityUtil.filterByCondition(facilityRateList, EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, fromDate));
					 if(UtilValidate.isNotEmpty(futureFacilityRate)){
							GenericValue facilityRateDesc = delegator.findOne("RateType", UtilMisc.toMap("rateTypeId", rateTypeId), false);	
					        return ServiceUtil.returnError("Cannot create "+facilityRateDesc.get("description"));
					 }
					 GenericValue newEntity = delegator.makeValue("FacilityRate");
					 if(UtilValidate.isNotEmpty(activeFacilityRate)){
							GenericValue activeRate = activeFacilityRate.get(0);
					       	tempfromDate = activeRate.getTimestamp("fromDate");
					    	if(fromDate.compareTo(UtilDateTime.getDayStart(tempfromDate))>0){
					    		activeRate.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1), TimeZone.getDefault(), locale));
					       		activeRate.store();
						    }
					       	if(fromDate.compareTo(UtilDateTime.getDayStart(tempfromDate))==0){
					       		activeRate.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					       		activeRate.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					       		activeRate.set("rateAmount",rateAmount);
					       		activeRate.store();
					       		isNewFacility=false;
					       	}
					 }
				if(isNewFacility ){
	     	        newEntity.set("facilityId", facilityId);
	     	        newEntity.set("productId", productId);
	     	        newEntity.set("rateTypeId", rateTypeId);
	     	        newEntity.set("supplyTypeEnumId", supplyTypeEnumId);
	     	        newEntity.set("rateCurrencyUomId", rateCurrencyUomId);
	     	        newEntity.set("rateAmount", rateAmount);
	     	        newEntity.set("fromDate", fromDate);
	     	        newEntity.set("thruDate", thruDate);
	     	        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
	    	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	 		        try {
	 					delegator.create(newEntity);
	 				}catch (GenericEntityException e) {
	 					Debug.logError("Error in creating Facility Rate: "+facilityId+ "\t"+e.toString(),module);
	 					return ServiceUtil.returnError(e.getMessage());
	 				}
				  }
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
		            return ServiceUtil.returnError(e.getMessage());
				} 
			return result;
	    }
}
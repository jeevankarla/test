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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;


import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class LmsServices {

    public static final String module = LmsServices.class.getName();
    
    private static BigDecimal ZERO = BigDecimal.ZERO;
    private static int decimals;
    private static int rounding;
    public static final String resource_error = "OrderErrorUiLabels";
    static {
        decimals = 1;//UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }    

    public static Map<String, Object> updateFacilityParty(DispatchContext dctx, Map context) {		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		String facilityId = (String) context.get("facilityId");
		String parentFacilityId = (String) context.get("parentFacilityId");
		String partyId = (String) context.get("partyId");
		String oldParentFacilityId = (String) context.get("oldParentFacilityId");
		String oldPartyId = (String) context.get("oldPartyId");
		java.util.Date todaysDate = UtilDateTime.nowDate();
		Timestamp fromDate = new Timestamp(todaysDate.getTime());
		Timestamp newFromDate = UtilDateTime.getDayStart(fromDate,+1);
		Timestamp existingFromDate = null;
		boolean isNewFacility = true;
		boolean needFacilityPartyUpdate = true;
		if(oldParentFacilityId != null){
			isNewFacility = false;
		}
		try {
			if(!isNewFacility){
				List conditionList = UtilMisc.toList(
		                EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		        conditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, oldParentFacilityId));
		        conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, oldPartyId));
		        conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "TRANSPORT_BOOTH"));
		        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
		        List<GenericValue> facilityParties = delegator.findList("FacilityParty", condition, null, null, null, false);
		        facilityParties = EntityUtil.filterByDate(facilityParties, todaysDate);
		        if(facilityParties.size()==1){
		        	GenericValue facilityParty = facilityParties.get(0);
		        	existingFromDate = (Timestamp) facilityParty.get("fromDate"); 
		        	//Check if the faciityParty is updated more than once on the same day!
		        	if(existingFromDate.equals(newFromDate) ){
		        		needFacilityPartyUpdate = false;
		        		//Check if owner partyId is updated more than once on the same day!
		        		if(!partyId.equals(oldPartyId)){
		        			delegator.removeAll(facilityParties);
		        			needFacilityPartyUpdate = true;
		        		}else{
		        			facilityParty.put("facilityIdTo", parentFacilityId);
		        		}
			        }
		        	else{
		        		facilityParty.put("thruDate", UtilDateTime.getDayEnd(fromDate));
		        	}
		        	facilityParty.store();
				}
		        else{
		        	Debug.logError("There are either more than one or less than one 'active routes' for the given Booth", module);
		        	return ServiceUtil.returnError("There are either more than one or less than one 'active routes' for the given Booth");
		        }
			}
		    
			if(isNewFacility || needFacilityPartyUpdate){
		        GenericValue newRouteFacility = delegator.makeValue("FacilityParty");
				newRouteFacility.put("facilityId", facilityId );
				newRouteFacility.put("facilityIdTo", parentFacilityId);
				newRouteFacility.put("roleTypeId", "TRANSPORT_BOOTH");
				newRouteFacility.put("partyId", partyId);
				newRouteFacility.put("fromDate", newFromDate);
				newRouteFacility.put("thruDate", null);
				newRouteFacility.create(); 
			}
			
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil.returnError("Error while populating FacilityParty" + e);
		}
	    result = ServiceUtil.returnSuccess("Booth "+facilityId+" is successfully updated");
		return result;
    }
    
    public static Map<String, Object> updateFacilityRoute(DispatchContext dctx, Map context) {		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		String routeId = (String) context.get("routeId");
		String fDate = (String) context.get("fromDate");
		Timestamp fromDate = null;
		if(UtilValidate.isNotEmpty(fDate)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				fromDate = new java.sql.Timestamp(sdf.parse(fDate).getTime());
				fromDate = UtilDateTime.getDayStart(fromDate);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ fDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ fDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ fDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
		if(UtilValidate.isEmpty(fromDate)){
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		Timestamp nowDate = UtilDateTime.nowTimestamp();
		Map<String, Object> resultMap = FastMap.newInstance();
		boolean createFlag = true;
		try {
				List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(fromDate))));
		        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		        List<GenericValue> facilityGroups = delegator.findList("FacilityGroupMember", condition, null, null, null, false);
		        facilityGroups = EntityUtil.filterByDate(facilityGroups, fromDate);
		        if(UtilValidate.isNotEmpty(facilityGroups)){
		        	GenericValue facilityGroup = facilityGroups.get(0);
		        	Timestamp froDate = facilityGroup.getTimestamp("fromDate");
		        	if(!(facilityGroup.getString("facilityGroupId")).equals(routeId)){
		        		if(froDate.compareTo(fromDate) == 0){
		        			facilityGroup.set("facilityGroupId", routeId);
			        		facilityGroup.store();
			        		createFlag = false;
		        		}
		        		else{
		        			facilityGroup.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(nowDate, -1)));
			        		facilityGroup.store();
			        		createFlag = true;
		        		}
			        }
		        }
		        if(createFlag){
		        	Map tempMap = FastMap.newInstance();
		        	tempMap.put("facilityId", facilityId);
		        	tempMap.put("facilityGroupId", routeId);
		        	tempMap.put("fromDate", fromDate);
		        	tempMap.put("userLogin", userLogin);
		        	resultMap = dispatcher.runSync("addFacilityToGroup", tempMap);
		        	if(ServiceUtil.isError(resultMap)){
		        		Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                    return resultMap;
		        	}
		        }
		        
		}catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil.returnError("Error while populating FacilityRoute" + e);
		}
	    result = ServiceUtil.returnSuccess("FacilityRoute is successfully updated");
		return result;
    }
    
    public static Map<String, Object> getFactoryStore(Delegator delegator){
        
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
    
    public static Map<String, Object> createFacilityPaymentDefault(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> resultMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String facilityId = (String) context.get("facilityId");
		String finAccountBranch = (String) context.get("finAccountBranch");
		String finAccountName = (String) context.get("finAccountName");
		String finAccountCode = (String) context.get("finAccountCode");
		String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
		String ifscCode = (String) context.get("ifscCode");
		String productStoreId = (String)(getFactoryStore(delegator)).get("factoryStoreId");
		String fDate = (String) context.get("fromDate");
		Timestamp fromDate = null;
		if(UtilValidate.isNotEmpty(fDate)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				fromDate = new java.sql.Timestamp(sdf.parse(fDate).getTime());
				fromDate = UtilDateTime.getDayStart(fromDate);
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: "+ fDate, module);
				return ServiceUtil.returnError("Cannot parse date string: "+ fDate);
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: "+ fDate, module);
				return ServiceUtil.returnError("Cannot parse empty date string ");
			}
		}
		if(UtilValidate.isEmpty(fromDate)){
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		try{
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			String partyId = (String) facility.get("ownerPartyId");
			List conditionList  = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyProfileDefault = delegator.findList("PartyProfileDefault", cond, null, null, null, false);
			partyProfileDefault = EntityUtil.filterByDate(partyProfileDefault, fromDate);
			boolean createEntry = true;
			if(UtilValidate.isNotEmpty(partyProfileDefault)){
				GenericValue partyProfile = EntityUtil.getFirst(partyProfileDefault);
				Timestamp startDate = partyProfile.getTimestamp("fromDate");
				if(startDate.compareTo(fromDate) == 0){
					partyProfile.set("defaultPayMeth", paymentMethodTypeId);
					partyProfile.store();
					createEntry = false;
				}
				else if(startDate.compareTo(fromDate)<0){
					partyProfile.set("thruDate", UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayEnd(fromDate), -1));
					partyProfile.store();
					createEntry = true;
				}
			}
			if(createEntry){
				GenericValue newEntity = delegator.makeValue("PartyProfileDefault");
		        newEntity.set("productStoreId",productStoreId);
		        newEntity.set("partyId",partyId);
		        newEntity.set("defaultPayMeth",paymentMethodTypeId);
		        newEntity.set("fromDate",fromDate);
		        newEntity.create();
			}
			if (UtilValidate.isNotEmpty(finAccountName)){
				 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", partyId, "finAccountBranch", finAccountBranch, "finAccountName", finAccountName,"finAccountCode", finAccountCode,"ifscCode", ifscCode);
				 resultMap = dispatcher.runSync("createFinAccount", input);
				 if (ServiceUtil.isError(resultMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                 return resultMap;
	             }
			 }
			
		}catch (Exception e) {
			Debug.logError(e, e.getMessage());
			return ServiceUtil.returnError("Error while populating Facility PaymentDefault" + e);
		}
		resultMap = ServiceUtil.returnSuccess("Facility PaymentDefault is successfully updated");
		return resultMap;
    }
    
    public static Map<String, Object> createBooth(DispatchContext dctx, Map context) {
	
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String ownerPartyId = null;
		String sequenceNum = null;
		String address1 = null;
		String address2 = null;
		String contactMechId = null;
		String partyId = null;
		String firstName  = null;
		String lastName = null;
		
		String facilityId = (String) context.get("facilityId");
		String parentFacilityId = (String) context.get("parentFacilityId");
		String categoryTypeEnum = (String) context.get("categoryTypeEnum");
		String groupName = (String) context.get("groupName");
		firstName = (String) context.get("firstName");
		lastName = (String) context.get("lastName");
		String middleName = (String) context.get("middleName");
		address1 = (String) context.get("address1");
		address2 = (String) context.get("address2");
		Map<String, Object> resultMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
	
		GenericValue parentFacility;
		GenericValue facility;
		try {
			facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			if(UtilValidate.isNotEmpty(facility)){
				Debug.logError("Booth Id Already Exists!", module);
				return ServiceUtil.returnError("Booth Id Already Exists!");
			}
			if(parentFacilityId == null){
				Debug.logError("Please Enter 'Parent Facility Id", module);
				return ServiceUtil.returnError("Please Enter 'Parent Facility Id'");
			}
			parentFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", parentFacilityId), false);
			if(UtilValidate.isEmpty(parentFacility)){
				Debug.logError("Invalid Route Id", module);
				return ServiceUtil.returnError("Invalid Route Id");
			}
			if(!(parentFacility.getString("facilityTypeId")).equals("ROUTE")){
				Debug.logError("Incorrect Route Id", module);
				return ServiceUtil.returnError("Incorrect Route Id");
			}
			if(categoryTypeEnum == null){
				Debug.logError("Category is missing", module);
				return ServiceUtil.returnError("Category is missing");
			}
			
			if(categoryTypeEnum.equals("VENDOR") || categoryTypeEnum.equals("FRANCHISEE")){
				if(firstName == null){
					Debug.logError("firstName is missing", module);
					return ServiceUtil.returnError("firstName is missing");
				}
				if(lastName == null){
					Debug.logError("lastName is missing", module);
					return ServiceUtil.returnError("lastName is missing");
				}
				Object tempInput = "PARTY_ENABLED";
				input = UtilMisc.toMap("firstName", firstName, "lastName", lastName, "middleName",middleName, "statusId", tempInput);
				resultMap = dispatcher.runSync("createPerson", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                    return resultMap;
                }
				ownerPartyId = (String) resultMap.get("partyId");
			}else {
				if(groupName == null){
					Debug.logError("groupName is missing", module);
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
					return ServiceUtil.returnError("groupName is missing");
				}
				input = UtilMisc.toMap("groupName",context.get("groupName"));
				resultMap = dispatcher.runSync("createPartyGroup", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                    return resultMap;
                }
				ownerPartyId = (String) resultMap.get("partyId"); 
			}
			Object tempInput = "BOOTH_OWNER";
			input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "roleTypeId", tempInput);
			resultMap = dispatcher.runSync("createPartyRole", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                return resultMap;
            }
			tempInput = "TRANSPORT_BOOTH";
			input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "roleTypeId", tempInput);
			resultMap = dispatcher.runSync("createPartyRole", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                return resultMap;
            }
			tempInput = "TRADE_CUSTOMER";
			input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "roleTypeId", tempInput);
			resultMap = dispatcher.runSync("createPartyRole", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                return resultMap;
            }
			if (UtilValidate.isNotEmpty(address1)){
				input = UtilMisc.toMap("userLogin", userLogin, "partyId",ownerPartyId, "address1",address1, "address2", address2, "city", (String)context.get("city"), "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", (String)context.get("postalCode"), "contactMechId", contactMechId);
				resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                return resultMap;
	            }
				contactMechId = (String) resultMap.get("contactMechId");
				 
				tempInput = "BILLING_LOCATION";
				input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",ownerPartyId, "contactMechPurposeTypeId", tempInput);
				resultMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
				if (ServiceUtil.isError(resultMap)) {
				    Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                return resultMap;
	            }
				partyId = (String) resultMap.get("partyId"); 
			 }
			 GenericValue newSubscription = delegator.makeValue("Subscription");
			 newSubscription.put("facilityId", facilityId);
			 if (UtilValidate.isEmpty((String)context.get("facilityName"))){
				Debug.logError("Name of the Booth is Missing", module);
			   	return ServiceUtil.returnError("Name of the Booth is Missing");
			 }
			 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "openedDate", UtilDateTime.nowTimestamp(), "facilityId", facilityId, "facilityTypeId", "BOOTH", "parentFacilityId", parentFacilityId, "categoryTypeEnum", categoryTypeEnum,"facilityName", (String)context.get("facilityName"), "description", (String)context.get("description"));
			 boolean isSeqNumNumeric=(StringUtils.isNumeric(facilityId));
			 
			 if(isSeqNumNumeric){		 
				 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "openedDate", UtilDateTime.nowTimestamp(), "facilityId", facilityId, "facilityTypeId", "BOOTH", "parentFacilityId", parentFacilityId, "categoryTypeEnum", categoryTypeEnum,"sequenceNum", facilityId,"facilityName", (String)context.get("facilityName"), "description", (String)context.get("description"));
			 }
			 resultMap =  dispatcher.runSync("createFacility", input);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                 return resultMap;
             }
			 String resultFacilityId = (String) resultMap.get("facilityId");
			 
			 /*enableLmsPmSales = delegator.findOne("TenantConfiguration", [propertyTypeEnumId:"LMS", propertyName:"enableLmsPmSales"], true);
			 if (enableLmsPmSales && enableLmsPmSales.propertyValue == "Y") {
			 	context.enableLmsPmSales = true;
			 }*/
			 Boolean enableLmsPmSalesEntry = Boolean.FALSE;
				try{
					 GenericValue tenantConfigEnableLmsPmSales = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableLmsPmSales"), false);
					 if (UtilValidate.isNotEmpty(tenantConfigEnableLmsPmSales) && (tenantConfigEnableLmsPmSales.getString("propertyValue")).equals("Y")) {
						 enableLmsPmSalesEntry = Boolean.TRUE;
					}
				 }catch (GenericEntityException e) {
					// TODO: handle exception
					 Debug.logError(e, module);
				}
			 if(enableLmsPmSalesEntry){
				 newSubscription.set("subscriptionTypeId", "AM");
				 delegator.createSetNextSeqId(newSubscription);
				 newSubscription.set("subscriptionTypeId", "PM");
				 delegator.createSetNextSeqId(newSubscription);
			 }else{
				 delegator.createSetNextSeqId(newSubscription);
			 }
			 input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "facilityId", facilityId, "parentFacilityId", parentFacilityId);
			 resultMap = dispatcher.runSync("updateFacilityParty", input);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                 return resultMap;
             }
			 result = ServiceUtil.returnSuccess("Booth "+resultFacilityId+ " is successfully created");
			 result.put("facilityId", resultFacilityId);
			 
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error while populating FacilityParty" + e);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createRoute(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String resultFacilityId = null;
		String routeName = null;
		String facilityId = (String) context.get("facilityId");
		String description = (String) context.get("description");
		routeName = (String) context.get("facilityName");
		String parentFacilityId = (String) context.get("parentFacilityId");
		BigDecimal facilitySize = (BigDecimal) context.get("facilitySize");
		
		String ownerPartyId = "COMPANY";
		
		GenericValue facility;
		try {
		    GenericValue parentFacility;
		    if(facilityId == null){
		    	Debug.logError("Please Enter 'Facility Id'", module);
		    	return ServiceUtil.returnError("Please Enter 'Facility Id'");
		    }
		    if(parentFacilityId == null){
		    	Debug.logError("Please Enter 'Parent Facility Id'", module);
		    	return ServiceUtil.returnError("Please Enter 'Parent Facility Id'");
		    }
		    parentFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", parentFacilityId), false);
			if (UtilValidate.isEmpty(parentFacility)){
				Debug.logError("Invalid Zone Id", module);
				return ServiceUtil.returnError("Invalid Zone Id");
			}
			if(!(parentFacility.getString("facilityTypeId")).equals("ZONE")){
				Debug.logError("Incorrect Zone Id", module);
				return ServiceUtil.returnError("Incorrect Zone Id");
			}
			facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			if(UtilValidate.isNotEmpty(facility)){
				Debug.logError("Route Id Already Exists!", module);
				return ServiceUtil.returnError("Route Id Already Exists!");
			}
		    String typeInput = "ROUTE";
			Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "openedDate", UtilDateTime.nowTimestamp(), "facilityId", facilityId, "facilityTypeId", typeInput, "parentFacilityId", parentFacilityId, "facilityName", routeName, "description", description, "facilitySize", facilitySize);   
			Map<String, Object> resultMap =  dispatcher.runSync("createFacility", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
				return resultMap;
            }
			resultFacilityId = (String) resultMap.get("facilityId");
		
		    result = ServiceUtil.returnSuccess("Route "+resultFacilityId+" is successfully created");
		    result.put("facilityId", resultFacilityId);
			
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Error while populating FacilityParty" + e);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		return result;
	}

    public static Map<String, Object> CreateTransporterDue(DispatchContext dctx, Map<String, ? extends Object> context){
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = ((String) context.get("facilityId")).toUpperCase();
        Timestamp supplyDate = UtilDateTime.nowTimestamp();
        supplyDate = (Timestamp) context.get("supplyDate");        
        BigDecimal amount = (BigDecimal) context.get("amount");
        String partyIdFrom = "Company";
        String partyId = "";
        String invoiceTypeId = "TRANSPORTER_IN";
        String invoiceItemTypeId = "TRANSPORTER_INV_ITEM";
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        String errorMsg = "Faild to create transporter due invoice, for the route  [" + facilityId + "-->" + amount  + "]";
        Map<String, Object> result =ServiceUtil.returnError(errorMsg, null, null, null); 
        
    	try {
    		GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
    		if(UtilValidate.isEmpty(facility)){
    			 Debug.logError("Transporter facility  not found", module);
    	         return ServiceUtil.returnError("Transporter facility  not found", null, null, result);
    		}
    		partyId = (String)facility.getString("ownerPartyId");	
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.toString(), null, null, result);
        }
    	String invoiceId = "";
        Map input = UtilMisc.toMap("userLogin", userLogin);
        input.put("invoiceTypeId", invoiceTypeId);        
        input.put("partyIdFrom", partyIdFrom);
        input.put("partyId", partyId);
        input.put("facilityId", facilityId);
        input.put("statusId", "INVOICE_IN_PROCESS");	
        input.put("currencyUomId", "INR");
        input.put("invoiceDate", supplyDate);
        input.put("dueDate", UtilDateTime.getNextDayStart(UtilDateTime.nowTimestamp())); 	        
       	try{
       		result = dispatcher.runSync("createInvoice", input);
       		if (ServiceUtil.isError(result)) {
       			Debug.logError(result.toString(), module);
				return ServiceUtil.returnError(errorMsg, null, null, result);
			}	        
			invoiceId = (String)result.get("invoiceId");			
			input.clear();
			input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);			
			input.put("invoiceItemTypeId", invoiceItemTypeId);
			//input.put("description", InvoicePayrolWorker.fetchInvoiceItemTypeDescription(dctx, invoiceItemTypeId));
			input.put("quantity", BigDecimal.ONE);
			input.put("amount", amount);
            result = dispatcher.runSync("createInvoiceItem", input);
            if (ServiceUtil.isError(result)) {
            	Debug.logError(result.toString(), module);
				return ServiceUtil.returnError("Faild to add invoice item to transporter due invoice ", null, null, result);
			}
            input.clear();
			input = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
			input.put("userLogin", userLogin);
			input.put("statusId","INVOICE_APPROVED");
            result = dispatcher.runSync("setInvoiceStatus",input);
         	if (ServiceUtil.isError(result)) {
         		Debug.logError(result.toString(), module);
                 return ServiceUtil.returnError(null, null, null, result);
             }
            
       	}catch (GenericServiceException e) {
			// TODO: handle exception
		}
       	result = ServiceUtil.returnSuccess();
       	result.put("invoiceId", invoiceId);
    	return result;
	}  
    
    public static Map<String, Object> createTransporterDuePayment(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = ((String) context.get("facilityId")).toUpperCase();            
        BigDecimal amount = (BigDecimal) context.get("amount");
        Locale locale = (Locale) context.get("locale");     
        String paymentMethodType = "TRANSPORTER_PAYIN";
        String paymentType = "TRANSPORTER_PAYIN";
        String partyIdTo ="Company";
        String paymentId = "";
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List exprListForParameters = FastList.newInstance();
        List invoiceList = FastList.newInstance();
        
		
		exprListForParameters.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));	
		
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF")));		
		/*exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));*/

		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

		EntityFindOptions findOptions = new EntityFindOptions();
		
		try{
			invoiceList = delegator.findList("Invoice", paramCond, null , null, findOptions, false);
			if(UtilValidate.isEmpty(invoiceList)){
				Debug.logError("paramCond==================== "+paramCond, module);
				Debug.logError("No dues found for the Booth "+facilityId, module);
				return ServiceUtil.returnError("No dues found for the transporter"+facilityId);
			}
		}catch(GenericEntityException e){
			Debug.logError(e, module);	
            return ServiceUtil.returnError(e.toString());			
		}
        
        try {
        	GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
        	String partyIdFrom = (String)facility.getString("ownerPartyId");
        	
        	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentType);
        	List invoiceIds =  EntityUtil.getFieldListFromEntityList(invoiceList, "invoiceId", true);
        	Map<String, Object> totalAmount =FastMap.newInstance();					
            paymentCtx.put("paymentMethodTypeId", paymentMethodType);
            paymentCtx.put("organizationPartyId", partyIdTo);
            paymentCtx.put("partyId", partyIdFrom);
            paymentCtx.put("facilityId", facilityId);            
            paymentCtx.put("statusId", "PMNT_RECEIVED");            
            paymentCtx.put("amount", amount);
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
    
    public static Map<String, Object> createTransporterMarginDuePayment(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = ((String) context.get("facilityId")).toUpperCase();            
        BigDecimal amount = (BigDecimal) context.get("amount");
        String periodBillingId =(String) context.get("periodBillingId");           
        String paymentMethodType = "TRANS_CREDIT_PAYIN";
        String paymentType = "TRANS_CREDIT_PAYIN";
        String partyIdTo ="Company";
        String paymentId = "";
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List exprListForParameters = FastList.newInstance();
        List invoiceList = FastList.newInstance();
        
		
		exprListForParameters.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));	
		
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID","INVOICE_CANCELLED","INVOICE_WRITEOFF")));		
		/*exprListForParameters.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));*/

		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

		EntityFindOptions findOptions = new EntityFindOptions();
		
		try{
			invoiceList = delegator.findList("Invoice", paramCond, null , null, findOptions, false);
			
			if(UtilValidate.isEmpty(invoiceList)){
				Debug.logError("paramCond==================== "+paramCond, module);
				Debug.logError("No dues found for the Booth "+facilityId, module);
				return ServiceUtil.returnError("No dues found for the transporter"+facilityId);
			}
		}catch(GenericEntityException e){
			Debug.logError(e, module);	
            return ServiceUtil.returnError(e.toString());			
		}
        
        try {
        	GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
        	String partyIdFrom = (String)facility.getString("ownerPartyId");
        	
        	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentType);
        	List invoiceIds =  EntityUtil.getFieldListFromEntityList(invoiceList, "invoiceId", true);        	
        	Map<String, Object> totalAmount =FastMap.newInstance();       	
        	String paymentRef = "MRG_ADJUST_"+periodBillingId;
        	
        	paymentCtx.put("paymentMethodTypeId", paymentMethodType);
            paymentCtx.put("organizationPartyId", partyIdTo);
            paymentCtx.put("partyId", partyIdFrom);
            paymentCtx.put("facilityId", facilityId);            
            paymentCtx.put("statusId", "PMNT_RECEIVED");            
            paymentCtx.put("amount", amount);
            paymentCtx.put("paymentRefNum", paymentRef);            
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
		 result.put("paymentId",paymentId);
        return result;
    }
    public static Map getTransporterDues(DispatchContext dctx, Map<String, ? extends Object> context){
		//TO DO:for now getting one shipment id  we need to get pmand am shipment id irrespective of Shipment type Id
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
    	List exprListForParameters = FastList.newInstance();
		List transporterDuesList = FastList.newInstance();
		List transporterDueInvoiceList = FastList.newInstance();
		List transporterInvoiceList = FastList.newInstance();
		BigDecimal invoicesTotalAmount = BigDecimal.ZERO;
		BigDecimal invoicesTotalDueAmount = BigDecimal.ZERO;
		String invoiceTypeId = "TRANSPORTER_IN";
		String facilityId = (String)context.get("facilityId");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		Timestamp thruDate = UtilDateTime.nowTimestamp();
		if(!UtilValidate.isEmpty(context.get("fromDate"))){
			fromDate = (Timestamp)context.get("fromDate");
		}
		if(!UtilValidate.isEmpty(context.get("thruDate"))){
			thruDate = (Timestamp)context.get("thruDate");
		}
		Timestamp dayEnd = UtilDateTime.getDayEnd(thruDate);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		exprListForParameters.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceTypeId));
		if(!UtilValidate.isEmpty(context.get("fromDate")) && !UtilValidate.isEmpty(context.get("thruDate"))){
			exprListForParameters.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
			exprListForParameters.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		}else{
			exprListForParameters.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		}
		if(facilityId != null){	
			exprListForParameters.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
			try{
				GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), true);				
				if(facilityDetail == null ){
					Debug.logError("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
					return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth or Zone ");
				}
				
			}catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e, module);
			}			
		}		
		
		List invoiceStatusList=FastList.newInstance();		
		invoiceStatusList = UtilMisc.toList("INVOICE_CANCELLED","INVOICE_WRITEOFF","INVOICE_PAID");
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, invoiceStatusList));
		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		try{			
			transporterInvoiceList = delegator.findList("Invoice", paramCond, null , null, findOptions, false);
		}catch(GenericEntityException e){
			Debug.logError(e, module);	
			return ServiceUtil.returnError(e.toString());
		}
		
		Map<String, Object> totalAmount =FastMap.newInstance();
		if (!UtilValidate.isEmpty(transporterInvoiceList)) {
			
			String tempFacilityId = "";		
			Map tempPayment = FastMap.newInstance();			
			for(int i =0 ; i< transporterInvoiceList.size(); i++){
				GenericValue dueInvoice = (GenericValue)transporterInvoiceList.get(i);
				Map tempInvoice = FastMap.newInstance();
				tempInvoice.putAll(dueInvoice);
				Map invoicePaymentInfoMap =FastMap.newInstance();
				BigDecimal outstandingAmount =BigDecimal.ZERO;
				BigDecimal amount =BigDecimal.ZERO;
				invoicePaymentInfoMap.put("invoiceId", dueInvoice.getString("invoiceId"));
				invoicePaymentInfoMap.put("userLogin",userLogin);
				try{
					Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", invoicePaymentInfoMap);
					 if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
			            	Debug.logError(getInvoicePaymentInfoListResult.toString(), module);    			
			                return ServiceUtil.returnError(null, null, null, getInvoicePaymentInfoListResult);
			            }
					Map invoicePaymentInfo = (Map)((List)getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
					outstandingAmount = (BigDecimal)invoicePaymentInfo.get("outstandingAmount");					
					amount = (BigDecimal)invoicePaymentInfo.get("amount");					
					tempInvoice.put("amount", outstandingAmount);
					
				}catch (GenericServiceException e) {
					// TODO: handle exception
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.toString());
				}
				
				transporterDueInvoiceList.add(tempInvoice);
				invoicesTotalAmount = invoicesTotalAmount.add(outstandingAmount);
				invoicesTotalDueAmount = invoicesTotalDueAmount.add(amount);
				if(tempFacilityId == ""){
					tempFacilityId = dueInvoice.getString("facilityId");
					tempPayment =FastMap.newInstance();
					tempPayment.put("facilityId", tempFacilityId);					
					tempPayment.put("supplyDate",  dueInvoice.getTimestamp("invoiceDate"));
					tempPayment.put("amount", BigDecimal.ZERO);									
				}					
				if (!(tempFacilityId.equals(dueInvoice.getString("facilityId"))))  {				
					//populating paymentMethodTypeId for paid invoices										
					transporterDuesList.add(tempPayment);
					tempFacilityId = dueInvoice.getString("facilityId");
					tempPayment =FastMap.newInstance();
					tempPayment.put("facilityId", tempFacilityId);
					tempPayment.put("supplyDate", dueInvoice.getTimestamp("invoiceDate"));
					tempPayment.put("amount", BigDecimal.ZERO);
					tempPayment.put("amount", outstandingAmount);
				}else{										
					tempPayment.put("amount", outstandingAmount.add((BigDecimal)tempPayment.get("amount")));					
				}					
				if((i == transporterInvoiceList.size()-1)){						
					transporterDuesList.add(tempPayment);						
					
				}
			}		
			
		}		
		Map transporterDuesMap =FastMap.newInstance();
		transporterDuesMap.put("invoicesTotalAmount", invoicesTotalAmount); // total outstanding due amount 
		transporterDuesMap.put("invoicesTotalDueAmount", invoicesTotalDueAmount);	// total due  amount for the period (outstanding+appliedamount)
		transporterDuesMap.put("transporterDuesList", transporterDuesList);
		transporterDuesMap.put("transporterDueInvoiceList", transporterDueInvoiceList);
		
		return transporterDuesMap;
	}
    public static Map getTransporterPaid(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
    	List exprListForParameters = FastList.newInstance();
		List<GenericValue> transporterPaidList = FastList.newInstance();
		Map resultMap = ServiceUtil.returnSuccess();		
		String paymentTypeId = "TRANSPORTER_PAYIN";
		String facilityId = (String)context.get("facilityId");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		Timestamp thruDate = UtilDateTime.nowTimestamp();
		if(!UtilValidate.isEmpty(context.get("fromDate"))){
			fromDate = (Timestamp)context.get("fromDate");
		}
		if(!UtilValidate.isEmpty(context.get("thruDate"))){
			thruDate = (Timestamp)context.get("thruDate");
		}		
		exprListForParameters.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, paymentTypeId));
		if(UtilValidate.isNotEmpty(facilityId)){
			exprListForParameters.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		}
		exprListForParameters.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED"));
		exprListForParameters.add(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
		exprListForParameters.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
		EntityCondition	paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);
		try{			
			transporterPaidList = delegator.findList("Payment", paramCond, null , null, null, false);
		}catch(GenericEntityException e){
			Debug.logError(e, module);	
			return ServiceUtil.returnError(e.toString());
		}
		if(UtilValidate.isEmpty(transporterPaidList)){
  		  return ServiceUtil.returnSuccess("no payment to process for the time period");
		}
		 Map transporterPaidMap = FastMap.newInstance();
		 for(GenericValue trnsptPaidDetails : transporterPaidList){	
			 facilityId = (String) trnsptPaidDetails.get("facilityId");
			  // here validating the transporter payments which added manually.
			 if(facilityId ==null){
				 Debug.logInfo("facilityId is Missing==========="+trnsptPaidDetails,module);
			 }
			 BigDecimal amount =BigDecimal.ZERO;
			 if(UtilValidate.isNotEmpty(trnsptPaidDetails)){
				 amount = (BigDecimal)trnsptPaidDetails.get("amount");
			 }		 
			 if(UtilValidate.isNotEmpty(transporterPaidMap.get(facilityId))){
				 BigDecimal tempAmt = (BigDecimal)transporterPaidMap.get(facilityId);       		 
				 BigDecimal totalAmt = amount.add(tempAmt);
				 transporterPaidMap.put(facilityId, totalAmt);
	   	   	 }else{
	   	   		 transporterPaidMap.put(facilityId, amount); 
	   	   	 }
		 }	
		 resultMap.put("transporterPaidMap", transporterPaidMap);
		 return resultMap;
    }
    public static Map<String, Object> createProductPayment(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = (String) context.get("facilityId");   
        Timestamp paymentDate = (Timestamp) (context.get("paymentDate"));
        Timestamp paymentDateTime = UtilDateTime.nowTimestamp();
        if(UtilValidate.isNotEmpty(paymentDate)){
        	paymentDateTime = paymentDate;
        }
        BigDecimal amount = (BigDecimal) context.get("amount");
        Locale locale = (Locale) context.get("locale");     
        String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
        String paymentType = (String) context.get("paymentTypeId");
        String partyIdTo ="Company";
        String paymentId = "";
        String partyIdFrom = "";
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List exprListForParameters = FastList.newInstance();
        List invoiceList = FastList.newInstance();
        try {
        	if(paymentType.equals("ADVDEPOSIT_PAYIN")){
        		GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
            	if(facility == null ){
    				Debug.logError("facilityId '"+facilityId+ "'is not a Booth or Zone ", "");
    				return ServiceUtil.returnError("facilityId '"+facilityId+ "'is not a Booth");
    			}
            	partyIdFrom = (String)facility.getString("ownerPartyId");
        	}else{
        		partyIdFrom ="PROCPAYMENT_VENDOR";
        	}
        	
        	
        	Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", paymentType);        	
        	Map<String, Object> totalAmount =FastMap.newInstance();					
            paymentCtx.put("paymentMethodTypeId", paymentMethodTypeId);
            paymentCtx.put("partyIdFrom", partyIdFrom);
            paymentCtx.put("partyIdTo", partyIdTo);
            paymentCtx.put("facilityId", facilityId);            
            paymentCtx.put("statusId", "PMNT_RECEIVED");            
            paymentCtx.put("amount", amount);
            paymentCtx.put("paymentDate", paymentDateTime);
            paymentCtx.put("effectiveDate", paymentDateTime);
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
   
    public static Map<String, Object> depositBoothPayment(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String paymentId = (String) context.get("paymentId");
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List exprListForParameters = FastList.newInstance();
        List invoiceList = FastList.newInstance();
        Map result = ServiceUtil.returnSuccess();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        try {        	
			GenericValue finAccount = getCompayCashFinAccoun(delegator);			
        	Map<String, Object> depositPaymentCtx = UtilMisc.<String, Object>toMap("paymentId", paymentId);
        	depositPaymentCtx.put("finAccountId", finAccount.getString("finAccountId"));           
            Map<String, Object> paymentDepositResult = dispatcher.runSync("depositWithdrawPayments", depositPaymentCtx);
            if (ServiceUtil.isError(paymentDepositResult)) {
            	Debug.logError(paymentDepositResult.toString(), module);    			
                return ServiceUtil.returnError(null, null, null, paymentDepositResult);
            }            
            }catch (Exception e) {
            Debug.logError(e, e.toString(), module);
            return ServiceUtil.returnError(e.toString());
        }		
		 result.put("paymentId",paymentId);
        return result;
    } 
    // this method will return the company  fin account  which type CASH
    public static GenericValue getCompayCashFinAccoun(Delegator delegator){
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	List condList = FastList.newInstance();
		condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));		
		condList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "CASH"));
		condList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, "Company"));
		condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
		EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
		GenericValue finAccount = null;
		try {
			List<GenericValue> finAccounts = delegator.findList("FinAccount", condition, null, null, null, false);
			finAccounts =EntityUtil.filterByDate(finAccounts ,UtilDateTime.getNextDayStart(nowTimestamp));
			finAccount = EntityUtil.getFirst(finAccounts);
		}catch (GenericEntityException e) {
			// TODO: handle exception
			 Debug.logError(e, module);             
		}		
		return finAccount;
    }
    
    public static Map<String, Object> createFacilityRecovery(DispatchContext dctx, Map<String, ? extends Object> context){
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = (String) context.get("facilityId");
        Timestamp supplyDate = UtilDateTime.nowTimestamp();
        supplyDate = (Timestamp) context.get("supplyDate");  
        BigDecimal amount = (BigDecimal) context.get("amount");
        String partyIdFrom = "Company";
        String partyId = "";
        String invoiceTypeId = (String) context.get("invoiceTypeId");
        List conditionList =FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS, invoiceTypeId));
		EntityCondition condition = EntityCondition.makeCondition(conditionList,	EntityOperator.AND);
		List InvoiceItemType =FastList.newInstance();;       
        try{
        	 InvoiceItemType = delegator.findList("InvoiceItemTypeMap", condition, null, null, null, false);
        }catch(GenericEntityException ge){
        	ge.printStackTrace();
        }
        String invoiceItemTypeId = EntityUtil.getFirst(InvoiceItemType).getString("invoiceItemTypeId");
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        String errorMsg = "Faild to create Recovery invoice, for the route  [" + facilityId + "-->" + amount  + "]";
        Map<String, Object> result =ServiceUtil.returnError(errorMsg, null, null, null); 
    	try {
    		GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
    		if(UtilValidate.isEmpty(facility)){
    			 Debug.logError("Recovery facility  not found", module);
    	         return ServiceUtil.returnError("Recovery facility  not found", null, null, result);
    		}
    		partyId = (String)facility.getString("ownerPartyId");	
    	} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.toString(), null, null, result);
        }
    	String invoiceId = "";
        Map input = UtilMisc.toMap("userLogin", userLogin);
        input.put("invoiceTypeId", invoiceTypeId);        
        input.put("partyIdFrom", partyIdFrom);
        input.put("partyId", partyId);
        input.put("facilityId", facilityId);
        input.put("statusId", "INVOICE_IN_PROCESS");	
        input.put("currencyUomId", "INR");
        input.put("invoiceDate", supplyDate);
        input.put("dueDate", UtilDateTime.getNextDayStart(UtilDateTime.nowTimestamp())); 	        
       	try{
       		result = dispatcher.runSync("createInvoice", input);
       		if (ServiceUtil.isError(result)) {
       			Debug.logError(result.toString(), module);
				return ServiceUtil.returnError(errorMsg, null, null, result);
			}	        
			invoiceId = (String)result.get("invoiceId");			
			input.clear();
			input = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);			
			input.put("invoiceItemTypeId", invoiceItemTypeId);
			//input.put("description", InvoicePayrolWorker.fetchInvoiceItemTypeDescription(dctx, invoiceItemTypeId));
			input.put("quantity", BigDecimal.ONE);
			input.put("amount", amount);
            result = dispatcher.runSync("createInvoiceItem", input);
            if (ServiceUtil.isError(result)) {
            	Debug.logError(result.toString(), module);
				return ServiceUtil.returnError("Faild to add invoice item to Recovery invoice ", null, null, result);
			}
            input.clear();
			input = UtilMisc.<String, Object>toMap("invoiceId", invoiceId);
			input.put("userLogin", userLogin);
			input.put("statusId","INVOICE_APPROVED");
            result = dispatcher.runSync("setInvoiceStatus",input);
         	if (ServiceUtil.isError(result)) {
         		Debug.logError(result.toString(), module);
                 return ServiceUtil.returnError(null, null, null, result);
             }
            
       	}catch (GenericServiceException e) {
			// TODO: handle exception
		}
       	result = ServiceUtil.returnSuccess();
       	result.put("invoiceId", invoiceId);
    	return result;
	}
    
    
        public static Map<String, Object> UpdateProductIndentQtyCategory(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = (String)context.get("facilityId");
        Locale locale = (Locale) context.get("locale");
        Map result = ServiceUtil.returnSuccess();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productId = (String)context.get("productId");
        String productCategoryId = (String)context.get("productCategoryId");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        if(UtilValidate.isEmpty(productCategoryId)){
        	Debug.logError( "productCategoryId is Empty", module);	 				 
			return ServiceUtil.returnError("Indent product quantity is empty");
        }
        try{
        	List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
			condList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> facProdCategory = delegator.findList("FacilityWiseProductCategory", cond, null, null, null, false);
			facProdCategory = EntityUtil.filterByDate(facProdCategory, UtilDateTime.getDayStart(nowTimestamp));
			if(UtilValidate.isEmpty(facProdCategory)){
				GenericValue newFacProdCat = delegator.makeValue("FacilityWiseProductCategory");
				newFacProdCat.put("facilityId", facilityId );
				newFacProdCat.put("productId", productId);
				newFacProdCat.put("productCategoryId", productCategoryId);
				newFacProdCat.put("fromDate", UtilDateTime.getDayStart(nowTimestamp));
				newFacProdCat.put("thruDate", null);
				newFacProdCat.create(); 
			}
			else{
				GenericValue facProductCategory = EntityUtil.getFirst(facProdCategory);
				String extCategory = facProductCategory.getString("productCategoryId");
				Timestamp fromDate = facProductCategory.getTimestamp("fromDate");
				if(!(extCategory.equals(productCategoryId)) && fromDate.before(UtilDateTime.getDayStart(nowTimestamp))){
					GenericValue newFacProductCat = delegator.makeValue("FacilityWiseProductCategory");
					newFacProductCat.put("facilityId", facilityId );
					newFacProductCat.put("productId", productId);
					newFacProductCat.put("productCategoryId", productCategoryId);
					newFacProductCat.put("fromDate", UtilDateTime.getDayStart(nowTimestamp));
					newFacProductCat.put("thruDate", null);
					newFacProductCat.create();
					
					facProductCategory.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(nowTimestamp, -1)));
				}
				else{
					facProductCategory.put("productCategoryId", productCategoryId);
				}
				facProductCategory.store();
			}
			Map serviceResult = endSubscriptionIndent(dctx, UtilMisc.toMap("userLogin", userLogin, "facilityId", facilityId, "productId", productId));
			if(ServiceUtil.isError(serviceResult)){
				Debug.logError("Error in service endSubscriptionIndent", module);
				return ServiceUtil.returnError("Error in service endSubscriptionIndent");
			}
        }catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Failed to update entry"+ e);
		}
        result = ServiceUtil.returnSuccess("Updated Quantity measure in "+productCategoryId+" for DealerId:"+facilityId);;
        return result;
    }
    public static Map<String, Object> endSubscriptionIndent(DispatchContext dctx, Map<String, ? extends Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Map result = ServiceUtil.returnSuccess();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String facilityId = (String)context.get("facilityId");
        String tripId = (String)context.get("tripId");
        String subscriptionTypeId = (String)context.get("subscriptionTypeId");
        Timestamp closeDate = (Timestamp)context.get("closeDate");
        String productSubscriptionTypeId = (String)context.get("productSubscriptionTypeId");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        String productId = (String)context.get("productId");
        if(UtilValidate.isEmpty(closeDate)){
        	closeDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
        }
        try{
        	List conditionList = FastList.newInstance();
        	if(UtilValidate.isNotEmpty(subscriptionTypeId)){
        		conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
        	}
        	if(UtilValidate.isNotEmpty(tripId)){
        		conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
        	}
        	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	List<GenericValue> subscription = delegator.findList("Subscription", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), UtilMisc.toSet("subscriptionId"), null, null, false);
        	List subscriptionList = EntityUtil.getFieldListFromEntityList(subscription, "subscriptionId", true);
        	
        	conditionList.clear();
    		conditionList.add(EntityCondition.makeCondition("subscriptionId",EntityOperator.IN, subscriptionList));
    		if(UtilValidate.isNotEmpty(productId)){
    			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
    		}
    		if(UtilValidate.isNotEmpty(productSubscriptionTypeId)){
    			conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId",EntityOperator.EQUALS, productSubscriptionTypeId));
    		}
    		EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
    		List<GenericValue> subscriptionProduct = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
    		List<GenericValue> subscProduct = EntityUtil.filterByDate(subscriptionProduct, UtilDateTime.getDayStart(closeDate));
    		
    		
    		for(GenericValue subProd : subscProduct){
    			Timestamp fromDate = subProd.getTimestamp("fromDate");
    			if(fromDate.compareTo(UtilDateTime.getDayStart(closeDate))<=0){
    				subProd.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(closeDate, -1)));
    				subProd.store();
    			}
    		}
    		
    		List<GenericValue> activeSubscriptionProduct = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
    		
    		List<GenericValue> tomorrowIndentProd = EntityUtil.filterByDate(activeSubscriptionProduct, UtilDateTime.addDaysToTimestamp(closeDate, 1));
    		for(GenericValue tommorowSubProd : tomorrowIndentProd){
    			delegator.removeValue(tommorowSubProd);
    		}
        }catch(Exception e){
        	Debug.logError(e, module);
			return ServiceUtil.returnError("Failed to end the subsription indent"+ e);
        }
        result = ServiceUtil.returnSuccess("Closed the subscriptions for the Party "+facilityId);
        return result;
    }
     
}    
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
import org.ofbiz.entity.GenericEntity;
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
		String actionFlag = (String) context.get("actionFlag");
		if(UtilValidate.isEmpty(actionFlag)){
			actionFlag ="create";
		}
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
		try {
				List conditionList = UtilMisc.toList(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				conditionList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, routeId));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),EntityOperator.OR,EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(fromDate))));
		        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		        List<GenericValue> facilityGroups = delegator.findList("FacilityGroupMember", condition, null, null, null, false);
		        facilityGroups = EntityUtil.filterByDate(facilityGroups, fromDate);
		        if(UtilValidate.isNotEmpty(facilityGroups) && actionFlag.equals("update")){
		        	GenericValue facilityGroup = facilityGroups.get(0);
		        	facilityGroup.set("thruDate", UtilDateTime.getDayEnd(nowDate));
	        		facilityGroup.store();
		        	Timestamp froDate = facilityGroup.getTimestamp("fromDate");
		        }else if(UtilValidate.isNotEmpty(facilityGroups) && actionFlag.equals("delete")){
		        	GenericValue facilityGroup = facilityGroups.get(0);
		        	facilityGroup.remove();
		        	
		        }else{
		        	Map tempMap = FastMap.newInstance();
		        	tempMap.put("facilityId", facilityId);
		        	tempMap.put("facilityGroupId", routeId);
		        	tempMap.put("fromDate", fromDate);
		        	tempMap.put("userLogin", userLogin);
		        	resultMap = dispatcher.runSync("addFacilityToGroup", tempMap);
		        	String subscriptionType="";
		        	if(UtilValidate.isNotEmpty(routeId)){
		        		List<GenericValue> groupMemberList = delegator.findList("FacilityGroupMember",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,routeId) , null, null, null, false);
		        		if(UtilValidate.isNotEmpty(groupMemberList)){
		        			GenericValue groupMember = EntityUtil.getFirst(groupMemberList);
		        			if("AM_RT_GROUP".equals(groupMember.get("facilityGroupId"))){
		        				subscriptionType="AM";
		        				
		        			}
		        			if("PM_RT_GROUP".equals(groupMember.get("facilityGroupId"))){
		        				subscriptionType="PM";
		        			}		
		        		}
		        		conditionList.clear();
		        		conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionType));
		        		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
						EntityCondition cond2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
						List<GenericValue> subscriptionList = delegator.findList("Subscription",  cond2, null, UtilMisc.toList("fromDate"), null, false);
						if(UtilValidate.isEmpty(subscriptionList)){
			        		GenericValue newSubscription = delegator.makeValue("Subscription");
							newSubscription.put("facilityId", facilityId);
							newSubscription.set("subscriptionTypeId", subscriptionType);
							try{
							    delegator.createSetNextSeqId(newSubscription);
							    Debug.log("created SubscriptionList ==="+newSubscription);
							}catch (GenericEntityException e) {
								Debug.logError(e, module);
								return ServiceUtil.returnError("Error while creating  subscription" + e);	
							}
					     }
		        	}
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
		String finAccountId = (String) context.get("finAccountId");
		String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
		String productStoreId = (String)(getFactoryStore(delegator)).get("factoryStoreId");
		Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try{
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			String partyId = (String) facility.get("ownerPartyId");
			List conditionList  = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyProfileDefault = delegator.findList("PartyProfileDefault", cond, null, null, null, false);
			partyProfileDefault = EntityUtil.filterByDate(partyProfileDefault, fromDate);
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACT_ACTIVE"));
			conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS, "BANK_ACCOUNT"));
			EntityCondition  finCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue>finAccountList = delegator.findList("FinAccount", finCond, null, null, null, false);
			
			boolean createEntry = true;
			if(UtilValidate.isNotEmpty(partyProfileDefault)){
				if(UtilValidate.isNotEmpty(finAccountList)){
					GenericValue finAcccount = EntityUtil.getFirst(finAccountList);
					finAcccount.set("statusId", "FNACT_CANCELLED");
					finAcccount.set("thruDate", UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayEnd(fromDate), -1));
					finAcccount.store();
				}
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
			if (paymentMethodTypeId.equals("CHALLAN_PAYIN")){
				 GenericValue finAccount = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
				 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", partyId, "finAccountTypeId", finAccount.get("finAccountTypeId"),"finAccountName", finAccount.get("finAccountName"),"finAccountCode",finAccount.get("finAccountCode"), "finAccountBranch", finAccount.get("finAccountBranch"),"ifscCode", finAccount.get("ifscCode"),"fromDate",fromDate);
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
		String parentFacilityId = "";
		String categoryTypeEnum = (String) context.get("categoryTypeEnum");
		String groupName = (String) context.get("groupName");
		firstName = (String) context.get("firstName");
		lastName = (String) context.get("lastName");
		String middleName = (String) context.get("middleName");
		address1 = (String) context.get("address1");
		address2 = (String) context.get("address2");
		String email = (String) context.get("emailAddress");
		String mobileNumber = (String) context.get("mobileNumber");
		String contactNumber =(String)context.get("contactNumber");
		String countryCode = (String) context.get("countryCode");
		BigDecimal securityDeposit =(BigDecimal)context.get("securityDeposit");
		String openedDateStr =(String)context.get("openedDate");
		String fDateStr =(String)context.get("fDateStr");
		String tDateStr =(String)context.get("tDateStr");
		String marginOnMilk =(String)context.get("marginOnMilk");
		String marginOnProduct =(String)context.get("marginOnProduct");
		String fdrNumber =(String)context.get("fdrNumber");
		String amRoute =(String)context.get("amRoute");
		String pmRoute =(String)context.get("pmRoute");
		BigDecimal rateAmount =(BigDecimal)context.get("rateAmount");
		Map<String, Object> resultMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		Timestamp openedDate=null;
		GenericValue parentFacility=null;
		GenericValue facility;
		try {
			facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			if(UtilValidate.isNotEmpty(facility)){
				Debug.logError("Booth Id Already Exists!", module);
				return ServiceUtil.returnError("Booth Id Already Exists!");
			}
			if(amRoute == null  && pmRoute == null ){
				Debug.logError("Please Enter 'AM' or 'PM' Route", module);
				return ServiceUtil.returnError("Please Enter 'AM' or 'PM' Route");
			}
			if(UtilValidate.isNotEmpty(amRoute)){
				parentFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", amRoute), false);
				if(UtilValidate.isEmpty(parentFacility)){
					Debug.logError("Invalid Route Id: " + amRoute, "");
					return ServiceUtil.returnError("Invalid Route Id "+amRoute);
				}
			 }
			if(UtilValidate.isNotEmpty(pmRoute)){
				parentFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", pmRoute), false);
				if(UtilValidate.isEmpty(pmRoute)){
					Debug.logError("Invalid Route Id: " + pmRoute, "");
					return ServiceUtil.returnError("Invalid Route Id "+pmRoute);
				}
			 } 
			if(!(parentFacility.getString("facilityTypeId")).equals("ROUTE")){
				Debug.logError("Incorrect Route Id", module);
				return ServiceUtil.returnError("Incorrect Route Id");
			}
			if(categoryTypeEnum == null){
				Debug.logError("Category is missing", module);
				return ServiceUtil.returnError("Category is missing");
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
			if(UtilValidate.isNotEmpty(openedDateStr)){
				try {
					openedDate = new java.sql.Timestamp(dateFormat.parse(openedDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + openedDate, "");
				}
				 catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "+ openedDateStr, module);
					return ServiceUtil.returnError("Cannot parse empty date string ");
				}
			}
			if(UtilValidate.isEmpty(openedDateStr)){
				openedDate = UtilDateTime.nowTimestamp();
			}
			openedDate = UtilDateTime.getDayStart(openedDate);
			if(!categoryTypeEnum.equals("CR_INST")){
				if(firstName == null){
					Debug.logError("firstName is missing", module);
					return ServiceUtil.returnError("firstName is missing");
				}
				if(lastName == null){
					Debug.logError("lastName is missing", module);
					return ServiceUtil.returnError("lastName is missing");
				}
				Object tempInput = "PARTY_ENABLED";
				input = UtilMisc.toMap("firstName", firstName, "lastName", lastName, "middleName",middleName, "statusId", tempInput,"partyId",facilityId);
				resultMap = dispatcher.runSync("createPerson", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                    return resultMap;
                }
				ownerPartyId = (String) resultMap.get("partyId");
			}else{
				if(groupName == null){
					Debug.logError("groupName is missing", module);
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
					return ServiceUtil.returnError("groupName is missing");
				}
				input = UtilMisc.toMap("groupName",context.get("groupName"),"partyId",facilityId);
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
			// create phone number
			if (UtilValidate.isNotEmpty(mobileNumber)){
				if (UtilValidate.isEmpty(countryCode)){
					countryCode	="91";
				}
	            input.clear();
	            input.put("userLogin", userLogin);
	            input.put("contactNumber",mobileNumber);
	            input.put("contactMechPurposeTypeId","PRIMARY_PHONE");
	            input.put("countryCode",countryCode);	
	            input.put("partyId", ownerPartyId);
	            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
	            if(ServiceUtil.isError(outMap)){
	           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
	            }
			}
            // create landLine number
			if (UtilValidate.isNotEmpty(contactNumber)){
	            input.clear();
	            input.put("userLogin", userLogin);
	            input.put("contactNumber",contactNumber);
	            input.put("contactMechPurposeTypeId","PHONE_HOME");
	            input.put("partyId", ownerPartyId);
	            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
	            if(ServiceUtil.isError(outMap)){
	           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
	            }
			}
            // Create Party Email
			if (UtilValidate.isNotEmpty(email)){
	            input.clear();
	            input.put("userLogin", userLogin);
	            input.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
	            input.put("emailAddress", email);
	            input.put("partyId", ownerPartyId);
	            input.put("verified", "Y");
	            input.put("fromDate", UtilDateTime.nowTimestamp());
	            outMap = dispatcher.runSync("createPartyEmailAddress", input);
	            if(ServiceUtil.isError(outMap)){
	           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
	            }
			}
			
			 GenericValue newSubscription = delegator.makeValue("Subscription");
			 newSubscription.put("facilityId", facilityId);
			 if (UtilValidate.isEmpty((String)context.get("facilityName"))){
				Debug.logError("Name of the Booth is Missing", module);
			   	return ServiceUtil.returnError("Name of the Booth is Missing");
			 }
			 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "openedDate", openedDate, "facilityId", facilityId, "facilityTypeId", "BOOTH","categoryTypeEnum", categoryTypeEnum,"facilityName", (String)context.get("facilityName"), "description", (String)context.get("description"),"securityDeposit", (BigDecimal)context.get("securityDeposit"));
			 boolean isSeqNumNumeric=(StringUtils.isNumeric(facilityId));
			 
			 if(isSeqNumNumeric){		 
				 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "openedDate",openedDate, "facilityId", facilityId, "facilityTypeId", "BOOTH", "categoryTypeEnum", categoryTypeEnum,"sequenceNum", facilityId,"facilityName", (String)context.get("facilityName"), "description", (String)context.get("description"),"securityDeposit", (BigDecimal)context.get("securityDeposit"));
			 }
			 resultMap =  dispatcher.runSync("createFacility", input);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                 return resultMap;
             }
			 String resultFacilityId = (String) resultMap.get("facilityId");
			 
				 if(UtilValidate.isNotEmpty(amRoute)){
					newSubscription.set("subscriptionTypeId", "AM");
					try{
					    delegator.createSetNextSeqId(newSubscription);
					}catch (GenericEntityException e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError("Error while creating  subscription" + e);	
					}
					 Map tempMap = FastMap.newInstance();
			        	tempMap.put("facilityId", facilityId);
			        	tempMap.put("facilityGroupId", amRoute);
			        	tempMap.put("fromDate", openedDate);
			        	tempMap.put("userLogin", userLogin);
			        	resultMap = dispatcher.runSync("addFacilityToGroup", tempMap);
			        	if(ServiceUtil.isError(resultMap)){
			        		Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                 return resultMap;
			        	}
			        	parentFacilityId=amRoute;
				 }
				 if(UtilValidate.isNotEmpty(pmRoute)){
						newSubscription.set("subscriptionTypeId", "PM");
						try{
						    delegator.createSetNextSeqId(newSubscription);
						}catch (GenericEntityException e) {
							Debug.logError(e, module);
							return ServiceUtil.returnError("Error while creating  subscription" + e);	
						}
						 Map tempMap = FastMap.newInstance();
				        	tempMap.put("facilityId", facilityId);
				        	tempMap.put("facilityGroupId", pmRoute);
				        	tempMap.put("fromDate", openedDate);
				        	tempMap.put("userLogin", userLogin);
				        	resultMap = dispatcher.runSync("addFacilityToGroup", tempMap);
				        	if(ServiceUtil.isError(resultMap)){
				        		Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			                 return resultMap;
				        	}
				        	 if(UtilValidate.isEmpty(parentFacilityId)){
				                 parentFacilityId=pmRoute;
				        	}
				 }
			 
			 if (UtilValidate.isNotEmpty(categoryTypeEnum)&& categoryTypeEnum.equals("SHP_RTLR")){
				  input.clear();
				  input = UtilMisc.toMap("userLogin", userLogin, "fromDate",openedDate,"facilityId", facilityId,
							 "rateTypeId", "SHOPEE_RENT","productId","_NA_","rateAmount",rateAmount,"supplyTypeEnumId","_NA_","rateCurrencyUomId","INR");
			      dispatcher.runSync("createOrUpdateFacilityRate",input);
			      if (ServiceUtil.isError(resultMap)) {
					  Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			          return resultMap;
			       }
			 }
			 input = UtilMisc.toMap("userLogin", userLogin, "partyId", ownerPartyId, "facilityId", facilityId, "parentFacilityId", parentFacilityId);
			 resultMap = dispatcher.runSync("updateFacilityParty", input);
			 if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                 return resultMap;
             }
			    input.clear();
			    input = UtilMisc.toMap("userLogin", userLogin,"facilityId", facilityId,"finAccountId",(String)context.get("finAccountId"),"paymentMethodTypeId",(String)context.get("paymentMethodTypeId"));
				resultMap = dispatcher.runSync("createFacilityPaymentDefault", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                 return resultMap;
             }
			 if (UtilValidate.isNotEmpty(fdrNumber)){
			     input.clear();
				 input = UtilMisc.toMap("userLogin", userLogin,"facilityId", facilityId, "fdrNumber",fdrNumber,
						 "bankName",(String)context.get("bankName"),"branchName",(String)context.get("branchName"),"amount",(BigDecimal)context.get("amount"),
						 "fromDate",fDateStr, "thruDate",tDateStr);
				 resultMap =createorUpdateFixedDeposit(dctx,input);
				 if (ServiceUtil.isError(resultMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                 return resultMap;
	             }
			 }
			 if (UtilValidate.isNotEmpty(categoryTypeEnum)&& categoryTypeEnum.equals("CR_INST")){
				  String rateTypeId =categoryTypeEnum+"_MRGN";
				  input.clear();
				  input = UtilMisc.toMap("userLogin", userLogin,"partyId",ownerPartyId, "rateTypeId",rateTypeId,"fromDate",openedDate,"lmsProductPriceTypeId",marginOnMilk,"byprodProductPriceTypeId",marginOnProduct,"rateAmount",new BigDecimal("0"));
				  resultMap =  dispatcher.runSync("updateRateAmount", input);
				  if (ServiceUtil.isError(resultMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                 return resultMap;
	              }
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
		String shipmentTypeId = (String) context.get("description");
		String subscriptionTypeId="";
		String ownerPartyId = "COMPANY";
		
		GenericValue facility;
		try {
		    GenericValue parentFacility;
		    if(facilityId == null){
		    	Debug.logError("Please Enter 'Facility Id'", module);
		    	return ServiceUtil.returnError("Please Enter 'Facility Id'");
		    }
		    /*if(parentFacilityId == null){
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
			}*/
		    if(UtilValidate.isNotEmpty(shipmentTypeId)){
	        	if(shipmentTypeId.equals("AM_SHIPMENT")){
	        		shipmentTypeId = "AM";
	        		subscriptionTypeId=shipmentTypeId+"_RT_GROUP";
	        	}else{
	        		shipmentTypeId = "PM";
	        		subscriptionTypeId=shipmentTypeId+"_RT_GROUP";
	        	}
	        }
		    
			facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			if(UtilValidate.isNotEmpty(facility)){
				Debug.logError("Route Id Already Exists!", module);
				return ServiceUtil.returnError("Route Id Already Exists!");
			}
		    String typeInput = "ROUTE";
			Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "openedDate", UtilDateTime.nowTimestamp(),
					"facilityId", facilityId, "facilityTypeId", typeInput, "parentFacilityId", parentFacilityId, "facilityName", routeName, "description", description, "facilitySize", facilitySize);   
			Map<String, Object> resultMap =  dispatcher.runSync("createFacility", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
				return resultMap;
            }
			resultFacilityId = (String) resultMap.get("facilityId");
		
		    result = ServiceUtil.returnSuccess("Route "+resultFacilityId+" is successfully created");
		    result.put("facilityId", resultFacilityId);
			
		    input = UtilMisc.toMap("userLogin", userLogin, "facilityGroupId", resultFacilityId, "facilityGroupTypeId", "RT_BOOTH_GROUP","primaryParentGroupId",subscriptionTypeId,"ownerFacilityId",resultFacilityId,
		    		"facilityGroupName",resultFacilityId,"description",resultFacilityId);
			resultMap = dispatcher.runSync("createFacilityGroup", input);
			if (ServiceUtil.isError(resultMap)) {
				Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                return resultMap;
            }
			Map tempMap = FastMap.newInstance();
        	tempMap.put("facilityId", facilityId);
        	tempMap.put("facilityGroupId", subscriptionTypeId);
        	tempMap.put("fromDate", UtilDateTime.nowTimestamp());
        	tempMap.put("userLogin", userLogin);
        	resultMap = dispatcher.runSync("addFacilityToGroup", tempMap);
        	if(ServiceUtil.isError(resultMap)){
        		Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
             return resultMap;
        	}
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
    			if(fromDate.before(UtilDateTime.getDayStart(closeDate))){
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
 public static Map<String, Object> createorUpdateFixedDeposit(DispatchContext dctx, Map context) {
		
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		String fdrNumber = (String) context.get("fdrNumber");
		String bankName = (String) context.get("bankName");
		String branchName = (String) context.get("branchName");
		BigDecimal amount = (BigDecimal) context.get("amount");
		String fDateStr = (String) context.get("fromDate");
		String tDateStr = (String) context.get("thruDate");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		SimpleDateFormat dateFormat=null;
		if(UtilValidate.isNotEmpty((String)context.get("actionFlag"))){
			  dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else{
			  dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		}
			if(UtilValidate.isNotEmpty(fDateStr)){
				try {
					fromDate = new java.sql.Timestamp(dateFormat.parse(fDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + fDateStr, "");
				}
				 catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "+ fDateStr, module);
					return ServiceUtil.returnError("Cannot parse empty date string ");
				}
			}
			
			if(UtilValidate.isEmpty(fDateStr)){
				fromDate = UtilDateTime.nowTimestamp();
			}
			    fromDate=UtilDateTime.getDayStart(fromDate);
			if(UtilValidate.isNotEmpty(tDateStr)){
				try {
					thruDate = new java.sql.Timestamp(dateFormat.parse(tDateStr).getTime());
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: " + tDateStr, "");
				}
				 catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "+ tDateStr, module);
					return ServiceUtil.returnError("Cannot parse empty date string ");
				}
			}
		Locale locale = (Locale) context.get("locale");
	    List<GenericValue> fixedDepositList = FastList.newInstance();
        List<GenericValue> activeFixedDeposit = FastList.newInstance();
        List<GenericValue> futureFixedDeposit = FastList.newInstance();
        Timestamp tempfromDate=null;
        boolean isNewFacility = true;
        if(UtilValidate.isNotEmpty(thruDate)&& thruDate.before(fromDate)){
        	Debug.logError("Thru Date  shoud be greater than From Date : "+fromDate+ "\t",module);
			return ServiceUtil.returnError("Thru Date  shoud be greater than From Date");
        }
		 List conditionList = UtilMisc.toList(
	                EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	                conditionList.add( EntityCondition.makeCondition("fdrNumber", EntityOperator.EQUALS, fdrNumber));
	                EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);  
				try {
					 fixedDepositList = delegator.findList("FacilityFixedDeposit", condition, null, UtilMisc.toList("-fromDate"), null, false);
					 activeFixedDeposit = EntityUtil.filterByDate(fixedDepositList, fromDate);
					 GenericValue newEntity = delegator.makeValue("FacilityFixedDeposit");
					 if(UtilValidate.isNotEmpty(activeFixedDeposit)){
							GenericValue activeRate = activeFixedDeposit.get(0);
					       	tempfromDate = activeRate.getTimestamp("fromDate");
					       	if(fromDate.compareTo(UtilDateTime.getDayStart(tempfromDate))==0){
					       		activeRate.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					       		activeRate.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					       		activeRate.set("amount",amount);
					       		activeRate.set("thruDate", thruDate);
					       		activeRate.store();
					       		isNewFacility=false;
					       	}
					 }
				if(isNewFacility ){
	     	        newEntity.set("facilityId", facilityId);
	     	        newEntity.set("fdrNumber", fdrNumber);
	     	        newEntity.set("bankName", bankName);
	     	        newEntity.set("branchName",branchName);
	     	       	newEntity.set("amount", amount);
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
			result = ServiceUtil.returnSuccess("Fixed Deposit is successfully updated");
			return result;
 }
   public static Map<String, Object> updateBooth(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Long sequenceNum = null;
		String address1 = null;
		String address2 = null;
		String contactMechId = null;
		String firstName  = null;
		String lastName = null;
		Locale locale = (Locale) context.get("locale");
		String facilityId = (String) context.get("facilityId");
		String parentFacilityId = (String) context.get("parentFacilityId");
		sequenceNum = (Long) context.get("sequenceNum");
		String categoryTypeEnum = (String) context.get("categoryTypeEnum");
		String groupName = (String) context.get("groupName");
		firstName = (String) context.get("firstName");
		lastName = (String) context.get("lastName");
		String middleName = (String) context.get("middleName");
		address1 = (String) context.get("address1");
		address2 = (String) context.get("address2");
		String email = (String) context.get("emailAddress");
		String mobileNumber = (String) context.get("mobileNumber");
		String countryCode = (String) context.get("countryCode");
		Timestamp openedDate=(Timestamp)context.get("openedDate");
		Timestamp closedDate =(Timestamp)context.get("closedDate");
		String marginOnMilk =(String)context.get("marginOnMilk");
		String marginOnProduct =(String)context.get("marginOnProduct");
		String partyId =(String)context.get("ownerPartyId");
		String fdrNumber =(String)context.get("fdrNumber");
		Map<String, Object> resultMap = FastMap.newInstance();
		Map<String, Object> input = FastMap.newInstance();
		Map<String, Object> outMap = FastMap.newInstance();
		String postalCode=(String)context.get("postalCode");
		BigDecimal rateAmount =(BigDecimal)context.get("rateAmount");
		String city=(String)context.get("city");
		String reopen =(String)context.get("reopen");
		String amRoute =(String)context.get("amRoute");
		String pmRoute =(String)context.get("pmRoute");
		GenericValue parentFacility;
		GenericValue facility;
		boolean updateFlag=false;
		try{
		 boolean isSeqNumNumeric=(StringUtils.isNumeric(facilityId));
		
		 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", partyId, "openedDate",openedDate,"closedDate",closedDate, "facilityId", facilityId, "facilityTypeId", (String)context.get("facilityTypeId"), 
				 "categoryTypeEnum", categoryTypeEnum,"facilityName", (String)context.get("facilityName"),"useEcs", (String)context.get("useEcs"),
				 "description", (String)context.get("description"),"securityDeposit", (BigDecimal)context.get("securityDeposit"));
	
		 if(isSeqNumNumeric){		 
			 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", partyId, "openedDate",openedDate, "facilityId", facilityId, "facilityTypeId", "BOOTH", 
					 "categoryTypeEnum", categoryTypeEnum,"sequenceNum", facilityId,"facilityName", (String)context.get("facilityName"),"useEcs", (String)context.get("useEcs"),
					 "description", (String)context.get("description"),"securityDeposit", (BigDecimal)context.get("securityDeposit"));
		 }
		 	 resultMap =  dispatcher.runSync("updateFacility", input);
		 if(ServiceUtil.isError(outMap)){
        	 	Debug.logError("failed service update facility party:"+ServiceUtil.getErrorMessage(outMap), module);
        	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
         }
		 if (UtilValidate.isNotEmpty(categoryTypeEnum)&& categoryTypeEnum.equals("CR_INST")){
			    boolean isNewCrInst = true;
			    List<GenericValue> activeRateAmount = FastList.newInstance();
			    List<GenericValue> futureRateAmount = FastList.newInstance();
			    String rateTypeId =categoryTypeEnum+"_MRGN";
			    GenericValue rateAmountTypes=null;
			    Timestamp fromDate=UtilDateTime.getDayStart(openedDate);
			    Timestamp thruDate=null;
			    if(UtilValidate.isNotEmpty(closedDate)){
			       thruDate=UtilDateTime.getDayEnd(closedDate);
			    }
			    List conditionList = UtilMisc.toList(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS,rateTypeId));
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				EntityCondition condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> rateAmountList = delegator.findList("RateAmount", condition1, null, UtilMisc.toList("-fromDate"), null, false);
				activeRateAmount = EntityUtil.filterByDate(rateAmountList, fromDate);
				futureRateAmount = EntityUtil.filterByCondition(rateAmountList, EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, fromDate));
				futureRateAmount = EntityUtil.orderBy(futureRateAmount, UtilMisc.toList("fromDate","fromDate"));
				GenericValue futureRateAmounts = EntityUtil.getFirst(futureRateAmount);
				 if(UtilValidate.isNotEmpty(futureRateAmounts)){
					 futureRateAmounts.getTimestamp("fromDate");
					 thruDate=UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(futureRateAmounts.getTimestamp("fromDate"), -1), TimeZone.getDefault(), locale);
				 }
				if (UtilValidate.isNotEmpty(activeRateAmount)){
					 Timestamp tempfromDate=null;
					 rateAmountTypes= EntityUtil.getFirst(activeRateAmount);
					 rateAmountTypes.getString("lmsProductPriceTypeId");
					 rateAmountTypes.getString("byprodProductPriceTypeId");
					 tempfromDate= rateAmountTypes.getTimestamp("fromDate");
					   if(UtilDateTime.getDayStart(openedDate).compareTo(UtilDateTime.getDayStart(tempfromDate))>0){
						   rateAmountTypes.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(openedDate, -1), TimeZone.getDefault(), locale));
						   rateAmountTypes.store();
					    }
				       	if(UtilDateTime.getDayStart(openedDate).compareTo(UtilDateTime.getDayStart(tempfromDate))==0){
				       		rateAmountTypes.set("lmsProductPriceTypeId", marginOnMilk);
				       		rateAmountTypes.set("byprodProductPriceTypeId", marginOnProduct);
				       		rateAmountTypes.set("thruDate",closedDate);
				       		rateAmountTypes.store();
				       		isNewCrInst=false;
				       	}
				}   
				if(isNewCrInst){
					GenericValue newEntity = delegator.makeValue("RateAmount");
	     	        newEntity.set("partyId", partyId);
	     	        newEntity.set("productId", "_NA_");
	     	        newEntity.set("rateTypeId", "CR_INST_MRGN");
	     	        newEntity.set("periodTypeId", "RATE_HOUR");
	     	        newEntity.set("rateCurrencyUomId", "INR");
	     	        newEntity.set("emplPositionTypeId", "_NA_");
	     	        newEntity.set("workEffortId", "_NA_");
	     	        newEntity.set("lmsProductPriceTypeId", marginOnMilk);
	     	        newEntity.set("byprodProductPriceTypeId", marginOnProduct);
	     	        newEntity.set("fromDate", openedDate);
	     	        newEntity.set("thruDate", closedDate);
	 		        try {
	 					delegator.create(newEntity);
	 				}catch (GenericEntityException e) {
	 					Debug.logError("Error in creating Facility Rate: "+facilityId+ "\t"+e.toString(),module);
	 					return ServiceUtil.returnError(e.getMessage());
	 				}
				  }
			  if (ServiceUtil.isError(resultMap)) {
				 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	            return resultMap;
	         }
		 }
			if (UtilValidate.isNotEmpty(categoryTypeEnum)&& categoryTypeEnum.equals("SHP_RTLR")){
				if (UtilValidate.isNotEmpty(reopen)){
				      input.clear();
					  input = UtilMisc.toMap("userLogin", userLogin, "fromDate",UtilDateTime.nowTimestamp(),"facilityId", facilityId,
								 "rateTypeId", "SHOPEE_RENT","productId","_NA_","rateAmount",rateAmount,"supplyTypeEnumId","_NA_","rateCurrencyUomId","INR");
				      dispatcher.runSync("createOrUpdateFacilityRate",input);
				      if (ServiceUtil.isError(resultMap)) {
						  Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
				          return resultMap;
				       }
				}else{
				  input.clear();
				  List<GenericValue> facilityRateList = FastList.newInstance();
			      List<GenericValue> activeFacilityRate = FastList.newInstance();
			      Timestamp tempfromDate=null;
			      Timestamp fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			      Timestamp thruDate=null;
				    if(UtilValidate.isNotEmpty(closedDate)){
				       thruDate=UtilDateTime.getDayEnd(closedDate);
				    }
			      boolean isNewShpRtlr = true;
				  List conditionList = UtilMisc.toList(
			                EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
			                conditionList.add( EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, "SHOPEE_RENT"));
			                conditionList.add( EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
			                conditionList.add( EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS, "_NA_"));
			                conditionList.add( EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, "INR"));
			                EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			                try {
								 facilityRateList = delegator.findList("FacilityRate", condition, null, UtilMisc.toList("-fromDate"), null, false);
								 GenericValue newEntity = delegator.makeValue("FacilityRate");
								 if(UtilValidate.isNotEmpty(facilityRateList)){
										GenericValue activeRate = facilityRateList.get(0);
								       	tempfromDate = activeRate.getTimestamp("fromDate");
								    	if(fromDate.compareTo(UtilDateTime.getDayStart(tempfromDate))>0){
								    		if(UtilValidate.isNotEmpty(thruDate)){
								    		   activeRate.set("thruDate", thruDate);
								    		   isNewShpRtlr=false;
								    		}else if(fromDate.compareTo(UtilDateTime.getDayStart(tempfromDate))!=0){
								    			activeRate.set("rateAmount",rateAmount);
								    			activeRate.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1), TimeZone.getDefault(), locale));
								    			isNewShpRtlr=true;
								    		}
								       		activeRate.store();
								       		
									    }
								       	if(fromDate.compareTo(UtilDateTime.getDayStart(tempfromDate))==0){
								       		activeRate.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
								       		activeRate.set("lastModifiedDate", UtilDateTime.nowTimestamp());
								       		activeRate.set("rateAmount",rateAmount);
								       		activeRate.set("thruDate",thruDate);
								       		activeRate.store();
								       		isNewShpRtlr=false;
								       	}
								 }
								 if(isNewShpRtlr ){
						     	        newEntity.set("facilityId", facilityId);
						     	        newEntity.set("productId", "_NA_");
						     	        newEntity.set("rateTypeId", "SHOPEE_RENT");
						     	        newEntity.set("supplyTypeEnumId", "_NA_");
						     	        newEntity.set("rateCurrencyUomId", "INR");
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
			      if (ServiceUtil.isError(resultMap)) {
					  Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			          return resultMap;
			       }
				}catch (GenericEntityException e) {
					Debug.logError(e, module);
		            return ServiceUtil.returnError(e.getMessage());
				} 
			  }
			}
			if (UtilValidate.isNotEmpty(amRoute)||UtilValidate.isNotEmpty(pmRoute)){
				 input = UtilMisc.toMap("userLogin", userLogin, "facilityId", facilityId, "amRoute", amRoute,"pmRoute",pmRoute,"fromDate",openedDate,"thruDate",closedDate);
				 resultMap =updateFacilityGroupMember(dctx,input);
				 if (ServiceUtil.isError(resultMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                 return resultMap;
	             }
			}
		   if (UtilValidate.isNotEmpty(address1)){
			    input.clear();
	        	input.put("partyId", partyId);
	        	input.put("userLogin", userLogin);
	            outMap = dispatcher.runSync("getPartyPostalAddress", input);
	            if (UtilValidate.isNotEmpty(outMap.get("address1"))){
	            	 if (!outMap.get("address1").equals(address1)){
	            		 input.put("address1",address1);
	            		 updateFlag=true;
	            	 }
	            	 if (UtilValidate.isNotEmpty(address2) && !outMap.get("address2").equals(address2)){
	            		 input.put("address2",address2);
	            		 updateFlag=true;
	            	 }
	            	 if (UtilValidate.isNotEmpty(city)&&!outMap.get("city").equals(city)){
	            		 input.put("city",city);
	            		 updateFlag=true;
	            	 }
	            	 if (UtilValidate.isNotEmpty(postalCode)&&!outMap.get("postalCode").equals(postalCode)){
	            		 input.put("postalCode",postalCode);
	            		 updateFlag=true;
	            	 }
	            	 if(updateFlag){
	            		 input.put("stateProvinceGeoId","IND");
	            		 input.put("contactMechId",outMap.get("contactMechId"));
			            resultMap =  dispatcher.runSync("updatePartyPostalAddress", input);
	            	 }
					 if(ServiceUtil.isError(outMap)){
			     	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
			     	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
			         }
				 }else{
					    input = UtilMisc.toMap("userLogin", userLogin, "partyId",partyId, "address1",address1, "address2", address2, "city", city, "stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", postalCode, "contactMechId", contactMechId);
						resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
						if (ServiceUtil.isError(resultMap)) {
							Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			                return resultMap;
			            }
						contactMechId = (String) resultMap.get("contactMechId");
						input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",partyId, "contactMechPurposeTypeId", "BILLING_LOCATION");
						resultMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
						if (ServiceUtil.isError(resultMap)) {
						    Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			                return resultMap;
			            }
				 }
		     }
		     // update phone number
	        if (UtilValidate.isNotEmpty(mobileNumber)){
	        	if (UtilValidate.isEmpty(countryCode)){
					countryCode	="91";
				}
	        	input.clear();
	        	input.put("partyId", partyId);
	        	input.put("userLogin", userLogin); 
	            outMap = dispatcher.runSync("getPartyTelephone", input);
	            if(ServiceUtil.isError(outMap)){
	           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
	           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
	            }
	            if (UtilValidate.isNotEmpty(outMap)){
		        	  if (outMap.containsKey("contactNumber") ){
		        		   if(!outMap.get("contactNumber").equals(mobileNumber) || !outMap.get("countryCode").equals(countryCode)){
					            input.clear();
					            input.put("userLogin", userLogin);
					            input.put("countryCode",countryCode);
					            input.put("contactNumber",mobileNumber);
					            input.put("contactMechId", outMap.get("contactMechId"));
					            input.put("partyId", partyId);
					            outMap = dispatcher.runSync("updatePartyTelecomNumber", input);
					            if(ServiceUtil.isError(outMap)){
					           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
					           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
					            }
							}
		        	  }else{
							    input.clear();
					            input.put("userLogin", userLogin);
					            input.put("countryCode",countryCode);
					            input.put("contactNumber",mobileNumber);
					            input.put("contactMechTypeId","TELECOM_NUMBER");
					            input.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
					            input.put("partyId", partyId);
					            outMap = dispatcher.runSync("createPartyTelecomNumber", input);
					           
					            if(ServiceUtil.isError(outMap)){
					           	 	Debug.logError("failed service create party contact telecom number:"+ServiceUtil.getErrorMessage(outMap), module);
					           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
					            }
			          }
	            }
	          } 
	     // update  email
	            if (UtilValidate.isNotEmpty(email)){
	            	input.clear();
		        	input.put("partyId", partyId);
		        	input.put("userLogin", userLogin);
		        	input.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
		            outMap = dispatcher.runSync("getPartyEmail", input);   
		             if (outMap.containsKey("emailAddress") ){
		            	 if(!outMap.get("emailAddress").equals(email))
					        input.clear();
				            input.put("userLogin", userLogin);
				            input.put("emailAddress",email);
				            input.put("contactMechId", outMap.get("contactMechId"));
				            input.put("partyId", partyId);
				            outMap = dispatcher.runSync("updatePartyEmailAddress", input);
				            if(ServiceUtil.isError(outMap)){
				           	 	Debug.logError("failed service create party email:"+ServiceUtil.getErrorMessage(outMap), module);
				           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
				            }
				            
					 }
					 else{
						input.clear();
			            input.put("userLogin", userLogin);
			            input.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
			            input.put("emailAddress", email);
			            input.put("partyId", partyId);
			            input.put("verified", "Y");
			            input.put("fromDate", UtilDateTime.nowTimestamp());
			            outMap = dispatcher.runSync("createPartyEmailAddress", input);
			            if(ServiceUtil.isError(outMap)){
			           	 	Debug.logError("faild service create party Email:"+ServiceUtil.getErrorMessage(outMap), module);
			           	 	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(outMap));
			            }
					}
	            
	            }    
	        
			
		}catch (Exception e){
			Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
		} 
		return result;		
}
   public static Map<String, Object> updateFacilityGroupMember(DispatchContext ctx,Map<String, Object> context) {
       Map<String, Object> finalResult = FastMap.newInstance();
       Map<String, Object> result = FastMap.newInstance();
       Delegator delegator = ctx.getDelegator();
       LocalDispatcher dispatcher = ctx.getDispatcher();
       Locale locale = (Locale) context.get("locale");
       String facilityId = (String) context.get("facilityId");
       String amRoute = (String) context.get("amRoute");
       String pmRoute = (String) context.get("pmRoute");
      
       Timestamp thruDate = (Timestamp) context.get("thruDate");
       List<GenericValue> facilityGroupMemList = FastList.newInstance();
       List<GenericValue>  boothsList= FastList.newInstance();
       List<GenericValue> activeFacilityGroupMem = FastList.newInstance();
       List<GenericValue> futureFacilityRate = FastList.newInstance();
       List facilityGroupList = FastList.newInstance();
       List<GenericValue> amRoutelist =FastList.newInstance();
       List<GenericValue> pmRoutelist =FastList.newInstance();
       GenericValue userLogin = (GenericValue) context.get("userLogin");
       GenericValue amRt =null;
       GenericValue pmRt =null;
       List amRoutelst =null;
       List pmRoutelst=null;
       Timestamp tempfromDate=null;
       String tempfacAmGroupId=null;
       String tempfacPmGroupId=null;
       List condList = FastList.newInstance();
       
       boolean isNewFacilityGroupMem = true;
       if(UtilValidate.isNotEmpty(thruDate)){
       	thruDate=UtilDateTime.getDayEnd(thruDate);
       }
       Timestamp fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
       EntityCondition condition=null;
			try {
				condList.clear();
				condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				condList.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS, null));
				EntityCondition cond3=EntityCondition.makeCondition(condList,EntityOperator.AND);
				 facilityGroupMemList = delegator.findList("FacilityGroupMember",  cond3, null, UtilMisc.toList("fromDate"), null, false);
				 if(UtilValidate.isNotEmpty(facilityGroupMemList)){
				    activeFacilityGroupMem = EntityUtil.filterByDate(facilityGroupMemList, fromDate);
				    facilityGroupList = EntityUtil.getFieldListFromEntityList(facilityGroupMemList, "facilityGroupId", true);
				 }
				 if(UtilValidate.isNotEmpty(amRoute)){
					    if(UtilValidate.isNotEmpty(facilityGroupList)){
						    condList.clear();
						    condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityGroupList));
						    condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, "AM_RT_GROUP"));
							condition=EntityCondition.makeCondition(condList,EntityOperator.AND);
							boothsList = delegator.findList("FacilityGroupAndMemberAndFacility",condition , null, UtilMisc.toList("fromDate"), null, false);
							if(UtilValidate.isNotEmpty(boothsList)){
							      amRoutelst= EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", true);
							        condList.clear();
									condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
									condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.IN, amRoutelst));
									
									EntityCondition cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
									amRoutelist = delegator.findList("FacilityGroupMember", cond, null, UtilMisc.toList("fromDate"), null, false);
							
							}
							if(UtilValidate.isNotEmpty(amRoutelist)){
								 amRt =EntityUtil.getFirst(amRoutelist);
							}
					 }
				 }
				 if(UtilValidate.isNotEmpty(pmRoute)){
						if(UtilValidate.isNotEmpty(facilityGroupList)){
							condList.clear();
							condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityGroupList));
							condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, "PM_RT_GROUP"));
							
							EntityCondition condition1=EntityCondition.makeCondition(condList,EntityOperator.AND);
							
							boothsList.clear();
							boothsList = delegator.findList("FacilityGroupAndMemberAndFacility",condition1 , null, null, null, false);
							if(UtilValidate.isNotEmpty(boothsList)){
								pmRoutelst= EntityUtil.getFieldListFromEntityList(boothsList, "facilityId", true);
								condList.clear();
								condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.IN, pmRoutelst));
								condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
								EntityCondition cond1=EntityCondition.makeCondition(condList,EntityOperator.AND);
							
								pmRoutelist = delegator.findList("FacilityGroupMember",  cond1, null, UtilMisc.toList("fromDate"), null, false);
							}
						
							if(UtilValidate.isNotEmpty(pmRoutelist)){
								pmRt =EntityUtil.getFirst(pmRoutelist);
							}
					 }
				 }
				 if(UtilValidate.isNotEmpty(amRt)){
				       	tempfacAmGroupId= (String) amRt.get("facilityGroupId");
				       	if(!tempfacAmGroupId.equals(amRoute) ){
				       		amRt.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1), TimeZone.getDefault(), locale));
				       		amRt.store();
				       	}
				 }
					 if(UtilValidate.isNotEmpty(pmRt)){
				       	tempfacPmGroupId= (String) pmRt.get("facilityGroupId");
				       	if(!tempfacPmGroupId.equals(pmRoute)){
				       		pmRt.set("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1), TimeZone.getDefault(), locale));
				       		pmRt.store();
				       	}
					 }
					    GenericValue newEntity = delegator.makeValue("FacilityGroupMember");
					    newEntity.set("facilityId", facilityId);
		    	        newEntity.set("fromDate", fromDate);
		    	        newEntity.set("thruDate", thruDate);
					if(UtilValidate.isNotEmpty(amRoute)){
						condList.clear();
						condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, amRoute));
						condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
						EntityCondition cond1=EntityCondition.makeCondition(condList,EntityOperator.AND);
					
						List<GenericValue> amlist = delegator.findList("FacilityGroupMember",  cond1, null, UtilMisc.toList("fromDate"), null, false);
						if(UtilValidate.isEmpty(amlist)){
							 newEntity.set("facilityGroupId", amRoute);
							 try {
									delegator.create(newEntity);
								}catch (GenericEntityException e) {
									Debug.logError("Error in creating Facility Group Member: "+amRoute+ "\t"+e.toString(),module);
									return ServiceUtil.returnError(e.getMessage());
								}
								condList.clear();
								condList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "AM"));
								condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
								EntityCondition cond2=EntityCondition.makeCondition(condList,EntityOperator.AND);
								List<GenericValue> amSubscriptionList = delegator.findList("Subscription",  cond1, null, UtilMisc.toList("fromDate"), null, false);
								if(UtilValidate.isEmpty(amSubscriptionList)){
									GenericValue newSubscription = delegator.makeValue("Subscription");
									newSubscription.put("facilityId", facilityId);
									newSubscription.set("subscriptionTypeId", "AM");
									try{
									    delegator.createSetNextSeqId(newSubscription);
									}catch (GenericEntityException e) {
										Debug.logError(e, module);
										return ServiceUtil.returnError("Error while creating  subscription" + e);	
									}
								}
			       		 }else{
			       			
			       			    amlist = EntityUtil.filterByAnd(amlist, UtilMisc.toMap("fromDate", fromDate));
			       			    amRt.clear();	
			       			    if(UtilValidate.isNotEmpty(amlist)){
							    amRt =EntityUtil.getFirst(amlist);
								amRt.set("thruDate", null);
								amRt.store();
			       			    }
							}
					}
					if(UtilValidate.isNotEmpty(pmRoute)){
						condList.clear();
						condList.add(EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, pmRoute));
						condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
						EntityCondition cond1=EntityCondition.makeCondition(condList,EntityOperator.AND);
						List<GenericValue> pmlist = delegator.findList("FacilityGroupMember",  cond1, null, UtilMisc.toList("fromDate"), null, false);
						if(UtilValidate.isEmpty(pmlist)){
							newEntity.set("facilityGroupId", pmRoute);
							try {
								delegator.create(newEntity);
							}catch (GenericEntityException e) {
								Debug.logError("Error in creating Facility Group Member: "+pmRoute+ "\t"+e.toString(),module);
								return ServiceUtil.returnError(e.getMessage());
							}
							condList.clear();
							condList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "PM"));
							condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
							EntityCondition cond2=EntityCondition.makeCondition(condList,EntityOperator.AND);
							List<GenericValue> pmSubscriptionList = delegator.findList("Subscription",  cond1, null, UtilMisc.toList("fromDate"), null, false);
							if(UtilValidate.isEmpty(pmSubscriptionList)){
								GenericValue newSubscription = delegator.makeValue("Subscription");
								newSubscription.put("facilityId", facilityId);
								newSubscription.set("subscriptionTypeId", "PM");
								try{
								    delegator.createSetNextSeqId(newSubscription);
								}catch (GenericEntityException e) {
									Debug.logError(e, module);
									return ServiceUtil.returnError("Error while creating  subscription" + e);	
								}
							}
						}else{
							pmlist = EntityUtil.filterByAnd(pmlist, UtilMisc.toMap("fromDate", fromDate));
							pmRt.clear();	
							if(UtilValidate.isNotEmpty(pmlist)){
								pmRt =EntityUtil.getFirst(pmlist);
								pmRt.set("thruDate", null);
								pmRt.store();
							}
						}
					}
			}catch (GenericEntityException e) {
				Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
			} 
		return result;
   }
}
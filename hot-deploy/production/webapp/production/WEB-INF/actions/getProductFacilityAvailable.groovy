
import javolution.util.FastList
import javolution.util.FastMap

import org.ofbiz.base.util.*
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.service.ServiceUtil

dctx = dispatcher.getDispatchContext();
Map resultReturn = ServiceUtil.returnSuccess();
String productId = parameters.get("productId");
String ownerFacilityId = parameters.get("ownerFacilityId");

if(UtilValidate.isEmpty(ownerFacilityId) && UtilValidate.isEmpty(productId)){
	Debug.logError("ProductId or facilityId is missing ","");
	resultReturn = ServiceUtil.returnError("ProductId or facilityId is missing ");
	return resultReturn;	
}

Map productFacilityComponentDetails = FastMap.newInstance();
List productFacilityDetailsList = FastList.newInstance();

List condList = FastList.newInstance();
condList.add(EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS, ownerFacilityId));
condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SILO"));
EntityCondition cond = EntityCondition.makeCondition(condList, EntityJoinOperator.AND);
List plantFacilities = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null, null, false);

List<String> facilityIds = EntityUtil.getFieldListFromEntityList(plantFacilities, "facilityId", false);
facilityIds.add(ownerFacilityId);
if(UtilValidate.isNotEmpty(facilityIds)){
	
	List prodFacilityConditionList = UtilMisc.toList(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
	prodFacilityConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIds));
	EntityCondition prodFacCondition = EntityCondition.makeCondition(prodFacilityConditionList,EntityJoinOperator.AND);
	List prodFacilityList = FastList.newInstance();
	
	prodFacilityList = delegator.findList("ProductFacility", prodFacCondition, null, null, null, false);
	if(UtilValidate.isNotEmpty(prodFacilityList)){
		for(GenericValue prodFacility in prodFacilityList){
			String facilityId = (String) prodFacility.get("facilityId");
			GenericValue facilityDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
			String ownerPartyId = (String) facilityDetails.get("ownerPartyId");
			Map inMap = FastMap.newInstance();
			inMap.put("userLogin",userLogin);
			inMap.put("ownerPartyId",ownerPartyId);
			inMap.put("facilityId",facilityId);
			inMap.put("productId",productId);
			
			Map inventoryAvailable = dispatcher.runSync("getProductInventoryOpeningBalance", inMap);
			invQty = inventoryAvailable.get("inventoryCount");
			if(invQty == 0){
				inMap.put("ownerPartyId","Company");
				inventoryAvailable = dispatcher.runSync("getProductInventoryOpeningBalance", inMap);
				invQty = inventoryAvailable.get("inventoryCount");
			}
			if(invQty>0){
				Map productFacilityDetailsMap = FastMap.newInstance();
				productFacilityDetailsMap.put("facilityId",facilityId);
				productFacilityDetailsMap.put("productId",productId);
				productFacilityDetailsMap.put("availableQty",invQty);
				productFacilityDetailsList.add(productFacilityDetailsMap);
			}
		}
	}	
}
GenericValue prodDetails = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
productFacilityComponentDetails.put("productFacilityDetailsList",productFacilityDetailsList);
productFacilityComponentDetails.put("prodDetails",prodDetails);

resultReturn.put("productFacilityComponentDetails",productFacilityComponentDetails);
return resultReturn;
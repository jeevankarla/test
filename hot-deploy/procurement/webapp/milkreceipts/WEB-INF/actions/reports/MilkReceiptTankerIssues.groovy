import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.sql.*;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.service.ServiceUtil;
import org.webslinger.resolver.UtilDateResolver;

import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import in.vasista.vbiz.procurement.ProcurementNetworkServices;

String displayScreen = parameters.get("displayScreen");
List<GenericValue> productCatMembers = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), context);
List productIds = EntityUtil.getFieldListFromEntityList(productCatMembers, "productId", false);

EntityCondition condition = EntityCondition.makeCondition("facilityGroupId",EntityOperator.EQUALS,"MPU_PASTEURIZATION");
List<GenericValue> pmSilosList  = delegator.findList("FacilityGroupMember",condition,null,null,null,false);
pmSilosList = EntityUtil.filterByDate(pmSilosList,UtilDateTime.nowTimestamp());
List<GenericValue> siloProductMembers = FastList.newInstance();
if(UtilValidate.isNotEmpty(pmSilosList)){
	List<String> facilityIds = EntityUtil.getFieldListFromEntityList(pmSilosList, "facilityId", false);
	List siloProductCondList = FastList.newInstance();
	siloProductCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator .IN,facilityIds));
	condition = EntityCondition.makeCondition(siloProductCondList);
	List siloProductFacility = delegator.findList("ProductFacility",condition,null,null,null,false);
	List siloProductsList = FastList.newInstance();
	siloProductsList= EntityUtil.getFieldListFromEntityList(siloProductFacility, "productId", false);
	if(UtilValidate.isNotEmpty(siloProductsList)){
		 siloProductMembers = delegator.findList("Product",EntityCondition.makeCondition("productId",EntityOperator.IN,siloProductsList),null,null,null,false);
	}
	
}
String tempProductId = null;
JSONObject productJson = new JSONObject();
JSONArray productItemsJSON = new JSONArray();
for(product in productCatMembers){
	JSONObject productNamesJson = new JSONObject();
	JSONObject productDetailsJson = new JSONObject();
	productDetailsJson.put("name",product.get("productName"));
	productDetailsJson.put("brandName",product.get("brandName"));
	productNamesJson.put("value",product.get("productId"));
	productNamesJson.put("label",product.get("productName")+"("+product.get("productId")+")"+ " [" + product.get("brandName") + "]");
	productItemsJSON.add(productNamesJson);
	productJson.put(product.get("productId"), productDetailsJson);
	}
//HERE WE ARE ADDING SILO PRODUCTS
for(product in siloProductMembers){
	JSONObject productNamesJson = new JSONObject();
	JSONObject productDetailsJson = new JSONObject();
	productDetailsJson.put("name",product.get("productName"));
	productDetailsJson.put("brandName",product.get("brandName"));
	productNamesJson.put("value",product.get("productId"));
	productNamesJson.put("label",product.get("productName")+"("+product.get("productId")+")"+ " [" + product.get("brandName") + "]");
	productItemsJSON.add(productNamesJson);
	productJson.put(product.get("productId"), productDetailsJson);
	}

context.putAt("productItemsJSON", productItemsJSON);
context.put("productJson",productJson);


// vehicles auto complete from vehicle master
Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
Timestamp thruDate = UtilDateTime.getDayEnd(fromDate);

List vehCondList = FastList.newInstance();
vehCondList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE"));
vehCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null),EntityOperator.OR,EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate)));
EntityCondition vehCondition = EntityCondition.makeCondition(vehCondList);
List vehicleRoleList = delegator.findList("VehicleRole",vehCondition, null, null, null, true);

Set vehicleIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(vehicleRoleList, "vehicleId", false));
List<GenericValue> vehiclesList = delegator.findList("Vehicle",EntityCondition.makeCondition("vehicleId",EntityOperator.IN,vehicleIdsSet), null, null, null, true);

JSONObject vehicleCodeJson = new JSONObject();
JSONArray vehItemsJSON = new JSONArray();
for(vehicle in vehiclesList){
		JSONObject vehObjectJson = new JSONObject();
		vehObjectJson.put("value",vehicle.get("vehicleId"));
		String label = vehicle.get("vehicleId");
		if(UtilValidate.isNotEmpty(vehicle.get("vehicleName"))){
				label = label.concat("-").concat(vehicle.get("vehicleName"));
			}
		
		vehObjectJson.put("label",label);
		vehItemsJSON.add(vehObjectJson);
		
		JSONObject vehDetJson = new JSONObject();
		vehDetJson.put("vehicleId",vehicle.get("vehicleId"));
		vehDetJson.put("vehicleName",vehicle.get("vehicleName"));
		
		vehicleCodeJson.put(vehicle.get("vehicleId"),vehDetJson);
		
}
context.put("vehItemsJSON",vehItemsJSON);
context.put("vehicleCodeJson",vehicleCodeJson);

List partyRoleTypes = FastList.newInstance();
List<String> internalPartyIds = FastList.newInstance();

partyRoleTypes.add("INTERNAL_ORGANIZATIO");
partyRoleTypes.add("UNION");
partyRoleTypes.add("UNITS");
List unionConditionList = UtilMisc.toList(EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,partyRoleTypes));
unionConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"PARTY_DISABLED"),EntityOperator.OR,EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,null)));
EntityCondition unionCondition = EntityCondition.makeCondition(unionConditionList);

List<GenericValue> unionsList = delegator.findList("PartyRoleAndPartyDetail",unionCondition, null, null, null, true);
List<GenericValue> intOrgParties = EntityUtil.filterByCondition(unionsList,EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"INTERNAL_ORGANIZATIO") );
internalPartyIds = EntityUtil.getFieldListFromEntityList(intOrgParties, "partyId", false);

JSONObject unionCodeJson = new JSONObject();
JSONArray unionItemsJSON = new JSONArray();
JSONObject  intPartyJSON = new JSONObject(); 
for(union in unionsList){
		if((UtilValidate.isNotEmpty(displayScreen) && (displayScreen.equalsIgnoreCase("ISSUE_INIT")))){
			String partyIdStr = union.get("partyId");
			if(UtilValidate.isNotEmpty(internalPartyIds) && internalPartyIds.contains(partyIdStr)){
				continue;
			}
		}
		String roleTypeId = union.get("roleTypeId");
		JSONObject unionObjectJson = new JSONObject();
		unionObjectJson.put("value",union.get("partyId"));
		String label = union.get("partyId");
		if(UtilValidate.isNotEmpty(union.get("groupName"))){
				label = label.concat("-").concat(union.get("groupName"));
			}
		
		unionObjectJson.put("label",label);
		unionItemsJSON.add(unionObjectJson);
		
		JSONObject unionDetJson = new JSONObject();
		unionDetJson.put("partyId",union.get("partyId"));
		unionDetJson.put("partyName",union.get("groupName"));
		
		unionCodeJson.put(union.get("partyId"),unionDetJson);
		if(UtilValidate.isNotEmpty(roleTypeId) && roleTypeId.equalsIgnoreCase("INTERNAL_ORGANIZATIO")){
			if(UtilValidate.isEmpty(displayScreen) || (UtilValidate.isNotEmpty(displayScreen) && (!displayScreen.equalsIgnoreCase("ISSUE_INIT")))){
				intPartyJSON.put(union.get("partyId"),union.get("partyId"));
			}
		}
		
}

context.put("partyCodeJson",unionCodeJson);
context.put("partyItemsJSON",unionItemsJSON);
context.put("intPartyJSON",intPartyJSON);

List rawMilkSilosList = FastList.newInstance();
List rawMilkSiloConditionList = FastList.newInstance();
List siloTypeList = FastList.newInstance();
siloTypeList.add("RAWMILK");
siloTypeList.add("PASTEURIZATION");

rawMilkSiloConditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"SILO"));
rawMilkSiloConditionList.add(EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.IN,siloTypeList));
EntityCondition rawMilkSiloCondition = EntityCondition.makeCondition(rawMilkSiloConditionList,EntityOperator.AND);
rawMilkSilosList = delegator.findList("Facility",rawMilkSiloCondition, null, null, null, true);
if(UtilValidate.isNotEmpty(rawMilkSilosList)){
	prodFacilitys=EntityUtil.getFieldListFromEntityList(rawMilkSilosList, "facilityId", true);
}
context.putAt("rawMilkSilosList", rawMilkSilosList);
List productFacilityDetails =FastList.newInstance();
if(UtilValidate.isNotEmpty(rawMilkSilosList)){
	productFacilityDetails = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId", EntityOperator.IN , prodFacilitys)  , null, null, null, false );
}
JSONObject productFacilityIdMap = new JSONObject();
if(UtilValidate.isNotEmpty(productFacilityDetails)){
	facilityProdIds=EntityUtil.getFieldListFromEntityList(productFacilityDetails, "productId", true);
	if(UtilValidate.isNotEmpty(facilityProdIds)){
		facilityProdIds.each{eachProdId->
			productFacilities = EntityUtil.filterByCondition(productFacilityDetails, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProdId));
			eachProdFacilityIds=EntityUtil.getFieldListFromEntityList(productFacilities, "facilityId", true);
			List invCondList =FastList.newInstance();
			invCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN , eachProdFacilityIds));
			invCondList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , eachProdId));
			invCondList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN , BigDecimal.ZERO));
			EntityCondition invCond = EntityCondition.makeCondition(invCondList,EntityOperator.AND);
			inventoryItemForProducts = delegator.findList("InventoryItem",invCond , null, null, null, false );
			eachProdInvFacilityIds=EntityUtil.getFieldListFromEntityList(inventoryItemForProducts, "facilityId", true);
			java.util.Collections.sort(eachProdInvFacilityIds);
			productFacilityIdMap.putAt(eachProdId, eachProdInvFacilityIds);
		}
	}
}
context.putAt("productFacilityIdMap", productFacilityIdMap);
List purposeList = FastList.newInstance();
purposeList = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS , "MILK_OUT_PURP")  , null, null, null, false );
context.putAt("purposeList", purposeList);
JSONObject purposeJson = new JSONObject();
if(UtilValidate.isNotEmpty(purposeList)){
	for(purposeDet in purposeList ){
		String purposeTypeId = (String) purposeDet.get("enumId"); 
		String description = (String) purposeDet.get("description");
		if(UtilValidate.isNotEmpty(purposeTypeId)){
			purposeJson.put(purposeTypeId,description);
		}
	}
}
context.putAt("purposeJson", purposeJson);
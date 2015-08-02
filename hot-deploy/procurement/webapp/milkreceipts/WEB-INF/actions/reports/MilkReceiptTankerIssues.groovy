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


List<GenericValue> productCatMembers = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), context);
List productIds = EntityUtil.getFieldListFromEntityList(productCatMembers, "productId", false);


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
	List conditionList = FastList.newInstance();
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS,"SNFFAT_CHECK"));
	conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS,"_NA_"));
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,product.get("productId")));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List fatSnfList = delegator.findList("ProcBillingValidationRule",condition,null,null,null,true);
	
	BigDecimal minFat = new BigDecimal(2.5);;
	BigDecimal maxFat = new BigDecimal(12);
	BigDecimal minSnf = new BigDecimal(7.5);
	BigDecimal maxSnf = new BigDecimal(12);
	if(UtilValidate.isNotEmpty(fatSnfList)){
			Map fatSnfDetails = EntityUtil.getFirst(fatSnfList);
			minFat = fatSnfDetails.get("minFat");
			maxFat = fatSnfDetails.get("maxFat");
			minSnf = fatSnfDetails.get("minSnf");
			maxSnf = fatSnfDetails.get("maxSnf");
		}
	productDetailsJson.put("minFat",minFat);
	productDetailsJson.put("maxFat",maxFat);
	productDetailsJson.put("minSnf",minSnf);
	productDetailsJson.put("maxSnf",maxSnf);
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

List<GenericValue> unionsList = delegator.findList("PartyRoleAndPartyDetail",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"INTERNAL_ORGANIZATIO"), null, null, null, true);

JSONObject unionCodeJson = new JSONObject();
JSONArray unionItemsJSON = new JSONArray();
for(union in unionsList){
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
		
} 
context.put("partyCodeJson",unionCodeJson);
context.put("partyItemsJSON",unionItemsJSON);

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

 productFacilityDetails ='';
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
			productFacilityIdMap.putAt(eachProdId, eachProdFacilityIds);
		}
	}
}
context.putAt("productFacilityIdMap", productFacilityIdMap);



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
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;


//List<GenericValue> productCatMembers = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), context);
List<GenericValue> productCatMembers = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, UtilMisc.toList("RAW_MILK", "WHOLE_MILK")), null, null, null, false);

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
vehCondList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"TRANS_VEHICLE"));
vehCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null),EntityOperator.OR,EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate)));
EntityCondition vehCondition = EntityCondition.makeCondition(vehCondList);
List vehicleRoleList = delegator.findList("VehicleRole",vehCondition, null, null, null, true);

Set vehicleIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(vehicleRoleList, "vehicleId", false));
List<GenericValue> vehiclesList = delegator.findList("Vehicle",EntityCondition.makeCondition("vehicleId",EntityOperator.IN,vehicleIdsSet), null, null, null, true);

vehicleIdsList = EntityUtil.getFieldListFromEntityList(vehiclesList, "vehicleId", true);
context.put("vehiclesList", vehiclesList);

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

List<GenericValue> unionsList = delegator.findList("PartyRoleAndPartyDetail",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"UNION"), null, null, null, true);

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
context.putAt("rawMilkSilosList", rawMilkSilosList);

// Order Items

conditionList = [];
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
conditionList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "TANKER_SALES_CHANNEL"));
EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderHeaderList = delegator.findList("OrderHeader", cond, null,null, null, false);



JSONArray orderItemsJSON = new JSONArray();
for(eachOrder in orderHeaderList){
		
		JSONObject ordObjectJson = new JSONObject();
		ordObjectJson.put("value",eachOrder.get("orderId"));
		
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrder.get("orderId")));
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
		EntityCondition ordRoleCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		orderRoleList = delegator.findList("OrderRole", ordRoleCond, null,null, null, false);
		shipToPartyName = eachOrder.get("orderId");
		if(UtilValidate.isNotEmpty(orderRoleList)){
			shipToParty = EntityUtil.getFirst(orderRoleList);
			shipToPartyName=PartyHelper.getPartyName(delegator, shipToParty.get("partyId"), false);
			
			shipToPartyName = shipToPartyName + "-" + eachOrder.get("orderId");
		}
		ordObjectJson.put("label", shipToPartyName);
		
		orderItemsJSON.add(ordObjectJson);
		
		/*JSONObject vehDetJson = new JSONObject();
		vehDetJson.put("vehicleId",vehicle.get("vehicleId"));
		vehDetJson.put("vehicleName",vehicle.get("vehicleName"));
		
		vehicleCodeJson.put(vehicle.get("vehicleId"),vehDetJson);*/
		
}
context.put("orderItemsJSON",orderItemsJSON);
		
		shipmentIds=[];
		conditionList.clear();
		//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "TANKER_SALES"));
		
		EntityCondition shipCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<GenericValue> shipmentList = delegator.findList("Shipment", shipCond, null,UtilMisc.toList("routeId"), null, false);
		shipmentIds.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
		EntityCondition vhCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<GenericValue> vehicleTrpStatList = delegator.findList("VehicleTripAndStatusAndShipment", vhCondition, null, UtilMisc.toList("-estimatedStartDate","originFacilityId"), null, false);
		
		List<GenericValue> vehicleTripStatusList=FastList.newInstance();
		if(UtilValidate.isNotEmpty(vehicleTrpStatList)){
			for(i=0;i<shipmentIds.size();i++){
				shipmentId=shipmentIds.get(i);
				List<GenericValue> tempVehicleTripStatusList = EntityUtil.orderBy(EntityUtil.filterByCondition(vehicleTrpStatList, EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS , shipmentId)),["-estimatedStartDate"]);
				if(UtilValidate.isNotEmpty(tempVehicleTripStatusList)){
					 vehicleTripStatusMap=[:];
					 statusList=EntityUtil.getFieldListFromEntityList(tempVehicleTripStatusList, "statusId", false);
					
					 GenericValue vehicleTripStatus=EntityUtil.getFirst(tempVehicleTripStatusList);
					 //vehicleTripStatusMap.putAll(vehicleTripStatus);
					
					 vehicleTripStatusList.add(vehicleTripStatus);//only needs to get one valid status for each shipment which is recent one
					
				 }
			}
		}

		
		List<GenericValue> planningClearedShipList = EntityUtil.filterByCondition(vehicleTripStatusList, EntityCondition.makeCondition("statusId",EntityOperator.EQUALS , "TS_SHIPMENT_PLANNED"))
		List<GenericValue> cipClearedShipList = EntityUtil.filterByCondition(vehicleTripStatusList, EntityCondition.makeCondition("statusId",EntityOperator.EQUALS , "TS_CIP"))
		List<GenericValue> tareWtClearedShipList = EntityUtil.filterByCondition(vehicleTripStatusList, EntityCondition.makeCondition("statusId",EntityOperator.EQUALS , "TS_TARE_WEIGHT"))
		List<GenericValue> qcClearedShipList = EntityUtil.filterByCondition(vehicleTripStatusList, EntityCondition.makeCondition("statusId",EntityOperator.EQUALS , "TS_QC"))
		List<GenericValue> grossWtClearedShipList = EntityUtil.filterByCondition(vehicleTripStatusList, EntityCondition.makeCondition("statusId",EntityOperator.EQUALS , "TS_GROSS_WEIGHT"))
		
		
		context.put("planningClearedShipList",EntityUtil.filterByCondition(vehiclesList, EntityCondition.makeCondition("vehicleId",EntityOperator.IN , EntityUtil.getFieldListFromEntityList(planningClearedShipList, "vehicleId", false))));
		context.put("cipClearedShipList",EntityUtil.filterByCondition(vehiclesList, EntityCondition.makeCondition("vehicleId",EntityOperator.IN , EntityUtil.getFieldListFromEntityList(cipClearedShipList, "vehicleId", false))));
		context.put("tareWtClearedShipList",EntityUtil.filterByCondition(vehiclesList, EntityCondition.makeCondition("vehicleId",EntityOperator.IN , EntityUtil.getFieldListFromEntityList(tareWtClearedShipList, "vehicleId", false))));
		context.put("qcClearedShipList",EntityUtil.filterByCondition(vehiclesList, EntityCondition.makeCondition("vehicleId",EntityOperator.IN , EntityUtil.getFieldListFromEntityList(qcClearedShipList, "vehicleId", false))));
		context.put("grossWtClearedShipList",EntityUtil.filterByCondition(vehiclesList, EntityCondition.makeCondition("vehicleId",EntityOperator.IN , EntityUtil.getFieldListFromEntityList(grossWtClearedShipList, "vehicleId", false))));
		
		List<GenericValue> routesList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"TS_ROUTE"), null, null, null, true);

		JSONObject routesObjectJson = new JSONObject();
		JSONArray routeListJSON = new JSONArray();
		for(route in routesList){
				JSONObject routeObjectJson = new JSONObject();
				routeObjectJson.put("value",route.get("facilityId"));
				String label = route.get("facilityId");
				label = label + "-" + route.get("description")
				
				
				routeObjectJson.put("label",label);
				routeListJSON.add(routeObjectJson);
				
				JSONObject routeDetailJson = new JSONObject();
				routeDetailJson.put("routeId",route.get("facilityId"));
				routeDetailJson.put("description",route.get("description"));
				
				routesObjectJson.put(route.get("facilityId"),routeDetailJson);
				
		}
		context.put("routeListJSON",routeListJSON);
		context.put("routesObjectJson",routesObjectJson);

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
import org.ofbiz.party.party.PartyHelper;

// vehicles auto complete from vehicle master
List vehicleRoleList = delegator.findList("VehicleRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.NOT_EQUAL,"ROUTE_VEHICLE"),UtilMisc.toSet("vehicleId"),null,null,false);
JSONObject otherVehicleCodeJson = new JSONObject();
for(vehicle in vehicleRoleList){
	otherVehicleCodeJson.put(vehicle.get("vehicleId"),"Other Vehicle");
}
List<GenericValue> vehiclesList = delegator.findList("Vehicle",EntityCondition.makeCondition("vehicleId",EntityOperator.NOT_IN,EntityUtil.getFieldListFromEntityList(vehicleRoleList, "vehicleId", true)), null, null, null, true);

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
context.put("otherVehicleCodeJson", otherVehicleCodeJson);
List facilityIds = FastList.newInstance();
List productIds = FastList.newInstance();
List facilityGroupAndMemberAndFacility = delegator.findList("FacilityGroupAndMemberAndFacility",EntityCondition.makeCondition("primaryParentGroupId",EntityOperator.EQUALS,"MILK_SILO_GROUP"),UtilMisc.toSet("facilityId"),null,null,false);
if(UtilValidate.isNotEmpty(facilityGroupAndMemberAndFacility)){
	facilityIds = EntityUtil.getFieldListFromEntityList(facilityGroupAndMemberAndFacility, "facilityId", true);
	List productFacility = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIds),UtilMisc.toSet("productId"),null,null,false);
	if(UtilValidate.isNotEmpty(productFacility)){
		productIds = EntityUtil.getFieldListFromEntityList(productFacility, "productId", true);
	}
}
List productList = delegator.findList("Product",EntityCondition.makeCondition("productId",EntityOperator.NOT_IN,productIds),null,null,null,false);
JSONObject productJson = new JSONObject();
JSONArray productItemsJSON = new JSONArray();
for(product in productList){
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

String displayScreen = parameters.displayScreen;
if(UtilValidate.isEmpty(displayScreen)){
	displayScreen = context.displayScreen;
}
if(UtilValidate.isNotEmpty(displayScreen) && (displayScreen=="VEHICLE_OUT") || (displayScreen=="VEHICLE_TAREWEIGHT") || (displayScreen=="VEHICLE_GROSSWEIGHT")){
	List conList = FastList.newInstance();
	if(displayScreen=="VEHICLE_GROSSWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"WMNT_VCL_IN"));
	}
	if(displayScreen=="VEHICLE_TAREWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"WMNT_VCL_GRSWEIGHT"));
	}
	if(displayScreen=="VEHICLE_OUT"){
		List<String> statusList = FastList.newInstance();
		statusList.add("WMNT_VCL_TAREWEIGHT");
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,statusList));
	}
	conList.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
	EntityCondition ecl = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List vehicleTripStatus = delegator.findList("VehicleTripStatus",ecl,UtilMisc.toSet("vehicleId","sequenceNum","statusId"),null,null,false);
	List vehicleList = FastList.newInstance();
	vehicleTripStatus.each{trip->
		tempMap=[:];
		vehicleInTime = delegator.findOne("VehicleTripStatus",[vehicleId:trip.vehicleId,sequenceNum:trip.sequenceNum,statusId:"WMNT_VCL_IN"],false);
		List weighmentDetailsList = delegator.findList("WeighmentDetails",EntityCondition.makeCondition([EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,trip.vehicleId),
																							 EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,trip.sequenceNum)],EntityOperator.AND),UtilMisc.toSet("partyId"),null,null,false);
		String partyId="";
		String partyName="";
		if(weighmentDetailsList){
			GenericValue weighmentDetails = EntityUtil.getFirst(weighmentDetailsList);
			partyId = weighmentDetails.partyId;
			partyName = PartyHelper.getPartyName(delegator, partyId, false);
		}
		tempMap.partyId = partyName +"["+partyId+"]";
		tempMap.inTime = vehicleInTime.estimatedStartDate;
		tempMap.vehicleId = trip.vehicleId;
		vehicleList.add(tempMap);
	}
	context.vehicleList = vehicleList;
}
if(UtilValidate.isNotEmpty(displayScreen) && (displayScreen=="ISSUE_TARWEIGHT") || (displayScreen=="ISSUE_GRSWEIGHT") || (displayScreen=="ISSUE_OUT")){
	List conList = FastList.newInstance();
	if(displayScreen=="ISSUE_TARWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"WMNT_ISSUE_VCL_INIT"));
	}
	if(displayScreen=="ISSUE_GRSWEIGHT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"WMNT_ISSUE_VCL_TARE"));
	}
	if(displayScreen=="ISSUE_OUT"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"WMNT_ISSUE_VCL_GRS"));
	}
	
	conList.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
	EntityCondition ecl = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List vehicleTripStatus = delegator.findList("VehicleTripStatus",ecl,UtilMisc.toSet("vehicleId","sequenceNum","statusId"),null,null,false);
	List vehicleList = FastList.newInstance();
	vehicleTripStatus.each{trip->
		tempMap=[:];
		vehicleInTime = delegator.findOne("VehicleTripStatus",[vehicleId:trip.vehicleId,sequenceNum:trip.sequenceNum,statusId:"WMNT_ISSUE_VCL_INIT"],false);
		List weighmentDetailsList = delegator.findList("WeighmentDetails",EntityCondition.makeCondition([EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,trip.vehicleId),
																							 EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,trip.sequenceNum)],EntityOperator.AND),null,null,null,false);
		String partyId="";
		String weighmentId="";
		if(weighmentDetailsList){
			GenericValue weighmentDetails = EntityUtil.getFirst(weighmentDetailsList);
			weighmentId = weighmentDetails.weighmentId;
			List weighmentPartyList = delegator.findList("WeighmentParty",EntityCondition.makeCondition("weighmentId",EntityOperator.EQUALS,weighmentId),null,null,null,false);
			if(UtilValidate.isNotEmpty(weighmentPartyList) && weighmentPartyList.size()){
				weighmentPartyList.each{wmntParty->
					String partyName="";
					partyName = PartyHelper.getPartyName(delegator, wmntParty.partyId, false);
					if(UtilValidate.isNotEmpty(partyId)){
						partyId = partyId+", "+partyName+"["+wmntParty.partyId+"]";
					}else{
						partyId = partyName+"["+wmntParty.partyId+"]";
					}
				}
			}
		}
			/*partyId = weighmentDetails.partyIdTo;
			partyName = PartyHelper.getPartyName(delegator, partyId, false);*/
		tempMap.partyId = partyId;
		tempMap.inTime = vehicleInTime.estimatedStartDate;
		tempMap.vehicleId = trip.vehicleId;
		vehicleList.add(tempMap);
		}
		
	
	context.vehicleList = vehicleList;
}
//party lookup json
JSONArray partyJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();
partyList = delegator.findList("Party", EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "PARTY_ENABLED"),EntityOperator.OR,EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , null)), null, null, null, false);
if(UtilValidate.isNotEmpty(partyList)){
	partyList.each{party->
		JSONObject newObj = new JSONObject();
		newObj.put("value",party.partyId);
		partyName=PartyHelper.getPartyName(delegator, party.partyId, false);
		newObj.put("label",partyName+"["+party.partyId+"]");
		partyJSON.add(newObj);
		partyNameObj.put(party.partyId, partyName+"["+party.partyId+"]");
	}
}
context.partyNameObj = partyNameObj;
context.partyJSON = partyJSON;
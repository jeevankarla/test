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



detailsMap=[:];
detailsMap["quantity"]="";
detailsMap["quantityLtrs"]="";
detailsMap["fat"]="";
detailsMap["snf"]="";
detailsMap["acid"]="";
detailsMap["temp"]="";
detailsMap["clr"]="";
milkTankerCellsList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"MILK_CELL_TYPE"), null, ['sequenceId'], null, true);

JSONArray dataJSONList= new JSONArray();
milkTankerCellsList.each{ cellDetails->
	JSONObject newObj = new JSONObject(detailsMap);
	newObj.put("id",cellDetails.enumId);
	dataJSONList.add(newObj);
}
context.dataJSON = dataJSONList.toString();

JSONObject mccCodeJson = new JSONObject();
JSONArray mccItemsJSON = new JSONArray();

List conditionList  =FastList.newInstance();
conditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"UNIT"));
conditionList.add(EntityCondition.makeCondition("mccCode",EntityOperator.NOT_EQUAL,null));
EntityCondition condition = EntityCondition.makeCondition(conditionList);

List<GenericValue> unitsList = delegator.findList("Facility",condition, null,["mccCode"],null,false);
List tempUnitsList = FastList.newInstance();
for(unit in unitsList){
	Map tempUnitMap = FastMap.newInstance();
	tempUnitMap.put("facilityId", unit.get("facilityId"));
	tempUnitMap.put("facilityName", unit.get("facilityName"));
	tempUnitMap.put("parentFacilityId", unit.get("parentFacilityId"));
	tempUnitMap.put("mccCode",Integer.parseInt(unit.get("mccCode")));
	tempUnitsList.add(tempUnitMap);
}
unitsList = UtilMisc.sortMaps(tempUnitsList, UtilMisc.toList("mccCode"));
unitWiseList=[];
for(unit in unitsList){		
	JSONObject unitJson = new JSONObject();
	unitJson.put("name", unit.get("facilityName"));
	unitJson.put("facilityId", unit.get("facilityId"));
	facilityAttribute = delegator.findOne("FacilityAttribute", [facilityId : unit.get("parentFacilityId"), attrName:"enableQuantityKgs"], false);
	unitJson.put("showQtyKgs", "N");
	if(UtilValidate.isNotEmpty(facilityAttribute)){
		qtyConfigValue=facilityAttribute.attrValue;
		unitJson.put("showQtyKgs", qtyConfigValue);
	}
	mccCodeJson.put( unit.get("mccCode"), unitJson);
	if(UtilValidate.isNotEmpty(unit.get("mccCode"))){
		UnitMap=[:];
		UnitMap.put("mccCode", unit.get("mccCode"));
		UnitMap.put("mccName", unit.get("facilityName"));
		unitWiseList.addAll(UnitMap);
		
		JSONObject mccObjectJson = new JSONObject();
		mccObjectJson.put("value",unit.get("mccCode"));
		mccObjectJson.put("label",unit.get("facilityName")+"-"+unit.get("parentFacilityId")+ " [" + unit.get("mccCode") + "]");
		mccItemsJSON.add(mccObjectJson);
	}
}

context.putAt("unitWiseList", unitWiseList);
context.put("mccCodeJson",mccCodeJson);
context.put("mccItemsJSON",mccItemsJSON);
//List<GenericValue> productCatMembers = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), context);
//List productIds = EntityUtil.getFieldListFromEntityList(productCatMembers, "productId", false);
List<GenericValue> productCatMembers = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, UtilMisc.toList("RAW_MILK", "WHOLE_MILK")), null, null, null, false);
List productIds = EntityUtil.getFieldListFromEntityList(productCatMembers, "productId", false);
context.put("productsList", productCatMembers);

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
context.put("vehicleRoleList",vehicleRoleList);
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

List<GenericValue> unionsList = delegator.findList("PartyRoleAndPartyDetail",EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,UtilMisc.toList("UNION","CHILL_CENTER")), null, null, null, true);
context.unionsList = unionsList;
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
rawMilkSiloConditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"SILO"));
rawMilkSiloConditionList.add(EntityCondition.makeCondition("categoryTypeEnum",EntityOperator.EQUALS,"RAWMILK"));
EntityCondition rawMilkSiloCondition = EntityCondition.makeCondition(rawMilkSiloConditionList,EntityOperator.AND);
rawMilkSilosList = delegator.findList("Facility",rawMilkSiloCondition, null, null, null, true);
context.putAt("rawMilkSilosList", rawMilkSilosList);

List milkPurchasePurposeTypeList = FastList.newInstance();
milkPurchasePurposeTypeList = delegator.findList("Enumeration",EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"MILK_PRCH_PURP"), null, ["sequenceId"], null, true);

context.put("milkPurchasePurposeTypeList",milkPurchasePurposeTypeList);
String displayScreen = parameters.displayScreen;
if(UtilValidate.isEmpty(displayScreen)){
	displayScreen = context.displayScreen;
}
if(UtilValidate.isNotEmpty(displayScreen) && (displayScreen=="VEHICLE_CIPNEW") || (displayScreen=="VEHICLE_CIP") || (displayScreen=="VEHICLE_QC")){
	List conList = FastList.newInstance();
	if(displayScreen=="VEHICLE_CIP"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_VEHICLE_QC"));
	}
	if(displayScreen=="VEHICLE_CIPNEW"){								 
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_VEHICLE_UNLOAD"));
	}	
	if(displayScreen=="VEHICLE_QC"){
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MR_VEHICLE_GRSWEIGHT"));
	}
	conList.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
	EntityCondition ecl = EntityCondition.makeCondition(conList,EntityOperator.AND);
	List vehicleTripStatus = delegator.findList("VehicleTripStatus",ecl,UtilMisc.toSet("vehicleId","sequenceNum","statusId"),null,null,false);
	List vehicleList = FastList.newInstance();
	vehicleTripStatus.each{trip->
		tempMap=[:];
		vehicleInTime = delegator.findOne("VehicleTripStatus",[vehicleId:trip.vehicleId,sequenceNum:trip.sequenceNum,statusId:"MR_VEHICLE_IN"],false);
		List milkTransferList = delegator.findList("MilkTransfer",EntityCondition.makeCondition([EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,trip.vehicleId),
			                                                                                 EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,trip.sequenceNum)],EntityOperator.AND),UtilMisc.toSet("partyId"),null,null,false);
		String partyId="";
		String partyName="";
		if(milkTransferList){
			GenericValue milkTransfer = EntityUtil.getFirst(milkTransferList);
			partyId = milkTransfer.partyId;
			partyName = PartyHelper.getPartyName(delegator, partyId, false);
		}
		tempMap.partyId = partyName +"["+partyId+"]";
		tempMap.inTime = vehicleInTime.estimatedStartDate;
		tempMap.vehicleId = trip.vehicleId;
		vehicleList.add(tempMap);
	}
	context.vehicleList = vehicleList;
}


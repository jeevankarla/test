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

List<GenericValue> vehiclesList = delegator.findList("Vehicle",null, null, null, null, true);

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





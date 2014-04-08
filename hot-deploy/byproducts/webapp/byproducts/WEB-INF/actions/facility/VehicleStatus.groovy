/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;
import org.ofbiz.service.ServiceUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.Delegator;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastSet;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
result = ServiceUtil.returnSuccess();

rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();

List exprList = [];

if (parameters.supplyDate) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMMM, yyyy");
	try {
		supplyDate = UtilDateTime.toTimestamp(dateFormat.parse(parameters.supplyDate));
	} catch (Exception e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.supplyDate, "");
	}
}
else {
	supplyDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
}

if(parameters.facilityId){
	facilityId = parameters.facilityId;
}else{
	facilityId = context.facilityId;
}

hideSearch ="Y";
if(parameters.hideSearch){
	hideSearch = parameters.hideSearch;
}
/*if(context.hideSearch){
	hideSearch = context.hideSearch;
}
context.hideSearch = hideSearch;*/

dayBegin = UtilDateTime.getDayStart(supplyDate);
dayEnd = UtilDateTime.getDayEnd(supplyDate);


boothsResultMap = [:];
routeMap = [:];
tripMap = [:];
conditionList=[];
shipments = [];
routeId = parameters.routeId;
shipmentIds=[];
routeIdsList=[];
subscriptionTypeId=parameters.subscriptionTypeId;
shipmentTypeId="";
		if(UtilValidate.isNotEmpty(routeId)){
			conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
		}
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
		
		if(UtilValidate.isNotEmpty(subscriptionTypeId)){
			if("AM".equals(subscriptionTypeId)){
				conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "AM_SHIPMENT"));
			}else if("PM".equals(subscriptionTypeId)){
			  conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "PM_SHIPMENT"));
			}
		}
		/*conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));*/
		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<GenericValue> shipmentList = delegator.findList("Shipment", cond, null, null, null, false);
		shipmentIds.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false));
		routeIdsList.addAll(EntityUtil.getFieldListFromEntityList(shipmentList, "routeId", false))
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
		/*conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, routeId));*/
		//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("RETURN_CANCELLED")));
		EntityCondition vhCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List<GenericValue> vehicleTrpList = delegator.findList("VehicleTrip", vhCondition, null, null, null, false);
		List<GenericValue> vehicleTripStatusList=FastList.newInstance();
		List routeVehicleCratesList=FastList.newInstance();
		if(UtilValidate.isNotEmpty(vehicleTrpList)){
			for(i=0;i<vehicleTrpList.size();i++){
				Map vehicleCratesMap = FastMap.newInstance();
				GenericValue vehicleTrip=vehicleTrpList.get(i);
				shipmentId=vehicleTrip.getString("shipmentId");
				sequenceId=vehicleTrip.getString("sequenceNum");
				vehicleId=vehicleTrip.getString("vehicleId");
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceId));
				conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS, vehicleId));
				/*if(UtilValidate.isNotEmpty(routeId)){
				 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, routeId));
				 }*/
				EntityCondition vhTripCondi = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> tempVehicleTripStatusList = delegator.findList("VehicleTripStatus", vhTripCondi, null, UtilMisc.toList("-estimatedStartDate"), null, false);
				if(UtilValidate.isNotEmpty(tempVehicleTripStatusList)){
					GenericValue vehicleTripStatus=EntityUtil.getFirst(tempVehicleTripStatusList);
					vehicleTripStatusList.add(vehicleTripStatus);//only needs to get one valid status for each shipment which is recent one
					
					vehicleCratesMap.put("shipmentId", shipmentId);
					vehicleCratesMap.put("sequenceNum", sequenceId);
					vehicleCratesMap.put("vehicleId", vehicleId);
					vehicleCratesMap.put("statusId", vehicleTripStatus.statusId);
					vehicleCratesMap.put("facilityId",vehicleTripStatus.facilityId);
					cratesSent=0;cratesReceived=0;cansSent=0;cansReceived=0;
					/*issuanceTotalRes=ByProductNetworkServices.getItemIssuenceForShipments(dctx,[shipmentIds:UtilMisc.toList(shipmentId)]);
					if(UtilValidate.isNotEmpty(issuanceTotalRes)){
						if(UtilValidate.isNotEmpty(issuanceTotalRes.get("crateTotal"))){
							crateTotal=issuanceTotalRes.get("crateTotal");
						}
						if(UtilValidate.isNotEmpty(issuanceTotalRes.get("canTotal"))){
							canTotal=issuanceTotalRes.get("canTotal");
						}
					}*/
					GenericValue crateCanAcct = delegator.findOne("CrateCanAccount", UtilMisc.toMap("shipmentId", shipmentId), false);
					if(UtilValidate.isNotEmpty(crateCanAcct)){
						cratesSent=crateCanAcct.cratesSent;
						cratesReceived=crateCanAcct.cratesReceived;
						cansSent=crateCanAcct.cansSent;
						cansReceived=crateCanAcct.cansReceived;
					}
					vehicleCratesMap.put("cratesSent",cratesSent);
					vehicleCratesMap.put("cratesReceived",cratesReceived);
					vehicleCratesMap.put("cansSent",cansSent);
					vehicleCratesMap.put("cansReceived",cansReceived);
					routeVehicleCratesList.add(vehicleCratesMap);
				 }
			}
		}

result.vehicleTripStatusList = vehicleTripStatusList;
context.vehicleTripStatusList = vehicleTripStatusList;
context.routeVehicleCratesList = routeVehicleCratesList;
return result;

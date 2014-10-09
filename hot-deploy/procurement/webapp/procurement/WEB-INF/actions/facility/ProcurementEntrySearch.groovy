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

import java.util.List;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

String fromDate = null;
String thruDate = null;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	  customTimePeriod = delegator.findOne("CustomTimePeriod",[customTimePeriodId:parameters.customTimePeriodId],false);
	  if(UtilValidate.isNotEmpty(customTimePeriod)){
		   		fromDate = customTimePeriod.fromDate;
				thruDate = customTimePeriod.thruDate;
		  }
	}
if(fromDate){
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.util.Date fromParsedDate = dateFormat.parse(fromDate);
	java.sql.Timestamp fromTimestamp = new java.sql.Timestamp(fromParsedDate.getTime());
	java.sql.Timestamp fromStartTime =  UtilDateTime.getDayStart(fromTimestamp);
	java.sql.Timestamp fromEndTime =  UtilDateTime.getDayEnd(fromTimestamp);
	
	//perform estimatedDeliveryDate perform find by fromDate and ThruDate.
	parameters.estimatedDeliveryDate_fld0_value = (String)fromStartTime ;
	parameters.estimatedDeliveryDate_fld0_op = "greaterThanFromDayStart";
	context.fromDate = fromTimestamp;
	if(thruDate){
		java.util.Date thruParsedDate = dateFormat.parse(thruDate);
		java.sql.Timestamp thruTimestamp = new java.sql.Timestamp(thruParsedDate.getTime());
		java.sql.Timestamp thruEndTime =  UtilDateTime.getDayEnd(thruTimestamp);
		thruDateTimeStamp = (String)thruEndTime ;
	}else{
		thruDateTimeStamp = (String)fromEndTime ;
	}
	parameters.estimatedDeliveryDate_fld1_value = thruDateTimeStamp;
	parameters.estimatedDeliveryDate_fld1_op = "opLessThan";
}
List condList=[];
/*parameters.orderTypeId = "PURCHASE_ORDER";
parameters.purposeTypeId = "MILK_PROCUREMENT";
*/
//this fromdate we are using in  foxpro export

	dctx = dispatcher.getDispatchContext();		
	if(!UtilValidate.isEmpty(parameters.unitCode) && !UtilValidate.isEmpty(parameters.facilityCode)){
		GenericValue facility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, UtilMisc.toMap("shedId", parameters.shedId,"centerCode", parameters.facilityCode,"unitCode",parameters.unitCode))).get("agentFacility");
		if(UtilValidate.isEmpty(facility)){
			context.errorMessage = "No Facility found with UnitCode:"+parameters.unitCode+" center Code : "+parameters.facilityCode ;
			return ;
		}else{
			parameters.originFacilityId = UtilMisc.toList(facility.getString("facilityId"));
			parameters.originFacilityId_op = "in";
		}
	}else if(!UtilValidate.isEmpty(parameters.routeCode) && !UtilValidate.isEmpty(parameters.unitCode)){
		condList.clear();
		condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"UNIT"));
		condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS,parameters.shedId));
		condList.add(EntityCondition.makeCondition("facilityCode",EntityOperator.EQUALS,parameters.unitCode));
		List<GenericValue> unitDetail = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
			
		condList.clear();
		condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN,UtilMisc.toList("PROC_ROUTE")));
		condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS,unitDetail.facilityId));
		condList.add(EntityCondition.makeCondition("facilityCode", parameters.routeCode));
		List<GenericValue> routeDetail = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
		routeData = routeDetail.get(0);
		routeCentersList = (ProcurementNetworkServices.getRouteAgents(dctx,UtilMisc.toMap("routeId",routeData.facilityId)).get("agentsList"));
		
		parameters.originFacilityId = routeCentersList;
		parameters.originFacilityId_op = "in";
	}else if(!UtilValidate.isEmpty(parameters.unitCode)){
		List<GenericValue> facilityList= (ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("shedId", parameters.shedId ,"unitCode", parameters.unitCode))).get("agentsList");
		parameters.originFacilityId = EntityUtil.getFieldListFromEntityList(facilityList,"facilityId" , true);
		parameters.originFacilityId_op = "in";
	}else if(!UtilValidate.isEmpty(parameters.facilityCode)){		
		condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN,UtilMisc.toList("CENTER")));
		condList.add(EntityCondition.makeCondition("facilityCode", parameters.facilityCode));
		List<GenericValue> facilityList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
		parameters.originFacilityId = EntityUtil.getFieldListFromEntityList(facilityList,"facilityId" , true);
		parameters.originFacilityId_op = "in";
	}else if(!UtilValidate.isEmpty(parameters.unitId)){
		List facilityList = (ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("unitId", parameters.unitId))).get("agentsList");			
		parameters.originFacilityId = EntityUtil.getFieldListFromEntityList(facilityList,"facilityId" , true);
		parameters.originFacilityId_op = "in";
	}else if(!UtilValidate.isEmpty(parameters.shedId)){
		if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
			unitIdsList=(List)(ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId])).get("unitsList");
			List finalCentersList=FastList.newInstance();
			unitIdsList.each{ unitId->
				List centerList = (ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("unitId", unitId))).get("agentsList");
				centerIds=EntityUtil.getFieldListFromEntityList(centerList,"facilityId" , true)
				finalCentersList.addAll(centerIds);
			}
			parameters.originFacilityId = finalCentersList;
			parameters.originFacilityId_op = "in";
		}else{
			List facilityList = (ProcurementNetworkServices.getShedAgents(dctx, UtilMisc.toMap("shedId", parameters.shedId))).get("agentsList");			
			parameters.originFacilityId = facilityList;
			parameters.originFacilityId_op = "in";
		}
	}
if(parameters.hideSearch == 'Y'){
	 //parameters.orderDate =null;
	 parameters.orderId =null;
	 parameters.fat =null;
	 parameters.snf =null;
	 parameters.quantity =null;
	 parameters.quantityKgs =null;
	 parameters.quantityLtrs =null;
	 parameters.sFat =null;
	 parameters.sQuantity =null;
	 parameters.cQuantity =null; 
}

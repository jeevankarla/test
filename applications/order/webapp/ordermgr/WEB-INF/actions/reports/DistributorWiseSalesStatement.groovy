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

import java.io.ObjectOutputStream.DebugTraceInfoStack;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.DispatchContext;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import javolution.util.FastList;



amTruckList =[];
pmTruckList =[];
amTruckList = context.amTruckList;
pmTruckList = context.pmTruckList;
resultDistList = [];
List<GenericValue>  distributorFacilites= delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DISTRIBUTOR"), null, UtilMisc.toList("sequenceNum","facilityName"), null, false);
firstDistributorId = (distributorFacilites.get(0)).facilityId;

distributorFacilites.each{ distributorFacility ->
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, distributorFacility.facilityId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "DISTRIBUTOR"));	
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	List<GenericValue>  zoneFacilities= delegator.findList("Facility", condition, null, UtilMisc.toList("sequenceNum","facilityName"), null, false);
	Set zoneSet = new HashSet(EntityUtil.getFieldListFromEntityList(zoneFacilities, "facilityId", false));
	tempDistMap =[:];
	//AM Zone totals
	amTruckList.each{ amTruck ->		
		if((amTruck.facilityId.equals(distributorFacility.facilityId))){
			tempDistMap = amTruck;
		}else if((zoneSet.contains(amTruck.facilityId))){
			amTruck.facilityId = amTruck.facilityId +"-M";
			resultDistList.add(amTruck);
		}		
	}
	
	if(tempDistMap){
		resultDistList.add(tempDistMap);
		tempDistMap=[:];
	}
	
	//PM Zone totals
	pmTruckList.each{ pmTruck ->		
		if((pmTruck.facilityId.equals(distributorFacility.facilityId))){
			tempDistMap = pmTruck;			
		}else if((zoneSet.contains(pmTruck.facilityId) )){
			pmTruck.facilityId = pmTruck.facilityId +"-E";
			resultDistList.add(pmTruck);
		}		
	}
	if(tempDistMap){		
		resultDistList.add(tempDistMap);		
	}
}
getZonesComissionRates = NetworkServices.getZonesComissionRates(dctx, context);
context.zoneComissionRates = getZonesComissionRates.zonesComissionRates;
context.firstDistributorId = firstDistributorId;
context.resultDistList = resultDistList;



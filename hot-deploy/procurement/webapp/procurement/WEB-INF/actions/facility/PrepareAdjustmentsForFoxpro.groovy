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
import in.vasista.vbiz.procurement.ProcurementServices;

shedId = parameters.shedId;
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
  	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
customTimePeriodId = parameters.customTimePeriodId;
if(UtilValidate.isEmpty(shedId)){
	context.errorMessage = "Shed is Not Selected";
	return ;
	}
dctx = dispatcher.getDispatchContext();
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
shedUnits = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);
unitIds=shedUnits.unitsList;
centerIdsList=[];
unitIds.each{ unitId->
	List centerList = (ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("unitId", unitId))).get("agentsList");
	centerIds=EntityUtil.getFieldListFromEntityList(centerList,"facilityId" , true);
	centerIdsList.addAll(centerIds);
}
orderAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityIds:centerIdsList]);
centerWiseAdjustments = orderAdjustments.centerWiseAdjustments;
additionsListFoxpro = [];
deductionsListFoxpro = [];
if(UtilValidate.isNotEmpty(centerWiseAdjustments)){
	centerAdjustments = centerWiseAdjustments.entrySet();
	for(center in centerAdjustments){
		    centerId = center.getKey();
			centerDetails = delegator.findOne("Facility",[facilityId:centerId],false);
			routeDetails =[:];
			unitDetails = [:];
			cCode = "";
			uCode = "";
			rNo  = "";
			context.putAt("centerId", centerId);
			agentDetails = ProcurementNetworkServices.getCenterDtails(dctx ,context);
			if(UtilValidate.isNotEmpty(agentDetails.get("centerFacility"))){
				cCode = (agentDetails.get("centerFacility")).facilityCode;				
			}
			if(UtilValidate.isNotEmpty(agentDetails.get("routeFacility"))){
				rNo = (agentDetails.get("routeFacility")).facilityCode;
			}
			
			if(UtilValidate.isNotEmpty(agentDetails.get("unitFacility"))){
				uCode = (agentDetails.get("unitFacility")).facilityCode;
			}
			/*if(UtilValidate.isNotEmpty(agentDetails)){
				cCode = centerDetails.facilityCode;
				routeDetails = delegator.findOne("Facility",[facilityId:centerDetails.facilityId],false);
				}
			if(UtilValidate.isNotEmpty(routeDetails)){
				rNo = routeDetails.facilityCode;
				unitDetails = delegator.findOne("Facility",[facilityId:centerDetails.facilityId],false);
				}
			if(UtilValidate.isNotEmpty(unitDetails)){
				uCode = unitDetails.facilityCode;
				}*/

			adjustments = [:];
			adjustments = center.getValue();
			centerAdditions = [:];
			centerDeductions = [:];
			if(UtilValidate.isNotEmpty(adjustments)){
				centerAdditions = adjustments.MILKPROC_ADDITIONS;
				centerDeductions = adjustments.MILKPROC_DEDUCTIONS;
				}
			
			additionsMap = [:];
			deductuionsMap = [:];
			if(UtilValidate.isNotEmpty(centerAdditions)){
				additionsMap.putAll(centerAdditions);
				additionsMap.put("DDATE", thruDate);
				additionsMap.put("UCODE",uCode);
				additionsMap.put("CCODE",cCode);
				additionsMap.put("RNO",rNo);
				additionsListFoxpro.add(additionsMap);
				}
			if(UtilValidate.isNotEmpty(centerDeductions)){
				deductuionsMap.putAll(centerDeductions);
				deductuionsMap.put("DDATE", thruDate);
				deductuionsMap.put("UCODE",uCode);
				deductuionsMap.put("CCODE",cCode);
				deductuionsMap.put("RNO",rNo);
				deductionsListFoxpro.add(deductuionsMap);
				}
		}
}
context.put("additionsListFoxpro",additionsListFoxpro);
context.put("deductionsListFoxpro",deductionsListFoxpro);

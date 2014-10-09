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
import org.ofbiz.base.util.*;
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
import net.sf.json.JSONObject;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;


additionsAdjList =[];
dctx = dispatcher.getDispatchContext();
context.putAt("dctx", dctx);
additionsListIterator = context.adjustmentsList;
if(UtilValidate.isNotEmpty(additionsListIterator)){

	additionsListIterator.each { additions ->
		tempEntry =[:];
		/*tempEntry.putAll(additions);*/
		Map centerDetails = ProcurementNetworkServices.getCenterDtails(dctx, UtilMisc.toMap("centerId", additions.originFacilityId));
		GenericValue centerFacility = (GenericValue)(centerDetails).get("centerFacility");
		GenericValue unitFacility = (GenericValue)(centerDetails).get("unitFacility");
		GenericValue shedFacility = (GenericValue)(centerDetails).get("shedFacility");
		tempEntry["centerCode"] = centerFacility.getString("facilityCode");
		tempEntry["unitCode"] = unitFacility.getString("facilityCode");
		tempEntry["shedCode"] = shedFacility.getString("facilityCode");
		tempEntry["adjustmentType"]=additions.orderAdjustmentTypeId;
		tempEntry["amount"]=additions.amount;
		tempEntry["orderDate"]=UtilDateTime.toDateString(additions.orderDate, "dd/MM/yyyy");		
		additionsAdjList.add(tempEntry);
	}
	//additionsListIterator.close();
	context.additionsAdjList = additionsAdjList;
}
JSONObject lasAdjChangeJson = new JSONObject();
if(UtilValidate.isNotEmpty(additionsAdjList)){
	lasAdjChangeJson = additionsAdjList.get(0);
}
if(UtilValidate.isNotEmpty(lasAdjChangeJson.centerCode)){
	lasAdjChangeJson.putAt("centerCode",lasAdjChangeJson.centerCode);
}
if(UtilValidate.isNotEmpty(lasAdjChangeJson.unitCode)){
	lasAdjChangeJson.putAt("unitCode",lasAdjChangeJson.unitCode);
	}
if(UtilValidate.isNotEmpty(lasAdjChangeJson.shedCode)){
	lasAdjChangeJson.putAt("shedCode", lasAdjChangeJson.shedCode);
}
if(UtilValidate.isNotEmpty(lasAdjChangeJson.adjustmentType)){
	lasAdjChangeJson.putAt("adjustmentType", lasAdjChangeJson.adjustmentType);
}
if(UtilValidate.isNotEmpty(lasAdjChangeJson.amount)){
	lasAdjChangeJson.putAt("amount", lasAdjChangeJson.amount);
}
if(UtilValidate.isNotEmpty(lasAdjChangeJson.orderDate)){
	lasAdjChangeJson.putAt("orderDate", lasAdjChangeJson.orderDate);
}
context.lasAdjChangeJson = lasAdjChangeJson;
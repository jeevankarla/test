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



dctx = dispatcher.getDispatchContext();
listProcurementOrdersIterator = context.listProcurementOrders;
if(UtilValidate.isNotEmpty(listProcurementOrdersIterator)){	

	List<GenericValue> listProcurementOrders = listProcurementOrdersIterator.getCompleteList();
	procurementEntryList =[];
	listProcurementOrders.each { procurementEntry ->
		tempEntry =[:];
		tempEntry.putAll(procurementEntry);
		Map centerDetails = ProcurementNetworkServices.getCenterDtails(dctx, UtilMisc.toMap("centerId", procurementEntry.originFacilityId));
		GenericValue centerFacility = (GenericValue)(centerDetails).get("centerFacility");
		GenericValue unitFacility = (GenericValue)(centerDetails).get("unitFacility");
		GenericValue shedFacility = (GenericValue)(centerDetails).get("shedFacility");
		tempEntry["centerCode"] = centerFacility.getString("facilityCode");
		tempEntry["unitCode"] = unitFacility.getString("facilityCode");
		tempEntry["shedCode"] = shedFacility.getString("facilityCode"); 
		tempEntry["purchaseTime"] = procurementEntry.supplyTypeEnumId;
		if (procurementEntry.unitPrice != null && procurementEntry.quantity != null) {
			tempEntry["totalAmount"] =  (procurementEntry.quantity)*(procurementEntry.unitPrice);
		}
		
		if (procurementEntry.unitPremiumPrice != null && procurementEntry.quantity != null) {
			tempEntry["totalPremium"] =  (procurementEntry.quantity)*(procurementEntry.unitPremiumPrice);
		}
		if (procurementEntry.sQuantityLtrs != null && procurementEntry.sUnitPrice != null) {
			tempEntry["sTotalAmount"] =  (procurementEntry.sQuantityLtrs)*(procurementEntry.sUnitPrice);
		}
		procurementEntryList.add(tempEntry);
	}
	listProcurementOrdersIterator.close();
	context.procurementEntryList = procurementEntryList;	
}


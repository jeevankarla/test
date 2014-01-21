import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;

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

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.network.NetworkServices;
parameters.boothId = parameters.facilityId_0;
if(!UtilValidate.isEmpty(parameters.boothId)){	
	bothIds =FastList.newInstance();
		GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", parameters.boothId), true);
		if(!UtilValidate.isEmpty(facilityDetail)){
			if(facilityDetail.getString("facilityTypeId").equals("ZONE")){
				parameters.boothId  = (Collection)NetworkServices.getZoneBooths(delegator,parameters.boothId);
				parameters.boothId_op = "in";
			}else if (facilityDetail.getString("facilityTypeId").equals("ROUTE")){
				parameters.boothId = (Collection)NetworkServices.getRouteBooths(delegator,parameters.boothId);
				parameters.boothId_op = "in";
			}
			
		}
}

milkCardOrderDate = parameters.milkCardOrderDate;
if(!UtilValidate.isEmpty(milkCardOrderDate)){
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	java.util.Date parsedDate = dateFormat.parse(milkCardOrderDate);
	java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
	startTime =  UtilDateTime.getDayStart(timestamp);
	endTime =  UtilDateTime.getDayEnd(timestamp);
	parameters.orderDate_fld0_value	= startTime;
	parameters.orderDate_fld0_op	= "greaterThan";
	parameters.orderDate_fld1_value	= endTime;
	parameters.orderDate_fld1_op	= "opLessThan";
}
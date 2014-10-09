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

import java.sql.Timestamp;
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

fromDate =  parameters.fromDate;
thruDate =  parameters.thruDate;
java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
if(UtilValidate.isNotEmpty(fromDate)){
	
	java.util.Date fromParsedDate = dateFormat.parse(fromDate);
	java.sql.Timestamp fromTimestamp = UtilDateTime.getDayStart(new java.sql.Timestamp(fromParsedDate.getTime()));
	java.sql.Timestamp thruTimestamp = UtilDateTime.getDayEnd(new java.sql.Timestamp(fromParsedDate.getTime()));
	
	parameters.sendDate_fld0_op = "greaterThanFromDayStart";
	parameters.sendDate_fld0_value = fromTimestamp.toString();
	parameters.sendDate_fld1_op = "opLessThan";
	parameters.sendDate_fld1_value = thruTimestamp.toString();
	
	//parameters.remove("fromDate");
	
}
if(UtilValidate.isNotEmpty(thruDate)){
	java.util.Date thruParsedDate = dateFormat.parse(thruDate);
	java.sql.Timestamp thruTimestamp = UtilDateTime.getDayEnd(new java.sql.Timestamp(thruParsedDate.getTime()));

	parameters.sendDate_fld1_op = "opLessThan";
	parameters.sendDate_fld1_value = thruTimestamp.toString();
	
	//parameters.remove("fromDate");
}


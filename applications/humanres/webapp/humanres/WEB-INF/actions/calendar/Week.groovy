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

import java.util.*;
import org.ofbiz.security.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.webapp.pseudotag.*;
import org.ofbiz.workeffort.workeffort.*;
import java.sql.Timestamp;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

String currentDay = parameters.get("currentDay");
String startParam = parameters.get("start");

facilityId = parameters.get("facilityId");
fixedAssetId = parameters.get("fixedAssetId");
//partyId = parameters.get("partyId");
employeeId = parameters.get("employeeId");
workEffortTypeId = parameters.get("workEffortTypeId");
String eventsParam = ""; 
if(employeeId  != null)
{
party = delegator.findByPrimaryKey("Party", [partyId : employeeId]); 
partyId = party.partyId;
eventsParam = "partyId=" + partyId;

}

if (facilityId != null) {
    eventsParam = "facilityId=" + facilityId;
}
if (fixedAssetId != null) {
    eventsParam = "fixedAssetId=" + fixedAssetId;
}
if (partyId != null) {
    eventsParam = "partyId=" + partyId;
}


if (workEffortTypeId != null) {
    eventsParam = "workEffortTypeId=" + workEffortTypeId;
}



Timestamp start = null;
if(startParam != null) 
    start = new Timestamp(Long.parseLong(startParam));
    
if(start == null) {
    start = UtilDateTime.getWeekStart(nowTimestamp, timeZone, locale);
} else {
    start = UtilDateTime.getWeekStart(start, timeZone, locale);
}
    
Timestamp prev = UtilDateTime.getDayStart(start, -7, timeZone, locale);
Timestamp next = UtilDateTime.getDayStart(start, 7, timeZone, locale);
Timestamp end = UtilDateTime.getDayStart(start, 6, timeZone, locale);

Map serviceCtx = UtilMisc.toMap("userLogin", userLogin,"start",start,"numPeriods",new Integer(7),"periodType",new Integer(Calendar.DATE));
serviceCtx.putAll(UtilMisc.toMap("partyId", partyId, "facilityId", facilityId, "fixedAssetId", fixedAssetId, "workEffortTypeId", workEffortTypeId, "locale", locale, "timeZone", timeZone));

Map result = dispatcher.runSync("getWorkEffortEventsByPeriod",serviceCtx);
context.put("periods",result.get("periods"));
context.put("maxConcurrentEntries",result.get("maxConcurrentEntries"));
context.put("start",start);
context.put("end",end);
context.put("prev",prev);
context.put("next",next);
context.put("eventsParam", eventsParam);

// getting punch in type list

punchList = UtilMisc.toList(
	EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "PUNCH_TYPE"));
condition = EntityCondition.makeCondition(punchList, EntityOperator.AND);
punchTypeList=delegator.findList("Enumeration" , condition, null, ['sequenceId'],null,true);
context.put("punchTypeList",punchTypeList);

inOutTypeList = UtilMisc.toList(
	EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "INOUT_TYPE"));
condition = EntityCondition.makeCondition(inOutTypeList, EntityOperator.AND);
inOutList=delegator.findList("Enumeration" , condition, null, ['sequenceId'],null,true);
context.put("inOutList",inOutList);

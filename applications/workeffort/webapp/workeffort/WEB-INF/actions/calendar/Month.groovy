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

import java.sql.*;
import java.text.*;
import java.util.*;
import org.ofbiz.security.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.webapp.pseudotag.*;
import org.ofbiz.workeffort.workeffort.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityUtil;

localDispatcherName=parameters.localDispatcherName;
scopeEnumId="";
 	periods = delegator.findByAnd("CustomTimePeriod", [periodTypeId : "HR_YEAR"]);
	period = EntityUtil.getFirst(periods);
            if (period) {
               fromDate = period.fromDate;
                if(periods[1] && periods[1].thruDate > nowTimestamp){
                thruDate =periods[1].thruDate; 
                } else{
                thruDate =period.thruDate; 
               	  }            
            }
       //Debug.logInfo("in CustomTimePeriod month " +  fromDate,"");
startParam = parameters.start;

facilityId = parameters.facilityId;
fixedAssetId = parameters.fixedAssetId;
partyId = parameters.partyId;
workEffortTypeId = parameters.workEffortTypeId;
calendarType = parameters.calendarType;
entityExprList = context.entityExprList;

start = null;
if (UtilValidate.isNotEmpty(startParam)) {
    start = new Timestamp(Long.parseLong(startParam));
    }
if (start == null) {
    start = UtilDateTime.getMonthStart(nowTimestamp, timeZone, locale);
} else {
     start = UtilDateTime.getMonthStart(start, timeZone, locale);
}

tempCal = UtilDateTime.toCalendar(start, timeZone, locale);
numDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

prev = UtilDateTime.getMonthStart(start, -1, timeZone, locale);
if(prev < fromDate ){
context.put("prevFlag","false");
}
else{
context.put("prevFlag","true");
}
context.prevMillis = new Long(prev.getTime()).toString();
next = UtilDateTime.getDayStart(start, numDays+1, timeZone, locale);
if(next > thruDate ){
context.put("nextFlag","false");
}
else{
context.put("nextFlag","true");
}
context.nextMillis = new Long(next.getTime()).toString();
end = UtilDateTime.getMonthEnd(start, timeZone, locale);

//Find out what date to get from
getFrom = null;
prevMonthDays =  tempCal.get(Calendar.DAY_OF_WEEK) - tempCal.getFirstDayOfWeek();
if (prevMonthDays < 0) prevMonthDays += 7;
tempCal.add(Calendar.DATE, -prevMonthDays);
numDays += prevMonthDays;
getFrom = new Timestamp(tempCal.getTimeInMillis());
firstWeekNum = tempCal.get(Calendar.WEEK_OF_YEAR);
context.put("firstWeekNum", firstWeekNum);

// also get days until the end of the week at the end of the month
lastWeekCal = UtilDateTime.toCalendar(end, timeZone, locale);
monthEndDay = lastWeekCal.get(Calendar.DAY_OF_WEEK);
getTo = UtilDateTime.getWeekEnd(end, timeZone, locale);
lastWeekCal = UtilDateTime.toCalendar(getTo, timeZone, locale);
followingMonthDays = lastWeekCal.get(Calendar.DAY_OF_WEEK) - monthEndDay;
if (followingMonthDays < 0) {
    followingMonthDays += 7;
}
numDays += followingMonthDays; 
if(localDispatcherName == "Humanres"){
scopeEnumId="WES_HR";
}
serviceCtx = UtilMisc.toMap("userLogin", userLogin, "start", getFrom, "numPeriods", numDays, "periodType", Calendar.DATE ,"scopeEnumId",scopeEnumId);
serviceCtx.putAll(UtilMisc.toMap("partyId", partyId, "facilityId", facilityId, "fixedAssetId", fixedAssetId, "workEffortTypeId", workEffortTypeId, "calendarType", calendarType,"scopeEnumId",scopeEnumId, "locale", locale, "timeZone", timeZone));
if (entityExprList) {
    serviceCtx.putAll(["entityExprList" : entityExprList]);
}

result = dispatcher.runSync("getWorkEffortEventsByPeriod", serviceCtx);

context.put("periods",result.get("periods"));
context.put("maxConcurrentEntries", result.get("maxConcurrentEntries"));
context.put("start", start);
context.put("end", end);
context.put("prev", prev);
context.put("next", next);


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

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilMisc;
import java.text.ParseException;

userLogin= context.userLogin;
//finAccount Reconcilation Report will use this logic....
SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");
if(UtilValidate.isNotEmpty(parameters.fromDate)){
   Timestamp daystart;
	try {
		daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.fromDate));
		 } catch (ParseException e) {
			 Debug.logError(e, "Cannot parse date string: " + parameters.fromDate, "");
			 }
   parameters.fromDateReport=UtilDateTime.getDayStart(daystart);
}
if(UtilValidate.isNotEmpty(parameters.thruDate)){
   Timestamp dayend;
   try {
	   dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.thruDate));
   } catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + parameters.thruDate, "");
		}
   parameters.thruDateReport=UtilDateTime.getDayEnd(dayend);
}
reconciledDate=parameters.reconciledDate;
if(UtilValidate.isNotEmpty(reconciledDate)){
Timestamp fromDateTs = null;
if(reconciledDate){
		SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
	try {
		fromDateTs = new java.sql.Timestamp(sdfo.parse(reconciledDate).getTime());	} catch (ParseException e) {
	}
}
parameters.reconciledDateStart = UtilDateTime.getDayStart(fromDateTs);
parameters.reconciledDateEnd = UtilDateTime.getDayEnd(fromDateTs);
}

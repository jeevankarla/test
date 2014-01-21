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

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.network.NetworkServices;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.salesDate) {
		salesDate = new java.sql.Timestamp(sdf.parse(parameters.salesDate).getTime());
	}
	else {
		salesDate = UtilDateTime.nowTimestamp();
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

Debug.logInfo("salesDate="+salesDate, "");

zoneReportList = [];
dayTotals = NetworkServices.getDayTotals(dispatcher.getDispatchContext(), salesDate, false, false);
zonesMap = dayTotals.zoneTotals;
Iterator mapIter = zonesMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	zoneReportList.add(entry.getValue());
}
//Debug.logInfo("zoneReportList"+zoneReportList, "");
context.zoneReportList = zoneReportList;

productReportList = [];
productsMap = dayTotals.productTotals;
mapIter = productsMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	productReportList.add(entry.getValue());
}
//Debug.logInfo("productReportList"+productReportList, "");
context.productReportList = productReportList;

supplyTypeReportList = [];
supplyTypeMap = dayTotals.supplyTypeTotals;
mapIter = supplyTypeMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	supplyTypeReportList.add(entry.getValue());
}
Debug.logInfo("supplyTypeReportList"+supplyTypeReportList, "");
context.supplyTypeReportList = supplyTypeReportList;
context.totalQuantity=dayTotals.totalQuantity;
context.totalRevenue=dayTotals.totalRevenue;
context.totalFat=dayTotals.totalFat;
context.totalSnf=dayTotals.totalSnf;
context.salesDate=salesDate;


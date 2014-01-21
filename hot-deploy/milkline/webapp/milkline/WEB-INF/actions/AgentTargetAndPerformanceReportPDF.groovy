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
import java.util.Map;



import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.product.product.ProductWorker;


fromDate = parameters.fromDate;
thruDate = parameters.thruDate;


facilityId = parameters.facilityId;
dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
	context.put("fromDateTime", fromDateTime);
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
	context.put("thruDateTime", thruDateTime);
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}

monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);

context.put("dayBegin",monthBegin);
context.put("dayEnd",monthEnd);
lmsproductList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
byProductsList=ProductWorker.getProductsByCategory(delegator ,"BYPROD" ,null);

lmsproductList=  EntityUtil.filterByCondition(lmsproductList, EntityCondition.makeCondition("productId",EntityOperator.NOT_IN , byProductsList.productId));
context.put("lmsProductList", lmsproductList);
context.put("byProductsList", byProductsList);


conditionList =[];

if(parameters.facilityId !="All-Routes"){
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , parameters.facilityId));
}
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
routeList = delegator.findList("Facility",condition,null,null,null,false);

allRoutesGrandTotalMap =[:];

routeWiseList=[];
lmsProductIdsList = EntityUtil.getFieldListFromEntityList(lmsproductList, "productId", false);
byProductIdsList= EntityUtil.getFieldListFromEntityList(byProductsList, "productId", false);

Map routeWiseMap = FastMap.newInstance();

monthDaysMap=[:]
for(int j=0 ; j < (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)+1); j++){
	Timestamp saleDate = UtilDateTime.addDaysToTimestamp(monthBegin, j);
	dayNo=UtilDateTime.getDayOfMonth(saleDate,timeZone, locale);
	//curntDay=UtilDateTime.toDateString(saleDate ,"yyyy-MM-dd");
	monthDaysMap[j] =dayNo ;
}

context.put("monthDaysMap",monthDaysMap);

for(int i=0; i< routeList.size();i++){
	route = routeList.get(i);
	boothTotalMap=[:];
	boothsList=NetworkServices.getBoothList(delegator ,route.facilityId);//getting list of Booths

	boothsList.each{ boothId->
		totalsMap= [:];
		dayTotalsMap = [:];
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , boothId));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO  ,monthBegin));
		conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"SALES_MONTH"));
		conditionList.add(EntityCondition.makeCondition("uomId", EntityOperator.EQUALS ,"INR"));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fieldsToSelect = ["targetValue"]as Set;
		facilityTargetList= delegator.findList("FacilityTarget",condition,fieldsToSelect,null,null,false);

		facilityTargetList=EntityUtil.filterByDate(facilityTargetList);
		targetAmount=BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(facilityTargetList)){
			facilityTargetList= EntityUtil.getFirst(facilityTargetList);
			targetAmount=facilityTargetList.getAt("targetValue");
		}
		dayTotalsMap["target"]=targetAmount;
		supplyDate=monthBegin;
		dayTotals = NetworkServices.getPeriodTotals(dctx, [facilityIds:[boothId],fromDate:monthBegin, thruDate:monthEnd]);
		dayWiseTotalsMap =dayTotals.get("dayWiseTotals");

		monthTotal=BigDecimal.ZERO;
		for(int j=0 ; j < (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)+1); j++){
			Timestamp saleDate = UtilDateTime.addDaysToTimestamp(monthBegin, j);
			dayLmsTotalQty = 0;
			curntDay=UtilDateTime.toDateString(saleDate ,"yyyy-MM-dd");
			curntDaySalesMap=[:];
			if(UtilValidate.isNotEmpty(dayWiseTotalsMap)){//checking emty for whole period
				curntDaySalesMap=dayWiseTotalsMap.getAt(curntDay);
				if(UtilValidate.isNotEmpty(curntDaySalesMap)){
					productTotList=curntDaySalesMap.get("productTotals");
					productTotList.each{ product->
						productId=product.getKey();
						if(lmsProductIdsList.contains(productId))
						{
							dayLmsTotalQty+=product.getValue().get("total");
						}
					}

				}//curnt Day caliculation
			}
			dayTotalsMap[curntDay] = dayLmsTotalQty;//if curnt Day sale exists it will updates
			monthTotal+=dayLmsTotalQty;
		}//for loop end
		dayTotalsMap["Total"]=monthTotal;
		boothTotalMap[boothId]=dayTotalsMap;

	}

	routeWiseMap[route.facilityId]=boothTotalMap;
}
context.put("routeWiseMap",routeWiseMap);


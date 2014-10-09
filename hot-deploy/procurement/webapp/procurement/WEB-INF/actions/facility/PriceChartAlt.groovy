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

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.List;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;

rounding = RoundingMode.HALF_UP;


fatValueList =[];


context.fatValueList = fatValueList;

facilityId = parameters.getAt("facilityId");
productId = parameters.getAt("productId");
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

List fatValueList = FastList.newInstance();
/*BigDecimal minFat = new BigDecimal(8.5);
BigDecimal maxFat = new BigDecimal(13.0);
while(minFat.compareTo(maxFat) < 0){
	fatValueList.add("F"+(minFat.setScale(1 ,0)).toString());
	minFat = minFat.add(new BigDecimal(0.1));
}
context.fatValueList = fatValueList;*/
Timestamp priceDate = UtilDateTime.nowTimestamp();
if(parameters.get("priceDate")!=null){
	 String parseDate = parameters.get("priceDate");
	try {
		priceDate = new java.sql.Timestamp(sdf.parse(parseDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+priceDate, "");   
	}
}
 procPriceChartList =[];
 priceChartLists = [];
 procPriceChartId = null;
 if(!facilityId){
	 facilityId = "_NA_";
  }
 if(!productId && UtilValidate.isNotEmpty(procurementProductList)){
	 productId = EntityUtil.getFirst(procurementProductList).get("productId");
  }
 //context.facilityId = facilityId;
 context.product = delegator.findOne("Product",[productId:productId],true);
 facility = delegator.findOne("Facility",[facilityId:facilityId],false);
 categoryTypeEnum = parameters.get("categoryTypeEnum");
 dctx = dispatcher.getDispatchContext();
 inMap = [:];
 inMap.put("userLogin",context.userLogin);
 inMap.put("facilityId",facilityId);
 inMap.put("priceDate",priceDate);
 inMap.put("productId",productId);
 inMap.put("supplyTypeEnumId",parameters.supplyTypeEnumId);
 inMap.put("categoryTypeEnum",categoryTypeEnum);
 Map priceChart = PriceServices.getProcurementProductPrice(dctx,inMap);
 //Debug.log("priceChart========================="+priceChart)
 /*result.put("priceChartMap", priceChartMap);
 result.put("fatPercentList", fatPercentList);
 result.put("price", price);
 result.put("premium", premium);*/
 JSONArray dataJSONList= new JSONArray();
 priceChartMap  = priceChart.get("priceChartMap");
 snfPercentList  = priceChart.get("snfPercentList");
 if(UtilValidate.isNotEmpty(priceChartMap)){
	Iterator mapIter = priceChartMap.entrySet().iterator();
	while (mapIter.hasNext()) {
		Map.Entry entry = mapIter.next();
		fat =entry.getKey();
		fatPriceMap = priceChartMap[fat];
		JSONObject newObj = new JSONObject(fatPriceMap);
		newObj.put("id",fat);
		newObj.put("fat",fat);
		dataJSONList.add(newObj);
	}
 }
/*JSONArray dataJSONList= new JSONArray();
JSONObject newObj = new JSONObject();
newObj.put("id",4.5);
newObj.put("Snf",4.5);
newObj.put("supplyType",parameters.supplyTypeEnumId);
for(int i=0;i<fatValueList.size();i++){
	newObj.put(fatValueList.get(i),4.5);
}
dataJSONList.add(newObj);*/
//Debug.logInfo("dataJSONList================="+dataJSONList, "");
context.dataJSON = dataJSONList.toString();
context.snfPercentList = snfPercentList;

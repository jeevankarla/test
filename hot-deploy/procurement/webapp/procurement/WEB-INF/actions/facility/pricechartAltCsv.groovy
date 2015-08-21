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
 import org.ofbiz.base.util.*;
 
 import net.sf.json.JSONObject;
 import net.sf.json.JSONArray;
 import java.math.RoundingMode;
 import org.ofbiz.service.ServiceUtil;
 import java.sql.Timestamp;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.List;
 
 import in.vasista.vbiz.byproducts.ByProductNetworkServices;
 import in.vasista.vbiz.byproducts.ByProductServices;
 import org.ofbiz.entity.util.EntityUtil;
 import org.ofbiz.entity.Delegator;
 
 
 import java.util.Calendar;
 import javolution.util.FastList;
 import javolution.util.FastMap;
 import org.ofbiz.base.util.UtilNumber;
 import java.lang.Integer;
 
 
 import in.vasista.vbiz.procurement.ProcurementNetworkServices;
 import in.vasista.vbiz.procurement.PriceServices;
 
 
	  result = ServiceUtil.returnSuccess();
	 rounding = RoundingMode.HALF_UP;
 
	 facilityId = parameters.getAt("facilityId");
	 productId = parameters.getAt("productId");
	 def sdf = new SimpleDateFormat("yyyy-MM-dd");
	 procurementProductList =[];
	 procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	 context.procurementProductList = procurementProductList;
 
	 List fatValueList = FastList.newInstance();
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
 
	  Map priceMap = priceChart.get("priceChartMap");
	 Iterator priceIter = priceMap.entrySet().iterator();
 
	 priceList = [];
	 while(priceIter.hasNext()){
		 Map.Entry priceIter2 = priceIter.next();
		 fatsnf = priceIter2.getKey();
	 
		 fatsnfValue = priceIter2.getValue();
		 Iterator fatsnfValueItr = fatsnfValue.entrySet().iterator();
		 tempMap = [:];
		 tempMap.put("fatsnf", fatsnf);
	 
		 int i=0;
		 while(fatsnfValueItr.hasNext()){
			 Map.Entry fatsnfValueItr2 = fatsnfValueItr.next();
			 fatsnfKey = fatsnfValueItr2.getKey();
			 fatsnfVal = fatsnfValueItr2.getValue();
			 i = i+1;
			 tempMap.put("S"+i, fatsnfVal);
		 
		 }
		 priceList.add(tempMap);
	 }
	 context.priceList = priceList;
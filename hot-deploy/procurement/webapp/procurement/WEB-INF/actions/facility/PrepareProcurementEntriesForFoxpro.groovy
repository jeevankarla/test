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
import in.vasista.vbiz.procurement.PriceServices;
import java.util.Collections;
import java.util.Arrays;


procurementEntryListFoxpro = [];
procurementEntries = context.procurementEntryList;
dctx = dispatcher.getDispatchContext();
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
productWiseSlabsMap = [:];
shedId  = parameters.shedId;
fromDate = context.fromDate;
if(UtilValidate.isEmpty(fromDate)){
	fromDate = UtilDateTime.nowTimestamp();
}
for(GenericValue product : procurementProductList){
	inMap = [:];
	productSlabMap = [:];
	inMap.put("userLogin",context.userLogin);
	inMap.put("facilityId", shedId);
	inMap.put("fatPercent", BigDecimal.ZERO);
	inMap.put("snfPercent", BigDecimal.ZERO);
	inMap.put("productId", product.productId);
	Map priceChart = PriceServices.getProcurementProductPrice(dctx,inMap);
	useTotalSolids = priceChart.get("useTotalSolids");
	productSlabMap["useTotalSolids"] = useTotalSolids;
	List<EntityCondition> condList = FastList.newInstance();
	
	List<GenericValue> chartList = delegator.findList("ProcurementPriceChart", EntityCondition.makeCondition("regionId" , EntityOperator.EQUALS, shedId), null, null, null, true);
	GenericValue selChart = EntityUtil.getFirst(EntityUtil.filterByDate(chartList, fromDate));
	condList.add(EntityCondition.makeCondition("procPriceChartId" ,EntityOperator.EQUALS , selChart.procPriceChartId));
	condList.add(EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS ,  product.productId));
	condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.LIKE, "PROC_PRICE_SLAB"+"%") , EntityOperator.OR, EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, "PROC_PRICE_MIN_QLTY")));
	List<GenericValue> priceSlabesList = delegator.findList("ProcurementPrice", EntityCondition.makeCondition(condList , EntityOperator.AND), null, UtilMisc.toList("-price"), null, true);
	List slabsList = EntityUtil.getFieldListFromEntityList(priceSlabesList, "price", true);
	productSlabMap["slabs"] = slabsList;
	
	productWiseSlabsMap[product.productId] = productSlabMap;
	
}


if(UtilValidate.isNotEmpty(procurementEntries)){
	 sNo = 1;    
	 for(procurementEntry in procurementEntries ){
	 		   Map tempMap =[:];
			   parentFacility = delegator.findOne("Facility",[facilityId:procurementEntry.parentFacilityId],false);
		       if(UtilValidate.isNotEmpty(parentFacility)){
		   	     tempMap.put("RNO", parentFacility.facilityCode);
			     }
		       tempMap.put("UCODE", procurementEntry.unitCode);
		       tempMap.put("MCCTYP",BigDecimal.ONE);
		       tempMap.put("DDATE", procurementEntry.estimatedDeliveryDate);
			   tempMap.put("DAYTYP", procurementEntry.supplyTypeEnumId);
			   tempMap.put("CCODE", procurementEntry.centerCode);
			   if(Integer.parseInt(procurementEntry.centerCode) >= 300){
								tempMap.put("MCCTYP","2");
				}
			   milkType = "";
			   if("Cow Milk".equalsIgnoreCase(procurementEntry.productName)){
				   milkType = "CM";
				   }else{
				   milkType = "BM";
				   }
			   tempMap.put("TYPMLK", milkType);
			   tempMap.put("CANS", "");
			   tempMap.put("GQTY", procurementEntry.quantity);
			   tempMap.put("GFAT", procurementEntry.fat);
			   tempMap.put("GSNF", procurementEntry.snf);
			   tempMap.put("SAMPL", "");
			   tempMap.put("SQTY", procurementEntry.sQuantityLtrs);
			   tempMap.put("SFAT", procurementEntry.sFat);
			   tempMap.put("CQTY", procurementEntry.cQuantityLtrs);
			   tempMap.put("PTCC", procurementEntry.ptcQuantity);
			   tempMap.put("PTYP", procurementEntry.ptcMilkType);
			   tempMap.put("SNO",sNo);
			   tempMap.put("USERID",BigDecimal.ONE);
			   sNo++;
			   Map productSlabMap = productWiseSlabsMap.get(procurementEntry.get("productId"));
			   useTotalSolids = productSlabMap.useTotalSolids;
			   ArrayList slabsArrayList = new ArrayList(productSlabMap.slabs);
			   solids = 0;
			   if(procurementEntry.fat >0 && procurementEntry.quantity >0){
				   solids = (procurementEntry.fat)/100;
				   if(useTotalSolids == "Y"){
					   solids = (procurementEntry.fat+8.5)/100;
				   }
			   }			  
			   slabValue =slabsArrayList.get(0);
			   if(solids > 0 && ((procurementEntry.unitPrice-procurementEntry.unitPremiumPrice) >0)){
				   slabValue =  (1/solids)*(procurementEntry.unitPrice-procurementEntry.unitPremiumPrice);
			   }
			   slabValue = Math.ceil(slabValue);
			   //find the nearest value from the list of slabs
			   nearestSlab = slabsArrayList.get(0);
			   minDifValue =  Math.abs(Math.ceil(slabsArrayList.get(0))-slabValue);
			  for(int k=0;k< slabsArrayList.size() ;k++ ){
				  currentValue = slabsArrayList.get(k);
				  if(currentValue >0){
					  currentRoundedValue =  Math.ceil(slabsArrayList.get(k));
					  dif =  Math.abs(slabValue-currentRoundedValue);					
					  if(dif < minDifValue){
						  minDifValue =  Math.abs(dif);
						  nearestSlab = currentValue;
					  }
				  }
				  
			  }  
			  tempMap.put("RATE",nearestSlab);
			  /* inMap = [:];
			   inMap.put("userLogin",context.userLogin);
			   inMap.put("productId", procurementEntry.get("productId"));
			   inMap.put("facilityId", procurementEntry.originFacilityId);
			   inMap.put("fatPercent", procurementEntry.fat);
			   inMap.put("snfPercent", procurementEntry.snf);
			   inMap.put("priceDate", procurementEntry.estimatedDeliveryDate);
			   inMap.put("supplyTypeEnumId",procurementEntry.supplyTypeEnumId);
			   inMap.put("categoryTypeEnum",procurementEntry.categoryTypeEnum);
			   rateMap = dispatcher.runSync("calculateProcurementProductPrice",inMap);
			   tempMap.put("RATE",rateMap.defaultRate);*/
			   procurementEntryListFoxpro.add(tempMap);
		 }
	      
	}
Debug.log("procurementEntries list size========"+procurementEntries.size());
Debug.log("procurementEntryListFoxpro list size========"+procurementEntryListFoxpro.size());
context.putAt("procurementEntryListFoxpro", procurementEntryListFoxpro);
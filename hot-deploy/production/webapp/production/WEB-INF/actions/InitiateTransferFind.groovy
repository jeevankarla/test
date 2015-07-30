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

// The only required parameter is "productionRunId".
// The "actionForm" parameter triggers actions (see "ProductionRunSimpleEvents.xml").

import org.ofbiz.entity.util.EntityUtil;
/*import org.eclipse.emf.ecore.impl.EPackageRegistryImpl.Delegator;*/
import org.ofbiz.manufacturing.jobshopmgt.ProductionRun;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityListIterator;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import in.vasista.vbiz.purchase.MaterialHelperServices;

dctx = dispatcher.getDispatchContext();
List facilityTypeIds = FastList.newInstance();
List facilityIds = FastList.newInstance();
List facilityList = FastList.newInstance();
List productFacility = FastList.newInstance(); 
List productIds = FastList.newInstance();
List productList = FastList.newInstance();
List conditionList = FastList.newInstance();
List userFacilityList = FastList.newInstance();
List userFacilityIds = FastList.newInstance();
JSONObject facilityJSON = new JSONObject();
JSONObject productNameJSON = new JSONObject();
JSONObject facilityNameJSON = new JSONObject();
JSONArray productJSON = new JSONArray();
JSONArray facilityJson = new JSONArray();
facilityTypeIds.add("PLANT");
facilityTypeIds.add("SILO");


userLogin=parameters.userLogin;
partyIdTo=userLogin.partyId;
List employmentList=FastList.newInstance();
partyIdFrom="";
Map inputMap = FastMap.newInstance();
inputMap.clear();
inputMap.put("userLogin",userLogin);
inputMap.put("partyId",partyIdTo);
inputMap.put("roleTypeIdTo","PRODUCTION_RUN");
resultMap=MaterialHelperServices.getDepartmentByUserLogin(dctx, inputMap);
 if(UtilValidate.isNotEmpty(resultMap.get("deptId"))){
		partyIdFrom=resultMap.get("deptId");
	
	facilityList=delegator.findList("Facility",EntityCondition.makeCondition([EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,partyIdFrom),
																					  EntityCondition.makeCondition("facilityTypeId",EntityOperator.IN,facilityTypeIds)],EntityOperator.AND),UtilMisc.toSet("facilityId","facilityName"),null,null,false);
 }else{
		 facilityList=delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.IN,facilityTypeIds),UtilMisc.toSet("facilityId","facilityName"),null,null,false);
 }
//facilityList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.IN,facilityTypeIds),UtilMisc.toSet("facilityId"),null,null,false);
facilityIds = EntityUtil.getFieldListFromEntityList(facilityList, "facilityId",true);
facilityList.each{facility->
	JSONObject newObj = new JSONObject();
	newObj.put("value", facility.facilityId);
	newObj.put("label", facility.facilityName);
	facilityJson.add(newObj);
	facilityNameJSON.put(facility.facilityId, facility.facilityName);
}
productFacility = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIds),UtilMisc.toSet("productId","facilityId","capacity"),null,null,false);
productIds = EntityUtil.getFieldListFromEntityList(productFacility, "productId", true);
productList = delegator.findList("Product",EntityCondition.makeCondition("productId",EntityOperator.IN,productIds),null,null,null,false);
if(productFacility){
	facilityIds.each{ facilityId->
		JSONArray  facilityArray = new JSONArray();
		JSONObject newObj = new JSONObject(); 
		prodFacility = EntityUtil.filterByCondition(productFacility, EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
		prodFacility.each{prFacility->
			productName = EntityUtil.filterByCondition(productList, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,prFacility.productId));
			prodName = EntityUtil.getFirst(productName);
			if(prodName){
				newObj.put("value", prFacility.productId);
				newObj.put("label", prodName.productName);
				facilityArray.add(newObj);
				productJSON.add(newObj);
				productNameJSON.put(prFacility.productId, prodName.productName);
			}
		}
		if(UtilValidate.isNotEmpty(facilityArray)){
		facilityJSON.put(facilityId, facilityArray);
		}
	}
}
context.facilityJSON=facilityJSON;
context.productNameJSON=productNameJSON;
context.facilityJson=facilityJson;
context.facilityNameJSON=facilityNameJSON;
//context.productJSON=productJSON;
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
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;
	
dctx = dispatcher.getDispatchContext();
Map resultReturn = ServiceUtil.returnSuccess();
String ownerfacilityId = parameters.getAt("facilityId");
String productIdStr = parameters.getAt("productId");

List conditionList = UtilMisc.toList(EntityCondition.makeCondition("ownerFacilityId",EntityOperator.EQUALS,ownerfacilityId));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
List facilityGroupList = delegator.findList("FacilityGroup",condition,null,null,null,false);
List facilityIdsList = FastList.newInstance();
if(UtilValidate.isNotEmpty(facilityGroupList)){
	List facilityGroupIds = EntityUtil.getFieldListFromEntityList(facilityGroupList, "facilityGroupId", false);
	List fgmCondList = UtilMisc.toList(EntityCondition.makeCondition("facilityGroupId",EntityOperator.IN,facilityGroupIds));
	EntityCondition fgmCondition = EntityCondition.makeCondition(fgmCondList,EntityJoinOperator.AND);
	List facilityGroupMembers = delegator.findList("FacilityGroupMember",fgmCondition,null,null,null,false);
	if(UtilValidate.isNotEmpty(facilityGroupMembers)){
		Set facilityIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(facilityGroupMembers, "facilityId", false));
		facilityIdsList.addAll(facilityIdsSet);
	}
}
facilityIdsList.add(ownerfacilityId);
List facilityProductsList = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIdsList),null,null,null,false); 
Set productIdsList =new HashSet( EntityUtil.getFieldListFromEntityList(facilityProductsList, "productId", false));
List purposeProducts = FastList.newInstance();

/*// Here we are getting List of workEffors
//="" 
List  workEffortConditionList = UtilMisc.toList(
	EntityCondition.makeCondition("workEffortGoodStdTypeId",EntityOperator.EQUALS,"PRUNT_PROD_NEEDED")
	);
workEffortConditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productIdStr));
EntityCondition workEffortCondition = EntityCondition.makeCondition(workEffortConditionList);
List workEffrortGdList = delegator.findList("WorkEffortGoodStandard",workEffortCondition,null,null,null,false);
*/

if(UtilValidate.isNotEmpty(productIdsList)){
	for(String productId in productIdsList){
		GenericValue productDetails = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
		String productName = productDetails.get("productName");
		String internalName = productDetails.get("internalName");
		Map productDetMap = FastMap.newInstance();
		productDetMap.put("productId",productId);
		if(UtilValidate.isNotEmpty(internalName)){
			productDetMap.put("productId",internalName);
		}
		productDetMap.put("productName",productName);
		purposeProducts.add(productDetMap);
	}
}
resultReturn.put("purposeProducts",purposeProducts);
return resultReturn;





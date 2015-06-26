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
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl.Delegator;
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

List conditionList=FastList.newInstance();
List facilityGroupAndMemberAndFacility = FastList.newInstance();
List facilityList = context.get("facilityList");
List facilityIds = FastList.newInstance();
List facilityGroupIds = FastList.newInstance();
facilityIds = EntityUtil.getFieldListFromEntityList(facilityList, "facilityId", true);

if(UtilValidate.isNotEmpty(context.get("partyIdFrom"))){
	conditionList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,context.get("partyIdFrom")));
}

List facilityGroup = delegator.findList("FacilityGroup",EntityCondition.makeCondition("ownerFacilityId",EntityOperator.IN,facilityIds),UtilMisc.toSet("facilityGroupId"),null,null,false);
facilityGroupIds = EntityUtil.getFieldListFromEntityList(facilityGroup, "facilityGroupId", true);

if(UtilValidate.isNotEmpty(facilityGroupIds)){
	conditionList.add(EntityCondition.makeCondition("facilityGroupId",EntityOperator.IN,facilityGroupIds));
}
conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_IN,facilityIds));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

facilityGroupAndMemberAndFacility = delegator.findList("FacilityGroupAndMemberAndFacility",condition,UtilMisc.toSet("facilityId","description"),null,null,false);
context.facilityGroupAndMemberAndFacility=facilityGroupAndMemberAndFacility;
context.listSize = facilityGroupAndMemberAndFacility.size();
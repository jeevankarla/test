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
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Time;
if(UtilValidate.isNotEmpty(result.listIt)){
	list=result.listIt;
 List resultList = FastList.newInstance();
	 GenericValue facilityTemp = null;
	 while(facilityTemp=list.next()){
		 Map tempMap=FastMap.newInstance();
		 recordTimeStr = "";
		 shiftTypeId = "";
		 recordDateTime = facilityTemp.recordDateTime;
		 recordTimeStr = recordDateTime.getHours()+":"+recordDateTime.getMinutes()+":"+"00";
		 Time recordTime =null;
		 SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		 if(UtilValidate.isNotEmpty(recordTimeStr)){
			 try {
				 recordTime =new java.sql.Time(format.parse(recordTimeStr).getTime());
			 } catch (ParseException e) {
				 Debug.logError(e, "Cannot parse date string: " + e, "");
			 }
		 }
			 List conditionList = FastList.newInstance();
			
			conditionList.add(EntityCondition.makeCondition("startTime",EntityOperator.LESS_THAN_EQUAL_TO,recordTime));
			conditionList.add(EntityCondition.makeCondition("endTime",EntityOperator.GREATER_THAN_EQUAL_TO,recordTime));
			conditionList.add(EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,"PROD_SHIFT"));
			conditionList.add(EntityCondition.makeCondition("isDefault",EntityOperator.EQUALS,"Y"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			List workShiftTypePeriodAndMap = delegator.findList("WorkShiftTypePeriodAndMap",condition,UtilMisc.toSet("shiftTypeId"),null,null,false);
			if(UtilValidate.isEmpty(workShiftTypePeriodAndMap)){
				shiftTypeId = "PROD_SHIFT_NIGHT";
			}
			if(UtilValidate.isNotEmpty(workShiftTypePeriodAndMap)){
			workShift = EntityUtil.getFirst(workShiftTypePeriodAndMap);
			
			shiftTypeId = workShift.shiftTypeId;
			}
		 
		 tempMap.put("shiftTypeId",shiftTypeId);
		 tempMap.put("recordDateTime", recordDateTime);
		 tempMap.put("facilityId", facilityTemp.facilityId);
		 tempMap.put("temperature", facilityTemp.temperature);
		 tempMap.put("comments", facilityTemp.comments);
		 if(UtilValidate.isNotEmpty(parameters.shiftTypeId)){
			 if(shiftTypeId.equals(parameters.shiftTypeId)){
				 resultList.add(tempMap);
			 }
		 }else{
		   resultList.add(tempMap);
		 }
	  }
	 context.listIt=resultList;
}


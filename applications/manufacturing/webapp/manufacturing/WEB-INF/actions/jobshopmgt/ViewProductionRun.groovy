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
import org.ofbiz.manufacturing.jobshopmgt.ProductionRun;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
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

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
dctx = dispatcher.getDispatchContext();
productionRunId = parameters.productionRunId;
if (productionRunId) {
    ProductionRun productionRun = new ProductionRun(productionRunId, delegator, dispatcher);
    if (productionRun.exist()) {
        productionRunId = productionRun.getGenericValue().workEffortId;
        context.productionRunId = productionRunId;
        context.productionRun = productionRun.getGenericValue();
        // Prepare production run header data
        productionRunData = [:];
        productionRunData.productionRunId = productionRunId;
        productionRunData.productId = productionRun.getProductProduced().productId;
        productionRunData.currentStatusId = productionRun.getGenericValue().currentStatusId;
        productionRunData.facilityId = productionRun.getGenericValue().facilityId;
        productionRunData.workEffortName = productionRun.getProductionRunName();
        productionRunData.description = productionRun.getDescription();
        productionRunData.quantity = productionRun.getQuantity();
        productionRunData.estimatedStartDate = productionRun.getEstimatedStartDate();
        productionRunData.estimatedCompletionDate = productionRun.getEstimatedCompletionDate();

		if(productionRunData.productId){
			conditionList = [];
			conditionList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId", EntityOperator.EQUALS, "ROU_PROD_TEMPLATE"));
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productionRunData.productId));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			productionRunRoutingTasks = delegator.findList("WorkEffortGoodStandard", condition, null, null, null, false);
			
			productionRunRoutingTasks = EntityUtil.filterByDate(productionRunRoutingTasks, UtilDateTime.nowTimestamp());
			
			if(productionRunRoutingTasks){
				productionRunData.routingTaskId = (EntityUtil.getFirst(productionRunRoutingTasks)).workEffortId;
			}
		}
		
		context.productionRunData = productionRunData;
        // Find all the order items to which this production run is linked.
        orderItems = delegator.findByAnd("WorkOrderItemFulfillment", [workEffortId : productionRunId]);
        if (orderItems) {
            context.orderItems = orderItems;
        }
        //  RoutingTasks list
        context.productionRunRoutingTasks = productionRun.getProductionRunRoutingTasks();
        context.quantity = productionRun.getQuantity(); // this is useful to compute the total estimates runtime in the form
        //  Product component/parts list
        context.productionRunComponents = productionRun.getProductionRunComponents();;

        // Find all the notes linked to this production run.
        productionRunNoteData = delegator.findByAnd("WorkEffortNoteAndData", [workEffortId : productionRunId]);
        if (productionRunNoteData) {
            context.productionRunNoteData = productionRunNoteData;
        }
    }
}
List facilityList=FastList.newInstance();
List facilityIds=FastList.newInstance();
facilityList=context.get("facilityList");
facilityIds=EntityUtil.getFieldListFromEntityList(facilityList,"facilityId", true);
List condList=FastList.newInstance();
List workEffortList=FastList.newInstance();
List productIds=FastList.newInstance();
List productList=FastList.newInstance();
JSONObject productNameObj = new JSONObject();
JSONObject workEffortObj = new JSONObject();
JSONObject facilityWorkEffObj=new JSONObject();
if(UtilValidate.isNotEmpty(facilityIds)){
	condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIds));
}
//condList.add(EntityCondition.makeCondition("workEffortTypeId",EntityOperator.EQUALS,"ROUTING"));
EntityCondition con=EntityCondition.makeCondition(condList,EntityOperator.AND);

workEffortList=delegator.findList("WorkEffortAndGoods",con,UtilMisc.toSet("workEffortId","description","productId","facilityId","workEffortTypeId"),null,null,false);
productIds=EntityUtil.getFieldListFromEntityList(workEffortList,"productId",true);
productList=delegator.findList("Product",EntityCondition.makeCondition("productId",EntityOperator.IN,productIds),UtilMisc.toSet("productId","description","internalName","quantityUomId"),null,null,false);
productIds.each{productId->
	productNames=EntityUtil.filterByCondition(productList,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
	JSONObject productDetObj = new JSONObject();
	productDetObj.put("description",productNames.description);
	String uomId = productNames[0].get("quantityUomId");
	uomDetails = delegator.findOne("Uom",["uomId":uomId],false);
	if(UtilValidate.isNotEmpty(uomDetails) ){
		productDetObj.put("uomId",uomDetails.description);
	}else{
		productDetObj.put("uomId"," ");
	}
	productNameObj.put(productId, productDetObj);
}

workEffortList = EntityUtil.filterByCondition(workEffortList,EntityCondition.makeCondition("workEffortTypeId",EntityOperator.EQUALS,"ROUTING"));

workEffortList.each{workEffort->
	workEffortObj.put(workEffort.workEffortId, workEffort.productId);
}
facilityIds.each{facilityId->
	JSONArray arrayJSON = new JSONArray();
	workEffort=EntityUtil.filterByCondition(workEffortList,EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
	workEffort.each{effort->
		JSONObject newObj=new JSONObject();
		newObj.put("workEffortId",effort.workEffortId);
		newObj.put("description", effort.description);
		arrayJSON.add(newObj);
	}
	if(UtilValidate.isNotEmpty(arrayJSON)){
	facilityWorkEffObj.put(facilityId, arrayJSON);
	}
}
context.workEffortList=workEffortList;
context.productNameObj=productNameObj;
context.workEffortObj=workEffortObj;
context.facilityWorkEffObj=facilityWorkEffObj;
//Debug.log("facilityWorkEffObj#####################"+facilityWorkEffObj);

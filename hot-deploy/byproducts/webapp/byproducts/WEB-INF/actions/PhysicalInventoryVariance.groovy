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
import org.ofbiz.base.util.Debug;

// get physicalInventoryAndVarianceDatas if this is a NON_SERIAL_INV_ITEM
inventoryItemVarianceList = [];
if(parameters.physicalInventoryId){
	inventoryItemVariance = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition("physicalInventoryId", EntityOperator.EQUALS, parameters.physicalInventoryId), null, ['-lastUpdatedStamp', '-physicalInventoryId'], null, false);
	if(inventoryItemVariance){
		inventoryItemVariance.each{eachItem ->
			varianceMap = [:];
			varianceMap.inventoryItemId = eachItem.getAt("inventoryItemId");
			varianceMap.physicalInventoryId = eachItem.getAt("physicalInventoryId");
			varianceMap.varianceReasonId = eachItem.getAt("varianceReasonId");
			varianceMap.quantity = eachItem.getAt("availableToPromiseVar");
			varianceMap.date = eachItem.getAt("lastUpdatedStamp");
			varianceMap.comments = eachItem.getAt("comments");
			inventoryItem = delegator.findOne("InventoryItem", ["inventoryItemId" : varianceMap.inventoryItemId], false);
			if(inventoryItem){
				varianceMap.productId = inventoryItem.productId;
			}
			inventoryItemVarianceList.add(varianceMap);
		}
	}
}
else{
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("physicalInventoryId", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("comments", EntityOperator.NOT_EQUAL, "New Opening Balance"));
	conditionList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.NOT_EQUAL, null));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
	inventoryItemVariance = delegator.findList("InventoryItemVariance", condition, null, ['-lastUpdatedStamp', '-physicalInventoryId'], null, false);
	if(inventoryItemVariance){
		inventoryItemVariance.each{eachItem ->
			varianceMap = [:];
			varianceMap.inventoryItemId = eachItem.getAt("inventoryItemId");
			varianceMap.physicalInventoryId = eachItem.getAt("physicalInventoryId");
			varianceMap.varianceReasonId = eachItem.getAt("varianceReasonId");
			varianceMap.quantity = eachItem.getAt("availableToPromiseVar");
			varianceMap.date = eachItem.getAt("lastUpdatedStamp");
			varianceMap.comments = eachItem.getAt("comments");
			inventoryItem = delegator.findOne("InventoryItem", ["inventoryItemId" : varianceMap.inventoryItemId], false);
			if(inventoryItem){
				varianceMap.productId = inventoryItem.productId;
			}
			inventoryItemVarianceList.add(varianceMap);
		}
	}
}
context.inventoryItemVarianceList = inventoryItemVarianceList;
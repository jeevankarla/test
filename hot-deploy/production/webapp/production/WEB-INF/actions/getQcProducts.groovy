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
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.product.product.ProductWorker;

import org.ofbiz.entity.util.EntityUtil;
dctx = dispatcher.getDispatchContext();
//resultReturn = ServiceUtil.returnSuccess();
workEffortId=parameters.workEffortId;

qcProductsList=[];
List<GenericValue> workEffort = delegator.findList("WorkEffort", EntityCondition.makeCondition("workEffortParentId", EntityOperator.EQUALS, workEffortId), null, UtilMisc.toList("workEffortId"), null, false);
if(UtilValidate.isNotEmpty(workEffort)){
	List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(workEffort, "workEffortId", false);
	List<GenericValue> inventoryItemAndDetail = delegator.findList("InventoryItemAndDetail", EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds), null, null, null, false);
	if(UtilValidate.isNotEmpty(inventoryItemAndDetail)){
		inventoryItemAndDetail.each{eachDeclaredItem->
			String productId = eachDeclaredItem.productId;
			String productBatchId = eachDeclaredItem.productBatchId;
			String ownerPartyId = eachDeclaredItem.ownerPartyId;
			Timestamp effectiveDate = eachDeclaredItem.effectiveDate;
			
			BigDecimal quantityOnHandDiff = eachDeclaredItem.quantityOnHandDiff;
     		if((quantityOnHandDiff.compareTo(BigDecimal.ZERO) >= 0) && (UtilValidate.isNotEmpty(productBatchId))){
                 tempMap=[:]
				 tempMap.put("workEffortId", workEffortId);
				 tempMap.put("productId", productId);
				 tempMap.put("ownerPartyId", ownerPartyId);
				 tempMap.put("productBatchId", productBatchId);
				 tempMap.put("effectiveDate", effectiveDate);
				 tempMap.put("quantity", quantityOnHandDiff);
                 qcProductsList.add(tempMap);
			}
		}
	}
}
context.qcProductsList=qcProductsList;


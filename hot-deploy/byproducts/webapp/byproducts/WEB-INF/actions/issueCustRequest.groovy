	import org.ofbiz.base.util.UtilValidate;
	
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
	
	import org.ofbiz.entity.*
	import org.ofbiz.entity.util.*
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	
	import java.math.BigDecimal;
	import java.math.MathContext;
	import java.sql.Timestamp;
	import com.ibm.icu.util.Calendar;
	import java.util.List;
	import java.util.Locale;
	import java.util.Map;
	import java.util.Set;
	
	import javolution.util.FastList;
	import javolution.util.FastMap;
	
	import org.ofbiz.base.util.Debug;
	import org.ofbiz.base.util.UtilDateTime;
	import org.ofbiz.base.util.UtilGenerics;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.base.util.UtilProperties;
	import org.ofbiz.base.util.UtilValidate;
	import org.ofbiz.service.DispatchContext;
	import org.ofbiz.service.GenericServiceException;
	import org.ofbiz.service.LocalDispatcher;
	import org.ofbiz.service.ServiceUtil;
	
	shipmentId = request.getParameter("shipmentId");
	custRequestId = parameters.custRequestId;
	
	if(custRequestId){
		custRequest = delegator.findOne("CustRequest", [custRequestId : custRequestId], false);
	}
	
	productId = null;
	facilityIdTo = "UHT_MILK_GDWN";
	inventoryItemId = null;
	inventoryTransferId = null;
	inventoryTransferIdList = [];
	Map<String, Object> inputMap = [:];
	
	if (custRequest){
		custRequestItems = custRequest.getRelated("CustRequestItem");
		
		custRequestItems.each { custRequestItem ->
			
			custRequestItemSeqId = custRequestItem.get("custRequestItemSeqId");
			inventoryItemMap = [:];
			availableToPromiseTotal = 0;
			requestedQuantity = 0;
			
			if (custRequestItem.productId) {
				productId = custRequestItem.get("productId");
			}
			if (custRequestItem.quantity) {
				requestedQuantity = custRequestItem.getDouble("quantity");
			}
			
			List conditionList=[];
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, "COLDCARE_WH_AT"));
			conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, "Company"));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			inventoryItems =  delegator.findList("InventoryItem", condition , null, null, null, false );
			inventoryItems.each { inventoryItem ->
				
				if ((inventoryItem.availableToPromiseTotal) && ((inventoryItem.getDouble("availableToPromiseTotal")) >= (requestedQuantity))) {
					inventoryItemId = inventoryItem.get("inventoryItemId");
				}
				if((inventoryItem.getDouble("availableToPromiseTotal")) > 0){
					inventoryItemMap[inventoryItem.get("inventoryItemId")] = 0;
					inventoryItemMap[inventoryItem.get("inventoryItemId")] = (inventoryItem.getDouble("availableToPromiseTotal"));
					availableToPromiseTotal += inventoryItem.getDouble("availableToPromiseTotal");
				}
			}
			
			if(UtilValidate.isNotEmpty(inventoryItemId)){
				Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", inventoryItemId, "statusId","IXF_COMPLETE", "facilityId","COLDCARE_WH_AT", "facilityIdTo", facilityIdTo, "xferQty", requestedQuantity);
				Map<String, Object> inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
				inventoryTransferId = inventoryXferResult.get("inventoryTransferId");
			}
			else if(UtilValidate.isEmpty(inventoryItemId) && (availableToPromiseTotal >= requestedQuantity)){
				
				Iterator invItemIter = inventoryItemMap.entrySet().iterator();
				while (invItemIter.hasNext()) {
					tempXferQty = 0;
					Map.Entry invItemEntry = invItemIter.next();
					if(requestedQuantity >= (invItemEntry.getValue())){
						tempXferQty = invItemEntry.getValue();
						requestedQuantity = requestedQuantity - (invItemEntry.getValue());
					}else{
						tempXferQty = requestedQuantity;
					}
					if(tempXferQty == 0){
						continue;
					}
					Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", invItemEntry.getKey(), "statusId","IXF_COMPLETE", "facilityId","COLDCARE_WH_AT", "facilityIdTo", facilityIdTo, "xferQty", tempXferQty);
					Map<String, Object> inventoryXferResult = dispatcher.runSync("createInventoryTransfer", input);
					if(inventoryXferResult){
						inventoryTransferId = inventoryXferResult.get("inventoryTransferId");
						inventoryTransferIdList.add(inventoryTransferId);
					}
				}
			}
			else{
				Debug.logError("There is no enough stock left for the product" + productId + "to transfer", "");
				request.setAttribute("_ERROR_MESSAGE_", "There is no enough stock left for the product" + productId + "to transfer");
				return "error";
			}
			inputMap = UtilMisc.toMap("userLogin", userLogin, "custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId, "statusId","CRQ_COMPLETED");
			Map<String, Object> custItemStatusResult = dispatcher.runSync("updateCustRequestItem", inputMap);
		}
	}
	
	inputMap.clear();
	inputMap = UtilMisc.toMap("userLogin", userLogin, "custRequestId", custRequestId, "statusId","CRQ_COMPLETED");
	Map<String, Object> custReqStatusResult = dispatcher.runSync("updateCustRequest", inputMap);
	
	context.inventoryTransferId = inventoryTransferId;
	context.inventoryTransferIdList = inventoryTransferIdList;

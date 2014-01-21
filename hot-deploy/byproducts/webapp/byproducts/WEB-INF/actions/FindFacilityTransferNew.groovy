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
	
	
	import java.math.BigDecimal;
	import java.math.RoundingMode;
	import java.text.ParseException;
	import java.util.*;
	import java.util.Map.Entry;
	import java.sql.Date;
	import java.sql.Timestamp;
	
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import javolution.util.FastSet;
	
	import org.apache.tools.ant.filters.TokenFilter.ContainsString;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.entity.util.*;
	import org.ofbiz.base.util.*;
	import org.ofbiz.network.NetworkServices;
	import org.ofbiz.service.DispatchContext;
	import org.ofbiz.service.GenericServiceException;
	import org.ofbiz.service.LocalDispatcher;
	import org.ofbiz.service.ModelService;
	import org.ofbiz.service.ServiceUtil;
	
	
	import org.ofbiz.entity.condition.*;
	
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
	import java.util.HashSet;
	
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
	
	import in.vasista.vbiz.byproducts.ByProductServices;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	
	// get the 'to' this facility transfers
	
	stockReceiptsList = [];
	stockTransfersList = [];
	
	initializingMap = [:];
	initializingMap["inventoryTransferId"] = null;
	initializingMap["inventoryItemId"] = null;
	initializingMap["fromFacilityId"] = null;
	initializingMap["toFacilityId"] = null;
	initializingMap["productId"] = null;
	initializingMap["productName"] = null;
	initializingMap["quantity"] = null;
	initializingMap["receiveDate"] = null;
	
	stockMovementMap = [:];
	stockMovementMap.putAll(initializingMap);
	
	List finalToTransfers = [];
	List xferConditionList=[];
	List receiptsCondList=[];
	
	if(parameters.action == "SEARCH"){
		
		parlourList = ByProductServices.getByproductParlours(delegator).get("parlourIdsList");
		receiptsCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, parlourList));
		
		if(parameters.facilityId){
			facilityId = parameters.facilityId;
			
			if(parameters.stockMovement){
				if(parameters.stockMovement == "IN"){
					xferConditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId));
					receiptsCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
					xferConditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.IN, parlourList));
				}else{
					xferConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
					xferConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, parlourList));
				}
			}else{
				xferConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId), EntityOperator.OR, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId)));
				xferConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityIdTo", EntityOperator.IN, parlourList), EntityOperator.OR, EntityCondition.makeCondition("facilityId", EntityOperator.IN, parlourList)));
				receiptsCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
			}
		}
		
		condition = EntityCondition.makeCondition(xferConditionList, EntityOperator.AND);
		toTransfers = delegator.findList("InventoryTransfer", condition, null, null, null, false);
		
		if(parameters.productId){
			productId = parameters.productId;
			receiptsCondList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			toTransfers.each { eachTransfer ->
				inventoryItem = eachTransfer.getRelatedOne("InventoryItem");
				if(inventoryItem.get("productId") == productId){
					finalToTransfers.addAll(eachTransfer);
				}
			}
		}
		else{
			finalToTransfers = toTransfers;
		}
		
		receiptsCond = EntityCondition.makeCondition(receiptsCondList, EntityOperator.AND);
		
		shipmentReceipts = delegator.findList("ShipmentReceiptAndItem", receiptsCond, null, null, null, false);
		
		shipmentReceipts.each { shipmentReceipt ->
			
			stockMovementMap["inventoryItemId"] = shipmentReceipt.get("inventoryItemId");
			stockMovementMap["toFacilityId"] = shipmentReceipt.get("facilityId");
			stockMovementMap["productId"] = shipmentReceipt.get("productId");
			stockMovementMap["quantity"] = shipmentReceipt.get("quantityAccepted");
			stockMovementMap["receiveDate"] = shipmentReceipt.get("datetimeReceived");
			
			tempResultMap = [:];
			tempResultMap.putAll(stockMovementMap);
			
			stockReceiptsList.addAll(tempResultMap);
			
			stockMovementMap.clear();
			stockMovementMap.putAll(initializingMap);
		}
		
		finalToTransfers.each { eachTransfer ->
			
			stockMovementMap["inventoryTransferId"] = eachTransfer.get("inventoryTransferId");
			stockMovementMap["inventoryItemId"] = eachTransfer.get("inventoryItemId");
			stockMovementMap["fromFacilityId"] = eachTransfer.get("facilityId");
			stockMovementMap["toFacilityId"] = eachTransfer.get("facilityIdTo");
			if(UtilValidate.isNotEmpty(eachTransfer.get("productId"))){
				stockMovementMap["productId"] = eachTransfer.get("productId");
			}
			if(UtilValidate.isNotEmpty(eachTransfer.get("quantityAccepted"))){
				stockMovementMap["quantity"] = eachTransfer.get("quantityAccepted");
			}
			stockMovementMap["receiveDate"] = eachTransfer.get("receiveDate");
			
			tempResultMap = [:];
			tempResultMap.putAll(stockMovementMap);
			
			stockTransfersList.addAll(tempResultMap);
			
			stockMovementMap.clear();
			stockMovementMap.putAll(initializingMap);
		}
			
		if(parameters.stockMovement != "OUT"){
			stockTransfersList.addAll(stockReceiptsList);
		}
		
	}
	
	
	
	context.toTransfers = stockTransfersList;


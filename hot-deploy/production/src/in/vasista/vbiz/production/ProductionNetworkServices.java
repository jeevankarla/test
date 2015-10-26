/*******************************************************************************
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
 *******************************************************************************/
package in.vasista.vbiz.production;

/**
 * @author vadmin
 *
 */
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.util.EntityUtil;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.NullPointerException;
import java.math.BigDecimal;


public class ProductionNetworkServices {

	 public static final String module = ProductionNetworkServices.class.getName();
	 private static BigDecimal ZERO = BigDecimal.ZERO;
	    private static int decimals;
	    private static int rounding;
	    public static final String resource_error = "OrderErrorUiLabels";
	    static {
	        decimals = 3;//UtilNumber.getBigDecimalScale("order.decimals");
	        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

	        // set zero to the proper scale
	        if (decimals != -1) ZERO = ZERO.setScale(decimals); 
	    }	
	    
	   /**
	    * It will returns All types of silos
	    * @param delegator
	    * @return
	    */
	    public static List getSilos(Delegator delegator) {
	    	
	    	List silosList = FastList.newInstance();
	    	try {
	    		silosList = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"SILO"), null, null, null, true);
	    		     
	    	}catch (GenericEntityException e) {
	            Debug.logError(e, module);	           
	        }
	        return silosList;
	    } // End of the service
	   
	    public static String getRootProductionRun(Delegator delegator, String productionRunId)  throws GenericEntityException {
	        List<GenericValue> linkedWorkEfforts = delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdFrom", productionRunId, "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY"));
	        GenericValue linkedWorkEffort = EntityUtil.getFirst(linkedWorkEfforts);
	        if (linkedWorkEffort != null) {
	            productionRunId = getRootProductionRun(delegator, linkedWorkEffort.getString("workEffortIdTo"));
	        }
	        return productionRunId;
	    }
	    
	    public static Map<String, Object> adjustProductionTransactionDate(DispatchContext dctx, Map<String, ? extends Object> context) {
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String workEffortId = (String)context.get("workEffortId");
	        String transferGroupId = (String)context.get("transferGroupId");
	        Map<String, Object> result = FastMap.newInstance();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        
	        List conditionList = FastList.newInstance();
	        try {
	        	if(UtilValidate.isNotEmpty(workEffortId)){
			        
	        		GenericValue workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
	        		
	        		if(UtilValidate.isEmpty(workEffort)){
	        			Debug.logError("Not a valid production run : "+workEffortId, module);
	    				return ServiceUtil.returnError("Not a valid production run : "+workEffortId);
	        		}
	        		
	        		if(!(workEffort.getString("currentStatusId")).equals("PRUN_COMPLETED")){
	        			Debug.logError("Production run is not in completed status : "+workEffortId, module);
	    				return ServiceUtil.returnError("Production run is not in completed status : "+workEffortId);
	        		}
	        		
	        		Timestamp startDate = workEffort.getTimestamp("estimatedStartDate");
			        List<GenericValue> workEffortInventoryDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId), null, null, null, false);
			        
			        List<String> inventoryItemIds = EntityUtil.getFieldListFromEntityList(workEffortInventoryDetails, "inventoryItemId", true);
			        GenericValue inventoryItem = null;
			        
			        // change effectiveDate to workEffort startDate
			        for(GenericValue invDetail : workEffortInventoryDetails){
			        	invDetail.set("effectiveDate", startDate);
			        	invDetail.store();
			        }
			        
			        for(String inventoryItemId : inventoryItemIds){
			        	
			        	conditionList.clear();
			        	conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId));
			        	EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			        	List<GenericValue> inventoryDetails = delegator.findList("InventoryItemDetail", cond, null, UtilMisc.toList("effectiveDate"), null, false);
			        	
			        	List<GenericValue> invDateCheckDetail = EntityUtil.filterByCondition(inventoryDetails, EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN_EQUAL_TO, BigDecimal.ZERO));
			        	if(UtilValidate.isNotEmpty(invDateCheckDetail)){
			        		GenericValue invItemDetail = EntityUtil.getFirst(invDateCheckDetail);
			        		Timestamp compareDate = invItemDetail.getTimestamp("effectiveDate");
			        		if(startDate.compareTo(compareDate)<=0){
			        			inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
			        			inventoryItem.set("datetimeReceived", startDate);
			        			inventoryItem.store();
			        			for(GenericValue eachItem :  invDateCheckDetail){
			        				eachItem.set("effectiveDate", startDate);
			        				eachItem.store();
			        			}
			        		}
			        	}
			        }
	        	}
	        	
	        	if(UtilValidate.isNotEmpty(transferGroupId)){
			        
	        		GenericValue transferGroup = delegator.findOne("InventoryTransferGroup", UtilMisc.toMap("transferGroupId", transferGroupId), false);
	        		
	        		if(UtilValidate.isEmpty(transferGroup)){
	        			Debug.logError("Transfer with Id: "+transferGroupId+" doesn't exists", module);
	    				return ServiceUtil.returnError("Transfer with Id: "+transferGroupId+" doesn't exists");
	        		}
	        		
	        		if(!(transferGroup.getString("statusId")).equals("IXF_COMPLETE")){
	        			Debug.logError("Transfer with Id: "+transferGroupId+" transaction is not complete", module);
	    				return ServiceUtil.returnError("Transfer with Id: "+transferGroupId+" transaction is not complete");
	        		}
	        		
	        		List<GenericValue> inventoryTransferGroup = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null ,null, null, false);
	        		
	        		List<String> inventoryTransferIds = EntityUtil.getFieldListFromEntityList(inventoryTransferGroup, "inventoryTransferId", false);
	        		
	        		List<GenericValue> inventoryTransfers = delegator.findList("InventoryTransfer", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, inventoryTransferIds), null, null, null, false);
	        		
	        		for(GenericValue eachXfer : inventoryTransfers){
	        			Timestamp sendDate = eachXfer.getTimestamp("sendDate");
	        			Timestamp inventoryTransferId = eachXfer.getTimestamp("inventoryTransferId");
	        			
	        			conditionList.clear();
	        			conditionList.add(EntityCondition.makeCondition("inventoryTransferId", EntityOperator.EQUALS, eachXfer.getString("inventoryTransferId")));
	        			EntityCondition cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	        			List<GenericValue> invDetails = delegator.findList("InventoryItemDetail", cond1, null, null, null, false);
	        			
	        			for(GenericValue eachInvDetail : invDetails){
	        				String detailItemId = eachInvDetail.getString("inventoryItemId");
	        				
	        				List<GenericValue> inventoryItemCheck = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, detailItemId), UtilMisc.toSet("effectiveDate"), UtilMisc.toList("effectiveDate"), null, false);
	        				
	        				if(UtilValidate.isNotEmpty(inventoryItemCheck)){
	        					Timestamp compInvDetailDate = (EntityUtil.getFirst(inventoryItemCheck)).getTimestamp("effectiveDate");
	        					
	        					if(compInvDetailDate.compareTo(sendDate)< 0){
	        						eachInvDetail.set("effectiveDate", sendDate);
	        						eachInvDetail.store();
	        						Debug.log("changing transfer effective date ############");
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
	        catch(GenericEntityException e){
	        	Debug.logError(e, module);
	        	return ServiceUtil.returnError(e.toString());
	        }
	        result = ServiceUtil.returnSuccess("Successfully");
	        return result;
	    }// End of the Service
	   
}

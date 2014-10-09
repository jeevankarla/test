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
package in.vasista.vbiz.procurement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.NullPointerException;
import java.lang.SecurityException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.rmi.server.ServerCloneException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import java.util.Random;
import java.util.Map.Entry;

import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapComparator;
import org.ofbiz.base.util.string.FlexibleStringExpander;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.image.ScaleImage;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;


import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementReports;
import org.procurementURI.APDairyWCFServiceStub;
import org.ofbiz.base.conversion.JSONConverters.JSONToList;
import org.json.JSONArray;
import com.google.gson.Gson; 
import in.vasista.vbiz.procurement.ProcurementServices;
/**
 * Procurement Services
 */
public class ProcurementSyncServices {

	public static final String module = ProcurementSyncServices.class.getName();
	
	
	public static Map<String, Object> syncProcurementEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> resultMap = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        String currencyUomId = (String)context.get("currencyUomId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        try{
        	APDairyWCFServiceStub stub = new APDairyWCFServiceStub();

    		APDairyWCFServiceStub.GetDataByDate request = new APDairyWCFServiceStub.GetDataByDate();
    		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    		String dateToProcess = UtilDateTime.toDateString(nowTimestamp, "dd-MM-yyyy");
    		request.setDate(dateToProcess);
    		APDairyWCFServiceStub.GetDataByDateResponse response = stub.getDataByDate(request);
    		Debug.log("Response : " + response.getGetDataByDateResult());
    		ArrayList<Object> entryResponse = new ArrayList();
    		Gson gson = new Gson();
    		entryResponse = gson.fromJson(response.getGetDataByDateResult(), ArrayList.class);  
    		//JSONArray jsonArray = new JSONArray(response.getGetDataByDateResult());
    		//entryResponse = (new JSONObject((response.getGetDataByDateResult()))).values();
    		//Debug.log("entryResponse #################"+entryResponse);
    		for(int i=0;i< entryResponse.size();i++){
    			Map inputMap = FastMap.newInstance();
    			Map entry = FastMap.newInstance();
    			entry = (Map)entryResponse.get(i);
    			Map input = FastMap.newInstance();
    			String shedCode = (String)entry.get("ShedCode");
    			String unitCode = (String)entry.get("BMCCode");
    			String centerCode = (String)entry.get("VillageCode");
    			String MilkType = (String)entry.get("MilkType");
    			String purchaseTime = (String)entry.get("purchaseTime");
    			String productId = "";
    			if(MilkType.equalsIgnoreCase("BM")){
    				productId = "101";
    			}
    			else{
    				productId = "102";
    			}
    			String procDate = (String)entry.get("ProcurementDate");
    			Timestamp procurementDate = null;
    			if (UtilValidate.isNotEmpty(procDate)) { 
    				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    				try {
    					procurementDate = new java.sql.Timestamp(sdf.parse(procDate).getTime());
    					procurementDate = UtilDateTime.getDayStart(procurementDate);
    				} catch (ParseException e) {
    					Debug.logError(e, "Cannot parse date string: "+ procDate, module);
    					return ServiceUtil.returnError("Error in parsoing procurement date");
    				} catch (NullPointerException e) {
    					Debug.logError(e, "Cannot parse date string: "+ procDate, module);
    					return ServiceUtil.returnError("Error in parsoing procurement date");
    				}
    			}
    			else{
    				Debug.logError("Procurement date is empty",module);
       	    		return ServiceUtil.returnError("Procurement date is empty");
    			}
    			Timestamp dayStart = UtilDateTime.getDayStart(procurementDate);
    			Timestamp dayEnd = UtilDateTime.getDayEnd(procurementDate);
    			input.put("shedCode", shedCode);
    			input.put("unitCode", unitCode);
    			input.put("centerCode", centerCode);
    			GenericValue facility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, input)).get("agentFacility");
    	    	if(UtilValidate.isEmpty(facility)){
    	    	    Debug.logError("Agent Not found with Code ==>"+centerCode+" and with the unitCode :"+unitCode, module);
    	      		return ServiceUtil.returnError("Agent Not found with Id ==>"+centerCode+" and with the unitCode :"+unitCode);        	
    	    	}
    	    	String facilityId = facility.getString("facilityId");
    			List<GenericValue> orderHeader = FastList.newInstance();
    			List conditionList = FastList.newInstance();
    			conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
    			conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
    			conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
    			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ORDER_CREATED","ORDER_APPROVED")));
    			conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
    			conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
    			EntityCondition orderCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			orderHeader = delegator.findList("OrderHeader", orderCond, UtilMisc.toSet("orderId"), null, null, false);
    			String orderId = "";
    			if(UtilValidate.isNotEmpty(orderHeader)){
    				orderId = (EntityUtil.getFirst(orderHeader)).getString("orderId");
    				conditionList.clear();
    				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"));
        			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
        			conditionList.add(EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS, purchaseTime));
        			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
        			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
        			EntityCondition orderItemCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        			List<GenericValue> orderItems = delegator.findList("OrderItem", orderItemCond, UtilMisc.toSet("orderId","orderItemSeqId"), null, null, false);
        			if(UtilValidate.isNotEmpty(orderItems)){
        				GenericValue orderItem = EntityUtil.getFirst(orderItems);
        				String orderItemSeqId = orderItem.getString("orderItemSeqId");
        				Map<String, Object> deleteProcrementRecordInMap = FastMap.newInstance();
        				deleteProcrementRecordInMap.put("orderId", orderId);
        				deleteProcrementRecordInMap.put("orderItemSeqId", orderItemSeqId);
        				deleteProcrementRecordInMap.put("userLogin", userLogin);
        				resultMap = dispatcher.runSync("deleteProcurementEntry",deleteProcrementRecordInMap);
        				if (ServiceUtil.isError(resultMap)) {
        	                Debug.logWarning("There was an error while deleting the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap), module);
        	        		return ServiceUtil.returnError("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap));          	            
        	            }
        			}
    			}
    			inputMap.put("shedCode", shedCode);
    			inputMap.put("unitCode", unitCode);
    			inputMap.put("centerCode", centerCode);
    			inputMap.put("userLogin", userLogin);
    			inputMap.put("orderDate", procurementDate);
    			inputMap.put("productId", productId);
    			
    			inputMap.put("fat", new BigDecimal((Double)entry.get("Fat")));
    			inputMap.put("snf", new BigDecimal((Double)entry.get("Snf")));
    			inputMap.put("purchaseTime", purchaseTime);
    			inputMap.put("quantity", new BigDecimal((Double)entry.get("QtyLtr")));
   				Map result = ProcurementServices.createProcurementEntry(dctx, inputMap);
       			if(ServiceUtil.isError(result)){
       				Debug.logError("Error in creating procurement entry",module);
       	    		return ServiceUtil.returnError("Error in creating procurement procurement entries");
       			}
    		}
        }catch(Exception e){
        	Debug.logError(e.getMessage(),module);
    		return ServiceUtil.returnError("Error syncing procurement entries");
        }
        return resultMap;
   }
	  	
}

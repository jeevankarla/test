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
package in.vasista.vbiz.byproducts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.ibm.icu.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.avalon.framework.service.ServiceException;
import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
/**
 * Subscription Services
 */
public class ByProductChangeIndentServices {

    public static final String module = ByProductChangeIndentServices.class.getName();
    public static final String resource = "ProductUiLabels";
    public static final String resourceOrderError = "OrderErrorUiLabels";
    
    
    
    public static String processChangeIndentMIS(HttpServletRequest request, HttpServletResponse response) {
    	  Delegator delegator = (Delegator) request.getAttribute("delegator");
    	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	  Locale locale = UtilHttp.getLocale(request);
    	  String boothId = (String) request.getParameter("boothId");
    	  String routeChangeFlag = (String) request.getParameter("routeChangeFlag");
    	  String tripId = (String) request.getParameter("tripId");
    	  String effectiveDateStr = (String) request.getParameter("effectiveDate");
    	  String productSubscriptionTypeId = (String) request.getParameter("productSubscriptionTypeId");
    	  String subscriptionTypeId = (String) request.getParameter("subscriptionTypeId");
    	  String shipmentTypeId = "AM_SHIPMENT";
    	  if(UtilValidate.isNotEmpty(request.getParameter("shipmentTypeId"))){
    		  shipmentTypeId = (String) request.getParameter("shipmentTypeId");
    	  }
    	  String productId = null;
    	  String quantityStr = null;
    	  String sequenceNum = null;	  
    	  Timestamp effectiveDate=null;
    	  Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
    	  BigDecimal quantity = BigDecimal.ZERO;
    	  List<GenericValue> subscriptionList=FastList.newInstance();
    	  Map<String, Object> result = ServiceUtil.returnSuccess();
    	  HttpSession session = request.getSession();
    	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	  GenericValue subscription = null;
    	  GenericValue facility = null;
    	  List custTimePeriodList = FastList.newInstance();  	 
    	  if (UtilValidate.isNotEmpty(effectiveDateStr)) { //2011-12-25 18:09:45
    		  SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");             
    		  try {
    			  effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr).getTime());
    		  } catch (ParseException e) {
    			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
                // effectiveDate = UtilDateTime.nowTimestamp();
    		  } catch (NullPointerException e) {
    			  Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, module);
                 //effectiveDate = UtilDateTime.nowTimestamp();
    		  }
    	  }
    	  
    	  List routeIdValidateList = FastList.newInstance();
  	      if(UtilValidate.isNotEmpty(subscriptionTypeId)){
  	    	  try{
  	    		  Map resultCtx = dispatcher.runSync("getRoutesByAMPM", UtilMisc.toMap("supplyType", subscriptionTypeId, "userLogin", userLogin));
    	    	  if(ServiceUtil.isError(resultCtx)){
    	    		  request.setAttribute("_ERROR_MESSAGE_","Error in service getRoutesByAMPM");
    	    		  return "error";
    	    	  }
    	    	  routeIdValidateList = (List)resultCtx.get("routeIdsList");
  	    	  }catch(GenericServiceException e){
  	    		request.setAttribute("_ERROR_MESSAGE_","Error in calling service getRoutesByAMPM");
	    		  return "error";
  	    	  }
  	      }
  	      
    	  if (boothId == "") {
    			request.setAttribute("_ERROR_MESSAGE_","Booth Id is empty");
    			return "error";
    	  }
        // Get the parameters as a MAP, remove the productId and quantity params.
    	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
    	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
    	  if (rowCount < 1) {
    		  Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
    		  return "success";
    	  }
    	  try{
    		  facility=delegator.findOne("Facility", UtilMisc.toMap("facilityId",boothId), false);
    		  if(UtilValidate.isEmpty(facility)){
    			  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
    			  return "error";
    		  }
    		  //lets override productSubscriptionTypeId based on facility category
    		  if(facility.getString("categoryTypeEnum").equals("SO_INST")){
    			  productSubscriptionTypeId = "SPECIAL_ORDER";
    		  }else if(facility.getString("categoryTypeEnum").equals("CR_INST")){
    			 productSubscriptionTypeId = "CREDIT";
    		  }
    	  }catch (GenericEntityException e) {
    		  Debug.logError(e, "Booth does not exist", module);
    		  request.setAttribute("_ERROR_MESSAGE_", "Booth"+" '"+boothId+"'"+" does not exist");
    		  return "error";
    	  }
    	  try {
    		  List conditionList =FastList.newInstance();
    		  conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
	  		  if(subscriptionTypeId.equals("AM")){
	            	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
	            	
	          }else{
	            	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
	          }
	  		  if(UtilValidate.isNotEmpty(tripId)){
	  			  conditionList.add(EntityCondition.makeCondition("tripNum", EntityOperator.EQUALS, tripId));
	  		  }
	  		  EntityCondition subCond =  EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
    		  subscriptionList=delegator.findList("SubscriptionAndFacility", subCond, null, null, null, false);
    		  subscriptionList = EntityUtil.filterByDate(subscriptionList ,effectiveDate);
    		  if(UtilValidate.isEmpty(subscriptionList)){
    			  request.setAttribute("_ERROR_MESSAGE_", "Booth subscription does not exist");
    			  return "error";     		
    		  }
    		  subscription = EntityUtil.getFirst(subscriptionList);
    	  
    	  }  catch (GenericEntityException e) {
    		  Debug.logError(e, "Problem getting Booth subscription", module);
    		  request.setAttribute("_ERROR_MESSAGE_", "Problem getting Booth subscription");
    		  return "error";	
    	  }
    	
    	  List<Map>productQtyList =FastList.newInstance();
    	  for (int i = 0; i < rowCount; i++) {
    		  Map<String  ,Object> productQtyMap = FastMap.newInstance();
    		  
    		  List<GenericValue> subscriptionProductsList = FastList.newInstance();
    		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
    		  if (paramMap.containsKey("productId" + thisSuffix)) {
    			  productId = (String) paramMap.get("productId" + thisSuffix);
    		  }
    		  else {
    			  request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
    			  return "error";			  
    		  }
    		  if (paramMap.containsKey("sequenceNum" + thisSuffix)) {
    			  sequenceNum = (String) paramMap.get("sequenceNum" + thisSuffix);
    			  if(!routeIdValidateList.contains(sequenceNum)){
    				  request.setAttribute("_ERROR_MESSAGE_", sequenceNum+" is not a valid route");
        			  return "error";
    			  }
    		  }	
    		  else {
    			  request.setAttribute("_ERROR_MESSAGE_", "Missing sequence number");
    			  return "error";			  
    		  }		  
    		  if (paramMap.containsKey("quantity" + thisSuffix)) {
    			  quantityStr = (String) paramMap.get("quantity" + thisSuffix);
    		  }
    		  else {
    			  request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
    			  return "error";			  
    		  }		  
    		  if (quantityStr.equals("")) {
    			  request.setAttribute("_ERROR_MESSAGE_", "Empty product quantity");
    			  return "error";	
    		  }
    		  try {
    			  quantity = new BigDecimal(quantityStr);
    		  } catch (Exception e) {
    			  Debug.logError(e, "Problems parsing quantity string: " + quantityStr, module);
    			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + quantityStr);
    			  return "error";
    		  } 
    		  
    		  productQtyMap.put("productId", productId);
    		  productQtyMap.put("quantity", quantity);
    		  productQtyMap.put("sequenceNum", sequenceNum);
    		  productQtyList.add(productQtyMap);
    		       
    	  }//end row count for loop
    	  
    	  if(UtilValidate.isNotEmpty(routeChangeFlag) && routeChangeFlag.equals("Y")){
    		  try{
        		  String dateFmt = UtilDateTime.toDateString(effectiveDate, "dd MMMMM, yyyy");
        		  Map ctxMap = FastMap.newInstance();
        		  ctxMap.put("boothId", boothId);
        		  ctxMap.put("supplyDate", dateFmt);
        		  ctxMap.put("subscriptionTypeId", subscriptionTypeId);
        		  ctxMap.put("productSubscriptionTypeId", productSubscriptionTypeId);
        		  ctxMap.put("screenFlag", "indentAlt");
        		  ctxMap.put("userLogin", userLogin);
        		  Map ctxResultMap = dispatcher.runSync("getBoothChandentIndent",ctxMap);
        		  if(ServiceUtil.isError(ctxResultMap)){
        			  Debug.logError("Error fetching indents from service getBoothChangeIndent", module);
        			  request.setAttribute("_ERROR_MESSAGE_", "Error fetching indents getBoothChangeIndent");
        			  return "error";
        		  }
        		  
        		  List<Map> indentProdList = (List)ctxResultMap.get("changeIndentProductList");
        		         		  
        		  List tempProductList = FastList.newInstance();
        		  
        		  
        		  if(UtilValidate.isNotEmpty(indentProdList)){
        			 
        			  for(Map eachEntry: indentProdList){
          	        	Map tempChangeProdMap = FastMap.newInstance();
          	        	tempChangeProdMap.put("productId", (String)eachEntry.get("cProductId"));
          	        	tempChangeProdMap.put("quantity", BigDecimal.ZERO);
          	        	tempChangeProdMap.put("sequenceNum", (String)eachEntry.get("seqRouteId"));
          	        	tempProductList.add(tempChangeProdMap);
        			  }
        			  
        			  Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);
          	       	 processChangeIndentHelperCtx.put("subscriptionId", subscription.getString("subscriptionId"));
          	       	 processChangeIndentHelperCtx.put("boothId", boothId);
          	       	 processChangeIndentHelperCtx.put("shipmentTypeId", shipmentTypeId);
          	       	 processChangeIndentHelperCtx.put("effectiveDate", effectiveDate);
          	       	 processChangeIndentHelperCtx.put("productQtyList", tempProductList);
          	       	 processChangeIndentHelperCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
          	       	 String indentChanged = "";
          	   		 
          	       	 result = dispatcher.runSync("processChangeIndentHelper",processChangeIndentHelperCtx);
          	   		 
               		 if (ServiceUtil.isError(result)) {
               			String errMsg =  ServiceUtil.getErrorMessage(result);
               			Debug.logError(errMsg , module);
               			request.setAttribute("_ERROR_MESSAGE_",errMsg);
               			return "error";
               		 }
        		  }
        		  
        	  }catch(GenericServiceException e){
        		  Debug.logError(e, "Error calling service getBoothChandentIndent ", module);
    			  request.setAttribute("_ERROR_MESSAGE_", "Error calling service getBoothChandentIndent ");
    			  return "error";
        	  }
    	  }
		  
    	  try{
    		
     		 Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);
			 processChangeIndentHelperCtx.put("subscriptionId", subscription.getString("subscriptionId"));
			 processChangeIndentHelperCtx.put("boothId", boothId);
			 processChangeIndentHelperCtx.put("shipmentTypeId", shipmentTypeId);
			 processChangeIndentHelperCtx.put("effectiveDate", effectiveDate);
			 processChangeIndentHelperCtx.put("productQtyList", productQtyList);
			 processChangeIndentHelperCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
			 String indentChanged = "";
			 
			 result = dispatcher.runSync("processChangeIndentHelper",processChangeIndentHelperCtx);

			 if (ServiceUtil.isError(result)) {
				String errMsg =  ServiceUtil.getErrorMessage(result);
				Debug.logError(errMsg , module);
				request.setAttribute("_ERROR_MESSAGE_",errMsg);
				return "error";
			 }
			 indentChanged = (String)result.get("indentChangeFlag");
			 request.setAttribute("indentChangeFlag", indentChanged);
		 }catch (Exception e) {
				  Debug.logError(e, "Problem updating subscription for booth " + boothId, module);     
				  request.setAttribute("_ERROR_MESSAGE_", "Problem updating subscription for booth " + boothId);
				  return "error";			  
		 }
	     return "success";     
      }
          
      public static Map<String ,Object>  processChangeIndentHelper(DispatchContext dctx, Map<String, ? extends Object> context){
  		  Delegator delegator = dctx.getDelegator();
  	      LocalDispatcher dispatcher = dctx.getDispatcher();       
  	      GenericValue userLogin = (GenericValue) context.get("userLogin");
  	      Map<String, Object> result = ServiceUtil.returnSuccess();
  	      String productSubscriptionTypeId = (String)context.get("productSubscriptionTypeId");
  	      String subscriptionId = (String)context.get("subscriptionId");
  	      String boothId = (String)context.get("boothId");
  	      String shipmentTypeId = (String)context.get("shipmentTypeId");
  	      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
  	      List<Map> productQtyList = (List)context.get("productQtyList");
  	      String routeChangeFlag = (String)context.get("routeChangeFlag");
  	      List<GenericValue> custTimePeriodList =FastList.newInstance();
  	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  	      boolean routeChange = false;
  	      //GenericValue subscription = delegator.findOne("Subscription", UtilMisc.toMap("subscriptionId",subscriptionId), false);
  	      if(productSubscriptionTypeId.equals("CARD")){
  	   		 List custConditionList = UtilMisc.toList(
  	 					  EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"CARD_MONTH"));
  	 			  custConditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
  	 			  custConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(effectiveDate.getTime())));
  	 			  custConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(effectiveDate.getTime())));				  
  	 			  EntityCondition CustCondition = EntityCondition.makeCondition(custConditionList, EntityOperator.AND);
  	 			  try{
  	 				  custTimePeriodList = delegator.findList("CustomTimePeriod", CustCondition, null, null, null,false); 
  	 			  }catch (GenericEntityException e) {
  	 		  		  Debug.logError(e, "Problem getting custom time period", module);	 		  		  
  	 		  		  return ServiceUtil.returnError("Problem getting custom time period");
  	 		  	  }
  	 			 
  	 			  if(UtilValidate.isEmpty(custTimePeriodList)){
  	 				  Debug.logError( "There no active card periods ,Please contact administrator.", module);	 				 
  	 				  return ServiceUtil.returnError("There no active card periods ,Please contact administrator.");
  	 			  }
  	   	  }
  	      if(UtilValidate.isNotEmpty(routeChangeFlag) && routeChangeFlag.equalsIgnoreCase("Y")){
  	    	  routeChange = true;
  	      }
  	      List<String> contIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"CONTINUES_INDENT" ,UtilDateTime.getDayStart(effectiveDate)), "productId", true);
  	      List<String> dayIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"DAILY_INDENT" ,UtilDateTime.getDayStart(effectiveDate)), "productId", true);
  	      
	  	    
    		/*List<GenericValue> crateIndentProducts = ProductWorker.getProductsByCategory(delegator ,"CRATE_INDENT" ,UtilDateTime.getDayStart(effectiveDate));
    		List<GenericValue> packetIndentProducts = ProductWorker.getProductsByCategory(delegator ,"PACKET_INDENT" ,UtilDateTime.getDayStart(effectiveDate));
    		List<String> crateIndentProductList = EntityUtil.getFieldListFromEntityList( crateIndentProducts, "productId", true);
    		List<String> packetIndentProductList = EntityUtil.getFieldListFromEntityList(packetIndentProducts, "productId", true);*/
    		Map prodIndentCat = (Map)ByProductNetworkServices.getFacilityIndentQtyCategories(dctx,UtilMisc.toMap("userLogin", userLogin, "facilityId", boothId)).get("indentQtyCategory");
    		List crateCanIndentProductList = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(prodIndentCat)){
    			String prodId = "";
    			String categoryType = "";
    			Iterator mapIterator = prodIndentCat.entrySet().iterator();
	  			while (mapIterator.hasNext()) {
	  				Map.Entry entry = (Entry) mapIterator.next();
	    			prodId = (String)entry.getKey();
	  	        	categoryType = (String)entry.getValue();
	  	        	if((categoryType.equals("CRATE") || categoryType.equals("CAN")) && !productSubscriptionTypeId.equals("EMP_SUBSIDY")){
	  	        		crateCanIndentProductList.add(prodId);
	  	        	}
	      		}
    		}
    		List<GenericValue> subscriptionProdList =FastList.newInstance();
    		List conditionList = UtilMisc.toList(
  				  EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
  		conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
  		  /*conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));*/  		  
  		if(productSubscriptionTypeId.equals("CARD")){
  			conditionList.add(EntityCondition.makeCondition( EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, null),EntityOperator.OR ,EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custTimePeriodList,"customTimePeriodId" ,false)) ) );
  			 //conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, UtilDateTime.getNextDayStart(nowTimeStamp)));
  		}else{
  			/*conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));*/
  		}
  		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  		boolean indentChanged = false;  
  		try {
  			List<GenericValue> products = delegator.findList("Product", null, UtilMisc.toSet("productId", "quantityIncluded"), null, null, true);
  			  
  			subscriptionProdList = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
  			subscriptionProdList = EntityUtil.filterByDate(subscriptionProdList, effectiveDate);
  			
  			List productsList = EntityUtil.getFieldListFromEntityList(subscriptionProdList, "productId", true);
  			List activeProdList = FastList.newInstance();
  			List<GenericValue> subscriptionProductsList =FastList.newInstance();
  			  for(int i=0; i< productQtyList.size() ; i++){
  				  Map productQtyMap = productQtyList.get(i);
  				  String productId = (String)productQtyMap.get("productId");
  				  String sequenceNum = (String)productQtyMap.get("sequenceNum");
  				  BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");
  				  BigDecimal crateQuantity = BigDecimal.ZERO;
  				  if(crateCanIndentProductList.contains(productId)){
  					  //GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId )));
  					  crateQuantity = ByProductNetworkServices.convertPacketsToCrates(dctx, UtilMisc.toMap("userLogin", userLogin, "productId", productId, "packetQuantity",quantity));
  					  //crateQuantity = quantity;
  					  //quantity = ByProductNetworkServices.convertCratesToPackets(dctx, UtilMisc.toMap("userLogin", userLogin, "productId", productId, "crateQuantity",crateQuantity));
  	    		  
  				  }
  				  /*else{
  					  GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId )));
  					  //crateQuantity = quantity;
  					  if(UtilValidate.isNotEmpty(product)){
  						  crateQuantity = ByProductNetworkServices.convertPacketsToCrates(dctx, UtilMisc.toMap("userLogin", userLogin, "productId", productId, "packetQuantity",quantity));
  					  }
  	    		  
  				  }*/
  				  
  				  conditionList.clear();
  				  conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
  				  conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));
  				  EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  				  subscriptionProductsList = EntityUtil.filterByCondition(subscriptionProdList, cond);
  				  /*List conditionList = UtilMisc.toList(
  					  EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
  				  conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
  				  conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));  		  
  				  if(productSubscriptionTypeId.equals("CARD")){
  					  conditionList.add(EntityCondition.makeCondition( EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, null),EntityOperator.OR ,EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custTimePeriodList,"customTimePeriodId" ,false)) ) );
  					  //conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, UtilDateTime.getNextDayStart(nowTimeStamp)));
  				  }else{
  					  conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));
  				  }
  				  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
  				  try {
  				  subscriptionProductsList = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
  				  subscriptionProductsList = EntityUtil.filterByDate(subscriptionProductsList, effectiveDate);*/
  				  if(UtilValidate.isEmpty(subscriptionProductsList)){
  					  indentChanged = true;
  					  Map createNewSubscriptionProduct = FastMap.newInstance();
  					  createNewSubscriptionProduct.put("facilityId", boothId);
  					  createNewSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);
  					  createNewSubscriptionProduct.put("userLogin",userLogin);
  					  createNewSubscriptionProduct.put("subscriptionId",subscriptionId);
  					  createNewSubscriptionProduct.put("productId", productId);
  					  createNewSubscriptionProduct.put("sequenceNum", sequenceNum);				  
  					  createNewSubscriptionProduct.put("productSubscriptionTypeId", productSubscriptionTypeId);				  
  					  createNewSubscriptionProduct.put("quantity", quantity);
  					  createNewSubscriptionProduct.put("crateQuantity", crateQuantity);
  					  createNewSubscriptionProduct.put("fromDate", UtilDateTime.getDayStart(effectiveDate));
  					 // for now populate thruDate
  					  //createNewSubscriptionProduct.put("thruDate", null);	
  					  createNewSubscriptionProduct.put("thruDate",  UtilDateTime.getDayEnd(effectiveDate));
  					  if(((productSubscriptionTypeId.equals("SPECIAL_ORDER")) || (productSubscriptionTypeId.equals("CASH_FS") || dayIndentProductList.contains(productId)))&&!productSubscriptionTypeId.equals("EMP_SUBSIDY")){
  						  createNewSubscriptionProduct.put("thruDate",  UtilDateTime.getDayEnd(effectiveDate)); 
  					  }
  					  if(productSubscriptionTypeId.equals("CARD")){ 
  						  createNewSubscriptionProduct.put("sequenceNum", null);	
  						  createNewSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp( EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
  						  createNewSubscriptionProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")); 
  					  }
  					  createNewSubscriptionProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
  					  createNewSubscriptionProduct.put("createdDate",nowTimeStamp);   
  					  createNewSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
  					  createNewSubscriptionProduct.put("lastModifiedDate",nowTimeStamp); 
  					  result = dispatcher.runSync("createSubscriptionProduct",createNewSubscriptionProduct);
  					  
  					  if (ServiceUtil.isError(result)) {
  						String errMsg =  ServiceUtil.getErrorMessage(result);
  						Debug.logError(errMsg , module);					
  						return ServiceUtil.returnError(errMsg);
  					  }
  					  createNewSubscriptionProduct.clear();
  					  continue;
  				  }
  				  if(productSubscriptionTypeId.equals("CARD")){
  					  Map createSubscriptionProduct = FastMap.newInstance();
  					  for (GenericValue subscriptionProduct : subscriptionProductsList){
  			  			  Map updateSubscriptionProduct = FastMap.newInstance();  		  			  
  			  			  updateSubscriptionProduct.put("userLogin",userLogin);
  			  			  updateSubscriptionProduct.put("facilityId", boothId);
  			  			  updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
  			  			  updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
  			  			  updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
  			  			  updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
  			  			  updateSubscriptionProduct.put("quantity", subscriptionProduct.getBigDecimal("quantity"));
  			  			  updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
  			  			  updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1)));  		  			 
  			  			  if(subscriptionProduct.getTimestamp("fromDate") != UtilDateTime.getDayStart(effectiveDate)){
  			  				 result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
  			  				if (ServiceUtil.isError(result)) {
  			  					String errMsg =  ServiceUtil.getErrorMessage(result);
  			  					Debug.logError(errMsg , module);
  			  					return ServiceUtil.returnError(errMsg);
  			                  }
  			  			  }
  			  			  
  			  			  createSubscriptionProduct.putAll(updateSubscriptionProduct);
  					  }
  					  BigDecimal newQuantity=quantity;  				  
  					  createSubscriptionProduct.put("quantity", newQuantity);
  					  createSubscriptionProduct.put("crateQuantity", crateQuantity);
  	  			  createSubscriptionProduct.put("fromDate", UtilDateTime.getDayStart(effectiveDate));
  	  			  createSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
  				  createSubscriptionProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId"));	  			   			  
  	  			  result.clear();
  	  			  //to check whether  change indent entered  for the next day
  	  			  List checkSubscriptionProductList = delegator.findByAnd("SubscriptionProduct", 
  	  					  UtilMisc.toMap("subscriptionId",createSubscriptionProduct.get("subscriptionId"),"productId",createSubscriptionProduct.get("productId"),
  	  					  "productSubscriptionTypeId",productSubscriptionTypeId , 
  	  					  "fromDate", UtilDateTime.getDayStart(effectiveDate) ,"customTimePeriodId" ,EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")));	  			  
  	  			 GenericValue checkSubscriptionProduct = EntityUtil.getFirst(checkSubscriptionProductList);
  	  			 if(checkSubscriptionProduct == null || !(checkSubscriptionProduct.get("customTimePeriodId").equals(EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")))){
  	  				    createSubscriptionProduct.put("sequenceNum", null); 
  	  				    createSubscriptionProduct.put("facilityId", boothId);
  	            		createSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);
  	                    createSubscriptionProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
  	              	    createSubscriptionProduct.put("createdDate",nowTimeStamp);   
  	                	createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
  	              	    createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp);             	  
  	  				    result = dispatcher.runSync("createSubscriptionProduct",createSubscriptionProduct);
  	  				    if (ServiceUtil.isError(result)) {
  	    					String errMsg =  ServiceUtil.getErrorMessage(result);
  	    					Debug.logError(errMsg , module);
  	    					return ServiceUtil.returnError(errMsg);
  	                    }
  	  			  }else{
  	  					if(newQuantity.compareTo(checkSubscriptionProduct.getBigDecimal("quantity")) != 0){
  	  						 createSubscriptionProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
  	  		            	 createSubscriptionProduct.put("lastModifiedDate",nowTimeStamp); 				  
  	  				  	}	                	 
  	  				  result = dispatcher.runSync("updateSubscriptionProduct",createSubscriptionProduct);
  	  				  if (ServiceUtil.isError(result)) {
  	  					String errMsg =  ServiceUtil.getErrorMessage(result);
  	  					Debug.logError(errMsg , module);
  	  					return ServiceUtil.returnError(errMsg);
  	                  }
  	  			  } 				  
  					
  				  }else{
  					  boolean createFlag = true;
  					  if(productsList.contains(productId)){
  						  activeProdList.add(productId);
  					  }
  					  GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
  					  Timestamp extSubsDate = (Timestamp)subscriptionProduct.get("fromDate");
  					  if(extSubsDate.compareTo(UtilDateTime.getDayStart(effectiveDate)) != 0){
  						  BigDecimal preQty = subscriptionProduct.getBigDecimal("quantity");
  						  if(preQty.compareTo(quantity)!= 0){
  							  Map updateSubscriptionProduct = FastMap.newInstance();
  				  			  updateSubscriptionProduct.put("userLogin",userLogin);
  				  			  updateSubscriptionProduct.put("facilityId", boothId);
  				  			  updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
  				  			  updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
  				  			  updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
  				  			  updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
  				  			  updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
  				  			  updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1)));
  				  			  if (!productSubscriptionTypeId.equals("SPECIAL_ORDER") || !productSubscriptionTypeId.equals("CASH_FS")	) {
  				  				  // don't close out existing subscription (except for special orders)
  				  				  result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
  				  				  if (ServiceUtil.isError(result)) {
  				  					String errMsg =  ServiceUtil.getErrorMessage(result);
  				  					Debug.logError(errMsg , module);
  				  					return ServiceUtil.returnError(errMsg);
  				                  }
  				  			  }
  						  }
  						  else{
  							  createFlag = false;
  						  }
  					  }
  					  else{
  						  int removed = delegator.removeValue(subscriptionProduct);
  						  Debug.log("removed todays subscription "+subscriptionProduct);
  					  }
  					  if(quantity.compareTo(BigDecimal.ZERO)>0 && createFlag){
  						  indentChanged = true;
  						  Map createNewSubscProduct = FastMap.newInstance();
  						  createNewSubscProduct.put("facilityId", boothId);
  						  createNewSubscProduct.put("shipmentTypeId", shipmentTypeId);
  						  createNewSubscProduct.put("userLogin",userLogin);
  						  createNewSubscProduct.put("subscriptionId",subscriptionId);
  						  createNewSubscProduct.put("productId", productId);
  						  createNewSubscProduct.put("sequenceNum", sequenceNum);				  
  						  createNewSubscProduct.put("productSubscriptionTypeId", productSubscriptionTypeId);				  
  						  createNewSubscProduct.put("quantity", quantity);
  						  createNewSubscProduct.put("crateQuantity", crateQuantity);
  						  createNewSubscProduct.put("fromDate", UtilDateTime.getDayStart(effectiveDate));
  						  if(routeChange){
  							  createNewSubscProduct.put("thruDate", UtilDateTime.getDayEnd(effectiveDate));
  						  }
  						  else{
  							  // for now populate thruDate
  							  //createNewSubscProduct.put("thruDate", null);  
  							  createNewSubscProduct.put("thruDate", UtilDateTime.getDayEnd(effectiveDate));
  						  }
  						  				  
  						  if(((productSubscriptionTypeId.equals("SPECIAL_ORDER")) || (productSubscriptionTypeId.equals("CASH_FS") || dayIndentProductList.contains(productId)))&&!productSubscriptionTypeId.equals("EMP_SUBSIDY")){
  							  createNewSubscProduct.put("thruDate",  UtilDateTime.getDayEnd(effectiveDate)); 
  						  }
  						  if(productSubscriptionTypeId.equals("CARD")){ 
  							  createNewSubscProduct.put("sequenceNum", null);	
  							  createNewSubscProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.toTimestamp( EntityUtil.getFirst(custTimePeriodList).getDate("thruDate"))));
  							  createNewSubscProduct.put("customTimePeriodId", EntityUtil.getFirst(custTimePeriodList).getString("customTimePeriodId")); 
  						  }
  						  createNewSubscProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
  						  createNewSubscProduct.put("createdDate",nowTimeStamp);   
  						  createNewSubscProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
  						  createNewSubscProduct.put("lastModifiedDate",nowTimeStamp);
  						  Debug.log("createNewSubscProduct #########################"+createNewSubscProduct);
  						  result = dispatcher.runSync("createSubscriptionProduct",createNewSubscProduct);
  						  if (ServiceUtil.isError(result)) {
  							String errMsg =  ServiceUtil.getErrorMessage(result);
  							Debug.logError(errMsg , module);					
  							return ServiceUtil.returnError(errMsg);
  						  }
  					  }
  				  }
  			  }//end of product Qty List
  			  if(routeChange){
  				  Timestamp nextEffDay = UtilDateTime.addDaysToTimestamp(effectiveDate, 1);
  				  List condList = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
  				  condList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
  				  condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(nextEffDay)));
  				  condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
  						  EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(nextEffDay))));
  				  EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
  				  List<GenericValue> removeSubscriptionProdList = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
  				  Debug.log("removeSubscriptionProdList"+removeSubscriptionProdList);
  				  if(UtilValidate.isNotEmpty(removeSubscriptionProdList)){
  					  int removedNxtDaySubs = delegator.removeAll(removeSubscriptionProdList);
  				  }
  				  
  			  }
  			  
  			  if(UtilValidate.isNotEmpty(activeProdList)){
  				  List<GenericValue> subscriptionProdClose = EntityUtil.filterByCondition(subscriptionProdList, EntityCondition.makeCondition("productId", EntityOperator.NOT_IN, UtilMisc.toList(activeProdList)));
  				  for(int j=0;j<subscriptionProdClose.size();j++){
  					  GenericValue subcProdClose = (GenericValue)subscriptionProdClose.get(j);
  					  Map updateSubscriptionClose = FastMap.newInstance();
  					  updateSubscriptionClose.put("userLogin",userLogin);
  					  updateSubscriptionClose.put("facilityId", boothId);
  					  updateSubscriptionClose.put("subscriptionId",subcProdClose.getString("subscriptionId"));
  					  updateSubscriptionClose.put("productId",subcProdClose.getString("productId"));
  					  updateSubscriptionClose.put("productSubscriptionTypeId",subcProdClose.getString("productSubscriptionTypeId"));
  					  updateSubscriptionClose.put("sequenceNum",subcProdClose.getString("sequenceNum"));
  					  updateSubscriptionClose.put("fromDate", subcProdClose.getTimestamp("fromDate"));
  					  updateSubscriptionClose.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1)));
  					Debug.log("updateSubscriptionClose #############"+updateSubscriptionClose);
  		  			  if (!productSubscriptionTypeId.equals("SPECIAL_ORDER") || !productSubscriptionTypeId.equals("CASH_FS")	) {
  		  				  // don't close out existing subscription (except for special orders)
  		  				  result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionClose);
  		  				  if (ServiceUtil.isError(result)) {
  		  					String errMsg =  ServiceUtil.getErrorMessage(result);
  		  					Debug.logError(errMsg , module);
  		  					return ServiceUtil.returnError(errMsg);
  		                  }
  		  			  }
  				  }
  				  
  			  }
  			  
  		  }catch (Exception e) {
  			  Debug.logError(e, "Problem updating subscription for booth " + boothId, module);		  
  			  return ServiceUtil.returnError("Problem updating subscription for booth " + boothId);			  
  		  }
  		  String change = "NotChanged";
  		  if(indentChanged){
  			  change = "Changed";
  		  }
  		  result.put("indentChangeFlag", change);
  		return result;  
      }

      public static Map<String ,Object>  processChangeRouteIndentHelper(DispatchContext dctx, Map<String, ? extends Object> context){
		  Delegator delegator = dctx.getDelegator();
	      LocalDispatcher dispatcher = dctx.getDispatcher();       
	      GenericValue userLogin = (GenericValue) context.get("userLogin");
	      Map<String, Object> result = ServiceUtil.returnSuccess();
	      String productSubscriptionTypeId = (String)context.get("productSubscriptionTypeId");
	      String subscriptionId = (String)context.get("subscriptionId");
	      String boothId = (String)context.get("boothId");
	      String shipmentTypeId = (String)context.get("shipmentTypeId");
	      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
	      List<Map> productQtyList = (List)context.get("productQtyList");
	      Map inputMap = FastMap.newInstance();
	      inputMap.put("subscriptionId", subscriptionId);
	      inputMap.put("productSubscriptionTypeId", productSubscriptionTypeId);
	      inputMap.put("boothId", boothId);
	      inputMap.put("shipmentTypeId", shipmentTypeId);
	      inputMap.put("effectiveDate", effectiveDate);
	      inputMap.put("routeChangeFlag", "Y");
	      inputMap.put("productQtyList", productQtyList);
	      inputMap.put("userLogin", userLogin);
	      
	      try{
	    	  result = dispatcher.runSync("processChangeIndentHelper",inputMap);
	    	  if (ServiceUtil.isError(result)) {
	    		  Debug.logError("Error in service processChangeIndentHelper", module);
	    		  return ServiceUtil.returnError("Error in service processChangeIndentHelper");
	    	  }
	    	  Map boothDetails = (Map)(ByProductNetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "userLogin", userLogin))).get("boothDetails");
	    	  String defaultRouteId = (String)boothDetails.get("routeId");
	    	  List<GenericValue> productCategoryList = FastList.newInstance();
	    	  List prodCatCondition = UtilMisc.toList(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, "CONTINUES_INDENT"));
	    	  /*prodCatCondition.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, "MILK"));*/
	    	  EntityCondition prodCatCond = EntityCondition.makeCondition(prodCatCondition, EntityOperator.AND);
	    	  productCategoryList = delegator.findList("ProductAndCategoryMember", prodCatCond, null, null, null, false);
	    	  productCategoryList = EntityUtil.filterByDate(productCategoryList, effectiveDate);
	    	  List initProductList = EntityUtil.getFieldListFromEntityList(productCategoryList, "productId", true);
	    	  List<Map> initProductQtyList = FastList.newInstance();
	    	  for(int i=0; i< productQtyList.size() ; i++){
				  Map productQtyMap = productQtyList.get(i);
				  String productId = (String)productQtyMap.get("productId");
				  if(initProductList.contains(productId)){
					  productQtyMap.put("sequenceNum", defaultRouteId);
					  initProductQtyList.add(productQtyMap);
				  }
	    	  }
	    	  if(UtilValidate.isNotEmpty(initProductQtyList)){
	    		  Map inputInitMap = FastMap.newInstance();
	    		  inputInitMap.put("subscriptionId", subscriptionId);
	    		  inputInitMap.put("productSubscriptionTypeId", productSubscriptionTypeId);
	    		  inputInitMap.put("boothId", boothId);
	    		  inputInitMap.put("shipmentTypeId", shipmentTypeId);
	    		  inputInitMap.put("userLogin", userLogin);
	    		  /*inputInitMap.put("routeChangeFlag", "Y");*/
	    		  inputInitMap.put("effectiveDate", (Timestamp)UtilDateTime.addDaysToTimestamp(effectiveDate, 1));
	    		  inputInitMap.put("productQtyList", initProductQtyList);
			      Map resultInit = dispatcher.runSync("processChangeIndentHelper",inputInitMap);
		    	  if (ServiceUtil.isError(resultInit)) {
		    		  Debug.logError("Error in service processChangeIndentHelper", module);
		    		  return ServiceUtil.returnError("Error in service processChangeIndentHelper");
		    	  }
	    	  }
	      }catch(Exception e){
	    	  Debug.logError(e, "Problem updating subscription product for booth " + boothId, module);		  
			  return ServiceUtil.returnError("Problem temporary route change for dealer" + boothId);
	      }
	      return ServiceUtil.returnSuccess();  
    }
      
    public static String createSpecialOrderSubscription(HttpServletRequest request, HttpServletResponse response){
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		//Locale locale = UtilHttp.getLocale(request);
		String boothId = (String) request.getParameter("boothId");
		String fromDateStr = (String) request.getParameter("fromDate");
		String thruDateStr = (String) request.getParameter("thruDate");
		String productSubscriptionTypeId = "SPECIAL_ORDER";
		String shipmentTypeId = "AM_SHIPMENT"; // ::TODO::
		String subscriptionTypeId = "AM"; // ::TODO::
		
		String productId = null;
		String quantityStr = null;
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		BigDecimal quantity = BigDecimal.ZERO;
		Map createSubscriptionMap = UtilMisc.toMap("userLogin", userLogin);
		Map<String, Object> result = ServiceUtil.returnSuccess();		
		if (UtilValidate.isNotEmpty(fromDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr)
						.getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + fromDateStr,
						module);
				// effectiveDate = UtilDateTime.nowTimestamp();
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + fromDateStr,
						module);
				// effectiveDate = UtilDateTime.nowTimestamp();
			}
		}
		if (UtilValidate.isNotEmpty(thruDateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				thruDate = new java.sql.Timestamp(sdf.parse(thruDateStr)
						.getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + thruDateStr,
						module);
				// effectiveDate = UtilDateTime.nowTimestamp();
			} catch (NullPointerException e) {
				Debug.logError(e, "Cannot parse date string: " + thruDateStr,
						module);
				// effectiveDate = UtilDateTime.nowTimestamp();
			}
		}
		// create new subscription for the facility(Booth)

		createSubscriptionMap.put("facilityId", boothId);
		createSubscriptionMap.put("subscriptionTypeId", subscriptionTypeId);
		createSubscriptionMap.put("fromDate", fromDate);
		createSubscriptionMap.put("thruDate", thruDate);
		try {

			result = dispatcher.runSync("createSubscription",createSubscriptionMap);

		} catch (GenericServiceException e) {
			Debug.logError(e, "Problem creating  subscription", module);
		}
		String subscriptionId = (String) result.get("subscriptionId");
		request.setAttribute("subscriptionId", subscriptionId);		
		// Get the parameters as a MAP, remove the productId and quantity
		// params.
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logWarning("No rows to process, as rowCount = " + rowCount,module);
		} else {
			for (int i = 0; i < rowCount; i++) {
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.remove("productId"
							+ thisSuffix);
				}
				if (paramMap.containsKey("quantity" + thisSuffix)) {
					quantityStr = (String) paramMap.remove("quantity"
							+ thisSuffix);
				}
				if ((quantityStr == null) || (quantityStr.equals(""))) {
					continue;
				}
				try {
					quantity = new BigDecimal(quantityStr);
				} catch (Exception e) {
					Debug.logWarning(e, "Problems parsing quantity string: "+ quantityStr, module);
					quantity = BigDecimal.ZERO;
				}

				try {
					Map createSubscriptionProduct = UtilMisc.toMap("userLogin",userLogin);
					createSubscriptionProduct.put("subscriptionId",	subscriptionId);
					createSubscriptionProduct.put("facilityId", boothId);					
					createSubscriptionProduct.put("shipmentTypeId", shipmentTypeId);					
					createSubscriptionProduct.put("productId", productId);
					createSubscriptionProduct.put("productSubscriptionTypeId",productSubscriptionTypeId);
					createSubscriptionProduct.put("quantity", quantity);
					Timestamp tempFromDate = fromDate;
					Timestamp tempThruDate = nowTimeStamp;
					while (tempThruDate.before(thruDate)) {
						tempThruDate =UtilDateTime.getDayEnd(tempFromDate);
						if(tempThruDate.after(thruDate)){
							tempThruDate = thruDate;							
						}
						createSubscriptionProduct.put("fromDate", tempFromDate);
						createSubscriptionProduct.put("thruDate", tempThruDate);
						result = dispatcher.runSync("createSubscriptionProduct",createSubscriptionProduct);
						tempFromDate= UtilDateTime.getNextDayStart(tempThruDate);						
					}

				} catch (Exception e) {
					Debug.logError(e, "Problem getting order subscription",
							module);
				}
			}
		}
		return "success";
	}   
    public static Map<String, Object> absenteeOverrideForBooth(DispatchContext dctx, Map<String, Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = (String) context.get("facilityId");
        Locale locale = (Locale) context.get("locale");       
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        Timestamp supplyDate = (Timestamp) context.get("supplyDate");
        Map<String, Object> result = new HashMap<String, Object>();
        Map boothsPaymentsDetail = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        boothsPaymentsDetail = ByProductNetworkServices.getBoothPayments(delegator ,dispatcher ,userLogin , UtilDateTime.nowDateString("yyyy-MM-dd"),null ,facilityId ,null ,Boolean.FALSE);        
        List boothPaymentsList = (List)boothsPaymentsDetail.get("boothPaymentsList");
        if (UtilValidate.isEmpty(boothPaymentsList)) {
            Debug.logError("No payment dues found for booth; " + facilityId, module);
            return ServiceUtil.returnError("No payment dues found for Booth " + facilityId);			
		}
        GenericValue newEntity = delegator.makeValue("AbsenteeOverride");
        newEntity.set("boothId", facilityId);
        newEntity.set("supplyDate", new java.sql.Date((new Date().getTime())+(1000 * 60 * 60 * 24)));
        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
        try {
            delegator.create(newEntity);            
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());            
        } 
        return result;
    }
    public static Map<String, Object> removeAbsenteeOverride(DispatchContext dctx, Map<String, Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String facilityId = (String) context.get("facilityId");
        Locale locale = (Locale) context.get("locale");       
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        Timestamp supplyDate = (Timestamp) context.get("supplyDate");
        Map<String, Object> result = new HashMap<String, Object>();
        Map boothsPaymentsDetail = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");        
        GenericValue newEntity = delegator.makeValue("AbsenteeOverride");
        newEntity.set("boothId", facilityId);
        try{
         newEntity.set("supplyDate",((new DateTimeConverters.TimestampToSqlDate()).convert(supplyDate)));
        }catch(ConversionException e){
            Debug.logError(e, module);			
            return ServiceUtil.returnError(e.getMessage());        	
		}       
       
        try {
            delegator.removeValue(newEntity);            
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());            
        } 
        return result;
    }
}
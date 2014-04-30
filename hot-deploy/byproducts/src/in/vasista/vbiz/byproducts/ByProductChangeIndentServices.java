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
import org.ofbiz.entity.transaction.TransactionUtil;
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
    	  List genRouteIds = FastList.newInstance();
    	  try {
              // make sure this is in a transaction
              boolean beganTransaction = TransactionUtil.begin();
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
    	    		  TransactionUtil.rollback(beganTransaction, ServiceUtil.getErrorMessage(resultCtx),null);
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
    		  genRouteIds = ByProductNetworkServices.getShipedRouteIdsByAMPM(delegator , UtilDateTime.toDateString(effectiveDate, "yyyy-MM-dd HH:mm:ss"),subscriptionTypeId,null);
    	  
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
    		  if(genRouteIds.contains(sequenceNum)){
    			  String errMsg="Trucksheet already generated for the route :"+ sequenceNum;
	    			Debug.logError(errMsg , module);
	      			request.setAttribute("_ERROR_MESSAGE_",errMsg);
	      			return "error";
	    	  }     
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
        			  TransactionUtil.rollback(beganTransaction, ServiceUtil.getErrorMessage(ctxResultMap),null);
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
          	       	 processChangeIndentHelperCtx.put("routeChangeFlag", routeChangeFlag);
          	       	 processChangeIndentHelperCtx.put("shipmentTypeId", shipmentTypeId);
          	       	 processChangeIndentHelperCtx.put("effectiveDate", effectiveDate);
          	       	 processChangeIndentHelperCtx.put("productQtyList", tempProductList);
          	       	 processChangeIndentHelperCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
          	       	 processChangeIndentHelperCtx.put("subscriptionTypeId", subscriptionTypeId);
          	       
          	       	 String indentChanged = "";
          	   		 
          	       	 result = dispatcher.runSync("processChangeIndentHelper",processChangeIndentHelperCtx);
          	   		 
               		 if (ServiceUtil.isError(result)) {
               			String errMsg =  ServiceUtil.getErrorMessage(result);
               			Debug.logError(errMsg , module);
               			request.setAttribute("_ERROR_MESSAGE_",errMsg);
               		 TransactionUtil.rollback(beganTransaction, ServiceUtil.getErrorMessage(result),null);
               			return "error";
               		 }
        		  }
        		  
        	  }catch(GenericServiceException e){
        		  
        		  Debug.logError(e, "Error calling service getBoothChandentIndent ", module);
    			  request.setAttribute("_ERROR_MESSAGE_", "Error calling service getBoothChandentIndent ");
    			  TransactionUtil.rollback(beganTransaction,null,e);
    			  return "error";
        	  }
    	  }
		  
    	  try{
    		
     		 Map processChangeIndentHelperCtx = UtilMisc.toMap("userLogin",userLogin);
			 processChangeIndentHelperCtx.put("subscriptionId", subscription.getString("subscriptionId"));
			 processChangeIndentHelperCtx.put("boothId", boothId);
			 processChangeIndentHelperCtx.put("subscriptionTypeId", subscriptionTypeId);
  	       	 processChangeIndentHelperCtx.put("routeChangeFlag", routeChangeFlag);
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
				TransactionUtil.rollback(beganTransaction, ServiceUtil.getErrorMessage(result),null);
				return "error";
			 }
			 indentChanged = (String)result.get("indentChangeFlag");
			 request.setAttribute("indentChangeFlag", indentChanged);
			 TransactionUtil.commit(beganTransaction);
		 }catch (Exception e) {
				  Debug.logError(e, "Problem updating subscription for booth " + boothId, module);     
				  request.setAttribute("_ERROR_MESSAGE_", "Problem updating subscription for booth " + boothId);
				  TransactionUtil.rollback(beganTransaction, null,e);
				  return "error";			  
		 }
		 
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
  	      String subscriptionTypeId = (String)context.get("subscriptionTypeId");
  	      String boothId = (String)context.get("boothId");
  	      String secaCall = (String)context.get("enableSECA");
  	      String shipmentTypeId = (String)context.get("shipmentTypeId");
  	      Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");	      
  	      List<Map> productQtyList = (List)context.get("productQtyList");
  	      String routeChangeFlag = (String)context.get("routeChangeFlag");
  	      List<GenericValue> custTimePeriodList =FastList.newInstance();
  	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  	      Boolean enableContinuousIndent = Boolean.FALSE;
  	      try{
  	    	  GenericValue tenantEnableContinuousIndent = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableContinuousIndent"), false);
  	    	  if (UtilValidate.isNotEmpty(tenantEnableContinuousIndent) && (tenantEnableContinuousIndent.getString("propertyValue")).equals("Y")) {
  	    		  enableContinuousIndent = Boolean.TRUE;
  	    	  }
  	      }catch(GenericEntityException e){
  	    	  Debug.log("Error in fetching Tenant Configuration");		  
			  return ServiceUtil.returnError("Error in fetching Tenant Configuration");
  	      }
    	  
  	      Map nxtDayPermanentIndent = FastMap.newInstance();
  	      String defaultRouteId = "";
  	      boolean routeChange = false;
  	      if(enableContinuousIndent){
  	    	  Map boothDetails = (Map)(ByProductNetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "subscriptionTypeId", subscriptionTypeId, "userLogin", userLogin))).get("boothDetails");
  	    	  defaultRouteId = (String)boothDetails.get("routeId");
  		  
  	    	  if(UtilValidate.isEmpty(defaultRouteId)){
  	    		Debug.log("Permanent Route doesn't exist for retailer " + boothId);
    			  return ServiceUtil.returnError("Permanent Route doesn't exist for retailer " + boothId);
  	    	  }
  	      }
  	      else{
  	    	  if(UtilValidate.isNotEmpty(routeChangeFlag) && routeChangeFlag.equalsIgnoreCase("Y")){
  	    		  routeChange = true;
  	    	  }
  	      }
  	      
  	      List<String> contIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"CONTINUES_INDENT" ,UtilDateTime.getDayStart(effectiveDate)), "productId", true);
  	      //List<String> dayIndentProductList = EntityUtil.getFieldListFromEntityList(ProductWorker.getProductsByCategory(delegator ,"DAILY_INDENT" ,UtilDateTime.getDayStart(effectiveDate)), "productId", true);
  	      
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
    		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
    		conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
    		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    		boolean indentChanged = false;  
    		try {
	  			//List<GenericValue> products = delegator.findList("Product", null, UtilMisc.toSet("productId", "quantityIncluded"), null, null, true);
	  			  
	  			subscriptionProdList = delegator.findList("SubscriptionProduct", condition, null, null, null, false);
	  			subscriptionProdList = EntityUtil.filterByDate(subscriptionProdList, effectiveDate);
	  			List productsList = EntityUtil.getFieldListFromEntityList(subscriptionProdList, "productId", true);
	  			List activeProdList = FastList.newInstance();
	  			List<GenericValue> subscriptionProductsList = FastList.newInstance();
	  			for(int i=0; i< productQtyList.size() ; i++){
	  				Map productQtyMap = productQtyList.get(i);
	  				String productId = (String)productQtyMap.get("productId");
	  				String sequenceNum = (String)productQtyMap.get("sequenceNum");
	  				BigDecimal quantity = (BigDecimal)productQtyMap.get("quantity");
	  				BigDecimal crateQuantity = BigDecimal.ZERO;
	  				if(!sequenceNum.equalsIgnoreCase(defaultRouteId) && !routeChange && enableContinuousIndent){
	  					routeChange = true;
	  				}
	  				if(crateCanIndentProductList.contains(productId)){
	  					//GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId )));
	  					crateQuantity = ByProductNetworkServices.convertPacketsToCrates(dctx, UtilMisc.toMap("userLogin", userLogin, "productId", productId, "packetQuantity",quantity));
	  					//crateQuantity = quantity;
	  					//quantity = ByProductNetworkServices.convertCratesToPackets(dctx, UtilMisc.toMap("userLogin", userLogin, "productId", productId, "crateQuantity",crateQuantity));
  	    		  
	  				}
	  				  
	  				  conditionList.clear();
	  				  conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	  				  if(!productSubscriptionTypeId.equals("EMP_SUBSIDY")){
	  					  conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, sequenceNum));
					  }
	  				  EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	  				  
	  				  subscriptionProductsList = EntityUtil.filterByCondition(subscriptionProdList, cond);
	  				  
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
	  					  if(enableContinuousIndent && contIndentProductList.contains(productId) && !routeChange && UtilValidate.isEmpty(routeChangeFlag)){
	  						  createNewSubscriptionProduct.put("thruDate", null);
	  					  }
	  					  else{
	  						  createNewSubscriptionProduct.put("thruDate",  UtilDateTime.getDayEnd(effectiveDate));
	  					  }
	  					  
	  					//for employee route shift thruDate is null later remove below condition
	  					  if(!enableContinuousIndent && productSubscriptionTypeId.equals("EMP_SUBSIDY")){
	  						createNewSubscriptionProduct.put("thruDate",  null);
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
	  				  // subscription already exists
  				  
	  				  boolean createFlag = true;
	  				  if(productsList.contains(productId)){
	  					  activeProdList.add(productId);
	  				  }
	  				  GenericValue subscriptionProduct = EntityUtil.getFirst(subscriptionProductsList);
	  				  Timestamp extSubsDate = (Timestamp)subscriptionProduct.get("fromDate");
	  				  if(extSubsDate.compareTo(UtilDateTime.getDayStart(effectiveDate)) != 0 ){
	  					  BigDecimal preQty = subscriptionProduct.getBigDecimal("quantity");
	  					  if(preQty.compareTo(quantity)!= 0 || productSubscriptionTypeId.equalsIgnoreCase("EMP_SUBSIDY")){
							  Map updateSubscriptionProduct = FastMap.newInstance();
				  			  updateSubscriptionProduct.put("userLogin",userLogin);
				  			  updateSubscriptionProduct.put("facilityId", boothId);
				  			  updateSubscriptionProduct.put("subscriptionId",subscriptionProduct.getString("subscriptionId"));
				  			  updateSubscriptionProduct.put("productId",subscriptionProduct.getString("productId"));
				  			  updateSubscriptionProduct.put("productSubscriptionTypeId",subscriptionProduct.getString("productSubscriptionTypeId"));
				  			  updateSubscriptionProduct.put("sequenceNum",subscriptionProduct.getString("sequenceNum"));
				  			  updateSubscriptionProduct.put("fromDate", subscriptionProduct.getTimestamp("fromDate"));
				  			  updateSubscriptionProduct.put("thruDate", UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1)));
				  			  result = dispatcher.runSync("updateSubscriptionProduct",updateSubscriptionProduct);
			  				  if (ServiceUtil.isError(result)) {
			  					  String errMsg =  ServiceUtil.getErrorMessage(result);
			  					  Debug.logError(errMsg , module);
			  					  return ServiceUtil.returnError(errMsg);
			  				  }
	  					  }
						  else{
								createFlag = false;
						  }
	  				  }
					  else{
						  int removed = delegator.removeValue(subscriptionProduct);
						  //Debug.log("removed todays subscription "+subscriptionProduct);
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
						  if(enableContinuousIndent && contIndentProductList.contains(productId) && !routeChange && UtilValidate.isEmpty(routeChangeFlag)){
							  createNewSubscProduct.put("thruDate", null);
	  					  }
	  					  else{
	  						  createNewSubscProduct.put("thruDate",  UtilDateTime.getDayEnd(effectiveDate));
	  					  }
	  					  
	  					//for employee route shift thruDate is null later remove below condition
	  					  if(!enableContinuousIndent && productSubscriptionTypeId.equals("EMP_SUBSIDY")){
	  						  createNewSubscProduct.put("thruDate",  null);
	  					  }
						  createNewSubscProduct.put("createdByUserLogin",userLogin.get("userLoginId"));
						  createNewSubscProduct.put("createdDate",nowTimeStamp);   
						  createNewSubscProduct.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
						  createNewSubscProduct.put("lastModifiedDate",nowTimeStamp);
						  result = dispatcher.runSync("createSubscriptionProduct",createNewSubscProduct);
						  if (ServiceUtil.isError(result)) {
							String errMsg =  ServiceUtil.getErrorMessage(result);
							Debug.logError(errMsg , module);					
							return ServiceUtil.returnError(errMsg);
						  }
					  }
	  			}//end of product Qty List

	  			List condList = FastList.newInstance();
	  			Timestamp nextEffDay = UtilDateTime.addDaysToTimestamp(effectiveDate, 1);
	  			if(routeChange){
					  condList.clear();
					  condList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
					  condList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
					  condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(nextEffDay)));
					  condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
							  EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(nextEffDay))));
					  EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
					  List<GenericValue> removeSubscriptionProdList = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
					  //Debug.log("removeSubscriptionProdList"+removeSubscriptionProdList);
					  if(UtilValidate.isNotEmpty(removeSubscriptionProdList)){
						  int removedNxtDaySubs = delegator.removeAll(removeSubscriptionProdList);
					  }
			  	}
	  			
	  			if(enableContinuousIndent && !productSubscriptionTypeId.equalsIgnoreCase("EMP_SUBSIDY") && routeChange){
	  				condList.clear();
	  			   	condList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
	  	  			condList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
	  	  			condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(effectiveDate)));
	  	  			condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
						  EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(effectiveDate))));
	  	  			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	  	  			List<GenericValue> getTodaysIndent = delegator.findList("SubscriptionProduct", cond, null, null, null, false);
	  	  			List<String> todayProductList = EntityUtil.getFieldListFromEntityList(getTodaysIndent, "productId", true);
	  	  			List<String> nxtDayContIndentProd = FastList.newInstance();
	  	  			for(String eachProductId : todayProductList){
	  	  				if(contIndentProductList.contains(eachProductId)){
	  	  					nxtDayContIndentProd.add(eachProductId);
	  	  				}
	  	  			}
	  	  			Map createNxtDayIndent = FastMap.newInstance();
	  	  			if(UtilValidate.isNotEmpty(nxtDayContIndentProd)){
	  	  				for(String contIndentProduct: nxtDayContIndentProd){
	  	  					List<GenericValue> eachProdIndents = EntityUtil.filterByCondition(getTodaysIndent, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, contIndentProduct));
	  	  					BigDecimal nxtDayQty = BigDecimal.ZERO; 
	  	  					for(GenericValue eachIndent : eachProdIndents){
	  	  						nxtDayQty = nxtDayQty.add(eachIndent.getBigDecimal("quantity")); 
	  	  					}
	  	  					createNxtDayIndent.put("facilityId", boothId);
	  	  					createNxtDayIndent.put("shipmentTypeId", shipmentTypeId);
	  	  					createNxtDayIndent.put("userLogin",userLogin);
	  	  					createNxtDayIndent.put("subscriptionId",subscriptionId);
	  	  					createNxtDayIndent.put("productId", contIndentProduct);
	  	  					createNxtDayIndent.put("sequenceNum", defaultRouteId);				  
	  	  					createNxtDayIndent.put("productSubscriptionTypeId", productSubscriptionTypeId);				  
	  	  					createNxtDayIndent.put("quantity", nxtDayQty);
	  	  					createNxtDayIndent.put("fromDate", UtilDateTime.getDayStart(nextEffDay));
	  	  					createNxtDayIndent.put("thruDate", null);
	  						createNxtDayIndent.put("createdByUserLogin",userLogin.get("userLoginId"));
	  						createNxtDayIndent.put("createdDate",nowTimeStamp);   
	  						createNxtDayIndent.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
	  						createNxtDayIndent.put("lastModifiedDate",nowTimeStamp); 
	  						Map resultCtx = dispatcher.runSync("createSubscriptionProduct",createNxtDayIndent);
	  						if (ServiceUtil.isError(result)) {
	  							String errMsg =  ServiceUtil.getErrorMessage(result);
	  							Debug.logError(errMsg , module);					
	  							return ServiceUtil.returnError(errMsg);
	  						}
	  						createNxtDayIndent.clear();
	  	  				}
	  	  			}
	  					
	  			}
	  			
  			    result.put("subscriptionId", subscriptionId);
  			    result.put("supplyDate", effectiveDate);
  			    result.put("enableSECA", secaCall);
  			    result.put("shipmentTypeId", shipmentTypeId);
  			  
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
      public static Map<String ,Object>  routeShiftEmpSubsidy(DispatchContext dctx, Map<String, ? extends Object> context){
  		  Delegator delegator = dctx.getDelegator();
  	      LocalDispatcher dispatcher = dctx.getDispatcher();       
  	      GenericValue userLogin = (GenericValue) context.get("userLogin");
  	      Map<String, Object> result = ServiceUtil.returnSuccess();
  	      String subscriptionId = (String)context.get("subscriptionId");
  	      String shipmentTypeId = (String)context.get("shipmentTypeId");
  	      Timestamp effectiveDate = (Timestamp)context.get("supplyDate");
  	      List condList = FastList.newInstance();
  	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
  	      String subsidyProduct = "15";
  	      List<String> altShiftProduct = UtilMisc.toList("470");
  	      try{
  	    	  
  	    	  Boolean enableSubsidyRouteShift = Boolean.FALSE;
  	    	  GenericValue tenantConfigEnableSubsidyShift = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSubsidyRouteShift"), false);
  	    	  if (UtilValidate.isNotEmpty(tenantConfigEnableSubsidyShift) && (tenantConfigEnableSubsidyShift.getString("propertyValue")).equals("Y")) {
  	    		enableSubsidyRouteShift = Boolean.TRUE;
  	    	  }
  	    	  if(enableSubsidyRouteShift){
	  	    	  condList.clear();
	  	  		  condList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
	  	  		  condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, subsidyProduct));
	  	  		  condList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "EMP_SUBSIDY"));
	  	  		  EntityCondition subsidyCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	  	  		  List<GenericValue> subsidyList = delegator.findList("SubscriptionProduct", subsidyCond, null, null, null, false);
	  	  		  subsidyList = EntityUtil.filterByDate(subsidyList, effectiveDate);
	  	  		   BigDecimal actualSubsidyQty = BigDecimal.ZERO;
	  	  		  if(UtilValidate.isNotEmpty(subsidyList)){
	  	  			  actualSubsidyQty = ((GenericValue)EntityUtil.getFirst(subsidyList)).getBigDecimal("quantity");
	  	  		  }
	  	  		  condList.clear();
	  	  		  condList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
	  	  		  condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN, altShiftProduct), EntityOperator.OR, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, subsidyProduct)));
	  	  		  condList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "AM"));
	  	  		  condList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.NOT_EQUAL, "EMP_SUBSIDY"));
	  	  		  condList.add(EntityCondition.makeCondition("quantity", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
	  	  		  condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayStart(effectiveDate)));
	  	  		  condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
	  	  				  EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayEnd(effectiveDate))));
	  	  		  EntityCondition shiftCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	  	  		  List<GenericValue> indentList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", shiftCond, null, null, null, false);
	  	  		  List<GenericValue> tmsLtrIndentList = EntityUtil.filterByCondition(indentList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, subsidyProduct));
	  	  		  List<GenericValue> tmsHalfLtrIndentList = EntityUtil.filterByCondition(indentList, EntityCondition.makeCondition("productId", EntityOperator.IN, altShiftProduct));
	  	  		  if(UtilValidate.isNotEmpty(tmsLtrIndentList) || UtilValidate.isNotEmpty(tmsHalfLtrIndentList)){
		  			 
	  	  			  String boothId = "";
	  	  			  String subscriptionTypeId = "";
	  	  			  String tmsInRoute = "";
	  	  			  String defaultRouteId = "";
	  	  			  boolean subsidyShift = false;
	  	  			  GenericValue empSubscription = null;
	  	  			  boolean checkTMSHalfLtr = true;
	  	  			  List<String> tmsRouteList = FastList.newInstance();
	  	  			  if(UtilValidate.isNotEmpty(tmsLtrIndentList)){
	  	  				  empSubscription = EntityUtil.getFirst(tmsLtrIndentList);
			  			  boothId = empSubscription.getString("facilityId");
			  			  subscriptionTypeId = empSubscription.getString("subscriptionTypeId");
			  			  tmsRouteList = EntityUtil.getFieldListFromEntityList(tmsLtrIndentList, "sequenceNum", true);
			  			  if(UtilValidate.isNotEmpty(tmsRouteList) && tmsRouteList.size()==1){
			  				  tmsInRoute = (String)tmsRouteList.get(0);
			  				  Map boothDetails = (Map)(ByProductNetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "subscriptionTypeId", subscriptionTypeId, "userLogin", userLogin))).get("boothDetails");
			  				  defaultRouteId = (String)boothDetails.get("routeId");
			  				  //if(UtilValidate.isNotEmpty(defaultRouteId) && !defaultRouteId.equalsIgnoreCase(tmsInRoute)){
			  					  subsidyShift = true;
			  				  //}
			  				  checkTMSHalfLtr = false;
			  			  }
	  	  			  }
  	  				  if(!subsidyShift && UtilValidate.isNotEmpty(tmsHalfLtrIndentList) && checkTMSHalfLtr){
	  	  				  empSubscription = EntityUtil.getFirst(tmsHalfLtrIndentList);
			  			  boothId = empSubscription.getString("facilityId");
			  			  subscriptionTypeId = empSubscription.getString("subscriptionTypeId");
			  			  tmsRouteList = EntityUtil.getFieldListFromEntityList(tmsHalfLtrIndentList, "sequenceNum", true);
			  			  if(UtilValidate.isNotEmpty(tmsRouteList) && tmsRouteList.size()==1){
			  				  tmsInRoute = (String)tmsRouteList.get(0);
			  				  Map boothDetails = (Map)(ByProductNetworkServices.getBoothRoute(dctx, UtilMisc.toMap("boothId", boothId, "subscriptionTypeId", subscriptionTypeId, "userLogin", userLogin))).get("boothDetails");
			  				  defaultRouteId = (String)boothDetails.get("routeId");
			  				  //if(UtilValidate.isNotEmpty(defaultRouteId) && !defaultRouteId.equalsIgnoreCase(tmsInRoute)){
			  					  subsidyShift = true;
			  				  //}  
			  			  }
  	  				  }
	  				  if(subsidyShift){
	  					  
	  					  List productQtyList = FastList.newInstance();
	  					  Map productQtyMap = FastMap.newInstance();
	  					  productQtyMap.put("productId", subsidyProduct);
	  		    		  productQtyMap.put("quantity", actualSubsidyQty);
	  		    		  productQtyMap.put("sequenceNum", tmsInRoute);
	  		    		  productQtyList.add(productQtyMap);
		  					Map inputMap = FastMap.newInstance();
		  					inputMap.put("subscriptionId", subscriptionId);
		  					inputMap.put("productSubscriptionTypeId", "EMP_SUBSIDY");
		  					inputMap.put("boothId", boothId);
		  					inputMap.put("shipmentTypeId", shipmentTypeId);
		  					inputMap.put("effectiveDate", effectiveDate);
		  					inputMap.put("routeChangeFlag", "Y");
		  					inputMap.put("enableSECA", "Y");
		  					inputMap.put("productQtyList", productQtyList);
		  					inputMap.put("userLogin", userLogin);
		  					Map resultCtx = dispatcher.runSync("processChangeIndentHelper",inputMap);
		  					if (ServiceUtil.isError(resultCtx)) {
		  						Debug.logError("Error in service processChangeIndentHelper", module);
		  						return ServiceUtil.returnError("Error in service processChangeIndentHelper");
		  					}
	  					  
		  					List tempList = FastList.newInstance();
		  					Map prodQtyMap = FastMap.newInstance();
		  					prodQtyMap.put("productId", subsidyProduct);
		  					prodQtyMap.put("quantity", actualSubsidyQty);
		  					prodQtyMap.put("sequenceNum", defaultRouteId);
		  					tempList.add(prodQtyMap);
		  					
		  					Timestamp nxtDay = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(effectiveDate, 1));  
		  					inputMap = FastMap.newInstance();
		  					inputMap.put("subscriptionId", subscriptionId);
		  					inputMap.put("productSubscriptionTypeId", "EMP_SUBSIDY");
		  					inputMap.put("boothId", boothId);
		  					inputMap.put("shipmentTypeId", shipmentTypeId);
		  					inputMap.put("effectiveDate", nxtDay);
		  					inputMap.put("enableSECA", "Y");
		  					//inputMap.put("routeChangeFlag", "Y");
		  					inputMap.put("productQtyList", tempList);
		  					inputMap.put("userLogin", userLogin);
		  					resultCtx = dispatcher.runSync("processChangeIndentHelper",inputMap);
		  					if (ServiceUtil.isError(resultCtx)) {
		  						Debug.logError("Error in service processChangeIndentHelper, while adjusting subsidy to next day permanent route", module);
		  						return ServiceUtil.returnError("Error in service processChangeIndentHelper, while adjusting subsidy to next day permanent route");
		  					}
	  				  	}
		  			}
	  	  		 }
  	      }catch (Exception e) {
  			  Debug.logError(e, "Problem changing employee subsidy route", module);		  
  			  return ServiceUtil.returnError("Problem changing employee subsidy route");			  
  		  }
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
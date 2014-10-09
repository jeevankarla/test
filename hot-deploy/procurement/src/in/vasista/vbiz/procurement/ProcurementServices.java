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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.stream.StreamSource;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import java.util.Random;
import java.util.Map.Entry;

import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapComparator;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
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
import org.ofbiz.webapp.view.ApacheFopWorker;
import org.ofbiz.widget.fo.FoScreenRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.ofbiz.widget.screen.ScreenRenderer;
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

import org.ofbiz.party.party.PartyHelper;
import org.xml.sax.SAXException;
/**
 * Procurement Services
 */
public class ProcurementServices {

	public static final String module = ProcurementServices.class.getName();
	public static final String resource = "CommonUiLabels";
	protected static final HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected static final FoScreenRenderer foScreenRenderer = new FoScreenRenderer();
	
	public static final Map<String, Object> createProcurementEntryAjax(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> resultMap = FastMap.newInstance();
        

        try{            	
        	resultMap = dispatcher.runSync("createProcurementEntry",context);
    		if (ServiceUtil.isError(resultMap)) {
    			
                Debug.logWarning("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap), module);
                
        		return ServiceUtil.returnError("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap));          	            
            }       		
    		//GenericValue recentEntry = delegator.findOne("OrderItem", resultMap ,true);
    		//resultMap.put("orderItem", recentEntry);
        }catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("There was an error while updateing   the ProcurementEntry: " + e.toString());
        }		  
        
         //Map orderItem = procurementEntryJson((GenericValue)resultMap.get("orderItem"));
        return resultMap;
   }
	
	
	public static Map<String, Object> createProcurementEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	 LocalDispatcher dispatcher = dctx.getDispatcher();
         Delegator delegator = dctx.getDelegator();
         String productId = (String) context.get("productId");
         String shedCode = (String) context.get("shedCode");
         String centerCode = (String) context.get("centerCode");
         String unitCode = (String) context.get("unitCode");
         Timestamp estimatedDeliveryDate = (Timestamp) context.get("orderDate");
         String supplyTypeEnumId = (String) context.get("purchaseTime");
         BigDecimal quantity = (BigDecimal) context.get("quantity");
         BigDecimal quantityKgs = BigDecimal.ZERO;
         BigDecimal  quantityLtrs= BigDecimal.ZERO;
         BigDecimal fatQty = (BigDecimal) context.get("fat");
         BigDecimal snfQty = (BigDecimal) context.get("snf");
         BigDecimal sQuantity = (BigDecimal) context.get("sQuantity"); 
         BigDecimal sQtyKgs = (BigDecimal) context.get("sQtyKgs");
         BigDecimal sFatQty = (BigDecimal) context.get("sFat"); 
         BigDecimal cQuantity = (BigDecimal) context.get("cQuantity");
         String ptcMilkType = (String) context.get("ptcMilkType");
         BigDecimal ptcQuantity = (BigDecimal) context.get("ptcQuantity");
         BigDecimal lactoReading = (BigDecimal)context.get("lactoReading");
         Locale locale = (Locale) context.get("locale");
         String currencyUomId = (String)context.get("currencyUomId");
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         Map<String, Object> resultMap = FastMap.newInstance();
         String productStoreId="";
         String salesChannel = (String) context.get("salesChannel");
         
         Map tenantConfigMap = FastMap.newInstance();
         try{
    		 List<GenericValue> tenantConfigList = delegator.findList("TenantConfiguration",EntityCondition.makeCondition("propertyTypeEnumId",EntityOperator.EQUALS,"MILK_PROCUREMENT"),null,null,null,false);
    		 for(GenericValue tenantConfig : tenantConfigList){
    			 tenantConfigMap.put(tenantConfig.getString("propertyName"), tenantConfig.getString("propertyValue")); 
    		 }
    	 }catch(GenericEntityException e){
    		 Debug.logError("Error while getting Tenant Configuration ::"+e.getMessage(), module);
    	 }
         if(UtilValidate.isEmpty(salesChannel)){
        	 salesChannel = "PRODUC_SALES_CHANNEL";
        	 
         }
         if(UtilValidate.isNotEmpty(sQtyKgs)){
         	sQuantity = ProcurementNetworkServices.convertKGToLitreSetScale(sQtyKgs, false);
         }
         
         if(UtilValidate.isNotEmpty(context.get("qtyLtrs"))){
        	 quantityLtrs = (BigDecimal)context.get("qtyLtrs");
        	 quantity = ProcurementNetworkServices.convertLitresToKG((BigDecimal)context.get("qtyLtrs"));
         }
         quantityKgs = quantity;
         
         String purposeTypeId = "MILK_PROCUREMENT";
         String facilityId = null;
         String partyId ="";       
         GenericValue facility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, context)).get("agentFacility");
    	 if(UtilValidate.isEmpty(facility)){
    		 Debug.logError("Agent Not found with Code ==>"+centerCode+" and with the unitCode :"+unitCode, module);
      		return ServiceUtil.returnError("Agent Not found with Id ==>"+centerCode+" and with the unitCode :"+unitCode);        	
    		
    	}
         partyId = facility.getString("ownerPartyId");
         facilityId = facility.getString("facilityId");
         // checking for active facilities
         boolean isActive = EntityUtil.isValueActive(facility, estimatedDeliveryDate,	"openedDate", "closedDate");
         if (!isActive) {
				Debug.logError("facility is not active: Please contact administrator", module);
				return ServiceUtil.returnError("facility is not active: ");
			}
         String categoryTypeEnum = facility.getString("categoryTypeEnum");
         // here we are trying to get facility wise tenant configurations
         try{
    		 Map getFacilityTenantConfig = dispatcher.runSync("getFacilityTenantConfigurations",UtilMisc.toMap("userLogin", userLogin, "facilityId", facilityId));
    		 if(ServiceUtil.isSuccess(getFacilityTenantConfig)){
    			 Map facilityTenantConfig = (Map)getFacilityTenantConfig.get("tenantConfigurationsMap");
    			 if(UtilValidate.isNotEmpty(facilityTenantConfig)){
    				 Set<String> keys = facilityTenantConfig.keySet();
    				 for(String key : keys){	
        				 tenantConfigMap.put(key,facilityTenantConfig.get(key));
        			 }
    			 }
    		 }
    	 }catch (GenericServiceException e) {
			// TODO: handle exception
    		 Debug.logError("Error while getting facilityWise Configurations"+e, module);
    	 }
         
    	 if(quantityLtrs.compareTo(BigDecimal.ZERO)==0){
        	 quantityLtrs = ProcurementNetworkServices.convertKGToLitreSetScale(quantity,true);
        	 String setScaleConvertKgToLtr = "Y";
        	 if(UtilValidate.isNotEmpty(tenantConfigMap)){
        		 setScaleConvertKgToLtr = (String)tenantConfigMap.get("enableConvertKgToLtrSetScale");
        	 }
        	 if(UtilValidate.isNotEmpty(setScaleConvertKgToLtr) &&("N".equalsIgnoreCase(setScaleConvertKgToLtr))){
        		 quantityLtrs = ProcurementNetworkServices.convertKGToLitreSetScale(quantity,false);
        	 }
         }
    	 
         // calculation of Snf from Lacto reading
         Map<String, Object> priceResult;     	
         Map<String, Object> priceContext = FastMap.newInstance();
         priceContext.put("userLogin", userLogin);                
         priceContext.put("productId", productId);
         priceContext.put("facilityId", facilityId);
         priceContext.put("priceDate", estimatedDeliveryDate );
         priceContext.put("categoryTypeEnum", categoryTypeEnum);
         priceContext.put("supplyTypeEnumId", supplyTypeEnumId);
         priceContext.put("fatPercent", fatQty);                        			
         if(UtilValidate.isNotEmpty(lactoReading)){
        	 priceContext.put("snfPercent",BigDecimal.ZERO);
        	 priceResult = PriceServices.getProcurementProductPrice(dctx, priceContext);
             if (ServiceUtil.isError(priceResult)) {
                 Debug.logWarning("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
         		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
             } 
            String uomId =(String) priceResult.get("uomId");
            String acctgFormulaId = (String) priceResult.get("lrFormulaId");
         // use accounting formula if exists
        	Map inputMap  = FastMap.newInstance();
        	inputMap.put("userLogin", userLogin);
        	inputMap.put("fatQty", fatQty);
        	inputMap.put("lactoReading", lactoReading);
        	inputMap.put("acctgFormulaId", acctgFormulaId);
        	Map snfFromLR = ProcurementNetworkServices.getSnfFromLactoReading(dctx,inputMap);
        	snfQty = (BigDecimal)snfFromLR.get("snfQty");
        	 
         }else{
        	 if(UtilValidate.isNotEmpty(fatQty) && UtilValidate.isNotEmpty(snfQty)){
        		 lactoReading = ProcurementNetworkServices.convertFatSnfToLR(fatQty, snfQty);
        	 }
         }
         
         try {       
         	GenericValue product=delegator.findOne("Product",UtilMisc.toMap("productId", productId), false);
         	List<GenericValue> prodCatalogCategoryList = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, product.getString("primaryProductCategoryId")), null, null, null, false);
         	List<GenericValue> productStoreCatalogList = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, (String)prodCatalogCategoryList.get(0).getString("prodCatalogId")), null, null, null, false);
         	productStoreId= (String)productStoreCatalogList.get(0).getString("productStoreId");
         } catch (GenericEntityException e) {
             Debug.logError(e, "Problem getting product store Id", module);
     		return ServiceUtil.returnError("Problem getting product store Id: " + e);          	
             
         }
         //productStoreId= "SMPL";
     	 GenericValue customTimePeriod = null;
     	 GenericValue orderDetail = null;
     	List conditionList =FastList.newInstance();
     	Map timePeriodResultMap = FastMap.newInstance();
     	try{
     		Map checkTimeperiodInMap = FastMap.newInstance();
     		checkTimeperiodInMap.put("userLogin", userLogin);
     		checkTimeperiodInMap.put("facilityId", facilityId);
     		checkTimeperiodInMap.put("fromDate", estimatedDeliveryDate);
     		timePeriodResultMap  = dispatcher.runSync("getFacilityCustomTimePeriod", checkTimeperiodInMap);
     		if(ServiceUtil.isError(timePeriodResultMap)){
     		  Debug.logError( "There no active billing time periods for . "+estimatedDeliveryDate, module);				 
  			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");     			
     		
     		}
     	   customTimePeriod = (GenericValue)timePeriodResultMap.get("customTimePeriod");
     	   //code for checking unit is associate with customTimePeriodId
     	   String customTimePeriodId= customTimePeriod.getString("customTimePeriodId");
     	   GenericValue unitDetails =  (GenericValue)(ProcurementNetworkServices.getCenterDtails(dctx,UtilMisc.toMap("centerId",facility.get("facilityId")))).get("unitFacility");
     	   String unitId= unitDetails.getString("facilityId");
     	   String unitName=unitDetails.getString("facilityName");
     	   GenericValue facilityCustomTimePeriod = delegator.findOne("FacilityCustomTimePeriod", UtilMisc.toMap("facilityId", unitId,"customTimePeriodId",customTimePeriodId), false);
		   if(UtilValidate.isEmpty(facilityCustomTimePeriod)){
			   Debug.logError("Please Associate CustomTimePeriod=="+customTimePeriodId+" with This Unit=="+unitName, module);
	   	         return ServiceUtil.returnError("Please Associate CustomTimePeriod=="+customTimePeriodId+" with This Unit=="+unitName);
		   }
		   conditionList.clear();
	       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
	       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		   conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
		   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"))) ));
		   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   List orderList = delegator.findList("OrderHeader", orderCondition, null, null, null,false);
		   orderDetail = EntityUtil.getFirst(orderList);
		   
		   
     	 }catch (GenericEntityException e) {
 			// TODO: handle exception
      		 Debug.logError(e.getMessage(), module);
      		 return ServiceUtil.returnError(e.getMessage());
 		}catch (Exception e) {
			// TODO: handle exception
 			Debug.logError(e.getMessage(), module);
     		
 			return ServiceUtil.returnError(e.getMessage());
		}
     	Map periodValidationMap = FastMap.newInstance();
     	Map checkProcPeriodBillingInMap = FastMap.newInstance();
     	checkProcPeriodBillingInMap.put("customTimePeriodId", customTimePeriod.get("customTimePeriodId"));
     	checkProcPeriodBillingInMap.put("userLogin", userLogin);
     	checkProcPeriodBillingInMap.put("facilityId", facilityId);

     	periodValidationMap = checkProcPeriodBilling(dctx,checkProcPeriodBillingInMap);
     	if(ServiceUtil.isError(periodValidationMap)){
     		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(periodValidationMap));
     	}
     	String orderId = null;
     	// calculate Product price here 
     	priceContext.put("snfPercent", snfQty);                        			
		priceResult = PriceServices.getProcurementProductPrice(dctx, priceContext);              			
        if (ServiceUtil.isError(priceResult)) {
            Debug.logWarning("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
    		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
        } 
        String uomId =(String) priceResult.get("uomId");
        if(UtilValidate.isNotEmpty(uomId)&&( uomId.equalsIgnoreCase("VLIQ_L"))){
        	quantity = quantityLtrs;
        }
        BigDecimal productPrice = (BigDecimal)priceResult.get("price");
        BigDecimal unitPremiumPrice = (BigDecimal)priceResult.get("premium");
        BigDecimal sUnitPrice = (BigDecimal)priceResult.get("sourRate"); 
        //BigDecimal unitPremiumPrice = (BigDecimal)priceResult.get("unitPremiumPrice");        
        if(UtilValidate.isEmpty(orderDetail)){     		 
     		 ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
             cart.setOrderType("PURCHASE_ORDER");
             cart.setFacilityId(facilityId);
             cart.setOrderDate(estimatedDeliveryDate);
             cart.setProductStoreId(productStoreId);             
             cart.setBillToCustomerPartyId(cart.getBillFromVendorPartyId()); //Company
             cart.setBillFromVendorPartyId(partyId);
             cart.setOrderPartyId(partyId);  
             cart.setPurposeTypeId(purposeTypeId);
             try {
                 cart.setUserLogin(userLogin, dispatcher);
             } catch (Exception exc) {
                 Debug.logWarning("Error setting userLogin in the cart: " + exc.getMessage(), module);
         		return ServiceUtil.returnError("Error setting userLogin in the cart: " + exc.getMessage());          	            
             } 
             try { 
            	 ShoppingCartItem cartItem = ShoppingCartItem.makeItem(Integer.valueOf(0),productId, null, 
          				quantity, productPrice,
                          null, null, null, null, null, null, null,
                          null, null, null, null, null, null, dispatcher,
                          cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE);
            	cartItem.setLactoReading(lactoReading);
            	cartItem.setQuantityKgs(quantityKgs);
            	cartItem.setQuantityLtrs(quantityLtrs);
            	cartItem.setSnf(snfQty);
     			cartItem.setFat(fatQty);
     			cartItem.setEstimatedDeliveryDate(estimatedDeliveryDate);
    			cartItem.setSupplyTypeEnumId(supplyTypeEnumId);
    			cartItem.setUnitPremiumPrice(unitPremiumPrice);
    			cartItem.setSQuantityLtrs(sQuantity);
    			cartItem.setSUnitPrice(sUnitPrice);
    			cartItem.setSFat(sFatQty);
    			cartItem.setCQuantityLtrs(cQuantity);
    			//**************       need to handle sour milk entry here    **************
     			//cartItem.setIsSour();
                cart.addItem(0, cartItem);
             } catch (Exception exc) {
                 Debug.logWarning("Error adding product with id " + productId+ " to the cart: " + exc.getMessage(), module);
         		return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: " + exc.getMessage());          	            
             }
             cart.setDefaultCheckoutOptions(dispatcher);
             CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart, true);
             Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
             orderId = (String) orderCreateResult.get("orderId");
             Map tempOrderItemMap = FastMap.newInstance();
             try{
            	 tempOrderItemMap.putAll(EntityUtil.getFirst((delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId" ,orderId))))); 
             }catch (Exception e) {
				// TODO: handle exception
			}
             //Debug.log("orderCreateResult========"+tempOrderItemMap);	
	        tempOrderItemMap.putAll(context);
	        resultMap.put("orderItem", tempOrderItemMap);
     		 
     	 }else{
     		orderId = orderDetail.getString("orderId");
     		Timestamp dayBegin =  UtilDateTime.getDayStart(estimatedDeliveryDate);
     		Timestamp dayEnd =  UtilDateTime.getDayEnd(estimatedDeliveryDate);
     		conditionList.clear(); 
     		// avoid duplicate entry
     		conditionList = UtilMisc.toList(
					  EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
	   		conditionList.add(EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS,supplyTypeEnumId));
	   		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_CREATED"));
	   		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	   		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	   		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));				  
			EntityCondition CustCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> orderItemList = FastList.newInstance();
			try{
				orderItemList =delegator.findList("OrderItem", CustCondition, null, null, null,false);	
			}catch (Exception e) {
				// TODO: handle exception
				 Debug.logError( e.getMessage(), module);				 
				 return ServiceUtil.returnError(e.getMessage());
			}
			// lets check the tenant configuration for  to enable multiple entries
			Boolean enableMultipleProcEntries = Boolean.FALSE;
			if(UtilValidate.isNotEmpty(tenantConfigMap)&&(UtilValidate.isNotEmpty(tenantConfigMap.get("enableMultipleProcEntries")))&&("Y".equalsIgnoreCase((String)tenantConfigMap.get("enableMultipleProcEntries")))){
	           		enableMultipleProcEntries = Boolean.TRUE;
	       	}
			// lets comment the duplicate functionality for demo purpose 
			if(!enableMultipleProcEntries){
				if(!UtilValidate.isEmpty(orderItemList)){
					  Debug.logError( "There another  active record for the same day. ", module);
					  return ServiceUtil.returnError("There another  active record for the same day. please edit exiting record.");
				 }  
			}else{
				for(GenericValue orederItemEntry : orderItemList){
					BigDecimal tempFat = orederItemEntry.getBigDecimal("fat");
					BigDecimal tempSnf = orederItemEntry.getBigDecimal("snf");
					BigDecimal tempLactoReading =  ProcurementNetworkServices.convertFatSnfToLR(tempFat, tempSnf);
					if(UtilValidate.isNotEmpty(lactoReading) && tempFat.compareTo(fatQty)==0 && tempLactoReading.compareTo(lactoReading) == 0){
						Debug.logError( "There another  active record for the same day. ", module);
						return ServiceUtil.returnError("There another  active record for the same day. please edit exiting record.");
					}
				}
			}
	        GenericValue newOrderItemMap = delegator.makeValue("OrderItem");
	        newOrderItemMap.put("orderId", orderId);
	        newOrderItemMap.put("orderItemTypeId", "PRODUCT_ORDER_ITEM");
	        newOrderItemMap.put("productId", productId);
	        newOrderItemMap.put("quantity", quantity);
	        newOrderItemMap.put("quantityLtrs", quantityLtrs);
	        newOrderItemMap.put("quantityKgs", quantityKgs);
	        newOrderItemMap.put("unitPrice", productPrice);
	        newOrderItemMap.put("statusId", "ITEM_CREATED");
	        newOrderItemMap.put("lactoReading", lactoReading);
	        newOrderItemMap.put("fat",fatQty );
	        newOrderItemMap.put("snf", snfQty);
	        newOrderItemMap.put("estimatedDeliveryDate",estimatedDeliveryDate );
	        newOrderItemMap.put("supplyTypeEnumId", supplyTypeEnumId);
	        newOrderItemMap.put("unitPremiumPrice",unitPremiumPrice );
	        newOrderItemMap.put("sQuantityLtrs", sQuantity);
	        newOrderItemMap.put("sFat",sFatQty );
	        newOrderItemMap.put("sUnitPrice",sUnitPrice );
	        newOrderItemMap.put("cQuantityLtrs", cQuantity);
	        newOrderItemMap.put("isPromo", "N");
	        newOrderItemMap.put("ptcMilkType",ptcMilkType );	    
	        newOrderItemMap.put("ptcQuantity", ptcQuantity);	             
	        newOrderItemMap.put("isModifiedPrice", "N"); 
	        newOrderItemMap.put("changeByUserLoginId", userLogin.get("userLoginId"));
	        newOrderItemMap.put("changeDatetime", UtilDateTime.nowTimestamp());
	        newOrderItemMap.put("createdByUserLoginId", userLogin.get("userLoginId"));
	        newOrderItemMap.put("createdDate", UtilDateTime.nowTimestamp());	
	        try {		
	        	delegator.setNextSubSeqId(newOrderItemMap, "orderItemSeqId", 5, 1);
	        	delegator.create(newOrderItemMap);
	        	Map tempOrderItemMap = FastMap.newInstance();
	        	tempOrderItemMap.putAll(newOrderItemMap);
	        	tempOrderItemMap.putAll(context);
	        	resultMap.put("orderItem", tempOrderItemMap);
	        } catch (Exception e) {
	        	Debug.logError("", module);
	            return ServiceUtil.returnError(e.getMessage());
	        }		  
     		 
     	 }
     	
     	 resultMap.put("orderId", orderId);
     	 return resultMap;
    }

	public static Map<String, Object> createProcurementSADFEntryAjax(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> resultMap = FastMap.newInstance();
        try{            	
        	resultMap = dispatcher.runSync("createWeighingEntry",context);
    		if (ServiceUtil.isError(resultMap)) {
    			
                Debug.logWarning("There was an error while updating   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap), module);
                
        		return ServiceUtil.returnError("There was an error while updating   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap));          	            
            }       		
        }catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("There was an error while updating   the ProcurementEntry: " + e.toString());
        }        
        return resultMap;
   }
	
	public static Map<String, Object> createWeighingEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String productId = (String) context.get("productId");
        String shedCode = (String) context.get("shedCode");
        String canCode = (String) context.get("canCode");
        String centerCode = (String) context.get("centerCode");
        String unitCode = (String) context.get("unitCode");
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("orderDate");
        String supplyTypeEnumId = (String) context.get("purchaseTime");
        BigDecimal quantity = (BigDecimal) context.get("quantity");
        BigDecimal quantityKgs = BigDecimal.ZERO;
        BigDecimal  quantityLtrs= BigDecimal.ZERO;     
        Locale locale = (Locale) context.get("locale");
        String currencyUomId = (String)context.get("currencyUomId");
        String exOrderId = (String) context.get("orderId");
		String exOrderItemSeqId = (String) context.get("orderItemSeqId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> resultMap = FastMap.newInstance();
        String productStoreId="";
        String salesChannel = (String) context.get("salesChannel");
        
        Map tenantConfigMap = FastMap.newInstance();
        try{
   		 List<GenericValue> tenantConfigList = delegator.findList("TenantConfiguration",EntityCondition.makeCondition("propertyTypeEnumId",EntityOperator.EQUALS,"MILK_PROCUREMENT"),null,null,null,false);
   		 for(GenericValue tenantConfig : tenantConfigList){
   			 tenantConfigMap.put(tenantConfig.getString("propertyName"), tenantConfig.getString("propertyValue")); 
   		 }
   		 
   	 }catch(GenericEntityException e){
   		 Debug.logError("Error while getting Tenant Configuration ::"+e.getMessage(), module);
   	 }
        if(UtilValidate.isEmpty(salesChannel)){
       	 salesChannel = "PRODUC_SALES_CHANNEL";
       	 
        }        
        if(UtilValidate.isNotEmpty(context.get("qtyLtrs"))){
       	 quantityLtrs = (BigDecimal)context.get("qtyLtrs");
       	 quantity = ProcurementNetworkServices.convertLitresToKG((BigDecimal)context.get("qtyLtrs"));
        }
        quantityKgs = quantity;
        
        String purposeTypeId = "MILK_PROCUREMENT";
        GenericValue facility = null;
        String partyId ="";
        String facilityId =null;  
        if(UtilValidate.isNotEmpty(canCode)){
        	Map canCodeResult = ProcurementNetworkServices.getFacilityByCanCode(dctx, context);
        	if (ServiceUtil.isError(canCodeResult)) {
	               Debug.logWarning(ServiceUtil.getErrorMessage(canCodeResult), module);
	       		return canCodeResult;          	            
	           } 
        	facility = (GenericValue)canCodeResult.get("facility");
        }else{
        	 facility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, context)).get("agentFacility");
        }
	        if(UtilValidate.isEmpty(facility)){
	      		 Debug.logError("Agent Not found with Code ==>"+canCode+" and with the unitCode :"+unitCode, module);
	        		return ServiceUtil.returnError("Agent Not found with Id ==>"+canCode+" and with the unitCode :"+unitCode);        	
	      	 	}
	           partyId = facility.getString("ownerPartyId");
	           facilityId = facility.getString("facilityId");
	           // checking for active facilities
	           //Map faclityResult = NetworkServices.isFacilityAcitve(dctx, UtilMisc.toMap("userLogin", userLogin, "facilityId", facilityId,"fromDate",estimatedDeliveryDate));
	           boolean isActive = EntityUtil.isValueActive(facility, estimatedDeliveryDate,	"openedDate", "closedDate");
	           if (!isActive) {
	  				Debug.logError("facility is not active: Please contact administrator", module);
	  				return ServiceUtil.returnError("facility is not active: ");
	  			}
	           
	           /*if (ServiceUtil.isError(faclityResult)) {
	               Debug.logWarning("facility is not active: " + ServiceUtil.getErrorMessage(faclityResult), module);
	       		return ServiceUtil.returnError("facility is not active: " + ServiceUtil.getErrorMessage(faclityResult));          	            
	           }*/ 
	           String categoryTypeEnum = facility.getString("categoryTypeEnum");
        
        // here we are trying to get facility wise tenant configurations
        try{
   		 Map getFacilityTenantConfig = dispatcher.runSync("getFacilityTenantConfigurations",UtilMisc.toMap("userLogin", userLogin, "facilityId", facilityId));
   		 if(ServiceUtil.isSuccess(getFacilityTenantConfig)){
   			 Map facilityTenantConfig = (Map)getFacilityTenantConfig.get("tenantConfigurationsMap");
   			 if(UtilValidate.isNotEmpty(facilityTenantConfig)){
   				 Set<String> keys = facilityTenantConfig.keySet();
   				 for(String key : keys){	
       				 tenantConfigMap.put(key,facilityTenantConfig.get(key));
       			 }
   			 }
   		 }
   	 }catch (GenericServiceException e) {
			// TODO: handle exception
   		 Debug.logError("Error while getting facilityWise Configurations"+e, module);
   	 }
   	if(quantityLtrs.compareTo(BigDecimal.ZERO)==0){
      	 quantityLtrs = ProcurementNetworkServices.convertKGToLitreSetScale(quantity,true);
      	 String setScaleConvertKgToLtr = "Y";
      	 if(UtilValidate.isNotEmpty(tenantConfigMap)){
      		 setScaleConvertKgToLtr = (String)tenantConfigMap.get("enableConvertKgToLtrSetScale");
      	 }
      	 if(UtilValidate.isNotEmpty(setScaleConvertKgToLtr) &&("N".equalsIgnoreCase(setScaleConvertKgToLtr))){
      		 quantityLtrs = ProcurementNetworkServices.convertKGToLitreSetScale(quantity,false);
      	 }
       }   	
        
        try {       
        	GenericValue product=delegator.findOne("Product",UtilMisc.toMap("productId", productId), false);
        	List<GenericValue> prodCatalogCategoryList = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, product.getString("primaryProductCategoryId")), null, null, null, false);
        	List<GenericValue> productStoreCatalogList = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, (String)prodCatalogCategoryList.get(0).getString("prodCatalogId")), null, null, null, false);
        	productStoreId= (String)productStoreCatalogList.get(0).getString("productStoreId");
	       
        	// check for  existing entry and update the quantity 
	  		 if(UtilValidate.isNotEmpty(exOrderId) && UtilValidate.isNotEmpty(exOrderItemSeqId)){
	  			GenericValue orderItem = delegator.findOne("OrderItem",UtilMisc.toMap("orderId", exOrderId , "orderItemSeqId",exOrderItemSeqId), false);
	     		 if(UtilValidate.isNotEmpty(orderItem)){
	     			orderItem.put("productId", productId);
	     			orderItem.put("quantity", quantity);
	     			orderItem.put("quantityLtrs", quantityLtrs);
	     			orderItem.put("quantityKgs", quantityKgs);
	     			orderItem.store();
	     			resultMap.put("orderItem", orderItem);
	     			resultMap.put("orderId", exOrderId);
	     	    	return resultMap;
	     		 }
	  		 }
        
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting product store Id", module);
    		return ServiceUtil.returnError("Problem getting product store Id: " + e);          	
            
        } 
    	 GenericValue customTimePeriod = null;
    	 GenericValue orderDetail = null;
    	List conditionList =FastList.newInstance();
    	Map timePeriodResultMap = FastMap.newInstance();
    	try{
    		Map checkTimeperiodInMap = FastMap.newInstance();
    		checkTimeperiodInMap.put("userLogin", userLogin);
    		checkTimeperiodInMap.put("facilityId", facilityId);
    		checkTimeperiodInMap.put("fromDate", estimatedDeliveryDate);
    		timePeriodResultMap  = dispatcher.runSync("getFacilityCustomTimePeriod", checkTimeperiodInMap);
    		if(ServiceUtil.isError(timePeriodResultMap)){
    		  Debug.logError( "There no active billing time periods. ", module);				 
 			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
    			
    		}
    	   customTimePeriod = (GenericValue)timePeriodResultMap.get("customTimePeriod");
		   conditionList.clear();
	       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
	       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		   conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
		   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"))) ));
		   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   List orderList = delegator.findList("OrderHeader", orderCondition, null, null, null,false);
		   orderDetail = EntityUtil.getFirst(orderList);
		   
		   
    	 }catch (GenericEntityException e) {
			// TODO: handle exception
     		 Debug.logError(e.getMessage(), module);
     		 return ServiceUtil.returnError(e.getMessage());
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), module);
    		
			return ServiceUtil.returnError(e.getMessage());
		}
    	Map periodValidationMap = FastMap.newInstance();
    	Map checkProcPeriodBillingInMap = FastMap.newInstance();
    	checkProcPeriodBillingInMap.put("customTimePeriodId", customTimePeriod.get("customTimePeriodId"));
    	checkProcPeriodBillingInMap.put("userLogin", userLogin);
    	checkProcPeriodBillingInMap.put("facilityId", facilityId);

    	periodValidationMap = checkProcPeriodBilling(dctx,checkProcPeriodBillingInMap);
    	if(ServiceUtil.isError(periodValidationMap)){
    		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(periodValidationMap));
    	}
    	String orderId = null;    	     
       if(UtilValidate.isEmpty(orderDetail)){     		 
    		 ShoppingCart cart = new ShoppingCart(delegator, productStoreId, locale, currencyUomId);
            cart.setOrderType("PURCHASE_ORDER");
            cart.setFacilityId(facilityId);
            cart.setOrderDate(estimatedDeliveryDate);
            cart.setProductStoreId(productStoreId);             
            cart.setBillToCustomerPartyId(cart.getBillFromVendorPartyId()); //Company
            cart.setBillFromVendorPartyId(partyId);
            cart.setOrderPartyId(partyId);  
            cart.setPurposeTypeId(purposeTypeId);
            try {
                cart.setUserLogin(userLogin, dispatcher);
            } catch (Exception exc) {
                Debug.logWarning("Error setting userLogin in the cart: " + exc.getMessage(), module);
        		return ServiceUtil.returnError("Error setting userLogin in the cart: " + exc.getMessage());          	            
            } 
            try { 
           	 ShoppingCartItem cartItem = ShoppingCartItem.makeItem(Integer.valueOf(0),productId, null, 
         				quantity, null,
                         null, null, null, null, null, null, null,
                         null, null, null, null, null, null, dispatcher,
                         cart, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, Boolean.TRUE);
           	cartItem.setQuantityKgs(quantityKgs);
           	cartItem.setQuantityLtrs(quantityLtrs);
    		cartItem.setEstimatedDeliveryDate(estimatedDeliveryDate);
   			cartItem.setSupplyTypeEnumId(supplyTypeEnumId);   			
   			//**************       need to handle sour milk entry here    **************
    			//cartItem.setIsSour();
               cart.addItem(0, cartItem);
            } catch (Exception exc) {
                Debug.logWarning("Error adding product with id " + productId+ " to the cart: " + exc.getMessage(), module);
        		return ServiceUtil.returnError("Error adding product with id " + productId + " to the cart: " + exc.getMessage());          	            
            }
            cart.setDefaultCheckoutOptions(dispatcher);
            CheckOutHelper checkout = new CheckOutHelper(dispatcher, delegator, cart, true);
            Map<String, Object> orderCreateResult = checkout.createOrder(userLogin);
            orderId = (String) orderCreateResult.get("orderId");
            Map tempOrderItemMap = FastMap.newInstance();
            try{
           	 tempOrderItemMap.putAll(EntityUtil.getFirst((delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId" ,orderId))))); 
            }catch (Exception e) {
				// TODO: handle exception
			}
	        tempOrderItemMap.putAll(context);
	        resultMap.put("orderItem", tempOrderItemMap);
    		 
    	 }else{
    		orderId = orderDetail.getString("orderId");
    		Timestamp dayBegin =  UtilDateTime.getDayStart(estimatedDeliveryDate);
    		Timestamp dayEnd =  UtilDateTime.getDayEnd(estimatedDeliveryDate);
    		conditionList.clear(); 
    		// avoid duplicate entry
    		conditionList = UtilMisc.toList(
					  EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
	   		conditionList.add(EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS,supplyTypeEnumId));
	   		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_CREATED"));
	   		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	   		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	   		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));				  
			EntityCondition CustCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> orderItemList = FastList.newInstance();
			try{
				orderItemList =delegator.findList("OrderItem", CustCondition, null, null, null,false);	
			}catch (Exception e) {
				// TODO: handle exception
				 Debug.logError( e.getMessage(), module);				 
				 return ServiceUtil.returnError(e.getMessage());
			}
			// lets check the tenant configuration for  to enable multiple entries
			Boolean enableMultipleProcEntries = Boolean.FALSE;
			if(UtilValidate.isNotEmpty(tenantConfigMap)&&(UtilValidate.isNotEmpty(tenantConfigMap.get("enableMultipleProcEntries")))&&("Y".equalsIgnoreCase((String)tenantConfigMap.get("enableMultipleProcEntries")))){
	           		enableMultipleProcEntries = Boolean.TRUE;
	       	}
			// lets comment the duplicate functionality for demo purpose 
			if(!enableMultipleProcEntries){
				if(!UtilValidate.isEmpty(orderItemList)){
					  Debug.logError( "There another  active record for the same day. ", module);
					  return ServiceUtil.returnError("There another  active record for the same day. please edit exiting record.");
				 }  
			}else{
				/*for(GenericValue orederItemEntry : orderItemList){
					BigDecimal tempFat = orederItemEntry.getBigDecimal("fat");
					BigDecimal tempSnf = orederItemEntry.getBigDecimal("snf");
					BigDecimal tempLactoReading =  ProcurementNetworkServices.convertFatSnfToLR(tempFat, tempSnf);
					if(UtilValidate.isNotEmpty(lactoReading) && tempFat.compareTo(fatQty)==0 && tempLactoReading.compareTo(lactoReading) == 0){
						Debug.logError( "There another  active record for the same day. ", module);
						return ServiceUtil.returnError("There another  active record for the same day. please edit exiting record.");
					}
				}*/
			}
	        GenericValue newOrderItemMap = delegator.makeValue("OrderItem");
	        newOrderItemMap.put("orderId", orderId);
	        newOrderItemMap.put("orderItemTypeId", "PRODUCT_ORDER_ITEM");
	        newOrderItemMap.put("productId", productId);
	        newOrderItemMap.put("quantity", quantity);
	        newOrderItemMap.put("quantityLtrs", quantityLtrs);
	        newOrderItemMap.put("quantityKgs", quantityKgs);
	        newOrderItemMap.put("statusId", "ITEM_CREATED");
	        newOrderItemMap.put("estimatedDeliveryDate",estimatedDeliveryDate );
	        newOrderItemMap.put("supplyTypeEnumId", supplyTypeEnumId);	      
	        newOrderItemMap.put("isPromo", "N");	                   
	        newOrderItemMap.put("isModifiedPrice", "N"); 
	        newOrderItemMap.put("changeByUserLoginId", userLogin.get("userLoginId"));
	        newOrderItemMap.put("changeDatetime", UtilDateTime.nowTimestamp());
	        newOrderItemMap.put("createdByUserLoginId", userLogin.get("userLoginId"));
	        newOrderItemMap.put("createdDate", UtilDateTime.nowTimestamp());
	        newOrderItemMap.put("canCode", canCode);
	        try {		
	        	delegator.setNextSubSeqId(newOrderItemMap, "orderItemSeqId", 5, 1);
	        	delegator.create(newOrderItemMap);
	        	Map tempOrderItemMap = FastMap.newInstance();
	        	tempOrderItemMap.putAll(newOrderItemMap);
	        	tempOrderItemMap.putAll(context);
	        	resultMap.put("orderItem", tempOrderItemMap);
	        } catch (Exception e) {
	        	Debug.logError("", module);
	            return ServiceUtil.returnError(e.getMessage());
	        }		  
    		 
    	 }
    	
    	 resultMap.put("orderId", orderId);
    	 return resultMap;
   }
	
	// AJAX SERVICE FOR FETCH THE RECORD
	
	public static Map<String, Object> fetchProcurementRecordAjax(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String productId = (String) context.get("productId");
        String shedCode = (String) context.get("shedCode");
        String unitCode = (String) context.get("unitCode");
        String centerCode = (String) context.get("centerCode");
        String canCode = (String) context.get("canCode");
        Timestamp estimatedDeliveryDate = (Timestamp) context.get("orderDate");
        String supplyTypeEnumId = (String) context.get("purchaseTime");
        Map<String, Object> result = ServiceUtil.returnError("Record Not found");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String purposeTypeId = "MILK_PROCUREMENT";
        String facilityId = null;
        String partyId ="";
        GenericValue facility= null;
        if(UtilValidate.isNotEmpty(canCode)){
        	Map canCodeResult = ProcurementNetworkServices.getFacilityByCanCode(dctx, context);
        	if (ServiceUtil.isError(canCodeResult)) {
	               Debug.logWarning(ServiceUtil.getErrorMessage(canCodeResult), module);
	       		return canCodeResult;          	            
	           } 
        	facility = (GenericValue)canCodeResult.get("facility");
        }else{
        	 facility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, context)).get("agentFacility");
        }
   	 	if(UtilValidate.isEmpty(facility)){
   	 		Debug.logError("Agent Not found with Code ==>"+centerCode, module);
     		return ServiceUtil.returnError("Agent Not found with Id ==>"+centerCode);        	
   		
   	 	}
        partyId = facility.getString("ownerPartyId");
        facilityId = facility.getString("facilityId");
        String categoryTypeEnum = facility.getString("categoryTypeEnum");
    	GenericValue customTimePeriod = null;
    	GenericValue orderDetail = null;
    	List conditionList =FastList.newInstance();
    	Map timePeriodResultMap = FastMap.newInstance();
    	try{
    		Map checkTimeperiodInMap = FastMap.newInstance();
     		checkTimeperiodInMap.put("userLogin", userLogin);
     		checkTimeperiodInMap.put("facilityId", facilityId);
     		checkTimeperiodInMap.put("fromDate", estimatedDeliveryDate);
     		timePeriodResultMap  = dispatcher.runSync("getFacilityCustomTimePeriod", checkTimeperiodInMap);
     		if(ServiceUtil.isError(timePeriodResultMap)){
       		  Debug.logError( "There no active billing time periods. ", module);				 
    			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
       			
       		}
       	   	customTimePeriod = (GenericValue)timePeriodResultMap.get("customTimePeriod");
		    conditionList.clear();
		    conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
		    conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		    conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		    conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
		    conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"))) ));
		    EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    List orderList = delegator.findList("OrderHeader", orderCondition, null, null, null,false);
		    orderDetail = EntityUtil.getFirst(orderList);
    	 }catch (GenericEntityException e) {
			// TODO: handle exception
     		 Debug.logError(e.getMessage(), module);
     		return ServiceUtil.returnError(e.getMessage());
		}catch (Exception e){
			
			Debug.logError("Error while getting Custom Timeperiods",module);
			return ServiceUtil.returnError("Error while getting Custom Timeperiods :"+e.getMessage());
		}
    	 
    	String orderId = null;
        
    	orderId = orderDetail.getString("orderId");
 		Timestamp dayBegin =  UtilDateTime.getDayStart(estimatedDeliveryDate);
 		Timestamp dayEnd =  UtilDateTime.getDayEnd(estimatedDeliveryDate);
 		conditionList.clear(); 
 		// avoid duplicate entry
 		conditionList = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
   		conditionList.add(EntityCondition.makeCondition("supplyTypeEnumId", EntityOperator.EQUALS,supplyTypeEnumId));
   		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_CREATED"));
   		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	   	if(UtilValidate.isNotEmpty(canCode)){
	   		conditionList.add(EntityCondition.makeCondition("canCode", EntityOperator.EQUALS, canCode));
	   	}
   		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
   		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));				  
		EntityCondition CustCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		List orderItemList = FastList.newInstance();
		try{
			orderItemList =delegator.findList("OrderItem", CustCondition, null, null, null,false);	
		}catch (Exception e) {
			// TODO: handle exception
			 Debug.logError( e.getMessage(), module);				 
			 return ServiceUtil.returnError(e.getMessage());
		}	
        
		if(UtilValidate.isNotEmpty(orderItemList)){
			Map tempOrderItem = FastMap.newInstance();
			tempOrderItem = (Map) orderItemList.get(0);
			BigDecimal fat = (BigDecimal)tempOrderItem.get("fat");
			BigDecimal snf = (BigDecimal)tempOrderItem.get("snf");
			//BigDecimal lactoReading =  ProcurementNetworkServices.convertFatSnfToLR(fat, snf);
			result= ServiceUtil.returnSuccess("fetched the record");
			if(UtilValidate.isNotEmpty(tempOrderItem.get("sQuantityLtrs"))){
				result.put("sQtyKgs", ProcurementNetworkServices.convertLitresToKG((BigDecimal)tempOrderItem.get("sQuantityLtrs")));
			}
			result.put("orderItem",tempOrderItem);
		 }  
        return result;
	}
	
	
	
	/**
	 * Service for fetching the recent change by the user
	 * @param shedCode,unitCode
	 * @return orderItem
	 * 
	 */
	
	public static Map<String, Object> fetchRecentChangeAjax(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        String shedId = (String) context.get("shedId");
	        String unitId = (String) context.get("unitId");
	        String shedCode = null;
	        String unitCode = null;
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String facilityId = null;
	    	GenericValue orderItemDetails = null;
	    	GenericValue shedDetails = null;
	    	List conditionList =FastList.newInstance();
	    	Map orderItem = FastMap.newInstance();
	    	try{
	    		List agentList = FastList.newInstance();
	    		if(UtilValidate.isNotEmpty(shedId)){
		    		facilityId = shedId;
		    		if(UtilValidate.isNotEmpty(unitId)){
		    			facilityId = unitId;
		    		}
		    		agentList = (List)ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facilityId)).get("facilityIds");
	    		}
	    		Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -30));
	    		
    			List<GenericValue> procurementOrderList = FastList.newInstance();
    			conditionList.clear();
	 	       	conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
	 	       	conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
	 	       
	 	       	conditionList.add(EntityCondition.makeCondition("createdByUserLoginId", EntityOperator.EQUALS,(String)userLogin.get("userLoginId")));
	 	       	if(UtilValidate.isNotEmpty(agentList)){
	 	       		conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, agentList));
	 	       	}
	 	       	conditionList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	 	       	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_CREATED"));
	 	       	EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	 	       	procurementOrderList = delegator.findList("OrderHeaderItemProductAndFacility", orderCondition, null, null, null,false);
	 	       	if(UtilValidate.isEmpty(procurementOrderList)){
	 	       		result.put("orderItem", orderItem);
	 	       		return result;
	 	       	}
	 	       procurementOrderList = EntityUtil.orderBy(procurementOrderList, UtilMisc.toList("-changeDatetime"));
	 	       orderItem = EntityUtil.getFirst(procurementOrderList);
	 	       Map centerDetails = FastMap.newInstance();
	    	   centerDetails = ProcurementNetworkServices.getCenterDtails(dctx, UtilMisc.toMap("userLogin", userLogin,"centerId",(String)orderItem.get("originFacilityId")));
	    	   if(ServiceUtil.isSuccess(centerDetails)){
					Map unitDetails =(Map) centerDetails.get("unitFacility");
					unitCode = (String) unitDetails.get("facilityCode");
					Map shedDetailsMap =(Map) centerDetails.get("shedFacility");
					shedCode = (String) shedDetailsMap.get("facilityCode");
				}
	 	       Map tempOrderItem = FastMap.newInstance();
	 	       tempOrderItem.putAll(orderItem);
	 	       tempOrderItem.put("unitCode",unitCode);
	 	       tempOrderItem.put("shedCode",shedCode);
	 	       tempOrderItem.put("purchaseTime",orderItem.get("supplyTypeEnumId"));
	 	       tempOrderItem.put("sQtyKgs",BigDecimal.ZERO);
	 	       if(UtilValidate.isNotEmpty(orderItem.get("sQuantityLtrs"))){
	 	    	   tempOrderItem.put("sQtyKgs", ProcurementNetworkServices.convertLitresToKG((BigDecimal)orderItem.get("sQuantityLtrs")));
	 	       }
	 	       tempOrderItem.put("centerCode", orderItem.get("facilityCode"));
	 	       result.put("orderItem", tempOrderItem);
    		
    	}catch (Exception e) {
			// TODO: handle exception
    		Debug.logError("Error while getting orderItem"+e, module);
    		result = ServiceUtil.returnError("Error while getting orderItem"+e.getMessage());
		}
        return result;
	}
	
	public static Map<String, Object> updateProcurementEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		BigDecimal fatQty = (BigDecimal) context.get("fat");
		BigDecimal snfQty = (BigDecimal) context.get("snf");
		BigDecimal sQuantity = (BigDecimal) context.get("sQuantity");
		BigDecimal sQtyKgs = (BigDecimal) context.get("sQtyKgs");
		BigDecimal sFatQty = (BigDecimal) context.get("sFat");
		BigDecimal cQuantity = (BigDecimal) context.get("cQuantity");
		String ptcMilkType = (String) context.get("ptcMilkType");
		BigDecimal ptcQuantity = (BigDecimal) context.get("ptcQuantity");
		// this qtyLtrs is coming from procurementEntryScreen
		BigDecimal qtyLtrs = (BigDecimal) context.get("qtyLtrs");
		// quantityKgs , quantityLtrs are fields of OrderItem Entity
		BigDecimal quantityKgs = BigDecimal.ZERO;
        BigDecimal  quantityLtrs= BigDecimal.ZERO;
        BigDecimal lactoReading = (BigDecimal)context.get("lactoReading");
        Map tenantConfigMap = FastMap.newInstance();
        try{
	   		List<GenericValue> tenantConfigList = delegator.findList("TenantConfiguration",EntityCondition.makeCondition("propertyTypeEnumId",EntityOperator.EQUALS,"MILK_PROCUREMENT"),null,null,null,false);
	   		for(GenericValue tenantConfig : tenantConfigList){
	   			tenantConfigMap.put(tenantConfig.getString("propertyName"), tenantConfig.getString("propertyValue")); 
	   		}
	   	}catch(GenericEntityException e){
	   		Debug.logError("Error while getting Tenant Configuration ::"+e.getMessage(), module);
	   	}
        if(UtilValidate.isNotEmpty(context.get("quantityKgs"))){
        	quantityKgs =(BigDecimal) context.get("quantityKgs");
        	quantityKgs = quantityKgs.setScale(1, BigDecimal.ROUND_HALF_EVEN);
        }
        if(UtilValidate.isNotEmpty(context.get("quantityLtrs"))){
        	quantityLtrs =(BigDecimal) context.get("quantityLtrs");
        	quantity = ProcurementNetworkServices.convertLitresToKG(quantityLtrs);
        }
        
        if(UtilValidate.isNotEmpty(qtyLtrs)){
        	quantityLtrs = qtyLtrs;
       		quantity = ProcurementNetworkServices.convertLitresToKG(qtyLtrs);
        }
        if(UtilValidate.isNotEmpty(sQtyKgs)){
        	sQuantity = ProcurementNetworkServices.convertKGToLitreSetScale(sQtyKgs, false);
        }
        if(quantityKgs.compareTo(BigDecimal.ZERO)==0){
        	quantityKgs = quantity;
        }
        
        GenericValue orderItem = null;
		GenericValue order = null;
		try {
			orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId),false);
			order = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId", orderId), false);
			if (UtilValidate.isEmpty(orderItem)) {
				Debug.logError(" No entry found. ", module);
				return ServiceUtil.returnError("No entry found.");
			}
		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		try{
			Map getFacilityTenantConfig = dispatcher.runSync("getFacilityTenantConfigurations",UtilMisc.toMap("userLogin", userLogin, "facilityId", order.getString("originFacilityId")));
			if(ServiceUtil.isSuccess(getFacilityTenantConfig)){
   		 		Map facilityTenantConfig = (Map)getFacilityTenantConfig.get("tenantConfigurationsMap");
   		 		if(UtilValidate.isNotEmpty(facilityTenantConfig)){
   		 			Set<String> keys = facilityTenantConfig.keySet();
   		 			for(String key : keys){	
   		 				tenantConfigMap.put(key,facilityTenantConfig.get(key));
   		 			}
   		 		}
   		 	}
   	 	}catch (GenericServiceException e) {
			// TODO: handle exception
   	 		Debug.logError("Error while getting facilityWise Configurations"+e, module);
   	 	}
		if(quantityLtrs.compareTo(BigDecimal.ZERO)==0){
	       	 quantityLtrs = ProcurementNetworkServices.convertKGToLitreSetScale(quantity,true);
	       	 String setScaleConvertKgToLtr = "Y";
	       	 
	       	 if(UtilValidate.isNotEmpty(tenantConfigMap)){
	       		 setScaleConvertKgToLtr = (String)tenantConfigMap.get("enableConvertKgToLtrSetScale");
	       	 }
	       	 if(UtilValidate.isNotEmpty(setScaleConvertKgToLtr) &&("N".equalsIgnoreCase(setScaleConvertKgToLtr))){
	       		 quantityLtrs = ProcurementNetworkServices.convertKGToLitreSetScale(quantity,false);
	       	 }
       }
		if ((orderItem.getString("statusId")).equals("ITEM_APPROVED")) {
			Debug.logWarning(" you can't update entry.once  billing is generated. ", module);
			return ServiceUtil.returnError("you can't update entry.once  billing is generated.");
		}		
		
		GenericValue tempOrderItem = (GenericValue)orderItem.clone();
		
		Map<String, Object> priceResult = FastMap.newInstance();

		Map<String, Object> priceContext = FastMap.newInstance();
		priceContext.put("userLogin", userLogin);
		priceContext.put("productId", orderItem.getString("productId"));
		priceContext.put("facilityId", order.getString("originFacilityId"));
		priceContext.put("priceDate", orderItem.getTimestamp("estimatedDeliveryDate"));
		priceContext.put("supplyTypeEnumId", orderItem.getString("supplyTypeEnumId"));
		priceContext.put("fatPercent", fatQty);
		boolean enableLR = false;
        if(UtilValidate.isNotEmpty(tenantConfigMap)&&(UtilValidate.isNotEmpty(tenantConfigMap.get("enableLR")))&&("Y".equalsIgnoreCase((String)tenantConfigMap.get("enableLR")))){
         		enableLR = true;
    	 }
        boolean enableQuantityInLtrs = false;
        if(UtilValidate.isNotEmpty(tenantConfigMap)&&(UtilValidate.isNotEmpty(tenantConfigMap.get("enableQuantityInLtrs")))&&("Y".equalsIgnoreCase((String)tenantConfigMap.get("enableQuantityInLtrs")))){
        	enableQuantityInLtrs = true;
        }
        if(enableQuantityInLtrs && UtilValidate.isNotEmpty(quantityLtrs) && quantityLtrs.compareTo(BigDecimal.ZERO)!=0){
        	quantityKgs = ProcurementNetworkServices.convertLitresToKG(quantityLtrs);
        	quantity = ProcurementNetworkServices.convertLitresToKG(quantityLtrs);
        }
		if(enableLR && UtilValidate.isNotEmpty(lactoReading)){
			priceContext.put("snfPercent", BigDecimal.ZERO);
			priceResult = PriceServices.getProcurementProductPrice(dctx,priceContext);
			if (ServiceUtil.isError(priceResult)) {
				Debug.logWarning("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult), module);
				return ServiceUtil.returnError("There was an error while calculating the price: "
								+ ServiceUtil.getErrorMessage(priceResult));
			}
			String uomId =(String) priceResult.get("uomId");
			String acctgFormulaId = (String) priceResult.get("lrFormulaId");
	        // use accounting formula if exists
	       	Map inputMap  = FastMap.newInstance();
	       	inputMap.put("userLogin", userLogin);
	       	inputMap.put("fatQty", fatQty);
	       	inputMap.put("lactoReading", lactoReading);
	       	inputMap.put("acctgFormulaId", acctgFormulaId);
	       	Map snfFromLR = ProcurementNetworkServices.getSnfFromLactoReading(dctx,inputMap);
	       	snfQty = (BigDecimal)snfFromLR.get("snfQty");
		}
        priceContext.put("snfPercent", snfQty);
		priceResult = PriceServices.getProcurementProductPrice(dctx,priceContext);
        if (ServiceUtil.isError(priceResult)) {
			Debug.logWarning("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult), module);
			return ServiceUtil.returnError("There was an error while calculating the price: "
							+ ServiceUtil.getErrorMessage(priceResult));
		}
		BigDecimal productPrice = (BigDecimal) priceResult.get("price");
		BigDecimal unitPremiumPrice = (BigDecimal) priceResult.get("premium");
		BigDecimal sUnitPrice = (BigDecimal) priceResult.get("sourRate");

		try {
			tempOrderItem.set("sQuantityLtrs", sQuantity);
			tempOrderItem.set("cQuantityLtrs", cQuantity);
			tempOrderItem.set("sFat", sFatQty);
			tempOrderItem.set("ptcMilkType", ptcMilkType);
			tempOrderItem.set("ptcQuantity", ptcQuantity);
			tempOrderItem.set("quantity", quantity);
			tempOrderItem.set("quantityKgs", quantityKgs);
			tempOrderItem.set("quantityLtrs", quantityLtrs);
			tempOrderItem.set("fat", fatQty);
			tempOrderItem.set("snf", snfQty);
			tempOrderItem.set("lactoReading", lactoReading);
			tempOrderItem.set("unitPrice", productPrice);
			tempOrderItem.set("unitPremiumPrice", unitPremiumPrice);
			tempOrderItem.set("sUnitPrice", sUnitPrice);			
			
			if(tempOrderItem.compareTo(orderItem) != 0){
				tempOrderItem.put("changeByUserLoginId",userLogin.get("userLoginId"));
				tempOrderItem.put("changeDatetime", UtilDateTime.nowTimestamp());
				Debug.logInfo("====== update  tempOrderItem ========", module);
				delegator.store(tempOrderItem);				
			}		

		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> resultMap = ServiceUtil
				.returnSuccess("entry updated successfully for center ==>>"
						+ context.get("facilityCode"));
		Map updatedOrderItem = FastMap.newInstance();
		
		context.remove("orderId");
		context.remove("orderItemSeqId");
		updatedOrderItem.putAll(tempOrderItem);
		updatedOrderItem.putAll(context);
    	resultMap.put("orderItem", updatedOrderItem);
		resultMap.put("orderId", orderId);
		return resultMap;
		
	}	
	/**
	 * 
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateProcurementEntryRecord(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		Map<String, Object> resultMap = FastMap.newInstance();
		try{            	
			Map<String, Object> deleteProcrementRecordInMap = FastMap.newInstance();
			deleteProcrementRecordInMap.put("orderId", orderId);
			deleteProcrementRecordInMap.put("orderItemSeqId", orderItemSeqId);
			deleteProcrementRecordInMap.put("userLogin", userLogin);
			resultMap = dispatcher.runSync("deleteProcurementEntry",deleteProcrementRecordInMap);
			context.remove("orderId");
			context.remove("orderItemSeqId");
			if (ServiceUtil.isError(resultMap)) {
                Debug.logWarning("There was an error while deleting the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap), module);
        		return ServiceUtil.returnError("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap));          	            
            }
			resultMap.clear();
			resultMap = dispatcher.runSync("createProcurementEntry",context);
    		if (ServiceUtil.isError(resultMap)) {
    			
                Debug.logWarning("There was an error while updating   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap), module);
                
        		return ServiceUtil.returnError("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(resultMap));          	            
            }       		
    		//GenericValue recentEntry = delegator.findOne("OrderItem", resultMap ,true);
    		//resultMap.put("orderItem", recentEntry);
        }catch (GenericServiceException e) {
			// TODO: handle exception
        	Debug.logError("Error while creating new Record ===>"+e, module);
            return ServiceUtil.returnError("There was an error while updating   the ProcurementEntry: " + e.toString());
		}		  
        Map orderItem = (Map)resultMap.get("orderItem");
		resultMap = ServiceUtil.returnSuccess("Record Updated Successfully");
		resultMap.put("orderItem", orderItem);
		resultMap.put("orderId", orderItem.get("orderId"));
		
		return resultMap;
		
	}
	public static Map<String, Object> updateApproveBillingValidation(DispatchContext dctx, Map<String, ? extends Object> context) {		
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		BigDecimal fatQty = (BigDecimal) context.get("fat");
		BigDecimal snfQty = (BigDecimal) context.get("snf");
		BigDecimal sQuantity = (BigDecimal) context.get("sQuantity");
		BigDecimal sFatQty = (BigDecimal) context.get("sFat");
		BigDecimal cQuantity = (BigDecimal) context.get("cQuantity");
		String ptcMilkType = (String) context.get("ptcMilkType");
		BigDecimal ptcQuantity = (BigDecimal) context.get("ptcQuantity");
		
		String validationTypeId = (String) context.get("validationTypeId");
		String sequenceNum = (String) context.get("sequenceNum");
		String shedId = (String) context.get("shedId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");		
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> upDateEntryContext = FastMap.newInstance(); 		 
	 		upDateEntryContext.put("userLogin", userLogin);
	 		upDateEntryContext.put("orderId", orderId);	 		
	 		upDateEntryContext.put("orderItemSeqId", orderItemSeqId);
	 		upDateEntryContext.put("quantity", quantity);	 		
	 		upDateEntryContext.put("fat", fatQty);
	 		upDateEntryContext.put("snf", snfQty);	 		
	 		upDateEntryContext.put("sQuantity", sQuantity);
	 		upDateEntryContext.put("sFat", sFatQty);	 		
	 		upDateEntryContext.put("cQuantity", cQuantity);
	 		upDateEntryContext.put("ptcMilkType", ptcMilkType);
	 		upDateEntryContext.put("ptcQuantity", ptcQuantity);
	 		try{            	
				result = dispatcher.runSync("updateProcurementEntry",upDateEntryContext);
    		if (ServiceUtil.isError(result)) {
                Debug.logWarning("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(result), module);
        		return ServiceUtil.returnError("There was an error while updating   the ProcurementEntry: " + ServiceUtil.getErrorMessage(result));          	            
            }       		
                    	 
        }catch (GenericServiceException e) {
            Debug.logError("Error while running updateApproveBillingValidation Service==========>"+e.getMessage(),module);
            return ServiceUtil.returnError(e.getMessage());
        }
        Map<String, Object> approveProcValidation = FastMap.newInstance(); 
        approveProcValidation.put("userLogin", userLogin);
        approveProcValidation.put("shedId", shedId);	 		
        approveProcValidation.put("validationTypeId", validationTypeId);
        approveProcValidation.put("customTimePeriodId", customTimePeriodId);
        approveProcValidation.put("sequenceNum", sequenceNum);
        approveProcValidation.put("statusId", "ITEM_APPROVED");
        try{            	
			result = dispatcher.runSync("approveValidation",approveProcValidation);
			if (ServiceUtil.isError(result)) {
	            Debug.logWarning("There was an error while approveValidation: " + ServiceUtil.getErrorMessage(result), module);
	    		return ServiceUtil.returnError("There was an error while approve Validation: " + ServiceUtil.getErrorMessage(result));          	            
	        }               	 
	    }catch (GenericServiceException e) {
	        Debug.logError("Error while running approveValidation Service==========>"+e.getMessage(),module);
	        return ServiceUtil.returnError(e.getMessage());
	    }
        Map<String, Object> resultMap = ServiceUtil.returnSuccess("entry updated and Approved successfully for center ==>>"+ context.get("facilityCode"));
		resultMap.put("orderId", orderId);
        return resultMap;
	}
public static Map<String, Object> approveValidationEntries(DispatchContext dctx, Map<String, ? extends Object> context) {		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String shedId = (String) context.get("unitId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String validationTypeId = (String) context.get("validationTypeId");
		String sequenceNum = (String) context.get("sequenceNum");	
		BigDecimal quantity=(BigDecimal) context.get("quantity");
		BigDecimal itemFat=(BigDecimal) context.get("fat");
		BigDecimal itemSnf=(BigDecimal) context.get("snf");
		BigDecimal sQty=BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.get("sQuantity"))){
			sQty=(BigDecimal) context.get("sQuantity");
		}
		BigDecimal itemSFat=(BigDecimal) context.get("sFat");
		BigDecimal cQtyLtrs=BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.get("cQuantity"))){
			cQtyLtrs=(BigDecimal) context.get("cQuantity");
		}
		String productId=(String) context.get("productId");
		List conditionList= FastList.newInstance();
		try{
			GenericValue facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", shedId),false);
			String facilityId = facility.getString("parentFacilityId");
			 conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, facilityId));
      	   	 conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
			 EntityCondition ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			 List<GenericValue> validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
			 if(UtilValidate.isEmpty(validationRuleList)){
				   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, "_NA_"));
	        	   conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
				   ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				    validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);				   
			   }
			   validationRuleList = EntityUtil.filterByDate(validationRuleList);
			   List<GenericValue> entryValidationRule =  EntityUtil.filterByAnd(validationRuleList, UtilMisc.toMap("productId", productId));
   		    GenericValue validationRule =  EntityUtil.getFirst(entryValidationRule);
   		    if(UtilValidate.isNotEmpty(validationRule)){
   		    	BigDecimal maxFat = (validationRule.getBigDecimal("maxFat")).add(new BigDecimal(2));
   		    	BigDecimal maxSnf = (validationRule.getBigDecimal("maxSnf")).add(new BigDecimal(2));
   		    	BigDecimal maxSFat = validationRule.getBigDecimal("maxSFat");
   		    	BigDecimal qtyDivisor = validationRule.getBigDecimal("qtyDivisor");			    		    	
   		    	if( (itemFat.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxFat) &&
		    			(maxFat.compareTo(itemFat) <=0)){
   		    		return ServiceUtil.returnError("You can not apporve this record: please edit FAT value");
		    	}
		    	if((itemFat.compareTo(BigDecimal.ZERO) ==0)&& (sQty.compareTo(BigDecimal.ZERO)==0)&& (cQtyLtrs.compareTo(BigDecimal.ZERO)==0)){
		    		return ServiceUtil.returnError("You can not apporve this record: please edit FAT value ");
		    	}
		    	//sour Fat check 
		    	if(UtilValidate.isNotEmpty(itemSFat)&&UtilValidate.isNotEmpty(maxSFat) &&((maxSFat.compareTo(itemSFat) <=0))){
		    		return ServiceUtil.returnError("You can not apporve this record: please edit sFat value ");
		    	}
		    	if(((itemSnf.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxSnf)) &&
		    			(maxSnf.compareTo(itemSnf) <=0)){
		    		return ServiceUtil.returnError("You can not apporve this record: please edit SNF value ");
		    	}		    	
		    	if((itemSnf.compareTo(BigDecimal.ZERO) ==0) && (sQty.compareTo(BigDecimal.ZERO)==0)&& (cQtyLtrs.compareTo(BigDecimal.ZERO)==0)){
		    		return ServiceUtil.returnError("You can not apporve this record: please edit SNF value ");
		    	}
		    	/*if(UtilValidate.isNotEmpty(quantity) && (quantity.compareTo(BigDecimal.ZERO)==0)&&((itemFat.compareTo(BigDecimal.ZERO)>0)||(itemSnf.compareTo(BigDecimal.ZERO)>0))){
		    		return ServiceUtil.returnError("You can not apporve this record: please edit QUANTITY value ");
		    	}
		    	if(UtilValidate.isNotEmpty(qtyDivisor)&&UtilValidate.isNotEmpty(quantity)&&(quantity.remainder(qtyDivisor)!=BigDecimal.ZERO)){
		    		return ServiceUtil.returnError("You can not apporve this record: please edit QUANTITY record ");
		    	}*/
   		    }
			   
		}catch (GenericEntityException e) {
			// TODO: handle exception
	    	 Debug.logError("Error while running service==========>"+e.getMessage(),module);
	  		 return ServiceUtil.returnError(e.getMessage());
	 	}
		Map<String, Object> result = FastMap.newInstance();		
        Map<String, Object> approveProcValidation = FastMap.newInstance(); 
        approveProcValidation.put("userLogin", userLogin);
        approveProcValidation.put("shedId", shedId);	 		
        approveProcValidation.put("validationTypeId", validationTypeId);
        approveProcValidation.put("customTimePeriodId", customTimePeriodId);
        approveProcValidation.put("sequenceNum", sequenceNum);
        approveProcValidation.put("statusId", "ITEM_APPROVED");
        try{            	
			result = dispatcher.runSync("approveValidation",approveProcValidation);
			if (ServiceUtil.isError(result)) {
	            Debug.logWarning("There was an error while approveValidation: " + ServiceUtil.getErrorMessage(result), module);
	    		return ServiceUtil.returnError("There was an error while approve Validation: " + ServiceUtil.getErrorMessage(result));          	            
	        }               	 
	    }catch (GenericServiceException e) {
	        Debug.logError("Error while running approveValidation Service==========>"+e.getMessage(),module);
	        return ServiceUtil.returnError(e.getMessage());
	    }
        Map<String, Object> resultMap = ServiceUtil.returnSuccess("entry Approved successfully for center");
        return resultMap;
	}	

	public static String updateNegativeBillingAmount(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);			
			
			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
			if (rowCount < 1) {
				Debug.logWarning("No rows to process, as rowCount = " + rowCount,module);
				request.setAttribute("_ERROR_MESSAGE_", "No Crate Type record found");	
				return "error";
			}
			
			for (int i = 0; i < rowCount; i++) {				
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;	
				
				String orderAdjustmentIdStr = null;
				String orderAdjustmentTypeStr = null;
				String amountStr = null;
				String orderId = null;
				orderAdjustmentIdStr = (String) paramMap.get("orderAdjustmentId"+ thisSuffix);
				orderAdjustmentTypeStr = (String) paramMap.get("orderAdjustmentTypeId"+ thisSuffix);
				orderId = (String) paramMap.get("orderId"+ thisSuffix);
				amountStr = (String) paramMap.get("amount" + thisSuffix);
				amountStr = amountStr.replace(",", "");
				BigDecimal amount = BigDecimal.ZERO;				
				
				if (UtilValidate.isNotEmpty(amountStr)) {	
					amount = new BigDecimal(amountStr);
				}		 		
				Map<String, Object> adjustmentsContext = FastMap.newInstance();
				adjustmentsContext.put("userLogin", userLogin);
				adjustmentsContext.put("orderId", orderId);
				adjustmentsContext.put("orderAdjustmentId", orderAdjustmentIdStr);
				adjustmentsContext.put("orderAdjustmentTypeId", orderAdjustmentTypeStr);
				adjustmentsContext.put("amount", amount);	
				result = dispatcher.runSync("updateBillingAdjustment",adjustmentsContext);
				if( ServiceUtil.isError(result)) {
					String errMsg =  ServiceUtil.getErrorMessage(result);
					Debug.logWarning(errMsg , module);
					request.setAttribute("_ERROR_MESSAGE_",errMsg);
					return "error";
				}
			}
		} catch (Exception e) {				
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
			Debug.logWarning(e.getMessage() , module);
			return "error";
		}
		request.setAttribute("MESSAGE", "RECORDS UPDATED SUCCESSFULLY");
	    return "success";
	}    
	
	public static Map<String, Object> recalculateProcurementEntryPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List agentList = FastList.newInstance();
        List<GenericValue> procurementEntryList = FastList.newInstance();
        List<GenericValue> procurementOrderList = FastList.newInstance();
        String facilityId = (String) context.get("facilityId");
        String customTimePeriodId = (String)context.get("customTimePeriodId");
        GenericValue customTimePeriod =null;
        Map<String, Object> resultMap = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(facilityId)){
        	agentList = (List)ProcurementNetworkServices.getFacilityAgents(dctx, context).get("facilityIds");
        	
        }
        
        try{
           List conditionList = FastList.newInstance();
     	   customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
 		   if(UtilValidate.isEmpty(customTimePeriod)){
 			  Debug.logError( "There no active billing time periods. ", module);				 
 			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
 		    }
 		   	
 	    	List<GenericValue> orderItems= FastList.newInstance();
 	        Map<String, Object> result = FastMap.newInstance();
 	        
 		   conditionList.clear();
 	       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
 	       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
 	       //we nedd to handle Unit and Route also
 	       if(!UtilValidate.isEmpty(facilityId)){
 	    	   conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, agentList));
 	       }
 	       conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
 		   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"))) ));
 		   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
 		   procurementOrderList = delegator.findList("OrderHeader", orderCondition, null, null, null,false);
 		   if(UtilValidate.isEmpty(procurementOrderList)){
 			  resultMap =  ServiceUtil.returnSuccess("no order to process for the time period===>"+customTimePeriodId);
 			  resultMap.put("facilityId", facilityId);
 			  return resultMap;
     	  }
 		/* List<String> fieldToSelect = delegator.getModelEntity("OrderItem").getPkFieldNames();
 		fieldToSelect.addAll(delegator.getModelEntity("OrderItem").getNoPkFieldNames());
 		Debug.logInfo("fieldToSelect=========="+fieldToSelect,module);*/
 		 procurementEntryList = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId",EntityOperator.IN , EntityUtil.getFieldListFromEntityList(procurementOrderList, "orderId", true)), null, null, null,false); 
 		 //lets iterrate through each entry and recalculate the price 
 		 
 		 for( GenericValue procurementEntry : procurementEntryList){
 			Map<String, Object> upDateEntryContext = FastMap.newInstance(); 		 
 	 		upDateEntryContext.put("userLogin", userLogin);
 			upDateEntryContext.putAll(procurementEntry.getAllFields()); 			
 			upDateEntryContext.remove("createdTxStamp");
 			upDateEntryContext.remove("createdStamp");
 			upDateEntryContext.remove("lastUpdatedStamp");
 			upDateEntryContext.remove("lastUpdatedTxStamp");
 			try{            	
 				result = dispatcher.runSync("updateProcurementEntry",upDateEntryContext);
        		if (ServiceUtil.isError(result)) {
                    Debug.logWarning("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(result), module);
            		return ServiceUtil.returnError("There was an error while updateing   the ProcurementEntry: " + ServiceUtil.getErrorMessage(result));          	            
                }       		
                        	 
            }catch (GenericServiceException e) {
                Debug.logError("Error while running updateProcurementEntry Service==========>"+e.getMessage(),module);
                return ServiceUtil.returnError(e.getMessage());
            }
 			  
 		 }
 		   
     }catch (GenericEntityException e) {
		// TODO: handle exception
    	 Debug.logError("Error while running service==========>"+e.getMessage(),module);
  		 return ServiceUtil.returnError(e.getMessage());
 	}
     	 
    resultMap = ServiceUtil.returnSuccess("entry updated successfully for facility ==>>"+facilityId);
    resultMap.put("facilityId",facilityId);
    return resultMap;
	}
	public static Map<String, Object>  populateProcBillingForTransportCommission(DispatchContext dctx, Map<String, ? extends Object> context)  {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess("Commission stored successfully");	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		String periodBillingId = null;
		List<GenericValue> billingFacility = FastList.newInstance();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		
		GenericValue facility = null;		
	    try{ 
	    	 facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);			 
	 		   if(UtilValidate.isEmpty(facility)){
	 			  Debug.logError( "invalid facilityId", module);				 
	 			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
	 		    }
	    	
	    }catch(GenericEntityException e){
	    	Debug.logError( e.toString(), module);				 
			return ServiceUtil.returnError(e.toString());
	    }
	    
	 // get the shed details for given facility 
		GenericValue shedDetails = (GenericValue)(ProcurementNetworkServices.getShedDetailsForFacility(dctx,UtilMisc.toMap("facilityId", facilityId))).get("facility");
		String shedId = shedDetails.getString("facilityId");
		
		 /*try{
			  Map<String, Object> resultMap = ServiceUtil.returnSuccess();
	        	Map errorCtx = UtilMisc.toMap("userLogin",userLogin);
	        	errorCtx.put("customTimePeriodId", customTimePeriodId);
	            errorCtx.put("shedId", shedId);
	            errorCtx.put("facilityId", facilityId);
	            errorCtx.put("verifyApprove", "Y");    		
	        	resultMap = dispatcher.runSync("validatePeriodEntries", errorCtx);
	        	if (ServiceUtil.isError(resultMap)) {
	                Debug.logWarning("There was an error while validating Quantity OutLier: " + ServiceUtil.getErrorMessage(resultMap), module);
	        		return ServiceUtil.returnError("There was an error while validating Quantity OutLier : " + ServiceUtil.getErrorMessage(resultMap));          	            
	            }	
	        }catch (GenericServiceException e) {
				// TODO: handle exception
	        	 Debug.logError( e.toString(), module);				 
				  return ServiceUtil.returnError(e.toString());
			}*/	    
	    
	    if(facility.getString("facilityTypeId").equals("SHED")){
	    	List<GenericValue>  unitsList= (List)ProcurementNetworkServices.getShedUnitsByShed(dctx ,UtilMisc.toMap("shedId", facilityId)).get("unitsDetailList");
	    	billingFacility.addAll(unitsList);
	    	
	    }else{
	    	billingFacility.add(facility);
	    }   
	    GenericValue customTimePeriod = null;
    	try{
    		customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
		   if(UtilValidate.isEmpty(customTimePeriod)){
			  Debug.logError( "There no active billing time periods. ", module);				 
			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
		    }
    	}catch(Exception e){
    		
    	}
	    for(GenericValue eachFacility: billingFacility){	    	
	    	String billFacilityId = eachFacility.getString("facilityId");	   
	    	String facilityName= eachFacility.getString("facilityName");
	    	 Map<String, Object> agentContext = FastMap.newInstance();
		        Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		        Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));   	 
			    agentContext.put("fromDate", fromDateTime);     	 
			   	agentContext.put("thruDate", thruDateTime);
				agentContext.put("facilityId",billFacilityId);
				Map UnitTotals = ProcurementReports.getPeriodTotals(dctx, agentContext);
				BigDecimal totLtrs=BigDecimal.ZERO;
				Iterator unitTotalIter = UnitTotals.entrySet().iterator();
				Map agentTotalValuesMap = FastMap.newInstance();
				while (unitTotalIter.hasNext()){
					Map.Entry agentEntry = (Entry) unitTotalIter.next();						
					Map unitPeriodTotals = (Map) agentEntry.getValue();				
					if(UtilValidate.isNotEmpty(unitPeriodTotals)){
						Map unitTotalsMap = (Map)((Map)((Map)((Map)(unitPeriodTotals.get("dayTotals"))).get("TOT")).get("TOT")).get("TOT");
						 totLtrs= (BigDecimal)unitTotalsMap.get("qtyLtrs");
					}
				}
				if(totLtrs.compareTo(BigDecimal.ZERO) <= 0 ){
					return ServiceUtil.returnError("No Procurement Found For This Unit=="+facilityName);
				}	    	
	        
	    	List conditionList = FastList.newInstance();		    	
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"TRNSPT_PROC_MRGN"));
	    	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , billFacilityId));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	GenericValue billingId =null;
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(periodBillingList)){
	    			continue;	    			
	    		}
	    		GenericValue newEntity = delegator.makeValue("PeriodBilling");
	            newEntity.set("billingTypeId", "TRNSPT_PROC_MRGN");
	            newEntity.set("customTimePeriodId", customTimePeriodId);
	            newEntity.set("statusId", "IN_PROCESS");
	            newEntity.set("facilityId", billFacilityId);
	            newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	            newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            newEntity.set("createdDate", UtilDateTime.nowTimestamp());
	            newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());  
				delegator.createSetNextSeqId(newEntity);
				periodBillingId = (String) newEntity.get("periodBillingId");
		        Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId, "customTimePeriodId", customTimePeriodId,"userLogin", userLogin,"facilityId",billFacilityId);
		        dispatcher.runAsync("populateFacilityCommissionProcForTransporter", runSACOContext);
				
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			}catch (GenericServiceException e) {
	            Debug.logError(e, "Error in calling 'populateFacilityCommissionProcForTransporter' service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        } 	    
	    	     
	     }
	     result.put("periodBillingId", periodBillingId);
	     
		 if(UtilValidate.isEmpty(result.get("periodBillingId"))){
			 Debug.logError("Failed to generated billing, Already Generated OR Inprocess for the specified Period and Facility"+facility.getString("facilityId"), module);
	         return ServiceUtil.returnError("Failed to generated billing, Already Generated OR Inprocess for the specified Period and Facility"+facility.getString("facilityId"));
			 
		 }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    	return result;
	}
	public static Map<String, Object> populateFacilityCommissionProcForTransporter(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        String facilityId = (String) context.get("facilityId");
        String purposeTypeId = "MILK_PROCUREMENT";
        String periodBillingId = (String) context.get("periodBillingId");
        GenericValue customTimePeriod = null;
        Map resultMap = ServiceUtil.returnSuccess();
        boolean generationFailed = false;
        //getting unit routes
        Map routeDetailsMap=FastMap.newInstance();
        Map routeDetailsInMap = FastMap.newInstance();
    	routeDetailsInMap.put("userLogin",userLogin);
    	routeDetailsInMap.put("unitId",facilityId);
    	List<GenericValue> routeDetailsList =FastList.newInstance();
    	try{
    		routeDetailsMap = dispatcher.runSync("getUnitRoutes",routeDetailsInMap); 	        	
    		routeDetailsList = (List)routeDetailsMap.get("routesDetailList");
    	}catch(GenericServiceException ge){
    		 Debug.logError(ge, module);             
         return ServiceUtil.returnError("Failed to get unitRoutes " + ge);
    	}        
        GenericValue periodBilling = null;
        try{
   		 periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
   	   }catch(GenericEntityException e){
   		 Debug.logError(e, module);
   		 return ServiceUtil.returnSuccess(e.toString());	
   	   }   
    	 try{
    	   customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
		   if(UtilValidate.isEmpty(customTimePeriod)){
			  Debug.logError( "There no active billing time periods. ", module);				 
			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
		    }		   
		   if(UtilValidate.isEmpty(routeDetailsList)){
			   return ServiceUtil.returnError("No Procurement Routes found in this Unit");
		   }
		   for(GenericValue routeDetails: routeDetailsList){			 
			   String routeId = routeDetails.getString("facilityId");
		        Map<String, Object> agentContext = FastMap.newInstance();
		        Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		        Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));   	 
			    agentContext.put("fromDate", fromDateTime);     	 
			   	agentContext.put("thruDate", thruDateTime);
				agentContext.put("facilityId",routeId);
				Map routeTotals = ProcurementReports.getPeriodTotals(dctx, agentContext);
				Iterator agentTotalIter = routeTotals.entrySet().iterator();
				Map agentTotalValuesMap = FastMap.newInstance();
				while (agentTotalIter.hasNext()){
					Map.Entry agentEntry = (Entry) agentTotalIter.next();						
					String agentFacilityId = (String) agentEntry.getKey();
					Map agentTotalMap = FastMap.newInstance();
					Map agentPeriodTotals = (Map) agentEntry.getValue();
					Map routeWiseValues = (Map)((Map)((Map)((Map)(agentPeriodTotals.get("dayTotals"))).get("TOT")).get("TOT")).get("TOT");
					BigDecimal totLtrs= (BigDecimal)routeWiseValues.get("qtyLtrs");
					BigDecimal kgFat= (BigDecimal)routeWiseValues.get("kgFat");
					BigDecimal kgSnf= (BigDecimal)routeWiseValues.get("kgSnf");
					BigDecimal price= (BigDecimal)routeWiseValues.get("price");
					BigDecimal sPrice=(BigDecimal)routeWiseValues.get("sPrice");
					GenericValue facilityCommission = delegator.findOne("FacilityCommissionProc", UtilMisc.toMap("periodBillingId", periodBillingId, "commissionDate", fromDateTime, "facilityId", facilityId, "productId","_NA_"), false);
					if (facilityCommission == null) {
						facilityCommission = delegator.makeValue("FacilityCommissionProc");
						facilityCommission.put("periodBillingId", periodBillingId);
						facilityCommission.put("commissionDate", fromDateTime);
						facilityCommission.put("facilityId",routeId);
						facilityCommission.put("productId",	"_NA_");
						facilityCommission.put("totalQty", totLtrs);
						facilityCommission.put("kgFat",	((BigDecimal)routeWiseValues.get("kgFat")).add((BigDecimal)routeWiseValues.get("sKgFat")));
						facilityCommission.put("kgSnf",	routeWiseValues.get("kgSnf"));
						facilityCommission.put("totalAmount", ((BigDecimal)routeWiseValues.get("price")).add((BigDecimal)routeWiseValues.get("sPrice")));
						Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
						inputRateAmt.put("rateTypeId", "TRNSPT_PROC_MRGN");
						inputRateAmt.put("productId", "_NA_");
						inputRateAmt.put("fromDate", fromDateTime);
						Map<String, Object> rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
						if (ServiceUtil.isError(rateAmount)) {
							return ServiceUtil.returnError("rate amount is Empty for facility ==="+routeId);
						}
						BigDecimal normalMargin = (BigDecimal) rateAmount.get("rateAmount");
						facilityCommission.put("commissionAmount", normalMargin.multiply(totLtrs));
						facilityCommission.create();
					}									
			   	 }    	
		   	}
			 if (generationFailed) {
	   			 periodBilling.set("statusId", "GENERATION_FAIL");
	   		 } else {
				periodBilling.set("statusId", "GENERATED");
				periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
	   		 }
		 try{
			 periodBilling.store();
		 }catch (Exception e) {
			 Debug.logError(e, module);
			// TODO: handle exception
		}		
    	 }catch (Exception e) {
			 Debug.logError(e, module);
				// TODO: handle exception
			}	
        return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> cancelTransportCommission(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();       
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String userLoginId = (String)userLogin.get("userLoginId");
	    Map<String, Object> result = ServiceUtil.returnSuccess("Commission Successfully Cancelled ");
	    String periodBillingId = (String) context.get("periodBillingId");
	    GenericValue periodBilling = null;
	    try{
	            periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	            periodBilling.set("statusId", "COM_CANCELLED");
	            periodBilling.store();
	    }catch(GenericEntityException e){
	            Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	            return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
	    }
	   
	    return result;
	}
	public static Map<String, Object>  populateProcurementPeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context)  {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess("Billing successfully processed");	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		String periodBillingId = null;
		List<GenericValue> billingFacility = FastList.newInstance();
		String customTimePeriodId = (String) context.get("customTimePeriodId");

		GenericValue facility = null;		
	    try{ 
	    	 facility =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);			 
	 		   if(UtilValidate.isEmpty(facility)){
	 			  Debug.logError( "invalid facilityId", module);				 
	 			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
	 		    }
	    	
	    }catch(GenericEntityException e){
	    	Debug.logError( e.toString(), module);				 
			return ServiceUtil.returnError(e.toString());
	    }
	    
	 // get the shed details for given facility 
		GenericValue shedDetails = (GenericValue)(ProcurementNetworkServices.getShedDetailsForFacility(dctx,UtilMisc.toMap("facilityId", facilityId))).get("facility");
		String shedId = shedDetails.getString("facilityId");
		
		 try{
			  Map<String, Object> resultMap = ServiceUtil.returnSuccess();
	        	Map errorCtx = UtilMisc.toMap("userLogin",userLogin);
	        	errorCtx.put("customTimePeriodId", customTimePeriodId);
	            errorCtx.put("shedId", shedId);
	            errorCtx.put("facilityId", facilityId);
	            errorCtx.put("verifyApprove", "Y");    		
	        	resultMap = dispatcher.runSync("validatePeriodEntries", errorCtx);
	        	if (ServiceUtil.isError(resultMap)) {
	                Debug.logWarning("There was an error while validating Quantity OutLier: " + ServiceUtil.getErrorMessage(resultMap), module);
	        		return ServiceUtil.returnError("There was an error while validating Quantity OutLier : " + ServiceUtil.getErrorMessage(resultMap));          	            
	            }
	        }catch (GenericServiceException e) {
				// TODO: handle exception
	        	 Debug.logError( e.toString(), module);				 
				  return ServiceUtil.returnError(e.toString());
			}	    
	    
	        try{
	           GenericValue customTimePeriod = null;
		       List<GenericValue> orderItems= FastList.newInstance();
    		   customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
    		   if(UtilValidate.isEmpty(customTimePeriod)){
    			   Debug.logError( "There no active billing time periods. ", module);				 
    			   return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
    		   }
    		   if(facility.getString("facilityTypeId").equals("UNIT")){
    			   //Checking for billing is Inprocess or Generated
    			   List periodBilling = FastList.newInstance();
    			   List conList=FastList.newInstance();
    			   	conList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS")));
    			   	conList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
    			   	conList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PB_PROC_MRGN"));
    			   	conList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId));
    		    	EntityCondition cond=EntityCondition.makeCondition(conList,EntityOperator.AND);
    		    	periodBilling = delegator.findList("PeriodBilling", cond, null,null, null, false);
		    		if(UtilValidate.isNotEmpty(periodBilling)){
		    			Debug.logError("Failed to generated billing, Already Generated OR Inprocess for the specified Period and Facility"+facility.getString("facilityName"), module);
			   	         return ServiceUtil.returnError("Failed to generated billing, Already Generated OR Inprocess for the specified Period and Facility"+facility.getString("facilityName"));	    			
			    	}
		    		//Checking unit is associated with customTimePeriodId
    			   GenericValue facilityCustomTimePeriod = delegator.findOne("FacilityCustomTimePeriod", UtilMisc.toMap("facilityId", facilityId,"customTimePeriodId",customTimePeriodId), false);
    			   if(UtilValidate.isEmpty(facilityCustomTimePeriod)){
    				   Debug.logError("Please Associate CustomTimePeriod=="+customTimePeriodId+" with This Unit=="+facility.getString("facilityName"), module);
			   	         return ServiceUtil.returnError("Please Associate CustomTimePeriod=="+customTimePeriodId+" with This Unit=="+facility.getString("facilityName"));
    			   }	
    			   GenericValue tenantConfiguration = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyName", "enableRecalculateProcEntryPrice","propertyTypeEnumId","MILK_PROCUREMENT"), false);
    			   if(UtilValidate.isNotEmpty(tenantConfiguration)&& ("Y".equals(tenantConfiguration.get("propertyValue")))){	   
	    			   //Calling recalculateProcurementEntryPrice service based on tenantConfiguration
	    			   Map procurementPriceCtx = UtilMisc.toMap("userLogin",userLogin);
	    			   	procurementPriceCtx.put("facilityId", facilityId);
	    			   	procurementPriceCtx.put("customTimePeriodId", customTimePeriodId);
	    			   	try{
	    			   		Map resultMap=FastMap.newInstance();
	    			   		resultMap = dispatcher.runSync("recalculateProcurementEntryPrice", procurementPriceCtx);
		    	        	if (ServiceUtil.isError(resultMap)) {
		    	                Debug.logWarning("There was an error while validating Quantity OutLier: " + ServiceUtil.getErrorMessage(resultMap), module);
		    	        		return ServiceUtil.returnError("There was an error while validating Quantity OutLier : " + ServiceUtil.getErrorMessage(resultMap));          	            
		    	            }
	    			   	}catch(GenericServiceException e){
	    			   	 Debug.logError("Error in calling 'recalculateProcurementEntryPrice' service"+e, module);
	    		            return ServiceUtil.returnError("Error in calling 'recalculateProcurementEntryPrice' service");
	    			   	}
    			   }  			   
    		    	
    		   }
    		   
    		   Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
    		   Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
    		   Map orderItemCtx = UtilMisc.toMap("userLogin",userLogin);
    		   orderItemCtx.put("fromDate", fromDateTime);
    		   orderItemCtx.put("thruDate", thruDateTime);
    		   orderItemCtx.put("shedId", facilityId);
    		   orderItems =getProcurementOrderItemsForPeriod(dctx ,orderItemCtx);	
    		   if(UtilValidate.isEmpty(orderItems)){
    	    		  Debug.logError("Failed to generate billing, No Procurement Order to Process"+facility.getString("facilityName"), module);
    	    		  return ServiceUtil.returnError("no Procurement Order to process for the Unit '"+facility.getString("facilityName")+ "'  in the time period " +customTimePeriodId);
    		   }
    	   }catch (GenericEntityException e) {
			// TODO: handle exception
     		 Debug.logError(e.getMessage(), module);
     		 return ServiceUtil.returnError(e.getMessage());
    	   }
    	   
	    if(facility.getString("facilityTypeId").equals("SHED")){
	    	List<GenericValue>  unitsList= (List)ProcurementNetworkServices.getShedUnitsByShed(dctx ,UtilMisc.toMap("shedId", facilityId)).get("unitsDetailList");
	    	billingFacility.addAll(unitsList);
	    	
	    }else{
	    	billingFacility.add(facility);
	    }   
	    for(GenericValue eachFacility: billingFacility){
	    	
	    	List conditionList = FastList.newInstance();
	    	String billFacilityId = eachFacility.getString("facilityId");
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PB_PROC_MRGN"));
	    	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , billFacilityId));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	GenericValue billingId =null;
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(periodBillingList)){
	    			continue;	    			
	    		}
	    		GenericValue newEntity = delegator.makeValue("PeriodBilling");
	            newEntity.set("billingTypeId", "PB_PROC_MRGN");
	            newEntity.set("customTimePeriodId", customTimePeriodId);
	            newEntity.set("statusId", "IN_PROCESS");
	            newEntity.set("facilityId", billFacilityId);
	            newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	            newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            newEntity.set("createdDate", UtilDateTime.nowTimestamp());
	            newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());  
				delegator.createSetNextSeqId(newEntity);
				periodBillingId = (String) newEntity.get("periodBillingId");
	    		
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			}    
	    	     
	        try {
				Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId, "customTimePeriodId", customTimePeriodId,"userLogin", userLogin,"facilityId",facilityId);
		        dispatcher.runAsync("processPeriodBilling", runSACOContext);
	    	}
	        catch (GenericServiceException e) {
	            Debug.logError(e, "Error in calling 'processPeriodBilling' service", module);
	            return ServiceUtil.returnError(e.getMessage());
	        } 	        
	        result.put("periodBillingId", periodBillingId);	  
	    	
	    }	    
		 if(UtilValidate.isEmpty(result.get("periodBillingId"))){
			 Debug.logError("Failed to generated billing, Already Generated OR Inprocess for the specified Period and Facility"+facility.getString("facilityId"), module);
	         return ServiceUtil.returnError("Failed to generated billing, Already Generated OR Inprocess for the specified Period and Facility"+facility.getString("facilityId"));
			 
		 }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    	return result;
	}
	public static Map<String, Object> processPeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        String facilityId = (String) context.get("facilityId");
        String purposeTypeId = "MILK_PROCUREMENT";
        String periodBillingId = (String) context.get("periodBillingId");
        GenericValue customTimePeriod = null;
        List masterList = FastList.newInstance();
        Map resultMap = ServiceUtil.returnSuccess();
        Map resultValue = ServiceUtil.returnSuccess();
        List conditionList =FastList.newInstance();
        List agentFacilityList = FastList.newInstance();
        boolean generationFailed = false;
        List<GenericValue> orderList = FastList.newInstance();
        GenericValue periodBilling = null;
        try{
   		 periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
   	   }catch(GenericEntityException e){
   		 Debug.logError(e, module);
   		 return ServiceUtil.returnSuccess(e.toString());	
   	   }   
    	 try{
    	   customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
		   if(UtilValidate.isEmpty(customTimePeriod)){
			  Debug.logError( "There no active billing time periods. ", module);				 
			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
		    }
		   List<String> facilityIds= FastList.newInstance();    	
	    	List<GenericValue> orderItems= FastList.newInstance();
	        Map<String, Object> result = FastMap.newInstance();
	        Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facilityId));
	        if(UtilValidate.isNotEmpty(facilityAgents)){
	        	facilityIds= (List) facilityAgents.get("facilityIds");
	        	agentFacilityList = (List) facilityAgents.get("facilityIds");
	        }
		   conditionList.clear();
	       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
	       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
	       //we nedd to handle Unit and Route also
	       if(!UtilValidate.isEmpty(facilityId)){
	    	   conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
	       }		   
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
		   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"))) ));
		   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   orderList =delegator.findList("OrderHeaderAndItems", orderCondition, null, null, null,false);	   
		   
    	 }catch (GenericEntityException e) {
			// TODO: handle exception
     		 Debug.logError(e.getMessage(), module);
     		 return ServiceUtil.returnError(e.getMessage());
		}
    	 if(UtilValidate.isEmpty(orderList)){
    		  return ServiceUtil.returnSuccess("no order to process for the time period===>"+customTimePeriodId);
    	 }
    	 List invoiceIdsList =FastList.newInstance();
    	 List orderIdsList =FastList.newInstance();
    	 orderIdsList = EntityUtil.getFieldListFromEntityList(orderList, "orderId", true);
    	 for(int i=0;i< orderIdsList.size();i++){		  
	           String orderId = (String)orderIdsList.get(i);	           
	            try{            	
	        		resultMap = dispatcher.runSync("createInvoiceForOrderAllItems", UtilMisc.<String, Object>toMap("orderId", orderId,"userLogin", userLogin));
	        		if (ServiceUtil.isError(resultMap)) {
	        			 periodBilling.set("statusId", "GENERATION_FAIL");
	        			 periodBilling.store();
	                    Debug.logWarning("There was an error while creating  the invoice: " + ServiceUtil.getErrorMessage(resultMap), module);
	            		return ServiceUtil.returnError("There was an error while creating the invoice: " + ServiceUtil.getErrorMessage(resultMap));          	            
	                }  
	        		invoiceIdsList.add(resultMap.get("invoiceId"));
	             //OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, "ORDER_COMPLETED", "ITEM_APPROVED", "ITEM_COMPLETED", null);           	 
	            }catch (Exception e) {
	                Debug.logError(e, module);
	            }
	           boolean approved = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId); 
		   }
    	 Map<String, Object> agentContext = FastMap.newInstance();
    	Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
    	Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));   	 
    	for(int i=0;i< agentFacilityList.size();i++){
    		 String agentFaciltyId =(String) agentFacilityList.get(i);
    		 agentContext.put("fromDate", fromDateTime);     	 
        	 agentContext.put("thruDate", thruDateTime);
    		 agentContext.put("facilityId",agentFaciltyId);
    		 Map agentTotals = ProcurementReports.getPeriodTotals(dctx, agentContext);
    		 Iterator agentTotalIter = agentTotals.entrySet().iterator();
        	 Map agentTotalValuesMap = FastMap.newInstance();
        	 while (agentTotalIter.hasNext()){
        		 Map.Entry agentEntry = (Entry) agentTotalIter.next();						
    				String agentFacilityId = (String) agentEntry.getKey();
    				Map agentTotalMap = FastMap.newInstance();
    				Map agentPeriodTotals = (Map) agentEntry.getValue();
    				Iterator agentPeriodEntries = agentPeriodTotals.entrySet().iterator();				
    				while (agentPeriodEntries.hasNext()) {					
    					Map.Entry dayAgentEntry = (Entry) agentPeriodEntries.next();	
    					Map dayAgentEntryMap = (Map) dayAgentEntry.getValue();
    					Map agentWiseTotals = (Map) dayAgentEntryMap.get("TOT");
    					Map agentPeriodToals = (Map) agentWiseTotals.get("TOT");
    					agentTotalMap.putAll(agentPeriodToals);				
    				}
    				agentTotalValuesMap.put(agentFacilityId, agentTotalMap);						
        	 }
        	 masterList.add(agentTotalValuesMap);
    	}    	 
    	  	  	 
    	Map result = populateProcuementFacilityCommissions(dctx, context, masterList,periodBillingId, customTimePeriodId);
    	
    	//here we are assuming always we are going with unitId
    	Map inputAbstMap = FastMap.newInstance();
    	inputAbstMap.put("userLogin",userLogin);
    	inputAbstMap.put("customTimePeriodId",customTimePeriodId);
    	inputAbstMap.put("periodBillingId",periodBillingId);
    	inputAbstMap.put("unitId",facilityId);
    	
    	Map result1 = populateProcuementAbstractFromFacilityComissions(dctx, inputAbstMap);
    	
    	//Storing periodBillingId in Invoice Entity to change status when invoice get cancelled
    	List<GenericValue> tempInvoicelist=FastList.newInstance();
		for (int j = 0; j < invoiceIdsList.size(); ++j) {
			String invoiceId = (String) invoiceIdsList.get(j);
			try {
				GenericValue invoiceDetails = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
				invoiceDetails.set("referenceNumber", "PROC_BILLING_"+ periodBillingId);
				tempInvoicelist.add(invoiceDetails);				
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				generationFailed = true;
			}
		}
		try{
			delegator.storeAll(tempInvoicelist);
		}catch(GenericEntityException g){
			 Debug.logError(g, module);
			 generationFailed = true;
		}
    	
		 if (generationFailed) {
   			 periodBilling.set("statusId", "GENERATION_FAIL");
   		 } else {
				periodBilling.set("statusId", "GENERATED");
				periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
   		 }
		 try{
			 periodBilling.store();
		 }catch (Exception e) {
			 Debug.logError(e, module);
			// TODO: handle exception
		}		
        return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> populateProcuementFacilityCommissions(DispatchContext dctx, Map<String, ? extends Object> context, List masterList, String periodBillingId, String customTimePeriodId) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		customTimePeriodId = (String) context.get("customTimePeriodId");
		periodBillingId = (String) context.get("periodBillingId");
		GenericValue customTimePeriod = null;
		GenericValue periodBilling = null;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if (UtilValidate.isEmpty(customTimePeriod)) {
				Debug.logError("There no active billing time periods. ", module);
				return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
			}
			periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
			if (UtilValidate.isEmpty(periodBilling)) {
				Debug.logError("invalid period billing. ", module);
				return ServiceUtil.returnError("invalid period billing.");
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		Timestamp fromDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		if (masterList.size() == 0) {
			Debug.logError("masterList is empty", module);
			return ServiceUtil.returnError("masterList is empty");
		}
		// here populate product price calculation map to figure out  to use total solids or kgfat for cartage and comission
		 List<GenericValue> procProductList = ProcurementNetworkServices.getProcurementProducts(dctx, context);
		 /*Map<String, String>productUseSolidMap = FastMap.newInstance();
		 for(GenericValue procProduct :procProductList){
			 // call price calc service with some default fat and snf
			    Map priceContext = FastMap.newInstance();
				priceContext.put("userLogin", userLogin);
				priceContext.put("productId", procProduct.getString("productId"));
				priceContext.put("facilityId",periodBilling.getString("facilityId"));
				priceContext.put("priceDate",UtilDateTime.nowTimestamp());
				priceContext.put("snfPercent", new BigDecimal("8.95"));
				priceContext.put("fatPercent", new BigDecimal("6"));
				Map priceResult = PriceServices.getProcurementProductPrice(dctx,priceContext);
				if (ServiceUtil.isError(priceResult)) {
					Debug.logWarning("There was an error while calculating the price: "+ ServiceUtil.getErrorMessage(priceResult), module);
					return ServiceUtil.returnError("There was an error while calculating the price: "
									+ ServiceUtil.getErrorMessage(priceResult));
				}
				productUseSolidMap.put(procProduct.getString("productId"), (String)priceResult.get("useTotalSolids"));		 
		 }*/
		for (int i = 0; i < masterList.size(); i++) {
			Map agentMargins = FastMap.newInstance();
			agentMargins = (Map) masterList.get(i);
			Iterator agentMarginsIter = agentMargins.entrySet().iterator();
			while (agentMarginsIter.hasNext()) {
				Map.Entry agentEntry = (Entry) agentMarginsIter.next();
				String facilityId = (String) agentEntry.getKey();
				Map AgentWiseTotalValues = (Map) agentEntry.getValue();
				Map AgentPeriodEntries = (Map) AgentWiseTotalValues.get("TOT");
				GenericValue facilityDetail = null;
				BigDecimal normalMargin = null;
				BigDecimal cartage = null;
				Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
				try {
					facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
					inputRateAmt.put("facilityId", facilityId);
					inputRateAmt.put("rateCurrencyUomId", "INR");
					inputRateAmt.put("fromDate", fromDateTime);
					inputRateAmt.put("rateTypeId", "PROC_CARTAGE");
					Map<String, Object> cartageAmt = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
					cartage = (BigDecimal) cartageAmt.get("rateAmount");
				} catch (GenericServiceException s) {
					s.printStackTrace();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				Iterator milkTypeIter = AgentWiseTotalValues.entrySet().iterator();
				List<GenericValue> productDetailsList = null;
				while (milkTypeIter.hasNext()) {
					Map.Entry milkTypeEntry = (Entry) milkTypeIter.next();
					String milkKeyType = (String) milkTypeEntry.getKey();
					if (milkKeyType != "TOT") {
						try {
							String productName = (String) milkTypeEntry.getKey();
							Map milkTypeTotals = (Map) milkTypeEntry.getValue();
							productDetailsList = delegator.findList("Product", EntityCondition.makeCondition(EntityCondition.makeCondition("productName", EntityOperator.EQUALS, productName),EntityOperator.AND,EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(procProductList, "productId", true))), null, null, null, false);
							String productId = null;
							for (GenericValue productDetails : productDetailsList) {
								productId = productDetails.getString("productId");
								try {
									GenericValue facilityCommission = delegator.findOne("FacilityCommissionProc", UtilMisc.toMap("periodBillingId", periodBillingId, "commissionDate", fromDateTime, "facilityId", facilityId, "productId",	productId), false);
									if (facilityCommission == null) {
										facilityCommission = delegator.makeValue("FacilityCommissionProc");
										facilityCommission.put("periodBillingId", periodBillingId);
										facilityCommission.put("commissionDate", fromDateTime);
										facilityCommission.put("facilityId",facilityId);
										facilityCommission.put("productId",	productId);
										facilityCommission.put("totalQty", milkTypeTotals.get("qtyKgs"));
										facilityCommission.put("kgFat",	((BigDecimal)milkTypeTotals.get("kgFat")));
										facilityCommission.put("kgSnf", milkTypeTotals.get("kgSnf"));
										facilityCommission.put("totalAmount", ((BigDecimal)milkTypeTotals.get("price")).add((BigDecimal)milkTypeTotals.get("sPrice")));
										inputRateAmt.put("rateTypeId", "PROC_AGENT_MRGN");
										inputRateAmt.put("productId", productId);
										inputRateAmt.put("facilityId", facilityId);
										Map<String, Object> rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
										if (ServiceUtil.isError(rateAmount)) {
											return ServiceUtil.returnError("rate amount is Empty for facility ==="+facilityId);
										}
										
										normalMargin = (BigDecimal) rateAmount.get("rateAmount");
										String uomId = "VLIQ_KG";
										if(UtilValidate.isNotEmpty(rateAmount.get("uomId"))){
											uomId = (String)rateAmount.get("uomId");
										}
										BigDecimal totSolids = BigDecimal.ZERO;
										totSolids = ((BigDecimal)facilityCommission.get("kgFat"));
										if(uomId.equalsIgnoreCase("VLIQ_TS")){
											totSolids = totSolids.add((BigDecimal)facilityCommission.getBigDecimal("kgSnf"));
										}
										if(uomId.equalsIgnoreCase("VLIQ_TS")||uomId.equalsIgnoreCase("VLIQ_KGFAT")){
											facilityCommission.put("commissionAmount", normalMargin.multiply(totSolids));
										}else{
											facilityCommission.put("commissionAmount", normalMargin.multiply((BigDecimal) milkTypeTotals.get("qtyKgs")));
										}
										BigDecimal cQty = BigDecimal.ZERO;
										BigDecimal cQtyKgs = BigDecimal.ZERO;
										if(UtilValidate.isNotEmpty(milkTypeTotals.get("cQtyLtrs"))){
											cQty = (BigDecimal)milkTypeTotals.get("cQtyLtrs");
											cQtyKgs = ProcurementNetworkServices.convertLitresToKG(cQty);
										}
										
										facilityCommission.put("cartage", (cartage.multiply(((BigDecimal) milkTypeTotals.get("qtyKgs")).add(cQtyKgs))));									
										facilityCommission.create();
									}
								} catch (GenericEntityException e) {
									Debug.logError("Error While Creating New FacilityCommistionProc", module);
									return ServiceUtil.returnError("Error While Creating New FacilityCommistion");
								} catch (GenericServiceException s) {
									// TODO: handle exception
									Debug.logError("Error while getting Facility Rate ===="+s.getMessage(),module);
								}
							}
						} catch (GenericEntityException ge) {
							ge.printStackTrace();
						}
					}
				}
			}
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> cancelProcessPeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
	    LocalDispatcher dispatcher = dctx.getDispatcher();       
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String userLoginId = (String)userLogin.get("userLoginId");
	    Map<String, Object> result = ServiceUtil.returnSuccess("Billing Successfully Cancelled ");
	    String periodBillingId = (String) context.get("periodBillingId");
	    GenericValue periodBilling = null;
	    GenericValue customTimePeriod = null;
	    try{
            periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
            periodBilling.set("statusId", "CANCEL_INPROCESS");
            periodBilling.store();
            customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", periodBilling.get("customTimePeriodId")), false);
            if((customTimePeriod.getString("isClosed")).equals("Y")){
            	Debug.logError("You cannot cancel a closed period", module);
				return ServiceUtil.returnError("You cannot cancel a closed period");
            }
	    }catch(GenericEntityException e){
            Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
            return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
	    }
	    Map<String, Object> inMap = FastMap.newInstance();
        inMap.put("periodBillingId",periodBillingId);
        inMap.put("userLogin",userLogin);
	    try{
            dispatcher.runAsync("cancelProcessBilling",inMap);
	    }catch(GenericServiceException e){
            Debug.logError("Unable to cancel the adjustment"+e, module);
            return ServiceUtil.returnError("Unable to cancel margin adjustment in Payments");
	    }
	    return result;
	}
	 public static Map<String, Object> cancelProcessBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();       
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map<String, Object> result = ServiceUtil.returnSuccess("Billing Successfully Cancelled ");;
	        String periodBillingId = (String) context.get("periodBillingId");
	        String InvoicebillingId ="PROC_BILLING_"+periodBillingId;
	        List<GenericValue> invoicesList=FastList.newInstance();
	        List<GenericValue> orderItemBillingList = FastList.newInstance();
	        GenericValue periodBilling = null;
	        Set invoiceIdsSet= null;
	        try{
	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	        	invoicesList = delegator.findList("Invoice", EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS, InvoicebillingId), null, null,null, false);
	            invoiceIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(invoicesList, "invoiceId", true));
	        }catch (GenericEntityException e) {
	    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
			}        
	    	//changing invoice Status to Invoice Cancelled
	    	for(GenericValue invoiceDetails : invoicesList){
	    		 Map<String, Object> invoiceCtx = FastMap.newInstance();
	    		 invoiceCtx.put("invoiceId", invoiceDetails.getString("invoiceId"));
	    		 invoiceCtx.put("statusId", "INVOICE_CANCELLED");
	    		 invoiceCtx.put("userLogin",userLogin);
	    		 try{
	    			 Map<String, Object> setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", invoiceCtx);
	    			 if(ServiceUtil.isError(setInvoiceStatusResult)){
	    				 Debug.logError("Error while running setInvoiceStatus service ===>"+ServiceUtil.getErrorMessage(setInvoiceStatusResult),module);
		    			 return ServiceUtil.returnError("Error while cancelling the invoices");
	    			 }
	    		 }catch (GenericServiceException e) {
	    			 Debug.logError("Error while cancelling the invoices===>"+e.getMessage(),module);
	    			 return ServiceUtil.returnError("Error while cancelling the invoices");
				}
	    	 }
	    	 // Changing orderItem to ItemCreated and orderHeader status  to OrderCreated
    		 List<GenericValue> itemStatusStoreList=FastList.newInstance();
    		 List<GenericValue> headerStatusStoreList=FastList.newInstance();
    		 try{
    			 String [] fields = {"orderId","orderItemSeqId"};
    	         Set<String> selectFields = UtilMisc.toSetArray(fields);
    			 orderItemBillingList = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIdsSet), selectFields, null,null, false);
    	         Set<GenericValue> orderItemsSet= null;
    	         orderItemsSet = new HashSet(orderItemBillingList);
    	         for(GenericValue orderItems : orderItemsSet){
    	        	 String orderId = orderItems.getString("orderId");
    	        	 String orderItemSeqId = orderItems.getString("orderItemSeqId");
    	        	 GenericValue orderItem = delegator.findOne("OrderItem",UtilMisc.toMap("orderId",orderId,"orderItemSeqId",orderItemSeqId), false);
    	        	 GenericValue orderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId",orderId),false);
	    			 orderItem.set("statusId", "ITEM_CREATED");
	    			 orderHeader.set("statusId", "ORDER_CREATED");
	    			 headerStatusStoreList.add(orderHeader);
	    			 itemStatusStoreList.add(orderItem);
    	    	 }
    	         delegator.storeAll(headerStatusStoreList);
    	         delegator.storeAll(itemStatusStoreList);
 	    	}catch (Exception e) {
 	    		Debug.logError("Unable to Store invoice Status"+e, module);
 	    		return ServiceUtil.returnError("Unable to Store invoice Status"); 
 			}	    	 
 	    		periodBilling.set("statusId", "COM_CANCELLED"); 
 	    	try{
		    		periodBilling.store();    		
	    	 	}catch (Exception e) {
		    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
		    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
	    	 	}
	        return result;
	    }
	public static Map<String, Object> getPeriodAdjustmentsForAgent(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String facilityId = (String) context.get("facilityId");
        String purposeTypeId = "MILK_PROCUREMENT";
        Timestamp fromDate = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
		if(UtilValidate.isEmpty(fromDate)){	
			Debug.logError("fromDate cannot be empty", module);
			return ServiceUtil.returnError("fromDate cannot be empty");							
		}	    	
    	Timestamp thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
		if(UtilValidate.isEmpty(thruDate)){	
			Debug.logError("thruDate cannot be empty", module);
			return ServiceUtil.returnError("thruDate cannot be empty");							
		}
		Map resultMap = ServiceUtil.returnSuccess();
        List conditionList =FastList.newInstance();
        List<String> facilityIds= FastList.newInstance();
        if(UtilValidate.isNotEmpty(facilityId)){
	        Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facilityId));
	        if(UtilValidate.isNotEmpty(facilityAgents)){
	        	facilityIds= (List) facilityAgents.get("facilityIds");
	        }
        }
        if(UtilValidate.isNotEmpty(context.get("facilityIds"))){
        	facilityIds= (List) context.get("facilityIds");
        }
        List<GenericValue> orderList = FastList.newInstance();
        Map adjustmentsTypeMap = FastMap.newInstance();
        Map centerAdjustmentsTypeMap = FastMap.newInstance();
    	 try{    	   
		   conditionList.clear();
	       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
	       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
	       //we need to handle Unit and Route also
	      
	       if(UtilValidate.isEmpty(facilityIds)){
	    	   resultMap.put("adjustmentsTypeMap",adjustmentsTypeMap);
	       	   resultMap.put("centerWiseAdjustments",centerAdjustmentsTypeMap);
		       return resultMap;
	       }
	       		   
		   //conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
	       conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
		   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , thruDate) ));
		   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   orderList =delegator.findList("OrderHeaderAdjustmentAndAdjustmentType", orderCondition, null, UtilMisc.toList("parentTypeId"), null,false);	  		   
    	 }catch (GenericEntityException e) {
			// TODO: handle exception
     		 Debug.logError(e.getMessage(), module);
     		 return ServiceUtil.returnError(e.getMessage());
		}
    	 if(UtilValidate.isEmpty(orderList)){
    		  return ServiceUtil.returnSuccess("no order to process for the time period");
    	 }    	
    	
    	 Map tempAdditionsMap = FastMap.newInstance();
    	 Map tempDeductionMap = FastMap.newInstance();
    	 try{
    		 for(GenericValue orderDetail : orderList){		
    			 Map tempAdjustmentsMap = FastMap.newInstance();
    			 Map tempAdjustmentsTypeMap = FastMap.newInstance();
    			 Map tempCenterAdjustmentMap = FastMap.newInstance();
    			 Map centerAdjMap = FastMap.newInstance();
    			 Map centerAdjTypeMap = FastMap.newInstance();
    			 String centerFacilityId = orderDetail.getString("originFacilityId");
    			 String parentTypeId = orderDetail.getString("parentTypeId");
    			 String orderAdjustmentTypeId = orderDetail.getString("orderAdjustmentTypeId");		          
    			 BigDecimal amount = orderDetail.getBigDecimal("amount");
    			 
    			 if("MILKPROC_ADDITIONS".equals(orderDetail.getString("parentTypeId"))){	 
  	        	   	if(UtilValidate.isNotEmpty(tempAdditionsMap.get(orderAdjustmentTypeId))){
  	        	   		BigDecimal tempAmt = (BigDecimal)tempAdditionsMap.get(orderAdjustmentTypeId);
  	        	   		BigDecimal totalAmt = amount.add(tempAmt);
  	        	   		tempAdditionsMap.put(orderAdjustmentTypeId, totalAmt);
  	        	   	}else{
  	        	   		tempAdditionsMap.put(orderAdjustmentTypeId, amount); 	  
  	        	   	}	        	   
  	        	   		adjustmentsTypeMap.put(parentTypeId,tempAdditionsMap);
	             }else{
	        	    amount = amount.multiply(new BigDecimal(-1));
	        	    if(UtilValidate.isNotEmpty(tempDeductionMap.get(orderAdjustmentTypeId))){
	        	   		tempDeductionMap.put(orderAdjustmentTypeId, (amount.add((BigDecimal)tempDeductionMap.get(orderAdjustmentTypeId))));
	        	    }else{
	        	   		tempDeductionMap.put(orderAdjustmentTypeId, amount);	        	
	        	    }        	   		
	        	    adjustmentsTypeMap.put(parentTypeId,tempDeductionMap);	
	             }       
    			 
    			//center wise adjustments
       	   		if(UtilValidate.isEmpty(centerAdjustmentsTypeMap.get(centerFacilityId))){
       	   			tempCenterAdjustmentMap.put(orderAdjustmentTypeId, amount);
       	   			Map AdjTempMap = FastMap.newInstance();
       	   			tempAdjustmentsMap.putAll(tempCenterAdjustmentMap);
  		   			AdjTempMap.put(orderDetail.getString("parentTypeId"), tempAdjustmentsMap);
  		   			tempAdjustmentsTypeMap.putAll(AdjTempMap);
  		   			centerAdjustmentsTypeMap.put(centerFacilityId,AdjTempMap);
       	   		}else{
 	        		centerAdjMap = (Map)centerAdjustmentsTypeMap.get(centerFacilityId);
 	        		if(UtilValidate.isEmpty(centerAdjMap.get(orderDetail.getString("parentTypeId")))){
 	        		   tempCenterAdjustmentMap.put(orderAdjustmentTypeId, amount); 
 	        		   tempAdjustmentsMap.putAll(tempCenterAdjustmentMap);
 	        		   centerAdjMap.put(orderDetail.getString("parentTypeId"), tempAdjustmentsMap);
 	        		   tempAdjustmentsTypeMap.putAll(centerAdjMap);
 	        		   centerAdjustmentsTypeMap.put(centerFacilityId,centerAdjMap);
 	        		}else{
 	        			centerAdjTypeMap = (Map)centerAdjMap.get(orderDetail.getString("parentTypeId"));
 	        		   
 	        			if(UtilValidate.isNotEmpty(centerAdjTypeMap.get(orderAdjustmentTypeId))){
 	        				BigDecimal tempAmt = BigDecimal.ZERO;
 	        				tempAmt = (BigDecimal)centerAdjTypeMap.get(orderAdjustmentTypeId);
 	        				BigDecimal totalAmt = amount.add(tempAmt);
 	        				centerAdjTypeMap.put(orderAdjustmentTypeId, totalAmt);
 	        			}else{
 	        				centerAdjTypeMap.put(orderAdjustmentTypeId, amount); 	  
 	        			}
 	        			tempAdjustmentsMap.putAll(centerAdjTypeMap);
 	        		 	centerAdjMap.put(orderDetail.getString("parentTypeId"), tempAdjustmentsMap);
 	        		 	tempAdjustmentsTypeMap.putAll(centerAdjMap);
 	        		 	centerAdjustmentsTypeMap.put(centerFacilityId,centerAdjMap);
 	        		}
       	    	}
    		}    
    	 } catch(Exception e){
    		// TODO: handle exception
     		 Debug.logError(e.getMessage(), module);
     		 return ServiceUtil.returnError(e.getMessage());
    	 }
    	resultMap.put("adjustmentsTypeMap",adjustmentsTypeMap);
    	resultMap.put("centerWiseAdjustments",centerAdjustmentsTypeMap);
        return resultMap;
	} 
   
	public static Map<String, Object> createAdjustmentEntryAjax(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> resultMap = FastMap.newInstance();
        try{            	
        	resultMap = dispatcher.runSync("createBillingAdjustment",context);
    		if (ServiceUtil.isError(resultMap)) {
    			
                Debug.logWarning("There was an error while updateing   the adjustment entry: " + ServiceUtil.getErrorMessage(resultMap), module);
                
        		return ServiceUtil.returnError("There was an error while updateing   the adjustment entry: : " + ServiceUtil.getErrorMessage(resultMap));          	            
            }       		
        }catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("There was an error while updateing   the AdjustmentEntry: " + e.toString());
        }		  
        return resultMap;
   }
	
	public static String createAdjustmentsEntry(HttpServletRequest request,HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);

		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);			
			
			int rowCount = UtilHttp.getMultiFormRowCount(request);
			if (rowCount < 1) {
				Debug.logWarning("No rows to process, as rowCount = " + rowCount,module);
				request.setAttribute("_ERROR_MESSAGE_", "No Adjustment Type record found");	
				TransactionUtil.rollback();
				return "error";
			}
			
			for (int i = 0; i < rowCount; i++) {				
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;	
				String customTimePeriodIdStr = null;
				String orderAdjustmentTypeIdStr = null;
				String shedCode = null;
			    String unitCode = null;
			    String centerCode = null;
				String amountStr = null;
				
				customTimePeriodIdStr = (String) paramMap.get("customTimePeriodId");
				shedCode = (String) paramMap.get("shedCode");
				unitCode = (String) paramMap.get("unitCode");
				centerCode = (String) paramMap.get("centerCode");
				orderAdjustmentTypeIdStr = (String) paramMap.get("adjustmentTypeId"+ thisSuffix);
				amountStr = (String) paramMap.get("amount" + thisSuffix);
				
				amountStr = amountStr.replace(",", "");
				BigDecimal amount = BigDecimal.ZERO;				
				if (UtilValidate.isNotEmpty(amountStr)) {	
					amount = new BigDecimal(amountStr);
				}				

				if(!(amount.equals(BigDecimal.ZERO))){
					Map<String, Object> adjustmentsContext = FastMap.newInstance();
					adjustmentsContext.put("userLogin", userLogin);
					adjustmentsContext.put("shedCode", shedCode);
					adjustmentsContext.put("centerCode", centerCode);
					adjustmentsContext.put("unitCode", unitCode);
					adjustmentsContext.put("customTimePeriodId", customTimePeriodIdStr);
					adjustmentsContext.put("adjustmentTypeId", orderAdjustmentTypeIdStr);
					adjustmentsContext.put("amount", amount);	
					result = dispatcher.runSync("createBillingAdjustment",adjustmentsContext);
					if( ServiceUtil.isError(result)) {
						String errMsg =  ServiceUtil.getErrorMessage(result);
						Debug.logWarning(errMsg , module);
						request.setAttribute("_ERROR_MESSAGE_",errMsg);
						TransactionUtil.rollback();
						return "error";
					}
				}
			}
		} catch (Exception e) {				
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());	
			Debug.logWarning(e.getMessage() , module);
            try{
            	TransactionUtil.rollback(beganTransaction, "Error saving Adjustments", e);
            }catch (Exception e2) {
            	Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
			}
            Debug.logError(e, "An entity engine error occurred while saving Adjustments", module);
            return "error";
		}finally {
	  		  // only commit the transaction if we started one... this will throw an exception if it fails
	  		  try {
	  			  TransactionUtil.commit(beganTransaction);
	  		  } catch (GenericEntityException e) {
	  			  Debug.logError(e, "Could not commit transaction for entity engine error occurred while saving Adjustments", module);
	  		  }
	  	}
		request.setAttribute("MESSAGE", "RECORDS CERATED SUCCESSFULLY....");
	    return "success";
	}    
	
   public static Map<String, Object> createBillingAdjustment(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        String shedCode = (String) context.get("shedCode");
        String unitCode = (String) context.get("unitCode");
        String centerCode = (String) context.get("centerCode");
        String orderAdjustmentTypeId = (String) context.get("adjustmentTypeId");
        BigDecimal amount = (BigDecimal)context.get("amount");
        Timestamp periodDate = (Timestamp) context.get("periodDate");
        GenericValue facility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, context)).get("agentFacility");
	   	if(UtilValidate.isEmpty(facility)){
	   		 Debug.logError("Agent Not found with Code ==>"+centerCode, module);
	     	 return ServiceUtil.returnError("Agent Not found with Id ==>"+centerCode);        	
	   	}
	   	String facilityId = facility.getString("facilityId");
	   	if(UtilValidate.isEmpty(customTimePeriodId)&&UtilValidate.isEmpty(periodDate)){
	   		Debug.logError("CustomTimePeriodId or estimated delivery date is Missing", module);
	   		return ServiceUtil.returnError("CustomTimePeriodId or estimated Delivery date is Missing");
	   	}
	   	if(UtilValidate.isNotEmpty(periodDate)){
	   		try{
	   	     		Map checkTimeperiodInMap = FastMap.newInstance();
	   	     		checkTimeperiodInMap.put("userLogin", userLogin);
	   	     		checkTimeperiodInMap.put("facilityId", facilityId);
	   	     		checkTimeperiodInMap.put("fromDate", periodDate);
	   	     		Map timePeriodResultMap  = dispatcher.runSync("getFacilityCustomTimePeriod", checkTimeperiodInMap);
	   	     		if(ServiceUtil.isError(timePeriodResultMap)){
	   	     		  Debug.logError( "There no active billing time periods for"+periodDate+ ". ", module);				 
	   	  			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
	   	     			
	   	     		}
	   	     	GenericValue  customTimePeriod = (GenericValue)timePeriodResultMap.get("customTimePeriod");
	   	     	customTimePeriodId = (String)customTimePeriod.get("customTimePeriodId");
	   		}catch (GenericServiceException e) {
				// TODO: handle exception
	   			Debug.logError("Error while getting customTimePeriodId", module);
	   			ServiceUtil.returnError("Error while getting customTimePeriodId for the given Date"+periodDate);
			}
	   	}
	   	Map checkProcPeriodBillingInMap = FastMap.newInstance();
     	checkProcPeriodBillingInMap.put("customTimePeriodId", customTimePeriodId);
     	checkProcPeriodBillingInMap.put("facilityId", facilityId);
     	checkProcPeriodBillingInMap.put("userLogin", userLogin);
     	
     	Map periodValidationMap = checkProcPeriodBilling(dctx,checkProcPeriodBillingInMap);
     	Debug.log("periodValidationMap==============="+periodValidationMap);
     	if(ServiceUtil.isError(periodValidationMap)){
     		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(periodValidationMap));
     	}
	   	
	   		   	
        GenericValue customTimePeriod = null;
    	GenericValue orderDetail = null;
        Map resultMap = ServiceUtil.returnSuccess();
        List conditionList =FastList.newInstance();
        List<GenericValue> orderList = FastList.newInstance();
    	 try{
    	   customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
		   if(UtilValidate.isEmpty(customTimePeriod)){
			  Debug.logError( "There no active billing time periods. ", module);				 
			  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
		    }		  
		   conditionList.clear();
	       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
	       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));	          
	       conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));	          
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
		   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"))) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"))) ));
		   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   orderList =delegator.findList("OrderHeader", orderCondition, null, null, null,false);	
		   
		   // if the adjustmentType parent is  deduction (MILKPROC_DEDUCTIONS) make the amount as negative value
		  GenericValue  orderAdjustmentType =delegator.findOne("OrderAdjustmentType", UtilMisc.toMap("orderAdjustmentTypeId", orderAdjustmentTypeId),false);
		 if(UtilValidate.isEmpty(orderAdjustmentType)){
			 return ServiceUtil.returnError("Adjustment Not found with adjustmentType========="+orderAdjustmentTypeId);
		 }
		  if((orderAdjustmentType.getString("parentTypeId")).equals("MILKPROC_DEDUCTIONS") ){
			  if(amount.compareTo(BigDecimal.ZERO)>0){
				  amount = amount.negate();
			  }
			  			  
		  }
		  
    	 }catch (GenericEntityException e) {
			// TODO: handle exception
     		 Debug.logError(e.getMessage(), module);
     		 return ServiceUtil.returnError(e.getMessage());
		}
    	 Map orderItemCtx = UtilMisc.toMap("userLogin",userLogin);
		   orderItemCtx.put("fromDate", UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
		   orderItemCtx.put("thruDate", UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
		   orderItemCtx.put("shedId", facilityId);
		  List<GenericValue>orderItems =getProcurementOrderItemsForPeriod(dctx ,orderItemCtx);
    	 if(UtilValidate.isEmpty(orderList) || UtilValidate.isEmpty(orderItems)){
    		  return ServiceUtil.returnError("no order to process for the time period===>"+customTimePeriodId);
    	 }
    	 orderDetail =EntityUtil.getFirst(orderList);
    	 try{
	    	 List conList=FastList.newInstance();
	    	 conList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
	    	 conList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderDetail.getString("orderId")));	          
	    	 conList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));	 
	    	 conList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, orderAdjustmentTypeId));	
	    	 conList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
			   EntityCondition orderCond = EntityCondition.makeCondition(conList, EntityOperator.AND);
			   List adjustmentList =delegator.findList("OrderHeaderAdjustmentAndAdjustmentType", orderCond, null, null, null,false);
			  if(UtilValidate.isNotEmpty(adjustmentList)){
				  Debug.logWarning("Adjustment Type=="+orderAdjustmentTypeId+"==Already Exists for center=="+facilityId+"["+centerCode+"]", module);
				  return ServiceUtil.returnError("Adjustment Type=="+orderAdjustmentTypeId+"==Already Exists for facility=="+facilityId);
			  }
    	 }catch(GenericEntityException g){
    		 Debug.logError(g.getMessage(), module);
     		 return ServiceUtil.returnError(g.getMessage());
    	 }
    	 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
    	 createOrderAdjustmentCtx.put("orderId", orderDetail.getString("orderId"));
    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);    	
    	 createOrderAdjustmentCtx.put("amount", amount);
    	 try{            	
     		resultMap = dispatcher.runSync("createOrderAdjustment", createOrderAdjustmentCtx);
     		
     		if (ServiceUtil.isError(resultMap)) {
                 Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(resultMap), module);
         		return ServiceUtil.returnError("There was an error while creating the adjustment: " + ServiceUtil.getErrorMessage(resultMap));          	            
             }    
     		 Map tempOrderAdjMap = FastMap.newInstance();
     		 tempOrderAdjMap.put("shedCode", shedCode);
     		 tempOrderAdjMap.put("unitCode", unitCode);
     		 tempOrderAdjMap.put("centerCode", centerCode);
     		 tempOrderAdjMap.put("orderDate", UtilDateTime.toDateString(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")),"dd/MM/yyyy"));
     		 tempOrderAdjMap.put("adjustmentType", orderAdjustmentTypeId);
     		 tempOrderAdjMap.put("amount", amount);           
     		resultMap.put("orderAdjMap", tempOrderAdjMap);     		
         }catch (GenericServiceException e) {
             Debug.logError(e, module);
             return ServiceUtil.returnError("There was an error while creating the adjustment: " +e.getMessage());
         }        
        return resultMap;
	}
   
   public static Map<String, Object> updateBillingAdjustment(DispatchContext dctx, Map<String, ? extends Object> context) {
  	 	LocalDispatcher dispatcher = dctx.getDispatcher();
  	 	Delegator delegator = dctx.getDelegator();
  	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
  	 	//String facilityId = (String)context.get("originFacilityId");
  	 	//Timestamp orderDate = (Timestamp)context.get("orderDate");
  	 	String amountStr = ((BigDecimal)context.get("amount")).toString();
  	 	String orderId = (String)context.get("orderId");
  	 	String orderAdjustmentId = (String)context.get("orderAdjustmentId");
  	 	String orderAdjustmentTypeId = (String)context.get("orderAdjustmentTypeId");
  	 	BigDecimal amount = BigDecimal.ZERO;
  	 	if(UtilValidate.isNotEmpty(amountStr)){
  	 		amountStr = amountStr.replace(",","");
  	 		amount = new BigDecimal(amountStr);
  	 	}
  	 	Map resultMap = ServiceUtil.returnSuccess();
  	 	List conditionList =FastList.newInstance();
  	 	GenericValue orderHeader = null;
   	 	try{
   	 		orderHeader =delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId),false);
   	 		if(UtilValidate.isEmpty(orderHeader)){
   	 			return ServiceUtil.returnError("Order Not found with orderId====>"+orderId);
   	 		}
   	 		String statusId = (String) orderHeader.get("statusId");
   	 		if(statusId.equalsIgnoreCase("ORDER_APPROVED")){
   	 			return ServiceUtil.returnError("You can not update the record once billing is generated");
   	 		}
		   	// if the adjustmentType parent is  deduction (MILKPROC_DEDUCTIONS) make the amount as negative value
		   	GenericValue  orderAdjustmentType =delegator.findOne("OrderAdjustmentType", UtilMisc.toMap("orderAdjustmentTypeId", orderAdjustmentTypeId),false);
		   	if((orderAdjustmentType.getString("parentTypeId")).equals("MILKPROC_DEDUCTIONS") ){
		   		if(amount.compareTo(BigDecimal.ZERO)>0){
		   			amount = amount.negate();			  
		   		}
		   	}
		   	GenericValue orderAdjustment= null;
		   	orderAdjustment = delegator.findOne("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId",orderAdjustmentId), false);
		   	orderAdjustment.set("amount", amount);
		   	orderAdjustment.set("orderAdjustmentTypeId",orderAdjustmentTypeId);
		   	orderAdjustment.store();
		  
   	 	}catch (GenericEntityException e) {
			// TODO: handle exception
    		 Debug.logError(e.getMessage(), module);
    		 return ServiceUtil.returnError(e.getMessage());
		}
   	 	
   	 	return ServiceUtil.returnSuccess("Adjustment updated successfully ");
	}
   

	public static Map<String, Object> batchRunMilkProcurementEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp orderDateTime = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("orderDate")).getTime()));
		Map randamMinMaxMap =FastMap.newInstance();
		randamMinMaxMap.put("0" ,UtilMisc.toMap("min", 10,"max",50));
		randamMinMaxMap.put("1" ,UtilMisc.toMap("min", 10,"max",45));
		randamMinMaxMap.put("2" ,UtilMisc.toMap("min", 10,"max",40));
		randamMinMaxMap.put("3" ,UtilMisc.toMap("min", 10,"max",25));
		randamMinMaxMap.put("4" ,UtilMisc.toMap("min", 10,"max",30));
		randamMinMaxMap.put("5" ,UtilMisc.toMap("min", 10,"max",35));
		Map resultMap = ServiceUtil.returnSuccess();
		List conditionList = FastList.newInstance();
		List<GenericValue> facilityList = FastList.newInstance();
		List productList = FastList.newInstance();
		List supplyTypeEnumList = FastList.newInstance();
		Timestamp nowTimestamp =UtilDateTime.nowTimestamp();
		Debug.logImportant("randamMinMaxMap============="+randamMinMaxMap, module);
		int minuteMod = (nowTimestamp.getMinutes())%6;
		Debug.logImportant("minuteMod============="+((Map)randamMinMaxMap.get(Integer.toString(minuteMod))).get("min"), module);
		int min = ((Integer)((Map)randamMinMaxMap.get(Integer.toString(minuteMod))).get("min")).intValue();
		int max = ((Integer)(((Map)randamMinMaxMap.get(Integer.toString(minuteMod))).get("max"))).intValue();
		try {
			/*EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(5);*/
			facilityList = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN,
							UtilMisc.toList("CENTER")), null, null,null, false);			
			productList = delegator.findList("Product", EntityCondition.makeCondition("primaryProductCategoryId",
							EntityOperator.EQUALS, "MILK_PROCUREMENT"), UtilMisc.toSet("productId" ,"description"),null, null, false);			
			
			supplyTypeEnumList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId",
					EntityOperator.EQUALS, "PROC_SUPPLY_TYPE"), UtilMisc.toSet("enumId"),null, null, false);
			supplyTypeEnumList =EntityUtil.getFieldListFromEntityList(supplyTypeEnumList, "enumId", false);

		} catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map createProcurementEntryCtx = UtilMisc.toMap("userLogin",userLogin);
		for(int j=0 ; j<supplyTypeEnumList.size() ; j++){		
			createProcurementEntryCtx.put("purchaseTime",supplyTypeEnumList.get(j));
			for(GenericValue facility : facilityList){
				if(UtilValidate.isEmpty(facility.getString("facilityCode"))){
					continue;
				}
				String centerCode = facility.getString("facilityCode");
				String unitCode =  (String)((GenericValue)(ProcurementNetworkServices.getCenterDtails(dctx,UtilMisc.toMap("centerId",facility.get("facilityId")))).get("unitFacility")).getString("facilityCode");
				//for each product
				
				createProcurementEntryCtx.put("centerCode", centerCode);
				createProcurementEntryCtx.put("unitCode", unitCode);
				createProcurementEntryCtx.put("orderDate", orderDateTime);
				
				for(int i=0 ; i<productList.size(); i++){
					GenericValue product = (GenericValue)productList.get(i);
					createProcurementEntryCtx.put("productId", product.getString("productId"));				
					Random r = new Random();
					int randomNumber = r.nextInt(max-min) + min;
					BigDecimal quantity = new BigDecimal(randomNumber);
					createProcurementEntryCtx.put("quantity", quantity);
					if((product.getString("description").toLowerCase()).contains("cow")){
						if(quantity.compareTo(new BigDecimal(20)) > 0){
							continue;
						}
						createProcurementEntryCtx.put("fat", new BigDecimal(3 +(5 - 3)* r.nextDouble()).setScale(1 ,BigDecimal.ROUND_HALF_UP));
						createProcurementEntryCtx.put("snf", new BigDecimal(8 +(8.5 - 8)* r.nextDouble()).setScale(1 ,BigDecimal.ROUND_HALF_UP));
					}else{
						createProcurementEntryCtx.put("fat", new BigDecimal(6 +(10 - 6)* r.nextDouble()).setScale(1 ,BigDecimal.ROUND_HALF_UP));
						createProcurementEntryCtx.put("snf", new BigDecimal(8.5 +(10 - 8.5)* r.nextDouble()).setScale(1 ,BigDecimal.ROUND_HALF_UP));
					}					
					if(randomNumber > 45){
						createProcurementEntryCtx.put("sQuantity", new BigDecimal(r.nextInt(max-min) + min));
						createProcurementEntryCtx.put("sFat", (new BigDecimal(4 +(6 - 4)* r.nextDouble())).setScale(1 ,BigDecimal.ROUND_HALF_UP));
					}
					try{            	
						resultMap = dispatcher.runSync("createProcurementEntry", createProcurementEntryCtx);
						if (ServiceUtil.isError(resultMap)) {
							Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(resultMap), module);
							return ServiceUtil.returnError("There was an error while creating the adjustment: " + ServiceUtil.getErrorMessage(resultMap));          	            
						}
						
		            }catch (GenericServiceException e) {
		             Debug.logError(e, module);
		             return ServiceUtil.returnError("There was an error while creating Procuremnet entry: " + e.getMessage());
		          }
					
				}//end of product
				
			}
		}	
		return ServiceUtil.returnSuccess(" successfully done.");
	}	
	
	public static Map<String, Object> batchRunMilkProcurementMigration(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp procurementFromDateTime = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("procurementFromDate")).getTime()));
		Timestamp procurementThruDateTime = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("procurementThruDate")).getTime()));
		List<GenericValue> entryList =FastList.newInstance();		
		Timestamp dayBegin = UtilDateTime.getDayStart(procurementFromDateTime);
		Timestamp dayEnd = UtilDateTime.getDayEnd(procurementThruDateTime);	
		try{
			entryList =delegator.findList("ProcurementMigration",EntityCondition.makeCondition(EntityCondition.makeCondition("ddate", EntityOperator.GREATER_THAN_EQUAL_TO  , dayBegin) ,EntityOperator.AND ,EntityCondition.makeCondition("ddate", EntityOperator.LESS_THAN_EQUAL_TO , dayEnd) ), null, null, null,false);
			Debug.logImportant("entryList===================="+entryList, module);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
            return ServiceUtil.returnError("There was an error while Doing Procuremnet Migration: " + e.getMessage());
		}
		Map createProcurementEntryCtx = UtilMisc.toMap("userLogin",userLogin);
		for(GenericValue entry : entryList){
				Map resultMap= ServiceUtil.returnSuccess();
				createProcurementEntryCtx.put("purchaseTime",entry.getString("daytyp"));
				createProcurementEntryCtx.put("shedCode", entry.getString("scode"));
				createProcurementEntryCtx.put("centerCode", entry.getString("ccode"));
				createProcurementEntryCtx.put("unitCode", entry.getString("ucode"));
				createProcurementEntryCtx.put("orderDate", entry.getTimestamp("ddate"));
				createProcurementEntryCtx.put("productId", entry.getString("typmlk"));
				createProcurementEntryCtx.put("quantity", entry.getBigDecimal("gqty"));
				createProcurementEntryCtx.put("fat", entry.getBigDecimal("gfat"));
				createProcurementEntryCtx.put("snf", entry.getBigDecimal("gsnf"));
				createProcurementEntryCtx.put("sQuantity", entry.getBigDecimal("sqty"));
				createProcurementEntryCtx.put("sFat", entry.getBigDecimal("sfat"));
				createProcurementEntryCtx.put("cQuantity", entry.getBigDecimal("cqty"));
				createProcurementEntryCtx.put("ptcMilkType", entry.getString("f"));
				createProcurementEntryCtx.put("ptcQuantity", entry.getBigDecimal("ptcQuantity"));
				if(UtilValidate.isNotEmpty(entry.getBigDecimal("gqtyLtrs"))){
					createProcurementEntryCtx.put("quantity", ProcurementNetworkServices.convertLitresToKG(entry.getBigDecimal("gqtyLtrs")));
				}
				try{            	
					resultMap = dispatcher.runSync("createProcurementEntry", createProcurementEntryCtx);
					if (ServiceUtil.isError(resultMap)) {
						Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(resultMap), module);
						return ServiceUtil.returnError("There was an error while creating the adjustment: " + ServiceUtil.getErrorMessage(resultMap));          	            
					}
					
	            }catch (GenericServiceException e) {
	             Debug.logError(e, module);
	             return ServiceUtil.returnError("There was an error while creating Procuremnet entry: " + e.getMessage());
	          }		
			
		}		   
		
		return ServiceUtil.returnSuccess("successfully done.");
	}
	
	/**
	 * creates record for sending milk
	 * @param dctx
	 * @param context
	 * @return
	 */
	 public static Map<String, Object>  createMilkTransferRecord(DispatchContext dctx, Map<String, ? extends Object> context)  {
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = FastMap.newInstance();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String facilityIdTo = (String) context.get("facilityIdTo");
			String facilityId = (String) context.get("facilityId");
			String customTimePeriodId = (String) context.get("customTimePeriodId");
			BigDecimal quantity = (BigDecimal) context.get("quantity");
			BigDecimal quantityLtrs = (BigDecimal) context.get("quantityLtrs");
			String productId = (String) context.get("productId");
			String containerId = ((String)context.get("containerId"));
			String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
			String isMilkRcpt = (String) context.get("isMilkRcpt");
			Boolean checkTimePeriod = Boolean.TRUE;
			if(UtilValidate.isNotEmpty(context.get("checkTimePeriod"))){
				checkTimePeriod = (Boolean)context.get("checkTimePeriod");
			}
			Timestamp sendDate = (Timestamp) context.get("sendDate");
			Timestamp receiveDate = (Timestamp) context.get("receiveDate");
			BigDecimal receivedFat=(BigDecimal) context.get("receivedFat");
			BigDecimal receivedSnf=(BigDecimal) context.get("receivedSnf");
			BigDecimal receivedQuantityLtrs=(BigDecimal) context.get("receivedQuantityLtrs");
			BigDecimal receivedQuantity=(BigDecimal) context.get("receivedQuantity");
			
			if(facilityId.equalsIgnoreCase(facilityIdTo)){
				Debug.logError("YOU CAN NOT MAKE THIS TRANSFER . REASON: From and Destination are same.", module);
				 return ServiceUtil.returnError("YOU CAN NOT MAKE THIS TRANSFER . REASON: facilityId, facilityIdTo are same.");
			}
			
			if(UtilValidate.isEmpty(isMilkRcpt) && facilityIdTo.equalsIgnoreCase("MAIN_PLANT")){
				Debug.logError("YOU CAN NOT MAKE THIS TRANSFER . REASON: Making MilkTransfers To MainPlant is disabled.", module);
				 return ServiceUtil.returnError("YOU CAN NOT MAKE THIS TRANSFER . REASON: Making MilkTransfers To MPF is disabled.");
			}
			
			if(UtilValidate.isNotEmpty(sendDate) && UtilValidate.isNotEmpty(customTimePeriodId)&& checkTimePeriod){
				try{
					GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false );
					Timestamp periodFromDate = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) customTimePeriod.get("fromDate")).getTime()));
					Timestamp periodThruDate = UtilDateTime.getDayEnd(UtilDateTime.getTimestamp(((java.sql.Date) customTimePeriod.get("thruDate")).getTime()));
					if(sendDate.before(periodFromDate)||sendDate.after(periodThruDate)){
						 Debug.logError("Transfer Date Out of TimePeriod ==>" +customTimePeriod.getDate("fromDate")+" to "+customTimePeriod.getDate("thruDate"), module);
						 return ServiceUtil.returnError("Transfer Date Out of TimePeriod ==>" +customTimePeriod.getDate("fromDate")+" to "+customTimePeriod.getDate("thruDate"));  
					}
				}catch(GenericEntityException e){
					Debug.logError("Error while getting CustomTimePeriod "+e.getMessage(), module);
					result = ServiceUtil.returnError("Error while getting CustomTimePeriod Details");
				}
			}
			
			if(UtilValidate.isEmpty(context.get("fat")) && UtilValidate.isEmpty(context.get("snf")) && UtilValidate.isEmpty(context.get("sendKgFat")) && UtilValidate.isEmpty(context.get("sendKgSnf"))){
				 Debug.logError("sendFat, sendSnf, sendKgFat,sendKgSnf are empty ==>"+facilityId, module);
				 return ServiceUtil.returnError("sendFat, sendSnf, sendKgFat,sendKgSnf are empty ==>"+facilityId);  
			}
			if(UtilValidate.isEmpty(quantityLtrs)&&UtilValidate.isNotEmpty(quantity)){
				quantityLtrs = ProcurementNetworkServices.convertKGToLitre(quantity);
			}
			BigDecimal fat =BigDecimal.ZERO;
			BigDecimal snf =BigDecimal.ZERO;
			BigDecimal kgFat =BigDecimal.ZERO;
			BigDecimal kgSnf =BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(context.get("sendKgFat")) && UtilValidate.isNotEmpty(context.get("sendKgSnf"))){
				kgFat = (BigDecimal) context.get("sendKgFat");
				kgSnf = (BigDecimal) context.get("sendKgSnf");
				fat = ProcurementNetworkServices.calculateFatOrSnf(kgFat, quantity);
				snf = ProcurementNetworkServices.calculateFatOrSnf(kgSnf, quantity);
			}else{
				 fat = (BigDecimal) context.get("fat");
				 snf = (BigDecimal) context.get("snf");
				 kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity,fat);
				 kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity,snf);
			}			
			
			BigDecimal sendLR =BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(context.get("sendLR"))){
				sendLR = (BigDecimal) context.get("sendLR");
			}
			
			if(UtilValidate.isNotEmpty(containerId)){
				containerId = containerId.toUpperCase();
			}
				try{
					GenericValue newTransferRecord = delegator.makeValue("MilkTransfer");
					newTransferRecord.put("statusId", "MXF_INPROCESS");
					newTransferRecord.put("facilityId", facilityId);
					newTransferRecord.put("facilityIdTo", facilityIdTo);
					newTransferRecord.put("fat", fat);
					newTransferRecord.put("snf", snf);
					newTransferRecord.put("productId", productId);
					newTransferRecord.put("containerId", containerId);
					newTransferRecord.put("createdByUserLogin", userLogin.get("userLoginId"));
					newTransferRecord.put("lastModifiedByUserLogin",userLogin.get("userLoginId"));
					newTransferRecord.put("sendDate", sendDate);
					newTransferRecord.put("quantity", quantity);
					newTransferRecord.put("quantityLtrs", quantityLtrs);
					newTransferRecord.put("sendKgFat", kgFat);
					newTransferRecord.put("sendKgSnf", kgSnf);
					newTransferRecord.put("supplyTypeEnumId", supplyTypeEnumId);
					if(UtilValidate.isNotEmpty(isMilkRcpt)){
						newTransferRecord.put("receivedFat",receivedFat);
						newTransferRecord.put("receivedSnf",receivedSnf);
						newTransferRecord.put("receivedKgFat",ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQuantity,receivedFat));
						newTransferRecord.put("receivedKgSnf",ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQuantity,receivedSnf));
						newTransferRecord.put("receivedQuantityLtrs",receivedQuantityLtrs);
						newTransferRecord.put("receivedQuantity",receivedQuantity);
						newTransferRecord.put("receiveDate",receiveDate);
						newTransferRecord.put("isMilkRcpt", isMilkRcpt);
						newTransferRecord.put("statusId", "MXF_RECD");
					}
					newTransferRecord.put("sendLR", sendLR);
					newTransferRecord.put("receiveDate", receiveDate);
					delegator.createSetNextSeqId(newTransferRecord);
					result = ServiceUtil.returnSuccess("Record created successfully");
					result.put("milkTransferId",newTransferRecord.get("milkTransferId"));
				}catch(GenericEntityException e){
							Debug.logError("Error while inserting sending details "+e.getMessage(), module);
							result = ServiceUtil.returnError("Error while inserting Sending Details");
				}
			
			return result;
	}//end of service
	 /**
	  * updates the milk Record  
	  * @param dctx
	  * @param context
	  * @return
	  */
	public static Map<String, Object>  updateMilkDetails(DispatchContext dctx, Map<String, ? extends Object> context)  {
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = FastMap.newInstance();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String status = (String) context.get("status");
			String milkType = (String) context.get("milkType");
			String facilityIdTo = (String) context.get("facilityIdTo");
			String facilityId = (String) context.get("facilityId");
			String milkTransferId = (String)context.get("milkTransferId");
			BigDecimal quantity = (BigDecimal) context.get("quantity");
			BigDecimal quantityLtrs = (BigDecimal) context.get("quantityLtrs");
			String productId = (String) context.get("productId");
			String containerId = ((String)context.get("containerId"));
			BigDecimal receivedQuantity = (BigDecimal) context.get("receivedQuantity");
			BigDecimal receivedQuantityLtrs = (BigDecimal) context.get("receivedQuantityLtrs");
			BigDecimal receivedLR =BigDecimal.ZERO;
			String isMilkRcpt = (String) context.get("isMilkRcpt");
			if(facilityId.equalsIgnoreCase(facilityIdTo)){
				 Debug.logError("YOU CAN NOT UPDATE THIS TRANSFER . REASON: From and Destination are same.", module);
				 return ServiceUtil.returnError("YOU CAN NOT UPDATE THIS TRANSFER . REASON: facilityId, facilityIdTo are same.");
			}
			if(UtilValidate.isEmpty(isMilkRcpt) && facilityIdTo.equalsIgnoreCase("MAIN_PLANT")){
				Debug.logError("YOU CAN NOT UPDATE THIS TRANSFER . REASON: Making MilkTransfers To MPF is disabled.", module);
				 return ServiceUtil.returnError("YOU CAN NOT UPDATE THIS TRANSFER . REASON: Making MilkTransfers To MPF is disabled.");
			}
			if(UtilValidate.isEmpty(quantityLtrs)&&UtilValidate.isNotEmpty(quantity)){
				quantityLtrs = ProcurementNetworkServices.convertKGToLitre(quantity);
			}
			if(UtilValidate.isEmpty(receivedQuantityLtrs)&&UtilValidate.isNotEmpty(receivedQuantity)){
				receivedQuantityLtrs = ProcurementNetworkServices.convertKGToLitre(receivedQuantity);
			}
			
			if(UtilValidate.isNotEmpty(context.get("receivedLR"))){
				receivedLR = (BigDecimal) context.get("receivedLR");
			}			
			Timestamp sendDate = (Timestamp) context.get("sendDate");
			Timestamp receiveDate = (Timestamp) context.get("receiveDate");
			BigDecimal gheeYield = (BigDecimal) context.get("gheeYield");
			BigDecimal sQuantityLtrs = (BigDecimal) context.get("sQuantityLtrs");
			BigDecimal sFat = BigDecimal.ZERO;
			BigDecimal sKgFat = BigDecimal.ZERO;
			BigDecimal sKgSnf = BigDecimal.ZERO;
			BigDecimal sSnf = BigDecimal.ZERO;
			BigDecimal cQuantityLtrs = (BigDecimal) context.get("cQuantityLtrs");
			GenericValue milkRecord = null;
			if(UtilValidate.isEmpty(cQuantityLtrs)){
				cQuantityLtrs = BigDecimal.ZERO;
			}
			if(UtilValidate.isEmpty(sQuantityLtrs)){	
				sQuantityLtrs = BigDecimal.ZERO;
			}
			if(containerId!=null){
				containerId = containerId.toUpperCase();
			}
			GenericValue container = null;
			try{
				if(UtilValidate.isNotEmpty(containerId)){
					container = delegator.findOne("Container",UtilMisc.toMap("containerId", containerId), false );
					if(UtilValidate.isEmpty(container)){
						GenericValue newContainer = delegator.makeValue("Container");	
							newContainer.put("containerId", containerId);
							newContainer.put("facilityId", facilityId);
							delegator.createOrStore(newContainer);
					}
				}
				milkRecord = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", milkTransferId), false );
				if(UtilValidate.isNotEmpty(status) && status.equals("approved")){	
					if(UtilValidate.isEmpty(context.get("receivedFat")) && UtilValidate.isEmpty(context.get("receivedSnf")) && UtilValidate.isEmpty(context.get("receivedKgFat")) && UtilValidate.isEmpty(context.get("receivedKgSnf"))){
						 Debug.logError("receivedFat, receivedSnf, receivedKgSnf,receivedKgSnf are empty ==>"+facilityId, module);
				     		return ServiceUtil.returnError("receivedFat, receivedSnf, receivedKgSnf,receivedKgSnf are empty ==>"+facilityId);  
					}
					BigDecimal receivedFat =BigDecimal.ZERO;
					BigDecimal receivedSnf =BigDecimal.ZERO;
					BigDecimal receivedKgFat =BigDecimal.ZERO;
					BigDecimal receivedKgSnf =BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(context.get("receivedKgFat")) && UtilValidate.isNotEmpty(context.get("receivedKgSnf"))){
						receivedKgFat = (BigDecimal) context.get("receivedKgFat");
						receivedKgSnf = (BigDecimal) context.get("receivedKgSnf");
						receivedFat = ProcurementNetworkServices.calculateFatOrSnf(receivedKgFat, receivedQuantity);
						receivedSnf = ProcurementNetworkServices.calculateFatOrSnf(receivedKgSnf, receivedQuantity);
					}else{
						receivedFat = (BigDecimal) context.get("receivedFat");
						receivedSnf = (BigDecimal) context.get("receivedSnf");
						receivedKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQuantity,receivedFat);
						receivedKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQuantity,receivedSnf);
					}
					BigDecimal sQuantityKgs =BigDecimal.ZERO;
					if(sQuantityLtrs.compareTo(BigDecimal.ZERO)>0){
						sQuantityKgs = ProcurementNetworkServices.convertLitresToKG(sQuantityLtrs);
						if(UtilValidate.isNotEmpty(context.get("sKgFat"))){
							sKgFat = (BigDecimal) context.get("sKgFat");
							sFat = ProcurementNetworkServices.calculateFatOrSnf(sKgFat, sQuantityKgs);
						}
						if(UtilValidate.isNotEmpty(context.get("sFat"))){
							sFat = (BigDecimal) context.get("sFat");
							sKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQuantityKgs,sFat);
						}
						if(UtilValidate.isNotEmpty(context.get("sKgSnf"))){
							sKgSnf = (BigDecimal) context.get("sKgSnf");
							sSnf = ProcurementNetworkServices.calculateFatOrSnf(sKgSnf, sQuantityKgs);
						}
						if(UtilValidate.isNotEmpty(context.get("sSnf"))){
							sSnf = (BigDecimal) context.get("sSnf");
							sKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQuantityKgs,sSnf);
						}
					}
					milkRecord.set("receivedQuantity", receivedQuantity);
					milkRecord.set("receivedQuantityLtrs", receivedQuantityLtrs);
					milkRecord.set("milkType", milkType);
					milkRecord.set("receivedFat", receivedFat);
					milkRecord.set("receivedSnf", receivedSnf);
					milkRecord.set("receivedKgFat", receivedKgFat);
					milkRecord.set("receivedKgSnf",receivedKgSnf);
					milkRecord.set("receivedLR",receivedLR);
					milkRecord.set("receiveDate",receiveDate);
					milkRecord.set("sQuantityLtrs", sQuantityLtrs);
					milkRecord.set("sFat", sFat);
					milkRecord.set("sKgFat", sKgFat);
					milkRecord.set("sKgSnf", sKgSnf);
					milkRecord.set("sSnf", sSnf);
					milkRecord.set("gheeYield",gheeYield);
					milkRecord.set("cQuantityLtrs", cQuantityLtrs);
					milkRecord.set("createdByUserLogin", userLogin.get("userLoginId"));
					milkRecord.set("statusId", "MXF_RECD");
					delegator.createOrStore(milkRecord);
					result = ServiceUtil.returnSuccess("Transfer approved successfully for milkTransferId:"+milkTransferId);
					//update milk transfer(received Milk) record
			    }else if(UtilValidate.isNotEmpty(status) && status.equals("MXF_RECD")){
					    	if(UtilValidate.isEmpty(context.get("receivedFat")) && UtilValidate.isEmpty(context.get("receivedSnf")) && UtilValidate.isEmpty(context.get("receivedKgFat")) && UtilValidate.isEmpty(context.get("receivedKgSnf"))){
								 Debug.logError("receivedFat, receivedSnf, receivedKgFat,receivedKgSnf are empty ==>"+facilityId, module);
						     		return ServiceUtil.returnError("receivedFat, receivedSnf, receivedKgFat,receivedKgSnf are empty ==>"+facilityId);  
							}
							BigDecimal receivedFat =BigDecimal.ZERO;
							BigDecimal receivedSnf =BigDecimal.ZERO;
							BigDecimal receivedKgFat =BigDecimal.ZERO;
							BigDecimal receivedKgSnf =BigDecimal.ZERO;
							if(UtilValidate.isNotEmpty(context.get("receivedKgFat")) && UtilValidate.isNotEmpty(context.get("receivedKgSnf"))){
								receivedKgFat = (BigDecimal) context.get("receivedKgFat");
								receivedKgSnf = (BigDecimal) context.get("receivedKgSnf");
								receivedFat = ProcurementNetworkServices.calculateFatOrSnf(receivedKgFat, receivedQuantity);
								receivedSnf = ProcurementNetworkServices.calculateFatOrSnf(receivedKgSnf, receivedQuantity);
							}else{
								receivedFat = (BigDecimal) context.get("receivedFat");
								receivedSnf = (BigDecimal) context.get("receivedSnf");
								receivedKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQuantity,receivedFat);
								receivedKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQuantity,receivedSnf);	
							}
							BigDecimal sQuantityKgs =BigDecimal.ZERO;
							if(sQuantityLtrs.compareTo(BigDecimal.ZERO)>0){
								sQuantityKgs = ProcurementNetworkServices.convertLitresToKG(sQuantityLtrs);
								if(UtilValidate.isNotEmpty(context.get("sKgFat"))){
									sKgFat = (BigDecimal) context.get("sKgFat");
									sFat = ProcurementNetworkServices.calculateFatOrSnf(sKgFat, sQuantityKgs);
								}
								if(UtilValidate.isNotEmpty(context.get("sFat"))){
									sFat = (BigDecimal) context.get("sFat");
									sKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQuantityKgs,sFat);
								}
								if(UtilValidate.isNotEmpty(context.get("sKgSnf"))){
									sKgSnf = (BigDecimal) context.get("sKgSnf");
									sSnf = ProcurementNetworkServices.calculateFatOrSnf(sKgSnf, sQuantityKgs);
								}
								if(UtilValidate.isNotEmpty(context.get("sSnf"))){
									sSnf = (BigDecimal) context.get("sSnf");
									sKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQuantityKgs,sSnf);
								}
							}
							milkRecord.set("facilityIdTo", facilityIdTo);
					    	milkRecord.set("receivedQuantity", receivedQuantity);
					    	milkRecord.set("receivedQuantityLtrs", receivedQuantityLtrs);
							milkRecord.set("receivedFat", receivedFat);
							milkRecord.set("receivedSnf", receivedSnf);
							milkRecord.set("receivedKgFat", receivedKgFat);
							milkRecord.set("receivedKgSnf", receivedKgSnf);
							milkRecord.set("sQuantityLtrs", sQuantityLtrs);
							milkRecord.set("sFat", sFat);
							milkRecord.set("sKgFat", sKgFat);
							milkRecord.set("sKgSnf", sKgSnf);
							milkRecord.set("sSnf", sSnf);
							milkRecord.set("cQuantityLtrs", cQuantityLtrs);
							milkRecord.set("receiveDate",receiveDate);
							milkRecord.set("containerId",containerId);
							milkRecord.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				            delegator.createOrStore(milkRecord);
				            result = ServiceUtil.returnSuccess("Details updated successfully");
			    }else{
			    	   //update milk transfer(send Milk) record
				    	if(UtilValidate.isEmpty(context.get("fat")) && UtilValidate.isEmpty(context.get("snf")) && UtilValidate.isEmpty(context.get("sendKgFat")) && UtilValidate.isEmpty(context.get("sendKgSnf"))){
							 Debug.logError("sendFat, sendSnf, sendKgFat,sendKgSnf are empty ==>"+facilityId, module);
					     		return ServiceUtil.returnError("sendFat, sendSnf, sendKgFat,sendKgSnf are empty ==>"+facilityId);  
						}
						BigDecimal fat =BigDecimal.ZERO;
						BigDecimal snf =BigDecimal.ZERO;
						BigDecimal kgFat =BigDecimal.ZERO;
						BigDecimal kgSnf =BigDecimal.ZERO;
						
						if(UtilValidate.isNotEmpty(context.get("sendKgFat")) && UtilValidate.isNotEmpty(context.get("sendKgSnf"))){
							kgFat = (BigDecimal) context.get("sendKgFat");
							kgSnf = (BigDecimal) context.get("sendKgSnf");
							fat = ProcurementNetworkServices.calculateFatOrSnf(kgFat, quantity);
							snf = ProcurementNetworkServices.calculateFatOrSnf(kgSnf, quantity);
						}else{
							fat = (BigDecimal) context.get("fat");
							snf = (BigDecimal) context.get("snf");
							kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity,fat);
							kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity,snf);	
						}
				    	
						milkRecord.set("facilityIdTo", facilityIdTo);
				    	milkRecord.set("quantity", quantity);
				    	milkRecord.set("quantityLtrs", quantityLtrs);
						milkRecord.set("fat", fat);
						milkRecord.set("snf", snf);
						milkRecord.set("sendKgFat", kgFat);
						milkRecord.set("sendKgSnf", kgSnf);
						milkRecord.set("sendDate",sendDate);
						milkRecord.set("productId",productId);
						milkRecord.set("containerId",containerId);
						milkRecord.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						delegator.createOrStore(milkRecord);
			            result = ServiceUtil.returnSuccess("Details updated successfully");
			     }	
			}catch(GenericEntityException e){
				Debug.logError("error while updating Details : "+e.getMessage(), containerId);
				result = ServiceUtil.returnError("Error while updating  Details"); 
			}
			String containsSourAdviceValue = (String)context.get("containsSourAdviceValue");
			Map sourAdviceResultMap = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(containsSourAdviceValue) && (containsSourAdviceValue.equalsIgnoreCase("true"))){
					Map sourDistInMap = FastMap.newInstance();
					sourDistInMap.put("milkTransferId", milkTransferId);
					sourDistInMap.put("userLogin",userLogin);
					List adviceValueList = FastList.newInstance();
					for(int i=0;i<10;i++){
						if(UtilValidate.isNotEmpty(context.get("facilityId_o_"+i))){
							Map adviceValueMap = FastMap.newInstance();
							adviceValueMap.put("facilityId",(String)context.get("facilityId_o_"+i));
							//adviceValueMap.put("quantity",(BigDecimal)context.get("quantity_o_"+i));
							if(!adviceValueList.contains(adviceValueMap)){
								adviceValueList.add(adviceValueMap);
							}
						}
					}
					sourDistInMap.put("adviceValueList",adviceValueList);
				try{
					sourAdviceResultMap = dispatcher.runSync("addSourDistributionAdvice", sourDistInMap);
				}catch (GenericServiceException e) {
					// TODO: handle exception
					Debug.logError("Error while adding SourDistribution Advice"+e,module);
					result = ServiceUtil.returnError("Error while adding SourDistribution Advice :"+e.getMessage());
				}
			}
			if(ServiceUtil.isError(sourAdviceResultMap)){
				Debug.logError("Error while adding SourDistributionAdvice :"+sourAdviceResultMap,module);
				result = ServiceUtil.returnError("Error while adding SourDistributionAdvice :"+ServiceUtil.getErrorMessage(sourAdviceResultMap));
			}
			return result;
	}// end of service

	 /**
	  * Deletes the milk record by using milkTransferId
	  * @param dctx
	  * @param context
	  * @return
	  */
	public static Map<String, Object>  deleteMilkRecord(DispatchContext dctx, Map<String, ? extends Object> context)  {
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = FastMap.newInstance();	
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String milkTransferId = (String) context.get("milkTransferId");
			GenericValue milkRecord =null;				
			try{				
				 List<GenericValue> milkTransferAdviceList = delegator.findList("MilkTransferAdvice", EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS, milkTransferId), null, null, null, false);
				 List<GenericValue> milkTransferList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS, milkTransferId), null, null, null, false); 
	    		 if(UtilValidate.isNotEmpty(milkTransferList)){
	    			 delegator.removeAll(milkTransferList);
	    		 }
	    		 if(UtilValidate.isNotEmpty(milkTransferAdviceList)){
	    			 delegator.removeAll(milkTransferAdviceList);
	    		 }
				 milkRecord = delegator.findOne("MilkTransfer", false, UtilMisc.toMap("milkTransferId",milkTransferId));
				 if(UtilValidate.isNotEmpty(milkRecord)){
					 milkRecord.remove(); 
				 }				
			}catch(GenericEntityException e){
				Debug.logError("error while removing  Record"+e.getMessage(), module);
				result = ServiceUtil.returnError("Error while removing record"); 
			}
			result = ServiceUtil.returnSuccess("Record Removed Successfully");
			return result;
	}// end of service
	
	public static Map<String, Object>  createProcFaciltiyOutputEntry(DispatchContext dctx, Map<String, ? extends Object> context)  {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();	
		GenericValue userLogin = (GenericValue) context.get("userLogin");   
		String shedCode = (String) context.get("shedCode");	       
	    String unitCode = (String) context.get("unitCode");
	    String facilityId = (String)(ProcurementNetworkServices.getFacilityByShedAndUnitCodes(dctx, context)).get("facilityId");
	    if(UtilValidate.isEmpty(facilityId)){
	   		 Debug.logError("Unit Not found with Code ==>"+facilityId, module);
	     		return ServiceUtil.returnError("Unit Not found with Id ==>"+facilityId);        	
	   		
	   	}
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		
		/*Map periodValidationMap = FastMap.newInstance();
     	Map checkProcPeriodBillingInMap = FastMap.newInstance();
     	checkProcPeriodBillingInMap.put("customTimePeriodId", customTimePeriodId);
     	checkProcPeriodBillingInMap.put("facilityId", facilityId);
     	checkProcPeriodBillingInMap.put("userLogin", userLogin);
     	
     	periodValidationMap = checkProcPeriodBilling(dctx,checkProcPeriodBillingInMap);
     	if(ServiceUtil.isError(periodValidationMap)){
     		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(periodValidationMap));
     	}*/
		
		String outputTypeId = (String) context.get("outputTypeId");
		BigDecimal quantity = (BigDecimal) context.get("qty");
		BigDecimal quantityLtrs = (BigDecimal) context.get("quantityLtrs");
		BigDecimal kgFat = (BigDecimal) context.get("kgFat");
		BigDecimal kgSnf = (BigDecimal) context.get("kgSnf");
		BigDecimal fatQty = (BigDecimal) context.get("fat");
		BigDecimal lactoReading = (BigDecimal) context.get("lactoReading");
		BigDecimal snfQty = null;
   	 	
		if(UtilValidate.isEmpty(quantityLtrs)){ 
			quantityLtrs = ProcurementNetworkServices.convertKGToLitre(quantity);
		}
		
		List<GenericValue> procProductList = ProcurementNetworkServices.getProcurementProducts(dctx, context);
		GenericValue productMap =(GenericValue)EntityUtil.getFirst(procProductList) ;
       	String productId = (String)productMap.get("productId"); 
		
       	// calculation of Snf from Lacto reading
        Map<String, Object> priceResult;     	
        Map<String, Object> priceContext = FastMap.newInstance();
        priceContext.put("productId", productId);
        priceContext.put("facilityId", facilityId);
		if(UtilValidate.isNotEmpty(lactoReading)){
       	 priceContext.put("snfPercent",BigDecimal.ZERO);
       	 priceResult = PriceServices.getProcurementProductPrice(dctx, priceContext);
            if (ServiceUtil.isError(priceResult)) {
                Debug.logWarning("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult), module);
        		return ServiceUtil.returnError("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));          	            
            } 
           String uomId =(String) priceResult.get("uomId");
           String acctgFormulaId = (String) priceResult.get("lrFormulaId");
        // use accounting formula if exists
       	Map inputMap  = FastMap.newInstance();
       	inputMap.put("userLogin", userLogin);
       	inputMap.put("fatQty", fatQty);
       	inputMap.put("lactoReading", lactoReading);
       	inputMap.put("acctgFormulaId", acctgFormulaId);
       	Map snfFromLR = ProcurementNetworkServices.getSnfFromLactoReading(dctx,inputMap);
       	snfQty = (BigDecimal)snfFromLR.get("snfQty");
        }else{
       	 	if(UtilValidate.isNotEmpty(fatQty) && UtilValidate.isNotEmpty(snfQty)){
       		   lactoReading = ProcurementNetworkServices.convertFatSnfToLR(fatQty, snfQty);
       	 	}
        }
        if(UtilValidate.isEmpty(kgFat) && UtilValidate.isEmpty(kgSnf)){
        	kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity,fatQty);
    		kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity,snfQty);
        }
		try{		
			GenericValue newOutputEntryRecord = delegator.makeValue("ProcFacilityOutput");	
			if(!("SOUR".equals(outputTypeId)) && !("CURDLED".equals(outputTypeId))){
				newOutputEntryRecord.put("facilityId", facilityId);
				newOutputEntryRecord.put("customTimePeriodId", customTimePeriodId);
				newOutputEntryRecord.put("outputTypeId", outputTypeId);
				newOutputEntryRecord.put("qty", quantity);
				newOutputEntryRecord.put("kgFat", kgFat);
				newOutputEntryRecord.put("kgSnf", kgSnf);				
				newOutputEntryRecord.put("quantityLtrs", quantityLtrs);
				newOutputEntryRecord.put("fat", fatQty);
				newOutputEntryRecord.put("snf", snfQty);
				newOutputEntryRecord.put("lactoReading", lactoReading);
				
			}else if("SOUR".equals(outputTypeId)){
				newOutputEntryRecord.put("facilityId", facilityId);
				newOutputEntryRecord.put("customTimePeriodId", customTimePeriodId);
				newOutputEntryRecord.put("outputTypeId", outputTypeId);
				if(UtilValidate.isEmpty(kgSnf)){
					kgSnf =BigDecimal.ZERO;
				}				
				newOutputEntryRecord.put("qty", quantity);
				newOutputEntryRecord.put("kgFat", kgFat);
				newOutputEntryRecord.put("kgSnf", kgSnf);
				newOutputEntryRecord.put("quantityLtrs", quantityLtrs);
			}else if("CURDLED".equals(outputTypeId)){	
				newOutputEntryRecord.put("facilityId", facilityId);
				newOutputEntryRecord.put("customTimePeriodId", customTimePeriodId);
				newOutputEntryRecord.put("outputTypeId", outputTypeId);
				if(UtilValidate.isEmpty(kgFat)){
					kgFat =BigDecimal.ZERO;
				}
				if(UtilValidate.isEmpty(kgSnf)){
					kgSnf =BigDecimal.ZERO;
				}
				newOutputEntryRecord.put("qty", quantity);
				newOutputEntryRecord.put("kgFat", kgFat);
				newOutputEntryRecord.put("kgSnf", kgSnf);
				newOutputEntryRecord.put("quantityLtrs", quantityLtrs);				
			}
			newOutputEntryRecord.put("createdByUserLogin", userLogin.get("userLoginId"));
			newOutputEntryRecord.put("createdDate", UtilDateTime.nowTimestamp());
			delegator.create(newOutputEntryRecord);
			result = ServiceUtil.returnSuccess("Record created successfully");
		}catch(GenericEntityException e){
					Debug.logError("Error while inserting sending details "+e.getMessage(), module);
					result = ServiceUtil.returnError("Entry is already existed for the period :"+customTimePeriodId);
		}
		return result;
	}
	

	/**
	 * 
	 * Service for Populating ProcurementAbstract for Shed 
	 * @param dctx
	 * @param context
	 * @param shedId,CustomTimePeriod,PeriodBillingId
	 * @return
	 * 
	 */
	public static Map<String, Object> populateProcAbstFromFacilityComissionsByShed(DispatchContext dctx, Map<String, ? extends Object> context){
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
	    String periodBillingId = (String) context.get("periodBillingId");
		String shedId = (String) context.get("shedId");
		List unitsList = FastList.newInstance();
		try {
			if(UtilValidate.isEmpty(shedId)||UtilValidate.isEmpty(customTimePeriodId)){
				Debug.logError("ShedId or customTimePeriod is Empty",module);
				return ServiceUtil.returnError("ShedId or customTimePeriod is Empty");
			}
			unitsList = (List)(ProcurementNetworkServices.getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",shedId))).get("unitsList");
			if(UtilValidate.isEmpty(unitsList)){
				Debug.logError("units not found for shed",module);
				return ServiceUtil.returnError("units not found for shed");
			}
			Map populateProcAbstMap= FastMap.newInstance();
			populateProcAbstMap.put("periodBillingId", periodBillingId);
			populateProcAbstMap.put("customTimePeriodId", customTimePeriodId);
			populateProcAbstMap.put("userLogin", userLogin);
			for(Object unit : unitsList){
				String unitId = (String)unit;
				populateProcAbstMap.put("unitId",unitId);
				Map populateAbstractResult = FastMap.newInstance();
				populateAbstractResult = populateProcuementAbstractFromFacilityComissions(dctx, populateProcAbstMap);
				if(ServiceUtil.isError(populateAbstractResult)){
					Debug.logError("Error while populating for unit "+unitId+"  : ErrorMessage :"+ServiceUtil.getErrorMessage(populateAbstractResult),module);
					continue;
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error while populating abstract by Shed ::"+e, module);
			result = ServiceUtil.returnError("Error while populating abstract by Shed ::"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 
	 * service for populating ProcurementAbstract
	 * @param dctx
	 * @param context
	 * @param unitId,PeriodBillingId,CustomTimePeriod
	 * @return
	 * In this Service We are storing totalQtyLtrs(qtyLtrs+sQtyLtrs) as qtyLtrs and totalQtyKgs(qtyKgs+sQtyKgs) as qtyKgs to PocurementAbstract Entity
	 */
	
	public static Map<String, Object> populateProcuementAbstractFromFacilityComissions(DispatchContext dctx, Map<String, ? extends Object> context){
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
	    String periodBillingId = (String) context.get("periodBillingId");
		String unitId = (String) context.get("unitId");
		GenericValue customTimePeriod = null;
		GenericValue periodBilling = null;
		Timestamp fromDateTime = UtilDateTime.nowTimestamp();
		Timestamp thruDateTime = UtilDateTime.nowTimestamp();
		Map finAccountMap = FastMap.newInstance();
		GenericValue unitFinAccountMap = null;
		GenericValue billingFacilityDetails = null;
		List<GenericValue> facilityProcCommissionList = FastList.newInstance();
		Map centerWiseValuesMap = FastMap.newInstance();
		
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if (UtilValidate.isEmpty(customTimePeriod)) {
				Debug.logError("There no active billing time periods. ", module);
				return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
			}
		    if(UtilValidate.isNotEmpty(periodBillingId)){
		    	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
		    	if (UtilValidate.isEmpty(periodBilling) || (UtilValidate.isNotEmpty(periodBilling.get("statusId"))&&(("COM_CANCELLED".equalsIgnoreCase((String)periodBilling.get("statusId")))))) {
					Debug.logError("invalid period billing. ", module);
					return ServiceUtil.returnError("invalid period billing.");
				}
		    	
		    }else{
		    	List condList = FastList.newInstance();
				List periodBillingList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,customTimePeriodId));
				condList.add(EntityCondition.makeCondition("billingTypeId",EntityOperator.EQUALS,"PB_PROC_MRGN"));
				condList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"GENERATED"));
				condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,unitId));
				EntityCondition procConditon = EntityCondition.makeCondition(condList,EntityJoinOperator.AND);
				periodBillingList = delegator.findList("CustomTimePeriodAndBilling", procConditon, null, null, null, false);
				
				if (UtilValidate.isEmpty(periodBillingList)) {
					Debug.logError("invalid period billing. ", module);
					return ServiceUtil.returnError("invalid period billing.");
				}
				periodBilling = EntityUtil.getFirst(periodBillingList);
		    }
		    periodBillingId = periodBilling.getString("periodBillingId");
			fromDateTime = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			thruDateTime = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
			
			Map finAccInMap = FastMap.newInstance();
			finAccInMap.put("userLogin", userLogin);
 	 		finAccInMap.put("facilityId", unitId);
			finAccountMap = (Map)((Map)(ProcurementNetworkServices.getShedFacilityFinAccount(dctx,finAccInMap)).get("facAccntsMap"));
			billingFacilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId",unitId), false);
			List procCommissionCondList = FastList.newInstance();
			procCommissionCondList.add(EntityCondition.makeCondition("periodBillingId",EntityOperator.EQUALS,periodBillingId));
			EntityCondition procCommConditon = EntityCondition.makeCondition(procCommissionCondList,EntityJoinOperator.AND);
			facilityProcCommissionList = delegator.findList("FacilityCommissionProc", procCommConditon, null, null, null, false);
			if(UtilValidate.isEmpty(facilityProcCommissionList)){
				Debug.logError("No Order to Process for facility  ===========>"+unitId+" and for BillingId ====="+periodBillingId,module);
				return ServiceUtil.returnError("No Order to Process for facility  ===========>"+unitId+" and for BillingId ====="+periodBillingId );
			}
			
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		// here populate product price calculation map to figure out  to use total solids or kgfat for cartage and comission
		List<GenericValue> procProductList = ProcurementNetworkServices.getProcurementProducts(dctx, context);
		Map orderAdjustmentTypeMapping = FastMap.newInstance();
		Map facilityPeriodTotals = FastMap.newInstance();
		int count = 0;
		try{
			//here we are querying OrderAdjustmentTypeProcAbstractMapping
			List<GenericValue> orderAdjumentMapList = FastList.newInstance();
			orderAdjumentMapList = delegator.findList("OrderAdjustmentTypeProcAbstractMapping", null, null, null, null, false);
			if(UtilValidate.isNotEmpty(orderAdjumentMapList)){
				for(GenericValue orderAdjustmentMap : orderAdjumentMapList){
					orderAdjustmentTypeMapping.put((String)orderAdjustmentMap.get("orderAdjustmentTypeId"), orderAdjustmentMap.get("procAbstractFieldName"));
				}
				
			}
			Map inputMap = FastMap.newInstance();
			inputMap.put("facilityId", unitId);
			inputMap.put("userLogin", userLogin);
			inputMap.put("fromDate", fromDateTime);
			inputMap.put("thruDate", thruDateTime);
			inputMap.put("includeCenterTotals", Boolean.TRUE);
			facilityPeriodTotals = ProcurementReports.getPeriodTotals(dctx, inputMap);
			
			if(ServiceUtil.isError(facilityPeriodTotals)){
				Debug.logError("Error while populating Period Totals==="+ServiceUtil.getErrorMessage(facilityPeriodTotals),module);
				return ServiceUtil.returnError("Error while populating Period Totals==="+ServiceUtil.getErrorMessage(facilityPeriodTotals));
			}
			centerWiseValuesMap = (Map)facilityPeriodTotals.get("centerWiseTotals");
			
			
		}catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError("Error While getting Fields Mapping for Abstract......."+e,module);
			return ServiceUtil.returnError("Error While getting Fields Mapping for Abstract......."+e.getMessage());
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error while getting PeriodTotals========"+e,module);
			return ServiceUtil.returnError("Error While getting Period Totals ......"+e.getMessage());
		}
		
		Set<String> adjustmentsPopulated = new HashSet();
		for(int i=0; i < facilityProcCommissionList.size();i++){
			GenericValue facilityCommission = (GenericValue)facilityProcCommissionList.get(i);
			String facilityId = (String)facilityCommission.get("facilityId");
			String productId = (String)facilityCommission.get("productId");
			GenericValue procurementAbstract = null;
			try{
				procurementAbstract = delegator.findOne("ProcurementAbstract", UtilMisc.toMap("periodBillingId", periodBillingId, "fromDate", fromDateTime,"thruDate", thruDateTime, "facilityId", facilityId, "productId",	productId), false);
				if(UtilValidate.isNotEmpty(procurementAbstract)){
					Debug.logError("Already abstract populated for "+facilityId+"  for  BillingId "+periodBillingId,module);
					continue;
				}
				
				// here let's populate ProcurementAbstract
				procurementAbstract = delegator.makeValue("ProcurementAbstract");
				procurementAbstract.putAll(initProcurementAbstract());
				
				Map centerFacilityPeriodTotals = FastMap.newInstance();
				Map facilityTotals = FastMap.newInstance();
				centerFacilityPeriodTotals = (Map)centerWiseValuesMap.get(facilityId);
				
				Map supplyTypeMap = FastMap.newInstance();
				Map productTotalsMap = FastMap.newInstance();
				Map tipQuantityMap = FastMap.newInstance();
				tipQuantityMap.put("kgFat",BigDecimal.ZERO);
				tipQuantityMap.put("kgSnf",BigDecimal.ZERO);
				if(UtilValidate.isNotEmpty(centerFacilityPeriodTotals)){
					facilityTotals = (Map)((Map)centerFacilityPeriodTotals.get("dayTotals")).get("TOT");
					Map supplyTypeTotals = FastMap.newInstance();
					Map centerTotalValues = FastMap.newInstance();
					Iterator supplyTypeIter = facilityTotals.entrySet().iterator();
					while (supplyTypeIter.hasNext()) {
						Map.Entry supplyTypeEntry = (Entry) supplyTypeIter.next();
						String supplyType = (String) supplyTypeEntry.getKey();
						Map productWiseMap = (Map)supplyTypeEntry.getValue();
						
						if (!(supplyType.equalsIgnoreCase("TOT")) ) {
							Map productTotalMap = FastMap.newInstance();
							Iterator prodTypeIter = productWiseMap.entrySet().iterator();
							while (prodTypeIter.hasNext()) {
								Map.Entry prodTypeEntry = (Entry) prodTypeIter.next();
								String prodType = (String) prodTypeEntry.getKey();
								if(!(prodType.equalsIgnoreCase("TOT"))){
									List<GenericValue> productDetailsList = FastList.newInstance();
									try {
										productDetailsList = EntityUtil.filterByCondition(procProductList,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
										if(UtilValidate.isNotEmpty(productDetailsList)){
											for (GenericValue productDetails : productDetailsList) {
												String procProductType = (String)productDetails.get("productName"); 
												if(procProductType.equalsIgnoreCase(prodType)){
													Map tempProductQtyMap = FastMap.newInstance();
													tempProductQtyMap = (Map) productWiseMap.get(prodType);
													if(UtilValidate.isNotEmpty(tempProductQtyMap)){
														procurementAbstract.put("qtyLtrs", ((BigDecimal)procurementAbstract.get("qtyLtrs")).add(((BigDecimal)(tempProductQtyMap.get("qtyLtrs"))).add(((BigDecimal)(tempProductQtyMap.get("sQtyLtrs"))))));
														procurementAbstract.put("sQtyKgs", ((BigDecimal)procurementAbstract.get("sQtyKgs")).add(((BigDecimal)tempProductQtyMap.get("sQtyKgs"))));
														procurementAbstract.put("cQtyLtrs", ((BigDecimal)procurementAbstract.get("cQtyLtrs")).add(((BigDecimal)tempProductQtyMap.get("cQtyLtrs"))));
														procurementAbstract.put("ptcCurd", ((BigDecimal)procurementAbstract.get("ptcCurd")).add(((BigDecimal)tempProductQtyMap.get("ptcQtyLtrs"))));
														procurementAbstract.put("qtyKgs", ((BigDecimal)procurementAbstract.get("qtyKgs")).add(((BigDecimal)(tempProductQtyMap.get("qtyKgs"))).add(((BigDecimal)(tempProductQtyMap.get("sQtyKgs"))))));
														tipQuantityMap.put("kgFat", ((BigDecimal)tipQuantityMap.get("kgFat")).add(((BigDecimal)tempProductQtyMap.get("kgFat"))).subtract(((BigDecimal)tempProductQtyMap.get("zeroKgFat"))));
														tipQuantityMap.put("kgSnf", ((BigDecimal)tipQuantityMap.get("kgSnf")).add(((BigDecimal)tempProductQtyMap.get("kgSnf"))).subtract(((BigDecimal)tempProductQtyMap.get("zeroKgSnf"))));
														if(supplyType.equalsIgnoreCase("AM")){
															procurementAbstract.put("amQtyLtrs", ((BigDecimal)procurementAbstract.get("amQtyLtrs")).add(((BigDecimal)(tempProductQtyMap.get("qtyLtrs"))).add(((BigDecimal)(tempProductQtyMap.get("sQtyLtrs"))))));
														}else{
															procurementAbstract.put("pmQtyLtrs", ((BigDecimal)procurementAbstract.get("pmQtyLtrs")).add(((BigDecimal)(tempProductQtyMap.get("qtyLtrs"))).add(((BigDecimal)(tempProductQtyMap.get("sQtyLtrs"))))));
														}
													}
												}
											}
										} 
									}catch (Exception e) {
										// TODO: handle exception
										Debug.logError("Error while getting product details ::"+e,module);
									}
									
								}
							}
						}
					}// end of while loop 					
					
						
						// getting tip Amount for Center
						Map inputPriceMap =FastMap.newInstance();
						inputPriceMap.put("userLogin",userLogin);
						inputPriceMap.put("facilityId",facilityId);
						inputPriceMap.put("fatPercent", BigDecimal.ZERO);
						inputPriceMap.put("snfPercent", BigDecimal.ZERO);
						inputPriceMap.put("productId",productId);
						Map priceChart = PriceServices.getProcurementProductPrice(dctx,inputPriceMap);
						String useTotalSolids = (String)priceChart.get("useTotalSolids");
						
						//tip for center
						Map rateAmount = FastMap.newInstance();
						Map inputRateAmt = FastMap.newInstance();
						inputRateAmt.put("facilityId", facilityId);
						inputRateAmt.put("userLogin",userLogin);
						inputRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
						inputRateAmt.put("productId", productId);
						
						rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
						BigDecimal tempTipAmt = BigDecimal.ZERO;
						if("Y".equals(useTotalSolids)){
							tempTipAmt = (((BigDecimal)tipQuantityMap.get("kgFat")).add((BigDecimal)tipQuantityMap.get("kgSnf"))).multiply((BigDecimal)rateAmount.get("rateAmount"));
						}else{
							tempTipAmt = (((BigDecimal)tipQuantityMap.get("kgFat"))).multiply((BigDecimal)rateAmount.get("rateAmount"));
						}
						tempTipAmt = tempTipAmt.setScale(2,BigDecimal.ROUND_HALF_EVEN);
						
						
						// here we are storing finAccountDetails if center have no finAccountCode then We are storing UnitFinAccount
						Map centerFinAccountMap = FastMap.newInstance();
						if(UtilValidate.isNotEmpty(finAccountMap.get(facilityId))){
							centerFinAccountMap = (Map)finAccountMap.get(facilityId);
						}
						String finAccountCode = null;
						if(UtilValidate.isNotEmpty(centerFinAccountMap)){
							finAccountCode = (String)centerFinAccountMap.get("finAccountCode");
						}
						GenericValue unitDetails = null;
						if(UtilValidate.isEmpty(centerFinAccountMap)||(UtilValidate.isEmpty(finAccountCode))|| "0".equalsIgnoreCase(finAccountCode)){
							Map CenterDtails = ProcurementNetworkServices.getCenterDtails(dctx, UtilMisc.toMap("centerId", facilityId));
							if(ServiceUtil.isSuccess(CenterDtails)){
								unitDetails = (GenericValue) CenterDtails.get("unitFacility");
							}
							if(UtilValidate.isEmpty(unitDetails)){
								Debug.logError("unit Details not Found. ", module);
								return ServiceUtil.returnError("Unit details Not Found .");
							}
							String unitOwnerPartyId = (String)unitDetails.get("ownerPartyId");
							List unitFinAccountsList = delegator.findList("FinAccount",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,unitOwnerPartyId),null, null,null, false);
							unitFinAccountsList =  EntityUtil.filterByDate(unitFinAccountsList,fromDateTime);
							unitFinAccountMap = EntityUtil.getFirst(unitFinAccountsList);
							
							centerFinAccountMap.putAll(unitFinAccountMap);
						}
						
						//  getting adjustments 
						BigDecimal price = BigDecimal.ZERO;
						BigDecimal grsAddn = BigDecimal.ZERO;
						BigDecimal grsDed = BigDecimal.ZERO;
						if(UtilValidate.isNotEmpty(facilityCommission.get("totalAmount"))){
							price = (BigDecimal)facilityCommission.get("totalAmount");
						}
						
						Map adjustmentTypeMap = FastMap.newInstance();
						Map inputAdjMap = FastMap.newInstance();
						inputAdjMap.put("userLogin",userLogin);
						inputAdjMap.put("facilityId",facilityId);
						inputAdjMap.put("fromDate",fromDateTime);
						inputAdjMap.put("thruDate", thruDateTime);
						
						String populateAdj = "Y";
						
						
						//BigDecimal sQtyKgs = (BigDecimal)((Map)centerTotalValues.get(productId)).get("sQtyKgs"); 
						if((UtilValidate.isEmpty(adjustmentsPopulated)||(!(adjustmentsPopulated.contains(facilityId)))) &&(((((BigDecimal)procurementAbstract.get("qtyKgs")).compareTo(BigDecimal.ZERO)>0)))){
							adjustmentsPopulated.add(facilityId);
							adjustmentTypeMap = getPeriodAdjustmentsForAgent(dctx,inputAdjMap);
							if(ServiceUtil.isSuccess(adjustmentTypeMap)){
								Map adjustmentsMap = (Map) adjustmentTypeMap.get("adjustmentsTypeMap");
								Map additionsMap = FastMap.newInstance();
								Map deductionsMap = FastMap.newInstance();
								if(UtilValidate.isNotEmpty(adjustmentsMap) && UtilValidate.isNotEmpty((adjustmentsMap.get("MILKPROC_DEDUCTIONS")))){
									deductionsMap = (Map)adjustmentsMap.get("MILKPROC_DEDUCTIONS");
									Set<String> dedKeys = deductionsMap.keySet();
									for(String dedKey : dedKeys ){
										BigDecimal dedValue = (BigDecimal)deductionsMap.get(dedKey);
										String mappingKey = (String)orderAdjustmentTypeMapping.get(dedKey);
										if(UtilValidate.isEmpty(mappingKey)){
											return ServiceUtil.returnError("Mapping not Done for ========"+dedKey);
										}
										procurementAbstract.put(mappingKey, dedValue);
										grsDed = grsDed.add(dedValue);
									}
								}
								if(UtilValidate.isNotEmpty(adjustmentsMap) && UtilValidate.isNotEmpty((adjustmentsMap.get("MILKPROC_ADDITIONS")))){
									additionsMap = (Map)adjustmentsMap.get("MILKPROC_ADDITIONS");
									Set<String> addnKeys = additionsMap.keySet();
									for(String addnKey : addnKeys){
										BigDecimal addnValue = (BigDecimal)additionsMap.get(addnKey);
										String mappingKey = (String)orderAdjustmentTypeMapping.get(addnKey);
										if(UtilValidate.isEmpty(mappingKey)){
											return ServiceUtil.returnError("Mapping not Done for ========"+addnKey);
										}
										procurementAbstract.put(mappingKey, addnValue);
										grsAddn = grsAddn.add(addnValue);
									}
								}
								procurementAbstract.put("grsDed",grsDed);
								procurementAbstract.put("grsAddn",grsAddn);
							}
						}
						BigDecimal commAmt = (BigDecimal) facilityCommission.get("commissionAmount");
						BigDecimal cartageAmt = (BigDecimal) facilityCommission.get("cartage");
						
						commAmt = commAmt.setScale(2,BigDecimal.ROUND_HALF_EVEN);
						cartageAmt = cartageAmt.setScale(2,BigDecimal.ROUND_HALF_EVEN);
						
						BigDecimal grossAmt = price.add(commAmt).add(cartageAmt).add(grsAddn);
						BigDecimal netAmt = grossAmt.subtract(grsDed);
						
						GenericValue facilityDetail = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId",facilityId));
						String partyId = (String) facilityDetail.get("ownerPartyId");
						String partyName = (String)PartyHelper.getPartyName(delegator, partyId, true);
						
						if(UtilValidate.isEmpty(partyName)){
							partyName = (String)facilityDetail.get("facilityName");
						}
						List<GenericValue> tempProductList = EntityUtil.filterByCondition(procProductList, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
						GenericValue productDetails = EntityUtil.getFirst(tempProductList);
						procurementAbstract.put("facilityId",facilityId);
						procurementAbstract.put("productId",productId);
						procurementAbstract.put("periodBillingId", periodBillingId);
						procurementAbstract.put("fromDate", fromDateTime);
						procurementAbstract.put("thruDate", thruDateTime);
						
						procurementAbstract.put("facilityName",facilityDetail.get("facilityName"));
						procurementAbstract.put("ownerName", partyName);
						procurementAbstract.put("facilityCode",facilityDetail.get("facilityCode"));
						procurementAbstract.put("productId",	productId);
						procurementAbstract.put("kgFat",((BigDecimal)facilityCommission.get("kgFat")));
						procurementAbstract.put("kgSnf",((BigDecimal)facilityCommission.get("kgSnf")));
						procurementAbstract.put("solids",(((BigDecimal)procurementAbstract.get("kgSnf")).add((BigDecimal)procurementAbstract.get("kgFat"))));
						
						procurementAbstract.put("price", price);
						procurementAbstract.put("commissionAmount", commAmt);
						procurementAbstract.put("cartage", cartageAmt);
						procurementAbstract.put("netAmt", netAmt);
						procurementAbstract.put("tipAmt", tempTipAmt);
						procurementAbstract.put("grossAmt",grossAmt );
						procurementAbstract.put("finAccountCode", centerFinAccountMap.get("finAccountCode"));
						procurementAbstract.put("bCode", centerFinAccountMap.get("bCode"));
						procurementAbstract.put("gbCode", centerFinAccountMap.get("gbCode"));
						procurementAbstract.put("finAccountName", centerFinAccountMap.get("finAccountName"));
						procurementAbstract.put("finAccountBranch", centerFinAccountMap.get("finAccountBranch"));
						procurementAbstract.put("ifscCode", centerFinAccountMap.get("ifscCode"));
						procurementAbstract.put("bPlace", centerFinAccountMap.get("bPlace"));
						procurementAbstract.put("dist",	billingFacilityDetails.get("district"));
						procurementAbstract.create();
				}//end of if
				
				
			}catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError("Error while getting AbstractDetails", module);
				return ServiceUtil.returnError("Error while getting AbstractDetails for PeriodBillingId : "+periodBillingId);
			}catch (GenericServiceException e) {
				// TODO: handle exception
				Debug.logError("Error while getting RateAmounts", module);
				return ServiceUtil.returnError("Error while getting RateAmounts for PeriodBillingId : "+periodBillingId);
			}catch (Exception e){
				Debug.logError("Error whike Populating Abstract"+e, module);
				return ServiceUtil.returnError("Error whike Populating Abstract"+e.getMessage());
			}
			
		}// end of for loop
		
		BigDecimal opCost = BigDecimal.ZERO;
		try{
			Map grandSupplyTotalsMap =FastMap.newInstance();
			String billingFacilityTypeId = null;
			
			if(UtilValidate.isNotEmpty(billingFacilityDetails)){
				billingFacilityTypeId = (String) billingFacilityDetails.get("facilityTypeId");
			}
			Map inputMap = FastMap.newInstance();
			inputMap.put("facilityId", unitId);
			inputMap.put("userLogin", userLogin);
			inputMap.put("fromDate", fromDateTime);
			inputMap.put("thruDate", thruDateTime);
			Map unitPeriodTotals = ProcurementReports.getPeriodTotals(dctx, inputMap);
			if(UtilValidate.isNotEmpty(billingFacilityTypeId)&& ("UNIT".equalsIgnoreCase(billingFacilityTypeId))){
				Map inputOpCostMap = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(unitPeriodTotals.get(unitId))){
					grandSupplyTotalsMap = (Map)((Map)((Map)unitPeriodTotals.get(unitId)).get("dayTotals")).get("TOT");
				}
				
				procProductList = ProcurementNetworkServices.getProcurementProducts(dctx, context);
				Set<String> supplyKeySet = grandSupplyTotalsMap.keySet();
				for(String supplyType : supplyKeySet){
					if(!(supplyType.equalsIgnoreCase("TOT"))){
						Map tempProductMap = (Map)grandSupplyTotalsMap.get(supplyType);
						Set<String> prodKeySet = tempProductMap.keySet();  
						for(String prodKey : prodKeySet){
							if(!(prodKey.equalsIgnoreCase("TOT"))){	
								List<GenericValue> productDetailsList = FastList.newInstance();
								productDetailsList.addAll(EntityUtil.filterByCondition(procProductList, EntityCondition.makeCondition("productName",EntityOperator.EQUALS,prodKey)));
								GenericValue productDetails = EntityUtil.getFirst(productDetailsList);
								Map tempMap = FastMap.newInstance();
								tempMap = (Map)tempProductMap.get(prodKey);
								BigDecimal qtyLtrs = BigDecimal.ZERO;
								BigDecimal totalSolids=BigDecimal.ZERO;
								inputOpCostMap.put("facilityId",unitId);
								inputOpCostMap.put("rateTypeId", "PROC_OP_COST");
								inputOpCostMap.put("supplyTypeEnumId", supplyType);
								inputOpCostMap.put("userLogin", userLogin);
								
								inputOpCostMap.put("productId",productDetails.get("productId"));
								inputOpCostMap.put("slabAmount",billingFacilityDetails.get("facilitySize"));
								Map<String, Object> opCostAmtMap =FastMap.newInstance();
								opCostAmtMap = dispatcher.runSync("getProcurementFacilityRateAmount", inputOpCostMap);
								BigDecimal opCostRate = BigDecimal.ZERO;
								String uomId = "VLIQ_L";
								if(ServiceUtil.isSuccess(opCostAmtMap)){
									opCostRate = (BigDecimal)opCostAmtMap.get("rateAmount");
									if(UtilValidate.isNotEmpty(opCostAmtMap.get("uomId"))){
										uomId = (String) opCostAmtMap.get("uomId");
									}
								}
								if(uomId.equalsIgnoreCase("VLIQ_KGFAT")){
									totalSolids = totalSolids.add((BigDecimal) tempMap.get("kgFat"));
									uomId = "VLIQ_TS";
								}else{
									totalSolids = totalSolids.add((BigDecimal)tempMap.get("kgFat")).add((BigDecimal)tempMap.get("kgSnf"));
								}
								qtyLtrs = qtyLtrs.add((BigDecimal)tempMap.get("qtyLtrs")).add((BigDecimal)tempMap.get("sQtyLtrs"));
								opCost = opCost.add((BigDecimal)ProcurementNetworkServices.calculateProcOPCost(dctx,UtilMisc.toMap("uomId", uomId,"totalSolids",totalSolids,"qtyLtrs",qtyLtrs,"opCostRate",opCostRate)));
						 	}
						}
					}
				}
				if(opCost.compareTo(BigDecimal.ZERO)>0){
						GenericValue procurementAbstract = delegator.makeValue("ProcurementAbstract");
						Map facilityInMap = FastMap.newInstance();
						facilityInMap.put("shedId", billingFacilityDetails.get("parentFacilityId"));
						facilityInMap.put("unitCode", billingFacilityDetails.get("facilityCode"));
						//here we are assuming 300 as centerCode for OpCost
						facilityInMap.put("centerCode", "300");
						GenericValue opCostFacility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, facilityInMap)).get("agentFacility");
						if(UtilValidate.isNotEmpty(opCostFacility)){
							procurementAbstract.putAll(initProcurementAbstract());
							procurementAbstract.put("periodBillingId", periodBillingId);
							procurementAbstract.put("fromDate", fromDateTime);
							procurementAbstract.put("thruDate", thruDateTime);
							procurementAbstract.put("facilityId",opCostFacility.get("facilityId"));
							procurementAbstract.put("facilityCode",opCostFacility.get("facilityCode"));
							procurementAbstract.put("facilityName",opCostFacility.get("facilityName"));
							procurementAbstract.put("productId","_NA_");
							//procurementAbstract.put("productName","_NA_");
							opCost = opCost.setScale(2,BigDecimal.ROUND_HALF_EVEN);
							procurementAbstract.put("opCost", opCost);
							procurementAbstract.put("grossAmt", opCost);
							procurementAbstract.put("netAmt", opCost);
							if(UtilValidate.isEmpty(unitFinAccountMap)){
								List unitFinAccountsList = delegator.findList("FinAccount",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,billingFacilityDetails.get("ownerPartyId")),null,null,null,false);
								unitFinAccountsList = EntityUtil.filterByDate(unitFinAccountsList,fromDateTime);
								unitFinAccountMap = EntityUtil.getFirst(unitFinAccountsList); 
							}
							if(UtilValidate.isNotEmpty(unitFinAccountMap)){
								procurementAbstract.put("finAccountCode", unitFinAccountMap.get("finAccountCode"));
								procurementAbstract.put("finAccountName", unitFinAccountMap.get("finAccountName"));
								procurementAbstract.put("finAccountBranch", unitFinAccountMap.get("finAccountBranch"));
								procurementAbstract.put("ifscCode", unitFinAccountMap.get("ifscCode"));
								procurementAbstract.put("bPlace", unitFinAccountMap.get("bPlace"));
								procurementAbstract.put("bCode", unitFinAccountMap.get("bCode"));
								procurementAbstract.put("gbCode", unitFinAccountMap.get("gbCode"));
							}
							procurementAbstract.create();
						}
				}
			}
		}catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError("Error while Storing OpCost======"+e,module);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error while storing Opcost========="+e, module);
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	
	/**
	 * method to initialize PROCUREMENT ABSTRACT ENTITY
	 * @param dctx
	 * @param context
	 * @return
	 */
	
	private static Map<String, Object> initProcurementAbstract() {
		Map<String, Object> fieldsMap = FastMap.newInstance();
		// here we are initialising Additions , deductions,cartage,gross,commission,net for abstract Entity
		fieldsMap.put("addn1", BigDecimal.ZERO);
		fieldsMap.put("addn2", BigDecimal.ZERO);
		fieldsMap.put("addn3", BigDecimal.ZERO);
		fieldsMap.put("addn4", BigDecimal.ZERO);
		fieldsMap.put("addn5", BigDecimal.ZERO);
		fieldsMap.put("addn6", BigDecimal.ZERO);
		fieldsMap.put("addn7", BigDecimal.ZERO);
		fieldsMap.put("addn8", BigDecimal.ZERO);
		fieldsMap.put("addn9", BigDecimal.ZERO);
		fieldsMap.put("addn10", BigDecimal.ZERO);
		
		fieldsMap.put("ded1", BigDecimal.ZERO);
		fieldsMap.put("ded2", BigDecimal.ZERO);
		fieldsMap.put("ded3", BigDecimal.ZERO);
		fieldsMap.put("ded4", BigDecimal.ZERO);
		fieldsMap.put("ded5", BigDecimal.ZERO);
		fieldsMap.put("ded6", BigDecimal.ZERO);
		fieldsMap.put("ded7", BigDecimal.ZERO);
		fieldsMap.put("ded8", BigDecimal.ZERO);
		fieldsMap.put("ded9", BigDecimal.ZERO);
		fieldsMap.put("ded10", BigDecimal.ZERO);
		fieldsMap.put("ded11", BigDecimal.ZERO);
		fieldsMap.put("ded12", BigDecimal.ZERO);
		fieldsMap.put("ded13", BigDecimal.ZERO);
		fieldsMap.put("ded14", BigDecimal.ZERO);
		fieldsMap.put("ded15", BigDecimal.ZERO);
		fieldsMap.put("ded16", BigDecimal.ZERO);
		fieldsMap.put("ded17", BigDecimal.ZERO);
		fieldsMap.put("ded18", BigDecimal.ZERO);
		fieldsMap.put("ded19", BigDecimal.ZERO);
		fieldsMap.put("ded20", BigDecimal.ZERO);
		
		fieldsMap.put("grsDed", BigDecimal.ZERO);
		fieldsMap.put("grsAddn", BigDecimal.ZERO);
		fieldsMap.put("tipAmt", BigDecimal.ZERO);
		fieldsMap.put("netAmt", BigDecimal.ZERO);
		fieldsMap.put("commissionAmount", BigDecimal.ZERO);
		fieldsMap.put("cartage", BigDecimal.ZERO);
		fieldsMap.put("price", BigDecimal.ZERO);
		fieldsMap.put("grossAmt", BigDecimal.ZERO);
		//fieldsMap.put("productId","_NA_");
		fieldsMap.put("qtyKgs", BigDecimal.ZERO);
		fieldsMap.put("qtyLtrs", BigDecimal.ZERO);
		fieldsMap.put("amQtyLtrs", BigDecimal.ZERO);
		fieldsMap.put("pmQtyLtrs", BigDecimal.ZERO);
		fieldsMap.put("cQtyLtrs",BigDecimal.ZERO);
		fieldsMap.put("ptcCurd",BigDecimal.ZERO);
		fieldsMap.put("sQtyKgs", BigDecimal.ZERO);
		fieldsMap.put("kgFat", BigDecimal.ZERO);
		fieldsMap.put("kgSnf", BigDecimal.ZERO);
		
		return fieldsMap;
    }
	
	
	
	
	
	
	
	/*
	 * ProcurementProducerMigration BatchService
	 *	
	 *	
	 *	
	*/	
	public static Map<String, Object> batchRunMilkProcurementProducerMigration(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		List<GenericValue> producerList =FastList.newInstance();		
		
		try{
			producerList =delegator.findList("ProcProducerMigration",EntityCondition.makeCondition( EntityCondition.makeCondition("isMigrated", EntityOperator.NOT_EQUAL  ,"Y"),EntityOperator.OR ,EntityCondition.makeCondition("isMigrated", EntityOperator.EQUALS  , null)), null, null, null,false);
			Debug.logImportant("entryList===================="+producerList, module);
		
			Map createProcurementEntryCtx = UtilMisc.toMap("userLogin", userLogin);
		
			for(GenericValue producerEntry : producerList){
					Map resultMap= ServiceUtil.returnSuccess();
					GenericValue shedGeo = delegator.findOne("Geo",UtilMisc.toMap("geoId", producerEntry.getString("shedCode")) , true);				
					Map inAgentMap = FastMap.newInstance();
					String shedCode = shedGeo.getString("geoSecCode");
					String bmcuCode = producerEntry.getString("bmcuCode");
					String centerCode = producerEntry.getString("centerCode");
					String partyId ="";
					inAgentMap.put("shedCode",shedCode);
					inAgentMap.put("unitCode", bmcuCode);
					inAgentMap.put("centerCode", centerCode);
					GenericValue centerFacility= (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, inAgentMap)).get("agentFacility");
					if(UtilValidate.isEmpty(centerFacility)){
			    		Debug.logError("Center Not found with Code ==>'"+centerCode +"' in unitcode '"+bmcuCode+"'", module);
			      		return ServiceUtil.returnError("Center Not found with Code ==>'"+centerCode +"' in unitcode '"+bmcuCode+"'");        	
			    		
			    	}
					// lets Create party 
					Map inPartyMap = UtilMisc.toMap("userLogin", userLogin);
					inPartyMap.put("statusId", "PARTY_ENABLED");
					inPartyMap.put("firstName", producerEntry.getString("firstName"));
					inPartyMap.put("lastName", producerEntry.getString("lastName"));
					try{            	
						resultMap = dispatcher.runSync("createPerson", inPartyMap);
						if (ServiceUtil.isError(resultMap)) {
		  					String errMsg =  ServiceUtil.getErrorMessage(resultMap);
		  					Debug.logError(errMsg , module);
		  					return ServiceUtil.returnError(errMsg);
		                 }
						partyId = (String)resultMap.get("partyId");
						
		            }catch (GenericServiceException e) {
		             Debug.logError(e, module);
		             return ServiceUtil.returnError("Service Exception: " + e.getMessage());
		          }
		            resultMap.clear();
		            // lets create facility role
		            Map inPartyRoleMap = UtilMisc.toMap("userLogin", userLogin);
		            inPartyRoleMap.put("partyId", partyId);
		            inPartyRoleMap.put("facilityId", centerFacility.getString("facilityId"));	
		            inPartyRoleMap.put("roleTypeId", "MILK_PRODUCER");	 
		            try{            	
						resultMap = dispatcher.runSync("addPartyToFacility", inPartyRoleMap);
						if (ServiceUtil.isError(resultMap)) {
		  					String errMsg =  ServiceUtil.getErrorMessage(resultMap);
		  					Debug.logError(errMsg , module);
		  					return ServiceUtil.returnError(errMsg);
		                 }						
						
		            }catch (GenericServiceException e) {
		             Debug.logError(e, module);
		             return ServiceUtil.returnError("Service Exception: " + e.getMessage());
		          }
		            //lets party  contactMech telecomNumber if mobile number not  null in current record
		            if(UtilValidate.isNotEmpty(producerEntry.getString("mobileNo"))){
		            	String contactMechId = null;
		            	Map createPartyTelecomNumberMap = UtilMisc.toMap("userLogin",userLogin);
		            	createPartyTelecomNumberMap.put("partyId" , partyId);
		            	createPartyTelecomNumberMap.put("contactNumber" , producerEntry.getString("mobileNo"));
		            	 try{            	
							resultMap = dispatcher.runSync("createPartyTelecomNumber", createPartyTelecomNumberMap);
							if (ServiceUtil.isError(resultMap)) {
			  					String errMsg =  ServiceUtil.getErrorMessage(resultMap);
			  					Debug.logError(errMsg , module);
			  					return ServiceUtil.returnError(errMsg);
			                 }
							contactMechId = (String)resultMap.get("contactMechId");
							Map createPartyContactMechPurposeMap = UtilMisc.toMap("userLogin",userLogin);
							createPartyContactMechPurposeMap.put("partyId" , partyId);
							createPartyContactMechPurposeMap.put("contactMechId" , contactMechId);
							createPartyContactMechPurposeMap.put("contactMechPurposeTypeId" , "PHONE_MOBILE");
							resultMap = dispatcher.runSync("createPartyContactMechPurpose", createPartyContactMechPurposeMap);
							if (ServiceUtil.isError(resultMap)) {
			  					String errMsg =  ServiceUtil.getErrorMessage(resultMap);
			  					Debug.logError(errMsg , module);
			  					return ServiceUtil.returnError(errMsg);
			                 }
							
				           }catch (GenericServiceException e) {
				             Debug.logError(e, module);
				             return ServiceUtil.returnError("Service Exception: " + e.getMessage());
				       }
		            }
		            producerEntry.put("isMigrated", "Y");
		            delegator.store(producerEntry);		          
			}		   
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
            return ServiceUtil.returnError("There was an error while Doing Procuremnet Migration: " + e.getMessage());
		}
		return ServiceUtil.returnSuccess("successfully done.");
	}
	/**
	 * service for orderAdjustment Migration
	 */
	public static Map<String, Object> batchRunAdditionDeductionMigration(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp procurementFromDateTime = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("procurementFromDate")).getTime()));
		Timestamp procurementThruDateTime = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(((java.sql.Date) context.get("procurementThruDate")).getTime()));
		List<GenericValue> entryList =FastList.newInstance();		
		Timestamp dayBegin = UtilDateTime.getDayStart(procurementFromDateTime);
		Timestamp dayEnd = UtilDateTime.getDayEnd(procurementThruDateTime);	
		Map createBillingAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);
		try{
			entryList =delegator.findList("AdditionDeductionMigration",EntityCondition.makeCondition(EntityCondition.makeCondition("ddate", EntityOperator.GREATER_THAN_EQUAL_TO  , dayBegin) ,EntityOperator.AND ,EntityCondition.makeCondition("ddate", EntityOperator.LESS_THAN_EQUAL_TO , dayEnd) ), null, null, null,false);
			Debug.logImportant("entryList Size===================="+entryList.size(), module);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError(e, module);
            return ServiceUtil.returnError("There was an error while Doing OrderAdjustment Migration: " + e.getMessage());
		}
		for(GenericValue entry : entryList){
				Map resultMap= ServiceUtil.returnSuccess();
				createBillingAdjustmentCtx.put("shedCode", entry.getString("scode"));
				createBillingAdjustmentCtx.put("centerCode", entry.getString("ccode"));
				createBillingAdjustmentCtx.put("unitCode", entry.getString("ucode"));
				createBillingAdjustmentCtx.put("periodDate", entry.getTimestamp("ddate"));
				createBillingAdjustmentCtx.put("amount", entry.getBigDecimal("amount"));
				createBillingAdjustmentCtx.put("adjustmentTypeId", entry.getString("orderAdjustmentTypeId"));
				try{            	
					resultMap = dispatcher.runSync("createBillingAdjustment", createBillingAdjustmentCtx);
					if (ServiceUtil.isError(resultMap)) {
						Debug.logWarning("There was an error while creating  the Billing Adjustment: " + ServiceUtil.getErrorMessage(resultMap), module);
						return ServiceUtil.returnError("There was an error while creating the Billing Adjustment: " + ServiceUtil.getErrorMessage(resultMap));          	            
					}
	            }catch (GenericServiceException e) {
	             Debug.logError(e, module);
	             return ServiceUtil.returnError("There was an error while creating the Billing Adjustment : " + e.getMessage());
	          }		
		}		   
		
		return ServiceUtil.returnSuccess("successfully done.");
	}
	
	  public static Map<String, Object> getProcurementFacilityRateAmount(DispatchContext dctx, Map<String, ? extends Object> context){
	    	
	    	
	      
	    	 // todo: need to return UOM for rate amount , to support  op cost per ltr or kg 
	    	
	        Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String facilityId = (String) context.get("facilityId");
	        String rateTypeId = (String) context.get("rateTypeId");
	        String productId = (String) context.get("productId");
	        String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
	        BigDecimal slabAmount = (BigDecimal) context.get("slabAmount");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        GenericValue userLogin =(GenericValue)context.get("userLogin");        
	        String rateCurrencyUomId = "INR";
	        if(UtilValidate.isNotEmpty(context.get("rateCurrencyUomId"))){
	        	rateCurrencyUomId = (String)context.get("rateCurrencyUomId");
	        }
	        // if from date is null then lets take now timestamp as default 
	        if(UtilValidate.isEmpty(fromDate)){
	        	fromDate = UtilDateTime.nowTimestamp();
	        }
	        if(UtilValidate.isEmpty(productId)){
	        	productId = "_NA_";
	        }
	        if(UtilValidate.isEmpty(supplyTypeEnumId)){
	        	supplyTypeEnumId = "_NA_";
	        }
	       
	        Map result = ServiceUtil.returnSuccess();
	        BigDecimal rateAmount = BigDecimal.ZERO;
	       
	        EntityCondition	paramCond =null;
	        //lets get the active rateAmount
	        List facilityRates = FastList.newInstance();
	        List exprList = FastList.newInstance();
	        List tempExpList =FastList.newInstance();
	        tempExpList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
	        tempExpList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS, rateCurrencyUomId));
	        GenericValue facilityDetail =null;
	        String shedId = null;
	        String unitId = null;
	        String centerId = null;
	        try{
	        	paramCond = EntityCondition.makeCondition(tempExpList, EntityOperator.AND);
	        	facilityRates = delegator.findList("FacilityRate", paramCond, null , null, null, false);
	        	facilityRates = EntityUtil.filterByDate(facilityRates , fromDate);
	        	if(UtilValidate.isNotEmpty(facilityId)){  
		        	facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId" ,facilityId), true);
		        	if(facilityDetail.getString("facilityTypeId").equals("CENTER")){        		
		        		Map CenterDtails = ProcurementNetworkServices.getCenterDtails(dctx, UtilMisc.toMap("centerId", facilityId));
		        		centerId = facilityId;
		        		unitId = (String)((Map)CenterDtails.get("unitFacility")).get("facilityId");
		        		shedId = (String)((Map)CenterDtails.get("shedFacility")).get("facilityId");        		
		        	}
		        	if(facilityDetail.getString("facilityTypeId").equals("UNIT")){
		        		unitId = facilityId;
		        		shedId = facilityDetail.getString("parentFacilityId");
		        	}
		        	if(facilityDetail.getString("facilityTypeId").equals("SHED")){
		        		shedId = facilityId ;
		        	}
	        	}
	        	
	        }catch (GenericEntityException e) {
				// TODO: handle exception
	        	Debug.logError(e, module);	
	            return ServiceUtil.returnError(e.toString());	
			}
	     // getting transport commission
	        if(rateTypeId.equals("TRNSPT_PROC_MRGN")){
	        	 List<GenericValue> trnsptCommission = FastList.newInstance();
	        	 if(UtilValidate.isNotEmpty(facilityId)){
	        		// check at unit  level and facilityId exact match
	        		 trnsptCommission = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,facilityId),EntityOperator.AND, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,"_NA_") ));
	        	 }  
	        	// check at facilityId  level and product _NA_
	        	 if(UtilValidate.isEmpty(trnsptCommission)){
            		 trnsptCommission = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS, "_NA_"),EntityOperator.AND, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,"_NA_") ));
            	}
	        	facilityRates = trnsptCommission;
	        }
	        // TIP Amount facility(SHED OR UNIT) and product
	        if(rateTypeId.equals("PROC_TIP_AMOUNT")){
	        	 List<GenericValue> tipAmountList = FastList.newInstance();
	        	 if(UtilValidate.isNotEmpty(unitId)){
	        		// check at unit  level and product exact match
	            	 tipAmountList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,productId) ));
	            	// check at unit  level and product _NA_
	            	 if(UtilValidate.isEmpty(tipAmountList)){
	            		tipAmountList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS, unitId),EntityOperator.AND, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,"_NA_") ));
	            		
	            	}
	        	 }        	
	        	// check at shed  level and product 
	        	if(UtilValidate.isEmpty(tipAmountList)){
	        		tipAmountList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS, shedId),EntityOperator.AND, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,productId) ));
	        		
	        	}
	        	// check at shed  level and product _NA_
	        	if(UtilValidate.isEmpty(tipAmountList)){
	        		tipAmountList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS, shedId),EntityOperator.AND, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,"_NA_") ));
	        		
	        	}
	        	facilityRates = tipAmountList;
	        }        
	        
	        //OP Cost is based on facility(SHED OR UNIT) and supplyType
	        if(rateTypeId.equals("PROC_OP_COST")){        	
	        	List<GenericValue> opCostList = FastList.newInstance();
	        	if(UtilValidate.isNotEmpty(unitId)){
	        		// check at unit  level,productId and supplytime exact match
	        		opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,supplyTypeEnumId) ),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
	        		if(UtilValidate.isEmpty(opCostList)){
	        			// check at unit  level,productId and supplytime default(_NA_)
	        			opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,"_NA_") ),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
	        		}
	        		
	        		if(UtilValidate.isEmpty(opCostList)){ 
	        			// check at unit  level and supplytime exact match
	        			opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,supplyTypeEnumId) ));
	        		}
	            	
	        		// check at unit and supply Time default(_NA_)
	            	if(UtilValidate.isEmpty(opCostList)){            		
	            		opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,"_NA_") ));     		
	            		       		
	            	}
	        	}
	        	if(UtilValidate.isEmpty(opCostList)){
	        		// check at shed  level,productId and supplytime exact match
	        		opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,shedId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,supplyTypeEnumId) ),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
	        	}
	        	if(UtilValidate.isEmpty(opCostList)){
	        		// check at shed  level,productId and supplytime default(_NA_)
        			opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,shedId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,"_NA_") ),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
        		}
	        	// check at shed level and supplyTime
	        	if(UtilValidate.isEmpty(opCostList)){
	    			opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,shedId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,supplyTypeEnumId) ));
	    		}
	        	// check at shed level and supplyTime default(_NA_)
	        	if(UtilValidate.isEmpty(opCostList)){    			
	    			opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,shedId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,"_NA_") ));
	    		}
	        	facilityRates = opCostList;        	
	        	
	        }  
	        
	      //Quantity Incentive is based on facility(SHED OR UNIT ), supplyType AND ProductId
	        if(rateTypeId.equals("PROC_QTY_INCENTIVE")){    
	        	List<GenericValue> qtyIncentiveCostList = FastList.newInstance();
	        	/*// check at facilityId ,productId at center level exact match
	        	 qtyIncentiveCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,centerId));
	        	if(UtilValidate.isNotEmpty(productId)){
	        		qtyIncentiveCostList = EntityUtil.filterByCondition(facilityRates,EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,supplyTypeEnumId)),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
	        	}*/
        		// check at unit  level,ProductId and supplytime exact match
        		qtyIncentiveCostList = EntityUtil.filterByCondition(facilityRates,EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,supplyTypeEnumId)),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
            	// check at unit,productId and supply Time default(_NA_)
            	if(UtilValidate.isEmpty(qtyIncentiveCostList)){            		
            		qtyIncentiveCostList = EntityUtil.filterByCondition(facilityRates,EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,"_NA_") ),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));     		
            	}
	        	
	        	// check at shed level,productId and supplyTime
	        	if(UtilValidate.isEmpty(qtyIncentiveCostList)){
	        		
	        		qtyIncentiveCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,shedId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,supplyTypeEnumId) ),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
	    		}
	        	// check at shed level,productId and supplyTime default(_NA_)
	        	if(UtilValidate.isEmpty(qtyIncentiveCostList)){    			
	        		qtyIncentiveCostList = EntityUtil.filterByCondition(facilityRates,EntityCondition.makeCondition( EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,shedId),EntityOperator.AND, EntityCondition.makeCondition("supplyTypeEnumId" ,EntityOperator.EQUALS,"_NA_") ),EntityOperator.AND,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId)));
	    		}
	        	facilityRates = qtyIncentiveCostList;        	
	        	
	        }  
	        
	        // Penalty Rate Amount based on facility(SHED)
	        if(rateTypeId.equals("PROC_PENALTY_AMOUNT")){         	
	        	
	        	// check at facilityId exact match
	        	List<GenericValue> penaltyCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,shedId));
	        	/*if(UtilValidate.isEmpty(penaltyCostList)){
	        		penaltyCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,"_NA_"));
	        		
	        	}*/
	        	facilityRates = penaltyCostList;      	
	        }
	        
	        // margin rate amount  based on facility(CENETR OR UNIT) 
	        // for milkline margin is unit  base
	        if(rateTypeId.equals("PROC_AGENT_MRGN")){ 
	        	
	        	if((!facilityDetail.getString("facilityTypeId").equals("CENTER"))&&(!facilityDetail.getString("facilityTypeId").equals("UNIT"))){
	        		Debug.logWarning("specified facility is not a Center and not a Unit===="+facilityId, module);
	        		result.put("rateAmount",rateAmount);
	        		return result;
	        	}
	        	
	        	List<GenericValue> marginCostList = FastList.newInstance();
	        	// check at facilityId  at center level exact match
	        	if(UtilValidate.isNotEmpty(productId)){
	        		marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, centerId), EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
	        	}
	        	if(UtilValidate.isEmpty(marginCostList)){
	        		marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,centerId));
	        	}
	        	
	        	// check at facilityId  at unit level exact match
	        	if(UtilValidate.isEmpty(marginCostList)||(facilityDetail.getString("facilityTypeId").equals("UNIT"))){
	        		if(UtilValidate.isNotEmpty(productId)){
		        		marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, unitId), EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
		        	}
	        		if(UtilValidate.isEmpty(marginCostList)){
	        			marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,unitId));
	        		}
	        	}
	        	facilityRates = marginCostList;
	        	
	        }
	        // agent Bonus  rate amount  based on productId     
	        if(rateTypeId.equals("PROC_AGENT_BONUS")){ 	        	
	        	
	        	// check at productId exact match
	        	List<GenericValue> marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,centerId));
	        	if(UtilValidate.isNotEmpty(productId) && (facilityDetail.getString("facilityTypeId").equals("UNIT"))){
	        		marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, unitId), EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
	        		//marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,productId));
	        	}
	        	
	        	facilityRates = marginCostList;
	        	
	        }
	     // agent Bonus  rate amount  based on productId     
	        if(rateTypeId.equals("PROC_AGENT_RETNBONUS")){ 	        	
	        	
	        	// check at productId exact match
	        	List<GenericValue> marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,centerId));
	        	if((facilityDetail.getString("facilityTypeId").equals("UNIT"))){
	        		marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, unitId));
	        		//marginCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("productId" ,EntityOperator.EQUALS,productId));
	        	}
	        	
	        	facilityRates = marginCostList;
	        	
	        }
	        // CARTAGE  rate amount based on facility(CENETR)       
	        if(rateTypeId.equals("PROC_CARTAGE")){ 
	        	if(!facilityDetail.getString("facilityTypeId").equals("CENTER")){
	        		Debug.logWarning("specified facility is not a Center===="+facilityId, module);
	        		result.put("rateAmount",rateAmount);
	        		return result;
	        	}
	        	// check at facilityId exact match
	        	List<GenericValue> cartageCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,centerId));
	        	facilityRates = cartageCostList;
	        }
	        // Milk Receipt Op cost
	        
	        if(rateTypeId.equals("MLKRECPT_OPCOST")){ 
	        	if(!facilityDetail.getString("facilityTypeId").equals("UNIT")){
	        		Debug.logWarning("specified facility is not a Unit===="+facilityId, module);
	        		result.put("rateAmount",rateAmount);
	        		return result;
	        	}
	        	// check at facilityId exact match
	        	List<GenericValue> opCostList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition("facilityId" ,EntityOperator.EQUALS,facilityId));
	        	facilityRates = opCostList;
	        }
	        //Milk Receipts Product Rate
	        if(rateTypeId.equals("MLKRCPT_PROD_RATE")){	        	
	        	if(UtilValidate.isNotEmpty(productId)){
	        	List<GenericValue>	productRateList = EntityUtil.filterByCondition(facilityRates, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, "_NA_"), EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
	        		facilityRates = productRateList;
	        	}	        	
	        }      
	        
	  		try{
	  			GenericValue validFacilityRate= EntityUtil.getFirst(facilityRates);
				if(UtilValidate.isNotEmpty(validFacilityRate)){
					result.put("uomId", validFacilityRate.getString("uomId"));
					if(UtilValidate.isNotEmpty(validFacilityRate.getString("acctgFormulaId"))){
						String acctgFormulaId =  validFacilityRate.getString("acctgFormulaId");
						result.put("acctgFormulaId", validFacilityRate.getString("acctgFormulaId"));
						if(UtilValidate.isEmpty(slabAmount)){						
							slabAmount = BigDecimal.ZERO;
							Debug.logWarning("no slab amount found for acctgFormulaId taking zero as default ", module);
						}
						Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "acctgFormulaId",acctgFormulaId, "variableValues","QUANTITY="+"1", "slabAmount", slabAmount);
		    			Map<String, Object> incentivesResult = dispatcher.runSync("evaluateAccountFormula", input);
		        		if (ServiceUtil.isError(incentivesResult)) {
		        			Debug.logError("unable to evaluate AccountFormula"+acctgFormulaId, module);	
		                    return ServiceUtil.returnError("unable to evaluate AccountFormula"+acctgFormulaId);	
		                }
		        		double formulaValue = (Double) incentivesResult.get("formulaResult");
		        		rateAmount = new BigDecimal(formulaValue);
						
					}else{
						rateAmount = validFacilityRate.getBigDecimal("rateAmount");
					}
					
				}
				
	  		}catch (Exception e) {
				// TODO: handle exception
				Debug.logError(e, module);	
		        return ServiceUtil.returnError(e.toString());
	  		}       
			result.put("rateAmount",rateAmount.setScale(2,3));
			 
	        return result;
	    }  
	    
	    
	    public static Map<String, Object> createProcFacility(DispatchContext dctx, Map context) {
	    	
			GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Map<String, Object> result = ServiceUtil.returnSuccess();
		
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String ownerPartyId = null;
			String parentFacilityId = null;
			String sequenceNum = null;
			String address = null;
			String contactMechId = null;
			String partyId = null;
			String firstName  = null;
			String lastName = null;
			String district = (String)context.get("district");
			String schemeTypeId = (String)context.get("schemeTypeId");
			String managedBy = (String)context.get("managedBy");

			String facilityCode = (String) context.get("facilityId");
			if( UtilValidate.isNotEmpty(context.get("facilityCode"))){
				facilityCode = (String) context.get("facilityCode");
			}
			String finAccountName = (String) context.get("bankName");
			String finAccountTypeId = (String)context.get("finAccountTypeId");
			String finAccountBranch = (String) context.get("finAccountBranch");
			String finAccountCode = (String) context.get("finAccountCode");
			String gbCode = (String) context.get("gbCode");
			String bCode = (String) context.get("bCode");
			String bPlace = (String) context.get("bPlace");
			String shortName = (String) context.get("shortName");
			String ifscCode = (String) context.get("ifscCode");
			String supervisorId = (String) context.get("supervisorId");
			
			BigDecimal cartage = (BigDecimal)context.get("cartage");
			BigDecimal commission = (BigDecimal)context.get("commission");
			BigDecimal opCost = (BigDecimal)context.get("opCost");
			BigDecimal eOpCost = (BigDecimal)context.get("eOpCost");
			BigDecimal facilitySize = (BigDecimal)context.get("facilitySize");
			String shedId = (String) context.get("shedId");
			String unitId = (String) context.get("unitId");
			String routeId = (String) context.get("routeId");
			String facilityTypeId = (String) context.get("facilityTypeId");
			String categoryTypeEnum = (String) context.get("categoryTypeEnum");
			String groupName = (String) context.get("firstName");
			String contactNumber = (String) context.get("contactNumber");
			String commissionUomId = (String) context.get("commissionUomId");
			String cartageUomId = (String) context.get("cartageUomId");
			String opCostUomId = (String) context.get("opCostUomId");
			String eOpCostUomId = (String) context.get("eOpCostUomId");
			address = (String) context.get("address");
			Map<String, Object> resultMap = FastMap.newInstance();
			Map input = FastMap.newInstance();
			String mccCode = (String) context.get("mccCode");
			if(UtilValidate.isNotEmpty(mccCode)){
				facilityCode = mccCode;
				groupName = (String)context.get("facilityName");
			}
			GenericValue parentFacility;
			GenericValue facility;
			try {
				if(facilityTypeId == null){
					Debug.logError("Please Enter 'Facility Type Id", module);
					return ServiceUtil.returnError("Please Enter 'Facility Type Id'");
				}
				if(UtilValidate.isNotEmpty(context.get("parentFacilityId"))){
					parentFacilityId = (String)context.get("parentFacilityId");
				}else if(facilityTypeId.equals("CENTER")){
					parentFacilityId = routeId;
					if(UtilValidate.isEmpty(unitId)){
						Debug.logError("Unit can not be empty ",module);
						return ServiceUtil.returnError("Unit can not be empty");
					}
					GenericValue unitDetails = (GenericValue)delegator.findOne("Facility", UtilMisc.toMap("facilityId",unitId), false);
					Map unitAgentsMap = ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("unitId", unitId));	    			
		    		List unitAgents = EntityUtil.getFieldListFromEntityList((List)unitAgentsMap.get("agentsList"), "facilityCode", false);
		    		if(unitAgents.contains(facilityCode)){
		    			Debug.logError(facilityTypeId+" with same code exists in Unit "+unitId +" facilityName :"+(String)unitDetails.get("facilityName"), module);
						return ServiceUtil.returnError(facilityTypeId+" with same code exists in unit :"+(String)unitDetails.get("facilityName"));
		    		}
					
				}else if (facilityTypeId.equals("PROC_ROUTE")) {
					parentFacilityId = unitId;
				}else if (facilityTypeId.equals("UNIT")) {
					parentFacilityId = shedId;
				}else{
					Debug.logError("Please Enter 'Facility Type Id Belongs to Procurement", module);
					return ServiceUtil.returnError("Please Enter 'Facility Type Id Belongs to Procurement");
				}
								
				if(parentFacilityId == null){
					Debug.logError("Please Enter 'Parent Facility Id", module);
					return ServiceUtil.returnError("Please Enter 'Parent Facility Id'");
				}
				List<EntityCondition> condList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("parentFacilityId",EntityOperator.EQUALS, parentFacilityId));
				condList.add(EntityCondition.makeCondition("facilityCode",EntityOperator.EQUALS, facilityCode));
				/*if(UtilValidate.isNotEmpty(facilityTypeId)){
					condList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS, facilityTypeId));
				}*/
				List childFacilitiesList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("facilityId"), null, null, true);
				if(UtilValidate.isNotEmpty(childFacilitiesList)){
					Debug.logError(facilityTypeId+" with same code exists in Route"+parentFacilityId , module);
					return ServiceUtil.returnError(facilityTypeId+" with same code exists in "+parentFacilityId);
				}
				
				//creat person (owner party)
				if(groupName == null){
					Debug.logError("groupName is missing", module);
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
					return ServiceUtil.returnError("groupName is missing");
				}
				input = UtilMisc.toMap("groupName",groupName);
				resultMap = dispatcher.runSync("createPartyGroup", input);
				if (ServiceUtil.isError(resultMap)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
                    return resultMap;
                }
				ownerPartyId = (String) resultMap.get("partyId"); 
				
				//create address (PartyContactMech)
				if (UtilValidate.isNotEmpty(address)){
					input = UtilMisc.toMap("userLogin", userLogin, "partyId",ownerPartyId, "address1",address,"stateProvinceGeoId", (String)context.get("stateProvinceGeoId"), "postalCode", (String)context.get("postalCode"), "contactMechId", contactMechId);
					resultMap =  dispatcher.runSync("createPartyPostalAddress", input);
					if (ServiceUtil.isError(resultMap)) {
						Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                return resultMap;
		            }
					contactMechId = (String) resultMap.get("contactMechId");
					 
					Object tempInput = "BILLING_LOCATION";
					input = UtilMisc.toMap("userLogin", userLogin, "contactMechId", contactMechId, "partyId",ownerPartyId, "contactMechPurposeTypeId", tempInput);
					resultMap =  dispatcher.runSync("createPartyContactMechPurpose", input);
					if (ServiceUtil.isError(resultMap)) {
					    Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                return resultMap;
		            }
					partyId = (String) resultMap.get("partyId"); 
				 }
				
				//create mobile number 
	            if(UtilValidate.isNotEmpty(contactNumber)){
	            	Map createPartyTelecomNumberMap = UtilMisc.toMap("userLogin",userLogin);
	            	createPartyTelecomNumberMap.put("partyId" , ownerPartyId);
	            	createPartyTelecomNumberMap.put("contactNumber" , contactNumber);
					resultMap = dispatcher.runSync("createPartyTelecomNumber", createPartyTelecomNumberMap);
					if (ServiceUtil.isError(resultMap)) {
		  				String errMsg =  ServiceUtil.getErrorMessage(resultMap);
		  				Debug.logError(errMsg , module);
		  				return ServiceUtil.returnError(errMsg);
		            }
					contactMechId = (String)resultMap.get("contactMechId"); 
	            }
	            
				//facility creation
				 if (UtilValidate.isEmpty((String)context.get("facilityName"))){
					Debug.logError("Name of the Facility is Missing", module);
				   	return ServiceUtil.returnError("Name of the Facility is Missing");
				 }
				 Timestamp openedDate = UtilDateTime.nowTimestamp();
				 if(UtilValidate.isNotEmpty( context.get("openedDate"))){
					 openedDate = (Timestamp) context.get("openedDate");
				 }
				 input = UtilMisc.toMap("userLogin", userLogin,"mccCode",mccCode, "ownerPartyId", ownerPartyId,"district",district,"schemeTypeId",schemeTypeId,"managedBy",managedBy, "openedDate", openedDate, "facilityCode", facilityCode, "facilityTypeId", facilityTypeId, "parentFacilityId", parentFacilityId, "categoryTypeEnum", categoryTypeEnum, "facilityName", (String)context.get("facilityName"), "description", (String)context.get("description"),"facilitySize",facilitySize);   
				 resultMap =  dispatcher.runSync("createFacility", input);
				 if (ServiceUtil.isError(resultMap)) {
					 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
	                 return resultMap;
	             }
				 String resultFacilityId = (String) resultMap.get("facilityId");
				 
				 result = ServiceUtil.returnSuccess("Facility "+resultFacilityId+ " is successfully created");
				 result.put("facilityId", resultFacilityId);
				
				 //create Party Role 
					if (UtilValidate.isNotEmpty(supervisorId)){
						input = UtilMisc.toMap("partyId",supervisorId,"facilityId",resultFacilityId,"userLogin", userLogin,"roleTypeId","SUPERVISOR");
						resultMap = dispatcher.runSync("createFacilityParty", input);
						if (ServiceUtil.isError(resultMap)) {
							Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                    return resultMap;
		                }
					}
				 // creating Facility Rate
				 if(UtilValidate.isNotEmpty(resultFacilityId)){
			        	Map facilityRateInMap = FastMap.newInstance();
			        	facilityRateInMap.put("facilityId",resultFacilityId);
			        	facilityRateInMap.put("userLogin",userLogin);
			        	facilityRateInMap.put("fromDate",openedDate);
			        	if(UtilValidate.isNotEmpty(cartage)){
			        		facilityRateInMap.put("rateAmount",cartage);
			        		facilityRateInMap.put("rateTypeId", "PROC_CARTAGE");
			        		facilityRateInMap.put("supplyTypeEnumId", "_NA_");
			        		if(UtilValidate.isNotEmpty(cartageUomId)){
			        			facilityRateInMap.put("uomId", cartageUomId);
			        		}
			        		Map facilityRate = dispatcher.runSync("createProcFacilityRate", facilityRateInMap);
			        	}
			        	if(UtilValidate.isNotEmpty(opCost)){
			        		facilityRateInMap.put("rateAmount",opCost);
			        		facilityRateInMap.put("supplyTypeEnumId","AM");
			        		facilityRateInMap.put("rateTypeId", "PROC_OP_COST");
			        		if(UtilValidate.isNotEmpty(opCostUomId)){
			        			facilityRateInMap.put("uomId", opCostUomId);
			        		}
			        		Map facilityRate = dispatcher.runSync("createProcFacilityRate", facilityRateInMap);
			        	}
			        	if(UtilValidate.isNotEmpty(eOpCost)){
			        		facilityRateInMap.put("rateAmount",eOpCost);
			        		facilityRateInMap.put("supplyTypeEnumId","PM");
			        		facilityRateInMap.put("rateTypeId", "PROC_OP_COST");
			        		if(UtilValidate.isNotEmpty(eOpCostUomId)){
			        			facilityRateInMap.put("uomId", eOpCostUomId);
			        		}
			        		Map facilityRate = dispatcher.runSync("createProcFacilityRate", facilityRateInMap);
			        	}
			        	if(UtilValidate.isNotEmpty(commission)){
			        		facilityRateInMap.put("rateTypeId", "PROC_AGENT_MRGN");
			        		facilityRateInMap.put("supplyTypeEnumId", "_NA_");
				        	facilityRateInMap.put("rateAmount",commission);
				        	if(UtilValidate.isNotEmpty(commissionUomId)){
			        			facilityRateInMap.put("uomId", commissionUomId);
			        		}
			        		Map facilityRate = dispatcher.runSync("createProcFacilityRate", facilityRateInMap);
			        	}
			        }
				 
				 //FinAccount
				 if (UtilValidate.isNotEmpty(finAccountCode)){
					 input = UtilMisc.toMap("userLogin", userLogin, "ownerPartyId", ownerPartyId, "finAccountCode", finAccountCode, "finAccountBranch", finAccountBranch, "finAccountName" , finAccountName,"gbCode",gbCode,"bCode",bCode,"finAccountTypeId",finAccountTypeId,"ifscCode",ifscCode,"shortName",shortName,"bPlace",bPlace);
					 resultMap = dispatcher.runSync("createFinAccount", input);
					 if (ServiceUtil.isError(resultMap)) {
						 Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		                 return resultMap;
		             }
				 }
				 String finAccountId = (String) resultMap.get("finAccountId");
				 
			}catch (GenericServiceException e) {
				Debug.logError("Error while creating Facility :"+e.getMessage(), module);
				return ServiceUtil.returnError("Error while creating Facility :"+e.getMessage());
			}catch (Exception e) {
				Debug.logError("Error while creating Facility :"+e.getMessage(), module);
				return ServiceUtil.returnError("Error while creating Facility :"+e.getMessage());
			}
			return result;
		}
	    
	    
	    public static Map<String, Object> updatePremiumAdjestment(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();        
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        List agentList = FastList.newInstance();
	        List<GenericValue> procurementEntryList = FastList.newInstance();
	        List<GenericValue> procurementOrderList = FastList.newInstance();
	        String orderId = (String) context.get("orderId");
	        BigDecimal discountAmount = BigDecimal.ZERO;
	        try{	         
	 	        Map<String, Object> result = FastMap.newInstance();	 	      
	 	        procurementEntryList = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId",EntityOperator.EQUALS, orderId), null, null, null,false); 
	 		 
	 	        GenericValue orderDetail = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
	 	        String facilityId = orderDetail.getString("originFacilityId");
	 	        //lets iterate through each entry and calculate the Premium Discount
	 	    
	 		 for( GenericValue procurementEntry : procurementEntryList){
	 			// calculate Product price here 
	 	     	Map<String, Object> priceResult;     	
	 	        Map<String, Object> priceContext = FastMap.newInstance();
	 	        
	 	        priceContext.put("userLogin", userLogin);                
	 	        priceContext.put("productId", procurementEntry.getString("productId"));
	 	        priceContext.put("facilityId", facilityId);
	 	        priceContext.put("priceDate", procurementEntry.getTimestamp("estimatedDeliveryDate"));	 	       
	 	        priceContext.put("supplyTypeEnumId", procurementEntry.getString("supplyTypeEnumId"));
	 	        priceContext.put("snfPercent",procurementEntry.getBigDecimal("snf"));
	 	        priceContext.put("fatPercent", procurementEntry.getBigDecimal("fat"));
	 	        priceContext.put("isPremiumChart","Y");	 	      
	 			priceResult = PriceServices.getProcurementProductPrice(dctx, priceContext);              			
	 	        if (ServiceUtil.isError(priceResult)) {
	 	            Debug.logWarning("There is not special premium rate for : " + ServiceUtil.getErrorMessage(priceResult), module);
	 	    		continue;         	            
	 	        } 
	 	        BigDecimal productPrice = (BigDecimal)priceResult.get("price");
	 	        // premiumDisAmount = (premiumDiscountAmount -common actual amount(itemAmount))
	 	        BigDecimal itemAmount = (procurementEntry.getBigDecimal("quantity")).multiply(procurementEntry.getBigDecimal("unitPrice"));
	 	        BigDecimal premiumDiscountAmount = (procurementEntry.getBigDecimal("quantity")).multiply(productPrice);	 	        
	 	        discountAmount = discountAmount.add(premiumDiscountAmount.subtract(itemAmount));
	 	 		
	 		 }
	 		   
	     }catch (GenericEntityException e) {
			// TODO: handle exception
	  		 Debug.logError(e.getMessage(), module);
	  		 return ServiceUtil.returnError(e.getMessage());
	 	}
	     String  orderAdjustmentTypeId = "MILKPROC_PREMAMOUNT";
    	 List condList = FastList.newInstance();
    	 condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
    	 condList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, orderAdjustmentTypeId));
    	 try{ 
    		 List<GenericValue> adjEntryList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(condList ,EntityOperator.AND ), null, null, null, false); 
    		 delegator.removeAll(adjEntryList);
    	 }catch (Exception e) {
    		 Debug.logWarning("There was an error while removing premium adjustment: " + e.toString(), module);
      		return ServiceUtil.returnError("There was an error while removing premium adjustment: " + e.toString());
		}
    	 
	     if(discountAmount.compareTo(BigDecimal.ZERO) !=0 ){
	    	
	    	 Map createOrderAdjustmentCtx = UtilMisc.toMap("userLogin",userLogin);	    	 
	    	 createOrderAdjustmentCtx.put("orderId", orderId);
	    	 createOrderAdjustmentCtx.put("orderAdjustmentTypeId", orderAdjustmentTypeId);    	
	    	 createOrderAdjustmentCtx.put("amount", discountAmount);
	    	 try{ 
	    		Map<String, Object> resultMap = ServiceUtil.returnSuccess();
	     		resultMap = dispatcher.runSync("createOrderAdjustment", createOrderAdjustmentCtx);
	     		if (ServiceUtil.isError(resultMap)) {
	                 Debug.logWarning("There was an error while creating  the adjustment: " + ServiceUtil.getErrorMessage(resultMap), module);
	         		return ServiceUtil.returnError("There was an error while creating premium adjustment: " + ServiceUtil.getErrorMessage(resultMap));          	            
	             }        	 
	          //OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, "ORDER_COMPLETED", "ITEM_APPROVED", "ITEM_COMPLETED", null);           	 
	         }catch (GenericServiceException e) {
	             Debug.logError(e, module);
	             return ServiceUtil.returnError("There was an error while creating premium adjustment: " +e.getMessage());
	         }     
		     
	     }
	     
	    Map<String, Object> resultMap = ServiceUtil.returnSuccess();
	    resultMap.put("orderId",orderId);        
	    return resultMap;
	}  
	    public static List<GenericValue>  getProcurementOrderItemsForPeriod(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        Timestamp thruDate = UtilDateTime.getDayEnd((Timestamp) context.get("thruDate"));
	        String shedId = (String) context.get("shedId");
	        List<GenericValue> orderItems = FastList.newInstance();
	        List conditionList = FastList.newInstance();
	        if(UtilValidate.isEmpty(fromDate) || UtilValidate.isEmpty(thruDate)){
	        	 return orderItems;
	        }
	        try{	    	  
		       String purposeTypeId = "MILK_PROCUREMENT";
		       List<String> facilityIds= FastList.newInstance();    	
		    	
		        Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", shedId));
		        if(UtilValidate.isNotEmpty(facilityAgents)){
		        	facilityIds= (List) facilityAgents.get("facilityIds");	
		        	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
		        }
		       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
		       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));		      	   
			   conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_CREATED"));
			   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , fromDate) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , thruDate)));
			   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   Set<String> fieldsToSelect = UtilMisc.toSet("orderId", "orderItemSeqId" ,"originFacilityId" ,"quantity", "fat" ,"snf");
			   fieldsToSelect.add("sFat");
			   fieldsToSelect.add("quantityKgs");
			   fieldsToSelect.add("quantityLtrs");
			   fieldsToSelect.add("sQuantityLtrs");
			   fieldsToSelect.add("cQuantityLtrs");
			   fieldsToSelect.add("productId");
			   fieldsToSelect.add("supplyTypeEnumId");
			   orderItems =delegator.findList("OrderHeaderItemProductAndFacility", orderCondition, fieldsToSelect, null, null,false);			   
		    }catch (GenericEntityException e) {
				// TODO: handle exception
		     	Debug.logError(e.getMessage(), module);		     		 
			}
	        return orderItems;
	    }
	    public static Map<String, Object> validatePeriodEntries(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        String shedId = (String) context.get("shedId");
	        String unitCode = null;
	        // For now take mahabub nagar shed if shedId is empty in parameters
	        Map result = ServiceUtil.returnSuccess();
	        if(UtilValidate.isEmpty(shedId)){
	        	shedId = "MBNR";
	        }
	        String facilityId = (String) context.get("facilityId");
	        //List agentsList = FastList.newInstance();
	        List unitsList =FastList.newInstance();
	        if(UtilValidate.isNotEmpty(facilityId)){
	        	unitsList.add(facilityId);
	        	/*Map temp = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", facilityId));
	        	agentsList = (List)temp.get("facilityIds");*/
	        }else{
	        	unitsList = (List)(ProcurementNetworkServices.getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",shedId))).get("unitsList"); 
	        }	       
	        
	        String  verifyApprove = "N";
	        if(UtilValidate.isNotEmpty(context.get("verifyApprove"))){
	        	verifyApprove = (String)context.get("verifyApprove");
	        }
	       for(int i=0;i<unitsList.size();i++){
	    	   String unitId = (String)unitsList.get(i);
	    	   Map temp = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", unitId));
	    	   List agentsList = (List)temp.get("facilityIds");
	       
	    	   GenericValue customTimePeriod = null;
	    	   GenericValue facilityDetail = null;
	    	   List<GenericValue> orderItems= FastList.newInstance();
	    	   List conditionList =FastList.newInstance();
	    	   Map errorEntryMap =FastMap.newInstance();
	    	   Timestamp fromDateTime = UtilDateTime.nowTimestamp();
	    	   Timestamp thruDateTime = UtilDateTime.nowTimestamp();  
	    	   try{
	    		   customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
	    		   if(UtilValidate.isEmpty(customTimePeriod)){
	    			   Debug.logError( "There no active billing time periods. ", module);				 
	    			   return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
	    		   }
	    		   fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	    		   thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		    	
	    		   facilityDetail =delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
	    		   if(UtilValidate.isNotEmpty(facilityDetail)){
	    			   unitCode = facilityDetail.getString("facilityCode");
	    		   }
	    		   
	    		   Map orderItemCtx = UtilMisc.toMap("userLogin",userLogin);
	    		   orderItemCtx.put("fromDate", fromDateTime);
	    		   orderItemCtx.put("thruDate", thruDateTime);
	    		   orderItemCtx.put("shedId", unitId);
	    		   orderItems =getProcurementOrderItemsForPeriod(dctx ,orderItemCtx);	
	    		   /*if(UtilValidate.isNotEmpty(agentsList)){
			    		orderItems = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("originFacilityId", EntityOperator.IN ,agentsList));
			    	}*/
			   
	    	   }catch (GenericEntityException e) {
				// TODO: handle exception
	     		 Debug.logError(e.getMessage(), module);
	     		 return ServiceUtil.returnError(e.getMessage());
	    	   }
	    	   if(UtilValidate.isEmpty(orderItems)){
	    		  return ServiceUtil.returnSuccess("no order to process for the time period===>"+customTimePeriodId);
	    	   }

	    	   Map<String, Object> resultMap = ServiceUtil.returnSuccess();
	    	   Map qtyOutLierCtx = UtilMisc.toMap("userLogin",userLogin);
	    	   try{	    		
	    		   qtyOutLierCtx.put("customTimePeriodId", customTimePeriodId);
	    		   //qtyOutLierCtx.put("shedId", shedId);
	    		   qtyOutLierCtx.put("unitId", unitId);
	    		   qtyOutLierCtx.put("orderItems", orderItems);
	    		   qtyOutLierCtx.put("verifyApprove", verifyApprove);
	    		   //qtyOutLierCtx.put("orderItems", orderItems);
	    		   resultMap = dispatcher.runSync("checkQuantityOutLier", qtyOutLierCtx);
	    		   if (ServiceUtil.isError(resultMap)) {
	    			   Debug.logWarning("There was an error while validating Quantity OutLier: " + ServiceUtil.getErrorMessage(resultMap), module);
	    			   return ServiceUtil.returnError("There was an error while validating Quantity OutLier : " + ServiceUtil.getErrorMessage(resultMap));          	            
	    		   }		     	
	    		   resultMap = dispatcher.runSync("checkNegativeBillValue", qtyOutLierCtx);
	    		   if (ServiceUtil.isError(resultMap)) {
	    			   Debug.logWarning("There was an error while checking negative billValue : " + ServiceUtil.getErrorMessage(resultMap), module);
	    			   return ServiceUtil.returnError("There was an error while checking negative billValue : " + ServiceUtil.getErrorMessage(resultMap));          	            
	    		   }		     	
	    		   resultMap = dispatcher.runSync("checkSnfFatRange", qtyOutLierCtx);		     	
	    		   if (ServiceUtil.isError(resultMap)) {
	    			   Debug.logWarning("There was an error while checking SNF, FAT Ranges : " + ServiceUtil.getErrorMessage(resultMap), module);
	    			   return ServiceUtil.returnError("There was an error while checking SNF , FAT Ranges : " + ServiceUtil.getErrorMessage(resultMap));          	            
	    		   } 
	    		   resultMap = dispatcher.runSync("checkCode", qtyOutLierCtx);		     	
	    		   if (ServiceUtil.isError(resultMap)) {
	    			   Debug.logWarning("There was an error while adding check Code : " + ServiceUtil.getErrorMessage(resultMap), module);
	    			   return ServiceUtil.returnError("There was an error while adding check Code : " + ServiceUtil.getErrorMessage(resultMap));          	            
	    		   } 
	    	   }catch (GenericServiceException e) {
	    		   // TODO: handle exception
	    		   Debug.logError(e, module);
	    		   return ServiceUtil.returnError("There was an error while validating entries: " +e.getMessage());
	    	   }	    	  	 
	       }
	       	result.put("unitCode", unitCode);
	        return result;
		}  
	    
	    public static Map<String, Object> checkQuantityOutLier(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	                
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        String unitId = (String) context.get("unitId");
	        String shedId = null;
	       /* String shedId = (String) context.get("shedId");*/
	        String validationTypeId = "QTY_OUTLIER";
	        String  verifyApprove = "N";
	        if(UtilValidate.isNotEmpty(context.get("verifyApprove"))){
	        	verifyApprove = (String)context.get("verifyApprove");
	        }
	        Timestamp fromDate = UtilDateTime.nowTimestamp();
	        Timestamp thruDate = UtilDateTime.nowTimestamp();	
	        List conditionList = FastList.newInstance();
	        List<GenericValue> orderItems = FastList.newInstance();	        
	        try{
	    	   GenericValue customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
			   if(UtilValidate.isEmpty(customTimePeriod)){
				  Debug.logError( "There no active billing time periods. ", module);				 
				  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
			    }
			    fromDate =UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		    	thruDate =UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")); 			 
		    	
		    	orderItems = (List<GenericValue>)context.get("orderItems");
		    	GenericValue UnitDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", unitId),false);
		    	shedId = (String) UnitDetails.get("parentFacilityId");
		    	if(UtilValidate.isEmpty(orderItems)){
		    		 Map orderItemCtx = UtilMisc.toMap("userLogin",userLogin);
			    	 orderItemCtx.put("shedId", unitId);
					 orderItemCtx.put("fromDate", fromDate);
					 orderItemCtx.put("thruDate", thruDate);
					 orderItems = getProcurementOrderItemsForPeriod(dctx ,orderItemCtx);
		    	}   	 	     
				   
		     }catch (GenericEntityException e) {
				// TODO: handle exception
	     		 Debug.logError(e.getMessage(), module);
	     		 return ServiceUtil.returnError(e.getMessage());
			}
		    if(UtilValidate.isEmpty(orderItems)){
		    	return ServiceUtil.returnSuccess("no order to process for the time period===>"+customTimePeriodId);
		    } 
	        
	        try{	        	
	           List<String> centerList = EntityUtil.getFieldListFromEntityList(orderItems, "originFacilityId", true);
	           // Outlier diff value from validation rule entity
	           conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, shedId));
	           conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
        	   conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
			   EntityCondition ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
			   if(UtilValidate.isEmpty(validationRuleList)){
				   conditionList.clear();
				   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, "_NA_"));
		           conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
	        	   conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
				   ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				    validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
				   
			   }
			   validationRuleList = EntityUtil.filterByDate(validationRuleList);
			   
			   GenericValue validationRule = EntityUtil.getFirst(validationRuleList);			   
		       BigDecimal outLierDiff = new BigDecimal(50);
		       if(UtilValidate.isNotEmpty(validationRule) && UtilValidate.isNotEmpty(validationRule.getBigDecimal("quantity"))){
		    	   outLierDiff = validationRule.getBigDecimal("quantity");
		       }
		       // remove error entries if there is any for custom time period
		       if(verifyApprove.equals("N")){
			       Map validationCtx =  UtilMisc.toMap("userLogin",userLogin);
			       validationCtx.put("shedId", unitId);
			       validationCtx.put("validationTypeId", validationTypeId);
			       validationCtx.put("customTimePeriodId", customTimePeriodId);
			       removeProcBillingValidation(dctx , validationCtx);
		       }
		       List<GenericValue> procProduct = ProcurementNetworkServices.getProcurementProducts(dctx, context);
		       List<GenericValue> purchaseTimeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SUPPLY_TYPE"), null, null, null, true);
		       
	           for(int i=0;i<centerList.size();i++){
		    	   String centerId = centerList.get(i);
		    	   GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" ,centerId), false);
		    	   if(UtilValidate.isEmpty(facility.getString("categoryTypeEnum")) || ((UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum"))&&!"RECVYBOOTH".equals(facility.getString("categoryTypeEnum"))))){
			    	   List<GenericValue> centerItemList = EntityUtil.filterByAnd(orderItems, UtilMisc.toMap("originFacilityId", centerId ));
		    		   
			    	   Map<String ,Map<String ,BigDecimal>> productMinQtyMap =FastMap.newInstance();
			    	   for( GenericValue product :procProduct){		    		   
			    		   for(GenericValue purchaseTime : purchaseTimeList){
			    			   List<GenericValue> centerproductQty = EntityUtil.filterByAnd(centerItemList, UtilMisc.toMap("originFacilityId", centerId ,"productId" ,product.getString("productId") , "supplyTypeEnumId" ,purchaseTime.getString("enumId")));
			    		   if(UtilValidate.isNotEmpty(centerproductQty)){
			    			   ArrayList<BigDecimal> qtyList = new ArrayList(EntityUtil.getFieldListFromEntityList(centerproductQty, "quantity", false));
					    	   BigDecimal centerMinQty = Collections.min(qtyList);
						    	   BigDecimal centerMaxQty = Collections.max(qtyList);					    	   
						    	   if(qtyList.size() >2){
						    		   qtyList.remove((qtyList.size()-1));
							    	   qtyList.remove(1);	
						    	   }					    	   				    	   
						    	   BigDecimal sumQty = BigDecimal.ZERO;
						    	   for(int j=0 ;j<qtyList.size();j++){
						    		   sumQty = sumQty.add(qtyList.get(j));
						    	   }
						    	   BigDecimal centerAvgsQty = BigDecimal.ZERO;
						    	   
						    	   if(qtyList.size() >0){
						    		   centerAvgsQty = sumQty.divide(new BigDecimal(qtyList.size()),0, BigDecimal.ROUND_HALF_UP);
						    	   }
						    	  
						    	   Map supplyTypeQtyMap = FastMap.newInstance();
						    	   if(UtilValidate.isNotEmpty(productMinQtyMap.get(product.getString("productId")))){
						    		   supplyTypeQtyMap.putAll(productMinQtyMap.get(product.getString("productId")));					    		   
						    	   }
						    	   supplyTypeQtyMap.put(purchaseTime.getString("enumId"), centerAvgsQty);
						    	   productMinQtyMap.put(product.getString("productId"), supplyTypeQtyMap);
				    		   }
			    		   }
			    		  
			    	   }
			    	   for(GenericValue centerItem : centerItemList ){
			    		    BigDecimal itemQty = centerItem.getBigDecimal("quantity");
			    		    BigDecimal centerMinQty =BigDecimal.ZERO;
			    		    if(UtilValidate.isNotEmpty(productMinQtyMap.get(centerItem.get("productId"))) && UtilValidate.isNotEmpty((productMinQtyMap.get(centerItem.get("productId"))).get(centerItem.getString("supplyTypeEnumId")))){
			    		    	 centerMinQty = (productMinQtyMap.get(centerItem.get("productId"))).get(centerItem.getString("supplyTypeEnumId"));
			    		    }
			    		    BigDecimal diffQty = itemQty.subtract(centerMinQty);
			    		   if((diffQty.compareTo(outLierDiff)) >= 0 ){
			    			   String orderId = centerItem.getString("orderId");
			    			   String orderItemSeqId = centerItem.getString("orderItemSeqId");
			    			   
			    			   conditionList.clear();
		    				   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"));		    	        	   
		    				   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, unitId));
		    		           conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
		    	        	   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
		    	        	   conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,orderItemSeqId));
		    	        	   conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
		    	        	   
		    				   EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    				   List<GenericValue> validationList =delegator.findList("ProcBillingValidation", condition, null, null, null,false);
			    			   if(verifyApprove.equals("N")){
			    				   GenericValue validationEntry = delegator.makeValue("ProcBillingValidation");
				    			   validationEntry.set("shedId", unitId);
				    			   validationEntry.set("customTimePeriodId", customTimePeriodId);
				    			   validationEntry.set("validationTypeId", validationTypeId);
				    			   validationEntry.set("orderId", orderId);
				    			   validationEntry.set("orderItemSeqId", orderItemSeqId);
				    			   validationEntry.set("centerId", centerId);
				    			   validationEntry.set("statusId", "ITEM_CREATED");
				    			   validationEntry.set("approvedDate", UtilDateTime.nowTimestamp());
				    			   validationEntry.set("approvedByUserLoginId", userLogin.getString("userLoginId"));
				    			   if(UtilValidate.isEmpty(validationList)){
				    				   delegator.setNextSubSeqId(validationEntry, "sequenceNum", 5, 1);
							           delegator.create(validationEntry);
				    			   }
				    			   
			    			   }else{		    				   
			    				   if(UtilValidate.isEmpty(validationList)){
			    					   String errMsg = "There is some invalid Etrires please verify and approve or remove the entries and run the billing";
			    					   Debug.logError(errMsg+"==================="+validationTypeId, module);
			    					   return ServiceUtil.returnError(errMsg);
			    				   }
			    			   }
			    			   
			    		   }
			    	   }
			    	   
			       }
	           }
			   
	    	 }catch (Exception e) {
				// TODO: handle exception
	     		Debug.logError(e.getMessage(), module);
	     		return ServiceUtil.returnError(e.getMessage());
			}
	        return ServiceUtil.returnSuccess();
		} 
	    
	    public static Map<String, Object> checkCode(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	                
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        String unitId = (String) context.get("unitId");
	        String shedId = null;
	        String validationTypeId = "CHECKCENTER_CODE";
	        String  verifyApprove = "N";
	        if(UtilValidate.isNotEmpty(context.get("verifyApprove"))){
	        	verifyApprove = (String)context.get("verifyApprove");
	        }
	        Timestamp fromDate = UtilDateTime.nowTimestamp();
	        Timestamp thruDate = UtilDateTime.nowTimestamp();	
	        List conditionList = FastList.newInstance();
	        List<GenericValue> orderItems = FastList.newInstance();	        
	        try{
	    	   GenericValue customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
			   if(UtilValidate.isEmpty(customTimePeriod)){
				  Debug.logError( "There no active billing time periods. ", module);				 
				  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
			    }
			    fromDate =UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		    	thruDate =UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")); 			 
		    	
		    	orderItems = (List<GenericValue>)context.get("orderItems");
		    	GenericValue UnitDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", unitId),false);
		    	shedId = (String) UnitDetails.get("parentFacilityId");
		    	if(UtilValidate.isEmpty(orderItems)){
		    		 Map orderItemCtx = UtilMisc.toMap("userLogin",userLogin);
			    	 orderItemCtx.put("shedId", unitId);
					 orderItemCtx.put("fromDate", fromDate);
					 orderItemCtx.put("thruDate", thruDate);
					 orderItems = getProcurementOrderItemsForPeriod(dctx ,orderItemCtx);
		    	}   	 	     
				   
		     }catch (GenericEntityException e) {
				// TODO: handle exception
	     		 Debug.logError(e.getMessage(), module);
	     		 return ServiceUtil.returnError(e.getMessage());
			}
		    if(UtilValidate.isEmpty(orderItems)){
		    	return ServiceUtil.returnSuccess("no order to process for the time period===>"+customTimePeriodId);
		    } 
	        
	        try{	        	
	           List<String> centerList = EntityUtil.getFieldListFromEntityList(orderItems, "originFacilityId", true);
		           // check center code entries value from validation rule entity
		           conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, shedId));
		           conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
	        	   conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
				   EntityCondition ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				   List<GenericValue> validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
				   if(UtilValidate.isEmpty(validationRuleList)){
					   conditionList.clear();
					   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, "_NA_"));
			           conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "_NA_"));
		        	   conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
					   ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					    validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
					   
				   }
				   validationRuleList = EntityUtil.filterByDate(validationRuleList);
				   
				   GenericValue validationRule = EntityUtil.getFirst(validationRuleList);			   
			       BigDecimal checkCodeEntries = new BigDecimal(3);
			       if(UtilValidate.isNotEmpty(validationRule) && UtilValidate.isNotEmpty(validationRule.getBigDecimal("quantity"))){
			    	   checkCodeEntries = validationRule.getBigDecimal("quantity");			    	   
			       }
		       // remove error entries if there is any for custom time period
		       if(verifyApprove.equals("N")){
			       Map validationCtx =  UtilMisc.toMap("userLogin",userLogin);
			       validationCtx.put("shedId", unitId);
			       validationCtx.put("validationTypeId", validationTypeId);
			       validationCtx.put("customTimePeriodId", customTimePeriodId);
			       removeProcBillingValidation(dctx , validationCtx);
		       }
		       List<GenericValue> procProduct = ProcurementNetworkServices.getProcurementProducts(dctx, context);
		       List<GenericValue> purchaseTimeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SUPPLY_TYPE"), null, null, null, true);
		       
	           for(int i=0;i<centerList.size();i++){
		    	   String centerId = centerList.get(i);
		    	   GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" ,centerId), false);
		    	   if(UtilValidate.isEmpty(facility.getString("categoryTypeEnum")) || ((UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum"))&&!"RECVYBOOTH".equals(facility.getString("categoryTypeEnum"))))){
			    	   List<GenericValue> centerItemList = EntityUtil.filterByAnd(orderItems, UtilMisc.toMap("originFacilityId", centerId ));	    		   
			    	   Map<String ,Map<String ,BigDecimal>> productMinQtyMap =FastMap.newInstance();
			    	   for( GenericValue product :procProduct){		    		   
			    		   for(GenericValue purchaseTime : purchaseTimeList){
			    			   List<GenericValue> centerproductQty = EntityUtil.filterByAnd(centerItemList, UtilMisc.toMap("originFacilityId", centerId ,"productId" ,product.getString("productId") , "supplyTypeEnumId" ,purchaseTime.getString("enumId")));
			    			   if(UtilValidate.isNotEmpty(centerproductQty)){
	    				   			if(centerproductQty.size()<=(checkCodeEntries.intValue())){
		    				   				for(GenericValue centerItem : centerproductQty ){	    		   
							    			   String orderId = centerItem.getString("orderId");
							    			   String orderItemSeqId = centerItem.getString("orderItemSeqId");							    			   
							    			   conditionList.clear();
						    				   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"));		    	        	   
						    				   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, unitId));
						    		           conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
						    	        	   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
						    	        	   conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,orderItemSeqId));
						    	        	   conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
						    	        	   
						    				   EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						    				   List<GenericValue> validationList =delegator.findList("ProcBillingValidation", condition, null, null, null,false);
						    				  if(verifyApprove.equals("N")){
							    				   GenericValue validationEntry = delegator.makeValue("ProcBillingValidation");
								    			   validationEntry.set("shedId", unitId);
								    			   validationEntry.set("customTimePeriodId", customTimePeriodId);
								    			   validationEntry.set("validationTypeId", validationTypeId);
								    			   validationEntry.set("orderId", orderId);
								    			   validationEntry.set("orderItemSeqId", orderItemSeqId);
								    			   validationEntry.set("centerId", centerId);
								    			   validationEntry.set("statusId", "ITEM_CREATED");
								    			   validationEntry.set("approvedDate", UtilDateTime.nowTimestamp());
								    			   validationEntry.set("approvedByUserLoginId", userLogin.getString("userLoginId"));
								    			   if(UtilValidate.isEmpty(validationList)){
								    				   delegator.setNextSubSeqId(validationEntry, "sequenceNum", 5, 1);
											           delegator.create(validationEntry);
								    			   }							    			   
							    			   }else{		    				   
							    				   if(UtilValidate.isEmpty(validationList)){
							    					   String errMsg = "There is some invalid Etrires please verify and approve or remove the entries and run the billing";
							    					   Debug.logError(errMsg+"==================="+validationTypeId, module);
							    					   return ServiceUtil.returnError(errMsg);
							    				   }
							    			   }    		   
							    	   }
	    				   			}
				    		   }
			    		   }		    		  
			    	   }  	   
			       }
	           }
			   
	    	 }catch (Exception e) {
				// TODO: handle exception
	     		Debug.logError(e.getMessage(), module);
	     		return ServiceUtil.returnError(e.getMessage());
			}
	        return ServiceUtil.returnSuccess();
		}   
	  public static Map<String, Object> checkNegativeBillValue(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	                
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        String unitId = (String) context.get("unitId");
	        String validationTypeId ="NEGATIVE_AMOUNT";
	        Timestamp fromDate = UtilDateTime.nowTimestamp();
	        Timestamp thruDate = UtilDateTime.nowTimestamp();	
	        List conditionList = FastList.newInstance();
	        List<GenericValue> orderItems = FastList.newInstance();
	        String  verifyApprove = "N";
	        if(UtilValidate.isNotEmpty(context.get("verifyApprove"))){
	        	verifyApprove = (String)context.get("verifyApprove");
	        }
	        try{
	    	   GenericValue customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
			   if(UtilValidate.isEmpty(customTimePeriod)){
				  Debug.logError( "There no active billing time periods. ", module);				 
				  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
			    }
			    fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		    	thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		    	
		    	/*orderItems = (List<GenericValue>)context.get("orderItems");
		    	if(UtilValidate.isEmpty(orderItems)){
		    		 Map orderItemCtx = UtilMisc.toMap("userLogin",userLogin);
			    	 orderItemCtx.put("shedId", unitId);
					 orderItemCtx.put("fromDate", fromDate);
					 orderItemCtx.put("thruDate", thruDate);
					 orderItems = getProcurementOrderItemsForPeriod(dctx ,orderItemCtx);
		    	}  */ 	 	  
	    	   String purposeTypeId = "MILK_PROCUREMENT";
		       List<String> facilityIds= FastList.newInstance();    	
		    	
		        Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", unitId));
		        if(UtilValidate.isNotEmpty(facilityAgents)){
		        	facilityIds= (List) facilityAgents.get("facilityIds");	
		        	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
		        }
		       conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, purposeTypeId));
		       conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));		      	   
			   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED"));
			   conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO  , fromDate) ,EntityOperator.AND ,EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO , thruDate)));
			   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   orderItems =delegator.findList("OrderHeader", orderCondition, null, null, null,false);
		    	
		     }catch (GenericEntityException e) {
				// TODO: handle exception
	     		 Debug.logError(e.getMessage(), module);
	     		 return ServiceUtil.returnError(e.getMessage());
			}
		    if(UtilValidate.isEmpty(orderItems)){
		    	return ServiceUtil.returnSuccess("no order to process for the time period===>"+customTimePeriodId);
		    } 
	        
	        try{
	        	// remove error entries if there is any for custom time period
	        if(verifyApprove.equals("N")){
		       Map validationCtx =  UtilMisc.toMap("userLogin",userLogin);
		       validationCtx.put("shedId", unitId);
		       validationCtx.put("validationTypeId", validationTypeId);
		       validationCtx.put("customTimePeriodId", customTimePeriodId);
		       removeProcBillingValidation(dctx , validationCtx);
	        }  
	           List<String> centerList = EntityUtil.getFieldListFromEntityList(orderItems, "originFacilityId", true);
	           String orderId =null;
	           for(int i=0;i<centerList.size();i++){	        	   
		    	   String centerId = centerList.get(i);
		    	   GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" ,centerId), false);
		    	   if(UtilValidate.isEmpty(facility.getString("categoryTypeEnum")) || ((UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum"))&&!"RECVYBOOTH".equals(facility.getString("categoryTypeEnum"))))){
			    	   List<GenericValue> centerItemList = EntityUtil.filterByAnd(orderItems, UtilMisc.toMap("originFacilityId", centerId));
			    	   List<String> orderIdList = new ArrayList(EntityUtil.getFieldListFromEntityList(centerItemList, "orderId", true)) ;
			    	   BigDecimal billValue = BigDecimal.ZERO;
			    	   for(int j=0;j<orderIdList.size();j++){		    		   
			    		    orderId = orderIdList.get(j);
			                Map<String, Object> resetResult = null;
			                try {
			                    resetResult = dispatcher.runSync("resetGrandTotal", UtilMisc.<String, Object>toMap("orderId", orderId, "userLogin", userLogin));
			                    if (ServiceUtil.isError(resetResult)) {
				                    Debug.logError("ERROR : Cannot reset order totals"+orderId, module);
				                    return ServiceUtil.returnError("ERROR : Cannot reset order totals"+orderId);
				                }
			                    GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId" ,orderId), false);
			                    billValue = billValue.add(order.getBigDecimal("grandTotal"));
			                 } catch (GenericServiceException e) {
			                    Debug.logError(e, "ERROR: Cannot reset order totals - " + orderId, module);
			                    return ServiceUtil.returnError(e.getMessage());
			                }
			    	   }		    
			    	   // billValue is negative then add as error record
			    	   if(billValue.compareTo(BigDecimal.ZERO) < 0 ){
			    		   conditionList.clear();
	    				   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"));		    	        	   
	    				   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, unitId));
	    		           conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
	    	        	   conditionList.add(EntityCondition.makeCondition("centerId", EntityOperator.EQUALS,centerId));		    	        	   
	    	        	   conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
	    	        	   
	    				   EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	    				   List<GenericValue> validationList =delegator.findList("ProcBillingValidation", condition, null, null, null,false);
	    				   
			    			   if(verifyApprove.equals("N")){
			    				   GenericValue validationEntry = delegator.makeValue("ProcBillingValidation");
			    				   
				    			   validationEntry.set("shedId", unitId);
				    			   validationEntry.set("customTimePeriodId", customTimePeriodId);
				    			   validationEntry.set("validationTypeId", validationTypeId);
				    			   validationEntry.set("centerId", centerId);
				    			   validationEntry.set("orderId", orderId);
				    			   validationEntry.set("statusId", "ITEM_CREATED");
				    			   validationEntry.set("approvedDate", UtilDateTime.nowTimestamp());
				    			   validationEntry.set("approvedByUserLoginId", userLogin.getString("userLoginId"));
				    			   if(UtilValidate.isEmpty(validationList)){
				    				   delegator.setNextSubSeqId(validationEntry, "sequenceNum", 5, 1);
							           delegator.create(validationEntry);	
				    			   }
			    			   }else{		    				  
			    				   if(UtilValidate.isEmpty(validationList)){
			    					   String errMsg = "There is some invalid Etrires please verify and approve or remove the entries and run the billing";
			    					   Debug.logError(errMsg+"==================="+validationTypeId, module);
			    					   return ServiceUtil.returnError(errMsg);
			    				   }
			    			   }
			    		       		   
			    	   }
			       }
	           }
			   
	    	 }catch (Exception e) {
				// TODO: handle exception
	     		Debug.logError(e.getMessage(), module);
	     		return ServiceUtil.returnError(e.getMessage());
			}
	        return ServiceUtil.returnSuccess();
		}  
	    
	  public static Map<String, Object> checkSnfFatRange(DispatchContext dctx, Map<String, ? extends Object> context) {
		  	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	                
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        String unitId = (String) context.get("unitId");
	        String shedId = null;
	        String validationTypeId = "QTYSNFFAT_CHECK";
	        Timestamp fromDate = UtilDateTime.nowTimestamp();
	        Timestamp thruDate = UtilDateTime.nowTimestamp();	
	        List conditionList = FastList.newInstance();
	        List<GenericValue> orderItems = FastList.newInstance();
	        String  verifyApprove = "N";
	        if(UtilValidate.isNotEmpty(context.get("verifyApprove"))){
	        	verifyApprove = (String)context.get("verifyApprove");
	        }
	        try{
	    	   GenericValue customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);			 
			   if(UtilValidate.isEmpty(customTimePeriod)){
				  Debug.logError( "There no active billing time periods. ", module);				 
				  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
			    }
			    fromDate =UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		    	thruDate =UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));  
		    	GenericValue UnitDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", unitId),false);
		    	shedId = (String) UnitDetails.get("parentFacilityId");
		    	orderItems = (List<GenericValue>)context.get("orderItems");
		    	if(UtilValidate.isEmpty(orderItems)){
		    		 Map orderItemCtx = UtilMisc.toMap("userLogin",userLogin);
			    	 orderItemCtx.put("shedId", unitId);
					 orderItemCtx.put("fromDate", fromDate);
					 orderItemCtx.put("thruDate", thruDate);
					 orderItems = getProcurementOrderItemsForPeriod(dctx ,orderItemCtx);
		    	}		    	 	 
			    				   
		     }catch (GenericEntityException e) {
				// TODO: handle exception
	     		 Debug.logError(e.getMessage(), module);
	     		 return ServiceUtil.returnError(e.getMessage());
			}
		    if(UtilValidate.isEmpty(orderItems)){
		    	return ServiceUtil.returnSuccess("no order to process for the time period===>"+customTimePeriodId);
		    }		    
		    // remove error entries if there is any for custom time period
		    if(verifyApprove.equals("N")){
		       Map validationCtx =  UtilMisc.toMap("userLogin",userLogin);
		       validationCtx.put("shedId", unitId);
		       validationCtx.put("validationTypeId", validationTypeId);
		       validationCtx.put("customTimePeriodId", customTimePeriodId);
		       removeProcBillingValidation(dctx , validationCtx);
		    }
	        try{
        	   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, shedId));
        	   conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
			   EntityCondition ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List<GenericValue> validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);
			   if(UtilValidate.isEmpty(validationRuleList)){
				   conditionList.clear();
				   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, "_NA_"));
	        	   conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
				   ruleCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				    validationRuleList =delegator.findList("ProcBillingValidationRule", ruleCondition, null, null, null,false);				   
			   }
			   validationRuleList = EntityUtil.filterByDate(validationRuleList);
			   List<String> centerList = EntityUtil.getFieldListFromEntityList(orderItems, "originFacilityId", true);
	           
	           for(int i=0;i<centerList.size();i++){
		    	   String centerId = centerList.get(i);
		    	   GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId" ,centerId), false);
		    	   if(UtilValidate.isEmpty(facility.getString("categoryTypeEnum")) || ((UtilValidate.isNotEmpty(facility.getString("categoryTypeEnum"))&&!"RECVYBOOTH".equals(facility.getString("categoryTypeEnum"))))){
			    	   List<GenericValue> centerItemList = EntityUtil.filterByAnd(orderItems, UtilMisc.toMap("originFacilityId", centerId));
			    	   for(GenericValue centerItem : centerItemList ){
			    		    boolean isPretendEntry = false;
			    		    BigDecimal itemFat = centerItem.getBigDecimal("fat");
			    		    BigDecimal itemSnf = centerItem.getBigDecimal("snf");
			    		    BigDecimal itemSFat = centerItem.getBigDecimal("sFat");
			    		    BigDecimal quantity = centerItem.getBigDecimal("quantityKgs");
			    		    BigDecimal sQty = BigDecimal.ZERO;
			    		    if(UtilValidate.isNotEmpty(centerItem.getBigDecimal("sQuantityLtrs"))){
			    		    	sQty = centerItem.getBigDecimal("sQuantityLtrs");
			    		    }
			    		    BigDecimal cQtyLtrs = BigDecimal.ZERO;
			    		    if(UtilValidate.isNotEmpty(centerItem.getBigDecimal("cQuantityLtrs"))){
			    		    	 cQtyLtrs = centerItem.getBigDecimal("cQuantityLtrs");
			    		    }
			    		    List<GenericValue> entryValidationRule =  EntityUtil.filterByAnd(validationRuleList, UtilMisc.toMap("productId", centerItem.getString("productId")));
			    		    GenericValue validationRule =  EntityUtil.getFirst(entryValidationRule);
			    		   
			    		    if(UtilValidate.isNotEmpty(validationRule)){
			    		    	BigDecimal minFat = validationRule.getBigDecimal("minFat");
			    		    	BigDecimal maxFat = validationRule.getBigDecimal("maxFat");
			    		    	BigDecimal minSnf = validationRule.getBigDecimal("minSnf");
			    		    	BigDecimal maxSnf = validationRule.getBigDecimal("maxSnf");
			    		    	BigDecimal maxSFat = validationRule.getBigDecimal("maxSFat");
			    		    	BigDecimal qtyDivisor = validationRule.getBigDecimal("qtyDivisor");			    		    	
			    		    	if((UtilValidate.isNotEmpty(minFat) && (itemFat.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxFat)) &&
			    		    			( (minFat.compareTo(itemFat) >0) || (maxFat.compareTo(itemFat) <=0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if((itemFat.compareTo(BigDecimal.ZERO) ==0)&& (sQty.compareTo(BigDecimal.ZERO)==0)&& (cQtyLtrs.compareTo(BigDecimal.ZERO)==0)){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	//sour Fat check 
			    		    	if(UtilValidate.isNotEmpty(itemSFat)&&UtilValidate.isNotEmpty(maxSFat) &&((maxSFat.compareTo(itemSFat) <=0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if((UtilValidate.isNotEmpty(minSnf) && (itemSnf.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxSnf)) &&
			    		    			( (minSnf.compareTo(itemSnf) >0) ||	(maxSnf.compareTo(itemSnf) <=0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if((UtilValidate.isNotEmpty(minSnf) && (itemSnf.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxSnf)) &&
			    		    			( (minSnf.compareTo(itemSnf) >0) ||	(maxSnf.compareTo(itemSnf) <=0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if((itemSnf.compareTo(BigDecimal.ZERO) ==0) && (sQty.compareTo(BigDecimal.ZERO)==0)&& (cQtyLtrs.compareTo(BigDecimal.ZERO)==0)){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if(UtilValidate.isNotEmpty(quantity) && (quantity.compareTo(BigDecimal.ZERO)==0)&&((itemFat.compareTo(BigDecimal.ZERO)>0)||(itemSnf.compareTo(BigDecimal.ZERO)>0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if(UtilValidate.isNotEmpty(qtyDivisor)&&UtilValidate.isNotEmpty(quantity)&&(quantity.remainder(qtyDivisor)!=BigDecimal.ZERO)){
			    		    			isPretendEntry = true;
			    		    	}
			    		    	/*if((UtilValidate.isNotEmpty(minFat) && (itemFat.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxFat)) &&
			    		    			( (minFat.compareTo(itemFat) >0) || (maxFat.compareTo(itemFat) <=0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	//sour Fat check 
			    		    	if(UtilValidate.isNotEmpty(itemSFat)&&UtilValidate.isNotEmpty(maxSFat) &&((maxSFat.compareTo(itemSFat) <=0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if((UtilValidate.isNotEmpty(minSnf) && (itemSnf.compareTo(BigDecimal.ZERO) !=0) && UtilValidate.isNotEmpty(maxSnf)) &&
			    		    			( (minSnf.compareTo(itemSnf) >0) ||	(maxSnf.compareTo(itemSnf) <=0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if(UtilValidate.isNotEmpty(quantity) && (quantity.compareTo(BigDecimal.ZERO)==0)&&((itemFat.compareTo(BigDecimal.ZERO)>0)||(itemSnf.compareTo(BigDecimal.ZERO)>0))){
			    		    		isPretendEntry = true;
			    		    	}
			    		    	if(UtilValidate.isNotEmpty(qtyDivisor)&&UtilValidate.isNotEmpty(quantity)&&(quantity.remainder(qtyDivisor)!=BigDecimal.ZERO)){
			    		    			isPretendEntry = true;
			    		    	}*/
			    		    }
			    		  if(isPretendEntry){		    			  
					           String orderId = centerItem.getString("orderId");
			    			   String orderItemSeqId = centerItem.getString("orderItemSeqId");
			    			   conditionList.clear();
		    				   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"));		    	        	   
		    				   conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, unitId));
		    		           conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
		    	        	   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));
		    	        	   conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,orderItemSeqId));
		    	        	   conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
		    	        	   
		    				   EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    				   List<GenericValue> validationList =delegator.findList("ProcBillingValidation", condition, null, null, null,false);
		    				   
			    			   if(verifyApprove.equals("N")){
			    				   GenericValue validationEntry = delegator.makeValue("ProcBillingValidation");
				    			   validationEntry.set("shedId", unitId);
				    			   validationEntry.set("customTimePeriodId", customTimePeriodId);
				    			   validationEntry.set("validationTypeId", validationTypeId);
				    			   validationEntry.set("centerId", centerId);
				    			   validationEntry.set("orderId", centerItem.getString("orderId"));
				    			   validationEntry.set("orderItemSeqId", centerItem.getString("orderItemSeqId"));
				    			   validationEntry.set("statusId", "ITEM_CREATED");
				    			   validationEntry.set("approvedDate", UtilDateTime.nowTimestamp());
				    			   validationEntry.set("approvedByUserLoginId", userLogin.getString("userLoginId"));
				    			   if(UtilValidate.isEmpty(validationList)){
				    				   delegator.setNextSubSeqId(validationEntry, "sequenceNum", 5, 1);
							           delegator.create(validationEntry);
				    			   }
				    			  
			    			   }else{		    				   
			    				   if(UtilValidate.isEmpty(validationList)){
			    					   String errMsg = "There is some invalid Etrires please verify and approve or remove the entries and run the billing";
			    					   Debug.logError(errMsg+"==================="+validationTypeId, module);
			    					   return ServiceUtil.returnError(errMsg);
			    				   }
			    			   }
			    		   }
			    	   }
			    	}
		    	   
		       }
			   
	    	 }catch (Exception e) {
				// TODO: handle exception
	     		Debug.logError(e.getMessage(), module);
	     		return ServiceUtil.returnError(e.getMessage());
			}
	        return ServiceUtil.returnSuccess();
		}
	  
	  public static Map<String, Object> removeProcBillingValidation(DispatchContext dctx, Map<String, ? extends Object> context) {
		  	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	                
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        String shedId = (String) context.get("shedId");
	        String validationTypeId = (String) context.get("validationTypeId");
	        //String validationTypeId = "QTYSNFFAT_CHECK";
	        if(UtilValidate.isEmpty(customTimePeriodId) || UtilValidate.isEmpty(shedId)){
	        	 return ServiceUtil.returnSuccess();
	        }
	        try{
	        	 List conditionList = FastList.newInstance();
	        	 conditionList.add(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, shedId));
	        	 conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
	        	 if(UtilValidate.isNotEmpty(validationTypeId)){
	        		 conditionList.add(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS, validationTypeId));
	        	 }
	        	 
	        	 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_CREATED"));
				 EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				 List<GenericValue> errorEntryList =delegator.findList("ProcBillingValidation", condition, null, null, null,false);
	        	 delegator.removeAll(errorEntryList);
	        	 
		     }catch (GenericEntityException e) {
				// TODO: handle exception
	     		 Debug.logError(e.getMessage(), module);
	     		 return ServiceUtil.returnError(e.getMessage());
			}
		    
	        return ServiceUtil.returnSuccess();
		}
	  	public static Map<String, Object> checkProcPeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
	  		LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	  		String customTimePeriodId = (String)context.get("customTimePeriodId");
	  		String facilityId = (String)context.get("facilityId");
	  		GenericValue userLogin = (GenericValue)context.get("userLogin");
	  		GenericValue customTimePeriod = null;
	  		Map result = ServiceUtil.returnSuccess("success");
	  		if(UtilValidate.isEmpty(facilityId)){
	  			Debug.logError("facilityId is missing  ",module);
	  			result =	ServiceUtil.returnError("facilityId is missing  ");
	  			return result;
	  		}
	  		if(UtilValidate.isEmpty(customTimePeriodId)){
	  			Debug.logError("TimePeriod Or orderDate is missing  ",module);
	  			result =	ServiceUtil.returnError("TimePeriod Or orderDate is missing  ");
	  			return result;
	  		}
	  		
	  		List facilityIdsList = FastList.newInstance();
	  		facilityIdsList.add(facilityId);
	  		try{
	  			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
	  			String facilityTypeId = (String)facility.get("facilityTypeId");
	  			Map unitDetails = FastMap.newInstance();
	  			Map shedDetails = FastMap.newInstance();
	  			if("CENTER".equalsIgnoreCase(facilityTypeId)){
	  				Map centerDetailsMap = ProcurementNetworkServices.getCenterDtails(dctx, UtilMisc.toMap("userLogin", userLogin,"centerId",facilityId));
	  				if(ServiceUtil.isSuccess(centerDetailsMap)){
	  					shedDetails =(Map) centerDetailsMap.get("shedFacility");
	  					unitDetails =(Map) centerDetailsMap.get("unitFacility");
	  					if(UtilValidate.isNotEmpty(shedDetails)){
	  						facilityIdsList.add((String)shedDetails.get("facilityId"));
	  					}
	  					if(UtilValidate.isNotEmpty(unitDetails)){
	  						facilityIdsList.add((String)unitDetails.get("facilityId"));
	  					}
	  				}
	  			}else{
	  				Map shedDetailsMap = ProcurementNetworkServices.getShedDetailsForFacility(dctx, context);
	  				if(ServiceUtil.isSuccess(shedDetails)){
	  					shedDetails = (Map)shedDetailsMap.get("facility"); 
	  					facilityIdsList.add((String)shedDetails.get("facilityId"));
	  				}
	  			}
	  		}catch (GenericEntityException e) {
				// TODO: handle exception
	  			Debug.logError("Error while getting facilityIds List======>"+e, module);
	  			result = ServiceUtil.returnError("Error while getting facilityIds List======>"+e.getMessage());
	  			return result;
			}catch (Exception e) {
				// TODO: handle exception
	  			Debug.logError("Unknown Error while getting facilityIds List======>"+e, module);
	  			result = ServiceUtil.returnError("Unknown Error while getting facilityIds List======>"+e.getMessage());
	  			return result;
			}
	  		List<GenericValue> periodBillingList = FastList.newInstance();
		    try{
			   	List periodBillingConditionList = FastList.newInstance();
			   	periodBillingConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
			   	periodBillingConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
			   	periodBillingConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIdsList));
			   	EntityCondition periodBillingCondition = EntityCondition.makeCondition(periodBillingConditionList,EntityOperator.AND);
			   	periodBillingList = delegator.findList("PeriodBilling",periodBillingCondition,null,null , null,false);
	     	 }catch(GenericEntityException e){
			   	Debug.logError("Error while getting Period Billing List"+e.getMessage(),module);
	     	 }
	     	 if(UtilValidate.isNotEmpty(periodBillingList)){
			   	Debug.logError( "You can not make or Delete Entries for already billed Period ,Please contact administrator. ", module);				 
			   	result =  ServiceUtil.returnError("You can not make,edit or Delete Entries for already billed Period ,Please contact administrator.");
	     	 }
	     	 return result;
	  		
	  	}//End of the service
		/**
		 * Service for Adding Sour Distribution Advice
		 */
	  	public static Map<String, Object> addSourDistributionAdvice(DispatchContext dctx, Map<String, ? extends Object> context) {
		  	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	                
	        Map<String, Object> resultMap = FastMap.newInstance();
	        List<Map> adviceValueList  = (List) context.get("adviceValueList");
	        String milkTransferId = (String) context.get("milkTransferId");
	        resultMap = ServiceUtil.returnSuccess("Sucess");
	        List<GenericValue> milkTransferAdviceList = FastList.newInstance();
	   		try{
	   			if(UtilValidate.isEmpty(adviceValueList)){
	   				for(int i=0;i<10;i++){
	   					if(UtilValidate.isNotEmpty(context.get("facilityId_o_"+i))){
	   						Map adviceValueMap = FastMap.newInstance();
							adviceValueMap.put("facilityId",(String)context.get("facilityId_o_"+i));
							//adviceValueMap.put("quantity",(BigDecimal)context.get("quantity_o_"+i));
							if(!adviceValueList.contains(adviceValueMap)){
								adviceValueList.add(adviceValueMap);
							}
	   					}
	   				}
	   			}
	   			for(Map adviceValueMap : adviceValueList){
	        		String facilityId = (String) adviceValueMap.get("facilityId");	
	        		BigDecimal quantity = (BigDecimal) adviceValueMap.get("quantity");
	   				GenericValue milkTransferAdvice= delegator.findOne("MilkTransferAdvice", UtilMisc.toMap("milkTransferId", milkTransferId, "facilityId", facilityId),false);
	        			if(UtilValidate.isNotEmpty(milkTransferAdvice)){
	        				continue;
	        			}else{
		        			milkTransferAdvice = delegator.makeValue("MilkTransferAdvice");
		        			milkTransferAdvice.set("milkTransferId",milkTransferId);
		        			milkTransferAdvice.set("facilityId",facilityId);
		        			//milkTransferAdvice.set("quantityLtrs",quantity);
		        			milkTransferAdvice.set("createdByUserLogin", userLogin.get("userLoginId"));
		        			milkTransferAdviceList.add(milkTransferAdvice);
	        			}
	   			}
	   			if(!milkTransferAdviceList.isEmpty()){
	   				delegator.storeAll(milkTransferAdviceList);
	   			}
    		}catch (GenericEntityException e) {
					// TODO: handle exception
		        	Debug.logError("Error while creating SourDistribution Advice for milkTransferId :"+milkTransferId+"==================>"+e.getMessage(),module);
		        	return ServiceUtil.returnError("Error while creating SourDistribution Advice for milkTransferId :"+milkTransferId+"==================>"+e.getMessage());
			}
	        
	        return ServiceUtil.returnSuccess("Sour Distribution Advice Successfully created for MilkTransferId : "+milkTransferId);
		}//End of Service
	  	
	  	
	  	
	  	/**
		 * Service for Adding Sour Distribution Advice New
		 * 
		 * 
		 */
	  	public static Map<String, Object> addSourDistributionAdviceNew(DispatchContext dctx, Map<String, ? extends Object> context) {
		  	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");	                
	        Map<String, Object> resultMap = FastMap.newInstance();
	        String milkTransferId = (String) context.get("milkTransferId");
	        BigDecimal gheeYield = (BigDecimal) context.get("gheeYield");
	        List unitIds = (List)context.get("unitIds");
	        resultMap = ServiceUtil.returnSuccess("Sucess");
	        List<GenericValue> milkTransferAdviceList = FastList.newInstance();
	   		try{
	   			if(UtilValidate.isNotEmpty(gheeYield) && gheeYield.compareTo(BigDecimal.ZERO)>0){
	   				GenericValue milkTransfer = delegator.findByPrimaryKey("MilkTransfer", UtilMisc.toMap("milkTransferId",milkTransferId));
	   				milkTransfer.put("gheeYield", gheeYield);
	   				milkTransfer.store(); 
	   			}
	   			if(UtilValidate.isNotEmpty(unitIds)){
	   				List<Map> adviceValueList = FastList.newInstance();
		   			
		   			if(UtilValidate.isEmpty(adviceValueList)){
		   				for(int i=0;i<unitIds.size();i++){
		   						Map adviceValueMap = FastMap.newInstance();
								adviceValueMap.put("facilityId",(String)unitIds.get(i));
								if(!adviceValueList.contains(adviceValueMap)){
									adviceValueList.add(adviceValueMap);
								}
		   				}
		   			}
		   			for(Map adviceValueMap : adviceValueList){
		        		String facilityId = (String) adviceValueMap.get("facilityId");	
		        		BigDecimal quantity = (BigDecimal) adviceValueMap.get("quantity");
		   				GenericValue milkTransferAdvice= delegator.findOne("MilkTransferAdvice", UtilMisc.toMap("milkTransferId", milkTransferId, "facilityId", facilityId),false);
		        			if(UtilValidate.isNotEmpty(milkTransferAdvice)){
		        				continue;
		        			}else{
			        			milkTransferAdvice = delegator.makeValue("MilkTransferAdvice");
			        			milkTransferAdvice.set("milkTransferId",milkTransferId);
			        			milkTransferAdvice.set("facilityId",facilityId);
			        			//milkTransferAdvice.set("quantityLtrs",quantity);
			        			milkTransferAdvice.set("createdByUserLogin", userLogin.get("userLoginId"));
			        			milkTransferAdviceList.add(milkTransferAdvice);
		        			}
		   			}
	   				
	   				
	   			}
	   			
	   			if(!milkTransferAdviceList.isEmpty()){
	   				delegator.storeAll(milkTransferAdviceList);
	   			}
    		}catch (GenericEntityException e) {
					// TODO: handle exception
		        	Debug.logError("Error while creating SourDistribution Advice for milkTransferId :"+milkTransferId+"==================>"+e.getMessage(),module);
		        	return ServiceUtil.returnError("Error while creating SourDistribution Advice for milkTransferId :"+milkTransferId+"==================>"+e.getMessage());
			}
	        
	        return ServiceUtil.returnSuccess("Sour Distribution Advice Successfully created for MilkTransferId : "+milkTransferId);
		}//End of Service
	  	
	  	
	  	
	  	/**
	  	 * This service returns customTimePeriod List for the given facilityId.
	  	 * if custom time period not available for that facility it will return ancestor facilities timePeriods
	  	 * @param ctx
	  	 * @param context
	  	 * @return
	  	 */
	  	public static Map<String, Object> getFacilityCustomTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		Map resultMap = FastMap.newInstance();
	  		Delegator delegator = ctx.getDelegator();
	  		LocalDispatcher dispatcher = ctx.getDispatcher();
	  		GenericValue userLogin = (GenericValue) context.get("userLogin");
	  		String facilityId = (String)context.get("facilityId");
	  		Timestamp fromDate = (Timestamp)context.get("fromDate");
	  		GenericValue customTimePeriod = null;
	  		List<GenericValue> customTimePeriodList = FastList.newInstance();
	  		List<GenericValue> activeCustomTimePeriodList = FastList.newInstance();
	  		String facilityTypeId = null; 
	  		if(UtilValidate.isEmpty(facilityId)&&UtilValidate.isEmpty(fromDate)){
	  			Debug.logError("facilityId or from Date is missing",module);
	  			return ServiceUtil.returnError("facilityId or from Date is missing");
	  		}
	  		List<GenericValue> tempCustomTimePeriodList = FastList.newInstance(); 
	  		List conditionList = FastList.newInstance();
	  		conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"PROC_BILL_MONTH"));
	  		conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
	  		if(UtilValidate.isNotEmpty(fromDate)){
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(fromDate.getTime())));
	     		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(fromDate.getTime())));
	  		}
	  		try{
  				GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
  				facilityTypeId = (String) facility.get("facilityTypeId");
		  		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,facilityId));
		  		EntityCondition  condition =  EntityCondition.makeCondition(conditionList,EntityOperator.AND);
  				tempCustomTimePeriodList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",condition ,null,null, null,false);
  				if(UtilValidate.isNotEmpty(tempCustomTimePeriodList)){
  					for(GenericValue tempCustomTimePeriod : tempCustomTimePeriodList){
	  						if(UtilValidate.isEmpty(activeCustomTimePeriodList)&& UtilValidate.isNotEmpty(fromDate)){
	  							activeCustomTimePeriodList.add(tempCustomTimePeriod);
	  						}
							customTimePeriodList.add(tempCustomTimePeriod);
	  					}
				  		resultMap = ServiceUtil.returnSuccess();
				  		resultMap.put("customTimePeriod", EntityUtil.getFirst(activeCustomTimePeriodList));
				  		resultMap.put("customTimePeriodList", customTimePeriodList);
				  		return resultMap;
				  		
  				}
  				if("SHED".equalsIgnoreCase(facilityTypeId)){
  					/*conditionList.clear();
  					conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"PROC_BILL_MONTH"));
  			  		conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
  			  		if(UtilValidate.isNotEmpty(fromDate)){
  						conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(fromDate.getTime())));
  			     		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(fromDate.getTime())));
  			  		}
  					condition =  EntityCondition.makeCondition(conditionList,EntityOperator.AND);
  	  				tempCustomTimePeriodList = delegator.findList("CustomTimePeriod",condition ,null,null, null,false);	
  					for(GenericValue tempCustomTimePeriod : tempCustomTimePeriodList){
  						if(UtilValidate.isEmpty(activeCustomTimePeriodList)&& UtilValidate.isNotEmpty(fromDate)){
  							activeCustomTimePeriodList.add(tempCustomTimePeriod);
  						}
						customTimePeriodList.add(tempCustomTimePeriod);
  					}
			  		resultMap = ServiceUtil.returnSuccess();
			  		resultMap.put("customTimePeriod", EntityUtil.getFirst(activeCustomTimePeriodList));
			  		resultMap.put("customTimePeriodList", customTimePeriodList);*/
  					resultMap = ServiceUtil.returnError("CustomTimePeriod is Not Configured for this Facility ");
			  		return resultMap;
  				}
  				facilityId = (String)facility.get("parentFacilityId");
				Map getFacilityCustomMap = FastMap.newInstance();
				getFacilityCustomMap.put("facilityId",facilityId);
				getFacilityCustomMap.put("fromDate",fromDate);
				getFacilityCustomMap.put("userLogin",userLogin);
				return getFacilityCustomTimePeriod(ctx, getFacilityCustomMap);
  				
  			}catch (GenericEntityException e) {
				// TODO: handle exception
  				Debug.logError("Error while getting CustomTimePeriods========>"+e.getMessage(),module);
  				resultMap = ServiceUtil.returnError("Error while getting CustomTimePeriods========>"+e.getMessage());
			}
  			return resultMap;
	  		
	  	}//End of Service
	  	/**
	  	 * 
	  	 * @param ctx
	  	 * @param context
	  	 * @return
	  	 */
	  	public static Map<String, Object> getFacilityTenantConfigurations(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		Map resultMap = FastMap.newInstance();
	  		Delegator delegator = ctx.getDelegator();
	  		LocalDispatcher dispatcher = ctx.getDispatcher();
	  		GenericValue userLogin = (GenericValue) context.get("userLogin");
	  		String facilityId = (String)context.get("facilityId");
	  		String facilityTypeId = null;
	  		List<GenericValue> tenantConfigurationsList = FastList.newInstance();
	  		try{
	  			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
  				facilityTypeId = (String) facility.get("facilityTypeId");
	  			Map tenantConfigMap = FastMap.newInstance();
	  			tenantConfigurationsList = delegator.findList("FacilityAttribute", EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId), null, null, null, false);
	  			if(UtilValidate.isNotEmpty(tenantConfigurationsList)){
	  				for(GenericValue tenantConfig : tenantConfigurationsList){
	  					tenantConfigMap.put((String)tenantConfig.get("attrName"),(String)tenantConfig.get("attrValue"));
	  				}
	  				resultMap.put("tenantConfigurationsMap",tenantConfigMap);
	  				return resultMap;
	  			}
	  			if("SHED".equalsIgnoreCase(facilityTypeId)){
	  				tenantConfigurationsList = delegator.findList("TenantConfiguration", EntityCondition.makeCondition("propertyTypeEnumId",EntityOperator.EQUALS,"MILK_PROCUREMENT"), null, null, null, false);
	  				for(GenericValue tenantConfig : tenantConfigurationsList){
	  					tenantConfigMap.put((String)tenantConfig.get("propertyName"),(String)tenantConfig.get("propertyValue"));
	  				}
	  				resultMap.put("tenantConfigurationsMap",tenantConfigMap);
	  				return resultMap;
				}
				facilityId = (String)facility.get("parentFacilityId");
				Map getFacilityTenantMap = FastMap.newInstance();
				getFacilityTenantMap.put("facilityId",facilityId);
				getFacilityTenantMap.put("userLogin",userLogin);
				return getFacilityTenantConfigurations(ctx, getFacilityTenantMap);
		  	}catch(GenericEntityException e){
		  		Debug.logError("Error while getting Tenant Configurations", module);
		  		resultMap = ServiceUtil.returnError("Error while getting Tenant Configurations");
		  	}
	  		return resultMap;
	  	}//end of the service
	  	
	  	/**
	  	 *  service to create centers from Dbf data
	  	 * @param ctx
	  	 * @param context
	  	 * @return
	  	 */
	  	public static Map<String, Object> mpMasToVbiz(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		 Map resultMap = ServiceUtil.returnSuccess();
	  		 Delegator delegator = ctx.getDelegator();
	  	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	  	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	 	 String file_name = (String) context.get("file_name");
	  	 	 String shedId = (String) context.get("shedId");
	  	 	 Map unitDetailsMap = FastMap.newInstance();
	  	 	 Map unitDetailsInMap = FastMap.newInstance();
	  	 	 unitDetailsInMap.put("shedId",shedId);
	  	 	 unitDetailsInMap.put("userLogin",userLogin);
	  	 	 // here we are taking previous year start as openedDate
	  	 	 Timestamp openedDate = UtilDateTime.getYearStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.getYearStart(UtilDateTime.nowTimestamp()), -1));
	  	 	 List<GenericValue> unitDetailsList = FastList.newInstance();
	  	 	 List ignoredDetailsList = FastList.newInstance();
	  	 	 try{
	  	 		 unitDetailsMap = dispatcher.runSync("getShedUnitsByShed",unitDetailsInMap);
	  	 	 }catch (Exception e) {
	  	 		 Debug.logError("Error while getting Units for the given ShedId=======>"+e,module);
	  	 		 resultMap = ServiceUtil.returnError("Error while getting Units for the given ShedId=======>"+shedId);
	  	 		 return resultMap;
	  	 	 }
	  	 	 unitDetailsList = (List)unitDetailsMap.get("unitsDetailList");
	  	 	 try{
	  	 		 InputStream fileInputStream = new FileInputStream(file_name);
	  	 		 DBFReader reader = new DBFReader( fileInputStream); 
	  	 		 int numberOfFields = reader.getFieldCount();
	  	 		 //To know the field Indexes
	  	 		 /*for(int i=0;i<numberOfFields;i++){
	  	 			 Debug.log("field :"+i+"  ========>"+reader.getField(i).getName());
	  	 		 }*/
	  	 		 Object []rowObjects;
	  	 		 /* here we know the field Indexes  */
	  	 	    while( (rowObjects = reader.nextRecord()) != null) {
	  	 	    	if(UtilValidate.isNotEmpty(rowObjects)){
	  	 	    		Map ignoredDetailsMap = FastMap.newInstance();
	  	 	    		Map tempMap = FastMap.newInstance();
	  	 	    		tempMap.put("parentFacilityId", "");
	  		 			tempMap.put("unitCode", "");
	  		 			tempMap.put("centerCode", "");
	  		 			tempMap.put("routeCode", "");
	  		 			tempMap.put("centerName", "");
	  		 			tempMap.put("presidentName", "");
	  		 			//tempMap.put("commission", BigDecimal.ZERO);
	  		 			//tempMap.put("cartage", BigDecimal.ZERO);
	  		 			tempMap.put("gbCode", "");
	  		 			tempMap.put("bCode", "");
	  		 			tempMap.put("bankAcctNumber", "");
	  		 			//tempMap.put("incentives", Boolean.FALSE);
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[0])){
	  		 	    		tempMap.put("unitCode", Double.toString((Double)rowObjects[0]).replace(".0", ""));
	  		 	    		ignoredDetailsMap.put("unitCode", tempMap.get("unitCode"));
	  		 	    	 }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[1])){
	  		 	    		tempMap.put("centerCode", Double.toString((Double)rowObjects[1]).replace(".0",""));
	  		 	    		ignoredDetailsMap.put("centerCode", tempMap.get("centerCode"));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[3])){
	  		 	    		tempMap.put("routeCode", Double.toString((Double)rowObjects[3]).replace(".0",""));
	  		 	    		ignoredDetailsMap.put("routeCode", tempMap.get("routeCode"));
	  		 	    		
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[4])){
	  		 	    		tempMap.put("centerName", rowObjects[4].toString().trim().replace("<", "(").replace(">", ")"));
	  		 	    		ignoredDetailsMap.put("centerName", tempMap.get("centerName"));
	  		 	    	}
	  		 	    	if(UtilValidate.isEmpty(tempMap.get("centerName"))||UtilValidate.isEmpty(tempMap.get("centerCode"))){
	  		 	    		ignoredDetailsList.add(ignoredDetailsMap);
	  		 	    		continue;
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[5])){
	  		 	    		tempMap.put("presidentName",rowObjects[5].toString().trim().replace("<", "(").replace(">", ")"));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[6])){
	  		 	    		tempMap.put("commission", new BigDecimal((Double)rowObjects[6]));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[7])){
	  		 	    		tempMap.put("cartage", new BigDecimal ((Double)rowObjects[7]));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[8])){
	  		 	    		tempMap.put("gbCode", Double.toString((Double)rowObjects[8]).replace(".0", ""));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[9])){
	  		 	    		tempMap.put("bCode", Double.toString((Double)rowObjects[9]).replace(".0", ""));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[10])){
	  		 	    		String bankAcctNumber = null;
	  		 	    		if(rowObjects[10] instanceof String){
	  		 	    			bankAcctNumber = (String) rowObjects[10];
	  		 	    		}else if(rowObjects[10] instanceof Double){
	  		 	    			BigDecimal tempAccNumber = new BigDecimal((Double)rowObjects[10]);
	  		 	    			bankAcctNumber = tempAccNumber.toString();
	  		 	    		}
	  		 	    		if(bankAcctNumber.endsWith(".0")){
	  	 	    				bankAcctNumber = (bankAcctNumber.replace(".0", ""));
	  		 	    		}
	  		 	    		if(bankAcctNumber.contains("E")){
	  		 	    			bankAcctNumber = bankAcctNumber.replace(bankAcctNumber.substring(bankAcctNumber.indexOf("E")),"");
	  		 	    		}
	  		 	    		if(bankAcctNumber.contains(".")){
	  		 	    			bankAcctNumber = (bankAcctNumber.replace(".", ""));
	  		 	    		}
	  		 	    		tempMap.put("bankAcctNumber",bankAcctNumber);
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(tempMap.get("bankAcctNumber"))&&UtilValidate.isEmpty(tempMap.get("presidentName"))){
	  		 	    		tempMap.put("presidentName", tempMap.get("centerName"));
	  		 	    	}
	  		 	    	/*if(UtilValidate.isNotEmpty(rowObjects[11])){
	  		 	    		incentives = (Boolean)rowObjects[11]; 
	  		 	    	}*/
	  			        List unitDetails = (List)EntityUtil.filterByCondition(unitDetailsList, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId" ,EntityOperator.EQUALS,"UNIT"),EntityOperator.AND, EntityCondition.makeCondition("facilityCode" ,EntityOperator.EQUALS,tempMap.get("unitCode"))));
	  			        if(UtilValidate.isEmpty(unitDetails)){
	  		 	    		ignoredDetailsMap.put("reason", "unit Not Found");
	  		 	    		ignoredDetailsList.add(ignoredDetailsMap);
	  			        	continue;
	  			        }
	  			        String unitId = (String)((Map)(unitDetails.get(0))).get("facilityId");
	  			        // here we are getting unit routes
	  			        Map routeDetailsMap = FastMap.newInstance();
		 	        	Map routeDetailsInMap = FastMap.newInstance();
		 	        	routeDetailsInMap.put("userLogin",userLogin);
		 	        	routeDetailsInMap.put("unitId",unitId);
		 	        	routeDetailsMap = dispatcher.runSync("getUnitRoutes",routeDetailsInMap);
		 	        	List<GenericValue> routeDetailsList = (List)routeDetailsMap.get("routesDetailList");
		 	        	if(routeDetailsList.size()<1){
	  		 	    		ignoredDetailsMap.put("reason", "routes are not Available");
	  		 	    		ignoredDetailsList.add(ignoredDetailsMap);
  		 	        		continue;
  		 	        	}
	  			        //tempMap.put("parentFacilityId", unitId);
	  			        if(UtilValidate.isNotEmpty(tempMap.get("routeCode"))){
	  		 	        	List routeDetails = (List)EntityUtil.filterByCondition(routeDetailsList, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId" ,EntityOperator.EQUALS,"PROC_ROUTE"),EntityOperator.AND, EntityCondition.makeCondition("facilityCode" ,EntityOperator.EQUALS,tempMap.get("routeCode"))));
	  		 	        	if(UtilValidate.isEmpty(routeDetails)){
		  		 	    		ignoredDetailsMap.put("reason", "route Not Found");
		  		 	    		ignoredDetailsList.add(ignoredDetailsMap);
	  		 	        		continue;
	  		 	        	}
	  		 	        	tempMap.put("parentFacilityId",(String)((Map)(routeDetails.get(0))).get("facilityId"));
	  		 	        }else{
	  		 	        	// when routeCode is empty then Store the center under the available route
	  		 	        	GenericValue routeDetailMap =(GenericValue)EntityUtil.getFirst(routeDetailsList) ;
	  		 	        	String routeId = (String)routeDetailMap.get("facilityId"); 
	  		 	        	tempMap.put("parentFacilityId", routeId);
	  		 	        }
	  			        //creating Procurement Facility
	  			        Map createFacilityInMap = FastMap.newInstance();
	  			        createFacilityInMap.put("unitId",unitId);
	  			        createFacilityInMap.put("facilityCode",(String)tempMap.get("centerCode"));
	  			        createFacilityInMap.put("facilityName",(String)tempMap.get("centerName"));
	  			        createFacilityInMap.put("userLogin",userLogin);
	  			        createFacilityInMap.put("facilityTypeId","CENTER");
	  			        createFacilityInMap.put("parentFacilityId",(String)tempMap.get("parentFacilityId"));
	  			        createFacilityInMap.put("firstName",(String)tempMap.get("presidentName"));
	  			        createFacilityInMap.put("finAccountCode",(String)tempMap.get("bankAcctNumber"));
	  			        createFacilityInMap.put("finAccountTypeId","BANK_ACCOUNT");
	  			       
	  			        createFacilityInMap.put("openedDate",openedDate);
	  			        createFacilityInMap.put("gbCode",(String)tempMap.get("gbCode"));
	  			        createFacilityInMap.put("bCode",(String)tempMap.get("bCode"));
	  			        Map facilityMap = createProcFacility(ctx, createFacilityInMap);
	  			        if(ServiceUtil.isError(facilityMap)){
	  			        	Debug.log("Error msg "+facilityMap);
	  			        	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(facilityMap));
	  			        }
	  	 	    	}
	  	 	    }
	  	 	     fileInputStream.close();
	  	 	 }catch (DBFException e) {
	  	 		Debug.logError("Error while getting data from given file=======>"+e,module);
	  			/* resultMap = ServiceUtil.returnError("Error while getting data from given file=======>"+e.getMessage());
	  			 return resultMap;*/
	  		}catch (IOException ioe_ex) {
	  			// TODO: handle exception
	  	 		 Debug.logInfo("error while getting fields from the file"+ioe_ex, module);			
	  	 		 resultMap = ServiceUtil.returnError("error while getting fields from the file"+ioe_ex.getMessage());
	  	 	 }catch (Exception e) {
	  			// TODO: handle exception
	  			 Debug.logError("Unknown Error while creating centers =======>"+e,module);
	  			 resultMap = ServiceUtil.returnError("Unknown Error while creating centers =======>"+e.getMessage());
	  			 return resultMap;
	  		}
	  		resultMap = ServiceUtil.returnSuccess("Centers Created Successfully. ");
	  		if(UtilValidate.isNotEmpty(ignoredDetailsList)){
	  			resultMap = ServiceUtil.returnSuccess("Centers Created Successfully. The following List is ignored due to some fields are missed :"+ignoredDetailsList+" Number of Records ignored :"+ignoredDetailsList.size());
	  		}
	  		 return resultMap;
	  	 }// end of the service
	  	/**
	  	 * Service for creating routes from the dbf file
	  	 * @param ctx
	  	 * @param context
	  	 * @return
	  	 */
	  	 public static Map<String, Object> rtMasToVbiz(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		 Map resultMap = ServiceUtil.returnSuccess();
	  		 Delegator delegator = ctx.getDelegator();
	  	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	  	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	 	 String file_name = (String) context.get("file_name");
	  	 	 String shedId = (String) context.get("shedId");
	  	 	 Map unitDetailsMap = FastMap.newInstance();
	  	 	 Map unitDetailsInMap = FastMap.newInstance();
	  	 	 unitDetailsInMap.put("shedId",shedId);
	  	 	 unitDetailsInMap.put("userLogin",userLogin);
	  	 	 // here we are taking previous year start as openedDate
	  	 	 Timestamp openedDate = UtilDateTime.getYearStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.getYearStart(UtilDateTime.nowTimestamp()), -1));
	  	 	 List<GenericValue> unitDetailsList = FastList.newInstance();
	  	 	 List ignoredDetailsList = FastList.newInstance();
	  	 	 try{
	  	 		 unitDetailsMap = dispatcher.runSync("getShedUnitsByShed",unitDetailsInMap);
	  	 	 }catch (Exception e) {
	  	 		 Debug.logError("Error while getting Units for the given ShedCode=======>"+e,module);
	  	 		 resultMap = ServiceUtil.returnError("Error while getting Units for the given ShedId=======>"+shedId);
	  	 		 return resultMap;
	  	 	 }
	  	 	 unitDetailsList = (List)unitDetailsMap.get("unitsDetailList");
	  	 	 try{
	  	 		 InputStream fileInputStream = new FileInputStream(file_name);
	  	 		 DBFReader reader = new DBFReader( fileInputStream); 
	  	 		 int numberOfFields = reader.getFieldCount();
	  	 		//To know the field Indexes
	  	 		 /*for(int i=0;i<numberOfFields;i++){
	  	 			 Debug.log("field :"+i+"  ========>"+reader.getField(i).getName());
	  	 		 }*/
	  	 		 Object []rowObjects;
	  	 		 /* here we know the field Indexes  */
	  	 	    while( (rowObjects = reader.nextRecord()) != null) {
	  	 	    	
	  	 	    	Map ignoredDetailsMap = FastMap.newInstance();
	  		    	Map tempMap = FastMap.newInstance();
	  	 	    	if(UtilValidate.isNotEmpty(rowObjects)){
	  		 			 tempMap.put("parentFacilityId", "");
	  		 	    	 tempMap.put("unitCode","");
	  		 	    	 tempMap.put("routeCode","");
	  		 	    	 tempMap.put("routeName", "");
	  		 	    	 
	  		 	    	
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[0])){
	  		 	    		tempMap.put("unitCode", Double.toString((Double)rowObjects[0]).replace(".0", ""));
	  		 	    		ignoredDetailsMap.put("unitCode", tempMap.get("unitCode"));
	  		 	    	 }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[1])){
	  		 	    		tempMap.put("routeCode", Double.toString((Double)rowObjects[1]).replace(".0",""));
	  		 	    		ignoredDetailsMap.put("RNO", tempMap.get("routeCode"));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[2])){
	  		 	    		tempMap.put("routeName", ((String)rowObjects[2]).trim().replace("<", "(").replace(">", ")"));
	  		 	    		ignoredDetailsMap.put("RName", tempMap.get("routeName"));
	  		 	    	}
	  		 	    	if(UtilValidate.isEmpty(tempMap.get("routeName"))){
  		 	    			tempMap.put("routeName", tempMap.get("routeCode"));
  		 	    		}
	  			        if(UtilValidate.isEmpty(tempMap.get("routeCode"))){
	  			        	ignoredDetailsList.add(ignoredDetailsMap);
	  			        	continue;
	  			        }
	  		 	    	List unitDetails = (List)EntityUtil.filterByCondition(unitDetailsList, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId" ,EntityOperator.EQUALS,"UNIT"),EntityOperator.AND, EntityCondition.makeCondition("facilityCode" ,EntityOperator.EQUALS,tempMap.get("unitCode")) ));
	  			        if(UtilValidate.isEmpty(unitDetails)){
	  			        	 continue;
	  			        }
	  			        String unitId = (String)((Map)(unitDetails.get(0))).get("facilityId");
	  			        tempMap.put("parentFacilityId", unitId);
	  			        //creating Procurement Facility
	  			        Map createFacilityInMap = FastMap.newInstance();
	  			        createFacilityInMap.put("facilityCode",tempMap.get("routeCode"));
	  			        createFacilityInMap.put("facilityName",tempMap.get("routeName"));
	  			        createFacilityInMap.put("firstName",tempMap.get("routeName"));
	  			        createFacilityInMap.put("userLogin",userLogin);
	  			        createFacilityInMap.put("facilityTypeId","PROC_ROUTE");
	  			        createFacilityInMap.put("parentFacilityId",tempMap.get("parentFacilityId"));
	  			        
	  			        Map facilityMap = createProcFacility(ctx, createFacilityInMap);
	  			        if(ServiceUtil.isError(facilityMap)){
	  			        	ignoredDetailsMap.put("reason", ServiceUtil.getErrorMessage(facilityMap));
	  			        	ignoredDetailsList.add(ignoredDetailsMap);
	  			        	Debug.log("Error msg "+facilityMap);
	  			        	continue;
	  			        }
	  	 	    	}
	  	 	    }
	  	 	     fileInputStream.close();
	  	 	 }catch (DBFException e) {
	  	 		Debug.logError("Error while getting data from given file=======>"+e,module);
	  			/* resultMap = ServiceUtil.returnError("Error while getting data from given file=======>"+e.getMessage());
	  			 return resultMap;*/
	  		}catch (IOException ioe_ex) {
	  			// TODO: handle exception
	  	 		 Debug.logInfo("error while getting fields from the file"+ioe_ex, module);			
	  	 		 resultMap = ServiceUtil.returnError("error while getting fields from the file"+ioe_ex.getMessage());
	  	 	 }catch (Exception e) {
	  			// TODO: handle exception
	  			Debug.logError("Error while getting routes for the given UnitCode=======>"+e,module);
	  			 resultMap = ServiceUtil.returnError("Error while getting routes for the given UnitCode=======>"+e.getMessage());
	  			 return resultMap;
	  		}
	  		resultMap = ServiceUtil.returnSuccess("Routes Created Successfully. ");
	  		if(UtilValidate.isNotEmpty(ignoredDetailsList)){
	  			resultMap = ServiceUtil.returnSuccess("Routes Created Successfully. The following List is ignored due to some fields are missed :"+ignoredDetailsList+" Number of Records ignored :"+ignoredDetailsList.size());
	  		}
	  		return resultMap;
	  	 }
	  	/**
	  	 * Service for creating units from the dbf file
	  	 * @param ctx
	  	 * @param context
	  	 * @return
	  	 */
	  	 public static Map<String, Object> unitMasToVbiz(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		 Map resultMap = ServiceUtil.returnSuccess();
	  		 Delegator delegator = ctx.getDelegator();
	  	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	  	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	 	 String file_name = (String) context.get("file_name");
	  	 	 String shedId = (String) context.get("shedId");
	  	 	 // here we are taking previous year start as openedDate
	  	 	 Timestamp openedDate = UtilDateTime.getYearStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.getYearStart(UtilDateTime.nowTimestamp()), -1));
	  	 	 List ignoredDetailsList = FastList.newInstance();
	  	 	 try{
	  	 		 List condList = UtilMisc.toList(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_SCHEME_TYPE"));
	  	 		 condList.add(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_MNGNBY_TYPE"));
	  	 		 EntityCondition condition = EntityCondition.makeCondition(condList,EntityOperator.OR);
	  	 		 List enumerationList = delegator.findList("Enumeration",condition ,null, null, null,false);
	  	 		 //here we are getting Managed By
		  	 	 List<GenericValue> managedByList = (List)EntityUtil.filterByCondition(enumerationList,EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_MNGNBY_TYPE"));
		  	 	 Map mngByMap = FastMap.newInstance();
		  	 	 for(GenericValue manage : managedByList){
		  	 		mngByMap.put(manage.get("sequenceId"), manage.get("enumId"));
		  	 	 }
	  	 		 
		  	 	 //here we are getting scemeTypes
		  	 	List<GenericValue> schemesList = (List)EntityUtil.filterByCondition(enumerationList,EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_SCHEME_TYPE"));
		  	 	Map schemeMap = FastMap.newInstance();
		  	 	 for(GenericValue scheme : schemesList){
		  	 		schemeMap.put(scheme.get("sequenceId"), scheme.get("enumId"));
		  	 	 } 
	  	 		 String parentFacilityId = shedId;
	  	 		 InputStream fileInputStream = new FileInputStream(file_name);
	  	 		 DBFReader reader = new DBFReader( fileInputStream); 
	  	 		 int numberOfFields = reader.getFieldCount();
	  	 		 //to know the field Indexes
	  	 		/* for(int i=0;i<numberOfFields;i++){
	  	 			 Debug.log("field :"+i+"  ========>"+reader.getField(i).getName());
	  	 		 }*/
	  	 		 Object []rowObjects;
	  	 		 /* here we know the field Indexes  */
	  	 	    while( (rowObjects = reader.nextRecord()) != null) {
	  	 	    	if(UtilValidate.isNotEmpty(rowObjects)){
	  	 	    		Map ignoredDetailsMap = FastMap.newInstance();
	  	 	    		Map tempMap = FastMap.newInstance();
	  	 	    		tempMap.put("unitCode","");
	  	 	    		tempMap.put("unitName","");
	  	 	    		tempMap.put("presidentName","");
	  	 	    		tempMap.put("opCost",BigDecimal.ZERO);
	  	 	    		tempMap.put("eOpCost",BigDecimal.ZERO);
	  	 	    		tempMap.put("gbCode","");
	  	 	    		tempMap.put("bCode","");
	  	 	    		tempMap.put("district","");
	  		 	    	tempMap.put("facilitySize",BigDecimal.ZERO );
	  	 	    		tempMap.put("bankAcctNumber","");
	  	 	    		tempMap.put("schemeTypeId","");
	  	 	    		tempMap.put("managedBy","");
	  		 	    	Boolean incentives = false ;
	  		 	    	
	  		 	    	
  			        	
	  		 	    	// for date field 5
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[0])){
	  		 	    		tempMap.put("unitCode", Double.toString((Double)rowObjects[0]).replace(".0", ""));
	  		 	    		ignoredDetailsMap.put("unitCode",tempMap.get("unitCode"));
	  		 	    	 }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[1])){
	  		 	    		tempMap.put("unitName",((String)rowObjects[1]).trim().replace("<", "(").replace(">", ")"));
	  		 	    		ignoredDetailsMap.put("unitName",tempMap.get("unitName"));
	  		 	    	}
	  		 	    	
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[3])){
	  		 	    		String seqId = Double.toString((Double)rowObjects[3]).replace(".0", "");
	  		 	    		if(((Double)rowObjects[3])<10){
	  		 	    			seqId="0".concat(seqId);
	  		 	    		}
	  		 	    		if(UtilValidate.isNotEmpty(schemeMap)){
	  		 	    			tempMap.put("schemeTypeId", schemeMap.get(seqId));
	  		 	    		}
	  		 	    	 }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[4])){
	  		 	    		String seqId = Double.toString((Double)rowObjects[4]).replace(".0", ""); 
	  		 	    		if(((Double)rowObjects[4])<10){
	  		 	    			seqId="0".concat(seqId);
	  		 	    		}
	  		 	    		if(UtilValidate.isNotEmpty(mngByMap)){
	  		 	    			tempMap.put("managedBy", mngByMap.get(seqId));
	  		 	    		}
	  		 	    	 }
	  		 	    	
	  		 	    	if(UtilValidate.isEmpty(tempMap.get("unitName"))||UtilValidate.isEmpty(tempMap.get("unitCode"))){
	  			        	ignoredDetailsList.add(ignoredDetailsMap);
	  			        	continue;
	  			        }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[5])){
	  		 	    		openedDate = UtilDateTime.toTimestamp((java.util.Date)rowObjects[5]);
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[6])){
	  		 	    		tempMap.put("facilitySize", new BigDecimal((Double)rowObjects[6]));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[7])){
	  		 	    		tempMap.put("opCost", new BigDecimal((Double)rowObjects[7]));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[8])){
	  		 	    		tempMap.put("eOpCost",new BigDecimal ((Double)rowObjects[8]));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[10])){
	  		 	    		tempMap.put("gbCode", Double.toString((Double)rowObjects[10]).replace(".0", ""));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[11])){
	  		 	    		tempMap.put("bCode", Double.toString((Double)rowObjects[11]).replace(".0", ""));
	  		 	    	}
	  		 	    	
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[12])){
	  		 	    		String bankAcctNumber = null;
	  		 	    		if(rowObjects[12] instanceof String){
	  		 	    			bankAcctNumber = (String) rowObjects[12];
	  		 	    		}else if(rowObjects[12] instanceof Double){
	  		 	    			BigDecimal tempAccNumber = new BigDecimal((Double)rowObjects[12]);
	  		 	    			bankAcctNumber = tempAccNumber.toString();
	  		 	    		}
	  		 	    		if(bankAcctNumber.endsWith(".0")){
	  	 	    				bankAcctNumber = (bankAcctNumber.replace(".0", ""));
	  		 	    		}
	  		 	    		if(bankAcctNumber.contains("E")){
	  		 	    			bankAcctNumber = bankAcctNumber.replace(bankAcctNumber.substring(bankAcctNumber.indexOf("E")),"");
	  		 	    		}
	  		 	    		if(bankAcctNumber.contains(".")){
	  		 	    			bankAcctNumber = (bankAcctNumber.replace(".", ""));
	  		 	    		}
	  		 	    		tempMap.put("bankAcctNumber",bankAcctNumber);
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[13])){
	  		 	    		tempMap.put("presidentName",((String)rowObjects[13]).trim().replace("<", "(").replace(">", ")"));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[14])){
	  		 	    		tempMap.put("district",((String)rowObjects[14]).trim());
	  		 	    	}
	  		 	    	if(UtilValidate.isEmpty(tempMap.get("presidentName"))){
	  		 	    		tempMap.put("presidentName",tempMap.get("unitName"));
	  		 	    	}
	  		 	    	
	  		 	    	//creating Proc Facility
	  			        Map createFacilityInMap = FastMap.newInstance();
	  			        createFacilityInMap.put("facilityCode",tempMap.get("unitCode"));
	  			        createFacilityInMap.put("facilityName",tempMap.get("unitName"));
	  			        createFacilityInMap.put("userLogin",userLogin);
	  			        createFacilityInMap.put("facilityTypeId","UNIT");
	  			        createFacilityInMap.put("parentFacilityId",parentFacilityId);
	  			        createFacilityInMap.put("firstName",tempMap.get("presidentName"));
	  			        createFacilityInMap.put("facilitySize",tempMap.get("facilitySize"));
	  			        createFacilityInMap.put("finAccountCode",tempMap.get("bankAcctNumber"));
	  			        createFacilityInMap.put("finAccountTypeId","BANK_ACCOUNT");
	  			        createFacilityInMap.put("opCost",tempMap.get("opCost"));
	  			        createFacilityInMap.put("eOpCost",tempMap.get("eOpCost"));
	  			        createFacilityInMap.put("openedDate",openedDate);
	  			        createFacilityInMap.put("gbCode",tempMap.get("gbCode"));
	  			        createFacilityInMap.put("bCode",tempMap.get("bCode"));
	  			        createFacilityInMap.put("district",tempMap.get("district"));
	  			        createFacilityInMap.put("schemeTypeId",tempMap.get("schemeTypeId"));
	  			        createFacilityInMap.put("managedBy",tempMap.get("managedBy"));
	  			        Map facilityMap = createProcFacility(ctx, createFacilityInMap);
	  			        if(ServiceUtil.isError(facilityMap)){
	  			        	ignoredDetailsMap.put("reason",ServiceUtil.getErrorMessage(facilityMap));
	  			        	ignoredDetailsList.add(ignoredDetailsMap);
	  			        	Debug.logError("Error msg ==========="+facilityMap,module);
	  			        	continue;
	  			        }
	  	 	    	}
	  	 	    }
	  	 	     fileInputStream.close();
	  	 	 }catch (DBFException e) {
	  	 		Debug.logError("Error while getting data from given file=======>"+e,module);
	  			/* resultMap = ServiceUtil.returnError("Error while getting data from given file=======>"+e.getMessage());
	  			 return resultMap;*/
	  		}catch (IOException ioe_ex) {
	  			// TODO: handle exception
	  	 		 Debug.logInfo("error while getting fields from the file"+ioe_ex, module);			
	  	 		 resultMap = ServiceUtil.returnError("error while getting fields from the file"+ioe_ex.getMessage());
	  	 	 }catch (Exception e) {
	  			// TODO: handle exception
	  			Debug.logError("Error while Storing Units===>"+e.getMessage(),module);
	  			 resultMap = ServiceUtil.returnError("Error while Storing Units===>"+e.getMessage());
	  			 return resultMap;
	  		}
	  		resultMap = ServiceUtil.returnSuccess("Units Created Successfully. ");
	  		if(UtilValidate.isNotEmpty(ignoredDetailsList)){
	  			resultMap = ServiceUtil.returnSuccess("Units Created Successfully. The following List is ignored due to some fields are missed :"+ignoredDetailsList+" Number of Records ignored :"+ignoredDetailsList.size());
	  		}
	  		return resultMap;
	  	 }// end of the service
	  	/**
	  	 * Service for updating the bank details for all fin_accounts 
	  	 * @param ctx
	  	 * @param context
	  	 * @return
	  	 */
	  	 
	  	 public static Map<String, Object> bankMasToVbiz(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		 Map resultMap = ServiceUtil.returnSuccess();
	  		 Delegator delegator = ctx.getDelegator();
	  	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	  	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	 	 String file_name = (String) context.get("file_name");
	  	 	 String shedId = (String) context.get("shedId");
	  	 	 //here we have to get all the finAccountIds for the given Shed
	  	 	 List finAccIds = FastList.newInstance();
	  	 	 List ignoredDetailsList = FastList.newInstance();
	  	 	 try{
	  	 		 GenericValue shedDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",shedId), false);
	  	 		 Map finAccInMap = FastMap.newInstance();
	  	 		 finAccInMap.put("userLogin", userLogin);
	  	 		 finAccInMap.put("facilityId", shedId);
	  	 		 Map finAccMap = dispatcher.runSync("getShedFacilityFinAccount",finAccInMap);
	  	 		 if(ServiceUtil.isError(finAccMap)){
	  	 			 String errorMessage = ServiceUtil.getErrorMessage(finAccMap);
	  	 			 Debug.logError("Error while getting FinAccount IDs ======>"+errorMessage, module);
	  	 			 resultMap = ServiceUtil.returnError(errorMessage);
	  	 			 return resultMap;
	  	 		 }
	  	 		 List finAccountsList = (List)finAccMap.get("finAccountList");
	  	 		 if(UtilValidate.isEmpty(finAccountsList)){
	  	 			 Debug.logError("Financial accounts not found for the shed :"+shedDetails.get("facilityName"), module);
	  	 			 resultMap = ServiceUtil.returnError("Financial accounts not found for the shed :"+shedDetails.get("facilityName"));
	  	 			 return resultMap;
	  	 		 }
	  	 		 for(int i=0;i<finAccountsList.size();i++){
	  	 			 finAccIds.add((String)(((Map)finAccountsList.get(i)).get("finAccountId")));
	  	 		 }
	  	 		 InputStream fileInputStream = new FileInputStream(file_name);
	  	 		 DBFReader reader = new DBFReader( fileInputStream); 
	  	 		 int numberOfFields = reader.getFieldCount();
	  	 		 //To know the field Indexes
	  	 		 /*for(int i=0;i<numberOfFields;i++){
	  	 			 Debug.log("field :"+i+"  ========>"+reader.getField(i).getName());
	  	 		 }*/
	  	 		 Object []rowObjects;
	  	 	    while( (rowObjects = reader.nextRecord()) != null) {
	  	 	    	Map ignoredDetailsMap = FastMap.newInstance();
	  	 	    	Map tempMap = FastMap.newInstance();
	  	 	    	if(UtilValidate.isNotEmpty(rowObjects)){
	  	 	    		tempMap.put("ifscCode", "");
	  	 	    		tempMap.put("gbCode", "");
	  	 	    		tempMap.put("bCode", "");
	  	 	    		tempMap.put("finAccountName", "");
	  	 	    		tempMap.put("finAccountBranch", "");
	  	 	    		tempMap.put("bPlace","");
	  	 	    		
	  		 	    	// for date field 5
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[0])){
	  		 	    		tempMap.put("gbCode", Double.toString((Double)rowObjects[0]).replace(".0", ""));
	  		 	    	 }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[1])){
	  		 	    		tempMap.put("bCode", Double.toString((Double)rowObjects[1]).replace(".0", ""));
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[2])){
	  		 	    		tempMap.put("finAccountName",((String)rowObjects[2]).trim());
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[3])){
	  		 	    		tempMap.put("finAccountBranch",((String)rowObjects[3]).trim());
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[4])){
	  		 	    		tempMap.put("bPlace", ((String)rowObjects[4]).trim());
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[5])){
	  		 	    		tempMap.put("ifscCode", ((String)rowObjects[5]).trim());
	  		 	    	}
	  		 	    	
	  		 	    	if(UtilValidate.isEmpty(tempMap.get("gbCode"))||UtilValidate.isEmpty(tempMap.get("bCode"))){
	  		 	    		ignoredDetailsMap.put("gbCode", tempMap.get("gbCode"));
	  		 	    		ignoredDetailsMap.put("bCode", tempMap.get("gbCode"));
	  		 	    		ignoredDetailsMap.put("branch", tempMap.get("finAccountName"));
	  		 	    		ignoredDetailsMap.put("bPlace", tempMap.get("bPlace"));
	  		 	    		ignoredDetailsMap.put("ifscCode",tempMap.get("ifscCode"));
	  		 	    		ignoredDetailsList.add(ignoredDetailsMap);
	  		 	    		continue;
	  		 	    	}
	  		 	    	List conditionList = FastList.newInstance();
	  		 	    	conditionList.add(EntityCondition.makeCondition("finAccountId",EntityOperator.IN,finAccIds));
	  		 	    	conditionList.add(EntityCondition.makeCondition("bCode",EntityOperator.EQUALS,tempMap.get("bCode")));
	  		 	    	conditionList.add(EntityCondition.makeCondition("gbCode",EntityOperator.EQUALS,tempMap.get("gbCode")));
	  		 	    	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	  		 	    	List<GenericValue> finAccList = delegator.findList("FinAccount",condition,null, null,null, false);
	  		 	    	List<GenericValue> storeFinAccList = FastList.newInstance();
	  		 	    	for(GenericValue finAcc : finAccList){
	  		 	    		finAcc.set("finAccountName", tempMap.get("finAccountName"));
	  		 	    		finAcc.set("bPlace", tempMap.get("bPlace"));
	  		 	    		finAcc.set("finAccountBranch", tempMap.get("finAccountBranch"));
	  		 	    		finAcc.set("ifscCode", tempMap.get("ifscCode"));
	  		 	    		storeFinAccList.add(finAcc);
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(storeFinAccList)){
	  		 	    		delegator.storeAll(storeFinAccList);
	  		 	    	}
	  	 	    	}
	  	 	    }
	  	 	   	fileInputStream.close();
	  	 	 }catch (DBFException e) {
	  	 		Debug.logError("Error while getting data from given file=======>"+e,module);
	  			/* resultMap = ServiceUtil.returnError("Error while getting data from given file=======>"+e.getMessage());
	  			 return resultMap;*/
	  		}catch (IOException ioe_ex) {
	  			// TODO: handle exception
	  	 		 Debug.logInfo("error while getting fields from the file"+ioe_ex, module);			
	  	 		 resultMap = ServiceUtil.returnError("error while getting fields from the file"+ioe_ex.getMessage());
	  	 	 }catch (GenericEntityException e) {
	  			// TODO: handle exception
	  		 	Debug.logError("Error while updating Bank Details=======>"+e,module);
	  		 	resultMap = ServiceUtil.returnError("Error while updating Bank Details=======>"+e.getMessage());
	  		 	return resultMap;
	  	 	 }
	  	 	 catch (Exception e) {
	  	 		 // TODO: handle exception
	  	 		 Debug.logError("Unknown Error while updating Bank Details===>"+e.getMessage(),module);
	  			 resultMap = ServiceUtil.returnError("Unknown Error while updating Bank Details===>"+e.getMessage());
	  			 return resultMap;
	  	 	 }	
	  	 	resultMap = ServiceUtil.returnSuccess("Bank Details updated successfully");
	  		return resultMap;
	  	 }// end of the service
	  	 
	  	 
  	 	/**
	  	 * Service for updating the bank details for all fin_accounts 
	  	 * @param ctx
	  	 * @param context
	  	 * @return
	  	 */
	  	 
	  	 public static Map<String, Object> gbMasToVbiz(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		 Map resultMap = ServiceUtil.returnSuccess();
	  		 Delegator delegator = ctx.getDelegator();
	  	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	  	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	 	 String file_name = (String) context.get("file_name");
	  	 	 String shedId = (String) context.get("shedId");
	  	 	 //here we have to get all the finAccountIds for the given Shed
	  	 	 List finAccIds = FastList.newInstance();
	  	 	 List ignoredDetailsList = FastList.newInstance();
	  	 	 try{
	  	 		 GenericValue shedDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",shedId), false);
	  	 		 Map finAccInMap = FastMap.newInstance();
	  	 		 finAccInMap.put("userLogin", userLogin);
	  	 		 finAccInMap.put("facilityId", shedId);
	  	 		 Map finAccMap = dispatcher.runSync("getShedFacilityFinAccount",finAccInMap);
	  	 		 if(ServiceUtil.isError(finAccMap)){
	  	 			 String errorMessage = ServiceUtil.getErrorMessage(finAccMap);
	  	 			 Debug.logError("Error while getting FinAccount IDs ======>"+errorMessage, module);
	  	 			 resultMap = ServiceUtil.returnError(errorMessage);
	  	 			 return resultMap;
	  	 		 }
	  	 		 List finAccountsList = (List)finAccMap.get("finAccountList");
	  	 		 if(UtilValidate.isEmpty(finAccountsList)){
	  	 			 Debug.logError("Financial accounts not found for the shed :"+shedDetails.get("facilityName"), module);
	  	 			 resultMap = ServiceUtil.returnError("Financial accounts not found for the shed :"+shedDetails.get("facilityName"));
	  	 			 return resultMap;
	  	 		 }
	  	 		 for(int i=0;i<finAccountsList.size();i++){
	  	 			 finAccIds.add((String)(((Map)finAccountsList.get(i)).get("finAccountId")));
	  	 		 }
	  	 		 InputStream fileInputStream = new FileInputStream(file_name);
	  	 		 DBFReader reader = new DBFReader( fileInputStream); 
	  	 		 int numberOfFields = reader.getFieldCount();
	  	 		 //To know the field Indexes
	  	 		 /*for(int i=0;i<numberOfFields;i++){
	  	 			 Debug.log("field :"+i+"  ========>"+reader.getField(i).getName());
	  	 		 }*/
	  	 		 Object []rowObjects;
	  	 	    while( (rowObjects = reader.nextRecord()) != null) {
	  	 	    	Map ignoredDetailsMap = FastMap.newInstance();
	  	 	    	Map tempMap = FastMap.newInstance();
	  	 	    	if(UtilValidate.isNotEmpty(rowObjects)){
	  	 	    		tempMap.put("gbCode", "");
	  	 	    		tempMap.put("finAccountName", "");
	  	 	    		tempMap.put("shortName","");
	  	 	    		
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[0])){
	  		 	    		tempMap.put("gbCode", Double.toString((Double)rowObjects[0]).replace(".0", ""));
	  		 	    	 }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[1])){
	  		 	    		tempMap.put("finAccountName",((String)rowObjects[1]).trim());
	  		 	    	}
	  		 	    	// here we are checking this because of some sheds having 2 fields only
	  		 	    	if(numberOfFields>2){
		  		 	    	if(UtilValidate.isNotEmpty(rowObjects[2])){
		  		 	    		tempMap.put("shortName",((String)rowObjects[2]).trim());
		  		 	    	}
	  		 	    	}	
	  		 	    	if(UtilValidate.isEmpty(tempMap.get("gbCode"))||UtilValidate.isEmpty(tempMap.get("shortName"))){
	  		 	    		ignoredDetailsMap.put("gbCode", tempMap.get("gbCode"));
	  		 	    		ignoredDetailsMap.put("GBNAME", tempMap.get("finAccountName"));
	  		 	    		ignoredDetailsMap.put("SHNAM",tempMap.get("shortName"));
	  		 	    		ignoredDetailsList.add(ignoredDetailsMap);
	  		 	    		continue;
	  		 	    	}
	  		 	    	
	  		 	    	List conditionList = FastList.newInstance();
	  		 	    	conditionList.add(EntityCondition.makeCondition("finAccountId",EntityOperator.IN,finAccIds));
	  		 	    	conditionList.add(EntityCondition.makeCondition("gbCode",EntityOperator.EQUALS,tempMap.get("gbCode")));
	  		 	    	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	  		 	    	List<GenericValue> finAccList = delegator.findList("FinAccount",condition,null, null,null, false);
	  		 	    	List<GenericValue> storeFinAccList = FastList.newInstance();
	  		 	    	
	  		 	    	for(GenericValue finAcc : finAccList){
	  		 	    		finAcc.set("finAccountName", tempMap.get("finAccountName"));
	  		 	    		finAcc.set("shortName", tempMap.get("shortName"));
	  		 	    		storeFinAccList.add(finAcc);
	  		 	    	}
	  		 	    	if(UtilValidate.isNotEmpty(storeFinAccList)){
	  		 	    		delegator.storeAll(storeFinAccList);
	  		 	    	}
	  	 	    	}
	  	 	    }
	  	 	   	fileInputStream.close();
	  	 	 }catch (DBFException e) {
	  	 		Debug.logError("Error while getting data from given file=======>"+e,module);
	  			/* resultMap = ServiceUtil.returnError("Error while getting data from given file=======>"+e.getMessage());
	  			 return resultMap;*/
	  		}catch (IOException ioe_ex) {
	  			// TODO: handle exception
	  	 		 Debug.logInfo("error while getting fields from the file"+ioe_ex, module);			
	  	 		 resultMap = ServiceUtil.returnError("error while getting fields from the file"+ioe_ex.getMessage());
	  	 	 }catch (GenericEntityException e) {
	  			// TODO: handle exception
	  		 	Debug.logError("Error while updating Bank Details=======>"+e,module);
	  		 	resultMap = ServiceUtil.returnError("Error while updating Bank Details=======>"+e.getMessage());
	  		 	return resultMap;
	  	 	 }
	  	 	 catch (Exception e) {
	  	 		 // TODO: handle exception
	  	 		 Debug.logError("Unknown Error while updating Bank Details===>"+e.getMessage(),module);
	  			 resultMap = ServiceUtil.returnError("Unknown Error while updating Bank Details===>"+e.getMessage());
	  			 return resultMap;
	  	 	 }	
	  	 	resultMap = ServiceUtil.returnSuccess("Bank Details updated successfully");
	  		return resultMap;
	  	 }// end of the service
	  
	  	 
	  	 public static Map<String, Object>  createProcurementFacilityTimePeriod(DispatchContext dctx, Map<String, ? extends Object> context){
	  	 	 Delegator delegator = dctx.getDelegator();
	  	     LocalDispatcher dispatcher = dctx.getDispatcher();	
	  	     Map<String, Object> result = ServiceUtil.returnSuccess("FacilityTimePeriod created succesfully.");
	  	     String shedId = (String) context.get("shedId");
	  	     String unitId = (String) context.get("unitId");
	  	     String customTimePeriodId = (String) context.get("customTimePeriodId");
	  	     GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	     List<String> facilityIdList = FastList.newInstance();
	  	     try{
		  	    GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", shedId), false);
		  	    GenericValue customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
		  	    if(UtilValidate.isEmpty(customTimePeriod)){
		  	    	 Debug.logError("CustomTimePeriod Not Found", module);
		  	    	 return ServiceUtil.returnError("CustomTimePeriod Not Found ");
		  	     }
		  	     java.sql.Date fromDate = (java.sql.Date)customTimePeriod.getDate("fromDate");
		  	     java.sql.Date thruDate = (java.sql.Date)customTimePeriod.getDate("thruDate");
		  	     
		  	     
			  	if(UtilValidate.isEmpty(unitId)){
			    	List<GenericValue>  unitsList = (List)ProcurementNetworkServices.getShedUnitsByShed(dctx ,UtilMisc.toMap("shedId", shedId)).get("unitsDetailList");
			    	Set facilityIdsSet = new HashSet(EntityUtil.getFieldListFromEntityList(unitsList, "facilityId", true));  
			    	facilityIdList = new ArrayList<String>(facilityIdsSet);
			     }else{
			    	 facilityIdList.add(unitId);
			     }
		  	     facilityIdList.add(shedId); 
			     for(String tempFacilityId: facilityIdList){
			    	 List facilityCustomTimePeriodList = FastList.newInstance();
			    		List conditionList = FastList.newInstance();
			    		conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,tempFacilityId));
			    		conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,fromDate));
			    		conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,thruDate));
			    		EntityCondition facilityTimePeriodCondition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
			    		facilityCustomTimePeriodList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod", facilityTimePeriodCondition, null, null, null, false);
			    		if(UtilValidate.isNotEmpty(facilityCustomTimePeriodList)&&(!tempFacilityId.equalsIgnoreCase(shedId))){
			    			GenericValue tempCustomTimePeriod = EntityUtil.getFirst(facilityCustomTimePeriodList);
			    			GenericValue facCustomTimePeriod = delegator.findOne("FacilityCustomTimePeriod",UtilMisc.toMap("facilityId",tempFacilityId,"customTimePeriodId",tempCustomTimePeriod.getString("customTimePeriodId")),false);
			    			facCustomTimePeriod.remove();
			    		}
			    	 	GenericValue facilityCustomTimePeriod = delegator.findOne("FacilityCustomTimePeriod",UtilMisc.toMap("facilityId",tempFacilityId,"customTimePeriodId",customTimePeriodId),false);
			    		if(UtilValidate.isNotEmpty(facilityCustomTimePeriod)){
			    			continue;	    			
			    		}
			    		GenericValue newFacilityCustomTimePeriod = delegator.makeValue("FacilityCustomTimePeriod");
		    			newFacilityCustomTimePeriod.put("facilityId", tempFacilityId);
		    			newFacilityCustomTimePeriod.put("customTimePeriodId", customTimePeriodId);
		    			newFacilityCustomTimePeriod.put("createdByUserLogin", userLogin.get("userLoginId"));
		    			newFacilityCustomTimePeriod.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		    			newFacilityCustomTimePeriod.put("createdDate", UtilDateTime.nowTimestamp());
		    			newFacilityCustomTimePeriod.put("lastModifiedDate", UtilDateTime.nowTimestamp());
  				        delegator.create(newFacilityCustomTimePeriod);
			  	 }
	  	      }catch (GenericEntityException e) {
		  			Debug.logError("Can't Update  Data!"+e.getMessage(), module);
		  		    return ServiceUtil.returnError("Can't Update  Data!"); 
		  	  } 
		      return result;
	  	 }   
       /**
        * Service for ProcurementEntryMigration
        */
	  	public static Map<String, Object> mProcToVbiz(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		 Map resultMap = ServiceUtil.returnSuccess();
	  		 Delegator delegator = ctx.getDelegator();
	  	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	  	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	 	 String file_name = (String) context.get("file_name");
	  	 	 String shedCode = (String) context.get("shedCode");
	  	 	 Map productMap = FastMap.newInstance();
	  	 	 List<GenericValue> productsList = ProcurementNetworkServices.getProcurementProducts(ctx, context);
	  	 	 for(GenericValue product : productsList){
	  	 		 productMap.put(product.get("brandName"),product.get("productId"));
	  	 	 }
	  	 	 List ignoredDetailsList = FastList.newInstance();
	  	 	 int recordsCreated = 0;
	  	 	 try{
	  	 		 InputStream fileInputStream = new FileInputStream(file_name);
	  	 		 DBFReader reader = new DBFReader( fileInputStream); 
	  	 		 int numberOfFields = reader.getFieldCount();
	  	 		 //to know the field Indexes
	  	 		 /*for(int i=0;i<numberOfFields;i++){
	  	 			 Debug.log("field :"+i+"  ========>"+reader.getField(i).getName());
	  	 		 }*/
	  	 		 Object []rowObjects;
	  	 		 /* here we know the field Indexes  */
	  	 	    
	  	 		while( (rowObjects = reader.nextRecord()) != null) {
	  	 	    	if(UtilValidate.isNotEmpty(rowObjects)){
	  	 	    		Map ignoredDetailsMap = FastMap.newInstance();
	  	 	    		Map tempMap = FastMap.newInstance();
	  	 	    		tempMap.put("unitCode","");
	  	 	    		tempMap.put("shedCode",shedCode);
	  	 	    		tempMap.put("centerCode","");
	  	 	    		tempMap.put("ptcMilkType","");
	  	 	    		tempMap.put("productId", "");
	  	 	    		tempMap.put("quantity", BigDecimal.ZERO);
	  	 	    		tempMap.put("fat", BigDecimal.ZERO);
	  	 	    		tempMap.put("snf", BigDecimal.ZERO);
	  	 	    		tempMap.put("sQuantity", BigDecimal.ZERO);
	  	 	    		tempMap.put("sFat", BigDecimal.ZERO);
	  	 	    		tempMap.put("cQuantity", BigDecimal.ZERO);
	  	 	    		tempMap.put("ptcQuantity", BigDecimal.ZERO);
	  	 	    		tempMap.put("userLogin", userLogin);
	  	 	    		
 			        	ignoredDetailsMap.put("uCode",rowObjects[0]);
 			        	ignoredDetailsMap.put("ddate",rowObjects[3]);
 			        	ignoredDetailsMap.put("TypMlk",rowObjects[6]);
 			        	ignoredDetailsMap.put("DayTyp",rowObjects[4]);
 			        	ignoredDetailsMap.put("cCode",rowObjects[5]);
 			        	ignoredDetailsMap.put("gQty",rowObjects[8]);
 			        	
	  	 	    		
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[0])){
	  	 	    			//unitCode
	  	 	    			tempMap.put("unitCode",Double.toString((Double)rowObjects[0]).replace(".0", ""));
	  	 	    		}else{
	  	 	    			ignoredDetailsList.add(ignoredDetailsMap);
	  	 	    			continue;
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[3])){
	  	 	    			//orderDate
	  	 	    			tempMap.put("orderDate", UtilDateTime.toTimestamp((java.util.Date)rowObjects[3]));
	  	 	    		}else{
	  	 	    			ignoredDetailsList.add(ignoredDetailsMap);
	  	 	    			continue;
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[4])){
	  	 	    			//DayType
	  	 	    			
	  	 	    			tempMap.put("purchaseTime", (String)rowObjects[4]);
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[5])){
	  	 	    			//CenterCode
	  	 	    			tempMap.put("centerCode",Double.toString((Double)rowObjects[5]).replace(".0", ""));
	  	 	    			
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[6])){
	  	 	    			// ProductId
	  	 	    			String productName = (String)rowObjects[6];
	  	 	    			if(UtilValidate.isNotEmpty(productMap.get(productName))){
	  	 	    				tempMap.put("productId", productMap.get(productName));
	  	 	    			}
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[8])){
	  	 	    			//quantity
	  	 	    			Double quantity = (Double)rowObjects[8];
	  	 	    			tempMap.put("quantity", new BigDecimal(quantity).setScale(1,BigDecimal.ROUND_HALF_UP));
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[9])){
	  	 	    			//fat
	  	 	    			Double fat = (Double)rowObjects[9];
	  	 	    			tempMap.put("fat",new BigDecimal(fat).setScale(1,BigDecimal.ROUND_HALF_UP));
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[10])){
	  	 	    			//snf
	  	 	    			Double snf = (Double)rowObjects[10];
	  	 	    			tempMap.put("snf",new BigDecimal(snf).setScale(2,BigDecimal.ROUND_HALF_UP));
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[12])){
	  	 	    			//sQuantity
	  	 	    			Double sQuantity = (Double)rowObjects[12];
	  	 	    			tempMap.put("sQuantity", new BigDecimal(sQuantity).setScale(1,BigDecimal.ROUND_HALF_UP));
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[13])){
	  	 	    			//fat
	  	 	    			Double sFat = (Double)rowObjects[13];
	  	 	    			tempMap.put("sFat",new BigDecimal(sFat).setScale(1,BigDecimal.ROUND_HALF_UP));
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[14])){
	  	 	    			//cQuantity
	  	 	    			tempMap.put("cQuantity", new BigDecimal((Double)rowObjects[14]).setScale(1,BigDecimal.ROUND_HALF_UP));
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[15])){
	  	 	    			//ptcQuantity
	  	 	    			tempMap.put("ptcQuantity", new BigDecimal((Double)rowObjects[15]).setScale(1,BigDecimal.ROUND_HALF_UP));
	  	 	    		}
	  	 	    		if(UtilValidate.isNotEmpty(rowObjects[16])){
	  	 	    			//PtcType
	  	 	    			tempMap.put("ptcMilkType", (String)rowObjects[16]);
	  	 	    		}
	  	 	    		if(UtilValidate.isEmpty(tempMap.get("quantity"))||UtilValidate.isEmpty(tempMap.get("orderDate"))||UtilValidate.isEmpty(tempMap.get("productId"))||UtilValidate.isEmpty(tempMap.get("centerCode"))||UtilValidate.isEmpty(tempMap.get("unitCode"))||UtilValidate.isEmpty(tempMap.get("purchaseTime"))){
	  	 	    			ignoredDetailsList.add(ignoredDetailsMap);
	  	 	    			continue;
	  	 	    		}
	  	 	    		Map createProcEntryMap = FastMap.newInstance();
	  	 	    		createProcEntryMap.putAll(tempMap);
	  	 	    		Map facilityMap = dispatcher.runSync("createProcurementEntry",createProcEntryMap);
	  			        if(ServiceUtil.isError(facilityMap)){
	  			        	ignoredDetailsMap.put("reason",ServiceUtil.getErrorMessage(facilityMap));
	  			        	ignoredDetailsList.add(ignoredDetailsMap);
	  			        	Debug.logError("Error msg ==========="+facilityMap,module);
	  			        	continue;
	  			        }
	  			      recordsCreated++; 
	  	 	    	}
	  	 	    }
	  	 	     fileInputStream.close();
	  	 	 }catch (DBFException e) {
	  	 		Debug.logError("Error while getting data from given file=======>"+e,module);
	  			/* resultMap = ServiceUtil.returnError("Error while getting data from given file=======>"+e.getMessage());
	  			 return resultMap;*/
	  		}catch (IOException ioe_ex) {
	  			// TODO: handle exception
	  	 		 Debug.logInfo("error while getting fields from the file"+ioe_ex, module);			
	  	 		 resultMap = ServiceUtil.returnError("error while getting fields from the file"+ioe_ex.getMessage());
	  	 	 }/*catch (GenericServiceException e) {
	  			// TODO: handle exception
	  	 		 Debug.logError("Error while creating units for the given shed=======>"+e,module);
	  			 resultMap = ServiceUtil.returnError("Error while creating units for the given shed=======>"+e.getMessage());
	  			 return resultMap;
	  		}*/catch (Exception e) {
	  			// TODO: handle exception
	  			Debug.logError("Error while Creating ProcurementEntries===>"+e.getMessage(),module);
	  			 resultMap = ServiceUtil.returnError("Error while Creating ProcurementEntries===>"+e.getMessage());
	  			 return resultMap;
	  		}
	  		resultMap = ServiceUtil.returnSuccess(recordsCreated +" ProcurementEntries Created Successfully. ");
	  		if(UtilValidate.isNotEmpty(ignoredDetailsList)){
	  			resultMap = ServiceUtil.returnSuccess(recordsCreated +" ProcurementEntries  Created Successfully. The following List is ignored due to some fields are missed :"+ignoredDetailsList+" Number of Records ignored :"+ignoredDetailsList.size());
	  		}
	  		return resultMap;
	  	 }// end of the service 
	  	
	  	
	  	/**
        * Service for Billing Adjustments(Additions/Deductions)  Migration
        */
	  	public static Map<String, Object> orderAdjustmentsFromDbfToVbiz(DispatchContext ctx, Map<String, ? extends Object> context) {
	  		 Map resultMap = ServiceUtil.returnSuccess();
	  		 Delegator delegator = ctx.getDelegator();
	  	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	  	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	 	 String file_name = (String) context.get("file_name");
	  	 	 String shedCode = (String) context.get("shedCode");
	  	 	 List ignoredDetailsList = FastList.newInstance();
	  	 	int recordsCreated = 0;
	  	 	 try{
	  	 		 InputStream fileInputStream = new FileInputStream(file_name);
	  	 		 DBFReader reader = new DBFReader( fileInputStream); 
	  	 		 int numberOfFields = reader.getFieldCount();
	  	 		 Map headerMap = FastMap.newInstance();
	  	 		 //to know the field Indexes
	  	 		 for(int i=0;i<numberOfFields;i++){
	  	 			 headerMap.put(i, reader.getField(i).getName());
	  	 			 //Debug.log("field :"+i+"  ========>"+reader.getField(i).getName());
	  	 		 }
	  	 		 Object []rowObjects;
	  	 		 /* here we know the field Indexes  */
	  	 	    
	  	 		while( (rowObjects = reader.nextRecord()) != null) {
	  	 	    	if(UtilValidate.isNotEmpty(rowObjects)){
	  	 	    		Map ignoredDetailsMap = FastMap.newInstance();
	  	 	    		Map tempMap = FastMap.newInstance();
	  	 	    		tempMap.put("unitCode","");
	  	 	    		tempMap.put("shedCode",shedCode);
	  	 	    		tempMap.put("centerCode","");
	  	 	    		tempMap.put("amount", BigDecimal.ZERO);
	  	 	    		tempMap.put("userLogin", userLogin);
	  	 	    		
 			        	ignoredDetailsMap.put("ddate",rowObjects[0]);
 			        	ignoredDetailsMap.put("ucode",rowObjects[1]);
 			        	ignoredDetailsMap.put("ccode",rowObjects[3]);
 			        	if(UtilValidate.isNotEmpty(rowObjects[0])){
	  	 	    			//orderDate
	  	 	    			tempMap.put("periodDate", UtilDateTime.toTimestamp((java.util.Date)rowObjects[0]));
	  	 	    		}
 			        	if(UtilValidate.isNotEmpty(rowObjects[1])){
	  		 	    		tempMap.put("unitCode", Double.toString((Double)rowObjects[1]).replace(".0", ""));
	  		 	    	 }
	  		 	    	if(UtilValidate.isNotEmpty(rowObjects[3])){
	  		 	    		tempMap.put("centerCode", Double.toString((Double)rowObjects[3]).replace(".0",""));
	  		 	    	}
	  		 	    	if(UtilValidate.isEmpty(tempMap.get("periodDate"))||UtilValidate.isEmpty(tempMap.get("unitCode"))||UtilValidate.isEmpty(tempMap.get("centerCode"))){
	  	 	    			ignoredDetailsList.add(ignoredDetailsMap);
	  	 	    			continue;
	  	 	    		}
	  		 	    	for(int i=4;i<numberOfFields;i++){
	  		 	    		String header =null;
	  		 	    		header = (String)headerMap.get(i);
	  		 	    		Double amount = 0.0;
	  		 	    		String adjustmentTypeId = null;
	  		 	    		if(UtilValidate.isNotEmpty(rowObjects[i])){
	  		 	    			tempMap.put("adjustmentTypeId","" );
	  		 	    			amount = (Double)rowObjects[i];
	  		 	    			tempMap.put("amount",new BigDecimal(amount).setScale(2,BigDecimal.ROUND_HALF_UP));
	  		 	    			if(header.equalsIgnoreCase("FEED")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_FEEDDED" );
		  	 	    			}else if(header.equalsIgnoreCase("SEED")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_SEEDDED" );
		  	 	    			}else if(header.equalsIgnoreCase("TSTORE")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_STORET" );
		  	 	    			}else if(header.equalsIgnoreCase("ASTORE")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_STOREA" );
		  	 	    			}else if(header.equalsIgnoreCase("VIJAYARD")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_VIJAYARD" );
		  	 	    			}else if(header.equalsIgnoreCase("VACCINE")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_VACCINE" );
		  	 	    			}else if(header.equalsIgnoreCase("VIJAYALN")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_VIJAYALN" );
		  	 	    			}else if(header.equalsIgnoreCase("OTHERS")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_OTHERDED" );
		  	 	    			}else if(header.equalsIgnoreCase("TESTER")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_MTESTER" );
		  	 	    			}else if(header.equalsIgnoreCase("SPARES")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_MSPARES" );
		  	 	    			}else if(header.equalsIgnoreCase("COSALE")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_CESSONSALE" );
		  	 	    			}else if(header.equalsIgnoreCase("STANRY")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_STATONRY" );
		  	 	    			}else if(header.equalsIgnoreCase("AIWR")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_AIWR" );
		  	 	    			}else if(header.equalsIgnoreCase("INTR")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_INTEREST" );
		  	 	    			}else if(header.equalsIgnoreCase("OTHR")){
		  	 	    				tempMap.put("adjustmentTypeId","MILKPROC_OTHER_ADDNs" );
		  	 	    			}
		  	 	    			adjustmentTypeId =(String) tempMap.get("adjustmentTypeId");
		  	 	    			if((UtilValidate.isNotEmpty(adjustmentTypeId))&& (amount!=0)){
		  	 	    				Map createBillingAdjustmentMap = FastMap.newInstance();
			  	 	    			createBillingAdjustmentMap.putAll(tempMap);
				  	 	    		Map facilityMap = dispatcher.runSync("createBillingAdjustment",createBillingAdjustmentMap);
				  			        if(ServiceUtil.isError(facilityMap)){
				  			        	ignoredDetailsMap.put("reason",ServiceUtil.getErrorMessage(facilityMap));
				  			        	ignoredDetailsList.add(ignoredDetailsMap);
				  			        	Debug.logError("Error msg ==========="+facilityMap,module);
				  			        	continue;
				  			        }
		  	 	    			}    
		  	 	    		}
	  		 	    	}
	  			      recordsCreated++; 
	  	 	    	}
	  	 	    }
	  	 	     fileInputStream.close();
	  	 	 }catch (DBFException e) {
	  	 		Debug.logError("Error while getting data from given file=======>"+e,module);
	  			/* resultMap = ServiceUtil.returnError("Error while getting data from given file=======>"+e.getMessage());
	  			 return resultMap;*/
	  		}catch (IOException ioe_ex) {
	  			// TODO: handle exception
	  	 		 Debug.logInfo("error while getting fields from the file"+ioe_ex, module);			
	  	 		 resultMap = ServiceUtil.returnError("error while getting fields from the file"+ioe_ex.getMessage());
	  	 	 }/*catch (GenericServiceException e) {
	  			// TODO: handle exception
	  	 		 Debug.logError("Error while creating units for the given shed=======>"+e,module);
	  			 resultMap = ServiceUtil.returnError("Error while creating units for the given shed=======>"+e.getMessage());
	  			 return resultMap;
	  		}*/catch (Exception e) {
	  			// TODO: handle exception
	  			Debug.logError("Error while Creating Billing Adjustments===>"+e.getMessage(),module);
	  			 resultMap = ServiceUtil.returnError("Error while Creating Billing Adjustments===>"+e.getMessage());
	  			 return resultMap;
	  		}
	  		resultMap = ServiceUtil.returnSuccess(recordsCreated +"  Billing Adjustments Created Successfully. ");
	  		if(UtilValidate.isNotEmpty(ignoredDetailsList)){
	  			resultMap = ServiceUtil.returnSuccess(recordsCreated +"  Billing Adjustments Created Successfully. The following List is ignored due to some fields are missed :"+ignoredDetailsList+" Number of Records ignored :"+ignoredDetailsList.size());
	  		}
	  		return resultMap;
	  	 }// end of the service
	  	
	  	
	  	public static Map<String, Object> createShedMaintenanceAmtInvoice(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        String shedId = (String) context.get("shedId");	       
	        BigDecimal amount = (BigDecimal)context.get("amount");
	        String periodBillingId = null;
	        GenericValue facilityPartyDetails=null;
	        GenericValue customTimePeriod = null;
	       try{
	    	   customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId",customTimePeriodId),false);	 
			   if(UtilValidate.isEmpty(customTimePeriod)){
				  Debug.logError( "There no active billing time periods. ", module);				 
				  return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
			    } 
	    	  List conditionList=FastList.newInstance();
		       conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shedId));
		       conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "DD_ROLE"));
			   EntityCondition orderCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   List facilityPartyList = delegator.findList("FacilityParty", orderCondition, null, null, null,false);
			   if(UtilValidate.isEmpty(facilityPartyList)){
				  Debug.logError( "DD account configuration missing for== "+shedId, module);				 
				  return ServiceUtil.returnError("DD account configuration missing for=="+shedId);
			    }
			   Timestamp fromDate=UtilDateTime.nowTimestamp();
			   facilityPartyList =  EntityUtil.filterByDate(facilityPartyList,fromDate);
			   facilityPartyDetails = EntityUtil.getFirst(facilityPartyList);	
			  
	       }catch(GenericEntityException ge){
	    	// TODO: handle exception
	     		 Debug.logError(ge.getMessage(), module);
	     		 return ServiceUtil.returnError(ge.getMessage());
	       }	
	       String partyId = facilityPartyDetails.getString("partyId");
	       
	        Map resultMap = ServiceUtil.returnSuccess();
	        String invoiceId = "";
	    	 try{
		    	  
			    List conList=FastList.newInstance();
			    	conList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shedId));
			    	conList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SHEDMAINT_OUT"));
			    	conList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "SHEDMAINT_ITEM"));
			    	conList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN, UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")))));
			    	conList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")))));
			    	conList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_APPROVED"));
			    	EntityCondition condition = EntityCondition.makeCondition(conList, EntityOperator.AND);
				   List invoiceAndInvoiceItemsList = delegator.findList("InvoiceItemInvoiceItemTypeInvoice", condition, null, null, null,false);
				   if(UtilValidate.isNotEmpty(invoiceAndInvoiceItemsList)){
					  Debug.logError( "Shed Maintenance amount Already exists this Period"+customTimePeriodId, module);				 
					  return ServiceUtil.returnError("Shed Maintenance amount Already exists");
				    }
	    		Map<String, Object> createInvoiceContext = FastMap.newInstance();
	    	    createInvoiceContext.put("partyId", "Company");
	    	    createInvoiceContext.put("partyIdFrom",partyId );
	    	    createInvoiceContext.put("invoiceDate", UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
	    	    createInvoiceContext.put("dueDate", UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
	    	    createInvoiceContext.put("invoiceTypeId", "SHEDMAINT_OUT");
	    	    createInvoiceContext.put("facilityId", shedId);
	    	    createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
	    	    createInvoiceContext.put("userLogin", userLogin);
	    	    Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
	    	    invoiceId = (String) createInvoiceResult.get("invoiceId");
                if (ServiceUtil.isError(createInvoiceResult)) {
                    return ServiceUtil.returnError("There was an error while creating Invoice" + ServiceUtil.getErrorMessage(createInvoiceResult));
                }
              //create invoiceItem here 
                Map inputItem = UtilMisc.toMap("userLogin", userLogin,"invoiceId", invoiceId);
                inputItem.put("invoiceItemTypeId","SHEDMAINT_ITEM"); 
                inputItem.put("amount", amount);
                Map<String, Object> serviceResults;
                serviceResults = dispatcher.runSync("createInvoiceItem", inputItem);
                if (ServiceUtil.isError(serviceResults)) {
       		          return ServiceUtil.returnError("Unable to create Invoice Item", null, null, serviceResults);
       		    }
                serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", "INVOICE_APPROVED", "userLogin", userLogin));
       			if (ServiceUtil.isError(serviceResults)) {
       		          return ServiceUtil.returnError("Unable to set Invoice Status", null, null, serviceResults);
       		     }	
       			
       			List periodBillingList = FastList.newInstance();
       			List condtList=FastList.newInstance();
    	        condtList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS")));
    	        condtList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
    	        condtList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PB_PROC_MRGN"));
    	        condtList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , shedId));
    	    	EntityCondition cond=EntityCondition.makeCondition(condtList,EntityOperator.AND);
    	    	
	    		periodBillingList = delegator.findList("PeriodBilling", cond, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(periodBillingList)){
	    			return ServiceUtil.returnError("Period Billing Already exists", null, null, serviceResults);   			
	    		}
	    		GenericValue newEntity = delegator.makeValue("PeriodBilling");
	            newEntity.set("billingTypeId", "PB_PROC_MRGN");
	            newEntity.set("customTimePeriodId", customTimePeriodId);
	            newEntity.set("statusId", "IN_PROCESS");
	            newEntity.set("facilityId", shedId);
	            newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
	            newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            newEntity.set("createdDate", UtilDateTime.nowTimestamp());
	            newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());  
				delegator.createSetNextSeqId(newEntity);
				periodBillingId = (String) newEntity.get("periodBillingId");
       			Timestamp fromDateTime= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
       			Timestamp thruDateTime= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
       			String productId="_NA_";
       			GenericValue facilityDetail= delegator.findOne("Facility", UtilMisc.toMap("facilityId", shedId), false);
       			GenericValue procurementAbstract = delegator.findOne("ProcurementAbstract", UtilMisc.toMap("periodBillingId", periodBillingId, "fromDate", fromDateTime,"thruDate", thruDateTime, "facilityId", shedId, "productId",productId), false);
       			if(UtilValidate.isNotEmpty(procurementAbstract)){
					Debug.logError("Already abstract populated for "+shedId,module);
					return ServiceUtil.returnError("Already abstract populated for "+shedId, null, null, serviceResults);
				}
				GenericValue procAbstract = delegator.makeValue("ProcurementAbstract");	
				procAbstract.set("facilityId",shedId);
				procAbstract.set("productId",productId);
				procAbstract.set("periodBillingId", periodBillingId);
				procAbstract.set("fromDate", fromDateTime);
				procAbstract.set("thruDate", thruDateTime);					
				procAbstract.set("facilityName",facilityDetail.get("facilityName"));
				procAbstract.set("facilityCode",facilityDetail.get("facilityCode"));
				procAbstract.set("grossAmt",amount);
				procAbstract.create();	
				GenericValue periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
					periodBilling.set("statusId", "GENERATED");
					periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
					periodBilling.store();
			  
	    	 }catch (GenericEntityException e) {
				// TODO: handle exception
	     		 Debug.logError(e.getMessage(), module);
	     		 return ServiceUtil.returnError(e.getMessage());
			}catch(GenericServiceException ge){
				 Debug.logError(ge.getMessage(), module);
	     		 return ServiceUtil.returnError(ge.getMessage());
			}
			 resultMap.put("invoiceId", invoiceId);
	        return resultMap;
		}
	  	
	  	public static Map<String, Object> cancelShedMaintenanceInvoicePeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
	        Delegator delegator = dctx.getDelegator();
		    LocalDispatcher dispatcher = dctx.getDispatcher();       
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
		    Map<String, Object> result = ServiceUtil.returnSuccess("Shed Billing Invoice Successfully Cancelled ");
		    String periodBillingId = (String) context.get("periodBillingId");
		    String shedId= (String) context.get("facilityId");
		    Timestamp fromDate= (Timestamp) context.get("fromDate");
		    Timestamp thruDate= (Timestamp) context.get("thruDate");
		    GenericValue periodBilling = null;
		    GenericValue customTimePeriod = null;
		    try{
	            periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
	            periodBilling.set("statusId", "CANCEL_INPROCESS");	
	            periodBilling.store(); 
	            List conList=FastList.newInstance();
		    	conList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shedId));
		    	conList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SHEDMAINT_OUT"));
		    	conList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "SHEDMAINT_ITEM"));
		    	conList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN, UtilDateTime.getDayStart(fromDate)));
		    	conList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO ,  UtilDateTime.getDayEnd(thruDate)));
		    	conList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_APPROVED"));
		    	EntityCondition condition = EntityCondition.makeCondition(conList, EntityOperator.AND);
		    	List<GenericValue>  invoiceAndInvoiceItemsList = delegator.findList("InvoiceItemInvoiceItemTypeInvoice", condition, null, null, null,false);
		    	if(UtilValidate.isEmpty(invoiceAndInvoiceItemsList)){
				  Debug.logError( "Shed Maintenance Invoice does not exists"+shedId, module);				 
				  return ServiceUtil.returnError("Shed does not exists");
			    }
			   List invoiceIdsList=EntityUtil.getFieldListFromEntityList(invoiceAndInvoiceItemsList, "invoiceId", true);
			   for(GenericValue invoiceDetails : invoiceAndInvoiceItemsList){
				   Map<String, Object> invoiceCtx = FastMap.newInstance();
		    		 invoiceCtx.put("invoiceId", invoiceDetails.getString("invoiceId"));
		    		 invoiceCtx.put("statusId", "INVOICE_CANCELLED");
		    		 invoiceCtx.put("userLogin",userLogin);
		    		 try{
		    			 Map<String, Object> setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", invoiceCtx);
		    			 if(ServiceUtil.isError(setInvoiceStatusResult)){
		    				 Debug.logError("Error while running setInvoiceStatus service ===>"+ServiceUtil.getErrorMessage(setInvoiceStatusResult),module);
			    			 return ServiceUtil.returnError("Error while cancelling the invoices");
		    			 }
		    		 }catch (GenericServiceException e) {
		    			 Debug.logError("Error while cancelling the invoices===>"+e.getMessage(),module);
		    			 return ServiceUtil.returnError("Error while cancelling the invoices");
					}
			   }
			   periodBilling.set("statusId", "COM_CANCELLED"); 
			   periodBilling.store();    		
	 	    	
		    }catch(GenericEntityException e){
	            Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
	            return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		    }
		   
		    return result;
		}
	  	
	  	/*public static Map<String, Object> sendDBFDataMail(DispatchContext dctx, Map<String, ? extends Object> rServiceContext) {
	        Map<String, Object> serviceContext = UtilMisc.makeMapWritable(rServiceContext);
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        String webSiteId = (String) serviceContext.remove("webSiteId");
	        String bodyText = (String) serviceContext.remove("bodyText");
	        String bodyScreenUri = (String) serviceContext.remove("bodyScreenUri");
	        //String xslfoAttachScreenLocationParam = (String) serviceContext.remove("xslfoAttachScreenLocation");
	        String xslfoAttachScreenLocationParam = "component://procurement/widget/ProcurementScreens.xml#ShedWiseAmountAbstract";
	        String attachmentNameParam = (String) serviceContext.remove("attachmentName");
	        List<String> xslfoAttachScreenLocationListParam = UtilGenerics.checkList(serviceContext.remove("xslfoAttachScreenLocationList"));
	        List<String> attachmentNameListParam = UtilGenerics.checkList(serviceContext.remove("attachmentNameList"));
	        
	        List<String> xslfoAttachScreenLocationList = FastList.newInstance();
	        List<String> attachmentNameList = FastList.newInstance();
	        if (UtilValidate.isNotEmpty(xslfoAttachScreenLocationParam)) xslfoAttachScreenLocationList.add(xslfoAttachScreenLocationParam);
	        if (UtilValidate.isNotEmpty(attachmentNameParam)) attachmentNameList.add(attachmentNameParam);
	        if (UtilValidate.isNotEmpty(xslfoAttachScreenLocationListParam)) xslfoAttachScreenLocationList.addAll(xslfoAttachScreenLocationListParam);
	        if (UtilValidate.isNotEmpty(attachmentNameListParam)) attachmentNameList.addAll(attachmentNameListParam);
	        
	        Locale locale = (Locale) serviceContext.get("locale");
	        Map<String, Object> bodyParameters = UtilGenerics.checkMap(serviceContext.remove("bodyParameters"));
	        if (bodyParameters == null) {
	            bodyParameters = MapStack.create();
	            bodyParameters.put("sendFrom", "nagababu@vasista.in");
	            bodyParameters.put("sendTo", "nagababu@vasista.in");
	        }
	        if (!bodyParameters.containsKey("locale")) {
	            bodyParameters.put("locale", locale);
	        } else {
	            locale = (Locale) bodyParameters.get("locale");
	        }
	        
	       
	        String contentType = (String) serviceContext.remove("contentType");

	        StringWriter bodyWriter = new StringWriter();

	        MapStack<String> screenContext = MapStack.create();
	        screenContext.put("locale", locale);
	        ScreenRenderer screens = new ScreenRenderer(bodyWriter, screenContext, htmlScreenRenderer);
	        screens.populateContextForService(dctx, bodyParameters);
	        screenContext.putAll(bodyParameters);

	        if (bodyScreenUri != null) {
	            try {
	                screens.render(bodyScreenUri);
	            } catch (GeneralException e) {
	                Debug.logError(e, "Error rendering screen for email: " + e.toString(), module);
	                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenEmailError", UtilMisc.toMap("errorString", e.toString()), locale));
	            } catch (IOException e) {
	                Debug.logError(e, "Error rendering screen for email: " + e.toString(), module);
	                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenEmailError", UtilMisc.toMap("errorString", e.toString()), locale));
	            } catch (SAXException e) {
	                Debug.logError(e, "Error rendering screen for email: " + e.toString(), module);
	                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenEmailError", UtilMisc.toMap("errorString", e.toString()), locale));
	            } catch (ParserConfigurationException e) {
	                Debug.logError(e, "Error rendering screen for email: " + e.toString(), module);
	                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenEmailError", UtilMisc.toMap("errorString", e.toString()), locale));
	            }
	        }

	        boolean isMultiPart = false;

	        // check if attachment screen location passed in
	        if (UtilValidate.isNotEmpty(xslfoAttachScreenLocationList)) {
	            List<Map<String, ? extends Object>> bodyParts = FastList.newInstance();
	            if (bodyText != null) {
	                bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
	                bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyText, "type", "text/html"));
	            } else {
	                bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyWriter.toString(), "type", "text/html"));
	            }
	            
	            for (int i = 0; i < xslfoAttachScreenLocationList.size(); i++) {
	                String xslfoAttachScreenLocation = xslfoAttachScreenLocationList.get(i);
	                String attachmentName = "Details.pdf";
	                if (UtilValidate.isNotEmpty(attachmentNameList) && attachmentNameList.size() >= i) {
	                    attachmentName = attachmentNameList.get(i);
	                }
	                isMultiPart = true;
	                // start processing fo pdf attachment
	                try {
	                    Writer writer = new StringWriter();
	                    MapStack<String> screenContextAtt = MapStack.create();
	                    // substitute the freemarker variables...
	                    ScreenRenderer screensAtt = new ScreenRenderer(writer, screenContext, foScreenRenderer);
	                    screensAtt.populateContextForService(dctx, bodyParameters);
	                    screenContextAtt.putAll(bodyParameters);
	                    screensAtt.render(xslfoAttachScreenLocation);

	                    
	                    try { // save generated fo file for debugging
	                        String buf = writer.toString();
	                        java.io.FileWriter fw = new java.io.FileWriter(new java.io.File("/tmp/file1.xml"));
	                        fw.write(buf.toString());
	                        fw.close();
	                    } catch (IOException e) {
	                        Debug.logError(e, "Couldn't save xsl-fo xml debug file: " + e.toString(), module);
	                    }
	                    

	                    // create the input stream for the generation
	                    StreamSource src = new StreamSource(new StringReader(writer.toString()));

	                    // create the output stream for the generation
	                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	                    Fop fop = ApacheFopWorker.createFopInstance(baos, MimeConstants.MIME_PDF);
	                    ApacheFopWorker.transform(src, null, fop);

	                    // and generate the PDF
	                    baos.flush();
	                    baos.close();

	                    // store in the list of maps for sendmail....
	                    bodyParts.add(UtilMisc.<String, Object>toMap("content", baos.toByteArray(), "type", "application/pdf", "filename", attachmentName));
	                } catch (GeneralException ge) {
	                    Debug.logError(ge, "Error rendering PDF attachment for email: " + ge.toString(), module);
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenPdfError", UtilMisc.toMap("errorString", ge.toString()), locale));
	                } catch (IOException ie) {
	                    Debug.logError(ie, "Error rendering PDF attachment for email: " + ie.toString(), module);
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenPdfError", UtilMisc.toMap("errorString", ie.toString()), locale));
	                } catch (FOPException fe) {
	                    Debug.logError(fe, "Error rendering PDF attachment for email: " + fe.toString(), module);
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenPdfError", UtilMisc.toMap("errorString", fe.toString()), locale));
	                } catch (SAXException se) {
	                    Debug.logError(se, "Error rendering PDF attachment for email: " + se.toString(), module);
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenPdfError", UtilMisc.toMap("errorString", se.toString()), locale));
	                } catch (ParserConfigurationException pe) {
	                    Debug.logError(pe, "Error rendering PDF attachment for email: " + pe.toString(), module);
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenPdfError", UtilMisc.toMap("errorString", pe.toString()), locale));
	                }
	                
	                serviceContext.put("bodyParts", bodyParts);
	            }
	        } else {
	            isMultiPart = false;
	            // store body and type for single part message in the context.
	            if (bodyText != null) {
	                bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
	                serviceContext.put("body", bodyText);
	            } else {
	                serviceContext.put("body", bodyWriter.toString());
	            }

	            // Only override the default contentType in case of plaintext, since other contentTypes may be multipart
	            //    and would require specific handling.
	            if (contentType != null && contentType.equalsIgnoreCase("text/plain")) {
	                serviceContext.put("contentType", "text/plain");
	            } else {
	                serviceContext.put("contentType", "text/html");
	            }
	        }

	        // also expand the subject at this point, just in case it has the FlexibleStringExpander syntax in it...
	        String subject = (String) serviceContext.remove("subject");
	        subject = FlexibleStringExpander.expandString(subject, screenContext, locale);
	        Debug.logInfo("Expanded email subject to: " + subject, module);
	        serviceContext.put("subject", subject);
	       
	        
	        if (Debug.verboseOn()) Debug.logVerbose("sendMailFromScreen sendMail context: " + serviceContext, module);

	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        Map<String, Object> sendMailResult;
	        try {
	            if (isMultiPart) {
	                sendMailResult = dispatcher.runSync("sendMailMultiPart", serviceContext);
	            } else {
	                sendMailResult = dispatcher.runSync("sendMail", serviceContext);
	            }
	        } catch (Exception e) {
	            Debug.logError(e, "Error send email:" + e.toString(), module);
	            return ServiceUtil.returnError(e.toString());
	        }
	        if (ServiceUtil.isError(sendMailResult)) {
	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(sendMailResult));
	        }

	        result.put("messageWrapper", sendMailResult.get("messageWrapper"));
	        result.put("body", bodyWriter.toString());
	        result.put("subject", subject);
	                   
	        return result;
	    }*/
	  	
}
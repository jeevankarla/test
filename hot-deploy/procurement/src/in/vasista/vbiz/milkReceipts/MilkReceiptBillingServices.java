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
package in.vasista.vbiz.milkReceipts;

import static org.hamcrest.Matchers.describedAs;

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
import java.util.TimeZone;

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
import org.ofbiz.product.product.ProductEvents;
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
 * MilkReciept Billing  Services
 */
public class MilkReceiptBillingServices {

	public static final String module = MilkReceiptBillingServices.class.getName();
	public static final String resource = "CommonUiLabels";
	protected static final HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected static final FoScreenRenderer foScreenRenderer = new FoScreenRenderer();
	
    public static Map<String, Object> populateMilkReceiptPeriodBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map resultMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	String customTimePeriodId = (String)context.get("customTimePeriodId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String partyId = (String)context.get("partyId");
    	GenericValue customTimePeriod = null;
    	try{
    		customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
        	if(UtilValidate.isEmpty(customTimePeriod)){
        		Debug.logError( "There no active billing time periods. ", module);				 
        		return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
        	}
    	}catch(GenericEntityException e){
    		Debug.logError("Error while getting customTimePeriod Details ::"+e,module);
    		return ServiceUtil.returnError("Error while getting customTimePeriod Details ::"+e.getMessage());
    	}
		
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
        Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
        Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime);
        Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime);
		List MilkTransfersList = FastList.newInstance();
		//Here we are getting shifts for MILK_SHIFT
		try{
			Map inMap = FastMap.newInstance();
			inMap.put("userLogin", userLogin);
			inMap.put("shiftType", "MILK_SHIFT");
			inMap.put("fromDate", monthBegin);
			inMap.put("thruDate", monthEnd);
			Map workShifts = getShiftDaysByType(dctx,inMap );
			if(ServiceUtil.isError(workShifts)){
				Debug.logError("Error while getting shift times :"+workShifts, module);
				return ServiceUtil.returnError("Error while getting shift times :"+ServiceUtil.getErrorMessage(workShifts));
			}
			fromDateTime =(Timestamp)workShifts.get("fromDate");
			thruDateTime =(Timestamp)workShifts.get("thruDate");
		}catch(Exception e){
			Debug.logError("Error while getting fromdate and thruDate ::"+e,module);
			return ServiceUtil.returnError("Error while getting fromdate and thruDate ::"+e.getMessage());
		}
		// here we are getting MilkReceipts 
		List milkTrnasferCondList = FastList.newInstance();
		milkTrnasferCondList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.EQUALS,"INTERNAL"));
		milkTrnasferCondList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,"MD"));
		milkTrnasferCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDateTime));
		milkTrnasferCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDateTime));
		//
		milkTrnasferCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_APPROVED"));
		if(UtilValidate.isNotEmpty(partyId)){
			milkTrnasferCondList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		}
		EntityCondition milkTranCondition = EntityCondition.makeCondition(milkTrnasferCondList);
		List<GenericValue> milkTransferList = FastList.newInstance();
		try{
			milkTransferList = delegator.findList("MilkTransfer", milkTranCondition, null, null, null, false);
		}catch(Exception e){
			Debug.logError("Error while getting MilkTransfers for the period ::"+customTimePeriodId+"  error:"+e,module);
			return ServiceUtil.returnError("Error while getting MilkTransfers for the period ::"+customTimePeriodId+"  error:"+e.getMessage());
		}
		if(UtilValidate.isEmpty(milkTransferList)){
			Debug.logError("No Records found for Billing ::",module);
			return ServiceUtil.returnError("No Records found for Billing ::");
		}
    	// Here we are tryingTo raise Invoice 
		Map inMap = FastMap.newInstance();
		inMap.put("userLogin", userLogin);
		if(UtilValidate.isNotEmpty(partyId)){
			inMap.put("partyId", partyId);
		}
		inMap.put("customTimePeriodId", customTimePeriodId);
		inMap.put("milkTransferList",milkTransferList);
		inMap.put("fromDateTime",fromDateTime);
		inMap.put("thruDateTime",thruDateTime);
		inMap.put("monthBegin",monthBegin);
		inMap.put("monthEnd",monthEnd);
		inMap.put("statusId", "generate");
		String statusId = (String)context.get("statusId");
		if(UtilValidate.isNotEmpty(statusId)){
			inMap.put("statusId", statusId);
		}
		
    	Map periodBillingResult = populatePeriodBillingForParty(dctx, inMap);
		if(ServiceUtil.isError(periodBillingResult)){
			Debug.logError("Error while populating PeridBilling :"+periodBillingResult,module);
			return ServiceUtil.returnError("Error while populating PeridBilling :"+ServiceUtil.getErrorMessage(periodBillingResult));
		}
		resultMap.putAll(periodBillingResult);
    	return resultMap;
    }//End of the service
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> populatePeriodBillingForParty(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map resultMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	List<GenericValue> milkTransferList = (List) context.get("milkTransferList");
    	Timestamp fromDateTime = (Timestamp) context.get("fromDateTime");
    	Timestamp thruDateTime = (Timestamp) context.get("thruDateTime");
    	Timestamp monthBegin = (Timestamp) context.get("monthBegin");
    	Timestamp monthEnd = (Timestamp) context.get("monthEnd");
    	String statusId = (String)context.get("statusId");
    	List<String> partyIdsList = FastList.newInstance();
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	String periodBillingId = null;
    	if(UtilValidate.isEmpty(milkTransferList)){
    		Debug.logError("MilkTransfers not Found ::",module);
    		return ServiceUtil.returnError("MilkTransfers not Found ::");
    	}
    	HashSet<String> partyIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(milkTransferList, "partyId", true));
    	if(UtilValidate.isNotEmpty(partyIdsSet)){
    		//Here we are checking for PeriodBilling If it is not existed
			List conditionList = FastList.newInstance();
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED","APPROVED_PAYMENT")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PB_PROC_MRGN"));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	GenericValue billingId =null;
	    	GenericValue newEntity = null;
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(statusId) && !statusId.equalsIgnoreCase("generate")){
	    			newEntity = EntityUtil.getFirst(periodBillingList);
	    			periodBillingId = (String) newEntity.get("periodBillingId");
	    		}
	    		
	    		if(UtilValidate.isNotEmpty(periodBillingList) && UtilValidate.isEmpty(periodBillingId)){
	    			// need to handle.
	    			Debug.logError("This billing is already  Generated or IN-Process",module);
	    			return ServiceUtil.returnError("This billing is already  Generated or IN-Process");
	    			
	    		}
	    		if(UtilValidate.isEmpty(periodBillingId)){
		    		newEntity = delegator.makeValue("PeriodBilling");
		            newEntity.set("billingTypeId", "PB_PROC_MRGN");
		            newEntity.set("customTimePeriodId", customTimePeriodId);
		            newEntity.set("statusId", "IN_PROCESS");
		            newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		            newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		            newEntity.set("createdDate", UtilDateTime.nowTimestamp());
		            newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());  
					delegator.createSetNextSeqId(newEntity);
					periodBillingId = (String) newEntity.get("periodBillingId");
	    		}
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			}	
    		
    		try{
		    	Map partyProductWiseMap = FastMap.newInstance();
	    		for(String partyId : partyIdsSet){
		    		Map productWiseAmtMap = FastMap.newInstance();
		    		List<GenericValue> partyTransfersList = EntityUtil.filterByCondition(milkTransferList, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		    		if(UtilValidate.isNotEmpty(partyTransfersList)){
		    	    	HashSet<String> productIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(partyTransfersList, "productId", true));
		    	    	if(UtilValidate.isNotEmpty(productIdsSet)){
		    	    		for(String productId:productIdsSet){
		    	    			Map priceCtx = FastMap.newInstance();
		    	    			priceCtx.put("userLogin",userLogin);
		    	    			priceCtx.put("productId",productId);
		    	    			priceCtx.put("partyId",partyId);
		    	    			List<GenericValue> productTransfers = FastList.newInstance();
		    	    			productTransfers = EntityUtil.filterByCondition(partyTransfersList,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		    	    			if(UtilValidate.isNotEmpty(productTransfers)){
		    	    				for(GenericValue productTransfer : productTransfers){
		    	    					// Here we are trying to create invoices for Purchase Billing
		    	    					BigDecimal price = BigDecimal.ZERO;
		    	    					BigDecimal premiumAmt = BigDecimal.ZERO;
		    	    					
		    	    					BigDecimal sendQtyKgs = (BigDecimal)productTransfer.get("quantity");
		    	    					BigDecimal sendQtyLtrs = (BigDecimal)productTransfer.get("quantity");
		    	    					BigDecimal sendFat = (BigDecimal)productTransfer.get("fat");
		    	    					BigDecimal sendSnf = (BigDecimal)productTransfer.get("snf");
		    	    					
		    	    					BigDecimal sendKgFat = (BigDecimal)productTransfer.get("sendKgFat");
		    	    					BigDecimal sendKgSnf = (BigDecimal)productTransfer.get("sendKgSnf");
		    	    					
		    	    					
		    	    					BigDecimal recdQtyKgs = (BigDecimal)productTransfer.get("receivedQuantity");
		    	    					BigDecimal recdQtyLtrs = (BigDecimal)productTransfer.get("receivedQuantityLtrs");
		    	    					BigDecimal recdFat = (BigDecimal)productTransfer.get("receivedFat");
		    	    					BigDecimal recdSnf = (BigDecimal)productTransfer.get("receivedSnf");
		    	    					BigDecimal recdKgFat = (BigDecimal)productTransfer.get("receivedKgFat");
		    	    					BigDecimal recdKgSnf = (BigDecimal)productTransfer.get("receivedKgSnf");
		    	    					
		    	    					Timestamp priceDate = (Timestamp)productTransfer.get("receiveDate");
		    	    					Map priceResultMap = FastMap.newInstance();
		    	    					
		    	    					priceCtx.put("priceDate",priceDate);
		    	    					priceCtx.put("fatPercent", sendFat);
		    	    					priceCtx.put("snfPercent", sendSnf);
		    	    					Map priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
		    	    					if(ServiceUtil.isError(priceResult)){
		    	    						Debug.logError("Error while getting priceChartDetails"+" ::For party :"+partyId+",productId ::"+productId+"  Error Message::"+priceResult, module);
		    	    						return ServiceUtil.returnError("Error while getting priceChartDetails"+" ::For party :"+partyId+",productId ::"+productId+"  Error Message::"+ServiceUtil.getErrorMessage(priceResult));
		    	    					}else{
		    	    						BigDecimal defaultRate = BigDecimal.ZERO;
		    	    						String uomId = "VLIQ_KG";
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("defaultRate"))){
		    	    							defaultRate = (BigDecimal)priceResult.get("defaultRate");
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("price"))){
		    	    							price = (BigDecimal)priceResult.get("price");
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("uomId"))){
		    	    							uomId = (String)priceResult.get("uomId");
		    	    						}
		    	    						BigDecimal fatPremiumRate = BigDecimal.ZERO;
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("fatPremium"))){
		    	    							fatPremiumRate = (BigDecimal)priceResult.get("fatPremium");
		    	    						}
		    	    						BigDecimal snfPremiumRate = BigDecimal.ZERO;
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("snfPremium"))){
		    	    							snfPremiumRate = (BigDecimal)priceResult.get("snfPremium");
		    	    						}
		    	    						if(price.compareTo(BigDecimal.ZERO)==0){
		    	    							fatPremiumRate = BigDecimal.ZERO;
		    	    							snfPremiumRate = BigDecimal.ZERO;
		    	    							defaultRate    = BigDecimal.ZERO;
		    	    						}
		    	    						
		    	    						String billQuantity = "ACK_QTY";
		    	    						// Here we are taking Dispatch Quality ,Because KMF Billing is done with dispatch QLTY and acknowledged QTY
		    	    						String billQuality = "DISP_QLTY";
		    	    						//DISP_QTY
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("billQuantity"))){
		    	    							billQuantity = (String)priceResult.get("billQuantity");
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("billQuality"))){
		    	    							billQuality = (String)priceResult.get("billQuality");
		    	    						}
		    	    						// here we are checking billQuality 
		    	    						if(UtilValidate.isNotEmpty(billQuality) && (!billQuality.equalsIgnoreCase("DISP_QLTY"))){
		    	    							// If bill Quality is Not Dispatched then we have to get the Ack Qlty Rate
		    	    							priceCtx.put("fatPercent", recdFat);
		    	    	    					priceCtx.put("snfPercent", recdSnf);
		    	    	    					priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
		    	    	    					
		    	    	    					if(ServiceUtil.isSuccess(priceResult)){
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("defaultRate"))){
		    	    	    							defaultRate = (BigDecimal)priceResult.get("defaultRate");
		    	    	    						}
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("fatPremium"))){
		    	    	    							fatPremiumRate = (BigDecimal)priceResult.get("fatPremium");
		    	    	    						}
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("snfPremium"))){
		    	    	    							snfPremiumRate = (BigDecimal)priceResult.get("snfPremium");
		    	    	    						}
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("price"))){
				    	    							price = (BigDecimal)priceResult.get("price");
				    	    						}
		    	    	    						if(price.compareTo(BigDecimal.ZERO)==0){
				    	    							fatPremiumRate = BigDecimal.ZERO;
				    	    							snfPremiumRate = BigDecimal.ZERO;
				    	    							defaultRate    = BigDecimal.ZERO;
				    	    						}
		    	    	    					}
		    	    						}
		    	    						// Here we are taking billing qty as per recieved. Based on uomId we need to handle it
		    	    						BigDecimal billingQuantity = recdQtyKgs;	
		    	    						if(UtilValidate.isNotEmpty(billQuantity) && (!billQuantity.equalsIgnoreCase("ACK_QTY"))){
		    	    							billingQuantity = sendQtyKgs;
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(uomId) && (uomId.equalsIgnoreCase("VLIQ_L"))){
		    	    							if(billQuantity.equalsIgnoreCase("DISP_QTY")){
		    	    								billingQuantity = sendQtyLtrs;
		    	    							}else{
		    	    								billingQuantity = recdQtyLtrs;
		    	    							}
		    	    							
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(uomId) && (uomId.equalsIgnoreCase("VLIQ_KGFAT"))){
		    	    							if(billQuantity.equalsIgnoreCase("DISP_QTY")){
		    	    								billingQuantity = sendKgFat;
		    	    							}else{
		    	    								billingQuantity = recdKgFat;
		    	    							}
		    	    							
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(uomId) && (uomId.equalsIgnoreCase("VLIQ_TS"))){
		    	    							if(billQuantity.equalsIgnoreCase("DISP_TS")){
		    	    								billingQuantity = sendKgFat.add(sendKgSnf);
		    	    							}else{
		    	    								billingQuantity = recdKgFat.add(recdKgSnf);
		    	    							}
		    	    						}
		    	    						// price Rate  is addition of defaultRate+fatPremium+snfPremium
		    	    						BigDecimal priceRate = defaultRate.add(fatPremiumRate).add(snfPremiumRate);
		    	    						BigDecimal totUnitAmt = billingQuantity.multiply(defaultRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						BigDecimal fatPremAmt = billingQuantity.multiply(fatPremiumRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						BigDecimal snfPremAmt = billingQuantity.multiply(snfPremiumRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						BigDecimal totAmt = billingQuantity.multiply(priceRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						
		    	    						Map billingDetailsMap = FastMap.newInstance();
		    	    						if(UtilValidate.isNotEmpty(billingQuantity) && billingQuantity.compareTo(BigDecimal.ZERO)==1 && UtilValidate.isNotEmpty(totAmt) && totAmt.compareTo(BigDecimal.ZERO)==1){
		    	    							billingDetailsMap.put("amount",totAmt);
		    	    							billingDetailsMap.put("quantity",billingQuantity);
		    	    						}
		    	    						if(UtilValidate.isEmpty(productWiseAmtMap) || UtilValidate.isEmpty(productWiseAmtMap.get(productId))){
		    	    							productWiseAmtMap.put(productId,billingDetailsMap);
		    	    						}else{
		    	    							Map tempMap = FastMap.newInstance();
		    	    							tempMap.putAll((Map)productWiseAmtMap.get(productId));
		    	    							for(Object billingDetKey : billingDetailsMap.keySet()){
		    	    								String billngDetKeyStr = billingDetKey.toString();
		    	    								tempMap.put(billingDetKey, ((BigDecimal)tempMap.get(billingDetKey)).add(((BigDecimal)billingDetailsMap.get(billingDetKey))));
		    	    							}
		    	    							productWiseAmtMap.put(productId, tempMap);
		    	    						}
		    	    						// Here we are trying to store fatPremium,snfPremium , price value in MilkTransfertransfer
		    	    						try{
		    	    							productTransfer.set("unitPrice", defaultRate);
		    	    							productTransfer.set("fatPremium", fatPremiumRate);
		    	    							productTransfer.set("snfPremium", snfPremiumRate);
		    	    							productTransfer.set("billQuantity", billQuantity);
		    	    							productTransfer.set("billQuality", billQuality);
		    	    							productTransfer.store();
		    	    						}catch(Exception e){
		    	    							Debug.logError("Error while storing prices in Milk Transfer :"+e,module);
		    	    							return ServiceUtil.returnError("Error while storing prices in Milk Transfer :"+e);
		    	    						}
		    	    					}
		    	    				}
		    	    			}
		    	    		}
		    	    	}
		    		}
		    		if(UtilValidate.isNotEmpty(productWiseAmtMap)){
		    			partyProductWiseMap.put(partyId, productWiseAmtMap);
		    		}
		    		// Here we are trying to create invoice for each party with product wise items
		    		Map inVoiceInMap = FastMap.newInstance();
					inVoiceInMap.put("userLogin", userLogin);
					inVoiceInMap.put("invoiceTypeId", "MILK_OUT");
					inVoiceInMap.put("productWiseAmtMap", productWiseAmtMap);
					inVoiceInMap.put("partyId",partyId);
					// here we hvae to raise invoice for union
					inVoiceInMap.put("unionId",partyId);
					//here we are trying to get unionId
					List unionsList = FastList.newInstance();
					try {  	   
				        	List partyRelationShipConditionList = FastList.newInstance();
				        	partyRelationShipConditionList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyId));
				        	partyRelationShipConditionList.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"UNION"));
				 	 	   	EntityCondition partyRelationShipCondition  = EntityCondition.makeCondition(partyRelationShipConditionList)	;
				        	unionsList = delegator.findList("PartyRelationship",partyRelationShipCondition,null,null,null,false);
				        	unionsList = EntityUtil.filterByDate(unionsList,monthBegin);
				        }catch(Exception e){
				        	Debug.logError("Error while getting ancestor nodes ::"+e,module);
				        	return ServiceUtil.returnError("Error while getting ancestor nodes ::"+e.getMessage());
				        }
					String unionId="";
					if(UtilValidate.isNotEmpty(unionsList)){
						GenericValue unionDetails = EntityUtil.getFirst(unionsList);
						unionId = unionDetails.getString("partyIdFrom");
					}
					if(UtilValidate.isNotEmpty(unionId)){
						inVoiceInMap.put("unionId",unionId);
					}
					
		    		inVoiceInMap.put("periodBillingId", periodBillingId);
		    		inVoiceInMap.put("fromDateTime", monthBegin);
		    		inVoiceInMap.put("thruDateTime", monthEnd);
		    		inVoiceInMap.put("description", "MILK PURCHASE FROM "+UtilDateTime.toDateString(monthBegin,"dd-MMM-yyyy")+" to "+UtilDateTime.toDateString(monthEnd,"dd-MMM-yyyy"));
		    		if(UtilValidate.isNotEmpty(statusId) && !statusId.equalsIgnoreCase("generate")){
		    			Map invoiceResultMap = createInvoiceForParty(dctx,inVoiceInMap);
			    		if(ServiceUtil.isError(invoiceResultMap)){
			    			Debug.logError("Error while creating invoice for party :"+partyId+" errorMessage :"+invoiceResultMap, module);
			    			return ServiceUtil.returnError("Error while creating invoice for party :"+partyId+" errorMessage :"+ServiceUtil.getErrorMessage(invoiceResultMap));
			    		}
		    		}
		    	}
	    	}catch(Exception e){
	    		Debug.logError("Error while populating purchase billing "+e, module);
	    		return ServiceUtil.returnError("Error while populating purchase billing "+e.getMessage());
	    	}
    		// here we are updating PeriodBilling status to generated
    		try{
    			GenericValue periodDetails = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId",periodBillingId), false);
    			if(UtilValidate.isNotEmpty(periodDetails)){
    				periodDetails.set("statusId","GENERATED");
    				periodDetails.store();
    			}
    		}catch(Exception e){
    			Debug.logError("Error while updating status of Period Billing"+e,module);
    			return ServiceUtil.returnError("Error while updating status of Period Billing"+e.getMessage());
    		}
    	}
    	resultMap = ServiceUtil.returnSuccess("Billing Successfully Generated :"+periodBillingId);
    	resultMap.put("periodBillingId",periodBillingId);
    	return resultMap;
    }// end of the service
    
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createInvoiceForParty(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map resultMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	String periodBillingId = (String)context.get("periodBillingId");
    	Timestamp thruDateTime = (Timestamp)context.get("thruDateTime");
    	Timestamp fromDateTime = (Timestamp)context.get("fromDateTime");
    	String unionId = (String)context.get("unionId");
    	Map productWiseAmtMap = (Map)context.get("productWiseAmtMap");
    	String description = (String)context.get("description");
    	if(UtilValidate.isEmpty(productWiseAmtMap)){
    		Debug.logError("product Wise Map is not found",module);
    		return ServiceUtil.returnError("product Wise Map is not found");
    	}
    	String partyIdTo = (String) context.get("partyId");
    	Map<String, Object> createInvoiceContext = FastMap.newInstance();
        createInvoiceContext.put("partyId", "Company");
        createInvoiceContext.put("partyIdFrom", partyIdTo);
        if(UtilValidate.isNotEmpty(unionId) && !unionId.equalsIgnoreCase(partyIdTo)){
        	createInvoiceContext.put("partyIdFrom", unionId);
        	String partyName = (String)PartyHelper.getPartyName(delegator, partyIdTo, true);
			
			if(UtilValidate.isEmpty(partyName)){
				partyName = "";
			}
        	if(UtilValidate.isNotEmpty(description)){
        		description = description.concat(",");
        	}
        	description = description.concat("On behalf of "+partyIdTo+"["+partyName+"]");
        }
        createInvoiceContext.put("dueDate", thruDateTime);
        createInvoiceContext.put("invoiceDate",thruDateTime);
        createInvoiceContext.put("invoiceTypeId", "MILK_OUT");
        createInvoiceContext.put("referenceNumber", "MILK_PUR_"+periodBillingId);
        createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
        createInvoiceContext.put("currencyUomId", "INR");
        createInvoiceContext.put("description", description);
        createInvoiceContext.put("userLogin", userLogin);

        // store the invoice first
        Map<String, Object> createInvoiceResult = FastMap.newInstance();
        try{
	        createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
	        if (ServiceUtil.isError(createInvoiceResult)) {
	    		//generationFailed = true;
                Debug.logWarning("There was an error while creating  Invoice For TransporterCommission: " + ServiceUtil.getErrorMessage(createInvoiceResult), module);
        		return ServiceUtil.returnError("There was an error while creating Invoice for  TransporterCommission: " + ServiceUtil.getErrorMessage(createInvoiceResult));          	            
            }
        }catch(Exception e){
        	Debug.logError("Error while creating invoice for partyId:"+partyIdTo+" Message:"+e,module);
    		return ServiceUtil.returnError("Error while creating invoice for partyId:"+partyIdTo+" Message:"+e.getMessage());
        }
        // call service for creation invoice);
        String invoiceId = (String) createInvoiceResult.get("invoiceId");
    	// here we are creating Invoice Items
        Map invItemInMap = FastMap.newInstance();
        invItemInMap.put("userLogin",userLogin);
        invItemInMap.put("invoiceId",invoiceId);
        invItemInMap.put("invoiceItemTypeId","MILKSTORE1");
        for(Object productKey : productWiseAmtMap.keySet()){
        	String productKeyStr = productKey.toString();
        	Map tempProdMap = FastMap.newInstance();
        	tempProdMap.putAll((Map)productWiseAmtMap.get(productKey));
        	//BigDecimal quantity = (BigDecimal)tempProdMap.get("quantity");
        	BigDecimal quantity = BigDecimal.ONE;
        	BigDecimal amount = (BigDecimal)tempProdMap.get("amount");
        	/*BigDecimal unitPrice = BigDecimal.ZERO;
        	if(UtilValidate.isNotEmpty(quantity) && UtilValidate.isNotEmpty(amount) && quantity.compareTo(BigDecimal.ZERO)==1){
        		unitPrice = amount.divide(quantity, 8,BigDecimal.ROUND_HALF_UP);
        	}*/
        	invItemInMap.put("amount",amount);
        	invItemInMap.put("quantity",quantity);
        	invItemInMap.put("productId",productKeyStr);
        	invItemInMap.put("description","Bill Qty(Kgs) : "+(BigDecimal)tempProdMap.get("quantity"));
        	try{
        		resultMap =  dispatcher.runSync("createInvoiceItem",invItemInMap);
        	}catch(Exception e){
        		Debug.logError("Error while creating invoiceItem for Product :"+productKeyStr+",partyId:"+partyIdTo+" Message:"+e,module);
        		resultMap = ServiceUtil.returnError("Error while creating invoiceItem for Product :"+productKeyStr+",partyId:"+partyIdTo+" Message:"+e.getMessage());
        	}
        }
        return resultMap;
    }// End of the service
    
    
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getShiftDaysByType(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map resultMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	Timestamp fromDateTime = (Timestamp)context.get("fromDate");
    	if(UtilValidate.isEmpty(fromDateTime)){
    		fromDateTime = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    	}
    	Timestamp thruDateTime = (Timestamp)context.get("thruDate");
    	if(UtilValidate.isEmpty(thruDateTime)){
    		thruDateTime = UtilDateTime.getDayEnd(fromDateTime);
    	}
    	
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	String shiftType = (String) context.get("shiftType");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String shiftTypeId = (String) context.get("shiftTypeId");
    	List<GenericValue> shiftsList = FastList.newInstance(); 
    	if(UtilValidate.isEmpty(shiftType)){
    		Debug.logError("ShiftType can not be empty",module);
    		return ServiceUtil.returnError("ShiftType can not be empty");
    	}
    	try{
    		List conditionList = FastList.newInstance();
    		conditionList.add(EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS,shiftType));
    		if(UtilValidate.isNotEmpty(shiftTypeId)){
    			conditionList.add(EntityCondition.makeCondition("shiftTypeId",EntityOperator.EQUALS,shiftTypeId));
    		}
    		EntityCondition condition = EntityCondition.makeCondition(conditionList);
    		shiftsList = delegator.findList("WorkShiftTypePeriodAndMap",condition,null,UtilMisc.toList("shiftTypeId"),null,false);
    		if(UtilValidate.isEmpty(shiftsList)){
    			Debug.logError("Shifts not found for shiftType ::"+shiftType,module);
    		}
    		
    	}catch(GenericEntityException e){
    		Debug.logError("Error while getting shiftType ::"+e,module);
    		return ServiceUtil.returnError("Error while getting shiftType ::"+e.getMessage());
    	}
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(UtilValidate.isNotEmpty(shiftsList)){
			String startTime= null;
			String endTime = null;
			if(UtilValidate.isNotEmpty(shiftTypeId)){
				GenericValue shiftDetails = EntityUtil.getFirst(shiftsList);
				Timestamp thruDTimestampStart = thruDateTime;
				Timestamp thruDTimestampEnd = thruDateTime;
				startTime = ((java.sql.Time)shiftDetails.get("startTime")).toString();
				endTime = ((java.sql.Time)shiftDetails.get("endTime")).toString();
				if(UtilValidate.isNotEmpty(startTime)){
					String dateString = UtilDateTime.toDateString(thruDateTime,"yyyy-MM-dd");
					dateString = dateString.concat(" "+startTime);
					try{
						thruDTimestampStart =  new java.sql.Timestamp(sdf.parse(dateString).getTime());
					}catch(ParseException e){
						Debug.logError("Error while getting day Start by shift Time"+e,module);
						return ServiceUtil.returnError("Error while getting day Start by shift Time"+e);
					}
				}
				fromDateTime = thruDTimestampStart;
				if(UtilValidate.isNotEmpty(endTime)){
					String dateString = UtilDateTime.toDateString(thruDateTime,"yyyy-MM-dd");
					dateString = dateString.concat(" "+endTime);
					try{
						thruDTimestampEnd = new java.sql.Timestamp(sdf.parse(dateString).getTime());
					}catch(ParseException e){
						Debug.logError("Error while getting day Start by shift Time"+e,module);
						return ServiceUtil.returnError("Error while getting day Start by shift Time"+e);
					}
				}
				if(UtilValidate.isNotEmpty(thruDTimestampEnd) && UtilValidate.isNotEmpty(thruDTimestampStart)){
					if(thruDTimestampStart.compareTo(thruDTimestampEnd)>0){
						thruDateTime = UtilDateTime.addDaysToTimestamp(thruDTimestampEnd, 1);
					}else{
						thruDateTime = thruDTimestampEnd;
					}
				}
			}else{
			List<GenericValue> firstShiftList = EntityUtil.filterByCondition(shiftsList, EntityCondition.makeCondition("shiftTypeId",EntityOperator.EQUALS,"MILK_SHIFT_01"));
			if(UtilValidate.isNotEmpty(firstShiftList)){
				GenericValue firstShift = EntityUtil.getFirst(firstShiftList);
				startTime = ((java.sql.Time)firstShift.get("startTime")).toString();
				if(UtilValidate.isNotEmpty(startTime)){
					String dateString = UtilDateTime.toDateString(fromDateTime,"yyyy-MM-dd");
					dateString = dateString.concat(" "+startTime);
					try{
						fromDateTime =  new java.sql.Timestamp(sdf.parse(dateString).getTime());
					}catch(ParseException e){
						Debug.logError("Error while getting day Start by shift Time"+e,module);
						return ServiceUtil.returnError("Error while getting day Start by shift Time"+e);
					}
				}
			}
				List<GenericValue> lastShiftList = EntityUtil.filterByCondition(shiftsList, EntityCondition.makeCondition("shiftTypeId",EntityOperator.EQUALS,"MILK_SHIFT_NIGHT"));
				if(UtilValidate.isNotEmpty(lastShiftList)){
					Timestamp thruDTimestampStart = thruDateTime;
					Timestamp thruDTimestampEnd = thruDateTime;
					GenericValue lastShift = EntityUtil.getFirst(lastShiftList);
					startTime = ((java.sql.Time)lastShift.get("startTime")).toString();
					endTime = ((java.sql.Time)lastShift.get("endTime")).toString();
					if(UtilValidate.isNotEmpty(startTime)){
						String dateString = UtilDateTime.toDateString(thruDateTime,"yyyy-MM-dd");
						dateString = dateString.concat(" "+startTime);
						try{
							thruDTimestampStart =  new java.sql.Timestamp(sdf.parse(dateString).getTime());
						}catch(ParseException e){
							Debug.logError("Error while getting day Start by shift Time"+e,module);
							return ServiceUtil.returnError("Error while getting day Start by shift Time"+e);
						}
					}
					if(UtilValidate.isNotEmpty(endTime)){
						String dateString = UtilDateTime.toDateString(thruDateTime,"yyyy-MM-dd");
						dateString = dateString.concat(" "+endTime);
						try{
							thruDTimestampEnd = new java.sql.Timestamp(sdf.parse(dateString).getTime());
						}catch(ParseException e){
							Debug.logError("Error while getting day Start by shift Time"+e,module);
							return ServiceUtil.returnError("Error while getting day Start by shift Time"+e);
						}
					}
					if(UtilValidate.isNotEmpty(thruDTimestampEnd) && UtilValidate.isNotEmpty(thruDTimestampStart)){
						if(thruDTimestampStart.compareTo(thruDTimestampEnd)>0){
							thruDateTime = UtilDateTime.addDaysToTimestamp(thruDTimestampEnd, 1);
						}else{
							thruDateTime = thruDTimestampEnd;
						}
					}
				}
			}
    	}
		
		
		
    	resultMap = ServiceUtil.returnSuccess();
    	resultMap.put("shiftsList", shiftsList);
    	resultMap.put("fromDate", fromDateTime);
    	resultMap.put("thruDate", thruDateTime);
    	return resultMap;
    }// End of the service
    
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateMilkReceiptPeriodBilling (DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	TimeZone timeZone = TimeZone.getDefault();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = new HashMap<String, Object>();
        result = ServiceUtil.returnSuccess();
        String periodBillingId = (String) context.get("periodBillingId");
        String statusId = (String) context.get("statusId");
        if(UtilValidate.isEmpty(statusId)){
        	//statusId="GENERATED";
        	Debug.logError("status can not be empty",module);
        	return ServiceUtil.returnError("status can not be empty");
        }
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        	  periodBilling.set("statusId", statusId);
			  periodBilling.store();
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}   
        GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1,"Error While Finding Customtime Period");
			return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
		}
		if("APPROVED".equalsIgnoreCase(statusId)){
			Map populatePeriodBillingInMap = FastMap.newInstance();
			
			populatePeriodBillingInMap.put("statusId", "approved");
			populatePeriodBillingInMap.put("userLogin", userLogin);
			populatePeriodBillingInMap.put("customTimePeriodId", customTimePeriodId);
			
			Map  populateMilkReceiptPeriodBillingResult = populateMilkReceiptPeriodBilling(dctx,populatePeriodBillingInMap);
			if(ServiceUtil.isError(populateMilkReceiptPeriodBillingResult)){
				Debug.logError("Error while creating invoices :"+populateMilkReceiptPeriodBillingResult,module);
				return ServiceUtil.returnError("Error while creating invoices :"+ServiceUtil.getErrorMessage(populateMilkReceiptPeriodBillingResult));
			}
			
			Map updatePurchaseInvoiceBilling = updatePurchaseBillingInvoices(dctx,UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin,"statusId","INVOICE_READY"));
			if(ServiceUtil.isError(updatePurchaseInvoiceBilling)){
				Debug.logError("Error while processing invoices :"+updatePurchaseInvoiceBilling,module);
				return ServiceUtil.returnError("Error while processing invoices :"+ServiceUtil.getErrorMessage(updatePurchaseInvoiceBilling));
			}
			try{
				periodBilling.set("statusId","APPROVED");
				delegator.store(periodBilling);
			}catch(GenericEntityException e){
				Debug.logError("Error while approving the billing ::"+e,module);
				return ServiceUtil.returnError("Error while approving the billing ::"+e.getMessage());
			}
			result = ServiceUtil.returnSuccess("Billing successfully Approved for "+periodBillingId);
		}
		if("APPROVED_PAYMENT".equalsIgnoreCase(statusId)){
			Map purchasePaymentResult=createPurchaseBillingPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
			try{
	        	  periodBilling.set("statusId", "APPROVED_PAYMENT");
				  periodBilling.store();
	        }catch (GenericEntityException e) {
	    		Debug.logError("Unable to Make Payment Process For Purchase Billing"+e, module);
	    		return ServiceUtil.returnError("Unable to Make Payment Process For Purchase Billing! "); 
			}   
			result = ServiceUtil.returnSuccess("Payment Approved successfully for "+periodBillingId);
			result.putAll(purchasePaymentResult);
			/*Map updatePurchaseInvoiceBilling = updatePurchaseBillingInvoices(dctx,UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin,"statusId","INVOICE_PAID"));
			if(ServiceUtil.isError(updatePurchaseInvoiceBilling)){
				Debug.logError("Error while processing invoices :"+updatePurchaseInvoiceBilling,module);
				return ServiceUtil.returnError("Error while processing invoices :"+ServiceUtil.getErrorMessage(updatePurchaseInvoiceBilling));
			}*/
	       }
		if("REJECT_PAYMENT".equalsIgnoreCase(statusId)){
			Map purchasePaymentCancelResult=cancelPurchaseBillingPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
			try{
	        	  periodBilling.set("statusId", "REJECT_PAYMENT");
				  periodBilling.store();
	        }catch (GenericEntityException e) {
	    		Debug.logError("Unable To Cancel Purchase Bill Payment"+e, module);
	    		return ServiceUtil.returnError("Unable To Cancel Purchase Bill Payment.. "); 
			}   
			result = ServiceUtil.returnSuccess("Payment Rejected successfully for "+periodBillingId);
			result.putAll(purchasePaymentCancelResult);
	       }
		return result;
    }  
    
    public static Map<String, Object> updatePurchaseBillingInvoices(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	TimeZone timeZone = TimeZone.getDefault();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = new HashMap<String, Object>();
        result = ServiceUtil.returnSuccess();
        String periodBillingId = (String) context.get("periodBillingId");
        List<GenericValue> invoicesList = FastList.newInstance();
        String referenceNumber = "MILK_PUR_"+periodBillingId;
        String statusId = (String) context.get("statusId");
        try{
        	List conditionList = FastList.newInstance();
        	conditionList.add(EntityCondition.makeCondition("referenceNumber",EntityOperator.EQUALS,referenceNumber));
        	if(statusId.equalsIgnoreCase("INVOICE_READY")){
        		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_IN_PROCESS"));
        	}
        	if(statusId.equalsIgnoreCase("INVOICE_PAID")){
        		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_READY"));
        	}
        	EntityCondition condition = EntityCondition.makeCondition(conditionList);
        	invoicesList = delegator.findList("Invoice", condition, null, null, null, false);
        	if(UtilValidate.isEmpty(invoicesList)){
        		Debug.logError("Invoices not found for the billing period :"+periodBillingId,module);
        		return ServiceUtil.returnError("Invoices not found for the billing period :"+periodBillingId);
        	}
        }catch(Exception e){
        	Debug.logError("Error while getting invoices "+e,module);
        	return ServiceUtil.returnError("Error while getting invoices "+e.getMessage());
        }
        HashSet<String> invoiceIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(invoicesList, "invoiceId", true));
        for(String invoiceId : invoiceIdsSet){
	        Map invoiceCtx = FastMap.newInstance();
	      //set to Ready for Posting
	        invoiceCtx.put("userLogin", userLogin);
	        invoiceCtx.put("invoiceId", invoiceId);
	        invoiceCtx.put("statusId",statusId);
	        try{
	        	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
	        	if (ServiceUtil.isError(invoiceResult)) {
	        		Debug.logError(invoiceResult.toString(), module);
	                return ServiceUtil.returnError(null, null, null, invoiceResult);
	            }	             	
	        }catch(GenericServiceException e){
	        	 Debug.logError(e, e.toString(), module);
	            return ServiceUtil.returnError(e.toString());
	        } 
        }
    	return result;
    }//end of the method
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createPurchaseBillingPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 String periodBillingId = (String) context.get("periodBillingId");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		List<String> billingInvoiceIdsList=(List<String>)getPurchaseBillingInvoices(dctx, UtilMisc.toMap("periodBillingId", periodBillingId,"userLogin", userLogin)).get("invoiceIdsList");
		boolean useFifo = Boolean.FALSE;
		if (UtilValidate.isNotEmpty(context.get("useFifo"))) {
			useFifo = (Boolean) context.get("useFifo");
		}
		Locale locale = (Locale) context.get("locale");
		String paymentMethodType = (String) context.get("paymentMethodTypeId");
		
		String facilityId = (String) context.get("facilityId");
	
		String orderId = (String) context.get("orderId");
		BigDecimal paymentAmount = ProductEvents.parseBigDecimalForEntity((String) context.get("amount"));
		String paymentRef = "MILK_PUR_"+periodBillingId;
		String paymentId = "";
		boolean roundingAdjustmentFlag = Boolean.TRUE;
		List exprListForParameters = FastList.newInstance();
		List boothOrdersList = FastList.newInstance();
		Timestamp paymentTimestamp = UtilDateTime.nowTimestamp();
		Timestamp instrumentDate = UtilDateTime.nowTimestamp();
		
		Map<String, Object> paymentCtx = UtilMisc.<String, Object>toMap("paymentTypeId", "EXPENSE_PAYOUT");
		paymentCtx.put("paymentMethodTypeId", "CHEQUE_PAYIN");
		paymentCtx.put("paymentMethodId", "");
		paymentCtx.put("partyId","Company");	
	 try { 
		for(String invoiceId:billingInvoiceIdsList){
				
			Map<String, Object> getInvoicePaymentInfoListResult = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.toMap("userLogin", userLogin, "invoiceId",invoiceId));
			if (ServiceUtil.isError(getInvoicePaymentInfoListResult)) {
				Debug.logError(getInvoicePaymentInfoListResult.toString(),module);
				return ServiceUtil.returnError(null, null, null,getInvoicePaymentInfoListResult);
			}
			Map invoicePaymentInfo = (Map) ((List) getInvoicePaymentInfoListResult.get("invoicePaymentInfoList")).get(0);
			BigDecimal outStandingAmount = (BigDecimal) invoicePaymentInfo.get("outstandingAmount");
			if(UtilValidate.isNotEmpty(invoicePaymentInfo)){
				 if(outStandingAmount.compareTo(BigDecimal.ZERO)>0){
					GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId",invoiceId), false);
		           // paymentCtx.put("partyIdFrom","Company");
		            paymentCtx.put("organizationPartyId",invoice.getString("partyIdFrom"));
		            paymentCtx.put("facilityId", invoice.getString("facilityId"));
		            paymentCtx.put("paymentPurposeType", "");
		            paymentCtx.put("paymentRefNum", paymentRef); 
		            paymentCtx.put("instrumentDate", invoice.getTimestamp("dueDate"));
					paymentCtx.put("paymentDate", invoice.getTimestamp("dueDate"));
					paymentCtx.put("effectiveDate", invoice.getTimestamp("dueDate"));
		            //paymentCtx.put("statusId", "PMNT_RECEIVED");
		            paymentCtx.put("statusId", "PMNT_NOT_PAID");
		            paymentCtx.put("isEnableAcctg", "Y");
		            paymentCtx.put("amount", outStandingAmount);
		            paymentCtx.put("userLogin", userLogin); 
		            paymentCtx.put("invoices", UtilMisc.toList(invoiceId));
		    		try{
			            Map<String, Object> paymentResult = dispatcher.runSync("createPaymentAndApplicationForInvoices", paymentCtx);
			            if (ServiceUtil.isError(paymentResult)) {
			            	Debug.logError(paymentResult.toString(), module);
			                return ServiceUtil.returnError(null, null, null, paymentResult);
			            }
			            paymentId = (String)paymentResult.get("paymentId");
		            }catch (Exception e) {
			            Debug.logError(e, e.toString(), module);
			            return ServiceUtil.returnError(e.toString());
			        }
				 }
			 }
		}
	 }catch (GenericEntityException e) {
			Debug.logError("Error while Creating Payment for Purchase billing "+ e, module);
			return ServiceUtil.returnError("Error while Creating Payment for Purchase billing ");
		} catch (GenericServiceException e) {
			Debug.logError("Error while Creating Payment for PurchaseBilling" + e,module);
			return ServiceUtil.returnError("Error while Creating Payment for PurchaseBilling");
		}
		result = ServiceUtil.returnSuccess("Payment successfully done for This Billing ..!");
		return result;
	}//end of the service
    
    
    public static Map<String, Object> getPurchaseBillingInvoices(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Locale locale = (Locale) context.get("locale");
        boolean cancelationFailed = false;	
        String periodBillingId = (String) context.get("periodBillingId");
        List invoiceIdsList=FastList.newInstance();
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}        
    	GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil.returnError("Error in customTimePeriod" + e1);
		}
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
		Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
		Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
		
		try{
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
			  conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS,  "MILK_PUR_"+periodBillingId));
			  
		     conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "MILK_OUT"));
		     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
		     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
             conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED")); 
            
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
        	List<GenericValue> invoiceRows = delegator.findList("Invoice", condition, null, null, null, false);
	        invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceRows, "invoiceId", false);
	        
		  }catch(GenericEntityException e){
	        	Debug.logError("Unable to get PURCHASE BILLING invoices"+e, module);
	    		return ServiceUtil.returnError("Unable to get PURCHASE BILLING invoices "); 
	      }
	     result.put("invoiceIdsList", invoiceIdsList);
        return result;
    }//
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> cancelPurchaseBillingPayment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String periodBillingId = (String) context.get("periodBillingId");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<String> billingPaymentIdsList=(List<String>)getPurchaseBillingPayments(dctx, UtilMisc.toMap("periodBillingId", periodBillingId,"userLogin", userLogin)).get("paymentIdsList");
		Locale locale = (Locale) context.get("locale");
		if(UtilValidate.isEmpty(billingPaymentIdsList)){
			result = ServiceUtil.returnSuccess("No Payments Found To Cancel ..!");
			return result;
		}
	 try { 
		for(String paymentId:billingPaymentIdsList){
		            	 Map<String, Object> removePaymentApplResult = dispatcher.runSync("voidPayment", UtilMisc.toMap("userLogin" ,userLogin ,"paymentId", paymentId));
						 if (ServiceUtil.isError(removePaymentApplResult)) {
				            	Debug.logError(removePaymentApplResult.toString(), module);    			
				                return ServiceUtil.returnError(null, null, null, removePaymentApplResult);
				         }
		    }
	    }catch (GenericServiceException e) {
			Debug.logError("Error while Cancel Payment for Purchase Billing" + e,module);
			return ServiceUtil.returnError("Error while Cancel Payment for Purchase Billing");
		}
		result = ServiceUtil.returnSuccess("Payment  Cancelled For Billing ..!");
		return result;
	}// end of service
    
    
    //getPurchaseBillingPayments
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    
    public static Map<String, Object> getPurchaseBillingPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Locale locale = (Locale) context.get("locale");
        boolean cancelationFailed = false;	
        String periodBillingId = (String) context.get("periodBillingId");
        List paymentIdsList=FastList.newInstance();
    	List purchasePaymentsList = FastList.newInstance();
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}        
    	GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil.returnError("Error in customTimePeriod" + e1);
		}
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
		Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
		Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
		
		try{
			List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
			  conditionList.add(EntityCondition.makeCondition("paymentRefNum", EntityOperator.EQUALS,  "MILK_PUR_"+periodBillingId));
		     conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, "EXPENSE_PAYOUT"));
             conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
            
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
        	List<GenericValue> paymentsList = delegator.findList("Payment", condition, null, null, null, false);
	        paymentIdsList = EntityUtil.getFieldListFromEntityList(paymentsList, "paymentId", false);
	        purchasePaymentsList.addAll(paymentsList);
		  }catch(GenericEntityException e){
	        	Debug.logError("Unable to get Puchase Billing Payments"+e, module);
	    		return ServiceUtil.returnError("Unable to get purchase Billing Payments "); 
	      }
	     result.put("paymentIdsList", paymentIdsList);
	     result.put("purchasePaymentsList", purchasePaymentsList);
        return result;
    }// end of the service
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> cancelPurchaseBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
        //Map<String, Object> result = ServiceUtil.returnSuccess();
        
        Map<String, Object> result = new HashMap<String, Object>();
        
        String periodBillingId = (String) context.get("periodBillingId");
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}        
    	periodBilling.set("statusId", "CANCEL_INPROCESS");
        boolean cancelationFailed = false;	
    	try{
    		periodBilling.store();    		
    	}catch (Exception e) {
    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
		}
    	//first Cancel Purchase billing  payments
		Map purchasePaymentCancelResult=cancelPurchaseBillingPayment(dctx, UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin));
		if (ServiceUtil.isError(purchasePaymentCancelResult)) {
			cancelationFailed = true;
			periodBilling.set("statusId", "CANCEL_FAILED");
			try{
	    		periodBilling.store();    		
	    	}catch (Exception e) {
	    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
	    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
			}
            Debug.logWarning("There was an error while Canceling Payment: " + ServiceUtil.getErrorMessage(purchasePaymentCancelResult), module);
    		return ServiceUtil.returnError("There was an error while Canceling Payment: " + ServiceUtil.getErrorMessage(purchasePaymentCancelResult));          	            
        } 
           
    	try{
    		Map<String,  Object> inputContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"userLogin", userLogin);
			Map cancelPtcTranInvoiceResult = dispatcher.runSync("cancelPurchaseBillingInvoice", inputContext);
			if(ServiceUtil.isError(cancelPtcTranInvoiceResult)){
				Debug.logError("Error whiel cancelling PTC Transporter Invoice",module);
				return ServiceUtil.returnError("Error whiel cancelling PTC Transporter Invoice");
			}
			
    	} catch (GenericServiceException e) {
            Debug.logError(e, "Error in canceling 'transporterMargin' service", module);
            return ServiceUtil.returnError(e.getMessage());
        } 
    	result = ServiceUtil.returnSuccess("Successfully cancelled");
        return result;
    }//end of the service
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    
    public static Map<String, Object> cancelPurchaseBillingInvoice(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Locale locale = (Locale) context.get("locale");
        boolean cancelationFailed = false;	
        String periodBillingId = (String) context.get("periodBillingId");
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}        
    	GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil.returnError("Error in customTimePeriod" + e1);
		}
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
		Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
		Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
		
		if(customTimePeriod == null){
			cancelationFailed = true;
		}
		
	try{
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
		  conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS,  "MILK_PUR_"+periodBillingId));
		  
	     conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "MILK_OUT"));
	     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
	     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
         conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED")); 
        
    	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
    	List<GenericValue> invoiceRows = delegator.findList("Invoice", condition, null, null, null, false);
        List invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceRows, "invoiceId", false);
        Debug.logInfo("==invoiceIdsList======Before==Cacnelation==="+invoiceIdsList,module);
        	//cancel mass invoice
        	Map<String, Object> cancelInvoiceInput = UtilMisc.<String, Object>toMap("invoiceIds",invoiceIdsList);
     	    cancelInvoiceInput.put("userLogin", userLogin);
            cancelInvoiceInput.put("statusId", "INVOICE_CANCELLED");
         	Map<String, Object> invoiceResult = dispatcher.runSync("massChangeInvoiceStatus",cancelInvoiceInput);
         	if (ServiceUtil.isError(invoiceResult)) {
         		Debug.logError("There was an error while cancel Invoice: " + ServiceUtil.getErrorMessage(invoiceResult), module);
         		  try{
      	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
      	        }catch (GenericEntityException e) {
      	    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
      	    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
      			}     
         		periodBilling.set("statusId", "CANCEL_FAILED");
		    	try{
		    		periodBilling.store();    		
		    	}catch (Exception e) {
		    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
		    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
				}
		    	return ServiceUtil.returnError("There was an error while cancel Invoice:");
             }	
         	
        	
        	if (cancelationFailed) {
				periodBilling.set("statusId", "CANCEL_FAILED");
			} else {
				periodBilling.set("statusId", "COM_CANCELLED");
				periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			}
			periodBilling.store();	
        
        }catch(GenericServiceException e){
	        	Debug.logError("Unable to Cancel Purchase billint"+e, module);
	    		return ServiceUtil.returnError("Unable to Cancel Purchase Billing"); 
	    }catch(GenericEntityException e){
        	Debug.logError("Unable to Cancel Purchase Billing"+e, module);
    		return ServiceUtil.returnError("Unable to Cancel Purchase Billing "); 
        }
        return result;
    }
    /** 
     * Services for Milk Sale Billing 
     * 
     */
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> populateMilkSaleBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map resultMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	String customTimePeriodId = (String)context.get("customTimePeriodId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String partyId = (String)context.get("partyIdTo");
    	GenericValue customTimePeriod = null;
    	try{
    		customTimePeriod =delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
        	if(UtilValidate.isEmpty(customTimePeriod)){
        		Debug.logError( "There no active billing time periods. ", module);				 
        		return ServiceUtil.returnError("There no active billing  periods ,Please contact administrator.");
        	}
    	}catch(GenericEntityException e){
    		Debug.logError("Error while getting customTimePeriod Details ::"+e,module);
    		return ServiceUtil.returnError("Error while getting customTimePeriod Details ::"+e.getMessage());
    	}
		
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
        Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
        Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime);
        Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime);
		List MilkTransfersList = FastList.newInstance();
		//Here we are getting shifts for MILK_SHIFT
		try{
			Map inMap = FastMap.newInstance();
			inMap.put("userLogin", userLogin);
			inMap.put("shiftType", "MILK_SHIFT");
			inMap.put("fromDate", monthBegin);
			inMap.put("thruDate", monthEnd);
			Map workShifts = getShiftDaysByType(dctx,inMap );
			if(ServiceUtil.isError(workShifts)){
				Debug.logError("Error while getting shift times :"+workShifts, module);
				return ServiceUtil.returnError("Error while getting shift times :"+ServiceUtil.getErrorMessage(workShifts));
			}
			fromDateTime =(Timestamp)workShifts.get("fromDate");
			thruDateTime =(Timestamp)workShifts.get("thruDate");
		}catch(Exception e){
			Debug.logError("Error while getting fromdate and thruDate ::"+e,module);
			return ServiceUtil.returnError("Error while getting fromdate and thruDate ::"+e.getMessage());
		}
		// here we are getting MilkReceipts 
		List milkTrnasferCondList = FastList.newInstance();
		milkTrnasferCondList.add(EntityCondition.makeCondition("purposeTypeId",EntityOperator.EQUALS,"OUTGOING"));
		milkTrnasferCondList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,"MD"));
		milkTrnasferCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDateTime));
		milkTrnasferCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDateTime));
		//
		milkTrnasferCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
		if(UtilValidate.isNotEmpty(partyId)){
			milkTrnasferCondList.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyId));
		}
		EntityCondition milkTranCondition = EntityCondition.makeCondition(milkTrnasferCondList);
		List<GenericValue> milkTransferList = FastList.newInstance();
		try{
			milkTransferList = delegator.findList("MilkTransfer", milkTranCondition, null, null, null, false);
		}catch(Exception e){
			Debug.logError("Error while getting MilkTransfers for the period ::"+customTimePeriodId+"  error:"+e,module);
			return ServiceUtil.returnError("Error while getting MilkTransfers for the period ::"+customTimePeriodId+"  error:"+e.getMessage());
		}
		if(UtilValidate.isEmpty(milkTransferList)){
			Debug.logError("No Records found for Billing ::",module);
			return ServiceUtil.returnError("No Records found for Billing ::");
		}
    	// Here we are tryingTo raise Invoice 
		Map inMap = FastMap.newInstance();
		inMap.put("userLogin", userLogin);
		if(UtilValidate.isNotEmpty(partyId)){
			inMap.put("partyId", partyId);
		}
		inMap.put("customTimePeriodId", customTimePeriodId);
		inMap.put("milkTransferList",milkTransferList);
		inMap.put("fromDateTime",fromDateTime);
		inMap.put("thruDateTime",thruDateTime);
		inMap.put("monthBegin",monthBegin);
		inMap.put("monthEnd",monthEnd);
		inMap.put("statusId","generate");
		String statusId= (String)context.get("statusId");
		if(UtilValidate.isNotEmpty(statusId)){
			inMap.put("statusId",statusId);
		}
		
    	Map saleBillingResult = populateSaleBillingForParty(dctx, inMap);
		if(ServiceUtil.isError(saleBillingResult)){
			Debug.logError("Error while populating PeridBilling :"+saleBillingResult,module);
			return ServiceUtil.returnError("Error while populating PeridBilling :"+ServiceUtil.getErrorMessage(saleBillingResult));
		}
		resultMap.putAll(saleBillingResult);
    	return resultMap;
    }//End of the service
    
    
    
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> populateSaleBillingForParty(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map resultMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	List<GenericValue> milkTransferList = (List) context.get("milkTransferList");
    	Timestamp fromDateTime = (Timestamp) context.get("fromDateTime");
    	Timestamp thruDateTime = (Timestamp) context.get("thruDateTime");
    	Timestamp monthBegin = (Timestamp) context.get("monthBegin");
    	Timestamp monthEnd = (Timestamp) context.get("monthEnd");
    	List<String> partyIdsList = FastList.newInstance();
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	String periodBillingId = null;
    	String statusId = (String)context.get("statusId");
    	if(UtilValidate.isEmpty(milkTransferList)){
    		Debug.logError("MilkTransfers not Found ::",module);
    		return ServiceUtil.returnError("MilkTransfers not Found ::");
    	}
    	HashSet<String> partyIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(milkTransferList, "partyIdTo", true));
    	if(UtilValidate.isNotEmpty(partyIdsSet)){
    		//Here we are checking for PeriodBilling If it is not existed
			List conditionList = FastList.newInstance();
	        List periodBillingList = FastList.newInstance();
	        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , UtilMisc.toList("GENERATED","IN_PROCESS","APPROVED","APPROVED_PAYMENT")));
	        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,customTimePeriodId));
	    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PB_SALE_MRGN"));
	    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    	GenericValue billingId =null;
	    	GenericValue newEntity = null;
	    	try {
	    		periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
	    		if(UtilValidate.isNotEmpty(statusId) && !statusId.equalsIgnoreCase("generate")){
	    			newEntity = EntityUtil.getFirst(periodBillingList);
	    			periodBillingId = (String) newEntity.get("periodBillingId");
	    		}
	    		
	    		if(UtilValidate.isNotEmpty(periodBillingList) && UtilValidate.isEmpty(periodBillingId)){
	    			// need to handle.
	    			Debug.logError("This billing is already  Generated or IN-Process",module);
	    			return ServiceUtil.returnError("This billing is already  Generated or IN-Process");
	    			
	    		}
	    		if(UtilValidate.isEmpty(periodBillingId)){
		    		newEntity = delegator.makeValue("PeriodBilling");
		            newEntity.set("billingTypeId", "PB_SALE_MRGN");
		            newEntity.set("customTimePeriodId", customTimePeriodId);
		            newEntity.set("statusId", "IN_PROCESS");
		            newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		            newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		            newEntity.set("createdDate", UtilDateTime.nowTimestamp());
		            newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());  
					delegator.createSetNextSeqId(newEntity);
					periodBillingId = (String) newEntity.get("periodBillingId");
	    		}
	    	}catch (GenericEntityException e) {
	    		 Debug.logError(e, module);             
	             return ServiceUtil.returnError("Failed to find periodBillingList " + e);
			}	
    		
    		try{
		    	Map partyProductWiseMap = FastMap.newInstance();
	    		for(String partyId : partyIdsSet){
		    		Map productWiseAmtMap = FastMap.newInstance();
		    		List<GenericValue> partyTransfersList = EntityUtil.filterByCondition(milkTransferList, EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyId));
		    		if(UtilValidate.isNotEmpty(partyTransfersList)){
		    	    	HashSet<String> productIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(partyTransfersList, "productId", true));
		    	    	
		    	    	if(UtilValidate.isNotEmpty(productIdsSet)){
		    	    		for(String productId:productIdsSet){
		    	    			Map priceCtx = FastMap.newInstance();
		    	    			priceCtx.put("userLogin",userLogin);
		    	    			priceCtx.put("productId",productId);
		    	    			List<GenericValue> productTransfers = FastList.newInstance();
		    	    			productTransfers = EntityUtil.filterByCondition(partyTransfersList,EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		    	    			if(UtilValidate.isNotEmpty(productTransfers)){
		    	    				for(GenericValue productTransfer : productTransfers){
		    	    					// Here we are trying to create invoices for Purchase Billing
		    	    					BigDecimal price = BigDecimal.ZERO;
		    	    					BigDecimal premiumAmt = BigDecimal.ZERO;
		    	    					
		    	    					BigDecimal sendQtyKgs = (BigDecimal)productTransfer.get("quantity");
		    	    					BigDecimal sendQtyLtrs = (BigDecimal)productTransfer.get("quantity");
		    	    					BigDecimal sendFat = (BigDecimal)productTransfer.get("fat");
		    	    					BigDecimal sendSnf = (BigDecimal)productTransfer.get("snf");
		    	    					
		    	    					BigDecimal sendKgFat = (BigDecimal)productTransfer.get("sendKgFat");
		    	    					BigDecimal sendKgSnf = (BigDecimal)productTransfer.get("sendKgSnf");
		    	    					
		    	    					
		    	    					BigDecimal recdQtyKgs = (BigDecimal)productTransfer.get("receivedQuantity");
		    	    					BigDecimal recdQtyLtrs = (BigDecimal)productTransfer.get("receivedQuantityLtrs");
		    	    					BigDecimal recdFat = (BigDecimal)productTransfer.get("receivedFat");
		    	    					BigDecimal recdSnf = (BigDecimal)productTransfer.get("receivedSnf");
		    	    					BigDecimal recdKgFat = (BigDecimal)productTransfer.get("receivedKgFat");
		    	    					BigDecimal recdKgSnf = (BigDecimal)productTransfer.get("receivedKgSnf");
		    	    					
		    	    					Timestamp priceDate = (Timestamp)productTransfer.get("receiveDate");
		    	    					Map priceResultMap = FastMap.newInstance();
		    	    					
		    	    					// here we may need changes in future.
		    	    					priceCtx.put("partyId","MD");
		    	    					priceCtx.put("priceDate",priceDate);
		    	    					priceCtx.put("fatPercent", sendFat);
		    	    					priceCtx.put("snfPercent", sendSnf);
		    	    					Map priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
		    	    					if(ServiceUtil.isError(priceResult)){
		    	    						Debug.logError("Error while getting priceChartDetails"+" ::For party :"+partyId+",productId ::"+productId+"  Error Message::"+priceResult, module);
		    	    						return ServiceUtil.returnError("Error while getting priceChartDetails"+" ::For party :"+partyId+",productId ::"+productId+"  Error Message::"+ServiceUtil.getErrorMessage(priceResult));
		    	    					}else{
		    	    						BigDecimal defaultRate = BigDecimal.ZERO;
		    	    						String uomId = "VLIQ_KG";
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("defaultRate"))){
		    	    							defaultRate = (BigDecimal)priceResult.get("defaultRate");
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("price"))){
		    	    							price = (BigDecimal)priceResult.get("price");
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("uomId"))){
		    	    							uomId = (String)priceResult.get("uomId");
		    	    						}
		    	    						BigDecimal fatPremiumRate = BigDecimal.ZERO;
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("fatPremium"))){
		    	    							fatPremiumRate = (BigDecimal)priceResult.get("fatPremium");
		    	    						}
		    	    						BigDecimal snfPremiumRate = BigDecimal.ZERO;
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("snfPremium"))){
		    	    							snfPremiumRate = (BigDecimal)priceResult.get("snfPremium");
		    	    						}
		    	    						if(price.compareTo(BigDecimal.ZERO)==0){
		    	    							fatPremiumRate = BigDecimal.ZERO;
		    	    							snfPremiumRate = BigDecimal.ZERO;
		    	    							defaultRate    = BigDecimal.ZERO;
		    	    						}
		    	    						
		    	    						String billQuantity = "DISP_QTY";
		    	    						// Here we are taking Dispatch Quality ,Because KMF Billing is done with dispatch QLTY and dispatched QTY
		    	    						String billQuality = "DISP_QLTY";
		    	    						//DISP_QTY
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("billQuantity"))){
		    	    							billQuantity = (String)priceResult.get("billQuantity");
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(priceResult.get("billQuality"))){
		    	    							billQuality = (String)priceResult.get("billQuality");
		    	    						}
		    	    						// here we are checking billQuality 
		    	    						if(UtilValidate.isNotEmpty(billQuality) && (!billQuality.equalsIgnoreCase("DISP_QLTY"))){
		    	    							// If bill Quality is Not Dispatched then we have to get the Ack Qlty Rate
		    	    							priceCtx.put("fatPercent", recdFat);
		    	    	    					priceCtx.put("snfPercent", recdSnf);
		    	    	    					priceResult = PriceServices.getProcurementProductPrice(dctx,priceCtx);
		    	    	    					
		    	    	    					if(ServiceUtil.isSuccess(priceResult)){
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("defaultRate"))){
		    	    	    							defaultRate = (BigDecimal)priceResult.get("defaultRate");
		    	    	    						}
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("fatPremium"))){
		    	    	    							fatPremiumRate = (BigDecimal)priceResult.get("fatPremium");
		    	    	    						}
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("snfPremium"))){
		    	    	    							snfPremiumRate = (BigDecimal)priceResult.get("snfPremium");
		    	    	    						}
		    	    	    						if(UtilValidate.isNotEmpty(priceResult.get("price"))){
				    	    							price = (BigDecimal)priceResult.get("price");
				    	    						}
		    	    	    						if(price.compareTo(BigDecimal.ZERO)==0){
				    	    							fatPremiumRate = BigDecimal.ZERO;
				    	    							snfPremiumRate = BigDecimal.ZERO;
				    	    							defaultRate    = BigDecimal.ZERO;
				    	    						}
		    	    	    					}
		    	    						}
		    	    						// Here we are taking billing qty as per recieved. Based on uomId we need to handle it
		    	    						BigDecimal billingQuantity = recdQtyKgs;	
		    	    						if(UtilValidate.isNotEmpty(billQuantity) && (!billQuantity.equalsIgnoreCase("ACK_QTY"))){
		    	    							billingQuantity = sendQtyKgs;
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(uomId) && (uomId.equalsIgnoreCase("VLIQ_L"))){
		    	    							if(billQuantity.equalsIgnoreCase("DISP_QTY")){
		    	    								billingQuantity = sendQtyLtrs;
		    	    							}else{
		    	    								billingQuantity = recdQtyLtrs;
		    	    							}
		    	    							
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(uomId) && (uomId.equalsIgnoreCase("VLIQ_KGFAT"))){
		    	    							if(billQuantity.equalsIgnoreCase("DISP_QTY")){
		    	    								billingQuantity = sendKgFat;
		    	    							}else{
		    	    								billingQuantity = recdKgFat;
		    	    							}
		    	    							
		    	    						}
		    	    						if(UtilValidate.isNotEmpty(uomId) && (uomId.equalsIgnoreCase("VLIQ_TS"))){
		    	    							if(billQuantity.equalsIgnoreCase("DISP_TS")){
		    	    								billingQuantity = sendKgFat.add(sendKgSnf);
		    	    							}else{
		    	    								billingQuantity = recdKgFat.add(recdKgSnf);
		    	    							}
		    	    						}
		    	    						// price Rate  is addition of defaultRate+fatPremium+snfPremium
		    	    						BigDecimal priceRate = defaultRate.add(fatPremiumRate).add(snfPremiumRate);
		    	    						BigDecimal totUnitAmt = billingQuantity.multiply(defaultRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						BigDecimal fatPremAmt = billingQuantity.multiply(fatPremiumRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						BigDecimal snfPremAmt = billingQuantity.multiply(snfPremiumRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						BigDecimal totAmt = billingQuantity.multiply(priceRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	    						
		    	    						Map billingDetailsMap = FastMap.newInstance();
		    	    						if(UtilValidate.isNotEmpty(billingQuantity) && billingQuantity.compareTo(BigDecimal.ZERO)==1 && UtilValidate.isNotEmpty(totAmt) && totAmt.compareTo(BigDecimal.ZERO)==1){
		    	    							billingDetailsMap.put("amount",totAmt);
		    	    							billingDetailsMap.put("quantity",billingQuantity);
		    	    						}
		    	    						if(UtilValidate.isEmpty(productWiseAmtMap) || UtilValidate.isEmpty(productWiseAmtMap.get(productId))){
		    	    							productWiseAmtMap.put(productId,billingDetailsMap);
		    	    						}else{
		    	    							Map tempMap = FastMap.newInstance();
		    	    							tempMap.putAll((Map)productWiseAmtMap.get(productId));
		    	    							for(Object billingDetKey : billingDetailsMap.keySet()){
		    	    								String billngDetKeyStr = billingDetKey.toString();
		    	    								tempMap.put(billingDetKey, ((BigDecimal)tempMap.get(billingDetKey)).add(((BigDecimal)billingDetailsMap.get(billingDetKey))));
		    	    							}
		    	    							productWiseAmtMap.put(productId, tempMap);
		    	    						}
		    	    						// Here we are trying to store fatPremium,snfPremium , price value in MilkTransfertransfer
		    	    						try{
		    	    							productTransfer.set("unitPrice", defaultRate);
		    	    							productTransfer.set("fatPremium", fatPremiumRate);
		    	    							productTransfer.set("snfPremium", snfPremiumRate);
		    	    							productTransfer.set("billQuantity", billQuantity);
		    	    							productTransfer.set("billQuality", billQuality);
		    	    							productTransfer.store();
		    	    						}catch(Exception e){
		    	    							Debug.logError("Error while storing prices in Milk Transfer :"+e,module);
		    	    							return ServiceUtil.returnError("Error while storing prices in Milk Transfer :"+e);
		    	    						}
		    	    					}
		    	    				}
		    	    			}
		    	    		}
		    	    	}
		    		}
		    		if(UtilValidate.isNotEmpty(productWiseAmtMap)){
		    			partyProductWiseMap.put(partyId, productWiseAmtMap);
		    		}
		    		// Here we are trying to create invoice for each party with product wise items
		    		Map inVoiceInMap = FastMap.newInstance();
					inVoiceInMap.put("userLogin", userLogin);
					inVoiceInMap.put("invoiceTypeId", "MILK_SALES_INVOICE");
					inVoiceInMap.put("productWiseAmtMap", productWiseAmtMap);
					inVoiceInMap.put("partyId",partyId);
		    		inVoiceInMap.put("periodBillingId", periodBillingId);
		    		inVoiceInMap.put("fromDateTime", monthBegin);
		    		inVoiceInMap.put("thruDateTime", monthEnd);
		    		inVoiceInMap.put("description", "MILK SALE FROM "+UtilDateTime.toDateString(monthBegin,"dd-MMM-yyyy")+" to "+UtilDateTime.toDateString(monthEnd,"dd-MMM-yyyy"));
		    		if(UtilValidate.isNotEmpty(statusId) && !statusId.equalsIgnoreCase("generate")){
						Map invoiceResultMap = createSaleInvoiceForParty(dctx,inVoiceInMap);
			    		if(ServiceUtil.isError(invoiceResultMap)){
			    			Debug.logError("Error while creating invoice for party :"+partyId+" errorMessage :"+invoiceResultMap, module);
			    			return ServiceUtil.returnError("Error while creating invoice for party :"+partyId+" errorMessage :"+ServiceUtil.getErrorMessage(invoiceResultMap));
			    		}
		    		}
		    	}
	    	}catch(Exception e){
	    		Debug.logError("Error while populating purchase billing "+e, module);
	    		return ServiceUtil.returnError("Error while populating purchase billing "+e.getMessage());
	    	}
    		// here we are updating PeriodBilling status to generated
    		try{
    			GenericValue periodDetails = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId",periodBillingId), false);
    			if(UtilValidate.isNotEmpty(periodDetails)){
    				periodDetails.set("statusId","GENERATED");
    				periodDetails.store();
    			}
    		}catch(Exception e){
    			Debug.logError("Error while updating status of Period Billing"+e,module);
    			return ServiceUtil.returnError("Error while updating status of Period Billing"+e.getMessage());
    		}
    	}
    	resultMap = ServiceUtil.returnSuccess("Billing Successfully Generated :"+periodBillingId);
    	resultMap.put("periodBillingId",periodBillingId);
    	return resultMap;
    }// end of the service
    
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createSaleInvoiceForParty(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map resultMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	String periodBillingId = (String)context.get("periodBillingId");
    	Timestamp thruDateTime = (Timestamp)context.get("thruDateTime");
    	Timestamp fromDateTime = (Timestamp)context.get("fromDateTime");
    	String invoiceTypeId   = (String) context.get("invoiceTypeId");
    	
    	Map productWiseAmtMap = (Map)context.get("productWiseAmtMap"); 
    	if(UtilValidate.isEmpty(productWiseAmtMap)){
    		Debug.logError("product Wise Map is not found",module);
    		return ServiceUtil.returnError("product Wise Map is not found");
    	}
    	String partyIdTo = (String) context.get("partyId");
    	Map<String, Object> createInvoiceContext = FastMap.newInstance();
        createInvoiceContext.put("partyId", partyIdTo);
        createInvoiceContext.put("partyIdFrom","Company");
        createInvoiceContext.put("dueDate", thruDateTime);
        createInvoiceContext.put("invoiceDate",thruDateTime);
        createInvoiceContext.put("invoiceTypeId", invoiceTypeId);
        createInvoiceContext.put("referenceNumber", "MILK_SALE_"+periodBillingId);
        createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
        createInvoiceContext.put("currencyUomId", "INR");
        createInvoiceContext.put("description", (String)context.get("description"));
        createInvoiceContext.put("userLogin", userLogin);

        // store the invoice first
        Map<String, Object> createInvoiceResult = FastMap.newInstance();
        try{
	        createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
	        if (ServiceUtil.isError(createInvoiceResult)) {
	    		//generationFailed = true;
                Debug.logWarning("There was an error while creating  Invoice For TransporterCommission: " + ServiceUtil.getErrorMessage(createInvoiceResult), module);
        		return ServiceUtil.returnError("There was an error while creating Invoice for  TransporterCommission: " + ServiceUtil.getErrorMessage(createInvoiceResult));          	            
            }
        }catch(Exception e){
        	Debug.logError("Error while creating invoice for partyId:"+partyIdTo+" Message:"+e,module);
    		return ServiceUtil.returnError("Error while creating invoice for partyId:"+partyIdTo+" Message:"+e.getMessage());
        }
        // call service for creation invoice);
        String invoiceId = (String) createInvoiceResult.get("invoiceId");
    	// here we are creating Invoice Items
        Map invItemInMap = FastMap.newInstance();
        invItemInMap.put("userLogin",userLogin);
        invItemInMap.put("invoiceId",invoiceId);
        invItemInMap.put("invoiceItemTypeId","SALE_MILK");
        for(Object productKey : productWiseAmtMap.keySet()){
        	String productKeyStr = productKey.toString();
        	Map tempProdMap = FastMap.newInstance();
        	tempProdMap.putAll((Map)productWiseAmtMap.get(productKey));
        	//BigDecimal quantity = (BigDecimal)tempProdMap.get("quantity");
        	BigDecimal quantity = BigDecimal.ONE;
        	BigDecimal amount = (BigDecimal)tempProdMap.get("amount");
        	/*BigDecimal unitPrice = BigDecimal.ZERO;
        	if(UtilValidate.isNotEmpty(quantity) && UtilValidate.isNotEmpty(amount) && quantity.compareTo(BigDecimal.ZERO)==1){
        		unitPrice = amount.divide(quantity, 8,BigDecimal.ROUND_HALF_UP);
        	}*/
        	invItemInMap.put("amount",amount);
        	invItemInMap.put("quantity",quantity);
        	invItemInMap.put("productId",productKeyStr);
        	invItemInMap.put("description","Bill Qty(Kgs) : "+(BigDecimal)tempProdMap.get("quantity"));
        	try{
        		resultMap =  dispatcher.runSync("createInvoiceItem",invItemInMap);
        	}catch(Exception e){
        		Debug.logError("Error while creating invoiceItem for Product :"+productKeyStr+",partyId:"+partyIdTo+" Message:"+e,module);
        		resultMap = ServiceUtil.returnError("Error while creating invoiceItem for Product :"+productKeyStr+",partyId:"+partyIdTo+" Message:"+e.getMessage());
        	}
        }
        return resultMap;
    }// End of the service
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateSaleBillingStatus (DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	TimeZone timeZone = TimeZone.getDefault();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = new HashMap<String, Object>();
        result = ServiceUtil.returnSuccess();
        String periodBillingId = (String) context.get("periodBillingId");
        String statusId = (String) context.get("statusId");
        if(UtilValidate.isEmpty(statusId)){
        	statusId="GENERATED";
        }
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        	  periodBilling.set("statusId", statusId);
			  periodBilling.store();
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}   
        GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1,"Error While Finding Customtime Period");
			return ServiceUtil.returnError("Error While Finding Customtime Period" + e1);
		}
		if("APPROVED".equalsIgnoreCase(statusId)){
			//populateMilkSaleBilling
			Map populateSaleBillingInMap = FastMap.newInstance();
			populateSaleBillingInMap.put("statusId", "approved");
			populateSaleBillingInMap.put("userLogin", userLogin);
			populateSaleBillingInMap.put("customTimePeriodId", customTimePeriodId);
			
			Map  populateSalePeriodBillingResult = populateMilkSaleBilling(dctx,populateSaleBillingInMap);
			if(ServiceUtil.isError(populateSalePeriodBillingResult)){
				Debug.logError("Error while creating invoices :"+populateSalePeriodBillingResult,module);
				return ServiceUtil.returnError("Error while creating invoices :"+ServiceUtil.getErrorMessage(populateSalePeriodBillingResult));
			}
			Map updateSaleInvoiceBilling = updateSaleBillingInvoices(dctx,UtilMisc.toMap("periodBillingId",periodBillingId ,"userLogin",userLogin,"statusId","INVOICE_READY"));
			if(ServiceUtil.isError(updateSaleInvoiceBilling)){
				Debug.logError("Error while processing invoices :"+updateSaleInvoiceBilling,module);
				return ServiceUtil.returnError("Error while processing invoices :"+ServiceUtil.getErrorMessage(updateSaleInvoiceBilling));
			}
			try{
				periodBilling.set("statusId","APPROVED");
				delegator.store(periodBilling);
			}catch(GenericEntityException e){
				Debug.logError("Error while approving the billing ::"+e,module);
				return ServiceUtil.returnError("Error while approving the billing ::"+e.getMessage());
			}
			
			result = ServiceUtil.returnSuccess("Billing successfully Approved for "+periodBillingId);
			
		}
		
		return result;
    }
    
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateSaleBillingInvoices(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	TimeZone timeZone = TimeZone.getDefault();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = new HashMap<String, Object>();
        result = ServiceUtil.returnSuccess();
        String periodBillingId = (String) context.get("periodBillingId");
        List<GenericValue> invoicesList = FastList.newInstance();
        String referenceNumber = "MILK_SALE_"+periodBillingId;
        String statusId = (String) context.get("statusId");
        try{
        	List conditionList = FastList.newInstance();
        	conditionList.add(EntityCondition.makeCondition("referenceNumber",EntityOperator.EQUALS,referenceNumber));
        	if(statusId.equalsIgnoreCase("INVOICE_READY")){
        		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_IN_PROCESS"));
        	}
        	if(statusId.equalsIgnoreCase("INVOICE_PAID")){
        		conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"INVOICE_READY"));
        	}
        	EntityCondition condition = EntityCondition.makeCondition(conditionList);
        	invoicesList = delegator.findList("Invoice", condition, null, null, null, false);
        	if(UtilValidate.isEmpty(invoicesList)){
        		Debug.logError("Invoices not found for the billing period :"+periodBillingId,module);
        		return ServiceUtil.returnError("Invoices not found for the billing period :"+periodBillingId);
        	}
        }catch(Exception e){
        	Debug.logError("Error while getting invoices "+e,module);
        	return ServiceUtil.returnError("Error while getting invoices "+e.getMessage());
        }
        HashSet<String> invoiceIdsSet= new HashSet( EntityUtil.getFieldListFromEntityList(invoicesList, "invoiceId", true));
        for(String invoiceId : invoiceIdsSet){
	        Map invoiceCtx = FastMap.newInstance();
	      //set to Ready for Posting
	        invoiceCtx.put("userLogin", userLogin);
	        invoiceCtx.put("invoiceId", invoiceId);
	        invoiceCtx.put("statusId",statusId);
	        try{
	        	Map<String, Object> invoiceResult = dispatcher.runSync("setInvoiceStatus",invoiceCtx);
	        	if (ServiceUtil.isError(invoiceResult)) {
	        		Debug.logError(invoiceResult.toString(), module);
	                return ServiceUtil.returnError(null, null, null, invoiceResult);
	            }	             	
	        }catch(GenericServiceException e){
	        	 Debug.logError(e, e.toString(), module);
	            return ServiceUtil.returnError(e.toString());
	        } 
        }
    	return result;
    }//end of the method
    
    
    
    public static Map<String, Object> cancelSaleBilling(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
        //Map<String, Object> result = ServiceUtil.returnSuccess();
        
        Map<String, Object> result = new HashMap<String, Object>();
        
        String periodBillingId = (String) context.get("periodBillingId");
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}        
    	periodBilling.set("statusId", "CANCEL_INPROCESS");
        boolean cancelationFailed = false;	
    	try{
    		periodBilling.store();    		
    	}catch (Exception e) {
    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
		}
    	//first Cancel  invoices
    	try{
    		Map<String,  Object> inputContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"userLogin", userLogin);
			Map cancelPtcTranInvoiceResult = dispatcher.runSync("cancelSaleBillingInvoice", inputContext);
			if(ServiceUtil.isError(cancelPtcTranInvoiceResult)){
				Debug.logError("Error whiel cancelling PTC Transporter Invoice",module);
				return ServiceUtil.returnError("Error whiel cancelling PTC Transporter Invoice");
			}
			
    	} catch (GenericServiceException e) {
            Debug.logError(e, "Error in canceling 'transporterMargin' service", module);
            return ServiceUtil.returnError(e.getMessage());
        } 
    	result = ServiceUtil.returnSuccess("Successfully cancelled");
        return result;
    }//end of the service
    /**
     * 
     * @param dctx
     * @param context
     * @return
     */
    
    public static Map<String, Object> cancelSaleBillingInvoice(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();       
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = new HashMap<String, Object>();
        Locale locale = (Locale) context.get("locale");
        boolean cancelationFailed = false;	
        String periodBillingId = (String) context.get("periodBillingId");
        GenericValue periodBilling = null;
        String customTimePeriodId="";
        try{
        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
        	 customTimePeriodId =  periodBilling.getString("customTimePeriodId");
        }catch (GenericEntityException e) {
    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
		}        
    	GenericValue customTimePeriod;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, e1.getMessage());
			return ServiceUtil.returnError("Error in customTimePeriod" + e1);
		}
		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
		
		Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, TimeZone.getDefault(), locale);
		Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, TimeZone.getDefault(), locale);
		
		if(customTimePeriod == null){
			cancelationFailed = true;
		}
		
	try{
		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
		  conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS,  "MILK_SALE_"+periodBillingId));
		  
	     conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "MILK_SALES_INVOICE"));
	     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
	     conditionList.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
         conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED")); 
        
    	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
    	List<GenericValue> invoiceRows = delegator.findList("Invoice", condition, null, null, null, false);
        List invoiceIdsList = EntityUtil.getFieldListFromEntityList(invoiceRows, "invoiceId", false);
        Debug.logInfo("==invoiceIdsList======Before==Cacnelation==="+invoiceIdsList,module);
        	//cancel mass invoice
        	Map<String, Object> cancelInvoiceInput = UtilMisc.<String, Object>toMap("invoiceIds",invoiceIdsList);
     	    cancelInvoiceInput.put("userLogin", userLogin);
            cancelInvoiceInput.put("statusId", "INVOICE_CANCELLED");
         	Map<String, Object> invoiceResult = dispatcher.runSync("massChangeInvoiceStatus",cancelInvoiceInput);
         	if (ServiceUtil.isError(invoiceResult)) {
         		Debug.logError("There was an error while cancel Invoice: " + ServiceUtil.getErrorMessage(invoiceResult), module);
         		  try{
      	        	periodBilling = delegator.findOne("PeriodBilling",UtilMisc.toMap("periodBillingId", periodBillingId), false);
      	        }catch (GenericEntityException e) {
      	    		Debug.logError("Unable to get PeriodBilling record from DataBase"+e, module);
      	    		return ServiceUtil.returnError("Unable to get PeriodBilling record from DataBase "); 
      			}     
         		periodBilling.set("statusId", "CANCEL_FAILED");
		    	try{
		    		periodBilling.store();    		
		    	}catch (Exception e) {
		    		Debug.logError("Unable to Store PeriodBilling Status"+e, module);
		    		return ServiceUtil.returnError("Unable to Store PeriodBilling Status"); 
				}
		    	return ServiceUtil.returnError("There was an error while cancel Invoice:");
             }	
         	
        	
        	if (cancelationFailed) {
				periodBilling.set("statusId", "CANCEL_FAILED");
			} else {
				periodBilling.set("statusId", "COM_CANCELLED");
				periodBilling.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			}
			periodBilling.store();	
        
        }catch(GenericServiceException e){
	        	Debug.logError("Unable to Cancel Sale billing"+e, module);
	    		return ServiceUtil.returnError("Unable to Cancel Purchase Billing"); 
	    }catch(GenericEntityException e){
        	Debug.logError("Unable to Cancel Purchase Billing"+e, module);
    		return ServiceUtil.returnError("Unable to Cancel Purchase Billing "); 
        }
        return result;
    }
    
    
    
    /**
     * End of sale Billing services
     */
}
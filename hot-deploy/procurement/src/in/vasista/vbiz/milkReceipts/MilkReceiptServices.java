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
import org.ofbiz.base.util.UtilNumber;
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
public class MilkReceiptServices {

	public static final String module = MilkReceiptServices.class.getName();
	public static final String resource = "CommonUiLabels";
	protected static final HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected static final FoScreenRenderer foScreenRenderer = new FoScreenRenderer();
   
    
    
    public static final Map<String, Object> createMilkReceiptEntryAjax(DispatchContext dctx, Map<String, ? extends Object> context) {
   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> resultMap = FastMap.newInstance();
        

        try{            	
        	resultMap = dispatcher.runSync("createNewMilkReceiptEntry",context);
    		if (ServiceUtil.isError(resultMap)) {
                Debug.logWarning("There was an error while creating   the MilkReceipt Entry: " + ServiceUtil.getErrorMessage(resultMap), module);
        		return ServiceUtil.returnError("There was an error while creating   the MilkReceipt Entry:  " + ServiceUtil.getErrorMessage(resultMap));          	            
            }else{
            	GenericValue milkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", (String)resultMap.get("milkTransferId")),false);
            	if(UtilValidate.isNotEmpty(milkTransfer)){
            		Timestamp receiveDate = (Timestamp) milkTransfer.get("receiveDate");
            		BigDecimal recdQtyLtrs = (BigDecimal) milkTransfer.get("receivedQuantityLtrs");
            		String facilityId = (String) milkTransfer.get("facilityId");
            		
            		GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", (String)milkTransfer.get("facilityId")),false);
            		String mccCode = (String) facility.get("mccCode");
            		Map orderItemMap = FastMap.newInstance();
            		List itemsConditionList = UtilMisc.toList(EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,(String)resultMap.get("milkTransferId")));
            		EntityCondition itemCondition = EntityCondition.makeCondition(itemsConditionList,EntityJoinOperator.AND);
            		List<GenericValue> milkTransferItems = delegator.findList("MilkTransferItem", itemCondition, null, null, null, false);
            		List cellQtyList = FastList.newInstance();
            		if(UtilValidate.isNotEmpty(milkTransferItems)){
    	        		for(GenericValue milkTransferItem : milkTransferItems){
    	        			Map cellQtyMap = FastMap.newInstance();
    	        			BigDecimal qty = BigDecimal.ZERO;
    	        			qty = (BigDecimal)milkTransferItem.get("receivedQuantityLtrs");
    	        			String cellType = null;
    	        			cellType = (String)milkTransferItem.get("cellType");
    	        			cellQtyMap.put(cellType, qty);
    	        			cellQtyList.add(cellQtyMap);
    	        		}
            		}
            		orderItemMap.put("cellQtyList",cellQtyList);
            		orderItemMap.put("mccCode",mccCode);
            		orderItemMap.put("tankerId",milkTransfer.get("containerId"));
            		orderItemMap.put("milkTransferId",milkTransfer.get("milkTransferId"));
            		orderItemMap.put("receiveDate",receiveDate);
            		orderItemMap.put("recdQtyLtrs",recdQtyLtrs);
            		orderItemMap.put("lastModifiedByUserLogin",(String)milkTransfer.get("lastModifiedByUserLogin"));
            		resultMap.put("orderItemMap", orderItemMap);
            	}
            	
            }       		
        }catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("There was an error while creating   the MilkReceipt Entry: " + e.toString());
        }		  
        
        
        return resultMap;
   }
    
    public static String milkReceiptEntryCreation(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        List<GenericValue> subscriptionList=FastList.newInstance();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String facilityId = (String) request.getParameter("facilityId");
        String facilityIdTo = (String) request.getParameter("facilityIdTo");       
        String fromDateStr = (String) request.getParameter("fromDate");
	    String productId = (String) request.getParameter("productId");
        String tankerNo = (String) request.getParameter("tankerNo");
        String capacity = (String) request.getParameter("capacity");
        String sendTime = (String) request.getParameter("sendTime");
        String soda = (String) request.getParameter("soda");
        String cob = (String) request.getParameter("cob");
        String milkCondition = (String) request.getParameter("milkCondition");       
        Map milkTransferInMap = FastMap.newInstance();
	    Timestamp fromDate =null;      
        if (UtilValidate.isNotEmpty(fromDateStr)) { //2011-12-25
        	SimpleDateFormat   sdf = new SimpleDateFormat("MMMM dd, yyyy");             
	  		  try {
	  			  fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + fromDateStr, module);
	              
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + fromDateStr, module);	               
	  		  }
	  	  } 
        if(UtilValidate.isEmpty(fromDate)){
      	   request.setAttribute("_ERROR_MESSAGE_","Send Date can not be empty");
  			return "error";
         }       
        if(UtilValidate.isEmpty(facilityId)){
     	   request.setAttribute("_ERROR_MESSAGE_","From facility can not be empty");
 			return "error";
        }
        if(UtilValidate.isEmpty(facilityIdTo)){
      	   request.setAttribute("_ERROR_MESSAGE_","To facility can not be empty");
  			return "error";
         }
       if(UtilValidate.isEmpty(fromDateStr)){
    	   request.setAttribute("_ERROR_MESSAGE_","send date can not be empty");
			return "error";
       }
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        List<GenericValue> milkTankerCellList=FastList.newInstance();
        List orderBy = UtilMisc.toList("sequenceId");
        try{
        	milkTankerCellList  = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "MILK_CELL_TYPE"), null,orderBy, null, false);
        }catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), module);
		}
        Map cellWiseDetailsMap=FastMap.newInstance();
        for(GenericValue tankerCell : milkTankerCellList){
        	Map cellEntriesMap=FastMap.newInstance();
        	if(UtilValidate.isNotEmpty(paramMap)){
        		cellEntriesMap.put("quantity", paramMap.get(tankerCell.get("enumId")+"_"+"quantity"));
        		cellEntriesMap.put("quantityLtrs", paramMap.get(tankerCell.get("enumId")+"_"+"quantityLtrs"));
        		cellEntriesMap.put("fat", paramMap.get(tankerCell.get("enumId")+"_"+"fat"));
        		cellEntriesMap.put("snf", paramMap.get(tankerCell.get("enumId")+"_"+"snf"));
        		cellEntriesMap.put("acid", paramMap.get(tankerCell.get("enumId")+"_"+"acid"));
        		cellEntriesMap.put("temp", paramMap.get(tankerCell.get("enumId")+"_"+"temp"));
        		cellEntriesMap.put("clr", paramMap.get(tankerCell.get("enumId")+"_"+"clr"));
        	}
        	cellWiseDetailsMap.put(tankerCell.get("enumId"), cellEntriesMap);        	
        }
        Map milkTransferResult = FastMap.newInstance();
        milkTransferInMap.put("userLogin", userLogin);
        milkTransferInMap.put("facilityId", facilityId);
        milkTransferInMap.put("facilityIdTo", facilityIdTo);
        milkTransferInMap.put("productId", productId);
        milkTransferInMap.put("sendDate", fromDate);
        milkTransferInMap.put("capacity", capacity);
        milkTransferInMap.put("cob", cob);
        milkTransferInMap.put("milkCondition", milkCondition);
        milkTransferInMap.put("vehicleId", tankerNo);
        milkTransferInMap.put("sendTime", sendTime);
        milkTransferInMap.put("soda", soda);
        milkTransferInMap.put("cellWiseDetailsMap", cellWiseDetailsMap);
        try{
        	milkTransferResult = dispatcher.runSync("createMilkReceiptEntry", milkTransferInMap);
        	if( ServiceUtil.isError(milkTransferResult)) {
				String errMsg =  ServiceUtil.getErrorMessage(milkTransferResult);
				Debug.logWarning(errMsg , module);
				request.setAttribute("_ERROR_MESSAGE_",errMsg);
				return "error";
			}
        }catch (GenericServiceException e) {
			// TODO: handle exception
        	Debug.logError(e,module);
        	
		}
      	request.setAttribute("_EVENT_MESSAGE_", "Transfer record Created Successfully  ");
      	request.setAttribute("milkTransferId", milkTransferResult.get("milkTransferId"));
      	 return "success";
    }   
    public static Map<String, Object> createMilkReceiptEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = FastMap.newInstance();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String productId = (String) context.get("productId");
        String facilityId = (String) context.get("facilityId");
        String facilityIdTo = (String) context.get("facilityIdTo");
        Timestamp sendDate = (Timestamp) context.get("sendDate"); 
        String capacity = (String) context.get("capacity");
        String sendTime = (String) context.get("sendTime");
        String soda = (String) context.get("soda");
        String cob = (String) context.get("cob");
        String milkCondition = (String) context.get("milkCondition");
        String vehicleId = (String)context.get("vehicleId");
        Map cellWiseDetailsMap= (Map)context.get("cellWiseDetailsMap");
        BigDecimal trnQtyLtrs = BigDecimal.ZERO;
        BigDecimal trnQtyKgs = BigDecimal.ZERO; 
        BigDecimal trnKgFat = BigDecimal.ZERO;
        BigDecimal trnKgSnf = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(cellWiseDetailsMap)){
        	Iterator cellDetailsItr = cellWiseDetailsMap.entrySet().iterator();
			while (cellDetailsItr.hasNext()){
				Map.Entry cellEntry = (Entry) cellDetailsItr.next();						
				Map cellDetails = (Map) cellEntry.getValue();				
				if(UtilValidate.isNotEmpty(cellDetails)){
					//Map cellDtlMap = (Map)(cellDetails.get("dayTotals"));
					String qtyLtrsStr= null;
					String qtyKgsStr= null;
					String fatStr= null;
					String snfStr= null;
					String lrStr= null;
					BigDecimal qtyLtrs= BigDecimal.ZERO;
					BigDecimal qtykgs= BigDecimal.ZERO;
					BigDecimal fat= BigDecimal.ZERO;
					BigDecimal snf= BigDecimal.ZERO;
					BigDecimal lr= BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(cellDetails.get("quantityLtrs"))){
						qtyLtrsStr= (String)cellDetails.get("quantityLtrs");
					 try {
						 qtyLtrs = new BigDecimal(qtyLtrsStr);
		                } catch (Exception e) {
		                    Debug.logWarning(e, "Problems parsing quantity string: " + qtyLtrsStr, module);
		                    
		                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("quantity"))){
						qtyKgsStr= (String)cellDetails.get("quantity");						
						try {
							qtykgs = new BigDecimal(qtyKgsStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + qtykgs, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("fat"))){
						fatStr= (String)cellDetails.get("fat");
						try {
							fat = new BigDecimal(fatStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + fatStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("snf"))){
						snfStr= (String)cellDetails.get("snf");
						try {
							snf = new BigDecimal(snfStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + snfStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("clr"))){
						lrStr= (String)cellDetails.get("clr");
						try {
							lr = new BigDecimal(lrStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + lrStr, module);
			                    
			                } 
					}
					trnQtyLtrs= trnQtyLtrs.add(qtyLtrs);
					if(qtyLtrs.compareTo(BigDecimal.ZERO) !=0){
						qtykgs=convertLitresToKGByLR(qtyLtrs,lr,true);
			        }
					trnQtyKgs =trnQtyKgs.add(qtykgs); 
				    trnKgFat =trnKgFat.add((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs,fat));
				    trnKgSnf = trnKgSnf.add((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs,snf));
				}
			}
        }
        if(trnQtyLtrs.compareTo(BigDecimal.ZERO)<=0){
        	resultMap = ServiceUtil.returnError("Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        } 
        if(trnQtyLtrs.compareTo(new BigDecimal(9500))>0){
	        capacity="Y";
        }else{
        	capacity="N";
        }
        String milkTransferId="";
        Map milkTransferInMap = FastMap.newInstance();
        milkTransferInMap.put("userLogin",userLogin);
        milkTransferInMap.put("facilityIdTo",facilityIdTo);
        milkTransferInMap.put("facilityId",facilityId);
        milkTransferInMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(trnKgFat, trnQtyKgs).setScale(1,BigDecimal.ROUND_HALF_UP));
        milkTransferInMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(trnKgSnf, trnQtyKgs).setScale(2,BigDecimal.ROUND_HALF_UP));
        milkTransferInMap.put("sendKgFat",trnKgFat);
        milkTransferInMap.put("sendKgSnf",trnKgSnf);
        milkTransferInMap.put("quantityLtrs",trnQtyLtrs);
        milkTransferInMap.put("quantity",trnQtyKgs);
        milkTransferInMap.put("containerId",vehicleId);
        milkTransferInMap.put("sendDate",sendDate);
        milkTransferInMap.put("productId",productId);
        milkTransferInMap.put("isMilkRcpt","Y");
        Map milkTransferResult = FastMap.newInstance();
        try{
        	milkTransferResult = dispatcher.runSync("createMilkTransferRecord", milkTransferInMap);
        }catch (GenericServiceException e) {
			// TODO: handle exception
        	Debug.logError(e,module);
        	return ServiceUtil.returnError("Error while creating MilkReceipts=========="+e.getMessage());
		}
        if(ServiceUtil.isError(milkTransferResult)){
        	Debug.logError("milkTransferResult==============="+milkTransferResult,module);
        	return ServiceUtil.returnError("Error while creating MilkReceipts   =========="+ServiceUtil.getErrorMessage(milkTransferResult));
        }
         milkTransferId = (String)milkTransferResult.get("milkTransferId");         
         List<GenericValue> milkTankerCellList=FastList.newInstance();
         List orderBy = UtilMisc.toList("sequenceId");
         try{
         	milkTankerCellList  = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "MILK_CELL_TYPE"), null,orderBy, null, false);
         }catch (GenericEntityException e) {
 			// TODO: handle exception
 			Debug.logError(e.getMessage(), module);
 		}
         if(UtilValidate.isNotEmpty(milkTransferId)){
	         for(GenericValue tankerCell : milkTankerCellList){
	        	Map milkTransferItemInMap = FastMap.newInstance();
	        	String qtyLtrStr= null;
				String qtyKgStr= null;
				String fatStr= null;
				String snfStr= null;
				String cLrStr= null;
				String acidStr= null;
				String tempStr= null;
				BigDecimal qtyLtrs= BigDecimal.ZERO;
				BigDecimal qtykgs= BigDecimal.ZERO;
				BigDecimal fat= BigDecimal.ZERO;
				BigDecimal snf= BigDecimal.ZERO;
				BigDecimal cLr= BigDecimal.ZERO;
				Long acid= Long.parseLong("0");				
				BigDecimal temp= BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(cellWiseDetailsMap.get(tankerCell.get("enumId")))){
					Map cellDetailsMap=(Map)cellWiseDetailsMap.get(tankerCell.get("enumId"));
					if(UtilValidate.isNotEmpty(cellDetailsMap.get("quantityLtrs"))){
						qtyLtrStr= (String)cellDetailsMap.get("quantityLtrs");
					 try {
						 qtyLtrs = new BigDecimal(qtyLtrStr);
		                } catch (Exception e) {
		                    Debug.logWarning(e, "Problems parsing quantity string: " + qtyLtrStr, module);
		                    
		                } 
					}
					if(UtilValidate.isNotEmpty(cellDetailsMap.get("quantity"))){
						qtyKgStr= (String)cellDetailsMap.get("quantity");						
						try {
							qtykgs = new BigDecimal(qtyKgStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + qtykgs, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetailsMap.get("fat"))){
						fatStr= (String)cellDetailsMap.get("fat");
						try {
								fat = new BigDecimal(fatStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + fatStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetailsMap.get("snf"))){
						snfStr= (String)cellDetailsMap.get("snf");
						try {
							snf = new BigDecimal(snfStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + snfStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetailsMap.get("clr"))){
						cLrStr= (String)cellDetailsMap.get("clr");
						try {
							cLr = new BigDecimal(cLrStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + cLrStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetailsMap.get("acid"))){
						acidStr= (String)cellDetailsMap.get("acid");						
						try {
							acid = Long.parseLong(acidStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + acidStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetailsMap.get("temp"))){
						tempStr= (String)cellDetailsMap.get("temp");
						try {
							temp = new BigDecimal(tempStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + tempStr, module);
			                    
			                } 
					}
					if(qtyLtrs.compareTo(BigDecimal.ZERO) !=0){
						qtykgs=convertLitresToKGByLR(qtyLtrs,cLr,true);
					}					
		         	milkTransferItemInMap.put("milkTransferId",milkTransferId);
		         	milkTransferItemInMap.put("userLogin", userLogin);
		         	milkTransferItemInMap.put("productId",productId);
		         	milkTransferItemInMap.put("vehicleId",vehicleId);
		         	milkTransferItemInMap.put("sendDate", sendDate);
		         	milkTransferItemInMap.put("capacity",capacity);
		         	milkTransferItemInMap.put("cob",cob);
		         	milkTransferItemInMap.put("milkCondition",milkCondition);
		         	milkTransferItemInMap.put("quantityLtrs",qtyLtrs);
		         	milkTransferItemInMap.put("quantity",qtykgs);
		         	milkTransferItemInMap.put("fat",fat);
		         	milkTransferItemInMap.put("snf",snf);
		         	milkTransferItemInMap.put("sendLR",cLr);
		         	milkTransferItemInMap.put("sendAcidity",acid);
		         	milkTransferItemInMap.put("sendTime",sendTime);
		         	milkTransferItemInMap.put("soda",soda);
		         	milkTransferItemInMap.put("sendTemparature",temp);
		         	milkTransferItemInMap.put("cellType", tankerCell.get("enumId"));
		         	Map milkTransferItemMap = FastMap.newInstance();
		         	try{
		         		resultMap= dispatcher.runSync("createMilkTransferItem", milkTransferItemInMap);
		         		if (ServiceUtil.isError(resultMap)) {
		    		  		Debug.logError("unable to create Milk transfer Item  for milkTransferId"+milkTransferId, module);	
		    		  		resultMap = ServiceUtil.returnError("unable to create Milk transfer Item  for milkTransferId"+milkTransferId);
		    		  		return resultMap; 
		    		  	}
		         	}catch (GenericServiceException e) {
		 				// TODO: handle exception
		         		Debug.logError("Error while creating MilkReceipts for "+tankerCell.get("enumId")+"=========="+e,module);
		             	return ServiceUtil.returnError("Error while creating MilkReceipts for First Cell=========="+e.getMessage());
		 			} 
				
	         }
	       }
         }        
    	resultMap.put("milkTransferId", milkTransferId);
    	return resultMap;
   }// End of Create Milk Receipt
    
    
    public static Map<String, Object> createMilkTransferItem(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = FastMap.newInstance();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String productId = (String) context.get("productId");
   	 	String milkTransferId = (String) context.get("milkTransferId");
   	 	Timestamp sendDate = (Timestamp) context.get("sendDate"); 
        String capacity = (String) context.get("capacity");
        String sendTime = (String) context.get("sendTime");
        String soda = (String) context.get("soda");
        String cob = (String) context.get("cob");
        String milkCondition = (String) context.get("milkCondition");
        String vehicleId = (String)context.get("vehicleId");
        BigDecimal quantityLtrs = (BigDecimal)context.get("quantityLtrs");
        BigDecimal quantity = (BigDecimal)context.get("quantity");
        BigDecimal fat = (BigDecimal)context.get("fat");
        BigDecimal snf = (BigDecimal)context.get("snf");
        BigDecimal sendLR = (BigDecimal)context.get("sendLR");
        BigDecimal sendTemparature = (BigDecimal)context.get("sendTemparature");
        String cellType = (String) context.get("cellType");
        Long sendAcidity = (Long) context.get("sendAcidity");        
        Timestamp receivedDate = (Timestamp) context.get("receivedDate");
        String receivedProductId = (String) context.get("receivedProductId");
        BigDecimal receivedQuantityLtrs = (BigDecimal)context.get("receivedQuantityLtrs");
        BigDecimal receivedQty = (BigDecimal)context.get("receivedQuantity");
        BigDecimal receivedFat = (BigDecimal)context.get("receivedFat");
        BigDecimal receivedSnf = (BigDecimal)context.get("receivedSnf");
        BigDecimal receivedLR = (BigDecimal)context.get("receivedLR");
        String ackTime = (String) context.get("ackTime");     
        BigDecimal receivedTemparature = (BigDecimal)context.get("receivedTemparature");
        Long receivedAcidity = (Long) context.get("receivedAcidity");
        
        String sequenceNum = null;
        try{        	
			GenericValue newTransferItem = delegator.makeValue("MilkTransferItem");
			newTransferItem.set("quantityLtrs", quantityLtrs);
			newTransferItem.set("quantity", quantity);
			newTransferItem.set("fat", fat);
			newTransferItem.set("snf", snf);
			newTransferItem.set("sendProductId", productId);
			newTransferItem.set("milkTransferId", milkTransferId);
			newTransferItem.set("createdByUserLogin", userLogin.get("userLoginId"));
			newTransferItem.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
			newTransferItem.set("sendTemparature", sendTemparature);
			newTransferItem.set("sendAcidity", sendAcidity);
			newTransferItem.set("sendCob", cob);
			newTransferItem.set("cellType",cellType);
			newTransferItem.set("capacity",capacity);
			newTransferItem.set("vehicleId", vehicleId);
			newTransferItem.set("milkCondition", milkCondition);
			newTransferItem.set("sendDate", sendDate);
			newTransferItem.set("sendKgFat", (((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity, fat)).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
			newTransferItem.set("sendKgSnf", (((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity, snf)).setScale(2,BigDecimal.ROUND_HALF_EVEN)));
			newTransferItem.set("sendLR", sendLR);
			newTransferItem.set("sendTime", sendTime);
			newTransferItem.set("sendSoda", soda);
			newTransferItem.set("receiveDate", receivedDate);
			newTransferItem.set("receivedCob", cob);
			newTransferItem.set("receivedTemparature", receivedTemparature);
			newTransferItem.set("receivedAcidity", receivedAcidity);
			newTransferItem.set("receivedFat", receivedFat);
			newTransferItem.set("receivedProductId", receivedProductId);
			newTransferItem.set("receivedSnf", receivedSnf);
			newTransferItem.set("receivedKgFat", ((((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQty, receivedFat)).setScale(2,BigDecimal.ROUND_HALF_EVEN))));
			newTransferItem.set("receivedKgSnf", ((((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQty, receivedSnf)).setScale(2,BigDecimal.ROUND_HALF_EVEN))));
			newTransferItem.set("receivedLR", receivedLR);
			newTransferItem.set("ackTime", ackTime);
			newTransferItem.set("receivedQuantity", (receivedQty.setScale(2,BigDecimal.ROUND_HALF_UP)).setScale(1,BigDecimal.ROUND_HALF_UP));
			newTransferItem.set("receivedQuantityLtrs", receivedQuantityLtrs);
			newTransferItem.set("receivedSoda", soda);
			newTransferItem.set("createdDate", UtilDateTime.nowTimestamp());
			newTransferItem.set("lastModifiedDate", UtilDateTime.nowTimestamp());  
			delegator.setNextSubSeqId(newTransferItem,"sequenceNum",5,1);
			delegator.create(newTransferItem);
			sequenceNum = (String)newTransferItem.get("sequenceNum");
			resultMap = ServiceUtil.returnSuccess("Receipt created successfully Receipt Number "+milkTransferId);
		}catch(Exception e){
			Debug.logError("Error while inserting sending details "+e, module);
			resultMap = ServiceUtil.returnError("Error while inserting Sending Details");
			return resultMap;
		}
    	
    	resultMap.put("milkTransferId", milkTransferId);
    	resultMap.put("sequenceNum", sequenceNum);
    	return resultMap;
   }
    
    /**
     * 
     * Service for update MilkTransfer Item
     * 
     * 
     */   
    
    public static String milkReceiptEntryUpdation(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        List<GenericValue> subscriptionList=FastList.newInstance();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String milkTransferId = (String)request.getParameter("milkTransferId");
        String facilityId = (String) request.getParameter("facilityId");
        String facilityIdTo = (String) request.getParameter("facilityIdTo");    
        String receivedDateStr = (String) request.getParameter("receivedDate");	 
	    String productId = (String) request.getParameter("productId");
        String tankerNo = (String) request.getParameter("tankerNo");
        String capacity = (String) request.getParameter("capacity");
        String ackTime = (String) request.getParameter("ackTime");
        String soda = (String) request.getParameter("soda");
        String cob = (String) request.getParameter("cob");
        String milkCondition = (String) request.getParameter("milkCondition");    
        String sQtyLtrStr =(String) request.getParameter("sQuantityLtrs");  
        String sFatStr =(String) request.getParameter("sFat");  
        String sSnfStr =(String) request.getParameter("sSnf");  
        String gheeYieldStr =(String) request.getParameter("gheeYield");  
        String cQuantityLtrsStr =(String) request.getParameter("cQuantityLtrs");
        String milkType=(String) request.getParameter("milkType");
        BigDecimal sQtyLtr=BigDecimal.ZERO;
        BigDecimal sFat=BigDecimal.ZERO;
        BigDecimal sSnf=BigDecimal.ZERO;
        BigDecimal gheeYield=BigDecimal.ZERO;
        BigDecimal cQuantityLtrs=BigDecimal.ZERO;
        try {
        	sQtyLtr = new BigDecimal(sQtyLtrStr);
           } catch (Exception e) {
               Debug.logWarning(e, "Problems parsing sQtyLtr string: " + sQtyLtrStr, module);               
        } 
       try {
       		sFat = new BigDecimal(sFatStr);
          } catch (Exception e) {
              Debug.logWarning(e, "Problems parsing sFatStr string: " + sFatStr, module);               
       }
       try {
    	   sSnf = new BigDecimal(sSnfStr);
         } catch (Exception e) {
             Debug.logWarning(e, "Problems parsing sSnfStr string: " + sSnfStr, module);               
       }
       try {
    	   	gheeYield = new BigDecimal(gheeYieldStr);
           } catch (Exception e) {
               Debug.logWarning(e, "Problems parsing gheeYieldStr string: " + gheeYieldStr, module);               
        } 
       try {
    	   cQuantityLtrs = new BigDecimal(cQuantityLtrsStr);
          } catch (Exception e) {
              Debug.logWarning(e, "Problems parsing cQuantityLtrsStr string: " + cQuantityLtrsStr, module);               
       }    
        Map milkTransferInMap = FastMap.newInstance();
	    Timestamp receivedDate =null;     
	    
        if (UtilValidate.isNotEmpty(receivedDateStr)) { //2011-12-25
        	SimpleDateFormat   sdf = new SimpleDateFormat("MMMM dd, yyyy");             
	  		  try {
	  			receivedDate = new java.sql.Timestamp(sdf.parse(receivedDateStr).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + receivedDateStr, module);
	              
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + receivedDateStr, module);	               
	  		  }
	  	  }else{
	  		request.setAttribute("_ERROR_MESSAGE_","Received Date can not be empty");
			return "error";
	  	  }
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        List<GenericValue> milkTankerCellList=FastList.newInstance();
        List orderBy = UtilMisc.toList("sequenceId");
        try{
        	milkTankerCellList  = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "MILK_CELL_TYPE"), null,orderBy, null, false);
        }catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), module);
		}
        Map cellWiseDetailsMap=FastMap.newInstance();
        for(GenericValue tankerCell : milkTankerCellList){
        	Map cellEntriesMap=FastMap.newInstance();
        	if(UtilValidate.isNotEmpty(paramMap)){
        		cellEntriesMap.put("quantity", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"quantity"));
        		cellEntriesMap.put("quantityLtrs", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"quantityLtrs"));
        		cellEntriesMap.put("fat", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"fat"));
        		cellEntriesMap.put("snf", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"snf"));
        		cellEntriesMap.put("acid", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"acid"));
        		cellEntriesMap.put("temp", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"temp"));
        		cellEntriesMap.put("clr", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"clr"));
        		cellEntriesMap.put("sequenceNum", paramMap.get(tankerCell.get("enumId")+"_"+"ACK"+"_"+"sequenceNum"));
        	}
        	cellWiseDetailsMap.put(tankerCell.get("enumId"), cellEntriesMap);        	
        }
        Map milkTransferResult = FastMap.newInstance();
        milkTransferInMap.put("userLogin", userLogin);
        milkTransferInMap.put("milkTransferId", milkTransferId);
        milkTransferInMap.put("facilityId", facilityId);
        milkTransferInMap.put("facilityIdTo", facilityIdTo);
        milkTransferInMap.put("recdProductId", productId);
        milkTransferInMap.put("recdDate", receivedDate);
        milkTransferInMap.put("capacity", capacity);
        milkTransferInMap.put("recdCob", cob);
        milkTransferInMap.put("milkCondition", milkCondition);
        milkTransferInMap.put("vehicleId", tankerNo);
        milkTransferInMap.put("sQuantityLtrs", sQtyLtr);
        milkTransferInMap.put("sFat", sFat);
        milkTransferInMap.put("sSnf", sSnf);
        milkTransferInMap.put("milkType", milkType);
        milkTransferInMap.put("gheeYield", gheeYield);
        milkTransferInMap.put("cQuantityLtrs", cQuantityLtrs);
        milkTransferInMap.put("ackTime", ackTime);
        milkTransferInMap.put("soda", soda);
        milkTransferInMap.put("cellWiseDetailsMap", cellWiseDetailsMap);
        try{
        	milkTransferResult = dispatcher.runSync("updateMilkReceiptEntry", milkTransferInMap);
        	if( ServiceUtil.isError(milkTransferResult)) {
				String errMsg =  ServiceUtil.getErrorMessage(milkTransferResult);
				Debug.logWarning(errMsg , module);
				request.setAttribute("_ERROR_MESSAGE_",errMsg);
				return "error";
			}
        }catch (GenericServiceException e) {
			// TODO: handle exception
        	Debug.logError(e,module);
        	
		}
         request.setAttribute("_EVENT_MESSAGE_", "Transfer record Updated Successfully  ");
      	 return "success";
    }
    public static Map<String, Object> updateMilkReceiptEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = FastMap.newInstance();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String milkTransferId = (String)context.get("milkTransferId"); 
   	 	String productId = (String) context.get("recdProductId");
        Timestamp recdDate = (Timestamp) context.get("recdDate"); 
        String capacity = (String) context.get("capacity");
        String ackTime = (String) context.get("ackTime");
        String soda = (String) context.get("soda");
        String cob = (String) context.get("recdCob");
        String milkCondition = (String) context.get("milkCondition");
        Map cellWiseDetailsMap= (Map)context.get("cellWiseDetailsMap");
        String facilityId = (String) context.get("facilityId");
        String facilityIdTo = (String) context.get("facilityIdTo");
        BigDecimal sQtyLtrs=(BigDecimal) context.get("sQuantityLtrs");
        BigDecimal sFat=(BigDecimal) context.get("sFat");
        BigDecimal sSnf=(BigDecimal) context.get("sSnf");
        BigDecimal gheeYield=(BigDecimal) context.get("gheeYield");
        BigDecimal cQuantityLtrs=(BigDecimal) context.get("cQuantityLtrs");
        String milkType=(String) context.get("milkType");
        BigDecimal trnQtyLtrs = BigDecimal.ZERO;
        BigDecimal trnQtyKgs = BigDecimal.ZERO; 
        BigDecimal trnKgFat = BigDecimal.ZERO;
        BigDecimal trnKgSnf = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(cellWiseDetailsMap)){
        	Iterator cellDetailsItr = cellWiseDetailsMap.entrySet().iterator();
			while (cellDetailsItr.hasNext()){
				Map.Entry cellEntry = (Entry) cellDetailsItr.next();						
				Map cellDetails = (Map) cellEntry.getValue();				
				if(UtilValidate.isNotEmpty(cellDetails)){
					//Map cellDtlMap = (Map)(cellDetails.get("dayTotals"));
					String qtyLtrsStr= null;
					String qtyKgsStr= null;
					String fatStr= null;
					String snfStr= null;
					String lrStr= null;
					BigDecimal qtyLtrs= BigDecimal.ZERO;
					BigDecimal qtykgs= BigDecimal.ZERO;
					BigDecimal fat= BigDecimal.ZERO;
					BigDecimal snf= BigDecimal.ZERO;
					BigDecimal lr= BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(cellDetails.get("quantityLtrs"))){
						qtyLtrsStr= (String)cellDetails.get("quantityLtrs");
					 try {
						 qtyLtrs = new BigDecimal(qtyLtrsStr);
		                } catch (Exception e) {
		                    Debug.logWarning(e, "Problems parsing quantity string: " + qtyLtrsStr, module);
		                    
		                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("quantity"))){
						qtyKgsStr= (String)cellDetails.get("quantity");						
						try {
							qtykgs = new BigDecimal(qtyKgsStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + qtykgs, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("fat"))){
						fatStr= (String)cellDetails.get("fat");
						try {
							fat = new BigDecimal(fatStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + fatStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("snf"))){
						snfStr= (String)cellDetails.get("snf");
						try {
							snf = new BigDecimal(snfStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + snfStr, module);
			                    
			                } 
					}
					if(UtilValidate.isNotEmpty(cellDetails.get("clr"))){
						lrStr= (String)cellDetails.get("clr");
						try {
							lr = new BigDecimal(lrStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + lrStr, module);
			                    
			                } 
					}
					trnQtyLtrs= trnQtyLtrs.add(qtyLtrs);
					if(qtyLtrs.compareTo(BigDecimal.ZERO) !=0){
						qtykgs=convertLitresToKGByLR(qtyLtrs,lr,true);
			        }
					trnQtyKgs =trnQtyKgs.add(qtykgs); 
				    trnKgFat =trnKgFat.add((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs,fat));
				    trnKgSnf = trnKgSnf.add((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs,snf));
				}
			}
        }
        if((trnQtyLtrs.compareTo(BigDecimal.ZERO)<=0) && (sQtyLtrs.compareTo(BigDecimal.ZERO)==0)){
        	resultMap = ServiceUtil.returnError("Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }     
        if(trnQtyLtrs.compareTo(new BigDecimal(9500))>0){
	        capacity="Y";
        }else{
        	capacity="N";
        }
        Map milkTransferInMap = FastMap.newInstance();        
        List<GenericValue> milkTankerCellList=FastList.newInstance();
        List orderBy = UtilMisc.toList("sequenceId");
        try{
        	milkTankerCellList  = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "MILK_CELL_TYPE"), null,orderBy, null, false);
        }catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError(e.getMessage(), module);
		}
        
        milkTransferInMap.put("userLogin",userLogin);
        milkTransferInMap.put("receivedFat",ProcurementNetworkServices.calculateFatOrSnf(trnKgFat, trnQtyKgs).setScale(1,BigDecimal.ROUND_HALF_UP));
        milkTransferInMap.put("receivedSnf",ProcurementNetworkServices.calculateFatOrSnf(trnKgSnf, trnQtyKgs).setScale(2,BigDecimal.ROUND_HALF_UP));
        milkTransferInMap.put("facilityId",facilityId);
        milkTransferInMap.put("facilityIdTo",facilityIdTo);
        milkTransferInMap.put("receivedQuantityLtrs",trnQtyLtrs);
        milkTransferInMap.put("receivedQuantity",trnQtyKgs);
        milkTransferInMap.put("receiveDate",recdDate);
        milkTransferInMap.put("status","approved");
        milkTransferInMap.put("productId",productId); 
        if(sQtyLtrs.compareTo(BigDecimal.ZERO)>0){
	        milkTransferInMap.put("sQuantityLtrs",sQtyLtrs);
	        milkTransferInMap.put("sFat",sFat);
	        milkTransferInMap.put("sSnf",sSnf);
	        milkTransferInMap.put("gheeYield",gheeYield);	       
        }
        if(cQuantityLtrs.compareTo(BigDecimal.ZERO)>0){
        	 milkTransferInMap.put("cQuantityLtrs",cQuantityLtrs);
        }
   	 	milkTransferInMap.put("milkType",milkType);   	 	
        milkTransferInMap.put("milkTransferId",milkTransferId);
        Map milkTransferResult = FastMap.newInstance();
        try{
        	milkTransferResult = dispatcher.runSync("updateMilkDetails", milkTransferInMap);
        	if (ServiceUtil.isError(milkTransferResult)) {
		  		Debug.logError("unable to update Milk transfer  milkTransferId"+milkTransferId, module);	
		  		resultMap = ServiceUtil.returnError("unable to update Milk transfer  for milkTransferId"+milkTransferId);
		  		return resultMap; 
		  	}
        }catch (GenericServiceException e) {
			// TODO: handle exception
        	Debug.logError(e,module);
        	return ServiceUtil.returnError("Error while creating MilkReceipts=========="+e.getMessage());
		}
        if(ServiceUtil.isError(milkTransferResult)){
        	Debug.logError("milkTransferResult==============="+milkTransferResult,module);
        	return ServiceUtil.returnError("Error while creating MilkReceipts   =========="+ServiceUtil.getErrorMessage(milkTransferResult));
        }        
        if(UtilValidate.isNotEmpty(milkTransferId)){
	         for(GenericValue tankerCell : milkTankerCellList){
		        	Map milkTransferItemInMap = FastMap.newInstance();
		        	String qtyLtrStr= null;
					String qtyKgStr= null;
					String fatStr= null;
					String snfStr= null;
					String cLrStr= null;
					String acidStr= null;
					String tempStr= null;
					BigDecimal qtyLtrs= BigDecimal.ZERO;
					BigDecimal qtykgs= BigDecimal.ZERO;
					BigDecimal fat= BigDecimal.ZERO;
					BigDecimal snf= BigDecimal.ZERO;
					BigDecimal cLr= BigDecimal.ZERO;
					
					Long acid= Long.parseLong("0");				
					BigDecimal temp= BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(cellWiseDetailsMap.get(tankerCell.get("enumId")))){
						Map cellDetailsMap=(Map)cellWiseDetailsMap.get(tankerCell.get("enumId"));
						
						if(UtilValidate.isNotEmpty(cellDetailsMap.get("quantityLtrs"))){
							qtyLtrStr= (String)cellDetailsMap.get("quantityLtrs");
						 try {
							 qtyLtrs = new BigDecimal(qtyLtrStr);
			                } catch (Exception e) {
			                    Debug.logWarning(e, "Problems parsing quantity string: " + qtyLtrStr, module);
			                    
			                } 
						}					
						String sequenceNum= (String)cellDetailsMap.get("sequenceNum");
						if(UtilValidate.isNotEmpty(cellDetailsMap.get("quantity"))){
							qtyKgStr= (String)cellDetailsMap.get("quantity");						
							try {
								qtykgs = new BigDecimal(qtyKgStr);
				                } catch (Exception e) {
				                    Debug.logWarning(e, "Problems parsing quantity string: " + qtykgs, module);
				                    
				                } 
						}
						if(UtilValidate.isNotEmpty(cellDetailsMap.get("fat"))){
							fatStr= (String)cellDetailsMap.get("fat");
							try {
									fat = new BigDecimal(fatStr);
				                } catch (Exception e) {
				                    Debug.logWarning(e, "Problems parsing quantity string: " + fatStr, module);
				                    
				                } 
						}
						if(UtilValidate.isNotEmpty(cellDetailsMap.get("snf"))){
							snfStr= (String)cellDetailsMap.get("snf");
							try {
								snf = new BigDecimal(snfStr);
				                } catch (Exception e) {
				                    Debug.logWarning(e, "Problems parsing quantity string: " + snfStr, module);
				                    
				                } 
						}
						if(UtilValidate.isNotEmpty(cellDetailsMap.get("clr"))){
							cLrStr= (String)cellDetailsMap.get("clr");
							try {
								cLr = new BigDecimal(cLrStr);
				                } catch (Exception e) {
				                    Debug.logWarning(e, "Problems parsing quantity string: " + cLrStr, module);
				                    
				                } 
						}
						if(UtilValidate.isNotEmpty(cellDetailsMap.get("acid"))){
							acidStr= (String)cellDetailsMap.get("acid");
							try {
								acid = Long.parseLong(acidStr);
				                } catch (Exception e) {
				                    Debug.logWarning(e, "Problems parsing quantity string: " + acidStr, module);
				                    
				                } 
						}
						if(UtilValidate.isNotEmpty(cellDetailsMap.get("temp"))){
							tempStr= (String)cellDetailsMap.get("temp");
							try {
								temp = new BigDecimal(tempStr);
				                } catch (Exception e) {
				                    Debug.logWarning(e, "Problems parsing quantity string: " + tempStr, module);
				                    
				                } 
						}
						if(qtyLtrs.compareTo(BigDecimal.ZERO) !=0){
							qtykgs=convertLitresToKGByLR(qtyLtrs,cLr,false);
						}					         	
			         	milkTransferItemInMap.put("milkTransferId",milkTransferId);
			         	milkTransferItemInMap.put("sequenceNum",sequenceNum);
			        	milkTransferItemInMap.put("userLogin", userLogin);
			        	milkTransferItemInMap.put("receivedProductId",productId);
			        	milkTransferItemInMap.put("receiveDate", recdDate);
			        	milkTransferItemInMap.put("capacity",capacity);
			        	milkTransferItemInMap.put("receivedCob",cob);
			        	milkTransferItemInMap.put("milkCondition",milkCondition);
			        	milkTransferItemInMap.put("receivedQuantityLtrs",qtyLtrs);
			        	milkTransferItemInMap.put("receivedQuantity",qtykgs);
			        	milkTransferItemInMap.put("receivedFat",fat);
			        	milkTransferItemInMap.put("receivedSnf",snf);
			        	milkTransferItemInMap.put("receivedLR",cLr);
			        	milkTransferItemInMap.put("receivedAcidity",acid);
			        	milkTransferItemInMap.put("ackTime",ackTime);
			        	milkTransferItemInMap.put("soda",soda);
			        	milkTransferItemInMap.put("receivedTemparature",temp);
			        	milkTransferItemInMap.put("cellType", tankerCell.get("enumId"));
			         	
			         	Map milkTransferItemMap = FastMap.newInstance();
			         	try{
			         		resultMap= dispatcher.runSync("updateMilkTransferItem", milkTransferItemInMap);
			         		if (ServiceUtil.isError(resultMap)) {
						  		Debug.logError("unable to update Milk transfer item for milkTransferId"+milkTransferId, module);	
						  		resultMap = ServiceUtil.returnError("unable to update Milk transfer item for milkTransferId"+milkTransferId);
						  		return resultMap; 
						  	}
			         	}catch (GenericServiceException e) {
			 				// TODO: handle exception
			         		Debug.logError("Error while creating MilkReceipts for "+tankerCell.get("enumId")+"=========="+e,module);
			             	return ServiceUtil.returnError("Error while creating MilkReceipts for First Cell=========="+e.getMessage());
			 			} 
					
		         }
		       
	         }
        }
    	resultMap.put("milkTransferId", milkTransferId);
    	return resultMap;
   }// End of Updat Milk Receipt
    
    public static Map<String, Object> updateMilkTransferItem(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = FastMap.newInstance();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String receivedProductId = (String) context.get("receivedProductId");
   	 	String milkTransferId = (String) context.get("milkTransferId");
   	 	String sequenceNum = (String) context.get("sequenceNum");
   	 	String productId = (String) context.get("productId");
   	 	Timestamp sendDate = (Timestamp) context.get("sendDate"); 
	    String capacity = (String) context.get("capacity");
	    String sendTime = (String) context.get("sendTime");
	    String soda = (String) context.get("soda");
	    String cob = (String) context.get("cob");
	    String milkCondition = (String) context.get("milkCondition");
	    String vehicleId = (String)context.get("vehicleId");
	    BigDecimal quantityLtrs = (BigDecimal)context.get("quantityLtrs");
	    BigDecimal quantity = (BigDecimal)context.get("quantity");
	    BigDecimal fat = (BigDecimal)context.get("fat");
	    BigDecimal snf = (BigDecimal)context.get("snf");
	    BigDecimal sendLR = (BigDecimal)context.get("sendLR");
	    BigDecimal sendTemparature = (BigDecimal)context.get("sendTemparature");
	    String cellType = (String) context.get("cellType");
	    Long sendAcidity = (Long) context.get("sendAcidity");        
	    Timestamp receivedDate = (Timestamp) context.get("receivedDate");
	    BigDecimal receivedQuantityLtrs = (BigDecimal)context.get("receivedQuantityLtrs");
	    BigDecimal receivedQty = (BigDecimal)context.get("receivedQuantity");
	    BigDecimal receivedFat = (BigDecimal)context.get("receivedFat");
	    BigDecimal receivedSnf = (BigDecimal)context.get("receivedSnf");
	    BigDecimal receivedLR = (BigDecimal)context.get("receivedLR");
	    String ackTime = (String) context.get("ackTime");     
	    BigDecimal receivedTemparature = (BigDecimal)context.get("receivedTemparature");
	    Long receivedAcidity = (Long) context.get("receivedAcidity");
	    try{        	
        	GenericValue newTransferItem = null;
			try {
				newTransferItem = delegator.findOne("MilkTransferItem", UtilMisc.toMap("milkTransferId", milkTransferId,"sequenceNum", sequenceNum),false);
				if (UtilValidate.isEmpty(newTransferItem)) {
					Debug.logError(" No entry found. ", module);
					return ServiceUtil.returnError("No entry found.");
				}
				newTransferItem.set("quantityLtrs", quantityLtrs);
				newTransferItem.set("quantity", quantity);
				newTransferItem.set("fat", fat);
				newTransferItem.set("snf", snf);
				newTransferItem.set("sendProductId", productId);
				newTransferItem.set("milkTransferId", milkTransferId);
				newTransferItem.set("createdByUserLogin", userLogin.get("userLoginId"));
				newTransferItem.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
				newTransferItem.set("sendTemparature", sendTemparature);
				newTransferItem.set("sendAcidity", sendAcidity);
				newTransferItem.set("sendCob", cob);
				newTransferItem.set("cellType",cellType);
				newTransferItem.set("capacity",capacity);
				newTransferItem.set("vehicleId", vehicleId);
				newTransferItem.set("milkCondition", milkCondition);
				newTransferItem.set("sendDate", sendDate);
				newTransferItem.set("sendKgFat", (((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity, fat)).setScale(2,BigDecimal.ROUND_HALF_EVEN)));
				newTransferItem.set("sendKgSnf", (((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(quantity, snf)).setScale(2,BigDecimal.ROUND_HALF_EVEN)));
				newTransferItem.set("sendLR", sendLR);
				newTransferItem.set("sendTime", sendTime);
				newTransferItem.set("sendSoda", soda);
				newTransferItem.set("receiveDate", receivedDate);
				newTransferItem.set("receivedCob", cob);
				newTransferItem.set("receivedTemparature", receivedTemparature);
				newTransferItem.set("receivedAcidity", receivedAcidity);
				newTransferItem.set("receivedFat", receivedFat);
				newTransferItem.set("receivedProductId", receivedProductId);
				newTransferItem.set("receivedSnf", receivedSnf);
				newTransferItem.set("receivedKgFat", (((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQty, receivedFat)).setScale(2,BigDecimal.ROUND_HALF_EVEN)));
				newTransferItem.set("receivedKgSnf", (((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(receivedQty, receivedSnf)).setScale(2,BigDecimal.ROUND_HALF_EVEN)));
				newTransferItem.set("receivedLR", receivedLR);
				newTransferItem.set("ackTime", ackTime);
				newTransferItem.set("receivedQuantity", (receivedQty.setScale(2,BigDecimal.ROUND_HALF_UP)).setScale(1,BigDecimal.ROUND_HALF_UP));
				newTransferItem.set("receivedQuantityLtrs", receivedQuantityLtrs);
				newTransferItem.set("receivedSoda", soda);
				delegator.store(newTransferItem);
					
			} catch (GenericEntityException e) {
				// TODO: handle exception
				Debug.logError(e.getMessage(), module);
				return ServiceUtil.returnError(e.getMessage());
			}      
			resultMap = ServiceUtil.returnSuccess("Receipt Updated successfully Receipt Number "+milkTransferId);
		}catch(Exception e){
			Debug.logError("Error while inserting sending details "+e, module);
			resultMap = ServiceUtil.returnError("Error while inserting Sending Details");
			return resultMap;
		}   	
    	return resultMap;
   }
    
    public static BigDecimal convertLitresToKGByLR(BigDecimal qtyLtrs, BigDecimal lr, Boolean roundingValue ){
		  BigDecimal qtyKgs = BigDecimal.ZERO;
		  if(UtilValidate.isNotEmpty(lr) && UtilValidate.isNotEmpty(qtyLtrs)){
			  qtyKgs = ((qtyLtrs.multiply((BigDecimal)((lr.divide(new BigDecimal(1000),5,BigDecimal.ROUND_HALF_EVEN)).add(BigDecimal.ONE)))));
			  if(roundingValue){
				  qtyKgs = qtyKgs.setScale(0,BigDecimal.ROUND_HALF_UP);  
			  }
		  }
		  return qtyKgs;
	  }
    public static BigDecimal convertKgsToLitresByLR(BigDecimal qtyKgs, BigDecimal lr ){
		  BigDecimal qtyLtrs = BigDecimal.ZERO;
		  if(UtilValidate.isNotEmpty(lr) && UtilValidate.isNotEmpty(qtyKgs)){
			  qtyLtrs = ((qtyKgs.divide((BigDecimal)(lr.divide(new BigDecimal(1000),5,BigDecimal.ROUND_HALF_EVEN).add(BigDecimal.ONE)),1,BigDecimal.ROUND_HALF_UP)).setScale(2,BigDecimal.ROUND_HALF_UP)).setScale(1,BigDecimal.ROUND_HALF_UP);			 			 
		  }
		  return qtyLtrs;
	  }
    
    public static Map<String, Object> createNewMilkReceiptEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = FastMap.newInstance();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");

   	 	String productId = (String) context.get("productId");
        String facilityId = (String) context.get("facilityId");
        String facilityIdTo = (String) context.get("facilityIdTo");
        String sendDateStr = (String) context.get("sendDate"); 
        
        String sendTime = (String) context.get("sendTime");
        String receivedDateStr = (String) context.get("receiveDate"); 
        String ackTime = (String) context.get("ackTime");
        String capacity = (String) context.get("capacity");
        String cob = (String) context.get("cob");
        String soda = (String) context.get("soda");
        String milkCondition = (String) context.get("milkCondition");
        String vehicleId = (String)context.get("tankerNo");
        String qtyKgsFlag = (String) context.get("qtyKgsFlag");
        if(UtilValidate.isEmpty(sendDateStr)){
        	return ServiceUtil.returnError("sendDate can not be empty");
        }
        if(UtilValidate.isEmpty(receivedDateStr)){
        	return ServiceUtil.returnError("receivedDate can not be empty");
        }
        
        Timestamp sendDate =null; 
        Timestamp receiveDate =null; 
        if (UtilValidate.isNotEmpty(sendDateStr)) { //2011-12-25
        	SimpleDateFormat   sdf = new SimpleDateFormat("MMMM dd, yyyy");             
	  		  try {
	  			sendDate = new java.sql.Timestamp(sdf.parse(sendDateStr).getTime());
	  			receiveDate = new java.sql.Timestamp(sdf.parse(receivedDateStr).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + sendDateStr, module);
	              
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + sendDateStr, module);	               
	  		  }
	  	  }
       
        BigDecimal fcQtyLtrs = (BigDecimal)context.get("fcQtyLtrs");
        if(UtilValidate.isEmpty(fcQtyLtrs)){
        	fcQtyLtrs=BigDecimal.ZERO;
        }
        BigDecimal fcFat = (BigDecimal)context.get("fcFat");
        if(UtilValidate.isEmpty(fcFat)){
        	fcFat=BigDecimal.ZERO;
        }
        BigDecimal fcSnf = (BigDecimal)context.get("fcSnf");
        if(UtilValidate.isEmpty(fcSnf)){
        	fcSnf=BigDecimal.ZERO;
        }
        Long fcAcid = (Long)context.get("fcAcid");
        BigDecimal fcTemp = (BigDecimal)context.get("fcTemp");
        BigDecimal fcLR = (BigDecimal)context.get("fcClr");
        BigDecimal fcQtyKgs= BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(qtyKgsFlag) && ("Y".equals(qtyKgsFlag))){
        	fcQtyKgs=fcQtyLtrs;
        	fcQtyLtrs=convertKgsToLitresByLR(fcQtyKgs,fcLR);
        }else{
        	  fcQtyKgs= convertLitresToKGByLR(fcQtyLtrs,fcLR,true);
        }
        
        BigDecimal fcAckQtyLtrs = (BigDecimal)context.get("fcAckQtyLtrs");
        if(UtilValidate.isEmpty(fcAckQtyLtrs)){
        	fcAckQtyLtrs=BigDecimal.ZERO;
        }
        BigDecimal fcAckFat = (BigDecimal)context.get("fcAckFat");
        if(UtilValidate.isEmpty(fcAckFat)){
        	fcAckFat=BigDecimal.ZERO;
        }
        BigDecimal fcAckSnf = (BigDecimal)context.get("fcAckSnf");
        if(UtilValidate.isEmpty(fcAckSnf)){
        	fcAckSnf=BigDecimal.ZERO;
        }
        Long fcAckAcid = (Long)context.get("fcAckAcid");
        BigDecimal fcAckTemp = (BigDecimal)context.get("fcAckTemp");
        BigDecimal fcAckLR = (BigDecimal)context.get("fcAckClr");
        
        BigDecimal mcQtyLtrs = (BigDecimal)context.get("mcQtyLtrs");
        if(UtilValidate.isEmpty(mcQtyLtrs)){
        	mcQtyLtrs=BigDecimal.ZERO;
        }
        BigDecimal mcFat = (BigDecimal)context.get("mcFat");
        if(UtilValidate.isEmpty(mcFat)){
        	mcFat=BigDecimal.ZERO;
        }
        BigDecimal mcSnf = (BigDecimal)context.get("mcSnf");
        if(UtilValidate.isEmpty(mcSnf)){
        	mcSnf=BigDecimal.ZERO;
        }
        Long mcAcid = (Long)context.get("mcAcid");
        BigDecimal mcTemp = (BigDecimal)context.get("mcTemp");
        if(UtilValidate.isEmpty(mcTemp)){
        	mcTemp=BigDecimal.ZERO;
        }
        BigDecimal mcLR = (BigDecimal)context.get("mcClr");
        if(UtilValidate.isEmpty(mcLR)){
        	mcLR=BigDecimal.ZERO;
        }
        BigDecimal mcQtyKgs= BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(qtyKgsFlag) && ("Y".equals(qtyKgsFlag))){
        	mcQtyKgs=mcQtyLtrs;
        	mcQtyLtrs=convertKgsToLitresByLR(mcQtyKgs,mcLR);
        }else{
        	mcQtyKgs= convertLitresToKGByLR(mcQtyLtrs,mcLR,true);
        }
        
        BigDecimal mcAckQtyLtrs = (BigDecimal)context.get("mcAckQtyLtrs");
        if(UtilValidate.isEmpty(mcAckQtyLtrs)){
        	mcAckQtyLtrs=BigDecimal.ZERO;
        }
        BigDecimal mcAckFat = (BigDecimal)context.get("mcAckFat");
        if(UtilValidate.isEmpty(mcAckFat)){
        	mcAckFat=BigDecimal.ZERO;
        }
        BigDecimal mcAckSnf = (BigDecimal)context.get("mcAckSnf");
        if(UtilValidate.isEmpty(mcAckSnf)){
        	mcAckSnf=BigDecimal.ZERO;
        }
        Long mcAckAcid = (Long)context.get("mcAckAcid");
        BigDecimal mcAckTemp = (BigDecimal)context.get("mcAckTemp");
        if(UtilValidate.isEmpty(mcAckTemp)){
        	mcAckTemp=BigDecimal.ZERO;
        }
        BigDecimal mcAckLR = (BigDecimal)context.get("mcAckClr");
        if(UtilValidate.isEmpty(mcAckLR)){
        	mcAckLR=BigDecimal.ZERO;
        }
        BigDecimal bcQtyLtrs = (BigDecimal)context.get("bcQtyLtrs");
        if(UtilValidate.isEmpty(bcQtyLtrs)){
        	bcQtyLtrs=BigDecimal.ZERO;
        }
        BigDecimal bcFat = (BigDecimal)context.get("bcFat");
        if(UtilValidate.isEmpty(bcFat)){
        	bcFat=BigDecimal.ZERO;
        }
        BigDecimal bcSnf = (BigDecimal)context.get("bcSnf");
        if(UtilValidate.isEmpty(bcSnf)){
        	bcSnf=BigDecimal.ZERO;
        }
        Long bcAcid = (Long)context.get("bcAcid");
        BigDecimal bcTemp = (BigDecimal)context.get("bcTemp");
        if(UtilValidate.isEmpty(bcTemp)){
        	bcTemp=BigDecimal.ZERO;
        }
        BigDecimal bcLR = (BigDecimal)context.get("bcClr");
        if(UtilValidate.isEmpty(bcLR)){
        	bcLR=BigDecimal.ZERO;
        }
        BigDecimal bcQtyKgs= BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(qtyKgsFlag) && ("Y".equals(qtyKgsFlag))){
        	bcQtyKgs=bcQtyLtrs;
        	bcQtyLtrs=convertKgsToLitresByLR(bcQtyKgs,bcLR);
        }else{
        	 bcQtyKgs= convertLitresToKGByLR(bcQtyLtrs,bcLR,true);
        }
        BigDecimal bcAckQtyLtrs = (BigDecimal)context.get("bcAckQtyLtrs");
        if(UtilValidate.isEmpty(bcAckQtyLtrs)){
        	bcAckQtyLtrs=BigDecimal.ZERO;
        }
        BigDecimal bcAckFat = (BigDecimal)context.get("bcAckFat");
        if(UtilValidate.isEmpty(bcAckFat)){
        	bcAckFat=BigDecimal.ZERO;
        }
        BigDecimal bcAckSnf = (BigDecimal)context.get("bcAckSnf");
        if(UtilValidate.isEmpty(bcAckSnf)){
        	bcAckSnf=BigDecimal.ZERO;
        }
        Long bcAckAcid = (Long)context.get("bcAckAcid");
        BigDecimal bcAckTemp = (BigDecimal)context.get("bcAckTemp");
        if(UtilValidate.isEmpty(bcAckTemp)){
        	bcAckTemp=BigDecimal.ZERO;
        }
        BigDecimal bcAckLR = (BigDecimal)context.get("bcAckClr");
        if(UtilValidate.isEmpty(bcAckLR)){
        	bcAckLR=BigDecimal.ZERO;
        }
        if(UtilValidate.isNotEmpty(fcQtyLtrs)&&(fcQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        if(UtilValidate.isNotEmpty(fcAckQtyLtrs)&&(fcAckQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Acknowledge Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        Map milkTransferInMap = FastMap.newInstance();
        BigDecimal trnQtyLtrs = fcQtyLtrs;
        BigDecimal trnQtyKgs = convertLitresToKGByLR(fcQtyLtrs,fcLR,true); 
        BigDecimal trnKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs, fcFat);
        BigDecimal trnKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs, fcSnf);
        trnKgFat =trnKgFat.setScale(2,BigDecimal.ROUND_HALF_UP);
        trnKgSnf =trnKgSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
        if(UtilValidate.isNotEmpty(mcQtyLtrs)&&(mcQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	/*BigDecimal mcQtyKgs = convertLitresToKGByLR(mcQtyLtrs,mcLR); */
        	BigDecimal mcKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(mcQtyKgs, mcFat);
            BigDecimal mcKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(mcQtyKgs, mcSnf);
        	trnQtyLtrs = trnQtyLtrs.add(mcQtyLtrs);
        	trnQtyKgs =  trnQtyKgs.add(mcQtyKgs);
        	mcKgFat = mcKgFat.setScale(2,BigDecimal.ROUND_HALF_UP);
        	mcKgSnf = mcKgSnf.setScale(2,BigDecimal.ROUND_HALF_UP);	
        	trnKgFat = trnKgFat.add(mcKgFat);
        	trnKgSnf = trnKgSnf.add(mcKgSnf);
        }
        if(UtilValidate.isNotEmpty(bcQtyLtrs)&&(bcQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	/*BigDecimal bcQtyKgs = convertLitresToKGByLR(bcQtyLtrs,bcLR);*/ 
        	trnQtyLtrs = trnQtyLtrs.add(bcQtyLtrs);
        	trnQtyKgs =  trnQtyKgs.add(bcQtyKgs);
        	BigDecimal bcKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(bcQtyKgs, bcFat);
            BigDecimal bcKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(bcQtyKgs, bcSnf);
            bcKgSnf = bcKgSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
            bcKgFat = bcKgFat.setScale(2,BigDecimal.ROUND_HALF_UP);
            trnKgFat = trnKgFat.add(bcKgFat);
            trnKgSnf = trnKgSnf.add(bcKgSnf);
        }	
        
       //Acknowledgement 
        BigDecimal trnAckQtyLtrs = fcAckQtyLtrs;
        BigDecimal trnAckQtyKgs = convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,true); 
        BigDecimal trnAckKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,false), fcAckFat);
        BigDecimal trnAckKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,false), fcAckSnf);
        trnAckKgFat =(trnAckKgFat.setScale(2,BigDecimal.ROUND_HALF_EVEN));
        trnAckKgSnf =(trnAckKgSnf.setScale(2,BigDecimal.ROUND_HALF_EVEN));
        
        if(UtilValidate.isNotEmpty(mcAckQtyLtrs)&&(mcAckQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	BigDecimal mcAckQtyKgs = convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,true);
        	
        	BigDecimal mcAckKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,false), mcAckFat);
            BigDecimal mcAckKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,false), mcAckSnf);
            trnAckQtyLtrs = trnAckQtyLtrs.add(mcAckQtyLtrs);
        	trnAckQtyKgs =  trnAckQtyKgs.add(mcAckQtyKgs);
        	mcAckKgFat = mcAckKgFat.setScale(2,BigDecimal.ROUND_HALF_UP);
        	mcAckKgSnf = mcAckKgSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
        	
        	trnAckKgFat = trnAckKgFat.add(mcAckKgFat);
        	trnAckKgSnf = trnAckKgSnf.add(mcAckKgSnf);
        }
        if(UtilValidate.isNotEmpty(bcAckQtyLtrs)&&(bcAckQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	BigDecimal bcAckQtyKgs = convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,true); 
        	
        	trnAckQtyLtrs = trnAckQtyLtrs.add(bcAckQtyLtrs);
        	trnAckQtyKgs =  trnAckQtyKgs.add(bcAckQtyKgs);
        		BigDecimal bcAckKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,false), bcAckFat);
                BigDecimal bcAckKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,false), bcAckSnf);
                bcAckKgFat = bcAckKgFat.setScale(2,BigDecimal.ROUND_HALF_UP);
                bcAckKgSnf = bcAckKgSnf.setScale(2,BigDecimal.ROUND_HALF_UP);
                trnAckKgFat = trnAckKgFat.add(bcAckKgFat);
        		trnAckKgSnf = trnAckKgSnf.add(bcAckKgSnf);
        }
        if(trnQtyLtrs.compareTo(new BigDecimal(9500))>0){
	        capacity="Y";
        }else{
        	capacity="N";
        }
        if(UtilValidate.isNotEmpty(trnQtyLtrs)&&(trnQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        if(UtilValidate.isNotEmpty(trnAckQtyLtrs)&&(trnAckQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Acknowledge Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        milkTransferInMap.put("userLogin",userLogin);
        milkTransferInMap.put("facilityIdTo",facilityIdTo);
        milkTransferInMap.put("facilityId",facilityId);
        milkTransferInMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(trnKgFat, trnQtyKgs).setScale(1,BigDecimal.ROUND_HALF_UP));
        milkTransferInMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(trnKgSnf, trnQtyKgs).setScale(2,BigDecimal.ROUND_HALF_UP));
        
        //milkTransferInMap.put("sendKgFat",trnKgFat);
        //milkTransferInMap.put("sendKgSnf",trnKgSnf);
        milkTransferInMap.put("quantityLtrs",trnQtyLtrs);
        milkTransferInMap.put("quantity",trnQtyKgs);
        milkTransferInMap.put("containerId",vehicleId);
        milkTransferInMap.put("sendDate",sendDate);
        milkTransferInMap.put("productId",productId);
        milkTransferInMap.put("isMilkRcpt","Y");
        milkTransferInMap.put("checkTimePeriod", Boolean.FALSE);
        milkTransferInMap.put("customTimePeriodId", "None");
        
        milkTransferInMap.put("receivedFat",ProcurementNetworkServices.calculateFatOrSnf(trnAckKgFat, trnAckQtyKgs).setScale(1,BigDecimal.ROUND_HALF_UP));
        milkTransferInMap.put("receivedSnf",ProcurementNetworkServices.calculateFatOrSnf(trnAckKgSnf, trnAckQtyKgs).setScale(2,BigDecimal.ROUND_HALF_UP));
        milkTransferInMap.put("receivedQuantityLtrs",trnAckQtyLtrs);
        milkTransferInMap.put("receivedQuantity",trnAckQtyKgs);
        milkTransferInMap.put("receiveDate",receiveDate);       
       
        Map milkTransferResult = FastMap.newInstance();
        try{
        	milkTransferResult = dispatcher.runSync("createMilkTransferRecord", milkTransferInMap);
        }catch (GenericServiceException e) {
			// TODO: handle exception
        	Debug.logError(e,module);
        	return ServiceUtil.returnError("Error while creating MilkReceipts=========="+e.getMessage());
		}
        if(ServiceUtil.isError(milkTransferResult)){
        	Debug.logError("milkTransferResult==============="+milkTransferResult,module);
        	return ServiceUtil.returnError("Error while creating MilkReceipts   =========="+ServiceUtil.getErrorMessage(milkTransferResult));
        }
        String milkTransferId = (String)milkTransferResult.get("milkTransferId");
        
        if(UtilValidate.isNotEmpty(milkTransferId)){
        	
        	Map milkTransferItemInMap = FastMap.newInstance();
        	milkTransferItemInMap.put("milkTransferId",milkTransferId);
        	milkTransferItemInMap.put("userLogin", userLogin);
        	milkTransferItemInMap.put("productId",productId);
        	milkTransferItemInMap.put("vehicleId",vehicleId);
        	milkTransferItemInMap.put("sendDate", sendDate);
        	milkTransferItemInMap.put("capacity",capacity);
        	milkTransferItemInMap.put("cob",cob);
        	milkTransferItemInMap.put("milkCondition",milkCondition);
        	milkTransferItemInMap.put("quantityLtrs",fcQtyLtrs);
        	milkTransferItemInMap.put("quantity",fcQtyKgs);
        	milkTransferItemInMap.put("fat",fcFat);
        	milkTransferItemInMap.put("snf",fcSnf);
        	milkTransferItemInMap.put("sendLR",fcLR);
        	milkTransferItemInMap.put("sendAcidity",fcAcid);
        	milkTransferItemInMap.put("sendTime",sendTime);
        	milkTransferItemInMap.put("soda",soda);
        	milkTransferItemInMap.put("sendAcidity",fcAcid);
        	milkTransferItemInMap.put("sendTemparature",fcTemp);
        	milkTransferItemInMap.put("cellType", "FC");     	
        	milkTransferItemInMap.put("receivedProductId",productId);
        	milkTransferItemInMap.put("receivedDate", receiveDate);        
        	milkTransferItemInMap.put("receivedQuantityLtrs",fcAckQtyLtrs);
        	milkTransferItemInMap.put("receivedQuantity",convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,false));
        	milkTransferItemInMap.put("receivedFat",fcAckFat);
        	milkTransferItemInMap.put("receivedSnf",fcAckSnf);
        	milkTransferItemInMap.put("receivedLR",fcAckLR);
        	milkTransferItemInMap.put("receivedAcidity",fcAckAcid);
        	milkTransferItemInMap.put("ackTime",ackTime);
        	milkTransferItemInMap.put("receivedTemparature",fcAckTemp);
        	
        	
        	Map milkTransferItemMap = FastMap.newInstance();
        	try{        		
        		resultMap= dispatcher.runSync("createMilkTransferItem", milkTransferItemInMap);
        	}catch (GenericServiceException e) {
				// TODO: handle exception
        		Debug.logError("Error while creating MilkReceipts for First Cell=========="+e,module);
            	return ServiceUtil.returnError("Error while creating MilkReceipts for First Cell=========="+e.getMessage());
			}
        	if(UtilValidate.isNotEmpty(mcQtyLtrs)&&(mcQtyLtrs.compareTo(BigDecimal.ZERO)>=0)){
        		try{
        			milkTransferItemInMap.put("quantityLtrs",mcQtyLtrs);
                	milkTransferItemInMap.put("quantity",mcQtyKgs);
                	milkTransferItemInMap.put("fat",mcFat);
                	milkTransferItemInMap.put("snf",mcSnf);
                	milkTransferItemInMap.put("sendLR",mcLR);
                	milkTransferItemInMap.put("sendAcidity",mcAcid);
                	milkTransferItemInMap.put("sendTemparature",mcTemp);
                	milkTransferItemInMap.put("sendTime",sendTime);
                	milkTransferItemInMap.put("soda",soda);
                	milkTransferItemInMap.put("capacity",capacity);
                	milkTransferItemInMap.put("cob",cob);
                	milkTransferItemInMap.put("milkCondition",milkCondition);         	
                	milkTransferItemInMap.put("cellType", "MC");                	
                	milkTransferItemInMap.put("receivedProductId",productId);
                	milkTransferItemInMap.put("receivedDate", receiveDate);
                	milkTransferItemInMap.put("receivedQuantityLtrs",mcAckQtyLtrs);
                	milkTransferItemInMap.put("receivedQuantity",convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,false));
                	milkTransferItemInMap.put("receivedFat",mcAckFat);
                	milkTransferItemInMap.put("receivedSnf",mcAckSnf);
                	milkTransferItemInMap.put("receivedLR",mcAckLR);
                	milkTransferItemInMap.put("receivedAcidity",mcAckAcid);
                	milkTransferItemInMap.put("ackTime",ackTime);
                	milkTransferItemInMap.put("receivedTemparature",mcAckTemp);
        			resultMap= dispatcher.runSync("createMilkTransferItem", milkTransferItemInMap);
            	}catch (GenericServiceException e) {
    				// TODO: handle exception
            		Debug.logError("Error while creating MilkReceipts for Middle Cell=========="+e,module);
                	return ServiceUtil.returnError("Error while creating MilkReceipts for Middle Cell=========="+e.getMessage());
    			}
        		
        	}
        	if(UtilValidate.isNotEmpty(bcQtyLtrs)&&(bcQtyLtrs.compareTo(BigDecimal.ZERO)>=0)){
        		try{
        			milkTransferItemInMap.put("quantityLtrs",bcQtyLtrs);
                	milkTransferItemInMap.put("quantity",bcQtyKgs);
                	milkTransferItemInMap.put("fat",bcFat);
                	milkTransferItemInMap.put("snf",bcSnf);
                	milkTransferItemInMap.put("sendLR",bcLR);
                	milkTransferItemInMap.put("sendAcidity",bcAcid);
                	milkTransferItemInMap.put("sendTemparature",bcTemp);
                	milkTransferItemInMap.put("sendTime",sendTime);
                	milkTransferItemInMap.put("soda",soda);
                	milkTransferItemInMap.put("cellType", "BC");
                	milkTransferItemInMap.put("receivedProductId",productId);
                	milkTransferItemInMap.put("receivedDate", receiveDate);
                	milkTransferItemInMap.put("capacity",capacity);
                	milkTransferItemInMap.put("cob",cob);
                	milkTransferItemInMap.put("milkCondition",milkCondition);
                	milkTransferItemInMap.put("receivedQuantityLtrs",bcAckQtyLtrs);
                	milkTransferItemInMap.put("receivedQuantity",convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,false));
                	milkTransferItemInMap.put("receivedFat",bcAckFat);
                	milkTransferItemInMap.put("receivedSnf",bcAckSnf);
                	milkTransferItemInMap.put("receivedLR",bcAckLR);
                	milkTransferItemInMap.put("receivedAcidity",bcAckAcid);
                	milkTransferItemInMap.put("ackTime",ackTime);
                	milkTransferItemInMap.put("receivedTemparature",bcAckTemp);
        			resultMap= dispatcher.runSync("createMilkTransferItem", milkTransferItemInMap);
            	}catch (GenericServiceException e) {
    				// TODO: handle exception
            		Debug.logError("Error while creating MilkReceipts for Back Cell=========="+e,module);
                	return ServiceUtil.returnError("Error while creating MilkReceipts for Back Cell=========="+e.getMessage());
    			}
        		
        	}
        	
        	return resultMap;
        }
    	resultMap.put("milkTransferId", milkTransferId);
    	return resultMap;
   }
    public static Map<String, Object> IncomeExpenditureEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Entry Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String productId = (String)context.get("productId");
		 String shedId = (String) context.get("shedId");
		 String customTimePeriodId = (String) context.get("customTimePeriodId");
		 String amountTypeId = (String) context.get("amountTypeId");
		 BigDecimal kgFat = (BigDecimal) context.get("kgFat");
		 BigDecimal kgSnf = (BigDecimal) context.get("kgSnf"); 
		 BigDecimal salesQtyKgs = (BigDecimal) context.get("salesQtyKgs"); 
		 BigDecimal saleAmount = (BigDecimal) context.get("saleAmount");
		 BigDecimal cashAmount = (BigDecimal) context.get("cashAmount");
		 BigDecimal amount = (BigDecimal) context.get("amount");
		 Map<String, Object> priceResult = FastMap.newInstance();
		  if(amountTypeId.equalsIgnoreCase("MLKRCPT_PROD_RATE")){
			  Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
				inputRateAmt.put("rateTypeId", "MLKRCPTPROD_RATE");
				inputRateAmt.put("productId", productId);
				inputRateAmt.put("facilityId", shedId);
				try{
					Map<String, Object> rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
					if (ServiceUtil.isError(rateAmount)) {
						return ServiceUtil.returnError("rate amount is Empty for product ==="+productId);
					}
					BigDecimal productPrice = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(rateAmount.get("rateAmount"))){
						 productPrice = (BigDecimal) rateAmount.get("rateAmount");						
					}
					amount=productPrice.multiply(salesQtyKgs);
				}catch(GenericServiceException ge){
					 Debug.logError(ge.getMessage(), module);
		     		 return ServiceUtil.returnError(ge.getMessage());
				}
				
		 }
		 try{
			 List conList=FastList.newInstance();
	    	 conList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,shedId));
	    	 conList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
	    	 conList.add(EntityCondition.makeCondition("amountTypeId", EntityOperator.EQUALS,amountTypeId));
			   EntityCondition saleCond = EntityCondition.makeCondition(conList, EntityOperator.AND);
			   List salesList =delegator.findList("FacilityIncomeExpenditure", saleCond, null, null, null,false);
			  if(UtilValidate.isNotEmpty(salesList)){
				  Debug.logWarning("Already Sales Entry Exists for this facility"+shedId, module);
				  return ServiceUtil.returnError("Already Sales Entry Exists for this facility"+shedId);
			  }
			 }catch(GenericEntityException g){
	    		 Debug.logError(g.getMessage(), module);
	     		 return ServiceUtil.returnError(g.getMessage());
	    	 }
		 
		 try{
			 GenericValue newEntity = delegator.makeValue("FacilityIncomeExpenditure");
			 newEntity.set("facilityId", shedId);
	         newEntity.set("customTimePeriodId", customTimePeriodId);
	         newEntity.set("productId", productId);
	         newEntity.set("kgFat", kgFat);
	         newEntity.set("kgSnf", kgSnf);
	         newEntity.set("qtyKgs", salesQtyKgs);
	         newEntity.set("amount", saleAmount);
	         newEntity.set("amountTypeId",amountTypeId);
	         newEntity.set("amount", amount);
	         newEntity.set("productId", productId);
	         if(amountTypeId.equalsIgnoreCase("SALES")){
	        	 newEntity.set("cashRemittance", cashAmount);
	         }
	         delegator.create(newEntity);
	         
		 }catch(GenericEntityException e){
		 	Debug.logError("Error while creating MilkReceipts sales entry=========="+e,module);
     		return ServiceUtil.returnError("Error while creating MilkReceipts sales entry=========="+e.getMessage());
		 }
		 return resultMap;
    }
    public static Map<String, Object>  deleteIncomeExpEntry(DispatchContext dctx, Map<String, ? extends Object> context)  {
    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		 String shedId = (String) context.get("facilityId");
		 String customTimePeriodId = (String) context.get("customTimePeriodId");
		 String amountTypeId=(String) context.get("amountTypeId");
		 GenericValue delSales = null;
		try{
			delSales = delegator.findOne("FacilityIncomeExpenditure", false, UtilMisc.toMap("facilityId",shedId,"customTimePeriodId",customTimePeriodId,"amountTypeId",amountTypeId));
			delSales.remove();
		}catch(GenericEntityException e){
			Debug.logError("error while removing  Record"+e.getMessage(), module);
			result = ServiceUtil.returnError("Error while removing record"); 
		}
		result = ServiceUtil.returnSuccess("Record Removed Successfully");
		return result;
}// end of service
      
    /**
	 * Service for fetching the recent MilkReceipt change by the user
	 * @return orderItem
	 * 
	 */
	
	public static Map<String, Object> fetchRecentReceiptAjax(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        
	        Map orderItemMap = FastMap.newInstance();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String milkTransferId = (String) context.get("milkTransferId");
	        try{
	        	List<GenericValue> milkTransfersList = FastList.newInstance();
	        	if(UtilValidate.isEmpty(milkTransferId)){
	        		Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -30));
		        	List conditionList = FastList.newInstance();
		        	conditionList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		        	conditionList.add(EntityCondition.makeCondition("lastModifiedByUserLogin",EntityOperator.EQUALS,(String)userLogin.get("userLoginId")));
		        	conditionList.add(EntityCondition.makeCondition("isMilkRcpt",EntityOperator.EQUALS,"Y"));
		        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
		        	milkTransfersList = delegator.findList("MilkTransfer",condition,null,null,null,false);
	        	}else{
	        		GenericValue milkTransferDetails = delegator.findOne("MilkTransfer", UtilMisc.toMap("milkTransferId",milkTransferId), false);
	        		if(UtilValidate.isNotEmpty(milkTransferDetails)){
	        			milkTransfersList.add(milkTransferDetails);
	        		}
	        	}
	        	if(UtilValidate.isEmpty(milkTransfersList)){
	        		result.put("orderItemMap", orderItemMap);
	        		return result;
	        	}
	        	milkTransfersList = EntityUtil.orderBy(milkTransfersList, UtilMisc.toList("-lastUpdatedStamp"));
	        	
	        	GenericValue milkTransfer = EntityUtil.getFirst(milkTransfersList);
	        	Timestamp receiveDate = (Timestamp) milkTransfer.get("receiveDate");
	        	Timestamp sendDate = (Timestamp) milkTransfer.get("sendDate");
        		BigDecimal recdQtyLtrs = (BigDecimal) milkTransfer.get("receivedQuantityLtrs");
        		String facilityId = (String) milkTransfer.get("facilityId");
        		GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId", (String)milkTransfer.get("facilityId")),false);
        		GenericValue facilityAttribute = delegator.findOne("FacilityAttribute",UtilMisc.toMap("facilityId", (String)facility.get("parentFacilityId"),"attrName","enableQuantityKgs"),false);
        		String qtyKgsFlag="N";
        		if(UtilValidate.isNotEmpty(facilityAttribute)){
        			qtyKgsFlag=facilityAttribute.getString("attrValue");
        		}
        		String mccCode = (String) facility.get("mccCode");
        		String facilityName = (String)facility.get("facilityName");
        		
        		// here we are getting cellWise received quantity details
        		milkTransferId = (String)milkTransfer.get("milkTransferId");
        		
        		String productId = (String) milkTransfer.get("productId");
        		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId",productId), false);
        		
        		
        		
        		List itemsConditionList = UtilMisc.toList(EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId));
        		EntityCondition itemCondition = EntityCondition.makeCondition(itemsConditionList,EntityJoinOperator.AND);
        		List<GenericValue> milkTransferItems = delegator.findList("MilkTransferItem", itemCondition, null, null, null, false);
        		List cellQtyList = FastList.newInstance();
        		if(UtilValidate.isNotEmpty(milkTransferItems)){
	        		for(GenericValue milkTransferItem : milkTransferItems){
	        			Map cellQtyMap = FastMap.newInstance();
	        			BigDecimal qty = BigDecimal.ZERO;
	        			qty = (BigDecimal)milkTransferItem.get("receivedQuantityLtrs");
	        			String cellType = null;
	        			cellType = (String)milkTransferItem.get("cellType");
	        			cellQtyMap.put(cellType, qty);
	        			cellQtyList.add(cellQtyMap);
	        		}
        		}
        		orderItemMap.put("cellQtyList",cellQtyList);
        		orderItemMap.put("mccCode",mccCode);
        		orderItemMap.put("milkTransferId",milkTransferId);
        		orderItemMap.put("productId",productId);
        		orderItemMap.put("facilityId",facilityId);
        		orderItemMap.put("productName",(String)product.get("productName"));
        		orderItemMap.put("facilityName",facilityName);
        		orderItemMap.put("receiveDate",receiveDate);
        		orderItemMap.put("receiveDateStr",UtilDateTime.toDateString(receiveDate,"dd-MM-yyyy"));
        		orderItemMap.put("sendDateStr",UtilDateTime.toDateString(sendDate,"dd-MM-yyyy"));
        		orderItemMap.put("recdQtyLtrs",recdQtyLtrs);
        		orderItemMap.put("qtyKgsFlag",qtyKgsFlag);
        		orderItemMap.put("tankerId",milkTransfer.get("containerId"));
        		orderItemMap.put("milkTransferItems",milkTransferItems);
        		orderItemMap.put("lastModifiedByUserLogin",(String)milkTransfer.get("lastModifiedByUserLogin"));
        		result.put("orderItemMap", orderItemMap);
	        	
	        }catch (Exception e) {
			// TODO: handle exception
	        	Debug.logError("Error while getting orderItem"+e, module);
	        	result = ServiceUtil.returnError("Error while getting orderItem"+e.getMessage());
	        }
        return result;
	}
    /**
     * 
     * Service for update milkReceipt Entry
     * 
     */
	public static Map<String, Object> updateReceipt(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");

   	 	String productId = (String) context.get("productId");
        String facilityId = (String) context.get("facilityId");
        String milkTransferId = (String) context.get("milkTransferId");
        String facilityIdTo = (String) context.get("facilityIdTo");
        String sendDateStr = (String) context.get("sendDate"); 
        Map<String, Object> resultMap = ServiceUtil.returnSuccess("MilkReceipt updated successfully=========="+milkTransferId); 
        String sendTime = (String) context.get("sendTime");
        String receivedDateStr = (String) context.get("receiveDate"); 
        String ackTime = (String) context.get("ackTime");
        String capacity = (String) context.get("capacity");
        String cob = (String) context.get("cob");
        String soda = (String) context.get("soda");
        String milkCondition = (String) context.get("milkCondition");
        String vehicleId = (String)context.get("tankerNo");
        String qtyKgsFlag = (String) context.get("qtyKgsFlag");
        if(UtilValidate.isEmpty(sendDateStr)){
        	return ServiceUtil.returnError("sendDate can not be empty");
        }
        if(UtilValidate.isEmpty(receivedDateStr)){
        	return ServiceUtil.returnError("receivedDate can not be empty");
        }
        
        Timestamp sendDate =null; 
        Timestamp receiveDate =null; 
        if (UtilValidate.isNotEmpty(sendDateStr)) { //2011-12-25
        	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yy");             
	  		  try {
	  			sendDate = new java.sql.Timestamp(sdf.parse(sendDateStr).getTime());
	  			receiveDate = new java.sql.Timestamp(sdf.parse(receivedDateStr).getTime());
	  		  } catch (ParseException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + sendDateStr, module);
	              
	  		  } catch (NullPointerException e) {
	  			  Debug.logError(e, "Cannot parse date string: " + sendDateStr, module);	               
	  		  }
	  	  } 
        BigDecimal fcQtyLtrs = (BigDecimal)context.get("fcQtyLtrs");
        BigDecimal fcFat = (BigDecimal)context.get("fcFat");
        BigDecimal fcSnf = (BigDecimal)context.get("fcSnf");
        Long fcAcid = (Long)context.get("fcAcid");
        BigDecimal fcTemp = (BigDecimal)context.get("fcTemp");
        BigDecimal fcLR = (BigDecimal)context.get("fcClr");
        BigDecimal fcQtyKgs= BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(qtyKgsFlag) && ("Y".equals(qtyKgsFlag))){
        	fcQtyKgs=fcQtyLtrs;
        	fcQtyLtrs=convertKgsToLitresByLR(fcQtyKgs,fcLR);
        }else{
        	  fcQtyKgs= convertLitresToKGByLR(fcQtyLtrs,fcLR,true);
        }
        
        BigDecimal fcAckQtyLtrs = (BigDecimal)context.get("fcAckQtyLtrs");
        BigDecimal fcAckFat = (BigDecimal)context.get("fcAckFat");
        BigDecimal fcAckSnf = (BigDecimal)context.get("fcAckSnf");
        Long fcAckAcid = (Long)context.get("fcAckAcid");
        BigDecimal fcAckTemp = (BigDecimal)context.get("fcAckTemp");
        BigDecimal fcAckLR = (BigDecimal)context.get("fcAckClr");
        
        BigDecimal mcQtyLtrs = (BigDecimal)context.get("mcQtyLtrs");
        BigDecimal mcFat = (BigDecimal)context.get("mcFat");
        BigDecimal mcSnf = (BigDecimal)context.get("mcSnf");
        Long mcAcid = (Long)context.get("mcAcid");
        BigDecimal mcTemp = (BigDecimal)context.get("mcTemp");
        BigDecimal mcLR = (BigDecimal)context.get("mcClr");
        BigDecimal mcQtyKgs= BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(qtyKgsFlag) && ("Y".equals(qtyKgsFlag))){
        	mcQtyKgs=mcQtyLtrs;
        	mcQtyLtrs=convertKgsToLitresByLR(mcQtyKgs,mcLR);
        }else{
        	mcQtyKgs= convertLitresToKGByLR(mcQtyLtrs,mcLR,true);
        }
        
        BigDecimal mcAckQtyLtrs = (BigDecimal)context.get("mcAckQtyLtrs");
        BigDecimal mcAckFat = (BigDecimal)context.get("mcAckFat");
        BigDecimal mcAckSnf = (BigDecimal)context.get("mcAckSnf");
        Long mcAckAcid = (Long)context.get("mcAckAcid");
        BigDecimal mcAckTemp = (BigDecimal)context.get("mcAckTemp");
        BigDecimal mcAckLR = (BigDecimal)context.get("mcAckClr");
	 	
        BigDecimal bcQtyLtrs = (BigDecimal)context.get("bcQtyLtrs");
        BigDecimal bcFat = (BigDecimal)context.get("bcFat");
        BigDecimal bcSnf = (BigDecimal)context.get("bcSnf");
        Long bcAcid = (Long)context.get("bcAcid");
        BigDecimal bcTemp = (BigDecimal)context.get("bcTemp");
        BigDecimal bcLR = (BigDecimal)context.get("bcClr");
        BigDecimal bcQtyKgs= BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(qtyKgsFlag) && ("Y".equals(qtyKgsFlag))){
        	bcQtyKgs=bcQtyLtrs;
        	bcQtyLtrs=convertKgsToLitresByLR(bcQtyKgs,bcLR);
        }else{
        	 bcQtyKgs= convertLitresToKGByLR(bcQtyLtrs,bcLR,true);
        }
        BigDecimal bcAckQtyLtrs = (BigDecimal)context.get("bcAckQtyLtrs");
        BigDecimal bcAckFat = (BigDecimal)context.get("bcAckFat");
        BigDecimal bcAckSnf = (BigDecimal)context.get("bcAckSnf");
        Long bcAckAcid = (Long)context.get("bcAckAcid");
        BigDecimal bcAckTemp = (BigDecimal)context.get("bcAckTemp");
        BigDecimal bcAckLR = (BigDecimal)context.get("bcAckClr");
        
        if(UtilValidate.isNotEmpty(fcQtyLtrs)&&(fcQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        if(UtilValidate.isNotEmpty(fcAckQtyLtrs)&&(fcAckQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Acknowledge Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        Map milkTransferInMap = FastMap.newInstance();
        BigDecimal trnQtyLtrs = fcQtyLtrs;
        BigDecimal trnQtyKgs = convertLitresToKGByLR(fcQtyLtrs,fcLR,true); 
        BigDecimal trnKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs, fcFat);
        BigDecimal trnKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(trnQtyKgs, fcSnf);
        if(UtilValidate.isNotEmpty(mcQtyLtrs)&&(mcQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	mcQtyKgs = convertLitresToKGByLR(mcQtyLtrs,mcLR,true); 
        	BigDecimal mcKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(mcQtyKgs, mcFat);
            BigDecimal mcKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(mcQtyKgs, mcSnf);
        	trnQtyLtrs = trnQtyLtrs.add(mcQtyLtrs);
        	trnQtyKgs =  trnQtyKgs.add(mcQtyKgs);
        		trnKgFat = trnKgFat.add(mcKgFat);
        		trnKgSnf = trnKgSnf.add(mcKgSnf);
        }
        if(UtilValidate.isNotEmpty(bcQtyLtrs)&&(bcQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	bcQtyKgs = convertLitresToKGByLR(bcQtyLtrs,bcLR,true); 
        	trnQtyLtrs = trnQtyLtrs.add(bcQtyLtrs);
        	trnQtyKgs =  trnQtyKgs.add(bcQtyKgs);
        		BigDecimal bcKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(bcQtyKgs, bcFat);
                BigDecimal bcKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(bcQtyKgs, bcSnf);
        		trnKgFat = trnKgFat.add(bcKgFat);
        		trnKgSnf = trnKgSnf.add(bcKgSnf);
        }
        
       //Acknowledgement 
        BigDecimal trnAckQtyLtrs = fcAckQtyLtrs;
        BigDecimal trnAckQtyKgs = convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,true); 
        BigDecimal trnAckKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,false), fcAckFat);
        BigDecimal trnAckKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,false), fcAckSnf);
        if(UtilValidate.isNotEmpty(mcAckQtyLtrs)&&(mcAckQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	BigDecimal mcAckQtyKgs = convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,true); 
        	BigDecimal mcAckKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,false), mcAckFat);
            BigDecimal mcAckKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,false), mcAckSnf);
            trnAckQtyLtrs = trnAckQtyLtrs.add(mcAckQtyLtrs);
        	trnAckQtyKgs =  trnAckQtyKgs.add(mcAckQtyKgs);
        	trnAckKgFat = trnAckKgFat.add(mcAckKgFat);
        	trnAckKgSnf = trnAckKgSnf.add(mcAckKgSnf);
        }
        if(UtilValidate.isNotEmpty(bcAckQtyLtrs)&&(bcAckQtyLtrs.compareTo(BigDecimal.ZERO)>0)){
        	BigDecimal bcAckQtyKgs = convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,true); 
        	
        	trnAckQtyLtrs = trnAckQtyLtrs.add(bcAckQtyLtrs);
        	trnAckQtyKgs =  trnAckQtyKgs.add(bcAckQtyKgs);
        		BigDecimal bcAckKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,false), bcAckFat);
                BigDecimal bcAckKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,false), bcAckSnf);
        		trnAckKgFat = trnAckKgFat.add(bcAckKgFat);
        		trnAckKgSnf = trnAckKgSnf.add(bcAckKgSnf);
        }
        if(trnQtyLtrs.compareTo(new BigDecimal(9500))>0){
	        capacity="Y";
        }else{
        	capacity="N";
        }
        if(UtilValidate.isNotEmpty(trnQtyLtrs)&&(trnQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        if(UtilValidate.isNotEmpty(trnAckQtyLtrs)&&(trnAckQtyLtrs.compareTo(BigDecimal.ZERO)<=0)){
        	resultMap = ServiceUtil.returnError("Acknowledge Qty Can not be Zero OR Negative Value" );
        	return resultMap;
        }
        try{
        	GenericValue milkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", (String)context.get("milkTransferId")),false);
        	milkTransfer.set("facilityIdTo",facilityIdTo);
        	milkTransfer.set("facilityId",facilityId);
        	milkTransfer.set("fat",ProcurementNetworkServices.calculateFatOrSnf(trnKgFat, trnQtyKgs).setScale(1,BigDecimal.ROUND_HALF_UP));
        	milkTransfer.set("snf",ProcurementNetworkServices.calculateFatOrSnf(trnKgSnf, trnQtyKgs).setScale(2,BigDecimal.ROUND_HALF_UP));
            
            //milkTransferInMap.put("sendKgFat",trnKgFat);
            //milkTransferInMap.put("sendKgSnf",trnKgSnf);
        	milkTransfer.set("quantityLtrs",trnQtyLtrs);
        	milkTransfer.set("quantity",trnQtyKgs);
        	milkTransfer.set("containerId",vehicleId);
        	milkTransfer.set("sendDate",sendDate);
        	milkTransfer.set("productId",productId);
        	milkTransfer.set("isMilkRcpt","Y");
        	milkTransfer.set("receivedFat",ProcurementNetworkServices.calculateFatOrSnf(trnAckKgFat, trnAckQtyKgs).setScale(1,BigDecimal.ROUND_HALF_UP));
        	milkTransfer.set("receivedSnf",ProcurementNetworkServices.calculateFatOrSnf(trnAckKgSnf, trnAckQtyKgs).setScale(2,BigDecimal.ROUND_HALF_UP));
        	milkTransfer.set("receivedQuantityLtrs",trnAckQtyLtrs);
        	milkTransfer.set("receivedQuantity",trnAckQtyKgs);
        	milkTransfer.set("receiveDate",receiveDate);
        	milkTransfer.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
        	delegator.createOrStore(milkTransfer);
        	
        }catch (Exception e) {
			// TODO: handle exception
        	Debug.logError("Error while updating MilkReceipts=========="+e, module);
        	return ServiceUtil.returnError("Error while updating MilkReceipts=========="+e.getMessage());
		}
        GenericValue milkTransferItem = null;
        Map milkTransferItemInMap = FastMap.newInstance();
        Map updateItemResultMap = FastMap.newInstance();
        String fcSequenceNum = (String) context.get("fcSequenceNum");
        String mcSequenceNum = (String) context.get("mcSequenceNum");
        String bcSequenceNum = (String) context.get("bcSequenceNum");
        
       try{
    	   	milkTransferItemInMap.put("milkTransferId",milkTransferId);
    	   	milkTransferItemInMap.put("sequenceNum", fcSequenceNum);
    	   	milkTransferItemInMap.put("userLogin", userLogin);
	       	milkTransferItemInMap.put("productId",productId);
	       	milkTransferItemInMap.put("vehicleId",vehicleId);
	       	milkTransferItemInMap.put("sendDate", sendDate);
	       	milkTransferItemInMap.put("capacity",capacity);
	       	milkTransferItemInMap.put("cob",cob);
	       	milkTransferItemInMap.put("milkCondition",milkCondition);
	       	milkTransferItemInMap.put("quantityLtrs",fcQtyLtrs);
	       	milkTransferItemInMap.put("quantity",fcQtyKgs);
	       	milkTransferItemInMap.put("fat",fcFat);
	       	milkTransferItemInMap.put("snf",fcSnf);
	       	milkTransferItemInMap.put("sendLR",fcLR);
	       	milkTransferItemInMap.put("sendAcidity",fcAcid);
	       	milkTransferItemInMap.put("sendTime",sendTime);
	       	milkTransferItemInMap.put("soda",soda);
	       	milkTransferItemInMap.put("sendAcidity",fcAcid);
	       	milkTransferItemInMap.put("sendTemparature",fcTemp);
	       	milkTransferItemInMap.put("cellType", "FC");     	
	       	milkTransferItemInMap.put("receivedProductId",productId);
	       	milkTransferItemInMap.put("receivedDate", receiveDate);        
	       	milkTransferItemInMap.put("receivedQuantityLtrs",fcAckQtyLtrs);
	       	milkTransferItemInMap.put("receivedQuantity",convertLitresToKGByLR(fcAckQtyLtrs,fcAckLR,false));
	       	milkTransferItemInMap.put("receivedFat",fcAckFat);
	       	milkTransferItemInMap.put("receivedSnf",fcAckSnf);
	       	milkTransferItemInMap.put("receivedLR",fcAckLR);
	       	milkTransferItemInMap.put("receivedAcidity",fcAckAcid);
	       	milkTransferItemInMap.put("ackTime",ackTime);
	       	milkTransferItemInMap.put("receivedTemparature",fcAckTemp);
	       	updateItemResultMap = dispatcher.runSync("updateMilkTransferItem", milkTransferItemInMap);
	       	if(ServiceUtil.isError(updateItemResultMap)){
	       		Debug.logError("Error while updating FC MilkReceipt=========="+ServiceUtil.getErrorMessage(updateItemResultMap), module);
	       	}
       }catch (Exception e) {
		// TODO: handle exception
    	   Debug.logError("Error while updating FC MilkReceipt=========="+e, module);
       	   return ServiceUtil.returnError("Error while updating FC MilkReceipt=========="+e.getMessage());
       }
       
       try{
   	   		milkTransferItemInMap.put("milkTransferId",milkTransferId);
   	   		milkTransferItemInMap.put("sequenceNum", mcSequenceNum);
   	   		milkTransferItemInMap.put("userLogin", userLogin);
	       	milkTransferItemInMap.put("productId",productId);
	       	milkTransferItemInMap.put("vehicleId",vehicleId);
	       	milkTransferItemInMap.put("sendDate", sendDate);
	       	milkTransferItemInMap.put("capacity",capacity);
	       	milkTransferItemInMap.put("cob",cob);
	       	milkTransferItemInMap.put("milkCondition",milkCondition);
	       	milkTransferItemInMap.put("quantityLtrs",mcQtyLtrs);
	       	milkTransferItemInMap.put("quantity",mcQtyKgs);
	       	milkTransferItemInMap.put("fat",mcFat);
	       	milkTransferItemInMap.put("snf",mcSnf);
	       	milkTransferItemInMap.put("sendLR",mcLR);
	       	milkTransferItemInMap.put("sendAcidity",mcAcid);
	       	milkTransferItemInMap.put("sendTime",sendTime);
	       	milkTransferItemInMap.put("soda",soda);
	       	milkTransferItemInMap.put("sendAcidity",mcAcid);
	       	milkTransferItemInMap.put("sendTemparature",mcTemp);
	       	milkTransferItemInMap.put("cellType", "MC");     	
	       	milkTransferItemInMap.put("receivedProductId",productId);
	       	milkTransferItemInMap.put("receivedDate", receiveDate);        
	       	milkTransferItemInMap.put("receivedQuantityLtrs",mcAckQtyLtrs);
	       	milkTransferItemInMap.put("receivedQuantity",convertLitresToKGByLR(mcAckQtyLtrs,mcAckLR,false));
	       	milkTransferItemInMap.put("receivedFat",mcAckFat);
	       	milkTransferItemInMap.put("receivedSnf",mcAckSnf);
	       	milkTransferItemInMap.put("receivedLR",mcAckLR);
	       	milkTransferItemInMap.put("receivedAcidity",mcAckAcid);
	       	milkTransferItemInMap.put("ackTime",ackTime);
	       	milkTransferItemInMap.put("receivedTemparature",mcAckTemp);
	       	if(UtilValidate.isEmpty(mcSequenceNum)){
	       		milkTransferItemInMap.remove("sequenceNum");
	       		updateItemResultMap = dispatcher.runSync("createMilkTransferItem", milkTransferItemInMap);
	       	}else{
	       		updateItemResultMap = dispatcher.runSync("updateMilkTransferItem", milkTransferItemInMap);
	       	}
	       	if(ServiceUtil.isError(updateItemResultMap)){
	       		Debug.logError("Error while updating MC MilkReceipt=========="+ServiceUtil.getErrorMessage(updateItemResultMap), module);
	       	}
      }catch (Exception e) {
		// TODO: handle exception
   	   Debug.logError("Error while updating MC MilkReceipt=========="+e, module);
      	   return ServiceUtil.returnError("Error while updating MC MilkReceipt=========="+e.getMessage());
      }
      try{
 	   		milkTransferItemInMap.put("milkTransferId",milkTransferId);
 	   		milkTransferItemInMap.put("sequenceNum", bcSequenceNum);
 	   		milkTransferItemInMap.put("userLogin", userLogin);
	       	milkTransferItemInMap.put("productId",productId);
	       	milkTransferItemInMap.put("vehicleId",vehicleId);
	       	milkTransferItemInMap.put("sendDate", sendDate);
	       	milkTransferItemInMap.put("capacity",capacity);
	       	milkTransferItemInMap.put("cob",cob);
	       	milkTransferItemInMap.put("milkCondition",milkCondition);
	       	milkTransferItemInMap.put("quantityLtrs",bcQtyLtrs);
	       	milkTransferItemInMap.put("quantity",bcQtyKgs);
	       	milkTransferItemInMap.put("fat",bcFat);
	       	milkTransferItemInMap.put("snf",bcSnf);
	       	milkTransferItemInMap.put("sendLR",bcLR);
	       	milkTransferItemInMap.put("sendAcidity",bcAcid);
	       	milkTransferItemInMap.put("sendTime",sendTime);
	       	milkTransferItemInMap.put("soda",soda);
	       	milkTransferItemInMap.put("sendAcidity",bcAcid);
	       	milkTransferItemInMap.put("sendTemparature",bcTemp);
	       	milkTransferItemInMap.put("cellType", "BC");     	
	       	milkTransferItemInMap.put("receivedProductId",productId);
	       	milkTransferItemInMap.put("receivedDate", receiveDate);        
	       	milkTransferItemInMap.put("receivedQuantityLtrs",bcAckQtyLtrs);
	       	milkTransferItemInMap.put("receivedQuantity",convertLitresToKGByLR(bcAckQtyLtrs,bcAckLR,false));
	       	milkTransferItemInMap.put("receivedFat",bcAckFat);
	       	milkTransferItemInMap.put("receivedSnf",bcAckSnf);
	       	milkTransferItemInMap.put("receivedLR",bcAckLR);
	       	milkTransferItemInMap.put("receivedAcidity",bcAckAcid);
	       	milkTransferItemInMap.put("ackTime",ackTime);
	       	milkTransferItemInMap.put("receivedTemparature",bcAckTemp);
	       	if(UtilValidate.isEmpty(bcSequenceNum)){
	       		milkTransferItemInMap.remove("sequenceNum");
	       		updateItemResultMap = dispatcher.runSync("createMilkTransferItem", milkTransferItemInMap);
	       	}else{
	       		updateItemResultMap = dispatcher.runSync("updateMilkTransferItem", milkTransferItemInMap);
	       	}
	       	if(ServiceUtil.isError(updateItemResultMap)){
	       		Debug.logError("Error while updating BC MilkReceipt=========="+ServiceUtil.getErrorMessage(updateItemResultMap), module);
	       	}
    }catch (Exception e) {
		// TODO: handle exception
 	   		Debug.logError("Error while updating BC MilkReceipt=========="+e, module);
    	   return ServiceUtil.returnError("Error while updating BC MilkReceipt=========="+e.getMessage());
    }
          
       
       
       
       
       
    	return resultMap;
   }
	
   /**
    * finalizeMilkReceipts
    * 	
    */
	
	public static Map<String, Object> finalizeMilkReceipts(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	Timestamp fromDate = (Timestamp) context.get("fromDate");
   	 	Timestamp thruDate = (Timestamp) context.get("thruDate");
   	 	fromDate = UtilDateTime.getDayStart(fromDate);
   	 	thruDate = UtilDateTime.getDayEnd(thruDate);
   	 	Map resultMap = ServiceUtil.returnSuccess("Finalized milkReceipts Successfully");
   	 	try{
   	 		List conditionList = UtilMisc.toList(EntityCondition.makeCondition("isMilkRcpt",EntityOperator.EQUALS,"Y"));
   	 		conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
   	 		conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
   	 		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
   	 		
   	 		List<GenericValue> milkTransfersList = delegator.findList("MilkTransfer", condition, null, null, null, false);
   	 		SimpleDateFormat   sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
   	 		Timestamp compareDate = UtilDateTime.nowTimestamp();
		  try {
			  compareDate = new java.sql.Timestamp(sdf.parse("2014-09-01 00:00:00").getTime());
		  } catch (ParseException e) {
			  Debug.logError(e, "Cannot parse date string: " + "2014-09-01 00:00:00", module);
            
		  } catch (NullPointerException e) {
			  Debug.logError(e, "Cannot parse date string: " + "2014-09-01 00:00:00", module);	               
		  }
	   	 	if(fromDate.compareTo((compareDate))<0){
		   	 		Debug.logError("You are not permitted to finalize MilkReceipts before 1st Sept 2014 ",module);
			 		return ServiceUtil.returnError("You are not permitted to finalize MilkReceipts before 1st Sept 2014 ");
	 		}
   	 		if(UtilValidate.isEmpty(milkTransfersList)){
	   	 		Debug.logError("No MilkReceipts Found For the given Period to finalize==",module);
		 		return ServiceUtil.returnError("No MilkReceipts Found For the given Period to finalize==");
			}
   	 		
   	 		if(UtilValidate.isNotEmpty(milkTransfersList)){
   	 			for(GenericValue milkTransfer : milkTransfersList){
   	 				GenericValue tempMilkTransfer = milkTransfer;
   	 				String milkTransferId = (String)milkTransfer.get("milkTransferId");
   	 				List itemConditionList = UtilMisc.toList(EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId));
   	 				EntityCondition itemCondition = EntityCondition.makeCondition(itemConditionList,EntityJoinOperator.AND);
   	 				BigDecimal sendKgFat = BigDecimal.ZERO;
   	 				BigDecimal sendKgSnf = BigDecimal.ZERO;
   	 				BigDecimal receivedKgFat = BigDecimal.ZERO;
	 				BigDecimal receivedKgSnf = BigDecimal.ZERO;
	 				BigDecimal sKgFat = BigDecimal.ZERO;
   	 				BigDecimal sKgSnf = BigDecimal.ZERO;
   	 				BigDecimal sQtyLtrs = BigDecimal.ZERO;
	 				BigDecimal sQtyKgs = BigDecimal.ZERO;
	 				BigDecimal recdKgs = BigDecimal.ZERO;
	 				BigDecimal recdLtrs = BigDecimal.ZERO;
	 				
	 				String milkType =null;   	 				
	 				List<GenericValue> milkTransferItemsList = delegator.findList("MilkTransferItem", itemCondition, null, null, null, false);
	 				for(GenericValue tranItem : milkTransferItemsList){
	 					String receivedProductId = (String)tranItem.get("receivedProductId");
	 					if(receivedProductId.equalsIgnoreCase("999")){
	 						milkType ="S";
	 						sQtyLtrs = sQtyLtrs.add((BigDecimal)tranItem.get("receivedQuantityLtrs"));
	 						sQtyKgs = sQtyKgs.add((BigDecimal)tranItem.get("receivedQuantity"));
	 						sKgFat = sKgFat.add((BigDecimal)tranItem.get("receivedKgFat"));
	 						sKgSnf = sKgSnf.add((BigDecimal)tranItem.get("receivedKgSnf"));
	 					}else{
	 						recdKgs = recdKgs.add((BigDecimal)tranItem.get("receivedQuantity"));
	 						recdLtrs = recdLtrs.add((BigDecimal)tranItem.get("receivedQuantityLtrs"));
	 						sendKgFat = sendKgFat.add((BigDecimal)tranItem.get("sendKgFat"));
	 						sendKgSnf = sendKgSnf.add((BigDecimal)tranItem.get("sendKgSnf"));
	 						receivedKgFat = receivedKgFat.add((BigDecimal)tranItem.get("receivedKgFat"));
	 						receivedKgSnf = receivedKgSnf.add((BigDecimal)tranItem.get("receivedKgSnf"));
	 					}
	 				}//End of transferItems
   	 				
	 				if(UtilValidate.isNotEmpty(milkType) && milkType.equalsIgnoreCase("S")){
	 					tempMilkTransfer.put("milkType", milkType);
	 					tempMilkTransfer.put("sQuantityLtrs",sQtyLtrs);
	 					tempMilkTransfer.put("sQtyKgs",sQtyKgs);
	 					tempMilkTransfer.put("sKgFat",sKgFat);
	 					tempMilkTransfer.put("sKgSnf",sKgSnf);
	 					tempMilkTransfer.put("sFat",(ProcurementNetworkServices.calculateFatOrSnf(sKgFat, sQtyKgs)).setScale(1,BigDecimal.ROUND_HALF_UP));
	 					tempMilkTransfer.put("sSnf",(ProcurementNetworkServices.calculateFatOrSnf(sKgSnf, sQtyKgs)).setScale(2,BigDecimal.ROUND_HALF_UP));
	 					tempMilkTransfer.put("receivedQuantity",BigDecimal.ZERO);
	 					tempMilkTransfer.put("receivedQuantityLtrs",BigDecimal.ZERO);
	 					tempMilkTransfer.put("receivedKgFat",BigDecimal.ZERO);
	 					tempMilkTransfer.put("receivedKgSnf",BigDecimal.ZERO);
	 					tempMilkTransfer.put("isMilkRcpt","N");
	 				}else{
	 					tempMilkTransfer.put("receivedQuantity",recdKgs);
	 					tempMilkTransfer.put("receivedQuantityLtrs",recdLtrs);
	 					tempMilkTransfer.put("receivedKgFat",receivedKgFat);
	 					tempMilkTransfer.put("receivedKgSnf",receivedKgSnf);
	 					tempMilkTransfer.put("sendKgFat",sendKgFat);
	 					tempMilkTransfer.put("isMilkRcpt","N");
	 					tempMilkTransfer.put("sendKgSnf",sendKgSnf);
	 				}
	 				tempMilkTransfer.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	 				tempMilkTransfer.store();
   	 			}
   	 		}
   	 	}catch (Exception e) {
			// TODO: handle exception
   	 		Debug.logError("Error While finalizing the MilkReceipts=="+e,module);
   	 		return ServiceUtil.returnError("Error While finalizing the MilkReceipts==");
		}
   	 	
   	 	return resultMap;
   	 	
   	 	
	}//End of the service 
	
	
}
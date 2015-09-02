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
import java.text.DateFormat;
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
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;
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
import org.ofbiz.entity.util.EntityListIterator;

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
        BigDecimal sendAcidity = (BigDecimal) context.get("sendAcidity");        
        Timestamp receivedDate = (Timestamp) context.get("receivedDate");
        String receivedProductId = (String) context.get("receivedProductId");
        BigDecimal receivedQuantityLtrs = (BigDecimal)context.get("receivedQuantityLtrs");
        BigDecimal receivedQty = (BigDecimal)context.get("receivedQuantity");
        BigDecimal receivedFat = (BigDecimal)context.get("receivedFat");
        BigDecimal receivedSnf = (BigDecimal)context.get("receivedSnf");
        BigDecimal receivedLR = (BigDecimal)context.get("receivedLR");
        String ackTime = (String) context.get("ackTime");     
        BigDecimal receivedTemparature = (BigDecimal)context.get("receivedTemparature");
        BigDecimal receivedAcidity = (BigDecimal) context.get("receivedAcidity");
        String recdOrganoLepticTest = (String) context.get("recdOrganoLepticTest");
        
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
			newTransferItem.set("recdOrganoLepticTest", recdOrganoLepticTest);
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
	    BigDecimal sendAcidity = (BigDecimal) context.get("sendAcidity");        
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
	
	/**
	 * Services for tanker Receipts
	 * 
	 */
	
	public static Map<String, Object> getTankerRecordNumber(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map resultMap = FastMap.newInstance();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	String milkTransferId ="";
   	 	String reqStatusId = (String)context.get("reqStatusId");
		// here we are checking vehicleTripStatus
   	 	
   	 	String sequenceNum = "";
   	 	List<GenericValue> vehicleTripStatusListTest = FastList.newInstance();
   	 	try{
   	 		List vehicleTripConditionList = UtilMisc.toList(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,tankerNo));
   	 		vehicleTripConditionList.add(EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null));
   	 		vehicleTripConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.LIKE,"MR_%"));
   	 		EntityCondition vehicleTripCondition = EntityCondition.makeCondition(vehicleTripConditionList);
   	 		
   	 		vehicleTripStatusListTest = delegator.findList("VehicleTripStatus", vehicleTripCondition,null,null,null,false);
   	 	}catch(GenericEntityException e){
   	 		Debug.logError("Error while getting vehicle trip status ::"+e,module);
   	 		return ServiceUtil.returnError("Error while getting vehicle trip status ::"+e.getMessage());
   	 	}
   	 	
   	 	if(UtilValidate.isEmpty(vehicleTripStatusListTest)){
   	 		Debug.logError("No record Found",module);
   	 		resultMap = ServiceUtil.returnError("No record Found");
   	 		return resultMap;
   	 	}
   	 	GenericValue vehicleTripStatusTest = EntityUtil.getFirst(vehicleTripStatusListTest);
   	 	sequenceNum = (String) vehicleTripStatusTest.get("sequenceNum");
   	 	List<GenericValue> transfersList = FastList.newInstance();
   	 	try{
   	 		List transfersCondList = FastList.newInstance();
   	 		transfersCondList.add(EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,tankerNo));
   	 		transfersCondList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,sequenceNum));
   	 		EntityCondition transferCondtion = EntityCondition.makeCondition(transfersCondList,EntityJoinOperator.AND) ;
   	 		List orderBy = UtilMisc.toList("-milkTransferId");
   	 		transfersList = delegator.findList("MilkTransfer", transferCondtion,null, orderBy, null, false);
   	 	}catch(GenericEntityException e){
   	 		Debug.logError("Error while getting MilkTransfer Details ::"+e,module);
   	 		return ServiceUtil.returnError("Error while getting MilkTransfer Details ::"+e.getMessage());
   	 	}
   	 	if(UtilValidate.isEmpty(transfersList)){
   	 		resultMap = ServiceUtil.returnError("No record Found");
   	 		return resultMap;
   	 	}else{
	   	 	GenericValue transferRecord = EntityUtil.getFirst(transfersList);
		 	String mtStatusId = (String) transferRecord.get("statusId");
			milkTransferId =(String)transferRecord.get("milkTransferId"); 
			resultMap = ServiceUtil.returnSuccess();
			resultMap.put("dcNo", transferRecord.get("dcNo"));
			resultMap.put("productId", transferRecord.get("productId"));
			resultMap.put("milkTransferId", transferRecord.get("milkTransferId"));
			resultMap.put("vehicleId", transferRecord.get("containerId"));
			
			BigDecimal grossWeight = new BigDecimal(0);
			if(UtilValidate.isNotEmpty(transferRecord.get("grossWeight"))){
				grossWeight = (BigDecimal)transferRecord.get("grossWeight");
			}
			BigDecimal tareWeight = new BigDecimal(0);
			if(UtilValidate.isNotEmpty(transferRecord.get("tareWeight"))){
				tareWeight = (BigDecimal)transferRecord.get("tareWeight");
			}
			resultMap.put("grossWeight", grossWeight);
			resultMap.put("tareWeight", tareWeight);
			resultMap.put("sequenceNum", transferRecord.get("sequenceNum"));
			resultMap.put("partyId", transferRecord.get("partyId"));
			resultMap.put("partyIdTo", transferRecord.get("partyIdTo"));
			resultMap.put("isSealChecked",transferRecord.get("isSealChecked"));
			resultMap.put("isCipChecked",transferRecord.get("isCipChecked"));
			
			resultMap.put("milkTransfer",transferRecord);
			List<GenericValue> milkTransferItemsList = FastList.newInstance();
			try{
				milkTransferItemsList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
			}catch(Exception e){
				Debug.logError("Error while getting transferItems List ::"+e,module);
				return ServiceUtil.returnError("Error while getting transferItems List ::"+e.getMessage());
			}
			
			if(UtilValidate.isNotEmpty(milkTransferItemsList)){
				GenericValue milkTransferItem = EntityUtil.getFirst(milkTransferItemsList);
				resultMap.put("milkTransferItem",milkTransferItem);
			}
			if(UtilValidate.isNotEmpty((Timestamp)transferRecord.get("sendDate"))){
				java.sql.Date sendDateFormat = new java.sql.Date(((Timestamp)transferRecord.get("sendDate")).getTime());
				String sendDateStr = UtilDateTime.toDateString(sendDateFormat,"dd-MM-yyyy");
				String sendTimeStr = UtilDateTime.toDateString(sendDateFormat,"HHmm");
				resultMap.put("sendDateStr", sendDateStr);
				resultMap.put("sendTimeStr", sendTimeStr);
			}
   	 	}
   	 	return resultMap;
	}
	
	
	public static Map<String, Object> createMilkTankerReceiptEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String receivedProductId = (String) context.get("receivedProductId");
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	String sendDateStr = (String) context.get("sendDate");
   	 	String entryDateStr = (String) context.get("entryDate");
   	 	String sendTime = (String) context.get("sendTime");
   	 	String entryTime = (String) context.get("entryTime");
   	 	String dcNo=(String)context.get("dcNo");
   	 	String productId = (String) context.get("productId");
   	 	String isCipChecked = (String)context.get("isCipChecked");
   	 	String partyId = (String) context.get("partyId");
	 	String partyIdTo = (String) context.get("partyIdTo");
	 	String sealCheck = (String) context.get("sealCheck");
	 	String vehicleStatusId = (String) context.get("vehicleStatusId");
	 	String milkTransferId = (String)context.get("milkTransferId");
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	try{
	 		//we need to check the transfer status of the give tanker
	 		Map tankerInMap = FastMap.newInstance();
	 		tankerInMap.put("tankerNo", tankerNo);
	 		tankerInMap.put("userLogin", userLogin);
	 		tankerInMap.put("reqStatusId","MXF_INIT");
	 		
	 		Map getTankerDetailsMap = getTankerRecordNumber(dctx,tankerInMap);
	 		if(ServiceUtil.isSuccess(getTankerDetailsMap) && UtilValidate.isEmpty(milkTransferId)){
	 			Debug.logError("Exisiting receipt is not completed of the tanker :"+tankerNo,module);
	 			resultMap = ServiceUtil.returnError("Exisiting receipt is not completed of the tanker :"+tankerNo);
	 			return resultMap;
	 		}
	 		// we need to create vehicle trip and vehicle trip status
	 		
	 		//creating vehicle trip
	 		Map vehicleTripMap = FastMap.newInstance();
	 		vehicleTripMap.put("vehicleId", tankerNo);
	 		vehicleTripMap.put("partyId", partyId);
	 		vehicleTripMap.put("userLogin",userLogin);
	 		Map vehicleTripResultMap = dispatcher.runSync("createVehicleTrip", vehicleTripMap);
	 		
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTrip :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip ");
	 			return resultMap;
	 		}
	 		String sequenceNum = (String)vehicleTripResultMap.get("sequenceNum");
	 		
	 		Map vehicleTripStatusMap = FastMap.newInstance();
	 		vehicleTripStatusMap.putAll(vehicleTripResultMap);
	 		vehicleTripStatusMap.put("statusId","MR_VEHICLE_IN");
	 		if(UtilValidate.isNotEmpty(vehicleStatusId)){
	 			vehicleTripStatusMap.put("statusId",vehicleStatusId);
	 		}
	 		vehicleTripStatusMap.put("userLogin",userLogin);
	 		Timestamp entryDate = UtilDateTime.nowTimestamp();
	 		if(UtilValidate.isNotEmpty(sendDateStr)){
	 			if(UtilValidate.isNotEmpty(sendTime)){
	 				entryDateStr = entryDateStr.concat(entryTime);
	 			}
	 			entryDate = new java.sql.Timestamp(sdf.parse(entryDateStr).getTime());
	 		}
	 		Timestamp sendDate = UtilDateTime.nowTimestamp();
	 		if(UtilValidate.isNotEmpty(sendDateStr)){
	 			if(UtilValidate.isNotEmpty(sendTime)){
	 				sendDateStr = sendDateStr.concat(sendTime);
	 			}
	 			sendDate = new java.sql.Timestamp(sdf.parse(sendDateStr).getTime());
	 		}
	 		if(entryDate.compareTo(sendDate)<0){
	 			Debug.logError("Dispatch date time should not  greater than or Equal to entryDate and time ", module);
	 			resultMap = ServiceUtil.returnError("Dispatch date time should not  greater than or Equal to entryDate and time ");
	 			return resultMap;
	 		}
	 		vehicleTripStatusMap.put("estimatedStartDate",entryDate);
	 		vehicleTripStatusMap.remove("responseMessage");
	 		Map vehicleStatusResultMap = dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusMap);
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTripStatus :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip Status");
	 			return resultMap;
	 		}
	 		GenericValue newTransfer = null; 
	 		if(UtilValidate.isNotEmpty(milkTransferId)){
		 		try{
		 			newTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId",milkTransferId),false);
		 			
		 		}catch(GenericEntityException e){
		 			Debug.logError("Error while getting Transfer Details for :: "+milkTransferId,module);
		 			return ServiceUtil.returnError("Error while getting Transfer Details for :: "+milkTransferId);
		 		}
		 	}
	 		if(UtilValidate.isEmpty(newTransfer)){
	 			newTransfer = delegator.makeValue("MilkTransfer");
	 		}
	 		newTransfer.set("containerId", tankerNo);
	 		newTransfer.set("sequenceNum", sequenceNum);
	 		newTransfer.set("sendDate", sendDate);
	 		newTransfer.set("dcNo", dcNo);
	 		if(UtilValidate.isNotEmpty(isCipChecked)){
	 			newTransfer.set("isCipChecked", isCipChecked);
	 		}
	 		newTransfer.set("productId",productId);
	 		newTransfer.set("partyId", partyId);
	 		newTransfer.set("isSealChecked", sealCheck);
 			newTransfer.set("statusId", "MXF_INPROCESS");
	 		newTransfer.set("partyIdTo", partyIdTo);
	 		newTransfer.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		newTransfer.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		if(UtilValidate.isEmpty(milkTransferId)){
	 			delegator.createSetNextSeqId(newTransfer);
	 		}else{
	 			delegator.createOrStore(newTransfer);
	 		}
	 		resultMap = ServiceUtil.returnSuccess("Successfully Created Tanker Receipt with Record Number :"+newTransfer.get("milkTransferId"));
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating record==========="+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating record==========="+e.getMessage());
		}
    	return resultMap;
   }
	
	
	public static Map<String, Object> updateMilkTankerReceiptEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Updated Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String tankerNo = (String) context.get("tankerNo");
	 	String statusId = (String) context.get("statusId");
	 	String milkTransferId = (String) context.get("milkTransferId");
	 	String qcReject = (String) context.get("qcReject");
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	try{
	 		GenericValue milkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", milkTransferId),false);
	 		if(UtilValidate.isEmpty(milkTransfer)){
	 			Debug.logError("Error while getting existing Record", module);
	 			resultMap = ServiceUtil.returnError("Error while getting existing Record");
	 			return resultMap;
	 		}
	 		String sequenceNum = (String) milkTransfer.get("sequenceNum");
	 		Map updateVehStatusInMap = FastMap.newInstance();
        	updateVehStatusInMap.put("userLogin", userLogin);
        	updateVehStatusInMap.put("statusId", statusId);
        	updateVehStatusInMap.put("vehicleId", tankerNo);
        	updateVehStatusInMap.put("sequenceNum", sequenceNum);
        	
        	Map updateVehStatResultMap = FastMap.newInstance();
	 		// Need to handle based on the statusId
	 		if(statusId.equalsIgnoreCase("MR_VEHICLE_GRSWEIGHT")){
	 			
	 			Long numberOfCells = (Long)context.get("numberOfCells");
	 			BigDecimal grossWeight = (BigDecimal)context.get("grossWeight");
	 			BigDecimal dispatchWeight = (BigDecimal)context.get("dispatchWeight");
	 			String grossDateStr = (String) context.get("grossDate"); 
	 	        String grossTime = (String) context.get("grossTime");
	 	        Timestamp grossDate = UtilDateTime.nowTimestamp();
	 	        String driverName = (String) context.get("driverName");
	 	        
	 	        try{
	 		 		if(UtilValidate.isNotEmpty(grossDateStr)){
	 		 			if(UtilValidate.isNotEmpty(grossTime)){
	 		 				grossDateStr = grossDateStr.concat(grossTime);
	 		 			}
	 		 			grossDate = new java.sql.Timestamp(sdf.parse(grossDateStr).getTime());
	 		 		}
	 	        	
	 	        }catch(ParseException e){
	 	        	Debug.logError(e, "Cannot parse date string: " + grossDateStr, module);
	 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
		 			return resultMap;
	 	        }
	 	        updateVehStatusInMap.put("estimatedStartDate",grossDate);
	 	        try{
	 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
			 			return resultMap;
	 	        	}
	 	        }catch (GenericServiceException e) {
					// TODO: handle exception
	 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
		 			return resultMap;
	 	        	
				}catch(Exception e){
					Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
		 			return resultMap;
				}
	 	        String sealCheck = (String) context.get("sealCheck");
				milkTransfer.set("dispatchWeight", dispatchWeight);
				milkTransfer.set("grossWeight", grossWeight);
				milkTransfer.set("numberOfCells", numberOfCells);
				milkTransfer.set("isSealChecked",sealCheck);
				milkTransfer.set("driverName", driverName);
				milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	 		}else if(statusId.equalsIgnoreCase("MR_VEHICLE_TARWEIGHT")){
	 			BigDecimal tareWeight = (BigDecimal)context.get("tareWeight");
	 			String tareDateStr = (String) context.get("tareDate"); 
	 	        String tareTime = (String) context.get("tareTime");
	 	        Timestamp tareDate = UtilDateTime.nowTimestamp();
	 	        try{
	 		 		if(UtilValidate.isNotEmpty(tareDateStr)){
	 		 			if(UtilValidate.isNotEmpty(tareTime)){
	 		 				tareDateStr = tareDateStr.concat(tareTime);
	 		 			}
	 		 			tareDate = new java.sql.Timestamp(sdf.parse(tareDateStr).getTime());
	 		 		}
	 	        	
	 	        }catch(ParseException e){
	 	        	Debug.logError(e, "Cannot parse date string: " + tareDateStr, module);
	 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
		 			return resultMap;
	 	        }
	 	        updateVehStatusInMap.put("estimatedStartDate",tareDate);
	 	        try{
	 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
			 			return resultMap;
	 	        	}
	 	        }catch (GenericServiceException e) {
					// TODO: handle exception
	 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
	 	        	
				}catch(Exception e){
					Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
				}
	 	        
				milkTransfer.set("tareWeight", tareWeight);
				//need to calcultae actual weight of the milk received i.e recdQty,sendQty
				BigDecimal sendQty = BigDecimal.ZERO;
				BigDecimal recdQty = BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(tareWeight) && UtilValidate.isNotEmpty(milkTransfer.get("dispatchWeight"))){
					sendQty = ((BigDecimal)milkTransfer.get("dispatchWeight"));
					if(sendQty.compareTo(BigDecimal.ZERO)<0){
						Debug.logError("Dispatch Weight is Wrong",module);
						resultMap = ServiceUtil.returnError("DispatchWeight is Wrong");
						return resultMap;
					}
				}
				if(UtilValidate.isNotEmpty(tareWeight) && UtilValidate.isNotEmpty(milkTransfer.get("grossWeight"))){
					recdQty = ((BigDecimal)milkTransfer.get("grossWeight")).subtract(tareWeight);
				}
				BigDecimal fat = (BigDecimal)milkTransfer.get("fat");
				BigDecimal snf = (BigDecimal)milkTransfer.get("snf");
				
				BigDecimal receivedFat = (BigDecimal)milkTransfer.get("receivedFat");
				BigDecimal receivedSnf = (BigDecimal)milkTransfer.get("receivedSnf");
				
				BigDecimal sendKgFat = BigDecimal.ZERO;
				BigDecimal sendKgSnf = BigDecimal.ZERO;
				
				BigDecimal receivedKgFat = BigDecimal.ZERO;
				BigDecimal receivedKgSnf = BigDecimal.ZERO;
				
				BigDecimal receivedQuantityLtrs = BigDecimal.ZERO;
				BigDecimal quantityLtrs = BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(sendQty) && sendQty.compareTo(BigDecimal.ZERO)>0){
					quantityLtrs= ProcurementNetworkServices.convertKGToLitreSetScale(sendQty,false);	
				}
				if(UtilValidate.isNotEmpty(recdQty) && recdQty.compareTo(BigDecimal.ZERO)>0){
					receivedQuantityLtrs= ProcurementNetworkServices.convertKGToLitreSetScale(recdQty,false);	
				}
				
				if(quantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(fat) && fat.compareTo(BigDecimal.ZERO)>0){
					sendKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sendQty,fat);
				}
				if(quantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(snf) && snf.compareTo(BigDecimal.ZERO)>0){
					sendKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(sendQty,snf);
				}
				if(receivedQuantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(receivedFat) && receivedFat.compareTo(BigDecimal.ZERO)>0){
					receivedKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(recdQty,receivedFat);
				}
				if(receivedQuantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(receivedSnf) && receivedSnf.compareTo(BigDecimal.ZERO)>0){
					receivedKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(recdQty,receivedSnf);
				}
				
				
				milkTransfer.set("quantity", sendQty);
				milkTransfer.set("quantityLtrs", quantityLtrs);
				milkTransfer.set("receivedQuantityLtrs", receivedQuantityLtrs);
				milkTransfer.set("statusId", "MXF_RECD");
				milkTransfer.set("receivedQuantity", recdQty);
				milkTransfer.set("receiveDate",tareDate);
				milkTransfer.set("receivedKgFat", receivedKgFat);
				milkTransfer.set("receivedKgSnf",receivedKgSnf);
				milkTransfer.set("sendKgFat", sendKgFat);
				milkTransfer.set("sendKgSnf",sendKgSnf);
				milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				//need to update TransferItem
				try{
					
					List conditionList = UtilMisc.toList(EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId));
					EntityCondition condition = EntityCondition.makeCondition(conditionList);
					List<GenericValue> itemsList = delegator.findList("MilkTransferItem",condition,null,null,null,false);
					if(UtilValidate.isEmpty(itemsList)){
						Debug.logError("Receipt Failed at QC ",module);
						resultMap = ServiceUtil.returnError("Receipt failed at QC");
						return resultMap;
					}
					GenericValue item = EntityUtil.getFirst(itemsList);
					// In KMF we have only one transfer Item
						item.set("quantity", sendQty);
						item.set("receivedQuantity", recdQty);
						item.set("receivedKgFat", receivedKgFat);
						item.set("receivedKgSnf",receivedKgSnf);
						item.set("sendKgFat", sendKgFat);
						item.set("sendKgSnf",sendKgSnf);
						item.set("quantityLtrs", quantityLtrs);
						item.set("receivedQuantityLtrs", receivedQuantityLtrs);
						item.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
						item.store();
					
				}catch(Exception e){
					Debug.logError("Error while updating actual weights "+e,module);
					resultMap= ServiceUtil.returnError("Error while updating actual weights "+e.getMessage());
					return resultMap;
				}
				
				
				
				
	 		}else if(statusId.equalsIgnoreCase("MR_VEHICLE_OUT")){
	 			String exitDateStr = (String) context.get("exitDate"); 
	 	        String exitTime = (String) context.get("exitTime");
	 	        Timestamp exitDate = UtilDateTime.nowTimestamp();
	 	        try{
	 		 		if(UtilValidate.isNotEmpty(exitDateStr)){
	 		 			if(UtilValidate.isNotEmpty(exitTime)){
	 		 				exitDateStr = exitDateStr.concat(exitTime);
	 		 			}
	 		 			exitDate = new java.sql.Timestamp(sdf.parse(exitDateStr).getTime());
	 		 		}
	 	        	
	 	        }catch(ParseException e){
	 	        	Debug.logError(e, "Cannot parse date string: " + exitDateStr, module);
	 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
		 			return resultMap;
	 	        }
	 	        updateVehStatusInMap.put("estimatedStartDate",exitDate);
	 	        updateVehStatusInMap.put("estimatedEndDate",exitDate);
	 	        try{
	 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
			 			return resultMap;
	 	        	}
	 	        }catch (GenericServiceException e) {
					// TODO: handle exception
	 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
	 	        	
				}catch(Exception e){
					Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
				}
				String statusIdCheck = (String) milkTransfer.get("statusId");
				milkTransfer.set("statusId", "MXF_RECD");
				milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
 	        	if(statusIdCheck.equalsIgnoreCase("MXF_REJECTED") || statusIdCheck.equalsIgnoreCase("MXF_APPROVED")){
					 milkTransfer.set("statusId", statusIdCheck);
		 	    }
	 		}else if(statusId.equalsIgnoreCase("MR_VEHICLE_UNLOAD")){
	 			String cipDateStr = (String) context.get("cipDate"); 
	 	        String cipTime = (String) context.get("cipTime");
	 	        String siloId = (String)context.get("silo");
	 	        String purposeTypeId = (String) context.get("purposeTypeId");
	 	        Timestamp cipDate = UtilDateTime.nowTimestamp();
	 	        try{
	 		 		if(UtilValidate.isNotEmpty(cipDateStr)){
	 		 			if(UtilValidate.isNotEmpty(cipTime)){
	 		 				cipDateStr = cipDateStr.concat(cipTime);
	 		 			}
	 		 			cipDate = new java.sql.Timestamp(sdf.parse(cipDateStr).getTime());
	 		 		}
	 	        	
	 	        }catch(ParseException e){
	 	        	Debug.logError(e, "Cannot parse date string: " + cipDateStr, module);
	 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
		 			return resultMap;
	 	        }
	 	        updateVehStatusInMap.put("estimatedStartDate",cipDate);
	 	        try{
	 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
			 			return resultMap;
	 	        	}
	 	        }catch (GenericServiceException e) {
					// TODO: handle exception
	 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
	 	        	
				}catch(Exception e){
					Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
				}
	 	    try{
	 	    	 
	 	         List<GenericValue> milkTransferItems = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);	
	 	         if(UtilValidate.isNotEmpty(milkTransferItems)){
	 	        	 for(GenericValue milkTransferItem : milkTransferItems){
	 	        		 milkTransferItem.set("siloId", siloId);
	 	        		 milkTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	 	        		 milkTransferItem.store();
	 	        	 }
	 	        	 
	 	         }	
	 	        	
	 	       }catch(Exception e){
					Debug.logError("Exception while updating the Silo Id :"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the Silo Id :"+e.getMessage());
		 			return resultMap;
				}
	 	     
	 	    try{
 	        	milkTransfer.set("purposeTypeId", purposeTypeId);
 	        	milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
 	        	milkTransfer.store();
	 	   }catch(Exception e){
				Debug.logError("Exception while updating the purpose  :"+e,module);
				resultMap = ServiceUtil.returnError("Exception while updating purpose :"+e.getMessage());
	 			return resultMap;
			}
	 			
	 	}else if(statusId.equalsIgnoreCase("MR_VEHICLE_QC")){String testDateStr = (String) context.get("testDate"); 
	        String testTime = (String) context.get("testTime");
	        String productId = (String) context.get("productId");
	        Timestamp testDate = UtilDateTime.nowTimestamp();
	        try{
		 		if(UtilValidate.isNotEmpty(testDateStr)){
		 			if(UtilValidate.isNotEmpty(testTime)){
		 				testDateStr = testDateStr.concat(testTime);
		 			}
		 			testDate = new java.sql.Timestamp(sdf.parse(testDateStr).getTime());
		 		}
	        	
	        }catch(ParseException e){
	        	Debug.logError(e, "Cannot parse date string: " + testDateStr, module);
	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
 			return resultMap;
	        }
	        updateVehStatusInMap.put("estimatedStartDate",testDate);
	        try{
	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
	 			return resultMap;
	        	}
	        }catch (GenericServiceException e) {
			// TODO: handle exception
	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
     		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
 			return resultMap;
	        	
		}catch(Exception e){
			Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
     		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
 			return resultMap;
		}
	      if(qcReject.equalsIgnoreCase("Y")){
	    	 statusId="MR_VEHICLE_QCREJECT";
	    	 updateVehStatusInMap.put("estimatedStartDate",testDate);
	    	 updateVehStatusInMap.put("statusId",statusId);
	    	try{
	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
	 			return resultMap;
	        	}
	        }catch (GenericServiceException e) {
			// TODO: handle exception
	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
     		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
 			return resultMap;
	        	
		}catch(Exception e){
			Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
     		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
 			return resultMap;
		}
	      }
	        
		String qcComments = (String) context.get("qcComments");
	        milkTransfer.set("productId",productId);
	        milkTransfer.set("fat",(BigDecimal)context.get("sendFat"));
		milkTransfer.set("snf",(BigDecimal)context.get("sendSnf"));
		milkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
		milkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
		milkTransfer.set("comments", qcComments);
		milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		GenericValue milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
		try{
			milkTransferAttribute.set("milkTransferId",milkTransferId);
			milkTransferAttribute.set("attrName","sealNumber1");
			milkTransferAttribute.set("attrValue",(String)context.get("sealNumber1"));
			milkTransferAttribute.create();
		}catch(Exception e){
			Debug.logError("Exception while Creating MilkTransferAttribute sealNumber1"+e,module);
     		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber1"+e.getMessage());
 			return resultMap;
		}	
		if(UtilValidate.isNotEmpty(context.get("sealNumber2"))){
			try{	
				milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
				milkTransferAttribute.set("milkTransferId",milkTransferId);
				milkTransferAttribute.set("attrName","sealNumber2");
				milkTransferAttribute.set("attrValue",(String)context.get("sealNumber2"));
				milkTransferAttribute.create();
			}catch(Exception e){
				Debug.logError("Exception while Creating MilkTransferAttribute sealNumber2"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber2"+e.getMessage());
	 			return resultMap;
			}
		}
		if(UtilValidate.isNotEmpty(context.get("sealNumber3"))){
			try{	
				milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
				milkTransferAttribute.set("milkTransferId",milkTransferId);
				milkTransferAttribute.set("attrName","sealNumber3");
				milkTransferAttribute.set("attrValue",(String)context.get("sealNumber3"));
				milkTransferAttribute.create();
			}catch(Exception e){
				Debug.logError("Exception while Creating MilkTransferAttribute sealNumber3"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber3"+e.getMessage());
	 			return resultMap;
			}
		}
		if(UtilValidate.isNotEmpty(context.get("sealNumber4"))){
			try{	
				milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
				milkTransferAttribute.set("milkTransferId",milkTransferId);
				milkTransferAttribute.set("attrName","sealNumber4");
				milkTransferAttribute.set("attrValue",(String)context.get("sealNumber4"));
				milkTransferAttribute.create();
			}catch(Exception e){
				Debug.logError("Exception while Creating MilkTransferAttribute sealNumber4"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber4"+e.getMessage());
	 			return resultMap;
			}
		}
		if(UtilValidate.isNotEmpty(context.get("sealNumber5"))){
			try{	
				milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
				milkTransferAttribute.set("milkTransferId",milkTransferId);
				milkTransferAttribute.set("attrName","sealNumber5");
				milkTransferAttribute.set("attrValue",(String)context.get("sealNumber5"));
				milkTransferAttribute.create();
			}catch(Exception e){
				Debug.logError("Exception while Creating MilkTransferAttribute sealNumber5"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber5"+e.getMessage());
	 			return resultMap;
			}
		}
		if(UtilValidate.isNotEmpty(context.get("sealNumber6"))){
			try{	
				milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
				milkTransferAttribute.set("milkTransferId",milkTransferId);
				milkTransferAttribute.set("attrName","sealNumber6");
				milkTransferAttribute.set("attrValue",(String)context.get("sealNumber6"));
				milkTransferAttribute.create();
			}catch(Exception e){
				Debug.logError("Exception while Creating MilkTransferAttribute sealNumber6"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber6"+e.getMessage());
	 			return resultMap;
			}
		}
		
		if(qcReject.equalsIgnoreCase("Y")){
			statusId="MXF_REJECTED";
			 milkTransfer.set("receiveDate",testDate);
 			 milkTransfer.set("statusId", statusId);
	    }
	    	context.remove("qcReject"); 
	    	context.remove("sealNumber1");
	    	context.remove("sealNumber2");
	    	context.remove("sealNumber3");
	    	context.remove("sealNumber4");
	    	context.remove("sealNumber5");
	    	context.remove("sealNumber6");
		Map milkTransferItemResult = dispatcher.runSync("createTankerReceiptItem", context);
		
	        if(ServiceUtil.isError(milkTransferItemResult)){
	        	Debug.logError("Error while storing QC details"+ServiceUtil.getErrorMessage(milkTransferItemResult),module);
	        	resultMap = ServiceUtil.returnError("Error while storing QC details");
	        	return resultMap;
	        }}else if(statusId.equalsIgnoreCase("MR_VEHICLE_CIP")){
	 			String isCipChecked = (String) context.get("isCipChecked");
	 			String cipDateStr = (String) context.get("cipDate"); 
	 	        String cipTime = (String) context.get("cipTime");
	 	        Timestamp cipDate = UtilDateTime.nowTimestamp();
	 	        try{
	 		 		if(UtilValidate.isNotEmpty(cipDateStr)){
	 		 			if(UtilValidate.isNotEmpty(cipTime)){
	 		 				cipDateStr = cipDateStr.concat(cipTime);
	 		 			}
	 		 			cipDate = new java.sql.Timestamp(sdf.parse(cipDateStr).getTime());
	 		 		}
	 	        	
	 	        }catch(ParseException e){
	 	        	Debug.logError(e, "Cannot parse date string: " + cipDateStr, module);
	 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
		 			return resultMap;
	 	        }
	 			updateVehStatusInMap.put("estimatedStartDate",cipDate);
	 	        try{
	 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
			 			return resultMap;
	 	        	}
	 	        }catch (GenericServiceException e) {
					// TODO: handle exception
	 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
	 	        	
				}catch(Exception e){
					Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
 	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
		 			return resultMap;
				}
	 			try{
	 				milkTransfer.set("isCipChecked",isCipChecked);
		 			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	 	        	milkTransfer.store();
		 	   }catch(Exception e){
					Debug.logError("Exception while updating the Cip Check  :"+e,module);
					resultMap = ServiceUtil.returnError("Exception while updating Cip Check :"+e.getMessage());
		 			return resultMap;
				}
	 		}
	 		milkTransfer.store();
	 	}catch(GenericEntityException e){
	 		Debug.logError("Error While updating the status "+e,module);
	 		resultMap = ServiceUtil.returnError("Error While updating the status "+e.getMessage());
	 		return resultMap;
	 	}
	 	catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error While updating the status:"+e,module);
	 		resultMap = ServiceUtil.returnError("Error While updating the status:"+e.getMessage());
	 		return resultMap;
	 	}
    	return resultMap;
   }
	
	public static Map<String, Object> updateReceiptVehicleTripStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Vehicle Status Updated Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	
   	 	String vehicleId = (String) context.get("vehicleId");
   	 	String sequenceNum = (String) context.get("sequenceNum");
	 	String statusId = (String) context.get("statusId");
	 	Timestamp estimatedStartDate = (Timestamp)context.get("estimatedStartDate");
	 	String replaceVehicleStatusString = (String) context.get("replaceStatusString");
	 	if(UtilValidate.isEmpty(replaceVehicleStatusString)){
	 		replaceVehicleStatusString = "MR_VEHICLE_";	
	 	}
	 	String replaceWithStatusString = (String)context.get("replaceWithStatusString");
	 	if(UtilValidate.isEmpty(replaceWithStatusString)){
	 		replaceWithStatusString ="";
	 	}
	 	try{
	 		//trying to get previoustate from StatusValidChange
	 		List<GenericValue> previousStatusList = delegator.findList("StatusValidChange",EntityCondition.makeCondition("statusIdTo",EntityOperator.EQUALS,statusId),null,null,null,false);
	 		if(UtilValidate.isEmpty(previousStatusList)){
	 			Debug.logError("Cannot update vehicleTripStatus ",module);
	 			resultMap = ServiceUtil.returnError("Cannot update VehicleTrip Status . Reason : Previous status Not found");
	 		}
	        List previousStatusIds = FastList.newInstance();
	 		previousStatusIds = EntityUtil.getFieldListFromEntityList(previousStatusList, "statusId", true);

	 		// here we are getting 
	 		List vehicleTripStatusConditionList = UtilMisc.toList(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
	 		vehicleTripStatusConditionList.add(EntityCondition.makeCondition("sequenceNum",EntityOperator.EQUALS,sequenceNum));
	 		EntityCondition vehicleTripStatusCondition = EntityCondition.makeCondition(vehicleTripStatusConditionList);
	 		List<GenericValue> previousStatusRecordList = delegator.findList("VehicleTripStatus",vehicleTripStatusCondition,null,null,null,false);

	 		EntityCondition prevStatCond = EntityCondition.makeCondition("statusId",EntityOperator.IN,previousStatusIds);
	 		List<GenericValue> prevStatRecStatusList = EntityUtil.filterByCondition(previousStatusRecordList,prevStatCond);
	 		GenericValue previousStatusRecord =EntityUtil.getFirst(prevStatRecStatusList);
	 		String previousStatusId = (String)previousStatusRecord.get("statusId");

	 		if(UtilValidate.isEmpty(previousStatusRecord)){
	 			EntityCondition prevVehicleCondition = EntityCondition.makeCondition("estimatedEndDate",EntityOperator.EQUALS,null);
	 			prevStatRecStatusList = EntityUtil.filterByCondition(previousStatusRecordList,prevVehicleCondition);
	 			
	 			Debug.logError("Cannot update to presnet status ",module);
	 			resultMap = ServiceUtil.returnError("Cannot update to present Status . Reason : Previous action is not completed");
	 			
	 			if(UtilValidate.isNotEmpty(prevStatRecStatusList)){
	 				String currentVehicleStatusId = (String)(EntityUtil.getFirst(prevStatRecStatusList)).get("statusId");
	 				try{
	 					GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId",currentVehicleStatusId),false); 
	 					Debug.logError("Cannot update to presnet status . Vehucle status is  :"+statusItem.get("description"), module);
	 					return ServiceUtil.returnError("Cannot update to presnet status . Vehucle status is :"+statusItem.get("description"));
	 					
	 				}catch(Exception e){
	 					Debug.logError("Error while getting status Description", module);
	 					return ServiceUtil.returnError("Error while getting status Description");
	 				}
	 			}
	 			
	 		}
	 		if(UtilValidate.isNotEmpty(previousStatusRecord.get("estimatedEndDate"))){
	 			Debug.logError("Cannot update present Status ",module);
	 			resultMap = ServiceUtil.returnError("Reason : Tanker is already in present status or went for next step");
	 			return resultMap;
	 		}
	 		//need to validate the dates before updating
	 		Timestamp previousEstimatedStartDate =(Timestamp) previousStatusRecord.get("estimatedStartDate");
	 		
	 		if(UtilValidate.isNotEmpty(previousEstimatedStartDate) && previousEstimatedStartDate.compareTo(estimatedStartDate)>0){
	 			Debug.logError(statusId.replace(replaceVehicleStatusString,replaceWithStatusString) +" date shold be greater than "+previousStatusId.replace(replaceVehicleStatusString,"") , module);
	 			resultMap = ServiceUtil.returnError(" :"+statusId.replace(replaceVehicleStatusString,replaceWithStatusString) +" date shold be greater than "+previousStatusId.replace(replaceVehicleStatusString,""));
	 			return resultMap;
	 		}
	 		// trying to update 
	 		previousStatusRecord.set("estimatedEndDate",estimatedStartDate);
	 		previousStatusRecord.store();
	 		context.remove("replaceVehicleStatusString");
	 		context.remove("replaceWithStatusString");
	 		// trying to create vehicleTripStatus
	 		Map vehicleStatusResultMap = dispatcher.runSync("createVehicleTripStatus", context);
	 		if(ServiceUtil.isError(vehicleStatusResultMap)){
	 			Debug.logError("Error While Creationg VehicleTrip Status"+ServiceUtil.getErrorMessage(vehicleStatusResultMap), module);
	 			resultMap = ServiceUtil.returnError("Error While Creationg VehicleTrip Status"+ServiceUtil.getErrorMessage(vehicleStatusResultMap));
	 			return resultMap;
	 		}
	 	}catch (GenericEntityException e) {
			// TODO: handle exception
	 		Debug.logError("Error while updating vehicleTripstatus"+e, module);
	 		resultMap = ServiceUtil.returnError("Error while updating vehicleTripstatus"+e.getMessage());
 			return resultMap;
		}catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError("Error while updating vehicleTripstatus"+e, module);
	 		resultMap = ServiceUtil.returnError("Error while updating vehicleTripstatus"+e.getMessage());
 			return resultMap;
		}catch(Exception e){
			Debug.logError("Error while updating vehicleTripstatus"+e, module);
	 		resultMap = ServiceUtil.returnError("Error while updating vehicleTripstatus"+e.getMessage());
 			return resultMap;
		}
	 	
	 	
	 	
	 	
    	return resultMap;
   }
// create tanker ReceiptItem
	public static Map<String, Object> createTankerReceiptItem(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map resultMap = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
		String milkTransferId = (String)context.get("milkTransferId");

		BigDecimal sendTemp = (BigDecimal)context.get("sendTemp");
        BigDecimal recdTemp = (BigDecimal)context.get("recdTemp");
        
        
        BigDecimal sendAcid = (BigDecimal)context.get("sendAcid");
        BigDecimal recdAcid = (BigDecimal)context.get("recdAcid");
        
        
        BigDecimal sendFat = (BigDecimal)context.get("sendFat");
        BigDecimal recdFat = (BigDecimal)context.get("recdFat");
        
        BigDecimal sendSnf = (BigDecimal)context.get("sendSnf");
        BigDecimal recdSnf = (BigDecimal)context.get("recdSnf");
		
        BigDecimal sendCLR = (BigDecimal)context.get("sendCLR");
        BigDecimal recdCLR = (BigDecimal)context.get("sendCLR");
        
        BigDecimal recdPH = (BigDecimal)context.get("recdPH");
        BigDecimal recdMBRT = (BigDecimal)context.get("recdMBRT");
        
        
        String sendCob = (String)context.get("sendCob");
        String recdCob = (String)context.get("recdCob");
        
        String sendNutriliser = (String)context.get("sendNutriliser");
        String recdSedimentTest = (String)context.get("recdSedimentTest");
        String recdFlavour = (String)context.get("recdSedimentTest");
		
        String productId = (String) context.get("productId");
        
        String recdOrganoLepticTest = (String) context.get("recdOrganoLepticTest");
        try{
	        GenericValue newTransferItem = delegator.makeValue("MilkTransferItem");
	        newTransferItem.set("milkTransferId",milkTransferId);
	        
	        newTransferItem.set("sendTemparature",sendTemp);
	        newTransferItem.set("receivedTemparature",recdTemp);
	        
	        newTransferItem.set("sendAcidity",sendAcid);
	        newTransferItem.set("receivedAcidity",recdAcid);
	        newTransferItem.set("recdOrganoLepticTest",recdOrganoLepticTest);
	        
	        newTransferItem.set("fat",sendFat);
	        newTransferItem.set("receivedFat",recdFat);
	        
	        newTransferItem.set("snf",sendSnf);
	        newTransferItem.set("receivedSnf",recdSnf);
	        
	        newTransferItem.set("sendCob",sendCob);
	        newTransferItem.set("receivedCob",recdCob);
	        
	        newTransferItem.set("sendLR",sendCLR);
	        newTransferItem.set("receivedLR",recdCLR);
	        
	        newTransferItem.set("sendProductId",productId);
	        newTransferItem.set("receivedProductId",productId);
	        
	        
	        newTransferItem.set("sendNutrilisers",sendNutriliser);
	        newTransferItem.set("receivedSedimentTest",recdSedimentTest);
	        newTransferItem.set("receivedPH",recdPH);
	        newTransferItem.set("receivedMBRT",recdMBRT);
	        newTransferItem.set("receivedFlavour",recdFlavour);
	        newTransferItem.set("createdByUserLogin", userLogin.get("userLoginId"));
	        newTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	        
	        delegator.setNextSubSeqId(newTransferItem,"sequenceNum",5,1);
	        delegator.create(newTransferItem);
	        
        }catch(GenericEntityException e){
        	Debug.logError("Error ,while Creating trnasferItem",module);
        	resultMap= ServiceUtil.returnError("Error ,while Creating trnasferItem");
        	return resultMap;
        }catch(Exception e){
        	Debug.logError("Unknown error ,while creating trnasferItem"+e,module);
        	resultMap= ServiceUtil.returnError("Unknown error ,while Creating trnasferItem");
        	return resultMap;
        }
		return resultMap;
	}
	
	public static String ApproveReceipts(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    HttpSession session = request.getSession();
	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	    String dateTimeReceivedStr = (String) request.getParameter("dateTimeReceived");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
	  	Timestamp dateTimeReceived = UtilDateTime.nowTimestamp();
		DateFormat givenFormatter = new SimpleDateFormat("dd:MM:yyyy hh:mm");
        DateFormat reqformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(UtilValidate.isNotEmpty(dateTimeReceivedStr)){
	        try {
	        Date givenReceiptDate = (Date)givenFormatter.parse(dateTimeReceivedStr);
	        dateTimeReceived = new java.sql.Timestamp(givenReceiptDate.getTime());
	        }catch (ParseException e) {
		  		Debug.logError(e, "Cannot parse date string: " + dateTimeReceivedStr, module);
		  	} catch (NullPointerException e) {
	  			Debug.logError(e, "Cannot parse date string: " + dateTimeReceivedStr, module);
		  	}
        }
		
	        String milkTransferId = "";
	        boolean beginTransaction = false;
			for (int i = 0; i < rowCount; i++) {
				  
				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				
				if (paramMap.containsKey("milkTransferId" + thisSuffix)) {
					milkTransferId = (String) paramMap.get("milkTransferId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing milkTransferId");
				}
				
				try{
					beginTransaction = TransactionUtil.begin();
					Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
					String shipmentId ="";
					List <GenericValue> milkTransferAndItemList = delegator.findList("MilkTransferAndMilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
					GenericValue milkTransfer = EntityUtil.getFirst(milkTransferAndItemList) ;
					
					if(UtilValidate.isNotEmpty(milkTransfer)){
						String partyIdFrom = milkTransfer.getString("partyId");
						String containerId = milkTransfer.getString("containerId");
						String productId = milkTransfer.getString("receivedProductId");
						BigDecimal quantity = milkTransfer.getBigDecimal("receivedQuantity");
						String facilityId = milkTransfer.getString("siloId");
						BigDecimal fat = milkTransfer.getBigDecimal("receivedFat");
						BigDecimal snf = milkTransfer.getBigDecimal("receivedSnf");
					
						if(UtilValidate.isNotEmpty(milkTransfer.get("receiveDate"))){
							dateTimeReceived = (Timestamp) milkTransfer.get("receiveDate");
						}
						GenericValue newEntity = delegator.makeValue("Shipment");
				        newEntity.set("estimatedShipDate", dateTimeReceived);
				        newEntity.set("shipmentTypeId", "MILK_RE_SHIPMENT");
				        newEntity.set("statusId", "GENERATED");
				        newEntity.set("vehicleId",containerId);
				        newEntity.set("partyIdFrom",partyIdFrom);
				        /*newEntity.put("primaryOrderId", milkTransferId);*/
				        newEntity.set("createdDate", nowTimeStamp);
				        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
				        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			            delegator.createSetNextSeqId(newEntity);            
			            shipmentId = (String) newEntity.get("shipmentId");
			            
			            Map<String,Object> itemInMap = FastMap.newInstance();
				        itemInMap.put("shipmentId",shipmentId);
				        itemInMap.put("userLogin",userLogin);
				        itemInMap.put("productId",productId);
				        itemInMap.put("quantity",quantity);
				        itemInMap.put("fat",fat);
				        itemInMap.put("snf",snf);
				        
				        Map resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
				        
				        if (ServiceUtil.isError(resultMap)) {
				        	Debug.logError("Problem creating shipment Item for milkTransfer :"+milkTransferId, module);
							request.setAttribute("_ERROR_MESSAGE_", "Problem creating shipment Item for milkTransfer :"+milkTransferId);	
							TransactionUtil.rollback();
					  		return "error";
				        }
			            if(UtilValidate.isEmpty(dateTimeReceived)){
			            	dateTimeReceived = nowTimeStamp;
			            }
				        Map inventoryReceiptCtx = FastMap.newInstance();
						inventoryReceiptCtx.put("userLogin", userLogin);
						inventoryReceiptCtx.put("productId", productId);
						inventoryReceiptCtx.put("snfPercent", snf);
						inventoryReceiptCtx.put("fatPercent", fat);
						
						inventoryReceiptCtx.put("datetimeReceived", dateTimeReceived);
						inventoryReceiptCtx.put("quantityAccepted", quantity);
						inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
						inventoryReceiptCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						inventoryReceiptCtx.put("ownerPartyId", "Company");
						inventoryReceiptCtx.put("facilityId", facilityId);
						inventoryReceiptCtx.put("unitCost", BigDecimal.ZERO);
						inventoryReceiptCtx.put("shipmentId", shipmentId);
						Map<String, Object> receiveInventoryResult;
						receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", inventoryReceiptCtx);
						
						if (ServiceUtil.isError(receiveInventoryResult)) {
							Debug.logWarning("There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult), module);
							request.setAttribute("_ERROR_MESSAGE_", "There was an error adding inventory: " + ServiceUtil.getErrorMessage(receiveInventoryResult));	
							TransactionUtil.rollback();
					  		return "error";
			            }
			            
						GenericValue milkTrans = delegator.findOne("MilkTransfer", UtilMisc.toMap("milkTransferId",milkTransferId), false);
						milkTrans.set("statusId", "MXF_APPROVED");
						milkTrans.set("shipmentId",shipmentId);
						milkTrans.store();
			            
					}else{
						Debug.logError("No Records Found To Process", module);
						request.setAttribute("_ERROR_MESSAGE_", "No Records Found To Process");	  		  
				  		 return "error";
					}
				}catch (Exception e) {
					// TODO: handle exception
					try {
						TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
			  		} catch (GenericEntityException e2) {
			  			Debug.logError(e, module);
						request.setAttribute("_ERROR_MESSAGE_", " Error while getting the MilkTransfer. ");
			  			return "error";
			  		}
				}
				finally {
			  		try {
			  			TransactionUtil.commit(beginTransaction);
			  		} catch (GenericEntityException e) {
			  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
			  			request.setAttribute("_ERROR_MESSAGE_", e.toString());
			  		}
			  	}
			}
			
	        request.setAttribute("_EVENT_MESSAGE_", "Milk Receipt Successfully Approved");
			
		return "success";
    }// End of the service
	
	
	/***
	 * Services for Internal Tanker Transfers
	 * 
	 */
	
	
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createMilkTankerIssueEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	//String receivedProductId = (String) context.get("receivedProductId");
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	String partyId = (String) context.get("partyId");
	 	String partyIdTo = (String) context.get("partyIdTo");
	 	String tareDateStr = (String) context.get("tareDate");
   	 	String tareTime = (String) context.get("tareTime");

	 	String vehicleStatusId = (String) context.get("vehicleStatusId");
	 	String milkTransferId = (String)context.get("milkTransferId");
		BigDecimal tareWeight = (BigDecimal)context.get("tareWeight");
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	try{
	 		//we need to check the transfer status of the give tanker
	 		Map tankerInMap = FastMap.newInstance();
	 		tankerInMap.put("tankerNo", tankerNo);
	 		tankerInMap.put("userLogin", userLogin);
	 		tankerInMap.put("reqStatusId","MXF_INIT");
	 		
	 		Map getTankerDetailsMap = getTankerRecordNumber(dctx,tankerInMap);
	 		if(ServiceUtil.isSuccess(getTankerDetailsMap) && UtilValidate.isEmpty(milkTransferId)){
	 			Debug.logError("Exisiting receipt is not completed of the tanker :"+tankerNo,module);
	 			resultMap = ServiceUtil.returnError("Exisiting receipt is not completed of the tanker :"+tankerNo);
	 			return resultMap;
	 		}
	 		// we need to create vehicle trip and vehicle trip status
	 		
	 		GenericValue newTransfer = null; 
	 		if(UtilValidate.isNotEmpty(milkTransferId)){
		 		try{
		 			newTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId",milkTransferId),false);
		 			
		 		}catch(GenericEntityException e){
		 			Debug.logError("Error while getting Transfer Details for :: "+milkTransferId,module);
		 			return ServiceUtil.returnError("Error while getting Transfer Details for :: "+milkTransferId);
		 		}
		 	}
	 		String sequenceNum = "";
	 		if(UtilValidate.isNotEmpty(newTransfer)){
	 			String existedStatus = (String) newTransfer.get("statusId");
	 			sequenceNum = (String)newTransfer.get("sequenceNum");
	 			if(UtilValidate.isNotEmpty(existedStatus) && !existedStatus.equalsIgnoreCase("MXF_INIT")){
	 				Debug.logError("This tanker is already in process . ",module);
	 				return ServiceUtil.returnError("This tanker is already in process . ");
	 			}
	 		}
	 		
	 		boolean createVehicleStatus = true;
	 		if(UtilValidate.isNotEmpty(sequenceNum)){
	 			createVehicleStatus = false;
	 		}
	 		if(createVehicleStatus){
	 			Map vehicleTripMap = FastMap.newInstance();
		 		vehicleTripMap.put("vehicleId", tankerNo);
		 		vehicleTripMap.put("partyId", partyId);
		 		vehicleTripMap.put("userLogin",userLogin);
		 		Map vehicleTripResultMap = FastMap.newInstance();
		 		vehicleTripResultMap = dispatcher.runSync("createVehicleTrip", vehicleTripMap);
		 		if(ServiceUtil.isError(vehicleTripResultMap)){
		 			Debug.logError("Error While Creating vehicleTrip :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
		 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip ");
		 			return resultMap;
		 		}
		 		sequenceNum = (String)vehicleTripResultMap.get("sequenceNum");
	 		}
	 		
	 		
	 		Timestamp tareDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(tareDateStr)){
 		 			if(UtilValidate.isNotEmpty(tareTime)){
 		 				tareDateStr = tareDateStr.concat(tareTime);
 		 			}
 		 			tareDate = new java.sql.Timestamp(sdf.parse(tareDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + tareDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
	 		
	 		// here based on status id we have to create new trip status 
	 		
	 		Map vehicleTripStatusMap = FastMap.newInstance();
	 		vehicleTripStatusMap.put("vehicleId", tankerNo);
	 		vehicleTripStatusMap.put("sequenceNum", sequenceNum);
	 		vehicleTripStatusMap.put("statusId","MR_VEHICLE_IN");
	 		if(UtilValidate.isNotEmpty(vehicleStatusId)){
	 			vehicleTripStatusMap.put("statusId",vehicleStatusId);
	 		}
	 		vehicleTripStatusMap.put("userLogin",userLogin);
	 		vehicleTripStatusMap.put("estimatedStartDate",tareDate);
	 		Map vehicleStatusResultMap = FastMap.newInstance(); 
	 		Debug.log("vehicleStatusId========"+vehicleStatusId);
	 		
	 		if(UtilValidate.isNotEmpty(vehicleStatusId) && vehicleStatusId.equalsIgnoreCase("MR_ISSUE_INIT")){
	 			Debug.log("Hello==========="+vehicleTripStatusMap);
	 			try{
		 			vehicleStatusResultMap = dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusMap);
			 		if(ServiceUtil.isError(vehicleStatusResultMap)){
			 			Debug.logError("Error While Creating vehicleTripStatus :: "+ServiceUtil.getErrorMessage(vehicleStatusResultMap),module);
			 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip Status");
			 			return resultMap;
			 		}	
	 			}catch(GenericServiceException e){
	 				Debug.logError("Error while creating vehicleTripStatus ::"+e,module);
	 				return ServiceUtil.returnError("Error while creating vehicleTripStatus ::"+e.getMessage());
	 			}
	 					
	 			
	 		}else{
	 			Debug.log("CAse ==========="+vehicleTripStatusMap);
	 			try{
	 				String replaceVehicleStatusString = "MR_ISSUE_";
	 				vehicleTripStatusMap.put("replaceVehicleStatusString", replaceVehicleStatusString);
		 			vehicleStatusResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", vehicleTripStatusMap);
			 		if(ServiceUtil.isError(vehicleStatusResultMap)){
			 			Debug.logError("Error While updating vehicleTripStatus :: "+vehicleStatusResultMap,module);
			 			resultMap = ServiceUtil.returnError("Error while updating vehicle Trip Status"+ServiceUtil.getErrorMessage(vehicleStatusResultMap));
			 			return resultMap;
			 		}	
	 			}catch(GenericServiceException e){
	 				Debug.logError("Error while updating vehicleTripStatus ::"+e,module);
	 				return ServiceUtil.returnError("Error while updating vehicleTripStatus ::"+e.getMessage());
	 			}
	 			
	 		}
	 		
	 		
	 		//creating vehicle trip
	 		
	 		if(UtilValidate.isEmpty(newTransfer)){
	 			newTransfer = delegator.makeValue("MilkTransfer");
	 		}
	 		newTransfer.set("containerId", tankerNo);
	 		newTransfer.set("sequenceNum", sequenceNum);
	 		//newTransfer.set("tareDate", tareDate);
	 		newTransfer.set("partyId", partyId);
	 		newTransfer.set("statusId", "MXF_INPROCESS");
	 		if(UtilValidate.isNotEmpty(vehicleStatusId) && vehicleStatusId.equalsIgnoreCase("MR_ISSUE_INIT")){
	 			newTransfer.set("statusId", "MXF_INIT");
 			}else{
 				newTransfer.set("tareWeight", tareWeight);
 			}
	 		newTransfer.set("partyIdTo", partyIdTo);
	 		newTransfer.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		newTransfer.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		if(UtilValidate.isEmpty(milkTransferId)){
	 			delegator.createSetNextSeqId(newTransfer);
	 		}else{
	 			delegator.createOrStore(newTransfer);
	 		}
	 		resultMap = ServiceUtil.returnSuccess("Successfully Created Tanker Receipt with Record Number :"+newTransfer.get("milkTransferId"));
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating record==========="+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating record==========="+e.getMessage());
		}
    	return resultMap;
   }

	public static Map<String,Object> updateInternalMilkTransfer(DispatchContext dctx,Map<String,? extends Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Updated Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String tankerNo = (String) context.get("tankerNo");
	 	String statusId = (String) context.get("statusId");
	 	String milkTransferId = (String) context.get("milkTransferId");
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	
		String replaceVehicleStatusString = "MR_ISSUE_";
		String replaceWithStatusString = "";
		String vehicleStatusId = (String) context.get("vehicleStatusId");
		GenericValue milkTransfer = null;
    	try{
    		milkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", milkTransferId),false);
    	}catch(Exception e){
    		Debug.logError("Error while getting Transfer Details"+e, module);
    		return ServiceUtil.returnError("Error while getting Transfer Details "+e.getMessage());
    	}
    	if(UtilValidate.isEmpty(milkTransfer)){
    		Debug.logError("Milk Transfer not Found with Id :"+milkTransferId, module);
    		return ServiceUtil.returnError("Milk Transfer not Found with Id :"+milkTransferId);
    	}
    	String sequenceNum = (String) milkTransfer.get("sequenceNum");
    	
    	Map updateVehStatusInMap = FastMap.newInstance();
    	updateVehStatusInMap.put("userLogin", userLogin);
    	updateVehStatusInMap.put("statusId", vehicleStatusId);
    	updateVehStatusInMap.put("vehicleId", tankerNo);
    	updateVehStatusInMap.put("sequenceNum", sequenceNum);
    	updateVehStatusInMap.put("replaceVehicleStatusString", replaceVehicleStatusString);
    	Map updateVehStatResultMap = FastMap.newInstance();
		if(vehicleStatusId.equalsIgnoreCase("MR_ISSUE_CIP")){
 			String cipDateStr = (String) context.get("cipDate"); 
 	        String cipTime = (String) context.get("cipTime");
 	        Timestamp cipDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(cipDateStr)){
 		 			if(UtilValidate.isNotEmpty(cipTime)){
 		 				cipDateStr = cipDateStr.concat(cipTime);
 		 			}
 		 			cipDate = new java.sql.Timestamp(sdf.parse(cipDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + cipDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",cipDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        	resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
 	        try{
 	        	delegator.store(milkTransfer);
 	        }catch(Exception e){
 	        	Debug.logError("Error While "+e,module);
	        	resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
 	        }
		}else if(vehicleStatusId.equalsIgnoreCase("MR_ISSUE_QC")){
			String testDateStr = (String) context.get("testDate"); 
 	        String testTime = (String) context.get("testTime");
 	        String productId = (String) context.get("productId");
 	        Timestamp testDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(testDateStr)){
 		 			if(UtilValidate.isNotEmpty(testTime)){
 		 				testDateStr = testDateStr.concat(testTime);
 		 			}
 		 			testDate = new java.sql.Timestamp(sdf.parse(testDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + testDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",testDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
			String qcComments = (String) context.get("qcComments");
 	        milkTransfer.set("productId",productId);
 	        milkTransfer.set("fat",(BigDecimal)context.get("sendFat"));
			milkTransfer.set("snf",(BigDecimal)context.get("sendSnf"));
			milkTransfer.set("sendLR",(BigDecimal)context.get("sendCLR"));
			//milkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
			//milkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			//milkTransfer.set("comments", qcComments);
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			
			//here we need to add transferItem
		    List<GenericValue> milkTransferItemsList = FastList.newInstance();
	        try{
				milkTransferItemsList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
			}catch(Exception e){
				Debug.logError("Error while getting transferItems List ::"+e,module);
				return ServiceUtil.returnError("Error while getting transferItems List ::"+e.getMessage());
			}
	        GenericValue MilkTransferItem = null;
			if(UtilValidate.isNotEmpty(milkTransferItemsList)){
				MilkTransferItem = EntityUtil.getFirst(milkTransferItemsList);
			}
			try{

				MilkTransferItem.set("milkTransferId",milkTransferId);
				MilkTransferItem.set("fat",(BigDecimal)context.get("sendFat"));
				MilkTransferItem.set("snf",(BigDecimal)context.get("sendSnf"));
				MilkTransferItem.set("sendTemparature",(BigDecimal)context.get("sendTemp"));
				MilkTransferItem.set("sendAcidity",(BigDecimal)context.get("sendAcid"));
				
				MilkTransferItem.set("sendLR",(BigDecimal)context.get("sendCLR"));
				MilkTransferItem.set("sendCob",(String)context.get("sendCob"));
				MilkTransferItem.set("sendOrganoLepticTest",(String)context.get("sendOrganoLepticTest"));
				MilkTransferItem.set("sendSedimentTest",(String)context.get("sendSedimentTest"));
				
				MilkTransferItem.set("createdByUserLogin", userLogin.get("userLoginId"));
				MilkTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				
				delegator.store(MilkTransferItem);
			}catch(Exception e){
				Debug.logError("Error while storing qc details "+e,module);
				return ServiceUtil.returnError("Error while storing qc details "+e.getMessage());
			}
			try{
				delegator.store(milkTransfer);
			}catch(Exception e){
				Debug.logError("Error occured ::"+e,module);
				return ServiceUtil.returnError("Error occured ::"+e.getMessage());
			}
			
		}else if(vehicleStatusId.equalsIgnoreCase("MR_ISSUE_GRWEIGHT")){
			//Long numberOfCells = (Long)context.get("numberOfCells");
 			BigDecimal grossWeight = (BigDecimal)context.get("grossWeight");
 			BigDecimal dispatchWeight = (BigDecimal)context.get("dispatchWeight");
 			String grossDateStr = (String) context.get("grossDate"); 
 	        String grossTime = (String) context.get("grossTime");
 	        Timestamp grossDate = UtilDateTime.nowTimestamp();
 	        String driverName = (String) context.get("driverName");
 	        String purposeTypeId = (String) context.get("purposeTypeId");
 	        try{
 		 		if(UtilValidate.isNotEmpty(grossDateStr)){
 		 			if(UtilValidate.isNotEmpty(grossTime)){
 		 				grossDateStr = grossDateStr.concat(grossTime);
 		 			}
 		 			grossDate = new java.sql.Timestamp(sdf.parse(grossDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + grossDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",grossDate);
 	        if(UtilValidate.isNotEmpty(purposeTypeId)&& purposeTypeId.equalsIgnoreCase("INTERNALUSE")){
 	        	updateVehStatusInMap.put("estimatedEndDate",grossDate);
 	        }
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
			}
 	       
 	       GenericValue MilkTransferItem = null;
 	       List<GenericValue> milkTransferItemsList = FastList.newInstance();
 			try{
 				milkTransferItemsList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
 			}catch(Exception e){
 				Debug.logError("Error while getting transferItems List ::"+e,module);
 				return ServiceUtil.returnError("Error while getting transferItems List ::"+e.getMessage());
 			}
 			
 			if(UtilValidate.isNotEmpty(milkTransferItemsList)){
 				MilkTransferItem = EntityUtil.getFirst(milkTransferItemsList);
 			}
 			BigDecimal sendFat = (BigDecimal) milkTransfer.get("fat");
 			BigDecimal sendSnf = (BigDecimal) milkTransfer.get("snf");
 			
 			BigDecimal tareWeight = (BigDecimal) milkTransfer.get("tareWeight");
 		//	BigDecimal grossWeight = (BigDecimal) milkTransfer.get("grossWeight");
 			
 			BigDecimal sendKgFat = BigDecimal.ZERO;
 			BigDecimal sendKgSnf = BigDecimal.ZERO;
 			
 			if(UtilValidate.isNotEmpty(tareWeight) && UtilValidate.isNotEmpty(grossWeight)){
 				BigDecimal qtyKgs = grossWeight.subtract(tareWeight);
 				BigDecimal qtyLtrs = ProcurementNetworkServices.convertKGToLitre(qtyKgs);
 				sendKgFat =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,sendFat));
 				sendKgSnf =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,sendSnf));
 				
 				milkTransfer.set("quantity",qtyKgs);
 				milkTransfer.set("receivedQuantity",qtyKgs);
 				milkTransfer.set("quantityLtrs",qtyLtrs);
 				milkTransfer.set("receivedQuantityLtrs",qtyLtrs);
 				milkTransfer.set("sendDate",grossDate);

 				milkTransfer.set("sendKgFat",sendKgFat);
 				milkTransfer.set("sendKgSnf",sendKgSnf);
 				milkTransfer.set("receivedKgFat",sendKgFat);
 				milkTransfer.set("receivedKgSnf",sendKgSnf);
 				
 				MilkTransferItem.set("quantity",qtyKgs);
 				MilkTransferItem.set("receivedQuantity",qtyKgs);
 				MilkTransferItem.set("quantityLtrs",qtyLtrs);
 				MilkTransferItem.set("receivedQuantityLtrs",qtyLtrs);
 				
 				MilkTransferItem.set("sendKgFat",sendKgFat);
 				MilkTransferItem.set("sendKgSnf",sendKgSnf);
 				MilkTransferItem.set("receivedKgFat",sendKgFat);
 				MilkTransferItem.set("receivedKgSnf",sendKgSnf);
 				MilkTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
 			}
 			milkTransfer.set("receivedFat",sendFat);
 			milkTransfer.set("receivedSnf",sendSnf);
 			milkTransfer.set("purposeTypeId", purposeTypeId);
 			
 			String shipmentId = (String)milkTransfer.get("shipmentId");
 			if(UtilValidate.isEmpty(shipmentId)){
 				//here we have to create shipment
 				try{
 					String partyIdFrom = (String)milkTransfer.get("partyId");
 					String productId = (String)milkTransfer.get("productId");
 					GenericValue newEntity = delegator.makeValue("Shipment");
			        newEntity.set("estimatedShipDate", grossDate);
			        
			        if(UtilValidate.isNotEmpty(purposeTypeId) ){
			        	if(purposeTypeId.equalsIgnoreCase("INTERNALUSE")){
			        		newEntity.set("shipmentTypeId", "PRODUCTION_SHIPMENT");
			        	}else if(purposeTypeId.equalsIgnoreCase("COPACKING")){
			        		newEntity.set("shipmentTypeId", "MILK_COPACK_SHIPMENT");
			        	}else{
			        		newEntity.set("shipmentTypeId", "MILK_OUT_SHIPMENT");
			        	}
		 	        }
			        newEntity.set("statusId", "GENERATED");
			        newEntity.set("vehicleId",tankerNo);
			        newEntity.set("partyIdFrom",partyIdFrom);
			        /*newEntity.put("primaryOrderId", milkTransferId);*/
			        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
			        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
			        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		            delegator.createSetNextSeqId(newEntity);            
		            shipmentId = (String) newEntity.get("shipmentId");
		            Map<String,Object> itemInMap = FastMap.newInstance();
			        itemInMap.put("shipmentId",shipmentId);
			        itemInMap.put("userLogin",userLogin);
			        itemInMap.put("productId",productId);
			        itemInMap.put("quantity",grossWeight.subtract(tareWeight));
			        itemInMap.put("fat",sendFat);
			        itemInMap.put("snf",sendSnf);
			        resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
			        if (ServiceUtil.isError(resultMap)) {
			        	Debug.logError("Problem creating shipment Item for milkTransfer :"+milkTransferId, module);
						return ServiceUtil.returnError("Problem creating shipment Item for milkTransfer :"+milkTransferId);	
			        }
			        BigDecimal shipQty = grossWeight.subtract(tareWeight);
			        String shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");
					
					List<GenericValue> receiptInventoryItems = FastList.newInstance();
					List invConditionList = FastList.newInstance();
					invConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					invConditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		            invConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, (String)MilkTransferItem.get("siloId")));
		            EntityCondition invCondition = EntityCondition.makeCondition(invConditionList, EntityOperator.AND);
		            
		            try{
			            receiptInventoryItems= delegator.findList("InventoryItem", invCondition, null, UtilMisc.toList("datetimeReceived"), null, false);
	 				}catch(GenericEntityException e){
	 					Debug.logError("Error while getting inventory Detail "+e,module);
	 					return ServiceUtil.returnError("Error while getting inventory Detail");
	 				}	
					BigDecimal atpQty = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(receiptInventoryItems)){
						for(GenericValue inventoryItem : receiptInventoryItems){
							BigDecimal atpTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
							atpQty = atpQty.add(atpTotal);
						}
					}
					if(atpQty.compareTo(shipQty)<0){
						Debug.logError("You cannot make this transfer from this Silo .Available quantity is less than shipping quantity. ",module);
						return ServiceUtil.returnError("You cannot make this transfer.Available quantity is less than shipping quantity. ");
					}
					String custRequestId = (String)milkTransfer.get("custRequestId");
					String custRequestItemSeqId = (String)milkTransfer.get("custRequestItemSeqId");
					BigDecimal toBeIssuedQty = shipQty;
					for(GenericValue inventoryItem : receiptInventoryItems){
						BigDecimal issueQty = BigDecimal.ZERO;
						if(toBeIssuedQty.compareTo(BigDecimal.ZERO) ==0 ){
							break;
						}
						BigDecimal atpTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
						String inventoryItemId = (String) inventoryItem.get("inventoryItemId");
						if(toBeIssuedQty.compareTo(atpTotal)<=0){
							issueQty = toBeIssuedQty;
							toBeIssuedQty = BigDecimal.ZERO;
						}else{
							issueQty = atpTotal; 
							toBeIssuedQty = toBeIssuedQty.subtract(atpTotal); 
						}
						
						Map itemIssueCtx = FastMap.newInstance();
				        itemIssueCtx.put("userLogin", userLogin);
						itemIssueCtx.put("shipmentId", shipmentId);
						itemIssueCtx.put("shipmentItemSeqId", shipmentItemSeqId);
						itemIssueCtx.put("productId", productId);
						itemIssueCtx.put("quantity", issueQty);
						itemIssueCtx.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
						itemIssueCtx.put("modifiedByUserLoginId", userLogin.getString("userLoginId"));
						itemIssueCtx.put("modifiedDateTime", UtilDateTime.nowTimestamp());
						itemIssueCtx.put("snfPercent", sendSnf);
						itemIssueCtx.put("fatPercent",sendFat);
						itemIssueCtx.put("facilityId", (String)MilkTransferItem.get("siloId"));
						itemIssueCtx.put("inventoryItemId", inventoryItemId);
						if(UtilValidate.isNotEmpty(custRequestId) ){
							itemIssueCtx.put("custRequestId",custRequestId);
							itemIssueCtx.put("custRequestItemSeqId", custRequestItemSeqId);
						}
						
						resultMap.remove("shipmentItemSeqId");
						
						String itemIssuanceId ="" ;
						Map resultCtx = FastMap.newInstance();
						try{
							 resultCtx = dispatcher.runSync("createItemIssuance", itemIssueCtx);
							if (ServiceUtil.isError(resultCtx)) {
								Debug.logError("Problem creating item issuance for requested item", module);
								return resultCtx;
							}
							itemIssuanceId = (String)resultCtx.get("itemIssuanceId") ;
					   	}catch (Exception e) {
					 		Debug.logError(e, "Error While Creating Item Issuance !", module);
					 		return ServiceUtil.returnError("Failed to create a Item Issuance " + e);	  
					  	}
						
						
						
						
						
						try{
							Map createInvDetail = FastMap.newInstance();
							createInvDetail.put("userLogin", userLogin);
							createInvDetail.put("inventoryItemId", inventoryItemId);
							createInvDetail.put("itemIssuanceId", itemIssuanceId);
							createInvDetail.put("quantityOnHandDiff", issueQty.negate());
							createInvDetail.put("availableToPromiseDiff", issueQty.negate());
							resultCtx = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
							if (ServiceUtil.isError(resultCtx)) {
								Debug.logError("Problem decrementing inventory for requested item ", module);
								return resultCtx;
							}
						}catch(Exception e){
							Debug.logError("Error while decrementing inventory :"+e,module);
							return ServiceUtil.returnError("Error while decrementing inventory :"+e.getMessage());
						}
					}
					try{
					
					// Here we are updating to respect floor 
					String partyIdTo = (String) milkTransfer.get("partyIdTo");
					//here we need to consider party role 
					String floorId = "";
					
					
					if(purposeTypeId.equalsIgnoreCase("INTERNALUSE") && UtilValidate.isEmpty(custRequestId)){
						if(UtilValidate.isNotEmpty(partyIdTo)){
							try{
								List floorConditionList = UtilMisc.toList(EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyIdTo));
								floorConditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT"));
								EntityCondition floorcond = EntityCondition.makeCondition(floorConditionList);
								
								List<GenericValue> floorsList = delegator.findList("Facility",floorcond,null,null,null,false);
								GenericValue floorDetails = EntityUtil.getFirst(floorsList);
								if(UtilValidate.isNotEmpty(floorDetails)){
									floorId = (String) floorDetails.get("facilityId");
								}
							}catch(GenericEntityException e){
								Debug.logError("Error while getting floor details ::"+e,module);
								return ServiceUtil.returnError("Error while getting floor details ::"+e.getMessage());
							}
						}
						if(UtilValidate.isEmpty(floorId)){
							Debug.logError("Error while updating receiver end. received floor not found :",module);
							return ServiceUtil.returnError("Error while updating receiver end. received floor not found :");
						}
						BigDecimal quantity = (BigDecimal)milkTransfer.get("quantity");
						Map inventoryReceiptCtx = FastMap.newInstance();
						inventoryReceiptCtx.put("userLogin", userLogin);
						inventoryReceiptCtx.put("productId", productId);
						inventoryReceiptCtx.put("snfPercent", sendSnf);
						inventoryReceiptCtx.put("fatPercent",sendFat);
						inventoryReceiptCtx.put("datetimeReceived", grossDate);
						inventoryReceiptCtx.put("quantityAccepted", quantity);
						inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
						inventoryReceiptCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						inventoryReceiptCtx.put("ownerPartyId", "Company");
						inventoryReceiptCtx.put("facilityId", floorId);
						inventoryReceiptCtx.put("unitCost", BigDecimal.ZERO);
						inventoryReceiptCtx.put("shipmentId", shipmentId);
						Map<String, Object> receiveInventoryResult;
						try{
							receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", inventoryReceiptCtx);
						}catch(GenericServiceException e){
							Debug.logError("Error while updating receiver inventory ::"+e, module);
							return ServiceUtil.returnError("Error while updating receiver inventory ::"+e.getMessage());
						}
					}else{
						
						// here we are changing the custRequest Item status to issued
						if(UtilValidate.isNotEmpty(custRequestId)){
								try{
									Map itemStatusCtx = FastMap.newInstance();
									itemStatusCtx.put("custRequestId", custRequestId);
									itemStatusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
									itemStatusCtx.put("userLogin", userLogin);
									itemStatusCtx.put("description", "");
									itemStatusCtx.put("statusId", "CRQ_ISSUED");
									Map crqResultCtx = dispatcher.runSync("setCustRequestItemStatus", itemStatusCtx);
									if (ServiceUtil.isError(crqResultCtx)) {
										Debug.logError("Problem changing status for requested item ", module);
										return crqResultCtx;
									}
								}catch(GenericServiceException e){
									Debug.logError("Error while updating indent item status :"+e,module);
									return ServiceUtil.returnError("Error while updating indent item status :");
								}
						}
					}
					
					if(purposeTypeId.equalsIgnoreCase("INTERNALUSE")){
						milkTransfer.set("statusId", "MXF_RECD");
					}else{
						milkTransfer.set("statusId", "MXF_INPROCESS");
					}
					milkTransfer.set("shipmentId",shipmentId);
					delegator.store(milkTransfer);
 				}catch(Exception e){
 					Debug.logError("Error while creating shipment. "+e,module);
 					return ServiceUtil.returnError("Error while creating shipment. "+e.getMessage());
 				}
 			}catch(Exception e){
 				Debug.logError("Error while updating gross weight details :"+e, module);
 				}
 			}
 			//here we need to create transferItem
 			try{
 				MilkTransferItem.set("milkTransferId",milkTransferId);
 				MilkTransferItem.set("receivedFat",sendFat);
 				MilkTransferItem.set("receivedSnf",sendSnf);
 				milkTransfer.set("receiveDate",grossDate);
 				MilkTransferItem.set("receivedTemparature",(BigDecimal) MilkTransferItem.get("sendTemparature"));
 				MilkTransferItem.set("receivedAcidity",(BigDecimal) MilkTransferItem.get("sendAcidity"));
 				
 				MilkTransferItem.set("receivedLR",(BigDecimal) MilkTransferItem.get("sendLR"));
 				MilkTransferItem.set("receivedCob",(String) MilkTransferItem.get("sendCob"));
 				//MilkTransferItem.set("recdOrganoLepticTest",(String)MilkTransferItem.get("sendOrganoLepticTest"));
 				//MilkTransferItem.set("receivedSedimentTest",(String)MilkTransferItem.get("sendSedimentTest"));
 				
 				delegator.store(MilkTransferItem);
 			}catch(Exception e){
 				Debug.logError("Error while updating qc details "+e,module);
 				return ServiceUtil.returnError("Error while updating qc details "+e.getMessage());
 			}
 	        //  String sealCheck = (String) context.get("sealCheck");
			milkTransfer.set("dispatchWeight", dispatchWeight);
			milkTransfer.set("grossWeight", grossWeight);
			//milkTransfer.set("numberOfCells", numberOfCells);
			/*if(UtilValidate.isNotEmpty(sealCheck)){
				milkTransfer.set("isSealChecked",sealCheck);
			}*/
			milkTransfer.set("driverName", driverName);
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			try{
				delegator.store(milkTransfer);
			}catch(Exception e){
				Debug.logError ("Error while storing grossweight details ::"+e,module);
				return ServiceUtil.returnError("Error while storing grossweight details ::"+e.getMessage());
			}
			
			// here we are going to create milkTransfer Attribute for storing seal number
			if(UtilValidate.isNotEmpty(context.get("sealNumber1"))){
				GenericValue milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
				try{
					milkTransferAttribute.set("milkTransferId",milkTransferId);
					milkTransferAttribute.set("attrName","sealNumber1");
					milkTransferAttribute.set("attrValue",(String)context.get("sealNumber1"));
					milkTransferAttribute.create();
				}catch(Exception e){
					Debug.logError("Exception while Creating MilkTransferAttribute sealNumber1"+e,module);
		     		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber1"+e.getMessage());
		 			return resultMap;
				}	
				if(UtilValidate.isNotEmpty(context.get("sealNumber2"))){
					try{	
						milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
						milkTransferAttribute.set("milkTransferId",milkTransferId);
						milkTransferAttribute.set("attrName","sealNumber2");
						milkTransferAttribute.set("attrValue",(String)context.get("sealNumber2"));
						milkTransferAttribute.create();
					}catch(Exception e){
						Debug.logError("Exception while Creating MilkTransferAttribute sealNumber2"+e,module);
			        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber2"+e.getMessage());
			 			return resultMap;
					}
				}
				if(UtilValidate.isNotEmpty(context.get("sealNumber3"))){
					try{	
						milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
						milkTransferAttribute.set("milkTransferId",milkTransferId);
						milkTransferAttribute.set("attrName","sealNumber3");
						milkTransferAttribute.set("attrValue",(String)context.get("sealNumber3"));
						milkTransferAttribute.create();
					}catch(Exception e){
						Debug.logError("Exception while Creating MilkTransferAttribute sealNumber3"+e,module);
			        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber3"+e.getMessage());
			 			return resultMap;
					}
				}
				if(UtilValidate.isNotEmpty(context.get("sealNumber4"))){
					try{	
						milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
						milkTransferAttribute.set("milkTransferId",milkTransferId);
						milkTransferAttribute.set("attrName","sealNumber4");
						milkTransferAttribute.set("attrValue",(String)context.get("sealNumber4"));
						milkTransferAttribute.create();
					}catch(Exception e){
						Debug.logError("Exception while Creating MilkTransferAttribute sealNumber4"+e,module);
			        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber4"+e.getMessage());
			 			return resultMap;
					}
				}
				if(UtilValidate.isNotEmpty(context.get("sealNumber5"))){
					try{	
						milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
						milkTransferAttribute.set("milkTransferId",milkTransferId);
						milkTransferAttribute.set("attrName","sealNumber5");
						milkTransferAttribute.set("attrValue",(String)context.get("sealNumber5"));
						milkTransferAttribute.create();
					}catch(Exception e){
						Debug.logError("Exception while Creating MilkTransferAttribute sealNumber5"+e,module);
			        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber5"+e.getMessage());
			 			return resultMap;
					}
				}
				if(UtilValidate.isNotEmpty(context.get("sealNumber6"))){
					try{	
						milkTransferAttribute = delegator.makeValue("MilkTransferAttribute");
						milkTransferAttribute.set("milkTransferId",milkTransferId);
						milkTransferAttribute.set("attrName","sealNumber6");
						milkTransferAttribute.set("attrValue",(String)context.get("sealNumber6"));
						milkTransferAttribute.create();
					}catch(Exception e){
						Debug.logError("Exception while Creating MilkTransferAttribute sealNumber6"+e,module);
			        		resultMap = ServiceUtil.returnError("Exception while Creating MilkTransferAttribute sealNumber6"+e.getMessage());
			 			return resultMap;
					}
				}
			}
			
		}else if(vehicleStatusId.equalsIgnoreCase("MR_ISSUE_LOAD")){
		
			String loadDateStr = (String) context.get("loadDate"); 
	        String loadTime = (String) context.get("loadTime");
	        String dcNo = (String) context.get("dcNo");
	        String productId = (String) context.get("productId");
	        String silo = (String) context.get("silo");

	        Timestamp loadDate = UtilDateTime.nowTimestamp();
	        
	        try{
		 		if(UtilValidate.isNotEmpty(loadDateStr)){
		 			if(UtilValidate.isNotEmpty(loadTime)){
		 				loadDateStr = loadDateStr.concat(loadTime);
		 			}
		 			loadDate = new java.sql.Timestamp(sdf.parse(loadDateStr).getTime());
		 		}
	        	
	        }catch(ParseException e){
	        	Debug.logError(e, "Cannot parse date string: " + loadDateStr, module);
	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
 			return resultMap;
	        }
	        updateVehStatusInMap.put("estimatedStartDate",loadDate);
	        try{
	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
	        	if(ServiceUtil.isError(updateVehStatResultMap)){
	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
	 			return resultMap;
	        	}
	        }catch (GenericServiceException e) {
			// TODO: handle exception
	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
 			return resultMap;
	        	
	        }catch(Exception e){
	        	Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
        		return resultMap;
	        }
	        milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	        try{
		        GenericValue newTransferItem = delegator.makeValue("MilkTransferItem");
		        newTransferItem.set("milkTransferId",milkTransferId);
		        newTransferItem.set("siloId",silo);
		        newTransferItem.set("createdByUserLogin", userLogin.get("userLoginId"));
		        newTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        
		        delegator.setNextSubSeqId(newTransferItem,"sequenceNum",5,1);
		        delegator.create(newTransferItem);
		        
	        }catch(GenericEntityException e){
	        	Debug.logError("Error ,while Creating trnasferItem",module);
	        	resultMap= ServiceUtil.returnError("Error ,while Creating trnasferItem");
	        	return resultMap;
	        }catch(Exception e){
	        	Debug.logError("Unknown error ,while creating trnasferItem"+e,module);
	        	resultMap= ServiceUtil.returnError("Unknown error ,while Creating trnasferItem");
	        	return resultMap;
	        }

	        
	        
			milkTransfer.set("dcNo", dcNo);
			milkTransfer.set("productId", productId);
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			String purposeTypeId   = (String) context.get("purposeTypeId");
			if(UtilValidate.isNotEmpty(purposeTypeId)){
				milkTransfer.set("purposeTypeId", purposeTypeId);
			}
	        try{
	        	delegator.store(milkTransfer);
	        }catch(Exception e){
	        	Debug.logError ("Error while Loading milk details ::"+e,module);
	        	return ServiceUtil.returnError("Error while Loading milk details ::"+e.getMessage());
	        }
		}else if(vehicleStatusId.equalsIgnoreCase("MR_ISSUE_AQC")){
			replaceVehicleStatusString = "MR_ISSUE_AQC";
			replaceWithStatusString = "ACK QC";
			
			
			updateVehStatusInMap.put("replaceWithStatusString",replaceWithStatusString);
			updateVehStatusInMap.put("replaceVehicleStatusString",replaceVehicleStatusString);
			
			String testDateStr = (String) context.get("testDate"); 
 	        String testTime = (String) context.get("testTime");
 	        String productId = (String) context.get("productId");
 	        Timestamp testDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(testDateStr)){
 		 			if(UtilValidate.isNotEmpty(testTime)){
 		 				testDateStr = testDateStr.concat(testTime);
 		 			}
 		 			testDate = new java.sql.Timestamp(sdf.parse(testDateStr).getTime());
 		 		}
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + testDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",testDate);
 	        updateVehStatusInMap.put("estimatedEndDate",testDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
 	       GenericValue MilkTransferItem = null;
 	      List<GenericValue> milkTransferItemsList = FastList.newInstance();
			try{
				milkTransferItemsList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
			}catch(Exception e){
				Debug.logError("Error while getting transferItems List ::"+e,module);
				return ServiceUtil.returnError("Error while getting transferItems List ::"+e.getMessage());
			}
			
			if(UtilValidate.isNotEmpty(milkTransferItemsList)){
				MilkTransferItem = EntityUtil.getFirst(milkTransferItemsList);
			}
			String qcComments = (String) context.get("qcComments");
 	        milkTransfer.set("receiveDate",testDate);
			milkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
			milkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			
			BigDecimal recdFat = (BigDecimal) milkTransfer.get("receivedFat");
			BigDecimal recdSnf = (BigDecimal) milkTransfer.get("receivedSnf");
			
			BigDecimal sendFat = (BigDecimal) milkTransfer.get("fat");
			BigDecimal sendSnf = (BigDecimal) milkTransfer.get("snf");
			
			BigDecimal tareWeight = (BigDecimal) milkTransfer.get("tareWeight");
			BigDecimal grossWeight = (BigDecimal) milkTransfer.get("grossWeight");
			
			BigDecimal sendKgFat = BigDecimal.ZERO;
			BigDecimal sendKgSnf = BigDecimal.ZERO;
			BigDecimal recdKgFat = BigDecimal.ZERO;
			BigDecimal recdKgSnf = BigDecimal.ZERO;
			
			if(UtilValidate.isNotEmpty(tareWeight) && UtilValidate.isNotEmpty(grossWeight)){
				BigDecimal qtyKgs = grossWeight.subtract(tareWeight);
				BigDecimal qtyLtrs = ProcurementNetworkServices.convertKGToLitre(qtyKgs);
				sendKgFat =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,sendFat));
				sendKgSnf =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,sendSnf));
				recdKgFat =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,recdFat));
				recdKgSnf =((BigDecimal)ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,recdSnf));
				
				milkTransfer.set("quantity",qtyKgs);
				milkTransfer.set("receivedQuantity",qtyKgs);
				milkTransfer.set("quantityLtrs",qtyLtrs);
				milkTransfer.set("receivedQuantityLtrs",qtyLtrs);
				
				milkTransfer.set("sendKgFat",sendKgFat);
				milkTransfer.set("sendKgSnf",sendKgSnf);
				milkTransfer.set("receivedKgFat",recdKgFat);
				milkTransfer.set("receivedKgSnf",recdKgSnf);
				
				MilkTransferItem.set("quantity",qtyKgs);
				MilkTransferItem.set("receivedQuantity",qtyKgs);
				MilkTransferItem.set("quantityLtrs",qtyLtrs);
				MilkTransferItem.set("receivedQuantityLtrs",qtyLtrs);
				
				MilkTransferItem.set("sendKgFat",sendKgFat);
				MilkTransferItem.set("sendKgSnf",sendKgSnf);
				MilkTransferItem.set("receivedKgFat",recdKgFat);
				MilkTransferItem.set("receivedKgSnf",recdKgSnf);
				
				MilkTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				
				
			}
			
			milkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
			milkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			
			milkTransfer.set("comments", qcComments);
			milkTransfer.set("statusId", "MXF_RECD");
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			
			//here we need to create transferItem
			
			try{
				
				MilkTransferItem.set("milkTransferId",milkTransferId);
				MilkTransferItem.set("receivedFat",(BigDecimal)context.get("recdFat"));
				MilkTransferItem.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
				MilkTransferItem.set("receivedTemparature",(BigDecimal)context.get("recdTemp"));
				MilkTransferItem.set("receivedAcidity",(BigDecimal)context.get("recdAcid"));
				
				MilkTransferItem.set("receivedLR",(BigDecimal)context.get("recdCLR"));
				MilkTransferItem.set("receivedCob",(String)context.get("recdCob"));
				MilkTransferItem.set("recdOrganoLepticTest",(String)context.get("recdOrganoLepticTest"));
				MilkTransferItem.set("receivedSedimentTest",(String)context.get("recdSedimentTest"));
				
				delegator.store(MilkTransferItem);
			}catch(Exception e){
				Debug.logError("Error while updating qc details "+e,module);
				return ServiceUtil.returnError("Error while updating qc details "+e.getMessage());
			}
			try{
				delegator.store(milkTransfer);
			}catch(Exception e){
				Debug.logError("Error occured ::"+e,module);
				return ServiceUtil.returnError("Error occured ::"+e.getMessage());
			}
		}else if(statusId.equalsIgnoreCase("MR_ISSUE_OUT")){
 			String exitDateStr = (String) context.get("exitDate"); 
 	        String exitTime = (String) context.get("exitTime");
 	        Timestamp exitDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(exitDateStr)){
 		 			if(UtilValidate.isNotEmpty(exitTime)){
 		 				exitDateStr = exitDateStr.concat(exitTime);
 		 			}
 		 			exitDate = new java.sql.Timestamp(sdf.parse(exitDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + exitDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",exitDate);
 	        updateVehStatusInMap.put("estimatedEndDate",exitDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status"+e.getMessage());
	 			return resultMap;
			}
			String statusIdCheck = (String) milkTransfer.get("statusId");
			milkTransfer.set("statusId", "MXF_RECD");
			milkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
        	if(statusIdCheck.equalsIgnoreCase("MXF_REJECTED")){
        		milkTransfer.set("statusId", "MXF_REJECTED");
        	}
        	try{
        		milkTransfer.store();
 	        }catch (GenericEntityException e) {
				// TODO: handle exception
 	        	Debug.logError("Error while updating receipt status "+e,module);
	        	resultMap = ServiceUtil.returnError("Error while updating receipt status "+e.getMessage());
			}
 		}
		return resultMap ;
	}// End of the service
	
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	
	public static Map<String, Object> createMilkTankerReturnEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String receivedProductId = (String)context.get("receivedProductId");
   	 	String tankerNo = (String) context.get("tankerNo");
   	 	String sendDateStr = (String) context.get("sendDate");
   	 	String sendTime = (String) context.get("sendTime");
   	 	
   	 	String dcNo=(String)context.get("dcNo");
   	 	String productId = (String) context.get("productId");
   	 	String partyId = (String) context.get("partyId");
	 	String partyIdTo = (String) context.get("partyIdTo");
	 	String vehicleStatusId = (String) context.get("vehicleStatusId");
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	String milkTransferId = "";
	 	try{
	 		//we need to check the transfer status of the give tanker
	 		Map tankerInMap = FastMap.newInstance();
	 		tankerInMap.put("tankerNo", tankerNo);
	 		tankerInMap.put("userLogin", userLogin);
	 		tankerInMap.put("reqStatusId","MXF_INIT");

	 		Map getTankerDetailsMap = getTankerRecordNumber(dctx,tankerInMap);

	 		if(ServiceUtil.isSuccess(getTankerDetailsMap)){
	 			Debug.logError("Exisiting receipt is not completed of the tanker :"+tankerNo,module);
	 			resultMap = ServiceUtil.returnError("Exisiting receipt is not completed of the tanker :"+tankerNo);
	 			return resultMap;
	 		}
	 		
	 		// we need to create vehicle trip and vehicle trip status
	 		
	 		//creating vehicle trip
	 		Map vehicleTripMap = FastMap.newInstance();
	 		vehicleTripMap.put("vehicleId", tankerNo);
	 		vehicleTripMap.put("partyId", partyId);
	 		vehicleTripMap.put("userLogin",userLogin);
	 		Map vehicleTripResultMap = dispatcher.runSync("createVehicleTrip", vehicleTripMap);
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTrip :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip ");
	 			return resultMap;
	 		}
	 		String sequenceNum = (String)vehicleTripResultMap.get("sequenceNum");
	 		
	 		Map vehicleTripStatusMap = FastMap.newInstance();
	 		vehicleTripStatusMap.putAll(vehicleTripResultMap);
	 		vehicleTripStatusMap.put("statusId","MR_VEHICLE_IN");
	 		if(UtilValidate.isNotEmpty(vehicleStatusId)){
	 			vehicleTripStatusMap.put("statusId",vehicleStatusId);
	 		}
	 		vehicleTripStatusMap.put("userLogin",userLogin);
	 		
	 		Timestamp sendDate = UtilDateTime.nowTimestamp();
	 		if(UtilValidate.isNotEmpty(sendDateStr)){
	 			if(UtilValidate.isNotEmpty(sendTime)){
	 				sendDateStr = sendDateStr.concat(sendTime);
	 			}
	 			sendDate = new java.sql.Timestamp(sdf.parse(sendDateStr).getTime());
	 		}
	 		
	 		vehicleTripStatusMap.put("estimatedStartDate",sendDate);
	 		vehicleTripStatusMap.remove("responseMessage");
	 		Map vehicleStatusResultMap = dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusMap);
	 		if(ServiceUtil.isError(vehicleTripResultMap)){
	 			Debug.logError("Error While Creating vehicleTripStatus :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
	 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip Status");
	 			return resultMap;
	 		}
	 		GenericValue newTransfer = delegator.makeValue("MilkTransfer");
	 		newTransfer.set("containerId", tankerNo);
	 		newTransfer.set("sequenceNum", sequenceNum);
	 		newTransfer.set("sendDate", sendDate);
	 		newTransfer.set("dcNo", dcNo);
	 		newTransfer.set("productId",productId);
	 		newTransfer.set("partyId", partyId);
	 		newTransfer.set("statusId", "MXF_INPROCESS");
	 		newTransfer.set("partyIdTo", partyIdTo);
	 		newTransfer.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		newTransfer.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));

	 		delegator.createSetNextSeqId(newTransfer);
	 		milkTransferId = (String)newTransfer.get("milkTransferId");
	 		resultMap = ServiceUtil.returnSuccess("Successfully Created Tanker Receipt with Record Number :"+newTransfer.get("milkTransferId"));
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating record==========="+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating record==========="+e.getMessage());
	 		return resultMap;
		}
	 	return resultMap;
   }// End of the service
	
	
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateMilkTankerReturnEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Tanker Recipt Updated Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String tankerNo = (String) context.get("tankerNo");
	 	String vehicleStatusId = (String) context.get("vehicleStatusId");
	 	String milkTransferId = (String) context.get("milkTransferId");
	 	SimpleDateFormat   sdf = new SimpleDateFormat("dd-MM-yyyyHHmm");
	 	GenericValue MilkTransfer = null;
	 	Map updateVehStatusInMap = FastMap.newInstance();
	 	Map updateVehStatResultMap = FastMap.newInstance();
	 	updateVehStatusInMap.put("userLogin",userLogin);
	 	updateVehStatusInMap.put("vehicleId",tankerNo);
	 	updateVehStatusInMap.put("statusId",vehicleStatusId);
	 	updateVehStatusInMap.put("replaceVehicleStatusString","MR_RETURN_");
	 	updateVehStatusInMap.put("replaceVehicleStatusString","MR_RETURN_");
	 	try{
	 		MilkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", milkTransferId),false);
	 	}catch(Exception e){
	 		Debug.logError("Error while getting MilkTransfer Details========"+e ,module);
	 		return ServiceUtil.returnError("Error while getting MilkTransfer Details========"+e.getMessage());
	 	}
	 	if(UtilValidate.isEmpty(MilkTransfer)){
	 		Debug.logError ("Milk Transfer Not Found with transfer Id :"+milkTransferId,module);
	 		return ServiceUtil.returnError("Milk Transfer Not Found with transfer Id :"+milkTransferId);
	 	}
	 	String sequenceNum = (String) MilkTransfer.get("sequenceNum");
	    updateVehStatusInMap.put("sequenceNum", sequenceNum);
	 	if(vehicleStatusId.equalsIgnoreCase("MR_RETURN_GRWEIGHT")){
	 		
 			BigDecimal grossWeight = (BigDecimal)context.get("grossWeight");
 			String grossDateStr = (String) context.get("grossDate"); 
 	        String grossTime = (String) context.get("grossTime");
 	        Timestamp grossDate = UtilDateTime.nowTimestamp();
 	        String driverName = (String) context.get("driverName");
 	        
 	        try{
 		 		if(UtilValidate.isNotEmpty(grossDateStr)){
 		 			if(UtilValidate.isNotEmpty(grossTime)){
 		 				grossDateStr = grossDateStr.concat(grossTime);
 		 			}
 		 			grossDate = new java.sql.Timestamp(sdf.parse(grossDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + grossDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",grossDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
			}
			MilkTransfer.set("grossWeight", grossWeight);
			MilkTransfer.set("driverName", driverName);
			MilkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			try{
				delegator.store(MilkTransfer);
			}catch(Exception e){
				Debug.logError("Error while storing Gross weight  Details ::"+e,module);
				return ServiceUtil.returnError("Error while storing Gross weight  Details ::"+e.getMessage());
			}
	 		
	 	}else if(vehicleStatusId.equalsIgnoreCase("MR_RETURN_QC")){
 			String testDateStr = (String) context.get("testDate"); 
 	        String testTime = (String) context.get("testTime");
 	        Timestamp testDate = UtilDateTime.nowTimestamp();
 	        try{
 		 		if(UtilValidate.isNotEmpty(testDateStr)){
 		 			if(UtilValidate.isNotEmpty(testTime)){
 		 				testDateStr = testDateStr.concat(testTime);
 		 			}
 		 			testDate = new java.sql.Timestamp(sdf.parse(testDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + testDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",testDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
			}
			String qcComments = (String) context.get("qcComments");

	        MilkTransfer.set("productId",(String) context.get("productId"));
			MilkTransfer.set("receivedFat",(BigDecimal)context.get("recdFat"));
			MilkTransfer.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			MilkTransfer.set("fat",(BigDecimal)context.get("recdFat"));
			MilkTransfer.set("snf",(BigDecimal)context.get("recdSnf"));
			MilkTransfer.set("comments", qcComments);
			MilkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			MilkTransfer.set("receivedLR",(BigDecimal)context.get("recdCLR"));
			try{
				delegator.store(MilkTransfer);
			}catch(Exception e){
				Debug.logError("Error while storing Gross weight  Details ::"+e,module);
				return ServiceUtil.returnError("Error while storing Gross weight  Details ::"+e.getMessage());
			}
			
			try{
			GenericValue MilkTransferItem = delegator.makeValue("MilkTransferItem");
			MilkTransferItem.set("milkTransferId",milkTransferId);
			MilkTransferItem.set("receivedFat",(BigDecimal)context.get("recdFat"));
			MilkTransferItem.set("receivedSnf",(BigDecimal)context.get("recdSnf"));
			MilkTransferItem.set("fat",(BigDecimal)context.get("recdFat"));
			MilkTransferItem.set("snf",(BigDecimal)context.get("recdSnf"));
			MilkTransferItem.set("receivedTemparature",(BigDecimal)context.get("recdTemp"));
			MilkTransferItem.set("receivedAcidity",(BigDecimal)context.get("recdAcid"));
			
			MilkTransferItem.set("receivedLR",(BigDecimal)context.get("recdCLR"));
			MilkTransferItem.set("receivedCob",(String)context.get("recdCob"));
			MilkTransferItem.set("recdOrganoLepticTest",(String)context.get("recdOrganoLepticTest"));
			MilkTransferItem.set("receivedSedimentTest",(String)context.get("recdSedimentTest"));

			MilkTransferItem.set("createdByUserLogin", userLogin.get("userLoginId"));
			MilkTransferItem.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));

			delegator.setNextSubSeqId(MilkTransferItem,"sequenceNum",5,1);
			delegator.create(MilkTransferItem);
		}catch(Exception e){
			Debug.logError("Error while storing qc details "+e,module);
			return ServiceUtil.returnError("Error while storing qc details "+e.getMessage());
		}
	 }else if(vehicleStatusId.equalsIgnoreCase("MR_RETURN_UNLOAD")){
	 		
	 		String cipDateStr = (String) context.get("cipDate"); 
 	        String cipTime = (String) context.get("cipTime");
 	        Timestamp cipDate = UtilDateTime.nowTimestamp();
 	        
 	        try{
 		 		if(UtilValidate.isNotEmpty(cipDateStr)){
 		 			if(UtilValidate.isNotEmpty(cipTime)){
 		 				cipDateStr = cipDateStr.concat(cipTime);
 		 			}
 		 			cipDate = new java.sql.Timestamp(sdf.parse(cipDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + cipDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",cipDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
			}
 	        GenericValue MilkTransferItem = null;
			List<GenericValue> milkTransferItemsList = FastList.newInstance();
			try{
				milkTransferItemsList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId), null, null, null, false);
			}catch(Exception e){
				Debug.logError("Error while getting transferItems List ::"+e,module);
				return ServiceUtil.returnError("Error while getting transferItems List ::"+e.getMessage());
			}
			
			if(UtilValidate.isNotEmpty(milkTransferItemsList)){
				MilkTransferItem = EntityUtil.getFirst(milkTransferItemsList);
			}
			
			MilkTransferItem.set("siloId",(String)context.get("silo"));
			MilkTransferItem.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			MilkTransferItem.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			MilkTransferItem.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
			try{
				delegator.store(MilkTransferItem);
			}catch(Exception e){
				Debug.logError("Error while storing QC  Details ::"+e,module);
				return ServiceUtil.returnError("Error while storing QC  Details ::"+e.getMessage());	
			}
	 		
	 	}else if(vehicleStatusId.equalsIgnoreCase("MR_RETURN_CIP")){
	 		
	 		String cipDateStr = (String) context.get("cipDate"); 
 	        String cipTime = (String) context.get("cipTime");
 	        Timestamp cipDate = UtilDateTime.nowTimestamp();
 	        
 	        try{
 		 		if(UtilValidate.isNotEmpty(cipDateStr)){
 		 			if(UtilValidate.isNotEmpty(cipTime)){
 		 				cipDateStr = cipDateStr.concat(cipTime);
 		 			}
 		 			cipDate = new java.sql.Timestamp(sdf.parse(cipDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + cipDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",cipDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
			}
			
			if(UtilValidate.isEmpty(MilkTransfer)){
				Debug.logError("Error while getting transfer Details ::",module);
				return ServiceUtil.returnError("Error while getting transfer Details ::");
			}
			MilkTransfer.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
			MilkTransfer.set("isCipChecked",(String)context.get("isCipChecked"));
			try{
				delegator.store(MilkTransfer);
			}catch(Exception e){
				Debug.logError("Error while storing CIP Details ::"+e,module);
				return ServiceUtil.returnError("Error while storing CIP  Details ::"+e.getMessage());	
			}
	 		
	 	}else if(vehicleStatusId.equalsIgnoreCase("MR_RETURN_TARWEIGHT")){
	 		
	 		String tareDateStr = (String) context.get("tareDate"); 
 	        String tareTime = (String) context.get("tareTime");
 	        Timestamp tareDate = UtilDateTime.nowTimestamp();
 	        
 	        try{
 		 		if(UtilValidate.isNotEmpty(tareDateStr)){
 		 			if(UtilValidate.isNotEmpty(tareTime)){
 		 				tareDateStr = tareDateStr.concat(tareTime);
 		 			}
 		 			tareDate = new java.sql.Timestamp(sdf.parse(tareDateStr).getTime());
 		 		}
 	        	
 	        }catch(ParseException e){
 	        	Debug.logError(e, "Cannot parse date string: " + tareDateStr, module);
 	        	resultMap = ServiceUtil.returnError("Cannot parse date string: ");
	 			return resultMap;
 	        }
 	        updateVehStatusInMap.put("estimatedStartDate",tareDate);
 	        updateVehStatusInMap.put("estimatedEndDate",tareDate);
 	        try{
 	        	updateVehStatResultMap = dispatcher.runSync("updateReceiptVehicleTripStatus", updateVehStatusInMap);
 	        	if(ServiceUtil.isError(updateVehStatResultMap)){
 	        		Debug.logError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap),module);
 	        		resultMap = ServiceUtil.returnError("Error while updating the vehicleTrip Status ::"+ServiceUtil.getErrorMessage(updateVehStatResultMap));
		 			return resultMap;
 	        	}
 	        }catch (GenericServiceException e) {
				// TODO: handle exception
 	        	Debug.logError("Service Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
 	        	
			}catch(Exception e){
				Debug.logError("Exception while updating the vehicleTrip Status"+e,module);
	        		resultMap = ServiceUtil.returnError("Exception while updating the vehicleTrip Status ::"+e.getMessage());
	 			return resultMap;
			}
 	       BigDecimal tareWeight = (BigDecimal)context.get("tareWeight");
	        
			MilkTransfer.set("tareWeight", tareWeight);
			//need to calcultae actual weight of the milk received i.e recdQty,sendQty
			BigDecimal sendQty = BigDecimal.ZERO;
			BigDecimal recdQty = BigDecimal.ZERO;
			
			if(UtilValidate.isNotEmpty(tareWeight) && UtilValidate.isNotEmpty(MilkTransfer.get("grossWeight"))){
				recdQty = ((BigDecimal)MilkTransfer.get("grossWeight")).subtract(tareWeight);
			}
			sendQty =recdQty;
			BigDecimal fat = (BigDecimal)MilkTransfer.get("fat");
			BigDecimal snf = (BigDecimal)MilkTransfer.get("snf");
			
			BigDecimal receivedFat = (BigDecimal)MilkTransfer.get("receivedFat");
			BigDecimal receivedSnf = (BigDecimal)MilkTransfer.get("receivedSnf");
			
			BigDecimal sendKgFat = BigDecimal.ZERO;
			BigDecimal sendKgSnf = BigDecimal.ZERO;
			
			BigDecimal receivedKgFat = BigDecimal.ZERO;
			BigDecimal receivedKgSnf = BigDecimal.ZERO;
			
			BigDecimal receivedQuantityLtrs = BigDecimal.ZERO;
			BigDecimal quantityLtrs = BigDecimal.ZERO;
			
			if(UtilValidate.isNotEmpty(sendQty) && sendQty.compareTo(BigDecimal.ZERO)>0){
				quantityLtrs= ProcurementNetworkServices.convertKGToLitreSetScale(sendQty,false);	
			}
			if(UtilValidate.isNotEmpty(recdQty) && recdQty.compareTo(BigDecimal.ZERO)>0){
				receivedQuantityLtrs= ProcurementNetworkServices.convertKGToLitreSetScale(recdQty,false);	
			}
			
			if(quantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(fat) && fat.compareTo(BigDecimal.ZERO)>0){
				sendKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sendQty,fat);
			}
			if(quantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(snf) && snf.compareTo(BigDecimal.ZERO)>0){
				sendKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(sendQty,snf);
			}
			if(receivedQuantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(receivedFat) && receivedFat.compareTo(BigDecimal.ZERO)>0){
				receivedKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(recdQty,receivedFat);
			}
			if(receivedQuantityLtrs.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(receivedSnf) && receivedSnf.compareTo(BigDecimal.ZERO)>0){
				receivedKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(recdQty,receivedSnf);
			}
			
			
			MilkTransfer.set("quantity", sendQty);
			MilkTransfer.set("quantityLtrs", quantityLtrs);
			MilkTransfer.set("receivedQuantityLtrs", receivedQuantityLtrs);
			
			MilkTransfer.set("receivedQuantity", recdQty);
			MilkTransfer.set("receiveDate",tareDate);
			MilkTransfer.set("statusId","MXF_RECD");
			MilkTransfer.set("receivedKgFat", receivedKgFat);
			MilkTransfer.set("receivedKgSnf",receivedKgSnf);
			MilkTransfer.set("sendKgFat", sendKgFat);
			MilkTransfer.set("sendKgSnf",sendKgSnf);
			MilkTransfer.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			
			try{
				MilkTransfer.store();
			}catch(Exception e){
				Debug.logError("Error while updating tareweight ::"+e,module);
				return ServiceUtil.returnError("Error while updating tareweight ::"+e);
			}
			
			
			//need to update TransferItem
			try{
				
				List conditionList = UtilMisc.toList(EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId));
				EntityCondition condition = EntityCondition.makeCondition(conditionList);
				List<GenericValue> itemsList = delegator.findList("MilkTransferItem",condition,null,null,null,false);
				if(UtilValidate.isEmpty(itemsList)){
					Debug.logError("Receipt Failed at QC ",module);
					resultMap = ServiceUtil.returnError("Receipt failed at QC");
					return resultMap;
				}
				GenericValue item = EntityUtil.getFirst(itemsList);
				// In KMF we have only one transfer Item
					item.set("quantity", sendQty);
					item.set("receivedQuantity", recdQty);
					item.set("receivedKgFat", receivedKgFat);
					item.set("receivedKgSnf",receivedKgSnf);
					item.set("sendKgFat", sendKgFat);
					item.set("sendKgSnf",sendKgSnf);
					item.set("quantityLtrs", quantityLtrs);
					item.set("receivedQuantityLtrs", receivedQuantityLtrs);
					item.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
					item.store();
				
			}catch(Exception e){
				Debug.logError("Error while updating actual weights "+e,module);
				resultMap= ServiceUtil.returnError("Error while updating actual weights "+e.getMessage());
				return resultMap;
			}
			
			// here we are trying to reduce the floor Inventory and update it to respect MPU silo inventory
			
			String partyId = (String) MilkTransfer.get("partyId");
			BigDecimal atpQty = BigDecimal.ZERO;
			String facilityId = "";
			// currently we are assuming one floor for each partyId
			GenericValue floorFacilityDetails = null;
			try{
				List floorConditionList = FastList.newInstance();
				floorConditionList.add( EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyId));
				floorConditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT"));
				EntityCondition floorCondition = EntityCondition.makeCondition(floorConditionList);
				List floorsList = delegator.findList("Facility",floorCondition , null, null, null, false);
				if(UtilValidate.isNotEmpty(floorsList)){
					floorFacilityDetails = EntityUtil.getFirst(floorsList);
					facilityId = (String)floorFacilityDetails.get("facilityId");
				}
			}catch(GenericEntityException e){
				Debug.logError("Error while getting plant details for partyId "+partyId+" Exception "+e, module);
				return ServiceUtil.returnError("Error while getting plant details for partyId"+partyId);
			}
			
			String productId = (String) MilkTransfer.get("productId");
			List<GenericValue> aptProdList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(facilityId)){
				try{
					List atpCondList = FastList.newInstance();
					atpCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
					atpCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
					atpCondList.add(EntityCondition.makeCondition("availableToPromiseTotal",EntityOperator.GREATER_THAN,BigDecimal.ZERO));
					EntityCondition atpCondtion = EntityCondition.makeCondition(atpCondList);
					aptProdList = delegator.findList("InventoryItem", atpCondtion, null, null, null, false);
					for(GenericValue atpProduct : aptProdList){
						atpQty = atpQty.add((BigDecimal)atpProduct.get("availableToPromiseTotal"));
					}
				}catch(GenericEntityException e){
					Debug.logError("Error while getting floor details of department :: "+e, module);
					return ServiceUtil.returnError("Error while getting floor details of department :: "+e.getMessage());
				}
			}
			
			BigDecimal sendFat = (BigDecimal)MilkTransfer.get("receivedFat");
			BigDecimal sendSnf = (BigDecimal)MilkTransfer.get("receivedSnf");
			BigDecimal quantity = (BigDecimal)MilkTransfer.get("quantity");
			String containerId = (String)MilkTransfer.get("containerId");
			BigDecimal toBeIssuedQty = quantity;
			//here we are trying to decrease at Sender
			
			if(UtilValidate.isNotEmpty(atpQty) && atpQty.compareTo(BigDecimal.ZERO)==1){
				if(atpQty.compareTo(quantity)<0){
					Debug.logError("You cannot make this transfer from this Silo .Available quantity is less than shipping quantity. ",module);
					return ServiceUtil.returnError("You cannot make this transfer.Available quantity is less than shipping quantity. ");
				}
				
				Timestamp nowTimeStamp= (Timestamp) UtilDateTime.nowTimestamp();
				
				// Creating Shipment
				GenericValue newEntity = delegator.makeValue("Shipment");
		        newEntity.set("estimatedShipDate", tareDate);
		        newEntity.set("shipmentTypeId", "MILK_RETURN_SHIPMENT");
		        newEntity.set("statusId", "GENERATED");
		        newEntity.set("vehicleId",containerId);
		        newEntity.set("partyIdFrom",partyId);
		        /*newEntity.put("primaryOrderId", milkTransferId);*/
		        newEntity.set("createdDate", nowTimeStamp);
		        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        String shipmentId = null;
				try{
					delegator.createSetNextSeqId(newEntity);            
					shipmentId = (String) newEntity.get("shipmentId");
				}catch(GenericEntityException e){
					Debug.logError("Error while creating return shipment ::"+e,module);
					return ServiceUtil.returnError("Error while creating return shipment ::"+e.getMessage());
				}
				// Creating shipmentItem
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("shipmentId",shipmentId);
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        itemInMap.put("quantity",quantity);
		        itemInMap.put("fat",sendFat);
		        itemInMap.put("snf",sendSnf);
		        try{
		        	resultMap = dispatcher.runSync("createShipmentItem",itemInMap);
		        	if (ServiceUtil.isError(resultMap)) {
			        	Debug.logError("Problem creating shipment Item for milkTransfer :"+milkTransferId, module);
				  		return ServiceUtil.returnError("Problem creating shipment Item for milkTransfer :"+milkTransferId);
			        }
		        }catch(Exception e){
		        	Debug.logError("Error while creating shipmentItem :"+e, module);
		        	return ServiceUtil.returnError("Error while creating shipmentItem :"+e.getMessage());
		        }
		        String shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");
				
				
				
				// Creating itemIssuance
		        shipmentItemSeqId = (String)resultMap.get("shipmentItemSeqId");
		        Map itemIssueCtx = FastMap.newInstance();
		        itemIssueCtx.put("userLogin", userLogin);
				itemIssueCtx.put("shipmentId", shipmentId);
				itemIssueCtx.put("shipmentItemSeqId", shipmentItemSeqId);
				itemIssueCtx.put("productId", productId);
				itemIssueCtx.put("quantity", quantity);
				itemIssueCtx.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
				itemIssueCtx.put("modifiedByUserLoginId", userLogin.getString("userLoginId"));
				itemIssueCtx.put("modifiedDateTime", UtilDateTime.nowTimestamp());
				itemIssueCtx.put("snfPercent", sendSnf);
				itemIssueCtx.put("fatPercent",sendFat);
				itemIssueCtx.put("facilityId", facilityId);
				
				resultMap.remove("shipmentItemSeqId");
				
				String itemIssuanceId ="" ;
				Map resultCtx = FastMap.newInstance();
				try{
					 resultCtx = dispatcher.runSync("createItemIssuance", itemIssueCtx);
					if (ServiceUtil.isError(resultCtx)) {
						Debug.logError("Problem creating item issuance for requested item", module);
						return resultCtx;
					}
					itemIssuanceId = (String)resultCtx.get("itemIssuanceId") ;
			   	}catch (Exception e) {
			 		Debug.logError(e, "Error While Creating Item Issuance !", module);
			 		return ServiceUtil.returnError("Failed to create a Item Issuance " + e);	  
			  	}
				
				// Creating InventoryDetails 
				for(GenericValue inventoryItem : aptProdList){
					BigDecimal issueQty = BigDecimal.ZERO;
					if(toBeIssuedQty.compareTo(BigDecimal.ZERO) ==0 ){
						break;
					}
					BigDecimal atpTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");
					String inventoryItemId = (String) inventoryItem.get("inventoryItemId");
					if(toBeIssuedQty.compareTo(atpTotal)<=0){
						issueQty = toBeIssuedQty;
						toBeIssuedQty = BigDecimal.ZERO;
					}else{
						issueQty = atpTotal; 
						toBeIssuedQty = toBeIssuedQty.subtract(atpTotal); 
					}
					try{
						Map createInvDetail = FastMap.newInstance();
						createInvDetail.put("userLogin", userLogin);
						createInvDetail.put("inventoryItemId", inventoryItemId);
						createInvDetail.put("itemIssuanceId", itemIssuanceId);
						createInvDetail.put("quantityOnHandDiff", issueQty.negate());
						createInvDetail.put("availableToPromiseDiff", issueQty.negate());
						resultCtx = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
						if (ServiceUtil.isError(resultCtx)) {
							Debug.logError("Problem decrementing inventory for requested item ", module);
							return resultCtx;
						}
					}catch(Exception e){
						Debug.logError("Error while decrementing inventory :"+e,module);
						return ServiceUtil.returnError("Error while decrementing inventory :"+e.getMessage());
					}
				}// End of the Inventory Item Details.
				// Here we are trying to update the silo inventory
				
				String siloId = "";
				try{
					List<GenericValue> milkTransferItems = delegator.findList("MilkTransferItem",
							EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId),
							null, null, null, false);
					GenericValue milkTransferItem = EntityUtil.getFirst(milkTransferItems);
					if(UtilValidate.isNotEmpty(milkTransferItem)){
						siloId = (String) milkTransferItem.get("siloId");
					}
				}catch(GenericEntityException e){
					Debug.logError("Error while gettin silo Details :"+e ,module);
					return ServiceUtil.returnError("Error while gettin silo Details :"+e.getMessage());
				}
				
				Map inventoryReceiptCtx = FastMap.newInstance();
				inventoryReceiptCtx.put("userLogin", userLogin);
				inventoryReceiptCtx.put("productId", productId);
				inventoryReceiptCtx.put("snfPercent", sendSnf);
				inventoryReceiptCtx.put("fatPercent",sendFat);
				inventoryReceiptCtx.put("datetimeReceived", tareDate);
				inventoryReceiptCtx.put("quantityAccepted", quantity);
				inventoryReceiptCtx.put("quantityRejected", BigDecimal.ZERO);
				inventoryReceiptCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				inventoryReceiptCtx.put("ownerPartyId", "Company");
				inventoryReceiptCtx.put("facilityId", siloId);
				inventoryReceiptCtx.put("unitCost", BigDecimal.ZERO);
				inventoryReceiptCtx.put("shipmentId", shipmentId);
				Map<String, Object> receiveInventoryResult;
				try{
					receiveInventoryResult = dispatcher.runSync("receiveInventoryProduct", inventoryReceiptCtx);
				}catch(GenericServiceException e){
					Debug.logError("Error while updating silo inventory ::"+e, module);
					return ServiceUtil.returnError("Error while updating silo inventory ::"+e.getMessage());
				}
				Debug.log("success fully completed ===");
				resultMap = ServiceUtil.returnSuccess("Successfully updated ");
			}else{
				Debug.logError("It is a false transaction . Reason This section have only "+atpQty+" KGS",module);
				return ServiceUtil.returnError("It is a false transaction . Reason This section have only "+atpQty+" KGS");
			}
	 	}
	 	return resultMap;
	}
	
	public static String finalizeTankerReceipts(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		if (rowCount < 1) {
			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			return "error";
		}
		    String milkTransferId = "";
		    String productId = "";
		    String partyId = "";
		    String purposeTypeId = "";
		  boolean beginTransaction = false;
		try{
  		  	beginTransaction = TransactionUtil.begin();
		  	for (int i = 0; i < rowCount; i++){
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				
				if (paramMap.containsKey("milkTransferId" + thisSuffix)) {
					milkTransferId = (String) paramMap.get("milkTransferId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing milkTransferId");
				}
				if (paramMap.containsKey("productId" + thisSuffix)) {
					productId = (String) paramMap.get("productId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing productId");
				}
				if (paramMap.containsKey("purposeTypeId" + thisSuffix)) {
					purposeTypeId = (String) paramMap.get("purposeTypeId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing purposeTypeId");
				}
				if (paramMap.containsKey("partyId" + thisSuffix)) {
					partyId = (String) paramMap.get("partyId" + thisSuffix);
				}
				else {
					request.setAttribute("_ERROR_MESSAGE_", "Missing partyId");
				}
				GenericValue milkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId", milkTransferId),false);
				
				if(UtilValidate.isEmpty(milkTransfer)){
					Debug.logError("No Record Found For :"+milkTransferId, module);
					request.setAttribute("_ERROR_MESSAGE_", "No Record Found For :"+milkTransferId);	
			  		return "error";
				}
				Timestamp receiveDate = milkTransfer.getTimestamp("receiveDate");
				Map resultMap = checkBillingGeneratedOrNotForDate(dctx,UtilMisc.toMap("userLogin",userLogin,"receiveDate",receiveDate));
				if(ServiceUtil.isError(resultMap)){
					Debug.logError("Billing Generated For this Date",module);
					request.setAttribute("_ERROR_MESSAGE_", (String)resultMap.get("errorMessage"));
					return "error";
				}
				if(!productId.equals(milkTransfer.getString("productId"))){
					milkTransfer.set("productId", productId);
					
					List<GenericValue> milkTransferItemList = delegator.findList("MilkTransferItem",EntityCondition.makeCondition("milkTransferId",EntityOperator.EQUALS,milkTransferId),null,null,null,false);
					GenericValue milkTransferItem = EntityUtil.getFirst(milkTransferItemList);
					if(UtilValidate.isEmpty(milkTransferItem)){
						Debug.logError("No Record Found For :"+milkTransferId, module);
						request.setAttribute("_ERROR_MESSAGE_", "No Record Found For :"+milkTransferId);	
				  		return "error";
					}
					milkTransferItem.set("sendProductId", productId);
					milkTransferItem.set("receivedProductId", productId);
					delegator.store(milkTransferItem);
				}
				if(!partyId.equals(milkTransfer.getString("partyId"))){
					milkTransfer.set("partyId", partyId);
				}
				if(!purposeTypeId.equals(milkTransfer.getString("purposeTypeId"))){
					milkTransfer.set("purposeTypeId", purposeTypeId);
				}
				milkTransfer.store();
		  	}
  	  	}
	  	catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
		catch (Exception e) {
			try {
				TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beginTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", e.toString());
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Finalization Successfully Completed.!");
		return "success"; 
		
    }// End of the service
	public static String createNewTanker(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx =  dispatcher.getDispatchContext();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	    String vehicleId = (String)paramMap.get("vehicleId");
	    String roleTypeId = (String)paramMap.get("roleTypeId");
	    String partyId = (String)paramMap.get("partyId");
	    String rateAmountStr = (String)paramMap.get("rateAmount");
	    String fromDateStr = (String)paramMap.get("fromDate");
	    String vehicleCapacityStr = (String)paramMap.get("vehicleCapacity");
	    String custmCapacityStr = (String)paramMap.get("custmCapacity");
	    
	    Timestamp fromDate =UtilDateTime.nowTimestamp();
	    BigDecimal vehicleCapacity = BigDecimal.ZERO;
	    BigDecimal rateAmount = BigDecimal.ZERO;
		  boolean beginTransaction = false;
		try{
  		  	beginTransaction = TransactionUtil.begin();
          if (UtilValidate.isNotEmpty(fromDateStr)) { //2011-12-25
          	SimpleDateFormat   sdf = new SimpleDateFormat("dd MMM, yyyy");             
  	  		  try {
  	  			  fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
  	  		  } catch (ParseException e) {
  	  			  Debug.logError(e, "Cannot parse date string: " + fromDateStr, module);
  	  			request.setAttribute("_ERROR_MESSAGE_","Cannot parse date string: " + fromDateStr);
    			return "error";
  	              
  	  		  } catch (NullPointerException e) {
  	  			  Debug.logError(e, "Cannot parse date string: " + fromDateStr, module);
  	  			request.setAttribute("_ERROR_MESSAGE_","Cannot parse date string: " + fromDateStr);
    			return "error";
  	  		  }
  	  	  } 
          if(UtilValidate.isEmpty(vehicleId)){
        	   request.setAttribute("_ERROR_MESSAGE_"," vehicleId is missing");
    			return "error";
           }
          if(UtilValidate.isEmpty(roleTypeId)){
       	   request.setAttribute("_ERROR_MESSAGE_"," roleTypeId is missing");
   			return "error";
          }		
          if(UtilValidate.isEmpty(partyId)){
          	   request.setAttribute("_ERROR_MESSAGE_"," partyId is missing");
      			return "error";
             }
          if(UtilValidate.isEmpty(partyId)){
         	   request.setAttribute("_ERROR_MESSAGE_"," partyId is missing");
     			return "error";
            }
           if(UtilValidate.isEmpty(vehicleCapacityStr)){
        	   request.setAttribute("_ERROR_MESSAGE_"," vehicleCapacity is missing");
    			return "error";
           }
           if(vehicleCapacityStr.equals("OTHER")){
        	   vehicleCapacityStr = custmCapacityStr;
           }
           
           vehicleCapacity = new BigDecimal(vehicleCapacityStr);
           if(UtilValidate.isNotEmpty(rateAmountStr)){
        	   rateAmount = new BigDecimal(rateAmountStr);
           }
           
           //Creating Vechicle
           Map vehicleMap = FastMap.newInstance();
	           vehicleMap.put("vehicleId", vehicleId);
	           vehicleMap.put("vehicleCapacity", vehicleCapacity);
	           vehicleMap.put("vehicleNumber", vehicleId);
	           vehicleMap.put("userLogin",userLogin);
	 		Map vehicleResultMap = dispatcher.runSync("createVehicle", vehicleMap);
	 		if(ServiceUtil.isError(vehicleResultMap)){
	 			Debug.logError("Error While Creating vehicle,module",module);
	 			request.setAttribute("_ERROR_MESSAGE_", "Error While Creating Vehicle");
	 			return "error";
	 		}
	 		//Create VehicleRate
	 		/* Map vehicleRateMap = FastMap.newInstance();
		 		vehicleRateMap.put("vehicleId", vehicleId);
		 		vehicleRateMap.put("rateTypeId", "TANKER_RATE");
		 		vehicleRateMap.put("rateCurrencyUomId", "INR");
		 		vehicleRateMap.put("fromDate", fromDate);
		 		vehicleRateMap.put("rateAmount", rateAmount);
		 		vehicleRateMap.put("userLogin",userLogin);
		 		Map vehicleRateResultMap = dispatcher.runSync("createVehicleRate", vehicleRateMap);
		 		if(ServiceUtil.isError(vehicleRateResultMap)){
		 			Debug.logError("Error While Creating vehicleRate",module);
		 			request.setAttribute("_ERROR_MESSAGE_", "Error While Creating VehicleRate");
		 			return "error";
		 		}*/
           //Create vehicleRole
		 		Map vehicleRoleMap = FastMap.newInstance();
			 		vehicleRoleMap.put("vehicleId", vehicleId);
			 		vehicleRoleMap.put("roleTypeId", roleTypeId);
			 		vehicleRoleMap.put("partyId", partyId);
			 		vehicleRoleMap.put("fromDate", fromDate);
			 		vehicleRoleMap.put("facilityId", "_NA_");
			 		vehicleRoleMap.put("userLogin",userLogin);
			 		Map vehicleRoleResultMap = dispatcher.runSync("createVehicleRole", vehicleRoleMap);
			 		if(ServiceUtil.isError(vehicleRoleResultMap)){
			 			Debug.logError("Error While Creating vehicleRole",module);
			 			request.setAttribute("_ERROR_MESSAGE_", "Error While Creating VehicleRole");
			 			return "error";
			 		}
  	  	}
	  	catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
		catch (Exception e) {
			try {
				TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beginTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", e.toString());
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Vehicle  Successfully Created.!");
		return "success"; 
		
    }// End of the service createVehicle
	public static Map<String, Object> createVehicle(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Vehicle Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String vehicleId = (String) context.get("vehicleId");
   	 	String vehicleNumber = (String) context.get("vehicleNumber");
   	 	BigDecimal vehicleCapacity = (BigDecimal) context.get("vehicleCapacity");
	 	try{
	 		GenericValue vehicle = delegator.makeValue("Vehicle");
	 		vehicle.set("vehicleId", vehicleId);
	 		vehicle.set("vehicleCapacity", vehicleCapacity);
	 		vehicle.set("vehicleNumber", vehicleNumber);
	 		vehicle.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		vehicle.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		vehicle.set("createdDate", UtilDateTime.nowTimestamp());
	 		delegator.create(vehicle);
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating vehicle"+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating vehicle"+e.getMessage());
	 		return resultMap;
		}
	 	
    	return resultMap;
   }// End of the service
	public static Map<String, Object> createVehicleRate(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Vehicle Rate Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String vehicleId = (String) context.get("vehicleId");
   	 	String rateTypeId = (String) context.get("rateTypeId");
   	 	String rateCurrencyUomId = (String) context.get("rateCurrencyUomId");
   	 	BigDecimal rateAmount = (BigDecimal) context.get("rateAmount");
   	 	Timestamp fromDate = (Timestamp) context.get("fromDate");
	 	try{
	 		GenericValue vehicleRate = delegator.makeValue("VehicleRate");
	 		vehicleRate.set("vehicleId", vehicleId);
	 		vehicleRate.set("rateTypeId", rateTypeId);
	 		vehicleRate.set("rateCurrencyUomId", rateCurrencyUomId);
	 		vehicleRate.set("rateAmount", rateAmount);
	 		vehicleRate.set("fromDate", fromDate);
	 		vehicleRate.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		vehicleRate.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		vehicleRate.set("createdDate", UtilDateTime.nowTimestamp());
	 		delegator.create(vehicleRate);
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating vehicleRate"+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating vehicleRate"+e.getMessage());
	 		return resultMap;
		}
	 	
    	return resultMap;
   }// End of the service
	public static Map<String, Object> createVehicleRole(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Vehicle Role Created Successfully");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String vehicleId = (String) context.get("vehicleId");
   	 	String roleTypeId = (String) context.get("roleTypeId");
   	 	String partyId = (String) context.get("partyId");
   	 	String facilityId = (String) context.get("facilityId");
   	 	Timestamp fromDate = (Timestamp) context.get("fromDate");
	 	try{
	 		GenericValue party = delegator.findOne("Party",UtilMisc.toMap("partyId", partyId),false);
	 		if(UtilValidate.isEmpty(party)){
	 			Debug.logError(partyId+" Not Found..!",module);
	 			return ServiceUtil.returnError(partyId+" Not Found..!");
	 		}
	 		GenericValue vehicleRole = delegator.makeValue("VehicleRole");
	 		vehicleRole.set("vehicleId", vehicleId);
	 		vehicleRole.set("roleTypeId", roleTypeId);
	 		vehicleRole.set("fromDate", fromDate);
	 		vehicleRole.set("partyId",partyId);
	 		vehicleRole.set("facilityId",facilityId);
	 		vehicleRole.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		vehicleRole.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		vehicleRole.set("createdDate", UtilDateTime.nowTimestamp());
	 		delegator.create(vehicleRole);
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating vehicleRole"+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating vehicleRole"+e.getMessage());
	 		return resultMap;
		}
	 	
    	return resultMap;
   }// End of the service
 public static Map<String, Object> updateVehicleRateAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> resultMap = ServiceUtil.returnSuccess("Vehicle Rate Successfully Updated.!");
   	 	Delegator delegator = dctx.getDelegator();
   	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
   	 	String vehicleId = (String) context.get("vehicleId");
   	 	String rateAmountStr = (String) context.get("rateAmount");
   	 	Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
   	 	BigDecimal rateAmount = BigDecimal.ZERO;
   	 	if(UtilValidate.isNotEmpty(rateAmountStr)){
   	 		rateAmount = new BigDecimal(rateAmountStr);
   	 	}
	 	try{
	 		List conditionList = FastList.newInstance();
	 		conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
	 		conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN,effectiveDate));
	 		EntityCondition condition = EntityCondition.makeCondition(conditionList);
	 		List vehicleRateList = delegator.findList("VehicleRate",condition , null, null, null, false);
	 		if(UtilValidate.isEmpty(vehicleRateList)){
	 			Debug.logError(vehicleId+" Not Found or check the Effective Date..!",module);
		 		resultMap = ServiceUtil.returnError(vehicleId+" Not Found or check the Effective Date..!");
		 		return resultMap;
	 		}
	 		GenericValue vehicleRate = EntityUtil.getFirst(vehicleRateList);
	 		Timestamp thruDate =  UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(effectiveDate, -1));
	 		vehicleRate.set("thruDate",thruDate);
	 		vehicleRate.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		vehicleRate.store();
	 		GenericValue newVehicleRate = delegator.makeValue("VehicleRate");
	 		newVehicleRate.set("vehicleId", vehicleId);
	 		newVehicleRate.set("rateTypeId", "TANKER_RATE");
	 		newVehicleRate.set("fromDate", UtilDateTime.getDayStart(effectiveDate));
	 		newVehicleRate.set("rateAmount", rateAmount);
	 		newVehicleRate.set("rateCurrencyUomId", "INR");
	 		newVehicleRate.set("createdByUserLogin", (String)userLogin.get("userLoginId"));
	 		newVehicleRate.set("lastModifiedByUserLogin", (String)userLogin.get("userLoginId"));
	 		newVehicleRate.set("createdDate", UtilDateTime.nowTimestamp());
	 		delegator.create(newVehicleRate);
	 	}catch (Exception e) {
			// TODO: handle exception
	 		Debug.logError("Error while creating vehicleRole"+e,module);
	 		resultMap = ServiceUtil.returnError("Error while creating vehicleRole"+e.getMessage());
	 		return resultMap;
		}
	 	
    	return resultMap;
   }// End of the service
 public static  Map<String, Object> checkBillingGeneratedOrNotForDate(DispatchContext dctx, Map context) {
	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
	 	Date date =  (Date)context.get("date");
	 	Timestamp receiveDate = (Timestamp)context.get("receiveDate");
     Delegator delegator = dctx.getDelegator();
     Timestamp dateTime=null;
     if(UtilValidate.isNotEmpty(receiveDate)){
     	 dateTime = receiveDate;
     }else{
     	 dateTime = UtilDateTime.toTimestamp(date);
     }
 	Timestamp dateStart = UtilDateTime.getDayStart(dateTime);
 	Timestamp dateEnd = UtilDateTime.getDayEnd(dateTime);
     Map result = ServiceUtil.returnSuccess();
    List<String> customTimePeriodIds = FastList.newInstance();
     try {
     	List condList = FastList.newInstance();
			condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN ,UtilMisc.toList("PTC_FORTNIGHT_BILL","PROC_BILL_MONTH")));
			condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(dateStart.getTime())));
			condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(dateEnd.getTime())));
			EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND); 	
			List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", cond, null, null, null, false);
			if(UtilValidate.isNotEmpty(customTimePeriodList)){
				customTimePeriodIds = EntityUtil.getFieldListFromEntityList(customTimePeriodList, "customTimePeriodId", true);
			}
			if(UtilValidate.isNotEmpty(customTimePeriodIds)){
		        List conditionList = FastList.newInstance();
		        List<GenericValue> periodBillingList = FastList.newInstance();
		        conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,UtilMisc.toList("REJECT_PAYMENT","COM_CANCELLED")));
		        conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN ,customTimePeriodIds));
		    	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.IN ,UtilMisc.toList("PB_PTC_TRSPT_MRGN","PB_PROC_MRGN")));
		    	EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		    	periodBillingList = delegator.findList("PeriodBilling", condition, null,null, null, false);
		    	if(UtilValidate.isNotEmpty(periodBillingList)){	
		    		return ServiceUtil.returnError("Billing Already Generated.");
		        }
			}
     }
     catch (GenericEntityException e) {
         Debug.logError(e, "Error retrieving CustomTimePeriodId");
     }        
     return result;
}
 public static Map<String, Object> updateVechicleDetails(DispatchContext ctx,Map<String, Object> context) {
     Map<String, Object> finalResult = FastMap.newInstance();
     Map<String, Object> result = FastMap.newInstance();
     Delegator delegator = ctx.getDelegator();
     LocalDispatcher dispatcher = ctx.getDispatcher();
     Locale locale = (Locale) context.get("locale");
     String vehicleId = (String) context.get("vehicleId");
     String partyId = (String) context.get("partyId");
     String contractor = (String) context.get("contractor");
     Timestamp fromDate = (Timestamp) context.get("fromDate");
     Timestamp thruDate = (Timestamp) context.get("thruDate");
     Timestamp startDate = (Timestamp) context.get("startDate");
     Timestamp endDate = (Timestamp) context.get("endDate");
     GenericValue userLogin = (GenericValue) context.get("userLogin");
     if(UtilValidate.isNotEmpty(contractor) && contractor.equals("TEMP_CONTRACTOR")){
    	 return ServiceUtil.returnError("Please select the contractor name other than "+contractor);
     }
     if(UtilValidate.isNotEmpty(thruDate)){
     	thruDate = UtilDateTime.getDayEnd(thruDate);
     }
     if(UtilValidate.isNotEmpty(fromDate)){
     	fromDate = UtilDateTime.getDayStart(fromDate);
     }
     if(UtilValidate.isNotEmpty(startDate)){
     	startDate = UtilDateTime.getDayStart(startDate);
     }
     if(UtilValidate.isNotEmpty(endDate)){
     	endDate = UtilDateTime.getDayEnd(endDate);
     }
     if(UtilValidate.isEmpty(thruDate) && UtilValidate.isEmpty(startDate)){
     	return ServiceUtil.returnError("No values found to update..!");
     }
     if(UtilValidate.isNotEmpty(thruDate)){
     	if(thruDate.before(fromDate)){
     		return ServiceUtil.returnError("ThruDate must greater than fromDate");
     	}
     }
     if(UtilValidate.isNotEmpty(startDate)){
    	 if(partyId.equals("TEMP_CONTRACTOR")){
    		 startDate = fromDate;
    	 }else if(startDate.compareTo(fromDate)<0 || startDate.compareTo(fromDate)==0){
     		return ServiceUtil.returnError("startDate must greater than fromDate");
     	 }
     	if(UtilValidate.isNotEmpty(thruDate)){
     		if(startDate.before(thruDate)){
     			return ServiceUtil.returnError("startDate must greater than thruDate");	
     		}
     	}else{
     		thruDate = UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayEnd(startDate),-1);
     	}
     }
     if( UtilValidate.isNotEmpty(endDate)){
     	if(UtilValidate.isEmpty(startDate)){
     		return ServiceUtil.returnError("startDate not found..!");
     	}else{
     		if(endDate.before(startDate)){
     			return ServiceUtil.returnError("endDate must greater than startDate");	
     		}
     	}
     }
    // Debug.log("vehicleId###partyId###contractor####fromDate###thruDate###startDate##endDate"+vehicleId+"###"+partyId+"####"+contractor+"####"+fromDate+"###"+thruDate+"####"+startDate+"####"+endDate);
     List conditionList = FastList.newInstance();
     List<GenericValue> vehicleRoleList = FastList.newInstance();
		try {
			if(UtilValidate.isNotEmpty(thruDate) && UtilValidate.isEmpty(startDate)){
				conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		        conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
		        conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE"));
		        conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,"_NA_"));
		        EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		        vehicleRoleList = delegator.findList("VehicleRole", cond, null, UtilMisc.toList("-fromDate"), null, false);
		        GenericValue vehicleRole = EntityUtil.getFirst(vehicleRoleList);
		        if(UtilValidate.isEmpty(vehicleRole.getTimestamp("thruDate"))){
		        	vehicleRole.set("thruDate", thruDate);
		        	vehicleRole.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		        	vehicleRole.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		        	vehicleRole.store();
		        }
				
			}else if(UtilValidate.isNotEmpty(startDate)){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		        conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
		        conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE"));
		        conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,"_NA_"));
				conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.NOT_EQUAL,null));
				conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN,startDate));
				conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN,startDate));
				
				EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				vehicleRoleList = delegator.findList("VehicleRole", condition, null, null, null, false);
				if(UtilValidate.isNotEmpty(vehicleRoleList)){
					Debug.logError("VehicleRole aleady created for the Period..!",module);
					return ServiceUtil.returnError("VehicleRole already created for the Period..!");
				}else{
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
			        conditionList.add(EntityCondition.makeCondition("vehicleId",EntityOperator.EQUALS,vehicleId));
			        conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PTC_VEHICLE"));
			        conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,"_NA_"));
			        EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			        vehicleRoleList = delegator.findList("VehicleRole", cond, null, UtilMisc.toList("-fromDate"), null, false);
			        GenericValue vehicleRole = EntityUtil.getFirst(vehicleRoleList);
			        if(UtilValidate.isEmpty(vehicleRole.getTimestamp("thruDate"))){
			        	vehicleRole.set("thruDate", thruDate);
			        	vehicleRole.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			        	vehicleRole.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			        	vehicleRole.store();
			        }
			        if(UtilValidate.isNotEmpty(startDate)){
			        	if(UtilValidate.isNotEmpty(contractor)){
			        		if(!partyId.equals(contractor)){
			        			if(partyId.equals("TEMP_CONTRACTOR")){
			        				vehicleRole.remove();
			        			}
			        			partyId=contractor;
			        		}
			        	}
				        GenericValue newEntity = delegator.makeValue("VehicleRole");
				        newEntity.set("vehicleId", vehicleId);
				        newEntity.set("facilityId", "_NA_");
		     	        newEntity.set("roleTypeId", "PTC_VEHICLE");
		     	        newEntity.set("partyId", partyId);
		     	        newEntity.set("fromDate", startDate);
		     	        if(UtilValidate.isNotEmpty(endDate)){
		     	        newEntity.set("thruDate", endDate);
		     	        }
		     	        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
		    	        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
		 		        try {
		 					delegator.create(newEntity);
		 				}catch (GenericEntityException e) {
		 					Debug.logError("Error in creating Vechicle Role: "+vehicleId+ "\t"+e.toString(),module);
		 					return ServiceUtil.returnError("Error in creating Vehicle Role: "+vehicleId+ "\t"+e.toString());
		 				}
			        }
				}
			}
		}catch (GenericEntityException e) {
			Debug.logError(e, module);
         return ServiceUtil.returnError(e.getMessage());
		} 
		result = ServiceUtil.returnSuccess("Successfully Updated..!");
		return result;
 }
 
 
 public static Map<String, Object> UpdateEditMilkReceiptRecord(DispatchContext ctx,Map<String, Object> context) {
	 Map<String, Object> finalResult = FastMap.newInstance();
	 Map<String, Object> result = FastMap.newInstance();
	 Delegator delegator = ctx.getDelegator();
	 LocalDispatcher dispatcher = ctx.getDispatcher();
     Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
     GenericValue userLogin = (GenericValue) context.get("userLogin");
     String milkTransferId = (String)context.get("milkTransferId");
     String sequenceNum = (String)context.get("sequenceNum");
     String tankerNo = (String) context.get("vehicleId");
     String fromPartyId = (String) context.get("partyId");
	 String productId = (String) context.get("productId");
	 String siloId = (String) context.get("siloId");
 
	 BigDecimal dispatchWeight = (BigDecimal) context.get("dispatchWeight");
	 BigDecimal grossWeight = (BigDecimal) context.get("grossWeight");
	 BigDecimal tareWeight = (BigDecimal) context.get("tareWeight");
	 
	 BigDecimal sendTemp = (BigDecimal) context.get("sendTemp");
	 BigDecimal sendAcid = (BigDecimal) context.get("sendAcid");
	 BigDecimal sendCLR = (BigDecimal) context.get("sendCLR");
	 BigDecimal sendFat = (BigDecimal) context.get("sendFat");
	 BigDecimal sendSnf = (BigDecimal) context.get("sendSnf");
	 String sendCob = (String) context.get("sendCob");
	 
	 BigDecimal recdTemp = (BigDecimal) context.get("recdTemp");
	 BigDecimal recdAcid = (BigDecimal) context.get("recdAcid");
	 BigDecimal recdCLR = (BigDecimal) context.get("recdCLR");
	 BigDecimal recdFat = (BigDecimal) context.get("recdFat");
	 BigDecimal recdSnf = (BigDecimal) context.get("recdSnf");
	 String recdCob = (String) context.get("recdCob");
	 
	 String recdOrganoLepticTest = (String) context.get("recdOrganoLepticTest");
	 String recdSedimentTest = (String) context.get("recdSedimentTest");
		
	 //need to calcultae actual weight of the milk received i.e recdQty,sendQty
	 BigDecimal sendQty = BigDecimal.ZERO;
	 BigDecimal recdQty = BigDecimal.ZERO;
	 BigDecimal sendQuantityLtrs = BigDecimal.ZERO;
	 BigDecimal recdQuantityLtrs = BigDecimal.ZERO;
	
	 BigDecimal sendKgFat = BigDecimal.ZERO;
	 BigDecimal sendKgSnf = BigDecimal.ZERO;
	 BigDecimal receivedKgFat = BigDecimal.ZERO;
	 BigDecimal receivedKgSnf = BigDecimal.ZERO;
     if(UtilValidate.isNotEmpty(dispatchWeight)){
    	 sendQty=dispatchWeight;
     }
     if(UtilValidate.isNotEmpty(tareWeight) && UtilValidate.isNotEmpty(grossWeight)){
		 recdQty = (grossWeight).subtract(tareWeight);
	 }
     if(UtilValidate.isNotEmpty(sendQty) && sendQty.compareTo(BigDecimal.ZERO)>0){
    	 sendQuantityLtrs= ProcurementNetworkServices.convertKGToLitreSetScale(sendQty,false);	
     }
     if(UtilValidate.isNotEmpty(recdQty) && recdQty.compareTo(BigDecimal.ZERO)>0){
    	 recdQuantityLtrs= ProcurementNetworkServices.convertKGToLitreSetScale(recdQty,false);	
     }
     if(sendQty.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(sendFat) && sendFat.compareTo(BigDecimal.ZERO)>0){
		sendKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sendQty,sendFat);
	 }
	 if(sendQty.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(sendSnf) && sendSnf.compareTo(BigDecimal.ZERO)>0){
		 sendKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(sendQty,sendSnf);
	 }
	 if(recdQty.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(recdFat) && recdFat.compareTo(BigDecimal.ZERO)>0){
		 receivedKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(recdQty,recdFat);
	 }
	 if(recdQty.compareTo(BigDecimal.ZERO)>0 && UtilValidate.isNotEmpty(recdSnf) && recdSnf.compareTo(BigDecimal.ZERO)>0){
		 receivedKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(recdQty,recdSnf);
	 }
     Map vehicleTripStatusResult = FastMap.newInstance();
	 String containerId= null;
	 String seqNum= null;
	 String newSeqNum= null;
	 String partyId =null;
	 //update MilkTransfer and milktransferitem and vehicle trip status details ==========
		try{
			GenericValue milkTransfer = delegator.findOne("MilkTransfer", UtilMisc.toMap("milkTransferId", milkTransferId), false);

		// vehicleTripStatus creation
			if(UtilValidate.isNotEmpty(milkTransfer)){
				containerId=(String) milkTransfer.get("containerId");
				seqNum=(String) milkTransfer.get("sequenceNum");
				partyId=(String) milkTransfer.get("partyId");

				if(!containerId.equals(tankerNo) && UtilValidate.isNotEmpty(tankerNo)){
					Map vehicleTripMap = FastMap.newInstance();
					vehicleTripMap.put("oldTankerNo",containerId);
					vehicleTripMap.put("newTankerNo",tankerNo);
					vehicleTripMap.put("seqNum",seqNum);
					vehicleTripMap.put("fromPartyId",fromPartyId);
					vehicleTripMap.put("userLogin",userLogin);
					try{
					 	vehicleTripStatusResult = dispatcher.runSync("updateVehicleTripStatusAndTrip", vehicleTripMap);
					 	if( ServiceUtil.isError(vehicleTripStatusResult)) {
								String errMsg =  ServiceUtil.getErrorMessage(vehicleTripStatusResult);
								Debug.logWarning(errMsg , module);
							    return ServiceUtil.returnError("Error While Updating MilkTransfer Details 2: "+errMsg);
						}else{
							newSeqNum =(String) vehicleTripStatusResult.get("sequenceNum");
						}
					 }catch (GenericServiceException e) {
							// TODO: handle exception
					 	Debug.logError(e,module);
					 }
				}else if(!partyId.equals(fromPartyId) && UtilValidate.isNotEmpty(fromPartyId)){
					 try{
				        	GenericValue vehicleTripChangeParty = delegator.findOne("VehicleTrip", UtilMisc.toMap("vehicleId", tankerNo,"sequenceNum",seqNum), false);
				        	vehicleTripChangeParty.set("partyId",fromPartyId);
				        	vehicleTripChangeParty.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
				        	delegator.createOrStore(vehicleTripChangeParty);
				        }catch (Exception e) {
							// TODO: handle exception
				        	Debug.logError("Error while updating party in Vehicle trip=========="+e, module);
				        	return ServiceUtil.returnError("Error while updating  party in Vehicle trip=========="+e.getMessage());
						}
				}

			}
      // update MilkTransfer and milktransferitem
		GenericValue milkTransferItem=null;
		List<GenericValue> milkTransferItemList = delegator.findList("MilkTransferItem", EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS, milkTransferId), null, null, null, false);
			if(UtilValidate.isNotEmpty(milkTransferItemList)){
				milkTransferItem = EntityUtil.getFirst(milkTransferItemList);
				String silo =(String)milkTransferItem.get("siloId");
				BigDecimal sendLR=(BigDecimal) milkTransferItem.get("sendLR");
				BigDecimal receivedLR=(BigDecimal) milkTransferItem.get("receivedLR");
				BigDecimal sendTemparature=(BigDecimal) milkTransferItem.get("sendTemparature");
				BigDecimal receivedTemparature=(BigDecimal) milkTransferItem.get("receivedTemparature");
				BigDecimal sendAcidity=(BigDecimal) milkTransferItem.get("sendAcidity");
				BigDecimal receivedAcidity=(BigDecimal) milkTransferItem.get("receivedAcidity");
	    		String sendedCob = (String) milkTransferItem.get("sendCob");
	    		String receivedCob = (String) milkTransferItem.get("receivedCob");
	    		String receivedOrganoLepticTest = (String) milkTransferItem.get("recdOrganoLepticTest");
	    		String receivedSedimentTest = (String) milkTransferItem.get("receivedSedimentTest");
	    		if(!silo.equals(siloId) && UtilValidate.isNotEmpty(siloId)){
					milkTransferItem.set("siloId", siloId);
				}
	    		if(!sendLR.equals(sendCLR) && UtilValidate.isNotEmpty(sendCLR)){
					milkTransferItem.set("sendLR", sendCLR);
				}
	    		if(!receivedLR.equals(recdCLR) && UtilValidate.isNotEmpty(recdCLR)){
					milkTransferItem.set("receivedLR", recdCLR);
				}
	    		if(!sendTemparature.equals(sendTemp) && UtilValidate.isNotEmpty(sendTemp)){
					milkTransferItem.set("sendTemparature", sendTemp);
				}
	    		if(!receivedTemparature.equals(recdTemp) && UtilValidate.isNotEmpty(recdTemp)){
					milkTransferItem.set("receivedTemparature", recdTemp);
				}
	    		if(!sendAcidity.equals(sendAcid) && UtilValidate.isNotEmpty(sendAcid)){
					milkTransferItem.set("sendAcidity", sendAcid);
				}
	    		if(!receivedAcidity.equals(recdAcid) && UtilValidate.isNotEmpty(recdAcid)){
					milkTransferItem.set("receivedAcidity", recdAcid);
				}
	    		if(!sendedCob.equals(sendCob) && UtilValidate.isNotEmpty(sendCob)){
					milkTransferItem.set("sendCob", sendCob);
				}
	    		if(!receivedCob.equals(recdCob) && UtilValidate.isNotEmpty(recdCob)){
					milkTransferItem.set("receivedCob", recdCob);
				}
	    		if(!receivedOrganoLepticTest.equals(recdOrganoLepticTest) && UtilValidate.isNotEmpty(recdOrganoLepticTest)){
					milkTransferItem.set("recdOrganoLepticTest", recdOrganoLepticTest);
				}
	    		if(!receivedSedimentTest.equals(recdSedimentTest) && UtilValidate.isNotEmpty(recdSedimentTest)){
					milkTransferItem.set("receivedSedimentTest", recdSedimentTest);
				}
			}
			if(UtilValidate.isNotEmpty(milkTransfer)){
				containerId=(String) milkTransfer.get("containerId");
				seqNum=(String) milkTransfer.get("sequenceNum");
	    		partyId = (String) milkTransfer.get("partyId");
	    		String prodId = (String) milkTransfer.get("productId");
	    		BigDecimal dWeight = (BigDecimal) milkTransfer.get("dispatchWeight");
	    		BigDecimal gWeight = (BigDecimal) milkTransfer.get("grossWeight");
	    		BigDecimal tWeight = (BigDecimal) milkTransfer.get("tareWeight");
				BigDecimal fat = (BigDecimal)milkTransfer.get("fat");
				BigDecimal snf = (BigDecimal)milkTransfer.get("snf");
				BigDecimal quantity = (BigDecimal)milkTransfer.get("quantity");
				BigDecimal receivedQuantity = (BigDecimal)milkTransfer.get("receivedQuantity");
				BigDecimal receivedQuantityLtrs	 = (BigDecimal)milkTransfer.get("receivedQuantityLtrs	");
				BigDecimal receivedFat = (BigDecimal)milkTransfer.get("receivedFat");
				BigDecimal receivedSnf = (BigDecimal)milkTransfer.get("receivedSnf");
				if(!prodId.equals(productId) && UtilValidate.isNotEmpty(productId)){
					milkTransfer.set("productId", productId);
					if(UtilValidate.isNotEmpty(milkTransferItem)){
						milkTransferItem.set("receivedProductId", productId);
					}
				}
				if(UtilValidate.isNotEmpty(newSeqNum)){
					milkTransfer.set("sequenceNum", newSeqNum);
					//milkTransferItem.set("sequenceNum", newSeqNum);
				}
				if(!containerId.equals(tankerNo) && UtilValidate.isNotEmpty(tankerNo)){
					milkTransfer.set("containerId", tankerNo);
				}
				if(!partyId.equals(fromPartyId) && UtilValidate.isNotEmpty(fromPartyId)){
					milkTransfer.set("partyId", fromPartyId);
				}
				if(!dWeight.equals(dispatchWeight) && UtilValidate.isNotEmpty(dispatchWeight)){
					milkTransfer.set("dispatchWeight", dispatchWeight);
				}
				if(!gWeight.equals(grossWeight) && UtilValidate.isNotEmpty(grossWeight)){
					milkTransfer.set("grossWeight", grossWeight);
				}
				if(!tWeight.equals(tareWeight) && UtilValidate.isNotEmpty(tareWeight)){
					milkTransfer.set("tareWeight", tareWeight);
				}
				if(!quantity.equals(sendQty) && UtilValidate.isNotEmpty(sendQty)){
					milkTransfer.set("quantity", sendQty);
					milkTransfer.set("quantityLtrs", sendQuantityLtrs);
					if(UtilValidate.isNotEmpty(milkTransferItem)){
						milkTransferItem.set("quantity", sendQty);
						milkTransferItem.set("quantityLtrs", sendQuantityLtrs);
					}
				}
				if(!receivedQuantity.equals(recdQty) && UtilValidate.isNotEmpty(recdQty)){
					milkTransfer.set("receivedQuantity", recdQty);
					milkTransfer.set("receivedQuantityLtrs", recdQuantityLtrs);
					if(UtilValidate.isNotEmpty(milkTransferItem)){
						milkTransferItem.set("receivedQuantity", recdQty);
						milkTransferItem.set("receivedQuantityLtrs", recdQuantityLtrs);
					}
				}
				if((!fat.equals(sendFat) && UtilValidate.isNotEmpty(sendFat)) || (!snf.equals(sendSnf) && UtilValidate.isNotEmpty(sendSnf))){
					milkTransfer.set("fat", sendFat);
					milkTransfer.set("sendKgFat", sendKgFat);
					milkTransfer.set("snf", sendSnf);
					milkTransfer.set("sendKgSnf", sendKgFat);
					if(UtilValidate.isNotEmpty(milkTransferItem)){
						milkTransferItem.set("fat", sendFat);
						milkTransferItem.set("sendKgFat", sendKgFat);
						milkTransferItem.set("snf", sendSnf);
						milkTransferItem.set("sendKgSnf", sendKgFat);
					}
				}
				if((!receivedFat.equals(recdFat) && UtilValidate.isNotEmpty(recdFat)) || (!receivedSnf.equals(recdSnf) && UtilValidate.isNotEmpty(recdSnf))){
					milkTransfer.set("receivedFat", recdFat);
					milkTransfer.set("receivedKgFat", receivedKgFat);
					milkTransfer.set("receivedSnf", recdSnf);
					milkTransfer.set("receivedKgSnf", receivedKgSnf);
					if(UtilValidate.isNotEmpty(milkTransferItem)){
						milkTransferItem.set("receivedFat", recdFat);
						milkTransferItem.set("receivedKgFat", receivedKgFat);
						milkTransferItem.set("receivedSnf", recdSnf);
						milkTransferItem.set("receivedKgSnf", receivedKgSnf);
					}
				}
				milkTransfer.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
	        	milkTransferItem.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
	    	}
			 try{
				 milkTransferItem.store();
			 }catch(GenericEntityException e){
				 Debug.logError("Error While Updating milkTransferItem Details11 "+e, module);
				  return ServiceUtil.returnError("Error While Updating milkTransferItem Details11 : "+milkTransferId);
			 }
			 try{
				 milkTransfer.store();
			 }catch(GenericEntityException e){
				 Debug.logError("Error While Updating milkTransfer Details222 "+e, module);
				  return ServiceUtil.returnError("Error While Updating milkTransferItem Details2221 : "+milkTransferId);
				 
			 }
		}catch (Exception e) {
			  Debug.logError(e, "Error While Updating MilkTransfer Details1 ", module);
			  return ServiceUtil.returnError("Error While Updating MilkTransfer Details1 : "+milkTransferId);
	 	}
		result = ServiceUtil.returnSuccess("Successfully Updated..!");
		return result;
 }

public static Map<String, Object> updateVehicleTripStatusAndTrip(DispatchContext dctx, Map<String, ? extends Object> context) {
	LocalDispatcher dispatcher = dctx.getDispatcher();
	Map<String, Object> resultMap = FastMap.newInstance();
 	Delegator delegator = dctx.getDelegator();
 	GenericValue userLogin = (GenericValue) context.get("userLogin");
 	String seqNum = (String) context.get("seqNum");
 	String oldTankerNo = (String) context.get("oldTankerNo"); 
 	String newTankerNo = (String) context.get("newTankerNo"); 
 	String fromPartyId = (String) context.get("fromPartyId");
	String sequenceNum = null;
	// Getting oldVehicle Record and creating new VehicleTrip Record
	GenericValue vehicleTrip =null;				
	try{        	
		vehicleTrip = delegator.findOne("VehicleTrip", UtilMisc.toMap("vehicleId", oldTankerNo,"sequenceNum",seqNum), false);
		if(UtilValidate.isNotEmpty(vehicleTrip)){
			String vehicleId =(String) vehicleTrip.get("vehicleId");
			String partyId = (String) vehicleTrip.get("partyId");
			Timestamp estimatedStartDate = (Timestamp) vehicleTrip.get("estimatedStartDate");
			Timestamp estimatedEndDate = (Timestamp) vehicleTrip.get("estimatedEndDate");
			Timestamp createdDate = (Timestamp) vehicleTrip.get("createdDate");
			 String createdByUserLogin = (String) vehicleTrip.get("createdByUserLogin");
		     try{        	
			 		Map vehicleTripMap = FastMap.newInstance();
			 	    Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
			 		vehicleTripMap.put("vehicleId", newTankerNo);
			 		vehicleTripMap.put("partyId", fromPartyId);
			 		vehicleTripMap.put("estimatedStartDate", estimatedStartDate);
			 		vehicleTripMap.put("estimatedEndDate", estimatedEndDate);
			 		vehicleTripMap.put("createdDate", createdDate);
			 		vehicleTripMap.put("userLogin", userLogin);
			 		vehicleTripMap.put("createdByUserLogin",createdByUserLogin);
			 		Map vehicleTripResultMap = dispatcher.runSync("createVehicleTrip", vehicleTripMap);
			 		if(ServiceUtil.isError(vehicleTripResultMap)){
			 			Debug.logError("Error While Creating vehicleTrip :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
			 			resultMap = ServiceUtil.returnError("Error while creating vehicle Trip ");
			 			return resultMap;
			 		}
			 		sequenceNum = (String)vehicleTripResultMap.get("sequenceNum");
				}catch(Exception e){
					 Debug.logError("Error while creating vehicle trip details "+e, module);
					 resultMap = ServiceUtil.returnError("Error while creating vehicle trip details");
					 return resultMap;
				 }
		        vehicleTrip.remove();
		}
	 }catch(Exception e){
		 Debug.logError("Error while creating vehicle trip details "+e, module);
		 resultMap = ServiceUtil.returnError("Error while creating vehicle trip details");
		 return resultMap;
	 }
  // deleting old tanker record and create new tanker in VEHICLE TRIP STATUS
	try{
		List conList=FastList.newInstance();
		conList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS,oldTankerNo));
		conList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS,seqNum));
		EntityCondition saleCond = EntityCondition.makeCondition(conList, EntityOperator.AND);
		List<GenericValue> vehicleTripStatusItr = delegator.findList("VehicleTripStatus", saleCond, null, null, null, false);
		if(UtilValidate.isNotEmpty(vehicleTripStatusItr)){
    		for(GenericValue vehicleStatusDetails : vehicleTripStatusItr){
    			String statusId = vehicleStatusDetails.getString("statusId");
				Timestamp estimatedStartDateVts =  vehicleStatusDetails.getTimestamp("estimatedStartDate");
				Timestamp estimatedEndDateVts =  vehicleStatusDetails.getTimestamp("estimatedEndDate");
				Timestamp createdDateVts =  vehicleStatusDetails.getTimestamp("createdDate");
                String createdByUserLoginVts =  vehicleStatusDetails.getString("createdByUserLogin");
                try{        	
					GenericValue newVehicleTripStatus = delegator.makeValue("VehicleTripStatus");
					newVehicleTripStatus.set("vehicleId", newTankerNo);
					newVehicleTripStatus.set("sequenceNum", sequenceNum);
					newVehicleTripStatus.set("statusId", statusId);
					newVehicleTripStatus.set("estimatedStartDate", estimatedStartDateVts);
		    		if(UtilValidate.isNotEmpty(estimatedEndDateVts)){
		    			newVehicleTripStatus.set("estimatedEndDate", estimatedEndDateVts);
		    		}
		    		if(UtilValidate.isNotEmpty(createdDateVts)){
		    			newVehicleTripStatus.set("createdDate", createdDateVts);
		    		}
		    		if(UtilValidate.isNotEmpty(createdByUserLoginVts)){
		    			newVehicleTripStatus.set("createdByUserLogin", createdByUserLoginVts);
		    		}
		    		newVehicleTripStatus.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
		    		newVehicleTripStatus.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    		//newVehicleTripStatus.set("lastModifiedByUserLogin", userLogin);
					delegator.create(newVehicleTripStatus);
                }catch(Exception e){
					Debug.logError("Error while creating VehicleTripStatus details "+e, module);
					resultMap = ServiceUtil.returnError("Error while creating VehicleTripStatus trip details");
					return resultMap;
				}
    		}
    		 try{
    			 delegator.removeAll(vehicleTripStatusItr);
					//vehicleTripStatusItr.remove();
				}catch(Exception e){
					 Debug.logError("Error while removing old vehicle trip status details "+e, module);
					 resultMap = ServiceUtil.returnError("Error while removing old vehicle trip status details");
					 return resultMap;
				}
    		}
	}catch(Exception e){
		 Debug.logError("Error while creating vehicle trip status details "+e, module);
		 resultMap = ServiceUtil.returnError("Error while creating vehicle trip status details");
		 return resultMap;
	}
	
	 resultMap.put("vehicleId", newTankerNo);
     resultMap.put("sequenceNum", sequenceNum);
     return resultMap;
}
 
}
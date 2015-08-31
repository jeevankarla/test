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
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.view.ApacheFopWorker;
import org.ofbiz.widget.fo.FoScreenRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.ofbiz.widget.screen.ScreenRenderer;

import in.vasista.vbiz.production.ProductionNetworkServices;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;

import org.xml.sax.SAXException;

import in.vasista.vbiz.procurement.ProcurementNetworkServices;

import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * Procurement Services
 */
public class ProductionServices {

	public static final String module = ProductionServices.class.getName();
	public static final String resource = "CommonUiLabels";
	protected static final HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected static final FoScreenRenderer foScreenRenderer = new FoScreenRenderer();
    
    /**
     * returns Opening Balance map for the given Silo(Facility) as on that Date
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getSiloInventoryOpeningBalance(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
        String facilityId = (String)context.get("facilityId");
        String productId = (String)context.get("productId");
        Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue facility = null;
		List<String> productIds = FastList.newInstance();
        try {
            facility = delegator.findByPrimaryKey("Facility", UtilMisc.toMap("facilityId", facilityId));
            
            if(UtilValidate.isEmpty(productId)){
            	List<GenericValue> productFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);
            	if(UtilValidate.isNotEmpty(productFacility)){
            		productIds = EntityUtil.getFieldListFromEntityList(productFacility, "productId", true);
            	}
            	
            }
            
        } catch (GenericEntityException e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError("Error fetching data " + e);
        }
        
        if(UtilValidate.isEmpty(effectiveDate)){
        	effectiveDate = UtilDateTime.nowTimestamp();
        }
        
        if(UtilValidate.isEmpty(facility)){
        	Debug.logError("Silo with code "+facilityId+" doesn't exists", module);
        	return ServiceUtil.returnError("Silo with code "+facilityId+" doesn't exists");
        }
        
        
        List conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN, effectiveDate));
        conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
        if(UtilValidate.isEmpty(productId)){
            conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
        }else{
            conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        }
   	 	//conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

        BigDecimal inventoryCount = BigDecimal.ZERO;
        BigDecimal inventoryKgFat = BigDecimal.ZERO;
        BigDecimal inventoryKgSnf = BigDecimal.ZERO;
        String invProductId="";
        EntityListIterator eli = null;
        Map openingBalanceMap = FastMap.newInstance();
        
        try {
            eli = delegator.find("InventoryItemAndDetail", condition, null, null, UtilMisc.toList("effectiveDate"), null);
            GenericValue inventoryTrans;
            while ((inventoryTrans = eli.next()) != null) {
            	BigDecimal quantityOnHandDiff = inventoryTrans.getBigDecimal("quantityOnHandDiff");
                BigDecimal fatPercent = inventoryTrans.getBigDecimal("fatPercent");
                BigDecimal snfPercent = inventoryTrans.getBigDecimal("snfPercent");
                invProductId = inventoryTrans.getString("productId");
                BigDecimal kgFat =BigDecimal.ZERO;
                BigDecimal kgSnf =BigDecimal.ZERO;
                kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(inventoryCount, fatPercent);
                kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(inventoryCount, fatPercent);
               
                inventoryCount = inventoryCount.add(quantityOnHandDiff);
                inventoryKgFat = inventoryKgFat.add(kgFat);
                inventoryKgSnf = inventoryKgSnf.add(kgSnf);
            }
            BigDecimal fatPercent = BigDecimal.ZERO;
            BigDecimal snfPercent = BigDecimal.ZERO;
            
            fatPercent = ProcurementNetworkServices.calculateFatOrSnf(inventoryKgFat, inventoryCount);
            snfPercent = ProcurementNetworkServices.calculateFatOrSnf(inventoryKgSnf, inventoryCount);
            
            openingBalanceMap.put("invProductId",invProductId);
            openingBalanceMap.put("fat",fatPercent);
            openingBalanceMap.put("snf",snfPercent);
            openingBalanceMap.put("kgFat",inventoryKgFat);
            openingBalanceMap.put("kgSnf",inventoryKgSnf);
            openingBalanceMap.put("productId",productId);
            openingBalanceMap.put("quantityKgs",inventoryCount);
            eli.close();
        }
        catch(GenericEntityException e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
        }
        result.put("openingBalance",openingBalanceMap);
        return result;
    }// End of the Service
    
    public static Map<String, Object> getProductionRunDetails(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String workEffortId = (String)context.get("workEffortId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = FastMap.newInstance();
            EntityListIterator returnItems = null;
            EntityListIterator eli = null;
            EntityListIterator eliForQc = null;

            List issuedProductsList = FastList.newInstance();
            List declareProductsList = FastList.newInstance();
            List returnProductsList = FastList.newInstance();
            List qcComponentsList = FastList.newInstance();
            try {
            	List conditionList = FastList.newInstance();
	            conditionList.add(EntityCondition.makeCondition("workEffortParentId", EntityOperator.EQUALS, workEffortId));
	            conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "PRUN_COMPLETED"));
	            EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            	List<GenericValue> workEffort = delegator.findList("WorkEffort", condition, null, UtilMisc.toList("workEffortId"), null, false);
            	
            	if(UtilValidate.isNotEmpty(workEffort)){
		        	List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(workEffort, "workEffortId", true);
		            eli = delegator.find("InventoryItemAndDetail", EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds), null, null, null, null);
		            GenericValue productionTrans;
		            while ((productionTrans = eli.next()) != null) {
		                Map issuedProductsMap = FastMap.newInstance();
		                Map declareProductsMap = FastMap.newInstance();
		                BigDecimal fatPercent = BigDecimal.ZERO;
		                BigDecimal snfPercent = BigDecimal.ZERO;
		                String productId = productionTrans.getString("productId");
		            	BigDecimal quantityOnHandDiff = productionTrans.getBigDecimal("quantityOnHandDiff");
		            	if(UtilValidate.isNotEmpty(productionTrans.getBigDecimal("fatPercent"))){
		            	fatPercent = productionTrans.getBigDecimal("fatPercent");
		            	}
		            	if(UtilValidate.isNotEmpty(productionTrans.getBigDecimal("snfPercent"))){
		            	snfPercent = productionTrans.getBigDecimal("snfPercent");
		            	}
		            	String inventoryTransferId = productionTrans.getString("inventoryTransferId");
	                    String productBatchId = productionTrans.getString("productBatchId");

		             	if((quantityOnHandDiff.compareTo(BigDecimal.ZERO) <= 0) && (UtilValidate.isEmpty(inventoryTransferId))){
		             		issuedProductsMap.put("issuedProdId",productId);
		             		issuedProductsMap.put("issuedQty",quantityOnHandDiff);
		             		issuedProductsMap.put("fatPercent",fatPercent);
		             		issuedProductsMap.put("snfPercent",snfPercent);
		             		issuedProductsList.add(issuedProductsMap);
		                } else if((quantityOnHandDiff.compareTo(BigDecimal.ZERO) >= 0) && (UtilValidate.isNotEmpty(productBatchId))){
		                    	List<GenericValue> productBatchSequence = delegator.findList("ProductBatchAndSequence", EntityCondition.makeCondition("productBatchId", EntityOperator.EQUALS, productBatchId), null, UtilMisc.toList("sequenceId"), null, false);
		                     	if(UtilValidate.isNotEmpty(productBatchSequence)){
		                     		String batchNo = (EntityUtil.getFirst(productBatchSequence)).getString("sequenceId");
		                 		    declareProductsMap.put("batchNo",batchNo);
		                     	}
		             		declareProductsMap.put("declareProdId",productId);
		             		declareProductsMap.put("declareQty",quantityOnHandDiff);
		             		declareProductsMap.put("fatPercent",fatPercent);
		             		declareProductsMap.put("snfPercent",snfPercent);
		             		declareProductsList.add(declareProductsMap);
		             	} 
		            }
		            eli.close();
		            
		          //production run Return Products Details
	            	conditionList.clear();
		            conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
		            conditionList.add(EntityCondition.makeCondition("transferGroupTypeId", EntityOperator.EQUALS, "RETURN_XFER"));
		            EntityCondition conditionForReturn = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            	List<GenericValue> inventoryTransferGroup = delegator.findList("InventoryTransferGroup", conditionForReturn, null, null, null, false);
	            	if(UtilValidate.isNotEmpty(inventoryTransferGroup)){
	               	    List<String> transferGroupIds = EntityUtil.getFieldListFromEntityList(inventoryTransferGroup, "transferGroupId", true);
    	            	returnItems = delegator.find("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.IN, transferGroupIds), null, null, null, null);
		            	if(UtilValidate.isNotEmpty(returnItems)){
				            GenericValue returnTrans;
				            while ((returnTrans = returnItems.next()) != null) {
				                Map returnProductsMap = FastMap.newInstance();
				            	String returnTransferGroupId = returnTrans.getString("transferGroupId");
				            	String returnInventoryTransferId = returnTrans.getString("inventoryTransferId");
				                String returnProdId = returnTrans.getString("productId");
				            	BigDecimal returnQty = returnTrans.getBigDecimal("xferQty");
				            	if(UtilValidate.isNotEmpty(returnTransferGroupId)){
				             		returnProductsMap.put("returnId",returnTransferGroupId);
				             		returnProductsMap.put("returnItemSeqId",returnInventoryTransferId);
				             		returnProductsMap.put("returnProdId",returnProdId);
				             		returnProductsMap.put("returnQty",returnQty);
				             		returnProductsList.add(returnProductsMap);
				             	}
				            }
		            	}
		            	returnItems.close();
	            	}
		            // QC Details for Production Run
		            conditionList.clear();
		            conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
		            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "QC_ACCEPT"));
		            EntityCondition conditionForQc = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            	List<GenericValue> productQcTest = delegator.findList("ProductQcTest", conditionForQc, null, null, null, false);
	            	if(UtilValidate.isNotEmpty(productQcTest)){
	               	    List<String> qcTestIds = EntityUtil.getFieldListFromEntityList(productQcTest, "qcTestId", true);

			            eliForQc = delegator.find("ProductQcTestDetails", EntityCondition.makeCondition("qcTestId", EntityOperator.IN, qcTestIds), null, null, null, null);
		            	if(UtilValidate.isNotEmpty(eliForQc)){
				            GenericValue qcTrans;
				            while ((qcTrans = eliForQc.next()) != null) {
				                Map qcComponentsMap = FastMap.newInstance();
				            	String sequenceNumber = qcTrans.getString("sequenceNumber");
				            	String testComponent = qcTrans.getString("testComponent");
				                String testValue = qcTrans.getString("value");
				                String qcProductId = qcTrans.getString("productId");
				                String qcTestId = qcTrans.getString("qcTestId");

		            		    qcComponentsMap.put("qcProductId",qcProductId);
			            		qcComponentsMap.put("qcTestId",qcTestId);
			            		qcComponentsMap.put("sequenceNumber",sequenceNumber);
			            		qcComponentsMap.put("testComponent",testComponent);
			            		qcComponentsMap.put("testValue",testValue);
			            		qcComponentsList.add(qcComponentsMap);
				            }
		            	}
		            	eliForQc.close();
	            	}
		            
            	}
            }
            catch(GenericEntityException e){
            	Debug.logError(e, module);
            	return ServiceUtil.returnError(e.toString());
            }
            result.put("issuedProductsList",issuedProductsList);
            result.put("declareProductsList",declareProductsList);
            result.put("returnProductsList",returnProductsList);
            result.put("qcComponentsList",qcComponentsList);
        	
        return result;
    }// End of the Service
    
    public static String issueRoutingTaskNeededMaterial(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String workEffortId = (String) request.getParameter("workEffortId");
	  	  boolean beginTransaction = false;
	  	  try{
	  		  beginTransaction = TransactionUtil.begin();
		  	  
	  		  String productionRunId = (String)ProductionNetworkServices.getRootProductionRun(delegator, workEffortId);
	  		  
	  		  GenericValue productionRun = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", productionRunId), false);
	  		  
	  		  if(UtilValidate.isEmpty(productionRun)){
	  			  Debug.logError("Primary Production run doesn't exist for Task: "+workEffortId, module);
				  request.setAttribute("_ERROR_MESSAGE_", "Primary Production run doesn't exist for Task: "+workEffortId);	  		  
		  		  return "error";
	  		  }
	  		
	  		  boolean enableInputPrunIssueDate = Boolean.FALSE;
	  		  GenericValue tenantConfigEnableCancelAfterShipDate = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PRUN_CHECK", "propertyName","enableInputPrunIssueDate"), false);
	  		  if (UtilValidate.isNotEmpty(tenantConfigEnableCancelAfterShipDate) && (tenantConfigEnableCancelAfterShipDate.getString("propertyValue")).equals("Y")) {
				 enableInputPrunIssueDate = Boolean.TRUE;
	  		  }

	  		  String productionFloorId = productionRun.getString("facilityId"); 
	  		
	  		  for (int i = 0; i < rowCount; i++){
		  		  
		  		  String facilityId = "";
		  		  String productId = "";
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  BigDecimal quantity = BigDecimal.ZERO;
		  		  String quantityStr = "";
		  		  if (paramMap.containsKey("facilityId" + thisSuffix)) {
		  			  facilityId = (String) paramMap.get("facilityId" + thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("productId" + thisSuffix)) {
		  			  productId = (String) paramMap.get("productId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
		  			quantityStr = (String) paramMap.get("quantity"+thisSuffix);
		  		  }
		  		  
		  		  if(UtilValidate.isNotEmpty(quantityStr)){
		  			  quantity = new BigDecimal(quantityStr);
		  		  }
		  		  
			  		GenericValue productFacility = delegator.findOne("ProductFacility", UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
	                if(UtilValidate.isEmpty(productFacility)){
	                	Debug.logError("Material not associated to the Plant/Silo: "+facilityId, module);
	                	request.setAttribute("_ERROR_MESSAGE_", "Material not associated to the Plant/Silo: "+facilityId);
	                	TransactionUtil.rollback();
	            		return "error";
	                }
	                
	                Map inputCtx = FastMap.newInstance();
	                inputCtx.put("productId", productId);
	                inputCtx.put("facilityId", facilityId);
	                inputCtx.put("quantity", quantity);
	                if(enableInputPrunIssueDate){
	                	inputCtx.put("issuedInputDate", productionRun.getTimestamp("estimatedStartDate"));
	                }
	                inputCtx.put("workEffortId", workEffortId);
	                inputCtx.put("userLogin", userLogin);
	                Map resultCtx = dispatcher.runSync("issueProductionRunTaskComponent", inputCtx);
	                if(ServiceUtil.isError(resultCtx)){
	                	Debug.logError("Error issuing material for routing task : "+workEffortId, module);
	                	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultCtx));
	                	TransactionUtil.rollback();
	            		return "error";
	                }
		  	  }
	  	  }
	  	catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
  	  	catch (GenericServiceException e) {
  	  		try {
  			  TransactionUtil.rollback(beginTransaction, "Error while calling services", e);
  	  		} catch (GenericEntityException e2) {
  			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  			  request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
  	  		}
  	  		Debug.logError("An entity engine error occurred while calling services", module);
  	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beginTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Successfully made issue of material for Task :"+workEffortId);
		return "success";  
    }
    
    public static String returnUnusedMaterialOfRoutingTask(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimestamp = UtilDateTime.nowTimestamp();	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String workEffortId = (String) request.getParameter("workEffortId");
	  	  String transferGroupTypeId = (String) request.getParameter("transferGroupTypeId");
	  	  
	  	  boolean beginTransaction = false;
	  	  try{
	  		  beginTransaction = TransactionUtil.begin();
	  		  
	  		  String productionRunId = (String)ProductionNetworkServices.getRootProductionRun(delegator, workEffortId);
	  		  
	  		  GenericValue productionRun = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", productionRunId), false);
	  		  
	  		  if(UtilValidate.isEmpty(productionRun)){
	  			  Debug.logError("Primary Production run doesn't exist for Task: "+workEffortId, module);
				  request.setAttribute("_ERROR_MESSAGE_", "Primary Production run doesn't exist for Task: "+workEffortId);	  		  
		  		  return "error";
	  		  }
	  		  
	  		  boolean enableInputPrunIssueDate = Boolean.FALSE;
	  		  GenericValue tenantConfigEnableCancelAfterShipDate = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PRUN_CHECK", "propertyName","enableInputPrunIssueDate"), false);
	  		  if (UtilValidate.isNotEmpty(tenantConfigEnableCancelAfterShipDate) && (tenantConfigEnableCancelAfterShipDate.getString("propertyValue")).equals("Y")) {
				 enableInputPrunIssueDate = Boolean.TRUE;
	  		  }
	  		  
	  		  String facilityId = productionRun.getString("facilityId"); 
	  		  
	  		  String transferDateStr = UtilDateTime.toDateString(productionRun.getTimestamp("estimatedStartDate"), "dd-MM-yyyy HH:mm:ss");
	  		  /*GenericValue returnHeader = delegator.makeValue("ReturnHeader");
	  		  returnHeader.set("returnHeaderTypeId", "PRODUCTION_RETURN");
	  		  returnHeader.set("statusId", "RTN_INITIATED");
	  		  returnHeader.set("originFacilityId", facilityId);
	  		  returnHeader.set("entryDate", nowTimestamp);
	  		  returnHeader.set("createdBy", userLogin.getString("userLoginId"));
	  		  returnHeader.set("returnDate", nowTimestamp);
	  		  returnHeader.set("workEffortId", workEffortId);
	  		  delegator.createSetNextSeqId(returnHeader);
    		
	  		  String returnId = (String)returnHeader.get("returnId");
	  		  */
	  		  for (int i = 0; i < rowCount; i++){
		  		  
		  		  String productId = "";
		  		  String toFacilityId = "";
		  		  String description = "";
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  BigDecimal quantity = BigDecimal.ZERO;
		  		  String quantityStr = "";
		  		  if (paramMap.containsKey("productId" + thisSuffix)) {
		  			  productId = (String) paramMap.get("productId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
		  			quantityStr = (String) paramMap.get("quantity"+thisSuffix);
		  		  }
		  		  
		  		  if(UtilValidate.isNotEmpty(quantityStr)){
		  			  quantity = new BigDecimal(quantityStr);
		  		  }
		  		  if (paramMap.containsKey("toFacilityId" + thisSuffix)) {
		  			  toFacilityId = (String) paramMap.get("toFacilityId"+thisSuffix);
		  		  }
		  		  
		  		  if (paramMap.containsKey("description" + thisSuffix)) {
		  			  description = (String) paramMap.get("description"+thisSuffix);
		  		  }
		  		
			  		Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("productId", productId, "inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
	                serviceContext.put("facilityId", facilityId);
	                serviceContext.put("datetimeReceived", UtilDateTime.nowTimestamp());
	                if(enableInputPrunIssueDate){
	                	serviceContext.put("datetimeReceived", productionRun.getTimestamp("estimatedStartDate"));
			  		}
	                serviceContext.put("userLogin", userLogin);
	                Map<String, Object> resultService = dispatcher.runSync("createInventoryItem", serviceContext);
	                if(ServiceUtil.isError(resultService)){
	                	Debug.logError("Error creating inventory item for work effort : "+workEffortId, module);
	                	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultService));
	                	TransactionUtil.rollback();
	            		return "error";
	                }
	                
	                String inventoryItemId = (String)resultService.get("inventoryItemId");
	                
	                serviceContext.clear();
	                serviceContext.put("inventoryItemId", inventoryItemId);
	                serviceContext.put("availableToPromiseDiff", quantity);
	                serviceContext.put("quantityOnHandDiff", quantity);
	                if(enableInputPrunIssueDate){
	                	serviceContext.put("effectiveDate", productionRun.getTimestamp("estimatedStartDate"));
			  		}
	                serviceContext.put("workEffortId", workEffortId);
	                serviceContext.put("userLogin", userLogin);
	                resultService = dispatcher.runSync("createInventoryItemDetail", serviceContext);
	                if(ServiceUtil.isError(resultService)){
	                	Debug.logError("Error creating inventory item detail for work effort : "+workEffortId, module);
	                	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultService));
	                	TransactionUtil.rollback();
	            		return "error";
	                }
		  			/*GenericValue returnItem = delegator.makeValue("ReturnItem");
		    		returnItem.set("returnReasonId", "RTN_DEFECTIVE_ITEM");
		    		if(UtilValidate.isNotEmpty(returnReasonId)){
		    			returnItem.set("returnReasonId", returnReasonId);
		    		}
		    		returnItem.set("statusId", "RTN_INITIATED");
		    		returnItem.set("returnId", returnId);
		    		returnItem.set("productId", productId);
		    		returnItem.set("returnQuantity", quantity);
		    		returnItem.set("description", description);
		    		returnItem.set("returnTypeId", "RTN_REFUND");
		    		returnItem.set("returnItemTypeId", "RET_FPROD_ITEM");
	    		    delegator.setNextSubSeqId(returnItem, "returnItemSeqId", 5, 1);
		    		delegator.create(returnItem);
		    		*/
		  		  Map inputCtx = FastMap.newInstance();
		  		  inputCtx.put("xferQty", quantity);
		  		  inputCtx.put("fromFacilityId", facilityId);
		  		  inputCtx.put("toFacilityId", toFacilityId);
		  		  inputCtx.put("productId", productId);
		  		  //inputCtx.put("inventoryItemId", "");
		  		  inputCtx.put("statusId", "IXF_REQUESTED");
		  		  inputCtx.put("transferGroupTypeId", transferGroupTypeId);
		  		  inputCtx.put("workEffortId", workEffortId);
		  		  if(enableInputPrunIssueDate){
		  			  inputCtx.put("transferDate", transferDateStr);
		  		  }
		  		  inputCtx.put("comments", description);
		  		  inputCtx.put("userLogin", userLogin);
		  		  Map resultCtx = dispatcher.runSync("createStockXferRequest", inputCtx);
		  		  if(ServiceUtil.isError(resultCtx)){
		  			  Debug.logError("Error create transfer request for routing task : "+workEffortId, module);
		  			  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultCtx));
		  			  TransactionUtil.rollback();
		  			  return "error";
		  		  }
		  		  /*Map inputCtx = FastMap.newInstance();
		  		  inputCtx.put("productId", productId);
		  		  inputCtx.put("quantity", quantity);
		  		  inputCtx.put("workEffortId", workEffortId);
		  		  inputCtx.put("userLogin", userLogin);
		  		  Map resultCtx = dispatcher.runSync("productionRunTaskReturnMaterial", inputCtx);
		  		  if(ServiceUtil.isError(resultCtx)){
		  			  Debug.logError("Error issuing material for routing task : "+workEffortId, module);
		  			  TransactionUtil.rollback();
		  			  return "error";
		  		  }*/
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
	  	catch (GenericServiceException e) {
	  		try {
			  TransactionUtil.rollback(beginTransaction, "Error while calling services", e);
	  		} catch (GenericEntityException e2) {
			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
			  request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  		}
	  		Debug.logError("An entity engine error occurred while calling services", module);
	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beginTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", e.toString());
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Successfully made return of material for Task :"+workEffortId);
		return "success";  
    }
    
    public static String declareRoutingTaskMaterial(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  String workEffortId = (String) request.getParameter("workEffortId");
	  	  String productionRunId = (String) request.getParameter("productionRunId");
	  	  boolean beginTransaction = false;
	  	  try{
	  		  beginTransaction = TransactionUtil.begin();
	  		  
	  		  GenericValue workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
	  		  
	  		  if(UtilValidate.isEmpty(workEffort)){
	  			  Debug.logError("Invalid routing task for the production run "+productionRunId, module);
	  			  request.setAttribute("_ERROR_MESSAGE_", "Invalid routing task for the production run "+productionRunId);
	  			  TransactionUtil.rollback();
	  			  return "error";
	  		  }
	  		  
	  		  if(UtilValidate.isEmpty(productionRunId)){
	  			  productionRunId = (String)ProductionNetworkServices.getRootProductionRun(delegator, workEffortId);
	  		  }
	  		  GenericValue productionRun = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", productionRunId), false);
	  		  
	  		  boolean enableInputPrunIssueDate = Boolean.FALSE;
	  		  GenericValue tenantConfigEnableCancelAfterShipDate = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PRUN_CHECK", "propertyName","enableInputPrunIssueDate"), false);
	  		  if (UtilValidate.isNotEmpty(tenantConfigEnableCancelAfterShipDate) && (tenantConfigEnableCancelAfterShipDate.getString("propertyValue")).equals("Y")) {
				 enableInputPrunIssueDate = Boolean.TRUE;
	  		  }
	  		  
		  	  for (int i = 0; i < rowCount; i++){
		  		  
		  		  String productId = "";
		  		  String toFacilityId = workEffort.getString("facilityId");
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  BigDecimal quantity = BigDecimal.ZERO;
		  		  String quantityStr = "";
		  		  if (paramMap.containsKey("productId" + thisSuffix)) {
		  			  productId = (String) paramMap.get("productId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("quantity" + thisSuffix)) {
		  			quantityStr = (String) paramMap.get("quantity"+thisSuffix);
		  		  }
		  		  
		  		  if(UtilValidate.isNotEmpty(quantityStr)){
		  			  quantity = new BigDecimal(quantityStr);
		  		  }
		  		  if (paramMap.containsKey("toFacilityId" + thisSuffix)) {
		  			  toFacilityId = (String) paramMap.get("toFacilityId"+thisSuffix);
		  		  }
		  		  Map inputCtx = FastMap.newInstance();
		  		  inputCtx.put("productId", productId);
		  		  inputCtx.put("facilityId", toFacilityId);
		  		  inputCtx.put("quantity", quantity);
		  		  inputCtx.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
		  		  if(enableInputPrunIssueDate){
		  			  inputCtx.put("dateTimeProduced", productionRun.getTimestamp("estimatedStartDate"));
		  		  }
		  		  inputCtx.put("workEffortId", workEffortId);
		  		  inputCtx.put("userLogin", userLogin);
		  		  Map resultCtx = dispatcher.runSync("productionRunTaskProduce", inputCtx);
		  		  if(ServiceUtil.isError(resultCtx)){
		  			  Debug.logError("Error in declaring material of routing task : "+workEffortId, module);
		  			  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultCtx));
		  			  TransactionUtil.rollback();
		  			  return "error";
		  		  }
		  		  
		  		  List<String> inventoryItemIds = (List)resultCtx.get("inventoryItemIds");
		  		  Map batchCtx = FastMap.newInstance();
		  		  batchCtx.put("productId", productId);
		  		  batchCtx.put("workEffortId", workEffortId);
		  		  batchCtx.put("userLogin", userLogin);
		  		  resultCtx = dispatcher.runSync("createBatchForRoutingTask", batchCtx);
		  		  if(ServiceUtil.isError(resultCtx)){
		  			  Debug.logError("Error while creating batch number for production run : "+workEffortId, module);
		  			  request.setAttribute("_ERROR_MESSAGE_", "Error while creating batch number for production run : "+workEffortId);
		  			  TransactionUtil.rollback();
		  			  return "error";
		  		  }
		  		  
		  		  String productBatchId = (String)resultCtx.get("productBatchId");

		  		  for(String eachItem : inventoryItemIds){
		  			  GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", eachItem), false);
		  			 inventoryItem.put("productBatchId", productBatchId);
		  			 inventoryItem.store();
		  		  }
		  		  
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
	  	catch (GenericServiceException e) {
	  		try {
			  TransactionUtil.rollback(beginTransaction, "Error while calling services", e);
	  		} catch (GenericEntityException e2) {
			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
			  request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  		}
	  		Debug.logError("An entity engine error occurred while calling services", module);
	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beginTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", e.toString());
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Successfully made issue of material for Task :"+workEffortId);
		return "success";  
    }
    
    public static Map<String, Object> createBatchForRoutingTask(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
	  	Map<String, Object> result = ServiceUtil.returnSuccess();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	String workEffortId = (String) context.get("workEffortId");
	  	String productId = (String) context.get("productId");
	  	String productBatchId = "";
	  	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	  	try{
	  		
	  		GenericValue productionRun = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
	  		
	  		Timestamp manufacturedDate = UtilDateTime.getDayStart(nowTimestamp);
	  		if(UtilValidate.isNotEmpty(productionRun)){
	  			manufacturedDate = UtilDateTime.getDayStart(productionRun.getTimestamp("actualStartDate"));
	  		}
	  		List conditionList = FastList.newInstance();
	  		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	  		conditionList.add(EntityCondition.makeCondition("manufacturedDate", EntityOperator.EQUALS, manufacturedDate));
	  		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PBTCH_CANCELLED"));
	  		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	  		List<GenericValue> productBatch = delegator.findList("ProductBatch", condition, null, null, null, false);
	  		
	  		if(UtilValidate.isEmpty(productBatch)){
	  			GenericValue newEntity = delegator.makeValue("ProductBatch");
	        	 newEntity.set("productId", productId);
	 	         newEntity.set("manufacturedDate", manufacturedDate);
	 	         newEntity.set("statusId", "PBTCH_CREATED");
	 	         newEntity.set("createdDate", nowTimestamp);
	 	         newEntity.set("createdByUserLogin", userLogin.getString("userLoginId"));
	 	         newEntity.set("lastModifiedDate", nowTimestamp);
	 	         newEntity.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
	 	         delegator.createSetNextSeqId(newEntity);
	 	         productBatchId = newEntity.getString("productBatchId");
	  		}
	  		else{
	  			GenericValue prodBatch = EntityUtil.getFirst(productBatch);
	  			productBatchId = prodBatch.getString("productBatchId");
	  		}
	  	}catch(Exception e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
	  	}
	  	result.put("productBatchId", productBatchId);
	  	return result;
    }
    
    public static Map<String, Object> createProductVarianceForFacility(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
	  	Map<String, Object> result = ServiceUtil.returnSuccess();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	String productId = (String) context.get("productId");
	  	String batchId = (String) context.get("inventoryItemId");
	  	String facilityId = (String) context.get("facilityId");
	  	String varianceDateStr = (String) context.get("varianceDate");
	  	BigDecimal variance = (BigDecimal) context.get("variance");
	  	String varianceTypeId = (String) context.get("varianceTypeId");
	  	String comment = (String) context.get("comment");
	  	String varianceReasonId = (String) context.get("varianceReasonId");
	  	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	  	String inventoryItemId = "";
	  	Timestamp varianceDate = UtilDateTime.nowTimestamp();
	  	try{
	  		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	       	 if(UtilValidate.isNotEmpty(varianceDateStr)){
	       		 try {
	       			varianceDate = new java.sql.Timestamp(sdf.parse(varianceDateStr).getTime());
         		 } catch (ParseException e) {
         			Debug.logError(e, "Cannot parse date string: "+ varianceDateStr, module);			
         			return ServiceUtil.returnError("Cannot parse date string: "+ varianceDateStr);
         		 }
	       	 }
	  		if(UtilValidate.isEmpty(variance)){
	  			Debug.logError("variance cannot be empty ", module);
				return ServiceUtil.returnError("variance cannot be empty");
	  		}
	  		
	  		if(variance.compareTo(BigDecimal.ZERO)<1){
	  			Debug.logError("variance cannot be empty or negative value", module);
				return ServiceUtil.returnError("variance cannot be empty or negative value");
	  		}
	  		
	  		if(UtilValidate.isEmpty(batchId)){
	  			batchId = "FIFO";
	  		}
	  		if(UtilValidate.isEmpty(varianceTypeId)){
	  			varianceTypeId = "LOSS_VARIANCE";
	  		}
	  			
	  		List conditionList = FastList.newInstance();
	  		conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal",EntityOperator.GREATER_THAN, BigDecimal.ZERO));
	  		conditionList.add(EntityCondition.makeCondition("availableToPromiseTotal",EntityOperator.GREATER_THAN, BigDecimal.ZERO));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> inventoryItems = null;
	  		if(batchId.equals("FIFO")){
				inventoryItems = delegator.findList("InventoryItem", condition, null, UtilMisc.toList("datetimeReceived"), null, false);
				
	  		}else if(batchId.equals("LIFO")){
	  			inventoryItems = delegator.findList("InventoryItem", condition, null, UtilMisc.toList("-datetimeReceived"), null, false);
	  		}else{
	  			inventoryItemId = batchId;
	  			inventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId), null, null, null, false);
	  		}
	  		
	  		if(UtilValidate.isEmpty(inventoryItems)){
	  			Debug.logError("No Inventory for the product exists, to create variance", module);
				return ServiceUtil.returnError("No Inventory for the product exists, to create variance");
	  		}
	  		
	  		if(varianceTypeId.equals("GAIN_VARIANCE")){
	  			GenericValue invItem = EntityUtil.getFirst(inventoryItems);
	  			
	  			Map inputCtx = FastMap.newInstance();
	  			inputCtx.put("userLogin", userLogin);
	  			inputCtx.put("physicalInventoryDate", varianceDate);
	  			inputCtx.put("generalComments", comment);
	  			try{
	  				result = dispatcher.runSync("createPhysicalInventory", inputCtx);
					if(ServiceUtil.isError(result)){
						Debug.logError("Error while creating variance", module);
						return ServiceUtil.returnError("Error while creating variance");
					}
				}catch(GenericServiceException e){
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}
	  			
	  			String physicalInventoryId = (String)result.get("physicalInventoryId");
	  			
	  			inputCtx.clear();
	  			inputCtx.put("userLogin", userLogin);
	  			inputCtx.put("availableToPromiseVar", variance);
	  			inputCtx.put("inventoryItemId", invItem.getString("inventoryItemId"));
	  			inputCtx.put("comments", comment);
	  			inputCtx.put("physicalInventoryId", physicalInventoryId);
	  			inputCtx.put("quantityOnHandVar", variance);
	  			inputCtx.put("varianceReasonId", varianceReasonId);
	  			try{
	  				result = dispatcher.runSync("createInventoryItemVariance", inputCtx);
					if(ServiceUtil.isError(result)){
						Debug.logError("Error while creating Itemvariance", module);
						return ServiceUtil.returnError("Error while creating Itemvariance");
					}
				}catch(GenericServiceException e){
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}
	  			
	  		}else{
	  			BigDecimal varQty = variance;
	  			BigDecimal atpQty = BigDecimal.ZERO;
	  			Iterator<GenericValue> itr = inventoryItems.iterator();
	            while ((variance.compareTo(BigDecimal.ZERO) > 0) && itr.hasNext()) {
	                GenericValue invItem = itr.next();
	                atpQty = invItem.getBigDecimal("availableToPromiseTotal");
	                BigDecimal requestedQty = null;
	                if (variance.compareTo(atpQty) >= 0) {	
	                	requestedQty = atpQty;
	                } else {
	                	requestedQty = variance;
	                }
	                
	                Map inputCtx = FastMap.newInstance();
		  			inputCtx.put("userLogin", userLogin);
		  			inputCtx.put("physicalInventoryDate", varianceDate);
		  			inputCtx.put("generalComments", comment);
		  			try{
		  				result = dispatcher.runSync("createPhysicalInventory", inputCtx);
						if(ServiceUtil.isError(result)){
							Debug.logError("Error while creating variance", module);
							return ServiceUtil.returnError("Error while creating variance");
						}
					}catch(GenericServiceException e){
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}
		  			
		  			String physicalInventoryId = (String)result.get("physicalInventoryId");
		  			
		  			inputCtx.clear();
		  			inputCtx.put("userLogin", userLogin);
		  			inputCtx.put("availableToPromiseVar", requestedQty.negate());
		  			inputCtx.put("inventoryItemId", invItem.getString("inventoryItemId"));
		  			inputCtx.put("comments", comment);
		  			inputCtx.put("physicalInventoryId", physicalInventoryId);
		  			inputCtx.put("quantityOnHandVar", requestedQty.negate());
		  			inputCtx.put("varianceReasonId", varianceReasonId);
		  			try{
		  				result = dispatcher.runSync("createInventoryItemVariance", inputCtx);
						if(ServiceUtil.isError(result)){
							Debug.logError("Error while creating Itemvariance", module);
							return ServiceUtil.returnError("Error while creating Itemvariance");
						}
					}catch(GenericServiceException e){
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}
	                variance = variance.subtract(requestedQty);
	            }
	  		}
	  		
	  	}catch(Exception e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
	  	}
	  	result = ServiceUtil.returnSuccess("Successfully create variance..!");
	  	return result;
    }
    
    public static Map<String, Object> createProductBatchSequence(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
	  	Map<String, Object> result = ServiceUtil.returnSuccess();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	String productBatchId = (String) context.get("productBatchId");
	  	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	  	try{
	  		
	  		if(UtilValidate.isEmpty(productBatchId)){
	  			Debug.logError("productBatchId cannot be empty, to create sequence ", module);
				return ServiceUtil.returnError("Problem creating Product Batch");
	  		}
	  		
	  		GenericValue productBatch = delegator.findOne("ProductBatch", UtilMisc.toMap("productBatchId", productBatchId), false);
	  		Timestamp manufacturedDate = productBatch.getTimestamp("manufacturedDate");
	  		String productId = productBatch.getString("productId");
	  		
	  		Map finYearContext = FastMap.newInstance();
			finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
			finYearContext.put("organizationPartyId", "Company");
			finYearContext.put("userLogin", userLogin);
			finYearContext.put("findDate", manufacturedDate);
			finYearContext.put("excludeNoOrganizationPeriods", "Y");
			List customTimePeriodList = FastList.newInstance();
			Map resultCtx = FastMap.newInstance();
			try{
				resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
				if(ServiceUtil.isError(resultCtx)){
					Debug.logError("Problem in fetching financial year ", module);
					return ServiceUtil.returnError("Problem in fetching financial year ");
				}
			}catch(GenericServiceException e){
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
			customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
			String finYearId = "";
			if(UtilValidate.isNotEmpty(customTimePeriodList)){
				GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
				finYearId = (String)customTimePeriod.get("customTimePeriodId");
			}
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("productBatchId",EntityOperator.EQUALS, productBatchId));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
			conditionList.add(EntityCondition.makeCondition("finYearId",EntityOperator.EQUALS, finYearId));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> batchSequence = delegator.findList("ProductBatchSequence", condition, null, null, null, false);
			if(UtilValidate.isEmpty(batchSequence)){
				GenericValue batchSeq = delegator.makeValue("ProductBatchSequence");
				batchSeq.set("productBatchId", productBatchId);
				batchSeq.set("finYearId", finYearId);
				batchSeq.set("productId", productId);
				delegator.setNextSubSeqId(batchSeq, "sequenceId", 10, 1);
	            delegator.create(batchSeq);
			}
	  	}catch(Exception e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
	  	}
	  	return result;
    }
    
    public static Map<String, Object> changeRoutingTaskStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
	  	Map<String, Object> result = ServiceUtil.returnSuccess();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	  	String workEffortId = (String) context.get("workEffortId");
	  	String productionRunId = (String) context.get("productionRunId");
	  	String statusId = (String) context.get("statusId");
	  	try{
	  		  Map inputStatusCtx = FastMap.newInstance();
	  		  inputStatusCtx.put("productionRunId", productionRunId);
	  		  inputStatusCtx.put("workEffortId", workEffortId);
	  		  inputStatusCtx.put("statusId", statusId);
	  		  inputStatusCtx.put("userLogin", userLogin);
	  		  Map resultCtx = dispatcher.runSync("changeProductionRunTaskStatus", inputStatusCtx);
	  		  if(ServiceUtil.isError(resultCtx)){
	  			  Debug.logError("Error changing routing task status : "+workEffortId, module);
	  			  return ServiceUtil.returnError("Error changing routing task status : "+workEffortId);
	  		  }
	  		  String newStatusId = (String)resultCtx.get("newStatusId");
			  String oldStatusId = (String)resultCtx.get("oldStatusId");
	  	 
	  	}catch(GenericServiceException e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
	  	}
	  	result.put("productionRunId",productionRunId);
	  	return result;
     }
    
     public static Map<String, Object> confirmProductionRunStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productionRunId = (String)context.get("productionRunId");
        Map<String, Object> result = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try {
            Map inputCtx = FastMap.newInstance();
            inputCtx.put("productionRunId", productionRunId);
            inputCtx.put("statusId", "PRUN_DOC_PRINTED");
            inputCtx.put("userLogin", userLogin);
            Map resultCtx = dispatcher.runSync("changeProductionRunStatus", inputCtx);
	  		if(ServiceUtil.isError(resultCtx)){
	  			Debug.logError("Error changing production run status to confirmed: "+productionRunId, module);
	  			return ServiceUtil.returnError("Error changing production run status to confirmed: "+productionRunId);
	  		}
	  		String newStatusId = (String)resultCtx.get("newStatusId");
        }
        catch(GenericServiceException e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
        }
        result.put("productionRunId",productionRunId);
        return result;
    }// End of the Service
    
      
    public static Map<String, ? extends Object> checkAndManageBlendedProductInventory(DispatchContext dctx, Map context) {
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         String facilityId = (String)context.get("facilityId");
         String productId = (String)context.get("productId");
         BigDecimal incomingQty = (BigDecimal)context.get("quantityOnHandTotal");
         Map<String, ? extends Object> result = ServiceUtil.returnSuccess();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         try {
        	 
        	 GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
        	 
        	 String facilityTypeId = "";
        	 if(UtilValidate.isNotEmpty(facility) && UtilValidate.isNotEmpty(facility.get("facilityTypeId"))){
        		 facilityTypeId = facility.getString("facilityTypeId");
        	 }
        	 if(UtilValidate.isNotEmpty(facilityTypeId) && facilityTypeId.equals("SILO")){
        		 
        		 boolean allowFacilityBlend = Boolean.FALSE;
        		 if(UtilValidate.isNotEmpty(facility.get("allowProductBlend")) && (facility.getString("allowProductBlend").equals("Y"))){
            		 allowFacilityBlend = Boolean.TRUE;
            	 }
        		 List conditionList = FastList.newInstance();
            	 
            	 conditionList.clear();
            	 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
            	 conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            	 EntityCondition invCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            	 List<GenericValue> inventoryProducts = delegator.findList("InventoryItem", invCond, UtilMisc.toSet("productId"), null, null, false);
            	 String blendedProductId = "";
            	 BigDecimal blendQty = null;
            	 
            	 if(allowFacilityBlend){
            		 conditionList.clear();
                	 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
                	 conditionList.add(EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "SILO_PROD_BLEND"));
                	 conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
                	 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
                	 EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
                	 List<GenericValue> prodAssoc = delegator.findList("ProductAssoc", condition, null, null, null, false);
                	 
                	 if(UtilValidate.isNotEmpty(prodAssoc)){
                		 GenericValue blendedProduct = EntityUtil.getFirst(prodAssoc);
                		 blendedProductId = blendedProduct.getString("productIdTo");
                		 if(UtilValidate.isNotEmpty(blendedProduct.get("quantity"))){
                			 blendQty = blendedProduct.getBigDecimal("quantity");
                		 }
                	 }
            	 }
        		 BigDecimal facilitySize = BigDecimal.ZERO;
            	 if(UtilValidate.isNotEmpty(facility.get("facilitySize"))){
            		 facilitySize = facility.getBigDecimal("facilitySize");
            	 }
            	 
            	 String invProductId = productId;
        		 if(UtilValidate.isNotEmpty(blendedProductId)){
        			 invProductId = blendedProductId;
        		 }
        		 
        		 // facility capacity check
            	 if(facilitySize.compareTo(BigDecimal.ZERO) >0){
            		 Map<String, ? extends Object> findCurrInventoryParams =  UtilMisc.toMap("productId", invProductId, "facilityId", facilityId);
            		 Map<String, Object> resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
                     if (ServiceUtil.isError(resultCtx)) {
                    	 Debug.logError("Problem getting inventory level of the request for product Id :"+invProductId, module);
                         return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+invProductId);
                     }
                     Object qohObj = resultCtx.get("quantityOnHandTotal");
                     BigDecimal qoh = BigDecimal.ZERO;
                     if (qohObj != null) {
                     	qoh = new BigDecimal(qohObj.toString());
                     }
                     if (UtilValidate.isEmpty(incomingQty)) {
                    	 incomingQty = BigDecimal.ZERO;
                     }
                     BigDecimal totalQtyInc = facilitySize.subtract(qoh.add(incomingQty));
                     if(totalQtyInc.compareTo(BigDecimal.ZERO) < 0){
                    	 Debug.logError("Facility capacity exceeded..!", module);
                         return ServiceUtil.returnError("Facility capacity exceeded..!"+facilityId);
                     }
            	 }
            	 
            	 // productFacility check
            	 List<GenericValue> productFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);
            	 List<String> productIds = EntityUtil.getFieldListFromEntityList(productFacility, "productId", true);
            	 if(!productIds.contains(invProductId)){
            		 Debug.logError("This product with Id ["+invProductId+"] is not mapped to facility :"+facilityId, module);
                     return ServiceUtil.returnError("This product with Id ["+invProductId+"] is not mapped to facility :"+facilityId);
            	 }

            	 
            	 List<String> extProdIds = EntityUtil.getFieldListFromEntityList(inventoryProducts, "productId", true);
            	 String extProd = "";
            	 for(String prodId : extProdIds){
            		 if(!prodId.equals(invProductId)){
            			 extProd = prodId;
                	 }
            	 }
            	 if(!extProdIds.contains(invProductId)){
            		 extProdIds.add(invProductId);
            	 }
            	// Raw Milk Silo Check
            	 if(allowFacilityBlend){
            		 
                	 if(UtilValidate.isEmpty(inventoryProducts)){
                		 if(UtilValidate.isNotEmpty(blendedProductId)){
            				 if(UtilValidate.isNotEmpty(blendQty)){
                    			 Object qty = blendQty;
                    			 context.put("quantityOnHandTotal", qty);
                    			 context.put("availableToPromiseTotal", qty);
                    		 }
                    		 Object blendProdId = blendedProductId;
                    		 context.put("productId", blendProdId);
            			 }
            		 }else{
            			 String prodToCompare = (String)extProdIds.get(0);
            			 if(!prodToCompare.equals(invProductId)){
            				 Debug.logError("Already product with Id :["+prodToCompare+"] exists. Empty it, before storing new product :"+invProductId, module);
                             return ServiceUtil.returnError("Already product with Id :["+prodToCompare+"] exists. Empty it, before storing new product :"+invProductId);
            			 }
            			 if(UtilValidate.isNotEmpty(blendQty)){
                			 Object qty = blendQty;
                			 context.put("quantityOnHandTotal", qty);
                			 context.put("availableToPromiseTotal", qty);
                		 }
            			 context.put("productId", invProductId);
            		 }
            	 }
            	 else{
            		 if(extProdIds.size() > 1){
            			 Debug.logError("Already product with Id :["+extProd+"] exists. Empty it, before storing new product :"+invProductId, module);
                         return ServiceUtil.returnError("Already product with Id :["+extProd+"] exists. Empty it, before storing new product :"+invProductId);
            		 }
            	 }
        	 }
         }
         catch(Exception e){
         	Debug.logError(e, module);
         	return ServiceUtil.returnError(e.toString());
         }
         return context;
     }
    
    public static Map<String, ? extends Object> validateInventoryForFacility(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, ? extends Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String inventoryItemId = (String) context.get("inventoryItemId");
        BigDecimal qohDiff = (BigDecimal) context.get("quantityOnHandDiff");
        BigDecimal atpDiff = (BigDecimal) context.get("availableToPromiseDiff");
        try {
       	 	
       	 	GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
       	 
	       	String facilityId = inventoryItem.getString("facilityId");
	       	GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
	       	String facilityTypeId = facility.getString("facilityTypeId");
	       	BigDecimal facilitySize = facility.getBigDecimal("facilitySize");
	       	if(UtilValidate.isNotEmpty(qohDiff)){
	       		BigDecimal qoh = BigDecimal.ZERO;
	       		if(UtilValidate.isNotEmpty(inventoryItem.get("quantityOnHandTotal"))){
	       			qoh = inventoryItem.getBigDecimal("quantityOnHandTotal");
	       		}
	       		BigDecimal totalQOH = qoh.add(qohDiff);
	       		if(totalQOH.compareTo(BigDecimal.ZERO)<0){
	       			Debug.logError("Inventory(QOH) cannot be less than ZERO for product Id :"+inventoryItem.getString("productId"), module);
                    return ServiceUtil.returnError("Inventory cannot be less than ZERO for product Id :"+inventoryItem.getString("productId"));
	       		}
	       	}
	       	
	       	if(UtilValidate.isNotEmpty(atpDiff)){
	       		BigDecimal atp = BigDecimal.ZERO;
	       		if(UtilValidate.isNotEmpty(inventoryItem.get("availableToPromiseTotal"))){
	       			atp = inventoryItem.getBigDecimal("availableToPromiseTotal");
	       		}
	       		BigDecimal totalATP = atp.add(atpDiff);
	       		if(totalATP.compareTo(BigDecimal.ZERO)<0){
	       			Debug.logError("Inventory(ATP) cannot be less than ZERO for product Id :"+inventoryItem.getString("productId"), module);
                    return ServiceUtil.returnError("Inventory cannot be less than ZERO for product Id :"+inventoryItem.getString("productId"));
	       		}
	       	}
	       	
	       	if(UtilValidate.isNotEmpty(facilityTypeId) && UtilValidate.isNotEmpty(facilityTypeId.equals("SILO")) && UtilValidate.isNotEmpty(facilitySize)){
	       		Map<String, ? extends Object> findCurrInventoryParams =  UtilMisc.toMap("productId", inventoryItem.getString("productId"), "facilityId", facilityId);
          		Map<String, Object> resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
          		if (ServiceUtil.isError(resultCtx)) {
              	 	Debug.logError("Problem getting inventory level of the request for product Id :"+inventoryItem.getString("productId"), module);
              	 	return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+inventoryItem.getString("productId"));
          		}
          		
          		Object qohObj = resultCtx.get("quantityOnHandTotal");
          		Object atpObj = resultCtx.get("availableToPromiseTotal");
                BigDecimal qohAvl = BigDecimal.ZERO;
                BigDecimal atpAvl = BigDecimal.ZERO;
                if (qohObj != null) {
                	qohAvl = new BigDecimal(qohObj.toString());
                }
                if (atpAvl != null) {
                	atpAvl = new BigDecimal(atpAvl.toString());
                }
                BigDecimal totalQOHQtyInc = facilitySize.subtract(qohAvl.add(qohDiff));
                if(totalQOHQtyInc.compareTo(BigDecimal.ZERO) < 0){
               	 	Debug.logError("Facility capacity exceeded..!", module);
                    return ServiceUtil.returnError("Facility capacity exceeded..!"+facilityId);
                }
                
                BigDecimal totalATPQtyInc = facilitySize.subtract(atpAvl.add(atpDiff));
                if(totalATPQtyInc.compareTo(BigDecimal.ZERO) < 0){
               	 	Debug.logError("Facility capacity exceeded..!", module);
                    return ServiceUtil.returnError("Facility capacity exceeded..!"+facilityId);
                }
	       	} 
        }
        catch(Exception e){
        	Debug.logError(e, module);
        	return ServiceUtil.returnError(e.toString());
        }
        return context;
     }
    
     public static Map<String, Object> createStockXferRequest(DispatchContext dctx, Map<String, ? extends Object> context) {
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         String transferDate = (String)context.get("transferDate");
         String fromFacilityId = (String)context.get("fromFacilityId");
         String toFacilityId = (String)context.get("toFacilityId");
         BigDecimal xferQty = (BigDecimal)context.get("xferQty");
         String productId = (String)context.get("productId");
         String statusId = (String)context.get("statusId");
         String inventoryItemId = (String)context.get("inventoryItemId");
         String transferGroupTypeId = (String)context.get("transferGroupTypeId");
         String comments = (String)context.get("comments");
         String workEffortId = (String)context.get("workEffortId");
         Map<String, Object> result = ServiceUtil.returnSuccess();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         if(UtilValidate.isEmpty(transferGroupTypeId)){
        	 transferGroupTypeId = "_NA_";
         }
         Timestamp xferDate = null;
         try {
        	 
        	 SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        	 if(UtilValidate.isNotEmpty(transferDate)){
        		 try {
        			 xferDate = new java.sql.Timestamp(sdf.parse(transferDate).getTime());
          		 } catch (ParseException e) {
          			Debug.logError(e, "Cannot parse date string: "+ transferDate, module);			
          			return ServiceUtil.returnError("Cannot parse date string: "+ transferDate);
          		 }
        	 }else{
        		 xferDate = UtilDateTime.nowTimestamp();
        	 }
        	 if(xferQty.compareTo(BigDecimal.ZERO)<1){
        		 Debug.logError("Transfer Qty cannot be less than 1", module);
   	  			return ServiceUtil.returnError("Transfer Qty cannot be less than 1");
        	 }
        	 List conditionList = FastList.newInstance();
        	 
        	 Map<String, ? extends Object> serviceCtx =  UtilMisc.toMap("productId", productId, "facilityIdTo", toFacilityId, "quantity", xferQty, "userLogin", userLogin);
             Map<String, Object> resultCtx = dispatcher.runSync("validateProductionTransfers", serviceCtx);
        	 if (ServiceUtil.isError(resultCtx)) {
        		 Debug.logError("Error ::"+ServiceUtil.getErrorMessage(resultCtx), module);
                 return ServiceUtil.returnError("Error ::"+ServiceUtil.getErrorMessage(resultCtx));
             }
        	 /*if(UtilValidate.isEmpty(toFacilityId)){
        		 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, toFacilityId));
        		 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        		 EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
             	 List<GenericValue> productFacility = delegator.findList("ProductFacility", cond, null, null, null, false);
             	 if(UtilValidate.isEmpty(productFacility)){
             		Debug.logError("ProductId[ "+productId+" ] is not associated to store :"+toFacilityId, module);	
             		return ServiceUtil.returnError("ProductId[ "+productId+" ] is not associated to store :"+toFacilityId);
             	 }
             }*/
             
             Map<String, ? extends Object> findCurrInventoryParams =  UtilMisc.toMap("productId", productId, "facilityId", fromFacilityId);
             
             resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
             if (ServiceUtil.isError(resultCtx)) {
             	Debug.logError("Problem getting inventory level of the request for product Id :"+productId, module);
                 return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+productId);
             }
             Object qohObj = resultCtx.get("availableToPromiseTotal");
             BigDecimal qoh = BigDecimal.ZERO;
             if (qohObj != null) {
             	qoh = new BigDecimal(qohObj.toString());
             }
             
             if (xferQty.compareTo(qoh) > 0) {
             	Debug.logError("Available Inventory level for productId : "+productId + " is "+qoh, module);
                 return ServiceUtil.returnError("Available Inventory level for productId : "+productId + " is "+qoh);
             }
        	 
             conditionList.clear();
        	 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, fromFacilityId));
        	 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
        	 if(UtilValidate.isNotEmpty(inventoryItemId)){	
        		 conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId));
        	 }
        	 conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
        	 conditionList.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
        	 EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        	 List<GenericValue> inventoryItems = delegator.findList("InventoryItem", condition, null, UtilMisc.toList("datetimeReceived"), null, false);
        	 BigDecimal requestedQty = xferQty;
        	 
        	 GenericValue newEntity = delegator.makeValue("InventoryTransferGroup");
        	 newEntity.set("transferGroupTypeId", transferGroupTypeId);
        	 newEntity.set("workEffortId", workEffortId);
        	 newEntity.set("comments", comments);
 	         newEntity.set("statusId", "IXF_REQUESTED");
 	         delegator.createSetNextSeqId(newEntity);
 	         
 	         String transferGroupId = (String) newEntity.get("transferGroupId");
        	 List<GenericValue> inventoryItemDetail = null;
        	 Iterator<GenericValue> itr = inventoryItems.iterator();
        	 
        	 while ((requestedQty.compareTo(BigDecimal.ZERO) > 0) && itr.hasNext()) {
                 GenericValue inventoryItem = itr.next();
                 String invItemId = inventoryItem.getString("inventoryItemId");
                 BigDecimal itemQOH = inventoryItem.getBigDecimal("availableToPromiseTotal");
                 BigDecimal xferQuantity = null;
                 if (requestedQty.compareTo(itemQOH) >= 0) {	
                	 xferQuantity = itemQOH;
                 } else {
                	 xferQuantity = requestedQty;
                 }
                 
                 Map inputCtx = FastMap.newInstance();
                 inputCtx.put("statusId", statusId);
                 inputCtx.put("comments", comments);
                 inputCtx.put("facilityId", fromFacilityId);
                 inputCtx.put("facilityIdTo", toFacilityId);
                 inputCtx.put("inventoryItemId", invItemId);
                 inputCtx.put("sendDate", xferDate);
                 inputCtx.put("xferQty", xferQuantity);
                 inputCtx.put("userLogin", userLogin);
                 Map resultMap = dispatcher.runSync("createInventoryTransfer", inputCtx);
     	  		 if(ServiceUtil.isError(resultMap)){
     	  			Debug.logError("Error in processing transfer entry ", module);
     	  			return ServiceUtil.returnError("Error in processing transfer entry ");
     	  		 }
     	  		 
     	  		 requestedQty = requestedQty.subtract(xferQuantity);
     	  		 
     	  		String inventoryTransferId = (String)resultMap.get("inventoryTransferId");
     	  		
     	  		Timestamp dayStart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
     	  		Timestamp dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
     	  		
     	  		conditionList.clear();
     	  		conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, invItemId));
     	  		conditionList.add(EntityCondition.makeCondition("availableToPromiseDiff", EntityOperator.EQUALS, xferQuantity.negate()));
     	  		conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
     	  		conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
     	  		EntityCondition invDetailCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
     	  		inventoryItemDetail = delegator.findList("InventoryItemDetail", invDetailCond, null, UtilMisc.toList("-effectiveDate"), null, false);
     	  		if(UtilValidate.isNotEmpty(inventoryItemDetail)){
     	  			GenericValue invItemDet = EntityUtil.getFirst(inventoryItemDetail);
     	  			invItemDet.set("inventoryTransferId", inventoryTransferId);
 	  				if(UtilValidate.isNotEmpty(workEffortId)){
 	  					invItemDet.set("workEffortId", workEffortId);
 	  				}
 	  				invItemDet.store();
     	  		}
     	  		
     	  		GenericValue newEntityMember = delegator.makeValue("InventoryTransferGroupMember");
     	  		newEntityMember.set("transferGroupId", transferGroupId);
     	  		newEntityMember.set("inventoryTransferId", inventoryTransferId);
     	  		newEntityMember.set("inventoryItemId", invItemId);
     	  		newEntityMember.set("productId", productId);
    	        newEntityMember.set("xferQty", xferQuantity);
    	        newEntityMember.set("createdDate", UtilDateTime.nowTimestamp());
    	        newEntityMember.set("createdByUserLogin", userLogin.get("userLoginId"));
    	        newEntityMember.set("lastModifiedDate", UtilDateTime.nowTimestamp());
    	        newEntityMember.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
    	        newEntityMember.create();
             }
         }
         catch(Exception e){
         	Debug.logError(e, module);
         	return ServiceUtil.returnError(e.toString());
         }
         
         result = ServiceUtil.returnSuccess("Sucessfully initated transfer");
         return result;
     }
     public static String updateTransferGroupStatus(HttpServletRequest request, HttpServletResponse response) {
	  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
	  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  	  Locale locale = UtilHttp.getLocale(request);
	  	  Map<String, Object> result = ServiceUtil.returnSuccess();
	  	  HttpSession session = request.getSession();
	  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	      Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();	 
	  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	  	  int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	  	  if (rowCount < 1) {
	  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
			  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
	  		  return "error";
	  	  }
	  	  boolean beginTransaction = false;
	  	  try{
	  		  beginTransaction = TransactionUtil.begin();
		  	  for (int i = 0; i < rowCount; i++){
		  		  
		  		  String transferGroupId = "";
		  		  String statusId = "";
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if (paramMap.containsKey("transferGroupId" + thisSuffix)) {
		  			  transferGroupId = (String) paramMap.get("transferGroupId" + thisSuffix);
		  		  }else{
		  			Debug.logError("Transfer Group Id cannot be empty", module);
		  			request.setAttribute("_ERROR_MESSAGE_", "Transfer Id cannot be empty");
                	TransactionUtil.rollback();
            		return "error";
		  		  }
		  		  
		  		  if (paramMap.containsKey("statusId" + thisSuffix)) {
		  			  statusId = (String) paramMap.get("statusId"+thisSuffix);
		  		  }
		  		  else{
		  			Debug.logError("status cannot be empty", module);
		  			request.setAttribute("_ERROR_MESSAGE_", "status cannot be empty");
                	TransactionUtil.rollback();
            		return "error";
		  		  }
		  		  
		  		  GenericValue transferGroup = delegator.findOne("InventoryTransferGroup", UtilMisc.toMap("transferGroupId", transferGroupId), false);
		  		  
		  		  String oldStatusId = transferGroup.getString("statusId");
		  		  String workEffortId = transferGroup.getString("workEffortId");
		  		  String transferTypeId = transferGroup.getString("transferGroupTypeId");
		  		  GenericValue statusItem = delegator.findOne("StatusValidChange", UtilMisc.toMap("statusId", oldStatusId, "statusIdTo", statusId), false);
		  		  
		  		  if(UtilValidate.isEmpty(statusItem)){
		  			  Debug.logError("Not a valid status change", module);
		  			  request.setAttribute("_ERROR_MESSAGE_", "Not a valid status change");
		  			  TransactionUtil.rollback();
		  			  return "error";
		  		  }
		  		  
		  		  transferGroup.set("statusId", statusId);
		  		  transferGroup.store();
		  		  
		  		  List<GenericValue> transferGroupMembers = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null, null, null, false);
		  		  
		  		  List<String> inventoryTransferIds = EntityUtil.getFieldListFromEntityList(transferGroupMembers, "inventoryTransferId", true);
		  		  
		  		  List<GenericValue> inventoryTransfers = delegator.findList("InventoryTransfer", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN, inventoryTransferIds), null, null, null, false);
		  		  
		  		  for(GenericValue eachTransfer : inventoryTransfers){
		  			  Map inputCtx = FastMap.newInstance();
		              inputCtx.put("inventoryTransferId", eachTransfer.getString("inventoryTransferId"));
		              inputCtx.put("inventoryItemId", eachTransfer.getString("inventoryItemId"));
		              inputCtx.put("statusId", statusId);
		              inputCtx.put("userLogin", userLogin);
		              Map resultCtx = dispatcher.runSync("updateInventoryTransfer", inputCtx);
		              if(ServiceUtil.isError(resultCtx)){
		            	  Debug.logError("Error updating inventory transfer status : "+eachTransfer.getString("inventoryTransferId"), module);
		            	  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultCtx));
		            	  TransactionUtil.rollback();
		            	  return "error";
		              }
		              
		              if(UtilValidate.isNotEmpty(statusId) && statusId.equals("IXF_COMPLETE") && transferTypeId.equals("RETURN_XFER") && UtilValidate.isNotEmpty(transferGroup.get("workEffortId"))){
		            		
		            	  String facIdTo = eachTransfer.getString("facilityIdTo");
		            	  GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", eachTransfer.getString("inventoryItemId")), false);
		            	  inventoryItem.set("facilityId", facIdTo);
		            	  inventoryItem.store();
		              }
		              
		              if(UtilValidate.isNotEmpty(statusId) && statusId.equals("IXF_CANCELLED") && transferTypeId.equals("RETURN_XFER") && UtilValidate.isNotEmpty(transferGroup.get("workEffortId"))){
		            	  List<GenericValue> tranferGroupMembers = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("transferGroupId", EntityOperator.EQUALS, transferGroupId), null, null, null, false);
		            	  for(GenericValue eachXferGroup : tranferGroupMembers){
		            		  inputCtx.clear();
			            	  inputCtx.put("inventoryItemId", eachXferGroup.getString("inventoryItemId"));
			            	  inputCtx.put("availableToPromiseDiff", (eachXferGroup.getBigDecimal("xferQty")).negate());
			            	  inputCtx.put("quantityOnHandDiff", (eachXferGroup.getBigDecimal("xferQty")).negate());
			            	  inputCtx.put("userLogin", userLogin);
				              Map resultService = dispatcher.runSync("createInventoryItemDetail", inputCtx);
				              if(ServiceUtil.isError(resultService)){
				            	  Debug.logError("Error while rejecting return for production run : "+workEffortId, module);
				            	  request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultService));
				            	  TransactionUtil.rollback();
				            	  return "error";
				              }
		            	  }
		              }
		  		  }
		  	  }
	  	  }
	  	catch (GenericEntityException e) {
			try {
				TransactionUtil.rollback(beginTransaction, "Error Fetching data", e);
	  		} catch (GenericEntityException e2) {
	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
	  			request.setAttribute("_ERROR_MESSAGE_", e2.toString());
	  			return "error";
	  		}
	  		Debug.logError("An entity engine error occurred while fetching data", module);
	  	}
 	  	catch (GenericServiceException e) {
 	  		try {
 			  TransactionUtil.rollback(beginTransaction, "Error while calling services", e);
 	  		} catch (GenericEntityException e2) {
 			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
 			  request.setAttribute("_ERROR_MESSAGE_", e2.toString());
 			  return "error";
 	  		}
 	  		Debug.logError("An entity engine error occurred while calling services", module);
 	  	}
	  	finally {
	  		try {
	  			TransactionUtil.commit(beginTransaction);
	  		} catch (GenericEntityException e) {
	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
	  			request.setAttribute("_ERROR_MESSAGE_", e.toString());
	  			return "error";
	  		}
	  	}
	  	request.setAttribute("_EVENT_MESSAGE_", "Entry Successfully");
		return "success";  
     }
     

     public static String processProductionIndentItems(HttpServletRequest request, HttpServletResponse response) {
     		
     		Delegator delegator = (Delegator) request.getAttribute("delegator");
     		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
     		DispatchContext dctx =  dispatcher.getDispatchContext();
     		Locale locale = UtilHttp.getLocale(request);
     		Map<String, Object> result = ServiceUtil.returnSuccess();
     	    String requestDateStr = (String) request.getParameter("requestDate");
     	    String responseDateStr = (String) request.getParameter("responseDate");
     	    String requestName = (String) request.getParameter("custRequestName");
     	    String custRequestId="";
     	    HttpSession session = request.getSession();
     	    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
     	    String partyId = (String) request.getParameter("partyIdFrom");
     	  	String custRequestTypeId = (String) request.getParameter("custRequestTypeId");
     	  	String partyIdFrom = (String) request.getParameter("partyId"); 
     		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
     		if (UtilValidate.isEmpty(partyId)) {
     			Debug.logError("Cannot create request without partyId: "+ partyId, module);
     			return "error";
     		}
     		if(UtilValidate.isEmpty(requestName)){
     			requestName = "_NA_";
     		}
     		Timestamp requestDate = null;
     		Timestamp responseDate = null;
     		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
     		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
     		if (rowCount < 1) {
     			Debug.logError("No rows to process, as rowCount = " + rowCount, module);
     			return "error";
     		}
     		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
     	  	if(UtilValidate.isNotEmpty(requestDateStr)){
     	  		try {
     	  			requestDate = new java.sql.Timestamp(sdf.parse(requestDateStr).getTime());
     		  	} catch (ParseException e) {
     		  		Debug.logError(e, "Cannot parse date string: " + requestDateStr, module);
     		  	} catch (NullPointerException e) {
     	  			Debug.logError(e, "Cannot parse date string: " + requestDateStr, module);
     		  	}
     	  	}
     	  	else{
     	  		requestDate = UtilDateTime.nowTimestamp();
     	  	}
     	  	if(UtilValidate.isNotEmpty(responseDateStr)){
     	  		try {
     	  			responseDate = new java.sql.Timestamp(sdf.parse(responseDateStr).getTime());
     		  	} catch (ParseException e) {
     		  		Debug.logError(e, "Cannot parse date string: " + responseDateStr, module);
     		  	} catch (NullPointerException e) {
     	  			Debug.logError(e, "Cannot parse date string: " + responseDateStr, module);
     		  	}
     	  	}else{
     	  		responseDate = UtilDateTime.nowTimestamp();
     	  	}
     		boolean beganTransaction = false;
     		try{
     			beganTransaction = TransactionUtil.begin(7200);
     			String roleTypeId = null;
     			if(partyId.contains("SUB")){
     				roleTypeId = "DIVISION";
     			}else{
     				roleTypeId = "INTERNAL_ORGANIZATIO";
     			}
     			GenericValue party = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
     			if(UtilValidate.isEmpty(party)){
     				Debug.logError("Request can only made by departments", module);
     				request.setAttribute("_ERROR_MESSAGE_", "Request can only made by departments");
     				TransactionUtil.rollback();
     		  		return "error";
     			}
     			
     			Map<String,Object> custRequestInMap = FastMap.newInstance();
     			custRequestInMap.put("custRequestTypeId",custRequestTypeId);
     			custRequestInMap.put("userLogin",userLogin);
     			custRequestInMap.put("currencyUomId","INR");
     			custRequestInMap.put("maximumAmountUomId","INR");
     			custRequestInMap.put("fromPartyId",partyId);
     			custRequestInMap.put("custRequestName",requestName);
     			custRequestInMap.put("custRequestDate",requestDate);
     			custRequestInMap.put("responseRequiredDate",responseDate);
     	        Map resultMap = dispatcher.runSync("createCustRequest",custRequestInMap);
     	        
     	        if (ServiceUtil.isError(resultMap)) {
     	        	Debug.logError("Problem Filing Request for party :"+partyId, module);
     				request.setAttribute("_ERROR_MESSAGE_", "Problem Filing Request for party :"+partyId);	
     				TransactionUtil.rollback();
     		  		return "error";
     	        }
     	         custRequestId = (String)resultMap.get("custRequestId");
     	        
     	        String productId = "";
     	        String quantityStr = "";
     			BigDecimal quantity = BigDecimal.ZERO;
     			for (int i = 0; i < rowCount; i++) {
     				  
     				String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
     				if (paramMap.containsKey("productId" + thisSuffix)) {
     					productId = (String) paramMap.get("productId" + thisSuffix);
     				}
     				else {
     					request.setAttribute("_ERROR_MESSAGE_", "Missing product id");
     					return "error";			  
     				}
     			  
     				if (paramMap.containsKey("quantity" + thisSuffix)) {
     					quantityStr = (String) paramMap.get("quantity" + thisSuffix);
     				}
     				else {
     					request.setAttribute("_ERROR_MESSAGE_", "Missing product quantity");
     					return "error";			  
     				}		  
     				if(UtilValidate.isNotEmpty(quantityStr)){
     					quantity = new BigDecimal(quantityStr);
     				}
     			    List conditionList = FastList.newInstance();
     			    conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
     			    conditionList.add(EntityCondition.makeCondition("fromPartyId",EntityOperator.EQUALS,partyId));
     			    conditionList.add(EntityCondition.makeCondition("itemStatusId",EntityOperator.IN,UtilMisc.toList("CRQ_DRAFT","CRQ_SUBMITTED","CRQ_ACCEPTED","CRQ_ISSUED")));
     			    EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
     				List<GenericValue> custRequestItemList = delegator.findList("CustRequestAndItemAndAttribute",condition, null, null, null, false);
     				
     				if(UtilValidate.isNotEmpty(custRequestItemList)){
     					Debug.logError(productId +" is aleady in process.", module);
     	 				request.setAttribute("_ERROR_MESSAGE_", productId +" is aleady in process.");
     	 				TransactionUtil.rollback();
     	 		  		return "error";
     				}
     				
     				Map<String,Object> itemInMap = FastMap.newInstance();
     		        itemInMap.put("custRequestId",custRequestId);
     		        itemInMap.put("statusId","CRQ_DRAFT");
     		        itemInMap.put("userLogin",userLogin);
     		        itemInMap.put("productId",productId);
     		        itemInMap.put("description","");
     		        itemInMap.put("quantity",quantity);
     		        itemInMap.put("origQuantity",quantity);
     		        resultMap = dispatcher.runSync("createCustRequestItem",itemInMap);
     		        
     		        if (ServiceUtil.isError(resultMap)) {
     		        	Debug.logError("Problem creating Request Item for party :"+partyId, module);
     					request.setAttribute("_ERROR_MESSAGE_", "Problem creating Request Item for party :"+partyId);	
     					TransactionUtil.rollback();
     			  		return "error";
     		        }
     			}
     			
     			roleTypeId = null;
     			if(partyIdFrom.contains("SUB")){
     				roleTypeId = "DIVISION";
     			}else{
     				roleTypeId = "INTERNAL_ORGANIZATIO";
     			}
     			GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", roleTypeId), false);
     			if(UtilValidate.isEmpty(partyRole)){
     				Debug.logError("Request can only made to departments", module);
     				request.setAttribute("_ERROR_MESSAGE_", "Request can only made to departments");
     				TransactionUtil.rollback();
     		  		return "error";
     			}
     			
     			Map<String,Object> inputCtxMap = FastMap.newInstance();  
     			inputCtxMap.put("custRequestId", custRequestId);
     			inputCtxMap.put("partyId", partyIdFrom);
     			inputCtxMap.put("roleTypeId",roleTypeId);
     			inputCtxMap.put("userLogin", userLogin);
     			
     			resultMap = dispatcher.runSync("createCustRequestParty", inputCtxMap);
    			if (ServiceUtil.isError(resultMap)) {
    				Debug.logError("RequestItem set status failed for Request: " + custRequestId+" : "+partyIdFrom, module);
    				request.setAttribute("_ERROR_MESSAGE_", "Error occuring while calling createCustRequestParty service:");
    				TransactionUtil.rollback();
     		  		return "error";
    			}
     			
     		}
     		catch (GenericEntityException e) {
     			try {
     				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
     	  		} catch (GenericEntityException e2) {
     	  			Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
     	  		}
     	  		Debug.logError("An entity engine error occurred while fetching data", module);
     	  	}
       	  	catch (GenericServiceException e) {
       	  		try {
       			  TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
       	  		} catch (GenericEntityException e2) {
       			  Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
       	  		}
       	  		Debug.logError("An entity engine error occurred while calling services", module);
       	  	}
     	  	finally {
     	  		try {
     	  			TransactionUtil.commit(beganTransaction);
     	  		} catch (GenericEntityException e) {
     	  			Debug.logError(e, "Could not commit transaction for entity engine error occurred while fetching data", module);
     	  		}
     	  	}
     		request.setAttribute("_EVENT_MESSAGE_", "Successfully made request entries ...!IndentNo:"+custRequestId );
     		return "success";
     	}
     
     /**
      * 
      * @param dctx
      * @param context
      * @return
      */
     public static Map<String,Object> createProductQcDetails(DispatchContext dctx,Map<String,? extends Object> context){
    	 LocalDispatcher dispatcher = dctx.getDispatcher();
    	 Delegator delegator = dctx.getDelegator();
    	 Map resultMap = ServiceUtil.returnSuccess();
    	 String productId = (String) context.get("productId");
    	 String statusId  = (String) context.get("statusId");
    	 //Timestamp testDateStr = (Timestamp) context.get("testDate");
    	 GenericValue userLogin = (GenericValue)context.get("userLogin");
    	 Timestamp testDateTime = (Timestamp) context.get("testDate");
    	 List<String> productQcTestFieldsList = FastList.newInstance();  
    	 Map<String,String> testFieldTypes = FastMap.newInstance();
    	 try{
    		 ModelReader reader = delegator.getModelReader();
    		 ModelEntity entity = reader.getModelEntity("ProductQcTest");
    		 Iterator fieldIterator = entity.getFieldsIterator();
    		 while( fieldIterator != null && ( fieldIterator.hasNext())) {
    			 ModelField fieldDet = (ModelField)fieldIterator.next();
    			 
    			 String fieldName = (String)fieldDet.getName();
    			 String fieldType = (String)fieldDet.getType();
    			 ModelFieldType type = delegator.getEntityFieldType(entity, fieldType);
    			 testFieldTypes.put(fieldName, (type.getJavaType()).toString());
    			 productQcTestFieldsList.add(fieldName);
    		 }//end of the loop
    	 }catch(Exception e){
    		Debug.logError("Error while getting the fields of ProductQcTest :"+e,module);
    		resultMap = ServiceUtil.returnError("Error while getting the fields of ProductQcTest :");
    	 }
    	 Map createQcTestInMap = FastMap.newInstance();
    	 Map createQcTestItemsInMap = FastMap.newInstance();
    	 for(String key : context.keySet()){
    		 if(key.contains("_testComponent")){
    			 createQcTestItemsInMap.put(key, (String)context.get(key));
    		 }else{
    			 if(productQcTestFieldsList.contains(key)){
	    			createQcTestInMap.put(key, context.get(key));
    			 }
    		 }
    	 }
    	 Debug.logInfo("context=======##########=========="+context, module);
    	 Debug.logInfo("createQcTestItemsInMap=======##########=========="+createQcTestItemsInMap,module);
    	 Debug.logInfo("createQcTestInMap=======##########=========="+createQcTestInMap,module);
    	 if(UtilValidate.isNotEmpty(createQcTestInMap)){
    		 createQcTestInMap.put("userLogin",userLogin );
    		 createQcTestInMap.put("productQcTestFieldsList",productQcTestFieldsList );
    		 createQcTestInMap.put("testFieldTypes", testFieldTypes);
    		 createQcTestInMap.put("testDate", testDateTime);
    	 }
    	 if(UtilValidate.isNotEmpty(createQcTestItemsInMap)){
    		 createQcTestItemsInMap.put("userLogin",userLogin );
    	 }
    	 Map createQcTestResult = createProductQcTest(dctx,createQcTestInMap);
    	 String qcTestId = "";
    	 if(ServiceUtil.isSuccess(createQcTestResult)){
    		 qcTestId = (String)createQcTestResult.get("qcTestId");
    		 resultMap = ServiceUtil.returnSuccess("Successfully Created the Qc Details ");
    	 }else{
    		 Debug.logError("Error ::"+ServiceUtil.getErrorMessage(createQcTestResult), module);
    		 resultMap = ServiceUtil.returnError("Error ::"+ServiceUtil.getErrorMessage(createQcTestResult));
    	 }
    	 if(UtilValidate.isNotEmpty(qcTestId)){
    		 createQcTestItemsInMap.put("qcTestId", qcTestId);
    		 Map createProductQcTestDetailsResult = createProductQcTestDetails(dctx,createQcTestItemsInMap);
    		 if(ServiceUtil.isError(createProductQcTestDetailsResult) || ServiceUtil.isFailure(createProductQcTestDetailsResult)){
    			 Debug.logError("Error :: "+ServiceUtil.getErrorMessage(createProductQcTestDetailsResult),module);
    			 resultMap = ServiceUtil.returnError("Error :: "+ServiceUtil.getErrorMessage(createProductQcTestDetailsResult));
    		 }
    	 }
    	 resultMap.put("qcTestId",qcTestId);
    	 return resultMap;
     }// end of the service
     
     /**
      * 
      * @param dctx
      * @param context
      * @return
      */
     
     private static Map<String, Object> createProductQcTest(DispatchContext dctx,Map<String,Object> context) {
    	 LocalDispatcher dispatcher = dctx.getDispatcher();
    	 Delegator delegator = dctx.getDelegator();
    	 GenericValue userLogin = (GenericValue)context.get("userLogin");
    	 Map resultMap = ServiceUtil.returnSuccess();
    	 List<String> productQcTestFieldsList = (List)context.get("productQcTestFieldsList");
    	 GenericValue ProductQcTest = delegator.makeValue("ProductQcTest");
    	 Map<String,String> testFieldTypes = (Map)context.get("testFieldTypes");
    	 
    	 if(UtilValidate.isNotEmpty(productQcTestFieldsList)){
	    	 try{
		    	 for(String key : context.keySet()){
		    		 if(productQcTestFieldsList.contains(key)){
		    			 String fieldType = (String)testFieldTypes.get(key);
		    			 if(UtilValidate.isNotEmpty(fieldType)){
		    				 if(fieldType.equalsIgnoreCase("String")){
		    					 ProductQcTest.set(key,(String)context.get(key));
		    				 }
		    				 if(fieldType.equalsIgnoreCase("java.sql.Timestamp")){
		    					 ProductQcTest.set(key,(Timestamp)context.get(key));
		    				 }
		    				 if(fieldType.equalsIgnoreCase("java.math.BigDecimal")){
		    					 ProductQcTest.set(key,(BigDecimal)context.get(key));
		    				 }
		    			 }
		    		 }
		    	 }
		    	 ProductQcTest.set("createdByUserLogin", userLogin.getString("userLoginId"));
		    	 ProductQcTest.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
		    	 ProductQcTest.set("createdDate", UtilDateTime.nowTimestamp());
		    	 ProductQcTest.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		    	 ProductQcTest.setNextSeqId();
		    	 ProductQcTest.create();
		    	 
		    	 
	    	 }catch(Exception e){
	    		 Debug.logError("Error while creating productQcTest ::"+e,module);
	    		 resultMap = ServiceUtil.returnError("Error while creating productQcTest ::");
	    	 }
    	 }
    	 String qcTestId = "";
    	 if(UtilValidate.isNotEmpty(ProductQcTest)){
    		 qcTestId = (String) ProductQcTest.get("qcTestId");
    	 }
    	 resultMap.put("qcTestId",qcTestId);
    	 return resultMap;
     }//End of the service
     /**
      * 
      * @param dctx
      * @param context
      * @return
      */
     private static Map<String, Object> createProductQcTestDetails(DispatchContext dctx,Map<String,Object> context) {
    	 LocalDispatcher dispatcher = dctx.getDispatcher();
    	 Delegator delegator = dctx.getDelegator();
    	 Map resultMap = ServiceUtil.returnSuccess();
    	 String qcTestId = (String)context.get("qcTestId");
    	 try{
	    	 for(String key : context.keySet()){
	    		 if(key.contains("_testComponent")){
	    			 GenericValue ProductQcTestDetails = delegator.makeValue("ProductQcTestDetails");
	    			 String testComponent = (String)key.replace("_testComponent", "");
	    			 ProductQcTestDetails.put("testComponent", testComponent);
	    			 ProductQcTestDetails.put("value", (String)context.get(key));
	    			 ProductQcTestDetails.set("qcTestId", qcTestId);
	    			 delegator.setNextSubSeqId(ProductQcTestDetails,"sequenceNumber", 5, 1);
	    			 delegator.create(ProductQcTestDetails);
	    		 }
	    	 }
    	 }catch(GenericEntityException e){
    		 Debug.logError("Error while storing Product QC Test Details ::"+e,module);
    		 resultMap = ServiceUtil.returnError("Error while storing Product QC Test Details ::"+e);
    	 }
    	 return resultMap;
     }//End of the service
     
     public static String processFacilityTemperature(HttpServletRequest request, HttpServletResponse response) {
  		Delegator delegator = (Delegator) request.getAttribute("delegator");
  		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  		DispatchContext dctx =  dispatcher.getDispatchContext();
  		Locale locale = UtilHttp.getLocale(request);
  		Map<String, Object> result = ServiceUtil.returnSuccess();
  		HttpSession session = request.getSession();
  		String recordDateTimeStr = (String) request.getParameter("recordDateTime");
  		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
  		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
  		int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
  		if (rowCount < 1) {
  		Debug.logError("No rows to process, as rowCount = " + rowCount, module);
  		return "error";
  		}
  		Timestamp recordDateTime = null;
         SimpleDateFormat SimpleDF = new SimpleDateFormat("dd:MM:yyyy HH:mm");
 	  	if(UtilValidate.isNotEmpty(recordDateTimeStr)){
 	  		try {
 	  			recordDateTime = new java.sql.Timestamp(SimpleDF.parse(recordDateTimeStr).getTime());
 		  	} catch (ParseException e) {
 		  		Debug.logError(e, "Cannot parse date string: " + recordDateTimeStr, module);
 		  	} catch (NullPointerException e) {
 	  			Debug.logError(e, "Cannot parse date string: " + recordDateTimeStr, module);
 		  	}
 	  	}
  		boolean beganTransaction = false;
  		try{
  		       String facilityId = "";
  		       String temperature = "";
  		       String comments="";

  		Map inputCtx = FastMap.newInstance();
  		for (int i = 0; i < rowCount; i++) {
  			 
  			String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
  			if (paramMap.containsKey("facilityId" + thisSuffix)) {
  				facilityId = (String) paramMap.get("facilityId" + thisSuffix);
  			}else {
  			   request.setAttribute("_ERROR_MESSAGE_", "Missing facilityId");
  			}
  			if (paramMap.containsKey("temperature" + thisSuffix)) {
  				temperature = (String) paramMap.get("temperature" + thisSuffix);
  			}else {
  			   request.setAttribute("_ERROR_MESSAGE_", "Missing temperature");
  			}
  			if (paramMap.containsKey("comments" + thisSuffix)) {
  				comments = (String) paramMap.get("comments" + thisSuffix);
  			}else {
  				request.setAttribute("_ERROR_MESSAGE_", "Missing comments");
  			}
  			
  			inputCtx.clear();
  			inputCtx.put("facilityId", facilityId);
  			inputCtx.put("temperature", temperature);
  			inputCtx.put("comments", comments);
  			inputCtx.put("userLogin", userLogin);
  			inputCtx.put("recordDateTime", recordDateTime);
  			Map resultCtx = dispatcher.runSync("createFacilityTemperature", inputCtx);
  			if (ServiceUtil.isError(resultCtx)) {
  				Debug.logError("Record Adding Failed For : " + facilityId, module);
  				request.setAttribute("_ERROR_MESSAGE_", "Record Adding Failed for :"+facilityId);	
  				TransactionUtil.rollback();
  				return "error";
  			}
  			                     	
  		}
  		request.setAttribute("_EVENT_MESSAGE_", "Record(s) Successfully Added.!");
  		}catch (GenericEntityException e) {
  		try {
  				TransactionUtil.rollback(beganTransaction, "Error Fetching data", e);
  		 	} catch (GenericEntityException e2) {
  		 		Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  		 	}
  		 		Debug.logError("An entity engine error occurred while fetching data", module);
  		 	}
  		   	catch (GenericServiceException e) {
  		   	try {
  		   		TransactionUtil.rollback(beganTransaction, "Error while calling services", e);
  		   	} catch (GenericEntityException e2) {
  		   		Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
  		   	}
  		   		Debug.logError("An entity engine error occurred while calling services", module);
  		   	}
  		return "success";
  	}
     
     public static Map<String, Object> createFacilityTemperature(DispatchContext dctx, Map<String, ? extends Object> context) {
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         String facilityId = (String) context.get("facilityId");
         String temperature = (String) context.get("temperature");
         Timestamp recordDateTime = (Timestamp)context.get("recordDateTime");
         String comments = (String)context.get("comments");
         Map<String, Object> result = ServiceUtil.returnSuccess();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         try {
         	GenericValue facilityTemperature = delegator.makeValue("FacilityTemperature");
         	facilityTemperature.set("facilityId",facilityId);
         	facilityTemperature.set("temperature",temperature);
         	facilityTemperature.set("recordDateTime",recordDateTime);
         	if(UtilValidate.isNotEmpty(comments)){
         		facilityTemperature.set("comments",comments);
         	}
         	facilityTemperature.set("createdDate",UtilDateTime.nowTimestamp());
         	facilityTemperature.set("createdByUserLogin", userLogin.getString("userLoginId"));
         	facilityTemperature.set("lastModifiedDate", UtilDateTime.nowTimestamp());
         	facilityTemperature.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
         	facilityTemperature.create();
         }
         catch(Exception e){
         	Debug.logError(e, module);
         	return ServiceUtil.returnError(e.toString());
         }
         result = ServiceUtil.returnSuccess("Record Seccessfully Added.!");
         return result;
     }
     public static Map<String, Object> deleteFacilityTemperature(DispatchContext dctx, Map<String, ? extends Object> context) {
     	Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         String facilityId = (String) context.get("facilityId");
         Timestamp recordDateTime = (Timestamp)context.get("recordDateTime");
         Map<String, Object> result = ServiceUtil.returnSuccess();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         try {
         	GenericValue facilityTemperature = delegator.findOne("FacilityTemperature", UtilMisc.toMap("facilityId", facilityId, "recordDateTime", recordDateTime), false);
         	if(UtilValidate.isEmpty(facilityTemperature)){
 	  			Debug.logError("No Record Found To Delete.", module);
 				return ServiceUtil.returnError("No Record Found To Delete.");
 	  		}
         	facilityTemperature.remove();
         }
         catch(Exception e){
         	Debug.logError("Error While Deleting the Recorde.", module);
         	return ServiceUtil.returnError("Error While Deleting the Recorde.");
         }
         result = ServiceUtil.returnSuccess("Record Deleted Seccessfully.!");
         return result;
     }
     
     public static Map<String, ? extends Object> validateProductionTransfers(DispatchContext dctx, Map context) {
         Delegator delegator = dctx.getDelegator();
         LocalDispatcher dispatcher = dctx.getDispatcher();
         String facilityId = (String)context.get("facilityIdTo");
         String productId = (String)context.get("productId");
         BigDecimal incomingQty = (BigDecimal)context.get("quantity");
         Map<String, ? extends Object> result = ServiceUtil.returnSuccess();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         try {
        	 
        	 GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
        	 
        	 String facilityTypeId = "";
        	 if(UtilValidate.isNotEmpty(facility) && UtilValidate.isNotEmpty(facility.get("facilityTypeId"))){
        		 facilityTypeId = facility.getString("facilityTypeId");
        	 }
        	 
        	 GenericValue productFacility = delegator.findOne("ProductFacility", UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
        	 
        	 if(UtilValidate.isEmpty(productFacility)){
        		 Debug.logError("Product is not mapped to the facility: "+facilityId, module);
                 return ServiceUtil.returnError("Product is not mapped to the facility: "+facilityId);
        	 }
        	 
        	 if(UtilValidate.isNotEmpty(facilityTypeId) && facilityTypeId.equals("SILO")){
        		 
        		 boolean allowFacilityBlend = Boolean.FALSE;
        		 if(UtilValidate.isNotEmpty(facility.get("allowProductBlend")) && (facility.getString("allowProductBlend").equals("Y"))){
            		 allowFacilityBlend = Boolean.TRUE;
            	 }
        		 List conditionList = FastList.newInstance();
            	 
            	 conditionList.clear();
            	 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
            	 conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            	 EntityCondition invCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            	 List<GenericValue> inventoryProducts = delegator.findList("InventoryItem", invCond, UtilMisc.toSet("productId"), null, null, false);
            	 String blendedProductId = "";
            	 BigDecimal blendQty = null;
            	 
            	 if(allowFacilityBlend){
            		 conditionList.clear();
                	 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
                	 conditionList.add(EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "SILO_PROD_BLEND"));
                	 conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
                	 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
                	 EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
                	 List<GenericValue> prodAssoc = delegator.findList("ProductAssoc", condition, null, null, null, false);
                	 
                	 if(UtilValidate.isNotEmpty(prodAssoc)){
                		 GenericValue blendedProduct = EntityUtil.getFirst(prodAssoc);
                		 blendedProductId = blendedProduct.getString("productIdTo");
                		 if(UtilValidate.isNotEmpty(blendedProduct.get("quantity"))){
                			 blendQty = blendedProduct.getBigDecimal("quantity");
                		 }
                	 }
            	 }
        		 BigDecimal facilitySize = BigDecimal.ZERO;
            	 if(UtilValidate.isNotEmpty(facility.get("facilitySize"))){
            		 facilitySize = facility.getBigDecimal("facilitySize");
            	 }
            	 
            	 String invProductId = productId;
        		 if(UtilValidate.isNotEmpty(blendedProductId)){
        			 invProductId = blendedProductId;
        		 }
        		 
        		 // facility capacity check
            	 if(facilitySize.compareTo(BigDecimal.ZERO) >0){
            		 Map<String, ? extends Object> findCurrInventoryParams =  UtilMisc.toMap("productId", invProductId, "facilityId", facilityId);
            		 Map<String, Object> resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
                     if (ServiceUtil.isError(resultCtx)) {
                    	 Debug.logError("Problem getting inventory level of the request for product Id :"+invProductId, module);
                         return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+invProductId);
                     }
                     Object qohObj = resultCtx.get("quantityOnHandTotal");
                     BigDecimal qoh = BigDecimal.ZERO;
                     if (qohObj != null) {
                     	qoh = new BigDecimal(qohObj.toString());
                     }
                     if (UtilValidate.isEmpty(incomingQty)) {
                    	 incomingQty = BigDecimal.ZERO;
                     }
                     BigDecimal totalQtyInc = facilitySize.subtract(qoh.add(incomingQty));
                     if(totalQtyInc.compareTo(BigDecimal.ZERO) < 0){
                    	 Debug.logError("Facility capacity exceeded..!", module);
                         return ServiceUtil.returnError("Facility capacity exceeded..!"+facilityId);
                     }
            	 }
            	 
            	 List<String> extProdIds = EntityUtil.getFieldListFromEntityList(inventoryProducts, "productId", true);
            	 String extProd = "";
            	 for(String prodId : extProdIds){
            		 if(!prodId.equals(invProductId)){
            			 extProd = prodId;
                	 }
            	 }
            	 if(!extProdIds.contains(invProductId)){
            		 extProdIds.add(invProductId);
            	 }
            	// Raw Milk Silo Check
            	 if(allowFacilityBlend){
            		 
                	 if(UtilValidate.isEmpty(inventoryProducts)){
                		 if(UtilValidate.isNotEmpty(blendedProductId)){
            				 if(UtilValidate.isNotEmpty(blendQty)){
                    			 Object qty = blendQty;
                    		 }
                    		 Object blendProdId = blendedProductId;
                 		 }
            		 }else{
            			 String prodToCompare = (String)extProdIds.get(0);
            			 if(!prodToCompare.equals(invProductId)){
            				 Debug.logError("Already product with Id :["+prodToCompare+"] exists. Empty it, before storing new product :"+invProductId, module);
                             return ServiceUtil.returnError("Already product with Id :["+prodToCompare+"] exists. Empty it, before storing new product :"+invProductId);
            			 }
            		 }
            	 }
            	 else{
            		 if(extProdIds.size() > 1){
            			 Debug.logError("Already product with Id :["+extProd+"] exists. Empty it, before storing new product :"+invProductId, module);
                         return ServiceUtil.returnError("Already product with Id :["+extProd+"] exists. Empty it, before storing new product :"+invProductId);
            		 }
            	 }
        	 }
         }
         catch(Exception e){
         	Debug.logError(e, module);
         	return ServiceUtil.returnError(e.toString());
         }
         return result;
     }
     /**
      * 
      * @param request
      * @param response
      * @return
      */
     public static String IssueRequestThroughTransfer(HttpServletRequest request, HttpServletResponse response) {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
		  	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		  	  Locale locale = UtilHttp.getLocale(request);
		  	  Map<String, Object> resultCtx = ServiceUtil.returnSuccess();
		  	  HttpSession session = request.getSession();
		  	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		  	  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		  	  
		  	  String custRequestId = (String)paramMap.get("custRequestId");
		  	  String custRequestItemSeqId = (String)paramMap.get("custRequestItemSeqId");
		  	  
		  	  BigDecimal toBeIssuedQty = BigDecimal.ZERO;
		  	  String qty="";
		  	  String facilityId = "";
		  	  String shipmentTypeId = "";
		  	  String tankerNo = (String)paramMap.get("tankerNo");
		  	  String partyIdTo = (String)paramMap.get("partyIdTo");
		  	  String productId = (String)paramMap.get("productId");
		  	  
		  	  
		  	 int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
		  	  if (rowCount < 1 && UtilValidate.isEmpty(custRequestId) && UtilValidate.isEmpty(custRequestItemSeqId)) {
		  		  Debug.logError("No rows to process, as rowCount = " + rowCount, module);
				  request.setAttribute("_ERROR_MESSAGE_", "No rows to process");	  		  
		  		  return "error";
		  	  }
		  	  if(UtilValidate.isEmpty(tankerNo)){
		  		  Debug.logError("Vehicle Number should not be empty ", module);
	  			  request.setAttribute("_ERROR_MESSAGE_", "Vehicle Number should not be empty ");
	  			  return "error";
		  	  }
		  	  GenericValue MilkTransfer = null;
		  	  for (int i = 0; i < rowCount; i++){
		  		  String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if (paramMap.containsKey("custRequestId" + thisSuffix)) {
		  			custRequestId = (String) paramMap.get("custRequestId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
		  			custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId"+thisSuffix);
		  		  }
		  	  }
		  	  if(UtilValidate.isEmpty(custRequestId)){
		  		  Debug.logError("Indent not found ",module);
		  		  request.setAttribute("_ERROR_MESSAGE_", "Indent not found ");
	  			  return "error";
		  	  }
		  	  
		  	  try{
		  		  List tranConditionList = UtilMisc.toList(EntityCondition.makeCondition("containerId",EntityOperator.EQUALS,tankerNo));
		  		  tranConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_INPROCESS"));
		  		  EntityCondition tranCondition = EntityCondition.makeCondition(tranConditionList);
		  		  List<GenericValue> milkTransferList = delegator.findList("MilkTransfer",tranCondition,null,null,null,false);
		  		  MilkTransfer = EntityUtil.getFirst(milkTransferList);
		  		  
		  	  }catch(Exception e){
		  		  Debug.logError("Error while checking vehicle previous status "+e , module);
		  		  request.setAttribute("_ERROR_MESSAGE_", "Error while checking vehicle previous status "+e.getMessage());
	  			  return "error";
		  	  }
		  	  
		  	  if(UtilValidate.isNotEmpty(MilkTransfer)){
		  		  Debug.logError("Transfer is in process for the given vehicle. " , module);
		  		  request.setAttribute("_ERROR_MESSAGE_", "Transfer is in process for the given vehicle. ");
	  			  return "error";
		  	  }
		  	  
		  	  // Here we are checking for already initiated transfers 
		  	  
		  	  try{
		  		  List initTranConditionList = FastList.newInstance();
		  		  initTranConditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId));
		  		  initTranConditionList.add(EntityCondition.makeCondition("custRequestItemSeqId",EntityOperator.EQUALS,custRequestItemSeqId));
		  		  initTranConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,UtilMisc.toList("MXF_INPROCESS","MXF_INIT")));
		  		  EntityCondition tranCondition = EntityCondition.makeCondition(initTranConditionList);
		  		  
		  	      List<GenericValue> milkTransferList = delegator.findList("MilkTransfer",tranCondition,null,null,null,false);
		  		  MilkTransfer = EntityUtil.getFirst(milkTransferList);
		  		  Debug.log("MilkTransfer=============="+MilkTransfer);
		  	  }catch(GenericEntityException e){
		  		  Debug.logError("Error while getting already initiated transfers for this indent" , module);
		  		  request.setAttribute("_ERROR_MESSAGE_", "Error while getting already initiated transfers for this indent");
	  			  return "error";
		  	  }
		  	  if(UtilValidate.isNotEmpty(MilkTransfer)){
		  		  Debug.logError("MilkTransfer already initiated for this indent  with transferId:"+MilkTransfer.get("milkTransferId"), module);
		  		  request.setAttribute("_ERROR_MESSAGE_", "MilkTransfer already initiated for this indent  with transferId:"+(String)MilkTransfer.get("milkTransferId"));
	  			  return "error";
		  	  }
		  	  
		  	  // here we are trying to initiate vehicle trip
		  	  
		  	  
		  	  
		  	Map vehicleTripMap = FastMap.newInstance();
	 		vehicleTripMap.put("vehicleId", tankerNo);
	 		vehicleTripMap.put("partyId", partyIdTo);
	 		vehicleTripMap.put("userLogin",userLogin);
	 		Map vehicleTripResultMap = FastMap.newInstance();
	 		
		 	try{	
	 			vehicleTripResultMap = dispatcher.runSync("createVehicleTrip", vehicleTripMap);
		 		
		 		if(ServiceUtil.isError(vehicleTripResultMap)){
		 			Debug.logError("Error While Creating vehicleTrip :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
		 			request.setAttribute("_ERROR_MESSAGE_","Error while creating vehicle Trip ");
		 			return "error";
		 		}
		 	}catch(GenericServiceException e){
		 		Debug.logError("Error while creating vehicle Trip ::" +e ,module);
		 		request.setAttribute("_ERROR_MESSAGE_","Error while creating vehicle Trip ::" +e.getMessage());
		 		return "error";
		 	}
	 		String sequenceNum = (String)vehicleTripResultMap.get("sequenceNum");  
		  	 
	 		// Here we are initiating vehicleTrip Status
	 		Map vehicleTripStatusMap = FastMap.newInstance();
	 		vehicleTripStatusMap.putAll(vehicleTripResultMap);
	 		vehicleTripStatusMap.put("statusId","MR_ISSUE_INIT");
	 		vehicleTripStatusMap.put("userLogin",userLogin);
	 		vehicleTripStatusMap.put("estimatedStartDate",UtilDateTime.nowTimestamp());
	 		try{
		 		Map vehicleStatusResultMap = dispatcher.runSync("createVehicleTripStatus", vehicleTripStatusMap);
		 		if(ServiceUtil.isError(vehicleTripResultMap)){
		 			Debug.logError("Error While Creating vehicleTripStatus :: "+ServiceUtil.getErrorMessage(vehicleTripResultMap),module);
		 			request.setAttribute("_ERROR_MESSAGE_","Error while creating vehicle Trip Status");
		 			return "error";
		 		}
	 		}catch(GenericServiceException e){
	 			Debug.logError("Error while creating vehicleTrip Status :"+e,module);
	 			request.setAttribute("_ERROR_MESSAGE_","Error while creating vehicleTrip Status :"+e.getMessage());
	 			return "error";
	 		}
	 		
		  	  
		  	String milkTransferId = "";
		  	try{
	  		  MilkTransfer = delegator.makeValue("MilkTransfer");
	  		  MilkTransfer.set("containerId", tankerNo);
	  		  MilkTransfer.set("sequenceNum", sequenceNum);
	  		  MilkTransfer.set("statusId","MXF_INIT");
	  		  MilkTransfer.set("productId",productId);
	  		  MilkTransfer.set("partyIdTo",partyIdTo);
	  		  MilkTransfer.set("custRequestId",custRequestId);
	  		  MilkTransfer.set("custRequestItemSeqId",custRequestItemSeqId);
	  		  MilkTransfer.set("sendDate",UtilDateTime.nowTimestamp());
	  		  MilkTransfer.set("createdByUserLogin", userLogin.getString("userLoginId"));
	  		  MilkTransfer.set("lastModifiedByUserLogin",userLogin.getString("userLoginId"));
	  		  
	  		  delegator.createSetNextSeqId(MilkTransfer);
		  	}catch(GenericEntityException e){
	  		  Debug.logError("Error while initiating Transfer ::"+e , module);
	  		  request.setAttribute("_ERROR_MESSAGE_", "Error while initiating Transfer ::"+e.getMessage());
  			  return "error";
		  	}
		  	  //String createNewShipment = "Y";
		  	  
		  	  
			 /* for (int i = 0; i < rowCount; i++){
		  		 qty="";
			  	 facilityId = "";
			  	 shipmentTypeId = "";  
			  	 toBeIssuedQty = BigDecimal.ZERO;
	  			 String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		  		  if(paramMap.containsKey("shipmentTypeId" + thisSuffix)){
		  			shipmentTypeId = (String) paramMap.get("shipmentTypeId" + thisSuffix);
		  		  }
		  		  if(paramMap.containsKey("facilityId" + thisSuffix)){
		  			facilityId = (String) paramMap.get("facilityId" + thisSuffix);
		  		  }
		  		  
		  		  if (paramMap.containsKey("custRequestId" + thisSuffix)) {
		  			custRequestId = (String) paramMap.get("custRequestId"+thisSuffix);
		  		  }
		  		  if (paramMap.containsKey("custRequestItemSeqId" + thisSuffix)) {
		  			custRequestItemSeqId = (String) paramMap.get("custRequestItemSeqId"+thisSuffix);
		  		  }
		  		   if (paramMap.containsKey("toBeIssuedQty" + thisSuffix)) {
		  			 qty = (String) paramMap.get("toBeIssuedQty"+thisSuffix);
		  		  }
		  		  if(qty.contains(","))
		  		   {
		  			 qty = qty.replace(",", "");
		  		   }
		  		  if(UtilValidate.isNotEmpty(qty)){
					  try {
						  toBeIssuedQty = new BigDecimal(qty);
			  		  } catch (Exception e) {
			  			  Debug.logError(e, "Problems parsing quantity string: " + qty, module);
			  			  request.setAttribute("_ERROR_MESSAGE_", "Problems parsing quantity string: " + qty);
			  			  return "error";
			  		  }
		  		  }
		  		  if(UtilValidate.isEmpty(toBeIssuedQty) || (UtilValidate.isNotEmpty(toBeIssuedQty) && toBeIssuedQty.compareTo(BigDecimal.ZERO)<=0)){
					request.setAttribute("_ERROR_MESSAGE_", "Cannot Accept Quantity ZERO for"+custRequestId+"--!");	  		  
			  		  return "error";
		  		  }
		  		  if(toBeIssuedQty.compareTo(BigDecimal.ZERO)==1){
		  			  try{
							Map issuanceMapCtx = FastMap.newInstance();
							issuanceMapCtx.put("custRequestId", custRequestId);
							issuanceMapCtx.put("custRequestItemSeqId", custRequestItemSeqId);
							issuanceMapCtx.put("toBeIssuedQty", toBeIssuedQty);
							issuanceMapCtx.put("facilityId", facilityId);
							issuanceMapCtx.put("shipmentTypeId", shipmentTypeId);
							issuanceMapCtx.put("userLogin", userLogin);
							issuanceMapCtx.put("locale", locale);
							issuanceMapCtx.put("createNewShipment", createNewShipment);
							resultCtx = dispatcher.runSync("issueProductForRequest", issuanceMapCtx);
							
							if (ServiceUtil.isError(resultCtx)) {
								Debug.logError("Issuance Failed in Service for Indent : " + custRequestId+":"+custRequestItemSeqId, module);
								return "error";
							}
						} catch (Exception e) {
							// TODO: handle exception
							Debug.logError(e, module);
							request.setAttribute("_ERROR_MESSAGE_", " Issuance Request Failed ");
				  			return "error";
						}
		  			  
		  			  if(ServiceUtil.isSuccess(resultCtx) && createNewShipment.equalsIgnoreCase("Y")){
		  				  String shipmentId = (String)resultCtx.get("shipmentId");
		  				  if(UtilValidate.isNotEmpty(shipmentId) ){
		  					  MilkTransfer.set("productId",(String)paramMap.get("productId"));
		  					  MilkTransfer.set("shipmentId", shipmentId);
		  					  Debug.log("MilkTransfer=========="+MilkTransfer);
		  					  try{
		  						  delegator.store(MilkTransfer);
		  					  }catch(Exception e){
		  						  Debug.logError("Error while storing shipment To MilkTransfer "+e,module);
		  						  request.setAttribute("_ERROR_MESSAGE_", "Error while storing shipment To MilkTransfer "+e.getMessage());
		  						  return "error";
		  					  }
		  					  
		  				  }
		  			  }
		  			createNewShipment="N";
		  		  }	  
		  		
		  	}*/
			    request.setAttribute("_EVENT_MESSAGE_", "successfully Initiated  MilkTransfer :");
				return "success";
		}
     public static Map<String, Object> cancelProductionIssuenceForCustRequest(DispatchContext ctx, Map<String, Object> context) {
	        LocalDispatcher dispatcher = ctx.getDispatcher();
	        Delegator delegator = ctx.getDelegator();
	        Locale locale = (Locale) context.get("locale");
	        String custRequestId = (String)context.get("custRequestId");
	        String custRequestItemSeqId = (String)context.get("custRequestItemSeqId");
	        String itemIssuanceId = (String)context.get("itemIssuanceId");
	        String shipmentId = (String)context.get("shipmentId");
	        String facilityId = (String)context.get("facilityId");
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        try {
	        	String productId="";
	        	/*GenericValue custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId), false);
	        	
	        	if (custRequestItem == null) {
	                return ServiceUtil.returnError("No Request found for Id : "+custRequestId+" and seqId : "+custRequestItemSeqId);
	            }*/
	        	GenericValue issuanceAndShipmentAndCustRequest= null;
	        	List<GenericValue> issuanceAndShipmentAndCustRequestList = delegator.findList("IssuanceAndShipmentAndCustRequest", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
	        	
	        	if(UtilValidate.isNotEmpty(issuanceAndShipmentAndCustRequestList)){
	        		issuanceAndShipmentAndCustRequest = EntityUtil.getFirst(issuanceAndShipmentAndCustRequestList);
	        	    productId=issuanceAndShipmentAndCustRequest.getString("productId");
	        	}
	        	
             List<GenericValue> itemIssuanceList = delegator.findList("ItemIssuance", EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId), null, null, null, false);
	            for(GenericValue itemShipIssuence:itemIssuanceList){
	            	
	                itemIssuanceId=itemShipIssuence.getString("itemIssuanceId");
	            	BigDecimal actQuantity = itemShipIssuence.getBigDecimal("quantity");
	            	BigDecimal cancelQuantity = actQuantity;
	                
	            	//update isssuenceItem
	            	Map itemIssueCtx = FastMap.newInstance();
					itemIssueCtx.put("cancelQuantity", cancelQuantity);
					itemIssueCtx.put("itemIssuanceId", itemIssuanceId);
					itemIssueCtx.put("userLogin", userLogin);
					itemIssueCtx.put("modifiedByUserLoginId", userLogin.getString("userLoginId"));
					itemIssueCtx.put("modifiedDateTime", UtilDateTime.nowTimestamp());
					
					Map resultCtx = dispatcher.runSync("updateItemIssuance", itemIssueCtx);
					if (ServiceUtil.isError(resultCtx)) {
						Debug.logError("Problem updateItemIssuance item issuance for requested item", module);
						return resultCtx;
					}
	            	
	            	String inventoryItemId=itemShipIssuence.getString("inventoryItemId");
	            	//update inventery details.
	            	Map createInvDetail = FastMap.newInstance();
					createInvDetail.put("userLogin", userLogin);
					createInvDetail.put("inventoryItemId", inventoryItemId);
					createInvDetail.put("itemIssuanceId", itemIssuanceId);
					createInvDetail.put("quantityOnHandDiff", actQuantity);
					createInvDetail.put("availableToPromiseDiff", actQuantity);
					Map invResultCtx = dispatcher.runSync("createInventoryItemDetail", createInvDetail);
					if (ServiceUtil.isError(invResultCtx)) {
						Debug.logError("Problem Incrementing inventory for requested item ", module);
						return invResultCtx;
					}
					
	            }
				//set previous status
					Map itemStatusCtx = FastMap.newInstance();
					itemStatusCtx.put("custRequestId", custRequestId);
					itemStatusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
					itemStatusCtx.put("userLogin", userLogin);
					itemStatusCtx.put("description", "");
					itemStatusCtx.put("statusId", "CRQ_SUBMITTED");
					Map crqResultCtx = dispatcher.runSync("setCustRequestItemStatus", itemStatusCtx);
					if (ServiceUtil.isError(crqResultCtx)) {
						Debug.logError("Problem changing status for requested item ", module);
						return crqResultCtx;
					}
					//updating shipment
		            try{
		            	GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		            	shipment.set("statusId","SHIPMENT_CANCELLED");
		            	shipment.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
		            	shipment.set("lastModifiedDate", UtilDateTime.nowTimestamp());
		                delegator.store(shipment);
		            } catch (GenericEntityException e) {
		                Debug.logError(e, module);
		                return ServiceUtil.returnError("Failed to update shipment " + e);            
		            }
		            
	            result = ServiceUtil.returnSuccess("Successfully Canceled item :"+productId+" for Indent Number:"+custRequestId);
	        } catch (GenericEntityException e) {
	            Debug.logError("Problem in retriving data from database", module);
	        } catch (GenericServiceException e) {
	            Debug.logError("Problem in calling service issueInventoryItemToCustRequest", module);
	            return ServiceUtil.returnError("Problem in calling service issueInventoryItemToCustRequest");
	        }
	        /**
	         * Here we are trying to cancel the MilkTransfer if it exists.
	         */
	         if(ServiceUtil.isSuccess(result)){
	        	 try{
	        		 List<GenericValue> milkTransfersList = delegator.findList("MilkTransfer",EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipmentId),null,null,null,false);
	        		 if(UtilValidate.isNotEmpty(milkTransfersList)){
	        			 GenericValue milkTransfer = EntityUtil.getFirst(milkTransfersList);
	        			 milkTransfer.set("statusId","MXF_CANCELLED");
	        			 try{
	        				 delegator.store(milkTransfer);
	        			 }catch(Exception e){
	        				 Debug.logError("Error while restoring Transfer status ::"+e,module);
	        				 result = ServiceUtil.returnError("Error while restoring Transfer status ::"+e.getMessage());
	        			 }
	        		 }
	        		 
	        	 }catch(Exception e){
	        		 Debug.logError("Error while cancelling related MilkTransfers ::"+e, module);
	        		 result = ServiceUtil.returnError("Error while cancelling related MilkTransfers ::"+e.getMessage()); 
	        	 }
	         }
	        return result;
	   }  
     public static Map<String, Object> setRequestItemStatus(DispatchContext ctx,Map<String, ? extends Object> context) {
 		Delegator delegator = ctx.getDelegator();
 		LocalDispatcher dispatcher = ctx.getDispatcher();
 		String statusId = (String) context.get("statusId");
 		String custRequestId = (String) context.get("custRequestId");
 		String custRequestItemSeqId = (String) context.get("custRequestItemSeqId");
 		GenericValue userLogin = (GenericValue) context.get("userLogin");
 		Map result = ServiceUtil.returnSuccess();
 		try{
 			Map statusCtx = FastMap.newInstance();
 				statusCtx.put("statusId", statusId);
 				statusCtx.put("custRequestId", custRequestId);
 				statusCtx.put("custRequestItemSeqId", custRequestItemSeqId);
 				statusCtx.put("userLogin", userLogin);
 				statusCtx.put("description", "");
 				Map resultCtx = dispatcher.runSync("setCustRequestItemStatus", statusCtx);
 				if (ServiceUtil.isError(resultCtx)) {
 					Debug.logError("RequestItem set status failed for Request: " + custRequestId+" : "+custRequestItemSeqId, module);
 					return resultCtx;
 				}
 		} catch (Exception e) {
 			// TODO: handle exception
 			Debug.logError(e, module);
 			return ServiceUtil.returnError(e.getMessage());
 		}
 		return result;
 	} 
     
}
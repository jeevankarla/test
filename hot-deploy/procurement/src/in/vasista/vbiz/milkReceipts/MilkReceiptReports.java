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
import java.util.TreeMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


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
public class MilkReceiptReports {

	public static final String module = MilkReceiptReports.class.getName();
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
    
    private static Map<String, Object> initFieldsMap() {
		Map<String, Object> fieldsMap = FastMap.newInstance();
		fieldsMap.put("qtyKgs", ZERO);
		fieldsMap.put("qtyLtrs", ZERO);
		fieldsMap.put("snf", ZERO);
		fieldsMap.put("fat", ZERO);
		fieldsMap.put("kgFat", ZERO);		
		fieldsMap.put("kgSnf", ZERO);		
		//fieldsMap.put("sendLR", ZERO);
		//fieldsMap.put("sendAcidity", ZERO);
		//fieldsMap.put("sendTemparature", ZERO);
		
		
		fieldsMap.put("recdQtyKgs", ZERO);
		fieldsMap.put("recdQtyLtrs", ZERO);
		fieldsMap.put("receivedSnf", ZERO);
		fieldsMap.put("receivedFat", ZERO);
		fieldsMap.put("recdKgFat", ZERO);		
		fieldsMap.put("recdKgSnf", ZERO);		
		//fieldsMap.put("receivedLR", ZERO);
		//fieldsMap.put("gheeYield", ZERO);
		//fieldsMap.put("receivedAcidity", ZERO);
		//fieldsMap.put("receivedTemparature", ZERO);
		
		fieldsMap.put("sQtyLtrs", ZERO);
		fieldsMap.put("sQtyKgs", ZERO);
		fieldsMap.put("cQtyLtrs", ZERO);
		fieldsMap.put("sFat", ZERO);
		fieldsMap.put("sSnf", ZERO);
		fieldsMap.put("sKgFat", ZERO);
		fieldsMap.put("sKgSnf", ZERO);
		
		
		
		return fieldsMap;
    }
    
    private static Map<String, Object> initDayMap(List<String> productIds) {		
        Map<String, Object> result = FastMap.newInstance();
        
        List<String> allproductIds = FastList.newInstance();
        allproductIds.addAll(productIds);
        allproductIds.add("TOT");
        for (int i = 0; i < allproductIds.size(); ++i) {	
        	result.put(allproductIds.get(i), initFieldsMap());
        }
        return result;   
    }

    private static void populateProductMap(BigDecimal qtyKgs,BigDecimal qtyLtrs,BigDecimal  kgFat,BigDecimal kgSnf,BigDecimal  sQtyLtrs,BigDecimal sQtyKgs,BigDecimal sKgFat,BigDecimal sKgSnf,BigDecimal  cQtyLtrs,BigDecimal recdQtyKgs,BigDecimal recdQtyLtrs,BigDecimal recdKgFat,BigDecimal recdKgSnf,Map<String, Object> productMap){
    	
    	BigDecimal totQtyLts = qtyLtrs.add((BigDecimal)productMap.get("qtyLtrs"));
    	BigDecimal totQtyKgs = qtyKgs.add((BigDecimal)productMap.get("qtyKgs"));
    	BigDecimal totKgFat = kgFat.add((BigDecimal)productMap.get("kgFat")); 
    	BigDecimal totKgSnf = kgSnf.add((BigDecimal)productMap.get("kgSnf")); 
    	BigDecimal totFat = ZERO;
    	BigDecimal totSnf = ZERO;
    	
    	if(totQtyKgs.compareTo(BigDecimal.ZERO)>0){
    		totFat = (ProcurementNetworkServices.calculateFatOrSnf(totKgFat, totQtyKgs)).setScale(1, BigDecimal.ROUND_HALF_UP);
    		totSnf = (ProcurementNetworkServices.calculateFatOrSnf(totKgSnf, totQtyKgs)).setScale(2,BigDecimal.ROUND_HALF_UP);
    	}
    	
    	
    	
    	BigDecimal sTotQtyLtrs = sQtyLtrs.add((BigDecimal)productMap.get("sQtyLtrs"));
    	BigDecimal sTotKgFat = sKgFat.add((BigDecimal)productMap.get("sKgFat"));
    	BigDecimal sTotKgSnf = sKgSnf.add((BigDecimal)productMap.get("sKgSnf"));
    	BigDecimal sTotFat = ZERO;
    	BigDecimal sTotSnf = ZERO;
    	BigDecimal sTotQtyKgs = ZERO;
    	BigDecimal totRecdQtyKgs = recdQtyKgs.add((BigDecimal)productMap.get("recdQtyKgs"));
    	BigDecimal totRecdQtyLtrs =recdQtyLtrs.add((BigDecimal)productMap.get("recdQtyLtrs"));
    	BigDecimal totRecdFat = BigDecimal.ZERO;
    	BigDecimal totRecdSnf = BigDecimal.ZERO;
    	BigDecimal totRecdKgFat = recdKgFat.add((BigDecimal)productMap.get("recdKgFat"));
    	BigDecimal totRecdKgSnf = recdKgSnf.add((BigDecimal)productMap.get("recdKgSnf"));    	
    	
    	if(totQtyKgs.compareTo(BigDecimal.ZERO)>0){
    		totRecdFat = (ProcurementNetworkServices.calculateFatOrSnf(totRecdKgFat, totRecdQtyKgs)).setScale(1,BigDecimal.ROUND_HALF_UP);
    		totRecdSnf = (ProcurementNetworkServices.calculateFatOrSnf(totRecdKgSnf, totRecdQtyKgs)).setScale(2,BigDecimal.ROUND_HALF_UP);
    	}
    	
    	BigDecimal cTotQtyLtrs = cQtyLtrs.add((BigDecimal)productMap.get("cQtyLtrs"));
    	
    	if(!(sTotQtyLtrs.equals(ZERO))){
    		sTotQtyKgs = (ProcurementNetworkServices.convertLitresToKGSetScale(sTotQtyLtrs ,true)).setScale(decimals,rounding);
    		if(!(sTotKgFat.equals(ZERO))){
        		sTotFat = ProcurementNetworkServices.calculateFatOrSnf(sTotKgFat, sTotQtyKgs);
        	}
    	}
    	if(!(sTotQtyKgs.equals(ZERO))){
    		if(!(sTotKgSnf.equals(ZERO))){
        		sTotSnf = ProcurementNetworkServices.calculateFatOrSnf(sTotKgSnf, sTotQtyKgs);
        	}
    	}
    	productMap.put("qtyLtrs", totQtyLts);
    	productMap.put("qtyKgs", totQtyKgs);
        productMap.put("kgFat", totKgFat); 
        productMap.put("kgSnf", totKgSnf);  
        productMap.put("fat", totFat);
        productMap.put("snf", totSnf);
        
        productMap.put("recdQtyLtrs", totRecdQtyLtrs);
    	productMap.put("recdQtyKgs", totRecdQtyKgs);
        productMap.put("recdKgFat", totRecdKgFat); 
        productMap.put("recdKgSnf", totRecdKgSnf);  
        productMap.put("receivedFat", totRecdFat);
        productMap.put("receivedSnf", totRecdSnf);
        
        
        productMap.put("sQtyLtrs", sTotQtyLtrs);
        productMap.put("sQtyKgs", sTotQtyKgs);
        productMap.put("sFat", sTotFat);
        productMap.put("sSnf", sTotSnf);
        productMap.put("sKgFat", sTotKgFat);
        productMap.put("sKgSnf", sTotKgSnf);
        
        productMap.put("cQtyLtrs", cTotQtyLtrs);
    }
    
    private static void populateDayTotalsMap(GenericValue orderItem, Map<String, Object> dayTotalsMap) {
        String productId = orderItem.getString("receivedProductId");    
        BigDecimal qtyKgs = BigDecimal.ZERO;
        BigDecimal qtyLtrs = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("quantity"))){
        	 qtyKgs = orderItem.getBigDecimal("quantity").setScale(decimals, rounding);
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("quantityLtrs"))){
        	 qtyLtrs = orderItem.getBigDecimal("quantityLtrs").setScale(decimals, rounding);
        }
        if(qtyLtrs.compareTo(BigDecimal.ZERO)==0){
        	qtyLtrs = ProcurementNetworkServices.convertKGToLitre(qtyKgs);
        }
        BigDecimal snf = orderItem.getBigDecimal("snf");
        BigDecimal fat = orderItem.getBigDecimal("fat"); 
       
        
        BigDecimal kgFat = orderItem.getBigDecimal("sendKgFat");
        BigDecimal kgSnf = orderItem.getBigDecimal("sendKgSnf");
        BigDecimal sQtyLtrs = BigDecimal.ZERO;
        BigDecimal sQtyKgs = BigDecimal.ZERO;
        BigDecimal sFat = BigDecimal.ZERO;
        BigDecimal sSnf = BigDecimal.ZERO;
        BigDecimal sKgFat =BigDecimal.ZERO;
        BigDecimal cQtyLtrs = BigDecimal.ZERO;
        BigDecimal sKgSnf = BigDecimal.ZERO;
        
        BigDecimal recdQtyKgs = BigDecimal.ZERO;
        BigDecimal recdQtyLtrs = BigDecimal.ZERO;
        BigDecimal recdKgFat = BigDecimal.ZERO;
        BigDecimal recdKgSnf = BigDecimal.ZERO;
        BigDecimal recdFat = BigDecimal.ZERO;
        BigDecimal recdSnf = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("receivedQuantity"))){
        	recdQtyKgs = orderItem.getBigDecimal("receivedQuantity");
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("receivedQuantityLtrs"))){
        	recdQtyLtrs = orderItem.getBigDecimal("receivedQuantityLtrs");
        }
        if(recdQtyLtrs.compareTo(BigDecimal.ZERO)==0){
        	recdQtyLtrs = ProcurementNetworkServices.convertKGToLitre(recdQtyKgs);
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("receivedKgFat"))){
        	recdKgFat = orderItem.getBigDecimal("receivedKgFat");
        }
        
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("receivedKgSnf"))){
        	recdKgSnf = orderItem.getBigDecimal("receivedKgSnf");
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("receivedFat"))&&(recdKgFat.compareTo(BigDecimal.ZERO)==0)){
        	recdFat = orderItem.getBigDecimal("receivedFat");
        	recdKgFat = (recdQtyKgs.multiply(recdFat).divide(new BigDecimal(100))).setScale(4,BigDecimal.ROUND_HALF_UP);
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("receivedSnf"))&&(recdKgSnf.compareTo(BigDecimal.ZERO)==0)){
        	recdSnf = orderItem.getBigDecimal("receivedSnf");
        	recdKgSnf = (recdQtyKgs.multiply(recdSnf).divide(new BigDecimal(100))).setScale(4,BigDecimal.ROUND_HALF_UP);
        }
        if(orderItem.getBigDecimal("sQuantityLtrs") !=null){
        	sQtyLtrs = orderItem.getBigDecimal("sQuantityLtrs").setScale(2, rounding);
        	//sQtyKgs = sQtyLtrs.multiply(new BigDecimal(1.03));
        	sQtyKgs = ProcurementNetworkServices.convertLitresToKGSetScale(sQtyLtrs, true);
        }
        if(UtilValidate.isNotEmpty( orderItem.getBigDecimal("sKgFat"))){
        	sKgFat = orderItem.getBigDecimal("sKgFat").setScale(decimals, rounding);
        }
        if(UtilValidate.isNotEmpty( orderItem.getBigDecimal("sKgSnf"))){
        	sKgSnf = orderItem.getBigDecimal("sKgSnf").setScale(decimals, rounding);
        }
               
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("sFat"))&&(sKgFat.compareTo(BigDecimal.ZERO)==0)){
        	sFat = orderItem.getBigDecimal("sFat").setScale(1, rounding);
        	sKgFat = (sQtyKgs.multiply(sFat).divide(new BigDecimal(100))).setScale(4,BigDecimal.ROUND_HALF_UP);
        }
        if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("sFat"))&&(sKgFat.compareTo(BigDecimal.ZERO)==0)){
        	sSnf = orderItem.getBigDecimal("sSnf").setScale(2, rounding);
        	sKgSnf = (sQtyKgs.multiply(sSnf).divide(new BigDecimal(100))).setScale(4,BigDecimal.ROUND_HALF_UP);
        }
        
        
        if(orderItem.getBigDecimal("cQuantityLtrs") !=null){
        	cQtyLtrs = orderItem.getBigDecimal("cQuantityLtrs").setScale(2, rounding);
        }
        
        Timestamp receiveDate = orderItem.getTimestamp("receiveDate");   
        String dateKey = UtilDateTime.toDateString(receiveDate, "yyyy/MM/dd");
        Map dateMap = (Map)dayTotalsMap.get(dateKey);
        Map productMap = (Map)dateMap.get(productId);
        
        populateProductMap(qtyKgs,qtyLtrs, kgFat,kgSnf, sQtyLtrs,sQtyKgs,sKgFat,sKgSnf, cQtyLtrs,recdQtyKgs,recdQtyLtrs,recdKgFat,recdKgSnf,productMap);

        productMap = (Map)dateMap.get("TOT");  
        populateProductMap(qtyKgs,qtyLtrs, kgFat,kgSnf, sQtyLtrs,sQtyKgs,sKgFat,sKgSnf, cQtyLtrs,recdQtyKgs,recdQtyLtrs,recdKgFat,recdKgSnf,productMap);   
        
        // update dayTotals TOT
        Map dayTotalsTOTMap = (Map)dayTotalsMap.get("TOT");
        productMap = (Map)dayTotalsTOTMap.get(productId);  
        populateProductMap(qtyKgs,qtyLtrs, kgFat,kgSnf, sQtyLtrs,sQtyKgs,sKgFat,sKgSnf, cQtyLtrs,recdQtyKgs,recdQtyLtrs,recdKgFat,recdKgSnf,productMap);    
        
        productMap = (Map)dayTotalsTOTMap.get("TOT");  
        populateProductMap(qtyKgs,qtyLtrs, kgFat,kgSnf, sQtyLtrs,sQtyKgs,sKgFat,sKgSnf, cQtyLtrs,recdQtyKgs,recdQtyLtrs,recdKgFat,recdKgSnf,productMap);
    }
    
    /**
     * Get the procurement totals for the requested period. Totals are returned for the requested facility Id
     * as well as the breakup center-wise if the given facility Id is a unit, route, plant, etc  
     * @return totals map for the requested facility Id (including center-wise totals breakup) which look like this:
     * Here 101,102,103 are MilkReceipt Products.
     * 
     * 
     * totalsMap : {
     *  facilityId : {
     *  	dayTotals : {date:   {101 : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, },
     *  						  102 : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, },
     *  						  103: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, },
     *  						  TOT: {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx,}},
     *  				  		 
     *  				 TOT:  {101 : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, },
     *  						102 : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, },
     *  						103 : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, },
     *  						TOT : {qtyLtrs:xx, qtyKgs:xx, kgFat:xx, kgSnf:xx, }},
     *  			  			 
     *  		}
     *  	}
     *  unitWiseTotals : {
     *  	same as above per unit facilityId
     *  } 
     * }
     *  
     */
    public static Map<String, Object> getMilkReceiptPeriodTotals(DispatchContext ctx,  Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Timestamp fromDate = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
    	String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
		if(UtilValidate.isEmpty(fromDate)){	
			Debug.logError("fromDate cannot be empty", module);
			return ServiceUtil.returnError("fromDate cannot be empty");							
		}	    	
    	Timestamp thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
		if(UtilValidate.isEmpty(thruDate)){	
			Debug.logError("thruDate cannot be empty", module);
			return ServiceUtil.returnError("thruDate cannot be empty");							
		}	  
    	String facilityId = (String) context.get("facilityId");
    	String facilityIdTo = (String) context.get("facilityIdTo");
		if(UtilValidate.isEmpty(facilityId)){	
			Debug.logError("facilityId cannot be empty", module);
			return ServiceUtil.returnError("facilityId cannot be empty");							
		}
		GenericValue facilityDetails =null;
		String facilityTypeId = null;
		try{
			facilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
			if(UtilValidate.isNotEmpty(facilityDetails)){
				facilityTypeId = (String) facilityDetails.get("facilityTypeId");
			}else{
				return ServiceUtil.returnError("Facility Not Found with FacilityId == "+facilityId);
			}
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error while getting FacilityDetails", module);
			return ServiceUtil.returnError("Error while getting FacilityDetails=="+e.getMessage());
		}
        Boolean includeUnitTotals = (Boolean) context.get("includeUnitTotals");
        if (includeUnitTotals == null) {
        	includeUnitTotals = Boolean.FALSE;
        }		
    	List<String> facilityIds= FastList.newInstance();    	
    	List<GenericValue> orderItems= FastList.newInstance();
        Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> unitWiseTotals = FastMap.newInstance();
        if (includeUnitTotals.booleanValue()) {
        	result.put("unitWiseTotals", unitWiseTotals);
        }
        Map facilityAgents = FastMap.newInstance();
        if(facilityTypeId.equalsIgnoreCase("SHED")){
        	facilityAgents = ProcurementNetworkServices.getShedUnitsByShed(ctx, UtilMisc.toMap("shedId", facilityId)); 
        }else if(facilityTypeId.equalsIgnoreCase("UNIT")){
        	facilityIds.add(facilityId);
        }else{
        	return ServiceUtil.returnError("Facility Should be UNIT OR SHED ");
        }
        if(UtilValidate.isNotEmpty(facilityAgents)){
        	facilityIds= (List) facilityAgents.get("unitsList");
        }
        try{
            
            List<GenericValue> productCatMembers = ProcurementNetworkServices.getMilkReceiptProducts(ctx, context);
            
            List<String> productIds = EntityUtil.getFieldListFromEntityList(productCatMembers, "productId", false);
      Debug.logInfo("productIds=" + productIds, module);         		
            
            List conditionList= FastList.newInstance(); 
            //let filter out items which has value null ,receivedProductId,receivedQuantiy
            conditionList.add(EntityCondition.makeCondition("receivedQuantity", EntityOperator.NOT_EQUAL, null));  
            conditionList.add(EntityCondition.makeCondition("receivedProductId", EntityOperator.NOT_EQUAL, null));
            if(UtilValidate.isNotEmpty(facilityIdTo)){
            	conditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityIdTo));
            }/*else{
            	conditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, "MAIN_PLANT"));
            }*/
        	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "MXF_RECD"));    		
			conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
			conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds));	
        	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
Debug.logInfo("condition=" + condition,module);         		
        	orderItems = delegator.findList("MilkTransferAndMilkTransferItem", condition, null, UtilMisc.toList("receiveDate"), null, false);
Debug.logInfo("orderItems.size()=" + orderItems.size(),module);  
        	Iterator<GenericValue> itemIter = orderItems.iterator();
        	while(itemIter.hasNext()) {
                GenericValue orderItem = itemIter.next();
                Timestamp receiveDate = orderItem.getTimestamp("receiveDate");
                String facilityKey = facilityId; //orderItem.getString("originFacilityId");
                String dateKey = UtilDateTime.toDateString(receiveDate, "yyyy/MM/dd");
                Map<String, Object> dayTotalsMap;
                if( result.get(facilityKey) == null) {
    				Map<String, Object> dateMap = initDayMap(productIds);
    				dayTotalsMap = new TreeMap<String, Object>();
    				dayTotalsMap.put(dateKey, dateMap);
    				Map<String, Object> dayTotalsTOTMap = initDayMap(productIds);
    				dayTotalsMap.put("TOT", dayTotalsTOTMap);
    				Map<String, Object> newFacilityMap = FastMap.newInstance();
    				newFacilityMap.put("dayTotals", dayTotalsMap);
    				result.put(facilityKey, newFacilityMap); 
                }
                else {
                    Map facilityMap = (Map)result.get(facilityKey); 
                    dayTotalsMap = (Map)facilityMap.get("dayTotals");
                    if (dayTotalsMap.get(dateKey) == null) {
        				Map<String, Object> dateMap = initDayMap(productIds);
        				dayTotalsMap.put(dateKey, dateMap);                    	
                    }
                }
                populateDayTotalsMap(orderItem, dayTotalsMap);
                if (includeUnitTotals.booleanValue()) {
	                // next populate unitWise totals 
	                facilityKey = orderItem.getString("facilityId");
	                Map<String, Object> unitDayTotalsMap;
	                if( unitWiseTotals.get(facilityKey) == null) {
	    				Map<String, Object> dateMap = initDayMap(productIds);
	    				unitDayTotalsMap = new TreeMap<String, Object>();
	    				unitDayTotalsMap.put(dateKey, dateMap);
	    				Map<String, Object> dayTotalsTOTMap = initDayMap(productIds);
	    				unitDayTotalsMap.put("TOT", dayTotalsTOTMap);
	    				Map<String, Object> newFacilityMap = FastMap.newInstance();
	    				newFacilityMap.put("dayTotals", unitDayTotalsMap);
	    				unitWiseTotals.put(facilityKey, newFacilityMap);  
	    				result.put("unitWiseTotals", unitWiseTotals);                	
	                }
	                
	                else {
	                    Map facilityMap = (Map)unitWiseTotals.get(facilityKey); 
	                    unitDayTotalsMap = (Map)facilityMap.get("dayTotals");
	                    if (unitDayTotalsMap.get(dateKey) == null) {
	        				Map<String, Object> dateMap = initDayMap(productIds);
	        				unitDayTotalsMap.put(dateKey, dateMap);                    	
	                    }
	                }
	                populateDayTotalsMap(orderItem, unitDayTotalsMap);   
                }
        	}
    	}
        catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return result;	        
        
    }// end of the service
    /**
     * 	
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> getAllMilkReceipts(DispatchContext ctx,  Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Timestamp fromDate = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
    	Map<String,Object> result = FastMap.newInstance();
		if(UtilValidate.isEmpty(fromDate)){	
			Debug.logError("fromDate cannot be empty", module);
			return ServiceUtil.returnError("fromDate cannot be empty");							
		}	    	
    	Timestamp thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
		if(UtilValidate.isEmpty(thruDate)){	
			Debug.logError("thruDate cannot be empty", module);
			return ServiceUtil.returnError("thruDate cannot be empty");							
		}	
		Map allMilkReceiptsMap = FastMap.newInstance();
		Map allDayTotalsMap = FastMap.newInstance();
		try{
			List<String> mccTypeList = FastList.newInstance();
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"SHED"));
			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
			List<GenericValue> facilitiesList = FastList.newInstance();
			facilitiesList = delegator.findList("Facility", condition, null, null, null, false);
			
			if(UtilValidate.isEmpty(facilitiesList)){
				return result;
			}
			mccTypeList = EntityUtil.getFieldListFromEntityList(facilitiesList, "mccTypeId", false);
			Set<String> mccTypeSet = new HashSet<String>(mccTypeList);
			Map grandDayTotalsMap = FastMap.newInstance();
			for(String mccType : mccTypeSet){
				Map mccShedWiseMap = FastMap.newInstance();
				Map mccTypeDayTotalsMap = FastMap.newInstance();
				List shedsList = FastList.newInstance();
				shedsList = EntityUtil.filterByCondition(facilitiesList, EntityCondition.makeCondition("mccTypeId",EntityOperator.EQUALS,mccType));
				for(int i=0;i< shedsList.size();i++){
					GenericValue shedDetails = null;
					Map shedWiseMap = FastMap.newInstance();
					shedDetails = (GenericValue)shedsList.get(i);
					String shedId = (String) shedDetails.get("facilityId");
					Map shedResultMap = FastMap.newInstance();
					Map inMap = FastMap.newInstance();
					inMap.putAll(context);
					inMap.put("facilityId", shedId);
					inMap.put("includeUnitTotals", Boolean.TRUE);
					shedResultMap = getMilkReceiptPeriodTotals(ctx, inMap);
					if(UtilValidate.isNotEmpty(shedResultMap)){
						Map shedDayTotals = (Map)shedResultMap.get(shedId);
						if(UtilValidate.isNotEmpty(shedDayTotals)){
							Map unitWiseTotMap = FastMap.newInstance();
							unitWiseTotMap=	(Map)shedResultMap.get("unitWiseTotals");
							shedWiseMap.putAll(unitWiseTotMap);
							shedWiseMap.putAll(shedDayTotals);
						}
						
					}
					if(UtilValidate.isNotEmpty(shedWiseMap)){
					    	mccShedWiseMap.put(shedId, shedWiseMap);
					    	if(UtilValidate.isEmpty(mccTypeDayTotalsMap)){
					    		mccTypeDayTotalsMap.putAll((Map)shedWiseMap.get("dayTotals"));
					    	}else{
					    		Map shedWiseDayTotalsMap = FastMap.newInstance();
					    		shedWiseDayTotalsMap.putAll((Map)shedWiseMap.get("dayTotals"));
					    		Set<String> dateKeys = shedWiseDayTotalsMap.keySet(); 
					    		for(String dateKey : dateKeys){
					    			if(UtilValidate.isEmpty(mccTypeDayTotalsMap.get(dateKey))){
					    				mccTypeDayTotalsMap.put(dateKey,(Map)shedWiseDayTotalsMap.get(dateKey));
					    			}else{
					    				Map tempMccTypeProdctWiseMap = FastMap.newInstance();
					    				tempMccTypeProdctWiseMap.putAll((Map)mccTypeDayTotalsMap.get(dateKey));
					    				Map tempShedDayProductWiseMap = FastMap.newInstance();
					    				tempShedDayProductWiseMap.putAll((Map)shedWiseDayTotalsMap.get(dateKey));
					    				Set<String> productKeys = tempShedDayProductWiseMap.keySet();
					    				for(String productKey : productKeys){
					    					Map tempMccTypeQtyMap = FastMap.newInstance();
					    					Map tempShedProductQtyMap = FastMap.newInstance();
					    					tempMccTypeQtyMap.putAll((Map)tempMccTypeProdctWiseMap.get(productKey));
					    					tempShedProductQtyMap.putAll((Map)tempShedDayProductWiseMap.get(productKey));
					    					Set<String> qtyKeys = tempMccTypeQtyMap.keySet();
					    					for(String qtyKey : qtyKeys){
					    						BigDecimal qty= BigDecimal.ZERO;
					    						if(UtilValidate.isNotEmpty(tempMccTypeQtyMap.get(qtyKey))){
					    							qty = (BigDecimal)tempMccTypeQtyMap.get(qtyKey);
					    						}
					    						if(UtilValidate.isNotEmpty(tempShedProductQtyMap.get(qtyKey))){
					    							qty = qty.add((BigDecimal)tempShedProductQtyMap.get(qtyKey));
					    						}
					    						tempMccTypeQtyMap.put(qtyKey, qty);
					    					}
					    					
					    					tempMccTypeProdctWiseMap.put(productKey, tempMccTypeQtyMap);
					    				}
					    				mccTypeDayTotalsMap.put(dateKey, tempMccTypeProdctWiseMap);
					    			}
					    			
					    		}
					    		
					    		
					    	}
						
					}
				
				}//end of forLoop
				if(UtilValidate.isNotEmpty(mccTypeDayTotalsMap)){
					mccShedWiseMap.put("dayTotals",mccTypeDayTotalsMap);
				}
				
				
				allMilkReceiptsMap.put(mccType, mccShedWiseMap);
				
			}//end of mccTypeList forLoop
			
		}catch (Exception e) {
			// TODO: handle exception
			Debug.logError("Error While getting All Milk Receipts===========",module);
			return ServiceUtil.returnError("Error while getting all Receipts"+e.getMessage());
		}
		//Populating all dayTotals Map
		if(UtilValidate.isNotEmpty(allMilkReceiptsMap)){
			Set<String> mccTypeKeys = allMilkReceiptsMap.keySet();
			for(String mccTypeKey : mccTypeKeys){
				Map tempMccTypeMap=FastMap.newInstance();
				tempMccTypeMap.putAll((Map)allMilkReceiptsMap.get(mccTypeKey));
				if(UtilValidate.isNotEmpty(tempMccTypeMap)){
					Map tempMccDayTotals = FastMap.newInstance();
					tempMccDayTotals.putAll((Map)tempMccTypeMap.get("dayTotals"));
					if(UtilValidate.isNotEmpty(tempMccDayTotals)){
						if(UtilValidate.isEmpty(allDayTotalsMap)){
							allDayTotalsMap.putAll(tempMccDayTotals);
						}else{
							Set<String> dateKeys = tempMccDayTotals.keySet();
							for(String date : dateKeys){
								Map tempMccTypeProductWiseMap = FastMap.newInstance();
								Map tempAllDayTotProductWiseMap = FastMap.newInstance();
								
								tempMccTypeProductWiseMap.putAll((Map)tempMccDayTotals.get(date));
								if(UtilValidate.isEmpty(allDayTotalsMap.get(date))){
									allDayTotalsMap.put(date, tempMccTypeProductWiseMap);
								}else{
									tempAllDayTotProductWiseMap.putAll((Map)allDayTotalsMap.get(date));
									Set<String> productKeys = tempMccTypeProductWiseMap.keySet();
									
									for(String productKey : productKeys){
										Map tempMccQtyMap = FastMap.newInstance();
										Map tempAllDayQtyMap = FastMap.newInstance();
										tempMccQtyMap.putAll((Map)tempMccTypeProductWiseMap.get(productKey));
										tempAllDayQtyMap.putAll((Map)tempAllDayTotProductWiseMap.get(productKey));
										
										Set<String> qtyKeys = tempMccQtyMap.keySet();
										for(String qtyKey : qtyKeys){
											BigDecimal qty = BigDecimal.ZERO;
											if(UtilValidate.isNotEmpty(tempAllDayQtyMap.get(qtyKey))){
												qty = qty.add((BigDecimal)tempAllDayQtyMap.get(qtyKey));
											}
											if(UtilValidate.isNotEmpty(tempMccQtyMap.get(qtyKey))){
												qty = qty.add((BigDecimal)tempMccQtyMap.get(qtyKey));
											}
											tempAllDayQtyMap.put(qtyKey,qty);
										}
										
										tempAllDayTotProductWiseMap.put(productKey, tempAllDayQtyMap);
									}
									allDayTotalsMap.put(date, tempAllDayTotProductWiseMap);
								}
							}
							
						}
						
					}
				}
			}
		}
		allMilkReceiptsMap.put("dayTotals", allDayTotalsMap);
		
		result.put("milkReceiptsMap",allMilkReceiptsMap);
        return result;	        
        
    }// end of the service
    
    
	  	
}
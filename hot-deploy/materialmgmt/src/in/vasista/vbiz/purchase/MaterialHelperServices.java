package in.vasista.vbiz.purchase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;

public class MaterialHelperServices{
	
	public static final String module = MaterialHelperServices.class.getName();
	
	public static Map<String, Object> getCustRequestIssuancesForPeriod(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productId = (String) context.get("productId");
		String facilityId = (String) context.get("facilityId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String orgPartyId = (String) context.get("orgPartyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		try{
			condList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.BETWEEN, UtilMisc.toList(fromDate,thruDate)));
			//condList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_SUBMITTED"));
			
			 EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			List<GenericValue> custRequestStatus = delegator.findList("CustRequestStatus", cond, UtilMisc.toSet("custRequestId"),null, null, false);
			if(UtilValidate.isEmpty(custRequestStatus)){
				return result;
			}
			
			 condList.clear();
			 condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custRequestStatus, "custRequestId", true)));
			 condList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
			 if(UtilValidate.isNotEmpty(orgPartyId)){
				 condList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, orgPartyId));
			 }
			 cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 List<GenericValue> custRequestsList = delegator.findList("CustRequest", cond, UtilMisc.toSet("custRequestId","fromPartyId","custRequestDate","responseRequiredDate"),null, null, false);
			
			 condList.clear();
			 condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custRequestsList, "custRequestId", true)));
			 if(UtilValidate.isNotEmpty(productId)){
				 condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			 }		
			 //condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			 //condList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.BETWEEN, UtilMisc.toList(fromDate,thruDate)));
			 if(UtilValidate.isNotEmpty(facilityId)){
				 condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
			 }
			 
			 cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 EntityListIterator custRequestIssuesItr = null;
			 Set fieldToSelect =UtilMisc.toSet("custRequestId","custRequestItemSeqId","facilityId","issuedDateTime" ,"quantity","unitCost");
			 fieldToSelect.add("productId");
			 custRequestIssuesItr = delegator.find("ItemIssuanceAndInventoryItem", cond, null,fieldToSelect, null,null);
			 GenericValue custRequestitemIssue;
			 List itemIssuanceList =FastList.newInstance();
			 Map<String, Object> productTotals = new TreeMap<String, Object>();
			 Map<String, Object> storeTotals = new TreeMap<String, Object>();
			 while( custRequestIssuesItr != null && (custRequestitemIssue = custRequestIssuesItr.next()) != null) {
				    Map tempMap = FastMap.newInstance();
		            String custRequestId = custRequestitemIssue.getString("custRequestId");
		            String tmpProductId = custRequestitemIssue.getString("productId");
		            BigDecimal quantity  = custRequestitemIssue.getBigDecimal("quantity");
		            BigDecimal price  = custRequestitemIssue.getBigDecimal("unitCost");
		            Timestamp issuedDateTime =  custRequestitemIssue.getTimestamp("issuedDateTime");
		            String issueDate = UtilDateTime.toDateString(issuedDateTime,"yyyy-MM-dd");
		            BigDecimal amount = price.multiply(quantity);
		            //String storeId = custRequestitemIssue.getString("facilityId");
		            GenericValue custRequest = EntityUtil.getFirst(EntityUtil.filterByAnd(custRequestsList, UtilMisc.toMap("custRequestId",custRequestId)));
		            String deptId = custRequest.getString("fromPartyId");
		            tempMap.putAll(custRequest);
		            //tempMap.put("storeId", storeId);
		            tempMap.put("issuedDate", issueDate);
		            tempMap.put("deptId", deptId);
		            tempMap.put("quantity", quantity);
		            tempMap.put("amount", amount);
		            tempMap.put("price", price);
		            itemIssuanceList.add(tempMap);
		            
		         // Handle product totals   			
	    			if (productTotals.get(tmpProductId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				Map<String, Object> dayWiseMap = FastMap.newInstance();
	    				Map<String, Object> dayDetailMap = FastMap.newInstance();
	    				dayDetailMap.put("quantity", quantity);
	    				dayDetailMap.put("amount", amount);
	    				dayWiseMap.put(issueDate, dayDetailMap);
	    				newMap.put("dayWiseMap", dayWiseMap);
	    				newMap.put("quantity", quantity);
	    				newMap.put("amount", amount);
	    				productTotals.put(tmpProductId, newMap);
	    				
	    			}else {
	    				Map productMap = (Map)productTotals.get(tmpProductId);
	    				BigDecimal runningQuantity = (BigDecimal)productMap.get("quantity");
	    				runningQuantity = runningQuantity.add(quantity);
	    				productMap.put("quantity", runningQuantity);
	    				BigDecimal runningTotalAmount = (BigDecimal)productMap.get("amount");
	    				runningTotalAmount = runningTotalAmount.add(amount);
	    				productMap.put("amount", runningTotalAmount);
	    				
	    				Map dayWiseMap = (Map) productMap.get("dayWiseMap");
	    				if(dayWiseMap.get(issueDate)!= null){
	    					Map<String, Object> dayDetailMap = FastMap.newInstance();
	    					dayDetailMap = (Map<String, Object>) dayWiseMap.get(issueDate);
	    					BigDecimal runningDayQuantity = (BigDecimal)dayDetailMap.get("quantity");
	    					BigDecimal runningDayAmount = (BigDecimal)dayDetailMap.get("amount");
	    					runningDayQuantity = runningDayQuantity.add(quantity);
	    					runningDayAmount = runningDayAmount.add(amount);
		    				dayDetailMap.put("quantity", runningDayQuantity);
		    				dayDetailMap.put("amount", runningDayAmount);
		    				dayWiseMap.put(issueDate,dayDetailMap);
	        				productMap.put("dayWiseMap", dayWiseMap);
	    				}else{
	    					Map<String, Object> dayDetailMap = FastMap.newInstance();
		    				dayDetailMap.put("quantity", quantity);
		    				dayDetailMap.put("amount", amount);
		    				dayWiseMap.put(issueDate,dayDetailMap);
	        				productMap.put("dayWiseMap", dayWiseMap);
	    				}
	    				productTotals.put(tmpProductId, productMap);
	    			}
			 }       
			 custRequestIssuesItr.close();
		   result.put("itemIssuanceList", itemIssuanceList);
		   result.put("productTotals", productTotals);  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		
		return result;
	}	
	public static Map<String, Object> getOrderTaxComponentBreakUp(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String taxType = (String) context.get("taxType");
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		BigDecimal taxRate = (BigDecimal) context.get("taxRate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		Map taxComponents = FastMap.newInstance();
		if(UtilValidate.isEmpty(effectiveDate)){
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		try{
			condList.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, taxType));
			condList.add(EntityCondition.makeCondition("taxRate", EntityOperator.EQUALS, taxRate.setScale(6)));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> taxRateComponent = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
			taxRateComponent = EntityUtil.filterByDate(taxRateComponent, effectiveDate);
			for(GenericValue eachTaxRate : taxRateComponent){
				String componentType = eachTaxRate.getString("componentType");
				BigDecimal componentRate = eachTaxRate.getBigDecimal("componentRate");
				taxComponents.put(componentType, componentRate);
			}
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("taxComponents", taxComponents);
		return result;
	}
	
	public static Map<String, Object> getOrderTaxRateForComponentRate(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String taxType = (String) context.get("taxType");
		Timestamp effectiveDate = (Timestamp) context.get("effectiveDate");
		BigDecimal componentRate = (BigDecimal) context.get("componentRate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		Map taxComponents = FastMap.newInstance();
		if(UtilValidate.isEmpty(effectiveDate)){
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		BigDecimal taxRate = BigDecimal.ZERO;
		try{
			condList.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, taxType));
			condList.add(EntityCondition.makeCondition("componentRate", EntityOperator.EQUALS, componentRate.setScale(6)));
			EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
			List<GenericValue> taxRateComponent = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
			taxRateComponent = EntityUtil.filterByDate(taxRateComponent, effectiveDate);
			
			if(UtilValidate.isNotEmpty(taxRateComponent)){
				GenericValue taxRateValue = EntityUtil.getFirst(taxRateComponent);
				taxRate = taxRateValue.getBigDecimal("taxRate");
			}
			
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("taxRate", taxRate);
		return result;
	}
	
	public static Map<String, Object> getMaterialReceiptsForPeriod(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productId = (String) context.get("productId");
		String facilityId = (String) context.get("facilityId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String isForMRRReg = (String) context.get("isForMrrReg");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		try{
			condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.BETWEEN, UtilMisc.toList(fromDate,thruDate)));
			
			 if(UtilValidate.isNotEmpty(facilityId)){
				 condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
			 }
			 if(UtilValidate.isNotEmpty(productId)){
				 condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			 }
			 EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 EntityListIterator shipmentReceiptItr = null;
			 Set fieldsToSelect = UtilMisc.toSet("receiptId","facilityId","datetimeReceived" ,"quantityAccepted","unitCost");
			 fieldsToSelect.add("orderId");
			 fieldsToSelect.add("orderItemSeqId");
			 fieldsToSelect.add("productId");
			 shipmentReceiptItr = delegator.find("ShipmentReceiptAndItem", cond, null,fieldsToSelect, null,null);
			 GenericValue receiptItem;
			 List receiptsList =FastList.newInstance();
			 Map<String, Object> productTotals = new TreeMap<String, Object>();
			 Map<String, Object> storeTotals = new TreeMap<String, Object>();
			 Map<String, Object> MaterialReceiptRegisterMap = new TreeMap<String, Object>();
			 
			 while( shipmentReceiptItr != null && (receiptItem = shipmentReceiptItr.next()) != null) {
				    Map tempMap = FastMap.newInstance();
		            String receiptId = receiptItem.getString("receiptId");
		            String tmpProductId = receiptItem.getString("productId");
		            BigDecimal quantity  = receiptItem.getBigDecimal("quantityAccepted");
		            BigDecimal price  = receiptItem.getBigDecimal("unitCost");
		            Timestamp datetimeReceived =  receiptItem.getTimestamp("datetimeReceived");
		            String datetReceived = UtilDateTime.toDateString(UtilDateTime.toSqlDate(datetimeReceived));
		            BigDecimal amount = price.multiply(quantity);
		            tempMap.put("datetReceived", datetReceived);
		            tempMap.put("quantity", quantity);
		            tempMap.put("receiptId", receiptId);
		            tempMap.put("amount", amount);
		            tempMap.put("price", price);
		            receiptsList.add(tempMap);
		            if(UtilValidate.isNotEmpty(isForMRRReg) && isForMRRReg.equals("Y")){
		            	if(UtilValidate.isEmpty(MaterialReceiptRegisterMap.get(receiptId))){
		            		Map<String ,Object> receiptDetailsMap = FastMap.newInstance();
		            		String orderId = receiptItem.getString("orderId");
		            		//SUPPLIER_AGENT,ISSUE_TO_DEPT
		            		
		            		String supplierId =null;
		            		String departmentId =null;
		            		String billNo =null;
		            		String billDate =null;
		            		List<GenericValue> orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId",orderId));
		            		GenericValue supplierRole = EntityUtil.getFirst(EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId","SUPPLIER_AGENT")));
		            		GenericValue deptRole = EntityUtil.getFirst(EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId","ISSUE_TO_DEPT")));
		            		if(UtilValidate.isNotEmpty(supplierRole))
		            			supplierId = supplierRole.getString("partyId");
		            		
		            		if(UtilValidate.isNotEmpty(deptRole))
		            			departmentId = deptRole.getString("partyId");
		            		
		            		List<GenericValue> orderAttributes = delegator.findByAnd("OrderAttribute", UtilMisc.toMap("orderId",orderId));
		            		GenericValue billNoAttr = EntityUtil.getFirst(EntityUtil.filterByAnd(orderAttributes, UtilMisc.toMap("attrName","SUP_INV_NUMBER")));
		            		GenericValue billDateAttr = EntityUtil.getFirst(EntityUtil.filterByAnd(orderAttributes, UtilMisc.toMap("attrName","SUP_INV_DATE")));
		            		
		            		if(UtilValidate.isNotEmpty(billNoAttr))
		            			billNo = billNoAttr.getString("attrValue");
		            		
		            		if(UtilValidate.isNotEmpty(billDateAttr))
		            			billDate = billDateAttr.getString("attrValue");
		            		
		            		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), false);
		            		BigDecimal orderTotal = orderHeader.getBigDecimal("grandTotal");
		            		
		            		receiptDetailsMap.put("receiptId", receiptId);
		            		receiptDetailsMap.put("datetimeReceived", datetimeReceived);
		            		receiptDetailsMap.put("departmentId", departmentId);
		            		receiptDetailsMap.put("supplierId", supplierId);
		            		receiptDetailsMap.put("billNo", billNo);
		            		receiptDetailsMap.put("billDate", billDate);
		            		receiptDetailsMap.put("amount", orderTotal);
		            		MaterialReceiptRegisterMap.put(receiptId, receiptDetailsMap);
		            	}
		            	
		            }
		         // Handle product totals   			
	    			if (productTotals.get(tmpProductId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				Map<String, Object> dayWiseMap = FastMap.newInstance();
	    				Map<String, Object> dayDetailMap = FastMap.newInstance();
	    				dayDetailMap.put("quantity", quantity);
	    				dayDetailMap.put("amount", amount);
	    				dayWiseMap.put(datetReceived, dayDetailMap);
	    				newMap.put("dayWiseMap", dayWiseMap);
	    				newMap.put("quantity", quantity);
	    				newMap.put("amount", amount);
	    				productTotals.put(tmpProductId, newMap);
	    				
	    			}else {
	    				Map productMap = (Map)productTotals.get(tmpProductId);
	    				BigDecimal runningQuantity = (BigDecimal)productMap.get("quantity");
	    				runningQuantity = runningQuantity.add(quantity);
	    				productMap.put("quantity", runningQuantity);
	    				BigDecimal runningTotalAmount = (BigDecimal)productMap.get("amount");
	    				runningTotalAmount = runningTotalAmount.add(amount);
	    				productMap.put("amount", runningTotalAmount);
	    				
	    				Map dayWiseMap = (Map) productMap.get("dayWiseMap");
	    				if(dayWiseMap.get(datetReceived)!= null){
	    					Map<String, Object> dayDetailMap = FastMap.newInstance();
	    					dayDetailMap = (Map<String, Object>) dayWiseMap.get(datetReceived);
	    					BigDecimal runningDayQuantity = (BigDecimal)dayDetailMap.get("quantity");
	    					BigDecimal runningDayAmount = (BigDecimal)dayDetailMap.get("amount");
	    					runningDayQuantity = runningDayQuantity.add(quantity);
	    					runningDayAmount = runningDayAmount.add(amount);
		    				dayDetailMap.put("quantity", runningDayQuantity);
		    				dayDetailMap.put("amount", runningDayAmount);
		    				dayWiseMap.put(datetReceived,dayDetailMap);
	        				productMap.put("dayWiseMap", dayWiseMap);
	    				}else{
	    					Map<String, Object> dayDetailMap = FastMap.newInstance();
		    				dayDetailMap.put("quantity", quantity);
		    				dayDetailMap.put("amount", amount);
		    				dayWiseMap.put(datetReceived,dayDetailMap);
	        				productMap.put("dayWiseMap", dayWiseMap);
	    				}
	    				productTotals.put(tmpProductId, productMap);
	    			}
			 }       
		   shipmentReceiptItr.close();
		   result.put("MaterialReceiptRegisterMap",MaterialReceiptRegisterMap);
		   result.put("receiptsList", receiptsList);
		   result.put("productTotals", productTotals);  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		
		return result;
	}
	
	public static Map<String, Object> getLastSupplyMaterialDetails(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productId = (String) context.get("productId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		Map supplyDetailMap = FastMap.newInstance();
		try{
			condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_REJECTED"));
			EntityFindOptions opts = new EntityFindOptions();
	        opts.setMaxRows(1);
	        opts.setFetchSize(1);
	        
	        List<String> orderBy = UtilMisc.toList("-datetimeReceived");

	        List<GenericValue> receipts = null;
	        try {
	        	receipts = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(condList, EntityOperator.AND), null, orderBy, opts, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        
	        if(UtilValidate.isNotEmpty(receipts)){
	        	GenericValue receipt = EntityUtil.getFirst(receipts);
	        	
	        	String orderId = receipt.getString("orderId");
	        	String supplierPartyId = "";
	        	BigDecimal supplyRate = BigDecimal.ZERO;
	        	if(UtilValidate.isNotEmpty(orderId)){
	        		condList.clear();
	        		condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	        		condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	        		EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	        		List<GenericValue> orderRoles = delegator.findList("OrderRole", cond, null, null, null, false);
	        		
	        		GenericValue orderRole = EntityUtil.getFirst(orderRoles);
	        		if(UtilValidate.isNotEmpty(orderRole)){
	        			supplierPartyId = orderRole.getString("partyId");
	        		}
	        		condList.clear();
	        		condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	        		condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	        		EntityCondition condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
	        		List<GenericValue> orderItems = delegator.findList("OrderItem", condExpr, null, null, null, false);
	        		
	        		GenericValue orderItem  = EntityUtil.getFirst(orderItems);
	        		
	        		if(UtilValidate.isNotEmpty(orderItem)){
	        			supplyRate = orderItem.getBigDecimal("unitListPrice");
	        		}
	        		
	        	}
	        	
	        	condList.clear();
        		condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
        		condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
        		EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	        	supplyDetailMap.put("supplyProduct", receipt.getString("productId"));
	        	supplyDetailMap.put("supplyQty", receipt.getBigDecimal("quantityAccepted"));
	        	supplyDetailMap.put("supplyDate", receipt.getTimestamp("datetimeReceived"));
	        	supplyDetailMap.put("supplierPartyId", supplierPartyId);
	        	supplyDetailMap.put("supplyRate", supplyRate);
	        	
	        }
			  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		result.put("productSupplyDetails", supplyDetailMap);
		return result;
	}
	
	public static Map<String, Object> getCustRequestIndentsForPeriod(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productId = (String) context.get("productId");
		//String facilityId = (String) context.get("facilityId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		try{
			condList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate,thruDate)));
			condList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
			 if(UtilValidate.isNotEmpty(productId)){
				 condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			 }
			 
			 EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 EntityListIterator indentListItr = null;
			 Set fieldsToSelect = UtilMisc.toSet("custRequestId","fromPartyId","custRequestDate","itemStatusId","responseRequiredDate" ,"quantity");

			 indentListItr = delegator.find("CustRequestAndItemAndAttribute", cond, null,fieldsToSelect, null,null);
			 GenericValue indentItem;
			 List indentList =FastList.newInstance();
			 Map<String, Object> productTotals = new TreeMap<String, Object>();
			 Map<String, Object> departmentTotals = new TreeMap<String, Object>();
			 
			 while( indentListItr != null && (indentItem = indentListItr.next()) != null) {
				    Map tempMap = FastMap.newInstance();
		            String custRequestId = indentItem.getString("custRequestId");
		            String tmpProductId = indentItem.getString("productId");
		            BigDecimal quantity  = indentItem.getBigDecimal("quantityAccepted");
		            String deptId  = indentItem.getString("fromPartyId");
		            Timestamp custRequestDateTime =  indentItem.getTimestamp("custRequestDate");
		            String custRequestDate = UtilDateTime.toDateString(UtilDateTime.toSqlDate(custRequestDateTime));
		            tempMap.putAll(indentItem);
		            tempMap.put("deptId", deptId);
		            indentList.add(tempMap);
		            
		         // Handle product totals   			
	    			if (productTotals.get(tmpProductId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				Map<String, Object> dayWiseMap = FastMap.newInstance();
	    				Map<String, Object> dayDetailMap = FastMap.newInstance();
	    				dayDetailMap.put("quantity", quantity);
	    				dayWiseMap.put(custRequestDate, dayDetailMap);
	    				newMap.put("dayWiseMap", dayWiseMap);
	    				newMap.put("quantity", quantity);
	    				productTotals.put(tmpProductId, newMap);
	    				
	    			}else {
	    				Map productMap = (Map)productTotals.get(tmpProductId);
	    				BigDecimal runningQuantity = (BigDecimal)productMap.get("quantity");
	    				runningQuantity = runningQuantity.add(quantity);
	    				productMap.put("quantity", runningQuantity);
	    				
	    				
	    				Map dayWiseMap = (Map) productMap.get("dayWiseMap");
	    				if(dayWiseMap.get(custRequestDate)!= null){
	    					Map<String, Object> dayDetailMap = FastMap.newInstance();
	    					dayDetailMap = (Map<String, Object>) dayWiseMap.get(custRequestDate);
	    					BigDecimal runningDayQuantity = (BigDecimal)dayDetailMap.get("quantity");
	    					runningDayQuantity = runningDayQuantity.add(quantity);
		    				dayDetailMap.put("quantity", runningDayQuantity);
		    				dayWiseMap.put(custRequestDate,dayDetailMap);
	        				productMap.put("dayWiseMap", dayWiseMap);
	    				}else{
	    					Map<String, Object> dayDetailMap = FastMap.newInstance();
		    				dayDetailMap.put("quantity", quantity);
		    				dayWiseMap.put(custRequestDate,dayDetailMap);
	        				productMap.put("dayWiseMap", dayWiseMap);
	    				}
	    				productTotals.put(tmpProductId, productMap);
	    			}
	    			//handle department totals
	    			if (departmentTotals.get(deptId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				Map<String, Object> productWiseMap = FastMap.newInstance();
	    				Map<String, Object> productDetailMap = FastMap.newInstance();
	    				productDetailMap.put("quantity", quantity);
	    				productWiseMap.put(tmpProductId, productDetailMap);
	    				newMap.put("productWiseMap", productWiseMap);
	    				newMap.put("quantity", quantity);
	    				departmentTotals.put(deptId, newMap);
	    				
	    			}else {
	    				Map deptMap = (Map)departmentTotals.get(deptId);
	    				BigDecimal runningQuantity = (BigDecimal)deptMap.get("quantity");
	    				runningQuantity = runningQuantity.add(quantity);
	    				deptMap.put("quantity", runningQuantity);
	    				
	    				Map productWiseMap = (Map) deptMap.get("productWiseMap");
	    				if(productWiseMap.get(tmpProductId)!= null){
	    					Map<String, Object> prodcutDetailMap = FastMap.newInstance();
	    					prodcutDetailMap = (Map<String, Object>) productWiseMap.get(tmpProductId);
	    					BigDecimal runningDayQuantity = (BigDecimal)prodcutDetailMap.get("quantity");
	    					runningDayQuantity = runningDayQuantity.add(quantity);
	    					prodcutDetailMap.put("quantity", runningDayQuantity);
	    					productWiseMap.put(tmpProductId,prodcutDetailMap);
	    					deptMap.put("productWiseMap", productWiseMap);
	    				}else{
	    					Map<String, Object> prodcutDetailMap = FastMap.newInstance();
	    					prodcutDetailMap.put("quantity", quantity);
	    					productWiseMap.put(custRequestDate,prodcutDetailMap);
		    				deptMap.put("productWiseMap", productWiseMap);
	    				}
	    				departmentTotals.put(tmpProductId, deptMap);
	    			}
	    			
			 }       
			 indentListItr.close();
		   result.put("indentList", indentList);
		   result.put("productTotals", productTotals);  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		
		return result;
	}	
public static Map<String, Object> getMaterialStores(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List productFacilities=FastList.newInstance();
		List storesList=FastList.newInstance();
		try{
			List<GenericValue> productFacilitiesList=delegator.findList("ProductFacility",null,null,null,null,false);
			if(UtilValidate.isNotEmpty(productFacilitiesList)){
				productFacilities=EntityUtil.getFieldListFromEntityList(productFacilitiesList, "facilityId", true);
			}
			if(UtilValidate.isNotEmpty(productFacilities)){
				for(int i=0;i<productFacilities.size();i++){
					GenericValue Facility=delegator.findOne("Facility",UtilMisc.toMap("facilityId",productFacilities.get(i)),false);
					storesList.add(Facility);
				}
			}
			result.put("storesList",storesList);
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}	
public static Map<String, Object> setReauirementStatusId(DispatchContext ctx,Map<String, ? extends Object> context) {
	
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String statusId = (String) context.get("statusId");
		String requirementId = (String) context.get("requirementId");
		Map result = ServiceUtil.returnSuccess();
		String oldStatusId = null;
		Timestamp statusDate = UtilDateTime.nowTimestamp();
		try{
			GenericValue requirements= delegator.findOne("Requirement",UtilMisc.toMap("requirementId",requirementId),false);
			if(UtilValidate.isNotEmpty(requirements)){
				oldStatusId = requirements.getString("statusId");
				if((oldStatusId).equals(statusId)){
					return ServiceUtil.returnSuccess();
				}
				Map checkStatusChange = MaterialHelperServices.checkValidChangeOrNot(ctx,UtilMisc.toMap("statusId",oldStatusId,"statusIdTo",statusId));
				if(ServiceUtil.isError(checkStatusChange)){
					return checkStatusChange;
				}
				
				GenericValue newEntity = delegator.makeValue("RequirementStatus");
				newEntity.set("requirementId", requirementId);
				newEntity.set("statusId", statusId);
				newEntity.set("statusDate", statusDate);
				newEntity.create();
			}
			
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}	
	public static Map<String, Object> checkValidChangeOrNot(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		String statusId = (String) context.get("statusId");
		String statusIdTo = (String) context.get("statusIdTo");
		Map result = ServiceUtil.returnSuccess();
		try{
			GenericValue StatusValidChange= delegator.findOne("StatusValidChange",UtilMisc.toMap("statusId",statusId,"statusIdTo",statusIdTo),false);
			if(UtilValidate.isEmpty(StatusValidChange)){
				return ServiceUtil.returnError("This is not a Valid Change.");
			}
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
	
	public static Map getProductUOM(Delegator delegator, List productIds) {
		
		Map uomLabelMap = FastMap.newInstance();
		Map productUomMap = FastMap.newInstance();
		List<Map> productUOMList = FastList.newInstance();
		List conditionList = FastList.newInstance();
		Map outCtx = FastMap.newInstance();
        try {
        	
        	List<GenericValue> uomDetails = delegator.findList("Uom", null, null, null, null, false);
        	
        	conditionList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
        	if(UtilValidate.isNotEmpty(productIds)){
        		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
        	}
        	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
            EntityListIterator eli = delegator.find("Product", condition, null, null, null, null);
            GenericValue product = null;
            List uniqueUOMs = FastList.newInstance();
            String productId = "";
            String uomId = "";
            List<GenericValue> uomDetail = FastList.newInstance();
            while ((product = eli.next()) != null) {
            	uomId = product.getString("quantityUomId");
            	if(UtilValidate.isNotEmpty(uomId)){
            		uomDetail = EntityUtil.filterByCondition(uomDetails, EntityCondition.makeCondition("uomId", EntityOperator.EQUALS, uomId));
                	GenericValue uom = EntityUtil.getFirst(uomDetail);
                	String description = "";
                	String uomTypeId = "";
                	if(UtilValidate.isNotEmpty(uom) && UtilValidate.isNotEmpty(uom.get("description"))){
                		description = uom.getString("description");
                		uomTypeId = uom.getString("uomTypeId");
                	}
                	productUomMap.put(product.getString("productId"), uomId);
                	uomLabelMap.put(uomId, description);
                	
                	Map tempMap = FastMap.newInstance();
                	tempMap.put("productId", product.getString("productId"));
                	tempMap.put("quantityUomId", uomId);
                	tempMap.put("uomTypeId", uomTypeId);
                	tempMap.put("uomDescription", description);
                	productUOMList.add(tempMap);
            	}
            	
            }
            eli.close();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        outCtx.put("uomLabel", uomLabelMap);
        outCtx.put("productUom", productUomMap);
        outCtx.put("productUomDetail", productUOMList);
		return outCtx;
	}
  public static Map<String, Object> getMaterialProducts(DispatchContext ctx,Map<String, ? extends Object> context) {
		
		Delegator delegator = ctx.getDelegator();
		
		Map result = ServiceUtil.returnSuccess();
		List<GenericValue> productList = FastList.newInstance();
		result.put("productList", productList);
		try{
			List<GenericValue> productCategoryList = delegator.findByAnd("ProductCategory", UtilMisc.toMap("productCategoryTypeId","RAW_MATERIAL"));
			List productCategoryIdsList = EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
			productList = ProductWorker.getProductsByCategoryList(delegator, productCategoryIdsList, null);
			List<String> prodIdsList = FastList.newInstance();
			List<GenericValue> uniqueProducts = FastList.newInstance();
			for(GenericValue product : productList){
				String productId = product.getString("productId");
				if(!prodIdsList.contains(productId)){
					prodIdsList.add(productId);
					uniqueProducts.add(product);
				}
			}
			
			result.put("productList", uniqueProducts);
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		return result;
	}
	
  //Get RecevidQty and RemainingQty for selected PO
  
  public static Map<String, Object> getBalanceAndReceiptQtyForPO(DispatchContext ctx,Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		//String productId = (String) context.get("productId");
		String orderId = (String) context.get("orderId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map result = ServiceUtil.returnSuccess();
		List condList=FastList.newInstance();
		 Map<String, Object> receiptItemTotals = new TreeMap<String, Object>();
		 Map<String, Object> productTotals = new TreeMap<String, Object>();
		try{
			 EntityListIterator shipmentReceiptItr = null;
			 if(orderId!=null){
				 
			 if(UtilValidate.isNotEmpty(fromDate) &&UtilValidate.isNotEmpty(thruDate))
					condList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.BETWEEN, UtilMisc.toList(fromDate,thruDate)));
		     condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			 EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 Set fieldsToSelect = UtilMisc.toSet("receiptId","facilityId","datetimeReceived" ,"quantityAccepted","unitCost");
			 fieldsToSelect.add("orderId");
			 fieldsToSelect.add("orderItemSeqId");
			 fieldsToSelect.add("productId");
			 shipmentReceiptItr = delegator.find("ShipmentReceiptAndItem", cond, null,fieldsToSelect, null,null);
			 
			 GenericValue receiptItem;
			 while( shipmentReceiptItr != null && (receiptItem = shipmentReceiptItr.next()) != null) {
				    Map tempMap = FastMap.newInstance();
		            String receiptId = receiptItem.getString("receiptId");
		            String tmpProductId = receiptItem.getString("productId");
		            BigDecimal quantity  = receiptItem.getBigDecimal("quantityAccepted");
		            BigDecimal price  = receiptItem.getBigDecimal("unitCost");
		            BigDecimal amount = price.multiply(quantity);
		         // Handle product totals   			
	    			if (receiptItemTotals.get(tmpProductId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				newMap.put("receivedQty", quantity);
	    				newMap.put("receivedQtyValue", amount);
	    				receiptItemTotals.put(tmpProductId, newMap);
	    			}else {
	    				Map productMap = (Map)receiptItemTotals.get(tmpProductId);
	    				BigDecimal runningQuantity = (BigDecimal)productMap.get("receivedQty");
	    				runningQuantity = runningQuantity.add(quantity);
	    				productMap.put("receivedQty", runningQuantity);
	    				BigDecimal runningTotalAmount = (BigDecimal)productMap.get("receivedQtyValue");
	    				runningTotalAmount = runningTotalAmount.add(amount);
	    				productMap.put("receivedQtyValue", runningTotalAmount);
	    				receiptItemTotals.put(tmpProductId, productMap);
	    				}
				 }       
			   shipmentReceiptItr.close();
			   List<GenericValue> orderItemsList = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId",orderId));
			  for(GenericValue orderItem:orderItemsList) {
				  String productId=orderItem.getString("productId");
				 
					  BigDecimal orderdQty = (BigDecimal)orderItem.getBigDecimal("quantity");
					  BigDecimal checkQty = (orderdQty.multiply(new BigDecimal(1.1))).setScale(0, BigDecimal.ROUND_CEILING);
					  BigDecimal receivedQty=BigDecimal.ZERO;
					  BigDecimal receivedQtyValue=BigDecimal.ZERO;
					  Map productMap=FastMap.newInstance();
					  if(UtilValidate.isNotEmpty(receiptItemTotals)){
						  Map productInnerMap= (Map)receiptItemTotals.get(productId);
						  if(productInnerMap!=null){
							  receivedQty=(BigDecimal)productInnerMap.get("receivedQty");
							  receivedQtyValue=(BigDecimal)productInnerMap.get("receivedQtyValue");
						  }
					  }
					 
					  BigDecimal toBeReceived=orderdQty.subtract(receivedQty);
					  productMap.put("receivedQty",receivedQty);
					  productMap.put("receivedQtyValue",receivedQtyValue);
					  productMap.put("orderedQty",orderdQty);
					  productMap.put("toBeReceivedQty",toBeReceived);
					  productMap.put("maxReceivedQty",checkQty);
					  productTotals.put(productId,productMap);
				  }
			  }
			  
		}catch(Exception e){
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError(e.toString());
		}
		 result.put("productTotals",productTotals);
		 result.put("receiptItemTotals", receiptItemTotals);  
		return result;
	}
  	
  public static Map<String, Object> getDivisionDepartments(DispatchContext dctx, Map<String, ? extends Object> context) {
  	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();    	
      GenericValue userLogin = (GenericValue) context.get("userLogin");
      Timestamp fromDate =  (Timestamp)context.get("fromDate");
      Timestamp thruDate = (Timestamp)context.get("thruDate");
      String partyIdFrom = (String) context.get("partyIdFrom");
      List partyRelationshipAndDetailList = FastList.newInstance();
      List partyIds = FastList.newInstance();
      Security security = dctx.getSecurity();
      Map result = ServiceUtil.returnSuccess();
      try {
    	  List conditionList = FastList.newInstance();
    	  conditionList.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PARTY_GROUP"));
    	  conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
    	  if(UtilValidate.isNotEmpty(partyIdFrom)){
    		  conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
    	  }
    	  conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "DIVISION"));
    	  conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "SUB_DIVISION"));
    	  conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));	
    	  if(UtilValidate.isNotEmpty(thruDate)){
    		  conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
    	  }
    	  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
    	  partyRelationshipAndDetailList = delegator.findList("PartyRelationshipAndDetail", condition, null, null, null, false);
    	  if(UtilValidate.isNotEmpty(partyRelationshipAndDetailList)){
    		   partyIds = EntityUtil.getFieldListFromEntityList(partyRelationshipAndDetailList, "partyId", true);
    	  }
      }catch(GenericEntityException e){
		Debug.logError("Error fetching employments " + e.getMessage(), module);
      }
  		result.put("subDivisionDepartmentList", partyRelationshipAndDetailList);
  		result.put("subDivisionPartyIds", partyIds);
  		return result;
  } 
  public static  Map<String, Object> getUnitPriceAndQuantity(DispatchContext dctx, Map context) {
	 	GenericValue userLogin = (GenericValue) context.get("userLogin");
	 	Date date =  (Date)context.get("date");
	 	Timestamp dateTime = (Timestamp)context.get("dateTime");
	 	String custRequestId = (String)context.get("custRequestId");
	 	String custRequestItemSeqId = (String)context.get("custRequestItemSeqId");
        Delegator delegator = dctx.getDelegator();
        Timestamp issuedDateTime=null;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalUnitPrice = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(dateTime)){
        	issuedDateTime = UtilDateTime.getDayEnd(dateTime);
        }else if(UtilValidate.isNotEmpty(date)){
        	issuedDateTime = UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(date));
        }else{
        	issuedDateTime = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
        }
      Map result = ServiceUtil.returnSuccess();
      try {
    	  BigDecimal quantity=BigDecimal.ZERO;
    	  List conditionList = FastList.newInstance();
    	  conditionList.add(EntityCondition.makeCondition("custRequestItemSeqId",EntityOperator.EQUALS,custRequestItemSeqId));
    	  conditionList.add(EntityCondition.makeCondition("issuedDateTime",EntityOperator.LESS_THAN_EQUAL_TO,issuedDateTime));
    	  conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId));
    	  EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND); 
    	  List<GenericValue> itemIssuances=delegator.findList("ItemIssuance",condition,null,null,null,false);
			if(UtilValidate.isNotEmpty(itemIssuances)){
				for(GenericValue issuedItem:itemIssuances){
					GenericValue inventoryItem = delegator.findOne("InventoryItem",UtilMisc.toMap("inventoryItemId",issuedItem.get("inventoryItemId")),false);
					totalValue = totalValue.add(((BigDecimal)inventoryItem.get("unitCost")).multiply((BigDecimal)issuedItem.get("quantity")));
					totalUnitPrice = totalUnitPrice.add((BigDecimal)inventoryItem.get("unitCost"));
					quantity = quantity.add(issuedItem.getBigDecimal("quantity"));
					if(UtilValidate.isNotEmpty(issuedItem.getBigDecimal("cancelQuantity"))){
						quantity = quantity.subtract(issuedItem.getBigDecimal("cancelQuantity"));
					}
					totalQty = totalQty.add(quantity);
				}
				totalUnitPrice = totalUnitPrice.divide(new BigDecimal((itemIssuances.size())));
			}
			result.put("totalUnitPrice",totalUnitPrice);
			result.put("totalQty",totalQty);
			result.put("totalValue",totalValue);
      }
      catch (GenericEntityException e) {
          Debug.logError(e, "Error While getting the Quantity and UnitPrice.!");
      }        
      return result;
}
}


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
			 
			 cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 List<GenericValue> custRequestsList = delegator.findList("CustRequest", cond, UtilMisc.toSet("custRequestId","fromPartyId","custRequestDate","responseRequiredDate"),null, null, false);
			
			 condList.clear();
			 condList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(custRequestsList, "custRequestId", true)));
			 condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			 //condList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.BETWEEN, UtilMisc.toList(fromDate,thruDate)));
			 if(UtilValidate.isNotEmpty(facilityId)){
				 condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
			 }
			 
			 cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 EntityListIterator custRequestIssuesItr = null;
			 custRequestIssuesItr = delegator.find("ItemIssuanceAndInventoryItem", cond, null,UtilMisc.toSet("custRequestId","custRequestItemSeqId","facilityId","issuedDateTime" ,"quantity","unitCost"), null,null);
			 GenericValue custRequestitemIssue;
			 List itemIssuanceList =FastList.newInstance();
			 Map<String, Object> productTotals = new TreeMap<String, Object>();
			 Map<String, Object> storeTotals = new TreeMap<String, Object>();
			 while( custRequestIssuesItr != null && (custRequestitemIssue = custRequestIssuesItr.next()) != null) {
				    Map tempMap = FastMap.newInstance();
		            String custRequestId = custRequestitemIssue.getString("custRequestId");
		            
		            BigDecimal quantity  = custRequestitemIssue.getBigDecimal("quantity");
		            BigDecimal price  = custRequestitemIssue.getBigDecimal("unitCost");
		            Timestamp issuedDateTime =  custRequestitemIssue.getTimestamp("issuedDateTime");
		            String issueDate = UtilDateTime.toDateString(UtilDateTime.toSqlDate(issuedDateTime));
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
	    			if (productTotals.get(productId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				Map<String, Object> dayWiseMap = FastMap.newInstance();
	    				Map<String, Object> dayDetailMap = FastMap.newInstance();
	    				dayDetailMap.put("quantity", quantity);
	    				dayDetailMap.put("amount", amount);
	    				dayWiseMap.put(issueDate, dayDetailMap);
	    				newMap.put("dayWiseMap", dayWiseMap);
	    				newMap.put("quantity", quantity);
	    				newMap.put("amount", amount);
	    				productTotals.put(productId, newMap);
	    				
	    			}else {
	    				Map productMap = (Map)productTotals.get(productId);
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
	        				productMap.put("dayWiseMap", dayDetailMap);
	    				}else{
	    					Map<String, Object> dayDetailMap = FastMap.newInstance();
		    				dayDetailMap.put("quantity", quantity);
		    				dayDetailMap.put("amount", amount);
		    				dayWiseMap.put(issueDate,dayDetailMap);
	        				productMap.put("dayWiseMap", dayWiseMap);
	    				}
	    				productTotals.put(productId, productMap);
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
	public static Map<String, Object> getCustRequestReceiptsForPeriod(DispatchContext ctx,Map<String, ? extends Object> context) {
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
			 
			 EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
			 EntityListIterator shipmentReceiptItr = null;
			 Set fieldsToSelect = UtilMisc.toSet("receiptId","facilityId","datetimeReceived" ,"quantityAccepted","unitCost");
			 fieldsToSelect.add("orderId");
			 fieldsToSelect.add("orderItemSeqId");
			 shipmentReceiptItr = delegator.find("ShipmentReceiptAndItem", cond, null,fieldsToSelect, null,null);
			 GenericValue receiptItem;
			 List receiptsList =FastList.newInstance();
			 Map<String, Object> productTotals = new TreeMap<String, Object>();
			 Map<String, Object> storeTotals = new TreeMap<String, Object>();
			 Map<String, Object> MaterialReceiptRegisterMap = new TreeMap<String, Object>();
			 
			 while( shipmentReceiptItr != null && (receiptItem = shipmentReceiptItr.next()) != null) {
				    Map tempMap = FastMap.newInstance();
		            String receiptId = receiptItem.getString("receiptId");
		            
		            BigDecimal quantity  = receiptItem.getBigDecimal("quantityAccepted");
		            BigDecimal price  = receiptItem.getBigDecimal("unitCost");
		            Timestamp datetimeReceived =  receiptItem.getTimestamp("datetimeReceived");
		            String datetReceived = UtilDateTime.toDateString(UtilDateTime.toSqlDate(datetimeReceived));
		            BigDecimal amount = price.multiply(quantity);
		            tempMap.put("datetReceived", datetReceived);
		            tempMap.put("quantity", quantity);
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
	    			if (productTotals.get(productId) == null) {
	    				Map<String, Object> newMap = FastMap.newInstance();
	    				Map<String, Object> dayWiseMap = FastMap.newInstance();
	    				Map<String, Object> dayDetailMap = FastMap.newInstance();
	    				dayDetailMap.put("quantity", quantity);
	    				dayDetailMap.put("amount", amount);
	    				dayWiseMap.put(datetReceived, dayDetailMap);
	    				newMap.put("dayWiseMap", dayWiseMap);
	    				newMap.put("quantity", quantity);
	    				newMap.put("amount", amount);
	    				productTotals.put(productId, newMap);
	    				
	    			}else {
	    				Map productMap = (Map)productTotals.get(productId);
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
	    				productTotals.put(productId, productMap);
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
	
}


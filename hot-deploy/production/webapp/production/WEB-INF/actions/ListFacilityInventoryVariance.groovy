import java.sql.*
import java.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.entity.util.EntityUtil;

facilityId = parameters.facilityId;
productId = parameters.productId;

facilityInventoryList = [];
productBatchMap = [:];
JSONObject inventoryQtyJSON = new JSONObject();
if(facilityId){

	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
	if(productId){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
	conditionList.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	inventoryItemDetails = delegator.findList("InventoryItem", condition, null, null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(inventoryItemDetails, "productId", true);
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), UtilMisc.toSet("productId", "productName", "internalName"), null, null, false);
	
	products.each {eachProd->
		
		productInventory = EntityUtil.filterByCondition(inventoryItemDetails, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		
		inventoryTransMap=[:];
		inventoryTransMap.put("facilityId", facilityId);
		inventoryTransMap.put("productId", eachProd.productId);
		inventoryTransMap.put("productName", eachProd.productName);
		atpTotal = BigDecimal.ZERO;
		tempList = [];
		productInventory.each{ prodInv ->
			tempMap = [:];
			tempMap.put("inventoryItemId", prodInv.inventoryItemId);
			tempMap.put("batchNumber", prodInv.productBatchId);
			tempMap.put("quantity", prodInv.availableToPromiseTotal);
			tempList.add(tempMap);
			atpTotal = atpTotal.add(prodInv.availableToPromiseTotal);
			inventoryQtyJSON.put(prodInv.inventoryItemId, prodInv.availableToPromiseTotal);
		}
		productBatchMap.put(eachProd.productId, tempList);
		inventoryTransMap.put("quantity", atpTotal);
		facilityInventoryList.add(inventoryTransMap);
	}
}
enumVarianceTypes = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "VARIANCE_TYPE"), null, UtilMisc.toList("sequenceId"), null,true);
enumTypeIds = EntityUtil.getFieldListFromEntityList(enumVarianceTypes, "enumId", true);

varianceReasons = delegator.findList("VarianceReason", EntityCondition.makeCondition("varianceTypeId", EntityOperator.IN, enumTypeIds), null, null, null, true);
JSONObject varianceReasonTypeJSON = new JSONObject();
enumTypeIds.each{ eachType ->
	varReasons = EntityUtil.filterByCondition(varianceReasons, EntityCondition.makeCondition("varianceTypeId", EntityOperator.EQUALS, eachType));
	JSONArray reasonsListJSON = new JSONArray();
	varReasons.each { eachReason ->
		JSONObject newObj = new JSONObject();
		newObj.put("varianceReasonId",eachReason.varianceReasonId);
		newObj.put("description", eachReason.description);
		reasonsListJSON.add(newObj);
	}
	varianceReasonTypeJSON.put(eachType, reasonsListJSON);
}
context.varianceTypes = enumVarianceTypes;
context.varianceReasonTypeJSON = varianceReasonTypeJSON;
context.facilityInventoryList = facilityInventoryList;
context.productBatchMap = productBatchMap;
context.inventoryQtyJSON = inventoryQtyJSON;

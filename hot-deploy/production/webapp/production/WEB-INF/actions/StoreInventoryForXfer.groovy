import java.sql.*
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

facilityId = parameters.facilityId;
productId = parameters.productId;

storeProductInventory = [];
if(facilityId){

	context.facilityId = facilityId;
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId))
	conditionList.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO))
	if(productId){	
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId))
	}
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	List<String> orderBy = UtilMisc.toList("productId");
	inventoryItemItr = delegator.find("InventoryItem", condition, null, null, orderBy, null);
	
	productQtyMap = [:];
	while (inventoryItem = inventoryItemItr.next()) {
		prodId = inventoryItem.productId;
		qty = inventoryItem.quantityOnHandTotal;
		if(productQtyMap.get(prodId)){
			extQty = productQtyMap.get(prodId);
			productQtyMap.put(prodId, (extQty+qty));
		}else{
			productQtyMap.put(prodId, qty);
		}
	}
	
	Iterator mapIter = productQtyMap.entrySet().iterator();
	while (mapIter.hasNext()) {
		tempMap = [:];
		Map.Entry entry = mapIter.next();
		prodId=entry.getKey();
		qty=entry.getValue();
		tempMap.put("productId", prodId);
		tempMap.put("qoh", qty);
		tempMap.put("facilityId", facilityId);
		storeProductInventory.add(tempMap)
	}
}
context.storeProductInventory = storeProductInventory;
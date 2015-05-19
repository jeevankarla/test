import java.sql.*
import java.text.SimpleDateFormat;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

storeId = parameters.facilityId;
productId = parameters.productId;

if(productId){
	
	exprList = [];
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	exprList.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
	if(storeId){
		exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, storeId));
	}
	condExpr = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodInventoryItems = delegator.findList("InventoryItem", condExpr, null, null, null, false);
	
	batchNumberList = [];
	
	prodInventoryItems.each{ eachInv ->
		tempMap = [:];
		tempMap["inventoryItemId"] = eachInv.inventoryItemId;
		tempMap["qoh"] = eachInv.quantityOnHandTotal;
		tempMap["batchNumber"] = eachInv.batchNumber;
		batchNumberList.add(tempMap);
	}
	
	product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.NOT_EQUAL, storeId));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	productFacility = delegator.findList("ProductFacility", condition, null, null, null, false);
	facilityIds = EntityUtil.getFieldListFromEntityList(productFacility, "facilityId", true);
	fromFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", storeId), false);
	toFacility = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), null, null, null, false);
	productMap = [:];
	productMap["productId"] = product.productId;
	productMap["productName"] = product.productName;
	
	fromFacilityList = [];
	fromFacMap = [:];
	fromFacMap["facilityId"] = fromFacility.facilityId;
	fromFacMap["facilityName"] = fromFacility.facilityName;
	fromFacilityList.add(fromFacMap);
	toFacilityList = [];
	toFacility.each{ eachFac ->
		toFacMap = [:];
		toFacMap["facilityId"] = eachFac.facilityId;
		toFacMap["facilityName"] = eachFac.facilityName;
		toFacilityList.add(toFacMap);
	}
	
	request.setAttribute("toFacility", toFacilityList);
	request.setAttribute("fromFacility", fromFacilityList);
	request.setAttribute("product", productMap);
	request.setAttribute("batchNumberList", batchNumberList);
	
}
return "success";
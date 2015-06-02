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
JSONObject categorySiloJSON = new JSONObject();
categoryList = [];
if(facilityId){
	condList = [];
	condList.add(EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS, facilityId));
	condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SILO"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	plantFacilities = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null, null, false);
	
	categories = EntityUtil.getFieldListFromEntityList(plantFacilities, "categoryTypeEnum", true);
	
	colorList = ["#FF0F00", "#FF6600", "#FF9E01", "#FCD202", "#F8FF01", "#B0DE09", "#04D215", "#0D8ECF", "#0D52D1", "#2A0CD0","#8A0CCF", "#EB0BF9", "#7E7C7C"];
	categories.each{ eachType ->
		categorySiloFacility = EntityUtil.filterByCondition(plantFacilities, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, eachType));
		facilityIds = EntityUtil.getFieldListFromEntityList(categorySiloFacility, "facilityId", true);
		JSONArray categorySiloData = new JSONArray();
		inventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), null, null, null, false);
		
		invProductIds = EntityUtil.getFieldListFromEntityList(inventoryItems, "productId", true);
		products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, invProductIds), UtilMisc.toSet("productId", "productName"), null, null, false);
		i = 0;
		catTypeMap = [:];
		enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", eachType), false);
		catTypeMap.put("siloType", eachType);
		catTypeMap.put("description", enumeration.description);
		categoryList.add(catTypeMap);
		facilityIds.each{ eachFacilityId ->
			JSONObject newObj = new JSONObject();
			facilityInventory = EntityUtil.filterByCondition(inventoryItems, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachFacilityId));
			facility = EntityUtil.filterByCondition(plantFacilities, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachFacilityId));
			capacity = "Undefined"
			if(facility){
				capacity = (EntityUtil.getFirst(facility)).facilitySize
			}
			prodIds = EntityUtil.getFieldListFromEntityList(facilityInventory, "productId", true);
			productId = "";
			qoh = BigDecimal.ZERO;
			productNames = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.IN, prodIds));
			
			if(prodIds){
				productId = prodIds.get(0);
				productInventory = EntityUtil.filterByCondition(facilityInventory, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				productInventory.each{ eachItem ->
					qoh = qoh.add(eachItem.quantityOnHandTotal);
				}
			}
			productName = "";
			if(productNames){
				productName = (EntityUtil.getFirst(productNames)).productName
			}
			color = colorList.getAt(i);
			newObj.put("facility", eachFacilityId);
			newObj.put("quantity", qoh);
			newObj.put("color", color);
			newObj.put("product", productId);
			newObj.putAt("productName", productName);
			newObj.put("capacity", capacity);
			categorySiloData.add(newObj);
			i++;
		}
		categorySiloJSON.put(eachType, categorySiloData);
	}
	
}
context.categorySiloJSON = categorySiloJSON;
context.categoryList = categoryList;

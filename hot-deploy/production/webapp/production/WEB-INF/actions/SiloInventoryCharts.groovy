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

siloTypes = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "SILO_CAT_TYPE"), null, null, null, false);

siloCategoryTypeIds = EntityUtil.getFieldListFromEntityList(siloTypes, "enumId", true);

siloFacilities = delegator.findList("Facility", EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN, siloCategoryTypeIds), null, UtilMisc.toList("sequenceNum"), null, false);

JSONObject categorySiloJSON = new JSONObject();
colorList = ["#FF0F00", "#FF6600", "#FF9E01", "#FCD202", "#F8FF01", "#B0DE09", "#04D215", "#0D8ECF", "#0D52D1", "#2A0CD0","#8A0CCF", "#EB0BF9", "#7E7C7C"];
siloCategoryTypeIds.each{ eachType ->
	categorySiloFacility = EntityUtil.filterByCondition(siloFacilities, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, eachType));
	facilityIds = EntityUtil.getFieldListFromEntityList(categorySiloFacility, "facilityId", true);
	JSONArray categorySiloData = new JSONArray();
	inventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), null, null, null, false);
	i = 0;
	facilityIds.each{ eachFacilityId ->
		JSONObject newObj = new JSONObject();
		facilityInventory = EntityUtil.filterByCondition(inventoryItems, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachFacilityId));
		facility = EntityUtil.filterByCondition(siloFacilities, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachFacilityId));
		capacity = "Undefined"
		if(facility){
			capacity = (EntityUtil.getFirst(facility)).facilitySize
		}
		prodIds = EntityUtil.getFieldListFromEntityList(facilityInventory, "productId", true);
		productId = "";
		qoh = BigDecimal.ZERO;
		
		if(prodIds){
			productId = prodIds.get(0);
			productInventory = EntityUtil.filterByCondition(facilityInventory, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			productInventory.each{ eachItem ->
				qoh = qoh.add(eachItem.quantityOnHandTotal);
			}
		}
		color = colorList.getAt(i);
		newObj.put("facility", eachFacilityId);
		newObj.put("quantity", qoh);
		newObj.put("color", color);
		newObj.put("product", productId);
		newObj.put("capacity", capacity);
		categorySiloData.add(newObj);
		i++;
	}
	categorySiloJSON.put(eachType, categorySiloData);
}
context.categorySiloJSON = categorySiloJSON;

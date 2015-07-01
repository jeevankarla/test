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
JSONArray facilityStorageJSON = new JSONArray();
JSONArray facilityFloorJSON = new JSONArray();
JSONArray facilityInvSummaryJSON = new JSONArray();
productInventorySummary = [:];
categoryList = [];
productsDescList = [];
if(facilityId){
	condList = [];
	condList.add(EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS, facilityId));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	plantFacilities = delegator.findList("FacilityGroupAndMemberAndFacility", cond, null, null, null, false);

	facilityGroups = EntityUtil.filterByCondition(plantFacilities, EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SILO"));

	categories = EntityUtil.getFieldListFromEntityList(facilityGroups, "facilityGroupId", true);
	colorList = ["#FF0F00", "#FF6600", "#FF9E01", "#FCD202", "#F8FF01", "#2A0CD0", "#8A0CCF", "#B0DE09", "#04D215", "#EB0BF9", "#7E7C7C", "#0D8ECF", "#0D52D1"];
	categories.each{ eachType ->
		categorySiloFacility = EntityUtil.filterByCondition(plantFacilities, EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, eachType));
		facilityIds = EntityUtil.getFieldListFromEntityList(categorySiloFacility, "facilityId", true);
		JSONArray categorySiloData = new JSONArray();
		inventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), null, null, null, false);

		invProductIds = EntityUtil.getFieldListFromEntityList(inventoryItems, "productId", true);
		products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, invProductIds), UtilMisc.toSet("productId", "productName"), null, null, false);
		productsDescList.addAll(products);
		i = 0;
		catTypeMap = [:];
		groupName = delegator.findOne("FacilityGroup", UtilMisc.toMap("facilityGroupId", eachType), false);
		catTypeMap.put("siloType", eachType);
		catTypeMap.put("description", groupName.description);
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
				if(productInventorySummary.get(productId)){
					extQty = productInventorySummary.get(productId);
					productInventorySummary.put(productId, extQty+qoh)
				}else{
					productInventorySummary.put(productId, qoh);
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

	storageGroups = EntityUtil.filterByCondition(plantFacilities, EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN, UtilMisc.toList("STORAGE", "PLANT")));
	
	storeCategories = EntityUtil.getFieldListFromEntityList(storageGroups, "facilityGroupId", true);
	storeCategories.each{ eachType ->
		categorySiloFacility = EntityUtil.filterByCondition(plantFacilities, EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, eachType));
		isFloor = false;
		if(categorySiloFacility){
			facType = (EntityUtil.getFirst(categorySiloFacility)).getString("facilityTypeId");
			if(facType && facType == "PLANT"){
				isFloor = true;
			}
		}
		facilityIds = EntityUtil.getFieldListFromEntityList(categorySiloFacility, "facilityId", true);
		inventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIds), null, null, null, false);

		invProductIds = EntityUtil.getFieldListFromEntityList(inventoryItems, "productId", true);
		products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, invProductIds), UtilMisc.toSet("productId", "productName"), null, null, false);
		productsDescList.addAll(products);
		facilityIds.each{ eachFacilityId ->
			facilityInventory = EntityUtil.filterByCondition(inventoryItems, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachFacilityId));
			facility = EntityUtil.filterByCondition(plantFacilities, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachFacilityId));
			prodIds = EntityUtil.getFieldListFromEntityList(facilityInventory, "productId", true);
			productId = "";
			qoh = BigDecimal.ZERO;
			
			prodIds.each{ eachProdId ->
				JSONObject newObj = new JSONObject();
				productNames = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProdId));
				productName = "";
				if(productNames){
					productName = (EntityUtil.getFirst(productNames)).productName;
				}
				productInventory = EntityUtil.filterByCondition(facilityInventory, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProdId));
				productInventory.each{ eachItem ->
					qoh = qoh.add(eachItem.quantityOnHandTotal);
				}
				if(productInventorySummary.get(eachProdId)){
					extQty = productInventorySummary.get(eachProdId);
					productInventorySummary.put(eachProdId, extQty+qoh)
				}else{
					productInventorySummary.put(eachProdId, qoh);
				}
				newObj.put("id", eachFacilityId+productName);
				newObj.put("facility", eachFacilityId);
				newObj.put("product", productName+"["+eachProdId+"]");
				newObj.put("quantity", qoh);
				if(isFloor){
					facilityFloorJSON.add(newObj)
				}
				else{
					facilityStorageJSON.add(newObj);
				}
			}
		}
	}
	
	Iterator mapIter = productInventorySummary.entrySet().iterator();
	while (mapIter.hasNext()) {
		Map.Entry entry = mapIter.next();
		 prodId = entry.getKey();
		 prodDesc = EntityUtil.filterByCondition(productsDescList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
		 invAvl = entry.getValue();
		 JSONObject newObj = new JSONObject();
		 newObj.put("productId", prodId);
		 newObj.put("quantity", invAvl);
		 if(prodDesc){
			 internalName = (prodDesc.get(0)).internalName;
			 productName = (prodDesc.get(0)).productName;
			 newObj.put("productName", productName);
			 newObj.put("internalName", internalName);
			 facilityInvSummaryJSON.add(newObj)
		 }
	}
}

context.facilityInvSummaryJSON = facilityInvSummaryJSON;
context.categorySiloJSON = categorySiloJSON;
context.categoryList = categoryList;
context.facilityFloorId = facilityId;
context.facilityStorageJSON = facilityStorageJSON;
context.facilityFloorJSON = facilityFloorJSON;
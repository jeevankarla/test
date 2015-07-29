import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Map;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.manufacturing.jobshopmgt.ProductionRun;

workEffortId = parameters.workEffortId;

if (workEffortId) {
	
	workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
	
	productionFloorId = workEffort.facilityId;
	
	condList = [];
	condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId", EntityOperator.EQUALS, "PRUNT_PROD_NEEDED"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	workEffortNeededProducts = delegator.findList("WorkEffortGoodStandard", cond, null, null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(workEffortNeededProducts, "productId", true);
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	conditionList = [];
	productionFloorFacility = delegator.findList("FacilityGroupAndMemberAndFacility", EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS, productionFloorId), UtilMisc.toSet("facilityId", "facilityName"), null, null, false);
	
	productionFloorFacilityIds = EntityUtil.getFieldListFromEntityList(productionFloorFacility, "facilityId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	inventoryItemAndDetail = delegator.findList("InventoryItemAndDetail", condition, null, null, null, false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, productionFloorFacilityIds));
	condExpr1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	productFacility = delegator.findList("ProductFacility", condExpr1, null, null, null, false);
	
	prodFacilityIds = EntityUtil.getFieldListFromEntityList(productFacility, "facilityId", true);
	JSONArray issuedProductItemsJSON = new JSONArray();
	displayButton = 'Y';
	rawMaterialNeededList = [];
	products.each{ eachProd ->
		
		tempMap = [:];
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO));
		cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		
		inventoryItemDetail = EntityUtil.filterByCondition(inventoryItemAndDetail, cond1);
		
		facilityId = "";
		qty = 0;
		if(inventoryItemDetail){
			facilityId = (EntityUtil.getFirst(inventoryItemDetail)).facilityId;
			inventoryItemDetail.each{ eachDetail ->
				qty = qty+((eachDetail.quantityOnHandDiff).negate());
			}
		}
		
		String uomId = eachProd.get("quantityUomId");
		uomDetails = delegator.findOne("Uom",["uomId":uomId],false);
		
		if(qty > 0){
			displayButton = 'N';
			JSONObject newObj = new JSONObject();
			newObj.put("cIssueProductId",eachProd.productId);
			newObj.put("cIssueProductName", eachProd.brandName+"["+eachProd.description+"]");
			newObj.put("issueQuantity", qty);
			newObj.put("uomDescription"," ");
			if(UtilValidate.isNotEmpty(uomDetails)){
				newObj.put("uomDescription",uomDetails.description);
			}
			newObj.put("issueFacilityId", facilityId);
			issuedProductItemsJSON.add(newObj);
		}
		tempMap.putAt("productId", eachProd.productId);
		tempMap.putAt("productName", eachProd.productName);
		rawMaterialNeededList.add(tempMap);
		
	}
	JSONArray productItemsJSON = new JSONArray();
	JSONObject productIdLabelJSON = new JSONObject();
	JSONObject productLabelIdJSON = new JSONObject();
	JSONObject productDetailJSON = new JSONObject();
	products.each{eachItem ->
		prodFacility = EntityUtil.filterByCondition(productFacility, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		
		prodFacIds = EntityUtil.getFieldListFromEntityList(prodFacility, "facilityId", true);
		facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, prodFacIds), null, null, null, false);
		JSONObject tempProdFacJSON = new JSONObject();
		JSONArray productFacilityJSON = new JSONArray();
		JSONObject facilityIdLabelJSON = new JSONObject();
		JSONObject facilityLabelIdJSON = new JSONObject();
		JSONObject facilityInventoryJSON = new JSONObject();
		facilities.each{ eachFac ->
			JSONObject newObj1 = new JSONObject();
			newObj1.put("value",eachFac.facilityId);
			newObj1.put("label", eachFac.facilityName);
			productFacilityJSON.add(newObj1);
			facilityIdLabelJSON.put(eachFac.facilityId,  eachFac.facilityName);
			facilityLabelIdJSON.put(eachFac.facilityName, eachFac.facilityId);
			inputCtx = UtilMisc.toMap("productId", eachItem.productId, "facilityId", eachFac.facilityId, "userLogin", userLogin);
			resultCtx = dispatcher.runSync("getInventoryAvailableByFacility", inputCtx);
			if (ServiceUtil.isError(resultCtx)) {
				return ServiceUtil.returnError("Problem getting inventory level of the request for product Id :"+eachItem.productId);
			}
			atp = (BigDecimal)resultCtx.get("availableToPromiseTotal");
			facilityInventoryJSON.put(eachFac.facilityId, atp);
		}
		tempProdFacJSON.put("productFacilityJSON", productFacilityJSON);
		tempProdFacJSON.put("facilityIdLabelJSON", facilityIdLabelJSON);
		tempProdFacJSON.put("facilityLabelIdJSON", facilityLabelIdJSON);
		tempProdFacJSON.put("facilityInventoryJSON", facilityInventoryJSON);
		productDetailJSON.put(eachItem.productId, tempProdFacJSON);
		
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.productId);
		newObj.put("label", eachItem.brandName+"["+eachItem.description+"]");
		productItemsJSON.add(newObj);
		productIdLabelJSON.put(eachItem.productId,eachItem.brandName+"["+eachItem.description+"]");
		productLabelIdJSON.put(eachItem.brandName+"["+eachItem.description+"]", eachItem.productId);
	}
	request.setAttribute("issuedProductItemsJSON", issuedProductItemsJSON);
	request.setAttribute("productItemsJSON", productItemsJSON);
	request.setAttribute("productIdLabelJSON", productIdLabelJSON);
	request.setAttribute("productLabelIdJSON", productLabelIdJSON);
	request.setAttribute("productDetailJSON", productDetailJSON);
	request.setAttribute("displayButton", displayButton);
	request.setAttribute("requiredMaterial",rawMaterialNeededList);
}
return "success";
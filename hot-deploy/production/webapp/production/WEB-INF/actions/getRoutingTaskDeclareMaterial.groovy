
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.manufacturing.jobshopmgt.ProductionRun;
dctx = dispatcher.getDispatchContext();

workEffortId = parameters.workEffortId;
productionRunId = parameters.productionRunId;

if (workEffortId) {
	
	workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
	
	statusId = workEffort.currentStatusId;
	condList = [];
	condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId", EntityOperator.EQUALS, "PRUNT_PROD_NEEDED"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	workEffortNeededProducts = delegator.findList("WorkEffortGoodStandard", cond, null, null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(workEffortNeededProducts, "productId", true);
	
	returnProductFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	returnToFacilityIds = EntityUtil.getFieldListFromEntityList(returnProductFacility, "facilityId", true);
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, returnToFacilityIds));
	condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN, UtilMisc.toList("PLANT", "SILO")));
	facCond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	returnFacility = delegator.findList("Facility", facCond, UtilMisc.toSet("facilityId", "facilityName"), null, null, false);
	
	JSONObject rtnToFacilityIdLabelJSON = new JSONObject();
	JSONObject rtnToFacilityLabelIdJSON = new JSONObject();
	JSONArray rtnToFacilityJSON = new JSONArray();
	returnFacility.each{ eachFac ->
		JSONObject newObj1 = new JSONObject();
		newObj1.put("value",eachFac.facilityId);
		newObj1.put("label", eachFac.facilityName);
		rtnToFacilityJSON.add(newObj1);
		rtnToFacilityIdLabelJSON.put(eachFac.facilityId,  eachFac.facilityName);
		rtnToFacilityLabelIdJSON.put(eachFac.facilityName, eachFac.facilityId);
	}
	request.setAttribute("returnToFacilityJSON", rtnToFacilityJSON);
	request.setAttribute("returnToFacilityIdLabelJSON", rtnToFacilityIdLabelJSON);
	request.setAttribute("returnToFacilityLabelIdJSON", rtnToFacilityLabelIdJSON);
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	inventoryItemAndDetail = delegator.findList("InventoryItemAndDetail", condition, null, null, null, false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	conditionList.add(EntityCondition.makeCondition("transferGroupTypeId", EntityOperator.EQUALS, "RETURN_XFER"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IXF_CANCELLED"));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	returnedIssues = delegator.findList("InventoryTransferGroupAndMemberSum", condExpr, null, null, null, false);
	
	
	JSONArray returnProductItemsJSON = new JSONArray();
	returnDispBtn = 'Y';
	products.each{ eachProd ->
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO));
		cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		
		returnableItemDetail = EntityUtil.filterByCondition(inventoryItemAndDetail, cond1);
		
		facilityId = "";
		qty = 0;
		returnedQty = 0;
		comments = "";
		if(returnableItemDetail){
			qty = ((EntityUtil.getFirst(returnableItemDetail)).quantityOnHandDiff).negate();
			facilityId = (EntityUtil.getFirst(returnableItemDetail)).facilityId;
		}
		String uomId = eachProd.get("quantityUomId");
		uomDetails = delegator.findOne("Uom",["uomId":uomId],false);
		
		if(returnedIssues){
			returnProdDetails = EntityUtil.filterByCondition(returnedIssues, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
			
			if(returnProdDetails){
				returnedQty = (EntityUtil.getFirst(returnProdDetails)).xferQtySum;
				facilityId = (EntityUtil.getFirst(returnProdDetails)).toFacilityId;
				comments = (EntityUtil.getFirst(returnProdDetails)).comments
			}
			returnDispBtn = 'N';
		}
		if(qty > 0){
			JSONObject newObj = new JSONObject();
			newObj.put("cReturnProductId",eachProd.productId);
			newObj.put("cReturnProductName", eachProd.brandName+" [ "+eachProd.description+"]");
			newObj.put("returnQuantity", "");
			if(returnedQty > 0){
				newObj.put("returnQuantity", returnedQty);
			}
			newObj.put("returnUom"," ");
			if(UtilValidate.isNotEmpty(uomDetails) ){
				newObj.put("returnUom",uomDetails.description);
			}
			newObj.put("returnFacilityId", facilityId);
			newObj.put("description", comments);
			returnProductItemsJSON.add(newObj);
		}
		
	}
	if(statusId && (statusId == "PRUN_COMPLETED")){
		returnDispBtn = 'N';
	}
	request.setAttribute("returnProductItemsJSON", returnProductItemsJSON);
	request.setAttribute("returnDisplayButton", returnDispBtn);
	/*---------------------------declaration grid------------------*/
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId", EntityOperator.EQUALS, "PRUN_PROD_DELIV"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	workEffortNeededProducts = delegator.findList("WorkEffortGoodStandard", cond, null, null, null, false);
	
	declareProductIds = EntityUtil.getFieldListFromEntityList(workEffortNeededProducts, "productId", true);
	
	productFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.IN, declareProductIds), null, null, null, false);
	
	prodConfigFacilityIds = EntityUtil.getFieldListFromEntityList(productFacility, "facilityId", true);
	
	workEffortDetail = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
	productionFloorId = workEffortDetail.facilityId;
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, prodConfigFacilityIds));
	conditionList.add(EntityCondition.makeCondition("ownerFacilityId", EntityOperator.EQUALS, productionFloorId));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	productionFloorFacility = delegator.findList("FacilityGroupAndMemberAndFacility", condExpr, UtilMisc.toSet("facilityId", "facilityName"), null, null, false);
	
	JSONObject moveToFacilityIdLabelJSON = new JSONObject();
	JSONObject moveToFacilityLabelIdJSON = new JSONObject();
	JSONArray moveToFacilityJSON = new JSONArray();
	productionFloorFacility.each{ eachFac ->
		JSONObject newObj1 = new JSONObject();
		newObj1.put("value",eachFac.facilityId);
		newObj1.put("label", eachFac.facilityName);
		moveToFacilityJSON.add(newObj1);
		moveToFacilityIdLabelJSON.put(eachFac.facilityId,  eachFac.facilityName);
		moveToFacilityLabelIdJSON.put(eachFac.facilityName, eachFac.facilityId);
	}
	request.setAttribute("moveToFacilityJSON", moveToFacilityJSON);
	request.setAttribute("moveToFacilityIdLabelJSON", moveToFacilityIdLabelJSON);
	request.setAttribute("moveToFacilityLabelIdJSON", moveToFacilityLabelIdJSON);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, declareProductIds));
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	declaredInventoryProducts = delegator.findList("InventoryItemAndDetail", condition, null, null, null, false);
	
	declareProducts = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, declareProductIds), null, null, null, false);
	declareDispBtn = "Y";
	JSONArray declareProductItemsJSON = new JSONArray();
	declareProducts.each{ eachProd ->
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		declaredMaterial = EntityUtil.filterByCondition(declaredInventoryProducts, cond1);
		
		JSONObject newObj = new JSONObject();
		newObj.put("cDeclareProductId",eachProd.productId);
		newObj.put("cDeclareProductName", eachProd.brandName+" [ "+eachProd.description+"]");
		newObj.put("declareQuantity", "");
		
		String uomId = eachProd.get("quantityUomId");
		uomDetails = delegator.findOne("Uom",["uomId":uomId],false);
		
		if(UtilValidate.isNotEmpty(uomDetails) ){
			newObj.put("declareUom",uomDetails.description);
		}else{
			newObj.put("declareUom"," ");
		}
		if(declaredMaterial){
			qty = ((EntityUtil.getFirst(declaredMaterial)).quantityOnHandDiff);
			toFacilityId = (EntityUtil.getFirst(declaredMaterial)).facilityId;
			newObj.put("declareQuantity", qty);
			newObj.put("toFacilityId", toFacilityId);
			declareDispBtn = "N";
		}
		declareProductItemsJSON.add(newObj);
	}
	JSONObject conversionJSON = new JSONObject();
    conversionResult = ByProductNetworkServices.getProductQtyConversions(dctx, UtilMisc.toMap("productList", declareProducts, "userLogin", userLogin));
	if(conversionResult){
		conversionMap = conversionResult.get("productConversionDetails");
		if(conversionMap){
			Iterator prodConvIter = conversionMap.entrySet().iterator();
			while (prodConvIter.hasNext()) {
				Map.Entry entry = prodConvIter.next();
				productId = entry.getKey();
				convDetail = entry.getValue();
				
				Iterator detailIter = convDetail.entrySet().iterator();
				JSONObject conversionDetailJSON = new JSONObject();
				while (detailIter.hasNext()) {
					Map.Entry entry1 = detailIter.next();
					attrName = entry1.getKey();
					attrValue = entry1.getValue();
					conversionDetailJSON.put(attrName,attrValue);
				}
				conversionJSON.put(productId, conversionDetailJSON);
			}
		}
	}
	context.conversionJSON = conversionJSON;
	
	if(statusId && (statusId == "PRUN_COMPLETED")){
		declareDispBtn = 'N';
	}
	request.setAttribute("declareProductItemsJSON", declareProductItemsJSON);
	request.setAttribute("declareDisplayButton", declareDispBtn);
	request.setAttribute("conversionJSON", conversionJSON);
	
}
return "success";

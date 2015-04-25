
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

workEffortId = parameters.workEffortId;
productionRunId = parameters.productionRunId;

if (workEffortId) {
	
	workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
	
	condList = [];
	condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId", EntityOperator.EQUALS, "PRUNT_PROD_NEEDED"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	workEffortNeededProducts = delegator.findList("WorkEffortGoodStandard", cond, null, null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(workEffortNeededProducts, "productId", true);
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	inventoryItemAndDetail = delegator.findList("InventoryItemAndDetail", condition, null, null, null, false);
	
	JSONArray returnProductItemsJSON = new JSONArray();
	returnDispBtn = 'N';
	products.each{ eachProd ->
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
		cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		returnedMaterial = EntityUtil.filterByCondition(inventoryItemAndDetail, cond1);
		
		if(!returnedMaterial){
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
			conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO));
			cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			returnedMaterial = EntityUtil.filterByCondition(inventoryItemAndDetail, cond1);
			returnDispBtn = 'Y';
		}
		
		qty = 0;
		if(returnedMaterial){
			qty = ((EntityUtil.getFirst(returnedMaterial)).quantityOnHandDiff);
			facilityId = (EntityUtil.getFirst(returnedMaterial)).facilityId;
			JSONObject newObj = new JSONObject();
			newObj.put("cReturnProductId",eachProd.productId);
			newObj.put("cReturnProductName", eachProd.brandName+" [ "+eachProd.description+"]");
			newObj.put("returnQuantity", qty.negate());
			if(returnDispBtn == 'N'){
				newObj.put("returnQuantity", qty);
			}
			newObj.put("returnFacilityId", facilityId);
			returnProductItemsJSON.add(newObj);
		} 	
	
		
	}
	request.setAttribute("returnProductItemsJSON", returnProductItemsJSON);
	request.setAttribute("returnDisplayButton", returnDispBtn);
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	condList.add(EntityCondition.makeCondition("workEffortGoodStdTypeId", EntityOperator.EQUALS, "PRUN_PROD_DELIV"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	workEffortNeededProducts = delegator.findList("WorkEffortGoodStandard", cond, null, null, null, false);
	
	declareProductIds = EntityUtil.getFieldListFromEntityList(workEffortNeededProducts, "productId", true);
	
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
		if(declaredMaterial){
			qty = ((EntityUtil.getFirst(declaredMaterial)).quantityOnHandDiff);
			newObj.put("declareQuantity", qty);
			declareDispBtn = "N";
		}
		declareProductItemsJSON.add(newObj);
	}
	request.setAttribute("declareProductItemsJSON", declareProductItemsJSON);
	request.setAttribute("declareDisplayButton", declareDispBtn);
	Debug.log("declareProductItemsJSON #######"+declareProductItemsJSON);
}
return "success";


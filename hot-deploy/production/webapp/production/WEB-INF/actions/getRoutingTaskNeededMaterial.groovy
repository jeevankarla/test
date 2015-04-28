
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
Debug.log("workEffortId ###############"+workEffortId);

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
	productFacility = delegator.findList("ProductFacility", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	JSONArray issueProductItemsJSON = new JSONArray();
	displayButton = 'Y';
	products.each{ eachProd ->
		
		
		prodFacility = EntityUtil.filterByCondition(productFacility, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		facilityId = "";
		if(prodFacility){
			facilityId = (EntityUtil.getFirst(prodFacility)).facilityId;
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO));
		cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		
		inventoryItemDetail = EntityUtil.filterByCondition(inventoryItemAndDetail, cond1);
		
		qty = 0;
		if(inventoryItemDetail){
			qty = ((EntityUtil.getFirst(inventoryItemDetail)).quantityOnHandDiff).negate();
		} 	
	
		JSONObject newObj = new JSONObject();
		newObj.put("cIssueProductId",eachProd.productId);
		newObj.put("cIssueProductName", eachProd.brandName+" [ "+eachProd.description+"]");
		newObj.put("issueQuantity", "");
		if(qty > 0){
			displayButton = 'N';
			newObj.put("issueQuantity", qty);
		}
		newObj.put("issueFacilityId", facilityId);
		issueProductItemsJSON.add(newObj);
	}
	request.setAttribute("issueProductItemsJSON", issueProductItemsJSON);
	request.setAttribute("displayButton", displayButton);

}
return "success";


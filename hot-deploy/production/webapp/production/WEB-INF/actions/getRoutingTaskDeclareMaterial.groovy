
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
	displayButton = 'Y';
	products.each{ eachProd ->
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd.productId));
		cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		
		inventoryItemDetail = EntityUtil.filterByCondition(inventoryItemAndDetail, cond1);
		
		qty = 0;
		if(inventoryItemDetail){
			qty = ((EntityUtil.getFirst(inventoryItemDetail)).quantityOnHandDiff);
			facilityId = (EntityUtil.getFirst(inventoryItemDetail)).facilityId;
			if(qty <= 0){
				JSONObject newObj = new JSONObject();
				newObj.put("cReturnProductId",eachProd.productId);
				newObj.put("cReturnProductName", eachProd.brandName+" [ "+eachProd.description+"]");
				newObj.put("returnQuantity", qty.negate());
				newObj.put("returnFacilityId", facilityId);
				returnProductItemsJSON.add(newObj);
				displayButton = 'N';
			}
			
		} 	
	
		
	}
	request.setAttribute("returnProductItemsJSON", returnProductItemsJSON);
	request.setAttribute("displayButton", displayButton);

}
return "success";


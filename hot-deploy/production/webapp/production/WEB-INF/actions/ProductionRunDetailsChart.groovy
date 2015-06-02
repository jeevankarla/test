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
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		context.froDate = parameters.fromDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		froDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		context.froDate = UtilDateTime.toDateString(froDate, "MMMM dd, yyyy");
		fromDate = froDate;
	}
	if (parameters.thruDate) {
		context.toDate = parameters.thruDate;
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		context.toDate = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "MMMM dd, yyyy");
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.facId = facilityId;
conditionList = [];
conditionList.add(EntityCondition.makeCondition("actualCompletionDate",EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
conditionList.add(EntityCondition.makeCondition("actualCompletionDate",EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
conditionList.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS, "PRUN_COMPLETED"));
conditionList.add(EntityCondition.makeCondition("workEffortPurposeTypeId",EntityOperator.EQUALS, "WEPT_PRODUCTION_RUN"));
if(facilityId){
	conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
}
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

workEffort = delegator.findList("WorkEffort", condition, null, null, null, false);
workEffortIds = EntityUtil.getFieldListFromEntityList(workEffort, "workEffortId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("workEffortParentId",EntityOperator.IN, workEffortIds));
conditionList.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS, "PRUN_COMPLETED"));
cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
routingTasks = delegator.findList("WorkEffort", cond1, null, null, null, false);

routingIds = EntityUtil.getFieldListFromEntityList(routingTasks, "workEffortId", true);

workEffortIds.addAll(routingIds);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("workEffortId",EntityOperator.IN, workEffortIds));
conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff",EntityOperator.GREATER_THAN, BigDecimal.ZERO));
cond2 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
productionDetails = delegator.findList("InventoryItemAndDetail", cond2, null, null, null, false);

productIds = EntityUtil.getFieldListFromEntityList(productionDetails, "productId", true);

products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), UtilMisc.toSet("productId", "productName"), null, null, false);


JSONArray productionRunData = new JSONArray();
productIds.each{ eachProdId ->
	JSONObject newObj = new JSONObject();
	prodDetails = EntityUtil.filterByCondition(productionDetails, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProdId));
	prodName = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProdId));
	qoh = BigDecimal.ZERO;
	prodDetails.each{ eachDetail ->
		qoh = qoh.add(eachDetail.quantityOnHandDiff);
	}
	productName = "";
	if(prodName){
		productName = (EntityUtil.getFirst(prodName)).productName;
	}
	newObj.put("product", eachProdId);
	newObj.put("productName", productName);
	newObj.put("quantity", qoh);
	productionRunData.add(newObj);
}
context.productionRunData = productionRunData;
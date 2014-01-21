
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
import  org.ofbiz.network.NetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

if(parameters.productId){
	parameters.productId = parameters.productId.toUpperCase();
}
productId = parameters.productId;
if (!productId) {
	Debug.logInfo("ProductId does not exist!!","");
	context.errorMessage = "ProductId does not exist!!";
	return;
}
subscriptionTypeId = parameters.subscriptionTypeId;
routeId = parameters.routeId;

effectiveDate = parameters.effectiveDate;
subscriptionProductList = [];
SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
try {
	effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effDate, "");
}

effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
exprList = [];
routeValue = null;
if(routeId){
	
	routeIdsList = ByProductServices.getByproductRoutes(delegator).get("routeIdsList");
	if(!routeIdsList.contains(routeId)){
		Debug.logInfo("Route does not exist!","");
		context.errorMessage = "Route does not exist!";
		return;
	}
	route = delegator.findOne("Facility", UtilMisc.toMap("facilityId": routeId), false);
	if(route){
		context.route = route;
	}
}

if(productId){
	product = delegator.findOne("Product", ["productId" : productId],false);
	context.product = product;
	if(UtilValidate.isEmpty(product)){
		Debug.logInfo("ProductId '["+productId+"]'does not exist!!","");
		context.errorMessage = "ProductId '["+productId+"]'does not exist!!";
		return;
	}
}
effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
conditionList = [];
lastIndentDate = null;
subscriptionId = null;
indentAdjustList = [];
todaySubProdList = [];
lastSubProdList = [];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
/*conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));*/
if(parameters.routeId){
	conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, parameters.routeId));
}
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, effDateDayBegin));
conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, effDateDayEnd));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
subscriptionProductList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, null, null, false);
if(subscriptionProductList){
	subscriptionProductList.each{eachItem ->
		productId = eachItem.getAt("productId");
		quantity = eachItem.getAt("quantity");
		boothId = eachItem.getAt("facilityId");
		fromDate = eachItem.getAt("fromDate");
		productSubscriptionTypeId = eachItem.getAt("productSubscriptionTypeId");
		seqNum = eachItem.getAt("sequenceNum");
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
		conditionList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, seqNum));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effDateDayBegin));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		subscriptionList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null, ["-fromDate"], null, false);
		tempDate = effDateDayBegin;
		if(subscriptionList){
			subscriptionList.each{eachListItem ->
				if(tempDate.compareTo(eachListItem.fromDate)== 0){
					todaySubProdList.addAll(eachListItem);
				}
				else if(UtilValidate.isEmpty(lastSubProdList)){
					lastSubProdList.addAll(eachListItem);
					lastIndentDate = eachListItem.getAt("fromDate");
				}
				else{
					if((eachListItem.fromDate).compareTo((lastSubProdList[0].fromDate)) == 0){
						lastSubProdList.addAll(eachListItem);
					}
				}
			}
		}
	}
}
finalProdList = [];
if(todaySubProdList){
	todaySubProdList.each{eachItem ->
		ProdMap = [:];
		
		condList = [];
		condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		condList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
		condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,effDateDayBegin));
		condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,effDateDayEnd));
		condList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,eachItem.sequenceNum));
		cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
		shipmentList = delegator.findList("Shipment", cond, null , null, null, false);
		if(!shipmentList){
			ProdMap.facilityId = eachItem.facilityId;
			ProdMap.route = eachItem.sequenceNum;
			ProdMap.quantity = eachItem.quantity;
			ProdMap.productSubscriptionTypeId = eachItem.productSubscriptionTypeId;
			exprList = [];
			exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachItem.facilityId));
			exprList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, eachItem.sequenceNum));
			if(lastSubProdList){
				lastIndentQuant = EntityUtil.filterByAnd(lastSubProdList, exprList);
				if(lastIndentQuant){
					lastQuantity = lastIndentQuant.get(0).getAt("quantity");
					ProdMap.lastQuantity = lastQuantity;
				}
			}
			else{
				ProdMap.lastQuantity = "";
			}
			finalProdList.add(ProdMap);
		}
	}
}
JSONArray dataJSONList= new JSONArray();
if (finalProdList.size() > 0) {
	JSONObject quotaObj = new JSONObject();
	finalProdList.eachWithIndex {subProd, idx ->
		quotaObj.put("id",idx+1);
		quotaObj.put("title", "");
		quotaObj.put("boothId", subProd.facilityId);
		quotaObj.put("lastQuantity", subProd.lastQuantity);
		quotaObj.put("quantity", subProd.quantity);
		quotaObj.put("productSubscriptionTypeId", subProd.productSubscriptionTypeId);
		quotaObj.put("route", subProd.route);
		dataJSONList.add(quotaObj);
	}
}
else{
	Debug.logInfo("No indents with the product: ["+productId+"] to change!!","");
	context.errorMessage = "No indents with the product: ["+productId+"] to change!!";
	return;
}

if (dataJSONList.size() > 0) {
	context.dataJSON = dataJSONList.toString();
	Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
}
if(lastIndentDate){
	lastIndentDate = UtilDateTime.toDateString(lastIndentDate, "MMMM dd, yyyy");
}
context.lastIndentDate = lastIndentDate;

if (dataJSONList.size() > 0) {
	context.dataJSON = dataJSONList.toString();
	Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
}

prodList = ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
context.productList = prodList;
prodList.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label",eachItem.productId + " [" + eachItem.productName + "]");
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId, eachItem.productId + " [" + eachItem.productName + "]");
}
context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;

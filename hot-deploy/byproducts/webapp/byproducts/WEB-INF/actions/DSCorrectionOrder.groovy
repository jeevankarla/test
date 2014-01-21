
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.network.NetworkServices;
import javolution.util.FastMap;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.service.ServiceUtil;

if(parameters.boothId){
	parameters.boothId = parameters.boothId.toUpperCase();
}
boothId = (String)parameters.boothId;
exprList = [];
effDate = null;
displayGrid = true;
supplyDate = parameters.effectiveDate;
dctx = dispatcher.getDispatchContext();
SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
try {
	supplyDate = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
} catch (ParseException e) {
	displayGrid = false;
	Debug.logError(e, "Cannot parse date string: " + effDate, "");
}
productSubscriptionTypeId = parameters.productSubscriptionTypeId;
subscriptionTypeId = parameters.subscriptionTypeId;
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

boothType = null;
shipmentId = null;

dayBegin = UtilDateTime.getDayStart(supplyDate);
dayEnd = UtilDateTime.getDayEnd(supplyDate);

facility = delegator.findOne("Facility",[facilityId : parameters.boothId], false);
if (facility == null) {
	 Debug.logInfo("Party Code '" + boothId + "' does not exist!","");
	 context.errorMessage = "Party Code '" + boothId + "' does not exist!";
	 displayGrid = false;
	 return;
 }
routeId = parameters.routeId;
shipmentTypeId = parameters.shipmentTypeId
exprList = [];
exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,routeId));
exprList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
//exprList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,"BYPRODUCTS"));
conds=EntityCondition.makeCondition(exprList,EntityOperator.AND);
route = delegator.findList("Facility", conds, null , null, null, false);
if(UtilValidate.isEmpty(route)){
	Debug.logInfo(" Not a valid Route : "+routeId, "");
	context.errorMessage = "Not a valid Route : "+routeId;
	displayGrid = false;
	return ;
}
exprList.clear();
exprList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, shipmentTypeId));
exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
exprList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId));
exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
shipmentList = delegator.findList("Shipment", condition, null, null, null, false);
if(shipmentList){
	if(shipmentList.size() == 1){
		shipmentId = shipmentList[0].get("shipmentId");
	}
}
facilityTypeList = delegator.findOne("Facility", UtilMisc.toMap("facilityId" : boothId), false);
if(facilityTypeList){
	boothType = facilityTypeList.getAt("categoryTypeEnum");
}
shipmentItemsList = [];
orderProdList = [];
conditionList = [];
lastIndentDate = null;
if(shipmentId){
	if(boothType == "PARLOUR"){
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.EQUALS, dayBegin));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
		conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		shipmentItemsList = delegator.findList("ShipmentReceiptAndItem", condition, null, null, null, false);
		if(shipmentItemsList){
			lastIndentDate = shipmentItemsList[0].getAt("datetimeReceived");
		}
	}
	else{
		if(UtilValidate.isNotEmpty(shipmentId) && UtilValidate.isNotEmpty(boothId)){
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
			conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
			conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, boothId));
			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			orderProdList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, null, null, false);
			if(orderProdList){
				lastIndentDate = orderProdList[0].getAt("estimatedDeliveryDate");
			}
		}
	}
}

// now let's populate the json data structure

JSONArray dataJSONList= new JSONArray();
/*if(UtilValidate.isEmpty(shipmentItemsList) && UtilValidate.isEmpty(orderProdList))
{
	Debug.logInfo("No Orders found for correction to Party Code :" + boothId,"");
	context.errorMessage = "No Orders found for correction to Party Code '" + boothId+"'";
	return;
}*/
context.booth = facility;
if (shipmentItemsList.size() > 0) {
	JSONObject quotaObj = new JSONObject();
	shipmentItemsList.eachWithIndex {subProd, idx ->
		quotaObj.put("id",idx+1);
		quotaObj.put("title", "");
		quotaObj.put("productId", subProd.productId);
		quotaObj.put("lastQuantity", subProd.quantityAccepted);
		quotaObj.put("quantity", subProd.quantityAccepted);
		dataJSONList.add(quotaObj);
	}
}
else{
	if (orderProdList.size() > 0) {
		JSONObject quotaObj = new JSONObject();
		orderProdList.eachWithIndex {subProd, idx ->
			quotaObj.put("id",idx+1);
			quotaObj.put("title", "");
			quotaObj.put("productId", subProd.productId);
			quotaObj.put("lastQuantity", subProd.quantity);
			quotaObj.put("quantity", subProd.quantity);
			dataJSONList.add(quotaObj);
		}
	}
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
productPrices = [];
if(boothId){
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	inMap = [:];
	inMap.productStoreId = productStoreId;
	result = ByProductServices.getProdStoreProducts(dctx, inMap)
	productsList = result.productIdsList;
	
	productsList.each{ eachProd ->
		prodPrice = [:];
		priceContext = [:];
		priceResult = [:];
		Map<String, Object> priceResult;
		priceContext.put("userLogin", userLogin);
		priceContext.put("productStoreId", productStoreId);
		priceContext.put("productId", eachProd);
		priceContext.put("priceDate", dayBegin);
		priceContext.put("facilityId", boothId);
		priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
		if(!ServiceUtil.isError(priceResult)){
			if (priceResult) {
				unitCost = (BigDecimal)priceResult.get("basicPrice");
				taxList = priceResult.get("taxList");
				totalAmount = BigDecimal.ZERO;
				if(taxList){
					taxList.each{eachItem ->
						taxAmount = (BigDecimal)eachItem.get("amount");
						totalAmount = totalAmount.add(taxAmount);
					}
				}
				prodPrice.productId = eachProd;
				prodPrice.unitCost = (unitCost.add(totalAmount));
				productPrices.add(prodPrice);
			}
		}
	}
}
JSONObject productCostJSON = new JSONObject();
productPrices.each{eachProdPrice ->
	productCostJSON.put(eachProdPrice.productId,eachProdPrice.unitCost);
}


context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productCostJSON = productCostJSON;
context.screenFlag = "DSCorrection";
if(displayGrid){
	context.partyCode = facility;
}
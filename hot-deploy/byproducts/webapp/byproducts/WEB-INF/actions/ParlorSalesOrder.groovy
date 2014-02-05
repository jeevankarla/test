
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.math.RoundingMode;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import javolution.util.FastList;
import javolution.util.FastMap;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.tax.TaxAuthorityServices;
import in.vasista.vbiz.facility.util.FacilityUtil;

rounding = RoundingMode.HALF_UP;

if(parameters.boothId){
	parameters.boothId = parameters.boothId.toUpperCase();
}
boothId = (String)parameters.boothId;
option = parameters.orderOption;
exprList = [];
/*actionFlag = "create";*/
effDate = null;
orderId = null;
supplyDate = parameters.effectiveDate;

SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
try {
	effDate = new java.sql.Timestamp(sdf.parse(supplyDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effDate, "");
}
dctx = dispatcher.getDispatchContext();
dayBegin = UtilDateTime.getDayStart(effDate);
dayEnd = UtilDateTime.getDayEnd(effDate);
conditionList = [];
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "PARLOUR"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
facilityList = delegator.findList("Facility", condition, null, null, null, false);
if(!facilityList){
	Debug.logInfo("["+boothId+"] is not a Parlor!", "");
	context.errorMessage = "["+boothId+"] is not a Parlor!";
	return ;
}

result = [:];
result = FacilityUtil.isFacilityAcitve(dctx ,[facilityId: boothId, userLogin: userLogin]);
if (ServiceUtil.isError(result)) {
	Debug.logInfo("Parlor ["+boothId+"] is not Active !", "");
	context.errorMessage = "Parlor ["+boothId+"] is not Active !";
	return;
}
facility = delegator.findOne("Facility", [facilityId : boothId],false);
if(facility){
	context.booth = facility;
}
if(option == "update"){
	exprList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
	exprList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, boothId));
	exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	exprList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	exprList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	orderList = delegator.findList("OrderHeader", condition, null, ["-lastUpdatedStamp"], null, false);
	
	JSONArray dataJSONList= new JSONArray();
	if (orderList.size() > 0) {
		JSONObject quotaObj = new JSONObject();
		orderId = orderList[0].get("orderId");
		orderProdList = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, ["-orderItemSeqId"],null,false);
		if(orderProdList){
			orderProdList.eachWithIndex {subProd, idx ->
				quotaObj.put("id",idx+1);
				quotaObj.put("title", "");
				quotaObj.put("productId", subProd.productId);
				quotaObj.put("quantity", subProd.quantity);
				dataJSONList.add(quotaObj);
			}
		}
	}
	context.orderId = orderId;
	if (dataJSONList.size() > 0) {
		context.dataJSON = dataJSONList.toString();
		Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
	}
}

facilityList = [];
productList = [];
if(parameters.productId){
	productList.add(parameters.productId);
}
else{
	productList =ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	/*productList = EntityUtil.filterByCondition(productList,EntityCondition.makeCondition("requireInventory",EntityOperator.EQUALS,null));*/
	productList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
}
todayDate = UtilDateTime.nowTimestamp();
paramDate = effDate;
storeId = parameters.boothId;
inventoryFinalList = [];
condList = [];
condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, boothId));
condList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
condList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "PARLOR_SALES_CHANNEL"));
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
orderHeader = delegator.findList("OrderHeader", cond, null, ["-lastUpdatedStamp"], null, false);
if(productList){
	productList.each{eachprod ->
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachprod));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, storeId));
		conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		inventorySummReport = delegator.findList("InventorySummary", condition, null , ["-saleDate"], null, false);
		if(inventorySummReport){
			inventoryMap = [:];
			totalInventory = BigDecimal.ZERO;
			latestInventory = inventorySummReport.get(0);
			inventoryMap = [:];
			prodId = latestInventory.productId;
			salesQty = BigDecimal.ZERO;
			if(orderHeader){
				order = EntityUtil.getFirst(orderHeader);
				orderId = order.orderId;
				orderItem = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null,null, false);
				orderProdItem = EntityUtil.filterByCondition(orderItem, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
				if(orderProdItem){
					salesQty = orderProdItem.quantity;
				}
			}
			product = delegator.findOne("Product", UtilMisc.toMap("productId",eachprod),false);
			requireDisplay = product.requireInventory;
			quantityOnHandTotal = latestInventory.closingBalance;
			inventoryMap.putAt("productId", prodId);
			inventoryMap.putAt("closingBalance", quantityOnHandTotal);
			inventoryMap.putAt("saleQty", salesQty);
			inventoryMap.putAt("display", requireDisplay);
			inventoryFinalList.add(inventoryMap);
		}
	}
}
JSONObject inventoryCBJSON = new JSONObject();
JSONObject inventorySaleJSON = new JSONObject();
JSONObject invDisplayJSON = new JSONObject();
if(inventoryFinalList){
	inventoryFinalList.each{eachProdInv ->
		inventoryCBJSON.put(eachProdInv.productId,eachProdInv.closingBalance);
		inventorySaleJSON.put(eachProdInv.productId,eachProdInv.saleQty);
		invDisplayJSON.put(eachProdInv.productId,eachProdInv.display);
	}
}
context.inventoryCBJSON = inventoryCBJSON;
context.inventorySaleJSON = inventorySaleJSON;
context.invDisplayJSON = invDisplayJSON;
dctx = dispatcher.getDispatchContext();
productPrices = [];
if(boothId){
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	inMap = [:];
	inMap.productStoreId = productStoreId;
	result = ByProductServices.getProdStoreProducts(dctx, inMap)
	prodList = result.productIdsList;
	
	prodList.each{ eachProd ->
		prodPrice = [:];
		priceContext = [:];
		priceResult = [:];
		Map<String, Object> priceResult;
		priceContext.put("userLogin", userLogin);
		priceContext.put("productStoreId", boothId);
		priceContext.put("productId", eachProd);
		priceContext.put("priceDate", dayBegin);
		priceContext.put("productPriceTypeId", "PM_RC_P_PRICE");
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
prodList =ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
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
context.productCostJSON = productCostJSON;
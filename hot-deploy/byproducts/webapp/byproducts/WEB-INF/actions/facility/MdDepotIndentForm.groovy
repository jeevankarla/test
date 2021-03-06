
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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;

if(parameters.boothId){
	parameters.boothId = parameters.boothId.toUpperCase();
}
if(UtilValidate.isNotEmpty(parameters.productStoreIdFrom)){
	parameters.productStoreId = parameters.productStoreIdFrom;
	productStoreId=parameters.productStoreIdFrom;
}
boothId = parameters.boothId;
subscriptionTypeId = parameters.subscriptionTypeId;
productSubscriptionTypeId = parameters.productSubscriptionTypeId;
shipmentTypeId = parameters.shipmentTypeId;
dctx = dispatcher.getDispatchContext();
effectiveDate = parameters.effectiveDate;
priceTypeId=parameters.priceTypeId;
changeFlag=parameters.changeFlag;

productCatageoryId=parameters.productCatageoryId;

if(changeFlag=="IcpSales"){
	productCatageoryId="ICE_CREAM_NANDINI";
}
if(changeFlag=="IcpSalesAmul"){
	productCatageoryId="ICE_CREAM_AMUL";
}
if(changeFlag=="IcpSalesBellary"){
	productCatageoryId="ICE_CREAM_BELLARY";
}

boolean prodCatString = productCatageoryId instanceof String;

displayGrid = true;
effDateDayBegin="";
effDateDayEnd="";

SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
if(UtilValidate.isNotEmpty(effectiveDate)){
try {
	effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDate).getTime());
}catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effDate, "");
	displayGrid = false;
}
	effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
	effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
}else{
	effDateDayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	effDateDayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
}
conditionList = [];
exprList = [];
result = [:];
routeId = parameters.routeId;
partyId="";
orderTaxType = parameters.orderTaxType;
packingType = parameters.packingType;
facility = null;
prodPriceMap = [:];
if(changeFlag != "AdhocSaleNew"){
	partyId = parameters.partyId;
	party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
	roleTypeId = parameters.roleTypeId;
	partyRole = null;
	if(party){
		partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
	}
	if(!party || !partyRole){
		context.errorMessage = partyId+" incorrect for the transaction !!";
		displayGrid = false;
		return result;
	}
	Debug.log("==productCatageoryId==="+productCatageoryId);
	if(UtilValidate.isEmpty(productCatageoryId)){
		context.errorMessage = "Please Select At Least One productCatageoryId !";
		displayGrid = false;
		return result;
	}
	context.productCategoryId = parameters.productCatageoryId;
	context.party = party;
	context.orderTaxType = parameters.orderTaxType;
	context.packingType = parameters.packingType;
}else{
	facility = delegator.findOne("Facility", [facilityId : boothId],false);
	if(facility){
		context.booth = facility;
		routeId=facility.parentFacilityId;
		parameters.routeId=routeId;
	}
	if (UtilValidate.isEmpty(boothId)) {
		Debug.logInfo(boothId+" boothId Must Not Empty !", "");
		context.errorMessage = boothId+" boothId Must Not Empty !";
		displayGrid = false;
		return result;
	}
	facility = delegator.findOne("Facility",[facilityId : boothId], false);
	context.booth = facility;
	routeId=facility.parentFacilityId;
	partyId = facility.ownerPartyId;
	
}

partyPostalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null,null,null, false);
if(partyPostalAddress){
	partyPostalAddress = EntityUtil.getFirst(partyPostalAddress);
	partyAddress = partyPostalAddress.address1;
	context.partyAddress = partyAddress;
}

prodList=[];
//productCatageoryId = "INDENT";
if(prodCatString && UtilValidate.isNotEmpty(productCatageoryId) && "INDENT"==productCatageoryId){
	prodList= ProductWorker.getProductsByCategory(delegator ,"INDENT" ,null);
}else if(UtilValidate.isNotEmpty(productCatageoryId)){
//TO DO:
	if(prodCatString){
		prodList= ProductWorker.getProductsByCategory(delegator ,productCatageoryId ,null);
	}else{
		prodList= ProductWorker.getProductsByCategoryList(delegator ,productCatageoryId ,null);
	}
}

Map inputProductRate = FastMap.newInstance();
inputProductRate.put("facilityId",boothId);
inputProductRate.put("partyId",partyId);
inputProductRate.put("userLogin",userLogin);
priceResultMap = [:];
if(facility){
	inputProductRate.put("fromDate",effDateDayBegin);
	inputProductRate.put("facilityCategory",facility.categoryTypeEnum);
	inputProductRate.put("productsList",prodList);
	priceResultMap = ByProductNetworkServices.getProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
}else{
	inputProductRate.put("priceDate",effDateDayBegin);
	inputProductRate.put("productList",prodList);
	//inputProductRate.put("productCategoryId", productCatageoryId);
	Debug.log("=====orderTaxType=IN=====DepotIndentForm==="+orderTaxType);
	if(orderTaxType){
		if(orderTaxType == "INTRA"){
			inputProductRate.put("geoTax", "VAT");
		}else{
			inputProductRate.put("geoTax", "CST");
		}
	}
	if(packingType){
		inputProductRate.put("productPriceTypeId", packingType);
	}
	priceResultMap = ByProductNetworkServices.getStoreProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
}

productIds = EntityUtil.getFieldListFromEntityList(prodList, "productId", true);
Map result = (Map)MaterialHelperServices.getProductUOM(delegator, productIds);
uomLabelMap = result.get("uomLabel");
productUomMap = result.get("productUom");
prodPriceMap=[:];
prodPriceMap = (Map)priceResultMap.get("priceMap");
//listPrice
prodListPriceMap=[:];
prodListPriceMap=(Map)priceResultMap.get("prodListPriceMap");
JSONObject productTaxListJSON = new JSONObject();
productTaxListJSON=prodListPriceMap;
Debug.log("=====prodListPriceMap==="+prodListPriceMap);
conversionResult = ByProductNetworkServices.getProductQtyConversions(dctx, UtilMisc.toMap("productList", prodList, "userLogin", userLogin));
conversionMap = conversionResult.get("productConversionDetails");
if(conversionMap){
	Iterator prodConvIter = conversionMap.entrySet().iterator();
	JSONObject conversionJSON = new JSONObject();
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
	context.conversionJSON = conversionJSON;
}
JSONObject productUOMJSON = new JSONObject();
JSONObject uomLabelJSON=new JSONObject();

JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON=new JSONObject();
context.productList = prodList;
prodList.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	//newObj.put("label","[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName);
	newObj.put("label","[" +eachItem.internalName+"] " +eachItem.description+"-"+eachItem.productId);
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId, eachItem.description);
	//productLabelIdJSON.put("[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName, eachItem.productId);
	productLabelIdJSON.put("[" +eachItem.internalName+"] " +eachItem.description+"-"+eachItem.productId, eachItem.productId);
	
	if(productUomMap){
		uomId = productUomMap.get(eachItem.productId);
		if(uomId){
			productUOMJSON.put(eachItem.productId, uomId);
			uomLabelJSON.put(uomId, uomLabelMap.get(uomId));
		}
	}
	
	
}
context.productUOMJSON = productUOMJSON;
context.uomLabelJSON = uomLabelJSON;

productPrices = [];

JSONObject productCostJSON = new JSONObject();
productCostJSON=prodPriceMap;
JSONObject prodIndentQtyCat = new JSONObject();
JSONObject qtyInPieces = new JSONObject();

context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productCostJSON = productCostJSON;
context.productTaxListJSON=productTaxListJSON;
context.productLabelIdJSON = productLabelIdJSON;
if(displayGrid){
	context.partyCode = facility;
}
//adding order adjustments
orderAdjTypes = delegator.findList("OrderAdjustmentType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALE_ORDER_ADJUSTMNT"), null, null, null, false);

JSONArray orderAdjItemsJSON = new JSONArray();
JSONObject orderAdjLabelJSON = new JSONObject();
JSONObject orderAdjLabelIdJSON=new JSONObject();
orderAdjTypes.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.orderAdjustmentTypeId);
	newObj.put("label",eachItem.description +" [ " +eachItem.orderAdjustmentTypeId+"]");
	orderAdjItemsJSON.add(newObj);
	orderAdjLabelJSON.put(eachItem.orderAdjustmentTypeId, eachItem.description);
	orderAdjLabelIdJSON.put(eachItem.description +" [ " +eachItem.orderAdjustmentTypeId+"]", eachItem.orderAdjustmentTypeId);
	
}

context.orderAdjItemsJSON = orderAdjItemsJSON;
context.orderAdjLabelJSON = orderAdjLabelJSON;
context.orderAdjLabelIdJSON = orderAdjLabelIdJSON;
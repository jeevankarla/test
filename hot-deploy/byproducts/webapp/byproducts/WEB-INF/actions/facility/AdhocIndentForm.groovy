
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


if(parameters.boothId){
	parameters.boothId = parameters.boothId.toUpperCase();
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
if(changeFlag=="PowderSales"){
	productCatageoryId="MILK_POWDER";
}
if(changeFlag=="FgsSales"){
	productCatageoryId="FG_STORE";
}
if(changeFlag=="ConvCharges"){
	productCatageoryId="CON_CHG";
}
if(changeFlag=="InterUnitTransferSale"){
	productCatageoryId="INTER_UNIT";//later we should change tranferble products only
}
subscriptionProdList = [];
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
subscriptionId = null;
conditionList = [];
lastIndentDate = null;
subscriptionId = null;
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
if(UtilValidate.isNotEmpty(productCatageoryId) && "INDENT"==productCatageoryId){
	prodList= ProductWorker.getProductsByCategory(delegator ,"INDENT" ,null);
}else if(UtilValidate.isNotEmpty(productCatageoryId)){
//TO DO:
// we should not go with primaryProductCategoryId for now comment this , use product  catagory member
	/*exprList.clear();
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
	exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
	exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, productCatageoryId));
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, effDateDayBegin)));
	  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);*/
       prodList= ProductWorker.getProductsByCategory(delegator ,productCatageoryId ,null);
		//Debug.log("=====discontinuationDateCondition===="+discontinuationDateCondition);
}
else{
	prodList =ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
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
	inputProductRate.put("productCategoryId", productCatageoryId);
	if(orderTaxType){
		if(orderTaxType == "INTRA"){
			inputProductRate.put("geoTax", "VAT");
		}
		else{
			inputProductRate.put("geoTax", "CST");
		}
	}
	if(packingType){
		inputProductRate.put("productPriceTypeId", packingType);
	}
	if(changeFlag!="InterUnitTransferSale"){
	priceResultMap = ByProductNetworkServices.getStoreProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	}
}
prodPriceMap=[:];
if(changeFlag!="InterUnitTransferSale"){//for Stock Transfer price no need to populate
	prodPriceMap = (Map)priceResultMap.get("priceMap");
}

conversionResult = ByProductNetworkServices.getProductQtyConversions(dctx, UtilMisc.toMap("productCategoryId", productCatageoryId, "userLogin", userLogin));
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


JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON=new JSONObject();
context.productList = prodList;
prodList.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label",eachItem.description +" [ " +eachItem.brandName+"]");
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId, eachItem.description);
	productLabelIdJSON.put(eachItem.description+" [ "+eachItem.brandName+"]", eachItem.productId);
}
productPrices = [];

JSONObject productCostJSON = new JSONObject();
productCostJSON=prodPriceMap;

JSONObject prodIndentQtyCat = new JSONObject();
JSONObject qtyInPieces = new JSONObject();

context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productCostJSON = productCostJSON;
context.productLabelIdJSON = productLabelIdJSON;
if(displayGrid){
	context.partyCode = facility;
}

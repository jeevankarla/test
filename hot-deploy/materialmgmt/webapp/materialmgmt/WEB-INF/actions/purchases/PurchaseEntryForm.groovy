
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

import in.vasista.vbiz.purchase.PurchaseStoreServices;

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

if(changeFlag=="PurchaseOrder"){
	productCatageoryId="PACKING_PRODUCT";
}

//Debug.log("========changeFlagAFTERR NEWAPPLICATIONNNNNN=="+changeFlag+"=====in OrderINDesdff");
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
billToParty="";
billToPartyId = parameters.billToPartyId;

issueToDeptId = parameters.issueToDeptId;
facility = null;
prodPriceMap = [:];
if(changeFlag == "PurchaseOrder" || changeFlag == "InterUnitPurchase"){
	partyId = parameters.partyId;
	party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
	context.party = party;
}
if(changeFlag == "PurchaseOrder" || changeFlag == "InterUnitPurchase"){
	billToParty = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", billToPartyId), false);
	context.billToParty = billToParty;
	issueToDept = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", issueToDeptId), false);
	context.issueToDept = issueToDept;
	
}

if(UtilValidate.isNotEmpty(billToPartyId)){
	context.billToPartyId = billToPartyId;
	context.billToPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,billToPartyId, false);;
	
}
partyPostalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null,null,null, false);
if(partyPostalAddress){
	partyPostalAddress = EntityUtil.getFirst(partyPostalAddress);
	partyAddress = partyPostalAddress.address1;
	context.partyAddress = partyAddress;
}
billToPartyPostalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, billToPartyId), null,null,null, false);
if(billToPartyPostalAddress){
	billToPartyPostalAddress = EntityUtil.getFirst(billToPartyPostalAddress);
	billToAddress = billToPartyPostalAddress.address1;
	context.billToAddress = billToAddress;
}

prodList=[];

if(UtilValidate.isNotEmpty(changeFlag) && changeFlag == "PurchaseOrder"){
	exprList.clear();
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
	exprList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, effDateDayBegin)));
	  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
}
/*if(UtilValidate.isNotEmpty(productCatageoryId)){
	exprList.clear();
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
	//exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
	exprList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
	exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, productCatageoryId));
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, effDateDayBegin)));
	  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	  //Debug.log("=====EntityCondition===="+EntityCondition);
		prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
		
}*/
if(UtilValidate.isNotEmpty(changeFlag) &&( (changeFlag == "InterUnitPurchase")||(changeFlag == "PurchaseOrder"))){
	tempProdList=ProductWorker.getProductsByCategory(delegator,"PUR_WSD",null);
	Debug.log("===WSD==prodList==Size==="+tempProdList.size()+"prodList==Size==="+prodList.size());
	prodList.addAll(tempProdList);
}
Debug.log("=====prodList=Size==="+prodList.size());
productStoreId =PurchaseStoreServices.getPurchaseFactoryStore(delegator).get("factoryStoreId");
Map inputProductRate = FastMap.newInstance();
inputProductRate.put("productStoreId", "9000");
inputProductRate.put("fromDate",effDateDayBegin);
inputProductRate.put("facilityId",boothId);
inputProductRate.put("partyId",partyId);
if(facility){
	inputProductRate.put("facilityCategory",facility.categoryTypeEnum);
}
inputProductRate.put("userLogin",userLogin);
inputProductRate.put("productsList",prodList);
//Map priceResultMap =ByProductNetworkServices.getProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
//prodPriceMap = (Map)priceResultMap.get("priceMap");
prodPriceMap=[];

JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON=new JSONObject();
context.productList = prodList;
prodList.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label",eachItem.brandName +" [ " +eachItem.description+"]");
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId, eachItem.brandName+" [ "+eachItem.description +"]");
	productLabelIdJSON.put(eachItem.brandName+" [ "+eachItem.description+"]", eachItem.productId);
}
productPrices = [];

JSONObject productCostJSON = new JSONObject();
//productCostJSON=prodPriceMap;

JSONObject prodIndentQtyCat = new JSONObject();
JSONObject qtyInPieces = new JSONObject();

context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productCostJSON = productCostJSON;
context.productLabelIdJSON = productLabelIdJSON;
if(displayGrid){
	context.partyCode = facility;
}


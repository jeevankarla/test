
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
import in.vasista.vbiz.purchase.MaterialHelperServices;

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
effectiveDate = parameters.effectiveDate;
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
partyId="";
billToParty="";
billToPartyId = parameters.billToPartyId;

issueToDeptId = parameters.issueToDeptId;
facility = null;
prodPriceMap = [:];

if(UtilValidate.isNotEmpty(billToPartyId)){
	context.billToPartyId = billToPartyId;
	context.billToPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,billToPartyId, false);;
	
}

prodList=[];

	/*exprList.clear();
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
	exprList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
	exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, effDateDayBegin)));
	  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);


if(UtilValidate.isNotEmpty(changeFlag) &&( (changeFlag == "InterUnitPurchase")||(changeFlag == "PurchaseOrder"))){
	tempProdList=ProductWorker.getProductsByCategory(delegator,"GEL_STATIONERY",null);
	
	prodList.addAll(tempProdList);
}*/
dctx = dispatcher.getDispatchContext();

resultMap = MaterialHelperServices.getMaterialProducts(dctx, context);
prodList.addAll(resultMap.get("productList"));
JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
JSONObject productLabelIdJSON=new JSONObject();
context.productList = prodList;

productIds = EntityUtil.getFieldListFromEntityList(prodList, "productId", true);
Map result = (Map)MaterialHelperServices.getProductUOM(delegator, productIds);
uomLabelMap = result.get("uomLabel");
productUomMap = result.get("productUom");
JSONObject productUOMJSON = new JSONObject();
JSONObject uomLabelJSON=new JSONObject();

prodList.each{eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label",eachItem.brandName +" [ " +eachItem.description+"](" +eachItem.internalName+")");
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId, eachItem.brandName+" [ "+eachItem.description +"]("+eachItem.internalName+")");
	productLabelIdJSON.put(eachItem.brandName+" [ "+eachItem.description+"]("+eachItem.internalName+")", eachItem.productId);
	
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
context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productLabelIdJSON = productLabelIdJSON;
if(displayGrid){
	context.partyCode = facility;
}

paymentTerms = delegator.findByAnd("TermType",UtilMisc.toMap("parentTypeId","FEE_PAYMENT_TERM"));
deliveryTerms = delegator.findByAnd("TermType",UtilMisc.toMap("parentTypeId","DELIVERY_TERM"));
otherTerms = delegator.findByAnd("TermType",UtilMisc.toMap("parentTypeId","OTHERS"));

JSONArray paymentTermsJSON = new JSONArray();
JSONArray deliveryTermsJSON = new JSONArray();
JSONArray otherTermsJSON = new JSONArray();

JSONObject newObj = new JSONObject();
newObj.put("value","");
newObj.put("label","");
paymentTermsJSON.add(newObj);
deliveryTermsJSON.add(newObj);
otherTermsJSON.add(newObj);

paymentTerms.each{ eachTerm ->
	newObj.put("value",eachTerm.termTypeId);
	newObj.put("label",eachTerm.description);
	paymentTermsJSON.add(newObj);
}
deliveryTerms.each{ eachTerm ->
	newObj.put("value",eachTerm.termTypeId);
	newObj.put("label",eachTerm.description);
	deliveryTermsJSON.add(newObj);
}
otherTerms.each{ eachTerm ->
	newObj.put("value",eachTerm.termTypeId);
	newObj.put("label",eachTerm.description);
	otherTermsJSON.add(newObj);
}

context.paymentTermsJSON =paymentTermsJSON;
context.deliveryTermsJSON =deliveryTermsJSON;
context.otherTermsJSON =otherTermsJSON;
// orderTypes here
purchaseTypeFlag = parameters.purchaseTypeFlag;
if(UtilValidate.isNotEmpty(purchaseTypeFlag) && purchaseTypeFlag == "contractPurchase"){
	orderTypes = delegator.findByAnd("OrderType",UtilMisc.toMap("parentTypeId","PURCHASE_CONTRACT"));
	context.orderTypes =orderTypes;
}else{
	orderTypes = delegator.findByAnd("OrderType",UtilMisc.toMap("parentTypeId","PURCHASE_ORDER"));
	orderTypes = EntityUtil.orderBy(orderTypes,UtilMisc.toList("-description"));
	context.orderTypes =orderTypes;
}

productStoreId =PurchaseStoreServices.getPurchaseFactoryStore(delegator).get("factoryStoreId");
context.productStoreId = productStoreId;
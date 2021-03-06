
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

if(changeFlag=="SalesOrder"){
	productCatageoryId="PACKING_PRODUCT";
}

if(parameters.POId){
	orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", parameters.POId), false);
	orderTypeId = orderHeader.orderTypeId;
	orderType = delegator.findOne("OrderType", UtilMisc.toMap("orderTypeId", orderTypeId), false);
		
	if(orderType){
		parentOrderTypeId = orderType.parentTypeId;
		if(parentOrderTypeId == "PURCHASE_CONTRACT"){
			parameters.purchaseTypeFlag = "contractPurchase";
		}
	}
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

prodList = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, UtilMisc.toList("RAW_MILK", "WHOLE_MILK")), null, null, null, false);
dctx = dispatcher.getDispatchContext();

//resultMap = MaterialHelperServices.getMaterialProducts(dctx, context);

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
JSONObject otherTermsLabelJSON = new JSONObject();
JSONObject otherTermsLabelIdJSON=new JSONObject();

otherTerms.each{eachItem ->
	newObj = new JSONObject();
	newObj.put("value",eachItem.termTypeId);
	newObj.put("label",eachItem.termTypeId +" [ " +eachItem.description+"]");
	otherTermsJSON.add(newObj);
	
	otherTermsLabelJSON.put(eachItem.termTypeId, eachItem.description);
	otherTermsLabelIdJSON.put(eachItem.termTypeId +" [ " +eachItem.description+"]", eachItem.termTypeId);
	
}

context.paymentTermsJSON =paymentTermsJSON;
context.deliveryTermsJSON =deliveryTermsJSON;
context.otherTermsJSON =otherTermsJSON;
context.otherTermsLabelJSON =otherTermsLabelJSON;
context.otherTermsLabelIdJSON =otherTermsLabelIdJSON;

// orderTypes here
purchaseTypeFlag = parameters.purchaseTypeFlag;
if(UtilValidate.isNotEmpty(purchaseTypeFlag) && purchaseTypeFlag == "contractPurchase"){
	orderTypes = delegator.findByAnd("OrderType",UtilMisc.toMap("parentTypeId","SALES_CONTRACT"));
	context.orderTypes =orderTypes;
}else{
	orderTypes = delegator.findByAnd("OrderType",UtilMisc.toMap("parentTypeId","SALES_ORDER"));
	orderTypes = EntityUtil.orderBy(orderTypes,UtilMisc.toList("-description"));
	context.orderTypes =orderTypes;
}

productStoreId =PurchaseStoreServices.getPurchaseFactoryStore(delegator).get("factoryStoreId");
context.productStoreId = productStoreId;


JSONArray cstJSON = new JSONArray();
JSONArray vatJSON = new JSONArray();
JSONArray excJSON = new JSONArray();
orderTaxTypeList=delegator.findList("OrderTaxType",null,UtilMisc.toSet("taxType","taxRate"),null,null,false);
if(UtilValidate.isNotEmpty(orderTaxTypeList)){
	orderTaxTypeList.each{ orderTax ->
		JSONObject newObjt = new JSONObject();
		newObjt.put("value",orderTax.taxRate);
		newObjt.put("label",orderTax.taxRate);
		if(orderTax.taxType=="EXCISE_DUTY_PUR"){
			excJSON.add(newObjt);
		}
		if(orderTax.taxType=="CST_PUR"){
			cstJSON.add(newObjt);
		}
		if(orderTax.taxType=="VAT_PUR"){
			vatJSON.add(newObjt);
		}
	}
}
context.cstJSON=cstJSON;
context.excJSON=excJSON;
context.vatJSON=vatJSON;

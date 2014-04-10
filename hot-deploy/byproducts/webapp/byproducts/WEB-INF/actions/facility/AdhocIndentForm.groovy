
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
	if(UtilValidate.isEmpty(facility)){		
		Map serviceCtx = UtilMisc.toMap("userLogin", userLogin);
		serviceCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
		serviceCtx.put("shipmentTypeId", shipmentTypeId);
		serviceCtx.put("boothId", boothId);
		//serviceCtx.put("routeId","DIR_SALE");//dir sale IS ROUTEiD
		serviceCtx.put("firstName",parameters.firstName);
		serviceCtx.put("lastName",parameters.lastName);
		serviceCtx.put("address1",parameters.address1);
		serviceCtx.put("address2",parameters.address2);
		serviceCtx.put("pinNumber",parameters.pinNumber);
		serviceCtx.put("contactNumber", parameters.contactNumber);
		serviceCtx.put("name", parameters.name);
		serviceCtx.put("facilityName", parameters.name);
		if(UtilValidate.isNotEmpty(priceTypeId)){
			serviceCtx.put("partyClassificationGroupId",priceTypeId);
		}
		if(UtilValidate.isEmpty(parameters.name)){
			Debug.logInfo(" BoothId: ["+boothId+"] must have Name !", "");
			context.errorMessage = " BoothId: ["+boothId+"] must have Name !";
			return ;
			serviceCtx.put("facilityName","SPL-"+boothId);
		}
		/*route = delegator.findOne("Facility",[facilityId :"DIR_SALE"], false);
		if(!route){
			Debug.logInfo(" Not a valid Route : "+parameters.routeId, "");
			context.errorMessage = "Not a valid Route : "+parameters.routeId;
			displayGrid = false;
			return ;
		}
		context.route = route;*/
		Map result = dispatcher.runSync("createByprodSOorGiftBooth",serviceCtx);
		if (ServiceUtil.isError(result)) {
			Debug.logError(ServiceUtil.getErrorMessage(result), "");
			displayGrid = false;
		   return result;
	   }
		String boothId = (String) result.get("facilityId");
 
	}	
	facility = delegator.findOne("Facility",[facilityId : boothId], false);
	context.booth = facility;
	routeId=facility.parentFacilityId;
	//parameters.routeId=routeId;
	

  /* result = FacilityUtil.isFacilityAcitve(dctx ,[facilityId: boothId, userLogin: userLogin]);

  if (ServiceUtil.isError(result)) {
	Debug.logInfo(boothId+" Party code is not Active !", "");
	context.errorMessage = boothId+" Party code is not Active !";
	displayGrid = false;
	return result;
   }*/

/* exprList = [];
 exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,routeId));
 exprList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"ROUTE"));
exprList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS ,"BYPRODUCTS"));
  conds=EntityCondition.makeCondition(exprList,EntityOperator.AND);
  route = delegator.findList("Facility", conds, null , null, null, false);
  if(UtilValidate.isEmpty(route)){
	Debug.logInfo(" Not a valid Route : "+routeId, "");
	context.errorMessage = "Not a valid Route : "+routeId;
	displayGrid = false;
	return ;
  }*/

lastSubProdList = [];
todaySubProdList = [];
finalProdList = [];

partyId="";
facilityParty=null;
if(boothId){
	partyAddress = null
	facilityParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", boothId),false);
	partyId = facilityParty.ownerPartyId;
	partyPostalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null,null,null, false);
	if(partyPostalAddress){
		partyPostalAddress = EntityUtil.getFirst(partyPostalAddress);
		partyAddress = partyPostalAddress.address1;
	}
	context.partyAddress = partyAddress;
}

JSONArray dataJSONList= new JSONArray();

if (finalProdList.size() > 0) {
	JSONObject quotaObj = new JSONObject();
	finalProdList.eachWithIndex {subProd, idx ->
		quotaObj.put("id",idx+1);
		quotaObj.put("title", "");
		quotaObj.put("productId", subProd.productId);
		quotaObj.put("quantity", subProd.quantity);
		dataJSONList.add(quotaObj);		
	}
}

if (dataJSONList.size() > 0) {
	context.dataJSON = dataJSONList.toString();	
	Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
}

if(lastIndentDate){
	lastIndentDate = UtilDateTime.toDateString(lastIndentDate, "MMMM dd, yyyy");
}
context.lastIndentDate = lastIndentDate;
prodList=[];

if(UtilValidate.isNotEmpty(productCatageoryId) && "INDENT"==productCatageoryId){
	prodList= ProductWorker.getProductsByCategory(delegator ,"INDENT" ,null);
}else if(UtilValidate.isNotEmpty(productCatageoryId)){
exprList.clear();
exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, productCatageoryId));
exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
		 EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, effDateDayBegin)));
  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
}
else{
prodList =ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
}
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
Map prodPriceMap =[:];
if(boothId){
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	Map inputProductRate = FastMap.newInstance();
	inputProductRate.put("productStoreId", productStoreId);
	inputProductRate.put("fromDate",effDateDayBegin);
	inputProductRate.put("facilityId",boothId);
	inputProductRate.put("partyId",partyId);
	inputProductRate.put("facilityCategory",facilityParty.categoryTypeEnum);
	inputProductRate.put("userLogin",userLogin);
	inputProductRate.put("productsList",prodList);
	Map priceResultMap =ByProductNetworkServices.getProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	prodPriceMap = (Map)priceResultMap.get("priceMap");
}
JSONObject productCostJSON = new JSONObject();
productCostJSON=prodPriceMap;
/*prodPriceMap.each{eachProdPrice ->
	productCostJSON.put(eachProdPrice.productId,eachProdPrice.unitCost);
}*/
context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;
context.productCostJSON = productCostJSON;
context.productLabelIdJSON = productLabelIdJSON;
if(displayGrid){
	context.partyCode = facility;
}
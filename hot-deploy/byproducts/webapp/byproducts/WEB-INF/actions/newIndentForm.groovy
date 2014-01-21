
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
import org.ofbiz.network.NetworkServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;

if(parameters.boothId){
	parameters.boothId = parameters.boothId.toUpperCase();
}
boothId = parameters.boothId;
subscriptionTypeId = parameters.subscriptionTypeId;
productSubscriptionTypeId = parameters.productSubscriptionTypeId;
dctx = dispatcher.getDispatchContext();
effectiveDate = parameters.effectiveDate;
subscriptionProdList = [];
displayGrid = true;
SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
try {
	effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effDate, "");
	displayGrid = false;
}
subscriptionId = null;

effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
conditionList = [];
lastIndentDate = null;
subscriptionId = null;
exprList = [];
result = [:];

facility = delegator.findOne("Facility", [facilityId : boothId],false);
if(facility){
	context.booth = facility;
}
if((!("SPECIAL_ORDER_BYPROD".equals(productSubscriptionTypeId))) && (!("GIFT_BYPROD".equals(productSubscriptionTypeId))) && (!("REPLACEMENT_BYPROD".equals(productSubscriptionTypeId)))){
	if (facility == null) {
		 Debug.logInfo("Booth '" + boothId + "' does not exist!","");
		 context.errorMessage = "Booth '" + boothId + "' does not exist!";
		 displayGrid = false;
		 return;
	 }
	
}else{
	if(("REPLACEMENT_BYPROD".equals(productSubscriptionTypeId))){
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, parameters.destinationFacilityId));
		/*conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "PARLOUR"));*/
		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		facilityList = delegator.findList("Facility", condition, null, null, null, false);
		if(!facilityList){
			Debug.logInfo(" No Facility with Party Code: ["+parameters.destinationFacilityId+"] !", "");
			context.errorMessage = "No Facility with Party Code: ["+parameters.destinationFacilityId+"] !";
			return ;
		}
		
	}

	if(UtilValidate.isEmpty(facility)){		
		Map serviceCtx = UtilMisc.toMap("userLogin", userLogin);
		serviceCtx.put("productSubscriptionTypeId", productSubscriptionTypeId);
		serviceCtx.put("boothId", boothId);
		serviceCtx.put("routeId",parameters.routeId);
		serviceCtx.put("firstName",parameters.firstName);
		serviceCtx.put("lastName",parameters.lastName);
		serviceCtx.put("address1",parameters.address1);
		serviceCtx.put("address2",parameters.address2);
		serviceCtx.put("pinNumber",parameters.pinNumber);
		serviceCtx.put("contactNumber", parameters.contactNumber);
		serviceCtx.put("name", parameters.name);
		serviceCtx.put("facilityName", parameters.name);
		route = delegator.findOne("Facility",[facilityId : parameters.routeId], false);
		if(!route){
			Debug.logInfo(" Not a valid Route : "+parameters.routeId, "");
			context.errorMessage = "Not a valid Route : "+parameters.routeId;
			displayGrid = false;
			return ;
		}
		context.route = route;
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
	
}
result = NetworkServices.isFacilityAcitve(dctx ,[facilityId: boothId, userLogin: userLogin]);
if (ServiceUtil.isError(result)) {
	Debug.logInfo(boothId+" Party code is not Active !", "");
	context.errorMessage = boothId+" Party code is not Active !";
	displayGrid = false;
	return result;
}
routeId = parameters.routeId;
exprList = [];
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
}

lastSubProdList = [];
todaySubProdList = [];
finalProdList = [];
if(("newIndent".equals(changeFlag)) ){
	condList = [];
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	condList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,effDateDayBegin));
	condList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,effDateDayEnd));
	condList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,routeId));
	cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
	shipmentList = delegator.findList("Shipment", cond, null , null, null, false);
	if(shipmentList){
		Debug.logInfo(" Delivery Schedule already generated for the route : "+routeId, "");
		context.errorMessage = " Delivery Schedule already generated for the route : "+routeId;
		displayGrid = false;
		return ;
	}
	if(boothId && subscriptionTypeId && productSubscriptionTypeId && effectiveDate){
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, boothId));
		conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		subscriptionList = delegator.findList("Subscription", condition, null, null, null, false);
		subscription = EntityUtil.filterByDate(subscriptionList);
		if(subscription.size()==1){
			subscriptionId = subscription.getAt(0).getAt("subscriptionId");
			if(subscriptionId){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscriptionId));
				conditionList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, productSubscriptionTypeId));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effDateDayBegin));
				condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				subscriptionProdList = delegator.findList("SubscriptionProduct", condition, null, ["productId", "-fromDate"], null, false);
				tempDate = effDateDayBegin;
				subscriptionProdList.each{eachItem ->
					if(tempDate.compareTo(eachItem.fromDate)== 0){
						todaySubProdList.addAll(eachItem);
					}
					else if(UtilValidate.isEmpty(lastSubProdList)){
						lastSubProdList.addAll(eachItem);
						lastIndentDate = eachItem.getAt("fromDate");
					}
					else{
						if((eachItem.fromDate).compareTo((lastSubProdList[0].fromDate)) == 0){
							lastSubProdList.addAll(eachItem);
						}
					}
				}
				todaySubProdList = EntityUtil.filterByCondition(todaySubProdList, EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, routeId));
			}
		}
	}
	
	/*indentProductList = [];
	if(UtilValidate.isNotEmpty(lastSubProdList) && UtilValidate.isNotEmpty(todaySubProdList)){
		indentProductList.addAll(todaySubProdList);
		indentProductList.addAll(lastSubProdList);
		
	}
	else if(lastSubProdList){
		indentProductList.addAll(lastSubProdList);
	}
	else{
		indentProductList.addAll(todaySubProdList);
	}
	
	distIndProdList = [];*/
	
	if(todaySubProdList){
		distIndProdList = EntityUtil.getFieldListFromEntityList(todaySubProdList, "productId", true);
		distIndProdList.each{eachItem ->
			ProdMap = [:];
			productId = eachItem;
			ProdMap.productId = productId;
			/*if(lastSubProdList){
				lastIndentQuant = EntityUtil.filterByAnd(lastSubProdList, ["productId": productId]);
				if(lastIndentQuant){
					lastQuantity = lastIndentQuant.get(0).getAt("quantity");
					ProdMap.lastQuantity = lastQuantity;
				}
			}
			else{
				ProdMap.lastQuantity = "";
			}*/
			if(todaySubProdList){
				todayIndentQuant = EntityUtil.filterByAnd(todaySubProdList, ["productId": productId]);
				if(todayIndentQuant){
					quantity = todayIndentQuant.get(0).getAt("quantity");
					ProdMap.quantity = quantity;
				}
			}
			else{
				ProdMap.quantity = "";
			}
			finalProdList.add(ProdMap);
		}
	}
}

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
		/*quotaObj.put("lastQuantity", subProd.lastQuantity);*/
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
		priceContext.put("priceDate", effDateDayBegin);
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
if(displayGrid){
	context.partyCode = facility;
}
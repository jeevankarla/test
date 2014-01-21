
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import javolution.util.FastMap;


List exprList = [];
dayBegin = UtilDateTime.getDayStart((Timestamp)context.defaultEffectiveDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd((Timestamp)context.defaultEffectiveDateTime, timeZone, locale);
changesList = []; // this is a list of change lists
changeList = [];
selSubProdList = [];
quotaSubProdList=[];
conditionList = [];
changeFlag = context.changeFlag;
 Boolean enableSameDayPmEntry = Boolean.FALSE;
 GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSameDayPmEntry"), false);
 if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
	 enableSameDayPmEntry = Boolean.TRUE; 
 }
 

productSubscriptionTypeId = parameters.productSubscriptionTypeId;
facility = delegator.findOne("Facility",[ facilityId : parameters.boothId ], false);

if(!("SPECIAL_ORDER".equals(productSubscriptionTypeId))){	
	if (facility == null) {
		 Debug.logInfo("Booth '" + boothId + "' does not exist!","");
		 context.errorMessage = "Booth '" + boothId + "' does not exist!";
		 return;
	 }
	
}else{
	if(UtilValidate.isEmpty(facility)){
		Map serviceCtx = UtilMisc.toMap("userLogin", userLogin);
		serviceCtx.put("boothId", boothId);
		serviceCtx.put("routeId",parameters.routeId);
		serviceCtx.put("firstName",parameters.firstName);
		serviceCtx.put("lastName",parameters.lastName);
		serviceCtx.put("address1",parameters.address1);
		serviceCtx.put("address2",parameters.address2);
		serviceCtx.put("contactNumber", parameters.contactNumber);
		serviceCtx.put("facilityName", parameters.firstName+parameters.lastName);	
		Map result = dispatcher.runSync("createByprodSOBooth",serviceCtx);
		if (ServiceUtil.isError(result)) {
			Debug.logError(ServiceUtil.getErrorMessage(result), "");
		   return result;
	   }
		String boothId = (String) result.get("facilityId");
 
	}
	
	facility = delegator.findOne("Facility",[facilityId : boothId], false);
	
	if(!(parameters.routeId).equals(facility.getString("byProdRouteId"))){
		Map serviceCtx = UtilMisc.toMap("userLogin", userLogin);
		serviceCtx.putAll(facility);
		serviceCtx.put("byProdRouteId", parameters.routeId);
		Map result = dispatcher.runSync("updateFacility",serviceCtx);
		if (ServiceUtil.isError(result)) {
			Debug.logError(ServiceUtil.getErrorMessage(result), "");
		   return result;
	   }
		
	}
}

context.booth=facility;



if( !("GatePass".equals(changeFlag)) && !("TruckSheetCorrection".equals(changeFlag)) && !("CardSale".equals(changeFlag))){
	
	
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,boothId));
	//::TODO:: need to add checks for AM/PM etc
	if(subscriptionTypeId.equals("AM")){
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId) ,EntityOperator.OR ,EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, null)));
		            	
    }else{
    	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, subscriptionTypeId));
    }
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	subscriptionList =delegator.findList("Subscription", condition, null, null, null, false);
	//::TODO:: for now just pick the first one. Also need to handle missing subscription
	if ((subscriptionList == null || subscriptionList.size() == 0)) {
		context.errorMessage = "Booth '" + boothId + "' does not have any active data!";
		return;
	}
	
	
	if(UtilValidate.isNotEmpty(subscriptionList)){
		subscription = subscriptionList.get(0);
		
		quotaSubProdList=[];
		if (parameters.productSubscriptionTypeId == "CASH" || parameters.productSubscriptionTypeId == "CREDIT"  || parameters.productSubscriptionTypeId == "CARD") {
			quotaSubProdList =EntityUtil.filterByDate(delegator.findByAnd("SubscriptionProduct", [subscriptionId: subscription.subscriptionId, productSubscriptionTypeId:parameters.productSubscriptionTypeId], null));
			//here handle two active cards  for the same day
			Map productQuantity = FastMap.newInstance();
			quotaSubProdList.each { quotaSubProd ->
				if(UtilValidate.isEmpty(productQuantity[quotaSubProd.productId])){
					productQuantity[quotaSubProd.productId] = quotaSubProd.quantity;
				}else{
					quotaSubProd.quantity += productQuantity[quotaSubProd.productId];
				}
			}
			
		}
	
		exprList.add(EntityCondition.makeCondition([
			EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())),
			EntityCondition.makeCondition("lastModifiedDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()))
		   ], EntityOperator.AND));
	   exprList.add(EntityCondition.makeCondition("subscriptionId", EntityOperator.EQUALS, subscription.subscriptionId));
	   exprList.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, parameters.productSubscriptionTypeId));
	   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	   
	   
	   subProdList =delegator.findList("SubscriptionProduct", condition, null, ["sequenceNum"], null, false);
	   selSubProdList = EntityUtil.filterByDate(subProdList, UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 1), "fromDate", "thruDate", true);
	}
	
	   
}

//handling gate pass change

if( "GatePass".equals(changeFlag) || "TruckSheetCorrection".equals(changeFlag)){
	
	if("TruckSheetCorrection".equals(changeFlag)){		
		exprList1 =[];
		exprList1.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS  ,"SALES_ORDER"));
		exprList1.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS  , boothId));
		exprList1.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL  , "ORDER_CANCELLED"));
		exprList1.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS  ,parameters.productSubscriptionTypeId));
		exprList1.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS , "GENERATED"));
		exprList1.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS , context.shipmentTypeId));		
		if((context.shipmentTypeId) == "PM_SHIPMENT_SUPPL" && !enableSameDayPmEntry){							
			exprList1.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO  ,UtilDateTime.getDayStart(dayBegin,-1)));
			exprList1.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(dayBegin,-1)));
		}else{
			exprList1.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO  ,dayBegin));
			exprList1.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		}		
		
		condition1 = EntityCondition.makeCondition(exprList1, EntityOperator.AND);		
		quotaSubProdList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, null, ["changeDatetime"], null, false);
		
		if(!quotaSubProdList){
			context.errorMessage = "Booth '" + boothId + "' No Dispatch";
			return;
		}
		
		changesList =[];
	}
}

tempSequenceNum = "";
selSubProdList.each {subProd ->
	if (tempSequenceNum == "") {
		 tempSequenceNum = subProd.sequenceNum;
	}
	if (tempSequenceNum != subProd.sequenceNum) {
		changesList.add(changeList);
		changeList = [];
		tempSequenceNum = subProd.sequenceNum;
	}
	changeList.add(subProd);
}
if (changeList.size() > 0) {
	changesList.add(changeList);
	lastIndentDate = changeList.get(0).getAt("createdDate");
	context.lastIndentDate = lastIndentDate;
}

Debug.logInfo("changeList="+changeList,"");

// now let's populate the json data structure

JSONArray dataJSONList= new JSONArray();

/*
 Commenting out for now, can use this to store the last indent
if (quotaSubProdList.size() > 0) {
	JSONObject quotaObj = new JSONObject();
	quotaObj.put("id","1");
	quotaObj.put("title", "");
	if("TruckSheetCorrection".equals(changeFlag)){
		quotaObj.put("title", "Dispatched");
	}
	quotaObj.put("productSubscriptionTypeId", parameters.productSubscriptionTypeId);
	quotaSubProdList.each {subProd ->
		quotaObj.put("productId", subProd.productId);
		quotaObj.put("quantity", subProd.quantity);
	}
	dataJSONList.add(quotaObj);
}

*/

changesList.eachWithIndex {changeList, idx ->
	changeList.each {subProd ->
		JSONObject changeObj = new JSONObject();
		changeObj.put("id",idx+1);
		changeObj.put("title", "");
		changeObj.put("productSubscriptionTypeId", parameters.productSubscriptionTypeId);
		sequenceNum = "";

		changeObj.put("productId", subProd.productId);
		changeObj.put("quantity", subProd.quantity);
		changeObj.put("lastQuantity", subProd.quantity);

		dataJSONList.add(changeObj);
	}
}



if (dataJSONList.size() > 0) {
	context.dataJSON = dataJSONList.toString();
Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
}


products = ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.productList = products;
JSONArray productItemsJSON = new JSONArray();
JSONObject productIdLabelJSON = new JSONObject();
products.each{ eachItem ->
	JSONObject newObj = new JSONObject();
	newObj.put("value",eachItem.productId);
	newObj.put("label",eachItem.productId + " [" + eachItem.productName + "]");
	productItemsJSON.add(newObj);
	productIdLabelJSON.put(eachItem.productId, eachItem.productId + " [" + eachItem.productName + "]")
}
context.productItemsJSON = productItemsJSON;
context.productIdLabelJSON = productIdLabelJSON;

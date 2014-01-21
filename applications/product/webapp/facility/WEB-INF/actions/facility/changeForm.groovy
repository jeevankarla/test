
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.network.NetworkServices;
import javolution.util.FastMap;
import java.text.SimpleDateFormat;
import java.sql.Timestamp; 
import org.ofbiz.base.util.UtilMisc;
import javax.swing.text.html.parser.Entity;
import java.text.ParseException;

List exprList = [];
dayBegin = UtilDateTime.getDayStart((Timestamp)context.defaultEffectiveDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd((Timestamp)context.defaultEffectiveDateTime, timeZone, locale);
fromDateTime = UtilDateTime.getDayStart((Timestamp)context.defaultEffectiveDateTime, timeZone, locale);

changeFlag = context.changeFlag;
if(("TruckSheetCorrection".equals(changeFlag))){
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(parameters.effectiveDate).getTime());
		
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	
	if(fromDateTime){
		dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
		dayEnd =  UtilDateTime.getDayEnd(fromDateTime, timeZone, locale);
	}
}

changesList = []; // this is a list of change lists
changeList = [];
selSubProdList = [];
quotaSubProdList=[];
conditionList = [];

 Boolean enableSameDayPmEntry = Boolean.FALSE;
 GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSameDayPmEntry"), false);
 if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
	 enableSameDayPmEntry = Boolean.TRUE; 
 }
 

 


if(!("CardSale".equals(changeFlag))){
		
		Boolean enableLmsDefaultProductSubscriptionType = Boolean.FALSE;
		GenericValue tenantConfigEnableLmsDefaultProductSubscriptionType = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableLmsDefaultProductSubscriptionType"), false);
		if (UtilValidate.isNotEmpty(tenantConfigEnableLmsDefaultProductSubscriptionType) && (tenantConfigEnableLmsDefaultProductSubscriptionType.getString("propertyValue")).equals("Y")) {
			enableLmsDefaultProductSubscriptionType = Boolean.TRUE;
		}
		
		productList = NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("salesDate", dayBegin));
		context.productList = productList;
		facility = delegator.findOne("Facility",[facilityId : parameters.boothId], false);
		if (facility == null) {
			 Debug.logInfo("Booth '" + boothId + "' does not exist!","");
			 context.errorMessage = "Booth '" + boothId + "' does not exist!";
			 return;
		 }
		// validations for supply type and booth categoryType
		if(facility.categoryTypeEnum == "VENDOR" && parameters.productSubscriptionTypeId != "CARD"){			
			if(enableLmsDefaultProductSubscriptionType){
			   parameters.productSubscriptionTypeId = "CASH";
			   context.productSubscriptionTypeId = "CASH";
			}
		}
		
		
		 if( parameters.productSubscriptionTypeId == "CREDIT" && facility.categoryTypeEnum != "CR_INST"){			
			 context.errorMessage = "Booth '" + boothId + "'must be  credit institution";
			 return;
		 }
		 if(facility.categoryTypeEnum == "CR_INST" && parameters.productSubscriptionTypeId != "CREDIT"){
			 if(enableLmsDefaultProductSubscriptionType){
				 parameters.productSubscriptionTypeId = "CREDIT";
				 context.productSubscriptionTypeId = "CREDIT";
			 }else{
				 context.errorMessage = "Booth '" + boothId + "'accepts only credit";
				 return;
			 }	 
			 
		 }
		 if(parameters.productSubscriptionTypeId == "CARD" && facility.categoryTypeEnum != "VENDOR"){			
			 context.errorMessage = "Booth '" + boothId + "'does not accept card";
			 return;			
		 }
		if( parameters.productSubscriptionTypeId != "SPECIAL_ORDER" && facility.categoryTypeEnum == "FRANCHISEE"){
			 context.errorMessage = "Booth '" + boothId + "'does not accept other than special order";
			 return;
			 
		 }
		 if( parameters.productSubscriptionTypeId != "SPECIAL_ORDER" && facility.categoryTypeEnum == "SO_INST" ){
			 			 
			 if(enableLmsDefaultProductSubscriptionType){
				 parameters.productSubscriptionTypeId = "SPECIAL_ORDER";
				 context.productSubscriptionTypeId = "SPECIAL_ORDER";
			 }else{			 			 
				 if( (facility.facilityId != "9999" && facility.facilityId !="6666" && (facility.facilityId != "9956" )) ||  ((facility.facilityId == "9956" ) && (parameters.productSubscriptionTypeId != "CARD"))) {
					 context.errorMessage = "Booth '" + boothId + "'does not accept other than special order";
					 return;
				 }			 
			 }		 
		 }	 
		context.booth=facility;
		shipmentType = delegator.findOne("ShipmentType",[shipmentTypeId : parameters.shipmentTypeId], false);
		context.shipmentType=shipmentType;
}

if(("CardSale".equals(changeFlag))){
		
	milkCardTypeList =delegator.findList("MilkCardType", EntityCondition.makeCondition([isLMS:'Y']), UtilMisc.toSet(["milkCardTypeId","name"]), null, null, false);
	context.milkCardTypeList = milkCardTypeList;
	
}

//Debug.logInfo("parameters.productSubscriptionTypeId="+parameters.productSubscriptionTypeId,"");


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
	if (subscriptionList == null || subscriptionList.size() == 0) {
		context.errorMessage = "Booth '" + boothId + "' does not have any active data!";
		return;
	}
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
}
// now let's populate the json data structure

JSONArray dataJSONList= new JSONArray();

if (quotaSubProdList.size() > 0) {
	JSONObject quotaObj = new JSONObject();
	quotaObj.put("id","1");
	quotaObj.put("title", "Quota");
	if("TruckSheetCorrection".equals(changeFlag)){
		quotaObj.put("title", "Dispatched");
	}
	quotaObj.put("productSubscriptionTypeId", parameters.productSubscriptionTypeId);
	quotaSubProdList.each {subProd ->
		quotaObj.put(subProd.productId, subProd.quantity);	
	}
	dataJSONList.add(quotaObj);
}


changesList.eachWithIndex {changeList, idx ->
	JSONObject changeObj = new JSONObject();
	changeObj.put("id",idx+2);
	changeObj.put("productSubscriptionTypeId", parameters.productSubscriptionTypeId);
	sequenceNum = "";
	changeList.each {subProd ->
		changeObj.put(subProd.productId, subProd.quantity);
		changeObj.put("sequenceNum", subProd.sequenceNum);
		sequenceNum = subProd.sequenceNum;
	}
	if (parameters.productSubscriptionTypeId == "SPECIAL_ORDER") {
		changeObj.put("title", "Order #" + sequenceNum);
	}
	else {
		changeObj.put("title", "Changes");
	}
	dataJSONList.add(changeObj);
}

if (changesList.size() == 0) {
	JSONObject newObj = new JSONObject();
	newObj.put("id","2");
	newObj.put("productSubscriptionTypeId", parameters.productSubscriptionTypeId);
	if (parameters.productSubscriptionTypeId == "SPECIAL_ORDER") {
		newObj.put("title", "New");
		newObj.put("sequenceNum",1);
	}
	else if (("GatePass".equals(changeFlag)) || ("CardSale".equals(changeFlag))) {
		newObj.put("title", "New");		
	}
	else if ("TruckSheetCorrection".equals(changeFlag)) {
		newObj.put("title", "Correction");
	}
	else {
		newObj.put("title", "Changes");
		newObj.put("sequenceNum","_NA_");
	}
	dataJSONList.add(newObj);
}
else if (parameters.productSubscriptionTypeId == "SPECIAL_ORDER" ) {
	// for special orders we must always allow option of adding another row
	JSONObject newObj = new JSONObject();
	newObj.put("id",changesList.size() + 2);
	newObj.put("productSubscriptionTypeId", parameters.productSubscriptionTypeId);
	newObj.put("title", "New");
	newObj.put("sequenceNum",changesList.size() + 1);
	dataJSONList.add(newObj);
}

if (dataJSONList.size() > 0) {
	context.dataJSON = dataJSONList.toString();
Debug.logInfo("dataJSONList="+dataJSONList.toString(),"");
}
else {
	context.errorMessage = "Booth '" + boothId + "' does not have any active data!";
	return;
}

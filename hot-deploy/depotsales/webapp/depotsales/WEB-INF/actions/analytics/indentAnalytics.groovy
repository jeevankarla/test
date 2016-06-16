import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.service.ServiceUtil;

dctx = dispatcher.getDispatchContext();
JSONArray dataList = new JSONArray();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
fromDate=null;
thruDate=null;
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if (parameters.thruDate) {
			   thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	   }else {
			   thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	   }
} catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + e, "");
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
if(UtilValidate.isNotEmpty(fromDate)){
	context.defaultEffectiveDate=UtilDateTime.toDateString(fromDate,"MMMM dd, yyyy");
}
if(UtilValidate.isNotEmpty(thruDate)){
	context.defaultEffectiveThruDate=UtilDateTime.toDateString(thruDate,"MMMM dd, yyyy");
}
 dctx = dispatcher.getDispatchContext();
 conditionList = [];
 conditionList.clear();
 partyIdNameMap = [:];
 branchROMap = [:]; 
		conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "RO"));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		roList = delegator.findList("Facility", condition , UtilMisc.toSet("facilityId", "facilityTypeId", "ownerPartyId", "facilityName"), null, null, false );		
		for(int i=0; i<roList.size(); i++){
			
			ro = roList.get(i);
			partyIdNameMap.put(ro.ownerPartyId, ro.facilityName); //::TODO:: get PartyName instead
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, ro.ownerPartyId));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
//			conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
			
			branchParties = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
			for (int j = 0; j < branchParties.size(); j++) {
				branchParty = branchParties.get(j);
				partyIdNameMap.put(branchParty.partyId, branchParty.groupName);
				branchROMap.put(branchParty.partyIdTo, ro.ownerPartyId);	
			}
		}
//	 Debug.log("===partyIdNameMap=====>"+partyIdNameMap);			 	
//	 Debug.log("===branchROMap=====>"+branchROMap);
		
 conditionList.clear();
 
 
 
//roleTypeAndPartyList = delegator.findByAnd("RoleTypeAndParty",["roleTypeId" :"EMPANELLED_CUSTOMER"]);
 

 roleTypeId=parameters.roleTypeId;
  
 if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate) ){
	 conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	 conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
 }
 
/* 
 if(UtilValidate.isNotEmpty(parameters.productId)){
	 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
	 context.productId = parameters.productId;
 }
 if(UtilValidate.isNotEmpty(filterProductSale)){
	 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, filterProductSale));
 }
 */
 /*
 if(UtilValidate.isNotEmpty(parameters.partyId)){
	 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
 }
*/
 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
 conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
 
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
 
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 salesOrderList = delegator.findList("OrderHeaderAndRoles", condition, UtilMisc.toSet("orderId","statusId","partyId","grandTotal"), null, null, false);
 
 //Debug.log("===salesOrderList=="+salesOrderList+"==condition=="+condition);
 salesOrderIds = EntityUtil.getFieldListFromEntityList(salesOrderList, "orderId", true);

 conditionList.clear();
 conditionList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.IN, salesOrderIds));
 conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 poOrderAssocList = delegator.findList("OrderAssoc", condition, UtilMisc.toSet("orderId", "toOrderId"), null, null, false);
 poOrderAssocIdsList = EntityUtil.getFieldListFromEntityList(poOrderAssocList, "orderId", true);
 
 conditionList.clear(); 
 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, poOrderAssocIdsList)); 
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 poOrderList = delegator.findList("OrderHeader", condition, UtilMisc.toSet("orderId","statusId", "externalId"), null, null, false);
 salesOrderPOMap = [:]
 //Debug.log("===poOrderList=="+poOrderList+"==condition=="+condition);	 
 	if(poOrderList){
		 poOrderList.each{eachItem ->
	 		salesOrderPOMap.put(eachItem.getAt("externalId"), eachItem);
		 }
	 }
	  
 SortedMap DataMap = new TreeMap();
	if(salesOrderList){
		 salesOrderList.each{eachItem ->
		 	partyId = eachItem.getAt("partyId");
		 	completed = 0;
		 	if (salesOrderPOMap.containsKey(eachItem.getAt("orderId"))) {
		 		purchaseOrder = salesOrderPOMap.get(eachItem.getAt("orderId"));
		 		if (purchaseOrder.get("statusId") == "ORDER_COMPLETED") {
		 			completed = 1;
		 		}
		 	}
		 	roId = branchROMap.get(partyId);	
		 	grandTotal = (new BigDecimal(eachItem.getAt("grandTotal"))).setScale(0, RoundingMode.HALF_UP);	 	
		 	if (DataMap.containsKey(partyId)) {
		 		branchDetails = DataMap.get(partyId);
		 		totIndents = branchDetails.get("totIndents");
		 		branchDetails.putAt("totIndents", ++totIndents);
		 		branchDetails.putAt("completed", completed + branchDetails.get("completed"));		 		
		 		branchDetails.putAt("totRevenue", grandTotal + branchDetails.get("totRevenue"));		 		
		 	}
		 	else {
		 		branchDetails = [:];
		 		branchDetails.putAt("totIndents", 1);
		 		branchDetails.putAt("totRevenue", grandTotal);
		 		branchDetails.putAt("completed", completed);		 		
		 		DataMap.putAt(partyId, branchDetails);		 	
		 	}
		 	if (DataMap.containsKey(roId)) {
		 		roDetails = DataMap.get(roId);
		 		totIndents = roDetails.get("totIndents");
		 		roDetails.putAt("totIndents", ++totIndents);
		 		roDetails.putAt("completed", completed + roDetails.get("completed"));		 				 		
		 		roDetails.putAt("totRevenue", grandTotal + roDetails.get("totRevenue"));		 				 		
		 	}
		 	else {
		 		roDetails = [:];
		 		roDetails.putAt("totIndents", 1);
		 		roDetails.putAt("totRevenue", grandTotal);	
		 		roDetails.putAt("completed", completed);		 				 			 		
		 		DataMap.putAt(roId, roDetails);	 	
		 	}		 	
		 }
	 }
	


//Debug.log("===DataMap=="+DataMap);
 
	 for(Map.Entry entry : DataMap.entrySet()){
	 		JSONObject newObj = new JSONObject();
	 		partyId = entry.getKey();
	 		entryValue = entry.getValue();
	 		if (branchROMap.containsKey(partyId)) {
	 			roId = branchROMap.get(partyId);
				newObj.put("partyId", partyId );						
				newObj.put("branch", partyIdNameMap.get(partyId));
				newObj.put("ReportsTo", roId);
				newObj.put("ro","");
				newObj.put("avgTAT","");			
				newObj.put("totalRevenue", entryValue.get("totRevenue"));
				newObj.put("totalIndents", entryValue.get("totIndents"));				
				newObj.put("inProcess",entryValue.get("totIndents") - entryValue.get("completed"));
				newObj.put("completed", entryValue.get("completed"));		
	 		}
	 		else {
				newObj.put("partyId", partyId );						
				newObj.put("branch", "");
				newObj.put("ReportsTo", "");
				newObj.put("ro", partyIdNameMap.get(partyId));
				newObj.put("avgTAT","");	
				newObj.put("totalRevenue", entryValue.get("totRevenue"));						
				newObj.put("totalIndents", entryValue.get("totIndents"));
				newObj.put("inProcess",entryValue.get("totIndents") - entryValue.get("completed"));
				newObj.put("completed", entryValue.get("completed"));	 		
	 		}
	 		dataList.add(newObj);			
	 }		
				
// Debug.log("===dataList=="+dataList);
				
context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess(); 
resultMap.put("data",dataList);

return resultMap;


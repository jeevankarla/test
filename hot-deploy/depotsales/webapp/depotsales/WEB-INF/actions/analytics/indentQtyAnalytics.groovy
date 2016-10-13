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
 

 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED","ORDER_REJECTED")));
// conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
 
//conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
 
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 salesOrderList = delegator.findList("IndentSummaryDetails", condition,null, null, null, false);
 
 //Debug.log("===salesOrderList=="+salesOrderList+"==condition=="+condition);
	  
 ROOT_ID = "NHDC"; //::TODO::
 SortedMap DataMap = new TreeMap();
	if(salesOrderList){
		 salesOrderList.each{eachItem ->
		 partyId = eachItem.getAt("branchId");
		 
			 completed = 0;
				 if (eachItem.get("shippedQty")) {
					 completed = eachItem.get("shippedQty");
				 }

			 roId = branchROMap.get(partyId);
			 totalQty = new BigDecimal(eachItem.getAt("quantity")).setScale(2, 0);
			 totalPoQty = new BigDecimal(eachItem.getAt("poQuantity")).setScale(2, 0);
			 
			 if (DataMap.containsKey(partyId)) {
				 branchDetails = DataMap.get(partyId);
				 branchDetails.putAt("poQty", totalPoQty+ branchDetails.get("poQty"));
				 branchDetails.putAt("completed", completed + branchDetails.get("completed"));
				 branchDetails.putAt("totQty", totalQty + branchDetails.get("totQty"));
			 }
			 else {
				 branchDetails = [:];
				 branchDetails.putAt("poQty", totalPoQty);
				 branchDetails.putAt("totQty", totalQty);
				 branchDetails.putAt("completed", completed);
				 DataMap.putAt(partyId, branchDetails);
			 }
			 if (DataMap.containsKey(roId)) {
				 roDetails = DataMap.get(roId);
				 roDetails.putAt("poQty", totalPoQty+roDetails.get("poQty"));
				 roDetails.putAt("completed", completed + roDetails.get("completed"));
				 roDetails.putAt("totQty", totalQty + roDetails.get("totQty"));
			 }
			 else {
				 roDetails = [:];
				 roDetails.putAt("poQty", totalPoQty);
				 roDetails.putAt("totQty", totalQty);
				 roDetails.putAt("completed", completed);
				 DataMap.putAt(roId, roDetails);
			 }
			 if (DataMap.containsKey(ROOT_ID)) {
				 totDetails = DataMap.get(ROOT_ID);
				 poQty = totDetails.get("poQty");
				 totDetails.putAt("poQty", totalPoQty+poQty);
				 totDetails.putAt("completed", completed + totDetails.get("completed"));
				 totDetails.putAt("totQty", totalQty + totDetails.get("totQty"));
			 }
			 else {
				 totDetails = [:];
				 totDetails.putAt("poQty", totalPoQty);
				 totDetails.putAt("totQty", totalQty);
				 totDetails.putAt("completed", completed);
				 DataMap.putAt(ROOT_ID, totDetails);
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
				newObj.put("totalRevenue", entryValue.get("totQty"));
				newObj.put("totalIndents", entryValue.get("poQty"));
				newObj.put("inProcess",entryValue.get("poQty") - entryValue.get("completed"));
				newObj.put("completed", entryValue.get("completed"));
			 }
			 else if (partyId == ROOT_ID) {
				newObj.put("partyId", ROOT_ID );
				newObj.put("branch", "");
				newObj.put("ReportsTo", "");
				newObj.put("ro", ROOT_ID);
				newObj.put("avgTAT","");
				newObj.put("totalRevenue", entryValue.get("totQty"));
				newObj.put("totalIndents", entryValue.get("poQty"));
				newObj.put("inProcess",entryValue.get("poQty") - entryValue.get("completed"));
				newObj.put("completed", entryValue.get("completed"));
			 }
			 else {
				newObj.put("partyId", partyId );
				newObj.put("branch", "");
				newObj.put("ReportsTo", ROOT_ID);
				newObj.put("ro", partyIdNameMap.get(partyId));
				newObj.put("avgTAT","");
				newObj.put("totalRevenue", entryValue.get("totQty"));
				newObj.put("totalIndents", entryValue.get("poQty"));
				newObj.put("inProcess",entryValue.get("poQty") - entryValue.get("completed"));
				newObj.put("completed", entryValue.get("completed"));
			 }
			 dataList.add(newObj);
	 }
				
//Debug.log("===dataList=="+dataList);
				
context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess();
resultMap.put("data",dataList);

return resultMap;


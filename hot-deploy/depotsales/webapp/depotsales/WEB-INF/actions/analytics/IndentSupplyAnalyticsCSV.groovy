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
import java.text.DecimalFormat;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.service.ServiceUtil;

dctx = dispatcher.getDispatchContext();
JSONArray dataList = new JSONArray();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
fromDate=null;
thruDate=null;


analyticsThruDate=UtilDateTime.nowTimestamp();
analyticsFrmDate= UtilDateTime.addDaysToTimestamp(analyticsThruDate,-90);
defaultEffectiveThruDateStr=UtilDateTime.toDateString(analyticsThruDate,"MMMM dd, yyyy");
defaultEffectiveDateStr=UtilDateTime.toDateString(analyticsFrmDate,"MMMM dd, yyyy");
context.defaultEffectiveDateStr=defaultEffectiveDateStr
context.defaultEffectiveThruDateStr=defaultEffectiveThruDateStr;
try {
	   if (parameters.fromDateCsv) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDateCsv).getTime()));
	   }
	   if (parameters.thruDateCsv) {
			   thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDateCsv).getTime()));
	   }else {
			   thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	   }
} catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + e, "");
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}






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
 
 DecimalFormat df = new DecimalFormat("0.00");
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
			 totalSaleQty=0;
			 if(eachItem.getAt("saleQuantity")){
			    totalSaleQty = new BigDecimal(eachItem.getAt("saleQuantity")).setScale(2, 0);
				}
			 totalSaleAmount =0;
			 if(eachItem.getAt("saleAmount")){
				 totalSaleAmount = new BigDecimal(eachItem.getAt("saleAmount")).setScale(2, 0);
				 if(eachItem.saleTenPrcAmount){
				 totalSaleAmount=totalSaleAmount.minus(new BigDecimal(eachItem.getAt("saleTenPrcAmount")).setScale(2, 0))
				 }
				 }
			 totalPurAmount=0;
			 if(eachItem.getAt("purAmout")){
				 totalPurAmount = new BigDecimal(eachItem.getAt("purAmout")).setScale(2, 0);
			 }
			 if (DataMap.containsKey(partyId)) {
				 branchDetails = DataMap.get(partyId);
				 branchDetails.putAt("saleQty", totalSaleQty+ branchDetails.get("saleQty"));
				 branchDetails.putAt("saleAmt", totalSaleAmount+ branchDetails.get("saleAmt"));
				 branchDetails.putAt("purAmout", totalPurAmount+ branchDetails.get("purAmout"));
				 branchDetails.putAt("completed", completed + branchDetails.get("completed"));
				 branchDetails.putAt("totQty", totalQty + branchDetails.get("totQty"));
			 }
			 else {
				 branchDetails = [:];
				 branchDetails.putAt("saleQty", totalSaleQty);
				 branchDetails.putAt("saleAmt",totalSaleAmount);
				 branchDetails.putAt("purAmout", totalPurAmount);
				 branchDetails.putAt("totQty", totalQty);
				 branchDetails.putAt("completed", completed);
				 DataMap.putAt(partyId, branchDetails);
			 }
			 if (DataMap.containsKey(roId)) {
				 roDetails = DataMap.get(roId);
				 roDetails.putAt("saleQty", totalSaleQty+roDetails.get("saleQty"));
				 roDetails.putAt("saleAmt", totalSaleAmount+ roDetails.get("saleAmt"));
				 roDetails.putAt("purAmout", totalPurAmount+ roDetails.get("purAmout"));
				 roDetails.putAt("completed", completed + roDetails.get("completed"));
				 roDetails.putAt("totQty", totalQty + roDetails.get("totQty"));
			 }
			 else {
				 roDetails = [:];
				 roDetails.putAt("saleQty", totalSaleQty);
				 roDetails.putAt("saleAmt",totalSaleAmount);
				 roDetails.putAt("purAmout", totalPurAmount);
				 roDetails.putAt("totQty", totalQty);
				 roDetails.putAt("completed", completed);
				 DataMap.putAt(roId, roDetails);
			 }
			 if (DataMap.containsKey(ROOT_ID)) {
				 totDetails = DataMap.get(ROOT_ID);
				 saleQty = totDetails.get("saleQty");
				 totDetails.putAt("saleQty", totalSaleQty+saleQty);
				 totDetails.putAt("saleAmt", totalSaleAmount+ totDetails.get("saleAmt"));
				 totDetails.putAt("purAmout", totalPurAmount+ totDetails.get("purAmout"));
				 totDetails.putAt("completed", completed + totDetails.get("completed"));
				 totDetails.putAt("totQty", totalQty + totDetails.get("totQty"));
			 }
			 else {
				 totDetails = [:];
				 totDetails.putAt("saleQty", totalSaleQty);
				 totDetails.putAt("saleAmt",totalSaleAmount);
				 totDetails.putAt("purAmout",totalPurAmount);
				 totDetails.putAt("totQty", totalQty);
				 totDetails.putAt("completed", completed);
				 DataMap.putAt(ROOT_ID, totDetails);
			 }
		 }
	 }
	


//Debug.log("===DataMap=="+DataMap);
 
	  	
	entryValue =DataMap.get("NHDC") ;
	tempMap=[:];
	saleAmt= totalSaleAmount+ totDetails.get("saleAmt");
	saleQty = totDetails.get("saleQty");
	saleQty= totalSaleQty+saleQty
	
	purAmout= totalPurAmount+ totDetails.get("purAmout");
	completed= completed + totDetails.get("completed");
	totQty =totalQty + totDetails.get("totQty");
	
	
				tempMap.put("partyId",ROOT_ID);
				tempMap.put("branch", "");
				tempMap.put("ReportsTo", "");
				tempMap.put("ro",ROOT_ID);
				tempMap.put("avgTAT","");
				tempMap.put("totalRevenue", df.format((entryValue.get("totQty")/100000)));
				tempMap.put("saleQty", df.format((entryValue.get("saleQty")/100000)));
				tempMap.put("totalIndents", df.format((entryValue.get("saleQty")/100000)));
				tempMap.put("purAmout", df.format((entryValue.get("purAmout")/100000)));
				tempMap.put("saleAmt", df.format((entryValue.get("saleAmt")/100000)));
				tempMap.put("inProcess",df.format(((entryValue.get("totQty") - entryValue.get("saleQty"))/100000)));
				tempMap.put("completed", entryValue.get("completed"));
		dataList.add(tempMap);
		
		//conditionList.clear();
		//conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "RO"));
		//condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		
		//roPartyIdList = delegator.findList("Facility", condition , UtilMisc.toSet("ownerPartyId"), null, null, false );
		roPartyIdList = EntityUtil.getFieldListFromEntityList(roList, "ownerPartyId", true);
		for(int i=0; i<roPartyIdList.size(); i++){
			newObj=[:];
			roId = roPartyIdList.get(i);
			partyId = roId;
			roWiseEntryValue =DataMap.get(roId) ;
			 
			if(roWiseEntryValue){
			
			    newObj.put("partyId",partyId);
				newObj.put("branch", "");
				newObj.put("ReportsTo", ROOT_ID);
				newObj.put("ro",partyIdNameMap.get(partyId));
				newObj.put("avgTAT","");
				newObj.put("totalRevenue", df.format((roWiseEntryValue.get("totQty")/100000)));
				newObj.put("totalIndents", df.format((roWiseEntryValue.get("saleQty")/100000)));
				newObj.put("purAmout", df.format((roWiseEntryValue.get("purAmout")/100000)));
				newObj.put("saleAmt", df.format((roWiseEntryValue.get("saleAmt")/100000)));
				newObj.put("saleQty", df.format((roWiseEntryValue.get("saleQty")/100000)));
				newObj.put("inProcess",df.format(((roWiseEntryValue.get("totQty") - roWiseEntryValue.get("saleQty"))/100000)));
				newObj.put("completed", roWiseEntryValue.get("completed"));
			
				
				dataList.add(newObj);
			 }
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, roId));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
			conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
//			conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
			
			branchParties = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
			branchPartyIds=EntityUtil.getFieldListFromEntityList(branchParties, "partyId", true);
			for(int j=0; j<branchPartyIds.size(); j++){
				newObj1=[:];
				branchId=branchPartyIds.get(j);
				partyId = branchId;
				branchWiseEntryValue =DataMap.get(branchId);
				
				if(branchWiseEntryValue){
				
				newObj1.put("partyId","");
				newObj1.put("branch", partyIdNameMap.get(partyId));
				newObj1.put("ReportsTo", roId);
				newObj1.put("ro","");
				newObj1.put("avgTAT","");
				newObj1.put("totalRevenue", df.format((branchWiseEntryValue.get("totQty")/100000)));
				newObj1.put("totalIndents", df.format((branchWiseEntryValue.get("saleQty")/100000)));
				newObj1.put("purAmout", df.format((branchWiseEntryValue.get("purAmout")/100000)));
				newObj1.put("saleAmt", df.format((branchWiseEntryValue.get("saleAmt")/100000)));
				newObj1.put("saleQty", df.format((branchWiseEntryValue.get("saleQty")/100000)));
				newObj1.put("inProcess",df.format(((branchWiseEntryValue.get("totQty") - branchWiseEntryValue.get("saleQty"))/100000)));
				newObj1.put("completed", branchWiseEntryValue.get("completed"));
				dataList.add(newObj1);
				
				  }
				
			
			}
			
		}
	
	 context.dataList =  dataList;


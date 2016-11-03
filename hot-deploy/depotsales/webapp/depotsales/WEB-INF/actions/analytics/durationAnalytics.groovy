import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.lang.*;
import java.lang.Long;
import java.math.BigDecimal;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Time;
import java.sql.Timestamp;
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
			branchParties = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
			for (int j = 0; j < branchParties.size(); j++) {
				branchParty = branchParties.get(j);
				partyIdNameMap.put(branchParty.partyId, branchParty.groupName);
				branchROMap.put(branchParty.partyIdTo, ro.ownerPartyId);
			}
		}
 conditionList.clear();
 roleTypeId=parameters.roleTypeId;
 if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate) ){
	 conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	 conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
 }
 conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
 conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
 conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "BRANCH_PURCHASE"));
 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
 salesOrderList = delegator.findList("OrderHeaderAndRoles", condition,UtilMisc.toSet("orderId","entryDate","externalId","partyId"), null, null, false);  
 ROOT_ID = "NHDC"; //::TODO::
 SortedMap DataMap = new TreeMap();
	if(salesOrderList){
			 salesOrderList.each{eachItem ->
	
			 partyId = eachItem.getAt("partyId");
			 orderId = eachItem.getAt("orderId");
			 poentryDate = eachItem.getAt("entryDate");
			 BigDecimal diffMinutes= BigDecimal.ZERO;
			 String toOrderId=null;
			 orderAssocs = delegator.findList("OrderAssoc", (EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId)),UtilMisc.toSet("toOrderId"), null, null, false);
			 if(UtilValidate.isNotEmpty(orderAssocs)){
			 	 orderAssoc =EntityUtil.getFirst(orderAssocs);
				 toOrderId=orderAssoc.toOrderId;
				 orderHeader = delegator.findOne("OrderHeader", [orderId : toOrderId], false);
				 indentDate = orderHeader.entryDate;
				 long timeDiff = poentryDate.getTime() - indentDate.getTime();
				 BigDecimal diffHours = timeDiff / (60 * 60 * 1000);
				 BigDecimal modOfDiffHours = timeDiff % (60 * 60 * 1000);
				 diffMinutes = modOfDiffHours/ (60 * 1000);
				 String totalTime = diffHours.toString() + ":"+ diffMinutes.toString() + " Hrs";
			}	
			BigDecimal saleTotalTime = BigDecimal.ZERO;
			BigDecimal totalShipments = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(toOrderId)){
				orderItemBilling = delegator.findList("OrderItemBilling", (EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,toOrderId)),UtilMisc.toSet("invoiceId"), null, null, false);
				if(UtilValidate.isNotEmpty(orderItemBilling)){
					List invoiceIds=EntityUtil.getFieldListFromEntityList(orderItemBilling, "invoiceId", true);
					BigDecimal totalDiffMinutes= BigDecimal.ZERO;
					for(i=0; i<invoiceIds.size();i++){
						invoiceId = invoiceIds.get(i);
						invoice = delegator.findOne("Invoice", [invoiceId : invoiceId], true);
						invoiceDate=invoice.createdStamp;
						shipmentId = invoice.shipmentId;
						shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], true);
						//orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
						if(shipment){
							shipDate = shipment.createdDate;
							//indentDate = orderHeader.orderDate;
							long saleTimeDiff = invoiceDate.getTime() - shipDate.getTime();
							BigDecimal saleDiffHours = saleTimeDiff / (60 * 60 * 1000);
							BigDecimal modOfSaleDiffHours = saleTimeDiff % (60 * 60 * 1000);
							saleDiffMinutes = modOfSaleDiffHours/ (60 * 1000);
							saleTotalTime  = saleTotalTime+saleDiffMinutes;
						}
					}
				}
		    }
			shipmentList = delegator.findList("Shipment", (EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,orderId)),UtilMisc.toSet("shipmentId"), null, null, false);
			if(UtilValidate.isNotEmpty(shipmentList)){
				totalShipments=shipmentList.size();
			}
			roId = branchROMap.get(partyId);	
		 	if (DataMap.containsKey(partyId)) {
		 		branchDetails = DataMap.get(partyId);
		 		totalPos = branchDetails.get("totalPos");
		 		branchDetails.putAt("totalPos", ++totalPos);
				branchDetails.putAt("totalShipments", ++totalShipments);
				branchDetails.putAt("diffMinutes", diffMinutes+ branchDetails.get("diffMinutes"));
				branchDetails.putAt("saleTotalTime", saleTotalTime+ branchDetails.get("saleTotalTime"));
		 	}
		 	else {
		 		branchDetails = [:];
		 		branchDetails.putAt("totalPos", 1);
				branchDetails.putAt("totalShipments", 1);
				branchDetails.putAt("diffMinutes", diffMinutes);
				branchDetails.putAt("saleTotalTime", saleTotalTime);
		 		DataMap.putAt(partyId, branchDetails);		 	
		 	}
		 	if (DataMap.containsKey(roId)) {
		 		roDetails = DataMap.get(roId);
		 		totalPos = roDetails.get("totalPos");
				roDetails.putAt("totalPos", ++totalPos);
				roDetails.putAt("totalShipments", ++totalShipments);
			    roDetails.putAt("diffMinutes", diffMinutes+roDetails.get("diffMinutes"));
				roDetails.putAt("saleTotalTime", saleTotalTime+ branchDetails.get("saleTotalTime"));
				
		 	}
		 	else {
		 		roDetails = [:];
		 		roDetails.putAt("totalPos", 1);
				roDetails.putAt("totalShipments", 1);
				roDetails.putAt("diffMinutes", diffMinutes);
				roDetails.putAt("saleTotalTime", saleTotalTime);
		 		DataMap.putAt(roId, roDetails);	 	
		 	}	
		 	if (DataMap.containsKey(ROOT_ID)) {
		 		totDetails = DataMap.get(ROOT_ID);
		 		totalPos = totDetails.get("totalPos");
		 		totDetails.putAt("totalPos", ++totalPos);
				totDetails.putAt("totalShipments", ++totalShipments);
				totDetails.putAt("diffMinutes", diffMinutes+totDetails.get("diffMinutes"));
				totDetails.putAt("saleTotalTime", saleTotalTime+totDetails.get("saleTotalTime"));
		 	}
		 	else {
		 		totDetails = [:];
		 		totDetails.putAt("totalPos", 1);
				totDetails.putAt("totalShipments", 1);
				totDetails.putAt("diffMinutes", diffMinutes);
				totDetails.putAt("saleTotalTime", saleTotalTime);
		 		DataMap.putAt(ROOT_ID, totDetails);	 	
		 	}	
		}
	 }
 
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
				long durationMinits=entryValue.get("diffMinutes")/entryValue.get("totalPos");
				BigDecimal durationhrs=durationMinits / (1440);
				newObj.put("avgDuration", (durationhrs).setScale(0, 0));
				long saledurationMinits=entryValue.get("saleTotalTime")/entryValue.get("totalShipments");
				BigDecimal saledurationhrs=saledurationMinits / (1440);
				newObj.put("slaeAvgDuration", (saledurationhrs).setScale(0, 0));
			 }
			 else if (partyId == ROOT_ID) {
				newObj.put("partyId", ROOT_ID );
				newObj.put("branch", "");
				newObj.put("ReportsTo", "");
				newObj.put("ro", ROOT_ID);
				newObj.put("avgTAT","");
				long durationMinits=entryValue.get("diffMinutes")/entryValue.get("totalPos");
				BigDecimal durationhrs=durationMinits / (1440);
				newObj.put("avgDuration", (durationhrs).setScale(0, 0));
				long saledurationMinits=entryValue.get("saleTotalTime")/entryValue.get("totalShipments");
				BigDecimal saledurationhrs=saledurationMinits / (1440);
				newObj.put("slaeAvgDuration", (saledurationhrs/9).setScale(0, 0));
			 }
			 else {
				newObj.put("partyId", partyId );
				newObj.put("branch", "");
				newObj.put("ReportsTo", ROOT_ID);
				newObj.put("ro", partyIdNameMap.get(partyId));
				newObj.put("avgTAT","");
				long durationMinits=entryValue.get("diffMinutes")/entryValue.get("totalPos");
				BigDecimal durationhrs=durationMinits / (1440);
				newObj.put("avgDuration", (durationhrs).setScale(0, 0));
				long saledurationMinits=entryValue.get("saleTotalTime")/entryValue.get("totalShipments");
				BigDecimal saledurationhrs=saledurationMinits / (1440);
				newObj.put("slaeAvgDuration", (saledurationhrs).setScale(0, 0));
			 }
			 dataList.add(newObj);
	 }
context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess();
resultMap.put("data",dataList);
return resultMap;


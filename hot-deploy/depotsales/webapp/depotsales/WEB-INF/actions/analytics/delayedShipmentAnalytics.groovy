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
import org.ofbiz.party.party.PartyHelper;
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

days = parameters.days;

if(!days)
days = 0;

partyRoles = delegator.find("PartyRoleNameDetail", (EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,["EMPANELLED_SUPPLIER","UNEMPALED_SUPPLIER"])),null,UtilMisc.toSet("partyId","groupName"), null, null);
partyIds=EntityUtil.getFieldListFromEntityListIterator(partyRoles, "partyId", true);
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIds));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"ORDER_APPROVED"));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
if(UtilValidate.isNotEmpty(fromDate)){
	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderHeaderAndRoles = delegator.findList("OrderHeaderAndRoles", condition,UtilMisc.toSet("orderId","partyId"), null, null, false);

partyIdsFromOrders=EntityUtil.getFieldListFromEntityList(orderHeaderAndRoles, "partyId", true);


partynameMap = [:]as TreeMap;

partynameMapDetail = [:];

for (eachParty in partyIdsFromOrders) {
	partyName =  PartyHelper.getPartyName(delegator, eachParty, false);
	partynameMap.put(partyName.trim(), eachParty);
	partynameMapDetail.put(eachParty, partyName);
}


allParties = partynameMap.values();

SortedMap DataMap = new TreeMap();
Timestamp currentDate = UtilDateTime.nowTimestamp();


for (eachParty in allParties) {
	orderHeaderAndRole = EntityUtil.filterByCondition(orderHeaderAndRoles, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,eachParty));
	
	
	double peddingShipments = 0;
	if(orderHeaderAndRole){
	
	  for (eachList in orderHeaderAndRole) {
		
		  conditionList.clear();
		  conditionList.add(EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, eachList.orderId));
		  conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
		  condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		  List<String> payOrderBy = UtilMisc.toList("supplierInvoiceDate");
		  Shipment = delegator.findList("Shipment", condition,UtilMisc.toSet("shipmentId","supplierInvoiceDate"), payOrderBy, null, false);
		  
		  if(!Shipment){
			  peddingShipments = peddingShipments+1;
		  }else{
		  
		   if(days > 0){
		   ShipmentFirst = Shipment[0];
		   supplierInvoiceDate  = ShipmentFirst.supplierInvoiceDate;
		  
		   conditionList.clear();
		   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachList.orderId));
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		   condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		   OrderStatus = delegator.findList("OrderStatus", condition,UtilMisc.toSet("orderId","statusDatetime"), null, null, false);
 
		   
		   statusDatetime = OrderStatus[0].get("statusDatetime");
		  
		  long timeDiff = statusDatetime.getTime() - supplierInvoiceDate.getTime();
		  diffHours = timeDiff / (60 * 60 * 1000);
		  
		  int diffHours = diffHours.intValue();
		  
		  diffDays = diffHours/24;
		  
		  if(diffDays >= days){
			  
			  peddingShipments = peddingShipments+1;
		  }
		  
		   }
		  
		  }
		  
		  
		  
		  
		  
		  
	  }
	  
	  
	  if(peddingShipments > 0){
	  JSONObject newObj = new JSONObject();
	  //partyName =  PartyHelper.getPartyName(delegator, eachParty, false);
	  partyName = partynameMapDetail.get(eachParty);
	  
	  newObj.put("partyId", eachParty);
	  newObj.put("partyName", partyName);
	  newObj.put("pendingShip", peddingShipments);
	  dataList.add(newObj);
	  }
	
	}
	
}





/*if(orderHeaderAndRoles){
	orderHeaderAndRoles.each{eachItem ->
	orderId=eachItem.getAt("orderId");
	shipments = delegator.findList("Shipment", EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId),null, null, null, false);
	pendingShip = 0;
	if (UtilValidate.isEmpty(shipments)) {
		pendingShip = 1;
		orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
		indentDate = orderHeader.orderDate;
		long timeDiff = currentDate.getTime() - indentDate.getTime();
		diffHours = timeDiff / (60 * 60 * 1000);
		partyId = eachItem.getAt("partyId");
		if (DataMap.containsKey(partyId)) {
			branchDetails = DataMap.get(partyId);
			branchDetails.putAt("pendingShip", pendingShip + branchDetails.get("pendingShip"));
			branchDetails.putAt("pendingShipDays", diffHours + branchDetails.get("pendingShipDays"));
			
		}
		else {
			branchDetails = [:];
			branchDetails.putAt("pendingShip", pendingShip);
			branchDetails.putAt("pendingShipDays", diffHours);
			
			DataMap.putAt(partyId, branchDetails);
		}
		}
	}
}*/
/*for(Map.Entry entry : DataMap.entrySet()){
	partyId = entry.getKey();
	entryValue = entry.getValue();
	long durationMinits=entryValue.get("pendingShipDays")/entryValue.get("pendingShip");
	BigDecimal durationhrs=(durationMinits / (24)).setScale(0, 0);
	partyName =  PartyHelper.getPartyName(delegator, partyId, false);
	BigDecimal noDays=BigDecimal.ZERO;
	if(parameters.days){
	 noDays=new BigDecimal(parameters.days);
	}
	if (durationhrs.compareTo(noDays) > 0) {
	JSONObject newObj = new JSONObject();
	newObj.put("partyId", partyName);
	newObj.put("pendingShip", entryValue.get("pendingShip"));
	newObj.put("pendingShipDays", (durationhrs));
	dataList.add(newObj);
	}
}*/
context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess();
resultMap.put("data",dataList);
return resultMap;

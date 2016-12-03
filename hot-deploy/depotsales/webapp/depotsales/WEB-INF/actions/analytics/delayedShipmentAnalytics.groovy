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
partyRoles = delegator.find("PartyRole", (EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,["EMPANELLED_SUPPLIER","UNEMPALED_SUPPLIER"])),null,UtilMisc.toSet("partyId"), null, null);
partyIds=EntityUtil.getFieldListFromEntityListIterator(partyRoles, "partyId", true);
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_APPROVED"));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderHeaderAndRoles = delegator.findList("OrderHeaderAndRoles", condition,UtilMisc.toSet("orderId","entryDate","orderDate","externalId","partyId"), null, null, false);
SortedMap DataMap = new TreeMap();
Timestamp currentDate = UtilDateTime.nowTimestamp();
if(orderHeaderAndRoles){
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
}
for(Map.Entry entry : DataMap.entrySet()){
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
}
context.putAt("dataJSON",dataList);
Map resultMap = FastMap.newInstance();
resultMap = ServiceUtil.returnSuccess();
resultMap.put("data",dataList);
return resultMap;

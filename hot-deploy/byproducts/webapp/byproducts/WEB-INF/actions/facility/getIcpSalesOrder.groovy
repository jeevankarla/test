import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];

salesChannel = parameters.salesChannelEnumId;

orderList=[];
condList = [];
condList.add(EntityCondition.makeCondition("salesChannelEnumId" ,EntityOperator.EQUALS, salesChannel));
condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_CREATED")));
condList.add(EntityCondition.makeCondition("shipmentId" ,EntityOperator.EQUALS, null));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
orderHeader = delegator.findList("OrderHeader", cond, null, null, null ,false);

orderIds = EntityUtil.getFieldListFromEntityList(orderHeader, "orderId", true);
custCondList = [];
//give prefrence to ShipToCustomer
custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
shipCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
orderRoles = delegator.findList("OrderRole", shipCond, null, null, null, false);
if(UtilValidate.isEmpty(orderRoles)){
custCondList.clear();
custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
orderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
}

orderHeader.each{ eachHeader ->
	orderId = eachHeader.orderId;
	orderParty = EntityUtil.filterByCondition(orderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	partyId = "";
	if(orderParty){
		partyId = orderParty.get(0).get("partyId");
	}
	partyName = PartyHelper.getPartyName(delegator, partyId, false);
	tempData = [:];
	tempData.put("partyId", partyId);
	tempData.put("partyName", partyName);
	tempData.put("orderId", eachHeader.orderId);
	tempData.put("orderDate", eachHeader.estimatedDeliveryDate);
	tempData.put("statusId", eachHeader.statusId);
	orderList.add(tempData);
}
context.orderList = orderList;


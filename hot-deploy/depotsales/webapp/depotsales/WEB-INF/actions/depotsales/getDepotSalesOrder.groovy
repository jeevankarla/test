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
List<String> orderBy = UtilMisc.toList("-orderDate");
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
orderHeader = delegator.findList("OrderHeader", cond, null, orderBy, null ,false);

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

Set partyIdsSet=new HashSet();
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
	 if(UtilValidate.isNotEmpty(eachHeader.getBigDecimal("grandTotal"))){
		tempData.put("orderTotal", eachHeader.getBigDecimal("grandTotal"));
	  } 
	 creditPartRoleList=delegator.findByAnd("PartyRole", [partyId :partyId,roleTypeId :"CR_INST_CUSTOMER"]);
	 creditPartyRole = EntityUtil.getFirst(creditPartRoleList);
	 if(creditPartyRole) {
		 tempData.put("isCreditInstution", "Y");
	 }else{
	      tempData.put("isCreditInstution", "N");
		  partyIdsSet.add(partyId);
	 }
	orderList.add(tempData);
}

obDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(UtilDateTime.nowTimestamp()), 1));

partyOBMap=[:];
partyIdsSet.each{partyId->
	arPartyOB  =BigDecimal.ZERO;
		arOpeningBalanceRes = (org.ofbiz.accounting.ledger.GeneralLedgerServices.getGenericOpeningBalanceForParty( dctx , [userLogin: userLogin, tillDate:obDate, partyId:partyId]));
		if(UtilValidate.isNotEmpty(arOpeningBalanceRes)){
			arPartyOB=arOpeningBalanceRes.get("openingBalance");
		}
		//Debug.log("===============arPartyOB="+arPartyOB);
		if(UtilValidate.isNotEmpty(arPartyOB)&&(arPartyOB<0)){
			partyOBMap.put(partyId, arPartyOB *(-1));
		}else{
			partyOBMap.put(partyId, BigDecimal.ZERO);
		}
		
		
}
//Debug.log("===============partyOBMap="+partyOBMap+"==obDate=="+obDate);

context.orderList = orderList;
context.partyOBMap = partyOBMap;


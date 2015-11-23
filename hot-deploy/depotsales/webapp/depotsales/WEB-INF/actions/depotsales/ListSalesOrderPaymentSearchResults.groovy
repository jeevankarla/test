import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
searchOrderId = parameters.orderId;

facilityOrderId = parameters.orderId;
facilityDeliveryDate = parameters.estimatedDeliveryDate;
productId = parameters.productId;
facilityStatusId = parameters.statusId;
facilityPartyId = parameters.partyId;
facilityDateStart = null;
facilityDateEnd = null;
if(UtilValidate.isNotEmpty(facilityDeliveryDate)){
	def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		transDate = new java.sql.Timestamp(sdf.parse(facilityDeliveryDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + facilityDeliveryDate, "");
	}
	facilityDateStart = UtilDateTime.getDayStart(transDate);
	facilityDateEnd = UtilDateTime.getDayEnd(transDate);
}

orderList=[];
condList = [];
if(UtilValidate.isNotEmpty(searchOrderId)){
	condList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.LIKE, "%"+searchOrderId + "%"));
}
condList.add(EntityCondition.makeCondition("salesChannelEnumId" ,EntityOperator.EQUALS, salesChannel));



condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_CREATED")));
condList.add(EntityCondition.makeCondition("shipmentId" ,EntityOperator.EQUALS, null));
if(UtilValidate.isNotEmpty(facilityDeliveryDate)){
	condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, facilityDateStart));
	condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, facilityDateEnd));
}
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
	 if(UtilValidate.isNotEmpty(eachHeader.productSubscriptionTypeId)&&("CREDIT"==eachHeader.productSubscriptionTypeId) || creditPartyRole) {
		 tempData.put("isCreditInstution", "Y");
	 }else{
		  tempData.put("isCreditInstution", "N");
	 }
	partyIdsSet.add(partyId);
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
		if(arPartyOB<0){
		partyOBMap.put(partyId, arPartyOB *(-1));
		}else{
		
		partyOBMap.put(partyId, BigDecimal.ZERO);
		}
		
		
}
//Debug.log("===============partyOBMap="+partyOBMap+"==obDate=="+obDate);





finalFilteredList = []as LinkedHashSet;

for (eachOrderList in orderList) {
	 
	if(UtilValidate.isNotEmpty(facilityPartyId)){
	 
		 if(facilityPartyId.equals(eachOrderList.get("partyId"))){
			 
			 finalFilteredList.add(eachOrderList);
		 }
		 
	}
	if(UtilValidate.isNotEmpty(facilityOrderId)){
		
		if(facilityOrderId.equals(eachOrderList.get("orderId"))){
			
			finalFilteredList.add(eachOrderList);
		}
		
	}
	if(UtilValidate.isNotEmpty(facilityStatusId)){
		
		if(facilityStatusId.equals(eachOrderList.get("statusId"))){
			
			finalFilteredList.add(eachOrderList);
		}
		
	}
	if(UtilValidate.isEmpty(facilityPartyId) && UtilValidate.isEmpty(facilityOrderId) && UtilValidate.isEmpty(facilityStatusId)){
		
		finalFilteredList.add(eachOrderList);
	}
}

   orderIdsList = [];
   
   orderPreferenceMap = [:];
   
   paymentSatusMap = [:];
   
   for (eachList in finalFilteredList) {

	   condtList = [];
	   condtList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, eachList.orderId));
	   cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
	   OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
	   getFirstOrderPayment = EntityUtil.getFirst(OrderPaymentPreference);
	   
	   orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
	   
	  if(UtilValidate.isNotEmpty(orderPreferenceIds)){
	   
	   orderPreferenceMap.put(eachList.orderId, getFirstOrderPayment.get("orderPaymentPreferenceId"))
	   conditonList = [];
	   conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN, orderPreferenceIds));
	   cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	   PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
	   
	   totAmount = 0;
	   
	   if(UtilValidate.isNotEmpty(PaymentList)){
	   
	   for (eachpayment in PaymentList) {
		   totAmount = totAmount+eachpayment.get("amount");
		  }
	   
			tempMap = [:];
			
			tempMap.put("statusId", PaymentList[0].statusId);
			
			tempMap.put("amount", totAmount);
			
	   
	   paymentSatusMap.put(eachList.orderId, tempMap);
	   
	   }
	   
	  }
}
   
   
   
   condtList = [];
   condtList.add(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS, "MONEY"));
   cond = EntityCondition.makeCondition(condtList, EntityOperator.AND);
   PaymentMethodType = delegator.findList("PaymentMethodType", cond, UtilMisc.toSet("paymentMethodTypeId","description"), null, null ,false);

   
   context.PaymentMethodType = PaymentMethodType;
   
   context.orderPreferenceMap = orderPreferenceMap;
   context.paymentSatusMap = paymentSatusMap;

context.orderList = finalFilteredList;
context.partyOBMap = partyOBMap;


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
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;


dctx = dispatcher.getDispatchContext();
Map boothsPaymentsDetail = [:];

partyId = userLogin.get("partyId");


resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));


Map formatMap = [:];
List formatList = [];
List productStoreList = resultCtx.get("productStoreList");
context.productStoreList = productStoreList;

for (eachList in productStoreList) {
	formatMap = [:];
	formatMap.put("productStoreName",eachList.get("storeName"));
	formatMap.put("payToPartyId",eachList.get("payToPartyId"));
	formatList.addAll(formatMap);
}
context.formatList = formatList;

branchList = EntityUtil.getFieldListFromEntityList(productStoreList, "payToPartyId", true);
if(UtilValidate.isNotEmpty(parameters.partyIdFrom)){
	branchList.clear();
	branchList.add(parameters.partyIdFrom)
}

//branchId = parameters.partyIdFrom;

salesChannel = parameters.salesChannelEnumId;


uniqueOrderId = parameters.uniqueOrderId;

/*
JSONObject personalDetailMap = null;
if (UtilValidate.isNotEmpty(uniqueOrderId)) {
	personalDetailMap = new JSONObject();
	personalDetailMap = (JSONObject) JSONSerializer.toJSON(uniqueOrderId);
}*/

uniqueOrderIdsList = Eval.me(uniqueOrderId)


Debug.log("uniqueOrderIdsList==============="+uniqueOrderIdsList);



searchOrderId = parameters.orderId;

facilityOrderId = parameters.orderId;
facilityDeliveryDate = parameters.estimatedDeliveryDate;
productId = parameters.productId;
facilityStatusId = parameters.statusId;
facilityPartyId = parameters.partyId;
 screenFlag = parameters.screenFlag;

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

JSONArray orderList=new JSONArray();
condList = [];

inputFields = [:];
inputFields.put("noConditionFind", "Y");
inputFields.put("hideSearch","Y");


if(UtilValidate.isNotEmpty(searchOrderId)){
	condList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.LIKE, "%"+searchOrderId + "%"));
}
if(UtilValidate.isNotEmpty(uniqueOrderIdsList)){
	condList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.NOT_IN, uniqueOrderIdsList));
}
if(UtilValidate.isNotEmpty(facilityStatusId)){
	condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.EQUALS, facilityStatusId));
}
else{
	condList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_CREATED")));
}
	condList.add(EntityCondition.makeCondition("purposeTypeId" ,EntityOperator.EQUALS, "BRANCH_SALES"));
	condList.add(EntityCondition.makeCondition("shipmentId" ,EntityOperator.EQUALS, null)); // Review
if(UtilValidate.isNotEmpty(facilityDeliveryDate)){
	condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, facilityDateStart));
	condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, facilityDateEnd));
	
}


if(parameters.indentDateSort)
dateSort = parameters.indentDateSort;
else
dateSort = "-orderDate";

cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
List<String> payOrderBy = UtilMisc.toList(dateSort,"-orderId");
result = delegator.find("OrderHeader", cond, null, null, payOrderBy, null);


orderHeader = result.getPartialList(Integer.valueOf(parameters.low),Integer.valueOf(parameters.high));

orderIds=EntityUtil.getFieldListFromEntityList(orderHeader, "orderId", true);

result.close();
/*
forTotalresult = null;
forTotalresult = dispatcher.runSync("performFind", UtilMisc.toMap("entityName", "OrderHeader", "inputFields", inputFields, "userLogin", userLogin));
listItr = forTotalresult.listIt;
forTotalorderHeader = result.getPartialList(0,listItr.size());
forTOTcondList = [];
forTOTcondList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_CREATED")));
forToTcond = EntityCondition.makeCondition(forTOTcondList, EntityOperator.AND);
forTotalorderHeaderList = EntityUtil.filterByCondition(forTotalorderHeader, forToTcond);

totIndents = forTotalorderHeaderList.size();
listItr.close();*/



custCondList = [];
//give preference to ShipToCustomer
custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
if(UtilValidate.isNotEmpty(facilityPartyId)){
	custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facilityPartyId));
}
custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
shipCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
orderRoles = delegator.findList("OrderRole", shipCond, null, null, null, false);
if(UtilValidate.isEmpty(orderRoles)){
	custCondList.clear();
	custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
	if(UtilValidate.isNotEmpty(facilityPartyId)){
		custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, facilityPartyId));
	}
	custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
	custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
	orderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
}

customerBasedOrderIds = EntityUtil.getFieldListFromEntityList(orderRoles, "orderId", true);
orderHeader = EntityUtil.filterByCondition(orderHeader, EntityCondition.makeCondition("orderId", EntityOperator.IN, customerBasedOrderIds));

	
custCondList.clear();
custCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
// query based on branch
if(UtilValidate.isNotEmpty(branchList)){
	custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
}
custCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
billFromVendorOrderRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(custCondList, EntityOperator.AND), null, null, null, false);

vendorBasedOrderIds = EntityUtil.getFieldListFromEntityList(billFromVendorOrderRoles, "orderId", true);
orderHeader = EntityUtil.filterByCondition(orderHeader, EntityCondition.makeCondition("orderId", EntityOperator.IN, vendorBasedOrderIds));

orderDetailsMap=[:];


JSONObject eachPaymentOrderMap = new JSONObject();


Set partyIdsSet=new HashSet();
orderHeader.each{ eachHeader ->
	orderId = eachHeader.orderId;
	orderParty = EntityUtil.filterByCondition(orderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	partyId = "";
	if(orderParty){
		partyId = orderParty.get(0).get("partyId");
	}
	
	billFromOrderParty = EntityUtil.filterByCondition(billFromVendorOrderRoles, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	billFromVendorPartyId = "";
	if(billFromOrderParty){
		billFromVendorPartyId = billFromOrderParty.get(0).get("partyId");
	}
	
	partyName = PartyHelper.getPartyName(delegator, partyId, false);
	JSONObject tempData = new JSONObject();
	tempData.put("partyId", partyId);
	tempData.put("billFromVendorPartyId", billFromVendorPartyId);
	tempData.put("partyName", partyName);
	orderNo ="NA";
	orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , eachHeader.orderId)  , null, null, null, false );
	if(UtilValidate.isNotEmpty(orderHeaderSequences)){
		orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
		orderNo = orderSeqDetails.orderNo;
	}
	tempData.put("orderNo", orderNo);
	tempData.put("orderId", eachHeader.orderId);
	tempData.put("orderDate", String.valueOf(eachHeader.estimatedDeliveryDate).substring(0,10));
	tempData.put("statusId", eachHeader.statusId);
	
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachHeader.orderId));
	orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	double adjAmout = 0;
	for (eachAdjustment in orderAdjustments) {
		adjAmout = adjAmout+eachAdjustment.amount;
	}
	
	double grandTotWithAdj = 0;
	grandTotWithAdj = Double.valueOf(eachHeader.getBigDecimal("grandTotal"))+adjAmout;
	
	if(UtilValidate.isNotEmpty(eachHeader.getBigDecimal("grandTotal"))){
		tempData.put("orderTotal", eachHeader.getBigDecimal("grandTotal"));
	}
	/*creditPartRoleList=delegator.findByAnd("PartyRole", [partyId :partyId,roleTypeId :"CR_INST_CUSTOMER"]);
	creditPartyRole = EntityUtil.getFirst(creditPartRoleList);
	if(UtilValidate.isNotEmpty(eachHeader.productSubscriptionTypeId)&&("CREDIT"==eachHeader.productSubscriptionTypeId) || creditPartyRole) {
		tempData.put("isCreditInstution", "Y");
	}else{
		tempData.put("isCreditInstution", "N");
	}*/
	partyIdsSet.add(partyId);
	
	
	isgeneratedPO="N";
	// Also check if associated order is cancelled. If cancelled show generate PO button
	/*exprCondList=[];
	exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
	exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
	OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
	if(OrderAss){
		isgeneratedPO="Y";
	}*/
	
	/*exprList=[];
	exprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
	EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	supplierPartyId="";
	productStoreId="";
	supplierDetails = EntityUtil.getFirst(delegator.findList("OrderRole", discontinuationDateCondition, null,null,null, false));
		
	if(supplierDetails){
		supplierPartyId=supplierDetails.get("partyId");
	}
*/	productStoreId=eachHeader.productStoreId;
	tempMap=[:];
//	tempMap.put("supplierPartyId", supplierPartyId);
	//tempMap.put("isgeneratedPO", isgeneratedPO);
	/*supplierPartyName="";
	if(supplierPartyId){
		supplierPartyName = PartyHelper.getPartyName(delegator, supplierPartyId, false);
	}
	tempMap.put("supplierPartyName", supplierPartyName);
	tempMap.put("productStoreId", productStoreId);
*/	orderDetailsMap.put(orderId,tempMap);
	
		
	conditonList = [];
	conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, orderId));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
	double paidAmt = 0;
	
	paymentIdsOfIndentPayment = [];
	
	if(OrderPaymentPreference){
	
	orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
 
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN,orderPreferenceIds));
	conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
	
	paymentIdsOfIndentPayment = EntityUtil.getFieldListFromEntityList(PaymentList,"paymentId", true);
	
	
	for (eachPayment in PaymentList) {
		paidAmt = paidAmt+eachPayment.get("amount");
	}
	
  }
	
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,orderId));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	OrderItemBillingList = delegator.findList("OrderItemBilling", cond, null, null, null ,false);
	
	invoiceIds = EntityUtil.getFieldListFromEntityList(OrderItemBillingList,"invoiceId", true);
	
	if(invoiceIds){
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("invoiceId" ,EntityOperator.IN,invoiceIds));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	PaymentApplicationList = delegator.findList("PaymentApplication", cond, null, null, null ,false);
	
		for (eachList in PaymentApplicationList) {
			 if(!paymentIdsOfIndentPayment.contains(eachList.paymentId))
				paidAmt = paidAmt+eachList.amountApplied;
		}
	}
	
	tempData.put("paidAmt", paidAmt);
	grandTOT = eachHeader.getBigDecimal("grandTotal");
	balance = grandTOT-paidAmt;
	tempData.put("balance", balance);
	
	orderList.add(tempData);
	
}



/*sortedOrderMap =  [:]as TreeMap;
for (eachList in orderList) {
	sortedOrderMap.put(eachList.orderId, eachList);
}
NavigableSet nset=sortedOrderMap.descendingKeySet();
	
	List basedList = [];
	for (eachKey in nset){
		basedList.add(sortedOrderMap.getAt(eachKey));
		
	}
	
	Debug.log("===================== basedList=====" +basedList.size());*/
request.setAttribute("orderList", orderList);
return "success";



//context.partyOBMap = partyOBMap;
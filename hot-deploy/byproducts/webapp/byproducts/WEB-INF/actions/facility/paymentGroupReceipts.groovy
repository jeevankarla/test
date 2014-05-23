
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

int decimals;
int rounding;

dctx = dispatcher.getDispatchContext();
decimals = 0;//UtilNumber.getBigDecimalScale("order.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

reportTypeFlag=parameters.reportTypeFlag;
unDepositedCheques=parameters.unDepositedCheques;
allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}
List exprList = [];
conditionList=[];
boothRouteIdsMap = [:];

bankPaidMap=[:];

List routeCheckListReportList = [];
List nonRouteCheckListReportList = [];
paymentIdsList=[];

	paymentDate=parameters.paymentDate;
	paymentMethodTypeId = parameters.paymentMethodTypeId;
	fromDateTime=UtilDateTime.nowTimestamp();
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(paymentDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+paymentDate, "");
	}
	context.paymentDate=fromDateTime;
	dayStart = UtilDateTime.getDayStart(fromDateTime);
	dayEnd = UtilDateTime.getDayEnd(fromDateTime);
	
	routeIds=[];
	List<GenericValue> paymentsList = FastList.newInstance();
	
	facilityIdsList=[];
		boothRouteResultMap = ByProductNetworkServices.getBoothsRouteByShipment(delegator,UtilMisc.toMap("facilityId",null,"effectiveDate",dayStart));
		if(UtilValidate.isNotEmpty(boothRouteResultMap)){
			boothRouteIdsMap=(Map)boothRouteResultMap.get("boothRouteIdsMap");//to get routeIds
		}
	   exprList.clear();
		exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart), EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
		exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
		if (!UtilValidate.isEmpty(paymentMethodTypeId)) {
			exprList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, paymentMethodTypeId));
		}
		EntityCondition condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
		boothTempPaymentsList = delegator.findList("Payment", condition, null, ["paymentId"], null, false);
		
		routeCheckListReportList = EntityUtil.filterByCondition(boothTempPaymentsList, EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "ROUTE_MKTG"));
		nonRouteCheckListReportList = EntityUtil.filterByCondition(boothTempPaymentsList, EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "NON_ROUTE_MKTG"));
		paymentIdList = EntityUtil.getFieldListFromEntityList(routeCheckListReportList, "paymentId", true);
		paymentIdsList.addAll(paymentIdList);

partyIdFrom = "";
amount = "";
paymentId = "";

paymentGrpMap = [:];
paymentGrpFacMap=[:];
paymentGrpPartyMap=[:];
nonGroupPaymentIdsList=[];
nonGroupPaymentsList=[];
condList =[];

nonGrpPaymentIdsList=[];
groupPaymentIdsList=[];
if(UtilValidate.isNotEmpty(paymentIdsList)){
condList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN,paymentIdsList));
EntityCondition cond = EntityCondition.makeCondition(condList ,EntityOperator.AND);
paymentGroupMemberList = delegator.findList("PaymentGroupMember", cond, null, null, null, false);
groupPaymentIdsList=EntityUtil.getFieldListFromEntityList(paymentGroupMemberList, "paymentId", true);

nonGrpPayCondList =[];
if(UtilValidate.isNotEmpty(groupPaymentIdsList)){
nonGrpPayCondList.add(EntityCondition.makeCondition("paymentId", EntityOperator.NOT_IN,groupPaymentIdsList));
}
nonGrpPayCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart), EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
nonGrpPayCondList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS,"SALES_PAYIN"));
if (!UtilValidate.isEmpty(paymentMethodTypeId)) {
	nonGrpPayCondList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, paymentMethodTypeId));
}
nonGrpPayCondList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
EntityCondition nonPayCondition = EntityCondition.makeCondition(nonGrpPayCondList ,EntityOperator.AND);
paymentList = delegator.findList("Payment", nonPayCondition	, null, null, null, false);
nonGroupPaymentIdsList=EntityUtil.getFieldListFromEntityList(paymentList, "paymentId", true);

if(UtilValidate.isNotEmpty(paymentGroupMemberList)){
	paymentGroupIdList = EntityUtil.getFieldListFromEntityList(paymentGroupMemberList, "paymentGroupId", true);
	paymentGroupIdList.each{ paymentGroupId->
		paymentMap = [:];
		facilityPartyMap=[:];
		GenericValue paymentGroup = delegator.findOne("PaymentGroup", UtilMisc.toMap("paymentGroupId" ,paymentGroupId), false);
		
		paymentDate="";
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("paymentGroupId", EntityOperator.EQUALS,paymentGroupId));
		EntityCondition payGrpcondition = EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
		newPaymentGroupMemberList = delegator.findList("PaymentGroupMember", payGrpcondition, null, null, null, false);
		grpPaymentIdList = EntityUtil.getFieldListFromEntityList(newPaymentGroupMemberList, "paymentId", true);
		payCondList =[];
		payCondList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN,grpPaymentIdList));
		payCondList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS,"SALES_PAYIN"));
		payCondList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
		EntityCondition payCondition = EntityCondition.makeCondition(payCondList ,EntityOperator.AND);
		paymentList = delegator.findList("Payment", payCondition, null, ["paymentId"], null, false);
		amount = 0;
		cashReceivedMap = [:];
		if(UtilValidate.isNotEmpty(paymentList)){
			paymentList.each{ payment->
					amount += payment.amount;
					}
			if(UtilValidate.isNotEmpty(paymentGroup)){
				routeId=paymentGroup.facilityId;
				if(UtilValidate.isNotEmpty(routeId)){
				facilityPartyMap=ByProductNetworkServices.getFacilityPartyContractor(dispatcher.getDispatchContext(), UtilMisc.toMap("facilityId",routeId,"saleDate",paymentDate)).get("facilityPartyMap");
				//to populate Contrcter And RouteId
				paymentGrpPartyMap[paymentGroupId]=facilityPartyMap.get(routeId);
				paymentGrpFacMap[paymentGroupId]=routeId;
				cashReceivedMap["partyIdFrom"]=facilityPartyMap.get(routeId);
				cashReceivedMap["routeId"]=routeId;
				}
				}
			cashReceivedMap["amount"]=amount;
			paymentGrpMap.put(paymentGroupId,cashReceivedMap);
		}
	}
}

if(UtilValidate.isNotEmpty(nonGroupPaymentIdsList)){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.IN,nonGroupPaymentIdsList));
	conditionList.add(EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "ROUTE_MKTG"));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("paymentDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart), EntityOperator.AND, EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS,"SALES_PAYIN"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,UtilMisc.toList("PMNT_VOID","PMNT_CANCELLED")));
	EntityCondition nonGrpCondition = EntityCondition.makeCondition(conditionList ,EntityOperator.AND);
	nonGroupPaymentsList = delegator.findList("Payment", nonGrpCondition, null, ["paymentId"], null, false);
	
}
}
context.put("nonGroupPaymentsList",nonGroupPaymentsList);
context.put("paymentGrpMap",paymentGrpMap);
context.put("nonRouteCheckListReportList",nonRouteCheckListReportList);

context.bankPaidMap=bankPaidMap;
context.boothRouteIdsMap=boothRouteIdsMap;


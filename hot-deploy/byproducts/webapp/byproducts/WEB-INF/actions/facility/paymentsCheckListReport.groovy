
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
allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}
List exprList = [];
List checkListReportList = [];
boothRouteIdsMap = [:];
if(UtilValidate.isEmpty(reportTypeFlag)){
   dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
	exprList.add(EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
		EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	   ], EntityOperator.OR));
   if (!allChanges) {
	   exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
   }
   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
   checkListItemList = delegator.findList("Payment", condition, null, ["lastModifiedDate"], null, false);
   boothRouteResultMap = ByProductNetworkServices.getBoothsRouteByShipment(delegator,UtilMisc.toMap("facilityId",null,"effectiveDate",dayBegin));
   if(UtilValidate.isNotEmpty(boothRouteResultMap)){
	   boothRouteIdsMap=(Map)boothRouteResultMap.get("boothRouteIdsMap");//to get routeIds
   }
  
   checkListItemList.each { checkListItem -> 
	   lastPaymentMap = [:];
	   lastPaymentMap["lastModifiedDate"] = UtilDateTime.toDateString(checkListItem.lastModifiedDate, "HH:mm:ss");
	   lastPaymentMap["boothId"] = checkListItem.facilityId;
	   if (checkListItem.facilityId) {
		   //facility = delegator.findOne("Facility",[facilityId : checkListItem.facilityId], false);
		   //if (facility) {
			   lastPaymentMap["routeId"] = boothRouteIdsMap.get(checkListItem.facilityId);
		   //}
	   }
	   lastPaymentMap["lastModifiedBy"] = checkListItem.lastModifiedByUserLogin;
	   lastPaymentMap["amount"] = (new BigDecimal(checkListItem.amount)).setScale(0 ,rounding);
	   checkListReportList.add(lastPaymentMap);
   }
}
context.checkListReportList = checkListReportList;
//report invoking for Daily checklist report

boothPaymentCheckMap=[:];
bankPaidMap=[:];


if(UtilValidate.isNotEmpty(reportTypeFlag) && "DailyPaymentCheckList".equals(reportTypeFlag)){
paymentDate=parameters.paymentDate;
paymentMethodTypeId = parameters.paymentMethodTypeId;
fromDateTime=UtilDateTime.nowTimestamp();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(paymentDate+" 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+paymentDate, "");
}
dayStart = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(fromDateTime);

routeIds=[];
List<GenericValue> paymentsList = FastList.newInstance();
conditionList=[];
facilityIdsList=[];
	if(parameters.routeId !="All"){
		facilityIdsList =ByProductNetworkServices.getRouteBooths(delegator, parameters.routeId);
	}else{
	facilityIdsList=ByProductNetworkServices.getAllBooths(delegator,null).get("boothsList");
	}
	paidPaymentInput=[:];
	paidPaymentInput["fromDate"]=dayStart;
	paidPaymentInput["thruDate"]=dayEnd;
	paidPaymentInput["paymentMethodTypeId"]=paymentMethodTypeId;
	paidPaymentInput["facilityIdsList"]=facilityIdsList;
	paidPaymentInput["orderByBankName"]=true;
	
	if(UtilValidate.isNotEmpty(paymentMethodTypeId) && (paymentMethodTypeId=="CHEQUE_PAYIN")){
		paidPaymentInput["findByInstrumentDate"]=true;
	}
	boothPaidDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , paidPaymentInput);
	boothTempPaymentsList = boothPaidDetail["paymentsList"];
	boothRouteIdsMap= boothPaidDetail["boothRouteIdsMap"];

	boothTempPaymentsList.each{eachBoothPayment->
			if(UtilValidate.isNotEmpty(eachBoothPayment.issuingAuthority)){
				bankName=eachBoothPayment.issuingAuthority;
					if(UtilValidate.isEmpty(bankPaidMap.get(bankName))){
					List<GenericValue> tempboothPaidList=FastList.newInstance();
					tempboothPaidList.add(eachBoothPayment);
					bankPaidMap[bankName]=tempboothPaidList;
					}else{
					List<GenericValue> tempboothPaidList=FastList.newInstance();
					tempboothPaidList=bankPaidMap.get(bankName);
					tempboothPaidList.add(eachBoothPayment);
					bankPaidMap[bankName]=tempboothPaidList;
					}
			}else{
				if(UtilValidate.isEmpty(bankPaidMap.get("noBankName"))){
				List<GenericValue> tempboothPaidListnew=FastList.newInstance();
				tempboothPaidListnew.add(eachBoothPayment);
				bankPaidMap["noBankName"]=tempboothPaidListnew;
				}else{
				List<GenericValue> tempboothPaidList=FastList.newInstance();
				tempboothPaidList=bankPaidMap.get("noBankName");
				tempboothPaidList.add(eachBoothPayment);
				bankPaidMap["noBankName"]=tempboothPaidList;
				}
			}
	}
}

List routeCheckListReportList = [];
List nonRouteCheckListReportList = [];
if(UtilValidate.isNotEmpty(reportTypeFlag) && "CashPaymentCheckList".equals(reportTypeFlag)){
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
	conditionList=[];
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
			boothTempPaymentsList = delegator.findList("Payment", condition, null, ["facilityId"], null, false);
		routeCheckListReportList = EntityUtil.filterByCondition(boothTempPaymentsList, EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "ROUTE_MKTG"));
		nonRouteCheckListReportList = EntityUtil.filterByCondition(boothTempPaymentsList, EntityCondition.makeCondition("paymentPurposeType", EntityOperator.EQUALS, "NON_ROUTE_MKTG"));
	}
context.routeCheckListReportList=routeCheckListReportList;
context.nonRouteCheckListReportList=nonRouteCheckListReportList;
context.bankPaidMap=bankPaidMap;

context.boothRouteIdsMap=boothRouteIdsMap;

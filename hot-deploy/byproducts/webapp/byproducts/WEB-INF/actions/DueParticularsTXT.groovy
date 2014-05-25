import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.price.PriceServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

dctx=dispatcher.getDispatchContext();
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);
	context.fromDateTime = fromDateTime;
	context.thruDateTime = thruDateTime;
}

/*if(UtilValidate.isNotEmpty(parameters.saleDate)){
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	try {
		fromDateTime=new java.sql.Timestamp(sdf.parse(parameters.saleDate).getTime());
		thruDateTime=new java.sql.Timestamp(sdf.parse(parameters.saleDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.fromDate, "");
	}
	dayBegin = UtilDateTime.getDayStart(fromDateTime,-1, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime ,-1, timeZone, locale);
	context.saleDate = fromDateTime;
	context.supplyDate = dayBegin;
}*/
/*conditionList=[];
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL , null));
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL, "2093"));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN , UtilMisc.toList("S1074","S1187")));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
boothsList = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);*/
boothsList=ByProductNetworkServices.getAllBooths(delegator,null).get("boothsList");

boothTotalsWithReturn=[:];
periodBoothTotals=[:];
List shipmentIds = ByProductNetworkServices.getAllShipmentIds(delegator, dayBegin, dayEnd);//include Adhoc SALE
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	boothTotalsWithReturn = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds,facilityIds:UtilMisc.toList(boothsList),fromDate:dayBegin, thruDate:dayEnd,includeReturnOrders:true]);
}
if(UtilValidate.isNotEmpty(boothTotalsWithReturn)){
	boothTotals=boothTotalsWithReturn.get("boothTotals");
}

List<GenericValue> paymentsList = FastList.newInstance();
conditionList=[];
facilityIdsList=[];
	paidPaymentInput=[:];
	paidPaymentInput["fromDate"]=dayBegin;
	paidPaymentInput["thruDate"]=dayEnd;
	paidPaymentInput["paymentMethodTypeId"]="CASH_PAYIN";
	paidPaymentInput["facilityIdsList"]=boothsList;
	
	boothPaidDetail=[:];
	//Lets find each type of payment
	boothCashPaidDetail = ByProductNetworkServices.getBoothPaidPayments( dctx , paidPaymentInput);
	boothCashPaymentsList = boothCashPaidDetail["boothPaymentsList"];
	boothCashRouteIdsMap= boothCashPaidDetail["boothRouteIdsMap"];
	
	paidPaymentInput["paymentMethodTypeId"]="CHEQUE_PAYIN";
	boothChequePaidDetail=ByProductNetworkServices.getBoothPaidPayments( dctx , paidPaymentInput);
	boothChequePaymentsList = boothChequePaidDetail["boothPaymentsList"];
	boothChequeRouteIdsMap= boothChequePaidDetail["boothRouteIdsMap"];
	
	paidPaymentInput["paymentMethodTypeId"]="CHALLAN_PAYIN";
	boothChallanPaidDetail=ByProductNetworkServices.getBoothPaidPayments( dctx , paidPaymentInput);
	boothChallanPaymentsList = boothChallanPaidDetail["boothPaymentsList"];
	boothChallanRouteIdsMap= boothChallanPaidDetail["boothRouteIdsMap"];
	
categoryTotalMap = [:];
categorysList = [];
categorysParloursList = [];
Iterator boothTotIter = boothTotals.entrySet().iterator();
while (boothTotIter.hasNext()) {
	Map.Entry boothEntry = boothTotIter.next();
	boothId = boothEntry.getKey();
	BigDecimal totalRevenue=BigDecimal.ZERO;
	totalRevenue=boothEntry.getValue().getAt("totalRevenue");
    cashAmount=0;
    chequeAmount=0;
    challanAmount=0;
	boothCashPaymentsList.each{boothPayment->
		if(boothId==boothPayment.get("facilityId")){
			cashAmount+=boothPayment.get("amount");
		}
	}
	boothChequePaymentsList.each{boothPayment->
		if(boothId==boothPayment.get("facilityId")){
			chequeAmount+=boothPayment.get("amount");
		}
	}
	boothChallanPaymentsList.each{boothPayment->
		if(boothId==boothPayment.get("facilityId")){
			challanAmount+=boothPayment.get("amount");
		}
	}
	BigDecimal invoiceAmount = totalRevenue;
	BigDecimal totalPaidAmnt=(cashAmount+chequeAmount+challanAmount);
	BigDecimal netAmount =(BigDecimal) invoiceAmount.subtract(totalPaidAmnt);
   boothTotalsMap=[:];
   boothTotalsMap.put("facilityId", boothId);
   boothTotalsMap.put("invoiceAmount", invoiceAmount);
   boothTotalsMap.put("cashAmount", cashAmount);
   boothTotalsMap.put("chequeAmount", chequeAmount);
   boothTotalsMap.put("challanAmount", challanAmount);
   boothTotalsMap.put("chequeRetnAmount", 0);
   boothTotalsMap.put("netAmount", netAmount);
   
	facility = delegator.findOne("Facility",[facilityId : boothId], false);
	categoryType = facility.categoryTypeEnum;
		if(categoryTotalMap.containsKey(categoryType)){
		tempCatList = categoryTotalMap.get(categoryType);
		tempCatList.addAll(boothTotalsMap);
		categoryTotalMap.putAt(categoryType, tempCatList);
	}else{
		tempList = [];
		tempList.add(boothTotalsMap);
		categoryTotalMap.putAt(categoryType, tempList);
		categorysList.add(categoryType);
	}
}

categorysParloursList.add("PARLOUR");
categorysParloursList.add("PARLOUR_EX");
context.categoryTotalMap = categoryTotalMap;
if(parloursOnly == "Y"){
	context.categorysList = categorysParloursList;
}else{
	context.categorysList = categorysList;
}





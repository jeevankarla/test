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
import org.ofbiz.base.util.Debug;

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

isByParty = Boolean.TRUE;
fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
categoryTypeEnum=parameters.categoryTypeEnum;
context.categoryTypeEnum = categoryTypeEnum;
fromDateTime=UtilDateTime.nowTimestamp();
thruDateTime=UtilDateTime.nowTimestamp();

if (UtilValidate.isNotEmpty(fromDateStr)) {
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		fromDateTime = new java.sql.Timestamp(sdf.parse(fromDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + fromDateStr, "");
	}
}
if (UtilValidate.isNotEmpty(thruDateStr)) {
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		thruDateTime = new java.sql.Timestamp(sdf.parse(thruDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruDateStr, "");
	}
}
boothsList=[];
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDateTime = fromDateTime;
context.thruDateTime = thruDateTime;

/*conditionList=[];
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL , null));
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL, "2093"));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN , UtilMisc.toList("S1174","S1169","B30101","B30202")));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
boothsList = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);*/

boothsList=ByProductNetworkServices.getAllBooths(delegator,categoryTypeEnum).get("boothsList");
boothTotals=[:];
returnBoothTotals=[:];
boothTotalsWithReturn=[:];
periodBoothTotals=[:];
List shipmentIds = ByProductNetworkServices.getAllShipmentIds(delegator, dayBegin, dayEnd);//include Adhoc SALE

boothTotals = ByProductNetworkServices.getByProductDayWiseInvoiceTotals(dctx, UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd, "facilityList", boothsList, "userLogin", userLogin, "isByParty", isByParty)).get("boothInvoiceTotalMap");
inputMap = [];
penaltyResult = ByProductNetworkServices.getChequePenaltyTotals(dctx, UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd,"facilityList", boothsList, "userLogin", userLogin));
facilityPenaltyMap = penaltyResult.get("facilityPenalty");
returnPaymentReferences = penaltyResult.get("returnPaymentReferences");
facilityPenaltyPaymentIdsMap= penaltyResult.get("facilityPenaltyPaymentIdsMap");
List<GenericValue> paymentsList = FastList.newInstance();
conditionList=[];
facilityIdsList=[];

boothPaidDetail=[:];
boothPaidDetail = ByProductNetworkServices.getByProductPaymentDetails(dctx, UtilMisc.toMap("fromDate",dayBegin,"thruDate" ,dayEnd,"facilityList", boothsList, "isByParty",isByParty)).get("facilityPaidMap");

categoryTotalMap = [:];
categorysList = [];
categorysParloursList = [];
BigDecimal totaRETNAmount=BigDecimal.ZERO;
Iterator boothTotIter = boothTotals.entrySet().iterator();

boothsList.each{  boothId->
	BigDecimal totalRevenue=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(boothTotals.get(boothId))){
		totalRevenue=boothTotals.get(boothId);
	}
	BigDecimal paymentAmount=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(boothPaidDetail.get(boothId))){
		paymentAmount=boothPaidDetail.get(boothId);
	}
	
	BigDecimal invoiceAmount = totalRevenue;
	BigDecimal chequePenality=BigDecimal.ZERO;
	BigDecimal chequePenalityPaidAmount=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(facilityPenaltyMap.get(boothId))){
		chequePenality=facilityPenaltyMap.get(boothId);
	}
	if(UtilValidate.isNotEmpty(facilityPenaltyPaymentIdsMap.get(boothId))){
		penaltyPaymentIdsList=facilityPenaltyPaymentIdsMap.get(boothId);
		penaltyPaymentIdsList.each{paymentId->
			returnDetail = returnPaymentReferences.get(paymentId);
			if(returnDetail){
				chequePenalityPaidAmount=chequePenalityPaidAmount.add(returnDetail.get("amount"));
			}
		}
	}
	
	BigDecimal returnAmount=BigDecimal.ZERO;
	/*if(UtilValidate.isNotEmpty(returnBoothTotals.get(boothId))){
		returnAmount=(returnBoothTotals.get(boothId)).get("totalRevenue");
		totaRETNAmount=totaRETNAmount.add(returnAmount);
		//invoiceAmount=invoiceAmount.add(returnAmount);
		
	}*/
	
	invoiceAmount=invoiceAmount.add(chequePenality);
	BigDecimal totalPaidAmnt=(paymentAmount+returnAmount);
	
	BigDecimal netAmount =(BigDecimal) invoiceAmount.subtract(totalPaidAmnt);
	BigDecimal openingBalance=BigDecimal.ZERO;
	boothTotalsMap=[:];
	if(reportTypeFlag=="DuesAbstractReport"){
	openingBalance =(ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: dayBegin , facilityId:boothId, isByParty:isByParty])).get("openingBalance");
	boothTotalsMap.put("openingBalance", openingBalance);
	netAmount=netAmount.add(openingBalance);
	}
	netAmount=netAmount.add(chequePenalityPaidAmount);//to match with closing Balance
	
   boothTotalsMap.put("facilityId", boothId);
   boothTotalsMap.put("invoiceAmount", totalRevenue+chequePenality);
   boothTotalsMap.put("returnAmount", returnAmount);
   boothTotalsMap.put("chequeRetnAmount", chequePenality);
   boothTotalsMap.put("chequePenalityPaidAmount", chequePenalityPaidAmount);
   boothTotalsMap.put("totalPaid", totalPaidAmnt);
   boothTotalsMap.put("netAmount", netAmount);
   if(openingBalance!=0|| totalPaidAmnt!=0 || totalRevenue!=0 ){
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
}

categorysParloursList.add("PARLOUR");
categorysParloursList.add("PARLOUR_EX");
context.categoryTotalMap = categoryTotalMap;
if(parloursOnly == "Y"){
	context.categorysList = categorysParloursList;
}else{
	context.categorysList = categorysList;
}




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
reportTypeFlag = parameters.reportTypeFlag;

if(UtilValidate.isEmpty(reportTypeFlag)){
	effectiveDateStr = parameters.effectiveDate;
	categoryTypeEnum = parameters.categoryTypeEnum;
	effectiveDate = null;
	if (UtilValidate.isNotEmpty(effectiveDateStr)) {
		def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, "");
		}
	}
	else{
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	dayBegin = UtilDateTime.getDayStart(effectiveDate);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate);
	resultCtx = ByProductNetworkServices.getAllBooths(delegator, categoryTypeEnum);
}else{
	fromDateStr = parameters.fromDate;
	thruDateStr = parameters.thruDate;
	fromDate = null;
	thruDate = null;
	if (UtilValidate.isNotEmpty(fromDateStr)) {
		def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + fromDateStr, "");
		}
	}
	else{
		fromDate = UtilDateTime.nowTimestamp();
	}
	if (UtilValidate.isNotEmpty(thruDateStr)) {
		def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			thruDate = new java.sql.Timestamp(sdf.parse(thruDateStr+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + thruDateStr, "");
		}
	}
	else{
		thruDate = UtilDateTime.nowTimestamp();
	}
	dayBegin = UtilDateTime.getDayStart(fromDate);
	dayEnd = UtilDateTime.getDayEnd(thruDate);
	resultCtx = ByProductNetworkServices.getAllBooths(delegator, "");
}
conditionList=[];
isByParty = Boolean.TRUE;
context.displayDate = UtilDateTime.toDateString(dayBegin, "dd MMMMM, yyyy");
conditionList=[];
isByParty = Boolean.TRUE;
boothsList= resultCtx.get("boothsList");
boothsDetailsList = resultCtx.get("boothsDetailsList");
boothTotals=[:];
returnBoothTotals=[:];
boothTotalsWithReturn=[:];
periodBoothTotals=[:];
FDRDetail = ByProductNetworkServices.getFacilityFixedDeposit( dctx , [userLogin: userLogin, effectiveDate: dayBegin]).get("FacilityFDRDetail");
duesFDRList = [];
/*//boothsList.clear();
//boothsList.add("B80902");
//boothsDetailsList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, "B80902"), null, null, null, false);
*/
if(isByParty){
	ownerPartyList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN, boothsList), UtilMisc.toSet("ownerPartyId"), null, null, false);
	boothsList.clear();
	boothsList = EntityUtil.getFieldListFromEntityList(ownerPartyList, "ownerPartyId", true);
}
if(UtilValidate.isNotEmpty(reportTypeFlag)&&reportTypeFlag=="DuesFDRAvgReport"){
	curntMonthDays= UtilDateTime.getIntervalInDays(dayBegin,dayEnd)+1;
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:boothsList,fromDate: dayBegin, thruDate: dayEnd, includeReturnOrders:true, isByParty: isByParty]).get("boothTotals");
}
boothsList.each{ eachBoothId ->
	facilityFDRMap = [:];
	facilityFDRMap.putAt("facilityId", eachBoothId);
	facilityDet = [];
	if(isByParty){
		facilityDet = EntityUtil.filterByCondition(boothsDetailsList, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, eachBoothId));
	}
	else{
		facilityDet = EntityUtil.filterByCondition(boothsDetailsList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachBoothId));
	}
	facId = (EntityUtil.getFirst(facilityDet)).facilityId;
	facilityFDRMap.putAt("facilityName", (EntityUtil.getFirst(facilityDet)).facilityName);
	openingBalance =(ByProductNetworkServices.getOpeningBalanceForBooth( dctx , [userLogin: userLogin ,saleDate: dayBegin , facilityId:facId, isByParty:isByParty])).get("openingBalance");
	facilityFDRMap.putAt("openingBalance", openingBalance);
	boothFDRDet = FDRDetail.get(eachBoothId);
	fdrAmt = 0;
	fdrNums = "";
	if(boothFDRDet){
		if(UtilValidate.isNotEmpty(boothFDRDet.get("totalAmount"))){
			fdrAmt = boothFDRDet.get("totalAmount");
		}
		
		fdrDetails = boothFDRDet.get("FDRDetail");
		if(UtilValidate.isNotEmpty(fdrDetails)){
			fdrDetails.each{ eachDetail->
				if(UtilValidate.isNotEmpty(eachDetail) && UtilValidate.isNotEmpty(eachDetail.fdrNumber)){
					fdrNums = fdrNums+eachDetail.fdrNumber+",";
				}
				
			}
		}
		
	}
	if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag=="DuesFDRAvgReport"){
		milkAvgTotal=0;
		if(UtilValidate.isNotEmpty(dayTotals)&& dayTotals.containsKey(eachBoothId)){
			boothTotal = dayTotals.get(eachBoothId).get("totalRevenue");
			milkAvgTotal=(boothTotal/curntMonthDays);
		}
		partyProfileDetail = delegator.findList("PartyProfileDefault", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachBoothId), null, null, null, false);
		partyProfileDetail = EntityUtil.filterByDate(partyProfileDetail, dayEnd);
		if(partyProfileDetail){
			partyPayType = EntityUtil.getFirst(partyProfileDetail);
			partyPayMeth = partyPayType.defaultPayMeth;
		}
		paymentMethodType = delegator.findList("PaymentMethodType", EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, partyPayMeth), null, null, null, false);
		paymentType = EntityUtil.getFirst(paymentMethodType);
		facilityFDRMap.putAt("milkAvgTotal", milkAvgTotal);
		facilityFDRMap.putAt("paymentMethodType", paymentType.description);
	}
	
	diffAmount = openingBalance-fdrAmt;
	facilityFDRMap.putAt("fdrNumber", fdrNums);
	facilityFDRMap.putAt("fdrAmount", fdrAmt);
	facilityFDRMap.putAt("diffAmount", diffAmount);
	if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag=="DuesFDRAvgReport"){
		duesFDRList.add(facilityFDRMap);
	}
	else{
		if(diffAmount>0){
			duesFDRList.add(facilityFDRMap);
		}
	}
	
	
}
context.duesFDRList = duesFDRList;
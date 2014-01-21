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
import  org.ofbiz.network.NetworkServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.price.PriceServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);
}

if(UtilValidate.isNotEmpty(parameters.saleDate)){
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
}
conditionList=[];
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "BOOTH"));
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL , null));
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL, "2093"));

EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

booths = delegator.findList("Facility", condition, null, UtilMisc.toList("facilityId"), null, false);
boothsList = EntityUtil.getFieldListFromEntityList(booths, "facilityId", false);

//boothsList = ByProductServices.getAllByproductBooths(delegator).get("boothsIdsList");
if(UtilValidate.isNotEmpty(parameters.saleDate)){
	boothTotalsMap = ByProductReportServices.getBoothSaleAndPaymentTotals(dispatcher.getDispatchContext(), ["userLogin":userLogin ,facilityIds:UtilMisc.toList(boothsList),fromDate:dayBegin, thruDate:dayEnd],true);
}
if(UtilValidate.isNotEmpty(parameters.customTimePeriodId)){
	boothTotalsMap = ByProductReportServices.getBoothSaleAndPaymentTotals(dispatcher.getDispatchContext(), ["userLogin":userLogin ,facilityIds:UtilMisc.toList(boothsList),fromDate:dayBegin, thruDate:dayEnd],false);
}
boothTotals = boothTotalsMap.get("boothTotalsMap");
categoryTotalMap = [:];
categorysList = [];
categorysParloursList = [];
Iterator boothTotIter = boothTotals.entrySet().iterator();
while (boothTotIter.hasNext()) {
	Map.Entry boothEntry = boothTotIter.next();
	booth = boothEntry.getKey();
	facility = delegator.findOne("Facility",[facilityId : booth], false);
	categoryType = facility.categoryTypeEnum;
	if((booth.toString()).startsWith("E")){
		categoryType = "PARLOUR_EX";
	}
	if(categoryTotalMap.containsKey(categoryType)){
		tempCatList = categoryTotalMap.get(categoryType);
		tempCatList.addAll(boothEntry.getValue());
		categoryTotalMap.putAt(categoryType, tempCatList);
	}else{
		tempList = [];
		tempList.add(boothEntry.getValue());
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
//Debug.log("categorysParloursList--------------------------->"+categorysParloursList);





import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
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
import org.ofbiz.base.util.UtilNumber;

import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
if(UtilValidate.isEmpty(parameters.shedId)){
	Debug.logError("shedId Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
if(UtilValidate.isEmpty(fromDate)){
	Debug.logError("fromDate Cannot Be Empty","");
	context.errorMessage = "FromDate Cannot Be Empty.......!";
	return;
}
if(UtilValidate.isEmpty(thruDate)){
	Debug.logError("thruDate Cannot Be Empty","");
	context.errorMessage = "ThruDate Cannot Be Empty.......!";
	return;
}
def sdf1 = new SimpleDateFormat("yyyy/MM/dd");
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (fromDate) {
		fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
	}
	if (thruDate) {
		thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("fromDate", fromDateStart);
context.putAt("thruDate", thruDateEnd);

prevFromDate = UtilDateTime.previousYearDateString(fromDateStart.toString());
prevThruDate = UtilDateTime.previousYearDateString(thruDateEnd.toString());
def sdf2 = new SimpleDateFormat("yyyy-MM-dd");
try {
	if (fromDate) {
		prevDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(prevFromDate).getTime()));
	}
	if (thruDate) {
		prevDateEnd = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(prevThruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("prevDateStart", prevDateStart);
context.putAt("prevDateEnd", prevDateEnd);


totalDays=UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd);
if(totalDays > 366){
	Debug.logError("You Cannot Choose More Than 366 Days.","");
	context.errorMessage = "You Cannot Choose More Than 366 Days";
	return;
}

Map monthDaysMap = FastMap.newInstance();
List currMonthKeyList = FastList.newInstance();
tempCurrentDate =  UtilDateTime.getMonthStart(fromDateStart);
while(tempCurrentDate<= (UtilDateTime.getMonthEnd(thruDateEnd,timeZone, locale))){
	date = UtilDateTime.toDateString(tempCurrentDate,"MM/yyyy");
	String monthKey = UtilDateTime.toDateString(tempCurrentDate,"MM");
	Timestamp currentMonthEnd = UtilDateTime.getMonthEnd(tempCurrentDate,timeZone, locale);
	noofDays=UtilDateTime.getIntervalInDays(tempCurrentDate,currentMonthEnd);
	monthDaysMap.put(monthKey, noofDays+1);
	currMonthKeyList.add(date);
	tempCurrentDate=UtilDateTime.addDaysToTimestamp(currentMonthEnd, 1);
	
}
context.putAt("currMonthKeyList", currMonthKeyList);
context.putAt("monthDaysMap", monthDaysMap);

Map prevMonthDaysMap = FastMap.newInstance();
List prevMonthKeyList = FastList.newInstance();
tempPreviousDate =  UtilDateTime.getMonthStart(prevDateStart);
while(tempPreviousDate<= (UtilDateTime.getMonthEnd(prevDateEnd,timeZone, locale))){
	date = UtilDateTime.toDateString(tempPreviousDate,"MM/yyyy");
	String monthKey = UtilDateTime.toDateString(tempPreviousDate,"MM");
	Timestamp previousMonthEnd = UtilDateTime.getMonthEnd(tempPreviousDate,timeZone, locale);
	noofPrevDays=UtilDateTime.getIntervalInDays(tempPreviousDate,previousMonthEnd);
	prevMonthDaysMap.put(monthKey, noofPrevDays+1);
	prevMonthKeyList.add(date);
	tempPreviousDate = UtilDateTime.getMonthEnd(tempPreviousDate,timeZone, locale);
	tempPreviousDate=UtilDateTime.addDaysToTimestamp(previousMonthEnd, 1);
}
context.putAt("prevMonthKeyList", prevMonthKeyList);
context.putAt("prevMonthDaysMap", prevMonthDaysMap);

totalDays=totalDays+1;
dctx = dispatcher.getDispatchContext();
currentQtyDateMap =[:];
shedCurrTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd, facilityId : parameters.shedId]);
if(UtilValidate.isNotEmpty(shedCurrTotals)){
	Iterator shedTotIter = shedCurrTotals.entrySet().iterator();
	while(shedTotIter.hasNext()){
		Map.Entry entry = shedTotIter.next();
		if(!"dayTotals".equals(entry.getKey())){
			unitCurrValue= entry.getValue();
			if(UtilValidate.isNotEmpty(unitCurrValue)){
				shedCurrDayTotals = unitCurrValue.get("dayTotals");
				if(UtilValidate.isNotEmpty(shedCurrDayTotals)){
					for(dateKey in shedCurrDayTotals.keySet()){
						if(dateKey != "TOT"){
							reqDateFormate=null;
							dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
							reqDateFormate = UtilDateTime.toDateString(dateFormate,"MM/yyyy");
							recQtyLtrs = shedCurrDayTotals.get(dateKey).get("TOT");
							roundedQty = (recQtyLtrs.get("recdQtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
							if(UtilValidate.isEmpty(currentQtyDateMap.get(reqDateFormate))){
								shedMap = [:];
								shedMap.put("qtyLtrs",0);
								shedMap.put("qtyLtrs", roundedQty);
								currentQtyDateMap.put(reqDateFormate,shedMap);
							}
							else{
								Map tempMap=FastMap.newInstance();
								tempMap.putAll(currentQtyDateMap.get(reqDateFormate));
								tempMap.put("qtyLtrs", tempMap.get("qtyLtrs")+roundedQty);
								currentQtyDateMap.put(reqDateFormate, tempMap);
							}
						}
					}
				}
			}
		}
	}
}
if(UtilValidate.isNotEmpty(currentQtyDateMap)){
	for(key in currentQtyDateMap.keySet()){
		String monthKey = (String)key;
		monthKey = monthKey.substring(0, monthKey.indexOf("/"));
		monthDays = monthDaysMap.get(monthKey);
		Map tempMap = FastMap.newInstance();
		tempMap.putAll(currentQtyDateMap.get(key));
		avgMonthQty = (tempMap.get("qtyLtrs")/monthDays);
		roundedAvg = avgMonthQty.setScale(2,BigDecimal.ROUND_HALF_UP);
		tempMap.put("avgQty", roundedAvg);
		currentQtyDateMap.put(key, tempMap);
	}
}
context.putAt("currentQtyDateMap", currentQtyDateMap);

previousQtyDateMap = [:];
shedPrevTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: prevDateStart , thruDate: prevDateEnd, facilityId : parameters.shedId]);
if(UtilValidate.isNotEmpty(shedPrevTotals)){
	Iterator shedTotIter = shedPrevTotals.entrySet().iterator();
	while(shedTotIter.hasNext()){
		Map.Entry entry = shedTotIter.next();
		if(!"dayTotals".equals(entry.getKey())){
			unitPrevValue= entry.getValue();
			if(UtilValidate.isNotEmpty(unitPrevValue)){
				shedPrevDayTotals = unitPrevValue.get("dayTotals");
				if(UtilValidate.isNotEmpty(shedPrevDayTotals)){
					for(dateKey in shedPrevDayTotals.keySet()){
						if(dateKey != "TOT"){
							reqDateFormate=null;
							dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
							reqDateFormate = UtilDateTime.toDateString(dateFormate,"MM/yyyy");
							recQtyLtrs = shedPrevDayTotals.get(dateKey).get("TOT");
								roundedPrevQty = (recQtyLtrs.get("recdQtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
							if(UtilValidate.isEmpty(previousQtyDateMap.get(reqDateFormate))){
								shedMap = [:];
								shedMap.put("qtyLtrs",0);
								shedMap.put("qtyLtrs", roundedPrevQty);
								previousQtyDateMap.put(reqDateFormate,shedMap);
							}
							else{
								Map tempMap=FastMap.newInstance();
								tempMap.putAll(previousQtyDateMap.get(reqDateFormate));
								tempMap.put("qtyLtrs", tempMap.get("qtyLtrs")+roundedPrevQty);
								previousQtyDateMap.put(reqDateFormate, tempMap);
							}
						}
					}
				}
			}
		}
	}
}
if(UtilValidate.isNotEmpty(previousQtyDateMap)){
	for(key in previousQtyDateMap.keySet()){
		String monthKey = (String)key;
		monthKey = monthKey.substring(0, monthKey.indexOf("/"));
		monthDays = prevMonthDaysMap.get(monthKey);
		Map tempMap = FastMap.newInstance();
		tempMap.putAll(previousQtyDateMap.get(key));
		avgMonthQty = (tempMap.get("qtyLtrs")/monthDays);
		roundedPrevAvg = avgMonthQty.setScale(2,BigDecimal.ROUND_HALF_UP);
		tempMap.put("avgQty", roundedPrevAvg);
		previousQtyDateMap.put(key, tempMap);
		
	}
}
context.putAt("previousQtyDateMap", previousQtyDateMap);

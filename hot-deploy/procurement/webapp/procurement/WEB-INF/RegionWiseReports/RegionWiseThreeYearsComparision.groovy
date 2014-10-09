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
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices

yearList = [];
fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("yyyy/MM/dd");
def sdf1 = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		currSelectedDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(parameters.fromDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
dctx = dispatcher.getDispatchContext();
context.putAt("fromDate", currSelectedDate);

def sdf2 = new SimpleDateFormat("yyyy-MM-dd");
currentMonthStart =  UtilDateTime.getMonthStart(currSelectedDate);
currentYearStart =  UtilDateTime.getYearStart(currSelectedDate);
String currentYear = UtilDateTime.toDateString(currentYearStart,"yyyy");
context.putAt("currentYear", currentYear);
String cummCurrYear=currentYear+"-04-01";
cummulativeCurrentYear = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(cummCurrYear).getTime()));
if(currSelectedDate < cummulativeCurrentYear ){
	cummulativeCurrYear =  UtilDateTime.previousYearDateString(cummulativeCurrentYear.toString());
	cummulativeCurrYear = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(cummulativeCurrYear).getTime()));
}else{
	cummulativeCurrYear = cummulativeCurrentYear;
}

def sdf3 = new SimpleDateFormat("yyyy-MM-dd");
prevYearStart =  UtilDateTime.previousYearDateString(currentYearStart.toString());
prevDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf3.parse(prevYearStart).getTime()));
String previousYearEnding = UtilDateTime.toDateString(currSelectedDate,"-MM-dd");
String previousYear = UtilDateTime.toDateString(prevDateStart,"yyyy");
context.putAt("previousYear", previousYear);
String cummPrevYear=previousYear+"-04-01";
cummulativePreviousYear = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf3.parse(cummPrevYear).getTime()));
prevSelectedDate = previousYear + previousYearEnding;
prevSelectedDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf3.parse(prevSelectedDate).getTime()));
previousMonthStart =  UtilDateTime.getMonthStart(prevSelectedDate);
if(prevSelectedDate < cummulativePreviousYear ){
	cummulativePrevYear =  UtilDateTime.previousYearDateString(cummulativePreviousYear.toString());
	cummulativePrevYear = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(cummulativePrevYear).getTime()));
}else{
	cummulativePrevYear = cummulativePreviousYear;
}

def sdf4 = new SimpleDateFormat("yyyy-MM-dd");
prevPreviousYearStart =  UtilDateTime.previousYearDateString(prevYearStart.toString());
prevPreviousDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf4.parse(prevPreviousYearStart).getTime()));
String previousPreviousYear = UtilDateTime.toDateString(prevPreviousDateStart,"yyyy");
context.putAt("previousPreviousYear", previousPreviousYear);
String cummPrevPrevYear=previousPreviousYear+"-04-01";
cummulativePrevPreviousYear = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf4.parse(cummPrevPrevYear).getTime()));
prevPrevSelectedDate = previousPreviousYear + previousYearEnding;
prevPrevSelectedDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf4.parse(prevPrevSelectedDate).getTime()));
previousPrevMonthStart =  UtilDateTime.getMonthStart(prevPrevSelectedDate);
if(prevPrevSelectedDate < cummulativePrevPreviousYear ){
	cummulativePrevPrevYear =  UtilDateTime.previousYearDateString(cummulativePrevPreviousYear.toString());
	cummulativePrevPrevYear = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(cummulativePrevPrevYear).getTime()));
}else{
	cummulativePrevPrevYear = cummulativePrevPreviousYear;
}

finalMap = [:];
List allRegionShedsList = delegator.findList("FacilityGroupMember",EntityCondition.makeCondition("facilityGroupId",EntityOperator.IN,UtilMisc.toList("ANDHRA","TELANGANA")), null, null, null, false );
andhraShedList = EntityUtil.filterByAnd(allRegionShedsList, [facilityGroupId : 'ANDHRA']);
telanganaShedList = EntityUtil.filterByAnd(allRegionShedsList, [facilityGroupId : 'TELANGANA']);
List andhraShedIdsList = EntityUtil.getFieldListFromEntityList(andhraShedList,"facilityId", false);
List telanganaSheIdsList= EntityUtil.getFieldListFromEntityList(telanganaShedList,"facilityId", false);

if(UtilValidate.isNotEmpty(parameters.facilityGroupId)){
	if (parameters.facilityGroupId == "ANDHRA"){
		telanganaSheIdsList= telanganaSheIdsList.clear();
	}
	if (parameters.facilityGroupId == "TELANGANA"){
		andhraShedIdsList= andhraShedIdsList.clear();
	}
}
context.put("andhraShedIdsList",andhraShedIdsList);
context.put("telanganaSheIdsList",telanganaSheIdsList);

andhraShedsFinalMap = [:];
andhraShedIdsList.each{ andhraShedId->
	dayWiseAndhraFinalMap = [:];
	thisDayAndhraMap = [:];
	thisDayAndhraMap[currentYear]=0;
	thisDayAndhraMap[previousYear]=0;
	thisDayAndhraMap[previousPreviousYear]=0;
	currDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: currSelectedDate,thruDate: currSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	if(UtilValidate.isNotEmpty(currDayTotals)){
		currAndhraShedTotals = currDayTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(currAndhraShedTotals)){
			andhraCurrDayTotals = currAndhraShedTotals.get("dayTotals");
			if(UtilValidate.isNotEmpty(andhraCurrDayTotals)){
				for(currDateKey in andhraCurrDayTotals.keySet()){
					if(currDateKey != "TOT"){
						dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(currDateKey).getTime()));
						currYear = UtilDateTime.toDateString(dateFormate,"yyyy");
						qtyLtrs = (andhraCurrDayTotals.get(currDateKey).get("TOT").get("TOT").get("qtyLtrs"));
						qtyLtrs = qtyLtrs.setScale(0,BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isNotEmpty(thisDayAndhraMap.get(currYear))){
							thisDayAndhraMap.put(currYear, qtyLtrs);
						}
					}
				}
			}
		}
	}
	prevDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: prevSelectedDate,thruDate: prevSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	if(UtilValidate.isNotEmpty(prevDayTotals)){
		prevAndhraShedTotals = prevDayTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(prevAndhraShedTotals)){
			andhraPrevDayTotals = prevAndhraShedTotals.get("dayTotals");
			if(UtilValidate.isNotEmpty(andhraPrevDayTotals)){
				for(PrevDateKey in andhraPrevDayTotals.keySet()){
					if(PrevDateKey != "TOT"){
						dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(PrevDateKey).getTime()));
						prevYear = UtilDateTime.toDateString(dateFormate,"yyyy");
						qtyLtrs = (andhraPrevDayTotals.get(PrevDateKey).get("TOT").get("TOT").get("qtyLtrs"));
						qtyLtrs = qtyLtrs.setScale(0,BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isNotEmpty(thisDayAndhraMap.get(prevYear))){
							thisDayAndhraMap.put(prevYear, qtyLtrs);
						}
					}
				}
			}
		}
	}
	prevPrevDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: prevPrevSelectedDate,thruDate: prevPrevSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	if(UtilValidate.isNotEmpty(prevPrevDayTotals)){
		prevPrevAndhraShedTotals = prevPrevDayTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(prevPrevAndhraShedTotals)){
			andhraPrevPrevDayTotals = prevPrevAndhraShedTotals.get("dayTotals");
			if(UtilValidate.isNotEmpty(andhraPrevPrevDayTotals)){
				for(dateKey in andhraPrevPrevDayTotals.keySet()){
					if(dateKey != "TOT"){
						dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(dateKey).getTime()));
						prevPrevYear = UtilDateTime.toDateString(dateFormate,"yyyy");
						qtyLtrs = (andhraPrevPrevDayTotals.get(dateKey).get("TOT").get("TOT").get("qtyLtrs"));
						qtyLtrs = qtyLtrs.setScale(0,BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isNotEmpty(thisDayAndhraMap.get(prevPrevYear))){
							thisDayAndhraMap.put(prevPrevYear, qtyLtrs);
						}
					}
				}
			}
		}
	}
	dayWiseAndhraFinalMap.put("DayWise",thisDayAndhraMap);
	thisMonthAndhraMap = [:];
	thisMonthAndhraMap[currentYear]=0;
	thisMonthAndhraMap[previousYear]=0;
	thisMonthAndhraMap[previousPreviousYear]=0;
	currMonthTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: currentMonthStart,thruDate: currSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	if(UtilValidate.isNotEmpty(currMonthTotals)){
		andhraCurrShedTotals = currMonthTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(andhraCurrShedTotals)){
			currDate =  UtilDateTime.getYearStart(currSelectedDate);
			String reqCurrYear = UtilDateTime.toDateString(currDate,"yyyy");
			currentMonthQtyLtrs = andhraCurrShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("qtyLtrs");
			currentMonthSQtyLtrs = andhraCurrShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("sQtyLtrs");
			currentMonthQty = currentMonthQtyLtrs + currentMonthSQtyLtrs;
			currMonthQty = (currentMonthQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(thisMonthAndhraMap.get(reqCurrYear))){
				thisMonthAndhraMap.put(reqCurrYear, currMonthQty);
			}
		}
	}
	prevMonthTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: previousMonthStart,thruDate: prevSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	if(UtilValidate.isNotEmpty(prevMonthTotals)){
		andhraPrevShedTotals = prevMonthTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(andhraPrevShedTotals)){
			prevDate =  UtilDateTime.getYearStart(prevSelectedDate);
			String reqPrevYear = UtilDateTime.toDateString(prevDate,"yyyy");
			previousMonthQtyLtrs = andhraPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("qtyLtrs");
			previousMonthSQtyLtrs = andhraPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("sQtyLtrs");
			previousMonthQty = previousMonthQtyLtrs + previousMonthSQtyLtrs;
			prevMonthQty = (previousMonthQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(thisMonthAndhraMap.get(reqPrevYear))){
				thisMonthAndhraMap.put(reqPrevYear, prevMonthQty);
			}
		}
	}
	prevPrevMonthTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: previousPrevMonthStart,thruDate: prevPrevSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	if(UtilValidate.isNotEmpty(prevPrevMonthTotals)){
		andhraPrevPrevShedTotals = prevPrevMonthTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(andhraPrevPrevShedTotals)){
			prevPrevDate =  UtilDateTime.getYearStart(prevPrevSelectedDate);
			String reqPrevPrevYear = UtilDateTime.toDateString(prevPrevDate,"yyyy");
			prevPreviosMonthQtyLtrs = andhraPrevPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("qtyLtrs");
			prevPreviosMonthSQtyLtrs = andhraPrevPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("sQtyLtrs");
			prevPreviosMonthQty = prevPreviosMonthQtyLtrs + prevPreviosMonthSQtyLtrs;
			prevPrevMonthQtyMap = (prevPreviosMonthQtyMap/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(thisMonthAndhraMap.get(reqPrevPrevYear))){
				thisMonthAndhraMap.put(reqPrevPrevYear, prevPrevMonthQtyMap);
			}
		}
	}
	dayWiseAndhraFinalMap.put("MonthWise",thisMonthAndhraMap);
	cummulativeAndhraMap = [:];
	cummulativeAndhraMap[currentYear]=0;
	cummulativeAndhraMap[previousYear]=0;
	cummulativeAndhraMap[previousPreviousYear]=0;
	currentYearAndhraTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: cummulativeCurrYear , thruDate: currSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	currDate =  UtilDateTime.getYearStart(cummulativeCurrYear);
	String requiredCurrYear = UtilDateTime.toDateString(currDate,"yyyy");
	if(UtilValidate.isNotEmpty(currentYearAndhraTotals)){
		currentShedDetails = currentYearAndhraTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(currentShedDetails)){
			currShedTotals = currentShedDetails.get("dayTotals").get("TOT");
			currentYearQtyLtrs = currShedTotals.get("TOT").get("TOT").get("qtyLtrs");
			currentYearSQtyLtrs = currShedTotals.get("TOT").get("TOT").get("sQtyLtrs");
			currentYearQty = currentYearQtyLtrs + currentYearSQtyLtrs;
			currYearQty = (currentYearQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(cummulativeAndhraMap.get(requiredCurrYear))){
				cummulativeAndhraMap.put(requiredCurrYear, currYearQty);
			}
		}
	}
	previousYearTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: cummulativePrevYear , thruDate: prevSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	prevDate =  UtilDateTime.getYearStart(cummulativePrevYear);
	String requiredPrevYear = UtilDateTime.toDateString(prevDate,"yyyy");
	if(UtilValidate.isNotEmpty(previousYearTotals)){
		previousShedDetails = previousYearTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(previousShedDetails)){
			prevShedTotals = previousShedDetails.get("dayTotals").get("TOT");
			previousYearQtyLtrs = prevShedTotals.get("TOT").get("TOT").get("qtyLtrs");
			previousYearSQtyLtrs = prevShedTotals.get("TOT").get("TOT").get("sQtyLtrs");
			previousYearQty = previousYearQtyLtrs + previousYearSQtyLtrs;
			prevYearQty = (previousYearQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(cummulativeAndhraMap.get(requiredPrevYear))){
				cummulativeAndhraMap.put(requiredPrevYear, prevYearQty);
			}
		}
	}
	previousPreviousYearTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: cummulativePrevPrevYear , thruDate: prevPrevSelectedDate,userLogin: userLogin,facilityId: andhraShedId]);
	prevPreviousDate =  UtilDateTime.getYearStart(cummulativePrevPrevYear);
	String requiredPrevPrevYear = UtilDateTime.toDateString(prevPreviousDate,"yyyy");
	if(UtilValidate.isNotEmpty(previousPreviousYearTotals)){
		previousPrevShedDetails = previousPreviousYearTotals.get(andhraShedId);
		if(UtilValidate.isNotEmpty(previousPrevShedDetails)){
			prevPrevShedTotals = previousPrevShedDetails.get("dayTotals").get("TOT");
			prevPreviousYearQtyLtrs = prevPrevShedTotals.get("TOT").get("TOT").get("qtyLtrs");
			prevPreviousYearSQtyLtrs = prevPrevShedTotals.get("TOT").get("TOT").get("sQtyLtrs");
			prevPreviousYearQty = prevPreviousYearQtyLtrs + prevPreviousYearSQtyLtrs;
			prevPrevYearQty = (prevPreviousYearQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(cummulativeAndhraMap.get(requiredPrevPrevYear))){
				cummulativeAndhraMap.put(requiredPrevPrevYear, prevPrevYearQty);
			}
		}
	}
	dayWiseAndhraFinalMap.put("cummulative",cummulativeAndhraMap);
	andhraShedsFinalMap.put(andhraShedId, dayWiseAndhraFinalMap);
}
context.putAt("andhraShedsFinalMap", andhraShedsFinalMap);

telanganaShedsFinalMap = [:];
telanganaSheIdsList.each{ telanganaShedId->
	dayWiseTelanganaFinalMap = [:];
	thisDayTelanganaMap = [:];
	thisDayTelanganaMap[currentYear]=0;
	thisDayTelanganaMap[previousYear]=0;
	thisDayTelanganaMap[previousPreviousYear]=0;
	currDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: currSelectedDate,thruDate: currSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	if(UtilValidate.isNotEmpty(currDayTotals)){
		currAndhraShedTotals = currDayTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(currAndhraShedTotals)){
			andhraCurrDayTotals = currAndhraShedTotals.get("dayTotals");
			if(UtilValidate.isNotEmpty(andhraCurrDayTotals)){
				for(currDateKey in andhraCurrDayTotals.keySet()){
					if(currDateKey != "TOT"){
						dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(currDateKey).getTime()));
						currYear = UtilDateTime.toDateString(dateFormate,"yyyy");
						qtyLtrs = (andhraCurrDayTotals.get(currDateKey).get("TOT").get("TOT").get("qtyLtrs"));
						qtyLtrs = qtyLtrs.setScale(0,BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isNotEmpty(thisDayTelanganaMap.get(currYear))){
							thisDayTelanganaMap.put(currYear, qtyLtrs);
						}
					}
				}
			}
		}
	}
	prevDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: prevSelectedDate,thruDate: prevSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	if(UtilValidate.isNotEmpty(prevDayTotals)){
		prevAndhraShedTotals = prevDayTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(prevAndhraShedTotals)){
			andhraPrevDayTotals = prevAndhraShedTotals.get("dayTotals");
			if(UtilValidate.isNotEmpty(andhraPrevDayTotals)){
				for(PrevDateKey in andhraPrevDayTotals.keySet()){
					if(PrevDateKey != "TOT"){
						dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(PrevDateKey).getTime()));
						prevYear = UtilDateTime.toDateString(dateFormate,"yyyy");
						qtyLtrs = (andhraPrevDayTotals.get(PrevDateKey).get("TOT").get("TOT").get("qtyLtrs"));
						qtyLtrs = qtyLtrs.setScale(0,BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isNotEmpty(thisDayTelanganaMap.get(prevYear))){
							thisDayTelanganaMap.put(prevYear, qtyLtrs);
						}
					}
				}
			}
		}
	}
	prevPrevDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: prevPrevSelectedDate,thruDate: prevPrevSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	if(UtilValidate.isNotEmpty(prevPrevDayTotals)){
		prevPrevAndhraShedTotals = prevPrevDayTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(prevPrevAndhraShedTotals)){
			andhraPrevPrevDayTotals = prevPrevAndhraShedTotals.get("dayTotals");
			if(UtilValidate.isNotEmpty(andhraPrevPrevDayTotals)){
				for(dateKey in andhraPrevPrevDayTotals.keySet()){
					if(dateKey != "TOT"){
						dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(dateKey).getTime()));
						prevPrevYear = UtilDateTime.toDateString(dateFormate,"yyyy");
						qtyLtrs = (andhraPrevPrevDayTotals.get(dateKey).get("TOT").get("TOT").get("qtyLtrs"));
						qtyLtrs = qtyLtrs.setScale(0,BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isNotEmpty(thisDayTelanganaMap.get(prevPrevYear))){
							thisDayTelanganaMap.put(prevPrevYear, qtyLtrs);
						}
					}
				}
			}
		}
	}
	dayWiseTelanganaFinalMap.put("DayWise",thisDayTelanganaMap);
	thisMonthTelanganaMap = [:];
	thisMonthTelanganaMap[currentYear]=0;
	thisMonthTelanganaMap[previousYear]=0;
	thisMonthTelanganaMap[previousPreviousYear]=0;
	currMonthTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: currentMonthStart,thruDate: currSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	if(UtilValidate.isNotEmpty(currMonthTotals)){
		andhraCurrShedTotals = currMonthTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(andhraCurrShedTotals)){
			currDate =  UtilDateTime.getYearStart(currSelectedDate);
			String reqCurrYear = UtilDateTime.toDateString(currDate,"yyyy");
			currentMonthQtyLtrs = andhraCurrShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("qtyLtrs");
			currentMonthSQtyLtrs = andhraCurrShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("sQtyLtrs");
			currentMonthQty = currentMonthQtyLtrs + currentMonthSQtyLtrs;
			currMonthQty = (currentMonthQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(thisMonthTelanganaMap.get(reqCurrYear))){
				thisMonthTelanganaMap.put(reqCurrYear, currMonthQty);
			}
		}
	}
	prevMonthTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: previousMonthStart,thruDate: prevSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	if(UtilValidate.isNotEmpty(prevMonthTotals)){
		andhraPrevShedTotals = prevMonthTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(andhraPrevShedTotals)){
			prevDate =  UtilDateTime.getYearStart(prevSelectedDate);
			String reqPrevYear = UtilDateTime.toDateString(prevDate,"yyyy");
			previousMonthQtyLtrs = andhraPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("qtyLtrs");
			previousMonthSQtyLtrs = andhraPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("sQtyLtrs");
			previousMonthQty = previousMonthQtyLtrs + previousMonthSQtyLtrs;
			prevMonthQty = (previousMonthQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(thisMonthTelanganaMap.get(reqPrevYear))){
				thisMonthTelanganaMap.put(reqPrevYear, prevMonthQty);
			}
		}
	}
	prevPrevMonthTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: previousPrevMonthStart,thruDate: prevPrevSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	if(UtilValidate.isNotEmpty(prevPrevMonthTotals)){
		andhraPrevPrevShedTotals = prevPrevMonthTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(andhraPrevPrevShedTotals)){
			prevPrevDate =  UtilDateTime.getYearStart(prevPrevSelectedDate);
			String reqPrevPrevYear = UtilDateTime.toDateString(prevPrevDate,"yyyy");
			prevPreviosMonthQtyLtrs = andhraPrevPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("qtyLtrs");
			prevPreviosMonthSQtyLtrs = andhraPrevPrevShedTotals.get("dayTotals").get("TOT").get("TOT").get("TOT").get("sQtyLtrs");
			prevPreviosMonthQty = prevPreviosMonthQtyLtrs + prevPreviosMonthSQtyLtrs;
			prevPrevMonthQtyMap = (prevPreviosMonthQtyMap/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(thisMonthTelanganaMap.get(reqPrevPrevYear))){
				thisMonthTelanganaMap.put(reqPrevPrevYear, prevPrevMonthQtyMap);
			}
		}
	}
	dayWiseTelanganaFinalMap.put("MonthWise",thisMonthTelanganaMap);
	cummulativeTelanganaMap = [:];
	cummulativeTelanganaMap[currentYear]=0;
	cummulativeTelanganaMap[previousYear]=0;
	cummulativeTelanganaMap[previousPreviousYear]=0;
	currentYearTelanganaTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: cummulativeCurrYear , thruDate: currSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	currDate =  UtilDateTime.getYearStart(cummulativeCurrYear);
	String requiredCurrYear = UtilDateTime.toDateString(currDate,"yyyy");
	if(UtilValidate.isNotEmpty(currentYearTelanganaTotals)){
		currentShedDetails = currentYearTelanganaTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(currentShedDetails)){
			currShedTotals = currentShedDetails.get("dayTotals").get("TOT");
			currentYearQtyLtrs = currShedTotals.get("TOT").get("TOT").get("qtyLtrs");
			currentYearSQtyLtrs = currShedTotals.get("TOT").get("TOT").get("sQtyLtrs");
			currentYearQty = currentYearQtyLtrs + currentYearSQtyLtrs;
			currYearQty = (currentYearQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(cummulativeTelanganaMap.get(requiredCurrYear))){
				cummulativeTelanganaMap.put(requiredCurrYear, currYearQty);
			}
		}
	}
	previousYearTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: cummulativePrevYear , thruDate: prevSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	prevDate =  UtilDateTime.getYearStart(cummulativePrevYear);
	String requiredPrevYear = UtilDateTime.toDateString(prevDate,"yyyy");
	if(UtilValidate.isNotEmpty(previousYearTotals)){
		previousShedDetails = previousYearTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(previousShedDetails)){
			prevShedTotals = previousShedDetails.get("dayTotals").get("TOT");
			previousYearQtyLtrs = prevShedTotals.get("TOT").get("TOT").get("qtyLtrs");
			previousYearSQtyLtrs = prevShedTotals.get("TOT").get("TOT").get("sQtyLtrs");
			previousYearQty = previousYearQtyLtrs + previousYearSQtyLtrs;
			prevYearQty = (previousYearQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(cummulativeTelanganaMap.get(requiredPrevYear))){
				cummulativeTelanganaMap.put(requiredPrevYear, prevYearQty);
			}
		}
	}
	previousPreviousYearTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: cummulativePrevPrevYear , thruDate: prevPrevSelectedDate,userLogin: userLogin,facilityId: telanganaShedId]);
	prevPreviousDate =  UtilDateTime.getYearStart(cummulativePrevPrevYear);
	String requiredPrevPrevYear = UtilDateTime.toDateString(prevPreviousDate,"yyyy");
	if(UtilValidate.isNotEmpty(previousPreviousYearTotals)){
		previousPrevShedDetails = previousPreviousYearTotals.get(telanganaShedId);
		if(UtilValidate.isNotEmpty(previousPrevShedDetails)){
			prevPrevShedTotals = previousPrevShedDetails.get("dayTotals").get("TOT");
			prevPreviousYearQtyLtrs = prevPrevShedTotals.get("TOT").get("TOT").get("qtyLtrs");
			prevPreviousYearSQtyLtrs = prevPrevShedTotals.get("TOT").get("TOT").get("sQtyLtrs");
			prevPreviousYearQty = prevPreviousYearQtyLtrs + prevPreviousYearSQtyLtrs;
			prevPrevYearQty = (prevPreviousYearQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(cummulativeTelanganaMap.get(requiredPrevPrevYear))){
				cummulativeTelanganaMap.put(requiredPrevPrevYear, prevPrevYearQty);
			}
		}
	}
	dayWiseTelanganaFinalMap.put("cummulative",cummulativeTelanganaMap);
	telanganaShedsFinalMap.put(telanganaShedId, dayWiseTelanganaFinalMap);
}
context.putAt("telanganaShedsFinalMap", telanganaShedsFinalMap);

if(UtilValidate.isNotEmpty(andhraShedsFinalMap)){
	finalMap.put("andhra",andhraShedsFinalMap);
}
if(UtilValidate.isNotEmpty(telanganaShedsFinalMap)){
	finalMap.put("telangana",telanganaShedsFinalMap);
}
context.putAt("finalMap", finalMap);

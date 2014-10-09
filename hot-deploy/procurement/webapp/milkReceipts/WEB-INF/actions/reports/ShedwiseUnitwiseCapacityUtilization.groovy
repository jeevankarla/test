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
import in.vasista.vbiz.procurement.PriceServices;

dctx = dispatcher.getDispatchContext();
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

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

tempCurrentDate = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(fromDateStart), timeZone, locale);
tempCurrentDateEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(thruDateEnd), timeZone, locale);
currentDayStart = UtilDateTime.getDayStart(tempCurrentDate);
String currentDateKey = UtilDateTime.toDateString(currentDayStart,"dd/MM/yyyy");

prevFromDate = UtilDateTime.previousYearDateString(fromDateStart.toString());
prevThruDate = UtilDateTime.previousYearDateString(thruDateEnd.toString());
def sdf2 = new SimpleDateFormat("yyyy-MM-dd");
try {
	if (prevFromDate) {
		prevDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(prevFromDate).getTime()));
	}
	if (prevThruDate) {
		prevDateEnd = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(prevThruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("prevDateStart", prevDateStart);
context.putAt("prevDateEnd", prevDateEnd);
tempprevDatestart = UtilDateTime.getMonthStart(UtilDateTime.toTimestamp(prevDateStart), timeZone, locale);
TempprevDateEnd = UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(prevDateEnd), timeZone, locale);
prevDayStart = UtilDateTime.getDayStart(tempprevDatestart);
String prevDateKey = UtilDateTime.toDateString(prevDayStart,"dd/MM/yyyy");

Map finalShedMap = FastMap.newInstance();
Map currYearshedWiseTotalsMap = FastMap.newInstance();
Map prevYearshedWiseTotalsMap = FastMap.newInstance();
mccTypeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SHED" )  , null, null, null, false );
mccshedIds = EntityUtil.getFieldListFromEntityList(mccTypeList, "mccTypeId", true);
shedWiseTotMap=[:];
mccshedIds.each{ mccIds->
	if("FEDERATION".equals(mccIds)){
		shedList = EntityUtil.filterByAnd(mccTypeList, [mccTypeId : mccIds]);
		for(shed in shedList){
			shedTotMap=[:];
			finalUnitMap = [:];
			totPrevLtrs=0;
			totCurrLtrs=0;
			totcurrAvg=0;
			totprevAvg=0;
			totPrevCapacity=0;
			totCurrCapacity=0;
			shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: shed.facilityId]);
			unitsList = shedUnitDetails.get("unitsList");
			unitsList.each{ unit->
				unitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: tempCurrentDate , thruDate: tempCurrentDateEnd,userLogin: userLogin,facilityId: unit]);
				currYearMap = [:];
				prevYearMap =[:];
				unitcurrtotLtrs=0;
				unitprevtotLtrs=0;
				UnitMap=[:];
				procDays=0;
				prevprocDays=0;
				if(UtilValidate.isNotEmpty(unitTotals)){
					facilityTotals = unitTotals.get(unit);
					if(UtilValidate.isNotEmpty(facilityTotals)){
						dayTotals = facilityTotals.get("dayTotals");
						if(UtilValidate.isNotEmpty(dayTotals)){
							Iterator dayTotIter = dayTotals.entrySet().iterator();
							while(dayTotIter.hasNext()){
								Map.Entry entry = dayTotIter.next();
								if(!"TOT".equals(entry.getKey())){
									procDays=procDays+1;
								}
								if("TOT".equals(entry.getKey())){
									currunitMap=[:];
									currunitMap["qtyLtrs"] = 0;
									currunitMap.put("qtyLtrs", entry.getValue().get("TOT").get("recdQtyLtrs"));
									totCurrLtrs=totCurrLtrs+entry.getValue().get("TOT").get("recdQtyLtrs");
									unitcurrtotLtrs=unitcurrtotLtrs+entry.getValue().get("TOT").get("recdQtyLtrs");
									unitDetails = delegator.findOne("Facility", [facilityId : unit], false);
									if(UtilValidate.isNotEmpty(unitDetails.get("facilitySize"))){
										capacity=unitDetails.get("facilitySize");
										totCurrCapacity=totCurrCapacity+capacity;
										currunitMap.put("capacity", capacity);
									}
									currYearMap.putAt(currentDateKey, currunitMap);
								}
								if(procDays!=0){
									currAvg=unitcurrtotLtrs/procDays;
								}
								totcurrAvg=totcurrAvg+currAvg;
								currYearMap.putAt("procurementDays", procDays);
							}
						}
					}
				}
				prevYearunitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: tempprevDatestart , thruDate: TempprevDateEnd,userLogin: userLogin,facilityId: unit]);
				if(UtilValidate.isNotEmpty(prevYearunitTotals)){
					prevYearfacilityTotals = prevYearunitTotals.get(unit);
					if(UtilValidate.isNotEmpty(prevYearfacilityTotals)){
						prevdayTotals = prevYearfacilityTotals.get("dayTotals");
						if(UtilValidate.isNotEmpty(prevdayTotals)){
							Iterator prevdayTotIter = prevdayTotals.entrySet().iterator();
							while(prevdayTotIter.hasNext()){
								Map.Entry preventry = prevdayTotIter.next();
								if(!"TOT".equals(preventry.getKey())){
									prevprocDays=prevprocDays+1;
								}
								if("TOT".equals(preventry.getKey())){
									prevunitMap = [:];
									prevunitMap["qtyLtrs"] = 0;
									prevunitMap.put("qtyLtrs", preventry.getValue().get("TOT").get("recdQtyLtrs"));
									totPrevLtrs=totPrevLtrs+preventry.getValue().get("TOT").get("recdQtyLtrs");
									unitprevtotLtrs=unitprevtotLtrs+preventry.getValue().get("TOT").get("recdQtyLtrs");
									unitDetails = delegator.findOne("Facility", [facilityId : unit], false);
									if(UtilValidate.isNotEmpty(unitDetails.get("facilitySize"))){
										capacity=unitDetails.get("facilitySize");
										totPrevCapacity=totPrevCapacity+capacity;
										prevunitMap.put("capacity", capacity);
									}
									prevYearMap.put(prevDateKey, prevunitMap);
								}
								if(prevprocDays!=0){
									prevAvg=unitprevtotLtrs/prevprocDays;
									totprevAvg=totprevAvg+prevAvg;
								}
								prevYearMap.putAt("prevprocurementDays", prevprocDays);
							}
						}
					}
				}
				UnitMap.putAt("currYearMap", currYearMap);
				UnitMap.putAt("prevYearMap", prevYearMap);
				finalUnitMap.put(unit, UnitMap);
			}
			shedTotMap.putAt("totPrevLtrs", totPrevLtrs);
			shedTotMap.putAt("totcurrAvg", totcurrAvg);
			shedTotMap.putAt("totprevAvg", totprevAvg);
			shedTotMap.putAt("totPrevCapacity", totPrevCapacity);
			shedTotMap.putAt("totCurrLtrs", totCurrLtrs);
			shedTotMap.putAt("totCurrCapacity", totCurrCapacity);
			shedWiseTotMap.put(shed.facilityId, shedTotMap);
			finalShedMap.put(shed.facilityId, finalUnitMap);
		}
	}
}
context.putAt("shedWiseTotMap", shedWiseTotMap);
context.putAt("currentDateKey", currentDateKey);
context.putAt("prevDateKey", prevDateKey);
context.putAt("finalShedMap", finalShedMap);


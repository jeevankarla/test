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

facility =delegator.findList("Facility", EntityCondition.makeCondition([facilityTypeId : "UNIT"]), null, null ,null, false);
districtList = EntityUtil.getFieldListFromEntityList(facility, "district", true);
shedNameMap=[:];
shedWiseTotMap=[:];
if(UtilValidate.isNotEmpty(districtList)){
	districtList.each{ district->
			shedTotMap=[:];
			finalUnitMap = [:];
			totPrevLtrs=0;
			totCurrLtrs=0;
			totcurrAvg=0;
			totprevAvg=0;
			totPrevCapacity=0;
			totCurrCapacity=0;
			unitsList = EntityUtil.filterByAnd(facility, [EntityCondition.makeCondition("district", EntityOperator.EQUALS, district)]);
			unitsList.each{ unit->
				parentFacilityId=unit.get("parentFacilityId");
				shedname=delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, parentFacilityId )  , null, null, null, false );
				if(UtilValidate.isNotEmpty(shedname)){
					shedname.each{shed->
						shedNameMap.put(district, shed.get("facilityName"));
					}
				}
				unitTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: tempCurrentDate , thruDate: tempCurrentDateEnd , facilityId: unit.get("facilityId")]);
				currYearMap = [:];
				prevYearMap =[:];
				unitcurrtotLtrs=0;
				unitprevtotLtrs=0;
				UnitMap=[:];
				procDays=0;
				prevprocDays=0;
				if(UtilValidate.isNotEmpty(unitTotals) && ServiceUtil.isSuccess(unitTotals)){
					Iterator unitIter = unitTotals.entrySet().iterator();
					while(unitIter.hasNext()){
						Map.Entry unitEntry = unitIter.next();
						Map unitValueMap = (Map)unitEntry.getValue();
						Iterator unitEntryIter = unitValueMap.entrySet().iterator();
						while(unitEntryIter.hasNext()){
							Map.Entry dayEntry = unitEntryIter.next();
							if("dayTotals".equals(dayEntry.getKey())){
								Map dayValuesMap = (Map)dayEntry.getValue();
								Iterator daytotIter = dayValuesMap.entrySet().iterator();
								while(daytotIter.hasNext()){
									Map.Entry daytotEntry = daytotIter.next();
									if(!"TOT".equals(daytotEntry.getKey())){
										Map dayEntryMap = (Map)daytotEntry.getValue();
										Iterator dayIter = dayEntryMap.entrySet().iterator();
										while(dayIter.hasNext()){
											Map.Entry dayinEntry = dayIter.next();
											if("TOT".equals(dayinEntry.getKey())){
												if((dayinEntry.getValue().get("TOT").get("qtyLtrs"))!=0){
													procDays=procDays+1;
												}
											}
										}
									}
									else{
										currunitMap=[:];
										currunitMap["qtyLtrs"] = 0;
										Map dayValueEntryMap = (Map)daytotEntry.getValue();
										Iterator daytotValuesIter = dayValueEntryMap.entrySet().iterator();
										while(daytotValuesIter.hasNext()){
											Map.Entry dayValEntry = daytotValuesIter.next();
											if("TOT".equals(dayValEntry.getKey())){
												qtyLtrs=dayValEntry.getValue().get("TOT").get("qtyLtrs").setScale(0,BigDecimal.ROUND_HALF_UP);
												currunitMap.put("qtyLtrs", qtyLtrs);
												totCurrLtrs=totCurrLtrs+qtyLtrs;
												unitcurrtotLtrs=unitcurrtotLtrs+qtyLtrs;
											}
										}
										unitDetails = delegator.findOne("Facility", [facilityId : unit.get("facilityId")], false);
										if(UtilValidate.isNotEmpty(unitDetails.get("facilitySize"))){
											capacity=unitDetails.get("facilitySize");
											totCurrCapacity=totCurrCapacity+capacity;
											currunitMap.put("capacity", capacity);
										}
										if(procDays!=0){
											currAvg=unitcurrtotLtrs/procDays;
											totcurrAvg=totcurrAvg+currAvg;
										}
										currYearMap.putAt(currentDateKey, currunitMap);
										currYearMap.putAt("procurementDays", procDays);
									}
								}
							}
						}
					}
				}
				prevYearunitTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: tempprevDatestart , thruDate: TempprevDateEnd , facilityId: unit.get("facilityId")]);
				if(UtilValidate.isNotEmpty(prevYearunitTotals) && ServiceUtil.isSuccess(prevYearunitTotals)){
					Iterator prevUnitIter = prevYearunitTotals.entrySet().iterator();
					while(prevUnitIter.hasNext()){
						Map.Entry prevUnitEntry = prevUnitIter.next();
						Map prevUnitValueMap = (Map)prevUnitEntry.getValue();
						Iterator prevUnitEntryIter = prevUnitValueMap.entrySet().iterator();
						while(prevUnitEntryIter.hasNext()){
							Map.Entry prevDayEntry = prevUnitEntryIter.next();
							if("dayTotals".equals(prevDayEntry.getKey())){
								Map prevDayValuesMap = (Map)prevDayEntry.getValue();
								Iterator prevdaytotIter = prevDayValuesMap.entrySet().iterator();
								while(prevdaytotIter.hasNext()){
									Map.Entry prevdaytotEntry = prevdaytotIter.next();
									if(!"TOT".equals(prevdaytotEntry.getKey())){
										Map prevdayEntryMap = (Map)prevdaytotEntry.getValue();
										Iterator prevdayIter = prevdayEntryMap.entrySet().iterator();
										while(prevdayIter.hasNext()){
											Map.Entry prevdayinEntry = prevdayIter.next();
											if("TOT".equals(prevdayinEntry.getKey())){
												if((prevdayinEntry.getValue().get("TOT").get("qtyLtrs"))!=0){
													prevprocDays=prevprocDays+1;
												}
											}
										}
									}
									else{
										prevunitMap=[:];
										prevunitMap["qtyLtrs"] = 0;
										Map prevdayValueEntryMap = (Map)prevdaytotEntry.getValue();
										Iterator prevdaytotValuesIter = prevdayValueEntryMap.entrySet().iterator();
										while(prevdaytotValuesIter.hasNext()){
											Map.Entry prevdayValEntry = prevdaytotValuesIter.next();
											if("TOT".equals(prevdayValEntry.getKey())){
												if((prevdayValEntry.getValue().get("TOT").get("qtyLtrs"))!=0){
													qtyLtrs=prevdayValEntry.getValue().get("TOT").get("qtyLtrs").setScale(0,BigDecimal.ROUND_HALF_UP);
													prevunitMap.put("qtyLtrs", qtyLtrs);
													totPrevLtrs=totPrevLtrs+qtyLtrs;
													unitprevtotLtrs=unitprevtotLtrs+qtyLtrs;
												}
											}
										}
										unitDetails = delegator.findOne("Facility", [facilityId : unit.get("facilityId")], false);
										if(UtilValidate.isNotEmpty(unitDetails.get("facilitySize"))){
											capacity=unitDetails.get("facilitySize");
											totPrevCapacity=totPrevCapacity+capacity;
											prevunitMap.put("capacity", capacity);
										}
										if(procDays!=0){
											prevAvg=unitprevtotLtrs/prevprocDays;
											totprevAvg=totprevAvg+prevAvg;
										}
										prevYearMap.putAt(prevDateKey, prevunitMap);
										prevYearMap.putAt("prevprocurementDays", prevprocDays);
									}
								}
							}
						}
					}
				}
				UnitMap.putAt("currYearMap", currYearMap);
				UnitMap.putAt("prevYearMap", prevYearMap);
				finalUnitMap.put(unit.get("facilityId"), UnitMap);
			}
			shedTotMap.putAt("totPrevLtrs", totPrevLtrs);
			shedTotMap.putAt("totcurrAvg", totcurrAvg);
			shedTotMap.putAt("totprevAvg", totprevAvg);
			shedTotMap.putAt("totPrevCapacity", totPrevCapacity);
			shedTotMap.putAt("totCurrLtrs", totCurrLtrs);
			shedTotMap.putAt("totCurrCapacity", totCurrCapacity);
			if((UtilValidate.isNotEmpty(totPrevLtrs) && totPrevLtrs!=0) || (UtilValidate.isNotEmpty(totCurrLtrs) && totCurrLtrs!=0) ){
				String districtName = district ;
				
				GenericValue geo = delegator.findOne("Geo", [geoId:district], false);
				if(UtilValidate.isNotEmpty(geo)&& UtilValidate.isNotEmpty(geo.get("geoName"))){
					districtName = geo.get("geoName");
					}
				shedWiseTotMap.put(districtName, shedTotMap);
				finalShedMap.put(districtName, finalUnitMap);
			}
			
		}
	}
			
context.putAt("shedWiseTotMap", shedWiseTotMap);
context.putAt("currentDateKey", currentDateKey);
context.putAt("prevDateKey", prevDateKey);
context.putAt("finalShedMap", finalShedMap);
context.putAt("shedNameMap", shedNameMap);


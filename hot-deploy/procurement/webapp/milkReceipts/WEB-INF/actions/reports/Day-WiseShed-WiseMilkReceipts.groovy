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
fromDateStart = null;
thruDateEnd =null;
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
prevDateStart = null;
prevDateEnd =null;

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
totalDays=UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd);
if(totalDays > 31){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
List currentDateKeysList = FastList.newInstance();
totalDays=totalDays+1;
for(int i=0; i <totalDays; i++){
	currentDayTimeStart = UtilDateTime.getDayStart(fromDateStart, i);
	currentDayTimeEnd = UtilDateTime.getDayEnd(currentDayTimeStart);
	date = UtilDateTime.toDateString(currentDayTimeStart,"dd/MM");
	currentDateKeysList.add(date);
}
context.putAt("currentDateKeysList", currentDateKeysList);

mccTypeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SHED" )  , null, null, null, false );
mccshedIds = EntityUtil.getFieldListFromEntityList(mccTypeList, "mccTypeId", true);
mccTypeShedMap = [:];
mccshedIds.each{ mccIds->
	shedList = EntityUtil.filterByAnd(mccTypeList, [mccTypeId : mccIds]);
	facilityIds = EntityUtil.getFieldListFromEntityList(shedList, "facilityId", true);
	mccTypeShedMap.put(mccIds , facilityIds);
}
context.put("mccTypeShedMap", mccTypeShedMap);

dctx = dispatcher.getDispatchContext();
currentShedFinalMap = [:];
currentGrandQtyDateMap =[:];
milkReceiptsCurrTotalMap =[:];
milkReceiptsCurrTotalMap = MilkReceiptReports.getAllMilkReceipts(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd]);
if(UtilValidate.isNotEmpty(milkReceiptsCurrTotalMap)){
	Map currYearReceipts = milkReceiptsCurrTotalMap.get("milkReceiptsMap");
	if(UtilValidate.isNotEmpty(currYearReceipts)){
		Iterator mccIter = currYearReceipts.entrySet().iterator();
		while(mccIter.hasNext()){
			Map.Entry mccEntry = mccIter.next();
			Map currentMccQtyDateMap = FastMap.newInstance();
			if(!"dayTotals".equals(mccEntry.getKey())){
				value = mccEntry.getValue();
				Iterator shedIter = value.entrySet().iterator();
				while(shedIter.hasNext()){
					Map.Entry shedEntry = shedIter.next();
					Map currentFacilityMap = FastMap.newInstance();
					if(!"dayTotals".equals(shedEntry.getKey())){
						shedValue = shedEntry.getValue();
						Iterator unitIter = shedValue.entrySet().iterator();
						while(unitIter.hasNext()){
							Map.Entry unitEntry = unitIter.next();
							shedTotalValue  = unitEntry.getValue();
							if("dayTotals".equals(unitEntry.getKey())){
								shedDayTotals  = unitEntry.getValue();
								if(UtilValidate.isNotEmpty(shedDayTotals)){
									for(dateKey in shedDayTotals.keySet()){
										if(dateKey != "TOT"){
											dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
											reqDateFormate = UtilDateTime.toDateString(dateFormate,"dd/MM");
											recQtyLtrs = shedDayTotals.get(dateKey).get("TOT").get("recdQtyLtrs");
											roundedQty = (recQtyLtrs/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
											if(UtilValidate.isEmpty(currentFacilityMap.get(reqDateFormate))){
												currentFacilityMap.put(reqDateFormate, roundedQty);
												if(UtilValidate.isEmpty(currentMccQtyDateMap) || (UtilValidate.isNotEmpty(currentMccQtyDateMap) && UtilValidate.isEmpty(currentMccQtyDateMap.get(reqDateFormate)))){
													currentMccQtyDateMap.put(reqDateFormate, recQtyLtrs);
												}else{
														currentMccQtyDateMap.put(reqDateFormate, currentMccQtyDateMap.get(reqDateFormate)+recQtyLtrs);
												}
												if(UtilValidate.isEmpty(currentGrandQtyDateMap) || (UtilValidate.isNotEmpty(currentGrandQtyDateMap) && UtilValidate.isEmpty(currentGrandQtyDateMap.get(reqDateFormate)))){
													currentGrandQtyDateMap.put(reqDateFormate, recQtyLtrs);
												}else{
														currentGrandQtyDateMap.put(reqDateFormate, currentGrandQtyDateMap.get(reqDateFormate)+recQtyLtrs);
												}
											}
										}else{
											recQtyLtrs = shedDayTotals.get(dateKey).get("TOT").get("recdQtyLtrs");
											roundedQty = (recQtyLtrs/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
											if(UtilValidate.isEmpty(currentFacilityMap.get("CUR.YEAR"))){
												currentFacilityMap.put("CUR.YEAR", roundedQty);
												if(UtilValidate.isEmpty(currentMccQtyDateMap) || (UtilValidate.isNotEmpty(currentMccQtyDateMap) && UtilValidate.isEmpty(currentMccQtyDateMap.get("CUR.YEAR")))){
													currentMccQtyDateMap.put("CUR.YEAR", recQtyLtrs);
												}else{
													currentMccQtyDateMap.put("CUR.YEAR", currentMccQtyDateMap.get("CUR.YEAR")+recQtyLtrs);
												}
												if(UtilValidate.isEmpty(currentGrandQtyDateMap) || (UtilValidate.isNotEmpty(currentGrandQtyDateMap) && UtilValidate.isEmpty(currentGrandQtyDateMap.get("CUR.YEAR")))){
													currentGrandQtyDateMap.put("CUR.YEAR", recQtyLtrs);
												}else{
													currentGrandQtyDateMap.put("CUR.YEAR", currentGrandQtyDateMap.get("CUR.YEAR")+recQtyLtrs);
												}
											}
										}
									}
								}
							}
						}			
					}
					if(UtilValidate.isNotEmpty(currentFacilityMap)){
						currentShedFinalMap.put(shedEntry.getKey(), currentFacilityMap);
					}
				}
				if(UtilValidate.isNotEmpty(currentMccQtyDateMap)){
					currentShedFinalMap.put(mccEntry.getKey(), currentMccQtyDateMap);
				}
			}
		}	
	}
}
Map tempCurrentMccMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(currentShedFinalMap)){
	for(mccKeys in mccTypeShedMap.keySet()){
	Map tempMonthMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(currentShedFinalMap.get(mccKeys))){
		tempMonthMap.putAll(currentShedFinalMap.get(mccKeys));
		Map tempMonthValueMap = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(tempMonthMap)){
				for(monthKey in tempMonthMap.keySet()){
					BigDecimal qtyLtrs = ((tempMonthMap.get(monthKey)/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
					if((tempMonthMap.get(monthKey)) != 0){
						tempMonthValueMap.put(monthKey,qtyLtrs);
					}
				}
				tempCurrentMccMap.put(mccKeys, tempMonthValueMap);
			}
		}
	}
}
Map tempCurrentGrndMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(currentGrandQtyDateMap)){
	Iterator shedCurrQtyIter = currentGrandQtyDateMap.entrySet().iterator();
	while (shedCurrQtyIter.hasNext()) {
		Map.Entry shedEntry = shedCurrQtyIter.next();
		if(UtilValidate.isNotEmpty(shedEntry.getKey())){
			BigDecimal roundedQty = (shedEntry.getValue()/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(roundedQty != 0){
				tempCurrentGrndMap.put(shedEntry.getKey(),roundedQty);
			}
		}
		else{
			tempCurrentGrndMap.put("CUR.YEAR",roundedQty);
		}
	}
}
context.put("currentShedFinalMap",currentShedFinalMap);
context.put("tempCurrentMccMap",tempCurrentMccMap);
context.put("tempCurrentGrndMap",tempCurrentGrndMap);

previousShedFinalMap = [:];
previousGrandQtyDateMap = [:];
milkReceiptsPrevTotalMap =[:];
milkReceiptsPrevTotalMap = MilkReceiptReports.getAllMilkReceipts(dctx , [fromDate: prevDateStart , thruDate: prevDateEnd]);
if(UtilValidate.isNotEmpty(milkReceiptsPrevTotalMap)){
	Map prevYearReceipts = milkReceiptsPrevTotalMap.get("milkReceiptsMap");
	if(UtilValidate.isNotEmpty(prevYearReceipts)){
		Iterator mccIter = prevYearReceipts.entrySet().iterator();
		while(mccIter.hasNext()){
		Map.Entry mccEntry = mccIter.next();
		Map previousMccQtyDateMap = FastMap.newInstance();
			if(!"dayTotals".equals(mccEntry.getKey())){
			value = mccEntry.getValue();
			Iterator shedIter = value.entrySet().iterator();
				while(shedIter.hasNext()){
				Map.Entry shedEntry = shedIter.next();
				Map previousFacilityMap = FastMap.newInstance();
					if(!"dayTotals".equals(shedEntry.getKey())){
					shedValue = shedEntry.getValue();
					Iterator unitIter = shedValue.entrySet().iterator();
						while(unitIter.hasNext()){
						Map.Entry unitEntry = unitIter.next();
						shedTotalValue  = unitEntry.getValue();
							if("dayTotals".equals(unitEntry.getKey())){
							shedDayTotals  = unitEntry.getValue();
								if(UtilValidate.isNotEmpty(shedDayTotals)){
									for(dateKey in shedDayTotals.keySet()){
										if(dateKey != "TOT"){
											dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
											reqDateFormate = UtilDateTime.toDateString(dateFormate,"dd/MM");
											recQtyLtrs = shedDayTotals.get(dateKey).get("TOT").get("recdQtyLtrs");
											roundedQty = (recQtyLtrs/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
											if(UtilValidate.isEmpty(previousFacilityMap.get(reqDateFormate))){
												previousFacilityMap.put(reqDateFormate, roundedQty);
												if(UtilValidate.isEmpty(previousMccQtyDateMap) || (UtilValidate.isNotEmpty(previousMccQtyDateMap) && UtilValidate.isEmpty(previousMccQtyDateMap.get(reqDateFormate)))){
													previousMccQtyDateMap.put(reqDateFormate, recQtyLtrs);
												}else{
														previousMccQtyDateMap.put(reqDateFormate, previousMccQtyDateMap.get(reqDateFormate)+recQtyLtrs);
												}
												if(UtilValidate.isEmpty(previousGrandQtyDateMap) || (UtilValidate.isNotEmpty(previousGrandQtyDateMap) && UtilValidate.isEmpty(previousGrandQtyDateMap.get(reqDateFormate)))){
													previousGrandQtyDateMap.put(reqDateFormate, recQtyLtrs);
												}else{
														previousGrandQtyDateMap.put(reqDateFormate, previousGrandQtyDateMap.get(reqDateFormate)+recQtyLtrs);
												}
											}
										}else{
											recQtyLtrs = shedDayTotals.get(dateKey).get("TOT").get("recdQtyLtrs");
											roundedQty = (recQtyLtrs/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
											if(UtilValidate.isEmpty(previousFacilityMap.get("PRE.YEAR"))){
												previousFacilityMap.put("PRE.YEAR", roundedQty);
												if(UtilValidate.isEmpty(previousMccQtyDateMap) || (UtilValidate.isNotEmpty(previousMccQtyDateMap) && UtilValidate.isEmpty(previousMccQtyDateMap.get("PRE.YEAR")))){
													previousMccQtyDateMap.put("PRE.YEAR", recQtyLtrs);
												}else{
														previousMccQtyDateMap.put("PRE.YEAR", previousMccQtyDateMap.get("PRE.YEAR")+recQtyLtrs);
												}
												if(UtilValidate.isEmpty(previousGrandQtyDateMap) || (UtilValidate.isNotEmpty(previousGrandQtyDateMap) && UtilValidate.isEmpty(previousGrandQtyDateMap.get("PRE.YEAR")))){
													previousGrandQtyDateMap.put("PRE.YEAR", recQtyLtrs);
												}else{
														previousGrandQtyDateMap.put("PRE.YEAR", previousGrandQtyDateMap.get("PRE.YEAR")+recQtyLtrs);
												}
											}
										}
									}
								}
							}
						}
					}
					if(UtilValidate.isNotEmpty(previousFacilityMap)){
						previousShedFinalMap.put(shedEntry.getKey(), previousFacilityMap);
					}
				}
				if(UtilValidate.isNotEmpty(previousMccQtyDateMap)){
					previousShedFinalMap.put(mccEntry.getKey(), previousMccQtyDateMap);
				}
			}
		}
	}
}
Map tempPreviousMccMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(previousShedFinalMap)){
	for(mccKeys in mccTypeShedMap.keySet()){
	Map tempPrevMonthMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(previousShedFinalMap.get(mccKeys))){
		tempPrevMonthMap.putAll(previousShedFinalMap.get(mccKeys));
		Map tempMonthValueMap = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(tempPrevMonthMap)){
				for(monthKey in tempPrevMonthMap.keySet()){
					BigDecimal qtyLtrs = ((tempPrevMonthMap.get(monthKey)/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
					if((tempPrevMonthMap.get(monthKey)) != 0){
						tempMonthValueMap.put(monthKey,qtyLtrs);
					}
				}
				if(UtilValidate.isNotEmpty(tempMonthValueMap)){
					tempPreviousMccMap.put(mccKeys, tempMonthValueMap);
				}
			}
		}
	}
}
Map tempPreviousGrndMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(previousGrandQtyDateMap)){
	Iterator shedPrevQtyIter = previousGrandQtyDateMap.entrySet().iterator();
	while (shedPrevQtyIter.hasNext()) {
		Map.Entry shedEntry = shedPrevQtyIter.next();
		if(UtilValidate.isNotEmpty(shedEntry.getKey())){
			BigDecimal roundedQty = (shedEntry.getValue()/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(roundedQty != 0){
				tempPreviousGrndMap.put(shedEntry.getKey(),roundedQty);
			}
		}
		else{
			tempPreviousGrndMap.put("PRE.YEAR",roundedQty);
		}
	}
}
context.put("previousShedFinalMap",previousShedFinalMap);
context.put("tempPreviousMccMap",tempPreviousMccMap);
context.put("tempPreviousGrndMap",tempPreviousGrndMap);

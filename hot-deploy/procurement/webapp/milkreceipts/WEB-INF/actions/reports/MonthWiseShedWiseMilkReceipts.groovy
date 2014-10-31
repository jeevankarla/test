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
List currMonthKeyList = FastList.newInstance();
	tempCurrentDate =  UtilDateTime.getMonthStart(fromDateStart);
	while(tempCurrentDate<= (UtilDateTime.getMonthEnd(thruDateEnd,timeZone, locale))){
		date = UtilDateTime.toDateString(tempCurrentDate,"MM/yyyy");
		currMonthKeyList.add(date);
		tempCurrentDate = UtilDateTime.getMonthEnd(tempCurrentDate,timeZone, locale);
		tempCurrentDate=UtilDateTime.addDaysToTimestamp(tempCurrentDate, 1);
		noofDays = currMonthKeyList.size()
	}
context.putAt("currMonthKeyList", currMonthKeyList);
context.putAt("noofDays", noofDays);

List prevMonthKeyList = FastList.newInstance();
tempPreviousDate =  UtilDateTime.getMonthStart(prevDateStart);
while(tempPreviousDate<= (UtilDateTime.getMonthEnd(prevDateEnd,timeZone, locale))){
	date = UtilDateTime.toDateString(tempPreviousDate,"MM/yyyy");
	prevMonthKeyList.add(date);
	tempPreviousDate = UtilDateTime.getMonthEnd(tempPreviousDate,timeZone, locale);
	tempPreviousDate=UtilDateTime.addDaysToTimestamp(tempPreviousDate, 1);
}
context.putAt("prevMonthKeyList", prevMonthKeyList);

totalDays=totalDays+1;
dctx = dispatcher.getDispatchContext();

mccTypeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SHED" )  , null, null, null, false );
mccshedIds = EntityUtil.getFieldListFromEntityList(mccTypeList, "mccTypeId", true);
mccTypeShedMap = [:];
mccshedIds.each{ mccIds->
	shedList = EntityUtil.filterByAnd(mccTypeList, [mccTypeId : mccIds]);
	List tempShedList = FastList.newInstance();
	for(shed in shedList){
		Map shedMap = FastMap.newInstance();
		shedMap.put("facilityId", shed.get("facilityId"));
		shedMap.put("mccCode", shed.get("mccCode"));
		if(UtilValidate.isNotEmpty(shedMap.get("mccCode"))){
			shedMap.put("mccCode", Integer.parseInt(shedMap.get("mccCode")));
		}
		tempShedList.add(shedMap);
	}
	tempShedList = UtilMisc.sortMaps(tempShedList, UtilMisc.toList("mccCode"));
	List facilityIdsList = FastList.newInstance();
	for(tempShed in tempShedList){
		facilityIdsList.add(tempShed.get("facilityId"));
	}
	mccTypeShedMap.put(mccIds , facilityIdsList);
}
context.put("mccTypeShedMap", mccTypeShedMap);
currentShedFinalMap =[:];
mpfMilkReceiptsMap =[:];
mpfMilkReceiptsMap = MilkReceiptReports.getAllMilkReceipts(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd]);
Map currentGrandDateMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(mpfMilkReceiptsMap)){
Map milkReceipts = mpfMilkReceiptsMap.get("milkReceiptsMap");
	if(UtilValidate.isNotEmpty(milkReceipts)){
	Iterator mccIter = milkReceipts.entrySet().iterator();
		while(mccIter.hasNext()){
			Map.Entry mccEntry = mccIter.next();
			if(!"dayTotals".equals(mccEntry.getKey())){
				Map currentMccDateMap = FastMap.newInstance();
				value = mccEntry.getValue();
				Iterator shedIter = value.entrySet().iterator();
				while(shedIter.hasNext()){
					Map.Entry shedEntry = shedIter.next();
					if(!"dayTotals".equals(shedEntry.getKey())){
						unitValue =shedEntry.getValue();
						Map currentQtyDateMap = FastMap.newInstance();
						if(UtilValidate.isNotEmpty(unitValue)){
						shedDayTotals = unitValue.get("dayTotals");
							if(UtilValidate.isNotEmpty(shedDayTotals)){
								for(dateKey in shedDayTotals.keySet()){
									if(dateKey != "TOT"){
										dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
										reqDateFormate = UtilDateTime.toDateString(dateFormate,"MM/yyyy");	
										recQtyLtrs = shedDayTotals.get(dateKey).get("TOT");
										roundedQty = (recQtyLtrs.get("recdQtyLtrs")).setScale(2,BigDecimal.ROUND_HALF_UP);
										roundedFat = (recQtyLtrs.get("recdKgFat")).setScale(2,BigDecimal.ROUND_HALF_UP);
										roundedSnf = (recQtyLtrs.get("recdKgSnf")).setScale(2,BigDecimal.ROUND_HALF_UP);
										if(UtilValidate.isEmpty(currentQtyDateMap.get(reqDateFormate))){
											Map shedMap = FastMap.newInstance();
											shedMap.put("qtyLtrs",0);
											shedMap.put("kgFat",0);
											shedMap.put("kgSnf",0);
											shedMap.put("qtyLtrs", roundedQty);
											shedMap.put("kgFat", roundedFat);
											shedMap.put("kgSnf", roundedSnf);
											currentQtyDateMap.put(reqDateFormate, shedMap);
										}else{
											Map tempMap=FastMap.newInstance();										
											tempMap.putAll(currentQtyDateMap.get(reqDateFormate));
											tempMap.put("qtyLtrs", tempMap.get("qtyLtrs")+roundedQty);
											tempMap.put("kgFat", tempMap.get("kgFat")+roundedFat);
											tempMap.put("kgSnf", tempMap.get("kgSnf")+roundedSnf);
											currentQtyDateMap.put(reqDateFormate, tempMap);
										}
										if(UtilValidate.isEmpty(currentMccDateMap.get(reqDateFormate))){
											Map shedMccMap = FastMap.newInstance();
											shedMccMap.put("qtyLtrs",0);
											shedMccMap.put("kgFat",0);
											shedMccMap.put("kgSnf",0);
											shedMccMap.put("qtyLtrs", roundedQty);
											shedMccMap.put("kgFat", roundedFat);
											shedMccMap.put("kgSnf", roundedSnf);
											currentMccDateMap.put(reqDateFormate, shedMccMap);
										}else{
											Map tempMccMap=FastMap.newInstance();
											tempMccMap.putAll(currentMccDateMap.get(reqDateFormate));
											tempMccMap.put("qtyLtrs", tempMccMap.get("qtyLtrs")+roundedQty);
											tempMccMap.put("kgFat", tempMccMap.get("kgFat")+roundedFat);
											tempMccMap.put("kgSnf", tempMccMap.get("kgSnf")+roundedSnf);
											currentMccDateMap.put(reqDateFormate, tempMccMap);
										}
										if(UtilValidate.isEmpty(currentGrandDateMap.get(reqDateFormate))){
											Map shedGrandMap = FastMap.newInstance();
											shedGrandMap.put("qtyLtrs",0);
											shedGrandMap.put("kgFat",0);
											shedGrandMap.put("kgSnf",0);
											shedGrandMap.put("qtyLtrs", roundedQty);
											shedGrandMap.put("kgFat", roundedFat);
											shedGrandMap.put("kgSnf", roundedSnf);
											currentGrandDateMap.put(reqDateFormate, shedGrandMap);
										}else{
											Map tempGrandMap=FastMap.newInstance();
											tempGrandMap.putAll(currentGrandDateMap.get(reqDateFormate));
											tempGrandMap.put("qtyLtrs", tempGrandMap.get("qtyLtrs")+roundedQty);
											tempGrandMap.put("kgFat", tempGrandMap.get("kgFat")+roundedFat);
											tempGrandMap.put("kgSnf", tempGrandMap.get("kgSnf")+roundedSnf);
											currentGrandDateMap.put(reqDateFormate, tempGrandMap);
										}
									}
								}
							}
						}
					if(UtilValidate.isNotEmpty(currentQtyDateMap)){
						currentShedFinalMap.put(shedEntry.getKey(), currentQtyDateMap);
					}
					}
				}
				if(UtilValidate.isNotEmpty(currentMccDateMap)){
					currentShedFinalMap.put(mccEntry.getKey(), currentMccDateMap);
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
					Map tempMap = FastMap.newInstance();
					BigDecimal qtyLtrs = ((tempMonthMap.get(monthKey).get("qtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
					BigDecimal kgFat = ((tempMonthMap.get(monthKey).get("kgFat")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
					BigDecimal kgSnf = ((tempMonthMap.get(monthKey).get("kgSnf")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
					if((tempMonthMap.get(monthKey).get("qtyLtrs")) != 0){
						tempMap.put("qtyLtrs", qtyLtrs);
						tempMap.put("kgFat", kgFat);
						tempMap.put("kgSnf", kgSnf);
						tempMonthValueMap.put(monthKey,tempMap);
					}
				}		
				tempCurrentMccMap.put(mccKeys, tempMonthValueMap);
			}
		}	
	}
}


Map tempCurrentGrndMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(currentGrandDateMap)){
	for(dateKey in currentGrandDateMap.keySet()){
		Map tempMap = FastMap.newInstance();
		tempMap.putAll(currentGrandDateMap.get(dateKey));
		BigDecimal qtyLtrs = ((tempMap.get("qtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
		BigDecimal kgFat = ((tempMap.get("kgFat")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
		BigDecimal kgSnf = ((tempMap.get("kgSnf")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
		if(tempMap.get("qtyLtrs") != 0){
			tempMap.put("qtyLtrs", qtyLtrs);
			tempMap.put("kgFat", kgFat);
			tempMap.put("kgSnf", kgSnf);
			tempCurrentGrndMap.put(dateKey, tempMap);
		}
	}
}
context.put("currentShedFinalMap",currentShedFinalMap);
context.put("tempCurrentMccMap",tempCurrentMccMap);
context.put("tempCurrentGrndMap",tempCurrentGrndMap);

Map shedCurrentFinalMap = FastMap.newInstance();
mccTypeShedMap.each{ mccTypeList->
	Map currentMccQtyMonthMap = FastMap.newInstance();
	mccDetails = mccTypeList.getValue();
	mccDetails.each{ shed->
		if(UtilValidate.isNotEmpty(currentShedFinalMap)){
			Iterator shedCurrQtyIter = currentShedFinalMap.entrySet().iterator();
			while (shedCurrQtyIter.hasNext()) {
				Map.Entry shedEntry = shedCurrQtyIter.next();
				if(UtilValidate.isNotEmpty(shedEntry.getKey()) && (shedEntry.getKey() == shed) ){
					monthQtyMap =  shedEntry.getValue();
					Map tempCurrMonthMap = FastMap.newInstance();
					Iterator monthWiseIter = monthQtyMap.entrySet().iterator();
					while (monthWiseIter.hasNext()){
						Map.Entry monthCurrEntry = monthWiseIter.next();
						if(UtilValidate.isNotEmpty(monthCurrEntry.getKey())){
							qtyLtrs = ((monthCurrEntry.getValue().get("qtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
							kgFat = ((monthCurrEntry.getValue().get("kgFat")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
							kgSnf = ((monthCurrEntry.getValue().get("kgSnf")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
							if(UtilValidate.isEmpty(tempCurrMonthMap.get(monthCurrEntry.getKey()))){
								Map CurrentShedMap =FastMap.newInstance();
								CurrentShedMap.put("qtyLtrs", qtyLtrs);
								CurrentShedMap.put("kgFat", kgFat);
								CurrentShedMap.put("kgSnf", kgSnf);
								tempCurrMonthMap.put(monthCurrEntry.getKey(), CurrentShedMap);
							}else{
								Map tempCurrShedMap=FastMap.newInstance();
								tempCurrShedMap.putAll(tempCurrMonthMap.get(monthCurrEntry.getKey()));
								tempCurrShedMap.put("qtyLtrs", qtyLtrs);
								tempCurrShedMap.put("kgFat",  kgFat);
								tempCurrShedMap.put("kgSnf", kgSnf);
								tempCurrMonthMap.put(monthCurrEntry.getKey(), tempCurrShedMap);
							}
							
						}
					}
					
					if(UtilValidate.isNotEmpty(tempCurrMonthMap)){
						Map tempMapDetails = FastMap.newInstance();
						tempMapDetails.putAll(tempCurrMonthMap);
						shedCurrentFinalMap.put(shedEntry.getKey(), tempMapDetails);
					}
				}
				
			}
		}
	}
}
context.put("shedCurrentFinalMap",shedCurrentFinalMap);

previousShedFinalMap =[:];
milkReceiptsPrevTotalMap =[:];
milkReceiptsPrevTotalMap = MilkReceiptReports.getAllMilkReceipts(dctx , [fromDate: prevDateStart , thruDate: prevDateEnd]);
Map previousGrandDateMap =FastMap.newInstance();
if(UtilValidate.isNotEmpty(milkReceiptsPrevTotalMap)){
	Map prevYearReceipts = milkReceiptsPrevTotalMap.get("milkReceiptsMap");
	if(UtilValidate.isNotEmpty(prevYearReceipts)){
		Iterator mccIter = prevYearReceipts.entrySet().iterator();
		while(mccIter.hasNext()){
			Map.Entry mccEntry = mccIter.next();
			Map previousMccQtyMonthMap = FastMap.newInstance();
			if(!"dayTotals".equals(mccEntry.getKey())){
				Map previousMccDateMap = FastMap.newInstance();
				preValue = mccEntry.getValue();
				Iterator shedIter = preValue.entrySet().iterator();
				while(shedIter.hasNext()){
					Map.Entry preShedEntry = shedIter.next();
					previousQtyDateMap =[:];
					if(!"dayTotals".equals(preShedEntry.getKey())){
						preUnitValue =preShedEntry.getValue();
						if(UtilValidate.isNotEmpty(preUnitValue)){
							preShedDayTotals = preUnitValue.get("dayTotals");
							if(UtilValidate.isNotEmpty(preShedDayTotals)){
								for(dateKey in preShedDayTotals.keySet()){
									if(dateKey != "TOT"){
										dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
										reqDateFormate = UtilDateTime.toDateString(dateFormate,"MM/yyyy");
										recQtyLtrs = preShedDayTotals.get(dateKey).get("TOT");
										roundedPrevQty = (recQtyLtrs.get("recdQtyLtrs").setScale(2,BigDecimal.ROUND_HALF_UP));
										roundedPrevFat = (recQtyLtrs.get("recdKgFat").setScale(2,BigDecimal.ROUND_HALF_UP));
										roundedPrevSnf = (recQtyLtrs.get("recdKgSnf").setScale(2,BigDecimal.ROUND_HALF_UP));
										if(UtilValidate.isEmpty(previousQtyDateMap.get(reqDateFormate))){
											Map prevShedMap =FastMap.newInstance();
											prevShedMap.put("qtyLtrs",0);
											prevShedMap.put("kgFat",0);
											prevShedMap.put("kgSnf",0);
											prevShedMap.put("qtyLtrs", roundedPrevQty);
											prevShedMap.put("kgFat", roundedPrevFat);
											prevShedMap.put("kgSnf", roundedPrevSnf);
											previousQtyDateMap.put(reqDateFormate, prevShedMap);
										}else{
											Map tempPrevMap=FastMap.newInstance();
											tempPrevMap.putAll(previousQtyDateMap.get(reqDateFormate));
											tempPrevMap.put("qtyLtrs", tempPrevMap.get("qtyLtrs")+roundedPrevQty);
											tempPrevMap.put("kgFat", tempPrevMap.get("kgFat")+roundedPrevFat);
											tempPrevMap.put("kgSnf", tempPrevMap.get("kgSnf")+roundedPrevSnf);
											previousQtyDateMap.put(reqDateFormate, tempPrevMap);
										}
										if(UtilValidate.isEmpty(previousMccDateMap.get(reqDateFormate))){
											Map prevShedMap =FastMap.newInstance();
											prevShedMap.put("qtyLtrs",0);
											prevShedMap.put("kgFat",0);
											prevShedMap.put("kgSnf",0);
											prevShedMap.put("qtyLtrs", roundedPrevQty);
											prevShedMap.put("kgFat", roundedPrevFat);
											prevShedMap.put("kgSnf", roundedPrevSnf);
											previousMccDateMap.put(reqDateFormate, prevShedMap);
										}else{
											Map tempPrevMap=FastMap.newInstance();
											tempPrevMap.putAll(previousMccDateMap.get(reqDateFormate));
											tempPrevMap.put("qtyLtrs", tempPrevMap.get("qtyLtrs")+roundedPrevQty);
											tempPrevMap.put("kgFat", tempPrevMap.get("kgFat")+roundedPrevFat);
											tempPrevMap.put("kgSnf", tempPrevMap.get("kgSnf")+roundedPrevSnf);
											previousMccDateMap.put(reqDateFormate, tempPrevMap);
										}
										if(UtilValidate.isEmpty(previousGrandDateMap.get(reqDateFormate))){
											Map prevShedMap =FastMap.newInstance();
											prevShedMap.put("qtyLtrs",0);
											prevShedMap.put("kgFat",0);
											prevShedMap.put("kgSnf",0);
											prevShedMap.put("qtyLtrs", roundedPrevQty);
											prevShedMap.put("kgFat", roundedPrevFat);
											prevShedMap.put("kgSnf", roundedPrevSnf);
											previousGrandDateMap.put(reqDateFormate, prevShedMap);
										}else{
											Map tempPrevMap=FastMap.newInstance();
											tempPrevMap.putAll(previousGrandDateMap.get(reqDateFormate));
											tempPrevMap.put("qtyLtrs", tempPrevMap.get("qtyLtrs")+roundedPrevQty);
											tempPrevMap.put("kgFat", tempPrevMap.get("kgFat")+roundedPrevFat);
											tempPrevMap.put("kgSnf", tempPrevMap.get("kgSnf")+roundedPrevSnf);
											previousGrandDateMap.put(reqDateFormate, tempPrevMap);
										}
									}
								}
							}
						}
						if(UtilValidate.isNotEmpty(previousQtyDateMap)){
							previousShedFinalMap.put(preShedEntry.getKey(), previousQtyDateMap);
						}
					}
				}
				if(UtilValidate.isNotEmpty(previousMccDateMap)){
					previousShedFinalMap.put(mccEntry.getKey(), previousMccDateMap);
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
					Map tempMap = FastMap.newInstance();
					BigDecimal qtyLtrs = ((tempPrevMonthMap.get(monthKey).get("qtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
					BigDecimal kgFat = ((tempPrevMonthMap.get(monthKey).get("kgFat")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
					BigDecimal kgSnf = ((tempPrevMonthMap.get(monthKey).get("kgSnf")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
					if((tempPrevMonthMap.get(monthKey).get("qtyLtrs")) != 0){
						tempMap.put("qtyLtrs", qtyLtrs);
						tempMap.put("kgFat", kgFat);
						tempMap.put("kgSnf", kgSnf);
						tempMonthValueMap.put(monthKey,tempMap);
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
if(UtilValidate.isNotEmpty(previousGrandDateMap)){
	for(dateKey in previousGrandDateMap.keySet()){
		Map tempMap = FastMap.newInstance();
		tempMap.putAll(previousGrandDateMap.get(dateKey));
		BigDecimal qtyLtrs = ((tempMap.get("qtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
		BigDecimal kgFat = ((tempMap.get("kgFat")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
		BigDecimal kgSnf = ((tempMap.get("kgSnf")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
		if(tempMap.get("qtyLtrs") != 0){
			tempMap.put("qtyLtrs", qtyLtrs);
			tempMap.put("kgFat", kgFat);
			tempMap.put("kgSnf", kgSnf);
			tempPreviousGrndMap.put(dateKey, tempMap);
		}
	}
}
context.put("previousShedFinalMap",previousShedFinalMap);
context.put("tempPreviousMccMap",tempPreviousMccMap);
context.put("tempPreviousGrndMap",tempPreviousGrndMap);

Map shedPreviousFinalMap = FastMap.newInstance();
mccTypeShedMap.each{ mccTypeList->
	Map previousMccQtyMonthMap = FastMap.newInstance();
	mccDetails = mccTypeList.getValue();
	mccDetails.each{ shed->
		if(UtilValidate.isNotEmpty(previousShedFinalMap)){
			Iterator shedPrevQtyIter = previousShedFinalMap.entrySet().iterator();
			while (shedPrevQtyIter.hasNext()) {
				Map.Entry shedEntry = shedPrevQtyIter.next();
				if(UtilValidate.isNotEmpty(shedEntry.getKey()) && (shedEntry.getKey() == shed) ){
					monthQtyMap =  shedEntry.getValue();
					Map tempPrevMonthMap = FastMap.newInstance();
					Iterator monthWiseIter = monthQtyMap.entrySet().iterator();
					while (monthWiseIter.hasNext()){
						Map.Entry monthPrevEntry = monthWiseIter.next();
						if(UtilValidate.isNotEmpty(monthPrevEntry.getKey())){
							qtyLtrs = ((monthPrevEntry.getValue().get("qtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP));
							kgFat = ((monthPrevEntry.getValue().get("kgFat")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
							kgSnf = ((monthPrevEntry.getValue().get("kgSnf")/1000).setScale(2,BigDecimal.ROUND_HALF_UP));
							if(UtilValidate.isEmpty(tempPrevMonthMap.get(monthPrevEntry.getKey()))){
								Map previousShedMap =FastMap.newInstance();
								previousShedMap.put("qtyLtrs", qtyLtrs);
								previousShedMap.put("kgFat", kgFat);
								previousShedMap.put("kgSnf", kgSnf);
								tempPrevMonthMap.put(monthPrevEntry.getKey(), previousShedMap);
							}else{
								Map tempPrevMap=FastMap.newInstance();
								tempPrevMap.putAll(tempPrevMonthMap.get(monthPrevEntry.getKey()));
								tempPrevMap.put("qtyLtrs", qtyLtrs);
								tempPrevMap.put("kgFat", kgFat);
								tempPrevMap.put("kgSnf", kgSnf);
								tempPrevMonthMap.put(monthPrevEntry.getKey(), tempPrevMap);
							}
						}
					}
					if(UtilValidate.isNotEmpty(tempPrevMonthMap)){
						Map tempPrevMapDetails = FastMap.newInstance();
						tempPrevMapDetails.putAll(tempPrevMonthMap);
						shedPreviousFinalMap.put(shedEntry.getKey(), tempPrevMapDetails);
					}
				}
				
			}
		}
	}
}
context.put("shedPreviousFinalMap",shedPreviousFinalMap);

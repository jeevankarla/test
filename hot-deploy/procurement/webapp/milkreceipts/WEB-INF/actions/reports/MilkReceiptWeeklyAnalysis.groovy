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
thruDateEnd = null;
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

totalDays=UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd);
if(totalDays > 31){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
List dateKeysList = FastList.newInstance();
List weekKeysList = FastList.newInstance();
Map dayKeysMap = FastMap.newInstance();
totalDays=totalDays+1;
for(int i=0; i <totalDays; i++){
	currentDayTimeStart = UtilDateTime.getDayStart(fromDateStart, i);
	currentDayTimeEnd = UtilDateTime.getDayEnd(currentDayTimeStart);
	date = UtilDateTime.toDateString(currentDayTimeStart,"dd/MM/yyyy");
	dateKeysList.add(date);
	dayKeysMap.put(UtilDateTime.toDateString(currentDayTimeStart,"dd/MM/yyyy"), date);
}
context.putAt("dateKeysList", dateKeysList);
context.putAt("dayKeysMap", dayKeysMap);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

finalMap =[:];
finalShedMap = [:];
shedTotalMap = [:];
weekMap=[:];

mccTypeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SHED" )  , null, null, null, false );
mccshedIds = EntityUtil.getFieldListFromEntityList(mccTypeList, "mccTypeId", true);
mccTypeShedMap = [:];
mccshedIds.each{ mccIds->
	if("FEDERATION".equals(mccIds)){
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
}
context.put("mccTypeShedMap", mccTypeShedMap);

grandTotMap=[:];
mccshedIds.each{ mccIds->
	if("FEDERATION".equals(mccIds)){
		shedList = EntityUtil.filterByAnd(mccTypeList, [mccTypeId : mccIds]);
		shedList.each{ shed->
			totalMap=[:];
			grandtotQty=0;
			grandtotkgFat=0;
			grandkgSnf=0;
			shedTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: shed.facilityId]);
			if(UtilValidate.isNotEmpty(shedTotals)){
				Iterator shedTotIter = shedTotals.entrySet().iterator();
				while(shedTotIter.hasNext()){
					Map.Entry entry = shedTotIter.next();
					if(!"dayTotals".equals(entry.getKey())){
						unitValue= entry.getValue();
						currentQtyDateMap =[:];
						if(UtilValidate.isNotEmpty(unitValue)){
							shedDayTotals = unitValue.get("dayTotals");
							if(UtilValidate.isNotEmpty(shedDayTotals)){
								set=shedDayTotals.keySet();
								for(dateKey in set){
									if(dateKey != "TOT"){
										reqDateFormate=null;
										dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
										reqDateFormate = UtilDateTime.toDateString(dateFormate,"dd/MM/yyyy");
										recQtyLtrs = shedDayTotals.get(dateKey).get("TOT");
										roundedQty = (recQtyLtrs.get("recdQtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
										roundedFat = (recQtyLtrs.get("recdKgFat")/1000).setScale(2,BigDecimal.ROUND_HALF_UP);
										roundedSnf = (recQtyLtrs.get("recdKgSnf")/1000).setScale(2,BigDecimal.ROUND_HALF_UP);
										grandtotQty=grandtotQty+roundedQty;
										grandtotkgFat=grandtotkgFat+roundedFat;
										grandkgSnf=grandkgSnf+roundedSnf;
										if(UtilValidate.isEmpty(currentQtyDateMap.get(reqDateFormate))){
											shedMap = [:];
											shedMap.put("qtyLtrs",0);
											shedMap.put("kgFat",0);
											shedMap.put("kgSnf",0);
											shedMap.put("qtyLtrs", roundedQty);
											shedMap.put("kgFat", roundedFat);
											shedMap.put("kgSnf", roundedSnf);
											currentQtyDateMap.put(reqDateFormate,shedMap);
											finalMap.put(entry.getKey(), currentQtyDateMap);
										}
									}
								}
							}
						}
					}
					
				}
			}
			totalMap.put("grandtotQty", grandtotQty);
			totalMap.put("grandtotkgFat", grandtotkgFat);
			totalMap.put("grandkgSnf", grandkgSnf);
			grandTotMap.put(shed.facilityId,totalMap);
		}
	}
}
context.putAt("finalMap", finalMap);
context.putAt("grandTotMap", grandTotMap);

int weekCount = 1;
int noOfDays = 7;
int dayCount = 0;
Map initMap = FastMap.newInstance();
initMap.put("qtyLtrs",0);
initMap.put("kgFat",0);
initMap.put("kgSnf",0);

Map WeekWiseMap = FastMap.newInstance();
for(dateKey in dateKeysList){
	dayCount = dayCount+1;
	String weekKey = "week"+weekCount;
	if(UtilValidate.isNotEmpty(finalMap)){
	for(shed in finalMap.keySet()){
		Map finalShedValue = FastMap.newInstance();
		finalShedValue.putAll(finalMap.get(shed));
		if(UtilValidate.isEmpty(WeekWiseMap.get(weekKey))){
			Map weekShedMap = FastMap.newInstance();
			Map weekShedQtyMap = FastMap.newInstance();
			weekShedQtyMap.putAll(initMap);
			weekShedMap.put(shed, weekShedQtyMap);
			WeekWiseMap.put(weekKey, weekShedMap);
			}
			Map tempWeekMap = FastMap.newInstance();
			tempWeekMap.putAll(WeekWiseMap.get(weekKey));
			Map tempShedQtyMap = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(tempWeekMap.get(shed))){
			tempShedQtyMap.putAll(tempWeekMap.get(shed));
			}else{
			tempShedQtyMap.putAll(initMap);
			}
			if(UtilValidate.isNotEmpty(finalShedValue.get(dateKey))){
				Map tempDateValuesMap = FastMap.newInstance();
				tempDateValuesMap.putAll(finalShedValue.get(dateKey));
				for(qtyKey in tempShedQtyMap.keySet()){
					tempShedQtyMap.put(qtyKey, tempShedQtyMap.get(qtyKey)+ tempDateValuesMap.get(qtyKey));
				}
			}
			tempWeekMap.put(shed, tempShedQtyMap);
			WeekWiseMap.put(weekKey, tempWeekMap);
			if(dayCount == noOfDays){
				dayCount = 0;
				weekKeysList.add(weekCount);
				weekCount = weekCount+1;
				if(weekCount>1){
					noOfDays = 8;
				}
			}
		}
	}
}

context.putAt("weekKeysList", weekKeysList);
context.putAt("WeekWiseMap", WeekWiseMap);



	
	
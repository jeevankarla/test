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

prevfromDate=UtilDateTime.previousYearDateString(fromDateStart.toString());
prevthruDate=UtilDateTime.previousYearDateString(thruDateEnd.toString());

def sdf2=new SimpleDateFormat("yyyy-MM-dd");
try{
	if(fromDate){
		prevDateStart=UtilDateTime.getDayStart(new java.sql.Timestamp(sdf2.parse(prevfromDate).getTime()));
	}
	if(thruDate){
		prevDateEnd=UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf2.parse(prevthruDate).getTime()));
	}
}catch(ParseException e){
		Debug.logError(e,"Cannot parse date string:"+ e,"");
		context.errorMessage="Cannot parse date string:"+ e;
		return;
}
context.put("prevDateStart",prevDateStart);
context.put("prevDateEnd",prevDateEnd);

currTotalDays=UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd);
currTotalDays=currTotalDays+1;
context.putAt("currTotalDays", currTotalDays);
prevTotalDays=UtilDateTime.getIntervalInDays(prevDateStart, prevDateEnd);
prevTotalDays=prevTotalDays+1;
context.putAt("prevTotalDays", prevTotalDays);
if(currTotalDays>366){
	Debug.logError("You can not choose more than 366 days.","");
	context.errorMessage="You can not choose more than 366 days.";
	return;
}
dctx = dispatcher.getDispatchContext();

mccTypeList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SHED" )  , null, null, null, false );
Collections.reverse(mccTypeList);
mccshedIds = EntityUtil.getFieldListFromEntityList(mccTypeList,"mccTypeId", true);
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
currentGrandTotalMap=[:];
currentShedFinalMap=[:];
currentShedTotalMap=[:];
mpfMilkReceiptsMap=[:];
grandTotal=0;
totalAvgPerDay=0;

mpfMilkReceiptsMap=MilkReceiptReports.getAllMilkReceipts(dctx,[fromDate:fromDateStart,thruDate:thruDateEnd] );
if(UtilValidate.isNotEmpty(mpfMilkReceiptsMap)){
	Map milkReceipts=mpfMilkReceiptsMap.get("milkReceiptsMap");	
		if(UtilValidate.isNotEmpty(milkReceipts)){
			Iterator mccIter=milkReceipts.entrySet().iterator();
			while(mccIter.hasNext()){
				Map.Entry mccEntry=mccIter.next();
				if(!"dayTotals".equals(mccEntry.getKey())){
					totals=0;
					value=mccEntry.getValue();
					Iterator shedIter=value.entrySet().iterator();
					while(shedIter.hasNext()){
						Map.Entry shedEntry=shedIter.next();
						if(!"dayTotals".equals(shedEntry.getKey())){
							unitValues=shedEntry.getValue();
							Iterator unitIter=unitValues.entrySet().iterator();
							while(unitIter.hasNext()){
								Map.Entry unitEntry=unitIter.next();
								if("dayTotals".equals(unitEntry.getKey())){
									unitValueTotals=unitEntry.getValue();
									unitTotals=unitValueTotals.get("TOT");
									recdQtyLtrs=unitTotals.get("TOT").get("recdQtyLtrs");
									roundQtyLtrs=(recdQtyLtrs/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
									totals=totals+roundQtyLtrs;
									currentShedFinalMap.put(shedEntry.getKey(), roundQtyLtrs);
								}
								
								
							}
							
						}
					}
					currentShedTotalMap.put(mccEntry.getKey(), totals);
					avgPerDay=(totals/currTotalDays).setScale(2,BigDecimal.ROUND_HALF_UP);
					grandTotal=grandTotal+totals;
					totalAvgPerDay=totalAvgPerDay+avgPerDay;
					currentGrandTotalMap.put("currentGrandTotal", grandTotal);
					currentGrandTotalMap.put("totalAvgPerDay", totalAvgPerDay);
				}
		}	
	}
}
context.putAt("currentShedFinalMap", currentShedFinalMap);
context.putAt("currentShedTotalMap", currentShedTotalMap);
context.putAt("currentGrandTotalMap", currentGrandTotalMap);
	


prevGrandTotal=0;
prevTotalAvgPerDay=0;
prevGrandTotalMap=[:];
prevShedFinalMap=[:];
prevShedTotalMap=[:];
prevMpfMilkReceiptsMap=[:];
prevMpfMilkReceiptsMap=MilkReceiptReports.getAllMilkReceipts(dctx,[fromDate:prevDateStart,thruDate:prevDateEnd] );
if(UtilValidate.isNotEmpty(prevMpfMilkReceiptsMap)){
	Map prevMilkReceipts=prevMpfMilkReceiptsMap.get("milkReceiptsMap");
		if(UtilValidate.isNotEmpty(prevMilkReceipts)){
			Iterator mccIter=prevMilkReceipts.entrySet().iterator();
			while(mccIter.hasNext()){
				Map.Entry mccEntry=mccIter.next();
				if(!"dayTotals".equals(mccEntry.getKey())){
					totals=0;
					value=mccEntry.getValue();
					Iterator shedIter=value.entrySet().iterator();
					while(shedIter.hasNext()){
						Map.Entry shedEntry=shedIter.next();
						if(!"dayTotals".equals(shedEntry.getKey())){
							unitValues=shedEntry.getValue();
							Iterator unitIter=unitValues.entrySet().iterator();
							while(unitIter.hasNext()){
								Map.Entry unitEntry=unitIter.next();
								if("dayTotals".equals(unitEntry.getKey())){	
									unitValueTotals=unitEntry.getValue();
									unitTotals=unitValueTotals.get("TOT");
									recdQtyLtrs=unitTotals.get("TOT").get("recdQtyLtrs");
									roundQtyLtrs=(recdQtyLtrs/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
									totals=totals+roundQtyLtrs;
									prevShedFinalMap.put(shedEntry.getKey(), roundQtyLtrs);
								}
							}
						}
					}
					prevShedTotalMap.put(mccEntry.getKey(), totals);
					avgPerDay=(totals/prevTotalDays).setScale(2,BigDecimal.ROUND_HALF_UP);
					prevGrandTotal=prevGrandTotal+totals;
					prevTotalAvgPerDay=prevTotalAvgPerDay+avgPerDay;
					prevGrandTotalMap.put("prevGrandTotal", prevGrandTotal);
					prevGrandTotalMap.put("prevTotalAvgPerDay", prevTotalAvgPerDay);
				}
		}
	}
}
context.putAt("prevShedFinalMap", prevShedFinalMap);
context.putAt("prevShedTotalMap", prevShedTotalMap);
context.putAt("prevGrandTotalMap", prevGrandTotalMap);

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}

	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}

} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);
dctx = dispatcher.getDispatchContext();

facilityCode = parameters.facilityCode;
if(UtilValidate.isEmpty(parameters.shedId)){
	Debug.logError("shedId Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
if(UtilValidate.isEmpty(parameters.unitId)){
	Debug.logError("unitId Cannot Be Empty","");
	context.errorMessage = "No Unit Has Been Selected.......!";
	return;
}

orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
orderAdjItemsList = UtilMisc.sortMaps(orderAdjItemsList, UtilMisc.toList("sequenceNum"));
context.put("orderAdjItemsList",orderAdjItemsList);

totalValuesMap =[:];
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);
finalMap = [:];
		unitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: parameters.unitId]);
		if(UtilValidate.isNotEmpty(unitTotals)){
			facilityTotals = unitTotals.get(parameters.unitId);
			if(facilityTotals != null){
				Iterator annualPeriodIter = facilityTotals.entrySet().iterator();
				while(annualPeriodIter.hasNext()){
					Map.Entry entry = annualPeriodIter.next();
					if(!"TOT".equals(entry.getKey())){
						Map annualValuesMap = (Map)entry.getValue();
						if(UtilValidate.isNotEmpty(annualValuesMap)){
						Iterator monthMapIter = annualValuesMap.entrySet().iterator();
							while(monthMapIter.hasNext()){
								Map.Entry monthEntry = monthMapIter.next();
								if(!"TOT".equals(monthEntry.getKey())){
									Map monthValuesMap = (Map)monthEntry.getValue();
									String monthName= monthEntry.getKey().substring(0,3)+","+entry.getKey();
									finalMap.put(monthName, monthValuesMap.get("TOT"));
									}
								}
							}
						}
					if("TOT".equals(entry.getKey())){
						totalValuesMap = (Map)entry.getValue();
					}
				}
			}
		}	

yearStartDate = UtilDateTime.getYearStart(UtilDateTime.nowTimestamp());
yearEndDate = UtilDateTime.getYearEnd(UtilDateTime.nowTimestamp(),timeZone, locale);
		
tempMonthStart = yearStartDate;
Map monthSeqMap = FastMap.newInstance();
while(tempMonthStart<yearEndDate){
	monthSeqMap.put(((String)UtilDateTime.toDateString(tempMonthStart,"MMMMM")).substring(0,3),UtilDateTime.toDateString(tempMonthStart, "MM"));
	tempMonthStart = UtilDateTime.addDaysToTimestamp(UtilDateTime.getMonthEnd(tempMonthStart,timeZone, locale),1);
	}


List tempList = FastList.newInstance();
Map tempFinalMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(finalMap)){
	for(key in finalMap.keySet()){
		Map tempMap = FastMap.newInstance();
		tempMap.putAt("monthKey", key);
		String monthKey = key;
		String yearKey = monthKey.substring(4);
		monthKey = monthKey.substring(0,3);
		tempMap.put("seqNum", yearKey.concat(monthSeqMap.get(monthKey)));
		tempList.add(tempMap);
		}
	tempList = UtilMisc.sortMaps(tempList, UtilMisc.toList("seqNum"));
	for(temp in tempList ){
		String key = temp.get("monthKey");
		tempFinalMap.put(key,finalMap.get(key));
		}
}
context.put("finalMap", tempFinalMap);

context.put("totalValuesMap",totalValuesMap);

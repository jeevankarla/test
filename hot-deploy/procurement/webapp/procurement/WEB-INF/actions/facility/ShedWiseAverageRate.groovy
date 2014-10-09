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
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;



dctx = dispatcher.getDispatchContext();

if(UtilValidate.isEmpty(parameters.shedId)){
	Debug.logError("Shed Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
String shedId = parameters.get("shedId");
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
def sdf1 = new SimpleDateFormat("yyyy-MM-dd");
try {
	if (parameters.fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		Debug.logError("fromDate Cannot Be Empty","");
		context.errorMessage = " From Date Can not be empty.......!";
		return;
	}
	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		Debug.logError("thruDate Cannot Be Empty","");
		context.errorMessage = " Thru Date Can not be empty.......!";
		return;
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);

facility = delegator.findOne("Facility",[facilityId:parameters.shedId],false);
context.putAt("facility", facility);

Map shedTotals = FastMap.newInstance();

Map initMap = FastMap.newInstance();
initMap.put("grossAmt",BigDecimal.ZERO);
initMap.put("tipAmt",BigDecimal.ZERO);
initMap.put("qtyKgs",BigDecimal.ZERO);

int intervalDays = UtilDateTime.getIntervalInDays(fromDate, thruDate);
intervalDays +=1;



Map finalPeriodWiseMap = FastMap.newInstance();
Map finalMonthWiseMap = FastMap.newInstance();
Map finalYearWiseMap = FastMap.newInstance();

List periodSortList = FastList.newInstance();
List monthSortList = FastList.newInstance();

shedTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: shedId]);
if(ServiceUtil.isError(shedTotals)){
	Debug.logError("NO DATA FORUND","");
	context.errorMessage = "NO DATA FOUND.......!";
	return;
	}
if(UtilValidate.isNotEmpty(shedTotals)){
	Map shedWiseTotals = FastMap.newInstance(); 
	shedWiseTotals.putAll(shedTotals.get(shedId));
	if(UtilValidate.isNotEmpty(shedWiseTotals)){
		Map totalsMap = FastMap.newInstance();
		totalsMap.putAll(initMap);
		for(String yearKey in shedWiseTotals.keySet()){
			if(!"TOT".equalsIgnoreCase(yearKey)){
				Map monthWiseTotals = FastMap.newInstance();
				monthWiseTotals.putAll(shedWiseTotals.get(yearKey));
				for(String monthKey in monthWiseTotals.keySet()){
					if(!monthKey.equalsIgnoreCase("TOT")){
						Map monthSortMap = FastMap.newInstance();
						String sortMonthKey = '';
						Map periodWiseDetails = FastMap.newInstance();
						//Getting each PeriodWise Details 
						periodWiseDetails.putAll(monthWiseTotals.get(monthKey));
						for(String periodKey in periodWiseDetails.keySet()){
							if(!periodKey.equalsIgnoreCase("TOT")){
								Map periodSortMap = FastMap.newInstance();
								String sortPeriodKey = '';
								periodBillingDetails = delegator.findOne("PeriodBilling", [periodBillingId : periodKey], false);
								if(UtilValidate.isNotEmpty(periodBillingDetails)){
									Map periodDetailsMap = FastMap.newInstance();
									periodDetailsMap.putAll(periodWiseDetails.get(periodKey).get("TOT"));
									customPeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : periodBillingDetails.customTimePeriodId], false);
									String periodWiseKey = '';
									String monthWiseKey = monthKey.concat("-"+yearKey);
									
									String fromMonth = UtilDateTime.toDateString( UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse((customPeriodDetails.fromDate).toString()).getTime())),"MMMdd");
									String thruMonth = UtilDateTime.toDateString( UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse((customPeriodDetails.thruDate).toString()).getTime())),"MMMdd,yyyy");
									
									sortMonthKey = UtilDateTime.toDateString( UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse((customPeriodDetails.fromDate).toString()).getTime())),"yyyyMM");
									sortPeriodKey = UtilDateTime.toDateString( UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse((customPeriodDetails.fromDate).toString()).getTime())),"yyyyMMdd");
									
									
									periodWiseKey = periodWiseKey.concat(fromMonth);
									periodWiseKey = periodWiseKey.concat("-");
									periodWiseKey = (periodWiseKey.concat(thruMonth)).toUpperCase();
									Map periodQtyMap = FastMap.newInstance();
									BigDecimal qtyKgs = periodDetailsMap.get("qtyKgs");
									BigDecimal grossAmt = periodDetailsMap.get("grossAmt");
									BigDecimal tipAmt = periodDetailsMap.get("tipAmt");
									BigDecimal avgRate = BigDecimal.ZERO;
									if(qtyKgs.compareTo(BigDecimal.ZERO)>0){
											avgRate = (grossAmt.add(tipAmt)).divide(qtyKgs,2,BigDecimal.ROUND_HALF_UP);
										}
									periodQtyMap.put("qtyKgs", qtyKgs);
									periodQtyMap.put("grossAmt", grossAmt);
									periodQtyMap.put("tipAmt", tipAmt);
									periodQtyMap.put("avgRate", avgRate);
									if(UtilValidate.isEmpty(finalPeriodWiseMap) ||(UtilValidate.isNotEmpty(finalPeriodWiseMap)&& UtilValidate.isEmpty(finalPeriodWiseMap.get(periodWiseKey)))){
										finalPeriodWiseMap.put(periodWiseKey, periodQtyMap);
									}else{
										Map tempPeriodMap = FastMap.newInstance();
										tempPeriodMap.putAll(finalPeriodWiseMap.get(periodWiseKey));
										tempPeriodMap.remove("avgRate");
										for(String tempKey in tempPeriodMap.keySet()){
											tempPeriodMap.put(tempKey, tempPeriodMap.get(tempKey)+periodQtyMap.get(tempKey));
										}
										if(tempPeriodMap.get("qtyKgs")!=0){
												BigDecimal tQtyKgs = tempPeriodMap.get("qtyKgs");
												BigDecimal tGrossAmt = tempPeriodMap.get("grossAmt");
												BigDecimal tTipAmt = tempPeriodMap.get("tipAmt");
												BigDecimal tAvgRate = BigDecimal.ZERO;
												if(tQtyKgs.compareTo(BigDecimal.ZERO)>0){
													tAvgRate = (tGrossAmt.add(tTipAmt)).divide(tQtyKgs,2,BigDecimal.ROUND_HALF_UP);
												}												
												tempPeriodMap.put("avgRate", tAvgRate);
											}
										
										finalPeriodWiseMap.put(periodWiseKey, tempPeriodMap);
									}
									// populating monthWise
									if(UtilValidate.isEmpty(finalMonthWiseMap) ||(UtilValidate.isNotEmpty(finalMonthWiseMap)&& UtilValidate.isEmpty(finalMonthWiseMap.get(monthWiseKey)))){
										finalMonthWiseMap.put(monthWiseKey, periodQtyMap);
									}else{
										Map tempPeriodMap = FastMap.newInstance();
										tempPeriodMap.putAll(finalMonthWiseMap.get(monthWiseKey));
										tempPeriodMap.remove("avgRate");
										for(String tempKey in tempPeriodMap.keySet()){
											tempPeriodMap.put(tempKey, tempPeriodMap.get(tempKey)+periodQtyMap.get(tempKey));
										}
										if(tempPeriodMap.get("qtyKgs")!=0){
												BigDecimal tQtyKgs = tempPeriodMap.get("qtyKgs");
												BigDecimal tGrossAmt = tempPeriodMap.get("grossAmt");
												BigDecimal tTipAmt = tempPeriodMap.get("tipAmt");
												BigDecimal tAvgRate = BigDecimal.ZERO;
												if(tQtyKgs.compareTo(BigDecimal.ZERO)>0){
													tAvgRate = (tGrossAmt.add(tTipAmt)).divide(tQtyKgs,2,BigDecimal.ROUND_HALF_UP);
												}
												tempPeriodMap.put("avgRate", tAvgRate);
											}
										
										finalMonthWiseMap.put(monthWiseKey, tempPeriodMap);
									}
									
									
									// populating yearWise
									if(UtilValidate.isEmpty(finalYearWiseMap) ||(UtilValidate.isNotEmpty(finalYearWiseMap)&& UtilValidate.isEmpty(finalYearWiseMap.get(yearKey)))){
										finalYearWiseMap.put(yearKey, periodQtyMap);
									}else{
										Map tempPeriodMap = FastMap.newInstance();
										tempPeriodMap.putAll(finalYearWiseMap.get(yearKey));
										tempPeriodMap.remove("avgRate");
										for(String tempKey in tempPeriodMap.keySet()){
											tempPeriodMap.put(tempKey, tempPeriodMap.get(tempKey)+periodQtyMap.get(tempKey));
										}
										if(tempPeriodMap.get("qtyKgs")!=0){
												BigDecimal tQtyKgs = tempPeriodMap.get("qtyKgs");
												BigDecimal tGrossAmt = tempPeriodMap.get("grossAmt");
												BigDecimal tTipAmt = tempPeriodMap.get("tipAmt");
												BigDecimal tAvgRate = BigDecimal.ZERO;
												if(tQtyKgs.compareTo(BigDecimal.ZERO)>0){
													tAvgRate = (tGrossAmt.add(tTipAmt)).divide(tQtyKgs,2,BigDecimal.ROUND_HALF_UP);
												}
												tempPeriodMap.put("avgRate", tAvgRate);
											}
										
										finalYearWiseMap.put(yearKey, tempPeriodMap);
									}
									for(String qtyKey in totalsMap.keySet()){
											totalsMap.put(qtyKey, (periodQtyMap.get(qtyKey)).add(totalsMap.get(qtyKey)));
										}
									
									if(UtilValidate.isNotEmpty(sortPeriodKey)){
											periodSortMap.put("sortKey", sortPeriodKey);
											periodSortMap.put("key", periodWiseKey);
											if(!periodSortList.contains(periodSortMap)){
												periodSortList.add(periodSortMap);
												}
											
											
										}
									if(UtilValidate.isNotEmpty(sortMonthKey)){
										monthSortMap.put("sortKey", sortMonthKey);
										monthSortMap.put("key", monthWiseKey);
										if(! monthSortList.contains(monthSortMap)){
											monthSortList.add(monthSortMap);
										}
									}
									
								}
							}
						}
							
					}
				}
				totalsMap.put("avgRate",BigDecimal.ZERO);
				//populateTotalsMap 
				if(totalsMap.get("qtyKgs")!=0){
						
					BigDecimal qtyKgs = totalsMap.get("qtyKgs");
					BigDecimal grossAmt = totalsMap.get("grossAmt");
					BigDecimal tipAmt = totalsMap.get("tipAmt");
					BigDecimal avgRate = BigDecimal.ZERO;
					if(qtyKgs.compareTo(BigDecimal.ZERO)>0){
							avgRate = (grossAmt.add(tipAmt)).divide(qtyKgs,2,BigDecimal.ROUND_HALF_UP);
						}
					totalsMap.put("avgRate",avgRate);
					
					}
				finalPeriodWiseMap.put("TOT", totalsMap);
				finalMonthWiseMap.put("TOT", totalsMap);
				finalYearWiseMap.remove("TOT");
				finalYearWiseMap.put("TOT", totalsMap);
			}
		}
	}else{
	Debug.logError("NO DATA FORUND","");
	context.errorMessage = "No DATA FOUND.......!";
	return;
	}
} 

periodSortList = UtilMisc.sortMaps(periodSortList, UtilMisc.toList("sortKey"));
monthSortList = UtilMisc.sortMaps(monthSortList, UtilMisc.toList("sortKey"));



Map tempSortedPeriodsMap = FastMap.newInstance();
Map tempSortedMonthsMap = FastMap.newInstance();
for(periodSortedMap in  periodSortList){
	String periodKey = periodSortedMap.get("key");
	tempSortedPeriodsMap.put(periodKey, finalPeriodWiseMap.get(periodKey));
	}
tempSortedPeriodsMap.put("TOT", finalPeriodWiseMap.get("TOT"));


for(monthSortedMap in  monthSortList){
	String monthKey = monthSortedMap.get("key");
	tempSortedMonthsMap.put(monthKey, finalMonthWiseMap.get(monthKey));
	}
tempSortedMonthsMap.put("TOT", finalPeriodWiseMap.get("TOT"));



context.put("finalPeriodWiseMap",tempSortedPeriodsMap);
context.put("rTypeFlag","Period Wise");
if(UtilValidate.isNotEmpty(parameters.getAt("rTypeFlag"))){
	context.put("rTypeFlag",parameters.getAt("rTypeFlag"));
	if(parameters.getAt("rTypeFlag") == "Month Wise"){
		context.put("finalPeriodWiseMap",tempSortedMonthsMap);
		}
	
	if(parameters.getAt("rTypeFlag") == "Year Wise"){
		context.put("finalPeriodWiseMap",finalYearWiseMap);
		}
	
	}




 

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
def sdf1 = new SimpleDateFormat("yyyy/MM/dd");
try {
	   if (fromDate){
			   fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
					   }
	   if (thruDate){
			   thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
					   }
	} catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + e, "");
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
								}
context.put("fromDate", fromDateStart);
context.put("thruDate", thruDateEnd);

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
totalDays=UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd);
if(totalDays>366){
	Debug.logError("You can not choose more than 366 days.","");
	context.errorMessage="You can not choose more than 366 days.";
	return;
}

List monthList=FastList.newInstance();
tempCurrentDate=UtilDateTime.getMonthStart(fromDateStart);
while(tempCurrentDate<=(UtilDateTime.getMonthEnd(thruDateEnd,timeZone,locale))){
	date=UtilDateTime.toDateString(tempCurrentDate,"MMM/yyyy");
	monthList.add(date);
	tempCurrentDate=UtilDateTime.getMonthEnd(tempCurrentDate,timeZone,locale);
	tempCurrentDate=UtilDateTime.addDaysToTimestamp(tempCurrentDate,1);
}
context.put("monthList",monthList);
	
List prevMonthList=FastList.newInstance();
tempPreviousDate=UtilDateTime.getMonthStart(prevDateStart);
while(tempPreviousDate<=(UtilDateTime.getMonthEnd(prevDateEnd,timeZone,locale))){
	prevDate=UtilDateTime.toDateString(tempPreviousDate,"MMM/yyyy");
	prevMonthList.add(prevDate);
	tempPreviousDate=UtilDateTime.getMonthEnd(tempPreviousDate,timeZone,locale);
	tempPreviousDate=UtilDateTime.addDaysToTimestamp(tempPreviousDate,1);
}
context.put("prevMonthList",prevMonthList);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

finalMap = [:];
String monthName;
grandTotalMap=[:];
unitTotalFinalMap=[:];
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx,[userLogin: userLogin,shedId: parameters.shedId]);
if(UtilValidate.isNotEmpty(shedUnitDetails)){
	unitLists = shedUnitDetails.get("unitsList");
	unitLists.each{ unitId->
		tempFinalMap = [:];
		unitTempFinalMap=[:];
		unitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: unitId]);
		if(UtilValidate.isNotEmpty(unitTotals)){
			facilityTotals = unitTotals.get(unitId);
			if(UtilValidate.isNotEmpty(facilityTotals)){
				Iterator unitMapIter = facilityTotals.entrySet().iterator();
				while(unitMapIter.hasNext()){
					Map.Entry entry = unitMapIter.next();
					if(!"TOT".equals(entry.getKey())){
						 Map annualValuesMap = (Map)entry.getValue();
						if(UtilValidate.isNotEmpty(annualValuesMap)){
							Iterator monthMapIter = annualValuesMap.entrySet().iterator();
							while(monthMapIter.hasNext()){
								Map.Entry monthEntry = monthMapIter.next();
								if(!"TOT".equals(monthEntry.getKey())){
									 monthName=monthEntry.getKey().substring(0,3)+"/"+entry.getKey();
									monthValue=(monthEntry.getValue().get("TOT").get("TOT").get("qtyLtrs"));
									monthValue=(monthValue/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN);
									if(UtilValidate.isEmpty(tempFinalMap.get(monthName))){
										tempFinalMap.put(monthName,monthValue);
									}else{
										tempFinalMap.put(monthName,monthValue+tempFinalMap.get(monthName));
									}
									if(UtilValidate.isEmpty(grandTotalMap[monthName])){
										monthValue=(monthEntry.getValue().get("TOT").get("TOT").get("qtyLtrs"));
										monthValue=(monthValue/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN);
										grandTotalMap.put(monthName,monthValue);
									}	
									else{
										 grandTotalMap.put(monthName,monthValue+grandTotalMap.get(monthName));
										
										}
								}
							}
						}
						
					}
					else{
						Map unitTempFinalMap=(Map)entry.getValue();
						currYearValue=(entry.getValue().get("TOT").get("qtyLtrs"));
						currYearValue=(currYearValue/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN);
						unitTotalFinalMap.put(unitId, currYearValue);
						
						}
				}
				
				finalMap.put(unitId, tempFinalMap);
			}
		}
	}
}
context.put("finalMap",finalMap);
context.put("grandTotalMap", grandTotalMap);
context.put("unitTotalFinalMap", unitTotalFinalMap);

prevFinalMap = [:];
prevGrFinalMap=[:];
prevGrValue=0;
String prevMonthName;
if(UtilValidate.isNotEmpty(shedUnitDetails)){
	unitLists=shedUnitDetails.get("unitsList");
	unitLists.each{unitId->
		prevTempFinalMap=[:];
		unitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: prevDateStart , thruDate: prevDateEnd,userLogin: userLogin,facilityId: unitId]);
		if(UtilValidate.isNotEmpty(unitTotals)){
			facilityTotals=unitTotals.get(unitId);
			if(UtilValidate.isNotEmpty(facilityTotals)){
				Iterator unitMapIter=facilityTotals.entrySet().iterator();
				while(unitMapIter.hasNext()){
					Map.Entry prevEntry=unitMapIter.next();
					if("TOT".equals(prevEntry.getKey())){
						Map prevTempFinalMap=(Map)prevEntry.getValue();
						prevYearValue=(prevEntry.getValue().get("TOT").get("qtyLtrs"));	
						prevYearValue=(prevYearValue/100000).setScale(2,BigDecimal.ROUND_HALF_EVEN);
						prevGrValue=prevGrValue+prevYearValue;
						prevFinalMap.put(unitId, prevYearValue);
						prevGrFinalMap.put("preGrValue", prevGrValue);
						
					}			
				}
			}
		}
	}
}
context.put("prevFinalMap",prevFinalMap);
context.put("prevGrFinalMap", prevGrFinalMap);

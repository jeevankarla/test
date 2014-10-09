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
context.putAt("fromDate",fromDate);
context.putAt("thruDate", thruDate);

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

List MonthKeyList = FastList.newInstance();
List leanList = FastList.newInstance();
List flushList = FastList.newInstance();

List leanKeysList = FastList.newInstance();
List flushKeysList = FastList.newInstance();

Timestamp tempFromDate = fromDateStart;
while(tempFromDate<= thruDateEnd){
	monthKey = UtilDateTime.toDateString(tempFromDate,"MM/yyyy");
	int monthnum = Integer.parseInt(UtilDateTime.toDateString(tempFromDate,"MM"));
	
	if(monthnum>3 && monthnum<10){
		leanList.add(monthKey);
	}else{
		flushList.add(monthKey);
	}
	tempFromDate =UtilDateTime.addDaysToTimestamp(UtilDateTime.getMonthEnd(tempFromDate,timeZone, locale), 1);
		
	MonthKeyList.add(monthKey);
}
context.putAt("MonthKeyList", MonthKeyList);
context.putAt("leanList", leanList);
context.putAt("flushList", flushList);

facilityId = parameters.shedId ;

if(UtilValidate.isEmpty(facilityId)){
	Debug.logError("No Shed Has Been Selected......","");
	context.errorMessage = "No Shed Has Been Selected......!";
	return;
	}

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

totalDays=UtilDateTime.getIntervalInDays(fromDateStart,thruDateEnd);
if(totalDays > 366){
	Debug.logError("You Cannot Choose More Than 366 Days.","");
	context.errorMessage = "You Cannot Choose More Than 366 Days";
	return;
}

Map monthMap = FastMap.newInstance();

Timestamp currentYearStart =UtilDateTime.getYearStart(UtilDateTime.nowTimestamp());
Timestamp currentYearEnd = UtilDateTime.getYearEnd(UtilDateTime.nowTimestamp(),timeZone, locale);

Timestamp tempYearStart = currentYearStart;
while(tempYearStart <= currentYearEnd){
		monthName = UtilDateTime.toDateString(tempYearStart,"MMMM");
		monthMap.put(monthName,UtilDateTime.toDateString(tempYearStart,"MM"));
		tempYearStart = UtilDateTime.addDaysToTimestamp(UtilDateTime.getMonthEnd(tempYearStart,timeZone, locale), 1);
	}
context.putAt("monthMap", monthMap);

tempfinalMap = [:];
totalMap = [:];
finalMap=[:];
totMonthValue = 0;
	annualPeriodTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: facilityId ]);
	totMonthValue = 0;
	if(UtilValidate.isNotEmpty(annualPeriodTotals)){
		facilityTotals=annualPeriodTotals.get(facilityId);
		if(UtilValidate.isNotEmpty(facilityTotals)){
			Iterator annualPeriodIter=facilityTotals.entrySet().iterator();
			while(annualPeriodIter.hasNext()){
				Map.Entry entry=annualPeriodIter.next();
				if(!"TOT".equals(entry.getKey())){
					Map annualValuesMap=(Map) entry.getValue();
					if(UtilValidate.isNotEmpty(annualValuesMap)){
						Iterator monthMapIter=annualValuesMap.entrySet().iterator();
						while(monthMapIter.hasNext()){
							Map.Entry monthEntry=monthMapIter.next();
							if(!"TOT".equals(monthEntry.getKey())){
								Map monthValuesMap=(Map) monthEntry.getValue();
								mnthValue=monthValuesMap.get("TOT");
								Map totalDetailsMap = FastMap.newInstance();
									for(prodKey in mnthValue.keySet() ){
										if(prodKey != "_NA_"){
											Map tempProdWiseMap = mnthValue.get(prodKey);
											totQtyLtrs = tempProdWiseMap.get("qtyLtrs");
											tip = tempProdWiseMap.get("tipAmt");
											price = tempProdWiseMap.get("price");
											amount = tip + price;
											totAmount = amount;
											
											roundedQtyLtrs = (tempProdWiseMap.get("qtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
											roundedAmount = (amount/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
											if(totQtyLtrs != 0){
												avgRate = (totAmount/totQtyLtrs).setScale(2,BigDecimal.ROUND_HALF_UP);
											}
											Map DetailsMap = FastMap.newInstance();
											DetailsMap.put("qtyLtrs", roundedQtyLtrs);
											DetailsMap.put("amount", roundedAmount);
											DetailsMap.put("avgRate", avgRate);
											DetailsMap.put("totQtyLtrs", totQtyLtrs);
											DetailsMap.put("totAmount", totAmount);
											totalDetailsMap.put(prodKey, DetailsMap);
										}
										monthNumber = monthMap.get(monthEntry.getKey())+"/"+entry.getKey();
										if(leanList.contains(monthNumber)){
										    if(!leanKeysList.contains(monthMap.get(monthEntry.getKey())+"/"+entry.getKey())){
												leanKeysList.add(monthMap.get(monthEntry.getKey())+"/"+entry.getKey());
											}
										}else{
											if(!flushKeysList.contains(monthMap.get(monthEntry.getKey())+"/"+entry.getKey())){
												flushKeysList.add(monthMap.get(monthEntry.getKey())+"/"+entry.getKey());
											}
										}
										tempfinalMap.put(monthMap.get(monthEntry.getKey())+"/"+entry.getKey(),totalDetailsMap);
									}	
							}
						}
					}
				}
				if("TOT".equals(entry.getKey())){
					totalMap = (Map)entry.getValue();
				}
			}
		}
	}
	
	context.putAt("tempfinalMap", tempfinalMap);
	context.putAt("totalMap", totalMap);
	context.putAt("leanKeysList", leanKeysList);
	context.putAt("flushKeysList", flushKeysList);
	
	
	// populating lean totals
	leanTotalValue = 0;
	Map initMap = FastMap.newInstance();
	initMap.put("qtyLtrs", 0);
	initMap.put("amount", 0);
	initMap.put("avgRate", 0);
	initMap.put("totQtyLtrs", 0);
	initMap.put("totAmount", 0);
	
	Map tempFinalLeanMap = FastMap.newInstance();
	Map tempFinalFlushMap = FastMap.newInstance();
	
	Map leanTotalsMap = FastMap.newInstance();
	
	if(UtilValidate.isNotEmpty(tempfinalMap)){
		for(leanKey in leanKeysList){
			Map leanMonthTotal = tempfinalMap.get(leanKey);
			Map tempTotalsMap = FastMap.newInstance();
			tempTotalsMap.putAll(initMap);
			
			Map productTotMap = FastMap.newInstance();
			for(prodKey in leanMonthTotal.keySet() ){
				Map tempProdMap = leanMonthTotal.get(prodKey);
				
				productTotMap.putAll(initMap);
					if(prodKey !="_NA_"){
						Map tempLeanQtyMap = FastMap.newInstance();
						if(UtilValidate.isEmpty(leanTotalsMap.get(prodKey))){
							leanTotalsMap.put(prodKey, initMap);
						}
						tempLeanQtyMap.putAll(leanTotalsMap.get(prodKey));
						
						BigDecimal qtyLtrs = tempProdMap.get("qtyLtrs");
						BigDecimal amount = tempProdMap.get("amount");
						BigDecimal avgRate = tempProdMap.get("avgRate");
						BigDecimal totQtyLtrs = tempProdMap.get("totQtyLtrs");
						BigDecimal totAmount = tempProdMap.get("totAmount");
						
						tempLeanQtyMap.put("qtyLtrs", tempLeanQtyMap.get("qtyLtrs")+qtyLtrs);
						tempLeanQtyMap.put("amount", tempLeanQtyMap.get("amount")+amount);
						tempLeanQtyMap.put("avgRate", tempLeanQtyMap.get("avgRate")+avgRate);
						tempLeanQtyMap.put("totQtyLtrs", tempLeanQtyMap.get("totQtyLtrs")+totQtyLtrs);
						tempLeanQtyMap.put("totAmount", tempLeanQtyMap.get("totAmount")+totAmount);
						
						tempTotalsMap.put("qtyLtrs", tempTotalsMap.get("qtyLtrs")+qtyLtrs);
						tempTotalsMap.put("amount", tempTotalsMap.get("amount")+amount);
						tempTotalsMap.put("avgRate", tempTotalsMap.get("avgRate")+avgRate);
						tempTotalsMap.put("totQtyLtrs", tempTotalsMap.get("totQtyLtrs")+totQtyLtrs);
						tempTotalsMap.put("totAmount", tempTotalsMap.get("totAmount")+totAmount);
						
						if(qtyLtrs != 0){
							productTotMap.put("qtyLtrs", qtyLtrs);
							productTotMap.put("amount",amount);
							productTotMap.put("avgRate",avgRate);
							productTotMap.put("totQtyLtrs", totQtyLtrs);
							productTotMap.put("totAmount", totAmount);
							
							leanTotalsMap.put(prodKey, tempLeanQtyMap);
						}
						
					}
					
					tempFinalLeanMap.put("LEAN TOT",leanTotalsMap);
			}
		}
	}
	
	Map tempLeanMap = FastMap.newInstance();
	for(key in leanTotalsMap.keySet()){
		if(UtilValidate.isNotEmpty(leanTotalsMap.get(key))){
			
			Map tempMap = FastMap.newInstance();
			tempMap.putAll(leanTotalsMap.get(key));
			tempMap.remove("qtyLtrs");
			tempMap.remove("amount");
			tempMap.remove("avgRate");
			BigDecimal totQtyLtrs = leanTotalsMap.get(key).get("totQtyLtrs");
			
			BigDecimal totAmt=leanTotalsMap.get(key).get("totAmount")
			
			roundedtotQtyLtrs = (leanTotalsMap.get(key).get("totQtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			roundedtotAmount = (leanTotalsMap.get(key).get("totAmount")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			BigDecimal roundedAvgRate = 0;
			if(roundedtotQtyLtrs != 0){
				roundedAvgRate = (totAmt/totQtyLtrs).setScale(2,BigDecimal.ROUND_HALF_UP);
			}
			tempMap.put("qtyLtrs", roundedtotQtyLtrs);
			tempMap.put("amount", roundedtotAmount);
			tempMap.put("avgRate", roundedAvgRate);
			tempLeanMap.put(key,tempMap);
		}
	}
	tempFinalLeanMap.clear();
	
	tempFinalLeanMap.put("LEAN TOT",tempLeanMap);
	
	
	// populating flush totals
	Map flushTotalsMap = FastMap.newInstance();
	if(UtilValidate.isNotEmpty(tempfinalMap)){
		for(flushKey in flushKeysList){
			Map flushMonthTotal = tempfinalMap.get(flushKey);
			Map tempTotalsMap = FastMap.newInstance();
			tempTotalsMap.putAll(initMap);
			
			Map productTotMap = FastMap.newInstance();
			for(prodKey in flushMonthTotal.keySet() ){
				Map tempProdMap = flushMonthTotal.get(prodKey);
				productTotMap.putAll(initMap);
				if(prodKey !="_NA_"){
					Map tempFlushQtyMap = FastMap.newInstance();
					if(UtilValidate.isEmpty(flushTotalsMap.get(prodKey))){
						flushTotalsMap.put(prodKey, initMap);
					}	
						tempFlushQtyMap.putAll(flushTotalsMap.get(prodKey));
						
						BigDecimal qtyLtrs = tempProdMap.get("qtyLtrs");
						BigDecimal amount = tempProdMap.get("amount");
						BigDecimal avgRate = tempProdMap.get("avgRate");
						BigDecimal totQtyLtrs = tempProdMap.get("totQtyLtrs");
						BigDecimal totAmount = tempProdMap.get("totAmount");
						
						tempFlushQtyMap.put("qtyLtrs", tempFlushQtyMap.get("qtyLtrs")+qtyLtrs);
						tempFlushQtyMap.put("amount", tempFlushQtyMap.get("amount")+amount);
						tempFlushQtyMap.put("avgRate", tempFlushQtyMap.get("avgRate")+avgRate);
						tempFlushQtyMap.put("totQtyLtrs", tempFlushQtyMap.get("totQtyLtrs")+totQtyLtrs);
						tempFlushQtyMap.put("totAmount", tempFlushQtyMap.get("totAmount")+totAmount);
						
						tempTotalsMap.put("qtyLtrs", tempTotalsMap.get("qtyLtrs")+qtyLtrs);
						tempTotalsMap.put("amount", tempTotalsMap.get("amount")+amount);
						tempTotalsMap.put("avgRate", tempTotalsMap.get("avgRate")+avgRate);
						tempTotalsMap.put("totQtyLtrs", tempTotalsMap.get("totQtyLtrs")+totQtyLtrs);
						tempTotalsMap.put("totAmount", tempTotalsMap.get("totAmount")+totAmount);
						
						if(qtyLtrs != 0){
							productTotMap.put("qtyLtrs", qtyLtrs);
							productTotMap.put("amount",amount);
							productTotMap.put("avgRate",avgRate);
							productTotMap.put("totQtyLtrs",totQtyLtrs);
							productTotMap.put("totAmount", totAmount);
							
							flushTotalsMap.put(prodKey, tempFlushQtyMap);
						}
				}	
				tempFinalFlushMap.put("FLUSH TOT",flushTotalsMap);
			}	
		}
	}	
	
	Map tempFlushMap = FastMap.newInstance();
	for(key in flushTotalsMap.keySet()){
		if(UtilValidate.isNotEmpty(flushTotalsMap.get(key))){
			
			Map tempMap = FastMap.newInstance();
			tempMap.putAll(flushTotalsMap.get(key));
			tempMap.remove("qtyLtrs");
			tempMap.remove("amount");
			tempMap.remove("avgRate");
			
			BigDecimal totQtyLtrs = flushTotalsMap.get(key).get("totQtyLtrs");
			BigDecimal totAmt=flushTotalsMap.get(key).get("totAmount")
			
			roundedtotQtyLtrs = (flushTotalsMap.get(key).get("totQtyLtrs")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			roundedtotAmount = (flushTotalsMap.get(key).get("totAmount")/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
			BigDecimal roundedAvgRate = 0;
			if(roundedtotQtyLtrs != 0){
				roundedAvgRate = (totAmt/totQtyLtrs).setScale(2,BigDecimal.ROUND_HALF_UP);
			}
			tempMap.put("qtyLtrs", roundedtotQtyLtrs);
			tempMap.put("amount", roundedtotAmount);
			tempMap.put("avgRate", roundedAvgRate);
			
			tempFlushMap.put(key,tempMap);
		}
	}
	tempFinalFlushMap.clear();
	
	tempFinalFlushMap.put("FLUSH TOT",tempFlushMap);

context.putAt("tempFinalLeanMap", tempFinalLeanMap);
context.putAt("tempFinalFlushMap", tempFinalFlushMap);

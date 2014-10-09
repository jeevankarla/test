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

String dateStr = fromDate+"-04-01";
String thruDateStr = thruDate+"-03-31";

SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
try {
	if (fromDate) {
		fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(fmt.parse(dateStr).getTime()));
	}
	if (thruDate) {
		thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(fmt.parse(thruDateStr).getTime()));
	}
}catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("fromDate", fromDateStart);
context.putAt("thruDate", thruDateEnd);

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
	
shedTotValuesMap = [:];
shedTotalList = [];
yearKeyList = [];
Timestamp tempFromDate = fromDateStart;
while(tempFromDate < thruDateEnd){
	Timestamp currentYearMarchEnd = UtilDateTime.addDaysToTimestamp(tempFromDate,-1);
	year = UtilDateTime.getYear(currentYearMarchEnd, timeZone, locale);
	String marchEndString = UtilDateTime.toDateString(currentYearMarchEnd,"yyyy-MM-dd");
	nextYear = year+1;
	String nextYearMarchEndStr = nextYear+marchEndString.substring( (marchEndString.indexOf("-")));
	
	String yearKey = year+"-"+nextYear;
	
	yearKeyList.add(yearKey);
	
	tempThruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(fmt.parse(nextYearMarchEndStr).getTime()));
	
	mpfMilkReceiptsMap =[:];
	shedTotalsMap = [:];
	mpfMilkReceiptsMap = MilkReceiptReports.getAllMilkReceipts(dctx , [fromDate: tempFromDate , thruDate: tempThruDate]);
	if(UtilValidate.isNotEmpty(mpfMilkReceiptsMap)){
		Map milkReceipts = mpfMilkReceiptsMap.get("milkReceiptsMap");
		if(UtilValidate.isNotEmpty(milkReceipts)){
			Iterator mccIter = milkReceipts.entrySet().iterator();
			while(mccIter.hasNext()){
				Map.Entry mccEntry = mccIter.next();
				if(!"dayTotals".equals(mccEntry.getKey())){
					shedValue = mccEntry.getValue();
					if(UtilValidate.isNotEmpty(shedValue)){
						Iterator shedIter = shedValue.entrySet().iterator();
						while(shedIter.hasNext()){
							Map.Entry shedEntry = shedIter.next();
							if(!"dayTotals".equals(shedEntry.getKey())){
								shedTotValue = shedEntry.getValue();
								if(UtilValidate.isNotEmpty(shedTotValue)){
									Iterator totIter = shedTotValue.entrySet().iterator();
									while(totIter.hasNext()){
										Map.Entry totEntry = totIter.next();
										if("dayTotals".equals(totEntry.getKey())){
											shedFinalTotValue = totEntry.getValue();
											if(UtilValidate.isNotEmpty(shedFinalTotValue)){
												totRecQty = shedFinalTotValue.getAt("TOT").getAt("TOT").getAt("recdQtyLtrs");
												roundedtotRecQty = (totRecQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
												shedTotalsMap.put(shedEntry.getKey(), roundedtotRecQty);
											}	
										}	
									}
								}
							}else{
								totalValue = shedEntry.getValue();
								if(UtilValidate.isNotEmpty(totalValue)){
									totalRecQty = totalValue.getAt("TOT").getAt("TOT").getAt("recdQtyLtrs");
									roundedtotalRecQty = (totalRecQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
									shedTotalsMap.put(mccEntry.getKey(), roundedtotalRecQty);
								}	
							}
						}
					}
				}else{
					mccValue = mccEntry.getValue();
					if(UtilValidate.isNotEmpty(mccValue)){
						grandTotRecQty = mccValue.getAt("TOT").getAt("TOT").getAt("recdQtyLtrs");
						roundedTotQty = (grandTotRecQty/100000).setScale(2,BigDecimal.ROUND_HALF_UP);
						shedTotalsMap.put(mccEntry.getKey(), roundedTotQty);
					}	
				}
			}
		}
	}
	tempFromDate = UtilDateTime.addDaysToTimestamp(UtilDateTime.getDayStart(new java.sql.Timestamp(fmt.parse(nextYearMarchEndStr).getTime())),1);
	shedTotValuesMap.put(yearKey, shedTotalsMap);
	shedTotalList.add(shedTotValuesMap);
}
context.putAt("yearKeyList", yearKeyList);
context.putAt("shedTotValuesMap", shedTotValuesMap);

	
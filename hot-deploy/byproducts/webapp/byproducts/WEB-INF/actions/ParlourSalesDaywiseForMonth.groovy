import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.sql.Date;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.price.PriceServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

facility = delegator.findOne("Facility", [facilityId : parameters.parlourId], false);
if(facility){
	context.parlourName = facility.get("facilityName");
	context.parlourCode = facility.get("facilityId");
}

customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
IntervalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
dayTotalsList = [];
for(int i=0;i<=IntervalDays;i++){
	Timestamp saleDate = UtilDateTime.addDaysToTimestamp(fromDateTime, +i);
	dayBegin = UtilDateTime.getDayStart(saleDate, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(saleDate , timeZone, locale);
	boothsList = parameters.parlourId;
	dayTotalsMap =[:];
	boothTotalsMap = ByProductReportServices.getBoothSaleAndPaymentTotals(dispatcher.getDispatchContext(), ["userLogin":userLogin ,facilityIds:UtilMisc.toList(boothsList),fromDate:dayBegin, thruDate:dayEnd], false);
	boothTotals = boothTotalsMap.get("boothTotalsMap");
	Iterator boothTotIter = boothTotals.entrySet().iterator();
	while (boothTotIter.hasNext()) {
		Map.Entry boothEntry = boothTotIter.next();
		booth = boothEntry.getKey();
		dayTotalsMap[saleDate] = boothEntry.getValue();
		dayTotalsList.add(dayTotalsMap);
	}
}
context.dayTotalsList = dayTotalsList;
//Debug.log("=======================dayTotalsList===========================>"+dayTotalsList);

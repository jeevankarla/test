import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;

subsidyFromDate=parameters.subsidyFromDate;
subsidyThruDate=parameters.subsidyThruDate;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
finalList = [];
def sdf = new SimpleDateFormat("yyyy, MMM dd");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(subsidyFromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(subsidyThruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
fromDateTime = UtilDateTime.getDayStart(fromDateTime);
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
int totalDays = UtilDateTime.getIntervalInDays(dayBegin, dayEnd);
int BgMonth;
int EndMonth;
if(totalDays > 31){
	BgMonth = UtilDateTime.getMonth(UtilDateTime.toTimestamp(dayBegin), timeZone, locale) + 1;
	EndMonth = UtilDateTime.getMonth(UtilDateTime.toTimestamp(dayEnd), timeZone, locale) + 1;

def sdf1 = new SimpleDateFormat("yyyy-MM-dd");
for(i=BgMonth; i<=EndMonth; i++){
	
	tempMap = [:];
	monthQty = 0;
	monthSubsidy=0;
	monthServiceCharg=0;
	monthTotal = 0;
	day = UtilDateTime.getDayOfMonth(UtilDateTime.toTimestamp(dayBegin), timeZone, locale);
	year = UtilDateTime.getYear(UtilDateTime.toTimestamp(dayBegin), timeZone, locale);
	String dayFormat=String.format("%02d", day);
	String monthFormat=String.format("%02d", i);
	String yearFormat=String.format("%04d", year);
	String Date=yearFormat+"-"+monthFormat+"-"+dayFormat;
	DateTime = new java.sql.Timestamp(sdf1.parse(Date).getTime());
	MothDayBegin = UtilDateTime.getMonthStart(DateTime, timeZone, locale);
	MothDayEnd = UtilDateTime.getMonthEnd(DateTime, timeZone, locale);
	orderCondList =[];
	actualorderIds = [];
	orderCondList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(MothDayBegin)))
	orderCondList.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(MothDayEnd )))
	condition=EntityCondition.makeCondition(orderCondList,EntityOperator.AND);
	orderHeader = delegator.find("OrderHeader",condition,null,UtilMisc.toSet("orderId"),null,null);
	orderIds=EntityUtil.getFieldListFromEntityListIterator(orderHeader, "orderId", true);
	if(UtilValidate.isNotEmpty(orderIds)){
		orderCondList.clear();
		orderCondList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds));
		orderCondList.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"SCHEME_CAT"));
		condition=EntityCondition.makeCondition(orderCondList,EntityOperator.AND);
		orderAttribute = delegator.find("OrderAttribute",condition,null,UtilMisc.toSet("orderId"),null,null);
		actualorderIds=EntityUtil.getFieldListFromEntityListIterator(orderAttribute, "orderId", true);
	}
	for(i=0; i<actualorderIds.size(); i++){
		orderId = actualorderIds.get(i);
		eachOrderSubsidy=0;
		eachOrderQty=0
		total =0
		orderCondList.clear();
		orderCondList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		orderCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		cond=EntityCondition.makeCondition(orderCondList,EntityOperator.AND);
		orderAdjustments = delegator.findList("OrderAdjustment", cond, null, null, null, false );
		for(j=0; j<orderAdjustments.size(); j++){
			eachPayment = orderAdjustments[j];
			subSidyAmt = eachPayment.get("amount");
			eachOrderSubsidy = eachOrderSubsidy+subSidyAmt;
			orderAdjustmentId = eachPayment.get("orderAdjustmentId");
			orderAdjustmentBilling = delegator.findList("OrderAdjustmentBilling",EntityCondition.makeCondition("orderAdjustmentId", EntityOperator.EQUALS , orderAdjustmentId)  , null, null, null, false );
			if(UtilValidate.isNotEmpty(orderAdjustmentBilling)){
				for(k=0; k<orderAdjustmentBilling.size(); k++){
					 quantity =0;
					 orderAdjustment = orderAdjustments[k];
					 if(UtilValidate.isNotEmpty(orderAdjustment) && (orderAdjustment.get("quantity"))){
						quantity = orderAdjustment.get("quantity");
					 }
					 eachOrderQty = eachOrderQty+quantity;
				}
			}
		}
		monthQty = monthQty+eachOrderQty;
		monthSubsidy = monthSubsidy+eachOrderSubsidy;
		serviceCharg= eachOrderSubsidy*0.05;
		monthServiceCharg = monthServiceCharg+serviceCharg;
		monthTotal= monthSubsidy+monthServiceCharg;
	}
	tempMap.put("MothDayBegin",UtilDateTime.toDateString(MothDayBegin,"dd/MM/yyyy"));
	tempMap.put("MothDayEnd",UtilDateTime.toDateString(MothDayEnd,"dd/MM/yyyy"));
	tempMap.put("monthQty",monthQty);
	tempMap.put("monthSubsidy",monthSubsidy);
	tempMap.put("monthServiceCharg",monthServiceCharg);
	tempMap.put("monthTotal",monthTotal);
	finalList.add(tempMap);
}
}
context.finalList = finalList;

import org.ofbiz.base.util.*;
import java.math.RoundingMode;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.*;
import java.math.BigDecimal;
import java.text.ParseException;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.network.LmsServices;
import org.ofbiz.entity.util.EntityFindOptions;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
rounding = RoundingMode.HALF_UP;
customTimePeriod=delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("customTimePeriodId", parameters.customTimePeriodId), null,null, null, false);
dayStartfromDate=UtilDateTime.toTimestamp(customTimePeriod[0].fromDate);
dayStartThruDate=UtilDateTime.toTimestamp(customTimePeriod[0].thruDate);

dayBegin = UtilDateTime.getDayStart(dayStartfromDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(dayStartThruDate, timeZone, locale);
context.put("dayStartfromDate", dayStartfromDate);
context.put("dayStartThruDate", dayStartThruDate);

printDate = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "dd/MM/yyyy");
context.printDate = printDate;

finalList =[];
facilityIdsList =[];
conditionList =[];
//Map<String, Object> resultMaplst=dispatcher.runSync("getPeriodBillingList", UtilMisc.toMap("billingTypeId","SHOPEE_RENT","customTimePeriodId",parameters.customTimePeriodId,"statusId","GENERATED","userLogin", userLogin));
//List<GenericValue> periodBillingList=(List<GenericValue>)resultMaplst.get("periodBillingList");

boothList = ByProductNetworkServices.getAllBooths(delegator, "SHP_RTLR").get("boothsDetailsList");
boothList = (List)((Map)ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator, "SHP_RTLR" ,dayBegin)).get("boothActiveList");
boothList.each{facility ->
	Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
	inputRateAmt.put("rateCurrencyUomId", "INR");
	inputRateAmt.put("facilityId", facility.facilityId);
	inputRateAmt.put("fromDate",dayBegin);
	inputRateAmt.put("rateTypeId", "SHOPEE_RENT");
	
	facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
	BigDecimal rateAmount=(BigDecimal)facilityRateResult.get("rateAmount");
	BigDecimal basicRateAmount=BigDecimal.ZERO;;
	if (rateAmount>0) {
	    basicRateAmount = (rateAmount.divide(new BigDecimal(1.1236) , 0, rounding));
	}
	tempMap =[:];
	tempMap.put("boothId", facility.facilityId);
	tempMap.put("facilityName", facility.facilityName);
	tempMap.put("rateAmount", rateAmount);
	tempMap.put("basicRateAmount", basicRateAmount);
	finalList.addAll(tempMap);
}
context.putAt("finalList", finalList);









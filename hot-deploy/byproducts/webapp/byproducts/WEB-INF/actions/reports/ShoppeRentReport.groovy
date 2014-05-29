import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
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

customTimePeriod=delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("customTimePeriodId", parameters.customTimePeriodId), null,null, null, false);
dayStartfromDate=UtilDateTime.toTimestamp(customTimePeriod[0].fromDate);
dayStartThruDate=UtilDateTime.toTimestamp(customTimePeriod[0].thruDate);

dayBegin = UtilDateTime.getDayStart(dayStartfromDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(dayStartThruDate, timeZone, locale);
context.put("dayStartfromDate", dayStartfromDate);
context.put("dayStartThruDate", dayStartThruDate);

decimals = UtilNumber.getBigDecimalScale("ledger.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("ledger.rounding");

finalList =[];
facilityIdsList=ByProductNetworkServices.getAllBooths(delegator,null).get("boothsList");
facilityIdsList. each {facilityId ->
	facilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
	boothFacilityId = facilityDetails.get("facilityId");
	facilityName = facilityDetails.get("description");
	Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
	inputRateAmt.put("rateCurrencyUomId", "INR");
	inputRateAmt.put("facilityId", boothFacilityId);
	inputRateAmt.put("fromDate",dayBegin);
	inputRateAmt.put("rateTypeId", "SHOPEE_RENT");
	
	facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
	BigDecimal rateAmount=(BigDecimal)facilityRateResult.get("rateAmount");
	BigDecimal basicRateAmount = rateAmount.divide(new BigDecimal(1.1236), rounding);
	
	tempMap =[:];
	tempMap.put("boothId", boothFacilityId);
	tempMap.put("facilityName", facilityName);
	tempMap.put("rateAmount", rateAmount);
	tempMap.put("basicRateAmount", basicRateAmount);
	finalList.addAll(tempMap);
}
context.putAt("finalList", finalList);









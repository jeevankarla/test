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
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;

import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
periodBillingId = null;
if(parameters.periodBillingId){
	periodBillingId = parameters.periodBillingId;
}else{
	context.errorMessage = "No PeriodBillingId Found";
	return;
}
facilityCommissionList = [];
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dctx = dispatcher.getDispatchContext();
monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
conditionList = [];
conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
conditionList.add(EntityCondition.makeCondition("commissionDate", EntityOperator.EQUALS , monthBegin));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
EntityFindOptions findOptions = new EntityFindOptions();
routesList = delegator.findList("FacilityAndCommission",condition,["facilityId"]as Set, UtilMisc.toList("parentFacilityId","facilityId"),findOptions,false);
routeMarginMap =[:];
masterList=[];
grTotalMap =[:];
supplyDate = monthBegin;
Map transporterMargins= new LinkedHashMap();
routesList.each{ route ->
	TransporterMarginReportList =[];
	dayTotalsMap = [:];
	grTotalMap["grTotQty"]=BigDecimal.ZERO;
	grTotalMap["grTotRtAmount"]=BigDecimal.ZERO;
	grTotalMap["grTotpendingDue"]=BigDecimal.ZERO;
	grTotalMap["monthBill"]=BigDecimal.ZERO;
	for(int i=1 ; i <= (UtilDateTime.getIntervalInDays(monthBegin,monthEnd)+1); i++){
		dayOfMonth = i;
		dayTotalsMap[(String)i] = [:];
		dayTotalsMap[(String)i].putAll(routeMarginMap);
	}
	routesRateAmount =[:];
	dayTotalsMap["Tot"] =[:];
	dayTotalsMap["Tot"].putAll(grTotalMap);
	TransporterMarginReportList.add(dayTotalsMap);
	transporterMargins[route.facilityId]=TransporterMarginReportList;
}
facilityCommissionList = delegator.findList("FacilityCommission",EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId) , null, ["commissionDate"], null, false);

if(UtilValidate.isNotEmpty(facilityCommissionList)){
	facilityCommissionList.each { facilityCommission ->
		facilityId = facilityCommission.facilityId;
		facilityRateResult=[:];
		rateMap =[:];
		rateList =[];
		List<GenericValue> facilities = delegator.findList("FacilityPersonAndFinAccount", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);
		facility = EntityUtil.getFirst(facilities);

		Map inputRateAmt =  UtilMisc.toMap("userLogin", userLogin);
			inputRateAmt.put("rateCurrencyUomId", "INR");
			inputRateAmt.put("facilityId", facilityId);
			inputRateAmt.put("fromDate",monthBegin );
			inputRateAmt.put("rateTypeId", "TRANSPORTER_MRGN");
			facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
		
		List dayTotalsList =  transporterMargins[facilityId];
		dayValuesMap = dayTotalsList.get(0);
		routeValueMap =[:];
		totalsMap =[:];
		totalsMap = dayValuesMap["Tot"];
		totalsMap.put("partyCode", facility.ownerPartyId);
		totalsMap.put("distance", facility.facilitySize);
		String partyName = "";
			if(UtilValidate.isNotEmpty(person.firstName)){
		       partyName=facility.firstName;
			}
			if(UtilValidate.isNotEmpty(facility.lastName)){
				partyName=facility.firstName+","+facility.lastName;
			}
			if(UtilValidate.isNotEmpty(facility.panId)){
			    totalsMap.put("panId",facility.panId);
			}
			if(UtilValidate.isNotEmpty(facility.closedDate)){
				closedDate=UtilDateTime.toDateString(facility.closedDate, "dd-MMM-yyyy");
				totalsMap.put("closedDate", closedDate);
			}
			if(UtilValidate.isNotEmpty(facility.finAccountCode) && "FNACT_ACTIVE".equals(facility.statusId)){
				totalsMap.put("accNo", facility.finAccountCode);
			}
			if(UtilValidate.isNotEmpty(facility.facilityCode)){
				totalsMap.put("facilityCode", facility.facilityCode);
			}
		   totalsMap.put("partyName",partyName);
		
		if(UtilValidate.isNotEmpty(facilityRateResult)){
			monthBeginMargin = (BigDecimal) facilityRateResult.get("rateAmount");
			uomId=(String)facilityRateResult.get("uomId");
			totalsMap.put("uomId", uomId);
			totalsMap.put("margin", monthBeginMargin);
		}
		int dayInteger = UtilDateTime.getDayOfMonth((facilityCommission.commissionDate),timeZone, locale);
		routeValueMap = dayValuesMap[(String)dayInteger];
		routeValueMap["totalQuantity"]=BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(facilityCommission.totalQty)){
			routeValueMap["totalQuantity"] = ((new BigDecimal(facilityCommission.totalQty)).setScale(1,BigDecimal.ROUND_HALF_UP));
			totalsMap["grTotQty"] += ((new BigDecimal(facilityCommission.totalQty)).setScale(1,BigDecimal.ROUND_HALF_UP));
		}
		routeValueMap["rtAmount"] = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(facilityCommission.totalAmount)){
			routeValueMap["rtAmount"] = ((new BigDecimal(facilityCommission.totalAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
			totalsMap["grTotRtAmount"] += ((new BigDecimal(facilityCommission.totalAmount)).setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		routeValueMap["pendingDue"] =BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(facilityCommission.dues)){
			routeValueMap["pendingDue"] = ((new BigDecimal(facilityCommission.dues)).setScale(2,BigDecimal.ROUND_HALF_UP));
		totalsMap["grTotpendingDue"] += ((new BigDecimal(facilityCommission.dues)).setScale(2,BigDecimal.ROUND_HALF_UP));

		}		
	}
}
masterList.add(transporterMargins);
context.put("masterList", masterList);

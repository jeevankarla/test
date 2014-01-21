import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
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
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

/*customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);*/
fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;
Timestamp fromDate ;
Timestamp thruDate ;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (UtilValidate.isNotEmpty(fromDateStr)) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDateStr).getTime()));
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(fromDateStr).getTime()));
	}else{
		context.errorMessage = "From Date Not Selected " ;
		return;
	}
	if (UtilValidate.isNotEmpty(thruDateStr)) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDateStr).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("fromDate", fromDate);
context.putAt("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
conditionList =[];
facilityId = parameters.unitId ;

centerListMaps = [];
centerListMaps = ProcurementNetworkServices.getUnitAgents(dctx ,[unitId:facilityId]);
centersList = [];
centersList = centerListMaps.get("agentsList");
centerMap=[:];

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
productMap =[:];

centersList.each{ center ->
	centerWiseMap =[:];
	
	centerId = center.facilityId;
	productMap =[:];
	procurementProductList.each{ procProduct ->
		productId =procProduct.productId;
		Map inputFacRateAmt = UtilMisc.toMap("userLogin", userLogin);
		inputFacRateAmt.put("facilityId", centerId);
		inputFacRateAmt.put("rateTypeId", "PROC_AGENT_BONUS");
		inputFacRateAmt.put("rateCurrencyUomId", "INR");
		inputFacRateAmt.put("productId", productId);
		facilityRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
		productMap[procProduct.productName]=facilityRateAmount.rateAmount;
	}
	//for getting return amount rate
	Map inMap = UtilMisc.toMap("userLogin", userLogin);
	inMap.put("facilityId", centerId);
	inMap.put("rateTypeId", "PROC_AGENT_RETNBONUS");
	inMap.put("rateCurrencyUomId", "INR");
	facilityRate = dispatcher.runSync("getProcurementFacilityRateAmount", inMap);
	returnRate =facilityRate.rateAmount;
	
	centerWiseMap["centerId"]=centerId;
	centerWiseMap["parentFacilityId"]=center.parentFacilityId;
	centerWiseMap["centerName"]=center.facilityName;
	centerWiseMap["facilityCode"]=center.facilityCode;
	centerWiseMap["ownerPartyId"]=center.ownerPartyId;
	centerWisePeriodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId: centerId]);
	Iterator centerTotalsMapItr =centerWisePeriodTotals.entrySet().iterator();
	
	centerWiseMap["BMLtrs"] = BigDecimal.ZERO;
	centerWiseMap["BMAmt"] = BigDecimal.ZERO;
	centerWiseMap["CMLtrs"] = BigDecimal.ZERO;
	centerWiseMap["CMAmt"] = BigDecimal.ZERO;
	centerWiseMap["RetnAmt"] = BigDecimal.ZERO;
	while (centerTotalsMapItr.hasNext()) {
		Map.Entry centerEntry = centerTotalsMapItr.next();
		Map centerWiseValuesMap = (Map)centerEntry.getValue();
		Map centerWiseValues = ((Map)((Map)((Map)centerWiseValuesMap.get("dayTotals")).get("TOT")).get("TOT")).get("TOT");
		//for milkline
		centerMilkDetails = centerWiseValuesMap.get("dayTotals").get("TOT").get("TOT");
		centerWiseMap["BMLtrs"] = centerMilkDetails.get("Buffalo Milk").get("qtyLtrs");
		centerWiseMap["CMLtrs"] = centerMilkDetails.get("Cow Milk").get("qtyLtrs");
		centerWiseMap["BMAmt"] = ((centerMilkDetails.get("Buffalo Milk").get("qtyLtrs"))*productMap.get("Buffalo Milk"));
		centerWiseMap["CMAmt"] = ((centerMilkDetails.get("Cow Milk").get("qtyLtrs"))*productMap.get("Cow Milk"));
		centerWiseMap["RetnAmt"] = ((centerMilkDetails.get("Buffalo Milk").get("qtyLtrs")+centerMilkDetails.get("Cow Milk").get("qtyLtrs"))*returnRate);
	}	
	centerMap[centerId]=[:];
	centerMap[centerId].putAll(centerWiseMap);
}
context.put("centerMap", centerMap);








































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
dctx = dispatcher.getDispatchContext();
shedWiseTotalsMap = [:];
	shedTotalMap=[:];
	finalUnitMap = [:];
	totQtyLtrs=0;
	totQtyKgs=0;
	totKgFat=0;
	totKgSnf=0;
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: parameters.shedId]);
 	unitsDetailList = shedUnitDetails.get("unitsDetailList");
	unitsList = UtilMisc.sortMaps(unitsDetailList, UtilMisc.toList("mccCode"));
 	unitsList.each{ unit->
		unitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: unit.facilityId]);
		if(UtilValidate.isNotEmpty(unitTotals)){
			facilityTotals = unitTotals.get(unit.facilityId);
			if(UtilValidate.isNotEmpty(facilityTotals)){
				dayTotals = facilityTotals.get("dayTotals");
				if(UtilValidate.isNotEmpty(dayTotals)){
					Iterator dayTotIter = dayTotals.entrySet().iterator();
					while(dayTotIter.hasNext()){
						Map.Entry entry = dayTotIter.next();
						if("TOT".equals(entry.getKey())){
							unitTotalMap = [:];
							unitTotalMap["qtyLtrs"] = 0;
							unitTotalMap["qtyKgs"] = 0;
							unitTotalMap["kgFat"] = 0;
							unitTotalMap["kgSnf"] = 0;
							unitTotalMap["fat"] = 0;
							unitTotalMap["snf"] = 0;
							unitTotalMap.put("qtyLtrs", entry.getValue().get("TOT").get("recdQtyLtrs"));
							unitTotalMap.put("qtyKgs", entry.getValue().get("TOT").get("recdQtyKgs"));
							unitTotalMap.put("kgFat", entry.getValue().get("TOT").get("recdKgFat"));
							unitTotalMap.put("kgSnf", entry.getValue().get("TOT").get("recdKgSnf"));
							unitTotalMap.put("fat", entry.getValue().get("TOT").get("receivedFat"));
							unitTotalMap.put("snf", entry.getValue().get("TOT").get("receivedSnf"));
							totQtyLtrs = totQtyLtrs+entry.getValue().get("TOT").get("recdQtyLtrs");
							totQtyKgs = totQtyKgs+entry.getValue().get("TOT").get("recdQtyKgs");
							totKgFat = totKgFat+entry.getValue().get("TOT").get("recdKgFat");
							totKgSnf = totKgSnf+entry.getValue().get("TOT").get("recdKgSnf");
							finalUnitMap.put(unit.facilityId, unitTotalMap);
						}
					}
				}
			}
		}
	}
	 shedTotalMap.put("totQtyLtrs", totQtyLtrs);
	 shedTotalMap.put("totQtyKgs", totQtyKgs);
	 shedTotalMap.put("totKgFat", totKgFat);
	 shedTotalMap.put("totKgSnf", totKgSnf);
	 finalUnitMap.put("TOTAL", shedTotalMap);
	 shedWiseTotalsMap.put(parameters.shedId, finalUnitMap);

context.putAt("shedWiseTotalsMap", shedWiseTotalsMap);



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

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	}
	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.unitId ; 
unitFacilityMap = [:];
unitFacilityMap = delegator.findOne("Facility",[facilityId:facilityId],false);
unitCode = null;
if(ServiceUtil.isError(unitFacilityMap)){
	context.errorMessage = "Unit Not Found" ; 
	return;
	}else{
	unitCode = unitFacilityMap.get("facilityCode");
	unitName = unitFacilityMap.get("facilityName");
	}
centerListMaps = []; 
centerListMaps = ProcurementNetworkServices.getUnitAgents(dctx ,[unitId:facilityId]);
centersList = [];
centersList = centerListMaps.get("agentsList");
centerTotalsList = [];
unitTotalsMap = [:];
BigDecimal grBMTotals = 0;
BigDecimal grCMTotals = 0;
BigDecimal grTotalQty = 0;
for(centerMap in centersList){
	centerDetailsMap = [:];
	centerId = centerMap.get("facilityId");
	centerFacilityMap = delegator.findOne("Facility",[facilityId:centerId],false);
	if(!ServiceUtil.isError(centerFacilityMap)){
		centerDetailsMap.put("centerCode",centerFacilityMap.get("facilityCode"));
		centerDetailsMap.put("centerName",centerFacilityMap.get("facilityName"));
	}
	centerTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId:centerId,userLogin:userLogin]);
	if(centerTotals){
		totalsMap = centerTotals.get(centerId).get("dayTotals").get("TOT").get("TOT");
		centerBMTotal = ProcurementNetworkServices.convertKGToLitre(new BigDecimal( totalsMap.get("Buffalo Milk").get("qtyKgs")));
		centerCMTotal = ProcurementNetworkServices.convertKGToLitre(new BigDecimal(totalsMap.get("Cow Milk").get("qtyKgs")));
		centerTotalQuantity = ProcurementNetworkServices.convertKGToLitre(new BigDecimal(totalsMap.get("TOT").get("qtyKgs")));
		grBMTotals = grBMTotals+centerBMTotal;
		grCMTotals = grCMTotals+centerCMTotal;
		grTotalQty = grTotalQty+centerTotalQuantity;
		centerDetailsMap.put("BMTotal",centerBMTotal);
		centerDetailsMap.put("CMTotal",centerCMTotal);
		centerDetailsMap.put("totalqty",centerTotalQuantity);
		centerTotalsList.add(centerDetailsMap);
		}
}
centerTotalsMap = [:];
if(centerTotalsList){
	centerTotalsMap.put("centerTotalsList",centerTotalsList);
	}
unitTotalsMap.put("grBMTotals",grBMTotals);
unitTotalsMap.put("grCMTotals",grCMTotals);
unitTotalsMap.put("grTotalQty",grTotalQty);
unitTotalsMap.put("unitCode",unitCode);
unitTotalsMap.put("unitName",unitName);
context.put("unitTotalsMap",unitTotalsMap);
context.put("centerTotalsMap",centerTotalsMap);
context.put("centerTotalsList",centerTotalsList);





 

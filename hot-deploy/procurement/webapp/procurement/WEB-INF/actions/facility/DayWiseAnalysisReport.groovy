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
import in.vasista.vbiz.procurement.PriceServices;


procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;
fromDate = parameters.procurementDate;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(parameters.procurementDate).getTime());
	context.put("fromDateTime", fromDateTime);
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
conditionList =[];
if(parameters.unitId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, parameters.unitId)));
}
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"UNIT")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);
unitRouteMap =[:]; 
unitsList.each{ unit ->
	unitRoutes = ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId", unit.facilityId));
	routeList = unitRoutes.get("routesList");
	routeValueMap =[:];
	routeList.each{ route ->
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, route)));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"CENTER")));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		agentsList = delegator.findList("Facility",condition,null,null,null,false);
		agentEntryDetails =[:];
		agentsList.each{ agent ->
			agentDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDateTime , thruDate: fromDateTime , facilityId: agent.facilityId]);
			 if(UtilValidate.isNotEmpty(agentDayTotals)){
				 agentEntryDetails.putAll(agentDayTotals);
			 }			
		}
		routeValueMap[route]=[:];
		routeValueMap[route].putAll(agentEntryDetails);		
	}
	unitRouteMap[unit.facilityId] =[:];
	unitRouteMap[unit.facilityId].putAll(routeValueMap);
 }
purchageTimeMap =[:];
purchageTimeMap.put("AM","");
purchageTimeMap.put("PM",""); 
milkTypeMap =[:];
milkTypeMap.put("Buffalo Milk","");
milkTypeMap.put("Cow Milk","");
DayGrandTotalsMap =[:];
if(parameters.unitId){
	UnitWisePeriodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDateTime , thruDate: fromDateTime , facilityId: parameters.unitId]);
	Iterator unitTotalsMapItr =UnitWisePeriodTotals.entrySet().iterator();
	while (unitTotalsMapItr.hasNext()) {
		Map.Entry unitEntry = unitTotalsMapItr.next();
		Map unitValuesMap = (Map)unitEntry.getValue();
		DayGrandTotalsMap = ((Map)unitValuesMap.get("dayTotals"));
		
	}
}
context.putAt("DayGrandTotalsMap", DayGrandTotalsMap);
context.put("milkTypeMap",milkTypeMap);
context.put("purchageTimeMap",purchageTimeMap);
context.putAt("unitRouteMap", unitRouteMap);






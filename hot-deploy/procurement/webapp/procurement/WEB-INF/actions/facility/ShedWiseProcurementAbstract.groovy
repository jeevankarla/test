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
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
conditionList =[];
unitTotalsMap =[:];
if(parameters.shedId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, parameters.shedId)));
}
//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"UNIT")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);
unitsList.each{ unit ->	
	unitTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId: unit.facilityId]);
	if(UtilValidate.isNotEmpty(unitTotals)){
		unitTotalsMap[unit.facilityId] =[:];
		unitTotalsMap[unit.facilityId].putAll(unitTotals);
	}
}
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;
shedTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId:  parameters.shedId]);
Map shedWiseTotalValues =[:];
Iterator mapIter = shedTotals.entrySet().iterator();
	while(mapIter.hasNext()){
		Map.Entry entry = mapIter.next();
		Map shedValuesMap = (Map)entry.getValue();
		shedWiseTotalValues = ((Map)((Map)((Map)shedValuesMap.get("dayTotals")).get("TOT")).get("TOT"));	
	}
context.putAt("shedWiseTotalValues", shedWiseTotalValues);
context.put("unitTotalsMap", unitTotalsMap);

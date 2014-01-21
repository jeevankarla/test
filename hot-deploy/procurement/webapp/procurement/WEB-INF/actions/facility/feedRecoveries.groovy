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
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

unitRoutesList = ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",parameters.unitId ));
routesDetailsList = unitRoutesList.get("routesDetailList");
routeCodesMap = [:];
routesDetailsList.each { route->
	routeCodesMap[route.facilityId] = route.facilityCode; 
}

unitCentersList = ProcurementNetworkServices.getUnitAgents(dctx,UtilMisc.toMap("unitId",parameters.unitId ));
centersList = unitCentersList.get("agentsList");
centersList = UtilMisc.sortMaps(centersList, UtilMisc.toList("facilityCode"));
adjustments =[:];
adjustmentsTotMap = [:];

orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.IN , UtilMisc.toList("MILKPROC_ADDITIONS","MILKPROC_DEDUCTIONS")),null,null,null,false);
context.put("orderAdjItemsList",orderAdjItemsList);
orderAdjItemsList.each { orderAdj ->
	adjustmentsTotMap[orderAdj.orderAdjustmentTypeId] = 0;
}
centersList.each{ center ->
	agentAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDateTime , thruDate: thruDateTime, facilityId: center.facilityId]);
  adjustmentsMap =[:];
  if(UtilValidate.isNotEmpty(agentAdjustments)){
	adjustmentsTypeValues = agentAdjustments.get("adjustmentsTypeMap");
	 if(adjustmentsTypeValues !=null){
		 adjustmentsMap["routeNo"] = routeCodesMap.get(center.parentFacilityId);
		 adjustmentsMap["centerCode"] = center.facilityCode;
		 adjustmentsMap["centerName"] = center.facilityName;
		 adjustmentsTypeValues.each{ adjustmentValues ->
			if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
				additionsList = adjustmentValues.getValue();
				additionsList.each{ additionValues ->
					adjustmentsMap[additionValues.getKey()] = 0;
					adjustmentsMap[additionValues.getKey()] = additionValues.getValue();
					adjustmentsTotMap[additionValues.getKey()] += additionValues.getValue();
				
				}
			}else{
				deductionsList = adjustmentValues.getValue();
				deductionsList.each{ deductionValues ->
					adjustmentsMap[deductionValues.getKey()] = 0;
					adjustmentsMap[deductionValues.getKey()] = deductionValues.getValue();
					adjustmentsTotMap[deductionValues.getKey()] += deductionValues.getValue();
				}
			}
		 }
	  }   } 
  if(UtilValidate.isNotEmpty(adjustmentsMap)){
	adjustments[center.facilityId]=adjustmentsMap;
  }
}
Debug.log("adjustments----------------------------------------"+adjustments);
context.put("adjustmentsTotMap",adjustmentsTotMap);
context.put("adjustments",adjustments);

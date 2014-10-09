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

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	   if (parameters.thruDate) {
			   thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
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

codeList=[];
sortedCodeList=[];
finalMap = [:];
unitCentersList = ProcurementNetworkServices.getUnitAgents(dctx,UtilMisc.toMap("unitId",parameters.unitId ));
centersList = unitCentersList.get("agentsList");
List tempCentersList = FastList.newInstance();
centersList = UtilMisc.sortMaps(centersList, UtilMisc.toList("facilityCode"));
for(center in centersList){
	Map tempCenterMap = FastMap.newInstance();
	tempCenterMap.put("facilityId", center.get("facilityId"));
	tempCenterMap.put("facilityName", center.get("facilityName"));
	tempCenterMap.put("facilityCode",Integer.parseInt(center.get("facilityCode")));
	tempCentersList.add(tempCenterMap);
}
tempCentersList = UtilMisc.sortMaps(tempCentersList, UtilMisc.toList("facilityCode"));
context.putAt("tempCentersList", tempCentersList);
unitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: parameters.unitId,includeCenterTotals: true]);
if(UtilValidate.isNotEmpty(unitTotals) && ServiceUtil.isSuccess(unitTotals)){
	Map annualValuesMap = (Map)unitTotals.get("centerWiseTotals");
	Iterator centerIter = annualValuesMap.entrySet().iterator();
	while(centerIter.hasNext()){
		Map.Entry centerEntry = centerIter.next();
		Map centerValuesMap = (Map)centerEntry.getValue();
		Iterator finalIter = centerValuesMap.entrySet().iterator();
		while(finalIter.hasNext()){
			Map.Entry finalEntry = finalIter.next();
			if("TOT".equals(finalEntry.getKey())){
				Map finalTotalMap = (Map)finalEntry.getValue();
				Iterator finaltotIter = finalTotalMap.entrySet().iterator();
				while(finaltotIter.hasNext()){
					Map.Entry finaltotEntry = finaltotIter.next();
					if("TOT".equals(finaltotEntry.getKey())){
						finalValue = finaltotEntry.getValue().get(parameters.fieldName);
						finalFieldValue = (finalValue).setScale(2,BigDecimal.ROUND_HALF_UP);
						finalMap.put(centerEntry.getKey(),finalFieldValue);
					}
				}
			}
		}
	}
}
context.putAt("finalMap", finalMap);

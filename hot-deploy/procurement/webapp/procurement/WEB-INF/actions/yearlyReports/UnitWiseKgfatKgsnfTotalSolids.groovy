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
finalMap = [:];
totalAnnualMap = [:];
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx,[userLogin: userLogin,shedId: parameters.shedId]);

shedWiseTotalsMap = [:];
for(procProduct in procurementProductList){
	shedWiseTotalsMap[procProduct.brandName+"QtyKgs"] = 0;
	shedWiseTotalsMap[procProduct.brandName+"QtyLtrs"] = 0;
	shedWiseTotalsMap[procProduct.brandName+"kgFat"] = 0;
	shedWiseTotalsMap[procProduct.brandName+"kgSnf"] = 0;
}
if(UtilValidate.isNotEmpty(shedUnitDetails)){
	unitLists = shedUnitDetails.get("unitsList");
	unitLists.each{ unitId->
		unitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: unitId]);
		if(UtilValidate.isNotEmpty(unitTotals)){
			facilityTotals = unitTotals.get(unitId);
			if(facilityTotals != null){
				Iterator unitMapIter = facilityTotals.entrySet().iterator();
				while(unitMapIter.hasNext()){
					Map.Entry entry = unitMapIter.next();
					//Debug.log("================================================"+entry.getKey());
					/*if(!"TOT".equals(entry.getKey())){
						Map annualValuesMap = (Map)entry.getValue();
						Iterator annualMapIter = annualValuesMap.entrySet().iterator();
						while(annualMapIter.hasNext()){
							Map.Entry annualEntry = annualMapIter.next();
							Debug.log("annualEntry============"+annualEntry.getKey());
							if("TOT".equals(annualEntry.getKey())){
									Map annualMonthMap = (Map)annualEntry.getValue();
									Debug.log("annualMonthMap=============="+annualMonthMap.keySet());
									finalMap.put(unitId, annualMonthMap);
							}
						}
					}*/
					if("TOT".equals(entry.getKey())){
					   totalAnnualMap = (Map)entry.getValue();
					  // finalMap.put(unitId, totalAnnualMap);
					   if(UtilValidate.isNotEmpty(totalAnnualMap)){
						   finalMap.put(unitId, totalAnnualMap);
						   //Debug.log("totalAnnualMap=============="+totalAnnualMap.keySet());
						   for(procProduct in procurementProductList){
							   shedWiseTotalsMap[procProduct.brandName+"QtyKgs"] +=  (totalAnnualMap.get(procProduct.productId).get("qtyKgs"));
							   shedWiseTotalsMap[procProduct.brandName+"QtyLtrs"] += (totalAnnualMap.get(procProduct.productId).get("qtyLtrs"));
							   shedWiseTotalsMap[procProduct.brandName+"kgFat"] += totalAnnualMap.get(procProduct.productId).get("kgFat");
							   shedWiseTotalsMap[procProduct.brandName+"kgSnf"] += totalAnnualMap.get(procProduct.productId).get("kgSnf");
						   }
					   }
				    }
				}
			}
		}
	}
}
context.putAt("finalMap",finalMap);
context.putAt("shedWiseTotalsMap",shedWiseTotalsMap);
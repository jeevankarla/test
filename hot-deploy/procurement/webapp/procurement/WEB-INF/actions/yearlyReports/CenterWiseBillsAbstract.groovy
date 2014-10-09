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

facilityDetail = delegator.findOne("Facility", [facilityId : parameters.unitId], false);
if(UtilValidate.isNotEmpty(facilityDetail)){
	unitName = facilityDetail.facilityName;
	unitCode = facilityDetail.facilityCode;
	context.put("unitName",unitName);
	context.put("unitCode",unitCode);
	context.put("shedId",facilityDetail.parentFacilityId);
}else{
	return;
}

dctx = dispatcher.getDispatchContext();
context.put("dctx", dctx);

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.put("procurementProductList", procurementProductList);

adjustmentDedTypes = [:];
orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
orderAdjItemsList = UtilMisc.sortMaps(orderAdjItemsList, UtilMisc.toList("sequenceNum"));

initAdjMap =[:];
for(int i=0;i<orderAdjItemsList.size();i++){
	orderAdjItem = orderAdjItemsList.get(i);
	adjustmentDedTypes[i] = orderAdjItem;
	initAdjMap[orderAdjItem.orderAdjustmentTypeId] = 0;
}
testAdjMap = [:];
for(int j=0;j < 12;j++){
	if(UtilValidate.isNotEmpty(adjustmentDedTypes.get(j))){
		testAdjMap[j] = (adjustmentDedTypes.get(j).orderAdjustmentTypeId);
	}else{
		testAdjMap[j] = 0.00;
	}
}
context.put("testAdjMap",testAdjMap);
context.put("adjustmentDedTypes",adjustmentDedTypes);

	unitMilkBillAbstMap =[:]
	finalMap = [:];
	totalMap = [:];
	annualPeriodTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId: parameters.unitId, includeCenterTotals: true]);
	if(UtilValidate.isNotEmpty(annualPeriodTotals) && ServiceUtil.isSuccess(annualPeriodTotals)){
		Iterator annualPeriodIter = annualPeriodTotals.entrySet().iterator();
		while(annualPeriodIter.hasNext()){
			Map.Entry entry = annualPeriodIter.next();
			if(entry.getKey()=="centerWiseTotals"){
				Map annualValuesMap = (Map)entry.getValue();
				Iterator centerIter = annualValuesMap.entrySet().iterator();
				while(centerIter.hasNext()){
					Map.Entry centerEntry = centerIter.next();
					Map centerValuesMap = (Map)centerEntry.getValue();
					Iterator finalIter = centerValuesMap.entrySet().iterator();
					while(finalIter.hasNext()){
						Map.Entry finalEntry = finalIter.next();
						if("TOT".equals(finalEntry.getKey())){
							Map finalTotalMap = (Map)finalEntry.getValue();
							finalMap.put(centerEntry.getKey(), finalTotalMap);
						}
					}
				}
			}else{
				Map totalValuesMap = (Map)entry.getValue();
				Iterator totalIter = totalValuesMap.entrySet().iterator();
				while(totalIter.hasNext()){
					Map.Entry totalEntry = totalIter.next();
					if("TOT".equals(totalEntry.getKey())){
						totalMap = (Map)totalEntry.getValue();
					}
				}
			}
		}
	}
context.put("finalMap",finalMap);
context.put("totalMap",totalMap);

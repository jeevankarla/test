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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

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
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

dctx = dispatcher.getDispatchContext();

orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
orderAdjItemsList = UtilMisc.sortMaps(orderAdjItemsList, UtilMisc.toList("sequenceNum"));
context.put("orderAdjItemsList",orderAdjItemsList);

totalValuesMap =[:];
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);

grandTotMap = [:];
shedMap = [:];
regionDetailMap = [:];
regionWiseMap = [:];
regionMap = [:];
	unitTotals = ProcurementReports.getRegionWisePeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate, facilityGroupId:parameters.facilityGroupId]);
	if(UtilValidate.isNotEmpty(unitTotals)){
		facilityTotals = unitTotals.get("periodTotalsMap");
		if(facilityTotals != null){
			Iterator regionWiseIter = facilityTotals.entrySet().iterator();
			while(regionWiseIter.hasNext()){
				Map.Entry regionEntry = regionWiseIter.next();
				if(!"TOT".equals(regionEntry.getKey())){
					List shedList=FastList.newInstance();
					Map regionValuesMap = (Map)regionEntry.getValue();
					if(UtilValidate.isNotEmpty(regionValuesMap)){
						Iterator shedWiseIter = regionValuesMap.entrySet().iterator();
						while(shedWiseIter.hasNext()){
							Map.Entry shedEntry = shedWiseIter.next();
							if(!"TOT".equals(shedEntry.getKey())){
								shedName= shedEntry.getKey();
								shedList.add(shedName);
								Map shedValuesMap = (Map)shedEntry.getValue().get("TOT");
								shedMap.put(shedName, shedValuesMap);
							}
							regionDetailMap.put(regionEntry.getKey(),shedList);
							if("TOT".equals(shedEntry.getKey())){
								regionMap = (Map)shedEntry.getValue();
							}
						}
					}
					regionWiseMap.put(regionEntry.getKey(), regionMap);
				}
				if("TOT".equals(regionEntry.getKey())){
					grandTotMap = (Map)regionEntry.getValue();
				}
			}
		}
	}
	
	context.put("shedMap", shedMap);
	context.put("regionMap", regionMap);
	context.put("regionDetailMap", regionDetailMap);
	context.put("regionWiseMap", regionWiseMap);
	context.put("grandTotMap",grandTotMap);

	
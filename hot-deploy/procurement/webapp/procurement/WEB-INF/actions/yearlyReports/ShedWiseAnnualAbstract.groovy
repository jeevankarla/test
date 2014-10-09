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


if(UtilValidate.isEmpty(parameters.shedId)){
	return;
}
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

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);

finalMap = [:];
shedWiseTotalsMap = [:];        
for(productId in procurementProductList){
	shedWiseTotalsMap[productId.brandName+"price"] = 0;
}
shedWiseTotalsMap["commissionAmount"] = 0;
shedWiseTotalsMap["cartage"] = 0;
shedWiseTotalsMap["opCost"] = 0;
shedWiseTotalsMap["grsAddn"] = 0;
shedWiseTotalsMap["grossAmt"] = 0;
orderAdjItemsList.each { orderAdj ->
	shedWiseTotalsMap[orderAdj.orderAdjustmentTypeId] = 0;
}
shedWiseTotalsMap["grsDed"] = 0;
shedWiseTotalsMap["netAmt"] = 0;
shedWiseTotalsMap["tipAmt"] = 0;

shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: parameters.shedId]);
if(UtilValidate.isNotEmpty(shedUnitDetails)){
	unitList = shedUnitDetails.get("unitsList");
	unitList.each{ unitId->
		unitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,userLogin: userLogin,facilityId: unitId]);
		if(UtilValidate.isNotEmpty(unitTotals)){
			facilityTotals = unitTotals.get(unitId);
			if(facilityTotals != null){
				Iterator unitMapIter = facilityTotals.entrySet().iterator();
				while(unitMapIter.hasNext()){
					Map.Entry entry = unitMapIter.next();
					if("TOT".equals(entry.getKey())){
						Map annualValuesMap = (Map)entry.getValue();
						finalMap.put(unitId, annualValuesMap);
						for(product in procurementProductList){
							productId=product.productId;
  						    shedWiseTotalsMap[product.brandName+"price"] += annualValuesMap.get(productId).get("price");
						}
						shedWiseTotalsMap["commissionAmount"] += annualValuesMap.get("TOT").get("commissionAmount");
						shedWiseTotalsMap["cartage"] += annualValuesMap.get("TOT").get("cartage");
						shedWiseTotalsMap["opCost"] += annualValuesMap.get("TOT").get("opCost");
						shedWiseTotalsMap["grsAddn"] += annualValuesMap.get("TOT").get("grsAddn");
    					shedWiseTotalsMap["grossAmt"]+= annualValuesMap.get("TOT").get("grossAmt");
						orderAdjItemsList.each { orderAdj ->
							if(UtilValidate.isNotEmpty(annualValuesMap.get("TOT").get(orderAdj.orderAdjustmentTypeId))){
								shedWiseTotalsMap[orderAdj.orderAdjustmentTypeId] += annualValuesMap.get("TOT").get(orderAdj.orderAdjustmentTypeId);
							}
						}   
						shedWiseTotalsMap["grsDed"]+= annualValuesMap.get("TOT").get("grsDed");
						shedWiseTotalsMap["netAmt"]+= annualValuesMap.get("TOT").get("netAmt");
						shedWiseTotalsMap["tipAmt"]+= annualValuesMap.get("TOT").get("tipAmt");
					}
				}
			}
		}
	}
}	
context.put("finalMap",finalMap);
context.put("shedWiseTotalsMap",shedWiseTotalsMap);




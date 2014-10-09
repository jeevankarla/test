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

dctx = dispatcher.getDispatchContext();

unitCode = null;
shedCode = null;
centerFacilityId = null;
facilityCode = parameters.facilityCode;
if(UtilValidate.isEmpty(parameters.shedId)){
	Debug.logError("shedId Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
if(UtilValidate.isEmpty(parameters.unitId)){
	Debug.logError("unitId Cannot Be Empty","");
	context.errorMessage = "No Unit Has Been Selected.......!";
	return;
}
if(UtilValidate.isEmpty(facilityCode)){
	Debug.logError("facilityCode Cannot Be Empty","");
	context.errorMessage = "Center Cannot Be Empty.......!";
	return;
}
unitFacility = delegator.findOne("Facility",[facilityId:parameters.unitId],false);
if(UtilValidate.isNotEmpty(unitFacility)){
	unitCode = unitFacility.facilityCode;
}
shedFacility = delegator.findOne("Facility",[facilityId:parameters.shedId],false);
if(UtilValidate.isNotEmpty(shedFacility)){
	shedCode = shedFacility.facilityCode;
}
GenericValue agentFacility = (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, UtilMisc.toMap("shedCode", shedCode,"centerCode", facilityCode,"unitCode",unitCode))).get("agentFacility");
if(UtilValidate.isNotEmpty(agentFacility)){
	centerFacilityId = agentFacility.facilityId;
}

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
context.put("centerFacilityId", centerFacilityId);

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.put("procurementProductList", procurementProductList);

finalMap = [:];
totalMap = [:];
if(UtilValidate.isNotEmpty(centerFacilityId)){
	annualPeriodTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId: centerFacilityId, includeCenterTotals: true]);
	if(UtilValidate.isNotEmpty(annualPeriodTotals)){
		facilityTotals = annualPeriodTotals.get(centerFacilityId);
		if(UtilValidate.isNotEmpty(facilityTotals)){
			Iterator annualPeriodIter = facilityTotals.entrySet().iterator();
			while(annualPeriodIter.hasNext()){
				Map.Entry entry = annualPeriodIter.next();
				if(!"TOT".equals(entry.getKey())){
					Map annualValuesMap = (Map)entry.getValue();
					if(UtilValidate.isNotEmpty(annualValuesMap)){
						Iterator monthMapIter = annualValuesMap.entrySet().iterator();
						while(monthMapIter.hasNext()){
							Map.Entry monthEntry = monthMapIter.next();
							if(!"TOT".equals(monthEntry.getKey())){
								Map monthValuesMap = (Map)monthEntry.getValue();
								if(UtilValidate.isNotEmpty(annualValuesMap)){
									Iterator periodIter = monthValuesMap.entrySet().iterator();
									while(periodIter.hasNext()){
										Map.Entry periodEntry = periodIter.next();
										if(!"TOT".equals(periodEntry.getKey())){
											Map periodMap = (Map)periodEntry.getValue();
											finalMap.put(periodEntry.getKey(),periodMap);
										}
									}
								}
							}
						}
					}
				}
				if("TOT".equals(entry.getKey())){
					totalMap = (Map)entry.getValue();
				}
			}
		}
	}
}
Map tempFinalMap = FastMap.newInstance();
List tempSeqList = FastList.newInstance();
if(UtilValidate.isNotEmpty(finalMap)){
	for(key in finalMap.keySet()){
			Map tempMap = FastMap.newInstance();
			
	       periodBillingDetails = delegator.findOne("PeriodBilling",[periodBillingId:key],false);
	       customPeriodId = periodBillingDetails.get("customTimePeriodId");
	       custPeriodDetails = delegator.findOne("CustomTimePeriod",[customTimePeriodId:customPeriodId],false);
	       fromDateStr = UtilDateTime.toDateString(custPeriodDetails.fromDate, "yyyyMMdd");
		   tempMap.put("periodId", key);
		   tempMap.put("seqNum", fromDateStr);
		   tempSeqList.add(tempMap);
	       }
	
	tempSeqList = UtilMisc.sortMaps(tempSeqList, UtilMisc.toList("seqNum"));
	
	for(tmpListEl in tempSeqList){
		tempFinalMap.put(tmpListEl.get("periodId"), finalMap.get(tmpListEl.get("periodId")));
		}
}
context.put("finalMap", tempFinalMap);
context.put("totalMap", totalMap);





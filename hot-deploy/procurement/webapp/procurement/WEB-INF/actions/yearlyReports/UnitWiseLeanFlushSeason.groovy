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
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.firstYearId;
}

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=customTimePeriod.getDate("fromDate");
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
leanStart=UtilDateTime.getDayStart( UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
leanAdd= UtilDateTime.addDaysToTimestamp(leanStart, 180);
leanEnd = UtilDateTime.getDayEnd(UtilDateTime.getMonthEnd(leanAdd, timeZone, locale));
flushStart=UtilDateTime.addDaysToTimestamp(leanEnd, 1);
flushAdd= UtilDateTime.addDaysToTimestamp(flushStart, 180);
flushEnd=UtilDateTime.getMonthEnd(flushAdd, timeZone, locale);
totalDay = UtilDateTime.getIntervalInDays(leanStart,leanEnd)+1;
totalD = UtilDateTime.getIntervalInDays(flushStart,flushEnd)+1;
context.put("leanStart", leanStart);
context.put("leanEnd", leanEnd);
context.put("flushStart", flushStart);
context.put("flushEnd", flushEnd);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
context.put("totalDay", totalDay);
context.put("totalD", totalD);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

finalMap = [:];
totalFlushMap = [:];
shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx,[userLogin: userLogin,shedId: parameters.shedId]);
if(UtilValidate.isNotEmpty(shedUnitDetails)){
	unitLists = shedUnitDetails.get("unitsList");
	unitLists.each{ unitId->
		finalUnitMap =[:];
		unitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: leanStart , thruDate: leanEnd,userLogin: userLogin,facilityId: unitId]);
		List<String> facilityIds= FastList.newInstance();
		Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", unitId));
		if(UtilValidate.isNotEmpty(facilityAgents)){
			facilityIds= (List) facilityAgents.get("facilityIds");
		}
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("fat", EntityOperator.NOT_EQUAL, null));
		conditionList.add(EntityCondition.makeCondition("snf", EntityOperator.NOT_EQUAL, null));
		conditionList.add(EntityCondition.makeCondition("unitPrice", EntityOperator.NOT_EQUAL, null));
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, leanStart));
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,leanEnd));
		conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
		EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		daysList = delegator.findList("OrderHeaderItemProductAndFacility", cond, null,null, null, false);
		PeriodIds = EntityUtil.getFieldListFromEntityList(daysList,"estimatedDeliveryDate",true);
		Set dateSet=new HashSet();
		for(date in PeriodIds){
			String tempDate = (String)UtilDateTime.toDateString(date,"MMM/dd/yyyy");
			dateSet.add(tempDate);
		}
		unitdays=dateSet.size();
		unitT = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate:flushStart , thruDate: flushEnd,userLogin: userLogin,facilityId: unitId]);
		List<String> facilityFlushIds= FastList.newInstance();
		Map facilityAgents1 = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", unitId));
		if(UtilValidate.isNotEmpty(facilityAgents1)){
			facilityFlushIds= (List) facilityAgents1.get("facilityIds");
		}
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("fat", EntityOperator.NOT_EQUAL, null));
		conditionList.add(EntityCondition.makeCondition("snf", EntityOperator.NOT_EQUAL, null));
		conditionList.add(EntityCondition.makeCondition("unitPrice", EntityOperator.NOT_EQUAL, null));
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, flushStart));
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,flushEnd));
		conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityFlushIds));
		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		daysListFlush = delegator.findList("OrderHeaderItemProductAndFacility", condition, null,null, null, false);
		PeriodIdsFlush = EntityUtil.getFieldListFromEntityList(daysListFlush,"estimatedDeliveryDate",true);
		Set dateSetFlush=new HashSet();
		for(flushDate in PeriodIdsFlush){
			String tempFlushDate = (String)UtilDateTime.toDateString(flushDate,"MMM/dd/yyyy");
			dateSetFlush.add(tempFlushDate);
		}
		unitFlushdays=dateSetFlush.size();
		if(UtilValidate.isNotEmpty(unitTotals)){
			facilityTotals = unitTotals.get(unitId);
			if(facilityTotals != null){
				Iterator unitMapIter = facilityTotals.entrySet().iterator();
				while(unitMapIter.hasNext()){
					Map.Entry entry = unitMapIter.next();
					if(!"TOT".equals(entry.getKey())){
						Map annualValuesMap = (Map)entry.getValue();
						Iterator annualMapIter = annualValuesMap.entrySet().iterator();
						while(annualMapIter.hasNext()){
							Map.Entry annualEntry = annualMapIter.next();
							if(!"TOT".equals(annualEntry.getKey())){
								Map annualMonthMap = (Map)annualEntry.getValue();
								finalUnitMap.put("LEAN",annualMonthMap.get("TOT"));
								finalUnitMap.put("leanDays",unitdays);
							}
						}
					}
				}
			}
		}
		if(UtilValidate.isNotEmpty(unitT)){
			facilityTotalsFlush = unitT.get(unitId);
			if(facilityTotalsFlush != null){
				Iterator unitMapIterFlush = facilityTotalsFlush.entrySet().iterator();
				while(unitMapIterFlush.hasNext()){
					Map.Entry entryFlush = unitMapIterFlush.next();
					if(!"TOT".equals(entryFlush.getKey())){
						Map annualValuesMapFlush = (Map)entryFlush.getValue();
						Iterator annualMapIterFlush = annualValuesMapFlush.entrySet().iterator();
						while(annualMapIterFlush.hasNext()){
							Map.Entry annualEntryFlush = annualMapIterFlush.next();
							if(!"TOT".equals(annualEntryFlush.getKey())){
								Map annualMonthMapFlush = (Map)annualEntryFlush.getValue();
								finalUnitMap.put("FLUSH", annualMonthMapFlush.get("TOT"));
								finalUnitMap.put("flushDays",unitFlushdays);
							}
						}
					}
					if("TOT".equals(entryFlush.getKey())){
						 totalFlushMap = (Map)entryFlush.getValue();
					}   
				}
			}
		}
		if(UtilValidate.isNotEmpty(finalUnitMap)){
		finalMap.put(unitId,finalUnitMap);
		}
	}
}

context.put("finalMap", finalMap);












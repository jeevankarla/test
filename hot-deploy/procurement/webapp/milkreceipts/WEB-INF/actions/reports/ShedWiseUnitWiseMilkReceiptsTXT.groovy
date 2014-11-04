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

import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

if(UtilValidate.isEmpty(fromDate)){
	Debug.logError("fromDate Cannot Be Empty","");
	context.errorMessage = "FromDate Cannot Be Empty.......!";
	return;
}
if(UtilValidate.isEmpty(thruDate)){
	Debug.logError("thruDate Cannot Be Empty","");
	context.errorMessage = "ThruDate Cannot Be Empty.......!";
	return;
}
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (fromDate) {
		fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
	}
	if (thruDate) {
		thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("fromDate", fromDateStart);
context.putAt("thruDate", thruDateEnd);
dctx = dispatcher.getDispatchContext();

shedWiseTotalsMap = [:];
shedList = ProcurementNetworkServices.getSheds(delegator);
mccTypeIdsList= UtilMisc.toList("UNION","OTHERS");
shedList.each{ shed->
	shedTotalMap=[:];
	finalUnitMap = [:];
	totQtyLtrs=0;
	totQtyKgs=0;
	totKgFat=0;
	totKgSnf=0;
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: shed.facilityId]);
 	unitsList = shedUnitDetails.get("unitsList");
 	unitsList.each{ unit->
		unitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: unit]);
		unitDetails = delegator.findOne("Facility", [facilityId : unit], false);
		if(UtilValidate.isNotEmpty(unitDetails)){
			mccTypeId= unitDetails.get("mccTypeId");
			if(UtilValidate.isNotEmpty(mccTypeId) && (!mccTypeIdsList.contains(mccTypeId))){
				if(UtilValidate.isNotEmpty(unitTotals)){
					facilityTotals = unitTotals.get(unit);
					if(UtilValidate.isNotEmpty(facilityTotals)){
						dayTotals = facilityTotals.get("dayTotals");
						if(UtilValidate.isNotEmpty(dayTotals)){
							Iterator dayTotIter = dayTotals.entrySet().iterator();
							while(dayTotIter.hasNext()){
								Map.Entry entry = dayTotIter.next();
								if("TOT".equals(entry.getKey())){
									unitTotalMap = [:];
									unitTotalMap["qtyLtrs"] = 0;
									unitTotalMap["qtyKgs"] = 0;
									unitTotalMap["kgFat"] = 0;
									unitTotalMap["kgSnf"] = 0;
									unitTotalMap["fat"] = 0;
									unitTotalMap["snf"] = 0;
									unitTotalMap.put("qtyLtrs", entry.getValue().get("TOT").get("recdQtyLtrs"));
									unitTotalMap.put("qtyKgs", entry.getValue().get("TOT").get("recdQtyKgs"));
									unitTotalMap.put("kgFat", entry.getValue().get("TOT").get("recdKgFat"));
									unitTotalMap.put("kgSnf", entry.getValue().get("TOT").get("recdKgSnf"));
									unitTotalMap.put("fat", entry.getValue().get("TOT").get("receivedFat"));
									unitTotalMap.put("snf", entry.getValue().get("TOT").get("receivedSnf"));
									totQtyLtrs = totQtyLtrs+entry.getValue().get("TOT").get("recdQtyLtrs");
									totQtyKgs = totQtyKgs+entry.getValue().get("TOT").get("recdQtyKgs");
									totKgFat = totKgFat+entry.getValue().get("TOT").get("recdKgFat");
									totKgSnf = totKgSnf+entry.getValue().get("TOT").get("recdKgSnf");
									finalUnitMap.put(unit, unitTotalMap);
								}
							}
						}
					}
				}
			}
		}
	}
	 shedTotalMap.put("totQtyLtrs", totQtyLtrs);
	 shedTotalMap.put("totQtyKgs", totQtyKgs);
	 shedTotalMap.put("totKgFat", totKgFat);
	 shedTotalMap.put("totKgSnf", totKgSnf);
	 finalUnitMap.put("TOTAL", shedTotalMap);
	 shedWiseTotalsMap.put(shed.facilityId, finalUnitMap);
}
context.putAt("shedWiseTotalsMap", shedWiseTotalsMap);

unionShedWiseTotalsMap = [:];
conditionList =[];
conditionList.add(EntityCondition.makeCondition("mccTypeId", EntityOperator.IN , UtilMisc.toList("UNION")));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "SHED"));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unionShedList = delegator.findList("Facility",condition,null,null,null,false);
unionShedList.each{ shed->
	unionShedTotalMap=[:];
	unionFinalUnitMap = [:];
	totQtyLtrs=0;
	totQtyKgs=0;
	totKgFat=0;
	totKgSnf=0;
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: shed.facilityId]);
	 unitsList = shedUnitDetails.get("unitsList");
	 unitsList.each{ unit->
		unitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: unit]);
		unitDetails = delegator.findOne("Facility", [facilityId : unit], false);
		if(UtilValidate.isNotEmpty(unitDetails)){
			mccTypeId= unitDetails.get("mccTypeId");
			if(UtilValidate.isNotEmpty(mccTypeId) && (mccTypeIdsList.contains(mccTypeId))){
				if(UtilValidate.isNotEmpty(unitTotals)){
					facilityTotals = unitTotals.get(unit);
					if(UtilValidate.isNotEmpty(facilityTotals)){
						dayTotals = facilityTotals.get("dayTotals");
						if(UtilValidate.isNotEmpty(dayTotals)){
							Iterator dayTotIter = dayTotals.entrySet().iterator();
							while(dayTotIter.hasNext()){
								Map.Entry entry = dayTotIter.next();
								if("TOT".equals(entry.getKey())){
									unionUnitTotalMap = [:];
									unionUnitTotalMap["qtyLtrs"] = 0;
									unionUnitTotalMap["qtyKgs"] = 0;
									unionUnitTotalMap["kgFat"] = 0;
									unionUnitTotalMap["kgSnf"] = 0;
									unionUnitTotalMap["fat"] = 0;
									unionUnitTotalMap["snf"] = 0;
									unionUnitTotalMap.put("qtyLtrs", entry.getValue().get("TOT").get("recdQtyLtrs"));
									unionUnitTotalMap.put("qtyKgs", entry.getValue().get("TOT").get("recdQtyKgs"));
									unionUnitTotalMap.put("kgFat", entry.getValue().get("TOT").get("recdKgFat"));
									unionUnitTotalMap.put("kgSnf", entry.getValue().get("TOT").get("recdKgSnf"));
									unionUnitTotalMap.put("fat", entry.getValue().get("TOT").get("receivedFat"));
									unionUnitTotalMap.put("snf", entry.getValue().get("TOT").get("receivedSnf"));
									totQtyLtrs = totQtyLtrs+entry.getValue().get("TOT").get("recdQtyLtrs");
									totQtyKgs = totQtyKgs+entry.getValue().get("TOT").get("recdQtyKgs");
									totKgFat = totKgFat+entry.getValue().get("TOT").get("recdKgFat");
									totKgSnf = totKgSnf+entry.getValue().get("TOT").get("recdKgSnf");
									unionFinalUnitMap.put(unit, unionUnitTotalMap);
								}
							}
						}
					}
				}
			}
		}
	}
	 unionShedTotalMap.put("totQtyLtrs", totQtyLtrs);
	 unionShedTotalMap.put("totQtyKgs", totQtyKgs);
	 unionShedTotalMap.put("totKgFat", totKgFat);
	 unionShedTotalMap.put("totKgSnf", totKgSnf);
	 unionFinalUnitMap.put("TOTAL", unionShedTotalMap);
	 unionShedWiseTotalsMap.put(shed.facilityId, unionFinalUnitMap);
}
context.putAt("unionShedWiseTotalsMap", unionShedWiseTotalsMap);

othersShedWiseTotalsMap = [:];
othersconditionList =[];
othersconditionList.add(EntityCondition.makeCondition("mccTypeId", EntityOperator.IN , UtilMisc.toList("OTHERS")));
othersconditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "SHED"));
EntityCondition othersCondition = EntityCondition.makeCondition(othersconditionList,EntityOperator.AND);
othersShedList = delegator.findList("Facility",othersCondition,null,null,null,false);
othersShedList.each{ shed->
	othersShedTotalMap=[:];
	othersFinalUnitMap = [:];
	totQtyLtrs=0;
	totQtyKgs=0;
	totKgFat=0;
	totKgSnf=0;
	shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: shed.facilityId]);
	 unitsList = shedUnitDetails.get("unitsList");
	 unitsList.each{ unit->
		unitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: unit]);
		unitDetails = delegator.findOne("Facility", [facilityId : unit], false);
		if(UtilValidate.isNotEmpty(unitDetails)){
			mccTypeId= unitDetails.get("mccTypeId");
			if(UtilValidate.isNotEmpty(mccTypeId) && (mccTypeIdsList.contains(mccTypeId))){
				if(UtilValidate.isNotEmpty(unitTotals)){
					facilityTotals = unitTotals.get(unit);
					if(UtilValidate.isNotEmpty(facilityTotals)){
						dayTotals = facilityTotals.get("dayTotals");
						if(UtilValidate.isNotEmpty(dayTotals)){
							Iterator dayTotIter = dayTotals.entrySet().iterator();
							while(dayTotIter.hasNext()){
								Map.Entry entry = dayTotIter.next();
								if("TOT".equals(entry.getKey())){
									othersUnitTotalMap = [:];
									othersUnitTotalMap["qtyLtrs"] = 0;
									othersUnitTotalMap["qtyKgs"] = 0;
									othersUnitTotalMap["kgFat"] = 0;
									othersUnitTotalMap["kgSnf"] = 0;
									othersUnitTotalMap["fat"] = 0;
									othersUnitTotalMap["snf"] = 0;
									othersUnitTotalMap.put("qtyLtrs", entry.getValue().get("TOT").get("recdQtyLtrs"));
									othersUnitTotalMap.put("qtyKgs", entry.getValue().get("TOT").get("recdQtyKgs"));
									othersUnitTotalMap.put("kgFat", entry.getValue().get("TOT").get("recdKgFat"));
									othersUnitTotalMap.put("kgSnf", entry.getValue().get("TOT").get("recdKgSnf"));
									othersUnitTotalMap.put("fat", entry.getValue().get("TOT").get("receivedFat"));
									othersUnitTotalMap.put("snf", entry.getValue().get("TOT").get("receivedSnf"));
									totQtyLtrs = totQtyLtrs+entry.getValue().get("TOT").get("recdQtyLtrs");
									totQtyKgs = totQtyKgs+entry.getValue().get("TOT").get("recdQtyKgs");
									totKgFat = totKgFat+entry.getValue().get("TOT").get("recdKgFat");
									totKgSnf = totKgSnf+entry.getValue().get("TOT").get("recdKgSnf");
									othersFinalUnitMap.put(unit, othersUnitTotalMap);
								}
							}
						}
					}
				}
			}
		}
	}
	 othersShedTotalMap.put("totQtyLtrs", totQtyLtrs);
	 othersShedTotalMap.put("totQtyKgs", totQtyKgs);
	 othersShedTotalMap.put("totKgFat", totKgFat);
	 othersShedTotalMap.put("totKgSnf", totKgSnf);
	 othersFinalUnitMap.put("TOTAL", othersShedTotalMap);
	 othersShedWiseTotalsMap.put(shed.facilityId, othersFinalUnitMap);
}
context.putAt("othersShedWiseTotalsMap", othersShedWiseTotalsMap);





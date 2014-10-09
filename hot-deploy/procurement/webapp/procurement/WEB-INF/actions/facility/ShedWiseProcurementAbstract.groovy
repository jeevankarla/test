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

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
conditionList =[];
unitTotalsMap =[:];
finalUnitTotMap=[:];
if(UtilValidate.isEmpty(parameters.shedId)){
	return;
}
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;
//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"UNIT")));
/*condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);*/
unitsListSorted = [];

shedUnits = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);
//shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : parameters.shedId]);
unitsListSorted = shedUnits.customTimePeriodUnitsDetailList;
shedTotalsMap = [:];
for(procProduct in procurementProductList){
	shedTotalsMap[procProduct.brandName+"QtyKgs"] = 0;
	shedTotalsMap[procProduct.brandName+"QtyLtrs"] = 0;
	shedTotalsMap[procProduct.brandName+"kgFat"] = 0;
	shedTotalsMap[procProduct.brandName+"kgSnf"] = 0;
	shedTotalsMap[procProduct.brandName+"Solids"] = 0;
}
shedTotalsMap["totMixQtyKgs"] = 0;
shedTotalsMap["totMixQtyLtrs"] = 0;
shedTotalsMap["totMixKgFat"] = 0;
shedTotalsMap["totMixKgSnf"] = 0;
shedTotalsMap["totMixSolids"] = 0;
shedTotalsMap["totMixPtcCurd"] = 0;
shedTotalsMap["totMixCurdLtrs"] = 0;
unitsListSorted.each{ unit ->	
	unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: parameters.customTimePeriodId , unitId: unit.facilityId]);
	
	if(UtilValidate.isNotEmpty(unitBillAbstract)){
	unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
	unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
	unitWiseDetailsMap=[:];
		if(UtilValidate.isNotEmpty(unitGrndValuesTot)){		
			for(procProduct in procurementProductList){
				if(UtilValidate.isNotEmpty(unitGrndValuesTot.getAt(procProduct.productId))){
					productAbs = unitGrndValuesTot.getAt(procProduct.productId);
							
					qtyKgs= productAbs.get("qtyKgs");
					kgFat= productAbs.get("kgFat");
					kgSnf= productAbs.get("kgSnf");
					unitWiseDetailsMap[procProduct.brandName+"QtyKgs"] = qtyKgs;
					unitWiseDetailsMap[procProduct.brandName+"QtyLtrs"] = productAbs.get("qtyLtrs");
					unitWiseDetailsMap[procProduct.brandName+"kgFat"] = productAbs.get("kgFat");
					unitWiseDetailsMap[procProduct.brandName+"kgSnf"] = productAbs.get("kgSnf");
					unitWiseDetailsMap[procProduct.brandName+"Solids"] =productAbs.get("solids");
				}
			}
			totProductMap = unitGrndValuesTot.get("TOT");
			unitWiseDetailsMap["totQtyKgs"] = totProductMap.get("qtyKgs");
			unitWiseDetailsMap["totQtyLtrs"] = totProductMap.get("qtyLtrs");
			unitWiseDetailsMap["totKgFat"] = totProductMap.get("kgFat");
			unitWiseDetailsMap["totKgSnf"] = totProductMap.get("kgSnf");
			unitWiseDetailsMap["totSolids"] = totProductMap.get("solids");
			unitWiseDetailsMap["ptcCurdQty"] = totProductMap.get("ptcCurd");
			unitWiseDetailsMap["curdQtyLtrs"] = totProductMap.get("cQtyLtrs");
		}
		if(UtilValidate.isNotEmpty(unitWiseDetailsMap)){
			finalUnitTotMap.put(unit.facilityId,unitWiseDetailsMap);
			for(procProduct in procurementProductList){
				shedTotalsMap[procProduct.brandName+"QtyKgs"] +=  (unitWiseDetailsMap.get(procProduct.brandName+"QtyKgs"));
				shedTotalsMap[procProduct.brandName+"QtyLtrs"] += (unitWiseDetailsMap.get(procProduct.brandName+"QtyLtrs"));
				shedTotalsMap[procProduct.brandName+"kgFat"] += (unitWiseDetailsMap.get(procProduct.brandName+"kgFat"));
				shedTotalsMap[procProduct.brandName+"kgSnf"] += (unitWiseDetailsMap.get(procProduct.brandName+"kgSnf"));
				shedTotalsMap[procProduct.brandName+"Solids"] =(unitWiseDetailsMap.get(procProduct.brandName+"Solids"));;
			}
			shedTotalsMap["totMixQtyKgs"] +=unitWiseDetailsMap.get("totQtyKgs");
			shedTotalsMap["totMixQtyLtrs"] += unitWiseDetailsMap.get("totQtyLtrs");
			shedTotalsMap["totMixKgFat"] += unitWiseDetailsMap.get("totKgFat");
			shedTotalsMap["totMixKgSnf"] += unitWiseDetailsMap.get("totKgSnf");
			shedTotalsMap["totMixSolids"] += unitWiseDetailsMap.get("totSolids");
			shedTotalsMap["totMixPtcCurd"] += unitWiseDetailsMap.get("ptcCurdQty");
			shedTotalsMap["totMixCurdLtrs"] += unitWiseDetailsMap.get("curdQtyLtrs");
			
		}
	}
	
}
context.putAt("finalUnitTotMap", finalUnitTotMap);
context.putAt("shedTotalsMap", shedTotalsMap);	

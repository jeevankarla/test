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

districtWiseProcList = [];

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
fromDate = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
thruDate = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);
context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

maxIntervalDays=UtilDateTime.getIntervalInDays(fromDate,thruDate)+1;

if(maxIntervalDays > 16){
	Debug.logError("You Cannot Choose More Than 16 Days.","");
	context.errorMessage = "You Cannot Choose More Than 16 Days";
	return;
}
context.putAt("maxIntervalDays",maxIntervalDays);

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

allUnitsList = [];
dctx = dispatcher.getDispatchContext();
	facility =delegator.findList("Facility", EntityCondition.makeCondition([facilityTypeId : "UNIT"]), null, null ,null, false);
	distList = EntityUtil.getFieldListFromEntityList(facility, "district", true);
	distDetailsMap = [:];
	totProdMap = [:];
	totProdMap["TOT"+"QtyKgs"] = 0;
	totProdMap["TOT"+"QtyLtrs"] = 0;
	totProdMap["TOT"+"kgFat"] = 0;
	totProdMap["TOT"+"kgSnf"] = 0;
	totProdMap["TOT"+"netAmt"] = 0;
	totProdMap["TOT"+"opCost"] = 0;
	totProdMap["TOT"+"solids"] = 0;
	totProdMap["TOT"+"tipAmt"] = 0;
	
	for(procProduct in procurementProductList){
		totProdMap[procProduct.brandName+"QtyKgs"] = 0;
		totProdMap[procProduct.brandName+"QtyLtrs"] = 0;
		totProdMap[procProduct.brandName+"kgFat"] = 0;
		totProdMap[procProduct.brandName+"kgSnf"] = 0;
		totProdMap[procProduct.brandName+"netAmt"] = 0;
		totProdMap[procProduct.brandName+"solids"] = 0;
		totProdMap[procProduct.brandName+"tipAmt"] = 0;
	}
	totProdMap["opCost"] = 0;
	
	distList.each{ dist ->
			prodMap = [:];
			for(procProduct in procurementProductList){
				prodMap[procProduct.brandName+"QtyKgs"] = 0;
				prodMap[procProduct.brandName+"QtyLtrs"] = 0;
				prodMap[procProduct.brandName+"kgFat"] = 0;
				prodMap[procProduct.brandName+"kgSnf"] = 0;
				prodMap[procProduct.brandName+"netAmt"] = 0;
				prodMap[procProduct.brandName+"tipAmt"] = 0;
				if(procProduct.brandName == "CM"){
					prodMap[procProduct.brandName+"solids"] = 0;
				}
				
				prodMap["TOT"+"QtyKgs"] = 0;
				prodMap["TOT"+"QtyLtrs"] = 0;
				prodMap["TOT"+"kgFat"] = 0;
				prodMap["TOT"+"kgSnf"] = 0;
				prodMap["TOT"+"netAmt"] = 0;
				prodMap["TOT"+"opCost"] = 0;
				prodMap["TOT"+"tipAmt"] = 0;
			}
			prodMap["opCost"] = 0;
			
			unitsList = EntityUtil.filterByAnd(facility, [EntityCondition.makeCondition("district", EntityOperator.EQUALS, dist)]);
			
			unitsList.each{ unit ->
				unitBillAbstract = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate,facilityId: unit.facilityId]);
				if(UtilValidate.isNotEmpty(unitBillAbstract)){
					Map	centerWiseAbsMap = FastMap.newInstance();
					if(UtilValidate.isNotEmpty(unitBillAbstract.get(unit.facilityId))){
					centerWiseAbsMap = (unitBillAbstract.get(unit.facilityId)).get("TOT");
					if(UtilValidate.isNotEmpty(centerWiseAbsMap)){
						for(procProduct in procurementProductList){
							brandName = procProduct.get("brandName");
							prodProdDetailsMap = [:];
							prodProdDetailsMap = centerWiseAbsMap.get(procProduct.productId);
							prodMap[brandName+"QtyKgs"] += prodProdDetailsMap.get("qtyKgs");
							prodMap[brandName+"QtyLtrs"] += prodProdDetailsMap.get("qtyLtrs");
							prodMap[brandName+"kgFat"] += prodProdDetailsMap.get("kgFat");
							prodMap[brandName+"kgSnf"] += prodProdDetailsMap.get("kgSnf");
							prodMap[brandName+"netAmt"] += prodProdDetailsMap.get("price");
							prodMap[brandName+"tipAmt"] += prodProdDetailsMap.get("tipAmt");
							if(brandName == "CM"){
								prodMap[brandName+"solids"] += (prodProdDetailsMap.get("kgFat")+prodProdDetailsMap.get("kgSnf"));
							}
							totProdMap[brandName+"QtyKgs"] += prodProdDetailsMap.get("qtyKgs");
							totProdMap[brandName+"QtyLtrs"] += prodProdDetailsMap.get("qtyLtrs");
							totProdMap[brandName+"kgFat"] += prodProdDetailsMap.get("kgFat");
							totProdMap[brandName+"kgSnf"] += prodProdDetailsMap.get("kgSnf");
							totProdMap[brandName+"netAmt"] += prodProdDetailsMap.get("price");
							totProdMap[brandName+"tipAmt"] += prodProdDetailsMap.get("tipAmt");
							if(brandName == "CM"){
								totProdMap[brandName+"solids"] += (prodProdDetailsMap.get("kgFat")+prodProdDetailsMap.get("kgSnf"));
							}
						}
						prodProdDetailsMap = [:];
						prodProdDetailsMap = centerWiseAbsMap.get("TOT");
						
						prodMap["TOTQtyKgs"] += prodProdDetailsMap.get("qtyKgs");
						prodMap["TOTQtyLtrs"] += prodProdDetailsMap.get("qtyLtrs");
						prodMap["TOTkgFat"] += prodProdDetailsMap.get("kgFat");
						prodMap["TOTkgSnf"] += prodProdDetailsMap.get("kgSnf");
						prodMap["TOTnetAmt"] += prodProdDetailsMap.get("price");
						prodMap["TOTtipAmt"] += prodProdDetailsMap.get("tipAmt");
						prodMap["TOTopCost"] += prodProdDetailsMap.get("opCost");
						
						totProdMap["TOTQtyKgs"] += prodProdDetailsMap.get("qtyKgs");
						totProdMap["TOTQtyLtrs"] += prodProdDetailsMap.get("qtyLtrs");
						totProdMap["TOTkgFat"] += prodProdDetailsMap.get("kgFat");
						totProdMap["TOTkgSnf"] += prodProdDetailsMap.get("kgSnf");
						totProdMap["TOTnetAmt"] += prodProdDetailsMap.get("price");
						totProdMap["TOTtipAmt"] += prodProdDetailsMap.get("tipAmt");
						totProdMap["TOTopCost"] += prodProdDetailsMap.get("opCost");
						
						}
					}
				}
			}
			tempDistMap = [:];
			tempDistMap.putAll(prodMap);
			
			distDetailsMap.put(dist, tempDistMap);
		}
			
	tempTotDistMap = [:];
	tempTotDistMap.putAll(totProdMap);
	
	distDetailsMap.put("GrandTotals", tempTotDistMap);
	context.putAt("distDetailsMap",distDetailsMap);

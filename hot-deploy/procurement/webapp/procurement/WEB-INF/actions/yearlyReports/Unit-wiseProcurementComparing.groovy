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
	Debug.logError("shedId Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.lastYearId;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.currentYearId;
}

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.lastYearId], false);
prevYearfromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
prevYearthruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.currentYearId], false);
CurrYearfromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
CurrYearthruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("prevYearfromDate", prevYearfromDate);
context.put("prevYearthruDate", prevYearthruDate);
context.put("CurrYearfromDate", CurrYearfromDate);
context.put("CurrYearthruDate", CurrYearthruDate);


PrevTotalDays=UtilDateTime.getIntervalInDays(prevYearfromDate,prevYearthruDate);
context.put("PrevTotalDays", PrevTotalDays+1);
CurrTotalDays=UtilDateTime.getIntervalInDays(CurrYearfromDate,CurrYearthruDate);
context.put("CurrTotalDays", CurrTotalDays+1);
dctx = dispatcher.getDispatchContext();

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.put("procurementProductList", procurementProductList);
for(product in procurementProductList){
	productId=product.productId;
}
Map initMap = FastMap.newInstance();
initMap.put("qtyLtrs", BigDecimal.ZERO);
initMap.put("qtyKgs", BigDecimal.ZERO);
initMap.put("fat", BigDecimal.ZERO);
initMap.put("snf", BigDecimal.ZERO);
initMap.put("kgFat", BigDecimal.ZERO);
initMap.put("kgSnf", BigDecimal.ZERO);
Map grandTotPrevProductMap = FastMap.newInstance();
Map prodInitMap = FastMap.newInstance();
Map tempUnitMap = FastMap.newInstance();
for(product in procurementProductList){
	tempInitMap = [:];
	tempInitMap.putAll(initMap);
	prodInitMap.put(product.get("productId"), tempInitMap);
}
prodInitMap.put("TOT",tempInitMap );
grandTotPrevProductMap.putAll(prodInitMap);
Map previousYearMap = FastMap.newInstance();
shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[fromDate: prevYearfromDate , thruDate: prevYearthruDate,userLogin: userLogin,shedId: parameters.shedId]);
if(UtilValidate.isNotEmpty(shedUnitDetails)){
	unitList = shedUnitDetails.get("unitsList");
	Map productWisePreMap = FastMap.newInstance();
	unitList.each{ unitId->
	if((UtilValidate.isNotEmpty(previousYearMap)&& UtilValidate.isEmpty(previousYearMap.get(unitId)))||UtilValidate.isEmpty(previousYearMap)){
		previousYearMap.put(unitId, prodInitMap);
	}
	prevUnitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: prevYearfromDate , thruDate: prevYearthruDate,userLogin: userLogin,facilityId: unitId]);
		if(UtilValidate.isNotEmpty(prevUnitTotals)){
			Map unitDetails = prevUnitTotals.get(unitId);
			if(UtilValidate.isNotEmpty(unitDetails)){
				Map totalDetails = unitDetails.get("TOT");
				if(UtilValidate.isNotEmpty(totalDetails)){
					Map tempUnitProductValues = FastMap.newInstance();
					tempUnitProductValues.putAll(previousYearMap.get(unitId));
					for(prodKey in tempUnitProductValues.keySet()){
						Map tempQtyMap = FastMap.newInstance();
						tempQtyMap.putAll(tempUnitProductValues.get(prodKey));
						roundedQty = (totalDetails.get(prodKey).get("qtyLtrs")).setScale(1,BigDecimal.ROUND_HALF_UP);
						roundedFat = (totalDetails.get(prodKey).get("fat")).setScale(1,BigDecimal.ROUND_HALF_UP);
						roundedSnf = (totalDetails.get(prodKey).get("snf")).setScale(2,BigDecimal.ROUND_HALF_UP);
						tempQtyMap.put("qtyLtrs", tempQtyMap.get("qtyLtrs")+roundedQty);
						tempQtyMap.put("qtyKgs", tempQtyMap.get("qtyKgs")+totalDetails.get(prodKey).get("qtyKgs"));
						tempQtyMap.put("fat", tempQtyMap.get("fat")+roundedFat);
						tempQtyMap.put("snf", tempQtyMap.get("snf")+roundedSnf);
						tempQtyMap.put("kgFat", tempQtyMap.get("kgFat")+totalDetails.get(prodKey).get("kgFat"));
						tempQtyMap.put("kgSnf", tempQtyMap.get("kgSnf")+totalDetails.get(prodKey).get("kgSnf"));
						tempUnitProductValues.put(prodKey, tempQtyMap);
						Map tempGrandTotalProductQtyMap =FastMap.newInstance();
						tempGrandTotalProductQtyMap.putAll(grandTotPrevProductMap.get(prodKey));
						tempGrandTotalProductQtyMap.put("qtyLtrs", tempGrandTotalProductQtyMap.get("qtyLtrs")+roundedQty);
						tempGrandTotalProductQtyMap.put("qtyKgs", tempGrandTotalProductQtyMap.get("qtyKgs")+totalDetails.get(prodKey).get("qtyKgs"));
						tempGrandTotalProductQtyMap.put("fat", tempGrandTotalProductQtyMap.get("fat")+roundedFat);
						tempGrandTotalProductQtyMap.put("snf", tempGrandTotalProductQtyMap.get("snf")+roundedSnf);
						tempGrandTotalProductQtyMap.put("kgFat", tempGrandTotalProductQtyMap.get("kgFat")+totalDetails.get(prodKey).get("kgFat"));
						tempGrandTotalProductQtyMap.put("kgSnf", tempGrandTotalProductQtyMap.get("kgSnf")+totalDetails.get(prodKey).get("kgSnf"));
						grandTotPrevProductMap.putAt(prodKey, tempGrandTotalProductQtyMap);
					}
					previousYearMap.put(unitId, tempUnitProductValues);
				}
			}
		}
	}
}
context.put("previousYearMap",previousYearMap);
context.put("grandTotPrevProductMap",grandTotPrevProductMap);

Map grandTotCurrProductMap = FastMap.newInstance();
grandTotCurrProductMap.putAll(prodInitMap);
Map currentYearMap = FastMap.newInstance();
Map productWiseCurrMap = FastMap.newInstance();
unitList.each{ unitId->
if((UtilValidate.isNotEmpty(currentYearMap)&& UtilValidate.isEmpty(currentYearMap.get(unitId)))||UtilValidate.isEmpty(currentYearMap)){
	currentYearMap.put(unitId, prodInitMap);
}
currUnitTotals = ProcurementReports.getAnnualPeriodTotals(dctx , [fromDate: CurrYearfromDate , thruDate: CurrYearthruDate,userLogin: userLogin,facilityId: unitId]);
	if(UtilValidate.isNotEmpty(currUnitTotals)){
		Map unitCurrDetails = currUnitTotals.get(unitId);
		if(UtilValidate.isNotEmpty(unitCurrDetails)){
			Map totalCurrDetails = unitCurrDetails.get("TOT");
			if(UtilValidate.isNotEmpty(totalCurrDetails)){
				Map tempUnitCurrProductValues = FastMap.newInstance();
				tempUnitCurrProductValues.putAll(currentYearMap.get(unitId));
				for(prodKey in tempUnitCurrProductValues.keySet()){
					Map tempCurrQtyMap = FastMap.newInstance();
					tempCurrQtyMap.putAll(tempUnitCurrProductValues.get(prodKey));
					roundedCurrQty = (totalCurrDetails.get(prodKey).get("qtyLtrs")).setScale(1,BigDecimal.ROUND_HALF_UP);
					roundedCurrFat = (totalCurrDetails.get(prodKey).get("fat")).setScale(1,BigDecimal.ROUND_HALF_UP);
					roundedCurrSnf = (totalCurrDetails.get(prodKey).get("snf")).setScale(2,BigDecimal.ROUND_HALF_UP);
					tempCurrQtyMap.put("qtyLtrs", tempCurrQtyMap.get("qtyLtrs")+roundedCurrQty);
					tempCurrQtyMap.put("qtyKgs", tempCurrQtyMap.get("qtyKgs")+totalCurrDetails.get(prodKey).get("qtyKgs"));
					tempCurrQtyMap.put("fat", tempCurrQtyMap.get("fat")+roundedCurrFat);
					tempCurrQtyMap.put("snf", tempCurrQtyMap.get("snf")+roundedCurrSnf);
					tempCurrQtyMap.put("kgFat", tempCurrQtyMap.get("kgFat")+totalCurrDetails.get(prodKey).get("kgFat"));
					tempCurrQtyMap.put("kgSnf", tempCurrQtyMap.get("kgSnf")+totalCurrDetails.get(prodKey).get("kgSnf"));
					tempUnitCurrProductValues.put(prodKey, tempCurrQtyMap);
					Map tempGrandTotCurrProductMap =FastMap.newInstance();
					tempGrandTotCurrProductMap.putAll(grandTotCurrProductMap.get(prodKey));
					tempGrandTotCurrProductMap.put("qtyLtrs", tempGrandTotCurrProductMap.get("qtyLtrs")+roundedCurrQty);
					tempGrandTotCurrProductMap.put("qtyKgs", tempGrandTotCurrProductMap.get("qtyKgs")+totalCurrDetails.get(prodKey).get("qtyKgs"));
					tempGrandTotCurrProductMap.put("fat", tempGrandTotCurrProductMap.get("fat")+roundedCurrFat);
					tempGrandTotCurrProductMap.put("snf", tempGrandTotCurrProductMap.get("snf")+roundedCurrSnf);
					tempGrandTotCurrProductMap.put("kgFat", tempGrandTotCurrProductMap.get("kgFat")+totalCurrDetails.get(prodKey).get("kgFat"));
					tempGrandTotCurrProductMap.put("kgSnf", tempGrandTotCurrProductMap.get("kgSnf")+totalCurrDetails.get(prodKey).get("kgSnf"));
					grandTotCurrProductMap.putAt(prodKey, tempGrandTotCurrProductMap);
				}
				currentYearMap.put(unitId, tempUnitCurrProductValues);
			}
		}
	}
}
currentGrndTotQty = grandTotCurrProductMap.get("TOT").get("qtyLtrs");
previousGrndTotQty = grandTotPrevProductMap.get("TOT").get("qtyLtrs");
if ((previousGrndTotQty == 0) && (currentGrndTotQty == 0)) {
	context.errorMessage = "data does not exist!";
	return;
}
context.put("currentYearMap",currentYearMap);
context.put("grandTotCurrProductMap",grandTotCurrProductMap);


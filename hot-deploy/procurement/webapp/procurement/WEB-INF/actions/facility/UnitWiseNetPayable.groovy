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
import in.vasista.vbiz.procurement.ProcurementServices;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
// getting DD amount 
ddAmountMap =[:];
if(UtilValidate.isNotEmpty(context.get("shedWiseTotalsMap"))){
	
	shedWiseTotalsMap = context.get("shedWiseTotalsMap");
	tipAmount = shedWiseTotalsMap.get("tipAmount");
	totalDeductions = shedWiseTotalsMap.get("DednsTot");
	feedDeductions= shedWiseTotalsMap.get("MILKPROC_FEEDDED");
	totalDDAmount = tipAmount + (totalDeductions-feedDeductions);
	shedId= parameters.shedId;
	customTimePeriodId = parameters.customTimePeriodId;
	ddAccDetailsMap = [:];
	shedDDAccountMap = ProcurementReports.getShedDDAccount(dctx, [userLogin:userLogin,facilityId:shedId,customTimePeriodId:customTimePeriodId]);
	if(ServiceUtil.isSuccess(shedDDAccountMap)){
		ddAccDetailsMap = shedDDAccountMap.get("ddAccDetailsMap");
		ddAmountMap.putAll(ddAccDetailsMap);
		ddAmountMap["unitCode"] =ddAccDetailsMap.partyId;
		ddAmountMap["amount"]=totalDDAmount;
		}
	
}
context.putAt("ddAmountMap", ddAmountMap);
if(UtilValidate.isNotEmpty(shedWiseAmountAbstractMap)){
	context.shedWiseAmountAbstractMap=shedWiseAmountAbstractMap;
}
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
inputRateAmt.put("rateTypeId", "GHEE_YEILD_RATE");
inputRateAmt.put("periodTypeId", "RATE_HOUR");
inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));
rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
gheeRate = rateAmount.rateAmount;
context.putAt("gheeRate", gheeRate);


conditionList =[];
Map additionDeductionMap =[:];
unitTotalsMap =[:];
if(parameters.shedId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, parameters.shedId)));
}
//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"UNIT")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);
unitWiseAdjustmentMap =[:]
unitsList.each{ unit ->
	unitTotals = ProcurementReports.getPeriodTransferTotals(dctx , [userLogin: userLogin,fromDate: fromDate , thruDate: thruDate , facilityId: unit.facilityId]);
	if(UtilValidate.isNotEmpty(unitTotals)){
		unitTotalsMap[unit.facilityId] =[:];
		unitTotalsMap[unit.facilityId].putAll(unitTotals);
	}
	unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityId: unit.facilityId]);
	totAdditions=0;
	totDeductions=0;
	adjustmentsMap=[:];
	adjustmentsMap["totAdditions"]=0;
	adjustmentsMap["totDeductions"]=0;
	if(UtilValidate.isNotEmpty(unitAdjustments)){
		adjustmentsTypeValues = unitAdjustments.get("adjustmentsTypeMap");
		if(adjustmentsTypeValues !=null){
			adjustmentsTypeValues.each{ adjustmentValues ->
				if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
					additionsList = adjustmentValues.getValue();
					additionsList.each{ additionValues ->
						totAdditions += additionValues.getValue();
					}
				}else{					
						deductionsList = adjustmentValues.getValue();
						deductionsList.each{ deductionValues ->
						totDeductions += deductionValues.getValue();
					}
				}
			}
		}
		adjustmentsMap.put("totAdditions",totAdditions);
		adjustmentsMap.put("totDeductions",totDeductions);		
		unitWiseAdjustmentMap[unit.facilityId]=[:];
		unitWiseAdjustmentMap[unit.facilityId].putAll(adjustmentsMap);
	}
}	
Iterator unitTotMap = unitTotalsMap.entrySet().iterator();
UnitWiseTotMap =[:];
while (unitTotMap.hasNext()) {
	UnitDetailsMap =[:]
	totQtyLtrs = BigDecimal.ZERO;
	milkValue = BigDecimal.ZERO;
	sourQty =BigDecimal.ZERO;
	gheeYeild =BigDecimal.ZERO;
	additions =BigDecimal.ZERO;
	deductions=BigDecimal.ZERO;
	UnitDetailsMap["totQtyLtrs"]=BigDecimal.ZERO;
	UnitDetailsMap["milkValue"]=BigDecimal.ZERO;
	Map.Entry entry = unitTotMap.next();
	Map UnitTotalsValue = (Map)entry.getValue();
	if(UtilValidate.isNotEmpty(UnitTotalsValue.get("periodTransferTotalsMap"))){
		transfers = UnitTotalsValue.get("periodTransferTotalsMap").get(entry.getKey()).get("transfers");
		if(UtilValidate.isNotEmpty(transfers)){
			periodTotals = transfers.get("procurementPeriodTotals");
			shortages = transfers.get("shortages");
			dayTotals = transfers.get("procurementPeriodTotals").get("dayTotals");
			if(UtilValidate.isNotEmpty(dayTotals)){
				additions =unitWiseAdjustmentMap[entry.getKey()].get("totAdditions");
				deductions =unitWiseAdjustmentMap[entry.getKey()].get("totDeductions");
				totQtyLtrs = (dayTotals.get("TOT").get("qtyLtrs")+dayTotals.get("TOT").get("sQtyLtrs"));
				cartage =0;
				opCost =0;
				if(UtilValidate.isNotEmpty(shedWiseAmountAbstractMap)){
					cartage =shedWiseAmountAbstractMap.get(entry.getKey()).get("cartage");
					opCost =shedWiseAmountAbstractMap.get(entry.getKey()).get("opCost");			
				}
				milkValue = (((dayTotals.get("TOT").get("price"))+dayTotals.get("TOT").get("sPrice")+additions+cartage+opCost)-deductions);
			}			
		}
		if(UtilValidate.isNotEmpty(totQtyLtrs)){
			UnitDetailsMap["totQtyLtrs"]= totQtyLtrs;
		}
		if(UtilValidate.isNotEmpty(milkValue)){
			UnitDetailsMap["milkValue"]=milkValue;
		}
	}
	UnitWiseTotMap.put(entry.getKey(), UnitDetailsMap);
	
}
// for sourAmount calculation
facilityIds = EntityUtil.getFieldListFromEntityList(unitsList, "facilityId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO , thruDate)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.IN , facilityIds)));

condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkTransfers = delegator.findList("MilkTransfer",condition1,null,null,null,false);

avgRate =0;
sQuantity=0;
gheeYeild =0;
sourDistributionMap=[:];
if(UtilValidate.isNotEmpty(milkTransfers)){
	milkTransfers.each{ milkTransfer ->
		unitSourAmt =0;
		milkTransferId = milkTransfer.milkTransferId;
		transferFacilityId =milkTransfer.facilityId;
		sQuantity= milkTransfer.sQuantityLtrs;
		if(UtilValidate.isNotEmpty(milkTransfer.gheeYield)){
			gheeYeild = milkTransfer.gheeYield;
		}
		
		sourDistribution = delegator.findList("MilkTransferAdvice",EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS, milkTransferId)  , null, null, null, false );
		totLtrs =0;
		Amount =0;
		if(UtilValidate.isNotEmpty(sourDistribution)){
			sourDistribution.each{ sourDistb ->					
				facilityId =sourDistb.facilityId;
				ltrs = UnitWiseTotMap[facilityId].get("totQtyLtrs");
				amt = UnitWiseTotMap[facilityId].get("milkValue");
				totLtrs =totLtrs+ltrs;	
				Amount=Amount+amt;
									
			}
		}else{
			totLtrs = UnitWiseTotMap[transferFacilityId].get("totQtyLtrs");
			Amount =  UnitWiseTotMap[transferFacilityId].get("milkValue");
		}			
		avgRate = 0;
		if(UtilValidate.isNotEmpty(Amount) && Amount!=0){
			avgRate = totLtrs/Amount;
		}
			// deductionRate = ((sourAmount - gheeYeildAmt)/totLtrs)
			if(UtilValidate.isEmpty(gheeYeild)){
				gheeYeild =0;
			}
			if(UtilValidate.isEmpty(sQuantity)){
				sQuantity =0;
			}
			deducionRate =0;
			if(UtilValidate.isNotEmpty(totLtrs) && totLtrs!=0){
				deducionRate = (((sQuantity*avgRate) - (gheeYeild *gheeRate))/totLtrs);
			}				
		if(UtilValidate.isNotEmpty(sourDistribution)){	
			sourDistribution.each{ sourFacility ->
				sourDistFacilityId=sourFacility.facilityId;
				facilityQtyltrs = UnitWiseTotMap[sourDistFacilityId].get("totQtyLtrs");
				unitSourAmt = facilityQtyltrs*deducionRate;
				if(UtilValidate.isEmpty(sourDistributionMap[sourDistFacilityId])){
					sourDistributionMap[sourDistFacilityId]=unitSourAmt;
				}else{
					sourDistributionMap[sourDistFacilityId] +=unitSourAmt;
				}				
			}
		}else{
			facilityQtyltrs = UnitWiseTotMap[transferFacilityId].get("totQtyLtrs");
			unitSourAmt = facilityQtyltrs*deducionRate;
			if(UtilValidate.isEmpty(sourDistributionMap[transferFacilityId])){
				sourDistributionMap[transferFacilityId]=unitSourAmt;
			}else{
				sourDistributionMap[transferFacilityId] +=unitSourAmt;
			}
		
		}		
	}
}
context.put("sourDistributionMap", sourDistributionMap);
context.put("unitWiseAdjustmentMap", unitWiseAdjustmentMap);
context.put("unitTotalsMap", unitTotalsMap);


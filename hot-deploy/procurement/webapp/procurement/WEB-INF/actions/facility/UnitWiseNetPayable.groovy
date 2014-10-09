import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
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

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
Timestamp billingDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse("JUL 20, 2014").getTime()));

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

fromDateStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
thruDateEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
String recoveryFromDDAccount = "NO";
if(UtilValidate.isNotEmpty(parameters.recoveryFromDDAccount)){
	recoveryFromDDAccount = parameters.recoveryFromDDAccount;
	}

if(UtilValidate.isNotEmpty(shedWiseAmountAbstractMap)){
	context.shedWiseAmountAbstractMap=shedWiseAmountAbstractMap;
}
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
gheeRate=0;
Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
inputRateAmt.put("rateTypeId", "GHEE_YEILD_RATE");
inputRateAmt.put("periodTypeId", "RATE_HOUR");
inputRateAmt.put("rateCurrencyUomId", context.get("currencyUomId"));
rateAmount = dispatcher.runSync("getRateAmount", inputRateAmt);
if(UtilValidate.isNotEmpty(rateAmount.get("rateAmount"))){
	gheeRate = rateAmount.get("rateAmount");
}
context.putAt("gheeRate", gheeRate);

conditionList =[];
Map additionDeductionMap =[:];
unitTotalsMap =[:];
unitsList = [];
if(parameters.shedId){
	//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, parameters.shedId)));
	//unitsList = (List)(ProcurementNetworkServices.getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",parameters.shedId))).get("unitsDetailList");
	unitsList = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]).get("customTimePeriodUnitsDetailList");
}
//conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"UNIT")));
/*condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);*/
unitWiseAdjustmentMap =[:]
Map netPayableMap = (Map)context.get("netPayableMap");
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
	BigDecimal milkValue = BigDecimal.ZERO;
	sourQty =BigDecimal.ZERO;
	gheeYeild =BigDecimal.ZERO;
	additions =BigDecimal.ZERO;
	deductions=BigDecimal.ZERO;
	UnitDetailsMap["totQtyLtrs"]=BigDecimal.ZERO;
	UnitDetailsMap["milkValue"]=BigDecimal.ZERO;
	Map.Entry entry = unitTotMap.next();
	
	Map UnitTotalsValue = (Map)entry.getValue();
	
	unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: parameters.customTimePeriodId , unitId: entry.getKey()]);
	//netPayableAmt=0;
	if(UtilValidate.isNotEmpty(unitBillAbstract)){
		unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
		unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
		if(UtilValidate.isNotEmpty(unitGrndValuesTot)){
			totProductMap = unitGrndValuesTot.get("TOT");
			if(UtilValidate.isNotEmpty(totProductMap)){
				totQtyLtrs = totProductMap.get("qtyLtrs")+(totProductMap.get("sQtyKgs")/1.03);
				//netPayableAmt=totProductMap.get("netAmt");
			}
		}	
	}		
	if(UtilValidate.isNotEmpty(totQtyLtrs)){
		UnitDetailsMap["totQtyLtrs"]= totQtyLtrs;
	}
	if(UtilValidate.isNotEmpty(netPayableMap) && UtilValidate.isNotEmpty(netPayableMap.get(entry.getKey()))){
		milkValue = (BigDecimal) netPayableMap.get(entry.getKey())
	}
	if(UtilValidate.isNotEmpty(milkValue)){
		UnitDetailsMap["milkValue"]=milkValue.setScale(0,BigDecimal.ROUND_HALF_UP);
}
	
	UnitWiseTotMap.put(entry.getKey(), UnitDetailsMap);
	
}
context.put("UnitWiseTotMap",UnitWiseTotMap);
// for sourAmount calculation
facilityIds = EntityUtil.getFieldListFromEntityList(unitsList, "facilityId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateStart)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO , thruDateEnd)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.IN , facilityIds)));

condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkTransfers = delegator.findList("MilkTransfer",condition1,null,null,null,false);

sourDistributionMap=[:];
if(UtilValidate.isNotEmpty(milkTransfers)){
	milkTransfers.each{ milkTransfer ->
		avgRate =0;
		sQuantity=0;
		gheeYeild =0;
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
				if(UtilValidate.isNotEmpty(shedWiseAmountAbstractMap.get(facilityId))){
					amt = shedWiseAmountAbstractMap.get(facilityId).get("grossAmount");
					}
				
				totLtrs =totLtrs+ltrs;
				Amount=Amount+amt;
									
			}
		}else{
			totLtrs = UnitWiseTotMap[transferFacilityId].get("totQtyLtrs");
			Amount =  UnitWiseTotMap[transferFacilityId].get("milkValue");
			if(UtilValidate.isNotEmpty(shedWiseAmountAbstractMap.get(transferFacilityId))){
				Amount = shedWiseAmountAbstractMap.get(transferFacilityId).get("grossAmount");
				}
		}
		BigDecimal avgRate = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(Amount) && Amount!=0 && totLtrs !=0){
			avgRate = (Amount/totLtrs);
		}
		 avgRate = avgRate.setScale(2,BigDecimal.ROUND_HALF_UP);
			// deductionRate = ((sourAmount - gheeYeildAmt)/totLtrs)
			if(UtilValidate.isEmpty(gheeYeild)){
				gheeYeild =0;
			}
			if(UtilValidate.isEmpty(sQuantity)){
				sQuantity =0;
			}
			BigDecimal deducionRate =BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(totLtrs) && totLtrs!=0){
				BigDecimal gheeValue = (gheeYeild *gheeRate);
				gheeValue = gheeValue.setScale(2,BigDecimal.ROUND_HALF_UP);
				deducionRate = (((sQuantity*avgRate) - gheeValue));
				deducionRate = deducionRate.setScale(0,BigDecimal.ROUND_HALF_UP);
			}
		
		if(UtilValidate.isNotEmpty(sourDistribution)){
			sourDistribution.each{ sourFacility ->
				sourDistFacilityId=sourFacility.facilityId;
				facilityQtyltrs = UnitWiseTotMap[sourDistFacilityId].get("totQtyLtrs");
				unitSourAmt = deducionRate*facilityQtyltrs/totLtrs;
				unitSourAmt = unitSourAmt.setScale(0,BigDecimal.ROUND_HALF_UP);
				if(UtilValidate.isEmpty(sourDistributionMap[sourDistFacilityId])){
					sourDistributionMap[sourDistFacilityId]=unitSourAmt;
				}else{
					sourDistributionMap[sourDistFacilityId] +=unitSourAmt;
				}
			}
		}else{
			facilityQtyltrs = UnitWiseTotMap[transferFacilityId].get("totQtyLtrs");
			unitSourAmt = deducionRate;
			if(UtilValidate.isEmpty(sourDistributionMap[transferFacilityId])){
				sourDistributionMap[transferFacilityId]=unitSourAmt;
			}else{
				sourDistributionMap[transferFacilityId] +=unitSourAmt;
			}
		
		}
	}
	
}
//UnitWiseTotals value

Iterator unitTotValueMap = unitTotalsMap.entrySet().iterator();
UnitWiseDetailsMap =[:];
GrandTotalsMap =[:];
GrandTotalsMap["milkValue"]=BigDecimal.ZERO;
GrandTotalsMap["shortKgFat"]=BigDecimal.ZERO;
GrandTotalsMap["shortKgFatAmt"]=BigDecimal.ZERO;
GrandTotalsMap["shortKgSnf"]=BigDecimal.ZERO;
GrandTotalsMap["shortKgSnfAmt"]=BigDecimal.ZERO;
GrandTotalsMap["sourAmt"]=BigDecimal.ZERO;
GrandTotalsMap["totalAmtRecovery"]=BigDecimal.ZERO;
GrandTotalsMap["netAmtPayable"]=BigDecimal.ZERO;
List shortageUnitsList=FastList.newInstance();
while (unitTotValueMap.hasNext()) {
	UnitAllValuesMap =[:]
	totQtyLtrs = BigDecimal.ZERO;
	BigDecimal milkValue = BigDecimal.ZERO;
	additions =BigDecimal.ZERO;
	deductions=BigDecimal.ZERO;
	UnitAllValuesMap["totQtyLtrs"]=BigDecimal.ZERO;
	UnitAllValuesMap["milkValue"]=BigDecimal.ZERO;
	UnitAllValuesMap["shortKgFat"]=BigDecimal.ZERO;
	UnitAllValuesMap["shortKgFatAmt"]=BigDecimal.ZERO;
	UnitAllValuesMap["shortKgSnf"]=BigDecimal.ZERO;
	UnitAllValuesMap["shortKgSnfAmt"]=BigDecimal.ZERO;
	UnitAllValuesMap["sourAmt"]=BigDecimal.ZERO;
	UnitAllValuesMap["totalAmtRecovery"]=BigDecimal.ZERO;
	UnitAllValuesMap["netAmtPayable"]=BigDecimal.ZERO;
	
	Map.Entry entry = unitTotValueMap.next();
	Map UnitTotalsValue = (Map)entry.getValue();
	//netPayableAmt=0
	if(UtilValidate.isNotEmpty(UnitTotalsValue.get("periodTransferTotalsMap"))){
		transfers = UnitTotalsValue.get("periodTransferTotalsMap").get(entry.getKey()).get("transfers");
		if(UtilValidate.isNotEmpty(transfers)){
			shortages = transfers.get("shortages");
			unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: parameters.customTimePeriodId , unitId: entry.getKey()]);
			if(UtilValidate.isNotEmpty(unitBillAbstract)){
				unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
				unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
				if(UtilValidate.isNotEmpty(unitGrndValuesTot)){
					totProductMap = unitGrndValuesTot.get("TOT");
					//Debug.log("totProductMap=="+totProductMap);
					if(UtilValidate.isNotEmpty(totProductMap)){
						totQtyLtrs = totProductMap.get("qtyLtrs");
						//netPayableAmt=totProductMap.get("netAmt");
					}
				}
			}
			
		}
		if(UtilValidate.isNotEmpty(totQtyLtrs)){
			UnitAllValuesMap["totQtyLtrs"]= totQtyLtrs;
		}
		if(UtilValidate.isNotEmpty(netPayableMap) && UtilValidate.isNotEmpty(netPayableMap.get(entry.getKey()))){
			milkValue = (BigDecimal) netPayableMap.get(entry.getKey())
		}
		if(UtilValidate.isNotEmpty(milkValue)){
			milkValue=milkValue.setScale(0,BigDecimal.ROUND_HALF_UP);
		}
		UnitAllValuesMap["milkValue"]=milkValue;
		if(UtilValidate.isNotEmpty(shortages)){
			UnitAllValuesMap["shortKgFat"]=shortages.get("kgFat");
			UnitAllValuesMap["shortKgFatAmt"]=shortages.get("kgFatAmt");
			UnitAllValuesMap["shortKgSnf"]=shortages.get("kgSnf");
			UnitAllValuesMap["shortKgSnfAmt"]=shortages.get("kgSnfAmt");
			if((shortages.get("kgFatAmt")) <0 || (shortages.get("kgSnfAmt")<0)){
				shortageUnitsList.add(entry.getKey());
			}
			sourAmt =0;
			if(UtilValidate.isNotEmpty(sourDistributionMap.get(entry.getKey()))){
				sourAmt =sourDistributionMap.get(entry.getKey());
			}
			
			
			UnitAllValuesMap["sourAmt"]=sourAmt;
			totalAmtRecovery = shortages.get("kgFatAmt")+shortages.get("kgSnfAmt")-sourAmt;
			UnitAllValuesMap["totalAmtRecovery"]=totalAmtRecovery;
			UnitAllValuesMap["netAmtPayable"]=(milkValue+totalAmtRecovery);
			
			if(UtilValidate.isNotEmpty(recoveryFromDDAccount)&&(("YES".equalsIgnoreCase(recoveryFromDDAccount)))){
				UnitAllValuesMap["shortKgFatAmt"]=BigDecimal.ZERO;
				UnitAllValuesMap["shortKgSnfAmt"]=BigDecimal.ZERO;
				UnitAllValuesMap["totalAmtRecovery"]=BigDecimal.ZERO;
				UnitAllValuesMap["sourAmt"]=BigDecimal.ZERO;
				UnitAllValuesMap["netAmtPayable"]=milkValue;
			}
			GrandTotalsMap["milkValue"] +=milkValue;
			if(shortages.get("kgFat")<0){
				GrandTotalsMap["shortKgFat"] +=shortages.get("kgFat");
			}
			if(shortages.get("kgSnf")<0){
				GrandTotalsMap["shortKgSnf"] +=shortages.get("kgSnf");
			}
			GrandTotalsMap["shortKgFatAmt"] +=shortages.get("kgFatAmt");
			GrandTotalsMap["shortKgSnfAmt"] +=shortages.get("kgSnfAmt");
			GrandTotalsMap["sourAmt"] +=sourAmt;
			GrandTotalsMap["totalAmtRecovery"] +=totalAmtRecovery;
			GrandTotalsMap["netAmtPayable"] +=(milkValue+totalAmtRecovery);
		}
	}
	UnitWiseDetailsMap.put(entry.getKey(), UnitAllValuesMap);
}
context.put("UnitWiseDetailsMap", UnitWiseDetailsMap);
context.put("GrandTotalsMap", GrandTotalsMap);
totAmountsMap =context.get("totAmountsMap");

BigDecimal userCharges = BigDecimal.ZERO;
// getting DD amount
ddAmountMap =[:];
if(UtilValidate.isNotEmpty(context.get("shedWiseTotalsMap"))){
	
	shedWiseTotalsMap = context.get("shedWiseTotalsMap");
	totalDeductions = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("DednsTot"))){
		totalDeductions = shedWiseTotalsMap.get("DednsTot");
	}
	feedDeductions= 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_FEEDDED"))){
		feedDeductions = shedWiseTotalsMap.get("MILKPROC_FEEDDED");
	}
	seedDed = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_SEEDDED"))){
		seedDed = shedWiseTotalsMap.get("MILKPROC_SEEDDED");
	}
	vaccine = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_VACCINE"))){
		vaccine = shedWiseTotalsMap.get("MILKPROC_VACCINE");
	}
	stores = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_STORET"))){
		stores = shedWiseTotalsMap.get("MILKPROC_STORET");
	}
	others = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_OTHERDED"))){
		others = shedWiseTotalsMap.get("MILKPROC_OTHERDED");
	}
	cessOnSale = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_CESSONSALE"))){
		cessOnSale = shedWiseTotalsMap.get("MILKPROC_CESSONSALE");
	}
	vijayRD = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_VIJAYARD"))){
		vijayRD = shedWiseTotalsMap.get("MILKPROC_VIJAYARD");
	}
	vijayaLN = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_VIJAYALN"))){
		vijayaLN = shedWiseTotalsMap.get("MILKPROC_VIJAYALN");
	}
	MSpares = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_MSPARES"))){
		MSpares = shedWiseTotalsMap.get("MILKPROC_MSPARES");
	}
	MTester = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_MTESTER"))){
		MTester = shedWiseTotalsMap.get("MILKPROC_MTESTER");
	}
	storeA = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_STOREA"))){
		storeA = shedWiseTotalsMap.get("MILKPROC_STOREA");
	}
	stationary = 0;
	if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_STATONRY"))){
		stationary = shedWiseTotalsMap.get("MILKPROC_STATONRY");
	}
	shedId= parameters.shedId;
	if(shedId.equals("WGD")){
		if(UtilValidate.isNotEmpty(shedWiseTotalsMap.get("MILKPROC_MSPARES"))){
			cessOnSale =shedWiseTotalsMap.get("MILKPROC_MSPARES");
			MSpares=0;
		}
		
	 }
	customTimePeriodId = parameters.customTimePeriodId;
	netAmt =0;
	
	if(UtilValidate.isNotEmpty(totAmountsMap)){
		bmAmt = (totAmountsMap.get("BM")).setScale(0,BigDecimal.ROUND_HALF_UP);
		cmAmt = (totAmountsMap.get("CM")).setScale(0,BigDecimal.ROUND_HALF_UP);
		milkBillAmt = bmAmt+ cmAmt;
		tipValue = totAmountsMap.get("tipAmt");
		opcostValue = totAmountsMap.get("opCost");
		cartage = totAmountsMap.get("cartage");
		comnAmt= totAmountsMap.get("commAmt");
		addnAmt = totAmountsMap.get("addnAmt");
		grossAmt = milkBillAmt+tipValue+opcostValue+cartage+addnAmt+comnAmt;
		shortageAmt =0;
		if(UtilValidate.isNotEmpty(GrandTotalsMap)){
			shortageAmt =GrandTotalsMap.get("totalAmtRecovery");
		}
		netAmt = grossAmt -(feedDeductions+cessOnSale-shortageAmt);
		
	}
	grandTotalNetAmt =0;
	if(UtilValidate.isNotEmpty(GrandTotalsMap)){
		grandTotalNetAmt = GrandTotalsMap.get("netAmtPayable");
	}	
	
	ddAccDetailsMap = [:];
	shedDDAccountMap = ProcurementReports.getShedDDAccount(dctx, [userLogin:userLogin,facilityId:shedId,customTimePeriodId:customTimePeriodId]);
	
	if(ServiceUtil.isSuccess(shedDDAccountMap)){
		//Milkosoft Charges
		BigDecimal totalLtrs =BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(context.getAt("shedTotLtrsMap"))){
			shedTotLtrsMap = context.get("shedTotLtrsMap");
			bmTotLtrs = (BigDecimal)shedTotLtrsMap.get("Buffalo Milk");
			cmTotLtrs = (BigDecimal)shedTotLtrsMap.get("Cow Milk");
			totalLtrs=bmTotLtrs+cmTotLtrs;
		}		
		BigDecimal tempAmount = totalLtrs.multiply(new BigDecimal(0.025));
		BigDecimal serviceTax = tempAmount.multiply(new BigDecimal(12.36)).divide(new BigDecimal(100));
		userCharges = userCharges.add(tempAmount.add(serviceTax));
		userCharges=userCharges.setScale(0, BigDecimal.ROUND_HALF_UP);
		context.putAt("shedTotLtrs", totalLtrs);
		context.putAt("userCharges", userCharges);
		if(thruDate <= billingDate){
			context.putAt("userCharges", 0);
		}
		ddAccDetailsMap = shedDDAccountMap.get("ddAccDetailsMap");
		ddAmountMap.putAll(ddAccDetailsMap);
		ddAmountMap["unitCode"] =ddAccDetailsMap.partyId;
		ddAmountMap["vaccine"]=vaccine;
		ddAmountMap["seedDed"]=seedDed;
		ddAmountMap["stores"]=stores;
		ddAmountMap["vijayRD"]=vijayRD;
		ddAmountMap["vijayaLN"]=vijayaLN;
		ddAmountMap["MSpares"]=MSpares;
		ddAmountMap["MTester"]=MTester;
		ddAmountMap["storeA"]=storeA;
		ddAmountMap["stationary"]=stationary;
		ddAmountMap["feedDed"]=feedDeductions;
		ddAmountMap["cessOnSale"]=cessOnSale;
		shedMaintenance=0;
		if(UtilValidate.isNotEmpty(context.getAt("shedMaintAmount"))){
			shedMaintenance=context.getAt("shedMaintAmount");
		}
		ddAmountMap["shedMaintAmt"]=shedMaintenance;
		ddAmountMap["others"]=others;
		tipAmount = netAmt-grandTotalNetAmt;
		tipAmount = tipAmount-(vaccine+seedDed+stores+others+stationary);
		ddAmountMap["tipAmount"]=tipAmount;
		/*totalDDAmount = tipAmount + (totalDeductions-feedDeductions);*/
		ddAmountMap["amount"]=(netAmt-grandTotalNetAmt)+shedMaintenance;
		
		// For Few sheds we need to remit all recoveries in to DD Account.
		ddAmountDetails=delegator.findOne("FacilityAttribute",[facilityId : shedId, attrName: "DDACCOUNTAMOUNT"], false);
		if(UtilValidate.isNotEmpty(ddAmountDetails)){
			if("Y".equals(ddAmountDetails.get("attrValue"))){
				ddAmountMap["amount"]=(netAmt-grandTotalNetAmt)+(feedDeductions)+shedMaintenance;
				ddAmountMap["tipAmount"] = (ddAmountMap["tipAmount"]-(vijayRD+vijayaLN+storeA+MTester+MSpares));
				context.putAt("allRecoveries", ddAmountDetails.get("attrValue"));
			}
		}
		
		//
		if(UtilValidate.isNotEmpty(ddAmountMap["tipAmount"])){
			ddAmountMap["tipAmount"]=ddAmountMap["tipAmount"]-userCharges;
		}
		if(UtilValidate.isNotEmpty(ddAmountMap["amount"])){
			ddAmountMap["amount"]=ddAmountMap["amount"]-userCharges;
		}
		ddAmountMap["shortKgFatAmt"]=BigDecimal.ZERO;
		ddAmountMap["shortKgSnfAmt"]=BigDecimal.ZERO;
		ddAmountMap["sourAmt"]=BigDecimal.ZERO;
		ddAmountMap["totalAmtRecovery"]=BigDecimal.ZERO;
		ddAmountMap["netAmtPayable"] = ddAmountMap.get("amount");
		if(UtilValidate.isNotEmpty(recoveryFromDDAccount)&&(("YES".equalsIgnoreCase(recoveryFromDDAccount)))){
				BigDecimal totShortKgFatAmt = BigDecimal.ZERO;
				BigDecimal totShortKgSnfAmt = BigDecimal.ZERO;
				BigDecimal sour = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(context.getAt("totshrtKgFatAmt"))){
					totShortKgFatAmt = (context.get("totshrtKgFatAmt")*(-1));
				}
				if(UtilValidate.isNotEmpty(context.getAt("totshrtKgSnfAmt"))){
					totShortKgSnfAmt = (context.get("totshrtKgSnfAmt")*(-1));
				}
				if(UtilValidate.isNotEmpty(context.getAt("GrandTotalsMap"))){
					GrandTotalsMap = context.get("GrandTotalsMap");
					sour = (GrandTotalsMap.get("sourAmt"));
				}
				ddAmountMap["shortKgFatAmt"]=totShortKgFatAmt*(-1);
				ddAmountMap["shortKgSnfAmt"]=totShortKgSnfAmt*(-1);
				ddAmountMap["sourAmt"]=sour;
				
				ddAmountMap["totalAmtRecovery"]=totShortKgFatAmt+totShortKgSnfAmt+sour;
				ddAmountMap["netAmtPayable"] = ddAmountMap.get("netAmtPayable") - ddAmountMap.get("totalAmtRecovery");
			}
		
	}
}
context.putAt("shortageUnitsList", shortageUnitsList);
context.putAt("ddAmountMap", ddAmountMap);
context.put("sourDistributionMap", sourDistributionMap);
context.put("unitWiseAdjustmentMap", unitWiseAdjustmentMap);
context.put("unitTotalsMap", unitTotalsMap);


//for Procurement Sms
	shedId = parameters.shedId;
	shedCustomTimePeriodId = parameters.shedCustomTimePeriodId;
	shedMaintenance=0;
	smsBM = 0;
	smsCM = 0;
	smsOpCost = 0;
	smsCartage = 0;
	smsAddAmt = 0;
	smsTipAmt = 0;
	smsDiffAmt = 0;
	smsFeed = 0;
	smsCess = 0;
	smsTotShortKgFatAmt = 0;
	smsTotShortKgSnfAmt = 0;
	smsSour = 0;
	
	BigDecimal netAmountPayable = BigDecimal.ZERO;
	BigDecimal bmTotLtrs = BigDecimal.ZERO;
	BigDecimal cmTotLtrs = BigDecimal.ZERO;
	
	if(UtilValidate.isNotEmpty(context.getAt("shedMaintAmount"))){
		shedMaintenance=context.getAt("shedMaintAmount");
	}
	if(UtilValidate.isNotEmpty(context.getAt("totAmountsMap"))){
		totAmountsMap =context.get("totAmountsMap");
		smsBM = totAmountsMap.get("BM");
		smsCM = totAmountsMap.get("CM");
		smsOpCost = (totAmountsMap.get("opCost")+totAmountsMap.get("commAmt"));
		smsCartage = totAmountsMap.get("cartage");
		smsAddAmt = (totAmountsMap.get("addnAmt")+ shedMaintenance);
		smsTipAmt = totAmountsMap.get("tipAmt");
	}
	if(UtilValidate.isNotEmpty(context.getAt("difAmt"))){
		smsDiffAmt = context.get("difAmt");
	}
	netAdditions = (smsBM+smsCM+smsOpCost+smsCartage+smsAddAmt+smsTipAmt+smsDiffAmt);
	
	if(UtilValidate.isNotEmpty(context.getAt("feedAmt"))){
		smsFeed = context.get("feedAmt");
	}
	if(UtilValidate.isNotEmpty(context.getAt("cessOnSaleAmt"))){
		smsCess = context.get("cessOnSaleAmt");
	}
	if(UtilValidate.isNotEmpty(context.getAt("totshrtKgFatAmt"))){
		smsTotShortKgFatAmt = (context.get("totshrtKgFatAmt")*(-1));
	}
	if(UtilValidate.isNotEmpty(context.getAt("totshrtKgSnfAmt"))){
		smsTotShortKgSnfAmt = (context.get("totshrtKgSnfAmt")*(-1));
	}
	if(UtilValidate.isNotEmpty(context.getAt("GrandTotalsMap"))){
		GrandTotalsMap = context.get("GrandTotalsMap");
		smsSour = (GrandTotalsMap.get("sourAmt"));
	}
	netDifference = (smsFeed+smsCess+smsTotShortKgFatAmt+smsTotShortKgSnfAmt+smsSour);
	
	netAmountPayable = (netAdditions-netDifference);
	
	if(UtilValidate.isNotEmpty(context.getAt("shedTotLtrsMap"))){
		shedTotLtrsMap = context.get("shedTotLtrsMap");
		bmTotLtrs = (BigDecimal)shedTotLtrsMap.get("Buffalo Milk").setScale(0,BigDecimal.ROUND_HALF_UP);
		cmTotLtrs = (BigDecimal)shedTotLtrsMap.get("Cow Milk").setScale(0,BigDecimal.ROUND_HALF_UP);
	}
	
	context.put("bmTotLtrs", bmTotLtrs);
	context.put("cmTotLtrs", cmTotLtrs);
	context.put("netAmountPayable", netAmountPayable);
	sanctionedAmt = netAmountPayable-userCharges;
	context.put("sanctionedAmt",sanctionedAmt);
/*if(parameters.procSms == "Y"){
	smsService = dispatcher.runSync("ApProcurementSummarySms", [bmTotLtrs: bmTotLtrs, cmTotLtrs: cmTotLtrs, netAmountPayable: (netAmountPayable-userCharges), customTimePeriodId: shedCustomTimePeriodId, shedId: shedId, userLogin: userLogin]);
}*/

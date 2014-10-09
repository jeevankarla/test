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

if(UtilValidate.isEmpty(parameters.shedCustomTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);

condProductList =[];
condProductList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_PROC_MRGN")));
condProductList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId)));
condProductList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED")));
conditionPeriodBill = EntityCondition.makeCondition(condProductList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling",conditionPeriodBill,null,null,null,false);
List periodBillingIds = periodBillingList.periodBillingId;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
orderAdjItemsList = UtilMisc.sortMaps(orderAdjItemsList, UtilMisc.toList("sequenceNum"));
context.put("orderAdjItemsList",orderAdjItemsList);

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);

// here we are populatin productMap for getting productId
Map productDetailsMap = FastMap.newInstance();
for(product in procurementProductList){
		productDetailsMap.put(product.get("productName"),product.get("productId"));
	}


/*Map inputFacRateAmt = UtilMisc.toMap("userLogin", userLogin);
inputFacRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
inputFacRateAmt.put("rateCurrencyUomId", "INR");
*/
shedWiseAmountAbstractMap = [:];
shedWiseTotalsMap = [:];
for(procProduct in procurementProductList){
	shedWiseTotalsMap[procProduct.brandName+"Amount"] = 0;
}
shedWiseTotalsMap["cartage"] = 0;
shedWiseTotalsMap["commissionAmount"] = 0;
shedWiseTotalsMap["AddnTot"] = 0;
orderAdjItemsList.each { orderAdj ->
	shedWiseTotalsMap[orderAdj.orderAdjustmentTypeId] = 0;
}
shedWiseTotalsMap["DednsTot"] = 0;
shedWiseTotalsMap["grossAmount"] = 0;
shedWiseTotalsMap["netAmount"] = 0;
shedWiseTotalsMap["tipAmount"] = 0;
shedWiseTotalsMap["opCost"] = 0;

conditionList =[];
if(UtilValidate.isEmpty(parameters.shedId)){
	return;
}
/*condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);*/
unitsListSorted = [];
/*if(UtilValidate.isNotEmpty(unitsList)){
	unitsList.each{ unit->
		unit.set("facilityCode", (unit.facilityCode).toInteger());
		unitsListSorted.add(unit);
	}
	unitsListSorted = UtilMisc.sortMaps(unitsListSorted, UtilMisc.toList("facilityCode"));
}*/
shedUnits = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);
/*shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : parameters.shedId]);
unitsListSorted = shedUnits.unitsDetailList;*/
unitsListSorted = shedUnits.customTimePeriodUnitsDetailList;
unitsListSorted.each{ unit ->
	unitWiseAmountAbstractMap = [:];
	unitWiseAmountAbstractMap["unitName"] = unit.facilityName;
	unitWiseAmountAbstractMap["grossAmount"] = 0;
	orderAdjItemsList.each { orderAdj ->
		unitWiseAmountAbstractMap[orderAdj.orderAdjustmentTypeId] = 0;
	}
	//for BMilk and CMilk Price
	totQtyKgs = 0;
	totPrice = 0;
	for(procProduct in procurementProductList){
		unitWiseAmountAbstractMap[procProduct.brandName+"Amount"] = 0;
	}
	unitWiseAmountAbstractMap["tipAmount"] = 0;
	unitWiseAmountAbstractMap["opCost"] = 0;
	//unitTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: dayBegin , thruDate: dayEnd , facilityId: unit.facilityId ]);
	
	unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: parameters.customTimePeriodId , unitId: unit.facilityId]);
	if(ServiceUtil.isError(unitBillAbstract)){
		Debug.logError("error while geting Bill's Abstract unitId: :"+unitId, "");
		return error;
	}
	unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
	unitGrndValuesTot = (unitAbsTotals).getAt("TOT");	
	
			/*dayTotalsMap = unitMap.get("dayTotals");
			dateMap = dayTotalsMap.get("TOT");
			supplyMap = dateMap.get("TOT");
			totProductMap = supplyMap.get("TOT");
			totQtyKgs = totProductMap.get("qtyKgs");*/
			tipAmount = 0;
			opCost =0;
			cartage=0;
			commissionAmt=0;
			addnTot=0;
			dednTot=0;
	if(UtilValidate.isNotEmpty(unitGrndValuesTot)){
			for(procProduct in procurementProductList){				
				productAbs = unitGrndValuesTot.getAt(procProduct.productId);
				
				unitWiseAmountAbstractMap[procProduct.brandName+"Amount"] = productAbs.get("price");
				totPrice  +=  productAbs.get("price");
				
				/*inMap = [:];
				inMap.put("userLogin",context.userLogin);
				inMap.put("facilityId",unit.facilityId);
				inMap.put("fatPercent", BigDecimal.ZERO);
				inMap.put("snfPercent", BigDecimal.ZERO);
				inMap.put("productId",procProduct.productId);
				inMap.put("priceDate", dayBegin);
				Map priceChart = PriceServices.getProcurementProductPrice(dctx,inMap);
				useTotalSolids = priceChart.get("useTotalSolids");
				//tip for center
				inputFacRateAmt.put("facilityId", unit.facilityId);
				inputFacRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
				inputFacRateAmt.put("productId", procProduct.productId);
				unitRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
				if("Y".equals(useTotalSolids)){
					fatSnfQty = (productMap.get("kgFat")-productMap.get("zeroKgFat"))+(productMap.get("kgSnf")-productMap.get("zeroKgSnf"));
					tipAmount += fatSnfQty*unitRateAmount.rateAmount;
				}else{
					tipAmount += (productMap.get("kgFat")-productMap.get("zeroKgFat"))*unitRateAmount.rateAmount;
				}*/
			}
			totProductMap = unitGrndValuesTot.get("TOT");
			
			if(UtilValidate.isNotEmpty(totProductMap)){
				tipAmount=totProductMap.get("tipAmt");
				opCost=totProductMap.get("opCost");
				cartage=totProductMap.get("cartage");
				commissionAmt=totProductMap.get("commissionAmount");
				addnTot=totProductMap.get("grsAddn");
				dednTot=totProductMap.get("grsDed");				
			}
			
	}
		if(UtilValidate.isNotEmpty(tipAmount)){
			unitWiseAmountAbstractMap["tipAmount"]=tipAmount;
		}
		if(UtilValidate.isNotEmpty(opCost)){
			unitWiseAmountAbstractMap["opCost"]=opCost;
		}
		if(UtilValidate.isNotEmpty(cartage)){
			unitWiseAmountAbstractMap["cartage"]=cartage;
		}
		if(UtilValidate.isNotEmpty(commissionAmt)){
			unitWiseAmountAbstractMap["commissionAmount"]=commissionAmt;
		}
		if(UtilValidate.isNotEmpty(addnTot)){
			unitWiseAmountAbstractMap["AddnTot"]=addnTot;
		}
		if(UtilValidate.isNotEmpty(dednTot)){
			unitWiseAmountAbstractMap["DednsTot"]=dednTot;
		}
			//inputFacRateAmt.put("facilityId", unit.facilityId);			
			/*for(key in dateMap.keySet()){
				if(!key.equalsIgnoreCase("TOT")){
					inputFacRateAmt.put("supplyTypeEnumId", key);
					tempMap = dateMap.get(key);
					inputFacRateAmt.put("rateTypeId", "PROC_OP_COST");
					inputFacRateAmt.put("slabAmount",unit.facilitySize);
					Map<String, Object> opCostAmtMap = FastMap.newInstance();
					if(UtilValidate.isNotEmpty(tempMap)){
						for( productKey in tempMap.keySet()){
							if(!productKey.equalsIgnoreCase("TOT")){
								BigDecimal totalSolids=BigDecimal.ZERO;
								qtyLtrs = 0;
								String prodKey = productKey.toString(); 
								inputFacRateAmt.put("productId",productDetailsMap.get(productKey));
								opCostAmtMap = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
								BigDecimal opCostRate = BigDecimal.ZERO;
								String uomId = "VLIQ_L";
								if(ServiceUtil.isSuccess(opCostAmtMap)){
									opCostRate = (BigDecimal)opCostAmtMap.get("rateAmount");
									if(UtilValidate.isNotEmpty(opCostAmtMap.get("uomId"))){
										uomId = (String) opCostAmtMap.get("uomId");
									}
								}
								qtyLtrs = qtyLtrs+( tempMap.get(productKey).get("qtyLtrs"))+( tempMap.get(productKey).get("sQtyLtrs"));
								
								if(uomId.equalsIgnoreCase("VLIQ_KGFAT")){
									totalSolids = totalSolids+ ( tempMap.get(productKey).get("kgFat"))+( tempMap.get(productKey).get("sKgFat"));
									uomId = "VLIQ_TS";
								}else{
									totalSolids = totalSolids+ ( tempMap.get(productKey).get("kgFat"))+( tempMap.get(productKey).get("sKgFat"))+( tempMap.get(productKey).get("kgSnf"));
								}
								opCost = opCost+(ProcurementNetworkServices.calculateProcOPCost(dctx,UtilMisc.toMap("uomId", uomId,"totalSolids",totalSolids,"qtyLtrs",qtyLtrs,"opCostRate",opCostRate)));
							}
						}
					}
					
					
				}
			}
			*/
			
	
	
	
	//for Cartage and Commission
	/*unitWiseAmountAbstractMap["cartage"] = 0;
	unitWiseAmountAbstractMap["commissionAmount"] = 0;
	unitCentersList = ProcurementNetworkServices.getUnitAgents(dctx,UtilMisc.toMap("unitId", unit.facilityId));
	centersList = unitCentersList.get("agentsList");
	centersList.each{ center ->
		condList =[];
		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, center.facilityId)));
		condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN, periodBillingIds)));
		conditionFcp = EntityCondition.makeCondition(condList,EntityOperator.AND);
		facCommProList = delegator.findList("FacilityCommissionProc",conditionFcp,null,null,null,false);
		if(UtilValidate.isNotEmpty(facCommProList)){
			facCommProList.each{ facCommProItem ->
				//unitWiseAmountAbstractMap["cartage"] += facCommProItem.cartage;
				unitWiseAmountAbstractMap["commissionAmount"] += facCommProItem.commissionAmount;
			}
		}
	}*/

	//for Adjustments 
	//unitWiseAmountAbstractMap["AddnTot"] = 0;
	//unitWiseAmountAbstractMap["DednsTot"] = 0;
	unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: dayBegin , thruDate: dayEnd, facilityId: unit.facilityId]);
	if(UtilValidate.isNotEmpty(unitAdjustments)){
	  adjustmentsTypeValues = unitAdjustments.get("adjustmentsTypeMap");
	   if(adjustmentsTypeValues !=null){
		   adjustmentsTypeValues.each{ adjustmentValues ->
			  if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
				  additionsList = adjustmentValues.getValue();
				 /* additionsList.each{ additionValues ->
					  unitWiseAmountAbstractMap["AddnTot"] += additionValues.getValue();
				  }*/
			  }else{
				  deductionsList = adjustmentValues.getValue();
				  deductionsList.each{ deductionValues ->
					  unitWiseAmountAbstractMap[deductionValues.getKey()] = deductionValues.getValue();
					  //unitWiseAmountAbstractMap["DednsTot"] += deductionValues.getValue();
				  }
			  }
		   }
		}
	 }
	//for GrossAmount   GrossAmount = AdditionAmount + CommissionAmount + Cartage + Buffalo Milk Amount +Cow Milk Amount.
		unitWiseAmountAbstractMap["grossAmount"] = unitWiseAmountAbstractMap.get("grossAmount")+unitWiseAmountAbstractMap.get("AddnTot")+unitWiseAmountAbstractMap.get("commissionAmount")+unitWiseAmountAbstractMap.get("opCost")+unitWiseAmountAbstractMap.get("cartage")+totPrice;
		
		unitWiseAmountAbstractMap["opCost"] = unitWiseAmountAbstractMap.get("opCost");
		unitWiseAmountAbstractMap["commissionAmount"] = unitWiseAmountAbstractMap.get("commissionAmount");
	//for NetAmount  	NetAmount  = GrossAmount - AllDeductionsAmount.
		unitWiseAmountAbstractMap["netAmount"] = (new BigDecimal(unitWiseAmountAbstractMap.get("grossAmount")-unitWiseAmountAbstractMap.get("DednsTot")));
	
	shedWiseAmountAbstractMap[unit.facilityId] = unitWiseAmountAbstractMap;
	
	//populating totals
	shedWiseTotalsMap["unitName"] = "TOTAL";
	for(procProduct in procurementProductList){
		shedWiseTotalsMap[procProduct.brandName+"Amount"] += unitWiseAmountAbstractMap.get(procProduct.brandName+"Amount");
	}
	shedWiseTotalsMap["AddnTot"] += unitWiseAmountAbstractMap.get("AddnTot");
	shedWiseTotalsMap["commissionAmount"] += unitWiseAmountAbstractMap.get("commissionAmount");
	shedWiseTotalsMap["cartage"] += unitWiseAmountAbstractMap.get("cartage");
	orderAdjItemsList.each { orderAdj ->
		shedWiseTotalsMap[orderAdj.orderAdjustmentTypeId] += unitWiseAmountAbstractMap.get(orderAdj.orderAdjustmentTypeId);
	}
	shedWiseTotalsMap["DednsTot"] += unitWiseAmountAbstractMap.get("DednsTot");
	shedWiseTotalsMap["grossAmount"] += unitWiseAmountAbstractMap.get("grossAmount");
	shedWiseTotalsMap["netAmount"] += unitWiseAmountAbstractMap.get("netAmount");
	shedWiseTotalsMap["tipAmount"] += unitWiseAmountAbstractMap.get("tipAmount");
	shedWiseTotalsMap["opCost"] += unitWiseAmountAbstractMap.get("opCost");
}
shedWiseAmountAbstractMap["TOTAL"] = shedWiseTotalsMap;
context.put("shedWiseTotalsMap",shedWiseTotalsMap);
context.put("shedWiseAmountAbstractMap",shedWiseAmountAbstractMap);

//Debug.log("shedWiseAmountAbstractMap============================"+shedWiseAmountAbstractMap);




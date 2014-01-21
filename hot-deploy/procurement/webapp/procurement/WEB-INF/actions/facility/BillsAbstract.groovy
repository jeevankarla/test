import java.security.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;

import java.lang.ref.ReferenceQueue.Null;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.*;
import java.sql.Date;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.procurement.PriceServices;
import org.ofbiz.service.ServiceUtil;


dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);

def populateUnitBillAbstract(unitId , customTimePeriodId){		
		context.put("fromDateTime",fromDateTime);
		context.put("thruDateTime",thruDateTime);
		
		//context.put("facilityId",unitId);
	
		facilityDetail = delegator.findOne("Facility", [facilityId : unitId], false);
		if(UtilValidate.isNotEmpty(facilityDetail)){
			unitName = facilityDetail.facilityName;
			unitCode = facilityDetail.facilityCode;
			context.put("unitName",unitName);
			context.put("unitCode",unitCode);
		}else{
			return;
		}
		
		Map inputFacRateAmt = UtilMisc.toMap("userLogin", userLogin);
		inputFacRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
		inputFacRateAmt.put("rateCurrencyUomId", "INR");
		
		adjustmentDedTypes = [:];
		orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
		initAdjMap =[:];
		for(int i=0;i<orderAdjItemsList.size();i++){
			orderAdjItem = orderAdjItemsList.get(i);
			adjustmentDedTypes[i] = orderAdjItem;
			initAdjMap[orderAdjItem.orderAdjustmentTypeId] = 0;
		}
		testAdjMap = [:];
		for(int j=0;j < 12;j++){
			if(UtilValidate.isNotEmpty(adjustmentDedTypes.get(j))){
				testAdjMap[j] = (adjustmentDedTypes.get(j).orderAdjustmentTypeId);
			}else{
				testAdjMap[j] = 0.00;
			}
		}
		context.put("testAdjMap",testAdjMap);
		context.put("adjustmentDedTypes",adjustmentDedTypes);
		
		procurementProductList =[];
		procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
		context.put("procurementProductList",procurementProductList);
		
		//deductions
		unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: dayBegin , thruDate: dayEnd, facilityId: unitId]);
		unitCenterWiseAdjustments = unitAdjustments.get("centerWiseAdjustments");
		
		unitMilkBillAbstMap = [:];
		routeMilkBillAbstMap = [:];
		unitPeriodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: dayBegin , thruDate: dayEnd , facilityId: unitId, includeCenterTotals: true]);
		
		//total 
		unitGrndTot = [:];
		unitGrndValuesTot = unitPeriodTotals.get(unitId);
		unitGrndTot["centerName"] = "TOTAL";
		if (UtilValidate.isNotEmpty(unitGrndValuesTot)) {
			dayTotalsMap = unitGrndValuesTot.get("dayTotals");
			dateMap = dayTotalsMap.get("TOT");
			inputFacRateAmt =[:];
			inputFacRateAmt.put("userLogin", userLogin);
			inputFacRateAmt.put("rateCurrencyUomId", "INR");
			inputFacRateAmt.put("rateTypeId", "PROC_OP_COST");
			inputFacRateAmt.put("facilityId",unitId);
			opCost =0;
			for(key in dateMap.keySet()){
				if(!key.equalsIgnoreCase("TOT")){
					inputFacRateAmt.put("supplyTypeEnumId", key);
					qtyLtrs = 0;
					tempMap = dateMap.get(key);
					if(UtilValidate.isNotEmpty(tempMap)){
						for( productKey in tempMap.keySet()){
							if(!productKey.equalsIgnoreCase("TOT")){
								qtyLtrs = qtyLtrs+( tempMap.get(productKey).get("qtyLtrs"));
							}
						}
					}
					inputFacRateAmt.put("slabAmount",facilityDetail.facilitySize);
					Map<String, Object> opCostAmtMap = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
					BigDecimal opCostRate = BigDecimal.ZERO;
					if(ServiceUtil.isSuccess(opCostAmtMap)){
						opCostRate = (BigDecimal)opCostAmtMap.get("rateAmount");
					}
					opCost = opCost+(opCostRate.multiply(qtyLtrs));
				}
			}
			
			unitGrndTot["opCost"] = opCost;
			
			
			supplyMap = dateMap.get("TOT");
			
			totProductMap = supplyMap.get("TOT");
			unitGrndTot["totQtyKgs"] = totProductMap.get("qtyKgs");
			unitGrndTot["totQtyLtrs"] = totProductMap.get("qtyLtrs");
			unitGrndTot["totKgFat"] = totProductMap.get("kgFat");
			unitGrndTot["totKgSnf"] = totProductMap.get("kgSnf");
			//unitGrndTot["totPrice"] = totProductMap.get("price");
			
			procurementProductList.each { procProd ->
				productMap = supplyMap.get(procProd.productName);
				unitGrndTot[procProd.brandName+"QtyKgs"] = productMap.get("qtyKgs");
				unitGrndTot[procProd.brandName+"QtyLtrs"] = productMap.get("qtyLtrs");
				unitGrndTot[procProd.brandName+"KgFat"] = productMap.get("kgFat");
				unitGrndTot[procProd.brandName+"KgSnf"] = productMap.get("kgSnf");
				unitGrndTot[procProd.brandName+"Price"] = productMap.get("price");
				unitGrndTot[procProd.brandName+"tipAmount"] = 0;
			}
			
		}
		
		unitGrndTot["commAmt"] = 0;
		unitGrndTot["cartage"] = 0;
		orderAdjItemsList.each { orderAdj ->
			unitGrndTot[orderAdj.orderAdjustmentTypeId] = 0;
		}
		unitGrndTot["DednsTot"] = 0;
		unitGrndTot["netAmount"] = 0;
		unitGrndTot["grossAmount"] = 0;
		unitGrndTot["TOTtipAmount"] = 0;
		unitRoutesList = ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",unitId));
		routesDetailsList = unitRoutesList.get("routesDetailList");
		
		for(route in routesDetailsList){
			
			routeCentersList = ProcurementNetworkServices.getRouteAgents(dctx,UtilMisc.toMap("routeId",route.facilityId ));
			centerDetailsList = routeCentersList.get("agentDetailsList");
			routeWiseMilkBillsAbstmap = [:];
			routeWiseMilkBillsAbstmap["commAmt"] = 0;
			routeWiseMilkBillsAbstmap["cartage"] = 0;
			orderAdjItemsList.each { orderAdj ->
				routeWiseMilkBillsAbstmap[orderAdj.orderAdjustmentTypeId] = 0;
			}
			routeWiseMilkBillsAbstmap["TOTtipAmount"] = 0;
			routeWiseMilkBillsAbstmap["cartage"] = 0;
			routeWiseMilkBillsAbstmap["commAmt"] = 0;
			routeWiseMilkBillsAbstmap["grossAmount"] = 0;
			routeWiseMilkBillsAbstmap["DednsTot"] = 0;
			routeWiseMilkBillsAbstmap["netAmount"] = 0;
			routeWiseMilkBillsAbstmap["totQtyKgs"] = 0;
			routeWiseMilkBillsAbstmap["totQtyLtrs"] = 0;
			routeWiseMilkBillsAbstmap["totKgFat"] = 0;
			routeWiseMilkBillsAbstmap["totKgSnf"] = 0;
			for(procProd in procurementProductList){
				routeWiseMilkBillsAbstmap[procProd.brandName+"QtyKgs"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"QtyLtrs"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"KgFat"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"KgSnf"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"Price"] = 0;
				routeWiseMilkBillsAbstmap[procProd.brandName+"tipAmount"] = 0;
			}
			
			routeWiseMilkBillsAbstmap["centerName"] = "TOTAL";
			routeWiseMilkBillsAbstmap["centerCode"] = route.facilityCode;
			routeWiseMilkBillsAbstmap["centerOwnerName"] = (PartyHelper.getPartyName(delegator, route.ownerPartyId, true)).replace(',', '');
			
			centerDetailsList.each{ center ->
				unitCentersMilkBillAbstMap = [:];
				totPriceForCenter = 0;
				unitCentersMilkBillAbstMap["cartage"] = 0;
				unitCentersMilkBillAbstMap["commAmt"] = 0;
				unitCentersMilkBillAbstMap["grossAmount"] = 0;
				unitCentersMilkBillAbstMap["DednsTot"] = 0;
				unitCentersMilkBillAbstMap["netAmount"] = 0;
				unitCentersMilkBillAbstMap["centerId"] = center.facilityId;
			    unitCentersMilkBillAbstMap["RNO"]   = route.facilityCode;
				for(procProd in procurementProductList){
					unitCentersMilkBillAbstMap[procProd.brandName+"tipAmount"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"cartage"] = 0;
					unitCentersMilkBillAbstMap[procProd.brandName+"commAmt"] = 0;
				}
				unitTotals = unitPeriodTotals.get("centerWiseTotals");
			
					if(UtilValidate.isNotEmpty(unitPeriodTotals.get("centerWiseTotals"))){
					centerMap = unitTotals.get(center.facilityId);
					unitCentersMilkBillAbstMap["centerName"] = center.facilityName;
					unitCentersMilkBillAbstMap["centerCode"] = center.facilityCode;
					unitCentersMilkBillAbstMap["centerOwnerName"] = (PartyHelper.getPartyName(delegator, center.ownerPartyId, true)).replace(',', '');
					
					if (UtilValidate.isNotEmpty(centerMap)) {
						dayTotalsMap = centerMap.get("dayTotals");
						dateMap = dayTotalsMap.get("TOT");
						supplyMap = dateMap.get("TOT");
			
						totProductMap = supplyMap.get("TOT");
						unitCentersMilkBillAbstMap["totQtyKgs"] = totProductMap.get("qtyKgs");
						unitCentersMilkBillAbstMap["totQtyLtrs"] = totProductMap.get("qtyLtrs");
						unitCentersMilkBillAbstMap["totKgFat"] = totProductMap.get("kgFat");
						unitCentersMilkBillAbstMap["totKgSnf"] = totProductMap.get("kgSnf");
						
						routeWiseMilkBillsAbstmap["totQtyKgs"] += totProductMap.get("qtyKgs");
						routeWiseMilkBillsAbstmap["totQtyLtrs"] += totProductMap.get("qtyLtrs");
						routeWiseMilkBillsAbstmap["totKgFat"] += totProductMap.get("kgFat");
						routeWiseMilkBillsAbstmap["totKgSnf"] += totProductMap.get("kgSnf");
						
						totPriceForCenter = totProductMap.get("price");
						tipAmount = 0;
						//comm And cartage 
						billingValues = ProcurementReports.getProcurementBillingValues(dctx , [userLogin: userLogin ,customTimePeriodId: customTimePeriodId, facilityId: center.facilityId]);
						billingVal = billingValues.get("FacilityBillingMap");
						centerBillValue =[:];
						if(billingVal){
							centerBillValue = billingVal.get(center.facilityId);
						}
						
						procurementProductList.each { procProd ->
							ProdCartageComsnMap =[:];
							if(UtilValidate.isNotEmpty(centerBillValue)){
								ProdCartageComsnMap = centerBillValue.get(procProd.productId);
							}
							productMap = supplyMap.get(procProd.productName);
							unitCentersMilkBillAbstMap[procProd.brandName+"QtyKgs"] = productMap.get("qtyKgs");
							unitCentersMilkBillAbstMap[procProd.brandName+"QtyLtrs"] = productMap.get("qtyLtrs");
							unitCentersMilkBillAbstMap[procProd.brandName+"KgFat"] = productMap.get("kgFat");
							unitCentersMilkBillAbstMap[procProd.brandName+"KgSnf"] = productMap.get("kgSnf");
							unitCentersMilkBillAbstMap[procProd.brandName+"Price"] = productMap.get("price");
							
							if(ProdCartageComsnMap){
								unitCentersMilkBillAbstMap[procProd.brandName+"commAmt"] = ProdCartageComsnMap.get("commAmt");
								unitCentersMilkBillAbstMap[procProd.brandName+"cartage"] = ProdCartageComsnMap.get("cartage");
							}				
							
							
							routeWiseMilkBillsAbstmap[procProd.brandName+"QtyKgs"] += productMap.get("qtyKgs");
							routeWiseMilkBillsAbstmap[procProd.brandName+"QtyLtrs"] += productMap.get("qtyLtrs");
							routeWiseMilkBillsAbstmap[procProd.brandName+"KgFat"] += productMap.get("kgFat");
							routeWiseMilkBillsAbstmap[procProd.brandName+"KgSnf"] += productMap.get("kgSnf");
							routeWiseMilkBillsAbstmap[procProd.brandName+"Price"] += productMap.get("price");
							
							inMap = [:];
							inMap.put("userLogin",context.userLogin);
							inMap.put("facilityId",center.facilityId);
							inMap.put("fatPercent", BigDecimal.ZERO);
							inMap.put("snfPercent", BigDecimal.ZERO);
							inMap.put("productId",procProd.productId);
							Map priceChart = PriceServices.getProcurementProductPrice(dctx,inMap);
							useTotalSolids = priceChart.get("useTotalSolids");
							
							//tip for center
							inputFacRateAmt.put("facilityId", center.facilityId);
							inputFacRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
							inputFacRateAmt.put("productId", procProd.productId);
							unitRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
							tempTipAmt = 0;
							if("Y".equals(useTotalSolids)){
								tempTipAmt = ((productMap.get("kgFat")-productMap.get("zeroKgFat"))+(productMap.get("kgSnf")-productMap.get("zeroKgSnf")))*unitRateAmount.rateAmount;
							}else{
								tempTipAmt = (productMap.get("kgFat")-productMap.get("zeroKgFat"))*unitRateAmount.rateAmount;
							}
							tipAmount += tempTipAmt;
							unitCentersMilkBillAbstMap[procProd.brandName+"tipAmount"] = tempTipAmt;
							unitGrndTot[procProd.brandName+"tipAmount"] +=  tempTipAmt;
							routeWiseMilkBillsAbstmap[procProd.brandName+"tipAmount"] +=  tempTipAmt;
						
						}
						unitCentersMilkBillAbstMap["TOTtipAmount"] = tipAmount;
						unitGrndTot["TOTtipAmount"] +=  unitCentersMilkBillAbstMap.get("TOTtipAmount");
						routeWiseMilkBillsAbstmap["TOTtipAmount"] +=  unitCentersMilkBillAbstMap.get("TOTtipAmount");
					}
					
					//comm And cartage
					billingValues = ProcurementReports.getProcurementBillingValues(dctx , [userLogin: userLogin ,customTimePeriodId: customTimePeriodId, facilityId: center.facilityId]);
					billingVal = billingValues.get("FacilityBillingMap");
					if (UtilValidate.isNotEmpty(billingVal)) {
						billingFac = billingVal.get(center.facilityId);
						billingTot = billingFac.get("tot");
						billingComm = billingTot.get("commAmt");
						billingCartage = billingTot.get("cartage");
						unitCentersMilkBillAbstMap["commAmt"] = billingComm;
						unitCentersMilkBillAbstMap["cartage"] = billingCartage;
						
						unitGrndTot["cartage"] += billingCartage;
						routeWiseMilkBillsAbstmap["commAmt"] += billingComm;
						routeWiseMilkBillsAbstmap["cartage"] += billingCartage;
					}
					unitGrndTot["commAmt"]= opCost;			
					//Gross Amount = BmAmount + cmAmount + comm + Cartage;
					unitCentersMilkBillAbstMap["grossAmount"] = totPriceForCenter+unitCentersMilkBillAbstMap.get("cartage")+unitCentersMilkBillAbstMap.get("commAmt");
					unitGrndTot["grossAmount"] += unitCentersMilkBillAbstMap.get("grossAmount");
					unitGrndTot["grossAmount"] = unitGrndTot.get("grossAmount")+ opCost;
					routeWiseMilkBillsAbstmap["grossAmount"] += unitCentersMilkBillAbstMap.get("grossAmount");
							
					//Adjustments
					if(UtilValidate.isNotEmpty(unitCenterWiseAdjustments)){
						adjustmentsTypeValues = unitCenterWiseAdjustments.get(center.facilityId);
						if(adjustmentsTypeValues !=null){
							adjustmentsTypeValues.each{ adjustmentValues ->
							   if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
								   additionsList = adjustmentValues.getValue();
								   additionsList.each{ additionValues ->
									  // unitCentersMilkBillAbstMap["AddnTot"] += additionValues.getValue();
								   }
							   }else{
								   deductionsList = adjustmentValues.getValue();
								   deductionsList.each{ deductionValues ->
									   unitCentersMilkBillAbstMap[deductionValues.getKey()] = deductionValues.getValue();
									   
									   if(UtilValidate.isEmpty(unitGrndTot.get(deductionValues.getKey()))){
									   unitGrndTot[deductionValues.getKey()] = deductionValues.getValue();
									   routeWiseMilkBillsAbstmap[deductionValues.getKey()] = deductionValues.getValue();
									   } else {
									   unitGrndTot[deductionValues.getKey()] += deductionValues.getValue();
									   routeWiseMilkBillsAbstmap[deductionValues.getKey()] += deductionValues.getValue();
									   }
									   
									   unitGrndTot["DednsTot"] += deductionValues.getValue();
									   routeWiseMilkBillsAbstmap["DednsTot"] += deductionValues.getValue();
									   unitCentersMilkBillAbstMap["DednsTot"] += deductionValues.getValue();
								   }
							   }
							}
						 }
					}//check for unitCenterWiseAdjustments for the given time period
					
						
					
					//netAmount = Gross-totDedns
					if(UtilValidate.isNotEmpty(unitCentersMilkBillAbstMap.get("grossAmount"))){
						unitCentersMilkBillAbstMap["netAmount"] = unitCentersMilkBillAbstMap.get("grossAmount") - unitCentersMilkBillAbstMap.get("DednsTot");
					}
					unitGrndTot["netAmount"] = unitGrndTot.get("grossAmount")-unitGrndTot.get("DednsTot");
					routeWiseMilkBillsAbstmap["netAmount"] += unitCentersMilkBillAbstMap.get("netAmount");
					
					//Rnd Net
					unitCentersMilkBillAbstMap["netRndAmount"] = Math.round(unitCentersMilkBillAbstMap.get("netAmount"));
					unitGrndTot["netRndAmount"] = Math.round(unitGrndTot.get("netAmount"));
					routeWiseMilkBillsAbstmap["netRndAmount"] = Math.round(routeWiseMilkBillsAbstmap.get("netAmount"));
					
					if (!(UtilValidate.isEmpty(unitCentersMilkBillAbstMap.get("totQtyKgs")))) {
						unitMilkBillAbstMap[center.facilityCode] = unitCentersMilkBillAbstMap;
						routeMilkBillAbstMap[center.facilityId] = unitCentersMilkBillAbstMap;
					}
				}
			}
			
			if (!(UtilValidate.isEmpty(routeWiseMilkBillsAbstmap.get("totQtyKgs")))) {
				routeMilkBillAbstMap[route.facilityId] = routeWiseMilkBillsAbstmap;
			}
			
		}//route
		
		//for center order in unitWiseBillsABstract
		unitMilkBillAbstTempMap = [:];
		SortedSet keys = new TreeSet(unitMilkBillAbstMap.keySet());
		for(key in keys) {
		   value = unitMilkBillAbstMap.get(key);
		   unitMilkBillAbstTempMap[key] = value;
		}
		
		unitMilkBillAbstTempMap["Total"] = unitGrndTot;
		routeMilkBillAbstMap["Total"] = unitGrndTot;
		context.put("unitMilkBillAbstMap",unitMilkBillAbstTempMap);//For Center Wise And Unit Wise 
		context.put("routeMilkBillAbstMap",routeMilkBillAbstMap);//for Route Wise Bills Abstract
		context.put("unitGrndTot",unitGrndTot);//for bankAbstract
		Map unitAbsCsvMap = FastMap.newInstance();
		unitAbsCsvMap.put("abstract",unitMilkBillAbstMap);
		unitAbsCsvMap.put("opcost",unitGrndTot["opCost"]);
		return unitAbsCsvMap;
}// function

customTimePeriodId = parameters.customTimePeriodId;
customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);


if(parameters.unitId){
	populateUnitBillAbstract(parameters.unitId , parameters.customTimePeriodId);
}else{

		MPABSFoxproCsv =[];
		purchaseTimeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SUPPLY_TYPE"), null, null, null, true);
		context.put("purchaseTimeList",purchaseTimeList);		
		if(parameters.shedId){
			shedDetails = delegator.findOne("Facility", [facilityId : parameters.shedId], false);
			unitsDetailList = (List)(ProcurementNetworkServices.getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",parameters.shedId))).get("unitsDetailList");
			facilityFinaccountMap =[:];
			facilityFinaccountMap = (ProcurementNetworkServices.getShedFacilityFinAccount(dctx, UtilMisc.toMap("facilityId",parameters.shedId ))).get("facAccntsMap");
			for( GenericValue unitDetail : unitsDetailList){
				unitId =  unitDetail.facilityId;
				unitCode =  unitDetail.facilityCode;
				periodTotals =[:];
				periodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: dayBegin , thruDate: dayEnd , facilityId: unitId ,includeCenterTotals: true]);
				centerWiseTots = periodTotals.get("centerWiseTotals");
				unitBillAbsCsv = populateUnitBillAbstract(unitId , customTimePeriodId);
				tempBillValueCsv = unitBillAbsCsv.get("abstract");
				for(Map.Entry centerEntry : tempBillValueCsv.entrySet()){
					
					centerCode = centerEntry.getKey();
					centerValue = 	centerEntry.getValue();
					int countFlag =0;					
					centerWiseDayTotals = centerWiseTots.get(centerValue.centerId);					
					centerAmPmMap = (centerWiseDayTotals["dayTotals"])["TOT"];					
					procurementProductList.each { procProd ->
						if(centerValue && centerValue.get(procProd.brandName+"QtyLtrs")){
							countFlag +=1;
							Map tempCenterMap = FastMap.newInstance();
							// populate all deductions with 0 values
							tempCenterMap.putAll(initAdjMap);
							tempCenterMap.put("GRSDED", 0);
							tempCenterMap.putAll(centerValue);
							if(countFlag ==1){
								tempCenterMap.put("GRSDED", centerValue.get("DednsTot"));
								
							}else{
							    tempCenterMap.putAll(initAdjMap);
							}
							tempCenterMap.put("MCCTYP","1");
							if(Integer.parseInt(centerCode) >= 300){
								tempCenterMap.put("MCCTYP","2");
							}
							tempCenterMap.put("DIST", "MBN");
							tempCenterMap.put("UCODE", unitCode);
							tempCenterMap.put("CCODE", centerCode);
							tempCenterMap.put("BDATE", fromDateTime);
							tempCenterMap.put("LDATE", thruDateTime);
							tempCenterMap.put("TYPMLK", procProd.brandName);
							tempCenterMap.put("MLKLTS", centerValue.get(procProd.brandName+"QtyLtrs"));
							tempCenterMap.put("MLKKGS",  centerValue.get(procProd.brandName+"QtyKgs"));
							tempCenterMap.put("MLKAMT",  centerValue.get(procProd.brandName+"Price"));
							tempCenterMap.put("COMSN",  centerValue.get(procProd.brandName+"commAmt"));
							tempCenterMap.put("CART", centerValue.get(procProd.brandName+"cartage"));
							tempCenterMap.put("GRSAMT", (centerValue.get(procProd.brandName+"Price")+centerValue.get(procProd.brandName+"commAmt")+centerValue.get(procProd.brandName+"cartage")));
							
							tempCenterMap.put("NETAMT", (tempCenterMap.get("GRSAMT")-tempCenterMap.get("GRSDED")));
							tempCenterMap.put("KGFAT", centerValue.get(procProd.brandName+"KgFat"));
							tempCenterMap.put("KGSNF", centerValue.get(procProd.brandName+"KgSnf"));
							tempCenterMap.put("SOLIDS", tempCenterMap.get("KGFAT")+tempCenterMap.get("KGSNF"));
							tempCenterMap.put("TIP",  centerValue.get(procProd.brandName+"tipAmount"));
							tempCenterMap.put("MISCADD",  0);
							if(facilityFinaccountMap.get(centerValue.get("centerId"))){
								tempCenterMap.put("BANO",  (facilityFinaccountMap.get(centerValue.get("centerId"))).finAccountCode);
								tempCenterMap.put("GBCODE",  (facilityFinaccountMap.get(centerValue.get("centerId"))).gbCode);
								tempCenterMap.put("BCODE",  (facilityFinaccountMap.get(centerValue.get("centerId"))).bCode);
							}
							
							
							tempCenterMap.put("CURDLTS", 0);
							tempCenterMap.put("PTCCURD", 0);
							tempCenterMap.put("SOURKGS", 0);
							if(centerAmPmMap){
								productTotAMPM = (centerAmPmMap.TOT).get(procProd.productName);
								if(productTotAMPM){
									tempCenterMap.put("CURDLTS", productTotAMPM.cQtyLtrs);
									tempCenterMap.put("PTCCURD", productTotAMPM.ptcQtyKgs );
									tempCenterMap.put("SOURKGS", (productTotAMPM.sQtyLtrs)*1.03);
								}
							}							
							
							for(GenericValue purchaseTime : purchaseTimeList){
								typeMap= centerAmPmMap.get(purchaseTime.enumId);
								if(purchaseTime.enumId == "AM"){
									tempCenterMap.put("MORLTS", (typeMap.get(procProd.productName)).qtyLtrs);										
								}
								if(purchaseTime.enumId == "PM"){
									 tempCenterMap.put("EVELTS", (typeMap.get(procProd.productName)).qtyLtrs);											
								}
									
							}
							MPABSFoxproCsv.add(tempCenterMap);							
						} // end of center value
					
					}
				}
				
				if(unitBillAbsCsv.opcost && unitBillAbsCsv.opcost >0){			
				
						opcostMap =[:];
						opcostMap.putAll(initAdjMap);
						opcostMap.put("DIST", "MBN");
						opcostMap.put("UCODE", unitCode);
						opcostMap.put("CCODE", "300");
						opcostMap.put("BDATE", fromDateTime);
						opcostMap.put("LDATE", thruDateTime);
						
						opcostMap.put("COMSN", 0);
						opcostMap.put("CART", 0);
						opcostMap.put("GRSAMT", 0);
						opcostMap.put("CURDLTS", 0);
						opcostMap.put("PTCCURD", 0);
						opcostMap.put("SOURKGS", 0);
						opcostMap.put("MORLTS", 0);
						opcostMap.put("EVELTS", 0);
						opcostMap.put("NETAMT", 0);
						opcostMap.put("KGFAT", 0);
						opcostMap.put("KGSNF", 0);
						opcostMap.put("SOLIDS", 0);
						opcostMap.put("TIP",  0);
						opcostMap.put("MISCADD",  0);
						opcostMap.put("MLKLTS", 0);
						opcostMap.put("MLKKGS", 0);
						opcostMap.put("KGFAT", 0);
						opcostMap.put("KGSNF", 0);
						opcostMap.put("SOLIDS", 0);
						opcostMap.put("TIP",  0);
						opcostMap.put("MISCADD",  0);
						opcostMap.put("MCCTYP","2");
						if(unitBillAbsCsv.opcost){
							opcostMap.put("COMSN",  unitBillAbsCsv.opcost);
							opcostMap.put("GRSAMT",  unitBillAbsCsv.opcost);
							opcostMap.put("NETAMT",  unitBillAbsCsv.opcost);
						}
						
						shedCode = shedDetails.facilityCode;						
						facilityInMap = [:];
						facilityInMap.put("shedCode", shedCode);
						facilityInMap.put("unitCode", unitCode);
						facilityInMap.put("centerCode", "300");
						facilityInMap.put("userLogin",context.userLogin);
						facilityId = null;
						if(centerCode!=null&&unitCode!=null){
								agentFacility = ProcurementNetworkServices.getAgentFacilityByShedCode(dctx ,facilityInMap);
								if(agentFacility.get("agentFacility")==null){
									context.errorMessage = "No Region found";
									return;
								}else{
									facilityId = agentFacility.get("agentFacility").get("facilityId");
									categoryTypeEnum = agentFacility.get("agentFacility").get("categoryTypeEnum");
								}
						}		
						if(facilityFinaccountMap.get(facilityId)){
							opcostMap.put("BANO",  (facilityFinaccountMap.get(facilityId)).finAccountCode);
							opcostMap.put("GBCODE",  (facilityFinaccountMap.get(facilityId)).gbCode);
							opcostMap.put("BCODE",  (facilityFinaccountMap.get(facilityId)).bCode);
						}										 
						MPABSFoxproCsv.add(opcostMap);
				}		
			}
		}
		
  context.put("MPABSFoxproCsv", MPABSFoxproCsv);

}


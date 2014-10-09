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

result = ServiceUtil.returnSuccess();
//commAmtGtot = 0;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	  	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
	}
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());
context.put("procurementProductList",procurementProductList);

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
			context.put("shedId",facilityDetail.parentFacilityId);
		}else{
			return;
		}
		
		Map inputFacRateAmt = UtilMisc.toMap("userLogin", userLogin);
		inputFacRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
		inputFacRateAmt.put("rateCurrencyUomId", "INR");
		
		adjustmentDedTypes = [:];
		orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
		orderAdjItemsList = UtilMisc.sortMaps(orderAdjItemsList, UtilMisc.toList("sequenceNum"));
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
		
		
		
		Map productDetailsMap = FastMap.newInstance();
		procurementProductList.each { procProd ->
			productDetailsMap.put(procProd.get("productName"), procProd.getAt("productId"));
		}
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
		unitGrndTot["opCost"] = 0;
		opCost =0;
		if (UtilValidate.isNotEmpty(unitGrndValuesTot)) {
			dayTotalsMap = unitGrndValuesTot.get("dayTotals");
			dateMap = dayTotalsMap.get("TOT");
			inputFacRateAmt =[:];
			inputFacRateAmt.put("userLogin", userLogin);
			inputFacRateAmt.put("rateCurrencyUomId", "INR");
			inputFacRateAmt.put("facilityId",unitId);
			opCost =0;
			for(key in dateMap.keySet()){
				if(!key.equalsIgnoreCase("TOT")){
					inputFacRateAmt.put("supplyTypeEnumId", key);
					tempMap = dateMap.get(key);
					if(UtilValidate.isNotEmpty(tempMap)){
						for( productKey in tempMap.keySet()){
							if(!productKey.equalsIgnoreCase("TOT")){
								BigDecimal qtyLtrs = BigDecimal.ZERO;
								BigDecimal totalSolids=BigDecimal.ZERO;
								String productName = productKey;
								inputFacRateAmt.put("rateTypeId", "PROC_OP_COST");
								inputFacRateAmt.put("productId", productDetailsMap.get(productName));
								inputFacRateAmt.put("slabAmount",facilityDetail.facilitySize);
								Map<String, Object> opCostAmtMap = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
								BigDecimal opCostRate = BigDecimal.ZERO;
								String uomId = "VLIQ_L";
								if(ServiceUtil.isSuccess(opCostAmtMap)){
									opCostRate = (BigDecimal)opCostAmtMap.get("rateAmount");
									if(UtilValidate.isNotEmpty(opCostAmtMap.get("uomId"))){
										uomId = (String) opCostAmtMap.get("uomId");
									}
								}
								if(uomId.equalsIgnoreCase("VLIQ_KGFAT")){
									totalSolids = totalSolids+ ( tempMap.get(productKey).get("kgFat"))+( tempMap.get(productKey).get("sKgFat"));
									uomId = "VLIQ_TS";
								}else{
									totalSolids = totalSolids+ ( tempMap.get(productKey).get("kgFat"))+( tempMap.get(productKey).get("sKgFat"))+( tempMap.get(productKey).get("kgSnf"));
								}
								
								qtyLtrs = qtyLtrs+( tempMap.get(productKey).get("qtyLtrs"))+( tempMap.get(productKey).get("sQtyLtrs"));
								opCost = opCost+(ProcurementNetworkServices.calculateProcOPCost(dctx,UtilMisc.toMap("uomId", uomId,"totalSolids",totalSolids,"qtyLtrs",qtyLtrs,"opCostRate",opCostRate)));
							}
						}
					}
					
				}
			}
			
			unitGrndTot["opCost"] = opCost;
			
			unitGrndTot["opCostRnd"] = Math.round(opCost);
			
			
			supplyMap = dateMap.get("TOT");
			
			totProductMap = supplyMap.get("TOT");
			unitGrndTot["totQtyKgs"] = totProductMap.get("qtyKgs")+(totProductMap.get("sQtyLtrs")*1.03);
			unitGrndTot["totQtyLtrs"] = totProductMap.get("qtyLtrs")+totProductMap.get("sQtyLtrs");
			unitGrndTot["totKgFat"] = totProductMap.get("kgFat");
			unitGrndTot["totKgSnf"] = totProductMap.get("kgSnf");
			//unitGrndTot["totPrice"] = totProductMap.get("price");
			
			procurementProductList.each { procProd ->
				productMap = supplyMap.get(procProd.productName);
				unitGrndTot[procProd.brandName+"QtyKgs"] = productMap.get("qtyKgs")+(productMap.get("sQtyLtrs")*1.03);
				unitGrndTot[procProd.brandName+"QtyLtrs"] = productMap.get("qtyLtrs")+productMap.get("sQtyLtrs");
				unitGrndTot[procProd.brandName+"KgFat"] = productMap.get("kgFat");
				unitGrndTot[procProd.brandName+"KgSnf"] = productMap.get("kgSnf");
				unitGrndTot[procProd.brandName+"Price"] = productMap.get("price")+productMap.get("sPrice");
				unitGrndTot[procProd.brandName+"tipAmount"] = 0;
			}
			
		}
		
		unitGrndTot["commAmt"] = 0;
		unitGrndTot["cartage"] = 0;
		orderAdjItemsList.each { orderAdj ->
			unitGrndTot[orderAdj.orderAdjustmentTypeId] = 0;
		}
		unitGrndTot["DednsTot"] = 0;
		unitGrndTot["AddnTot"] =0;
		unitGrndTot["netAmount"] = 0;
		unitGrndTot["grossAmount"] = 0;
		unitGrndTot["TOTtipAmount"] = 0;
		unitRoutesList = ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",unitId));
		routesDetailsList = unitRoutesList.get("routesDetailList");
		unitGrndTot["netCenterRndUnitAmount"] = 0;
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
			routeWiseMilkBillsAbstmap["AddnTot"] =0;
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
				unitCentersMilkBillAbstMap["AddnTot"] =0;
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
						unitCentersMilkBillAbstMap["totQtyKgs"] = totProductMap.get("qtyKgs")+(totProductMap.get("sQtyLtrs")*1.03);
						unitCentersMilkBillAbstMap["totQtyLtrs"] = totProductMap.get("qtyLtrs")+totProductMap.get("sQtyLtrs");
						unitCentersMilkBillAbstMap["totKgFat"] = totProductMap.get("kgFat");
						unitCentersMilkBillAbstMap["totKgSnf"] = totProductMap.get("kgSnf");
						
						routeWiseMilkBillsAbstmap["totQtyKgs"] += totProductMap.get("qtyKgs")+(totProductMap.get("sQtyLtrs")*1.03);
						routeWiseMilkBillsAbstmap["totQtyLtrs"] += totProductMap.get("qtyLtrs")+totProductMap.get("sQtyLtrs");
						routeWiseMilkBillsAbstmap["totKgFat"] += totProductMap.get("kgFat");
						routeWiseMilkBillsAbstmap["totKgSnf"] += totProductMap.get("kgSnf");
						
						totPriceForCenter = totProductMap.get("price")+totProductMap.get("sPrice");
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
							unitCentersMilkBillAbstMap[procProd.brandName+"QtyKgs"] = productMap.get("qtyKgs")+(productMap.get("sQtyLtrs")*1.03);
							unitCentersMilkBillAbstMap[procProd.brandName+"QtyLtrs"] = productMap.get("qtyLtrs")+productMap.get("sQtyLtrs");
							unitCentersMilkBillAbstMap[procProd.brandName+"KgFat"] = productMap.get("kgFat");
							unitCentersMilkBillAbstMap[procProd.brandName+"KgSnf"] = productMap.get("kgSnf");
							unitCentersMilkBillAbstMap[procProd.brandName+"Price"] = productMap.get("price")+productMap.get("sPrice");
							
							if(ProdCartageComsnMap){
								unitCentersMilkBillAbstMap[procProd.brandName+"commAmt"] = ProdCartageComsnMap.get("commAmt");
								unitCentersMilkBillAbstMap[procProd.brandName+"cartage"] = ProdCartageComsnMap.get("cartage");
							}				
							
							
							routeWiseMilkBillsAbstmap[procProd.brandName+"QtyKgs"] += productMap.get("qtyKgs")+(productMap.get("sQtyLtrs")*1.03);
							routeWiseMilkBillsAbstmap[procProd.brandName+"QtyLtrs"] += productMap.get("qtyLtrs")+productMap.get("sQtyLtrs");
							routeWiseMilkBillsAbstmap[procProd.brandName+"KgFat"] += productMap.get("kgFat");
							routeWiseMilkBillsAbstmap[procProd.brandName+"KgSnf"] += productMap.get("kgSnf");
							routeWiseMilkBillsAbstmap[procProd.brandName+"Price"] += productMap.get("price")+productMap.get("sPrice");
							
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
							BigDecimal tempTipAmt = BigDecimal.ZERO;
							if("Y".equals(useTotalSolids)){
								tempTipAmt = ((productMap.get("kgFat")-productMap.get("zeroKgFat"))+(productMap.get("kgSnf")-productMap.get("zeroKgSnf")))*unitRateAmount.rateAmount;
							}else{
								tempTipAmt = (productMap.get("kgFat")-productMap.get("zeroKgFat"))*unitRateAmount.rateAmount;
							}
							//tempTipAmt = tempTipAmt.setScale(2,BigDecimal.ROUND_HALF_EVEN);
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
						unitGrndTot["commAmt"] += billingComm;
						unitGrndTot["cartage"] += billingCartage;
						routeWiseMilkBillsAbstmap["commAmt"] += billingComm;
						routeWiseMilkBillsAbstmap["cartage"] += billingCartage;
					}
					//unitGrndTot["commAmt"]= unitGrndTot["opCost"];			
					//Gross Amount = BmAmount + cmAmount + comm + Cartage;
						
					//Adjustments
					if(UtilValidate.isNotEmpty(unitCenterWiseAdjustments)){
						adjustmentsTypeValues = unitCenterWiseAdjustments.get(center.facilityId);
						if(adjustmentsTypeValues !=null){
							adjustmentsTypeValues.each{ adjustmentValues ->
							   if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
								   additionsList = adjustmentValues.getValue();
								   additionsList.each{ additionValues ->
								   unitCentersMilkBillAbstMap[additionValues.getKey()] = additionValues.getKey();
								   
								   if(UtilValidate.isEmpty(unitGrndTot.get(additionValues.getKey()))){
										   unitGrndTot[additionValues.getKey()] = additionValues.getValue();
										   routeWiseMilkBillsAbstmap[additionValues.getKey()] = additionValues.getValue();
									   } else {
										    unitGrndTot[additionValues.getKey()] += additionValues.getValue();
										    routeWiseMilkBillsAbstmap[additionValues.getKey()] += additionValues.getValue();
									   }
									   
									   unitGrndTot["AddnTot"] += additionValues.getValue();
									   routeWiseMilkBillsAbstmap["AddnTot"] += additionValues.getValue();
									   unitCentersMilkBillAbstMap["AddnTot"] += additionValues.getValue();
								   
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
					unitCentersMilkBillAbstMap["grossAmount"] = totPriceForCenter+unitCentersMilkBillAbstMap.get("cartage")+unitCentersMilkBillAbstMap.get("commAmt")+unitCentersMilkBillAbstMap.get("AddnTot");
					unitGrndTot["grossAmount"] += unitCentersMilkBillAbstMap.get("grossAmount");
					unitGrndTot["grossAmount"] = unitGrndTot.get("grossAmount");
					context.put("unitGrndTot",unitGrndTot);//for bankAbstract
					routeWiseMilkBillsAbstmap["grossAmount"] += unitCentersMilkBillAbstMap.get("grossAmount");
					
					//netAmount = Gross-totDedns
					if(UtilValidate.isNotEmpty(unitCentersMilkBillAbstMap.get("grossAmount"))){
						unitCentersMilkBillAbstMap["netAmount"] = unitCentersMilkBillAbstMap.get("grossAmount")- unitCentersMilkBillAbstMap.get("DednsTot");
					}
					/*unitGrndTot["netAmount"] = unitGrndTot.get("grossAmount")-unitGrndTot.get("DednsTot");*/
					routeWiseMilkBillsAbstmap["netAmount"] += unitCentersMilkBillAbstMap.get("netAmount");
					
					//Rnd Net
					unitCentersMilkBillAbstMap["netRndAmount"] = Math.round(unitCentersMilkBillAbstMap.get("netAmount"));
					unitGrndTot["netCenterRndUnitAmount"] += Math.round(unitCentersMilkBillAbstMap.get("netAmount"));
					/*unitGrndTot["netRndAmount"] = Math.round(unitGrndTot.get("netAmount"));*/
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
		unitGrndTot["grossAmount"] =  unitGrndTot.get("grossAmount")+opCost;
		unitGrndTot["netAmount"] = unitGrndTot.get("grossAmount")-unitGrndTot.get("DednsTot");
		unitGrndTot["netRndAmount"] = Math.round(unitGrndTot.get("netAmount"));
		unitMilkBillAbstTempMap["Total"] = unitGrndTot;
		routeMilkBillAbstMap["Total"] = unitGrndTot;
		context.put("unitMilkBillAbstMap",unitMilkBillAbstTempMap);//For Center Wise And Unit Wise 
		context.put("routeMilkBillAbstMap",routeMilkBillAbstMap);//for Route Wise Bills Abstract
		/*context.put("unitGrndTot",unitGrndTot);*///for bankAbstract
		
		Map unitAbsCsvMap = FastMap.newInstance();
		unitAbsCsvMap.put("abstract",unitMilkBillAbstMap);
		unitAbsCsvMap.put("opcost",unitGrndTot["opCost"]);
		unitAbsCsvMap.put("netRndAmount",unitGrndTot["netRndAmount"]);
		unitAbsCsvMap.put("netRndAmountWithOp",unitGrndTot["netCenterRndUnitAmount"]+Math.round(unitGrndTot["opCost"]));
		context.put("unitTotals",unitAbsCsvMap);
		return unitAbsCsvMap;
}// function

customTimePeriodId = parameters.customTimePeriodId;
if(UtilValidate.isEmpty(customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}

customTimePeriod =delegator.findOne("CustomTimePeriod",[customTimePeriodId : customTimePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriod)){
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
}
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);

if(UtilValidate.isNotEmpty(parameters.unitId) && UtilValidate.isEmpty(parameters.reportTypeFlag)){
	populateUnitBillAbstract(parameters.unitId , parameters.customTimePeriodId);
}else{
		MPABSFoxproCsv =[];
		purchaseTimeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SUPPLY_TYPE"), null, null, null, true);
		context.put("purchaseTimeList",purchaseTimeList);		
		Map netPayableMap = FastMap.newInstance();
		Map comparingAbstractEntityMap=FastMap.newInstance();
		if(parameters.shedId){
			shedDetails = delegator.findOne("Facility", [facilityId : parameters.shedId], false);
			List unitsDetailList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(parameters.unitId)){
					unitFacilityDetails = delegator.findOne("Facility", [facilityId : parameters.unitId], false);
					unitsDetailList.add(unitFacilityDetails);
				}else{
					//unitsDetailList.addAll((List)(ProcurementNetworkServices.getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",parameters.shedId))).get("unitsDetailList"));
					unitsDetailList.addAll((List)(ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId])).get("customTimePeriodUnitsDetailList"));
				}
			facilityFinaccountMap =[:];
			facilityFinaccountMap = (ProcurementNetworkServices.getShedFacilityFinAccount(dctx, UtilMisc.toMap("facilityId",parameters.shedId ))).get("facAccntsMap");
			for( GenericValue unitDetail : unitsDetailList){
				unitId =  unitDetail.facilityId;
				unitCode =  unitDetail.facilityCode;
				periodTotals =[:];
				periodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: dayBegin , thruDate: dayEnd , facilityId: unitId ,includeCenterTotals: true]);
				centerWiseTots = periodTotals.get("centerWiseTotals");
				unitBillAbsCsv = populateUnitBillAbstract(unitId , customTimePeriodId);
				// for unitMilkBill NetPayable
				netPayableMap.put(unitId,unitBillAbsCsv.get("netRndAmountWithOp"));
				if(UtilValidate.isNotEmpty(unitBillAbsCsv)){
					comparingAbstractEntityMap.put(unitId, unitBillAbsCsv);
				}
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
							tempCenterMap.put("GRSADD", 0);
							tempCenterMap.putAll(centerValue);
							if(countFlag ==1){
								tempCenterMap.put("GRSDED", centerValue.get("DednsTot"));
								tempCenterMap.put("GRSADD", centerValue.get("AddnTot"));
								
							}else{
							    tempCenterMap.putAll(initAdjMap);
							}
							tempCenterMap.put("MCCTYP","1");
							if(Integer.parseInt(centerCode) >= 300){
								tempCenterMap.put("MCCTYP","2");
							}
							tempCenterMap.put("DIST", "MBN");
							if(UtilValidate.isNotEmpty(unitDetail.district)){
									tempCenterMap.put("DIST",unitDetail.district);
								}
							tempCenterMap.put("UCODE", unitCode);
							tempCenterMap.put("CCODE", centerCode);
							tempCenterMap.put("centerId", centerValue.centerId);
							tempCenterMap.put("BDATE", fromDateTime);
							tempCenterMap.put("LDATE", thruDateTime);
							tempCenterMap.put("TYPMLK", procProd.brandName);
							tempCenterMap.put("MLKLTS", centerValue.get(procProd.brandName+"QtyLtrs"));
							tempCenterMap.put("MLKKGS",  centerValue.get(procProd.brandName+"QtyKgs"));
							tempCenterMap.put("MLKAMT",  centerValue.get(procProd.brandName+"Price"));
							tempCenterMap.put("COMSN",  centerValue.get(procProd.brandName+"commAmt"));
							tempCenterMap.put("CART", centerValue.get(procProd.brandName+"cartage"));
							tempCenterMap.put("GRSAMT", (centerValue.get(procProd.brandName+"Price")+centerValue.get(procProd.brandName+"commAmt")+centerValue.get(procProd.brandName+"cartage"))+tempCenterMap.get("GRSADD"));
							
							tempCenterMap.put("NETAMT", (tempCenterMap.get("GRSAMT")-tempCenterMap.get("GRSDED")));
							tempCenterMap.put("KGFAT", centerValue.get(procProd.brandName+"KgFat"));
							tempCenterMap.put("KGSNF", centerValue.get(procProd.brandName+"KgSnf"));
							tempCenterMap.put("SOLIDS", tempCenterMap.get("KGFAT")+tempCenterMap.get("KGSNF"));
							tempCenterMap.put("TIP",  centerValue.get(procProd.brandName+"tipAmount"));
							tempCenterMap.put("MISCADD",  tempCenterMap.get("GRSADD"));
							if(facilityFinaccountMap.get(centerValue.get("centerId"))){
								tempCenterMap.put("BANO",  (facilityFinaccountMap.get(centerValue.get("centerId"))).finAccountCode);
								if(UtilValidate.isEmpty(tempCenterMap.get("BANO"))||((UtilValidate.isNotEmpty(tempCenterMap.get("BANO")))&&("0".equalsIgnoreCase((String)tempCenterMap.get("BANO"))))){
									tempCenterMap.put("BANO",  0);
									tempCenterMap.put("GBCODE", 0);
									tempCenterMap.put("BCODE",  0);
								}else{
									tempCenterMap.put("GBCODE",  (facilityFinaccountMap.get(centerValue.get("centerId"))).gbCode);
									tempCenterMap.put("BCODE",  (facilityFinaccountMap.get(centerValue.get("centerId"))).bCode);
								}
							}
							tempCenterMap.put("CURDLTS", 0);
							tempCenterMap.put("PTCCURD", 0);
							tempCenterMap.put("SOURKGS", 0);
							if(centerAmPmMap){
								productTotAMPM = (centerAmPmMap.TOT).get(procProd.productName);
								if(productTotAMPM){
									tempCenterMap.put("CURDLTS", productTotAMPM.cQtyLtrs);
									tempCenterMap.put("PTCCURD", productTotAMPM.ptcQtyLtrs );
									tempCenterMap.put("SOURKGS", (productTotAMPM.sQtyLtrs)*1.03);
								}
							}							
							
							for(GenericValue purchaseTime : purchaseTimeList){
								typeMap= centerAmPmMap.get(purchaseTime.enumId);
								if(purchaseTime.enumId == "AM"){
									tempCenterMap.put("MORLTS", ((typeMap.get(procProd.productName)).qtyLtrs)+((typeMap.get(procProd.productName)).sQtyLtrs));										
								}
								if(purchaseTime.enumId == "PM"){
									 tempCenterMap.put("EVELTS", ((typeMap.get(procProd.productName)).qtyLtrs)+((typeMap.get(procProd.productName)).sQtyLtrs));											
								}
									
							}
							MPABSFoxproCsv.add(tempCenterMap);							
						} // end of center value
					
					}
				}
				
				if(unitBillAbsCsv.opcost && unitBillAbsCsv.opcost >0){			
				
						opcostMap =[:];
						opcostMap.putAll(initAdjMap);
						opcostMap.put("DIST", " ");
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
						opcostMap.put("centerId", facilityId);
						opcostMap.put("netRndAmount",  Math.round(unitBillAbsCsv.opcost));
						if(facilityFinaccountMap.get(facilityId)){							
							opcostMap.put("BANO",  (facilityFinaccountMap.get(facilityId)).finAccountCode);
							if(UtilValidate.isEmpty(opcostMap.get("BANO"))||((UtilValidate.isNotEmpty(opcostMap.get("BANO")))&&("0".equalsIgnoreCase((String)opcostMap.get("BANO"))))){
								opcostMap.put("BANO",  0);
								opcostMap.put("GBCODE", 0);
								opcostMap.put("BCODE",  0);
							}else{
								opcostMap.put("GBCODE",  (facilityFinaccountMap.get(facilityId)).gbCode);
								opcostMap.put("BCODE",  (facilityFinaccountMap.get(facilityId)).bCode);
							}
						}										 
						MPABSFoxproCsv.add(opcostMap);
				}		
			}
		}
	context.putAt("comparingAbstractEntityMap", comparingAbstractEntityMap);
	result.comparingAbstractEntityMap = comparingAbstractEntityMap;
	context.put("netPayableMap",netPayableMap);	// this is for Milkbill netPayable	
	context.put("MPABSFoxproCsv", MPABSFoxproCsv);
}
centerWiseMap=[:];
if(UtilValidate.isNotEmpty(context.getAt("MPABSFoxproCsv"))){
	mpAbsFoxPro=context.getAt("MPABSFoxproCsv");
	MPABSFoxproCsv.each{ centerWiseValues->
		amount=0;
		if(UtilValidate.isNotEmpty(centerWiseValues.get("NETAMT"))){
			amount=centerWiseValues.get("NETAMT");
		}
		centerId=centerWiseValues.get("centerId");
		//amount=amount.setScale(0,BigDecimal.ROUND_HALF_UP);
		if(UtilValidate.isNotEmpty(centerWiseMap[centerId])){
			centerWiseMap[centerId]+=amount;
		}else{
			centerWiseMap[centerId]=amount;
		}
	}
}
if(UtilValidate.isNotEmpty(centerWiseMap)){
	for(String key in centerWiseMap.keySet()){
		BigDecimal amount = (BigDecimal) centerWiseMap.get(key);
		centerWiseMap.put(key,amount.setScale(0,BigDecimal.ROUND_HALF_UP));
	}
}
context.putAt("billsCenterWiseMap", centerWiseMap);
return result;


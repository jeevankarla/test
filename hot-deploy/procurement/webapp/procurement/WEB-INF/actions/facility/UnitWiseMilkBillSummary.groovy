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
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;


procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
if(UtilValidate.isEmpty(parameters.unitId)){
	Debug.logError("unitId Cannot Be Empty","");
	context.errorMessage = "No Unit Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.unitId ;
Map MilkBillValuesMap =[:];
Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
//getting tip amount for each product
Map tipAmtRateMap=[:];
Map commRateMap = FastMap.newInstance();
procurementProductList.each{ procProducts ->
   inputRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
   inputRateAmt.put("rateCurrencyUomId", "INR");
   inputRateAmt.put("productId", procProducts.productId);  
   inputRateAmt.put("facilityId", facilityId);
   rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
   rateAmt= rateAmount.rateAmount;
   tipAmtRateMap[procProducts.productId]=rateAmt;  
}
context.putAt("tipAmtRateMap", tipAmtRateMap);

adjustmentDedTypes = [:];
orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS ,"MILKPROC_DEDUCTIONS"),null,null,null,false);
orderAdjItemsList = UtilMisc.sortMaps(orderAdjItemsList, UtilMisc.toList("sequenceNum"));
for(int i=0;i<orderAdjItemsList.size();i++){
	orderAdjItem = orderAdjItemsList.get(i);
	adjustmentDedTypes[i] = orderAdjItem;
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
// for getting supplyType
supplyTypeList= delegator.findList("Enumeration", EntityCondition.makeCondition([enumTypeId : 'PROC_SUPPLY_TYPE']), null, ['enumId'], null, true);
supplyTypeMap=[:];

UnitWisePeriodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId: facilityId]);
Iterator unitTotalsMapItr =UnitWisePeriodTotals.entrySet().iterator();
totalqty =BigDecimal.ZERO;
milkValue =BigDecimal.ZERO;
deductionsValuesList =FastList.newInstance();
totAdditions=0;
totDeductions=0;
opCostFromBills=0;
tipFromBills=0;
cartage=0;
commission=0;
//taking opcost , cartage,tipamount and commission from procurement abstract.
unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: parameters.customTimePeriodId , unitId: facilityId]);
if(UtilValidate.isNotEmpty(unitBillAbstract)){
	unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
	unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
	if(UtilValidate.isNotEmpty(unitGrndValuesTot)){
		totProductMap = unitGrndValuesTot.get("TOT");
		if(UtilValidate.isNotEmpty(totProductMap)){
			totQtyLtrs = totProductMap.get("qtyLtrs");
			opCostFromBills=totProductMap.getAt("opCost");
			tipFromBills=totProductMap.getAt("tipAmt");
			cartage=totProductMap.getAt("cartage");
			commission=totProductMap.getAt("commissionAmount");
		}
	}
}
while (unitTotalsMapItr.hasNext()) {
	Map.Entry unitEntry = unitTotalsMapItr.next();
	Map unitValuesMap = (Map)unitEntry.getValue();
	Map unitWiseTotalValues = ((Map)((Map)((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("TOT")).get("TOT");
	
	// For Displaying AM total ltrs and PM total ltrs seperately for specified Unit.
	AMBmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Buffalo Milk").get("qtyLtrs");
	AMBmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Buffalo Milk").get("sQtyLtrs");
	AMCmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Cow Milk").get("qtyLtrs");
	AMCmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Cow Milk").get("sQtyLtrs");
	totAMLtrs = (AMBmLtrs+AMBmSQtyLtrs)+(AMCmLtrs+AMCmSQtyLtrs);
	
	totAmKgFat =  ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Cow Milk").get("sKgFat"))+((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Cow Milk").get("kgFat"))+ ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Buffalo Milk").get("sKgFat"))+((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Buffalo Milk").get("kgFat"));
	totAmKgSnf =  ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Cow Milk").get("kgSnf"))+ ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("AM").get("Buffalo Milk").get("kgSnf"));
	
	totPmKgFat =  ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Cow Milk").get("sKgFat"))+((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Cow Milk").get("kgFat"))+ ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Buffalo Milk").get("sKgFat"))+((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Buffalo Milk").get("kgFat"));
	totPmKgSnf =  ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Cow Milk").get("kgSnf"))+ ((((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Buffalo Milk").get("kgSnf"));
	
	PMBmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Buffalo Milk").get("qtyLtrs");
	PMBmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Buffalo Milk").get("sQtyLtrs");
	PMCmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Cow Milk").get("qtyLtrs");
	PMCmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Cow Milk").get("sQtyLtrs");
	totPMLtrs = (PMBmLtrs+PMBmSQtyLtrs)+(PMCmLtrs+PMCmSQtyLtrs);	
	context.put("totAMLtrs", totAMLtrs);
	context.put("totPMLtrs", totPMLtrs);
	
	totAMTotalSolids = totAmKgFat+totAmKgSnf;
	totPMTotalSolids = totPmKgFat+totPmKgSnf
	
	context.putAt("totAMTotalSolids", totAMTotalSolids);
	context.putAt("totPMTotalSolids", totPMTotalSolids);
	
	if(UtilValidate.isNotEmpty(unitWiseTotalValues)){
		totalqty = (unitWiseTotalValues.get("qtyKgs")+(unitWiseTotalValues.get("sQtyLtrs")*1.03));	
		milkValue    = (unitWiseTotalValues.get("price")+unitWiseTotalValues.get("sPrice"));
	}
	MilkBillValuesMap["totalqty"]=totalqty;
	MilkBillValuesMap["milkValue"]=milkValue;
	context.put("totalqty", totalqty);
	context.put("milkValue", milkValue);
	
	Map solidsMap = FastMap.newInstance();
	procurementProductList.each{ procProducts ->
		solidsMap.put(procProducts.brandName, 0);
	}
	Map qtyMap = FastMap.newInstance();
	supplyTypeList.each{ supplyType ->
		qtyMap.put(supplyType.enumId, 0);
	}
	
	opCost =0;
	// opCost calculation
	supplyTypeList.each{ supplyType ->
		procurementProductList.each{ procProducts ->
			String productId = procProducts.productId;
			String productName = procProducts.productName;
			qtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get(productName).get("qtyLtrs");
			sQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get(productName).get("sQtyLtrs");
			kgFat = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get(productName).get("kgFat");
			sKgFat = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get(productName).get("sKgFat");
			kgSnf = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get(productName).get("kgSnf");
			totLtrs = (qtyLtrs+sQtyLtrs);
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
			
			BigDecimal totalSolids = 0.0;
			Map inputMap = UtilMisc.toMap("userLogin", userLogin);
			inputMap.put("supplyTypeEnumId", supplyType.enumId);
			inputMap.put("facilityId", facilityId);
			inputMap.put("productId", productId);
			inputMap.put("slabAmount", facility.facilitySize);
			inputMap.put("rateTypeId", "PROC_OP_COST");
			opCostRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputMap);
			BigDecimal opCostAmount = opCostRateAmount.rateAmount;
			String uomId = "VLIQ_L";
			if(UtilValidate.isNotEmpty(opCostRateAmount.get("uomId"))){
				uomId = opCostRateAmount.get("uomId")
				}
			if(uomId.equalsIgnoreCase("VLIQ_KGFAT")){
					totalSolids = kgFat+sKgFat;
					uomId = "VLIQ_TS";
				}else{
					totalSolids = kgFat+sKgFat+kgSnf;
				}
			context.putAt("uomId", uomId);	
			String brandName = 	procProducts.brandName;
			solidsMap.put(brandName, solidsMap.get(brandName)+totalSolids);
			String suplyTypeId = supplyType.enumId;
			qtyMap.put(suplyTypeId, qtyMap.get(suplyTypeId)+totLtrs);
			//opCost = opCost+(ProcurementNetworkServices.calculateProcOPCost(dctx,UtilMisc.toMap("uomId", uomId,"totalSolids",totalSolids,"qtyLtrs",totLtrs,"opCostRate",opCostAmount)));
		}
	}
	opCost=opCostFromBills;
	context.put("supplyTypeMap", supplyTypeMap);
	context.put("qtyMap", qtyMap);
	context.put("solidsMap", solidsMap);
	context.put("opCost", opCost);
	
	Map unitWiseValues = (((Map)((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("TOT"));	
	/*cmTipAmt=0;
	bmTipAmt=0;	
	procurementProductList.each{ procProducts ->
		inMap = [:];
		inMap.put("userLogin",context.userLogin);
		inMap.put("facilityId",facilityId);
		inMap.put("fatPercent", BigDecimal.ZERO);
		inMap.put("snfPercent", BigDecimal.ZERO);
		inMap.put("priceDate", fromDate);
		inMap.put("productId",procProducts.productId);		
		Map priceChart = PriceServices.getProcurementProductPrice(dctx,inMap);
		useTotalSolids = priceChart.get("useTotalSolids");		
		if("Y".equals(useTotalSolids)){
			cmKgFat = (unitWiseValues.get(procProducts.productName).getAt("kgFat")-unitWiseValues.get(procProducts.productName).getAt("zeroKgFat"));
			cmKgSnf = (unitWiseValues.get(procProducts.productName).getAt("kgSnf")-unitWiseValues.get(procProducts.productName).getAt("zeroKgSnf"));
			totCmSolides = (cmKgFat+cmKgSnf);
			cmRate =tipAmtRateMap[procProducts.productId];
			cmTipAmt=totCmSolides*cmRate;			
		}else{
			bmKgFat = (unitWiseValues.get(procProducts.productName).getAt("kgFat")-unitWiseValues.get(procProducts.productName).getAt("zeroKgFat"));
			bmRate = tipAmtRateMap[procProducts.productId];
			bmTipAmt=bmKgFat*bmRate;			
		}		
	}*/
	//tipAmount =cmTipAmt+bmTipAmt;	
	context.putAt("tipAmount", tipFromBills);
	context.put("unitWiseValues", unitWiseValues);
	
	unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityId: facilityId]);
	/*billingValues = ProcurementReports.getProcurementBillingValues(dctx , [userLogin: userLogin ,customTimePeriodId: parameters.customTimePeriodId, facilityId: facilityId]);
	Map billingValuesMap =FastMap.newInstance();
	if(UtilValidate.isNotEmpty(billingValues)){
		billingValuesMap =billingValues.get("FacilityBillingMap");
	}
	context.putAt("billingValuesMap", billingValuesMap);*/
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
				   deductionsValuesList .add(adjustmentValues.getValue());				  
				   deductionsList = adjustmentValues.getValue();
				   deductionsList.each{ deductionValues ->
					   totDeductions += deductionValues.getValue();
				   }
			   }			   
		   }		   
	   }
   }  
}
MilkBillValuesMap["cartage"]=cartage;
MilkBillValuesMap["commission"]=commission;
MilkBillValuesMap["totAdditions"]=totAdditions;
MilkBillValuesMap["totDeductions"]=totDeductions;
context.put("MilkBillValuesMap", MilkBillValuesMap);
context.put("deductionsValuesList", deductionsValuesList);






































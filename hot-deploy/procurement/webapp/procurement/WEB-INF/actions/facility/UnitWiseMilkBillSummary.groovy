import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
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
import in.vasista.vbiz.procurement.PriceServices;


procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;

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
	PMBmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Buffalo Milk").get("qtyLtrs");
	PMBmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Buffalo Milk").get("sQtyLtrs");
	PMCmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Cow Milk").get("qtyLtrs");
	PMCmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("PM").get("Cow Milk").get("sQtyLtrs");
	totPMLtrs = (PMBmLtrs+PMBmSQtyLtrs)+(PMCmLtrs+PMCmSQtyLtrs);	
	context.put("totAMLtrs", totAMLtrs);
	context.put("totPMLtrs", totPMLtrs);
	
	if(UtilValidate.isNotEmpty(unitWiseTotalValues)){
		totalqty = (unitWiseTotalValues.get("qtyKgs")+(unitWiseTotalValues.get("sQtyLtrs")*1.03));	
		milkValue    = (unitWiseTotalValues.get("price")+unitWiseTotalValues.get("sPrice"));
	}
	MilkBillValuesMap["totalqty"]=totalqty;
	MilkBillValuesMap["milkValue"]=milkValue;
	context.put("totalqty", totalqty);
	context.put("milkValue", milkValue);
	
	supplyTypeList.each{ supplyType ->
		BmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get("Buffalo Milk").get("qtyLtrs");
		BmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get("Buffalo Milk").get("sQtyLtrs");
		CmLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get("Cow Milk").get("qtyLtrs");
		CmSQtyLtrs = (((Map)unitValuesMap.get("dayTotals")).get("TOT")).get(supplyType.enumId).get("Cow Milk").get("sQtyLtrs");
		totLtrs = (BmLtrs+BmSQtyLtrs)+(CmLtrs+CmSQtyLtrs);
		GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
		Map inputMap = UtilMisc.toMap("userLogin", userLogin);
		inputMap.put("supplyTypeEnumId", supplyType.enumId);
		inputMap.put("facilityId", facilityId);
		inputMap.put("slabAmount", facility.facilitySize);
		inputMap.put("rateTypeId", "PROC_OP_COST");
		opCostRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputMap);
		BigDecimal opCostAmount = opCostRateAmount.rateAmount;
		supplyTypeMap[supplyType.enumId]=(opCostAmount);		
	}
	context.put("supplyTypeMap", supplyTypeMap);
	Map unitWiseValues = (((Map)((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("TOT"));	
	cmTipAmt=0;
	bmTipAmt=0;	
	procurementProductList.each{ procProducts ->
		inMap = [:];
		inMap.put("userLogin",context.userLogin);
		inMap.put("facilityId",facilityId);
		inMap.put("fatPercent", BigDecimal.ZERO);
		inMap.put("snfPercent", BigDecimal.ZERO);
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
	}
	tipAmount =cmTipAmt+bmTipAmt;	
	context.putAt("tipAmount", tipAmount);
	context.put("unitWiseValues", unitWiseValues);
	
	unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityId: facilityId]);
	billingValues = ProcurementReports.getProcurementBillingValues(dctx , [userLogin: userLogin ,customTimePeriodId: parameters.customTimePeriodId, facilityId: facilityId]);
	Map billingValuesMap =FastMap.newInstance();
	if(UtilValidate.isNotEmpty(billingValues)){
		billingValuesMap =billingValues.get("FacilityBillingMap");
	}
	context.putAt("billingValuesMap", billingValuesMap);
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
					   totDeductions= deductionValues.getValue();
				   }
			   }			   
		   }		   
	   }
   }  
}
MilkBillValuesMap["totAdditions"]=totAdditions;
MilkBillValuesMap["totDeductions"]=totDeductions;
context.put("MilkBillValuesMap", MilkBillValuesMap);
context.put("deductionsValuesList", deductionsValuesList);






































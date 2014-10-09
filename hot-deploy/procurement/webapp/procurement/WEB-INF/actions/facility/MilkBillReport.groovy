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
import in.vasista.vbiz.procurement.PriceServices;

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
rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
context.rounding = rounding;
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
context.userLogin=userLogin;
procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.procurementProductList = procurementProductList;
loopProductList = [];

if("All".equalsIgnoreCase(parameters.productName)){
		loopProductList = procurementProductList;
	}else{
		for (product in procurementProductList){
				String productName = product.productName;
				if(productName.equals(parameters.productName)){
					loopProductList.add(product);
					}
			}
	}
context.putAt("loopProductList", loopProductList);
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
routeBillingList = [];
unitRoutesList =[];
if("all".equalsIgnoreCase(parameters.routeId)){
	unitRoutesList = ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",parameters.unitId )).get("routesDetailList");
	}else{
		unitRouteMap = [:];
		unitRouteMap["facilityId"] =parameters.routeId;
		unitRoutesList.add(unitRouteMap);
	}
for(route in unitRoutesList){
	routeBillingMap = FastMap.newInstance();
	routeId = route.facilityId;
	routeBillingMap.put("routeId",routeId);
	productPriceRateMap=[:];
	conditionList =[];
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, routeId)));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS ,"CENTER")));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	agentsList = delegator.findList("Facility",condition,null,null,null,false);
	agentEntryDetails =[:];
	adjustments =[:];
	Map tipAmtRateMap=[:];
	useTotSolidsMap=[:];
	deductionsListValuesList=[];
	agentWiseCommnMap=[:];
	agentsList.each{ agents ->
		
			agentDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDateTime , thruDate: thruDateTime , facilityId: agents.facilityId]);
			 if(UtilValidate.isNotEmpty(agentDayTotals)){
				 agentEntryDetails.putAll(agentDayTotals);
			 }
			 agentAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDateTime , thruDate: thruDateTime, facilityId: agents.facilityId]);
			 adjustmentsMap =[:];
			 adjustmentsMap["ADDITIONS"]=0;
			 adjustmentsMap["DEDUCTIONS"]=0;
			if(UtilValidate.isNotEmpty(agentAdjustments)){
				adjustmentsTypeValues = agentAdjustments.get("adjustmentsTypeMap");
				if(adjustmentsTypeValues !=null){
					adjustmentsTypeValues.each{ adjustmentValues ->
						if("MILKPROC_ADDITIONS".equals(adjustmentValues.getKey())){
							additionsList = adjustmentValues.getValue();
							additionsList.each{ additionValues ->
								adjustmentsMap["ADDITIONS"] += additionValues.getValue();
							}
							adjustmentsMap.put("additionsList", additionsList);
							
						}else{
							deductionsList = adjustmentValues.getValue();
							deductionsList.each{ deductionValues ->
								adjustmentsMap["DEDUCTIONS"] += deductionValues.getValue();
							}
							adjustmentsMap.put("dedValuesList", deductionsList);
						}
						
					}
					
				}
			}
			// cartage
			billingValues = ProcurementReports.getProcurementBillingValues(dctx , [userLogin: userLogin ,customTimePeriodId: parameters.customTimePeriodId, facilityId: agents.facilityId]);
			billingVal = billingValues.get("FacilityBillingMap");
			billingCartage =0;
			cartageMap = [:];
			if (UtilValidate.isNotEmpty(billingVal)) {
				billingFac = billingVal.get(agents.facilityId);
				procurementProductList.each{ procProducts ->
					billingTot = billingFac.get(procProducts.productId);
					
						billingCartage = billingTot.get("cartage");					
						cartageMap[procProducts.productId]=billingCartage;
				}		
			}			
			adjustmentsMap["cartageMap"]=cartageMap;
			adjustmentsMap["cartage"] = billingCartage;
			adjustments[agents.facilityId]=(adjustmentsMap); 	
			// for displying default rates for products
			procurementProductList.each{ procProducts ->
				inMap = [:];
				inMap.put("userLogin",context.userLogin);
				inMap.put("facilityId", agents.facilityId);
				inMap.put("productId", procProducts.productId);
				inMap.put("fatPercent", BigDecimal.ZERO);
				inMap.put("snfPercent", BigDecimal.ZERO);	
				rateMap = dispatcher.runSync("calculateProcurementProductPrice",inMap);	
				productPriceRateMap[procProducts.productId]=rateMap.get("defaultRate");
			}
		//calculating Tip amount for centers
			
			Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
			Map inputComnAmt = UtilMisc.toMap("userLogin", userLogin);
			agentComnMap =[:];
			procurementProductList.each{ procProducts ->
			   inputRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
			   inputRateAmt.put("rateCurrencyUomId", "INR");
			   inputRateAmt.put("productId", procProducts.productId);
			   inputRateAmt.put("facilityId", agents.facilityId);
			   rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
			   rateAmt= rateAmount.rateAmount;
			   tipAmtRateMap[procProducts.productName]=rateAmt;
			   
			   inputComnAmt.put("rateTypeId", "PROC_AGENT_MRGN");
			   inputComnAmt.put("rateCurrencyUomId", "INR");
			   inputComnAmt.put("productId", procProducts.productId);
			   inputComnAmt.put("facilityId", agents.facilityId);
			   comnAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputComnAmt);
			   commission= comnAmount.rateAmount;
			   agentComnMap[procProducts.productName]=commission;
			  
			   inMap = [:];
			   inMap.put("userLogin",userLogin);
			   inMap.put("facilityId",agents.facilityId);
			   inMap.put("fatPercent", BigDecimal.ZERO);
			   inMap.put("snfPercent", BigDecimal.ZERO);
			   inMap.put("productId",procProducts.productId);
			   inMap.put("priceDate", dayBegin);
			   Map priceChart = PriceServices.getProcurementProductPrice(dctx,inMap);
			   useTotalSolids = priceChart.get("useTotalSolids");	
			   useTotSolidsMap[procProducts.productName]=useTotalSolids;		   
			}	
			agentWiseCommnMap[agents.facilityId]= agentComnMap;
	}
	routeBillingMap.put("agentWiseCommnMap",agentWiseCommnMap);
	routeBillingMap.putAt("tipAmtRateMap", tipAmtRateMap);
	routeBillingMap.putAt("useTotSolidsMap", useTotSolidsMap);
	routeBillingMap.put("productPriceRateMap",productPriceRateMap);
	routeBillingMap.put("adjustments",adjustments);
	routeBillingMap.put("agentEntryDetails",agentEntryDetails);
	routeBillingList.add(routeBillingMap);
 }	
context.putAt("routeBillingList",routeBillingList);




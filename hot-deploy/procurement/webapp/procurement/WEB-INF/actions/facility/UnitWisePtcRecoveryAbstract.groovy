import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.service.ServiceUtil;

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
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime",fromDateTime);
context.put("thruDateTime",thruDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDateTime , timeZone, locale);

dctx = dispatcher.getDispatchContext();

unitDetails = delegator.findOne("Facility",[facilityId : parameters.unitId],false);

context.put("dctx",dctx);
conditionList =[];
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, parameters.unitId)));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitRoutesList = delegator.findList("Facility",condition,null,null,null,false);

shipmentList = [];
shipmentList.add("AM");
shipmentList.add("PM");

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getProcurementProducts(dctx, UtilMisc.toMap());

Map inputFacRateAmt = UtilMisc.toMap("userLogin", userLogin);
inputFacRateAmt.put("rateCurrencyUomId", "INR");

routeWiseCentersList = [];
unitWiseGtotMap = [:];
unitWiseGtotMap["qtyLtrs"] = 0;
unitWiseGtotMap["qtyKgs"] = 0;
unitWiseGtotMap["fat"] = 0;
unitWiseGtotMap["snf"] = 0;
unitWiseGtotMap["kgFat"] = 0;
unitWiseGtotMap["kgSnf"] = 0;
unitWiseGtotMap["totAmount"] = 0;
unitWiseGtotMap["totPrem"] = 0;
unitWiseGtotMap["netAmount"] = 0;
unitWiseGtotMap["tipAmount"] = 0;
unitWiseGtotMap["penality"] = 0;
unitWiseGtotMap["totValue"] = 0;
unitWiseGtotMap["sValue"] = 0;
unitWiseGtotMap["netAmountToRecover"] = 0;

unitRoutesList.each{ route ->
	routeWisePtcRecoveryMap = [:];
	routeTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: dayBegin , thruDate: dayEnd , facilityId: route.facilityId ,includeCenterTotals: true]);
	
	routeWisePtcRecoveryMap["qtyLtrs"] = 0;
	routeWisePtcRecoveryMap["qtyKgs"] = 0;
	routeWisePtcRecoveryMap["fat"] = 0;
	routeWisePtcRecoveryMap["snf"] = 0;
	routeWisePtcRecoveryMap["kgFat"] = 0;
	routeWisePtcRecoveryMap["kgSnf"] = 0;
	routeWisePtcRecoveryMap["totAmount"] = 0;
	routeWisePtcRecoveryMap["totPrem"] = 0;
	routeWisePtcRecoveryMap["netAmount"] = 0;
	routeWisePtcRecoveryMap["tipAmount"] = 0;
	routeWisePtcRecoveryMap["penality"] = 0;
	routeWisePtcRecoveryMap["totValue"] = 0;
	routeWisePtcRecoveryMap["sValue"] = 0;
	routeWisePtcRecoveryMap["netAmountToRecover"] = 0;
	
	if(UtilValidate.isNotEmpty(routeTotals)){
		centerWiseTots = routeTotals.get("centerWiseTotals");
		
		
		routeCentersList = delegator.findList("Facility",EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, route.facilityId),null,null,null,false);
		
		routeCentersList.each { center ->
			centerWiseDayTotals = centerWiseTots.get(center.facilityId);
			
			if(UtilValidate.isNotEmpty(centerWiseDayTotals)){
				Map centerWiseDate = centerWiseDayTotals.get("dayTotals");
				//for getting the populated dates.
				dayTots = centerWiseDate.entrySet();
				Iterator dayTots = centerWiseDate.entrySet().iterator();
				
				while (dayTots.hasNext()) {
					Map.Entry centerEntry = dayTots.next();
					datesForCenter = centerEntry.getKey();
					
					if(datesForCenter != "TOT"){
						dayWiseCentersDetail = centerWiseDate.get(datesForCenter.toString());
						
						shipmentList.each { shipment ->
							ProductMap = dayWiseCentersDetail.get(shipment);
							
							procurementProductList.each { procProd ->
								centerWisePtcRecoveryMap = [:];
								dataForShipAndProd = ProductMap.get(procProd.productName);
								
								if(!(dataForShipAndProd.get("ptcQtyKgs") == 0.0)){
									
									Timestamp priceDate = null;
									SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
									try {
										priceDate = UtilDateTime.toTimestamp(dateFormat.parse(datesForCenter));
									} catch (ParseException e) {
										Debug.logError(e, "Cannot parse date string: " + datesForCenter.toString(), "");
									}
									
									centerWisePtcRecoveryMap["dated"] = datesForCenter.toString();
									centerWisePtcRecoveryMap["routeNo"] = route.facilityCode;
									centerWisePtcRecoveryMap["centerCode"] = center.facilityCode;
									centerWisePtcRecoveryMap["centerName"] = center.facilityName;
									centerWisePtcRecoveryMap["day"] = shipment;
									centerWisePtcRecoveryMap["typ"] = procProd.brandName;
									centerWisePtcRecoveryMap["qtyLtrs"] = (dataForShipAndProd.get("ptcQtyLtrs"));
									centerWisePtcRecoveryMap["qtyKgs"] = dataForShipAndProd.get("ptcQtyKgs");
									centerWisePtcRecoveryMap["fat"] = dataForShipAndProd.get("fat");
									centerWisePtcRecoveryMap["snf"] = dataForShipAndProd.get("snf");
									centerWisePtcRecoveryMap["kgFat"] = ProcurementNetworkServices.calculateKgFatOrKgSnf(dataForShipAndProd.get("ptcQtyKgs"),dataForShipAndProd.get("fat"));
									centerWisePtcRecoveryMap["kgSnf"] = ProcurementNetworkServices.calculateKgFatOrKgSnf(dataForShipAndProd.get("ptcQtyKgs"),dataForShipAndProd.get("snf"))
									
									routeWisePtcRecoveryMap["dated"] = "ROUTE-TOT";
									routeWisePtcRecoveryMap["centerCode"] = route.facilityCode;
									routeWisePtcRecoveryMap["centerName"] = route.facilityName;
									routeWisePtcRecoveryMap["qtyLtrs"] += centerWisePtcRecoveryMap.get("qtyLtrs");
									routeWisePtcRecoveryMap["qtyKgs"] += dataForShipAndProd.get("ptcQtyKgs");
									routeWisePtcRecoveryMap["kgFat"] += centerWisePtcRecoveryMap["kgFat"];
									routeWisePtcRecoveryMap["kgSnf"] += centerWisePtcRecoveryMap["kgSnf"];
									routeWisePtcRecoveryMap["fat"] = ProcurementNetworkServices.calculateKgFatOrKgSnf(routeWisePtcRecoveryMap.get("kgFat"),routeWisePtcRecoveryMap.get("qtyKgs"));
									routeWisePtcRecoveryMap["snf"] = ProcurementNetworkServices.calculateKgFatOrKgSnf(routeWisePtcRecoveryMap.get("kgSnf"),routeWisePtcRecoveryMap.get("qtyKgs"));
									
									
									unitWiseGtotMap["dated"] = "UNIT-TOT";
									unitWiseGtotMap["centerCode"] = unitDetails.facilityCode;
									unitWiseGtotMap["centerName"] = "Unit Total";
									unitWiseGtotMap["qtyLtrs"] += centerWisePtcRecoveryMap.get("qtyLtrs");
									unitWiseGtotMap["qtyKgs"] += dataForShipAndProd.get("ptcQtyKgs");
									unitWiseGtotMap["kgFat"] += centerWisePtcRecoveryMap["kgFat"];
									unitWiseGtotMap["kgSnf"] += centerWisePtcRecoveryMap["kgSnf"];
									unitWiseGtotMap["fat"] = ProcurementNetworkServices.calculateKgFatOrKgSnf(unitWiseGtotMap.get("kgFat"),unitWiseGtotMap.get("qtyKgs"));
									unitWiseGtotMap["snf"] = ProcurementNetworkServices.calculateKgFatOrKgSnf(unitWiseGtotMap.get("kgFat"),unitWiseGtotMap.get("qtyKgs"));
									
									//rate
									inMap = [:];
									inMap.put("userLogin",context.userLogin);
									inMap.put("facilityId",center.facilityId);
									inMap.put("priceDate",priceDate);
									inMap.put("productId", procProd.productId);
									inMap.put("fatPercent", centerWisePtcRecoveryMap.get("fat"));
									inMap.put("snfPercent", centerWisePtcRecoveryMap.get("snf"));
									inMap.put("supplyTypeEnumId",shipment);
									inMap.put("categoryTypeEnum",center.categoryTypeEnum);
									rateMap = dispatcher.runSync("calculateProcurementProductPrice",inMap);
									if (ServiceUtil.isError(rateMap)) {
										context.errorMessage = "No valid price chart found";
										return ;
									}
									
									String usetotalSolids = rateMap.get("useTotalSolids");
									//rate and totPrem
									centerWisePtcRecoveryMap["rate"] = rateMap.get("defaultRate");
									grossAmt=0;
									grossAmt =(centerWisePtcRecoveryMap.get("qtyKgs"))*rateMap.get("price");
									premium = rateMap.get("premium")*centerWisePtcRecoveryMap.get("qtyKgs");
									centerWisePtcRecoveryMap["totAmount"] = grossAmt-premium;
									routeWisePtcRecoveryMap["totAmount"] += centerWisePtcRecoveryMap["totAmount"];
									unitWiseGtotMap["totAmount"] += centerWisePtcRecoveryMap["totAmount"];
									centerWisePtcRecoveryMap["totPrem"] = premium;
									routeWisePtcRecoveryMap["totPrem"] += premium;
									unitWiseGtotMap["totPrem"] += premium;
									
									//netamount = totVal - totprem;
									centerWisePtcRecoveryMap["netAmount"] = centerWisePtcRecoveryMap.get("totAmount")+centerWisePtcRecoveryMap.get("totPrem");
									routeWisePtcRecoveryMap["netAmount"] += centerWisePtcRecoveryMap.get("netAmount");
									unitWiseGtotMap["netAmount"] += centerWisePtcRecoveryMap.get("netAmount");
									
									//tip amount
									inputFacRateAmt.put("productId", procProd.productId);
									inputFacRateAmt.put("rateTypeId", "PROC_TIP_AMOUNT");
									inputFacRateAmt.put("facilityId",parameters.unitId);
									centerRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
									if (ServiceUtil.isError(centerRateAmount)) {
										context.errorMessage = "No valid rate Amount found for the given Unit";
										return ;
									}
									if(UtilValidate.isNotEmpty(usetotalSolids)&&( usetotalSolids.equalsIgnoreCase("Y"))){
										centerWisePtcRecoveryMap["tipAmount"] = (centerWisePtcRecoveryMap.get("kgFat")+centerWisePtcRecoveryMap.get("kgSnf"))*(centerRateAmount.rateAmount);
									}else{
									    centerWisePtcRecoveryMap["tipAmount"] = (centerWisePtcRecoveryMap.get("kgFat"))*(centerRateAmount.rateAmount);
									}
									routeWisePtcRecoveryMap["tipAmount"] += centerWisePtcRecoveryMap.get("tipAmount");
									unitWiseGtotMap["tipAmount"] += centerWisePtcRecoveryMap.get("tipAmount");
									
									//difAmount
									centerWisePtcRecoveryMap["difAmount"] = 0.00;
									routeWisePtcRecoveryMap["difAmount"] = 0.00;
									unitWiseGtotMap["difAmount"] = 0.00;
									
									//PENALITY
									inputFacRateAmt.put("rateTypeId", "PROC_PENALTY_AMOUNT");
									inputFacRateAmt.put("facilityId",center.facilityId);
									centerPenalityAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputFacRateAmt);
									if (ServiceUtil.isError(centerPenalityAmount)) {
										context.errorMessage = "No valid Penality Amount found for the given Facility";
										return ;
									}
									centerWisePtcRecoveryMap["penality"] = centerWisePtcRecoveryMap.get("qtyLtrs")*(centerPenalityAmount.rateAmount);
									routeWisePtcRecoveryMap["penality"] += centerWisePtcRecoveryMap.get("penality");
									unitWiseGtotMap["penality"] += centerWisePtcRecoveryMap.get("penality");
									
									//totValue = netAmount + penality + tipAmpunt;
									centerWisePtcRecoveryMap["totValue"] = centerWisePtcRecoveryMap.get("netAmount")+centerWisePtcRecoveryMap.get("penality")+centerWisePtcRecoveryMap.get("tipAmount")
									routeWisePtcRecoveryMap["totValue"] += centerWisePtcRecoveryMap.get("totValue");
									unitWiseGtotMap["totValue"] += centerWisePtcRecoveryMap.get("totValue");
									
									//milk type curd or sour 
									if(dataForShipAndProd.get("ptcMilkType")=="C"){
										centerWisePtcRecoveryMap["curdOrSour"] = "CURD";
									}else{
									centerWisePtcRecoveryMap["curdOrSour"] = "SOUR";
									}
									
									//sValue
									if(centerWisePtcRecoveryMap.get("curdOrSour")=="CURD"){
										centerWisePtcRecoveryMap["sValue"] = 0;
									}else{
									    sourRate = rateMap.get("sourRate");
										sValue = 0;
										// need to configure snf value here  the value is taken as 8 
										if(UtilValidate.isNotEmpty(usetotalSolids)&&( usetotalSolids.equalsIgnoreCase("Y"))){
												sValue = (centerWisePtcRecoveryMap.get("qtyKgs")*sourRate*((centerWisePtcRecoveryMap.get("fat"))+8))/100;
											}else{
												sValue = centerWisePtcRecoveryMap.get("kgFat")*sourRate;
											}
										
										centerWisePtcRecoveryMap["sValue"] =sValue;
									}
									routeWisePtcRecoveryMap["sValue"] += centerWisePtcRecoveryMap.get("sValue");
									unitWiseGtotMap["sValue"] += centerWisePtcRecoveryMap.get("sValue");
									
									//NetAmount = totValue - sValue;
									centerWisePtcRecoveryMap["netAmountToRecover"] = centerWisePtcRecoveryMap.get("totValue") - centerWisePtcRecoveryMap.get("sValue");
									routeWisePtcRecoveryMap["netAmountToRecover"] = routeWisePtcRecoveryMap.get("totValue") - routeWisePtcRecoveryMap.get("sValue");
									unitWiseGtotMap["netAmountToRecover"] = unitWiseGtotMap.get("totValue") - unitWiseGtotMap.get("sValue");
									routeWiseCentersList.add(centerWisePtcRecoveryMap); 
								}//check for ptcqty avalibility
								
							}//milktype
							
						}//shipment
						
					}
					
				}
				
			}
			
		}//centers in each route
		routeWiseCentersList.add(routeWisePtcRecoveryMap);
	}
	
}

routeWiseCentersList.add(unitWiseGtotMap);
context.put("routeWiseCentersList", routeWiseCentersList);

//Debug.logInfo("routeWiseCentersList--------------------------->"+routeWiseCentersList, null);


//----------------order in which iteration is populating---------------------------
//Am
//amProductMap = dayWiseCentersDetail.get("AM");
//amBmData = amProductMap.get("Buffalo Milk");
//amcmData = amProductMap.get("Cow Milk");
//Pm
//pmProductMap = dayWiseCentersDetail.get("PM");
//pmBmData = pmProductMap.get("Buffalo Milk");
//pmcmData = pmProductMap.get("Cow Milk");


//sour Value
/*inSvalMap = [:];
inSvalMap.put("userLogin",context.userLogin);
inSvalMap.put("facilityId",center.facilityId);
inSvalMap.put("priceDate",priceDate);
inSvalMap.put("productId", procProd.productId);
inSvalMap.put("fatPercent", dataForShipAndProd.get("fat"));
inSvalMap.put("supplyTypeEnumId",shipment);
inSvalMap.put("categoryTypeEnum",center.categoryTypeEnum);
rateMap = dispatcher.runSync("calculateProcurementProductPrice",inSvalMap);
unitPrice = dataForShipAndProd.get("price")/dataForShipAndProd.get("qtyKgs");*/



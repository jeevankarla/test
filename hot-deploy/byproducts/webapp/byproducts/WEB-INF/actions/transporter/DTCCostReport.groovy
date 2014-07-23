import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;


dctx = dispatcher.getDispatchContext();

month = parameters.month;
if(UtilValidate.isEmpty(month)){
	Debug.logError("Month Cannot Be Empty","");
	context.errorMessage = "Month Cannot Be Empty";
	return;
}

def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
try {
	monthTime = new java.sql.Timestamp(sdf.parse(month+"-01 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: ", "");
}

locale = Locale.getDefault();
timeZone = TimeZone.getDefault();

Timestamp monthBegin = UtilDateTime.getMonthStart(monthTime);
Timestamp monthEnd = UtilDateTime.getMonthEnd(monthTime, timeZone, locale);
context.put("monthBegin", monthBegin);
context.put("monthEnd", monthEnd);
totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
context.put("totalDays", totalDays+1);

routeIdsList = [];
finalMap = [:];
routeIdsList = ByProductNetworkServices.getRoutes(dctx,context).get("routesList");
if(UtilValidate.isNotEmpty(routeIdsList)){
	routeIdsList.each{ routeId ->
		routeWiseMap =[:];
		customTimePeriodId = null;
		conditionList = [];
		conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
		conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "DTC_FORTNIGHT_BILL"));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthBegin)));
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(monthEnd)));
		condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		customTimePeriodList = delegator.findList("CustomTimePeriod", condition, null, null, null, true);
		if (UtilValidate.isNotEmpty(customTimePeriodList)) {
			customTimePeriodList.each { customTimePeriod ->
				if (UtilValidate.isNotEmpty(customTimePeriod)) {
					customTimePeriodId = customTimePeriod.customTimePeriodId;
					fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
					thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
					
					dayBegin = UtilDateTime.getDayStart(fromDate, timeZone, locale);
					dayEnd = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
					
					totalSaleQty=0;
					averageDays = 0;
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
					conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS , routeId));
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
					conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
					condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					shipments = delegator.findList("Shipment", condition, null, ["routeId"], null, false);
					shipmentIds=EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", false);
					averageDays = shipmentIds.size();
					if(UtilValidate.isNotEmpty(shipmentIds)){
						routeTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds,fromDate:dayBegin, thruDate:dayEnd,includeReturnOrders:true]);
						// Populating sales for Milk and Curd products
						if(UtilValidate.isNotEmpty(routeTotals)){
							routeProdTotals = routeTotals.get("productTotals");
							if(UtilValidate.isNotEmpty(routeProdTotals)){
								routeProdTotals.each{ productValue ->
									if(UtilValidate.isNotEmpty(productValue)){
										productId = productValue.getKey();
										product = delegator.findOne("Product", [productId : productId], false);
										if("Milk".equals(product.primaryProductCategoryId) || "Curd".equals(product.primaryProductCategoryId)){
											totalSaleQty = totalSaleQty+productValue.getValue().get("total");
										}
									}
								}
							}
						}
					}
					//populating Facility Rate and Facility Size
					/*List<GenericValue> facilities = delegator.findList("FacilityPersonAndFinAccount", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, routeId), null, null, null, false);
					facility = EntityUtil.getFirst(facilities);*/
					Map inputRateAmt =  UtilMisc.toMap("userLogin", userLogin);
					inputRateAmt.put("rateCurrencyUomId", "INR");
					inputRateAmt.put("facilityId", routeId);
					inputRateAmt.put("fromDate",dayBegin);
					inputRateAmt.put("rateTypeId", "TRANSPORTER_MRGN");
					facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
					
					Map inputFacilitySize =  UtilMisc.toMap("userLogin", userLogin);
					inputFacilitySize.put("rateCurrencyUomId", "LEN_km");
					inputFacilitySize.put("facilityId", routeId);
					inputFacilitySize.put("fromDate",dayBegin);
					inputFacilitySize.put("rateTypeId", "FACILITY_SIZE");
					facilitySizeResult = dispatcher.runSync("getRouteDistance", inputFacilitySize);
					
					if(UtilValidate.isEmpty(routeWiseMap[customTimePeriodId])){
						tempMap = [:];
						tempMap["saleQty"] = new BigDecimal(totalSaleQty).setScale(2,BigDecimal.ROUND_HALF_UP);
						tempMap["facilityRate"] = (BigDecimal) facilityRateResult.get("rateAmount");
						tempMap["facilitySize"] = (BigDecimal) facilitySizeResult.get("facilitySize");
						tempMap["averageDays"] = averageDays;
						if(totalSaleQty != 0){
							routeWiseMap[customTimePeriodId] = tempMap;
						}
					}else{
						Map tempMap = FastMap.newInstance();
						tempMap.putAll(routeWiseMap.get(customTimePeriodId));
						totalQty = 0;
						totalQty = new BigDecimal(totalSaleQty).setScale(2,BigDecimal.ROUND_HALF_UP);
						if(UtilValidate.isNotEmpty(totalQty) && totalQty!=0){
							tempMap["saleQty"] += totalQty;
						}
						if(totalQty != 0){
							routeWiseMap[customTimePeriodId] = tempMap;
						}
					}
				}
			}
		}
		if(UtilValidate.isNotEmpty(routeWiseMap)){
			finalMap.put(routeId,routeWiseMap);
		}
	}
}
context.put("dTCCostMap",finalMap);
// for DTC Cost Report CSV
dtcCostReportCsvList = [];
if(UtilValidate.isNotEmpty(finalMap)){
	Iterator mapIter = finalMap.entrySet().iterator();
	while (mapIter.hasNext()) {
		Map.Entry entry = mapIter.next();
		routeId = entry.getKey();
		routeValues = entry.getValue();
		Iterator routeMapIter = routeValues.entrySet().iterator();
		while (routeMapIter.hasNext()) {
			Map.Entry routeEntry = routeMapIter.next();
			periodId = routeEntry.getKey();
			payment = 0;
			averageSaleQty= 0;
			dtcCostCsvMap = [:];
			dtcCostCsvMap["route"] = routeId;
			customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : periodId], false);
			fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
			fromDateStr = UtilDateTime.toDateString(fromDateTime,"MMM(dd-");
			thruDateStr = UtilDateTime.toDateString(thruDateTime,"dd)");
			dtcCostCsvMap["periodId"] = fromDateStr+thruDateStr;
			dtcCostCsvMap["routeSaleQty"] = routeEntry.getValue().get("saleQty");
			averageDays = routeEntry.getValue().get("averageDays");
			if(UtilValidate.isNotEmpty(averageDays)){
				dtcCostCsvMap["averageSaleQty"] = (routeEntry.getValue().get("saleQty")/averageDays);
			}
			dtcCostCsvMap["routeFacilitySize"] = routeEntry.getValue().get("facilitySize");
			dtcCostCsvMap["routeFacilityRate"] = routeEntry.getValue().get("facilityRate");
			if(UtilValidate.isNotEmpty(routeEntry.getValue().get("facilitySize"))){
				payment = (routeEntry.getValue().get("facilitySize")*routeEntry.getValue().get("facilityRate"));
				dtcCostCsvMap["payment"] = payment;
			}else{
				payment = routeEntry.getValue().get("facilityRate");
				dtcCostCsvMap["payment"] = payment;
			}
			averageSaleQty = (routeEntry.getValue().get("saleQty")/averageDays);
			if(averageSaleQty!=0){
				dtcCostCsvMap["cost"] = ((payment/averageSaleQty).setScale(2,BigDecimal.ROUND_HALF_UP));
			}
			tempMap = [:];
			tempMap.putAll(dtcCostCsvMap);
			dtcCostReportCsvList.add(tempMap);
		}
	}
}
context.put("dtcCostReportCsvList",dtcCostReportCsvList);

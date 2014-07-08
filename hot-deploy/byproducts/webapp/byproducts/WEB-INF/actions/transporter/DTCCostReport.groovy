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

/*customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.put("fromDateTime", fromDateTime);
context.put("thruDateTime", thruDateTime);

monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
totalDays=UtilDateTime.getIntervalInDays(monthBegin,monthEnd);
context.put("totalDays", totalDays+1);
*/

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
routeWiseMap =[:];

/*periodBillingId = null;
conditionList=[];
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_LMS_TRSPT_MRGN"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
if(UtilValidate.isNotEmpty(periodBillingList)){
	for (int i = 0; i < periodBillingList.size(); ++i) {
		periodBillingDetails = periodBillingList.get(i);
		periodBillingId = periodBillingDetails.periodBillingId;
	}
}
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS , periodBillingId));
conditionList.add(EntityCondition.makeCondition("commissionDate", EntityOperator.GREATER_THAN_EQUAL_TO,monthBegin));
conditionList.add(EntityCondition.makeCondition("commissionDate", EntityOperator.LESS_THAN_EQUAL_TO,monthEnd));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
EntityFindOptions findOptions = new EntityFindOptions();
routesList = delegator.findList("FacilityAndCommission",condition,["facilityId"]as Set, UtilMisc.toList("parentFacilityId","facilityId"),findOptions,false);
routeIdsList = EntityUtil.getFieldListFromEntityList(routesList, "facilityId", false);*/
routeIdsList = ByProductNetworkServices.getRoutes(dctx,context).get("routesList");
if(UtilValidate.isNotEmpty(routeIdsList)){
	routeIdsList.each{ routeId ->
		totalSaleQty=0;
		conditionList=[];
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "GENERATED"));
		conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS , routeId));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO,monthBegin));
		conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,monthEnd));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		shipments = delegator.findList("Shipment", condition, null, ["routeId"], null, false);
		shipmentIds=EntityUtil.getFieldListFromEntityList(shipments, "shipmentId", false);
		if(UtilValidate.isNotEmpty(shipmentIds)){
			routeTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds,fromDate:monthBegin, thruDate:monthEnd,includeReturnOrders:true]);
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
		inputRateAmt.put("fromDate",monthBegin );
		inputRateAmt.put("rateTypeId", "TRANSPORTER_MRGN");
		facilityRateResult = dispatcher.runSync("getFacilityRateAmount", inputRateAmt);
		
		Map inputFacilitySize =  UtilMisc.toMap("userLogin", userLogin);
		inputFacilitySize.put("rateCurrencyUomId", "LEN_km");
		inputFacilitySize.put("facilityId", routeId);
		inputFacilitySize.put("fromDate",monthBegin );
		inputFacilitySize.put("rateTypeId", "FACILITY_SIZE");
		facilitySizeResult = dispatcher.runSync("getRouteDistance", inputFacilitySize);
		
		if(UtilValidate.isEmpty(routeWiseMap[routeId])){
			tempMap = [:];
			tempMap["saleQty"] = new BigDecimal(totalSaleQty).setScale(2,BigDecimal.ROUND_HALF_UP);
			tempMap["facilityRate"] = (BigDecimal) facilityRateResult.get("rateAmount");
			tempMap["facilitySize"] = (BigDecimal) facilitySizeResult.get("facilitySize");
			if(totalSaleQty != 0){
				routeWiseMap[routeId] = tempMap;
			}
		}else{
			Map tempMap = FastMap.newInstance();
			tempMap.putAll(routeWiseMap.get(routeId));
			totalQty = 0;
			totalQty = new BigDecimal(totalSaleQty).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(UtilValidate.isNotEmpty(totalQty) && totalQty!=0){
				tempMap["saleQty"] += totalQty;
			}
			if(totalQty != 0){
				routeWiseMap[routeId] = tempMap;
			}
		}
	}
}
context.put("dTCCostMap",routeWiseMap);
// for DTC Cost Report CSV
dtcCostReportCsvList = [];
if(UtilValidate.isNotEmpty(routeWiseMap)){
	routeWiseMap.each { route->
		totalDaysCsv = totalDays + 1;
		dtcCostCsvMap = [:];
		payment = 0;
		averageSaleQty= 0;
		dtcCostCsvMap["route"] = route.getKey();
		dtcCostCsvMap["routeSaleQty"] = route.getValue().get("saleQty");
		dtcCostCsvMap["averageSaleQty"] = ((route.getValue().get("saleQty"))/(totalDaysCsv));
		dtcCostCsvMap["routeFacilitySize"] = route.getValue().get("facilitySize");
		dtcCostCsvMap["routeFacilityRate"] = route.getValue().get("facilityRate");
		if(UtilValidate.isNotEmpty(route.getValue().get("facilitySize"))){
			payment = (route.getValue().get("facilitySize")*route.getValue().get("facilityRate"));
			dtcCostCsvMap["payment"] = payment;
		}else{
			payment = route.getValue().get("facilityRate");
			dtcCostCsvMap["payment"] = payment;
		}
		averageSaleQty = ((route.getValue().get("saleQty"))/(totalDaysCsv));
		if(averageSaleQty!=0){
			dtcCostCsvMap["cost"] = ((payment/averageSaleQty).setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		tempMap = [:];
		tempMap.putAll(dtcCostCsvMap);
		dtcCostReportCsvList.add(tempMap);
	}
}
context.put("dtcCostReportCsvList",dtcCostReportCsvList);














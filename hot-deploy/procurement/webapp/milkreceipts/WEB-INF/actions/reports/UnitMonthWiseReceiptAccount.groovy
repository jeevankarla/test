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

import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
def sdf1 = new SimpleDateFormat("yyyy/MM/dd");
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (fromDate) {
		fromDateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
	}
	if (thruDate) {
		thruDateEnd = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.putAt("fromDate", fromDateStart);
context.putAt("thruDate", thruDateEnd);

dctx = dispatcher.getDispatchContext();

procurementProductList =[];
procurementProductList = ProcurementNetworkServices.getMilkReceiptProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
context.put("procurementProductList", procurementProductList);

List productWiseList = FastList.newInstance();
productWiseList.addAll(EntityUtil.getFieldListFromEntityList(procurementProductList, "productId", true));
productWiseList.add("TOT");

int totalDays =UtilDateTime.getIntervalInDays(fromDateStart, thruDateEnd);
totalDays=totalDays+1;

facilityId = parameters.unitId ;

if(UtilValidate.isEmpty(facilityId)){
	Debug.logError("UnitId Cannot Be Empty","");
	context.errorMessage = "No Unit Has Been Selected.......!";
	return;
}

Map initMap = FastMap.newInstance();
initMap.put("recdQtyLtrs", BigDecimal.ZERO);
initMap.put("recdQtyKgs", BigDecimal.ZERO);
initMap.put("recdKgFat", BigDecimal.ZERO);
initMap.put("recdKgSnf", BigDecimal.ZERO);


Map initProductMap = FastMap.newInstance();
for(productId in productWiseList){
		tempInitMap = [:];
		tempInitMap.putAll(initMap);
		initProductMap.put(productId, tempInitMap);
	}
Map grandTotalsMap = FastMap.newInstance();
grandTotalsMap.putAll(initProductMap);

Map finalTotalsMap = FastMap.newInstance();


unitTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: facilityId]);
if(UtilValidate.isNotEmpty(unitTotals)){
	facilityTotals = unitTotals.get(facilityId);
	if(UtilValidate.isNotEmpty(facilityTotals)){
		dayTot = facilityTotals.get("dayTotals");
		if(UtilValidate.isNotEmpty(dayTot)){
			Iterator dayTotIter = dayTot.entrySet().iterator();
			while(dayTotIter.hasNext()){
				Map.Entry entry = dayTotIter.next();
				String dateKey = entry.getKey();
					if(dateKey != "TOT"){
					dateFormate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf1.parse(dateKey).getTime()));
					dateKeyFormate = UtilDateTime.toDateString(dateFormate,"MMM yyyy");
						Map productWiseMap = FastMap.newInstance();
						productWiseMap = entry.getValue();
						if(UtilValidate.isEmpty(finalTotalsMap.get(dateKeyFormate))){
								Map tempProductMap =FastMap.newInstance();
								tempProductMap.putAll(initProductMap);
								
								
								for(prodKey in tempProductMap.keySet() ){
										Map tempQtyMap = FastMap.newInstance();
										tempQtyMap.putAll(tempProductMap.get(prodKey));
										
										Map tempPeriodQtyMap = FastMap.newInstance();
										tempPeriodQtyMap.putAll(productWiseMap.get(prodKey));
										for(qtyKey in tempQtyMap.keySet()){
											tempQtyMap.put(qtyKey, tempQtyMap.get(qtyKey)+tempPeriodQtyMap.get(qtyKey));
										}
										tempProductMap.put(prodKey, tempQtyMap);
									}
									tempFinalMap = [:];
									tempFinalMap.putAll(tempProductMap);
									finalTotalsMap.put(dateKeyFormate, tempFinalMap);
								}else{
									Map tempProductWiseMap = FastMap.newInstance();
									Map tempProductMap = FastMap.newInstance();
									tempProductWiseMap.putAll( finalTotalsMap.get(dateKeyFormate));
									tempProdMap = [:];
									tempProdMap.putAll(tempProductWiseMap);
									
									
									for(prodKey in tempProdMap.keySet() ){
											Map tempQtyMap = FastMap.newInstance();
											tempQtyMap.putAll(tempProdMap.get(prodKey));
											
											Map tempPeriodQtyMap = FastMap.newInstance();
											tempPeriodQtyMap.putAll(productWiseMap.get(prodKey));
											
											for(qtyKey in tempQtyMap.keySet()){
												tempQtyMap.put(qtyKey, tempQtyMap.get(qtyKey)+tempPeriodQtyMap.get(qtyKey));
											}
											tempProdMap.put(prodKey, tempQtyMap);
									}
									tempFinalMap = [:];
									tempFinalMap.putAll(tempProdMap);
									
									finalTotalsMap.put(dateKeyFormate, tempFinalMap);
									
							}
					}else{
					Map productWiseMap = FastMap.newInstance();
					productWiseMap = entry.getValue();
					
					for(prodKey in grandTotalsMap.keySet()){
							Map tempQtyTotalMap = FastMap.newInstance();
							tempQtyTotalMap.putAll(grandTotalsMap.get(prodKey));
						
							Map periodQtyMap = FastMap.newInstance();
							periodQtyMap.putAll(productWiseMap.get(prodKey));
							
							for(qtyKey in tempQtyTotalMap.keySet()){
								tempQtyTotalMap.put(qtyKey, periodQtyMap.get(qtyKey)+tempQtyTotalMap.get(qtyKey));
								}
								grandTotalsMap.put(prodKey, tempQtyTotalMap);
						}
					}
				}
			}
		}
	}
	context.put("finalTotalsMap", finalTotalsMap);
	context.put("grandTotalsMap", grandTotalsMap);




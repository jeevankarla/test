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
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;

fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

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

shedList = ProcurementNetworkServices.getSheds(delegator);

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

Map finalTotalsMap = FastMap.newInstance();
List dateKeysList = FastList.newInstance();

shedList.each{ shed->
	shedTotals = MilkReceiptReports.getMilkReceiptPeriodTotals(dctx , [fromDate: fromDateStart , thruDate: thruDateEnd,userLogin: userLogin,facilityId: shed.facilityId]);
	if(UtilValidate.isNotEmpty(shedTotals)){
		facilityTotals = shedTotals.get(shed.facilityId);
		if(UtilValidate.isNotEmpty(facilityTotals)){
			dayTot = facilityTotals.get("dayTotals");
			if(UtilValidate.isNotEmpty(dayTot)){
				Iterator dayTotIter = dayTot.entrySet().iterator();
				while(dayTotIter.hasNext()){
					Map.Entry entry = dayTotIter.next();
					String dateKey = entry.getKey();
					if(dateKey != "TOT"){
						Map daysMap = FastMap.newInstance();
						daysMap.put("dateKey",dateKey );
						daysMap.put("dateFormat",dateKey.replace('/', '') );
						if(!dateKeysList.contains(daysMap)){
							dateKeysList.add(daysMap);
						}
					}
					Map productWiseMap = FastMap.newInstance();
					productWiseMap = entry.getValue();
						if(UtilValidate.isEmpty(finalTotalsMap.get(dateKey))){
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
							
							finalTotalsMap.put(dateKey, tempFinalMap);
						}else{
							Map tempProductWiseMap = FastMap.newInstance(); 
							tempProductWiseMap = finalTotalsMap.get(dateKey);
							
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
							
							finalTotalsMap.put(dateKey, tempFinalMap);
						}
					}
				}
			}
		}
    }
dateKeysList = UtilMisc.sortMaps(dateKeysList,UtilMisc.toList("dateFormat"));
Map tempFinalTotMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(finalTotalsMap)){
	for(dayMap in dateKeysList){
		String dateKey = dayMap.get("dateKey");
		tempFinalTotMap.put(dateKey, finalTotalsMap.get(dateKey));
	}
	tempFinalTotMap.put("TOT", finalTotalsMap.get("TOT"));
}

context.putAt("finalTotalsMap", tempFinalTotMap);


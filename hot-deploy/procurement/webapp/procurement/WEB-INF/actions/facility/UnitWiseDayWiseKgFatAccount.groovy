import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
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
import java.util.*;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.unitId ;
unitFacilityMap = [:];
unitFacilityMap = delegator.findOne("Facility",[facilityId:facilityId],false);
unitCode = null;
productsMap = [:];
productsList = [];
productsList = ProcurementNetworkServices.getProcurementProducts(dctx,context);
context.putAt("productsList", productsList);
for(product in productsList){
	productsMap.putAt(product.productName,product.brandName);
}
if(ServiceUtil.isError(unitFacilityMap)){
	context.errorMessage = "Unit Not Found" ;
	return;
	}else{
	unitCode = unitFacilityMap.get("facilityCode");
	unitName = unitFacilityMap.get("facilityName");
	}	
	context.put("unitName",unitName);
	context.put("unitCode",unitCode);
	unitTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId:facilityId,userLogin:userLogin]);
	Iterator unitTotalsMapItr =unitTotals.entrySet().iterator();
	Map unitWiseTotalValues =FastMap.newInstance();
	while (unitTotalsMapItr.hasNext()) {
		Map.Entry unitEntry = unitTotalsMapItr.next();
		Map unitValuesMap = (Map)unitEntry.getValue();
		unitWiseTotalValues = ((Map)((Map)((Map)unitValuesMap.get("dayTotals")).get("TOT")).get("TOT"));
	}	
	context.putAt("unitWiseTotalValues", unitWiseTotalValues);
	dayTotalsMap = [:];
	grandTotalsList = [];
	dayWiseEntriesList = [];
	if(UtilValidate.isNotEmpty(unitTotals)){
		dayTotalsMap = unitTotals.get(facilityId).get("dayTotals");
		for(dayTotal in dayTotalsMap){
			if(dayTotal.getKey()!="TOT"){
			// dayTotal.getValue().keySet() will give AM,PM,TOT as keys
			for(dayKey in dayTotal.getValue().keySet()){
				if(dayKey!="TOT"){	
					milkMap = [:];
					milkMap = dayTotal.getValue().get(dayKey) ;
					for(milkKey in milkMap.keySet()){
						dayWiseEntryMap = [:];
						// here we are eliminating totals
						if(UtilValidate.isNotEmpty(productsMap.get(milkKey))){
							tempMap = [:];
							tempMap = milkMap.get(milkKey);
							dayWiseEntryMap.putAll(tempMap);
							dayWiseEntryMap.put("date",dayTotal.getKey());
							dayWiseEntryMap.put("day",dayKey);
							dayWiseEntryMap.put("milkType",productsMap.get(milkKey));
							BigDecimal snf =  BigDecimal.valueOf(( milkMap.get(milkKey).get("snf"))).setScale(2,RoundingMode.FLOOR);
							dayWiseEntryMap.put("snf",snf);
							dayWiseEntryMap.put("sQtyKgs",tempMap.get("sQtyLtrs")*1.03);
							dayWiseEntryMap.put("sKgFat",ProcurementNetworkServices.calculateKgFatOrKgSnf(tempMap.get("sQtyKgs"),tempMap.get("sFat")));
							dayWiseEntriesList.add(dayWiseEntryMap);
						}
					}
				}else{
							milkMap = dayTotal.getValue().get(dayKey).get("TOT");
							
							dayWiseEntryMap.putAll(milkMap);
							dayWiseEntryMap.put("sQtyKgs",milkMap.get("sQtyLtrs")*1.03);
							dayWiseEntryMap.put("sKgFat",ProcurementNetworkServices.calculateKgFatOrKgSnf(milkMap.get("sQtyKgs"),milkMap.get("sFat")));
							dayWiseEntryMap.put("milkType",dayKey);
							dayWiseEntriesList.add(dayWiseEntryMap);
						}	
			}
		  }else{
				  for(key in dayTotal.getValue().keySet()){
					  milkMap = [:];
					  if(key!="TOT"){
						  milkMap = dayTotal.getValue().get(key);
						  for(milkKey in milkMap.keySet()){
							   dayWiseEntryMap = [:];
							   dayWiseEntryMap.put("day",key);
							   if(milkKey!="TOT"){
								   tempMap = [:];
								   tempMap =  milkMap.get(milkKey);
								   dayWiseEntryMap.putAll(tempMap);
								  if(UtilValidate.isNotEmpty(productsMap.get(milkKey))){
									  dayWiseEntryMap.putAt("milkType",productsMap.get(milkKey));
								  }
								  BigDecimal snf =  BigDecimal.valueOf(( milkMap.get(milkKey).get("snf"))).setScale(2,RoundingMode.FLOOR);
								  dayWiseEntryMap.put("snf",snf);
								  dayWiseEntryMap.put("sQtyKgs",tempMap.get("sQtyLtrs")*1.03);
								  dayWiseEntryMap.put("sKgFat",ProcurementNetworkServices.calculateKgFatOrKgSnf(tempMap.get("sQtyKgs"),tempMap.get("sFat")));
								  grandTotalsList.add(dayWiseEntryMap);
							  }
						  }
					  }else{
					  		dayWiseEntryMap = [:];
							milkMap = dayTotal.getValue().get(key).get("TOT");
							dayWiseEntryMap.putAll(milkMap);
							dayWiseEntryMap.put("sQtyKgs",milkMap.get("sQtyLtrs")*1.03);
							dayWiseEntryMap.put("sKgFat",ProcurementNetworkServices.calculateKgFatOrKgSnf(milkMap.get("sQtyKgs"),milkMap.get("sFat")));
							dayWiseEntryMap.put("milkType","TOT");
							grandTotalsList.add(dayWiseEntryMap);
					  } 
				  }
		  }	
		}
	}
	context.put("grandTotalsList",grandTotalsList);
	context.put("dayWiseEntriesList",dayWiseEntriesList);
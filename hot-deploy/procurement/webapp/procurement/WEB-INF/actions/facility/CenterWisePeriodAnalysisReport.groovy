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
import org.ofbiz.base.util.Debug;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.fromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	else {
		fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	}
	if (parameters.thruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
	else {
		thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
productsList = [];
productsList = ProcurementNetworkServices.getProcurementProducts(dctx,UtilMisc.toMap());
productsMap = [:];
totalsList = [];

unitCode = null;
shedCode = null;
centerFacilityId = null;
facilityCode = parameters.facilityCode;
unitFacility = delegator.findOne("Facility",[facilityId:parameters.unitId],false);
if(UtilValidate.isNotEmpty(unitFacility)){
	unitCode = unitFacility.facilityCode;
}
shedFacility = delegator.findOne("Facility",[facilityId:parameters.shedId],false);
if(UtilValidate.isNotEmpty(shedFacility)){
	shedCode = shedFacility.facilityCode;
}
GenericValue agentFacility = (GenericValue)(ProcurementNetworkServices.getAgentFacilityByShedCode(dctx, UtilMisc.toMap("shedCode", shedCode,"centerCode", facilityCode,"unitCode",unitCode))).get("agentFacility");
if(UtilValidate.isNotEmpty(agentFacility)){
	centerFacilityId = agentFacility.facilityId;
}

for(product in productsList){
	productsMap.putAt(product.productName,product.brandName);
}
if(UtilValidate.isNotEmpty(productsMap)&& UtilValidate.isNotEmpty(centerFacilityId)){
	facility = delegator.findOne("Facility",[facilityId:centerFacilityId],false);
	if(UtilValidate.isEmpty(facility)){
		Debug.logError("facility not found with the facilityId =====>"+centerFacilityId,"");
		context.errorMessage = "facility not found with facilityId ====> "+centerFacilityId;
		return;
		}
	context.put("facility",facility);
	route = delegator.findOne("Facility",[facilityId:facility.get("parentFacilityId")],false);
	context.put("route",route);
		unitDetails = ProcurementNetworkServices.getCenterDtails(dctx ,[centerId:centerFacilityId]);
	if(UtilValidate.isNotEmpty(unitDetails.unitFacility)){
		unit = unitDetails.get("unitFacility");
		context.put("unit",unit);
		dayWiseEntriesList = [];
		agentDayTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId: centerFacilityId,userLogin:userLogin]);
		if(UtilValidate.isNotEmpty(agentDayTotals)){
			dayTotalsMap = [:];
			dayTotalsMap = agentDayTotals.get(centerFacilityId).get("dayTotals");
			totalsMap = [:];
			totalsMap = dayTotalsMap.get("TOT").get("TOT");
			grandTotals = [:];
			grandTotals = totalsMap.get("TOT");
			grandTotals.put("sQtyKgs",grandTotals.get("sQtyLtrs")*1.03);
			grandTotals.put("sKgFat",((grandTotals.get("sQtyKgs"))*grandTotals.get("sFat"))/100);
			context.put("grandTotals",grandTotals);
			for(totalsKey in totalsMap.keySet()){
				if(UtilValidate.isNotEmpty(productsMap.get(totalsKey))){
					tempMap = [:];
					tempMap = totalsMap.get(totalsKey);
					tempMap.putAt("milkType",productsMap.get(totalsKey));
					tempMap.putAt("sQtyKgs",tempMap.get("sQtyLtrs")*1.03);
					tempMap.putAt("sKgFat",ProcurementNetworkServices.calculateKgFatOrKgSnf(tempMap.get("sQtyKgs"),tempMap.get("sFat")));
					totalsList.add(tempMap);
				}
			}
			context.put("totalsList",totalsList);
			purchageTimeMap = [:];
			purchageTimeMap.put("AM","");
			purchageTimeMap.put("PM","");
			// loop will repeate for AM and PM
			for(purchaseTimekey in purchageTimeMap.keySet()){
				for(dayTotal in dayTotalsMap){
					// dayTotal.getValue().keySet() will give AM,PM,TOT as keys
					for(dayKey in dayTotal.getValue().keySet()){
						if(purchaseTimekey.equals(dayKey)){
							if(dayTotal.getKey()!="TOT"){
								milkMap = [:];
								milkMap = dayTotal.getValue().get(dayKey) ;
								// milk map will contain BM , CM 
								for(milkKey in milkMap.keySet()){
									dayWiseEntryMap = [:];
									// here we are eliminating totals
									if(UtilValidate.isNotEmpty(productsMap.get(milkKey))){
										dayWiseEntryMap = milkMap.get(milkKey);
										dayWiseEntryMap.putAt("date",dayTotal.getKey());
										dayWiseEntryMap.putAt("day",purchaseTimekey);
										dayWiseEntryMap.putAt("milkType",productsMap.get(milkKey));
										dayWiseEntryMap.putAt("sQtyKgs",dayWiseEntryMap.get("sQtyLtrs")*1.03);
										dayWiseEntryMap.putAt("sKgFat",ProcurementNetworkServices.calculateKgFatOrKgSnf(dayWiseEntryMap.get("sQtyKgs"),dayWiseEntryMap.get("sFat")));
										dayWiseEntriesList.add(dayWiseEntryMap);
									}
								}
							}
						}	
					}
				}
			 	
			 }
		}
		context.putAt("dayWiseEntriesList", dayWiseEntriesList);
	}
}
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
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;

import javax.naming.Context;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;


import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.PriceServices;

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.shedId;
facility = delegator.findOne("Facility",[facilityId:facilityId],false);
context.put("facility",facility);
totAmountsMap =[:];
totAmountsMap.put("cartage", 0);
totAmountsMap.put("addnAmt", 0);
totAmountsMap.put("commAmt", 0);
totAmountsMap.put("opCost", 0);
totAmountsMap.put("tipAmt", 0);
facilityShortagesList = [];
if(UtilValidate.isNotEmpty(facility)){
		facilityShortages = [:];
		childFacilities = delegator.findList("Facility",EntityCondition.makeCondition("parentFacilityId",EntityOperator.EQUALS,facilityId),["facilityId","facilityCode","facilityName","destinationFacilityId"]as Set,null,null,false);
		if(UtilValidate.isNotEmpty(childFacilities)){
			tipAmt = 0;
			for(childFacility in childFacilities){
				if(childFacility.facilityId == childFacility.destinationFacilityId){
					childFacilityId = childFacility.get("facilityId");
					subChildList = [];
					subChildList = delegator.findList("Facility",EntityCondition.makeCondition("destinationFacilityId",EntityOperator.EQUALS,childFacilityId),["facilityId","facilityCode","facilityName"]as Set,null,null,false);
					tempMap = [:];
					tempMap.put("kgFat",0);
					tempMap.put("kgSnf",0);
					tempMap.put("kgFatAmt",0);
					tempMap.put("kgSnfAmt",0);
					tempMap.put("sPrice",0);
					for(subChild in subChildList){
						subChildFacilityShortages=[:];
						subChildFacilityPeriodTotals =[];
						facilityId = subChild.get("facilityId");
						subChildFacilityPeriodTotals = dispatcher.runSync("getPeriodTransferTotals" , [fromDate: fromDate , thruDate: thruDate , facilityId: facilityId,userLogin:userLogin]);
						subChildFacilityShortages = subChildFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers").get("shortages");
						amountsMap =[:]
						amountsMap = subChildFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers").get("amounts");
						// here we are populating totAmtMap for using in shedMilkBill Details
						for( key in totAmountsMap.keySet()){
							totAmountsMap.put(key, totAmountsMap.get(key)+(amountsMap.get(key)).setScale(0,BigDecimal.ROUND_HALF_UP));
							}
						
						
						
						for(key in tempMap.keySet()){
							if(key!="sPrice"){
								if(subChildFacilityShortages.get(key)<0){
									tempMap.put(key, tempMap.get(key)+subChildFacilityShortages.get(key));
								}else{
									tempMap.put(key, tempMap.get(key)+0);
								}	
							}
						}
						sPrice = 0;
						transfersMap =[:];
						transfersMap = subChildFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers");
						if(UtilValidate.isNotEmpty(transfersMap.get("procurementPeriodTotals").get("dayTotals"))){
							sPrice = sPrice - ((BigDecimal)transfersMap.get("procurementPeriodTotals").get("dayTotals").get("TOT").get("sPrice"));
							tempMap.put("sPrice",tempMap.get("sPrice")-sPrice);
						}
						subChildFacilityShortages.put("sPrice",sPrice);
						subChildFacilityShortages.put("facilityCode",subChild.get("facilityCode"));
						subChildFacilityShortages.put("facilityName", subChild.get("facilityName"));	
						facilityShortagesList.add(subChildFacilityShortages);
						//tipAmt = tipAmt + ((BigDecimal)transfersMap.get("amounts").get("tipAmt"));
					}
					tempMap.put("facilityCode","TOT");
					tempMap.put("facilityName",childFacility.facilityName);
					facilityShortagesList.add(tempMap);
				}  	
			}
		}
		context.put("tipAmt",tipAmt);
}
totshrtKgFatAmt =0;
totshrtKgSnfAmt =0;
for(shortage in facilityShortagesList){
		if(shortage.facilityCode !="TOT"){
			totshrtKgFatAmt = totshrtKgFatAmt + shortage.kgFatAmt;
			totshrtKgSnfAmt = totshrtKgSnfAmt + shortage.kgSnfAmt;
			}
	}
context.putAt("totshrtKgFatAmt", totshrtKgFatAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
context.putAt("totshrtKgSnfAmt", totshrtKgSnfAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
context.putAt("totAmountsMap",totAmountsMap);
context.put("facilityShortagesList",facilityShortagesList);

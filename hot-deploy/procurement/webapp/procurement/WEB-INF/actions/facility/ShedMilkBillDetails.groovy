import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
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
import org.ofbiz.product.spreadsheetimport.ImportProductHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.prefs.BackingStoreException;


import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.procurement.ProcurementServices;

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
Timestamp fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
Timestamp thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
fromDate = UtilDateTime.getDayStart(fromDate);
thruDate = UtilDateTime.getDayEnd(thruDate); 
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
context.putAt("noOfDays", (UtilDateTime.getIntervalInDays(fromDate, thruDate))+1);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.shedId;
facility = delegator.findOne("Facility",[facilityId:facilityId],false);
milkBillList = [];
opCost = 0;
if(!UtilValidate.isEmpty(facility)){
	context.put("facility",facility);
	productsList = [];
	productsList = ProcurementNetworkServices.getProcurementProducts(dctx,UtilMisc.toMap());
	productRatesList = [];
	//test to be done
	productsBrandMap = [:];
	for(product in productsList){
		productMap = [:];
		productMap.put("productName",product.brandName);
		rateMap = [:];
	    rateMap = PriceServices.getProcurementProductPrice(dctx,[userLogin:userLogin,facilityId:facilityId,productId:product.productId,fatPercent:BigDecimal.ZERO,snfPercent:BigDecimal.ZERO]);
		productMap.put("defaultRate",rateMap.defaultRate);
		if((rateMap.useTotalSolids)=="N"){
			productMap.put("using","KGFAT");
		}else{
			productMap.put("using","TOTAL SOLIDS");
		}
		productRatesList.add(productMap);
		productsBrandMap[product.brandName]=product.productName;
	}
	context.putAt("productsBrandMap",productsBrandMap);
	context.putAt("productRatesList",productRatesList);
	facilityPeriodTotals = [:];
	facilityPeriodTotals = ProcurementReports.getPeriodTotals(dctx , [fromDate: fromDate , thruDate: thruDate , facilityId:facilityId,userLogin:userLogin]);
	totalsMap =[:];
	shortages =[:];
	amounts	= [:];
	if(UtilValidate.isNotEmpty(facilityPeriodTotals.get(facilityId).get("dayTotals"))){
		totalsMap = facilityPeriodTotals.get(facilityId).get("dayTotals").get("TOT").get("TOT");
	}
	unitAdjustments = ProcurementServices.getPeriodAdjustmentsForAgent(dctx , [userLogin: userLogin ,fromDate: fromDate , thruDate: thruDate, facilityId: facilityId]);
	feedAmt =0;
	cessOnSaleAmt =0;
	if(UtilValidate.isNotEmpty(unitAdjustments)){
			adjustments = unitAdjustments.adjustmentsTypeMap;
			if(UtilValidate.isNotEmpty(adjustments)){
				deductions = adjustments.get("MILKPROC_DEDUCTIONS");
				if(UtilValidate.isNotEmpty(deductions.get("MILKPROC_FEEDDED"))){
					feedAmt = feedAmt+ (deductions.get("MILKPROC_FEEDDED")).setScale(0,BigDecimal.ROUND_HALF_UP);
					}
				if(UtilValidate.isNotEmpty(deductions.get("MILKPROC_CESSONSALE"))){
					cessOnSaleAmt = cessOnSaleAmt+ (deductions.get("MILKPROC_CESSONSALE")).setScale(0,BigDecimal.ROUND_HALF_UP);
					}
				if(UtilValidate.isNotEmpty(deductions.get("MILKPROC_COLSALE"))){
					cessOnSaleAmt = cessOnSaleAmt+ (deductions.get("MILKPROC_COLSALE")).setScale(0,BigDecimal.ROUND_HALF_UP);
					}
			}
		}
	context.put("feedAmt",feedAmt);
	context.put("cessOnSaleAmt",cessOnSaleAmt);
	context.put("difAmt",0);
	totAmountsMap = context.get("totAmountsMap");
	if(UtilValidate.isNotEmpty(totalsMap)){
	for(key in productsBrandMap.keySet()){
		productName = productsBrandMap.get(key);
		amt = 0;
		amt = totalsMap.get(productName).get("price");
		totAmountsMap.put(key,amt.setScale(0,BigDecimal.ROUND_HALF_UP));
		}
	
	sprice = 0;
	sprice = totalsMap.get("TOT").get("sPrice");
	totAmountsMap.put("sprice",sprice.setScale(0,BigDecimal.ROUND_HALF_UP));
	context.putAt("totAmountsMap", totAmountsMap);
	context.putAt("totalsMap", totalsMap);
	//for calculating variations in fat and snf
	centersFatSnfList = [];
	childFacilities = [];
	totProcKgFat = 0;
	totProcKgSnf = 0;
	totRecvKgFat = 0;
	totRecvKgSnf = 0;
	totProcQtyKgs = 0;
	totRecvQtyKgs = 0;
	childConditionList = [];
	childFacilities = delegator.findList("Facility",EntityCondition.makeCondition("parentFacilityId",EntityOperator.EQUALS,facilityId),["facilityId","facilityCode","facilityName","destinationFacilityId"]as Set,null,null,false);
	if(UtilValidate.isNotEmpty(childFacilities)){
		grProcTotKgs = 0;
		grProcTotKgFat = 0;
		grProcTotKgSnf = 0;
		grRecvTotKgs = 0;
		grRecvTotKgFat = 0;
		grRecvTotKgSnf = 0;
		for(childFacility in childFacilities){
			centersFatSnfMap = [:];
			childFacilityId = childFacility.get("facilityId");
			childFacilityCode = childFacility.get("facilityCode");
			childFacilityName = childFacility.get("facilityName");
			destinationFacilityId = childFacility.get("destinationFacilityId");
			if(childFacilityId==destinationFacilityId){
				Map facilityAgents = ProcurementNetworkServices.getFacilityAgents(dctx, UtilMisc.toMap("facilityId", childFacilityId));
				facilityIds = [];
				if(UtilValidate.isNotEmpty(facilityAgents)){
					facilityIds= (List) facilityAgents.get("facilityIds");
				}
				procConList =  [];
				procConList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN,facilityIds));
				procConList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));    		
	        	procConList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));    		
	        	procConList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "MILK_PROCUREMENT"));    		
				procConList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO ,fromDate));
				procConList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
				EntityCondition procEntityCondition = EntityCondition.makeCondition(procConList,EntityOperator.AND);
				procuredList = [];
				procuredList = delegator.findList("OrderHeaderItemProductAndFacility",procEntityCondition,null,null,null,false);
				BigDecimal procTotKgFat = 0;
				BigDecimal procTotKgSnf = 0;
				BigDecimal procTotKgs = 0;
				BigDecimal recvTotKgFat = 0;
				BigDecimal recvTotKgSnf = 0;
				BigDecimal recvTotKgs = 0;
				for(procurement in procuredList){
					BigDecimal qtyKgs = procurement.quantity;
					BigDecimal fat = procurement.fat;
					BigDecimal snf = procurement.snf;
					BigDecimal kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,fat);
					BigDecimal kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(qtyKgs,snf);
					procTotKgs += qtyKgs; 
					procTotKgFat += kgFat;
					procTotKgSnf += kgSnf;
				}
				grProcTotKgs += procTotKgs;
				grProcTotKgFat += procTotKgFat;
				grProcTotKgSnf += procTotKgSnf;
				if(procTotKgs!=0){
					centersFatSnfMap.put("facilityCode",childFacilityCode);
					centersFatSnfMap.put("facilityName",childFacilityName);
					centersFatSnfMap["procFat"] =  ProcurementNetworkServices.calculateFatOrSnf(procTotKgFat, procTotKgs);
					centersFatSnfMap["procSnf"] = ProcurementNetworkServices.calculateFatOrSnf(procTotKgSnf, procTotKgs);
				}
				//here we are calculating recieved fat ,snf
				//getting records of transfered Milk
				mpfRecievedConditionList = [];
				mpfRecievedConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,childFacilityId));
				mpfRecievedConditionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT"));
				mpfRecievedCondition = EntityCondition.makeCondition(mpfRecievedConditionList,EntityOperator.AND);
				mpfRecievedList = delegator.findList("MilkTransfer",mpfRecievedCondition,null,null,null,false);
				if(UtilValidate.isEmpty(mpfRecievedList)){
					centersFatSnfMap["recvFat"] = BigDecimal.ZERO;
					centersFatSnfMap["recvSnf"] = BigDecimal.ZERO;
				}else{
					for(mpfRecieved in mpfRecievedList){
						tempRecvKgs = mpfRecieved.receivedQuantity;
						tempRecvKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(tempRecvKgs,mpfRecieved.receivedFat);
						tempRecvKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(tempRecvKgs,mpfRecieved.receivedSnf);
						recvTotKgs += tempRecvKgs;
						recvTotKgFat += tempRecvKgFat;
						recvTotKgSnf += tempRecvKgSnf;
					}
					grRecvTotKgs += recvTotKgs;
					grRecvTotKgFat += recvTotKgFat;
					grRecvTotKgSnf += recvTotKgSnf;
					centersFatSnfMap["recvFat"] = ProcurementNetworkServices.calculateFatOrSnf(recvTotKgFat, recvTotKgs);
					centersFatSnfMap["recvSnf"] = ProcurementNetworkServices.calculateFatOrSnf(recvTotKgSnf, recvTotKgs);
				}
				if(UtilValidate.isNotEmpty(centersFatSnfMap.facilityName)){
					centersFatSnfList.add(centersFatSnfMap);
				}
			}	
		}
		tempfatSnfMap = [:];
		tempfatSnfMap.put("facilityCode","TOT");
		tempfatSnfMap.put("facilityName",facility.get("facilityName"));
		tempfatSnfMap.put("procFat", 0);
		tempfatSnfMap.put("procSnf", 0);
		tempfatSnfMap.put("recvFat", 0);
		tempfatSnfMap.put("recvSnf", 0);
		if(grProcTotKgs!=0){
			tempfatSnfMap.put("procFat", ProcurementNetworkServices.calculateFatOrSnf(grProcTotKgFat, grProcTotKgs));
			tempfatSnfMap.put("procSnf", ProcurementNetworkServices.calculateFatOrSnf(grProcTotKgSnf, grProcTotKgs));
		}
		if(grRecvTotKgs!=0){
			tempfatSnfMap.put("recvFat", ProcurementNetworkServices.calculateFatOrSnf(grRecvTotKgFat, grRecvTotKgs));
			tempfatSnfMap.put("recvSnf", ProcurementNetworkServices.calculateFatOrSnf(grRecvTotKgSnf, grRecvTotKgs));
		}
		centersFatSnfList.add(tempfatSnfMap);
		context.put("centersFatSnfList",centersFatSnfList);
	 }
	}
}

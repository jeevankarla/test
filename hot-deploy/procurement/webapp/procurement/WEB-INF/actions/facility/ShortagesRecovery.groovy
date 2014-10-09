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

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
fromDate = UtilDateTime.getDayStart(fromDate);
thruDate = UtilDateTime.getDayEnd(thruDate);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.shedId;
facility = delegator.findOne("Facility",[facilityId:facilityId],false);
context.put("facility",facility);
//shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : facilityId]);
shedUnits = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);
shedUnitIds =[];
shedUnitIds.addAll(shedUnits.unitsList);

List notTransferedUnitIds =FastList.newInstance();
notTransferedUnitIds.addAll(shedUnits.unitsList);

unitIds = [];
unitIds.addAll(shedUnits.unitsList);

productsList = [];
productsList = ProcurementNetworkServices.getProcurementProducts(dctx,UtilMisc.toMap());

totAmountsMap =[:];
totAmountsMap.put("cartage", BigDecimal.ZERO);
totAmountsMap.put("addnAmt", BigDecimal.ZERO);
totAmountsMap.put("commAmt", BigDecimal.ZERO);
totAmountsMap.put("opCost", BigDecimal.ZERO);
totAmountsMap.put("tipAmt", BigDecimal.ZERO);
for(product in productsList){
	totAmountsMap.put(product.get("brandName")+"TipAmt",BigDecimal.ZERO);
	}
facilityShortagesList = [];
tipAmt = 0;
if(UtilValidate.isNotEmpty(facility)){
	List transferedFacililitiesCondList = FastList.newInstance();
	transferedFacililitiesCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
	transferedFacililitiesCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds),EntityOperator.OR, EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,unitIds)));
	transferedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	transferedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	transferedFacililitiesCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
	EntityCondition transferedFacililitiesCondition = EntityCondition.makeCondition(transferedFacililitiesCondList,EntityOperator.AND);

	transferedFacililitiesList = delegator.findList("MilkTransfer",transferedFacililitiesCondition,null,null,null,false);
	
	List transferedUnitIds =  EntityUtil.getFieldListFromEntityList(transferedFacililitiesList,"facilityId",true);
	notTransferedUnitIds.removeAll(transferedUnitIds);
	
	
	facilityShortages = [:];
	//here we are trying to get the units that are sending milk to Mpf
	List mpfReceivedFacililitiesCondList = FastList.newInstance();
	mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
	mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
	mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT"));
	mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
	EntityCondition mpfReceivedFacililitiesCondition = EntityCondition.makeCondition(mpfReceivedFacililitiesCondList,EntityOperator.AND);

	mpfReceivedFacililitiesList = delegator.findList("MilkTransfer",mpfReceivedFacililitiesCondition,null,null,null,false);
	
	Set mpfReceivedFacilityIds= null;
	mpfReceivedFacilitiesList = EntityUtil.getFieldListFromEntityList(mpfReceivedFacililitiesList,"facilityId", true);
	mpfReceivedFacilityIds = new HashSet(mpfReceivedFacilitiesList);
	
	mpfReceivedFacilityIdsList = mpfReceivedFacilityIds.toList();
	if(UtilValidate.isEmpty(mpfReceivedFacilityIdsList)){
		mpfReceivedFacililitiesCondList.clear();
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.NOT_IN,unitIds));
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
		mpfReceivedFacililitiesCondition = EntityCondition.makeCondition(mpfReceivedFacililitiesCondList,EntityOperator.AND);
		mpfReceivedFacililitiesList = delegator.findList("MilkTransfer",mpfReceivedFacililitiesCondition,null,null,null,false);
		mpfReceivedFacilityIds= null;
		mpfReceivedFacilitiesList = EntityUtil.getFieldListFromEntityList(mpfReceivedFacililitiesList,"facilityId", true);
		mpfReceivedFacilityIds = new HashSet(mpfReceivedFacilitiesList);
		mpfReceivedFacilityIdsList = mpfReceivedFacilityIds.toList();
		}
	mpfReceivedFacilityIdsList.addAll(notTransferedUnitIds);
	mpfReceivedFacilityIdsList = (new HashSet(mpfReceivedFacilityIdsList)).toList(); 
	Collections.sort(mpfReceivedFacilityIdsList);
	
	mpfNotReceivedUnitIds = [];
	mpfNotReceivedUnitIds.addAll(unitIds);
	for(mpfReceivedFacilityId in mpfReceivedFacilityIds){
		mpfNotReceivedUnitIds.remove(mpfReceivedFacilityId);
		shedUnitIds.remove(mpfReceivedFacilityId);
		}
	//childFacilities = delegator.findList("Facility",EntityCondition.makeCondition("parentFacilityId",EntityOperator.EQUALS,facilityId),["facilityId","facilityCode","facilityName","destinationFacilityId"]as Set,null,null,false);
	
	// here childFacility represents which sends Milk to Main_plant
	// subchilds represents milk sent to child facility
	if(UtilValidate.isNotEmpty(mpfReceivedFacilityIdsList)){
		for(mpfReceivedFacilityId in mpfReceivedFacilityIdsList){
			childFacility = delegator.findOne("Facility",[facilityId:mpfReceivedFacilityId],false);
			childFacilityId = childFacility.get("facilityId");
			
			List unitReceivedFacililitiesCondList = FastList.newInstance();
			unitReceivedFacililitiesCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
			unitReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,shedUnitIds));
			unitReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,childFacilityId));
			unitReceivedFacililitiesCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
			unitReceivedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
			unitReceivedFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
			EntityCondition unitReceivedFacililitiesCondition = EntityCondition.makeCondition(unitReceivedFacililitiesCondList,EntityOperator.AND);

			unitReceivedFacililitiesList = delegator.findList("MilkTransfer",unitReceivedFacililitiesCondition,null,null,null,false);
			Set unitReceivedFacilityIds= null;
			unitReceivedFacilitiesList = EntityUtil.getFieldListFromEntityList(unitReceivedFacililitiesList,"facilityId", true);
			unitReceivedFacilityIds = new HashSet(unitReceivedFacilitiesList);
			unitReceivedFacilityIds.add(childFacilityId);
			unitIdsList = FastList.newInstance();
			unitIdsList = unitReceivedFacilityIds.toList();
			
			for(unitIds in unitIdsList){
				if(shedUnitIds.contains(unitIds)){
					shedUnitIds.remove(unitIds);
				}
			}
			
			tempMap = [:];
			tempMap.put("kgFat",BigDecimal.ZERO);
			tempMap.put("kgSnf",BigDecimal.ZERO);
			tempMap.put("kgFatAmt",BigDecimal.ZERO);
			tempMap.put("kgSnfAmt",BigDecimal.ZERO);
			tempMap.put("sPrice",BigDecimal.ZERO);
			
			// here we are getting unitIds that are sending to these units
			List unitRecdFacililitiesCondList = FastList.newInstance();
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,shedUnitIds));
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,unitIdsList));
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
			EntityCondition unitRecdFacililitiesCondition = EntityCondition.makeCondition(unitRecdFacililitiesCondList,EntityOperator.AND);

			List unitRecdFacililitiesList = delegator.findList("MilkTransfer",unitRecdFacililitiesCondition,null,null,null,false);
			Set unitRecdFacilityIds= null;
			unitRecdFacilitiesList = EntityUtil.getFieldListFromEntityList(unitRecdFacililitiesList,"facilityId", true);
			unitRecdFacilityIds = new HashSet(unitRecdFacilitiesList);
			List unitRecdIds = unitRecdFacilityIds.toList();
			unitIdsList.addAll(unitRecdIds);
			unitIdsList = (new HashSet(unitIdsList)).toList();
			
			Collections.sort(unitIdsList);
			
			for(unitIds in unitIdsList){
				if(shedUnitIds.contains(unitIds)){
					shedUnitIds.remove(unitIds);
				}
			}
			for(unitReceivedFacilityId in unitIdsList){
				subChildFacilityShortages=[:];
				subChildFacilityPeriodTotals =[];
				subChild = delegator.findOne("Facility",[facilityId:unitReceivedFacilityId],false);
				facilityId = subChild.get("facilityId");
				subChildFacilityPeriodTotals = dispatcher.runSync("getPeriodTransferTotals" , [fromDate: fromDate , thruDate: thruDate , facilityId: facilityId,userLogin:userLogin]);
				
				Map periodDetails = subChildFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers");
				
				Map procPeriodDetails = periodDetails.procurementPeriodTotals;
				
				Map dayTotalsMap = procPeriodDetails.dayTotals;
				
				
				if(UtilValidate.isEmpty(dayTotalsMap)){
					continue;
					}
				
				subChildFacilityShortages = subChildFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers").get("shortages");
				amountsMap =[:]
				//amountsMap = subChildFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers").get("amounts");
				// here we are populating totAmtMap for using in shedMilkBill Details
				
				unitBillAbstract = ProcurementNetworkServices.getUnitBillsAbstract(dctx , [customTimePeriodId: parameters.customTimePeriodId , unitId: facilityId]);
				if(UtilValidate.isNotEmpty(unitBillAbstract)){
					unitAbsTotals = unitBillAbstract.getAt("centerWiseAbsMap");
					unitGrndValuesTot = (unitAbsTotals).getAt("TOT");
					if(UtilValidate.isNotEmpty(unitGrndValuesTot)){
						for(product in productsList){
							productAbs = unitGrndValuesTot.getAt(product.productId);
							totAmountsMap.put(product.brandName+"TipAmt",((BigDecimal)totAmountsMap.get(product.brandName+"TipAmt")).add((BigDecimal)(productAbs.get("tipAmt"))));
						}
						totProductMap = unitGrndValuesTot.get("TOT");	
						if(UtilValidate.isNotEmpty(totProductMap)){
							BigDecimal tipAmount=totProductMap.get("tipAmt");
							BigDecimal opCost=totProductMap.get("opCost");
							BigDecimal cartage=totProductMap.get("cartage");
							BigDecimal commissionAmt=totProductMap.get("commissionAmount");
							BigDecimal addnTot=totProductMap.get("grsAddn");
							totAmountsMap.put("cartage", ((BigDecimal)totAmountsMap.get("cartage")).add((BigDecimal)(cartage)));
							totAmountsMap.put("addnAmt", ((BigDecimal)totAmountsMap.get("addnAmt")).add((BigDecimal)(addnTot)));
							totAmountsMap.put("commAmt", ((BigDecimal)totAmountsMap.get("commAmt")).add((BigDecimal)(commissionAmt)));
							totAmountsMap.put("opCost", ((BigDecimal)totAmountsMap.get("opCost")).add((BigDecimal)(opCost)));
							totAmountsMap.put("tipAmt", ((BigDecimal)totAmountsMap.get("tipAmt")).add((BigDecimal)(tipAmount)));
						}
					}
				}
				
				/*for( key in totAmountsMap.keySet()){
					if(UtilValidate.isNotEmpty(amountsMap.get(key))){
						totAmountsMap.put(key, ((BigDecimal)totAmountsMap.get(key)).add((BigDecimal)(amountsMap.get(key))));
						}
				}*/
				for(key in tempMap.keySet()){
					if((key!="sPrice")&&(key!="kgFat")&&(key!="kgSnf")){
						if(subChildFacilityShortages.get(key)<0){
							subChildFacilityShortages.putAt(key,((BigDecimal)subChildFacilityShortages.get(key)).setScale(0,BigDecimal.ROUND_HALF_EVEN));
							tempMap.put(key, (((BigDecimal)tempMap.get(key)).add((BigDecimal)subChildFacilityShortages.get(key))));
						}else {
							tempMap.put(key, ((BigDecimal)tempMap.get(key)).add(BigDecimal.ZERO));
						}	
					}
					if(("kgFat".equalsIgnoreCase(key))||("kgSnf".equalsIgnoreCase(key))){
						if(((BigDecimal)subChildFacilityShortages.get(key)).compareTo(BigDecimal.ZERO)<0){
							tempMap.put(key, (((BigDecimal)tempMap.get(key)).add((BigDecimal)subChildFacilityShortages.get(key).setScale(3,BigDecimal.ROUND_HALF_EVEN))));
						}
					}
				}
			    BigDecimal	sPrice = BigDecimal.ZERO;
				transfersMap =[:];
				transfersMap = subChildFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers");
				if(UtilValidate.isNotEmpty(transfersMap.get("procurementPeriodTotals").get("dayTotals"))){
					sPrice = sPrice.subtract(((BigDecimal)transfersMap.get("procurementPeriodTotals").get("dayTotals").get("TOT").get("sPrice")));
					if(UtilValidate.isNotEmpty(sPrice)){
						sPrice = sPrice.setScale(0,BigDecimal.ROUND_HALF_UP);
					}
					tempMap.put("sPrice",((BigDecimal)tempMap.get("sPrice")).subtract(sPrice));
				}
				subChildFacilityShortages.put("sPrice",sPrice);
				subChildFacilityShortages.put("facilityId",subChild.get("facilityId"));
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
BigDecimal totshrtKgFatAmt =0;
BigDecimal totshrtKgSnfAmt =0;
for(shortage in facilityShortagesList){
		if(shortage.facilityCode !="TOT"){
			totshrtKgFatAmt = totshrtKgFatAmt.add((BigDecimal) shortage.kgFatAmt);
			totshrtKgSnfAmt = totshrtKgSnfAmt.add((BigDecimal) shortage.kgSnfAmt)
			}
	}
for(key in totAmountsMap.keySet()){
	       if(UtilValidate.isNotEmpty(totAmountsMap.get(key))){
			 totAmountsMap[key] = ((BigDecimal)totAmountsMap.get(key)).setScale(0,BigDecimal.ROUND_HALF_UP);
	       }
	}
context.putAt("totshrtKgFatAmt", totshrtKgFatAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
context.putAt("totshrtKgSnfAmt", totshrtKgSnfAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
context.putAt("totAmountsMap",totAmountsMap);
context.put("facilityShortagesList",facilityShortagesList);

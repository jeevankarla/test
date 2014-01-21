import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.cache.EntityObjectCache;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.security.Provider.Service;
import java.sql.*;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;

import javax.naming.Context;
import javax.rmi.CORBA.Util;
import javax.xml.rpc.holders.BigDecimalHolder;

import javolution.util.FastMap;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.PriceServices;


dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
facilityId = parameters.unitId;
facility = delegator.findOne("Facility",[facilityId:facilityId],false);
context.put("facility",facility);
shedId = parameters.shedId;
shedFacility = delegator.findOne("Facility",[facilityId:shedId],false);
context.put("shed",shedFacility);

openingBalMap = [:];
tmPreparationMap =[:];// it is one of the input
closingBalList = [];
mpfReceiptsMap = [:];
procTotalsMap = [:];
dairyMap = [:]; // for showing All inputTotals except Opening Balance
totInMap = [:];// for showing all totals 
totOutMap = [:];// for showing all outputs including Closing Balance 
tempMap = [:]; // used for initialization
tempMap.put("qtyLtrs",BigDecimal.ZERO);
tempMap.put("qtyKgs",BigDecimal.ZERO);
tempMap.put("kgFat",BigDecimal.ZERO);
tempMap.put("kgSnf",BigDecimal.ZERO);
tempMap.put("fat",BigDecimal.ZERO);
tempMap.put("snf",BigDecimal.ZERO);

dairyMap.putAll(tempMap);
totInMap.putAll(tempMap);
totOutMap.putAll(tempMap);
tmPreparationMap.putAll(tempMap);


customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate= UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate= UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
fromDate = UtilDateTime.getDayStart(fromDate);
thruDate = UtilDateTime.getDayEnd(thruDate);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
openingBalMap = [:];
openingBalMap = ProcurementReports.getOpeningBalance(dctx,[facilityId:facilityId,customTimePeriodId:parameters.customTimePeriodId,userLogin:userLogin,periodTypeId:"PROC_BILL_MONTH"]);
if(UtilValidate.isNotEmpty(openingBalMap)){
	totInMap.putAll(openingBalMap.get("openingBalance"));
}
procThruTransfers = [];
if(UtilValidate.isNotEmpty(facility)){
	presentPeriodTotals = dispatcher.runSync("getPeriodTransferTotals" , [fromDate: fromDate , thruDate: thruDate , facilityId: facilityId,userLogin:userLogin]);
	if(UtilValidate.isNotEmpty(presentPeriodTotals)){
		transfersMap = [:];
		transfersMap =  presentPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers");
		procPeriodTotals = transfersMap.get("procurementPeriodTotals");
		if(UtilValidate.isNotEmpty(procPeriodTotals.get("dayTotals"))){
			procTotalsMap = procPeriodTotals.get("dayTotals").get("TOT");
			for(key in totInMap.keySet()){
				if(UtilValidate.isNotEmpty(procTotalsMap.get(key))){
					totInMap.put(key,((BigDecimal)totInMap.get(key)).add((BigDecimal)procTotalsMap.get(key)));
					dairyMap.put(key,((BigDecimal)dairyMap.get(key)).add((BigDecimal)procTotalsMap.get(key)));
				}				
			}
		}
		if(UtilValidate.isNotEmpty(transfersMap)){
				tmPreparationMap = transfersMap.get("tmPreparationOB");
				for(key in totInMap.keySet()){
					if(UtilValidate.isNotEmpty(tmPreparationMap.get(key))){
						totInMap.put(key,((BigDecimal)totInMap.get(key)).add((BigDecimal)tmPreparationMap.get(key)));
					}				
				}
				tempKgFat = (BigDecimal)(tmPreparationMap.get("kgFat"));
				tempKgSnf = (BigDecimal)(tmPreparationMap.get("kgSnf"));
				tempQtyKgs = (BigDecimal)(tmPreparationMap.get("qtyKgs"));
				if(UtilValidate.isNotEmpty(tempQtyKgs)&& tempQtyKgs!=0){
					tmPreparationMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(tempKgFat,tempQtyKgs));
					tmPreparationMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(tempKgSnf,tempQtyKgs));
				}else{
					tmPreparationMap.put("fat",0);
					tmPreparationMap.put("snf",0);
				}
		}
		outputTotals  = transfersMap.get("output");
		
		if(UtilValidate.isNotEmpty(outputTotals)){
			mpfReceiptsMap =  outputTotals.get("dayTotals").get("TOT");
			mpfReceiptsMap.put("qtyLtrs",ProcurementNetworkServices.convertKGToLitre(mpfReceiptsMap.get("qtyKgs")));
			for(key in totOutMap.keySet()){
				if(UtilValidate.isNotEmpty(mpfReceiptsMap.get(key))){
					totOutMap.put(key,((BigDecimal)totOutMap.get(key)).add((BigDecimal)mpfReceiptsMap.get(key)));
				}				
			}
		}
		outputEntriesList = transfersMap.get("outputEntries");
		if(UtilValidate.isNotEmpty(outputEntriesList)){
			for(outputEntry in outputEntriesList){
				kgFat = (BigDecimal)(outputEntry.get("kgFat"));
				kgSnf = (BigDecimal)(outputEntry.get("kgSnf"));
				qtyKgs = (BigDecimal)(outputEntry.get("qtyKgs"));
				outputEntryMap = [:];
				outputEntryMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(kgFat,qtyKgs));
				outputEntryMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(kgSnf,qtyKgs));
				outputEntryMap.put("kgFat",kgFat);
				outputEntryMap.put("kgSnf",kgSnf);
				outputEntryMap.put("qtyKgs",qtyKgs);
				outputEntryMap.put("qtyLtrs",outputEntry.get("qtyLts"));
				for(key in totOutMap.keySet()){
					if(UtilValidate.isNotEmpty(outputEntryMap.get(key))){
						totOutMap.put(key,((BigDecimal)totOutMap.get(key)).add((BigDecimal)outputEntryMap.get(key)));
					}
					
				}
				tempOutputEntryMap =[:];
				tempOutputEntryMap.putAll(outputEntryMap);
				tempOutputEntryMap.put("outputType",outputEntry.outputType);
				closingBalList.add(tempOutputEntryMap)	;
			}	
		}
	}
	// here we are getting the list of units transfers Milk to this unit
	childFacilityCondList = [];
	childFacilityCondList.add(EntityCondition.makeCondition("destinationFacilityId",EntityOperator.EQUALS,facilityId));
	childFacilityCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_EQUAL,facilityId));
	childFacilityCondition = EntityCondition.makeCondition(childFacilityCondList,EntityOperator.AND);
	childFacilitiesList = delegator.findList("Facility",childFacilityCondition,null,null,null,false);
	for(childFacility in childFacilitiesList){
		tranFacilityDetails = [:];
		tranFacilityMap = [:];
		tranFacilityId = childFacility.facilityId;
		tranFacilityCode = childFacility.facilityCode;
		tranFacilityName = childFacility.facilityName;
		// here we are taking procurement from Transfers Entity
		transfersCondList = [];
		transfersCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,tranFacilityId));
		transfersCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,facilityId));
		transfersCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
		transfersCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
		transfersCondition = EntityCondition.makeCondition(transfersCondList,EntityOperator.AND);
		transfersList = delegator.findList("MilkTransfer",transfersCondition,null,null,null,false);
		tranFacilityMap.put("facilityCode", tranFacilityCode);
		tranFacilityMap.put("facilityName", tranFacilityName);
		tranFacilityMap.put("kgFat", BigDecimal.ZERO);
		tranFacilityMap.put("kgSnf", BigDecimal.ZERO);
		tranFacilityMap.put("qtyKgs", BigDecimal.ZERO);
		tranFacilityMap.put("qtyLtrs", BigDecimal.ZERO);
		for(transfer in transfersList){
			tranFacilityMap.put("qtyKgs", tranFacilityMap.get("qtyKgs")+transfer.get("receivedQuantity"));
			tranFacilityMap.put("kgFat", tranFacilityMap.get("kgFat")+ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.get("receivedQuantity"),transfer.get("receivedFat")));
			tranFacilityMap.put("kgSnf", tranFacilityMap.get("kgSnf")+ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.get("receivedQuantity"),transfer.get("receivedSnf")));
		}
		tranFacilityMap.put("qtyLtrs",ProcurementNetworkServices.convertKGToLitre(tranFacilityMap.get("qtyKgs")));
		tranFacilityMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(tranFacilityMap.get("kgFat"),tranFacilityMap.get("qtyKgs")));
		tranFacilityMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(tranFacilityMap.get("kgSnf"),tranFacilityMap.get("qtyKgs")));
		procThruTransfers.add(tranFacilityMap);
		for(key in totInMap.keySet()){
			if(UtilValidate.isNotEmpty(tranFacilityMap.get(key))){
				totInMap.put(key,((BigDecimal)totInMap.get(key)).add((BigDecimal)tranFacilityMap.get(key)));
				dairyMap.put(key,((BigDecimal)dairyMap.get(key)).add((BigDecimal)tranFacilityMap.get(key)));
				}
				
			}
		}
	//here we are getting IUT Totals
	shedUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : shedId]);
	iutTransfers = [];
	if(UtilValidate.isNotEmpty(shedUnits.unitsList)){
			unitIds = [];	
			iutTransferMap =[:];
			unitIds = shedUnits.unitsList;
			iutTransfersCondList = [];
			iutTransfersCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_IN,unitIds));
			iutTransfersCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,facilityId));
			iutTransfersCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
			iutTransfersCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
			iutTransfersCondition = EntityCondition.makeCondition(iutTransfersCondList,EntityOperator.AND);
			iutTransfersList = delegator.findList("MilkTransfer",iutTransfersCondition,["facilityId"] as Set,null,null,false);
			if(UtilValidate.isNotEmpty(iutTransfersList)){
					iutUnitsList = [];
					iutUnitsList =  EntityUtil.getFieldListFromEntityList(iutTransfersList, "facilityId", false);
					if(UtilValidate.isNotEmpty(iutUnitsList)){
							iutUnitIdsSet = new HashSet(iutUnitsList);
							for(iutUnitId in iutUnitIdsSet){
									iutTranFacility = delegator.findOne("Facility",[facilityId:iutUnitId],false);
									iutTransfersCondList=[];
									iutTransfersCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,iutUnitId));
									iutTransfersCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,facilityId));
									iutTransfersCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
									iutTransfersCondList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
									iutTransfersCondition = EntityCondition.makeCondition(iutTransfersCondList,EntityOperator.AND);
									iutTransfersList = delegator.findList("MilkTransfer",iutTransfersCondition,null,null,null,false);
									
									iutTranFacilityMap = FastMap.newInstance();
									iutTranFacilityMap.putAll(tempMap);
									for(transfer in iutTransfersList){
										iutTranFacilityMap.put("qtyKgs", iutTranFacilityMap.get("qtyKgs")+transfer.get("receivedQuantity"));
										iutTranFacilityMap.put("kgFat", iutTranFacilityMap.get("kgFat")+ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.get("receivedQuantity"),transfer.get("receivedFat")));
										iutTranFacilityMap.put("kgSnf", iutTranFacilityMap.get("kgSnf")+ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.get("receivedQuantity"),transfer.get("receivedSnf")));
										}
									iutTranFacilityMap.put("qtyLtrs", ProcurementNetworkServices.convertKGToLitre(iutTranFacilityMap.get("qtyKgs")));
									for(key in totInMap.keySet()){
										totInMap.put(key,((BigDecimal)totInMap.get(key)).add((BigDecimal)iutTranFacilityMap.get(key)));
										}
									iutTranFacility = delegator.findOne("Facility",[facilityId:iutUnitId],false);
									iutTranFacilityMap.put("facilityCode",iutTranFacility.facilityCode);
									iutTranFacilityMap.put("facilityName",iutTranFacility.facilityName);
									
									iutTranFacilityMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(iutTranFacilityMap.get("kgFat"), iutTranFacilityMap.get("qtyKgs")));
									iutTranFacilityMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(iutTranFacilityMap.get("kgSnf"), iutTranFacilityMap.get("qtyKgs")));
									iutTransfers.add(iutTranFacilityMap);
								}
						}
				}
		}
	if(UtilValidate.isNotEmpty(dairyMap)){
		dairyMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(dairyMap.get("kgFat"),dairyMap.get("qtyKgs")));
		dairyMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(dairyMap.get("kgSnf"),dairyMap.get("qtyKgs")));
	}
	if(UtilValidate.isNotEmpty(totInMap)){
		totInMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(totInMap.get("kgFat"),totInMap.get("qtyKgs")));
		totInMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(totInMap.get("kgSnf"),totInMap.get("qtyKgs")));
	}
	if(UtilValidate.isNotEmpty(totOutMap)){
		totOutMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(totOutMap.get("kgFat"),totOutMap.get("qtyKgs")));
		totOutMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(totOutMap.get("kgSnf"),totOutMap.get("qtyKgs")));
	}
	context.putAt("iutTransfers", iutTransfers);
	context.putAt("openingBalMap", openingBalMap);
	context.putAt("closingBalList", closingBalList);
	context.putAt("procTotalsMap", procTotalsMap);
	context.putAt("procThruTransfers", procThruTransfers);
	context.putAt("mpfReceiptsMap", mpfReceiptsMap);
	context.putAt("dairyMap", dairyMap);
	context.putAt("totInMap", totInMap);
	context.putAt("totOutMap", totOutMap);
	context.putAt("tmPreparationMap", tmPreparationMap);
	results = "N";
	if((UtilValidate.isNotEmpty(iutTransfers))||(UtilValidate.isNotEmpty(openingBalMap))||(UtilValidate.isNotEmpty(tmPreparationMap))||(UtilValidate.isNotEmpty(closingBalList))||(UtilValidate.isNotEmpty(procTotalsMap))||(UtilValidate.isNotEmpty(procThruTransfers))||(UtilValidate.isNotEmpty(mpfReceiptsMap))||(UtilValidate.isNotEmpty(dairyMap))||(UtilValidate.isNotEmpty(totInMap))||(UtilValidate.isNotEmpty(totOutMap))){
			results = "Y";
	}
	context.put("results",results);
}
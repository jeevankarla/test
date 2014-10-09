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
Map shedUnits = FastMap.newInstance();
Map shedAllUnits = FastMap.newInstance();
shedAllUnits = ProcurementNetworkServices.getShedUnitsByShed(dctx,[shedId : facilityId]);
shedUnits = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);

List shedUnitFacilityIds = FastList.newInstance();
shedUnitFacilityIds.addAll(shedAllUnits.get("unitsList"));

List unitIds = FastList.newInstance();
unitIds.addAll(shedUnits.get("unitsList"));

List notTrnsferedIds = FastList.newInstance();
notTrnsferedIds.addAll(shedUnits.get("unitsList"));


 
Set interUnitTransFacIds = new HashSet()  ;
List transfersConditionList = FastList.newInstance();
transfersConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds),EntityOperator.OR, EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,unitIds)));
if("EGD".equalsIgnoreCase(parameters.shedId)){
	transfersConditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL,"104"));
	}
transfersConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
transfersConditionList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
transfersConditionList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
transfersConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
EntityCondition transfersCondition = EntityCondition.makeCondition(transfersConditionList,EntityOperator.AND);
List milkTransfersList = delegator.findList("MilkTransfer",transfersCondition,null,null,null,false);

/*List transferedUnitsConditionList = FastList.newInstance(); 
transferedUnitsConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,shedUnits));
EnitityCondition transferedUnitsCondition = EntityCondition.makeCondition(transferedUnitsConditionList,EntityOperator.AND);*/
List transferedUnitList =  EntityUtil.filterByCondition(milkTransfersList,EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
List transferUnitIds = EntityUtil.getFieldListFromEntityList(transferedUnitList,"facilityId",true);
notTrnsferedIds.removeAll(transferUnitIds);

if(UtilValidate.isNotEmpty(facility)){
	// Here we are getting List of units sending to Mpf 
	List mpfSendingCondList = FastList.newInstance();
	mpfSendingCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,"MAIN_PLANT"));
	mpfSendingCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
	EntityCondition mpfSendingCondition = EntityCondition.makeCondition(mpfSendingCondList,EntityOperator.AND);
	
	List mpfSendingList = EntityUtil.filterByCondition(milkTransfersList,mpfSendingCondition);
	Set mpfSendFacilityIds= null;
	mpfSendFacilitiesList = EntityUtil.getFieldListFromEntityList(mpfSendingList,"facilityId", true);
	mpfSendFacilityIds = new HashSet(mpfSendFacilitiesList);
	List mpfSendUnitIds =  mpfSendFacilityIds.toList();
	Collections.sort(mpfSendUnitIds);
	if(UtilValidate.isEmpty(mpfSendUnitIds)){
		mpfSendingCondList.clear();
		mpfSendingCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.NOT_IN,unitIds));
		mpfSendingCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
		mpfSendingCondition = EntityCondition.makeCondition(mpfSendingCondList,EntityOperator.AND);
		mpfSendingList = EntityUtil.filterByCondition(milkTransfersList,mpfSendingCondition);
		mpfSendFacilityIds= null;
		mpfSendFacilitiesList = EntityUtil.getFieldListFromEntityList(mpfSendingList,"facilityIdTo", true);
		mpfSendFacilityIds = new HashSet(mpfSendFacilitiesList);
		mpfSendUnitIds =  mpfSendFacilityIds.toList();
		mpfSendUnitIds.addAll((new HashSet(transferUnitIds)).toList());
		}
	mpfSendUnitIds.add(0,"MAIN_PLANT");
	mpfSendUnitIds.addAll(notTrnsferedIds);
	List mpfAcknoledgeList = FastList.newInstance();
	Map tempMap =FastMap.newInstance();
	tempMap.put("kgFat",BigDecimal.ZERO);
	tempMap.put("kgSnf",BigDecimal.ZERO);
	tempMap.put("qtyLtrs",BigDecimal.ZERO);
	tempMap.put("qtyKgs",BigDecimal.ZERO);
	
	Map grandTotalsMap = FastMap.newInstance();
	Map grandInputMap = FastMap.newInstance();
	Map grandOutputMap = FastMap.newInstance();
	Map grInputTotMap = FastMap.newInstance();
	Map grOutputTotMap = FastMap.newInstance();
	Map grShortMap = FastMap.newInstance();
	Map grExcesMap = FastMap.newInstance();
	grShortMap.put("kgFat",BigDecimal.ZERO);
	grShortMap.put("kgSnf",BigDecimal.ZERO);
	grExcesMap.put("kgFat",BigDecimal.ZERO);
	grExcesMap.put("kgSnf",BigDecimal.ZERO);
	
	grInputTotMap.putAll(tempMap);
	grOutputTotMap.putAll(tempMap);
	for(mpfSendUnit in mpfSendUnitIds){
		//here we are trying to get the units that are sending milk to MpfSendUnits
		List mpfReceivedFacililitiesCondList = FastList.newInstance();
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,mpfSendUnit));
		mpfReceivedFacililitiesCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
		EntityCondition mpfReceivedFacililitiesCondition = EntityCondition.makeCondition(mpfReceivedFacililitiesCondList,EntityOperator.AND);
	
		List mpfReceivedFacililitiesList = EntityUtil.filterByCondition(milkTransfersList,mpfReceivedFacililitiesCondition);
		Set mpfReceivedFacilityIds= null;
		mpfReceivedFacilitiesList = EntityUtil.getFieldListFromEntityList(mpfReceivedFacililitiesList,"facilityId", true);
		mpfReceivedFacilityIds = new HashSet(mpfReceivedFacilitiesList);
		
		List mpfReceivedFacilityIdsList = mpfReceivedFacilityIds.toList();
		sendToUnitIds = [];
		Collections.sort(mpfReceivedFacilityIdsList);
		// here we are removing mpfReceivedIds to get mpf not received unit Ids
		for(mpfReceivedId in mpfReceivedFacilityIdsList){
			unitIds.remove(mpfReceivedId);
			}
		Collections.sort(unitIds);
		
		if(!"MAIN_PLANT".equalsIgnoreCase(mpfSendUnit)){
			List unitRecdFacililitiesCondList = FastList.newInstance();
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds));
			unitRecdFacililitiesCondList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,mpfReceivedFacilityIdsList));
			EntityCondition unitRecdFacililitiesCondition = EntityCondition.makeCondition(unitRecdFacililitiesCondList,EntityOperator.AND);
	
			List unitRecdFacililitiesList = EntityUtil.filterByCondition(milkTransfersList,unitRecdFacililitiesCondition);
			Set unitRecdFacilityIds= null;
			unitRecdFacilitiesList = EntityUtil.getFieldListFromEntityList(unitRecdFacililitiesList,"facilityId", true);
			unitRecdFacilityIds = new HashSet(unitRecdFacilitiesList);
			//return;
			List unitRecdIds = unitRecdFacilityIds.toList();
			for(unitRecdId in unitRecdIds){
				unitIds.remove(unitRecdId);
				mpfReceivedFacilityIdsList.add(unitRecdId);
				}
			
		}
		
		if(notTrnsferedIds.contains(mpfSendUnit)){
			mpfReceivedFacilityIdsList.add(mpfSendUnit);
			}
		
		for(mpfReceivedFacilityId in mpfReceivedFacilityIdsList){
			String facilityId = mpfReceivedFacilityId;
			Map facilityDetails = delegator.findOne("Facility",[facilityId:facilityId],false);
			Map mpfAcknoledgeMap = FastMap.newInstance();
			String facilityCodeStr = facilityDetails.facilityCode;
			mpfAcknoledgeMap.put("facilityCode", facilityCodeStr.toInteger());
			mpfAcknoledgeMap.put("facilityName",facilityDetails.facilityName);
			Map mpfReceivedFacilityPeriodTotals = dispatcher.runSync("getPeriodTransferTotals" , [fromDate: fromDate , thruDate: thruDate , facilityId: facilityId,userLogin:userLogin]);
			
			Map periodDetails = mpfReceivedFacilityPeriodTotals.get("periodTransferTotalsMap").get(facilityId).get("transfers");
			Map procPeriodDetails = periodDetails.procurementPeriodTotals;
			Map dayTotalsMap = procPeriodDetails.dayTotals;
			Map inputTransfersMap = periodDetails.input;
			Map outputTransfersMap = periodDetails.output;
			
			if(UtilValidate.isEmpty(dayTotalsMap)&&UtilValidate.isEmpty(inputTransfersMap) && UtilValidate.isEmpty(outputTransfersMap)){
				continue;
			}
			
			Map openingBalanceMap = periodDetails.openingBalanceMap;
			Map shortages = periodDetails.shortages;
			Map openingBalance = FastMap.newInstance();
			openingBalance.putAll(tempMap);
			Map tmPreparationOB = periodDetails.tmPreparationOB;
			
			Map procPeriodTot = FastMap.newInstance();
			Map inputTot = FastMap.newInstance();
			Map outputTot = FastMap.newInstance();
			procPeriodTot.putAll(tempMap);
			inputTot.putAll(tempMap);
			outputTot.putAll(tempMap);
			List outputEntries = periodDetails.outputEntries;
			// these are used for iterating in report
			Map inputMap = FastMap.newInstance();
			Map outputMap = FastMap.newInstance();
			if(UtilValidate.isNotEmpty(dayTotalsMap)){
				procPeriodTot = procPeriodDetails.get("dayTotals").get("TOT");
				}
			if(UtilValidate.isNotEmpty(inputTransfersMap)){
					inputTot = inputTransfersMap.get("dayTotals").get("TOT");
					inputTot.put("qtyLtrs",inputTot.get("qtyLts"));
					inputTot.remove("qtyLts");
				}
			if(UtilValidate.isNotEmpty(outputTransfersMap)){
				outputTot = outputTransfersMap.get("dayTotals").get("TOT");
				outputTot.put("qtyLtrs",outputTot.get("qtyLts"));
				outputTot.remove("qtyLts");
			}
			List unitSendCondtionList = FastList.newInstance();
			unitSendCondtionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
			
			EntityCondition unitSendCondtion = EntityCondition.makeCondition(unitSendCondtionList,EntityOperator.AND);
			List unitSendFacilitiesList = EntityUtil.filterByCondition(milkTransfersList,unitSendCondtion);
			List unitSendFacilities = FastList.newInstance();
			Set unitSendFacilitiesSet= new HashSet();
			unitSendFacilitiesSet = new HashSet((List)EntityUtil.getFieldListFromEntityList(unitSendFacilitiesList,"facilityIdTo", true));
			
			unitSendFacilities = unitSendFacilitiesSet.toList();
			unitSendFacilities = (new HashSet(unitSendFacilities)).toList();
			if(UtilValidate.isNotEmpty(unitSendFacilities)){
				for(String unitSendFacilityId in unitSendFacilities){
					
					GenericValue sentFacility = delegator.findOne("Facility",[facilityId:unitSendFacilityId],false);
					List tranCondtionList = FastList.newInstance();
					tranCondtionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
					tranCondtionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,sentFacility.facilityId));
					EntityCondition tranCondtion = EntityCondition.makeCondition(tranCondtionList,EntityOperator.AND);
					
					List transfersList = EntityUtil.filterByCondition(milkTransfersList,tranCondtion);
					Map tranKgFatSnfMap = FastMap.newInstance();
					tranKgFatSnfMap.put("kgFat",BigDecimal.ZERO);
					tranKgFatSnfMap.put("kgSnf",BigDecimal.ZERO);
					tranKgFatSnfMap.put("qtyKgs",BigDecimal.ZERO);
					tranKgFatSnfMap.put("qtyLtrs",BigDecimal.ZERO);
					for(transfer in transfersList){
						if(UtilValidate.isNotEmpty(transfer.receivedQuantity)){
							tranKgFatSnfMap.put("qtyKgs",tranKgFatSnfMap.get("qtyKgs")+(transfer.receivedQuantity));
						}
						if(UtilValidate.isNotEmpty(transfer.receivedQuantityLtrs)){
							tranKgFatSnfMap.put("qtyLtrs",tranKgFatSnfMap.get("qtyLtrs")+(transfer.receivedQuantityLtrs));
						}else{
							  tranKgFatSnfMap.put("qtyLtrs",tranKgFatSnfMap.get("qtyLtrs")+ProcurementNetworkServices.convertKGToLitreSetScale(transfer.receivedQuantity, true));
						}
						if(UtilValidate.isNotEmpty(transfer.receivedKgFat)){
							tranKgFatSnfMap.put("kgFat",tranKgFatSnfMap.get("kgFat")+( transfer.receivedKgFat));
							}else{
							BigDecimal kgFat = BigDecimal.ZERO;
							kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.qtyKgs, transfer.receivedFat);
							tranKgFatSnfMap.put("kgFat",tranKgFatSnfMap.get("kgFat")+kgFat);
							}
						if(UtilValidate.isNotEmpty(transfer.receivedKgSnf)){
							tranKgFatSnfMap.put("kgSnf",tranKgFatSnfMap.get("kgSnf")+( transfer.receivedKgSnf));
							}else{
							BigDecimal kgSnf = BigDecimal.ZERO;
							kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.qtyKgs, transfer.receivedSnf);
							tranKgFatSnfMap.put("kgSnf",tranKgFatSnfMap.get("kgSnf")+kgSnf);
						}
						if((UtilValidate.isNotEmpty(transfer.milkType))){
							String milkType = (String)transfer.get("milkType");
							if (milkType.equals("S")) {
								BigDecimal sQtyLtrs = transfer.getBigDecimal("sQuantityLtrs");
								BigDecimal sFat = transfer.getBigDecimal("sFat");
								BigDecimal sSnf = transfer.getBigDecimal("sSnf");
								if(UtilValidate.isEmpty(sFat)){
									sFat = BigDecimal.ZERO;
								}
								if(UtilValidate.isEmpty(sSnf)){
									sSnf = BigDecimal.ZERO;
								}
								BigDecimal sKgFat = BigDecimal.ZERO;
								BigDecimal sKgSnf = BigDecimal.ZERO;
								if(UtilValidate.isNotEmpty(sQtyLtrs) && (sQtyLtrs.compareTo(BigDecimal.ZERO)>0)&&(sFat.compareTo(BigDecimal.ZERO)>0)){
									BigDecimal sQtyKgs = ProcurementNetworkServices.convertLitresToKG(sQtyLtrs);
									if(UtilValidate.isNotEmpty(transfer.get("sQtyKgs"))){
										sQtyKgs = transfer.get("sQtyKgs");
										}
									if(UtilValidate.isEmpty(transfer.get("sKgFat"))){
										sKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQtyKgs, sFat);
									}else{
										sKgFat = transfer.get("sKgFat");
									}
									if(UtilValidate.isEmpty(transfer.get("sKgSnf"))){
										sKgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQtyKgs, sSnf);
									}else{
										sKgSnf = transfer.get("sKgSnf");
									}
									tranKgFatSnfMap.put("qtyKgs",((BigDecimal)tranKgFatSnfMap.get("qtyKgs")).add(sQtyKgs) );
									tranKgFatSnfMap.put("qtyLtrs",((BigDecimal)tranKgFatSnfMap.get("qtyLtrs")).add(sQtyLtrs));
								}
								tranKgFatSnfMap.put("kgFat",((BigDecimal)tranKgFatSnfMap.get("kgFat")).add(sKgFat));
								tranKgFatSnfMap.put("kgSnf",((BigDecimal)tranKgFatSnfMap.get("kgSnf")).add(sKgSnf));
							}else if (milkType.equals("C")) {
									BigDecimal cQtyKgs = ProcurementNetworkServices.convertLitresToKG(transfer.cQuantityLtrs);
									tranKgFatSnfMap.put("qtyKgs",((BigDecimal)tranKgFatSnfMap.get("qtyKgs")).add(cQtyKgs) );
									tranKgFatSnfMap.put("qtyLtrs",((BigDecimal)tranKgFatSnfMap.get("qtyLtrs")).add(transfer.getBigDecimal("cQuantityLtrs")));
							}
							
						}
					}
				  //tranKgFatSnfMap.put("qtyLtrs",ProcurementNetworkServices.convertKGToLitreSetScale(tranKgFatSnfMap.get("qtyKgs"), true));
					Map outTransMap = FastMap.newInstance();
				  outTransMap.putAll(tranKgFatSnfMap);
				  if(UtilValidate.isNotEmpty(sentFacility.facilityCode)){
					  outputMap.put(sentFacility.facilityName+"("+sentFacility.facilityCode+")",outTransMap);
					  }else{
					  outputMap.put(sentFacility.facilityName,outTransMap);
					  }
				  
			  }
			}

	 		
			if(UtilValidate.isNotEmpty(openingBalanceMap)){
				openingBalance = openingBalanceMap.get("openingBalance"); 	
				}
			inputMap.put("OB",openingBalance);
			List inputEntries = periodDetails.inputEntries;
			if(inputEntries.size()>0){
					for(inputEntry in inputEntries){
						String outputType = inputEntry.outputType;
						Map entryMap = FastMap.newInstance();
						entryMap.put("qtyKgs", inputEntry.qtyKgs);
						entryMap.put("qtyLtrs", inputEntry.qtyLtrs);
						entryMap.put("kgFat", inputEntry.kgFat);
						entryMap.put("kgSnf", inputEntry.kgSnf);
						inputMap.put(outputType.replace("_", " "), entryMap);
						}
				}
			//inputMap.put("TM OB",tmPreparationOB);
			inputMap.put("PROCUREMENT",procPeriodTot);
			List unitReceivedCondtionList = FastList.newInstance();
			unitReceivedCondtionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,facilityId));
			
			EntityCondition unitReceivedCondtion = EntityCondition.makeCondition(unitReceivedCondtionList,EntityOperator.AND);
			List unitRecdFacilitiesList = EntityUtil.filterByCondition(milkTransfersList,unitReceivedCondtion);
			List unitRecdFacilities = FastList.newInstance();
			Set unitRecdFacilitiesSet= null;
			unitRecdFacilitiesSet = new HashSet((List)EntityUtil.getFieldListFromEntityList(unitRecdFacilitiesList,"facilityId", true));
			unitRecdFacilities = unitRecdFacilitiesSet.toList();
			unitRecdFacilities = unitRecdFacilities.sort();
			for(String unitRecdFacilityId in unitRecdFacilities){
				  GenericValue recdFacility = delegator.findOne("Facility",[facilityId:unitRecdFacilityId],false);
				  List tranCondtionList = FastList.newInstance();
				  tranCondtionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,unitRecdFacilityId));
				  tranCondtionList.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.EQUALS,facilityId));
				  EntityCondition tranCondtion = EntityCondition.makeCondition(tranCondtionList,EntityOperator.AND);
				  List transfersList = EntityUtil.filterByCondition(milkTransfersList,tranCondtion);
				  Map tranKgFatSnfMap = FastMap.newInstance();
				  tranKgFatSnfMap.put("kgFat",BigDecimal.ZERO);
				  tranKgFatSnfMap.put("kgSnf",BigDecimal.ZERO);
				  tranKgFatSnfMap.put("qtyKgs",BigDecimal.ZERO);
				  tranKgFatSnfMap.put("qtyLtrs",BigDecimal.ZERO);
				  for(transfer in transfersList){
					  if(UtilValidate.isNotEmpty(transfer.receivedQuantity)){
						  tranKgFatSnfMap.put("qtyKgs",tranKgFatSnfMap.get("qtyKgs")+(transfer.receivedQuantity));
					  }
					  if(UtilValidate.isNotEmpty(transfer.receivedQuantityLtrs)){
						  tranKgFatSnfMap.put("qtyLtrs",tranKgFatSnfMap.get("qtyLtrs")+(transfer.receivedQuantityLtrs));
					  }else{
							tranKgFatSnfMap.put("qtyLtrs",tranKgFatSnfMap.get("qtyLtrs")+ProcurementNetworkServices.convertKGToLitreSetScale(transfer.receivedQuantity, true));
					  }
					  if(UtilValidate.isNotEmpty(transfer.receivedKgFat)){
						  tranKgFatSnfMap.put("kgFat",tranKgFatSnfMap.get("kgFat")+( transfer.receivedKgFat));
						  }else{
						  BigDecimal kgFat = BigDecimal.ZERO;
						  kgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.qtyKgs, transfer.receivedFat);
						  tranKgFatSnfMap.put("kgFat",tranKgFatSnfMap.get("kgFat")+kgFat);
						  }
					  if(UtilValidate.isNotEmpty(transfer.receivedKgSnf)){
						  tranKgFatSnfMap.put("kgSnf",tranKgFatSnfMap.get("kgSnf")+( transfer.receivedKgSnf));
						  }else{
						  BigDecimal kgSnf = BigDecimal.ZERO;
						  kgSnf = ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.qtyKgs, transfer.receivedSnf);
						  tranKgFatSnfMap.put("kgSnf",tranKgFatSnfMap.get("kgSnf")+kgSnf);
					  }
						  
					  if((UtilValidate.isNotEmpty(transfer.milkType))){
						  String milkType = (String)transfer.get("milkType");
						  if (milkType.equals("S")) {
							  BigDecimal sQtyLtrs = transfer.getBigDecimal("sQuantityLtrs");
							  BigDecimal sFat = transfer.getBigDecimal("sFat");
							  if(UtilValidate.isEmpty(sFat)){
								  sFat = BigDecimal.ZERO;
							  }
							  BigDecimal sKgFat = BigDecimal.ZERO;
							  BigDecimal sKgSnf = BigDecimal.ZERO;
							  if(UtilValidate.isNotEmpty(sQtyLtrs) && (sQtyLtrs.compareTo(BigDecimal.ZERO)>0)&&(sFat.compareTo(BigDecimal.ZERO)>0)){
								  BigDecimal sQtyKgs = ProcurementNetworkServices.convertLitresToKG(sQtyLtrs);
								  sKgFat = ProcurementNetworkServices.calculateKgFatOrKgSnf(sQtyKgs, sFat);
								  tranKgFatSnfMap.put("qtyKgs",((BigDecimal)tranKgFatSnfMap.get("qtyKgs")).add(sQtyKgs) );
								  tranKgFatSnfMap.put("qtyLtrs",((BigDecimal)tranKgFatSnfMap.get("qtyLtrs")).add(sQtyLtrs));
							  }
							  tranKgFatSnfMap.put("kgFat",((BigDecimal)tranKgFatSnfMap.get("kgFat")).add(sKgFat));
						  }else if (milkType.equals("C")) {
								  BigDecimal cQtyKgs = ProcurementNetworkServices.convertLitresToKG(transfer.cQuantityLtrs);
								  tranKgFatSnfMap.put("qtyKgs",((BigDecimal)tranKgFatSnfMap.get("qtyKgs")).add(cQtyKgs) );
								  tranKgFatSnfMap.put("qtyLtrs",((BigDecimal)tranKgFatSnfMap.get("qtyLtrs")).add(transfer.getBigDecimal("cQuantityLtrs")));
						  }
						  
					  }
				  }
				//tranKgFatSnfMap.put("qtyLtrs",ProcurementNetworkServices.convertKGToLitreSetScale(tranKgFatSnfMap.get("qtyKgs"), true));
				Map inputTransMap = FastMap.newInstance();
				inputTransMap.putAll(tranKgFatSnfMap);
				String recdFacName = recdFacility.facilityName;
				if(recdFacName.size()>15){
					recdFacName = recdFacName.substring(0,15)
				}
				if(!shedUnitFacilityIds.contains(unitRecdFacilityId)){
					if(recdFacName.size()>15){
						recdFacName = recdFacName.substring(0,12)
					}
					inputMap.put(recdFacName+"("+"IUT"+")",inputTransMap);
				}else if(UtilValidate.isNotEmpty(recdFacility.facilityCode)){
							inputMap.put(recdFacName+"("+recdFacility.facilityCode+")",inputTransMap);
					}else{
						inputMap.put(recdFacility.facilityName,inputTransMap);
					}
			}
			
			//inputMap.put("IUT RECEIPTS",inputTot);
			if(UtilValidate.isNotEmpty(outputEntries)){
				outputEntries=UtilMisc.sortMaps(outputEntries, UtilMisc.toList("-outputType"));
					for(outputEntry in outputEntries){
						Map entryMap = FastMap.newInstance();
						entryMap.put("qtyKgs", outputEntry.qtyKgs);
						entryMap.put("qtyLtrs", outputEntry.qtyLts);
						entryMap.put("kgFat", outputEntry.kgFat);
						entryMap.put("kgSnf", outputEntry.kgSnf);
						outputMap.put(outputEntry.outputType, entryMap);		
						}
				}
			
			Map totInputMap = FastMap.newInstance();
			Map totOutputMap = FastMap.newInstance();
			Map totShrtMap = FastMap.newInstance();
			totInputMap.putAll(tempMap);
			totOutputMap.putAll(tempMap);
			totShrtMap.putAll(tempMap);
			totShrtMap.remove("qtyLtrs");
			totShrtMap.remove("qtyKgs");
			Map tempTotInputMap = FastMap.newInstance();
			Map tempTotOutputMap = FastMap.newInstance();
			Map tempTotShrtMap = FastMap.newInstance();
			tempTotInputMap.putAll(tempMap);
			tempTotOutputMap.putAll(tempMap);
			tempTotShrtMap.putAll(tempMap);
			tempTotShrtMap.remove("qtyLtrs");
			tempTotShrtMap.remove("qtyKgs");
			//here we are trying to get InputTot
			if(UtilValidate.isNotEmpty(inputMap)){
				for(inputKey in inputMap.keySet()){
					Map tempInputMap = inputMap.get(inputKey);
					Map tempGrandInputMap = FastMap.newInstance();
					tempGrandInputMap.putAll(tempMap);
					if(UtilValidate.isNotEmpty(grandInputMap)&&(UtilValidate.isNotEmpty(grandInputMap.get(inputKey)))){
						tempGrandInputMap = grandInputMap.get(inputKey);
					}
					
					for(key in tempTotInputMap.keySet()){
							if(UtilValidate.isNotEmpty(tempInputMap.get(key))){
								tempTotInputMap.put(key, tempTotInputMap.get(key)+tempInputMap.get(key));
								grInputTotMap.put(key, grInputTotMap.get(key)+tempInputMap.get(key));
								tempGrandInputMap.put(key, tempGrandInputMap.get(key)+tempInputMap.get(key));
							}
						}
					grandInputMap.put(inputKey, tempGrandInputMap);
				}
			}
			//here we are trying to get outputTot
			if(UtilValidate.isNotEmpty(outputMap)){
				for(outputKey in outputMap.keySet()){
					Map tempOutputMap = outputMap.get(outputKey);
					Map tempGrandOutputMap = FastMap.newInstance();
					tempGrandOutputMap.putAll(tempMap);
					if(UtilValidate.isNotEmpty(grandOutputMap)&&(UtilValidate.isNotEmpty(grandOutputMap.get(outputKey)))){
						tempGrandOutputMap = grandOutputMap.get(outputKey);
					}
					
					for(key in tempTotOutputMap.keySet()){
							if(UtilValidate.isNotEmpty(tempOutputMap.get(key))){
								tempTotOutputMap.put(key, tempTotOutputMap.get(key)+tempOutputMap.get(key));
								tempGrandOutputMap.put(key, tempGrandOutputMap.get(key)+tempOutputMap.get(key));
								grOutputTotMap.put(key, grOutputTotMap.get(key)+tempOutputMap.get(key));
							}
						}
					grandOutputMap.put(outputKey, tempGrandOutputMap);
				}
			}
			//here we are trying to get shrtages Tot
			if(UtilValidate.isNotEmpty(tempTotShrtMap)){
				for(key in tempTotShrtMap.keySet()){
					if(UtilValidate.isNotEmpty(shortages.get(key))){
						   tempTotShrtMap.put(key, tempTotShrtMap.get(key)+shortages.get(key));
						   if(shortages.get(key)<0){
								   grShortMap.put(key,grShortMap.get(key)+shortages.get(key) );
							   }else{
								   grExcesMap.put(key,grExcesMap.get(key)+shortages.get(key) );
							   }
						}
				   }
			}
			totInputMap.put("TOT INPUT",tempTotInputMap);
			totOutputMap.put("TOT OUTPUT",tempTotOutputMap);
			
			mpfAcknoledgeMap.put("input",inputMap);
			mpfAcknoledgeMap.put("output",outputMap);
			mpfAcknoledgeMap.put("shortages",shortages);
			mpfAcknoledgeMap.put("totInput",totInputMap);
			mpfAcknoledgeMap.put("totOutput",totOutputMap);
			mpfAcknoledgeMap.put("totShrt",tempTotShrtMap);
			
			mpfAcknoledgeList.add(mpfAcknoledgeMap);
		}
		
		//mpfAcknoledgeList=UtilMisc.sortMaps(mpfAcknoledgeList, UtilMisc.toList("facilityCode"));
  }
	Map grTotInput =FastMap.newInstance();
	Map grTotOutput =FastMap.newInstance();
	Map shortagesMap = FastMap.newInstance();
	shortagesMap.put("shrtKgFat", 0);
	shortagesMap.put("shrtKgSnf", 0);
	shortagesMap.put("excesKgFat", 0);
	shortagesMap.put("excesKgSnf", 0);
	if(UtilValidate.isNotEmpty(grShortMap)){
		shortagesMap.put("shrtKgFat", grShortMap.get("kgFat"));
		shortagesMap.put("shrtKgSnf", grShortMap.get("kgSnf"));
	}
	if(UtilValidate.isNotEmpty(grExcesMap)){
		shortagesMap.put("excesKgFat", grExcesMap.get("kgFat"));
		shortagesMap.put("excesKgSnf", grExcesMap.get("kgSnf"));
	}
	
	grTotInput.put("TOT INPUT",grInputTotMap);
	grTotOutput.put("TOT OUTPUT",grOutputTotMap);
	
	grandTotalsMap.put("totInput",grTotInput);
	grandTotalsMap.put("totOutput",grTotOutput);
	grandTotalsMap.put("input", grandInputMap);
	grandTotalsMap.put("output", grandOutputMap);
	grandTotalsMap.put("TOT INPUT", grandInputMap);
	grandTotalsMap.put("output", grandOutputMap);
	grandTotalsMap.put("shortages", shortagesMap);
	
	
	context.put("mpfAcknoledgeList",mpfAcknoledgeList);
	context.put("grandTotalsMap",grandTotalsMap);	
	
}// end of if
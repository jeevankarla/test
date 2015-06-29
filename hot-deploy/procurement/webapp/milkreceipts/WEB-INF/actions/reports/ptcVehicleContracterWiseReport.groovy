
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
import org.ofbiz.party.party.PartyHelper;

contractorId=parameters.partyId;
customTimePeriodId=parameters.customTimePeriodId;
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
dayBegin = UtilDateTime.getDayStart(fromDate);
dayEnd = UtilDateTime.getDayEnd(thruDate);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
//BigDecimal receivedUnionQty=BigDecimal.ZERO;

eachcontractorMap=[:];
totalsForPartiesMap=[:];
totalsForRecoveryMap=[:];
partyNames=[:];

containerIds=null;
contractors=null;
recoveryTypeIds=null;
contractorVehicleIds=null;
conditionList =[];

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "PTC_VEHICLE"));
 if(UtilValidate.isNotEmpty(contractorId) && (!"all".equalsIgnoreCase(contractorId))){
	context.contractorId=contractorId;
   	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , contractorId));
 }
EntityCondition contractCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
contractorVehicle = delegator.findList("VehicleRole", contractCond, null,null, null, false);
if(UtilValidate.isNotEmpty(contractorVehicle)){
	contractorVehicleIds=EntityUtil.getFieldListFromEntityList(contractorVehicle, "vehicleId", true);
}
if(UtilValidate.isNotEmpty(contractorVehicleIds)){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN , ["MXF_RECD","MXF_APPROVED"]));
	conditionList.add(EntityCondition.makeCondition("containerId", EntityOperator.IN, contractorVehicleIds));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	milkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null,null, null, false);
	
	if(UtilValidate.isNotEmpty(milkTransferList)){
		containerIds=EntityUtil.getFieldListFromEntityList(milkTransferList, "containerId", true);
		partyIds=EntityUtil.getFieldListFromEntityList(milkTransferList, "partyId", true);
		context.partyIds=partyIds;
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "PTC_VEHICLE"));
		conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.IN , containerIds));
		EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		vehicleRoleList = delegator.findList("VehicleRole", cond, null,null, null, false);
		if(UtilValidate.isNotEmpty(vehicleRoleList)){
			contractors=EntityUtil.getFieldListFromEntityList(vehicleRoleList, "partyId", true);
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
		conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, "DISTANCE_FROM_MD"));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd)));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		partyRate = delegator.findList("PartyRate", condition , null, null, null, false );
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.IN , containerIds));
		conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS , "TANKER_RATE"));
		conditionList.add(EntityCondition.makeCondition("rateCurrencyUomId", EntityOperator.EQUALS , "INR"));
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd)));
		EntityCondition con = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		vehicleRates = delegator.findList("VehicleRate", con, null,null, null, false);
		
		//ptcAddDeds = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.IN , ["PTC_ADDN","PTC_DED"]), null,null, null, false);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.IN , containerIds));
		conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS , customTimePeriodId));
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fineRecoveries = delegator.findList("FineRecovery",condition, null,null, null, false);
		if(UtilValidate.isNotEmpty(fineRecoveries)){
			recoveryTypeIds=EntityUtil.getFieldListFromEntityList(fineRecoveries, "recoveryTypeId", true);
			context.recoveryTypeIds=recoveryTypeIds;
		}
		
		totalTripsParties=0;
		totalDistanceParties=0;
		totalAmtParties=0;
		totalAdditions=0;
		totalDeductions=0;
		
		if(UtilValidate.isNotEmpty(contractors)){
			contractors.each{eachContractor->
				contractorList=EntityUtil.filterByCondition(vehicleRoleList,EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachContractor));
				eachVehicleMap=[:];
				contractorList.each{eachVehicle->
					partyIdsMap=[:];
					eachPartyMap=[:];
					vehiclesupplierParties=EntityUtil.filterByCondition(milkTransferList,EntityCondition.makeCondition("containerId", EntityOperator.EQUALS, eachVehicle.vehicleId));
					
					vehicleDetails = delegator.findOne("Vehicle",["vehicleId":eachVehicle.vehicleId],false);
					if(vehicleDetails){
						vehicleCapacity=vehicleDetails.get("vehicleCapacity");
					}
					eachPartyTrips=0;
					totPartyDistance=0;
					totAmount=0;
					totAdditionsForVehicle=0;
					totDeductionsForVehicle=0;
					partyIds.each{eachParty->
						eachSupplierData=EntityUtil.filterByCondition(vehiclesupplierParties,EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachParty));
						trips=0;
						if(UtilValidate.isNotEmpty(eachSupplierData)){
							eachSupplierData.each{eachTimeSupplier->
		                        partyDistance=0;
								vehicleRate=0;
								trips=trips+1;
								partyRateData=EntityUtil.filterByCondition(partyRate,EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachParty));
								if(UtilValidate.isNotEmpty(partyRateData) && trips >0 ){
									partyRateData = EntityUtil.getFirst(partyRateData);
									partyDistance = partyRateData.rateAmount;
									totPartyDistance=totPartyDistance+partyDistance
									//partyIdsMap.put("partyDistance",partyDistance);
								}
								receivedQuantity=eachTimeSupplier.receivedQuantity;
								receiveDate=eachTimeSupplier.receiveDate;
								conditionList.clear();
								conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , eachVehicle.vehicleId));
								conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, receiveDate));
								conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
									EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, receiveDate)));
							    con = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
								vehicleRatesForDate=EntityUtil.filterByCondition(vehicleRates,con);
								if(UtilValidate.isNotEmpty(vehicleRatesForDate)){
									vehicleRatesForDate = EntityUtil.getFirst(vehicleRatesForDate);
									vehicleRate = vehicleRatesForDate.rateAmount;
									partyIdsMap.put("vehicleRate",vehicleRate);
								}
								if(UtilValidate.isNotEmpty(vehicleRate) && UtilValidate.isNotEmpty(partyDistance) && UtilValidate.isNotEmpty(receivedQuantity) && vehicleCapacity>0){
									//amount = ((partyDistance.multiply(rateAmount)).multiply((receivedQuantity.divide(vehicleCapacity,2,BigDecimal.ROUND_HALF_UP)))).setScale(2,BigDecimal.ROUND_HALF_UP);
									amount = ((partyDistance*vehicleRate)*receivedQuantity)/(vehicleCapacity);
									amount=amount.setScale(2, BigDecimal.ROUND_HALF_UP);
									//partyIdsMap.put("amount",amount);
									totAmount=totAmount+amount;
								}
						   }
							
						}
						eachPartyTrips=eachPartyTrips+trips;
						eachPartyMap.put(eachParty, trips);
						
					    // total trips for each parties
						if(UtilValidate.isEmpty(totalsForPartiesMap) || (UtilValidate.isNotEmpty(totalsForPartiesMap) && UtilValidate.isEmpty(totalsForPartiesMap.get(eachParty)))){
							totalsForPartiesMap.put(eachParty, trips);
						}else{
							tempTrips=0;
							tempTrips=totalsForPartiesMap.get(eachParty);
							tempTrips=tempTrips+trips;
							totalsForPartiesMap.put(eachParty, tempTrips);
							}
					}
					// PTC additions and deductions
					fineRecoveryMap=[:];
					if(UtilValidate.isNotEmpty(recoveryTypeIds)){
						recoveryTypeIds.each {recoveryTypeId->
							conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS , recoveryTypeId));
							conditionList.add(EntityCondition.makeCondition("vehicleId", EntityOperator.EQUALS , eachVehicle.vehicleId));
							condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
							fineRecoveriesForVehicle=EntityUtil.filterByCondition(fineRecoveries,condition);
							if(UtilValidate.isNotEmpty(fineRecoveriesForVehicle)){
								amount=0;
								fineRecoveriesForVehicle = EntityUtil.getFirst(fineRecoveriesForVehicle);
								recoveryTypeId=fineRecoveriesForVehicle.recoveryTypeId;
								amount=fineRecoveriesForVehicle.amount;
								fineRecoveryMap.put(recoveryTypeId,amount);
								
								ptcAddDed = delegator.findOne("Enumeration",["enumId":recoveryTypeId],false);
								if(ptcAddDed  && ("PTC_ADDN".equals(ptcAddDed.get("enumTypeId"))) ){
									totAdditionsForVehicle=totAdditionsForVehicle+amount;
								}else
							      totDeductionsForVehicle=totDeductionsForVehicle+amount;
							  if(UtilValidate.isEmpty(totalsForRecoveryMap) || (UtilValidate.isNotEmpty(totalsForRecoveryMap) && UtilValidate.isEmpty(totalsForRecoveryMap.get(recoveryTypeId)))){
								  totalsForRecoveryMap.put(recoveryTypeId, amount);
							  }else{
								  tempAmt=0;
								  tempAmt=totalsForRecoveryMap.get(recoveryTypeId);
								  tempAmt=tempAmt+amount;
								  totalsForRecoveryMap.put(recoveryTypeId, tempAmt);
								  }
							}else{
						    fineRecoveryMap.put(recoveryTypeId,0);
							totalsForRecoveryMap.put(recoveryTypeId, 0);
							
							}
							
						}
					}
					partyIdsMap.put("fineRecoveryMap",fineRecoveryMap);
					
					totalTripsParties=totalTripsParties+eachPartyTrips;
					totalDistanceParties=totalDistanceParties+totPartyDistance;
					totalAmtParties=totalAmtParties+totAmount;
					partyIdsMap.put("partyIds",eachPartyMap);
					partyIdsMap.put("eachPartyTrips",eachPartyTrips);
					partyIdsMap.put("total",totAmount);
					partyIdsMap.put("totPartyDistance",totPartyDistance);
					partyIdsMap.put("totAdditionsForVehicle",totAdditionsForVehicle);
					partyIdsMap.put("totDeductionsForVehicle",totDeductionsForVehicle);
					
					eachVehicleMap.put(eachVehicle.vehicleId, partyIdsMap);
					
					totalAdditions=totalAdditions+totAdditionsForVehicle;
					totalDeductions=totalDeductions+totDeductionsForVehicle;
					
				}
				eachcontractorMap.put(eachContractor, eachVehicleMap);
				partyName =  PartyHelper.getPartyName(delegator, eachContractor, false);
				if(UtilValidate.isNotEmpty(partyName)){
					 partyNames.put(eachContractor,partyName);
				}
		
			}
		}
	context.partyNames=partyNames;
	context.totalTripsParties=totalTripsParties;
	context.totalDistanceParties=totalDistanceParties;
	context.totalAmtParties=totalAmtParties;
	
	context.totalAdditions=totalAdditions;
	context.totalDeductions=totalDeductions;
	
  }
}
context.eachcontractorMap=eachcontractorMap;
context.totalsForPartiesMap=totalsForPartiesMap;
context.totalsForRecoveryMap=totalsForRecoveryMap;

//Debug.log("partyNames===============5=totalsForRecoveryMap================="+totalsForRecoveryMap);

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
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
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
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
//getting customTimePeriod Ids based on fromDate and thrudate
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
Map shedUnitsByTimePeriod = FastMap.newInstance();

shedUnitsByTimePeriod = ProcurementNetworkServices.getShedCustomTimePeriodUnits(dctx,[shedId : parameters.shedId,customTimePeriodId : parameters.customTimePeriodId]);

List shedTimePeriodUnits = FastList.newInstance();
if(ServiceUtil.isSuccess(shedUnitsByTimePeriod)){
	shedTimePeriodUnits = shedUnitsByTimePeriod.get("unitsList");
	}
 Debug.log("shedTimePeriodUnits==========="+shedTimePeriodUnits);


shedUnitDetails = ProcurementNetworkServices.getShedUnitsByShed(dctx ,[userLogin: userLogin,shedId: parameters.shedId]);
unitsList = shedUnitDetails.get("unitsDetailList");
unitIds = shedUnitDetails.get("unitsList");

 timPeriodConditionList = [];
	timPeriodConditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,customTimePeriod.getDate("thruDate")));
	timPeriodConditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO,customTimePeriod.getDate("fromDate")));
	timPeriodConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, unitIds));
	timPeriodConditionList.add(EntityCondition.makeCondition("periodTypeId",EntityOperator.EQUALS,"PROC_BILL_MONTH"));
	EntityCondition timePeriodCondition = EntityCondition.makeCondition(timPeriodConditionList,EntityOperator.AND);
	customTimePeriodList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",timePeriodCondition,null,null,null,false);
	List timePeriodIds=FastList.newInstance();
	if(UtilValidate.isNotEmpty(customTimePeriodList)){
		timePeriodIds=EntityUtil.getFieldListFromEntityList(customTimePeriodList, "customTimePeriodId", true);
	}
	timePeriodIds = (new HashSet(timePeriodIds)).toList();
	falgValue="N";
	if(UtilValidate.isNotEmpty(timePeriodIds) && (timePeriodIds.size()>=2)){
		falgValue="Y";		
	}
	context.putAt("seperateIKPUnitsFlag", falgValue);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);

conditionList =[];

List  procInputOtuputTypeEnum = delegator.findList("Enumeration",EntityCondition.makeCondition(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_INPUT_TYPE"),EntityOperator.OR,EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_OUTPUT_TYPE")),null,null, null,false);
List inputConditionList = UtilMisc.toList(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_INPUT_TYPE"));
EntityCondition inputCondition = EntityCondition.makeCondition(inputConditionList,EntityOperator.AND);
List outputConditionList = UtilMisc.toList(EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"PROC_OUTPUT_TYPE"));
EntityCondition outputCondition = EntityCondition.makeCondition(outputConditionList,EntityOperator.AND);



List procInputTypes = EntityUtil.getFieldListFromEntityList((List)EntityUtil.filterByCondition(procInputOtuputTypeEnum,inputCondition),"enumId",false);
List procOutputTypes =EntityUtil.getFieldListFromEntityList((List)EntityUtil.filterByCondition(procInputOtuputTypeEnum,outputCondition),"enumId",false);


Map tempMap = FastMap.newInstance();
tempMap.put("qtyKgs", BigDecimal.ZERO);
tempMap.put("qtyLtrs", BigDecimal.ZERO);
tempMap.put("kgFat", BigDecimal.ZERO);
tempMap.put("kgSnf", BigDecimal.ZERO);
tempMap.put("fat", BigDecimal.ZERO);
tempMap.put("snf", BigDecimal.ZERO);


Map inputEntriesMap = FastMap.newInstance();
Map outputEntriesMap = FastMap.newInstance();
Map closingBalanceMap = FastMap.newInstance();
// initialization of inputTypes
for(procInputType in procInputTypes) {
	inputEntriesMap.put(procInputType,tempMap);
	}
// initialization of outputEntries
for(procOutputType in procOutputTypes) {
	outputEntriesMap.put(procOutputType,tempMap);
	}
closingBalanceMap.putAll(tempMap);
unitTotalsMap =[:];
conditionList.clear();
/*if(parameters.shedId){
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, parameters.shedId)));
}
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
unitsList = delegator.findList("Facility",condition,null,null,null,false);
unitIds =EntityUtil.getFieldListFromEntityList(unitsList, "facilityId", true);*/


if(UtilValidate.isEmpty(unitIds)){
	Debug.logError("This shed has no units","");
	context.errorMessage = "This Shed has no units.......!";
	return;
}
ikpUnits=[];
nonIkgUnits=[];


//getting IUT Totals
conditionList.clear();
periodStart =UtilDateTime.getDayStart(fromDate);
periodEnd =UtilDateTime.getDayEnd(thruDate);

List transfersConditionList = FastList.newInstance();
transfersConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.IN,unitIds),EntityOperator.OR, EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,unitIds)));
if("EGD".equalsIgnoreCase(parameters.shedId)){
	transfersConditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL,"104"));
	}
transfersConditionList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,periodEnd));
transfersConditionList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,periodStart));
transfersConditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
transfersConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isMilkRcpt", EntityOperator.NOT_EQUAL, "Y"),EntityJoinOperator.OR,EntityCondition.makeCondition("isMilkRcpt", EntityOperator.EQUALS, null)));
EntityCondition transfersCondition = EntityCondition.makeCondition(transfersConditionList,EntityOperator.AND);
List milkTransfersList = delegator.findList("MilkTransfer",transfersCondition,null,null,null,false);

List conditionList = FastList.newInstance();
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.NOT_IN, unitIds)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityIdTo", EntityOperator.IN ,unitIds)));
conditionValue = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List iutRecdMilkTransfers = EntityUtil.filterByCondition(milkTransfersList, conditionValue) ;
conditionList.clear();
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.IN, unitIds)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityIdTo",EntityOperator.NOT_EQUAL,"MAIN_PLANT"),EntityOperator.AND, EntityCondition.makeCondition("facilityIdTo",EntityOperator.NOT_IN,unitIds)));
conditionValue = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

List iutTransferedMilk = EntityUtil.filterByCondition(milkTransfersList, conditionValue) ;
List iutTransfers = FastList.newInstance();
Map iutTransferMap = FastMap.newInstance();
//here we are getting iut transfers
if(UtilValidate.isNotEmpty(iutTransferedMilk)){
	for(transfer in iutTransferedMilk){
		String facilityIdTo = transfer.get("facilityIdTo");
		Map toShed = FastMap.newInstance();
		Map shedInMap = FastMap.newInstance();
		shedInMap.put("userLogin",userLogin);
		shedInMap.put("facilityId",facilityIdTo);
		toShed = ProcurementNetworkServices.getShedDetailsForFacility(dctx, shedInMap);
		if(UtilValidate.isNotEmpty(toShed)&&(UtilValidate.isNotEmpty(toShed.get("facility")))){
			Map shedDetails = toShed.get("facility");
			Map transferMap = FastMap.newInstance();
			transferMap.put("qtyKgs",BigDecimal.ZERO);
			transferMap.put("qtyLtrs",BigDecimal.ZERO);
			transferMap.put("kgFat",BigDecimal.ZERO);
			transferMap.put("kgSnf",BigDecimal.ZERO);
			transferMap.put("qtyKgs", transfer.receivedQuantity);
			if(UtilValidate.isNotEmpty(transfer.receivedQuantityLtrs)){
				transferMap.put("qtyLtrs",transfer.receivedQuantityLtrs);
				}else{
				transferMap.put("qtyLtrs",ProcurementNetworkServices.convertKGToLitreSetScale(transfer.receivedQuantity,true));
				}
			
			if(UtilValidate.isNotEmpty(transfer.receivedKgFat)){
				transferMap.put("kgFat", transfer.receivedKgFat);
				}else{
				transferMap.put("kgFat", ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.receivedQuantity,milkTrns.receivedFat));
				}
			if(UtilValidate.isNotEmpty(transfer.receivedKgSnf)){
				transferMap.put("kgSnf",transfer.receivedKgSnf);
				}else{
				transferMap.put("kgSnf",ProcurementNetworkServices.calculateKgFatOrKgSnf(transfer.receivedQuantity,milkTrns.receivedSnf));
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
						transferMap.put("qtyKgs",((BigDecimal)transferMap.get("qtyKgs")).add(sQtyKgs) );
						transferMap.put("qtyLtrs",((BigDecimal)transferMap.get("qtyLtrs")).add(sQtyLtrs));
					}
					transferMap.put("kgFat",((BigDecimal)transferMap.get("kgFat")).add(sKgFat));
				}else if (milkType.equals("C")) {
						BigDecimal cQtyKgs = ProcurementNetworkServices.convertLitresToKG(transfer.cQuantityLtrs);
						transferMap.put("qtyKgs",((BigDecimal)transferMap.get("qtyKgs")).add(cQtyKgs) );
						transferMap.put("qtyLtrs",((BigDecimal)transferMap.get("qtyLtrs")).add(transfer.getBigDecimal("cQuantityLtrs")));
				}
				
			}
			String shedId = shedDetails.getString("facilityId");
			if(UtilValidate.isEmpty(iutTransferMap)|| ((UtilValidate.isNotEmpty(iutTransferMap))&&(UtilValidate.isEmpty(iutTransferMap.get(shedId))))){
				transferMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(transferMap.get("kgFat"), transferMap.get("qtyKgs")));
				transferMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(transferMap.get("kgSnf"), transferMap.get("qtyKgs")));
				
				iutTransferMap.put(shedId,transferMap);
				}else{
					Map tempTranMap = FastMap.newInstance();
					tempTranMap.putAll(iutTransferMap.get(shedId));
				    for(key in tempTranMap.keySet()){
						if(UtilValidate.isNotEmpty(tempTranMap.get(key)) && UtilValidate.isNotEmpty(transferMap.get(key))){
							tempTranMap.put(key, ((BigDecimal)tempTranMap.get(key)).add((BigDecimal)transferMap.get(key)));
						}
					}
					tempTranMap.put("fat",ProcurementNetworkServices.calculateFatOrSnf(tempTranMap.get("kgFat"), tempTranMap.get("qtyKgs")));
					tempTranMap.put("snf",ProcurementNetworkServices.calculateFatOrSnf(tempTranMap.get("kgSnf"), tempTranMap.get("qtyKgs")));
					iutTransferMap.put(shedId,tempTranMap);
				}
		}
	}
}

context.put("iutTransferMap",iutTransferMap);

IutTotalsMap =[:];
totIutQty=0;
totIutQtyLtrs =0;
totIutKgFat=0;
totIutKgSnf=0;
if(UtilValidate.isNotEmpty(iutRecdMilkTransfers)){
	iutRecdMilkTransfers.each{ milkTrns ->			
		totIutQty += milkTrns.receivedQuantity;
		if(UtilValidate.isNotEmpty(milkTrns.receivedQuantityLtrs)){
			totIutQtyLtrs += milkTrns.receivedQuantityLtrs;
			}else{
			totIutQtyLtrs += ProcurementNetworkServices.convertKGToLitreSetScale(milkTrns.receivedQuantity,true);
			}
		
		if(UtilValidate.isNotEmpty(milkTrns.receivedKgFat)){
			totIutKgFat += milkTrns.receivedKgFat;
			}else{
			totIutKgFat += ProcurementNetworkServices.calculateKgFatOrKgSnf(milkTrns.receivedQuantity,milkTrns.receivedFat);
			}
		if(UtilValidate.isNotEmpty(milkTrns.receivedKgSnf)){
			totIutKgSnf += milkTrns.receivedKgSnf;
			}else{
			totIutKgSnf += ProcurementNetworkServices.calculateKgFatOrKgSnf(milkTrns.receivedQuantity,milkTrns.receivedSnf);
			}
					
	}	
}
IutTotalsMap["iutQty"]=totIutQty;
IutTotalsMap["iutKgFat"]=totIutKgFat;
IutTotalsMap["iutKgSnf"]=totIutKgSnf;
IutTotalsMap["iutQtyLtrs"]=totIutQtyLtrs;
context.IutTotalsMap=IutTotalsMap;
//shedUnites = ProcurementNetworkServices.getShedAgents(dctx,UtilMisc.toMap("shedId", parameters.shedId));
totQty =0;
totKgFat=0;
totKgSnf=0;
totQtyLtrs=0;
tmObQty =0;
tmObKgFat=0;
tmObKgSnf=0;
tmObQtyLtrs=0;
shedWiseTotMap =[:];
openingBalMap =[:];
TMPreparationOB =[:];
unitsList.each{ unit ->
	String tempUnitId = unit.get("facilityId");
	openingBal = ProcurementReports.getOpeningBalance(dctx,[facilityId:unit.facilityId,customTimePeriodId:parameters.customTimePeriodId,userLogin:userLogin,periodTypeId:"PROC_BILL_MONTH"]);
	if(shedTimePeriodUnits.contains(tempUnitId)){
		if(UtilValidate.isNotEmpty(openingBal)){
			totQty = totQty+openingBal.get("openingBalance").get("qtyKgs");
			totKgFat = totKgFat+openingBal.get("openingBalance").get("kgFat");
			totKgSnf = totKgSnf+openingBal.get("openingBalance").get("kgSnf");
			totQtyLtrs = totQtyLtrs+openingBal.get("openingBalance").get("qtyLtrs");
		}
	}
	unitTotals = ProcurementReports.getPeriodTransferTotals(dctx , [userLogin: userLogin,fromDate: fromDate , thruDate: thruDate , facilityId: unit.facilityId]);
	
	
	if(UtilValidate.isNotEmpty(unitTotals)){
		unitTotalsMap[unit.facilityId] =[:];
		unitTotalsMap[unit.facilityId].putAll(unitTotals);
		
		Map transfersMap = FastMap.newInstance();
		Map transfersFacilityMap = FastMap.newInstance();
		Map periodTransferTotalsMap = FastMap.newInstance();
		periodTransferTotalsMap = unitTotals.get("periodTransferTotalsMap");
		if(UtilValidate.isNotEmpty(periodTransferTotalsMap)){
			transfersFacilityMap = unitTotals.get("periodTransferTotalsMap").get(unit.facilityId);
			if(UtilValidate.isNotEmpty(transfersFacilityMap)){
				transfersMap =  unitTotals.get("periodTransferTotalsMap").get(unit.facilityId).get("transfers");
				
				if(UtilValidate.isNotEmpty(transfersMap)){
					if("IKP".equals(unit.managedBy)){
						ikpUnits.add(unit.facilityId);
					}
					if("APDDCF".equals(unit.managedBy)){
						nonIkgUnits.add(unit.facilityId);
					}
					List inputEntriesList = FastList.newInstance(); 
					List outputEntriesList = FastList.newInstance();
					
					if(shedTimePeriodUnits.contains(tempUnitId)){
						inputEntriesList.addAll((List)transfersMap.get("inputEntries"));
						outputEntriesList.addAll((List)transfersMap.get("outputEntries"));
					}
					
					if(UtilValidate.isNotEmpty(outputEntriesList)){
						for(outputEntry in outputEntriesList){
							BigDecimal kgFat = (BigDecimal)(outputEntry.get("kgFat"));
							BigDecimal kgSnf = (BigDecimal)(outputEntry.get("kgSnf"));
							BigDecimal qtyKgs = (BigDecimal)(outputEntry.get("qtyKgs"));
							BigDecimal qtyLtrs = (BigDecimal)(outputEntry.get("qtyLts"));
							String outputType = (String)(outputEntry.get("outputType"));
							Map tempEntriesMap = FastMap.newInstance();
							if(UtilValidate.isEmpty(outputEntriesMap.get(outputType))){
								outputEntriesMap.put(outputType, tempMap);
							}
							tempEntriesMap.putAll(outputEntriesMap.get(outputType));
							
							tempEntriesMap.put("kgFat", ((BigDecimal)tempEntriesMap.get("kgFat")).add(kgFat));
							tempEntriesMap.put("kgSnf", ((BigDecimal)tempEntriesMap.get("kgSnf")).add(kgSnf));
							tempEntriesMap.put("qtyKgs", ((BigDecimal)tempEntriesMap.get("qtyKgs")).add(qtyKgs));
							tempEntriesMap.put("qtyLtrs", ((BigDecimal)tempEntriesMap.get("qtyLtrs")).add(qtyLtrs));
							if((qtyKgs.compareTo(BigDecimal.ZERO))!=0){
								tempEntriesMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(((BigDecimal)tempEntriesMap.get("kgFat")),((BigDecimal)tempEntriesMap.get("qtyKgs"))));
								tempEntriesMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(((BigDecimal)tempEntriesMap.get("kgSnf")),((BigDecimal)tempEntriesMap.get("qtyKgs"))));
							}
							
							if(outputType.equalsIgnoreCase("CLOSING_BALANCE")){
								closingBalanceMap.put("kgFat", ((BigDecimal)closingBalanceMap.get("kgFat")).add(kgFat));
								closingBalanceMap.put("kgSnf", ((BigDecimal)closingBalanceMap.get("kgSnf")).add(kgSnf));
								closingBalanceMap.put("qtyKgs", ((BigDecimal)closingBalanceMap.get("qtyKgs")).add(qtyKgs));
								closingBalanceMap.put("qtyLtrs", ((BigDecimal)closingBalanceMap.get("qtyLtrs")).add(qtyLtrs));
								if((qtyKgs.compareTo(BigDecimal.ZERO))!=0){
									closingBalanceMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(((BigDecimal)closingBalanceMap.get("kgFat")),((BigDecimal)closingBalanceMap.get("qtyKgs"))));
									closingBalanceMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(((BigDecimal)closingBalanceMap.get("kgSnf")),((BigDecimal)closingBalanceMap.get("qtyKgs"))));
								}
							}
							outputEntriesMap.put(outputType,tempEntriesMap);
							
							
						}
					}
					if(UtilValidate.isNotEmpty(inputEntriesList)){
						for(inputEntry in inputEntriesList){
							BigDecimal kgFat = (BigDecimal)(inputEntry.get("kgFat"));
							BigDecimal kgSnf = (BigDecimal)(inputEntry.get("kgSnf"));
							BigDecimal qtyKgs = (BigDecimal)(inputEntry.get("qtyKgs"));
							BigDecimal qtyLtrs = (BigDecimal)(inputEntry.get("qtyLtrs"));
							String outputType = (String)(inputEntry.get("outputType"));
							Map tempEntriesMap = FastMap.newInstance();
							if(UtilValidate.isEmpty(inputEntriesMap.get(outputType))){
								inputEntriesMap.put(outputType, tempMap);
							}
							tempEntriesMap.putAll(inputEntriesMap.get(outputType));
							
							tempEntriesMap.put("kgFat", ((BigDecimal)tempEntriesMap.get("kgFat")).add(kgFat));
							tempEntriesMap.put("kgSnf", ((BigDecimal)tempEntriesMap.get("kgSnf")).add(kgSnf));
							tempEntriesMap.put("qtyKgs", ((BigDecimal)tempEntriesMap.get("qtyKgs")).add(qtyKgs));
							tempEntriesMap.put("qtyLtrs", ((BigDecimal)tempEntriesMap.get("qtyLtrs")).add(qtyLtrs));
							if((qtyKgs.compareTo(BigDecimal.ZERO))!=0){
								tempEntriesMap.put("fat", ProcurementNetworkServices.calculateFatOrSnf(((BigDecimal)tempEntriesMap.get("kgFat")),((BigDecimal)tempEntriesMap.get("qtyKgs"))));
								tempEntriesMap.put("snf", ProcurementNetworkServices.calculateFatOrSnf(((BigDecimal)tempEntriesMap.get("kgSnf")),((BigDecimal)tempEntriesMap.get("qtyKgs"))));
							}
							inputEntriesMap.put(outputType,tempEntriesMap);
							
							
						}
					}	
					
				}
		  }	
		}
	}	
}
openingBalMap["qty"] = totQty;
openingBalMap["kgFat"] = totKgFat;
openingBalMap["kgSnf"] = totKgSnf;
openingBalMap["quantityLtrs"] = totQtyLtrs;

shedWiseTotMap["OpeningBalace"]=[:];
shedWiseTotMap["OpeningBalace"].putAll(openingBalMap);

TMPreparationOB["qty"] = tmObQty;
TMPreparationOB["kgFat"] = tmObKgFat;
TMPreparationOB["kgSnf"] = tmObKgSnf;
TMPreparationOB["quantityLtrs"] = tmObQtyLtrs;

shedWiseTotMap["TMPreparation"]=[:];
shedWiseTotMap["TMPreparation"].putAll(TMPreparationOB);
context.putAt("shedWiseTotMap", shedWiseTotMap);
shedTotals = ProcurementReports.getPeriodTransferTotals(dctx , [userLogin: userLogin,fromDate: fromDate , thruDate: thruDate , facilityId: parameters.shedId]);
context.putAt("ikpUnits", ikpUnits);
Debug.log("ikpUnits========"+ikpUnits);
context.putAt("nonIkgUnits", nonIkgUnits);
context.putAt("closingBalanceMap", closingBalanceMap)
context.putAt("inputEntriesMap", inputEntriesMap);
context.putAt("outputEntriesMap", outputEntriesMap);
context.putAt("shedTotals", shedTotals);
context.put("unitTotalsMap", unitTotalsMap);

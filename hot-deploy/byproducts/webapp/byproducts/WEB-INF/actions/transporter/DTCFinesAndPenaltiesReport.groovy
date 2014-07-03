import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
dctx = dispatcher.getDispatchContext();
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDateTime", fromDateTime);
context.put("thruDateTime", thruDateTime);

monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);

periodBillingId = null;
conditionList=[];
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PB_LMS_TRSPT_MRGN"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);
if(UtilValidate.isNotEmpty(periodBillingList)){
	for (int i = 0; i < periodBillingList.size(); ++i) {
		periodBillingDetails = periodBillingList.get(i);
		periodBillingId = periodBillingDetails.periodBillingId;
	}
}
contractorIdList=[];
contractorNamesMap =[:];
Map partyFacilityMap=(Map)ByProductNetworkServices.getFacilityPartyContractor(dctx, UtilMisc.toMap("saleDate",monthBegin)).get("partyAndFacilityList");
Iterator mapIter = partyFacilityMap.entrySet().iterator();
while (mapIter.hasNext()) {
	Map.Entry entry = mapIter.next();
	contractorId =entry.getKey();
	JSONArray labelsList= new JSONArray();
	contractorIdList.add(contractorId);
	label = PartyHelper.getPartyName(delegator, contractorId, false);
	contractorNamesMap.put(contractorId, label)
}

contractorRoutesMap = [:];
contractorIdList.each { contractorId ->
	
	routeWiseSaleMap = [:];
	facilityRecoveryInfoMap=[:];
	retailsList = partyFacilityMap.get(contractorId.trim());
	retailsList.each{eachFacilityId->
	quantityMap=[:];
	routeIdsList=[];
	facilityNamesMap=[:];
	conditionList. clear();
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,monthBegin));
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
	conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS ,eachFacilityId));
	
	EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	List<GenericValue> shipmentList = delegator.findList("Shipment", cond, null,UtilMisc.toList("routeId"), null, false);
	
	shipmentIds = EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", false);
	routeIdsList = EntityUtil.getFieldListFromEntityList(shipmentList, "routeId", false);
	Debug.log("shipmentIds==="+shipmentIds);
		if(UtilValidate.isNotEmpty(shipmentIds)){
			for(i=0;i<shipmentIds.size();i++){
				Map cratesMap = FastMap.newInstance();
				shipmentId=shipmentIds.get(i);
				routeId=routeIdsList.get(i);
				
				BigDecimal crateFineAmount = BigDecimal.ZERO;
				BigDecimal canFineAmount = BigDecimal.ZERO;
				BigDecimal finesAmount=BigDecimal.ZERO;
				BigDecimal transportAmount = BigDecimal.ZERO;
				BigDecimal securityFineAmount=BigDecimal.ZERO;
				BigDecimal remitFinesAmount=BigDecimal.ZERO;
				BigDecimal subTotal=BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(shipmentId)){
					routeId = routeId;
					Map<String, Object> facilityFineTempMap = FastMap.newInstance();
					    conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, routeId));
						EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
						try{
							List<GenericValue> facilityRecoveryList = FastList.newInstance();
							facilityRecoveryList = delegator.findList("FineRecovery", condition, null,null, null, false);
							if(UtilValidate.isNotEmpty(facilityRecoveryList)){
								facilityIdsList = EntityUtil.getFieldListFromEntityList(facilityRecoveryList, "facilityId", false);
								List<GenericValue>	allFaclityCrateFinesList = EntityUtil.filterByCondition(facilityRecoveryList, EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, "CRATES"));
								List<GenericValue>	allFaclityCanFinesList = EntityUtil.filterByCondition(facilityRecoveryList, EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, "CANS"));
								List<GenericValue>	allFaclityFinesList = EntityUtil.filterByCondition(facilityRecoveryList, EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, "FINES_PENALTIES"));
								List<GenericValue>	allFaclityTransportFinesList = EntityUtil.filterByCondition(facilityRecoveryList, EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, "FINES_TRNS_COST"));
								List<GenericValue>	allFaclitySecurityFineList = EntityUtil.filterByCondition(facilityRecoveryList, EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, "FINES_SECURITY"));
								List<GenericValue>	allFaclityRemitFinesList = EntityUtil.filterByCondition(facilityRecoveryList, EntityCondition.makeCondition("recoveryTypeId", EntityOperator.EQUALS, "FINES_CSH_SHORT"));
								
										for(GenericValue facilityRecCrate : allFaclityCrateFinesList){
											crateFineAmount=crateFineAmount.add(facilityRecCrate.getBigDecimal("amount"));
										}
										for(GenericValue facilityRecCan : allFaclityCanFinesList){
											canFineAmount=canFineAmount.add(facilityRecCan.getBigDecimal("amount"));
										}
										for(GenericValue facilityRecTrans : allFaclityFinesList){
											finesAmount=finesAmount.add(facilityRecTrans.getBigDecimal("amount"));
										}
										for(GenericValue facilityRecTrans : allFaclityTransportFinesList){
											transportAmount=transportAmount.add(facilityRecTrans.getBigDecimal("amount"));
										}
										for(GenericValue facilityRecSecurity : allFaclitySecurityFineList){
											securityFineAmount=securityFineAmount.add(facilityRecSecurity.getBigDecimal("amount"));
										}
										for(GenericValue facilityRecRemit : allFaclityRemitFinesList){
											remitFinesAmount=remitFinesAmount.add(facilityRecRemit.getBigDecimal("amount"));
										}
										if(UtilValidate.isEmpty(routeWiseSaleMap[routeId])){
											tempMap = [:];
											tempMap["cratesFine"]=crateFineAmount;
											tempMap["cansFine"]=canFineAmount;
											tempMap["finesAmount"]=finesAmount;
											tempMap["transportAmount"]=transportAmount;
											tempMap["securityFineAmount"]=securityFineAmount;
											tempMap["remitFinesAmount"]=remitFinesAmount;
											tempMap["subTotal"]=crateFineAmount+canFineAmount+finesAmount+transportAmount+transportAmount+remitFinesAmount;
											routeWiseSaleMap[routeId]=tempMap;
											
										 }else{
											 tempMap = [:];
											 tempMap.putAll(routeWiseSaleMap.get(routeId));
											 tempMap["cratesFine"]=crateFineAmount;
											 tempMap["cansFine"]=canFineAmount;
											 tempMap["finesAmount"]=finesAmount;
											 tempMap["transportAmount"]=transportAmount;
											 tempMap["securityFineAmount"]=transportAmount;
											 tempMap["remitFinesAmount"]=remitFinesAmount;
											 tempMap["subTotal"]=crateFineAmount+canFineAmount+finesAmount+transportAmount+transportAmount+remitFinesAmount;
										 }
							}
						}catch(GenericEntityException e){
							Debug.logError(e, module);
						}
				 }
			}
		}
	
	}
	if(UtilValidate.isNotEmpty(routeWiseSaleMap)){
		tempContMap = [:];
		tempContMap.putAll(routeWiseSaleMap);
	    contractorRoutesMap.put(contractorId, tempContMap);
	}
}
context.contractorNamesMap = contractorNamesMap;
context.contractorRoutesMap = contractorRoutesMap;

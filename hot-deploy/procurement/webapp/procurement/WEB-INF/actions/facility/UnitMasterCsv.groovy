
import java.sql.Timestamp
import java.util.List;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import org.ofbiz.party.party.PartyHelper;

if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
GenericValue mainPlantDetails = delegator.findOne("Facility",[facilityId : "MAIN_PLANT"], false)
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
Timestamp fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
Timestamp thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
fromDate = UtilDateTime.getDayStart(fromDate);
thruDate = UtilDateTime.getDayEnd(thruDate);
dctx = dispatcher.getDispatchContext();
shedUnits = ProcurementNetworkServices.getShedUnits(dctx ,context);
shedUnitsMap = (Map)shedUnits.get("shedUnits");
supplyTypeList= delegator.findList("Enumeration", EntityCondition.makeCondition([enumTypeId : 'PROC_SUPPLY_TYPE']), null, ['enumId'], null, true);

schemeTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SCHEME_TYPE"), null, null, null, true);

List unitTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_UNIT_CAT_TYPE"), null, null, null, true);

managedByList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_MNGNBY_TYPE"), null, null, null, true);
unitsList =[];
UnitMasterList =[];
if(UtilValidate.isNotEmpty(parameters.shedId)){
	unitsList = (List)shedUnitsMap[parameters.shedId];
	unitIdsList = (List)EntityUtil.getFieldListFromEntityList(unitsList,"facilityId", true);
	unitsList.each{ unitDetail ->		
		Map tempUnitMap = FastMap.newInstance();
		String facilityName = unitDetail.facilityName;
		tempUnitMap.put("UCODE", unitDetail.facilityCode);
		tempUnitMap.put("UNAME", facilityName);
		// Need to get clarification for UTYPE
		
		tempUnitMap.put("UTYPE","0");
		List unitTypeEnumDetailsList =  EntityUtil.filterByCondition(unitTypeList,EntityCondition.makeCondition("enumId",EntityOperator.EQUALS,unitDetail.categoryTypeEnum));
		GenericValue unitTypeEnumDetails = EntityUtil.getFirst(unitTypeEnumDetailsList);
		if(UtilValidate.isNotEmpty(unitTypeEnumDetails)){
			tempUnitMap.put("UTYPE",unitTypeEnumDetails.sequenceId)
		}
		
		tempUnitMap.put("UNITCATEGORY", unitDetail.categoryTypeEnum);
		tempUnitMap.put("SCHEME", "0");
		tempUnitMap.put("SCHEMEDESC", " ");
		schemeTypeList.each{schemeType ->
			if((schemeType.enumId).equals(unitDetail.schemeTypeId)){
				tempUnitMap.put("SCHEME", schemeType.sequenceId);
				tempUnitMap.put("SCHEMEDESC", schemeType.enumId);
			}			
		}	
		managedByList.each{managedDetails ->
			if((managedDetails.enumId).equals(unitDetail.managedBy)){
				tempUnitMap.put("MNGBY", managedDetails.sequenceId);
				tempUnitMap.put("MNGBYDESC", managedDetails.enumId);
			}
		}
		milkSentUnitDetails= null;
		if(UtilValidate.isNotEmpty(unitDetail.destinationFacilityId)){
			milkSentUnitDetails=delegator.findOne("Facility",[facilityId : unitDetail.destinationFacilityId], false);
			}
		
		if(UtilValidate.isNotEmpty(milkSentUnitDetails)){
			tempUnitMap.put("MILKSENTUNIT", milkSentUnitDetails.get("facilityName"));
			String unitFacilityId = unitDetail.facilityId;
			String destFacilityId=unitDetail.destinationFacilityId;
			if(unitFacilityId.equalsIgnoreCase(destFacilityId)){
				tempUnitMap.put("MILKSENTUNIT", mainPlantDetails.get("facilityName"));
				}
			
		}else{
			tempUnitMap.put("MILKSENTUNIT", " ");
		}
		tempUnitMap.put("DOP", unitDetail.openedDate);
		tempUnitMap.put("CAP", 0);
		if(unitDetail.facilitySize){
			tempUnitMap.put("CAP", unitDetail.facilitySize);
		}
		
		supplyTypeList.each{ supplyType ->
			Map inputMap = UtilMisc.toMap("userLogin", userLogin);
			inputMap.put("supplyTypeEnumId", supplyType.enumId);
			inputMap.put("facilityId", unitDetail.facilityId);
			inputMap.put("slabAmount", unitDetail.facilitySize);
			inputMap.put("fromDate", fromDate);
			inputMap.put("rateTypeId", "PROC_OP_COST");
			opCostRateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputMap);
			
			if("AM".equals(supplyType.get("enumId"))){
				BigDecimal amOpCost = opCostRateAmount.rateAmount;
				tempUnitMap.put("OPCOST", amOpCost);				
			}else{
				BigDecimal pmOpCost = opCostRateAmount.rateAmount;
				tempUnitMap.put("EOPCOST", pmOpCost);
			}
		}
		
		List<EntityCondition> condList = FastList.newInstance();
		
		condList.add(EntityCondition.makeCondition("statusId", "FNACT_ACTIVE"));
		condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, unitDetail.facilityId));
		List<GenericValue> facAccnts = delegator.findList("FacilityPersonAndFinAccount", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
		finAccnt = EntityUtil.getFirst(facAccnts);
		
		tempUnitMap.put("OCODE", unitDetail.facilityCode);
		tempUnitMap.put("GBCODE","0");
		tempUnitMap.put("BCODE", "0");
		tempUnitMap.put("BANO", "0");
		
		if(UtilValidate.isNotEmpty(finAccnt)  ){
			String tempBano = finAccnt.get("finAccountCode");
			tempBano = tempBano.trim();
			if( UtilValidate.isNotEmpty(tempBano)&& (!"0".equalsIgnoreCase(tempBano))){
				tempUnitMap.put("GBCODE", finAccnt.get("gbCode"));
				tempUnitMap.put("BCODE", finAccnt.get("bCode"));
				tempUnitMap.put("BANO", tempBano);
			}
		}
		tempUnitMap.put("PNAME", PartyHelper.getPartyName(delegator, unitDetail.ownerPartyId, true));
		tempUnitMap.put("DIST", unitDetail.district);
		tempUnitMap.put("USERID", "1");
		String destinationFacilityId = (String)unitDetail.get("destinationFacilityId");
		if(UtilValidate.isNotEmpty(destinationFacilityId)){
			GenericValue destinationFacDetails = delegator.findOne("Facility",[facilityId:destinationFacilityId],false);
			if(destinationFacDetails){
				tempUnitMap.put("OCODE", destinationFacDetails.facilityCode);
			}
			
			}
		
		List unitSendCondtionList = FastList.newInstance();
		unitSendCondtionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,unitDetail.facilityId));
		unitSendCondtionList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
		unitSendCondtionList.add(EntityCondition.makeCondition("receiveDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
		unitSendCondtionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MXF_RECD"));
		EntityCondition unitSendCondtion = EntityCondition.makeCondition(unitSendCondtionList,EntityOperator.AND);
		List unitSendFacilitiesList = delegator.findList("MilkTransfer",unitSendCondtion,["facilityIdTo"]as Set,null,null,false);
		List unitSendFacilities = FastList.newInstance();
		Set unitSendFacilitiesSet= new HashSet();
		if(unitSendFacilitiesList){
			unitSendFacilitiesSet = new HashSet((List)EntityUtil.getFieldListFromEntityList(unitSendFacilitiesList,"facilityIdTo", true));
		}
		
		unitSendFacilities = unitSendFacilitiesSet.toList();
		if(unitSendFacilities.size()>0){
				if((unitSendFacilities.contains("MAIN_PLANT"))){
					tempUnitMap.put("OCODE", unitDetail.facilityCode);
					}else{
					 GenericValue sendFacilityDetails = delegator.findOne("Facility",[facilityId:unitSendFacilities.get(0)],false);
					 tempUnitMap.put("OCODE", sendFacilityDetails.facilityCode);
					}
				// if this unit sends milk to Other ShedUnits then oCode should be the same UnitCode  	
				Boolean oCodeFlag = Boolean.TRUE;	
				for(unitSendFacilityId in unitSendFacilities){
						if((oCodeFlag)&&(!unitIdsList.contains(unitSendFacilityId))){
							tempUnitMap.put("OCODE", unitDetail.facilityCode);
							oCodeFlag = Boolean.FALSE;
						}
					}	
			}
		UnitMasterList.add(tempUnitMap);
	}
	
}
context.put("UnitMasterList",UnitMasterList);


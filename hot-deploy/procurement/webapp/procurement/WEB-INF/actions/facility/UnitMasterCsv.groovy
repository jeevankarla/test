
import java.util.List;

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import org.ofbiz.party.party.PartyHelper;

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

dctx = dispatcher.getDispatchContext();
shedUnits = ProcurementNetworkServices.getShedUnits(dctx ,context);
shedUnitsMap = (Map)shedUnits.get("shedUnits");
supplyTypeList= delegator.findList("Enumeration", EntityCondition.makeCondition([enumTypeId : 'PROC_SUPPLY_TYPE']), null, ['enumId'], null, true);

schemeTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_SCHEME_TYPE"), null, null, null, true);

managedByList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS,"PROC_MNGNBY_TYPE"), null, null, null, true);
unitsList =[];
UnitMasterList =[];
if(UtilValidate.isNotEmpty(parameters.shedId)){
	unitsList = (List)shedUnitsMap[parameters.shedId];
	
	unitsList.each{ unitDetail ->		
		
		Map tempUnitMap = FastMap.newInstance();
		
		tempUnitMap.put("UCODE", unitDetail.facilityCode);
		tempUnitMap.put("UNAME", unitDetail.facilityName);
		// Need to get clarification for UTYPE
		tempUnitMap.put("UTYPE", "1");
		schemeTypeList.each{schemeType ->
			if((schemeType.enumId).equals(unitDetail.schemeTypeId)){
				tempUnitMap.put("SCHEME", schemeType.sequenceId);
			}			
		}	
		managedByList.each{managedDetails ->
			if((managedDetails.enumId).equals(unitDetail.managedBy)){
				tempUnitMap.put("MNGBY", managedDetails.sequenceId);
			}
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
		if(finAccnt){
			tempUnitMap.put("OCODE", 0);
			tempUnitMap.put("GBCODE", finAccnt.get("gbCode"));
			tempUnitMap.put("BCODE", finAccnt.get("bCode"));
			tempUnitMap.put("BANO", finAccnt.finAccountCode);
		}
		tempUnitMap.put("PNAME", PartyHelper.getPartyName(delegator, unitDetail.ownerPartyId, true));
		tempUnitMap.put("DIST", unitDetail.district);
		tempUnitMap.put("USERID", "1");
		UnitMasterList.add(tempUnitMap);
	}
	
}
context.put("UnitMasterList",UnitMasterList);


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

dctx = dispatcher.getDispatchContext();
shedUnits = ProcurementNetworkServices.getShedUnits(dctx ,context);
shedUnitsMap = (Map)shedUnits.get("shedUnits");
centerMasterList =[];
if(UtilValidate.isNotEmpty(parameters.shedId)){
	facilityFinaccountMap = (ProcurementNetworkServices.getShedFacilityFinAccount(dctx, UtilMisc.toMap("facilityId",parameters.shedId ))).get("facAccntsMap");
	unitsList=[];
	if(UtilValidate.isNotEmpty(parameters.unitId)){
		unitFacility = delegator.findOne("Facility",[facilityId:parameters.unitId],false);
		context.putAt("unitDetails",unitFacility);
		Map tempMap = FastMap.newInstance();
		tempMap.put("facilityId", unitFacility.facilityId);
		tempMap.put("facilityCode", unitFacility.facilityCode);		
		unitsList.add(tempMap);
	}else{
		unitsList = (List)shedUnitsMap[parameters.shedId];
	}
	unitsList.each{ unitDetails ->
		unitRoutesList = ((Map)ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",unitDetails.facilityId))).get("routesDetailList");
		unitRoutesList.each{ routes ->
			routeCentersList = ProcurementNetworkServices.getRouteAgents(dctx,UtilMisc.toMap("routeId",routes.facilityId ));
			centerDetailsList = routeCentersList.get("agentDetailsList");
			centerDetailsList.each{ center ->
				Map tempCenterMap = FastMap.newInstance();
				tempCenterMap.put("UCODE", unitDetails.facilityCode);
				if(centerCodeSorting.equals("Y")){
					tempCenterMap.put("CCODE", (center.facilityCode).toInteger());
				}else{
					tempCenterMap.put("CCODE", center.facilityCode);
				}
				tempCenterMap.put("CENTERID", center.facilityId);
				tempCenterMap.put("MCCTYP","1");
				tempCenterMap.put("GBCODE","0");
				tempCenterMap.put("BCODE", "0");
				tempCenterMap.put("BANO", "0");
				
				if(Integer.parseInt(center.getAt("facilityCode")) >= 300){
					tempCenterMap.put("MCCTYP","2");
				}
				tempCenterMap.put("RNO", routes.facilityCode);
				tempCenterMap.put("CNAME", center.facilityName);
				tempCenterMap.put("PNAME", PartyHelper.getPartyName(delegator, center.ownerPartyId, true));
				
				Map inputRateAmt = UtilMisc.toMap("userLogin", userLogin);
				inputRateAmt.put("rateTypeId", "PROC_AGENT_MRGN");
				inputRateAmt.put("facilityId", center.facilityId);
				inputRateAmt.put("rateCurrencyUomId", "INR");
				rateAmount = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
				inputRateAmt.put("rateTypeId", "PROC_CARTAGE");
				cartageAmt = dispatcher.runSync("getProcurementFacilityRateAmount", inputRateAmt);
				
				normalMargin = (BigDecimal) rateAmount.get("rateAmount");
				cartage = (BigDecimal) cartageAmt.get("rateAmount");
				tempCenterMap.put("COMN", normalMargin);
				tempCenterMap.put("CART", cartage);				
				if(facilityFinaccountMap.get(center.get("facilityId"))){
					String tempBano = ((facilityFinaccountMap.get(center.get("facilityId"))).finAccountCode);
					if((UtilValidate.isNotEmpty(tempBano))&&(!"0".equalsIgnoreCase(tempBano))){
						tempCenterMap.put("GBCODE", (facilityFinaccountMap.get(center.get("facilityId"))).gbCode);
						tempCenterMap.put("BCODE", (facilityFinaccountMap.get(center.get("facilityId"))).bCode);
						tempCenterMap.put("BANO",  (facilityFinaccountMap.get(center.get("facilityId"))).finAccountCode);
					}
				}
				tempCenterMap.put("INCENTIVE", Boolean.FALSE);
				tempCenterMap.put("HABCODE", 0);
				centerMasterList.add(tempCenterMap);
			}
			
		}
	}
}
centerSortedMaster = UtilMisc.sortMaps(centerMasterList, UtilMisc.toList("CCODE"));
context.putAt("centerMasterList", centerSortedMaster);


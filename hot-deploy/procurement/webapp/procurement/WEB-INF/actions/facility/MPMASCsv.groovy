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
	unitsList = (List)shedUnitsMap[parameters.shedId];
	unitsList.each{ unitDetails ->
		unitRoutesList = ((Map)ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",unitDetails.facilityId))).get("routesDetailList");
		unitRoutesList.each{ routes ->
			routeCentersList = ProcurementNetworkServices.getRouteAgents(dctx,UtilMisc.toMap("routeId",routes.facilityId ));
			centerDetailsList = routeCentersList.get("agentDetailsList");
			centerDetailsList.each{ center ->
				Map tempCenterMap = FastMap.newInstance();
				tempCenterMap.put("UCODE", unitDetails.facilityCode);
				tempCenterMap.put("CCODE", center.facilityCode);
				tempCenterMap.put("MCCTYP","1");
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
					tempCenterMap.put("GBCODE", (facilityFinaccountMap.get(center.get("facilityId"))).gbCode);
					tempCenterMap.put("BCODE", (facilityFinaccountMap.get(center.get("facilityId"))).bCode);
					tempCenterMap.put("BANO",  (facilityFinaccountMap.get(center.get("facilityId"))).finAccountCode);
				}
				tempCenterMap.put("INCENTIVE", Boolean.TRUE);
				tempCenterMap.put("HABCODE", 0);
				centerMasterList.add(tempCenterMap);
				
			}
			
		}
		
	}	
}
/*Debug.log("centerMasterList=========="+centerMasterList);*/
context.putAt("centerMasterList", centerMasterList);

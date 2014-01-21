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

dctx = dispatcher.getDispatchContext();
shedUnits = ProcurementNetworkServices.getShedUnits(dctx ,context);
shedUnitsMap = (Map)shedUnits.get("shedUnits");
RouteMasterList =[];
if(UtilValidate.isNotEmpty(parameters.shedId)){
	unitsList = (List)shedUnitsMap[parameters.shedId];
	unitsList.each{ unitDetails ->
		unitRoutesList = ((Map)ProcurementNetworkServices.getUnitRoutes(dctx,UtilMisc.toMap("unitId",unitDetails.facilityId))).get("routesDetailList");
		unitRoutesList.each{ routes ->
			
			Map tempRouteMap = FastMap.newInstance();			
			tempRouteMap.put("UCODE", unitDetails.facilityCode);
			tempRouteMap.put("RNO", routes.facilityCode);
			tempRouteMap.put("RNAME", routes.facilityName);
			RouteMasterList.add(tempRouteMap);
		}
		
	}	
}
context.putAt("RouteMasterList", RouteMasterList);

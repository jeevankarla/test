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
context.put("dctx",dctx);
List finAccountList = FastList.newInstance();
finAccountList = (ProcurementNetworkServices.getShedFacilityFinAccount(dctx, UtilMisc.toMap("facilityId",parameters.shedId,"statusFlag","ALL"))).get("finAccountList");
List gbMasList = FastList.newInstance();
Set gbMasSet =  new HashSet();
bankMasList =[];
Map bankMasMap =FastMap.newInstance();
if(UtilValidate.isNotEmpty(finAccountList)){
	finAccountList.each{ finAccountMap ->
		Map gbMasMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(finAccountMap.shortName)&&UtilValidate.isNotEmpty(finAccountMap.finAccountName)&&UtilValidate.isNotEmpty(finAccountMap.gbCode)){
			String finAccountName = finAccountMap.finAccountName
			String gbCode = finAccountMap.gbCode;
			if(UtilValidate.isNotEmpty(gbCode)){
				gbCode = gbCode.trim();
			}
			if(UtilValidate.isNotEmpty(gbCode)){
				gbMasMap.put("gbCode",finAccountMap.gbCode);
				gbMasMap.put("finAccountName",finAccountMap.finAccountName);
				gbMasMap.put("SHNAME"," ");
				String shName= "";
				shName = finAccountMap.shortName;
				if(UtilValidate.isNotEmpty(shName)){
					shName = shName.trim();
				}
				gbMasMap.put("SHNAME",shName);
				gbMasSet.add(gbMasMap);
			}
		}		
	}
}
gbMasList = gbMasSet.toList();
gbMasList = UtilMisc.sortMaps(gbMasList,UtilMisc.toList("gbCode"));
context.putAt("gbMasList", gbMasList);
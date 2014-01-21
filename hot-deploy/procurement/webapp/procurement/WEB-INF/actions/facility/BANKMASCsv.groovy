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

finAccountList = (ProcurementNetworkServices.getShedFacilityFinAccount(dctx, UtilMisc.toMap("facilityId",parameters.shedId ))).get("finAccountList");

bankMasList =[];
Map bankMasMap =FastMap.newInstance();
if(UtilValidate.isNotEmpty(finAccountList)){
	finAccountList.each{ finAccount ->
		gbCode = finAccount.getAt("gbCode");
		bcode =  finAccount.getAt("bCode");		
		if(UtilValidate.isNotEmpty(gbCode) && UtilValidate.isNotEmpty(bcode)){
			if(UtilValidate.isNotEmpty(finAccount)){				
				bankMasMap.put(gbCode+"_"+bcode, finAccount);				
			}
		}		
		
	}
}
if(UtilValidate.isNotEmpty(bankMasMap)){
	Iterator finAccountIter = bankMasMap.entrySet().iterator();
	while (finAccountIter.hasNext()) {
		Map.Entry entry = finAccountIter.next();
		finAccountMap = entry.getValue();	
		bankMasList.add(finAccountMap);		
	}
}

context.putAt("bankMasList", bankMasList);
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator.*;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.text.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;

facilityId = parameters.facilityId;

if(UtilValidate.isNotEmpty(facilityId)){
	facilityInventoryMap = dispatcher.runSync("getInventory", [facilityId: facilityId, userLogin: userLogin]);
	
	facProdInvList = [];
	if (facilityInventoryMap) {
		
		inventoryTotalsMap = facilityInventoryMap.get("facilityInventoryMap");
		
		Iterator facIter = inventoryTotalsMap.entrySet().iterator();
		while (facIter.hasNext()) {
			Map.Entry facEntry = facIter.next();
			
			tempFacilityId = facEntry.getKey();
			if(tempFacilityId == "InventoryTotals"){
				continue;
			}
			prodMap = facEntry.getValue();
			
			if (prodMap) {
				Iterator prodIter = prodMap.entrySet().iterator();
				while (prodIter.hasNext()) {
					Map.Entry prodEntry = prodIter.next();
					if(prodEntry.getKey() != "facilityId"){
						prodId = prodEntry.getKey();
						InventoryMap = prodEntry.getValue();
						quantityOnHandTotal = InventoryMap.getAt("quantityOnHandTotal");
						availableToPromiseTotal = InventoryMap.getAt("availableToPromiseTotal");		
						invMap = [:];
						invMap.putAt("facilityId",tempFacilityId);
						invMap.putAt("productId",prodId);
						invMap.putAt("quantityOnHandTotal",quantityOnHandTotal);
						invMap.putAt("availableToPromiseTotal",availableToPromiseTotal);
						facProdInvList.add(invMap);
					}
					
				}
			}
		}
	}
	context.put("facProdInvList",facProdInvList);
}

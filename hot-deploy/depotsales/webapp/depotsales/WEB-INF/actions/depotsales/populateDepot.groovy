
import org.ofbiz.service.ServiceUtil
import org.ofbiz.entity.condition.*
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.party.party.PartyHelper;


ownerPartyId = parameters.ownerPartyId;

Debug.log("ownerPartyId================"+ownerPartyId);


JSONArray depotJSON = new JSONArray();
if(UtilValidate.isNotEmpty(ownerPartyId)){
   condList = [];
   condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
   condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN, ["CFC","DEPOT"]));
   depotsList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
   
   Debug.log("depotsList===="+depotsList);
   
   if(UtilValidate.isNotEmpty(depotsList)){
	   depotsList.each{ eachDepot ->
		   JSONObject newObj = new JSONObject();
		   newObj.put("value",eachDepot.facilityId);
		   newObj.put("label",eachDepot.facilityName);
		   depotJSON.add(newObj);
	   }
   }
}
Debug.log("depotJSON=============="+depotJSON);

request.setAttribute("depotJSON",depotJSON);

return "success";
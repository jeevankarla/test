import org.ofbiz.base.util.UtilDateTime;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import net.sf.json.JSONObject;

import javolution.util.FastList;

import org.ofbiz.base.util.*;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;

import javolution.util.FastMap;

import java.text.ParseException;

import org.ofbiz.service.ServiceUtil;

import in.vasista.vbiz.facility.util.FacilityUtil;



JSONArray supplierFacilityListJSON = new JSONArray();

partyId = parameters.partyId;


condList = [];
condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
faciList = delegator.findList("Facility", EntityCondition.makeCondition(condList,EntityOperator.AND), null,null,null, false);

/* Debug.log("faciList================"+faciList);

 facilityTypeList = EntityUtil.getFieldListFromEntityList(faciList, "facilityTypeId", true);

Debug.log("facilityTypeList================"+facilityTypeList);
*/

/* filterredFeciList = [];

   for (eachType in facilityTypeList) {
	
	   
	   condList.clear();
	   condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	   condList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, eachType));
		List<String> orderBy = UtilMisc.toList("-facilityId");
	   faciListBasedOnTypeList = delegator.findList("Facility", EntityCondition.makeCondition(condList,EntityOperator.AND), null,orderBy,null, false);
	   
	   if(faciListBasedOnTypeList[0])
	   filterredFeciList.add(faciListBasedOnTypeList[0]);
	   
}
*/
   

if(UtilValidate.isNotEmpty(faciList)){
	
	faciList.each{ echFaci ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",echFaci.facilityId);
			newObj.put("label",echFaci.facilityName);
			supplierFacilityListJSON.add(newObj);
	}
	
}

Debug.log("supplierFacilityListJSON================"+supplierFacilityListJSON);



request.setAttribute("supplierFacilityListJSON", supplierFacilityListJSON);
return "success";

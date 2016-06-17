






import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.party.party.PartyHelper;



supplierId = parameters.supplierId;








conditionList =[];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS , supplierId));
conditionList.add(EntityCondition.makeCondition("closedReason", EntityOperator.EQUALS , null));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
FacilityList = delegator.findList("Facility", condition, null, null, null, false);




JSONArray facilityAddressJSON = new  JSONArray();

if(FacilityList){

for (eachFacility in FacilityList) {
	
	JSONObject tempMap = new  JSONObject();
	
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS ,eachFacility.facilityId));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List<String> FeciorderBy = UtilMisc.toList("contactMechId");
	FacilityContactMechList = delegator.findList("FacilityContactMechPurpose", condition, UtilMisc.toSet("contactMechId","contactMechPurposeTypeId"), FeciorderBy, null, false);
	
	
	  contactMechIds = EntityUtil.getFieldListFromEntityList(FacilityContactMechList, "contactMechId", true);
	
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN ,contactMechIds));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("contactMechId");
		PostalAddressList = delegator.findList("PostalAddress", condition, null, orderBy, null, false);
	
			
		if(PostalAddressList)
		{
			tempMap.put("facilityId", eachFacility.facilityId);
			tempMap.put("NcontactMechId", PostalAddressList[0].contactMechId);
			tempMap.put("TcontactMechId", PostalAddressList[1].contactMechId);
			tempMap.put("facilityName", eachFacility.facilityName);
			tempMap.put("facilityTypeId", eachFacility.facilityTypeId);
			tempMap.put("Naddress1", PostalAddressList[0].address1);
			if(PostalAddressList[0].address2)
			tempMap.put("Naddress2", PostalAddressList[0].address2);
			else
			tempMap.put("Naddress2", "");
			tempMap.put("Ncity", PostalAddressList[0].city);
			tempMap.put("NcountryGeoId", PostalAddressList[0].countryGeoId);
			
			
		    geo=delegator.findOne("Geo",[geoId : PostalAddressList[0].stateProvinceGeoId], false);
			
			
			if(geo.geoName)
			tempMap.put("NstateProvinceGeoId",geo.geoName);
			else
			tempMap.put("NstateProvinceGeoId",PostalAddressList[0].stateProvinceGeoId);
			
			tempMap.put("NcontactMechPurposeTypeId", FacilityContactMechList[0].contactMechPurposeTypeId);
			tempMap.put("NpostalCode", PostalAddressList[0].postalCode);
			tempMap.put("Taddress1", PostalAddressList[1].address1);
			tempMap.put("Taddress2", PostalAddressList[1].address2);
			if(PostalAddressList[1].address2)
			tempMap.put("Taddress2", PostalAddressList[1].address2);
			else
			tempMap.put("Taddress2", "");
			tempMap.put("Tcity", PostalAddressList[1].city);
			tempMap.put("TcountryGeoId", PostalAddressList[1].countryGeoId);
			
			geo1=delegator.findOne("Geo",[geoId : PostalAddressList[1].stateProvinceGeoId], false);
			
			if(geo1.geoName)
			tempMap.put("TstateProvinceGeoId",geo1.geoName);
			else
			tempMap.put("TstateProvinceGeoId",PostalAddressList[1].stateProvinceGeoId);
		
			tempMap.put("TcontactMechPurposeTypeId", FacilityContactMechList[1].contactMechPurposeTypeId);
			tempMap.put("TpostalCode", PostalAddressList[1].postalCode);
			
		}
		
		facilityAddressJSON.add(tempMap);
		
}

}





/*
FacilityPostalAddress = [:];
FacilityPostalAddress.put("userLogin",userLogin);
FacilityPostalAddress.put("facilityId",facilityId);
FacilityPostalAddress.put("address1", address1);
FacilityPostalAddress.put("address2", address2);
FacilityPostalAddress.put("city", city);
FacilityPostalAddress.put("countryGeoId", country);
FacilityPostalAddress.put("stateProvinceGeoId", state);
FacilityPostalAddress.put("contactMechPurposeTypeId", "NORMAL_ADDRESS");
FacilityPostalAddress.put("postalCode", postalCode);

resultcreateFacilityPostalAddress = dispatcher.runSync("createFacilityPostalAddress", FacilityPostalAddress);


*/





request.setAttribute("facilityAddressJSON", facilityAddressJSON);

return "sucess";
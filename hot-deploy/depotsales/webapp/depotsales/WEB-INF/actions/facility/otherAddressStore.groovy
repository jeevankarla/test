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



JSONArray orderList =new JSONArray();

supplierId = parameters.supplierId;
address1 = parameters.address1;
address2 = parameters.address2;
city = parameters.city;
country = parameters.country;
state = parameters.state;
FcontactNumber = parameters.FcontactNumber;
facicontactMechType = parameters.facicontactMechType;
facilityName = parameters.facilityName;
postalCode = parameters.postalCode;
tinNumber = parameters.tinNumber;
TFcountry = parameters.TFcountry;
TFstate = parameters.TFstate;
TFaddress1 = parameters.TFaddress1;
TFaddress2 = parameters.TFaddress2;
TFcity = parameters.TFcity;
TFpostalCode = parameters.TFpostalCode;
TFtinNumber = parameters.TFtinNumber;
NcontactMechId = parameters.NcontactMechId;
TcontactMechId = parameters.TcontactMechId;

facilityId = "";

createdStatus = "";



if(UtilValidate.isEmpty(NcontactMechId) && UtilValidate.isEmpty(TcontactMechId)){

inputCtx = [:];
inputCtx.put("userLogin",userLogin);
inputCtx.put("facilityName", facilityName);
inputCtx.put("facilityTypeId", facicontactMechType);
inputCtx.put("ownerPartyId", supplierId);
inputCtx.put("openedDate", UtilDateTime.nowTimestamp());
try{
 resultCtx = dispatcher.runSync("createFacility", inputCtx);
 
 facilityId = resultCtx.facilityId;
 
 
}catch(Exception e){}

}
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

createdStatus="C"

if(UtilValidate.isEmpty(NcontactMechId) && UtilValidate.isEmpty(TcontactMechId)){
resultcreateFacilityPostalAddress = dispatcher.runSync("createFacilityPostalAddress", FacilityPostalAddress);

attributeMap = [:];
attributeMap.put("facilityId",facilityId);
attributeMap.put("attrName","TIN_NUMBER");
attributeMap.put("attrValue",tinNumber);

facilityAttribute = dispatcher.runSync("createOrUpdateFcilityAttribute", attributeMap);


}
else{
 FacilityPostalAddress.put("contactMechId", NcontactMechId);
 FacilityPostalAddress.remove("contactMechPurposeTypeId");
 
 resultEditedFacilityPostalAddress = dispatcher.runSync("updateFacilityPostalAddressDetails", FacilityPostalAddress);
 
 
 //Debug.log("parameters.facilityId==========="+parameters.facilityId);
 
 if(parameters.facilityId){
	 facilityUpdate = [:];
	 facilityUpdate.put("userLogin",userLogin);
	 facilityUpdate.put("facilityId",parameters.facilityId);
	 facilityUpdate.put("facilityTypeId",facicontactMechType);
	 facilityUpdate.put("facilityName",facilityName);
	 
	 resultfacilityUpdate = dispatcher.runSync("updateFacility", facilityUpdate);
	 
	 attributeMap = [:];
	 attributeMap.put("facilityId",parameters.facilityId);
	 attributeMap.put("attrName","TIN_NUMBER");
	 attributeMap.put("attrValue",tinNumber);
	 
	 facilityAttribute = dispatcher.runSync("createOrUpdateFcilityAttribute", attributeMap);
 
 
 }
 
 createdStatus="E"
 
 
}


FacilityTaxAddress = [:];
FacilityTaxAddress.put("userLogin",userLogin);
FacilityTaxAddress.put("facilityId",facilityId);
if(TFaddress1)
FacilityTaxAddress.put("address1", TFaddress1);
else
FacilityTaxAddress.put("address1", address1);
if(TFaddress2)
FacilityTaxAddress.put("address2", TFaddress2);
else
FacilityTaxAddress.put("address2", address2);
if(TFcity)
FacilityTaxAddress.put("city", TFcity);
else
FacilityTaxAddress.put("city", city);
if(TFcountry)
FacilityTaxAddress.put("countryGeoId", TFcountry);
else
FacilityTaxAddress.put("countryGeoId", country);
if(TFstate && TFstate !="IN-AN")
FacilityTaxAddress.put("stateProvinceGeoId", TFstate);
else
FacilityTaxAddress.put("stateProvinceGeoId", state);
if(TFpostalCode)
FacilityTaxAddress.put("postalCode", TFpostalCode);
else
FacilityTaxAddress.put("postalCode", postalCode);
FacilityTaxAddress.put("contactMechPurposeTypeId", "TAX_ADDRESS");


if(UtilValidate.isEmpty(NcontactMechId) && UtilValidate.isEmpty(TcontactMechId)){
  resultcreateFacilityTaxAddress = dispatcher.runSync("createFacilityPostalAddress", FacilityTaxAddress);
  
}
else{
	FacilityTaxAddress.put("contactMechId", TcontactMechId);
	FacilityTaxAddress.remove("contactMechPurposeTypeId");
	resultEditedFacilityTaxAddress = dispatcher.runSync("updateFacilityPostalAddressDetails", FacilityTaxAddress);
	
   }






JSONObject tempMap = new JSONObject();

tempMap.put("facilityId", "Sucess");
tempMap.put("createdStatus", createdStatus);


orderList.add(tempMap);


request.setAttribute("orderList", orderList);
return "success";

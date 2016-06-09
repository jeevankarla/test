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



JSONArray  deletedFacility =new JSONArray();


facilityId = parameters.facilityId;

NcontactMechId = parameters.NcontactMechId;

TcontactMechId = parameters.TcontactMechId;

Debug.log("NcontactMechId==============="+NcontactMechId);

Debug.log("TcontactMechId==============="+TcontactMechId);

/*
deleteFacilityContactMechPurposeMap = [:];
deleteFacilityContactMechPurposeMap.put("userLogin",userLogin);
deleteFacilityContactMechPurposeMap.put("contactMechId", contactMechId);

try{
 resultpurPoseCtx = dispatcher.runSync("deleteFacilityContactMechPurpose", deleteFacilityContactMechPurposeMap);
 
 Debug.log("resultpurPoseCtx==================="+resultpurPoseCtx);
 
}catch(Exception e){}


 Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

deleteFacilityContactMech = [:];
deleteFacilityContactMech.put("userLogin",userLogin);
deleteFacilityContactMech.put("facilityId", facilityId);
deleteFacilityContactMech.put("contactMechId", contactMechId);
deleteFacilityContactMech.put("fromDate", nowTimestamp);

try{
 deleteFacilityContact = dispatcher.runSync("deleteFacilityContactMech", deleteFacilityContactMech);
 
 Debug.log("deleteFacilityContact==================="+deleteFacilityContact);
 
}catch(Exception e){}
*/
/*inputCtx = [:];
inputCtx.put("userLogin",userLogin);
inputCtx.put("facilityId", facilityId);

try{
 resultCtx = dispatcher.runSync("deleteFacility", inputCtx);
 
 
 Debug.log("resultCtx==================="+resultCtx);
 
}catch(Exception e){}
*/



inputNCtx = [:];
inputNCtx.put("userLogin",userLogin);
inputNCtx.put("contactMechId", NcontactMechId);
inputNCtx.put("facilityId", facilityId);
//inputNCtx.put("contactMechTypeId", "POSTAL_ADDRESS");
//inputNCtx.put("extension", "REMOVED");


try{
 resultNCtx = dispatcher.runSync("updateFacilityContactMechDetail", inputNCtx);
 
 Debug.log("resultNCtx==================="+resultNCtx);
 
 
}catch(Exception e){}

/*
inputTCtx = [:];
inputTCtx.put("userLogin",userLogin);
inputTCtx.put("contactMechId", TcontactMechId);
inputTCtx.put("facilityId", facilityId);
//inputTCtx.put("contactMechTypeId", "POSTAL_ADDRESS");
//inputTCtx.put("extension", "REMOVED");


try{
 resultTCtx = dispatcher.runSync("updateFacilityContactMechDetail", inputTCtx);
 
 Debug.log("resultTCtx==================="+resultTCtx);
 
 
}catch(Exception e){}
*/
JSONArray orderList = new JSONArray();


request.setAttribute("orderList", orderList);
return "success";

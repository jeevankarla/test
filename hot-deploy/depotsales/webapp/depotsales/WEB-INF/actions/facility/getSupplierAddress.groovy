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


JSONArray suplierAddresList =new JSONArray();

supplierId = parameters.supplierId;

JSONObject tempMap = new JSONObject();
if(UtilValidate.isNotEmpty(supplierId)){

try{
	 partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:supplierId, userLogin: userLogin]);
 if(UtilValidate.isNotEmpty(partyPostalAddress)){
	 if(UtilValidate.isNotEmpty(partyPostalAddress.address1)){
	  address1=partyPostalAddress.address1;
	  tempMap.put("address1",address1);
	 }else{
	 
	 tempMap.put("address1","");
	 
	 }
	if(UtilValidate.isNotEmpty(partyPostalAddress.address2)){
		address2=partyPostalAddress.address2;
		tempMap.put("address2",address2);
	}else{
	
	    tempMap.put("address2","");
	   
	}
	if(UtilValidate.isNotEmpty(partyPostalAddress.city)){
		city=partyPostalAddress.city;
		tempMap.put("city",city);
	}else{
	
	tempMap.put("city","");
	
	}
 }
 
}catch(Exception e){}

}


suplierAddresList.add(tempMap);


request.setAttribute("suplierAddresList", suplierAddresList);
return "success";

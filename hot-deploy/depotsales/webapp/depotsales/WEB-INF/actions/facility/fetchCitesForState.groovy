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

state = parameters.state;


conditionList = [];
conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, state));
conditionList.add(EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, "DISTRICT"));
List GeoAssoc = delegator.findList("GeoAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

/*
JSONObject tempMap = new JSONObject();

tempMap.put("facilityId", "Sucess");
tempMap.put("createdStatus", createdStatus);


orderList.add(tempMap);

*/

for (eachAsso in GeoAssoc) {
	
	Geo = delegator.findOne("Geo",[geoId : eachAsso.geoIdTo] , false);
	
	JSONObject tempMap = new JSONObject();
	
	if(Geo){
		tempMap.put("geoId", eachAsso.geoIdTo);
		if(Geo.geoName)
		tempMap.put("geoName", Geo.geoName);
		else
		tempMap.put("geoName", "");
		
		orderList.add(tempMap);
	}
	
	
}

request.setAttribute("orderList", orderList);
return "success";









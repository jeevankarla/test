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
tempRoute = parameters.routeChange;

condList = [];
condList.add(EntityCondition.makeCondition("primaryParentGroupId", EntityOperator.EQUALS, "SUPPLYTIME_RT_GROUP"));
condList.add(EntityCondition.makeCondition("facilityGroupTypeId", EntityOperator.EQUALS, "DAIRY_LMD_TYPE"));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
supplyTypes = delegator.findList("FacilityGroup", cond, null, null, null, true);
supplyTypesList = EntityUtil.getFieldListFromEntityList(supplyTypes, "facilityGroupId", true);
facilityGroupMember = delegator.findList("FacilityGroupMember", EntityCondition.makeCondition("facilityGroupId", EntityOperator.IN, supplyTypesList), null, null, null, false);
facilityGroupMember = EntityUtil.filterByDate(facilityGroupMember, UtilDateTime.nowTimestamp());

JSONObject supplyRouteItemsJSON = new JSONObject();
JSONArray routesJSON = new JSONArray();
Set routesSet = new HashSet();
conditionList = [];
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.IN ,UtilMisc.toList("ROUTE","BOOTH")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
routesFacilitiesList = delegator.findList("Facility",condition,null,null,null,false);
routesFacilitiesList = EntityUtil.filterByDate(routesFacilitiesList, nowTimestamp, "openedDate", "closedDate", true);
routesList = EntityUtil.filterByCondition(routesFacilitiesList, EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "ROUTE"));
boothsList = ByProductNetworkServices.getAllBooths(delegator);
if(tempRoute){
	JSONArray boothsJSON = new JSONArray();
	routesList.each{route ->
		JSONObject newRouteObj = new JSONObject();
		newRouteObj.put("value",route.facilityId);
		newRouteObj.put("label",route.facilityName);
		routesJSON.add(newRouteObj);
	}
	JSONArray boothItemsJSON = new JSONArray();
	boothsList.each{booth ->
		JSONObject newBoothObj = new JSONObject();
		newBoothObj.put("value",booth.facilityId);
		newBoothObj.put("label",booth.facilityName);
		boothsJSON.add(newBoothObj);
	}
	context.boothsJSON = boothsJSON;
	/*Debug.log("boothsJson #######################################"+boothsJSON);*/
	
}else{
	AM_Routes = EntityUtil.filterByCondition(facilityGroupMember, EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, "AM_RT_GROUP"));
	Am_RoutesIds = EntityUtil.getFieldListFromEntityList(AM_Routes, "facilityId", true);
	Am_RoutesList = EntityUtil.filterByCondition(routesList, EntityCondition.makeCondition("facilityId", EntityOperator.IN, Am_RoutesIds));
	JSONArray AMRoutesJSON = new JSONArray();
	Am_RoutesList.each{route ->
		JSONObject newRouteObj = new JSONObject();
		newRouteObj.put("value",route.facilityId);
		newRouteObj.put("label",route.facilityName);
		AMRoutesJSON.add(newRouteObj);
		if(!routesSet.contains(route.facilityId)){
			routesJSON.add(newRouteObj);
			routesSet.add(route.facilityId);
		}
	}
	supplyRouteItemsJSON.putAt("AM", AMRoutesJSON);
	
	PM_Routes = EntityUtil.filterByCondition(facilityGroupMember, EntityCondition.makeCondition("facilityGroupId", EntityOperator.EQUALS, "PM_RT_GROUP"));
	Pm_RoutesIds = EntityUtil.getFieldListFromEntityList(PM_Routes, "facilityId", true);
	Pm_RoutesList = EntityUtil.filterByCondition(routesList, EntityCondition.makeCondition("facilityId", EntityOperator.IN, Pm_RoutesIds));
	JSONArray PMRoutesJSON = new JSONArray();
	Pm_RoutesList.each{route ->
		JSONObject newRouteObj = new JSONObject();
		newRouteObj.put("value",route.facilityId);
		newRouteObj.put("label",route.facilityName);
		PMRoutesJSON.add(newRouteObj);
		if(!routesSet.contains(route.facilityId)){
			routesJSON.add(newRouteObj);
			routesSet.add(route.facilityId);
		}
	}
	supplyRouteItemsJSON.putAt("PM", PMRoutesJSON);
	context.supplyRouteItemsJSON = supplyRouteItemsJSON;
	JSONObject facilityItemsJSON = new JSONObject();
	JSONArray boothsJSON = new JSONArray();
	Set boothsSet = new HashSet();
	routesList.each{route ->
		boothsList = ByProductNetworkServices.getRouteBooths(delegator , UtilMisc.toMap("routeId",route.facilityId)).get("boothsList");
		boothsList = EntityUtil.filterByDate(boothsList, nowTimestamp, "openedDate", "closedDate", true);
		JSONArray boothItemsJSON = new JSONArray();
		routeboothMap = [:];
		boothsList.each{booth ->
			JSONObject newBoothObj = new JSONObject();
			newBoothObj.put("value",booth.facilityId);
			//newBoothObj.put("label",booth.facilityName+" ["+route.facilityName+"]");
			newBoothObj.put("label",booth.facilityName+" ["+booth.facilityId+"]");
			boothItemsJSON.add(newBoothObj);
			if(!boothsSet.contains(booth.facilityId)){
				boothsJSON.add(newBoothObj);
				boothsSet.add(booth.facilityId);
			}
		}
		routeId = route.facilityId;
		facilityItemsJSON.put(routeId, boothItemsJSON);
	}
	context.facilityItemsJSON = facilityItemsJSON;
	context.boothsJSON = boothsJSON;
	/*Debug.log("boothsJson #######################################"+boothsJSON);*/
}

context.routesJSON = routesJSON;

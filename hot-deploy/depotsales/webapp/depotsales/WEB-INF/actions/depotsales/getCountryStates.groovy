
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;

import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.ofbiz.service.ServiceUtil;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.GenericValue;


dctx = dispatcher.getDispatchContext();

List<GenericValue> statesList = CommonWorkers.getAssociatedStateList(delegator, parameters.countryGeoId);
JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoCode);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;
if (stateListJSON.size() > 0) {
	request.setAttribute("stateListJSON",stateListJSON);
}
return "sucess";
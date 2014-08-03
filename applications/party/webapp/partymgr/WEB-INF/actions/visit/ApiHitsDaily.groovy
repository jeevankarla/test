
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
//SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

def sdf = new SimpleDateFormat("dd/MM/yyyy");
Timestamp hitsDate = null;
try {
	if (parameters.hitsDate) {
		context.hitsDate = parameters.hitsDate;
		hitsDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.hitsDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
}
if (hitsDate == null) {
	hitsDate = UtilDateTime.nowTimestamp();
}

String hitsDateStr = UtilDateTime.toDateString(hitsDate, "dd/MM/yyyy");			

Timestamp timePeriodStart = UtilDateTime.getDayStart(hitsDate);
Timestamp timePeriodEnd = UtilDateTime.getDayEnd(hitsDate);
JSONArray hitsListJSON = new JSONArray();
conditionList=[];
conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.GREATER_THAN_EQUAL_TO , timePeriodStart));
conditionList.add(EntityCondition.makeCondition("startDateTime", EntityOperator.LESS_THAN_EQUAL_TO , timePeriodEnd));
conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.NOT_EQUAL, 'hrmsapi'));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

hitList = delegator.findList("ApiHit", condition, null, null, null, false);

for (GenericValue hit : hitList) {
	//String startDateTime = hit.get("startDateTime").toString(); 
	String hitDate = UtilDateTime.toDateString(hit.get("startDateTime"), "dd/MM/yyyy");
    String hitTime = UtilDateTime.toDateString(hit.get("startDateTime"), "hh:mm:ss");

	String userLoginId= hit.getString("userLoginId");
	String contentId= hit.getString("contentId");
	String totalTimeMillis = hit.getLong("totalTimeMillis");
	
	JSONArray hitJSON = new JSONArray();	
	hitJSON.add(hitDate);
	hitJSON.add(hitTime);
	hitJSON.add(userLoginId);
	hitJSON.add(contentId);

	hitsListJSON.add(hitJSON);
}

Debug.logError("hitsListJSON="+hitsListJSON,"");
context.hitsDate = hitsDate
context.hitsListJSON = hitsListJSON;

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
Timestamp punchDate = null;
try {
	if (parameters.punchDate) {
		context.punchDate = parameters.punchDate;
		punchDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.punchDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
}
if (punchDate == null) {
	punchDate = UtilDateTime.nowTimestamp();
}

String punchDateStr = UtilDateTime.toDateString(punchDate, "dd/MM/yyyy");			

Timestamp timePeriodStart = UtilDateTime.getDayStart(punchDate);
Timestamp timePeriodEnd = UtilDateTime.getDayEnd(punchDate);
JSONArray punchListJSON = new JSONArray();
conditionList=[];
conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodStart)));
conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodEnd)));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);

punchList = delegator.findList("EmplPunch", condition, null, null, null, false);

for (GenericValue punch : punchList) {
	String partyId= punch.getString("partyId");
	String partyName = PartyHelper.getPartyName(delegator, partyId, false);
	String punchTime = punch.get("punchtime").toString(); //timeFormat.parse((punch.get("punchtime")).toString());
	String inOut = "";
	if (punch.getString("InOut")) {
		inOut = punch.getString("InOut");
	}
	JSONArray punchJSON = new JSONArray();	
	punchJSON.add(punchDateStr);
	punchJSON.add(partyId);
	punchJSON.add(partyName);
	punchJSON.add(punchTime);
	punchJSON.add(inOut);
	punchListJSON.add(punchJSON);
}

Debug.logError("punchListJSON="+punchListJSON,"");
context.punchDate = punchDate
context.punchListJSON = punchListJSON;
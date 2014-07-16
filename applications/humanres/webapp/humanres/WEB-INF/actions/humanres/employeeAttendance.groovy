
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

dctx = dispatcher.getDispatchContext();
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
def dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

def sdf = new SimpleDateFormat("dd/MM/yyyy");
Timestamp fromDate = null;
Timestamp thruDate = null;
employeeId = parameters.employeeId;

try {
	if (parameters.fromDate) {
		context.fromDate = parameters.fromDate;
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
	if (parameters.thruDate) {
		context.thruDate = parameters.thruDate;
		thruDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
}
if (thruDate == null) {
	thruDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
}
if (fromDate == null) {
	fromDate = UtilDateTime.getDayStart(UtilDateTime.addDaysToTimestamp(thruDate, -45));
}

//String punchDateStr = UtilDateTime.toDateString(punchDate, "dd/MM/yyyy");			
String employeeName = PartyHelper.getPartyName(delegator, employeeId, false);

Timestamp timePeriodStart = UtilDateTime.getDayStart(fromDate);
Timestamp timePeriodEnd = UtilDateTime.getDayEnd(thruDate);
JSONArray punchListJSON = new JSONArray();
conditionList=[];
conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodStart)));
conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(timePeriodEnd)));
conditionList.add(EntityCondition.makeCondition("partyId", employeeId));

condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderBy = UtilMisc.toList("-punchdate","-punchtime");
punchList = delegator.findList("EmplPunch", condition, null, orderBy, null, false);

def outTimestamp = null;
punchList.each { punch->
	String partyId= punch.getString("partyId");
	String partyName = PartyHelper.getPartyName(delegator, partyId, false);
	String punchTime = timeFormat.format(punch.get("punchtime"));
	String inOut = "";
	if (punch.getString("InOut")) {
		inOut = punch.getString("InOut");
	}
	if (inOut.equals("OUT")) {
		outTimestamp = UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
		return;
	}
	if (inOut.equals("IN")) {
		inTimestamp = UtilDateTime.toDateString(punch.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
		JSONArray punchJSON = new JSONArray();	
		punchJSON.add(partyId);
		punchJSON.add(partyName);
		punchJSON.add(inTimestamp);
		if (outTimestamp == null) {
			punchJSON.add("");
			punchJSON.add("");
		}
		else {
			punchJSON.add(outTimestamp);
			elapsedHours = UtilDateTime.getInterval(new java.sql.Timestamp(dateTimeFormat.parse(inTimestamp).getTime()), 
								new java.sql.Timestamp(dateTimeFormat.parse(outTimestamp).getTime()))/(1000*60*60);
			punchJSON.add(String.format( "%.2f", elapsedHours ));				
		}
		punchListJSON.add(punchJSON);
		outTimestamp = null;
	}
}


Debug.logError("punchListJSON="+punchListJSON,"");
context.employeeName = employeeName;
context.fromDate = fromDate;
context.thruDate = thruDate;
context.punchListJSON = punchListJSON;
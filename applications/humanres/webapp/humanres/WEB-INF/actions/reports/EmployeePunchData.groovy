import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.EPfromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.EPfromDate).getTime()));
	}
	if (parameters.EPthruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.EPthruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
}

context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

totalDays=UtilDateTime.getIntervalInDays(fromDate,thruDate);
if(totalDays > 31){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
List currentDateKeysList = [];
totalDays=totalDays+1;
for(int i=0; i <totalDays; i++){
	currentDayTimeStart = UtilDateTime.getDayStart(fromDate, i);
	currentDayTimeEnd = UtilDateTime.getDayEnd(currentDayTimeStart);
	currentDateKeysList.add(currentDayTimeStart);
}
context.putAt("currentDateKeysList", currentDateKeysList);

EmplPunchinMap =[:];
EmplPunchoutMap=[:];
TwoPunchEmployeesList = delegator.findList("EmployeeDetail",EntityCondition.makeCondition("punchType", EntityOperator.EQUALS, "AA") , null, null, null, false);
if(UtilValidate.isNotEmpty(TwoPunchEmployeesList)){
	TwoPunchEmployeesList.each{ employee ->
		currentDateKeysList.each{ date ->
			List inConditionList=[];
			inConditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
			inConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.get("partyId")));
			inCondition=EntityCondition.makeCondition(inConditionList,EntityOperator.AND);
			punchindeatils = delegator.findList("EmplPunch", inCondition , null, null, null, false );
			emplpunchdeatils = UtilMisc.sortMaps(punchindeatils, UtilMisc.toList("punchdate"));
			if(UtilValidate.isNotEmpty(emplpunchdeatils)){
				emplpunchdeatils.each{ emplpunch ->
					partyId=emplpunch.get("partyId");
					departmentDetails=delegator.findByAnd("Employment", [partyIdTo : partyId]);
					if((emplpunch.get("InOut")).equals("IN")){
						punchMap=[:];
						punchMap.put("date",date);
						punchMap.put("partyId",emplpunch.get("partyId"));
						punchMap.put("inTime",emplpunch.get("punchtime"));
						String partyName = PartyHelper.getPartyName(delegator, emplpunch.get("partyId"), false);
						punchMap.put("partyName",partyName);
						if(UtilValidate.isNotEmpty(departmentDetails)){
							departmentId=departmentDetails[0].get("partyIdFrom");
							punchMap.put("departmentId",departmentId);
							EmplPunchinMap.put(partyId,punchMap);
						}
					}
					if((emplpunch.get("InOut")).equals("OUT")){
						outPunchMap=[:];
						outPunchMap.put("date",date);
						outPunchMap.put("partyId",emplpunch.get("partyId"));
						outPunchMap.put("outTime",emplpunch.get("punchtime"));
						String partyName = PartyHelper.getPartyName(delegator, emplpunch.get("partyId"), false);
						outPunchMap.put("partyName",partyName);
						if(UtilValidate.isNotEmpty(departmentDetails)){
							departmentId=departmentDetails[0].get("partyIdFrom");
							outPunchMap.put("departmentId",departmentId);
							EmplPunchoutMap.put(partyId,outPunchMap);
						}
					}
				}
			}
		}
	}
}
context.put("EmplPunchinMap",EmplPunchinMap);
context.put("EmplPunchoutMap",EmplPunchoutMap);


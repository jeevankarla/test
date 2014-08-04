import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.EmplLeaveService;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresApiService;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();


def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.MPfromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.MPfromDate).getTime()));
	}
	if (parameters.MPthruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.MPthruDate).getTime()));
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
date = UtilDateTime.toDateString(currentDayTimeStart);
currentDateKeysList.add(date);
}
context.putAt("currentDateKeysList", currentDateKeysList);

misPunchDataMap=[:];

TwoPunchEmployeesList = delegator.findList("EmployeeDetail",EntityCondition.makeCondition("punchType", EntityOperator.EQUALS, "AA") , null, null, null, false);
if(UtilValidate.isNotEmpty(TwoPunchEmployeesList)){
	TwoPunchEmployeesList.each{ employee ->
		currentDateKeysList.each{ date ->
			List conditionList=[];
			conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.get("partyId")));
			conditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "IN"));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			punchindeatils = delegator.findList("EmplPunch", condition , null, null, null, false );
			emplpunchindeatils = UtilMisc.sortMaps(punchindeatils, UtilMisc.toList("punchdate"));
			if(UtilValidate.isNotEmpty(emplpunchindeatils)){
				emplpunchindeatils.each{ emplpunchin ->
					List outconditionList=[];
					outconditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
					outconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.get("partyId")));
					outconditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
					outcondition=EntityCondition.makeCondition(outconditionList,EntityOperator.AND);
					punchoutdeatils = delegator.findList("EmplPunch", outcondition , null, null, null, false );
					
					List shiftconditionList=[];
					shiftconditionList.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
					shiftconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.get("partyId")));
					shiftcondition=EntityCondition.makeCondition(shiftconditionList,EntityOperator.AND);
					punchshiftdeatils = delegator.findList("EmplDailyAttendanceDetail", shiftcondition , null, null, null, false );
					
					if(UtilValidate.isNotEmpty(punchshiftdeatils)){
						punchshiftdeatils.each{ shift ->
							if((shift.get("shiftType")).equals("SHIFT_03")){
								SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
								dateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(dateFormat.parse(date).getTime()));
								nextDay= UtilDateTime.getNextDayStart(dateStart);
								List shiftoutconditionList=[];
								shiftoutconditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(nextDay)));
								shiftoutconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.get("partyId")));
								shiftoutconditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
								shiftoutcondition=EntityCondition.makeCondition(shiftoutconditionList,EntityOperator.AND);
								shiftpunchoutdeatils = delegator.findList("EmplPunch", shiftoutcondition , null, null, null, false );
								if(UtilValidate.isEmpty(shiftpunchoutdeatils)){
									shiftmispunchMap=[:];
									shiftmispunchMap.put("date",date);
									shiftmispunchMap.put("partyId",employee.get("partyId"));
									shiftmispunchMap.put("Time",emplpunchin.get("punchtime"));
									String partyName = PartyHelper.getPartyName(delegator, employee.get("partyId"), false);
									shiftmispunchMap.put("partyName",partyName);
									misPunchDataMap.put(employee.get("partyId"),shiftmispunchMap);
								}
							}
						}
					}
					if(UtilValidate.isEmpty(punchoutdeatils)){
						currDateMisPunchMap=[:];
						currDateMisPunchMap.put("date",date);
						currDateMisPunchMap.put("partyId",employee.get("partyId"));
						currDateMisPunchMap.put("Time",emplpunchin.get("punchtime"));
						String partyName = PartyHelper.getPartyName(delegator, employee.get("partyId"), false);
						currDateMisPunchMap.put("partyName",partyName);
						misPunchDataMap.put(employee.get("partyId"),currDateMisPunchMap);
					}
				}
			}
		}
	}
}

context.put("misPunchDataMap",misPunchDataMap);





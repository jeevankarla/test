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
	leaveConditionList=[];
	leaveConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,EntityUtil.getFieldListFromEntityList(TwoPunchEmployeesList, "partyId", false)));
	leaveConditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
		EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)]));
	leavecondition=EntityCondition.makeCondition(leaveConditionList,EntityOperator.AND);
	
	empLeavedetailsList = delegator.findList("EmplLeave", leavecondition , null, null, null, false );
	
	List conditionList=[];
	conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.BETWEEN, UtilMisc.toList(UtilDateTime.toSqlDate(fromDate),UtilDateTime.toSqlDate(thruDate))));
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(TwoPunchEmployeesList, "partyId", false)));
	//conditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "IN"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	punchdeatils = delegator.findList("EmplPunch", condition , null, null, null, false );
	
	TwoPunchEmployeesList.each{ employee ->
		partyIdmisPunchDataMap=[:];
		DateKeysList=[];
		currentDateKeysList.each{ date ->
			dateStr = "";
			def sdf1 = new SimpleDateFormat("MM/dd/yyyy");
			try {
				if (date) {
					dateTimestamp = new java.sql.Timestamp(sdf1.parse(date).getTime());
					dateStr = UtilDateTime.toDateString(dateTimestamp,"dd/MM/yyyy");
				}
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + e, "");
				context.errorMessage = "Cannot parse date string: " + e;
				return;
			}
			DateKeysList.add(dateStr);
			SimpleDateFormat Format = new SimpleDateFormat("MM/dd/yyyy");
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(Format.parse(date).getTime()));
			thruDate=UtilDateTime.getDayEnd(new java.sql.Timestamp(Format.parse(date).getTime()));
			empLeavedetails = EntityUtil.filterByCondition(empLeavedetailsList, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,employee.get("partyId")));
			if(UtilValidate.isEmpty(empLeavedetails)){
				punchindeatils = EntityUtil.filterByAnd(punchdeatils,
					 UtilMisc.toMap("InOut","IN" ,"punchdate", UtilDateTime.toSqlDate(date) , "partyId" ,employee.get("partyId")));
				emplpunchindeatils = UtilMisc.sortMaps(punchindeatils, UtilMisc.toList("punchdate"));
				if(UtilValidate.isNotEmpty(emplpunchindeatils)){
					emplpunchindeatils.each{ emplpunchin ->
						shiftTypeId = emplpunchin.get("shiftType");
						if((emplpunchin.get("shiftType")).equals("SHIFT_NIGHT")){
							SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
							dateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(dateFormat.parse(date).getTime()));
							nextDay= UtilDateTime.getNextDayStart(dateStart);
							List shiftoutconditionList=[];
							shiftoutconditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(nextDay)));
							shiftoutconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.get("partyId")));
							shiftoutconditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
							shiftoutconditionList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS, "SHIFT_NIGHT"));
							shiftoutcondition=EntityCondition.makeCondition(shiftoutconditionList,EntityOperator.AND);
							//shiftpunchoutdeatils = delegator.findList("EmplPunch", shiftoutcondition , null, null, null, false );
							shiftpunchoutdeatils = EntityUtil.filterByCondition(empLeavedetailsList, shiftoutcondition);
							
							if(UtilValidate.isEmpty(shiftpunchoutdeatils)){
								shiftmispunchMap=[:];
								shiftmispunchMap.put("date",dateStr);
								shiftmispunchMap.put("partyId",employee.get("partyId"));
								shiftmispunchMap.put("Time",emplpunchin.get("punchtime"));
								String partyName = PartyHelper.getPartyName(delegator, employee.get("partyId"), false);
								shiftmispunchMap.put("partyName",partyName);
								partyIdmisPunchDataMap.put(emplpunchin.get("punchtime"),shiftmispunchMap);
							}
						}
						else{
							List outconditionList=[];
							outconditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
							outconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employee.get("partyId")));
							outconditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
							outcondition=EntityCondition.makeCondition(outconditionList,EntityOperator.AND);
							/*punchoutdeatils = delegator.findList("EmplPunch", outcondition , null, null, null, false );*/
							punchoutdeatils = EntityUtil.filterByCondition(punchdeatils,outcondition);
							if(UtilValidate.isEmpty(punchoutdeatils)){
								currDateMisPunchMap=[:];
								currDateMisPunchMap.put("date",dateStr);
								currDateMisPunchMap.put("partyId",employee.get("partyId"));
								currDateMisPunchMap.put("Time",emplpunchin.get("punchtime"));
								String partyName = PartyHelper.getPartyName(delegator, employee.get("partyId"), false);
								currDateMisPunchMap.put("partyName",partyName);
								partyIdmisPunchDataMap.put(emplpunchin.get("punchtime"),currDateMisPunchMap);
							}
						}
					}
				}
			}
		}
		if(UtilValidate.isNotEmpty(partyIdmisPunchDataMap)){
			misPunchDataMap.put(employee.get("partyId"),partyIdmisPunchDataMap);
		}
	}
}
context.put("DateKeysList",DateKeysList);
context.put("misPunchDataMap",misPunchDataMap);

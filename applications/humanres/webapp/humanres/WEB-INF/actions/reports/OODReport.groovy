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
	if (parameters.OODfromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.OODfromDate).getTime()));
	}
	if (parameters.OODthruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.OODthruDate).getTime()));
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

emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyId"));

employeeIdsList=[];

if(UtilValidate.isNotEmpty(employments)){
	employments.each { employment ->
		if((parameters.employeeId).equals(employment.get("partyId"))){
			employeeIdsList.add(employment.get("partyId"));
		}
		else{
			if((parameters.employeeId).equals("")){
				employeeIdsList.add(employment.get("partyId"));
			}
		}
	}
}

finalEmpMap=[:];
emplLOPFinalMap=[:];
if(UtilValidate.isNotEmpty(employeeIdsList)){
	employeeIdsList.each { partyId ->
		emplDayMap=[:];
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
			EmplOODMap=[:];
			List empConditionList=[];
			empConditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
			empConditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
			empConditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS, "Ood"));
			empConditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "IN"));
			empCondition=EntityCondition.makeCondition(empConditionList,EntityOperator.AND);
			empPunchindeatils = delegator.findList("EmplPunch", empCondition , null, null, null, false );
			if(UtilValidate.isNotEmpty(empPunchindeatils)){
				empPunchindeatils.each{ emplpunchin ->
					Debug.log("emplpunchin======================"+emplpunchin);
					if((emplpunchin.get("shiftType")).equals("SHIFT_NIGHT")){
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
						dateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(dateFormat.parse(date).getTime()));
						nextDay= UtilDateTime.getNextDayStart(dateStart);
						currentDayTimeStart = UtilDateTime.getDayStart(nextDay);
						nextdate = UtilDateTime.toDateString(currentDayTimeStart);
						dateStr1 = "";
						def sdf2 = new SimpleDateFormat("MM/dd/yyyy");
						try {
							if (date) {
								dateTimestamp1 = new java.sql.Timestamp(sdf2.parse(nextdate).getTime());
								dateStr1 = UtilDateTime.toDateString(dateTimestamp1,"dd/MM/yyyy");
							}
						} catch (ParseException e) {
							Debug.logError(e, "Cannot parse date string: " + e, "");
							context.errorMessage = "Cannot parse date string: " + e;
							return;
						}
						List shiftoutconditionList=[];
						shiftoutconditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(nextDay)));
						shiftoutconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, emplpunchin.get("partyId")));
						shiftoutconditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS, "Ood"));
						shiftoutconditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
						shiftoutconditionList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS, "SHIFT_NIGHT"));
						shiftoutcondition=EntityCondition.makeCondition(shiftoutconditionList,EntityOperator.AND);
						shiftpunchoutdeatils = delegator.findList("EmplPunch", shiftoutcondition , null, null, null, false );
						if(UtilValidate.isNotEmpty(shiftpunchoutdeatils)){
							shiftpunchoutdeatils.each{ emplshiftpunchOut ->
								EmplOODMap.put("punchOuttime",shiftpunchoutdeatils.get("punchtime"));
							}
						}
						String partyName = PartyHelper.getPartyName(delegator,partyId , false);
						EmplOODMap.put("partyId",partyId);
						EmplOODMap.put("partyName",partyName);
						EmplOODMap.put("punchindate",dateStr);
						EmplOODMap.put("punchOutdate",dateStr1);
						EmplOODMap.put("punchintime",emplpunchin.get("punchtime"));
					}else{
						List outConditionList=[];
						outConditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
						outConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, emplpunchin.get("partyId")));
						outConditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS, "Ood"));
						outConditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
						outCondition=EntityCondition.makeCondition(outConditionList,EntityOperator.AND);
						punchOutDetails = delegator.findList("EmplPunch", outCondition , null, null, null, false );
						if(UtilValidate.isNotEmpty(punchOutDetails)){
							punchOutDetails.each{ emplpunchOut ->
								EmplOODMap.put("punchOuttime",emplpunchOut.get("punchtime"));
							}
						}
						String partyName = PartyHelper.getPartyName(delegator,partyId , false);
						EmplOODMap.put("partyId",partyId);
						EmplOODMap.put("partyName",partyName);
						EmplOODMap.put("punchindate",dateStr);
						EmplOODMap.put("punchOutdate",dateStr);
						EmplOODMap.put("punchintime",emplpunchin.get("punchtime"));
					}
				}
			}
			if(UtilValidate.isNotEmpty(EmplOODMap)){
				emplDayMap.put(date,EmplOODMap);
			}
		}
		if(UtilValidate.isNotEmpty(emplDayMap)){
			finalEmpMap.put(partyId,emplDayMap);
		}
	}
}
context.put("finalEmpMap",finalEmpMap);




/*else{
	List outConditionList=[];
	outConditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(date)));
	outConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, emplpunchin.get("partyId")));
	outConditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS, "Ood"));
	outConditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
	outCondition=EntityCondition.makeCondition(outConditionList,EntityOperator.AND);
	punchOutDetails = delegator.findList("EmplPunch", outCondition , null, null, null, false );
	if(UtilValidate.isNotEmpty(punchOutDetails)){
		punchOutDetails.each{ emplpunchOut ->
			String partyName = PartyHelper.getPartyName(delegator,partyId , false);
			EmplOODMap.put("partyId",partyId);
			EmplOODMap.put("partyName",partyName);
			EmplOODMap.put("punchindate",dateStr);
			EmplOODMap.put("punchOutdate",dateStr);
			EmplOODMap.put("punchintime",emplpunchin.get("punchtime"));
			EmplOODMap.put("punchOuttime",emplpunchOut.get("punchtime"));
		}
	}
}*/




/*if((emplpunchin.get("shiftType")).equals("SHIFT_NIGHT")){
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	dateStart = UtilDateTime.getDayStart(new java.sql.Timestamp(dateFormat.parse(date).getTime()));
	nextDay= UtilDateTime.getNextDayStart(dateStart);
	currentDayTimeStart = UtilDateTime.getDayStart(nextDay);
	nextdate = UtilDateTime.toDateString(currentDayTimeStart);
	dateStr1 = "";
	def sdf2 = new SimpleDateFormat("MM/dd/yyyy");
	try {
		if (date) {
			dateTimestamp1 = new java.sql.Timestamp(sdf2.parse(nextdate).getTime());
			dateStr1 = UtilDateTime.toDateString(dateTimestamp1,"dd/MM/yyyy");
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	List shiftoutconditionList=[];
	shiftoutconditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(nextDay)));
	shiftoutconditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, emplpunchin.get("partyId")));
	shiftoutconditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS, "Ood"));
	shiftoutconditionList.add(EntityCondition.makeCondition("InOut", EntityOperator.EQUALS, "OUT"));
	shiftoutconditionList.add(EntityCondition.makeCondition("shiftType", EntityOperator.EQUALS, "SHIFT_NIGHT"));
	shiftoutcondition=EntityCondition.makeCondition(shiftoutconditionList,EntityOperator.AND);
	shiftpunchoutdeatils = delegator.findList("EmplPunch", shiftoutcondition , null, null, null, false );
	if(UtilValidate.isNotEmpty(shiftpunchoutdeatils)){
		shiftpunchoutdeatils.each{ emplshiftpunchOut ->
			String partyName = PartyHelper.getPartyName(delegator,partyId , false);
			EmplOODMap.put("partyId",partyId);
			EmplOODMap.put("partyName",partyName);
			EmplOODMap.put("punchindate",dateStr);
			EmplOODMap.put("punchOutdate",dateStr1);
			EmplOODMap.put("punchintime",emplpunchin.get("punchtime"));
			EmplOODMap.put("punchOuttime",shiftpunchoutdeatils.get("punchtime"));
		}
	}
}*/
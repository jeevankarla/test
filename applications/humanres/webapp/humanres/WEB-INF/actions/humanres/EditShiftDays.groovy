import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.humanres.PayrollService;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;


if (parameters.customTimePeriodId == null) {
	return;
}
timePeriodId=parameters.customTimePeriodId;
deptId=parameters.partyId;
if(parameters.partyIdTo){
	deptId=parameters.partyIdTo;
}
dctx = dispatcher.getDispatchContext();
context.put("type",parameters.type);
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : timePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));

context.timePeriodStart= timePeriodStart;
dates=UtilDateTime.toDateString(timePeriodStart,"dd MMM,yyyy")+"-"+UtilDateTime.toDateString(timePeriodEnd,"dd MMM,yyyy");


Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", deptId);
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
List<GenericValue> shiftTypes = delegator.findList("WorkShiftType",null, null,null, null, true);
shiftTypeIds = EntityUtil.getFieldListFromEntityList(shiftTypes, "shiftTypeId", true);
List noPunchEmpList=[];
List noPunchEmpIds=[];
if(parameters.partyIdTo){
	employementIds=UtilMisc.toList(parameters.partyIdTo);
}else{
	employementIds=employementIds;
}
//getting no punchEmployees
employementIds.each{employementId->
	List conditionList=[];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employementId));
	conditionList.add(EntityCondition.makeCondition("punchType", EntityOperator.EQUALS, "N"));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	noPunchEmpList= delegator.findList("EmployeeDetail", condition, UtilMisc.toSet("partyId"), null, null, false );
	noPunchEmpIds.addAll(EntityUtil.getFieldListFromEntityList(noPunchEmpList, "partyId", true));
	
}

customMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:timePeriodStart,timePeriodEnd:timePeriodEnd,timePeriodId:timePeriodId,locale:locale]);
lastClosePeriod=customMap.get("lastCloseAttedancePeriod");
if(UtilValidate.isNotEmpty(lastClosePeriod))
customTimePeriodId=lastClosePeriod.get("customTimePeriodId");
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employementIds));
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> payrollAttendanceShiftWiseList = delegator.findList("PayrollAttendanceShiftWise", condition, null, ["partyId"], null, false);
finalMap=[:];
noPunchEmpIds.each{partyId ->
	finaList=[];
	emplPayrollAttendanceList = EntityUtil.filterByAnd(payrollAttendanceShiftWiseList,UtilMisc.toMap("partyId",partyId));
	tempMap=[:];
	tempMap["Dates"]=dates;
	tempMap["timePeriodId"]=timePeriodId;
	tempMap["customTimePeriodId"]=customTimePeriodId;
	shiftTypeIds.each{shiftTypeId->
		tempMap[shiftTypeId]=0;
		emplPayrollAttendanceList.each{shiftWise ->
			shiftTypeId=shiftWise.get("shiftTypeId");
			noOfDays=shiftWise.get("noOfDays")
			if(UtilValidate.isEmpty(noOfDays))
				noOfDays=0;
			tempMap[shiftTypeId]=noOfDays;
		}
	}
	finalMap.put(partyId,tempMap);
}

JSONArray shiftDaysJSON = new JSONArray();
if(UtilValidate.isNotEmpty(finalMap)){
	Iterator partyIter = finalMap.entrySet().iterator();
	while(partyIter.hasNext()){
		Map.Entry entry = partyIter.next();
		emplyId= entry.getKey();
		JSONObject newObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, emplyId, false);
		departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId]);
		deptName="";
		if(departmentDetails){
			deptPartyId=departmentDetails[0].partyIdFrom;
			deptName=PartyHelper.getPartyName(delegator, deptPartyId, false);
		}
		newObj.put("id",emplyId+"["+partyName+"]");
		newObj.put("partyId",emplyId);
		if(UtilValidate.isNotEmpty(deptName)){
			newObj.put("deptName",deptName);
		}
		if(UtilValidate.isNotEmpty(entry.getValue())){
			Iterator shiftIter = (entry.getValue()).entrySet().iterator();
			while(shiftIter.hasNext()){
				Map.Entry itemEntry = shiftIter.next();
				newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
			}
		}
		shiftDaysJSON.add(newObj);
	}
}
context.shiftDaysJson=shiftDaysJSON;




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
orgId=parameters.partyId;
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
finalMap=[:];
finalList=[];
Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", deptId);
emplInputMap.put("fromDate", timePeriodStart);
emplInputMap.put("thruDate", timePeriodEnd);
Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);

Map InputMap = FastMap.newInstance();
InputMap.put("userLogin", userLogin);
InputMap.put("orgPartyId", orgId);
InputMap.put("fromDate", timePeriodStart);
InputMap.put("thruDate", timePeriodEnd);
Map resultInputMap = HumanresService.getActiveEmployements(dctx,InputMap);
List<GenericValue> employeesList = (List<GenericValue>)resultInputMap.get("employementList");
employeesList = EntityUtil.orderBy(employeesList, UtilMisc.toList("partyIdTo"));
employeeIds = EntityUtil.getFieldListFromEntityList(employeesList, "partyIdTo", true);


if(parameters.partyIdTo){
	employementIds=UtilMisc.toList(parameters.partyIdTo);
}else{
	employementIds=employementIds;
}

customMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:timePeriodStart,timePeriodEnd:timePeriodEnd,timePeriodId:timePeriodId,locale:locale]);
lastClosePeriod=customMap.get("lastCloseAttedancePeriod");
if(UtilValidate.isNotEmpty(lastClosePeriod))
customTimePeriodId=lastClosePeriod.get("customTimePeriodId");
GenericValue attnCustomTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : customTimePeriodId], false);
attnTimePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(attnCustomTimePeriod.getDate("fromDate")));
attnTimePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(attnCustomTimePeriod.getDate("thruDate")));
totalDays= UtilDateTime.getIntervalInDays(attnTimePeriodStart,attnTimePeriodEnd);
totalDays=totalDays+1;
conList=[];
conList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,customTimePeriodId));
conList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,employeeIds));
conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noOfAttendedDays",EntityOperator.EQUALS,null),EntityOperator.OR,
	EntityCondition.makeCondition("noOfAttendedDays",EntityOperator.GREATER_THAN,BigDecimal.ZERO)));
EntityCondition con=EntityCondition.makeCondition(conList,EntityOperator.AND);
List<GenericValue> payrollCountList=delegator.findList("PayrollAttendance",con,UtilMisc.toSet("partyId"),null,null,false);

if("leaveEncash".equals(screenFlag)){
	customTimePeriodId=timePeriodId;
}
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employementIds));
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> payrollAttendanceList = delegator.findList("PayrollAttendance", condition, null, ["partyId"], null, false);
if(UtilValidate.isNotEmpty(employementList)){
	employementList.each{employement ->
		partyId=employement.get("partyIdTo");
		emplPayrollAttendanceList = EntityUtil.filterByAnd(payrollAttendanceList,UtilMisc.toMap("partyId",partyId));
		tempFinalMap=[:];
		tempFinalMap["timePeriodId"]="";
		tempFinalMap.put("customTimePeriodId",customTimePeriodId);
		tempFinalMap.put("timePeriodId",timePeriodId);
		tempFinalMap.put("Dates",dates);
		tempFinalMap.put("deptId", deptId);
		tempFinalMap["noOfPayableDays"]="";
		if(!"leaveEncash".equals(screenFlag)){
			tempFinalMap["noOfAttendedDays"]="";
			tempFinalMap["noOfCalenderDays"]=totalDays;
			tempFinalMap["casualLeaveDays"]="";
			tempFinalMap["earnedLeaveDays"]="";
			tempFinalMap["commutedLeaveDays"]="";
			tempFinalMap["disabilityLeaveDays"]="";
			tempFinalMap["extraOrdinaryLeaveDays"]="";
			tempFinalMap["noOfAttendedHoliDays"]="";
			tempFinalMap["noOfAttendedSsDays"]="";
			tempFinalMap["noOfAttendedWeeklyOffDays"]="";
			tempFinalMap["noOfCompoffAvailed"]="";
			tempFinalMap["lossOfPayDays"]="";
			tempFinalMap["noOfArrearDays"]="";
			tempFinalMap["noOfNightAllowanceDays"]="";
			tempFinalMap["coldOrBoiledAllowanceDays"]="";
			tempFinalMap["noOfRiskAllowanceDays"]="";
			tempFinalMap["heavyTankerAllowanceDays"]="";
			tempFinalMap["trTankerAllowanceDays"]="";
			tempFinalMap["operatingAllowanceDays"]="";
			tempFinalMap["inChargeAllowanceDays"]="";
			tempFinalMap["noOfHalfPayDays"]="";
		}else{
			tempFinalMap["noOfCalenderDays"]=30;
		}
		
		 emplPayrollAttendanceList.each{payrollAttendance->
			
			tempFinalMap.put("Dates",dates);
			customTimePeriodId=payrollAttendance.get("customTimePeriodId");
			if("leaveEncash".equals(screenFlag)){
				customTimePeriodId=timePeriodId
				noOfCalenderDays=30;
				tempFinalMap.put("noOfCalenderDays",noOfCalenderDays);
			}
			
			noOfPayableDays="";
			if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfPayableDays")) && payrollAttendance.get("noOfPayableDays")!=0)
			noOfPayableDays=payrollAttendance.get("noOfPayableDays");
			tempFinalMap.put("noOfPayableDays",noOfPayableDays);
			if(!"leaveEncash".equals(screenFlag)){
				noOfAttendedDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedDays")) && payrollAttendance.get("noOfAttendedDays")!=0)
				noOfAttendedDays=payrollAttendance.get("noOfAttendedDays");
				tempFinalMap.put("noOfAttendedDays",noOfAttendedDays);
				
				noOfCalenderDays=totalDays;
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfCalenderDays")) && payrollAttendance.get("noOfCalenderDays")!=0)
				noOfCalenderDays=payrollAttendance.get("noOfCalenderDays");
				tempFinalMap.put("noOfCalenderDays",noOfCalenderDays);
				
				casualLeaveDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("casualLeaveDays")) && payrollAttendance.get("casualLeaveDays")!=0)
				casualLeaveDays=payrollAttendance.get("casualLeaveDays");
				tempFinalMap.put("casualLeaveDays",casualLeaveDays);
				
				earnedLeaveDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("earnedLeaveDays")) && payrollAttendance.get("earnedLeaveDays")!=0)
				earnedLeaveDays=payrollAttendance.get("earnedLeaveDays");
				tempFinalMap.put("earnedLeaveDays",earnedLeaveDays);
				
				commutedLeaveDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("commutedLeaveDays")) && payrollAttendance.get("commutedLeaveDays")!=0)
				commutedLeaveDays=payrollAttendance.get("commutedLeaveDays");
				tempFinalMap.put("commutedLeaveDays",commutedLeaveDays);
				
				disabilityLeaveDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("disabilityLeaveDays")) && payrollAttendance.get("disabilityLeaveDays")!=0)
				disabilityLeaveDays=payrollAttendance.get("disabilityLeaveDays");
				tempFinalMap.put("disabilityLeaveDays",disabilityLeaveDays);
				
				extraOrdinaryLeaveDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("extraOrdinaryLeaveDays")) && payrollAttendance.get("extraOrdinaryLeaveDays")!=0)
				extraOrdinaryLeaveDays=payrollAttendance.get("extraOrdinaryLeaveDays");
				tempFinalMap.put("extraOrdinaryLeaveDays",extraOrdinaryLeaveDays);
				
				noOfAttendedHoliDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedHoliDays")) && payrollAttendance.get("noOfAttendedHoliDays")!=0)
				noOfAttendedHoliDays=payrollAttendance.get("noOfAttendedHoliDays");
				tempFinalMap.put("noOfAttendedHoliDays",noOfAttendedHoliDays);
				
				noOfAttendedSsDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedSsDays")) && payrollAttendance.get("noOfAttendedSsDays")!=0)
				noOfAttendedSsDays=payrollAttendance.get("noOfAttendedSsDays");
				tempFinalMap.put("noOfAttendedSsDays",noOfAttendedSsDays);
				
				noOfAttendedWeeklyOffDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfAttendedWeeklyOffDays")) && payrollAttendance.get("noOfAttendedWeeklyOffDays")!=0)
				noOfAttendedWeeklyOffDays=payrollAttendance.get("noOfAttendedWeeklyOffDays");
				tempFinalMap.put("noOfAttendedWeeklyOffDays",noOfAttendedWeeklyOffDays);
				
				noOfCompoffAvailed="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfCompoffAvailed")) && payrollAttendance.get("noOfCompoffAvailed")!=0)
				noOfCompoffAvailed=payrollAttendance.get("noOfCompoffAvailed");
				tempFinalMap.put("noOfCompoffAvailed",noOfCompoffAvailed);
				
				lossOfPayDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("lossOfPayDays")) && payrollAttendance.get("lossOfPayDays")!=0)
				lossOfPayDays=payrollAttendance.get("lossOfPayDays");
				tempFinalMap.put("lossOfPayDays",lossOfPayDays);
				
				noOfArrearDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfArrearDays")) && payrollAttendance.get("noOfArrearDays")!=0)
				noOfArrearDays=payrollAttendance.get("noOfArrearDays");
				tempFinalMap.put("noOfArrearDays",noOfArrearDays);
							
				noOfNightAllowanceDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfNightAllowanceDays")) && payrollAttendance.get("noOfNightAllowanceDays")!=0)
				noOfNightAllowanceDays=payrollAttendance.get("noOfNightAllowanceDays");
				tempFinalMap.put("noOfNightAllowanceDays",noOfNightAllowanceDays);
				
				coldOrBoiledAllowanceDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("coldOrBoiledAllowanceDays")) && payrollAttendance.get("coldOrBoiledAllowanceDays")!=0)
				coldOrBoiledAllowanceDays=payrollAttendance.get("coldOrBoiledAllowanceDays");
				tempFinalMap.put("coldOrBoiledAllowanceDays",coldOrBoiledAllowanceDays);
				
				noOfRiskAllowanceDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfRiskAllowanceDays")) && payrollAttendance.get("noOfRiskAllowanceDays")!=0)
				noOfRiskAllowanceDays=payrollAttendance.get("noOfRiskAllowanceDays");
				tempFinalMap.put("noOfRiskAllowanceDays",noOfRiskAllowanceDays);
				
				heavyTankerAllowanceDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("heavyTankerAllowanceDays")) && payrollAttendance.get("heavyTankerAllowanceDays")!=0)
				heavyTankerAllowanceDays=payrollAttendance.get("heavyTankerAllowanceDays");
				tempFinalMap.put("heavyTankerAllowanceDays",heavyTankerAllowanceDays);
				
				trTankerAllowanceDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("trTankerAllowanceDays")) && payrollAttendance.get("trTankerAllowanceDays")!=0)
				trTankerAllowanceDays=payrollAttendance.get("trTankerAllowanceDays");
				tempFinalMap.put("trTankerAllowanceDays",trTankerAllowanceDays);
	
				operatingAllowanceDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("operatingAllowanceDays")) && payrollAttendance.get("operatingAllowanceDays")!=0)
				operatingAllowanceDays=payrollAttendance.get("operatingAllowanceDays");
				tempFinalMap.put("operatingAllowanceDays",operatingAllowanceDays);
				
				inChargeAllowanceDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("inChargeAllowanceDays")) && payrollAttendance.get("inChargeAllowanceDays")!=0)
				inChargeAllowanceDays=payrollAttendance.get("inChargeAllowanceDays");
				tempFinalMap.put("inChargeAllowanceDays",inChargeAllowanceDays);
				
				noOfHalfPayDays="";
				if(UtilValidate.isNotEmpty(payrollAttendance.get("noOfHalfPayDays")) && payrollAttendance.get("noOfHalfPayDays")!=0 )
				noOfHalfPayDays=payrollAttendance.get("noOfHalfPayDays");
				tempFinalMap.put("noOfHalfPayDays",noOfHalfPayDays);
			}
		}
		 finalMap.put(partyId,tempFinalMap);
	}
}

JSONArray payrollJSON = new JSONArray();
if(UtilValidate.isNotEmpty(finalMap)){
	Iterator PayIter = finalMap.entrySet().iterator();
	while(PayIter.hasNext()){
		Map.Entry entry = PayIter.next();
		emplyId= entry.getKey();
		JSONObject newObj = new JSONObject();
		partyName=PartyHelper.getPartyName(delegator, emplyId, false);
		departmentDetails=delegator.findByAnd("Employment", [partyIdTo : emplyId,thruDate:null]);
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
			Iterator payItemIter = (entry.getValue()).entrySet().iterator();
			while(payItemIter.hasNext()){
				Map.Entry itemEntry = payItemIter.next();
				newObj.put(itemEntry.getKey(),((itemEntry.getValue())));
			}
		}
		payrollJSON.add(newObj);
	}
}
context.payrollJson=payrollJSON;
totalEmpls=0;
enteredEmpls=0;
remainingEmpls=0;
totalEmpls=employeeIds.size();
enteredEmpls=payrollCountList.size();
remainingEmpls=employeeIds.size()-payrollCountList.size();
JSONObject emplsCountJson = new JSONObject();;
if(UtilValidate.isNotEmpty(totalEmpls) && UtilValidate.isNotEmpty(enteredEmpls)){
		emplsCountJson.put("totalEmpls",totalEmpls);
		emplsCountJson.put("enteredEmpls",enteredEmpls);
		emplsCountJson.put("remainingEmpls",remainingEmpls);
}
context.emplsCountJson=emplsCountJson;

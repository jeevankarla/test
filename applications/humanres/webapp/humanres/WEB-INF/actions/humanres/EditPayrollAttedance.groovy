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
if(parameters.partyIdTo){
	employementIds=UtilMisc.toList(parameters.partyIdTo);
}else{
	employementIds=employementIds;
}

customMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:timePeriodStart,timePeriodEnd:timePeriodEnd,timePeriodId:timePeriodId,locale:locale]);
lastClosePeriod=customMap.get("lastCloseAttedancePeriod");
if(UtilValidate.isNotEmpty(lastClosePeriod))
customTimePeriodId=lastClosePeriod.get("customTimePeriodId");

conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employementIds));
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> payrollAttendanceList = delegator.findList("PayrollAttendance", condition, null, ["partyId"], null, false);

if(UtilValidate.isNotEmpty(employementList)){
	employementList.each{employement ->
		emplPayrollAttendanceList = EntityUtil.filterByAnd(payrollAttendanceList,UtilMisc.toMap("partyId",employement.get("partyIdTo")));
		tempFinalMap=[:];
		tempFinalMap["timePeriodId"]="";
		tempFinalMap["customTimePeriodId"]="";
		tempFinalMap["Dates"]="";
		tempFinalMap["noOfPayableDays"]=0;
		tempFinalMap["noOfAttendedDays"]=0;
		tempFinalMap["noOfCalenderDays"]=0;
		tempFinalMap["noOfLeaveDays"]=0;
		tempFinalMap["noOfAttendedHoliDays"]=0;
		tempFinalMap["noOfAttendedSsDays"]=0;
		tempFinalMap["noOfAttendedWeeklyOffDays"]=0;
		tempFinalMap["noOfCompoffAvailed"]=0;
		tempFinalMap["lossOfPayDays"]=0;
		tempFinalMap["noOfArrearDays"]=0;
		tempFinalMap["noOfNightAllowanceDays"]=0;
		tempFinalMap["coldOrBoiledAllowanceDays"]=0;
		tempFinalMap["noOfRiskAllowanceDays"]=0;
		tempFinalMap["heavyTankerAllowanceDays"]=0;
		tempFinalMap["trTankerAllowanceDays"]=0;
		tempFinalMap["operatingAllowanceDays"]=0;
		tempFinalMap["inChargeAllowanceDays"]=0;
		tempFinalMap["noOfHalfPayDays"]=0;
		 emplPayrollAttendanceList.each{payrollAttendance->
			
			partyId=payrollAttendance.get("partyId");
			tempFinalMap.put("timePeriodId",timePeriodId);
			tempFinalMap.put("Dates",dates);
			customTimePeriodId=payrollAttendance.get("customTimePeriodId");
			tempFinalMap.put("customTimePeriodId",customTimePeriodId);
			
			noOfPayableDays=payrollAttendance.get("noOfPayableDays");
			if(UtilValidate.isEmpty(noOfPayableDays))
			noOfPayableDays=0;
			tempFinalMap.put("noOfPayableDays",noOfPayableDays);
			
			noOfAttendedDays=payrollAttendance.get("noOfAttendedDays");
			if(UtilValidate.isEmpty(noOfAttendedDays))
			noOfAttendedDays=0;
			tempFinalMap.put("noOfAttendedDays",noOfAttendedDays);
			
			noOfCalenderDays=payrollAttendance.get("noOfCalenderDays");
			if(UtilValidate.isEmpty(noOfCalenderDays))
			noOfCalenderDays=0;
			tempFinalMap.put("noOfCalenderDays",noOfCalenderDays);
			
			noOfLeaveDays=payrollAttendance.get("noOfLeaveDays");
			if(UtilValidate.isEmpty(noOfLeaveDays))
			noOfLeaveDays=0;
			tempFinalMap.put("noOfLeaveDays",noOfLeaveDays);
			
			noOfAttendedHoliDays=payrollAttendance.get("noOfAttendedHoliDays");
			if(UtilValidate.isEmpty(noOfAttendedHoliDays))
			noOfAttendedHoliDays=0;
			tempFinalMap.put("noOfAttendedHoliDays",noOfAttendedHoliDays);
			
			noOfAttendedSsDays=payrollAttendance.get("noOfAttendedSsDays");
			if(UtilValidate.isEmpty(noOfAttendedSsDays))
			noOfAttendedSsDays=0;
			tempFinalMap.put("noOfAttendedSsDays",noOfAttendedSsDays);
			
			noOfAttendedWeeklyOffDays=payrollAttendance.get("noOfAttendedWeeklyOffDays");
			if(UtilValidate.isEmpty(noOfAttendedWeeklyOffDays))
			noOfAttendedWeeklyOffDays=0;
			tempFinalMap.put("noOfAttendedWeeklyOffDays",noOfAttendedWeeklyOffDays);
			
			noOfCompoffAvailed=payrollAttendance.get("noOfCompoffAvailed");
			if(UtilValidate.isEmpty(noOfCompoffAvailed))
			noOfCompoffAvailed=0;
			tempFinalMap.put("noOfCompoffAvailed",noOfCompoffAvailed);
			
			lossOfPayDays=payrollAttendance.get("lossOfPayDays");
			if(UtilValidate.isEmpty(lossOfPayDays))
			lossOfPayDays=0;
			tempFinalMap.put("lossOfPayDays",lossOfPayDays);
			
			noOfArrearDays=payrollAttendance.get("noOfArrearDays");
			if(UtilValidate.isEmpty(noOfArrearDays))
			noOfArrearDays=0;
			tempFinalMap.put("noOfArrearDays",noOfArrearDays);
						
			noOfNightAllowanceDays=payrollAttendance.get("noOfNightAllowanceDays");
			if(UtilValidate.isEmpty(noOfNightAllowanceDays))
			noOfNightAllowanceDays=0;
			tempFinalMap.put("noOfNightAllowanceDays",noOfNightAllowanceDays);
			
			coldOrBoiledAllowanceDays=payrollAttendance.get("coldOrBoiledAllowanceDays");
			if(UtilValidate.isEmpty(coldOrBoiledAllowanceDays))
			coldOrBoiledAllowanceDays=0;
			tempFinalMap.put("coldOrBoiledAllowanceDays",coldOrBoiledAllowanceDays);
			
			noOfRiskAllowanceDays=payrollAttendance.get("noOfRiskAllowanceDays");
			if(UtilValidate.isEmpty(noOfRiskAllowanceDays))
			noOfRiskAllowanceDays=0;
			tempFinalMap.put("noOfRiskAllowanceDays",noOfRiskAllowanceDays);
			
			heavyTankerAllowanceDays=payrollAttendance.get("heavyTankerAllowanceDays");
			if(UtilValidate.isEmpty(heavyTankerAllowanceDays))
			heavyTankerAllowanceDays=0;
			tempFinalMap.put("heavyTankerAllowanceDays",heavyTankerAllowanceDays);
			
			trTankerAllowanceDays=payrollAttendance.get("trTankerAllowanceDays");
			if(UtilValidate.isEmpty(trTankerAllowanceDays))
			trTankerAllowanceDays=0;
			tempFinalMap.put("trTankerAllowanceDays",trTankerAllowanceDays);

			operatingAllowanceDays=payrollAttendance.get("operatingAllowanceDays");
			if(UtilValidate.isEmpty(operatingAllowanceDays))
			operatingAllowanceDays=0;
			tempFinalMap.put("operatingAllowanceDays",operatingAllowanceDays);
			
			inChargeAllowanceDays=payrollAttendance.get("inChargeAllowanceDays");
			if(UtilValidate.isEmpty(inChargeAllowanceDays))
			inChargeAllowanceDays=0;
			tempFinalMap.put("inChargeAllowanceDays",inChargeAllowanceDays);
			
			noOfHalfPayDays=payrollAttendance.get("noOfHalfPayDays");
			if(UtilValidate.isEmpty(noOfHalfPayDays))
			noOfHalfPayDays=0;
			tempFinalMap.put("noOfHalfPayDays",noOfHalfPayDays);
			
			finalMap.put(partyId,tempFinalMap);
			
		}
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

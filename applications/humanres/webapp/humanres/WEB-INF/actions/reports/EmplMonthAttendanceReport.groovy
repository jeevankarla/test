import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
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
dctx = dispatcher.getDispatchContext();
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);

if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
def dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
def sdfDay = new SimpleDateFormat("ddd");
def sdf = new SimpleDateFormat("dd/MM/yyyy");
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));
context.timePeriodStart= timePeriodStart;

context.timePeriodEnd= timePeriodEnd;

Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
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
weekMap=[:];
weekMap["1"]="Sunday";
weekMap["2"]="Monday";
weekMap["3"]="Tuesday";
weekMap["4"]="Wednesday";
weekMap["5"]="Thursday";
weekMap["6"]="Friday";
weekMap["7"]="Saturday";
saturdaysListMap=[:];
EmplWiseMap=[:];
EmplDetailsMap=[:];
GeneralHolidayMap=[:];
OODMap=[:];
AbsentMap=[:];
CHLeaveTypeMap=[:];
totalDays=UtilDateTime.getIntervalInDays(timePeriodStart,timePeriodEnd);
totalDays=totalDays+1;
if(UtilValidate.isNotEmpty(employementIds)){
	employementIds.each{ emplId->
		employeeId=emplId;
		punchInMap=[:];
		dayWiseMap=[:];		
		empDetails = delegator.findOne("EmployeeDetail", [partyId : employeeId],true);
		emplWeekOf=null;
		if(UtilValidate.isNotEmpty(empDetails)){
			emplWeekOf=empDetails.get("weeklyOff");
		}
		List weekOfList=FastList.newInstance();
		j=0;
		k=0;
		GHMap=[:];
		absentListMap=[:];
		OODdayWiseMap=[:];
		CHListMap=[:];
		for( i=0 ; i < (totalDays); i++){
			currentDay =UtilDateTime.addDaysToTimestamp(timePeriodStart, i);
			dayBegin=UtilDateTime.getDayStart(currentDay);
			dayEnd=UtilDateTime.getDayEnd(currentDay);
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(dayBegin)));
			conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(dayEnd)));
			conditionList.add(EntityCondition.makeCondition("partyId", employeeId));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			punchList = delegator.findList("EmplPunch", condition, null, null, null, false);
			punchINOUTMap=[:];
			if(UtilValidate.isNotEmpty(punchList)){
				punchList.each{ punchDetails->
					punchDetlsMap=[:];
					inOut= punchDetails.get("InOut");
					punchDate= punchDetails.get("punchdate");					
					if(UtilValidate.isNotEmpty(punchDate)){
						punchDate=UtilDateTime.toDateString(punchDetails.get("punchdate"), "dd/MM");
					}
					punchDetlsMap["punchdate"]=punchDate;
					String punchTime = timeFormat.format(punchDetails.get("punchtime"));
					punchDetlsMap["punchTime"]=punchTime;
					String shift="";
					if(punchDetails.get("shiftType")=="SHIFT_GEN"){
						shift="GNRL";
					}
					if(punchDetails.get("shiftType")=="SHIFT_01"){
						shift="I";
					}
					if(punchDetails.get("shiftType")=="SHIFT_02"){
						shift="II";
					}
					if(punchDetails.get("shiftType")=="SHIFT_NIGHT"){
						shift="III";
					}
					punchDetlsMap["shiftType"]=shift;
					timestamp = UtilDateTime.toDateString(punchDetails.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
					
					punchDetlsMap["dateFormat"]= new java.sql.Timestamp(dateTimeFormat.parse(timestamp).getTime());
					punchINOUTMap.put(inOut,punchDetlsMap);
				}
				if(UtilValidate.isNotEmpty(punchINOUTMap)){					
					dayWiseMap.put(currentDay,punchINOUTMap);				
				}
			}
			if(UtilValidate.isEmpty(punchList)){
				leaveConditionList=[];
				leaveConditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
				leaveConditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
				leaveConditionList.add(EntityCondition.makeCondition("partyId", employeeId));
				EntityCondition leaveCondition = EntityCondition.makeCondition(leaveConditionList,EntityOperator.AND);
				leaveList = delegator.findList("EmplLeave", leaveCondition, null, null, null, false);
				if(UtilValidate.isEmpty(leaveList)){
					HolidayscondList=[];
					HolidayscondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("holiDayDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin), EntityOperator.AND,
						EntityCondition.makeCondition("holiDayDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
					Holidayscond = EntityCondition.makeCondition(HolidayscondList,EntityOperator.AND);
					GHolidaysList = delegator.findList("HolidayCalendar", Holidayscond, null, null, null, false);
					if(UtilValidate.isEmpty(GHolidaysList)){
						String dayOfWeek = (UtilDateTime.getDayOfWeek(currentDay, timeZone, locale)).toString();
						if(dayOfWeek.equals("7")){
							k=k+1;
							if(k!=2){
								absentListMap.put(currentDay,currentDay);
							}
						}
						else{
							if(emplWeekOf.equalsIgnoreCase(weekMap.get(dayOfWeek))){
							}else{
								absentListMap.put(currentDay,currentDay);
							}
						}
					}
				}
				else{
					leaveList.each{ leaveType->
						if(leaveType.get("leaveTypeId")=="LOP" || leaveType.get("leaveTypeId")=="CHGH" || leaveType.get("leaveTypeId")=="CHSS"){
							CHListMap.put(currentDay,currentDay);
						}
					}
				}
			}
			String dayOfWeek = (UtilDateTime.getDayOfWeek(currentDay, timeZone, locale)).toString();
			if(dayOfWeek.equals("7")){
				j=j+1;
				if(j==2){
					saturdaysListMap.put(employeeId,currentDay);
				}
			}
			if(UtilValidate.isNotEmpty(weekMap.get(dayOfWeek))){
				if(emplWeekOf.equalsIgnoreCase(weekMap.get(dayOfWeek))){
					weekOfList.add(currentDay);
				}
			}
			List empConditionList=[];
			empConditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.EQUALS, UtilDateTime.toSqlDate(dayBegin)));
			empConditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, employeeId));
			empConditionList.add(EntityCondition.makeCondition("PunchType", EntityOperator.EQUALS, "Ood"));
			empCondition=EntityCondition.makeCondition(empConditionList,EntityOperator.AND);
			empPunchindeatils = delegator.findList("EmplPunch", empCondition , null, null, null, false );
			OODpunchINOUTMap=[:];
			if(UtilValidate.isNotEmpty(empPunchindeatils)){
				empPunchindeatils.each{ emplpunchin ->
					OODDetailsMap=[:];
					InOut= emplpunchin.get("InOut");
					OODpunchDate= emplpunchin.get("punchdate");
					
					if(UtilValidate.isNotEmpty(OODpunchDate)){
						OODpunchDate=UtilDateTime.toDateString(emplpunchin.get("punchdate"), "dd/MM");
					}
					OODDetailsMap["punchdate"]=punchDate;
					String punchTime = timeFormat.format(emplpunchin.get("punchtime"));
					OODDetailsMap["punchTime"]=punchTime;
					OODDetailsMap["shiftType"]=emplpunchin.get("shiftType");
					timestamp = UtilDateTime.toDateString(emplpunchin.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
					OODDetailsMap["dateFormat"]= new java.sql.Timestamp(dateTimeFormat.parse(timestamp).getTime());
					OODpunchINOUTMap.put(InOut,OODDetailsMap);
				}
			}
			if(UtilValidate.isNotEmpty(OODpunchINOUTMap)){
				OODdayWiseMap.put(currentDay,OODpunchINOUTMap);
				OODMap.put(employeeId,OODdayWiseMap);
			}
			if(UtilValidate.isNotEmpty(CHListMap)){
				CHLeaveTypeMap.put(employeeId,CHListMap);
			}
			HolidaysconditionList=[];
			HolidaysconditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("holiDayDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin), EntityOperator.AND,
				EntityCondition.makeCondition("holiDayDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
			Holidayscondition = EntityCondition.makeCondition(HolidaysconditionList,EntityOperator.AND);
			HolidaysList = delegator.findList("HolidayCalendar", Holidayscondition, null, null, null, false);
			if(UtilValidate.isNotEmpty(HolidaysList)){
				HolidaysList.each{holiday->
					GHMap.put(currentDay,holiday.get("holiDayDate"))
					GeneralHolidayMap.put(employeeId,GHMap);
				}
			}
		}
		
		leaveConList=[];
		leaveConList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN_EQUAL_TO,timePeriodStart));
		leaveConList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,timePeriodEnd));
		leaveConList.add(EntityCondition.makeCondition("partyId", employeeId));
		EntityCondition cond = EntityCondition.makeCondition(leaveConList,EntityOperator.AND);
		leaveList = delegator.findList("EmplLeave", cond, null, null, null, false);
		if(UtilValidate.isNotEmpty(leaveList)){
			punchInMap.put("leaveList",leaveList);
		}	
		if(UtilValidate.isNotEmpty(weekOfList)){	
			punchInMap.put("weekOfList",weekOfList);
			EmplDetailsMap.put(employeeId,punchInMap);
		}
		if(UtilValidate.isNotEmpty(dayWiseMap)){		
			EmplWiseMap.put(employeeId,dayWiseMap);
		}
		if(UtilValidate.isNotEmpty(absentListMap)){
			AbsentMap.put(employeeId,absentListMap);
		}
	}
}
misPunchMap=[:];
if(UtilValidate.isNotEmpty(EmplWiseMap)){
	Iterator emplWiseIter = EmplWiseMap.entrySet().iterator();
	while(emplWiseIter.hasNext()){
		Map.Entry emplWiseEntry = emplWiseIter.next();
		Map emplWiseEntryMap = (Map)emplWiseEntry.getValue();
		Iterator emplWiseEntryMapIter = emplWiseEntryMap.entrySet().iterator();
		misPunchdateMap=[:];
		while(emplWiseEntryMapIter.hasNext()){
			Map.Entry emplEntry = emplWiseEntryMapIter.next();
			if((emplEntry.getValue().get("OUT")).equals(null)){
				misPunchdateMap.put(emplEntry.getKey(),"mispunch");
			}
		}
		misPunchMap.put(emplWiseEntry.getKey(),misPunchdateMap);
	}
}
context.put("CHLeaveTypeMap",CHLeaveTypeMap);
context.put("AbsentMap",AbsentMap);
context.put("saturdaysListMap",saturdaysListMap);
context.put("GeneralHolidayMap",GeneralHolidayMap);
context.put("OODMap",OODMap);
context.put("misPunchMap",misPunchMap);
context.put("EmplWiseMap",EmplWiseMap);
context.put("EmplDetailsMap",EmplDetailsMap);


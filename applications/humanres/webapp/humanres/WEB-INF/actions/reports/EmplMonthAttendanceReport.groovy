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
EmplWiseMap=[:];
EmplDetailsMap=[:];
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
		for( i=0 ; i <= (totalDays); i++){
			currentDay =UtilDateTime.addDaysToTimestamp(timePeriodStart, i);
			
			
			dayBegin=UtilDateTime.getDayStart(currentDay);
			dayEnd=UtilDateTime.getDayEnd(currentDay);
			conditionList=[];
			conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.GREATER_THAN_EQUAL_TO , UtilDateTime.toSqlDate(dayBegin)));
			conditionList.add(EntityCondition.makeCondition("punchdate", EntityOperator.LESS_THAN_EQUAL_TO , UtilDateTime.toSqlDate(dayEnd)));
			conditionList.add(EntityCondition.makeCondition("partyId", employeeId));
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			//orderBy = UtilMisc.toList("punchdate","punchtime");
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
					punchDetlsMap["shiftType"]=punchDetails.get("shiftType");
					timestamp = UtilDateTime.toDateString(punchDetails.get("punchdate"), "dd/MM/yyyy") + " " + punchTime;
					
					punchDetlsMap["dateFormat"]= new java.sql.Timestamp(dateTimeFormat.parse(timestamp).getTime());
					punchINOUTMap.put(inOut,punchDetlsMap);
					
					
				}
				if(UtilValidate.isNotEmpty(punchINOUTMap)){					
					dayWiseMap.put(currentDay,punchINOUTMap);				
				}
			}
			String dayOfWeek = (UtilDateTime.getDayOfWeek(currentDay, timeZone, locale)).toString();
			if(emplWeekOf.equalsIgnoreCase(weekMap.get(dayOfWeek))){
				weekOfList.add(currentDay);
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
		
		
	}
	
}

context.put("EmplWiseMap",EmplWiseMap);
context.put("EmplDetailsMap",EmplDetailsMap);






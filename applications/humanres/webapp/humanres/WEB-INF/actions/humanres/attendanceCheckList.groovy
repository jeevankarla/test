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

dctx = dispatcher.getDispatchContext();
reportTypeFlag = parameters.reportTypeFlag;
context.dctx=dctx;
allChanges= false;

if (reportTypeFlag.equals("attendanceAllCheckList")) {
	allChanges = true;
}

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
if(reportTypeFlag.equals("attendanceMyCheckList")){
	try {
		if (parameters.AttMyfromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.AttMyfromDate).getTime()));
		}
		if (parameters.AttMythruDate) {
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.AttMythruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}else if(reportTypeFlag.equals("attendanceAllCheckList")){
	try {
		if (parameters.AttAllfromDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.AttAllfromDate).getTime()));
		}
		if (parameters.AttAllthruDate) {
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.AttAllthruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
}

context.put("fromDate",fromDate);
context.put("thruDate",thruDate);

dayBegin = UtilDateTime.getDayStart(fromDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(thruDate, timeZone, locale);

BigDecimal daysCheck = new BigDecimal("10");

employementIds = [];
Map emplInputMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(parameters.partyId)){
	emplInputMap.put("orgPartyId", parameters.partyId);
}else{
	emplInputMap.put("orgPartyId", "Company");
}
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", dayBegin);
emplInputMap.put("thruDate", dayEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);

employeeAttendanceMap = [:];
conditionList = [];
if(!allChanges){
	conditionList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
}
//conditionList.add(EntityCondition.makeCondition("noOfPayableDays", EntityOperator.LESS_THAN_EQUAL_TO, daysCheck));
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, employementIds));
conditionList.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)
   ], EntityOperator.AND));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
payableDaysList = delegator.findList("PayrollAttendance", condition, null, ["lastUpdatedStamp"], null, false);

if(UtilValidate.isNotEmpty(payableDaysList)){
	payableDaysList.each { payrollAttendanceDetails ->
		totalDays = 0;
		partyId = payrollAttendanceDetails.get("partyId");
		detailsMap = [:];
		unitDetails = delegator.findList("Employment", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , partyId), null, null, null, false);
		if(UtilValidate.isNotEmpty(unitDetails)){
			unitDetails = EntityUtil.getFirst(unitDetails);
			if(UtilValidate.isNotEmpty(unitDetails))	{
				locationGeoId=unitDetails.get("locationGeoId");
				detailsMap.put("unit",locationGeoId);
			}
		}
		partyRelationconditionList=[];
		partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS , "DEPARTMENT"));
		partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
		partyRelationconditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
		partyCondition = EntityCondition.makeCondition(partyRelationconditionList,EntityOperator.AND);
		def orderBy = UtilMisc.toList("comments");
		partyRelationList = delegator.findList("PartyRelationship", partyCondition, null, orderBy, null, false);
		if(UtilValidate.isNotEmpty(partyRelationList)){
			costDetails = EntityUtil.getFirst(partyRelationList);
			if(UtilValidate.isNotEmpty(costDetails))	{
				costCode=costDetails.get("comments");
				detailsMap.put("costCode",costCode);
			}
		}
		noOfAttendedDays = payrollAttendanceDetails.get("noOfAttendedDays");
		noOfAttendedHoliDays = payrollAttendanceDetails.get("noOfAttendedHoliDays");
		casualLeaveDays = payrollAttendanceDetails.get("casualLeaveDays");
		earnedLeaveDays = payrollAttendanceDetails.get("earnedLeaveDays");
		commutedLeaveDays = payrollAttendanceDetails.get("commutedLeaveDays");
		noOfHalfPayDays = payrollAttendanceDetails.get("noOfHalfPayDays");
		disabilityLeaveDays = payrollAttendanceDetails.get("disabilityLeaveDays");
		extraOrdinaryLeaveDays = payrollAttendanceDetails.get("extraOrdinaryLeaveDays");
		if(UtilValidate.isNotEmpty(noOfAttendedDays)){
			totalDays = totalDays + noOfAttendedDays;
		}
		if(UtilValidate.isNotEmpty(noOfAttendedHoliDays)){
			totalDays = totalDays + noOfAttendedHoliDays;
		}
		if(UtilValidate.isNotEmpty(casualLeaveDays)){
			totalDays = totalDays + casualLeaveDays;
		}
		if(UtilValidate.isNotEmpty(noOfAttendedDays)){
			detailsMap.put("WKD",noOfAttendedDays);
		}
		if(UtilValidate.isNotEmpty(noOfAttendedHoliDays)){
			detailsMap.put("CO",noOfAttendedHoliDays);
		}
		if(UtilValidate.isNotEmpty(casualLeaveDays)){
			detailsMap.put("CL",casualLeaveDays);
		}
		if(UtilValidate.isNotEmpty(earnedLeaveDays)){
			detailsMap.put("EL",earnedLeaveDays);
		}
		if(UtilValidate.isNotEmpty(commutedLeaveDays)){
			detailsMap.put("CHPL",commutedLeaveDays);
		}
		if(UtilValidate.isNotEmpty(noOfHalfPayDays)){
			detailsMap.put("HPL",noOfHalfPayDays);
		}
		if(UtilValidate.isNotEmpty(disabilityLeaveDays)){
			detailsMap.put("DBL",disabilityLeaveDays);
		}
		if(UtilValidate.isNotEmpty(extraOrdinaryLeaveDays)){
			detailsMap.put("EOL",extraOrdinaryLeaveDays);
		}
		detailsMap.put("totalDays",totalDays);
		if(UtilValidate.isNotEmpty(detailsMap)){
			employeeAttendanceMap.put(partyId,detailsMap);
		}
	}
}

context.put("employeeAttendanceMap",employeeAttendanceMap);


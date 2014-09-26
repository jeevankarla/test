import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.humanres.EmplLeaveService;

dctx = dispatcher.getDispatchContext();

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");

GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);

emplInputMap = [:];
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("orgPartyId", "Company");
emplInputMap.put("fromDate", fromDate);
emplInputMap.put("thruDate", thruDate);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
employments=EmploymentsMap.get("employementList");
employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyId"));

PayableDaysMap=[:];
if(UtilValidate.isNotEmpty(employments)){
	List conditionList=[];
	employmentIds = EntityUtil.getFieldListFromEntityList(employments, "partyId", true);
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, employmentIds));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noOfPayableDays", EntityOperator.NOT_EQUAL, BigDecimal.ZERO)));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, (parameters.customTimePeriodId)));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	attendanceDetails = delegator.findList("PayrollAttendance", condition , null, null, null, false);
	employments.each { employment ->
		partyId=employment.get("partyId");
		tempAttendance = EntityUtil.filterByCondition(attendanceDetails,EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		if(UtilValidate.isNotEmpty(tempAttendance)){
			tempAttendance.each { employee ->
				PayableMap=[:];
				employeeFirstName=employment.get("firstName");
				employeeMiddleName=employment.get("middleName");
				employeeLastName=employment.get("lastName");
				PayableDays=employee.get("noOfPayableDays");
				roundedPayableDays=PayableDays.setScale(0,BigDecimal.ROUND_HALF_UP);
				if(roundedPayableDays>PayableDays){
					roundedDiff=roundedPayableDays-PayableDays;
				}else{
					roundedDiff=PayableDays-roundedPayableDays;
				}
				if(roundedDiff>0){
					PayableDays=PayableDays.setScale(4,BigDecimal.ROUND_HALF_UP);
				}
				PayableMap.put("employeeFirstName",employeeFirstName);
				PayableMap.put("employeeMiddleName",employeeMiddleName);
				PayableMap.put("employeeLastName",employeeLastName);
				PayableMap.put("PayableDays",PayableDays);
				PayableDaysMap.put(partyId,PayableMap);
			}
		}
	}
}

context.put("PayableDaysMap",PayableDaysMap);

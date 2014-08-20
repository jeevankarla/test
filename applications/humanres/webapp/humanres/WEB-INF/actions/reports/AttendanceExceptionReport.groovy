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
import org.ofbiz.party.party.PartyHelper;

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
if(UtilValidate.isNotEmpty(EmploymentsMap)){
	employments=EmploymentsMap.get("employementList");
	employments = UtilMisc.sortMaps(employments, UtilMisc.toList("partyId"));
	finalNoPunchDataMap=[:];
	emplLOPFinalMap=[:];
	if(UtilValidate.isNotEmpty(employments)){
		employments.each { employment ->
			partyId=employment.get("partyId");
			List conditionList=[];
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, (parameters.customTimePeriodId)));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noOfPayableDays", EntityOperator.EQUALS, null) ,EntityOperator.OR ,EntityCondition.makeCondition("noOfPayableDays", EntityOperator.EQUALS, BigDecimal.ZERO)));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			attendanceDetails = delegator.findList("PayrollAttendance", condition , null, null, null, false);
			if(UtilValidate.isNotEmpty(attendanceDetails)){
				attendanceDetails.each { employee ->
					attendanceMap=[:];
					empPartyId=employee.get("partyId");
					String partyName = PartyHelper.getPartyName(delegator, employee.get("partyId"), false);
					attendanceMap.put("partyId",empPartyId);
					attendanceMap.put("partyName",partyName);
					finalNoPunchDataMap.put(empPartyId,attendanceMap);
				}
			}
		}
	}
}
context.put("finalNoPunchDataMap",finalNoPunchDataMap);

	
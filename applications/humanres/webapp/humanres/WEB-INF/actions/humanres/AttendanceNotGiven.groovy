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

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (parameters.AttfromDate) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.AttfromDate).getTime()));
	}
	if (parameters.AttthruDate) {
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.AttthruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
	return;
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

conditionList = [];

conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, employementIds));
conditionList.add(EntityCondition.makeCondition("noOfPayableDays", EntityOperator.LESS_THAN_EQUAL_TO, daysCheck));
conditionList.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)
   ], EntityOperator.AND));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
payableDaysList = delegator.findList("PayrollAttendance", condition, null, ["lastUpdatedStamp"], null, false);

conditionList1 = [];
conditionList1.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("noOfHalfPayDays", EntityOperator.NOT_EQUAL, null),
	EntityCondition.makeCondition("noOfHalfPayDays", EntityOperator.NOT_EQUAL, BigDecimal.ZERO)
   ], EntityOperator.AND));
conditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, employementIds));
conditionList1.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)
   ], EntityOperator.AND));
condition1 = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
halfPayDaysList = delegator.findList("PayrollAttendance", condition1, null, ["lastUpdatedStamp"], null, false);

context.put("payableDaysList",payableDaysList);
context.put("halfPayDaysList",halfPayDaysList);


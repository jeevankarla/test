import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

dctx = dispatcher.getDispatchContext();

if (parameters.customTimePeriodId == null) {
	return;
}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.putAt("fromDateStart", fromDateStart);

fromDayBegin = UtilDateTime.getDayStart(fromDateStart);
thruDayEnd = UtilDateTime.getDayEnd(thruDateEnd);
fromDateBeginStr = UtilDateTime.toDateString(fromDayBegin);
thruDateEndStr = UtilDateTime.toDateString(thruDayEnd);
List conditionList=[];
conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO,  UtilDateTime.toSqlDate(fromDateBeginStr)));
conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO,  UtilDateTime.toSqlDate(thruDateEndStr)));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
def orderBy = UtilMisc.toList("partyId","date");
attendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition , null, orderBy, null, false);

context.put("attendanceDetailList",attendanceDetailList);



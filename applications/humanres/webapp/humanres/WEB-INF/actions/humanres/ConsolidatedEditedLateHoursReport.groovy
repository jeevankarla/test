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
context.putAt("thruDateEnd", thruDateEnd);

fromDayBegin = UtilDateTime.getDayStart(fromDateStart);
thruDayEnd = UtilDateTime.getDayEnd(thruDateEnd);

List consolidateList=[];
consolidateList.add(EntityCondition.makeCondition("changedDate", EntityOperator.GREATER_THAN_EQUAL_TO,  fromDayBegin));
consolidateList.add(EntityCondition.makeCondition("changedDate", EntityOperator.LESS_THAN_EQUAL_TO,  thruDayEnd));
consolidateList.add(EntityCondition.makeCondition("changedEntityName", EntityOperator.EQUALS, "PayrollAttendance"));
consolidateList.add(EntityCondition.makeCondition("changedFieldName", EntityOperator.EQUALS, "lateMin"));
consolidateCondition=EntityCondition.makeCondition(consolidateList,EntityOperator.AND);
def orderBy1 = UtilMisc.toList("changedDate","auditHistorySeqId");
aduitLogDetailList = delegator.findList("EntityAuditLog", consolidateCondition , null, orderBy1, null, false);

context.put("aduitLogDetailList",aduitLogDetailList);

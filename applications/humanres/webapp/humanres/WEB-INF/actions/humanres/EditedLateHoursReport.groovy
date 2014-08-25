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
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
		fromDateStart = new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime());
	} 
	catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+parameters.fromDate, "");
	}
context.putAt("fromDateStart", fromDateStart);

dayBegin = UtilDateTime.getDayStart(fromDateStart);
dayEnd = UtilDateTime.getDayEnd(fromDateStart);
dayBeginStr = UtilDateTime.toDateString(dayBegin);
dayEndStr = UtilDateTime.toDateString(dayEnd);

FinalMap=[:];
List conditionList=[];
conditionList.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO,  UtilDateTime.toSqlDate(dayBeginStr)));
conditionList.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO,  UtilDateTime.toSqlDate(dayEndStr)));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
def orderBy = UtilMisc.toList("partyId");
attendanceDetailList = delegator.findList("EmplDailyAttendanceDetail", condition , null, orderBy, null, false);
if(UtilValidate.isNotEmpty(attendanceDetailList)){
	attendanceDetailList.each { attendenceDetails ->
		empAttendenceMap= [:];
		partyId = attendenceDetails.get("partyId");
		date=attendenceDetails.get("date");
		lateMin=attendenceDetails.get("lateMin");
		overrideLateMin=attendenceDetails.get("overrideLateMin");
		editedBy=attendenceDetails.get("overridenBy");
		editedDate=attendenceDetails.get("lastUpdatedStamp");
		empAttendenceMap.put("date",date);
		empAttendenceMap.put("lateMin",lateMin);
		empAttendenceMap.put("overrideLateMin",overrideLateMin);
		empAttendenceMap.put("editedBy",editedBy);
		empAttendenceMap.put("editedDate",editedDate);
		FinalMap.put(partyId,empAttendenceMap);
	}
}
context.put("FinalMap",FinalMap);



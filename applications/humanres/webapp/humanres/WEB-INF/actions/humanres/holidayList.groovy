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

holidyDate = parameters.holidayDate;
organizaionId = parameters.orgPartyId;
customTimePeriodId = parameters.customTimePeriodId;

dateStr = null;
holidayDatestart = null;
def sdf1 = new SimpleDateFormat("dd-MM-yyyy");
if(UtilValidate.isNotEmpty(holidyDate)){
	try {
		if (holidyDate) {
			dateTimestamp = new java.sql.Timestamp(sdf1.parse(holidyDate).getTime());
			dateStr = UtilDateTime.toDateString(dateTimestamp,"yyyy-MM-dd");
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	def sdf = new SimpleDateFormat("yyyy-MM-dd");
	holidayDatestart = new java.sql.Timestamp(sdf.parse(dateStr).getTime());
}


//holidayDatestart = UtilDateTime.getDayStart(UtilDateTime.toTimestamp(dateStr));
//holidayDatestart = UtilDateTime.toTimestamp(dateStr);
holidayDetails = [];
if(UtilValidate.isNotEmpty(customTimePeriodId)){
	conditionList = [];
	if(UtilValidate.isNotEmpty(customTimePeriodId)){
		conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId ));
	}
	if(UtilValidate.isNotEmpty(organizaionId)){
		conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizaionId ));
	}
	if(UtilValidate.isNotEmpty(holidayDatestart)){
		conditionList.add(EntityCondition.makeCondition("holiDayDate", EntityOperator.EQUALS,holidayDatestart));
	}
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	//holidayList = [];
	holidayDetails = delegator.findList("HolidayCalendar", condition , null, null, null, false );
}
context.put("holidayList",holidayDetails);
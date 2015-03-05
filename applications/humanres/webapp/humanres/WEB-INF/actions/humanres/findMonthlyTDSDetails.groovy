import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;

JSONArray headItemsJson = new JSONArray();
JSONObject newObj = new JSONObject();

timePeriodId = "";
yearMonthDate = "";
if(UtilValidate.isNotEmpty(parameters.timePeriodId)){
	timePeriodId = parameters.timePeriodId;
}
if(UtilValidate.isNotEmpty(parameters.yearMonthDate)){
	yearMonthDate = parameters.yearMonthDate;
}
if(UtilValidate.isNotEmpty(parameters.tdsRemittanceDetailsList)){
	tdsRemittanceDetailsList = parameters.tdsRemittanceDetailsList;
	tdsRemittanceDetailsList.each { tdsDetails->
		BSRcode = tdsDetails.get("BSRcode");
		challanNumber = tdsDetails.get("challanNumber");
		newObj.put("id",timePeriodId+"["+timePeriodId+"]");
		newObj.put("timePeriodId",timePeriodId);
		newObj.put("BSRcode",BSRcode);
		newObj.put("challanNumber",challanNumber);
		headItemsJson.add(newObj);
	}
}

Timestamp fromDateStart = null;
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : timePeriodId], false);
if(UtilValidate.isNotEmpty(customTimePeriod)){
	fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
}
context.headItemsJson=headItemsJson;
context.yearMonthDate=fromDateStart;

JSONArray headInputItemsJson = new JSONArray();
JSONObject newObj1 = new JSONObject();

actualDate = null;
Timestamp challanDate = null;
Timestamp monthStartDate = null;
Timestamp monthEndDate = null;
def sdf = new SimpleDateFormat("MMM-yyyy");
try {
	actualDate = new java.sql.Timestamp(sdf.parse(yearMonthDate+" 00:00:00").getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + actualDate, "");
}
if(UtilValidate.isNotEmpty(actualDate)){
	monthStartDate = UtilDateTime.getMonthStart(actualDate);
	monthEndDate = UtilDateTime.getMonthEnd(monthStartDate, timeZone, locale);
	List condPeriodList = FastList.newInstance();
	condPeriodList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
	condPeriodList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthEndDate)));
	condPeriodList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(monthStartDate)));
	EntityCondition periodCond = EntityCondition.makeCondition(condPeriodList,EntityOperator.AND);
	hrCustomTimePeriodList = delegator.findList("CustomTimePeriod", periodCond, null, null, null, false);
	if(UtilValidate.isNotEmpty(hrCustomTimePeriodList)){
		monthsList = EntityUtil.getFirst(hrCustomTimePeriodList);
		customTimePeriodId = monthsList.get("customTimePeriodId");
		List ConditionList1=[];
		ConditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
		ConditionList1.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
		Condition1 =EntityCondition.makeCondition(ConditionList1,EntityOperator.AND);
		tdsRemittanceList = delegator.findList("TDSRemittances", Condition1 , null, null, null, false );
		if(UtilValidate.isNotEmpty(tdsRemittanceList)){
			tdsRemittanceList = EntityUtil.getFirst(tdsRemittanceList);
			BSRcodeNo = tdsRemittanceList.get("BSRcode");
			challanNo = tdsRemittanceList.get("challanNumber");
			newObj1.put("id",customTimePeriodId+"["+customTimePeriodId+"]");
			newObj1.put("BSRcode",BSRcodeNo);
			newObj1.put("challanNumber",challanNo);
			headInputItemsJson.add(newObj1);
		}
	}
}

context.headInputItemsJson=headInputItemsJson;


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

def sdf = new SimpleDateFormat("yyyy-MM-dd");

periodTypeId = parameters.periodTypeId;
customTimePeriodId = parameters.customTimePeriodId;
periodName = parameters.periodName;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

if(UtilValidate.isNotEmpty(fromDate)){
	fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
}
if(UtilValidate.isNotEmpty(thruDate)){
	thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
}

customTimePeriodList = [];
if(UtilValidate.isNotEmpty(periodTypeId)){
	//customTimePeriodDetails = delegator.findOne("CustomTimePeriod", [customTimePeriodId : customTimePeriodId], false);
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId));
	if(UtilValidate.isNotEmpty(fromDate)){
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toSqlDate(fromDate)));
	}
	if(UtilValidate.isNotEmpty(thruDate)){
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(thruDate)));
	}
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	customTimePeriodList = delegator.findList("CustomTimePeriod", condition, null, null, null, true);
}
context.put("timePeriodList",customTimePeriodList);
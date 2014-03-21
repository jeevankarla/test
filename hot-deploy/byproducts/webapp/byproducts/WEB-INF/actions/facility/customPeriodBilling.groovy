import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;


dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
periodTypeList = ["INST_FORTNIGHT_BILL", "INST_MONTH_BILL", "INST_QUARTER_BILL", "INST_WEEK_BILL"];

facilityBillingConfig = delegator.findList("FacilityCustomBilling", EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeList), null, null, null, false);
facilityBillingConfig = EntityUtil.filterByDate(facilityBillingConfig, dayBegin);
periodFacilityMap = [:];
JSONObject periodFacilityJSON = new JSONObject();

for(String type : periodTypeList){
	periodAssoFacility = EntityUtil.filterByCondition(facilityBillingConfig, EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, type));
	periodAssoFacilityIds = EntityUtil.getFieldListFromEntityList(periodAssoFacility, "facilityId", true);
	//periodFacilityMap.put(type, periodAssoFacilityIds);
	periodFacilityJSON.putAt(type, periodAssoFacilityIds) ;
}
context.periodFacilityJSON = periodFacilityJSON;
condList = [];
condList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "CR_INST_BILLING"));
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
periodBillings = delegator.findList("PeriodBilling", cond, null, null, null, true);

billingCustTimePeriodIds = EntityUtil.getFieldListFromEntityList(periodBillings, "customTimePeriodId", true);

condList.clear();
condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeList));
condList.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toSqlDate(dayBegin)));
/*if(billingCustTimePeriodIds){
	condList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.NOT_IN, billingCustTimePeriodIds));
}*/
condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
customTimePeriods = delegator.findList("CustomTimePeriod", condition, null, null, null, true);
JSONObject periodCustomTimeJSON = new JSONObject();
JSONObject customTimePeriodLabelJSON = new JSONObject();
periodCustomTime = [:];
periodTypeList.each{ eachType ->
	periodCustomTimePeriod = EntityUtil.filterByCondition(customTimePeriods, EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, eachType));
	periodCustomTimePeriod = EntityUtil.orderBy(periodCustomTimePeriod, ["-lastUpdatedStamp"])
	periodCustomTimePeriod.each{custTimePeriod  ->
		customTimePeriodLabelJSON.putAt(custTimePeriod.customTimePeriodId, " "+custTimePeriod.fromDate+" :: "+custTimePeriod.thruDate);
	}
	periodIds = EntityUtil.getFieldListFromEntityList(periodCustomTimePeriod, "customTimePeriodId", true);
	periodCustomTimeJSON.putAt(eachType, periodIds);
}
context.periodCustomTimeJSON = periodCustomTimeJSON;
context.customTimePeriodLabelJSON = customTimePeriodLabelJSON;
//context.customTimePeriods = customTimePeriods;

/*
 * Listing customTimePeriods
*/
condList.clear();
condList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeList));
if(billingCustTimePeriodIds){
	condList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, billingCustTimePeriodIds));
}
condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
generatedCustomTimePeriods = delegator.findList("CustomTimePeriod", condition, null, ["-lastUpdatedStamp"], null, true);

generatedPeriods = [];
generatedCustomTimePeriods.each{ eachItem ->
	
	tempMap = [:];
	tempMap.customTimePeriodId = eachItem.customTimePeriodId;
	tempMap.periodName = eachItem.periodName;
	tempMap.periodTypeId = eachItem.periodTypeId;
	generatedPeriods.add(tempMap);
}
context.generatedPeriods = generatedPeriods;


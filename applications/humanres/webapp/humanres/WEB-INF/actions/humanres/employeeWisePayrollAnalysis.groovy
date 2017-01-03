import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;


List stautsList = UtilMisc.toList("GENERATED","APPROVED");
conditionList=[];
conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS ,"PAYROLL_BILL"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN ,stautsList));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
periodBillingList = delegator.findList("PeriodBilling", condition, null, null, null, false);

periodIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "customTimePeriodId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, periodIds));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
customTimePeriods = delegator.findList("CustomTimePeriod", condition, null, ["-thruDate"], null, true);
context.customTimePeriods = customTimePeriods;
Debug.logError("customTimePeriods="+customTimePeriods,"");

condList = [];
condList.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS ,"PARTY_GROUP"));
condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS ,"Company"));
cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
ROList = delegator.findList("PartyRelationshipAndDetail", cond, null, null, null, false);
context.ROList = ROList;
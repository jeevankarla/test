import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
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
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;


Map updateFatSnfMap =FastMap.newInstance();
List qtySnfFinalList = FastList.newInstance();
List qtySnfFatFinalList =FastList.newInstance();
conditionList =[];
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS ,parameters.orderItemSeqId)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS ,"ORDER_CREATED")));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS ,"PURCHASE_ORDER")));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
qtySnfFinalList = delegator.findList("OrderHeaderItemProductAndFacility",condition,null,null,null,false);
snfFatList =EntityUtil.getFirst(qtySnfFinalList);
tempMap =[:];
tempMap.putAll(snfFatList);
updateFatSnfMap.putAll(tempMap);

conditionList.clear();

conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("shedId", EntityOperator.EQUALS, parameters.unitId)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,parameters.customTimePeriodId)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("validationTypeId", EntityOperator.EQUALS ,parameters.validationTypeId)));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS ,parameters.sequenceNum)));
condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
procValidationEntry = delegator.findList("ProcBillingValidation",condition1,null,null,null,false);
billingValidationlist =EntityUtil.getFirst(procValidationEntry);
updateFatSnfMap.putAll(billingValidationlist);
qtySnfFatFinalList.add(updateFatSnfMap);
context.putAt("updateFatSnfMap", updateFatSnfMap);
context.putAt("qtySnfFatFinalList", qtySnfFatFinalList);




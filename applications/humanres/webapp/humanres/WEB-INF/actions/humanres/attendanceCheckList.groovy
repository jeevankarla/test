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

selectDate = UtilDateTime.nowTimestamp();
context.selectDate=selectDate;
dctx = dispatcher.getDispatchContext();
context.dctx=dctx;
allChanges= false;
if (parameters.all == 'Y') {
	allChanges = true;
}

dayBegin = UtilDateTime.getDayStart(selectDate, timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(selectDate, timeZone, locale);

BigDecimal daysCheck = new BigDecimal("10");

conditionList = [];
if(!allChanges){
	conditionList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
}
EntityCondition.makeCondition("noOfPayableDays", EntityOperator.LESS_THAN_EQUAL_TO, daysCheck);
conditionList.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)
   ], EntityOperator.AND));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
payableDaysList = delegator.findList("PayrollAttendance", condition, null, ["lastUpdatedStamp"], null, false);

conditionList1 = [];
if(!allChanges){
	conditionList1.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS , userLogin.userLoginId));
}
conditionList1.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("noOfHalfPayDays", EntityOperator.NOT_EQUAL, null),
	EntityCondition.makeCondition("noOfHalfPayDays", EntityOperator.NOT_EQUAL, BigDecimal.ZERO)
   ], EntityOperator.AND));
conditionList1.add(EntityCondition.makeCondition([
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)
   ], EntityOperator.AND));
condition1 = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
halfPayDaysList = delegator.findList("PayrollAttendance", condition1, null, ["lastUpdatedStamp"], null, false);

context.put("payableDaysList",payableDaysList);
context.put("halfPayDaysList",halfPayDaysList);
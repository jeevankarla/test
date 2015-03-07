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

import java.util.Calendar;
import org.ofbiz.base.util.UtilNumber;

customTimePeriodId = parameters.customTimePeriodId;

timePeriodList = [];
condPeriodList = [];
condPeriodList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"FISCAL_QUARTER"));
condPeriodList.add(EntityCondition.makeCondition("parentPeriodId", EntityOperator.EQUALS, customTimePeriodId));
EntityCondition periodCond = EntityCondition.makeCondition(condPeriodList,EntityOperator.AND);
def orderBy = UtilMisc.toList("fromDate");
CustomTimePeriodList = delegator.findList("CustomTimePeriod", periodCond, null, orderBy, null, false);
if(UtilValidate.isNotEmpty(CustomTimePeriodList)){
	CustomTimePeriodList.each{period ->
		timePeriodId = period.get("customTimePeriodId");
		timePeriodList.add(timePeriodId);
	}
}
request.setAttribute("timePeriodList", timePeriodList);

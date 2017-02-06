import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.inout.PunchService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import javax.swing.RowFilter.NotFilter;

import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.humanres.HumanresHelperServices;
import org.ofbiz.party.party.PartyHelper;



creditLeavesPeriodList = [];
conditionList = [];
conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
EntityCondition condition= EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List<GenericValue> hrPeriodList = delegator.findList("CustomTimePeriod", condition, null,["fromDate"], null, false);

if(UtilValidate.isNotEmpty(hrPeriodList)){
	for(i=0; i<hrPeriodList.size(); i++){
		hrPeriod = hrPeriodList.get(i);
		fromDate = hrPeriod.get("fromDate");
		fromDateStart=UtilDateTime.toTimestamp(fromDate);
		month = UtilDateTime.toDateString(fromDateStart,"MM");
		if(month.equals("01") || month.equals("07")){
			creditLeavesPeriodList.add(hrPeriod);
		}
	}
}

context.put("creditLeavesPeriodList", creditLeavesPeriodList);
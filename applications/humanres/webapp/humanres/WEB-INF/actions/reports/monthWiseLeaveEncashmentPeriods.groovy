import org.apache.derby.impl.sql.compile.OrderByColumn;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;

import freemarker.core.SequenceBuiltins.sort_byBI;
import in.vasista.vbiz.humanres.EmplLeaveService;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresApiService;
import in.vasista.vbiz.humanres.HumanresService;
import org.ofbiz.party.party.PartyHelper;
import javolution.util.FastList;
import javolution.util.FastMap;

dctx = dispatcher.getDispatchContext();
SupplyCustomTimePeriodList = [];
condList=[];
condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_QUARTER"), EntityOperator.OR,
	EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "HR_LEAVEENCASH")));
cond=EntityCondition.makeCondition(condList,EntityOperator.AND);
periodIdsList=delegator.findList("CustomTimePeriod",cond,null,null,null,false);
if(UtilValidate.isNotEmpty(periodIdsList)){
	periodIdsList.each { period ->
		customTImePeriodId = period.get("customTimePeriodId");
		conList=[];
		conList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,customTImePeriodId));
		conList.add(EntityCondition.makeCondition("billingTypeId",EntityOperator.EQUALS,"SP_LEAVE_ENCASH"));
		conList.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,UtilMisc.toList("GENERATED","APPROVED")));
		con=EntityCondition.makeCondition(conList,EntityOperator.AND);
		periodBillingList=delegator.findList("PeriodBilling",con,null,["basicSalDate"],null,false);
		if(UtilValidate.isNotEmpty(periodBillingList)){
			periodBillingList.each { periodBillng ->
				periodDetailsMap = [:];
				basicSalDate=UtilDateTime.toTimestamp(periodBillng.get("basicSalDate"));
				Timestamp monthStart=UtilDateTime.getMonthStart(basicSalDate);
				monthEnd = UtilDateTime.getMonthEnd(monthStart, timeZone, locale);
				periodDetailsMap.put("fromDate", monthStart);
				periodDetailsMap.put("thruDate", monthEnd);
				periodDetailsMap.put("periodBillingId", periodBillng.get("periodBillingId"));
				SupplyCustomTimePeriodList.addAll(periodDetailsMap);
			}
		}
	}
}
context.put("SupplyCustomTimePeriodList", SupplyCustomTimePeriodList);

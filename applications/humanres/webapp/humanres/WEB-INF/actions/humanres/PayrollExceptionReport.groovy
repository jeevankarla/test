import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
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
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.humanres.PayrollService;
import org.ofbiz.party.party.PartyHelper;
timePeriodId = null;
if (parameters.customTimePeriodId == null) {
	return;
}
dctx = dispatcher.getDispatchContext();
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.putAt("fromDateStart", fromDateStart);
fromDayBegin = UtilDateTime.getDayStart(fromDateStart);
thruDayEnd = UtilDateTime.getDayEnd(thruDateEnd);

resultMap = PayrollService.getPayrollAttedancePeriod(dctx, [timePeriodStart:fromDayBegin, timePeriodEnd: thruDayEnd, timePeriodId: parameters.customTimePeriodId, userLogin : userLogin]);
if(UtilValidate.isNotEmpty(resultMap.get("lastCloseAttedancePeriod"))){
	lastCloseAttedancePeriod=resultMap.get("lastCloseAttedancePeriod");
	timePeriodId=lastCloseAttedancePeriod.get("customTimePeriodId");
}
Map emplInputMap = FastMap.newInstance();
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", fromDayBegin);
emplInputMap.put("thruDate", thruDayEnd);

if(UtilValidate.isNotEmpty(parameters.partyId)){
	emplInputMap.put("orgPartyId", parameters.partyId);
}else{
	emplInputMap.put("orgPartyId", "Company");
}

Map resultMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)resultMap.get("employementList");
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);

payrollDetailsMap=[:];
if(UtilValidate.isNotEmpty(timePeriodId)){
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employementIds));
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,timePeriodId));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	payrollDetailsList = delegator.findList("PayrollAttendance", condition, null, null, null, false);
	if(UtilValidate.isNotEmpty(payrollDetailsList)){
		payrollDetailsList.each { payrollDetails ->
			noOfPayableDays=payrollDetails.get("noOfPayableDays");
			if(noOfPayableDays==0){
				payrollDaysMap= [:];
				partyId=payrollDetails.get("partyId");
				payableDays = 0;
				partyDetails = delegator.findOne("Person",[ partyId : partyId ], false);
				String partyName = partyDetails.get("nickname");
				if(UtilValidate.isEmpty(partyName)){
					partyName = PartyHelper.getPartyName(delegator, partyId, false);
				}
				//String partyName = PartyHelper.getPartyName(delegator, partyId, false);
				payrollDaysMap.put(partyName,payableDays);
				payrollDetailsMap.put(partyId,payrollDaysMap);
			}
		}
	}
}
context.put("payrollDetailsMap",payrollDetailsMap);


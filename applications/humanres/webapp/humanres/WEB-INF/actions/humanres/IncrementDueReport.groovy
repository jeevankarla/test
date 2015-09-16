import org.apache.derby.impl.sql.compile.OrderByColumn;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.HumanresService;
 
 dctx = dispatcher.getDispatchContext();
	
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("fromDate",fromDateStart);
context.put("thruDate",thruDateEnd);
prevMonthFromDateStart = UtilDateTime.getMonthStart(fromDateStart);
prevMonthThruDate=UtilDateTime.getMonthEnd(prevMonthFromDateStart, timeZone, locale);
curreentYearStart = UtilDateTime.getYearStart(UtilDateTime.addDaysToTimestamp(fromDateStart, -1));
previousYearEnd = UtilDateTime.addDaysToTimestamp(curreentYearStart, -1);
String year = UtilDateTime.toDateString(previousYearEnd,"yyyy");
String currMonth = UtilDateTime.toDateString(prevMonthFromDateStart,"-MM-dd");
String prevMonth = UtilDateTime.toDateString(prevMonthThruDate,"-MM-dd");

String reqFromDate = year+currMonth;
String reqThruDate = year+prevMonth;
def sdf = new SimpleDateFormat("yyyy-MM-dd");
timePeriodStart = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(reqFromDate).getTime()));
timePeriodEnd = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(reqThruDate).getTime()));
finalMap = [:];
Map emplInputMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(parameters.partyId)){
	emplInputMap.put("orgPartyId", parameters.partyId);
}else{
	emplInputMap.put("orgPartyId", "Company");
}
emplInputMap.put("userLogin", userLogin);
emplInputMap.put("fromDate", fromDateStart);
emplInputMap.put("thruDate", thruDateEnd);
Map EmploymentsMap = HumanresService.getActiveEmployements(dctx,emplInputMap);
List<GenericValue> employementList = (List<GenericValue>)EmploymentsMap.get("employementList");
employementList = EntityUtil.orderBy(employementList, UtilMisc.toList("partyIdTo"));
employementIds = EntityUtil.getFieldListFromEntityList(employementList, "partyIdTo", true);
empDetailMap = [:];
employementIds.each{ employee->
	payConditionList=[];
	payConditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,employee));
	payConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS ,timePeriodStart));
	payConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	payCondition = EntityCondition.makeCondition(payConditionList,EntityOperator.AND);
	incrementDateMap = [:];
	incrementDate = "";
	PayGradeHistory = delegator.findList("PayHistory", payCondition, null, null, null, false);
	if(UtilValidate.isNotEmpty(PayGradeHistory)){
		PayGradeHistory.each{ PayGradeHis->
			designation = "";
			String incrementDate = UtilDateTime.toDateString(fromDateStart,"dd-MM-yyyy");
			emplPositionAndFulfillments = EntityUtil.filterByDate(delegator.findByAnd("EmplPositionAndFulfillment", ["employeePartyId" : employee]));
			if(UtilValidate.isNotEmpty(emplPositionAndFulfillments)){
				emplPositionAndFulfillment = EntityUtil.getFirst(emplPositionAndFulfillments);
				if(UtilValidate.isNotEmpty(emplPositionAndFulfillment) && emplPositionAndFulfillment.getString("emplPositionTypeId") != null){
					emplPositionType = delegator.findOne("EmplPositionType",[emplPositionTypeId : emplPositionAndFulfillment.getString("emplPositionTypeId")], true);
					if(UtilValidate.isNotEmpty(emplPositionType)){
						designation = emplPositionType.getString("description");
					}
				}
			}
			String partyName = PartyHelper.getPartyName(delegator, employee, false);
			incrementDateMap.put("partyName", partyName);
			incrementDateMap.put("incrementDate",incrementDate);
			incrementDateMap.put("designation", designation);
		}
	}
	if(UtilValidate.isNotEmpty(incrementDateMap)){
		finalMap.put(employee,incrementDateMap);
	}
}
context.finalMap=finalMap;
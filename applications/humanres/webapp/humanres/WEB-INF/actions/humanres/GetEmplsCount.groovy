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

dctx = dispatcher.getDispatchContext();
orgId="";
partyId=parameters.partyId;
TimePeriodId=parameters.customTimePeriodId;
noOfAttendedDays=parameters.noOfAttendedDays;
deptId=parameters.deptId;
timePeriodId=parameters.timePeriodId;
if(partyId!=deptId){
	orgId=deptId;
}else{
departmentDetails=delegator.findByAnd("Employment", [partyIdTo : partyId,thruDate:null]);
partyId="";
if(departmentDetails){
	orgId=departmentDetails[0].partyIdFrom;
}

}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : TimePeriodId], false);
if (UtilValidate.isEmpty(customTimePeriod)) {
	return;
}
timePeriodStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
timePeriodEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));


Map InputMap = FastMap.newInstance();
InputMap.put("userLogin", userLogin);
InputMap.put("orgPartyId", orgId);
InputMap.put("fromDate", timePeriodStart);
InputMap.put("thruDate", timePeriodEnd);
Map resultInputMap = HumanresService.getActiveEmployements(dctx,InputMap);
List<GenericValue> employeesList = (List<GenericValue>)resultInputMap.get("employementList");
employeesList = EntityUtil.orderBy(employeesList, UtilMisc.toList("partyIdTo"));
employeeIds = EntityUtil.getFieldListFromEntityList(employeesList, "partyIdTo", true);
context.totalEmpls=employeeIds.size();
customMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:timePeriodStart,timePeriodEnd:timePeriodEnd,timePeriodId:timePeriodId,locale:locale]);
lastClosePeriod=customMap.get("lastCloseAttedancePeriod");
if(UtilValidate.isNotEmpty(lastClosePeriod))
customTimePeriodId=lastClosePeriod.get("customTimePeriodId");

conList=[];
conList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,customTimePeriodId));
conList.add(EntityCondition.makeCondition("partyId",EntityOperator.IN,employeeIds));
conList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noOfAttendedDays",EntityOperator.EQUALS,null),EntityOperator.OR,
	EntityCondition.makeCondition("noOfAttendedDays",EntityOperator.GREATER_THAN,BigDecimal.ZERO)));
EntityCondition con=EntityCondition.makeCondition(conList,EntityOperator.AND);
List<GenericValue> payrollCountList=delegator.findList("PayrollAttendance",con,UtilMisc.toSet("partyId"),null,null,false);
context.enteredEmpls=payrollCountList.size();
remainingEmpls=0;
remainingEmpls=employeeIds.size()-payrollCountList.size();
request.setAttribute("totalEmpls",employeeIds.size());
request.setAttribute("enteredEmpls",payrollCountList.size());
request.setAttribute("remainingEmpls",remainingEmpls);
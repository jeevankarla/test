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
attendanceExceptionMap=[:];
if(UtilValidate.isNotEmpty(timePeriodId)){
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN , employementIds));
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS ,timePeriodId));
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("noOfPayableDays", EntityOperator.EQUALS, null) ,EntityOperator.OR ,EntityCondition.makeCondition("noOfPayableDays", EntityOperator.EQUALS, BigDecimal.ZERO)));
	/*conditionList.add(EntityCondition.makeCondition([
		EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin),
		EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd)
	   ], EntityOperator.AND));*/
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	attendanceDetailsList = delegator.findList("PayrollAttendance", condition, null, null, null, false);
	if(UtilValidate.isNotEmpty(attendanceDetailsList)){
		attendanceDetailsList.each { attendanceDetails ->
			detailsMap = [:];
			GISNo = null;
			partyId=attendanceDetails.get("partyId");
			partyDetails = delegator.findOne("Person",[ partyId : partyId ], false);
			partyName = partyDetails.get("nickname");
			employeeDetails = delegator.findOne("EmployeeDetail", [partyId : partyId], false);
			if(UtilValidate.isNotEmpty(employeeDetails)){
				GISNo = employeeDetails.get("presentEpf");
				detailsMap.put("GISNo",GISNo);
			}
			unitDetails = delegator.findList("Employment", EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , partyId), null, null, null, false);
			if(UtilValidate.isNotEmpty(unitDetails)){
				unitDetails = EntityUtil.getFirst(unitDetails);
				if(UtilValidate.isNotEmpty(unitDetails))	{
					locationGeoId=unitDetails.get("locationGeoId");
					detailsMap.put("unit",locationGeoId);
				}
			}
			partyRelationconditionList=[];
			partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS , "DEPARTMENT"));
			partyRelationconditionList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
			partyRelationconditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS ,partyId));
			partyCondition = EntityCondition.makeCondition(partyRelationconditionList,EntityOperator.AND);
			def orderBy = UtilMisc.toList("comments");
			partyRelationList = delegator.findList("PartyRelationship", partyCondition, null, orderBy, null, false);
			if(UtilValidate.isNotEmpty(partyRelationList)){
				costDetails = EntityUtil.getFirst(partyRelationList);
				if(UtilValidate.isNotEmpty(costDetails))	{
					costCode=costDetails.get("comments");
					detailsMap.put("costCode",costCode);
				}
			}
			detailsMap.put("partyId",partyId);
			detailsMap.put("partyName",partyName);
			attendanceExceptionMap.put(partyId,detailsMap);
		}
	}
}

context.put("attendanceExceptionMap",attendanceExceptionMap);

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.humanres.PayrollService;

dctx = dispatcher.getDispatchContext();

if (parameters.customTimePeriodId == null) {
	return;
}
GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : parameters.customTimePeriodId], false);
fromDateStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.putAt("thruDateEnd", thruDateEnd);

resultMap = [:];
resultMap=PayrollService.getPayrollAttedancePeriod(dctx,[userLogin:userLogin,timePeriodStart:fromDateStart,timePeriodEnd:thruDateEnd,timePeriodId:parameters.customTimePeriodId,locale:locale]);
lastClosePeriod=resultMap.get("lastCloseAttedancePeriod");
if(UtilValidate.isNotEmpty(lastClosePeriod)){
	customTimePeriodId=lastClosePeriod.get("customTimePeriodId");
}

consolidatedFinalMap = [:];
List conditionList=[];
conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
attendanceDetails = delegator.findList("PayrollAttendance", condition , null, null, null, false);
if(UtilValidate.isNotEmpty(attendanceDetails)){
	attendanceDetails.each { employee ->
		attendanceMap=[:];
		empPartyId=employee.get("partyId");
		appendedPartyId = empPartyId + "::" + parameters.customTimePeriodId;
		List consolidateList=[];
		consolidateList.add(EntityCondition.makeCondition("pkCombinedValueText", EntityOperator.EQUALS, appendedPartyId));
		consolidateList.add(EntityCondition.makeCondition("changedEntityName", EntityOperator.EQUALS, "PayrollAttendance"));
		consolidateList.add(EntityCondition.makeCondition("changedFieldName", EntityOperator.EQUALS, "lateMin"));
		consolidateCondition=EntityCondition.makeCondition(consolidateList,EntityOperator.AND);
		def orderBy1 = UtilMisc.toList("auditHistorySeqId");
		consolidatedDetailsList = delegator.findList("EntityAuditLog", consolidateCondition , null, orderBy1, null, false);
		consolidatedDetailsMap = [:];
		if(UtilValidate.isNotEmpty(consolidatedDetailsList)){
			consolidatedDetailsList.each { consolidatedDetails ->
				consolidatedMap=[:];
				if(UtilValidate.isNotEmpty(consolidatedDetails.get("newValueText"))){
					consolidatedMap.put("oldValueText",consolidatedDetails.get("oldValueText"));
					consolidatedMap.put("newValueText",consolidatedDetails.get("newValueText"));
					consolidatedMap.put("changedDate",consolidatedDetails.get("changedDate"));
					consolidatedMap.put("changedByInfo",consolidatedDetails.get("changedByInfo"));
					consolidatedDetailsMap.putAll(consolidatedMap);
				}
			}
		}
		if(UtilValidate.isNotEmpty(consolidatedDetailsMap)){
			consolidatedFinalMap.put(empPartyId,consolidatedDetailsMap);
		}
	}
}
context.put("consolidatedFinalMap",consolidatedFinalMap);

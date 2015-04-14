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
attendanceDetails = delegator.findList("PayrollAttendance", condition , null, UtilMisc.toList("partyId"), null, false);
if(UtilValidate.isNotEmpty(attendanceDetails)){
	attendanceDetails.each { employee ->
		empPartyId=employee.get("partyId");
		remarks = employee.get("remarks");
		appendedPartyId = empPartyId + "::" + parameters.customTimePeriodId;
		List consolidateList=[];
		consolidateList.add(EntityCondition.makeCondition("pkCombinedValueText", EntityOperator.EQUALS, appendedPartyId));
		consolidateList.add(EntityCondition.makeCondition("changedEntityName", EntityOperator.EQUALS, "PayrollAttendance"));
		consolidateList.add(EntityCondition.makeCondition("changedFieldName", EntityOperator.EQUALS, "noOfPayableDays"));
		consolidateCondition=EntityCondition.makeCondition(consolidateList,EntityOperator.AND);
		def orderBy1 = UtilMisc.toList("auditHistorySeqId");
		consolidatedDetailsList = delegator.findList("EntityAuditLog", consolidateCondition , null, orderBy1, null, false);
		consolidatedFinalList=[];
		if(UtilValidate.isNotEmpty(consolidatedDetailsList)){
			consolidatedDetailsList.each { consolidatedDetails ->
				consolidatedMap=[:];
				if(UtilValidate.isNotEmpty(consolidatedDetails.get("newValueText"))){
					if(UtilValidate.isNotEmpty(consolidatedDetails.get("oldValueText"))){
						consolidatedMap.put("oldValueText",new BigDecimal(consolidatedDetails.get("oldValueText")));
					}
					if(UtilValidate.isNotEmpty(consolidatedDetails.get("newValueText"))){
						consolidatedMap.put("newValueText",new BigDecimal(consolidatedDetails.get("newValueText")));
					}
					if(UtilValidate.isNotEmpty(consolidatedDetails.get("changedDate"))){
						consolidatedMap.put("changedDate",consolidatedDetails.get("changedDate"));
					}
					if(UtilValidate.isNotEmpty(consolidatedDetails.get("changedByInfo"))){
						consolidatedMap.put("changedByInfo",consolidatedDetails.get("changedByInfo"));
					}
					enumeration = delegator.findOne("Enumeration",[enumId:remarks],false);
					if(UtilValidate.isNotEmpty(enumeration)){
						consolidatedMap.put("remarks", enumeration.get("description"));
					}
					consolidatedFinalList.addAll(consolidatedMap);
				}
			}
		}
		consolidatedFinalList = UtilMisc.sortMaps(consolidatedFinalList, UtilMisc.toList("changedDate"));
		if(UtilValidate.isNotEmpty(consolidatedFinalList)){
			consolidatedFinalMap.put(empPartyId,consolidatedFinalList);
		}
	}
}
context.put("consolidatedFinalMap",consolidatedFinalMap);

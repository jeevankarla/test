
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;

dctx = dispatcher.getDispatchContext();
orgList=[];
def populateChildren(org, employeeList) {
	internalOrgs = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", [partyIdFrom : org.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"],["partyIdFrom","groupName"]));
	internalOrgs.each { internalOrg ->
		populateChildren(internalOrg, employeeList);
	}
		
	
	orgList.add(org);
}
customTimePeriodList=[];
customTimePeriodList=delegator.findByAnd("CustomTimePeriod",[periodTypeId:"HR_MONTH"]);
employeeList = [];
internalOrgs=[];
context.internalOrgs=internalOrgs;
context.orgList=orgList;
context.customTimePeriodList=customTimePeriodList;
context.employeeList=employeeList;
company = delegator.findByPrimaryKey("PartyAndGroup", [partyId : "Company"]);
populateChildren(company, employeeList);

conditionList=[];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,"Company"));
conditionList.add(EntityCondition.makeCondition("finAccountTypeId", EntityOperator.EQUALS ,"BANK_ACCOUNT"));
finAccCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
companyAccList = delegator.findList("FinAccount", finAccCond, null, null, null, false);
context.put("companyAccList",companyAccList);

nowTimeStamp = UtilDateTime.nowTimestamp();
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS ,"HR_MONTH"));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO ,UtilDateTime.toSqlDate(nowTimeStamp)));
cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
customTimeList = delegator.findList("CustomTimePeriod", cond, null , UtilMisc.toList("-fromDate"), null, false);
context.put("defaultTimePeriodId","");
if(customTimeList){
	defaultTimePeriodId = (EntityUtil.getFirst(customTimeList)).get("customTimePeriodId");
	context.put("defaultTimePeriodId",defaultTimePeriodId);
}

//Json For EditPayrollAttendance : getting depatmentId for employeeId

if(UtilValidate.isNotEmpty(parameters.partyIdTo)){
JSONArray deptJSON = new JSONArray();
	JSONObject newObj = new JSONObject();
		departmentDetails=delegator.findByAnd("Employment", [partyIdTo : parameters.partyIdTo,thruDate:null]);
		partyId="";
		if(departmentDetails){
			partyId=departmentDetails[0].partyIdFrom;
		}
		request.setAttribute("partyId",partyId);
}

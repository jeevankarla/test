import org.ofbiz.base.util.Debug;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.party.party.PartyHelper;


// NOTE: This groovy assumes EmployeePayRollReport.groovy has been run before this


//Debug.logError("parameters.customTimePeriodId="+parameters.customTimePeriodId, "");
//Debug.logError("payRollSummaryMap="+payRollSummaryMap, "");
//Debug.logError("payRollMap="+payRollMap, "");
payRollSummaryMap = context.payRollSummaryMap;
Debug.logError("payRollSummaryMap="+payRollSummaryMap, "");

benefitDescMap=context.benefitDescMap;
benefitTypeIds=context.benefitTypeIds;
dedTypeIds=context.dedTypeIds;
dedDescMap=context.dedDescMap;

payheadTypeIds = [];
payheadTypeNames = [];

benefitTypeIds.each{ benefitTypeId->
	if (payRollSummaryMap.containsKey(benefitTypeId)) {
		payheadTypeIds.add(benefitTypeId);
		payheadTypeNames.add(benefitDescMap.get(benefitTypeId));
	}
}
dedTypeIds.each{ dedTypeId->
	if (payRollSummaryMap.containsKey(dedTypeId)) {
		payheadTypeIds.add(dedTypeId);
		payheadTypeNames.add(dedDescMap.get(dedTypeId));
	}
}

JSONArray benefitsTableJSON = new JSONArray();
employeeDeptMap = [:];
employments = [];
employments = EntityUtil.filterByDate(delegator.findList("Employment",null, null, null, null, false), context.timePeriodEnd);
employments.each { employment ->
	dept = delegator.findByPrimaryKey("PartyGroup", [partyId : employment.partyIdFrom]);	
	employeeDeptMap[employment.partyIdTo] = dept.groupName;
}
//Debug.logError("payRollEmployeeMap="+payRollEmployeeMap,"");
JSONArray employeesPayrollTableJSON = new JSONArray();
if (payRollEmployeeMap != null) {
	payRollEmployeeMap.each { employeePayroll ->
		partyId = employeePayroll.getKey();
		partyName = PartyHelper.getPartyName(delegator, partyId, false);		
		JSONArray employeePayrollJSON = new JSONArray();
		employeePayrollJSON.add(partyId);
		employeePayrollJSON.add(partyName);
		employeePayrollJSON.add(employeeDeptMap.get(partyId));
		employeePayrollItems = employeePayroll.getValue();
		payheadTypeIds.each{ payheadTypeId->
			amount = 0;
			if (employeePayrollItems.containsKey(payheadTypeId)) {
				amount = employeePayrollItems.get(payheadTypeId);//.setScale(0, BigDecimal.ROUND_HALF_UP);
			}
			employeePayrollJSON.add(amount);
		}
		employeesPayrollTableJSON.add(employeePayrollJSON);
	}
}
//Debug.logError("employeesPayrollTableJSON="+employeesPayrollTableJSON,"");

context.payheadTypes = payheadTypeNames;
context.employeesPayrollTableJSON = employeesPayrollTableJSON;

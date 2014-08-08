import org.ofbiz.base.util.Debug;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.party.party.PartyHelper;


// NOTE: This groovy assumes EmployeePayRollReport.groovy has been run before this


//Debug.logError("parameters.customTimePeriodId="+parameters.customTimePeriodId, "");
//Debug.logError("payRollSummaryMap="+payRollSummaryMap, "");
//Debug.logError("payRollMap="+payRollMap, "");
rounding = RoundingMode.HALF_UP;
benefitDescMap=context.benefitDescMap;
benefitTypeIds=context.benefitTypeIds;
benefitTypeNames=[];
benefitTypeIds.each{ benefitTypeId->
	benefitTypeNames.add(benefitDescMap.get(benefitTypeId));
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
		benefitTypeIds.each{ benefitTypeId->
			amount = 0;
			if (employeePayrollItems.containsKey(benefitTypeId)) {
				amount = employeePayrollItems.get(benefitTypeId).setScale(0,rounding);
			}
			//employeePayrollJSON.add(benefitTypeId);
			employeePayrollJSON.add(amount);
		}
		employeesPayrollTableJSON.add(employeePayrollJSON);
	}
}
Debug.logError("employeesPayrollTableJSON="+employeesPayrollTableJSON,"");

context.benefitTypes = benefitTypeNames;
context.employeesPayrollTableJSON = employeesPayrollTableJSON;

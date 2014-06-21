import org.ofbiz.base.util.Debug;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

// NOTE: This groovy assumes EmployeePayRollReport.groovy has been run before this


//Debug.logError("parameters.customTimePeriodId="+parameters.customTimePeriodId, "");
//Debug.logError("payRollSummaryMap="+payRollSummaryMap, "");
//Debug.logError("payRollMap="+payRollMap, "");
rounding = RoundingMode.HALF_UP;
JSONArray benefitsPieDataJSON = new JSONArray();
JSONArray benefitsTableJSON = new JSONArray();
payRollSummaryMap = context.payRollSummaryMap;
if (payRollSummaryMap != null) {
payRollSummaryMap.each { payhead ->
	if (payhead.getValue() > 0) {
		benefitName = benefitDescMap.get(payhead.getKey()) ? benefitDescMap.get(payhead.getKey()) : payhead.getKey();
		JSONArray benefitJSON = new JSONArray();
		benefitJSON.add(benefitName);
		benefitJSON.add(payhead.getValue().setScale(0,rounding));
		benefitsTableJSON.add(benefitJSON);
	
		JSONObject benefitPie = new JSONObject();
		benefitPie.put("label", benefitName);
		benefitPie.put("data", payhead.getValue().setScale(0,rounding));
		benefitsPieDataJSON.add(benefitPie);
	}
}
}

//Debug.logError("context.timePeriodEnd="+context.timePeriodEnd,"");
employeeDeptMap = [:];
employments = [];
employments = EntityUtil.filterByDate(delegator.findList("Employment",null, null, null, null, false), context.timePeriodEnd);
employments.each { employment ->
	dept = delegator.findByPrimaryKey("PartyGroup", [partyId : employment.partyIdFrom]);	
	employeeDeptMap[employment.partyIdTo] = dept.groupName;
}
//Debug.logError("payRollEmployeeMap="+payRollEmployeeMap,"");

deptBenefitsMap = [:];
if (payRollEmployeeMap != null) {
	payRollEmployeeMap.each { employeePayroll ->
		partyId = employeePayroll.getKey();
		(employeePayroll.getValue()).each { employeePayrollItem ->
			amount=employeePayrollItem.getValue();
			payrollHeaderItemTypeId=employeePayrollItem.getKey();		
			if(benefitTypeIds.contains(payrollHeaderItemTypeId)){
				if(UtilValidate.isEmpty(deptBenefitsMap.get(employeeDeptMap.get(partyId)))){
					deptBenefitsMap[employeeDeptMap.get(partyId)]=amount;
				}else{
					deptBenefitsMap[employeeDeptMap.get(partyId)]+=amount;
				}
			}
		}
	}
}
//Debug.logError("deptBenefitsMap="+deptBenefitsMap,"");
JSONArray benefitsByDeptPieDataJSON = new JSONArray();
JSONArray benefitsByDeptTableJSON = new JSONArray();
if (deptBenefitsMap != null) {
	deptBenefitsMap.each { deptBenefit ->
		if (deptBenefit.getValue() > 0) {
			JSONArray benefitJSON = new JSONArray();
			benefitJSON.add(deptBenefit.getKey());
			benefitJSON.add(deptBenefit.getValue().setScale(0,rounding));
			benefitsByDeptTableJSON.add(benefitJSON);
		
			JSONObject benefitsByDeptPie = new JSONObject();
			benefitsByDeptPie.put("label", deptBenefit.getKey());
			benefitsByDeptPie.put("data", deptBenefit.getValue().setScale(0,rounding));
			benefitsByDeptPieDataJSON.add(benefitsByDeptPie);
		}
	}
}

//Debug.logError("benefitsByDeptPieDataJSON="+benefitsByDeptPieDataJSON,"");
//Debug.logError("benefitsByDeptTableJSON="+benefitsByDeptTableJSON,"");
context.benefitsPieDataJSON = benefitsPieDataJSON;
context.benefitsTableJSON = benefitsTableJSON;
context.benefitsByDeptPieDataJSON = benefitsByDeptPieDataJSON;
context.benefitsByDeptTableJSON = benefitsByDeptTableJSON;
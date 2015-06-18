
	import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import in.vasista.vbiz.humanres.HumanresService;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.ofbiz.party.party.PartyHelper;

	dctx = dispatcher.getDispatchContext();
	JSONArray employeesJSON = new JSONArray();
	Map emplInputMap = FastMap.newInstance();


	String[] parties = parameters.parties;
	customTimePeriodId = parameters.customTimePeriodId;
	
	List partyList = new ArrayList();
	if(UtilValidate.isNotEmpty(parties)){
		for(int i=0;i<parties.length;i++){
			  partyList.add(parties[i]);
		}
	}
	
	List conditionList=[];
	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PAYROLL_BILL"));
	custTimeCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	periodBillingList = delegator.findList("PeriodBilling", custTimeCondition, null, null, null, false);
	periodBillingId = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN, periodBillingId));
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, UtilMisc.toList("1537","1941")));
	Condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	payrollHeaderList = delegator.findList("PayrollHeader", Condition, null, null, null, false);
	
	JSONObject employee = new JSONObject();
	
	for (int j = 0; j < payrollHeaderList.size(); ++j) {
		GenericValue employment = payrollHeaderList.get(j);
		employeeId = employment.getString("partyIdFrom");
		payrollHeaderId = employment.getString("payrollHeaderId");
		
		String partyName = PartyHelper.getPartyName(delegator, employeeId, false);
		
		employee.put("payrollHeaderId",payrollHeaderId);
		employee.put("employeeId", employeeId);
		employee.put("name", partyName);
		employee.put("comment", "");
		employeesJSON.add(employee);
	}
	
	context.customTimePeriodId = customTimePeriodId;
	context.employeesJSON = employeesJSON;

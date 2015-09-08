
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
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("GENERATED","APPROVED")));
	conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "PAYROLL_BILL"));
	custTimeCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	periodBillingList = delegator.findList("PeriodBilling", custTimeCondition, null, null, null, false);
	periodBillingId = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.IN, periodBillingId));
	//conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, UtilMisc.toList("6089","6398")));
	Condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	payrollHeaderList = delegator.findList("PayrollHeader", Condition, null, null, null, false);
	
	payrollHeaderIds = EntityUtil.getFieldListFromEntityList(payrollHeaderList, "payrollHeaderId", true);

	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.IN, payrollHeaderIds));
	payrollRetenctionondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	payrollRetentionList = delegator.findList("PayrollRetention", payrollRetenctionondition, null, null, null, false);

	deductionList = [];

	for(int i=0; i<payrollHeaderList.size(); i++ ){
		GenericValue employment = payrollHeaderList.get(i);
		employeeId = employment.getString("partyIdFrom");
		payrollHeaderId = employment.getString("payrollHeaderId");
		
		periodBillingId = employment.getString("periodBillingId");
		
		List conditionList1=[];
		conditionList1.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
		conditionList1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company"));
		conditionList1.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"));
		conditionList1.add(EntityCondition.makeCondition("paymentRefNum", EntityOperator.EQUALS, "PAYROLL_BILL"+"_"+periodBillingId));
		condition1=EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
		PaymentList = delegator.findList("Payment", condition1, null, null, null, false);
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("payrollHeaderId", EntityOperator.EQUALS, payrollHeaderId));
		conditionList.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId));
		filteredCondition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		payrollRetention = EntityUtil.filterByCondition(payrollRetentionList, filteredCondition);
		
		colorFlag = "N";
		comment = "";
		if(UtilValidate.isNotEmpty(payrollRetention)){
			colorFlag = "Y";
			comment = payrollRetention[0].get("comments");
		}
		
		
		String partyName = PartyHelper.getPartyName(delegator, employeeId, false);
		
		tmpMap = [:];
		tmpMap.put("payrollHeaderId",payrollHeaderId);
		tmpMap.put("employeeId", employeeId);
		tmpMap.put("name", partyName);
		tmpMap.put("colorFlag", colorFlag);
		tmpMap.put("comment", comment);
		if(UtilValidate.isEmpty(PaymentList)){
			deductionList.add(tmpMap);
		}
	}
	
	context.customTimePeriodId = customTimePeriodId;
	context.deductionList = deductionList;

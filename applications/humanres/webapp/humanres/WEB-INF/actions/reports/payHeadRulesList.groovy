import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.humanres.PayrollService;
import in.vasista.vbiz.humanres.HumanresService;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.party.party.PartyHelper;


dctx = dispatcher.getDispatchContext();

payrollBenDedRuleId = parameters.payrollBenDedRuleId;
payHeadTypeId = parameters.payHeadTypeId;
ruleName = parameters.ruleName;

benDedRuleList = [];
inputParamEnumId = "";
operatorEnumId = "";
condValue = "";
payHeadPriceActionTypeId = "";
customPriceCalcService = "";
acctgFormulaId = "";

conditionList=[];
if(UtilValidate.isNotEmpty(payrollBenDedRuleId)){
	conditionList.add(EntityCondition.makeCondition("payrollBenDedRuleId", EntityOperator.EQUALS, payrollBenDedRuleId));
}
if(UtilValidate.isNotEmpty(payHeadTypeId)){
	conditionList.add(EntityCondition.makeCondition("payHeadTypeId", EntityOperator.EQUALS, payHeadTypeId));
}
if(UtilValidate.isNotEmpty(ruleName)){
	conditionList.add(EntityCondition.makeCondition("ruleName", EntityOperator.EQUALS, ruleName));
}
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
payrollBenDedRuleList = delegator.findList("PayrollBenDedRule", condition , null, null, null, false );
if(UtilValidate.isNotEmpty(payrollBenDedRuleList)){
	payrollBenDedRuleList.each { benDedRule ->
		ruleId = benDedRule.get("payrollBenDedRuleId");
		typeId = benDedRule.get("payHeadTypeId");
		benDedRuleName = benDedRule.get("ruleName");
		benDedRuleMap = [:];
		
		benDedRuleMap["payrollBenDedRuleId"] = ruleId;
		benDedRuleMap["payHeadTypeId"] = typeId;
		benDedRuleMap["ruleName"] = benDedRuleName;
		
		conditionList1 = [];
		conditionList1.add(EntityCondition.makeCondition("payrollBenDedRuleId", EntityOperator.EQUALS, ruleId));
		condition1=EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
		PayrollBenDedCondList = delegator.findList("PayrollBenDedCond", condition1 , null, null, null, false );
		if(UtilValidate.isNotEmpty(PayrollBenDedCondList)){
			PayrollBenDedCondList.each { BenDedCond ->
				inputParamEnumId = BenDedCond.get("inputParamEnumId");
				operatorEnumId = BenDedCond.get("operatorEnumId");
				condValue = BenDedCond.get("condValue");
				benDedRuleMap["inputParamEnumId"] = inputParamEnumId;
				benDedRuleMap["operatorEnumId"] = operatorEnumId;
				benDedRuleMap["condValue"] = condValue;
				
				conditionList2 = [];
				conditionList2.add(EntityCondition.makeCondition("payrollBenDedRuleId", EntityOperator.EQUALS, ruleId));
				condition2=EntityCondition.makeCondition(conditionList2,EntityOperator.AND);
				PayHeadPriceActionList = delegator.findList("PayHeadPriceAction", condition2 , null, null, null, false );
				if(UtilValidate.isNotEmpty(PayHeadPriceActionList)){
					PayHeadPriceActionList.each { PayHeadAction ->
						payHeadPriceActionTypeId = PayHeadAction.get("payHeadPriceActionTypeId");
						customPriceCalcService = PayHeadAction.get("customPriceCalcService");
						acctgFormulaId = PayHeadAction.get("acctgFormulaId");
						
						acctgFormula = delegator.findOne("AcctgFormula", [acctgFormulaId : acctgFormulaId], false);
						formula = "";
						if(UtilValidate.isNotEmpty(acctgFormula)){
							formula = acctgFormula.get("formula");
						}
						
						amount = PayHeadAction.get("amount");
						benDedRuleMap["payHeadPriceActionTypeId"] = payHeadPriceActionTypeId;
						benDedRuleMap["customPriceCalcService"] = customPriceCalcService;
						benDedRuleMap["acctgFormulaId"] = acctgFormulaId;
						benDedRuleMap["formula"] = formula;
						benDedRuleMap["amount"] = amount;
						tempMap1 = [:];
						tempMap1.putAll(benDedRuleMap);
						if(UtilValidate.isNotEmpty(tempMap1)){
							benDedRuleList.addAll(tempMap1);
						}
					}
				}else{
					tempMap = [:];
					tempMap.putAll(benDedRuleMap);
					if(UtilValidate.isNotEmpty(tempMap)){
						benDedRuleList.addAll(tempMap);
					}
				}
			}
		}else{
			tempMap1 = [:];
			tempMap1.putAll(benDedRuleMap);
			if(UtilValidate.isNotEmpty(tempMap1)){
				benDedRuleList.addAll(tempMap1);
			}
		}
	}
}


context.putAt("benDedRuleList", benDedRuleList);



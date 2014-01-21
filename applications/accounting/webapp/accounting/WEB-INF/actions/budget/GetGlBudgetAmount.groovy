import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.entity.util.EntityFindOptions;

import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;

List exprList = [];
glAccountId = parameters.get("glAccountId");
exprList.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
exprList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
exprList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()), 
				EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)],EntityOperator.OR));

cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
EntityFindOptions findOptions = new EntityFindOptions();
findOptions.setMaxRows(1);
glBudgetItemTypes = delegator.findList("GlBudgetXref", cond, null, null, findOptions, false);   
if (glBudgetItemTypes) {
	budgetItemTypeId = glBudgetItemTypes[0].budgetItemTypeId;
	exprList.clear();
	exprList.add(EntityCondition.makeCondition("budgetItemTypeId", EntityOperator.EQUALS, budgetItemTypeId));
	cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	budgetItems = delegator.findList("BudgetItem", cond, null, null, null, false); 	
	if (budgetItems) {
		budgetItems.each { budgetItem ->	
			budgetId = budgetItem.budgetId;
			exprList.clear();
			exprList.add(EntityCondition.makeCondition("budgetId", EntityOperator.EQUALS, budgetId));
			cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
			budgets = delegator.findList("Budget", cond, null, null, null, false);
			budgets.each { budget ->
				budgetTimePeriod = delegator.findOne("CustomTimePeriod", [customTimePeriodId : budget.customTimePeriodId], false);	 				
				if (budgetTimePeriod && (budgetTimePeriod.fromDate <= UtilDateTime.nowTimestamp() && 
					budgetTimePeriod.thruDate >= UtilDateTime.nowTimestamp())) {
					context.budgetAmount=budgetItem.amount;
				}
			}
		}
	}
}
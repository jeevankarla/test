
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


List exprListForParameters = [];
exprListForParameters.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
exprListForParameters.add(EntityCondition.makeCondition("productSubscriptionTypeId", EntityOperator.EQUALS, "SPECIAL_ORDER"));
if (booths) {
	// this is typically populated only for zone owners
	exprListForParameters.add(EntityCondition.makeCondition([EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, booths),
		EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLogin.partyId)], EntityOperator.OR));
}

paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.AND);

listOrders = delegator.findList("OrderHeader", paramCond, null, ["-orderDate"], null, false);

context.listOrders = listOrders;
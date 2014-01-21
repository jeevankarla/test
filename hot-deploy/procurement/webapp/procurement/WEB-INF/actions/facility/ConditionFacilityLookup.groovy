import org.ofbiz.entity.condition.*;
 
exprList = [EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "BOOTH"), 
            EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "ZONE"),
			EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "DISTRIBUTOR")];
condList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
context.andCondition = EntityCondition.makeCondition([condList, EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, null)], EntityOperator.OR);
 
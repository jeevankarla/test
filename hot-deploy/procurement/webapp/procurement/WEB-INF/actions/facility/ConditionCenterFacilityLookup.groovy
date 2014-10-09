import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.Debug;
 
exprList = [EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "BOOTH"), 
            EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "ZONE"),
			EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "DISTRIBUTOR"),
			EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "PROC_ROUTE"),
			EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "SHED"),
			EntityCondition.makeCondition("facilityTypeId", EntityOperator.NOT_EQUAL, "UNIT")];
condList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
context.andCondition = EntityCondition.makeCondition([condList, EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, null)], EntityOperator.OR);
 
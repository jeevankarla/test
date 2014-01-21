import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;

listChangeIndents=[];

dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dateCondition = EntityCondition.makeCondition(
	[
	 EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	 EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	],
	EntityOperator.OR);
subProdList = delegator.findList("SubscriptionProduct", dateCondition, null, ["-lastModifiedDate"], null, false);
subProdList.each { subProd ->
	subProdMap = [:];
	subscription = subProd.getRelatedOne("Subscription");
	subProdMap["facilityId"] = subscription.facilityId 
	subProdMap["productId"] = subProd.productId;
	subProdMap["supplyType"] = subProd.productSubscriptionTypeId;
	subProdMap["quantity"] = subProd.quantity;
	subProdMap["modifiedBy"] = subProd.lastModifiedByUserLogin;
	subProdMap["modificationTime"] = subProd.lastModifiedDate;
	listChangeIndents.add(subProdMap);
}
context.listChangeIndents = listChangeIndents;

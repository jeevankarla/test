import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import java.text.SimpleDateFormat;

uiLabelMap = UtilProperties.getResourceBundleMap("OrderUiLabels", locale);

lastChangeSubProdMap=[:];

SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
List exprList = [];
exprList.add(EntityCondition.makeCondition([
	 EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
	 EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	], EntityOperator.OR));
exprList.add(EntityCondition.makeCondition("lastModifiedByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);


subProdList = delegator.findList("SubscriptionProduct", condition, null, ["-lastModifiedDate"], null, false);
if (subProdList.size() > 0) {
	productList.each{ product ->
		lastChangeSubProdMap[product.productId] = '';
	}
}
else {
	return;
}
tempSubscriptionId = "";
tempProductSubscriptionTypeId = "";
tempSequenceNum = "";
for (int i=0; i < subProdList.size(); i++) {
	subProd = subProdList.get(i);
	if (tempSubscriptionId == "") {
		tempSubscriptionId = subProd.subscriptionId;
		tempProductSubscriptionTypeId = subProd.productSubscriptionTypeId;
		tempSequenceNum = subProd.sequenceNum;
	}
	if (tempSubscriptionId != subProd.subscriptionId ||
		tempProductSubscriptionTypeId != subProd.productSubscriptionTypeId ||
		tempSequenceNum != subProd.sequenceNum)  {
		break;
	}
	subscription = subProd.getRelatedOne("Subscription");
	supplyType = (subProd.productSubscriptionTypeId == "SPECIAL_ORDER")?uiLabelMap.TypeSpecialOrder  : subProd.productSubscriptionTypeId;
	lastChangeSubProdMap["boothId"] = subscription.facilityId + " (" + supplyType +")";
	lastChangeSubProdMap[subProd.productId] = subProd.quantity;
	lastChangeSubProdMap["supplyType"] = supplyType;
	lastChangeSubProdMap["modifiedBy"] = subProd.lastModifiedByUserLogin;
	lastChangeSubProdMap["modificationTime"] = dateFormat.format(subProd.lastModifiedDate);
}
context.lastChangeSubProdMap = lastChangeSubProdMap;
Debug.logInfo("lastChangeSubProdMap="+lastChangeSubProdMap,"");

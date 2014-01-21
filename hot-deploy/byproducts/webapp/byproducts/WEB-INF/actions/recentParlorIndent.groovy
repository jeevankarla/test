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
orderId = null;
boothId = null;
modifiedBy = null;
modificationTime = null;
prodList = [];
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
List exprList = [];
exprList.add(EntityCondition.makeCondition([
	 EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd),
	 EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)
	], EntityOperator.OR));
exprList.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLogin.userLoginId));
exprList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, "PARLOR_SALES_CHANNEL"));
exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
lastOrderList = delegator.findList("OrderHeader", condition, null, ["-lastUpdatedStamp"], null, false);
if(lastOrderList){
	lastOrder = EntityUtil.getFirst(lastOrderList);
	//boothId = lastOrder.productStoreId;
	salesEnum = lastOrder.salesChannelEnumId;
	if(salesEnum == "PARLOR_SALES_CHANNEL"){
		boothId = lastOrder.productStoreId;
		orderId = lastOrder.orderId;
		modifiedBy = lastOrder.createdBy;
		modTime = lastOrder.lastUpdatedStamp;
		modificationTime = dateFormat.format(modTime);
	}
}
if(orderId){
	orderItemList = delegator.findList("OrderHeaderAndItems", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	if (orderItemList.size() > 0) {
		orderItemList.each{ eachItem ->
			productMap = [:];
			productMap.productId = eachItem.productId;
			productMap.productName = eachItem.productId;
			prodList.add(productMap);
			lastChangeSubProdMap[eachItem.productId] = eachItem.quantity;
			lastChangeSubProdMap["boothId"] = boothId;
			lastChangeSubProdMap["modifiedBy"] = modifiedBy;
			lastChangeSubProdMap["modificationTime"] = modificationTime;//dateFormat.format(eachItem.changeDatetime);
		}
	}
	context.prodList = prodList;
}

context.lastChangeSubProdMap = lastChangeSubProdMap;
Debug.logInfo("lastChangeSubProdMap="+lastChangeSubProdMap,"");

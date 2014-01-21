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
subscriptionId = null;
productSubscriptionTypeId = null;
fromDate = null;
modifiedBy = null;
modificationTime = null;
prodList = [];
SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);

List exprList = [];
exprList.add(EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS, userLogin.userLoginId));
exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
exprList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
exprList.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS, "GENERATED"));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
lastOrderProdList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, ["-changeDatetime"], null, false);
lastestOrder = null;
orderId = null;
checkFlag = "";
if(lastOrderProdList){
	lastOrder = EntityUtil.getFirst(lastOrderProdList);
	lastestOrder = lastOrder.changeDatetime;
	orderId = lastOrder.orderId;
	checkFlag ="lastestOrder";
}
List exprList1 = [];
exprList1.add(EntityCondition.makeCondition("receivedByUserLoginId", EntityOperator.EQUALS, userLogin.userLoginId));
exprList1.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
condition = EntityCondition.makeCondition(exprList1, EntityOperator.AND);
lastParlorProdList = delegator.findList("ShipmentReceiptAndItem", condition, null, ["-datetimeReceived"], null, false);
lastestTransfer = null;
boothId = null;
if(lastParlorProdList){
	lastTransfer = EntityUtil.getFirst(lastParlorProdList);
	lastestTransfer = lastTransfer.datetimeReceived;
	boothId = lastTransfer.facilityId;
	checkFlag ="lastestTransfer";
}
if(lastestTransfer && lastestOrder){
	if(lastestOrder.compareTo(lastestTransfer)> 0){
		checkFlag ="lastestOrder";
	}
	else {
		checkFlag ="lastestTransfer";
	}
}

if(checkFlag == "lastestOrder"){
	orderItemsList = EntityUtil.filterByAnd(lastOrderProdList, ["orderId": orderId]);
	if (orderItemsList.size() > 0) {
			booth = EntityUtil.getFirst(orderItemsList);
			boothId = booth.originFacilityId;
			orderItemsList.each{ eachItem ->
				productMap = [:];
				productMap.productId = eachItem.productId;
				productMap.productName = eachItem.productId;
				prodList.add(productMap);
				lastChangeSubProdMap[eachItem.productId] = eachItem.quantity;
				lastChangeSubProdMap["boothId"] = boothId;
				lastChangeSubProdMap["modifiedBy"] = eachItem.changeByUserLoginId;
				lastChangeSubProdMap["modificationTime"] = dateFormat.format(eachItem.changeDatetime);//dateFormat.format(eachItem.changeDatetime);
		}
	}
}
else{
	if (lastParlorProdList.size() > 0) {
		validTransferFlag = true;
			firstItemList = EntityUtil.getFirst(lastParlorProdList);
			boothId = firstItemList.facilityId;
			lastParlorProdList.each{ eachItem ->
				productMap = [:];
				if(eachItem.facilityId == boothId && validTransferFlag){
					productMap.productId = eachItem.productId;
					productMap.productName = eachItem.productId;
					prodList.add(productMap);
					lastChangeSubProdMap[eachItem.productId] = eachItem.quantityAccepted;
					lastChangeSubProdMap["boothId"] = boothId;
					lastChangeSubProdMap["modifiedBy"] = eachItem.receivedByUserLoginId;
					lastChangeSubProdMap["modificationTime"] = dateFormat.format(eachItem.datetimeReceived);//dateFormat.format(eachItem.changeDatetime);
				}
				else{
					validTransferFlag = false;
				}
				
		}
	}
}
context.prodList = prodList;
context.lastChangeSubProdMap = lastChangeSubProdMap;

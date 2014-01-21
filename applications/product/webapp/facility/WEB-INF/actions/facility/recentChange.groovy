import java.util.*;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import java.text.SimpleDateFormat;
import  org.ofbiz.network.NetworkServices;
import org.ofbiz.base.util.UtilDateTime;
uiLabelMap = UtilProperties.getResourceBundleMap("OrderUiLabels", locale);

lastChangeSubProdMap=[:];

SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
List exprList = [];


//Enable LMS PM Sales(if the property set to 'Y' then ,Day  NetSales  = 'AM Sales+Prev.Day PM Sales'   otherwise NetSales = 'AM Sales+ PM Sales')
Boolean enableSameDayPmEntry = Boolean.FALSE;
try{
	 GenericValue tenantConfigEnableSameDayPmEntry = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","LMS", "propertyName","enableSameDayPmEntry"), false);
	 if (UtilValidate.isNotEmpty(tenantConfigEnableSameDayPmEntry) && (tenantConfigEnableSameDayPmEntry.getString("propertyValue")).equals("Y")) {
		 enableSameDayPmEntry = Boolean.TRUE;
		 if(!("CardSale".equals(context.changeFlag))){
			 context.defaultEffectivePrevDateTime  =  defaultEffectiveDateTime;
			 context.defaultEffectivePrevDate = UtilDateTime.toDateString(context.defaultEffectivePrevDateTime, "dd MMMMM, yyyy"); 
		 }
	}	
 }catch (GenericEntityException e) {
	// TODO: handle exception
	 Debug.logError(e, module);
}
 context.enableSameDayPmEntry = enableSameDayPmEntry;
if("TruckSheetCorrection".equals(context.changeFlag)){	
	List shipmentIds = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"AM_SHIPMENT");
	exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN ,shipmentIds));
}
if("GatePass".equals(context.changeFlag)){
	List shipmentIds = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"AM_SHIPMENT_SUPPL");
	
	if(!enableSameDayPmEntry){
		shipmentIds.addAll(NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(UtilDateTime.addDaysToTimestamp(dayBegin,-1), "yyyy-MM-dd HH:mm:ss"),"PM_SHIPMENT_SUPPL"));
	}else{
		shipmentIds.addAll(NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),"PM_SHIPMENT_SUPPL"));
	}
	
	exprList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN ,shipmentIds));
}
if(!("CardSale".equals(context.changeFlag))){
	exprList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS  ,"SALES_ORDER"));
	exprList.add(EntityCondition.makeCondition("shipmentStatusId", EntityOperator.EQUALS , "GENERATED"));
	/*exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO  ,dayBegin));
	exprList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));*/
	exprList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL  , "ORDER_CANCELLED"));
	exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	exprList.add(EntityCondition.makeCondition("changeDatetime", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
	exprList.add(EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS, userLogin.userLoginId));
	
	
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	
	OrderItemList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition, null, ["-changeDatetime"], null, false);
}
if(("CardSale".equals(context.changeFlag))){
	exprList.add(EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
		EntityCondition.makeCondition("lastModifiedDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)
	   ], EntityOperator.OR));   	
	exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "ORDER_CREATED"));
	exprList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, userLogin.userLoginId));
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);	
	OrderItemList = delegator.findList("MilkCardOrderAndItem", condition, null, ["-lastModifiedDate"], null, false);
	
}

if (OrderItemList.size() > 0) {
	
	if("CardSale".equals(context.changeFlag)){
		milkCardTypeList = delegator.findList("MilkCardType", EntityCondition.makeCondition([isLMS:'Y']), null, null, null, false);
		milkCardTypeList.each{ milkCardType ->
			lastChangeSubProdMap[milkCardType.milkCardTypeId] = '';
		}		
		
	}else{
		productList.each{ product ->
			lastChangeSubProdMap[product.productId] = '';
		}	
	}
	
}
else {
	return;
}
tempOrderId = "";
for (int i=0; i < OrderItemList.size(); i++) {
	orderItemProd = OrderItemList.get(i);
	if (tempOrderId == "") {
		tempOrderId = orderItemProd.orderId;
	}
	if (tempOrderId != orderItemProd.orderId)  {
		break;
	}
	if("CardSale".equals(context.changeFlag)){			
						
			
		lastChangeSubProdMap["boothId"] = orderItemProd.boothId + " (CARD)";
		lastChangeSubProdMap[orderItemProd.milkCardTypeId] = (orderItemProd.quantity).intValue();
		lastChangeSubProdMap["supplyType"] = "CARD";
		lastChangeSubProdMap["modifiedBy"] = orderItemProd.lastModifiedByUserLogin;
		lastChangeSubProdMap["modificationTime"] = dateFormat.format(orderItemProd.lastModifiedDate);
		
	}else{
		supplyType = (orderItemProd.productSubscriptionTypeId == "SPECIAL_ORDER")?uiLabelMap.TypeSpecialOrder  : orderItemProd.productSubscriptionTypeId;
		lastChangeSubProdMap["boothId"] = orderItemProd.originFacilityId + " (" + supplyType +")";
		lastChangeSubProdMap[orderItemProd.productId] = (orderItemProd.quantity).intValue();
		lastChangeSubProdMap["supplyType"] = supplyType;
		lastChangeSubProdMap["modifiedBy"] = orderItemProd.changeByUserLoginId;
		lastChangeSubProdMap["modificationTime"] = dateFormat.format(orderItemProd.changeDatetime);
	}
	
}
context.lastChangeSubProdMap = lastChangeSubProdMap;
Debug.logInfo("lastChangeSubProdMap="+lastChangeSubProdMap,"");

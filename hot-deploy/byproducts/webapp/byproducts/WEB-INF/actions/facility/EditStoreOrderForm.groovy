
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;


partyId = parameters.partyId;
subscriptionTypeId = parameters.subscriptionTypeId;
productSubscriptionTypeId = parameters.productSubscriptionTypeId;
shipmentTypeId = parameters.shipmentTypeId;
salesChannel = parameters.salesChannel;
dctx = dispatcher.getDispatchContext();
effectiveDate = parameters.effectiveDate;
changeFlag=parameters.changeFlag;
subscriptionProdList = [];
displayGrid = true;
effDateDayBegin="";
effDateDayEnd="";

SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy");
if(UtilValidate.isNotEmpty(effectiveDate)){
try {
	effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDate).getTime());
}catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + effDate, "");
	displayGrid = false;
}
	effDateDayBegin = UtilDateTime.getDayStart(effectiveDate);
	effDateDayEnd = UtilDateTime.getDayEnd(effectiveDate);
}else{
	effDateDayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	effDateDayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
}
partyId = parameters.partyId;
orderTaxType = parameters.orderTaxType;
packingType = parameters.packingType;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, salesChannel));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, null));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, effDateDayBegin));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, effDateDayEnd));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderHeaders = delegator.findList("OrderHeader", cond, UtilMisc.toSet("orderId"), null, null, false);

orderIds = EntityUtil.getFieldListFromEntityList(orderHeaders, "orderId", true);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
partyOrders = delegator.findList("OrderRole", expr, UtilMisc.toSet("orderId"), null, null, false);

JSONArray orderItemsJSON = new JSONArray();
partyOrderIds = EntityUtil.getFieldListFromEntityList(partyOrders, "orderId", true);
if(partyOrderIds){
	updateOrderId = partyOrderIds.get(0);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId));
	conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId), null, null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	orderItems.each{ eachItem ->
		
		batchDetails = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		batchNo = "";
		if(batchDetails){
			batchNo = (batchDetails.get(0)).get("attrValue");
		}
		productDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		prodDetail = null;
		if(productDetails){
			prodDetail = productDetails.get(0);
		}
		JSONObject newObj = new JSONObject();
		
		newObj.put("cProductId",eachItem.productId);
		newObj.put("cProductName",prodDetail.description +" [ "+prodDetail.brandName+"]");
		newObj.put("quantity",eachItem.quantity);
		newObj.put("batchNo", batchNo);
		if(changeFlag && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale"){
			if(eachItem.unitPrice){
				newObj.put("basicPrice", eachItem.unitPrice);
			}else{
				newObj.put("basicPrice", 0);
			}
			if(eachItem.cstAmount){
				newObj.put("cstPrice", eachItem.cstAmount);
			}else{
				newObj.put("cstPrice", 0);
			}
			if(eachItem.vatAmount){
				newObj.put("vatPrice", eachItem.vatAmount);
			}else{
				newObj.put("vatPrice", 0);
			}
			if(eachItem.bedAmount){
				newObj.put("bedPrice", eachItem.bedAmount);
			}else{
				newObj.put("bedPrice", 0);
			}
			newObj.put("quantity",eachItem.quantity);
		}
		orderItemsJSON.add(newObj);
		
	}
	context.orderId = updateOrderId;
}
context.dataJSON = orderItemsJSON;





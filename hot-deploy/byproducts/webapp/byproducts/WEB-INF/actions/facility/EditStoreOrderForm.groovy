
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
if(UtilValidate.isNotEmpty(changeFlag) && "IcpSalesAmul"==changeFlag){
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
}else{
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
}
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
partyOrders = delegator.findList("OrderRole", expr, UtilMisc.toSet("orderId"), null, null, false);

JSONArray orderItemsJSON = new JSONArray();

JSONArray orderAdjustmentJSON = new JSONArray();//Orderadjustment Json

partyOrderIds = EntityUtil.getFieldListFromEntityList(partyOrders, "orderId", true);
if(partyOrderIds){
	updateOrderId = partyOrderIds.get(0);
	
	orderHeaderTemp=delegator.findOne("OrderHeader",[orderId :updateOrderId], false);
	if(UtilValidate.isNotEmpty(orderHeaderTemp)){
		context.orderMessage=orderHeaderTemp.orderMessage;
	}
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId));
	/*conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));*/
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
	batchNumberAttr = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));
	daysToStoreAttr = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "daysToStore"));
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId), null, null, null, false);
	
	
	productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	productCategorySelect = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	productCategorySelectIds = EntityUtil.getFieldListFromEntityList(productCategorySelect, "productCategoryId", true);
	
	JSONArray productCategoryJSON = new JSONArray();
	productCategorySelectIds.each{eachCatId ->
		productCategoryJSON.add(eachCatId);
	}
	context.productCategoryJSON = productCategoryJSON;
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	orderItems.each{ eachItem ->
		
		batchDetails = EntityUtil.filterByCondition(batchNumberAttr, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		batchNo = "";
		if(batchDetails){
			batchNo = (batchDetails.get(0)).get("attrValue");
		}
		daysDetails = EntityUtil.filterByCondition(daysToStoreAttr, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		daysToStore = "";
		if(daysDetails){
			daysToStore = (daysDetails.get(0)).get("attrValue");
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
		newObj.put("daysToStore", daysToStore);
		newObj.put("unitPrice", eachItem.unitListPrice);
		if(changeFlag && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale"){
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
			if(eachItem.serviceTaxAmount){
				newObj.put("serviceTaxPrice", eachItem.serviceTaxAmount);
			}else{
				newObj.put("serviceTaxPrice", 0);
			}
			//adding perecenatges
			if(eachItem.bedPercent){newObj.put("bedPercent", eachItem.bedPercent); }else{ newObj.put("bedPercent", 0); }
			
			if(eachItem.vatPercent){newObj.put("vatPercent", eachItem.vatPercent); }else{ newObj.put("vatPercent", 0); }
			
			if(eachItem.cstPercent){newObj.put("cstPercent", eachItem.cstPercent); }else{ newObj.put("cstPercent", 0); }
			
			if(eachItem.serviceTaxPercent){newObj.put("serviceTaxPercent", eachItem.serviceTaxPercent); }else{ newObj.put("serviceTaxPercent", 0); }
		
			newObj.put("quantity",eachItem.quantity);
		}
		orderItemsJSON.add(newObj);
		
	}
	context.orderId = updateOrderId;
	//adding adjustments here
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId));
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALE_ORDER_ADJUSTMNT"));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderAdjustmentList = delegator.findList("OrderHeaderAdjustmentAndAdjustmentType", condExpr, null, null, null, false);
	
	orderAdjustmentList.each{eachItem->
		JSONObject newAdjObj = new JSONObject();
		newAdjObj.put("orderAdjTypeId", eachItem.orderAdjustmentTypeId);
		newAdjObj.put("adjAmount", eachItem.amount);
		
		
		orderAdjustmentJSON.add(newAdjObj);
	}
	
	
}
context.dataJSON = orderItemsJSON;
context.data2JSON = orderAdjustmentJSON;





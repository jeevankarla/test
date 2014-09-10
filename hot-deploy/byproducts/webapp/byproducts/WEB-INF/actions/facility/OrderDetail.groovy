import org.ofbiz.base.util.UtilDateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.math.RoundingMode;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;

dctx = dispatcher.getDispatchContext();
rounding = RoundingMode.HALF_UP;
orderId = parameters.orderId;
screenFlag = parameters.screenFlag;
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId),false);
salesChannel = orderHeader.salesChannelEnumId;
requestFlag = ""
if(salesChannel == "DEPOT_CHANNEL"){
	requestFlag = "depot";
}
if(salesChannel == "ICP_NANDINI_CHANNEL"){
	requestFlag = "nandini";
}
if(salesChannel == "ICP_BELLARY_CHANNEL"){
	requestFlag = "bellary";
}
if(salesChannel == "ICP_AMUL_CHANNEL"){
	requestFlag = "amul";
}
if(salesChannel == "FGS_PRODUCT_CHANNEL"){
	requestFlag = "fgs";
}

JSONObject orderDetailJSON = new JSONObject();
orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
batchDetails = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

conversionResult = ByProductNetworkServices.getProductQtyConversions(dctx, UtilMisc.toMap("productList", products, "userLogin", userLogin));
productConversionDetails = [:];
if(conversionResult){
	productConversionDetails = conversionResult.get("productConversionDetails");
}
JSONArray orderItemListJSON = new JSONArray();
if(screenFlag && screenFlag=="batchEdit"){
	orderItems.each { eachItem ->
		
		JSONObject itemJSON = new JSONObject();
		orderItemSeqId = eachItem.orderItemSeqId;
		
		productConvDetail = productConversionDetails.get(eachItem.productId);
		prodCrateValue = 1;
		if(productConvDetail && productConvDetail.get("CRATE")){
			prodCrateValue = productConvDetail.get("CRATE");
		}

		prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		
		prodDesc = "";
		qtyInc = 1;
		if(prodDetails){
			prodDesc = (prodDetails.get(0)).description;
			qtyInc = (prodDetails.get(0)).quantityIncluded;
		}
		
		crateQty = (eachItem.quantity).divide(new BigDecimal(prodCrateValue) , 2, rounding);
		qtyLtr = (eachItem.quantity).multiply(qtyInc).setScale(2, rounding);

		batchNo = "";
		if(batchDetails){
			condList = [];
			condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
			condList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));
			condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
			itemBatchDetail = EntityUtil.filterByCondition(batchDetails, condExpr);
			
			if(itemBatchDetail){
				batchNo = (EntityUtil.getFirst(itemBatchDetail)).attrValue;
			}
		}
		itemJSON.put("orderId", eachItem.orderId);
		itemJSON.put("orderItemSeqId", eachItem.orderItemSeqId);
		itemJSON.put("productId", eachItem.productId);
		itemJSON.put("batchNo", batchNo);
		itemJSON.put("itemDescription", prodDesc);
		if(requestFlag == "nandini" || requestFlag == "amul" || requestFlag == "bellary"){
			itemJSON.put("quantity", crateQty);
		}
		else{
			itemJSON.put("quantity", qtyLtr);
		}
		orderItemListJSON.add(itemJSON);
	}
}
else{
	orderItems.each { eachItem ->
		taxPercent = 0;
		orderItemSeqId = eachItem.orderItemSeqId;
		if(eachItem.vatPercent && eachItem.vatPercent>0){
			taxPercent = eachItem.vatPercent;
		}
		if(eachItem.cstPercent && eachItem.cstPercent>0){
			taxPercent = eachItem.cstPercent;
		}
		
		productConvDetail = productConversionDetails.get(eachItem.productId);
		prodCrateValue = 1;
		if(productConvDetail && productConvDetail.get("CRATE")){
			prodCrateValue = productConvDetail.get("CRATE");
		}
		batchNo = "";
		if(batchDetails){
			condList = [];
			condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			condList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
			condList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));
			condExpr = EntityCondition.makeCondition(condList, EntityOperator.AND);
			itemBatchDetail = EntityUtil.filterByCondition(batchDetails, condExpr);
			
			if(itemBatchDetail){
				batchNo = (EntityUtil.getFirst(itemBatchDetail)).attrValue;
			}
		}
		JSONObject itemJSON = new JSONObject();
		prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		prodDesc = "";
		qtyInc = 1;
		if(prodDetails){
			prodDesc = (prodDetails.get(0)).description;
			qtyInc = (prodDetails.get(0)).quantityIncluded;
		}
		crateQty = (eachItem.quantity).divide(new BigDecimal(prodCrateValue) , 2, rounding);
		qtyLtr = (eachItem.quantity).multiply(qtyInc).setScale(2, rounding);

		itemJSON.put("productId", eachItem.productId);
		itemJSON.put("itemDescription", prodDesc);
		itemJSON.put("batchNo", batchNo);
		if(requestFlag == "nandini" || requestFlag == "amul" || requestFlag == "bellary"){
			itemJSON.put("quantity", crateQty);
		}
		else{
			itemJSON.put("quantity", qtyLtr);
		}
		itemJSON.put("taxPercent", taxPercent);
		itemJSON.put("itemTotal", eachItem.quantity*eachItem.unitListPrice);
		orderItemListJSON.add(itemJSON);
	}
}
context.orderItemListJSON = orderItemListJSON;
request.setAttribute("orderItemListJSON", orderItemListJSON);
request.setAttribute("requestFlag", requestFlag);
return "success";

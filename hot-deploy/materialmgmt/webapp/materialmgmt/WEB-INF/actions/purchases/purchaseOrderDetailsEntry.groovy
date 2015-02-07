
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
import in.vasista.vbiz.purchase.MaterialHelperServices;

orderId = parameters.orderId;
dctx = dispatcher.getDispatchContext();

supplierId = "";

// usage of po balance if needed
poBalanceProductMap=[:];
resultMap=MaterialHelperServices.getBalanceAndReceiptQtyForPO(dctx,UtilMisc.toMap("orderId", orderId));
if(UtilValidate.isNotEmpty(resultMap.get("productTotals"))){
	poBalanceProductMap=resultMap.get("productTotals");
}

if(orderId){
	orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	if(!orderHeader){

		context.errorMessage = "No Order Found with Order Id: "+orderId+" !";
		return;
		
	}
	if(orderHeader.statusId != "ORDER_APPROVED"){

		context.errorMessage = "Order with Order Id: "+orderId+" is not approved!";
		return;
		
	}
	if(orderHeader.orderTypeId != "PURCHASE_ORDER"){

		context.errorMessage = "Order with Order Id: "+orderId+" is not a purchase order!";
		return;
	}

	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderItemsAndRole = delegator.findList("OrderHeaderItemAndRoles", condition, null, null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(orderItemsAndRole, "productId", true);
	result = MaterialHelperServices.getProductUOM(delegator, productIds);
	uomLabelMap = result.get("uomLabel");
	productUomMap = result.get("productUom");
	prodQtyMap = [:];
	
	JSONArray orderItemsJSON = new JSONArray();
	orderItemsAndRole.each{ eachItem ->
		
		JSONObject newObj = new JSONObject();
		uomId = productUomMap.get(eachItem.productId);
		uomLabel = "";
		if(uomId){
			uomLabel = uomLabelMap.get(uomId);
		}
		newObj.put("cProductId",eachItem.productId);
		receivedQty=0;
		maxReceivedQty=0;
		poBalDetailsMap=poBalanceProductMap.get(eachItem.productId);
		if(UtilValidate.isNotEmpty(poBalDetailsMap.get("receivedQty"))){
			receivedQty=poBalDetailsMap.get("receivedQty");
		}
		if(UtilValidate.isNotEmpty(poBalDetailsMap.get("maxReceivedQty"))){
			maxReceivedQty=poBalDetailsMap.get("maxReceivedQty");
		}
		productDetails = delegator.findOne("Product", UtilMisc.toMap("productId", eachItem.productId), false);
		if(UtilValidate.isNotEmpty(productDetails)){
			newObj.put("cProductName",productDetails.brandName +" [ " +productDetails.description+"]");
		}else{
		newObj.put("cProductName",eachItem.itemDescription +" [ "+eachItem.productId+"]");
		}
		//newObj.put("cProductName",eachItem.itemDescription +" [ "+eachItem.productId+"]");
		
		
		newObj.put("orderedQty",eachItem.quantity);
		newObj.put("oldRecvdQty",receivedQty);
		newObj.put("maxReceivedQty",maxReceivedQty);
		newObj.put("uomDescription",uomLabel);
		orderItemsJSON.add(newObj);
	
		supplierId = eachItem.partyId;
	}
	supplierName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, supplierId, false);
	context.dataJSON = orderItemsJSON;
	context.supplierId = supplierId;
	context.supplierName = supplierName;
}else{

	resultMap = MaterialHelperServices.getMaterialProducts(dctx, context);
	prodList = resultMap.get("productList");
	JSONArray productItemsJSON = new JSONArray();
	JSONObject productIdLabelJSON = new JSONObject();
	JSONObject productLabelIdJSON=new JSONObject();
	//context.productList = prodList;
	
	prodList.each{eachItem ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.productId);
		newObj.put("label",eachItem.brandName +" [ " +eachItem.description+"]");
		productItemsJSON.add(newObj);
		productIdLabelJSON.put(eachItem.productId, eachItem.brandName+" [ "+eachItem.description +"]");
		productLabelIdJSON.put(eachItem.brandName+" [ "+eachItem.description+"]", eachItem.productId);
	}
	
	
	context.productItemsJSON = productItemsJSON;
	context.productIdLabelJSON = productIdLabelJSON;
	context.productLabelIdJSON = productLabelIdJSON;

}
//context.dataJSON = orderItemsJSON;
context.orderId = orderId;

context.vehicleId = parameters.vehicleId;
context.withoutPO = parameters.withoutPO;

if(parameters.supplierId){
	supplierId = parameters.supplierId;
}

if(supplierId){
	supplierName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, supplierId, false);
	context.supplierId = supplierId;
	context.supplierName = supplierName;
}

return "success";




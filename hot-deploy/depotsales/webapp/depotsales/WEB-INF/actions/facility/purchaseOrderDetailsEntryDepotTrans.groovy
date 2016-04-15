
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
/*resultMap=MaterialHelperServices.getBalanceAndReceiptQtyForPO(dctx,UtilMisc.toMap("orderId", orderId));
if(UtilValidate.isNotEmpty(resultMap.get("productTotals"))){
	poBalanceProductMap=resultMap.get("productTotals");
}*/
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
	//conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderItems = delegator.findList("OrderItem", condition, null, null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	result = MaterialHelperServices.getProductUOM(delegator, productIds);
	uomLabelMap = result.get("uomLabel");
	productUomMap = result.get("productUom");
	prodQtyMap = [:];
	
	JSONArray orderItemsJSON = new JSONArray();
	orderItems.each{ eachItem ->
		
		JSONObject newObj = new JSONObject();
		uomId = productUomMap.get(eachItem.productId);
		uomLabel = "";
		if(uomId){
			uomLabel = uomLabelMap.get(uomId);
		}
		newObj.put("cProductId",eachItem.productId);
		orderItemQty = eachItem.quantity;
		receivedQty=0;
		maxReceivedQty=0;
		poBalDetailsMap=[:];
		resultMap=MaterialHelperServices.getBalanceAndReceiptQtyForPO(dctx,UtilMisc.toMap("orderId", eachItem.orderId,"orderItemSeqId", eachItem.orderItemSeqId));
		if(UtilValidate.isNotEmpty(resultMap.get("productTotals"))){
			poBalanceProductMap=resultMap.get("productTotals");
		}
		if(UtilValidate.isNotEmpty(poBalanceProductMap)){
			poBalDetailsMap=poBalanceProductMap.get(eachItem.productId);
		}
		if(UtilValidate.isNotEmpty(poBalDetailsMap.get("receivedQty"))){
			receivedQty=poBalDetailsMap.get("receivedQty");
		}
		if(UtilValidate.isNotEmpty(poBalDetailsMap.get("maxReceivedQty"))){
			maxReceivedQty=poBalDetailsMap.get("maxReceivedQty");
		}
		productDetails = delegator.findOne("Product", UtilMisc.toMap("productId", eachItem.productId), false);
		if(UtilValidate.isNotEmpty(productDetails)){
			newObj.put("cProductName","("+productDetails.productId +") "+productDetails.description+" - "+productDetails.internalName);
		}else{
		newObj.put("cProductName",eachItem.itemDescription +" [ "+eachItem.productId+"]");
		}
		//newObj.put("cProductName",eachItem.itemDescription +" [ "+eachItem.productId+"]");
		newObj.put("orderItemSeqId",eachItem.orderItemSeqId);
		newObj.put("orderedQty",eachItem.quantity);
		newObj.put("oldRecvdQty",receivedQty);
		newObj.put("quantity",eachItem.quantity-receivedQty);
		newObj.put("balance",0);
		//newObj.put("balance",eachItem.quantity-receivedQty);
		newObj.put("balanceQty",eachItem.quantity-receivedQty);
		
		if(receivedQty>eachItem.quantity){
		    newObj.put("quantity",0);
			newObj.put("balance",0);
			newObj.put("balanceQty",0);
		}
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

prodList = [];
		try{
			/*productCategoryList = delegator.findByAnd("ProductCategory", UtilMisc.toMap("productCategoryTypeId","BYPROD"));
			productCategoryIdsList = EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);
		 	productList = ProductWorker.getProductsByCategoryList(delegator, productCategoryIdsList, null);*/
			exprList= [];
			exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
			exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
			exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.LIKE,"%HANK%"));
			/*exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
			EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));*/
			EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
					
			productList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
			
			//Debug.log("productList======"+productList);
			
			prodIdsList = [];
			 for(GenericValue product : productList){
				String productId = product.getString("productId");
				if(!prodIdsList.contains(productId)){
					prodIdsList.add(productId);
					prodList.add(product);
				}
			}
			
		}catch(Exception e){
		Debug.logError(e, "Unable get the products" );
		}
		
	productIds = EntityUtil.getFieldListFromEntityList(prodList, "productId", true);
	Map result = (Map)MaterialHelperServices.getProductUOM(delegator, productIds);
	uomLabelMap = result.get("uomLabel");
	productUomMap = result.get("productUom");
	
		
//	resultMap = MaterialHelperServices.getMaterialProducts(dctx, context);
//	prodList = resultMap.get("productList");
	JSONArray productItemsJSON = new JSONArray();
	JSONObject productIdLabelJSON = new JSONObject();
	JSONObject productLabelIdJSON=new JSONObject();
	//context.productList = prodList;
	JSONObject productUOMJSON = new JSONObject();
	JSONObject uomLabelJSON=new JSONObject();
	
	prodList.each{eachItem ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.productId);
		newObj.put("label","[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName);
		productItemsJSON.add(newObj);
		productIdLabelJSON.put(eachItem.productId, "[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName);
		productLabelIdJSON.put("[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName, eachItem.productId);
		
		productUOMJSON.put(eachItem.productId, "KG");
		uomLabelJSON.put("KG", "KG");
		/*if(productUomMap){
			uomId = productUomMap.get(eachItem.productId);
			if(uomId){
				productUOMJSON.put(eachItem.productId, uomId);
				uomLabelJSON.put(uomId, uomLabelMap.get(uomId));
			}
		}*/
	}
	
	//Debug.log("====productUOMJSON=="+productUOMJSON+"====uomLabelJSON==="+uomLabelJSON);
	
	context.productUOMJSON = productUOMJSON;
	context.uomLabelJSON = uomLabelJSON;
	
	context.productItemsJSON = productItemsJSON;
	context.productIdLabelJSON = productIdLabelJSON;
	context.productLabelIdJSON = productLabelIdJSON;

}

considerTerms = ["COGS_INSTL_CHG", "COGS_FREIGHT", "COGS_TNS_CHG"];

termTypes = delegator.findList("TermType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "OTHERS"), null, null, null, false);
JSONArray additionalChargesJSON = new JSONArray();
JSONObject addChargesIdLabelJSON = new JSONObject();
JSONObject addChargesLabelIdJSON=new JSONObject();

termTypes.each{ eachItem ->
	if(considerTerms.contains(eachItem.termTypeId)){
		JSONObject newObj = new JSONObject();
		newObj.put("termTypeId",eachItem.termTypeId);
		newObj.put("amount", "");
		additionalChargesJSON.add(newObj);
		addChargesIdLabelJSON.put(eachItem.termTypeId, eachItem.description+" [ "+eachItem.termTypeId +"]");
		addChargesLabelIdJSON.put(eachItem.description+" [ "+eachItem.termTypeId+"]", eachItem.termTypeId);
	}
}
context.additionalChargesJSON = additionalChargesJSON;
context.addChargesIdLabelJSON = addChargesIdLabelJSON;
context.addChargesLabelIdJSON = addChargesLabelIdJSON;

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




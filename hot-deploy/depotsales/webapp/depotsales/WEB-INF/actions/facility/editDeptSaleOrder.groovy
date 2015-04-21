
import in.vasista.vbiz.purchase.PurchaseStoreServices;
import org.ofbiz.party.party.PartyHelper;

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

//Debug.log("================== hii******** welcome** to** new** groovy====================");


partyId = parameters.partyId;

context.partyId=parameters.partyId;

partyPostalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null,null,null, false);
if(partyPostalAddress){
	partyPostalAddress = EntityUtil.getFirst(partyPostalAddress);
	partyAddress = partyPostalAddress.address1;
	context.partyAddress = partyAddress;
}
context.partyName =PartyHelper.getPartyName(delegator,partyId, false);

orderEditParamMap = [:];


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

effDateDayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
Debug.log("orderId====================="+orderId);
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
//Debug.log("orderHeader====================="+orderHeader);
if(UtilValidate.isNotEmpty(orderHeader)){
	effDateDayBegin=orderHeader.estimatedDeliveryDate;
}
partyId = parameters.partyId;
orderTaxType = parameters.orderTaxType;
packingType = parameters.packingType;

conditionList = [];

conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));

/*conditionList.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, salesChannel));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, null));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, effDateDayBegin));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, effDateDayEnd));*/
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
		context.effectiveDate=UtilDateTime.toDateString(orderHeaderTemp.estimatedDeliveryDate, "dd MMMMM, yyyy");
		context.productStoreId=orderHeaderTemp.productStoreId;
	}
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId));
	/*conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));*/
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
	batchNumberAttr = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "batchNumber"));
	daysToStoreAttr = EntityUtil.filterByCondition(orderItemAttr, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "daysToStore"));
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, updateOrderId), null, UtilMisc.toList("-orderItemSeqId"), null, false);
	
	
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
		newObj.put("prevQuantity",eachItem.quantity);
		newObj.put("batchNo", batchNo);
		newObj.put("daysToStore", daysToStore);
		newObj.put("unitPrice", eachItem.unitListPrice);
		if(changeFlag && changeFlag == "DepotSales" || changeFlag == "EditDepotSales" || changeFlag == "InterUnitTransferSale"){
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
			if(eachItem.tcsAmount){
				newObj.put("tcsPrice", eachItem.tcsAmount);
			}else{
				newObj.put("tcsAmount", 0);
			}
			//adding perecenatges
			if(eachItem.bedPercent){newObj.put("bedPercent", eachItem.bedPercent); }else{ newObj.put("bedPercent", 0); }
			
			if(eachItem.vatPercent){newObj.put("vatPercent", eachItem.vatPercent); }else{ newObj.put("vatPercent", 0); }
			
			if(eachItem.cstPercent){newObj.put("cstPercent", eachItem.cstPercent); }else{ newObj.put("cstPercent", 0); }
			
			if(eachItem.tcsPercent){newObj.put("tcsPercent", eachItem.tcsPercent); }else{ newObj.put("tcsPercent", 0); }
			
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
	//Debug.log("orderAdjustmentJSON===="+orderAdjustmentJSON+"===and orderId="+updateOrderId);
	
	
	//getting productPrices
	prodList=[];
	productCatageoryId = "BYPROD";
	 if(UtilValidate.isNotEmpty(productCatageoryId)){
			prodList= ProductWorker.getProductsByCategory(delegator ,productCatageoryId ,null);
	   }
	Map inputProductRate = FastMap.newInstance();
		inputProductRate.put("partyId",partyId);
		inputProductRate.put("userLogin",userLogin);
		inputProductRate.put("priceDate",effDateDayBegin);
		inputProductRate.put("productList",prodList);
		//inputProductRate.put("productCategoryId", productCatageoryId);
		if(orderTaxType){
			if(orderTaxType == "INTRA"){
				inputProductRate.put("geoTax", "VAT");
			}
			else{
				inputProductRate.put("geoTax", "CST");
			}
		}
		if(packingType){
			inputProductRate.put("productPriceTypeId", packingType);
		}
		priceResultMap = [:];
		priceResultMap = ByProductNetworkServices.getStoreProductPricesByDate(delegator, dctx.getDispatcher(), inputProductRate);
	
	
	productIds = EntityUtil.getFieldListFromEntityList(prodList, "productId", true);
	/*Map result = (Map)MaterialHelperServices.getProductUOM(delegator, productIds);
	uomLabelMap = result.get("uomLabel");
	productUomMap = result.get("productUom");*/
	prodPriceMap=[:];
	prodPriceMap = (Map)priceResultMap.get("priceMap");
	conversionResult = ByProductNetworkServices.getProductQtyConversions(dctx, UtilMisc.toMap("productList", prodList, "userLogin", userLogin));
	conversionMap = conversionResult.get("productConversionDetails");
	if(conversionMap){
		Iterator prodConvIter = conversionMap.entrySet().iterator();
		JSONObject conversionJSON = new JSONObject();
		while (prodConvIter.hasNext()) {
			Map.Entry entry = prodConvIter.next();
			productId = entry.getKey();
			convDetail = entry.getValue();
			
			Iterator detailIter = convDetail.entrySet().iterator();
			JSONObject conversionDetailJSON = new JSONObject();
			while (detailIter.hasNext()) {
				Map.Entry entry1 = detailIter.next();
				attrName = entry1.getKey();
				attrValue = entry1.getValue();
				conversionDetailJSON.put(attrName,attrValue);
			}
			conversionJSON.put(productId, conversionDetailJSON);
		}
		context.conversionJSON = conversionJSON;
	}
	JSONObject productUOMJSON = new JSONObject();
	JSONObject uomLabelJSON=new JSONObject();
	
	JSONArray productItemsJSON = new JSONArray();
	JSONObject productIdLabelJSON = new JSONObject();
	JSONObject productLabelIdJSON=new JSONObject();
	context.productList = prodList;
	prodList.each{eachItem ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.productId);
		newObj.put("label","[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName);
		productItemsJSON.add(newObj);
		productIdLabelJSON.put(eachItem.productId, eachItem.description);
		productLabelIdJSON.put("[" +eachItem.productId+"] " +eachItem.description+"-"+eachItem.internalName, eachItem.productId);
		
		/*if(productUomMap){
			uomId = productUomMap.get(eachItem.productId);
			if(uomId){
				productUOMJSON.put(eachItem.productId, uomId);
				uomLabelJSON.put(uomId, uomLabelMap.get(uomId));
			}
		}*/
		
		
	}
	context.productUOMJSON = productUOMJSON;
	context.uomLabelJSON = uomLabelJSON;
	
	productPrices = [];
	
	JSONObject productCostJSON = new JSONObject();
	productCostJSON=prodPriceMap;
	JSONObject prodIndentQtyCat = new JSONObject();
	JSONObject qtyInPieces = new JSONObject();
	//Debug.log("==productItemsJSON=================>"+productItemsJSON);
	context.productItemsJSON = productItemsJSON;
	context.productIdLabelJSON = productIdLabelJSON;
	context.productCostJSON = productCostJSON;
	context.productLabelIdJSON = productLabelIdJSON;
	
	//adding order adjustments
	orderAdjTypes = delegator.findList("OrderAdjustmentType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALE_ORDER_ADJUSTMNT"), null, null, null, false);
	
	JSONArray orderAdjItemsJSON = new JSONArray();
	JSONObject orderAdjLabelJSON = new JSONObject();
	JSONObject orderAdjLabelIdJSON=new JSONObject();
	orderAdjTypes.each{eachItem ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachItem.orderAdjustmentTypeId);
		newObj.put("label",eachItem.description +" [ " +eachItem.orderAdjustmentTypeId+"]");
		orderAdjItemsJSON.add(newObj);
		orderAdjLabelJSON.put(eachItem.orderAdjustmentTypeId, eachItem.description);
		orderAdjLabelIdJSON.put(eachItem.description +" [ " +eachItem.orderAdjustmentTypeId+"]", eachItem.orderAdjustmentTypeId);
	}
	//Debug.log("orderAdjLabelIdJSON===="+orderAdjLabelIdJSON+"===and orderId="+updateOrderId);
	context.orderAdjItemsJSON = orderAdjItemsJSON;
	context.orderAdjLabelJSON = orderAdjLabelJSON;
	context.orderAdjLabelIdJSON = orderAdjLabelIdJSON;
	
}
context.dataJSON = orderItemsJSON;
context.data2JSON = orderAdjustmentJSON;





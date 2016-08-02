
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
import org.ofbiz.party.contact.ContactMechWorker;


conditionList=[];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
partyOrders = delegator.findList("OrderRole", expr, null, null, null, false);
orderItemDetails = delegator.findList("OrderItemDetail", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId), null, null, null, false);

orderType="direct";
onbehalfof = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ON_BEHALF_OF"));
if(onbehalfof){
	orderType = "onbehalfof";
}
context.orderType=orderType;
JSONObject OrderItemUIJSON = new JSONObject();
JSONArray orderItemsJSON = new JSONArray();

if(UtilValidate.isNotEmpty(result.listIt)){
	list=result.listIt;
	resultList = [];
	GenericValue eachItem = null;
	//productIds = EntityUtil.getFieldListFromEntityList(list, "productId", true);
	
	//products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	int i=0;
	while ((eachItem=list.next()) != null) {
	prodDetail =  context.product = delegator.findOne("Product",[productId:eachItem.productId],true);
	if(i==0){
		catType="";
		productCategorySelect = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId), null, null, null, false);
		productCategorySelectIds = EntityUtil.getFieldListFromEntityList(productCategorySelect, "productCategoryId", true);
		
		JSONArray productCategoryJSON = new JSONArray();
		category="";
		productCategorySelectIds.each{eachCatId ->
			category=eachCatId;
			productCategoryJSON.add(eachCatId);
		}
		if(category.contains("SILK") || category.contains("TUSSAR")){
			catType="Silk";
		}else if(category.contains("COTTON") || category.contains("HANK")){
			catType="Cotton";
		}else{
			catType="other";
		}
		context.catType=catType;
		i=i+1;
	}
	
	JSONObject newObj = new JSONObject();
	remarks="";
	uom="";
	baleQty=0;
	bundleWght=0;
	bundleUnitPrice=0;
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
	orderItemDtl = EntityUtil.filterByCondition(orderItemDetails, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
	if(UtilValidate.isNotEmpty(orderItemDtl)){
		remarks = (orderItemDtl.get(0)).get("remarks");
		uom = (orderItemDtl.get(0)).get("Uom");
		baleQty = (orderItemDtl.get(0)).get("baleQuantity");
		bundleWght=(orderItemDtl.get(0)).get("bundleWeight");
		bundleUnitPrice=(orderItemDtl.get(0)).get("bundleUnitPrice");
		if(uom==null){
			uom="KGs";
		}
		
	}
	quantity = 0;
	quantity = eachItem.quantity;
	exprCondList=[];
	exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, parameters.orderId));
	exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	orderAssc = delegator.findList("OrderAssoc", EntityCondition.makeCondition(exprCondList, EntityOperator.AND), null, null, null, false);
	if(UtilValidate.isNotEmpty(orderAssc)){
		poOrderId = EntityUtil.getFirst(orderAssc).orderId;
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["SR_RECEIVED","SR_ACCEPTED"]));
		shipmentReceipts = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(shipmentReceipts)){
			shipmentQty = 0;
			shipmentReceipts.each{ eachShipment->
				shipmentQty += eachShipment.quantityAccepted;
			}
			quantity = quantity-shipmentQty;
		}
	}
		/*partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", wieverId, "partyIdentificationTypeId", "PSB_NUMER"), false);
		if(partyIdentification){
			psbNo = partyIdentification.get("idValue");
		}
		wieverName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, wieverId, false);*/
	
	
//	newObj.put("customerName",wieverName+"["+psbNo+"]");
//	newObj.put("customerId",wieverId);
	newObj.put("orderItemSeqId",eachItem.orderItemSeqId);	
	newObj.put("remarks",remarks);
//	newObj.put("psbNumber",psbNo);
	newObj.put("cProductId",eachItem.productId);
	newObj.put("cProductName",prodDetail.description +" [ "+prodDetail.brandName+"]");
	newObj.put("baleQuantity",baleQty);
	newObj.put("bundleunitPrice",bundleUnitPrice);
	newObj.put("cottonUom",uom);
	newObj.put("bundleWeight",bundleWght);
	newObj.put("quantity",eachItem.quantity);
	newObj.put("balQuantity",quantity);
	newObj.put("unitPrice",eachItem.unitPrice);
	amount=eachItem.unitPrice*quantity;
	newObj.put("amount", amount);
	orderItemsJSON.add(newObj);
	if(OrderItemUIJSON.get(eachItem.productId)){
		JSONObject existsObj = new JSONObject();
		existsObj=OrderItemUIJSON.get(eachItem.productId);
		//existsObj["quantity"]=existsObj.get("quantity")+eachItem.quantity;
		//existsObj["baleQuantity"]=existsObj.get("baleQuantity")+baleQty;
		OrderItemUIJSON.put(eachItem.productId, existsObj);
		
	}else{
		OrderItemUIJSON.put(eachItem.productId, newObj);
	}
	}
	
}
context.dataJSON = orderItemsJSON;

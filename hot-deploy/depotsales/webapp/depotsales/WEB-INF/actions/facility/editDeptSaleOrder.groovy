import org.ofbiz.base.util.*;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilNumber;
import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.ofbiz.service.ServiceUtil;

import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;

import org.ofbiz.product.product.ProductWorker;

import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.purchase.PurchaseStoreServices;
import org.ofbiz.party.party.PartyHelper;

Debug.log("================== hii******** welcome** to** new** groovy====================");



orderEditParamMap = [:];
Debug.log("orderId====================="+orderId);
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
Debug.log("orderHeader====================="+orderHeader);

if(orderHeader && orderHeader.statusId == "ORDER_CREATED"){
	
	orderInfoDetail = [:];
	conditionList = [];
	orderInfoDetail.putAt("orderId", orderHeader.orderId);
	orderInfoDetail.putAt("orderName", orderHeader.orderName);
	orderInfoDetail.putAt("orderDate", UtilDateTime.toDateString(orderHeader.orderDate, "dd MMMMM, yyyy"));
	orderInfoDetail.putAt("orderTypeId", orderHeader.orderTypeId);
	estDeliveryDate = "";
	if(orderHeader.estimatedDeliveryDate){
		estDeliveryDate = UtilDateTime.toDateString(orderHeader.estimatedDeliveryDate, "dd MMMMM, yyyy")
	}
	orderInfoDetail.putAt("estimatedDeliveryDate", estDeliveryDate);
	orderInfoDetail.putAt("PONumber", orderHeader.externalId);
	Debug.log("orderInfoDetail==================="+orderInfoDetail);
}
conditionList=[];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER")));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderRoles = delegator.findList("OrderRole", condition, null, null, null, false);
//orderRole = EntityUtil.getFirst(orderRoles);
if(orderRoles){
	roleCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SHIP_TO_CUSTOMER")],EntityOperator.AND);
	orderRole=EntityUtil.filterByCondition(orderRoles,roleCondition);
	Debug.log("orderRole===================="+orderRole);
	supplierRole = EntityUtil.getFirst(orderRole);
	Debug.log("supplierRole===================="+supplierRole);
	
	if(supplierRole){
	partyName = PartyHelper.getPartyName(delegator, supplierRole.partyId, false);
	Debug.log("partyName===================="+partyName);
	Debug.log("partyId===================="+supplierRole.partyId);
	}
	
}

orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);

products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
JSONArray orderItemsJSON = new JSONArray();
orderItems.each{ eachItem ->
	amount = eachItem.quantity*eachItem.unitPrice;
	if(!amount){
		amount = 0;
	}
	
	bedTaxPercent = 0;
	if(eachItem.bedPercent){
		bedCompare = (eachItem.bedPercent).setScale(6);
		condList = [];
		condList.add(EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, "EXCISE_DUTY_PUR"));
		condList.add(EntityCondition.makeCondition("componentRate", EntityOperator.EQUALS, bedCompare));
		cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
		
		taxComponent = delegator.findList("OrderTaxTypeAndComponentMap", cond, null, null, null, false);
		taxComponent = EntityUtil.filterByDate(taxComponent, UtilDateTime.nowTimestamp());
		
		if(taxComponent){
			bedTaxPercent = (BigDecimal)(EntityUtil.getFirst(taxComponent)).get("taxRate");
		}
		
	}
	
	prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
	prodDetail = EntityUtil.getFirst(prodDetails);
	JSONObject newObj = new JSONObject();
	newObj.put("cProductId",eachItem.productId);
	newObj.put("cProductName", prodDetail.brandName+" [ "+prodDetail.description +"]("+prodDetail.internalName+")");
	newObj.put("quantity",eachItem.quantity);
	newObj.put("unitPrice",eachItem.unitPrice);
	newObj.put("amount", amount);
	if(eachItem.bedPercent){
		newObj.put("bedPercent", bedTaxPercent);
	}
	else{
		newObj.put("bedPercent", 0);
	}
	//newObj.put("bedPercent", eachItem.bedPercent);
	newObj.put("cstPercent", eachItem.cstPercent);
	newObj.put("vatPercent", eachItem.vatPercent);
	orderItemsJSON.add(newObj);
}
Debug.log("orderItemsJSON========================="+orderItemsJSON);
context.put("orderItemsJSON", orderItemsJSON);
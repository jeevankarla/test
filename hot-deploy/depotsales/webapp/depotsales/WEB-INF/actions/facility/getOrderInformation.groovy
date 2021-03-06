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
import org.ofbiz.party.party.PartyHelper;


import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;

orderId = parameters.orderId;

orderItems = delegator.findList("OrderItemDetail", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);

conditionList = [];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
//conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

/*conditionList = [];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "quotaQty"));
orderAttributes = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
OrderItemRemarks = delegator.findList("OrderItemAttribute", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
*/

condList=[];
condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
expr = EntityCondition.makeCondition(condList, EntityOperator.AND);
partyOrders = delegator.findList("OrderRole", expr, null,null,null, false);

supplierdetails = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));

supplierPartyId="";
if(supplierdetails){	
	supplierPartyId=supplierdetails[0].get("partyId");
}
supplierpartyName="";
if(supplierPartyId){
	supplierpartyName = PartyHelper.getPartyName(delegator, supplierPartyId, false);
}
context.supplierpartyName=supplierpartyName;


orderType="direct";
onbehalfof = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ON_BEHALF_OF"));
if(onbehalfof){
	orderType = "onbehalfof";
}
JSONArray orderInformationDetails = new JSONArray();

for (eachItem in orderItems) {
	
	adjustmentAmount = 0;
	otherCharges = 0;
	quotaAvbl = 0;
	if(UtilValidate.isNotEmpty(orderAdjustments)){
			adjustmentAmount =eachItem.discountAmount;
		conditionList = [];
		if("direct".equals(orderType)){
		conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		}
			conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_IN, ["TEN_PERCENT_SUBSIDY","PRICE_DISCOUNT"]));
			otherChargesList = EntityUtil.filterByCondition(orderAdjustments, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "PRICE_DISCOUNT"));
			otherChargesList1 = EntityUtil.filterByCondition(orderAdjustments, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
		
		
		conditionList.clear();
		if("direct".equals(orderType)){
		conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		}
		
		/*if(orderType == "onbehalfof"){
			conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
		otherChargesListTen = EntityUtil.filterByCondition(orderAdjustments, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
		 
		if(otherChargesListTen)
		adjustmentAmount =otherChargesListTen[0].amount;
		
	}*/
		
		
		
		for(int i=0; i<otherChargesList.size(); i++){
			eachAdj = otherChargesList.get(i);
			if(UtilValidate.isNotEmpty(eachAdj.get("amount"))){
				otherCharges += eachAdj.get("amount");
			}
		}
		
		for(int i=0; i<otherChargesList1.size(); i++){
			eachAdjs = otherChargesList1.get(i);
			if(UtilValidate.isNotEmpty(eachAdjs.get("amount"))){
				adjustmentAmount += eachAdjs.get("amount");
			}
		}
		
	}
	
	quotaAvbl=eachItem.quotaQuantity;
	/*if(UtilValidate.isNotEmpty(orderAttributes)){
		orderItemAttributes = EntityUtil.filterByCondition(orderAttributes, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
		if(UtilValidate.isNotEmpty(orderItemAttributes)){
			quotaAvbl = (orderItemAttributes.get(0)).get("attrValue");
		}
	}*/
	remarks = "";
	if(eachItem.remarks){
		remarks=eachItem.remarks;
	}
	/*if(UtilValidate.isNotEmpty(OrderItemRemarks)){
		orderRemarks = EntityUtil.filterByCondition(OrderItemRemarks, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachItem.orderItemSeqId));
	
		if(UtilValidate.isNotEmpty(orderRemarks)){
			remarks = (orderRemarks.get(0)).get("attrValue");
		}
	}*/
	
	
	JSONObject orderDetail = new JSONObject();
	orderDetail.put("productId", eachItem.productId);
	GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",eachItem.productId),false);
	String	desc=(String)product.get("description");
	
	String partyName = PartyHelper.getPartyName(delegator, eachItem.partyId, false);
	
	passNo = "";
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachItem.partyId));
	conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
	if(PartyIdentificationList){
	passNo = PartyIdentificationList[0].get("idValue");
	}
	
	
	orderDetail.put("orderType",orderType);
	orderDetail.put("passNo",passNo);
	orderDetail.put("partyName",partyName);
	orderDetail.put("supplierpartyName",supplierpartyName);
	orderDetail.put("prductName",desc);
	orderDetail.put("quantity", eachItem.quantity);
	orderDetail.put("unitPrice", eachItem.unitPrice.setScale(2,0));
	orderDetail.put("itemAmt", ((eachItem.quantity.setScale(2,0))*(eachItem.unitPrice.setScale(2,0))));
	orderDetail.put("statusId", eachItem.statusId);
	orderDetail.put("adjustmentAmount", adjustmentAmount);
	orderDetail.put("otherCharges", otherCharges);
	orderDetail.put("quotaAvbl", quotaAvbl);
	orderDetail.put("remarks", remarks);

	orderDetail.put("payableAmt", (((eachItem.quantity).setScale(2,0))*((eachItem.unitPrice).setScale(2,0)) + adjustmentAmount + otherCharges));
	orderInformationDetails.add(orderDetail);
	   
}


request.setAttribute("orderInformationDetails", orderInformationDetails);
return "success";


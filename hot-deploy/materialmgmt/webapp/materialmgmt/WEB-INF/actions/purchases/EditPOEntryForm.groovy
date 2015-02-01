
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
import in.vasista.vbiz.purchase.PurchaseStoreServices;
import org.ofbiz.party.party.PartyHelper;

orderEditParamMap = [:];
orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
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
	
	orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	fileNo = "";
	refNo = "";
	orderAttr.each{ eachAttr ->
		if(eachAttr.attrName == "FILE_NUMBER"){
			fileNo =  eachAttr.attrValue;
		}
		if(eachAttr.attrName == "REF_NUMBER"){
			refNo = eachAttr.attrValue;
		}
	}
	orderInfoDetail.putAt("fileNo", fileNo);
	orderInfoDetail.putAt("refNo", refNo);
	
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER_AGENT"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	orderRoles = delegator.findList("OrderRole", condition, null, null, null, false);
	orderRole = EntityUtil.getFirst(orderRoles);
	if(orderRole){
		partyName = PartyHelper.getPartyName(delegator, orderRole.partyId, false);
		orderInfoDetail.putAt("supplierId", orderRole.partyId);
		orderInfoDetail.putAt("supplierName", partyName);
	}
	/*condList=[];
	condList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	condList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	orderPartys = delegator.findList("OrderRole", cond, null, null, null, false);
	orderParty = EntityUtil.getFirst(orderPartys);
	if(orderParty){
		orderInfoDetail.putAt("billToPartyId", orderParty.partyId);
		
	}*/
	orderEditParamMap.putAt("orderHeader", orderInfoDetail);
	orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	orderAdjDetail = [:];
	orderAdjustments.each{ eachAdj ->
		if(eachAdj.orderAdjustmentTypeId == "COGS_FREIGHT"){
			orderAdjDetail.putAt("freightCharges", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_DISC"){
			orderAdjDetail.putAt("discount", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_INSURANCE"){
			orderAdjDetail.putAt("insurence", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_PCK_FWD"){
			orderAdjDetail.putAt("packAndFowdg", eachAdj.amount);
		}
		if(eachAdj.orderAdjustmentTypeId == "COGS_OTH_CHARGES"){
			orderAdjDetail.putAt("otherCharges", eachAdj.amount);
		}
		
	}
	orderEditParamMap.put("orderAdjustment", orderAdjDetail);
	
	orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	
	quoteNo = EntityUtil.getFirst(orderItems);
	quoteDetailMap = [:];
	quoteDate = delegator.findOne("Quote", UtilMisc.toMap("quoteId", quoteNo.quoteId), false);
		if(quoteNo)
		 {
		quoteDetailMap.putAt("quoteId", quoteNo.quoteId);
		 }
		if(quoteDate)
		 {
		quoteDetailMap.putAt("quoteIssueDate", UtilDateTime.toDateString(quoteDate.issueDate, "dd MMMMM, yyyy"));
		 }
	orderEditParamMap.put("quoteDetails", quoteDetailMap);
	
	productIds = EntityUtil.getFieldListFromEntityList(orderItems, "productId", true);
	
	products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	
	JSONArray orderItemsJSON = new JSONArray();
	orderItems.each{ eachItem ->
		amount = eachItem.quantity*eachItem.unitPrice;
		if(!amount){
			amount = 0;
		}
		prodDetails = EntityUtil.filterByCondition(products, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId));
		prodDetail = EntityUtil.getFirst(prodDetails);
		JSONObject newObj = new JSONObject();
		newObj.put("cProductId",eachItem.productId);
		newObj.put("cProductName", prodDetail.brandName +" [ " +prodDetail.description+"]");
		newObj.put("quantity",eachItem.quantity);
		newObj.put("UPrice",eachItem.unitPrice);
		newObj.put("amount", amount);
		newObj.put("ExcisePercent",eachItem.bedPercent);
		newObj.put("Excise",eachItem.bedAmount);
		newObj.put("CSTPercent", eachItem.cstPercent);
		newObj.put("CST", eachItem.cstAmount);
		newObj.put("VatPercent", eachItem.vatPercent);
		newObj.put("VAT", eachItem.vatAmount);
		newObj.put("bedCessPercent", eachItem.bedcessPercent);
		newObj.put("bedCessAmount", eachItem.bedcessAmount);
		newObj.put("bedSecCessPercent", eachItem.bedseccessPercent);
		newObj.put("bedSecCessAmount", eachItem.bedseccessAmount);
		orderItemsJSON.add(newObj);
	}
	context.put("orderItemsJSON", orderItemsJSON);
	quoteIds = EntityUtil.getFieldListFromEntityList(orderItems, "quoteId", true);
	
	termTypes = delegator.findList("TermType", null, null, null, null, false);
	
	paymentTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "FEE_PAYMENT_TERM"));
	paymentTermTypeIds = EntityUtil.getFieldListFromEntityList(paymentTermTypes, "termTypeId", true);

	deliveryTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DELIVERY_TERM"));
	deliveryTermTypeIds = EntityUtil.getFieldListFromEntityList(deliveryTermTypes, "termTypeId", true);
	
	otherTermTypes = EntityUtil.filterByCondition(termTypes, EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "OTHERS"));
	otherTermTypeIds = EntityUtil.getFieldListFromEntityList(otherTermTypes, "termTypeId", true);
	
	orderTerms = [:];
	List<GenericValue> terms = [];
	terms = delegator.findList("OrderTerm", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	if(quoteIds && !terms){
		String quoteId = quoteIds.get(0);
		terms = delegator.findList("QuoteTerm", EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, quoteId), null, null, null, false);
	}
	
	termIds = EntityUtil.getFieldListFromEntityList(terms, "termTypeId", true);
	
	termTypesForDesc = delegator.findList("TermType", EntityCondition.makeCondition("termTypeId", EntityOperator.IN, termIds), null, null, null, false);
	
	termDescMap = [:];
	termTypesForDesc.each{ eachTermItem ->
		termDescMap.put(eachTermItem.termTypeId, eachTermItem.description);
	}
	
	paymentTerms = [];
	deliveryTerms = [];
	otherTerms = [];
	terms.each{ eachTerm ->
		termMap = [:];
		termMap.put("termTypeId", eachTerm.termTypeId);
		termMap.put("termTypeDescription", termDescMap.get(eachTerm.termTypeId));
		termMap.put("termValue", eachTerm.termValue);
		termMap.put("termDays", eachTerm.termDays);
		termMap.put("description", eachTerm.description);
		termMap.put("uomId", eachTerm.uomId);
		if(paymentTermTypeIds.contains(eachTerm.termTypeId)){
			paymentTerms.add(termMap);
		}
		if(deliveryTermTypeIds.contains(eachTerm.termTypeId)){
			deliveryTerms.add(termMap);
		}
		
		if(otherTermTypeIds.contains(eachTerm.termTypeId)){
			otherTerms.add(termMap);
		}
		
	}
	orderTerms.put("paymentTerms", paymentTerms);
	orderTerms.put("deliveryTerms", deliveryTerms);
	orderTerms.put("otherTerms", otherTerms);
	
	orderEditParamMap.putAt("orderTerms", orderTerms);
}
context.orderEditParam = orderEditParamMap;

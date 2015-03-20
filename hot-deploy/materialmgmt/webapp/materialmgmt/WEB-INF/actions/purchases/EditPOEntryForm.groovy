
import org.ofbiz.base.util.*;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

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
purchaseTaxFinalDecimals = UtilNumber.getBigDecimalScale("purchaseTax.final.decimals");
purchaseTaxCalcDecimals = UtilNumber.getBigDecimalScale("purchaseTax.calc.decimals");
purchaseTaxRounding = UtilNumber.getBigDecimalRoundingMode("purchaseTax.rounding");

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
	thruDate = "";
	fromDate = "";
	orderAttr.each{ eachAttr ->
		if(eachAttr.attrName == "FILE_NUMBER"){
			fileNo =  eachAttr.attrValue;
		}
		if(eachAttr.attrName == "REF_NUMBER"){
			refNo = eachAttr.attrValue;
		}
		if(eachAttr.attrName == "VALID_FROM"){
			fromDate = eachAttr.attrValue;
		}
		if(eachAttr.attrName == "VALID_THRU"){
			thruDate = eachAttr.attrValue;
		}
	}
	orderInfoDetail.putAt("fileNo", fileNo);
	orderInfoDetail.putAt("refNo", refNo);
	orderInfoDetail.putAt("validFromDate", fromDate);
	orderInfoDetail.putAt("validThruDate", thruDate);
	
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SUPPLIER_AGENT", "BILL_FROM_VENDOR")));
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
	
	termIds = EntityUtil.getFieldListFromEntityList(terms, "termTypeId", true);
	if(termIds.contains("INC_TAX")){
		context.includeTax="Y";
	}
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
		termMap.put("sequenceId", eachTerm.orderItemSeqId);
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
	if(otherTerms){
		context.termExists = "Y";
	}
	
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
//		newObj.put("bedPercent", eachItem.bedPercent);
		newObj.put("cstPercent", eachItem.cstPercent);
		newObj.put("vatPercent", eachItem.vatPercent);
		orderItemsJSON.add(newObj);
	}
	context.put("orderItemsJSON", orderItemsJSON);
	
	JSONArray orderAdjustmentJSON = new JSONArray();
	
	otherTerms.each{ eachOtherTerm ->
		
		sequenceId = eachOtherTerm.sequenceId;
		if(!(sequenceId == "_NA_")){
			
			sequenceItem = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachOtherTerm.sequenceId));
			if(sequenceItem){
				prodId = (EntityUtil.getFirst(sequenceItem)).productId;
				sequenceId = productIdLabelJSON.get(prodId);
			}
		}
		else{
			sequenceId = "ALL"
		}
		JSONObject newObj = new JSONObject();
		newObj.put("adjustmentTypeId",eachOtherTerm.termTypeId);
		newObj.put("applicableTo", sequenceId);
		newObj.put("adjValue",eachOtherTerm.termValue);
		newObj.put("uomId", eachOtherTerm.uomId);
		newObj.put("termDays", eachOtherTerm.termDays);
		newObj.put("description", eachOtherTerm.description);
		orderAdjustmentJSON.add(newObj);
	}
	context.put("orderAdjustmentJSON", orderAdjustmentJSON);
	
	orderTerms.put("paymentTerms", paymentTerms);
	orderTerms.put("deliveryTerms", deliveryTerms);
	
	orderEditParamMap.putAt("orderTerms", orderTerms);
}
context.orderEditParam = orderEditParamMap;

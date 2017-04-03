import java.util.*;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.MathContext;

import org.ofbiz.base.util.UtilNumber;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.party.party.PartyHelper;


import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.finder.EntityFinderUtil.ConditionList;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

invoiceId = parameters.invoiceId;

purchaseInvoiceMap=[:];
invoiceDetails=[:];
purchaseInvoiceItemList=[];
purchaseInvoiceAdjustmtsMap=[:];
branchContext=[:];
supplierDetailsMap=[:];
purchasePartyDetailsMap=[:];
invoiceDetails = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);

shipmentId=invoiceDetails.get("shipmentId");
partyId=invoiceDetails.get("partyId");

shipmentDetails = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);

conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
partyIdentifications = delegator.findList("PartyIdentification", condition, UtilMisc.toSet("partyIdentificationTypeId","idValue"), null, null, false);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
partyContactDetails = delegator.findList("PartyContactDetailByPurpose", condition, UtilMisc.toSet("contactMechPurposeTypeId","contactMechTypeId","toName","contactNumber","postalCode","address1"), null, null, false);

partyLocationDetails = EntityUtil.filterByCondition(partyContactDetails, EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"));
partytTelecomDetails = EntityUtil.filterByCondition(partyContactDetails, EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER"));

if(UtilValidate.isNotEmpty(partyLocationDetails)){
	partyLocationDetail=EntityUtil.getFirst(partyLocationDetails)
	purchasePartyDetailsMap.put("RoName", partyLocationDetail.toName)
	purchasePartyDetailsMap.put("address", partyLocationDetail.address1)
	purchasePartyDetailsMap.put("postalCode", partyLocationDetail.postalCode)
	purchasePartyDetailsMap.put("contactNumber", partyLocationDetail.contactNumber)
}

if(UtilValidate.isNotEmpty(partytTelecomDetails)){
	partytTelecomDetail=EntityUtil.getFirst(partytTelecomDetails)
	purchasePartyDetailsMap.put("telecomNumber", partytTelecomDetail.contactNumber)
}
if(UtilValidate.isNotEmpty(partyIdentifications)){
	for(partyIdentification in partyIdentifications){
		if("CIN_NUMBER".equals(partyIdentification.partyIdentificationTypeId)){
			purchasePartyDetailsMap.put("CIN_NUMBER", partyIdentification.idValue)
		}
		if("CST_NUMBER".equals(partyIdentification.partyIdentificationTypeId)){
			purchasePartyDetailsMap.put("CST_NUMBER", partyIdentification.idValue)
		}
		if("PAN_NUMBER".equals(partyIdentification.partyIdentificationTypeId)){
			purchasePartyDetailsMap.put("PAN_NUMBER", partyIdentification.idValue)
		}
		if("TIN_NUMBER".equals(partyIdentification.partyIdentificationTypeId)){
			purchasePartyDetailsMap.put("TIN_NUMBER", partyIdentification.idValue)
		}
	}
}
branchContext.put("branchId", partyId)
BOAddress="";
BOEmail="";
try{
	resultCtx = dispatcher.runSync("getBoHeader", branchContext);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in BO Header ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}
	if(resultCtx.get("boHeaderMap")){
		boHeaderMap=resultCtx.get("boHeaderMap");
		if(boHeaderMap.get("header0")){
			BOAddress=boHeaderMap.get("header0");
		}
		if(boHeaderMap.get("header1")){
			BOEmail=boHeaderMap.get("header1");
		}
	}
}catch(GenericServiceException e){
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
context.BOAddress=BOAddress;
context.BOEmail=BOEmail;
context.purchasePartyDetailsMap=purchasePartyDetailsMap;


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoiceItemDetails = delegator.findList("InvoiceItem", condition, null, null, null, false);

invoiceItems = EntityUtil.filterByCondition(invoiceItemDetails, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_RAWPROD_ITEM"));
invoiceAdjustments = EntityUtil.filterByCondition(invoiceItemDetails, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
if(UtilValidate.isNotEmpty(invoiceItems)){
	for(invoiceItem in invoiceItems){
		tempMap=[:];
		tempList=[];
		tempMap.put("productId", invoiceItem.productId);
		productDetails = delegator.findOne("Product",[productId : invoiceItem.productId] , false);
		tempMap.put("productName", productDetails.productName);
		tempMap.put("invoiceItemSeqId", invoiceItem.invoiceItemSeqId);
		tempMap.put("quantity", invoiceItem.quantity);
		tempMap.put("unitPrice", invoiceItem.unitPrice);
		tempMap.put("amount", invoiceItem.itemValue);
		invoiceitemAdjustments = EntityUtil.filterByCondition(invoiceAdjustments, EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, invoiceItem.invoiceItemSeqId));
		for(invoiceAdjstmt in invoiceitemAdjustments){
			tempMap2=[:];
			tempMap2.put("productId", invoiceAdjstmt.productId);
			tempMap2.put("taxTerm", invoiceAdjstmt.invoiceItemTypeId);
			tempMap2.put("parentInvoiceItemSeqId", invoiceAdjstmt.parentInvoiceItemSeqId);
			tempMap2.put("taxPer", invoiceAdjstmt.sourcePercentage);
			tempMap2.put("taxAmount", invoiceAdjstmt.itemValue);
			tempList.add(tempMap2);
		}
		purchaseInvoiceAdjustmtsMap.put(invoiceItem.invoiceItemSeqId, tempList);
		purchaseInvoiceItemList.add(tempMap);
	}
}
context.purchaseInvoiceAdjustmtsMap=purchaseInvoiceAdjustmtsMap
context.purchaseInvoiceItemList=purchaseInvoiceItemList;



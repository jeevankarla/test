import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;

dctx = dispatcher.getDispatchContext();
orderId = parameters.orderId;
subject=parameters.subject;
orderDetailsList=[];
allDetailsMap=[:]; 
orderTermList=[];

allDetailsMap.put("orderId",orderId);
allDetailsMap.put("subject",subject);

allDetailsMap["total"]=BigDecimal.ZERO;

orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
productIds=orderDetails.productId;
productCategories = delegator.findList("ProductCategoryAndMember",EntityCondition.makeCondition("productId", EntityOperator.IN, productIds) , null, null, null, false );
//productCategory = EntityUtil.getFieldListFromEntityList(productCategories, "productCategoryId", true);
productDescription = EntityUtil.getFieldListFromEntityList(productCategories, "description", true);
//productParentDescription = EntityUtil.getFieldListFromEntityList(productCategories, "primaryParentCategoryId", true);
//allDetailsMap.put("productParentDescription",productParentDescription);
allDetailsMap.put("productDescription",productDescription);

//orderSequenceNO
OrderHeaderSequenceData = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(OrderHeaderSequenceData)){
OrderHeaderSequenceData=EntityUtil.getFirst(OrderHeaderSequenceData);
sequenceId=OrderHeaderSequenceData.sequenceId;
orderNo=OrderHeaderSequenceData.orderNo;
allDetailsMap.put("sequenceId",sequenceId);
allDetailsMap.put("orderNo",orderNo);
	}
//FileNo
fileNumber = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"FILE_NUMBER"],false);
if(fileNumber){
	fileNo=fileNumber.get("attrValue");
	allDetailsMap.put("fileNo",fileNo);
}
//referNumber
referNumber = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"REF_NUMBER"],false);
if(referNumber){
	refNo=referNumber.get("attrValue");
	allDetailsMap.put("refNo",refNo);
}
//Valid from 
validFrom = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"VALID_FROM"],false);
if(validFrom){
	fromDate=validFrom.get("attrValue");
	allDetailsMap.put("fromDate",fromDate);
}
//valid TO
validThru = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"VALID_THRU"],false);
if(validThru){
	thruDate=validThru.get("attrValue");
	allDetailsMap.put("thruDate",thruDate);
}

//to get product details
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderitems->
		orderDetailsMap=[:];
		orderDetailsMap["productId"]=orderitems.productId;
		orderDetailsMap["quantity"]=orderitems.quantity;
		orderDetailsMap["unitPrice"]=orderitems.unitPrice;
		orderDetailsMap["createdDate"]=orderitems.createdDate;
		
		
		amount=((orderitems.quantity)*(orderitems.unitPrice));
		orderDetailsMap["amount"]=amount;
		allDetailsMap["total"]+=amount;
		
		orderDetailsList.addAll(orderDetailsMap);
		
	}
}

// partyId,partyName
if(UtilValidate.isNotEmpty(orderId)){
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
	vendorDetail=EntityUtil.getFirst(vendorDetails);
	if(UtilValidate.isNotEmpty(vendorDetail)){
	partyId=vendorDetail.partyId;
	allDetailsMap.put("partyId",partyId);
		partyName =  PartyHelper.getPartyName(delegator, partyId, false);
		allDetailsMap.put("partyName",partyName);
	  }
	}
// party Address
List conlist=[];
conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
vendorDetail=EntityUtil.getFirst(vendorDetails);
fromPartyId="";
if(UtilValidate.isNotEmpty(vendorDetail)){
fromPartyId=vendorDetail.partyId;
}
   if(UtilValidate.isNotEmpty(fromPartyId)){
	 partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:fromPartyId, userLogin: userLogin]);
	   address1="";address2="";city="";postalCode="";
		if (partyPostalAddress != null && UtilValidate.isNotEmpty(partyPostalAddress)) {
		   if(partyPostalAddress.address1){
	   address1=partyPostalAddress.address1;
	   allDetailsMap.put("address1",address1);
		   }
		   if(partyPostalAddress.address2){
	   address2=partyPostalAddress.address2;
	   allDetailsMap.put("address2",address2);
			}
		   if(partyPostalAddress.city){
	   city=partyPostalAddress.city;
	   allDetailsMap.put("city",city);
			}
		   if(partyPostalAddress.postalCode){
	   postalCode=partyPostalAddress.postalCode;
	   allDetailsMap.put("postalCode",postalCode);
			}
		 }
		
		partyTelephone= dispatcher.runSync("getPartyTelephone", [partyId: fromPartyId, userLogin: userLogin]);
		phoneNumber = "";
		if (partyTelephone != null && partyTelephone.contactNumber != null) {
			phoneNumber = partyTelephone.contactNumber;
		}
		allDetailsMap.put("phoneNumber", phoneNumber);
		
		partyEmail= dispatcher.runSync("getPartyEmail", [partyId: fromPartyId, userLogin: userLogin]);
		emailAddress="";
		if (partyEmail != null && partyEmail.emailAddress != null) {
			emailAddress = partyEmail.emailAddress;
		}
		allDetailsMap.put("emailAddress", emailAddress);
}
      

// orderAttributeDetails
orderAttributeDetails = delegator.findList("OrderAttribute",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );

tendorNotifiNo = EntityUtil.filterByCondition(orderAttributeDetails, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "TENDER_NO"));
tendorNotifiNo = EntityUtil.getFirst(tendorNotifiNo);
if(UtilValidate.isNotEmpty(tendorNotifiNo)){
	tendorNo=tendorNotifiNo.attrValue;
	allDetailsMap.put("tendorNo",tendorNo);
}
tendorNotifiDate = EntityUtil.filterByCondition(orderAttributeDetails, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "TENDER_DATE"));
tendorNotifiDate = EntityUtil.getFirst(tendorNotifiDate);
if(UtilValidate.isNotEmpty(tendorNotifiDate)){
	tendorDate=tendorNotifiDate.attrValue;
	allDetailsMap.put("tendorDate",tendorDate);
}
tendorTechDate = EntityUtil.filterByCondition(orderAttributeDetails, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "TECHNICAL_DATE"));
tendorTechDate = EntityUtil.getFirst(tendorTechDate);
if(UtilValidate.isNotEmpty(tendorTechDate)){
	techDate=tendorTechDate.attrValue;
	allDetailsMap.put("techDate",techDate);
}
tendorCommercialDate = EntityUtil.filterByCondition(orderAttributeDetails, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "COMMERCIAL_DATE"));
tendorCommercialDate = EntityUtil.getFirst(tendorCommercialDate);
if(UtilValidate.isNotEmpty(tendorCommercialDate)){
	commercialDate=tendorCommercialDate.attrValue;
	allDetailsMap.put("commercialDate",commercialDate);
}
tendorNegotiateDate = EntityUtil.filterByCondition(orderAttributeDetails, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "NEGOTIATION_DATE"));
tendorNegotiateDate = EntityUtil.getFirst(tendorNegotiateDate);
if(UtilValidate.isNotEmpty(tendorNegotiateDate)){
	negotiationDate=tendorNegotiateDate.attrValue;
	allDetailsMap.put("negotiationDate",negotiationDate);
}
tendorLOADate = EntityUtil.filterByCondition(orderAttributeDetails, EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "LETTER_DATE"));
tendorLOADate = EntityUtil.getFirst(tendorLOADate);
if(UtilValidate.isNotEmpty(tendorLOADate)){
	loaDate=tendorLOADate.attrValue;
	allDetailsMap.put("loaDate",loaDate);
}

context.allDetailsMap=allDetailsMap;
context.orderDetailsList=orderDetailsList;

//Debug.log("orderDetailsList=================================="+orderDetailsList);



// tax details

bedAmount=0;
vatAmount=0;
cstAmount=0;
listSize=0;
orderTerms= delegator.findList("OrderTerm",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
parentMap=[:];
if(UtilValidate.isNotEmpty(orderTerms)){
	orderTerms.each{orderTerm ->
		Amount=0;
		listSize+=1;
		ecl = EntityCondition.makeCondition([
			EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
			EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, orderTerm.termTypeId)],
		EntityOperator.AND);
		orderAdjustments= delegator.findList("OrderAdjustment",ecl , UtilMisc.toSet("orderAdjustmentTypeId","amount"), null, null, false );
		if(orderTerm.termTypeId == "BED_PUR"){
			condition = EntityCondition.makeCondition([
				EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
				EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN,UtilMisc.toList("BEDCESS_PUR","BEDSECCESS_PUR","BED_PUR") )],
			EntityOperator.AND);
			orderAdjts= delegator.findList("OrderAdjustment",condition , UtilMisc.toSet("orderAdjustmentTypeId","amount"), null, null, false );
			orderAdjts.each{orderAdjt->
				Amount+=orderAdjt.amount;
			}
			bedAmount=Amount;
		}
		
		orderAdjustment=[];
		if(UtilValidate.isNotEmpty(orderAdjustments)){
		orderAdjustment = EntityUtil.getFirst(orderAdjustments);
		}
		
		if(orderTerm.termTypeId == "VAT_PUR"){
			vatAmount=orderAdjustment.amount;
		}
		if(orderTerm.termTypeId == "CST_PUR"){
			cstAmount=orderAdjustment.amount;
		}
	GenericValue termTypes= delegator.findOne("TermType",[termTypeId:orderTerm.termTypeId],false);
	if(UtilValidate.isEmpty(parentMap[termTypes.parentTypeId])){
		termsList=[];
		tempMap=[:];
		tempMap.put("termTypeId", orderTerm.termTypeId);
		tempMap.put("parentTypeId", termTypes.parentTypeId);
		if(UtilValidate.isNotEmpty(orderAdjustments)){
			if(Amount>0){
				tempMap.put("amount",Amount);
			}else{
			tempMap.put("amount",orderAdjustment.amount);
			}
		}
		if(UtilValidate.isNotEmpty(orderTerm.orderItemSeqId)){
			tempMap.put("orderItemSeqId", orderTerm.orderItemSeqId);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termValue)){
			tempMap.put("termValue", orderTerm.termValue);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termDays)){
			tempMap.put("termDays", orderTerm.termDays);
		}
		if(UtilValidate.isNotEmpty(orderTerm.description)){
			tempMap.put("description", orderTerm.description);
		}
		if(UtilValidate.isNotEmpty(orderTerm.uomId)){
			tempMap.put("uomId", orderTerm.uomId);
		}
		tempMap.put("termTypeDes", termTypes.description);
		termsList.add(tempMap);
		parentMap[termTypes.parentTypeId]=termsList;
		
	}else{
		tempMap=[:];
		tempMap.put("termTypeId", orderTerm.termTypeId);
		tempMap.put("parentTypeId", termTypes.parentTypeId);
		if(UtilValidate.isNotEmpty(orderAdjustments)){
			if(Amount>0){
				tempMap.put("amount",Amount);
			}else{
			tempMap.put("amount",orderAdjustment.amount);
			}
		}
		if(UtilValidate.isNotEmpty(orderTerm.orderItemSeqId)){
			tempMap.put("orderItemSeqId", orderTerm.orderItemSeqId);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termValue)){
			tempMap.put("termValue", orderTerm.termValue);
		}
		if(UtilValidate.isNotEmpty(orderTerm.termDays)){
			tempMap.put("termDays", orderTerm.termDays);
		}
		if(UtilValidate.isNotEmpty(orderTerm.description)){
			tempMap.put("description", orderTerm.description);
		}
		if(UtilValidate.isNotEmpty(orderTerm.uomId)){
			tempMap.put("uomId", orderTerm.uomId);
		}
		tempMap.put("termTypeDes", termTypes.description);
		testList = parentMap[termTypes.parentTypeId]
		testList.add(tempMap);
		parentMap[termTypes.parentTypeId]=testList;
		
		}
	
	}
}
context.parentMap=parentMap;
context.Amount=bedAmount;
context.vatAmount=vatAmount;
context.cstAmount=cstAmount;
context.listSize=listSize;









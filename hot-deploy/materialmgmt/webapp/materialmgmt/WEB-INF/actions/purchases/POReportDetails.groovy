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
import org.ofbiz.order.order.*;

dctx = dispatcher.getDispatchContext();
orderId = parameters.orderId;
orderDetailsList=[];
allDetailsMap=[:];
allDetailsMap.put("orderId",orderId);
allDetailsMap["total"]=BigDecimal.ZERO;
allDetailsMap["grandTotal"]=BigDecimal.ZERO;
orderHeader=null;
if (orderId) {
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	context.hasPermission = true;
	context.canViewInternalDetails = true;
	if(UtilValidate.isNotEmpty(orderHeader)){
	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	orderAdjustments = orderReadHelper.getAdjustments();
	grandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
	allDetailsMap["grandTotal"] = grandTotal;
 }}
// orderDate
orderHeaderDetails=orderHeader;
if(UtilValidate.isNotEmpty(orderHeader)){
   orderDate=orderHeader.orderDate;
   allDetailsMap.put("orderDate",orderDate);
 }
 

//to get company details
tinCstDetails = delegator.findList("PartyGroup",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , "Company")  , null, null, null, false );
tinDetails=EntityUtil.getFirst(tinCstDetails);
tinNumber="";cstNumber="";kstNumber="";
if(UtilValidate.isNotEmpty(tinDetails.tinNumber)){
	tinNumber=tinDetails.tinNumber;
	allDetailsMap.put("tinNumber",tinNumber);
}
if(UtilValidate.isNotEmpty(tinDetails.cstNumber)){
	cstNumber=tinDetails.cstNumber;
	allDetailsMap.put("cstNumber",cstNumber);
}
//if(UtilValidate.isNotEmpty((tinDetails.kstNumber)){
//kstNumber=kstDetails.kstNumber;
//allDetailsMap.put("kstNumber",kstNumber);
//}


//to get product details
orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderitems->
		orderDetailsMap=[:];
		orderDetailsMap["productId"]=orderitems.productId;
		
		uomDetails = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , orderitems.productId)  , null, null, null, false );
		uomDetails=EntityUtil.getFirst(uomDetails);
		uomId=uomDetails.quantityUomId;
		
		if(UtilValidate.isNotEmpty(uomId)){
			uomDesc = delegator.findList("Uom",EntityCondition.makeCondition("uomId", EntityOperator.EQUALS , uomId)  , null, null, null, false );
			uomDesc=EntityUtil.getFirst(uomDesc);
			orderDetailsMap["uomAbbr"]=uomDesc.abbreviation;
		}
		orderDetailsMap["quantity"]=orderitems.quantity;
		orderDetailsMap["unitPrice"]=orderitems.unitPrice;
		orderDetailsMap["createdDate"]=orderitems.createdDate;
		amount=((orderitems.quantity)*(orderitems.unitPrice));
		orderDetailsMap["amount"]=amount;
		allDetailsMap["total"]+=amount;
		
		orderDetailsList.addAll(orderDetailsMap);
	}
}
// to get quoteid,date and (custRequestId)enquiryid ,(custRequestDate)enquiryDate
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails=EntityUtil.getFirst(orderDetails);
	quoteId=orderDetails.quoteId;
	if(UtilValidate.isNotEmpty(quoteId)){
		QuoteDetails = delegator.findList("Quote",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
		QuoteDetails=EntityUtil.getFirst(QuoteDetails);
		qutationDate=QuoteDetails.issueDate;
		allDetailsMap.put("quoteId",quoteId);
		allDetailsMap.put("qutationDate",qutationDate);
				
		enquiryDetails = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
		enquiryDetails=EntityUtil.getFirst(enquiryDetails);
		enquiryId=enquiryDetails.custRequestId;
		enquiryDate=enquiryDetails.custRequestDate;
		allDetailsMap.put("enquiryId",enquiryId);
		allDetailsMap.put("enquiryDate",enquiryDate);
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
			
			faxId="FAX_BILLING";
			partyFaxNumber= dispatcher.runSync("getPartyTelephone", [partyId: fromPartyId, contactMechPurposeTypeId: faxId, userLogin: userLogin]);
			faxNumber = "";
			if (partyFaxNumber != null && partyFaxNumber.contactNumber != null) {
				faxNumber = partyFaxNumber.contactNumber;
			}
			allDetailsMap.put("faxNumber", faxNumber);
 }
	   
// discount,pakfwdDetails,tax
orderAdjustmentDetails = delegator.findList("OrderAdjustment",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );

discountDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "COGS_DISC"));
discountDetail = EntityUtil.getFirst(discountDetails);
if(UtilValidate.isNotEmpty(discountDetail)){
	discount=discountDetail.amount;
	allDetailsMap.put("discount",discount);
}

pakfwdDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "COGS_PCK_FWD"));
pakfwdDetails = EntityUtil.getFirst(pakfwdDetails);
if(UtilValidate.isNotEmpty(pakfwdDetails)){
   pakfwdCharges =pakfwdDetails.amount;
   allDetailsMap.put("pakfwdCharges",pakfwdCharges);
}
taxDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN , UtilMisc.toList("VAT_PUR","CST_PUR")));
allDetailsMap.put("taxDetailsList",taxDetails);

if(UtilValidate.isNotEmpty(taxDetails)){
   tax =taxDetails.amount;
   allDetailsMap.put("tax",tax);
}

// exciseDuty
orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
exciseAmt = 0;
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderDet->
		if(orderDet.bedAmount){
			exciseAmt += orderDet.bedAmount;
		}
		if(orderDet.bedcessAmount){
			exciseAmt += orderDet.bedcessAmount;
		}
		if(orderDet.bedseccessAmount){
			exciseAmt += orderDet.bedseccessAmount;
		}
		}
	allDetailsMap.put("exciseAmt",exciseAmt);
	
	}

// waranty ,delivery,pod,payment
orderTermDetails = delegator.findList("OrderTerm",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );

deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "IMMEDIATELY"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
if(UtilValidate.isNotEmpty(deliveryDetails)){
   delivery =deliveryDetails.description;
   allDetailsMap.put("delivery",delivery);
}
podDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "FOR_MD"));
podDetails = EntityUtil.getFirst(podDetails);
if(UtilValidate.isNotEmpty(podDetails)){
   placeOfDispatch =podDetails.description;
   allDetailsMap.put("placeOfDispatch",placeOfDispatch);
}
paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "SATFACTRY_SUPLY"));
paymentDetails = EntityUtil.getFirst(paymentDetails);
if(UtilValidate.isNotEmpty(paymentDetails)){
   payment =paymentDetails.description;
   allDetailsMap.put("payment",payment);
}
//warantyDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "WARRANTY"));
//if(UtilValidate.isNotEmpty(warantyDetails)){
//waranty=warantyDetails.termTypeId;
//allDetailsMap.put("waranty",waranty);
//}

context.allDetailsMap=allDetailsMap;
context.orderDetailsList=orderDetailsList;

//Debug.log("allDetailsMap=================================="+allDetailsMap);
//Debug.log("orderDetailsList=================================="+orderDetailsList);




//company Details-tin,cst,kst
//List condlist=[];
//condlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
//condlist.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS,"TIN_NUMBER"));
//cond=EntityCondition.makeCondition(condlist,EntityOperator.AND);
//tinDetails = delegator.findList("PartyIdentification", cond , null, null, null, false );
//tinDetails=EntityUtil.getFirst(tinDetails);
//tinNumber="";
//if(UtilValidate.isNotEmpty(tinDetails)){
//tinNumber=tinDetails.idValue;
//allDetailsMap.put("tinNumber",tinNumber);
//}



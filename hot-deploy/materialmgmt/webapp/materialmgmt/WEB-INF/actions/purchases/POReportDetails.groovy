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

//to get product details
orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderitems->
		orderDetailsMap=[:];
		orderDetailsMap["productId"]=orderitems.productId;
		product = delegator.findOne("Product",["productId":orderitems.productId],false);
		if(product){
		uomId=product.get("quantityUomId");
		if(UtilValidate.isNotEmpty(uomId)){
			unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
		 orderDetailsMap["unit"]=unitDesciption.get("abbreviation");
		}}
		
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
		QuoteDetails = delegator.findOne("Quote",["quoteId":quoteId],false);
		qutationDate=QuoteDetails.get("issueDate");
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
pakfwdDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "COGS_FREIGHT"));
pakfwdDetails = EntityUtil.getFirst(pakfwdDetails);
if(UtilValidate.isNotEmpty(pakfwdDetails)){
   frightCharges =pakfwdDetails.amount;
   allDetailsMap.put("frightCharges",frightCharges);
}
pakfwdDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "COGS_INSURANCE"));
pakfwdDetails = EntityUtil.getFirst(pakfwdDetails);
if(UtilValidate.isNotEmpty(pakfwdDetails)){
   insurance =pakfwdDetails.amount;
   allDetailsMap.put("insurance",insurance);
}
pakfwdDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "COGS_OTH_CHARGES"));
pakfwdDetails = EntityUtil.getFirst(pakfwdDetails);
if(UtilValidate.isNotEmpty(pakfwdDetails)){
   otherCharges =pakfwdDetails.amount;
   allDetailsMap.put("otherCharges",otherCharges);
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
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "EX_GODN"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
  if(UtilValidate.isNotEmpty(deliveryDetails)){
    delivery =deliveryDetails.description;
   allDetailsMap.put("delivery",delivery);
   }
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "EX_OUR_GODN"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
      if(UtilValidate.isNotEmpty(deliveryDetails)){
       delivery =deliveryDetails.description;
       allDetailsMap.put("delivery",delivery);
}
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "EX_STOCK"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
        if(UtilValidate.isNotEmpty(deliveryDetails)){
         delivery =deliveryDetails.description;
         allDetailsMap.put("delivery",delivery);
   }
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "MTL_REDY_OBTND"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
        if(UtilValidate.isNotEmpty(deliveryDetails)){
        delivery =deliveryDetails.description;
          allDetailsMap.put("delivery",delivery);
   }
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "FOR_MD"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
      if(UtilValidate.isNotEmpty(deliveryDetails)){
      placeOfDispatch =deliveryDetails.description;
      allDetailsMap.put("placeOfDispatch",placeOfDispatch);
   }
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "OWN_CON_DEST_PONT"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
		if(UtilValidate.isNotEmpty(deliveryDetails)){
		delivery =deliveryDetails.description;
		allDetailsMap.put("delivery",delivery);
  }
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "WTHN_15_PO"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
	 if(UtilValidate.isNotEmpty(deliveryDetails)){
	 delivery =deliveryDetails.description;
	 allDetailsMap.put("delivery",delivery);
		}
deliveryDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "WTHN_30_PO"));
deliveryDetails = EntityUtil.getFirst(deliveryDetails);
    if(UtilValidate.isNotEmpty(deliveryDetails)){
	 delivery =deliveryDetails.description;
	  allDetailsMap.put("delivery",delivery);
		}


		  
paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "FEE_PAY_NETDAYS_1"));
paymentDetails = EntityUtil.getFirst(paymentDetails);
     if(UtilValidate.isNotEmpty(paymentDetails)){
     payment =paymentDetails.description;
      allDetailsMap.put("payment",payment);
}
 paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "100_COD_BASIS"));
 paymentDetails = EntityUtil.getFirst(paymentDetails);
	  if(UtilValidate.isNotEmpty(paymentDetails)){
	  payment =paymentDetails.description;
	   allDetailsMap.put("payment",payment);
 }
  paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "90_ADVANCE"));
  paymentDetails = EntityUtil.getFirst(paymentDetails);
	   if(UtilValidate.isNotEmpty(paymentDetails)){
	   payment =paymentDetails.description;
		allDetailsMap.put("payment",payment);
  }
 paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "AGNT_DELVER"));
 paymentDetails = EntityUtil.getFirst(paymentDetails);
	  if(UtilValidate.isNotEmpty(paymentDetails)){
	  payment =paymentDetails.description;
	   allDetailsMap.put("payment",payment);
 }
 paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "AGNT_PFORM"));
 paymentDetails = EntityUtil.getFirst(paymentDetails);
	  if(UtilValidate.isNotEmpty(paymentDetails)){
	  payment =paymentDetails.description;
	   allDetailsMap.put("payment",payment);
 }
 paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "ALD_MADE"));
 paymentDetails = EntityUtil.getFirst(paymentDetails);
	  if(UtilValidate.isNotEmpty(paymentDetails)){
	  payment =paymentDetails.description;
	   allDetailsMap.put("payment",payment);
 }
 paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "DOCMNT_NEGO"));
 paymentDetails = EntityUtil.getFirst(paymentDetails);
	  if(UtilValidate.isNotEmpty(paymentDetails)){
	  payment =paymentDetails.description;
	   allDetailsMap.put("payment",payment);
 }
  paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "50_ADVANCE"));
  paymentDetails = EntityUtil.getFirst(paymentDetails);
	   if(UtilValidate.isNotEmpty(paymentDetails)){
	   payment =paymentDetails.description;
		allDetailsMap.put("payment",payment);
  }
 paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "FEE_PAY_NETDAYS_2"));
  paymentDetails = EntityUtil.getFirst(paymentDetails);
		if(UtilValidate.isNotEmpty(paymentDetails)){
		payment =paymentDetails.description;
		 allDetailsMap.put("payment",payment);
   }
paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "FEE_PAY_NETDAYS_3"));
paymentDetails = EntityUtil.getFirst(paymentDetails);
	 if(UtilValidate.isNotEmpty(paymentDetails)){
	 payment =paymentDetails.description;
	  allDetailsMap.put("payment",payment);
}
 paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "SATFACTRY_SUPLY"));
 paymentDetails = EntityUtil.getFirst(paymentDetails);
	  if(UtilValidate.isNotEmpty(paymentDetails)){
	  payment =paymentDetails.description;
	   allDetailsMap.put("payment",payment);
 }
  paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "PMT_15_DAYS"));
  paymentDetails = EntityUtil.getFirst(paymentDetails);
	   if(UtilValidate.isNotEmpty(paymentDetails)){
	   payment =paymentDetails.description;
		allDetailsMap.put("payment",payment);
  }
   paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "PMT_30_DAYS"));
   paymentDetails = EntityUtil.getFirst(paymentDetails);
		if(UtilValidate.isNotEmpty(paymentDetails)){
		payment =paymentDetails.description;
		 allDetailsMap.put("payment",payment);
   }
paymentDetails = EntityUtil.filterByCondition(orderTermDetails, EntityCondition.makeCondition("termTypeId", EntityOperator.EQUALS, "TO_BE_MADE"));
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



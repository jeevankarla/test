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
orderDetailsList=[];
allDetailsMap=[:];
allDetailsMap.put("orderId",orderId);
allDetailsMap["total"]=BigDecimal.ZERO;

//company Details-tin,cst,kst
List condlist=[];
condlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
condlist.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS,"TIN_NUMBER"));
cond=EntityCondition.makeCondition(condlist,EntityOperator.AND);
tinDetails = delegator.findList("PartyIdentification", cond , null, null, null, false );
tinDetails=EntityUtil.getFirst(tinDetails);
tinNumber="";
if(UtilValidate.isNotEmpty(tinDetails)){
tinNumber=tinDetails.idValue;
allDetailsMap.put("tinNumber",tinNumber);
}
List clist=[];
clist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
clist.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS,"CST_NUMBER"));
cond=EntityCondition.makeCondition(clist,EntityOperator.AND);
cstDetails = delegator.findList("PartyIdentification", cond , null, null, null, false );
cstDetails=EntityUtil.getFirst(cstDetails);
cstNumber="";
if(UtilValidate.isNotEmpty(cstDetails)){
cstNumber=cstDetails.idValue;
allDetailsMap.put("cstNumber",cstNumber);
}
List condtionlist=[];
condtionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"));
condtionlist.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS,"KST_NUMBER"));
cond=EntityCondition.makeCondition(condtionlist,EntityOperator.AND);
kstDetails = delegator.findList("PartyIdentification", cond , null, null, null, false );
kstDetails=EntityUtil.getFirst(kstDetails);
kstNumber="";
if(UtilValidate.isNotEmpty(kstDetails)){
kstNumber=kstDetails.idValue;
allDetailsMap.put("kstNumber",kstNumber);
}

//to get product details
orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
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
// to get quoteid,date and enquiryid
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails=EntityUtil.getFirst(orderDetails);
	quoteId=orderDetails.quoteId;
	if(UtilValidate.isNotEmpty(quoteId)){
		QuoteDetails = delegator.findList("Quote",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
		QuoteDetails=EntityUtil.getFirst(QuoteDetails);
		qutationDate=QuoteDetails.issueDate;
		allDetailsMap.put("qutationDate",qutationDate);
		allDetailsMap.put("quoteId",quoteId);
		
		enquiryDetails = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
		enquiryDetails=EntityUtil.getFirst(enquiryDetails);
		enquiryId=enquiryDetails.custRequestId;
		allDetailsMap.put("enquiryId",enquiryId);
	   }
	}
// custRequestDate
orderHeaderDetails = delegator.findList("OrderHeader",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
orderHeaderDetails=EntityUtil.getFirst(orderHeaderDetails);
if(UtilValidate.isNotEmpty(orderHeaderDetails)){		
 orderDate=orderHeaderDetails.orderDate; 
  allDetailsMap.put("orderDate",orderDate);
  
  custRequestId=orderHeaderDetails.custRequestId;
   if(UtilValidate.isNotEmpty(custRequestId) && (custRequestId != null)){	
	custRequestDetails = delegator.findList("CustRequest",EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS , custRequestId)  , null, null, null, false );
	custRequestDetails=EntityUtil.getFirst(custRequestDetails);
	custRequestDate=custRequestDetails.custRequestDate;
	allDetailsMap.put("custRequestDate",custRequestDate);
   }
	 }

// party Address
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
	vendorDetail=EntityUtil.getFirst(vendorDetails);
	if(UtilValidate.isNotEmpty(vendorDetail)){
		
	fromPartyId=vendorDetail.partyId;
	}
	fromPartyId="";
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
 }
// waranty
orderTermDetails = delegator.findList("OrderTerm",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
orderTermDetails=EntityUtil.getFirst(orderTermDetails);
if(UtilValidate.isNotEmpty(orderTermDetails)){
waranty=orderTermDetails.termTypeId;
allDetailsMap.put("waranty",waranty);
}
// frieghtCharges,exciseDuty
orderAdjustmentDetails = delegator.findList("OrderAdjustment",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
orderAdjustmentDetails=EntityUtil.getFirst(orderAdjustmentDetails);
if(UtilValidate.isNotEmpty(orderAdjustmentDetails)){
 frieghtCharges =orderAdjustmentDetails.orderAdjustmentTypeId;
 exciseDuty=orderAdjustmentDetails.orderAdjustmentTypeId;
allDetailsMap.put("frieghtCharges",frieghtCharges);
allDetailsMap.put("exciseDuty",exciseDuty);
}


context.allDetailsMap=allDetailsMap;

context.orderDetailsList=orderDetailsList;

//Debug.log("allDetailsMap=================================="+allDetailsMap);
//Debug.log("orderDetailsList=================================="+orderDetailsList);










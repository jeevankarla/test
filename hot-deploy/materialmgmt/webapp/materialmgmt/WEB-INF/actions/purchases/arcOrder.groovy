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

orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
productIds=orderDetails.productId;
productCategories = delegator.findList("ProductCategoryAndMember",EntityCondition.makeCondition("productId", EntityOperator.IN, productIds) , null, null, null, false );
//productCategory = EntityUtil.getFieldListFromEntityList(productCategories, "productCategoryId", true);
productDescription = EntityUtil.getFieldListFromEntityList(productCategories, "description", true);

allDetailsMap.put("productDescription",productDescription);

//FileNo
fileNumber = delegator.findOne("OrderAttribute",["orderId":orderId,"attrName":"FILE_NUMBER"],false);
if(fileNumber){
	fileNo=fileNumber.get("attrValue");
	allDetailsMap.put("fileNo",fileNo);
}

//referNumber
referNumber = delegator.findOne("OrderHeader",["orderId":orderId],false);
if(referNumber){
	refNo=referNumber.get("externalId");
	allDetailsMap.put("refNo",refNo);
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
// ED,VAT
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
List condlist=[];
condlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
condlist.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS,"VAT_PUR"));
condition=EntityCondition.makeCondition(condlist,EntityOperator.AND);
vatDetails = delegator.findList("OrderAdjustment", condition , null, null, null, false );
vatDetails = EntityUtil.getFirst(vatDetails);
if(UtilValidate.isNotEmpty(vatDetails)){
   vat =vatDetails.sourcePercentage;
   allDetailsMap.put("vat",vat);
}

context.allDetailsMap=allDetailsMap;
context.orderDetailsList=orderDetailsList;

//Debug.log("orderDetailsList=================================="+orderDetailsList);










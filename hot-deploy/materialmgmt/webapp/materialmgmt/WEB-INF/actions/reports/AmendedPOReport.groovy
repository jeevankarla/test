import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
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
allDetailsMap["totVal"]=BigDecimal.ZERO;
allDetailsMap.put("orderId",orderId);

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
orderHeaderDetails = delegator.findList("OrderHeader",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
orderHeaderDetails=EntityUtil.getFirst(orderHeaderDetails);
if(UtilValidate.isNotEmpty(orderHeaderDetails)){
   orderDate=orderHeaderDetails.orderDate;
   allDetailsMap.put("orderDate",orderDate);
   custRequestId=orderHeaderDetails.custRequestId;   
   if(UtilValidate.isNotEmpty(custRequestId)){
	 allDetailsMap.put("custRequestId",custRequestId);
   }
}
orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );

exciseAmt = 0;
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderDet->
		quoteId = orderDet.quoteId;
		quantity= orderDet.quantity;
		unitPrice=orderDet.unitPrice;
		totVal=(quantity)*(unitPrice);
		allDetailsMap.put("totVal",totVal);
		allDetailsMap.put("quoteId",quoteId);
		if(orderDet.bedAmount){
			exciseAmt += orderDet.bedAmount;
		}
		if(orderDet.bedcessAmount){
			exciseAmt += orderDet.bedcessAmount;
		}		
		if(orderDet.bedseccessAmount){
			exciseAmt += orderDet.bedseccessAmount;
		}
		allDetailsMap.put("exciseAmt",exciseAmt);
		enquiryDetails = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
		enquiryDetails=EntityUtil.getFirst(enquiryDetails);
		if(enquiryDetails){
		custRequestId=enquiryDetails.custRequestId;
		allDetailsMap.put("custRequestId",custRequestId);	
		}	
	}
}


partyAddressMap=[:];
List conditionList = [];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_FROM_VENDOR"));
cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );
vendorDetail=EntityUtil.getFirst(vendorDetails);
fromPartyId="";
if(UtilValidate.isNotEmpty(vendorDetail)){
   fromPartyId	 = vendorDetail.partyId;
   partyAddressMap.put("fromPartyId",fromPartyId);
   
   partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:fromPartyId, userLogin: userLogin]);
   if(UtilValidate.isNotEmpty(partyPostalAddress)){
	   if(UtilValidate.isNotEmpty(partyPostalAddress.address1)){
		address1=partyPostalAddress.address1;
		partyAddressMap.put("address1",address1);
	   }
	  if(UtilValidate.isNotEmpty(partyPostalAddress.address2)){
		  address2=partyPostalAddress.address2;
		  partyAddressMap.put("address2",address2);
	  }
	  if(UtilValidate.isNotEmpty(partyPostalAddress.city)){
		  city=partyPostalAddress.city;
		  partyAddressMap.put("city",city);
	  }
	  if(UtilValidate.isNotEmpty(partyPostalAddress.postalCode)){
		  postalCode=partyPostalAddress.postalCode;
		  partyAddressMap.put("postalCode",postalCode);
	  }
   }
   partyContactDetails=dispatcher.runSync("getPartyTelephone", [partyId:fromPartyId, userLogin: userLogin]);
   if(UtilValidate.isNotEmpty(partyContactDetails)){
	   if(UtilValidate.isNotEmpty(partyContactDetails.contactNumber)){
		   contactNumber=partyContactDetails.contactNumber;
		   context.put("contactNumber",contactNumber);
	   }
   }
   context.partyAddressMap=partyAddressMap;
// FaxNumber
   conditionList.clear();   
   conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "FAX_BILLING"));
   conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, fromPartyId));
   cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
   faxNumber="";
   listAddress = delegator.findList("PartyContactDetailByPurpose", cond , null, null, null, false );
   if(UtilValidate.isNotEmpty(listAddress.contactNumber)){
	  faxNumber = listAddress.contactNumber;
	  allDetailsMap.put("faxNuber", faxNumber);
	}
}

orderAdjustmentDetails = delegator.findList("OrderAdjustment",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );

pckAndFwdDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "COGS_PCK_FWD"));
pckAndFwdDetail = EntityUtil.getFirst(pckAndFwdDetails);
if(UtilValidate.isNotEmpty(pckAndFwdDetail)){
   pckAndFwdAmt =pckAndFwdDetail.amount;   
   allDetailsMap.put("adjustmentTypeId",pckAndFwdDetail.orderAdjustmentTypeId);
   allDetailsMap.put("pckAndFwdAmt",pckAndFwdAmt);
}

discountDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "COGS_DISC"));
discountDetail = EntityUtil.getFirst(discountDetails);
if(UtilValidate.isNotEmpty(discountDetail)){
	discount=discountDetail.amount;
	allDetailsMap.put("discount",discount);	
}

taxDetails = EntityUtil.filterByCondition(orderAdjustmentDetails, EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN , UtilMisc.toList("VAT_PUR","CST_PUR")));
taxDetails = EntityUtil.getFirst(taxDetails);
if(UtilValidate.isNotEmpty(taxDetails)){
   tax =taxDetails.amount;
   allDetailsMap.put("tax",tax);
}

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
context.allDetailsMap=allDetailsMap;

/*conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productDet = delegator.findList("OrderItem", cond, null, null, null, false );
productIds = EntityUtil.getFieldListFromEntityList(productDet, "productId", true);


productDetail = EntityUtil.getFirst(productDet);

if(productDetail){
	orderId=productDetail.orderId;
	produtMap.put("orderId",orderId);
	productId=productDetail.productId;
	produtMap.put("productId",productId);
}*/
produtList=[];
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	OrderItemChangeDetails = delegator.findList("OrderItemChange", cond, null, null, null, false );
	orderItemChangeIds = EntityUtil.getFieldListFromEntityList(OrderItemChangeDetails, "orderItemChangeId", true);
	
	orderItemChangeIds.each {eachorderItemChangeId->
		produtMap=[:];
		
	   OrderItemChangeData = EntityUtil.filterByCondition(OrderItemChangeDetails, EntityCondition.makeCondition("orderItemChangeId", EntityOperator.EQUALS, eachorderItemChangeId));
	   OrderItemChangeData = EntityUtil.getFirst(OrderItemChangeData);    
	   quantity=OrderItemChangeData.quantity;
	   unitPrice=OrderItemChangeData.unitPrice;
	   orderId=OrderItemChangeData.orderId;
	   orderItemSeqId=OrderItemChangeData.orderItemSeqId;
	   
	   OrderItemDetails = delegator.findOne("OrderItem",["orderId":orderId,"orderItemSeqId":orderItemSeqId],false);
	   if(OrderItemDetails){
		   productId=OrderItemDetails.get("productId");
		   produtMap.put("productId",productId);
		   if(UtilValidate.isNotEmpty(productId)){
			   
			   productDetails = delegator.findOne("Product",["productId":productId],false);
			   itemCode=productDetails.internalName;
			   description=productDetails.description;
			   uomId=productDetails.quantityUomId;
			   produtMap.put("itemCode",itemCode);
			   produtMap.put("description",description);
			   if(UtilValidate.isNotEmpty(uomId)){
				   unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
				   produtMap.put("unit",unitDesciption.abbreviation);
			   }
		 
		   }
			   
	   }
	   produtMap.put("quantity",quantity);
	   produtMap.put("unitPrice",unitPrice);  
	   produtList.add(produtMap);
	}		
context.produtList=produtList;
	

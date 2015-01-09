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

allDetailsMap=[:];
List conditionList = [];
orderId=parameters.orderId;
allDetailsMap.put("orderId",orderId);

conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS,"LETTER_OF_INTENT"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"ORDER_CANCELLED"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderHeaderDetails = delegator.findList("OrderHeader", condition , null, null, null, false );
orderHeaderDetail=EntityUtil.getFirst(orderHeaderDetails);
if(UtilValidate.isNotEmpty(orderHeaderDetail)){
	orderDate=orderHeaderDetail.orderDate;
	allDetailsMap.put("orderDate",orderDate);	
}		
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS,"REF_NUMBER"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
OrderAttDet=delegator.findList("OrderAttribute", condition , null, null, null, false );
OrderAttDetails=EntityUtil.getFirst(OrderAttDet);
if(UtilValidate.isNotEmpty(OrderAttDetails)){
	attrValue=OrderAttDetails.attrValue;	
	allDetailsMap.put("attrValue",attrValue);
}
context.allDetailsMap=allDetailsMap;

//to get PartyAddress
partyAddressMap=[:];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_FROM_VENDOR"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", condition , null, null, null, false );
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
}  
context.partyAddressMap=partyAddressMap;

//to get CompanyAddress
companyAddressMap=[:];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", condition , null, null, null, false );
vendorDetail=EntityUtil.getFirst(vendorDetails);
fromPartyId="";
if(UtilValidate.isNotEmpty(vendorDetail)){
   fromPartyId	 = vendorDetail.partyId;
   companyAddressMap.put("fromPartyId",fromPartyId);
   
   companyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:"Company", userLogin: userLogin]);
   if(UtilValidate.isNotEmpty(companyPostalAddress)){
	   if(UtilValidate.isNotEmpty(companyPostalAddress.address1)){
		address1=companyPostalAddress.address1;
		companyAddressMap.put("address1",address1);
	   }
	  if(UtilValidate.isNotEmpty(companyPostalAddress.address2)){
		  address2=companyPostalAddress.address2;
		  companyAddressMap.put("address2",address2);
	  }
	  if(UtilValidate.isNotEmpty(companyPostalAddress.city)){
		  city=companyPostalAddress.city;
		  companyAddressMap.put("city",city);
	  }
	  if(UtilValidate.isNotEmpty(companyPostalAddress.postalCode)){
		  postalCode=companyPostalAddress.postalCode;
		  companyAddressMap.put("postalCode",postalCode);
	  }
   }
   companyContactDetails=dispatcher.runSync("getPartyTelephone", [partyId:"Company", userLogin: userLogin]);
   if(UtilValidate.isNotEmpty(partyContactDetails)){
	   if(UtilValidate.isNotEmpty(partyContactDetails.contactNumber)){
		   contactNumber=companyContactDetails.contactNumber;
		   context.put("contactNumber",contactNumber);
	   }
   }
}
context.companyAddressMap=companyAddressMap;

//to get product details
orderDetailsList=[];
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_CREATED"));
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
orderDetails = delegator.findList("OrderItem",cond  , null, null, null, false );

if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderitems->
		orderDetailsMap=[:];
		orderDetailsMap["productId"]=orderitems.productId;
		orderDetailsMap["quantity"]=orderitems.quantity;		
		orderDetailsList.addAll(orderDetailsMap);
	}
}
context.orderDetailsList=orderDetailsList;

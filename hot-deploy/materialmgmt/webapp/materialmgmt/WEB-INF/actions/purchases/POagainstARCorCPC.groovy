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
List pOrderList=[];

allDetailsMap.put("orderId",orderId);
orderDetails = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
// orderDate
orderHeaderDetails = delegator.findList("OrderHeader",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , null, null, null, false );
orderHeaderDetails=EntityUtil.getFirst(orderHeaderDetails);
if(UtilValidate.isNotEmpty(orderHeaderDetails)){
   orderDate=orderHeaderDetails.orderDate;
   allDetailsMap.put("orderDate",orderDate);
	 }
//to get product details
if(UtilValidate.isNotEmpty(orderDetails)){
	orderDetails.each{orderitems->
		orderDetailsMap=[:];
		orderDetailsMap["productId"]=orderitems.productId;
		orderDetailsMap["quantity"]=orderitems.quantity;
		orderDetailsMap["unitPrice"]=orderitems.unitPrice;
		orderDetailsMap["createdDate"]=orderitems.createdDate;
		
  	List condlist=[];
   condlist.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, orderId));
   condlist.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS,"ARC_ORDER"));
   condition=EntityCondition.makeCondition(condlist,EntityOperator.AND);
   orderIds = delegator.findList("OrderAssoc", condition , null, null, null, false );
   orderIds = EntityUtil.getFieldListFromEntityList(orderIds, "orderId", true);
   
   //to get poQty, totAccepQty, poBalanceQty, ARCBalanceQty
   poQty=0;
   totAccepQty=0;
   if(UtilValidate.isNotEmpty(orderIds)){
	  orderIds.each{orderId->
    List clist=[];
	clist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	clist.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,orderitems.productId));
    con=EntityCondition.makeCondition(clist,EntityOperator.AND);
    orderIdDetails = delegator.findList("OrderItem", con , null, null, null, false );
    orderIdDetails = EntityUtil.getFirst(orderIdDetails);
	
	
	if(UtilValidate.isNotEmpty(orderIdDetails)){
		pOrderMap=[:];
		pOrderMap["orderId"]=orderId;
		pOrderMap["productId"]=orderIdDetails.productId;
		pOrderMap["poQty"]=orderIdDetails.quantity;
		qtyAccepted=0;
	    colist=[];
		colist.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["SR_RECEIVED","SR_QUALITYCHECK"]));
		colist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		colist.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,orderitems.productId));
		co=EntityCondition.makeCondition(colist,EntityOperator.AND);
		shipmentDet = delegator.findList("ShipmentReceipt", co , null, null, null, false );
		shipmentDet = EntityUtil.getFirst(shipmentDet);
		if(UtilValidate.isNotEmpty(shipmentDet)){
			qtyAccepted=shipmentDet.quantityAccepted;
	pOrderMap["quantityAccepted"]=qtyAccepted;
			 }
		pOrderMap["pobalQty"]=orderIdDetails.quantity-qtyAccepted;
		
		pOrderList.addAll(pOrderMap);
		
		 }
	
	
    if(UtilValidate.isNotEmpty(orderIdDetails)){
	   poQty=poQty+orderIdDetails.quantity;
	  }	
   List conlist=[];	
   conlist.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["SR_RECEIVED","SR_QUALITYCHECK"]));
   conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
   conlist.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,orderitems.productId));
   cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
   shipmentDetails = delegator.findList("ShipmentReceipt", cond , null, null, null, false );
   shipmentDetails = EntityUtil.getFirst(shipmentDetails);
   if(UtilValidate.isNotEmpty(shipmentDetails)){
	  totAccepQty=totAccepQty+shipmentDetails.quantityAccepted;
       }
   
   if(UtilValidate.isNotEmpty(shipmentDetails)){	   
   orderDetailsMap["poQty"]=poQty;
   orderDetailsMap["totAccepQty"]=totAccepQty;
   orderDetailsMap["poBalanceQty"]=poQty-totAccepQty;
   orderDetailsMap["ARCBalanceQty"]=orderitems.quantity-totAccepQty;
   }
   if(UtilValidate.isEmpty(shipmentDetails)){	   
   orderDetailsMap["poQty"]=poQty;
   orderDetailsMap["totAccepQty"]=totAccepQty;
   orderDetailsMap["poBalanceQty"]=poQty-totAccepQty;
   orderDetailsMap["ARCBalanceQty"]=orderitems.quantity-totAccepQty;
   }
	  }
   }
	orderDetailsList.addAll(orderDetailsMap);
	}
}
context.allDetailsMap=allDetailsMap;
context.orderDetailsList=orderDetailsList;
context.pOrderList=pOrderList;



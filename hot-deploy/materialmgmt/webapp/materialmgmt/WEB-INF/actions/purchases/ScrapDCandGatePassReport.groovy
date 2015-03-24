import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
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
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

formDate=UtilDateTime.nowTimestamp();
thruDate=UtilDateTime.nowTimestamp();
SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy");
if(UtilValidate.isNotEmpty(parameters.fromDateScrap)){
	try {
		formDate = new java.sql.Timestamp(sdf.parse(parameters.fromDateScrap).getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.fromDateScrap, "");
	}
	try {
		thruDate = new java.sql.Timestamp(sdf.parse(parameters.thruDateScrap).getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.thruDateScrap, "");
	}
	formDate = UtilDateTime.getDayStart(formDate);
	thruDate = UtilDateTime.getDayEnd(thruDate);
}
context.formDate=formDate;
context.thruDate=thruDate;
List shipmentList=FastList.newInstance();
List shipmentIds=FastList.newInstance();
List orderHeaders=FastList.newInstance();
List orderIds = FastList.newInstance();
List orderRoleList = FastList.newInstance();
List orderItemsList = FastList.newInstance();
finalMap=[:];
condition=EntityCondition.makeCondition([EntityCondition.makeCondition("estimatedShipDate",EntityOperator.GREATER_THAN_EQUAL_TO,formDate),
	                                     EntityCondition.makeCondition("estimatedShipDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate),
										 EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"GENERATED"),
										 EntityCondition.makeCondition("shipmentTypeId",EntityOperator.IN,UtilMisc.toList("INTUNIT_TR_SHIPMENT","FGS_SHIPMENT"))],EntityOperator.AND);
shipmentList = delegator.findList("Shipment",condition,null,null,null,false);
shipmentIds = EntityUtil.getFieldListFromEntityList(shipmentList,"shipmentId",true);

orderCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("shipmentId",EntityOperator.IN,shipmentIds),
	                                          EntityCondition.makeCondition("statusId",EntityOperator.IN,UtilMisc.toList("ORDER_APPROVED","ORDER_COMPLETED"))],EntityOperator.AND);
orderHeaders = delegator.findList("OrderHeader", orderCondition, null, null, null, false);
orderIds = EntityUtil.getFieldListFromEntityList(orderHeaders, "orderId", true);

orderItemsList = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds),null,null,null,false);


orderRoleCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds)],EntityOperator.AND);
orderRoleList = delegator.findList("OrderRole",orderRoleCondition,UtilMisc.toSet("orderId","roleTypeId","partyId"),null,null,false);

orderItemsList.each{orderItem->
	billToCustomerCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderItem.orderId),
		                                                   EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"BILL_TO_CUSTOMER")],EntityOperator.AND);
	orderRole = EntityUtil.filterByCondition(orderRoleList,billToCustomerCondition);
	shipToCustomerCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderItem.orderId),
		                                                   EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SHIP_TO_CUSTOMER")],EntityOperator.AND);
	shipOrderRole = EntityUtil.filterByCondition(orderRoleList,shipToCustomerCondition);	
	if(UtilValidate.isNotEmpty(shipOrderRole)){
		orderRole=shipOrderRole;
	}			
	orderItemBillingCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderItem.orderId),
		                                                      EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED")],EntityOperator.AND);	
														  
	  orderItemBill = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", orderItemBillingCondition, UtilMisc.toSet("invoiceId"), null, null, false);
	  invoiceIds = EntityUtil.getFieldListFromEntityList(orderItemBill, "invoiceId", true);
	  invoices = delegator.findList("Invoice", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds), null, null, null, false);
	  invoice = null;
	  if(invoices){
		  invoice = invoices.get(0);
	  }
	  payments=null;
	  paymentApplication=delegator.findList("PaymentApplication",EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoice.invoiceId),UtilMisc.toSet("paymentId"),null,null,false);
	  if(UtilValidate.isNotEmpty(paymentApplication)){
		  paymentIds=EntityUtil.getFirst(paymentApplication);
	  	  payments=delegator.findOne("Payment",[paymentId:paymentIds.paymentId],false);
	  }
	  partyId = "";
	  if(orderRole){
		  partyId = (EntityUtil.getFirst(orderRole)).getString("partyId");
	  }
	  partyAddress = dispatcher.runSync("getPartyPostalAddress", [partyId: partyId, userLogin: userLogin]);
	  partyName = dispatcher.runSync("getPartyNameForDate", [partyId: partyId, userLogin: userLogin]);
	  orderHeader=EntityUtil.filterByCondition(orderHeaders,EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderItem.orderId));
	  if(UtilValidate.isNotEmpty(orderHeader)){
		  orderHead=EntityUtil.getFirst(orderHeader);
		  shipments=EntityUtil.filterByCondition(shipmentList,EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,orderHead.shipmentId));
		  shipment=null;
		  if(UtilValidate.isNotEmpty(shipments)){
			  shipment=EntityUtil.getFirst(shipments);
		  }
	  product=delegator.findOne("Product",[productId:orderItem.productId],false);
	  productName="";
	  if(UtilValidate.isNotEmpty(product)){
		  productName=product.description;
	  }
		  if(UtilValidate.isEmpty(finalMap[orderItem.productId])){
			  tempMap=[:];
			  tempList=[];
			  tempMap.put("orderHeader", orderHead);
			  tempMap.put("productName", productName);
			  tempMap.put("payment", payments);
			  tempMap.put("invoice", invoice);
			  tempMap.put("shipment", shipment);
			  tempMap.put("Name", partyName.get("fullName"));
			  tempMap.put("partyId", partyId);
			  tempMap.put("quantity", orderItem.quantity);
			  tempList.add(tempMap);
			  finalMap[orderItem.productId]=tempList;
		  }else{
		  	 List existingList = FastList.newInstance();
			   existingList = finalMap[orderItem.productId];
			   tempMap=[:];
			 tempMap.put("orderHeader", orderHeader);
			 tempMap.put("productName", productName);
			 tempMap.put("payment", payments);
			 tempMap.put("invoice", invoice);
			 tempMap.put("shipment", shipment);
			 tempMap.put("Name", partyName.get("fullName"));
			 tempMap.put("partyId", partyId);
			 tempMap.put("quantity", orderItem.quantity);
			 existingList.add(tempMap);
			 finalMap[orderItem.productId]=existingList;
			 
		  }
	  }											  
}
if(UtilValidate.isNotEmpty(finalMap)){
	Map<String, List> newSortedMap = new TreeMap<String, List>(finalMap);
	context.finalMap=newSortedMap;
}




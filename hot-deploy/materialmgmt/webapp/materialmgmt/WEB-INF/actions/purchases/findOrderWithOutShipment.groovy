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
resultReturn = ServiceUtil.returnSuccess();

shipmentMap=[:];
shipmentList=[];
ordersListForGRNLink=[];

//get PartyId from Role for Dept
List conditionlist=[];
//if(UtilValidate.isNotEmpty(parameters.noConditionFind) && parameters.noConditionFind=="Y"){
/*conditionlist.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
conditionlist.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS,"MATERIAL_SHIPMENT"));
conditionlist.add(EntityCondition.makeCondition("shipmentId", EntityOperator.NOT_EQUAL, parameters.shipmentId));
condition=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
shipmentDeatilsList = delegator.findList("Shipment", condition , null, null, null, false );

shippedOrderIds = EntityUtil.getFieldListFromEntityList(shipmentDeatilsList, "primaryOrderId", true);*/


conditionlist.clear();
conditionlist.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, parameters.shipmentId));
condition=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
shipmentItemList = delegator.findList("ShipmentItem", condition , null, null, null, false );

shipmentProductIds = EntityUtil.getFieldListFromEntityList(shipmentItemList, "productId", true);

Debug.log("===parameters.ShipmeID="+parameters.shipmentId+"===="+parameters.partyId+"==shipmentProductIds="+shipmentProductIds);
//get Orders WithOut LINK to Shipment
conditionlist.clear();
/*conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.NOT_IN, shippedOrderIds));*/
conditionlist.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS,"PURCHASE_ORDER"));
conditionlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_FROM_VENDOR"));
conditionlist.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ORDER_APPROVED"));
conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.partyId));
conditionlist.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"MATERIAL_PUR_CHANNEL"));

condition=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
ordersListWithOutShipment = delegator.findList("OrderHeaderAndRoles", condition , null, null, null, false );

orderIdsWithOutShipment = EntityUtil.getFieldListFromEntityList(ordersListWithOutShipment, "orderId", true);

//preapre List for GRN link

ordersListWithOutShipment.each{ eachOrderHeader->
	innerOrderHeaderMap=[:];
	innerOrderHeaderMap["orderId"]=eachOrderHeader.orderId ;
	innerOrderHeaderMap["entryDate"]=UtilDateTime.toDateString(eachOrderHeader.entryDate,"dd/MM/yyyy") ;
	partyId="";
	supplierName="";
	if(UtilValidate.isNotEmpty(eachOrderHeader.partyId)){
		partyId=eachOrderHeader.partyId;
		supplierName =  PartyHelper.getPartyName(delegator, partyId, false);
	}
	innerOrderHeaderMap["partyId"]=partyId ;
	innerOrderHeaderMap["supplierName"]=supplierName+"["+partyId+"]";
	
	conditionlist.clear();
	conditionlist.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, eachOrderHeader.orderId));
	conditionlist.add(EntityCondition.makeCondition("productId", EntityOperator.IN, shipmentProductIds));
	orderItemCondition=EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
	orderItemList = delegator.findList("OrderItem", orderItemCondition , null, null, null, false );
	if(UtilValidate.isNotEmpty(orderItemList)){
		ordersListForGRNLink.addAll(innerOrderHeaderMap);
	}
	
}
//}
context.ordersListForGRNLink=ordersListForGRNLink;

//Debug.log("ordersListForGRNLink=========frommmAJAX====="+ordersListForGRNLink);

resultReturn.put("ordersListForGRNLink", ordersListForGRNLink);
return resultReturn;

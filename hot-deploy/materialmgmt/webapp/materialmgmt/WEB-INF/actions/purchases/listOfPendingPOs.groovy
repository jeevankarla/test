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
thruDate=parameters.datependingPOs;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
//	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(thruDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.dayBegin=dayBegin;
context.dayEnd=dayEnd;
condList =[];
condList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
condList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.IN, ['ORDER_APPROVED','ORDER_COMPLETED']));
//condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
condList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
orderItemList = delegator.findList("OrderHeaderAndItems", cond, null,null, null, false);
orderIds = EntityUtil.getFieldListFromEntityList(orderItemList, "orderId", true);

//get PartyId from Role for Vendor , PartyName
 conlist=[];
conlist.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
conlist.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SUPPLIER_AGENT"));
cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
vendorDetails = delegator.findList("OrderRole", cond , null, null, null, false );

//partyIds = EntityUtil.getFieldListFromEntityList(vendorDetails, "partyId", true);
pendingPOsList=[];
orderItemList.each{orderItem->
	orderId=orderItem.orderId;
	orderItemSeqId=orderItem.orderItemSeqId;
	productId=orderItem.productId;
	quantity=orderItem.quantity;
	orderDate=orderItem.orderDate;

	product = delegator.findOne("Product",["productId":productId],false);
	if(product){
	productName=product.get("productName");
//	uomId=product.get("quantityUomId");
	internalName=product.get("internalName");
	}
//	if(UtilValidate.isNotEmpty(uomId)){
//		uomDesc = delegator.findOne("Uom",["uomId":uomId],false);
//		uomDesc=EntityUtil.getFirst(uomDesc);
//		unit=uomDesc.abbreviation;	
//		pendingPOsMap["unit"]=unit;
//	}
	
	pendingPOsMap=[:];
	partyIdDetails = EntityUtil.filterByCondition(vendorDetails, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	partyIdDetails = EntityUtil.getFirst(partyIdDetails);
	if(UtilValidate.isNotEmpty(partyIdDetails)){
		partyId=partyIdDetails.partyId;
		partyName =  PartyHelper.getPartyName(delegator, partyId, false);
		pendingPOsMap["partyName"]=partyName;
	}
	condionList =[];
	condionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["SR_RECEIVED","SR_QUALITYCHECK"]));
	condionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	condionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
	condionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	EntityCondition condition = EntityCondition.makeCondition(condionList,EntityOperator.AND);	
	shipmentReceiptList = delegator.findList("ShipmentReceipt", condition, null,null, null, false);	
	qtyAccepted=0;
	if(UtilValidate.isNotEmpty(shipmentReceiptList)){
		shipmentReceiptList.each{shipmentReceipt->
			qtyAccepted=qtyAccepted+shipmentReceipt.quantityAccepted;			
		}
		balancePOqty=quantity-qtyAccepted
        pendingPOsMap["orderId"]=orderId;
		pendingPOsMap["createdDate"]=orderDate;
		pendingPOsMap["description"]=productName;
		pendingPOsMap["productId"]=productId;
		pendingPOsMap["quantity"]=quantity;
		pendingPOsMap["qtyAccepted"]=qtyAccepted;
		pendingPOsMap["balancePOqty"]=balancePOqty;
	}
	if(UtilValidate.isEmpty(shipmentReceiptList)){
		pendingPOsMap=[:];
		pendingPOsMap["orderId"]=orderId;
		pendingPOsMap["createdDate"]=orderDate;
		pendingPOsMap["description"]=productName;
		pendingPOsMap["productId"]=productId;
		pendingPOsMap["quantity"]=quantity;
		pendingPOsMap["qtyAccepted"]=0;
		pendingPOsMap["balancePOqty"]=quantity;
	}		
	pendingPOsList.addAll(pendingPOsMap);
}
context.pendingPOsList=pendingPOsList;




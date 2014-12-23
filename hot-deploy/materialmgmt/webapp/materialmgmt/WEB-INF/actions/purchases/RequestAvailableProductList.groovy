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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;



dctx = dispatcher.getDispatchContext();

conditionList = [];
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_SUBMITTED"));
conditionList.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("CRQ_INPROCESS","CRQ_SUBMITTED")));
conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

custRequestItems = delegator.findList("CustRequestAndItemAndAttribute", condition, null, UtilMisc.toList("-custRequestDate"), null, false);

productIds = EntityUtil.getFieldListFromEntityList(custRequestItems, "productId", true);

inventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds),UtilMisc.toSet("productId", "quantityOnHandTotal"), null, null, false);

prodInvMap = [:];
productIds.each{eachProd ->
	prodInvItems = EntityUtil.filterByCondition(inventoryItems, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd));
	totalQty = 0;
	prodInvItems.each{ eachInvItem ->
		totalQty += eachInvItem.quantityOnHandTotal;
	}
	prodInvMap.putAt(eachProd, totalQty);
}
custRequestItemsList = [];
custRequestItems.each{ eachItem ->
	tempMap = [:];
	tempMap.putAt("custRequestId", eachItem.custRequestId);
	tempMap.putAt("custRequestItemSeqId", eachItem.custRequestItemSeqId);
	tempMap.putAt("custRequestDate", eachItem.custRequestDate);
	tempMap.putAt("fromPartyId", eachItem.fromPartyId);
	tempMap.putAt("productId", eachItem.productId);
	tempMap.putAt("quantity", eachItem.quantity);
	tempMap.putAt("statusId", eachItem.itemStatusId);
	invAvail = 0;
	if(prodInvMap.get(eachItem.productId)){
		invAvail = prodInvMap.get(eachItem.productId);
	}
	tempMap.putAt("QOH", invAvail);
	custRequestItemsList.add(tempMap);
}
context.custRequestItemsList = custRequestItemsList;
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
	fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	def sdf = new SimpleDateFormat("yyyy-mm-dd");
	try {
		if (parameters.custRequestDate) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.custRequestDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
		context.errorMessage = "Cannot parse date string: " + e;
		return;
	}


conditionList = [];
if(UtilValidate.isNotEmpty(parameters.custRequestId)){
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, parameters.custRequestId));
}
if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
}
if(UtilValidate.isNotEmpty(parameters.partyId)){
	conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, parameters.partyId));
}
if(UtilValidate.isNotEmpty(parameters.custRequestDate)){
	conditionList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.EQUALS, fromDate));
}
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CRQ_SUBMITTED"));
conditionList.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.IN, UtilMisc.toList("CRQ_INPROCESS","CRQ_SUBMITTED")));
conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

custRequestItems = delegator.findList("CustRequestAndItemAndAttribute", condition, null, UtilMisc.toList("-custRequestDate"), null, false);

productIds = EntityUtil.getFieldListFromEntityList(custRequestItems, "productId", true);

custRequestIds=EntityUtil.getFieldListFromEntityList(custRequestItems, "custRequestId", true);
//get ItemIssuence for custrequests
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds));
conditionItemIssue = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

itemIssuanceList = delegator.findList("ItemIssuance", conditionItemIssue, null, UtilMisc.toList("-issuedDateTime"), null, false);


prodInvMap = [:];
productIds.each{eachProd ->
	invCountMap = dispatcher.runSync("getProductInventoryOpeningBalance", [productId: eachProd, ownerPartyId:"Company", userLogin: userLogin]);
	invQty = invCountMap.get("inventoryCount");
	prodInvMap.putAt(eachProd, invQty);
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
	
	filterIssuenceReq = FastList.newInstance();
	filterIssuenceReq.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, eachItem.custRequestId));
	filterIssuenceReq.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, eachItem.custRequestItemSeqId));
	filterIssuenceCond = EntityCondition.makeCondition(filterIssuenceReq, EntityOperator.AND);
	
	invAvail = 0;
	if(prodInvMap.get(eachItem.productId)){
		invAvail = prodInvMap.get(eachItem.productId);
	}
	issuedQty=0;
	custReqIssuenceList = EntityUtil.filterByCondition(itemIssuanceList,filterIssuenceCond);
	custReqIssuenceList.each{custRequestIssueItem->
		if(UtilValidate.isNotEmpty(custRequestIssueItem.quantity)){
			issuedQty+=custRequestIssueItem.quantity;
		}
	}
	tempMap.putAt("QOH", invAvail);
	tempMap.putAt("issuedQty", issuedQty);
	
	custRequestItemsList.add(tempMap);
}
context.custRequestItemsList = custRequestItemsList;





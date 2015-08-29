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
import org.ofbiz.entity.Delegator;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;

// Debug.log("partyId####################"+context.get("partyId"));

dctx = dispatcher.getDispatchContext();
delegator  = dctx.getDelegator();
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

List issueThruTransferProductsList = FastList.newInstance();
issueThruTransferProductsList = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,"ISSUE_THRU_TRANSFER"),null,null,null,false );
List issueThruTransferProductIdsList = FastList.newInstance();
issueThruTransferProductIdsList = EntityUtil.getFieldListFromEntityList(issueThruTransferProductsList, "productId", true);

List custRequestItemsList = FastList.newInstance();
List productFacility = FastList.newInstance();
conditionList = [];
if(UtilValidate.isNotEmpty(parameters.custRequestId)){
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, parameters.custRequestId));
}
if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, parameters.productId));
}
if(UtilValidate.isNotEmpty(parameters.fromPartyId)){
	conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, parameters.fromPartyId));
}
if(UtilValidate.isNotEmpty(parameters.custRequestDate)){
	conditionList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.EQUALS, fromDate));
}
conditionList.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS,"CRQ_SUBMITTED"));
conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "INTERNAL_INDENT"));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

custRequestItems = delegator.findList("CustRequestAndItemAndAttribute", condition, null, UtilMisc.toList("-custRequestId"), null, false);

productIds = EntityUtil.getFieldListFromEntityList(custRequestItems, "productId", true);

custRequestIds=EntityUtil.getFieldListFromEntityList(custRequestItems, "custRequestId", true);
//get ItemIssuence for custrequests
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds));
conditionItemIssue = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
itemIssuanceList = delegator.findList("ItemIssuance", conditionItemIssue, null, UtilMisc.toList("-issuedDateTime","-custRequestId"), null, false);
String facilityId = "";
/*List facilityList = FastList.newInstance();
if(UtilValidate.isNotEmpty(context.get("facilityList"))){
	facilityList = context.get("facilityList");
	if(UtilValidate.isNotEmpty(facilityList)){
		facility = EntityUtil.getFirst(context.get("facilityList"));
		if(UtilValidate.isNotEmpty(facility)){
			facilityId = facility.get("facilityId");
		}
	}
	 
}
Map inMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(facilityId)){
	inMap.put("facilityId",facilityId);
}else{
	String partyFrom = (String) parameters.get("partyFrom");
	facilityList = delegator .findList("Facility",EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyFrom),null,null,null,false);
	if(UtilValidate.isNotEmpty(facilityList)){
		facility = EntityUtil.getFirst(facilityList);
		if(UtilValidate.isNotEmpty(facility)){
			inMap.put("facilityId",facility.get("facilityId"));
		}
	}

}
inMap.put("userLogin",userLogin);
inMap.put("ownerPartyId","Company");
prodInvMap = [:];
productIds.each{eachProd ->
	inMap.put("productId",eachProd);
	invCountMap = dispatcher.runSync("getProductInventoryOpeningBalance", inMap);
	invQty = invCountMap.get("inventoryCount");
	prodInvMap.putAt(eachProd, invQty);
}*/
custRequestItemsList = [];
custRequestItems.each{ eachItem ->
	String productId = eachItem.productId;
	
	String fromPartyId = eachItem.fromPartyId;
	tempMap = [:];
	tempMap.putAt("custRequestId", eachItem.custRequestId);
	tempMap.putAt("custRequestItemSeqId", eachItem.custRequestItemSeqId);
	tempMap.putAt("custRequestDate", eachItem.custRequestDate);
	tempMap.putAt("fromPartyId", eachItem.fromPartyId);
	tempMap.putAt("productId", eachItem.productId);
	tempMap.putAt("quantity", eachItem.quantity);
	tempMap.putAt("statusId", eachItem.itemStatusId);
	/*if(UtilValidate.isNotEmpty(context.get("facilityList"))){
		tempMap.putAt("facilityList", context.get("facilityList"));
	}*/
	
	boolean issueThruWeighBridge = false;
	List fromPartyIssuThruList = FastList.newInstance();
	
	List issueConditionList = UtilMisc.toList(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,fromPartyId));
	issueConditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"ISSUE_THRU_WEIGHBRDG"));
	
	EntityCondition issueCondition = EntityCondition.makeCondition(issueConditionList,EntityJoinOperator.AND);
	fromPartyIssuThruList = delegator.findList("PartyRole",issueCondition,null,null,null,false);
	
	if(UtilValidate.isNotEmpty(fromPartyIssuThruList)){
			issueThruWeighBridge = true;
	}
	
	List custRequstParty=delegator.findList("CustRequestParty",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,eachItem.custRequestId),null,null,null,false);
	custReqParty=EntityUtil.getFirst(custRequstParty);
	
	filterIssuenceReq = FastList.newInstance();
	filterIssuenceReq.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, eachItem.custRequestId));
	filterIssuenceReq.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, eachItem.custRequestItemSeqId));
	filterIssuenceCond = EntityCondition.makeCondition(filterIssuenceReq, EntityOperator.AND);
	
	invAvail = 0;
	/*if(prodInvMap.get(eachItem.productId)){
		invAvail = prodInvMap.get(eachItem.productId);
	}*/
	issuedQty=0;
	custReqIssuenceList = EntityUtil.filterByCondition(itemIssuanceList,filterIssuenceCond);
	custReqIssuenceList.each{custRequestIssueItem->
		if(UtilValidate.isNotEmpty(custRequestIssueItem.quantity)){
			issuedQty+=custRequestIssueItem.quantity;
		} 
		if(UtilValidate.isNotEmpty(custRequestIssueItem.cancelQuantity)){
			issuedQty-=custRequestIssueItem.cancelQuantity;
		}
		
	}
	tempMap.putAt("QOH", invAvail);
	tempMap.putAt("issuedQty", issuedQty);
	tempMap.put("showTransferButton","N");
	if(issueThruWeighBridge && UtilValidate.isNotEmpty(issueThruTransferProductIdsList)&& issueThruTransferProductIdsList.contains(productId)){
		tempMap.put("showTransferButton","Y");
	}
	
	if(UtilValidate.isNotEmpty(custReqParty.partyId)){
		tempMap.partyIdFrom = custReqParty.partyId;
	}
   if(UtilValidate.isEmpty(parameters.partyFrom) && UtilValidate.isNotEmpty(context.get("partyId"))){
	   if((context.get("partyId")).contains(custReqParty.partyId)){
		   custRequestItemsList.add(tempMap);
	   }
   }else if(UtilValidate.isNotEmpty(parameters.partyFrom) && parameters.partyFrom==custReqParty.partyId){
	custRequestItemsList.add(tempMap);
   }else if(UtilValidate.isEmpty(parameters.partyFrom) && UtilValidate.isEmpty(context.get("partyId"))){
   custRequestItemsList.add(tempMap);
   }
}
context.custRequestItemsList = custRequestItemsList;



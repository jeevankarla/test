import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.entity.finder.EntityFinderUtil.ConditionList;
productId=parameters.productId;
custRequestId = parameters.custRequestId;
custRequestDate = parameters.custRequestDate;
custRequestTypeId=parameters.custRequestTypeId;
context.custRequestTypeId=custRequestTypeId;
custRequestEndDate = parameters.custRequestEndDate;
partyId = parameters.fromPartyId;
fromPartyId=context.get("partyId");
conditionList=[];
if(UtilValidate.isNotEmpty(parameters.productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
}
if(UtilValidate.isNotEmpty(parameters.custRequestId)){
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
}
if(UtilValidate.isNotEmpty(partyId)){
	conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
}

if(UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(fromPartyId)){
	conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.IN, fromPartyId));
}
Timestamp custRequestDateNew=null;
Timestamp custRequestEndDateNew=null;
if(UtilValidate.isNotEmpty(custRequestDate)){
    def sdf = new SimpleDateFormat("yyyy-MM-dd");
	try {
		custRequestDateNew = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(custRequestDate).getTime()));
	}catch (Exception e) {
		Debug.logError(e, "Cannot parse date string: " + custRequestDate, "");
	}
}
if(UtilValidate.isNotEmpty(custRequestEndDate)){
	def sdf = new SimpleDateFormat("yyyy-MM-dd");
	try {
		custRequestEndDateNew = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(custRequestEndDate).getTime()));
	}catch (Exception e) {
		Debug.logError(e, "Cannot parse date string: " + custRequestEndDate, "");
	}
}
if(UtilValidate.isNotEmpty(custRequestDateNew)){
	conditionList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.GREATER_THAN_EQUAL_TO, custRequestDateNew));
}
if(UtilValidate.isNotEmpty(custRequestEndDateNew)){
	conditionList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.LESS_THAN_EQUAL_TO, custRequestEndDateNew));
}
orderBy = UtilMisc.toList("lastModifiedDate");	
//conditionList.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "CRQ_ISSUED"));
conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, custRequestTypeId));
condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
CustRequestAndItemAndAttribute = delegator.findList("CustRequestAndItemAndAttribute", condition, null, orderBy, null, false);
if(UtilValidate.isNotEmpty(CustRequestAndItemAndAttribute)){
	/*
	if(custRequestTypeId=="INTERNAL_INDENT"){
		tempList=[];
		CustRequestAndItemAndAttribute.each{items->
			tempMap = [:];
			tempMap.custRequestId=items.custRequestId;
			tempMap.custRequestItemSeqId=items.custRequestItemSeqId;
			tempMap.custRequestDate=items.custRequestDate;
			tempMap.custRequestName=items.custRequestName;
			tempMap.productId = items.productId;
			tempMap.fromPartyId=items.fromPartyId;
			tempMap.quantity = items.quantity;
			productFacility = delegator.findList("ProductFacility",EntityCondition.makeCondition("productId",EntityOperator.EQUALS,items.productId),UtilMisc.toSet("facilityId"),null,null,false);
			prodFacilityIds = EntityUtil.getFieldListFromEntityList(productFacility, "facilityId", true);
			ecl = EntityCondition.makeCondition([EntityCondition.makeCondition("facilityId",EntityOperator.IN,prodFacilityIds),
				                                 EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,items.fromPartyId)],EntityOperator.AND);               
			facilityList = delegator.findList("Facility",ecl,null,null,null,false);
			tempMap.put("facility",facilityList);
			tempList.add(tempMap);
		}
		context.indentItems = tempList;
	}else{
		context.indentItems = CustRequestAndItemAndAttribute;
	}*/
	tempList=[];
	 CustRequestAndItemAndAttribute.each{eachCusrequest->
	 custRequestId = eachCusrequest.custRequestId;
	 custRequestItemSeqId = eachCusrequest.custRequestItemSeqId;
	 conditionList.clear();
	 conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	 conditionList.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
	 condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	 itemIssuances = delegator.findList("ItemIssuance", condition, null, null, null, false);
	 itemIssuanceIds=EntityUtil.getFieldListFromEntityList(itemIssuances,"itemIssuanceId", true);
	 shipmentIds = EntityUtil.getFieldListFromEntityList(itemIssuances,"shipmentId", true);
	 shipmentCond = EntityCondition.makeCondition([EntityCondition.makeCondition("shipmentId",EntityOperator.IN,shipmentIds),
		                                           EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"SHIPMENT_CANCELLED")],EntityOperator.AND);
	 shipmentList = delegator.findList("Shipment",shipmentCond,UtilMisc.toSet("shipmentId"),null,null,false);
	 shipmentIds.clear();
	 shipmentIds=EntityUtil.getFieldListFromEntityList(shipmentList,"shipmentId", true);
	 itemIssuanceAttributes = delegator.findList("ItemIssuanceAttribute",EntityCondition.makeCondition("itemIssuanceId", EntityOperator.IN , itemIssuanceIds) ,null, null, null, false );
	 itemIssuanceIdss =EntityUtil.getFieldListFromEntityList(itemIssuanceAttributes,"itemIssuanceId", true);
	 itemIssueCond = EntityCondition.makeCondition([EntityCondition.makeCondition("itemIssuanceId", EntityOperator.NOT_IN, itemIssuanceIdss),
		 											EntityCondition.makeCondition("shipmentId",EntityOperator.IN,shipmentIds)],EntityOperator.AND);
	 itemIssuancesDetails = EntityUtil.filterByCondition(itemIssuances, itemIssueCond);
		 if(UtilValidate.isNotEmpty(itemIssuancesDetails)){
			 itemIssuancesDetails.each{eachItemIssuance->
				 indentItemsMap = [:];
				 itemIssuanceId = eachItemIssuance.itemIssuanceId;
				 custRequestId = eachItemIssuance.custRequestId;
				 custRequestItemSeqId = eachItemIssuance.custRequestItemSeqId;
				 quantity = eachItemIssuance.quantity;
				 custRequestName = eachCusrequest.custRequestName;
				 custRequestDate = eachCusrequest.custRequestDate;
				 productId = eachCusrequest.productId;
				 fromPartyId = eachCusrequest.fromPartyId;
				 indentItemsMap.put("custRequestId", custRequestId);
				 indentItemsMap.put("custRequestItemSeqId", custRequestItemSeqId);
				 indentItemsMap.put("quantity", quantity);
				 indentItemsMap.put("custRequestName", custRequestName);
				 indentItemsMap.put("custRequestDate", custRequestDate);
				 indentItemsMap.put("itemIssuanceId", itemIssuanceId);
				 indentItemsMap.put("productId", productId);
				 indentItemsMap.put("fromPartyId", fromPartyId);
				 indentItemsMap.put("issuedDateTime",eachItemIssuance.issuedDateTime);
				 productFacility = delegator.findList("ProductFacility",EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId),UtilMisc.toSet("facilityId"),null,null,false);
				 prodFacilityIds = EntityUtil.getFieldListFromEntityList(productFacility, "facilityId", true);
				 ecl = EntityCondition.makeCondition([EntityCondition.makeCondition("facilityId",EntityOperator.IN,prodFacilityIds),
				 EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,fromPartyId)],EntityOperator.AND);
				 facilityList = delegator.findList("Facility",ecl,null,null,null,false);
				 indentItemsMap.put("facility",facilityList);
				 tempList.add(indentItemsMap);
			 }
		 }
	 }
	 context.indentItems = tempList;
}

	
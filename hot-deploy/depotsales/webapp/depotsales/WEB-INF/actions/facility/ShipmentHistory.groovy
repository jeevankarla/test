import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import org.ofbiz.party.party.PartyHelper;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import java.io.ObjectOutputStream.DebugTraceInfoStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.service.GenericDispatcher;

/*HttpServletRequest httpRequest = (HttpServletRequest) request;
HttpServletResponse httpResponse = (HttpServletResponse) response;
dctx = dispatcher.getDispatchContext();
delegator = DelegatorFactory.getDelegator("default#NHDC");*/

isReport=parameters.isReport;
isFormSubmitted=parameters.isFormSubmitted;
bId=parameters.bId;
filterType=parameters.filterType; 
context.isFormSubmitted=isFormSubmitted;

JSONObject stateJSON = new JSONObject();
JSONObject partyNameObj2 = new JSONObject();
JSONArray cutomerJSON = new JSONArray();

if(UtilValidate.isNotEmpty(bId)){
	conditionList =[];
	if("By_Branch".equals(filterType)){
		partyRelationship1 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , bId)  , UtilMisc.toSet("partyIdTo"), null, null, false );
		partyIds = EntityUtil.getFieldListFromEntityList(partyRelationship1, "partyIdTo", true);
	}
	if("By_Ro".equals(filterType)){
		partyRelationship1 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , bId)  , UtilMisc.toSet("partyIdTo"), null, null, false );
		partyIds = EntityUtil.getFieldListFromEntityList(partyRelationship1, "partyIdTo", true);
		partyRelationship2 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,partyIds)  , UtilMisc.toSet("partyIdTo"), null, null, false );
		if(UtilValidate.isNotEmpty(partyRelationship2)){
			partyIds = EntityUtil.getFieldListFromEntityList(partyRelationship2, "partyIdTo", true);
		}
	}
	if("By_State".equals(filterType)){
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, bId));
		conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "INT%"));
		stateWiseRosAndBranchList = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(stateWiseRosAndBranchList)){
			List roAndBranchIds = EntityUtil.getFieldListFromEntityList(stateWiseRosAndBranchList, "partyId", true);
			conditionList.clear()
			conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN , roAndBranchIds))
			conditionList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_LIKE , "INT%"))
			partyRelationship1 = delegator.findList("PartyRelationship",EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false );
			partyIds = EntityUtil.getFieldListFromEntityList(partyRelationship1, "partyIdTo", true);
		}	
	}
	Condition1=[];
	Condition1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "BILL_TO_CUSTOMER"));
	Condition1.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIds));
	customersList=delegator.findList("PartyRole", EntityCondition.makeCondition(Condition1, EntityOperator.AND),null,null,null,false);
	partyIds2 = EntityUtil.getFieldListFromEntityList(customersList, "partyId", true);
	customersList = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN , partyIds2), null, null, null, false);
	if(customersList){
		customersList.each{ supplier ->
			JSONObject newObj = new JSONObject();
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , supplier.ownerPartyId));
			/*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "PARTY_ENABLED"));*/
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			partyList = delegator.findList("Party", condition, null, null, null, false);
			partyDetails = EntityUtil.getFirst(partyList);
			if(UtilValidate.isNotEmpty(partyDetails)){
				newObj.put("value",partyDetails.partyId);
				partyName=PartyHelper.getPartyName(delegator, partyDetails.partyId, false);
				newObj.put("label",partyName+"["+partyDetails.partyId+"]");
				cutomerJSON.add(newObj);
				partyNameObj2.put(partyDetails.partyId,partyName);
			}
		}
	}
	request.setAttribute("stateJSON",cutomerJSON);
	request.setAttribute("partyNameObj2",partyNameObj2);
	return "sucess";
}
if(UtilValidate.isEmpty(isReport)){
	List formatList = [];
	
	List<GenericValue> partyClassificationList = null;
		partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("BRANCH_OFFICE")), UtilMisc.toSet("partyId"), null, null,false);
	if(partyClassificationList){
		for (eachList in partyClassificationList) {
			formatMap = [:];
			partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
			formatMap.put("productStoreName",partyName);
			formatMap.put("payToPartyId",eachList.get("partyId"));
			formatList.addAll(formatMap);
		}
	}
	context.formatList = formatList;
JSONArray supplierJSON=new JSONArray();
JSONObject partyNameObj = new JSONObject();
	Condition=[];
	Condition.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "SUPPLIER"));
	supplierList=delegator.findList("PartyRole", EntityCondition.makeCondition(Condition, EntityOperator.AND),null,null,null,false);
	if(supplierList){
		supplierList.each{ supplier ->
			JSONObject newObj = new JSONObject();
			conditionList =[];
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , supplier.partyId));
			/*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "PARTY_ENABLED"));*/
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			partyList = delegator.findList("Party", condition, null, null, null, false);
			partyDetails = EntityUtil.getFirst(partyList);
			if(UtilValidate.isNotEmpty(partyDetails)){
				newObj.put("value",partyDetails.partyId);
				partyName=PartyHelper.getPartyName(delegator, partyDetails.partyId, false);
				newObj.put("label",partyName+"["+partyDetails.partyId+"]");
				supplierJSON.add(newObj);
				partyNameObj.put(partyDetails.partyId,partyName);
			}
		}
	}
context.supplierJSON=supplierJSON;
context.partyNameObj=partyNameObj;
}
List formatRList = [];
List formatBList = [];
List<GenericValue> partyClassificationList = null;
	partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("REGIONAL_OFFICE","BRANCH_OFFICE")), UtilMisc.toSet("partyId","partyClassificationGroupId"), null, null,false);
if(partyClassificationList){
	for (eachList in partyClassificationList) {
		formatMap = [:];
		partyName = PartyHelper.getPartyName(delegator, eachList.get("partyId"), false);
		formatMap.put("productStoreName",partyName);
		formatMap.put("payToPartyId",eachList.get("partyId"));
		if(eachList.partyClassificationGroupId=="REGIONAL_OFFICE"){
			formatRList.addAll(formatMap);
		}else{
			formatBList.addAll(formatMap);
		}
	}
}
context.formatRList = formatRList;
context.formatBList = formatBList;
daystart = null;
dayend = null;
period=parameters.period;
context.period=period;
periodFrmDate=null;
isFormSubmitted=parameters.isFormSubmitted;

context.period=period;
periodFrmDate=null;
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
	conditionList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
	statesList = delegator.findList("Geo",EntityCondition.makeCondition(conditionList,EntityOperator.AND),null,null,null,false);
	statesIdsList=EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);
	
	JSONArray stateListJSON = new JSONArray();
	statesList.each{ eachState ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",eachState.geoId);
			newObj.put("label",eachState.geoName);
			stateListJSON.add(newObj);
	}
	context.stateListJSON = stateListJSON;
	
if(UtilValidate.isNotEmpty(isFormSubmitted) && "Y".equals(isFormSubmitted)){
	conditionList=[];
	branchId=parameters.branchId2;
	customerId=parameters.customer;
	SupplierId=parameters.Supplier;
	
	branchIds=[];
	branchId = parameters.branchId2;
	searchType = parameters.searchType;
	context.searchType=searchType;
	branchIdName =  PartyHelper.getPartyName(delegator, branchId, false);
	context.branchId=branchId;
	context.branchIdName=branchIdName;
	regionId = parameters.regionId;
	stateId = parameters.stateId; 
	regionIdName =  PartyHelper.getPartyName(delegator, regionId, false);
	context.regionId=regionId;
	context.regionIdName=regionIdName;
	if(UtilValidate.isNotEmpty(branchId) && "BY_BO".equals(searchType)){
		branchIds.add(branchId)
		context.searchTypeName="By Branch"
	}
	if(UtilValidate.isNotEmpty(regionId) && "BY_RO".equals(searchType)){
		partyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,regionId), UtilMisc.toSet("partyIdTo"), null, null,false);
		branchIds=EntityUtil.getFieldListFromEntityList(partyRelationship, "partyIdTo", true);
		context.searchTypeName="By Regional Office";
	}
	if(UtilValidate.isNotEmpty(stateId) && "BY_STATE".equals(searchType)){
		GenericValue state=delegator.findOne("Geo",[geoId:stateId],false);
		context.stateId=stateId;
		context.stateIdName=state.geoName;
		context.searchTypeName="By State";
		roIdsList=[];
		branchIdsList=[];
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, stateId));
		conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "INT%"));
		stateWiseRosAndBranchList = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(stateWiseRosAndBranchList)){
			List roAndBranchIds = EntityUtil.getFieldListFromEntityList(stateWiseRosAndBranchList, "partyId", true);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, roAndBranchIds));
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, "BRANCH_OFFICE"));
			List<GenericValue> partyClassicationForBranch= delegator.findList("PartyClassification", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyClassicationForBranch)){
				 branchIdsList = EntityUtil.getFieldListFromEntityList(partyClassicationForBranch, "partyId", true);
			}
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, roAndBranchIds));
			conditionList.add(EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, "REGIONAL_OFFICE"));
			List<GenericValue> partyClassicationForRo= delegator.findList("PartyClassification", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyClassicationForRo)){
				roIdsList = EntityUtil.getFieldListFromEntityList(partyClassicationForRo, "partyId", true);
			}
			List<GenericValue> partyGroupRo=null;
			List<GenericValue> partyGroupBranch=null;
			if(UtilValidate.isNotEmpty(roIdsList)){
				stateRosList = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityOperator.IN, roIdsList), UtilMisc.toSet("partyId","groupName"), null, null, false);
				stateRosList.each{ eachState ->
					branchIds.add(eachState.partyId);
				}
			}
			if(UtilValidate.isNotEmpty(branchIdsList)){
				stateBranchsList = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityOperator.IN, branchIdsList), UtilMisc.toSet("partyId","groupName"), null, null, false);
				stateBranchsList.each{ eachState ->
					branchIds.add(eachState.partyId);
				}
			}
		}
	}
	dayend =UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	
	if("One_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-30);
		context.periodName="Last One Month";
	}
	if("Two_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-60);
		context.periodName="Last Two Months";
	}
	if("Three_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-90);
		context.periodName="Last Three  Months";
	}
	if("Six_Month".equals(period)){
		daystart = UtilDateTime.addDaysToTimestamp(dayend,-180);
		context.periodName="Last  Six Months";
	}
	String supplierName = PartyHelper.getPartyName(delegator,SupplierId,false);
	String customerName = PartyHelper.getPartyName(delegator,customerId,false);
	partygroup = delegator.findOne("PartyGroup",["partyId":branchId],false);
	if(UtilValidate.isNotEmpty(partygroup)){
		context.branchIdName=partygroup.groupName
	}
	
	context.SupplierIdName=supplierName
	context.customerName=customerName
	context.branchId=branchId 
	context.SupplierId=SupplierId
	context.customerId=customerId
	
	partyRelationship1 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN , branchIds)  , UtilMisc.toSet("partyIdTo"), null, null, false );
	partyIds = EntityUtil.getFieldListFromEntityList(partyRelationship1, "partyIdTo", true);
	partyRelationship2 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN ,partyIds)  , UtilMisc.toSet("partyIdTo"), null, null, false );
	if(UtilValidate.isNotEmpty(partyRelationship2)){
		partyIds = EntityUtil.getFieldListFromEntityList(partyRelationship2, "partyIdTo", true);
	}
	Condition1=[];
	Condition1.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "BILL_TO_CUSTOMER"));
	Condition1.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIds));
	customersList=delegator.findList("PartyRole", EntityCondition.makeCondition(Condition1, EntityOperator.AND),null,null,null,false);
	partyIds2 = EntityUtil.getFieldListFromEntityList(customersList, "partyId", true);
	customersList = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN , partyIds2), null, null, null, false);
	if(customersList){
		customersList.each{ supplier ->
			JSONObject newObj = new JSONObject();
			conditionList =[];
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , supplier.ownerPartyId));
			/*conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "PARTY_ENABLED"));*/
			condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			partyList = delegator.findList("Party", condition, null, null, null, false);
			partyDetails = EntityUtil.getFirst(partyList);
			if(UtilValidate.isNotEmpty(partyDetails)){
				newObj.put("value",partyDetails.partyId);
				partyName=PartyHelper.getPartyName(delegator, partyDetails.partyId, false);
				newObj.put("label",partyName+"["+partyDetails.partyId+"]");
				cutomerJSON.add(newObj);
				partyNameObj2.put(partyDetails.partyId,partyName);
			}
		}
	}
	context.partyNameObj2=partyNameObj2;
	context.cutomerJSON=cutomerJSON
	conditionList.clear();
	shipmentIds=[];
	shipmentDetailsItr=null;
	if(UtilValidate.isNotEmpty(partyIds)){ 
		/*conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, purchaseOrderIds));*/
		if(UtilValidate.isNotEmpty(daystart)){
			conditionList.add(EntityCondition.makeCondition("supplierInvoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,daystart));
			conditionList.add(EntityCondition.makeCondition("supplierInvoiceDate", EntityOperator.LESS_THAN_EQUAL_TO,dayend));
		}
		if(UtilValidate.isNotEmpty(SupplierId)){
			shipment = delegator.findList("Shipment",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , SupplierId)  , UtilMisc.toSet("shipmentId"), null, null, false );
			shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		}
		innerCondition=[];
		if(UtilValidate.isNotEmpty(customerId) && partyIds.contains(customerId)){
			innerCondition.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , customerId))
		}else{
			innerCondition.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , partyIds))
		}	
		if(UtilValidate.isNotEmpty(shipmentIds)){
			innerCondition.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipmentIds))
		}
		shipment = delegator.findList("Shipment",EntityCondition.makeCondition(innerCondition, EntityOperator.AND)  , UtilMisc.toSet("shipmentId"), null, null, false );
		shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SR_CANCELLED"));
		shipmentDetailsItr = delegator.find("ShipmentAndReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
	}
	receiptList=[];
	rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
	while(UtilValidate.isNotEmpty(shipmentDetailsItr) && shipmentDetailsItr.hasNext()){
		GenericValue shipmentDetails=shipmentDetailsItr.next();
		tempMap=[:];
		tempMap.put("datetimeReceived", UtilDateTime.toDateString(shipmentDetails.supplierInvoiceDate,"dd-MM-yyyy"))
		tempMap.put("statusId", shipmentDetails.statusId);
		tempMap.put("shipmentId", shipmentDetails.shipmentId);
		tempMap.put("quantityAccepted", shipmentDetails.quantityAccepted);
		orderItem = delegator.findOne("OrderItem",["orderId":shipmentDetails.orderId,"orderItemSeqId":shipmentDetails.orderItemSeqId],false);
		tempMap.put("unitPrice", (orderItem.unitPrice).setScale(2, rounding));
		tempMap.put("productId", shipmentDetails.productId);
		product = delegator.findOne("Product",["productId":shipmentDetails.productId],false);
		productCategory = delegator.findOne("ProductCategory",["productCategoryId":product.primaryProductCategoryId],false);
		tempMap.put("productName", product.productName);
		tempMap.put("categoryName", productCategory.categoryName);
		shipment = delegator.findOne("Shipment",["shipmentId":shipmentDetails.shipmentId],false);
		String supplier = PartyHelper.getPartyName(delegator,shipment.partyIdFrom,false);
		tempMap.put("supplier", supplier);
		String custName = PartyHelper.getPartyName(delegator,shipment.partyIdTo,false);
		tempMap.put("customerName", custName);
		orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , shipmentDetails.orderId)  , UtilMisc.toSet("orderNo"), null, null, false );
		ordHeadSeq=EntityUtil.getFirst(orderHeaderSequences);
		if(UtilValidate.isNotEmpty(ordHeadSeq)){
			tempMap.put("orderId", ordHeadSeq.orderNo);
		}
		receiptList.add(tempMap);
	}
	context.receiptList=receiptList;

}

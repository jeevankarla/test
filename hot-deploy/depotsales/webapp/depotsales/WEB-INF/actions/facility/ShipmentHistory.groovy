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

HttpServletRequest httpRequest = (HttpServletRequest) request;
HttpServletResponse httpResponse = (HttpServletResponse) response;
dctx = dispatcher.getDispatchContext();
delegator = DelegatorFactory.getDelegator("default#NHDC_LOCAL");


isReport=parameters.isReport;
isFormSubmitted=parameters.isFormSubmitted;
bId=parameters.bId;
context.isFormSubmitted=isFormSubmitted;

JSONObject stateJSON = new JSONObject();
JSONObject partyNameObj2 = new JSONObject();
JSONArray cutomerJSON = new JSONArray();

if(UtilValidate.isNotEmpty(bId)){
	partyRelationship1 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , bId)  , UtilMisc.toSet("partyIdTo"), null, null, false );
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
	request.setAttribute("stateJSON",cutomerJSON);
	request.setAttribute("partyNameObj2",partyNameObj2);
	return "sucess";
}

if(UtilValidate.isEmpty(isReport)){
	List formatList = [];
	
	List<GenericValue> partyClassificationList = null;
		partyClassificationList = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.IN, UtilMisc.toList("REGIONAL_OFFICE","BRANCH_OFFICE")), UtilMisc.toSet("partyId"), null, null,false);
	if(partyClassificationList){
		for (eachList in partyClassificationList) {
			//Debug.log("eachList========================"+eachList.get("partyId"));
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

if(UtilValidate.isNotEmpty(isFormSubmitted) && "Y".equals(isFormSubmitted)){
	conditionList=[];
	branchId=parameters.branchId2;
	customerId=parameters.customer;
	SupplierId=parameters.Supplier;
	fromDateStr=parameters.fromDate;
	thruDateStr=parameters.thruDate;
	Timestamp fromDate;
	Timestamp thruDate;
	daystart = null;
	dayend = null; 
	String supplierName = PartyHelper.getPartyName(delegator,SupplierId,false);
	String customerName = PartyHelper.getPartyName(delegator,customerId,false);
	partygroup = delegator.findOne("PartyGroup",["partyId":branchId],false);
	context.branchIdName=partygroup.groupName
	context.SupplierIdName=supplierName
	context.customerName=customerName
	context.fromDateStr=fromDateStr
	context.thruDateStr=thruDateStr
	context.branchId=branchId 
	context.SupplierId=SupplierId
	context.customerId=customerId
	
	partyRelationship1 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , branchId)  , UtilMisc.toSet("partyIdTo"), null, null, false );
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
	
	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
	if(UtilValidate.isNotEmpty(fromDateStr)){
		try {
			fromDate = new java.sql.Timestamp(sdf.parse(fromDateStr).getTime());
			daystart = UtilDateTime.getDayStart(fromDate);
			 } catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			}
	}
	if(UtilValidate.isNotEmpty(thruDateStr)){
	   try {
		   thruDate = new java.sql.Timestamp(sdf.parse(thruDateStr).getTime());
		   dayend = UtilDateTime.getDayEnd(thruDate);
	   } catch (ParseException e) {
		       Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
			}
	}
	robranchIds=[];
	if(UtilValidate.isNotEmpty(branchId)){
		partyRelationship = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , branchId)  , UtilMisc.toSet("partyIdTo"), null, null, false );
		robranchIds = EntityUtil.getFieldListFromEntityList(partyRelationship, "partyIdTo", true);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
		if(UtilValidate.isNotEmpty(robranchIds)){
			partyRelationship2 = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN , robranchIds)  , UtilMisc.toSet("partyIdTo"), null, null, false );
			if(UtilValidate.isNotEmpty(partyRelationship2)){
				robranchIds = EntityUtil.getFieldListFromEntityList(partyRelationship2, "partyIdTo", true);
			}
		}	
	}
	conditionList.clear();
	shipmentIds=[];
	shipmentDetailsItr=null;
	if(UtilValidate.isNotEmpty(robranchIds)){ 
		/*conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, purchaseOrderIds));*/
		if(UtilValidate.isNotEmpty(daystart)){
			conditionList.add(EntityCondition.makeCondition("supplierInvoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
			conditionList.add(EntityCondition.makeCondition("supplierInvoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
		}
		if(UtilValidate.isNotEmpty(SupplierId)){
			shipment = delegator.findList("Shipment",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , SupplierId)  , UtilMisc.toSet("shipmentId"), null, null, false );
			shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		}
		innerCondition=[];
		if(UtilValidate.isNotEmpty(customerId) && robranchIds.contains(customerId)){
			innerCondition.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , customerId))
		}else{
			innerCondition.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN , robranchIds))
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
		//tempMap.put("receiptId", shipmentDetails.receiptId)
		tempMap.put("datetimeReceived", UtilDateTime.toDateString(shipmentDetails.supplierInvoiceDate,"dd-MM-yyyy"))
		tempMap.put("statusId", shipmentDetails.statusId);
		tempMap.put("shipmentId", shipmentDetails.shipmentId)
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

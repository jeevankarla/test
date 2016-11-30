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



stateBranchsList=[];
stateRosList =[];
conditionDeopoList = [];
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
conditionDeopoList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
statesList = delegator.findList("Geo",conditionDepo,null,null,null,false);
statesIdsList=EntityUtil.getFieldListFromEntityList(statesList, "geoId", true);

JSONObject stateJSON = new JSONObject();

for(stateid in statesIdsList){
	result = dispatcher.runSync("getRegionalAndBranchOfficesByState",UtilMisc.toMap("state",stateid,"userLogin",userLogin));
	stateBranchsList=result.get("stateBranchsList");
	stateRosList=result.get("stateRosList");
	
	JSONArray stateBranchAndRoJSON = new JSONArray();
	stateBranchsList.each{ eachState ->
			JSONObject newObj = new JSONObject();
			newObj.put("value",eachState.partyId);
			newObj.put("label",eachState.groupName);
			stateBranchAndRoJSON.add(newObj);
	}
	stateRosList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.partyId);
		newObj.put("label",eachState.groupName);
		stateBranchAndRoJSON.add(newObj);
	}
	stateJSON.put(stateid, stateBranchAndRoJSON)
}
context.stateJSON = stateJSON;
JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoId);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;

JSONArray supplierJSON = new JSONArray();
JSONObject partyNameObj = new JSONObject();

Condition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId", "SUPPLIER")],EntityOperator.AND);
supplierList=delegator.findList("PartyRole",Condition,null,null,null,false);
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
JSONArray cutomerJSON = new JSONArray();
JSONObject partyNameObj2 = new JSONObject();

Condition1 = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeId", "BILL_TO_CUSTOMER")],EntityOperator.AND);
customersList=delegator.findList("PartyRole",Condition1,null,null,null,false);
partyIds = EntityUtil.getFieldListFromEntityList(customersList, "partyId", true);
customersList = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN , partyIds), null, null, null, false);
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
context.supplierJSON=supplierJSON;
context.cutomerJSON=cutomerJSON;
isFormSubmitted=parameters.isFormSubmitted;
context.isFormSubmitted=isFormSubmitted;
if(UtilValidate.isNotEmpty(isFormSubmitted) && "Y".equals(isFormSubmitted)){
	
	state=parameters.state;
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
	String cutomerName = PartyHelper.getPartyName(delegator,customerId,false);
	partygroup = delegator.findOne("PartyGroup",["partyId":branchId],false);
	geo = delegator.findOne("Geo",["geoId":state],false);
	if(UtilValidate.isNotEmpty(fromDateStr)){
		context.stateName=geo.geoName;
	}
	context.branchIdName=partygroup.groupName
	context.SupplierIdName=supplierName
	context.cutomerName=cutomerName
	context.fromDateStr=fromDateStr
	context.thruDateStr=thruDateStr
	context.state=state
	context.branchId=branchId 
	context.SupplierId=SupplierId
	context.customerId=customerId
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
	purchaseOrderIds=[];
	if(UtilValidate.isNotEmpty(branchId) && UtilValidate.isEmpty(SupplierId)){
		partyRelationship = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , branchId)  , UtilMisc.toSet("partyIdTo"), null, null, false );
		robranchIds = EntityUtil.getFieldListFromEntityList(partyRelationship, "partyIdTo", true);
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
		if(UtilValidate.isNotEmpty(robranchIds)){
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,robranchIds));
		}else{
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,branchId));
		}	
		orderRole = delegator.find("OrderRole", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("orderId"), null, null);
		saleOrderIds = EntityUtil.getFieldListFromEntityListIterator(orderRole, "orderId", true);
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.IN, saleOrderIds));
		conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS,"BackToBackOrder"));
		orderAssoc = delegator.find("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("orderId"), null, null);
		purchaseOrderIds = EntityUtil.getFieldListFromEntityListIterator(orderAssoc, "orderId", true);
		
		
	}
	conditionList.clear();
	shipmentIds=[];
	if(UtilValidate.isNotEmpty(purchaseOrderIds)){
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, purchaseOrderIds));
	}
	if(UtilValidate.isNotEmpty(daystart)){
		conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
		conditionList.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
	}
	if(UtilValidate.isNotEmpty(SupplierId)){
		shipment = delegator.findList("Shipment",EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , SupplierId)  , UtilMisc.toSet("shipmentId"), null, null, false );
		shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	}
	if(UtilValidate.isNotEmpty(customerId)){
		innerCondition=[];
		innerCondition.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , customerId))
		if(UtilValidate.isNotEmpty(shipmentIds)){
			innerCondition.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipmentIds))
		}
		shipment = delegator.findList("Shipment",EntityCondition.makeCondition(innerCondition, EntityOperator.AND)  , UtilMisc.toSet("shipmentId"), null, null, false );
		shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIds));
	}
	shipmentDetailsItr = delegator.find("ShipmentAndReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, null);
	receiptList=[];
	
	
	rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
	while(shipmentDetailsItr.hasNext()){
		GenericValue shipmentDetails=shipmentDetailsItr.next();
		tempMap=[:];
		//tempMap.put("receiptId", shipmentDetails.receiptId)
		tempMap.put("datetimeReceived", UtilDateTime.toDateString(shipmentDetails.datetimeReceived,"dd-MM-yyyy"))
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
		String customerName = PartyHelper.getPartyName(delegator,shipment.partyIdTo,false);
		tempMap.put("customerName", customerName);
		orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , shipmentDetails.orderId)  , UtilMisc.toSet("orderNo"), null, null, false );
		ordHeadSeq=EntityUtil.getFirst(orderHeaderSequences);
		if(UtilValidate.isNotEmpty(ordHeadSeq)){
			tempMap.put("orderId", ordHeadSeq.orderNo);
		}
		receiptList.add(tempMap);
	}
	context.receiptList=receiptList;

}


import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;

import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.widget.WidgetWorker.Parameter;
import java.util.Map.Entry;

SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
dayend = null;
daystart = null;
Timestamp fromDate;
Timestamp thruDate;
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
branchIds=[];
branchId = parameters.branchId;

DateMap = [:];
branchName = "";

if(branchId){
branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
branchName = branch.get("groupName");
DateMap.put("branchName", branchName);
}
branchList = [];
condListb = [];
if(branchId){
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);

branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

if(!branchList)
branchList.add(branchId);
}
/*Debug.log("branchList=================="+branchList);*/

branchBasedWeaversList = [];
condListb1 = [];
if(branchId){
condListb1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
condListb1.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
condListb = EntityCondition.makeCondition(condListb1, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

if(!branchBasedWeaversList)
branchBasedWeaversList.add(branchId);
}

/*Debug.log("branchBasedWeaversList=================="+branchBasedWeaversList);*/

productCategory=parameters.productCategory;
shipmentstate = parameters.shipmentstate;
context.partyfromDate=partyfromDate;
context.partythruDate=partythruDate;
context.shipmentstate=shipmentstate;
productCategoryDetails = delegator.findOne("ProductCategory",[productCategoryId : productCategory] , false);
if(UtilValidate.isNotEmpty(productCategoryDetails)){
	prodCatName=productCategoryDetails.description
}else{
	 prodCatName="PRODUCTS OTHER THAN SILK AND JUTE"
}
context.prodCatName=prodCatName;
conditionList = [];
	
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,branchId));
partyRelationship = delegator.find("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("partyIdTo","partyIdFrom"), null, null);
branchIds = EntityUtil.getFieldListFromEntityListIterator(partyRelationship, "partyIdTo", true);
partyRelationship2 = delegator.find("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN,branchIds), null, UtilMisc.toSet("partyIdTo"), null, null);
if(UtilValidate.isNotEmpty(partyRelationship2)){
	branchIds = EntityUtil.getFieldListFromEntityListIterator(partyRelationship2, "partyIdTo", true);
}
productIds = [];
productCategoryIds = [];
conditionList.clear();
if(productCategory != "OTHER"){
	conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}else if(productCategory == "OTHER"){
	conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","JUTE_YARN"]));
	condition1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ProductCategory = delegator.findList("ProductCategory", condition1,UtilMisc.toSet("productCategoryId"), null, null, false);
	productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
}
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
ProductCategoryMember = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productId"), null, null, false);
productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
  
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
	try {
		//daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
		 } catch (ParseException e) {
			 //////Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
		}
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   try {
	 //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
	   dayend = UtilDateTime.getDayEnd(thruDate);
   } catch (ParseException e) {
	   //////Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
}
context.daystart=daystart
context.dayend=dayend
branchContext=[:];
branchContext.put("branchId",branchId);

BOAddress="";
BOEmail="";
try{
	resultCtx = dispatcher.runSync("getBoHeader", branchContext);
	if(ServiceUtil.isError(resultCtx)){
		Debug.logError("Problem in BO Header ", module);
		return ServiceUtil.returnError("Problem in fetching financial year ");
	}
	if(resultCtx.get("boHeaderMap")){
		boHeaderMap=resultCtx.get("boHeaderMap");
		
		if(boHeaderMap.get("header0")){
			BOAddress=boHeaderMap.get("header0");
		}
		if(boHeaderMap.get("header1")){
			BOEmail=boHeaderMap.get("header1");
		}
	}
}catch(GenericServiceException e){
	Debug.logError(e, module);
	return ServiceUtil.returnError(e.getMessage());
}
context.BOAddress=BOAddress;
context.BOEmail=BOEmail;


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchList));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
salesOrderDetailsList = delegator.findList("OrderHeaderItemAndRoles", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
OrderIdList = EntityUtil.getFieldListFromEntityList(salesOrderDetailsList, "orderId", true);


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.IN, OrderIdList));
conditionList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
purchaseOrdersList = delegator.findList("OrderAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
purchaseOdrIds = EntityUtil.getFieldListFromEntityList(purchaseOrdersList, "orderId", true);


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, purchaseOdrIds));
conditionList.add(EntityCondition.makeCondition("shStatusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
shipmentDetailsForOrders = delegator.findList("ShipmentAndReceipt", EntityCondition.makeCondition(conditionList, EntityOperator.AND), UtilMisc.toSet("supplierInvoiceDate","shipmentId","quantityAccepted","partyIdFrom","orderId","partyIdTo"),, null, null, false);

finalList=[];
orderIdsCheck=[];

if(UtilValidate.isNotEmpty(parameters.header)&&parameters.header.equals("required")){
 headerData=[:];
 headerData.put("IndentNo", "IndentNo");
 headerData.put("IndentDate", "IndentDate");
 headerData.put("indQty", "indQty");
 headerData.put("indUnitPrice", "indUnitPrice");
 headerData.put("indentValue", "indentValue");
 headerData.put("PoNo", "PoNo");
 headerData.put("PoDate", "PoDate");
 headerData.put("supplier", "supplier");
 headerData.put("shipmentDate", "shipmentDate");
 headerData.put("shipQty", "shipQty");
 headerData.put("DurBWSoAndPo", "DurBWSoAndPo");
 headerData.put("DurBwSoAndShip", "DurBwSoAndShip");
 finalList.add(headerData);
 }
for(saleOrder in salesOrderDetailsList){
	Map tempMap = FastMap.newInstance();
	indQty=0;
	indUnitPrice=0;
	indentValue=0;
	shipQty=0;
	
	orderNo ="NA";
	orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , saleOrder.orderId)  , null, null, null, false );
	if(UtilValidate.isNotEmpty(orderHeaderSequences)){
		orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
		orderNo = orderSeqDetails.orderNo;
	}
	exprCondList=[];
	exprCondList.add(EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS, saleOrder.orderId));
	exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
	OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
	POorder="NA";
	isgeneratedPO="N";
	if(OrderAss){
		POorder=OrderAss.get("orderId");
		isgeneratedPO = "Y";
	}
	poSequenceNo="NA";
	poOrderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , POorder)  , null, null, null, false );
	if(UtilValidate.isNotEmpty(poOrderHeaderSequences)){
		poOrderSeqDetails = EntityUtil.getFirst(poOrderHeaderSequences);
		poSequenceNo = poOrderSeqDetails.orderNo;
	}
	tempMap.put("IndentNo", orderNo);
	tempMap.put("IndentDate", UtilDateTime.toDateString(saleOrder.orderDate,"dd-MM-yyyy"));
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,saleOrder.orderId));
	salesOrderDetailsFilteredList =EntityUtil.filterByCondition(salesOrderDetailsList, EntityCondition.makeCondition(conditionList, EntityOperator.AND));
	if(UtilValidate.isNotEmpty(salesOrderDetailsFilteredList) && salesOrderDetailsFilteredList.size()>1){
		noOfItems=salesOrderDetailsFilteredList.size();
		for(eachItem in salesOrderDetailsFilteredList){
			indQty=indQty+eachItem.quantity;
			indUnitPrice=indUnitPrice+eachItem.unitPrice;
		}
		indUnitPrice=indUnitPrice/noOfItems;
	}else{
		indQty=saleOrder.quantity;
		indUnitPrice=saleOrder.unitPrice;
	}
	indentValue=indentValue+((indQty)*indUnitPrice);
	tempMap.put("indUnitPrice", indUnitPrice);
	tempMap.put("indQty", indQty);
	tempMap.put("indentValue", indentValue);
	purchaseOrderDetails =EntityUtil.filterByCondition(purchaseOrdersList, EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS,saleOrder.orderId));
	purchaseOrderDetail=EntityUtil.getFirst(purchaseOrderDetails);
	if(UtilValidate.isNotEmpty(purchaseOrderDetail)){
		orderHeader = delegator.findOne("OrderHeader",[orderId : purchaseOrderDetail.orderId] , false);
		tempMap.put("PoDate", UtilDateTime.toDateString(orderHeader.orderDate,"dd-MM-yyyy"));
		tempMap.put("PoNo",poSequenceNo);
		soPoIntvlDays=UtilDateTime.getIntervalInDays(saleOrder.orderDate,orderHeader.orderDate)+1;
		tempMap.put("DurBWSoAndPo", soPoIntvlDays);
		shipmentDetails =EntityUtil.filterByCondition(shipmentDetailsForOrders, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderHeader.orderId));
		if(UtilValidate.isNotEmpty(shipmentDetails)){
			shipmentDetail=EntityUtil.getFirst(shipmentDetails);
			String supplier = PartyHelper.getPartyName(delegator,shipmentDetail.partyIdFrom,false);
			tempMap.put("supplier", supplier);
			tempMap.put("shipmentDate", UtilDateTime.toDateString(shipmentDetail.supplierInvoiceDate,"dd-MM-yyyy"));
			poShipInvlDays=UtilDateTime.getIntervalInDays(orderHeader.orderDate,shipmentDetail.supplierInvoiceDate)+1;
			tempMap.put("DurBwSoAndShip", poShipInvlDays);
			for(eachShipment in shipmentDetails){
				shipQty=shipQty+eachShipment.quantityAccepted;
			}
			tempMap.put("shipQty", shipQty);
		}
	}
	
	if(!orderIdsCheck.contains(saleOrder.orderId)){
		if(UtilValidate.isNotEmpty(indQty) && UtilValidate.isNotEmpty(shipQty)){
				remaining=indQty-shipQty;
			}
		if(parameters.shipmentstate=="Pending"){
		if(remaining>0){
			finalList.add(tempMap);
			}
		}
		if(parameters.shipmentstate=="Completed"){
		if(remaining==0){
			finalList.add(tempMap);
			}
		}
	}
	orderIdsCheck.add(saleOrder.orderId)
context.finalList=finalList;
}














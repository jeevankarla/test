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
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;


SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
dayend = null;
daystart = null;

Timestamp fromDate;
Timestamp thruDate;

DateList=[];
DateMap = [:];
partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;
partyId=parameters.partyId;
state=parameters.state;
productCategory=parameters.productCategory;


inputList = [];

if(partyfromDate)
inputList.add(partyfromDate);
if(partythruDate)
inputList.add(partythruDate);
if(partyId)
inputList.add(partyId);
if(state)
inputList.add(state);
if(productCategory)
inputList.add(productCategory);
if(state)
inputList.add(state);
if(productCategory)
inputList.add(productCategory);



DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);

DateList.add(DateMap);
context.DateList=DateList;
branchId = parameters.branchId;

if(branchId)
inputList.add(branchId);




conditionDeopoList = [];
conditionDeopoList.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE,"IN-%"));
conditionDeopoList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS,"STATE"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
statesList = delegator.findList("Geo",conditionDepo,null,null,null,false);


JSONArray stateListJSON = new JSONArray();
statesList.each{ eachState ->
		JSONObject newObj = new JSONObject();
		newObj.put("value",eachState.geoId);
		newObj.put("label",eachState.geoName);
		stateListJSON.add(newObj);
}
context.stateListJSON = stateListJSON;



//Debug.log("state================"+state);

//partyIdUser = userLogin.get("partyId");

resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));

Map formatMap = [:];
List formatList = [];
List productStoreList = resultCtx.get("productStoreList");
context.productStoreList = productStoreList;

for (eachList in productStoreList) {
	formatMap = [:];
	formatMap.put("productStoreName",eachList.get("storeName"));
	formatMap.put("payToPartyId",eachList.get("payToPartyId"));
	formatList.addAll(formatMap);
}
roList = dispatcher.runSync("getRegionalOffices",UtilMisc.toMap("userLogin",userLogin));
roPartyList = roList.get("partyList");

for(eachRO in roPartyList){
	formatMap = [:];
	formatMap.put("productStoreName",eachRO.get("groupName"));
	formatMap.put("payToPartyId",eachRO.get("partyId"));
	formatList.addAll(formatMap);
}
context.formatList = formatList;



//Debug.log("partyfromDate================"+partyfromDate);

//Debug.log("partythruDate================"+partythruDate);


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



branchBasedWeaversList = [];
condListb = [];
if(branchId){
condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

}


//Debug.log("branchList=================="+branchList);


//Debug.log("branchBasedWeaversList================"+branchBasedWeaversList.size());


productIds = [];

productCategoryIds = [];

condListCat = [];

if(!partyId){
	if(productCategory != "OTHER"){
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	}else if(productCategory == "OTHER"){
	
		condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","JUTE_YARN"]));
		condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
		
		productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
	
	}
	
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryMember", condList1,UtilMisc.toSet("productId"), null, null, false);
	
	productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
	
}
////////Debug.log("productIds================"+productIds.size());
  
daystart = null;
dayend = null;
if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
  
	try {
		//daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		daystart = UtilDateTime.getDayStart(fromDate);
		 } catch (ParseException e) {
			 ////////////Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			 }
   
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   
   try {
	 //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
	   
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
	   
	   dayend = UtilDateTime.getDayEnd(thruDate);
	   
   } catch (ParseException e) {
	   ////////////Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
}
  

//Debug.log("daystart================"+daystart);

//Debug.log("dayend================"+dayend);

if(daystart == null && inputList.size() == 0 ){
//daystart = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

daystart = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -30));

dayend = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
}


//Debug.log("daystart================"+daystart);

//Debug.log("dayend================"+dayend);


reimbursmentPercentage = [:];
reimbursmentPercentage.put("SILK", 1);
reimbursmentPercentage.put("JUTE_YARN", 10);


branchpartyIdsList = [];

if(state && !partyId){
	
	condListb4 = [];
	if(branchBasedWeaversList)
	condListb4.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
	condListb4.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	if(state == "IN-TS")
	condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, "IN-TNG"));
	else if(state)
	condListb4.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
	condListb = EntityCondition.makeCondition(condListb4, EntityOperator.AND);
	PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", condListb, null, UtilMisc.toSet("partyId"), null, null);
	
	branchBasedWeaversList = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);

	
	if(UtilValidate.isEmpty(branchBasedWeaversList)){
	 return "Selected Worng State";
	}
}


condList = [];
//condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, "11821"));

if(UtilValidate.isNotEmpty(daystart)){
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}

condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));

if(productIds)
condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));

/*if(branchList)
condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
if(partyId)
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
if(!partyId && branchpartyIdsList)
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchpartyIdsList));
*/

if(partyId){
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
}else if(!partyId && branchBasedWeaversList){
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));


////Debug.log("branchBasedWeaversList=========3434======="+branchBasedWeaversList.size());

}



condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));


cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

////Debug.log("cond================="+cond);


fieldsToSelect = ["invoiceId"] as Set;

invoice = delegator.find("InvoiceAndItem", cond, null, fieldsToSelect, null, null);
//////////////Debug.log("invoice========================="+invoice);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);
condList = [];
condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);


JSONObject shipmentReimbursementJson = new JSONObject();
//Debug.log("invoiceIds================="+invoiceIds.size());

finalList = [];
for (eachInvoiceList in Invoice) {
	
	tempMap=[:];
	
	tempMap.put("invoiceId", eachInvoiceList.invoiceId);
	
	
	billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachInvoiceList.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
	if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
		invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
		invoiceSequence = invoiceSeqDetails.invoiceSequence;
		
		////////Debug.log("invoiceSequence======2121==========="+invoiceSequence);
		
		tempMap.put("billno", invoiceSequence);
	}else{
		tempMap.put("billno", eachInvoiceList.invoiceId);
	}
	
	 tempMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoiceList.invoiceDate,"dd/MM/yyyy"));
		   
	 //////////Debug.log("eachInvoiceList.invoiceId================="+eachInvoiceList.invoiceId);
	 
	 
   
	condList.clear();
	
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	//condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL,null));
	//condList.add(EntityCondition.makeCondition("isAssessableValue", EntityOperator.EQUALS, "Y"));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	  
	
	//Debug.log("InvoiceItem================="+InvoiceItem);
	
	if(InvoiceItem){

		productId = "";
		if(InvoiceItem[0].productId)
		productId = InvoiceItem[0].productId;
		
		
		
	double invoiceAMT = 0;
	double invoiceQTY = 0;
	for (eachInvoiceItem in InvoiceItem) {
		
		//invoiceAMT = invoiceAMT+(eachInvoiceItem.amount*eachInvoiceItem.quantity);
		if(eachInvoiceItem.itemValue)
		invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
		invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
		
	}
	
	tempMap.put("invoiceAmount", invoiceAMT);
	
	//Debug.log("productId================="+productId);
	
	
	condListCat.clear();
	condListCat.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_EQUAL,null));
	condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
	ProductCategoryMember = delegator.findList("ProductCategoryAndMember", condList1,UtilMisc.toSet("productCategoryId"), null, null, false);

	
	//Debug.log("ProductCategoryMember================="+ProductCategoryMember);
	
	productCategoryId = ProductCategoryMember[0].productCategoryId;
	
	
	//Debug.log("productCategoryId================="+productCategoryId);
	
	
	ProductCategory = delegator.findOne("ProductCategory",[productCategoryId : productCategoryId] , false);
	
	primaryParentCategoryId = ProductCategory.primaryParentCategoryId;
	
	percentage = reimbursmentPercentage.get(primaryParentCategoryId);
	
	if(!percentage)
	percentage = 2.5;
	
	
	////Debug.log("percentage==============="+percentage);
	
	
	double eligibleAMT = 0;
	
	double maxAmt = 0;
	
//	eligibleAMT = (invoiceAMT*percentage)/100;
	
	maxAmt = (invoiceAMT*percentage)/100;
	
	
	tempMap.put("maxAmt", maxAmt);
	
	//Debug.log("eligibleAMT==============="+eligibleAMT);
	
	
	tempMap.put("invoiceQTY", invoiceQTY);
	
	
	//////////Debug.log("invoiceAMT================="+invoiceAMT);
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	//conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
   
	//////////Debug.log("OrderItemBilling======================"+OrderItemBilling);
		   
	 itemOrderId  = OrderItemBilling[0].orderId;
	 
	//Debug.log("itemOrderId================="+itemOrderId);
	 
	 conditionList = [];
	 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, itemOrderId));
	 conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
	 expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	 OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);
	 
	 partyId = "";
	 supplier = "";
	 
	 for (eachRole in OrderRoleList) {
		 
		 if(eachRole.roleTypeId == "SUPPLIER")
		  supplier = eachRole.get("partyId");
		 if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
		  partyId = eachRole.get("partyId");
		
	 }
	
	 
	 partyName = "";
	 if(partyId)
	  partyName = PartyHelper.getPartyName(delegator, partyId, false);
	 
	 
	 supplierName = "";
	 if(supplier)
	 supplierName = PartyHelper.getPartyName(delegator, supplier, false);
	 
	 //Debug.log("supplierName================="+supplierName);
	 
	 tempMap.put("supplierName", supplierName);
	 
	 tempMap.put("partyName", partyName);
	 
	 shipmentId = eachInvoiceList.shipmentId;
	 
	 if(shipmentId){
		 shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
		 
		 shipmentId = shipmentList.get("shipmentId");
		 
		 tempMap.put("shipmentId", shipmentId);
		 
		 //=================Shipment Reimbursement=============
		 
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		 expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 ShipmentReimbursement = delegator.findList("ShipmentReimbursement", expr, null, null, null, false);
		
		 double reimbursentAMT = 0;
		 if(ShipmentReimbursement){
			 
			 tempList = [];
			 for (eachReimbursement in ShipmentReimbursement) {
				 
				 tempMap1 = [:];
				 
				 reimbursentAMT = reimbursentAMT+Double.valueOf(eachReimbursement.receiptAmount);
				 
				 tempMap1.put("shipmentId", eachReimbursement.shipmentId)
				 if(eachReimbursement.claimId)
				 tempMap1.put("claimId", eachReimbursement.claimId)
				 else
				 tempMap1.put("claimId", "")
				 if(eachReimbursement.receiptNo)
				 tempMap1.put("receiptNo", eachReimbursement.receiptNo)
				 else
				 tempMap1.put("receiptNo", "")
				 if(eachReimbursement.receiptAmount)
				 tempMap1.put("receiptAmount", eachReimbursement.receiptAmount)
				 else
				 tempMap1.put("receiptAmount", "")
				 if(eachReimbursement.receiptDate)
				 tempMap1.put("receiptDate", eachReimbursement.receiptDate)
				 else
				 tempMap1.put("receiptDate", "")
				 
				 if(eachReimbursement.description)
				 tempMap1.put("description", eachReimbursement.description)
				 else
				 tempMap1.put("description", "")
				 
				 
				 tempList.add(tempMap1);
				 
			 }
			 
			 
			 shipmentReimbursementJson.put(shipmentId, tempList);
			 
			 
		 }
		 
		 
		 //===================================================
		 
		 
		 primaryOrderId = shipmentList.get("primaryOrderId");
		 
		 DstAddr = delegator.findOne("OrderAttribute",["orderId":primaryOrderId,"attrName":"DST_ADDR"],false);
		 if(DstAddr){
			 destAddr=DstAddr.get("attrValue");
			 tempMap.put("destAddr", destAddr);
		 }else{
			 tempMap.put("destAddr", "");
		 }
		 
		 
		 lrNumber = shipmentList.get("lrNumber");
		 
		 if(lrNumber)
		 tempMap.put("lrNumber", lrNumber);
		 else
		 tempMap.put("lrNumber", "");
		 
		 deliveryChallanDate = shipmentList.get("deliveryChallanDate");
		 
		 if(deliveryChallanDate)
		 tempMap.put("lrDate",UtilDateTime.toDateString(deliveryChallanDate,"dd/MM/yyyy") );
		 else
		 tempMap.put("lrDate", "");
		 
		 
		 double claimAmt = 0;
		 estimatedShipCost = shipmentList.get("estimatedShipCost");
					 
		 if(reimbursentAMT){
		 claimAmt = reimbursentAMT;
		 tempMap.put("claim", claimAmt);
		 } /*else{
		 tempMap.put("claim", reimbursentAMT);
		 }
		 */
		 if(maxAmt > reimbursentAMT)
		 tempMap.put("eligibleAMT", reimbursentAMT);
		 else
		 tempMap.put("eligibleAMT", maxAmt);
	 
		 estimatedShipDate = shipmentList.get("estimatedShipDate");
		 
		 if(estimatedShipDate)
		 tempMap.put("estimatedShipDate", UtilDateTime.toDateString(estimatedShipDate,"dd/MM/yyyy"));
		 else
		 tempMap.put("estimatedShipDate", "");
		 
		 carrierName = shipmentList.get("carrierName");
		 //Debug.log("carrierName================="+carrierName);
		 
		 if(carrierName)
		 tempMap.put("transporter", carrierName);
		 else
		 tempMap.put("transporter", "");
		 
		 supplierInvoiceId = ""
		 supplierInvoiceId = shipmentList.get("supplierInvoiceId");
		 
		 if(supplierInvoiceId)
		 tempMap.put("supplierInvoiceId", supplierInvoiceId);
		 else
		 tempMap.put("supplierInvoiceId", "");
		 
		 
		 supplierInvoiceDate = "";
		 supplierInvoiceDate = shipmentList.get("supplierInvoiceDate");
		 
		 if(supplierInvoiceDate)
		 tempMap.put("supplierInvoiceDate", UtilDateTime.toDateString(supplierInvoiceDate,"dd-MM-yyyy"));
		 else
		 tempMap.put("supplierInvoiceDate", "");
		 
		 //Debug.log("supplierInvoiceId================="+supplierInvoiceId);
		 
		 
		 }
	}
	 finalList.add(tempMap);
}

context.finalList = finalList;

//Debug.log("shipmentReimbursementJson================="+shipmentReimbursementJson);


context.shipmentReimbursementJson = shipmentReimbursementJson;


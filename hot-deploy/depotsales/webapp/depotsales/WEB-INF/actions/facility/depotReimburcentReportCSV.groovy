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



SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");
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

DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);

DateList.add(DateMap);
context.DateList=DateList;
branchId = parameters.branchId;

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
//Debug.log("productIds================"+productIds.size());
  
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

isDepotPartyIds = [];
condListb2 = [];
condListb2.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
if(branchBasedWeaversList)
condListb2.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN,branchBasedWeaversList));
fcond = EntityCondition.makeCondition(condListb2, EntityOperator.AND);
FacilityList = delegator.find("Facility", fcond, null, UtilMisc.toSet("ownerPartyId"), null, null);

branchBasedWeaversList = EntityUtil.getFieldListFromEntityListIterator(FacilityList, "ownerPartyId", true);


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
if(!partyId && branchBasedWeaversList)
condList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));

condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));


cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

////Debug.log("cond================="+cond);


fieldsToSelect = ["invoiceId","invoiceDate","shipmentId","partyIdFrom","referenceNumber"] as Set;

invoice = delegator.findList("InvoiceAndItem", cond, fieldsToSelect, null, null, false);

////////Debug.log("invoice========================="+invoice);


invoiceIds=EntityUtil.getFieldListFromEntityList(invoice, "invoiceId", true);



//Debug.log("invoiceIds================="+invoiceIds.size());

finalList = [];
for (eachInvoice in invoiceIds) {
	
	eachInvoiceList = delegator.findOne("Invoice",[invoiceId : eachInvoice] , false);
	
	
	tempMap=[:];
	
	tempMap.put("invoiceId", eachInvoiceList.invoiceId);
	
	
	billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachInvoiceList.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
	if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
		invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
		invoiceSequence = invoiceSeqDetails.invoiceSequence;
		
		//Debug.log("invoiceSequence======2121==========="+invoiceSequence);
		
		tempMap.put("billno", invoiceSequence);
	}else{
		tempMap.put("billno", eachInvoiceList.invoiceId);
	}
	
	 tempMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoiceList.invoiceDate,"dd/MM/yyyy"));
		   
	 ////Debug.log("eachInvoiceList.invoiceId================="+eachInvoiceList.invoiceId);
	 
	 
   
	condList.clear();
	
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	//condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	//condList.add(EntityCondition.makeCondition("isAssessableValue", EntityOperator.EQUALS, "Y"));
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	  
	
	//Debug.log("InvoiceItem================="+InvoiceItem.size());
	
	if(InvoiceItem){
	double invoiceAMT = 0;
	double invoiceQTY = 0;
	for (eachInvoiceItem in InvoiceItem) {
		
		invoiceAMT = invoiceAMT+(eachInvoiceItem.amount*eachInvoiceItem.quantity);
		invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
		
	}
	
	tempMap.put("invoiceAmount", invoiceAMT);
	
	double eligibleAMT = 0;
	
	eligibleAMT = (invoiceAMT*2)/100;
	
	tempMap.put("invoiceQTY", invoiceQTY);
	
	
	////Debug.log("invoiceAMT================="+invoiceAMT);
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	//conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
	condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
   
	////Debug.log("OrderItemBilling======================"+OrderItemBilling);
		   
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
					 
		 if(estimatedShipCost){
		 claimAmt = Double.valueOf(estimatedShipCost);
		 tempMap.put("claim", claimAmt);
		 } else{
		 tempMap.put("claim", "");
		 }
		 
		 if(claimAmt > eligibleAMT)
		 tempMap.put("eligibleAMT", eligibleAMT);
		 else
		 tempMap.put("eligibleAMT", claimAmt);
	 
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
		 
		 }
	}
	 finalList.add(tempMap);
}

context.finalList = finalList;
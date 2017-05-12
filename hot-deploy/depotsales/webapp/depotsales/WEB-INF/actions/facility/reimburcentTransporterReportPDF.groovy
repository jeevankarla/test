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
 
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.contact.ContactMechWorker;
 
 
 
//SimpleDateFormat sdf = new SimpleDateFormat("yyyy, MMM dd");
//dayend = null;
//daystart = null;
 
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
 
conditionList = [];
if(branchId){
conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
PartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyIdTo"), null, null, false);
branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
if(!branchList)
	branchList.add(branchId);
}
branchBasedWeaversList = [];
conditionList.clear()
if(branchId){
	conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
	conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	PartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyIdTo"), null, null, false);
	branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
}
productIds = [];
productCategoryIds = [];
conditionList.clear()
if(productCategory == "ALL"){
	productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition("productCategoryTypeId" ,EntityOperator.EQUALS,"NATURAL_FIBERS"), null, null, null ,false);		
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
	productPrimaryCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.IN,productCategoryIds), null, null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productPrimaryCategories, "productCategoryId", true);
}else if(productCategory == "OTHER"){
	productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition([EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "NATURAL_FIBERS"), EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_IN, UtilMisc.toList("COTTON","SILK"))], EntityOperator.AND), UtilMisc.toSet("productCategoryId"), null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
	
	productPrimaryCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.IN,productCategoryIds), null, null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productPrimaryCategories, "productCategoryId", true);
}else{
	productCategoris = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId" ,EntityOperator.EQUALS,productCategory), UtilMisc.toSet("productCategoryId","primaryParentCategoryId"), null, null ,false);
	productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoris, "productCategoryId", true);
}
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
	conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	ProductCategoryMember = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productId"), null, null, false);
	productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
	daystart = null;
	dayend = null;
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		if (UtilValidate.isNotEmpty(parameters.partyfromDate)) {
			fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime()));
			thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime()));
		}
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + e, "");
	context.errorMessage = "Cannot parse date string: " + e;
		return;
	}
	
daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);
context.daystart = daystart;
context.dayend = dayend;
reimbursmentPercentage = [:];
reimbursmentPercentage.put("SILK", 1);
reimbursmentPercentage.put("JUTE_YARN", 10);
branchpartyIdsList = [];
if(state && !partyId){
	conditionList.clear()
	if(branchBasedWeaversList)
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
	conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	if(state == "IN-TS")
	conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, "IN-TNG"));
	else if(state)
	conditionList.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, state));
	PartyContactDetailByPurposeList = delegator.find("PartyContactDetailByPurpose", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, UtilMisc.toSet("partyId"), null, null);
	branchBasedWeaversList = EntityUtil.getFieldListFromEntityListIterator(PartyContactDetailByPurposeList, "partyId", true);
	if(UtilValidate.isEmpty(branchBasedWeaversList)){
	 return "Selected Worng State";
	}
}
conditionList.clear()
if(UtilValidate.isNotEmpty(daystart)){
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}
conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
if(productIds)
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
if(partyId){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
}else if(!partyId && branchBasedWeaversList){
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
}
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "YARN_SALE"));
fieldsToSelect = ["invoiceId"] as Set;
invoice = delegator.find("InvoiceAndItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, fieldsToSelect, null, null);
invoiceIds=EntityUtil.getFieldListFromEntityListIterator(invoice, "invoiceId", true);
 
conditionList.clear()
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
orderIds=EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);
 
conditionList.clear()
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "SCHEME_CAT"));
conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.IN, ["MGPS_10Pecent","MGPS"]));
orderAttr = delegator.findList("OrderAttribute",EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
orderIds=EntityUtil.getFieldListFromEntityList(orderAttr, "orderId", true);
OrderItemBillingFilter = EntityUtil.filterByCondition(OrderItemBilling, EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
invoiceIds=EntityUtil.getFieldListFromEntityList(OrderItemBillingFilter, "invoiceId", true);
conditionList.clear()
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
Invoice = delegator.findList("Invoice", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
partyIds=EntityUtil.getFieldListFromEntityList(Invoice, "partyId", true);
partyPassMap= [:];
finalMap = [:];
partyWiseTotalsMap = [:];
totalsMap=[:];
totalSalQty=0;
totalSalAmt=0; 
totalClaimAmt=0
totalEligAmt=0;
for (eachPartyId in partyIds) {
	invoiceList = EntityUtil.filterByCondition(Invoice, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, eachPartyId));
	finalList = [];
	double TotalQuantiy = 0;
	double TotalAmount = 0;
	double totClaimAmt=0;
	double totElgibleAmt=0;
	totalTempMap = [:];
	for (eachInvoiceList in invoiceList) {
	tempMap=[:];
	tempMap.put("invoiceId", eachInvoiceList.invoiceId);
	billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachInvoiceList.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
	if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
		invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
		invoiceSequence = invoiceSeqDetails.invoiceSequence;
		tempMap.put("billno", invoiceSequence);
	}else{
		tempMap.put("billno", eachInvoiceList.invoiceId);
	}
	tempMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoiceList.invoiceDate,"dd/MM/yyyy"));
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE")));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,null));
	InvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	if(InvoiceItem){
		passNo = "";
		conditionList.clear()
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
		PartyIdentificationList = delegator.findList("PartyIdentification", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		if(PartyIdentificationList){
			passNo = PartyIdentificationList[0].get("idValue");
		}
		if(passNo)
			partyPassMap.put(partyId, passNo);
		productId = InvoiceItem[0].productId;
		double invoiceAMT = 0;
		double invoiceQTY = 0;
		for (eachInvoiceItem in InvoiceItem) {
			if(eachInvoiceItem.itemValue!=null){
				invoiceAMT = invoiceAMT+(eachInvoiceItem.itemValue);
				TotalAmount = TotalAmount + (eachInvoiceItem.itemValue);
			}
			invoiceQTY = invoiceQTY+(eachInvoiceItem.quantity);
			TotalQuantiy = TotalQuantiy + (eachInvoiceItem.quantity);
			
		}
		tempMap.put("invoiceAmount", invoiceAMT);
		totalSalAmt=totalSalAmt+invoiceAMT;
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		conditionList.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_EQUAL,null));
		ProductCategoryMember = delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("productCategoryId"), null, null, false);
		productCategoryId = ProductCategoryMember[0].productCategoryId;
		ProductCategory = delegator.findOne("ProductCategory",[productCategoryId : productCategoryId] , false);
		primaryParentCategoryId = ProductCategory.primaryParentCategoryId;
		percentage = reimbursmentPercentage.get(primaryParentCategoryId);
		if(!percentage)
			percentage = 2.5;
		double eligibleAMT = 0;
		maxAmt = (invoiceAMT*percentage)/100;
		tempMap.put("invoiceQTY", invoiceQTY);
		totalSalQty=totalSalQty+invoiceQTY
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		itemOrderId  = OrderItemBilling[0].orderId;
		conditionList.clear()
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, itemOrderId));
		conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
		OrderRoleList = delegator.findList("OrderRole", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
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
		 tempMap.put("supplierName", supplierName);
		 tempMap.put("partyName", partyName);
		 shipmentId = eachInvoiceList.shipmentId;
		 if(shipmentId){
			 shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
		 ShipmentReimbursement = delegator.findList("ShipmentReimbursement", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		 double reimbursentAMT = 0;
		 if(ShipmentReimbursement){
			 tempList = [];
			 for (eachReimbursement in ShipmentReimbursement) {
				 tempMap1 = [:];
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
		 }
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
		 double estimatedShipCost=0;
		 if(shipmentList.get("estimatedShipCost")){
		 estimatedShipCost = shipmentList.get("estimatedShipCost");
		 reimbursentAMT = shipmentList.get("estimatedShipCost");
		 }
		 if(reimbursentAMT){
		 claimAmt = Double.valueOf(reimbursentAMT);
		 totClaimAmt=totClaimAmt+claimAmt;
		 tempMap.put("claim", claimAmt);
		 totalClaimAmt=totalClaimAmt+claimAmt
		 } else{
		 tempMap.put("claim", "");
		 }
		 if(maxAmt > reimbursentAMT){
			 totElgibleAmt=totElgibleAmt+reimbursentAMT;
			 tempMap.put("eligibleAMT", reimbursentAMT);
			 totalEligAmt=totalEligAmt+reimbursentAMT;
		 } else{
			 totElgibleAmt=totElgibleAmt+maxAmt;
			 tempMap.put("eligibleAMT", maxAmt);
			 totalEligAmt=totalEligAmt+maxAmt;
		 }
		 estimatedShipDate = shipmentList.get("estimatedShipDate");
		 if(estimatedShipDate)
		 tempMap.put("estimatedShipDate", UtilDateTime.toDateString(estimatedShipDate,"dd/MM/yyyy"));
		 else
		 tempMap.put("estimatedShipDate", "");
		 carrierName = shipmentList.get("carrierName");
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
		 if(partyId){
		 finalAddresList=[];
		 address1="";
		 address2="";
		 city="";
		 postalCode="";
		 panId="";
		 tanId="";
		 partyPostalAddress = dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
		 if(partyPostalAddress[0]){
			if(partyPostalAddress.address1){
				address1=partyPostalAddress.address1;
			tempMapAdd=[:];
			tempMapAdd.put("key1","Road / Street / Lane");
			tempMapAdd.put("key2",address1);
			finalAddresList.add(tempMapAdd);
			if(partyPostalAddress.address2){
				address2=partyPostalAddress.address2;
			}
			tempMapAdd=[:];
			tempMapAdd.put("key1","Area / Locality");
			tempMapAdd.put("key2",address2);
			finalAddresList.add(tempMapAdd);
			if(partyPostalAddress.city){
				city=partyPostalAddress.city;
			}
			tempMapAdd=[:];
			tempMapAdd.put("key1","Town / District / City");
			tempMapAdd.put("key2",city);
			finalAddresList.add(tempMapAdd);
			 
			if(partyPostalAddress.postalCode){
				postalCode=partyPostalAddress.postalCode;
			}
			tempMapAdd=[:];
			tempMapAdd.put("key1","PIN Code");
			tempMapAdd.put("key2",postalCode);
			finalAddresList.add(tempMapAdd);
			 
			 }else{
				 contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
				 partyPostalAddress = contactMench.postalAddress;
				  
				 if(partyPostalAddress[0]){
					 address1=partyPostalAddress[0].address1;
				  
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","Road / Street / Lane");
				 tempMapAdd1.put("key2",address1);
				 finalAddresList.add(tempMapAdd1);
				 if(partyPostalAddress[0].address2){
					 address2=partyPostalAddress[0].address2;
				 }
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","Area / Locality");
				 tempMapAdd1.put("key2",address2);
				 finalAddresList.add(tempMapAdd1);
				 if(partyPostalAddress[0].city){
					 city=partyPostalAddress[0].city;
				 }
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","Town / District / City");
				 tempMapAdd1.put("key2",city);
				 finalAddresList.add(tempMapAdd1);
				  
				 if(partyPostalAddress[0].postalCode){
					 postalCode=partyPostalAddress[0].postalCode;
				 }
				 tempMapAdd1=[:];
				 tempMapAdd1.put("key1","PIN Code");
				 tempMapAdd1.put("key2",postalCode);
				 finalAddresList.add(tempMapAdd1);
				 }
			 }
			 
		 }else{
				 contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
				 partyPostalAddress = contactMench.postalAddress;
				 if(partyPostalAddress[0]){
					 address1=partyPostalAddress[0].address1;
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","Road / Street / Lane");
				 tempMapAdd2.put("key2",address1);
				 finalAddresList.add(tempMapAdd2);
				 if(partyPostalAddress[0].address2){
					 address2=partyPostalAddress[0].address2;
				 }
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","Area / Locality");
				 tempMapAdd2.put("key2",address2);
				 finalAddresList.add(tempMapAdd2);
				 if(partyPostalAddress[0].city){
					 city=partyPostalAddress[0].city;
				 }
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","Town / District / City");
				 tempMapAdd2.put("key2",city);
				 finalAddresList.add(tempMapAdd2);
				  
				 if(partyPostalAddress[0].postalCode){
					 postalCode=partyPostalAddress[0].postalCode;
				 }
				 tempMapAdd2=[:];
				 tempMapAdd2.put("key1","PIN Code");
				 tempMapAdd2.put("key2",postalCode);
				 finalAddresList.add(tempMapAdd2);
				 }
		 }
		 if(finalAddresList)
		 tempMap.put("finalAddresList", finalAddresList);
		 else
		 tempMap.put("finalAddresList", "");
		  
		 }
		 }
	}
  finalList.add(tempMap);
}
	totalTempMap.put("TotalQuantiy", TotalQuantiy);
	totalTempMap.put("TotalAmount", TotalAmount);
	totalTempMap.put("totClaimAmt", totClaimAmt);
	totalTempMap.put("totElgibleAmt", totElgibleAmt);
	if(finalList){
		finalMap.put(eachPartyId, finalList);
		partyWiseTotalsMap.put(eachPartyId, totalTempMap);
	}
}
totalsMap.put("invoiceQTY", totalSalQty);
totalsMap.put("invoiceAmount", totalSalAmt);
totalsMap.put("claim", totalClaimAmt);
totalsMap.put("eligibleAMT", totalEligAmt);
context.totalsMap=totalsMap;
context.finalMap = finalMap;
context.partyWiseTotalsMap = partyWiseTotalsMap;
 





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
DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);

DateList.add(DateMap);
context.DateList=DateList;
branchId = parameters.branchId;
branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
branchName = branch.get("groupName");
DateMap.put("branchName", branchName);

branchList = [];

condListb = [];

condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);

branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

if(!branchList)
branchList.add(branchId);



//Debug.log("branchList=================="+branchList);

  

if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
  
	try {
		//daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		
		 } catch (ParseException e) {
			 //Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			 }
   
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   
   try {
	 //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
	   
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
   } catch (ParseException e) {
	   //Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
}
  
daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);

//Debug.log("daystart==================="+daystart);

//Debug.log("dayend==================="+dayend);


condList = [];
//condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, "29375"));

if(UtilValidate.isNotEmpty(daystart)){
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}

condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
//condList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "DEPOT_YARN_SALE"));


cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

fieldsToSelect = ["invoiceId","invoiceDate","shipmentId","partyIdFrom","referenceNumber"] as Set;

invoice = delegator.findList("Invoice", cond, fieldsToSelect, null, null, false);

//Debug.log("invoice========================="+invoice);


if(UtilValidate.isEmpty(invoice)){
	return "No Invoices Found";
 }


invoiceIds=EntityUtil.getFieldListFromEntityList(invoice, "invoiceId", true);

//Debug.log("invoiceIds========================="+invoiceIds.size());

condList.clear();

if(UtilValidate.isNotEmpty(invoiceIds)){
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
}

billingcond = EntityCondition.makeCondition(condList, EntityOperator.AND);

fieldsToBilling = ["invoiceId","orderId","orderItemSeqId","invoiceItemSeqId"] as Set;

OrderItemBilling = delegator.findList("OrderItemBilling", billingcond, fieldsToBilling, null, null, false);

orderIdsFromBilling = EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);

//Debug.log("OrderItemBilling========================="+OrderItemBilling.size());


//Debug.log("orderIdsFromBilling========================="+orderIdsFromBilling.size());

actualInvoiceIds = EntityUtil.getFieldListFromEntityList(OrderItemBilling, "invoiceId", true);

//Debug.log("actualInvoiceIds========================="+actualInvoiceIds.size());


OrderItemDetail = delegator.findList("OrderItemDetail", EntityCondition.makeCondition("orderId", EntityOperator.IN,orderIdsFromBilling), null, null, null, false);

condList.clear();

condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, actualInvoiceIds));
condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, null));
condList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);

InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);

//Debug.log("InvoiceItem=============1212============"+InvoiceItem.size());


orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.IN , orderIdsFromBilling)  , null, null, null, false );


OrderHeader = delegator.findList("OrderHeader",EntityCondition.makeCondition("orderId", EntityOperator.IN , orderIdsFromBilling)  , null, null, null, false );


//Debug.log("OrderHeader=============1212============"+OrderHeader.size());


salesAndPurchaseList = [];

if(invoice){

for (eachInvoice in invoice) {
	
	
	invoiceList = delegator.findOne("Invoice",[invoiceId : eachInvoice.invoiceId] , false);
	
	purposeTypeId = invoiceList.purposeTypeId;
	
	if(purposeTypeId == "YARN_SALE"){
	
		
		//Debug.log("purposeTypeId============="+purposeTypeId);
	
	tallyRefNo = "";
	if(eachInvoice.referenceNumber)
	tallyRefNo = eachInvoice.referenceNumber;
	
	
	invoiceItemList = EntityUtil.filterByCondition(InvoiceItem, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
	
	
	PartyGroup = delegator.findOne("PartyGroup",[partyId : eachInvoice.partyIdFrom] , false);
	
	
	//Debug.log("eachInvoice.invoiceId============"+eachInvoice.invoiceId);
	
	
	groupName = "";
	if(PartyGroup)
	  groupName = PartyGroup.groupName;
	  
	  
	  //Debug.log("groupName============"+groupName);
	  
	
	shipmentList = delegator.findOne("Shipment",[shipmentId : eachInvoice.shipmentId] , false);
	
	//Debug.log("shipmentList============"+shipmentList);
	
	
	primaryOrderId = shipmentList.get("primaryOrderId");
	
	//Debug.log("primaryOrderId============"+primaryOrderId);
	exprCondList=[];
	exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
	exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
	OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
	
	actualOrderId = "";
	if(OrderAss){
		
		actualOrderId=OrderAss.toOrderId;
		
	}
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
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
		/*if(eachRole.roleTypeId == "ON_BEHALF_OF")
			 onbehalf = true;*/
	}
	
	partyName = "";
	if(partyId)
	 partyName = PartyHelper.getPartyName(delegator, partyId, false);

	 supplierName = "";
	 if(supplier)
	 supplierName = PartyHelper.getPartyName(delegator, supplier, false);
	
	 //Debug.log("partyId==================="+partyId);
	 
	 
	 //Debug.log("supplier==================="+supplier);
	
	//=================Addresss======================
	
	contactMechesDetails = [];
	conditionListAddress = [];
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
	conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	 List<String> orderBy = UtilMisc.toList("-contactMechId");
	contactMech = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy, null, false);
	
	
	
	if(contactMech){
	contactMechesDetails = contactMech;
	}
	else{
		conditionListAddress.clear();
		conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
		List<String> orderBy2 = UtilMisc.toList("-contactMechId");
		contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy2, null, false);
	}
	
	shipingAdd = [:];
	
	if(contactMechesDetails){
		contactMec=contactMechesDetails.getFirst();
		if(contactMec){
			//partyPostalAddress=contactMec.get("postalAddress");
			
			partyPostalAddress=contactMec;
			
			//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
		//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
			if(partyPostalAddress){
				address1="";
				address2="";
				state="";
				city="";
				postalCode="";
				if(partyPostalAddress.get("address1")){
				address1=partyPostalAddress.get("address1");
				//Debug.log("address1=========================="+address1);
				}
				if(partyPostalAddress.get("address2")){
					address2=partyPostalAddress.get("address2");
					}
				if(partyPostalAddress.get("city")){
					city=partyPostalAddress.get("city");
					}
				if(partyPostalAddress.get("stateGeoName")){
					state=partyPostalAddress.get("stateGeoName");
					}
				if(partyPostalAddress.get("postalCode")){
					postalCode=partyPostalAddress.get("postalCode");
					}
				//shipingAdd.put("name",shippPartyName);
				shipingAdd.put("address1",address1);
				shipingAdd.put("address2",address2);
				shipingAdd.put("city",city);
				shipingAdd.put("state",state);
				shipingAdd.put("postalCode",postalCode);
				
			}
		}
	}
	
	
	//===============address for supplier====================
	
	conditionListAddress.clear();
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplier));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);

shipingAddForSupplier = [:];

if(contactMechesDetails){
	contactMec=contactMechesDetails.getFirst();
	if(contactMec){
		//partyPostalAddress=contactMec.get("postalAddress");
		
		partyPostalAddress=contactMec;
		
		//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
	//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
		if(partyPostalAddress){
			address1="";
			address2="";
			state="";
			city="";
			postalCode="";
			if(partyPostalAddress.get("address1")){
			address1=partyPostalAddress.get("address1");
			//Debug.log("address1=========================="+address1);
			}
			if(partyPostalAddress.get("address2")){
				address2=partyPostalAddress.get("address2");
				}
			if(partyPostalAddress.get("city")){
				city=partyPostalAddress.get("city");
				}
			if(partyPostalAddress.get("stateGeoName")){
				state=partyPostalAddress.get("stateGeoName");
				}
			if(partyPostalAddress.get("postalCode")){
				postalCode=partyPostalAddress.get("postalCode");
				}
			//shipingAdd.put("name",shippPartyName);
			shipingAddForSupplier.put("address1",address1);
			shipingAddForSupplier.put("address2",address2);
			shipingAddForSupplier.put("city",city);
			shipingAddForSupplier.put("state",state);
			shipingAddForSupplier.put("postalCode",postalCode);
			
		}
	}
}

	
	
	//partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
	
	//Debug.log("partyPostalAddress===================="+partyPostalAddress);
	
	//Debug.log("shipingAdd========================="+shipingAdd);
	//================================================
	
	//============IS Depo
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	FacilityList = delegator.findList("Facility", fcond, null, null, null, false);
	
	
	isDepot = "";
	if(FacilityList)
	isDepot ="DEPOT";
	else
	isDepot ="NON DEPOT";
	
  //-------------------------------
	
   //============Scheme============
	
	orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
	
	scheme = "";
	if(UtilValidate.isNotEmpty(orderAttr)){
		orderAttr.each{ eachAttr ->
			if(eachAttr.attrName == "SCHEME_CAT"){
				scheme =  eachAttr.attrValue;
			}
			
		}
	   }
	
	
	//==========================Sequence=======================
	
	orderHeaderSequencesfilter = EntityUtil.filterByCondition(orderHeaderSequences, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	
	
	
	
	//Debug.log("orderHeaderSequencesfilter===3223============="+orderHeaderSequencesfilter);
	
	orderNo ="NA";
	if(UtilValidate.isNotEmpty(orderHeaderSequencesfilter)){
		orderSeqDetails = EntityUtil.getFirst(orderHeaderSequencesfilter);
		orderNo = orderSeqDetails.orderNo;
	}
	
	//=============indentDate============
	//Debug.log("orderNo================"+orderNo);
	
	
	orderDate = "";
	TallyPoNumber = "";
	if(actualOrderId){
	OrderHeader = delegator.findOne("OrderHeader",[orderId : actualOrderId] , false);
	orderDate = OrderHeader.orderDate;
	TallyPoNumber = OrderHeader.tallyRefNo;
	}
	
	//Debug.log("TallyPoNumber================"+TallyPoNumber);
	
	//=============================advance Payment=============================================
	
	conditonList = [];
	conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, actualOrderId));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
	double paidAmt = 0;
	
	paymentIdsOfIndentPayment = [];
	
	if(OrderPaymentPreference){
	
	orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
 
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN,orderPreferenceIds));
	conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
	
	paymentIdsOfIndentPayment = EntityUtil.getFieldListFromEntityList(PaymentList,"paymentId", true);
	
	
	for (eachPayment in PaymentList) {
		paidAmt = paidAmt+eachPayment.get("amount");
	}
	
  }
	
	
	//Debug.log("paidAmt================"+paidAmt);
	
	
	double appliedAmt = 0;
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,actualOrderId));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	OrderItemBillingList = delegator.findList("OrderItemBilling", cond, null, null, null ,false);
	
	invoiceIds = EntityUtil.getFieldListFromEntityList(OrderItemBillingList,"invoiceId", true);
	
	if(invoiceIds){
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("invoiceId" ,EntityOperator.IN,invoiceIds));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	PaymentApplicationList = delegator.findList("PaymentApplication", cond, null, null, null ,false);
	
		for (eachList in PaymentApplicationList) {
			 if(!paymentIdsOfIndentPayment.contains(eachList.paymentId))
				appliedAmt = appliedAmt+eachList.amountApplied;
		}
	}
	
	
//==============================All Taxes==================================================	
	
	
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderAdjustment = delegator.findList("OrderAdjustment", condExpr, null, null, null, false);

	if(OrderAdjustment){
	for (eachAdjment in OrderAdjustment) {
		
		if(eachAdjment.orderAdjustmentTypeId != "TEN_PERCENT_SUBSIDY"){
			altaxAmt = altaxAmt +Double.valueOf( eachAdjment.amount);
		}
	}
	}*/
	
//==============================ten Per==================================	
	
	
	//Debug.log("eachInvoice.invoiceId===================="+eachInvoice.invoiceId);
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_FPROD_ITEM"));
	
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceItemAdjustment = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	
	double tenPerAmt = 0;
	for (eachAdjment in InvoiceItemAdjustment) {
		
		if(eachAdjment.invoiceItemTypeId == "TEN_PERCENT_SUBSIDY"){
			//tenPerAmt = tenPerAmt +Double.valueOf( eachAdjment.amount);
			
			//Debug.log("eachAdjment.itemValue===================="+eachAdjment.itemValue);
			
			tenPerAmt = tenPerAmt +Double.valueOf(eachAdjment.itemValue);
		}
	}
	
	
	
	//Debug.log("tenPerAmt===================="+tenPerAmt);
//=============================================	
	
//=====================for Adjustments=================================================
	
	
	
	double allAdjWitOutTEN = 0;
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, ["INV_FPROD_ITEM","TEN_PERCENT_SUBSIDY","VAT_PUR", "CST_PUR","CST_SALE","VAT_SALE","CESS_SALE","CESS_PUR","VAT_SURHARGE","TEN_PER_CHARGES","TEN_PER_DISCOUNT"]));
	
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceRemainItemAdjustment = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);

	if(InvoiceRemainItemAdjustment){
		for (eachAdjustment in InvoiceRemainItemAdjustment) {
			
			//Debug.log("eachAdjustment.itemValue================"+eachAdjustment.itemValue);
			
			
			allAdjWitOutTEN = allAdjWitOutTEN+eachAdjustment.itemValue;
		}
	}
	
	
	double invoAmt = 0;
	
	dontRepeat = [];
	
	double grandTotal = 0;
	for (eachItem in invoiceItemList) {
		
		
		
		  dontRepeat.add(eachItem.invoiceId);
		
		
		   double invoiceNetAmt = 0;
		
		   tempMap = [:];
		   
		   billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachItem.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
		   if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
			   invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
			   invoiceSequence = invoiceSeqDetails.invoiceSequence;
			   tempMap.put("invoiceId", invoiceSequence);
		   }else{
			   tempMap.put("invoiceId", eachItem.invoiceId);
		   }
		
		   tempMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoice.invoiceDate,"dd-MM-yyyy"));
		   
		 //  invoAmt = invoAmt+(eachItem.amount*eachItem.quantity);
		   
		  // tempMap.put("invoiceAmount", (eachItem.amount*eachItem.quantity));
		   
		   tempMap.put("invoiceAmount", eachItem.itemValue);
		   
		  // invoiceNetAmt = invoiceNetAmt+Double.valueOf((eachItem.amount*eachItem.quantity));
		   
		   invoiceNetAmt = invoiceNetAmt+eachItem.itemValue;
		   
		   if(dontRepeat.size() == 1)
		   invoiceNetAmt = invoiceNetAmt+allAdjWitOutTEN;
		   
		  //Debug.log("eachItem.invoiceId================="+eachItem.invoiceId);
		   //Debug.log("eachItem.invoiceItemSeqId================="+eachItem.invoiceItemSeqId);
		   
		   
		   conditionList.clear();
		   conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
		   conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
		  
		   //Debug.log("OrderItemBilling======================"+OrderItemBilling);
		   
		 		 
		 itemOrderId  = OrderItemBilling[0].orderId;
		 orderItemSeqId  = OrderItemBilling[0].orderItemSeqId;
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,itemOrderId));
		 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
	
		 //Debug.log("OrderItemDetail======================"+OrderItemDetail);
		 
	
		 quantity = eachItem.quantity;
		 amount = eachItem.amount;
		
		 double baleQty = 0; 
		 double unit = 0;
		 double quotaQuantity = 0;
	  
		 for (eachOrderItemDetail in OrderItemDetail) {
		
			 if(eachOrderItemDetail.baleQuantity)
			 baleQty = baleQty+Double.valueOf(eachOrderItemDetail.baleQuantity);
			 
			 
			 if(eachOrderItemDetail.unitPrice)
			 unit = unit+Double.valueOf(eachOrderItemDetail.unitPrice);
			 
			 
			 if(eachOrderItemDetail.quotaQuantity)
			 quotaQuantity = quotaQuantity+Double.valueOf(eachOrderItemDetail.quotaQuantity);
			 
		}
		 
		 //Debug.log("unit======================"+unit);
		 
		 tempMap.put("unit", unit);
		 
		 
		    if(baleQty)
			tempMap.put("baleQty",baleQty);
			else
			tempMap.put("baleQty","");
			
			
		   tempMap.put("quantity", eachItem.quantity);
		
		//String schemeAmt = (String)SchemeQtyMap.get(eachInvoiceList.invoiceItemSeqId);
			
			
			//==============scheme Quantity===============
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
			conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachItem.invoiceItemSeqId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			invoiceInnerAdjItemList = EntityUtil.filterByCondition(InvoiceItemAdjustment, cond);
	   
			
			
			
			double schemeQQQty = 0;
			double schemeAMMMt = 0;
			
			
			if(invoiceInnerAdjItemList){
				
				//Debug.log("invoiceInnerAdjItemList============="+invoiceInnerAdjItemList);
				schemeAMMMt = Math.round(invoiceInnerAdjItemList[0].amount);
				
				 invoiceIdAdj = invoiceInnerAdjItemList[0].invoiceId; 
				 invoiceItemSeqIdAdj = invoiceInnerAdjItemList[0].invoiceItemSeqId;
				 
				 
				 conditionList.clear();
				 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceIdAdj));
				 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdAdj));
				 conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
				 cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				 OrderAdjustmentAndBilling = delegator.findList("OrderAdjustmentAndBilling", cond1, null, null, null, false);
	 
				 if(OrderAdjustmentAndBilling[0]){
				 schemeQQQty = OrderAdjustmentAndBilling[0].quantity;
				 //schemeAMMMt = Math.round(OrderAdjustmentAndBilling[0].amount);
				
				 }
			}
			

			//Debug.log("schemeQQQty======================"+schemeQQQty);
			
			//Debug.log("schemeAMMMt======================"+schemeAMMMt);
			
			
						    
			double tenPerQty = 0;
			tenPerQty = schemeQQQty;
			
			tempMap.put("schemeQty", schemeQQQty);
			
			
			//=============================Tax Amount======================
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
			conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachItem.invoiceItemSeqId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE")));
			
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			invoiceVatCstList = EntityUtil.filterByCondition(InvoiceItemAdjustment, cond);
	   
			double taxAmt = 0;
			if(invoiceVatCstList){
				 for (eachAdj in invoiceVatCstList) {
					 taxAmt = taxAmt+eachAdj.itemValue;
				}
			}
			
			//Debug.log("taxAmt======================"+taxAmt);
			//Debug.log("allAdjWitOutTEN======================"+allAdjWitOutTEN);
			
			
			tempMap.put("altaxAmt", taxAmt);
			
			if(dontRepeat.size() == 1)
			tempMap.put("allAdjWitOutTEN", allAdjWitOutTEN);
			else
			tempMap.put("allAdjWitOutTEN", "");
			
			
			invoiceNetAmt = invoiceNetAmt+taxAmt;
			
			//=====================================================================
			
			/*if(quantity > quotaQuantity)
			{
			  tempMap.put("schemeQty", quotaQuantity);
			  tenPerQty = quotaQuantity;
			}
			else
			{
			  tempMap.put("schemeQty", quantity);
			  tenPerQty = quantity;
			}*/
		
		/*if(UtilValidate.isNotEmpty(schemeAmt))
		  tempMap.put("schemeQty", Double.valueOf(schemeAmt));
		else
		  tempMap.put("schemeQty", 0);
*/		  
		  
		  if(scheme == "General")
		  tempMap.put("mgpsQty", 0);
		  else
		  tempMap.put("mgpsQty", quantity-tenPerQty);
			
		  //Debug.log("tenPerQty============4343========"+tenPerQty);
		  
		 double serviceAmt = 0;
		 double sourcePercentage = 0;
		 
		  if(scheme == "General"){
			  
			  conditionList.clear();
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachItem.invoiceItemSeqId));
			  conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
			  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			  invoiceInnerAdjItemList = EntityUtil.filterByCondition(InvoiceItemAdjustment, cond);
			  
			 // //Debug.log("invoiceInnerAdjItemList===================="+invoiceInnerAdjItemList);
			  
			 // //Debug.log("invoiceInnerAdjItemList===================="+invoiceInnerAdjItemList[0].itemValue);
			  
			  
			  if(invoiceInnerAdjItemList[0]){
			  serviceAmt = serviceAmt+invoiceInnerAdjItemList[0].itemValue;
			  //sourcePercentage = sourcePercentage+invoiceInnerAdjItemList[0].sourcePercentage;
			  
			  }
		  }
		  
		  
		  //Debug.log("serviceAmt===================="+serviceAmt);
		  
		  if(scheme == "General"){
			  
			  sourcePercentage = (serviceAmt/(eachItem.itemValue))*100;
			  double perAmt = (eachItem.amount*sourcePercentage)/100;
			  
			  tempMap.put("amount",(eachItem.amount+perAmt));
			  }else{
			  tempMap.put("amount", eachItem.amount);
		   }
			  
		  
		  tempMap.put("ToTamount", eachItem.itemValue+serviceAmt);
		  grandTotal = grandTotal+eachItem.itemValue+serviceAmt;
		  
		   
		   
			tempMap.put("userAgency", partyName);
			
			tempMap.put("cluster", "");
			
			tempMap.put("subsidyAmt", schemeAMMMt);
			
			invoiceNetAmt = invoiceNetAmt+schemeAMMMt;
			
			tempMap.put("District", shipingAdd.get("city"));
			
			tempMap.put("branch", groupName);
			
			tempMap.put("sate", shipingAdd.get("state"));
			
			tempMap.put("AgencyType", "");
			
			tempMap.put("Depo", isDepot);
			
			if(scheme == "MGPS_10Pecent")
			tempMap.put("scheme", "MGPS + 10Pecent");
			else
			tempMap.put("scheme", scheme);
			
			if(orderNo != "NA")
			tempMap.put("orderNo", orderNo);
			else
			tempMap.put("orderNo", actualOrderId);
			
			if(orderDate)
			tempMap.put("indentDate",UtilDateTime.toDateString(orderDate,"dd-MM-yyyy"));
			else
			tempMap.put("indentDate","");
			
			
			tempMap.put("custIndDate", "");
		    
			tempMap.put("advance", paidAmt);
			
			tempMap.put("appliedAmt", appliedAmt);
			
			tempMap.put("balance", paidAmt-appliedAmt);
			
			tempMap.put("advance", paidAmt);
			
			tempMap.put("cheque/dd", "");
			
			tempMap.put("tallyBillAmt", "");
			
			tempMap.put("supplierName", supplierName);
			
			tempMap.put("millState", shipingAddForSupplier.get("state"));
			
			tempMap.put("source", "");
			
			tempMap.put("millCategory", "");
			
			
			//================================purchase Invoice Details=====================
			
			//Debug.log("itemOrderId============="+itemOrderId);
			//Debug.log("orderItemSeqId============="+orderItemSeqId);
			

			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, itemOrderId));
			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItemAssoc = delegator.findList("OrderItemAssoc", cond, null, null, null, false);

			
			poOrderId = "";
			poOrderItemSeqId = "";
			if(OrderItemAssoc){
			poOrderId  = OrderItemAssoc[0].toOrderId;
			poOrderItemSeqId  = OrderItemAssoc[0].toOrderItemSeqId;
			}
			
			//Debug.log("poOrderId============="+poOrderId);
			//Debug.log("poOrderItemSeqId============="+poOrderItemSeqId);
			
			//Debug.log("eachInvoice.shipmentId============="+eachInvoice.shipmentId);
			
			
            conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, eachInvoice.shipmentId));
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			poInvoices = delegator.findList("Invoice", cond, null, null, null, false);	
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, poOrderItemSeqId));
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, poInvoices[0].invoiceId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			POrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
			
 
			poInvoiceId = "";
			poInvoiceItemSeqId = "";
			if(POrderItemBilling){
			poInvoiceId  = POrderItemBilling[0].invoiceId;
			poInvoiceItemSeqId  = POrderItemBilling[0].invoiceItemSeqId;
			}
			
			
			//Debug.log("poInvoiceId============="+poInvoiceId);
			//Debug.log("poInvoiceItemSeqId============="+poInvoiceItemSeqId);
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, poInvoiceId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, poInvoiceItemSeqId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, null));
			//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			POInvoiceItemList = delegator.findList("InvoiceItem", cond, null, null, null, false);
			
			
			//Debug.log("POInvoiceItemList=======1212======"+POInvoiceItemList);
			
			
			popartyId = "";
			poinvoiceDate = "";
			shipmentId = "";
			
			if(poInvoiceId){
			poInvoiceList = delegator.findOne("Invoice",[invoiceId : poInvoiceId] , false);
			popartyId = poInvoiceList.get("partyId");
			poinvoiceDate = poInvoiceList.get("invoiceDate");
			shipmentId = poInvoiceList.get("shipmentId");
	        }
			
			//Debug.log("popartyId============="+popartyId);
			//Debug.log("poinvoiceDate============="+poinvoiceDate);
			//Debug.log("shipmentId============="+shipmentId);
			
			
			if(POInvoiceItemList){
			
			billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , POInvoiceItemList[0].invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
			if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
				invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
				invoiceSequence = invoiceSeqDetails.invoiceSequence;
				tempMap.put("millInvoiceId", invoiceSequence);
			}else{
				tempMap.put("millInvoiceId", POInvoiceItemList[0].invoiceId);
			}
			
			
			if(poinvoiceDate)
			tempMap.put("millInvoiceDate", UtilDateTime.toDateString(poinvoiceDate,"dd-MM-yyyy"));
			else
			tempMap.put("millInvoiceDate","");
			
			
			//tempMap.put("poInvoiceAmt", POInvoiceItemList[0].quantity*POInvoiceItemList[0].amount);
			
			tempMap.put("poInvoiceAmt", POInvoiceItemList[0].itemValue);
			
			//tempMap.put("poInvoiceBasicAmt", POInvoiceItemList[0].quantity*POInvoiceItemList[0].amount);
			
			tempMap.put("poInvoiceBasicAmt", POInvoiceItemList[0].itemValue);
			
			}
			
			if(TallyPoNumber)
			tempMap.put("TallyPoNumber", TallyPoNumber);
			else
			tempMap.put("TallyPoNumber", "");
			
			
			PoorderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , poOrderId)  , UtilMisc.toSet("orderNo"), null, null, false );
			
			if(UtilValidate.isNotEmpty(PoorderHeaderSequences)){
				orderSeqDetails = EntityUtil.getFirst(PoorderHeaderSequences);
				draftPoNum = orderSeqDetails.orderNo;
				tempMap.put("poOrderId", draftPoNum);
			}else{
				tempMap.put("poOrderId", poOrderId);
			}

			PorderDate = "";
			if(poOrderId){
			OrderHeader = delegator.findOne("OrderHeader",[orderId : poOrderId] , false);
			PorderDate = OrderHeader.orderDate;
	        }
			
			if(PorderDate)
	        tempMap.put("poorderDate", UtilDateTime.toDateString(PorderDate,"dd-MM-yyyy"));
			else
			tempMap.put("poorderDate", "");
			
			
			if(shipmentId){
			shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
			
			lrNumber = shipmentList.get("lrNumber");
			
			if(lrNumber)
			tempMap.put("lrNumber", lrNumber);
			else
			tempMap.put("lrNumber", "");
			
			deliveryChallanDate = shipmentList.get("deliveryChallanDate");
			
			if(deliveryChallanDate)
			tempMap.put("lrDate",UtilDateTime.toDateString(deliveryChallanDate,"dd-MM-yyyy") );
			else
			tempMap.put("lrDate", "");
			
			
			estimatedShipCost = shipmentList.get("estimatedShipCost");
						
			if(estimatedShipCost && dontRepeat.size() == 1)
			tempMap.put("freight", estimatedShipCost);
			else
			tempMap.put("freight", "");
			
			estimatedShipCost = shipmentList.get("estimatedShipCost");
			
			if(estimatedShipCost)
			tempMap.put("eligibility", estimatedShipCost);
			else
			tempMap.put("eligibility", "");
			
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
			
			}

			
			
			DstAddr = delegator.findOne("OrderAttribute",["orderId":poOrderId,"attrName":"DST_ADDR"],false);
			if(DstAddr){
				destAddr=DstAddr.get("attrValue");
				tempMap.put("destAddr", destAddr);
			}else{
		    	tempMap.put("destAddr", "");
			}
			
			
			if(eachItem){
				
				ProductList = delegator.findOne("Product",[productId : eachItem.productId] , false);
				
				primaryProductCategoryId = ProductList.primaryProductCategoryId;
				
				///ProductList = delegator.findOne("ProductCategory",[productId : primaryProductCategoryId] , false);
				
				tempMap.put("ProductCategoryId", primaryProductCategoryId);
				
			}
			
			tempMap.put("itemDescription", eachItem.description);
			
			tempMap.put("ledgerName", "");
			
			if(POInvoiceItemList[0])
			tempMap.put("purchaseQuantity", POInvoiceItemList[0].quantity);
			else
			tempMap.put("purchaseQuantity", "");
						
			
			tempMap.put("salesQuantity", eachItem.quantity);
			
			tempMap.put("rate", eachItem.amount);
			
			
			//checking for tally ref no in purchase invoice
			if(UtilValidate.isEmpty(tallyRefNo)){
				List orderAssoc = delegator.findByAnd("OrderAssoc", UtilMisc.toMap("toOrderId", actualOrderId,"orderAssocTypeId","BackToBackOrder"));
				if(UtilValidate.isNotEmpty(orderAssoc)){
					String poOrderId = EntityUtil.getFirst(orderAssoc).orderId;
					List orderItemBilling = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId) , null, null, null, false );
					if(UtilValidate.isNotEmpty(orderItemBilling)){
						GenericValue orderItem = EntityUtil.getFirst(orderItemBilling);
						String purInvId = orderItem.invoiceId;
						GenericValue invoice =  delegator.findOne("Invoice", [invoiceId : purInvId], false);
						if(UtilValidate.isNotEmpty(invoice)){
							tallyRefNo = invoice.referenceNumber;
						}
					}
				}
			}
			//checking for tally ref no in order
			if(UtilValidate.isEmpty(tallyRefNo)){
				List orderItemBilling = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId) , null, null, null, false );
				if(UtilValidate.isNotEmpty(orderItemBilling)){
					GenericValue orderItem = EntityUtil.getFirst(orderItemBilling);
					orderId = orderItem.orderId;
					GenericValue orderHeader = delegator.findOne("OrderHeader", ["orderId" : orderId], false);
					if(UtilValidate.isNotEmpty(orderHeader)){
						tallyRefNo = orderHeader.tallyRefNo;
					}
				}
			}
			
			tempMap.put("tallyRefNo", tallyRefNo);
			
			if(eachInvoice.invoiceDate)
			tempMap.put("tallyRefDate", UtilDateTime.toDateString(eachInvoice.invoiceDate,"dd-MM-yyyy"));
			else
			tempMap.put("tallyRefDate", "");
			
			
			tempMap.put("invoiceNetAmt", invoiceNetAmt);
			
			
		   salesAndPurchaseList.add(tempMap);
		
	}
	
}else if(purposeTypeId == "DEPOT_YARN_SALE"){
	

//Debug.log("purposeTypeId============="+purposeTypeId);

	tallyRefNo = "";
	if(eachInvoice.referenceNumber)
	tallyRefNo = eachInvoice.referenceNumber;
	
	
	invoiceItemList = EntityUtil.filterByCondition(InvoiceItem, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
	
	
	PartyGroup = delegator.findOne("PartyGroup",[partyId : eachInvoice.partyIdFrom] , false);
	
	
	//Debug.log("eachInvoice.invoiceId============"+eachInvoice.invoiceId);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
	//conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemBillingDe = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);

	
	itemOrderIdDe  = OrderItemBillingDe[0].orderId;
	orderItemSeqIdDe  = OrderItemBillingDe[0].orderItemSeqId;
	
	//Debug.log("itemOrderIdDe============"+itemOrderIdDe);
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , itemOrderIdDe));
	conditionList.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS , "ORDRITEM_INVENTORY_ID"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemAttribute = delegator.findList("OrderItemAttribute",  cond,null, null, null, false );
	
	inventoryItemId = "";
	if(OrderItemAttribute){
		inventoryItemId = EntityUtil.getFirst(OrderItemAttribute).attrValue;
	}
	
	//Debug.log("inventoryItemId============"+inventoryItemId);
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS , inventoryItemId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	ShipmentReceipt = delegator.findList("ShipmentReceipt",  cond,null, null, null, false );
	
	shipmentIdDe = "";
	if(ShipmentReceipt){
		shipmentIdDe = EntityUtil.getFirst(ShipmentReceipt).shipmentId;
	}

	
	//Debug.log("shipmentId============"+shipmentId);
	
	groupName = "";
	if(PartyGroup)
	  groupName = PartyGroup.groupName;
	  
	  
	  //Debug.log("groupName============"+groupName);
	  
	
	shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentIdDe] , false);
	
	//Debug.log("shipmentList============"+shipmentList);
	
	
	primaryOrderId = shipmentList.get("primaryOrderId");
	
	//Debug.log("primaryOrderId============"+primaryOrderId);
	/*exprCondList=[];
	exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
	exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
	OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
	
	actualOrderId = "";
	if(OrderAss){
		
		actualOrderId=OrderAss.toOrderId;
		
	}*/
	
	actualOrderId = itemOrderIdDe;
	
	//Debug.log("actualOrderId============"+actualOrderId);
	
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
	expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, primaryOrderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER_AGENT","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
	expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderRoleListDepot = delegator.findList("OrderRole", expr, null, null, null, false);
	
	
	partyId = "";
	supplier = "";
	
	for (eachRole in OrderRoleList) {
		/*if(eachRole.roleTypeId == "SUPPLIER")
		 supplier = eachRole.get("partyId");*/
		if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
		 partyId = eachRole.get("partyId");
		/*if(eachRole.roleTypeId == "ON_BEHALF_OF")
			 onbehalf = true;*/
	}
	
	for (eachRole in OrderRoleListDepot) {
		if(eachRole.roleTypeId == "SUPPLIER_AGENT")
		 supplier = eachRole.get("partyId");
	}
	
	
	partyName = "";
	if(partyId)
	 partyName = PartyHelper.getPartyName(delegator, partyId, false);

	 supplierName = "";
	 if(supplier)
	 supplierName = PartyHelper.getPartyName(delegator, supplier, false);
	
	 //Debug.log("partyId==================="+partyId);
	 
	 
	 //Debug.log("supplier==================="+supplier);
	
	//=================Addresss======================
	
	contactMechesDetails = [];
	conditionListAddress = [];
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
	conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	 List<String> orderBy = UtilMisc.toList("-contactMechId");
	contactMech = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy, null, false);
	
	
	
	if(contactMech){
	contactMechesDetails = contactMech;
	}
	else{
		conditionListAddress.clear();
		conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
		conditionListAddress.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
		List<String> orderBy2 = UtilMisc.toList("-contactMechId");
		contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, orderBy2, null, false);
	}
	
	shipingAdd = [:];
	
	if(contactMechesDetails){
		contactMec=contactMechesDetails.getFirst();
		if(contactMec){
			//partyPostalAddress=contactMec.get("postalAddress");
			
			partyPostalAddress=contactMec;
			
			//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
		//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
			if(partyPostalAddress){
				address1="";
				address2="";
				state="";
				city="";
				postalCode="";
				if(partyPostalAddress.get("address1")){
				address1=partyPostalAddress.get("address1");
				//Debug.log("address1=========================="+address1);
				}
				if(partyPostalAddress.get("address2")){
					address2=partyPostalAddress.get("address2");
					}
				if(partyPostalAddress.get("city")){
					city=partyPostalAddress.get("city");
					}
				if(partyPostalAddress.get("stateGeoName")){
					state=partyPostalAddress.get("stateGeoName");
					}
				if(partyPostalAddress.get("postalCode")){
					postalCode=partyPostalAddress.get("postalCode");
					}
				//shipingAdd.put("name",shippPartyName);
				shipingAdd.put("address1",address1);
				shipingAdd.put("address2",address2);
				shipingAdd.put("city",city);
				shipingAdd.put("state",state);
				shipingAdd.put("postalCode",postalCode);
				
			}
		}
	}
	
	
	//===============address for supplier====================
	
	conditionListAddress.clear();
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplier));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
	contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);

shipingAddForSupplier = [:];

if(contactMechesDetails){
	contactMec=contactMechesDetails.getFirst();
	if(contactMec){
		//partyPostalAddress=contactMec.get("postalAddress");
		
		partyPostalAddress=contactMec;
		
		//Debug.log("partyPostalAddress=========================="+partyPostalAddress);
	//	partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:invoicePartyId, userLogin: userLogin]);
		if(partyPostalAddress){
			address1="";
			address2="";
			state="";
			city="";
			postalCode="";
			if(partyPostalAddress.get("address1")){
			address1=partyPostalAddress.get("address1");
			//Debug.log("address1=========================="+address1);
			}
			if(partyPostalAddress.get("address2")){
				address2=partyPostalAddress.get("address2");
				}
			if(partyPostalAddress.get("city")){
				city=partyPostalAddress.get("city");
				}
			if(partyPostalAddress.get("stateGeoName")){
				state=partyPostalAddress.get("stateGeoName");
				}
			if(partyPostalAddress.get("postalCode")){
				postalCode=partyPostalAddress.get("postalCode");
				}
			//shipingAdd.put("name",shippPartyName);
			shipingAddForSupplier.put("address1",address1);
			shipingAddForSupplier.put("address2",address2);
			shipingAddForSupplier.put("city",city);
			shipingAddForSupplier.put("state",state);
			shipingAddForSupplier.put("postalCode",postalCode);
			
		}
	}
}

	
	
	//partyPostalAddress= dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
	
	//Debug.log("partyPostalAddress===================="+partyPostalAddress);
	
	//Debug.log("shipingAdd========================="+shipingAdd);
	//================================================
	
	//============IS Depo
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	FacilityList = delegator.findList("Facility", fcond, null, null, null, false);
	
	
	isDepot = "";
	if(FacilityList)
	isDepot ="DEPOT";
	else
	isDepot ="NON DEPOT";
	
  //-------------------------------
	
   //============Scheme============
	
	orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
	
	scheme = "";
	if(UtilValidate.isNotEmpty(orderAttr)){
		orderAttr.each{ eachAttr ->
			if(eachAttr.attrName == "SCHEME_CAT"){
				scheme =  eachAttr.attrValue;
			}
			
		}
	   }
	
	
	//==========================Sequence=======================
	
	orderHeaderSequencesfilter = EntityUtil.filterByCondition(orderHeaderSequences, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	
	
	
	
	//Debug.log("orderHeaderSequencesfilter===3223============="+orderHeaderSequencesfilter);
	
	orderNo ="NA";
	if(UtilValidate.isNotEmpty(orderHeaderSequencesfilter)){
		orderSeqDetails = EntityUtil.getFirst(orderHeaderSequencesfilter);
		orderNo = orderSeqDetails.orderNo;
	}
	
	//=============indentDate============
	//Debug.log("orderNo================"+orderNo);
	
	
	orderDate = "";
	TallyPoNumber = "";
	if(actualOrderId){
	OrderHeader = delegator.findOne("OrderHeader",[orderId : actualOrderId] , false);
	orderDate = OrderHeader.orderDate;
	TallyPoNumber = OrderHeader.tallyRefNo;
	}
	
	//Debug.log("TallyPoNumber================"+TallyPoNumber);
	
	//=============================advance Payment=============================================
	
	conditonList = [];
	conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS, actualOrderId));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	OrderPaymentPreference = delegator.findList("OrderPaymentPreference", cond, null, null, null ,false);
	double paidAmt = 0;
	
	paymentIdsOfIndentPayment = [];
	
	if(OrderPaymentPreference){
	
	orderPreferenceIds = EntityUtil.getFieldListFromEntityList(OrderPaymentPreference,"orderPaymentPreferenceId", true);
 
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("paymentPreferenceId" ,EntityOperator.IN,orderPreferenceIds));
	conditonList.add(EntityCondition.makeCondition("statusId" ,EntityOperator.NOT_EQUAL, "PMNT_VOID"));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	PaymentList = delegator.findList("Payment", cond, null, null, null ,false);
	
	paymentIdsOfIndentPayment = EntityUtil.getFieldListFromEntityList(PaymentList,"paymentId", true);
	
	
	for (eachPayment in PaymentList) {
		paidAmt = paidAmt+eachPayment.get("amount");
	}
	
  }
	
	
	//Debug.log("paidAmt================"+paidAmt);
	
	
	double appliedAmt = 0;
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("orderId" ,EntityOperator.EQUALS,actualOrderId));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	OrderItemBillingList = delegator.findList("OrderItemBilling", cond, null, null, null ,false);
	
	invoiceIds = EntityUtil.getFieldListFromEntityList(OrderItemBillingList,"invoiceId", true);
	
	if(invoiceIds){
	conditonList.clear();
	conditonList.add(EntityCondition.makeCondition("invoiceId" ,EntityOperator.IN,invoiceIds));
	cond = EntityCondition.makeCondition(conditonList, EntityOperator.AND);
	PaymentApplicationList = delegator.findList("PaymentApplication", cond, null, null, null ,false);
	
		for (eachList in PaymentApplicationList) {
			 if(!paymentIdsOfIndentPayment.contains(eachList.paymentId))
				appliedAmt = appliedAmt+eachList.amountApplied;
		}
	}
	
	
//==============================All Taxes==================================================
	
	
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderAdjustment = delegator.findList("OrderAdjustment", condExpr, null, null, null, false);

	if(OrderAdjustment){
	for (eachAdjment in OrderAdjustment) {
		
		if(eachAdjment.orderAdjustmentTypeId != "TEN_PERCENT_SUBSIDY"){
			altaxAmt = altaxAmt +Double.valueOf( eachAdjment.amount);
		}
	}
	}*/
	
//==============================ten Per==================================
	
	
	//Debug.log("eachInvoice.invoiceId===================="+eachInvoice.invoiceId);
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_FPROD_ITEM"));
	
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceItemAdjustment = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);
	
	double tenPerAmt = 0;
	for (eachAdjment in InvoiceItemAdjustment) {
		
		if(eachAdjment.invoiceItemTypeId == "TEN_PERCENT_SUBSIDY"){
			//tenPerAmt = tenPerAmt +Double.valueOf( eachAdjment.amount);
			
			//Debug.log("eachAdjment.itemValue===================="+eachAdjment.itemValue);
			
			tenPerAmt = tenPerAmt +Double.valueOf(eachAdjment.itemValue);
		}
	}
	
	
	
	//Debug.log("tenPerAmt===================="+tenPerAmt);
//=============================================
	
//=====================for Adjustments=================================================
	
	
	
	double allAdjWitOutTEN = 0;
	
	condList.clear();
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));
	condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, ["INV_FPROD_ITEM","TEN_PERCENT_SUBSIDY","VAT_PUR", "CST_PUR","CST_SALE","VAT_SALE","CESS_SALE","CESS_PUR","VAT_SURHARGE","TEN_PER_CHARGES","TEN_PER_DISCOUNT"]));
	
	invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	
	InvoiceRemainItemAdjustment = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);

	if(InvoiceRemainItemAdjustment){
		for (eachAdjustment in InvoiceRemainItemAdjustment) {
			
			//Debug.log("eachAdjustment.itemValue================"+eachAdjustment.itemValue);
			
			
			allAdjWitOutTEN = allAdjWitOutTEN+eachAdjustment.itemValue;
		}
	}
	
	
	//Debug.log("allAdjWitOutTEN================"+allAdjWitOutTEN);
	
	//=======================================================
	
	//Debug.log("invoiceItemList================="+invoiceItemList.size());
	
	double invoAmt = 0;
	
	dontRepeat = [];
	
	double grandTotal = 0;
	for (eachItem in invoiceItemList) {
		
		
		
		  dontRepeat.add(eachItem.invoiceId);
		
		
		   double invoiceNetAmt = 0;
		
		   tempMap = [:];
		   
		   billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , eachItem.invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
		   if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
			   invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
			   invoiceSequence = invoiceSeqDetails.invoiceSequence;
			   tempMap.put("invoiceId", invoiceSequence);
		   }else{
			   tempMap.put("invoiceId", eachItem.invoiceId);
		   }
		
		   tempMap.put("invoiceDate",UtilDateTime.toDateString(eachInvoice.invoiceDate,"dd-MM-yyyy"));
		   
		 //  invoAmt = invoAmt+(eachItem.amount*eachItem.quantity);
		   
		  // tempMap.put("invoiceAmount", (eachItem.amount*eachItem.quantity));
		   
		   tempMap.put("invoiceAmount", eachItem.itemValue);
		   
		  // invoiceNetAmt = invoiceNetAmt+Double.valueOf((eachItem.amount*eachItem.quantity));
		   
		   invoiceNetAmt = invoiceNetAmt+eachItem.itemValue;
		   
		   if(dontRepeat.size() == 1)
		   invoiceNetAmt = invoiceNetAmt+allAdjWitOutTEN;
		   
		  //Debug.log("eachItem.invoiceId================="+eachItem.invoiceId);
		   //Debug.log("eachItem.invoiceItemSeqId================="+eachItem.invoiceItemSeqId);
		   
		   
		   conditionList.clear();
		   conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
		   conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   OrderItemBillingDePP = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
		  
		   //Debug.log("OrderItemBilling======================"+OrderItemBilling);
		   
				  
		 itemOrderId  = OrderItemBillingDePP[0].orderId;
		 orderItemSeqId  = OrderItemBillingDePP[0].orderItemSeqId;
		 
		 //Debug.log("itemOrderId======================"+itemOrderId);
		 
		 //Debug.log("orderItemSeqId======================"+orderItemSeqId);
		 
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,itemOrderId));
		 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
	
		 //Debug.log("OrderItemDetail======================"+OrderItemDetail);
		 
	
		 quantity = eachItem.quantity;
		 amount = eachItem.amount;
		
		 double baleQty = 0;
		 double unit = 0;
		 double quotaQuantity = 0;
	  
		 for (eachOrderItemDetail in OrderItemDetail) {
		
			 if(eachOrderItemDetail.baleQuantity)
			 baleQty = baleQty+Double.valueOf(eachOrderItemDetail.baleQuantity);
			 
			 
			 if(eachOrderItemDetail.unitPrice)
			 unit = unit+Double.valueOf(eachOrderItemDetail.unitPrice);
			 
			 
			 if(eachOrderItemDetail.quotaQuantity)
			 quotaQuantity = quotaQuantity+Double.valueOf(eachOrderItemDetail.quotaQuantity);
			 
		}
		 
		 //Debug.log("unit======================"+unit);
		 
		 tempMap.put("unit", unit);
		 
		 
			if(baleQty)
			tempMap.put("baleQty",baleQty);
			else
			tempMap.put("baleQty","");
			
			
		   tempMap.put("quantity", eachItem.quantity);
		
		//String schemeAmt = (String)SchemeQtyMap.get(eachInvoiceList.invoiceItemSeqId);
			
			
			//==============scheme Quantity===============
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
			conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachItem.invoiceItemSeqId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			invoiceInnerAdjItemList = EntityUtil.filterByCondition(InvoiceItemAdjustment, cond);
	   
			
			
			
			double schemeQQQty = 0;
			double schemeAMMMt = 0;
			
			
			if(invoiceInnerAdjItemList){
				
				////Debug.log("invoiceInnerAdjItemList============="+invoiceInnerAdjItemList);
				schemeAMMMt = Math.round(invoiceInnerAdjItemList[0].amount);
				
				 invoiceIdAdj = invoiceInnerAdjItemList[0].invoiceId;
				 invoiceItemSeqIdAdj = invoiceInnerAdjItemList[0].invoiceItemSeqId;
				 
				 
				 conditionList.clear();
				 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceIdAdj));
				 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdAdj));
				 conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
				 cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				 OrderAdjustmentAndBilling = delegator.findList("OrderAdjustmentAndBilling", cond1, null, null, null, false);
	 
				 if(OrderAdjustmentAndBilling[0]){
				 schemeQQQty = OrderAdjustmentAndBilling[0].quantity;
				 //schemeAMMMt = Math.round(OrderAdjustmentAndBilling[0].amount);
				
				 }
			}
			

			//Debug.log("schemeQQQty======================"+schemeQQQty);
			
			//Debug.log("schemeAMMMt======================"+schemeAMMMt);
			
			
							
			double tenPerQty = 0;
			tenPerQty = schemeQQQty;
			
			tempMap.put("schemeQty", schemeQQQty);
			
			
			//=============================Tax Amount======================
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
			conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachItem.invoiceItemSeqId));
			//conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,"TEN_PERCENT_SUBSIDY"));
			//conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL,"INVOICE_ITM_ADJ"));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE")));
			
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			invoiceVatCstList = EntityUtil.filterByCondition(InvoiceItemAdjustment, cond);
	   
			double taxAmt = 0;
			if(invoiceVatCstList){
				 for (eachAdj in invoiceVatCstList) {
					 taxAmt = taxAmt+eachAdj.itemValue;
				}
			}
			
			//Debug.log("taxAmt======================"+taxAmt);
			//Debug.log("allAdjWitOutTEN======================"+allAdjWitOutTEN);
			
			
			tempMap.put("altaxAmt", taxAmt);
			
			if(dontRepeat.size() == 1)
			tempMap.put("allAdjWitOutTEN", allAdjWitOutTEN);
			else
			tempMap.put("allAdjWitOutTEN", "");
			
			
			invoiceNetAmt = invoiceNetAmt+taxAmt;
			
			//=====================================================================
			
			/*if(quantity > quotaQuantity)
			{
			  tempMap.put("schemeQty", quotaQuantity);
			  tenPerQty = quotaQuantity;
			}
			else
			{
			  tempMap.put("schemeQty", quantity);
			  tenPerQty = quantity;
			}*/
		
		/*if(UtilValidate.isNotEmpty(schemeAmt))
		  tempMap.put("schemeQty", Double.valueOf(schemeAmt));
		else
		  tempMap.put("schemeQty", 0);
*/
		  
		  if(scheme == "General")
		  tempMap.put("mgpsQty", 0);
		  else
		  tempMap.put("mgpsQty", quantity-tenPerQty);
			
		  //Debug.log("tenPerQty============4343========"+tenPerQty);
		  
		 double serviceAmt = 0;
		 double sourcePercentage = 0;
		 
		  if(scheme == "General"){
			  
			  conditionList.clear();
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachItem.invoiceItemSeqId));
			  conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
			  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			  invoiceInnerAdjItemList = EntityUtil.filterByCondition(InvoiceItemAdjustment, cond);
			  
			 // //Debug.log("invoiceInnerAdjItemList===================="+invoiceInnerAdjItemList);
			  
			 // //Debug.log("invoiceInnerAdjItemList===================="+invoiceInnerAdjItemList[0].itemValue);
			  
			  
			  if(invoiceInnerAdjItemList[0]){
			  serviceAmt = serviceAmt+invoiceInnerAdjItemList[0].itemValue;
			  //sourcePercentage = sourcePercentage+invoiceInnerAdjItemList[0].sourcePercentage;
			  
			  }
		  }
		  
		  
		  //Debug.log("serviceAmt===================="+serviceAmt);
		  
		  if(scheme == "General"){
			  
			  sourcePercentage = (serviceAmt/(eachItem.itemValue))*100;
			  double perAmt = (eachItem.amount*sourcePercentage)/100;
			  
			  tempMap.put("amount",(eachItem.amount+perAmt));
			  }else{
			  tempMap.put("amount", eachItem.amount);
		   }
			  
		  
		  tempMap.put("ToTamount", eachItem.itemValue+serviceAmt);
		  grandTotal = grandTotal+eachItem.itemValue+serviceAmt;
		  
		   
		   
			tempMap.put("userAgency", partyName);
			
			tempMap.put("cluster", "");
			
			tempMap.put("subsidyAmt", schemeAMMMt);
			
			invoiceNetAmt = invoiceNetAmt+schemeAMMMt;
			
			tempMap.put("District", shipingAdd.get("city"));
			
			tempMap.put("branch", groupName);
			
			tempMap.put("sate", shipingAdd.get("state"));
			
			tempMap.put("AgencyType", "");
			
			tempMap.put("Depo", isDepot);
			
			if(scheme == "MGPS_10Pecent")
			tempMap.put("scheme", "MGPS + 10Pecent");
			else
			tempMap.put("scheme", scheme);
			
			if(orderNo != "NA")
			tempMap.put("orderNo", orderNo);
			else
			tempMap.put("orderNo", actualOrderId);
			
			if(orderDate)
			tempMap.put("indentDate",UtilDateTime.toDateString(orderDate,"dd-MM-yyyy"));
			else
			tempMap.put("indentDate","");
			
			
			tempMap.put("custIndDate", "");
			
			tempMap.put("advance", paidAmt);
			
			tempMap.put("appliedAmt", appliedAmt);
			
			tempMap.put("balance", paidAmt-appliedAmt);
			
			tempMap.put("advance", paidAmt);
			
			tempMap.put("cheque/dd", "");
			
			tempMap.put("tallyBillAmt", "");
			
			tempMap.put("supplierName", supplierName);
			
			tempMap.put("millState", shipingAddForSupplier.get("state"));
			
			tempMap.put("source", "");
			
			tempMap.put("millCategory", "");
			
			
			//================================purchase Invoice Details=====================
			
			/*//Debug.log("itemOrderId============="+itemOrderId);
			//Debug.log("orderItemSeqId============="+orderItemSeqId);
			

			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, itemOrderId));
			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItemAssoc = delegator.findList("OrderItemAssoc", cond, null, null, null, false);

			
			poOrderId = "";
			poOrderItemSeqId = "";
			if(OrderItemAssoc){
			poOrderId  = OrderItemAssoc[0].toOrderId;
			poOrderItemSeqId  = OrderItemAssoc[0].toOrderItemSeqId;
			}*/
			
			poOrderId = "";
			poOrderItemSeqId = "";
			
			poOrderId = primaryOrderId;
			
			//Debug.log("poOrderId============="+poOrderId);
			//Debug.log("poOrderItemSeqId============="+poOrderItemSeqId);
			
			//Debug.log("eachInvoice.shipmentId============="+eachInvoice.shipmentId);
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			poInvoices = delegator.findList("Invoice", cond, null, null, null, false);
			
			
			/*conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, "00001"));
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, poInvoices[0].invoiceId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			POrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
			*/
 
			/*poInvoiceId = "";
			poInvoiceItemSeqId = "";
			if(POrderItemBilling){
			poInvoiceId  = POrderItemBilling[0].invoiceId;
			poInvoiceItemSeqId  = POrderItemBilling[0].invoiceItemSeqId;
			}
			*/
			
			poInvoiceId = "";
			poInvoiceItemSeqId = "";
			
			if(poInvoices){
				poInvoiceId = poInvoices[0].invoiceId;
			}
			
			
			//Debug.log("poInvoiceId============="+poInvoiceId);
			//Debug.log("poInvoiceItemSeqId============="+poInvoiceItemSeqId);
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, poInvoiceId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, "00001"));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, null));
			//conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			POInvoiceItemList = delegator.findList("InvoiceItem", cond, null, null, null, false);
			
			//Debug.log("POInvoiceItemList=======1212======"+POInvoiceItemList);
			
			
			popartyId = "";
			poinvoiceDate = "";
			shipmentId = "";
			
			if(poInvoiceId){
			poInvoiceList = delegator.findOne("Invoice",[invoiceId : poInvoiceId] , false);
			popartyId = poInvoiceList.get("partyId");
			poinvoiceDate = poInvoiceList.get("invoiceDate");
			//shipmentId = poInvoiceList.get("shipmentId");
			}
			
			//Debug.log("popartyId============="+popartyId);
			//Debug.log("poinvoiceDate============="+poinvoiceDate);
			//Debug.log("shipmentId============="+shipmentId);
			
			
			if(POInvoiceItemList){
			
			billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , POInvoiceItemList[0].invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
			if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
				invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
				invoiceSequence = invoiceSeqDetails.invoiceSequence;
				tempMap.put("millInvoiceId", invoiceSequence);
			}else{
				tempMap.put("millInvoiceId", POInvoiceItemList[0].invoiceId);
			}
			
			
			if(poinvoiceDate)
			tempMap.put("millInvoiceDate", UtilDateTime.toDateString(poinvoiceDate,"dd-MM-yyyy"));
			else
			tempMap.put("millInvoiceDate","");
			
			
			//tempMap.put("poInvoiceAmt", POInvoiceItemList[0].quantity*POInvoiceItemList[0].amount);
			
			tempMap.put("poInvoiceAmt", POInvoiceItemList[0].itemValue);
			
			//tempMap.put("poInvoiceBasicAmt", POInvoiceItemList[0].quantity*POInvoiceItemList[0].amount);
			
			tempMap.put("poInvoiceBasicAmt", POInvoiceItemList[0].itemValue);
			
			}
			
			if(TallyPoNumber)
			tempMap.put("TallyPoNumber", TallyPoNumber);
			else
			tempMap.put("TallyPoNumber", "");
			
			
			PoorderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , poOrderId)  , UtilMisc.toSet("orderNo"), null, null, false );
			
			if(UtilValidate.isNotEmpty(PoorderHeaderSequences)){
				orderSeqDetails = EntityUtil.getFirst(PoorderHeaderSequences);
				draftPoNum = orderSeqDetails.orderNo;
				tempMap.put("poOrderId", draftPoNum);
			}else{
				tempMap.put("poOrderId", poOrderId);
			}

			PorderDate = "";
			if(poOrderId){
			OrderHeader = delegator.findOne("OrderHeader",[orderId : poOrderId] , false);
			PorderDate = OrderHeader.orderDate;
			}
			
			if(PorderDate)
			tempMap.put("poorderDate", UtilDateTime.toDateString(PorderDate,"dd-MM-yyyy"));
			else
			tempMap.put("poorderDate", "");
			
			
			if(shipmentIdDe){
			shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentIdDe] , false);
			
			lrNumber = shipmentList.get("lrNumber");
			
			if(lrNumber)
			tempMap.put("lrNumber", lrNumber);
			else
			tempMap.put("lrNumber", "");
			
			deliveryChallanDate = shipmentList.get("deliveryChallanDate");
			
			if(deliveryChallanDate)
			tempMap.put("lrDate",UtilDateTime.toDateString(deliveryChallanDate,"dd-MM-yyyy") );
			else
			tempMap.put("lrDate", "");
			
			
			estimatedShipCost = shipmentList.get("estimatedShipCost");
						
			if(estimatedShipCost && dontRepeat.size() == 1)
			tempMap.put("freight", estimatedShipCost);
			else
			tempMap.put("freight", "");
			
			estimatedShipCost = shipmentList.get("estimatedShipCost");
			
			if(estimatedShipCost)
			tempMap.put("eligibility", estimatedShipCost);
			else
			tempMap.put("eligibility", "");
			
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
			
			}

			
			
			DstAddr = delegator.findOne("OrderAttribute",["orderId":poOrderId,"attrName":"DST_ADDR"],false);
			if(DstAddr){
				destAddr=DstAddr.get("attrValue");
				tempMap.put("destAddr", destAddr);
			}else{
				tempMap.put("destAddr", "");
			}
			
			
			if(eachItem){
				
				ProductList = delegator.findOne("Product",[productId : eachItem.productId] , false);
				
				primaryProductCategoryId = ProductList.primaryProductCategoryId;
				
				///ProductList = delegator.findOne("ProductCategory",[productId : primaryProductCategoryId] , false);
				
				tempMap.put("ProductCategoryId", primaryProductCategoryId);
				
			}
			
			tempMap.put("itemDescription", eachItem.description);
			
			tempMap.put("ledgerName", "");
			
			if(POInvoiceItemList[0])
			tempMap.put("purchaseQuantity", POInvoiceItemList[0].quantity);
			else
			tempMap.put("purchaseQuantity", "");
						
			
			tempMap.put("salesQuantity", eachItem.quantity);
			
			tempMap.put("rate", eachItem.amount);
			
			
			//checking for tally ref no in purchase invoice
			if(UtilValidate.isEmpty(tallyRefNo)){
				List orderAssoc = delegator.findByAnd("OrderAssoc", UtilMisc.toMap("toOrderId", actualOrderId,"orderAssocTypeId","BackToBackOrder"));
				if(UtilValidate.isNotEmpty(orderAssoc)){
					String poOrderId = EntityUtil.getFirst(orderAssoc).orderId;
					List orderItemBilling = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId) , null, null, null, false );
					if(UtilValidate.isNotEmpty(orderItemBilling)){
						GenericValue orderItem = EntityUtil.getFirst(orderItemBilling);
						String purInvId = orderItem.invoiceId;
						GenericValue invoice =  delegator.findOne("Invoice", [invoiceId : purInvId], false);
						if(UtilValidate.isNotEmpty(invoice)){
							tallyRefNo = invoice.referenceNumber;
						}
					}
				}
			}
			//checking for tally ref no in order
			if(UtilValidate.isEmpty(tallyRefNo)){
				List orderItemBilling = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId) , null, null, null, false );
				if(UtilValidate.isNotEmpty(orderItemBilling)){
					GenericValue orderItem = EntityUtil.getFirst(orderItemBilling);
					orderId = orderItem.orderId;
					GenericValue orderHeader = delegator.findOne("OrderHeader", ["orderId" : orderId], false);
					if(UtilValidate.isNotEmpty(orderHeader)){
						tallyRefNo = orderHeader.tallyRefNo;
					}
				}
			}
			
			tempMap.put("tallyRefNo", tallyRefNo);
			
			if(eachInvoice.invoiceDate)
			tempMap.put("tallyRefDate", UtilDateTime.toDateString(eachInvoice.invoiceDate,"dd-MM-yyyy"));
			else
			tempMap.put("tallyRefDate", "");
			
			
			tempMap.put("invoiceNetAmt", invoiceNetAmt);
			
			
		   salesAndPurchaseList.add(tempMap);
		
	}
}

}
}

////Debug.log("salesAndPurchaseList=================="+salesAndPurchaseList);

context.salesAndPurchaseList = salesAndPurchaseList;




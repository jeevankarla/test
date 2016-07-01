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

partyfromDate=parameters.partyfromDate;
partythruDate=parameters.partythruDate;

branchId = parameters.branchId;

branchList = [];

condListb = [];

condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);

PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);

branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);

if(!branchList)
branchList.add(branchId);



Debug.log("branchList=================="+branchList);



if(UtilValidate.isNotEmpty(parameters.partyfromDate)){
  
	try {
		//daystart = UtilDateTime.toTimestamp(sdf.parse(parameters.partyfromDate));
		
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.partyfromDate).getTime());
		
		 } catch (ParseException e) {
			 Debug.logError(e, "Cannot parse date string: " + parameters.partyfromDate, "");
			 }
   
}
if(UtilValidate.isNotEmpty(parameters.partythruDate)){
   
   try {
	 //  dayend = UtilDateTime.toTimestamp(sdf.parse(parameters.partythruDate));
	   
	   thruDate = new java.sql.Timestamp(sdf.parse(parameters.partythruDate).getTime());
   } catch (ParseException e) {
	   Debug.logError(e, "Cannot parse date string: " + parameters.partythruDate, "");
		}
}

daystart = UtilDateTime.getDayStart(fromDate);
dayend = UtilDateTime.getDayEnd(thruDate);

Debug.log("daystart==================="+daystart);

Debug.log("dayend==================="+dayend);


condList = [];
//condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, "10372"));

if(UtilValidate.isNotEmpty(daystart)){
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, daystart));
	condList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO, dayend));
}

condList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));




cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

fieldsToSelect = ["invoiceId","invoiceDate","shipmentId","partyIdFrom"] as Set;

invoice = delegator.findList("Invoice", cond, fieldsToSelect, null, null, false);

//Debug.log("invoice========================="+invoice);


invoiceIds=EntityUtil.getFieldListFromEntityList(invoice, "invoiceId", true);

Debug.log("invoiceIds========================="+invoiceIds);

condList.clear();

if(UtilValidate.isNotEmpty(invoiceIds)){
	condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds));
}

billingcond = EntityCondition.makeCondition(condList, EntityOperator.AND);

fieldsToBilling = ["invoiceId","orderId","orderItemSeqId","invoiceItemSeqId"] as Set;

OrderItemBilling = delegator.findList("OrderItemBilling", billingcond, fieldsToBilling, null, null, false);

orderIdsFromBilling = EntityUtil.getFieldListFromEntityList(OrderItemBilling, "orderId", true);

//Debug.log("OrderItemBilling========================="+OrderItemBilling);


Debug.log("orderIdsFromBilling========================="+orderIdsFromBilling);

actualInvoiceIds = EntityUtil.getFieldListFromEntityList(OrderItemBilling, "invoiceId", true);

Debug.log("actualInvoiceIds========================="+actualInvoiceIds);


OrderItemDetail = delegator.findList("OrderItemDetail", EntityCondition.makeCondition("orderId", EntityOperator.IN,orderIdsFromBilling), null, null, null, false);

condList.clear();

condList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN, actualInvoiceIds));
condList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
invoiceItemcond = EntityCondition.makeCondition(condList, EntityOperator.AND);

InvoiceItem = delegator.findList("InvoiceItem", invoiceItemcond, null, null, null, false);

//Debug.log("InvoiceItem========================="+InvoiceItem);


orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.IN , orderIdsFromBilling)  , null, null, null, false );


OrderHeader = delegator.findList("OrderHeader",EntityCondition.makeCondition("orderId", EntityOperator.IN , orderIdsFromBilling)  , null, null, null, false );


salesAndPurchaseList = [];

if(invoice){

for (eachInvoice in invoice) {
	
	 
	invoiceItemList = EntityUtil.filterByCondition(InvoiceItem, EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoice.invoiceId));

	
	
	
	
	PartyGroup = delegator.findOne("PartyGroup",[partyId : eachInvoice.partyIdFrom] , false);
	
	groupName = "";
	if(PartyGroup)
	  groupName = PartyGroup.groupName;
	  
	  
	
	shipmentList = delegator.findOne("Shipment",[shipmentId : eachInvoice.shipmentId] , false);
	primaryOrderId = shipmentList.get("primaryOrderId");
	
	Debug.log("primaryOrderId============"+primaryOrderId);
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
	
	 Debug.log("partyId==================="+partyId);
	 
	 
	 Debug.log("supplier==================="+supplier);
	
	//=================Addresss======================
	
	contactMechesDetails = [];
	conditionListAddress = [];
	conditionListAddress.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionListAddress.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
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
		conditionAddress = EntityCondition.makeCondition(conditionListAddress,EntityOperator.AND);
		contactMechesDetails = delegator.findList("PartyContactDetailByPurpose", conditionAddress, null, null, null, false);
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
	
	Debug.log("partyPostalAddress===================="+partyPostalAddress);
	
	Debug.log("shipingAdd========================="+shipingAdd);
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
	
	
	
	
	//Debug.log("orderHeaderSequencesfilter================"+orderHeaderSequencesfilter);
	
	orderNo ="NA";
	if(UtilValidate.isNotEmpty(orderHeaderSequencesfilter)){
		orderSeqDetails = EntityUtil.getFirst(orderHeaderSequencesfilter);
		orderNo = orderSeqDetails.orderNo;
	}
	
	//=============indentDate============
	
	
	orderDate = "";
	if(actualOrderId){
	OrderHeader = delegator.findOne("OrderHeader",[orderId : actualOrderId] , false);
	orderDate = OrderHeader.orderDate;
	}
	
	
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
	
	double altaxAmt = 0;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderAdjustment = delegator.findList("OrderAdjustment", condExpr, null, null, null, false);

	if(OrderAdjustment){
	for (eachAdjment in OrderAdjustment) {
		
		if(eachAdjment.orderAdjustmentTypeId != "TEN_PERCENT_SUBSIDY"){
			altaxAmt = altaxAmt +Double.valueOf( eachAdjment.amount);
		}
	}
	}
	
//================================================================	
	
	Debug.log("invoiceItemList================="+invoiceItemList.size());
	
	double invoAmt = 0;
	for (eachItem in invoiceItemList) {
		
		   tempMap = [:];
		   
		   tempMap.put("invoiceId", eachItem.invoiceId);
		
		   tempMap.put("invoiceDate", eachInvoice.invoiceDate);
		   
		 //  invoAmt = invoAmt+(eachItem.amount*eachItem.quantity);
		   
		   tempMap.put("invoiceAmount", (eachItem.amount*eachItem.quantity));
		   
		   tempMap.put("at/OtherTax", altaxAmt);
		   
		 //  Debug.log("eachItem.invoiceId================="+eachItem.invoiceId);
		   
		   Debug.log("eachItem.invoiceItemSeqId================="+eachItem.invoiceItemSeqId);
		   
		   conditionList = [];
		   conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId));
		   conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachItem.invoiceItemSeqId));
		   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   OrderItemBilling = delegator.findList("OrderItemBilling", cond, null, null, null, false);
		   
		      //OrderItemBilling = EntityUtil.filterByCondition(OrderItemBilling, cond);
		   itemOrderId = "";
		   orderItemSeqId = "";
		   Debug.log("OrderItemBilling============="+OrderItemBilling);
		   if(OrderItemBilling[0]){			
		   itemOrderId  = OrderItemBilling[0].orderId;
		   orderItemSeqId  = OrderItemBilling[0].orderItemSeqId;
		   }
		 //  Debug.log("itemOrderId============="+itemOrderId);
		 //  Debug.log("orderItemSeqId============="+orderItemSeqId);
		   
		   
		   conditionList.clear();
		   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,itemOrderId));
		   conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
		   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   //OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
  
		   OrderItemDetail = EntityUtil.filterByCondition(OrderItemDetail, cond);
		   
		   Debug.log("OrderItemDetail============="+OrderItemDetail.size());
		   
		   
		   double quotaQuantity = 0;
		   
		   if(OrderItemDetail[0])
		   quotaQuantity = OrderItemDetail[0].quotaQuantity;
		   
		   double quantity = Double.valueOf(eachItem.quantity);
		   double tenPerQty = 0;
		   
		   Debug.log("quantity============="+quantity);
		   Debug.log("quotaQuantity============="+quotaQuantity);
		   
		   if(quantity > quotaQuantity)
		   {
			 tempMap.put("schemeQty", quotaQuantity);
			 tenPerQty = quotaQuantity;
		   }
		   else
		   {
			 tempMap.put("schemeQty", quantity);
			 tenPerQty = quantity;
		   }
		   
		   tempMap.put("subsidyAmt", tenPerQty*eachItem.amount);
		   
		  	   
		   
			tempMap.put("userAgency", partyName);
			
			tempMap.put("cluster", "");
			
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
			
			tempMap.put("indentDate", orderDate);
			
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
			
			Debug.log("poOrderId============="+poOrderId);
			Debug.log("poOrderItemSeqId============="+poOrderItemSeqId);
			
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
			conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, poOrderItemSeqId));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			POrderItemBilling = delegator.findList("OrderItemBilling", cond, null, null, null, false);
 
			poInvoiceId = "";
			poInvoiceItemSeqId = "";
			if(POrderItemBilling){
			poInvoiceId  = POrderItemBilling[0].invoiceId;
			poInvoiceItemSeqId  = POrderItemBilling[0].invoiceItemSeqId;
			}
			
			
			Debug.log("poInvoiceId============="+poInvoiceId);
			Debug.log("poInvoiceItemSeqId============="+poInvoiceItemSeqId);
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, poInvoiceId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, poOrderItemSeqId));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			POInvoiceItemList = delegator.findList("InvoiceItem", cond, null, null, null, false);
			
			
			popartyId = "";
			poinvoiceDate = "";
			shipmentId = "";
			
			if(poInvoiceId){
			poInvoiceList = delegator.findOne("Invoice",[invoiceId : poInvoiceId] , false);
			popartyId = poInvoiceList.get("partyId");
			poinvoiceDate = poInvoiceList.get("invoiceDate");
			shipmentId = poInvoiceList.get("shipmentId");
	        }
			
			Debug.log("popartyId============="+popartyId);
			Debug.log("poinvoiceDate============="+poinvoiceDate);
			Debug.log("shipmentId============="+shipmentId);
			
			
			if(POInvoiceItemList){
			tempMap.put("millInvoiceId", POInvoiceItemList[0].invoiceId);
			
			tempMap.put("millInvoiceDate", poinvoiceDate);
			
			tempMap.put("poInvoiceAmt", POInvoiceItemList[0].quantity*POInvoiceItemList[0].amount);
			
			tempMap.put("poInvoiceBasicAmt", POInvoiceItemList[0].quantity*POInvoiceItemList[0].amount);
			
			}
			
			PoorderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , poOrderId)  , UtilMisc.toSet("orderNo"), null, null, false );
			
			if(UtilValidate.isNotEmpty(PoorderHeaderSequences)){
				orderSeqDetails = EntityUtil.getFirst(PoorderHeaderSequences);
				draftPoNum = orderSeqDetails.orderNo;
				tempMap.put("poOrderId", draftPoNum);
			}else{
				tempMap.put("poOrderId", poOrderId);
			}

			orderDate = "";
			if(poOrderId){
			OrderHeader = delegator.findOne("OrderHeader",[orderId : poOrderId] , false);
			orderDate = OrderHeader.orderDate;
	        }
			
	        tempMap.put("poorderDate", orderDate);
			
			
			if(shipmentId){
			shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
			
			lrNumber = shipmentList.get("lrNumber");
			
			if(lrNumber)
			tempMap.put("lrNumber", lrNumber);
			else
			tempMap.put("lrNumber", "");
			
			deliveryChallanDate = shipmentList.get("deliveryChallanDate");
			
			if(deliveryChallanDate)
			tempMap.put("lrDate", deliveryChallanDate);
			else
			tempMap.put("lrDate", "");
			
			
			estimatedShipCost = shipmentList.get("estimatedShipCost");
						
			if(estimatedShipCost)
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
			
		   salesAndPurchaseList.add(tempMap);
		
	}
}
}

Debug.log("salesAndPurchaseList=================="+salesAndPurchaseList);

context.salesAndPurchaseList = salesAndPurchaseList;




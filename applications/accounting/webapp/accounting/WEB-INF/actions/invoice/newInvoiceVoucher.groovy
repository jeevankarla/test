
import java.util.*;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.MathContext;

import org.ofbiz.base.util.UtilNumber;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.party.party.PartyHelper;

import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.contact.ContactMechWorker;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.finder.EntityFinderUtil.ConditionList;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

invoiceId = parameters.invoiceId;

billOfSalesInvSeqs = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS , invoiceId)  , UtilMisc.toSet("invoiceSequence"), null, null, false );
if(UtilValidate.isNotEmpty(billOfSalesInvSeqs)){
	invoiceSeqDetails = EntityUtil.getFirst(billOfSalesInvSeqs);
	invoiceSequence = invoiceSeqDetails.invoiceSequence;
	context.invoiceId = invoiceSequence;
}else{
	context.invoiceId = invoiceId;
}

int cFormAgnst=0;

invoiceList = delegator.findOne("Invoice",[invoiceId : invoiceId] , false);

partyId = invoiceList.get("partyId");
partyIdFrom=invoiceList.get("costCenterId");
branchRo = delegator.findList("PartyRelationship",EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , partyIdFrom)  , UtilMisc.toSet("partyIdFrom"), null, null, false );
roID = EntityUtil.getFirst(branchRo);
context.partyIdFrom=partyIdFrom;
if(roID &&  (roID.partyIdFrom=="INT6" || roID.partyIdFrom=="INT3" || roID.partyIdFrom=="INT4")){
	kanAndKalRo="yes";
	context.kanAndKalRo=kanAndKalRo;
	tallySalesNo = invoiceList.get("referenceNumber");
	
	context.partyId = partyId;
	invoiceDate = invoiceList.get("invoiceDate");
	context.invoiceDate = invoiceDate;
	shipmentId = invoiceList.get("shipmentId");
	partyIdFrom = invoiceList.costCenterId;
	context.partyIdFrom = partyIdFrom;
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	FacilityList = delegator.findList("Facility", fcond, null, null, null, false);
	
	
	
	isDepot = "";
	if(FacilityList)
	isDepot ="Y"
	else
	isDepot ="N"
	
	
	context.isDepot = isDepot;
	
	passNo = "";
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
	if(PartyIdentificationList){
	passNo = PartyIdentificationList[0].get("idValue");
	}
	
	poNumber = "";
	orderId  = "";
	shipmentDate = "";
	lrNumber = "";
	supplierInvoiceId = "";
	supplierInvoiceDate = "";
	carrierName = "";
	estimatedShipCost = "";
	estimatedShipDate = "";
	destination = "";
	
	
	if(shipmentId){
	shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);
	
	
	orderId = shipmentList.get("primaryOrderId");
	
	estimatedShipDate = shipmentList.get("estimatedShipDate");
	
	
	deliveryChallanDate = "";
	
	
	if(shipmentList.get("deliveryChallanDate"))
	 deliveryChallanDate = shipmentList.get("deliveryChallanDate");
	
	 lrNumber = "";
	 if(shipmentList.get("lrNumber"))
	lrNumber = shipmentList.get("lrNumber");
	
	supplierInvoiceId ="";
	if(shipmentList.get("supplierInvoiceId"))
	supplierInvoiceId = shipmentList.get("supplierInvoiceId");
	
	carrierName = "";
	
	if(shipmentList.get("carrierName"))
	carrierName = shipmentList.get("carrierName");
	
	if(shipmentList.get("destination"))
	destination = shipmentList.get("destination");
	
	
	estimatedShipCost = shipmentList.get("estimatedShipCost");
	
	
	if(UtilValidate.isNotEmpty(shipmentList.get("supplierInvoiceDate"))){
	supplierInvoiceDate = shipmentList.get("supplierInvoiceDate");
	}
	}
	
	context.deliveryChallanDate = deliveryChallanDate;
	orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , UtilMisc.toSet("orderNo"), null, null, false );
	
	
	
	if(UtilValidate.isNotEmpty(orderHeaderSequences)){
		orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
		draftPoNum = orderSeqDetails.orderNo;
		context.poNumber = draftPoNum;
	}else{
		context.poNumber = orderId;
	}
	context.supplierInvoiceId = supplierInvoiceId;
	context.supplierInvoiceDate = supplierInvoiceDate;
	context.lrNumber = lrNumber;
	context.carrierName = carrierName;
	context.estimatedShipCost = estimatedShipCost;
	context.passNo = passNo;
	context.estimatedShipDate = estimatedShipDate;
	
	
	
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	invoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);
	
	invoiceItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
	invoiceAdjItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_FPROD_ITEM"));
	
	invoiceRemainigAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE","TEN_PERCENT_SUBSIDY")));
	
	
	
	invoiceItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["ADDITIONAL_CHARGES","DISCOUNTS"]), null, null, null, false);
	invoiceItemTypeIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemTypes, "invoiceItemTypeId", true);
	
	invoiceRemainigAdjItemListConsolidate = [];
	
	for (eachType in invoiceItemTypes) {
		
		invoiceRemainigAdj = EntityUtil.filterByCondition(invoiceRemainigAdjItemList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, eachType.invoiceItemTypeId));
		
		if(invoiceRemainigAdj){
			
			adjType = invoiceRemainigAdj[0].invoiceItemTypeId;
			
			totAmt = 0;
			tempMap = [:];
			for (eachAdj in invoiceRemainigAdj) {
				totAmt = totAmt+eachAdj.itemValue;
			}
			
			
			tempMap.put("invoiceItemTypeId", adjType);
			if(eachType.description)
			tempMap.put("description", eachType.description);
			tempMap.put("itemValue", totAmt);
			
			invoiceRemainigAdjItemListConsolidate.add(tempMap);
		}
		
		
		
	}
	
	
	
	
	
	
	
	invoiceItemLevelAdjustments = [:];
	invoiceItemLevelUnitListPrice = [:];
	
	double totTaxAmount = 0;
	double totTaxAmount2 = 0;
	double mgpsAmt = 0;
	
	
	int i=0;
	for (eachList in invoiceItemList) {
		
		 
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachList.invoiceId));
		conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachList.invoiceItemSeqId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
		
		// Debug.log("invoiceAdjItemList====+"+i+"======="+invoiceAdjItemList);
		
		
		 itemAdjustList = [];
		 unitPriceIncTax=0;
		 if(invoiceInnerAdjItemList){
		 for (eachItem in invoiceInnerAdjItemList) {
			
			 
			   if(eachItem.invoiceItemTypeId=="CST_SALE" || eachItem.invoiceItemTypeId=="VAT_SALE"||eachItem.invoiceItemTypeId=="CST_SURCHARGE" || eachItem.invoiceItemTypeId=="VAT_SURCHARGE"){
			  tempMap = [:];
			  
			  invoiceForPercentage = delegator.findOne("Invoice",[invoiceId : eachItem.invoiceId] , false);
			  
			  tempMap.put("invoiceId", eachItem.invoiceId);
			  tempMap.put("invoiceItemTypeId", eachItem.invoiceItemTypeId);
			 
				  
				  itemValue = eachItem.itemValue;
				  
				  invoiceItemTypes = delegator.findOne("InvoiceItemType",[invoiceItemTypeId : eachItem.invoiceItemTypeId] , false);
				 
				  orderItemBillings = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId), null, null, null, false);
				  orderItemBillings = EntityUtil.getFirst(orderItemBillings);
				  conditionList.clear();
				  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderItemBillings.orderId));
				  conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, eachItem.invoiceItemTypeId));
				  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				  orderAdjustments = delegator.findList("OrderAdjustment", cond, null, null, null, false);
				  orderAdjustments = EntityUtil.getFirst(orderAdjustments);
				  
				    
				  if(eachItem.sourcePercentage){
				  tempMap.put("percentage", eachItem.sourcePercentage);
				  }else{
				  invoiceGrandTotal = invoiceForPercentage.invoiceGrandTotal;
				  
				  if(itemValue != 0){
				  percentage = invoiceGrandTotal/itemValue;
				   if(eachItem.invoiceItemTypeId == "ENTRY_TAX")
					 percentage = 1;
				  }else{
					  percentage = 0;
				  }
			
				  tempMap.put("percentage", percentage);
				  }
				  
				   if(eachItem.description)
				   tempMap.put("description", eachItem.description);
				   else
				   tempMap.put("description", invoiceItemTypes.description);
				   
			  tempMap.put("quantity", eachItem.quantity);
			  tempMap.put("amount", eachItem.amount);
			  tempMap.put("itemValue", eachItem.itemValue);
			  
			  totTaxAmount = totTaxAmount+(eachItem.itemValue);
			 // totTaxAmount2 = totTaxAmount2+(eachItem.itemValue);
			  itemAdjustList.add(tempMap);
			  }
			  if(eachItem.invoiceItemTypeId == "TEN_PERCENT_SUBSIDY"){
			  mgpsAmt = mgpsAmt+eachItem.itemValue;
			  }
			  
			  if(eachItem.invoiceItemTypeId !="VAT_SALE" && eachItem.invoiceItemTypeId !="TEN_PERCENT_SUBSIDY" && eachItem.invoiceItemTypeId !="INV_FPROD_ITEM"){ 
				  unitPriceIncTax=unitPriceIncTax+(eachItem.amount/eachList.quantity);
			  }
			  if(eachItem.invoiceItemTypeId=="VAT_SALE" || eachItem.invoiceItemTypeId=="VAT_SURCHARGE"){
				  totTaxAmount2=totTaxAmount2+(eachItem.itemValue);
			  }
			  if(eachItem.invoiceItemTypeId=="CST_SALE"){
			  	cFormAgnst=cFormAgnst+1;
	 		  }
		}
		 }
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachList.invoiceId));
		 conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"INVOICE_ITM_ADJ"));
		 cond11 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 invoiceInnerAdjItemList2 = EntityUtil.filterByCondition(invoiceAdjItemList, cond11);
		 invoiceInnerAdjItem2 = EntityUtil.getFirst(invoiceInnerAdjItemList2);
		 if(UtilValidate.isNotEmpty(invoiceInnerAdjItem2)){
			 //unitPriceIncTax=unitPriceIncTax+(invoiceInnerAdjItem2.amount/eachList.quantity);
			 //totTaxAmount2=totTaxAmount2+(invoiceInnerAdjItem2.amount);
		 }
		 invoiceItemLevelUnitListPrice.put(eachList.productId, unitPriceIncTax);
		 
		if(itemAdjustList){
			invoiceItemLevelAdjustments.put(i, itemAdjustList);
		}else{
			invoiceItemLevelAdjustments.put(i,"NoAdjustments");
		}
		i++;
		
	}
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
	conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INVOICE_ITM_ADJ"));
	conditionList.add(EntityCondition.makeCondition("description", EntityOperator.LIKE, "Service%"));
	cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	invoiceAdjustments2 = delegator.findList("InvoiceItem", cond1, null, null, null, false);
	invoiceAdjustments2 = EntityUtil.getFirst(invoiceAdjustments2);
	if(invoiceAdjustments2){
		serchar=invoiceAdjustments2.amount;
		//serviceChargePer=orderAdjustments2.sourcePercentage;
		context.serchar=serchar;
		context.serviceChargePer="2";
	}
	context.invoiceItemLevelUnitListPrice=invoiceItemLevelUnitListPrice;
	
	context.invoiceItemLevelAdjustments = invoiceItemLevelAdjustments;
	
	context.invoiceRemainigAdjItemList = invoiceRemainigAdjItemListConsolidate;
	
	context.totTaxAmount = totTaxAmount;
	context.totTaxAmount2 = totTaxAmount2;
	context.mgpsAmt = mgpsAmt;
	
	context.mgpsAmtPositive = Math.abs(mgpsAmt);
	
	
	orderAttrForPo = [];
	if(orderId){
	orderAttrForPo = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
	OrderHeaderList = delegator.findOne("OrderHeader",[orderId : orderId] , false);
	
	tallyRefNo = OrderHeaderList.get("tallyRefNo");
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	shipmentListForPOInvoiceId = delegator.findList("Invoice", cond, null, null, null, false);
	
	purInvoiceId = "";
	double purInvoiceTOt = 0;
	
	if(shipmentListForPOInvoiceId){
	 purInvoiceId = shipmentListForPOInvoiceId[0].invoiceId;
	
	 
	 conditionList.clear();
	 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, purInvoiceId));
	 //conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
	 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	 purChaseInvoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);
	 
	 for (eachItem in purChaseInvoiceItemLists) {
	
		 purInvoiceTOt = purInvoiceTOt+(eachItem.itemValue);
	}
	}
	
	context.purInvoiceTOt = purInvoiceTOt;
	
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	PurchaseOrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
	*/
	
	if(purInvoiceId){
		purInvoiceList = delegator.findOne("Invoice",[invoiceId : purInvoiceId] , false);
		if(purInvoiceList.referenceNumber)
		tallyRefNo = purInvoiceList.referenceNumber;
	}
	
	
	if(tallySalesNo)
	tallyRefNo = tallySalesNo;
	
	
	/*if(invoiceId == "10360")
	 tallyRefNo= "YRPR/3/16-17";
	else if(invoiceId == "10358")
	 tallyRefNo= "YRPR/2/16-17";
	else if(invoiceId == "10362")
	 tallyRefNo= "YRPR/6/16-17";
	else if(invoiceId == "10391")
	tallyRefNo= "YRPR/5/16-17";
	else if(invoiceId == "10390")
	tallyRefNo= "YRPR/4/16-17";
	*/
	context.tallyRefNo = tallyRefNo;
	allDetailsMap = [:];
	productStoreId = OrderHeaderList.get("productStoreId");
	branchId="";
	if (productStoreId) {
		productStore = delegator.findByPrimaryKey("ProductStore", [productStoreId : productStoreId]);
		branchId=productStore.payToPartyId;
		
	}
	partyIdentification = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , branchId)  , null, null, null, false );
	if(UtilValidate.isNotEmpty(partyIdentification)){
		tinNumber="";
		tinDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
		if(UtilValidate.isNotEmpty(tinDetails)){
			tinDetails=EntityUtil.getFirst(tinDetails);
			tinNumber=tinDetails.idValue;
			allDetailsMap.put("tinNumber",tinNumber);
		}
		cstNumber="";
		cstDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
		if(UtilValidate.isNotEmpty(cstDetails)){
			cstDetails=EntityUtil.getFirst(cstDetails);
			cstNumber=cstDetails.idValue;
			allDetailsMap.put("cstNumber",cstNumber);
		}
		cinNumber="";
		cinDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CIN_NUMBER"));
		if(UtilValidate.isNotEmpty(cinDetails)){
			cinDetails=EntityUtil.getFirst(cinDetails);
			cinNumber=cinDetails.idValue;
			allDetailsMap.put("cinNumber",cinNumber);
		}
		panNumber="";
		panDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PAN_NUMBER"));
		if(UtilValidate.isNotEmpty(panDetails)){
			panDetails=EntityUtil.getFirst(panDetails);
			panNumber=panDetails.idValue;
			allDetailsMap.put("panNumber",panNumber);
		}
	}
	context.allDetailsMap= allDetailsMap;
	//get Report Header
	branchContext=[:];
	
	if(branchId == "INT12" || branchId == "INT49" || branchId == "INT55")
	branchId = "INT12";
	else if(branchId == "INT8" || branchId == "INT16" || branchId == "INT20" || branchId == "INT21" || branchId == "INT22" || branchId == "INT44")
	branchId = "INT8";
	
	
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
	}
	
	grandTotal = OrderHeaderList.get("grandTotal");
	
	context.grandTotal = grandTotal;
	
	   exprCondList=[];
	   exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	   exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
	   EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
	   OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
	   
	   actualOrderId = "";
	   if(OrderAss){
		   
		   actualOrderId=OrderAss.toOrderId;
		   
	   }
	
	if(UtilValidate.isNotEmpty(orderAttrForPo)){
		
		orderAttrForPo.each{ eachAttr ->
		/*	if(eachAttr.attrName == "PO_NUMBER"){
				actualOrderId =  eachAttr.attrValue;
			}*/
			if(eachAttr.attrName == "DST_ADDR"){
				
				if(!destination)
				destination =  eachAttr.attrValue;
				
				/*if(districtGeoId){
					districtName = districtGeoId ;
					GenericValue geo = delegator.findOne("Geo", [geoId:districtGeoId], false);
					if(UtilValidate.isNotEmpty(geo)&& UtilValidate.isNotEmpty(geo.get("geoName"))){
						destination = geo.get("geoName");
						}
				}*/
				
			}
		}
	}
	
	
	indentOrderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , actualOrderId)  , UtilMisc.toSet("orderNo"), null, null, false );
	if(UtilValidate.isNotEmpty(indentOrderSequences)){
		indentOrderSeqDetails = EntityUtil.getFirst(indentOrderSequences);
		salesOrder = indentOrderSeqDetails.orderNo;
		context.indentNo = salesOrder;
	}else{
		context.indentNo = actualOrderId;
	}
	orderHeaderSequences = delegator.findList("OrderHeaderSequence", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
	if(UtilValidate.isNotEmpty(orderHeaderSequences)){
		orderSquences = EntityUtil.getFirst(orderHeaderSequences);
		orderNo= orderSquences.orderNo;
		context.indentNo = orderNo;
	}
	
	
	
	OrderHeader = [];
	OrderRoleList = [];
	indentDate = "";
	soceity = "";
	poDate = "";
	scheme = "";
	supplier = "";
	onbehalf = "";
	finalDetails = [];
	if(actualOrderId){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, [orderId,actualOrderId]));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderHeader = delegator.findList("OrderHeader", cond, UtilMisc.toSet("orderId","orderDate","externalId"), null, null, false);
	
	indentDetails = EntityUtil.filterByCondition(OrderHeader, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	indentDate = indentDetails[0].get("orderDate");
	
	//externalOrderId = indentDetails[0].get("externalId");
	PODetails = EntityUtil.filterByCondition(OrderHeader, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	poDate = PODetails[0].get("orderDate");
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
	expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);
	
	
		orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
		
		if(UtilValidate.isNotEmpty(orderAttr)){
			orderAttr.each{ eachAttr ->
				if(eachAttr.attrName == "SCHEME_CAT"){
					scheme =  eachAttr.attrValue;
				}
			}
		   }
	
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemAttributeList = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
		C2E2Form = "";
		
		if(OrderItemAttributeList){
			for (eachAttr in OrderItemAttributeList) {
				if(eachAttr.attrName == "checkCForm"){
					C2E2Form = eachAttr.attrValue;
					break;
				}
				else if(eachAttr.attrName == "checkE2Form"){
					C2E2Form = eachAttr.attrValue;
					break;
				}
			}
		}
		
		context.C2E2Form = C2E2Form;
		
		}
	
	onbehalf = "";
	
	for (eachRole in OrderRoleList) {
		
		if(eachRole.roleTypeId == "SUPPLIER")
		 supplier = eachRole.get("partyId");
		if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
		 soceity = eachRole.get("partyId");
		if(eachRole.roleTypeId == "ON_BEHALF_OF")
			 onbehalf = true;
	}
	
	
	
	context.poDate = poDate;
	context.indentDate = indentDate;
	context.scheme = scheme;
	context.supplier = supplier;
	context.destination = destination;
	context.soceity = soceity;
	context.onbehalf = onbehalf;
	//context.externalOrderId = externalOrderId;
	
	
	
	//if(onbehalf == true){
	  // forOnbeHalf()
	//}else{
	  // context.reportTypeFlag = "DIRECT";
		
		SchemeQtyMap = [:];
		SchemeAmtMap = [:];
		
		double schemeDeductionAmt = 0;
		
		/*for (eachInvoiceList in invoiceAdjItemList) {
			if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.quantity))
			{
			SchemeQtyMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.quantity);
			}
			if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.amount))
			{
			SchemeAmtMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.amount);
			schemeDeductionAmt = schemeDeductionAmt+Math.abs(eachInvoiceList.amount);
			}
			
		}*/
		
		
		context.schemeDeductionAmt = Math.round(schemeDeductionAmt);
		
		
		double grandTotal = 0;
		if(invoiceItemList){
		
			
			List productIds = EntityUtil.getFieldListFromEntityList(invoiceItemList, "productId", true);
			
			
		/*	for(eachProd in productIds)
			{*/
	
				//eachInvoiceItemList = EntityUtil.filterByCondition(invoiceItemList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd));
				
				
				double schemeAmt = 0;
				double quantity = 0;
				double amount = 0;
				
				
			for (eachInvoiceList in invoiceItemList) {
		   
				OrderItemDetail = [];
				
				tempMap = [:];
			 tempMap.put("productId", eachInvoiceList.productId);
			 tempMap.put("prodDescription", eachInvoiceList.description);
			 tempMap.put("rateKg", eachInvoiceList.unitPrice);
			 
			// tempMap.put("amount", eachInvoiceList.amount);
			 /*String seq = String.format("%05d", i);
			 
			 
			 OrderItemAttribute = EntityUtil.filterByCondition(OrderItemAttributeList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seq));
			 
			  baleQty = "";
			  if(OrderItemAttribute){
			  OrderItemAttribute.each{ eachAttr ->
				 if(eachAttr.attrName == "BALE_QTY"){
					 baleQty =  eachAttr.attrValue;
				 }
			  }
			}*/
			 
			 
			// String seq = String.format("%05d", i);
			 
			 
			   conditionList.clear();
			   conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
			   conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachInvoiceList.invoiceItemSeqId));
			   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
			  
			
					  
			 itemOrderId  = OrderItemBilling[0].orderId;
			 orderItemSeqId  = OrderItemBilling[0].orderItemSeqId;
			 
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,itemOrderId));
			 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
			 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			 OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
		
			 
			 remarks = "";
			 if(OrderItemDetail)
			 remarks = OrderItemDetail[0].get("remarks");
			 
		/*	 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
			 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seq));
			 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			 OrderItemAttributeList1 = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
			 baleQty="";
			 unit="";
			 if(OrderItemAttributeList1){
			 OrderItemAttributeList1.each{ eachAttr ->
				if(eachAttr.attrName == "quotaQty"){
					schemeAmt =  schemeAmt+Double.valueOf(eachAttr.attrValue);
				}
				if(eachAttr.attrName == "BALE_QTY"){
					baleQty =  eachAttr.attrValue;
				}
				if(eachAttr.attrName == "YARN_UOM"){
					unit =  eachAttr.attrValue;
				}
			 }
		   }*/
			 
			 
			 quantity = eachInvoiceList.quantity;
			 amount = eachInvoiceList.amount;
			
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
			 
			 tempMap.put("unit", unit);
			 
			 tempMap.put("remarks", remarks);
			 
			 
			 
			 
				if(baleQty)
				tempMap.put("baleQty",baleQty);
				else
				tempMap.put("baleQty","");
				
				
				tempMap.put("quantity", quantity);
			
			//String schemeAmt = (String)SchemeQtyMap.get(eachInvoiceList.invoiceItemSeqId);
				
				
				//==============scheme Quantity===============
				
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
				conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceList.invoiceItemSeqId));
				conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
				cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
		   
				double schemeQQQty = 0;
				if(invoiceInnerAdjItemList){
					
					 invoiceIdAdj = invoiceInnerAdjItemList[0].invoiceId;
					 invoiceItemSeqIdAdj = invoiceInnerAdjItemList[0].invoiceItemSeqId;
					 
					 
					 conditionList.clear();
					 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceIdAdj));
					 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdAdj));
					 conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
					 cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					 OrderAdjustmentAndBilling = delegator.findList("OrderAdjustmentAndBilling", cond1, null, null, null, false);
		 
					 if(OrderAdjustmentAndBilling[0])
					 schemeQQQty = OrderAdjustmentAndBilling[0].quantity;
					
				}
				
				
				double tenPerQty = 0;
				tenPerQty = schemeQQQty;
				tempMap.put("schemeQty", schemeQQQty);
				
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
				
			  
			 double serviceAmt = 0;
			 double sourcePercentage = 0;
			 
			  if(scheme == "General"){
				  
				  conditionList.clear();
				  conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
				  conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceList.invoiceItemSeqId));
				  conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
				  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				  invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
				  
				  if(invoiceInnerAdjItemList){
				  serviceAmt = serviceAmt+invoiceInnerAdjItemList[0].itemValue;
				  //sourcePercentage = sourcePercentage+invoiceInnerAdjItemList[0].sourcePercentage;
				  
				  }
			  }
			  
			  
			  /*if(scheme == "General"){
				  
				  sourcePercentage = (serviceAmt/(eachInvoiceList.itemValue))*100;
				  double perAmt = (eachInvoiceList.amount*sourcePercentage)/100;
				  
				  tempMap.put("amount",(eachInvoiceList.amount+perAmt));
				  }else{*/
				  tempMap.put("amount", eachInvoiceList.amount);
			  // }
				  Debug.log("invoiceItemLevelUnitListPrice.get(eachInvoiceList.productId)========="+ invoiceItemLevelUnitListPrice.get(eachInvoiceList.productId))
				  tempMap.put("unitPriceIncTax", invoiceItemLevelUnitListPrice.get(eachInvoiceList.productId)+tempMap.get("amount"));
			 // tempMap.put("ToTamount", (quantity*amount)+serviceAmt);
				  
				  tempMap.put("ToTamount", Math.round(tempMap.get("quantity")*tempMap.get("unitPriceIncTax")));
			 
				   grandTotal = grandTotal+(eachInvoiceList.itemValue)+serviceAmt;
			  
			 /* double mgpsQty = 0;
			  if(quantity > schemeAmt)
				mgpsQty = schemeAmt;
			  else
				mgpsQty = quantity;
				tempMap.put("mgpsQty", mgpsQty);*/
			  
			  
			  
			finalDetails.add(tempMap);
			 }
			//}
		 }
		
		context.grandTotal = grandTotal;
		context.finalDetails = finalDetails;
	
			
	//}
	
	//==============Address Details===================
	
	finalAddresList=[];
	address1="";
	address2="";
	city="";
	postalCode="";
	panId="";
	tanId="";
	
	partyPostalAddress = dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);
	
	
	if(partyPostalAddress){
		
	
	   if(partyPostalAddress.address1){
		   address1=partyPostalAddress.address1;
	  
	   tempMap=[:];
	   tempMap.put("key1","Road / Street / Lane");
	   tempMap.put("key2",address1);
	   finalAddresList.add(tempMap);
	   if(partyPostalAddress.address2){
		   address2=partyPostalAddress.address2;
	   }
	   tempMap=[:];
	   tempMap.put("key1","Area / Locality");
	   tempMap.put("key2",address2);
	   finalAddresList.add(tempMap);
	   if(partyPostalAddress.city){
		   
		   city=partyPostalAddress.city;
	   }
	   tempMap=[:];
	   tempMap.put("key1","Town / District / City");
	   tempMap.put("key2",city);
	   finalAddresList.add(tempMap);
	   
	   if(partyPostalAddress.postalCode){
		   postalCode=partyPostalAddress.postalCode;
	   }
	   tempMap=[:];
	   tempMap.put("key1","PIN Code");
	   tempMap.put("key2",postalCode);
	   finalAddresList.add(tempMap);
	   
		}else{
			contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
			partyPostalAddress = contactMench.postalAddress;
			
			if(partyPostalAddress[0]){
				address1=partyPostalAddress[0].address1;
			
			tempMap=[:];
			tempMap.put("key1","Road / Street / Lane");
			tempMap.put("key2",address1);
			finalAddresList.add(tempMap);
			if(partyPostalAddress[0].address2){
				address2=partyPostalAddress[0].address2;
			}
			tempMap=[:];
			tempMap.put("key1","Area / Locality");
			tempMap.put("key2",address2);
			finalAddresList.add(tempMap);
			if(partyPostalAddress[0].city){
				city=partyPostalAddress[0].city;
			}
			tempMap=[:];
			tempMap.put("key1","Town / District / City");
			tempMap.put("key2",city);
			finalAddresList.add(tempMap);
			
			if(partyPostalAddress[0].postalCode){
				postalCode=partyPostalAddress[0].postalCode;
			}
			tempMap=[:];
			tempMap.put("key1","PIN Code");
			tempMap.put("key2",postalCode);
			finalAddresList.add(tempMap);
			}
		}
	   
	}else{
			contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
			partyPostalAddress = contactMench.postalAddress;
			
			if(partyPostalAddress){
				address1=partyPostalAddress[0].address1;
			
			tempMap=[:];
			tempMap.put("key1","Road / Street / Lane");
			tempMap.put("key2",address1);
			finalAddresList.add(tempMap);
			if(partyPostalAddress[0].address2){
				address2=partyPostalAddress[0].address2;
			}
			tempMap=[:];
			tempMap.put("key1","Area / Locality");
			tempMap.put("key2",address2);
			finalAddresList.add(tempMap);
			if(partyPostalAddress[0].city){
				city=partyPostalAddress[0].city;
			}
			tempMap=[:];
			tempMap.put("key1","Town / District / City");
			tempMap.put("key2",city);
			finalAddresList.add(tempMap);
			
			if(partyPostalAddress[0].postalCode){
				postalCode=partyPostalAddress[0].postalCode;
			}
			tempMap=[:];
			tempMap.put("key1","PIN Code");
			tempMap.put("key2",postalCode);
			finalAddresList.add(tempMap);
			}
	}
	
	context.finalAddresList = finalAddresList;
	//============================================================
	
	//def forOnbeHalf(){
		OrderItemAttributeList = [];
		finaOnbehalflDetails = [];
		
		//context.reportTypeFlag = "ONBEHALF";
		
		/*conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemList = delegator.findList("OrderItem", cond, null, null, null, false);
	*/
		if(actualOrderId){
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
			
			
			for (eachOrderItemList in OrderItemDetail) {
				
				//OrderItemAttribute = EntityUtil.filterByCondition(OrderItemAttributeList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
				
				partyId = "";
				productId = "";
				itemDescription = "";
				passNo = "";
				quantity = 0;
				unitPrice = 0;
				quotaQty = 0;
				amount = 0;
				tempMap = [:];
				
				//Debug.log("OrderItemAttribute============"+OrderItemAttribute);
				
		/*		if(UtilValidate.isNotEmpty(OrderItemAttribute)){
					OrderItemAttribute.each{ eachAttr ->
						if(eachAttr.attrName == "WIEVER_CUSTOMER"){
							partyId =  eachAttr.attrValue;
							conditionList.clear();
							conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
							conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
							cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
							PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
							if(PartyIdentificationList){
							passNo = PartyIdentificationList[0].get("idValue");
							}
						}
						if(eachAttr.attrName == "quotaQty"){
							quotaQty =  eachAttr.attrValue;
							
						}
					}
				   }*/
				
				
					partyId = eachOrderItemList.get("partyId");
				 
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
					cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
					if(PartyIdentificationList){
					passNo = PartyIdentificationList[0].get("idValue");
					}
				   
				   tempMap.put("partyId", partyId);
				   
				   tempMap.put("passNo", passNo);
				   
				//eachOrderItemList = EntityUtil.filterByCondition(OrderItemList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
				
				if(eachOrderItemList){
					
					//itemDescription = eachOrderItemList[0].get("itemDescription");
					tempMap.put("itemDescription", "");
					quantity = eachOrderItemList.get("quantity");
					tempMap.put("quantity", quantity);
					unitPrice = eachOrderItemList.get("unitPrice");
					
					quotaQty = eachOrderItemList.get("quotaQuantity");
					
					tempMap.put("quotaQty", Math.round(((quotaQty*unitPrice)*10)/100));
					amount = quantity*unitPrice;
					tempMap.put("amount", amount);
				}
				finaOnbehalflDetails.add(tempMap);
			}
			
		}
		context.finaOnbehalflDetails = finaOnbehalflDetails;
		
	//}
		invoiceAttribute = delegator.findOne("InvoiceAttribute",[invoiceId : invoiceId, attrName :"saleTitleTransferEnumId"] , false);
		if(invoiceAttribute){
			attrValue=invoiceAttribute.attrValue;
			if("CST_CFORM".equals(attrValue)){
				cFormAgnst=cFormAgnst+1;
			}
		}
		context.cFormAgnst=cFormAgnst;
}else{

tallySalesNo = invoiceList.get("referenceNumber");
context.partyId = partyId;
invoiceDate = invoiceList.get("invoiceDate");
context.invoiceDate = invoiceDate;
shipmentId = invoiceList.get("shipmentId");
partyIdFrom = invoiceList.costCenterId;
context.partyIdFrom = partyIdFrom;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "DEPOT_SOCIETY"));
fcond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

FacilityList = delegator.findList("Facility", fcond, null, null, null, false);


isDepot = "";
if(FacilityList)
isDepot ="Y"
else
isDepot ="N"


context.isDepot = isDepot;

passNo = "";
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
if(PartyIdentificationList){
passNo = PartyIdentificationList[0].get("idValue");
}

poNumber = "";
orderId  = "";
shipmentDate = "";
lrNumber = "";
supplierInvoiceId = "";
supplierInvoiceDate = "";
carrierName = "";
estimatedShipCost = "";
estimatedShipDate = "";
destination = "";

if(shipmentId){
shipmentList = delegator.findOne("Shipment",[shipmentId : shipmentId] , false);


orderId = shipmentList.get("primaryOrderId");

estimatedShipDate = shipmentList.get("estimatedShipDate");


deliveryChallanDate = "";


if(shipmentList.get("deliveryChallanDate"))
 deliveryChallanDate = shipmentList.get("deliveryChallanDate");

 lrNumber = "";
 if(shipmentList.get("lrNumber"))
lrNumber = shipmentList.get("lrNumber");

supplierInvoiceId ="";
if(shipmentList.get("supplierInvoiceId"))
supplierInvoiceId = shipmentList.get("supplierInvoiceId");

carrierName = "";

if(shipmentList.get("carrierName"))
carrierName = shipmentList.get("carrierName");

if(shipmentList.get("destination"))
destination = shipmentList.get("destination");


estimatedShipCost = shipmentList.get("estimatedShipCost");

if(UtilValidate.isNotEmpty(shipmentList.get("supplierInvoiceDate"))){
supplierInvoiceDate = shipmentList.get("supplierInvoiceDate");
}
context.deliveryChallanDate = deliveryChallanDate;
}


orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , orderId)  , UtilMisc.toSet("orderNo"), null, null, false );



if(UtilValidate.isNotEmpty(orderHeaderSequences)){
	orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
	draftPoNum = orderSeqDetails.orderNo;
	context.poNumber = draftPoNum;
}else{
	context.poNumber = orderId;
}
context.supplierInvoiceId = supplierInvoiceId;
context.supplierInvoiceDate = supplierInvoiceDate;
context.lrNumber = lrNumber;
context.carrierName = carrierName;
context.estimatedShipCost = estimatedShipCost;
context.passNo = passNo;
context.estimatedShipDate = estimatedShipDate;


conditionList = [];
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);

invoiceItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
invoiceAdjItemList = EntityUtil.filterByCondition(invoiceItemLists, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_FPROD_ITEM"));

invoiceRemainigAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,UtilMisc.toList("VAT_SALE","CST_SALE","CST_SURCHARGE","VAT_SURCHARGE","TEN_PERCENT_SUBSIDY")));
invoiceItemLevelAdjustments = [:];
adjmntsTot=0;
for(invoiceRemainigAdjItem in invoiceRemainigAdjItemList)
{
	adjmntsTot=adjmntsTot+invoiceRemainigAdjItem.itemValue;
}

double totTaxAmount = 0;
double mgpsAmt = 0;


int i=0;

for (eachList in invoiceItemList) {
	
	 
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachList.invoiceId));
	conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachList.invoiceItemSeqId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
	
	// Debug.log("invoiceAdjItemList====+"+i+"======="+invoiceAdjItemList);
	
	
	 itemAdjustList = [];
	 
	 if(invoiceInnerAdjItemList){
	 for (eachItem in invoiceInnerAdjItemList) {
		
		 
		   if(eachItem.invoiceItemTypeId=="CST_SALE" || eachItem.invoiceItemTypeId=="VAT_SALE"||eachItem.invoiceItemTypeId=="CST_SURCHARGE" || eachItem.invoiceItemTypeId=="VAT_SURCHARGE"){
		  tempMap = [:];
		  
		  invoiceForPercentage = delegator.findOne("Invoice",[invoiceId : eachItem.invoiceId] , false);
		  
		  tempMap.put("invoiceId", eachItem.invoiceId);
		  tempMap.put("invoiceItemTypeId", eachItem.invoiceItemTypeId);
		 
			  
			  itemValue = eachItem.itemValue;
			  
			  invoiceItemTypes = delegator.findOne("InvoiceItemType",[invoiceItemTypeId : eachItem.invoiceItemTypeId] , false);
			 
			  orderItemBillings = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachItem.invoiceId), null, null, null, false);
			  orderItemBillings = EntityUtil.getFirst(orderItemBillings);
			  conditionList.clear();
			  conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderItemBillings.orderId));
			  conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, eachItem.invoiceItemTypeId));
			  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			  orderAdjustments = delegator.findList("OrderAdjustment", cond, null, null, null, false);
			  orderAdjustments = EntityUtil.getFirst(orderAdjustments);
			  
			  if(eachItem.sourcePercentage){
			  tempMap.put("percentage", eachItem.sourcePercentage);
			  }else{
			  invoiceGrandTotal = invoiceForPercentage.invoiceGrandTotal;
			  
			  if(itemValue != 0){
			  percentage = invoiceGrandTotal/itemValue;
			   if(eachItem.invoiceItemTypeId == "ENTRY_TAX")
				 percentage = 1;
			  }else{
			  percentage = 0;
			  }
			  
			  tempMap.put("percentage", 0);
			  }
			  
			   if(eachItem.description)
			   tempMap.put("description", eachItem.description);
			   else
			   tempMap.put("description", invoiceItemTypes.description);
			   
		  tempMap.put("quantity", eachItem.quantity);
		  tempMap.put("amount", eachItem.amount);
		  tempMap.put("itemValue", eachItem.itemValue);
		  totTaxAmount = totTaxAmount+(eachItem.itemValue);
		  
		  itemAdjustList.add(tempMap);
		  }
		  if(eachItem.invoiceItemTypeId == "TEN_PERCENT_SUBSIDY"){
			  
		  if(eachItem.itemValue){
		  mgpsAmt = mgpsAmt+eachItem.itemValue;
		  }
		  }
		  if(eachItem.invoiceItemTypeId=="CST_SALE"){
			  cFormAgnst=cFormAgnst+1;
		   }
	}
	 }
	 
	if(itemAdjustList){
		invoiceItemLevelAdjustments.put(i, itemAdjustList);
	}else{
		invoiceItemLevelAdjustments.put(i,"NoAdjustments");
	}
	i++;
	
	
}
if(roID && roID.partyIdFrom=="INT26"){
	bhuneshwarRo="Yes";
	context.bhuneshwarRo=bhuneshwarRo;
	context.adjmntsTot=adjmntsTot;
}
context.invoiceItemLevelAdjustments = invoiceItemLevelAdjustments;


invoiceItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, ["ADDITIONAL_CHARGES","DISCOUNTS"]), null, null, null, false);
invoiceItemTypeIdsList = EntityUtil.getFieldListFromEntityList(invoiceItemTypes, "invoiceItemTypeId", true);


invoiceRemainigAdjItemListConsolidate = [];
       
for (eachType in invoiceItemTypes) {
	
	invoiceRemainigAdj = EntityUtil.filterByCondition(invoiceRemainigAdjItemList, EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, eachType.invoiceItemTypeId));
	
	if(invoiceRemainigAdj){
		
		adjType = invoiceRemainigAdj[0].invoiceItemTypeId;
		
		totAmt = 0;
		tempMap = [:];
		for (eachAdj in invoiceRemainigAdj) {
			totAmt = totAmt+eachAdj.itemValue;
		}
		
		
		tempMap.put("invoiceItemTypeId", adjType);
		if(eachType.description)
		tempMap.put("description", eachType.description);
		tempMap.put("itemValue", totAmt);
		
		invoiceRemainigAdjItemListConsolidate.add(tempMap);
	}
	
	
	
}
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INVOICE_ITM_ADJ"));
conditionList.add(EntityCondition.makeCondition("description", EntityOperator.LIKE, "Service%"));
cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoiceAdjusService = delegator.findList("InvoiceItem", cond1, null, null, null, false);

tempMapSer = [:];
totAmtse = 0;

if(invoiceAdjusService){
	for (eachType in invoiceAdjusService) {		
			//adjType = eachType.invoiceItemTypeId;
			totAmtse = totAmtse+eachType.itemValue;
	}
	tempMapSer.put("invoiceItemTypeId", "INVOICE_ITM_ADJ");
	tempMapSer.put("description","Service Charge");
	tempMapSer.put("itemValue", totAmtse);
	invoiceRemainigAdjItemListConsolidate.add(tempMapSer);
}

context.invoiceRemainigAdjItemList = invoiceRemainigAdjItemListConsolidate;

context.totTaxAmount = totTaxAmount;


context.mgpsAmt = mgpsAmt;

context.mgpsAmtPositive = Math.abs(mgpsAmt);


orderAttrForPo = [];
if(orderId){
orderAttrForPo = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
OrderHeaderList = delegator.findOne("OrderHeader",[orderId : orderId] , false);

tallyRefNo = OrderHeaderList.get("tallyRefNo");


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId));
conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));

cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
shipmentListForPOInvoiceId = delegator.findList("Invoice", cond, null, null, null, false);

purInvoiceId = "";
double purInvoiceTOt = 0;

if(shipmentListForPOInvoiceId){
 purInvoiceId = shipmentListForPOInvoiceId[0].invoiceId;

 
 conditionList.clear();
 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, purInvoiceId));
 //conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_RAWPROD_ITEM"));
 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
 purChaseInvoiceItemLists = delegator.findList("InvoiceItem", cond, null, null, null, false);
 
 for (eachItem in purChaseInvoiceItemLists) {

	 purInvoiceTOt = purInvoiceTOt+(eachItem.itemValue);
}
}

context.purInvoiceTOt = purInvoiceTOt;

/*conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
PurchaseOrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
*/

if(purInvoiceId){
	purInvoiceList = delegator.findOne("Invoice",[invoiceId : purInvoiceId] , false);
	if(purInvoiceList.referenceNumber)
	tallyRefNo = purInvoiceList.referenceNumber;
}


if(tallySalesNo)
tallyRefNo = tallySalesNo;


/*if(invoiceId == "10360")
 tallyRefNo= "YRPR/3/16-17";
else if(invoiceId == "10358")
 tallyRefNo= "YRPR/2/16-17";
else if(invoiceId == "10362")
 tallyRefNo= "YRPR/6/16-17";
else if(invoiceId == "10391")
tallyRefNo= "YRPR/5/16-17";
else if(invoiceId == "10390")
tallyRefNo= "YRPR/4/16-17";
*/
context.tallyRefNo = tallyRefNo;
allDetailsMap = [:];
productStoreId = OrderHeaderList.get("productStoreId");
branchId="";
if (productStoreId) {
	productStore = delegator.findByPrimaryKey("ProductStore", [productStoreId : productStoreId]);
	branchId=productStore.payToPartyId;
	
}
partyIdentification = delegator.findList("PartyIdentification",EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , branchId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(partyIdentification)){
	tinNumber="";
	tinDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
	if(UtilValidate.isNotEmpty(tinDetails)){
		tinDetails=EntityUtil.getFirst(tinDetails);
		tinNumber=tinDetails.idValue;
		allDetailsMap.put("tinNumber",tinNumber);
	}
	cstNumber="";
	cstDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
	if(UtilValidate.isNotEmpty(cstDetails)){
		cstDetails=EntityUtil.getFirst(cstDetails);
		cstNumber=cstDetails.idValue;
		allDetailsMap.put("cstNumber",cstNumber);
	}
	cinNumber="";
	cinDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CIN_NUMBER"));
	if(UtilValidate.isNotEmpty(cinDetails)){
		cinDetails=EntityUtil.getFirst(cinDetails);
		cinNumber=cinDetails.idValue;
		allDetailsMap.put("cinNumber",cinNumber);
	}
	panNumber="";
	panDetails = EntityUtil.filterByCondition(partyIdentification, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PAN_NUMBER"));
	if(UtilValidate.isNotEmpty(panDetails)){
		panDetails=EntityUtil.getFirst(panDetails);
		panNumber=panDetails.idValue;
		allDetailsMap.put("panNumber",panNumber);
	}
}
context.allDetailsMap= allDetailsMap;
//get Report Header
branchContext=[:];

if(branchId == "INT12" || branchId == "INT49" || branchId == "INT55")
branchId = "INT12";
else if(branchId == "INT8" || branchId == "INT16" || branchId == "INT20" || branchId == "INT21" || branchId == "INT22" || branchId == "INT44")
branchId = "INT8";


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
}

grandTotal = OrderHeaderList.get("grandTotal");

context.grandTotal = grandTotal;

actualOrderId = "";

   exprCondList=[];
   exprCondList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
   exprCondList.add(EntityCondition.makeCondition("orderAssocTypeId", EntityOperator.EQUALS, "BackToBackOrder"));
   EntityCondition disCondition = EntityCondition.makeCondition(exprCondList, EntityOperator.AND);
   OrderAss = EntityUtil.getFirst(delegator.findList("OrderAssoc", disCondition, null,null,null, false));
   
   actualOrderId = "";
   if(OrderAss){
	   
	   actualOrderId=OrderAss.toOrderId;
	   
   }

if(UtilValidate.isNotEmpty(orderAttrForPo)){
	
	orderAttrForPo.each{ eachAttr ->
		/*if(eachAttr.attrName == "PO_NUMBER"){
			actualOrderId =  eachAttr.attrValue;
		}*/
		if(eachAttr.attrName == "DST_ADDR"){
			
			if(!destination)
			destination =  eachAttr.attrValue;
			
			/*if(districtGeoId){
				districtName = districtGeoId ;
				GenericValue geo = delegator.findOne("Geo", [geoId:districtGeoId], false);
				if(UtilValidate.isNotEmpty(geo)&& UtilValidate.isNotEmpty(geo.get("geoName"))){
					destination = geo.get("geoName");
					}
			}*/
			
		}
	}
}









indentOrderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS , actualOrderId)  , UtilMisc.toSet("orderNo"), null, null, false );
if(UtilValidate.isNotEmpty(indentOrderSequences)){
	indentOrderSeqDetails = EntityUtil.getFirst(indentOrderSequences);
	salesOrder = indentOrderSeqDetails.orderNo;
	context.indentNo = salesOrder;
}else{
	context.indentNo = actualOrderId;
}
orderHeaderSequences = delegator.findList("OrderHeaderSequence", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
if(UtilValidate.isNotEmpty(orderHeaderSequences)){
	orderSquences = EntityUtil.getFirst(orderHeaderSequences);
	orderNo= orderSquences.orderNo;
	context.indentNo = orderNo;
}



OrderHeader = [];
OrderRoleList = [];
indentDate = "";
soceity = "";
poDate = "";
scheme = "";
supplier = "";
onbehalf = "";
finalDetails = [];
if(actualOrderId){
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, [orderId,actualOrderId]));
cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderHeader = delegator.findList("OrderHeader", cond, UtilMisc.toSet("orderId","orderDate","externalId"), null, null, false);

indentDetails = EntityUtil.filterByCondition(OrderHeader, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
indentDate = indentDetails[0].get("orderDate");

//externalOrderId = indentDetails[0].get("externalId");
PODetails = EntityUtil.filterByCondition(OrderHeader, EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
poDate = PODetails[0].get("orderDate");


conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ["SUPPLIER","ON_BEHALF_OF","BILL_TO_CUSTOMER"]));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
OrderRoleList = delegator.findList("OrderRole", expr, null, null, null, false);


	orderAttr = delegator.findList("OrderAttribute", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId), null, null, null, false);
	
	if(UtilValidate.isNotEmpty(orderAttr)){
		orderAttr.each{ eachAttr ->
			if(eachAttr.attrName == "SCHEME_CAT"){
				scheme =  eachAttr.attrValue;
			}
			
		}
	   }
	
	
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemAttributeList = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
	C2E2Form = "";
	
	
	if(OrderItemAttributeList){
		for (eachAttr in OrderItemAttributeList) {
			if(eachAttr.attrName == "checkCForm"){
				C2E2Form = eachAttr.attrValue;
				break;
			}
			else if(eachAttr.attrName == "checkE2Form"){
				C2E2Form = eachAttr.attrValue;
				break;
			}
		}
	}
	
	context.C2E2Form = C2E2Form;
	
	}

onbehalf = "";

for (eachRole in OrderRoleList) {
	
	if(eachRole.roleTypeId == "SUPPLIER")
	 supplier = eachRole.get("partyId");
	if(eachRole.roleTypeId == "BILL_TO_CUSTOMER")
	 soceity = eachRole.get("partyId");
	if(eachRole.roleTypeId == "ON_BEHALF_OF")
		 onbehalf = true;
}



context.poDate = poDate;
context.indentDate = indentDate;
context.scheme = scheme;
context.supplier = supplier;
context.destination = destination;
context.soceity = soceity;
context.onbehalf = onbehalf;
//context.externalOrderId = externalOrderId;



//if(onbehalf == true){
  // forOnbeHalf()
//}else{
  // context.reportTypeFlag = "DIRECT";
	
	SchemeQtyMap = [:];
	SchemeAmtMap = [:];
	
	double schemeDeductionAmt = 0;
	
	/*for (eachInvoiceList in invoiceAdjItemList) {
		if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.quantity))
		{
		SchemeQtyMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.quantity);
		}
		if(UtilValidate.isNotEmpty(eachInvoiceList.parentInvoiceItemSeqId) && UtilValidate.isNotEmpty(eachInvoiceList.amount))
		{
		SchemeAmtMap.put(eachInvoiceList.parentInvoiceItemSeqId, eachInvoiceList.amount);
		schemeDeductionAmt = schemeDeductionAmt+Math.abs(eachInvoiceList.amount);
		}
		
	}*/
	
	
	context.schemeDeductionAmt = Math.round(schemeDeductionAmt);
	
	
	double grandTotal = 0;
	if(invoiceItemList){
	
		
		List productIds = EntityUtil.getFieldListFromEntityList(invoiceItemList, "productId", true);
		
		
	/*	for(eachProd in productIds)
		{*/

			//eachInvoiceItemList = EntityUtil.filterByCondition(invoiceItemList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProd));
			
			
			double schemeAmt = 0;
			double quantity = 0;
			double amount = 0;
			
			
		for (eachInvoiceList in invoiceItemList) {
	   
			OrderItemDetail = [];
			
			tempMap = [:];
		 tempMap.put("productId", eachInvoiceList.productId);
		 tempMap.put("prodDescription", eachInvoiceList.description);
		 tempMap.put("rateKg", eachInvoiceList.unitPrice);
		// tempMap.put("amount", eachInvoiceList.amount);
		 /*String seq = String.format("%05d", i);
		 
		 
		 OrderItemAttribute = EntityUtil.filterByCondition(OrderItemAttributeList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seq));
		 
		  baleQty = "";
		  if(OrderItemAttribute){
		  OrderItemAttribute.each{ eachAttr ->
			 if(eachAttr.attrName == "BALE_QTY"){
				 baleQty =  eachAttr.attrValue;
			 }
		  }
		}*/
		 
		 
		// String seq = String.format("%05d", i);
		 
		 
		   conditionList.clear();
		   conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
		   conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, eachInvoiceList.invoiceItemSeqId));
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
		   cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   OrderItemBilling = delegator.findList("OrderItemBillingAndInvoiceAndInvoiceItem", cond, null, null, null, false);
		  
		
				  
		 itemOrderId  = OrderItemBilling[0].orderId;
		 orderItemSeqId  = OrderItemBilling[0].orderItemSeqId;
		 
		 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,itemOrderId));
		 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
	
		 
		 remarks = "";
		 if(OrderItemDetail)
		 remarks = OrderItemDetail[0].get("remarks");
		 
	/*	 conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		 conditionList.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, seq));
		 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 OrderItemAttributeList1 = delegator.findList("OrderItemAttribute", cond, null, null, null, false);
		 baleQty="";
		 unit="";
		 if(OrderItemAttributeList1){
		 OrderItemAttributeList1.each{ eachAttr ->
			if(eachAttr.attrName == "quotaQty"){
				schemeAmt =  schemeAmt+Double.valueOf(eachAttr.attrValue);
			}
			if(eachAttr.attrName == "BALE_QTY"){
				baleQty =  eachAttr.attrValue;
			}
			if(eachAttr.attrName == "YARN_UOM"){
				unit =  eachAttr.attrValue;
			}
		 }
	   }*/
		 
		 
		 quantity = eachInvoiceList.quantity;
		 amount = eachInvoiceList.amount;
		
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
		 
		 tempMap.put("unit", unit);
		 tempMap.put("remarks", remarks);
		 
		 
		 
			if(baleQty)
			tempMap.put("baleQty",baleQty);
			else
			tempMap.put("baleQty","");
			
			
			tempMap.put("quantity", quantity);
		
		//String schemeAmt = (String)SchemeQtyMap.get(eachInvoiceList.invoiceItemSeqId);
			
			
			//==============scheme Quantity===============
			
			
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
			conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceList.invoiceItemSeqId));
			conditionList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS,"TEN_PERCENT_SUBSIDY"));
			cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
	   
			double schemeQQQty = 0;
			if(invoiceInnerAdjItemList){
				
				 invoiceIdAdj = invoiceInnerAdjItemList[0].invoiceId;
				 invoiceItemSeqIdAdj = invoiceInnerAdjItemList[0].invoiceItemSeqId;
				 
				 
				 conditionList.clear();
				 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceIdAdj));
				 conditionList.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.EQUALS, invoiceItemSeqIdAdj));
				 conditionList.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "TEN_PERCENT_SUBSIDY"));
				 cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				 OrderAdjustmentAndBilling = delegator.findList("OrderAdjustmentAndBilling", cond1, null, null, null, false);
	 
				 if(OrderAdjustmentAndBilling[0])
				 schemeQQQty = OrderAdjustmentAndBilling[0].quantity;
				
			}
			
			
			double tenPerQty = 0;
			tenPerQty = schemeQQQty;
			tempMap.put("schemeQty", schemeQQQty);
			
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
			
		  
		 double serviceAmt = 0;
		 double sourcePercentage = 0;
		 
		  if(scheme == "General"){
			  
			  conditionList.clear();
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceId", EntityOperator.EQUALS, eachInvoiceList.invoiceId));
			  conditionList.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS,eachInvoiceList.invoiceItemSeqId));
			  conditionList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"Service Charge"));
			  cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			  invoiceInnerAdjItemList = EntityUtil.filterByCondition(invoiceAdjItemList, cond);
			  
			  if(invoiceInnerAdjItemList){
			  serviceAmt = serviceAmt+invoiceInnerAdjItemList[0].itemValue;
			  //sourcePercentage = sourcePercentage+invoiceInnerAdjItemList[0].sourcePercentage;
			  
			  }
		  }
		  
		  
		  if(scheme == "General"){
			  
			  sourcePercentage = (serviceAmt/(eachInvoiceList.itemValue))*100;
			  double perAmt = (eachInvoiceList.amount*sourcePercentage)/100;
			  
			  tempMap.put("amount",(eachInvoiceList.amount+perAmt));
			  }else{
			  tempMap.put("amount", eachInvoiceList.amount);
		   }
			  
		  
		 // tempMap.put("ToTamount", (quantity*amount)+serviceAmt);
			  
			  tempMap.put("ToTamount", eachInvoiceList.itemValue+serviceAmt);
		 
			  
			  
			   grandTotal = grandTotal+(eachInvoiceList.itemValue)+serviceAmt;
		  
		 /* double mgpsQty = 0;
		  if(quantity > schemeAmt)
			mgpsQty = schemeAmt;
		  else
			mgpsQty = quantity;
			tempMap.put("mgpsQty", mgpsQty);*/
		  
		  
		  
		finalDetails.add(tempMap);
		 }
		//}
	 }
	
	context.grandTotal = grandTotal;
	context.finalDetails = finalDetails;

		
//}

//==============Address Details===================

finalAddresList=[];
address1="";
address2="";
city="";
postalCode="";
panId="";
tanId="";

partyPostalAddress = dispatcher.runSync("getPartyPostalAddress", [partyId:partyId, userLogin: userLogin]);


if(partyPostalAddress){
	

   if(partyPostalAddress.address1){
	   address1=partyPostalAddress.address1;
  
   tempMap=[:];
   tempMap.put("key1","Road / Street / Lane");
   tempMap.put("key2",address1);
   finalAddresList.add(tempMap);
   if(partyPostalAddress.address2){
	   address2=partyPostalAddress.address2;
   }
   tempMap=[:];
   tempMap.put("key1","Area / Locality");
   tempMap.put("key2",address2);
   finalAddresList.add(tempMap);
   if(partyPostalAddress.city){
	   
	   city=partyPostalAddress.city;
   }
   tempMap=[:];
   tempMap.put("key1","Town / District / City");
   tempMap.put("key2",city);
   finalAddresList.add(tempMap);
   
   if(partyPostalAddress.postalCode){
	   postalCode=partyPostalAddress.postalCode;
   }
   tempMap=[:];
   tempMap.put("key1","PIN Code");
   tempMap.put("key2",postalCode);
   finalAddresList.add(tempMap);
   
	}else{
		contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
		partyPostalAddress = contactMench.postalAddress;
		
		if(partyPostalAddress[0]){
			address1=partyPostalAddress[0].address1;
		
		tempMap=[:];
		tempMap.put("key1","Road / Street / Lane");
		tempMap.put("key2",address1);
		finalAddresList.add(tempMap);
		if(partyPostalAddress[0].address2){
			address2=partyPostalAddress[0].address2;
		}
		tempMap=[:];
		tempMap.put("key1","Area / Locality");
		tempMap.put("key2",address2);
		finalAddresList.add(tempMap);
		if(partyPostalAddress[0].city){
			city=partyPostalAddress[0].city;
		}
		tempMap=[:];
		tempMap.put("key1","Town / District / City");
		tempMap.put("key2",city);
		finalAddresList.add(tempMap);
		
		if(partyPostalAddress[0].postalCode){
			postalCode=partyPostalAddress[0].postalCode;
		}
		tempMap=[:];
		tempMap.put("key1","PIN Code");
		tempMap.put("key2",postalCode);
		finalAddresList.add(tempMap);
		}
	}
   
}else{
		contactMench = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
		partyPostalAddress = contactMench.postalAddress;
		
		if(partyPostalAddress){
			address1=partyPostalAddress[0].address1;
		
		tempMap=[:];
		tempMap.put("key1","Road / Street / Lane");
		tempMap.put("key2",address1);
		finalAddresList.add(tempMap);
		if(partyPostalAddress[0].address2){
			address2=partyPostalAddress[0].address2;
		}
		tempMap=[:];
		tempMap.put("key1","Area / Locality");
		tempMap.put("key2",address2);
		finalAddresList.add(tempMap);
		if(partyPostalAddress[0].city){
			city=partyPostalAddress[0].city;
		}
		tempMap=[:];
		tempMap.put("key1","Town / District / City");
		tempMap.put("key2",city);
		finalAddresList.add(tempMap);
		
		if(partyPostalAddress[0].postalCode){
			postalCode=partyPostalAddress[0].postalCode;
		}
		tempMap=[:];
		tempMap.put("key1","PIN Code");
		tempMap.put("key2",postalCode);
		finalAddresList.add(tempMap);
		}
}

context.finalAddresList = finalAddresList;
//============================================================

//def forOnbeHalf(){
	OrderItemAttributeList = [];
	finaOnbehalflDetails = [];
	
	//context.reportTypeFlag = "ONBEHALF";
	
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	OrderItemList = delegator.findList("OrderItem", cond, null, null, null, false);
*/
	if(actualOrderId){
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, actualOrderId));
		cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		OrderItemDetail = delegator.findList("OrderItemDetail", cond, null, null, null, false);
		
		
		for (eachOrderItemList in OrderItemDetail) {
			
			//OrderItemAttribute = EntityUtil.filterByCondition(OrderItemAttributeList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
			
			partyId = "";
			productId = "";
			itemDescription = "";
			passNo = "";
			quantity = 0;
			unitPrice = 0;
			quotaQty = 0;
			amount = 0;
			tempMap = [:];
			
			//Debug.log("OrderItemAttribute============"+OrderItemAttribute);
			
	/*		if(UtilValidate.isNotEmpty(OrderItemAttribute)){
				OrderItemAttribute.each{ eachAttr ->
					if(eachAttr.attrName == "WIEVER_CUSTOMER"){
						partyId =  eachAttr.attrValue;
						conditionList.clear();
						conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
						cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
						if(PartyIdentificationList){
						passNo = PartyIdentificationList[0].get("idValue");
						}
					}
					if(eachAttr.attrName == "quotaQty"){
						quotaQty =  eachAttr.attrValue;
						
					}
				}
			   }*/
			
			
				partyId = eachOrderItemList.get("partyId");
			 
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PSB_NUMER"));
				cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				PartyIdentificationList = delegator.findList("PartyIdentification", cond, null, null, null, false);
				if(PartyIdentificationList){
				passNo = PartyIdentificationList[0].get("idValue");
				}
			   
			   tempMap.put("partyId", partyId);
			   
			   tempMap.put("passNo", passNo);
			   
			//eachOrderItemList = EntityUtil.filterByCondition(OrderItemList, EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, eachSeq));
			
			if(eachOrderItemList){
				
				//itemDescription = eachOrderItemList[0].get("itemDescription");
				tempMap.put("itemDescription", "");
				quantity = eachOrderItemList.get("quantity");
				tempMap.put("quantity", quantity);
				unitPrice = eachOrderItemList.get("unitPrice");
				
				quotaQty = eachOrderItemList.get("quotaQuantity");
				
				tempMap.put("quotaQty", Math.round(((quotaQty*unitPrice)*10)/100));
				amount = quantity*unitPrice;
				tempMap.put("amount", amount);
			}
			finaOnbehalflDetails.add(tempMap);
		}
		
	}
	context.finaOnbehalflDetails = finaOnbehalflDetails;
	invoiceAttribute = delegator.findOne("InvoiceAttribute",[invoiceId : invoiceId, attrName :"saleTitleTransferEnumId"] , false);
	if(invoiceAttribute){
		attrValue=invoiceAttribute.attrValue;
		if("CST_CFORM".equals(attrValue)){
			cFormAgnst=cFormAgnst+1;
		}
	}
	context.cFormAgnst=cFormAgnst;
	
//}

}

	

	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
    import org.ofbiz.entity.GenericValue;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.ofbiz.service.DispatchContext;
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
    import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	periodBillingList=[];
	facilityIds=[];
	conditionList=[];
	itemsList=[];
	itemsListMap=[:];
	returnItemsListMap=[:];
	returnItemMap=[:];
	invoiceListMap=[:];
	invoiceList=[];
	periodBillingIds=[];
	
	//partyPONumMap = [:];
	invoiceSequenceNumMap = [:];
	/*partyIdentification = delegator.findList("PartyIdentification", EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PO_NUMBER"), null, null, null, false);
	
	partyIdentification.each{eachPO ->
		partyPONumMap.put(eachPO.partyId, eachPO.idValue);
	}*/
	creditInstFac = delegator.findList("Facility", EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "CR_INST"), UtilMisc.toSet("ownerPartyId"), null, null, false);
	creditInstFacIds = EntityUtil.getFieldListFromEntityList(creditInstFac, "ownerPartyId", true);
	
	
    conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, parameters.customTimePeriodId));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"COM_CANCELLED"));
	if(!((parameters.periodBillingId).equals("allInstitutions"))){
		conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, parameters.periodBillingId));
	}
	EntityCondition condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	periodBillingList = delegator.findList("PeriodBilling", condExpr, null, null, null, false);
	facilityIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "facilityId", true);
	periodBillingIds = EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", true);
	
	customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", parameters.customTimePeriodId), false);
	Timestamp fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	Timestamp thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	billingPeriodDateStr = "";
	billingPeriodDateStr += UtilDateTime.toDateString(fromDate, "dd-MMM-yyyy");
	billingPeriodDateStr += " to ";
	billingPeriodDateStr += UtilDateTime.toDateString(thruDate, "dd-MMM-yyyy");
	context.billingPeriodDate = billingPeriodDateStr;
	
	shipmentIds = [];
	amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,"AM");
	shipmentIds.addAll(amShipmentIds);
	pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,"PM");
	shipmentIds.addAll(pmShipmentIds);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS , "CR_INST"));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipmentIds));
	conditionList.add(EntityCondition.makeCondition("invoiceStatusId", EntityOperator.NOT_EQUAL , "INVOICE_CANCELLED"));
	invCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceList = delegator.findList("OrderHeaderFacAndItemBillingInv", invCond, UtilMisc.toSet("invoiceId"), null, null, false);
	invoiceIds = EntityUtil.getFieldListFromEntityList(invoiceList, "invoiceId", true);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.IN , invoiceIds));
	billCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	invoiceData = delegator.findList("InvoiceAndBillOfSaleInvoiceSequence", billCond, null, null, null, false);
	invoiceData.each{eachItem ->
		invoiceSequenceNumMap.put(eachItem.invoiceId, eachItem.sequenceId);
	}
	/*invoiceData.each{eachItem ->
		partyPONumMap.put(eachItem.facilityId, eachItem.sequenceId);
	}*/
	//context.partyPONumMap = partyPONumMap;
	context.invoiceSequenceNumMap = invoiceSequenceNumMap;
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS , "RETURN_ACCEPTED"));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipmentIds));
	condition2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	returnItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", condition2, null, ["estimatedShipDate"], null, false);
	
	//Debug.log("------------------returnItemsList--------------------- : "+returnItemsList);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, facilityIds));
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipmentIds));
	condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["ownerPartyId","estimatedShipDate","orderId","externalId","productId","shipmentTypeId","itemDescription","productName","quantity","unitPrice","unitListPrice", "shipmentId", "vatPercent"] as Set;
	orderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, fieldsToSelect , ["estimatedDeliveryDate"], null, false);
	
	if(UtilValidate.isNotEmpty(reportTypeFlag)){
		if(reportTypeFlag=="instBillOfSale"){
			orderItemsList = EntityUtil.filterByCondition(orderItemsList, EntityCondition.makeCondition("vatPercent", EntityOperator.LESS_THAN, BigDecimal.ONE));
		}
		if(reportTypeFlag=="enclosureOfTaxInvoice"){
			orderItemsList = EntityUtil.filterByCondition(orderItemsList, EntityCondition.makeCondition("vatPercent", EntityOperator.GREATER_THAN, BigDecimal.ONE));
		}

	}
	if(parameters.BOS && parameters.BOS=="billOfSale"){
		
	}
	
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "VAT_SALE"));
	EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	Set fieldsToSelect = UtilMisc.toSet("productId", "taxPercentage");
	
	productPrice = delegator.findList("ProductPrice", condition1,  fieldsToSelect, ["taxPercentage"], null, false);
	productPrice = EntityUtil.filterByDate(productPrice, fromDate);
	List vatList = EntityUtil.getFieldListFromEntityList(productPrice, "taxPercentage", true);
	context.vatList = vatList;

	returnItemMap = FastMap.newInstance();
	saleTotal=0;
	returnTotal=0;
	finalAmount=0;
	
	facilityIds.each{eachFacilityId->
		
		itemsList = EntityUtil.filterByCondition(orderItemsList, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS , eachFacilityId));
		
		if (UtilValidate.isNotEmpty(itemsList)) {
			itemsListMap.put(eachFacilityId, itemsList);
		}
		//Debug.log("=============================="+itemsList);
		if (itemsList.size() > 0) {
			finalReturnItemList = [];
			for (i = 0; i < itemsList.size(); i++) {
				
				eachEntry = itemsList.get(i);
				tempMap = [:];
				/*condProductIdList = [];
				condProductIdList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, 0.00));
				condProductIdList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "VAT_SALE"));
				condProductIdList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachEntry.productId));
				conditionProdId=EntityCondition.makeCondition(condProductIdList,EntityOperator.AND);
				fieldsToSelect = ["taxPercentage"] as Set;
				productPriceList = delegator.findList("ProductPrice", conditionProdId, fieldsToSelect , null, null, false);
				
				productVatAmount=0;
				if(productPriceList){
					productVatAmount = EntityUtil.getFirst(productPriceList);
					}*/
			//if(productVatAmount==0 || productVatAmount==0.00){
				
				tempMap.estimatedShipDate = eachEntry.estimatedShipDate;
				tempMap.originFacilityId = eachEntry.ownerPartyId;
				tempMap.shipmentTypeId = eachEntry.shipmentTypeId;
				tempMap.productId = eachEntry.productId;
				tempMap.orderId=eachEntry.orderId;
				tempMap.externalId=eachEntry.externalId;
				tempMap.quantity = eachEntry.quantity;
				tempMap.productName=eachEntry.productName;
				tempMap.unitListPrice = eachEntry.unitListPrice;
				tempMap.itemDescription = eachEntry.itemDescription;
				tempMap.amount = (eachEntry.quantity*eachEntry.unitPrice);
				condList = [];
				condList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, eachFacilityId));
				condList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, eachEntry.shipmentId));
				condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachEntry.productId));
				cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				returnItems = EntityUtil.filterByCondition(returnItemsList, cond);
				returnPrice=0;
				returnProductName ="";
				
				
				List conditionList1= FastList.newInstance();
				conditionList1.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, null));
				conditionList1.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "VAT_SALE"));
				conditionList1.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachEntry.productId));
				EntityCondition condition2 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
				fieldsToSelect = ["taxPercentage"] as Set;
		     	 productPrice = delegator.findList("ProductPrice", condition2, fieldsToSelect ,  null , null, false);
				vatValues= EntityUtil.getFirst(productPrice);
				tempMap.vatPercentage = vatValues.taxPercentage;
				 
				 
				 
				
				if(returnItems){
					returnValues = EntityUtil.getFirst(returnItems);
					tempMap.returnQuantity = returnValues.returnQuantity;
					tempMap.taxReturnQuantity = returnValues.returnQuantity;
					productList=[];
					productList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachEntry.productId));
					condition = EntityCondition.makeCondition(productList, EntityOperator.AND);
					fieldsToSelect = ["productName"] as Set;
					returnProductDescriptionsList = delegator.findList("Product", condition, fieldsToSelect, null , null, false);
					returnProductDescriptions= EntityUtil.getFirst(returnProductDescriptionsList);
					
					tempMap.returnProductName = returnProductDescriptions.productName;

					if (UtilValidate.isNotEmpty(returnValues.returnPrice)) {
							tempMap.returnAmount = (returnValues.returnQuantity*returnValues.returnPrice);
			      	}else{
					  tempMap.returnAmount = (returnValues.returnQuantity*returnPrice);
					  }
				}
				else{
					tempMap.returnQuantity = 0;
					tempMap.returnAmount = 0;
					tempMap.returnProductName=returnProductName;
					tempMap.taxReturnQuantity = ""
					tempMap.taxReturnAmount = "";
				}
				netTotalAmount=0.00;
				taxAmount=0;
				
				
				finalReturnItemList.add(tempMap);
			//}
			}//for loop close
			
			finalAmount=finalAmount+netTotalAmount+taxAmount;
			returnItemMap.put(eachFacilityId,finalReturnItemList);
		}
	}
	context.itemsReturnListMap=returnItemMap;	
	context.itemsListMap=itemsListMap;

		periodBillingIds.each{eachperiodBillingId->
		conditionList.clear();
//		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "INVOICE_APPROVED"));
//		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
//		conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,eachperiodBillingId));
		cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		invoiceList = delegator.findList("Invoice", cond, ["invoiceId","invoiceDate","facilityId","partyId"] as Set , ["-invoiceDate"], null, false);
		if (UtilValidate.isNotEmpty(invoiceList)) {
			invoice=EntityUtil.getFirst(invoiceList);
			invoiceListMap.put(invoice.getString("facilityId"), invoice);
			context.invoiceListMap=invoiceListMap;
//			Debug.log("------------invoiceListMap--------------- : "+invoiceListMap);
		}
	}
	/*if (UtilValidate.isNotEmpty(itemsList)) {
		    itemsListMap.put(eachFacilityId, itemsList);
		}*/
	
	
	

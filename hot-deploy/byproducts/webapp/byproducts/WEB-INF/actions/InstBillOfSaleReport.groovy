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
	billingPeriodDateStr += UtilDateTime.toDateString(fromDate, "dd-MMM-2014");
	billingPeriodDateStr += "--";
	billingPeriodDateStr += UtilDateTime.toDateString(thruDate, "dd-MMM-2014");
	context.billingPeriodDate = billingPeriodDateStr;
	
	/*
	facilityIds.each{eachFacilityId->
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS ,eachFacilityId));
		condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fieldsToSelect = ["originFacilityId","estimatedShipDate","orderId","productId","shipmentTypeId","itemDescription","productName","quantity","unitListPrice"] as Set;
		itemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, fieldsToSelect , ["estimatedDeliveryDate"], null, false);
			if (UtilValidate.isNotEmpty(itemsList)) {
				itemsListMap.put(eachFacilityId, itemsList);
			}
		}
	context.itemsListMap=itemsListMap;*/
	
	
	
	
	
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS , "RETURN_ACCEPTED"));
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
	condition2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	returnItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", condition2, null, ["estimatedShipDate"], null, false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
	conditionList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, facilityIds));
	condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["originFacilityId","estimatedShipDate","orderId","productId","shipmentTypeId","itemDescription","productName","quantity","unitPrice","unitListPrice", "shipmentId"] as Set;
	orderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, fieldsToSelect , ["estimatedDeliveryDate"], null, false);
	
	returnItemMap = FastMap.newInstance();
	
	facilityIds.each{eachFacilityId->
		
		itemsList = EntityUtil.filterByCondition(orderItemsList, EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS , eachFacilityId));
		
		if (UtilValidate.isNotEmpty(itemsList)) {
			itemsListMap.put(eachFacilityId, itemsList);
		}
		
		if (itemsList.size() > 0) {
			finalReturnItemList = [];
			for (i = 0; i < itemsList.size(); i++) {
				
				eachEntry = itemsList.get(i);
				tempMap = [:];
				tempMap.estimatedShipDate = eachEntry.estimatedShipDate;
				tempMap.originFacilityId = eachEntry.originFacilityId;
				tempMap.shipmentTypeId = eachEntry.shipmentTypeId;
				tempMap.productId = eachEntry.productId;
				tempMap.orderId=eachEntry.orderId;
				tempMap.quantity = eachEntry.quantity;
				tempMap.productName=eachEntry.productName;
				tempMap.unitListPrice = eachEntry.unitListPrice;
				tempMap.itemDescription = eachEntry.itemDescription;
				tempMap.amount = (eachEntry.quantity*eachEntry.unitPrice);
				condList = [];
				condList.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, eachFacilityId));
				condList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, eachEntry.shipmentId));
				condList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachEntry.productId));
				cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
				returnItems = EntityUtil.filterByCondition(returnItemsList, cond);
				
				returnPrice=0;
				
				if(returnItems){
					returnValues = EntityUtil.getFirst(returnItems);
					tempMap.returnQuantity = returnValues.returnQuantity;
					if (UtilValidate.isNotEmpty(returnValues.returnPrice)) {
						tempMap.returnAmount = (returnValues.returnQuantity*returnValues.returnPrice);
			      	}else{
					  tempMap.returnAmount = (returnValues.returnQuantity*returnPrice);
					  }
				}
				else{
					tempMap.returnQuantity = 0;
					tempMap.returnAmount = 0;
				}
				finalReturnItemList.add(tempMap);
				
			}
			returnItemMap.put(eachFacilityId,finalReturnItemList);
		}
		/*if (UtilValidate.isNotEmpty(itemsList)) {
		    itemsListMap.put(eachFacilityId, itemsList);
		}*/
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
		}
	}
	
	
	

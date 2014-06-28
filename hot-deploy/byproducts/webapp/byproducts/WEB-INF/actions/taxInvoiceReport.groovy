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
	
	
	List conditionList= FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, null));
	conditionList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "VAT_SALE"));
	EntityCondition condition1 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	Set fieldsToSelect = UtilMisc.toSet("productId", "taxPercentage");
	
	productPrice = delegator.findList("ProductPrice", condition1,  fieldsToSelect, ["taxPercentage"], null, false);
	productPrice = EntityUtil.filterByDate(productPrice, fromDate);
	List vatList = EntityUtil.getFieldListFromEntityList(productPrice, "taxPercentage", true);
	context.vatList = vatList;
	
	vatMap = [:];
	vatProductList = [];
	List conditionList1= FastList.newInstance();
	conditionList1.add(EntityCondition.makeCondition("taxPercentage", EntityOperator.NOT_EQUAL, null));
	conditionList1.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.LIKE, "VAT_SALE"));
	EntityCondition condition2 = EntityCondition.makeCondition(conditionList1,EntityOperator.AND);
	vatProductList = delegator.findList("ProductPrice", condition2, null ,  null , null, false);
	vatProductList.each{ vatProd->
		vatPercentage = vatProd.taxPercentage;
		vatMap.put(vatProd.productId,vatPercentage);
	}
	context.put("vatMap",vatMap);
	
	shipmentIds = [];
	amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,"AM");
	shipmentIds.addAll(amShipmentIds);
	pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,"PM");
	shipmentIds.addAll(pmShipmentIds);
	vatProductIds=[:];
	facilityMap=[:];
	facilityIds.each{ eachFacilityId->
		productMap = [:];
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL , "ORDER_CANCELLED"));
		conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, eachFacilityId));
		conditionList.add(EntityCondition.makeCondition("vatPercent", EntityOperator.GREATER_THAN, BigDecimal.ONE));
		conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN , shipmentIds));
		condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		fieldsToSelect = ["ownerPartyId","estimatedShipDate","orderId","productId","shipmentTypeId","itemDescription","productName","quantity","unitPrice","unitListPrice", "shipmentId"] as Set;
		orderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", condition1, fieldsToSelect , ["estimatedDeliveryDate"], null, false);
		List vatProductIds = EntityUtil.getFieldListFromEntityList(orderItemsList, "productId", true);
		productTotalsMap = [:];
		if(UtilValidate.isNotEmpty(orderItemsList)){
			for (i = 0; i < orderItemsList.size(); i++) {
				eachEntry = orderItemsList.get(i);
				qtyIncluded = 0;
				product = delegator.findOne("Product", ["productId" : eachEntry.productId], true);
				if(UtilValidate.isNotEmpty(product)){
					qtyIncluded = product.quantityIncluded;
				}
				if(UtilValidate.isEmpty(productTotalsMap[eachEntry.productId])){
					tempMap = [:];
					tempMap["quantity"] = ((eachEntry.quantity)*(qtyIncluded));
					tempMap["amount"]= (eachEntry.quantity*eachEntry.unitPrice);
					productTotalsMap[eachEntry.productId] = tempMap;
				}else{
				    tempMap = [:];
					tempMap = productTotalsMap[eachEntry.productId];
					tempMap["quantity"] += ((eachEntry.quantity)*(qtyIncluded));
					tempMap["amount"] += (eachEntry.quantity*eachEntry.unitPrice);
					tempTempMap = [:];
					tempTempMap.putAll(tempMap);
					productTotalsMap[eachEntry.productId] = tempTempMap;
				}
			}
		}
		returnConditionList=[];
		returnConditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
		returnConditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
		returnConditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS ,eachFacilityId));
		returnConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN ,vatProductIds));//to get Only taxble products
		returnConditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS, "RETURN_ACCEPTED"));
		returnCondition = EntityCondition.makeCondition(returnConditionList,EntityOperator.AND);
		returnHeaderItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", returnCondition, null, null, null, false);
		productReturnMap = [:];
		if(UtilValidate.isNotEmpty(returnHeaderItemsList)){
			returnProducts = EntityUtil.getFieldListFromEntityList(returnHeaderItemsList, "productId", true);
			if(UtilValidate.isNotEmpty(returnProducts)){
				returnProducts.each{ eachProduct->
						returnProdList = EntityUtil.filterByCondition(returnHeaderItemsList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProduct));
						prodTotalQty = 0;
						returnBasicPrice = 0;
						product = delegator.findOne("Product", ["productId" : eachProduct], true);
						returnQtyIncluded = product.quantityIncluded;
						
						retTempMap = [:];
						returnProdList.each{ eachProdReturnItem ->
							prodTotalQty = prodTotalQty+eachProdReturnItem.returnQuantity;
							if(eachProdReturnItem.returnBasicPrice){
								returnBasicPrice = returnBasicPrice+(eachProdReturnItem.returnQuantity*eachProdReturnItem.returnBasicPrice);
							}
							
						}
						retTempMap.returnQuantity = prodTotalQty;
						retTempMap.returnPrice = returnBasicPrice;
						retTempMap.returnQtyLtrs = prodTotalQty*returnQtyIncluded;
						productReturnMap.put(eachProduct, retTempMap);
				}
			}
		}
		productMap.put("prodMap",productTotalsMap);
		productMap.put("vatMap",vatMap);
		productMap.put("returnMap",productReturnMap);
		if (UtilValidate.isNotEmpty(productTotalsMap) || UtilValidate.isNotEmpty(productReturnMap)) {
			facilityMap.put(eachFacilityId,productMap);
		}
	}
	context.put("facilityMap",facilityMap);
	periodBillingIds.each{eachperiodBillingId->
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS ,eachperiodBillingId));
		cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		invoiceList = delegator.findList("Invoice", cond, ["invoiceId","invoiceDate","facilityId","partyId","periodBillingId","dueDate"] as Set , ["-invoiceDate"], null, false);
		if (UtilValidate.isNotEmpty(invoiceList)) {
			invoice=EntityUtil.getFirst(invoiceList);
			invoiceListMap.put(invoice.getString("facilityId"), invoice);
			if (UtilValidate.isNotEmpty(invoiceListMap)) {
				context.invoiceListMap=invoiceListMap;
			}
			
		}
	}

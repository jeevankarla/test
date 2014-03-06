	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
	import java.lang.*;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import java.sql.*;
	import java.util.Calendar;
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
	import in.vasista.vbiz.byproducts.ByProductServices;
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	
	effectiveDate = null;
	thruEffectiveDate = null;
	effectiveDateStr = parameters.indentDate;
	thruEffectiveDateStr = parameters.indentThruDate;
	
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
		thruEffectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			thruEffectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(thruEffectiveDateStr));
		}catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
		}
	}
	intervalDays = (UtilDateTime.getIntervalInDays(effectiveDate, thruEffectiveDate)+1);
	
	context.put("effectiveDate", effectiveDate);
	formattedEffDate = UtilDateTime.toDateString(effectiveDate, "dd MMMMM yyyy");
	formattedThruEffDate = UtilDateTime.toDateString(thruEffectiveDate, "dd MMMMM yyyy");
	if(intervalDays > 1){
		context.reportDate = formattedEffDate+" - "+formattedThruEffDate;
	}
	else{
		context.reportDate = formattedEffDate;	
	}
	dayBegin = UtilDateTime.getDayStart(effectiveDate, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate, timeZone, locale);
	
	thruDateBegin = UtilDateTime.getDayStart(thruEffectiveDate, timeZone, locale);
	thruDateEnd = UtilDateTime.getDayEnd(thruEffectiveDate, timeZone, locale);
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDateEnd));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["facilityId", "sequenceNum", "productId", "quantity", "preRevisedQuantity"] as Set;
	indentList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);
	
	//List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, dayBegin, thruDateEnd, "BYPRODUCTS");
	List shipmentList =ByProductNetworkServices.getByProdShipmentIds(delegator, dayBegin, dayEnd);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","originFacilityId", "quantity","unitPrice","shipmentId","categoryTypeEnum"] as Set;
	
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect , ["productId"], null, false);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
	receiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId", "quantityAccepted"] as Set;
	shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", receiptCondition,  fieldsToSelect, null, null, false);
	
	prodIndentAndDispatchMap = [:];
	
	for(int i = 0; i < productList.size(); i++){
		
		BigDecimal totalIndentQty = BigDecimal.ZERO;
		BigDecimal totalDispatchQty = BigDecimal.ZERO;
		BigDecimal totalReceiptQty = BigDecimal.ZERO;
		
		productId = productList.get(i);
		List prodIndentList = EntityUtil.filterByAnd(indentList, UtilMisc.toMap("productId", productId));
		List prodOrderItemsList = EntityUtil.filterByAnd(boothOrderItemsList, UtilMisc.toMap("productId", productId));
		//List prodReceiptList = EntityUtil.filterByAnd(shipmentReceiptList, UtilMisc.toMap("productId", productId));
		
		for(j = 0; j < prodIndentList.size(); j++){
			if(UtilValidate.isNotEmpty((prodIndentList.get(j)).get("preRevisedQuantity"))){
				totalIndentQty = totalIndentQty.add((prodIndentList.get(j)).get("preRevisedQuantity"));
			}
			else{
				totalIndentQty = totalIndentQty.add((prodIndentList.get(j)).get("quantity"));
			}
		}
		for(k = 0; k < prodOrderItemsList.size(); k++){
			totalDispatchQty = totalDispatchQty.add((prodOrderItemsList.get(k)).get("quantity"));
		}
		/*for(m = 0; m < prodReceiptList.size(); m++){
			totalReceiptQty = totalReceiptQty.add((prodReceiptList.get(m)).get("quantityAccepted"));
		}*/
		
		
		indentAndDispatchMap = [:];
		indentAndDispatchMap["indentQty"] = totalIndentQty;
		indentAndDispatchMap["dispatchQty"] = totalDispatchQty.add(totalReceiptQty);
		
		tempIndentAndDispatchMap = [:];
		tempIndentAndDispatchMap.putAll(indentAndDispatchMap);
		
		prodIndentAndDispatchMap.put(productId, tempIndentAndDispatchMap);
		
	}
	
	context.put("prodIndentAndDispatchMap", prodIndentAndDispatchMap);

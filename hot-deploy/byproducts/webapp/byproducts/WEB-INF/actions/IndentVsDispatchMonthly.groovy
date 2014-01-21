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
	import org.ofbiz.network.NetworkServices;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	
	customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
	fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	
	month = UtilDateTime.toDateString(fromDateTime, "MMMMM-yyyy");
	context.putAt("month", month);
	
	monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
	monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
	
	intervalDays = (UtilDateTime.getIntervalInDays(monthBegin, monthEnd)+1);
	
	columnTitleMap = [:];
	headingTypeList = ["_Indent", "_Despatch"];
	
	subHeadingMap = [:];
	
	subHeadingMap.put("productId", "-");
	subHeadingMap.put("productName", "-");
	Timestamp startingDate = monthBegin;
	for (int k = 1; k <= intervalDays; k++) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String stDate = sdf.format(startingDate);
		int dateHeading = UtilDateTime.getDayOfMonth(startingDate, timeZone, locale);
		String heading = "Day" + new Integer(k).toString();
		for(int l=0; l<headingTypeList.size(); l++){
			columnTitleMap.put(heading + headingTypeList[l] + "Title", stDate.toString());
		}
		startingDate = UtilDateTime.addDaysToTimestamp(startingDate, 1);
		
		for(int l=0; l<headingTypeList.size(); l++){
			subHeadingMap.put(heading + headingTypeList[l], ((headingTypeList[l]).split("_"))[1]);
		}

	}
	
	tempSubHeadingMap = [:];
	tempSubHeadingMap.putAll(subHeadingMap);
	
	productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	productList = ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("productIdsList");
	
	products = delegator.findList("Product", null, null, null, null, false);
	productNames = [:];
	if(products){
		products.each{ eachProd ->
			productNames.putAt(eachProd.productId, eachProd.productName);
		}
	}
	
	conditionList = [];
	//conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS, "BYPRODUCTS"));
	conditionList.add(EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.IN, ["AM","PM"]));
	conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO ,monthEnd));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["facilityId", "sequenceNum", "productId", "quantity", "preRevisedQuantity", "fromDate", "thruDate"] as Set;
	indentList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, fieldsToSelect , ["productId"], null, false);
	
	//List shipmentList = ByProductNetworkServices.getByProdShipmentIdsByType(delegator, monthBegin, monthEnd, "BYPRODUCTS");
	List shipmentList =ByProductNetworkServices.getByProdShipmentIds(delegator, monthBegin, monthEnd);
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","originFacilityId", "quantity","unitPrice","shipmentId","categoryTypeEnum", "estimatedDeliveryDate"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect , ["productId"], null, false);
	
	/*conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentList));
	conditionList.add(EntityCondition.makeCondition("isCancelled", EntityOperator.EQUALS, null));
	receiptCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId", "quantityAccepted", "datetimeReceived"] as Set;
	shipmentReceiptList = delegator.findList("ShipmentReceiptAndItem", receiptCondition,  fieldsToSelect, null, null, false);*/
	
	indentVsDespatchList = [];
	indentVsDespatchList.addAll(tempSubHeadingMap);
	
	for(int i = 0; i < productList.size(); i++){
		
		productId = productList.get(i);
		List prodIndentList = EntityUtil.filterByAnd(indentList, UtilMisc.toMap("productId", productId));
		List prodOrderItemsList = EntityUtil.filterByAnd(boothOrderItemsList, UtilMisc.toMap("productId", productId));
		//List prodReceiptList = EntityUtil.filterByAnd(shipmentReceiptList, UtilMisc.toMap("productId", productId));
		
		prodIndentDespatchMap = [:];
		prodIndentDespatchMap.put("productId", productId);
		prodIndentDespatchMap.put("productName", productNames.get(productId));
		
		monthIterDate = monthBegin;
		BigDecimal totalIndentQty = BigDecimal.ZERO;
		BigDecimal totalDispatchQty = BigDecimal.ZERO;
		BigDecimal totalReceiptQty = BigDecimal.ZERO;
		
		for(int j=0; j<intervalDays; j++){
			
			BigDecimal dayIndentQty = BigDecimal.ZERO;
			BigDecimal dayDispatchQty = BigDecimal.ZERO;
			BigDecimal dayReceiptQty = BigDecimal.ZERO;
			
			dayStart = UtilDateTime.getDayStart(monthIterDate, timeZone, locale);
			dayEnd = UtilDateTime.getDayEnd(monthIterDate, timeZone, locale);
			
			andExprs = FastList.newInstance();
			andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayStart)));
			andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd)));
			andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			List dayWiseIndentList = EntityUtil.filterByCondition(prodIndentList, andCond);
			
			andExprs.clear();
			andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart)));
			andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
			andCond1 = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			List dayWiseOrderItems = EntityUtil.filterByCondition(prodOrderItemsList, andCond1);
			
			/*andExprs.clear();
			andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart)));
			andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
			andCond2 = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			List dayWiseReceiptList = EntityUtil.filterByCondition(prodReceiptList, andCond2);*/
			
			for(k = 0; k < dayWiseIndentList.size(); k++){
				if(UtilValidate.isNotEmpty((dayWiseIndentList.get(k)).get("preRevisedQuantity"))){
					dayIndentQty = dayIndentQty.add((dayWiseIndentList.get(k)).get("preRevisedQuantity"));
					totalIndentQty = totalIndentQty.add((dayWiseIndentList.get(k)).get("preRevisedQuantity"));
				}
				else{
					dayIndentQty = dayIndentQty.add((dayWiseIndentList.get(k)).get("quantity"));
					totalIndentQty = totalIndentQty.add((dayWiseIndentList.get(k)).get("quantity"));
				}
			}
			for(l = 0; l < dayWiseOrderItems.size(); l++){
				dayDispatchQty = dayDispatchQty.add((dayWiseOrderItems.get(l)).get("quantity"));
				totalDispatchQty = totalDispatchQty.add((dayWiseOrderItems.get(l)).get("quantity"));
			}
			/*for(m = 0; m < dayWiseReceiptList.size(); m++){
				dayReceiptQty = dayReceiptQty.add((dayWiseReceiptList.get(m)).get("quantityAccepted"));
				totalReceiptQty = totalReceiptQty.add((dayWiseReceiptList.get(m)).get("quantityAccepted"));
			}*/
			
			dayIndent = "Day" + (j+1) + "_Indent";
			dayDespatch = "Day" + (j+1) + "_Despatch";
			
			prodIndentDespatchMap.put(dayIndent, dayIndentQty);
			//prodIndentDespatchMap.put(dayDespatch, (dayDispatchQty + dayReceiptQty));
			prodIndentDespatchMap.put(dayDespatch, (dayDispatchQty ));
			monthIterDate = UtilDateTime.addDaysToTimestamp(monthIterDate, 1);
		}
		
		prodIndentDespatchMap.put("totalIndent", totalIndentQty);
		prodIndentDespatchMap.put("totalDespatch", (totalDispatchQty ));
		//prodIndentDespatchMap.put("totalDespatch", (totalDispatchQty + totalReceiptQty));
		
		tempIndentVsDespMap = [:];
		tempIndentVsDespMap.putAll(prodIndentDespatchMap);
		
		indentVsDespatchList.addAll(tempIndentVsDespMap);
	}
	
	intervalDaysMap = [:];
	intervalDaysMap.putAt("intervalDays", intervalDays);
	
	context.put("columnTitleMap", columnTitleMap);
	context.put("indentVsDespatchList", indentVsDespatchList);
	context.put("intervalDaysMap", intervalDaysMap);
	
	

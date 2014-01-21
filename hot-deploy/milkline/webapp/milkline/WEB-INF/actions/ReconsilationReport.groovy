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

	import org.ofbiz.network.NetworkServices;
	import org.ofbiz.network.LmsServices;
	import org.ofbiz.product.product.ProductWorker;
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	
	effectiveDate = null;
	effectiveDateStr = parameters.indentDate;
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	context.put("effectiveDate", effectiveDate);
	
	dayBegin = UtilDateTime.getDayStart(effectiveDate, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate, timeZone, locale);

	lmsProductList=NetworkServices.getLmsProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	lmsProductList = EntityUtil.getFieldListFromEntityList(lmsProductList, "productId", true);
	
	conditionList=[];


	List shipmentIdsList = NetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIdsList));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
	conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL ,"ORDER_REJECTED"));
	orderCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	fieldsToSelect = ["productId","originFacilityId","parentFacilityId","quantity","unitPrice","shipmentId","categoryTypeEnum","shipmentTypeId"] as Set;
	boothOrderItemsList = delegator.findList("OrderHeaderItemProductShipmentAndFacility", orderCondition, fieldsToSelect , ["productId"], null, false);

	
	dispacthedBoothList= EntityUtil.getFieldListFromEntityList(boothOrderItemsList, "originFacilityId", false);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition([EntityCondition.makeCondition("subscriptionTypeId", EntityOperator.EQUALS,null),
		EntityCondition.makeCondition("subscriptionTypeId",EntityOperator.IN, UtilMisc.toList("AM","PM"))],EntityOperator.OR));//for AM PM and null also
	conditionList.add(EntityCondition.makeCondition("facilityId",  EntityOperator.IN,dispacthedBoothList));
	condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	indentList = delegator.findList("SubscriptionFacilityAndSubscriptionProduct", condition, null , ["productId"], null, false);
	indentList = EntityUtil.filterByDate(indentList, effectiveDate);
	
	routeProdDispatchAndDeliveredMap = [:];
	Map routesMap = NetworkServices.getRoutes(dctx , UtilMisc.toMap());//to get all LmsRoutes
	lmsRouteList = routesMap.getAt("routesList");
	
	for(int n=0; n< lmsRouteList.size();n++){
		prodDispatchAndDeliveredMap = [:];
	   curntRouteId=lmsRouteList.get(n);
	   
	   for(int i = 0; i < lmsProductList.size(); i++){
		BigDecimal totalDispatchQty = BigDecimal.ZERO;
		BigDecimal totalDeliveredQty = BigDecimal.ZERO;
		BigDecimal totalGatePassQty = BigDecimal.ZERO;
		
		productId = lmsProductList.get(i);
		List prodIndentList = EntityUtil.filterByAnd(indentList, UtilMisc.toMap("productId", productId,"parentFacilityId",curntRouteId));
		List prodOrderItemsList = EntityUtil.filterByAnd(boothOrderItemsList, UtilMisc.toMap("productId",productId ,"parentFacilityId",curntRouteId));
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.IN, ["PM_SHIPMENT_SUPPL","AM_SHIPMENT_SUPPL"]));
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,productId));
		conditionList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS,curntRouteId));

		List gatePassOrderItemsList = EntityUtil.filterByAnd(boothOrderItemsList,conditionList);
		for(j = 0; j < prodIndentList.size(); j++){
				totalDispatchQty = totalDispatchQty.add((prodIndentList.get(j)).get("quantity"));
		}
		for(k = 0; k < prodOrderItemsList.size(); k++){
			totalDeliveredQty = totalDeliveredQty.add((prodOrderItemsList.get(k)).get("quantity"));
		}
		for(m = 0; m < gatePassOrderItemsList.size(); m++){
			totalGatePassQty = totalGatePassQty.add((gatePassOrderItemsList.get(m)).get("quantity"));
		}
		
		dispatchAndDeliveredMap = [:];
		dispatchAndDeliveredMap["dispatchQty"] = totalDispatchQty.add(totalGatePassQty);
		dispatchAndDeliveredMap["deliveredQty"] = totalDeliveredQty;
		
		tempdispatchAndDeliveredMap = [:];
		tempdispatchAndDeliveredMap.putAll(dispatchAndDeliveredMap);
		
		prodDispatchAndDeliveredMap.put(productId, tempdispatchAndDeliveredMap);
		
	  }//end of each Route by LMS Products
	 tempRouteDispatchAndDeliveredMap = [:];
	 tempRouteDispatchAndDeliveredMap.putAll(prodDispatchAndDeliveredMap);
	
	 routeProdDispatchAndDeliveredMap.put(curntRouteId,tempRouteDispatchAndDeliveredMap)
	}
	
	context.put("routeProdDispatchAndDeliveredMap", routeProdDispatchAndDeliveredMap);


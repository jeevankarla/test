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

effectiveDate = null;
thruEffectiveDate = null;
thruEffectiveDateStr = null;
dayBegin = "";
dayEnd = "";
if (UtilValidate.isNotEmpty(reportTypeFlag)) {
	if(reportTypeFlag=="productReturnsReport"){
		effectiveDateStr = parameters.prodReturnDate;
		thruEffectiveDateStr = parameters.prodReturnTDateId;
		if (UtilValidate.isEmpty(effectiveDateStr)) {
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
			try {
				effectiveDate = new java.sql.Timestamp(dateFormat.parse(effectiveDateStr+" 00:00:00").getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDateStr, "");
			}
		}
		if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
			thruEffectiveDate = effectiveDate;
		}
		else{
			def sdf = new SimpleDateFormat("MMMM dd, yyyy");
			try {
				thruEffectiveDate = new java.sql.Timestamp(sdf.parse(thruEffectiveDateStr+" 00:00:00").getTime());
			}catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + thruEffectiveDateStr, "");
			}
		}
		dayBegin = UtilDateTime.getDayStart(effectiveDate);
		dayEnd = UtilDateTime.getDayEnd(thruEffectiveDate);
	}
}


// for sales Report
if (UtilValidate.isNotEmpty(reportTypeFlag)) {
	if(reportTypeFlag=="salesReport"){
		effectiveDateStr = parameters.fromDate;
		thruEffectiveDateStr = parameters.thruDate;
		if (UtilValidate.isEmpty(effectiveDateStr)) {
			effectiveDate = UtilDateTime.nowTimestamp();
		}
		else{
			def sdf = new SimpleDateFormat("MMMM dd, yyyy");
			try {
				effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
			}
		}
		if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
			thruEffectiveDate = effectiveDate;
		}
		else{
			def sdf = new SimpleDateFormat("MMMM dd, yyyy");
			try {
				thruEffectiveDate = new java.sql.Timestamp(sdf.parse(thruEffectiveDateStr+" 00:00:00").getTime());
			}catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
			}
		}
		dayBegin = UtilDateTime.getDayStart(effectiveDate);
		dayEnd = UtilDateTime.getDayEnd(thruEffectiveDate);
	}
}
totalDays=UtilDateTime.getIntervalInDays(dayBegin,dayEnd);
context.put("totalDays", totalDays+1);
context.put("effectiveDateStr",effectiveDateStr);
if(thruEffectiveDate){
	context.put("thruEffectiveDateStr",thruEffectiveDateStr);
}
returnProductList = [];
date = "";
boothId = "";
routeId = "";
shipmentTypeId = "";
productId = "";
returnQuantity = "";
returnReasonId = "";
returnPrice = "";
returnQtyIncluded = "";
returnQtyLtrs = "";

shipmentIds=[];
shipmentIdList = [];
conditionList=[];
	if(parameters.subscriptionTypeId == "ALL"){
		if(parameters.routeId == "All-Routes"){
			shipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,null);
			//shipmentIds  = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
			shipmentIdList.addAll(shipmentIds);
		}else{
		    conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, parameters.routeId));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
			conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
			EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		    shipment = delegator.findList("Shipment", cond, null, null, null, false);
		    shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		    shipmentIdList.addAll(shipmentIds);
	   }
	}else{
	   if(parameters.routeId == "All-Routes"){
		   shipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,parameters.subscriptionTypeId);
		   shipmentIdList.addAll(shipmentIds);
	   }else{
		   shipType = parameters.subscriptionTypeId+"_SHIPMENT";
		   conditionList.clear();
		   conditionList.add(EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, parameters.routeId));
		   conditionList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, shipType));
		   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
		   conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.GREATER_THAN_EQUAL_TO ,dayBegin));
		   conditionList.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
		   EntityCondition cond1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		   shipment = delegator.findList("Shipment", cond1, null, null, null, false);
		   shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		   shipmentIdList.addAll(shipmentIds);
	   }
	}
if(reportTypeFlag=="salesReport"){
	amShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"AM");
	shipmentIdList.addAll(amShipmentIds);
	pmShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,"PM");
	shipmentIdList.addAll(pmShipmentIds);
	
}
saleProductReturnMap=[:];
conditionList=[];
conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN, shipmentIdList));
conditionList.add(EntityCondition.makeCondition("returnStatusId", EntityOperator.EQUALS, "RETURN_ACCEPTED"));
returnCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
returnHeaderItemsList = delegator.findList("ReturnHeaderItemAndShipmentAndFacility", returnCondition, null, null, null, false);
if(UtilValidate.isNotEmpty(returnHeaderItemsList)){
	returnHeaderItemsList.each{ returnItem->
			productReturnMap = [:];
			date = returnItem.estimatedShipDate;
			boothId = returnItem.originFacilityId;
			routeId = returnItem.routeId;
			shipmentTypeId = returnItem.shipmentTypeId;
			productId = returnItem.productId;
			product = delegator.findOne("Product", ["productId" : productId], true);
			returnQtyIncluded = product.quantityIncluded;
			returnQuantity = returnItem.returnQuantity;
			if(UtilValidate.isNotEmpty(returnQtyIncluded)){
				returnQtyLtrs = (returnQuantity*returnQtyIncluded);
			}
			returnReasonId = returnItem.returnReasonId;
			userLogin = returnItem.createdBy;
			returnPrice = returnItem.returnPrice; 
			productReturnMap["date"]=returnItem.estimatedShipDate;
			productReturnMap["boothId"]=boothId;
			productReturnMap["routeId"]=routeId;
			productReturnMap["shipmentTypeId"]=shipmentTypeId;
			productReturnMap["productId"]=productId;
			productReturnMap["returnQuantity"]=returnQuantity;
			productReturnMap["returnReasonId"]=returnReasonId;
			productReturnMap["userLoginId"]= userLogin;
			//for sales report
			saleProductPriceMap = [:];
			saleProductPriceMap["returnQuantity"] = returnQuantity;
			saleProductPriceMap["returnQtyLtrs"] = returnQtyLtrs;
			saleProductPriceMap["returnPrice"] = returnPrice;
			saleProductReturnMap[productId] = saleProductPriceMap;
		returnProductList.add(productReturnMap);
	}
}

milkReturnQty = 0;
curdReturnQty = 0;
productReturnMap = [:];
returnProducts = EntityUtil.getFieldListFromEntityList(returnHeaderItemsList, "productId", true);
if(UtilValidate.isNotEmpty(returnProducts)){
	returnProducts.each{ eachProduct->
			returnProdList = EntityUtil.filterByCondition(returnHeaderItemsList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProduct));
			prodTotalQty = 0;
			returnPrice = 0;
			product = delegator.findOne("Product", ["productId" : eachProduct], true);
			productCategoryId = product.primaryProductCategoryId;
			returnQtyIncluded = product.quantityIncluded;
			retTempMap = [:];
			returnProdList.each{ eachProdReturnItem ->
				prodTotalQty = prodTotalQty+eachProdReturnItem.returnQuantity;
				if(eachProdReturnItem.returnPrice){
					returnPrice = returnPrice+(eachProdReturnItem.returnQuantity*eachProdReturnItem.returnPrice);
				}
				if("Milk".equals(productCategoryId)){
					milkReturnQty = milkReturnQty+((eachProdReturnItem.returnQuantity)*returnQtyIncluded);
				}
				if("Curd".equals(productCategoryId)){
					curdReturnQty = curdReturnQty+((eachProdReturnItem.returnQuantity)*returnQtyIncluded);
				}
			}
			retTempMap.returnQuantity = prodTotalQty;
			retTempMap.returnPrice = returnPrice;
			retTempMap.returnQtyLtrs = prodTotalQty*returnQtyIncluded;
			retTempMap.returnProdName = prodTotalQty*returnQtyIncluded;
			productReturnMap.put(eachProduct, retTempMap);
	}
}
returnProductList = UtilMisc.sortMaps(returnProductList, UtilMisc.toList("routeId"));
context.put("milkReturnQty",milkReturnQty);
context.put("curdReturnQty",curdReturnQty);
context.put("productReturnMap",productReturnMap);
context.put("saleProductReturnMap",saleProductReturnMap);
context.put("returnProductList",returnProductList);

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

if (UtilValidate.isNotEmpty(reportTypeFlag)) {
	if(reportTypeFlag=="productReturnsReport"){
		effectiveDateStr = parameters.prodReturnDate;
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
	}
}
dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(effectiveDate);

// for sales Report
if (UtilValidate.isNotEmpty(reportTypeFlag)) {
	if(reportTypeFlag=="salesReport"){
		effectiveDateStr = parameters.saleFromDate;
		thruEffectiveDateStr = parameters.saleThruDate;
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
	}
}

dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(thruEffectiveDate);

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
	if(parameters.subscriptionTypeId == "ALL"){
		if(parameters.routeId == "All-Routes"){
			shipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,null);
			//shipmentIds  = ByProductNetworkServices.getShipmentIds(delegator , UtilDateTime.toDateString(dayBegin, "yyyy-MM-dd HH:mm:ss"),null);
			shipmentIdList.addAll(shipmentIds);
		}else{
		   shipment = delegator.findList("Shipment", EntityCondition.makeCondition([routeId : parameters.routeId, statusId: "GENERATED", estimatedShipDate : dayBegin]), null, null, null, false);
		   shipmentIds = EntityUtil.getFieldListFromEntityList(shipment, "shipmentId", true);
		   shipmentIdList.addAll(shipmentIds);
	   }
	}else{
	   if(parameters.routeId == "All-Routes"){
		   shipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,dayBegin,dayEnd,parameters.subscriptionTypeId);
		   shipmentIdList.addAll(shipmentIds);
	   }else{
		   shipType = parameters.subscriptionTypeId+"_SHIPMENT";
		   shipment = delegator.findList("Shipment", EntityCondition.makeCondition([routeId : parameters.routeId, shipmentTypeId: shipType, statusId: "GENERATED", estimatedShipDate : dayBegin]), null, null, null, false);
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
returnHeaderItemsList = delegator.findList("ReturnHeaderItemAndShipment", returnCondition, null, null, null, false);
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
			productReturnMap["date"]=dayBegin;
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
productReturnMap = [:];
returnProducts = EntityUtil.getFieldListFromEntityList(returnHeaderItemsList, "productId", true);
if(UtilValidate.isNotEmpty(returnProducts)){
	returnProducts.each{ eachProduct->
			returnProductList = EntityUtil.filterByCondition(returnHeaderItemsList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachProduct));
			prodTotalQty = 0;
			returnPrice = 0;
			product = delegator.findOne("Product", ["productId" : eachProduct], true);
			returnQtyIncluded = product.quantityIncluded;
			
			retTempMap = [:];
			returnProductList.each{ eachProdReturnItem ->
				prodTotalQty = prodTotalQty+eachProdReturnItem.returnQuantity;
				if(eachProdReturnItem.returnPrice){
					returnPrice = returnPrice+(eachProdReturnItem.returnQuantity*eachProdReturnItem.returnPrice);
				}
				
			}
			retTempMap.returnQuantity = prodTotalQty;
			retTempMap.returnPrice = returnPrice;
			retTempMap.returnQtyLtrs = prodTotalQty*returnQtyIncluded;
			productReturnMap.put(eachProduct, retTempMap);
	}
}
returnProductList = UtilMisc.sortMaps(returnProductList, UtilMisc.toList("routeId"));
context.put("productReturnMap",productReturnMap);
context.put("saleProductReturnMap",saleProductReturnMap);
context.put("returnProductList",returnProductList);


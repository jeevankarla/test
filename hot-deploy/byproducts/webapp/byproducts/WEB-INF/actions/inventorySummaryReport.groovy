import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator.*;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.text.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

fromDate = null;
thruDate = null;

if(parameters.fromDate && parameters.thruDate){
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		fromDate = new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime());
		thruDate = new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+ fromDate, module);
		return "error";
	} catch (NullPointerException e) {
		Debug.logError(e, "Cannot parse date string: "+ fromDate, module);
		return "error";
	}
}
else{
	fromDate = UtilDateTime.nowTimestamp();
	thruDate = UtilDateTime.nowTimestamp();
}

fromDate = UtilDateTime.getDayStart(fromDate);
thruDate = UtilDateTime.getDayEnd(thruDate);


facilityList = [];
productList = [];
if(parameters.facilityId){
	facilityList.add(parameters.facilityId);
}
else{
	facilityList = ByProductServices.getByproductParlours(delegator).get("parlourIdsList");
}
if(parameters.productId){
	productList.add(parameters.productId);
}
else{
	productList =ByProductNetworkServices.getByProductProducts(dispatcher.getDispatchContext(), UtilMisc.toMap());
	productList = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
}

conditionList=[];
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityList));
conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
inventorySummReport = delegator.findList("InventorySummary", condition, null , ["-saleDate","facilityId","productId"], null, false);
inventoryFinalList = [];
if(inventorySummReport){
	inventorySummReport.each{eachInv ->
		inventoryMap = [:];
		inventoryMap.putAt("facilityId",eachInv.facilityId);
		inventoryMap.putAt("productId",eachInv.productId);
		inventoryMap.putAt("salesQty",eachInv.sales);
		inventoryMap.putAt("saleDate",eachInv.saleDate);
		inventoryMap.putAt("receipts",eachInv.receipts);
		inventoryMap.putAt("xferIn",eachInv.xferIn);
		inventoryMap.putAt("xferOut",eachInv.xferOut);
		inventoryMap.putAt("adjustments",eachInv.adjustments);
		inventoryMap.putAt("openingBalQty",eachInv.openingBalance);
		inventoryMap.putAt("quantityOnHandTotal",eachInv.closingBalance);
		inventoryFinalList.add(inventoryMap);				
	}
}
context.inventorySummReport = inventoryFinalList;
products = delegator.findList("Product", null, ["productId", "productName"]as Set, null, null, false);
productNames = [:];
if(products){
	products.each{ eachProd ->
		productNames.putAt(eachProd.productId, eachProd.productName);
	}
}
context.productNames = productNames;

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

effectiveDate = null;
conditionList=[];
productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");

if(parameters.salesDate){
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		effectiveDate = new java.sql.Timestamp(sdf.parse(parameters.salesDate).getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: "+ effectiveDate, module);
		return "error";
	} catch (NullPointerException e) {
		Debug.logError(e, "Cannot parse date string: "+ effectiveDate, module);
		return "error";
	}
}
else{
	effectiveDate = UtilDateTime.nowTimestamp();
}
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
inventorySummReport = [];
inventoryFinalList = [];
conditionList = [];
if(parameters.action == "SEARCH"){
	facilityList.each{eachFacility ->
		productList.each{eachprod ->
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachprod));
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachFacility));
			conditionList.add(EntityCondition.makeCondition("saleDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			inventorySummReport = delegator.findList("InventorySummary", condition, null , ["-saleDate"], null, false);
			if(inventorySummReport){
				singleReport = inventorySummReport.get(0);
				inventoryMap = [:];
				unitPrice = BigDecimal.ZERO;
				totalValue = BigDecimal.ZERO;
				inventoryMap.putAt("facilityId",singleReport.facilityId);
				inventoryMap.putAt("productId",singleReport.productId);
				inventoryMap.putAt("salesQty",singleReport.sales);
				if(parameters.salesDate){
					inventoryMap.putAt("saleDate",effectiveDate);
				}
				else{
					inventoryMap.putAt("saleDate",UtilDateTime.nowTimestamp());
				}
				inventoryMap.putAt("receipts",singleReport.receipts);
				inventoryMap.putAt("xferIn",singleReport.xferIn);
				inventoryMap.putAt("xferOut",singleReport.xferOut);
				inventoryMap.putAt("adjustments",singleReport.adjustments);
				inventoryMap.putAt("openingBalQty",singleReport.openingBalance);
				inventoryMap.putAt("quantityOnHandTotal",singleReport.closingBalance);
				priceContext = [:];
				priceResult = [:];
				Map<String, Object> priceResult;
				priceContext.put("userLogin", userLogin);
				priceContext.put("productStoreId", productStoreId);
				priceContext.put("productId", singleReport.productId);
				priceContext.put("priceDate", singleReport.saleDate);
				priceContext.put("productPriceTypeId", "PM_RC_P_PRICE");
				priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
				if(ServiceUtil.isSuccess(priceResult)){
					unitPrice = (BigDecimal)priceResult.get("price");
				}
				totalValue = unitPrice.multiply(singleReport.closingBalance);
				inventoryMap.putAt("totalValue",totalValue);
				inventoryFinalList.add(inventoryMap);
			}
		}
	}
}

context.inventoryFinalList = inventoryFinalList;
		

	

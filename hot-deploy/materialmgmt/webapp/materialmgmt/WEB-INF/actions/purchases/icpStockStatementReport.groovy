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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;



dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
//categoryType=parameters.categoryType;
//context.put("categoryType",categoryType);
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}

fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = fromDateTime;
context.thruDate = thruDateTime;
//totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);


salesInvoiceTotals = SalesInvoiceServices.getPeriodSalesInvoiceTotals(dctx, [isQuantityLtrs:true,fromDate:dayBegin, thruDate:dayEnd]);
prodTotals=salesInvoiceTotals.get("productTotals");
productCategoryMap = [:];
	if(UtilValidate.isNotEmpty(prodTotals)){
			
			//for category wise 
			milkAverageTotal = 0;
			curdAverageTotal = 0;
			milkSaleTotal = 0;
			curdSaleTotal = 0;
			productCategoryMap = [:];
			productReturnMap = context.get("productReturnMap");
			prodTotals.each{ productValue ->
				if(UtilValidate.isNotEmpty(productValue)){
					currentProduct = productValue.getKey();
					returnProdMap=[:];
					product = delegator.findOne("Product", [productId : currentProduct], false);
					
					virtualProductId = currentProduct;
					tempVariantMap =[:];
					
					productAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", EntityCondition.makeCondition(["productAssocTypeId": "PRODUCT_VARIANT", "productIdTo": currentProduct,"thruDate":null]), null, ["-fromDate"], null, false));
					
					if(UtilValidate.isNotEmpty(productAssoc)){
						virtualProductId = productAssoc.productId;
					}
					if(UtilValidate.isEmpty(tempVariantMap[virtualProductId])){
						
						tempMap = [:];
						tempProdMap = [:];
						tempProdMap["quantity"] = productValue.getValue().get("total");
						tempProdMap["revenue"] = productValue.getValue().get("totalRevenue");
						tempMap[currentProduct] = tempProdMap;
						tempVariantMap[virtualProductId] = tempMap;
					}else{
						tempMap = [:];
						productQtyMap = [:];
						tempMap.putAll(tempVariantMap.get(virtualProductId));
						productQtyMap.putAll(tempMap);
						productQtyMap["quantity"] += productValue.getValue().get("total");
						productQtyMap["revenue"] += productValue.getValue().get("totalRevenue");
						tempMap[currentProduct] = productQtyMap;
						tempVariantMap[virtualProductId] = tempMap;
					}
					
					if(UtilValidate.isEmpty(productCategoryMap[product.primaryProductCategoryId])){
						productCategoryMap.put(product.primaryProductCategoryId,tempVariantMap);
					}else{
						tempCatMap = [:];
						tempCatMap.putAll(productCategoryMap[product.primaryProductCategoryId]);
						if(UtilValidate.isEmpty(tempCatMap[virtualProductId])){
							tempMap = [:];
							tempProdMap = [:];
							tempProdMap["quantity"] = productValue.getValue().get("total");
							tempProdMap["revenue"] = productValue.getValue().get("totalRevenue");
							tempMap[currentProduct] = tempProdMap;
							tempCatMap[virtualProductId] = tempMap;
						}else{
							tempMap = [:];
							tempMap.putAll(tempCatMap.get(virtualProductId));
								if(UtilValidate.isEmpty(tempMap.get(currentProduct))){
									currentTempMap = [:];
									currentTempMap["quantity"] = productValue.getValue().get("total");
									currentTempMap["revenue"] = productValue.getValue().get("totalRevenue");
									tempMap[currentProduct] = currentTempMap;
								}else{
									currentTempMap = [:];
									currentTempMap["quantity"] += productValue.getValue().get("total");
									currentTempMap["revenue"] += productValue.getValue().get("totalRevenue");
									tempMap[currentProduct] = currentTempMap;
								}
							tempCatMap[virtualProductId] = tempMap;
						}
						productCategoryMap.put(product.primaryProductCategoryId,tempCatMap);
					}
					
					
				}
			}
		
			context.putAt("productCategoryMap", productCategoryMap);

		
		}
	




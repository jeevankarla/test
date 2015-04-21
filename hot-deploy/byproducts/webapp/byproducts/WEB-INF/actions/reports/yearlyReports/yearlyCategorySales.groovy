
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.byproducts.SalesHistoryServices;

routeIdsList = [];
dctx = dispatcher.getDispatchContext();

effectiveDate = null;
thruEffectiveDate = null;

dayBegin = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
dayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());


// for sales Report
if (UtilValidate.isNotEmpty(reportTypeFlag)) {
	if(reportTypeFlag=="salesReport"){
		fromDateTime = null;
		thruDateTime = null;
		fromDate = parameters.fromDate;
		Debug.log("fromDate11=="+fromDate);
		thruDate = parameters.thruDate;
		Debug.log("thruDate11=="+thruDate);
		def sdf = new SimpleDateFormat("MMMM dd, yyyy");
		if (UtilValidate.isNotEmpty(fromDate)) {
			try {
				fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate+" 00:00:00").getTime());
			} catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + fromDateTime, "");
			}
		}
			if (UtilValidate.isNotEmpty(thruDate)) {
			try {
				thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate+" 00:00:00").getTime());
			}catch (ParseException e) {
				Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
			}
		}
		//fromDateTime = UtilDateTime.getMonthStart(fromDateTime);
		//thruDateTime = UtilDateTime.getMonthEnd(thruDateTime,TimeZone.getDefault(),Locale.getDefault());;
		dayBegin = UtilDateTime.getDayStart(fromDateTime);
		Debug.log("dayBegin11=="+dayBegin);
		dayEnd = UtilDateTime.getDayEnd(thruDateTime);
		Debug.log("dayEnd11=="+dayEnd);
		context.fromDate = fromDate;
		context.thruDate = thruDate;

	}
}
context.putAt("dayBegin", dayBegin);
context.putAt("dayEnd", dayEnd);
totLmsQty=0;
grandTotalRevenue=0;
	  dayTotals=[:];
	  if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag=="salesReport") {
		  boothsList=ByProductNetworkServices.getBoothList(delegator ,null);
		  //dayTotals = SalesHistoryServices.getSalesSummaryPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(boothsList),fromDate:dayBegin, thruDate:dayEnd,includeReturnOrders:true,"periodTypeId":"SALES_MONTH"]);	
		    dayTotals = SalesHistoryServices.getSalesDayPeriodTotals(dispatcher.getDispatchContext(), [facilityIds:UtilMisc.toList(boothsList),fromDate:dayBegin, thruDate:dayEnd,includeReturnOrders:true,"periodTypeId":"SALES_DAY"]);
		   }	 
	  if(UtilValidate.isNotEmpty(dayTotals)){
			//Debug.log("dayTotals====="+dayTotals.get("dayWiseTotals"));
				countMap=[:];
				if(UtilValidate.isNotEmpty(dayTotals.get("dayWiseTotals"))){
					curntDaySalesMap=dayTotals.get("dayWiseTotals").entrySet();
					//curntDaySalesMap1=dayperiodTotals.get("dayWiseTotals").entrySet();
					curntDaySalesMap.each{daySalesMap->
					if(UtilValidate.isNotEmpty(daySalesMap.getValue())){
						productTotList=daySalesMap.getValue().get("productTotals");
						productTotList.each{ product->
							count=0;
							productId=product.getKey();
							if(UtilValidate.isEmpty(countMap[productId])){
							  countMap.put(productId, count+1);
							}else{
							   val=countMap.get(productId);
							   count=val+1;
							   countMap.put(productId, count);
							}
						}
					 }						
					}//curnt Day caliculation
				}
		context.putAt("countMap", countMap);
		prodTotals = dayTotals.get("productTotals");
		//Debug.log("prodTotals=============="+prodTotals);
		grandTotalRevenue=dayTotals.get("totalRevenue");
		if(UtilValidate.isNotEmpty(prodTotals)){
			// for sale report
			Set prodKeys = prodTotals.keySet();
			productList = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.IN, prodKeys) , null, null, null, false);
			productList = UtilMisc.sortMaps(productList, UtilMisc.toList("brandName"));
			productValueMap = [:];
			productList.each{ product->
				if(UtilValidate.isNotEmpty(prodTotals.get(product.productId))){
					prodMap = prodTotals.get(product.productId);
					productValueMap.put(product.productId,prodMap);
				}
			}
			context.putAt("saleProductTotals", productValueMap);
			context.putAt("grandProdTotals", prodTotals);
			
			//for category wise sales report
			milkAverageTotal = 0;
			curdAverageTotal = 0;
			milkSaleTotal = 0;
			curdSaleTotal = 0;
			count=0;
			productCategoryMap = [:];
			productReturnMap = context.get("productReturnMap");
			productValueMap.each{ productValue ->
				if(UtilValidate.isNotEmpty(productValue)){
					currentProduct = productValue.getKey();
					returnProdMap=[:];
					if(UtilValidate.isNotEmpty(countMap)){
					   noOfDays=countMap.get(currentProduct);
					}
					if(UtilValidate.isNotEmpty(productReturnMap)){
					  returnProdMap=productReturnMap.get(currentProduct);
					}
					product = delegator.findOne("Product", [productId : currentProduct], false);
					tempVariantMap =[:];
					productAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", EntityCondition.makeCondition(["productAssocTypeId": "PRODUCT_VARIANT", "productIdTo": currentProduct,"thruDate":null]), null, ["-fromDate"], null, false));
					virtualProductId = currentProduct;
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
					if("Milk".equals(product.primaryProductCategoryId)){
						returnQty=0;
						saleProdQty=0;
						if(UtilValidate.isNotEmpty(returnProdMap)){
						  returnQty=returnProdMap.get("returnQtyLtrs");
						}else{
						  returnQty=0;
						}
						saleProdQty=productValue.getValue().get("total")-returnQty;
						if(noOfDays!=0){
							quantity=saleProdQty/noOfDays;
						}
						milkSaleTotal=milkSaleTotal+quantity;
					}
					if("Curd".equals(product.primaryProductCategoryId)){
						returnQty=0;
						saleProdQty=0;
						if(UtilValidate.isNotEmpty(returnProdMap)){
						  returnQty=returnProdMap.get("returnQtyLtrs");
						}else{
						  returnQty=0;
						}
						saleProdQty=productValue.getValue().get("total")-returnQty;
						if(noOfDays!=0){
							quantity=saleProdQty/noOfDays;
						}
						curdSaleTotal=curdSaleTotal+quantity;
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
			milkAverageTotal = milkSaleTotal;
			curdAverageTotal = curdSaleTotal;
			context.putAt("productCategoryMap", productCategoryMap);
			context.putAt("milkAverageTotal", milkAverageTotal);
			context.putAt("curdAverageTotal", curdAverageTotal);						
		}
	}


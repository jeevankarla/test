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
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductReportServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

/*fromDateTime=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -9);
thruDateTime=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -9);*/

month = UtilDateTime.toDateString(fromDateTime, "MMMMM-yyyy");
context.putAt("month", month);

monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
dctx = dispatcher.getDispatchContext();
facilityDetail = ByProductNetworkServices.getByProductFacilityCategoryAndClassification(delegator, monthBegin);
distinctClassification = delegator.findList("PartyClassification", null, null, null, null, false);
distinctClassification = EntityUtil.getFieldListFromEntityList(distinctClassification, "partyClassificationGroupId", true);
priceClassificationMap = [:];
priceMap = [:];
if(distinctClassification){
	distinctClassification.each{eachClass ->
		priceMap = ByProductReportServices.getByProductPricesForPartyClassification(dctx, UtilMisc.toMap("userLogin", userLogin, "partyClassificationId", eachClass)).get("productsPrice");
		priceClassificationMap.putAt(eachClass, priceMap);
	}
}
products = delegator.findList("Product", null, null, null, null, false);
productNames = [:];
if(products){
	products.each{ eachProd ->
		productNames.putAt(eachProd.productId, eachProd.productName);
	}
	context.productNames = productNames;
}
TreeMap productMap = new TreeMap();
dayWiseSale = ByProductNetworkServices.getByProductSales(dctx, monthBegin, monthEnd, null, null, null, null).get("datewiseSales");
//dayWiseParlourDespatch = ByProductNetworkServices.getByProductParlourDespatch(dctx, monthBegin, monthEnd).get("totalDespatch");
if(dayWiseSale){
	eachDaySale = dayWiseSale.getAt(0);
	Iterator eachDaySaleIter = eachDaySale.entrySet().iterator();
	while (eachDaySaleIter.hasNext()) {
		Map.Entry daySaleEntry = eachDaySaleIter.next();
		daywiseFacilitySale = daySaleEntry.getValue();
		Iterator daywiseFacilitySaleIter = daywiseFacilitySale.entrySet().iterator();
		while (daywiseFacilitySaleIter.hasNext()) {
			Map.Entry daywiseFacilitySaleEntry = daywiseFacilitySaleIter.next();
			boothId = daywiseFacilitySaleEntry.getKey();
			boothCategory = facilityDetail.get(boothId.toUpperCase());
			if(!boothCategory){
				boothCategory = facilityDetail.get(boothId.toLowerCase());
			}
			categoryType = boothCategory.get("categoryTypeEnum");
			
			classification = boothCategory.get("partyClassification");
			productQuant = daywiseFacilitySaleEntry.getValue(); 
			Iterator productQuantIter = productQuant.entrySet().iterator();
			while (productQuantIter.hasNext()) {
				categoryMap = [:];
				Map.Entry productQuantEntry = productQuantIter.next();
				productId = productQuantEntry.getKey();
				quantity = productQuantEntry.getValue();
				if(productMap.containsKey(productId)){
					tempProdCategory = [:];
					tempProdCategory = productMap.get(productId);
					if(tempProdCategory){
						if(tempProdCategory.containsKey(categoryType)){
							updateQtyValue = [:];
							tempProdQty = tempProdCategory.get(categoryType);
							tempQty = tempProdQty.get("quantity");
							tempQty = tempQty.add(quantity);
							updateQtyValue.putAt("quantity", tempQty);
							pricesMap = priceClassificationMap.get(classification);
							price = pricesMap.get(productId);
							totalValue = price.get("totalAmount");
							totalAmount = totalValue.multiply(tempQty);
							updateQtyValue.putAt("value", totalAmount);
							tempCatMap = [:];
							tempCatMap.putAt(categoryType, updateQtyValue);
							tempProdCategory.putAll(tempCatMap);
						}else{
							tempMap = [:];
							tempCategoryMap = [:];
							tempMap.putAt("quantity", quantity);
							pricesMap = priceClassificationMap.get(classification);
							price = pricesMap.get(productId);
							totalValue = price.get("totalAmount");
							totalAmount = totalValue.multiply(quantity);
							tempMap.putAt("value", totalAmount);
							tempCategoryMap.putAt(categoryType, tempMap);
							tempProdCategory.putAll(tempCategoryMap);
						}
					}
					productMap.putAt(productId, tempProdCategory);
				}else{
					tempMap = [:];
					categoryMap = [:];
					tempMap.putAt("quantity", quantity);
					priceMap = priceClassificationMap.get(classification);
					prices = priceMap.get(productId);
					totalValue = prices.get("totalAmount");
					totalAmount = totalValue.multiply(quantity);
					tempMap.putAt("value", totalAmount);
					categoryMap.putAt(categoryType, tempMap);
					productMap.putAt(productId, categoryMap);
				}
			}
		}	
	}
}
/*if(dayWiseParlourDespatch){
	category = "PARLOUR";
	Iterator dayWiseParlourDespatchIter = dayWiseParlourDespatch.entrySet().iterator();
	while (dayWiseParlourDespatchIter.hasNext()) {
		tempCategoryMap = [:];
		temp = [:];
		tempProductCategory = [:];
		Map.Entry productQuantEntry = dayWiseParlourDespatchIter.next();
		prodId = productQuantEntry.getKey();
		quant = productQuantEntry.getValue();
		parlourPriceMap = priceClassificationMap.get("PM_RC_P");
		prices = parlourPriceMap.get(prodId);
		totalValue = prices.get("totalAmount");
		totalAmount = totalValue.multiply(quant);
		temp.putAt("quantity", quant);
		temp.putAt("value", totalAmount);
		tempCategoryMap.putAt("PARLOUR", temp);
		if(productMap.containsKey(prodId)){
			tempProductCategory = productMap.get(prodId);
			tempProductCategory.putAll(tempCategoryMap);
			productMap.putAt(prodId, tempProductCategory);
		}else{
			productMap.putAt(prodId, tempCategoryMap);
		}
	}
}*/
context.productMap = productMap;
conditionList = [];
facilityCategoryList = [];
List conditionList= FastList.newInstance();
categoryNotIn = ["SHOPS","RENTAL","WHOLESALES_DEALER"];
conditionList.add(EntityCondition.makeCondition("enumId", EntityOperator.NOT_IN, categoryNotIn));
conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "BOOTH_CAT_TYPE"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
facilityCatEnum = delegator.findList("Enumeration", condition, null, ["sequenceId"], null, false);
facilityCategoryList = EntityUtil.getFieldListFromEntityList(facilityCatEnum, "enumId", false);
context.facilityCategoryList = facilityCategoryList;


productDespatchList = [];

serialNo = 1
Iterator productMapIter = productMap.entrySet().iterator();
while (productMapIter.hasNext()) {
	Map.Entry productMapEntry = productMapIter.next();
	
	productId = productMapEntry.getKey();
	categoryWiseDispatchMap = productMapEntry.getValue();
	
	productDispatchMap = [:];
	productDispatchMap.put("sNo", serialNo);
	productDispatchMap.put("productId", productId);
	productDispatchMap.put("productName", productNames.get(productId));
	
	totalDispatchQty = BigDecimal.ZERO;
	totalDispatchValue = BigDecimal.ZERO;
	
	for(i=0; i<facilityCategoryList.size(); i++){
		categoryWiseDetails = categoryWiseDispatchMap.get(facilityCategoryList.get(i));
		
		dispatchQtyKey = facilityCategoryList.get(i)+"_Qty"
		dispatchValueKey = facilityCategoryList.get(i)+"_Val"
		
		if(UtilValidate.isEmpty(categoryWiseDetails)){
			productDispatchMap.put(dispatchQtyKey, BigDecimal.ZERO);
			productDispatchMap.put(dispatchValueKey, BigDecimal.ZERO);
		}
		else{
			totalDispatchQty = totalDispatchQty.add(categoryWiseDetails.get("quantity"));
			totalDispatchValue = totalDispatchValue.add(categoryWiseDetails.get("value"));
			
			productDispatchMap.put(dispatchQtyKey, categoryWiseDetails.get("quantity"));
			productDispatchMap.put(dispatchValueKey, categoryWiseDetails.get("value"));
		}
	}
	productDispatchMap.put("totalDispatchQty", totalDispatchQty);
	productDispatchMap.put("totalDispatchValue", totalDispatchValue);
	
	tempDespatchMap = [:];
	tempDespatchMap.putAll(productDispatchMap);
	
	productDespatchList.addAll(tempDespatchMap);
	serialNo = serialNo + 1;
	
}
context.productDespatchList = productDespatchList;
Debug.log("productDespatchList==================================================="+productDespatchList);














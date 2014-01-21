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
import org.ofbiz.entity.condition.EntityFunction;
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

reportFlag = context.getAt("reportFlag");

productStoreId = ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

context.putAt("fromDateTime", fromDateTime);

monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);

dctx = dispatcher.getDispatchContext();
conditionList=[];
daysList =[];
intervelDays = (UtilDateTime.getIntervalInDays(monthBegin, monthEnd)+1);
for(k =1;k<=intervelDays;k++){
	day = UtilDateTime.getDayOfMonth(UtilDateTime.addDaysToTimestamp(monthBegin, k-1), timeZone, locale);
	daysList.add(day);
}
context.put("daysList",daysList);
dayWiseSale=[:];
if(reportFlag == "ParlourSales"){
	dayWiseSale = ByProductNetworkServices.getByProductSales(dctx, monthBegin, monthEnd, null, "PARLOUR", null, null).get("datewiseSales");
}
else{
	dayWiseSale = ByProductNetworkServices.getByProductParlourDespatch(dctx, monthBegin, monthEnd).get("datewiseDespatch");
}

finalBoothSaleMap = new TreeMap(); 
if(dayWiseSale){
	eachDaySale = dayWiseSale.getAt(0);
	Iterator eachDaySaleIter = eachDaySale.entrySet().iterator();
	while (eachDaySaleIter.hasNext()) {
		Map.Entry daySaleEntry = eachDaySaleIter.next();
		saleDate = daySaleEntry.getKey();
		dayOfMonth = UtilDateTime.getDayOfMonth(saleDate, timeZone, locale);
		daywiseFacilitySale = daySaleEntry.getValue();
		if(daywiseFacilitySale){
			Iterator daywiseFacilitySaleIter = daywiseFacilitySale.entrySet().iterator();
			while (daywiseFacilitySaleIter.hasNext()) {
				Map.Entry daywiseFacilitySaleEntry = daywiseFacilitySaleIter.next();
				boothId = daywiseFacilitySaleEntry.getKey();
				productQuant = daywiseFacilitySaleEntry.getValue();
				Iterator productQuantIter = productQuant.entrySet().iterator();
				while (productQuantIter.hasNext()) {
					Map.Entry productQuantEntry = productQuantIter.next();
					productId = productQuantEntry.getKey();
					
					quantity = productQuantEntry.getValue();
					if(finalBoothSaleMap.containsKey(boothId)){
						tempSaleDetail = finalBoothSaleMap.get(boothId);
						if(tempSaleDetail.containsKey(productId)){
							tempDayMap = tempSaleDetail.get(productId);
							if(tempDayMap.containsKey(dayOfMonth)){
								tempQuant = tempDayMap.get(dayOfMonth);
								totalQuant = tempQuant.add(quantity);
								tempDayQty = new TreeMap();
								tempProduct = new TreeMap();
								tempDayMap.putAt(dayOfMonth, totalQuant);
								tempSaleDetail.putAt(productId,tempDayMap);
								finalBoothSaleMap.putAt(boothId, tempSaleDetail);
							}
							else{
								tempDayMap.putAt(dayOfMonth, quantity);
								tempSaleDetail.putAt(productId,tempDayMap);
								finalBoothSaleMap.putAt(boothId, tempSaleDetail);
							}
						}
						else{
							tempNewProd = new TreeMap();
							tempNewProd.putAt(dayOfMonth, quantity);
							tempSaleDetail.putAt(productId, tempNewProd);
							finalBoothSaleMap.putAt(boothId, tempSaleDetail);
						}
					}
					else{
						tempProdMap = new TreeMap();
						tempDayQtyMap = new TreeMap();
						tempDayQtyMap.putAt(dayOfMonth, quantity);
						tempProdMap.putAt(productId, tempDayQtyMap);
						finalBoothSaleMap.putAt(boothId, tempProdMap);
					}
				}
			}
		}
	}
}

initializingDayMap = [:];
for(i=0;i<daysList.size();i++){
	initializingDayMap.putAt(daysList[i], BigDecimal.ZERO);
}


if(finalBoothSaleMap){
	Iterator parlourSaleIter = finalBoothSaleMap.entrySet().iterator();
	while (parlourSaleIter.hasNext()) {
		Map.Entry productSaleEntry = parlourSaleIter.next();
		parlourId = productSaleEntry.getKey();
		productSale = productSaleEntry.getValue();
		Iterator productSaleIter = productSale.entrySet().iterator();
		while (productSaleIter.hasNext()) {
			Map.Entry daywiseSaleEntry = productSaleIter.next();
			dayUpdateMap = [:];
			dayUpdateMap.putAll(initializingDayMap);
			productId = daywiseSaleEntry.getKey();
			daywiseSale = daywiseSaleEntry.getValue();
			tempList = [];
			Iterator daySaleIter = daywiseSale.entrySet().iterator();
			while (daySaleIter.hasNext()) {
				Map.Entry daySaleEntry = daySaleIter.next();
				day = daySaleEntry.getKey();
				qty = daySaleEntry.getValue();
				dayUpdateMap.putAt(day, qty)
			}
			
			prodSale = [:];
			prodSale = finalBoothSaleMap.get(parlourId);
			prodSale.putAt(productId, dayUpdateMap);
			finalBoothSaleMap.putAt(parlourId, prodSale);
		}
	}
}
context.parlourSalesMap = finalBoothSaleMap;
priceMap = ByProductReportServices.getByProductPricesForPartyClassification(dctx, UtilMisc.toMap("userLogin", userLogin, "partyClassificationId", "PM_RC_P")).get("productsPrice");
context.priceMap = priceMap;
context.reportFlag = reportFlag;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS, "PARLOUR"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
parlourList = delegator.findList("Facility", condition, ["facilityId","facilityName"] as Set, null, null, false);
parlourDescription = [:];
if(parlourList){
	parlourList.each{eachParlour ->
		parlourDescription.putAt(eachParlour.facilityId, eachParlour.facilityName);
	}
}
context.parlourDescription = parlourDescription;

productsList = ByProductNetworkServices.getByProductProducts(dctx, UtilMisc.toMap());
productDescription = [:];
if(productsList){
	productsList.each{eachProduct ->
		productDescription.put(eachProduct.productId, eachProduct.productName);
	}
}
context.productDescription = productDescription;

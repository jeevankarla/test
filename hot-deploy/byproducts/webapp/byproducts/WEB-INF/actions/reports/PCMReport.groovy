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
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import org.ofbiz.party.party.PartyHelper;



dctx = dispatcher.getDispatchContext();
customTimePeriodId = parameters.customTimePeriodId;
customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);

fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
context.put("dayBegin",fromDate);
printDate = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "dd/MM/yyyy");
context.printDate = printDate;
exprList=[];
exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, "Milk"));
exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
  EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, fromDate)));
  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
	prodIdsList=EntityUtil.getFieldListFromEntityList(prodList, "productId", false);
	
	//Debug.log("=====prodIdsList===="+prodIdsList);
	
facilityList = (List)((Map)ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator,null,fromDate)).get("boothActiveList");
shipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,null);
curntMonthDays= UtilDateTime.getIntervalInDays(fromDate,thruDate)+1;
Debug.log("curntMonthDays===="+curntMonthDays);
categoryTotalMap = [:];
categorysList = [];
facilityCurntSaleMap=[:];
facCount=1;
if(UtilValidate.isNotEmpty(shipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds, fromDate:fromDate, thruDate:thruDate,includeReturnOrders:true,isByParty:true]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		boothTotalsMap=dayTotals.get("boothTotals");
		if(UtilValidate.isNotEmpty(boothTotalsMap)){
			boothTotalsMap.each { booth->
				facilityId=booth.getKey();
				categoryType=booth.getValue().get("categoryTypeEnum");
				boothInnerMap=[:];
				milkAverageTotal = 0;
				milkSaleTotal = 0;
					prodTotals = booth.getValue().get("productTotals");
					if(UtilValidate.isNotEmpty(prodTotals)){
						prodTotals.each{ productValue ->
							if(UtilValidate.isNotEmpty(productValue)){
								currentProduct = productValue.getKey();
								if(prodIdsList.contains(currentProduct)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}
								/*product = delegator.findOne("Product", [productId : currentProduct], false);
								if("Milk".equals(product.primaryProductCategoryId)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}*/
							}
						}
						
						}
					boothInnerMap["facilityId"]=facilityId;
					boothInnerMap["categoryType"]=categoryType;
					boothInnerMap["milkSaleTotal"]=milkSaleTotal;
					boothInnerMap["milkAvgTotal"]=(milkSaleTotal/curntMonthDays);
					facilityCurntSaleMap[facilityId]=boothInnerMap;
					
					if(categoryTotalMap.containsKey(categoryType)){
						tempCatList = categoryTotalMap.get(categoryType);
						tempCatList.addAll(boothInnerMap);
						categoryTotalMap.putAt(categoryType, tempCatList);
					}else{
						tempList = [];
						tempList.add(boothInnerMap);
						categoryTotalMap.putAt(categoryType, tempList);
						categorysList.add(categoryType);
					}
					
					}
		}
	}
  }


//Debug.log("facilityCurntSaleMap===="+facilityCurntSaleMap);
Debug.log("categorysList===="+categorysList);
Timestamp monthStart=UtilDateTime.getMonthStart(fromDate,TimeZone.getDefault(),Locale.getDefault());
Timestamp pMonthStart=UtilDateTime.getMonthStart(UtilDateTime.addDaysToTimestamp(monthStart, -1),TimeZone.getDefault(),Locale.getDefault());
Timestamp pMonthEnd=UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(pMonthStart), timeZone, locale);
//Debug.log("pMonthStart===="+pMonthStart+"===pMonthEnd=="+pMonthEnd);
prvShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,pMonthStart,pMonthEnd,null);
context.put("cMonthStart",monthStart);
context.put("pMonthStart",pMonthStart);
prevMonthDays= UtilDateTime.getIntervalInDays(pMonthStart,pMonthEnd)+1;
Debug.log("prevMonthDays===="+prevMonthDays);
prevCategoryTotalMap = [:];
prevCategorysList = [];
facilityPrevSaleMap=[:];
facCount=1;

if(UtilValidate.isNotEmpty(prvShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:prvShipmentIds, fromDate:pMonthStart, thruDate:pMonthEnd,includeReturnOrders:true,isByParty:true]);
	if(UtilValidate.isNotEmpty(dayTotals)){
		boothTotalsMap=dayTotals.get("boothTotals");
		if(UtilValidate.isNotEmpty(boothTotalsMap)){
			boothTotalsMap.each { booth->
				facilityId=booth.getKey();
				categoryType=booth.getValue().get("categoryTypeEnum");
				boothInnerMap=[:];
				milkAverageTotal = 0;
				milkSaleTotal = 0;
					prodTotals = booth.getValue().get("productTotals");
					if(UtilValidate.isNotEmpty(prodTotals)){
						prodTotals.each{ productValue ->
							if(UtilValidate.isNotEmpty(productValue)){
								currentProduct = productValue.getKey();
								if(prodIdsList.contains(currentProduct)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}
								/*product = delegator.findOne("Product", [productId : currentProduct], false);
								if("Milk".equals(product.primaryProductCategoryId)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}*/
							}
						}
						
						}
					boothInnerMap["facilityId"]=facilityId;
					boothInnerMap["categoryType"]=categoryType;
					boothInnerMap["milkSaleTotal"]=milkSaleTotal;
					boothInnerMap["milkAvgTotal"]=(milkSaleTotal/prevMonthDays);
					facilityPrevSaleMap[facilityId]=boothInnerMap;
					
					if(prevCategoryTotalMap.containsKey(categoryType)){
						tempCatList = prevCategoryTotalMap.get(categoryType);
						tempCatList.addAll(boothInnerMap);
						prevCategoryTotalMap.putAt(categoryType, tempCatList);
					}else{
						tempList = [];
						tempList.add(boothInnerMap);
						prevCategoryTotalMap.putAt(categoryType, tempList);
						prevCategorysList.add(categoryType);
					}
					}
		}
  }
}
//Debug.log("prevCategoryTotalMap===="+prevCategoryTotalMap);
//Debug.log("categoryTotalMap===="+categoryTotalMap);
context.categorysList=categorysList;
context.categoryTotalMap=categoryTotalMap;

context.facilityCurntSaleMap=facilityCurntSaleMap;
context.facilityPrevSaleMap=facilityPrevSaleMap;
Debug.log("prevCategorysList===="+prevCategorysList);



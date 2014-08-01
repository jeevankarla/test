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
facilityList = (List)((Map)ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator,null,fromDate)).get("boothActiveList");
shipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,fromDate,thruDate,null);
curntMonthDays= UtilDateTime.getIntervalInDays(fromDate,thruDate)+1;
Debug.log("curntMonthDays===="+curntMonthDays);
facilityCurntSaleMap=[:];
facCount=1;
if(UtilValidate.isNotEmpty(shipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds, fromDate:fromDate, thruDate:thruDate,includeReturnOrders:true]);
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
								product = delegator.findOne("Product", [productId : currentProduct], false);
								if("Milk".equals(product.primaryProductCategoryId)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}
							}
						}
						
						}
					boothInnerMap["milkSaleTotal"]=milkSaleTotal;
					boothInnerMap["milkAvgTotal"]=(milkSaleTotal/curntMonthDays);
					if("SCT_RTLR"==categoryType){
					facilityCurntSaleMap[facilityId]=boothInnerMap;
					}
					}
		}
	}
  }


Debug.log("facilityCurntSaleMap===="+facilityCurntSaleMap);
Timestamp monthStart=UtilDateTime.getMonthStart(fromDate,TimeZone.getDefault(),Locale.getDefault());
Timestamp pMonthStart=UtilDateTime.getMonthStart(UtilDateTime.addDaysToTimestamp(monthStart, -1),TimeZone.getDefault(),Locale.getDefault());
Timestamp pMonthEnd=UtilDateTime.getMonthEnd(UtilDateTime.toTimestamp(pMonthStart), timeZone, locale);
Debug.log("pMonthStart===="+pMonthStart+"===pMonthEnd=="+pMonthEnd);
prvShipmentIds = ByProductNetworkServices.getShipmentIdsSupplyType(delegator,pMonthStart,pMonthEnd,null);
context.put("cMonthStart",monthStart);
context.put("pMonthStart",pMonthStart);
prevMonthDays= UtilDateTime.getIntervalInDays(pMonthStart,pMonthEnd)+1;
Debug.log("prevMonthDays===="+prevMonthDays);
facilityPrevSaleMap=[:];
facCount=1;

if(UtilValidate.isNotEmpty(prvShipmentIds)){
	dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:prvShipmentIds, fromDate:pMonthStart, thruDate:pMonthEnd,includeReturnOrders:true]);
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
								product = delegator.findOne("Product", [productId : currentProduct], false);
								if("Milk".equals(product.primaryProductCategoryId)){
									milkSaleTotal=milkSaleTotal+productValue.getValue().get("total");
								}
							}
						}
						
						}
					boothInnerMap["milkSaleTotal"]=milkSaleTotal;
					boothInnerMap["milkAvgTotal"]=(milkSaleTotal/prevMonthDays);
					if("SCT_RTLR"==categoryType){
						facilityPrevSaleMap[facilityId]=boothInnerMap;
					 }
					}
		}
  }
}
Debug.log("facilityPrevSaleMap===="+facilityPrevSaleMap);
context.facilityCurntSaleMap=facilityCurntSaleMap;
context.facilityPrevSaleMap=facilityPrevSaleMap;

context.pMonthStart=pMonthStart;


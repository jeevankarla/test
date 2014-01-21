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

month = UtilDateTime.toDateString(fromDateTime, "MMMMM-yyyy");
context.putAt("month", month);

monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
dctx = dispatcher.getDispatchContext();
priceMap = [:];
priceMap = ByProductReportServices.getByProductPricesForPartyClassification(dctx, UtilMisc.toMap("userLogin", userLogin, "partyClassificationId", "PM_RC_S")).get("productsPrice");
products = delegator.findList("Product", null, null, null, null, false);
productNames = [:];
if(products){
	products.each{ eachProd ->
		productNames.putAt(eachProd.productId, eachProd.productName);
	}
	context.productNames = productNames;
}
TreeMap productMap = new TreeMap();
boothList = [];
dayWiseSale = ByProductNetworkServices.getByProductSales(dctx, monthBegin, monthEnd, "BYPROD_SALES_CHANNEL", "BYPROD_SO", null, null).get("datewiseSales");

specialOrderSales = [:];
if(dayWiseSale){
	dayWiseSale.each{eachDaySale ->
		Iterator eachDaySaleIter = eachDaySale.entrySet().iterator();
		while (eachDaySaleIter.hasNext()) {
			Map.Entry daySaleEntry = eachDaySaleIter.next();
			saleDate = daySaleEntry.getKey();
			daywiseFacilitySale = daySaleEntry.getValue();
			if(daywiseFacilitySale){
				Iterator daywiseFacilitySaleIter = daywiseFacilitySale.entrySet().iterator();
				while (daywiseFacilitySaleIter.hasNext()) {
					Map.Entry daywiseFacilitySaleEntry = daywiseFacilitySaleIter.next();
					boothId = daywiseFacilitySaleEntry.getKey();
					productQuant = daywiseFacilitySaleEntry.getValue(); 
					Iterator productQuantIter = productQuant.entrySet().iterator();
					while (productQuantIter.hasNext()) {
						productMap = [:];
						Map.Entry productQuantEntry = productQuantIter.next();
						productId = productQuantEntry.getKey();
						quantity = productQuantEntry.getValue();
						productMap.putAt("productId", productId);
						productMap.putAt("saleDate", saleDate);
						productMap.putAt("quantity", quantity);
						productMap.putAt("unitPrice", priceMap.get(productId).get("totalAmount"));
						if(specialOrderSales.containsKey(boothId)){
							temp = [];
							checkFlag =0;
							temp = specialOrderSales.get(boothId);
							if(temp){
								temp.each{eachEntry ->
									checkProd = eachEntry.productId;
									if(eachEntry.saleDate == saleDate && productId == checkProd){
										checkFlag = 1;
									}
								}
							}
							if(checkFlag == 0){
								temp.add(productMap);
								specialOrderSales.putAt(boothId, temp);
							}
						}
						else{
							tempList = [];
							tempList.add(productMap);
							specialOrderSales.putAt(boothId, tempList);
						}
					}
				}
			}
		}
	}
}
context.specialOrderSales = specialOrderSales;

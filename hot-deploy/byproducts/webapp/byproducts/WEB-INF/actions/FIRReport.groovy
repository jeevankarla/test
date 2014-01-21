	import in.vasista.vbiz.byproducts.ByProductServices;
	
	import java.util.List;
	import java.util.Map;
	
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.base.util.UtilValidate;

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
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import in.vasista.vbiz.byproducts.ByProductReportServices;
	import in.vasista.vbiz.byproducts.ByProductServices;
	
	rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
	
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	
	effectiveDate = null;
	effectiveDateStr = parameters.saleDate;
	
	if (UtilValidate.isEmpty(effectiveDateStr)) {
		effectiveDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			effectiveDate = UtilDateTime.toTimestamp(dateFormat.parse(effectiveDateStr));
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
		}
	}
	context.put("effectiveDate", effectiveDate);
	
	dayBegin = UtilDateTime.getDayStart(effectiveDate, timeZone, locale);
	dayEnd = UtilDateTime.getDayEnd(effectiveDate, timeZone, locale);
	
	monthStart = UtilDateTime.getMonthStart(dayBegin, timeZone, locale);
	monthEnd = UtilDateTime.getMonthEnd(dayEnd, timeZone, locale);
	
	int noOfDays = (UtilDateTime.getIntervalInDays(monthStart, monthEnd))+1;
	
	String productStoreId = (String) ByProductServices.getByprodFactoryStore(delegator).get("factoryStoreId");
	parlourList = ByProductServices.getByproductParlours(delegator).get("parlourIdsList");
	
	Map categoryProductsMap = (Map) ByProductServices.getProdStoreProducts(dispatcher.getDispatchContext(), UtilMisc.toMap("productStoreId", productStoreId)).get("categoryProduct");
	
	//List categoryIdsList = FastList.newInstance();
	//categoryIdsList = (List) ByProductReportServices.getByProdReportCategories(delegator, UtilMisc.toMap("productCategoryTypeId", "FIR_REPORT_CATEGORY")).get("reportProductCategories");
	productCategories = delegator.findList("ProductCategory", null, null, UtilMisc.toList("sequenceNum", "productCategoryId"), null, false);
	categoryIdsList = EntityUtil.getFieldListFromEntityList(productCategories, "productCategoryId", false);
	salesMap = [:];
	productSalesList = [];
	
	BigDecimal grandTotalSaleValue = BigDecimal.ZERO;
	BigDecimal cumulativeSaleValueTotal = BigDecimal.ZERO;
	BigDecimal grandTotalParlourSales = BigDecimal.ZERO;
	
	
	for(int i = 0; i < categoryIdsList.size(); i++){
		String category = (String) categoryIdsList.get(i);
		String splitCategory = null;
		
		if (category.contains("_FIR")) {
			String[] categorySplit = category.split("_FIR");
			splitCategory = categorySplit[0];
		}
		else{
			splitCategory = category;
		}
		GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", category), false);
		
		uomId = null;
		if(UtilValidate.isNotEmpty(productCategory.get("prodCatUomId"))){
			uomId = productCategory.get("prodCatUomId");
		}
		uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
		uomId = uom.get("abbreviation");
		List categoryProductsList = (List) categoryProductsMap.get(category);
		
		resultMap = ByProductReportServices.getDayDespatchDetails(dctx, UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd, "productList", categoryProductsList));
		salesMap["sNo"] = i+1;
		salesMap["FIRCategory"] = splitCategory;
		salesMap["unit"] = uomId;
		salesMap["totalSaleValue"] = resultMap.get("totalValue");
		grandTotalSaleValue = grandTotalSaleValue.add(resultMap.get("totalValue"));
		
		parlourResultMap = ByProductReportServices.getDayDespatchDetails(dctx, UtilMisc.toMap("fromDate", dayBegin, "thruDate", dayEnd, "productList", categoryProductsList, "facilityList", parlourList));
		
		salesMap["totalParlourSaleValue"] = parlourResultMap.get("totalValue");
		grandTotalParlourSales = grandTotalParlourSales.add(parlourResultMap.get("totalValue"));
		
		cumulativeResultMap = ByProductReportServices.getDayDespatchDetails(dctx, UtilMisc.toMap("fromDate", monthStart, "thruDate", dayEnd, "productList", categoryProductsList));
		salesMap["OthersSaleValue"] = resultMap.get("totalValue").subtract(parlourResultMap.get("totalValue"));
		salesMap["cumulativeSaleValue"] = cumulativeResultMap.get("totalValue");
		salesMap["avgCumulativeSaleValue"] = ((cumulativeResultMap.get("totalValue"))/noOfDays).setScale(2, rounding);;
		
		cumulativeSaleValueTotal = cumulativeSaleValueTotal.add(cumulativeResultMap.get("totalValue"));
		
		if(uomId == "Nos"){
			salesMap["totalSaleQty"] = resultMap.get("totalQty");
			salesMap["totalParlourSaleQty"] = parlourResultMap.get("totalQty");
			salesMap["cumulativeSaleQty"] = cumulativeResultMap.get("totalQty");
		}
		else{
			salesMap["totalSaleQty"] = resultMap.get("totalQtyInc");
			salesMap["totalParlourSaleQty"] = parlourResultMap.get("totalQtyInc");
			salesMap["cumulativeSaleQty"] = cumulativeResultMap.get("totalQtyInc");
		}
		salesMap["OthersSaleQty"] = salesMap["totalSaleQty"].subtract(salesMap["totalParlourSaleQty"]);
		
		tempProdSalesMap = [:];
		tempProdSalesMap.putAll(salesMap);
		
		productSalesList.addAll(tempProdSalesMap);
		
		resultMap.clear();
		parlourResultMap.clear();
		cumulativeResultMap.clear();
	}
	totalSalesMap = [:];
	
	totalSalesMap["FIRCategory"] = "TOTALS";
	totalSalesMap["totalSaleValue"] = grandTotalSaleValue;
	totalSalesMap["cumulativeSaleValue"] = cumulativeSaleValueTotal;
	totalSalesMap["totalParlourSaleValue"] = grandTotalParlourSales;
	totalSalesMap["OthersSaleValue"] = grandTotalSaleValue.subtract(grandTotalParlourSales);
	
	tempTotalSalesMap = [:];
	tempTotalSalesMap.putAll(totalSalesMap);
	
	productSalesList.addAll(tempTotalSalesMap);
	
	context.productSalesList = productSalesList;
	
	
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
    import org.ofbiz.entity.GenericEntityException;
    import org.ofbiz.entity.GenericValue;
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
    import org.ofbiz.service.ServiceUtil;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	import in.vasista.vbiz.byproducts.ByProductNetworkServices;
	import org.ofbiz.product.product.ProductWorker;
	dctx = dispatcher.getDispatchContext();
	context.put("dctx",dctx);
	
	fromDate = null;
	thruDate = null;
	fromDateStr = parameters.bsFromDate;
	thruDateStr = parameters.bsThruDate;
	Map boothTotals = [:];
	if (UtilValidate.isEmpty(parameters.bsFromDate)) {
		fromDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			fromDate = new java.sql.Timestamp(dateFormat.parse(parameters.bsFromDate+" 00:00:00").getTime());
		} catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + fromDate, "");
		}
	}
	if (UtilValidate.isEmpty(parameters.bsThruDate)) {
		thruDate = UtilDateTime.nowTimestamp();
	}
	else{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		try {
			thruDate =new java.sql.Timestamp(dateFormat.parse(parameters.bsThruDate+" 00:00:00").getTime());
		}catch (ParseException e) {
			Debug.logError(e, "Cannot parse date string: " + thruDate, "");
		}
	}
	fromDate = UtilDateTime.getDayStart(fromDate, timeZone, locale);
	thruDate = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
	Set productSet = new HashSet();
	productNames = [:];
	List shipmentIds =ByProductNetworkServices.getShipmentIds(delegator,fromDate,thruDate);
	categoryProductIds = ProductWorker.getProductsByCategory(delegator ,"INDENT" ,fromDate);
	for(GenericValue product : categoryProductIds) {
		pdId= "PCD"+product.getString("productId");
	    brandName=product.getString("brandName");
		productSet.add(pdId);
		productNames.put(pdId, product.brandName);
    }
	productTestMap = [:];
	dataMap = [:];
	boothWiseSalesList=FastList.newInstance();
	salesList=[];
	if(UtilValidate.isNotEmpty(shipmentIds)){
		dayTotals = ByProductNetworkServices.getPeriodTotals(dispatcher.getDispatchContext(), [shipmentIds:shipmentIds,fromDate:fromDate, thruDate:thruDate]);
		if(UtilValidate.isNotEmpty(dayTotals)){
			boothTotals = dayTotals.get("boothTotals");
			for(Map.Entry entry : boothTotals.entrySet()){
				productTotMap= [:];
				productMap = [:];
				totalQty=0;
				productSet.each{pId->
					productTestMap[pId]=0;
				}
				
				boothId = entry.getKey();
				total = entry.getValue();
				productTotalsList=total.get("productTotals");
				productTotalsList.each{ product->
					    productId= "PCD"+product.getKey();
						packetQuantity=product.getValue().get("packetQuantity");
						if(productSet.contains(productId)){
							productTestMap[productId] += packetQuantity;
							productMap[productId]=packetQuantity;
							totalQty=totalQty+packetQuantity;
						}else{
						    productMap[productId]=0;
						}
					}
				productMap["qty"]=totalQty;
			    productMap["boothId"]=boothId;
				boothWiseSalesList.add(productMap);
				
			}
		}
	}
	context.productNames = productNames;
	context.boothWiseSalesList = boothWiseSalesList;
	context.productTestMap = productTestMap;
	
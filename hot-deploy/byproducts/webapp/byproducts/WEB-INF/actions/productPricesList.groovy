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
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.ofbiz.service.DispatchContext;
	import java.math.BigDecimal;
	import java.math.MathContext;
	import org.ofbiz.base.util.UtilNumber;
	
	productId = parameters.productId;
	
	consolidatedPriceList = [];
	productPrices = delegator.findList("ProductPriceAndType", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), null, null, null, false);
	fromDatesList = EntityUtil.getFieldListFromEntityList(productPrices, "fromDate", true);
	for(i=0; i < fromDatesList.size();i++){
		classificationPriceMap = [:];
		eachPrice = EntityUtil.filterByCondition(productPrices, EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDatesList.get(i)));
		eachPrice.each{eachPriceItem	 ->
			classificationPriceMap.put("fromDate", eachPriceItem.fromDate);
			if((eachPriceItem.productPriceTypeId).contains("PRICE")){
				classificationPriceMap.put("productPriceTypeId", eachPriceItem.get("productPriceTypeId"));
				classificationPriceMap.put("price", eachPriceItem.get("price"));
			}
			else{
				parentTypeId = eachPriceItem.productPriceTypeId;
				priceKey = parentTypeId + "_Price";
				percentageKey = parentTypeId + "_Rate";
				classificationPriceMap.put(priceKey, eachPriceItem.get("taxAmount"));
				classificationPriceMap.put(percentageKey, eachPriceItem.get("taxPercentage"));
			}
			classificationPriceMap.put("thruDate", eachPriceItem.get("thruDate"));
		}
		consolidatedPriceList.addAll(classificationPriceMap);
	}
	
	context.put("productPrices", consolidatedPriceList);
	


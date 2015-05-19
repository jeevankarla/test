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
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDateProduct;
thruDate=parameters.thruDateProduct;
categoryType=parameters.categoryType;
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
totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
List conditionList = [];
if(UtilValidate.isNotEmpty(categoryType)){
	conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS , categoryType));
}
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
				                                         EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productCategoryMember = delegator.findList("ProductCategoryAndMember", condition, null, null, null, false);
ProductCatMap = [:];
if(UtilValidate.isNotEmpty(productCategoryMember)){
	productCatIds = EntityUtil.getFieldListFromEntityList(productCategoryMember, "productCategoryId", true);
	productCatIds.each{eachProductCat->
		
		productDetails = EntityUtil.filterByCondition(productCategoryMember, EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, eachProductCat));
		if(UtilValidate.isNotEmpty(productDetails)){
			productIds = EntityUtil.getFieldListFromEntityList(productDetails, "productId", true);
			ProductMap = [:];
			productIds.each{eachProductId->
				
					ProductPriceMap = [:];
					ProductPriceList = [];
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , eachProductId));
					condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					productPrice = delegator.findList("ProductPrice", condition, null, null, null, false);
					if(UtilValidate.isNotEmpty(productPrice)){
						productPriceTypeIdIds = EntityUtil.getFieldListFromEntityList(productPrice, "productPriceTypeId", true);
						productPriceTypeIdIds.each{eachproductPriceTypeIdId->
							
							productPriceDetails = EntityUtil.filterByCondition(productPrice, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, eachproductPriceTypeIdId));
							price = productPriceDetails.get(0).get("price");
							if(UtilValidate.isNotEmpty(price) && price != null){
								ProductPriceMap.put(eachproductPriceTypeIdId, price);
							}	
						}
						ProductPriceList.add(ProductPriceMap);
					}
					if(UtilValidate.isNotEmpty(ProductPriceList)){
					    ProductMap.put(eachProductId, ProductPriceList);
					}	
		     }
	     }
		 if(UtilValidate.isNotEmpty(ProductMap)){
			 ProductCatMap.put(eachProductCat, ProductMap);
		 }	 
    }
}
context.ProductCatMap = ProductCatMap;

	
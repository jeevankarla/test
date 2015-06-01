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
fromDate=parameters.prodPriceFromDate;
thruDate=parameters.prodPriceThruDate;
categoryType=parameters.categoryType;
productId = parameters.productId;
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

/*totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);
isByParty = Boolean.TRUE;
if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}*/
List conditionList = [];
//productPriceTypeIdList= ["DEFAULT_PRICE","MRP_PRICE","UTP_PRICE","MRP_IS","MRP_OS"];

if(UtilValidate.isNotEmpty(categoryType)){
	conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS , categoryType));
}
if(UtilValidate.isNotEmpty(productId)){
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , productId));
}
conditionList.add(EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,"FINISHED_GOOD"));
conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.NOT_IN,UtilMisc.toList("BOX","CAN","CRATE")));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null), EntityOperator.OR,
	                  EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd)));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productCatDetails = delegator.findList("Product", condition, null, null, null, false);
ProductCatMap = [:];
if(UtilValidate.isNotEmpty(productCatDetails)){
	productCatIds = EntityUtil.getFieldListFromEntityList(productCatDetails, "primaryProductCategoryId", true);
	productCatIds.each{eachProductCat ->
		productDetails = EntityUtil.filterByCondition(productCatDetails, EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, eachProductCat));
		if(UtilValidate.isNotEmpty(productDetails)){
			productIds = EntityUtil.getFieldListFromEntityList(productDetails, "productId", true);
			ProductMap = [:];
			productIds.each{eachProductId->
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS , eachProductId));
				conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
				conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd), EntityOperator.OR,
										 EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
				cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				productPrice = delegator.findList("ProductPrice", cond, null, null, null, false);
				if(UtilValidate.isNotEmpty(productPrice)){
					finalMap=[:];
					productPrice.each{eachProdPrice->
						fromDate=eachProdPrice.fromDate;
						if((fromDate >= dayBegin) && (fromDate <= dayEnd)){
							prodDetails = EntityUtil.filterByCondition(productPrice, EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
							productPriceTypeIds = EntityUtil.getFieldListFromEntityList(prodDetails, "productPriceTypeId", true);
							tempMap=[:];
							productPriceTypeIds.each{eachproductPriceTypeId->
	                            if(eachproductPriceTypeId == "DEFAULT_PRICE"){  						
									productPriceDetails = EntityUtil.filterByCondition(prodDetails, EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, eachproductPriceTypeId));
									price = productPriceDetails.get(0).get("price");
									thruDate = eachProdPrice.thruDate;
									if(UtilValidate.isNotEmpty(price)){
										tempMap.put(eachproductPriceTypeId, price);
									}
									if(UtilValidate.isNotEmpty(thruDate)){
										tempMap.put("thruDate",thruDate);
									}
								}
							}
							if(UtilValidate.isNotEmpty(tempMap)){
							   finalMap.put(fromDate, tempMap);
							} 
					   }	 
					}
				   if(UtilValidate.isNotEmpty(finalMap)){
					   ProductMap.put(eachProductId, finalMap);
				   }
			   } 	   
		 }
		 if(UtilValidate.isNotEmpty(ProductMap)){
			 ProductCatMap.put(eachProductCat, ProductMap);
		 }	
	  }	 
	}
}
context.ProductCatMap = ProductCatMap;
//get productPrices for Revision
CatMap=[:];
for(Map.Entry entryCat : ProductCatMap.entrySet()){
	cat=entryCat.getKey();
	catDetails = entryCat.getValue();
	productsMap=[:];
	finalList=[];
	for(eachentry in catDetails){
		productId = eachentry.getKey();
		prodDetails = eachentry.getValue();
		if(prodDetails.size()>1){
			int s=0;
			tempList=[];
			for(eachprod in prodDetails){
				if(s>0){
					tempMap=[:];
					tempList.add(eachprod);
				}
				s=s+1;
			}
			tempMap.put(productId,tempList);
			productsMap.putAll(tempMap);
		}	
	}
	if(UtilValidate.isNotEmpty(productsMap)){
	   CatMap.put(cat, productsMap);
	}  
}
context.CatMap = CatMap;



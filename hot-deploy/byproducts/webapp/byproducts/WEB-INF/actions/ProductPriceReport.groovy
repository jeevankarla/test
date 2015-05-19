import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.lang.*;
import java.math.BigDecimal;

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

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import in.vasista.vbiz.byproducts.ByProductServices;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductReportServices;

userLogin= context.userLogin;
if(UtilValidate.isNotEmpty(parameters.fromDate)){
fromDate=parameters.fromDate;
}else{
 fromDate=UtilDateTime.toDateString(UtilDateTime.nowTimestamp(),"MMMM dd, yyyy");
}
primaryProductCategoryId=parameters.primaryProductCategoryId;
productId=parameters.productId;
dctx = dispatcher.getDispatchContext();
fromDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
context.fromDateTime=UtilDateTime.toDateString(fromDateTime,"dd MMMM, yyyy");
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(fromDateTime);
List conditionList = FastList.newInstance();
List productList = FastList.newInstance();
List productIds = FastList.newInstance();
List productPriceList = FastList.newInstance();
if(UtilValidate.isNotEmpty(primaryProductCategoryId)){
	conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.EQUALS,primaryProductCategoryId));
}
if(UtilValidate.isNotEmpty(productId)){
	conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
}
if(UtilValidate.isEmpty(primaryProductCategoryId) && UtilValidate.isEmpty(productId)){
	conditionList.add(EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,"FINISHED_GOOD"));
	conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.NOT_IN,UtilMisc.toList("BOX","CAN","CRATE")));
}
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productList = delegator.findList("Product",condition,null,null,null,false);
productIds=EntityUtil.getFieldListFromEntityList(productList, "productId", true);

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIds));
conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
												EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
EntityCondition pPriceCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);											
productPriceList=delegator.findList("ProductPrice",pPriceCondition,UtilMisc.toSet("taxInPrice","productId","productPriceTypeId","productPricePurposeId","price","taxPercentage"),null,null,false);

categoryWiseMap=FastMap.newInstance();
if(UtilValidate.isNotEmpty(productList)){
	productList.each{product->
		tempMap=FastMap.newInstance();
		productPrices=EntityUtil.filterByCondition(productPriceList, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,product.productId));
		productPrices.each{productPrice->
			price=0;
			if(UtilValidate.isNotEmpty(productPrice.price)){
				price=productPrice.price;
			}
			productPriceTypeId="NON";
			if(UtilValidate.isNotEmpty(productPrice.productPriceTypeId)){
			productPriceTypeId=productPrice.productPriceTypeId;
			}
			if(productPrice.taxInPrice =="Y" && UtilValidate.isNotEmpty(productPrice.taxInPrice)){
				tempMap["In_"+productPriceTypeId]="Y";
			}else{
				tempMap["In_"+productPriceTypeId]="N";
			}
			tempMap[productPriceTypeId]=price;
			tempMap.internalName=product.internalName;
			tempMap.productId=product.productId;
			tempMap.description=product.description;
		}
		if(UtilValidate.isNotEmpty(categoryWiseMap[product.primaryProductCategoryId])){
			tempList=FastList.newInstance();
			tempList=categoryWiseMap.get(product.primaryProductCategoryId);
			if(UtilValidate.isNotEmpty(productPrices)){
				tempList.add(tempMap);
			}
			categoryWiseMap[product.primaryProductCategoryId]=tempList;
		}else{
			tempList=FastList.newInstance();
			if(UtilValidate.isNotEmpty(productPrices)){
				tempList.add(tempMap);
			}
			categoryWiseMap[product.primaryProductCategoryId]=tempList;
		}
	}
}
context.categoryWiseMap=categoryWiseMap;







